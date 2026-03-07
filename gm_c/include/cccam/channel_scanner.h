#pragma once
// ChannelScanner: auto-scans encrypted channels on the STB, collecting ECM/CW samples.
// TurboPipeline:  unified sub-3s ECM response chaining all decryption tools.
//
// Scanner workflow:
//   1. Filter encrypted channels with video_pid > 0 (up to 200)
//   2. Switch STB to next channel every 30s
//   3. Monitor ECM traffic — if no ECM/CW within 5s, mark as poor signal
//   4. Feed all collected ECM/CW to: CaEngine, CwPredictor, CwLearner, OfflineCwDb, Harvester
//   5. Save scan results for AI training
//
// TurboPipeline:
//   Chains: CW cache → CA Engine predict → Offline DB → Upstream (pre-fetch aware, smart-routed)
//   → AI fast predict → AI Ollama predict
//   Target: < 3 second total response

#include <string>
#include <vector>
#include <thread>
#include <atomic>
#include <mutex>
#include <chrono>
#include <functional>
#include <algorithm>
#include <cstdint>
#include <cstring>
#include <ctime>
#include <fstream>
#include <sstream>
#include <set>
#include <array>
 #include <random>

namespace cccam {

// ═══════════════════════════════════════════════════════════════════════════
//  SCAN SAMPLE — one ECM/CW observation from a channel
// ═══════════════════════════════════════════════════════════════════════════
struct ScanSample {
    uint16_t caid = 0;
    uint32_t provid = 0;
    uint16_t sid = 0;
    std::array<uint8_t, 16> cw{};
    bool     cw_ok = false;
    float    latency_ms = 0;
    std::string server;
    time_t   timestamp = 0;
    int      service_index = -1;
    std::string service_name;
};

// ═══════════════════════════════════════════════════════════════════════════
//  SCANNED CHANNEL STATE — per-channel scan progress
// ═══════════════════════════════════════════════════════════════════════════
struct ScannedChannel {
    int    service_index = -1;
    std::string service_id;
    std::string service_name;
    bool   is_radio = false;

    // Scan metrics
    int    scan_count = 0;         // how many times we've dwelled on this channel
    int    ecm_received = 0;       // ECMs seen during dwell
    int    cw_obtained = 0;        // successful CWs
    int    cw_failed = 0;
    float  avg_latency_ms = 0;
    time_t last_scan = 0;
    time_t first_scan = 0;

    // Signal quality proxy (based on ECM success)
    // good = CW obtained within 5s, poor = no CW within dwell
    enum class Quality : uint8_t { Unknown = 0, Good, Poor, NoSignal };
    Quality signal_quality = Quality::Unknown;

    // Collected CA info
    uint16_t caid = 0;
    uint32_t provid = 0;
    uint16_t sid = 0;
    std::string best_server;

    // Last CW for this channel
    std::array<uint8_t, 16> last_cw{};
    bool has_cw = false;

    float successRate() const {
        int total = cw_obtained + cw_failed;
        return total > 0 ? (float)cw_obtained / total * 100.0f : 0;
    }
};

// ═══════════════════════════════════════════════════════════════════════════
//  CHANNEL SCANNER
// ═══════════════════════════════════════════════════════════════════════════
class ChannelScanner {
public:
    // ── Configuration ──
    int    dwell_time_sec = 30;        // time on each channel before switching
    int    signal_timeout_sec = 5;     // if no ECM/CW within this → poor signal
    int    max_channels = 200;         // max channels to scan
    bool   skip_radio = true;          // skip radio channels (usually not encrypted interestingly)
    bool   skip_poor_signal = true;    // skip channels with consistently poor signal
    int    poor_signal_threshold = 3;  // mark as NoSignal after this many consecutive poor scans

    std::atomic<bool> running{false};
    std::atomic<bool> paused{false};

    // ── Stats ──
    struct Stats {
        int  total_channels = 0;       // channels in scan queue
        int  scanned = 0;              // channels scanned at least once
        int  good_signal = 0;          // channels with good signal
        int  poor_signal = 0;          // channels with poor signal
        int  no_signal = 0;            // channels with no signal (skipped)
        int  total_samples = 0;        // total ECM/CW samples collected
        int  total_cw_ok = 0;
        int  total_cw_fail = 0;
        int  scan_cycles = 0;          // complete passes through all channels
        int  current_index = -1;       // current channel service_index
        std::string current_name;
        float cycle_progress = 0;      // 0..1 progress through current cycle
        bool running = false;
        bool paused = false;
        time_t started_at = 0;
        float elapsed_min = 0;
    };

