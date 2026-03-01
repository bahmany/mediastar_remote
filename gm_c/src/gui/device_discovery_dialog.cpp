#include "gui/device_discovery_dialog.h"
#include <commctrl.h>
#include <sstream>

namespace gui {

// Dialog resource IDs
constexpr int IDC_DEVICE_LIST = 1001;
constexpr int IDC_REFRESH_BTN = 1002;
constexpr int IDC_CONNECT_BTN = 1003;
constexpr int IDC_CANCEL_BTN = 1004;

DeviceDiscoveryDialog::DeviceDiscoveryDialog() = default;

DeviceDiscoveryDialog::~DeviceDiscoveryDialog() = default;

INT_PTR DeviceDiscoveryDialog::show(HWND parent) {
    parent_ = parent;
    
    // Create dialog template dynamically
    // In a real application, you'd use a resource file
    
    // For now, create a simple message box style dialog
    // with device list
    
    onDiscover();
    
    if (devices_.empty()) {
        MessageBoxW(parent, L"No devices found.\nMake sure your STB is on the same network.",
                   L"Device Discovery", MB_OK | MB_ICONINFORMATION);
        return IDCANCEL;
    }
    
    // Build device list string
    std::wstring list;
    for (size_t i = 0; i < devices_.size(); ++i) {
        const auto& dev = devices_[i];
        list += std::to_wstring(i + 1) + L". ";
        list += std::wstring(dev.ip.begin(), dev.ip.end()) + L" - ";
        list += std::wstring(dev.model_name.begin(), dev.model_name.end()) + L"\n";
        list += L"    Serial: " + std::wstring(dev.serial.begin(), dev.serial.end()) + L"\n\n";
    }
    
    list += L"\nClick OK to connect to the first device, or Cancel to enter manually.";
    
    int result = MessageBoxW(parent, list.c_str(), L"Discovered Devices", 
                            MB_OKCANCEL | MB_ICONINFORMATION);
    
    if (result == IDOK && !devices_.empty() && on_connect_) {
        on_connect_(devices_[0].ip, 20000);
    }
    
    return result;
}

void DeviceDiscoveryDialog::setOnConnect(std::function<void(const std::string& ip, int port)> callback) {
    on_connect_ = callback;
}

void DeviceDiscoveryDialog::onDiscover() {
    devices_.clear();
    devices_ = stb::network::UdpDiscovery::discover(5000);
}

void DeviceDiscoveryDialog::onConnect() {
    // Get selected device
    if (hwndList_ && on_connect_) {
        int selected = SendMessage(hwndList_, LB_GETCURSEL, 0, 0);
        if (selected >= 0 && selected < static_cast<int>(devices_.size())) {
            on_connect_(devices_[selected].ip, 20000);
            EndDialog(hwnd_, IDOK);
        }
    }
}

void DeviceDiscoveryDialog::updateDeviceList() {
    if (!hwndList_) return;
    
    SendMessage(hwndList_, LB_RESETCONTENT, 0, 0);
    
    for (const auto& dev : devices_) {
        std::wstring entry;
        entry += std::wstring(dev.ip.begin(), dev.ip.end()) + L" - ";
        entry += std::wstring(dev.model_name.begin(), dev.model_name.end());
        SendMessage(hwndList_, LB_ADDSTRING, 0, reinterpret_cast<LPARAM>(entry.c_str()));
    }
}

void DeviceDiscoveryDialog::addDeviceToList(const stb::network::UdpDiscovery::DiscoveredDevice& device) {
    devices_.push_back(device);
    
    if (hwndList_) {
        std::wstring entry;
        entry += std::wstring(device.ip.begin(), device.ip.end()) + L" - ";
        entry += std::wstring(device.model_name.begin(), device.model_name.end());
        SendMessage(hwndList_, LB_ADDSTRING, 0, reinterpret_cast<LPARAM>(entry.c_str()));
    }
}

} // namespace gui
