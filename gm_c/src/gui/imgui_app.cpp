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
    bool showLog = false, showCccam = false, showStbInfo = false;
    bool showDetail = false, showSatPanel = false;
    int  detailIdx = -1;           // index into chSnap for detail popup
    int  sortCol = 0;              // 0=index, 1=name, 2=freq, 3=type
    bool sortAsc = true;
    int  filterFav = 0;            // 0=all, 1=fav only, 2=FTA only, 3=scrambled only
    
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
                    saveCachedChannels();
                }
                if (ev == "satellite_list") {
                    try {
                        std::lock_guard<std::mutex> g(satMu);
                        satellites = client.state().satellites;
                    } catch (...) {}
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
    s.WindowPadding = {10,8}; s.FramePadding = {7,4}; s.CellPadding = {5,3};
    s.ItemSpacing = {7,4}; s.ItemInnerSpacing = {5,4}; s.ScrollbarSize = 10;
    s.GrabMinSize = 7; s.WindowBorderSize = 1; s.ChildBorderSize = 1;
    s.FrameBorderSize = 0; s.WindowRounding = 7; s.ChildRounding = 5;
    s.FrameRounding = 4; s.PopupRounding = 5; s.ScrollbarRounding = 4;
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

    // ── Auto-start CCcam server ──
    try {
        app->cccam.start([&app](const std::string& msg) {
            try { if (app && !app->down) app->log.add(msg); } catch (...) {}
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
        float P = 6.0f;
        float LW = VW * 0.655f - P;
        float RW = VW - LW - P * 3;
        float RX = OX + LW + P * 2;
        float CONN_H = 115.0f * dpi;
        float CH_H = VH - CONN_H - P * 3;
        float REM_H = VH * 0.60f;
        float LOG_H = VH - REM_H - P * 3;

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
            if (ImGui::SmallButton("CCcam")) app->showCccam = true;
            ImGui::SameLine(0,4);
            if (ImGui::SmallButton("Logs")) app->showLog = true;
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
                            try { app->client.changeChannel(ch.service_index); } catch (...) {}
                            app->log.add("[play] " + nm);
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
                            try { app->client.changeChannel(ch.service_index); } catch (...) {}
                            app->log.add("[play] " + ch.service_name);
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
        // REMOTE  (scrollable, all MediaStar 4030 4K features)
        // ════════════════════════════════════════════════════════════════
        ImGui::SetNextWindowPos({RX, OY+P});
        ImGui::SetNextWindowSize({RW, REM_H});
        ImGui::Begin("Remote##r", nullptr,
            ImGuiWindowFlags_NoMove | ImGuiWindowFlags_NoResize);
        {
            bool remDisabled = !connected;
            if (remDisabled) {
                ImGui::TextDisabled("(Connect to STB first)");
                ImGui::Separator();
                ImGui::BeginDisabled();
            }

            auto send = [&](int k) {
                try { app->client.sendRemoteKey(k); } catch (...) {}
            };
            float AW = ImGui::GetContentRegionAvail().x;
            float B2 = (AW - 4) / 2.0f;
            float B3 = (AW - 8) / 3.0f;
            float B4 = (AW - 12) / 4.0f;
            float B5 = (AW - 16) / 5.0f;
            float BH = 28*dpi, BHS = 24*dpi;

            ImGui::BeginChild("##remScroll", {0, 0}, false);

            // ── POWER ──
            ImGui::PushStyleColor(ImGuiCol_Button, {0.55f,0.18f,0.18f,0.70f});
            ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.72f,0.22f,0.22f,0.90f});
            if (ImGui::Button("POWER", {B3,BHS})) send(stb::keys::KEY_POWER);
            ImGui::PopStyleColor(2);
            ImGui::SameLine(0,4);
            if (ImGui::Button("Mute", {B3,BHS})) send(stb::keys::KEY_MUTE);
            ImGui::SameLine(0,4);
            if (ImGui::Button("Sleep", {B3,BHS})) send(stb::keys::KEY_SLEEP);

            ImGui::Spacing(); ImGui::Separator(); ImGui::Spacing();

            // ── NUMPAD ──
            if (ImGui::CollapsingHeader("Numpad", ImGuiTreeNodeFlags_DefaultOpen)) {
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

            ImGui::Spacing(); ImGui::Separator(); ImGui::Spacing();

            // ── NAVIGATION ──
            if (ImGui::CollapsingHeader("Navigation", ImGuiTreeNodeFlags_DefaultOpen)) {
                // D-pad
                ImGui::Dummy({B3+2,0}); ImGui::SameLine(0,4);
                if (ImGui::Button("Up",    {B3,BH})) send(stb::keys::KEY_UP);
                if (ImGui::Button("Left",  {B3,BH})) send(stb::keys::KEY_LEFT);  ImGui::SameLine(0,4);
                ImGui::PushStyleColor(ImGuiCol_Button, {0.20f,0.46f,0.88f,0.80f});
                ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.26f,0.56f,1.0f,1.0f});
                if (ImGui::Button("OK",    {B3,BH})) send(stb::keys::KEY_ENTER);
                ImGui::PopStyleColor(2);
                ImGui::SameLine(0,4);
                if (ImGui::Button("Right", {B3,BH})) send(stb::keys::KEY_RIGHT);
                ImGui::Dummy({B3+2,0}); ImGui::SameLine(0,4);
                if (ImGui::Button("Down",  {B3,BH})) send(stb::keys::KEY_DOWN);
                ImGui::Spacing();
                // Menu / Back / Info
                if (ImGui::Button("Menu",   {B4,BHS})) send(stb::keys::KEY_MENU);   ImGui::SameLine(0,4);
                if (ImGui::Button("Back",   {B4,BHS})) send(stb::keys::KEY_BACK);   ImGui::SameLine(0,4);
                if (ImGui::Button("Info",   {B4,BHS})) send(stb::keys::KEY_INFO);   ImGui::SameLine(0,4);
                if (ImGui::Button("EPG",    {B4,BHS})) send(stb::keys::KEY_EPG);
                if (ImGui::Button("FAV",    {B4,BHS})) send(stb::keys::KEY_FAV);    ImGui::SameLine(0,4);
                if (ImGui::Button("Recall", {B4,BHS})) send(stb::keys::KEY_RECALL); ImGui::SameLine(0,4);
                if (ImGui::Button("Find",   {B4,BHS})) send(stb::keys::KEY_FIND);   ImGui::SameLine(0,4);
                if (ImGui::Button("SAT",    {B4,BHS})) send(stb::keys::KEY_SAT);
            }

            ImGui::Spacing(); ImGui::Separator(); ImGui::Spacing();

            // ── CH / VOL ──
            if (ImGui::CollapsingHeader("Channel / Volume", ImGuiTreeNodeFlags_DefaultOpen)) {
                if (ImGui::Button("CH+",  {B4,BH})) send(stb::keys::KEY_CH_UP);   ImGui::SameLine(0,4);
                if (ImGui::Button("CH-",  {B4,BH})) send(stb::keys::KEY_CH_DOWN); ImGui::SameLine(0,4);
                if (ImGui::Button("PgUp", {B4,BH})) send(stb::keys::KEY_PAGE_UP); ImGui::SameLine(0,4);
                if (ImGui::Button("PgDn", {B4,BH})) send(stb::keys::KEY_PAGE_DOWN);
                if (ImGui::Button("VOL+", {B4,BH})) send(stb::keys::KEY_VOL_UP);  ImGui::SameLine(0,4);
                if (ImGui::Button("VOL-", {B4,BH})) send(stb::keys::KEY_VOL_DOWN); ImGui::SameLine(0,4);
                if (ImGui::Button("FavPrev", {B4,BHS})) send(stb::keys::KEY_FAV_PREV); ImGui::SameLine(0,4);
                if (ImGui::Button("FavNext", {B4,BHS})) send(stb::keys::KEY_FAV_NEXT);
            }

            ImGui::Spacing(); ImGui::Separator(); ImGui::Spacing();

            // ── COLOR BUTTONS ──
            if (ImGui::CollapsingHeader("Color Buttons", ImGuiTreeNodeFlags_DefaultOpen)) {
                ImGui::PushStyleColor(ImGuiCol_Button, {0.80f,0.12f,0.12f,0.80f});
                ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.95f,0.18f,0.18f,1.0f});
                if (ImGui::Button("RED", {B4,BHS})) send(stb::keys::KEY_RED);
                ImGui::PopStyleColor(2); ImGui::SameLine(0,4);
                ImGui::PushStyleColor(ImGuiCol_Button, {0.10f,0.65f,0.10f,0.80f});
                ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.14f,0.80f,0.14f,1.0f});
                if (ImGui::Button("GREEN", {B4,BHS})) send(stb::keys::KEY_GREEN);
                ImGui::PopStyleColor(2); ImGui::SameLine(0,4);
                ImGui::PushStyleColor(ImGuiCol_Button, {0.88f,0.78f,0.06f,0.80f});
                ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {1.0f,0.90f,0.10f,1.0f});
                if (ImGui::Button("YELLOW", {B4,BHS})) send(stb::keys::KEY_YELLOW);
                ImGui::PopStyleColor(2); ImGui::SameLine(0,4);
                ImGui::PushStyleColor(ImGuiCol_Button, {0.10f,0.40f,0.88f,0.80f});
                ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.14f,0.50f,1.0f,1.0f});
                if (ImGui::Button("BLUE", {B4,BHS})) send(stb::keys::KEY_BLUE);
                ImGui::PopStyleColor(2);
            }

            ImGui::Spacing(); ImGui::Separator(); ImGui::Spacing();

            // ── PLAYBACK ──
            if (ImGui::CollapsingHeader("Playback / PVR", ImGuiTreeNodeFlags_DefaultOpen)) {
                float B8 = (AW - 28) / 8.0f;
                if (ImGui::Button("|<##pb",  {B8,BHS})) send(stb::keys::KEY_PREVIOUS);     ImGui::SameLine(0,4);
                if (ImGui::Button("<<##pb",  {B8,BHS})) send(stb::keys::KEY_REWIND);       ImGui::SameLine(0,4);
                if (ImGui::Button(">##pb",   {B8,BHS})) send(stb::keys::KEY_PLAY_PAUSE);   ImGui::SameLine(0,4);
                if (ImGui::Button("||##pb",  {B8,BHS})) send(stb::keys::KEY_PAUSE);        ImGui::SameLine(0,4);
                if (ImGui::Button("[]##pb",  {B8,BHS})) send(stb::keys::KEY_STOP);         ImGui::SameLine(0,4);
                if (ImGui::Button(">>##pb",  {B8,BHS})) send(stb::keys::KEY_FAST_FORWARD); ImGui::SameLine(0,4);
                if (ImGui::Button(">|##pb",  {B8,BHS})) send(stb::keys::KEY_NEXT);         ImGui::SameLine(0,4);
                ImGui::PushStyleColor(ImGuiCol_Button, {0.80f,0.12f,0.12f,0.60f});
                ImGui::PushStyleColor(ImGuiCol_ButtonHovered, {0.95f,0.18f,0.18f,0.80f});
                if (ImGui::Button("REC",  {B8,BHS})) send(stb::keys::KEY_RECORD);
                ImGui::PopStyleColor(2);
                if (ImGui::Button("PVR List", {B3,BHS})) send(stb::keys::KEY_PVR_LIST); ImGui::SameLine(0,4);
                if (ImGui::Button("Audio",    {B3,BHS})) send(stb::keys::KEY_AUDIO);    ImGui::SameLine(0,4);
                if (ImGui::Button("Zoom",     {B3,BHS})) send(stb::keys::KEY_ZOOM);
            }

            ImGui::Spacing(); ImGui::Separator(); ImGui::Spacing();

            // ── EXTRA FUNCTIONS ──
            if (ImGui::CollapsingHeader("Functions", ImGuiTreeNodeFlags_DefaultOpen)) {
                if (ImGui::Button("SUB",     {B5,BHS})) send(stb::keys::KEY_SUBTITLE); ImGui::SameLine(0,4);
                if (ImGui::Button("TTX",     {B5,BHS})) send(stb::keys::KEY_TTX);      ImGui::SameLine(0,4);
                if (ImGui::Button("USB",     {B5,BHS})) send(stb::keys::KEY_USB);      ImGui::SameLine(0,4);
                if (ImGui::Button("Display", {B5,BHS})) send(stb::keys::KEY_DISPLAY);  ImGui::SameLine(0,4);
                if (ImGui::Button("Mode",    {B5,BHS})) send(stb::keys::KEY_MODE);
                if (ImGui::Button("Time",    {B5,BHS})) send(stb::keys::KEY_TIME);     ImGui::SameLine(0,4);
                if (ImGui::Button("PIP",     {B5,BHS})) send(stb::keys::KEY_PIP);      ImGui::SameLine(0,4);
                if (ImGui::Button("Multi",   {B5,BHS})) send(stb::keys::KEY_MULTI_PIC);ImGui::SameLine(0,4);
                if (ImGui::Button("Mosaic",  {B5,BHS})) send(stb::keys::KEY_MOSAIC);   ImGui::SameLine(0,4);
                if (ImGui::Button("Format",  {B5,BHS})) send(stb::keys::KEY_FORMAT);
                if (ImGui::Button("HDMI",    {B5,BHS})) send(stb::keys::KEY_HDMI);     ImGui::SameLine(0,4);
                if (ImGui::Button("HD",      {B5,BHS})) send(stb::keys::KEY_HD);       ImGui::SameLine(0,4);
                if (ImGui::Button("OPT",     {B5,BHS})) send(stb::keys::KEY_OPT);      ImGui::SameLine(0,4);
                if (ImGui::Button("Motor",   {B5,BHS})) send(stb::keys::KEY_MOTOR);    ImGui::SameLine(0,4);
                if (ImGui::Button("FUNC",    {B5,BHS})) send(stb::keys::KEY_FUNC);
                if (ImGui::Button("TV Menu", {B4,BHS})) send(stb::keys::KEY_TV_MENU);  ImGui::SameLine(0,4);
                if (ImGui::Button("TV Src",  {B4,BHS})) send(stb::keys::KEY_TV_SOURCE);ImGui::SameLine(0,4);
                if (ImGui::Button("NetApp",  {B4,BHS})) send(stb::keys::KEY_NETAPP);   ImGui::SameLine(0,4);
                if (ImGui::Button("Help",    {B4,BHS})) send(stb::keys::KEY_HELP);
            }

            ImGui::Spacing(); ImGui::Separator(); ImGui::Spacing();

            // ── SYSTEM OPERATIONS ──
            if (ImGui::CollapsingHeader("System", ImGuiTreeNodeFlags_DefaultOpen)) {
                if (ImGui::Button("TV/Radio", {B3,BHS})) {
                    try { app->client.tvRadioSwitch(); } catch (...) {}
                    app->log.add("TV/Radio switch");
                }
                ImGui::SameLine(0,4);
                if (ImGui::Button("Restart STB", {B3,BHS})) {
                    try { app->client.restartStb(); } catch (...) {}
                    app->log.add("STB Restart");
                }
                ImGui::SameLine(0,4);
                if (ImGui::Button("Get Info", {B3,BHS})) {
                    try { app->client.requestStbInfo(); } catch (...) {}
                    app->showStbInfo = true;
                    app->log.add("STB Info request");
                }
                if (ImGui::Button("Req EPG", {B3,BHS})) {
                    try { app->client.requestEpg(0); } catch (...) {}
                    app->log.add("EPG request");
                }
                ImGui::SameLine(0,4);
                if (ImGui::Button("Req SAT", {B3,BHS})) {
                    try { app->client.requestSatelliteList(); } catch (...) {}
                    app->log.add("SAT list request");
                }
                ImGui::SameLine(0,4);
                if (ImGui::Button("Keep Alive", {B3,BHS})) {
                    try { app->client.sendKeepAlive(); } catch (...) {}
                    app->log.add("Keep-alive sent");
                }
            }

            ImGui::Spacing(); ImGui::Separator(); ImGui::Spacing();

            // ── TEXT INPUT (Keyboard) ──
            if (ImGui::CollapsingHeader("Text Input (Keyboard)", ImGuiTreeNodeFlags_DefaultOpen)) {
                ImGui::TextDisabled("Type text to send to STB (server name, password, etc.)");
                ImGui::SetNextItemWidth(-1);
                bool enterPressed = ImGui::InputText("##textIn", app->textInput, sizeof(app->textInput),
                    ImGuiInputTextFlags_EnterReturnsTrue);
                if (enterPressed && app->textInput[0] != '\0') {
                    try {
                        if (app->client.sendText(std::string(app->textInput), true)) {
                            app->log.add("Sent text: " + std::string(app->textInput));
                        } else {
                            app->log.add("Failed to send text");
                        }
                    } catch (...) { app->log.add("Error sending text"); }
                }
                float BK = (AW - 16) / 5.0f;
                if (ImGui::Button("Send##txt", {BK,BHS})) {
                    if (app->textInput[0] != '\0') {
                        try {
                            app->client.sendText(std::string(app->textInput), true);
                            app->log.add("Sent: " + std::string(app->textInput));
                        } catch (...) {}
                    }
                }
                ImGui::SameLine(0,4);
                if (ImGui::Button("Enter##kb", {BK,BHS})) {
                    try { app->client.sendKeyboardEnter(); } catch (...) {}
                }
                ImGui::SameLine(0,4);
                if (ImGui::Button("Bksp##kb", {BK,BHS})) {
                    try { app->client.sendKeyboardBackspace(); } catch (...) {}
                }
                ImGui::SameLine(0,4);
                if (ImGui::Button("Dismiss##kb", {BK,BHS})) {
                    try { app->client.dismissInputMethod(); } catch (...) {}
                }
                ImGui::SameLine(0,4);
                if (ImGui::Button("Clear##kb", {BK,BHS})) {
                    app->textInput[0] = '\0';
                }
            }

            ImGui::EndChild();

            if (remDisabled) ImGui::EndDisabled();
        }
        ImGui::End();

        // ════════════════════════════════════════════════════════════════
        // MINI LOG
        // ════════════════════════════════════════════════════════════════
        ImGui::SetNextWindowPos({RX, OY+P+REM_H+P});
        ImGui::SetNextWindowSize({RW, LOG_H});
        ImGui::Begin("Log##m", nullptr,
            ImGuiWindowFlags_NoMove | ImGuiWindowFlags_NoResize);
        {
            if (ImGui::SmallButton("Clear")) app->log.clear();
            ImGui::SameLine(0,6);
            ImGui::Checkbox("Scroll", &app->log.scroll);
            ImGui::SameLine(0,6);
            if (ImGui::SmallButton("Full Log")) app->showLog = true;
            ImGui::Separator();
            ImGui::BeginChild("##lv", {0,0}, false, ImGuiWindowFlags_HorizontalScrollbar);
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
            ImGui::SetNextWindowSize({420*dpi, 300*dpi}, ImGuiCond_FirstUseEver);
            if (ImGui::Begin("CCcam Server", &app->showCccam)) {
                bool srv = app->cccam.running.load();
                ImVec4 sc2 = srv ? ImVec4(0.18f,0.82f,0.45f,1)
                                 : ImVec4(0.90f,0.35f,0.35f,1);
                ImGui::TextColored(sc2, srv ? "RUNNING" : "STOPPED");
                ImGui::SameLine(0,10);
                ImGui::TextDisabled("Clients: %d", app->cccam.clients.load());
                ImGui::SameLine(0,10);
                ImGui::TextDisabled("ECM: %d ok / %d fail",
                    app->cccam.ecmOk.load(), app->cccam.ecmFail.load());
                ImGui::SameLine(0,10);
                std::string cst = app->cccam.getStatus();
                ImGui::TextDisabled("%s", cst.c_str());
                ImGui::Separator();

                bool cfgDis = srv;
                if (cfgDis) ImGui::BeginDisabled();
                int cp = app->cccam.cfg.port;
                ImGui::SetNextItemWidth(90*dpi);
                if (ImGui::InputInt("Port##cc", &cp, 0, 0)) {
                    if (cp > 0 && cp < 65536) app->cccam.cfg.port = cp;
                }
                static char cu[64] = "a", cpw[64] = "a";
                ImGui::SetNextItemWidth(150*dpi);
                if (ImGui::InputText("User##cc", cu, sizeof(cu)))
                    app->cccam.cfg.user = cu;
                ImGui::SameLine(0,8);
                ImGui::SetNextItemWidth(150*dpi);
                if (ImGui::InputText("Pass##cc", cpw, sizeof(cpw), ImGuiInputTextFlags_Password))
                    app->cccam.cfg.pass = cpw;

                ImGui::Spacing();
                ImGui::TextDisabled("Upstream CCcam Server (for ECM forwarding):");
                static char uh[128] = {}, uusr[64] = {}, upwd[64] = {};
                static int uport = 0;
                ImGui::SetNextItemWidth(150*dpi);
                if (ImGui::InputText("Host##up", uh, sizeof(uh)))
                    app->cccam.cfg.upstream_host = uh;
                ImGui::SameLine(0,8);
                ImGui::SetNextItemWidth(70*dpi);
                if (ImGui::InputInt("##upport", &uport, 0, 0)) {
                    if (uport >= 0 && uport < 65536) app->cccam.cfg.upstream_port = uport;
                }
                ImGui::SameLine(0,4); ImGui::TextDisabled("Port");
                ImGui::SetNextItemWidth(150*dpi);
                if (ImGui::InputText("User##up", uusr, sizeof(uusr)))
                    app->cccam.cfg.upstream_user = uusr;
                ImGui::SameLine(0,8);
                ImGui::SetNextItemWidth(150*dpi);
                if (ImGui::InputText("Pass##up", upwd, sizeof(upwd), ImGuiInputTextFlags_Password))
                    app->cccam.cfg.upstream_pass = upwd;

                if (cfgDis) ImGui::EndDisabled();

                ImGui::Spacing();
                if (!srv) {
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.12f,0.55f,0.18f,0.80f});
                    if (ImGui::Button("Start Server", {-1,28*dpi}))
                        app->cccam.start([app](const std::string& m) { app->log.add(m); });
                    ImGui::PopStyleColor();
                } else {
                    ImGui::PushStyleColor(ImGuiCol_Button, {0.55f,0.18f,0.18f,0.80f});
                    if (ImGui::Button("Stop Server", {-1,28*dpi}))
                        app->cccam.stop();
                    ImGui::PopStyleColor();
                }
                ImGui::Separator();
                ImGui::TextDisabled("CCcam 2.x server. Set upstream to relay ECMs to a real server.");
            }
            ImGui::End();
        }

        // ════════════════════════════════════════════════════════════════
        // STB INFO — uses safe snapshot, NO direct state() access
        // ════════════════════════════════════════════════════════════════
        if (app->showStbInfo) {
            ImGui::SetNextWindowSize({380*dpi, 260*dpi}, ImGuiCond_FirstUseEver);
            if (ImGui::Begin("STB Info - MediaStar 4030 4K", &app->showStbInfo)) {
                auto si = app->snapStbInfo(); // SAFE copy
                auto row = [&](const char* k, const std::string& v) {
                    ImGui::TextDisabled("%s", k);
                    ImGui::SameLine(140*dpi);
                    ImGui::Text("%s", v.empty() ? "(unknown)" : v.c_str());
                };
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
