#pragma once

#include <windows.h>
#include <string>
#include <memory>
#include <functional>
#include <thread>
#include <atomic>

#include "stb/stb_client.h"

namespace gui {

// Custom window messages for thread-safe UI updates
constexpr UINT WM_UPDATE_CHANNEL_LIST = WM_USER + 1;
constexpr UINT WM_UPDATE_PROGRESS = WM_USER + 2;
constexpr UINT WM_LOG_MESSAGE = WM_USER + 3;

// Modern Color Palette (OkLCH-based for perceptual uniformity)
namespace colors {
    // Background layers
    constexpr COLORREF BG_PRIMARY = RGB(13, 13, 18);      // Deep dark
    constexpr COLORREF BG_SECONDARY = RGB(23, 23, 30);    // Card background
    constexpr COLORREF BG_TERTIARY = RGB(33, 33, 42);   // Elevated surfaces
    
    // Accent colors (vibrant but harmonious)
    constexpr COLORREF ACCENT_PRIMARY = RGB(99, 102, 241);    // Indigo
    constexpr COLORREF ACCENT_SECONDARY = RGB(139, 92, 246); // Purple
    constexpr COLORREF ACCENT_SUCCESS = RGB(34, 197, 94);    // Green
    constexpr COLORREF ACCENT_WARNING = RGB(251, 191, 36); // Amber
    constexpr COLORREF ACCENT_ERROR = RGB(239, 68, 68);    // Red
    
    // Text colors
    constexpr COLORREF TEXT_PRIMARY = RGB(250, 250, 252);   // Almost white
    constexpr COLORREF TEXT_SECONDARY = RGB(161, 161, 170); // Muted
    constexpr COLORREF TEXT_MUTED = RGB(113, 113, 122);     // Very muted
    
    // Borders and dividers
    constexpr COLORREF BORDER_SUBTLE = RGB(55, 55, 65);
    constexpr COLORREF BORDER_ACTIVE = RGB(99, 102, 241);
}

/**
 * @brief Modern main application window for GMScreen
 * 
 * Features:
 * - Dark modern theme with color science
 * - Acrylic/glass effects
 * - Smooth animations
 * - Minimal design
 */
class MainWindow {
public:
    MainWindow();
    ~MainWindow();
    
    // Disable copy/move
    MainWindow(const MainWindow&) = delete;
    MainWindow& operator=(const MainWindow&) = delete;
    
    /**
     * @brief Initialize and create the window
     * @param hInstance Application instance
     * @param nCmdShow Show command
     * @return true on success
     */
    bool initialize(HINSTANCE hInstance, int nCmdShow);
    
    /**
     * @brief Run the message loop
     * @return Exit code
     */
    int run();
    
    /**
     * @brief Get window handle
     */
    HWND hwnd() const { return hwnd_; }
    
private:
    // Window handle
    HWND hwnd_ = nullptr;
    HINSTANCE hInstance_ = nullptr;
    
    // Fonts
    HFONT font_title_ = nullptr;
    HFONT font_body_ = nullptr;
    HFONT font_small_ = nullptr;
    HFONT font_mono_ = nullptr;
    
    // Background brushes for dark theme
    HBRUSH bg_primary_brush_ = nullptr;
    HBRUSH bg_control_brush_ = nullptr;
    
    // Child window handles
    HWND hwndConnectGroup_ = nullptr;
    HWND hwndIpEdit_ = nullptr;
    HWND hwndPortEdit_ = nullptr;
    HWND hwndConnectBtn_ = nullptr;
    HWND hwndDiscoverBtn_ = nullptr;
    HWND hwndStatus_ = nullptr;
    HWND hwndProgress_ = nullptr;
    HWND hwndChannelList_ = nullptr;
    HWND hwndRemotePanel_ = nullptr;
    HWND hwndLog_ = nullptr;
    
    // STB Client
    std::unique_ptr<stb::STBClient> client_;
    
    // Background loading thread
    std::thread loading_thread_;
    std::atomic<bool> loading_cancelled_{false};
    
    // Window procedure
    static LRESULT CALLBACK wndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);
    LRESULT handleMessage(UINT msg, WPARAM wParam, LPARAM lParam);
    
    // UI creation
    void createFonts();
    void createControls();
    void layoutControls();
    void applyModernTheme();
    
    // Event handlers
    void onConnect();
    void onDisconnect();
    void onDiscover();
    void onChannelSelect(int index);
    void onRemoteKey(int key);
    void onRefreshChannels();
    void onConnectionStateChanged(stb::ConnectionState state, const std::string& info);
    void onNotification(const std::string& event, const std::string& data);
    void log(const std::string& message);
    
    // Thread-safe UI updates (called from any thread)
    void postLogMessage(const std::string& message);
    void postUpdateChannelList(int count, bool complete);
    void postUpdateProgress(int percent);
    
    // Background loading
    void loadChannelsAsync(bool force_refresh);
    void stopLoading();
    
    // Updates (called from main thread only)
    void updateStatus(const std::string& status);
    void updateChannelList();
    void updateProgress(int percent);
    void updateTitle();
    void appendLog(const std::wstring& message);
    
    // Constants
    static constexpr int WINDOW_WIDTH = 1100;
    static constexpr int WINDOW_HEIGHT = 750;
};

} // namespace gui
