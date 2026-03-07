#include "stb/message_processor.h"
#include "stb/compression.h"
#include "stb/serialization.h"
#include "stb/constants.h"
#include "stb/crash_log.h"
#include <algorithm>
#include <sstream>

// Simple JSON parser helper - in production, use a proper library like nlohmann/json
namespace {

// Parse a simple key=value or "key":"value" pair
std::string extractJsonString(const std::string& json, const std::string& key) {
    size_t pos = json.find("\"" + key + "\"");
    if (pos == std::string::npos) return "";
    
    pos = json.find(":", pos);
    if (pos == std::string::npos) return "";
    
    pos = json.find_first_of("\"0123456789-", pos + 1);
    if (pos == std::string::npos) return "";
    
    if (json[pos] == '"') {
        // String value
        size_t end = json.find("\"", pos + 1);
        if (end == std::string::npos) return "";
        return json.substr(pos + 1, end - pos - 1);
    } else {
        // Numeric value
        size_t end = json.find_first_of(",}]", pos);
        if (end == std::string::npos) end = json.length();
        return json.substr(pos, end - pos);
    }
}

// Parse objects out of a JSON string that contains an array region.
// The caller provides the start of the '[' and this function extracts all objects.
std::vector<std::map<std::string, std::string>> parseObjectsInArray(const std::string& json, size_t arr_start) {
    std::vector<std::map<std::string, std::string>> result;
    
    // Find matching ']' – handle nested brackets
    size_t arr_end = std::string::npos;
    int bracket = 0;
    for (size_t i = arr_start; i < json.size(); ++i) {
        if (json[i] == '[') bracket++;
        else if (json[i] == ']') { bracket--; if (bracket == 0) { arr_end = i; break; } }
    }
    if (arr_end == std::string::npos) arr_end = json.size();
    
    std::string content = json.substr(arr_start + 1, arr_end - arr_start - 1);
    
    size_t obj_start = 0;
    int brace_count = 0;
    for (size_t i = 0; i < content.length(); ++i) {
        if (content[i] == '{') {
            if (brace_count == 0) obj_start = i;
            brace_count++;
        } else if (content[i] == '}') {
            brace_count--;
            if (brace_count == 0) {
                std::string obj = content.substr(obj_start + 1, i - obj_start - 1);
                std::map<std::string, std::string> obj_map;
                
                size_t kv_pos = 0;
                while (kv_pos < obj.length()) {
                    size_t key_start = obj.find('"', kv_pos);
                    if (key_start == std::string::npos) break;
                    size_t key_end = obj.find('"', key_start + 1);
                    if (key_end == std::string::npos) break;
                    
                    std::string key = obj.substr(key_start + 1, key_end - key_start - 1);
                    
                    size_t val_sep = obj.find(':', key_end);
                    if (val_sep == std::string::npos) break;
                    
                    // Skip whitespace and tabs after ':'
                    size_t val_start = obj.find_first_not_of(" \t\r\n", val_sep + 1);
                    if (val_start == std::string::npos) break;
                    
                    std::string val;
                    if (obj[val_start] == '"') {
                        size_t val_end = obj.find('"', val_start + 1);
                        if (val_end == std::string::npos) break;
                        val = obj.substr(val_start + 1, val_end - val_start - 1);
                        kv_pos = val_end + 1;
                    } else if (obj[val_start] == '[' || obj[val_start] == '{') {
                        // Nested JSON array/object value (e.g. AudioArray/SubtArray)
                        // Parse until matching closing bracket/brace, handling strings and escapes.
                        char openC = obj[val_start];
                        char closeC = (openC == '[') ? ']' : '}';
                        int depth = 0;
                        bool inStr2 = false;
                        size_t i2 = val_start;
                        for (; i2 < obj.length(); ++i2) {
                            char c2 = obj[i2];
                            if (inStr2) {
                                if (c2 == '\\' && i2 + 1 < obj.length()) { i2++; continue; }
                                if (c2 == '"') inStr2 = false;
                                continue;
                            }
                            if (c2 == '"') { inStr2 = true; continue; }
                            if (c2 == openC) depth++;
                            else if (c2 == closeC) {
                                depth--;
                                if (depth == 0) { i2++; break; }
                            }
                        }
                        if (i2 > obj.length()) break;
                        val = obj.substr(val_start, i2 - val_start);
                        kv_pos = i2;
                    } else {
                        size_t val_end = obj.find_first_of(",}\r\n", val_start);
                        if (val_end == std::string::npos) val_end = obj.length();
                        val = obj.substr(val_start, val_end - val_start);
                        val.erase(0, val.find_first_not_of(" \t"));
                        val.erase(val.find_last_not_of(" \t") + 1);
                        kv_pos = val_end;
                    }
                    
                    obj_map[key] = val;
                }
                
                if (!obj_map.empty()) {
                    result.push_back(obj_map);
                }
            }
        }
    }
    
    return result;
}

std::map<std::string, std::string> parseXmlSimple(const std::string& xml);

std::vector<std::map<std::string, std::string>> parseXmlParmArray(const std::string& xml) {
    std::vector<std::map<std::string, std::string>> out;

    size_t pos = 0;
    while ((pos = xml.find("<parm", pos)) != std::string::npos) {
        size_t tag_end = xml.find('>', pos);
        if (tag_end == std::string::npos) break;
        size_t close_pos = xml.find("</parm>", tag_end);
        if (close_pos == std::string::npos) break;

        std::string inner = xml.substr(tag_end + 1, close_pos - tag_end - 1);
        auto m = parseXmlSimple(inner);
        if (!m.empty()) out.push_back(std::move(m));
        pos = close_pos + 7;
    }
    return out;
}

// Extract array from JSON. If array_key is empty, looks for a bare array.
std::vector<std::map<std::string, std::string>> extractJsonArray(const std::string& json, const std::string& array_key) {
    // First try keyed: "array_key": [...]
    if (!array_key.empty()) {
        size_t pos = json.find("\"" + array_key + "\"");
        if (pos != std::string::npos) {
            pos = json.find("[", pos);
            if (pos != std::string::npos)
                return parseObjectsInArray(json, pos);
        }
    }
    
    // Fall back to bare array: find first '['
    size_t pos = json.find('[');
    if (pos != std::string::npos)
        return parseObjectsInArray(json, pos);
    
    return {};
}

// Parse simple XML to map
std::map<std::string, std::string> parseXmlSimple(const std::string& xml) {
    std::map<std::string, std::string> result;
    
    size_t pos = 0;
    while ((pos = xml.find("<", pos)) != std::string::npos) {
        size_t end_tag = xml.find(">", pos);
        if (end_tag == std::string::npos) break;
        
        std::string tag = xml.substr(pos + 1, end_tag - pos - 1);
        
        // Skip closing tags and special tags
        if (tag.empty() || tag[0] == '/' || tag[0] == '?' || tag[0] == '!') {
            pos = end_tag + 1;
            continue;
        }
        
        // Find closing tag
        std::string close_tag = "</" + tag + ">";
        size_t close_pos = xml.find(close_tag, end_tag);
        if (close_pos == std::string::npos) {
            pos = end_tag + 1;
            continue;
        }
        
        std::string value = xml.substr(end_tag + 1, close_pos - end_tag - 1);
        result[tag] = value;
        pos = close_pos + close_tag.length();
    }
    
    return result;
}

int parseInt(const std::string& str, int default_val = -1) {
    try {
        return std::stoi(str);
    } catch (...) {
        return default_val;
    }
}

} // anonymous namespace

