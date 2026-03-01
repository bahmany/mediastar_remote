#pragma once

#include <windows.h>
#include <functional>
#include <map>

namespace gui {

/**
 * @brief Virtual remote control panel
 */
class RemoteControl {
public:
    RemoteControl();
    ~RemoteControl();
    
    bool create(HWND parent, HINSTANCE hInstance, int x, int y, int width, int height);
    void destroy();
    
    void setOnKeyPress(std::function<void(int)> callback);
    void enable(bool enabled);
    
    HWND hwnd() const { return hwnd_; }
    
private:
    HWND hwnd_ = nullptr;
    HWND parent_ = nullptr;
    HINSTANCE hInstance_ = nullptr;
    std::function<void(int)> on_key_press_;
    std::map<int, HWND> buttons_;
    
    void createButtons(int x, int y, int width, int height);
    void onButtonClick(int key);
    
    static LRESULT CALLBACK wndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);
    LRESULT handleMessage(UINT msg, WPARAM wParam, LPARAM lParam);
};

} // namespace gui
