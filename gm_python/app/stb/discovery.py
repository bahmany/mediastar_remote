from __future__ import annotations

import logging
import socket
import time
from typing import Any

from app.stb.constants import G_MS_BROADCAST_INFO_MAGIC_CODE, G_MS_BROADCAST_PORT, STB_LOGIN_INFO_DATA_LENGTH
from app.stb.models import GsMobileLoginInfo
from app.stb.scramble import scramble_stb_info_for_broadcast

logger = logging.getLogger(__name__)


def discover_stb_broadcast(
    *,
    timeout_s: float = 5.0,
    port: int = G_MS_BROADCAST_PORT,
    max_results: int = 50,
) -> list[dict[str, Any]]:
    found_by_sn: dict[str, GsMobileLoginInfo] = {}

    end_time = time.time() + max(0.1, float(timeout_s))

    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        try:
            sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        except Exception:
            pass
        try:
            sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        except Exception:
            pass

        sock.settimeout(0.5)
        sock.bind(("", int(port)))

        while time.time() < end_time and len(found_by_sn) < int(max_results):
            try:
                data, _addr = sock.recvfrom(2048)
            except socket.timeout:
                continue
            except OSError as e:
                logger.warning("udp discovery recv error: %s", e)
                break

            if not data or len(data) != STB_LOGIN_INFO_DATA_LENGTH:
                continue

            buf = bytearray(data)
            try:
                scramble_stb_info_for_broadcast(buf, len(buf))
            except Exception:
                continue

            try:
                magic = bytes(buf[0:12]).decode("ascii", errors="ignore")
            except Exception:
                continue

            if magic != G_MS_BROADCAST_INFO_MAGIC_CODE:
                continue

            try:
                info = GsMobileLoginInfo.from_bytes(bytes(buf))
            except Exception:
                continue

            if int(getattr(info, "is_current_stb_connected_full", 0)) == 1:
                continue

            sn = str(getattr(info, "stb_sn_disp", ""))
            if not sn:
                sn = str(getattr(info, "stb_ip_address_disp", ""))
            found_by_sn[sn] = info

    finally:
        try:
            sock.close()
        except Exception:
            pass

    devices: list[dict[str, Any]] = []
    for info in found_by_sn.values():
        devices.append(
            {
                "ip": str(getattr(info, "stb_ip_address_disp", "")),
                "model_name": str(getattr(info, "model_name", "")),
                "serial": str(getattr(info, "stb_sn_disp", "")),
                "platform_id": int(getattr(info, "platform_id", 0)),
                "send_data_type": int(getattr(info, "send_data_type", 0)),
                "sw_version": int(getattr(info, "sw_version", 0)),
                "sw_sub_version": int(getattr(info, "sw_sub_version", 0)),
                "sat_enable": int(getattr(info, "sat_enable", 0)),
                "sat2ip_enable": int(getattr(info, "sat2ip_enable", 0)),
            }
        )

    devices.sort(key=lambda d: (d.get("ip") or "", d.get("serial") or ""))
    return devices
