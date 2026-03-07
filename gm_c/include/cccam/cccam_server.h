#pragma once
// CCcam 2.x protocol server — proper handshake, RC4-variant cipher, message framing.
// Based on tvheadend/oscam CCcam protocol implementation.
// Runs in a dedicated thread.

#include <atomic>
#include <functional>
#include <string>
#include <thread>
#include <vector>
#include <mutex>
#include <array>
#include <cstring>
#include <cstdlib>
#include <ctime>
#include <algorithm>
#include <chrono>
#include <fstream>
#include <numeric>
#include "stb/crash_log.h"
#include "cccam/cccam_config.h"
#include "cccam/cccam_learner.h"
#include "ai/cw_predictor.h"
#include "cccam/ca_engine.h"
#include "cccam/channel_scanner.h"

#ifdef _WIN32
#  ifndef WIN32_LEAN_AND_MEAN
#    define WIN32_LEAN_AND_MEAN
#  endif
#  ifndef NOMINMAX
#    define NOMINMAX
#  endif
#  include <winsock2.h>
#  include <ws2tcpip.h>
#  include <windows.h>
   using SockT = SOCKET;
#  define INVALID_SOCK INVALID_SOCKET
#  define CLOSE_SOCK(s) closesocket(s)
#else
#  include <sys/socket.h>
#  include <netinet/in.h>
#  include <netinet/tcp.h>
#  include <arpa/inet.h>
#  include <unistd.h>
   using SockT = int;
#  define INVALID_SOCK (-1)
#  define CLOSE_SOCK(s) close(s)
#endif

namespace cccam {

// ── SHA1 ────────────────────────────────────────────────────────────────────
inline void sha1(const uint8_t* msg, size_t len, uint8_t out[20]) {
    uint32_t h0=0x67452301,h1=0xEFCDAB89,h2=0x98BADCFE,h3=0x10325476,h4=0xC3D2E1F0;
    std::vector<uint8_t> m(msg,msg+len);
    uint64_t bits=(uint64_t)len*8;
    m.push_back(0x80);
    while(m.size()%64!=56) m.push_back(0);
    for(int i=7;i>=0;i--) m.push_back((uint8_t)(bits>>(i*8)));
    for(size_t off=0;off<m.size();off+=64){
        uint32_t w[80];
        for(int j=0;j<16;j++) w[j]=(m[off+j*4]<<24)|(m[off+j*4+1]<<16)|(m[off+j*4+2]<<8)|m[off+j*4+3];
        for(int j=16;j<80;j++){uint32_t t=w[j-3]^w[j-8]^w[j-14]^w[j-16];w[j]=(t<<1)|(t>>31);}
        uint32_t a=h0,b=h1,c=h2,d=h3,e=h4;
        for(int j=0;j<80;j++){
            uint32_t f,k;
            if(j<20){f=(b&c)|((~b)&d);k=0x5A827999;}
            else if(j<40){f=b^c^d;k=0x6ED9EBA1;}
            else if(j<60){f=(b&c)|(b&d)|(c&d);k=0x8F1BBCDC;}
            else{f=b^c^d;k=0xCA62C1D6;}
            uint32_t t2=((a<<5)|(a>>27))+f+e+k+w[j];
            e=d;d=c;c=(b<<30)|(b>>2);b=a;a=t2;
        }
        h0+=a;h1+=b;h2+=c;h3+=d;h4+=e;
    }
    for(int i=0;i<4;i++){out[i]=(h0>>(24-i*8))&0xFF;out[4+i]=(h1>>(24-i*8))&0xFF;
        out[8+i]=(h2>>(24-i*8))&0xFF;out[12+i]=(h3>>(24-i*8))&0xFF;out[16+i]=(h4>>(24-i*8))&0xFF;}
}

// ── CCcam RC4-variant cipher (cc_crypt) ─────────────────────────────────────
// This is NOT standard RC4. It has an extra XOR with a rolling 'state' byte.
struct CcCryptBlock {
    uint8_t keytable[256];
    uint8_t state;
    uint8_t counter;
    uint8_t sum;

    void init(const uint8_t* key, int len) {
        for (int i = 0; i < 256; i++) keytable[i] = (uint8_t)i;
        uint8_t j = 0;
        for (int i = 0; i < 256; i++) {
            j += key[i % len] + keytable[i];
            std::swap(keytable[i], keytable[j]);
        }
        state   = key[0];
        counter = 0;
        sum     = 0;
    }

    // cc_decrypt: state is updated with the OUTPUT byte (after XOR)
    void decrypt(uint8_t* data, int len) {
        for (int i = 0; i < len; i++) {
            counter++;
            sum += keytable[counter];
            std::swap(keytable[counter], keytable[sum]);
            uint8_t z = data[i]; // input byte
            data[i] = z ^ keytable[(keytable[counter] + keytable[sum]) & 0xFF] ^ state;
            state ^= data[i];    // use OUTPUT byte
        }
    }

    // cc_encrypt: state is updated with the INPUT byte (before XOR)
    void encrypt(uint8_t* data, int len) {
        for (int i = 0; i < len; i++) {
            counter++;
            sum += keytable[counter];
            std::swap(keytable[counter], keytable[sum]);
            uint8_t z = data[i]; // input byte
            data[i] = z ^ keytable[(keytable[counter] + keytable[sum]) & 0xFF] ^ state;
            state ^= z;          // use INPUT byte
        }
    }
};

static const uint8_t CCCAM_STR[] = "CCcam";

// XOR first 6 bytes with "CCcam", then fill bytes 8-15 with i*buf[i]
inline void cc_crypt_xor(uint8_t* buf) {
    for (int i = 0; i < 8; i++) {
        buf[i + 8] = (uint8_t)(i * buf[i]);
        if (i <= 5) buf[i] ^= CCCAM_STR[i];
    }
}

// ── CCcam message types ─────────────────────────────────────────────────────
enum CcMsg : uint8_t {
    MSG_CLI_DATA      = 0x00,
    MSG_ECM_REQUEST   = 0x01,
    MSG_EMM_REQUEST   = 0x02,
    MSG_CARD_REMOVED  = 0x04,
    MSG_CMD_05        = 0x05,
    MSG_KEEPALIVE     = 0x06,
    MSG_NEW_CARD      = 0x07,
    MSG_SRV_DATA      = 0x08,
    MSG_ECM_NOK1      = 0xFE,
    MSG_ECM_NOK2      = 0xFF,
    MSG_NO_HEADER     = 0xF0  // internal: raw send, no 4-byte header
};

// ── TCP ping — test if host:port is reachable, returns ms or -1 ─────────────
// Uses non-blocking connect + select for reliable timeout on all platforms.
inline int tcpPing(const std::string& host, int port,
                   const std::string& pxHost = "", int pxPort = 0, int timeoutMs = 3000)
{
    auto t0 = std::chrono::steady_clock::now();
    SockT sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == INVALID_SOCK) return -1;

    // Determine target to connect to (proxy or direct)
    std::string cHost = (!pxHost.empty() && pxPort > 0) ? pxHost : host;
    int cPort = (!pxHost.empty() && pxPort > 0) ? pxPort : port;

    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_port = htons((uint16_t)cPort);
    if (inet_pton(AF_INET, cHost.c_str(), &addr.sin_addr) != 1) {
        struct addrinfo hints{}, *res = nullptr;
        hints.ai_family = AF_INET; hints.ai_socktype = SOCK_STREAM;
        if (getaddrinfo(cHost.c_str(), nullptr, &hints, &res) != 0 || !res) {
            CLOSE_SOCK(sock); return -1;
        }
        addr.sin_addr = ((sockaddr_in*)res->ai_addr)->sin_addr;
        freeaddrinfo(res);
    }

    // Set non-blocking
#ifdef _WIN32
    u_long nb = 1;
    ioctlsocket(sock, FIONBIO, &nb);
#else
    int flags = fcntl(sock, F_GETFL, 0);
    fcntl(sock, F_SETFL, flags | O_NONBLOCK);
#endif

    int cr = ::connect(sock, (sockaddr*)&addr, sizeof(addr));
    if (cr < 0) {
#ifdef _WIN32
        if (WSAGetLastError() != WSAEWOULDBLOCK) { CLOSE_SOCK(sock); return -1; }
#else
        if (errno != EINPROGRESS) { CLOSE_SOCK(sock); return -1; }
#endif
        fd_set wset, eset;
        FD_ZERO(&wset); FD_SET(sock, &wset);
        FD_ZERO(&eset); FD_SET(sock, &eset);
        timeval tv;
        tv.tv_sec = timeoutMs / 1000;
        tv.tv_usec = (timeoutMs % 1000) * 1000;
        int sr = select((int)sock + 1, nullptr, &wset, &eset, &tv);
        if (sr <= 0 || FD_ISSET(sock, &eset)) { CLOSE_SOCK(sock); return -1; }
        // Check SO_ERROR
        int serr = 0;
        socklen_t slen = sizeof(serr);
        getsockopt(sock, SOL_SOCKET, SO_ERROR, (char*)&serr, &slen);
        if (serr != 0) { CLOSE_SOCK(sock); return -1; }
    }

