// imgui_app.cpp – GMScreen ImGui frontend for MediaStar 4030 4K
// ALL data races eliminated: every shared field behind mutex.
// SEH + file crash log for diagnosis.
#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif
#ifndef NOMINMAX
#define NOMINMAX
#endif
#include <winsock2.h>
#include <ws2tcpip.h>
#include <windows.h>
#include <d3d11.h>
#include <dbghelp.h>

#include <atomic>
#include <chrono>
#include <deque>
#include <exception>
#include <fstream>
#include <memory>
#include <mutex>
#include <csignal>
#include <string>
#include <thread>
#include <vector>
#include <ctime>
#include <cstdio>
#include <cmath>
#include <cstring>
#include <zlib.h>

#include "imgui.h"
#include "imgui_impl_dx11.h"
#include "imgui_impl_win32.h"
#include "stb/stb_client.h"
#include "stb/rcu_keys.h"
#include "stb/crash_log.h"
#include "stb/channel_cache.h"
#include "cccam/cccam_server.h"
#include "cccam/ecm_services.h"
#include "cccam/cccam_config.h"
#include "stb/custom_lists.h"
#include "stb/rtsp_viewer.h"
#include <algorithm>

extern IMGUI_IMPL_API LRESULT
ImGui_ImplWin32_WndProcHandler(HWND,UINT,WPARAM,LPARAM);

// ── File-based crash log ────────────────────────────────────────────────────
// CrashLog is now in stb/crash_log.h (thread-safe, shared across all modules)
using stb::CrashLog;

static LONG WINAPI CrashHandler(EXCEPTION_POINTERS* ep) {
    char buf[512];
    snprintf(buf, sizeof(buf),
        "CRASH: ExceptionCode=0x%08lX  Address=%p",
        ep->ExceptionRecord->ExceptionCode,
        ep->ExceptionRecord->ExceptionAddress);
    CrashLog(buf);

    // Try to get module name
    HMODULE hMod = nullptr;
    GetModuleHandleExA(GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS,
        (LPCSTR)ep->ExceptionRecord->ExceptionAddress, &hMod);
    if (hMod) {
        char modName[MAX_PATH] = {};
        GetModuleFileNameA(hMod, modName, MAX_PATH);
        snprintf(buf, sizeof(buf), "  Module: %s  Offset: 0x%llX",
            modName,
            (unsigned long long)((char*)ep->ExceptionRecord->ExceptionAddress - (char*)hMod));
        CrashLog(buf);
    }

    // Stack context
    auto* ctx = ep->ContextRecord;
#ifdef _WIN64
    snprintf(buf, sizeof(buf),
        "  RIP=%p RSP=%p RBP=%p",
        (void*)ctx->Rip, (void*)ctx->Rsp, (void*)ctx->Rbp);
#else
    snprintf(buf, sizeof(buf),
        "  EIP=%p ESP=%p EBP=%p",
        (void*)ctx->Eip, (void*)ctx->Esp, (void*)ctx->Ebp);
#endif
    CrashLog(buf);
    CrashLog("--- end crash ---");

    return EXCEPTION_EXECUTE_HANDLER;
}

static void InstallFatalHandlers_() {
    try {
        std::set_terminate([]() {
            try { CrashLog("FATAL: std::terminate called"); } catch (...) {}
            TerminateProcess(GetCurrentProcess(), 0xEE);
        });
    } catch (...) {}
    try {
        std::signal(SIGABRT, [](int) {
            try { CrashLog("FATAL: SIGABRT"); } catch (...) {}
            TerminateProcess(GetCurrentProcess(), 0xEA);
        });
    } catch (...) {}
}

static void LogProcessPaths_() {
    try {
        char exe[MAX_PATH] = {};
        DWORD n = GetModuleFileNameA(nullptr, exe, MAX_PATH);
        if (n > 0 && n < MAX_PATH) {
            CrashLog((std::string("EXE: ") + exe).c_str());
        }
    } catch (...) {}
    try {
        char cwd[MAX_PATH] = {};
        DWORD n = GetCurrentDirectoryA(MAX_PATH, cwd);
        if (n > 0 && n < MAX_PATH) {
            CrashLog((std::string("CWD: ") + cwd).c_str());
        }
    } catch (...) {}
}

static double shannonEntropy_(const uint8_t* data, size_t n) {
    if (!data || n == 0) return 0.0;
    uint32_t freq[256] = {};
    for (size_t i = 0; i < n; ++i) freq[data[i]]++;
    double invN = 1.0 / (double)n;
    double ent = 0.0;
    for (int i = 0; i < 256; ++i) {
        if (!freq[i]) continue;
        double p = (double)freq[i] * invN;
        ent -= p * (std::log(p) / std::log(2.0));
    }
    return ent;
}

static inline uint16_t rd16le_(const uint8_t* p) {
    return (uint16_t)(p[0] | (p[1] << 8));
}
static inline uint32_t rd32le_(const uint8_t* p) {
    return (uint32_t)(p[0] | (p[1] << 8) | (p[2] << 16) | (p[3] << 24));
}
static inline uint32_t rd32be_(const uint8_t* p) {
    return (uint32_t)((p[0] << 24) | (p[1] << 16) | (p[2] << 8) | (p[3]));
}

struct FwContainerReport_ {
    bool ok = false;
    std::string err;

    std::string path;
    size_t size = 0;

    uint32_t magic_be = 0;
    bool has_marker_aa41ada3 = false;
    bool has_marker_a3ad41aa = false;

    double entropy_global = 0;
    double entropy_min_64k = 0;
    double entropy_max_64k = 0;

    int zlib_header_candidates = 0;
    int zlib_inflate_ok = 0;

    int jffs2_magic_hits = 0;
    int jffs2_valid_hdr_crc = 0;

    int der_seq_hits = 0;
    int der_plausible = 0;
};

static bool isZlibHeader_(uint8_t cmf, uint8_t flg) {
    if ((cmf & 0x0F) != 8) return false;
    if ((cmf >> 4) > 7) return false;
    uint16_t v = (uint16_t)((cmf << 8) | flg);
    return (v % 31) == 0;
}

static bool tryInflateProbe_(const uint8_t* in, size_t inLen) {
    if (!in || inLen < 8) return false;
    z_stream zs{};
    zs.next_in = (Bytef*)in;
    zs.avail_in = (uInt)std::min<size_t>(inLen, 256 * 1024);

    uint8_t outBuf[32 * 1024] = {};
    zs.next_out = outBuf;
    zs.avail_out = (uInt)sizeof(outBuf);

    int rc = inflateInit(&zs);
    if (rc != Z_OK) return false;
    rc = inflate(&zs, Z_SYNC_FLUSH);
    inflateEnd(&zs);
    if (rc == Z_STREAM_END) return true;
    if (rc == Z_OK && zs.total_out > 0) return true;
    return false;
}

static bool jffs2ValidHdrCrc_(const uint8_t* p, size_t remain) {
    if (!p || remain < 12) return false;
    if (p[0] != 0x85 || p[1] != 0x19) return false;
    uint16_t magic = rd16le_(p);
    if (magic != 0x1985) return false;
    uint32_t hdr_crc = rd32le_(p + 8);
    uLong crc = crc32(0L, Z_NULL, 0);
    crc = crc32(crc, (const Bytef*)p, 8);
    return (uint32_t)crc == hdr_crc;
}

static int derPlausibleCount_(const uint8_t* data, size_t n, int maxChecks = 1000) {
    int plausible = 0;
    int checked = 0;
    for (size_t i = 0; i + 4 < n && checked < maxChecks; ++i) {
        if (data[i] != 0x30) continue;
        uint8_t lb = data[i + 1];
        size_t len = 0;
        size_t lsz = 0;
        if (lb < 0x80) {
            len = lb;
            lsz = 1;
        } else {
            size_t nn = (size_t)(lb & 0x7F);
            if (nn == 0 || nn > 4 || i + 2 + nn >= n) continue;
            len = 0;
            for (size_t k = 0; k < nn; ++k) len = (len << 8) | data[i + 2 + k];
            lsz = 1 + nn;
        }
        size_t hdr = 1 + lsz;
        if (len < 16) continue;
        if (i + hdr + len <= n) plausible++;
        checked++;
    }
    return plausible;
}

static FwContainerReport_ analyzeFwContainer_(const char* path) {
    FwContainerReport_ r;
    if (!path || !path[0]) {
        r.err = "empty path";
        return r;
    }
    r.path = path;

    std::ifstream f(path, std::ios::binary);
    if (!f.good()) {
        r.err = "open failed";
        return r;
    }
    f.seekg(0, std::ios::end);
    std::streamoff sz = f.tellg();
    if (sz <= 0) {
        r.err = "empty file";
        return r;
    }
    f.seekg(0, std::ios::beg);
    r.size = (size_t)sz;
    std::vector<uint8_t> buf(r.size);
    f.read((char*)buf.data(), (std::streamsize)buf.size());
    if (!f.good()) {
        r.err = "read failed";
        return r;
    }

    if (buf.size() >= 12) {
        r.magic_be = rd32be_(buf.data());
        r.has_marker_aa41ada3 = (memcmp(buf.data() + 4, "\xAA\x41\xAD\xA3", 4) == 0);
        r.has_marker_a3ad41aa = (memcmp(buf.data() + 8, "\xA3\xAD\x41\xAA", 4) == 0);
    }

    r.entropy_global = shannonEntropy_(buf.data(), buf.size());
    r.entropy_min_64k = 1e9;
    r.entropy_max_64k = 0;
    for (size_t off = 0; off < buf.size(); off += 64 * 1024) {
        size_t n = std::min<size_t>(64 * 1024, buf.size() - off);
        double e = shannonEntropy_(buf.data() + off, n);
        if (e < r.entropy_min_64k) r.entropy_min_64k = e;
        if (e > r.entropy_max_64k) r.entropy_max_64k = e;
    }

    for (size_t i = 0; i + 2 < buf.size(); ++i) {
        if (buf[i] == 0x78 && (buf[i + 1] == 0x01 || buf[i + 1] == 0x5E || buf[i + 1] == 0x9C || buf[i + 1] == 0xDA)) {
            if (!isZlibHeader_(buf[i], buf[i + 1])) continue;
            r.zlib_header_candidates++;
            if (r.zlib_inflate_ok < 64) {
                if (tryInflateProbe_(buf.data() + i, buf.size() - i))
                    r.zlib_inflate_ok++;
            }
        }
    }

    for (size_t i = 0; i + 2 < buf.size(); ++i) {
        if (buf[i] == 0x85 && buf[i + 1] == 0x19) {
            r.jffs2_magic_hits++;
            if (jffs2ValidHdrCrc_(buf.data() + i, buf.size() - i))
                r.jffs2_valid_hdr_crc++;
        }
    }

    for (size_t i = 0; i + 2 < buf.size(); ++i) {
        if (buf[i] == 0x30 && (buf[i + 1] == 0x81 || buf[i + 1] == 0x82 || buf[i + 1] == 0x83 || buf[i + 1] == 0x84))
            r.der_seq_hits++;
    }
    r.der_plausible = derPlausibleCount_(buf.data(), buf.size());

    r.ok = true;
    return r;
}

static std::string exeDir_() {
#ifdef _WIN32
    char buf[MAX_PATH] = {};
    GetModuleFileNameA(nullptr, buf, MAX_PATH);
    std::string p(buf);
    auto pos = p.find_last_of("\\/");
    if (pos != std::string::npos) p = p.substr(0, pos + 1);
    return p;
#else
    return "";
#endif
}

// ── D3D11 boilerplate ───────────────────────────────────────────────────────
static ID3D11Device*           g_dev=nullptr;
static ID3D11DeviceContext*    g_ctx=nullptr;
static IDXGISwapChain*         g_chain=nullptr;
static ID3D11RenderTargetView* g_rtv=nullptr;

static void MakeRTV() {
    ID3D11Texture2D* b = nullptr;
    if (g_chain && SUCCEEDED(g_chain->GetBuffer(0, IID_PPV_ARGS(&b)))) {
        g_dev->CreateRenderTargetView(b, nullptr, &g_rtv);
        b->Release();
    }
}
static void DropRTV() { if (g_rtv) { g_rtv->Release(); g_rtv = nullptr; } }

static bool InitD3D(HWND hwnd) {
    DXGI_SWAP_CHAIN_DESC sd{};
    sd.BufferCount = 2;
    sd.BufferDesc.Format = DXGI_FORMAT_R8G8B8A8_UNORM;
    sd.BufferDesc.RefreshRate = {60,1};
    sd.BufferUsage = DXGI_USAGE_RENDER_TARGET_OUTPUT;
    sd.OutputWindow = hwnd;
    sd.SampleDesc = {1,0};
    sd.Windowed = TRUE;
    sd.SwapEffect = DXGI_SWAP_EFFECT_DISCARD;
    D3D_FEATURE_LEVEL fl;
    const D3D_FEATURE_LEVEL fla[] = {D3D_FEATURE_LEVEL_11_0, D3D_FEATURE_LEVEL_10_0};
    if (FAILED(D3D11CreateDeviceAndSwapChain(nullptr, D3D_DRIVER_TYPE_HARDWARE, nullptr, 0,
            fla, 2, D3D11_SDK_VERSION, &sd, &g_chain, &g_dev, &fl, &g_ctx)))
        return false;
    MakeRTV();
    return true;
}

static void ShutD3D() {
    DropRTV();
    if (g_chain) { g_chain->Release(); g_chain = nullptr; }
    if (g_ctx)   { g_ctx->Release();   g_ctx   = nullptr; }
    if (g_dev)   { g_dev->Release();   g_dev   = nullptr; }
}

static LRESULT WINAPI WndProc(HWND h, UINT m, WPARAM w, LPARAM l) {
    if (ImGui_ImplWin32_WndProcHandler(h,m,w,l)) return true;
    if (m == WM_SIZE && g_dev && w != SIZE_MINIMIZED) {
        DropRTV();
        g_chain->ResizeBuffers(0, LOWORD(l), HIWORD(l), DXGI_FORMAT_UNKNOWN, 0);
        MakeRTV();
        return 0;
    }
    if (m == WM_DESTROY) { PostQuitMessage(0); return 0; }
    return DefWindowProcW(h,m,w,l);
}

// ── Logger (max 30 KB, thread-safe) ─────────────────────────────────────────
static constexpr size_t LOG_MAX = 30*1024;
struct Logger {
    std::mutex mu;
    std::deque<std::string> lines;
    size_t total = 0;
    bool scroll  = true;

    void add(const std::string& s) {
        try {
            std::lock_guard<std::mutex> g(mu);
            char ts[12]; time_t t = time(nullptr); struct tm tm_{};
            localtime_s(&tm_, &t);
            strftime(ts, sizeof(ts), "%H:%M:%S", &tm_);
            std::string l = std::string(ts) + "  " + s;
            total += l.size() + 1;
            lines.push_back(std::move(l));
            while (total > LOG_MAX && !lines.empty()) {
                total -= lines.front().size() + 1;
                lines.pop_front();
            }
        } catch (...) {}
    }
    void clear() { std::lock_guard<std::mutex> g(mu); lines.clear(); total = 0; }
    std::vector<std::string> snap() {
        std::lock_guard<std::mutex> g(mu);
        return {lines.begin(), lines.end()};
    }
    std::string fullText() {
        std::lock_guard<std::mutex> g(mu);
        std::string o; o.reserve(total);
        for (auto& l : lines) { o += l; o += '\n'; }
        return o;
    }
    size_t bytes() { std::lock_guard<std::mutex> g(mu); return total; }
    int count()    { std::lock_guard<std::mutex> g(mu); return (int)lines.size(); }
};

// ── STB Info snapshot (safe copy) ───────────────────────────────────────────
struct StbInfoSnap {
    std::string stb_model, sw_version, stb_time;
    int channel_count = 0, radio_count = 0;
    int platform_id = 0;
    bool is_4k = false;
    int sat_enable = 0;
    bool uses_json = false;
    bool has_login = false;
};

// ── App State ───────────────────────────────────────────────────────────────
struct App {
    stb::STBClient client;
    std::atomic<bool> down{false};

    // channels (behind chMu)
    std::mutex chMu;
    std::vector<stb::Channel> channels;
    std::atomic<bool> chLoading{false};
    std::atomic<bool> chLoaded{false};
    std::atomic<int>  chPct{0};

    std::atomic<bool> connecting{false};
    std::atomic<bool> discovering{false};

    // discovery (behind discMu)
    std::mutex discMu;
    std::vector<stb::network::UdpDiscovery::DiscoveredDevice> discovered;

    // ── shared strings (behind sMu) ──
    std::mutex  sMu;
    std::string model_, swVer_, connStatus_{"Disconnected"}, stbTime_, discStatus_;
    int chTotal_ = 0, radioTotal_ = 0;

    void setModel(const std::string& v)   { std::lock_guard<std::mutex> g(sMu); model_ = v; }
    void setSwVer(const std::string& v)   { std::lock_guard<std::mutex> g(sMu); swVer_ = v; }
    void setConn(const std::string& v)    { std::lock_guard<std::mutex> g(sMu); connStatus_ = v; }
    void setDisc(const std::string& v)    { std::lock_guard<std::mutex> g(sMu); discStatus_ = v; }
    void setStbTime(const std::string& v) { std::lock_guard<std::mutex> g(sMu); stbTime_ = v; }
    void setCounts(int tv, int r)         { std::lock_guard<std::mutex> g(sMu); chTotal_ = tv; radioTotal_ = r; }

    struct StringSnap {
        std::string model, swVer, connStatus, stbTime, discStatus;
        int chTotal = 0, radioTotal = 0;
    };
    StringSnap snapStrings() {
        std::lock_guard<std::mutex> g(sMu);
        return {model_, swVer_, connStatus_, stbTime_, discStatus_, chTotal_, radioTotal_};
    }

    // STB info snapshot (behind siMu)
    std::mutex siMu;
    StbInfoSnap stbInfo_;
    void updateStbInfo() {
        try {
            std::lock_guard<std::mutex> g(siMu);
            auto& si = client.state().stb_info;
            stbInfo_.stb_model = si.stb_model;
            stbInfo_.sw_version = si.sw_version;
            stbInfo_.stb_time = si.stb_time;
            stbInfo_.channel_count = si.channel_count;
            stbInfo_.radio_count = si.radio_count;
            if (client.loginInfo()) {
                stbInfo_.has_login = true;
                stbInfo_.platform_id = client.loginInfo()->platformId();
                stbInfo_.is_4k = stbInfo_.platform_id >= 70;
                stbInfo_.sat_enable = client.loginInfo()->satEnable();
                stbInfo_.uses_json = client.loginInfo()->usesJson();
            }
        } catch (...) {}
    }
    StbInfoSnap snapStbInfo() {
        std::lock_guard<std::mutex> g(siMu);
        return stbInfo_;
    }

    cccam::CccamServer cccam;
    cccam::EcmHarvester harvester;
    cccam::OfflineCwDb  offlineCwDb;
    cccam::AiTrainer    aiTrainer;
    cccam::ChannelScanner scanner;
    stb::RtspViewer rtspViewer;
    std::mutex liveTuneMu;
    std::atomic<uint64_t> liveTuneSeq{0};
    char rtspUrl[1024] = "rtsp://192.168.1.2:554/";
    Logger log;
    Logger cccamLog;

    // Satellites (behind satMu)
    std::mutex satMu;
    std::vector<stb::Satellite> satellites;

    std::mutex tpMu;
    std::vector<stb::Transponder> transponders;

    std::string rtspProgramId;
    char liveFilter[128] = {};
    int  liveSelectedCh = -1;       // index into channels[] for live TV
    bool liveTuning = false;        // true while STB is tuning (dish may rotate)

    // UI state (main thread only)
    char ip[64]      = "192.168.1.2";
    int  port        = 20000;
    bool autoR       = true;
    char search[128] = {};
    char textInput[256] = {};          // text to send to STB keyboard
    int  sel = -1, tab = 0, discSel = 0;
    bool showLog = false, showCccamLog = false, showCccam = false, showStbInfo = false;
    bool showDetail = false, showSatPanel = false, showRemote = false, showKeyboard = false;
    int  rightTab = 0;     // 0=Remote, 1=Log, 2=My Lists
    int  mainTab  = 0;     // 0=Channels+Live, 1=CCcam+AI, 2=STB Info
    stb::CustomLists customLists;
    bool clDirty = false;  // auto-save trigger
    int  ccaiTab = 0;      // CCcam+AI combined window active tab
    int  detailIdx = -1;           // index into chSnap for detail popup
    int  sortCol = 0;              // 0=index, 1=name, 2=freq, 3=type
    bool sortAsc = true;
    int  filterFav = 0;            // 0=all, 1=fav only, 2=FTA only, 3=scrambled only

    // Recent channels (last 20 played)
    struct RecentChannel {
        std::string service_id;
        std::string name;
        int service_index = 0;
        bool is_radio = false;
    };
    std::vector<RecentChannel> recentChannels;
    static constexpr int MAX_RECENT = 20;
    void addRecentChannel(const std::string& sid, const std::string& name, int idx, bool radio) {
        // Remove duplicates
        recentChannels.erase(
            std::remove_if(recentChannels.begin(), recentChannels.end(),
                [&](const RecentChannel& r){ return r.service_id == sid; }),
            recentChannels.end());
        // Add to front
        recentChannels.insert(recentChannels.begin(), {sid, name, idx, radio});
        if ((int)recentChannels.size() > MAX_RECENT)
            recentChannels.resize(MAX_RECENT);
    }

    // PiP (Picture-in-Picture) mode
    bool pipMode = false;
    int  pipSize = 1;     // 0=small(320x180), 1=medium(480x270), 2=large(640x360)
    HWND mainHwnd = nullptr; // stored for SetWindowPos calls
    RECT pipSavedRect = {};  // original window rect before PiP
    LONG_PTR pipSavedStyle = 0;
    LONG_PTR pipSavedExStyle = 0;
    bool pipWindowStyleSaved = false;
    
    // Cross-reference channels with satellite list to fill satellite_name
    void crossRefSatellites() {
        std::lock_guard<std::mutex> g1(chMu);
        std::lock_guard<std::mutex> g2(satMu);
        if (satellites.empty()) return;
        int fixed = 0;
        for (auto& ch : channels) {
            int si = ch.satIndex();
            if (si >= 0 && ch.satellite_name.empty()) {
                ch.satellite_index = si;
                for (const auto& sat : satellites) {
                    if (sat.sat_index == si) {
                        ch.satellite_name = sat.sat_name;
                        fixed++;
                        break;
                    }
                }
            }
        }
        if (fixed > 0)
            log.add("[ch] Cross-referenced " + std::to_string(fixed) + " satellite names");
    }

    // Persistent cache helpers
    void loadCachedChannels() {
        try {
            auto cached = stb::ChannelCache::load();
            if (!cached.empty()) {
                std::lock_guard<std::mutex> g(chMu);
                channels = std::move(cached);
                chLoaded = true;

                // If cache contains truncated AudioArray/SubtArray (older parser bug),
                // auto-refresh from STB after connecting so apid/pids become correct.
                int checked = 0, bad = 0;
                for (const auto& ch : channels) {
                    if (checked >= 200) break;
                    checked++;
                    if (!ch.audio_pids_raw.empty()) {
                        // Common broken cache signature: "[{" (truncated nested JSON)
                        if (ch.audio_pids_raw.size() <= 3 && ch.audio_pids_raw.find("[") != std::string::npos) bad++;
                        else if (ch.audio_pids_raw.find("pid") == std::string::npos &&
                                 ch.audio_pids_raw.find("PID") == std::string::npos) bad++;
                    } else {
                        bad++;
                    }
                }
                if (checked > 0 && bad * 100 / checked >= 80) {
                    chLoaded = false;
                    log.add("[cache] Channel cache missing audio PIDs — will refresh from STB");
                }
                auto msg = "[cache] Loaded " + std::to_string(channels.size()) + " channels from disk";
                log.add(msg);
                CrashLog(msg.c_str());
            }
        } catch (...) {}
    }
    void saveCachedChannels() {
        try {
            std::vector<stb::Channel> snap;
            { std::lock_guard<std::mutex> g(chMu); snap = channels; }
            if (!snap.empty()) {
                stb::ChannelCache::save(snap);
                log.add("[cache] Saved " + std::to_string(snap.size()) + " channels to disk");
            }
        } catch (...) {}
    }

    App() {
        CrashLog("App constructor");
        client.setAutoReconnect(true);

        client.setConnectionCallback([this](stb::ConnectionState s, const std::string& info) {
            try {
                if (down) return;
                setConn(info);
                log.add("[conn] " + info);
                if (s == stb::ConnectionState::Authenticated) {
                    connecting = false;
                    try {
                        if (client.loginInfo())
                            setModel(client.loginInfo()->modelName());
                    } catch (...) {}
                    try {
                        if (!rtspViewer.isRunning()) {
                            try { client.requestStbInfo(); } catch (...) {}
                            try { client.requestSatelliteList(); } catch (...) {}
                            try { client.requestTransponderList(); } catch (...) {}
                            try { client.requestFavGroupNames(); } catch (...) {}
                        }
                    } catch (...) {}
                }
                if (s == stb::ConnectionState::Disconnected ||
                    s == stb::ConnectionState::ConnectionFailed)
                    connecting = false;
            } catch (const std::exception& e) {
                CrashLog((std::string("connectionCallback exception: ") + e.what()).c_str());
            } catch (...) {
                CrashLog("connectionCallback unknown exception");
            }
        });

        client.setChannelListCallback([this](int count, int total, bool complete) {
            try {
                if (down) return;
                chPct = (total > 0) ? (count * 100 / total) : (complete ? 100 : 0);
                if (complete) {
                    chLoading = false;
                    chLoaded = true;
                    try {
                        std::lock_guard<std::mutex> g(chMu);
                        channels = client.state().channels;
                    } catch (...) {}
                    log.add("[ch] Loaded " + std::to_string(count) + " channels");
                }
            } catch (const std::exception& e) {
                CrashLog((std::string("channelListCallback exception: ") + e.what()).c_str());
            } catch (...) {
                CrashLog("channelListCallback unknown exception");
            }
        });

        client.setNotificationCallback([this](const std::string& ev, const std::string& data) {
            try {
                if (down) return;
                if (ev == "stb_info") {
                    try {
                        auto& si = client.state().stb_info;
                        setSwVer(si.sw_version);
                        setStbTime(si.stb_time);
                        setCounts(si.channel_count, si.radio_count);
                        {
                            std::lock_guard<std::mutex> g(sMu);
                            if (model_.empty()) model_ = si.stb_model;
                        }
                        updateStbInfo();
                    } catch (...) {}
                }
                if (ev == "channel_list_complete") {
                    chLoading = false;
                    chLoaded = true;
                    try {
                        std::lock_guard<std::mutex> g(chMu);
                        channels = client.state().channels;
                    } catch (...) {}
                    crossRefSatellites();
                    saveCachedChannels();
                }
                if (ev == "satellite_list") {
                    try {
                        std::lock_guard<std::mutex> g(satMu);
                        satellites = client.state().satellites;
                    } catch (...) {}
                    crossRefSatellites();
                }
                if (ev == "transponder_list") {
                    try {
                        std::lock_guard<std::mutex> g(tpMu);
                        transponders = client.state().transponders;
                    } catch (...) {}
                }
                if (ev == "sat2ip_return") {
                    log.add("[SAT2IP] return " + data);
                }
            } catch (const std::exception& e) {
                CrashLog((std::string("notificationCallback exception: ") + e.what()).c_str());
            } catch (...) {
                CrashLog("notificationCallback unknown exception");
            }
        });
    }

    ~App() {
        CrashLog("App destructor start");
        down = true;
        try {
            ++liveTuneSeq;
            std::lock_guard<std::mutex> tuneGuard(liveTuneMu);
            rtspViewer.stop();
        } catch (...) {}
        try { scanner.stop(); } catch (...) {}
        try { harvester.stop(); } catch (...) {}
        try { aiTrainer.stop(); } catch (...) {}
        try { offlineCwDb.save(); } catch (...) {}
        try { cccam.caEngine.save(); } catch (...) {}
        try { cccam.stop(); } catch (...) {}
        try { client.disconnect(); } catch (...) {}
        std::this_thread::sleep_for(std::chrono::milliseconds(300));
        CrashLog("App destructor done");
    }
};

