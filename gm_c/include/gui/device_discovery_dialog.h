#pragma once

#include <windows.h>
#include <string>
#include <vector>
#include <functional>
#include "stb/network.h"

namespace gui {

/**
 * @brief Device discovery dialog
 */
class DeviceDiscoveryDialog {
public:
    DeviceDiscoveryDialog();
    ~DeviceDiscoveryDialog();
    
    INT_PTR show(HWND parent);
    
    void setOnConnect(std::function<void(const std::string& ip, int port)> callback);
    
private:
    HWND hwnd_ = nullptr;
    HWND parent_ = nullptr;
    HWND hwndList_ = nullptr;
    std::vector<stb::network::UdpDiscovery::DiscoveredDevice> devices_;
    std::function<void(const std::string& ip, int port)> on_connect_;
    
    static INT_PTR CALLBACK dialogProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);
    INT_PTR handleMessage(UINT msg, WPARAM wParam, LPARAM lParam);
    
    void onDiscover();
    void onConnect();
    void updateDeviceList();
    void addDeviceToList(const stb::network::UdpDiscovery::DiscoveredDevice& device);
};

} // namespace gui
