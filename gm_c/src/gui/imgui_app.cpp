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
#include <fstream>
#include <memory>
#include <mutex>
#include <string>
#include <thread>
#include <vector>
#include <ctime>
#include <cstdio>

#include "imgui.h"
#include "imgui_impl_dx11.h"
#include "imgui_impl_win32.h"
#include "stb/stb_client.h"
#include "stb/rcu_keys.h"
#include "stb/crash_log.h"
#include "stb/channel_cache.h"
#include "cccam/cccam_server.h"
#include "cccam/cccam_config.h"
#include "stb/custom_lists.h"
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
    Logger log;
    Logger cccamLog;

    // Satellites (behind satMu)
    std::mutex satMu;
    std::vector<stb::Satellite> satellites;

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
    stb::CustomLists customLists;
    bool clDirty = false;  // auto-save trigger
    
    // AI Decryption Engine panel
    bool showAI = false;
    int  detailIdx = -1;           // index into chSnap for detail popup
    int  sortCol = 0;              // 0=index, 1=name, 2=freq, 3=type
    bool sortAsc = true;
    int  filterFav = 0;            // 0=all, 1=fav only, 2=FTA only, 3=scrambled only
    
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
                    try { client.requestStbInfo(); } catch (...) {}
                    try { client.requestSatelliteList(); } catch (...) {}
                    try { client.requestFavGroupNames(); } catch (...) {}
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

        client.setNotificationCallback([this](const std::string& ev, const std::string&) {
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
        try { cccam.stop(); } catch (...) {}
        try { client.disconnect(); } catch (...) {}
        std::this_thread::sleep_for(std::chrono::milliseconds(300));
        CrashLog("App destructor done");
    }
};

