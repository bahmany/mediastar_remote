#pragma once

#include <cstdint>
#include <string>
#include <array>
#include <vector>
#include <set>

namespace stb {

// Forward declarations
class GsMobileLoginInfo;

/**
 * @brief Represents login information received from STB broadcast/response
 * 
 * This 108-byte structure contains device identification and capability info.
 * The raw data must be descrambled using XOR 0x5B + byte swap.
 */
class GsMobileLoginInfo {
public:
    // Constructors
    GsMobileLoginInfo() = default;
    
    /**
     * @brief Parse from 108-byte raw transport message
     * @param transport_msg Raw bytes from STB (must be exactly 108 bytes)
     * @throws std::invalid_argument if data length is incorrect
     */
    static GsMobileLoginInfo fromBytes(const std::vector<uint8_t>& transport_msg);
    static GsMobileLoginInfo fromBytes(const uint8_t* data, size_t length);
    
    // Getters
    const std::string& magicCode() const { return magic_code_; }
    const std::vector<uint8_t>& stbSn() const { return stb_sn_; }
    const std::string& stbSnDisp() const { return stb_sn_disp_; }
    const std::string& modelName() const { return model_name_; }
    const std::vector<uint8_t>& stbCpuChipId() const { return stb_cpu_chip_id_; }
    const std::vector<uint8_t>& stbFlashId() const { return stb_flash_id_; }
    const std::string& stbIpAddressDisp() const { return stb_ip_address_disp_; }
    uint8_t platformId() const { return platform_id_; }
    uint16_t swVersion() const { return sw_version_; }
    uint8_t stbCustomerId() const { return stb_customer_id_; }
    uint8_t stbModelId() const { return stb_model_id_; }
    uint32_t swSubVersion() const { return sw_sub_version_; }
    
    // Feature flags
    bool isCurrentStbConnectedFull() const { return is_current_stb_connected_full_; }
    bool clientType() const { return client_type_; }
    bool satEnable() const { return sat_enable_; }
    uint8_t sat2ipEnable() const { return sat2ip_enable_; }
    bool sendDataType() const { return send_data_type_; }  // 0=XML, 1=JSON
    
    // Helper methods
    bool isValid() const { return magic_code_ == "39WwijOog54a"; }
    bool usesJson() const { return send_data_type_; }
    bool usesXml() const { return !send_data_type_; }
    
private:
    std::string magic_code_;
    std::vector<uint8_t> stb_sn_;
    std::string stb_sn_disp_;
    std::string model_name_;
    std::vector<uint8_t> stb_cpu_chip_id_;
    std::vector<uint8_t> stb_flash_id_;
    std::string stb_ip_address_disp_;
    uint8_t platform_id_ = 0;
    uint16_t sw_version_ = 0;
    uint8_t stb_customer_id_ = 0;
    uint8_t stb_model_id_ = 0;
    uint32_t sw_sub_version_ = 0;
    
    // Feature flags
    bool is_current_stb_connected_full_ = false;
    bool client_type_ = false;
    bool sat_enable_ = false;
    uint8_t sat2ip_enable_ = 0;
    bool send_data_type_ = false;
    
    static std::string serialNumberToDisplay(const std::vector<uint8_t>& sn);
};

/**
 * @brief Represents a channel from the STB channel list
 */
struct Channel {
    // Core identifiers
    int service_index = -1;        // ServiceIndex (primary sort key)
    std::string service_id;        // ServiceID e.g. "00150038600160"
    std::string service_name;      // ServiceName
    
    // Type flags
    bool is_radio = false;         // Radio=1 → radio, 0 → TV
    bool is_hd = false;            // HD=1
    bool is_scrambled = false;     // Scramble=1
    bool is_locked = false;        // Lock=1
    bool has_epg = false;          // EPG=1
    bool is_playing = false;       // Playing=1
    bool will_be_played = false;   // WillBePlayed=1
    bool is_tuner2 = false;        // IsTuner2=1
    
    // Favorite
    int fav_bit = 0;               // FavBit (bitmask: bit0=fav1, bit1=fav2, ...)
    
    // PIDs
    int video_pid = 0;             // VideoPID
    int pmt_pid = 0;               // PMTPID
    int pcr_pid = 0;               // PID (PCR PID)
    int ttx_pid = 0;               // TTXPID
    std::string audio_pids_raw;    // AudioArray raw JSON
    std::string subtitle_pids_raw; // SubtArray raw JSON
    
