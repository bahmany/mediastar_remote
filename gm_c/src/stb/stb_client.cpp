#include "stb/stb_client.h"
#include "stb/serialization.h"
#include "stb/compression.h"
#include "stb/rcu_keys.h"
#include "stb/crash_log.h"
#include <thread>
#include <chrono>
#include <sstream>
#include <iomanip>

#ifdef _WIN32
#include <windows.h>
#endif

namespace stb {

// Helper to get current timestamp in milliseconds
static uint64_t getCurrentTimeMs() {
    return std::chrono::duration_cast<std::chrono::milliseconds>(
        std::chrono::steady_clock::now().time_since_epoch()).count();
}

// ============================================================================
// STBClient Implementation
// ============================================================================

STBClient::STBClient() = default;

STBClient::~STBClient() {
    disconnect();
}

STBClient::STBClient(STBClient&& other) noexcept
    : tcp_client_(std::move(other.tcp_client_)),
      processor_(std::move(other.processor_)),
      favorites_(std::move(other.favorites_)),
      state_(other.state_.load()),
      ip_(std::move(other.ip_)),
      port_(other.port_),
      login_info_(std::move(other.login_info_)),
      auto_reconnect_(other.auto_reconnect_.load()),
      reconnect_attempt_(other.reconnect_attempt_.load()) {
}

STBClient& STBClient::operator=(STBClient&& other) noexcept {
    if (this != &other) {
        disconnect();
        tcp_client_ = std::move(other.tcp_client_);
        processor_ = std::move(other.processor_);
        favorites_ = std::move(other.favorites_);
        state_ = other.state_.load();
        ip_ = std::move(other.ip_);
        port_ = other.port_;
        login_info_ = std::move(other.login_info_);
        auto_reconnect_ = other.auto_reconnect_.load();
        reconnect_attempt_ = other.reconnect_attempt_.load();
    }
    return *this;
}

bool STBClient::connect(const std::string& ip, int port, int timeout_ms) {
    if (state_ == ConnectionState::Connecting || state_ == ConnectionState::Authenticated) {
        return false;
    }
    
    state_ = ConnectionState::Connecting;
    ip_ = ip;
    port_ = port;
    
    if (connection_callback_) {
        connection_callback_(state_, "Connecting to " + ip + ":" + std::to_string(port));
    }
    
    // TCP connect
    if (!tcp_client_.connect(ip, port, timeout_ms)) {
        last_error_ = "TCP connection failed";
        state_ = ConnectionState::ConnectionFailed;
        if (connection_callback_) {
            connection_callback_(state_, last_error_);
        }
        return false;
    }
    
    // Perform login handshake
    if (!performLogin(timeout_ms)) {
        tcp_client_.disconnect();
        state_ = ConnectionState::ConnectionFailed;
        return false;
    }
    
    state_ = ConnectionState::Authenticated;
    reconnect_attempt_ = 0;
    
    // Start receive thread
    processor_.setUseJson(login_info_->usesJson());
    processor_.setCallback([this](const std::string& event, const std::string& data) {
        onStateChanged(event, data);
    });
    
    receive_thread_.start(&tcp_client_,
                          [this](const protocol::ReceivedMessage& msg) { onMessageReceived(msg); },
                          [this]() { onDisconnected(); },
                          login_info_->usesJson());
    
    if (connection_callback_) {
        connection_callback_(state_, "Connected and authenticated");
    }
    
    // Request initial data
    requestStbInfo();
    requestCurrentChannel();
    
    return true;
}

void STBClient::disconnect() {
    try {
        auto_reconnect_ = false;
        reconnecting_ = false;
        
        // Detach reconnect thread to avoid deadlock
        if (reconnect_thread_.joinable()) {
            reconnect_thread_.detach();
        }
        
        try { receive_thread_.stop(); } catch (...) {}
        try { tcp_client_.disconnect(); } catch (...) {}
        
        state_ = ConnectionState::Disconnected;
        login_info_.reset();
        
        if (connection_callback_) {
            try { connection_callback_(state_, "Disconnected"); } catch (...) {}
        }
    } catch (...) {}
}

bool STBClient::isConnected() const {
    return state_ == ConnectionState::Authenticated && tcp_client_.isConnected();
}

void STBClient::setAutoReconnect(bool enabled) {
    auto_reconnect_ = enabled;
}

void STBClient::setConnectionCallback(ConnectionCallback callback) {
    connection_callback_ = callback;
}

void STBClient::setChannelListCallback(ChannelListCallback callback) {
    channel_list_callback_ = callback;
}

void STBClient::setNotificationCallback(NotificationCallback callback) {
    notification_callback_ = callback;
}

bool STBClient::performLogin(int timeout_ms) {
    // Generate device info
    std::string device_model = "Windows-10-STB-Control";
    
    // Generate UUID (simplified version)
    std::ostringstream uuid;
    uuid << std::hex << std::setfill('0');
    for (int i = 0; i < 8; ++i) uuid << std::setw(2) << (rand() % 256);
    uuid << "-";
    for (int i = 0; i < 4; ++i) uuid << std::setw(2) << (rand() % 256);
    uuid << "-";
    for (int i = 0; i < 4; ++i) uuid << std::setw(2) << (rand() % 256);
    uuid << "-";
    for (int i = 0; i < 4; ++i) uuid << std::setw(2) << (rand() % 256);
    uuid << "-";
    for (int i = 0; i < 12; ++i) uuid << std::setw(2) << (rand() % 256);
    
    // Build login command
    std::vector<std::map<std::string, protocol::ParamValue>> params;
    std::map<std::string, protocol::ParamValue> param;
    param["data"] = device_model;
    param["uuid"] = uuid.str();
    params.push_back(param);
    
    std::string payload = protocol::XmlSerializer::serialize(constants::GMS_MSG_REQUEST_LOGIN_INFO, params);
    
    // Send login request
    if (!tcp_client_.sendFramed(payload)) {
        last_error_ = "Failed to send login request";
        return false;
    }
    
    // Receive login response (108 bytes)
    std::vector<uint8_t> response;
    if (!tcp_client_.receiveExact(response, constants::STB_LOGIN_INFO_DATA_LENGTH, timeout_ms)) {
        last_error_ = "Failed to receive login response";
        return false;
    }
    
    // Parse login info
    try {
        login_info_ = std::make_unique<GsMobileLoginInfo>(GsMobileLoginInfo::fromBytes(response));
    } catch (const std::exception& e) {
        last_error_ = std::string("Login parse error: ") + e.what();
        return false;
    }
    
    if (!login_info_->isValid()) {
        last_error_ = "Invalid login response (bad magic)";
        return false;
    }
    
    if (login_info_->isCurrentStbConnectedFull()) {
        last_error_ = "STB connection slots are full";
        return false;
    }
    
    return true;
}

void STBClient::onMessageReceived(const protocol::ReceivedMessage& msg) {
    try {
        processor_.processMessage(msg);
    } catch (...) {
        // Never let an exception escape into the receive thread
    }
}

void STBClient::onDisconnected() {
    try {
        // Avoid re-entry: if already disconnected/reconnecting, skip
        auto prev = state_.load();
        if (prev == ConnectionState::Disconnected || prev == ConnectionState::Reconnecting) return;

        state_ = ConnectionState::Disconnected;
        
        if (connection_callback_) {
            try { connection_callback_(state_, "Connection lost"); } catch (...) {}
        }
        
        if (auto_reconnect_ && reconnect_attempt_ < MAX_RECONNECT_ATTEMPTS) {
            scheduleReconnect();
        }
    } catch (...) {}
}

void STBClient::onStateChanged(const std::string& event, const std::string& data) {
    try {
        if (event == "channel_list_progress") {
            if (channel_list_callback_) {
                int count = static_cast<int>(processor_.state().channels.size());
                int expected = channels_expected_.load();
                bool complete = processor_.state().channels_complete;
                try { channel_list_callback_(count, expected, complete); } catch (...) {}
            }
        } else if (event == "channel_list_complete") {
            if (channel_list_callback_) {
                int count = static_cast<int>(processor_.state().channels.size());
                try { channel_list_callback_(count, count, true); } catch (...) {}
            }
            channel_loading_ = false;
        }
        
        if (notification_callback_) {
            try { notification_callback_(event, data); } catch (...) {}
        }
    } catch (...) {}
}

void STBClient::scheduleReconnect() {
    try {
        if (reconnecting_) return;
        reconnecting_ = true;
        
        // Detach old reconnect thread if it's still around
        // (join might deadlock if called from receive thread context)
        if (reconnect_thread_.joinable()) {
            reconnect_thread_.detach();
        }
        
        reconnect_thread_ = std::thread(&STBClient::doReconnect, this);
    } catch (...) {
        reconnecting_ = false;
    }
}

void STBClient::doReconnect() {
    try {
        state_ = ConnectionState::Reconnecting;
        
        while (auto_reconnect_ && reconnect_attempt_ < MAX_RECONNECT_ATTEMPTS) {
            reconnect_attempt_++;
            
            if (connection_callback_) {
                try {
                    connection_callback_(state_, "Reconnect attempt " + std::to_string(reconnect_attempt_) + 
                                               "/" + std::to_string(MAX_RECONNECT_ATTEMPTS));
                } catch (...) {}
            }
            
            // Exponential backoff: base * 2^(attempt-1), capped at max
            int attempt = reconnect_attempt_.load();
            int delay = RECONNECT_BASE_MS;
            for (int i = 1; i < attempt && delay < RECONNECT_MAX_MS; ++i)
                delay = std::min(delay * 2, RECONNECT_MAX_MS);
            std::this_thread::sleep_for(std::chrono::milliseconds(delay));
            
            if (!auto_reconnect_) break;

            // Stop old receive thread before reconnecting
            try { receive_thread_.stop(); } catch (...) {}
            try { tcp_client_.disconnect(); } catch (...) {}
            
            // Try to connect
            try {
                if (connect(ip_, port_, 4000)) {
                    reconnecting_ = false;
                    return;
                }
            } catch (...) {}
        }
        
        // Max attempts reached
        reconnecting_ = false;
        state_ = ConnectionState::ConnectionFailed;
        
        if (connection_callback_) {
            try { connection_callback_(state_, "Max reconnection attempts reached"); } catch (...) {}
        }
    } catch (...) {
        reconnecting_ = false;
    }
}

bool STBClient::internalSendCommand(int cmd, const std::vector<std::map<std::string, protocol::ParamValue>>& params) {
    if (!isConnected()) {
        return false;
    }
    
    std::string payload;
    if (login_info_->usesJson()) {
        payload = protocol::JsonSerializer::serialize(cmd, params);
    } else {
        payload = protocol::XmlSerializer::serialize(cmd, params);
    }
    
    uint64_t t0 = getCurrentTimeMs();
    bool ok = tcp_client_.sendFramed(payload);
    uint64_t elapsed = getCurrentTimeMs() - t0;

    if (ok) last_activity_ms_ = t0;

    {
        std::lock_guard<std::mutex> lk(audit_mutex_);
        cmd_audit_.push_back({t0, cmd, ok, static_cast<uint32_t>(elapsed)});
        if (cmd_audit_.size() > 200)
            cmd_audit_.pop_front();
    }

    return ok;
}

uint64_t STBClient::millisSinceLastActivity() const {
    uint64_t last = last_activity_ms_.load();
    if (last == 0) return UINT64_MAX;
    return getCurrentTimeMs() - last;
}

std::vector<STBClient::CmdAuditEntry> STBClient::getCmdAudit() const {
    std::lock_guard<std::mutex> lk(audit_mutex_);
    return {cmd_audit_.begin(), cmd_audit_.end()};
}

Channel* STBClient::findChannel(int channel_index) {
    return processor_.state().findChannelByIndex(channel_index);
}

// ==================== Remote Control ====================

bool STBClient::sendRemoteKey(int key_value) {
    std::vector<std::map<std::string, protocol::ParamValue>> params;
    std::map<std::string, protocol::ParamValue> param;
    param["KeyValue"] = std::to_string(key_value);
    params.push_back(param);
    
    return internalSendCommand(constants::GMS_MSG_DO_REMOTE_CONTROL, params);
}

// ── Platform helpers ──────────────────────────────────────────────────────
bool STBClient::isP30Platform() const {
    return login_info_ && login_info_->platformId() == 30;
}
int STBClient::platformId() const {
    return login_info_ ? static_cast<int>(login_info_->platformId()) : 0;
}

// ── Typed navigation ──────────────────────────────────────────────────────
bool STBClient::sendNavUp()    { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_UP    : keys::KEY_UP);    }
bool STBClient::sendNavDown()  { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_DOWN  : keys::KEY_DOWN);  }
bool STBClient::sendNavLeft()  { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_LEFT  : keys::KEY_LEFT);  }
bool STBClient::sendNavRight() { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_RIGHT : keys::KEY_RIGHT); }
bool STBClient::sendOk()       { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_ENTER : keys::KEY_ENTER); }
bool STBClient::sendBack()     { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_BACK  : keys::KEY_BACK);  }
bool STBClient::sendMenu()     { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_MENU  : keys::KEY_MENU);  }

