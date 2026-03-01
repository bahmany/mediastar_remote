#include "gui/main_window.h"
#include "stb/rcu_keys.h"
#include <commctrl.h>
#include <dwmapi.h>
#include <sstream>
#include <iomanip>

#pragma comment(lib, "comctl32.lib")
#pragma comment(lib, "dwmapi.lib")
#pragma comment(lib, "uxtheme.lib")

namespace gui {

// Window class name
constexpr wchar_t WINDOW_CLASS[] = L"GMScreenMainWindow";

// Control IDs
enum ControlId {
    ID_IP_EDIT = 100,
    ID_PORT_EDIT,
    ID_CONNECT_BTN,
    ID_DISCOVER_BTN,
    ID_PROGRESS_BAR,
    ID_CHANNEL_LIST,
    ID_REFRESH_BTN,
    ID_LOG_EDIT,
    ID_STATUS_BAR,
    ID_REMOTE_BASE = 200,
};

// Modern UI Constants
constexpr int CORNER_RADIUS = 8;
constexpr int SPACING = 16;
constexpr int BUTTON_HEIGHT = 36;
constexpr int INPUT_HEIGHT = 36;

MainWindow::MainWindow() = default;

MainWindow::~MainWindow() {
    stopLoading();
    if (loading_thread_.joinable()) {
        loading_thread_.join();
    }
    // Delete fonts
    if (font_title_) DeleteObject(font_title_);
    if (font_body_) DeleteObject(font_body_);
    if (font_small_) DeleteObject(font_small_);
    if (font_mono_) DeleteObject(font_mono_);
    // Delete background brushes
    if (bg_primary_brush_) DeleteObject(bg_primary_brush_);
    if (bg_control_brush_) DeleteObject(bg_control_brush_);
    if (hwnd_) {
        DestroyWindow(hwnd_);
    }
}

void MainWindow::createFonts() {
    // Title font - Segoe UI Semibold 14pt
    font_title_ = CreateFontW(20, 0, 0, 0, FW_SEMIBOLD, FALSE, FALSE, FALSE,
        DEFAULT_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS,
        CLEARTYPE_QUALITY, DEFAULT_PITCH | FF_SWISS, L"Segoe UI");
    
    // Body font - Segoe UI 11pt
    font_body_ = CreateFontW(16, 0, 0, 0, FW_NORMAL, FALSE, FALSE, FALSE,
        DEFAULT_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS,
        CLEARTYPE_QUALITY, DEFAULT_PITCH | FF_SWISS, L"Segoe UI");
    
    // Small font - Segoe UI 9pt
    font_small_ = CreateFontW(13, 0, 0, 0, FW_NORMAL, FALSE, FALSE, FALSE,
        DEFAULT_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS,
        CLEARTYPE_QUALITY, DEFAULT_PITCH | FF_SWISS, L"Segoe UI");
    
    // Mono font for logs - Consolas 10pt
    font_mono_ = CreateFontW(14, 0, 0, 0, FW_NORMAL, FALSE, FALSE, FALSE,
        DEFAULT_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS,
        CLEARTYPE_QUALITY, DEFAULT_PITCH | FF_MODERN, L"Consolas");
}

void MainWindow::applyModernTheme() {
    // Enable dark mode for window
    BOOL dark = TRUE;
    DwmSetWindowAttribute(hwnd_, DWMWA_USE_IMMERSIVE_DARK_MODE, &dark, sizeof(dark));

    // IMPORTANT: Don't extend the frame into the client area here.
    // On some systems this results in a glass-only window where child controls don't render.

    // Create brushes once
    if (!bg_primary_brush_) {
        bg_primary_brush_ = CreateSolidBrush(colors::BG_PRIMARY);
    }
    if (!bg_control_brush_) {
        bg_control_brush_ = CreateSolidBrush(colors::BG_SECONDARY);
    }
}

bool MainWindow::initialize(HINSTANCE hInstance, int nCmdShow) {
    hInstance_ = hInstance;
    
    // Initialize common controls
    INITCOMMONCONTROLSEX iccex;
    iccex.dwSize = sizeof(iccex);
    iccex.dwICC = ICC_LISTVIEW_CLASSES | ICC_PROGRESS_CLASS | ICC_STANDARD_CLASSES;
    InitCommonControlsEx(&iccex);
    
    // Register window class
    WNDCLASSEXW wc = {};
    wc.cbSize = sizeof(wc);
    wc.lpfnWndProc = wndProc;
    wc.hInstance = hInstance;
    wc.lpszClassName = WINDOW_CLASS;
    wc.hbrBackground = CreateSolidBrush(colors::BG_PRIMARY);
    wc.hCursor = LoadCursor(nullptr, IDC_ARROW);
    wc.hIcon = LoadIcon(nullptr, IDI_APPLICATION);
    
    if (!RegisterClassExW(&wc)) {
        return false;
    }
    
    // Create main window with modern size
    hwnd_ = CreateWindowExW(
        0,
        WINDOW_CLASS,
        L"GMScreen",
        WS_OVERLAPPEDWINDOW & ~WS_THICKFRAME & ~WS_MAXIMIZEBOX,
        CW_USEDEFAULT, CW_USEDEFAULT,
        WINDOW_WIDTH, WINDOW_HEIGHT,
        nullptr,
        nullptr,
        hInstance,
        this
    );
    
    if (!hwnd_) {
        return false;
    }
    
    applyModernTheme();
    
    // Create fonts
    createFonts();
    
    // Initialize STB client
    client_ = std::make_unique<stb::STBClient>();
    client_->setAutoReconnect(true);
    
    client_->setConnectionCallback([this](stb::ConnectionState state, const std::string& info) {
        onConnectionStateChanged(state, info);
    });
    
    client_->setNotificationCallback([this](const std::string& event, const std::string& data) {
        onNotification(event, data);
    });
    
    client_->setChannelListCallback([this](int count, int total, bool complete) {
        int percent = (total > 0) ? (count * 100 / total) : (complete ? 100 : 0);
        postUpdateProgress(percent);
        if (complete) {
            postUpdateChannelList(count, true);
            postLogMessage("Loaded " + std::to_string(count) + " channels");
        }
    });
    
    ShowWindow(hwnd_, nCmdShow);
    UpdateWindow(hwnd_);
    
    return true;
}

int MainWindow::run() {
    MSG msg;
    while (GetMessage(&msg, nullptr, 0, 0)) {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }
    return static_cast<int>(msg.wParam);
}

LRESULT CALLBACK MainWindow::wndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {
    MainWindow* window = nullptr;
    
    if (msg == WM_NCCREATE) {
        CREATESTRUCT* cs = reinterpret_cast<CREATESTRUCT*>(lParam);
        window = static_cast<MainWindow*>(cs->lpCreateParams);
        SetWindowLongPtr(hwnd, GWLP_USERDATA, reinterpret_cast<LONG_PTR>(window));
        window->hwnd_ = hwnd;
    } else {
        window = reinterpret_cast<MainWindow*>(GetWindowLongPtr(hwnd, GWLP_USERDATA));
    }
    
    if (window) {
        return window->handleMessage(msg, wParam, lParam);
    }
    
    return DefWindowProc(hwnd, msg, wParam, lParam);
}

LRESULT MainWindow::handleMessage(UINT msg, WPARAM wParam, LPARAM lParam) {
    switch (msg) {
        case WM_CREATE:
            createControls();
            layoutControls();
            return 0;
            
        case WM_SIZE:
            layoutControls();
            return 0;
            
        case WM_UPDATE_CHANNEL_LIST:
            updateChannelList();
            updateProgress(100);
            return 0;
            
        case WM_UPDATE_PROGRESS:
            updateProgress(static_cast<int>(wParam));
            return 0;
            
        case WM_LOG_MESSAGE: {
            std::wstring* msg_ptr = reinterpret_cast<std::wstring*>(lParam);
            if (msg_ptr) {
                appendLog(*msg_ptr);
                delete msg_ptr;
            }
            return 0;
        }
            
        case WM_COMMAND:
            switch (LOWORD(wParam)) {
                case ID_CONNECT_BTN:
                    if (client_->isConnected()) {
                        onDisconnect();
                    } else {
                        onConnect();
                    }
                    break;
                    
                case ID_DISCOVER_BTN:
                    onDiscover();
                    break;
                    
                case ID_REFRESH_BTN:
                    onRefreshChannels();
                    break;
                    
                default:
                    if (LOWORD(wParam) >= ID_REMOTE_BASE && LOWORD(wParam) < ID_REMOTE_BASE + 400) {
                        onRemoteKey(static_cast<int>(LOWORD(wParam) - ID_REMOTE_BASE));
                    }
                    break;
            }
            return 0;
            
        case WM_NOTIFY:
            if (reinterpret_cast<NMHDR*>(lParam)->idFrom == ID_CHANNEL_LIST) {
                if (reinterpret_cast<NMHDR*>(lParam)->code == LVN_ITEMCHANGED) {
                    NMLISTVIEW* nmlv = reinterpret_cast<NMLISTVIEW*>(lParam);
                    if (nmlv->uNewState & LVIS_SELECTED) {
                        onChannelSelect(nmlv->iItem);
                    }
                }
            }
            return 0;
            
        case WM_DESTROY:
            PostQuitMessage(0);
            return 0;
            
        case WM_CTLCOLORSTATIC:
        case WM_CTLCOLOREDIT:
        case WM_CTLCOLORLISTBOX: {
            HDC hdc = reinterpret_cast<HDC>(wParam);
            SetBkColor(hdc, colors::BG_SECONDARY);
            SetTextColor(hdc, colors::TEXT_PRIMARY);
            return reinterpret_cast<LRESULT>(bg_control_brush_ ? bg_control_brush_ : GetStockObject(BLACK_BRUSH));
        }
        
        case WM_CTLCOLORBTN: {
            HDC hdc = reinterpret_cast<HDC>(wParam);
            SetBkMode(hdc, TRANSPARENT);
            SetTextColor(hdc, colors::TEXT_PRIMARY);
            return reinterpret_cast<LRESULT>(bg_primary_brush_ ? bg_primary_brush_ : GetStockObject(BLACK_BRUSH));
        }
            
        case WM_ERASEBKGND: {
            HDC hdc = reinterpret_cast<HDC>(wParam);
            RECT rc;
            GetClientRect(hwnd_, &rc);
            FillRect(hdc, &rc, bg_primary_brush_ ? bg_primary_brush_ : reinterpret_cast<HBRUSH>(GetStockObject(BLACK_BRUSH)));
            return 1;
        }
        
        case WM_PAINT: {
            PAINTSTRUCT ps;
            HDC hdc = BeginPaint(hwnd_, &ps);
            RECT rc;
            GetClientRect(hwnd_, &rc);
            FillRect(hdc, &rc, bg_primary_brush_ ? bg_primary_brush_ : reinterpret_cast<HBRUSH>(GetStockObject(BLACK_BRUSH)));
            EndPaint(hwnd_, &ps);
            return 0;
        }
    }
    
    return DefWindowProc(hwnd_, msg, wParam, lParam);
}

void MainWindow::createControls() {
    // Left panel - Connection & Channels
    int left = SPACING;
    int top = SPACING + 32;
    int panelWidth = 520;
    
    // Connection title
    HWND hwndTitle = CreateWindowW(L"STATIC", L"Connection",
        WS_CHILD | WS_VISIBLE | SS_LEFT,
        left + SPACING, top, 200, 24, hwnd_, nullptr, hInstance_, nullptr);
    SendMessage(hwndTitle, WM_SETFONT, reinterpret_cast<WPARAM>(font_title_), TRUE);
    
    // IP input
    CreateWindowW(L"STATIC", L"IP Address",
        WS_CHILD | WS_VISIBLE | SS_LEFT,
        left + SPACING, top + 35, 80, 18, hwnd_, nullptr, hInstance_, nullptr);
    
    hwndIpEdit_ = CreateWindowW(L"EDIT", L"192.168.1.100",
        WS_CHILD | WS_VISIBLE | WS_BORDER | ES_AUTOHSCROLL,
        left + SPACING + 85, top + 32, 140, INPUT_HEIGHT, hwnd_,
        reinterpret_cast<HMENU>(ID_IP_EDIT), hInstance_, nullptr);
    SendMessage(hwndIpEdit_, WM_SETFONT, reinterpret_cast<WPARAM>(font_body_), TRUE);
    
    // Port input
    CreateWindowW(L"STATIC", L"Port",
        WS_CHILD | WS_VISIBLE | SS_LEFT,
        left + SPACING + 240, top + 35, 35, 18, hwnd_, nullptr, hInstance_, nullptr);
    
    hwndPortEdit_ = CreateWindowW(L"EDIT", L"20000",
        WS_CHILD | WS_VISIBLE | WS_BORDER | ES_NUMBER,
        left + SPACING + 280, top + 32, 70, INPUT_HEIGHT, hwnd_,
        reinterpret_cast<HMENU>(ID_PORT_EDIT), hInstance_, nullptr);
    SendMessage(hwndPortEdit_, WM_SETFONT, reinterpret_cast<WPARAM>(font_body_), TRUE);
    
    // Connect button
    hwndConnectBtn_ = CreateWindowW(L"BUTTON", L"Connect",
        WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
        left + panelWidth - 120 - SPACING, top + 32, 100, BUTTON_HEIGHT,
        hwnd_, reinterpret_cast<HMENU>(ID_CONNECT_BTN), hInstance_, nullptr);
    SendMessage(hwndConnectBtn_, WM_SETFONT, reinterpret_cast<WPARAM>(font_body_), TRUE);
    
    // Discover button
    hwndDiscoverBtn_ = CreateWindowW(L"BUTTON", L"Discover",
        WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
        left + SPACING, top + 80, 100, 28,
        hwnd_, reinterpret_cast<HMENU>(ID_DISCOVER_BTN), hInstance_, nullptr);
    SendMessage(hwndDiscoverBtn_, WM_SETFONT, reinterpret_cast<WPARAM>(font_small_), TRUE);
    
    // Status text
    hwndStatus_ = CreateWindowW(L"STATIC", L"Ready to connect",
        WS_CHILD | WS_VISIBLE | SS_LEFT,
        left + SPACING + 120, top + 85, 250, 20, hwnd_, nullptr, hInstance_, nullptr);
    SendMessage(hwndStatus_, WM_SETFONT, reinterpret_cast<WPARAM>(font_small_), TRUE);
    
    // Channel list section title
    int channelTop = top + 130;
    HWND hwndChannelTitle = CreateWindowW(L"STATIC", L"Channels",
        WS_CHILD | WS_VISIBLE | SS_LEFT,
        left + SPACING, channelTop, 200, 24, hwnd_, nullptr, hInstance_, nullptr);
    SendMessage(hwndChannelTitle, WM_SETFONT, reinterpret_cast<WPARAM>(font_title_), TRUE);
    
    // Progress bar
    hwndProgress_ = CreateWindowW(PROGRESS_CLASSW, L"",
        WS_CHILD | WS_VISIBLE | PBS_SMOOTH,
        left + SPACING, channelTop + 30, panelWidth - SPACING * 2, 4,
        hwnd_, reinterpret_cast<HMENU>(ID_PROGRESS_BAR), hInstance_, nullptr);
    SendMessage(hwndProgress_, PBM_SETRANGE, 0, MAKELPARAM(0, 100));
    SendMessage(hwndProgress_, PBM_SETPOS, 0, 0);
    
    // Channel list
    hwndChannelList_ = CreateWindowW(L"SysListView32", L"",
        WS_CHILD | WS_VISIBLE | WS_BORDER | LVS_REPORT | LVS_SINGLESEL | LVS_NOCOLUMNHEADER,
        left + SPACING, channelTop + 42, panelWidth - SPACING * 2, 420,
        hwnd_, reinterpret_cast<HMENU>(ID_CHANNEL_LIST), hInstance_, nullptr);
    
    ListView_SetExtendedListViewStyle(hwndChannelList_, 
        LVS_EX_FULLROWSELECT | LVS_EX_DOUBLEBUFFER | LVS_EX_AUTOSIZECOLUMNS);
    
    // Add columns
    LVCOLUMNW lvc = {};
    lvc.mask = LVCF_TEXT | LVCF_WIDTH;
    lvc.pszText = const_cast<LPWSTR>(L"#");
    lvc.cx = 50;
    ListView_InsertColumn(hwndChannelList_, 0, &lvc);
    
    lvc.pszText = const_cast<LPWSTR>(L"Channel");
    lvc.cx = 350;
    ListView_InsertColumn(hwndChannelList_, 1, &lvc);
    
    lvc.pszText = const_cast<LPWSTR>(L"Type");
    lvc.cx = 70;
    ListView_InsertColumn(hwndChannelList_, 2, &lvc);
    
    // Refresh button
    CreateWindowW(L"BUTTON", L"Refresh",
        WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
        left + SPACING, channelTop + 470, 90, 28,
        hwnd_, reinterpret_cast<HMENU>(ID_REFRESH_BTN), hInstance_, nullptr);
    
    // Right panel - Remote Control & Log
    int rightLeft = left + panelWidth + SPACING;
    int rightWidth = WINDOW_WIDTH - rightLeft - SPACING;
    
    // Remote panel title
    HWND hwndRemoteTitle = CreateWindowW(L"STATIC", L"Remote Control",
        WS_CHILD | WS_VISIBLE | SS_LEFT,
        rightLeft, top, 200, 24, hwnd_, nullptr, hInstance_, nullptr);
    SendMessage(hwndRemoteTitle, WM_SETFONT, reinterpret_cast<WPARAM>(font_title_), TRUE);
    
    // Remote control grid
    int remoteTop = top + 40;
    int btnSize = 52;
    int btnGap = 8;
    
    // Number pad
    const wchar_t* numbers[] = { L"1", L"2", L"3", L"4", L"5", L"6", L"7", L"8", L"9" };
    int num_keys[] = { stb::keys::KEY_1, stb::keys::KEY_2, stb::keys::KEY_3,
                       stb::keys::KEY_4, stb::keys::KEY_5, stb::keys::KEY_6,
                       stb::keys::KEY_7, stb::keys::KEY_8, stb::keys::KEY_9 };
    for (int i = 0; i < 9; ++i) {
        int row = i / 3;
        int col = i % 3;
        HWND btn = CreateWindowW(L"BUTTON", numbers[i],
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            rightLeft + col * (btnSize + btnGap), remoteTop + row * (btnSize + btnGap),
            btnSize, btnSize,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + num_keys[i]), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_body_), TRUE);
    }
    
    // 0 button
    HWND btn0 = CreateWindowW(L"BUTTON", L"0",
        WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
        rightLeft + (btnSize + btnGap), remoteTop + 3 * (btnSize + btnGap),
        btnSize, btnSize,
        hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_0), hInstance_, nullptr);
    SendMessage(btn0, WM_SETFONT, reinterpret_cast<WPARAM>(font_body_), TRUE);
    
    // CH/VOL/PgUp/PgDn buttons
    int navLeft = rightLeft + 3 * (btnSize + btnGap) + 20;
    int navTop = remoteTop;
    const wchar_t* nav_labels[] = { L"CH+", L"CH-", L"VOL+", L"VOL-", L"PgUp", L"PgDn" };
    int nav_keys[] = { stb::keys::KEY_CH_UP, stb::keys::KEY_CH_DOWN, 
                       stb::keys::KEY_VOL_UP, stb::keys::KEY_VOL_DOWN,
                       stb::keys::KEY_PAGE_UP, stb::keys::KEY_PAGE_DOWN };
    
    for (int i = 0; i < 4; ++i) {
        int row = i / 2;
        int col = i % 2;
        HWND btn = CreateWindowW(L"BUTTON", nav_labels[i],
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            navLeft + col * (btnSize + btnGap), navTop + row * (btnSize + btnGap),
            btnSize, btnSize,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + nav_keys[i]), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_small_), TRUE);
    }
    // PgUp / PgDn row
    for (int i = 4; i < 6; ++i) {
        int col = i - 4;
        HWND btn = CreateWindowW(L"BUTTON", nav_labels[i],
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            navLeft + col * (btnSize + btnGap), navTop + 2 * (btnSize + btnGap),
            btnSize, 30,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + nav_keys[i]), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_small_), TRUE);
    }
    
    // D-pad (UP / LEFT / OK / RIGHT / DOWN)
    int dpadLeft = navLeft;
    int dpadTop = navTop + 2 * (btnSize + btnGap) + 40;
    int dpadBtn = 42;
    int dpadGap = 4;
    // UP
    {
        HWND btn = CreateWindowW(L"BUTTON", L"\x25B2",
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            dpadLeft + dpadBtn + dpadGap, dpadTop, dpadBtn, dpadBtn,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_UP), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_body_), TRUE);
    }
    // LEFT
    {
        HWND btn = CreateWindowW(L"BUTTON", L"\x25C0",
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            dpadLeft, dpadTop + dpadBtn + dpadGap, dpadBtn, dpadBtn,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_LEFT), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_body_), TRUE);
    }
    // OK
    {
        HWND btn = CreateWindowW(L"BUTTON", L"OK",
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            dpadLeft + dpadBtn + dpadGap, dpadTop + dpadBtn + dpadGap, dpadBtn, dpadBtn,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_ENTER), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_body_), TRUE);
    }
    // RIGHT
    {
        HWND btn = CreateWindowW(L"BUTTON", L"\x25B6",
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            dpadLeft + 2 * (dpadBtn + dpadGap), dpadTop + dpadBtn + dpadGap, dpadBtn, dpadBtn,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_RIGHT), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_body_), TRUE);
    }
    // DOWN
    {
        HWND btn = CreateWindowW(L"BUTTON", L"\x25BC",
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            dpadLeft + dpadBtn + dpadGap, dpadTop + 2 * (dpadBtn + dpadGap), dpadBtn, dpadBtn,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_DOWN), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_body_), TRUE);
    }
    
    // Power / Mute
    {
        HWND btn = CreateWindowW(L"BUTTON", L"POWER",
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            rightLeft, remoteTop + 4 * (btnSize + btnGap) + 10, 70, 30,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_POWER), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_small_), TRUE);
    }
    {
        HWND btn = CreateWindowW(L"BUTTON", L"MUTE",
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            rightLeft + 78, remoteTop + 4 * (btnSize + btnGap) + 10, 52, 30,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_MUTE), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_small_), TRUE);
    }
    
    // Function buttons row 1
    int funcTop = remoteTop + 4 * (btnSize + btnGap) + 50;
    const wchar_t* func_labels[] = { L"MENU", L"BACK", L"EPG", L"INFO" };
    int func_keys[] = { stb::keys::KEY_MENU, stb::keys::KEY_BACK, 
                        stb::keys::KEY_EPG, stb::keys::KEY_INFO };
    
    for (int i = 0; i < 4; ++i) {
        HWND btn = CreateWindowW(L"BUTTON", func_labels[i],
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            rightLeft + i * (70 + btnGap), funcTop,
            70, 32,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + func_keys[i]), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_small_), TRUE);
    }
    
    // Function buttons row 2 (TV/R, Recall, SUB, TTX, USB)
    int func2Top = funcTop + 40;
    const wchar_t* func2_labels[] = { L"TV/R", L"Recall", L"SUB", L"TTX" };
    int func2_keys[] = { stb::keys::KEY_TV_RADIO, stb::keys::KEY_RECALL, 
                         stb::keys::KEY_SUBTITLE, stb::keys::KEY_TTX };
    for (int i = 0; i < 4; ++i) {
        HWND btn = CreateWindowW(L"BUTTON", func2_labels[i],
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            rightLeft + i * (70 + btnGap), func2Top,
            70, 32,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + func2_keys[i]), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_small_), TRUE);
    }
    
    // USB button
    {
        HWND btn = CreateWindowW(L"BUTTON", L"USB",
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            rightLeft, func2Top + 40, 70, 32,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_USB), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_small_), TRUE);
    }
    
    // Color buttons
    int colorTop = func2Top + 80;
    HWND btnRed = CreateWindowW(L"BUTTON", L"",
        WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_OWNERDRAW,
        rightLeft, colorTop, 70, 28,
        hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_RED), hInstance_, nullptr);
    
    HWND btnGreen = CreateWindowW(L"BUTTON", L"",
        WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_OWNERDRAW,
        rightLeft + 78, colorTop, 70, 28,
        hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_GREEN), hInstance_, nullptr);
    
    HWND btnYellow = CreateWindowW(L"BUTTON", L"",
        WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_OWNERDRAW,
        rightLeft + 156, colorTop, 70, 28,
        hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_YELLOW), hInstance_, nullptr);
    
    HWND btnBlue = CreateWindowW(L"BUTTON", L"",
        WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON | BS_OWNERDRAW,
        rightLeft + 234, colorTop, 70, 28,
        hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + stb::keys::KEY_BLUE), hInstance_, nullptr);
    
    // Playback controls
    int pbTop = colorTop + 38;
    int pbW = 36;
    int pbGap = 4;
    const wchar_t* pb_labels[] = { L"|<", L"<<", L">", L"||", L"[]", L">>", L">|", L"\x25CF" };
    int pb_keys[] = { stb::keys::KEY_PREVIOUS, stb::keys::KEY_REWIND, 
                      stb::keys::KEY_PLAY_PAUSE, stb::keys::KEY_PAUSE,
                      stb::keys::KEY_STOP, stb::keys::KEY_FAST_FORWARD, 
                      stb::keys::KEY_NEXT, stb::keys::KEY_RECORD };
    for (int i = 0; i < 8; ++i) {
        HWND btn = CreateWindowW(L"BUTTON", pb_labels[i],
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            rightLeft + i * (pbW + pbGap), pbTop,
            pbW, 28,
            hwnd_, reinterpret_cast<HMENU>(ID_REMOTE_BASE + pb_keys[i]), hInstance_, nullptr);
        SendMessage(btn, WM_SETFONT, reinterpret_cast<WPARAM>(font_small_), TRUE);
    }
    
    // Log section title
    int logTop = pbTop + 46;
    HWND hwndLogTitle = CreateWindowW(L"STATIC", L"Activity Log",
        WS_CHILD | WS_VISIBLE | SS_LEFT,
        rightLeft, logTop, 200, 20, hwnd_, nullptr, hInstance_, nullptr);
    SendMessage(hwndLogTitle, WM_SETFONT, reinterpret_cast<WPARAM>(font_title_), TRUE);
    
    // Log window
    hwndLog_ = CreateWindowW(L"EDIT", L"",
        WS_CHILD | WS_VISIBLE | WS_BORDER | ES_MULTILINE | ES_READONLY | 
        ES_AUTOVSCROLL | WS_VSCROLL | ES_NOHIDESEL,
        rightLeft, logTop + 28, rightWidth, 200,
        hwnd_, reinterpret_cast<HMENU>(ID_LOG_EDIT), hInstance_, nullptr);
    SendMessage(hwndLog_, WM_SETFONT, reinterpret_cast<WPARAM>(font_mono_), TRUE);
}