// ── Style ───────────────────────────────────────────────────────────────────
static void ApplyStyle(float dpi) {
    ImGuiStyle& s = ImGui::GetStyle();
    // Geometry — tight, modern, minimal
    s.WindowPadding     = {10, 8};
    s.FramePadding      = {8, 4};
    s.CellPadding       = {6, 3};
    s.ItemSpacing       = {8, 4};
    s.ItemInnerSpacing  = {6, 4};
    s.ScrollbarSize     = 10;
    s.GrabMinSize       = 8;
    s.WindowBorderSize  = 1;
    s.ChildBorderSize   = 1;
    s.FrameBorderSize   = 0;
    s.TabBorderSize     = 0;
    s.WindowRounding    = 6;
    s.ChildRounding     = 4;
    s.FrameRounding     = 5;
    s.PopupRounding     = 6;
    s.ScrollbarRounding = 6;
    s.GrabRounding      = 4;
    s.TabRounding       = 5;
    s.IndentSpacing     = 16;

    auto* c = s.Colors;

    // ── Catppuccin Mocha palette ──────────────────────────────────────
    // Base layers
    const float bx = 0.118f, by = 0.118f, bz = 0.180f; // #1e1e2e  base
    const float mx = 0.094f, my = 0.094f, mz = 0.145f; // #181825  mantle
    const float s0x= 0.192f, s0y= 0.196f, s0z= 0.267f; // #313244  surface0
    const float s1x= 0.271f, s1y= 0.278f, s1z= 0.353f; // #45475a  surface1
    const float s2x= 0.345f, s2y= 0.353f, s2z= 0.431f; // #585b70  surface2
    const float o0x= 0.424f, o0y= 0.439f, o0z= 0.525f; // #6c7086  overlay0
    // Accents
    const float blx= 0.537f, bly= 0.706f, blz= 0.980f; // #89b4fa  blue

    // ── Window backgrounds ──
    c[ImGuiCol_WindowBg]             = {bx, by, bz, 0.98f};
    c[ImGuiCol_ChildBg]              = {mx, my, mz, 0.45f};
    c[ImGuiCol_PopupBg]              = {mx, my, mz, 0.96f};
    c[ImGuiCol_Border]               = {s1x, s1y, s1z, 0.45f};
    c[ImGuiCol_BorderShadow]         = {0, 0, 0, 0};

    // ── Text ──
    c[ImGuiCol_Text]                 = {0.804f, 0.839f, 0.957f, 1.00f}; // #cdd6f4
    c[ImGuiCol_TextDisabled]         = {0.549f, 0.573f, 0.675f, 1.00f}; // #8c92ac

    // ── Frames (inputs, combos, sliders) ──
    c[ImGuiCol_FrameBg]              = {s0x, s0y, s0z, 0.55f};
    c[ImGuiCol_FrameBgHovered]       = {s1x, s1y, s1z, 0.65f};
    c[ImGuiCol_FrameBgActive]        = {s2x, s2y, s2z, 0.75f};

    // ── Title bar ──
    c[ImGuiCol_TitleBg]              = {mx, my, mz, 1.00f};
    c[ImGuiCol_TitleBgActive]        = {s0x, s0y, s0z, 1.00f};
    c[ImGuiCol_TitleBgCollapsed]     = {mx, my, mz, 0.60f};
    c[ImGuiCol_MenuBarBg]            = {mx, my, mz, 1.00f};

    // ── Buttons — blue accent, subtle ──
    c[ImGuiCol_Button]               = {blx*0.28f, bly*0.28f, blz*0.28f, 0.65f};
    c[ImGuiCol_ButtonHovered]        = {blx*0.40f, bly*0.40f, blz*0.40f, 0.80f};
    c[ImGuiCol_ButtonActive]         = {blx*0.55f, bly*0.55f, blz*0.55f, 1.00f};

    // ── Headers (collapsing headers, tree nodes) ──
    c[ImGuiCol_Header]               = {s0x, s0y, s0z, 0.55f};
    c[ImGuiCol_HeaderHovered]        = {blx*0.25f, bly*0.25f, blz*0.25f, 0.60f};
    c[ImGuiCol_HeaderActive]         = {blx*0.35f, bly*0.35f, blz*0.35f, 0.80f};

    // ── Tabs ──
    c[ImGuiCol_Tab]                  = {s0x, s0y, s0z, 0.75f};
    c[ImGuiCol_TabHovered]           = {blx*0.35f, bly*0.35f, blz*0.35f, 0.80f};
    c[ImGuiCol_TabActive]            = {blx*0.25f, bly*0.25f, blz*0.25f, 1.00f};
    c[ImGuiCol_TabDimmed]            = {mx, my, mz, 0.80f};
    c[ImGuiCol_TabDimmedSelected]    = {s0x, s0y, s0z, 1.00f};

    // ── Tables ──
    c[ImGuiCol_TableHeaderBg]        = {s0x, s0y, s0z, 1.00f};
    c[ImGuiCol_TableBorderStrong]    = {s1x, s1y, s1z, 0.70f};
    c[ImGuiCol_TableBorderLight]     = {s0x, s0y, s0z, 0.50f};
    c[ImGuiCol_TableRowBg]           = {0, 0, 0, 0};
    c[ImGuiCol_TableRowBgAlt]        = {1, 1, 1, 0.018f};

    // ── Scrollbar ──
    c[ImGuiCol_ScrollbarBg]          = {mx, my, mz, 0.40f};
    c[ImGuiCol_ScrollbarGrab]        = {s1x, s1y, s1z, 0.65f};
    c[ImGuiCol_ScrollbarGrabHovered] = {s2x, s2y, s2z, 0.80f};
    c[ImGuiCol_ScrollbarGrabActive]  = {o0x, o0y, o0z, 1.00f};

    // ── Misc controls ──
    c[ImGuiCol_CheckMark]            = {blx, bly, blz, 1.00f};
    c[ImGuiCol_SliderGrab]           = {blx*0.50f, bly*0.50f, blz*0.50f, 1.00f};
    c[ImGuiCol_SliderGrabActive]     = {blx, bly, blz, 1.00f};
    c[ImGuiCol_Separator]            = {s1x, s1y, s1z, 0.55f};
    c[ImGuiCol_SeparatorHovered]     = {blx*0.40f, bly*0.40f, blz*0.40f, 0.70f};
    c[ImGuiCol_SeparatorActive]      = {blx, bly, blz, 1.00f};
    c[ImGuiCol_ResizeGrip]           = {s1x, s1y, s1z, 0.25f};
    c[ImGuiCol_ResizeGripHovered]    = {blx*0.40f, bly*0.40f, blz*0.40f, 0.55f};
    c[ImGuiCol_ResizeGripActive]     = {blx*0.55f, bly*0.55f, blz*0.55f, 0.90f};
    c[ImGuiCol_TextSelectedBg]       = {blx*0.25f, bly*0.25f, blz*0.25f, 0.50f};
    c[ImGuiCol_NavHighlight]         = {blx, bly, blz, 1.00f};

    s.ScaleAllSizes(dpi);
}

static bool StrMatch(const char* t, const char* f) {
    if (!f || !f[0]) return true;
    if (!t || !t[0]) return false;
    for (; *t; ++t) {
        const char* a = t; const char* b = f;
        while (*a && *b && tolower((unsigned char)*a) == tolower((unsigned char)*b)) { ++a; ++b; }
        if (!*b) return true;
    }
    return false;
}

static const char* ConnStr(stb::ConnectionState s) {
    switch (s) {
    case stb::ConnectionState::Disconnected:     return "Disconnected";
    case stb::ConnectionState::Connecting:       return "Connecting...";
    case stb::ConnectionState::Connected:        return "Connected";
    case stb::ConnectionState::Authenticated:    return "Ready";
    case stb::ConnectionState::Reconnecting:     return "Reconnecting...";
    case stb::ConnectionState::ConnectionFailed: return "Failed";
    default: return "Unknown";
    }
}

