#pragma once

#include <string>
#include <vector>
#include <map>
#include <memory>
#include <functional>
#include <mutex>

#include "stb/models.h"
#include "stb/network.h"

namespace stb {

/**
 * @brief State change callback type
 */
using StateChangeCallback = std::function<void(const std::string& event, const std::string& data)>;

/**
 * @brief Message processor for parsing STB responses
 */
class MessageProcessor {
public:
    MessageProcessor();
    ~MessageProcessor();
    
    // Move constructor and assignment
    MessageProcessor(MessageProcessor&& other) noexcept;
    MessageProcessor& operator=(MessageProcessor&& other) noexcept;
    
    // Disable copy
    MessageProcessor(const MessageProcessor&) = delete;
    MessageProcessor& operator=(const MessageProcessor&) = delete;
    
    /**
     * @brief Process a received message
     * @param msg Message from STB
     * @return true if message was processed
     */
    bool processMessage(const protocol::ReceivedMessage& msg);
    
    /**
     * @brief Get current state
     */
    STBState& state() { return state_; }
    const STBState& state() const { return state_; }
    
    /**
     * @brief Set callback for state change events
     */
    void setCallback(StateChangeCallback callback);
    
    /**
     * @brief Set JSON/XML mode
     */
    void setUseJson(bool use_json) { use_json_ = use_json; }
    
    // Event waiting for async operations
    void startChannelListWait();
    bool waitForChannelList(int timeout_ms = 60000);
    void onChannelBatchReceived(int batch_size);
    int lastBatchSize() const { return last_batch_size_; }
    
private:
    STBState state_;
    StateChangeCallback callback_;
    bool use_json_ = false;
    
    std::mutex mutex_;
    std::condition_variable channel_cv_;
    bool channel_list_waiting_ = false;
    int last_batch_size_ = 0;
    int expected_total_channels_ = 0;
    int batch_counter_ = 0;       // incremented every batch
    int batch_wait_target_ = 0;   // the counter value we're waiting to exceed
    
    bool processMessageInner(const protocol::ReceivedMessage& msg);

    // Handlers
    void handleChannelList(const std::vector<std::map<std::string, std::string>>& data);
    void handleFavoriteGroups(const std::vector<std::map<std::string, std::string>>& data);
    void handleSatelliteList(const std::vector<std::map<std::string, std::string>>& data);
    void handleTransponderList(const std::vector<std::map<std::string, std::string>>& data);
    void handleSat2ipReturn(const std::map<std::string, std::string>& data);
    void handleCurrentChannel(const std::map<std::string, std::string>& data);
    void handleEpgData(const std::vector<std::map<std::string, std::string>>& data);
    void handleStbInfo(const std::map<std::string, std::string>& data);
    
    // Notification handlers
    void notify(const std::string& event, const std::string& data = "");
};

} // namespace stb
