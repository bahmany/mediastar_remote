// rtsp_viewer.h — RTSP Live Video Viewer for MediaStar STB
// SAT>IP RTSP client — manual SETUP/PLAY handshake + FFmpeg RTP/MPEG-TS decode
#pragma once

#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif
#include <winsock2.h>
#include <ws2tcpip.h>
#include <windows.h>
#include <d3d11.h>

#include <algorithm>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <cstdio>
#include <cstring>
#include <deque>
#include <fstream>
#include <functional>
#include <iomanip>
#include <mutex>
#include <sstream>
#include <string>
#include <thread>
#include <vector>

#include "stb/crash_log.h"

#include <mmsystem.h>

extern "C" {
#include <libavformat/avformat.h>
#include <libavformat/avio.h>
#include <libavcodec/avcodec.h>
#include <libswresample/swresample.h>
#include <libswscale/swscale.h>
#include <libavutil/error.h>
#include <libavutil/imgutils.h>
}

namespace stb {

struct RtspViewerConfig {
    std::string url;                    // rtsp://ip:554/stream or full URL with params
    int         connect_timeout_ms = 5000;
    int         read_timeout_ms    = 3000;
    int         min_buffer_ms      = 500;
    bool        tcp_transport      = true;  // RTSP over TCP (more reliable)
    int         target_width       = 0;     // 0 = auto (use stream native)
    int         target_height      = 0;
    
    // Channel-specific parameters for MediaStar STB
    int         program_id = 0;          // Channel program ID
    int         video_pid = 0;           // Video PID
    int         audio_pid = 0;           // Audio PID
    int         ttx_pid = 0;
    int         subt_pid = 0;
    int         pmt_pid = 0;             // PMT PID
    int         freq = 0;                // Frequency in kHz
    int         sym_rate = 0;            // Symbol rate
    int         sat_index = 0;           // Satellite index
    int         tp_index = 0;            // Transponder index
    int         fec = 0;                 // FEC rate
    char        pol = 'h';               // Polarization (h/v/l/r)
    int         msys = 0;                // Modulation system (0=DVBS, 1=DVBS2)
    int         mtype = 0;               // Modulation type (0=QPSK, 1=8PSK)
    float       roll_off = 0.35f;        // Roll-off factor
    int         pilot = 0;               // Pilot tones (0=off, 1=on)
    bool        scrambled = false;      // Is channel scrambled
};

struct RtspStats {
    std::atomic<uint64_t> frames_decoded{0};
    std::atomic<uint64_t> frames_dropped{0};
    std::atomic<uint64_t> bytes_received{0};
    std::atomic<uint64_t> rtp_packets{0};
    std::atomic<uint64_t> rtp_late_drops{0};
    std::atomic<uint64_t> rtp_gap_skips{0};
    std::atomic<uint64_t> ts_packets{0};
    std::atomic<uint64_t> ts_pending_bytes{0};
    std::atomic<int>      width{0};
    std::atomic<int>      height{0};
    std::atomic<float>    fps{0.0f};
    std::atomic<int>      bitrate_kbps{0};
    std::atomic<bool>     connected{false};
    std::atomic<bool>     has_video{false};
    std::string           codec_name;
    std::string           last_error;
    std::mutex            errMu;
    
    void setError(const std::string& e) {
        std::lock_guard<std::mutex> g(errMu);
        last_error = e;
    }
    std::string getError() {
        std::lock_guard<std::mutex> g(errMu);
        return last_error;
    }
};

// ── RTP-to-MPEG-TS ring buffer with dedicated UDP receive thread ──
// Receives RTP/AVP packets on a UDP socket, strips the 12-byte RTP header,
// and writes raw MPEG-TS payload into a lock-free-ish ring buffer.
// FFmpeg reads from the ring buffer via custom AVIO read callback.
class RtpTsBuffer {
public:
    static constexpr size_t RING_SIZE = 32 * 1024 * 1024; // 32 MB ring
    static constexpr int    RTP_HDR   = 12;                // minimal RTP header
    static constexpr int    REORDER_WIN = 256;             // RTP packet reorder window
    static constexpr int    RX_MAX_PACKET = 65536;
    static constexpr int    SLOT_MAX_PACKET = 8192;

    struct Diag {
        uint64_t rtp_packets = 0;
        uint64_t rtp_late_drops = 0;
        uint64_t rtp_gap_skips = 0;
        uint64_t rtp_truncations = 0;
        uint64_t rtp_slot_truncations = 0;
        uint64_t rtp_ext_packets = 0;
        uint64_t rtp_ext_fallback = 0;
        uint64_t rtp_ext_bad = 0;
        uint64_t rtp_resyncs = 0;
        uint64_t ts_packets = 0;
        uint64_t ts_pending_bytes = 0;
        uint64_t avio_refill_events = 0;
        uint64_t avio_refill_wait_ms_total = 0;
    };

    struct Metrics {
        size_t   avail_bytes = 0;
        double   bps = 0.0;
        double   stable_bps = 0.0;
        int      buffered_ms = -1;
        int      last_rtp_ago_ms = -1;
        int      low_ms = 0;
        int      target_ms = 0;
        bool     throttle_active = false;
        Diag     diag;
    };

    RtpTsBuffer() : buf_(RING_SIZE), slots_(REORDER_WIN) {}

    Diag getDiag() const {
        Diag d;
        d.rtp_packets = rtpPackets_.load();
        d.rtp_late_drops = rtpLateDrops_.load();
        d.rtp_gap_skips = rtpGapSkips_.load();
        d.rtp_truncations = rtpTruncations_.load();
        d.rtp_slot_truncations = rtpSlotTruncations_.load();
        d.rtp_ext_packets = rtpExtPackets_.load();
        d.rtp_ext_fallback = rtpExtFallback_.load();
        d.rtp_ext_bad = rtpExtBad_.load();
        d.rtp_resyncs = rtpResyncs_.load();
        d.ts_packets = tsPackets_.load();
        d.ts_pending_bytes = tsPendingBytes_.load();
        d.avio_refill_events = avioRefillEvents_.load();
        d.avio_refill_wait_ms_total = avioRefillWaitMsTotal_.load();
        return d;
    }

    Metrics getMetrics() {
        Metrics m;
        {
            std::lock_guard<std::mutex> g(mu_);
            m.avail_bytes = available_();
            m.low_ms = lowBufferMs_;
            m.target_ms = targetBufferMs_;
            m.throttle_active = throttleActive_;
            uint64_t bps = rxBps_.load();
            if (bps > 0) m.bps = (double)bps;
            uint64_t sbps = stableBps_.load();
            if (sbps > 0) m.stable_bps = (double)sbps;
        }
        int64_t last = lastRtpWallMs_.load();
        int64_t nowMs = (int64_t)std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::steady_clock::now().time_since_epoch()).count();
        if (last > 0 && nowMs >= last) m.last_rtp_ago_ms = (int)(nowMs - last);

        double calcBps = (m.stable_bps > 20000.0) ? m.stable_bps : m.bps;
        if (calcBps > 1024.0) {
            m.buffered_ms = (int)((double)m.avail_bytes * 1000.0 / calcBps);
        }
        m.diag = getDiag();
        return m;
    }

    bool start(int port, std::atomic<bool>& stopFlag) {
        stop_ = &stopFlag;
        sock_ = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
        if (sock_ == INVALID_SOCKET) return false;

        // Maximum OS receive buffer
        int rcvBuf = 16 * 1024 * 1024;
        setsockopt(sock_, SOL_SOCKET, SO_RCVBUF, (const char*)&rcvBuf, sizeof(rcvBuf));

        // Non-blocking with short timeout for clean shutdown
        DWORD tv = 200; // 200 ms
        setsockopt(sock_, SOL_SOCKET, SO_RCVTIMEO, (const char*)&tv, sizeof(tv));

        struct sockaddr_in addr{};
        addr.sin_family = AF_INET;
        addr.sin_addr.s_addr = INADDR_ANY;
        addr.sin_port = htons((u_short)port);
        if (::bind(sock_, (struct sockaddr*)&addr, sizeof(addr)) != 0) {
            closesocket(sock_); sock_ = INVALID_SOCKET; return false;
        }

        wHead_ = 0; rHead_ = 0;
        tsPending_.clear();
        tsPendingOff_ = 0;
        tsPendingBytes_.store(0);
        needTsSync_ = false;
        haveFirstRx_ = false;
        rateBytes_ = 0;
        rateStartMs_ = 0;
        rxBps_.store(0);
        stableBps_.store(0);
        haveSeq_ = false;
        expectSeq_ = 0;
        maxSeq_ = 0;
        missCount_ = 0;
        gapActive_ = false;
        gapSeq_ = 0;
        skipsWithoutProduce_ = 0;
        rtpPackets_.store(0);
        rtpLateDrops_.store(0);
        rtpGapSkips_.store(0);
        rtpTruncations_.store(0);
        rtpSlotTruncations_.store(0);
        rtpExtPackets_.store(0);
        rtpExtFallback_.store(0);
        rtpExtBad_.store(0);
        rtpResyncs_.store(0);
        tsPackets_.store(0);
        avioRefillEvents_.store(0);
        avioRefillWaitMsTotal_.store(0);
        lastRtpWallMs_.store(0);
        for (auto& slot : slots_) slot.valid = false;
        started_ = true;
        rxThread_ = std::thread([this]{ rxLoop_(); });
        return true;
    }

    void stop() {
        started_ = false;
        if (sock_ != INVALID_SOCKET) { closesocket(sock_); sock_ = INVALID_SOCKET; }
        // Join with timeout to prevent hang
        if (rxThread_.joinable()) {
            auto start = std::chrono::steady_clock::now();
            while (rxThread_.joinable()) {
                auto elapsed = std::chrono::duration_cast<std::chrono::milliseconds>(
                    std::chrono::steady_clock::now() - start).count();
                if (elapsed > 1000) {  // 1 second timeout
                    rxThread_.detach();
                    break;
                }
                std::this_thread::sleep_for(std::chrono::milliseconds(10));
            }
        }
        // Wake any blocked reader
        {
            std::lock_guard<std::mutex> g(mu_);
            cv_.notify_all();
        }
    }

    void setWatermarksMs(int targetMs, int lowMs) {
        std::lock_guard<std::mutex> g(mu_);
        targetBufferMs_ = targetMs;
        lowBufferMs_ = lowMs;
    }

    void enableThrottle(bool on) {
        std::lock_guard<std::mutex> g(mu_);
        throttleActive_ = on;
    }

    bool waitForPrebuffer(std::chrono::milliseconds prebuffer, std::chrono::milliseconds timeout,
        std::function<void(const std::string&)> logCb = nullptr) {
        if (prebuffer.count() <= 0) return true;
        // Simple byte threshold: assume ~4 Mbps stream, so 500ms ≈ 250KB
        size_t targetBytes = (size_t)(4000000.0 / 8.0 * ((double)prebuffer.count() / 1000.0));
        if (targetBytes < 188 * 50) targetBytes = 188 * 50; // minimum ~9KB
        auto deadline = std::chrono::steady_clock::now() + timeout;
        auto nextLog = std::chrono::steady_clock::now();
        std::unique_lock<std::mutex> lk(mu_);
        for (;;) {
            if (stop_ && stop_->load()) return false;
            if (!started_) return false;
            size_t avail = available_();
            auto now = std::chrono::steady_clock::now();
            if (logCb && now >= nextLog) {
                logCb("[SAT2IP] Prebuffering... " + std::to_string(avail) + "/" + std::to_string(targetBytes) +
                    " bytes, rtp_pkts=" + std::to_string(rtpPackets_.load()));
                nextLog = now + std::chrono::milliseconds(500);
            }
            if (avail >= targetBytes) return true;
            if (cv_.wait_until(lk, deadline) == std::cv_status::timeout) {
                // If we have *any* data at all, proceed — FFmpeg will block in avioRead until more arrives
                avail = available_();
                if (avail > 0 || rtpPackets_.load() > 0) return true;
                return false;
            }
        }
    }