    CLOSE_SOCK(sock);
    auto t1 = std::chrono::steady_clock::now();
    return (int)std::chrono::duration_cast<std::chrono::milliseconds>(t1 - t0).count();
}

// ── Full CCcam handshake test ─────────────────────────────────────────────────
// Performs the complete CCcam 2.x client handshake: TCP connect (or SOCKS5),
// receive seed, derive ciphers, send hash+user+pass+CCcam, receive ACK,
// then drain card announcements.
// Returns: 0=OK, -1=TCP/SOCKS5 fail, -2=no seed (wrong protocol/port),
//          -3=auth fail (handshake error or bad ACK).
// cardsOut is set to the number of NEW_CARD messages received on success.
inline int cccamFullTest(const UpstreamServer& srv, int& cardsOut, int timeoutMs = 5000)
{
    cardsOut = 0;
    SockT sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == INVALID_SOCK) return -1;

    // Set socket send/recv timeout
    auto setTo = [&](int ms) {
#ifdef _WIN32
        DWORD t = (DWORD)ms;
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&t, sizeof(t));
        setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, (char*)&t, sizeof(t));
#else
        timeval t; t.tv_sec = ms/1000; t.tv_usec = (ms%1000)*1000;
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &t, sizeof(t));
        setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, &t, sizeof(t));
#endif
    };
    setTo(timeoutMs);

    auto sa = [&](const uint8_t* d, int n) -> bool {
        int s = 0;
        while (s < n) { int r = send(sock,(const char*)d+s,n-s,0); if(r<=0) return false; s+=r; }
        return true;
    };
    auto ra = [&](uint8_t* d, int n) -> bool {
        int g = 0;
        while (g < n) { int r = recv(sock,(char*)d+g,n-g,0); if(r<=0) return false; g+=r; }
        return true;
    };
    auto fail = [&](int code) -> int { CLOSE_SOCK(sock); return code; };

    // ── Connect (direct or via SOCKS5) ────────────────────────────────────────
    if (srv.hasProxy()) {
        sockaddr_in pa{}; pa.sin_family = AF_INET;
        pa.sin_port = htons((uint16_t)srv.proxy_port);
        if (inet_pton(AF_INET, srv.proxy_host.c_str(), &pa.sin_addr) != 1) {
            struct addrinfo hints{}, *res = nullptr;
            hints.ai_family = AF_INET; hints.ai_socktype = SOCK_STREAM;
            if (getaddrinfo(srv.proxy_host.c_str(), nullptr, &hints, &res) != 0 || !res)
                return fail(-1);
            pa.sin_addr = ((sockaddr_in*)res->ai_addr)->sin_addr;
            freeaddrinfo(res);
        }
        if (::connect(sock, (sockaddr*)&pa, sizeof(pa)) < 0) return fail(-1);
        // SOCKS5 negotiation
        bool hasA = !srv.proxy_user.empty();
        uint8_t greet[4] = {0x05, (uint8_t)(hasA?2:1), 0x00, 0x02};
        if (!sa(greet, hasA?4:3)) return fail(-1);
        uint8_t gresp[2]; if (!ra(gresp,2) || gresp[0]!=0x05) return fail(-1);
        if (gresp[1]==0x02 && hasA) {
            std::vector<uint8_t> auth; auth.push_back(0x01);
            auth.push_back((uint8_t)srv.proxy_user.size());
            auth.insert(auth.end(), srv.proxy_user.begin(), srv.proxy_user.end());
            auth.push_back((uint8_t)srv.proxy_pass.size());
            auth.insert(auth.end(), srv.proxy_pass.begin(), srv.proxy_pass.end());
            if (!sa(auth.data(),(int)auth.size())) return fail(-1);
            uint8_t ar[2]; if (!ra(ar,2) || ar[1]!=0x00) return fail(-1);
        } else if (gresp[1]!=0x00) return fail(-1);
        // SOCKS5 CONNECT (hostname)
        std::vector<uint8_t> req;
        req.push_back(0x05); req.push_back(0x01); req.push_back(0x00);
        req.push_back(0x03); req.push_back((uint8_t)srv.host.size());
        req.insert(req.end(), srv.host.begin(), srv.host.end());
        req.push_back((uint8_t)(srv.port>>8)); req.push_back((uint8_t)(srv.port&0xFF));
        if (!sa(req.data(),(int)req.size())) return fail(-1);
        uint8_t rr[10]; if (!ra(rr,4) || rr[1]!=0x00) return fail(-1);
        if      (rr[3]==0x01) { uint8_t d[6]; ra(d,6); }
        else if (rr[3]==0x03) { uint8_t dl; ra(&dl,1); std::vector<uint8_t> d(dl+2); ra(d.data(),(int)d.size()); }
        else if (rr[3]==0x04) { uint8_t d[18]; ra(d,18); }
    } else {
        sockaddr_in addr{}; addr.sin_family = AF_INET;
        addr.sin_port = htons((uint16_t)srv.port);
        if (inet_pton(AF_INET, srv.host.c_str(), &addr.sin_addr) != 1) {
            struct addrinfo hints{}, *res = nullptr;
            hints.ai_family = AF_INET; hints.ai_socktype = SOCK_STREAM;
            if (getaddrinfo(srv.host.c_str(), nullptr, &hints, &res) != 0 || !res)
                return fail(-1);
            addr.sin_addr = ((sockaddr_in*)res->ai_addr)->sin_addr;
            freeaddrinfo(res);
        }
        if (::connect(sock, (sockaddr*)&addr, sizeof(addr)) < 0) return fail(-1);
    }

    // ── CCcam client handshake ────────────────────────────────────────────────
    uint8_t seed[16]; if (!ra(seed,16)) return fail(-2);  // no seed = wrong protocol/port

    uint8_t cdata[16]; memcpy(cdata, seed, 16);
    cc_crypt_xor(cdata);
    uint8_t chash[20]; sha1(cdata, 16, chash);
    CcCryptBlock sb{}, rb{};
    rb.init(chash,20); rb.decrypt(cdata,16);
    sb.init(cdata,16); sb.decrypt(chash,20);

    uint8_t hc[20]; memcpy(hc,chash,20); sb.encrypt(hc,20);
    if (!sa(hc,20)) return fail(-3);

    uint8_t ub[20]={};
    memcpy(ub, srv.user.data(), std::min(srv.user.size(), (size_t)19));
    sb.encrypt(ub,20); if (!sa(ub,20)) return fail(-3);

    if (!srv.pass.empty()) {
        std::vector<uint8_t> pd(srv.pass.begin(), srv.pass.end());
        sb.encrypt(pd.data(),(int)pd.size());
    }
    uint8_t cc6[6]={'C','C','c','a','m',0}; sb.encrypt(cc6,6);
    if (!sa(cc6,6)) return fail(-3);

    uint8_t ack[20]; if (!ra(ack,20)) return fail(-3);
    rb.decrypt(ack,20);
    if (memcmp(ack,"CCcam",5)!=0) return fail(-3);

    // ── Auth OK — send CLI_DATA and drain card announcements (2s window) ──────
    uint8_t cliHdr[4]={0,0x00,0,0}; sb.encrypt(cliHdr,4); sa(cliHdr,4);
    setTo(2000);
    for (int att = 0; att < 300; att++) {
        uint8_t uh[4]; if (!ra(uh,4)) break;
        rb.decrypt(uh,4);
        uint16_t mlen = (uint16_t)((uh[2]<<8)|uh[3]);
        if (mlen > 2048) break;
        std::vector<uint8_t> um(mlen);
        if (mlen>0 && !ra(um.data(),mlen)) break;
        if (mlen>0) rb.decrypt(um.data(),mlen);
        if      (uh[1]==0x07) cardsOut++;                           // MSG_NEW_CARD
        else if (uh[1]==0x06) {                                     // MSG_KEEPALIVE
            uint8_t ka[4]={0,0x06,0,0}; sb.encrypt(ka,4); sa(ka,4);
        }
        else if (uh[1]==0x08||uh[1]==0x00||uh[1]==0x04||uh[1]==0x05) {} // ok to skip
        else break;
    }
    CLOSE_SOCK(sock);
    return 0;
}

// ── Server ──────────────────────────────────────────────────────────────────
class CccamServer {
public:
    using LogCb = std::function<void(const std::string&)>;

    struct Config {
        int  port = 8000;
        std::string user = "a";
        std::string pass = "a";
        bool log_ecm = true;
        std::string global_proxy_host;
        int         global_proxy_port = 0;
        std::string global_proxy_user;
        std::string global_proxy_pass;
        std::vector<UpstreamServer> servers;
        bool hasUpstream() const {
            for (auto& s : servers) if (s.enabled && s.valid()) return true;
            return false;
        }
        void applyGlobalProxy() {
            if (global_proxy_host.empty() || global_proxy_port <= 0) return;
            for (auto& s : servers) {
                if (s.proxy_host.empty() || s.proxy_port <= 0) {
                    s.proxy_host = global_proxy_host;
                    s.proxy_port = global_proxy_port;
                    s.proxy_user = global_proxy_user;
                    s.proxy_pass = global_proxy_pass;
                }
            }
        }
    };

