from __future__ import annotations

import asyncio
import json
import logging
import time
from dataclasses import dataclass, field
from typing import Any, Callable, Dict, List, Optional
from xml.etree import ElementTree as ET

from app.stb.receiver import ReceivedMessage


logger = logging.getLogger(__name__)


# Response command types (same as request types)
CMD_CHANNEL_LIST = 0x00        # GMS_MSG_REQUEST_CHANNEL_LIST response
CMD_TIMER_LIST = 0x01          # GMS_MSG_REQUEST_EVENT_TIMER response  
CMD_CURRENT_CHANNEL = 0x03     # GMS_MSG_REQUEST_PLAYING_CHANNEL response
CMD_EPG_DATA = 0x05            # GMS_MSG_REQUEST_PROGRAM_EPG response
CMD_FAV_GROUP_NAMES = 0x0C     # GMS_MSG_REQUEST_FAV_GROUP_NAMES response (12)
CMD_CHANNEL_LIST_TYPE = 0x0E   # GMS_MSG_REQUEST_CHANNEL_LIST_TYPE response (14)
CMD_STB_INFO = 0x0F            # GMS_MSG_REQUEST_STB_INFO response (15)
CMD_SAT_LIST = 0x16            # GMS_MSG_REQUEST_SAT_LIST response (22)
CMD_KEEP_ALIVE = 0x1A          # GMS_MSG_REQUEST_SOCKET_KEEP_ALIVE response
CMD_REMOTE_RESPONSE = 0x410    # GMS_MSG_DO_REMOTE_CONTROL response
CMD_CHANNEL_LIST_UPDATE = 0x3F2  # GMS_MSG_DO_CHANNEL_LIST_UPDATE response (1010)

# Notification command types (STB → Client)
CMD_NOTIFY_CHANNEL_CHANGED = 0x7D1   # GMS_MSG_NOTIFY_PLAYING_CHANNEL_CHANGED
CMD_NOTIFY_CHANNEL_LIST = 0x7D2      # GMS_MSG_NOTIFY_CHANNEL_LIST_CHANGED
CMD_NOTIFY_SAT_LIST = 0x7E3          # GMS_MSG_NOTIFY_SAT_LIST_CHANGED (2019)
CMD_NOTIFY_INPUT_METHOD_POPUP = 0x7DB    # GMS_MSG_NOTIFY_INPUT_METHOD_POPUP (2011)
CMD_NOTIFY_INPUT_METHOD_DISMISS = 0x7DC  # GMS_MSG_NOTIFY_INPUT_METHOD_DISMISS (2012)
CMD_NOTIFY_FAV_GROUP_NAME_CHANGED = 0x7DD  # GMS_MSG_NOTIFY_FAV_GROUP_NAME_CHANGED (2013)


@dataclass
class STBState:
    channels: List[Dict[str, Any]] = field(default_factory=list)
    fav_groups: List[Dict[str, Any]] = field(default_factory=list)
    satellites: List[Dict[str, Any]] = field(default_factory=list)
    current_channel_index: int = 0
    current_program_id: str = ""
    channel_list_type: Dict[str, Any] = field(default_factory=dict)
    epg_events: List[Dict[str, Any]] = field(default_factory=list)
    timers: List[Dict[str, Any]] = field(default_factory=list)
    stb_info: Dict[str, Any] = field(default_factory=dict)
    channels_cache_time: Optional[float] = None  # Timestamp when channels were cached


StateChangeCallback = Callable[[str, Any], None]