// ── Channel / Volume ──────────────────────────────────────────────────────
bool STBClient::sendChannelUp()   { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_CH_UP   : keys::KEY_CH_UP);   }
bool STBClient::sendChannelDown() { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_CH_DOWN : keys::KEY_CH_DOWN); }
bool STBClient::sendVolUp()       { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_VOL_UP   : keys::KEY_VOL_UP);   }
bool STBClient::sendVolDown()     { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_VOL_DOWN : keys::KEY_VOL_DOWN); }
bool STBClient::sendMute()        { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_MUTE  : keys::KEY_MUTE);  }
bool STBClient::sendRecall()      { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_RECALL : keys::KEY_RECALL); }

// ── Numeric ───────────────────────────────────────────────────────────────
bool STBClient::sendNumericKey(int digit) {
    if (digit < 0 || digit > 9) return false;
    static const int def[10] = {
        keys::KEY_0, keys::KEY_1, keys::KEY_2, keys::KEY_3, keys::KEY_4,
        keys::KEY_5, keys::KEY_6, keys::KEY_7, keys::KEY_8, keys::KEY_9
    };
    static const int p30[10] = {
        keys::p30::KEY_0, keys::p30::KEY_1, keys::p30::KEY_2, keys::p30::KEY_3, keys::p30::KEY_4,
        keys::p30::KEY_5, keys::p30::KEY_6, keys::p30::KEY_7, keys::p30::KEY_8, keys::p30::KEY_9
    };
    return sendRemoteKey(isP30Platform() ? p30[digit] : def[digit]);
}