    // ── Callbacks (set before start) ──
    // Switch STB channel — returns true if command sent
    std::function<bool(const std::string& service_id, int tv_state)> onSwitchChannel;

    // Called when ECM/CW is obtained for the current scan channel
    // App wires this to feed all tools (CA engine, predictor, learner, offline DB, harvester)
    std::function<void(const ScanSample& sample)> onSampleCollected;

    // Logging
    std::function<void(const std::string&)> onLog;

    // ── Control ──
    void start() {
        if (running) return;
        running = true;
        paused = false;
        thread_ = std::thread(&ChannelScanner::loop, this);
    }

    void stop() {
        running = false;
        if (thread_.joinable()) thread_.join();
    }

    void pause()  { paused = true; }
    void resume() { paused = false; }

    // ── Feed channel list (call after loading channels from STB) ──
    void setChannelList(const std::vector<ScannedChannel>& encrypted_channels) {
        std::lock_guard<std::mutex> g(mu_);
        scanQueue_.clear();
        order_.clear();
        for (auto& ch : encrypted_channels) {
            if (skip_radio && ch.is_radio) continue;
            scanQueue_.push_back(ch);
            if ((int)scanQueue_.size() >= max_channels) break;
        }
        order_.reserve(scanQueue_.size());
        for (int i = 0; i < (int)scanQueue_.size(); i++) order_.push_back(i);
        shuffleOrderLocked();
        scanPos_ = 0;
        lg("Scan queue: " + std::to_string(scanQueue_.size()) + " encrypted channels");
    }

    // ── Notify scanner that ECM/CW was received for current channel ──
    // Called from CccamServer ECM pipeline when CW is obtained
    void notifyEcmResult(uint16_t caid, uint32_t provid, uint16_t sid,
                         bool cw_ok, const uint8_t* cw, float latency_ms,
                         const std::string& server)
    {
        std::lock_guard<std::mutex> g(mu_);
        if (currentIdx_ < 0 || currentIdx_ >= (int)scanQueue_.size()) return;

        auto& ch = scanQueue_[currentIdx_];
        ch.ecm_received++;
        if (cw_ok && cw) {
            ch.cw_obtained++;
            memcpy(ch.last_cw.data(), cw, 16);
            ch.has_cw = true;
            ch.best_server = server;
            if (ch.avg_latency_ms <= 0) ch.avg_latency_ms = latency_ms;
            else ch.avg_latency_ms += 0.2f * (latency_ms - ch.avg_latency_ms);

            // Mark signal quality
            ch.signal_quality = ScannedChannel::Quality::Good;
            gotCwThisDwell_ = true;
            totalCwOk_++;
        } else {
            ch.cw_failed++;
            totalCwFail_++;
        }

        ch.caid = caid;
        ch.provid = provid;
        ch.sid = sid;
        totalSamples_++;

        // Fire sample callback
        if (onSampleCollected) {
            ScanSample s;
            s.caid = caid;
            s.provid = provid;
            s.sid = sid;
            s.cw_ok = cw_ok;
            if (cw_ok && cw) memcpy(s.cw.data(), cw, 16);
            s.latency_ms = latency_ms;
            s.server = server;
            s.timestamp = time(nullptr);
            s.service_index = ch.service_index;
            s.service_name = ch.service_name;
            onSampleCollected(s);
        }
    }

    // ── Get stats ──
    Stats getStats() const {
        std::lock_guard<std::mutex> g(mu_);
        Stats s;
        s.total_channels = (int)scanQueue_.size();
        s.total_samples = totalSamples_;
        s.total_cw_ok = totalCwOk_;
        s.total_cw_fail = totalCwFail_;
        s.scan_cycles = cycles_;
        s.running = running.load();
        s.paused = paused.load();
        s.started_at = startedAt_;

        if (startedAt_ > 0)
            s.elapsed_min = (float)(time(nullptr) - startedAt_) / 60.0f;

        if (currentIdx_ >= 0 && currentIdx_ < (int)scanQueue_.size()) {
            s.current_index = scanQueue_[currentIdx_].service_index;
            s.current_name = scanQueue_[currentIdx_].service_name;
        }
        s.cycle_progress = scanQueue_.empty() ? 0 :
            (float)scanPos_ / (float)scanQueue_.size();

        for (auto& ch : scanQueue_) {
            if (ch.scan_count > 0) s.scanned++;
            if (ch.signal_quality == ScannedChannel::Quality::Good) s.good_signal++;
            else if (ch.signal_quality == ScannedChannel::Quality::Poor) s.poor_signal++;
            else if (ch.signal_quality == ScannedChannel::Quality::NoSignal) s.no_signal++;
        }
        return s;
    }

