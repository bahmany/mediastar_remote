"""Favorite channels management for STB client"""
import logging
import time
from dataclasses import dataclass, field
from typing import Any, Dict, List, Optional, Set

logger = logging.getLogger(__name__)


@dataclass
class FavoriteChannel:
    """Represents a favorite channel"""
    program_id: str
    service_index: int
    channel_name: str
    channel_number: str
    favorite_groups: Set[int] = field(default_factory=set)
    added_time: float = field(default_factory=time.time)
    
    def to_dict(self) -> Dict[str, Any]:
        return {
            "program_id": self.program_id,
            "service_index": self.service_index,
            "channel_name": self.channel_name,
            "channel_number": self.channel_number,
            "favorite_groups": list(self.favorite_groups),
            "added_time": self.added_time
        }
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'FavoriteChannel':
        return cls(
            program_id=data["program_id"],
            service_index=data["service_index"],
            channel_name=data["channel_name"],
            channel_number=data["channel_number"],
            favorite_groups=set(data.get("favorite_groups", [])),
            added_time=data.get("added_time", time.time())
        )


@dataclass
class FavoriteGroup:
    """Represents a favorite group"""
    group_id: int
    group_name: str
    created_time: float = field(default_factory=time.time)
    
    def to_dict(self) -> Dict[str, Any]:
        return {
            "group_id": self.group_id,
            "group_name": self.group_name,
            "created_time": self.created_time
        }
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'FavoriteGroup':
        return cls(
            group_id=data["group_id"],
            group_name=data["group_name"],
            created_time=data.get("created_time", time.time())
        )