    Config              cfg;
    CwLearner           learner;
    std::atomic<bool>   running{false};
    std::atomic<int>    clients{0};
    std::atomic<int>    ecmTotal{0};
    std::atomic<int>    ecmOk{0};
    std::atomic<int>    ecmFail{0};
    std::atomic<int>    ecmCacheHits{0};
    
    // AI-powered CW prediction engine
    ai::CwPredictor aiPredictor;
    bool aiEnabled = true;  // Enable AI-assisted decryption

    // Multi-CA decryption engine (CW prediction, smart routing, BISS, PowerVu)
    CaEngine caEngine;

    // Turbo pipeline stats (sub-3s ECM response)
    TurboPipeline turbo;

    // Background service callbacks (wired by App)
    std::function<void(uint16_t caid,uint32_t provid,uint16_t sid,
                       const uint8_t* ecmMsg,int ecmMsgLen)> onEcmCapture;
    std::function<bool(uint16_t caid,uint16_t sid,
                       const uint8_t* ecm,int ecmLen,uint8_t cwOut[16])> onOfflineLookup;

    // Scanner notification: called when CW obtained, so scanner knows signal quality
    std::function<void(uint16_t caid,uint32_t provid,uint16_t sid,
                       bool ok,const uint8_t* cw,float latMs,
                       const std::string& server)> onScannerNotify;

    std::string getStatus() {
        std::lock_guard<std::mutex> g(mu_);
        return status_;
    }

    void start(LogCb cb) {
        if (running) return;
        log_ = cb;
        running = true;
        learner.load();
        aiPredictor.load();
        aiPredictor.onLog = [this](const std::string& msg) { lg(msg); };
        if (aiEnabled) aiPredictor.startOllamaAnalyzer();
        setStatus("Starting on port " + std::to_string(cfg.port));
        thread_ = std::thread(&CccamServer::serverLoop, this);
    }

    void stop() {
        if (!running) return;
        running = false;
        aiPredictor.stopOllamaAnalyzer();
        learner.save();
        aiPredictor.save();
        SockT tmp = socket(AF_INET, SOCK_STREAM, 0);
        if (tmp != INVALID_SOCK) {
            sockaddr_in a{}; a.sin_family=AF_INET; a.sin_port=htons((uint16_t)cfg.port);
            inet_pton(AF_INET,"127.0.0.1",&a.sin_addr);
            ::connect(tmp,(sockaddr*)&a,sizeof(a));
            CLOSE_SOCK(tmp);
        }
        if (thread_.joinable()) thread_.join();
        setStatus("Stopped");
    }

    ~CccamServer() { stop(); }

private:
    std::thread thread_;
    LogCb       log_;
    std::mutex  mu_;
    std::string status_ = "Stopped";
    std::mutex  ecmLogMu_;

    // ── Per-upstream connection state ────────────────────────────────────────
    struct UpConn {
        UpstreamServer* srv = nullptr;  // non-const: updated with ping_status after connect
        SockT        sock = INVALID_SOCK;
        CcCryptBlock sendBlock{};
        CcCryptBlock recvBlock{};
        bool         connected = false;
        int          cardCount = 0;
        std::string  label;           // "name(host:port)"
        std::string  failReason;      // why connectUpstream failed (for test_detail)
    };

    void setStatus(const std::string& s) {
        std::lock_guard<std::mutex> g(mu_);
        status_ = s;
    }
    void lg(const std::string& s) {
        std::lock_guard<std::mutex> g(mu_);
        status_ = s;
        if (log_) log_("[CCcam] " + s);
        stb::CrashLog(("[CCcam] " + s).c_str());
    }

    // ── Socket helpers ──────────────────────────────────────────────────────
    static int sendAll(SockT s, const uint8_t* d, int len) {
        int sent=0;
        while(sent<len) {
            int r=send(s,(const char*)d+sent,len-sent,0);
            if(r<=0) return sent;
            sent+=r;
        }
        return sent;
    }
    static int recvAllT(SockT s, uint8_t* d, int len, bool* wasTimeout, int timeoutSec = 10) {
        if (wasTimeout) *wasTimeout = false;
        int got=0;
        while(got<len) {
            fd_set fds; FD_ZERO(&fds); FD_SET(s, &fds);
            timeval tv; tv.tv_sec = timeoutSec; tv.tv_usec = 0;
            int sel = select((int)s + 1, &fds, nullptr, nullptr, &tv);
            if (sel == 0) { if (wasTimeout) *wasTimeout = true; return got; }
            if (sel < 0) return got;
            int r=recv(s,(char*)d+got,len-got,0);
            if(r<=0) return got;
            got+=r;
        }
        return got;
    }
    // Legacy wrapper (for handshake code that doesn't need timeout info)
    static int recvAll(SockT s, uint8_t* d, int len) {
        return recvAllT(s, d, len, nullptr, 10);
    }

    // Send encrypted CCcam message with 4-byte header [seq, cmd, len_hi, len_lo]
    bool ccSend(SockT sock, CcCryptBlock& sendBlock, uint8_t cmd,
                const uint8_t* payload, uint16_t len, uint8_t seq = 0) {
        std::vector<uint8_t> buf(4 + len);
        buf[0] = seq;
        buf[1] = cmd;
        buf[2] = (uint8_t)(len >> 8);
        buf[3] = (uint8_t)(len & 0xFF);
        if (payload && len > 0)
            memcpy(buf.data() + 4, payload, len);
        sendBlock.encrypt(buf.data(), (int)buf.size());
        return sendAll(sock, buf.data(), (int)buf.size()) == (int)buf.size();
    }

    // Send raw encrypted data (no 4-byte header — used during handshake)
    bool ccSendRaw(SockT sock, CcCryptBlock& sendBlock, uint8_t* data, int len) {
        sendBlock.encrypt(data, len);
        return sendAll(sock, data, len) == len;
    }

    // Receive and decrypt a CCcam message. Returns total bytes (header+payload).
    // On success: hdr[0]=seq, hdr[1]=cmd, hdr[2..3]=len; payload in outBuf.
    // Returns -1 on error/timeout (cipher state preserved on header read failure).
    // wasTimeout is set to true if the failure was a select timeout (not a real error).
    static int ccRecv(SockT sock, CcCryptBlock& recvBlock, uint8_t hdr[4],
               uint8_t* outBuf, int outBufSize, bool* wasTimeout = nullptr) {
        if (wasTimeout) *wasTimeout = false;
        // Save cipher state before reading header, in case of timeout
        CcCryptBlock saved = recvBlock;
        bool hdrTimeout = false;
        if (recvAllT(sock, hdr, 4, &hdrTimeout) != 4) {
            recvBlock = saved; // restore on failure
            if (wasTimeout) *wasTimeout = hdrTimeout;
            return -1;
        }
        recvBlock.decrypt(hdr, 4);
        uint16_t msgLen = (uint16_t)((hdr[2] << 8) | hdr[3]);
        if (msgLen > outBufSize) return -1;
        if (msgLen > 0) {
            if (recvAllT(sock, outBuf, msgLen, nullptr) != msgLen) return -1;
            recvBlock.decrypt(outBuf, msgLen);
        }
        return 4 + msgLen;
    }

    static std::string hexU16(uint16_t v) {
        char buf[8]; snprintf(buf,sizeof(buf),"%04X",v); return buf;
    }
    static std::string hexU32(uint32_t v) {
        char buf[12]; snprintf(buf,sizeof(buf),"%08X",v); return buf;
    }

    // ── Server accept loop ──────────────────────────────────────────────────
    void serverLoop() {
#ifdef _WIN32
        WSADATA w; WSAStartup(MAKEWORD(2,2),&w);
#endif
        SockT srv = socket(AF_INET, SOCK_STREAM, 0);
        if (srv == INVALID_SOCK) { lg("Socket error"); running=false; return; }
        int opt=1;
        setsockopt(srv, SOL_SOCKET, SO_REUSEADDR,(char*)&opt,sizeof(opt));
        sockaddr_in addr{}; addr.sin_family=AF_INET;
        addr.sin_port=htons((uint16_t)cfg.port); addr.sin_addr.s_addr=INADDR_ANY;
        if (bind(srv,(sockaddr*)&addr,sizeof(addr))<0) {
            lg("Bind failed port " + std::to_string(cfg.port));
            CLOSE_SOCK(srv); running=false; return;
        }
        listen(srv, 8);
        lg("Listening on port " + std::to_string(cfg.port));

#ifdef _WIN32
        DWORD tv=500; setsockopt(srv,SOL_SOCKET,SO_RCVTIMEO,(char*)&tv,sizeof(tv));
#else
        timeval tv{0,500000}; setsockopt(srv,SOL_SOCKET,SO_RCVTIMEO,&tv,sizeof(tv));
#endif
        while (running) {
            sockaddr_in cli{};
#ifdef _WIN32
            int clen=sizeof(cli);
#else
            socklen_t clen=sizeof(cli);
#endif
            SockT cs = accept(srv,(sockaddr*)&cli,&clen);
            if (cs == INVALID_SOCK) continue;
            if (!running) { CLOSE_SOCK(cs); break; }
            clients++;
            char ipb[INET_ADDRSTRLEN]={};
            inet_ntop(AF_INET,&cli.sin_addr,ipb,sizeof(ipb));
            lg("Client " + std::string(ipb) + " connected (" +
               std::to_string(clients.load()) + " total)");
            std::thread(&CccamServer::handleClient, this, cs, std::string(ipb)).detach();
        }
        CLOSE_SOCK(srv);
        running=false;
        lg("Server stopped");
    }

