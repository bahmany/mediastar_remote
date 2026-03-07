#include "stb/serialization.h"
#include "stb/constants.h"
#include <sstream>
#include <iomanip>
#include <cstring>

namespace stb {
namespace protocol {

// ============================================================================
// Socket Frame Builder
// ============================================================================

std::vector<uint8_t> buildSocketFrame(const std::vector<uint8_t>& payload) {
    std::string length_str = std::to_string(payload.size());
    length_str = std::string(constants::MAX_DATA_LENGTH_BIT - length_str.length(), '0') + length_str;
    
    std::vector<uint8_t> frame;
    frame.reserve(5 + 7 + 3 + payload.size());
    
    // Start
    frame.insert(frame.end(), 
                 reinterpret_cast<const uint8_t*>(constants::SOCKET_HEADER_START_FLAG),
                 reinterpret_cast<const uint8_t*>(constants::SOCKET_HEADER_START_FLAG) + 5);
    
    // Length (7 digits)
    frame.insert(frame.end(), 
                 reinterpret_cast<const uint8_t*>(length_str.c_str()),
                 reinterpret_cast<const uint8_t*>(length_str.c_str()) + 7);
    
    // End
    frame.insert(frame.end(),
                 reinterpret_cast<const uint8_t*>(constants::SOCKET_HEADER_END_FLAG),
                 reinterpret_cast<const uint8_t*>(constants::SOCKET_HEADER_END_FLAG) + 3);
    
    // Payload
    frame.insert(frame.end(), payload.begin(), payload.end());
    
    return frame;
}

std::vector<uint8_t> buildSocketFrame(const std::string& payload) {
    return buildSocketFrame(std::vector<uint8_t>(payload.begin(), payload.end()));
}

// ============================================================================
// XML Serializer
// ============================================================================

std::string XmlSerializer::serialize(int request_type, const std::vector<std::map<std::string, ParamValue>>& params) {
    std::ostringstream oss;
    
    oss << "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>";
    oss << "<Command request=\"" << request_type << "\">";
    
    if (!params.empty()) {
        // Special handling for different command types
        switch (request_type) {
            case constants::GMS_MSG_REQUEST_CHANNEL_LIST:
                if (params.size() >= 2) {
                    oss << "<parm>";
                    auto it0 = params[0].find("FromIndex");
                    auto it1 = params[1].find("ToIndex");
                    if (it0 != params[0].end()) {
                        oss << "<FromIndex>" << std::get<std::string>(it0->second) << "</FromIndex>";
                    }
                    if (it1 != params[1].end()) {
                        oss << "<ToIndex>" << std::get<std::string>(it1->second) << "</ToIndex>";
                    }
                    oss << "</parm>";
                }
                break;
                
            case constants::GMS_MSG_DO_INPUT_METHOD_KEY_CODE_SET:
                if (!params.empty()) {
                    auto it = params[0].find("KeyCode");
                    if (it != params[0].end()) {
                        oss << "<KeyCode>" << xmlEscape(std::get<std::string>(it->second)) << "</KeyCode>";
                    }
                }
                break;
                
            case constants::GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED:
                if (!params.empty()) {
                    auto it_is_fav = params[0].find("IsFavList");
                    auto it_type = params[0].find("SelectListType");
                    if (it_is_fav != params[0].end()) {
                        oss << "<IsFavList>" << xmlEscape(std::get<std::string>(it_is_fav->second)) << "</IsFavList>";
                    }
                    if (it_type != params[0].end()) {
                        oss << "<SelectListType>" << xmlEscape(std::get<std::string>(it_type->second)) << "</SelectListType>";
                    }
                }
                break;
                
            case constants::GMS_MSG_DO_CHANNEL_FAV_MARK:
                if (!params.empty()) {
                    auto it_tv = params[0].find("TvState");
                    auto it_fav = params[0].find("FavMark");
                    auto it_group = params[0].find("FavorGroupID");
                    
                    if (it_tv != params[0].end()) {
                        oss << "<TvState>" << xmlEscape(std::get<std::string>(it_tv->second)) << "</TvState>";
                    }
                    if (it_fav != params[0].end()) {
                        oss << "<FavMark>" << xmlEscape(std::get<std::string>(it_fav->second)) << "</FavMark>";
                    }
                    if (it_group != params[0].end()) {
                        oss << "<FavorGroupID>" << xmlEscape(std::get<std::string>(it_group->second)) << "</FavorGroupID>";
                    }
                    
                    // ProgramIds array
                    auto it_prog = params[0].find("ProgramIds");
                    if (it_prog != params[0].end()) {
                        // ProgramIds should be a comma-separated list that we split
                        std::string prog_ids = std::get<std::string>(it_prog->second);
                        std::istringstream iss(prog_ids);
                        std::string prog_id;
                        while (std::getline(iss, prog_id, ',')) {
                            oss << "<ProgramId>" << xmlEscape(prog_id) << "</ProgramId>";
                        }
                    }
                    
                    auto it_total = params[0].find("TotalNum");
                    if (it_total != params[0].end()) {
                        oss << "<TotalNum>" << xmlEscape(std::get<std::string>(it_total->second)) << "</TotalNum>";
                    }
                }
                break;
                
            case constants::GMS_MSG_DO_REMOTE_CONTROL:
                for (const auto& param : params) {
                    auto it = param.find("KeyValue");
                    if (it != param.end()) {
                        oss << "<KeyValue>" << xmlEscape(std::get<std::string>(it->second)) << "</KeyValue>";
                    }
                }
                break;
                
            case constants::GMS_MSG_DO_CHANNEL_SWITCH:
            case constants::GMS_MSG_DO_SAT2IP_CHANNEL_PLAY:
                for (const auto& param : params) {
                    oss << "<parm>";
                    auto it_tv = param.find("TvState");
                    auto it_prog = param.find("ProgramId");
                    if (it_tv != param.end()) {
                        oss << "<TvState>" << xmlEscape(std::get<std::string>(it_tv->second)) << "</TvState>";
                    }
                    if (it_prog != param.end()) {
                        oss << "<ProgramId>" << xmlEscape(std::get<std::string>(it_prog->second)) << "</ProgramId>";
                    }
                    if (request_type == constants::GMS_MSG_DO_SAT2IP_CHANNEL_PLAY) {
                        auto it_rr = param.find("iResolutionRatio");
                        auto it_br = param.find("iBitrate");
                        if (it_rr != param.end()) {
                            oss << "<iResolutionRatio>" << xmlEscape(std::get<std::string>(it_rr->second)) << "</iResolutionRatio>";
                        }
                        if (it_br != param.end()) {
                            oss << "<iBitrate>" << xmlEscape(std::get<std::string>(it_br->second)) << "</iBitrate>";
                        }
                    }
                    oss << "</parm>";
                }
                break;
                
            case constants::GMS_MSG_REQUEST_PROGRAM_EPG:
                for (const auto& param : params) {
                    oss << "<parm>";
                    auto it = param.find("ProgramId");
                    if (it != param.end()) {
                        oss << "<ProgramId>" << xmlEscape(std::get<std::string>(it->second)) << "</ProgramId>";
                    }
                    oss << "</parm>";
                }
                break;
                
            case constants::GMS_MSG_REQUEST_LOGIN_INFO:
                for (const auto& param : params) {
                    for (const auto& [key, value] : param) {
                        oss << "<" << key << ">" << xmlEscape(std::get<std::string>(value)) << "</" << key << ">";
                    }
                }
                break;
                
            default:
                // Generic: output all params as key-value pairs
                for (const auto& param : params) {
                    for (const auto& [key, value] : param) {
                        oss << "<" << key << ">" << xmlEscape(std::get<std::string>(value)) << "</" << key << ">";
                    }
                }
                break;
        }
    }
    
    oss << "</Command>";
    return oss.str();
}

std::string XmlSerializer::serializeSimple(int request_type, const std::string& key, const std::string& value) {
    std::vector<std::map<std::string, ParamValue>> params;
    std::map<std::string, ParamValue> param;
    param[key] = value;
    params.push_back(param);
    return serialize(request_type, params);
}

std::map<std::string, std::string> XmlSerializer::parse(const std::string& xml) {
    std::map<std::string, std::string> result;
    // Simplified parser - in production use a proper XML library
    // For now, this is a placeholder
    return result;
}

std::vector<std::map<std::string, std::string>> XmlSerializer::parseChannelList(const std::string& xml) {
    std::vector<std::map<std::string, std::string>> result;
    // Simplified parser - in production use a proper XML library
    return result;
}

std::string XmlSerializer::xmlEscape(const std::string& text) {
    std::string result;
    result.reserve(text.size());
    
    for (char c : text) {
        switch (c) {
            case '&': result += "&amp;"; break;
            case '<': result += "&lt;"; break;
            case '>': result += "&gt;"; break;
            case '"': result += "&quot;"; break;
            case '\'': result += "&apos;"; break;
            default: result += c; break;
        }
    }
    
    return result;
}

// ============================================================================
// JSON Serializer
// ============================================================================

std::string JsonSerializer::serialize(int request_type, const std::vector<std::map<std::string, ParamValue>>& params) {
    std::ostringstream oss;
    
    oss << "{\"request\":\"" << request_type << "\"";
    
    if (!params.empty()) {
        switch (request_type) {
            case constants::GMS_MSG_DO_INPUT_METHOD_KEY_CODE_SET:
                if (!params.empty()) {
                    auto it = params[0].find("KeyCode");
                    if (it != params[0].end()) {
                        oss << ",\"KeyCode\":\"" << escapeString(std::get<std::string>(it->second)) << "\"";
                    }
                }
                break;
                
            case constants::GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED:
                if (!params.empty()) {
                    auto it_fav = params[0].find("IsFavList");
                    auto it_type = params[0].find("SelectListType");
                    if (it_fav != params[0].end()) {
                        oss << ",\"IsFavList\":\"" << escapeString(std::get<std::string>(it_fav->second)) << "\"";
                    }
                    if (it_type != params[0].end()) {
                        oss << ",\"SelectListType\":\"" << escapeString(std::get<std::string>(it_type->second)) << "\"";
                    }
                }
                break;
                
            case constants::GMS_MSG_DO_FAV_GROUP_RENAME:
                if (!params.empty()) {
                    auto it_pos = params[0].find("FavorRenamePos");
                    auto it_name = params[0].find("FavorNewName");
                    auto it_gid = params[0].find("FavorGroupID");
                    if (it_pos != params[0].end()) {
                        oss << ",\"FavorRenamePos\":\"" << escapeString(std::get<std::string>(it_pos->second)) << "\"";
                    }
                    if (it_name != params[0].end()) {
                        oss << ",\"FavorNewName\":\"" << escapeString(std::get<std::string>(it_name->second)) << "\"";
                    }
                    if (it_gid != params[0].end()) {
                        oss << ",\"FavorGroupID\":\"" << escapeString(std::get<std::string>(it_gid->second)) << "\"";
                    }
                }
                break;
                
            case constants::GMS_MSG_DO_CHANNEL_FAV_MARK:
                if (!params.empty()) {
                    auto it_tv = params[0].find("TvState");
                    auto it_fav = params[0].find("FavMark");
                    auto it_group = params[0].find("FavorGroupID");
                    
                    if (it_tv != params[0].end()) {
                        oss << ",\"TvState\":\"" << escapeString(std::get<std::string>(it_tv->second)) << "\"";
                    }
                    if (it_fav != params[0].end()) {
                        oss << ",\"FavMark\":\"" << escapeString(std::get<std::string>(it_fav->second)) << "\"";
                    }
                    if (it_group != params[0].end()) {
                        oss << ",\"FavorGroupID\":\"" << escapeString(std::get<std::string>(it_group->second)) << "\"";
                    }
                    
                    // ProgramIds array
                    auto it_prog = params[0].find("ProgramIds");
                    if (it_prog != params[0].end()) {
                        oss << ",\"array\":[";
                        std::string prog_ids = std::get<std::string>(it_prog->second);
                        std::istringstream iss(prog_ids);
                        std::string prog_id;
                        bool first = true;
                        while (std::getline(iss, prog_id, ',')) {
                            if (!first) oss << ",";
                            oss << "{\"ProgramId\":\"" << escapeString(prog_id) << "\"}";
                            first = false;
                        }
                        oss << "]";
                    }
                    
                    auto it_total = params[0].find("TotalNum");
                    if (it_total != params[0].end()) {
                        oss << ",\"TotalNum\":\"" << escapeString(std::get<std::string>(it_total->second)) << "\"";
                    }
                }
                break;
                
            case constants::GMS_MSG_REQUEST_CHANNEL_LIST:
                if (params.size() >= 2) {
                    auto it0 = params[0].find("FromIndex");
                    auto it1 = params[1].find("ToIndex");
                    if (it0 != params[0].end()) {
                        oss << ",\"FromIndex\":\"" << escapeString(std::get<std::string>(it0->second)) << "\"";
                    }
                    if (it1 != params[1].end()) {
                        oss << ",\"ToIndex\":\"" << escapeString(std::get<std::string>(it1->second)) << "\"";
                    }
                }
                break;
                
            case constants::GMS_MSG_DO_REMOTE_CONTROL:
                oss << ",\"array\":[";
                for (size_t i = 0; i < params.size(); ++i) {
                    if (i > 0) oss << ",";
                    auto it = params[i].find("KeyValue");
                    if (it != params[i].end()) {
                        oss << "{\"KeyValue\":\"" << escapeString(std::get<std::string>(it->second)) << "\"}";
                    }
                }
                oss << "]";
                break;
                
            case constants::GMS_MSG_DO_CHANNEL_SWITCH:
            case constants::GMS_MSG_DO_SAT2IP_CHANNEL_PLAY:
            case constants::GMS_MSG_REQUEST_PROGRAM_EPG:
                oss << ",\"array\":[";
                for (size_t i = 0; i < params.size(); ++i) {
                    if (i > 0) oss << ",";
                    oss << "{";
                    bool first = true;
                    for (const auto& [key, value] : params[i]) {
                        if (!first) oss << ",";
                        oss << "\"" << key << "\":\"" << escapeString(std::get<std::string>(value)) << "\"";
                        first = false;
                    }
                    oss << "}";
                }
                oss << "]";
                break;
                
            case constants::GMS_MSG_REQUEST_LOGIN_INFO:
                oss << ",\"array\":[";
                for (size_t i = 0; i < params.size(); ++i) {
                    if (i > 0) oss << ",";
                    oss << "{";
                    bool first = true;
                    for (const auto& [key, value] : params[i]) {
                        if (!first) oss << ",";
                        oss << "\"" << key << "\":\"" << escapeString(std::get<std::string>(value)) << "\"";
                        first = false;
                    }
                    oss << "}";
                }
                oss << "]";
                break;
                
            default:
                // Generic: output all params in array
                oss << ",\"array\":[";
                for (size_t i = 0; i < params.size(); ++i) {
                    if (i > 0) oss << ",";
                    oss << "{";
                    bool first = true;
                    for (const auto& [key, value] : params[i]) {
                        if (!first) oss << ",";
                        oss << "\"" << key << "\":\"" << escapeString(std::get<std::string>(value)) << "\"";
                        first = false;
                    }
                    oss << "}";
                }
                oss << "]";
                break;
        }
    }
    
    oss << "}";
    return oss.str();
}

std::string JsonSerializer::serializeSimple(int request_type, const std::string& key, const std::string& value) {
    std::vector<std::map<std::string, ParamValue>> params;
    std::map<std::string, ParamValue> param;
    param[key] = value;
    params.push_back(param);
    return serialize(request_type, params);
}

std::map<std::string, std::string> JsonSerializer::parse(const std::string& json) {
    std::map<std::string, std::string> result;
    // Simplified parser - in production use a proper JSON library
    return result;
}

std::vector<std::map<std::string, std::string>> JsonSerializer::parseArray(const std::string& json) {
    std::vector<std::map<std::string, std::string>> result;
    // Simplified parser - in production use a proper JSON library
    return result;
}

std::string JsonSerializer::escapeString(const std::string& text) {
    std::string result;
    result.reserve(text.size());
    
    for (char c : text) {
        switch (c) {
            case '"': result += "\\\""; break;
            case '\\': result += "\\\\"; break;
            case '\b': result += "\\b"; break;
            case '\f': result += "\\f"; break;
            case '\n': result += "\\n"; break;
            case '\r': result += "\\r"; break;
            case '\t': result += "\\t"; break;
            default: result += c; break;
        }
    }
    
    return result;
}

// ============================================================================
// GCDH Header
// ============================================================================

bool GcdhHeader::parse(const uint8_t* data, size_t length, 
                       uint32_t& out_data_length, 
                       uint32_t& out_command_type,
                       uint32_t& out_response_state) {
    if (!isValid(data, length)) {
        return false;
    }
    
    // Little-endian parsing (as per APK implementation)
    out_data_length = *reinterpret_cast<const uint32_t*>(data + 4);
    out_command_type = *reinterpret_cast<const uint32_t*>(data + 8);
    out_response_state = *reinterpret_cast<const uint32_t*>(data + 12);
    
    return true;
}

bool GcdhHeader::isValid(const uint8_t* data, size_t length) {
    if (!data || length < HEADER_SIZE) {
        return false;
    }
    return std::memcmp(data, MAGIC, 4) == 0;
}

} // namespace protocol
} // namespace stb