class FavoritesManager:
    """Manages favorite channels and groups"""
    
    def __init__(self):
        self.favorite_channels: Dict[str, FavoriteChannel] = {}
        self.favorite_groups: Dict[int, FavoriteGroup] = {}
        self._load_defaults()
    
    def _load_defaults(self):
        """Load default favorite groups"""
        default_groups = [
            FavoriteGroup(1, "Favorites 1"),
            FavoriteGroup(2, "Favorites 2"),
            FavoriteGroup(3, "Favorites 3"),
            FavoriteGroup(4, "Favorites 4"),
            FavoriteGroup(5, "Favorites 5"),
            FavoriteGroup(6, "Favorites 6"),
            FavoriteGroup(7, "Favorites 7"),
            FavoriteGroup(8, "Favorites 8"),
        ]
        
        for group in default_groups:
            self.favorite_groups[group.group_id] = group
    
    def add_favorite(self, channel: Dict[str, Any], group_ids: Optional[List[int]] = None) -> bool:
        """Add a channel to favorites"""
        try:
            program_id = str(channel.get("ProgramId", channel.get("ServiceID", channel.get("ServiceId", ""))))
            service_index = int(channel.get("ServiceIndex", channel.get("ProgramIndex", 0)))
            channel_name = str(channel.get("ServiceName", channel.get("ProgramName", "Unknown")))
            channel_number = str(channel.get("ServiceNum", channel.get("ProgramNum", service_index)))
            
            if not program_id:
                logger.error("Cannot add favorite: missing program_id")
                return False
            
            # Create or update favorite channel
            if program_id in self.favorite_channels:
                fav_channel = self.favorite_channels[program_id]
                # Update channel info in case it changed
                fav_channel.channel_name = channel_name
                fav_channel.channel_number = channel_number
                fav_channel.service_index = service_index
            else:
                fav_channel = FavoriteChannel(
                    program_id=program_id,
                    service_index=service_index,
                    channel_name=channel_name,
                    channel_number=channel_number
                )
                self.favorite_channels[program_id] = fav_channel
            
            # Add to specified groups (default to group 1 if none specified)
            if group_ids is None:
                group_ids = [1]
            
            for group_id in group_ids:
                if group_id in self.favorite_groups:
                    fav_channel.favorite_groups.add(group_id)
                    logger.info(f"Added channel '{channel_name}' to favorite group {group_id}")
                else:
                    logger.warning(f"Favorite group {group_id} does not exist")
            
            return True
            
        except Exception as e:
            logger.error(f"Failed to add favorite channel: {e}")
            return False
    
    def remove_favorite(self, program_id: str, group_ids: Optional[List[int]] = None) -> bool:
        """Remove a channel from favorites"""
        try:
            if program_id not in self.favorite_channels:
                logger.warning(f"Channel {program_id} is not in favorites")
                return False
            
            fav_channel = self.favorite_channels[program_id]
            
            if group_ids is None:
                # Remove from all groups
                removed_groups = list(fav_channel.favorite_groups)
                fav_channel.favorite_groups.clear()
                logger.info(f"Removed channel '{fav_channel.channel_name}' from all favorite groups")
            else:
                # Remove from specific groups
                removed_groups = []
                for group_id in group_ids:
                    if group_id in fav_channel.favorite_groups:
                        fav_channel.favorite_groups.remove(group_id)
                        removed_groups.append(group_id)
                        logger.info(f"Removed channel '{fav_channel.channel_name}' from favorite group {group_id}")
            
            # If channel is not in any groups, remove it completely
            if not fav_channel.favorite_groups:
                del self.favorite_channels[program_id]
                logger.info(f"Removed channel '{fav_channel.channel_name}' from favorites completely")
            
            return True
            
        except Exception as e:
            logger.error(f"Failed to remove favorite channel: {e}")
            return False
    
    def is_favorite(self, program_id: str, group_id: Optional[int] = None) -> bool:
        """Check if a channel is in favorites"""
        if program_id not in self.favorite_channels:
            return False
        
        if group_id is None:
            # Check if in any group
            return len(self.favorite_channels[program_id].favorite_groups) > 0
        
        return group_id in self.favorite_channels[program_id].favorite_groups
    
    def get_favorites_by_group(self, group_id: int) -> List[FavoriteChannel]:
        """Get all favorite channels in a specific group"""
        favorites = []
        for fav_channel in self.favorite_channels.values():
            if group_id in fav_channel.favorite_groups:
                favorites.append(fav_channel)
        
        # Sort by added time
        favorites.sort(key=lambda x: x.added_time)
        return favorites
    
    def get_all_favorites(self) -> List[FavoriteChannel]:
        """Get all favorite channels"""
        return list(self.favorite_channels.values())
    
    def get_favorite_groups(self) -> List[FavoriteGroup]:
        """Get all favorite groups"""
        return list(self.favorite_groups.values())
    
    def create_group(self, group_name: str) -> Optional[FavoriteGroup]:
        """Create a new favorite group"""
        try:
            # Find next available group ID
            max_id = max(self.favorite_groups.keys()) if self.favorite_groups else 0
            group_id = max_id + 1
            
            group = FavoriteGroup(group_id, group_name)
            self.favorite_groups[group_id] = group
            
            logger.info(f"Created favorite group '{group_name}' with ID {group_id}")
            return group
            
        except Exception as e:
            logger.error(f"Failed to create favorite group: {e}")
            return None
    
    def delete_group(self, group_id: int) -> bool:
        """Delete a favorite group and remove all channels from it"""
        try:
            if group_id not in self.favorite_groups:
                logger.warning(f"Favorite group {group_id} does not exist")
                return False
            
            group_name = self.favorite_groups[group_id].group_name
            
            # Remove all channels from this group
            channels_to_remove = []
            for program_id, fav_channel in self.favorite_channels.items():
                if group_id in fav_channel.favorite_groups:
                    fav_channel.favorite_groups.remove(group_id)
                    # If channel has no groups left, mark for removal
                    if not fav_channel.favorite_groups:
                        channels_to_remove.append(program_id)
            
            # Remove channels that are no longer in any group
            for program_id in channels_to_remove:
                del self.favorite_channels[program_id]
            
            # Delete the group
            del self.favorite_groups[group_id]
            
            logger.info(f"Deleted favorite group '{group_name}' and removed {len(channels_to_remove)} orphaned channels")
            return True
            
        except Exception as e:
            logger.error(f"Failed to delete favorite group: {e}")
            return False
    
    def rename_group(self, group_id: int, new_name: str) -> bool:
        """Rename a favorite group"""
        try:
            if group_id not in self.favorite_groups:
                logger.warning(f"Favorite group {group_id} does not exist")
                return False
            
            old_name = self.favorite_groups[group_id].group_name
            self.favorite_groups[group_id].group_name = new_name
            
            logger.info(f"Renamed favorite group from '{old_name}' to '{new_name}'")
            return True
            
        except Exception as e:
            logger.error(f"Failed to rename favorite group: {e}")
            return False
    
    def get_favorites_summary(self) -> Dict[str, Any]:
        """Get a summary of favorites"""
        summary = {
            "total_favorites": len(self.favorite_channels),
            "total_groups": len(self.favorite_groups),
            "groups": []
        }
        
        for group in self.favorite_groups.values():
            group_favorites = self.get_favorites_by_group(group.group_id)
            summary["groups"].append({
                "group_id": group.group_id,
                "group_name": group.group_name,
                "channel_count": len(group_favorites),
                "channels": [fav.to_dict() for fav in group_favorites]
            })
        
        return summary
    
    def export_favorites(self) -> Dict[str, Any]:
        """Export all favorites data"""
        return {
            "favorite_channels": {pid: fav.to_dict() for pid, fav in self.favorite_channels.items()},
            "favorite_groups": {gid: group.to_dict() for gid, group in self.favorite_groups.items()},
            "export_time": time.time()
        }
    
    def import_favorites(self, data: Dict[str, Any]) -> bool:
        """Import favorites data"""
        try:
            # Import groups
            if "favorite_groups" in data:
                for group_data in data["favorite_groups"].values():
                    group = FavoriteGroup.from_dict(group_data)
                    self.favorite_groups[group.group_id] = group
            
            # Import channels
            if "favorite_channels" in data:
                for channel_data in data["favorite_channels"].values():
                    channel = FavoriteChannel.from_dict(channel_data)
                    self.favorite_channels[channel.program_id] = channel
            
            logger.info("Successfully imported favorites data")
            return True
            
        except Exception as e:
            logger.error(f"Failed to import favorites data: {e}")
            return False