    // Custom AVIO read callback — called by FFmpeg
    // Simple blocking read like fread() from a pipe (matches Android DVBtoIP→VLC approach)
    static int avioRead(void* opaque, uint8_t* buf, int bufSize) {
        auto* self = static_cast<RtpTsBuffer*>(opaque);
        if (!self || !self->started_) return AVERROR_EOF;

        auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(8);
        std::unique_lock<std::mutex> lk(self->mu_);

        while (self->available_() == 0) {
            if (self->stop_ && self->stop_->load()) return AVERROR_EOF;
            if (!self->started_) return AVERROR_EOF;
            if (self->cv_.wait_until(lk, deadline) == std::cv_status::timeout) {
                if (self->available_() > 0) break;
                return AVERROR_EOF;
            }
        }

        size_t avail = self->available_();
        size_t toRead = std::min((size_t)bufSize, avail);
        if (toRead == 0) return AVERROR_EOF;
        if (toRead >= 188) toRead -= (toRead % 188);

        size_t r = self->rHead_ % RING_SIZE;
        size_t chunk1 = RING_SIZE - r;
        if (chunk1 >= toRead) {
            memcpy(buf, &self->buf_[r], toRead);
        } else {
            memcpy(buf, &self->buf_[r], chunk1);
            memcpy(buf + chunk1, &self->buf_[0], toRead - chunk1);
        }
        self->rHead_ += toRead;
        return (int)toRead;
    }

private:
    size_t available_() const { return wHead_ - rHead_; }

    static uint16_t rtpSeq_(const char* pkt) {
        uint16_t v;
        memcpy(&v, pkt + 2, sizeof(v));
        return ntohs(v);
    }

    void pushTs_(const uint8_t* tsData, int tsLen) {
        if (tsLen <= 0) return;
        std::lock_guard<std::mutex> g(mu_);
        if (!haveFirstRx_) {
            haveFirstRx_ = true;
            firstRxWall_ = std::chrono::steady_clock::now();
            rxBytesTotal_ = 0;
            rateStartMs_ = 0;
            rateBytes_ = 0;
            rxBps_.store(0);
        }
        rxBytesTotal_ += (uint64_t)tsLen;

        int64_t nowMs = (int64_t)std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::steady_clock::now().time_since_epoch()).count();
        if (rateStartMs_ <= 0) { rateStartMs_ = nowMs; rateBytes_ = 0; }
        rateBytes_ += (uint64_t)tsLen;
        int64_t dt = nowMs - rateStartMs_;
        if (dt >= 500 && dt > 0) {
            if (dt > 1500) {
                rateStartMs_ = nowMs;
                rateBytes_ = 0;
            } else {
                uint64_t bps = (uint64_t)((rateBytes_ * 1000) / (uint64_t)dt);
                rxBps_.store(bps);
                if (bps > 20000) {
                    uint64_t prev = stableBps_.load();
                    uint64_t next = (prev == 0) ? bps : ((prev * 7 + bps) / 8);
                    stableBps_.store(next);
                }
                rateStartMs_ = nowMs;
                rateBytes_ = 0;
            }
        }
        if (available_() + (size_t)tsLen > RING_SIZE) {
            rHead_ = wHead_ + (size_t)tsLen - RING_SIZE;
        }
        size_t w = wHead_ % RING_SIZE;
        size_t chunk1 = RING_SIZE - w;
        if (chunk1 >= (size_t)tsLen) {
            memcpy(&buf_[w], tsData, tsLen);
        } else {
            memcpy(&buf_[w], tsData, chunk1);
            memcpy(&buf_[0], tsData + chunk1, tsLen - chunk1);
        }
        wHead_ += (size_t)tsLen;
        tsPackets_ += (uint64_t)((size_t)tsLen / 188u);
        cv_.notify_one();
    }

    // Strip RTP header and push raw TS payload directly to ring buffer.
    // Matches Android DVBtoIP approach: no TS alignment buffer, no reorder.
    // FFmpeg's mpegts demuxer handles TS sync internally.
    void processRtpToTs_(const char* pkt, int n) {
        if (n <= RTP_HDR) return;
        if (((uint8_t)pkt[0] >> 6) != 2) return;

        const uint8_t* tsData = (const uint8_t*)pkt + RTP_HDR;
        int tsLen = n - RTP_HDR;

        if (((uint8_t)pkt[0]) & 0x20) {
            int padLen = (uint8_t)pkt[n - 1];
            tsLen -= padLen;
            if (tsLen <= 0) return;
        }

        int csrcCount = (uint8_t)pkt[0] & 0x0F;
        int extraHdr = csrcCount * 4;
        tsData += extraHdr;
        tsLen -= extraHdr;
        if (tsLen <= 0) return;

        if (((uint8_t)pkt[0]) & 0x10) {
            if (tsLen < 4) return;
            int extN = ((tsData[2] << 8) | tsData[3]);
            int extLen = extN * 4 + 4;
            if (extLen > tsLen) {
                int altLen = extN + 4;
                if (altLen <= tsLen) extLen = altLen;
                else return;
            }
            tsData += extLen;
            tsLen -= extLen;
            if (tsLen <= 0) return;
        }

        if (tsLen > 0) {
            pushTs_(tsData, tsLen);
        }
    }

    void reorderPush_(const char* pkt, int n) {
        if (n <= RTP_HDR) return;
        uint16_t seq = rtpSeq_(pkt);

        if (n > SLOT_MAX_PACKET) {
            rtpSlotTruncations_++;
            return;
        }

        // If we've been skipping continuously without producing any data
        // (e.g. STB reset RTP seq after control-connection drop), force resync.
        if (haveSeq_ && skipsWithoutProduce_ >= (uint32_t)(REORDER_WIN * 2)) {
            haveSeq_ = false;
            skipsWithoutProduce_ = 0;
            gapActive_ = false;
            for (auto& s : slots_) s.valid = false;
            rtpResyncs_++;
            tsPending_.clear();
            tsPendingOff_ = 0;
            tsPendingBytes_.store(0);
            needTsSync_ = true;
            rateBytes_ = 0;
            rateStartMs_ = 0;
        }

        if (!haveSeq_) {
            haveSeq_ = true;
            expectSeq_ = seq;
            maxSeq_ = seq;
            gapActive_ = false;
            gapSeq_ = seq;
            skipsWithoutProduce_ = 0;
        } else {
            if ((uint16_t)(seq - expectSeq_) >= 0x8000) {
                rtpLateDrops_++;
                return;
            }
        }

        // Update maxSeq_ (mod-65536 arithmetic)
        if ((uint16_t)(seq - maxSeq_) < 0x8000) maxSeq_ = seq;

        int idx = (int)(seq % REORDER_WIN);
        slots_[idx].valid = true;
        slots_[idx].seq = seq;
        slots_[idx].len = (n > (int)sizeof(slots_[idx].data)) ? (int)sizeof(slots_[idx].data) : n;
        memcpy(slots_[idx].data, pkt, (size_t)slots_[idx].len);

        // If we're too far behind, skip missing packets to catch up.
        while ((uint16_t)(maxSeq_ - expectSeq_) >= (uint16_t)REORDER_WIN) {
            int di = (int)(expectSeq_ % REORDER_WIN);
            slots_[di].valid = false;
            expectSeq_++;
            rtpGapSkips_++;
            skipsWithoutProduce_++;
            gapActive_ = false;
        }

        // Flush in-order packets
        for (;;) {
            int i = (int)(expectSeq_ % REORDER_WIN);
            if (slots_[i].valid && slots_[i].seq != expectSeq_) {
                slots_[i].valid = false;
            }
            if (slots_[i].valid && slots_[i].seq == expectSeq_) {
                slots_[i].valid = false;
                processRtpToTs_((const char*)slots_[i].data, slots_[i].len);
                expectSeq_++;
                gapActive_ = false;
                skipsWithoutProduce_ = 0;
                continue;
            }

            // Avoid stalling forever on a missing packet: if we have a gap and keep receiving
            // newer packets, skip the missing seq after a short wait.
            uint16_t ahead = (uint16_t)(maxSeq_ - expectSeq_);
            if (ahead > 8) {
                auto now = std::chrono::steady_clock::now();
                if (!gapActive_ || gapSeq_ != expectSeq_) {
                    gapActive_ = true;
                    gapSeq_ = expectSeq_;
                    gapStartWall_ = now;
                }
                auto waited = std::chrono::duration_cast<std::chrono::milliseconds>(now - gapStartWall_).count();
                if (waited > 120) {
                    int di = (int)(expectSeq_ % REORDER_WIN);
                    slots_[di].valid = false;
                    expectSeq_++;
                    rtpGapSkips_++;
                    skipsWithoutProduce_++;
                    gapActive_ = false;
                    continue;
                }
            } else {
                gapActive_ = false;
            }
            break;
        }
    }

    void rxLoop_() {
        char pkt[RX_MAX_PACKET];
        uint64_t rxCount = 0;
        uint64_t rxErrors = 0;
        while (started_ && !(stop_ && stop_->load())) {
            int n = ::recv(sock_, pkt, sizeof(pkt), 0);
            if (n == SOCKET_ERROR) {
                int e = WSAGetLastError();
                if (e == WSAETIMEDOUT || e == WSAEWOULDBLOCK) continue;
                if (e == WSAEMSGSIZE) { rtpTruncations_++; continue; }
                rxErrors++;
                break;
            }
            if (n == 0) break;
            int64_t nowMs = (int64_t)std::chrono::duration_cast<std::chrono::milliseconds>(
                std::chrono::steady_clock::now().time_since_epoch()).count();
            lastRtpWallMs_.store(nowMs);
            if (n == (int)sizeof(pkt)) rtpTruncations_++;
            rtpPackets_++;
            rxCount++;
            reorderPush_(pkt, n);
        }
    }

    std::vector<uint8_t>  buf_;
    std::vector<uint8_t>  tsPending_;
    size_t                tsPendingOff_ = 0;
    std::atomic<uint64_t> tsPendingBytes_{0};
    bool                  needTsSync_ = false;
    size_t                wHead_ = 0;
    size_t                rHead_ = 0;
    std::mutex            mu_;
    std::condition_variable cv_;
    SOCKET                sock_ = INVALID_SOCKET;
    std::thread           rxThread_;
    std::atomic<bool>     started_{false};
    std::atomic<bool>*    stop_ = nullptr;
    bool                  haveFirstRx_ = false;
    std::chrono::steady_clock::time_point firstRxWall_{};
    uint64_t               rxBytesTotal_ = 0;
    uint64_t               rateBytes_ = 0;
    int64_t                rateStartMs_ = 0;
    std::atomic<uint64_t>  rxBps_{0};
    std::atomic<uint64_t>  stableBps_{0};
    int                    targetBufferMs_ = 0;
    int                    lowBufferMs_ = 0;
    bool                   throttleActive_ = false;

    std::atomic<uint64_t> rtpPackets_{0};
    std::atomic<uint64_t> rtpLateDrops_{0};
    std::atomic<uint64_t> rtpGapSkips_{0};
    std::atomic<uint64_t> rtpTruncations_{0};
    std::atomic<uint64_t> rtpSlotTruncations_{0};
    std::atomic<uint64_t> rtpExtPackets_{0};
    std::atomic<uint64_t> rtpExtFallback_{0};
    std::atomic<uint64_t> rtpExtBad_{0};
    std::atomic<uint64_t> rtpResyncs_{0};
    std::atomic<uint64_t> tsPackets_{0};
    std::atomic<uint64_t> avioRefillEvents_{0};
    std::atomic<uint64_t> avioRefillWaitMsTotal_{0};
    std::atomic<int64_t>  lastRtpWallMs_{0};

    struct RtpSlot {
        bool     valid = false;
        uint16_t seq = 0;
        int      len = 0;
        uint8_t  data[SLOT_MAX_PACKET];
    };
    std::vector<RtpSlot> slots_;
    bool     haveSeq_ = false;
    uint16_t expectSeq_ = 0;
    uint16_t maxSeq_ = 0;
    int      missCount_ = 0;
    bool     gapActive_ = false;
    uint16_t gapSeq_ = 0;
    uint32_t skipsWithoutProduce_ = 0;
    std::chrono::steady_clock::time_point gapStartWall_{};
};

class WaveOutPlayer {
public:
    ~WaveOutPlayer() { close(); }

    bool open(int sampleRate, int channels) {
        close();
        if (sampleRate <= 0 || channels <= 0) return false;

        WAVEFORMATEX wfx{};
        wfx.wFormatTag = WAVE_FORMAT_PCM;
        wfx.nChannels = (WORD)channels;
        wfx.nSamplesPerSec = (DWORD)sampleRate;
        wfx.wBitsPerSample = 16;
        wfx.nBlockAlign = (WORD)(wfx.nChannels * (wfx.wBitsPerSample / 8));
        wfx.nAvgBytesPerSec = wfx.nSamplesPerSec * wfx.nBlockAlign;
        wfx.cbSize = 0;

        if (waveOutOpen(&hwo_, WAVE_MAPPER, &wfx, 0, 0, CALLBACK_NULL) != MMSYSERR_NOERROR) {
            hwo_ = nullptr;
            return false;
        }

        for (int i = 0; i < NUM_BUFS; i++) {
            bufs_[i].resize(BUF_BYTES);
            WAVEHDR& hdr = hdrs_[i];
            memset(&hdr, 0, sizeof(hdr));
            hdr.lpData = (LPSTR)bufs_[i].data();
            hdr.dwBufferLength = (DWORD)bufs_[i].size();
            waveOutPrepareHeader(hwo_, &hdr, sizeof(hdr));
        }

        opened_ = true;
        sampleRate_ = sampleRate;
        channels_ = channels;
        return true;
    }

