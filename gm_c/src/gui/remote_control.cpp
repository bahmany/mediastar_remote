#include "gui/remote_control.h"
#include "stb/rcu_keys.h"
#include <map>

namespace gui {

struct ButtonDef {
    const wchar_t* label;
    int keyCode;
    int x, y, width, height;
};

RemoteControl::RemoteControl() = default;

RemoteControl::~RemoteControl() {
    destroy();
}

bool RemoteControl::create(HWND parent, HINSTANCE hInstance, int x, int y, 
                          int width, int height) {
    parent_ = parent;
    hInstance_ = hInstance;
    
    // Create container group box
    hwnd_ = CreateWindow(
        L"BUTTON",
        L"Remote Control",
        WS_CHILD | WS_VISIBLE | BS_GROUPBOX,
        x, y, width, height,
        parent,
        nullptr,
        hInstance,
        nullptr
    );
    
    if (!hwnd_) {
        return false;
    }
    
    createButtons(x + 10, y + 20, width - 20, height - 30);
    
    return true;
}

void RemoteControl::destroy() {
    buttons_.clear();
    if (hwnd_) {
        DestroyWindow(hwnd_);
        hwnd_ = nullptr;
    }
}

void RemoteControl::createButtons(int x, int y, int width, int height) {
    // Define button layout — matches APK GsRemoteControlActivity buttons
    ButtonDef buttons[] = {
        // Number pad
        {L"1", stb::keys::KEY_1, 0, 0, 40, 35},
        {L"2", stb::keys::KEY_2, 45, 0, 40, 35},
        {L"3", stb::keys::KEY_3, 90, 0, 40, 35},
        {L"4", stb::keys::KEY_4, 0, 40, 40, 35},
        {L"5", stb::keys::KEY_5, 45, 40, 40, 35},
        {L"6", stb::keys::KEY_6, 90, 40, 40, 35},
        {L"7", stb::keys::KEY_7, 0, 80, 40, 35},
        {L"8", stb::keys::KEY_8, 45, 80, 40, 35},
        {L"9", stb::keys::KEY_9, 90, 80, 40, 35},
        {L"0", stb::keys::KEY_0, 45, 120, 40, 35},
        
        // CH / VOL / PgUp / PgDn
        {L"CH+",  stb::keys::KEY_CH_UP,     150, 0, 50, 30},
        {L"CH-",  stb::keys::KEY_CH_DOWN,   150, 35, 50, 30},
        {L"VOL+", stb::keys::KEY_VOL_UP,    205, 0, 50, 30},
        {L"VOL-", stb::keys::KEY_VOL_DOWN,  205, 35, 50, 30},
        {L"PgUp", stb::keys::KEY_PAGE_UP,   150, 65, 50, 25},
        {L"PgDn", stb::keys::KEY_PAGE_DOWN, 205, 65, 50, 25},
        
        // D-pad
        {L"\x25B2", stb::keys::KEY_UP,    290, 0, 40, 30},
        {L"\x25C0", stb::keys::KEY_LEFT,  260, 32, 40, 30},
        {L"OK",     stb::keys::KEY_ENTER, 305, 32, 40, 30},
        {L"\x25B6", stb::keys::KEY_RIGHT, 350, 32, 40, 30},
        {L"\x25BC", stb::keys::KEY_DOWN,  290, 64, 40, 30},
        {L"BACK",   stb::keys::KEY_BACK,  260, 0, 40, 30},
        
        // Menu buttons
        {L"MENU", stb::keys::KEY_MENU, 150, 80, 55, 30},
        {L"EPG",  stb::keys::KEY_EPG,  210, 80, 50, 30},
        {L"INFO", stb::keys::KEY_INFO, 265, 80, 50, 30},
        {L"Recall", stb::keys::KEY_RECALL, 320, 80, 60, 30},
        
        // Extra function buttons
        {L"SUB",  stb::keys::KEY_SUBTITLE, 150, 115, 50, 30},
        {L"TTX",  stb::keys::KEY_TTX,      205, 115, 50, 30},
        {L"USB",  stb::keys::KEY_USB,       260, 115, 50, 30},
        
        // Color buttons
        {L"RED",    stb::keys::KEY_RED,    0, 170, 50, 30},
        {L"GREEN",  stb::keys::KEY_GREEN,  55, 170, 50, 30},
        {L"YELLOW", stb::keys::KEY_YELLOW, 110, 170, 50, 30},
        {L"BLUE",   stb::keys::KEY_BLUE,   165, 170, 50, 30},
        
        // Power / Mute / TV-Radio
        {L"POWER", stb::keys::KEY_POWER,    250, 120, 60, 30},
        {L"TV/R",  stb::keys::KEY_TV_RADIO, 250, 155, 60, 30},
        {L"MUTE",  stb::keys::KEY_MUTE,     315, 155, 50, 30},
        
        // Playback controls
        {L"|<",     stb::keys::KEY_PREVIOUS,     0,   210, 40, 28},
        {L"<<",     stb::keys::KEY_REWIND,       45,  210, 40, 28},
        {L"\x25B6", stb::keys::KEY_PLAY_PAUSE,   90,  210, 40, 28},
        {L"||",     stb::keys::KEY_PAUSE,        135, 210, 40, 28},
        {L"\x25A0", stb::keys::KEY_STOP,         180, 210, 40, 28},
        {L">>",     stb::keys::KEY_FAST_FORWARD, 225, 210, 40, 28},
        {L">|",     stb::keys::KEY_NEXT,         270, 210, 40, 28},
        {L"\x25CF", stb::keys::KEY_RECORD,       315, 210, 50, 28},
    };
    
    const int baseX = x;
    const int baseY = y;
    
    for (const auto& btn : buttons) {
        HWND hBtn = CreateWindow(
            L"BUTTON",
            btn.label,
            WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            baseX + btn.x,
            baseY + btn.y,
            btn.width,
            btn.height,
            parent_,
            reinterpret_cast<HMENU>(static_cast<UINT_PTR>(1000 + btn.keyCode)),
            hInstance_,
            nullptr
        );
        
        if (hBtn) {
            buttons_[btn.keyCode] = hBtn;
        }
    }
}

void RemoteControl::setOnKeyPress(std::function<void(int)> callback) {
    on_key_press_ = callback;
}

void RemoteControl::enable(bool enabled) {
    for (auto& [key, hwnd] : buttons_) {
        EnableWindow(hwnd, enabled ? TRUE : FALSE);
    }
}

void RemoteControl::onButtonClick(int key) {
    if (on_key_press_) {
        on_key_press_(key);
    }
}

} // namespace gui
