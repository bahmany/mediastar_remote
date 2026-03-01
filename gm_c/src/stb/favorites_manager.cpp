#include "stb/favorites_manager.h"
#include <algorithm>
#include <sstream>
#include <chrono>

// Simple JSON serialization helpers
namespace {

std::string escapeJson(const std::string& str) {
    std::ostringstream oss;
    for (char c : str) {
        switch (c) {
            case '"': oss << "\\\""; break;
            case '\\': oss << "\\\\"; break;
            case '\n': oss << "\\n"; break;
            case '\r': oss << "\\r"; break;
            case '\t': oss << "\\t"; break;
            default: oss << c; break;
        }
    }
    return oss.str();
}

std::string toJsonString(const stb::FavoriteChannel& fav) {
    std::ostringstream oss;
    oss << "{"
        << "\"program_id\":\"" << escapeJson(fav.program_id) << "\","
        << "\"service_index\":" << fav.service_index << ","
        << "\"channel_name\":\"" << escapeJson(fav.channel_name) << "\","
        << "\"channel_number\":\"" << escapeJson(fav.channel_number) << "\","
        << "\"favorite_groups\":[";
    
    bool first = true;
    for (int gid : fav.favorite_groups) {
        if (!first) oss << ",";
        oss << gid;
        first = false;
    }
    
    oss << "],"
        << "\"added_time\":" << fav.added_time
        << "}";
    
    return oss.str();
}

std::string toJsonString(const stb::FavoriteGroup& group) {
    std::ostringstream oss;
    oss << "{"
        << "\"group_id\":" << group.group_id << ","
        << "\"group_name\":\"" << escapeJson(group.group_name) << "\","
        << "\"created_time\":" << group.created_time
        << "}";
    return oss.str();
}

// Simple JSON parsing helpers
std::string extractJsonValue(const std::string& json, const std::string& key) {
    size_t pos = json.find("\"" + key + "\"");
    if (pos == std::string::npos) return "";
    
    pos = json.find(":", pos);
    if (pos == std::string::npos) return "";
    
    pos = json.find_first_of("\"0123456789-[", pos + 1);
    if (pos == std::string::npos) return "";
    
    if (json[pos] == '"') {
        size_t end = json.find("\"", pos + 1);
        if (end == std::string::npos) return "";
        return json.substr(pos + 1, end - pos - 1);
    } else if (json[pos] == '[') {
        // Array - return the bracketed content
        size_t end = json.find("]", pos);
        if (end == std::string::npos) return "";
        return json.substr(pos, end - pos + 1);
    } else {
        size_t end = json.find_first_of(",}", pos);
        if (end == std::string::npos) end = json.length();
        std::string val = json.substr(pos, end - pos);
        // Trim whitespace
        val.erase(0, val.find_first_not_of(" \t"));
        val.erase(val.find_last_not_of(" \t") + 1);
        return val;
    }
}

std::vector<int> parseIntArray(const std::string& str) {
    std::vector<int> result;
    if (str.empty() || str[0] != '[') return result;
    
    std::string inner = str.substr(1, str.find("]") - 1);
    std::istringstream iss(inner);
    std::string val;
    
    while (std::getline(iss, val, ',')) {
        val.erase(0, val.find_first_not_of(" \t"));
        val.erase(val.find_last_not_of(" \t") + 1);
        try {
            result.push_back(std::stoi(val));
        } catch (...) {}
    }
    
    return result;
}

} // anonymous namespace

