#pragma once

#include <string>
#include <vector>
#include <map>
#include <set>
#include <memory>
#include <mutex>

#include "stb/models.h"

namespace stb {

/**
 * @brief Manages favorite channels and groups
 * 
 * Thread-safe manager for favorite channels with persistence support.
 * Default groups: Favorites 1-8
 */
class FavoritesManager {
public:
    FavoritesManager();
    ~FavoritesManager();
    
    // Move constructor and assignment
    FavoritesManager(FavoritesManager&& other) noexcept;
    FavoritesManager& operator=(FavoritesManager&& other) noexcept;
    
    // Disable copy
    FavoritesManager(const FavoritesManager&) = delete;
    FavoritesManager& operator=(const FavoritesManager&) = delete;
    
    /**
     * @brief Add a channel to favorites
     * @param channel Channel to add
     * @param group_ids Group IDs to add to (empty = group 1)
     * @return true on success
     */
    bool addFavorite(const Channel& channel, const std::vector<int>& group_ids = {});
    
    /**
     * @brief Remove a channel from favorites
     * @param program_id Program ID to remove
     * @param group_ids Specific groups to remove from (empty = all)
     * @return true on success
     */
    bool removeFavorite(const std::string& program_id, const std::vector<int>& group_ids = {});
    
    /**
     * @brief Check if a channel is in favorites
     * @param program_id Program ID to check
     * @param group_id Specific group to check (-1 = any)
     * @return true if favorite
     */
    bool isFavorite(const std::string& program_id, int group_id = -1) const;
    
    /**
     * @brief Get favorites by group
     * @param group_id Group ID
     * @return List of favorite channels in group
     */
    std::vector<FavoriteChannel> getFavoritesByGroup(int group_id) const;
    
    /**
     * @brief Get all favorites
     */
    std::vector<FavoriteChannel> getAllFavorites() const;
    
    /**
     * @brief Get favorite groups
     */
    std::vector<FavoriteGroup> getFavoriteGroups() const;
    
    /**
     * @brief Create a new favorite group
     * @param group_name Name for new group
     * @return Group info or nullptr if failed
     */
    std::unique_ptr<FavoriteGroup> createGroup(const std::string& group_name);
    
    /**
     * @brief Delete a favorite group
     * @param group_id Group to delete
     * @return true on success
     */
    bool deleteGroup(int group_id);
    
    /**
     * @brief Rename a favorite group
     * @param group_id Group to rename
     * @param new_name New name
     * @return true on success
     */
    bool renameGroup(int group_id, const std::string& new_name);
    
    /**
     * @brief Export favorites to JSON string
     */
    std::string exportToJson() const;
    
    /**
     * @brief Import favorites from JSON string
     * @param json JSON data
     * @return true on success
     */
    bool importFromJson(const std::string& json);
    
    /**
     * @brief Get favorites summary
     */
    struct Summary {
        int total_favorites = 0;
        int total_groups = 0;
        std::vector<std::pair<FavoriteGroup, std::vector<FavoriteChannel>>> groups;
    };
    Summary getSummary() const;
    
private:
    mutable std::mutex mutex_;
    std::map<std::string, FavoriteChannel> favorite_channels_;  // by program_id
    std::map<int, FavoriteGroup> favorite_groups_;             // by group_id
    
    void loadDefaults();
    int getNextGroupId() const;
};

} // namespace stb