void MainWindow::layoutControls() {
    RECT rc;
    GetClientRect(hwnd_, &rc);
    
    // Resize channel list to fill available vertical space
    int channelListTop = SPACING + 32 + 130 + 42;
    int channelListHeight = rc.bottom - channelListTop - SPACING - 40;
    if (channelListHeight > 300) {
        SetWindowPos(hwndChannelList_, nullptr, 
            SPACING * 2, channelListTop, 
            520 - SPACING * 2, channelListHeight,
            SWP_NOZORDER);
    }
}

void MainWindow::onConnect() {
    wchar_t ip_wide[256];
    wchar_t port_wide[32];
    GetWindowTextW(hwndIpEdit_, ip_wide, 256);
    GetWindowTextW(hwndPortEdit_, port_wide, 32);
    
    char ip[256];
    char port_str[32];
    WideCharToMultiByte(CP_UTF8, 0, ip_wide, -1, ip, 256, nullptr, nullptr);
    WideCharToMultiByte(CP_UTF8, 0, port_wide, -1, port_str, 32, nullptr, nullptr);
    
    int port = atoi(port_str);
    if (port == 0) port = 20000;
    
    log("Connecting to " + std::string(ip) + ":" + std::to_string(port) + "...");
    
    // Connect in background thread to keep UI responsive
    std::thread([this, ip_str = std::string(ip), port]() {
        if (client_->connect(ip_str, port)) {
            postLogMessage("Connected successfully!");
            // Load channels in background
            loadChannelsAsync(false);
        } else {
            postLogMessage("Connection failed: " + client_->getLastError());
        }
    }).detach();
}

