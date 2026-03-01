from __future__ import annotations

import asyncio
import base64
from dataclasses import asdict, is_dataclass
import json
import logging
import time
from pathlib import Path
from typing import Any

from fastapi import FastAPI, Request, WebSocket, WebSocketDisconnect
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles

from app.stb.client import STBClient, STBConnectionError
from app.stb.discovery import discover_stb_broadcast

log_path = Path(__file__).resolve().parent.parent / "gmscreen.log"

root_logger = logging.getLogger()
root_logger.handlers.clear()
root_logger.setLevel(logging.DEBUG)

_file_handler = logging.FileHandler(log_path, encoding="utf-8")
_file_handler.setLevel(logging.DEBUG)
_file_handler.setFormatter(logging.Formatter("%(asctime)s [%(levelname)s] %(name)s: %(message)s"))

_console_handler = logging.StreamHandler()
_console_handler.setLevel(logging.INFO)

root_logger.addHandler(_file_handler)
root_logger.addHandler(_console_handler)

for _lname in ("uvicorn", "uvicorn.error", "uvicorn.access"):
    logging.getLogger(_lname).propagate = True

logger = logging.getLogger(__name__)

app = FastAPI()

app.mount(
    "/static",
    StaticFiles(directory=str(Path(__file__).parent / "static")),
    name="static",
)


def _login_info_view(login_info: Any) -> dict[str, Any] | None:
    if login_info is None:
        return None
    try:
        return {
            "stb_ip_address_disp": str(getattr(login_info, "stb_ip_address_disp", "")),
            "platform_id": int(getattr(login_info, "platform_id", 0)),
            "send_data_type": int(getattr(login_info, "send_data_type", 0)),
            "model_name": str(getattr(login_info, "model_name", "")),
        }
    except Exception as e:
        logger.error("failed to convert login_info to view: %s", e)
        return None


def _connected_client() -> tuple[STBClient | None, Any]:
    client = getattr(app.state, "stb_client", None)
    login_info = getattr(app.state, "login_info", None)
    if client is None:
        return None, None

    if login_info is None and getattr(client, "login_info", None) is not None:
        login_info = client.login_info
        app.state.login_info = login_info

    if login_info is None:
        return None, None
    if not client.is_connected():
        logger.warning("client not connected, clearing state")
        app.state.stb_client = None
        app.state.login_info = None
        return None, None
    if login_info is None:
        logger.warning("login_info is None, clearing client")
        app.state.stb_client = None
        app.state.login_info = None
        return None, None
    app.state.login_info = login_info
    return client, login_info


def _to_jsonable(value: Any) -> Any:
    if value is None:
        return None
    if isinstance(value, (str, int, float, bool)):
        return value
    if isinstance(value, bytes):
        return base64.b64encode(value).decode("ascii")
    if isinstance(value, bytearray):
        return base64.b64encode(bytes(value)).decode("ascii")
    if is_dataclass(value):
        return _to_jsonable(asdict(value))
    if isinstance(value, dict):
        return {str(k): _to_jsonable(v) for k, v in value.items()}
    if isinstance(value, (list, tuple, set)):
        return [_to_jsonable(v) for v in value]
    return str(value)


@app.get("/", response_class=None)
async def index(request: Request) -> Any:
    ui_index = Path(__file__).parent / "static" / "ui" / "index.html"
    return FileResponse(str(ui_index))


@app.get("/ui")
async def ui_app() -> Any:
    ui_index = Path(__file__).parent / "static" / "ui" / "index.html"
    return FileResponse(str(ui_index))


