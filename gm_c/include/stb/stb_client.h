#pragma once

#include <string>
#include <vector>
#include <memory>
#include <functional>
#include <atomic>
#include <thread>
#include <future>

#include "stb/models.h"
#include "stb/network.h"
#include "stb/message_processor.h"
#include "stb/favorites_manager.h"

namespace stb {

/**
 * @brief Main STB client class - high-level interface
 * 
 * This class provides the main interface for communicating with MediaStar STB devices.
 * It handles connection management, automatic reconnection, channel operations,
 * favorites management, and event notifications.
 */
class STBClient {
public:
    // Connection callbacks
    using ConnectionCallback = std::function<void(ConnectionState state, const std::string& info)>;
    using ChannelListCallback = std::function<void(int count, int total, bool complete)>;
    using NotificationCallback = std::function<void(const std::string& event, const std::string& data)>;
    
    /**
     * @brief Constructor
     */
    STBClient();
    
    /**
     * @brief Destructor - automatically disconnects
     */
    ~STBClient();
    
    // Disable copy, enable move
    STBClient(const STBClient&) = delete;
    STBClient& operator=(const STBClient&) = delete;
    STBClient(STBClient&&) noexcept;
    STBClient& operator=(STBClient&&) noexcept;
    
    // ==================== Connection Management ====================
    
    /**
     * @brief Connect and login to STB
     * @param ip STB IP address
     * @param port STB port (default 20000)
     * @param timeout_ms Connection timeout
     * @return true on successful login
     */
    bool connect(const std::string& ip, int port = 20000, int timeout_ms = 4000);
    
    /**
     * @brief Disconnect from STB
     */
    void disconnect();
    
    /**
     * @brief Check if connected and authenticated
     */
    bool isConnected() const;
    
    /**
     * @brief Check if connection state is valid
     */
    ConnectionState getConnectionState() const { return state_.load(); }
    
    /**
     * @brief Set auto-reconnect enabled/disabled
     */
    void setAutoReconnect(bool enabled);
    
    /**
     * @brief Get auto-reconnect status
     */
    bool isAutoReconnectEnabled() const { return auto_reconnect_.load(); }
    
    // ==================== Callbacks ====================
    
    /**
     * @brief Set connection state change callback
     */
    void setConnectionCallback(ConnectionCallback callback);
    
    /**
     * @brief Set channel list loading progress callback
     */
    void setChannelListCallback(ChannelListCallback callback);
    
    /**
     * @brief Set general notification callback
     */
    void setNotificationCallback(NotificationCallback callback);
    
    // ==================== Remote Control ====================
    
    /**
     * @brief Send remote control key
     * @param key_value Key code (see rcu_keys.h)
     * @return true on success
     */
    bool sendRemoteKey(int key_value);
    
    /**
     * @brief Send text input (for keyboard)
     * @param text Text to send
     * @param force Force send even on restricted platforms
     * @return true on success
     */
    bool sendText(const std::string& text, bool force = false);
    
    /**
     * @brief Send keyboard key code
     * @param key_code ASCII/Unicode key code
     * @param force Force send
     * @return true on success
     */
    bool sendKeyboardCode(int key_code, bool force = false);
    
    /**
     * @brief Send keyboard enter key
     * @return true on success
     */
    bool sendKeyboardEnter();
    
    /**
     * @brief Send keyboard backspace
     * @return true on success
     */
    bool sendKeyboardBackspace();
    
    /**
     * @brief Dismiss input method/keyboard
     * @return true on success
     */
    bool dismissInputMethod();
    
    // ==================== Channel Operations ====================
    
    /**
     * @brief Request channel list from STB
     * @param force_refresh Force refresh even if cache valid
     * @param cache_hours Cache validity period
     * @return Number of channels received (async via callback)
     */
    int requestChannelList(bool force_refresh = false, double cache_hours = 1.0);
    
    /**
     * @brief Wait for channel list to complete (blocking)
     * @param timeout_ms Maximum wait time
     * @return true if complete
     */
    bool waitForChannelList(int timeout_ms = 60000);
    
    /**
     * @brief Change to a channel by index
     * @param channel_index Channel index to switch to
     * @return true on success
     */
    bool changeChannel(int channel_index);
    
    /**
     * @brief Change to a channel by program ID
     * @param program_id Program ID to switch to
     * @return true on success
     */
    bool changeChannelByProgramId(const std::string& program_id);
    
    /**
     * @brief Request current playing channel info
     * @return true on success
     */
    bool requestCurrentChannel();
    
    /**
     * @brief Request EPG for a channel
     * @param channel_index Channel index
     * @return true on success
     */
    bool requestEpg(int channel_index);
    