namespace stb {

FavoritesManager::FavoritesManager() {
    loadDefaults();
}

FavoritesManager::~FavoritesManager() = default;

// Move constructor
FavoritesManager::FavoritesManager(FavoritesManager&& other) noexcept
    : favorite_channels_(std::move(other.favorite_channels_)),
      favorite_groups_(std::move(other.favorite_groups_)) {
    // Note: mutex_ cannot be moved, it is default constructed in the new object
}

// Move assignment
FavoritesManager& FavoritesManager::operator=(FavoritesManager&& other) noexcept {
    if (this != &other) {
        favorite_channels_ = std::move(other.favorite_channels_);
        favorite_groups_ = std::move(other.favorite_groups_);
        // mutex_ cannot be moved, it remains as-is
    }
    return *this;
}

void FavoritesManager::loadDefaults() {
    std::lock_guard<std::mutex> lock(mutex_);
    
    // Create default groups 1-8
    for (int i = 1; i <= 8; ++i) {
        FavoriteGroup group;
        group.group_id = i;
        group.group_name = "Favorites " + std::to_string(i);
        group.created_time = std::chrono::system_clock::now().time_since_epoch().count() / 10000000;
        favorite_groups_[i] = group;
    }
}

bool FavoritesManager::addFavorite(const Channel& channel, const std::vector<int>& group_ids) {
    std::lock_guard<std::mutex> lock(mutex_);
    
    if (channel.program_id.empty() && channel.service_index < 0) {
        return false;
    }
    
    std::string prog_id = channel.program_id;
    if (prog_id.empty()) {
        prog_id = std::to_string(channel.service_index);
    }
    
    // Create or update favorite
    auto it = favorite_channels_.find(prog_id);
    if (it == favorite_channels_.end()) {
        FavoriteChannel fav;
        fav.program_id = prog_id;
        fav.service_index = channel.service_index;
        fav.channel_name = channel.service_name;
        fav.channel_number = channel.service_num;
        if (fav.channel_number.empty()) {
            fav.channel_number = std::to_string(channel.service_index);
        }
        fav.added_time = std::chrono::system_clock::now().time_since_epoch().count() / 10000000;
        favorite_channels_[prog_id] = fav;
        it = favorite_channels_.find(prog_id);
    } else {
        // Update channel info
        it->second.channel_name = channel.service_name;
        it->second.channel_number = channel.service_num;
        it->second.service_index = channel.service_index;
    }
    
    // Add to groups
    std::vector<int> groups_to_add = group_ids;
    if (groups_to_add.empty()) {
        groups_to_add.push_back(1);  // Default to group 1
    }
    
    for (int gid : groups_to_add) {
        if (favorite_groups_.find(gid) != favorite_groups_.end()) {
            it->second.favorite_groups.insert(gid);
        }
    }
    
    return true;
}

bool FavoritesManager::removeFavorite(const std::string& program_id, const std::vector<int>& group_ids) {
    std::lock_guard<std::mutex> lock(mutex_);
    
    auto it = favorite_channels_.find(program_id);
    if (it == favorite_channels_.end()) {
        return false;
    }
    
    if (group_ids.empty()) {
        // Remove from all groups
        favorite_channels_.erase(it);
    } else {
        // Remove from specific groups
        for (int gid : group_ids) {
            it->second.favorite_groups.erase(gid);
        }
        
        // If no groups left, remove entirely
        if (it->second.favorite_groups.empty()) {
            favorite_channels_.erase(it);
        }
    }
    
    return true;
}

bool FavoritesManager::isFavorite(const std::string& program_id, int group_id) const {
    std::lock_guard<std::mutex> lock(mutex_);
    
    auto it = favorite_channels_.find(program_id);
    if (it == favorite_channels_.end()) {
        return false;
    }
    
    if (group_id < 0) {
        // Check if in any group
        return !it->second.favorite_groups.empty();
    }
    
    return it->second.favorite_groups.find(group_id) != it->second.favorite_groups.end();
}

std::vector<FavoriteChannel> FavoritesManager::getFavoritesByGroup(int group_id) const {
    std::lock_guard<std::mutex> lock(mutex_);
    
    std::vector<FavoriteChannel> result;
    
    for (const auto& [prog_id, fav] : favorite_channels_) {
        if (fav.favorite_groups.find(group_id) != fav.favorite_groups.end()) {
            result.push_back(fav);
        }
    }
    
    // Sort by added time
    std::sort(result.begin(), result.end(), 
              [](const FavoriteChannel& a, const FavoriteChannel& b) {
                  return a.added_time < b.added_time;
              });
    
    return result;
}

std::vector<FavoriteChannel> FavoritesManager::getAllFavorites() const {
    std::lock_guard<std::mutex> lock(mutex_);
    
    std::vector<FavoriteChannel> result;
    for (const auto& [prog_id, fav] : favorite_channels_) {
        result.push_back(fav);
    }
    
    return result;
}

std::vector<FavoriteGroup> FavoritesManager::getFavoriteGroups() const {
    std::lock_guard<std::mutex> lock(mutex_);
    
    std::vector<FavoriteGroup> result;
    for (const auto& [gid, group] : favorite_groups_) {
        result.push_back(group);
    }
    
    // Sort by group_id
    std::sort(result.begin(), result.end(),
              [](const FavoriteGroup& a, const FavoriteGroup& b) {
                  return a.group_id < b.group_id;
              });
    
    return result;
}

std::unique_ptr<FavoriteGroup> FavoritesManager::createGroup(const std::string& group_name) {
    std::lock_guard<std::mutex> lock(mutex_);
    
    int new_id = getNextGroupId();
    
    FavoriteGroup group;
    group.group_id = new_id;
    group.group_name = group_name;
    group.created_time = std::chrono::system_clock::now().time_since_epoch().count() / 10000000;
    
    favorite_groups_[new_id] = group;
    
    return std::make_unique<FavoriteGroup>(group);
}

bool FavoritesManager::deleteGroup(int group_id) {
    std::lock_guard<std::mutex> lock(mutex_);
    
    if (favorite_groups_.find(group_id) == favorite_groups_.end()) {
        return false;
    }
    
    // Remove all channels from this group
    std::vector<std::string> to_remove;
    for (auto& [prog_id, fav] : favorite_channels_) {
        fav.favorite_groups.erase(group_id);
        if (fav.favorite_groups.empty()) {
            to_remove.push_back(prog_id);
        }
    }
    
    // Remove orphaned channels
    for (const auto& prog_id : to_remove) {
        favorite_channels_.erase(prog_id);
    }
    
    // Delete the group
    favorite_groups_.erase(group_id);
    
    return true;
}

bool FavoritesManager::renameGroup(int group_id, const std::string& new_name) {
    std::lock_guard<std::mutex> lock(mutex_);
    
    auto it = favorite_groups_.find(group_id);
    if (it == favorite_groups_.end()) {
        return false;
    }
    
    it->second.group_name = new_name;
    return true;
}

std::string FavoritesManager::exportToJson() const {
    std::lock_guard<std::mutex> lock(mutex_);
    
    std::ostringstream oss;
    oss << "{"
        << "\"favorite_channels\":{";
    
    bool first = true;
    for (const auto& [prog_id, fav] : favorite_channels_) {
        if (!first) oss << ",";
        oss << "\"" << escapeJson(prog_id) << "\":" << toJsonString(fav);
        first = false;
    }
    
    oss << "},"
        << "\"favorite_groups\":{";
    
    first = true;
    for (const auto& [gid, group] : favorite_groups_) {
        if (!first) oss << ",";
        oss << "\"" << gid << "\":" << toJsonString(group);
        first = false;
    }
    
    oss << "},"
        << "\"export_time\":" << std::chrono::system_clock::now().time_since_epoch().count() / 10000000
        << "}";
    
    return oss.str();
}

bool FavoritesManager::importFromJson(const std::string& json) {
    std::lock_guard<std::mutex> lock(mutex_);
    
    try {
        // Parse groups
        std::string groups_json = extractJsonValue(json, "favorite_groups");
        if (!groups_json.empty()) {
            // Extract individual groups
            size_t pos = 0;
            while ((pos = groups_json.find("{", pos)) != std::string::npos) {
                size_t end = groups_json.find("}", pos);
                if (end == std::string::npos) break;
                
                std::string group_str = groups_json.substr(pos, end - pos + 1);
                
                std::string id_str = extractJsonValue(group_str, "group_id");
                std::string name = extractJsonValue(group_str, "group_name");
                std::string time_str = extractJsonValue(group_str, "created_time");
                
                if (!id_str.empty() && !name.empty()) {
                    FavoriteGroup group;
                    group.group_id = std::stoi(id_str);
                    group.group_name = name;
                    group.created_time = std::stoll(time_str);
                    favorite_groups_[group.group_id] = group;
                }
                
                pos = end + 1;
            }
        }
        
        // Parse channels
        std::string channels_json = extractJsonValue(json, "favorite_channels");
        if (!channels_json.empty()) {
            size_t pos = 0;
            while ((pos = channels_json.find("{", pos)) != std::string::npos) {
                size_t end = channels_json.find("}", pos);
                if (end == std::string::npos) break;
                
                std::string ch_str = channels_json.substr(pos, end - pos + 1);
                
                std::string prog_id = extractJsonValue(ch_str, "program_id");
                std::string idx_str = extractJsonValue(ch_str, "service_index");
                std::string name = extractJsonValue(ch_str, "channel_name");
                std::string num = extractJsonValue(ch_str, "channel_number");
                std::string time_str = extractJsonValue(ch_str, "added_time");
                std::string groups_str = extractJsonValue(ch_str, "favorite_groups");
                
                if (!prog_id.empty()) {
                    FavoriteChannel fav;
                    fav.program_id = prog_id;
                    fav.service_index = std::stoi(idx_str);
                    fav.channel_name = name;
                    fav.channel_number = num;
                    fav.added_time = std::stoll(time_str);
                    fav.favorite_groups = std::set<int>(parseIntArray(groups_str).begin(), 
                                                          parseIntArray(groups_str).end());
                    favorite_channels_[prog_id] = fav;
                }
                
                pos = end + 1;
            }
        }
        
        return true;
    } catch (...) {
        return false;
    }
}

FavoritesManager::Summary FavoritesManager::getSummary() const {
    std::lock_guard<std::mutex> lock(mutex_);
    
    Summary summary;
    summary.total_favorites = static_cast<int>(favorite_channels_.size());
    summary.total_groups = static_cast<int>(favorite_groups_.size());
    
    for (const auto& [gid, group] : favorite_groups_) {
        std::vector<FavoriteChannel> group_favs;
        for (const auto& [prog_id, fav] : favorite_channels_) {
            if (fav.favorite_groups.find(gid) != fav.favorite_groups.end()) {
                group_favs.push_back(fav);
            }
        }
        summary.groups.push_back({group, group_favs});
    }
    
    return summary;
}

int FavoritesManager::getNextGroupId() const {
    int max_id = 0;
    for (const auto& [gid, _] : favorite_groups_) {
        if (gid > max_id) {
            max_id = gid;
        }
    }
    return max_id + 1;
}

} // namespace stb
