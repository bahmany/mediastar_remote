# MediaStar 4030 4K STB Protocol Documentation

This document describes the communication protocol used by the MediaStar 4030 4K Set-Top Box (STB) for mobile/remote control applications. The protocol is based on reverse-engineering the official GMScreen Android application.

## Table of Contents

1. [Overview](#overview)
2. [Network Configuration](#network-configuration)
3. [Message Framing](#message-framing)
4. [Login Protocol](#login-protocol)
5. [Response Message Format](#response-message-format)
6. [Data Serialization](#data-serialization)
7. [Data Compression](#data-compression)
8. [Command Reference](#command-reference)
9. [Remote Control Keys](#remote-control-keys)
10. [Data Models](#data-models)
11. [Error Codes](#error-codes)
12. [Keep-Alive Mechanism](#keep-alive-mechanism)
13. [Advanced Features](#advanced-features)

---

## Overview

The MediaStar 4030 4K STB exposes a TCP socket interface that allows external applications to:

- Authenticate and connect to the STB
- Send remote control key commands
- Retrieve channel lists and EPG data
- Manage timers and recordings (PVR)
- Control parental settings
- Access satellite/transponder information
- Use SAT>IP streaming capabilities
- Chat functionality (GChat)
- Spectrum analyzer features

---

## Network Configuration

| Parameter | Value | Description |
|-----------|-------|-------------|
| **Default TCP Port** | `20000` (0x4E20) | Main communication port |
| **Broadcast Port** | `25860` (0x6504) | UDP discovery port |
| **UPnP Port Range** | `20001-20005` | UPnP service ports |
| **Socket Timeout** | `3000ms` (0xBB8) | TCP connection timeout |

---

## Message Framing

### Outgoing Messages (Client → STB)

All messages sent to the STB use the following frame format:

```
┌─────────┬───────────────────┬─────────┬─────────────────┐
│  Start  │   Length (7 dig)  │   End   │     Payload     │
│ 5 bytes │      7 bytes      │ 3 bytes │   N bytes       │
└─────────┴───────────────────┴─────────┴─────────────────┘
```

- **Start**: ASCII literal `"Start"` (5 bytes)
- **Length**: 7-digit zero-padded ASCII decimal representing payload length
- **End**: ASCII literal `"End"` (3 bytes)
- **Payload**: UTF-8 encoded XML or JSON command

**Example Frame:**
```
Start0000095End<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><Command request="1040"><KeyValue>5</KeyValue></Command>
```

### Python Implementation:

```python
def build_socket_frame(payload: bytes) -> bytes:
    length_str = f"{len(payload):07d}".encode("ascii")
    return b"Start" + length_str + b"End" + payload
```

---

## Login Protocol

### Step 1: TCP Connection

Connect to the STB at `<ip>:20000`.

### Step 2: Send Login Request

Send a login command (request type `0x3E6` / `998`):

**XML Format:**
```xml
<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
<Command request="998">
    <data>Windows-10-10.0.19041-SP0</data>
    <uuid>550e8400-e29b-41d4-a716-446655440000</uuid>
</Command>
```

### Step 3: Receive Login Response

The STB responds with a **108-byte binary structure** that is XOR-scrambled.

### Descrambling Algorithm

The login response uses a reversible XOR + swap scrambling:

```python
def scramble_stb_info_for_broadcast(send_buff: bytearray, buff_length: int, xor_value: int = 0x5B) -> None:
    half = buff_length // 2
    for i in range(half):
        j = buff_length - 1 - i
        # Swap bytes
        temp = send_buff[j]
        send_buff[j] = send_buff[i]
        send_buff[i] = temp
        # XOR both bytes
        send_buff[i] = (send_buff[i] ^ xor_value) & 0xFF
        send_buff[j] = (send_buff[j] ^ xor_value) & 0xFF
    
    # Handle middle byte for odd-length buffers
    if buff_length % 2:
        mid = buff_length // 2
        send_buff[mid] = (send_buff[mid] ^ xor_value) & 0xFF
```

- **XOR Value**: `0x5B` (91 decimal)
- **Magic Code**: `"39WwijOog54a"` (12 bytes) - validates successful descrambling

### Login Response Structure (108 bytes)

| Offset | Size | Field | Description |
|--------|------|-------|-------------|
| 0 | 12 | `magic_code` | Must be `"39WwijOog54a"` |
| 12 | 8 | `stb_sn` | Serial number (raw bytes) |
| 20 | 32 | `model_name` | Model name string (null-terminated) |
| 52 | 8 | `stb_cpu_chip_id` | CPU chip identifier |
| 60 | 8 | `stb_flash_id` | Flash chip identifier |
| 68 | 4 | `stb_ip_address` | IP address (little-endian) |
| 72 | 1 | `platform_id` | Hardware platform identifier |
| 73 | 2 | `sw_version` | Software version (big-endian) |
| 75 | 1 | `stb_customer_id` | Customer/OEM identifier |
| 76 | 1 | `stb_model_id` | Model identifier |
| 77 | 3 | `reserved_1` | Reserved bytes |
| 80 | 4 | `sw_sub_version` | Sub-version (little-endian) |
| 84 | 1 | `flags` | Feature flags (see below) |
| 85 | 3 | `reserved_2` | Reserved bytes |
| 88 | 20 | `reserved_3` | Reserved bytes |

### Feature Flags (Byte 84)

| Bit(s) | Mask | Field | Description |
|--------|------|-------|-------------|
| 0 | 0x01 | `is_current_stb_connected_full` | STB connection slots full |
| 1 | 0x02 | `client_type` | Client type (0=normal, 1=master) |
| 2 | 0x04 | `sat_enable` | Satellite features enabled |
| 3-4 | 0x18 | `sat2ip_enable` | SAT>IP mode (0=off, 1-3=modes) |
| 6 | 0x40 | `send_data_type` | **0=XML, 1=JSON** |

---

## Response Message Format

STB responses use a 16-byte header followed by optional data:

```
┌──────────────┬─────────────┬──────────────┬────────────────┬─────────────┐
│    GCDH      │ Data Length │ Command Type │ Response State │    Data     │
│   4 bytes    │   4 bytes   │   4 bytes    │    4 bytes     │   N bytes   │
└──────────────┴─────────────┴──────────────┴────────────────┴─────────────┘
```

- **GCDH**: ASCII literal `"GCDH"` (header magic)
- **Data Length**: 32-bit big-endian unsigned integer
- **Command Type**: 32-bit big-endian command identifier
- **Response State**: 32-bit big-endian response code (0 = success)
- **Data**: Optional payload (may be compressed)

### Compression Detection

If the data begins with `"BegC"` (0x42656743), it is compressed using raw DEFLATE.

```python
COMPRESS_CONTROL_STRING = "BegC"

def gs_decompress(data: bytes) -> bytes:
    decompressor = zlib.decompressobj(-zlib.MAX_WBITS)
    decompressed = decompressor.decompress(data)
    decompressed += decompressor.flush()
    return decompressed
```

---

## Data Serialization

The STB supports both XML and JSON formats. The format is determined by the `send_data_type` flag in the login response.

### XML Format

```xml
<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
<Command request="REQUEST_TYPE">
    <field1>value1</field1>
    <field2>value2</field2>
</Command>
```

### JSON Format

```json
{
    "request": "REQUEST_TYPE",
    "array": [
        {"field1": "value1", "field2": "value2"}
    ]
}
```

---

## Data Compression

Large responses use raw DEFLATE compression (zlib without headers):

```python
import zlib

def gs_compress(data: bytes) -> bytes:
    compressor = zlib.compressobj(zlib.Z_DEFAULT_COMPRESSION, zlib.DEFLATED, -zlib.MAX_WBITS)
    compressed = compressor.compress(data)
    compressed += compressor.flush()
    return compressed

def gs_decompress(data: bytes) -> bytes:
    decompressor = zlib.decompressobj(-zlib.MAX_WBITS)
    decompressed = decompressor.decompress(data)
    decompressed += decompressor.flush()
    return decompressed
```

---

## Command Reference

### Request Commands (Client → STB)

| Code (Hex) | Code (Dec) | Name | Description |
|------------|------------|------|-------------|
| **Authentication & Info** |
| 0x3E6 | 998 | `GMS_MSG_REQUEST_LOGIN_INFO` | Login/authentication request |
| 0x00 | 0 | `GMS_MSG_REQUEST_CHANNEL_LIST` | Get channel list |
| 0x01 | 1 | `GMS_MSG_REQUEST_EVENT_TIMER` | Get timer list |
| 0x02 | 2 | `GMS_MSG_REQUEST_SLEEP_TIMER` | Get sleep timer state |
| 0x03 | 3 | `GMS_MSG_REQUEST_PLAYING_CHANNEL` | Get currently playing channel |
| 0x05 | 5 | `GMS_MSG_REQUEST_PROGRAM_EPG` | Get EPG for channel |
| 0x0B | 11 | `GMS_MSG_REQUEST_STB_TIME` | Get STB current time |
| 0x0C | 12 | `GMS_MSG_REQUEST_FAV_GROUP_NAMES` | Get favorite group names |
| 0x0D | 13 | `GMS_MSG_REQUEST_CONTROL_SETTING` | Get parental control settings |
| 0x0E | 14 | `GMS_MSG_REQUEST_CHANNEL_LIST_TYPE` | Get current channel list type |
| 0x0F | 15 | `GMS_MSG_REQUEST_STB_INFO` | Get STB hardware info |
| 0x10 | 16 | `GMS_MSG_REQUEST_STB_SETTING_LOGIN` | Get STB settings |
| 0x11 | 17 | `GMS_MSG_REQUEST_TV_RADIO_TYPE` | Get TV/Radio mode |
| 0x12 | 18 | `GMS_MSG_REQUEST_CHANNEL_SORT_TYPE` | Get channel sort type |
| 0x13 | 19 | `GMS_MSG_REQUEST_MUTE_STATE` | Get mute state |
| 0x14 | 20 | `GMS_MSG_REQUEST_CONTROL_PASSWORD_SWITCH` | Get password switch state |
| 0x15 | 21 | `GMS_MSG_REQUEST_UPDATE_HEADER` | Get update info |
| 0x16 | 22 | `GMS_MSG_REQUEST_SAT_LIST` | Get satellite list |
| 0x17 | 23 | `GMS_MSG_REQUEST_SAT_SELECT_NO` | Get selected satellite |
| 0x18 | 24 | `GMS_MSG_REQUEST_TP_LIST` | Get transponder list |
| 0x19 | 25 | `GMS_MSG_REQUEST_RS232_PRINTED_DATA` | Get RS232 debug output |
| 0x1A | 26 | `GMS_MSG_REQUEST_SOCKET_KEEP_ALIVE` | Keep-alive ping |
| 0x1B | 27 | `GMS_MSG_REQUEST_PVR_LIST` | Get PVR recording list |
| 0x1C | 28 | `GMS_MSG_REQUEST_MY_IP_FROM_STB` | Get client IP from STB |
| 0x1D | 29 | `GMS_MSG_REQUEST_STB_SCREENSHOT_DATA_INFO` | Get screenshot info |
| **Debug Commands** |
| 0x06 | 6 | `GMS_MSG_REQUEST_RS232_DATA_DEBUG` | RS232 debug data |
| 0x07 | 7 | `GMS_MSG_REQUEST_CUR_USER_DATA_DEBUG` | User data debug |
| 0x08 | 8 | `GMS_MSG_REQUEST_CHANNEL_DATA_DEBUG` | Channel data debug |
| 0x09 | 9 | `GMS_MSG_REQUEST_FLASH_DATA_DEBUG` | Flash data debug |
| 0x0A | 10 | `GMS_MSG_REQUEST_SCREENSHOT_DEBUG` | Screenshot debug |

### Action Commands (Client → STB)

| Code (Hex) | Code (Dec) | Name | Description |
|------------|------------|------|-------------|
| **Channel Operations** |
| 0x3E8 | 1000 | `GMS_MSG_DO_CHANNEL_SWITCH` | Switch to channel |
| 0x3E9 | 1001 | `GMS_MSG_DO_CHANNEL_RENAME` | Rename channel |
| 0x3EA | 1002 | `GMS_MSG_DO_CHANNEL_DELETE` | Delete channel |
| 0x3EB | 1003 | `GMS_MSG_DO_CHANNEL_LOCK` | Lock/unlock channel |
| 0x3EC | 1004 | `GMS_MSG_DO_CHANNEL_FAV_MARK` | Mark as favorite |
| 0x3ED | 1005 | `GMS_MSG_DO_CHANNEL_MOVE` | Move channel position |
| 0x3EE | 1006 | `GMS_MSG_DO_CHANNEL_LIST_SORT` | Sort channel list |
| 0x3EF | 1007 | `GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED` | Change list type |
| 0x3F0 | 1008 | `GMS_MSG_DO_PLAYING_CHANNEL_PASSWORD_CHECK` | Check channel password |
| 0x3F1 | 1009 | `GMS_MSG_DO_SAT2IP_CHANNEL_PLAY` | Play via SAT>IP |
| 0x3F2 | 1010 | `GMS_MSG_DO_CHANNEL_LIST_UPDATE` | Update channel list |
| 0x3F3 | 1011 | `GMS_MSG_DO_FAV_CHANNEL_DELETE` | Delete from favorites |
| 0x3F4 | 1012 | `GMS_MSG_DO_SAT2IP_PLAY_STOP` | Stop SAT>IP playback |
| **Timer Operations** |
| 0x3FC | 1020 | `GMS_MSG_DO_EVENT_TIMER_DELETE` | Delete timer |
| 0x3FD | 1021 | `GMS_MSG_DO_EVENT_TIMER_ADD` | Add timer |
| 0x3FE | 1022 | `GMS_MSG_DO_EVENT_TIMER_EDIT` | Edit timer |
| 0x3FF | 1023 | `GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE` | Save repeat timer |
| **Factory Reset** |
| 0x406 | 1030 | `GMS_MSG_DO_FACTORY_DEFAULT_ALL` | Full factory reset |
| 0x407 | 1031 | `GMS_MSG_DO_FACTORY_DEFAULT_CHANNEL` | Reset channels only |
| 0x408 | 1032 | `GMS_MSG_DO_FACTORY_DEFAULT_RADIO` | Reset radio channels |
| 0x409 | 1033 | `GMS_MSG_DO_FACTORY_DEFAULT_SCRAMBLE` | Reset scrambled channels |
| 0x40A | 1034 | `GMS_MSG_DO_FACTORY_DEFAULT_TP` | Reset transponders |
| 0x40B | 1035 | `GMS_MSG_DO_FACTORY_DFAULT_SAT` | Reset satellites |
| **Remote Control** |
| 0x410 | 1040 | `GMS_MSG_DO_REMOTE_CONTROL` | Send remote key |
| 0x411 | 1041 | `GMS_MSG_DO_POWER_SWITCH` | Power on/off |
| 0x412 | 1042 | `GMS_MSG_DO_TV_RADIO_SWITCH` | Toggle TV/Radio |
| 0x413 | 1043 | `GMS_MSG_DO_STB_RESTART` | Restart STB |
| **Settings** |
| 0x41A | 1050 | `GMS_MSG_DO_SLEEP_TIMER_SET` | Set sleep timer |
| 0x41B | 1051 | `GMS_MSG_DO_PASSWORD_SWITCH_SET` | Set password switch |
| 0x41C | 1052 | `GMS_MSG_DO_NEW_PASSWORD_SET` | Set new password |
| 0x41D | 1053 | `GMS_MSG_DO_SCREEN_LOCK` | Lock screen |
| 0x41E | 1054 | `GMS_MSG_DO_RS232_DEBUG_ENABLE` | Enable RS232 debug |
| 0x41F | 1055 | `GMS_MSG_DO_FAV_GROUP_RENAME` | Rename favorite group |
| 0x420 | 1056 | `GMS_MSG_DO_PASSWORD_CHECK` | Verify password |
| 0x421 | 1057 | `GMS_MSG_DO_INPUT_PASSWORD_CANCEL` | Cancel password input |
| 0x422 | 1058 | `GMS_MSG_DO_INPUT_METHOD_DISMISS` | Dismiss input method |
| 0x423 | 1059 | `GMS_MSG_DO_INPUT_METHOD_KEY_CODE_SET` | Set input key code |
| 0x424 | 1060 | `GMS_MSG_DO_SAT_SELECTED_CHANGE` | Change selected satellite |
| 0x425 | 1061 | `GMS_MSG_DO_INCOMMING_CALL_NUM_DISPLAY` | Display incoming call |
| 0x426 | 1062 | `GMS_MSG_DO_SMS_NUM_DISPLAY` | Display SMS notification |

### Notification Commands (STB → Client)

| Code (Hex) | Code (Dec) | Name | Description |
|------------|------------|------|-------------|
| 0x7D0 | 2000 | `GMS_MSG_NOTIFY_INPUT_PASSWORD_CANCEL` | Password input cancelled |
| 0x7D1 | 2001 | `GMS_MSG_NOTIFY_PLAYING_CHANNEL_CHANGED` | Channel changed |
| 0x7D2 | 2002 | `GMS_MSG_NOTIFY_CHANNEL_LIST_CHANGED` | Channel list updated |
| 0x7D3 | 2003 | `GMS_MSG_NOTIFY_MUTE_STATE_CHANGED` | Mute state changed |
| 0x7D4 | 2004 | `GMS_MSG_NOTIFY_TV_RADIO_SWITCH` | TV/Radio mode changed |
| 0x7D5 | 2005 | `GMS_MSG_NOTIFY_EVENT_TIMER_CHANGED` | Timer changed |
| 0x7D6 | 2006 | `GMS_MSG_NOTIFY_PSW_DIALOG_SHOW` | Password dialog shown |
| 0x7D7 | 2007 | `GMS_MSG_NOTIFY_PSW_DIALOG_DISMISS` | Password dialog dismissed |
| 0x7D8 | 2008 | `GMS_MSG_NOTIFY_CONTROL_SETTING_CHANGED` | Parental settings changed |
| 0x7D9 | 2009 | `GMS_MSG_NOTIFY_SAT_NO_SELECT_CHANGED` | Selected satellite changed |
| 0x7DA | 2010 | `GMS_MSG_NOTIFY_NEW_RS232_DATA_PRINTED` | New RS232 debug data |
| 0x7DB | 2011 | `GMS_MSG_NOTIFY_INPUT_METHOD_POPUP` | Input method shown |
| 0x7DC | 2012 | `GMS_MSG_NOTIFY_INPUT_METHOD_DISMISS` | Input method dismissed |
| 0x7DD | 2013 | `GMS_MSG_NOTIFY_FAV_GROUP_NAME_CHANGED` | Favorite group renamed |
| 0x7DE | 2014 | `GMS_MSG_NOTIFY_POWER_SWITCH_CHANGED` | Power state changed |
| 0x7DF | 2015 | `GMS_MSG_NOTIFY_CLIENT_TYPE_BECOME_MASTER` | Client became master |
| 0x7E0 | 2016 | `GMS_MSG_NOTIFY_FACTORY_DEFAULT_ALL` | Factory reset completed |
| 0x7E1 | 2017 | `GMS_MSG_NOTIFY_FACTORY_DEFAULT_CHANNEL` | Channel reset completed |
| 0x7E2 | 2018 | `GMS_MSG_NOTIFY_FACTORY_DEFAULT_RADIO` | Radio reset completed |
| 0x7E3 | 2019 | `GMS_MSG_NOTIFY_SAT_LIST_CHANGED` | Satellite list changed |
| 0x7E4 | 2020 | `GMS_MSG_NOTIFY_NEW_PVR_ADD` | New PVR recording added |
| 0x7E5 | 2021 | `GMS_MSG_NOTIFY_PVR_RENAME` | PVR recording renamed |
| 0x7E6 | 2022 | `GMS_MSG_NOTIFY_PVR_DELETE` | PVR recording deleted |
| 0x7E7 | 2023 | `GMS_MSG_NOTIFY_USB_DISK_REMOVE` | USB disk removed |
| 0x7E8 | 2024 | `GMS_MSG_NOTIFY_STB_IP_CHANGED` | STB IP address changed |
| 0x7E9 | 2025 | `GMS_MSG_NOTIFY_SDS_ENABLED` | SDS enabled |
| 0x7EA | 2026 | `GMS_MSG_NOTIFY_SDS_DISABLED` | SDS disabled |
| 0x7EB | 2027 | `GMS_MSG_NOTIFY_STB_SCREEN_UNLOCKED` | Screen unlocked |

### GChat Commands

| Code (Hex) | Code (Dec) | Name | Description |
|------------|------------|------|-------------|
| **Requests** |
| 0x64 | 100 | `GMS_MSG_GCHAT_REQUEST_ROOM_INFO` | Get chat room info |
| 0x65 | 101 | `GMS_MSG_GCHAT_REQUEST_NEW_MSG` | Get new messages |
| 0x66 | 102 | `GMS_MSG_GCHAT_REQUEST_BLACK_LIST` | Get blocked users |
| 0x67 | 103 | `GMS_MSG_GCHAT_REQUEST_SETTINGS` | Get chat settings |
| 0x68 | 104 | `GMS_MSG_GCHAT_REQUEST_PROGRAM_INFO` | Get current program info |
| 0x69 | 105 | `GMS_MSG_GCHAT_REQUEST_USERNAME` | Get username |
| **Actions** |
| 0x44C | 1100 | `GMS_MSG_GCHAT_DO_START` | Start chat session |
| 0x44D | 1101 | `GMS_MSG_GCHAT_DO_EXIT` | Exit chat |
| 0x44E | 1102 | `GMS_MSG_GCHAT_DO_SEND_MSG` | Send message |
| 0x44F | 1103 | `GMS_MSG_GCHAT_DO_BLOCK_UNBLOCK_USER` | Block/unblock user |
| 0x450 | 1104 | `GMS_MSG_GCHAT_DO_CHANGE_SETTING` | Change chat settings |
| 0x451 | 1105 | `GMS_MSG_GCHAT_DO_USER_RENAME` | Rename user |
| **Notifications** |
| 0x834 | 2100 | `GMS_MSG_GCHAT_NOTIFY_LOGOUT` | User logged out |
| 0x835 | 2101 | `GMS_MSG_GCHAT_NOTIFY_ROOM_INFO_CHANGED` | Room info changed |
| 0x836 | 2102 | `GMS_MSG_GCHAT_NOTIFY_NEW_MSG` | New message received |
| 0x837 | 2103 | `GMS_MSG_GCHAT_NOTIFY_USER_NAME_CHANGED` | Username changed |
| 0x838 | 2104 | `GMS_MSG_GCHAT_NOTIFY_UI_SETTING_CHANGED` | UI settings changed |

### Spectrum Analyzer Commands

| Code (Hex) | Code (Dec) | Name | Description |
|------------|------------|------|-------------|
| 0x12D | 301 | `GMS_MSG_SPE_REQUEST_SPECTRUM_INFO` | Get spectrum data |
| 0x12E | 302 | `GMS_MSG_SPE_REQUEST_SPECTRUM_SETTING` | Get spectrum settings |
| 0x515 | 1301 | `GMS_MSG_SPE_DO_SET_SPAN_AND_CENT_FRE` | Set span & center freq |
| 0x516 | 1302 | `GMS_MSG_SPE_DO_SET_STATE_VH` | Set V/H polarization |
| 0x517 | 1303 | `GMS_MSG_SPE_DO_SET_STATE_22K` | Set 22kHz tone |
| 0x518 | 1304 | `GMS_MSG_SPE_DO_SET_STATE_DISEQC` | Set DiSEqC switch |
| 0x519 | 1305 | `GMS_MSG_SPE_DO_SET_REF` | Set reference level |

---

## Remote Control Keys

### Default Key Mapping

| Key Name | Code (Hex) | Code (Dec) |
|----------|------------|------------|
| `NO_KEY_EVENT` | 0x00 | 0 |
| `UP_ARROW_KEY` | 0x01 | 1 |
| `DOWN_ARROW_KEY` | 0x02 | 2 |
| `LEFT_ARROW_KEY` | 0x03 | 3 |
| `RIGHT_ARROW_KEY` | 0x04 | 4 |
| `SELECT_KEY` (OK) | 0x05 | 5 |
| `MENU_KEY` | 0x06 | 6 |
| `EXIT_KEY` | 0x07 | 7 |
| `RED_KEY` | 0x08 | 8 |
| `GREEN_KEY` | 0x09 | 9 |
| `YELLOW_KEY` | 0x0A | 10 |
| `BLUE_KEY` | 0x0B | 11 |
| `KEY_DIGIT0` | 0x0C | 12 |
| `KEY_DIGIT1` | 0x0D | 13 |
| `KEY_DIGIT2` | 0x0E | 14 |
| `KEY_DIGIT3` | 0x0F | 15 |
| `KEY_DIGIT4` | 0x10 | 16 |
| `KEY_DIGIT5` | 0x11 | 17 |
| `KEY_DIGIT6` | 0x12 | 18 |
| `KEY_DIGIT7` | 0x13 | 19 |
| `KEY_DIGIT8` | 0x14 | 20 |
| `KEY_DIGIT9` | 0x15 | 21 |
| `TV_RADIO_KEY` | 0x16 | 22 |
| `MUTE_KEY` | 0x17 | 23 |
| `DISPLAY_KEY` | 0x18 | 24 |
| `MODE_KEY` | 0x19 | 25 |
| `TIME_KEY` | 0x1A | 26 |
| `PIP_KEY` | 0x1B | 27 |
| `MULTI_PIC_KEY` | 0x1C | 28 |
| `RECALL_KEY` | 0x1D | 29 |
| `SAT_KEY` | 0x1E | 30 |
| `SUB_KEY` | 0x1F | 31 |
| `EPG_KEY` | 0x20 | 32 |
| `FAV_KEY` | 0x21 | 33 |
| `TXT_KEY` | 0x22 | 34 |
| `VOL_UP_KEY` | 0x23 | 35 |
| `VOL_DOWN_KEY` | 0x24 | 36 |
| `PGUP_KEY` | 0x25 | 37 |
| `PGDN_KEY` | 0x26 | 38 |
| `FIND_KEY` | 0x27 | 39 |
| `MOSAIC_KEY` | 0x28 | 40 |
| `SLEEP_KEY` | 0x29 | 41 |
| `POWER_KEY` | 0x2A | 42 |
| `USB_KEY` | 0x2B | 43 |
| `F1_KEY` | 0x2C | 44 |
| `F2_KEY` | 0x2D | 45 |
| `F3_KEY` | 0x2E | 46 |
| `F4_KEY` | 0x2F | 47 |
| `F5_KEY` | 0x30 | 48 |
| `F6_KEY` | 0x31 | 49 |
| `F7_KEY` | 0x32 | 50 |
| `F8_KEY` | 0x33 | 51 |
| `FAV_PREV_KEY` | 0x34 | 52 |
| `FAV_NEXT_KEY` | 0x35 | 53 |
| `AUDIO_KEY` | 0x36 | 54 |
| `PAUSE_KEY` | 0x37 | 55 |
| `ZOOM_KEY` | 0x38 | 56 |
| `INFO_KEY` | 0x39 | 57 |
| `PVR_REC_KEY` | 0x3A | 58 |
| `PVR_BACKWARD_KEY` | 0x3B | 59 |
| `PVR_FORWARD_KEY` | 0x3C | 60 |
| `PVR_PLAY_KEY` | 0x3D | 61 |
| `PVR_STOP_KEY` | 0x3E | 62 |
| `PVR_PAUSE_KEY` | 0x3F | 63 |
| `PVR_PREVIOUS_KEY` | 0x40 | 64 |
| `PVR_NEXT_KEY` | 0x41 | 65 |
| `PVR_LIST_KEY` | 0x42 | 66 |
| `TV_SOURCE_KEY` | 0x43 | 67 |
| `TV_MENU_KEY` | 0x44 | 68 |
| `CH_UP_KEY` | 0x45 | 69 |
| `CH_DOWN_KEY` | 0x46 | 70 |
| `OPT_KEY` | 0x47 | 71 |
| `HDMI_KEY` | 0x48 | 72 |
| `FORMAT_KEY` | 0x49 | 73 |
| `YOUTUBE_KEY` | 0x4A | 74 |
| `WEATHER_KEY` | 0x4B | 75 |
| `NETAPP_KEY` | 0x4C | 76 |
| `HD_KEY` | 0x4D | 77 |
| `MOTOR_KEY` | 0x4E | 78 |
| `FUNC_KEY` | 0x4F | 79 |
| `HELP_KEY` | 0x50 | 80 |
| `BACK_KEY` | 0x51 | 81 |
| `MAX_KEY` | 0x52 | 82 |

### Trident 8471 Platform Key Mapping

When `platform_id == 0x1E` (30), use this alternate mapping:

| Key Name | Code (Hex) | Code (Dec) |
|----------|------------|------------|
| `POWER` | 0x2C | 44 |
| `MENU` | 0x14 | 20 |
| `BACK` | 0x30 | 48 |
| `OK` | 0x1B | 27 |
| `UP` | 0x18 | 24 |
| `DOWN` | 0x19 | 25 |
| `LEFT` | 0x16 | 22 |
| `RIGHT` | 0x17 | 23 |
| `VOL_UP` | 0x38 | 56 |
| `VOL_DOWN` | 0x39 | 57 |
| `MUTE` | 0x0C | 12 |
| `CH_UP` | 0x5A | 90 |
| `CH_DOWN` | 0x59 | 89 |
| `EPG` | 0x1D | 29 |
| `EXIT` | 0x15 | 21 |

---

## Data Models

### Channel Model

| Field | Type | Description |
|-------|------|-------------|
| `ProgramId` | string | Unique program identifier |
| `ProgramName` | string | Channel name |
| `ProgramIndex` | int | Channel index/number |
| `SatName` | string | Satellite name |
| `FavMark` | int | Favorite group bitmask |
| `LockMark` | int | Lock status (0/1) |
| `channelType` | int | 0=TV, 1=Radio |
| `isProgramHd` | int | HD flag (0/1) |
| `isProgramScramble` | int | Encrypted (0/1) |
| `haveEPG` | int | Has EPG data (0/1) |
| `isPlaying` | int | Currently playing (0/1) |
| `videoPid` | int | Video PID |
| `audioPid` | string | Audio PIDs (comma-separated) |
| `pmtPid` | int | PMT PID |
| `ttxPid` | int | Teletext PID |
| `subPid` | string | Subtitle PIDs |
| `freq` | int | Frequency (kHz) |
| `symRate` | int | Symbol rate |
| `pol` | char | Polarization (H/V/L/R) |
| `fec` | int | FEC rate |
| `modulationType` | int | Modulation type |
| `modulationSystem` | int | DVB-S/S2 |
| `pilotTones` | int | Pilot tones on/off |
| `rollOff` | int | Roll-off factor |
| `isTuner2` | short | Tuner 2 flag |

### Timer Model

| Field | Type | Description |
|-------|------|-------------|
| `index` | int | Timer index |
| `channelIndex` | int | Channel for timer |
| `timerType` | int | 0=none, 1=view, 2=record |
| `startTime` | datetime | Start time |
| `endTime` | datetime | End time |
| `repeatMode` | int | Repeat mode flags |

### EPG Event Model

| Field | Type | Description |
|-------|------|-------------|
| `eventId` | int | Event identifier |
| `eventName` | string | Program name |
| `startTime` | datetime | Start time |
| `duration` | int | Duration (seconds) |
| `description` | string | Program description |
| `timerType` | int | Associated timer type |

---

## Error Codes

| Code | Name | Description |
|------|------|-------------|
| 0x00 | `GMS_RESPONSE_NO_ERROR` | Success |
| 0x01 | `GMS_RESPONSE_FAIL` | General failure |
| 0x02 | `GMS_RESPONSE_FORMAT_ERROR` | Invalid format |
| 0x03 | `GMS_RESPONSE_NO_ENOUGH_MEMORY` | Out of memory |
| 0x04 | `GMS_RESPONSE_POINTER_INVALID` | Invalid pointer |
| 0x05 | `GMS_RESPONSE_TIMEOUT` | Operation timeout |
| 0x06 | `GMS_RESPONSE_BEYOND_SUPPORT_CLIENT_NUM` | Too many clients |
| 0x07 | `GMS_RESPONSE_CREATE_MESSAGE_FAILED` | Message creation failed |
| 0x08 | `GMS_RESPONSE_CREATE_TASK_FAILED` | Task creation failed |
| 0x09 | `GMS_RESPONSE_PASSWORD_ERROR` | Invalid password |
| 0x0A | `GMS_RESPONSE_CREATE_DATA_FILE_FAILED` | File creation failed |
| 0x0B | `GMS_RESPONSE_BAD_PARAMETER` | Invalid parameter |
| 0x0C | `GMS_RESPONSE_DEBUG_ERROR` | Debug error |
| 0x0D | `GMS_RESPONSE_NET_ERROR` | Network error |
| 0x0E | `GMS_RESPONSE_COMPRESS_FAILED` | Compression failed |
| 0x0F | `GMS_RESPONSE_EDIT_EVENT_TIMER_REPEAT` | Timer conflict |
| 0x10 | `GMS_RESPONSE_CHANNEL_RECORDED` | Channel being recorded |
| 0x11 | `GMS_RESPONSE_STB_IS_MENU_STATE` | STB in menu state |
| 0x12 | `GMS_RESPONSE_NOT_SUPPORT_USB` | USB not supported |
| 0x13 | `GMS_RESPONSE_USB_NOT_CONNECT` | USB not connected |
| 0x14 | `GMS_RESPONSE_USB_NOT_READY` | USB not ready |

### Connection Error Codes

| Code | Name | Description |
|------|------|-------------|
| -1 | `CONNECT_STB_ERROR_UNKONWN_ERROR` | Unknown error |
| -2 | `CONNECT_STB_ERROR_NOT_RESPONSE` | No response |
| -3 | `CONNECT_STB_ERROR_NOT_REACHABLE` | Not reachable |
| -4 | `CONNECT_STB_ERROR_NOT_VALID` | Invalid response |
| -5 | `CONNECT_STB_ERROR_IP_ERROR` | IP error |
| -6 | `CONNECT_STB_ERROR_SW_VERSION_ERROR` | SW version mismatch |
| -7 | `CONNECT_STB_ERROR_STB_IS_FULL` | Connection slots full |
| -8 | `CONNECT_STB_ERROR_HAND_SHARK_ERROR` | Handshake error |
| -9 | `CONNECT_STB_ERROR_SERVER_IP_NON_EXIST` | Server IP not found |
| -10 | `CONNECT_STB_ERROR_DATA_TRANSMISSION_FAIL` | Data transmission failed |

---

## Keep-Alive Mechanism

To maintain the connection, send a keep-alive request every **25 seconds**:

```python
KEEP_ALIVE_INTERVAL_S = 25
KEEP_ALIVE_CMD = 0x1A  # 26 decimal

# XML keep-alive message
payload = serialize_xml_command(0x1A, None).encode("utf-8")
sock.sendall(build_socket_frame(payload))
```

**Example Keep-Alive Message:**
```xml
<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
<Command request="26"></Command>
```

---

## Advanced Features

### SAT>IP Streaming

The STB supports SAT>IP protocol for streaming satellite channels over IP:

1. Check `sat2ip_enable` flag in login response
2. Use `GMS_MSG_DO_SAT2IP_CHANNEL_PLAY` (0x3F1) to start streaming
3. Use `GMS_MSG_DO_SAT2IP_PLAY_STOP` (0x3F4) to stop streaming

### PVR (Personal Video Recorder)

- Request PVR list: `GMS_MSG_REQUEST_PVR_LIST` (0x1B)
- PVR files stored as: `Channel_Data.sdx`, `User_Data.sdx`
- Screenshots: `ScreenShots.bmp`

### Channel List Types

| Type | Value | Description |
|------|-------|-------------|
| `CHANNEL_LIST_TYPE_ALL` | 0 | All channels |
| `CHANNEL_LIST_TYPE_FTA` | 1 | Free-to-air only |
| `CHANNEL_LIST_TYPE_SCRAMBLE` | 2 | Encrypted only |
| `CHANNEL_LIST_TYPE_HD` | 3 | HD only |

### Favorite Groups

| Group | Value | Description |
|-------|-------|-------------|
| `FAVOR_LIST_TYPE_NEWS` | 4 | News |
| `FAVOR_LIST_TYPE_MOVIES` | 5 | Movies |
| `FAVOR_LIST_TYPE_MUSIC` | 6 | Music |
| `FAVOR_LIST_TYPE_SPPORTS` | 7 | Sports |
| `FAVOR_LIST_TYPE_EDUCATION` | 8 | Education |
| `FAVOR_LIST_TYPE_WEATHER` | 9 | Weather |
| `FAVOR_LIST_TYPE_CHILDREN` | 10 | Children |
| `FAVOR_LIST_TYPE_CULTURE` | 11 | Culture |

---

## Usage Examples

### Sending a Remote Key (XML)

```python
# Send OK key (0x05)
xml = '''<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
<Command request="1040">
    <KeyValue>5</KeyValue>
</Command>'''
frame = b"Start" + f"{len(xml):07d}".encode() + b"End" + xml.encode("utf-8")
sock.sendall(frame)
```

### Sending a Remote Key (JSON)

```python
import json

data = {"request": "1040", "array": [{"KeyValue": "5"}]}
payload = json.dumps(data, separators=(",", ":")).encode("utf-8")
frame = b"Start" + f"{len(payload):07d}".encode() + b"End" + payload
sock.sendall(frame)
```

### Requesting Channel List

```python
# XML format
xml = '''<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
<Command request="0"></Command>'''
frame = build_socket_frame(xml.encode("utf-8"))
sock.sendall(frame)
```

---

## References

- Decompiled GMScreen APK (mktvsmart.screen)
- Protocol analysis from `GsConnectToSTB.smali`
- Data models from `dataconvert/model/*.smali`
- Constants from `GlobalConstantValue.smali`
- Key codes from `RcuKeyValue.smali`

---

*Document generated from reverse engineering of GMScreen Android application.*
*Last updated: 2026-01-31*
