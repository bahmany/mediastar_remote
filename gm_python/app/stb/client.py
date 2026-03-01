from __future__ import annotations

import asyncio
import json
import logging
import platform
import socket
import threading
import time
import uuid
from dataclasses import dataclass, field
from pathlib import Path
from typing import Optional, Callable

from app.stb.constants import (
    G_MS_BROADCAST_INFO_MAGIC_CODE,
    GMS_MSG_DO_CHANNEL_LIST_UPDATE,
    GMS_MSG_DO_CHANNEL_FAV_MARK,
    GMS_MSG_DO_FAV_GROUP_RENAME,
    GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED,
    GMS_MSG_DO_CHANNEL_SWITCH,
    GMS_MSG_DO_INPUT_METHOD_DISMISS,
    GMS_MSG_DO_INPUT_METHOD_KEY_CODE_SET,
    GMS_MSG_DO_REMOTE_CONTROL,
    GMS_MSG_REQUEST_CHANNEL_LIST,
    GMS_MSG_REQUEST_CHANNEL_LIST_TYPE,
    GMS_MSG_REQUEST_FAV_GROUP_NAMES,
    GMS_MSG_REQUEST_LOGIN_INFO,
    GMS_MSG_REQUEST_PLAYING_CHANNEL,
    GMS_MSG_REQUEST_PROGRAM_EPG,
    GMS_MSG_REQUEST_SOCKET_KEEP_ALIVE,
    GMS_MSG_REQUEST_SAT_LIST,
    GMS_MSG_REQUEST_STB_INFO,
    STB_LOGIN_INFO_DATA_LENGTH,
    UPNP_HANDSHAKE_MSG_MOBILE,
)
from app.stb.connection_monitor import connection_monitor, attempt_reconnect
from app.stb.favorites import FavoritesManager
from app.stb.message_processor import MessageProcessor
from app.stb.models import GsMobileLoginInfo
from app.stb.receiver import ReceivedMessage, SocketReceiveThread
from app.stb.scramble import scramble_stb_info_for_broadcast
from app.stb.serializers import build_socket_frame, serialize_json_command, serialize_xml_command

logger = logging.getLogger('stb.wire')


class STBConnectionError(RuntimeError):
    pass