    void close() {
        if (hwo_) {
            waveOutReset(hwo_);
            for (int i = 0; i < NUM_BUFS; i++) {
                if (hdrs_[i].dwFlags & WHDR_PREPARED) {
                    waveOutUnprepareHeader(hwo_, &hdrs_[i], sizeof(WAVEHDR));
                }
            }
            waveOutClose(hwo_);
        }
        hwo_ = nullptr;
        opened_ = false;
        sampleRate_ = 0;
        channels_ = 0;
    }

    bool isOpen() const { return opened_ && hwo_; }
    int sampleRate() const { return sampleRate_; }
    int channels() const { return channels_; }

    void submit(const uint8_t* data, size_t bytes) {
        if (!isOpen() || !data || bytes == 0) return;

        size_t off = 0;
        while (off < bytes) {
            int idx = -1;
            // Wait up to 100ms for a free buffer — longer for HD audio stability
            for (int attempt = 0; attempt < 100; attempt++) {
                idx = findFree_();
                if (idx >= 0) break;
                Sleep(1);
            }
            if (idx < 0) {
                // Drop oldest buffer instead of giving up - prevents audio starvation
                for (int i = 0; i < NUM_BUFS; i++) {
                    if ((hdrs_[i].dwFlags & WHDR_INQUEUE) == 0) {
                        idx = i;
                        break;
                    }
                }
                if (idx < 0) return; // All buffers truly busy
            }

            WAVEHDR& hdr = hdrs_[idx];
            size_t n = bytes - off;
            if (n > BUF_BYTES) n = BUF_BYTES;
            memcpy(bufs_[idx].data(), data + off, n);
            hdr.dwBufferLength = (DWORD)n;
            hdr.dwFlags &= ~WHDR_DONE;
            waveOutWrite(hwo_, &hdr, sizeof(WAVEHDR));
            off += n;
        }
    }

private:
    int findFree_() {
        for (int i = 0; i < NUM_BUFS; i++) {
            if ((hdrs_[i].dwFlags & WHDR_INQUEUE) == 0) return i;
        }
        return -1;
    }

    static constexpr int NUM_BUFS = 16;
    static constexpr size_t BUF_BYTES = 65536;

    HWAVEOUT hwo_ = nullptr;
    bool opened_ = false;
    int sampleRate_ = 0;
    int channels_ = 0;
    WAVEHDR hdrs_[NUM_BUFS]{};
    std::vector<uint8_t> bufs_[NUM_BUFS];
};

// Stub implementation - shows UI but requires FFmpeg DLLs at runtime
class RtspViewer {
public:
    RtspViewer() = default;
    ~RtspViewer() { stop(); }
    
    RtspViewer(const RtspViewer&) = delete;
    RtspViewer& operator=(const RtspViewer&) = delete;
    
    bool init(ID3D11Device* device, ID3D11DeviceContext* ctx) {
        d3dDevice_ = device;
        d3dCtx_ = ctx;
        return true;
    }
    
    bool start(const RtspViewerConfig& cfg) {
        try {
            try { CrashLog("[RTSP] start() enter"); } catch (...) {}

            // If a previous thread finished naturally, it can still be joinable.
            // Starting again without joining would call std::terminate.
            if (workerThread_.joinable()) stop();

            config_ = cfg;
            if (config_.url.empty()) {
                stats_.setError("Empty URL");
                return false;
            }

            try { CrashLog((std::string("[RTSP] start url=") + config_.url).c_str()); } catch (...) {}

            // Check if FFmpeg DLLs are available (try exe folder first)
            try { CrashLog("[RTSP] load avcodec-61.dll"); } catch (...) {}
            HMODULE hAvcodec = loadDll_("avcodec-61.dll");
            if (!hAvcodec) {
                try { CrashLog("[RTSP] load avcodec-60.dll"); } catch (...) {}
                hAvcodec = loadDll_("avcodec-60.dll");
            }
            if (!hAvcodec) {
                try { CrashLog("[RTSP] load avcodec.dll"); } catch (...) {}
                hAvcodec = loadDll_("avcodec.dll");
            }

            if (!hAvcodec) {
                stats_.setError("FFmpeg not found. " + getLastDllError());
                if (onLog) onLog("[RTSP] " + stats_.getError());
                return false;
            }
            try { CrashLog("[RTSP] avcodec loaded, FreeLibrary"); } catch (...) {}
            FreeLibrary(hAvcodec);

            HMODULE hAvformat = loadDll_("avformat-61.dll");
            if (!hAvformat) hAvformat = loadDll_("avformat.dll");
            if (hAvformat) FreeLibrary(hAvformat);

            HMODULE hAvutil = loadDll_("avutil-59.dll");
            if (!hAvutil) hAvutil = loadDll_("avutil.dll");
            if (hAvutil) FreeLibrary(hAvutil);

            HMODULE hSwscale = loadDll_("swscale-8.dll");
            if (!hSwscale) hSwscale = loadDll_("swscale.dll");
            if (hSwscale) FreeLibrary(hSwscale);

            running_ = true;
            stopFlag_ = false;
            try {
                try { CrashLog("[RTSP] starting worker thread"); } catch (...) {}
                workerThread_ = std::thread(&RtspViewer::workerLoop, this);
            } catch (const std::exception& e) {
                running_ = false;
                stopFlag_ = true;
                stats_.setError(std::string("RTSP thread start failed: ") + e.what());
                if (onLog) onLog("[RTSP] " + stats_.getError());
                return false;
            } catch (...) {
                running_ = false;
                stopFlag_ = true;
                stats_.setError("RTSP thread start failed (unknown exception)");
                if (onLog) onLog("[RTSP] " + stats_.getError());
                return false;
            }

            try { CrashLog("[RTSP] start() ok"); } catch (...) {}
            return true;
        } catch (const std::exception& e) {
            running_ = false;
            stopFlag_ = true;
            stats_.setError(std::string("RTSP start exception: ") + e.what());
            if (onLog) onLog("[RTSP] " + stats_.getError());
            return false;
        } catch (...) {
            running_ = false;
            stopFlag_ = true;
            stats_.setError("RTSP start exception (unknown)");
            if (onLog) onLog("[RTSP] " + stats_.getError());
            return false;
        }
    }
    
    void stop() {
        try {
            // Signal stop first
            stopFlag_ = true;
            running_ = false;
            
            // Wait for worker thread with timeout to prevent hangs
            if (workerThread_.joinable()) {
                if (std::this_thread::get_id() != workerThread_.get_id()) {
                    auto joinStart = std::chrono::steady_clock::now();
                    while (workerThread_.joinable()) {
                        auto elapsed = std::chrono::duration_cast<std::chrono::milliseconds>(
                            std::chrono::steady_clock::now() - joinStart).count();
                        if (elapsed > 2000) {  // 2 second timeout
                            workerThread_.detach();
                            break;
                        }
                        std::this_thread::sleep_for(std::chrono::milliseconds(5));
                    }
                } else {
                    workerThread_.detach();
                }
            }
            
            // Short delay to ensure socket/UDP cleanup before new session
            std::this_thread::sleep_for(std::chrono::milliseconds(200));
            
            stats_.connected = false;
            stats_.has_video = false;

            ID3D11ShaderResourceView* srv = nullptr;
            ID3D11Texture2D* tex = nullptr;
            {
                std::lock_guard<std::mutex> g(texMu_);
                srv = srv_;
                tex = texture_;
                srv_ = nullptr;
                texture_ = nullptr;
            }
            if (srv) srv->Release();
            if (tex) tex->Release();

            {
                std::lock_guard<std::mutex> g(frameMu_);
                frameQ_.clear();
                qHaveClock_ = false;
            }
            // Reset audio sync state
            aHaveClock_ = false;
            aBasePts_ = 0;
            aBaseWall_ = {};
            aVOffsetUs_.store(0);
            aVOffsetSamples_ = 0;
        } catch (...) {
            try { stats_.connected = false; stats_.has_video = false; } catch (...) {}
        }
    }
    
    bool isRunning() const { return running_.load(); }
    bool isConnected() const { return stats_.connected.load(); }
    bool hasVideo() const { return stats_.has_video.load(); }

    void pump() {
        if (!running_.load()) return;

        int w = 0, h = 0, stride = 0;
        std::vector<uint8_t> snap;
        bool canPresent = false;
        {
            std::lock_guard<std::mutex> g(frameMu_);
            if (frameQ_.empty()) return;
            auto now = std::chrono::steady_clock::now();
            // Not yet time for the front frame
            if (frameQ_.front().due > now) return;
            // Drop frames that are already late (keep the most recent one that's due)
            while (frameQ_.size() > 1 && frameQ_[1].due <= now) {
                // Return dropped buffer to pool
                if (framePool_.size() < MAX_FRAME_POOL)
                    framePool_.push_back(std::move(frameQ_.front().bgra));
                frameQ_.pop_front();
                stats_.frames_dropped++;
            }
            auto& item = frameQ_.front();
            w = item.w;
            h = item.h;
            stride = item.stride;
            snap = std::move(item.bgra);
            frameQ_.pop_front();
            canPresent = true;
        }

        if (!canPresent || w <= 0 || h <= 0 || stride <= 0 || snap.empty()) return;
        ensureTexture_(w, h);

        ID3D11Texture2D* tex = nullptr;
        {
            std::lock_guard<std::mutex> g(texMu_);
            tex = texture_;
        }
        if (tex && d3dCtx_) {
            d3dCtx_->UpdateSubresource(tex, 0, nullptr, snap.data(), (UINT)stride, 0);
            stats_.has_video = true;
            stats_.width = w;
            stats_.height = h;
        }

        // Return presented buffer to pool for reuse
        {
            std::lock_guard<std::mutex> g(frameMu_);
            if (framePool_.size() < MAX_FRAME_POOL)
                framePool_.push_back(std::move(snap));
        }
    }
    
    ID3D11ShaderResourceView* getTextureSRV() {
        std::lock_guard<std::mutex> g(texMu_);
        return srv_;
    }
    
    int getWidth() const { return stats_.width.load(); }
    int getHeight() const { return stats_.height.load(); }
    
    const RtspStats& getStats() const { return stats_; }
    RtspStats& getStats() { return stats_; }
    
    std::function<void(const std::string&)> onLog;
    
    // Check if FFmpeg is available
    static bool isFFmpegAvailable() {
        HMODULE h = loadDll_("avcodec-61.dll");
        if (!h) h = loadDll_("avcodec-60.dll");
        if (!h) h = loadDll_("avcodec.dll");
        if (h) { FreeLibrary(h); return true; }
        return false;
    }

    static std::string getLastDllError() {
        std::lock_guard<std::mutex> g(dllErrMu_);
        return lastDllError_;
    }

    static std::string getExecutableDir() {
        return exeDir_();
    }
    
    // Generate MediaStar STB RTSP URL with channel parameters
    static std::string generateMediaStarUrl(const std::string& ip, const RtspViewerConfig& cfg) {
        std::string base = "rtsp://" + ip + ":554/";
        
        // If no channel parameters, return base URL
        if (cfg.program_id == 0) {
            return base;
        }
        
        // Build query string based on Java implementation
        std::string query = "?";
        query += "alisatid=" + std::to_string(cfg.sat_index);
        query += "&freq=" + std::to_string(cfg.freq);
        query += "&pol=" + std::string(1, cfg.pol ? cfg.pol : 'h');
        query += "&msys=" + std::string(cfg.msys == 0 ? "dvbs" : "dvbs2");
        query += "&mtype=" + std::string(cfg.mtype == 0 ? "qpsk" : "8psk");
        {
            std::ostringstream oss;
            oss.setf(std::ios::fixed);
            oss << std::setprecision(2) << cfg.roll_off;
            query += "&ro=" + oss.str();
        }
        query += "&plts=" + std::string(cfg.pilot == 0 ? "off" : "on");
        query += "&sr=" + std::to_string(cfg.sym_rate);
        query += "&fec=" + std::to_string(cfg.fec);
        query += "&camode=" + std::to_string(cfg.scrambled ? 1 : 0);
        query += "&vpid=" + std::to_string(cfg.video_pid);
        query += "&apid=" + std::to_string(cfg.audio_pid);
        query += "&ttxpid=" + std::to_string(cfg.ttx_pid);
        query += "&subtpid=" + std::to_string(cfg.subt_pid);
        query += "&pmt=" + std::to_string(cfg.pmt_pid);
        query += "&prognumber=" + std::to_string(cfg.program_id);
        
        // PIDs list (include DVB PSI PIDs for stable stream parsing, matching Android style)
        // 0=PAT, 16=CAT, 17=SDT, 20=TDT/TOT, 8191=NULL
        query += "&pids=0,16,17,20,";
        query += std::to_string(cfg.video_pid);
        query += "," + std::to_string(cfg.audio_pid);
        query += "," + std::to_string(cfg.ttx_pid);
        query += "," + std::to_string(cfg.subt_pid);
        query += "," + std::to_string(cfg.pmt_pid);
        query += ",8191";
        
        return base + query;
    }
    
