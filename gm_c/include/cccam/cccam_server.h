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
#include <cstring>
#include <cstdlib>
#include <ctime>
#include <algorithm>
#include "stb/crash_log.h"

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

// ── Server ──────────────────────────────────────────────────────────────────
class CccamServer {
public:
    using LogCb = std::function<void(const std::string&)>;

    struct Config {
        int  port = 8000;
        std::string user = "a";
        std::string pass = "a";
        // Upstream CCcam server for ECM forwarding
        std::string upstream_host;
        int  upstream_port = 0;
        std::string upstream_user;
        std::string upstream_pass;
        bool hasUpstream() const { return !upstream_host.empty() && upstream_port > 0; }
    };

    Config              cfg;
    std::atomic<bool>   running{false};
    std::atomic<int>    clients{0};
    std::atomic<int>    ecmTotal{0};
    std::atomic<int>    ecmOk{0};
    std::atomic<int>    ecmFail{0};

    std::string getStatus() {
        std::lock_guard<std::mutex> g(mu_);
        return status_;
    }

    void start(LogCb cb) {
        if (running) return;
        log_ = cb;
        running = true;
        setStatus("Starting on port " + std::to_string(cfg.port));
        thread_ = std::thread(&CccamServer::serverLoop, this);
    }

    void stop() {
        if (!running) return;
        running = false;
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

    void setStatus(const std::string& s) {
        std::lock_guard<std::mutex> g(mu_);
        status_ = s;
    }
    void lg(const std::string& s) {
        setStatus(s);
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
    bool lastRecvTimeout_ = false;
    int recvAll(SockT s, uint8_t* d, int len) {
        lastRecvTimeout_ = false;
        int got=0;
        while(got<len) {
            fd_set fds; FD_ZERO(&fds); FD_SET(s, &fds);
            timeval tv; tv.tv_sec = 30; tv.tv_usec = 0;
            int sel = select((int)s + 1, &fds, nullptr, nullptr, &tv);
            if (sel == 0) { lastRecvTimeout_ = true; return got; }
            if (sel < 0) {
#ifdef _WIN32
                int e = WSAGetLastError();
#else
                int e = errno;
#endif
                if (log_) log_("[CCcam] select error=" + std::to_string(e));
                return got;
            }
            int r=recv(s,(char*)d+got,len-got,0);
            if(r<=0) {
#ifdef _WIN32
                int e = WSAGetLastError();
#else
                int e = errno;
#endif
                if (log_) log_("[CCcam] recv r=" + std::to_string(r) + " err=" + std::to_string(e) + " got=" + std::to_string(got) + "/" + std::to_string(len));
                return got;
            }
            got+=r;
        }
        return got;
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
    int ccRecv(SockT sock, CcCryptBlock& recvBlock, uint8_t hdr[4],
               uint8_t* outBuf, int outBufSize) {
        // Save cipher state before reading header, in case of timeout
        CcCryptBlock saved = recvBlock;
        if (recvAll(sock, hdr, 4) != 4) {
            recvBlock = saved; // restore on failure
            return -1;
        }
        recvBlock.decrypt(hdr, 4);
        uint16_t msgLen = (uint16_t)((hdr[2] << 8) | hdr[3]);
        if (msgLen > outBufSize) return -1;
        if (msgLen > 0) {
            if (recvAll(sock, outBuf, msgLen) != msgLen) return -1;
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

        // Set socket timeout for recv
#ifdef _WIN32
        DWORD tv=10000;
        setsockopt(sock,SOL_SOCKET,SO_RCVTIMEO,(char*)&tv,sizeof(tv));
#else
        timeval tv{10,0};
        setsockopt(sock,SOL_SOCKET,SO_RCVTIMEO,&tv,sizeof(tv));
#endif

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

        // Connect to upstream CCcam server (if configured)
        SockT upSock = INVALID_SOCK;
        CcCryptBlock upSendBlock{}, upRecvBlock{};
        bool upConnected = false;
        if (cfg.hasUpstream()) {
            upConnected = connectUpstream(upSock, upSendBlock, upRecvBlock);
            if (upConnected)
                lg("Upstream " + cfg.upstream_host + ":" + std::to_string(cfg.upstream_port) + " connected");
            else
                lg("Upstream connection FAILED - will send ECM_NOK2");
        }

        lg("Client " + ip + " session ready, waiting for ECMs" +
           (upConnected ? " (upstream relay ON)" : ""));

        // ECM / Keep-alive processing loop
        std::string lastEcmHex;
        int dupEcmCount = 0;
        while (running) {
            r = ccRecv(sock, recvBlock, hdr, msgBuf, sizeof(msgBuf));
            if (r < 0) {
                if (lastRecvTimeout_) {
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

                    // Build hex of first 16 bytes to detect duplicates
                    std::string ecmHex;
                    int showN = std::min(ecmLen, 16);
                    for (int i = 0; i < showN; i++) {
                        char hx[4]; snprintf(hx, sizeof(hx), "%02X", msgBuf[13+i]);
                        ecmHex += hx;
                    }

                    if (ecmHex != lastEcmHex) {
                        if (dupEcmCount > 0)
                            lg("  (+" + std::to_string(dupEcmCount) + " duplicate ECMs)");
                        dupEcmCount = 0;
                        lastEcmHex = ecmHex;
                        lg("ECM #" + std::to_string(ecmTotal.load()) +
                           " CAID:" + hexU16(caid) + " PROV:" + hexU32(provid) +
                           " SID:" + hexU16(sid) + " len:" + std::to_string(ecmLen));
                    } else {
                        dupEcmCount++;
                    }

                    // Forward ECM to upstream if connected
                    bool cwSent = false;
                    if (upConnected) {
                        cwSent = forwardEcm(upSock, upSendBlock, upRecvBlock,
                                            sock, sendBlock, hdr[0],
                                            msgBuf, msgLen);
                        if (cwSent) {
                            ecmOk++;
                        } else {
                            // Upstream failed, try reconnecting once
                            lg("Upstream ECM forward failed, reconnecting...");
                            if (upSock != INVALID_SOCK) CLOSE_SOCK(upSock);
                            upSock = INVALID_SOCK;
                            upConnected = connectUpstream(upSock, upSendBlock, upRecvBlock);
                            if (upConnected) {
                                cwSent = forwardEcm(upSock, upSendBlock, upRecvBlock,
                                                    sock, sendBlock, hdr[0],
                                                    msgBuf, msgLen);
                                if (cwSent) ecmOk++;
                            }
                        }
                    }

                    if (!cwSent) {
                        ecmFail++;
                        ccSend(sock, sendBlock, MSG_ECM_NOK2, nullptr, 0, hdr[0]);
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
                if (upConnected) {
                    ccSend(upSock, upSendBlock, MSG_EMM_REQUEST, msgBuf, msgLen);
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
        if (upSock != INVALID_SOCK) CLOSE_SOCK(upSock);
        done();
    }

    // ── Upstream CCcam client ─────────────────────────────────────────────────
    bool connectUpstream(SockT& sock, CcCryptBlock& sBlock, CcCryptBlock& rBlock) {
        sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock == INVALID_SOCK) return false;

#ifdef _WIN32
        DWORD utv = 8000;
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&utv, sizeof(utv));
        setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, (char*)&utv, sizeof(utv));
#else
        timeval utv{8,0};
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &utv, sizeof(utv));
        setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, &utv, sizeof(utv));
#endif

        sockaddr_in addr{};
        addr.sin_family = AF_INET;
        addr.sin_port = htons((uint16_t)cfg.upstream_port);
        if (inet_pton(AF_INET, cfg.upstream_host.c_str(), &addr.sin_addr) != 1) {
            struct addrinfo hints{}, *res = nullptr;
            hints.ai_family = AF_INET;
            hints.ai_socktype = SOCK_STREAM;
            if (getaddrinfo(cfg.upstream_host.c_str(), nullptr, &hints, &res) != 0 || !res) {
                CLOSE_SOCK(sock); sock = INVALID_SOCK;
                lg("Upstream DNS failed: " + cfg.upstream_host);
                return false;
            }
            addr.sin_addr = ((sockaddr_in*)res->ai_addr)->sin_addr;
            freeaddrinfo(res);
        }

        if (::connect(sock, (sockaddr*)&addr, sizeof(addr)) < 0) {
            CLOSE_SOCK(sock); sock = INVALID_SOCK;
            lg("Upstream connect failed");
            return false;
        }

        // CCcam client handshake
        // Step 1: Receive 16-byte seed from server
        uint8_t seed[16];
        if (recvAll(sock, seed, 16) != 16) {
            CLOSE_SOCK(sock); sock = INVALID_SOCK;
            lg("Upstream handshake: no seed");
            return false;
        }

        // Step 2: Client-side key setup
        uint8_t cdata[16];
        memcpy(cdata, seed, 16);
        cc_crypt_xor(cdata);

        uint8_t chash[20];
        sha1(cdata, 16, chash);

        // Client: recvBlock = init(hash), decrypt data
        rBlock.init(chash, 20);
        rBlock.decrypt(cdata, 16);

        // Client: sendBlock = init(data), decrypt hash
        sBlock.init(cdata, 16);
        sBlock.decrypt(chash, 20);

        // Step 3: Send hash to server (encrypted with sendBlock)
        uint8_t hashCopy[20];
        memcpy(hashCopy, chash, 20);
        sBlock.encrypt(hashCopy, 20);
        if (sendAll(sock, hashCopy, 20) != 20) {
            CLOSE_SOCK(sock); sock = INVALID_SOCK;
            return false;
        }

        // Step 4: Send username (20 bytes, encrypted)
        uint8_t userBuf[20] = {};
        size_t ulen = std::min(cfg.upstream_user.size(), (size_t)19);
        memcpy(userBuf, cfg.upstream_user.data(), ulen);
        sBlock.encrypt(userBuf, 20);
        if (sendAll(sock, userBuf, 20) != 20) {
            CLOSE_SOCK(sock); sock = INVALID_SOCK;
            return false;
        }

        // Step 5: Encrypt password to advance sendBlock cipher state (not sent)
        if (!cfg.upstream_pass.empty()) {
            std::vector<uint8_t> pwd(cfg.upstream_pass.begin(), cfg.upstream_pass.end());
            sBlock.encrypt(pwd.data(), (int)pwd.size());
        }

        // Step 6: Send "CCcam\0" (6 bytes, encrypted)
        uint8_t ccBuf[6] = {'C','C','c','a','m',0};
        sBlock.encrypt(ccBuf, 6);
        if (sendAll(sock, ccBuf, 6) != 6) {
            CLOSE_SOCK(sock); sock = INVALID_SOCK;
            return false;
        }

        // Step 7: Receive ACK (20 bytes)
        uint8_t ackBuf[20];
        if (recvAll(sock, ackBuf, 20) != 20) {
            CLOSE_SOCK(sock); sock = INVALID_SOCK;
            lg("Upstream auth failed (no ACK)");
            return false;
        }
        rBlock.decrypt(ackBuf, 20);
        if (memcmp(ackBuf, "CCcam", 5) != 0) {
            CLOSE_SOCK(sock); sock = INVALID_SOCK;
            lg("Upstream auth failed (bad ACK)");
            return false;
        }

        // Step 8: Send CLI_DATA
        ccSend(sock, sBlock, MSG_CLI_DATA, nullptr, 0);

        // Step 9: Drain SRV_DATA and card announcements
        uint8_t uhdr[4], umsgBuf[2048];
        int cardCount = 0;
#ifdef _WIN32
        DWORD utv2 = 2000;
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&utv2, sizeof(utv2));
#else
        timeval utv2{2,0};
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &utv2, sizeof(utv2));
#endif
        for (int attempt = 0; attempt < 200; attempt++) {
            int ur = ccRecv(sock, rBlock, uhdr, umsgBuf, sizeof(umsgBuf));
            if (ur < 0) break;
            if (uhdr[1] == MSG_NEW_CARD) cardCount++;
            else if (uhdr[1] == MSG_SRV_DATA) { /* ok */ }
            else if (uhdr[1] == MSG_CLI_DATA) { /* ok */ }
            else break;
        }
        lg("Upstream ready, received " + std::to_string(cardCount) + " cards");

        // Restore longer timeout for ECM responses
#ifdef _WIN32
        utv = 10000;
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&utv, sizeof(utv));
#else
        utv = {10,0};
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &utv, sizeof(utv));
#endif
        return true;
    }