    // ── Per-client handler ──────────────────────────────────────────────────
    void handleClient(SockT sock, std::string ip) {
        auto done = [&]{
            CLOSE_SOCK(sock); clients--;
            lg("Client " + ip + " disconnected");
        };

        // Enable TCP keepalive + disable Nagle
        {
            int one = 1;
            setsockopt(sock, SOL_SOCKET, SO_KEEPALIVE, (char*)&one, sizeof(one));
            setsockopt(sock, IPPROTO_TCP, TCP_NODELAY, (char*)&one, sizeof(one));
        }

        CcCryptBlock sendBlock{}, recvBlock{};

        // ══════════════════════════════════════════════════════════════════
        // HANDSHAKE — server side of CCcam 2.x protocol
        // ══════════════════════════════════════════════════════════════════
        //
        // Step 1: Generate and send 16 random bytes as seed
        uint8_t seed[16];
        srand((unsigned)time(nullptr) ^ (unsigned)(uintptr_t)&sock);
        for (auto& b : seed) b = (uint8_t)(rand() & 0xFF);
        // Make sure first byte != 0 to avoid ambiguity
        if (seed[0] == 0) seed[0] = 0x42;
        if (sendAll(sock, seed, 16) != 16) { done(); return; }

        // Step 2: Server-side handshake — exact copy of multics srv-cccam.c
        //
        // Reference: https://github.com/multi-cs/multics/blob/master/srv-cccam.c
        //   cc_crypt_xor(data);
        //   SHA1(data,16) → buf
        //   cc_crypt_init(&sendblock, buf, 20);
        //   cc_decrypt(&sendblock, data, 16);
        //   cc_crypt_init(&recvblock, data, 16);
        //   cc_decrypt(&recvblock, buf, 20);
        //   memcpy(usr, buf, 20);  // save expected hash
        //   recv(20) → buf; cc_decrypt(&recvblock, buf, 20); compare with usr

        uint8_t data[16];
        memcpy(data, seed, 16);

        // XOR transform
        cc_crypt_xor(data);

        // SHA1 hash
        uint8_t buf[20];
        sha1(data, 16, buf);

        // Init server sendblock with hash, decrypt data with it
        sendBlock.init(buf, 20);
        sendBlock.decrypt(data, 16);

        // Init server recvblock with decrypted data, decrypt hash with it
        recvBlock.init(data, 16);
        recvBlock.decrypt(buf, 20);

        // Save expected hash (buf is now modified by decrypt)
        uint8_t expectedHash[20];
        memcpy(expectedHash, buf, 20);

        // Receive 20-byte hash from client
        uint8_t clientHash[20];
        if (recvAll(sock, clientHash, 20) != 20) { done(); return; }

        // Decrypt received hash with recvblock and compare
        recvBlock.decrypt(clientHash, 20);
        if (memcmp(clientHash, expectedHash, 20) != 0) {
            lg("Client " + ip + " handshake FAILED");
            done(); return;
        }

        lg("Client " + ip + " handshake OK");

        // Step 3: Receive encrypted username (20 bytes)
        uint8_t userBuf[20] = {};
        if (recvAll(sock, userBuf, 20) != 20) { done(); return; }
        recvBlock.decrypt(userBuf, 20);
        std::string username((char*)userBuf, strnlen((char*)userBuf, 20));
        lg("Client " + ip + " login user='" + username + "'");

        // Step 4: The client encrypts the password with its sendBlock to advance
        // the cipher state, but does NOT send the encrypted password over the wire.
        // Then it sends "CCcam\0" (6 bytes) encrypted with the advanced sendBlock.
        // Our recvBlock must advance by the same password length to stay in sync.
        {
            size_t pwdLen = cfg.pass.size();
            if (pwdLen > 0) {
                // Advance our recvBlock to match client's sendBlock after password encrypt.
                // Client calls encrypt(password) on its sendBlock. Our recvBlock
                // mirrors that sendBlock, so we call encrypt(password) too.
                std::vector<uint8_t> dummyPwd(pwdLen);
                memcpy(dummyPwd.data(), cfg.pass.data(), pwdLen);
                recvBlock.encrypt(dummyPwd.data(), (int)pwdLen);
            }
        }
        uint8_t cccamBuf[6] = {};
        if (recvAll(sock, cccamBuf, 6) != 6) { done(); return; }
        recvBlock.decrypt(cccamBuf, 6);

        // Verify username and "CCcam" marker
        bool authOk = (username == cfg.user) && (memcmp(cccamBuf, "CCcam", 5) == 0);
        if (!authOk) {
            lg("Client " + ip + " auth check: user='" + username + 
               "' marker='" + std::string((char*)cccamBuf, 5) + "'");
            if (username != cfg.user) {
                lg("Client " + ip + " auth FAILED (bad user)");
                done(); return;
            }
            if (memcmp(cccamBuf, "CCcam", 5) != 0) {
                lg("Client " + ip + " auth FAILED (bad password/marker)");
                done(); return;
            }
        }

        // Step 5: Send encrypted "CCcam\0" ACK (20 bytes)
        uint8_t ackBuf[20] = {};
        memcpy(ackBuf, "CCcam", 5);
        sendBlock.encrypt(ackBuf, 20);
        if (sendAll(sock, ackBuf, 20) != 20) { done(); return; }

        lg("Client " + ip + " authenticated OK");

        // Step 6: Receive MSG_CLI_DATA from client
        uint8_t hdr[4];
        uint8_t msgBuf[1024];
        int r = ccRecv(sock, recvBlock, hdr, msgBuf, sizeof(msgBuf));
        if (r > 0 && hdr[1] == MSG_CLI_DATA) {
            lg("Client " + ip + " CLI_DATA received");
        } else {
            lg("Client " + ip + " expected CLI_DATA but got cmd=0x" + 
               hexU16(hdr[1]) + " r=" + std::to_string(r));
        }

        // Send CLI_DATA ACK (empty, matches multics: cc_msg_send(CC_MSG_CLI_INFO, 0, NULL))
        ccSend(sock, sendBlock, MSG_CLI_DATA, nullptr, 0);

        // Step 7: Send MSG_SRV_DATA (multics cc_sendinfo_cli format, 0x48 bytes)
        // [0-7] nodeid, [8-39] version (32 bytes), [40-71] build (32 bytes)
        {
            static const uint8_t nodeId[8] = {0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08};
            uint8_t srvData[0x48] = {};
            memcpy(srvData, nodeId, 8);
            memcpy(srvData + 8, "2.3.0", 5);
            memcpy(srvData + 40, "1234", 4);
            ccSend(sock, sendBlock, MSG_SRV_DATA, srvData, sizeof(srvData));
        }

        // Small delay before cards (multics: usleep(55000))
        std::this_thread::sleep_for(std::chrono::milliseconds(60));

        // Step 8: Announce cards — format matches multics cc_sendcard_cli exactly
        // [0-3] remote_id, [4-7] serial (=remote_id), [8-9] caid,
        // [10] uphops, [11] dnhops, [12-19] zeros,
        // [20] nbprov, [21..21+nprov*7-1] provid(3)+sa(4) per prov,
        // [21+nprov*7] node_count=1, [22+nprov*7..29+nprov*7] nodeid(8)
        // For 1 provider: total = 30 + 7 = 37 bytes
        {
            static const uint8_t nodeId[8] = {0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08};
            static const uint16_t caids[] = {
                0x0100, 0x0500, 0x0600, 0x0602, 0x0604, 0x0606, 0x0608,
                0x0B00, 0x0B01, 0x0B02,
                0x0D00, 0x0D02, 0x0D03, 0x0D05,
                0x0900, 0x0919, 0x0960, 0x0963,
                0x1700, 0x1702, 0x1722, 0x1762,
                0x1800, 0x1801, 0x1810, 0x1830, 0x1843,
                0x2600,
                0x4AE0, 0x4AE1,
                0x5581, 0x4A10,
                0x4A60, 0x4A61, 0x4A63,
                0x0E00,
            };
            int nprov = 1;
            int cardSize = 30 + nprov * 7; // 37
            uint32_t cardId = 1;
            for (uint16_t caid : caids) {
                uint8_t card[64] = {};
                // remote_id
                card[0] = (uint8_t)(cardId >> 24);
                card[1] = (uint8_t)(cardId >> 16);
                card[2] = (uint8_t)(cardId >> 8);
                card[3] = (uint8_t)(cardId);
                // serial (same as remote_id)
                card[4] = card[0]; card[5] = card[1];
                card[6] = card[2]; card[7] = card[3];
                // caid
                card[8] = (uint8_t)(caid >> 8);
                card[9] = (uint8_t)(caid & 0xFF);
                // uphops=1, dnhops=2
                card[10] = 0x01;
                card[11] = 0x02;
                // [12-19] zeros (ua/padding)
                // nbprov
                card[20] = (uint8_t)nprov;
                // provider 0: provid=000000 (3 bytes) + sa=00000000 (4 bytes)
                // already zero at [21..27]
                // node_count = 1
                card[21 + nprov * 7] = 0x01;
                // nodeid (8 bytes)
                memcpy(card + 22 + nprov * 7, nodeId, 8);
                if (!ccSend(sock, sendBlock, MSG_NEW_CARD, card, (uint16_t)cardSize)) {
                    lg("Client " + ip + " card send failed at CAID " + hexU16(caid));
                    break;
                }
                cardId++;
            }
            lg("Client " + ip + " announced " + std::to_string(cardId - 1) + " cards");
        }

        // Connect to all enabled upstream CCcam servers (in parallel)
        std::vector<UpConn> upstreams;
        for (auto& srv : cfg.servers) {
            if (!srv.enabled || !srv.valid()) continue;
            UpConn uc;
            uc.srv = &srv;
            uc.label = srv.name.empty()
                ? (srv.host + ":" + std::to_string(srv.port))
                : (srv.name + "(" + srv.host + ":" + std::to_string(srv.port) + ")");
            upstreams.push_back(std::move(uc));
        }
        lg("Connecting " + std::to_string(upstreams.size()) + " upstreams in parallel...");
        {
            std::vector<std::thread> cthreads;
            for (size_t i = 0; i < upstreams.size(); i++) {
                cthreads.emplace_back([this, &upstreams, i]() {
                    auto& uc = upstreams[i];
                    uc.connected = connectUpstream(uc, *uc.srv);
                });
            }
            for (auto& t : cthreads) if (t.joinable()) t.join();
        }
        int upOk = 0;
        for (auto& u : upstreams) {
            if (u.connected) {
                upOk++;
                u.srv->ping_status = 1;
                u.srv->dead_since = 0;
                u.srv->test_detail = "OK (" + std::to_string(u.cardCount) + " cards)";
                lg("Upstream " + u.label + " OK (" + std::to_string(u.cardCount) + " cards)");
            } else {
                u.srv->ping_status = -1;
                if (u.srv->dead_since == 0) u.srv->dead_since = time(nullptr);
                u.srv->test_detail = u.failReason.empty() ? "CONNECT FAIL" : u.failReason;
            }
        }
        lg("Client " + ip + " session ready, " + std::to_string(upOk) + "/" +
           std::to_string(upstreams.size()) + " upstreams connected");

        // ECM / Keep-alive processing loop
        std::string lastEcmHex;
        int dupEcmCount = 0;
        int ecmSinceLog = 0;           // ECMs since last logged line
        int dropsSinceLog = 0;         // drops since last "no upstreams" msg
        int cacheSinceLog = 0;         // cache hits since last logged
        int predSinceLog = 0;          // predictions since last logged
        int cwSinceLog = 0;            // CW forwards since last logged
        auto lastEcmLogTime   = std::chrono::steady_clock::now();
        auto lastDropLogTime  = std::chrono::steady_clock::now();
        auto lastSummaryTime  = std::chrono::steady_clock::now();
        auto reconnectTime    = std::chrono::steady_clock::now();
        int reconnectAttempts = 0;
        while (running) {
            // ── Periodic summary (every 30s) ──
            auto loopNow = std::chrono::steady_clock::now();
            if (std::chrono::duration_cast<std::chrono::seconds>(loopNow - lastSummaryTime).count() >= 30) {
                lastSummaryTime = loopNow;
                int connUp = 0;
                for (auto& uc2 : upstreams) if (uc2.connected) connUp++;
                lg("Summary: ECM=" + std::to_string(ecmTotal.load()) +
                   " ok=" + std::to_string(ecmOk.load()) + " fail=" + std::to_string(ecmFail.load()) +
                   " cache=" + std::to_string(ecmCacheHits.load()) +
                   " up=" + std::to_string(connUp) + "/" + std::to_string(upstreams.size()) +
                   " cli=" + std::to_string(clients.load()));
            }
            // ── Auto-reconnect disconnected upstreams (every 30s) ──
            {
                int connUp2 = 0;
                for (auto& uc2 : upstreams) if (uc2.connected) connUp2++;
                auto reconNow = std::chrono::steady_clock::now();
                bool reconDue = std::chrono::duration_cast<std::chrono::seconds>(reconNow - reconnectTime).count() >= 30;
                if (connUp2 < (int)upstreams.size() && reconDue) {
                    reconnectTime = reconNow;
                    reconnectAttempts++;
                    int reconOk = 0, reconTried = 0;
                    for (auto& uc2 : upstreams) {
                        if (uc2.connected) continue;
                        if (!uc2.srv || !uc2.srv->enabled || !uc2.srv->valid()) continue;
                        reconTried++;
                        if (uc2.sock != INVALID_SOCK) { CLOSE_SOCK(uc2.sock); uc2.sock = INVALID_SOCK; }
                        uc2.connected = connectUpstream(uc2, *uc2.srv);
                        if (uc2.connected) {
                            reconOk++;
                            uc2.srv->ping_status = 1;
                            uc2.srv->dead_since = 0;
                            uc2.srv->test_detail = "OK (" + std::to_string(uc2.cardCount) + " cards)";
                        }
                    }
                    if (reconTried > 0)
                        lg("Reconnect #" + std::to_string(reconnectAttempts) +
                           ": " + std::to_string(reconOk) + "/" + std::to_string(reconTried) + " restored");
                }
            }

            bool recvTimeout = false;
            r = ccRecv(sock, recvBlock, hdr, msgBuf, sizeof(msgBuf), &recvTimeout);
            if (r < 0) {
                if (recvTimeout) {
                    if (!ccSend(sock, sendBlock, MSG_KEEPALIVE, nullptr, 0)) {
                        lg("Client " + ip + " keepalive send failed, disconnecting");
                        break;
                    }
                    continue;
                }
                lg("Client " + ip + " recv error, disconnecting");
                break;
            }

            uint8_t cmd = hdr[1];
            uint16_t msgLen = (uint16_t)((hdr[2] << 8) | hdr[3]);

            if (cmd == MSG_KEEPALIVE) {
                ccSend(sock, sendBlock, MSG_KEEPALIVE, nullptr, 0);
                continue;
            }

            if (cmd == MSG_ECM_REQUEST) {
                ecmTotal++;
                if (msgLen >= 13) {
                    uint16_t caid  = (uint16_t)((msgBuf[0] << 8) | msgBuf[1]);
                    uint32_t provid= (uint32_t)((msgBuf[2]<<24)|(msgBuf[3]<<16)|(msgBuf[4]<<8)|msgBuf[5]);
                    uint32_t cardid= (uint32_t)((msgBuf[6]<<24)|(msgBuf[7]<<16)|(msgBuf[8]<<8)|msgBuf[9]);
                    uint16_t sid   = (uint16_t)((msgBuf[10] << 8) | msgBuf[11]);
                    int ecmLen     = (msgLen > 13) ? (int)(msgLen - 13) : 0;

                    // Feed ECM to harvester for background probing
                    if (onEcmCapture && ecmLen > 0)
                        try { onEcmCapture(caid, provid, sid, msgBuf, msgLen); } catch (...) {}

                    // Build hex of first 16 bytes to detect duplicates
                    std::string ecmHex;
                    int showN = std::min(ecmLen, 16);
                    for (int i = 0; i < showN; i++) {
                        char hx[4]; snprintf(hx, sizeof(hx), "%02X", msgBuf[13+i]);
                        ecmHex += hx;
                    }

                    if (ecmHex != lastEcmHex) {
                        dupEcmCount = 0;
                        lastEcmHex = ecmHex;
                        ecmSinceLog++;
                        // Log unique ECM at most once per 5s
                        auto ecmNow = std::chrono::steady_clock::now();
                        if (std::chrono::duration_cast<std::chrono::seconds>(ecmNow - lastEcmLogTime).count() >= 5) {
                            std::string extra;
                            if (ecmSinceLog > 1)
                                extra = " (+" + std::to_string(ecmSinceLog - 1) + " more)";
                            lg("ECM #" + std::to_string(ecmTotal.load()) +
                               " CAID:" + hexU16(caid) + " PROV:" + hexU32(provid) +
                               " SID:" + hexU16(sid) + " len:" + std::to_string(ecmLen) + extra);
                            ecmSinceLog = 0;
                            lastEcmLogTime = ecmNow;
                        }
                    } else {
                        dupEcmCount++;
                    }

                    // ── AI Cache lookup FIRST (zero upstream latency if hit) ──
                    bool cwSent = false;
                    if (aiEnabled && ecmLen > 0) {
                        uint8_t cachedCw[16] = {};
                        if (aiPredictor.cacheLookup(caid, sid, msgBuf+13, ecmLen, cachedCw)) {
                            ccSend(sock, sendBlock, MSG_ECM_REQUEST, cachedCw, 16, hdr[0]);
                            cwSent = true;
                            ecmOk++;
                            ecmCacheHits++;
                            cacheSinceLog++;
                            turbo.cacheHits++;
                        }
                    }

                    // Try learned CW prediction (pattern-based, no upstream needed)
                    bool predUsed = false;
                    bool predValidated = false;
                    bool predMatched = false;
                    std::array<uint8_t, 16> predCw{};
                    if (!cwSent) {
                        auto pred = learner.predict(caid, provid, sid, msgBuf + 13, ecmLen);
                        if (pred.ok) {
                            bool accept = pred.exact || pred.votes >= learner.minPatternVotes();
                            if (accept) {
                                predUsed = true;
                                predCw = pred.cw;
                                learner.markPredictionUsed(pred);
                                ccSend(sock, sendBlock, MSG_ECM_REQUEST, predCw.data(), 16, hdr[0]);
                                cwSent = true;
                                ecmOk++;
                                predSinceLog++;
                            }
                        }
                    }

                    // ── AI Smart Router: sort upstreams by score for this CAID ──
                    int connectedCount = 0;
                    for (auto& uc : upstreams) if (uc.connected) connectedCount++;

                    if (connectedCount == 0 && !cwSent) {
                        dropsSinceLog++;
                        auto dropNow = std::chrono::steady_clock::now();
                        if (std::chrono::duration_cast<std::chrono::seconds>(dropNow - lastDropLogTime).count() >= 10) {
                            lg("ECM dropped: no upstreams (0/" +
                               std::to_string(upstreams.size()) + ") [" +
                               std::to_string(dropsSinceLog) + " drops in last 10s]");
                            dropsSinceLog = 0;
                            lastDropLogTime = dropNow;
                        }
                    }

                    // Build AI-ranked server order (best for this CAID first)
                    std::vector<int> ecmOrder(upstreams.size());
                    std::iota(ecmOrder.begin(), ecmOrder.end(), 0);
                    if (aiEnabled && upstreams.size() > 1) {
                        std::vector<std::string> labels;
                        for (auto& uc : upstreams) labels.push_back(uc.label);
                        aiPredictor.sortByScore(caid, ecmOrder, labels);
                    }

                    auto ecmStart = std::chrono::steady_clock::now();
                    for (int ucIdx : ecmOrder) {
                        auto& uc = upstreams[ucIdx];
                        if (!uc.connected) continue;
                        uint8_t cwBuf[16] = {};
                        bool got = forwardEcmSingle(uc, msgBuf, msgLen, cwBuf);
                        auto ecmEnd = std::chrono::steady_clock::now();
                        float latencyMs = std::chrono::duration<float, std::milli>(ecmEnd - ecmStart).count();

                        if (!got) {
                            if (uc.connected) // only log first time
                                lg("Upstream " + uc.label + " disconnected (ECM fail)");
                            uc.connected = false;
                            if (uc.sock != INVALID_SOCK) { CLOSE_SOCK(uc.sock); uc.sock = INVALID_SOCK; }
                        }
                        // Feed learner + AI predictor + channel analytics + CA engine
                        learner.addSample(caid, provid, sid, msgBuf+13, ecmLen, got, cwBuf, uc.label);
                        if (aiEnabled) {
                            aiPredictor.learn(caid, sid, provid, msgBuf+13, ecmLen,
                                              cwBuf, got, uc.label, latencyMs);
                            aiPredictor.learnChannel(caid, sid, provid, got, latencyMs,
                                                     uc.label, cwBuf, msgBuf+13, ecmLen);
                        }
                        caEngine.learn(caid, sid, provid, msgBuf+13, ecmLen,
                                       cwBuf, got, uc.label, latencyMs);
                        if (cfg.log_ecm)
                            logEcmCw(caid, provid, sid, ecmLen, msgBuf+13, uc.label, got, cwBuf);

                        if (got) {
                            if (predUsed) {
                                predValidated = true;
                                bool match = (std::memcmp(predCw.data(), cwBuf, 16) == 0);
                                predMatched = predMatched || match;
                                if (!match)
                                    ccSend(sock, sendBlock, MSG_ECM_REQUEST, cwBuf, 16, hdr[0]);
                            }
                            if (!cwSent) {
                                // Store in AI cache for instant future responses
                                if (aiEnabled)
                                    aiPredictor.cacheStore(caid, sid, msgBuf+13, ecmLen, cwBuf, uc.label);
                                cwSinceLog++;
                                ccSend(sock, sendBlock, MSG_ECM_REQUEST, cwBuf, 16, hdr[0]);
                                cwSent = true;
                                ecmOk++;
                                turbo.upstreamHits++;
                            }
                            break; // AI: first CW wins, stop trying other servers
                        }
                    }
                    if (predUsed) {
                        if (predValidated) learner.reportPredictionResult(predMatched);
                        else learner.reportPredictionUnverified();
                    }
                    
                    // Offline CW DB fallback (persistent learned CWs)
                    if (!cwSent && onOfflineLookup && ecmLen > 0) {
                        uint8_t offCw[16] = {};
                        if (onOfflineLookup(caid, sid, msgBuf+13, ecmLen, offCw)) {
                            ccSend(sock, sendBlock, MSG_ECM_REQUEST, offCw, 16, hdr[0]);
                            cwSent = true;
                            ecmOk++;
                            predSinceLog++;
                            turbo.offlineHits++;
                        }
                    }

                    // CA Engine CW prediction (XOR-delta, BISS fixed key, PowerVu)
                    if (!cwSent && ecmLen > 0) {
                        uint8_t caCw[16] = {};
                        float conf = 0;
                        if (caEngine.predictCw(caid, sid, caCw, &conf) && conf >= 0.3f) {
                            ccSend(sock, sendBlock, MSG_ECM_REQUEST, caCw, 16, hdr[0]);
                            cwSent = true;
                            ecmOk++;
                            predSinceLog++;
                            turbo.caPredictHits++;
                        }
                    }

                    // AI fallback: try AI prediction if no CW from upstreams
                    if (!cwSent && aiEnabled && connectedCount == 0) {
                        uint8_t aiCw[16] = {};
                        // First try fast pattern match
                        if (aiPredictor.predictFast(caid, sid, msgBuf+13, ecmLen, aiCw)) {
                            ccSend(sock, sendBlock, MSG_ECM_REQUEST, aiCw, 16, hdr[0]);
                            cwSent = true;
                            ecmOk++;
                            predSinceLog++;
                            aiPredictor.predictions_correct++;
                            turbo.aiHits++;
                        }
                    }
                    
                    if (!cwSent) {
                        ecmFail++;
                        ccSend(sock, sendBlock, MSG_ECM_NOK2, nullptr, 0, hdr[0]);
                    }

                    // ── Turbo Pipeline tracking + Scanner notification ──
                    {
                        auto ecmEndAll = std::chrono::steady_clock::now();
                        float totalMs = std::chrono::duration<float, std::milli>(ecmEndAll - ecmStart).count();
                        turbo.totalEcm++;
                        turbo.recordLatency(totalMs);

                        // Notify scanner of ECM result (for signal quality tracking)
                        if (onScannerNotify) {
                            uint8_t cwForNotify[16] = {};
                            bool gotAny = cwSent;
                            // We don't have the CW here easily for all paths,
                            // but the scanner tracks via its own notifyEcmResult
                            try { onScannerNotify(caid, provid, sid, gotAny, cwForNotify, totalMs, ""); } catch (...) {}
                        }
                    }
                } else {
                    ccSend(sock, sendBlock, MSG_ECM_NOK2, nullptr, 0, hdr[0]);
                }

            } else if (cmd == MSG_CLI_DATA) {
                lg("Client " + ip + " re-sent CLI_DATA");

            } else if (cmd == MSG_EMM_REQUEST) {
                if (msgLen >= 4) {
                    uint16_t emmCaid = (uint16_t)((msgBuf[0] << 8) | msgBuf[1]);
                    lg("EMM CAID:" + hexU16(emmCaid) + " len:" + std::to_string(msgLen));
                }
                for (auto& uc : upstreams) {
                    if (uc.connected)
                        ccSend(uc.sock, uc.sendBlock, MSG_EMM_REQUEST, msgBuf, msgLen);
                }
                ccSend(sock, sendBlock, MSG_EMM_REQUEST, nullptr, 0);

            } else if (cmd == MSG_CMD_05) {
                ccSend(sock, sendBlock, MSG_KEEPALIVE, nullptr, 0);

            } else {
                std::string dmp;
                int showN = std::min((int)msgLen, 32);
                for (int i = 0; i < showN; i++) {
                    char hx[4]; snprintf(hx, sizeof(hx), "%02X", msgBuf[i]);
                    dmp += hx;
                }
                lg("Unknown cmd=0x" + hexU16(cmd) + " len=" + std::to_string(msgLen) +
                   " data:" + dmp + " from " + ip);
            }
        }
        for (auto& uc : upstreams) {
            if (uc.sock != INVALID_SOCK) CLOSE_SOCK(uc.sock);
        }
        done();
    }

