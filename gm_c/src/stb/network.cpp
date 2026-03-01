#include "stb/network.h"
#include "stb/constants.h"
#include "stb/compression.h"
#include "stb/serialization.h"
#include "stb/crash_log.h"

#ifdef _WIN32
#include <winsock2.h>
#include <ws2tcpip.h>
#include <windows.h>
#pragma comment(lib, "ws2_32.lib")
#else
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <fcntl.h>
#include <poll.h>
#endif

#include <cstring>
#include <chrono>
#include <algorithm>

namespace stb {
namespace network {

// ============================================================================
// TcpClient Implementation
// ============================================================================

TcpClient::TcpClient() {
#ifdef _WIN32
    static bool ws_initialized = false;
    if (!ws_initialized) {
        WSADATA wsaData;
        WSAStartup(MAKEWORD(2, 2), &wsaData);
        ws_initialized = true;
    }
#endif
}

TcpClient::~TcpClient() {
    disconnect();
}

TcpClient::TcpClient(TcpClient&& other) noexcept 
    : socket_fd_(other.socket_fd_), 
      connected_(other.connected_.load()),
      last_error_(other.last_error_) {
    other.socket_fd_ = -1;
    other.connected_ = false;
    other.last_error_ = 0;
}

TcpClient& TcpClient::operator=(TcpClient&& other) noexcept {
    if (this != &other) {
        disconnect();
        socket_fd_ = other.socket_fd_;
        connected_ = other.connected_.load();
        last_error_ = other.last_error_;
        other.socket_fd_ = -1;
        other.connected_ = false;
        other.last_error_ = 0;
    }
    return *this;
}

bool TcpClient::connect(const std::string& ip, int port, int timeout_ms) {
    std::lock_guard<std::mutex> lock(socket_mutex_);
    
    if (connected_) {
        closeSocket();
    }
    
    // Create socket
    socket_fd_ = socket(AF_INET, SOCK_STREAM, 0);
    if (socket_fd_ < 0) {
        last_error_ = -1;
        return false;
    }
    
    // Set non-blocking for timeout support
#ifdef _WIN32
    u_long mode = 1;
    ioctlsocket(socket_fd_, FIONBIO, &mode);
#else
    int flags = fcntl(socket_fd_, F_GETFL, 0);
    fcntl(socket_fd_, F_SETFL, flags | O_NONBLOCK);
#endif
    
    // Connect
    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(static_cast<uint16_t>(port));
    int pton_result = inet_pton(AF_INET, ip.c_str(), &addr.sin_addr);
    if (pton_result != 1) {
        closeSocket();
        return false;
    }
    
    int result = ::connect(socket_fd_, reinterpret_cast<sockaddr*>(&addr), sizeof(addr));
    
    if (result < 0) {
#ifdef _WIN32
        int wsa_error = WSAGetLastError();
        if (wsa_error != WSAEWOULDBLOCK) {
            closeSocket();
            return false;
        }
#else
        if (errno != EINPROGRESS) {
            closeSocket();
            return false;
        }
#endif
        
        // Wait for connection with timeout
        fd_set fdset;
        FD_ZERO(&fdset);
        FD_SET(socket_fd_, &fdset);
        
        timeval tv;
        tv.tv_sec = timeout_ms / 1000;
        tv.tv_usec = (timeout_ms % 1000) * 1000;
        
        result = select(socket_fd_ + 1, nullptr, &fdset, nullptr, &tv);
        
        if (result <= 0) {
            closeSocket();
            return false;
        }
        
        // Check connection success
        int so_error;
        socklen_t len = sizeof(so_error);
        getsockopt(socket_fd_, SOL_SOCKET, SO_ERROR, reinterpret_cast<char*>(&so_error), &len);
        
        if (so_error != 0) {
            closeSocket();
            return false;
        }
    }
    
    // Set back to blocking mode
#ifdef _WIN32
    mode = 0;
    ioctlsocket(socket_fd_, FIONBIO, &mode);
#else
    flags = fcntl(socket_fd_, F_GETFL, 0);
    fcntl(socket_fd_, F_SETFL, flags & ~O_NONBLOCK);
#endif
    
    // Set socket options
    int keepalive = 1;
    setsockopt(socket_fd_, SOL_SOCKET, SO_KEEPALIVE, reinterpret_cast<char*>(&keepalive), sizeof(keepalive));
    
    // Set TCP_NODELAY for better responsiveness
    int nodelay = 1;
    setsockopt(socket_fd_, IPPROTO_TCP, TCP_NODELAY, reinterpret_cast<char*>(&nodelay), sizeof(nodelay));
    
    connected_ = true;
    last_error_ = 0;
    return true;
}

void TcpClient::disconnect() {
    std::lock_guard<std::mutex> lock(socket_mutex_);
    closeSocket();
}

void TcpClient::closeSocket() {
    if (socket_fd_ >= 0) {
#ifdef _WIN32
        closesocket(socket_fd_);
#else
        ::close(socket_fd_);
#endif
        socket_fd_ = -1;
    }
    connected_ = false;
}

bool TcpClient::send(const std::vector<uint8_t>& data) {
    if (!connected_ || socket_fd_ < 0) {
        return false;
    }
    
    std::lock_guard<std::mutex> lock(socket_mutex_);
    
    size_t total_sent = 0;
    while (total_sent < data.size()) {
        int sent = ::send(socket_fd_, reinterpret_cast<const char*>(data.data() + total_sent), 
                          static_cast<int>(data.size() - total_sent), 0);
        if (sent <= 0) {
            closeSocket();
            return false;
        }
        total_sent += sent;
    }
    
    return true;
}

bool TcpClient::send(const std::string& data) {
    return send(std::vector<uint8_t>(data.begin(), data.end()));
}

bool TcpClient::sendFramed(const std::vector<uint8_t>& payload) {
    auto frame = protocol::buildSocketFrame(payload);
    return send(frame);
}

bool TcpClient::sendFramed(const std::string& payload) {
    return sendFramed(std::vector<uint8_t>(payload.begin(), payload.end()));
}

int TcpClient::receive(std::vector<uint8_t>& buffer, size_t max_length, int timeout_ms) {
    if (!connected_ || socket_fd_ < 0) {
        return -1;
    }
    
    buffer.resize(max_length);
    
    // Set timeout
    setTimeout(timeout_ms);
    
    int received = recv(socket_fd_, reinterpret_cast<char*>(buffer.data()), 
                        static_cast<int>(max_length), 0);
    
    if (received <= 0) {
        if (received == 0) {
            // Peer closed connection (orderly shutdown)
            closeSocket();
            return -1;
        }
#ifdef _WIN32
        if (WSAGetLastError() == WSAETIMEDOUT) {
            return 0;  // Timeout — no data yet
        }
#else
        if (errno == EAGAIN || errno == EWOULDBLOCK) {
            return 0;  // Timeout — no data yet
        }
#endif
        closeSocket();
        return -1;  // Error
    }
    
    buffer.resize(received);
    return received;
}

bool TcpClient::receiveExact(std::vector<uint8_t>& buffer, size_t exact_length, int timeout_ms) {
    buffer.resize(0);
    buffer.reserve(exact_length);
    
    size_t total_received = 0;
    auto start_time = std::chrono::steady_clock::now();
    
    while (total_received < exact_length) {
        auto elapsed = std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::steady_clock::now() - start_time).count();
        
        if (elapsed > timeout_ms) {
            return false;  // Timeout
        }
        
        std::vector<uint8_t> chunk;
        int received = receive(chunk, exact_length - total_received, 100);
        
        if (received < 0) {
            return false;  // Error
        }
        
        if (received > 0) {
            buffer.insert(buffer.end(), chunk.begin(), chunk.end());
            total_received += received;
        }
    }
    