    /**
     * @brief Request channel list type
     * @return true on success
     */
    bool requestChannelListType();
    
    /**
     * @brief Set channel list type
     * @param is_fav_list Show favorites only
     * @param list_type List type filter
     * @return true on success
     */
    bool setChannelListType(bool is_fav_list, int list_type);
    
    // ==================== Favorites Management ====================
    
    /**
     * @brief Add current channel to favorites
     * @param channel_index Channel to add
     * @param group_ids Groups to add to (empty = default)
     * @return true on success
     */
    bool addChannelToFavorites(int channel_index, const std::vector<int>& group_ids = {});
    
    /**
     * @brief Remove channel from favorites
     * @param channel_index Channel to remove
     * @param group_ids Groups to remove from (empty = all)
     * @return true on success
     */
    bool removeChannelFromFavorites(int channel_index, const std::vector<int>& group_ids = {});
    
    /**
     * @brief Set favorite mark on STB
     * @param program_ids List of program IDs
     * @param fav_mark 1=add, 0=remove
     * @param group_ids Groups to affect
     * @return true on success
     */
    bool setChannelFavMark(const std::vector<std::string>& program_ids, 
                           int fav_mark,
                           const std::vector<int>& group_ids = {});
    
    /**
     * @brief Request favorite group names
     * @return true on success
     */
    bool requestFavGroupNames();
    
    /**
     * @brief Rename a favorite group on STB
     * @param group_id Group to rename
     * @param new_name New name
     * @return true on success
     */
    bool renameFavGroup(int group_id, const std::string& new_name);
    
    /**
     * @brief Get favorites manager for local operations
     */
    FavoritesManager& favorites() { return favorites_; }
    
    // ==================== System Operations ====================
    
    /**
     * @brief Request STB information
     * @return true on success
     */
    bool requestStbInfo();
    
    /**
     * @brief Request satellite list
     * @return true on success
     */
    bool requestSatelliteList();
    
    /**
     * @brief Send keep-alive/heartbeat
     * @return true on success
     */
    bool sendKeepAlive();
    
    /**
     * @brief Restart the STB
     * @return true on success
     */
    bool restartStb();
    
    /**
     * @brief Power switch (toggle standby)
     * @return true on success
     */
    bool powerSwitch();
    
    /**
     * @brief Switch between TV and Radio
     * @return true on success
     */
    bool tvRadioSwitch();
    
    // ==================== State Access ====================
    
    /**
     * @brief Get current STB state
     */
    const STBState& state() const { return processor_.state(); }
    STBState& state() { return processor_.state(); }
    
    /**
     * @brief Get login info (if connected)
     */
    const GsMobileLoginInfo* loginInfo() const { return login_info_.get(); }
    
    /**
     * @brief Get last error message
     */
    std::string getLastError() const;
    
    // ==================== Static Helpers ====================
    
    /**
     * @brief Discover STB devices on network
     * @param timeout_ms Discovery timeout
     * @return List of discovered devices
     */
    static std::vector<network::UdpDiscovery::DiscoveredDevice> discoverDevices(int timeout_ms = 5000);
    
private:
    // Components
    network::TcpClient tcp_client_;
    network::ReceiveThread receive_thread_;
    MessageProcessor processor_;
    FavoritesManager favorites_;
    
    // State
    std::atomic<ConnectionState> state_{ConnectionState::Disconnected};
    std::string ip_;
    int port_ = 20000;
    std::unique_ptr<GsMobileLoginInfo> login_info_;
    std::string last_error_;
    
    // Reconnection
    std::atomic<bool> auto_reconnect_{true};
    std::atomic<int> reconnect_attempt_{0};
    static constexpr int MAX_RECONNECT_ATTEMPTS = 10;
    static constexpr int RECONNECT_DELAY_MS = 1000;
    std::thread reconnect_thread_;
    std::atomic<bool> reconnecting_{false};
    
    // Callbacks
    ConnectionCallback connection_callback_;
    ChannelListCallback channel_list_callback_;
    NotificationCallback notification_callback_;
    
    // Channel loading
    std::atomic<bool> channel_loading_{false};
    std::atomic<int> channels_expected_{0};
    std::atomic<int> channels_received_{0};
    
    // Private methods
    void onMessageReceived(const protocol::ReceivedMessage& msg);
    void onDisconnected();
    void onStateChanged(const std::string& event, const std::string& data);
    void scheduleReconnect();
    void doReconnect();
    bool performLogin(int timeout_ms);
    bool internalSendCommand(int cmd, const std::vector<std::map<std::string, protocol::ParamValue>>& params);
    Channel* findChannel(int channel_index);
};

} // namespace stb