    // ── SOCKS5 proxy connect ───────────────────────────────────────────────────
    // NOTE: Called from parallel threads — uses only local helpers.
    bool socks5Connect(SockT sock, const UpstreamServer& srv) {
        // Thread-safe local helpers
        auto sa = [](SockT s, const uint8_t* d, int len) -> int {
            int sent = 0;
            while (sent < len) { int r = send(s,(const char*)d+sent,len-sent,0); if(r<=0) return sent; sent+=r; }
            return sent;
        };
        auto ra = [](SockT s, uint8_t* d, int len) -> int {
            int got = 0;
            while (got < len) { int r = recv(s,(char*)d+got,len-got,0); if(r<=0) return got; got+=r; }
            return got;
        };

        // Connect to SOCKS5 proxy first
        sockaddr_in pa{};
        pa.sin_family = AF_INET;
        pa.sin_port = htons((uint16_t)srv.proxy_port);
        if (inet_pton(AF_INET, srv.proxy_host.c_str(), &pa.sin_addr) != 1) {
            struct addrinfo hints{}, *res = nullptr;
            hints.ai_family = AF_INET; hints.ai_socktype = SOCK_STREAM;
            if (getaddrinfo(srv.proxy_host.c_str(), nullptr, &hints, &res) != 0 || !res) {
                lg("SOCKS5 DNS failed: " + srv.proxy_host);
                return false;
            }
            pa.sin_addr = ((sockaddr_in*)res->ai_addr)->sin_addr;
            freeaddrinfo(res);
        }
        if (::connect(sock, (sockaddr*)&pa, sizeof(pa)) < 0) {
            lg("SOCKS5 proxy connect failed: " + srv.proxy_host);
            return false;
        }
        // Auth method negotiation
        bool hasAuth = !srv.proxy_user.empty();
        uint8_t greet[4] = {0x05, (uint8_t)(hasAuth ? 2 : 1), 0x00};
        if (hasAuth) greet[3] = 0x02;
        if (sa(sock, greet, hasAuth ? 4 : 3) != (hasAuth ? 4 : 3)) return false;
        uint8_t gresp[2];
        if (ra(sock, gresp, 2) != 2 || gresp[0] != 0x05) return false;
        if (gresp[1] == 0x02 && hasAuth) {
            // Username/password auth (RFC 1929)
            std::vector<uint8_t> auth;
            auth.push_back(0x01);
            auth.push_back((uint8_t)srv.proxy_user.size());
            auth.insert(auth.end(), srv.proxy_user.begin(), srv.proxy_user.end());
            auth.push_back((uint8_t)srv.proxy_pass.size());
            auth.insert(auth.end(), srv.proxy_pass.begin(), srv.proxy_pass.end());
            if (sa(sock, auth.data(), (int)auth.size()) != (int)auth.size()) return false;
            uint8_t aresp[2];
            if (ra(sock, aresp, 2) != 2 || aresp[1] != 0x00) {
                lg("SOCKS5 auth failed"); return false;
            }
        } else if (gresp[1] != 0x00) {
            lg("SOCKS5 unsupported auth method"); return false;
        }
        // CONNECT request — use domain name (type 0x03) for flexibility
        std::vector<uint8_t> req;
        req.push_back(0x05); req.push_back(0x01); req.push_back(0x00);
        req.push_back(0x03); // domain name
        req.push_back((uint8_t)srv.host.size());
        req.insert(req.end(), srv.host.begin(), srv.host.end());
        req.push_back((uint8_t)(srv.port >> 8));
        req.push_back((uint8_t)(srv.port & 0xFF));
        if (sa(sock, req.data(), (int)req.size()) != (int)req.size()) return false;
        uint8_t rresp[10];
        if (ra(sock, rresp, 4) != 4 || rresp[1] != 0x00) {
            lg("SOCKS5 CONNECT failed, status=" + std::to_string(rresp[1]));
            return false;
        }
        // Drain bind address based on address type
        if (rresp[3] == 0x01) { uint8_t d[6]; ra(sock, d, 6); }
        else if (rresp[3] == 0x03) {
            uint8_t dlen; ra(sock, &dlen, 1);
            std::vector<uint8_t> d(dlen + 2); ra(sock, d.data(), dlen + 2);
        }
        else if (rresp[3] == 0x04) { uint8_t d[18]; ra(sock, d, 18); }
        lg("SOCKS5 tunnel established to " + srv.host + ":" + std::to_string(srv.port));
        return true;
    }