void MainWindow::onDisconnect() {
    stopLoading();
    client_->disconnect();
    SetWindowTextW(hwndConnectBtn_, L"Connect");
    updateStatus("Disconnected");
    log("Disconnected");
}

void MainWindow::onDiscover() {
    log("Discovering devices...");
    
    auto devices = stb::STBClient::discoverDevices(5000);
    
    if (devices.empty()) {
        log("No devices found");
        MessageBoxW(hwnd_, L"No devices found", L"Discovery", MB_OK | MB_ICONINFORMATION);
    } else {
        std::wstring device_list;
        for (const auto& dev : devices) {
            device_list += std::wstring(dev.ip.begin(), dev.ip.end()) + L" - " +
                          std::wstring(dev.model_name.begin(), dev.model_name.end()) + L"\n";
            
            // Set first device IP
            SetWindowTextW(hwndIpEdit_, std::wstring(dev.ip.begin(), dev.ip.end()).c_str());
        }
        
        log("Found " + std::to_string(devices.size()) + " device(s)");
        MessageBoxW(hwnd_, device_list.c_str(), L"Discovered Devices", MB_OK | MB_ICONINFORMATION);
    }
}

void MainWindow::onChannelSelect(int index) {
    if (client_->isConnected()) {
        client_->changeChannel(index);
        log("Changed to channel " + std::to_string(index));
    }
}