@dataclass
class MessageProcessor:
    state: STBState = field(default_factory=STBState)
    use_json: bool = False
    _listeners: List[StateChangeCallback] = field(default_factory=list)
    _async_queue: Optional[asyncio.Queue] = None
    _async_loop: asyncio.AbstractEventLoop | None = None
    _channel_list_complete: Optional[asyncio.Event] = None
    _channel_batch_complete: Optional[asyncio.Event] = None
    _last_batch_size: int = 0
    _last_channel_count: int = 0
    _last_channel_time: Optional[float] = None

    def set_async_queue(self, queue: asyncio.Queue | None) -> None:
        self._async_queue = queue
        if queue is None:
            self._async_loop = None
            return
        try:
            self._async_loop = asyncio.get_running_loop()
        except RuntimeError:
            self._async_loop = None
    
    def add_event_queue(self, queue: asyncio.Queue) -> None:
        """Add an asyncio queue for event broadcasting"""
        self.set_async_queue(queue)
    
    def remove_event_queue(self, queue: asyncio.Queue) -> None:
        """Remove an asyncio queue from event broadcasting"""
        if self._async_queue is queue:
            self.set_async_queue(None)
    
    def start_channel_list_wait(self) -> asyncio.Event:
        """Create a new event to wait for channel list completion"""
        self._channel_list_complete = asyncio.Event()
        self._channel_batch_complete = None
        self._last_batch_size = 0
        self._last_channel_count = 0
        self._last_channel_time = time.time()
        return self._channel_list_complete

    def reset_channel_batch_wait(self) -> asyncio.Event:
        self._channel_batch_complete = asyncio.Event()
        self._last_batch_size = 0
        return self._channel_batch_complete

    async def wait_for_channel_batch(self, event: asyncio.Event | None = None, timeout: float = 10.0) -> int:
        ev = event or self._channel_batch_complete
        if ev is None:
            raise asyncio.TimeoutError()
        await asyncio.wait_for(ev.wait(), timeout=timeout)
        return int(self._last_batch_size)
    
    def finish_channel_list(self) -> None:
        """Mark channel list loading as complete"""
        if self._channel_list_complete:
            self._channel_list_complete.set()
            self._channel_list_complete = None
            if self._channel_batch_complete and not self._channel_batch_complete.is_set():
                self._channel_batch_complete.set()
            self._channel_batch_complete = None
            # Set cache time when loading is complete
            self.state.channels_cache_time = time.time()
            logger.info(f"Channel list cached at {self.state.channels_cache_time}")
    
    def is_channels_cache_valid(self, cache_hours: float = 1.0) -> bool:
        """Check if channel cache is still valid (default: 1 hour)"""
        if not self.state.channels_cache_time:
            return False

        if float(cache_hours) <= 0:
            return len(self.state.channels) > 0

        cache_age = time.time() - self.state.channels_cache_time
        max_age = cache_hours * 3600  # Convert hours to seconds

        is_valid = cache_age < max_age and len(self.state.channels) > 0
        if is_valid:
            logger.info(f"Channel cache valid: {cache_age/3600:.1f} hours old, {len(self.state.channels)} channels")
        
        return is_valid
    
    def clear_channels_cache(self) -> None:
        """Clear the channel cache"""
        self.state.channels = []
        self.state.channels_cache_time = None
        logger.info("Channel cache cleared")

    def add_listener(self, callback: StateChangeCallback) -> None:
        self._listeners.append(callback)

    def remove_listener(self, callback: StateChangeCallback) -> None:
        if callback in self._listeners:
            self._listeners.remove(callback)

    def process_message(self, msg: ReceivedMessage) -> None:
        cmd = msg.command_type
        data = msg.data

        # Debug logging for all received messages
        logger.info(f"Received message: cmd=0x{cmd:X} ({cmd}), state={msg.response_state}, data_len={len(data)}")
        if data and len(data) < 500:
            logger.debug(f"Data preview: {data[:200]}")
        try:
            if self.use_json:
                parsed = self._parse_json(data)
            else:
                parsed = self._parse_xml(data)
        except Exception as e:
            logger.warning(f"Failed to parse message cmd=0x{cmd:X}: {e}")
            parsed = {}

        try:
            if isinstance(parsed, list):
                logger.info(f"Parsed type=list len={len(parsed)}")
                if parsed and isinstance(parsed[0], dict):
                    logger.info(f"First channel keys={list(parsed[0].keys())}")
            elif isinstance(parsed, dict):
                logger.info(f"Parsed type=dict keys={list(parsed.keys())}")
        except Exception:
            pass        # Handle response command types
        if cmd == CMD_CHANNEL_LIST or cmd == CMD_CHANNEL_LIST_UPDATE:
            self._handle_channel_list(parsed)
        elif cmd == CMD_CHANNEL_LIST_TYPE:
            self._handle_channel_list_type(parsed)
        elif cmd == CMD_FAV_GROUP_NAMES:
            self._handle_fav_groups(parsed)
        elif cmd == CMD_NOTIFY_CHANNEL_LIST:
            self._notify("channel_list_changed", None)
        elif cmd == CMD_SAT_LIST or cmd == CMD_NOTIFY_SAT_LIST:
            self._handle_satellite_list(parsed)
        elif cmd == CMD_EPG_DATA:
            self._handle_epg_data(parsed)
        elif cmd == CMD_CURRENT_CHANNEL:
            self._handle_current_channel(parsed)
        elif cmd == CMD_NOTIFY_CHANNEL_CHANGED:
            self._notify("current_channel_changed", None)
        elif cmd == CMD_STB_INFO:
            self._handle_stb_info(parsed)
        elif cmd == CMD_TIMER_LIST:
            self._handle_timer_list(parsed)
        elif cmd == CMD_REMOTE_RESPONSE:
            pass
        elif cmd == CMD_KEEP_ALIVE:
            pass  # Keep-alive response, ignore
        elif cmd == CMD_NOTIFY_INPUT_METHOD_POPUP:
            self._notify("input_method_popup", None)
        elif cmd == CMD_NOTIFY_INPUT_METHOD_DISMISS:
            self._notify("input_method_dismiss", None)
        elif cmd == CMD_NOTIFY_FAV_GROUP_NAME_CHANGED:
            self._notify("fav_group_name_changed", None)
        else:
            logger.info(f"Unhandled command: 0x{cmd:X} ({cmd})")

    def _leaf_text(self, v: Any) -> Any:
        if isinstance(v, dict) and "_text" in v:
            return v.get("_text")
        return v

    def _flatten_leaf_dict(self, d: dict[str, Any]) -> dict[str, Any]:
        out: dict[str, Any] = {}
        for k, v in d.items():
            if k == "_text":
                continue
            if isinstance(v, dict) and set(v.keys()) == {"_text"}:
                out[k] = v.get("_text")
            else:
                out[k] = v
        return out

    def _as_int(self, v: Any, default: int = -1) -> int:
        v2 = self._leaf_text(v)
        try:
            return int(v2)
        except Exception:
            return default

    def _notify(self, event_type: str, data: Any) -> None:
        for listener in self._listeners:
            try:
                listener(event_type, data)
            except Exception:
                pass

        if self._async_queue:
            item = {"event": event_type, "data": data}
            if self._async_loop is not None:
                def _put() -> None:
                    try:
                        self._async_queue.put_nowait(item)
                    except asyncio.QueueFull:
                        pass

                try:
                    self._async_loop.call_soon_threadsafe(_put)
                    return
                except RuntimeError:
                    pass

            try:
                self._async_queue.put_nowait(item)
            except asyncio.QueueFull:
                pass

    def _handle_channel_list(self, parsed: Any) -> None:
        # STB sends channels ONE AT A TIME in multiple messages
        # APK logic: if ServiceIndex==0, clear list (first channel); else accumulate
        channels_to_add = []
        if isinstance(parsed, list):
            channels_to_add = parsed
        elif isinstance(parsed, dict):
            if "parm" in parsed:
                parms = parsed.get("parm")
                if isinstance(parms, dict):
                    channels_to_add = [parms]
                elif isinstance(parms, list):
                    channels_to_add = parms
            elif "channels" in parsed:
                channels_to_add = parsed.get("channels", [])
            elif "array" in parsed:
                channels_to_add = parsed.get("array", [])
            else:
                return
        
        if not isinstance(channels_to_add, list):
            return
        
        if not channels_to_add:
            batch_size = 0
            if self._channel_batch_complete and not self._channel_batch_complete.is_set():
                self._last_batch_size = batch_size
                self._channel_batch_complete.set()
            
            if self._channel_list_complete and not self._channel_list_complete.is_set():
                current_count = len(self.state.channels)
                logger.info(f"Channel list complete (empty batch): {current_count} channels received")
                self._notify("channel_list_complete", {"count": current_count})
                self.finish_channel_list()
            return
            
        batch_size = 0
        if isinstance(channels_to_add, list):
            batch_size = len(channels_to_add)

        for ch in channels_to_add:
            if not isinstance(ch, dict):
                continue
            if "_text" in ch or any(isinstance(v, dict) for v in ch.values()):
                ch = self._flatten_leaf_dict(ch)

            # Get ServiceIndex (or ProgramIndex for compatibility)
            idx = self._as_int(ch.get("ServiceIndex", ch.get("ProgramIndex", -1)))
            if idx == 0:
                # First channel - clear list and start fresh
                logger.info("Channel list reset (ServiceIndex=0), starting fresh accumulation")
                self.state.channels = [ch]
                self._notify("channel_list_reset", None)
            else:
                # Accumulate - add or update by ServiceIndex
                found = False
                for i, existing in enumerate(self.state.channels):
                    if not isinstance(existing, dict):
                        continue
                    existing_idx = self._as_int(existing.get("ServiceIndex", existing.get("ProgramIndex", -1)))
                    if existing_idx == idx:
                        self.state.channels[i] = ch
                        found = True
                        break
                if not found:
                    self.state.channels.append(ch)

            self._notify("channel_upsert", ch)
        
        logger.info(f"Channel list now has {len(self.state.channels)} channels")
        self._notify("channel_list_progress", {"count": len(self.state.channels)})

        if self._channel_batch_complete and not self._channel_batch_complete.is_set():
            self._last_batch_size = batch_size
            self._channel_batch_complete.set()
        
        if not (self._channel_list_complete and not self._channel_list_complete.is_set()):
            return
        
        current_count = len(self.state.channels)
        
        # Check if we have received all channels (based on STB info)
        if self.state.stb_info and "ChannelNum" in self.state.stb_info:
            expected: int | None
            try:
                expected = int(self.state.stb_info["ChannelNum"])
            except Exception:
                expected = None
            
            if expected is not None and current_count >= expected:
                logger.info(f"Channel list complete: {current_count} channels received")
                self._notify("channel_list_complete", {"count": current_count})
                self.finish_channel_list()
                return
        
        max_batch_size = 100
        if batch_size < max_batch_size:
            logger.info(
                f"Channel list complete (last batch {batch_size} < {max_batch_size}): {current_count} channels received"
            )
            self._notify("channel_list_complete", {"count": current_count})
            self.finish_channel_list()
            return

    def _handle_satellite_list(self, parsed: Any) -> None:
        # Satellite list response
        sats = []
        if isinstance(parsed, list):
            sats = parsed
        elif isinstance(parsed, dict):
            sats = parsed.get("satellites", parsed.get("array", []))
        
        if isinstance(sats, list) and sats:
            self.state.satellites = sats
            logger.info(f"Satellite list now has {len(self.state.satellites)} satellites")
            self._notify("satellite_list", self.state.satellites)

    def _handle_fav_groups(self, parsed: Any) -> None:
        groups: list[dict[str, Any]] = []

        if isinstance(parsed, list):
            for item in parsed:
                if not isinstance(item, dict):
                    continue
                if "favGroupNames" in item:
                    names = item.get("favGroupNames")
                    ids = item.get("favGroupIds")
                    if not isinstance(names, list):
                        continue
                    for idx, name in enumerate(names):
                        gid: Any = idx + 1
                        if isinstance(ids, list) and idx < len(ids):
                            gid = ids[idx]
                        groups.append({"favorGroupName": name, "FavorGroupID": gid})
                else:
                    groups.append(item)

        elif isinstance(parsed, dict):
            arr = parsed.get("array")
            if isinstance(arr, list):
                for item in arr:
                    if not isinstance(item, dict):
                        continue
                    name = self._leaf_text(item.get("favorGroupName"))
                    gid = self._leaf_text(item.get("FavorGroupID"))
                    if name is None and gid is None:
                        continue
                    groups.append({"favorGroupName": name, "FavorGroupID": gid})

        if groups:
            self.state.fav_groups = groups
            self._notify("fav_groups", groups)

    def _handle_channel_list_type(self, parsed: Any) -> None:
        if isinstance(parsed, dict):
            if "parm" in parsed and isinstance(parsed.get("parm"), dict):
                out = self._flatten_leaf_dict(parsed.get("parm"))
            else:
                out = self._flatten_leaf_dict(parsed)
            self.state.channel_list_type = out
            self._notify("channel_list_type", out)

    def _handle_epg_data(self, parsed: Any) -> None:
        # JSON response may be array directly
        if isinstance(parsed, list):
            self.state.epg_events = parsed
            self._notify("epg_data", parsed)
        elif isinstance(parsed, dict):
            events = parsed.get("events", parsed.get("array", []))
            if isinstance(events, list):
                self.state.epg_events = events
                self._notify("epg_data", events)

    def _handle_current_channel(self, parsed: Any) -> None:
        program_id: str | None = None
        index: int | None = None

        if isinstance(parsed, list):
            if parsed:
                program_id = str(self._leaf_text(parsed[0]))

        elif isinstance(parsed, dict):
            if "index" in parsed:
                index = self._as_int(parsed.get("index"), default=0)
            elif "ProgramIndex" in parsed:
                index = self._as_int(parsed.get("ProgramIndex"), default=0)
            elif "ServiceIndex" in parsed:
                index = self._as_int(parsed.get("ServiceIndex"), default=0)
            elif "Data" in parsed:
                program_id = str(self._leaf_text(parsed.get("Data")))
            elif "parm" in parsed:
                p = parsed.get("parm")
                if isinstance(p, dict):
                    if "Data" in p:
                        program_id = str(self._leaf_text(p.get("Data")))
                    elif "ProgramId" in p:
                        program_id = str(self._leaf_text(p.get("ProgramId")))

        if program_id is not None:
            self.state.current_program_id = program_id
            idx_found: int | None = None
            for ch in self.state.channels:
                if not isinstance(ch, dict):
                    continue
                pid = ch.get("ProgramId", ch.get("ServiceID", ch.get("ServiceId")))
                if pid is None:
                    continue
                if str(pid) == program_id:
                    idx_found = self._as_int(ch.get("ProgramIndex", ch.get("ServiceIndex", 0)), default=0)
                    break
            if idx_found is not None:
                index = idx_found
            else:
                try:
                    index = int(program_id)
                except Exception:
                    pass

        if index is None:
            index = 0

        self.state.current_channel_index = int(index)
        self._notify("current_channel", {"index": int(index), "program_id": self.state.current_program_id})

    def _handle_stb_info(self, parsed: Dict[str, Any]) -> None:
        self.state.stb_info = parsed
        self._notify("stb_info", parsed)

    def _handle_timer_list(self, parsed: Dict[str, Any]) -> None:
        timers = parsed.get("timers", [])
        if isinstance(timers, list):
            self.state.timers = timers
            self._notify("timer_list", timers)

    def _parse_json(self, data: bytes) -> Any:
        if not data:
            return {}
        text = data.decode("utf-8", errors="ignore").strip("\x00\r\n\t ")
        return json.loads(text)  # Can be dict or list

    def _parse_xml(self, data: bytes) -> Dict[str, Any]:
        if not data:
            return {}
        text = data.decode("utf-8", errors="ignore").strip("\x00\r\n\t ")
        root = ET.fromstring(text)
        return self._xml_to_dict(root)

    def _xml_to_dict(self, element: ET.Element) -> Dict[str, Any]:
        result: Dict[str, Any] = dict(element.attrib)

        children_by_tag: Dict[str, List[Any]] = {}
        for child in element:
            tag = child.tag
            child_dict = self._xml_to_dict(child)
            if tag not in children_by_tag:
                children_by_tag[tag] = []
            children_by_tag[tag].append(child_dict)

        for tag, items in children_by_tag.items():
            if len(items) == 1:
                result[tag] = items[0]
            else:
                result[tag] = items

        if element.text and element.text.strip():
            if result:
                result["_text"] = element.text.strip()
            else:
                return {"_text": element.text.strip()}

        return result