    // ── Upstream CCcam client (multi-server) ─────────────────────────────────
    // NOTE: This runs in parallel threads — uses only local send/recv helpers
    //       to avoid any shared state. recvAllT/ccRecv are now static.
    bool connectUpstream(UpConn& uc, const UpstreamServer& srv) {
        uc.sock = socket(AF_INET, SOCK_STREAM, 0);
        if (uc.sock == INVALID_SOCK) return false;

        // TCP keepalive + no-delay
        int one = 1;
        setsockopt(uc.sock, SOL_SOCKET, SO_KEEPALIVE, (char*)&one, sizeof(one));
        setsockopt(uc.sock, IPPROTO_TCP, TCP_NODELAY, (char*)&one, sizeof(one));

        // Set socket-level recv timeout (5s for handshake)
#ifdef _WIN32
        DWORD rcvTo = 5000;
        setsockopt(uc.sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&rcvTo, sizeof(rcvTo));
        setsockopt(uc.sock, SOL_SOCKET, SO_SNDTIMEO, (char*)&rcvTo, sizeof(rcvTo));
#else
        timeval rcvTo; rcvTo.tv_sec = 5; rcvTo.tv_usec = 0;
        setsockopt(uc.sock, SOL_SOCKET, SO_RCVTIMEO, &rcvTo, sizeof(rcvTo));
        setsockopt(uc.sock, SOL_SOCKET, SO_SNDTIMEO, &rcvTo, sizeof(rcvTo));
#endif

        // Thread-local send/recv helpers — no shared state
        auto localSendAll = [](SockT s, const uint8_t* d, int len) -> int {
            int sent = 0;
            while (sent < len) {
                int r = send(s, (const char*)d + sent, len - sent, 0);
                if (r <= 0) return sent;
                sent += r;
            }
            return sent;
        };
        auto localRecvAll = [](SockT s, uint8_t* d, int len) -> int {
            int got = 0;
            while (got < len) {
                int r = recv(s, (char*)d + got, len - got, 0);
                if (r <= 0) return got;
                got += r;
            }
            return got;
        };
        auto fail = [&](const std::string& reason) -> bool {
            lg("Upstream " + reason + ": " + srv.host + ":" + std::to_string(srv.port));
            uc.failReason = reason;
            CLOSE_SOCK(uc.sock); uc.sock = INVALID_SOCK;
            return false;
        };

        if (srv.hasProxy()) {
            if (!socks5Connect(uc.sock, srv)) {
                uc.failReason = "SOCKS5 FAIL";
                CLOSE_SOCK(uc.sock); uc.sock = INVALID_SOCK;
                return false;
            }
        } else {
            // Direct connect
            sockaddr_in addr{};
            addr.sin_family = AF_INET;
            addr.sin_port = htons((uint16_t)srv.port);
            if (inet_pton(AF_INET, srv.host.c_str(), &addr.sin_addr) != 1) {
                struct addrinfo hints{}, *res = nullptr;
                hints.ai_family = AF_INET; hints.ai_socktype = SOCK_STREAM;
                if (getaddrinfo(srv.host.c_str(), nullptr, &hints, &res) != 0 || !res) {
                    return fail("DNS failed");
                }
                addr.sin_addr = ((sockaddr_in*)res->ai_addr)->sin_addr;
                freeaddrinfo(res);
            }
            if (::connect(uc.sock, (sockaddr*)&addr, sizeof(addr)) < 0) {
                return fail("TCP connect failed");
            }
        }

        // CCcam client handshake — using local helpers only
        uint8_t seed[16];
        if (localRecvAll(uc.sock, seed, 16) != 16) {
            return fail("no seed (not a CCcam server?)");
        }

        uint8_t cdata[16];
        memcpy(cdata, seed, 16);
        cc_crypt_xor(cdata);
        uint8_t chash[20];
        sha1(cdata, 16, chash);

        uc.recvBlock.init(chash, 20);
        uc.recvBlock.decrypt(cdata, 16);
        uc.sendBlock.init(cdata, 16);
        uc.sendBlock.decrypt(chash, 20);

        // Send hash
        uint8_t hashCopy[20];
        memcpy(hashCopy, chash, 20);
        uc.sendBlock.encrypt(hashCopy, 20);
        if (localSendAll(uc.sock, hashCopy, 20) != 20) {
            return fail("send hash failed");
        }

        // Send username
        uint8_t userBuf[20] = {};
        size_t ulen = std::min(srv.user.size(), (size_t)19);
        memcpy(userBuf, srv.user.data(), ulen);
        uc.sendBlock.encrypt(userBuf, 20);
        if (localSendAll(uc.sock, userBuf, 20) != 20) {
            return fail("send user failed");
        }

        // Advance cipher with password
        if (!srv.pass.empty()) {
            std::vector<uint8_t> pwd(srv.pass.begin(), srv.pass.end());
            uc.sendBlock.encrypt(pwd.data(), (int)pwd.size());
        }

        // Send "CCcam\0"
        uint8_t ccBuf[6] = {'C','C','c','a','m',0};
        uc.sendBlock.encrypt(ccBuf, 6);
        if (localSendAll(uc.sock, ccBuf, 6) != 6) {
            return fail("send CCcam marker failed");
        }

        // Receive ACK (20 bytes)
        uint8_t ackBuf[20];
        if (localRecvAll(uc.sock, ackBuf, 20) != 20) {
            return fail("auth failed (no ACK, server closed connection - bad user/pass?)");
        }
        uc.recvBlock.decrypt(ackBuf, 20);
        if (memcmp(ackBuf, "CCcam", 5) != 0) {
            return fail("auth failed (bad ACK content - wrong password?)");
        }

        lg("Upstream auth OK: " + srv.host + ":" + std::to_string(srv.port) +
           " user=" + srv.user);

        // Send CLI_DATA (using local send since ccSend uses shared sendBlock encryption
        //   but here we own uc.sendBlock exclusively)
        {
            uint8_t cliHdr[4] = {0, MSG_CLI_DATA, 0, 0};
            uc.sendBlock.encrypt(cliHdr, 4);
            localSendAll(uc.sock, cliHdr, 4);
        }

        // Switch to longer timeout for card drain phase
#ifdef _WIN32
        DWORD drainTo = 3000;
        setsockopt(uc.sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&drainTo, sizeof(drainTo));
#else
        timeval drainTo; drainTo.tv_sec = 3; drainTo.tv_usec = 0;
        setsockopt(uc.sock, SOL_SOCKET, SO_RCVTIMEO, &drainTo, sizeof(drainTo));
#endif

        // Drain SRV_DATA + cards using local recv
        uc.cardCount = 0;
        for (int attempt = 0; attempt < 500; attempt++) {
            uint8_t uhdr[4];
            if (localRecvAll(uc.sock, uhdr, 4) != 4) break;
            uc.recvBlock.decrypt(uhdr, 4);
            uint16_t mlen = (uint16_t)((uhdr[2] << 8) | uhdr[3]);
            if (mlen > 2048) break;
            uint8_t umsgBuf[2048];
            if (mlen > 0) {
                if (localRecvAll(uc.sock, umsgBuf, mlen) != (int)mlen) break;
                uc.recvBlock.decrypt(umsgBuf, mlen);
            }
            uint8_t ucmd = uhdr[1];
            if (ucmd == MSG_NEW_CARD) uc.cardCount++;
            else if (ucmd == MSG_SRV_DATA || ucmd == MSG_CLI_DATA) { }
            else if (ucmd == MSG_KEEPALIVE) {
                uint8_t ka[4] = {0, MSG_KEEPALIVE, 0, 0};
                uc.sendBlock.encrypt(ka, 4);
                localSendAll(uc.sock, ka, 4);
            }
            else if (ucmd == MSG_CMD_05 || ucmd == MSG_CARD_REMOVED) { }
            else break;
        }

        // Set final timeout for ECM forwarding phase (10s)
#ifdef _WIN32
        DWORD ecmTo = 10000;
        setsockopt(uc.sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&ecmTo, sizeof(ecmTo));
#else
        timeval ecmTo; ecmTo.tv_sec = 10; ecmTo.tv_usec = 0;
        setsockopt(uc.sock, SOL_SOCKET, SO_RCVTIMEO, &ecmTo, sizeof(ecmTo));
#endif

        lg("Upstream connected: " + srv.host + ":" + std::to_string(srv.port) +
           " cards=" + std::to_string(uc.cardCount));
        return true;
    }

