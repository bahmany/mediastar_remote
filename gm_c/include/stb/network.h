#pragma once

#include <cstdint>
#include <string>
#include <vector>
#include <functional>
#include <memory>
#include <mutex>
#include <thread>
#include <atomic>
#include <condition_variable>
#include <queue>

#include "stb/models.h"
#include "stb/serialization.h"
#include "stb/constants.h"

namespace stb {
namespace network {

/**
 * @brief TCP socket client with blocking I/O
 */
class TcpClient {
public:
    using DataCallback = std::function<void(const std::vector<uint8_t>&)>;
    using ErrorCallback = std::function<void(int, const std::string&)>;
    
    TcpClient();
    ~TcpClient();
    
    // Disable copy, enable move
    TcpClient(const TcpClient&) = delete;
    TcpClient& operator=(const TcpClient&) = delete;
    TcpClient(TcpClient&&) noexcept;
    TcpClient& operator=(TcpClient&&) noexcept;
    
    /**
     * @brief Connect to server
     * @param ip Server IP address
     * @param port Server port
     * @param timeout_ms Connection timeout in milliseconds
     * @return true on success
     */
    bool connect(const std::string& ip, int port, int timeout_ms = 4000);
    
    /**
     * @brief Disconnect from server
     */
    void disconnect();
    
    /**
     * @brief Check if connected
     */
    bool isConnected() const { return connected_.load(); }
    
    /**
     * @brief Send data
     * @param data Data to send
     * @return true on success
     */
    bool send(const std::vector<uint8_t>& data);
    bool send(const std::string& data);
    
    /**
     * @brief Send framed message (Start+len+End+payload)
     */
    bool sendFramed(const std::vector<uint8_t>& payload);
    bool sendFramed(const std::string& payload);
    
    /**
     * @brief Receive data (blocking with timeout)
     * @param buffer Output buffer
     * @param max_length Maximum bytes to receive
     * @param timeout_ms Timeout in milliseconds (-1 = blocking)
     * @return Number of bytes received, 0 on timeout, -1 on error
     */
    int receive(std::vector<uint8_t>& buffer, size_t max_length, int timeout_ms = 4000);
    
    /**
     * @brief Receive exact number of bytes
     * @param buffer Output buffer (will be resized to exact_length)
     * @param exact_length Exact number of bytes to receive
     * @param timeout_ms Timeout per read attempt
     * @return true if exact_length bytes received
     */
    bool receiveExact(std::vector<uint8_t>& buffer, size_t exact_length, int timeout_ms = 4000);
    
    /**
     * @brief Set socket timeout
     */
    void setTimeout(int timeout_ms);
    
    /**
     * @brief Get local error code
     */
    int getLastError() const { return last_error_; }
    
private:
    int socket_fd_ = -1;
    std::atomic<bool> connected_{false};
    int last_error_ = 0;
    mutable std::mutex socket_mutex_;
    
    void closeSocket();
};

/**
 * @brief UDP broadcast discovery client
 */
class UdpDiscovery {
public:
    struct DiscoveredDevice {
        std::string ip;
        std::string model_name;
        std::string serial;
        int platform_id = 0;
        int send_data_type = 0;
        int sw_version = 0;
        int sw_sub_version = 0;
        bool sat_enable = false;
        int sat2ip_enable = 0;
    };
    
    /**
     * @brief Discover STB devices on network
     * @param timeout_ms Discovery timeout
     * @param port Broadcast port (default 25860)
     * @return List of discovered devices
     */
    static std::vector<DiscoveredDevice> discover(
        int timeout_ms = 5000, 
        int port = constants::G_MS_BROADCAST_PORT);
    
private:
    static bool parseBroadcastPacket(const uint8_t* data, size_t length, DiscoveredDevice& device);
};

/**
 * @brief Background receive thread with keep-alive
 */
class ReceiveThread {
public:
    using MessageCallback = std::function<void(const protocol::ReceivedMessage&)>;
    using DisconnectCallback = std::function<void()>;
    
    ReceiveThread();
    ~ReceiveThread();
    
    /**
     * @brief Start receive thread
     * @param tcp_client Associated TCP client (must be connected)
     * @param on_message Message received callback
     * @param on_disconnect Disconnection callback
     * @param use_json Whether to use JSON keep-alive messages
     */
    void start(TcpClient* tcp_client,
               MessageCallback on_message,
               DisconnectCallback on_disconnect,
               bool use_json = false);
    
    /**
     * @brief Stop receive thread
     */
    void stop();
    
    /**
     * @brief Check if running
     */
    bool isRunning() const { return running_.load(); }
    
    /**
     * @brief Update last activity timestamp (for external keep-alive tracking)
     */
    void updateActivity();
    
private:
    std::thread receive_thread_;
    std::thread keepalive_thread_;
    std::atomic<bool> running_{false};
    std::atomic<bool> stop_requested_{false};
    
    TcpClient* tcp_client_ = nullptr;
    MessageCallback on_message_;
    DisconnectCallback on_disconnect_;
    bool use_json_ = false;
    
    std::atomic<uint64_t> last_rx_time_{0};
    std::atomic<uint64_t> last_tx_time_{0};
    std::atomic<bool> keepalive_pending_{false};
    
    std::mutex mutex_;
    
    void receiveLoop();
    void keepAliveLoop();
    uint64_t getCurrentTimeMs() const;
};

} // namespace network
} // namespace stb