    return total_received == exact_length;
}

void TcpClient::setTimeout(int timeout_ms) {
    if (socket_fd_ < 0) return;
    
    timeval tv;
    tv.tv_sec = timeout_ms / 1000;
    tv.tv_usec = (timeout_ms % 1000) * 1000;
    
    setsockopt(socket_fd_, SOL_SOCKET, SO_RCVTIMEO, reinterpret_cast<char*>(&tv), sizeof(tv));
}

// ============================================================================
// UdpDiscovery Implementation
// ============================================================================

std::vector<UdpDiscovery::DiscoveredDevice> UdpDiscovery::discover(int timeout_ms, int port) {
    std::vector<DiscoveredDevice> devices;

#ifdef _WIN32
    SOCKET sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    if (sock == INVALID_SOCKET) return devices;
#else
    int sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    if (sock < 0) return devices;
#endif

    // Allow address reuse so multiple apps can listen on same port
    int reuse = 1;
    setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, reinterpret_cast<char*>(&reuse), sizeof(reuse));

    // Enable broadcast receive
    int broadcast = 1;
    setsockopt(sock, SOL_SOCKET, SO_BROADCAST, reinterpret_cast<char*>(&broadcast), sizeof(broadcast));

    // Bind to the broadcast port (listen for STB announcements)
    sockaddr_in addr{};
    addr.sin_family      = AF_INET;
    addr.sin_port        = htons(static_cast<uint16_t>(port));
    addr.sin_addr.s_addr = INADDR_ANY;

