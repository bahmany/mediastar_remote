#include "stb/models.h"
#include "stb/crypto.h"
#include "stb/constants.h"
#include <stdexcept>
#include <cstring>
#include <sstream>
#include <iomanip>

namespace stb {

// ============================================================================
// GsMobileLoginInfo Implementation
// ============================================================================

GsMobileLoginInfo GsMobileLoginInfo::fromBytes(const std::vector<uint8_t>& transport_msg) {
    return fromBytes(transport_msg.data(), transport_msg.size());
}

GsMobileLoginInfo GsMobileLoginInfo::fromBytes(const uint8_t* data, size_t length) {
    if (!data || length < constants::STB_LOGIN_INFO_DATA_LENGTH) {
        throw std::invalid_argument("Expected 108 bytes, got " + std::to_string(length));
    }
    
    // Copy data for descrambling
    std::vector<uint8_t> buffer(data, data + constants::STB_LOGIN_INFO_DATA_LENGTH);
    
    // Descramble
    crypto::descrambleStbInfo(buffer);
    
    GsMobileLoginInfo info;
    
    // Magic code (bytes 0-11)
    info.magic_code_ = std::string(reinterpret_cast<char*>(buffer.data()), 12);
    
    // Serial number (bytes 12-19)
    info.stb_sn_ = std::vector<uint8_t>(buffer.begin() + 12, buffer.begin() + 20);
    info.stb_sn_disp_ = serialNumberToDisplay(info.stb_sn_);
    
    // Model name (bytes 20-51, null-terminated)
    char model_name_buf[33] = {};
    std::memcpy(model_name_buf, buffer.data() + 20, 32);
    info.model_name_ = std::string(model_name_buf);
    
    // CPU chip ID (bytes 52-59)
    info.stb_cpu_chip_id_ = std::vector<uint8_t>(buffer.begin() + 52, buffer.begin() + 60);
    
    // Flash ID (bytes 60-67)
    info.stb_flash_id_ = std::vector<uint8_t>(buffer.begin() + 60, buffer.begin() + 68);
    
    // IP address (bytes 68-71) - stored little-endian, displayed in reverse
    uint8_t ip[4] = {buffer[68], buffer[69], buffer[70], buffer[71]};
    std::ostringstream ip_ss;
    ip_ss << static_cast<int>(ip[3]) << "." 
          << static_cast<int>(ip[2]) << "." 
          << static_cast<int>(ip[1]) << "." 
          << static_cast<int>(ip[0]);
    info.stb_ip_address_disp_ = ip_ss.str();
    
    // Platform ID (byte 72)
    info.platform_id_ = buffer[72];
    
    // Software version (bytes 73-74) - big-endian
    info.sw_version_ = (static_cast<uint16_t>(buffer[73]) << 8) | buffer[74];
    
    // Customer ID (byte 75)
    info.stb_customer_id_ = buffer[75];
    
    // Model ID (byte 76)
    info.stb_model_id_ = buffer[76];
    
    // Sub-version (bytes 80-83) - little-endian
    info.sw_sub_version_ = *reinterpret_cast<uint32_t*>(buffer.data() + 80);
    
    // Flags (byte 84)
    uint8_t flags = buffer[84];
    info.is_current_stb_connected_full_ = (flags & 0x01) != 0;
    info.client_type_ = (flags & 0x02) != 0;
    info.sat_enable_ = (flags & 0x04) != 0;
    info.sat2ip_enable_ = (flags & 0x18) >> 3;
    info.send_data_type_ = (flags & 0x40) != 0;
    
    return info;
}

std::string GsMobileLoginInfo::serialNumberToDisplay(const std::vector<uint8_t>& sn) {
    if (sn.size() < 6) return "";
    
    uint32_t i_date = (static_cast<uint32_t>(sn[0]) << 16) | 
                      (static_cast<uint32_t>(sn[1]) << 8) | 
                      static_cast<uint32_t>(sn[2]);
    uint32_t i_serial = (static_cast<uint32_t>(sn[3]) << 16) | 
                        (static_cast<uint32_t>(sn[4]) << 8) | 
                        static_cast<uint32_t>(sn[5]);
    
    std::ostringstream oss;
    oss << std::setw(6) << std::setfill('0') << i_date
        << std::setw(6) << std::setfill('0') << i_serial;
    return oss.str();
}

// ============================================================================
// STBState Implementation
// ============================================================================

bool STBState::isChannelsCacheValid(double cache_hours) const {
    if (channels_cache_time == 0 || channels.empty()) {
        return false;
    }
    
    if (cache_hours <= 0) {
        return !channels.empty();
    }
    
    auto now = std::time(nullptr);
    auto age = now - channels_cache_time;
    auto max_age = static_cast<int64_t>(cache_hours * 3600);
    
    return age < max_age;
}

void STBState::clearChannels() {
    channels.clear();
    channels_cache_time = 0;
    channels_complete = false;
}

Channel* STBState::findChannelByIndex(int index) {
    for (auto& ch : channels) {
        if (ch.service_index == index || ch.program_index == index) {
            return &ch;
        }
    }
    return nullptr;
}

Channel* STBState::findChannelByProgramId(const std::string& program_id) {
    for (auto& ch : channels) {
        if (ch.program_id == program_id) {
            return &ch;
        }
    }
    return nullptr;
}

bool STBState::isChannelFavorite(int channel_index, int group_id) const {
    for (const auto& ch : channels) {
        if (ch.service_index == channel_index || ch.program_index == channel_index) {
            if (group_id < 0) {
                return !ch.favorite_groups.empty();
            }
            for (int gid : ch.favorite_groups) {
                if (gid == group_id) return true;
            }
            return false;
        }
    }
    return false;
}

std::vector<int> STBState::getChannelFavoriteGroups(int channel_index) const {
    for (const auto& ch : channels) {
        if (ch.service_index == channel_index || ch.program_index == channel_index) {
            return std::vector<int>(ch.favorite_groups.begin(), ch.favorite_groups.end());
        }
    }
    return {};
}

} // namespace stb