void MainWindow::onRemoteKey(int key) {
    if (client_->isConnected()) {
        if (client_->sendRemoteKey(key)) {
            log("Sent key: " + std::to_string(key));
        } else {
            log("Failed to send key");
        }
    } else {
        log("Not connected");
    }
}

void MainWindow::onRefreshChannels() {
    if (client_->isConnected()) {
        log("Refreshing channels...");
        ListView_DeleteAllItems(hwndChannelList_);
        loadChannelsAsync(true);
    } else {
        log("Not connected");
    }
}

void MainWindow::onConnectionStateChanged(stb::ConnectionState state, const std::string& info) {
    switch (state) {
        case stb::ConnectionState::Disconnected:
            updateStatus("Disconnected");
            SetWindowTextW(hwndConnectBtn_, L"Connect");
            break;
        case stb::ConnectionState::Connecting:
            updateStatus("Connecting...");
            break;
        case stb::ConnectionState::Authenticated:
            updateStatus("Connected");
            SetWindowTextW(hwndConnectBtn_, L"Disconnect");
            updateTitle();
            break;
        case stb::ConnectionState::Reconnecting:
            updateStatus("Reconnecting...");
            break;
        case stb::ConnectionState::ConnectionFailed:
            updateStatus("Connection Failed");
            SetWindowTextW(hwndConnectBtn_, L"Connect");
            break;
        default:
            break;
    }
    
    log(info);
}