    if (bind(sock, reinterpret_cast<sockaddr*>(&addr), sizeof(addr)) < 0) {
#ifdef _WIN32
        closesocket(sock);
#else
        close(sock);
#endif
        return devices;
    }

    // Set receive timeout to 500ms so we can check the clock between polls
    timeval tv;
    tv.tv_sec  = 0;
    tv.tv_usec = 500000;
    setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, reinterpret_cast<char*>(&tv), sizeof(tv));

    auto end_time = std::chrono::steady_clock::now() + std::chrono::milliseconds(timeout_ms);

    while (std::chrono::steady_clock::now() < end_time) {
        uint8_t buffer[2048];
        sockaddr_in from_addr{};
#ifdef _WIN32
        int from_len = sizeof(from_addr);
        int received = recvfrom(sock, reinterpret_cast<char*>(buffer), (int)sizeof(buffer), 0,
                                reinterpret_cast<sockaddr*>(&from_addr), &from_len);
#else
        socklen_t from_len = sizeof(from_addr);
        int received = recvfrom(sock, reinterpret_cast<char*>(buffer), sizeof(buffer), 0,
                                reinterpret_cast<sockaddr*>(&from_addr), &from_len);
#endif
        if (received == static_cast<int>(constants::STB_LOGIN_INFO_DATA_LENGTH)) {
            DiscoveredDevice device;
            if (parseBroadcastPacket(buffer, received, device)) {
                // If IP is empty or all-zero, use the sender's address
                if (device.ip.empty() || device.ip == "0.0.0.0") {
                    char ip_buf[INET_ADDRSTRLEN] = {};
                    inet_ntop(AF_INET, &from_addr.sin_addr, ip_buf, sizeof(ip_buf));
                    device.ip = ip_buf;
                }
                // Deduplicate by serial; fall back to IP if serial empty
                bool exists = false;
                for (const auto& d : devices) {
                    bool same = !device.serial.empty() ? (d.serial == device.serial)
                                                       : (d.ip == device.ip);
                    if (same) { exists = true; break; }
                }
                if (!exists) devices.push_back(device);
            }
        }
    }

#ifdef _WIN32
    closesocket(sock);
#else
    close(sock);
#endif

    return devices;
}

bool UdpDiscovery::parseBroadcastPacket(const uint8_t* data, size_t length, DiscoveredDevice& device) {
    if (length != constants::STB_LOGIN_INFO_DATA_LENGTH) {
        return false;
    }
    
    try {
        auto info = GsMobileLoginInfo::fromBytes(data, length);
        
        if (!info.isValid()) {
            return false;
        }
        
        if (info.isCurrentStbConnectedFull()) {
            return false;
        }
        
        device.ip = info.stbIpAddressDisp();
        device.model_name = info.modelName();
        device.serial = info.stbSnDisp();
        device.platform_id = info.platformId();
        device.send_data_type = info.sendDataType() ? 1 : 0;
        device.sw_version = info.swVersion();
        device.sw_sub_version = info.swSubVersion();
        device.sat_enable = info.satEnable();
        device.sat2ip_enable = info.sat2ipEnable();
        
        return true;
    } catch (...) {
        return false;
    }
}

// ============================================================================
// ReceiveThread Implementation
// ============================================================================

ReceiveThread::ReceiveThread() = default;

ReceiveThread::~ReceiveThread() {
    stop();
}

void ReceiveThread::start(TcpClient* tcp_client,
                          MessageCallback on_message,
                          DisconnectCallback on_disconnect,
                          bool use_json) {
    if (running_) {
        stop();
    }
    
    tcp_client_ = tcp_client;
    on_message_ = on_message;
    on_disconnect_ = on_disconnect;
    use_json_ = use_json;
    stop_requested_ = false;
    running_ = true;
    
    last_rx_time_ = getCurrentTimeMs();
    last_tx_time_ = 0;
    keepalive_pending_ = false;
    
    receive_thread_ = std::thread(&ReceiveThread::receiveLoop, this);
    keepalive_thread_ = std::thread(&ReceiveThread::keepAliveLoop, this);
}

void ReceiveThread::stop() {
    stop_requested_ = true;
    running_ = false;
    
    if (receive_thread_.joinable()) {
        receive_thread_.join();
    }
    
    if (keepalive_thread_.joinable()) {
        keepalive_thread_.join();
    }
}

void ReceiveThread::updateActivity() {
    last_rx_time_ = getCurrentTimeMs();
}