    // ── Get scanned channel list ──
    std::vector<ScannedChannel> getScannedChannels() const {
        std::lock_guard<std::mutex> g(mu_);
        return scanQueue_;
    }

    // ── Save/Load scan data ──
    bool save(const std::string& path = "") const {
        std::string fp = path.empty() ? defaultPath() : path;
        std::lock_guard<std::mutex> g(mu_);
        try {
            std::ofstream f(fp, std::ios::binary);
            if (!f) return false;
            f << "SCAN1\n";
            f << scanQueue_.size() << "\n";
            for (auto& ch : scanQueue_) {
                f << ch.service_index << " "
                  << ch.service_id << " "
                  << ch.scan_count << " "
                  << ch.ecm_received << " "
                  << ch.cw_obtained << " "
                  << ch.cw_failed << " "
                  << ch.avg_latency_ms << " "
                  << (int)ch.signal_quality << " "
                  << ch.caid << " "
                  << ch.provid << " "
                  << ch.sid << " ";
                for (int i = 0; i < 16; i++) {
                    char h[4]; snprintf(h, sizeof(h), "%02X", ch.last_cw[i]);
                    f << h;
                }
                f << " " << (ch.has_cw ? 1 : 0)
                  << " " << ch.best_server << "\n";
            }
            f << totalSamples_ << " " << totalCwOk_ << " " << totalCwFail_ << " " << cycles_ << "\n";
            return true;
        } catch (...) { return false; }
    }

    bool load(const std::string& path = "") {
        std::string fp = path.empty() ? defaultPath() : path;
        std::lock_guard<std::mutex> g(mu_);
        try {
            std::ifstream f(fp);
            if (!f) return false;
            std::string hdr;
            std::getline(f, hdr);
            if (hdr != "SCAN1") return false;
            size_t n; f >> n;
            // Only load stats into existing channels (don't replace queue)
            std::map<int, size_t> idxMap; // service_index -> position in scanQueue_
            for (size_t i = 0; i < scanQueue_.size(); i++)
                idxMap[scanQueue_[i].service_index] = i;
            for (size_t i = 0; i < n; i++) {
                int si; std::string sid; int sc, er, co, cf; float lat;
                int sq; uint16_t caid; uint32_t provid; uint16_t sid2;
                std::string cwHex; int hasCw; std::string best;
                f >> si >> sid >> sc >> er >> co >> cf >> lat >> sq
                  >> caid >> provid >> sid2 >> cwHex >> hasCw >> best;
                auto it = idxMap.find(si);
                if (it != idxMap.end()) {
                    auto& ch = scanQueue_[it->second];
                    ch.scan_count = sc;
                    ch.ecm_received = er;
                    ch.cw_obtained = co;
                    ch.cw_failed = cf;
                    ch.avg_latency_ms = lat;
                    ch.signal_quality = (ScannedChannel::Quality)sq;
                    ch.caid = caid;
                    ch.provid = provid;
                    ch.sid = sid2;
                    ch.has_cw = (hasCw != 0);
                    ch.best_server = best;
                    for (int j = 0; j < 16 && j * 2 + 1 < (int)cwHex.size(); j++) {
                        unsigned b; sscanf(cwHex.c_str() + j * 2, "%02x", &b);
                        ch.last_cw[j] = (uint8_t)b;
                    }
                }
            }
            f >> totalSamples_ >> totalCwOk_ >> totalCwFail_ >> cycles_;
            return true;
        } catch (...) { return false; }
    }

private:
    mutable std::mutex mu_;
    std::thread thread_;
    std::vector<ScannedChannel> scanQueue_;
    std::vector<int> order_;      // shuffled indices into scanQueue_
    int  currentIdx_ = -1;
    int  scanPos_ = 0;          // position in current cycle
    bool gotCwThisDwell_ = false;
    int  totalSamples_ = 0;
    int  totalCwOk_ = 0;
    int  totalCwFail_ = 0;
    int  cycles_ = 0;
    time_t startedAt_ = 0;
    std::mt19937 rng_{std::random_device{}()};