    // Forward ECM to upstream and relay CW back to STB client
    bool forwardEcm(SockT upSock, CcCryptBlock& upSend, CcCryptBlock& upRecv,
                    SockT cliSock, CcCryptBlock& cliSend, uint8_t seq,
                    const uint8_t* ecmData, uint16_t ecmLen) {
        if (!ccSend(upSock, upSend, MSG_ECM_REQUEST, ecmData, ecmLen)) {
            return false;
        }

        // Wait for response (CW or NOK)
        uint8_t uhdr[4], ubuf[256];
        int ur = ccRecv(upSock, upRecv, uhdr, ubuf, sizeof(ubuf));
        if (ur < 0) return false;

        uint8_t rcmd = uhdr[1];
        uint16_t rlen = (uint16_t)((uhdr[2] << 8) | uhdr[3]);

        if (rcmd == MSG_ECM_REQUEST && rlen >= 16) {
            // CW response: 16 bytes (8 even + 8 odd control words)
            lg("CW received from upstream, relaying to STB");
            ccSend(cliSock, cliSend, MSG_ECM_REQUEST, ubuf, rlen, seq);
            return true;
        } else if (rcmd == MSG_KEEPALIVE) {
            // Keepalive during ECM wait, try reading once more
            ccSend(upSock, upSend, MSG_KEEPALIVE, nullptr, 0);
            ur = ccRecv(upSock, upRecv, uhdr, ubuf, sizeof(ubuf));
            if (ur > 0 && uhdr[1] == MSG_ECM_REQUEST) {
                rlen = (uint16_t)((uhdr[2] << 8) | uhdr[3]);
                if (rlen >= 16) {
                    lg("CW received (after keepalive), relaying");
                    ccSend(cliSock, cliSend, MSG_ECM_REQUEST, ubuf, rlen, seq);
                    return true;
                }
            }
        }

        return false;
    }
};

} // namespace cccam