    // Modulation / transponder info
    int modulation_system = 0;     // ModulationSystem (0=DVB-S, 1=DVB-S2)
    int modulation_type = 0;       // ModulationType (0=Auto,1=QPSK,2=8PSK,...)
    int roll_off = 0;              // RollOff (35=0.35, 25=0.25, 20=0.20)
    int pilot_tones = 0;           // PilotTones
    
    // Legacy compat
    int program_index = -1;
    std::string program_id;        // Alias for service_id
    std::string service_num;
    int channel_type = 0;          // 0=TV, 1=Radio (compat)
    bool is_fta = false;           // !is_scrambled (compat)
    bool is_favorite = false;
    std::set<int> favorite_groups;
    int satellite_index = -1;
    std::string satellite_name;
    int tp_index = -1;
    
    bool isValid() const { return service_index >= 0 || !service_id.empty(); }
    
    // Helper: decode ServiceID → frequency info
    // ServiceID format: SSTTFFFFFFPPPP  (sat, tp, freq, prognum)
    int frequency() const {
        // ServiceID is like "00150038600160" → freq at pos 4..10
        if (service_id.size() >= 10) {
            try { return std::stoi(service_id.substr(4, 6)); } catch (...) {}
        }
        return 0;
    }
    int satIndex() const {
        if (service_id.size() >= 4) {
            try { return std::stoi(service_id.substr(0, 4)); } catch (...) {}
        }
        return -1;
    }
    std::string modulationSystemStr() const {
        return modulation_system == 0 ? "DVB-S" : "DVB-S2";
    }
    std::string modulationTypeStr() const {
        switch (modulation_type) {
            case 0: return "Auto";
            case 1: return "QPSK";
            case 2: return "8PSK";
            case 3: return "16APSK";
            case 4: return "32APSK";
            default: return "?";
        }
    }
};

/**
 * @brief Represents an EPG event
 */
struct EpgEvent {
    std::string event_id;
    std::string program_id;
    std::string event_name;
    std::string description;
    time_t start_time = 0;
    time_t end_time = 0;
};

/**
 * @brief Represents a satellite
 */
struct Satellite {
    int sat_index = -1;
    std::string sat_name;
    int sat_position = 0;
    bool is_east = false;
};

/**
 * @brief Represents a favorite group
 */
struct FavoriteGroup {
    int group_id = 0;
    std::string group_name;
    time_t created_time = 0;
};

/**
 * @brief Represents a favorite channel entry
 */
struct FavoriteChannel {
    std::string program_id;
    int service_index = -1;
    std::string channel_name;
    std::string channel_number;
    std::set<int> favorite_groups;
    time_t added_time = 0;
};

/**
 * @brief STB information structure
 */
struct StbInfo {
    int channel_count = 0;
    int radio_count = 0;
    std::string stb_model;
    std::string sw_version;
    std::string stb_time;
    std::string serial_number;
    int max_programs = 0;
    int current_list_type = 0;
    bool is_fav_list = false;
};

/**
 * @brief Complete STB state container
 */
class STBState {
public:
    // Channel data
    std::vector<Channel> channels;
    int64_t channels_cache_time = 0;  // Unix timestamp
    bool channels_complete = false;
    
    // Current status
    int current_channel_index = 0;
    std::string current_program_id;
    
    // Lists
    std::vector<FavoriteGroup> fav_groups;
    std::vector<Satellite> satellites;
    std::vector<EpgEvent> epg_events;
    
    // STB info
    StbInfo stb_info;
    
    // Methods
    bool isChannelsCacheValid(double cache_hours = 1.0) const;
    void clearChannels();
    Channel* findChannelByIndex(int index);
    Channel* findChannelByProgramId(const std::string& program_id);
    
    // Favorites
    bool isChannelFavorite(int channel_index, int group_id = -1) const;
    std::vector<int> getChannelFavoriteGroups(int channel_index) const;
};

/**
 * @brief Connection state enumeration
 */
enum class ConnectionState {
    Disconnected,
    Connecting,
    Connected,
    Authenticated,
    Reconnecting,
    ConnectionFailed
};

/**
 * @brief Connection status information
 */
struct ConnectionStatus {
    ConnectionState state = ConnectionState::Disconnected;
    int error_code = 0;
    std::string error_message;
    int reconnect_attempt = 0;
    int max_reconnect_attempts = 10;
};

} // namespace stb
