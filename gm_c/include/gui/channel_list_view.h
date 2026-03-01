#pragma once

#include <windows.h>
#include <string>
#include <vector>
#include <functional>

namespace gui {

/**
 * @brief Channel list view control wrapper
 */
class ChannelListView {
public:
    ChannelListView();
    ~ChannelListView();
    
    bool create(HWND parent, HINSTANCE hInstance, int x, int y, int width, int height, int id);
    void destroy();
    
    void clear();
    void addChannel(int index, const std::wstring& name, const std::wstring& type);
    void addChannel(int index, const std::wstring& name, const std::wstring& type, bool isFavorite);
    int getSelectedChannel() const;
    void setSelectedChannel(int index);
    
    void setOnSelect(std::function<void(int)> callback);
    void setOnDoubleClick(std::function<void(int)> callback);
    
    HWND hwnd() const { return hwnd_; }
    
private:
    HWND hwnd_ = nullptr;
    HWND parent_ = nullptr;
    std::function<void(int)> on_select_;
    std::function<void(int)> on_double_click_;
    
    static LRESULT CALLBACK subclassProc(HWND hwnd, UINT msg, WPARAM wParam, 
                                         LPARAM lParam, UINT_PTR subclassId, 
                                         DWORD_PTR refData);
};

} // namespace gui