bool STBClient::sendNumericSequence(const std::string& digits, int delay_ms) {
    bool ok = true;
    for (char c : digits) {
        if (c >= '0' && c <= '9') {
            if (!sendNumericKey(c - '0')) ok = false;
            if (delay_ms > 0)
                std::this_thread::sleep_for(std::chrono::milliseconds(delay_ms));
        }
    }
    return ok;
}

// ── Color buttons ─────────────────────────────────────────────────────────
bool STBClient::sendColorKey(char color) {
    switch (color) {
        case 'R': case 'r': return sendRemoteKey(isP30Platform() ? keys::p30::KEY_RED    : keys::KEY_RED);
        case 'G': case 'g': return sendRemoteKey(isP30Platform() ? keys::p30::KEY_GREEN  : keys::KEY_GREEN);
        case 'Y': case 'y': return sendRemoteKey(isP30Platform() ? keys::p30::KEY_YELLOW : keys::KEY_YELLOW);
        case 'B': case 'b': return sendRemoteKey(isP30Platform() ? keys::p30::KEY_BLUE   : keys::KEY_BLUE);
        default:            return false;
    }
}

// ── Info / EPG / Fav ──────────────────────────────────────────────────────
bool STBClient::sendInfo()    { return sendRemoteKey(keys::KEY_INFO);    }
bool STBClient::sendEpg()     { return sendRemoteKey(keys::KEY_EPG);     }
bool STBClient::sendFav()     { return sendRemoteKey(keys::KEY_FAV);     }