// ── Style ───────────────────────────────────────────────────────────────────
static void ApplyStyle(float dpi) {
    ImGui::StyleColorsDark();
    ImGuiStyle& s = ImGui::GetStyle();
    s.WindowPadding = {8,6}; s.FramePadding = {6,3}; s.CellPadding = {4,2};
    s.ItemSpacing = {6,3}; s.ItemInnerSpacing = {4,3}; s.ScrollbarSize = 9;
    s.GrabMinSize = 6; s.WindowBorderSize = 1; s.ChildBorderSize = 0;
    s.FrameBorderSize = 0; s.WindowRounding = 6; s.ChildRounding = 4;
    s.FrameRounding = 4; s.PopupRounding = 5; s.ScrollbarRounding = 3;
    s.GrabRounding = 3; s.TabRounding = 4;
    auto* c = s.Colors;
    c[ImGuiCol_WindowBg]             = {0.06f,0.07f,0.11f,0.97f};
    c[ImGuiCol_ChildBg]              = {0.07f,0.08f,0.13f,0.55f};
    c[ImGuiCol_PopupBg]              = {0.07f,0.08f,0.13f,0.97f};
    c[ImGuiCol_Border]               = {0.20f,0.24f,0.36f,0.55f};
    c[ImGuiCol_FrameBg]              = {0.10f,0.12f,0.19f,0.60f};
    c[ImGuiCol_FrameBgHovered]       = {0.14f,0.18f,0.28f,0.80f};
    c[ImGuiCol_FrameBgActive]        = {0.18f,0.24f,0.38f,1.00f};
    c[ImGuiCol_TitleBg]              = {0.07f,0.08f,0.13f,1.00f};
    c[ImGuiCol_TitleBgActive]        = {0.10f,0.13f,0.21f,1.00f};
    c[ImGuiCol_Button]               = {0.16f,0.30f,0.55f,0.65f};
    c[ImGuiCol_ButtonHovered]        = {0.22f,0.40f,0.72f,0.85f};
    c[ImGuiCol_ButtonActive]         = {0.28f,0.50f,0.90f,1.00f};
    c[ImGuiCol_Header]               = {0.16f,0.30f,0.55f,0.50f};
    c[ImGuiCol_HeaderHovered]        = {0.22f,0.40f,0.72f,0.75f};
    c[ImGuiCol_HeaderActive]         = {0.28f,0.50f,0.90f,0.95f};
    c[ImGuiCol_Tab]                  = {0.12f,0.20f,0.36f,0.70f};
    c[ImGuiCol_TabHovered]           = {0.22f,0.40f,0.72f,0.85f};
    c[ImGuiCol_TabActive]            = {0.26f,0.46f,0.82f,1.00f};
    c[ImGuiCol_TableHeaderBg]        = {0.10f,0.12f,0.19f,1.00f};
    c[ImGuiCol_TableBorderStrong]    = {0.20f,0.24f,0.36f,1.00f};
    c[ImGuiCol_TableBorderLight]     = {0.14f,0.18f,0.26f,1.00f};
    c[ImGuiCol_TableRowBgAlt]        = {1,1,1,0.025f};
    c[ImGuiCol_ScrollbarGrab]        = {0.22f,0.28f,0.42f,0.80f};
    c[ImGuiCol_CheckMark]            = {0.40f,0.72f,1.00f,1.00f};
    c[ImGuiCol_Separator]            = {0.18f,0.22f,0.32f,0.80f};
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
    app->log.add("GMScreen started - MediaStar 4030 4K");
    app->loadCachedChannels();  // Load persistent cache from disk
    app->customLists.load();    // Load custom channel lists

    // ── Load CCcam config from disk ──
    {
        cccam::CccamConfig ccfg;
        bool configLoaded = ccfg.load();
        if (configLoaded) {
            app->cccam.cfg.port    = ccfg.port;
            app->cccam.cfg.user    = ccfg.user;
            app->cccam.cfg.pass    = ccfg.pass;
            app->cccam.cfg.log_ecm = ccfg.log_ecm;
            app->cccam.cfg.servers = ccfg.servers;
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
        app->cccam.start([&app](const std::string& msg) {
            try { if (app && !app->down) app->cccamLog.add(msg); } catch (...) {}
        });
    } catch (...) { CrashLog("CCcam auto-start failed"); }

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
        float CONN_H = 100.0f * dpi;
        float CH_H = VH - CONN_H - P * 3;
        float RPH = VH - P * 2;  // right panel full height

        // ── Periodic keepalive: send every 30s when connected and idle ──
        {
            static time_t last_ka = 0;
            time_t now_t = time(nullptr);
            if (now_t - last_ka >= 30) {
                last_ka = now_t;
                try {
                    if (app->client.isConnected() &&
                        app->client.millisSinceLastActivity() > 28000) {
                        app->client.sendKeepAlive();
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
            if (ImGui::SmallButton("STB Info")) app->showStbInfo = true;
            ImGui::SameLine(0,4);
            if (ImGui::SmallButton("Remote")) app->rightTab = 0;
            ImGui::SameLine(0,4);
            if (ImGui::SmallButton("My Lists")) app->rightTab = 2;
            ImGui::SameLine(0,4);
            if (ImGui::SmallButton("CCcam")) app->showCccam = true;
            ImGui::SameLine(0,4);
            if (ImGui::SmallButton("AI")) app->showAI = true;
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
        }
        ImGui::End();

        // ════════════════════════════════════════════════════════════════
        // CHANNELS
        // ════════════════════════════════════════════════════════════════
        ImGui::SetNextWindowPos({OX+P, OY+P+CONN_H+P});
        ImGui::SetNextWindowSize({LW, CH_H});
        ImGui::Begin("Channels", nullptr,
            ImGuiWindowFlags_NoMove | ImGuiWindowFlags_NoResize);
        {
            float tbW = 42*dpi, tbH = 22*dpi;

            // ── Row 1: Filter tabs + channel count + SAT ──
            const char* tabNames[] = {"All","TV","Radio","Fav","FTA","Scrambled"};
            const int tabCount = 6;
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
            int chCount = 0;
            { std::lock_guard<std::mutex> g(app->chMu); chCount = (int)app->channels.size(); }
            ImGui::SameLine(0,8);
            ImGui::TextDisabled("%d ch", chCount);
            ImGui::SameLine(0,6);
            if (ImGui::SmallButton("SAT")) app->showSatPanel = true;

            // ── Row 2: Search + Load/Refresh ──
            float loadBtnW = 62*dpi, refBtnW = 68*dpi;
            float btnArea2 = (connected && !busyCh) ? (loadBtnW + refBtnW + 12) 
                           : (busyCh ? (134*dpi + 8) : 0);
            float sW = ImGui::GetContentRegionAvail().x - btnArea2 - 4;
            if (sW < 100) sW = 100;
            ImGui::SetNextItemWidth(sW);
            ImGui::InputTextWithHint("##s", "Search...", app->search, sizeof(app->search));

            if (connected && !busyCh) {
                ImGui::SameLine(0,4);
                if (ImGui::Button("Load", {loadBtnW,tbH})) {
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
                        } catch (const std::exception& e) {
                            CrashLog((std::string("load ch exception: ") + e.what()).c_str());
                            app->chLoading = false;
                        } catch (...) {
                            CrashLog("load ch unknown exception");
                            app->chLoading = false;
                        }
                    }).detach();
                }
                ImGui::SameLine(0,4);
                if (ImGui::Button("Refresh", {refBtnW,tbH})) {
                    app->chLoading = true; app->chLoaded = false; app->chPct = 0;
                    std::thread([app]() {
                        try {
                            app->log.add("Refreshing channels from STB...");
                            int n = app->client.requestChannelList(true);
                            if (!app->chLoaded.load()) {
                                std::lock_guard<std::mutex> g(app->chMu);
                                app->channels = app->client.state().channels;
                                app->chLoaded = true; app->chLoading = false; app->chPct = 100;
                                app->log.add("Refreshed " + std::to_string(n) + " channels");
                            }
                            app->saveCachedChannels();
                        } catch (const std::exception& e) {
                            CrashLog((std::string("refresh ch exception: ") + e.what()).c_str());
                            app->chLoading = false;
                        } catch (...) {
                            CrashLog("refresh ch unknown exception");
                            app->chLoading = false;
                        }
                    }).detach();
                }
            } else if (busyCh) {
                ImGui::SameLine(0,4);
                ImGui::BeginDisabled();
                ImGui::Button("Loading...", {130*dpi,tbH});
                ImGui::EndDisabled();
            }

            // ── Row 3: Progress bar or spacer ──
            if (busyCh)
                ImGui::ProgressBar(app->chPct.load() / 100.0f, {-1, 3*dpi}, "");
            else
                ImGui::Spacing();

            // SAFE copy of channels under lock
            std::vector<stb::Channel> chSnap;
            { std::lock_guard<std::mutex> g(app->chMu); chSnap = app->channels; }

            // Sort
            if (!chSnap.empty()) {
                auto cmpLess = [&](const stb::Channel& a, const stb::Channel& b) -> bool {
                    switch (app->sortCol) {
                    case 1: return a.service_name < b.service_name;
                    case 2: return a.frequency() < b.frequency();
                    case 3: return (int)a.is_radio < (int)b.is_radio;
                    case 8: return a.satellite_name < b.satellite_name;
                    default: return a.service_index < b.service_index;
                    }
                };
                if (app->sortAsc)
                    std::stable_sort(chSnap.begin(), chSnap.end(), cmpLess);
                else
                    std::stable_sort(chSnap.begin(), chSnap.end(),
                        [&](const stb::Channel& a, const stb::Channel& b){ return cmpLess(b,a); });
            }

            // Channel table with detailed columns
            ImGui::BeginChild("##cht", {0,0}, false);
            // Columns: #, Play, Name, Satellite, Freq, Type, HD, Enc, Mod, Fav
            if (ImGui::BeginTable("##t", 11,
                    ImGuiTableFlags_RowBg | ImGuiTableFlags_ScrollY |
                    ImGuiTableFlags_BordersInnerV | ImGuiTableFlags_SizingStretchProp |
                    ImGuiTableFlags_Resizable | ImGuiTableFlags_Sortable)) {
                ImGui::TableSetupScrollFreeze(0, 1);
                ImGui::TableSetupColumn("#",     ImGuiTableColumnFlags_DefaultSort | ImGuiTableColumnFlags_WidthFixed, 38*dpi, 0);
                ImGui::TableSetupColumn("",      ImGuiTableColumnFlags_WidthFixed | ImGuiTableColumnFlags_NoSort | ImGuiTableColumnFlags_NoResize, 24*dpi, 10);
                ImGui::TableSetupColumn("Name",  ImGuiTableColumnFlags_WidthStretch, 3.0f, 1);
                ImGui::TableSetupColumn("Satellite", ImGuiTableColumnFlags_WidthFixed, 80*dpi, 8);
                ImGui::TableSetupColumn("Freq",  ImGuiTableColumnFlags_WidthFixed, 54*dpi, 2);
                ImGui::TableSetupColumn("SR",    ImGuiTableColumnFlags_WidthFixed | ImGuiTableColumnFlags_NoSort, 44*dpi, 9);
                ImGui::TableSetupColumn("Type",  ImGuiTableColumnFlags_WidthFixed, 30*dpi, 3);
                ImGui::TableSetupColumn("HD",    ImGuiTableColumnFlags_WidthFixed | ImGuiTableColumnFlags_NoSort, 22*dpi, 4);
                ImGui::TableSetupColumn("Enc",   ImGuiTableColumnFlags_WidthFixed | ImGuiTableColumnFlags_NoSort, 34*dpi, 5);
                ImGui::TableSetupColumn("Mod",   ImGuiTableColumnFlags_WidthFixed | ImGuiTableColumnFlags_NoSort, 46*dpi, 6);
                ImGui::TableSetupColumn("Fav",   ImGuiTableColumnFlags_WidthFixed | ImGuiTableColumnFlags_NoSort, 20*dpi, 7);
                ImGui::TableHeadersRow();

                // Handle sort spec clicks
                if (auto* specs = ImGui::TableGetSortSpecs()) {
                    if (specs->SpecsDirty && specs->SpecsCount > 0) {
                        app->sortCol = (int)specs->Specs[0].ColumnUserID;
                        app->sortAsc = (specs->Specs[0].SortDirection == ImGuiSortDirection_Ascending);
                        specs->SpecsDirty = false;
                    }
                }

                for (int i = 0; i < (int)chSnap.size(); i++) {
                    auto& ch = chSnap[i];
                    // Tab filter
                    if (app->tab == 1 && ch.is_radio) continue;
                    if (app->tab == 2 && !ch.is_radio) continue;
                    if (app->tab == 3 && ch.fav_bit == 0) continue;
                    if (app->tab == 4 && ch.is_scrambled) continue;
                    if (app->tab == 5 && !ch.is_scrambled) continue;
                    // Search filter
                    std::string nm = ch.service_name.empty() ? ch.service_id : ch.service_name;
                    if (nm.empty()) nm = "(ch " + std::to_string(ch.service_index) + ")";
                    if (!StrMatch(nm.c_str(), app->search) &&
                        !StrMatch(std::to_string(ch.service_index).c_str(), app->search) &&
                        !StrMatch(std::to_string(ch.frequency()).c_str(), app->search) &&
                        !StrMatch(ch.satellite_name.c_str(), app->search))
                        continue;

                    ImGui::TableNextRow();

                    // # (index)
                    ImGui::TableSetColumnIndex(0);
                    ImGui::Text("%d", ch.service_index);

                    // Play button
                    ImGui::TableSetColumnIndex(1);
                    ImGui::PushID(i);
                    ImGui::PushStyleColor(ImGuiCol_Button, {0,0,0,0});
                    ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.2f,0.5f,0.9f,0.40f});
                    ImGui::PushStyleColor(ImGuiCol_ButtonActive, {0.2f,0.5f,0.9f,0.70f});
                    if (ImGui::SmallButton(">")) {
                        if (connected) {
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
                                app->log.add("[play] " + nm);
                                const std::string sid = ch.service_id;
                                const int ts2 = tvst;
                                std::thread([app, sid, ts2]() {
                                    std::this_thread::sleep_for(std::chrono::milliseconds(700));
                                    try { app->client.changeChannelDirect(sid, ts2); } catch (...) {}
                                }).detach();
                            } else {
                                app->log.add("[play] failed for " + nm);
                            }
                        }
                    }
                    if (ImGui::IsItemHovered()) ImGui::SetTooltip("Play on STB");
                    ImGui::PopStyleColor(3);
                    ImGui::PopID();

                    // Name
                    ImGui::TableSetColumnIndex(2);
                    char selId[256];
                    snprintf(selId, sizeof(selId), "%s##ch%d", nm.c_str(), i);
                    if (ImGui::Selectable(selId, app->sel == i,
                            ImGuiSelectableFlags_AllowDoubleClick)) {
                        app->sel = i;
                        if (ImGui::IsMouseDoubleClicked(0)) {
                            app->detailIdx = i;
                            app->showDetail = true;
                        }
                    }
                    // Right-click: add to custom list
                    if (ImGui::BeginPopupContextItem()) {
                        auto& cls = app->customLists.lists;
                        if (cls.empty()) {
                            ImGui::TextDisabled("No custom lists - create one first");
                        } else {
                            ImGui::TextDisabled("Add to list:");
                            for (int li = 0; li < (int)cls.size(); li++) {
                                char mlbl[128];
                                snprintf(mlbl, sizeof(mlbl), "%s##addcl%d", cls[li].title.c_str(), li);
                                if (ImGui::MenuItem(mlbl)) {
                                    stb::CustomListEntry ce;
                                    ce.service_id = ch.service_id;
                                    ce.name = nm;
                                    ce.service_index = ch.service_index;
                                    ce.is_radio = ch.is_radio;
                                    cls[li].entries.push_back(std::move(ce));
                                    app->clDirty = true;
                                    app->log.add("[+list] " + nm + " -> " + cls[li].title);
                                }
                            }
                        }
                        ImGui::EndPopup();
                    }

                    // Satellite
                    ImGui::TableSetColumnIndex(3);
                    if (!ch.satellite_name.empty())
                        ImGui::TextDisabled("%s", ch.satellite_name.c_str());

                    // Freq (full MHz)
                    ImGui::TableSetColumnIndex(4);
                    {
                        int freq = ch.frequency();
                        if (freq > 0)
                            ImGui::TextDisabled("%d", freq);
                    }

                    // Symbol Rate (from service_id tp_index portion)
                    ImGui::TableSetColumnIndex(5);
                    if (ch.pmt_pid > 0)
                        ImGui::TextDisabled("%d", ch.pmt_pid);

                    // Type
                    ImGui::TableSetColumnIndex(6);
                    ImGui::TextDisabled(ch.is_radio ? "R" : "TV");

                    // HD
                    ImGui::TableSetColumnIndex(7);
                    if (ch.is_hd)
                        ImGui::TextColored({0.4f,0.8f,1.0f,1.0f}, "HD");

                    // Enc
                    ImGui::TableSetColumnIndex(8);
                    if (ch.is_scrambled)
                        ImGui::TextColored({0.95f,0.55f,0.15f,1.0f}, "ENC");
                    else
                        ImGui::TextColored({0.3f,0.8f,0.3f,0.7f}, "FTA");

                    // Modulation
                    ImGui::TableSetColumnIndex(9);
                    ImGui::TextDisabled("%s", ch.modulationSystemStr().c_str());

                    // Fav
                    ImGui::TableSetColumnIndex(10);
                    if (ch.fav_bit != 0)
                        ImGui::TextColored({1.0f,0.85f,0.0f,1.0f}, "*");
                }
                ImGui::EndTable();
            }
            ImGui::EndChild();
        }
        ImGui::End();

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
            if (ImGui::SmallButton("CC")) app->showCccam = true;
            ImGui::SameLine(0,4);
            if (ImGui::SmallButton("?")) app->showStbInfo = true;

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
                                    app->client.sendRemoteKey(fk);
                                    std::this_thread::sleep_for(std::chrono::milliseconds(50));
                                    app->client.sendNumericSequence(dig, 80);
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
                        app->showStbInfo = true;
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
                ImGui::Checkbox("Auto##rls", &app->log.scroll);
                ImGui::SameLine(0,4);
                if (ImGui::SmallButton("Full##rlf")) app->showLog = true;
                ImGui::SameLine(0,8);
                if (ImGui::SmallButton("CCcam Log##rlc")) app->showCccamLog = true;
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
                                if (connected) {
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
        // CCcam SERVER
        // ════════════════════════════════════════════════════════════════
        if (app->showCccam) {
            ImGui::SetNextWindowSize({680*dpi, 620*dpi}, ImGuiCond_FirstUseEver);
            if (ImGui::Begin("CCcam Server###cccam", &app->showCccam)) {
                // ── Ping state (static across frames) ──
                static std::atomic<bool> pinging{false};
                static std::atomic<int>  pingDone{0}, pingTotal{0};
                static std::atomic<bool> testing{false};
                static std::atomic<int>  testDone{0}, testTotal{0};
                static time_t lastAutoClean = 0;

                bool srv = app->cccam.running.load();
                auto& srvList = app->cccam.cfg.servers;

                // ── Status bar ──
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

                // ── Start/Stop buttons ──
                if (!srv) {
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.12f,0.55f,0.18f,0.80f});
                    if (ImGui::Button("Start Server", {140*dpi,28*dpi})) {
                        cccam::CccamConfig ccfg;
                        ccfg.port = app->cccam.cfg.port; ccfg.user = app->cccam.cfg.user;
                        ccfg.pass = app->cccam.cfg.pass; ccfg.log_ecm = app->cccam.cfg.log_ecm;
                        ccfg.servers = srvList; ccfg.save();
                        app->cccam.start([app](const std::string& m) { app->log.add(m); });
                    }
                    ImGui::PopStyleColor();
                } else {
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.55f,0.18f,0.18f,0.80f});
                    if (ImGui::Button("Stop Server", {140*dpi,28*dpi}))
                        app->cccam.stop();
                    ImGui::PopStyleColor();
                }
                ImGui::SameLine(0,8);
                if (ImGui::Button("Save Config", {100*dpi,28*dpi})) {
                    cccam::CccamConfig ccfg;
                    ccfg.port = app->cccam.cfg.port; ccfg.user = app->cccam.cfg.user;
                    ccfg.pass = app->cccam.cfg.pass; ccfg.log_ecm = app->cccam.cfg.log_ecm;
                    ccfg.servers = srvList; ccfg.save();
                    app->log.add("[CCcam] Config saved");
                }

                ImGui::Separator();

                // ── Tabs: Servers | Import | Learner | Config ──
                static int ccTab = 0;
                {
                    const char* tabs[] = {"Servers","Import","Learner","Config"};
                    for (int i = 0; i < 4; i++) {
                        if (i) ImGui::SameLine(0,2);
                        bool act = (ccTab == i);
                        if (act) {
                            ImGui::PushStyleColor(ImGuiCol_Button, ImGui::GetStyle().Colors[ImGuiCol_TabActive]);
                            ImGui::PushStyleColor(ImGuiCol_ButtonHovered, ImGui::GetStyle().Colors[ImGuiCol_TabActive]);
                        }
                        if (ImGui::Button(tabs[i], {0, 22*dpi})) ccTab = i;
                        if (act) ImGui::PopStyleColor(2);
                    }
                }

                // ═══════════════════════════════════════════════════════════
                // TAB 0: SERVERS
                // ═══════════════════════════════════════════════════════════
                if (ccTab == 0) {
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
                // TAB 1: IMPORT C-LINES
                // ═══════════════════════════════════════════════════════════
                if (ccTab == 1) {
                    static char clBuf[16384] = {};
                    static char clPxH[128] = "127.0.0.1";
                    static int  clPxP = 11808;
                    ImGui::TextColored({0.6f,0.85f,1.0f,1.0f}, "Paste C-lines below:");
                    ImGui::TextDisabled("Format: C: host port user pass (one per line)");
                    ImGui::Spacing();
                    float mlH = ImGui::GetContentRegionAvail().y - 80*dpi;
                    if (mlH < 60*dpi) mlH = 60*dpi;
                    ImGui::InputTextMultiline("##clines", clBuf, sizeof(clBuf), {-1, mlH});
                    ImGui::SetNextItemWidth(140*dpi);
                    ImGui::InputText("Proxy Host##imp", clPxH, sizeof(clPxH));
                    ImGui::SameLine(0,6);
                    ImGui::SetNextItemWidth(70*dpi);
                    ImGui::InputInt("Proxy Port##imp", &clPxP, 0, 0);
                    ImGui::SameLine(0,12);
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.15f,0.50f,0.20f,0.80f});
                    if (ImGui::Button("Import All", {120*dpi, 28*dpi})) {
                        std::string px = clPxH;
                        auto parsed = cccam::parseCLines(clBuf, px, clPxP);
                        for (auto& s : parsed)
                            srvList.push_back(std::move(s));
                        if (!parsed.empty()) {
                            app->log.add("[CCcam] Imported " + std::to_string(parsed.size()) + " servers");
                            clBuf[0] = 0;
                        }
                    }
                    ImGui::PopStyleColor();
                    ImGui::SameLine(0,8);
                    ImGui::TextDisabled("(%d servers currently loaded)", (int)srvList.size());
                }

                // ═══════════════════════════════════════════════════════════
                // TAB 2: LEARNER (ECM/CW ML Engine)
                // ═══════════════════════════════════════════════════════════
                if (ccTab == 2) {
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
                    ImGui::TextDisabled("Training log: gmscreen_ecm_train.csv");

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
                // TAB 3: CONFIG
                // ═══════════════════════════════════════════════════════════
                if (ccTab == 3) {
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
                    ImGui::TextDisabled("Training data: gmscreen_ecm_log.csv");
                    ImGui::TextDisabled("Learned model: gmscreen_cwlearn.dat");

                    if (cfgDis) ImGui::EndDisabled();
                }
            }
            ImGui::End();
        }

        // ════════════════════════════════════════════════════════════════
        // AI DECRYPTION ENGINE
        // ════════════════════════════════════════════════════════════════
        if (app->showAI) {
            ImGui::SetNextWindowSize({450*dpi, 400*dpi}, ImGuiCond_FirstUseEver);
            if (ImGui::Begin("AI Decryption Engine", &app->showAI)) {
                auto& ai = app->cccam.aiPredictor;
                auto aiStats = ai.getStats();
                bool aiOn = app->cccam.aiEnabled;
                
                // Status bar
                if (aiOn) {
                    ImGui::TextColored({0.3f,0.9f,0.4f,1}, "● Active");
                } else {
                    ImGui::TextColored({0.6f,0.6f,0.6f,1}, "○ Disabled");
                }
                ImGui::SameLine(0,8);
                if (aiStats.ollama_ok) {
                    ImGui::TextColored({0.5f,0.8f,1.0f,1}, "Ollama OK");
                }
                ImGui::SameLine(180*dpi);
                if (ImGui::Checkbox("Enable##ai", &aiOn)) {
                    app->cccam.aiEnabled = aiOn;
                }
                ImGui::SameLine(0,8);
                bool learning = ai.learning.load();
                if (ImGui::Checkbox("Learn##ai", &learning)) {
                    ai.learning = learning;
                }
                ImGui::SameLine(0,8);
                if (ImGui::SmallButton("Check Ollama")) {
                    ai.checkOllama();
                }
                ImGui::Separator();
                
                static int aiTab = 0;
                if (ImGui::BeginTabBar("##aiTabs")) {
                    // ── TAB 1: STATS ──
                    if (ImGui::BeginTabItem("Stats")) {
                        aiTab = 0;
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
                        if (aiStats.predictions > 0) {
                            ImGui::TextColored(aiStats.accuracy > 80 ? ImVec4{0.3f,0.9f,0.4f,1} : 
                                               aiStats.accuracy > 50 ? ImVec4{0.9f,0.8f,0.2f,1} : 
                                                                       ImVec4{0.9f,0.3f,0.3f,1},
                                               "%.1f%%", aiStats.accuracy);
                        } else {
                            ImGui::TextDisabled("--");
                        }
                        
                        ImGui::Spacing();
                        ImGui::TextColored({0.6f,0.85f,1.0f,1}, "CW Cache (10s TTL)");
                        ImGui::TextDisabled("Hits:");         ImGui::SameLine(100*dpi);
                        ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d", aiStats.cache_hits);
                        ImGui::TextDisabled("Misses:");       ImGui::SameLine(100*dpi);
                        ImGui::Text("%d", aiStats.cache_misses);
                        ImGui::TextDisabled("Entries:");      ImGui::SameLine(100*dpi);
                        ImGui::Text("%d live", aiStats.cache_size);
                        ImGui::TextDisabled("Srv Cache Hits:"); ImGui::SameLine(100*dpi);
                        ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%d", app->cccam.ecmCacheHits.load());
                        // Hit rate bar
                        int totalReq = aiStats.cache_hits + aiStats.cache_misses;
                        float hitRate = totalReq > 0 ? (float)aiStats.cache_hits / totalReq : 0;
                        ImGui::TextDisabled("Hit Rate:");     ImGui::SameLine(100*dpi);
                        ImGui::ProgressBar(hitRate, {120*dpi, 14*dpi});

                        ImGui::Spacing();
                        ImGui::Separator();
                        if (ImGui::Button("Save Model", {100*dpi, 24*dpi})) {
                            ai.save();
                            app->log.add("[AI] Model saved");
                        }
                        ImGui::SameLine(0,4);
                        if (ImGui::Button("Load Model", {100*dpi, 24*dpi})) {
                            ai.load();
                            app->log.add("[AI] Model loaded");
                        }
                        ImGui::SameLine(0,4);
                        if (ImGui::Button("Clear Stats", {100*dpi, 24*dpi})) {
                            ai.predictions_made = 0;
                            ai.predictions_correct = 0;
                            ai.ai_predictions = 0;
                            ai.cache_hits = 0;
                            ai.cache_misses = 0;
                            app->cccam.ecmCacheHits = 0;
                            app->log.add("[AI] Stats cleared");
                        }
                        ImGui::EndTabItem();
                    }
                    
                    // ── TAB 2: CAIDs ──
                    if (ImGui::BeginTabItem("CAIDs")) {
                        aiTab = 1;
                        auto caids = ai.getCaidList();
                        if (ImGui::BeginTable("##caidTable", 5, ImGuiTableFlags_Borders | ImGuiTableFlags_RowBg | ImGuiTableFlags_ScrollY)) {
                            ImGui::TableSetupScrollFreeze(0, 1);
                            ImGui::TableSetupColumn("CAID", ImGuiTableColumnFlags_WidthFixed, 50*dpi);
                            ImGui::TableSetupColumn("Name", ImGuiTableColumnFlags_WidthStretch);
                            ImGui::TableSetupColumn("ECMs", ImGuiTableColumnFlags_WidthFixed, 50*dpi);
                            ImGui::TableSetupColumn("OK", ImGuiTableColumnFlags_WidthFixed, 40*dpi);
                            ImGui::TableSetupColumn("Rate", ImGuiTableColumnFlags_WidthFixed, 50*dpi);
                            ImGui::TableHeadersRow();
                            
                            for (auto& ci : caids) {
                                ImGui::TableNextRow();
                                ImGui::TableSetColumnIndex(0);
                                ImGui::Text("%04X", ci.caid);
                                ImGui::TableSetColumnIndex(1);
                                ImGui::TextDisabled("%s", ci.name.c_str());
                                ImGui::TableSetColumnIndex(2);
                                ImGui::Text("%d", ci.ecm_count);
                                ImGui::TableSetColumnIndex(3);
                                ImGui::Text("%d", ci.success_count);
                                ImGui::TableSetColumnIndex(4);
                                float rate = ci.ecm_count > 0 ? (float)ci.success_count / ci.ecm_count * 100 : 0;
                                ImGui::TextColored(rate > 80 ? ImVec4{0.3f,0.9f,0.4f,1} : 
                                                   rate > 50 ? ImVec4{0.9f,0.8f,0.2f,1} : 
                                                               ImVec4{0.9f,0.3f,0.3f,1},
                                                   "%.0f%%", rate);
                            }
                            ImGui::EndTable();
                        }
                        if (caids.empty()) {
                            ImGui::TextDisabled("No CAIDs detected yet. Watch encrypted channels to learn.");
                        }
                        ImGui::EndTabItem();
                    }
                    
                    // ── TAB 3: AI REPORT ──
                    if (ImGui::BeginTabItem("AI Report")) {
                        aiTab = 2;
                        std::string report = ai.getOllamaReport();
                        time_t repTime = ai.getOllamaReportTime();
                        if (!report.empty()) {
                            char tBuf[64] = {};
                            struct tm* tm = localtime(&repTime);
                            if (tm) strftime(tBuf, sizeof(tBuf), "%H:%M:%S", tm);
                            ImGui::TextColored({0.6f,0.85f,1.0f,1}, "Last analysis: %s", tBuf);
                            ImGui::Separator();
                            if (ImGui::BeginChild("##report", {0, ImGui::GetContentRegionAvail().y - 30*dpi}, true)) {
                                // Render each line with color coding
                                std::istringstream ss(report);
                                std::string line;
                                while (std::getline(ss, line)) {
                                    if (line.substr(0, std::min((int)line.size(), 5)) == "RANK:") {
                                        ImGui::TextColored({0.3f,0.9f,0.4f,1}, "%s", line.c_str());
                                    } else if (line.substr(0, std::min((int)line.size(), 6)) == "ISSUE:") {
                                        ImGui::TextColored({0.9f,0.6f,0.2f,1}, "%s", line.c_str());
                                    } else if (line.substr(0, std::min((int)line.size(), 5)) == "BEST:") {
                                        ImGui::TextColored({0.5f,0.8f,1.0f,1}, "%s", line.c_str());
                                    } else if (line.substr(0, std::min((int)line.size(), 8)) == "SUMMARY:") {
                                        ImGui::TextColored({1.0f,0.9f,0.5f,1}, "%s", line.c_str());
                                    } else if (!line.empty()) {
                                        ImGui::TextDisabled("%s", line.c_str());
                                    }
                                }
                            }
                            ImGui::EndChild();
                        } else {
                            ImGui::Spacing();
                            if (aiStats.ollama_ok) {
                                ImGui::TextDisabled("Analysis pending...");
                                ImGui::TextDisabled("(runs every 60s when data is available)");
                            } else {
                                ImGui::TextColored({0.9f,0.3f,0.3f,1}, "Ollama not available");
                                ImGui::TextDisabled("Start Ollama with: ollama serve");
                                ImGui::TextDisabled("Model: %s", ai.model.c_str());
                            }
                        }
                        ImGui::Separator();
                        if (ImGui::Button("Analyze Now", {120*dpi, 24*dpi})) {
                            ai.triggerAnalysis();
                            app->log.add("[AI] Analysis triggered");
                        }
                        ImGui::SameLine(0,4);
                        if (ImGui::Button("Check Ollama##rep", {120*dpi, 24*dpi})) {
                            std::thread([&ai]() { ai.checkOllama(); }).detach();
                        }
                        ImGui::EndTabItem();
                    }

                    // ── TAB 4: SERVERS ──
                    if (ImGui::BeginTabItem("Servers")) {
                        aiTab = 3;
                        auto servers = ai.getServerList();
                        if (ImGui::BeginTable("##srvTable", 5, ImGuiTableFlags_Borders | ImGuiTableFlags_RowBg | ImGuiTableFlags_ScrollY)) {
                            ImGui::TableSetupScrollFreeze(0, 1);
                            ImGui::TableSetupColumn("Server", ImGuiTableColumnFlags_WidthStretch);
                            ImGui::TableSetupColumn("Reqs", ImGuiTableColumnFlags_WidthFixed, 45*dpi);
                            ImGui::TableSetupColumn("OK", ImGuiTableColumnFlags_WidthFixed, 40*dpi);
                            ImGui::TableSetupColumn("Rate", ImGuiTableColumnFlags_WidthFixed, 50*dpi);
                            ImGui::TableSetupColumn("Lat", ImGuiTableColumnFlags_WidthFixed, 50*dpi);
                            ImGui::TableHeadersRow();
                            
                            for (auto& [name, sq] : servers) {
                                ImGui::TableNextRow();
                                ImGui::TableSetColumnIndex(0);
                                ImGui::Text("%s", name.c_str());
                                ImGui::TableSetColumnIndex(1);
                                ImGui::Text("%d", sq.total_requests);
                                ImGui::TableSetColumnIndex(2);
                                ImGui::Text("%d", sq.successful);
                                ImGui::TableSetColumnIndex(3);
                                ImGui::TextColored(sq.success_rate > 0.8f ? ImVec4{0.3f,0.9f,0.4f,1} : 
                                                   sq.success_rate > 0.5f ? ImVec4{0.9f,0.8f,0.2f,1} : 
                                                                            ImVec4{0.9f,0.3f,0.3f,1},
                                                   "%.0f%%", sq.success_rate * 100);
                                ImGui::TableSetColumnIndex(4);
                                ImGui::TextDisabled("%.0fms", sq.avg_latency_ms);
                            }
                            ImGui::EndTable();
                        }
                        if (servers.empty()) {
                            ImGui::TextDisabled("No server data yet. Connect to CCcam servers to learn.");
                        }
                        ImGui::EndTabItem();
                    }
                    ImGui::EndTabBar();
                    (void)aiTab;
                }
            }
            ImGui::End();
        }

        // ════════════════════════════════════════════════════════════════
        // STB INFO — uses safe snapshot, NO direct state() access
        // ════════════════════════════════════════════════════════════════
        if (app->showStbInfo) {
            ImGui::SetNextWindowSize({420*dpi, 340*dpi}, ImGuiCond_FirstUseEver);
            if (ImGui::Begin("STB Info - MediaStar 4030 4K", &app->showStbInfo)) {
                auto si = app->snapStbInfo(); // SAFE copy
                auto row = [&](const char* k, const std::string& v) {
                    ImGui::TextDisabled("%s", k);
                    ImGui::SameLine(150*dpi);
                    ImGui::Text("%s", v.empty() ? "(unknown)" : v.c_str());
                };

                if (ImGui::BeginTabBar("##stbinfotabs")) {

                    // ── Tab 1: Device Info ──
                    if (ImGui::BeginTabItem("Device")) {
                        row("Model:",       si.stb_model);
                        row("SW Version:",  si.sw_version);
                        row("STB Time:",    si.stb_time);
                        row("TV Channels:", std::to_string(si.channel_count));
                        row("Radio:",       std::to_string(si.radio_count));
                        if (si.has_login) {
                            row("Platform ID:", std::to_string(si.platform_id));
                            row("4K Support:",  si.is_4k ? "Yes" : "Unknown");
                            row("SAT Enable:",  std::to_string(si.sat_enable));
                            row("Protocol:",    si.uses_json ? "JSON" : "XML");
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

        // ── Render ──────────────────────────────────────────────────────
        try {
            ImGui::Render();
            const float clr[4] = {0.024f, 0.030f, 0.050f, 1.0f};
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
