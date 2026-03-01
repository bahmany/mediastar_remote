"""Connection monitoring and recovery methods for STB client"""
import asyncio
import logging
import time
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from .client import STBClient

logger = logging.getLogger(__name__)


async def connection_monitor(client: 'STBClient') -> None:
    """Monitor connection health and send periodic heartbeats"""
    try:
        while client.is_connected():
            await asyncio.sleep(client._heartbeat_interval)
            
            last_rx = getattr(client, "_last_rx_activity", None)
            if last_rx is None:
                last_rx = getattr(client, "_last_activity", time.time())

            if time.time() - float(last_rx) > client._connection_timeout:
                logger.warning("Connection appears inactive (no RX data within timeout); disconnecting")
                client._on_disconnect()
                break
    except asyncio.CancelledError:
        pass
    except Exception as e:
        logger.error(f"Connection monitor error: {e}")


async def attempt_reconnect(client: 'STBClient') -> bool:
    """Attempt to reconnect to STB with exponential backoff"""
    if not client._auto_reconnect:
        return False

    while client._auto_reconnect and client._reconnect_attempts < client._max_reconnect_attempts:
        client._reconnect_attempts += 1
        delay = min(client._reconnect_delay * (2 ** (client._reconnect_attempts - 1)), 30.0)

        logger.info(f"Attempting reconnect {client._reconnect_attempts}/{client._max_reconnect_attempts} in {delay}s")

        if client._event_queue:
            try:
                await client._event_queue.put({
                    "event": "reconnecting",
                    "data": {
                        "attempt": client._reconnect_attempts,
                        "max_attempts": client._max_reconnect_attempts,
                        "delay": delay,
                    },
                })
            except Exception:
                pass

        await asyncio.sleep(delay)

        try:
            login_info = await asyncio.to_thread(client.connect_and_login)
            logger.info("Reconnection successful")

            if client._event_queue:
                try:
                    await client._event_queue.put({
                        "event": "reconnected",
                        "data": {"login_info": login_info},
                    })
                except Exception:
                    pass

            return True

        except Exception as e:
            logger.warning(f"Reconnect attempt {client._reconnect_attempts} failed: {e}")

    logger.error("Max reconnection attempts reached, giving up")
    if client._event_queue:
        try:
            await client._event_queue.put({
                "event": "reconnect_failed",
                "data": {"error": "Max attempts reached"},
            })
        except Exception:
            pass

    return False