namespace stb {

// ============================================================================
// MessageProcessor Implementation
// ============================================================================

MessageProcessor::MessageProcessor() = default;

MessageProcessor::~MessageProcessor() = default;

// Move constructor
MessageProcessor::MessageProcessor(MessageProcessor&& other) noexcept
    : state_(std::move(other.state_)),
      callback_(std::move(other.callback_)),
      use_json_(other.use_json_),
      channel_list_waiting_(other.channel_list_waiting_),
      last_batch_size_(other.last_batch_size_),
      expected_total_channels_(other.expected_total_channels_) {
    // Note: mutex_ and condition_variable_ are default constructed in the new object
    // They cannot be moved, so we just leave them as new instances
}

// Move assignment
MessageProcessor& MessageProcessor::operator=(MessageProcessor&& other) noexcept {
    if (this != &other) {
        state_ = std::move(other.state_);
        callback_ = std::move(other.callback_);
        use_json_ = other.use_json_;
        channel_list_waiting_ = other.channel_list_waiting_;
        last_batch_size_ = other.last_batch_size_;
        expected_total_channels_ = other.expected_total_channels_;
        // mutex_ and condition_variable_ cannot be moved, they remain as-is
    }
    return *this;
}

void MessageProcessor::setCallback(StateChangeCallback callback) {
    callback_ = callback;
}

void MessageProcessor::startChannelListWait() {
    std::lock_guard<std::mutex> lock(mutex_);
    channel_list_waiting_ = true;
    last_batch_size_ = 0;
    expected_total_channels_ = 0;
    batch_counter_ = 0;
    batch_wait_target_ = 0;
}

bool MessageProcessor::waitForChannelList(int timeout_ms) {
    std::unique_lock<std::mutex> lock(mutex_);
    int target = batch_wait_target_;
    
    // Wake up when ANY new batch arrives OR when complete
    auto result = channel_cv_.wait_for(lock, std::chrono::milliseconds(timeout_ms),
                                       [this, target] {
                                           return !channel_list_waiting_ || batch_counter_ > target;
                                       });
    
    // Advance target so next call waits for the NEXT batch
    batch_wait_target_ = batch_counter_;
    return result;
}

void MessageProcessor::onChannelBatchReceived(int batch_size) {
    std::lock_guard<std::mutex> lock(mutex_);
    last_batch_size_ = batch_size;
    batch_counter_++;
    channel_cv_.notify_all();
}

bool MessageProcessor::processMessage(const protocol::ReceivedMessage& msg) {
    try {
    return processMessageInner(msg);
    } catch (...) { return false; }
}

bool MessageProcessor::processMessageInner(const protocol::ReceivedMessage& msg) {
    std::string data_str(msg.data.begin(), msg.data.end());
    
    // Parse based on command type
    uint32_t cmd = msg.command_type;
    
    // Response command types
    constexpr uint32_t CMD_CHANNEL_LIST = 0x00;
    constexpr uint32_t CMD_CURRENT_CHANNEL = 0x03;
    constexpr uint32_t CMD_EPG_DATA = 0x05;
    constexpr uint32_t CMD_FAV_GROUP_NAMES = 0x0C;
    constexpr uint32_t CMD_CHANNEL_LIST_TYPE = 0x0E;
    constexpr uint32_t CMD_STB_INFO = 0x0F;
    constexpr uint32_t CMD_SAT2IP_RETURN = 0x10;
    constexpr uint32_t CMD_SAT_LIST = 0x16;
    constexpr uint32_t CMD_TP_LIST = 0x18;
    constexpr uint32_t CMD_KEEP_ALIVE = 0x1A;
    constexpr uint32_t CMD_CHANNEL_LIST_UPDATE = 0x3F2;  // 1010
    
    // Notifications
    constexpr uint32_t CMD_NOTIFY_CHANNEL_CHANGED = 0x7D1;     // 2001
    constexpr uint32_t CMD_NOTIFY_CHANNEL_LIST = 0x7D2;        // 2002
    constexpr uint32_t CMD_NOTIFY_SAT_LIST = 0x7E3;          // 2019
    constexpr uint32_t CMD_NOTIFY_INPUT_METHOD_POPUP = 0x7DB;
    constexpr uint32_t CMD_NOTIFY_INPUT_METHOD_DISMISS = 0x7DC;
    constexpr uint32_t CMD_NOTIFY_FAV_GROUP_NAME_CHANGED = 0x7DD;
    
    bool handled = true;
    
    if (cmd == CMD_CHANNEL_LIST || cmd == CMD_CHANNEL_LIST_UPDATE) {
        // Try keyed arrays first, then bare array, then XML
        auto parsed = extractJsonArray(data_str, "array");
        if (parsed.empty()) parsed = extractJsonArray(data_str, "parm");
        if (parsed.empty()) parsed = extractJsonArray(data_str, ""); // bare array
        if (parsed.empty()) {
            parsed = parseXmlParmArray(data_str);
            if (parsed.empty()) {
                auto simple = parseXmlSimple(data_str);
                if (!simple.empty()) parsed.push_back(simple);
            }
        }
        handleChannelList(parsed);
    } else if (cmd == CMD_FAV_GROUP_NAMES) {
        auto parsed = extractJsonArray(data_str, "array");
        if (parsed.empty()) parsed = extractJsonArray(data_str, ""); // bare array
        handleFavoriteGroups(parsed);
    } else if (cmd == CMD_NOTIFY_CHANNEL_LIST) {
        notify("channel_list_changed", "");
    } else if (cmd == CMD_SAT_LIST || cmd == CMD_NOTIFY_SAT_LIST) {
        auto parsed = extractJsonArray(data_str, "array");
        if (parsed.empty()) parsed = extractJsonArray(data_str, ""); // bare array
        if (parsed.empty()) parsed = parseXmlParmArray(data_str);
        handleSatelliteList(parsed);
    } else if (cmd == CMD_TP_LIST) {
        auto parsed = extractJsonArray(data_str, "array");
        if (parsed.empty()) parsed = extractJsonArray(data_str, "parm");
        if (parsed.empty()) parsed = extractJsonArray(data_str, ""); // bare array
        if (parsed.empty()) {
            parsed = parseXmlParmArray(data_str);
            if (parsed.empty()) {
                auto simple = parseXmlSimple(data_str);
                if (!simple.empty()) parsed.push_back(simple);
            }
        }
        handleTransponderList(parsed);
    } else if (cmd == CMD_EPG_DATA) {
        auto parsed = extractJsonArray(data_str, "array");
        if (parsed.empty()) parsed = extractJsonArray(data_str, ""); // bare array
        handleEpgData(parsed);
    } else if (cmd == CMD_SAT2IP_RETURN) {
        std::map<std::string, std::string> parsed = parseXmlSimple(data_str);
        if (parsed.empty()) {
            auto arr = extractJsonArray(data_str, "");
            if (!arr.empty()) parsed = arr.front();
        }
        if (parsed.empty()) {
            auto arr = parseXmlParmArray(data_str);
            if (!arr.empty()) parsed = arr.front();
        }
        handleSat2ipReturn(parsed);
    } else if (cmd == CMD_CURRENT_CHANNEL) {
        auto parsed = parseXmlSimple(data_str);
        if (parsed.empty()) {
            // Try JSON
            std::string data = extractJsonString(data_str, "Data");
            if (!data.empty()) parsed["Data"] = data;
        }
        handleCurrentChannel(parsed);
    } else if (cmd == CMD_NOTIFY_CHANNEL_CHANGED) {
        notify("current_channel_changed", "");
    } else if (cmd == CMD_STB_INFO) {
        // STB info can come as JSON array [{...}] or XML
        auto parsed = parseXmlSimple(data_str);
        if (parsed.empty()) {
            // Try JSON – STB sends [{"StbStatus":1, "ProductName":"MS-4030", ...}]
            auto arr = extractJsonArray(data_str, "");
            if (!arr.empty()) parsed = arr[0]; // first object
            else {
                // Fallback: extract individual keys
                for (const auto& key : {"ChannelNum", "ProductName", "SoftwareVersion",
                                        "SerialNumber", "MaxNumOfPrograms", "StbStatus",
                                        "Model", "SoftwareVer", "RadioNum", "STBTime"}) {
                    std::string val = extractJsonString(data_str, key);
                    if (!val.empty()) parsed[key] = val;
                }
            }
        }
        handleStbInfo(parsed);
    } else if (cmd == CMD_KEEP_ALIVE) {
        // Keep-alive response, ignore
    } else if (cmd == CMD_NOTIFY_INPUT_METHOD_POPUP) {
        notify("input_method_popup", "");
    } else if (cmd == CMD_NOTIFY_INPUT_METHOD_DISMISS) {
        notify("input_method_dismiss", "");
    } else if (cmd == CMD_NOTIFY_FAV_GROUP_NAME_CHANGED) {
        notify("fav_group_name_changed", "");
    } else {
        handled = false;
    }
    
    return handled;
}

void MessageProcessor::handleChannelList(const std::vector<std::map<std::string, std::string>>& data) {
    if (data.empty()) {
        return;
    }
    
    bool reset = false;
    int batch_size = 0;
    
    auto getInt = [&](const std::map<std::string,std::string>& m, const char* k, int def=0) -> int {
        auto it = m.find(k); return it != m.end() ? parseInt(it->second, def) : def;
    };
    auto getStr = [&](const std::map<std::string,std::string>& m, const char* k) -> std::string {
        auto it = m.find(k); return it != m.end() ? it->second : std::string();
    };
    auto getBool = [&](const std::map<std::string,std::string>& m, const char* k) -> bool {
        return getInt(m, k, 0) != 0;
    };

    for (const auto& item : data) {
        Channel ch;
        
        // Core
        ch.service_index  = getInt(item, "ServiceIndex", -1);
        ch.service_id     = getStr(item, "ServiceID");
        ch.service_name   = getStr(item, "ServiceName");
        
        // Type flags
        ch.is_radio       = getBool(item, "Radio");
        ch.is_hd          = getBool(item, "HD");
        ch.is_scrambled   = getBool(item, "Scramble");
        ch.is_locked      = getBool(item, "Lock");
        ch.has_epg        = getBool(item, "EPG");
        ch.is_playing     = getBool(item, "Playing");
        ch.will_be_played = getBool(item, "WillBePlayed");
        ch.is_tuner2      = getBool(item, "IsTuner2");
        
        // Favorite
        ch.fav_bit        = getInt(item, "FavBit");
        ch.is_favorite    = (ch.fav_bit != 0);
        
        // PIDs
        ch.video_pid      = getInt(item, "VideoPID");
        ch.pmt_pid        = getInt(item, "PMTPID");
        ch.pcr_pid        = getInt(item, "PID");
        ch.ttx_pid        = getInt(item, "TTXPID");
        ch.audio_pids_raw = getStr(item, "AudioArray");
        ch.subtitle_pids_raw = getStr(item, "SubtArray");
        
        // Modulation / transponder
        ch.modulation_system = getInt(item, "ModulationSystem");
        ch.modulation_type   = getInt(item, "ModulationType");
        ch.roll_off          = getInt(item, "RollOff");
        ch.pilot_tones       = getInt(item, "PilotTones");
        
        // Legacy compat
        ch.program_id    = ch.service_id;
        ch.channel_type  = ch.is_radio ? 1 : 0;
        ch.is_fta        = !ch.is_scrambled;
        
        state_.channels.push_back(std::move(ch));
        batch_size++;
    }
    
    // Notify progress
    notify("channel_list_progress", std::to_string(state_.channels.size()));
    
    // Signal batch received
    onChannelBatchReceived(batch_size);
    
    // Store last batch parse count so requestChannelList can check it
    last_batch_size_ = batch_size;

    // Check if complete
    bool complete = false;
    if (state_.stb_info.channel_count > 0 && 
        state_.channels.size() >= static_cast<size_t>(state_.stb_info.channel_count)) {
        complete = true;
    } else if (batch_size > 0 && batch_size < 100) {  // Last batch is typically smaller
        complete = true;
    }
    
    if (complete) {
        // Cross-reference channels with satellite list to populate satellite_name
        if (!state_.satellites.empty()) {
            for (auto& ch : state_.channels) {
                int si = ch.satIndex();
                if (si >= 0 && ch.satellite_name.empty()) {
                    ch.satellite_index = si;
                    for (const auto& sat : state_.satellites) {
                        if (sat.sat_index == si) {
                            ch.satellite_name = sat.sat_name;
                            break;
                        }
                    }
                }
            }
        }
        
        state_.channels_complete = true;
        state_.channels_cache_time = std::chrono::system_clock::now().time_since_epoch().count() / 10000000;
        {
            std::lock_guard<std::mutex> lock(mutex_);
            channel_list_waiting_ = false;
        }
        channel_cv_.notify_all();
        notify("channel_list_complete", std::to_string(state_.channels.size()));
    }
}

void MessageProcessor::handleFavoriteGroups(const std::vector<std::map<std::string, std::string>>& data) {
    state_.fav_groups.clear();
    
    for (const auto& item : data) {
        FavoriteGroup group;
        
        auto it = item.find("FavorGroupID");
        if (it != item.end()) {
            group.group_id = parseInt(it->second, 0);
        }
        
        it = item.find("favorGroupName");
        if (it != item.end()) {
            group.group_name = it->second;
        }
        
        if (group.group_id > 0 && !group.group_name.empty()) {
            state_.fav_groups.push_back(group);
        }
    }
    
    notify("fav_groups", "");
}

void MessageProcessor::handleSatelliteList(const std::vector<std::map<std::string, std::string>>& data) {
    state_.satellites.clear();
    
    for (const auto& item : data) {
        Satellite sat;
        
        auto it = item.find("SatIndex");
        if (it != item.end()) {
            sat.sat_index = parseInt(it->second, -1);
        }
        
        it = item.find("SatName");
        if (it != item.end()) {
            sat.sat_name = it->second;
        }
        
        it = item.find("SatPos");
        if (it != item.end()) {
            sat.sat_position = parseInt(it->second, 0);
        }
        
        it = item.find("SatIsEast");
        if (it != item.end()) {
            sat.is_east = parseInt(it->second, 0) != 0;
        }
        
        if (sat.sat_index >= 0) {
            state_.satellites.push_back(sat);
        }
    }
    
    notify("satellite_list", "");
}

void MessageProcessor::handleTransponderList(const std::vector<std::map<std::string, std::string>>& data) {
    state_.transponders.clear();

    auto getIntAny = [&](const std::map<std::string,std::string>& m,
                         const std::initializer_list<const char*>& keys,
                         int def = 0) -> int {
        for (auto* k : keys) {
            auto it = m.find(k);
            if (it != m.end()) return parseInt(it->second, def);
        }
        return def;
    };

    for (const auto& item : data) {
        Transponder tp;
        tp.tp_index = getIntAny(item, {"TpIndex", "TPIndex"}, -1);
        tp.sat_index = getIntAny(item, {"SatIndex"}, -1);
        tp.sym_rate = getIntAny(item, {"SystemRate", "SR"}, 0);
        tp.fec = getIntAny(item, {"Fec", "FEC"}, 0);
        tp.freq = getIntAny(item, {"Freq"}, 0);

        int polInt = getIntAny(item, {"Pol", "POL"}, 0);
        switch (polInt) {
            case 0: tp.pol = 'h'; break;
            case 1: tp.pol = 'v'; break;
            case 2: tp.pol = 'l'; break;
            case 3: tp.pol = 'r'; break;
            default: tp.pol = 'h'; break;
        }

        if (tp.tp_index >= 0 && tp.sat_index >= 0) {
            state_.transponders.push_back(tp);
        }
    }

    notify("transponder_list", "");
}

void MessageProcessor::handleSat2ipReturn(const std::map<std::string, std::string>& data) {
    auto itS = data.find("success");
    auto itU = data.find("url");
    auto itE = data.find("errormsg");

    std::string out;
    if (itS != data.end()) out += "success=" + itS->second;
    if (itU != data.end()) {
        if (!out.empty()) out += ";";
        out += "url=" + itU->second;
    }
    if (itE != data.end() && !itE->second.empty()) {
        if (!out.empty()) out += ";";
        out += "errormsg=" + itE->second;
    }

    notify("sat2ip_return", out);
}

void MessageProcessor::handleEpgData(const std::vector<std::map<std::string, std::string>>& data) {
    state_.epg_events.clear();
    
    for (const auto& item : data) {
        EpgEvent event;
        
        auto it = item.find("EventId");
        if (it != item.end()) {
            event.event_id = it->second;
        }
        
        it = item.find("ProgramId");
        if (it != item.end()) {
            event.program_id = it->second;
        }
        
        it = item.find("EventName");
        if (it != item.end()) {
            event.event_name = it->second;
        }
        
        it = item.find("EventDesc");
        if (it != item.end()) {
            event.description = it->second;
        }
        
        it = item.find("StartTime");
        if (it != item.end()) {
            event.start_time = parseInt(it->second, 0);
        }
        
        it = item.find("EndTime");
        if (it != item.end()) {
            event.end_time = parseInt(it->second, 0);
        }
        
        if (!event.event_id.empty()) {
            state_.epg_events.push_back(event);
        }
    }
    
    notify("epg_data", "");
}

void MessageProcessor::handleCurrentChannel(const std::map<std::string, std::string>& data) {
    auto it = data.find("Data");
    if (it != data.end()) {
        state_.current_program_id = it->second;
        
        // Try to find channel index from program_id
        for (const auto& ch : state_.channels) {
            if (ch.program_id == it->second) {
                state_.current_channel_index = ch.service_index;
                break;
            }
        }
    }
    
    it = data.find("ProgramIndex");
    if (it != data.end()) {
        state_.current_channel_index = parseInt(it->second, 0);
    }
    
    it = data.find("ServiceIndex");
    if (it != data.end()) {
        state_.current_channel_index = parseInt(it->second, 0);
    }
    
    notify("current_channel", "");
}

void MessageProcessor::handleStbInfo(const std::map<std::string, std::string>& data) {
    // ChannelNum
    auto it = data.find("ChannelNum");
    if (it != data.end()) state_.stb_info.channel_count = parseInt(it->second, 0);
    
    // RadioNum
    it = data.find("RadioNum");
    if (it != data.end()) state_.stb_info.radio_count = parseInt(it->second, 0);
    
    // Model: try "ProductName" first, then "Model"
    it = data.find("ProductName");
    if (it != data.end()) state_.stb_info.stb_model = it->second;
    else {
        it = data.find("Model");
        if (it != data.end()) state_.stb_info.stb_model = it->second;
    }
    
    // SW Version: try "SoftwareVersion" first, then "SoftwareVer"
    it = data.find("SoftwareVersion");
    if (it != data.end()) state_.stb_info.sw_version = it->second;
    else {
        it = data.find("SoftwareVer");
        if (it != data.end()) state_.stb_info.sw_version = it->second;
    }
    
    // STB Time
    it = data.find("STBTime");
    if (it != data.end()) state_.stb_info.stb_time = it->second;
    
    // SerialNumber
    it = data.find("SerialNumber");
    if (it != data.end()) state_.stb_info.serial_number = it->second;
    
    // MaxNumOfPrograms
    it = data.find("MaxNumOfPrograms");
    if (it != data.end()) state_.stb_info.max_programs = parseInt(it->second, 0);
    
    
    notify("stb_info", "");
}

void MessageProcessor::notify(const std::string& event, const std::string& data) {
    if (callback_) {
        callback_(event, data);
    }
}

} // namespace stb