void MainWindow::onNotification(const std::string& event, const std::string& data) {
    if (event == "channel_list_complete") {
        postUpdateChannelList(0, true);
    }
}

void MainWindow::loadChannelsAsync(bool force_refresh) {
    stopLoading();
    loading_cancelled_ = false;
    
    loading_thread_ = std::thread([this, force_refresh]() {
        postUpdateProgress(0);
        postLogMessage("Loading channels...");
        
        int count = client_->requestChannelList(force_refresh);
        
        if (!loading_cancelled_) {
            if (count > 0) {
                postLogMessage("Loaded " + std::to_string(count) + " channels");
                postUpdateChannelList(count, true);
            } else {
                postLogMessage("No channels loaded");
            }
        }
    });
    loading_thread_.detach();
}

void MainWindow::stopLoading() {
    loading_cancelled_ = true;
}

void MainWindow::postLogMessage(const std::string& message) {
    std::wstring* wmsg = new std::wstring(message.begin(), message.end());
    PostMessage(hwnd_, WM_LOG_MESSAGE, 0, reinterpret_cast<LPARAM>(wmsg));
}

void MainWindow::postUpdateChannelList(int count, bool complete) {
    PostMessage(hwnd_, WM_UPDATE_CHANNEL_LIST, static_cast<WPARAM>(count), complete ? 1 : 0);
}