@app.websocket("/ws")
async def websocket_endpoint(websocket: WebSocket) -> None:
    await websocket.accept()

    # Track connection state
    is_connected = True
    queue: asyncio.Queue = asyncio.Queue(maxsize=1000)

    async def _send_snapshot() -> None:
        nonlocal is_connected
        if not is_connected:
            return
        try:
            client, login_info = _connected_client()
            connected = client is not None and login_info is not None
            state = None
            if connected and client is not None:
                st = client.state
                state = {
                    "channels": st.channels,
                    "fav_groups": st.fav_groups,
                    "satellites": st.satellites,
                    "current_channel_index": st.current_channel_index,
                    "current_program_id": st.current_program_id,
                    "channel_list_type": st.channel_list_type,
                    "epg_events": st.epg_events,
                    "timers": st.timers,
                    "stb_info": st.stb_info,
                    "favorites_summary": client.get_favorites_summary(),
                    "cache_info": {
                        "channels_cached": st.channels_cache_time is not None,
                        "cache_age_minutes": ((time.time() - st.channels_cache_time) / 60) if st.channels_cache_time else 0,
                        "cache_valid": client._processor.is_channels_cache_valid(cache_hours=0)
                    }
                }
            payload = {
                "type": "snapshot",
                "connected": connected,
                "login_info": _login_info_view(login_info) if login_info else None,
                "state": state,
            }
            await websocket.send_json(payload)
        except Exception as e:
            logger.warning(f"Failed to send snapshot: {e}")
            # Mark as disconnected if send fails
            is_connected = False

    async def _queue_consumer() -> None:
        nonlocal is_connected
        try:
            while is_connected:
                ev = await queue.get()
                try:
                    if not is_connected:
                        break
                        
                    if isinstance(ev, dict) and "event" in ev:
                        event_type = ev.get("event")
                        event_data = _to_jsonable(ev.get("data"))
                        
                        # Handle connection events with user-friendly notifications
                        if event_type == "disconnected":
                            await websocket.send_json({
                                "type": "notification",
                                "level": "error",
                                "message": "STB connection lost. Attempting to reconnect..."
                            })
                        elif event_type == "reconnecting":
                            attempt = event_data.get("attempt", 1) if event_data else 1
                            max_attempts = event_data.get("max_attempts", 5) if event_data else 5
                            await websocket.send_json({
                                "type": "notification", 
                                "level": "info",
                                "message": f"Reconnecting to STB... (attempt {attempt}/{max_attempts})"
                            })
                        elif event_type == "reconnected":
                            await websocket.send_json({
                                "type": "notification",
                                "level": "success", 
                                "message": "STB reconnected successfully!"
                            })
                        elif event_type == "reconnect_failed":
                            await websocket.send_json({
                                "type": "notification",
                                "level": "error",
                                "message": "Failed to reconnect to STB. Please try connecting manually."
                            })
                        
                        # Send the original event as well
                        await websocket.send_json({"event": event_type, "data": event_data})

                        if event_type in ("disconnected", "reconnected"):
                            await _send_snapshot()
                        
                    elif isinstance(ev, (list, tuple)) and len(ev) >= 2:
                        await websocket.send_json({"event": ev[0], "data": _to_jsonable(ev[1])})
                    else:
                        await websocket.send_json({"event": "unknown", "data": _to_jsonable(ev)})
                except Exception as e:
                    logger.warning(f"Failed to send queue event: {e}")
                    # Connection likely closed, break the loop
                    is_connected = False
                    break
                queue.task_done()
        except Exception:
            logger.exception("queue consumer error")

    def _attach_queue() -> None:
        client = getattr(app.state, "stb_client", None)
        if client is not None:
            client.set_event_queue(queue)

    def _detach_queue() -> None:
        client = getattr(app.state, "stb_client", None)
        if client is not None:
            client.set_event_queue(None)

    _attach_queue()
    consumer_task = asyncio.create_task(_queue_consumer())
    await _send_snapshot()

    try:
        while is_connected:
            try:
                raw = await websocket.receive_text()
            except Exception as e:
                logger.info(f"WebSocket receive error: {e}")
                # Connection closed
                is_connected = False
                break
                
            try:
                msg = json.loads(raw)
                msg_type = msg.get("type")
                payload = msg.get("payload", {})
                msg_id = msg.get("id")

                async def _reply(ok: bool, data: Any = None, error: str | None = None) -> None:
                    nonlocal is_connected
                    try:
                        if not is_connected:
                            return
                        resp = {"type": "response", "id": msg_id, "ok": ok}
                        if data is not None:
                            resp["data"] = _to_jsonable(data)
                        if error is not None:
                            resp["error"] = error
                        await websocket.send_json(resp)
                    except Exception as e:
                        logger.warning(f"Failed to send WebSocket reply: {e}")
                        # Connection is likely closed, mark as disconnected
                        is_connected = False

                if msg_type == "ping":
                    # Handle heartbeat ping - just respond with pong
                    await _reply(True, {"type": "pong", "timestamp": payload.get("timestamp")})

                elif msg_type == "discover_stb":
                    timeout_s = float(payload.get("timeout") or 5.0)
                    max_results = int(payload.get("max_results") or 50)
                    try:
                        devices = await asyncio.to_thread(
                            discover_stb_broadcast,
                            timeout_s=timeout_s,
                            max_results=max_results,
                        )
                        app.state.last_discovered = devices
                        await _reply(True, {"devices": devices})
                    except Exception as e:
                        await _reply(False, error=str(e))
                    
                elif msg_type == "connect":
                    ip = str(payload.get("ip") or "")
                    port = int(payload.get("port") or 20000)
                    client = STBClient(ip=ip, port=port)
                    try:
                        login_info = await asyncio.to_thread(client.connect_and_login)
                    except STBConnectionError as e:
                        await _reply(False, error=str(e))
                        await _send_snapshot()
                        continue

                    app.state.stb_client = client
                    app.state.login_info = login_info
                    app.state.last_target = {"ip": ip, "port": port}
                    _attach_queue()
                    await _reply(True, {"login_info": _login_info_view(login_info)})
                    await _send_snapshot()

                    async def _bg_bootstrap() -> None:
                        try:
                            await asyncio.to_thread(client.request_stb_info)
                            await asyncio.to_thread(client.request_current_channel)
                            await asyncio.to_thread(client.request_fav_group_names)
                            await client.request_channel_list(timeout=60.0, force_refresh=False, cache_hours=0)
                            await _send_snapshot()
                            # Send success notification
                            try:
                                await websocket.send_json({
                                    "type": "notification",
                                    "level": "success",
                                    "message": "STB connected and data loaded successfully"
                                })
                            except Exception:
                                pass
                        except Exception as e:
                            logger.warning(f"Bootstrap failed: {e}")
                            # Send notification to UI about bootstrap failure
                            try:
                                await websocket.send_json({
                                    "type": "notification",
                                    "level": "warning", 
                                    "message": f"Initial data loading failed: {str(e)}"
                                })
                            except Exception:
                                pass
                            await _send_snapshot()

                    asyncio.create_task(_bg_bootstrap())

                elif msg_type == "disconnect":
                    client = getattr(app.state, "stb_client", None)
                    if client is not None:
                        await asyncio.to_thread(client.close)
                    app.state.stb_client = None
                    app.state.login_info = None
                    _detach_queue()
                    await _reply(True)
                    await _send_snapshot()

                elif msg_type == "reconnect":
                    cur_client, cur_login = _connected_client()
                    if cur_client is not None and cur_login is not None:
                        await _reply(True, {"login_info": _login_info_view(cur_login)})
                        await _send_snapshot()
                        continue

                    target = getattr(app.state, "last_target", None)
                    if not isinstance(target, dict) or not target.get("ip"):
                        await _reply(False, error="no previous target")
                        await _send_snapshot()
                        continue

                    ip = str(target.get("ip"))
                    port = int(target.get("port") or 20000)
                    client = STBClient(ip=ip, port=port)
                    try:
                        login_info = await asyncio.to_thread(client.connect_and_login)
                    except STBConnectionError as e:
                        await _reply(False, error=str(e))
                        await _send_snapshot()
                        continue

                    app.state.stb_client = client
                    app.state.login_info = login_info
                    _attach_queue()
                    await _reply(True, {"login_info": _login_info_view(login_info)})
                    await _send_snapshot()

                    try:
                        await asyncio.to_thread(client.request_stb_info)
                        await client.request_channel_list(timeout=60.0, force_refresh=False)
                        await asyncio.to_thread(client.request_current_channel)
                        await asyncio.to_thread(client.request_fav_group_names)
                        await _send_snapshot()
                    except Exception as e:
                        logger.warning(f"Reconnect bootstrap failed: {e}")
                        try:
                            await websocket.send_json({
                                "type": "notification",
                                "level": "warning",
                                "message": f"Reconnection data loading failed: {str(e)}"
                            })
                        except Exception:
                            pass

                elif msg_type == "get_state":
                    await _reply(True)
                    await _send_snapshot()

                else:
                    client = getattr(app.state, "stb_client", None)
                    if client is None or getattr(client, "login_info", None) is None:
                        await _reply(False, error="not connected")
                        continue

                    if msg_type == "refresh":
                        await asyncio.to_thread(client.request_current_channel)
                        await asyncio.to_thread(client.request_stb_info)
                        await asyncio.to_thread(client.request_satellite_list)
                        await _reply(True)

                    elif msg_type == "refresh_channels":
                        force = bool(payload.get("force") or False)
                        timeout = float(payload.get("timeout") or 60.0)
                        try:
                            count = await client.request_channel_list(timeout=timeout, force_refresh=force, cache_hours=0)
                            await _reply(True, {"channel_count": count})
                        except STBConnectionError as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "channel_play":
                        index = int(payload.get("index") or 0)
                        try:
                            await asyncio.to_thread(client.change_channel, index)
                            await _reply(True)
                        except STBConnectionError as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "remote_key":
                        key_value = int(payload.get("key_value") or 0)
                        try:
                            await asyncio.to_thread(client.send_remote_key, key_value)
                            await _reply(True)
                        except STBConnectionError as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "keyboard_send":
                        text = str(payload.get("text") or "")
                        force = bool(payload.get("force") or False)
                        try:
                            await asyncio.to_thread(client.send_keyboard_text, text, force=force)
                            await _reply(True)
                        except STBConnectionError as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "keyboard_key":
                        key = str(payload.get("key") or "")
                        force = bool(payload.get("force") or False)
                        try:
                            await asyncio.to_thread(client.send_keyboard_key, key, force=force)
                            await _reply(True)
                        except STBConnectionError as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "keyboard_code":
                        code = int(payload.get("code") or 0)
                        force = bool(payload.get("force") or False)
                        try:
                            await asyncio.to_thread(client.send_keyboard_code, code, force=force)
                            await _reply(True)
                        except STBConnectionError as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "epg":
                        channel = int(payload.get("channel") or 0)
                        try:
                            await asyncio.to_thread(client.request_epg, channel)
                            await _reply(True)
                        except STBConnectionError as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "cache_status":
                        processor = client._processor
                        await _reply(
                            True,
                            {
                                "channel_count": len(processor.state.channels),
                                "epg_count": len(processor.state.epg_events),
                                "timer_count": len(processor.state.timers),
                                "satellite_count": len(processor.state.satellites),
                            },
                        )

                    elif msg_type == "add_favorite":
                        channel_index = int(payload.get("channel_index") or 0)
                        group_ids = payload.get("group_ids")
                        try:
                            success = client.add_favorite_channel(channel_index, group_ids)
                            await _reply(True, {"success": success})
                            if success:
                                await _send_snapshot()  # Update UI with new favorites
                        except Exception as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "remove_favorite":
                        channel_index = int(payload.get("channel_index") or 0)
                        group_ids = payload.get("group_ids")
                        try:
                            success = client.remove_favorite_channel(channel_index, group_ids)
                            await _reply(True, {"success": success})
                            if success:
                                await _send_snapshot()  # Update UI
                        except Exception as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "get_favorites":
                        group_id = payload.get("group_id")
                        try:
                            favorites = client.get_favorite_channels(group_id)
                            await _reply(True, {"favorites": favorites})
                        except Exception as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "get_favorite_groups":
                        try:
                            groups = client.get_favorite_groups()
                            await _reply(True, {"groups": groups})
                        except Exception as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "create_favorite_group":
                        group_name = str(payload.get("group_name") or "")
                        if not group_name:
                            await _reply(False, error="group_name is required")
                        else:
                            try:
                                group = client.create_favorite_group(group_name)
                                if group:
                                    await _reply(True, {"group": group})
                                else:
                                    await _reply(False, error="Failed to create group")
                            except Exception as e:
                                await _reply(False, error=str(e))

                    elif msg_type == "delete_favorite_group":
                        group_id = int(payload.get("group_id") or 0)
                        try:
                            success = client.delete_favorite_group(group_id)
                            await _reply(True, {"success": success})
                            if success:
                                await _send_snapshot()  # Update UI
                        except Exception as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "rename_favorite_group":
                        group_id = int(payload.get("group_id") or 0)
                        new_name = str(payload.get("new_name") or "")
                        if not new_name:
                            await _reply(False, error="new_name is required")
                        else:
                            try:
                                success = client.rename_favorite_group(group_id, new_name)
                                await _reply(True, {"success": success})
                                if success:
                                    await _send_snapshot()  # Update UI
                            except Exception as e:
                                await _reply(False, error=str(e))

                    elif msg_type == "get_favorites_summary":
                        try:
                            summary = client.get_favorites_summary()
                            await _reply(True, {"summary": summary})
                        except Exception as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "export_favorites":
                        try:
                            export_data = client.favorites.export_favorites()
                            await _reply(True, {"export_data": export_data})
                        except Exception as e:
                            await _reply(False, error=str(e))

                    elif msg_type == "import_favorites":
                        import_data = payload.get("import_data")
                        if not import_data:
                            await _reply(False, error="import_data is required")
                        else:
                            try:
                                success = client.favorites.import_favorites(import_data)
                                await _reply(True, {"success": success})
                                if success:
                                    await _send_snapshot()  # Update UI with imported favorites
                            except Exception as e:
                                await _reply(False, error=str(e))

                    else:
                        await _reply(False, error=f"unknown message type: {msg_type}")

            except json.JSONDecodeError:
                try:
                    if is_connected:
                        await websocket.send_json({"type": "error", "error": "invalid json"})
                except Exception:
                    logger.warning("Failed to send JSON decode error - connection likely closed")
                    is_connected = False
            except Exception:
                logger.exception("websocket message handling error")
                try:
                    if is_connected:
                        await websocket.send_json({"type": "error", "error": "internal server error"})
                except Exception:
                    logger.warning("Failed to send internal error - connection likely closed")
                    is_connected = False

    except WebSocketDisconnect:
        pass
    except Exception:
        logger.exception("websocket endpoint error")
    finally:
        _detach_queue()

        consumer_task.cancel()
        try:
            await consumer_task
        except asyncio.CancelledError:
            pass