@dataclass
class STBClient:
    ip: str
    port: int = 20000
    timeout_s: float = 4.0

    _sock: socket.socket | None = None
    login_info: GsMobileLoginInfo | None = None
    _receiver: Optional[SocketReceiveThread] = None
    _processor: MessageProcessor = field(default_factory=MessageProcessor)
    _event_queue: Optional[asyncio.Queue] = None
    _event_loop: asyncio.AbstractEventLoop | None = None
    _send_lock: threading.Lock = field(default_factory=threading.Lock)
    _favorites: FavoritesManager = field(default_factory=FavoritesManager)
    
    # Connection monitoring and recovery
    _connection_monitor_task: Optional[asyncio.Task] = None
    _auto_reconnect: bool = True
    _reconnect_attempts: int = 0
    _max_reconnect_attempts: int = 10
    _reconnect_delay: float = 1.0
    _reconnect_future: Optional[object] = None
    _last_activity: float = field(default_factory=time.time)
    _last_rx_activity: float = field(default_factory=time.time)
    _heartbeat_interval: float = 15.0  # More frequent heartbeats
    _connection_timeout: float = 45.0  # Longer timeout before considering connection dead
    _last_synced_fav_marks: dict[str, int] = field(default_factory=dict)

    def _channels_cache_path(self) -> Path:
        safe_ip = str(self.ip).replace(":", "_").replace("/", "_").replace("\\", "_")
        cache_dir = Path(__file__).resolve().parent / "_cache"
        return cache_dir / f"channels_{safe_ip}_{int(self.port)}.json"

    def _load_channels_cache_from_disk(self) -> None:
        p = self._channels_cache_path()
        try:
            if not p.exists():
                return
            raw = p.read_text(encoding="utf-8")
            data = json.loads(raw)
            if not isinstance(data, dict):
                return
            channels = data.get("channels")
            cache_time = data.get("channels_cache_time")
            if not isinstance(channels, list) or not channels:
                return
            try:
                cache_time_f = float(cache_time) if cache_time is not None else None
            except Exception:
                cache_time_f = None
            if cache_time_f is None:
                return

            self._processor.state.channels = channels
            self._processor.state.channels_cache_time = cache_time_f
            logger.info("Loaded channel cache from disk: %d channels", len(channels))
        except Exception as e:
            logger.warning("Failed to load channel cache from disk: %s", e)

    def _save_channels_cache_to_disk(self) -> None:
        p = self._channels_cache_path()
        try:
            p.parent.mkdir(parents=True, exist_ok=True)
            tmp = p.with_suffix(p.suffix + ".tmp")
            payload = {
                "channels_cache_time": self._processor.state.channels_cache_time,
                "channels": self._processor.state.channels,
            }
            tmp.write_text(json.dumps(payload, ensure_ascii=False), encoding="utf-8")
            tmp.replace(p)
        except Exception as e:
            logger.warning("Failed to save channel cache to disk: %s", e)

    def _clear_channels_cache_on_disk(self) -> None:
        p = self._channels_cache_path()
        try:
            if p.exists():
                p.unlink(missing_ok=True)
        except Exception:
            pass

    def connect_and_login(self) -> GsMobileLoginInfo:
        old_receiver = self._receiver
        self._receiver = None
        if old_receiver is not None:
            try:
                old_receiver.stop()
            except Exception:
                pass

        old_sock = self._sock
        self._sock = None
        if old_sock is not None:
            try:
                old_sock.close()
            except Exception:
                pass

        try:
            sock = socket.create_connection((self.ip, self.port), timeout=self.timeout_s)
        except OSError as e:
            raise STBConnectionError(f"TCP connect failed: {e}") from e

        sock.settimeout(self.timeout_s)
        self._sock = sock

        device_model = platform.platform()
        device_uuid = str(uuid.uuid4())
        payload = serialize_xml_command(GMS_MSG_REQUEST_LOGIN_INFO, [{"data": device_model, "uuid": device_uuid}]).encode("utf-8")

        try:
            sock.sendall(build_socket_frame(payload))
        except OSError as e:
            try:
                sock.close()
            except Exception:
                pass
            if self._sock is sock:
                self._sock = None
            self.login_info = None
            raise STBConnectionError(f"send login failed: {e}") from e

        try:
            raw = _recv_exact(sock, STB_LOGIN_INFO_DATA_LENGTH)
        except OSError as e:
            try:
                sock.close()
            except Exception:
                pass
            if self._sock is sock:
                self._sock = None
            self.login_info = None
            raise STBConnectionError(f"read login response failed: {e}") from e

        buf = bytearray(raw)
        scramble_stb_info_for_broadcast(buf, len(buf))

        if bytes(buf[0:12]) != G_MS_BROADCAST_INFO_MAGIC_CODE.encode("ascii"):
            try:
                sock.close()
            except Exception:
                pass
            if self._sock is sock:
                self._sock = None
            self.login_info = None
            raise STBConnectionError("invalid login magic code")

        info = GsMobileLoginInfo.from_bytes(bytes(buf))
        self.login_info = info

        try:
            if int(getattr(info, "is_current_stb_connected_full", 0)) == 1:
                try:
                    sock.close()
                except Exception:
                    pass
                if self._sock is sock:
                    self._sock = None
                self.login_info = None
                raise STBConnectionError("stb is full")
        except STBConnectionError:
            raise
        except Exception:
            pass

        self._processor.use_json = (info.send_data_type == 1)

        try:
            self._load_channels_cache_from_disk()
        except Exception:
            pass

        self._receiver = SocketReceiveThread(
            sock=sock,
            on_message=self._on_message,
            on_disconnect=self._on_disconnect,
            use_json=(info.send_data_type == 1),
            send_lock=self._send_lock,
        )
        self._receiver.start()
        self._last_activity = time.time()
        self._last_rx_activity = self._last_activity
        self._reconnect_attempts = 0
        
        if self._event_loop is not None:
            def _start_monitor() -> None:
                try:
                    if self._connection_monitor_task:
                        self._connection_monitor_task.cancel()
                except Exception:
                    pass
                self._connection_monitor_task = asyncio.create_task(connection_monitor(self))

            try:
                self._event_loop.call_soon_threadsafe(_start_monitor)
            except Exception:
                pass
        
        logger.info(f"STB login OK: model={info.model_name} serial={info.stb_sn_disp} send_data_type={info.send_data_type}")

        return info

    def set_event_queue(self, queue: asyncio.Queue | None) -> None:
        self._event_queue = queue
        if queue is None:
            self._event_loop = None
        else:
            try:
                self._event_loop = asyncio.get_running_loop()
            except RuntimeError:
                self._event_loop = None
        self._processor.set_async_queue(queue)

        if self._event_loop is not None and self.is_connected() and self._connection_monitor_task is None:
            self._connection_monitor_task = asyncio.create_task(connection_monitor(self))

    def _on_message(self, msg: ReceivedMessage) -> None:
        self._last_activity = time.time()
        self._last_rx_activity = self._last_activity
        self._processor.process_message(msg)

    def _on_disconnect(self) -> None:
        if self._sock is None and self.login_info is None and self._receiver is None:
            return
        logger.warning("STB connection lost")
        try:
            old_receiver = self._receiver
            self._receiver = None

            old_sock = self._sock
            self._sock = None
            self.login_info = None

            if old_receiver is not None:
                try:
                    old_receiver.stop()
                except Exception:
                    pass

            if old_sock is not None:
                try:
                    old_sock.close()
                except Exception:
                    pass
        except Exception:
            pass
        
        # Stop connection monitoring
        if self._connection_monitor_task:
            task = self._connection_monitor_task
            self._connection_monitor_task = None

            if self._event_loop is not None:
                try:
                    self._event_loop.call_soon_threadsafe(task.cancel)
                except Exception:
                    try:
                        task.cancel()
                    except Exception:
                        pass
            else:
                try:
                    task.cancel()
                except Exception:
                    pass
            
        if self._event_queue:
            item = {"event": "disconnected", "data": None}

            if self._event_loop is not None:
                def _put() -> None:
                    try:
                        self._event_queue.put_nowait(item)
                    except asyncio.QueueFull:
                        pass

                try:
                    self._event_loop.call_soon_threadsafe(_put)
                except RuntimeError:
                    pass

            try:
                self._event_queue.put_nowait(item)
            except asyncio.QueueFull:
                pass
                
        # Trigger auto-reconnect if enabled
        if self._auto_reconnect and self._event_loop:
            try:
                if self._reconnect_future is not None:
                    done_fn = getattr(self._reconnect_future, "done", None)
                    if done_fn is None:
                        return
                    if done_fn() is False:
                        return
            except Exception:
                pass

            self._reconnect_future = object()

            def _schedule_reconnect() -> None:
                try:
                    self._reconnect_future = asyncio.run_coroutine_threadsafe(attempt_reconnect(self), self._event_loop)
                except Exception as e:
                    self._reconnect_future = None
                    logger.error(f"Failed to schedule reconnect: {e}")
            
            try:
                self._event_loop.call_soon_threadsafe(_schedule_reconnect)
            except RuntimeError:
                self._reconnect_future = None
                pass

    def send_remote_key(self, key_value: int) -> None:
        if self._sock is None or self.login_info is None:
            raise STBConnectionError("not connected")

        req = GMS_MSG_DO_REMOTE_CONTROL
        if self.login_info.send_data_type == 1:
            payload_str = serialize_json_command(req, [{"KeyValue": str(int(key_value))}])
        else:
            payload_str = serialize_xml_command(req, [str(int(key_value))])

        payload = payload_str.encode("utf-8")

        try:
            with self._send_lock:
                self._sock.sendall(build_socket_frame(payload))
            self._last_activity = time.time()
        except OSError as e:
            logger.error(f"Send key failed: {e}")
            self._on_disconnect()
            raise STBConnectionError(f"send key failed: {e}") from e

    def send_input_keycode(self, key_code: int, *, force: bool = False) -> None:
        if self._sock is None or self.login_info is None:
            raise STBConnectionError("not connected")

        if key_code <= 0:
            return

        if (not force) and int(self.login_info.platform_id) in (32, 71, 72, 74):
            logger.info("Skip input keycode for platform_id=%s (APK behavior)", self.login_info.platform_id)
            return

        self._send_command(GMS_MSG_DO_INPUT_METHOD_KEY_CODE_SET, [{"KeyCode": int(key_code)}])

    def dismiss_input_method(self) -> None:
        self._send_command(GMS_MSG_DO_INPUT_METHOD_DISMISS, None)

    def send_keyboard_backspace(self) -> None:
        self.send_remote_key(10)

    def send_keyboard_enter(self) -> None:
        if self.login_info is None:
            raise STBConnectionError("not connected")
        if int(self.login_info.platform_id) in (20, 21, 25):
            self.send_remote_key(8)
        else:
            self.send_remote_key(11)

    def send_text(self, text: str, *, force: bool = False) -> None:
        if not text:
            return
        for ch in text:
            self.send_input_keycode(ord(ch), force=force)

    async def request_channel_list(self, timeout: float = 60.0, force_refresh: bool = False, cache_hours: float = 1.0) -> int:
        # Check cache first (unless force refresh)
        if not force_refresh and self._processor.is_channels_cache_valid(cache_hours):
            cache_age = time.time() - (self._processor.state.channels_cache_time or 0)
            logger.info(f"Using cached channel list: {len(self._processor.state.channels)} channels (cached {cache_age/60:.1f} minutes ago)")
            return len(self._processor.state.channels)
        
        if force_refresh:
            logger.info("Force refresh requested, clearing channel cache")
            self._clear_channels_cache_on_disk()
        else:
            logger.info("Channel cache expired or invalid, refreshing")
        
        self._processor.clear_channels_cache()

        self._processor.start_channel_list_wait()

        batch_size = 100
        from_idx = 0
        expected_total: int | None = None
        if self._processor.state.stb_info and "ChannelNum" in self._processor.state.stb_info:
            try:
                expected_total = int(self._processor.state.stb_info["ChannelNum"])
            except Exception:
                expected_total = None

        start_time = time.time()
        while True:
            if self._sock is None or self.login_info is None:
                raise STBConnectionError("not connected")

            if time.time() - start_time > timeout:
                logger.warning(
                    f"Channel list loading timed out after {timeout}s, got {len(self._processor.state.channels)} channels"
                )
                break

            batch_event = self._processor.reset_channel_batch_wait()
            to_idx = from_idx + batch_size - 1
            logger.info(f"Requesting channel batch: {from_idx} to {to_idx}")
            self._send_command(
                GMS_MSG_REQUEST_CHANNEL_LIST,
                [{"FromIndex": from_idx}, {"ToIndex": to_idx}],
            )

            try:
                got = await self._processor.wait_for_channel_batch(batch_event, timeout=10.0)
            except asyncio.TimeoutError:
                logger.warning(
                    f"Channel list batch timed out (from={from_idx} to={to_idx}), got {len(self._processor.state.channels)} channels"
                )
                break

            if expected_total is not None and len(self._processor.state.channels) >= expected_total:
                break

            if got < batch_size:
                break

            from_idx += batch_size
            await asyncio.sleep(0.05)

        if len(self._processor.state.channels) > 0:
            self._processor.finish_channel_list()
            self._save_channels_cache_to_disk()
        logger.info(f"Channel list loading completed: {len(self._processor.state.channels)} channels")
        return len(self._processor.state.channels)
    
    def request_channel_list_type(self) -> None:
        self._send_command(GMS_MSG_REQUEST_CHANNEL_LIST_TYPE, [{"type": "0"}])

    def request_fav_group_names(self) -> None:
        self._send_command(GMS_MSG_REQUEST_FAV_GROUP_NAMES, None)

    def request_current_channel(self) -> None:
        self._send_command(GMS_MSG_REQUEST_PLAYING_CHANNEL, None)

    def _find_channel(self, channel_index: int) -> dict | None:
        try:
            for ch in self._processor.state.channels:
                if not isinstance(ch, dict):
                    continue
                idx = ch.get("ProgramIndex", ch.get("ServiceIndex", -1))
                try:
                    if int(idx) == int(channel_index):
                        return ch
                except Exception:
                    continue
        except Exception:
            return None
        return None

    def _resolve_program_id(self, ch: dict | None, fallback_index: int) -> str:
        if not ch:
            return str(fallback_index)
        for k in ("ProgramId", "ServiceID", "ServiceId"):
            v = ch.get(k)
            if v not in (None, ""):
                return str(v)
        return str(fallback_index)

    def _resolve_tv_state(self, ch: dict | None) -> int:
        if not ch:
            return 0
        for k in ("ChannelType", "Radio", "TvState"):
            v = ch.get(k)
            if v not in (None, ""):
                try:
                    return int(v)
                except Exception:
                    pass
        return 0

    def request_epg(self, channel_index: int) -> None:
        ch = self._find_channel(int(channel_index))
        program_id = self._resolve_program_id(ch, int(channel_index))
        self._send_command(GMS_MSG_REQUEST_PROGRAM_EPG, [{"ProgramId": program_id}])

    def request_stb_info(self) -> None:
        self._send_command(GMS_MSG_REQUEST_STB_INFO, None)

    def request_keep_alive(self) -> None:
        self._send_command(GMS_MSG_REQUEST_SOCKET_KEEP_ALIVE, None)

    def request_satellite_list(self) -> None:
        self._send_command(GMS_MSG_REQUEST_SAT_LIST, None)

    def request_channel_list_update(self) -> None:
        self._send_command(GMS_MSG_DO_CHANNEL_LIST_UPDATE, None)

    def set_channel_list_type(self, *, is_fav_list: int, select_list_type: int) -> None:
        self._send_command(
            GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED,
            [{"IsFavList": int(is_fav_list), "SelectListType": int(select_list_type)}],
        )

    def set_channel_fav_mark(
        self,
        *,
        tv_state: int,
        fav_mark: int,
        favor_group_ids: list[int] | None,
        program_ids: list[str],
    ) -> None:
        platform_id = int(self.login_info.platform_id) if self.login_info else 0
        include_total = platform_id not in (30, 31, 32, 71, 72, 74)

        group_str = ""
        if favor_group_ids:
            group_str = "".join(f"{int(g)}:" for g in favor_group_ids)

        payload = {
            "TvState": int(tv_state),
            "FavMark": int(fav_mark),
            "FavorGroupID": group_str,
            "ProgramIds": list(program_ids),
        }
        if include_total:
            payload["TotalNum"] = len(program_ids)

        self._send_command(GMS_MSG_DO_CHANNEL_FAV_MARK, [payload])

    def change_channel(self, channel_index: int) -> None:
        ch = self._find_channel(int(channel_index))
        program_id = self._resolve_program_id(ch, int(channel_index))
        tv_state = self._resolve_tv_state(ch)
        self._send_command(GMS_MSG_DO_CHANNEL_SWITCH, [{"TvState": tv_state, "ProgramId": program_id}])

    def _send_command(self, cmd: int, items: list | None) -> None:
        if self._sock is None or self.login_info is None:
            raise STBConnectionError("not connected")

        if self.login_info.send_data_type == 1:
            payload_str = serialize_json_command(cmd, items)
        else:
            payload_str = serialize_xml_command(cmd, items)

        payload = payload_str.encode("utf-8")

        logger.info("STB SEND cmd=0x%X (%d) len=%d", cmd, cmd, len(payload))
        logger.debug("STB SEND payload=%s", payload_str)

        try:
            with self._send_lock:
                self._sock.sendall(build_socket_frame(payload))
            self._last_activity = time.time()
        except OSError as e:
            logger.error(f"Send command failed: {e}")
            self._on_disconnect()
            raise STBConnectionError(f"send command failed: {e}") from e

    def _send_command_raw(self, cmd: int, extra_fields: dict) -> None:
        if self._sock is None or self.login_info is None:
            raise STBConnectionError("not connected")

        # Build JSON with request and extra fields directly (no array)
        import json
        data = {"request": str(cmd)}
        data.update(extra_fields)
        payload_str = json.dumps(data, separators=(",", ":"))

        payload = payload_str.encode("utf-8")

        logger.info("STB SEND cmd=0x%X (%d) len=%d", cmd, cmd, len(payload))
        logger.debug("STB SEND payload=%s", payload_str)

        try:
            with self._send_lock:
                self._sock.sendall(build_socket_frame(payload))
            self._last_activity = time.time()
        except OSError as e:
            logger.error(f"Send command raw failed: {e}")
            self._on_disconnect()
            raise STBConnectionError(f"send command failed: {e}") from e

    def close(self) -> None:
        self._auto_reconnect = False
        
        if self._connection_monitor_task:
            self._connection_monitor_task.cancel()
            self._connection_monitor_task = None
            
        if self._receiver:
            self._receiver.stop()
            self._receiver = None

        if self._sock is None:
            return
        try:
            self._sock.close()
        finally:
            self._sock = None
            self.login_info = None

    def send_keyboard_text(self, text: str, force: bool = False) -> None:
        """Send text to STB keyboard input"""
        for char in text:
            self.send_keyboard_key(char, force=force)

    def send_keyboard_key(self, key: str, force: bool = False) -> None:
        """Send a keyboard key (enter, backspace, space, tab, dismiss, or single char)"""
        key_map = {
            "enter": 0x0d,
            "backspace": 0x08,
            "space": 0x20,
            "tab": 0x09,
            "dismiss": 0x1b,
        }
        
        if key.lower() in key_map:
            code = key_map[key.lower()]
        else:
            # Single character
            if len(key) != 1:
                raise ValueError(f"Invalid keyboard key: {key}")
            code = ord(key)
        
        self.send_keyboard_code(code, force=force)

    def send_keyboard_code(self, code: int, force: bool = False) -> None:
        """Send keyboard code to STB"""
        if force:
            # Force mode for platforms that don't support Unicode
            self._send_command_raw(0x1059, {"KeyCode": code})
        else:
            # Normal mode - send as KeyCode
            self._send_command(0x1059, {"KeyCode": code})

    @property
    def state(self):
        return self._processor.state

    def is_connected(self) -> bool:
        """Check if client is connected to STB"""
        return self._sock is not None and self.login_info is not None
    
    def set_auto_reconnect(self, enabled: bool) -> None:
        """Enable or disable automatic reconnection"""
        self._auto_reconnect = enabled
        if not enabled and self._connection_monitor_task:
            self._connection_monitor_task.cancel()
            self._connection_monitor_task = None
    
    # Favorite channels management methods
    def add_favorite_channel(self, channel_index: int, group_ids: Optional[list[int]] = None) -> bool:
        """Add a channel to favorites by index"""
        try:
            channel = self._find_channel(channel_index)
            if not channel:
                logger.error(f"Channel with index {channel_index} not found")
                return False
            
            success = self._favorites.add_favorite(channel, group_ids)
            if success:
                # Send STB command to mark as favorite
                self._sync_favorites_to_stb()
            return success
        except Exception as e:
            logger.error(f"Failed to add favorite channel: {e}")
            return False
    
    def remove_favorite_channel(self, channel_index: int, group_ids: Optional[list[int]] = None) -> bool:
        """Remove a channel from favorites by index"""
        try:
            channel = self._find_channel(channel_index)
            if not channel:
                logger.error(f"Channel with index {channel_index} not found")
                return False
            
            program_id = self._resolve_program_id(channel, channel_index)
            success = self._favorites.remove_favorite(program_id, group_ids)
            if success:
                # Send STB command to update favorites
                self._sync_favorites_to_stb()
            return success
        except Exception as e:
            logger.error(f"Failed to remove favorite channel: {e}")
            return False
    
    def is_channel_favorite(self, channel_index: int, group_id: Optional[int] = None) -> bool:
        """Check if a channel is in favorites"""
        try:
            channel = self._find_channel(channel_index)
            if not channel:
                return False
            
            program_id = self._resolve_program_id(channel, channel_index)
            return self._favorites.is_favorite(program_id, group_id)
        except Exception as e:
            logger.error(f"Failed to check favorite status: {e}")
            return False
    
    def get_favorite_channels(self, group_id: Optional[int] = None) -> list[dict]:
        """Get favorite channels, optionally filtered by group"""
        try:
            if group_id is None:
                favorites = self._favorites.get_all_favorites()
            else:
                favorites = self._favorites.get_favorites_by_group(group_id)
            
            # Convert to channel dictionaries with current channel info
            result = []
            for fav in favorites:
                # Try to find current channel info
                channel = None
                for ch in self._processor.state.channels:
                    if isinstance(ch, dict):
                        ch_program_id = str(ch.get("ProgramId", ch.get("ServiceID", ch.get("ServiceId", ""))))
                        if ch_program_id == fav.program_id:
                            channel = ch
                            break
                
                if channel:
                    # Use current channel info
                    result.append({
                        **channel,
                        "is_favorite": True,
                        "favorite_groups": list(fav.favorite_groups),
                        "added_time": fav.added_time
                    })
                else:
                    # Use cached favorite info
                    result.append({
                        "ProgramId": fav.program_id,
                        "ServiceIndex": fav.service_index,
                        "ServiceName": fav.channel_name,
                        "ServiceNum": fav.channel_number,
                        "is_favorite": True,
                        "favorite_groups": list(fav.favorite_groups),
                        "added_time": fav.added_time
                    })
            
            return result
        except Exception as e:
            logger.error(f"Failed to get favorite channels: {e}")
            return []
    
    def get_favorite_groups(self) -> list[dict]:
        """Get all favorite groups"""
        try:
            groups = self._favorites.get_favorite_groups()
            return [group.to_dict() for group in groups]
        except Exception as e:
            logger.error(f"Failed to get favorite groups: {e}")
            return []
    
    def create_favorite_group(self, group_name: str) -> Optional[dict]:
        """Create a new favorite group"""
        try:
            group = self._favorites.create_group(group_name)
            return group.to_dict() if group else None
        except Exception as e:
            logger.error(f"Failed to create favorite group: {e}")
            return None
    
    def delete_favorite_group(self, group_id: int) -> bool:
        """Delete a favorite group"""
        try:
            success = self._favorites.delete_group(group_id)
            if success:
                self._sync_favorites_to_stb()
            return success
        except Exception as e:
            logger.error(f"Failed to delete favorite group: {e}")
            return False
    
    def rename_favorite_group(self, group_id: int, new_name: str) -> bool:
        """Rename a favorite group"""
        try:
            ok = self._favorites.rename_group(group_id, new_name)
            if not ok:
                return False

            # Persist rename to STB (APK behavior: FavorRenamePos is 0-based position)
            try:
                pos0 = int(group_id) - 1
                stb_gid = None
                stb_groups = self._processor.state.fav_groups
                if isinstance(stb_groups, list) and 0 <= pos0 < len(stb_groups):
                    item = stb_groups[pos0]
                    if isinstance(item, dict):
                        stb_gid = item.get("FavorGroupID")
                try:
                    stb_gid_i = int(stb_gid) if stb_gid is not None else int(group_id)
                except Exception:
                    stb_gid_i = int(group_id)

                self._send_command(
                    GMS_MSG_DO_FAV_GROUP_RENAME,
                    [{"FavorRenamePos": pos0, "FavorNewName": str(new_name), "FavorGroupID": stb_gid_i}],
                )
            except Exception:
                pass

            return True
        except Exception as e:
            logger.error(f"Failed to rename favorite group: {e}")
            return False
    
    def get_favorites_summary(self) -> dict:
        """Get a summary of all favorites"""
        try:
            summary = self._favorites.get_favorites_summary()
            stb_groups = self._processor.state.fav_groups
            if isinstance(stb_groups, list):
                for g in summary.get("groups", []) or []:
                    if not isinstance(g, dict):
                        continue
                    gid = g.get("group_id")
                    try:
                        pos = int(gid) - 1
                    except Exception:
                        continue
                    if 0 <= pos < len(stb_groups):
                        item = stb_groups[pos]
                        if isinstance(item, dict):
                            name = item.get("favorGroupName")
                            if name not in (None, ""):
                                g["group_name"] = str(name)
            return summary
        except Exception as e:
            logger.error(f"Failed to get favorites summary: {e}")
            return {"total_favorites": 0, "total_groups": 0, "groups": []}

    def _fav_bitmask_for_group_ids(self, group_ids: set[int]) -> int:
        mask = 0
        for gid in sorted(group_ids):
            try:
                g = int(gid)
            except Exception:
                continue
            if 1 <= g <= 8:
                mask |= 1 << (g - 1)
        return int(mask)

    def _resolve_stb_favor_group_ids(self, group_ids: set[int]) -> list[int]:
        """Map local group ids (1..8) to STB FavorGroupID values from cmd=12 response."""
        stb_groups = self._processor.state.fav_groups
        out: list[int] = []
        for gid in sorted(group_ids):
            try:
                g = int(gid)
            except Exception:
                continue

            resolved: int | None = None
            if isinstance(stb_groups, list) and 1 <= g <= len(stb_groups):
                item = stb_groups[g - 1]
                if isinstance(item, dict):
                    raw = item.get("FavorGroupID")
                    try:
                        if raw is not None and raw != "":
                            resolved = int(raw)
                    except Exception:
                        resolved = None

            if resolved is None:
                resolved = g

            out.append(int(resolved))

        return out
    
    def _sync_favorites_to_stb(self) -> None:
        """Sync favorites to STB using the channel favorite mark command"""
        try:
            # Build desired per-program favorite bitmask (FavMark)
            desired_marks: dict[str, int] = {}
            desired_groups: dict[str, set[int]] = {}
            for pid, fav in self._favorites.favorite_channels.items():
                group_ids = set(int(g) for g in fav.favorite_groups if isinstance(g, int) or str(g).isdigit())
                mask = self._fav_bitmask_for_group_ids(group_ids)
                if mask <= 0:
                    continue
                desired_marks[str(pid)] = int(mask)
                desired_groups[str(pid)] = group_ids

            # Determine which programs need updates (including removals)
            all_pids = set(self._last_synced_fav_marks.keys()) | set(desired_marks.keys())
            changed: list[str] = []
            for pid in all_pids:
                if int(self._last_synced_fav_marks.get(pid, 0)) != int(desired_marks.get(pid, 0)):
                    changed.append(pid)

            if not changed:
                return

            # Group updates by (tv_state, fav_mark, stb_group_ids)
            batches: dict[tuple[int, int, tuple[int, ...]], list[str]] = {}
            for pid in changed:
                group_ids = desired_groups.get(pid, set())
                fav_mark = int(desired_marks.get(pid, 0))
                stb_group_ids = tuple(self._resolve_stb_favor_group_ids(group_ids))

                ch = self._find_channel_by_program_id(pid)
                tv_state = self._resolve_tv_state(ch)

                key = (int(tv_state), int(fav_mark), stb_group_ids)
                batches.setdefault(key, []).append(pid)

            # STB seems to accept 100 items per request (APK behavior)
            max_items = 100
            for (tv_state, fav_mark, stb_group_ids), pids in batches.items():
                group_list = list(stb_group_ids)
                for i in range(0, len(pids), max_items):
                    chunk = pids[i : i + max_items]
                    self.set_channel_fav_mark(
                        tv_state=int(tv_state),
                        fav_mark=int(fav_mark),
                        favor_group_ids=group_list if fav_mark > 0 else None,
                        program_ids=[str(x) for x in chunk],
                    )

            # Update sync state (keep only non-zero marks)
            self._last_synced_fav_marks = {pid: int(m) for pid, m in desired_marks.items() if int(m) > 0}
        except Exception as e:
            logger.error(f"Failed to sync favorites to STB: {e}")
    
    def _find_channel_by_program_id(self, program_id: str) -> Optional[dict]:
        """Find channel by program ID"""
        try:
            for ch in self._processor.state.channels:
                if isinstance(ch, dict):
                    ch_program_id = str(ch.get("ProgramId", ch.get("ServiceID", ch.get("ServiceId", ""))))
                    if ch_program_id == program_id:
                        return ch
            return None
        except Exception:
            return None
    
    @property
    def favorites(self) -> FavoritesManager:
        """Access to favorites manager"""
        return self._favorites


def _recv_exact(sock: socket.socket, n: int) -> bytes:
    chunks: list[bytes] = []
    remaining = n
    while remaining > 0:
        chunk = sock.recv(remaining)
        if not chunk:
            raise OSError("connection closed")
        chunks.append(chunk)
        remaining -= len(chunk)
    return b"".join(chunks)

