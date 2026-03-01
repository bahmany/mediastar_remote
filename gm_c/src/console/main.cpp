#include <iostream>
#include <string>
#include <thread>
#include <chrono>
#include <sstream>

#include "stb/stb_client.h"
#include "stb/rcu_keys.h"

using namespace stb;

void printHelp() {
    std::cout << "GMScreen Console Client\n"
              << "Usage:\n"
              << "  connect <ip> [port]  - Connect to STB\n"
              << "  disconnect           - Disconnect from STB\n"
              << "  discover             - Discover STB devices\n"
              << "  channels             - Load channel list\n"
              << "  ch <index>           - Change to channel\n"
              << "  key <keycode>        - Send remote key\n"
              << "  text <text>          - Send text input\n"
              << "  info                 - Show STB info\n"
              << "  status               - Show connection status\n"
              << "  fav add <index>      - Add channel to favorites\n"
              << "  fav remove <index>   - Remove channel from favorites\n"
              << "  fav list             - List favorites\n"
              << "  power                - Power switch\n"
              << "  restart              - Restart STB\n"
              << "  quit                 - Exit\n"
              << "  help                 - Show this help\n";
}

void printStatus(STBClient& client) {
    auto state = client.getConnectionState();
    std::string state_str;
    switch (state) {
        case ConnectionState::Disconnected: state_str = "Disconnected"; break;
        case ConnectionState::Connecting: state_str = "Connecting"; break;
        case ConnectionState::Connected: state_str = "Connected"; break;
        case ConnectionState::Authenticated: state_str = "Authenticated"; break;
        case ConnectionState::Reconnecting: state_str = "Reconnecting"; break;
        case ConnectionState::ConnectionFailed: state_str = "Connection Failed"; break;
    }
    
    std::cout << "Status: " << state_str << "\n";
    
    if (auto login = client.loginInfo()) {
        std::cout << "Model: " << login->modelName() << "\n";
        std::cout << "Serial: " << login->stbSnDisp() << "\n";
        std::cout << "IP: " << login->stbIpAddressDisp() << "\n";
        std::cout << "Data Type: " << (login->usesJson() ? "JSON" : "XML") << "\n";
    }
    
    const auto& stb_state = client.state();
    if (!stb_state.channels.empty()) {
        std::cout << "Channels: " << stb_state.channels.size() << "\n";
    }
}