// ── Playback ──────────────────────────────────────────────────────────────
bool STBClient::sendPlayPause()    { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_PLAY_PAUSE    : keys::KEY_PLAY_PAUSE);    }
bool STBClient::sendStop()         { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_STOP          : keys::KEY_STOP);          }
bool STBClient::sendRecord()       { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_RECORD        : keys::KEY_RECORD);        }
bool STBClient::sendRewind()       { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_REWIND        : keys::KEY_REWIND);        }
bool STBClient::sendFastForward()  { return sendRemoteKey(isP30Platform() ? keys::p30::KEY_FAST_FORWARD  : keys::KEY_FAST_FORWARD);  }

bool STBClient::sendText(const std::string& text, bool force) {
    if (text.empty()) return true;
    
    bool all_sent = true;
    for (char ch : text) {
        if (!sendKeyboardCode(static_cast<int>(ch), force)) {
            all_sent = false;
        }
    }
    return all_sent;
}

bool STBClient::sendKeyboardCode(int key_code, bool force) {
    if (key_code <= 0) return false;
    
    // Check platform restrictions (force bypasses this check)
    if (!force && login_info_) {
        int platform = login_info_->platformId();
        if (platform == 32 || platform == 71 || platform == 72 || platform == 74) {
            return true;
        }
    }
    
    std::vector<std::map<std::string, protocol::ParamValue>> params;
    std::map<std::string, protocol::ParamValue> param;
    param["KeyCode"] = std::to_string(key_code);
    params.push_back(param);
    
    return internalSendCommand(constants::GMS_MSG_DO_INPUT_METHOD_KEY_CODE_SET, params);
}