    void lg(const std::string& s) { if (onLog) onLog("[Scanner] " + s); }

    void shuffleOrderLocked() {
        if (order_.size() > 1) {
            std::shuffle(order_.begin(), order_.end(), rng_);
        }
    }

    static std::string defaultPath() {
#ifdef _WIN32
        char buf[260];
        GetModuleFileNameA(nullptr, buf, 260);
        std::string p(buf);
        size_t pos = p.rfind('\\');
        if (pos != std::string::npos) p = p.substr(0, pos + 1);
        return p + "gmscreen_scanner.dat";
#else
        return "gmscreen_scanner.dat";
#endif
    }

    int nextScanIndex() {
        // Find next channel to scan in a shuffled order (skip NoSignal channels if configured).
        int attempts = 0;
        int qSize = (int)scanQueue_.size();
        if (qSize == 0) return -1;
        if ((int)order_.size() != qSize) {
            order_.clear();
            order_.reserve(qSize);
            for (int i = 0; i < qSize; i++) order_.push_back(i);
            shuffleOrderLocked();
            scanPos_ = 0;
        }

        while (attempts < qSize) {
            if (scanPos_ == 0) {
                shuffleOrderLocked();
            }
            int ordIdx = scanPos_;
            int idx = order_[ordIdx];
            scanPos_++;
            if (scanPos_ >= qSize) {
                scanPos_ = 0;
                cycles_++;
            }

            auto& ch = scanQueue_[idx];

            // Skip NoSignal channels (too many consecutive failures)
            if (skip_poor_signal &&
                ch.signal_quality == ScannedChannel::Quality::NoSignal &&
                ch.scan_count >= poor_signal_threshold)
            {
                attempts++;
                continue;
            }
            return idx;
        }
        return -1; // all channels are NoSignal
    }

    void loop() {
        lg("Started — dwell=" + std::to_string(dwell_time_sec) + "s, timeout=" +
           std::to_string(signal_timeout_sec) + "s");
        startedAt_ = time(nullptr);

        while (running) {
            // Wait while paused
            while (paused && running) {
                std::this_thread::sleep_for(std::chrono::milliseconds(200));
            }
            if (!running) break;

            // Pick next channel
            int idx;
            {
                std::lock_guard<std::mutex> g(mu_);
                idx = nextScanIndex();
                if (idx < 0) {
                    lg("No scannable channels — all NoSignal or queue empty");
                    std::this_thread::sleep_for(std::chrono::seconds(10));
                    continue;
                }
                currentIdx_ = idx;
                gotCwThisDwell_ = false;
            }

            // Get channel info
            std::string svcId, svcName;
            int svcIdx;
            bool isRadio;
            {
                std::lock_guard<std::mutex> g(mu_);
                auto& ch = scanQueue_[idx];
                svcId = ch.service_id;
                svcName = ch.service_name;
                svcIdx = ch.service_index;
                isRadio = ch.is_radio;
                ch.scan_count++;
                ch.last_scan = time(nullptr);
                if (ch.first_scan == 0) ch.first_scan = ch.last_scan;
            }

            // Switch STB to this channel
            bool switched = false;
            if (onSwitchChannel) {
                try {
                    switched = onSwitchChannel(svcId, isRadio ? 1 : 0);
                } catch (...) {}
            }

            if (!switched) {
                lg("Failed to switch to " + svcName + " (idx=" + std::to_string(svcIdx) + ")");
                std::this_thread::sleep_for(std::chrono::seconds(2));
                continue;
            }

            lg("Scanning: " + svcName + " (idx=" + std::to_string(svcIdx) + ")");

            // Dwell on this channel for dwell_time_sec
            auto dwellStart = std::chrono::steady_clock::now();
            auto signalDeadline = dwellStart + std::chrono::seconds(signal_timeout_sec);
            bool signalChecked = false;

            while (running && !paused) {
                auto now = std::chrono::steady_clock::now();
                float elapsed = std::chrono::duration<float>(now - dwellStart).count();

                // Check signal timeout
                if (!signalChecked && now >= signalDeadline) {
                    signalChecked = true;
                    std::lock_guard<std::mutex> g(mu_);
                    auto& ch = scanQueue_[idx];
                    if (!gotCwThisDwell_ && ch.ecm_received == 0) {
                        // No ECM at all — likely no signal or not encrypted on this transponder
                        if (ch.signal_quality != ScannedChannel::Quality::Good) {
                            int poorCount = 0;
                            for (int s = 0; s < ch.scan_count; s++)
                                if (ch.cw_obtained == 0) poorCount++;
                            if (poorCount >= poor_signal_threshold)
                                ch.signal_quality = ScannedChannel::Quality::NoSignal;
                            else
                                ch.signal_quality = ScannedChannel::Quality::Poor;
                        }
                    } else if (!gotCwThisDwell_) {
                        // Got ECM but no CW — server issue, not signal issue
                        ch.signal_quality = ScannedChannel::Quality::Poor;
                    }
                }

                // Dwell complete?
                if (elapsed >= (float)dwell_time_sec) break;

                std::this_thread::sleep_for(std::chrono::milliseconds(250));
            }
        }

        // Save on stop
        try { save(); } catch (...) {}
        lg("Stopped — " + std::to_string(totalSamples_) + " samples collected");
    }
};

// ═══════════════════════════════════════════════════════════════════════════
//  TURBO PIPELINE — unified sub-3s ECM response
// ═══════════════════════════════════════════════════════════════════════════
//
// This is integrated directly into CccamServer's ECM processing.
// The TurboPipeline struct holds configuration and timing stats.
//
struct TurboPipeline {
    // ── Configuration ──
    float max_response_sec = 3.0f;       // maximum time to respond to ECM
    float prefetch_lead_sec = 1.5f;      // request CW this far before expected rotation
    bool  parallel_lookup = true;        // try cache + CA engine + offline DB simultaneously
    bool  smart_routing = true;          // use CA engine's smart server ordering
    bool  prefetch_enabled = true;       // pre-fetch CW before rotation