void MainWindow::postUpdateProgress(int percent) {
    PostMessage(hwnd_, WM_UPDATE_PROGRESS, static_cast<WPARAM>(percent), 0);
}

void MainWindow::updateProgress(int percent) {
    if (hwndProgress_) {
        SendMessage(hwndProgress_, PBM_SETPOS, percent, 0);
    }
}

void MainWindow::updateChannelList() {
    ListView_DeleteAllItems(hwndChannelList_);
    
    const auto& channels = client_->state().channels;
    
    for (size_t i = 0; i < channels.size() && i < 1000; ++i) {  // Limit to 1000 for UI performance
        const auto& ch = channels[i];
        
        LVITEM lvi = {};
        lvi.mask = LVIF_TEXT;
        lvi.iItem = static_cast<int>(i);
        
        // Index
        std::wstring num = std::to_wstring(ch.service_index);
        lvi.pszText = num.data();
        ListView_InsertItem(hwndChannelList_, &lvi);
        
        // Name
        std::wstring name(ch.service_name.begin(), ch.service_name.end());
        ListView_SetItemText(hwndChannelList_, i, 1, const_cast<LPWSTR>(name.c_str()));
        
        // Type
        std::wstring type = (ch.channel_type == 0) ? L"TV" : L"Radio";
        ListView_SetItemText(hwndChannelList_, i, 2, const_cast<LPWSTR>(type.c_str()));
    }
}