    // Extract channel parameters from Channel struct
    static RtspViewerConfig extractChannelParams(const stb::Channel& channel) {
        RtspViewerConfig cfg;
        
        // Basic channel info
        cfg.program_id = channel.service_index;
        cfg.video_pid = channel.video_pid;
        cfg.pmt_pid = channel.pmt_pid;
        cfg.ttx_pid = channel.ttx_pid;
        cfg.scrambled = channel.is_scrambled;
        
        // Extract from ProgramId (Android: sat=0..4, tp=4..9, prog=9..14)
        cfg.sat_index = channel.satIndex();
        if (channel.service_id.size() >= 14) {
            try { cfg.tp_index = std::stoi(channel.service_id.substr(4, 5)); } catch (...) {}
            try { cfg.program_id = std::stoi(channel.service_id.substr(9, 5)); } catch (...) {}
        }
        
        // Modulation info
        cfg.msys = channel.modulation_system;
        cfg.mtype = channel.modulation_type;
        cfg.roll_off = channel.roll_off / 100.0f;  // Convert 35 -> 0.35
        cfg.pilot = channel.pilot_tones;

        cfg.freq = 0;
        cfg.sym_rate = 0;
        cfg.fec = 0;
        cfg.pol = 'h';
        
        // Extract audio PID from raw JSON if available
        if (!channel.audio_pids_raw.empty()) {
            // Parse first audio PID from JSON array
            // Format: [{"pid":101,"lang":"eng"},{"pid":102,"lang":"ara"}]
            size_t pos = channel.audio_pids_raw.find("\"pid\":");
            if (pos == std::string::npos) pos = channel.audio_pids_raw.find("\"PID\":");
            if (pos != std::string::npos) {
                pos += 6;  // Skip "pid":
                while (pos < channel.audio_pids_raw.size() && 
                       (channel.audio_pids_raw[pos] < '0' || channel.audio_pids_raw[pos] > '9')) {
                    pos++;
                }
                if (pos < channel.audio_pids_raw.size()) {
                    cfg.audio_pid = std::stoi(channel.audio_pids_raw.substr(pos));
                }
            }
        }

        if (!channel.subtitle_pids_raw.empty()) {
            size_t pos = channel.subtitle_pids_raw.find("\"pid\":");
            if (pos == std::string::npos) pos = channel.subtitle_pids_raw.find("\"PID\":");
            if (pos != std::string::npos) {
                pos += 6;
                while (pos < channel.subtitle_pids_raw.size() &&
                       (channel.subtitle_pids_raw[pos] < '0' || channel.subtitle_pids_raw[pos] > '9')) {
                    pos++;
                }
                if (pos < channel.subtitle_pids_raw.size()) {
                    cfg.subt_pid = std::stoi(channel.subtitle_pids_raw.substr(pos));
                }
            }
        }
        
        return cfg;
    }
    
private:
    inline static std::mutex dllErrMu_;
    inline static std::string lastDllError_;

    static std::string win32ErrToString_(DWORD err) {
        if (err == 0) return std::string();
        char* msg = nullptr;
        DWORD flags = FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS;
        DWORD n = FormatMessageA(flags, nullptr, err, 0, (LPSTR)&msg, 0, nullptr);
        std::string out;
        if (n && msg) {
            out.assign(msg, msg + n);
            while (!out.empty() && (out.back() == '\r' || out.back() == '\n' || out.back() == ' ')) out.pop_back();
        }
        if (msg) LocalFree(msg);
        return out;
    }

    static std::string exeDir_() {
        char path[MAX_PATH] = {};
        DWORD n = GetModuleFileNameA(nullptr, path, MAX_PATH);
        if (n == 0 || n >= MAX_PATH) return std::string();
        std::string s(path, path + n);
        size_t pos = s.find_last_of("\\/");
        if (pos == std::string::npos) return std::string();
        return s.substr(0, pos);
    }

    static HMODULE loadDll_(const char* dllName) {
        if (!dllName || !*dllName) return nullptr;

        {
            std::lock_guard<std::mutex> g(dllErrMu_);
            lastDllError_.clear();
        }

        // 1) Try next to the executable FIRST (most reliable)
        std::string dir = exeDir_();
        if (!dir.empty()) {
            std::string full = dir + "\\" + dllName;
            // Prefer LoadLibraryEx with DLL_LOAD_DIR so dependencies resolve from this directory.
            HMODULE h = LoadLibraryExA(full.c_str(), nullptr,
                LOAD_LIBRARY_SEARCH_DLL_LOAD_DIR | LOAD_LIBRARY_SEARCH_DEFAULT_DIRS);
            if (!h) {
                // Older Windows may not support the flags (ERROR_INVALID_PARAMETER).
                DWORD e = GetLastError();
                if (e == ERROR_INVALID_PARAMETER) {
                    h = LoadLibraryA(full.c_str());
                }
            }
            if (h) return h;
            DWORD err = GetLastError();
            std::ostringstream oss;
            oss << "LoadLibrary failed for '" << full << "' (err=" << err << ")";
            std::string msg = win32ErrToString_(err);
            if (!msg.empty()) oss << ": " << msg;
            {
                std::lock_guard<std::mutex> g(dllErrMu_);
                lastDllError_ = oss.str();
            }
        }

        // 2) Fallback to default search order (working directory / system paths)
        HMODULE h = LoadLibraryA(dllName);
        if (!h) {
            DWORD err = GetLastError();
            std::ostringstream oss;
            oss << "LoadLibrary failed for '" << dllName << "' (err=" << err << ")";
            std::string msg = win32ErrToString_(err);
            if (!msg.empty()) oss << ": " << msg;
            std::lock_guard<std::mutex> g(dllErrMu_);
            if (lastDllError_.empty()) lastDllError_ = oss.str();
            else lastDllError_ += " | " + oss.str();
        }
        return h;
    }

    static std::string ffErr_(int err) {
        char buf[256] = {};
        av_strerror(err, buf, sizeof(buf));
        return std::string(buf);
    }

    static void avLogCb_(void* ptr, int level, const char* fmt, va_list vl) {
        (void)ptr;
        if (level > av_log_get_level()) return;

        char line[1024];
        int n = vsnprintf(line, sizeof(line), fmt, vl);
        if (n <= 0) return;

        RtspViewer* self = tlsSelf_;
        if (!self) return;

        std::string s(line);
        while (!s.empty() && (s.back() == '\n' || s.back() == '\r')) s.pop_back();
        if (s.empty()) return;

        // Filter out noisy MPEG-2 decoder messages (normal for satellite TV mid-stream join)
        if (s.find("Invalid frame dimensions") != std::string::npos) return;
        if (s.find("MVs not available") != std::string::npos) return;
        if (s.find("motion_type") != std::string::npos) return;
        if (s.find("ac-tex damaged") != std::string::npos) return;
        if (s.find("mb incr damaged") != std::string::npos) return;
        if (s.find("slice mismatch") != std::string::npos) return;
        if (s.find("skipped MB in") != std::string::npos) return;
        if (s.find("Invalid mb type") != std::string::npos) return;
        if (s.find("invalid cbp") != std::string::npos) return;
        if (s.find("Packet corrupt") != std::string::npos) return;
        if (s.find("skip with previntra") != std::string::npos) return;
        if (s.find("concealing") != std::string::npos) return;
        if (s.find("Warning MVs") != std::string::npos) return;

        try {
            if (self->onLog) self->onLog("[FF] " + s);
        } catch (...) {}
    }

    void ensureTexture_(int w, int h) {
        if (!d3dDevice_ || !d3dCtx_ || w <= 0 || h <= 0) return;

        std::lock_guard<std::mutex> g(texMu_);
        if (texture_ && srv_ && stats_.width.load() == w && stats_.height.load() == h) return;

        if (srv_) { srv_->Release(); srv_ = nullptr; }
        if (texture_) { texture_->Release(); texture_ = nullptr; }

        D3D11_TEXTURE2D_DESC desc{};
        desc.Width = (UINT)w;
        desc.Height = (UINT)h;
        desc.MipLevels = 1;
        desc.ArraySize = 1;
        desc.Format = DXGI_FORMAT_B8G8R8A8_UNORM;
        desc.SampleDesc.Count = 1;
        desc.Usage = D3D11_USAGE_DEFAULT;
        desc.BindFlags = D3D11_BIND_SHADER_RESOURCE;

        ID3D11Texture2D* tex = nullptr;
        HRESULT hr = d3dDevice_->CreateTexture2D(&desc, nullptr, &tex);
        if (FAILED(hr) || !tex) return;

        D3D11_SHADER_RESOURCE_VIEW_DESC srvDesc{};
        srvDesc.Format = desc.Format;
        srvDesc.ViewDimension = D3D11_SRV_DIMENSION_TEXTURE2D;
        srvDesc.Texture2D.MipLevels = 1;
        ID3D11ShaderResourceView* srv = nullptr;
        hr = d3dDevice_->CreateShaderResourceView(tex, &srvDesc, &srv);
        if (FAILED(hr) || !srv) {
            tex->Release();
            return;
        }

        texture_ = tex;
        srv_ = srv;
        stats_.width = w;
        stats_.height = h;
    }

    // ── SAT>IP RTSP signaling helpers (raw TCP, matching Android Sat2IP_Rtsp.java) ──

    struct Sat2ipSession {
        SOCKET      sock = INVALID_SOCKET;
        int         cseq = 0;
        int         streamId = 0;
        std::string sessionId;
        std::string baseUrl;   // rtsp://host:554/
        int         rtpPort = 0;
    };

    // Parse host from rtsp://host:port/...
    static bool parseRtspHost_(const std::string& url, std::string& host, int& port) {
        // rtsp://host:port/...
        size_t p = url.find("://");
        if (p == std::string::npos) return false;
        p += 3;
        size_t slash = url.find('/', p);
        std::string hostPort = (slash != std::string::npos) ? url.substr(p, slash - p) : url.substr(p);
        size_t colon = hostPort.find(':');
        if (colon != std::string::npos) {
            host = hostPort.substr(0, colon);
            try { port = std::stoi(hostPort.substr(colon + 1)); } catch (...) { port = 554; }
        } else {
            host = hostPort;
            port = 554;
        }
        return !host.empty();
    }

    // Extract query string from URL (everything from '?' onwards, inclusive)
    static std::string extractQuery_(const std::string& url) {
        size_t q = url.find('?');
        return (q != std::string::npos) ? url.substr(q) : "";
    }

    // Build base URL: rtsp://host:port/
    static std::string buildBaseUrl_(const std::string& host, int port) {
        return "rtsp://" + host + ":" + std::to_string(port) + "/";
    }

    // Connect TCP to RTSP server (like PlainTCP.java)
    static SOCKET connectTcp_(const std::string& host, int port, int timeoutMs) {
        WSADATA wsa;
        WSAStartup(MAKEWORD(2, 2), &wsa);

        struct addrinfo hints{}, *res = nullptr;
        hints.ai_family = AF_INET;
        hints.ai_socktype = SOCK_STREAM;
        hints.ai_protocol = IPPROTO_TCP;

        std::string portStr = std::to_string(port);
        if (getaddrinfo(host.c_str(), portStr.c_str(), &hints, &res) != 0 || !res) {
            return INVALID_SOCKET;
        }

        SOCKET s = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
        if (s == INVALID_SOCKET) { freeaddrinfo(res); return INVALID_SOCKET; }

        // Set send/recv timeout
        DWORD tv = (DWORD)timeoutMs;
        setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, (const char*)&tv, sizeof(tv));
        setsockopt(s, SOL_SOCKET, SO_SNDTIMEO, (const char*)&tv, sizeof(tv));