bool STBClient::sendKeyboardEnter() {
    // APK SoftKeyboardActivity: keyCode==66 (Enter)
    // platforms 20/21/25 -> sendKeyValue(8)
    // default            -> sendKeyValue(11)
    if (login_info_) {
        int platform = login_info_->platformId();
        if (platform == 20 || platform == 21 || platform == 25) {
            return sendRemoteKey(8);
        }
    }
    return sendRemoteKey(11);
}

bool STBClient::sendKeyboardBackspace() {
    // APK SoftKeyboardActivity: keyCode==67 (Backspace) -> sendKeyValue(10)
    return sendRemoteKey(10);
}

bool STBClient::dismissInputMethod() {
    return internalSendCommand(constants::GMS_MSG_DO_INPUT_METHOD_DISMISS, {});
}

// ==================== Channel Operations ====================

int STBClient::requestChannelList(bool force_refresh, double cache_hours) {
    if (!force_refresh && processor_.state().isChannelsCacheValid(cache_hours)) {
        // Use cached data – still fire callback so UI updates
        int count = static_cast<int>(processor_.state().channels.size());
        if (channel_list_callback_) {
            channel_list_callback_(count, count, true);
        }
        return count;
    }
    
    processor_.state().clearChannels();
    processor_.startChannelListWait();
    channel_loading_ = true;
    channels_received_ = 0;
    
    // Request in batches of 100
    const int batch_size = 100;
    int from_idx = 0;
    int max_batches = 200; // safety limit (20 000 channels)
    
    for (int batch = 0; batch < max_batches && channel_loading_; ++batch) {
        std::vector<std::map<std::string, protocol::ParamValue>> params;
        std::map<std::string, protocol::ParamValue> param1;
        param1["FromIndex"] = std::to_string(from_idx);
        params.push_back(param1);
        std::map<std::string, protocol::ParamValue> param2;
        param2["ToIndex"] = std::to_string(from_idx + batch_size - 1);
        params.push_back(param2);
        
        if (!internalSendCommand(constants::GMS_MSG_REQUEST_CHANNEL_LIST, params)) {
            stb::CrashLog("reqChList: send FAILED");
            break;
        }
        
        // Wait for THIS batch to arrive (15 s timeout)
        if (!processor_.waitForChannelList(15000)) {
            stb::CrashLog(("reqChList: TIMEOUT at ch=" + 
                           std::to_string(processor_.state().channels.size())).c_str());
            break;
        }
        
        // Fire progress callback so UI can update in real-time
        int count = static_cast<int>(processor_.state().channels.size());
        int expected = channels_expected_.load();
        if (expected <= 0) expected = count + batch_size; // estimate
        if (channel_list_callback_) {
            try { channel_list_callback_(count, expected, false); } catch (...) {}
        }
        
        // Check if complete
        if (processor_.state().channels_complete) break;
        
        // Check if we got a partial batch (last batch)
        int got_this_batch = processor_.lastBatchSize();
        if (got_this_batch <= 0 || got_this_batch < batch_size) break;
        
        from_idx += batch_size;
        
        // Small delay between batches to avoid flooding STB
        std::this_thread::sleep_for(std::chrono::milliseconds(50));
    }
    
    // Mark complete
    channel_loading_ = false;
    int total = static_cast<int>(processor_.state().channels.size());
    
    // Fire final callback
    if (channel_list_callback_) {
        channel_list_callback_(total, total, true);
    }
    
    return total;
}