void MainWindow::updateStatus(const std::string& status) {
    std::wstring wstatus(status.begin(), status.end());
    SetWindowTextW(hwndStatus_, wstatus.c_str());
}

void MainWindow::updateTitle() {
    if (client_->isConnected() && client_->loginInfo()) {
        std::string title = "GMScreen - " + client_->loginInfo()->modelName();
        std::wstring wtitle(title.begin(), title.end());
        SetWindowTextW(hwnd_, wtitle.c_str());
    } else {
        SetWindowTextW(hwnd_, L"GMScreen - MediaStar STB Control");
    }
}

void MainWindow::log(const std::string& message) {
    std::wstring wmsg(message.begin(), message.end());
    wmsg += L"\r\n";
    
    int len = GetWindowTextLengthW(hwndLog_);
    SendMessageW(hwndLog_, EM_SETSEL, len, len);
    SendMessageW(hwndLog_, EM_REPLACESEL, FALSE, reinterpret_cast<LPARAM>(wmsg.c_str()));
    
    // Auto-scroll
    SendMessageW(hwndLog_, EM_SCROLLCARET, 0, 0);
}

void MainWindow::appendLog(const std::wstring& message) {
    int len = GetWindowTextLengthW(hwndLog_);
    SendMessageW(hwndLog_, EM_SETSEL, len, len);
    SendMessageW(hwndLog_, EM_REPLACESEL, FALSE, reinterpret_cast<LPARAM>(message.c_str()));
    SendMessageW(hwndLog_, EM_SCROLLCARET, 0, 0);
}

} // namespace gui