int main(int argc, char* argv[]) {
    STBClient client;
    
    client.setConnectionCallback([](ConnectionState state, const std::string& info) {
        std::cout << "[Connection] " << info << "\n";
    });
    
    client.setChannelListCallback([](int count, int total, bool complete) {
        std::cout << "[Channels] " << count << "/" << total;
        if (complete) std::cout << " (Complete)";
        std::cout << "\r" << std::flush;
        if (complete) std::cout << "\n";
    });
    
    client.setNotificationCallback([](const std::string& event, const std::string& data) {
        std::cout << "[Event] " << event;
        if (!data.empty()) std::cout << ": " << data;
        std::cout << "\n";
    });
    
    std::cout << "GMScreen Console Client\n";
    std::cout << "Type 'help' for commands\n\n";
    
    std::string line;
    while (true) {
        std::cout << "> ";
        std::getline(std::cin, line);
        
        if (line.empty()) continue;
        
        std::istringstream iss(line);
        std::string cmd;
        iss >> cmd;
        
        if (cmd == "quit" || cmd == "exit") {
            break;
        } else if (cmd == "help") {
            printHelp();
        } else if (cmd == "connect") {
            std::string ip;
            int port = 20000;
            iss >> ip >> port;
            if (ip.empty()) {
                std::cout << "Usage: connect <ip> [port]\n";
                continue;
            }
            std::cout << "Connecting to " << ip << ":" << port << "...\n";
            if (client.connect(ip, port)) {
                std::cout << "Connected successfully!\n";
            } else {
                std::cout << "Connection failed: " << client.getLastError() << "\n";
            }
        } else if (cmd == "disconnect") {
            client.disconnect();
            std::cout << "Disconnected\n";
        } else if (cmd == "discover") {
            std::cout << "Discovering devices...\n";
            auto devices = STBClient::discoverDevices(5000);
            if (devices.empty()) {
                std::cout << "No devices found\n";
            } else {
                for (const auto& dev : devices) {
                    std::cout << "  " << dev.ip << " - " << dev.model_name 
                              << " (SN: " << dev.serial << ")\n";
                }
            }
        } else if (cmd == "channels") {
            if (!client.isConnected()) {
                std::cout << "Not connected\n";
                continue;
            }
            std::cout << "Loading channels...\n";
            int count = client.requestChannelList(true);
            std::cout << "Loaded " << count << " channels\n";
        } else if (cmd == "ch") {
            int index;
            iss >> index;
            if (!client.isConnected()) {
                std::cout << "Not connected\n";
                continue;
            }
            if (client.changeChannel(index)) {
                std::cout << "Changed to channel " << index << "\n";
            } else {
                std::cout << "Failed to change channel\n";
            }
        } else if (cmd == "key") {
            int key;
            iss >> key;
            if (!client.isConnected()) {
                std::cout << "Not connected\n";
                continue;
            }
            if (client.sendRemoteKey(key)) {
                std::cout << "Sent key " << key << "\n";
            } else {
                std::cout << "Failed to send key\n";
            }
        } else if (cmd == "text") {
            std::string text;
            std::getline(iss, text);
            if (!text.empty() && text[0] == ' ') text = text.substr(1);
            if (!client.isConnected()) {
                std::cout << "Not connected\n";
                continue;
            }
            if (client.sendText(text)) {
                std::cout << "Sent text\n";
            } else {
                std::cout << "Failed to send text\n";
            }
        } else if (cmd == "info") {
            if (!client.isConnected()) {
                std::cout << "Not connected\n";
                continue;
            }
            client.requestStbInfo();
            std::this_thread::sleep_for(std::chrono::milliseconds(500));
            printStatus(client);
        } else if (cmd == "status") {
            printStatus(client);
        } else if (cmd == "fav") {
            std::string subcmd;
            iss >> subcmd;
            if (subcmd == "list") {
                auto& favs = client.favorites();
                auto summary = favs.getSummary();
                std::cout << "Favorites: " << summary.total_favorites << " channels in " 
                          << summary.total_groups << " groups\n";
                for (const auto& [group, channels] : summary.groups) {
                    std::cout << "  Group " << group.group_id << ": " << group.group_name 
                              << " (" << channels.size() << " channels)\n";
                }
            } else {
                int index;
                iss >> index;
                if (!client.isConnected()) {
                    std::cout << "Not connected\n";
                    continue;
                }
                if (subcmd == "add") {
                    if (client.addChannelToFavorites(index)) {
                        std::cout << "Added channel " << index << " to favorites\n";
                    } else {
                        std::cout << "Failed to add favorite\n";
                    }
                } else if (subcmd == "remove") {
                    if (client.removeChannelFromFavorites(index)) {
                        std::cout << "Removed channel " << index << " from favorites\n";
                    } else {
                        std::cout << "Failed to remove favorite\n";
                    }
                } else {
                    std::cout << "Unknown fav command\n";
                }
            }
        } else if (cmd == "power") {
            if (!client.isConnected()) {
                std::cout << "Not connected\n";
                continue;
            }
            if (client.powerSwitch()) {
                std::cout << "Power switch sent\n";
            } else {
                std::cout << "Failed to send power switch\n";
            }
        } else if (cmd == "restart") {
            if (!client.isConnected()) {
                std::cout << "Not connected\n";
                continue;
            }
            if (client.restartStb()) {
                std::cout << "Restart command sent\n";
            } else {
                std::cout << "Failed to send restart\n";
            }
        } else {
            std::cout << "Unknown command: " << cmd << "\n";
        }
    }
    
    client.disconnect();
    return 0;
}