bool STBClient::waitForChannelList(int timeout_ms) {
    return processor_.waitForChannelList(timeout_ms);
}

bool STBClient::changeChannel(int channel_index) {
    Channel* ch = findChannel(channel_index);
    if (!ch) {
        return false;
    }
    
    std::string program_id = ch->program_id;
    if (program_id.empty()) {
        program_id = std::to_string(channel_index);
    }
    
    std::vector<std::map<std::string, protocol::ParamValue>> params;
    std::map<std::string, protocol::ParamValue> param;
    param["TvState"] = std::to_string(ch->channel_type);
    param["ProgramId"] = program_id;
    params.push_back(param);
    
    return internalSendCommand(constants::GMS_MSG_DO_CHANNEL_SWITCH, params);
}

bool STBClient::changeChannelByProgramId(const std::string& program_id) {
    Channel* ch = processor_.state().findChannelByProgramId(program_id);
    if (!ch) {
        return changeChannelDirect(program_id, 0);
    }
    
    return changeChannel(ch->service_index);
}

bool STBClient::changeChannelDirect(const std::string& program_id, int tv_state) {
    if (program_id.empty()) return false;

    std::vector<std::map<std::string, protocol::ParamValue>> params;
    std::map<std::string, protocol::ParamValue> param;
    param["TvState"] = std::to_string(tv_state);
    param["ProgramId"] = program_id;
    params.push_back(param);

    return internalSendCommand(constants::GMS_MSG_DO_CHANNEL_SWITCH, params);
}

bool STBClient::requestCurrentChannel() {
    return internalSendCommand(constants::GMS_MSG_REQUEST_PLAYING_CHANNEL, {});
}

bool STBClient::requestEpg(int channel_index) {
    Channel* ch = findChannel(channel_index);
    if (!ch) {
        return false;
    }
    
    std::string program_id = ch->program_id;
    if (program_id.empty()) {
        program_id = std::to_string(channel_index);
    }
    
    std::vector<std::map<std::string, protocol::ParamValue>> params;
    std::map<std::string, protocol::ParamValue> param;
    param["ProgramId"] = program_id;
    params.push_back(param);
    
    return internalSendCommand(constants::GMS_MSG_REQUEST_PROGRAM_EPG, params);
}

bool STBClient::requestChannelListType() {
    return internalSendCommand(constants::GMS_MSG_REQUEST_CHANNEL_LIST_TYPE, {});
}

bool STBClient::setChannelListType(bool is_fav_list, int list_type) {
    std::vector<std::map<std::string, protocol::ParamValue>> params;
    std::map<std::string, protocol::ParamValue> param;
    param["IsFavList"] = is_fav_list ? "1" : "0";
    param["SelectListType"] = std::to_string(list_type);
    params.push_back(param);
    
    return internalSendCommand(constants::GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED, params);
}

// ==================== Favorites ====================

bool STBClient::addChannelToFavorites(int channel_index, const std::vector<int>& group_ids) {
    Channel* ch = findChannel(channel_index);
    if (!ch) {
        return false;
    }
    
    // Add to local favorites
    if (!favorites_.addFavorite(*ch, group_ids)) {
        return false;
    }
    
    // Sync to STB
    std::string program_id = ch->program_id;
    if (program_id.empty()) {
        program_id = std::to_string(channel_index);
    }
    
    return setChannelFavMark({program_id}, 1, group_ids);
}