    // ── Forward single ECM, return CW in cwOut[16] ──────────────────────────
    bool forwardEcmSingle(UpConn& uc, const uint8_t* ecmData, uint16_t ecmLen,
                          uint8_t cwOut[16]) {
        if (!ccSend(uc.sock, uc.sendBlock, MSG_ECM_REQUEST, ecmData, ecmLen)) {
            lg("fwdECM " + uc.label + ": send failed");
            return false;
        }

        uint8_t uhdr[4], ubuf[512];
        for (int tries = 0; tries < 10; tries++) {
            bool recvTo = false;
            int ur = ccRecv(uc.sock, uc.recvBlock, uhdr, ubuf, sizeof(ubuf), &recvTo);
            if (ur < 0) {
                lg("fwdECM " + uc.label + ": recv fail try=" + std::to_string(tries) +
                   " timeout=" + std::to_string(recvTo));
                if (recvTo) continue;  // timeout is not fatal, keep trying
                return false;
            }
            uint8_t rcmd = uhdr[1];
            uint16_t rlen = (uint16_t)((uhdr[2] << 8) | uhdr[3]);

            if (rcmd == MSG_ECM_REQUEST && rlen >= 16) {
                memcpy(cwOut, ubuf, 16);
                return true;
            } else if (rcmd == MSG_ECM_NOK1 || rcmd == MSG_ECM_NOK2) {
                lg("fwdECM " + uc.label + ": ECM_NOK cmd=0x" + hexU16(rcmd));
                return false;
            } else if (rcmd == MSG_KEEPALIVE) {
                ccSend(uc.sock, uc.sendBlock, MSG_KEEPALIVE, nullptr, 0);
            } else if (rcmd == MSG_NEW_CARD || rcmd == MSG_CARD_REMOVED ||
                       rcmd == MSG_CMD_05 || rcmd == MSG_SRV_DATA) {
                continue;
            } else {
                lg("fwdECM " + uc.label + ": unexpected cmd=0x" + hexU16(rcmd) +
                   " len=" + std::to_string(rlen));
                return false;
            }
        }
        lg("fwdECM " + uc.label + ": exhausted retries");
        return false;
    }