    // ── Stats ──
    struct Stats {
        int  total_ecm = 0;
        int  cache_hits = 0;
        int  ca_predict_hits = 0;
        int  offline_hits = 0;
        int  upstream_hits = 0;
        int  ai_hits = 0;
        int  timeouts = 0;               // responses > max_response_sec
        int  prefetch_triggered = 0;
        int  prefetch_saved = 0;          // prefetch that avoided a timeout
        float avg_response_ms = 0;
        float p95_response_ms = 0;
        float fastest_ms = 99999;
        float slowest_ms = 0;

        float cacheRate() const { return total_ecm > 0 ? (float)cache_hits / total_ecm * 100 : 0; }
        float successRate() const {
            int ok = cache_hits + ca_predict_hits + offline_hits + upstream_hits + ai_hits;
            return total_ecm > 0 ? (float)ok / total_ecm * 100 : 0;
        }
    };

    std::atomic<int> totalEcm{0};
    std::atomic<int> cacheHits{0};
    std::atomic<int> caPredictHits{0};
    std::atomic<int> offlineHits{0};
    std::atomic<int> upstreamHits{0};
    std::atomic<int> aiHits{0};
    std::atomic<int> timeouts{0};
    std::atomic<int> prefetchTriggered{0};
    std::atomic<int> prefetchSaved{0};

    // Latency tracking (EMA)
    std::atomic<int> latencySumUs{0};    // sum in microseconds for avg calc
    std::atomic<int> latencyCount{0};

    void recordLatency(float ms) {
        latencySumUs += (int)(ms * 1000);
        latencyCount++;
        if (ms >= max_response_sec * 1000) timeouts++;
    }

    Stats getStats() const {
        Stats s;
        s.total_ecm = totalEcm.load();
        s.cache_hits = cacheHits.load();
        s.ca_predict_hits = caPredictHits.load();
        s.offline_hits = offlineHits.load();
        s.upstream_hits = upstreamHits.load();
        s.ai_hits = aiHits.load();
        s.timeouts = timeouts.load();
        s.prefetch_triggered = prefetchTriggered.load();
        s.prefetch_saved = prefetchSaved.load();
        int cnt = latencyCount.load();
        if (cnt > 0)
            s.avg_response_ms = (float)latencySumUs.load() / cnt / 1000.0f;
        return s;
    }

    void reset() {
        totalEcm = 0; cacheHits = 0; caPredictHits = 0;
        offlineHits = 0; upstreamHits = 0; aiHits = 0;
        timeouts = 0; prefetchTriggered = 0; prefetchSaved = 0;
        latencySumUs = 0; latencyCount = 0;
    }
};

} // namespace cccam