bool STBClient::removeChannelFromFavorites(int channel_index, const std::vector<int>& group_ids) {
    Channel* ch = findChannel(channel_index);
    if (!ch) {
        return false;
    }
    
    std::string program_id = ch->program_id;
    if (program_id.empty()) {
        program_id = std::to_string(channel_index);
    }
    
    // Remove from local favorites
    if (!favorites_.removeFavorite(program_id, group_ids)) {
        return false;
    }
    
    // Sync to STB
    return setChannelFavMark({program_id}, 0, group_ids);
}

bool STBClient::setChannelFavMark(const std::vector<std::string>& program_ids, 
                                   int fav_mark,
                                   const std::vector<int>& group_ids) {
    // Build group string (format: "1:2:3:")
    std::string group_str;
    for (int gid : group_ids) {
        group_str += std::to_string(gid) + ":";
    }
    
    // Build program IDs string for serialization
    std::ostringstream prog_stream;
    for (size_t i = 0; i < program_ids.size(); ++i) {
        if (i > 0) prog_stream << ",";
        prog_stream << program_ids[i];
    }
    
    std::vector<std::map<std::string, protocol::ParamValue>> params;
    std::map<std::string, protocol::ParamValue> param;
    param["TvState"] = "0";  // TV by default
    param["FavMark"] = std::to_string(fav_mark);
    param["FavorGroupID"] = group_str;
    param["ProgramIds"] = prog_stream.str();
    
    int platform = login_info_ ? login_info_->platformId() : 0;
    if (platform != 30 && platform != 31 && platform != 32 && 
        platform != 71 && platform != 72 && platform != 74) {
        param["TotalNum"] = std::to_string(static_cast<int>(program_ids.size()));
    }
    
    params.push_back(param);
    
    return internalSendCommand(constants::GMS_MSG_DO_CHANNEL_FAV_MARK, params);
}

bool STBClient::requestFavGroupNames() {
    return internalSendCommand(constants::GMS_MSG_REQUEST_FAV_GROUP_NAMES, {});
}

bool STBClient::renameFavGroup(int group_id, const std::string& new_name) {
    // Update local first
    if (!favorites_.renameGroup(group_id, new_name)) {
        return false;
    }
    
    // Sync to STB
    std::vector<std::map<std::string, protocol::ParamValue>> params;
    std::map<std::string, protocol::ParamValue> param;
    param["FavorRenamePos"] = std::to_string(group_id - 1);  // 0-based position
    param["FavorNewName"] = new_name;
    param["FavorGroupID"] = std::to_string(group_id);
    params.push_back(param);
    
    return internalSendCommand(constants::GMS_MSG_DO_FAV_GROUP_RENAME, params);
}

// ==================== System Operations ====================

bool STBClient::requestStbInfo() {
    return internalSendCommand(constants::GMS_MSG_REQUEST_STB_INFO, {});
}

bool STBClient::requestSatelliteList() {
    return internalSendCommand(constants::GMS_MSG_REQUEST_SAT_LIST, {});
}

bool STBClient::sendKeepAlive() {
    return internalSendCommand(constants::GMS_MSG_REQUEST_SOCKET_KEEP_ALIVE, {});
}

bool STBClient::restartStb() {
    return internalSendCommand(constants::GMS_MSG_DO_STB_RESTART, {});
}

bool STBClient::powerSwitch() {
    return internalSendCommand(constants::GMS_MSG_DO_POWER_SWITCH, {});
}

bool STBClient::tvRadioSwitch() {
    return internalSendCommand(constants::GMS_MSG_DO_TV_RADIO_SWITCH, {});
}

std::string STBClient::getLastError() const {
    return last_error_;
}

std::vector<network::UdpDiscovery::DiscoveredDevice> STBClient::discoverDevices(int timeout_ms) {
    return network::UdpDiscovery::discover(timeout_ms);
}

} // namespace stb
