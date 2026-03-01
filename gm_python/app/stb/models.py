from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class GsMobileLoginInfo:
    magic_code: str
    stb_sn: bytes
    stb_sn_disp: str
    model_name: str
    stb_cpu_chip_id: bytes
    stb_flash_id: bytes
    stb_ip_address_disp: str
    platform_id: int
    sw_version: int
    stb_customer_id: int
    stb_model_id: int
    reserved_1: bytes
    sw_sub_version: int
    is_current_stb_connected_full: int
    client_type: int
    sat_enable: int
    sat2ip_enable: int
    send_data_type: int
    reserved_2: bytes
    reserved_3: bytes

    @staticmethod
    def _serial_number_to_disp(sn: bytes) -> str:
        if not sn or len(sn) < 6:
            return ""
        i_date = ((sn[0] & 0xFF) << 16) | ((sn[1] & 0xFF) << 8) | (sn[2] & 0xFF)
        i_serial = ((sn[3] & 0xFF) << 16) | ((sn[4] & 0xFF) << 8) | (sn[5] & 0xFF)
        return f"{i_date:06d}{i_serial:06d}"

    @classmethod
    def from_bytes(cls, transport_msg: bytes) -> "GsMobileLoginInfo":
        if len(transport_msg) < 108:
            raise ValueError(f"expected 108 bytes, got {len(transport_msg)}")

        magic_code = transport_msg[0:12].decode("ascii", errors="ignore")
        stb_sn = transport_msg[12:20]
        stb_sn_disp = cls._serial_number_to_disp(stb_sn)

        model_name_raw = transport_msg[20:52]
        model_name = model_name_raw.split(b"\x00", 1)[0].decode("utf-8", errors="ignore")

        stb_cpu_chip_id = transport_msg[52:60]
        stb_flash_id = transport_msg[60:68]

        ip_bytes = transport_msg[68:72]
        stb_ip_address_disp = f"{ip_bytes[3]}.{ip_bytes[2]}.{ip_bytes[1]}.{ip_bytes[0]}"

        platform_id = transport_msg[72] & 0xFF
        sw_version = ((transport_msg[73] & 0xFF) << 8) | (transport_msg[74] & 0xFF)
        stb_customer_id = transport_msg[75] & 0xFF
        stb_model_id = transport_msg[76] & 0xFF
        reserved_1 = transport_msg[77:80]

        sw_sub_version = int.from_bytes(transport_msg[80:84], "little", signed=False)

        flags = transport_msg[84] & 0xFF
        is_current_stb_connected_full = flags & 0x1
        client_type = (flags & 0x2) >> 1
        sat_enable = (flags & 0x4) >> 2
        sat2ip_enable = (flags & 0x18) >> 3
        send_data_type = (flags & 0x40) >> 6

        reserved_2 = transport_msg[84:88]
        reserved_3 = transport_msg[88:108]

        return cls(
            magic_code=magic_code,
            stb_sn=stb_sn,
            stb_sn_disp=stb_sn_disp,
            model_name=model_name,
            stb_cpu_chip_id=stb_cpu_chip_id,
            stb_flash_id=stb_flash_id,
            stb_ip_address_disp=stb_ip_address_disp,
            platform_id=platform_id,
            sw_version=sw_version,
            stb_customer_id=stb_customer_id,
            stb_model_id=stb_model_id,
            reserved_1=reserved_1,
            sw_sub_version=sw_sub_version,
            is_current_stb_connected_full=is_current_stb_connected_full,
            client_type=client_type,
            sat_enable=sat_enable,
            sat2ip_enable=sat2ip_enable,
            send_data_type=send_data_type,
            reserved_2=reserved_2,
            reserved_3=reserved_3,
        )