// ── Main ────────────────────────────────────────────────────────────────────
int RunImGuiApp(HINSTANCE hInstance, int nCmdShow) {
    // Install crash handler FIRST
    SetUnhandledExceptionFilter(CrashHandler);
    CrashLog("=== GMScreen starting ===");
    InstallFatalHandlers_();
    LogProcessPaths_();

    SetProcessDPIAware();

    WNDCLASSEXW wc{};
    wc.cbSize = sizeof(wc);
    wc.style = CS_CLASSDC;
    wc.lpfnWndProc = WndProc;
    wc.hInstance = hInstance;
    wc.lpszClassName = L"GMScreen6";
    wc.hCursor = LoadCursor(nullptr, IDC_ARROW);
    RegisterClassExW(&wc);

    HWND hwnd = CreateWindowW(wc.lpszClassName,
        L"GMScreen \u2014 MediaStar 4030 4K",
        WS_OVERLAPPEDWINDOW, 80, 50, 1300, 820,
        nullptr, nullptr, wc.hInstance, nullptr);

    if (!InitD3D(hwnd)) {
        CrashLog("InitD3D failed");
        return 1;
    }
    ShowWindow(hwnd, nCmdShow);
    UpdateWindow(hwnd);

    IMGUI_CHECKVERSION();
    ImGui::CreateContext();
    ImGuiIO& io = ImGui::GetIO();
    io.ConfigFlags |= ImGuiConfigFlags_NavEnableKeyboard;
    io.IniFilename = nullptr;

    float dpi = 1.0f;
    if (HDC hdc = GetDC(hwnd)) {
        dpi = GetDeviceCaps(hdc, LOGPIXELSX) / 96.0f;
        ReleaseDC(hwnd, hdc);
    }
    if (dpi < 0.5f) dpi = 1.0f;
    if (dpi > 4.0f) dpi = 4.0f;

    {
        char p[MAX_PATH]{};
        GetWindowsDirectoryA(p, MAX_PATH);
        strcat_s(p, "\\Fonts\\segoeui.ttf");
        ImFontConfig fc; fc.OversampleH = 3; fc.OversampleV = 2;
        if (!io.Fonts->AddFontFromFileTTF(p, 15.0f * dpi, &fc)) {
            ImFontConfig f2; f2.SizePixels = 15.0f * dpi;
            f2.OversampleH = 2; f2.OversampleV = 2; f2.PixelSnapH = true;
            io.Fonts->AddFontDefault(&f2);
        }
    }

    ApplyStyle(dpi);
    ImGui_ImplWin32_Init(hwnd);
    ImGui_ImplDX11_Init(g_dev, g_ctx);

    App* rawApp = nullptr;
    try {
        rawApp = new App();
    } catch (const std::exception& e) {
        CrashLog((std::string("App() constructor threw: ") + e.what()).c_str());
        return 1;
    }
    // shared_ptr so detached threads keep App alive
    std::shared_ptr<App> app(rawApp);
    app->mainHwnd = hwnd;
    app->log.add("my4030 started - MediaStar 4030 4K");
    app->loadCachedChannels();  // Load persistent cache from disk
    app->customLists.load();    // Load custom channel lists
    
    // Initialize RTSP viewer with D3D11 device
    if (!app->rtspViewer.init(g_dev, g_ctx)) {
        CrashLog("RTSP viewer init failed (D3D11)");
    }

    // ── Load CCcam config from disk ──
    {
        cccam::CccamConfig ccfg;
        bool configLoaded = ccfg.load();
        if (configLoaded) {
            app->cccam.cfg.port    = ccfg.port;
            app->cccam.cfg.user    = ccfg.user;
            app->cccam.cfg.pass    = ccfg.pass;
            app->cccam.cfg.log_ecm = ccfg.log_ecm;
            app->cccam.cfg.global_proxy_host = ccfg.global_proxy_host;
            app->cccam.cfg.global_proxy_port = ccfg.global_proxy_port;
            app->cccam.cfg.global_proxy_user = ccfg.global_proxy_user;
            app->cccam.cfg.global_proxy_pass = ccfg.global_proxy_pass;
            app->cccam.cfg.servers = ccfg.servers;
            app->cccam.cfg.applyGlobalProxy();
            CrashLog(("[CCcam] Config loaded, " + std::to_string(ccfg.servers.size()) + " servers").c_str());
        }
        
        // Add default servers if config is empty or missing
        if (!configLoaded || app->cccam.cfg.servers.empty()) {
            std::vector<cccam::UpstreamServer> defaults = {
                {"xy.cccamfrei.com", "xy.cccamfrei.com", 15700, "s6745ax", "fi3in", true},
                {"streamtveuropa.sytes.net", "streamtveuropa.sytes.net", 13090, "Channels_Online", "streamtveuropa.com", true},
                {"GeoForum", "193.34.144.135", 53053, "bokipan", "GeoForum", true},
                {"s8.starcline.com", "s8.starcline.com", 51002, "fei4328", "libyanet", true},
                {"Cline-sat", "cardsharing-sat.camdvr.org", 14000, "Cline-sat", "www.cardsharing-sat.com", true},
                {"free.cccam.net", "free.cccam.net", 22210, "xfioNOD4", "cccam.net", true},
                {"CCcam-service", "cardsharing-sat.camdvr.org", 14001, "CCcam-service", "www.cardsharing-sat.com", true}
            };
            app->cccam.cfg.servers = defaults;
            CrashLog(("[CCcam] Added " + std::to_string(defaults.size()) + " default servers").c_str());
        }
    }

    // ── Auto-start CCcam server ──
    try {
        // Make sure global proxy is applied before starting runtime connections
        app->cccam.cfg.applyGlobalProxy();
        app->cccam.start([&app](const std::string& msg) {
            try { if (app && !app->down) app->cccamLog.add(msg); } catch (...) {}
        });
    } catch (...) { CrashLog("CCcam auto-start failed"); }

    // ── Wire & start background services ──
    try {
        // Load offline CW database + CA engine
        app->offlineCwDb.load();
        app->cccam.caEngine.load();
        app->cccam.caEngine.onLog = [&app](const std::string& msg) {
            try { if (app && !app->down) app->cccamLog.add(msg); } catch (...) {}
        };

        // Wire ECM capture: feed harvester targets from live STB traffic
        app->cccam.onEcmCapture = [&app](uint16_t caid, uint32_t provid, uint16_t sid,
                                          const uint8_t* msg, int len) {
            try { app->harvester.addTarget(caid, provid, sid, msg, len); } catch (...) {}
        };

        // Wire offline CW lookup: fallback when upstreams are down
        app->cccam.onOfflineLookup = [&app](uint16_t caid, uint16_t sid,
                                             const uint8_t* ecm, int ecmLen, uint8_t* cwOut) -> bool {
            try { return app->offlineCwDb.lookup(caid, sid, ecm, ecmLen, cwOut); } catch (...) { return false; }
        };

        // Harvester: when CW obtained, feed learner + predictor + offline DB
        app->harvester.onLog = [&app](const std::string& msg) {
            try { if (app && !app->down) app->cccamLog.add(msg); } catch (...) {}
        };
        app->harvester.onResult = [&app](uint16_t caid, uint32_t provid, uint16_t sid,
                                          const uint8_t* ecm, int ecmLen, bool ok,
                                          const uint8_t* cw, const std::string& srv, float lat) {
            try {
                if (ok && ecm && ecmLen > 0) {
                    app->cccam.learner.addSample(caid, provid, sid, ecm, ecmLen, ok, cw, srv);
                    app->cccam.aiPredictor.learn(caid, sid, provid, ecm, ecmLen, cw, ok, srv, lat);
                    app->cccam.caEngine.learn(caid, sid, provid, ecm, ecmLen, cw, ok, srv, lat);
                    app->offlineCwDb.store(caid, sid, provid, ecm, ecmLen, cw, srv);
                }
            } catch (...) {}
        };
        app->harvester.start(&app->cccam.cfg.servers);

        // AI Trainer: periodic model analysis & saving
        app->aiTrainer.onLog = [&app](const std::string& msg) {
            try { if (app && !app->down) app->cccamLog.add(msg); } catch (...) {}
        };
        app->aiTrainer.predictor = &app->cccam.aiPredictor;
        app->aiTrainer.learner = &app->cccam.learner;
        app->aiTrainer.offlineDb = &app->offlineCwDb;
        app->aiTrainer.start();

        // ── Channel Scanner: auto-scan encrypted channels, collect samples ──
        app->scanner.onLog = [&app](const std::string& msg) {
            try { if (app && !app->down) app->log.add(msg); } catch (...) {}
        };
        // Switch STB channel
        app->scanner.onSwitchChannel = [&app](const std::string& svcId, int tvState) -> bool {
            try {
                bool ok = app->client.changeChannelDirect(svcId, tvState);
                if (ok) {
                    // Double-send after 700ms for reliable switching
                    std::string sid = svcId; int ts = tvState;
                    std::thread([&app, sid, ts]() {
                        std::this_thread::sleep_for(std::chrono::milliseconds(700));
                        try { app->client.changeChannelDirect(sid, ts); } catch (...) {}
                    }).detach();
                }
                return ok;
            } catch (...) { return false; }
        };
        // Feed collected samples to all tools
        app->scanner.onSampleCollected = [&app](const cccam::ScanSample& s) {
            try {
                if (s.cw_ok) {
                    app->cccam.learner.addSample(s.caid, s.provid, s.sid,
                        nullptr, 0, true, s.cw.data(), s.server);
                    app->cccam.aiPredictor.learn(s.caid, s.sid, s.provid,
                        nullptr, 0, s.cw.data(), true, s.server, s.latency_ms);
                    app->cccam.caEngine.learn(s.caid, s.sid, s.provid,
                        nullptr, 0, s.cw.data(), true, s.server, s.latency_ms);
                    app->offlineCwDb.store(s.caid, s.sid, s.provid,
                        nullptr, 0, s.cw.data(), s.server);
                }
            } catch (...) {}
        };
        // Wire CccamServer → Scanner notification (ECM results for signal quality)
        app->cccam.onScannerNotify = [&app](uint16_t caid, uint32_t provid, uint16_t sid,
                                             bool ok, const uint8_t* cw, float latMs,
                                             const std::string& srv) {
            try { app->scanner.notifyEcmResult(caid, provid, sid, ok, cw, latMs, srv); } catch (...) {}
        };

        CrashLog("Background services started (harvester + trainer + offline DB + scanner)");
    } catch (...) { CrashLog("Background services init failed"); }

    // Start on CCcam + AI tab
    app->mainTab = 1;
    app->ccaiTab = 0;

    // ── Auto-connect to STB ──
    {
        auto a = app;
        a->connecting = true;
        a->log.add("Connecting to " + std::string(a->ip) + ":" + std::to_string(a->port));
        std::thread([a]() {
            try {
                a->client.connect(a->ip, a->port);
                // Auto-load channels only if no disk cache was loaded
                if (a->client.isConnected() && !a->chLoading && !a->chLoaded) {
                    a->chLoading = true;
                    a->log.add("Loading channels from STB...");
                    std::thread([a]() {
                        try { a->client.requestChannelList(); }
                        catch (...) {}
                        a->chLoading = false;
                    }).detach();
                } else if (a->client.isConnected() && a->chLoaded) {
                    a->log.add("Using cached channels (" + std::to_string(a->channels.size()) + ")");
                }
            } catch (...) {}
            a->connecting = false;
        }).detach();
    }

    CrashLog("Main loop starting");

    bool quit = false;
    int frameNum = 0;

    while (!quit) {
        MSG msg;
        while (PeekMessageW(&msg, nullptr, 0, 0, PM_REMOVE)) {
            TranslateMessage(&msg);
            DispatchMessageW(&msg);
            if (msg.message == WM_QUIT) quit = true;
        }
        if (quit) break;

        frameNum++;

        try {
            ImGui_ImplDX11_NewFrame();
            ImGui_ImplWin32_NewFrame();
            ImGui::NewFrame();
        } catch (...) {
            CrashLog("ImGui NewFrame exception");
            break;
        }

        auto* vp = ImGui::GetMainViewport();
        float VW = vp->WorkSize.x, VH = vp->WorkSize.y;
        float OX = vp->WorkPos.x,  OY = vp->WorkPos.y;
        float P = 4.0f;
        float LW = VW * 0.66f - P;
        float RW = VW - LW - P * 3;
        float RX = OX + LW + P * 2;
        float CONN_H = 130.0f * dpi;
        float CH_H = VH - CONN_H - P * 3;
        float RPH = VH - P * 2;  // right panel full height

        // ── Periodic keepalive: send every 30s when connected and idle ──
        {
            static time_t last_ka = 0;
            static std::atomic<bool> ka_busy{false};
            time_t now_t = time(nullptr);
            if (now_t - last_ka >= 10) {
                last_ka = now_t;
                try {
                    if (app->client.isConnected() &&
                        (app->rtspViewer.isRunning() || app->client.millisSinceLastActivity() > 8000)) {
                        if (!ka_busy.exchange(true)) {
                            std::weak_ptr<App> wapp = app;
                            std::thread([wapp]{
                                try {
                                    auto sp = wapp.lock();
                                    if (!sp || sp->down) return;
                                    sp->client.sendKeepAlive();
                                } catch (...) {}
                                try { ka_busy = false; } catch (...) {}
                            }).detach();
                        }
                    }
                } catch (...) {}
            }
        }

        // ── Snapshot ALL shared state ONCE per frame (safe) ──
        bool connected = false;
        stb::ConnectionState connSt = stb::ConnectionState::Disconnected;
        try {
            connected = app->client.isConnected();
            connSt = app->client.getConnectionState();
        } catch (...) {}

        bool busyConn = app->connecting.load();
        bool busyDisc = app->discovering.load();
        bool busyCh   = app->chLoading.load();
        auto ss       = app->snapStrings();

        auto applyPipChrome = [&]() {
            if (!app->mainHwnd) return;
            LONG_PTR style = GetWindowLongPtr(app->mainHwnd, GWL_STYLE);
            LONG_PTR exStyle = GetWindowLongPtr(app->mainHwnd, GWL_EXSTYLE);
            if (!app->pipWindowStyleSaved) {
                app->pipSavedStyle = style;
                app->pipSavedExStyle = exStyle;
                app->pipWindowStyleSaved = true;
            }
            style &= ~(WS_CAPTION | WS_THICKFRAME | WS_MINIMIZEBOX | WS_MAXIMIZEBOX | WS_SYSMENU);
            style |= WS_POPUP | WS_VISIBLE;
            exStyle &= ~(WS_EX_DLGMODALFRAME | WS_EX_CLIENTEDGE | WS_EX_STATICEDGE | WS_EX_WINDOWEDGE | WS_EX_APPWINDOW);
            exStyle |= WS_EX_TOOLWINDOW;
            SetWindowLongPtr(app->mainHwnd, GWL_STYLE, style);
            SetWindowLongPtr(app->mainHwnd, GWL_EXSTYLE, exStyle);
        };
        auto restorePipChrome = [&]() {
            if (!app->mainHwnd || !app->pipWindowStyleSaved) return;
            SetWindowLongPtr(app->mainHwnd, GWL_STYLE, app->pipSavedStyle);
            SetWindowLongPtr(app->mainHwnd, GWL_EXSTYLE, app->pipSavedExStyle);
        };
        auto applyPipRect = [&]() {
            if (!app->mainHwnd) return;
            const int szW[] = {400, 600, 840};
            const int szH[] = {260, 380, 520};
            int si = app->pipSize;
            if (si < 0 || si > 2) si = 1;
            int sx = GetSystemMetrics(SM_CXSCREEN);
            int sy = GetSystemMetrics(SM_CYSCREEN);
            SetWindowPos(app->mainHwnd, HWND_TOPMOST,
                sx - szW[si] - 10, sy - szH[si] - 50, szW[si], szH[si],
                SWP_FRAMECHANGED | SWP_SHOWWINDOW);
        };
        auto exitPip = [&]() {
            app->pipMode = false;
            restorePipChrome();
            RECT& r = app->pipSavedRect;
            if (!app->mainHwnd) return;
            if (r.right > r.left && r.bottom > r.top) {
                SetWindowPos(app->mainHwnd, HWND_NOTOPMOST,
                    r.left, r.top, r.right - r.left, r.bottom - r.top,
                    SWP_FRAMECHANGED | SWP_SHOWWINDOW);
            } else {
                SetWindowPos(app->mainHwnd, HWND_NOTOPMOST, 0, 0, 0, 0,
                    SWP_NOMOVE | SWP_NOSIZE | SWP_FRAMECHANGED | SWP_SHOWWINDOW);
            }
        };
        auto enterPip = [&]() {
            if (!app->mainHwnd) return;
            if (!app->pipMode) GetWindowRect(app->mainHwnd, &app->pipSavedRect);
            app->pipMode = true;
            applyPipChrome();
            applyPipRect();
        };
        auto findChannelIndexForEntry = [&](const stb::CustomListEntry& entry) -> int {
            std::lock_guard<std::mutex> g(app->chMu);
            if (!entry.service_id.empty()) {
                for (int i = 0; i < (int)app->channels.size(); i++) {
                    const auto& ch = app->channels[i];
                    if (ch.service_id == entry.service_id) return i;
                    if (!ch.program_id.empty() && ch.program_id == entry.service_id) return i;
                }
            }
            if (entry.service_index >= 0) {
                for (int i = 0; i < (int)app->channels.size(); i++) {
                    const auto& ch = app->channels[i];
                    if (ch.service_index == entry.service_index && ch.is_radio == entry.is_radio) return i;
                }
            }
            return -1;
        };
        auto startLiveChannelByIndex = [&](int channelIdx) -> bool {
            if (channelIdx < 0) return false;
            app->mainTab = 0;
            app->liveSelectedCh = channelIdx;
            app->liveTuning = true;
            std::string sid, chName, url;
            bool chRadio = false;
            int chSvcIdx = 0;
            {
                std::lock_guard<std::mutex> g(app->chMu);
                if (channelIdx >= (int)app->channels.size()) {
                    app->liveTuning = false;
                    return false;
                }
                const auto& ch = app->channels[channelIdx];
                sid = !ch.service_id.empty() ? ch.service_id : ch.program_id;
                chName = ch.service_name;
                chRadio = ch.is_radio;
                chSvcIdx = ch.service_index;
                auto cfg = stb::RtspViewer::extractChannelParams(ch);
                {
                    std::lock_guard<std::mutex> gt(app->tpMu);
                    for (const auto& tp : app->transponders) {
                        if (tp.sat_index == cfg.sat_index && tp.tp_index == cfg.tp_index) {
                            cfg.freq = tp.freq;
                            cfg.sym_rate = tp.sym_rate;
                            cfg.fec = tp.fec;
                            cfg.pol = tp.pol;
                            break;
                        }
                    }
                }
                url = stb::RtspViewer::generateMediaStarUrl(app->ip, cfg);
            }
            if (sid.empty()) sid = std::to_string(chSvcIdx);
            if (url.empty()) {
                app->liveTuning = false;
                return false;
            }
            app->addRecentChannel(sid, chName, chSvcIdx, chRadio);
            snprintf(app->rtspUrl, sizeof(app->rtspUrl), "%s", url.c_str());
            app->rtspProgramId = sid;
            app->log.add("[LiveTV] " + chName);
            std::weak_ptr<App> wapp = app;
            std::string urlCopy = url;
            uint64_t tuneSeq = ++app->liveTuneSeq;
            std::thread([wapp, urlCopy, tuneSeq]{
                try {
                    auto sp = wapp.lock(); if (!sp || sp->down) return;
                    std::lock_guard<std::mutex> tuneGuard(sp->liveTuneMu);
                    if (sp->down || tuneSeq != sp->liveTuneSeq.load()) {
                        sp->liveTuning = false;
                        return;
                    }
                    sp->rtspViewer.stop();
                    try { if (sp->client.isConnected()) sp->client.sat2ipStop(); } catch (...) {}
                    std::this_thread::sleep_for(std::chrono::milliseconds(120));
                    if (sp->down || tuneSeq != sp->liveTuneSeq.load()) {
                        sp->liveTuning = false;
                        return;
                    }
                    sp->rtspViewer.onLog = [wapp](const std::string& msg) {
                        try { auto s2 = wapp.lock(); if (s2 && !s2->down) s2->log.add(msg); } catch (...) {}
                    };
                    stb::RtspViewerConfig rcfg;
                    rcfg.url = urlCopy;
                    rcfg.min_buffer_ms = 500;
                    sp->rtspViewer.start(rcfg);
                    if (tuneSeq == sp->liveTuneSeq.load()) sp->liveTuning = false;
                } catch (...) {
                    try { auto sp = wapp.lock(); if (sp) sp->liveTuning = false; } catch (...) {}
                }
            }).detach();
            return true;
        };

        // ════════════════════════════════════════════════════════════════
        // PiP MODE: borderless video-only, overlay on hover
        // ════════════════════════════════════════════════════════════════
        if (app->pipMode) {
            app->rtspViewer.pump();
            auto& rvs = app->rtspViewer.getStats();
            bool pipHasVideo = app->rtspViewer.hasVideo();
            bool pipRunning  = app->rtspViewer.isRunning();

            // Zero-chrome video window: no border, no padding, no rounding, black bg
            ImGui::PushStyleVar(ImGuiStyleVar_WindowPadding, {0, 0});
            ImGui::PushStyleVar(ImGuiStyleVar_WindowBorderSize, 0.0f);
            ImGui::PushStyleVar(ImGuiStyleVar_WindowRounding, 0.0f);
            ImGui::PushStyleColor(ImGuiCol_WindowBg, ImVec4{0, 0, 0, 1});
            ImGui::SetNextWindowPos({OX, OY});
            ImGui::SetNextWindowSize({VW, VH});
            ImGui::Begin("##pip_video", nullptr,
                ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoMove | ImGuiWindowFlags_NoResize |
                ImGuiWindowFlags_NoScrollbar | ImGuiWindowFlags_NoBringToFrontOnFocus | ImGuiWindowFlags_NoNav);
            {
                ImVec2 videoSize = {VW, VH};
                if (pipHasVideo) {
                    ID3D11ShaderResourceView* srv = app->rtspViewer.getTextureSRV();
                    if (srv) {
                        int srcW = rvs.width.load(), srcH = rvs.height.load();
                        float aspectSrc = (srcH > 0) ? (float)srcW / (float)srcH : 16.0f/9.0f;
                        float aspectDst = (videoSize.y > 0) ? videoSize.x / videoSize.y : 1.0f;
                        float drawW, drawH;
                        if (aspectSrc > aspectDst) { drawW = videoSize.x; drawH = videoSize.x / aspectSrc; }
                        else                       { drawH = videoSize.y; drawW = videoSize.y * aspectSrc; }
                        float offX2 = (videoSize.x - drawW) * 0.5f;
                        float offY2 = (videoSize.y - drawH) * 0.5f;
                        ImGui::SetCursorPos({offX2, offY2});
                        ImGui::Image((ImTextureID)srv, {drawW, drawH});
                    }
                } else if (pipRunning) {
                    ImGui::SetCursorPos({VW * 0.2f, VH * 0.4f});
                    ImGui::TextColored({0.5f, 0.5f, 0.5f, 1.0f}, "Waiting for video...");
                } else {
                    ImGui::SetCursorPos({VW * 0.2f, VH * 0.4f});
                    ImGui::TextColored({0.4f, 0.4f, 0.4f, 1.0f}, "No stream");
                }
            }
            ImGui::End();
            ImGui::PopStyleColor();
            ImGui::PopStyleVar(3);

            // Detect mouse hover over entire PiP area
            ImVec2 mpos = ImGui::GetIO().MousePos;
            bool mouseInPip = (mpos.x >= OX && mpos.x <= OX + VW && mpos.y >= OY && mpos.y <= OY + VH);

            // Smooth fade for overlay
            static float pipOverlayAlpha = 0.0f;
            float target = mouseInPip ? 1.0f : 0.0f;
            float speed = 8.0f * ImGui::GetIO().DeltaTime;
            pipOverlayAlpha += (target - pipOverlayAlpha) * (speed > 1.0f ? 1.0f : speed);
            if (pipOverlayAlpha < 0.01f) pipOverlayAlpha = 0.0f;

            if (pipOverlayAlpha > 0.01f) {
                float alpha = pipOverlayAlpha * 0.85f;
                float barH = 32 * dpi;
                float barY = OY + VH - barH;

                // Bottom bar overlay
                ImGui::PushStyleVar(ImGuiStyleVar_WindowPadding, {8*dpi, 4*dpi});
                ImGui::PushStyleVar(ImGuiStyleVar_WindowRounding, 0.0f);
                ImGui::PushStyleVar(ImGuiStyleVar_WindowBorderSize, 0.0f);
                ImGui::SetNextWindowPos({OX, barY});
                ImGui::SetNextWindowSize({VW, barH});
                ImGui::SetNextWindowBgAlpha(alpha * 0.70f);
                ImGui::Begin("##pip_bar", nullptr,
                    ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoMove | ImGuiWindowFlags_NoResize |
                    ImGuiWindowFlags_NoScrollbar | ImGuiWindowFlags_NoFocusOnAppearing);
                {
                    ImGui::PushStyleVar(ImGuiStyleVar_FrameRounding, 3.0f);
                    float ba = alpha;

                    // Close button
                    ImGui::PushStyleColor(ImGuiCol_Button, ImVec4{0.85f, 0.20f, 0.20f, ba});
                    ImGui::PushStyleColor(ImGuiCol_ButtonHovered, ImVec4{1.0f, 0.30f, 0.30f, ba});
                    ImGui::PushStyleColor(ImGuiCol_Text, ImVec4{1, 1, 1, ba});
                    if (ImGui::SmallButton("Close")) {
                        exitPip();
                    }
                    ImGui::PopStyleColor(3);

                    // Size buttons
                    const char* szN[] = {"S","M","L"};
                    const int szW[] = {400, 600, 840};
                    const int szH[] = {260, 380, 520};
                    for (int si = 0; si < 3; si++) {
                        ImGui::SameLine(0, 4);
                        bool act = (app->pipSize == si);
                        ImVec4 btnCol = act ? ImVec4{0.20f, 0.55f, 0.90f, ba} : ImVec4{0.30f, 0.30f, 0.35f, ba};
                        ImGui::PushStyleColor(ImGuiCol_Button, btnCol);
                        ImGui::PushStyleColor(ImGuiCol_ButtonHovered, ImVec4{0.35f, 0.60f, 0.95f, ba});
                        ImGui::PushStyleColor(ImGuiCol_Text, ImVec4{1, 1, 1, ba});
                        char sl[8]; snprintf(sl, sizeof(sl), "%s##pp%d", szN[si], si);
                        if (ImGui::SmallButton(sl)) {
                            app->pipSize = si;
                            applyPipRect();
                        }
                        ImGui::PopStyleColor(3);
                    }

                    // Stop button
                    if (pipRunning) {
                        ImGui::SameLine(0, 12);
                        ImGui::PushStyleColor(ImGuiCol_Button, ImVec4{0.80f, 0.25f, 0.20f, ba});
                        ImGui::PushStyleColor(ImGuiCol_ButtonHovered, ImVec4{1.0f, 0.35f, 0.30f, ba});
                        ImGui::PushStyleColor(ImGuiCol_Text, ImVec4{1, 1, 1, ba});
                        if (ImGui::SmallButton("Stop")) {
                            app->liveTuning = false; app->liveSelectedCh = -1;
                            uint64_t stopSeq = ++app->liveTuneSeq;
                            std::weak_ptr<App> ws = app;
                            std::thread([ws, stopSeq]{ try { auto s = ws.lock(); if (!s) return;
                                std::lock_guard<std::mutex> tuneGuard(s->liveTuneMu);
                                if (s->down || stopSeq != s->liveTuneSeq.load()) return;
                                s->rtspViewer.stop();
                                try { if (s->client.isConnected()) s->client.sat2ipStop(); } catch (...) {}
                            } catch (...) {} }).detach();
                        }
                        ImGui::PopStyleColor(3);
                    }

                    // Status text on the right
                    if (pipHasVideo) {
                        char st[64]; snprintf(st, sizeof(st), "%dx%d %.0ffps",
                            rvs.width.load(), rvs.height.load(), rvs.fps.load());
                        ImVec2 ts = ImGui::CalcTextSize(st);
                        float rx = ImGui::GetContentRegionAvail().x;
                        if (rx > ts.x + 8) { ImGui::SameLine(ImGui::GetWindowWidth() - ts.x - 12); }
                        ImGui::TextColored({0.5f, 0.9f, 0.5f, ba}, "%s", st);
                    }

                    ImGui::PopStyleVar(); // FrameRounding
                }
                ImGui::End();
                ImGui::PopStyleVar(3);
            }
            goto pip_end_frame;
        }

        // ════════════════════════════════════════════════════════════════
        // CONNECTION PANEL
        // ════════════════════════════════════════════════════════════════
        ImGui::SetNextWindowPos({OX+P, OY+P});
        ImGui::SetNextWindowSize({LW, CONN_H});
        ImGui::Begin("##conn", nullptr,
            ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoMove |
            ImGuiWindowFlags_NoResize   | ImGuiWindowFlags_NoScrollbar);
        {
            ImVec4 sc = connected
                ? ImVec4(0.18f,0.82f,0.45f,1)
                : (busyConn ? ImVec4(0.95f,0.75f,0.10f,1)
                            : ImVec4(0.90f,0.35f,0.35f,1));
            ImGui::TextColored(sc, "  %s", ConnStr(connSt));
            if (!ss.model.empty()) {
                ImGui::SameLine(0,8);
                ImGui::TextDisabled("| %s", ss.model.c_str());
            }
            if (!ss.swVer.empty()) {
                ImGui::SameLine(0,6);
                ImGui::TextDisabled("v%s", ss.swVer.c_str());
            }
            if (ss.chTotal > 0) {
                ImGui::SameLine(0,8);
                ImGui::TextDisabled("TV:%d Radio:%d", ss.chTotal, ss.radioTotal);
            }
            ImGui::SameLine(0,10);
            if (ImGui::SmallButton("Remote")) app->rightTab = 0;
            ImGui::SameLine(0,4);
            if (ImGui::SmallButton("My Lists")) app->rightTab = 2;
            ImGui::SameLine(0,4);
            if (ImGui::SmallButton("Logs")) { app->rightTab = 1; }
            ImGui::Separator();

            float avail = ImGui::GetContentRegionAvail().x;
            ImGui::Text("IP"); ImGui::SameLine(0,4);
            ImGui::SetNextItemWidth(avail * 0.40f);
            ImGui::InputText("##ip", app->ip, sizeof(app->ip));
            ImGui::SameLine(0,8);
            ImGui::Text("Port"); ImGui::SameLine(0,4);
            ImGui::SetNextItemWidth(72*dpi);
            ImGui::InputInt("##port", &app->port, 0, 0);
            if (app->port < 1 || app->port > 65535) app->port = 20000;
            ImGui::SameLine(0,10);
            if (ImGui::Checkbox("Auto-reconnect", &app->autoR))
                app->client.setAutoReconnect(app->autoR);

            float BH = 26*dpi, BW = 100*dpi;
            if (!connected && !busyConn) {
                if (ImGui::Button("Connect", {BW,BH})) {
                    app->connecting = true;
                    std::string ip(app->ip);
                    int p = app->port;
                    std::thread([app, ip, p]() {
                        try {
                            app->log.add("Connecting to " + ip + ":" + std::to_string(p));
                            if (!app->client.connect(ip, p)) {
                                app->log.add("Failed: " + app->client.getLastError());
                                app->connecting = false;
                            }
                        } catch (const std::exception& e) {
                            CrashLog((std::string("connect thread exception: ") + e.what()).c_str());
                            app->connecting = false;
                        } catch (...) {
                            CrashLog("connect thread unknown exception");
                            app->connecting = false;
                        }
                    }).detach();
                }
            } else if (!connected && busyConn) {
                ImGui::BeginDisabled();
                ImGui::Button("Connecting...", {BW,BH});
                ImGui::EndDisabled();
            } else {
                ImGui::PushStyleColor(ImGuiCol_Button, {0.55f,0.16f,0.16f,0.70f});
                ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.72f,0.20f,0.20f,0.90f});
                if (ImGui::Button("Disconnect", {BW,BH})) {
                    std::thread([app]() {
                        try { app->client.disconnect(); } catch (...) {}
                        app->log.add("Disconnected");
                    }).detach();
                }
                ImGui::PopStyleColor(2);
            }

            ImGui::SameLine(0,6);
            if (busyDisc) {
                ImGui::BeginDisabled();
                ImGui::Button("Searching...", {120*dpi,BH});
                ImGui::EndDisabled();
            } else {
                if (ImGui::Button("Discover STB", {120*dpi,BH})) {
                    app->discovering = true;
                    app->setDisc("Searching...");
                    app->log.add("UDP discovery started (port 25860)...");
                    std::thread([app]() {
                        try {
                            auto list = stb::STBClient::discoverDevices(6000);
                            {
                                std::lock_guard<std::mutex> g(app->discMu);
                                app->discovered = list;
                                app->discSel = 0;
                            }
                            if (list.empty()) {
                                app->setDisc("No devices found");
                                app->log.add("[disc] No STB found");
                            } else {
                                app->setDisc("Found " + std::to_string(list.size()) + " device(s)");
                                for (auto& d : list)
                                    app->log.add("[disc] " + d.ip + "  " + d.model_name);
                            }
                        } catch (...) {
                            CrashLog("discover thread exception");
                            app->setDisc("Discovery error");
                        }
                        app->discovering = false;
                    }).detach();
                }
            }

            // Discovered devices dropdown
            {
                std::lock_guard<std::mutex> g(app->discMu);
                if (!app->discovered.empty()) {
                    ImGui::SameLine(0,6);
                    float ddW = avail - BW - 120*dpi - 24;
                    if (ddW < 140) ddW = 140;
                    ImGui::SetNextItemWidth(ddW);
                    int& ds = app->discSel;
                    if (ds >= (int)app->discovered.size()) ds = 0;
                    std::string prev = app->discovered[ds].ip;
                    if (!app->discovered[ds].model_name.empty())
                        prev += "  " + app->discovered[ds].model_name;
                    if (ImGui::BeginCombo("##dd", prev.c_str())) {
                        for (int i = 0; i < (int)app->discovered.size(); i++) {
                            auto& d = app->discovered[i];
                            std::string lbl = d.ip;
                            if (!d.model_name.empty()) lbl += "  | " + d.model_name;
                            if (ImGui::Selectable(lbl.c_str(), ds == i)) {
                                ds = i;
                                strncpy_s(app->ip, d.ip.c_str(), sizeof(app->ip)-1);
                            }
                        }
                        ImGui::EndCombo();
                    }
                } else if (!ss.discStatus.empty()) {
                    ImGui::SameLine(0,8);
                    ImGui::TextDisabled("%s", ss.discStatus.c_str());
                }
            }
            // ── Main workspace tab bar ──
            ImGui::Separator();
            static int prevMainTab = -1;
            bool mtChanged = (prevMainTab >= 0 && prevMainTab != app->mainTab);
            if (ImGui::BeginTabBar("##mainWorkspaceTabs")) {
                auto mtf = [&](int idx) -> ImGuiTabItemFlags {
                    return (mtChanged && app->mainTab == idx) ? ImGuiTabItemFlags_SetSelected : 0;
                };
                if (ImGui::BeginTabItem("Channels + Live",  nullptr, mtf(0))) { app->mainTab = 0; ImGui::EndTabItem(); }
                if (ImGui::BeginTabItem("CCcam + AI", nullptr, mtf(1))) { app->mainTab = 1; ImGui::EndTabItem(); }
                if (ImGui::BeginTabItem("STB Info",   nullptr, mtf(2))) { app->mainTab = 2; ImGui::EndTabItem(); }
                ImGui::EndTabBar();
            }
            prevMainTab = app->mainTab;
        }
        ImGui::End();

        // ════════════════════════════════════════════════════════════════
        // MAIN CONTENT AREA (left panel, below connection)
        // ════════════════════════════════════════════════════════════════

        // Pump video frame queue every frame (regardless of active tab, for PiP)
        app->rtspViewer.pump();

        // ── TAB 0: CHANNELS + LIVE (unified) ────────────────────────────
        if (app->mainTab == 0) {
        ImGui::SetNextWindowPos({OX+P, OY+P+CONN_H+P});
        ImGui::SetNextWindowSize({LW, CH_H});
        ImGui::Begin("##channels_live", nullptr,
            ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoMove | ImGuiWindowFlags_NoResize);
        {
            bool isRunning = app->rtspViewer.isRunning();
            bool hasVideo  = app->rtspViewer.hasVideo();
            auto& rvStats  = app->rtspViewer.getStats();
            static bool ffmpegAvailable = stb::RtspViewer::isFFmpegAvailable();

            float fullW = ImGui::GetContentRegionAvail().x;
            float listW = 280 * dpi;
            float vidW  = fullW - listW - 8*dpi;

            // ════ LEFT PANEL: Channel List ════
            ImGui::BeginChild("##chlist_panel", {listW, 0}, true);
            {
                float tbH = 20*dpi;

                const char* tabNames[] = {"All","TV","Radio","Fav","FTA","Enc","Recent"};
                const int tabCount = 7;
                for (int i = 0; i < tabCount; i++) {
                    if (i) ImGui::SameLine(0,2);
                    bool active = (app->tab == i);
                    if (active) {
                        ImGui::PushStyleColor(ImGuiCol_Button, ImGui::GetStyle().Colors[ImGuiCol_TabActive]);
                        ImGui::PushStyleColor(ImGuiCol_ButtonHovered, ImGui::GetStyle().Colors[ImGuiCol_TabActive]);
                    }
                    if (ImGui::Button(tabNames[i], {0,tbH})) app->tab = i;
                    if (active) ImGui::PopStyleColor(2);
                }

                ImGui::SetNextItemWidth(-1);
                ImGui::InputTextWithHint("##livesearch", "Search...", app->search, sizeof(app->search));

                {
                    int chCount = 0;
                    { std::lock_guard<std::mutex> g(app->chMu); chCount = (int)app->channels.size(); }
                    if (connected && !busyCh) {
                        float bw = (ImGui::GetContentRegionAvail().x - 12) / 3.0f;
                        if (ImGui::Button("Load##ch", {bw, tbH})) {
                            app->chLoading = true; app->chLoaded = false; app->chPct = 0;
                            std::thread([app]() {
                                try {
                                    app->log.add("Loading channels (cached)...");
                                    int n = app->client.requestChannelList(false);
                                    if (!app->chLoaded.load()) {
                                        std::lock_guard<std::mutex> g(app->chMu);
                                        app->channels = app->client.state().channels;
                                        app->chLoaded = true; app->chLoading = false; app->chPct = 100;
                                        app->log.add("Loaded " + std::to_string(n) + " channels");
                                    }
                                    app->saveCachedChannels();
                                } catch (...) { app->chLoading = false; }
                            }).detach();
                        }
                        ImGui::SameLine(0,4);
                        if (ImGui::Button("Refresh##ch", {bw, tbH})) {
                            app->chLoading = true; app->chLoaded = false; app->chPct = 0;
                            std::thread([app]() {
                                try {
                                    app->log.add("Refreshing channels...");
                                    int n = app->client.requestChannelList(true);
                                    if (!app->chLoaded.load()) {
                                        std::lock_guard<std::mutex> g(app->chMu);
                                        app->channels = app->client.state().channels;
                                        app->chLoaded = true; app->chLoading = false; app->chPct = 100;
                                        app->log.add("Refreshed " + std::to_string(n) + " channels");
                                    }
                                    app->saveCachedChannels();
                                } catch (...) { app->chLoading = false; }
                            }).detach();
                        }
                        ImGui::SameLine(0,4);
                        ImGui::TextDisabled("%d", chCount);
                    } else if (busyCh) {
                        ImGui::ProgressBar(app->chPct.load() / 100.0f, {-1, 3*dpi}, "");
                    } else {
                        ImGui::TextDisabled("%d channels", chCount);
                        ImGui::SameLine(0, 4);
                        if (ImGui::SmallButton("SAT##ch")) app->showSatPanel = true;
                    }
                }

                if (isRunning) {
                    ImGui::PushStyleColor(ImGuiCol_Button, ImVec4{0.75f, 0.22f, 0.22f, 0.85f});
                    if (ImGui::Button("Stop##live", {-1, tbH})) {
                        app->liveTuning = false;
                        app->liveSelectedCh = -1;
                        uint64_t stopSeq = ++app->liveTuneSeq;
                        std::weak_ptr<App> wstop = app;
                        std::thread([wstop, stopSeq]{
                            try {
                                auto sp = wstop.lock(); if (!sp) return;
                                std::lock_guard<std::mutex> tuneGuard(sp->liveTuneMu);
                                if (sp->down || stopSeq != sp->liveTuneSeq.load()) return;
                                sp->rtspViewer.stop();
                                try { if (sp->client.isConnected()) sp->client.sat2ipStop(); } catch (...) {}
                            } catch (...) {}
                        }).detach();
                    }
                    ImGui::PopStyleColor();
                } else if (app->liveTuning) {
                    ImGui::TextColored({0.95f, 0.75f, 0.10f, 1.0f}, "Tuning...");
                }

                ImGui::Separator();

                int clickedChIdx = -1;
                ImGui::BeginChild("##chscroll", {0, 0});
                {
                    if (app->tab == 6) {
                        for (int ri = 0; ri < (int)app->recentChannels.size(); ri++) {
                            auto& rc = app->recentChannels[ri];
                            char lb[256]; snprintf(lb, sizeof(lb), "%d. %s##rc%d", rc.service_index, rc.name.c_str(), ri);
                            bool isSel = (app->rtspProgramId == rc.service_id && isRunning);
                            if (isSel) ImGui::PushStyleColor(ImGuiCol_Text, ImVec4{0.2f, 0.9f, 0.4f, 1.0f});
                            if (ImGui::Selectable(lb, isSel) && !app->liveTuning) {
                                std::lock_guard<std::mutex> g(app->chMu);
                                for (int ci = 0; ci < (int)app->channels.size(); ci++) {
                                    if (app->channels[ci].service_id == rc.service_id) { clickedChIdx = ci; break; }
                                }
                            }
                            if (isSel) ImGui::PopStyleColor();
                        }
                        if (app->recentChannels.empty()) ImGui::TextDisabled("No recent channels yet.");
                    } else {
                        std::lock_guard<std::mutex> g(app->chMu);
                        std::string flt;
                        if (app->search[0]) { flt = app->search; for (auto& c : flt) c = (char)tolower((unsigned char)c); }
                        for (int i = 0; i < (int)app->channels.size(); i++) {
                            const auto& ch = app->channels[i];
                            if (app->tab == 1 && ch.is_radio) continue;
                            if (app->tab == 2 && !ch.is_radio) continue;
                            if (app->tab == 3 && ch.fav_bit == 0) continue;
                            if (app->tab == 4 && ch.is_scrambled) continue;
                            if (app->tab == 5 && !ch.is_scrambled) continue;
                            if (!flt.empty()) {
                                std::string n = ch.service_name; for (auto& c : n) c = (char)tolower((unsigned char)c);
                                std::string idx = std::to_string(ch.service_index);
                                if (n.find(flt) == std::string::npos && idx.find(flt) == std::string::npos) continue;
                            }
                            bool isSel = (i == app->liveSelectedCh);
                            char lb[256];
                            const char* hd = ch.is_hd ? " HD" : "";
                            const char* enc = ch.is_scrambled ? " $" : "";
                            const char* typ = ch.is_radio ? " [R]" : "";
                            snprintf(lb, sizeof(lb), "%d. %s%s%s%s##lch%d", ch.service_index, ch.service_name.c_str(), hd, enc, typ, i);
                            if (isSel) ImGui::PushStyleColor(ImGuiCol_Text, ImVec4{0.2f, 0.9f, 0.4f, 1.0f});
                            if (ImGui::Selectable(lb, isSel) && !app->liveTuning) clickedChIdx = i;
                            if (isSel) ImGui::PopStyleColor();
                            if (ImGui::BeginPopupContextItem()) {
                                if (ImGui::MenuItem("Channel Details")) { app->detailIdx = i; app->showDetail = true; }
                                ImGui::Separator();
                                auto& cls = app->customLists.lists;
                                if (!cls.empty()) {
                                    ImGui::TextDisabled("Add to list:");
                                    for (int li = 0; li < (int)cls.size(); li++) {
                                        char mlbl[128]; snprintf(mlbl, sizeof(mlbl), "%s##addcl%d", cls[li].title.c_str(), li);
                                        if (ImGui::MenuItem(mlbl)) {
                                            stb::CustomListEntry ce; ce.service_id = ch.service_id; ce.name = ch.service_name;
                                            ce.service_index = ch.service_index; ce.is_radio = ch.is_radio;
                                            cls[li].entries.push_back(std::move(ce)); app->clDirty = true;
                                        }
                                    }
                                }
                                ImGui::EndPopup();
                            }
                        }
                    }
                }
                ImGui::EndChild();

                if (clickedChIdx >= 0) {
                    if (!startLiveChannelByIndex(clickedChIdx)) app->liveTuning = false;
                }
            }
            ImGui::EndChild();

            ImGui::SameLine(0, 8*dpi);

            // ════ RIGHT PANEL: Video + Status + PiP ════
            ImGui::BeginChild("##video_panel", {vidW, 0}, true);
            {
                if (isRunning) {
                    if (hasVideo) {
                        ImGui::TextColored({0.38f, 0.88f, 0.38f, 1.0f}, "LIVE");
                        ImGui::SameLine(0, 8);
                        ImGui::TextDisabled("%dx%d %.1ffps %dkbps",
                            rvStats.width.load(), rvStats.height.load(),
                            rvStats.fps.load(), rvStats.bitrate_kbps.load());
                    } else if (rvStats.connected.load()) {
                        ImGui::TextColored({0.95f, 0.75f, 0.10f, 1.0f}, "Waiting for video...");
                    } else {
                        ImGui::TextColored({0.95f, 0.75f, 0.10f, 1.0f}, "CONNECTING...");
                    }
                } else {
                    std::string err = rvStats.getError();
                    if (!err.empty())
                        ImGui::TextColored({0.95f, 0.42f, 0.38f, 1.0f}, "%s", err.c_str());
                    else
                        ImGui::TextDisabled("Select a channel to start Live TV");
                }
                {
                    float px = ImGui::GetContentRegionAvail().x;
                    if (px > 50*dpi) { ImGui::SameLine(px - 40*dpi); }
                    if (ImGui::SmallButton("PiP")) {
                        enterPip();
                    }
                }
                ImGui::Separator();

                ImVec2 videoSize = ImGui::GetContentRegionAvail();
                if (hasVideo) {
                    ID3D11ShaderResourceView* srv = app->rtspViewer.getTextureSRV();
                    if (srv) {
                        int srcW = rvStats.width.load(), srcH = rvStats.height.load();
                        float aspectSrc = (srcH > 0) ? (float)srcW / (float)srcH : 16.0f/9.0f;
                        float aspectDst = (videoSize.y > 0) ? videoSize.x / videoSize.y : 1.0f;
                        float drawW, drawH;
                        if (aspectSrc > aspectDst) { drawW = videoSize.x; drawH = videoSize.x / aspectSrc; }
                        else                       { drawH = videoSize.y; drawW = videoSize.y * aspectSrc; }
                        float offX = (videoSize.x - drawW) * 0.5f;
                        float offY = (videoSize.y - drawH) * 0.5f;
                        ImGui::SetCursorPos({ImGui::GetCursorPos().x + offX, ImGui::GetCursorPos().y + offY});
                        ImGui::Image((ImTextureID)srv, {drawW, drawH});
                    }
                } else if (!ffmpegAvailable) {
                    ImGui::SetCursorPosY(ImGui::GetCursorPosY() + videoSize.y * 0.3f);
                    ImGui::PushStyleColor(ImGuiCol_Text, ImVec4{0.95f, 0.75f, 0.10f, 1.0f});
                    ImGui::TextWrapped("FFmpeg DLLs not found.");
                    ImGui::PopStyleColor();
                } else if (isRunning) {
                    ImGui::SetCursorPosY(ImGui::GetCursorPosY() + videoSize.y * 0.35f);
                    ImGui::TextColored({0.6f, 0.6f, 0.6f, 1.0f}, "Waiting for video stream...");
                } else {
                    ImGui::SetCursorPosY(ImGui::GetCursorPosY() + videoSize.y * 0.3f);
                    ImGui::TextDisabled("Select a channel from the list to start Live TV.");
                    ImGui::Spacing();
                    ImGui::TextDisabled("The STB will tune the transponder.");
                    ImGui::TextDisabled("Video will appear once the stream is ready.");
                }
            }
            ImGui::EndChild();
        }
        ImGui::End();
        } // end mainTab == 0 (Channels + Live)

        // ════════════════════════════════════════════════════════════════
        // CHANNEL DETAIL POPUP
        // ════════════════════════════════════════════════════════════════
        if (app->showDetail && app->detailIdx >= 0) {
            // Get channel snapshot again (we're outside the channels window now)
            std::vector<stb::Channel> detSnap;
            { std::lock_guard<std::mutex> g(app->chMu); detSnap = app->channels; }
            // Apply same sort as main table
            if (!detSnap.empty()) {
                auto cmpLess = [&](const stb::Channel& a, const stb::Channel& b) -> bool {
                    switch (app->sortCol) {
                    case 1: return a.service_name < b.service_name;
                    case 2: return a.frequency() < b.frequency();
                    case 3: return (int)a.is_radio < (int)b.is_radio;
                    default: return a.service_index < b.service_index;
                    }
                };
                if (app->sortAsc)
                    std::stable_sort(detSnap.begin(), detSnap.end(), cmpLess);
                else
                    std::stable_sort(detSnap.begin(), detSnap.end(),
                        [&](const stb::Channel& a, const stb::Channel& b){ return cmpLess(b,a); });
            }

            if (app->detailIdx < (int)detSnap.size()) {
                auto& ch = detSnap[app->detailIdx];
                ImGui::SetNextWindowSize({400*dpi, 480*dpi}, ImGuiCond_FirstUseEver);
                std::string title = ch.service_name + " - Details###chdetail";
                if (ImGui::Begin(title.c_str(), &app->showDetail)) {
                    // Play button at top
                    if (connected) {
                        ImGui::PushStyleColor(ImGuiCol_Button, {0.15f,0.45f,0.85f,0.80f});
                        ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.20f,0.55f,0.95f,1.0f});
                        if (ImGui::Button("Play on STB", {-1, 28*dpi})) {
                            const int tvst = ch.is_radio ? 1 : 0;
                            bool ok = false;
                            try {
                                if (!ch.service_id.empty())
                                    ok = app->client.changeChannelDirect(ch.service_id, tvst);
                                if (!ok && !ch.program_id.empty() && ch.program_id != ch.service_id)
                                    ok = app->client.changeChannelDirect(ch.program_id, tvst);
                                if (!ok)
                                    ok = app->client.changeChannel(ch.service_index);
                            } catch (...) {}
                            if (ok) {
                                app->log.add("[play] " + ch.service_name);
                                const std::string sid = ch.service_id;
                                const int ts2 = tvst;
                                std::thread([app, sid, ts2]() {
                                    std::this_thread::sleep_for(std::chrono::milliseconds(700));
                                    try { app->client.changeChannelDirect(sid, ts2); } catch (...) {}
                                }).detach();
                            } else {
                                app->log.add("[play] failed for " + ch.service_name);
                            }
                        }
                        ImGui::PopStyleColor(2);
                        ImGui::Spacing();
                    }

                    auto row = [&](const char* k, const std::string& v) {
                        ImGui::TextDisabled("%-18s", k);
                        ImGui::SameLine(140*dpi);
                        ImGui::TextUnformatted(v.c_str());
                    };
                    auto rowCol = [&](const char* k, const std::string& v, const ImVec4& col) {
                        ImGui::TextDisabled("%-18s", k);
                        ImGui::SameLine(140*dpi);
                        ImGui::TextColored(col, "%s", v.c_str());
                    };

                    row("Service Index:", std::to_string(ch.service_index));
                    row("Service ID:",    ch.service_id);
                    row("Name:",          ch.service_name);
                    row("Type:",          ch.is_radio ? "Radio" : "TV");
                    if (ch.is_hd) rowCol("HD:", "Yes", {0.4f,0.8f,1.0f,1.0f});
                    else          row("HD:", "No");
                    row("Locked:",        ch.is_locked ? "Yes" : "No");
                    row("EPG:",           ch.has_epg ? "Yes" : "No");
                    row("Tuner 2:",       ch.is_tuner2 ? "Yes" : "No");

                    ImGui::Separator();
                    // Encryption section
                    if (ch.is_scrambled) {
                        rowCol("Encryption:", "Encrypted (CAS)", {0.95f,0.55f,0.15f,1.0f});
                        ImGui::TextDisabled("  %-16s", "");
                        ImGui::SameLine(140*dpi);
                        ImGui::TextDisabled("STB reports Scramble=1. Actual CAS");
                        ImGui::TextDisabled("  %-16s", "");
                        ImGui::SameLine(140*dpi);
                        ImGui::TextDisabled("(Viaccess/Nagra/Irdeto/...) visible via PMT.");
                    } else {
                        rowCol("Encryption:", "FTA (Free To Air)", {0.3f,0.85f,0.3f,1.0f});
                    }

                    ImGui::Separator();
                    row("Sat Index:",     std::to_string(ch.satIndex()));
                    row("Frequency:",     std::to_string(ch.frequency()) + " MHz");
                    row("Mod System:",    ch.modulationSystemStr());
                    row("Mod Type:",      ch.modulationTypeStr());
                    std::string roStr = ch.roll_off == 35 ? "0.35" : ch.roll_off == 25 ? "0.25" : ch.roll_off == 20 ? "0.20" : std::to_string(ch.roll_off);
                    row("Roll Off:",      roStr);
                    row("Pilot Tones:",   ch.pilot_tones ? "On" : "Off");
                    ImGui::Separator();
                    row("Video PID:",     std::to_string(ch.video_pid));
                    row("PMT PID:",       std::to_string(ch.pmt_pid));
                    row("PCR PID:",       std::to_string(ch.pcr_pid));
                    if (ch.ttx_pid != 8191)
                        row("TTX PID:",   std::to_string(ch.ttx_pid));
                    if (!ch.audio_pids_raw.empty() && ch.audio_pids_raw != "[]")
                        row("Audio PIDs:", ch.audio_pids_raw);
                    if (!ch.subtitle_pids_raw.empty() && ch.subtitle_pids_raw != "[]")
                        row("Subtitle PIDs:", ch.subtitle_pids_raw);
                    ImGui::Separator();
                    row("Fav Bit:",       std::to_string(ch.fav_bit));

                    // Favorite toggle buttons
                    ImGui::Spacing();
                    float bw = (ImGui::GetContentRegionAvail().x - 16) / 5.0f;
                    for (int g = 0; g < 5; g++) {
                        if (g) ImGui::SameLine(0,4);
                        bool inGrp = (ch.fav_bit & (1 << g)) != 0;
                        if (inGrp) {
                            ImGui::PushStyleColor(ImGuiCol_Button, {0.8f,0.65f,0.0f,0.80f});
                            ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.95f,0.78f,0.0f,1.0f});
                        }
                        char fbtn[32]; snprintf(fbtn, sizeof(fbtn), "Fav%d", g+1);
                        if (ImGui::Button(fbtn, {bw, 24*dpi})) {
                            if (connected) {
                                try {
                                    int newBit = inGrp ? (ch.fav_bit & ~(1<<g)) : (ch.fav_bit | (1<<g));
                                    app->client.setChannelFavMark(
                                        {ch.service_id}, newBit, {g});
                                    // Update local channel data immediately
                                    {
                                        std::lock_guard<std::mutex> g2(app->chMu);
                                        for (auto& lch : app->channels) {
                                            if (lch.service_id == ch.service_id) {
                                                lch.fav_bit = newBit;
                                                break;
                                            }
                                        }
                                    }
                                    app->log.add(std::string(inGrp ? "[-fav] " : "[+fav] ") +
                                        ch.service_name + " grp" + std::to_string(g+1));
                                } catch (...) {}
                            }
                        }
                        if (inGrp) ImGui::PopStyleColor(2);
                    }
                }
                ImGui::End();
            } else {
                app->showDetail = false;
            }
        }

        // ════════════════════════════════════════════════════════════════
        // SATELLITE LIST PANEL
        // ════════════════════════════════════════════════════════════════
        if (app->showSatPanel) {
            ImGui::SetNextWindowSize({340*dpi, 300*dpi}, ImGuiCond_FirstUseEver);
            if (ImGui::Begin("Satellites", &app->showSatPanel)) {
                std::vector<stb::Satellite> satSnap;
                { std::lock_guard<std::mutex> g(app->satMu); satSnap = app->satellites; }
                if (satSnap.empty()) {
                    ImGui::TextDisabled("No satellites loaded.");
                    if (connected && ImGui::Button("Request SAT List")) {
                        try { app->client.requestSatelliteList(); } catch (...) {}
                    }
                } else {
                    ImGui::TextDisabled("%d satellites", (int)satSnap.size());
                    ImGui::Separator();
                    if (ImGui::BeginTable("##satt", 3,
                            ImGuiTableFlags_RowBg | ImGuiTableFlags_ScrollY |
                            ImGuiTableFlags_BordersInnerV)) {
                        ImGui::TableSetupScrollFreeze(0,1);
                        ImGui::TableSetupColumn("#",    ImGuiTableColumnFlags_WidthFixed, 30*dpi);
                        ImGui::TableSetupColumn("Name", ImGuiTableColumnFlags_WidthStretch);
                        ImGui::TableSetupColumn("Pos",  ImGuiTableColumnFlags_WidthFixed, 60*dpi);
                        ImGui::TableHeadersRow();
                        for (auto& sat : satSnap) {
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0);
                            ImGui::Text("%d", sat.sat_index);
                            ImGui::TableSetColumnIndex(1);
                            ImGui::TextUnformatted(sat.sat_name.c_str());
                            ImGui::TableSetColumnIndex(2);
                            ImGui::Text("%.1f%s", sat.sat_position / 10.0f,
                                sat.is_east ? "E" : "W");
                        }
                        ImGui::EndTable();
                    }
                }
                if (connected) {
                    ImGui::Spacing();
                    if (ImGui::Button("Refresh SAT List", {-1, 24*dpi})) {
                        try { app->client.requestSatelliteList(); } catch (...) {}
                    }
                }
            }
            ImGui::End();
        }

        // ════════════════════════════════════════════════════════════════
        // RIGHT PANEL (tabbed: Remote | Log | My Lists)
        // ════════════════════════════════════════════════════════════════
        ImGui::SetNextWindowPos({RX, OY+P});
        ImGui::SetNextWindowSize({RW, RPH});
        ImGui::Begin("##rpanel", nullptr,
            ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoMove |
            ImGuiWindowFlags_NoResize   | ImGuiWindowFlags_NoScrollbar);
        {
            // Tab bar
            const char* rtNames[] = {"Remote","Log","My Lists"};
            for (int i = 0; i < 3; i++) {
                if (i) ImGui::SameLine(0,2);
                bool act = (app->rightTab == i);
                if (act) {
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.22f,0.44f,0.78f,0.90f});
                    ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.22f,0.44f,0.78f,0.90f});
                }
                if (ImGui::Button(rtNames[i], {0, 20*dpi})) app->rightTab = i;
                if (act) ImGui::PopStyleColor(2);
            }
            // Quick-access buttons on tab bar
            ImGui::SameLine(0,8);
            if (ImGui::SmallButton("KB")) app->showKeyboard = true;
            ImGui::SameLine(0,4);
            if (ImGui::SmallButton("CC")) app->mainTab = 1;
            ImGui::SameLine(0,4);
            if (ImGui::SmallButton("?")) app->mainTab = 2;

            ImGui::Separator();

            // ──────────────── TAB 0: REMOTE ────────────────
            if (app->rightTab == 0) {
                bool remDisabled = !connected;
                if (remDisabled) ImGui::BeginDisabled();

                auto send = [&](int k) {
                    try { app->client.sendRemoteKey(k); } catch (...) {}
                };
                float AW = ImGui::GetContentRegionAvail().x;
                float B3 = (AW - 8) / 3.0f;
                float B4 = (AW - 12) / 4.0f;
                float B5 = (AW - 16) / 5.0f;
                float BH = 24*dpi, BHS = 22*dpi;

                ImGui::BeginChild("##remScroll", {0, 0}, false);

                // Power row
                ImGui::PushStyleColor(ImGuiCol_Button, {0.55f,0.18f,0.18f,0.70f});
                if (ImGui::Button("PWR", {B4,BHS})) send(stb::keys::KEY_POWER);
                ImGui::PopStyleColor();
                ImGui::SameLine(0,4);
                if (ImGui::Button("Mute", {B4,BHS})) send(stb::keys::KEY_MUTE);
                ImGui::SameLine(0,4);
                if (ImGui::Button("Sleep", {B4,BHS})) send(stb::keys::KEY_SLEEP);
                ImGui::SameLine(0,4);
                if (ImGui::Button("TV/R##tvr", {B4,BHS})) {
                    try { app->client.tvRadioSwitch(); } catch (...) {}
                }

                // Numpad (3x4)
                ImGui::Spacing();
                {
                    static const char* NL[] = {"1","2","3","4","5","6","7","8","9","*","0","#"};
                    static const int NK[] = {
                        stb::keys::KEY_1, stb::keys::KEY_2, stb::keys::KEY_3,
                        stb::keys::KEY_4, stb::keys::KEY_5, stb::keys::KEY_6,
                        stb::keys::KEY_7, stb::keys::KEY_8, stb::keys::KEY_9,
                        stb::keys::KEY_TTX, stb::keys::KEY_0, stb::keys::KEY_SUBTITLE
                    };
                    for (int i = 0; i < 12; i++) {
                        if (i % 3) ImGui::SameLine(0,4);
                        if (ImGui::Button(NL[i], {B3,BH})) send(NK[i]);
                    }
                }

                ImGui::Spacing();

                // D-pad
                {
                    float nB = B3;
                    ImGui::Dummy({nB+2,0}); ImGui::SameLine(0,4);
                    if (ImGui::Button("Up##n",   {nB,BH})) send(stb::keys::KEY_UP);
                    if (ImGui::Button("Left##n", {nB,BH})) send(stb::keys::KEY_LEFT); ImGui::SameLine(0,4);
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.20f,0.46f,0.88f,0.80f});
                    if (ImGui::Button("OK##n",   {nB,BH})) send(stb::keys::KEY_ENTER);
                    ImGui::PopStyleColor();
                    ImGui::SameLine(0,4);
                    if (ImGui::Button("Right##n",{nB,BH})) send(stb::keys::KEY_RIGHT);
                    ImGui::Dummy({nB+2,0}); ImGui::SameLine(0,4);
                    if (ImGui::Button("Down##n", {nB,BH})) send(stb::keys::KEY_DOWN);
                }

                // Menu/Back/Info/EPG
                if (ImGui::Button("Menu",  {B4,BHS})) send(stb::keys::KEY_MENU);  ImGui::SameLine(0,4);
                if (ImGui::Button("Back",  {B4,BHS})) send(stb::keys::KEY_BACK);  ImGui::SameLine(0,4);
                if (ImGui::Button("Info",  {B4,BHS})) send(stb::keys::KEY_INFO);  ImGui::SameLine(0,4);
                if (ImGui::Button("EPG",   {B4,BHS})) send(stb::keys::KEY_EPG);
                if (ImGui::Button("FAV",   {B4,BHS})) send(stb::keys::KEY_FAV);   ImGui::SameLine(0,4);
                if (ImGui::Button("Recall",{B4,BHS})) send(stb::keys::KEY_RECALL);ImGui::SameLine(0,4);
                if (ImGui::Button("Find",  {B4,BHS})) send(stb::keys::KEY_FIND);  ImGui::SameLine(0,4);
                if (ImGui::Button("SAT",   {B4,BHS})) send(stb::keys::KEY_SAT);

                ImGui::Spacing();

                // CH/VOL
                if (ImGui::Button("CH+",{B4,BHS})) send(stb::keys::KEY_CH_UP);  ImGui::SameLine(0,4);
                if (ImGui::Button("CH-",{B4,BHS})) send(stb::keys::KEY_CH_DOWN);ImGui::SameLine(0,4);
                if (ImGui::Button("V+", {B4,BHS})) send(stb::keys::KEY_VOL_UP); ImGui::SameLine(0,4);
                if (ImGui::Button("V-", {B4,BHS})) send(stb::keys::KEY_VOL_DOWN);

                ImGui::Spacing();

                // Color buttons
                float cBW = (AW - 12) / 4.0f;
                ImGui::PushStyleColor(ImGuiCol_Button, {0.72f,0.12f,0.12f,0.85f});
                if (ImGui::Button("RED##c",{cBW,BHS})) send(stb::keys::KEY_RED);
                ImGui::PopStyleColor(); ImGui::SameLine(0,4);
                ImGui::PushStyleColor(ImGuiCol_Button, {0.10f,0.60f,0.10f,0.85f});
                if (ImGui::Button("GRN##c",{cBW,BHS})) send(stb::keys::KEY_GREEN);
                ImGui::PopStyleColor(); ImGui::SameLine(0,4);
                ImGui::PushStyleColor(ImGuiCol_Button, {0.78f,0.68f,0.06f,0.85f});
                if (ImGui::Button("YEL##c",{cBW,BHS})) send(stb::keys::KEY_YELLOW);
                ImGui::PopStyleColor(); ImGui::SameLine(0,4);
                ImGui::PushStyleColor(ImGuiCol_Button, {0.10f,0.30f,0.75f,0.85f});
                if (ImGui::Button("BLU##c",{cBW,BHS})) send(stb::keys::KEY_BLUE);
                ImGui::PopStyleColor();

                ImGui::Spacing();

                // Playback compact
                if (ImGui::CollapsingHeader("Playback##rp")) {
                    float B6 = (AW - 20) / 6.0f;
                    if (ImGui::Button("|<##r",{B6,BHS})) send(stb::keys::KEY_PREVIOUS);  ImGui::SameLine(0,4);
                    if (ImGui::Button("<<##r",{B6,BHS})) send(stb::keys::KEY_REWIND);    ImGui::SameLine(0,4);
                    if (ImGui::Button(">||##r",{B6,BHS})) send(stb::keys::KEY_PLAY_PAUSE);ImGui::SameLine(0,4);
                    if (ImGui::Button("[]##r",{B6,BHS})) send(stb::keys::KEY_STOP);      ImGui::SameLine(0,4);
                    if (ImGui::Button(">>##r",{B6,BHS})) send(stb::keys::KEY_FAST_FORWARD);ImGui::SameLine(0,4);
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.65f,0.12f,0.12f,0.75f});
                    if (ImGui::Button("REC##r",{B6,BHS})) send(stb::keys::KEY_RECORD);
                    ImGui::PopStyleColor();
                }

                // Functions compact
                if (ImGui::CollapsingHeader("Functions##rp")) {
                    if (ImGui::Button("SUB",{B5,BHS})) send(stb::keys::KEY_SUBTITLE);ImGui::SameLine(0,4);
                    if (ImGui::Button("TTX",{B5,BHS})) send(stb::keys::KEY_TTX);     ImGui::SameLine(0,4);
                    if (ImGui::Button("USB",{B5,BHS})) send(stb::keys::KEY_USB);     ImGui::SameLine(0,4);
                    if (ImGui::Button("Zoom",{B5,BHS})) send(stb::keys::KEY_ZOOM);   ImGui::SameLine(0,4);
                    if (ImGui::Button("Audio",{B5,BHS})) send(stb::keys::KEY_AUDIO);
                    if (ImGui::Button("PIP",{B5,BHS})) send(stb::keys::KEY_PIP);     ImGui::SameLine(0,4);
                    if (ImGui::Button("HDMI",{B5,BHS})) send(stb::keys::KEY_HDMI);   ImGui::SameLine(0,4);
                    if (ImGui::Button("HD",{B5,BHS})) send(stb::keys::KEY_HD);       ImGui::SameLine(0,4);
                    if (ImGui::Button("FMT",{B5,BHS})) send(stb::keys::KEY_FORMAT);  ImGui::SameLine(0,4);
                    if (ImGui::Button("FUNC",{B5,BHS})) send(stb::keys::KEY_FUNC);
                }

                // Function Keys (F1 + Code)
                if (ImGui::CollapsingHeader("F1 + Code##rp")) {
                    static int fnKeyChoice = 0;
                    auto FnBtn = [&](const char* lbl, const char* id, const char* digits, float w, float h) {
                        char fullId[64];
                        snprintf(fullId, sizeof(fullId), "%s##fn%s", lbl, id);
                        if (ImGui::Button(fullId, {w, h})) {
                            int fkey = (fnKeyChoice == 0) ? stb::keys::KEY_FUNC : stb::keys::KEY_F1;
                            std::thread([&, dig = std::string(digits), fk = fkey]() {
                                try {
                                    // Send F1 key
                                    app->client.sendRemoteKey(fk);
                                    std::this_thread::sleep_for(std::chrono::milliseconds(400));
                                    // Send digits one by one with longer delay
                                    for (char c : dig) {
                                        if (c >= '0' && c <= '9') {
                                            app->client.sendNumericKey(c - '0');
                                            std::this_thread::sleep_for(std::chrono::milliseconds(250));
                                        }
                                    }
                                } catch (...) {}
                            }).detach();
                        }
                    };
                    float fBW = (AW - 4) / 2.0f;
                    float fBH = 22*dpi;
                    ImGui::RadioButton("FUNC##fk", &fnKeyChoice, 0); ImGui::SameLine(0,6);
                    ImGui::RadioButton("F1##fk", &fnKeyChoice, 1);
                    FnBtn("000 Sys","000","000",fBW,fBH); ImGui::SameLine(0,4);
                    FnBtn("111 Act","111","111",fBW,fBH);
                    FnBtn("222 ECM","222","222",fBW,fBH); ImGui::SameLine(0,4);
                    FnBtn("333 BISS","333","333",fBW,fBH);
                    FnBtn("444 Key","444","444",fBW,fBH); ImGui::SameLine(0,4);
                    FnBtn("555 IP","555","555",fBW,fBH);
                    FnBtn("666 Srv","666","666",fBW,fBH); ImGui::SameLine(0,4);
                    FnBtn("777 AIP","777","777",fBW,fBH);
                    FnBtn("888 Upd","888","888",fBW,fBH); ImGui::SameLine(0,4);
                    FnBtn("999 Rst","999","999",fBW,fBH);
                    FnBtn("1111 CAS","1111","1111",fBW,fBH); ImGui::SameLine(0,4);
                    FnBtn("4444 IKS","4444","4444",fBW,fBH);
                }

                // System
                if (ImGui::CollapsingHeader("System##rp")) {
                    if (ImGui::Button("Restart STB",{B3,BHS})) {
                        try { app->client.restartStb(); } catch (...) {}
                    }
                    ImGui::SameLine(0,4);
                    if (ImGui::Button("Get Info",{B3,BHS})) {
                        try { app->client.requestStbInfo(); } catch (...) {}
                        app->mainTab = 2;
                    }
                    ImGui::SameLine(0,4);
                    if (ImGui::Button("KeepAlive",{B3,BHS})) {
                        try { app->client.sendKeepAlive(); } catch (...) {}
                    }
                }

                ImGui::EndChild();
                if (remDisabled) ImGui::EndDisabled();
            }

            // ──────────────── TAB 1: LOG ────────────────
            if (app->rightTab == 1) {
                if (ImGui::SmallButton("Clear##rl")) app->log.clear();
                ImGui::SameLine(0,4);
                if (ImGui::SmallButton("Copy##rlc")) ImGui::SetClipboardText(app->log.fullText().c_str());
                ImGui::SameLine(0,4);
                ImGui::Checkbox("Auto##rls", &app->log.scroll);
                ImGui::SameLine(0,4);
                if (ImGui::SmallButton("Full##rlf")) app->showLog = true;
                ImGui::SameLine(0,8);
                if (ImGui::SmallButton("CCcam Log##rlcc")) app->showCccamLog = true;
                ImGui::Separator();
                ImGui::BeginChild("##rlv", {0,0}, false, ImGuiWindowFlags_HorizontalScrollbar);
                auto logSnap = app->log.snap();
                for (auto& ln : logSnap) {
                    bool isErr = ln.find("ail") != std::string::npos ||
                                 ln.find("rror") != std::string::npos;
                    if (isErr)
                        ImGui::TextColored({0.95f,0.42f,0.38f,1.0f}, "%s", ln.c_str());
                    else
                        ImGui::TextUnformatted(ln.c_str());
                }
                if (app->log.scroll && ImGui::GetScrollY() >= ImGui::GetScrollMaxY() - 10)
                    ImGui::SetScrollHereY(1.0f);
                ImGui::EndChild();
            }

            // ──────────────── TAB 2: MY LISTS ────────────────
            if (app->rightTab == 2) {
                auto& cls = app->customLists.lists;
                static int selList = 0;
                static char newListName[64] = "My List";

                // Create new list button
                float AW = ImGui::GetContentRegionAvail().x;
                ImGui::SetNextItemWidth(AW - 70*dpi);
                ImGui::InputText("##nln", newListName, sizeof(newListName));
                ImGui::SameLine(0,4);
                if (ImGui::Button("+##nl", {0, 0})) {
                    stb::CustomList nl;
                    nl.title = newListName[0] ? newListName : "Untitled";
                    cls.push_back(std::move(nl));
                    selList = (int)cls.size() - 1;
                    app->clDirty = true;
                }
                ImGui::SameLine(0,4);
                ImGui::TextDisabled("(%d)", (int)cls.size());

                if (cls.empty()) {
                    ImGui::Spacing();
                    ImGui::TextDisabled("No custom lists yet.");
                    ImGui::TextDisabled("Create one above, then add channels");
                    ImGui::TextDisabled("from the channel table (right-click).");
                } else {
                    // List selector tabs
                    if (selList >= (int)cls.size()) selList = (int)cls.size() - 1;
                    for (int li = 0; li < (int)cls.size(); li++) {
                        if (li) ImGui::SameLine(0,2);
                        bool act = (selList == li);
                        if (act) {
                            ImGui::PushStyleColor(ImGuiCol_Button, {0.22f,0.44f,0.78f,0.90f});
                            ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.22f,0.44f,0.78f,0.90f});
                        }
                        char tlbl[64];
                        snprintf(tlbl, sizeof(tlbl), "%s (%d)##cl%d",
                            cls[li].title.c_str(), (int)cls[li].entries.size(), li);
                        if (ImGui::Button(tlbl, {0, 18*dpi})) selList = li;
                        if (act) ImGui::PopStyleColor(2);
                    }

                    auto& curList = cls[selList];
                    ImGui::Spacing();

                    // Delete list button
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.55f,0.15f,0.15f,0.70f});
                    char delLbl[64]; snprintf(delLbl, sizeof(delLbl), "Delete \"%s\"##dlist", curList.title.c_str());
                    if (ImGui::SmallButton(delLbl)) {
                        cls.erase(cls.begin() + selList);
                        if (selList >= (int)cls.size()) selList = std::max(0, (int)cls.size()-1);
                        app->clDirty = true;
                    }
                    ImGui::PopStyleColor();

                    if (selList < (int)cls.size()) {
                        auto& entries = cls[selList].entries;
                        ImGui::SameLine(0,8);
                        ImGui::TextDisabled("%d channels", (int)entries.size());

                        // Entry list
                        int removeEntry = -1;
                        int moveUp = -1, moveDown = -1;
                        ImGui::BeginChild("##clentries", {0, 0}, true);
                        for (int ei = 0; ei < (int)entries.size(); ei++) {
                            auto& e = entries[ei];
                            ImGui::PushID(ei);
                            // Play button
                            ImGui::PushStyleColor(ImGuiCol_Button, {0,0,0,0});
                            if (ImGui::SmallButton(">##clp")) {
                                int idx = findChannelIndexForEntry(e);
                                if (idx >= 0) {
                                    startLiveChannelByIndex(idx);
                                } else if (connected) {
                                    int tvst = e.is_radio ? 1 : 0;
                                    try {
                                        if (!e.service_id.empty())
                                            app->client.changeChannelDirect(e.service_id, tvst);
                                        else if (e.service_index >= 0)
                                            app->client.changeChannel(e.service_index);
                                    } catch (...) {}
                                    app->log.add("[list] " + e.name);
                                }
                            }
                            ImGui::PopStyleColor();
                            ImGui::SameLine(0,4);
                            // Name
                            ImGui::Text("%s", e.name.c_str());
                            ImGui::SameLine(ImGui::GetContentRegionAvail().x - 52*dpi);
                            // Move up/down
                            if (ei > 0) {
                                if (ImGui::SmallButton("^##mu")) moveUp = ei;
                            } else { ImGui::SmallButton(" ##mu"); }
                            ImGui::SameLine(0,2);
                            if (ei < (int)entries.size()-1) {
                                if (ImGui::SmallButton("v##md")) moveDown = ei;
                            } else { ImGui::SmallButton(" ##md"); }
                            ImGui::SameLine(0,2);
                            ImGui::PushStyleColor(ImGuiCol_Button, {0.5f,0.15f,0.15f,0.5f});
                            if (ImGui::SmallButton("x##re")) removeEntry = ei;
                            ImGui::PopStyleColor();
                            ImGui::PopID();
                        }
                        ImGui::EndChild();

                        // Apply changes
                        if (removeEntry >= 0 && removeEntry < (int)entries.size()) {
                            entries.erase(entries.begin() + removeEntry);
                            app->clDirty = true;
                        }
                        if (moveUp > 0 && moveUp < (int)entries.size()) {
                            std::swap(entries[moveUp], entries[moveUp-1]);
                            app->clDirty = true;
                        }
                        if (moveDown >= 0 && moveDown < (int)entries.size()-1) {
                            std::swap(entries[moveDown], entries[moveDown+1]);
                            app->clDirty = true;
                        }
                    }
                }

                // Auto-save
                if (app->clDirty) {
                    app->customLists.save();
                    app->clDirty = false;
                }
            }
        }
        ImGui::End();

        // ════════════════════════════════════════════════════════════════
        // LOG VIEWER
        // ════════════════════════════════════════════════════════════════
        if (app->showLog) {
            ImGui::SetNextWindowSize({700*dpi, 480*dpi}, ImGuiCond_FirstUseEver);
            if (ImGui::Begin("Log Viewer", &app->showLog)) {
                ImGui::TextDisabled("Lines: %d  Size: %zu / 30720 B",
                    app->log.count(), app->log.bytes());
                ImGui::SameLine(0,10);
                if (ImGui::SmallButton("Clear##lv")) app->log.clear();
                ImGui::SameLine(0,6);
                if (ImGui::SmallButton("Copy All"))
                    ImGui::SetClipboardText(app->log.fullText().c_str());
                ImGui::Separator();
                ImGui::BeginChild("##lvt", {0,0}, false, ImGuiWindowFlags_HorizontalScrollbar);
                auto lvSnap = app->log.snap();
                for (auto& ln : lvSnap) {
                    bool isErr = ln.find("ail") != std::string::npos ||
                                 ln.find("rror") != std::string::npos;
                    if (isErr)
                        ImGui::TextColored({0.95f,0.42f,0.38f,1.0f}, "%s", ln.c_str());
                    else
                        ImGui::TextUnformatted(ln.c_str());
                }
                if (app->log.scroll && ImGui::GetScrollY() >= ImGui::GetScrollMaxY() - 10)
                    ImGui::SetScrollHereY(1.0f);
                ImGui::EndChild();
            }
            ImGui::End();
        }

        // ════════════════════════════════════════════════════════════════
        // ── TAB 1: CCcam + AI ENGINE (docked in main content area) ──
        // ════════════════════════════════════════════════════════════════
        if (app->mainTab == 1) {
            ImGui::SetNextWindowPos({OX+P, OY+P+CONN_H+P});
            ImGui::SetNextWindowSize({LW, CH_H});
            if (ImGui::Begin("##ccai_docked", nullptr,
                ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoMove | ImGuiWindowFlags_NoResize)) {
                ImGui::PushStyleVar(ImGuiStyleVar_ItemSpacing, {6*dpi, 3*dpi});
                ImGui::PushStyleVar(ImGuiStyleVar_FramePadding, {6*dpi, 3*dpi});
                // ── Ping state (static across frames) ──
                static std::atomic<bool> pinging{false};
                static std::atomic<int>  pingDone{0}, pingTotal{0};
                static std::atomic<bool> testing{false};
                static std::atomic<int>  testDone{0}, testTotal{0};
                static time_t lastAutoClean = 0;

                bool srv = app->cccam.running.load();
                auto& srvList = app->cccam.cfg.servers;

                // ── Top Status Dashboard ──
                {
                    ImVec4 sc2 = srv ? ImVec4(0.18f,0.82f,0.45f,1)
                                     : ImVec4(0.90f,0.35f,0.35f,1);
                    ImGui::TextColored(sc2, srv ? "RUNNING" : "STOPPED");
                    ImGui::SameLine(0,12);
                    int nAlive = 0, nDead = 0;
                    for (auto& s : srvList) { if (s.ping_status==1) nAlive++; if (s.ping_status==-1) nDead++; }
                    ImGui::Text("Servers: %d", (int)srvList.size());
                    ImGui::SameLine(0,6);
                    ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d alive", nAlive);
                    ImGui::SameLine(0,6);
                    if (nDead > 0) ImGui::TextColored({0.9f,0.3f,0.3f,1}, "%d dead", nDead);
                    ImGui::SameLine(0,12);
                    ImGui::TextDisabled("Clients: %d  ECM: %d ok / %d fail",
                        app->cccam.clients.load(),
                        app->cccam.ecmOk.load(), app->cccam.ecmFail.load());
                }

                // ── Save config helper lambda ──
                auto saveConfig = [&]() {
                    cccam::CccamConfig ccfg;
                    ccfg.port = app->cccam.cfg.port; ccfg.user = app->cccam.cfg.user;
                    ccfg.pass = app->cccam.cfg.pass; ccfg.log_ecm = app->cccam.cfg.log_ecm;
                    ccfg.global_proxy_host = app->cccam.cfg.global_proxy_host;
                    ccfg.global_proxy_port = app->cccam.cfg.global_proxy_port;
                    ccfg.global_proxy_user = app->cccam.cfg.global_proxy_user;
                    ccfg.global_proxy_pass = app->cccam.cfg.global_proxy_pass;
                    ccfg.servers = srvList; ccfg.save();
                };

                // ── Start/Stop + Save row ──
                if (!srv) {
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.12f,0.55f,0.18f,0.80f});
                    if (ImGui::Button("Start Server", {120*dpi,26*dpi})) {
                        app->cccam.cfg.applyGlobalProxy();
                        saveConfig();
                        app->cccam.start([app](const std::string& m) {
                            try { if (app && !app->down) app->cccamLog.add(m); } catch (...) {}
                        });
                    }
                    ImGui::PopStyleColor();
                } else {
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.55f,0.18f,0.18f,0.80f});
                    if (ImGui::Button("Stop Server", {120*dpi,26*dpi}))
                        app->cccam.stop();
                    ImGui::PopStyleColor();
                }
                ImGui::SameLine(0,6);
                if (ImGui::Button("Save##ccai", {70*dpi,26*dpi})) {
                    saveConfig();
                    app->log.add("[CCcam] Config saved");
                }
                ImGui::SameLine(0,6);
                if (ImGui::SmallButton("CCcam Log##ccai2")) app->showCccamLog = true;

                ImGui::Separator();

                // ── 6 Tabs: Dashboard | Servers | Import | AI Engine | Learner | Config ──
                int& ccTab = app->ccaiTab;
                {
                    if (ImGui::BeginTabBar("##ccaiTabs")) {
                        if (ImGui::BeginTabItem("Dashboard"))  { ccTab = 0; ImGui::EndTabItem(); }
                        if (ImGui::BeginTabItem("Servers"))    { ccTab = 1; ImGui::EndTabItem(); }
                        if (ImGui::BeginTabItem("Import"))     { ccTab = 2; ImGui::EndTabItem(); }
                        if (ImGui::BeginTabItem("AI Engine"))  { ccTab = 3; ImGui::EndTabItem(); }
                        if (ImGui::BeginTabItem("Learner"))    { ccTab = 4; ImGui::EndTabItem(); }
                        if (ImGui::BeginTabItem("Config"))     { ccTab = 5; ImGui::EndTabItem(); }
                        ImGui::EndTabBar();
                    }
                }

                // ═══════════════════════════════════════════════════════════
                // TAB 0: DASHBOARD (overview of everything)
                // ═══════════════════════════════════════════════════════════
                if (ccTab == 0) {
                    auto& ai = app->cccam.aiPredictor;
                    auto aiStats = ai.getStats();
                    
                    if (ImGui::CollapsingHeader("Server Status##dash", ImGuiTreeNodeFlags_DefaultOpen)) {
                        int nOk = 0, nFail = 0, nUnk = 0;
                        for (auto& s : srvList) {
                            if (s.ping_status == 1) nOk++;
                            else if (s.ping_status == -1) nFail++;
                            else nUnk++;
                        }
                        if (ImGui::BeginTable("##dash_srv", 2, ImGuiTableFlags_SizingFixedFit)) {
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Servers");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::Text("%d total  ", (int)srvList.size());
                            ImGui::SameLine(0,6);
                            ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d ok", nOk);
                            ImGui::SameLine(0,6);
                            if (nFail > 0) ImGui::TextColored({0.9f,0.3f,0.3f,1}, "%d dead", nFail);
                            ImGui::SameLine(0,6);
                            ImGui::TextDisabled("%d unk", nUnk);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Clients");
                            ImGui::TableSetColumnIndex(1); ImGui::Text("%d", app->cccam.clients.load());
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("ECM");
                            ImGui::TableSetColumnIndex(1); ImGui::Text("%d ok / %d fail", app->cccam.ecmOk.load(), app->cccam.ecmFail.load());
                            ImGui::EndTable();
                        }
                    }

                    if (ImGui::CollapsingHeader("AI Engine##dash", ImGuiTreeNodeFlags_DefaultOpen)) {
                        bool aiOn = app->cccam.aiEnabled;
                        if (ImGui::BeginTable("##dash_ai", 2, ImGuiTableFlags_SizingFixedFit)) {
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Status");
                            ImGui::TableSetColumnIndex(1);
                            if (aiOn)
                                ImGui::TextColored({0.3f,0.9f,0.4f,1}, "Active");
                            else
                                ImGui::TextColored({0.6f,0.6f,0.6f,1}, "Disabled");
                            ImGui::SameLine(0,10);
                            if (aiStats.ollama_ok)
                                ImGui::TextColored({0.5f,0.8f,1.0f,1}, "Ollama OK");
                            else
                                ImGui::TextColored({0.9f,0.5f,0.2f,1}, "Ollama Offline");
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Learn");
                            ImGui::TableSetColumnIndex(1); ImGui::Text("%s", ai.learning.load() ? "On" : "Off");
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Samples");
                            ImGui::TableSetColumnIndex(1); ImGui::Text("%d", aiStats.total_samples);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Patterns");
                            ImGui::TableSetColumnIndex(1); ImGui::Text("%d", aiStats.unique_patterns);
                            ImGui::EndTable();
                        }
                    }

                    if (ImGui::CollapsingHeader("CW Cache##dash", ImGuiTreeNodeFlags_DefaultOpen)) {
                        ImGui::TextDisabled("Cache Hits:");   ImGui::SameLine(100*dpi);
                        ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d", aiStats.cache_hits);
                        ImGui::SameLine(0,12);
                        ImGui::TextDisabled("Misses:");       ImGui::SameLine(220*dpi);
                        ImGui::Text("%d", aiStats.cache_misses);
                        ImGui::SameLine(0,12);
                        ImGui::TextDisabled("Live:");         ImGui::SameLine(330*dpi);
                        ImGui::Text("%d entries", aiStats.cache_size);
                        
                        int totalReq = aiStats.cache_hits + aiStats.cache_misses;
                        float hitRate = totalReq > 0 ? (float)aiStats.cache_hits / totalReq : 0;
                        ImGui::TextDisabled("Hit Rate:");     ImGui::SameLine(100*dpi);
                        ImGui::ProgressBar(hitRate, {200*dpi, 14*dpi});
                        
                        ImGui::TextDisabled("Predictions:");  ImGui::SameLine(100*dpi);
                        ImGui::Text("%d  (correct: %d)", aiStats.predictions, aiStats.correct);
                        if (aiStats.predictions > 0) {
                            ImGui::SameLine(0,8);
                            ImGui::TextColored(aiStats.accuracy > 80 ? ImVec4{0.3f,0.9f,0.4f,1} :
                                               aiStats.accuracy > 50 ? ImVec4{0.9f,0.8f,0.2f,1} :
                                                                       ImVec4{0.9f,0.3f,0.3f,1},
                                               "%.1f%%", aiStats.accuracy);
                        }
                        ImGui::TextDisabled("Srv Cache:");    ImGui::SameLine(100*dpi);
                        ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d hits", app->cccam.ecmCacheHits.load());
                    }

                    if (ImGui::CollapsingHeader("Global Proxy (SOCKS5)##dash", ImGuiTreeNodeFlags_DefaultOpen)) {
                        static char gph[128] = {}, gpu[64] = {}, gpp[64] = {};
                        static int  gport = 0;
                        static bool gpInit = false;
                        if (!gpInit) {
                            gpInit = true;
                            snprintf(gph, sizeof(gph), "%s", app->cccam.cfg.global_proxy_host.c_str());
                            snprintf(gpu, sizeof(gpu), "%s", app->cccam.cfg.global_proxy_user.c_str());
                            snprintf(gpp, sizeof(gpp), "%s", app->cccam.cfg.global_proxy_pass.c_str());
                            gport = app->cccam.cfg.global_proxy_port;
                        }
                        ImGui::SetNextItemWidth(160*dpi);
                        if (ImGui::InputText("Host##gp", gph, sizeof(gph)))
                            app->cccam.cfg.global_proxy_host = gph;
                        ImGui::SameLine(0,4);
                        ImGui::SetNextItemWidth(70*dpi);
                        if (ImGui::InputInt("Port##gp", &gport, 0, 0)) {
                            if (gport >= 0 && gport < 65536) app->cccam.cfg.global_proxy_port = gport;
                        }
                        ImGui::SameLine(0,8);
                        ImGui::SetNextItemWidth(100*dpi);
                        if (ImGui::InputText("User##gp", gpu, sizeof(gpu)))
                            app->cccam.cfg.global_proxy_user = gpu;
                        ImGui::SameLine(0,4);
                        ImGui::SetNextItemWidth(100*dpi);
                        if (ImGui::InputText("Pass##gp", gpp, sizeof(gpp), ImGuiInputTextFlags_Password))
                            app->cccam.cfg.global_proxy_pass = gpp;
                        ImGui::SameLine(0,8);
                        if (ImGui::SmallButton("Apply to All##gp")) {
                            app->cccam.cfg.applyGlobalProxy();
                            app->log.add("[CCcam] Global proxy applied to all servers");
                        }
                        if (!app->cccam.cfg.global_proxy_host.empty() && app->cccam.cfg.global_proxy_port > 0)
                            ImGui::TextColored({0.5f,0.8f,1.0f,1}, "Proxy: %s:%d", 
                                app->cccam.cfg.global_proxy_host.c_str(), app->cccam.cfg.global_proxy_port);
                        else
                            ImGui::TextDisabled("No global proxy set (direct connection)");
                    }

                    if (ImGui::CollapsingHeader("CW Learner##dash")) {
                        auto lStats = app->cccam.learner.getStats();
                        ImGui::Text("Samples: %d | Unique ECM: %d | CW: %d | Servers: %d",
                            lStats.total_samples, lStats.unique_ecms, lStats.unique_cws, lStats.servers);
                        ImGui::Text("Predictions: %d used (Exact: %d, Pattern: %d) | Accuracy: %.1f%%",
                            lStats.pred_used, lStats.pred_exact, lStats.pred_pattern, lStats.pred_accuracy);
                        ImGui::TextDisabled("Auto-save: ON (every 50 learns or 30s) - works offline");
                    }

                    // ── Background Services ──
                    if (ImGui::CollapsingHeader("ECM Harvester##dash", ImGuiTreeNodeFlags_DefaultOpen)) {
                        auto hs = app->harvester.getStats();
                        if (ImGui::BeginTable("##dash_harv", 2, ImGuiTableFlags_SizingFixedFit)) {
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Status");
                            ImGui::TableSetColumnIndex(1);
                            if (hs.running)
                                ImGui::TextColored({0.3f,0.9f,0.4f,1}, "Running");
                            else
                                ImGui::TextColored({0.9f,0.3f,0.3f,1}, "Stopped");
                            ImGui::SameLine(0,10);
                            ImGui::TextDisabled("Upstreams: %d/%d", hs.servers_connected, hs.servers_total);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Targets");
                            ImGui::TableSetColumnIndex(1); ImGui::Text("%d CAID/SID pairs", hs.total_targets);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Harvests");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::Text("%d total", hs.total_harvests);
                            ImGui::SameLine(0,8);
                            ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d ok", hs.total_success);
                            ImGui::SameLine(0,8);
                            if (hs.total_fail > 0)
                                ImGui::TextColored({0.9f,0.3f,0.3f,1}, "%d fail", hs.total_fail);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Rate");
                            ImGui::TableSetColumnIndex(1); ImGui::Text("%.1f/min  |  Cycles: %d", hs.harvest_rate, hs.cycle_count);
                            if (!hs.current_target.empty()) {
                                ImGui::TableNextRow();
                                ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Current");
                                ImGui::TableSetColumnIndex(1); ImGui::TextColored({0.5f,0.8f,1.0f,1}, "%s", hs.current_target.c_str());
                            }
                            ImGui::EndTable();
                        }
                        // Controls
                        if (hs.running) {
                            ImGui::PushStyleColor(ImGuiCol_Button, {0.55f,0.18f,0.18f,0.80f});
                            if (ImGui::SmallButton("Stop Harvester")) app->harvester.stop();
                            ImGui::PopStyleColor();
                        } else {
                            ImGui::PushStyleColor(ImGuiCol_Button, {0.12f,0.55f,0.18f,0.80f});
                            if (ImGui::SmallButton("Start Harvester")) app->harvester.start(&app->cccam.cfg.servers);
                            ImGui::PopStyleColor();
                        }
                        ImGui::SameLine(0,8);
                        ImGui::SetNextItemWidth(80*dpi);
                        ImGui::SliderInt("Interval ms##harv", &app->harvester.probe_interval_ms, 500, 10000);
                    }

                    if (ImGui::CollapsingHeader("Offline CW Database##dash", ImGuiTreeNodeFlags_DefaultOpen)) {
                        auto os = app->offlineCwDb.getStats();
                        if (ImGui::BeginTable("##dash_offcw", 2, ImGuiTableFlags_SizingFixedFit)) {
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Entries");
                            ImGui::TableSetColumnIndex(1); ImGui::Text("%d CWs  |  %d channels", os.total_entries, os.unique_channels);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Lookups");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::Text("%d total", os.lookups);
                            ImGui::SameLine(0,8);
                            ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d hits", os.hits);
                            ImGui::SameLine(0,8);
                            ImGui::TextDisabled("%d misses", os.misses);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Hit Rate");
                            ImGui::TableSetColumnIndex(1);
                            float hr = os.lookups > 0 ? (float)os.hits / os.lookups : 0;
                            ImGui::ProgressBar(hr, {140*dpi, 14*dpi});
                            ImGui::SameLine(0,6);
                            ImGui::Text("%.1f%%", os.hit_rate);
                            ImGui::EndTable();
                        }
                        if (ImGui::SmallButton("Save CW DB")) { app->offlineCwDb.save(); app->log.add("Offline CW DB saved"); }
                        ImGui::SameLine(0,8);
                        if (ImGui::SmallButton("Reload CW DB")) { app->offlineCwDb.load(); app->log.add("Offline CW DB reloaded"); }
                    }

                    if (ImGui::CollapsingHeader("AI Trainer##dash", ImGuiTreeNodeFlags_DefaultOpen)) {
                        auto ts = app->aiTrainer.getStats();
                        if (ImGui::BeginTable("##dash_train", 2, ImGuiTableFlags_SizingFixedFit)) {
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Status");
                            ImGui::TableSetColumnIndex(1);
                            if (ts.running)
                                ImGui::TextColored({0.3f,0.9f,0.4f,1}, "Running");
                            else
                                ImGui::TextColored({0.9f,0.3f,0.3f,1}, "Stopped");
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Cycles");
                            ImGui::TableSetColumnIndex(1); ImGui::Text("%d  |  Patterns: %d  |  Saves: %d", ts.train_cycles, ts.patterns_discovered, ts.models_updated);
                            if (!ts.status.empty()) {
                                ImGui::TableNextRow();
                                ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Last");
                                ImGui::TableSetColumnIndex(1); ImGui::TextColored({0.5f,0.8f,1.0f,1}, "%s", ts.status.c_str());
                            }
                            ImGui::EndTable();
                        }
                        if (ts.running) {
                            ImGui::PushStyleColor(ImGuiCol_Button, {0.55f,0.18f,0.18f,0.80f});
                            if (ImGui::SmallButton("Stop Trainer")) app->aiTrainer.stop();
                            ImGui::PopStyleColor();
                        } else {
                            ImGui::PushStyleColor(ImGuiCol_Button, {0.12f,0.55f,0.18f,0.80f});
                            if (ImGui::SmallButton("Start Trainer")) {
                                app->aiTrainer.predictor = &app->cccam.aiPredictor;
                                app->aiTrainer.learner = &app->cccam.learner;
                                app->aiTrainer.offlineDb = &app->offlineCwDb;
                                app->aiTrainer.start();
                            }
                            ImGui::PopStyleColor();
                        }
                        ImGui::SameLine(0,8);
                        ImGui::SetNextItemWidth(80*dpi);
                        ImGui::SliderInt("Train interval s##train", &app->aiTrainer.train_interval_s, 10, 300);
                    }

                    // ── CA Engine (Multi-CA Decryption) ──
                    if (ImGui::CollapsingHeader("CA Engine (Multi-CA Decryption)##dash", ImGuiTreeNodeFlags_DefaultOpen)) {
                        auto ces = app->cccam.caEngine.getStats();
                        if (ImGui::BeginTable("##dash_ca", 2, ImGuiTableFlags_SizingFixedFit)) {
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Channels");
                            ImGui::TableSetColumnIndex(1); ImGui::Text("%d tracked", ces.total_channels);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("CW Predict");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::Text("%d made", ces.total_predictions);
                            ImGui::SameLine(0,8);
                            ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d correct", ces.total_correct);
                            ImGui::SameLine(0,8);
                            if (ces.total_predictions > 0)
                                ImGui::TextColored(ces.overall_accuracy > 60 ? ImVec4{0.3f,0.9f,0.4f,1} :
                                                   ces.overall_accuracy > 30 ? ImVec4{0.9f,0.8f,0.2f,1} :
                                                                               ImVec4{0.9f,0.3f,0.3f,1},
                                                   "%.1f%%", ces.overall_accuracy);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Pre-fetch");
                            ImGui::TableSetColumnIndex(1); ImGui::Text("%d triggers", ces.prefetch_triggers);
                            ImGui::SameLine(0,12);
                            ImGui::TextDisabled("Routes: %d switches", ces.route_switches);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("BISS");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::TextColored({0.5f,0.8f,1.0f,1}, "%d keys found", ces.biss_keys_found);
                            ImGui::SameLine(0,12);
                            ImGui::TextDisabled("PowerVu: %d rolls", ces.powervu_rolls);
                            ImGui::EndTable();
                        }

                        // Per-CA system breakdown
                        if (!ces.ca_stats.empty() && ImGui::TreeNode("Per-CA System Breakdown##ca")) {
                            if (ImGui::BeginTable("##ca_sys", 7, ImGuiTableFlags_Borders | ImGuiTableFlags_RowBg | ImGuiTableFlags_SizingFixedFit)) {
                                ImGui::TableSetupColumn("CA System");
                                ImGui::TableSetupColumn("Ch");
                                ImGui::TableSetupColumn("ECMs");
                                ImGui::TableSetupColumn("Rate");
                                ImGui::TableSetupColumn("Latency");
                                ImGui::TableSetupColumn("Predict");
                                ImGui::TableSetupColumn("Accuracy");
                                ImGui::TableHeadersRow();
                                for (auto& cs : ces.ca_stats) {
                                    ImGui::TableNextRow();
                                    ImGui::TableSetColumnIndex(0);
                                    ImGui::TextColored({0.5f,0.8f,1.0f,1}, "%s", cccam::caTypeName(cs.type));
                                    ImGui::TableSetColumnIndex(1); ImGui::Text("%d", cs.channels);
                                    ImGui::TableSetColumnIndex(2); ImGui::Text("%d", cs.ecm_total);
                                    ImGui::TableSetColumnIndex(3);
                                    ImGui::TextColored(cs.success_rate > 80 ? ImVec4{0.3f,0.9f,0.4f,1} :
                                                       cs.success_rate > 40 ? ImVec4{0.9f,0.8f,0.2f,1} :
                                                                              ImVec4{0.9f,0.3f,0.3f,1},
                                                       "%.0f%%", cs.success_rate);
                                    ImGui::TableSetColumnIndex(4);
                                    if (cs.avg_latency > 0) ImGui::Text("%.0fms", cs.avg_latency);
                                    else ImGui::TextDisabled("-");
                                    ImGui::TableSetColumnIndex(5); ImGui::Text("%d", cs.predictions_made);
                                    ImGui::TableSetColumnIndex(6);
                                    if (cs.predictions_made > 0)
                                        ImGui::TextColored(cs.prediction_accuracy > 60 ? ImVec4{0.3f,0.9f,0.4f,1} :
                                                           cs.prediction_accuracy > 30 ? ImVec4{0.9f,0.8f,0.2f,1} :
                                                                                         ImVec4{0.9f,0.3f,0.3f,1},
                                                           "%.0f%%", cs.prediction_accuracy);
                                    else ImGui::TextDisabled("-");
                                }
                                ImGui::EndTable();
                            }
                            ImGui::TreePop();
                        }

                        // CA system descriptions
                        if (ImGui::TreeNode("CA System Profiles##ca")) {
                            for (auto& p : cccam::kCaProfiles) {
                                ImGui::BulletText("%s: %s", cccam::caTypeName(p.type), p.description);
                                ImGui::SameLine(0,0);
                                ImGui::TextDisabled(" [rot=%.0fs, CCcam=%s, pred=%d%%]",
                                    p.typical_rotation_sec,
                                    p.supports_cccam ? "Y" : "N",
                                    p.prediction_confidence);
                            }
                            ImGui::TreePop();
                        }

                        if (ImGui::SmallButton("Save CA Engine")) {
                            app->cccam.caEngine.save();
                            app->log.add("CA Engine saved");
                        }
                    }

                    // ── Channel Scanner ──
                    if (ImGui::CollapsingHeader("Channel Scanner##dash", ImGuiTreeNodeFlags_DefaultOpen)) {
                        auto ss = app->scanner.getStats();
                        // Status + controls
                        ImGui::TextColored(ss.running ? (ss.paused ? ImVec4{0.9f,0.8f,0.2f,1} : ImVec4{0.3f,0.9f,0.4f,1}) : ImVec4{0.5f,0.5f,0.5f,1},
                            ss.running ? (ss.paused ? "PAUSED" : "SCANNING") : "STOPPED");
                        ImGui::SameLine(0,12);
                        if (!ss.running) {
                            if (ImGui::SmallButton("Start Scanner")) {
                                // Build scan queue from loaded channels
                                std::vector<cccam::ScannedChannel> encCh;
                                {
                                    std::lock_guard<std::mutex> g(app->chMu);
                                    for (auto& ch : app->channels) {
                                        if (!ch.is_scrambled) continue;
                                        if (ch.video_pid <= 0) continue;
                                        cccam::ScannedChannel sc;
                                        sc.service_index = ch.service_index;
                                        sc.service_id = ch.service_id;
                                        sc.service_name = ch.service_name;
                                        sc.is_radio = ch.is_radio;
                                        encCh.push_back(std::move(sc));
                                    }
                                }
                                if (encCh.empty()) {
                                    app->log.add("[Scanner] No encrypted channels — load channel list first");
                                } else {
                                    app->scanner.setChannelList(encCh);
                                    app->scanner.load(); // restore previous scan stats
                                    app->scanner.start();
                                }
                            }
                        } else {
                            if (ss.paused) {
                                if (ImGui::SmallButton("Resume")) app->scanner.resume();
                            } else {
                                if (ImGui::SmallButton("Pause")) app->scanner.pause();
                            }
                            ImGui::SameLine(0,8);
                            if (ImGui::SmallButton("Stop Scanner")) app->scanner.stop();
                        }

                        if (ss.running && ss.total_channels > 0) {
                            ImGui::SameLine(0,12);
                            ImGui::TextDisabled("Cycle %.0f%%", ss.cycle_progress * 100);
                            ImGui::ProgressBar(ss.cycle_progress, {-1, 3*dpi}, "");
                        }

                        if (ImGui::BeginTable("##dash_scan", 2, ImGuiTableFlags_SizingFixedFit)) {
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Queue");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::Text("%d channels", ss.total_channels);
                            ImGui::SameLine(0,8);
                            ImGui::TextDisabled("(%d scanned)", ss.scanned);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Signal");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d good", ss.good_signal);
                            ImGui::SameLine(0,6);
                            ImGui::TextColored({0.9f,0.8f,0.2f,1}, "%d poor", ss.poor_signal);
                            ImGui::SameLine(0,6);
                            ImGui::TextColored({0.9f,0.3f,0.3f,1}, "%d no-sig", ss.no_signal);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Samples");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::Text("%d total", ss.total_samples);
                            ImGui::SameLine(0,6);
                            ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d CW", ss.total_cw_ok);
                            ImGui::SameLine(0,6);
                            ImGui::TextColored({0.9f,0.3f,0.3f,1}, "%d fail", ss.total_cw_fail);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Cycles");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::Text("%d complete", ss.scan_cycles);
                            if (ss.elapsed_min > 0) {
                                ImGui::SameLine(0,8);
                                ImGui::TextDisabled("%.1f min", ss.elapsed_min);
                            }
                            if (ss.running && !ss.current_name.empty()) {
                                ImGui::TableNextRow();
                                ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Current");
                                ImGui::TableSetColumnIndex(1);
                                ImGui::TextColored({0.4f,0.8f,1.0f,1}, "%s (#%d)", ss.current_name.c_str(), ss.current_index);
                            }
                            ImGui::EndTable();
                        }

                        // Config sliders
                        ImGui::SetNextItemWidth(80*dpi);
                        ImGui::SliderInt("Dwell sec##scan", &app->scanner.dwell_time_sec, 10, 120);
                        ImGui::SameLine(0,12);
                        ImGui::SetNextItemWidth(80*dpi);
                        ImGui::SliderInt("Sig timeout##scan", &app->scanner.signal_timeout_sec, 2, 15);
                        ImGui::SameLine(0,12);
                        ImGui::SetNextItemWidth(80*dpi);
                        ImGui::SliderInt("Max ch##scan", &app->scanner.max_channels, 50, 500);

                        // Scanned channels detail tree
                        if (ss.scanned > 0 && ImGui::TreeNode("Scanned Channels##scan")) {
                            auto scCh = app->scanner.getScannedChannels();
                            if (ImGui::BeginTable("##scan_ch", 7, ImGuiTableFlags_Borders | ImGuiTableFlags_RowBg | ImGuiTableFlags_SizingFixedFit | ImGuiTableFlags_ScrollY, {0, 200*dpi})) {
                                ImGui::TableSetupScrollFreeze(0, 1);
                                ImGui::TableSetupColumn("#");
                                ImGui::TableSetupColumn("Name");
                                ImGui::TableSetupColumn("Signal");
                                ImGui::TableSetupColumn("CW OK");
                                ImGui::TableSetupColumn("Fail");
                                ImGui::TableSetupColumn("Latency");
                                ImGui::TableSetupColumn("CAID");
                                ImGui::TableHeadersRow();
                                for (auto& sc : scCh) {
                                    if (sc.scan_count == 0) continue;
                                    ImGui::TableNextRow();
                                    ImGui::TableSetColumnIndex(0); ImGui::Text("%d", sc.service_index);
                                    ImGui::TableSetColumnIndex(1); ImGui::Text("%s", sc.service_name.c_str());
                                    ImGui::TableSetColumnIndex(2);
                                    {
                                        const char* sq = "?";
                                        ImVec4 sqc = {0.5f,0.5f,0.5f,1};
                                        if (sc.signal_quality == cccam::ScannedChannel::Quality::Good) { sq = "Good"; sqc = {0.3f,0.9f,0.4f,1}; }
                                        else if (sc.signal_quality == cccam::ScannedChannel::Quality::Poor) { sq = "Poor"; sqc = {0.9f,0.8f,0.2f,1}; }
                                        else if (sc.signal_quality == cccam::ScannedChannel::Quality::NoSignal) { sq = "NoSig"; sqc = {0.9f,0.3f,0.3f,1}; }
                                        ImGui::TextColored(sqc, "%s", sq);
                                    }
                                    ImGui::TableSetColumnIndex(3);
                                    ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d", sc.cw_obtained);
                                    ImGui::TableSetColumnIndex(4);
                                    if (sc.cw_failed > 0) ImGui::TextColored({0.9f,0.3f,0.3f,1}, "%d", sc.cw_failed);
                                    else ImGui::TextDisabled("0");
                                    ImGui::TableSetColumnIndex(5);
                                    if (sc.avg_latency_ms > 0) ImGui::Text("%.0fms", sc.avg_latency_ms);
                                    else ImGui::TextDisabled("-");
                                    ImGui::TableSetColumnIndex(6);
                                    if (sc.caid > 0) ImGui::Text("%04X", sc.caid);
                                    else ImGui::TextDisabled("-");
                                }
                                ImGui::EndTable();
                            }
                            ImGui::TreePop();
                        }

                        if (ImGui::SmallButton("Save Scan Data##scan")) {
                            app->scanner.save();
                            app->log.add("Scanner data saved");
                        }
                    }

                    // ── Turbo Pipeline (Sub-3s ECM Response) ──
                    if (ImGui::CollapsingHeader("Turbo Pipeline (Sub-3s ECM)##dash", ImGuiTreeNodeFlags_DefaultOpen)) {
                        auto ts = app->cccam.turbo.getStats();
                        if (ImGui::BeginTable("##dash_turbo", 2, ImGuiTableFlags_SizingFixedFit)) {
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Total ECM");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::Text("%d", ts.total_ecm);
                            ImGui::SameLine(0,8);
                            ImGui::TextColored(ts.successRate() > 80 ? ImVec4{0.3f,0.9f,0.4f,1} :
                                               ts.successRate() > 40 ? ImVec4{0.9f,0.8f,0.2f,1} :
                                                                        ImVec4{0.9f,0.3f,0.3f,1},
                                               "%.1f%% success", ts.successRate());
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Avg Latency");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::TextColored(ts.avg_response_ms < 1000 ? ImVec4{0.3f,0.9f,0.4f,1} :
                                               ts.avg_response_ms < 3000 ? ImVec4{0.9f,0.8f,0.2f,1} :
                                                                            ImVec4{0.9f,0.3f,0.3f,1},
                                               "%.0f ms", ts.avg_response_ms);
                            ImGui::SameLine(0,12);
                            ImGui::TextColored({0.9f,0.3f,0.3f,1}, "%d timeouts (>%.0fs)", ts.timeouts, app->cccam.turbo.max_response_sec);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("Layer Hits");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::TextColored({0.3f,0.9f,0.4f,1}, "Cache:%d", ts.cache_hits);
                            ImGui::SameLine(0,6);
                            ImGui::TextColored({0.4f,0.8f,1.0f,1}, "Upstream:%d", ts.upstream_hits);
                            ImGui::SameLine(0,6);
                            ImGui::TextColored({0.6f,0.7f,1.0f,1}, "Offline:%d", ts.offline_hits);
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("");
                            ImGui::TableSetColumnIndex(1);
                            ImGui::TextColored({0.8f,0.6f,1.0f,1}, "CA-Pred:%d", ts.ca_predict_hits);
                            ImGui::SameLine(0,6);
                            ImGui::TextColored({1.0f,0.6f,0.8f,1}, "AI:%d", ts.ai_hits);
                            ImGui::SameLine(0,6);
                            ImGui::TextDisabled("Prefetch:%d/%d", ts.prefetch_saved, ts.prefetch_triggered);
                            ImGui::EndTable();
                        }

                        // Turbo pipeline layer breakdown bar
                        if (ts.total_ecm > 0) {
                            int ok = ts.cache_hits + ts.upstream_hits + ts.offline_hits + ts.ca_predict_hits + ts.ai_hits;
                            int fail = ts.total_ecm - ok;
                            float w = ImGui::GetContentRegionAvail().x;
                            float h = 14*dpi;
                            ImVec2 p = ImGui::GetCursorScreenPos();
                            auto* dl = ImGui::GetWindowDrawList();
                            float x = p.x;
                            auto bar = [&](int cnt, ImU32 col) {
                                if (cnt <= 0) return;
                                float bw = w * ((float)cnt / ts.total_ecm);
                                dl->AddRectFilled({x, p.y}, {x+bw, p.y+h}, col, 2);
                                x += bw;
                            };
                            bar(ts.cache_hits, IM_COL32(70, 230, 100, 200));
                            bar(ts.upstream_hits, IM_COL32(100, 200, 255, 200));
                            bar(ts.offline_hits, IM_COL32(150, 180, 255, 200));
                            bar(ts.ca_predict_hits, IM_COL32(200, 150, 255, 200));
                            bar(ts.ai_hits, IM_COL32(255, 150, 200, 200));
                            bar(fail, IM_COL32(230, 70, 70, 200));
                            ImGui::Dummy({w, h + 2});
                            ImGui::TextDisabled("Cache | Upstream | Offline | CA-Pred | AI | Fail");
                        }

                        if (ImGui::SmallButton("Reset Stats##turbo")) {
                            app->cccam.turbo.reset();
                        }
                    }
                }

                // ═══════════════════════════════════════════════════════════
                // TAB 1: SERVERS
                // ═══════════════════════════════════════════════════════════
                if (ccTab == 1) {
                    // Toolbar: Add / Ping All / Remove Dead / Auto-clean
                    if (ImGui::Button("+ Add Server", {100*dpi,24*dpi})) {
                        cccam::UpstreamServer ns;
                        ns.name = "Server " + std::to_string(srvList.size()+1);
                        ns.port = 12000; ns.enabled = true;
                        srvList.push_back(std::move(ns));
                    }
                    ImGui::SameLine(0,6);
                    bool anyBusy = pinging.load() || testing.load();
                    // ── TCP Ping All ──
                    if (pinging.load()) {
                        int done = pingDone.load(), total = pingTotal.load();
                        float pct = total > 0 ? (float)done / total : 0;
                        ImGui::ProgressBar(pct, {110*dpi, 22*dpi});
                        ImGui::SameLine(0,4);
                        ImGui::TextDisabled("Ping %d/%d", done, total);
                    } else {
                        if (anyBusy) ImGui::BeginDisabled();
                        if (ImGui::Button("TCP Ping", {78*dpi,24*dpi})) {
                            app->cccam.cfg.applyGlobalProxy();
                            pinging = true; pingDone = 0; pingTotal = (int)srvList.size();
                            auto appW = app;
                            std::thread([appW](){
                                auto& list = appW->cccam.cfg.servers;
                                int total = (int)list.size();
                                const int maxConc = 32;
                                std::atomic<int> idx{0};
                                auto worker = [&]() {
                                    while (true) {
                                        int i = idx.fetch_add(1);
                                        if (i >= total) break;
                                        auto& s = list[i];
                                        if (!s.valid()) {
                                            s.ping_status = -1; s.ping_ms = -1;
                                        } else {
                                            int ms = cccam::tcpPing(s.host, s.port,
                                                s.proxy_host, s.proxy_port, 3000);
                                            s.ping_ms = ms;
                                            s.ping_status = (ms >= 0) ? 1 : -1;
                                        }
                                        if (s.ping_status == -1) { if (s.dead_since == 0) s.dead_since = time(nullptr); }
                                        else s.dead_since = 0;
                                        pingDone++;
                                    }
                                };
                                int nT = std::min(maxConc, total);
                                std::vector<std::thread> pool;
                                for (int t = 0; t < nT; t++) pool.emplace_back(worker);
                                for (auto& t : pool) t.join();
                                pinging = false;
                            }).detach();
                        }
                        if (anyBusy) ImGui::EndDisabled();
                    }
                    ImGui::SameLine(0,6);
                    // ── Full CCcam Handshake Test ──
                    if (testing.load()) {
                        int done = testDone.load(), total = testTotal.load();
                        float pct = total > 0 ? (float)done / total : 0;
                        ImGui::ProgressBar(pct, {130*dpi, 22*dpi});
                        ImGui::SameLine(0,4);
                        ImGui::TextColored({1.0f,0.85f,0.2f,1}, "Test %d/%d", done, total);
                    } else {
                        if (anyBusy) ImGui::BeginDisabled();
                        ImGui::PushStyleColor(ImGuiCol_Button, {0.12f,0.42f,0.70f,0.80f});
                        if (ImGui::Button("Full Test", {80*dpi,24*dpi})) {
                            app->cccam.cfg.applyGlobalProxy();
                            testing = true; testDone = 0; testTotal = (int)srvList.size();
                            auto appW = app;
                            std::thread([appW](){
                                auto& list = appW->cccam.cfg.servers;
                                int total = (int)list.size();
                                const int maxConc = 32;
                                std::atomic<int> idx{0};
                                auto worker = [&]() {
                                    while (true) {
                                        int i = idx.fetch_add(1);
                                        if (i >= total) break;
                                        auto& s = list[i];
                                        if (!s.valid()) {
                                            s.ping_status = -1;
                                            s.test_detail = "INVALID";
                                        } else {
                                            int cards = 0;
                                            int r = cccam::cccamFullTest(s, cards, 5000);
                                            if (r == 0) {
                                                s.ping_status = 1;
                                                s.dead_since = 0;
                                                char buf[32]; snprintf(buf, sizeof(buf), "OK (%d cards)", cards);
                                                s.test_detail = buf;
                                            } else if (r == -1) {
                                                s.ping_status = -1;
                                                if (s.dead_since == 0) s.dead_since = time(nullptr);
                                                s.test_detail = "TCP FAIL";
                                            } else if (r == -2) {
                                                s.ping_status = -1;
                                                if (s.dead_since == 0) s.dead_since = time(nullptr);
                                                s.test_detail = "NO SEED";
                                            } else {
                                                s.ping_status = -1;
                                                if (s.dead_since == 0) s.dead_since = time(nullptr);
                                                s.test_detail = "AUTH FAIL";
                                            }
                                        }
                                        testDone++;
                                    }
                                };
                                int nT = std::min(maxConc, total);
                                std::vector<std::thread> pool;
                                for (int t = 0; t < nT; t++) pool.emplace_back(worker);
                                for (auto& t : pool) t.join();
                                testing = false;
                            }).detach();
                        }
                        ImGui::PopStyleColor();
                        if (anyBusy) ImGui::EndDisabled();
                    }
                    ImGui::SameLine(0,6);
                    // ── Discover Networks ──
                    if (anyBusy) ImGui::BeginDisabled();
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.15f,0.55f,0.15f,0.80f});
                    if (ImGui::Button("Discover Networks", {120*dpi,24*dpi})) {
                        app->cccam.cfg.applyGlobalProxy();
                        auto appW = app;
                        std::thread([appW](){
                            auto& list = appW->cccam.cfg.servers;
                            int total = (int)list.size();
                            appW->cccamLog.add("[Discover] Testing " + std::to_string(total) + " servers for networks...");
                            
                            for (int i = 0; i < total; i++) {
                                auto& s = list[i];
                                if (!s.valid()) continue;
                                
                                int cards = 0;
                                int r = cccam::cccamFullTest(s, cards, 7000); // longer timeout for discovery
                                
                                if (r == 0 && cards > 0) {
                                    // Try to extract provider info from cards (basic)
                                    std::string networks = "";
                                    if (cards > 0) {
                                        networks = std::to_string(cards) + " cards";
                                    }
                                    s.test_detail = networks;
                                    appW->cccamLog.add("[Discover] " + s.host + ":" + std::to_string(s.port) + " -> " + networks);
                                } else {
                                    const char* err = (r == -1) ? "TCP FAIL" : 
                                                      (r == -2) ? "NO SEED" : "AUTH FAIL";
                                    s.test_detail = err;
                                    appW->cccamLog.add("[Discover] " + s.host + ":" + std::to_string(s.port) + " -> " + std::string(err));
                                }
                            }
                            appW->cccamLog.add("[Discover] Network discovery complete");
                        }).detach();
                    }
                    ImGui::PopStyleColor();
                    if (anyBusy) ImGui::EndDisabled();
                    
                    ImGui::SameLine(0,6);
                    // ── Remove Invalid ──
                    {
                        int nInvalid = 0;
                        for (auto& s : srvList) if (s.ping_status == -1) nInvalid++;
                        ImGui::PushStyleColor(ImGuiCol_Button, {0.55f,0.15f,0.15f,0.80f});
                        char rmLbl[40]; snprintf(rmLbl, sizeof(rmLbl), "Remove Invalid (%d)##rminv", nInvalid);
                        if (ImGui::Button(rmLbl, {0,24*dpi})) {
                            srvList.erase(std::remove_if(srvList.begin(), srvList.end(),
                                [](const cccam::UpstreamServer& s){ return s.ping_status == -1; }),
                                srvList.end());
                        }
                        ImGui::PopStyleColor();
                    }

                    // Note: Auto-removal disabled - user controls manual removal via "Remove Invalid" button

                    // Server table
                    static int editIdx = -1;
                    int removeIdx = -1;
                    float listH = ImGui::GetContentRegionAvail().y - 4*dpi;
                    if (listH < 100*dpi) listH = 100*dpi;

                    if (ImGui::BeginTable("##srvtbl", 7,
                            ImGuiTableFlags_RowBg | ImGuiTableFlags_ScrollY |
                            ImGuiTableFlags_BordersInnerV | ImGuiTableFlags_SizingStretchProp |
                            ImGuiTableFlags_Resizable, {0, listH})) {
                        ImGui::TableSetupScrollFreeze(0, 1);
                        ImGui::TableSetupColumn("",        ImGuiTableColumnFlags_WidthFixed, 18*dpi);
                        ImGui::TableSetupColumn("En",      ImGuiTableColumnFlags_WidthFixed, 24*dpi);
                        ImGui::TableSetupColumn("Host:Port", ImGuiTableColumnFlags_WidthStretch, 2.5f);
                        ImGui::TableSetupColumn("User",    ImGuiTableColumnFlags_WidthStretch, 1.0f);
                        ImGui::TableSetupColumn("Ping ms", ImGuiTableColumnFlags_WidthFixed, 52*dpi);
                        ImGui::TableSetupColumn("Test Result", ImGuiTableColumnFlags_WidthStretch, 1.2f);
                        ImGui::TableSetupColumn("",        ImGuiTableColumnFlags_WidthFixed, 22*dpi);
                        ImGui::TableHeadersRow();

                        for (int si = 0; si < (int)srvList.size(); si++) {
                            auto& s = srvList[si];
                            ImGui::PushID(si);
                            ImGui::TableNextRow();

                            // Status circle
                            ImGui::TableSetColumnIndex(0);
                            {
                                ImVec4 col = {0.4f,0.4f,0.4f,1};
                                if (s.ping_status == 1)  col = {0.2f,0.9f,0.35f,1};
                                if (s.ping_status == -1) col = {0.9f,0.2f,0.2f,1};
                                ImVec2 p = ImGui::GetCursorScreenPos();
                                float r = 5*dpi;
                                ImGui::GetWindowDrawList()->AddCircleFilled(
                                    {p.x + r + 2, p.y + ImGui::GetTextLineHeight()*0.5f},
                                    r, ImGui::ColorConvertFloat4ToU32(col));
                                ImGui::Dummy({r*2+4, ImGui::GetTextLineHeight()});
                            }

                            // Enable
                            ImGui::TableSetColumnIndex(1);
                            ImGui::Checkbox("##en", &s.enabled);

                            // Host:Port (clickable to expand edit)
                            ImGui::TableSetColumnIndex(2);
                            {
                                char hp[192];
                                snprintf(hp, sizeof(hp), "%s:%d##sel%d", s.host.c_str(), s.port, si);
                                if (!s.enabled) ImGui::PushStyleColor(ImGuiCol_Text, {0.5f,0.5f,0.5f,1});
                                bool open = (editIdx == si);
                                if (ImGui::Selectable(hp, open, ImGuiSelectableFlags_SpanAllColumns))
                                    editIdx = open ? -1 : si;
                                if (!s.enabled) ImGui::PopStyleColor();
                            }

                            // User
                            ImGui::TableSetColumnIndex(3);
                            ImGui::TextDisabled("%s", s.user.c_str());

                            // Ping ms
                            ImGui::TableSetColumnIndex(4);
                            if (s.ping_ms >= 0)
                                ImGui::Text("%dms", s.ping_ms);

                            // Test result
                            ImGui::TableSetColumnIndex(5);
                            if (!s.test_detail.empty()) {
                                bool ok = (s.ping_status == 1);
                                ImVec4 tc = ok ? ImVec4{0.25f,0.90f,0.35f,1} : ImVec4{0.95f,0.38f,0.38f,1};
                                ImGui::TextColored(tc, "%s", s.test_detail.c_str());
                            }

                            // Remove button
                            ImGui::TableSetColumnIndex(6);
                            ImGui::PushStyleColor(ImGuiCol_Button, {0.6f,0.15f,0.15f,0.6f});
                            if (ImGui::SmallButton("X")) removeIdx = si;
                            ImGui::PopStyleColor();

                            ImGui::PopID();

                            // Expanded edit row
                            if (editIdx == si) {
                                ImGui::TableNextRow();
                                ImGui::TableSetColumnIndex(0);
                                ImGui::TableSetBgColor(ImGuiTableBgTarget_RowBg0, IM_COL32(20,25,40,200));
                                // Span all columns via column 0
                                static char en[64]={}, eh[128]={}, eu[64]={}, ep[64]={};
                                static char ph[128]={}, pu[64]={}, pp[64]={};
                                static int eport=0, pport=0;
                                static int lastEditIdx = -1;
                                if (lastEditIdx != si) {
                                    lastEditIdx = si;
                                    snprintf(en, sizeof(en), "%s", s.name.c_str());
                                    snprintf(eh, sizeof(eh), "%s", s.host.c_str());
                                    snprintf(eu, sizeof(eu), "%s", s.user.c_str());
                                    snprintf(ep, sizeof(ep), "%s", s.pass.c_str());
                                    snprintf(ph, sizeof(ph), "%s", s.proxy_host.c_str());
                                    snprintf(pu, sizeof(pu), "%s", s.proxy_user.c_str());
                                    snprintf(pp, sizeof(pp), "%s", s.proxy_pass.c_str());
                                    eport = s.port; pport = s.proxy_port;
                                }
                                ImGui::Dummy({0,2*dpi});
                                ImGui::TableSetColumnIndex(1);
                                ImGui::SetNextItemWidth(100*dpi);
                                if (ImGui::InputText("Name##se", en, sizeof(en))) s.name = en;
                                ImGui::TableSetColumnIndex(2);
                                ImGui::SetNextItemWidth(140*dpi);
                                if (ImGui::InputText("Host##se", eh, sizeof(eh))) s.host = eh;
                                ImGui::SameLine(0,4);
                                ImGui::SetNextItemWidth(60*dpi);
                                if (ImGui::InputInt("Port##se", &eport, 0, 0)) {
                                    if (eport > 0 && eport < 65536) s.port = eport;
                                }
                                ImGui::TableSetColumnIndex(3);
                                ImGui::SetNextItemWidth(90*dpi);
                                if (ImGui::InputText("User##se", eu, sizeof(eu))) s.user = eu;
                                ImGui::SameLine(0,4);
                                ImGui::SetNextItemWidth(90*dpi);
                                if (ImGui::InputText("Pass##se", ep, sizeof(ep))) s.pass = ep;

                                // Proxy row
                                ImGui::TableNextRow();
                                ImGui::TableSetBgColor(ImGuiTableBgTarget_RowBg0, IM_COL32(20,25,40,200));
                                ImGui::TableSetColumnIndex(1);
                                ImGui::TextDisabled("Proxy:");
                                ImGui::TableSetColumnIndex(2);
                                ImGui::SetNextItemWidth(120*dpi);
                                if (ImGui::InputText("PxHost##se", ph, sizeof(ph))) s.proxy_host = ph;
                                ImGui::SameLine(0,4);
                                ImGui::SetNextItemWidth(50*dpi);
                                if (ImGui::InputInt("PxPort##se", &pport, 0, 0)) {
                                    if (pport >= 0 && pport < 65536) s.proxy_port = pport;
                                }
                                ImGui::TableSetColumnIndex(3);
                                ImGui::SetNextItemWidth(80*dpi);
                                if (ImGui::InputText("PxUser##se", pu, sizeof(pu))) s.proxy_user = pu;
                                ImGui::SameLine(0,4);
                                ImGui::SetNextItemWidth(80*dpi);
                                if (ImGui::InputText("PxPass##se", pp, sizeof(pp))) s.proxy_pass = pp;
                            }
                        }
                        ImGui::EndTable();
                    }
                    if (removeIdx >= 0 && removeIdx < (int)srvList.size()) {
                        srvList.erase(srvList.begin() + removeIdx);
                        if (editIdx == removeIdx) editIdx = -1;
                        else if (editIdx > removeIdx) editIdx--;
                    }
                }

                // ═══════════════════════════════════════════════════════════
                // TAB 2: SMART IMPORT (parse + auto-test + auto-add working)
                // ═══════════════════════════════════════════════════════════
                if (ccTab == 2) {
                    static char clBuf[65536] = {};
                    static std::vector<cccam::UpstreamServer> candidates;
                    static std::atomic<bool> impTesting{false};
                    static std::atomic<int>  impTestDone{0};
                    static std::atomic<int>  impTestTotal{0};
                    static std::atomic<int>  impAutoAdded{0};
                    static bool showPaste = true;

                    // ── Helper: start parallel test + auto-add working ──
                    auto startSmartTest = [&]() {
                        if (candidates.empty() || impTesting.load()) return;
                        impTesting = true;
                        impTestDone = 0;
                        impTestTotal = (int)candidates.size();
                        impAutoAdded = 0;
                        // Capture pointers to static/shared data
                        auto* pCand = &candidates;
                        auto* pSrv  = &srvList;
                        auto  a     = app;
                        std::thread([pCand, pSrv, a]() {
                            auto& cands = *pCand;
                            std::atomic<int> idx{0};
                            int total = (int)cands.size();
                            int nThreads = std::min(32, total);
                            std::vector<std::thread> pool;
                            for (int t = 0; t < nThreads; t++) {
                                pool.emplace_back([&]() {
                                    while (true) {
                                        int i = idx.fetch_add(1);
                                        if (i >= total) break;
                                        auto& c = cands[i];
                                        int cards = 0;
                                        int res = cccam::cccamFullTest(c, cards, 8000);
                                        if (res == 0) {
                                            c.ping_status = 1;
                                            c.test_detail = "OK (" + std::to_string(cards) + " cards)";
                                            // Auto-add to server list (dedup)
                                            bool dup = false;
                                            for (auto& s : *pSrv)
                                                if (s.host == c.host && s.port == c.port && s.user == c.user) { dup = true; break; }
                                            if (!dup) {
                                                pSrv->push_back(c);
                                                impAutoAdded++;
                                            }
                                        } else {
                                            c.ping_status = -1;
                                            if (res == -1) c.test_detail = "TCP FAIL";
                                            else if (res == -2) c.test_detail = "NO SEED";
                                            else c.test_detail = "AUTH FAIL";
                                        }
                                        impTestDone++;
                                    }
                                });
                            }
                            for (auto& t : pool) if (t.joinable()) t.join();
                            // Auto-save config with newly added servers
                            int added = impAutoAdded.load();
                            if (added > 0) {
                                try {
                                    cccam::CccamConfig ccfg;
                                    ccfg.port = a->cccam.cfg.port; ccfg.user = a->cccam.cfg.user;
                                    ccfg.pass = a->cccam.cfg.pass; ccfg.log_ecm = a->cccam.cfg.log_ecm;
                                    ccfg.global_proxy_host = a->cccam.cfg.global_proxy_host;
                                    ccfg.global_proxy_port = a->cccam.cfg.global_proxy_port;
                                    ccfg.global_proxy_user = a->cccam.cfg.global_proxy_user;
                                    ccfg.global_proxy_pass = a->cccam.cfg.global_proxy_pass;
                                    ccfg.servers = *pSrv; ccfg.save();
                                } catch (...) {}
                                try { a->cccamLog.add("[Import] Auto-added " + std::to_string(added) + " working servers"); } catch (...) {}
                            }
                            impTesting = false;
                        }).detach();
                    };

                    if (showPaste && candidates.empty()) {
                        // ── Paste area ──
                        ImGui::TextColored({0.6f,0.85f,1.0f,1}, "Smart Import");
                        ImGui::SameLine(0,6);
                        ImGui::TextDisabled("Paste any text — C-lines, Oscam readers, HOST/PORT/USER/PASS, mixed");
                        ImGui::Spacing();
                        float mlH = ImGui::GetContentRegionAvail().y - 44*dpi;
                        if (mlH < 80*dpi) mlH = 80*dpi;
                        ImGui::InputTextMultiline("##smartpaste", clBuf, sizeof(clBuf), {-1, mlH});

                        ImGui::PushStyleColor(ImGuiCol_Button, {0.15f,0.55f,0.75f,0.90f});
                        if (ImGui::Button("Smart Parse & Test", {170*dpi, 28*dpi})) {
                            std::string gpH = app->cccam.cfg.global_proxy_host;
                            int gpP = app->cccam.cfg.global_proxy_port;
                            std::string gpU = app->cccam.cfg.global_proxy_user;
                            std::string gpPw = app->cccam.cfg.global_proxy_pass;
                            candidates = cccam::parseSmartText(clBuf, gpH, gpP, gpU, gpPw);
                            if (candidates.empty()) {
                                app->log.add("[Import] No servers found in pasted text");
                            } else {
                                app->log.add("[Import] Parsed " + std::to_string(candidates.size()) +
                                             " servers, testing...");
                                showPaste = false;
                                startSmartTest();  // immediately start testing
                            }
                        }
                        ImGui::PopStyleColor();
                        ImGui::SameLine(0,8);
                        ImGui::TextDisabled("(%d servers loaded)", (int)srvList.size());

                    } else {
                        // ── Results view (live updating during test) ──
                        int nOk = 0, nFail = 0, nUntested = 0;
                        for (auto& c : candidates) {
                            if (c.ping_status == 1) nOk++;
                            else if (c.ping_status == -1) nFail++;
                            else nUntested++;
                        }

                        // ── Status bar ──
                        bool testing = impTesting.load();
                        if (testing) {
                            int done = impTestDone.load(), total = impTestTotal.load();
                            ImGui::TextColored({0.5f,0.8f,1.0f,1}, "Testing %d/%d ...", done, total);
                            ImGui::SameLine(0,8);
                            ImGui::ProgressBar(total > 0 ? (float)done / total : 0, {140*dpi, 16*dpi});
                        } else {
                            ImGui::TextColored({0.6f,0.85f,1.0f,1}, "Done! %d servers", (int)candidates.size());
                        }
                        ImGui::SameLine(0,10);
                        if (nOk > 0) {
                            ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d OK (added)", nOk);
                            ImGui::SameLine(0,6);
                        }
                        if (nFail > 0) {
                            ImGui::TextColored({0.9f,0.3f,0.3f,1}, "%d fail", nFail);
                            ImGui::SameLine(0,6);
                        }
                        if (nUntested > 0 && testing) {
                            ImGui::TextDisabled("%d pending", nUntested);
                            ImGui::SameLine(0,6);
                        }

                        // ── Buttons row ──
                        if (!testing) {
                            // Re-test button (in case user wants to retry)
                            ImGui::PushStyleColor(ImGuiCol_Button, {0.15f,0.45f,0.70f,0.90f});
                            if (ImGui::Button("Re-Test All", {100*dpi, 26*dpi})) {
                                for (auto& c : candidates) { c.ping_status = 0; c.test_detail.clear(); }
                                startSmartTest();
                            }
                            ImGui::PopStyleColor();
                            ImGui::SameLine(0,6);
                            if (ImGui::SmallButton("Clear & Paste New")) {
                                candidates.clear();
                                showPaste = true;
                                clBuf[0] = 0;
                            }
                        }

                        ImGui::Separator();

                        // ── Candidates table — OK on top, then pending, then fail ──
                        float tblH = ImGui::GetContentRegionAvail().y;
                        if (tblH < 40*dpi) tblH = 40*dpi;
                        if (ImGui::BeginTable("##impTbl", 6,
                            ImGuiTableFlags_Borders | ImGuiTableFlags_RowBg | ImGuiTableFlags_ScrollY |
                            ImGuiTableFlags_Resizable | ImGuiTableFlags_SizingStretchProp,
                            {0, tblH}))
                        {
                            ImGui::TableSetupScrollFreeze(0, 1);
                            ImGui::TableSetupColumn("##st", ImGuiTableColumnFlags_WidthFixed, 20*dpi);
                            ImGui::TableSetupColumn("Host:Port", ImGuiTableColumnFlags_WidthStretch);
                            ImGui::TableSetupColumn("User", ImGuiTableColumnFlags_WidthFixed, 110*dpi);
                            ImGui::TableSetupColumn("Pass", ImGuiTableColumnFlags_WidthFixed, 110*dpi);
                            ImGui::TableSetupColumn("Result", ImGuiTableColumnFlags_WidthFixed, 110*dpi);
                            ImGui::TableSetupColumn("##x", ImGuiTableColumnFlags_WidthFixed, 20*dpi);
                            ImGui::TableHeadersRow();

                            // Build sorted display order: OK first, then pending, then fail
                            static std::vector<int> dispOrder;
                            dispOrder.clear();
                            for (int i = 0; i < (int)candidates.size(); i++)
                                if (candidates[i].ping_status == 1) dispOrder.push_back(i);
                            for (int i = 0; i < (int)candidates.size(); i++)
                                if (candidates[i].ping_status == 0) dispOrder.push_back(i);
                            for (int i = 0; i < (int)candidates.size(); i++)
                                if (candidates[i].ping_status == -1) dispOrder.push_back(i);

                            int removeIdx = -1;
                            for (int di : dispOrder) {
                                auto& c = candidates[di];
                                ImGui::TableNextRow();
                                ImGui::PushID(di + 90000);
                                // Status
                                ImGui::TableSetColumnIndex(0);
                                if (c.ping_status == 1)
                                    ImGui::TextColored({0.3f,0.9f,0.4f,1}, "+");
                                else if (c.ping_status == -1)
                                    ImGui::TextColored({0.9f,0.3f,0.3f,1}, "x");
                                else
                                    ImGui::TextDisabled("...");
                                // Host:Port
                                ImGui::TableSetColumnIndex(1);
                                ImGui::Text("%s:%d", c.host.c_str(), c.port);
                                // User
                                ImGui::TableSetColumnIndex(2);
                                ImGui::TextDisabled("%s", c.user.c_str());
                                // Pass
                                ImGui::TableSetColumnIndex(3);
                                ImGui::TextDisabled("%s", c.pass.c_str());
                                // Result
                                ImGui::TableSetColumnIndex(4);
                                if (c.ping_status == 1)
                                    ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%s", c.test_detail.c_str());
                                else if (c.ping_status == -1)
                                    ImGui::TextColored({0.9f,0.3f,0.3f,1}, "%s", c.test_detail.c_str());
                                else
                                    ImGui::TextDisabled("testing...");
                                // Remove
                                ImGui::TableSetColumnIndex(5);
                                if (!testing && ImGui::SmallButton("x")) removeIdx = di;
                                ImGui::PopID();
                            }
                            ImGui::EndTable();
                            if (removeIdx >= 0 && removeIdx < (int)candidates.size() && !testing)
                                candidates.erase(candidates.begin() + removeIdx);
                        }
                    }
                }

                // ═══════════════════════════════════════════════════════════
                // TAB 3: AI ENGINE (Prediction + Ollama + CAIDs + AI Servers)
                // ═══════════════════════════════════════════════════════════
                if (ccTab == 3) {
                    auto& ai = app->cccam.aiPredictor;
                    auto aiStats = ai.getStats();
                    bool aiOn = app->cccam.aiEnabled;
                    
                    // ── Controls row ──
                    if (aiOn)
                        ImGui::TextColored({0.3f,0.9f,0.4f,1}, "AI Active");
                    else
                        ImGui::TextColored({0.6f,0.6f,0.6f,1}, "AI Disabled");
                    ImGui::SameLine(0,8);
                    if (aiStats.ollama_ok)
                        ImGui::TextColored({0.5f,0.8f,1.0f,1}, "Ollama OK (%s)", ai.model.c_str());
                    else
                        ImGui::TextColored({0.9f,0.5f,0.2f,1}, "Ollama Offline");
                    ImGui::SameLine(200*dpi);
                    if (ImGui::Checkbox("Enable##ai3", &aiOn))
                        app->cccam.aiEnabled = aiOn;
                    ImGui::SameLine(0,6);
                    bool learning = ai.learning.load();
                    if (ImGui::Checkbox("Learn##ai3", &learning))
                        ai.learning = learning;
                    ImGui::SameLine(0,6);
                    if (ImGui::SmallButton("Check Ollama##ai3"))
                        std::thread([&ai]() { ai.checkOllama(); }).detach();
                    ImGui::Separator();

                    // ── AI Sub-tabs ──
                    static int aiSub = 0;
                    if (ImGui::BeginTabBar("##aiSubTabs")) {
                        // ── Stats ──
                        if (ImGui::BeginTabItem("Stats##ai")) {
                            aiSub = 0;
                            ImGui::TextColored({0.6f,0.85f,1.0f,1}, "Pattern Learning");
                            ImGui::TextDisabled("Samples:");     ImGui::SameLine(100*dpi); ImGui::Text("%d", aiStats.total_samples);
                            ImGui::TextDisabled("Patterns:");    ImGui::SameLine(100*dpi); ImGui::Text("%d", aiStats.unique_patterns);
                            ImGui::TextDisabled("Servers:");     ImGui::SameLine(100*dpi); ImGui::Text("%d", aiStats.servers_tracked);
                            ImGui::TextDisabled("CAIDs:");       ImGui::SameLine(100*dpi); ImGui::Text("%d", aiStats.caids_seen);
                            ImGui::Spacing();
                            ImGui::TextColored({0.6f,0.85f,1.0f,1}, "CW Prediction");
                            ImGui::TextDisabled("Predictions:"); ImGui::SameLine(100*dpi); ImGui::Text("%d", aiStats.predictions);
                            ImGui::TextDisabled("Correct:");     ImGui::SameLine(100*dpi); ImGui::Text("%d", aiStats.correct);
                            ImGui::TextDisabled("AI Pred:");     ImGui::SameLine(100*dpi); ImGui::Text("%d", aiStats.ai_predictions);
                            ImGui::TextDisabled("Accuracy:");    ImGui::SameLine(100*dpi);
                            if (aiStats.predictions > 0)
                                ImGui::TextColored(aiStats.accuracy > 80 ? ImVec4{0.3f,0.9f,0.4f,1} :
                                                   aiStats.accuracy > 50 ? ImVec4{0.9f,0.8f,0.2f,1} :
                                                                           ImVec4{0.9f,0.3f,0.3f,1},
                                                   "%.1f%%", aiStats.accuracy);
                            else ImGui::TextDisabled("--");
                            ImGui::Spacing();
                            ImGui::TextColored({0.6f,0.85f,1.0f,1}, "CW Cache (10s TTL)");
                            ImGui::TextDisabled("Hits:");    ImGui::SameLine(100*dpi);
                            ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d", aiStats.cache_hits);
                            ImGui::TextDisabled("Misses:");  ImGui::SameLine(100*dpi); ImGui::Text("%d", aiStats.cache_misses);
                            ImGui::TextDisabled("Entries:"); ImGui::SameLine(100*dpi); ImGui::Text("%d live", aiStats.cache_size);
                            ImGui::TextDisabled("Srv Cache:"); ImGui::SameLine(100*dpi);
                            ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d hits", app->cccam.ecmCacheHits.load());
                            int totalReq = aiStats.cache_hits + aiStats.cache_misses;
                            float hitRate = totalReq > 0 ? (float)aiStats.cache_hits / totalReq : 0;
                            ImGui::TextDisabled("Hit Rate:"); ImGui::SameLine(100*dpi);
                            ImGui::ProgressBar(hitRate, {120*dpi, 14*dpi});
                            ImGui::Spacing(); ImGui::Separator();
                            if (ImGui::Button("Save Model##ai3", {100*dpi, 24*dpi})) { ai.save(); app->log.add("[AI] Model saved"); }
                            ImGui::SameLine(0,4);
                            if (ImGui::Button("Load Model##ai3", {100*dpi, 24*dpi})) { ai.load(); app->log.add("[AI] Model loaded"); }
                            ImGui::SameLine(0,4);
                            if (ImGui::Button("Clear Stats##ai3", {100*dpi, 24*dpi})) {
                                ai.predictions_made = 0; ai.predictions_correct = 0; ai.ai_predictions = 0;
                                ai.cache_hits = 0; ai.cache_misses = 0; app->cccam.ecmCacheHits = 0;
                            }
                            ImGui::EndTabItem();
                        }
                        // ── CAIDs ──
                        if (ImGui::BeginTabItem("CAIDs##ai")) {
                            aiSub = 1;
                            auto caids = ai.getCaidList();
                            if (ImGui::BeginTable("##caidTbl3", 5, ImGuiTableFlags_Borders | ImGuiTableFlags_RowBg | ImGuiTableFlags_ScrollY)) {
                                ImGui::TableSetupScrollFreeze(0, 1);
                                ImGui::TableSetupColumn("CAID", ImGuiTableColumnFlags_WidthFixed, 50*dpi);
                                ImGui::TableSetupColumn("Name", ImGuiTableColumnFlags_WidthStretch);
                                ImGui::TableSetupColumn("ECMs", ImGuiTableColumnFlags_WidthFixed, 50*dpi);
                                ImGui::TableSetupColumn("OK", ImGuiTableColumnFlags_WidthFixed, 40*dpi);
                                ImGui::TableSetupColumn("Rate", ImGuiTableColumnFlags_WidthFixed, 50*dpi);
                                ImGui::TableHeadersRow();
                                for (auto& ci : caids) {
                                    ImGui::TableNextRow();
                                    ImGui::TableSetColumnIndex(0); ImGui::Text("%04X", ci.caid);
                                    ImGui::TableSetColumnIndex(1); ImGui::TextDisabled("%s", ci.name.c_str());
                                    ImGui::TableSetColumnIndex(2); ImGui::Text("%d", ci.ecm_count);
                                    ImGui::TableSetColumnIndex(3); ImGui::Text("%d", ci.success_count);
                                    ImGui::TableSetColumnIndex(4);
                                    float rate = ci.ecm_count > 0 ? (float)ci.success_count / ci.ecm_count * 100 : 0;
                                    ImGui::TextColored(rate > 80 ? ImVec4{0.3f,0.9f,0.4f,1} :
                                                       rate > 50 ? ImVec4{0.9f,0.8f,0.2f,1} :
                                                                   ImVec4{0.9f,0.3f,0.3f,1}, "%.0f%%", rate);
                                }
                                ImGui::EndTable();
                            }
                            if (caids.empty()) ImGui::TextDisabled("No CAIDs detected yet. Watch encrypted channels to learn.");
                            ImGui::EndTabItem();
                        }
                        // ── AI Report ──
                        if (ImGui::BeginTabItem("AI Report##ai")) {
                            aiSub = 2;
                            std::string report = ai.getOllamaReport();
                            time_t repTime = ai.getOllamaReportTime();
                            if (!report.empty()) {
                                char tBuf[64] = {};
                                struct tm* tm = localtime(&repTime);
                                if (tm) strftime(tBuf, sizeof(tBuf), "%H:%M:%S", tm);
                                ImGui::TextColored({0.6f,0.85f,1.0f,1}, "Last analysis: %s", tBuf);
                                ImGui::Separator();
                                if (ImGui::BeginChild("##aiRep3", {0, ImGui::GetContentRegionAvail().y - 30*dpi}, true)) {
                                    std::istringstream ss(report);
                                    std::string line;
                                    while (std::getline(ss, line)) {
                                        if (line.size() > 4 && line.substr(0,5) == "RANK:")
                                            ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%s", line.c_str());
                                        else if (line.size() > 5 && line.substr(0,6) == "ISSUE:")
                                            ImGui::TextColored({0.9f,0.6f,0.2f,1}, "%s", line.c_str());
                                        else if (line.size() > 4 && line.substr(0,5) == "BEST:")
                                            ImGui::TextColored({0.5f,0.8f,1.0f,1}, "%s", line.c_str());
                                        else if (line.size() > 7 && line.substr(0,8) == "SUMMARY:")
                                            ImGui::TextColored({1.0f,0.9f,0.5f,1}, "%s", line.c_str());
                                        else if (!line.empty())
                                            ImGui::TextDisabled("%s", line.c_str());
                                    }
                                }
                                ImGui::EndChild();
                            } else {
                                if (aiStats.ollama_ok) {
                                    ImGui::TextDisabled("Analysis pending... (runs every 60s)");
                                } else {
                                    ImGui::TextColored({0.9f,0.3f,0.3f,1}, "Ollama not available");
                                    ImGui::TextDisabled("Start Ollama: ollama serve");
                                    ImGui::TextDisabled("Model: %s", ai.model.c_str());
                                }
                            }
                            ImGui::Separator();
                            if (ImGui::Button("Analyze Now##ai3", {120*dpi, 24*dpi})) {
                                ai.triggerAnalysis(); app->log.add("[AI] Analysis triggered");
                            }
                            ImGui::SameLine(0,4);
                            if (ImGui::Button("Check Ollama##rep3", {120*dpi, 24*dpi}))
                                std::thread([&ai]() { ai.checkOllama(); }).detach();
                            ImGui::EndTabItem();
                        }
                        // ── AI Servers ──
                        if (ImGui::BeginTabItem("AI Servers##ai")) {
                            aiSub = 3;
                            auto servers = ai.getServerList();
                            if (ImGui::BeginTable("##aiSrvTbl3", 5, ImGuiTableFlags_Borders | ImGuiTableFlags_RowBg | ImGuiTableFlags_ScrollY)) {
                                ImGui::TableSetupScrollFreeze(0, 1);
                                ImGui::TableSetupColumn("Server", ImGuiTableColumnFlags_WidthStretch);
                                ImGui::TableSetupColumn("Reqs", ImGuiTableColumnFlags_WidthFixed, 45*dpi);
                                ImGui::TableSetupColumn("OK", ImGuiTableColumnFlags_WidthFixed, 40*dpi);
                                ImGui::TableSetupColumn("Rate", ImGuiTableColumnFlags_WidthFixed, 50*dpi);
                                ImGui::TableSetupColumn("Lat", ImGuiTableColumnFlags_WidthFixed, 50*dpi);
                                ImGui::TableHeadersRow();
                                for (auto& [name, sq] : servers) {
                                    ImGui::TableNextRow();
                                    ImGui::TableSetColumnIndex(0); ImGui::Text("%s", name.c_str());
                                    ImGui::TableSetColumnIndex(1); ImGui::Text("%d", sq.total_requests);
                                    ImGui::TableSetColumnIndex(2); ImGui::Text("%d", sq.successful);
                                    ImGui::TableSetColumnIndex(3);
                                    ImGui::TextColored(sq.success_rate > 0.8f ? ImVec4{0.3f,0.9f,0.4f,1} :
                                                       sq.success_rate > 0.5f ? ImVec4{0.9f,0.8f,0.2f,1} :
                                                                                ImVec4{0.9f,0.3f,0.3f,1},
                                                       "%.0f%%", sq.success_rate * 100);
                                    ImGui::TableSetColumnIndex(4); ImGui::TextDisabled("%.0fms", sq.avg_latency_ms);
                                }
                                ImGui::EndTable();
                            }
                            if (servers.empty()) ImGui::TextDisabled("No AI server data yet.");
                            ImGui::EndTabItem();
                        }
                        // ── Channel Analytics ──
                        if (ImGui::BeginTabItem("Channels##ai")) {
                            aiSub = 4;
                            auto chList = ai.getChannelStatsList();
                            ImGui::TextDisabled("%d channels tracked", (int)chList.size());
                            float tblH = ImGui::GetContentRegionAvail().y;
                            if (tblH < 40*dpi) tblH = 40*dpi;
                            if (ImGui::BeginTable("##chTbl", 9,
                                ImGuiTableFlags_Borders | ImGuiTableFlags_RowBg | ImGuiTableFlags_ScrollY |
                                ImGuiTableFlags_Resizable | ImGuiTableFlags_SizingStretchProp,
                                {0, tblH}))
                            {
                                ImGui::TableSetupScrollFreeze(0, 1);
                                ImGui::TableSetupColumn("CAID",    ImGuiTableColumnFlags_WidthFixed, 42*dpi);
                                ImGui::TableSetupColumn("SID",     ImGuiTableColumnFlags_WidthFixed, 42*dpi);
                                ImGui::TableSetupColumn("ECMs",    ImGuiTableColumnFlags_WidthFixed, 42*dpi);
                                ImGui::TableSetupColumn("Rate",    ImGuiTableColumnFlags_WidthFixed, 38*dpi);
                                ImGui::TableSetupColumn("Lat",     ImGuiTableColumnFlags_WidthFixed, 38*dpi);
                                ImGui::TableSetupColumn("CW Rot",  ImGuiTableColumnFlags_WidthFixed, 52*dpi);
                                ImGui::TableSetupColumn("XOR",     ImGuiTableColumnFlags_WidthFixed, 32*dpi);
                                ImGui::TableSetupColumn("Parity",  ImGuiTableColumnFlags_WidthFixed, 48*dpi);
                                ImGui::TableSetupColumn("Best Srv", ImGuiTableColumnFlags_WidthStretch);
                                ImGui::TableHeadersRow();
                                for (auto& ch : chList) {
                                    ImGui::TableNextRow();
                                    ImGui::TableSetColumnIndex(0); ImGui::Text("%04X", ch.caid);
                                    ImGui::TableSetColumnIndex(1); ImGui::Text("%04X", ch.sid);
                                    ImGui::TableSetColumnIndex(2); ImGui::Text("%d", ch.ecm_total);
                                    ImGui::TableSetColumnIndex(3);
                                    ImGui::TextColored(ch.success_rate > 0.8f ? ImVec4{0.3f,0.9f,0.4f,1} :
                                                       ch.success_rate > 0.5f ? ImVec4{0.9f,0.8f,0.2f,1} :
                                                                                ImVec4{0.9f,0.3f,0.3f,1},
                                                       "%.0f%%", ch.success_rate * 100);
                                    if (ImGui::IsItemHovered()) {
                                        ImGui::BeginTooltip();
                                        ImGui::Text("ECM: %d total", ch.ecm_total);
                                        ImGui::Text("OK: %d  Fail: %d", ch.ecm_ok, ch.ecm_fail);
                                        ImGui::Text("Rate(raw): %.1f%%", ch.success_rate_raw * 100.0f);
                                        ImGui::Text("Rate(smooth): %.1f%%", ch.success_rate * 100.0f);
                                        ImGui::EndTooltip();
                                    }
                                    ImGui::TableSetColumnIndex(4);
                                    ImGui::TextDisabled("%.0f", ch.avg_latency_ms);
                                    if (ImGui::IsItemHovered()) {
                                        ImGui::BeginTooltip();
                                        ImGui::Text("Latency EMA: %.0fms", ch.avg_latency_ms);
                                        ImGui::Text("EMA alpha: %.2f", ai::ChannelStats::kLatencyEmaAlpha);
                                        ImGui::EndTooltip();
                                    }
                                    // CW Rotation period
                                    ImGui::TableSetColumnIndex(5);
                                    if (ch.rotation_samples >= 2) {
                                        ImGui::TextColored({0.5f,0.8f,1.0f,1}, "%.0fs", ch.avg_rotation_sec);
                                        if (ImGui::IsItemHovered()) {
                                            ImGui::BeginTooltip();
                                            ImGui::Text("CW rotation: %.1f-%.1fs avg", ch.min_rotation_sec, ch.max_rotation_sec);
                                            ImGui::Text("Changes: %d, Samples: %d", ch.cw_changes, ch.rotation_samples);
                                            ImGui::Text("Jitter(EMA abs dev): %.2fs", ch.rotation_jitter_sec);
                                            ImGui::EndTooltip();
                                        }
                                    } else if (ch.cw_changes > 0) {
                                        ImGui::TextDisabled("%d chg", ch.cw_changes);
                                    } else {
                                        ImGui::TextDisabled("--");
                                    }
                                    // XOR-delta stability
                                    ImGui::TableSetColumnIndex(6);
                                    if (ch.xor_stable)
                                        ImGui::TextColored({0.3f,0.9f,0.4f,1}, "OK");
                                    else if (ch.xor_pattern_repeats > 0)
                                        ImGui::TextDisabled("%d", ch.xor_pattern_repeats);
                                    else
                                        ImGui::TextDisabled("--");
                                    if (ImGui::IsItemHovered()) {
                                        ImGui::BeginTooltip();
                                        ImGui::Text("XOR repeats: %d", ch.xor_pattern_repeats);
                                        ImGui::Text("Stability: %.2f", ch.xor_stability);
                                        ImGui::Text("Stable threshold: %d", ai::ChannelStats::kXorStableRepeats);
                                        ImGui::EndTooltip();
                                    }
                                    // Parity (even/odd ECM ratio)
                                    ImGui::TableSetColumnIndex(7);
                                    if (ch.even_ecm_count > 0 || ch.odd_ecm_count > 0)
                                        ImGui::TextDisabled("%d/%d", ch.even_ecm_count, ch.odd_ecm_count);
                                    else
                                        ImGui::TextDisabled("--");
                                    // Best server
                                    ImGui::TableSetColumnIndex(8);
                                    if (!ch.best_server.empty())
                                        ImGui::TextDisabled("%s", ch.best_server.c_str());
                                    else
                                        ImGui::TextDisabled("--");
                                }
                                ImGui::EndTable();
                            }
                            if (chList.empty()) ImGui::TextDisabled("No channel data yet. Watch encrypted channels.");
                            ImGui::EndTabItem();
                        }
                        ImGui::EndTabBar();
                    }
                    (void)aiSub;
                }

                // ═══════════════════════════════════════════════════════════
                // TAB 4: LEARNER (ECM/CW ML Engine)
                // ═══════════════════════════════════════════════════════════
                if (ccTab == 4) {
                    auto stats = app->cccam.learner.getStats();
                    ImGui::TextColored({0.6f,0.85f,1.0f,1.0f}, "CW Learning Engine");
                    ImGui::Separator();
                    ImGui::Text("Total Samples:       %d", stats.total_samples);
                    ImGui::Text("Unique ECMs:         %d", stats.unique_ecms);
                    ImGui::Text("Unique CWs:          %d", stats.unique_cws);
                    ImGui::Text("Prediction Requests: %d", stats.predictions);
                    ImGui::Text("Predictions Used:    %d (Exact: %d, Pattern: %d)",
                        stats.pred_used, stats.pred_exact, stats.pred_pattern);
                    ImGui::Text("Prediction Verified: %d correct / %d wrong", stats.pred_correct, stats.pred_wrong);
                    ImGui::Text("Prediction Unverified: %d", stats.pred_unverified);
                    if (stats.pred_used > 0)
                        ImGui::Text("Prediction Accuracy: %.1f%%", stats.pred_accuracy);
                    ImGui::Text("Upstream Servers Seen: %d", stats.servers);

                    ImGui::Spacing();
                    int minVotes = app->cccam.learner.minPatternVotes();
                    ImGui::SetNextItemWidth(120*dpi);
                    if (ImGui::SliderInt("Min Pattern Votes", &minVotes, 1, 12))
                        app->cccam.learner.setMinPatternVotes(minVotes);
                    ImGui::SameLine(0,8);
                    ImGui::TextDisabled("(higher = safer predictions)");

                    ImGui::Spacing();
                    if (ImGui::Button("Save Model", {100*dpi, 24*dpi}))
                        app->cccam.learner.save();
                    ImGui::SameLine(0,8);
                    if (ImGui::Button("Load Model", {100*dpi, 24*dpi}))
                        app->cccam.learner.load();
                    ImGui::SameLine(0,8);
                    ImGui::TextDisabled("Training log: my4030_ecm_train.csv");

                    ImGui::Spacing();
                    ImGui::TextColored({0.6f,0.85f,1.0f,1.0f}, "Upstream Success (Last Seen)");
                    if (ImGui::BeginTable("##srvstats", 5,
                            ImGuiTableFlags_RowBg | ImGuiTableFlags_BordersInnerV | ImGuiTableFlags_SizingStretchProp,
                            {0, 110*dpi})) {
                        ImGui::TableSetupScrollFreeze(0,1);
                        ImGui::TableSetupColumn("Server", ImGuiTableColumnFlags_WidthStretch, 2.2f);
                        ImGui::TableSetupColumn("OK", ImGuiTableColumnFlags_WidthFixed, 36*dpi);
                        ImGui::TableSetupColumn("Fail", ImGuiTableColumnFlags_WidthFixed, 36*dpi);
                        ImGui::TableSetupColumn("Rate", ImGuiTableColumnFlags_WidthFixed, 50*dpi);
                        ImGui::TableSetupColumn("Last OK", ImGuiTableColumnFlags_WidthFixed, 70*dpi);
                        ImGui::TableHeadersRow();
                        auto srvStats = app->cccam.learner.getServerStats();
                        for (const auto& it : srvStats) {
                            const auto& name = it.first;
                            const auto& ss = it.second;
                            float rate = ss.total > 0 ? (float)ss.ok / ss.total * 100.0f : 0.0f;
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0);
                            ImGui::TextDisabled("%s", name.c_str());
                            ImGui::TableSetColumnIndex(1);
                            ImGui::Text("%d", ss.ok);
                            ImGui::TableSetColumnIndex(2);
                            ImGui::Text("%d", ss.fail);
                            ImGui::TableSetColumnIndex(3);
                            ImGui::Text("%.0f%%", rate);
                            ImGui::TableSetColumnIndex(4);
                            if (ss.last_ok > 0) {
                                std::tm* tm = std::localtime(&ss.last_ok);
                                char ts[16] = {};
                                if (tm) std::strftime(ts, sizeof(ts), "%H:%M:%S", tm);
                                ImGui::TextDisabled("%s", ts);
                            }
                        }
                        ImGui::EndTable();
                    }

                    ImGui::Spacing();
                    ImGui::TextColored({0.6f,0.85f,1.0f,1.0f}, "Recent ECM/CW Samples");
                    ImGui::Separator();

                    auto samples = app->cccam.learner.getRecentSamples(80);
                    float sampH = ImGui::GetContentRegionAvail().y - 4*dpi;
                    if (sampH < 60*dpi) sampH = 60*dpi;
                    if (ImGui::BeginTable("##ecmtbl", 6,
                            ImGuiTableFlags_RowBg | ImGuiTableFlags_ScrollY |
                            ImGuiTableFlags_BordersInnerV | ImGuiTableFlags_SizingStretchProp,
                            {0, sampH})) {
                        ImGui::TableSetupScrollFreeze(0,1);
                        ImGui::TableSetupColumn("CAID",   ImGuiTableColumnFlags_WidthFixed, 44*dpi);
                        ImGui::TableSetupColumn("SID",    ImGuiTableColumnFlags_WidthFixed, 44*dpi);
                        ImGui::TableSetupColumn("Server", ImGuiTableColumnFlags_WidthStretch, 1.5f);
                        ImGui::TableSetupColumn("ECM",    ImGuiTableColumnFlags_WidthStretch, 2.0f);
                        ImGui::TableSetupColumn("CW",     ImGuiTableColumnFlags_WidthStretch, 2.0f);
                        ImGui::TableSetupColumn("OK",     ImGuiTableColumnFlags_WidthFixed, 24*dpi);
                        ImGui::TableHeadersRow();

                        for (int i = (int)samples.size()-1; i >= 0; i--) {
                            auto& sm = samples[i];
                            ImGui::TableNextRow();
                            ImGui::TableSetColumnIndex(0);
                            ImGui::Text("%04X", sm.caid);
                            ImGui::TableSetColumnIndex(1);
                            ImGui::Text("%04X", sm.sid);
                            ImGui::TableSetColumnIndex(2);
                            ImGui::TextDisabled("%s", sm.server.c_str());
                            ImGui::TableSetColumnIndex(3);
                            if (!sm.ecm_hex.empty())
                                ImGui::TextDisabled("%.24s...", sm.ecm_hex.c_str());
                            ImGui::TableSetColumnIndex(4);
                            if (!sm.cw_hex.empty())
                                ImGui::TextDisabled("%.32s...", sm.cw_hex.c_str());
                            ImGui::TableSetColumnIndex(5);
                            if (sm.success)
                                ImGui::TextColored({0.3f,0.9f,0.4f,1}, "Y");
                            else
                                ImGui::TextColored({0.9f,0.3f,0.3f,1}, "N");
                        }
                        ImGui::EndTable();
                    }
                }

                // ═══════════════════════════════════════════════════════════
                // TAB 5: CONFIG
                // ═══════════════════════════════════════════════════════════
                if (ccTab == 5) {
                    bool cfgDis = srv;
                    if (cfgDis) ImGui::BeginDisabled();

                    static bool ccUiInit = false;
                    static char cu[64] = {}, cpw[64] = {};
                    if (!ccUiInit) {
                        ccUiInit = true;
                        snprintf(cu, sizeof(cu), "%s", app->cccam.cfg.user.c_str());
                        snprintf(cpw, sizeof(cpw), "%s", app->cccam.cfg.pass.c_str());
                    }

                    ImGui::TextColored({0.6f,0.85f,1.0f,1.0f}, "Local Server Settings");
                    ImGui::Separator();
                    int cp = app->cccam.cfg.port;
                    ImGui::SetNextItemWidth(100*dpi);
                    if (ImGui::InputInt("Listen Port", &cp, 0, 0)) {
                        if (cp > 0 && cp < 65536) app->cccam.cfg.port = cp;
                    }
                    ImGui::SetNextItemWidth(140*dpi);
                    if (ImGui::InputText("Username (STB login)", cu, sizeof(cu)))
                        app->cccam.cfg.user = cu;
                    ImGui::SetNextItemWidth(140*dpi);
                    if (ImGui::InputText("Password (STB login)", cpw, sizeof(cpw), ImGuiInputTextFlags_Password))
                        app->cccam.cfg.pass = cpw;

                    ImGui::Spacing();
                    bool logEcm = app->cccam.cfg.log_ecm;
                    if (ImGui::Checkbox("Log ECM/CW to CSV (training data)", &logEcm))
                        app->cccam.cfg.log_ecm = logEcm;

                    ImGui::Spacing();
                    ImGui::TextDisabled("ECMs are forwarded to ALL enabled upstream servers.");
                    ImGui::TextDisabled("First successful CW is relayed to the STB.");
                    ImGui::TextDisabled("Training data: my4030_ecm_log.csv");
                    ImGui::TextDisabled("Learned model: my4030_cwlearn.dat");

                    if (cfgDis) ImGui::EndDisabled();
                }

                ImGui::PopStyleVar(2);
            }
            ImGui::End();
        }

        // ════════════════════════════════════════════════════════════════
        // ── TAB 2: STB INFO (docked in main content area) ──
        // ════════════════════════════════════════════════════════════════
        if (app->mainTab == 2) {
            ImGui::SetNextWindowPos({OX+P, OY+P+CONN_H+P});
            ImGui::SetNextWindowSize({LW, CH_H});
            if (ImGui::Begin("##stbinfo_docked", nullptr,
                ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoMove | ImGuiWindowFlags_NoResize)) {
                auto si = app->snapStbInfo(); // SAFE copy
                auto row = [&](const char* k, const std::string& v) {
                    ImGui::TextDisabled("%s", k);
                    ImGui::SameLine(150*dpi);
                    ImGui::Text("%s", v.empty() ? "(unknown)" : v.c_str());
                };

                if (ImGui::BeginTabBar("##stbinfotabs")) {

                    // ── Tab 1: Device Info ──
                    if (ImGui::BeginTabItem("Device")) {
                        if (ImGui::BeginTable("##stbdev", 2, ImGuiTableFlags_SizingFixedFit)) {
                            auto kv = [&](const char* k, const std::string& v) {
                                ImGui::TableNextRow();
                                ImGui::TableSetColumnIndex(0); ImGui::TextDisabled("%s", k);
                                ImGui::TableSetColumnIndex(1); ImGui::Text("%s", v.empty() ? "(unknown)" : v.c_str());
                            };
                            kv("Model",       si.stb_model);
                            kv("SW Version",  si.sw_version);
                            kv("STB Time",    si.stb_time);
                            kv("TV Channels", std::to_string(si.channel_count));
                            kv("Radio",       std::to_string(si.radio_count));
                            if (si.has_login) {
                                kv("Platform ID", std::to_string(si.platform_id));
                                kv("4K Support",  si.is_4k ? "Yes" : "Unknown");
                                kv("SAT Enable",  std::to_string(si.sat_enable));
                                kv("Protocol",    si.uses_json ? "JSON" : "XML");
                            }
                            ImGui::EndTable();
                        }
                        ImGui::Separator();
                        float bw = (ImGui::GetContentRegionAvail().x - 8) / 2.0f;
                        if (ImGui::Button("Refresh Info", {bw, 26*dpi})) {
                            try { app->client.requestStbInfo(); } catch (...) {}
                            app->log.add("[4030] Info req");
                        }
                        ImGui::SameLine(0,8);
                        if (ImGui::Button("Restart STB", {bw, 26*dpi})) {
                            try { app->client.restartStb(); } catch (...) {}
                            app->log.add("[4030] Restart");
                        }
                        ImGui::EndTabItem();
                    }

                    // ── Tab 2: Command Audit Log ──
                    if (ImGui::BeginTabItem("Cmd Audit")) {
                        auto audit = app->client.getCmdAudit();
                        ImGui::TextDisabled("Last %d commands (newest first)", (int)audit.size());
                        ImGui::SameLine(0,8);
                        uint64_t idle = 0;
                        try { idle = app->client.millisSinceLastActivity(); } catch (...) {}
                        if (idle == UINT64_MAX)
                            ImGui::TextDisabled("Idle: --");
                        else
                            ImGui::TextDisabled("Idle: %llus", (unsigned long long)(idle/1000));
                        ImGui::Separator();
                        ImGui::BeginChild("##audit", {0,0}, false);
                        // Show newest first
                        for (int i = (int)audit.size()-1; i >= 0; --i) {
                            auto& e = audit[i];
                            ImVec4 col = e.tcp_ok
                                ? ImVec4(0.55f,0.95f,0.55f,1.0f)
                                : ImVec4(0.95f,0.42f,0.38f,1.0f);
                            ImGui::TextColored(col, "[%4d] cmd=%-5d %s  %ums",
                                (int)(audit.size()-i),
                                e.cmd_id,
                                e.tcp_ok ? "OK  " : "FAIL",
                                e.latency_ms);
                        }
                        ImGui::EndChild();
                        ImGui::EndTabItem();
                    }

                    // ── Tab 3: Firmware / CA Info ──
                    if (ImGui::BeginTabItem("Firmware")) {
                        ImGui::TextDisabled("Firmware: MS-4030 BlueMenu v1.11.20688");
                        ImGui::TextDisabled("Date:     2026-01-22");
                        ImGui::Separator();
                        ImGui::TextDisabled("Flash Memory Sections:");
                        ImGui::Text("  ARM Bootloader   0x0000092E  20 KB");
                        ImGui::Text("  Main FS          0x0004012E   4.4 MB");
                        ImGui::Text("  BMPS (splash)    0x0055B002   387 KB");
                        ImGui::Text("  STRG (strings)   0x0079B002   70 KB");
                        ImGui::Text("  Transponder DB   0x0081B002   14 KB");
                        ImGui::Text("  CA Table         0x00826A0E    2 KB");
                        ImGui::Separator();
                        ImGui::TextDisabled("CA Systems (from CA_TABLE section):");
                        ImGui::Text("  0x01-0x05        Tandberg/Ericsson CAS");
                        ImGui::Text("  0x15-0x16 (21-22) Tandberg CAS");
                        ImGui::Text("  0x17 (23)        GetTV (Middle East)");
                        ImGui::Text("  0xCE (206)       Tandberg CAS");
                        ImGui::Separator();
                        ImGui::TextDisabled("OSD String resources include: Teletext (ttx)");

                        if (ImGui::CollapsingHeader("Offline Container Analyzer (static scan)##fw")) {
                            static bool fwInit = false;
                            static char fwPath[512] = {};
                            static std::atomic<bool> fwRunning{false};
                            static bool fwHasReport = false;
                            static FwContainerReport_ fwRep;
                            static std::mutex fwMu;
                            if (!fwInit) {
                                fwInit = true;
                                std::string def = exeDir_() + "MAIN_PAYLOAD_4MB_0004012E.bin";
                                snprintf(fwPath, sizeof(fwPath), "%s", def.c_str());
                            }

                            ImGui::SetNextItemWidth(ImGui::GetContentRegionAvail().x - 90*dpi);
                            ImGui::InputText("##fwPath", fwPath, sizeof(fwPath));
                            ImGui::SameLine(0, 6);
                            if (ImGui::Button("Analyze##fw", {80*dpi, 0}) && !fwRunning.load()) {
                                fwRunning = true;
                                fwHasReport = false;
                                std::string pathCopy = fwPath;
                                std::thread([pathCopy]() {
                                    auto rep = analyzeFwContainer_(pathCopy.c_str());
                                    {
                                        std::lock_guard<std::mutex> g(fwMu);
                                        fwRep = rep;
                                        fwHasReport = true;
                                    }
                                    fwRunning = false;
                                }).detach();
                            }

                            if (fwRunning.load()) {
                                ImGui::TextDisabled("Analyzing...");
                            } else if (fwHasReport) {
                                FwContainerReport_ rep;
                                {
                                    std::lock_guard<std::mutex> g(fwMu);
                                    rep = fwRep;
                                }
                                if (!rep.ok) {
                                    ImGui::TextColored({0.95f,0.42f,0.38f,1.0f}, "Analyze failed: %s", rep.err.c_str());
                                } else {
                                    ImGui::Text("File: %s", rep.path.c_str());
                                    ImGui::TextDisabled("Size: %zu bytes", rep.size);
                                    ImGui::TextDisabled("Magic (BE): 0x%08X", rep.magic_be);
                                    ImGui::TextDisabled("Markers: AA41ADA3=%s  A3AD41AA=%s",
                                        rep.has_marker_aa41ada3 ? "yes" : "no",
                                        rep.has_marker_a3ad41aa ? "yes" : "no");
                                    ImGui::Separator();
                                    ImGui::TextDisabled("Entropy: global=%.4f  64KB min/max=%.4f/%.4f",
                                        rep.entropy_global, rep.entropy_min_64k, rep.entropy_max_64k);
                                    ImGui::TextDisabled("zlib headers: %d  inflate-probe OK: %d",
                                        rep.zlib_header_candidates, rep.zlib_inflate_ok);
                                    ImGui::TextDisabled("JFFS2 magic hits: %d  valid hdr_crc: %d",
                                        rep.jffs2_magic_hits, rep.jffs2_valid_hdr_crc);
                                    ImGui::TextDisabled("DER seq hits: %d  plausible TLV: %d",
                                        rep.der_seq_hits, rep.der_plausible);
                                }
                            } else {
                                ImGui::TextDisabled("Set a file path and click Analyze. This is a safe static scan.");
                            }
                        }
                        ImGui::EndTabItem();
                    }

                    ImGui::EndTabBar();
                }
            }
            ImGui::End();
        }


        // ════════════════════════════════════════════════════════════════
        // CCcam LOG VIEWER
        // ════════════════════════════════════════════════════════════════
        if (app->showCccamLog) {
            ImGui::SetNextWindowSize({700*dpi, 480*dpi}, ImGuiCond_FirstUseEver);
            if (ImGui::Begin("CCcam Log Viewer", &app->showCccamLog)) {
                ImGui::TextDisabled("Lines: %d  Size: %zu B",
                    app->cccamLog.count(), app->cccamLog.bytes());
                ImGui::SameLine(0,10);
                if (ImGui::SmallButton("Clear##clv")) app->cccamLog.clear();
                ImGui::SameLine(0,6);
                if (ImGui::SmallButton("Copy All##clv"))
                    ImGui::SetClipboardText(app->cccamLog.fullText().c_str());
                ImGui::SameLine(0,6);
                ImGui::Checkbox("Scroll##clv", &app->cccamLog.scroll);
                ImGui::Separator();
                ImGui::BeginChild("##clvt", {0,0}, false, ImGuiWindowFlags_HorizontalScrollbar);
                auto lvSnap = app->cccamLog.snap();
                for (auto& ln : lvSnap) {
                    bool isErr = ln.find("ail") != std::string::npos ||
                                 ln.find("rror") != std::string::npos;
                    if (isErr)
                        ImGui::TextColored({0.95f,0.42f,0.38f,1.0f}, "%s", ln.c_str());
                    else
                        ImGui::TextUnformatted(ln.c_str());
                }
                if (app->cccamLog.scroll && ImGui::GetScrollY() >= ImGui::GetScrollMaxY() - 10)
                    ImGui::SetScrollHereY(1.0f);
                ImGui::EndChild();
            }
            ImGui::End();
        }

        // ════════════════════════════════════════════════════════════════
        // STB KEYBOARD WINDOW
        // ════════════════════════════════════════════════════════════════
        if (app->showKeyboard) {
            static int  kbMode    = 0;    // 0=Upper 1=Lower 2=Nums 3=Sym
            static bool kbCapture = true;

            ImGui::SetNextWindowSize({500*dpi, 300*dpi}, ImGuiCond_FirstUseEver);
            if (ImGui::Begin("STB Keyboard##kbwin", &app->showKeyboard)) {
                bool kbCon     = connected;
                bool kbFocused = ImGui::IsWindowFocused(ImGuiFocusedFlags_RootAndChildWindows);
                float wcw      = ImGui::GetContentRegionAvail().x;

                // ── PC key capture (typed chars + nav/special keys) ──────
                if (kbFocused && kbCapture && kbCon) {
                    auto& pio = ImGui::GetIO();
                    for (ImWchar wc : pio.InputQueueCharacters) {
                        if (wc >= 32 && wc <= 126)
                            try { app->client.sendKeyboardCode((int)wc, true); } catch (...) {}
                    }
                    if (ImGui::IsKeyPressed(ImGuiKey_Enter, false) ||
                        ImGui::IsKeyPressed(ImGuiKey_KeypadEnter, false))
                        try { app->client.sendKeyboardEnter(); } catch (...) {}
                    if (ImGui::IsKeyPressed(ImGuiKey_Backspace, false))
                        try { app->client.sendKeyboardBackspace(); } catch (...) {}
                    if (ImGui::IsKeyPressed(ImGuiKey_Escape, false))
                        try { app->client.dismissInputMethod(); } catch (...) {}
                    if (ImGui::IsKeyPressed(ImGuiKey_UpArrow,    false)) try { app->client.sendNavUp();    } catch (...) {}
                    if (ImGui::IsKeyPressed(ImGuiKey_DownArrow,  false)) try { app->client.sendNavDown();  } catch (...) {}
                    if (ImGui::IsKeyPressed(ImGuiKey_LeftArrow,  false)) try { app->client.sendNavLeft();  } catch (...) {}
                    if (ImGui::IsKeyPressed(ImGuiKey_RightArrow, false)) try { app->client.sendNavRight(); } catch (...) {}
                }

                // ── Mode selector ─────────────────────────────────────────
                const char* modeNames[] = {"ABC", "abc", "123", "Sym"};
                for (int m = 0; m < 4; m++) {
                    if (m) ImGui::SameLine(0, 4);
                    bool active = (kbMode == m);
                    if (active) ImGui::PushStyleColor(ImGuiCol_Button, ImVec4{0.22f,0.52f,0.85f,0.90f});
                    if (ImGui::Button(modeNames[m], ImVec2{44*dpi, 22*dpi})) kbMode = m;
                    if (active) ImGui::PopStyleColor();
                }
                ImGui::SameLine(0, 14);
                ImGui::Checkbox("PC Capture##kbcap", &kbCapture);
                if (ImGui::IsItemHovered())
                    ImGui::SetTooltip(
                        "When checked, typing on your PC keyboard sends\n"
                        "characters directly to STB (window must be focused).\n"
                        "PC arrow keys also send Up/Down/Left/Right to STB.");
                ImGui::SameLine(0, 8);
                if (!kbCon)
                    ImGui::TextColored({0.88f,0.38f,0.38f,1.0f}, "Not connected");
                else if (kbFocused && kbCapture)
                    ImGui::TextColored({0.38f,0.88f,0.38f,1.0f}, "Capturing...");
                else
                    ImGui::TextDisabled("(click window to activate)");

                ImGui::Separator();
                ImGui::Spacing();

                // ── QWERTY / Num / Sym key rows ───────────────────────────
                static const char* kbRows[4][3] = {
                    { "QWERTYUIOP", "ASDFGHJKL",  "ZXCVBNM"    }, // Upper
                    { "qwertyuiop", "asdfghjkl",  "zxcvbnm"    }, // Lower
                    { "1234567890", "+-*/=.,;:!",  "@#$%^&()[]" }, // Nums
                    { "!@#$%^&*()", "-_=+[];:'\"", ",./|<>?~`"  }, // Sym
                };
                const float kbBH = 26*dpi;
                auto sendCh = [&](char ch) {
                    if (!kbCon) return;
                    try { app->client.sendKeyboardCode((int)(unsigned char)ch, true); } catch (...) {}
                };

                if (!kbCon) ImGui::BeginDisabled();
                for (int r = 0; r < 3; r++) {
                    const char* row = kbRows[kbMode][r];
                    int   nk  = (int)strlen(row);
                    float bw  = (wcw - (float)(nk - 1) * 3.0f) / (float)nk;
                    if (bw < 20*dpi) bw = 20*dpi;
                    // Stagger rows: row1 half-key right, row2 one-key right
                    float indent = (r == 1) ? bw * 0.4f : (r == 2) ? bw * 0.8f : 0.0f;
                    if (indent > 0.0f)
                        ImGui::SetCursorPosX(ImGui::GetCursorPosX() + indent);
                    for (int k = 0; k < nk; k++) {
                        if (k) ImGui::SameLine(0, 3);
                        char lbl[12]; snprintf(lbl, sizeof(lbl), "%c##kk%d%d", row[k], r, k);
                        if (ImGui::Button(lbl, ImVec2{bw, kbBH})) sendCh(row[k]);
                    }
                }
                // Action row: Space / Bksp / Enter
                ImGui::Spacing();
                float a4 = (wcw - 12.0f) / 4.0f;
                if (ImGui::Button("     Space     ##kbsp", ImVec2{a4*2+4, kbBH})) sendCh(' ');
                ImGui::SameLine(0, 4);
                if (ImGui::Button("Bksp##kbbs", ImVec2{a4, kbBH}))
                    try { app->client.sendKeyboardBackspace(); } catch (...) {}
                ImGui::SameLine(0, 4);
                if (ImGui::Button("Enter##kben", ImVec2{a4, kbBH}))
                    try { app->client.sendKeyboardEnter(); } catch (...) {}
                if (!kbCon) ImGui::EndDisabled();

                ImGui::Spacing();
                ImGui::Separator();

                // ── OSD navigation row (for arrow-navigating STB keyboard) ─
                ImGui::TextDisabled("OSD Nav:");
                ImGui::SameLine(0, 6);
                const float nBw = 36*dpi, nBh = 22*dpi;
                auto navK = [&](const char* lbl, auto fn) {
                    if (!kbCon) {
                        ImGui::BeginDisabled();
                        ImGui::Button(lbl, ImVec2{nBw, nBh});
                        ImGui::EndDisabled();
                    } else if (ImGui::Button(lbl, ImVec2{nBw, nBh})) {
                        try { fn(); } catch (...) {}
                    }
                };
                navK("Up##knu", [&]{ app->client.sendNavUp();    }); ImGui::SameLine(0,3);
                navK("Dn##knd", [&]{ app->client.sendNavDown();  }); ImGui::SameLine(0,3);
                navK("Lt##knl", [&]{ app->client.sendNavLeft();  }); ImGui::SameLine(0,3);
                navK("Rt##knr", [&]{ app->client.sendNavRight(); }); ImGui::SameLine(0,3);
                ImGui::PushStyleColor(ImGuiCol_Button, ImVec4{0.18f,0.42f,0.85f,0.80f});
                navK("OK##kno", [&]{ app->client.sendOk(); });
                ImGui::PopStyleColor();
                ImGui::SameLine(0,3);
                navK("Bk##knb", [&]{ app->client.sendBack(); });
                ImGui::SameLine(0,10);
                ImGui::TextDisabled("arrow-navigate STB OSD keyboard, OK to select");

                ImGui::Spacing();

                // ── Dismiss ───────────────────────────────────────────────
                if (!kbCon) ImGui::BeginDisabled();
                if (ImGui::Button("Dismiss / Close STB keyboard##kbdm",
                                  ImVec2{ImGui::GetContentRegionAvail().x, 22*dpi}))
                    try { app->client.dismissInputMethod(); } catch (...) {}
                if (!kbCon) ImGui::EndDisabled();

                ImGui::Spacing();

                // ── Help & Guide ──────────────────────────────────────────
                if (ImGui::CollapsingHeader("? Help & Guide##kbhelp")) {
                    ImGui::PushStyleColor(ImGuiCol_Text, ImVec4{1.0f,0.85f,0.25f,1.0f});
                    ImGui::TextUnformatted("Direct mode  (click key or type on PC):");
                    ImGui::PopStyleColor();
                    ImGui::BulletText("Chars sent via INPUT_METHOD_KEY_CODE_SET (cmd 1120).");
                    ImGui::BulletText("STB on-screen keyboard must be open/visible on TV.");
                    ImGui::BulletText("Fastest — no need to navigate letter by letter.");
                    ImGui::BulletText("PC typing works when this window is focused + PC Capture ON.");
                    ImGui::BulletText("ABC/abc/123/Sym buttons switch the key layout.");
                    ImGui::Spacing();
                    ImGui::PushStyleColor(ImGuiCol_Text, ImVec4{1.0f,0.85f,0.25f,1.0f});
                    ImGui::TextUnformatted("OSD Navigate  (arrows + OK):");
                    ImGui::PopStyleColor();
                    ImGui::BulletText("Moves STB cursor on its own on-screen keyboard.");
                    ImGui::BulletText("Press OK to select the highlighted letter.");
                    ImGui::BulletText("Works on ALL platforms — use when Direct mode fails.");
                    ImGui::BulletText("PC arrow keys also work here (window focused + capture on).");
                    ImGui::Spacing();
                    ImGui::PushStyleColor(ImGuiCol_Text, ImVec4{0.65f,0.65f,0.65f,1.0f});
                    ImGui::TextUnformatted("Remote key reference:");
                    ImGui::PopStyleColor();
                    ImGui::BulletText("Backspace -> remote key 10  (sendKeyboardBackspace)");
                    ImGui::BulletText("Enter     -> remote key 11  (key 5 on platforms 20/21/25)");
                    ImGui::BulletText("Dismiss   -> INPUT_METHOD_DISMISS (cmd 1121)");
                    ImGui::BulletText("Direct chars use force=true, bypassing platform restrictions.");
                }
            }
            ImGui::End();
        }

        // ── Credit ─────────────────────────────────────────────────────
        {
            const char* credit = "by bahmanymb@gmail.com";
            ImVec2 ts = ImGui::CalcTextSize(credit);
            ImVec2 ws = io.DisplaySize;
            ImGui::GetForegroundDrawList()->AddText(
                {ws.x - ts.x - 8, ws.y - ts.y - 4},
                IM_COL32(140, 140, 140, 180), credit);
        }

        pip_end_frame: // PiP mode jumps here to skip normal UI
        // ── Render ──────────────────────────────────────────────────────
        try {
            ImGui::Render();
            const float clr[4] = {0.067f, 0.067f, 0.106f, 1.0f};
            g_ctx->OMSetRenderTargets(1, &g_rtv, nullptr);
            g_ctx->ClearRenderTargetView(g_rtv, clr);
            ImGui_ImplDX11_RenderDrawData(ImGui::GetDrawData());
            g_chain->Present(1, 0);
        } catch (...) {
            CrashLog("Render exception");
            break;
        }
    }

    CrashLog("Main loop exiting");
    app.reset(); // destroy App before ImGui shutdown

    ImGui_ImplDX11_Shutdown();
    ImGui_ImplWin32_Shutdown();
    ImGui::DestroyContext();
    ShutD3D();
    DestroyWindow(hwnd);
    UnregisterClassW(wc.lpszClassName, wc.hInstance);
    CrashLog("=== GMScreen exited cleanly ===");
    return 0;
}
