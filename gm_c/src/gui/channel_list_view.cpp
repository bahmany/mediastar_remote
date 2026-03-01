#include "gui/channel_list_view.h"
#include <commctrl.h>

namespace gui {

ChannelListView::ChannelListView() = default;

ChannelListView::~ChannelListView() {
    destroy();
}

bool ChannelListView::create(HWND parent, HINSTANCE hInstance, int x, int y, 
                              int width, int height, int id) {
    parent_ = parent;
    
    hwnd_ = CreateWindowExW(
        WS_EX_CLIENTEDGE,
        L"SysListView32",
        L"",
        WS_CHILD | WS_VISIBLE | LVS_REPORT | LVS_SINGLESEL | LVS_SHOWSELALWAYS,
        x, y, width, height,
        parent,
        reinterpret_cast<HMENU>(static_cast<UINT_PTR>(id)),
        hInstance,
        nullptr
    );
    
    if (!hwnd_) {
        return false;
    }
    
    // Set extended styles
    ListView_SetExtendedListViewStyle(hwnd_, LVS_EX_FULLROWSELECT | LVS_EX_GRIDLINES);
    
    // Add columns
    LVCOLUMNW lvc = {};
    lvc.mask = LVCF_TEXT | LVCF_WIDTH;
    
    lvc.pszText = const_cast<LPWSTR>(L"#");
    lvc.cx = 60;
    ListView_InsertColumn(hwnd_, 0, &lvc);
    
    lvc.pszText = const_cast<LPWSTR>(L"Channel Name");
    lvc.cx = width - 180;
    ListView_InsertColumn(hwnd_, 1, &lvc);
    
    lvc.pszText = const_cast<LPWSTR>(L"Fav");
    lvc.cx = 50;
    ListView_InsertColumn(hwnd_, 2, &lvc);
    
    return true;
}

void ChannelListView::destroy() {
    if (hwnd_) {
        DestroyWindow(hwnd_);
        hwnd_ = nullptr;
    }
}

void ChannelListView::clear() {
    if (hwnd_) {
        ListView_DeleteAllItems(hwnd_);
    }
}

void ChannelListView::addChannel(int index, const std::wstring& name, const std::wstring& type) {
    addChannel(index, name, type, false);
}

void ChannelListView::addChannel(int index, const std::wstring& name, 
                                  const std::wstring& type, bool isFavorite) {
    if (!hwnd_) return;
    
    LVITEMW lvi = {};
    lvi.mask = LVIF_TEXT;
    lvi.iItem = ListView_GetItemCount(hwnd_);
    
    std::wstring idx_str = std::to_wstring(index);
    lvi.pszText = idx_str.data();
    ListView_InsertItem(hwnd_, &lvi);
    
    ListView_SetItemText(hwnd_, lvi.iItem, 1, const_cast<LPWSTR>(name.c_str()));
    
    if (isFavorite) {
        ListView_SetItemText(hwnd_, lvi.iItem, 2, const_cast<LPWSTR>(L"*"));
    }
}

int ChannelListView::getSelectedChannel() const {
    if (!hwnd_) return -1;
    
    int selected = ListView_GetNextItem(hwnd_, -1, LVNI_SELECTED);
    if (selected == -1) return -1;
    
    wchar_t buf[32];
    ListView_GetItemText(hwnd_, selected, 0, buf, 32);
    
    try {
        return std::stoi(buf);
    } catch (...) {
        return -1;
    }
}

void ChannelListView::setSelectedChannel(int index) {
    if (!hwnd_) return;
    
    int count = ListView_GetItemCount(hwnd_);
    for (int i = 0; i < count; ++i) {
        LVITEMW lvi = {};
        lvi.mask = LVIF_TEXT;
        lvi.iItem = i;
        lvi.iSubItem = 0;
        wchar_t buf[32];
        lvi.pszText = buf;
        lvi.cchTextMax = 32;
        
        if (ListView_GetItem(hwnd_, &lvi)) {
            try {
                int item_idx = std::stoi(buf);
                if (item_idx == index) {
                    ListView_SetItemState(hwnd_, i, LVIS_SELECTED, LVIS_SELECTED);
                    ListView_EnsureVisible(hwnd_, i, FALSE);
                    break;
                }
            } catch (...) {}
        }
    }
}

void ChannelListView::setOnSelect(std::function<void(int)> callback) {
    on_select_ = callback;
}

void ChannelListView::setOnDoubleClick(std::function<void(int)> callback) {
    on_double_click_ = callback;
}

} // namespace gui