void ReceiveThread::receiveLoop() {
    // Maximum sane payload size (10 MB) – anything larger is a corrupt header
    static constexpr uint32_t MAX_PAYLOAD = 10u * 1024u * 1024u;

    try {
        while (!stop_requested_) {
            if (!tcp_client_ || !tcp_client_->isConnected()) {
                std::this_thread::sleep_for(std::chrono::milliseconds(100));
                break;
            }
            
            // Receive GCDH header (16 bytes)
            std::vector<uint8_t> header;
            if (!tcp_client_->receiveExact(header, protocol::GcdhHeader::HEADER_SIZE, 1000)) {
                if (!tcp_client_->isConnected()) break;
                continue;
            }
            
            // Parse header
            uint32_t data_length = 0, command_type = 0, response_state = 0;
            if (!protocol::GcdhHeader::parse(header.data(), header.size(), 
                                              data_length, command_type, response_state)) {
                continue;
            }
            
            updateActivity();
            keepalive_pending_ = false;
            
            // Sanity-check payload size to prevent OOM crash
            if (data_length > MAX_PAYLOAD) {
                stb::CrashLog("receiveLoop: data_length exceeds MAX_PAYLOAD, disconnecting");
                if (tcp_client_) tcp_client_->disconnect();
                break;
            }
            
            // Receive payload if any
            std::vector<uint8_t> payload;
            if (data_length > 0) {
                if (!tcp_client_->receiveExact(payload, data_length, 10000)) {
                    if (!tcp_client_->isConnected()) break;
                    continue;
                }
                updateActivity();
            }
            
            // Decompress if needed
            try {
                payload = compression::autoDecompress(payload);
            } catch (...) {
                // Keep raw data if decompression fails
            }
            
            // Remove trailing nulls
            while (!payload.empty() && payload.back() == 0) {
                payload.pop_back();
            }
            
            // Create message
            protocol::ReceivedMessage msg;
            msg.command_type = command_type;
            msg.response_state = response_state;
            msg.data = std::move(payload);
            
            // Call callback (wrapped for safety)
            if (on_message_) {
                try {
                    on_message_(msg);
                } catch (const std::exception& e) {
                    stb::CrashLog((std::string("receiveLoop callback exception: ") + e.what()).c_str());
                } catch (...) {
                    stb::CrashLog("receiveLoop callback unknown exception");
                }
            }
        }
    } catch (const std::exception& e) {
        stb::CrashLog((std::string("receiveLoop exception: ") + e.what()).c_str());
    } catch (...) {
        stb::CrashLog("receiveLoop unknown exception");
    }
    
    running_ = false;
    
    if (on_disconnect_) {
        try { on_disconnect_(); } catch (...) {
            stb::CrashLog("receiveLoop on_disconnect_ exception");
        }
    }
}

void ReceiveThread::keepAliveLoop() {
    try {
        while (!stop_requested_ && running_) {
            std::this_thread::sleep_for(std::chrono::seconds(1));
            
            if (!tcp_client_ || !tcp_client_->isConnected()) {
                break;
            }
            
            uint64_t now = getCurrentTimeMs();
            uint64_t last_rx = last_rx_time_.load();
            uint64_t last_tx = last_tx_time_.load();
            bool pending = keepalive_pending_.load();
            
            // Check for timeout
            if (pending && (now - last_tx > constants::KEEP_ALIVE_TIMEOUT_MS) && 
                (now - last_rx > constants::KEEP_ALIVE_TIMEOUT_MS)) {
                try { tcp_client_->disconnect(); } catch (...) {}
                break;
            }
            
            // Send keep-alive
            if (!pending && (now - last_rx > constants::KEEP_ALIVE_INTERVAL_MS)) {
                try {
                    std::string payload;
                    if (use_json_) {
                        payload = protocol::JsonSerializer::serialize(
                            constants::GMS_MSG_REQUEST_SOCKET_KEEP_ALIVE, {});
                    } else {
                        payload = protocol::XmlSerializer::serialize(
                            constants::GMS_MSG_REQUEST_SOCKET_KEEP_ALIVE, {});
                    }
                    
                    if (tcp_client_->sendFramed(payload)) {
                        last_tx_time_ = now;
                        keepalive_pending_ = true;
                    }
                } catch (...) {
                    try { tcp_client_->disconnect(); } catch (...) {}
                    break;
                }
            }
        }
    } catch (const std::exception& e) {
        stb::CrashLog((std::string("keepAliveLoop exception: ") + e.what()).c_str());
    } catch (...) {
        stb::CrashLog("keepAliveLoop unknown exception");
    }
}

uint64_t ReceiveThread::getCurrentTimeMs() const {
    return std::chrono::duration_cast<std::chrono::milliseconds>(
        std::chrono::steady_clock::now().time_since_epoch()).count();
}

} // namespace network
} // namespace stb