    // ── Log ECM request + CW response to CSV for training ───────────────────
    void logEcmCw(uint16_t caid, uint32_t provid, uint16_t sid,
                  int ecmLen, const uint8_t* ecmBody,
                  const std::string& serverLabel, bool gotCw, const uint8_t cw[16]) {
        std::lock_guard<std::mutex> g(ecmLogMu_);
        try {
            static std::string logPath = CccamConfig::ecmLogPath();
            bool isNew = false;
            {   std::ifstream test(logPath);
                isNew = !test.good() || test.peek() == std::ifstream::traits_type::eof();
            }
            std::ofstream f(logPath, std::ios::app);
            if (!f.is_open()) return;
            if (isNew)
                f << "time,server,caid,provid,sid,ecm_len,ecm_hex,cw_ok,cw_hex\n";
            // Timestamp
            auto now = std::chrono::system_clock::now();
            auto t = std::chrono::system_clock::to_time_t(now);
            char ts[32]; std::strftime(ts, sizeof(ts), "%Y-%m-%d %H:%M:%S", std::localtime(&t));
            f << ts << ",";
            f << "\"" << serverLabel << "\",";
            char hx[12];
            snprintf(hx, sizeof(hx), "%04X", caid); f << hx << ",";
            snprintf(hx, sizeof(hx), "%08X", provid); f << hx << ",";
            snprintf(hx, sizeof(hx), "%04X", sid); f << hx << ",";
            f << ecmLen << ",";
            // ECM body hex (first 32 bytes)
            int showN = std::min(ecmLen, 32);
            for (int i = 0; i < showN; i++) {
                snprintf(hx, sizeof(hx), "%02X", ecmBody[i]); f << hx;
            }
            f << "," << (gotCw ? "1" : "0") << ",";
            if (gotCw) {
                for (int i = 0; i < 16; i++) {
                    snprintf(hx, sizeof(hx), "%02X", cw[i]); f << hx;
                }
            }
            f << "\n";
        } catch (...) {}
    }
};

} // namespace cccam