        if (connect(s, res->ai_addr, (int)res->ai_addrlen) != 0) {
            closesocket(s);
            freeaddrinfo(res);
            return INVALID_SOCKET;
        }
        freeaddrinfo(res);
        return s;
    }

    // Send RTSP request and receive full response (up to blank line)
    // Handles interleaved RTP data on TCP: '$' + channel(1) + length(2) + data
    static bool rtspSendRecv_(SOCKET s, const std::string& request, std::string& response, int timeoutMs) {
        DWORD prevTo = 0;
        int prevToLen = sizeof(prevTo);
        getsockopt(s, SOL_SOCKET, SO_RCVTIMEO, (char*)&prevTo, &prevToLen);
        if (timeoutMs > 0) {
            DWORD tv = (DWORD)timeoutMs;
            setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, (const char*)&tv, sizeof(tv));
        }
        if (send(s, request.c_str(), (int)request.size(), 0) <= 0) {
            setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, (const char*)&prevTo, sizeof(prevTo));
            return false;
        }

        response.clear();
        char buf[4096];
        int maxIter = 200; // safety limit
        while (maxIter-- > 0) {
            int n = recv(s, buf, sizeof(buf) - 1, 0);
            if (n <= 0) {
                setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, (const char*)&prevTo, sizeof(prevTo));
                return !response.empty(); // connection closed or timeout
            }

            // Skip interleaved RTP frames (start with '$' = 0x24)
            int offset = 0;
            while (offset < n) {
                if ((unsigned char)buf[offset] == 0x24 && (offset + 4) <= n) {
                    // Interleaved: $ + channel(1) + length(2, big-endian)
                    int frameLen = ((unsigned char)buf[offset + 2] << 8) | (unsigned char)buf[offset + 3];
                    int skip = 4 + frameLen;
                    if (offset + skip <= n) {
                        offset += skip;
                        continue;
                    } else {
                        // Frame spans across recv calls — skip rest
                        offset = n;
                        break;
                    }
                } else {
                    break;
                }
            }

            if (offset < n) {
                response.append(buf + offset, n - offset);
            }

            // RTSP response ends with \r\n\r\n
            if (response.find("\r\n\r\n") != std::string::npos) break;
        }
        setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, (const char*)&prevTo, sizeof(prevTo));
        return !response.empty();
    }

    // Parse RTSP response status code
    static int parseStatusCode_(const std::string& resp) {
        // RTSP/1.0 200 OK\r\n
        size_t sp1 = resp.find(' ');
        if (sp1 == std::string::npos) return 0;
        try { return std::stoi(resp.substr(sp1 + 1)); } catch (...) { return 0; }
    }

    // Parse a header value from RTSP response (case-insensitive key search)
    static std::string parseHeader_(const std::string& resp, const std::string& key) {
        // Search for key: value\r\n
        std::string lowerResp = resp;
        std::string lowerKey = key;
        for (auto& c : lowerResp) c = (char)tolower((unsigned char)c);
        for (auto& c : lowerKey) c = (char)tolower((unsigned char)c);
        lowerKey += ":";

        size_t pos = lowerResp.find(lowerKey);
        if (pos == std::string::npos) return "";

        // Get the value from the ORIGINAL response (preserving case)
        pos += lowerKey.size();
        // Skip whitespace
        while (pos < resp.size() && (resp[pos] == ' ' || resp[pos] == '\t')) pos++;
        size_t end = resp.find("\r\n", pos);
        if (end == std::string::npos) end = resp.size();
        return resp.substr(pos, end - pos);
    }

    // Dynamic RTP port allocation to avoid bind conflicts on rapid channel switching
    static int findFreeUdpPort_() {
        static int nextPort = 10022;
        int port = nextPort;
        nextPort = (nextPort >= 10050) ? 10022 : nextPort + 1;
        return port;
    }

    void log_(const std::string& msg) {
        try { if (onLog) onLog(msg); } catch (...) {}
        try { CrashLog(msg.c_str()); } catch (...) {}
    }

    // SAT>IP SETUP: sends SETUP with DVB params, returns streamId
    bool sat2ipSetup_(Sat2ipSession& sess) {
        sess.cseq++;
        std::string query = extractQuery_(config_.url);
        std::string setupUrl = sess.baseUrl + query;

        // Allocate RTP port
        sess.rtpPort = findFreeUdpPort_();
        if (sess.rtpPort == 0) sess.rtpPort = 10022;

        std::ostringstream req;
        req << "SETUP " << setupUrl << " RTSP/1.0\r\n";
        req << "CSeq: " << sess.cseq << "\r\n";
        req << "Transport: RTP/AVP;unicast;client_port=" << sess.rtpPort << "-" << (sess.rtpPort + 1) << "\r\n";
        req << "\r\n";

        log_("[SAT2IP] >> SETUP " + setupUrl);
        log_("[SAT2IP] >> Transport: RTP/AVP;unicast;client_port=" + std::to_string(sess.rtpPort) + "-" + std::to_string(sess.rtpPort + 1));

        std::string resp;
        if (!rtspSendRecv_(sess.sock, req.str(), resp, config_.connect_timeout_ms)) {
            log_("[SAT2IP] SETUP send/recv failed");
            return false;
        }

        log_("[SAT2IP] << SETUP response (" + std::to_string(resp.size()) + " bytes):");
        // Log full response for debugging
        log_("[SAT2IP] << " + resp.substr(0, std::min(resp.size(), (size_t)800)));

        int status = parseStatusCode_(resp);
        log_("[SAT2IP] SETUP status=" + std::to_string(status));
        if (status != 200) {
            return false;
        }

        // Parse com.ses.streamID header
        std::string sid = parseHeader_(resp, "com.ses.streamID");
        log_("[SAT2IP] com.ses.streamID raw='" + sid + "'");
        if (!sid.empty()) {
            try { sess.streamId = std::stoi(sid); } catch (...) { sess.streamId = 0; }
        }

        // Parse Session header
        std::string rawSession = parseHeader_(resp, "Session");
        log_("[SAT2IP] Session raw='" + rawSession + "'");
        sess.sessionId = rawSession;
        // Session may contain ;timeout=xx, strip it
        size_t sc = sess.sessionId.find(';');
        if (sc != std::string::npos) sess.sessionId = sess.sessionId.substr(0, sc);

        // Parse server-assigned RTP port from Transport header if present
        std::string transport = parseHeader_(resp, "Transport");
        log_("[SAT2IP] Transport='" + transport + "'");

        log_("[SAT2IP] SETUP OK, streamId=" + std::to_string(sess.streamId) + " session='" + sess.sessionId + "' rtpPort=" + std::to_string(sess.rtpPort));
        return true;
    }

    // SAT>IP PLAY: sends PLAY with stream= path
    // NOTE: MediaStar STB often does NOT respond to PLAY (starts streaming after SETUP).
    // We send PLAY, try a brief recv (2s), and continue regardless.
    bool sat2ipPlay_(Sat2ipSession& sess) {
        sess.cseq++;
        std::string playUrl;
        if (sess.streamId > 0) {
            playUrl = sess.baseUrl + "stream=" + std::to_string(sess.streamId);
        } else {
            playUrl = sess.baseUrl;
        }

        std::ostringstream req;
        req << "PLAY " << playUrl << " RTSP/1.0\r\n";
        req << "CSeq: " << sess.cseq << "\r\n";
        if (!sess.sessionId.empty())
            req << "Session: " << sess.sessionId << "\r\n";
        req << "\r\n";

        log_("[SAT2IP] >> PLAY " + playUrl);

        // Send PLAY
        if (send(sess.sock, req.str().c_str(), (int)req.str().size(), 0) <= 0) {
            log_("[SAT2IP] PLAY send failed");
            return false;
        }

        // Try brief recv with 2s timeout (STB may not respond)
        DWORD shortTimeout = 2000;
        setsockopt(sess.sock, SOL_SOCKET, SO_RCVTIMEO, (const char*)&shortTimeout, sizeof(shortTimeout));

        char buf[4096];
        int n = recv(sess.sock, buf, sizeof(buf) - 1, 0);
        if (n > 0) {
            std::string resp(buf, n);
            log_("[SAT2IP] << PLAY response (" + std::to_string(n) + " bytes):");
            log_("[SAT2IP] << " + resp.substr(0, std::min(resp.size(), (size_t)500)));
            int status = parseStatusCode_(resp);
            log_("[SAT2IP] PLAY status=" + std::to_string(status));
            if (status == 200) {
                log_("[SAT2IP] PLAY OK");
            }
        } else {
            log_("[SAT2IP] PLAY no response (STB may stream after SETUP only)");
        }

        // Restore original timeout
        DWORD origTimeout = (DWORD)config_.connect_timeout_ms;
        setsockopt(sess.sock, SOL_SOCKET, SO_RCVTIMEO, (const char*)&origTimeout, sizeof(origTimeout));

        // Always return true — SETUP already succeeded, STB should be streaming
        return true;
    }

    // SAT>IP OPTIONS keepalive
    void sat2ipOptions_(Sat2ipSession& sess) {
        sess.cseq++;
        std::ostringstream req;
        req << "OPTIONS * RTSP/1.0\r\n";
        req << "CSeq: " << sess.cseq << "\r\n";
        if (!sess.sessionId.empty())
            req << "Session: " << sess.sessionId << "\r\n";
        req << "\r\n";

        DWORD prevSendTo = 0;
        int prevLen = sizeof(prevSendTo);
        getsockopt(sess.sock, SOL_SOCKET, SO_SNDTIMEO, (char*)&prevSendTo, &prevLen);
        DWORD tv = 200;
        setsockopt(sess.sock, SOL_SOCKET, SO_SNDTIMEO, (const char*)&tv, sizeof(tv));
        send(sess.sock, req.str().c_str(), (int)req.str().size(), 0);
        setsockopt(sess.sock, SOL_SOCKET, SO_SNDTIMEO, (const char*)&prevSendTo, sizeof(prevSendTo));
    }

    // SAT>IP TEARDOWN
    void sat2ipTeardown_(Sat2ipSession& sess) {
        if (sess.sock == INVALID_SOCKET) return;
        sess.cseq++;
        std::string tearUrl = sess.baseUrl + "stream=" + std::to_string(sess.streamId);

        std::ostringstream req;
        req << "TEARDOWN " << tearUrl << " RTSP/1.0\r\n";
        req << "CSeq: " << sess.cseq << "\r\n";
        if (!sess.sessionId.empty())
            req << "Session: " << sess.sessionId << "\r\n";
        req << "\r\n";

        if (onLog) onLog("[SAT2IP] >> TEARDOWN " + tearUrl);
        std::string resp;
        rtspSendRecv_(sess.sock, req.str(), resp, 2000);
        closesocket(sess.sock);
        sess.sock = INVALID_SOCKET;
    }

    // Generate SDP file for FFmpeg to open the RTP stream
    std::string writeSdpFile_(const std::string& stbHost, int rtpPort) {
        // Write to a temp file next to exe
        std::string dir = exeDir_();
        std::string sdpPath = (dir.empty() ? "." : dir) + "\\my4030_rtp.sdp";

        std::ofstream f(sdpPath, std::ios::trunc);
        if (!f.is_open()) return "";

        // SDP for receiving RTP unicast from STB
        // c=0.0.0.0 means accept from any source (unicast receive)
        f << "v=0\r\n";
        f << "o=- 0 0 IN IP4 0.0.0.0\r\n";
        f << "s=SAT>IP Stream\r\n";
        f << "c=IN IP4 0.0.0.0\r\n";
        f << "t=0 0\r\n";
        f << "m=video " << rtpPort << " RTP/AVP 33\r\n";
        f << "a=rtpmap:33 MP2T/90000\r\n";
        f << "a=recvonly\r\n";
        f.close();

        if (onLog) onLog("[SAT2IP] SDP written to " + sdpPath);
        return sdpPath;
    }

    // ── Main worker loop: SAT>IP handshake + FFmpeg decode ──

    void workerLoop() {
        Sat2ipSession sess;
        RtpTsBuffer rtpBuf;
        AVIOContext* avioCtx = nullptr;
        struct AudioChunk {
            int sampleRate = 0;
            int channels = 0;
            std::vector<uint8_t> pcm;
            std::chrono::steady_clock::time_point timestamp;
        };
        WaveOutPlayer audioOut;
        std::mutex audioMu;
        std::condition_variable audioCv;
        std::deque<AudioChunk> audioQ;
        size_t audioQueuedBytes = 0;
        static constexpr size_t MAX_AUDIO_QUEUE_BYTES = 1024 * 1024;
        static constexpr size_t TARGET_AUDIO_QUEUE_BYTES = 512 * 1024;
        std::atomic<bool> audioStop{false};
        std::thread audioThread;
        auto stopAudio_ = [&]() {
            audioStop = true;
            audioCv.notify_all();
            if (audioThread.joinable()) audioThread.join();
            audioOut.close();
        };
        try {
            log_("[RTSP] SAT>IP worker starting: " + config_.url);

            // Initialize FFmpeg once
            static std::once_flag once;
            std::call_once(once, [] {
                av_log_set_level(AV_LOG_INFO);
                avformat_network_init();
                av_log_set_callback(&RtspViewer::avLogCb_);
            });

            tlsSelf_ = this;

            // ── Step 1: Parse URL ──
            std::string stbHost;
            int stbPort = 554;
            if (!parseRtspHost_(config_.url, stbHost, stbPort)) {
                stats_.setError("Cannot parse RTSP URL");
                log_("[SAT2IP] " + stats_.getError());
                running_ = false;
                return;
            }
            sess.baseUrl = buildBaseUrl_(stbHost, stbPort);
            log_("[SAT2IP] Host=" + stbHost + " Port=" + std::to_string(stbPort) + " BaseUrl=" + sess.baseUrl);
            log_("[SAT2IP] Query=" + extractQuery_(config_.url));

            // ── Step 2: Connect TCP to STB RTSP port (like PlainTCP.java) ──
            log_("[SAT2IP] Connecting TCP to " + stbHost + ":" + std::to_string(stbPort) + "...");
            sess.sock = connectTcp_(stbHost, stbPort, config_.connect_timeout_ms);
            if (sess.sock == INVALID_SOCKET) {
                stats_.setError("TCP connect to " + stbHost + ":" + std::to_string(stbPort) + " failed");
                log_("[SAT2IP] " + stats_.getError());
                running_ = false;
                return;
            }
            log_("[SAT2IP] TCP connected");

            // ── Step 3: SETUP (sends DVB params, gets streamID) ──
            if (!sat2ipSetup_(sess)) {
                stats_.setError("SAT>IP SETUP failed");
                log_("[SAT2IP] " + stats_.getError());
                closesocket(sess.sock);
                sess.sock = INVALID_SOCKET;
                running_ = false;
                return;
            }

            if (stopFlag_.load()) { sat2ipTeardown_(sess); running_ = false; return; }

            // ── Step 3b: Start dedicated UDP receive thread with ring buffer ──
            // This completely decouples packet reception from FFmpeg decode.
            // The receive thread strips RTP headers and writes raw MPEG-TS into a ring buffer.
            if (!rtpBuf.start(sess.rtpPort, stopFlag_)) {
                stats_.setError("Cannot bind UDP port " + std::to_string(sess.rtpPort));
                log_("[SAT2IP] " + stats_.getError());
                sat2ipTeardown_(sess);
                running_ = false;
                return;
            }
            log_("[SAT2IP] UDP receive thread started on port " + std::to_string(sess.rtpPort));

            // ── Step 4: PLAY ──
            sat2ipPlay_(sess);

            if (stopFlag_.load()) { rtpBuf.stop(); sat2ipTeardown_(sess); running_ = false; return; }

            if (config_.min_buffer_ms > 0) {
                int prebufMs = config_.min_buffer_ms;
                if (prebufMs < 300) prebufMs = 300;  // Minimum 300ms for HD stability
                if (prebufMs > 800) prebufMs = 800;  // Maximum 800ms to avoid too much delay
                rtpBuf.setWatermarksMs(prebufMs + 600, prebufMs + 200);
                log_("[SAT2IP] Prebuffer " + std::to_string(prebufMs) + " ms for HD video stability...");
                auto pre = std::chrono::milliseconds(prebufMs);
                auto to = pre + std::chrono::milliseconds(10000);
                if (!rtpBuf.waitForPrebuffer(pre, to, [this](const std::string& s){ log_(s); })) {
                    // Don't abort — continue anyway and let avioRead block until data arrives
                    log_("[SAT2IP] Prebuffer incomplete, continuing anyway...");
                }
            }

            // ── Step 5: Open FFmpeg on custom AVIO (reads MPEG-TS from ring buffer) ──
            log_("[SAT2IP] Opening FFmpeg with custom AVIO (ring buffer)...");
            int err = -1;

            // Allocate AVIO buffer and context
            const int avioBufSize = 188 * 512; // ~96KB, multiple of TS packet size
            uint8_t* avioBuf = (uint8_t*)av_malloc(avioBufSize);
            avioCtx = avio_alloc_context(
                avioBuf, avioBufSize, 0/*read-only*/, &rtpBuf,
                &RtpTsBuffer::avioRead, nullptr, nullptr);

            AVFormatContext* fmt = avformat_alloc_context();
            fmt->pb = avioCtx;
            fmt->interrupt_callback.callback = [](void* opaque) -> int {
                return static_cast<RtspViewer*>(opaque)->stopFlag_.load() ? 1 : 0;
            };
            fmt->interrupt_callback.opaque = this;
            fmt->flags |= AVFMT_FLAG_CUSTOM_IO;

            // Open as mpegts (we already stripped RTP headers in the receive thread)
            const AVInputFormat* tsFmt = av_find_input_format("mpegts");
            AVDictionary* opts = nullptr;
            av_dict_set_int(&opts, "analyzeduration", 1000000, 0); // 1s - reasonable analysis
            av_dict_set_int(&opts, "probesize", 188 * 500, 0); // ~94KB - moderate probe
            av_dict_set(&opts, "max_delay", "0", 0); // Reduce initial delay
            err = avformat_open_input(&fmt, nullptr, tsFmt, &opts);
            av_dict_free(&opts);

            if (err < 0 || !fmt) {
                stats_.setError("avformat_open_input failed: " + ffErr_(err));
                log_("[SAT2IP] " + stats_.getError());
                if (avioCtx) { av_freep(&avioCtx->buffer); avio_context_free(&avioCtx); }
                rtpBuf.stop();
                sat2ipTeardown_(sess);
                running_ = false;
                return;
            }
            log_("[SAT2IP] AVIO+mpegts opened OK");

            // ── Discover all streams using FFmpeg's stream info discovery ──
            // This is necessary to properly detect audio streams in HD content
            av_log_set_level(AV_LOG_QUIET); // completely silent during discovery

            int vIndex = -1;
            int aIndex = -1;
            AVCodecContext* cc = nullptr;
            const AVCodec* dec = nullptr;

            AVCodecContext* acc = nullptr;
            const AVCodec* adec = nullptr;
            SwrContext* swr = nullptr;
            AVFrame* aFrame = nullptr;

            {
                log_("[SAT2IP] Analyzing streams with avformat_find_stream_info...");
                AVDictionary* streamOpts = nullptr;
                av_dict_set_int(&streamOpts, "analyzeduration", 1000000, 0); // 1s analysis
                av_dict_set_int(&streamOpts, "probesize", 188 * 500, 0); // ~94KB probe
                int analyzeErr = avformat_find_stream_info(fmt, &streamOpts);
                av_dict_free(&streamOpts);
                
                if (analyzeErr < 0) {
                    log_("[SAT2IP] Stream analysis failed: " + ffErr_(analyzeErr) + ", will try manual discovery");
                } else {
                    log_("[SAT2IP] Stream analysis successful, found " + std::to_string(fmt->nb_streams) + " streams");
                    for (unsigned int i = 0; i < fmt->nb_streams; i++) {
                        AVStream* s = fmt->streams[i];
                        const char* type = "unknown";
                        if (s->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) type = "VIDEO";
                        else if (s->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) type = "AUDIO";
                        else if (s->codecpar->codec_type == AVMEDIA_TYPE_SUBTITLE) type = "SUBTITLE";
                        log_("[SAT2IP] Stream " + std::to_string(i) + ": " + type + 
                            " codec_id=" + std::to_string((int)s->codecpar->codec_id));
                    }
                }
                
                // Find best video and audio streams
                vIndex = av_find_best_stream(fmt, AVMEDIA_TYPE_VIDEO, -1, -1, nullptr, 0);
                aIndex = av_find_best_stream(fmt, AVMEDIA_TYPE_AUDIO, -1, -1, nullptr, 0);
                
                if (vIndex >= 0) {
                    log_("[SAT2IP] Video stream found at index " + std::to_string(vIndex));
                } else {
                    log_("[SAT2IP] No video stream found");
                }
                
                if (aIndex >= 0) {
                    log_("[SAT2IP] Audio stream found at index " + std::to_string(aIndex));
                } else {
                    log_("[SAT2IP] No audio stream found - will retry during decode");
                }
            }
            av_log_set_level(AV_LOG_QUIET);

            if (vIndex < 0) {
                stats_.setError("No video stream found");
                log_("[SAT2IP] " + stats_.getError());
                avformat_close_input(&fmt);
                if (avioCtx) { av_freep(&avioCtx->buffer); avio_context_free(&avioCtx); }
                rtpBuf.stop();
                sat2ipTeardown_(sess);
                running_ = false;
                return;
            }

            rtpBuf.enableThrottle(true);

            AVStream* vs = fmt->streams[vIndex];
            dec = avcodec_find_decoder(vs->codecpar->codec_id);
            if (!dec) {
                stats_.setError("No decoder for codec " + std::to_string((int)vs->codecpar->codec_id));
                log_("[SAT2IP] " + stats_.getError());
                avformat_close_input(&fmt);
                if (avioCtx) { av_freep(&avioCtx->buffer); avio_context_free(&avioCtx); }
                rtpBuf.stop();
                sat2ipTeardown_(sess);
                running_ = false;
                return;
            }

            cc = avcodec_alloc_context3(dec);
            avcodec_parameters_to_context(cc, vs->codecpar);
            cc->err_recognition = 0;
            cc->thread_count = 0;
            cc->skip_frame = AVDISCARD_DEFAULT;
            err = avcodec_open2(cc, dec, nullptr);
            if (err < 0) {
                stats_.setError("avcodec_open2 failed: " + ffErr_(err));
                log_("[SAT2IP] " + stats_.getError());
                avcodec_free_context(&cc);
                avformat_close_input(&fmt);
                if (avioCtx) { av_freep(&avioCtx->buffer); avio_context_free(&avioCtx); }
                rtpBuf.stop();
                sat2ipTeardown_(sess);
                running_ = false;
                return;
            }

            stats_.connected = true;
            stats_.codec_name = dec->name ? dec->name : "";
            log_("[SAT2IP] Decoder: " + stats_.codec_name +
                " codecpar=" + std::to_string(vs->codecpar->width) + "x" + std::to_string(vs->codecpar->height) +
                " codec_id=" + std::to_string((int)vs->codecpar->codec_id));

            auto tryInitAudio = [&]() {
                if (acc) return;
                
                // Try to find audio stream multiple times with different strategies
                aIndex = av_find_best_stream(fmt, AVMEDIA_TYPE_AUDIO, -1, -1, nullptr, 0);
                if (aIndex < 0 || aIndex >= (int)fmt->nb_streams) {
                    // Try manual search through all streams
                    log_("[SAT2IP] No audio stream found with av_find_best_stream, searching manually...");
                    for (unsigned int i = 0; i < fmt->nb_streams; i++) {
                        if (fmt->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
                            aIndex = i;
                            log_("[SAT2IP] Found audio stream at index " + std::to_string(i));
                            break;
                        }
                    }
                }
                
                if (aIndex < 0 || aIndex >= (int)fmt->nb_streams) {
                    log_("[SAT2IP] No audio stream found in any stream");
                    return;
                }
                
                AVStream* as = fmt->streams[aIndex];
                log_("[SAT2IP] Audio stream details: codec_id=" + std::to_string((int)as->codecpar->codec_id) +
                    " sample_rate=" + std::to_string(as->codecpar->sample_rate) +
                    " channels=" + std::to_string(as->codecpar->ch_layout.nb_channels) +
                    " format=" + std::to_string((int)as->codecpar->format) +
                    " bitrate=" + std::to_string(as->codecpar->bit_rate));
                
                // Try multiple decoder attempts
                adec = avcodec_find_decoder(as->codecpar->codec_id);
                if (!adec) {
                    log_("[SAT2IP] No decoder found for audio codec " + std::to_string((int)as->codecpar->codec_id) + ", trying fallback...");
                    // Try to find any audio decoder
                    const AVCodec* fallback = nullptr;
                    void* opaque = nullptr;
                    while ((fallback = av_codec_iterate(&opaque))) {
                        if (fallback->type == AVMEDIA_TYPE_AUDIO && fallback->id == as->codecpar->codec_id) {
                            adec = fallback;
                            log_("[SAT2IP] Found fallback decoder: " + std::string(fallback->name ? fallback->name : "unknown"));
                            break;
                        }
                    }
                }
                
                if (!adec) {
                    log_("[SAT2IP] CRITICAL: No decoder available for audio codec " + std::to_string((int)as->codecpar->codec_id));
                    return;
                }

                acc = avcodec_alloc_context3(adec);
                if (!acc) {
                    log_("[SAT2IP] Failed to allocate audio codec context");
                    return;
                }
                avcodec_parameters_to_context(acc, as->codecpar);
                acc->thread_count = 0;
                // Enable low delay for better sync
                acc->flags |= AV_CODEC_FLAG_LOW_DELAY;
                // Enable error concealment for better audio recovery
                acc->err_recognition = AV_EF_CAREFUL;
                
                int aerr = avcodec_open2(acc, adec, nullptr);
                if (aerr < 0) {
                    log_("[SAT2IP] Failed to open audio decoder: " + ffErr_(aerr) + ", retrying with different options...");
                    // Retry without low delay flag
                    acc->flags &= ~AV_CODEC_FLAG_LOW_DELAY;
                    aerr = avcodec_open2(acc, adec, nullptr);
                    if (aerr < 0) {
                        log_("[SAT2IP] CRITICAL: Failed to open audio decoder after retry: " + ffErr_(aerr));
                        avcodec_free_context(&acc);
                        return;
                    }
                }
                aFrame = av_frame_alloc();
                log_("[SAT2IP] Audio decoder SUCCESSFULLY opened: " + std::string(adec->name ? adec->name : "") + 
                    " (index=" + std::to_string(aIndex) + 
                    " sr=" + std::to_string(acc->sample_rate) + 
                    " ch=" + std::to_string(acc->ch_layout.nb_channels) + 
                    " format=" + std::to_string((int)acc->sample_fmt) + ")");
            };

            // Try once here, and again later during decode loop if it wasn't discovered yet.
            tryInitAudio();

            audioThread = std::thread([&] {
                auto lastAudioTime = std::chrono::steady_clock::now();
                while (!audioStop.load()) {
                    AudioChunk chunk;
                    bool hasChunk = false;
                    {
                        std::unique_lock<std::mutex> lk(audioMu);
                        audioCv.wait_for(lk, std::chrono::milliseconds(2), [&] {
                            return audioStop.load() || !audioQ.empty();
                        });
                        if (audioStop.load() && audioQ.empty()) break;
                        if (!audioQ.empty()) {
                            chunk = std::move(audioQ.front());
                            audioQueuedBytes -= chunk.pcm.size();
                            audioQ.pop_front();
                            hasChunk = true;
                        }
                    }
                    
                    if (hasChunk) {
                        // Reopen audio device if format changed
                        if (!audioOut.isOpen() || audioOut.sampleRate() != chunk.sampleRate || audioOut.channels() != chunk.channels) {
                            audioOut.close();
                            if (audioOut.open(chunk.sampleRate, chunk.channels)) {
                                log_("[SAT2IP] Audio device opened: " + std::to_string(chunk.sampleRate) + "Hz " + std::to_string(chunk.channels) + "ch");
                            }
                        }
                        
                        if (audioOut.isOpen() && !chunk.pcm.empty()) {
                            // Intelligent audio-video sync
                            auto now = std::chrono::steady_clock::now();
                            int64_t offsetUs = aVOffsetUs_.load();
                            
                            // Calculate audio delay based on offset
                            auto audioDelay = now - chunk.timestamp;
                            int64_t audioDelayUs = std::chrono::duration_cast<std::chrono::microseconds>(audioDelay).count();
                            
                            // Total sync error = (timestamp delay) + (audio-video PTS offset)
                            int64_t syncErrorUs = audioDelayUs + offsetUs;
                            
                            // Intelligent sync correction:
                            // If audio is ahead (> 40ms), drop some samples or wait
                            // If audio is behind (< -40ms), we can't speed up, so just play as-is
                            if (syncErrorUs > 40000) {
                                // Audio is too far ahead (> 40ms), wait to sync
                                int waitMs = (int)(syncErrorUs / 1000) - 20;
                                if (waitMs > 0 && waitMs < 100) {
                                    std::this_thread::sleep_for(std::chrono::milliseconds(waitMs));
                                }
                            } else if (syncErrorUs < -40000) {
                                // Audio is too far behind (> 40ms), we can't fix this by dropping
                                // Just play normally - video will catch up or we accept the lag
                            }
                            
                            // Log sync status periodically
                            static int syncLogCount = 0;
                            if (++syncLogCount % 500 == 0) {
                                log_("[SAT2IP][SYNC] A-V offset=" + std::to_string(offsetUs / 1000) + "ms, delay=" + std::to_string(audioDelayUs / 1000) + "ms, error=" + std::to_string(syncErrorUs / 1000) + "ms");
                            }
                            
                            audioOut.submit(chunk.pcm.data(), chunk.pcm.size());
                            lastAudioTime = now;
                        }
                    } else {
                        // No audio data, short sleep to prevent CPU spinning
                        std::this_thread::sleep_for(std::chrono::milliseconds(1));
                    }
                }
            });

            int dstW = (config_.target_width > 0) ? config_.target_width : cc->width;
            int dstH = (config_.target_height > 0) ? config_.target_height : cc->height;
            if (dstW <= 0 || dstH <= 0) { dstW = 1920; dstH = 1080; }

            SwsContext* sws = nullptr;
            AVFrame* frame = av_frame_alloc();
            AVFrame* outFrame = av_frame_alloc();
            AVPacket* pkt = av_packet_alloc();
            std::vector<uint8_t> outBuf;
            int curSrcW = 0, curSrcH = 0;

            auto initSws = [&](int srcW, int srcH) {
                if (sws) sws_freeContext(sws);
                dstW = (config_.target_width > 0) ? config_.target_width : srcW;
                dstH = (config_.target_height > 0) ? config_.target_height : srcH;
                sws = sws_getContext(srcW, srcH, cc->pix_fmt,
                    dstW, dstH, AV_PIX_FMT_BGRA,
                    SWS_FAST_BILINEAR, nullptr, nullptr, nullptr);
                int bufSize = av_image_get_buffer_size(AV_PIX_FMT_BGRA, dstW, dstH, 1);
                outBuf.resize(bufSize > 0 ? (size_t)bufSize : 0);
                if (!outBuf.empty())
                    av_image_fill_arrays(outFrame->data, outFrame->linesize, outBuf.data(), AV_PIX_FMT_BGRA, dstW, dstH, 1);
                curSrcW = srcW;
                curSrcH = srcH;
                ensureTexture_(dstW, dstH);
                if (onLog) onLog("[SAT2IP] SwsContext: " + std::to_string(srcW) + "x" + std::to_string(srcH) + " -> " + std::to_string(dstW) + "x" + std::to_string(dstH));
            };

            // ── Step 6: Decode loop with OPTIONS keepalive ──
            uint64_t frameCount = 0;
            auto tLastFps = std::chrono::steady_clock::now();
            auto tLastKeepalive = tLastFps;
            uint64_t framesSince = 0, bytesSince = 0;
            bool gotFirstFrame = false;
            int goodKeyframes = 0;
            int audioInitRetryCount = 0;

            log_("[SAT2IP] Entering decode loop...");

            while (!stopFlag_.load()) {
                // Try to discover/init audio stream lazily (PMT may arrive later)
                if (!acc && audioInitRetryCount < 50) {
                    try { tryInitAudio(); } catch (...) {}
                    audioInitRetryCount++;
                    if (acc) {
                        log_("[SAT2IP] Audio initialized after " + std::to_string(audioInitRetryCount) + " attempts");
                    } else if (audioInitRetryCount % 10 == 0) {
                        log_("[SAT2IP] Still trying to initialize audio (attempt " + std::to_string(audioInitRetryCount) + ")");
                    }
                }

                // Send OPTIONS keepalive every 8 seconds (Android uses 10)
                auto now = std::chrono::steady_clock::now();
                auto kaElapsed = std::chrono::duration_cast<std::chrono::seconds>(now - tLastKeepalive).count();
                if (kaElapsed >= 8 && sess.sock != INVALID_SOCKET) {
                    sat2ipOptions_(sess);
                    tLastKeepalive = now;
                }

                err = av_read_frame(fmt, pkt);
                if (err < 0) {
                    if (err == AVERROR_EOF) {
                        log_("[SAT2IP] EOF");
                        break;
                    }
                    if (err == AVERROR(EAGAIN)) {
                        std::this_thread::sleep_for(std::chrono::milliseconds(5));
                        continue;
                    }
                    // For I/O errors on live streams, retry instead of dying
                    if (err == AVERROR(EIO) || err == AVERROR(ETIMEDOUT)) {
                        std::this_thread::sleep_for(std::chrono::milliseconds(10));
                        continue;
                    }
                    stats_.setError("av_read_frame: " + ffErr_(err));
                    log_("[SAT2IP] " + stats_.getError());
                    break;
                }

                bytesSince += (uint64_t)pkt->size;
                stats_.bytes_received += (uint64_t)pkt->size;

                if (pkt->stream_index == vIndex) {
                    err = avcodec_send_packet(cc, pkt);
                    if (err >= 0) {
                        while (!stopFlag_.load()) {
                            err = avcodec_receive_frame(cc, frame);
                            if (err == AVERROR(EAGAIN) || err == AVERROR_EOF) break;
                            if (err < 0) break;

                            // Skip frames with invalid dimensions (corrupt/partial)
                            if (frame->width <= 0 || frame->height <= 0) continue;

                            if (!gotFirstFrame) {
                                gotFirstFrame = true;
                                log_("[SAT2IP] First frame: " + std::to_string(frame->width) + "x" + std::to_string(frame->height));
                            }

                            // Lazy-init sws on first frame or resolution change
                            if (frame->width != curSrcW || frame->height != curSrcH) {
                                initSws(frame->width, frame->height);
                            }

                            if (!sws || outBuf.empty()) continue;

                            sws_scale(sws, frame->data, frame->linesize, 0, frame->height, outFrame->data, outFrame->linesize);

                            struct { int w, h, stride; std::vector<uint8_t> bgra; } item;
                            item.w = dstW;
                            item.h = dstH;
                            item.stride = outFrame->linesize[0];

                            // Obtain reusable BGRA buffer from pool (or allocate new)
                            {
                                std::vector<uint8_t> buf;
                                {
                                    std::lock_guard<std::mutex> gp(frameMu_);
                                    if (!framePool_.empty()) {
                                        buf = std::move(framePool_.back());
                                        framePool_.pop_back();
                                    }
                                }
                                if (buf.size() < outBuf.size()) buf.resize(outBuf.size());
                                memcpy(buf.data(), outBuf.data(), outBuf.size());
                                item.bgra = std::move(buf);
                            }

                            {
                                std::lock_guard<std::mutex> g(frameMu_);
                                FrameItem fi;
                                fi.w = item.w;
                                fi.h = item.h;
                                fi.stride = item.stride;
                                fi.bgra = std::move(item.bgra);
                                auto now2 = std::chrono::steady_clock::now();

                                // PTS-based wall-clock timing
                                int64_t pts = frame->best_effort_timestamp;
                                if (pts == AV_NOPTS_VALUE) pts = frame->pts;
                                if (pts != AV_NOPTS_VALUE && vs->time_base.den > 0) {
                                    if (!qHaveClock_) {
                                        qBasePts_ = pts;
                                        qBaseWall_ = now2;
                                        qHaveClock_ = true;
                                        fi.due = qBaseWall_;
                                    } else {
                                        double dt = (double)(pts - qBasePts_) * av_q2d(vs->time_base);
                                        auto offset = std::chrono::microseconds((int64_t)(dt * 1000000.0));
                                        fi.due = qBaseWall_ + offset;
                                        
                                        // Intelligent drift correction: only re-anchor if drift is large AND persistent
                                        auto diff = fi.due - now2;
                                        if (diff > std::chrono::milliseconds(200) || diff < std::chrono::milliseconds(-200)) {
                                            // Large drift detected - re-anchor
                                            qBasePts_ = pts;
                                            qBaseWall_ = now2;
                                            fi.due = now2;
                                            log_("[SAT2IP][SYNC] Video clock re-anchored (drift=" + std::to_string(std::chrono::duration_cast<std::chrono::milliseconds>(diff).count()) + "ms)");
                                        }
                                    }
                                } else {
                                    fi.due = now2;
                                }

                                // Drop late frames aggressively to prevent queue buildup
                                if (!frameQ_.empty() && fi.due < now2 - std::chrono::milliseconds(100)) {
                                    qHaveClock_ = true;
                                    if (pts != AV_NOPTS_VALUE) qBasePts_ = pts;
                                    qBaseWall_ = now2;
                                    fi.due = now2;
                                    while (!frameQ_.empty()) {
                                        if (framePool_.size() < MAX_FRAME_POOL)
                                            framePool_.push_back(std::move(frameQ_.front().bgra));
                                        frameQ_.pop_front();
                                        stats_.frames_dropped++;
                                    }
                                }

                                while (frameQ_.size() >= MAX_FRAME_QUEUE) {
                                    if (framePool_.size() < MAX_FRAME_POOL)
                                        framePool_.push_back(std::move(frameQ_.front().bgra));
                                    frameQ_.pop_front();
                                    stats_.frames_dropped++;
                                }
                                frameQ_.push_back(std::move(fi));
                            }

                            if (!stats_.has_video.load()) {
                                stats_.has_video = true;
                                stats_.width = frame->width;
                                stats_.height = frame->height;
                            }

                            frameCount++;
                            framesSince++;
                            stats_.frames_decoded = frameCount;

                            now = std::chrono::steady_clock::now();
                            auto dt = std::chrono::duration_cast<std::chrono::milliseconds>(now - tLastFps).count();
                            if (dt >= 1000) {
                                float sec = (float)dt / 1000.0f;
                                stats_.fps = (sec > 0.0f) ? (float)framesSince / sec : 0.0f;
                                stats_.bitrate_kbps = (sec > 0.0f) ? (int)((bytesSince * 8) / 1000 / sec) : stats_.bitrate_kbps.load();
                                {
                                    auto m = rtpBuf.getMetrics();
                                    auto d = m.diag;
                                    stats_.rtp_packets = d.rtp_packets;
                                    stats_.rtp_late_drops = d.rtp_late_drops;
                                    stats_.rtp_gap_skips = d.rtp_gap_skips;
                                    stats_.ts_packets = d.ts_packets;
                                    stats_.ts_pending_bytes = d.ts_pending_bytes;

                                    log_("[SAT2IP][buf] fps=" + std::to_string(stats_.fps.load()) +
                                        " drop=" + std::to_string(stats_.frames_dropped.load()) +
                                        " avail=" + std::to_string((uint64_t)m.avail_bytes) +
                                        "B buf=" + std::to_string(m.buffered_ms) + "ms" +
                                        " bps=" + std::to_string((uint64_t)m.bps) +
                                        " sbps=" + std::to_string((uint64_t)m.stable_bps) +
                                        " lastRtpAgo=" + std::to_string(m.last_rtp_ago_ms) + "ms" +
                                        " low=" + std::to_string(m.low_ms) + "ms" +
                                        " target=" + std::to_string(m.target_ms) + "ms" +
                                        " thr=" + std::string(m.throttle_active ? "1" : "0") +
                                        " late=" + std::to_string((unsigned long long)d.rtp_late_drops) +
                                        " skip=" + std::to_string((unsigned long long)d.rtp_gap_skips) +
                                        " trunc=" + std::to_string((unsigned long long)d.rtp_truncations) +
                                        " slotTrunc=" + std::to_string((unsigned long long)d.rtp_slot_truncations) +
                                        " ext=" + std::to_string((unsigned long long)d.rtp_ext_packets) +
                                        " extFB=" + std::to_string((unsigned long long)d.rtp_ext_fallback) +
                                        " extBad=" + std::to_string((unsigned long long)d.rtp_ext_bad) +
                                        " resync=" + std::to_string((unsigned long long)d.rtp_resyncs) +
                                        " refillEv=" + std::to_string((unsigned long long)d.avio_refill_events) +
                                        " refillWaitMs=" + std::to_string((unsigned long long)d.avio_refill_wait_ms_total) +
                                        " tsPendB=" + std::to_string((unsigned long long)d.ts_pending_bytes));
                                }
                                framesSince = 0;
                                bytesSince = 0;
                                tLastFps = now;
                            }
                        }
                    }
                } else if (aIndex >= 0 && pkt->stream_index == aIndex && !acc) {
                    static uint64_t audioPacketsNoDecoder = 0;
                    audioPacketsNoDecoder++;
                    if (audioPacketsNoDecoder == 1) {
                        log_("[SAT2IP] Audio packets received but decoder not ready yet");
                    }
                    if (audioPacketsNoDecoder % 50 == 0) {
                        log_("[SAT2IP] Audio packets waiting for decoder: " + std::to_string(audioPacketsNoDecoder));
                    }
                } else if (acc && pkt->stream_index == aIndex) {
                    static uint64_t audioPackets = 0;
                    static uint64_t audioFrames = 0;
                    audioPackets++;
                    
                    int aerr = avcodec_send_packet(acc, pkt);
                    if (aerr >= 0 && aFrame) {
                        while (!stopFlag_.load()) {
                            aerr = avcodec_receive_frame(acc, aFrame);
                            if (aerr == AVERROR(EAGAIN) || aerr == AVERROR_EOF) break;
                            if (aerr < 0) {
                                log_("[SAT2IP] Audio decode error: " + ffErr_(aerr));
                                break;
                            }
                            
                            audioFrames++;
                            if (audioFrames == 1) {
                                log_("[SAT2IP] First audio frame decoded successfully!");
                            }
                            if (audioPackets % 100 == 0) {
                                log_("[SAT2IP] Audio progress: packets=" + std::to_string(audioPackets) + " frames=" + std::to_string(audioFrames));
                            }

                            int ch = aFrame->ch_layout.nb_channels;
                            int sr = aFrame->sample_rate;
                            if (ch <= 0) ch = acc->ch_layout.nb_channels;
                            if (sr <= 0) sr = acc->sample_rate;
                            if (ch <= 0 || sr <= 0) {
                                log_("[SAT2IP] Invalid audio parameters: ch=" + std::to_string(ch) + " sr=" + std::to_string(sr));
                                continue;
                            }

                            if (!swr) {
                                AVChannelLayout outLayout = aFrame->ch_layout;
                                if (outLayout.nb_channels <= 0) outLayout = acc->ch_layout;
                                if (outLayout.nb_channels <= 0) av_channel_layout_default(&outLayout, ch);
                                AVChannelLayout inLayout = aFrame->ch_layout;
                                if (inLayout.nb_channels <= 0) inLayout = acc->ch_layout;
                                if (inLayout.nb_channels <= 0) av_channel_layout_default(&inLayout, ch);
                                if (swr_alloc_set_opts2(&swr,
                                        &outLayout, AV_SAMPLE_FMT_S16, sr,
                                        &inLayout, (AVSampleFormat)aFrame->format, sr,
                                        0, nullptr) >= 0) {
                                    if (swr_init(swr) < 0) {
                                        swr_free(&swr);
                                    }
                                }
                            }

                            if (swr) {
                                int outSamplesMax = swr_get_out_samples(swr, aFrame->nb_samples);
                                if (outSamplesMax <= 0) continue;
                                std::vector<int16_t> pcm;
                                pcm.resize((size_t)outSamplesMax * (size_t)ch);
                                uint8_t* outData[1] = { (uint8_t*)pcm.data() };
                                const uint8_t** inData = (const uint8_t**)aFrame->extended_data;
                                int outSamples = swr_convert(swr, outData, outSamplesMax, inData, aFrame->nb_samples);
                                if (outSamples > 0) {
                                    size_t outBytes = (size_t)outSamples * (size_t)ch * sizeof(int16_t);
                                    AudioChunk chunk;
                                    chunk.sampleRate = sr;
                                    chunk.channels = ch;
                                    chunk.pcm.resize(outBytes);
                                    memcpy(chunk.pcm.data(), pcm.data(), outBytes);
                                    
                                    // Intelligent audio-video sync: use audio's own clock base
                                    auto now = std::chrono::steady_clock::now();
                                    if (aFrame->best_effort_timestamp != AV_NOPTS_VALUE && fmt->streams[aIndex]->time_base.den > 0) {
                                        if (!aHaveClock_) {
                                            aBasePts_ = aFrame->best_effort_timestamp;
                                            aBaseWall_ = now;
                                            aHaveClock_ = true;
                                            chunk.timestamp = aBaseWall_;
                                        } else {
                                            double dt = (double)(aFrame->best_effort_timestamp - aBasePts_) * av_q2d(fmt->streams[aIndex]->time_base);
                                            chunk.timestamp = aBaseWall_ + std::chrono::microseconds((int64_t)(dt * 1000000.0));
                                            
                                            // Calculate audio-video offset
                                            if (qHaveClock_) {
                                                double vdt = (double)(aFrame->best_effort_timestamp - qBasePts_) * av_q2d(fmt->streams[aIndex]->time_base);
                                                auto expectedVideoTime = qBaseWall_ + std::chrono::microseconds((int64_t)(vdt * 1000000.0));
                                                int64_t offsetUs = std::chrono::duration_cast<std::chrono::microseconds>(chunk.timestamp - expectedVideoTime).count();
                                                aVOffsetUs_.store(offsetUs);
                                            }
                                        }
                                    } else {
                                        chunk.timestamp = now;
                                    }
                                    {
                                        std::lock_guard<std::mutex> lk(audioMu);
                                        while (!audioQ.empty() && audioQueuedBytes + chunk.pcm.size() > MAX_AUDIO_QUEUE_BYTES) {
                                            audioQueuedBytes -= audioQ.front().pcm.size();
                                            audioQ.pop_front();
                                        }
                                        if (audioQueuedBytes > TARGET_AUDIO_QUEUE_BYTES && !audioQ.empty()) {
                                            while (!audioQ.empty() && audioQueuedBytes > TARGET_AUDIO_QUEUE_BYTES) {
                                                audioQueuedBytes -= audioQ.front().pcm.size();
                                                audioQ.pop_front();
                                            }
                                        }
                                        audioQueuedBytes += chunk.pcm.size();
                                        audioQ.push_back(std::move(chunk));
                                    }
                                    audioCv.notify_one();
                                }
                            }
                        }
                    }
                }
                av_packet_unref(pkt);
            }

            stopAudio_();

            // ── Cleanup ──
            av_packet_free(&pkt);
            av_frame_free(&outFrame);
            av_frame_free(&frame);
            if (aFrame) av_frame_free(&aFrame);
            if (sws) sws_freeContext(sws);
            if (swr) swr_free(&swr);
            if (acc) avcodec_free_context(&acc);
            avcodec_free_context(&cc);
            avformat_close_input(&fmt);
            if (avioCtx) { av_freep(&avioCtx->buffer); avio_context_free(&avioCtx); }
            rtpBuf.stop();

            // Send TEARDOWN to STB
            sat2ipTeardown_(sess);

            tlsSelf_ = nullptr;
        } catch (const std::exception& e) {
            try { stopAudio_(); } catch (...) {}
            stats_.setError(std::string("SAT2IP worker exception: ") + e.what());
            try { if (onLog) onLog("[SAT2IP] " + stats_.getError()); } catch (...) {}
            try { rtpBuf.stop(); } catch (...) {}
            if (sess.sock != INVALID_SOCKET) { try { sat2ipTeardown_(sess); } catch (...) {} }
        } catch (...) {
            try { stopAudio_(); } catch (...) {}
            stats_.setError("SAT2IP worker exception (unknown)");
            try { if (onLog) onLog("[SAT2IP] " + stats_.getError()); } catch (...) {}
            try { rtpBuf.stop(); } catch (...) {}
            if (sess.sock != INVALID_SOCKET) { try { closesocket(sess.sock); } catch (...) {} }
        }
        tlsSelf_ = nullptr;
        stats_.connected = false;
        running_ = false;
    }
    
    RtspViewerConfig config_;
    RtspStats stats_;

    std::mutex texMu_;
    ID3D11Texture2D* texture_ = nullptr;
    ID3D11ShaderResourceView* srv_ = nullptr;

    std::mutex frameMu_;

    struct FrameItem {
        std::chrono::steady_clock::time_point due{};
        int w = 0, h = 0, stride = 0;
        std::vector<uint8_t> bgra;
    };
    static constexpr size_t MAX_FRAME_QUEUE = 8;
    static constexpr size_t MAX_FRAME_POOL = 8;
    std::deque<FrameItem> frameQ_;
    std::vector<std::vector<uint8_t>> framePool_; // reusable BGRA buffers
    
    // Audio-video sync tracking
    bool qHaveClock_ = false;
    int64_t qBasePts_ = 0;
    std::chrono::steady_clock::time_point qBaseWall_{};
    
    // Audio-specific sync tracking
    bool aHaveClock_ = false;
    int64_t aBasePts_ = 0;
    std::chrono::steady_clock::time_point aBaseWall_{};
    std::atomic<int64_t> aVOffsetUs_{0}; // Audio-video offset in microseconds (positive = audio ahead)
    int64_t aVOffsetSamples_{0}; // Offset in audio samples for smooth adjustment
    
    std::atomic<bool> running_{false};
    std::atomic<bool> stopFlag_{false};
    std::thread workerThread_;
    
    ID3D11Device* d3dDevice_ = nullptr;
    ID3D11DeviceContext* d3dCtx_ = nullptr;

    inline static thread_local RtspViewer* tlsSelf_ = nullptr;
    inline static int nextRtpPort_ = 10022;
};

} // namespace stb
