# Protocol Reference Guide

## Overview

This document details the network protocols implemented in MediaStar 4030 Remote Control, including MediaStar GCDH protocol and CCcam 2.x protocol.

## 📡 MediaStar GCDH Protocol

### Connection Details
- **Port**: 9982 (default)
- **Transport**: TCP
- **Encoding**: JSON/XML
- **Authentication**: None (local network)

### Message Format

#### Request Structure
```json
{
  "jsonrpc": "2.0",
  "method": "method_name",
  "params": {...},
  "id": 1
}
```

#### Response Structure
```json
{
  "jsonrpc": "2.0",
  "result": {...},
  "id": 1
}
```

### Key Methods

#### Remote Control
```json
{
  "method": "remote.send_key",
  "params": {
    "key": "KEY_POWER",
    "repeat": 1
  }
}
```

#### Channel Information
```json
{
  "method": "channel.get_list",
  "params": {
    "satellite_id": 0,
    "start_index": 0,
    "count": 1000
  }
}
```

#### System Information
```json
{
  "method": "system.get_info",
  "params": {}
}
```

### Key Codes

#### Navigation
- `KEY_UP` / `KEY_DOWN` / `KEY_LEFT` / `KEY_RIGHT`
- `KEY_OK` / `KEY_BACK` / `KEY_MENU`
- `KEY_EXIT` / `KEY_EPG`

#### Numbers
- `KEY_0` through `KEY_9`

#### Colors
- `KEY_RED` / `KEY_GREEN` / `KEY_YELLOW` / `KEY_BLUE`

#### Media
- `KEY_PLAY` / `KEY_PAUSE` / `KEY_STOP`
- `KEY_REWIND` / `KEY_FASTFORWARD`
- `KEY_RECORD`

#### Volume
- `KEY_VOLUME_UP` / `KEY_VOLUME_DOWN`
- `KEY_MUTE`

#### Channels
- `KEY_CHANNEL_UP` / `KEY_CHANNEL_DOWN`
- `KEY_PREVIOUS` / `KEY_NEXT`

#### Function Keys
- `KEY_F1` through `KEY_F4`

### Channel Data Structure

#### Channel Object
```json
{
  "service_id": 1234,
  "service_name": "Channel Name",
  "service_type": 1,
  "satellite_id": 1,
  "frequency": 12345,
  "symbol_rate": 27500,
  "polarization": "H",
  "is_hd": true,
  "is_fta": false,
  "is_encrypted": true,
  "caid_list": [0x0604, 0x0500],
  "service_index": 42
}
```

#### Satellite Object
```json
{
  "satellite_id": 1,
  "satellite_name": "Satellite Name",
  "position": 42.0,
  "longitude": "E"
}
```

## 🔐 CCcam 2.x Protocol

### Connection Details
- **Port**: 12000 (typical upstream), 8000 (application)
- **Transport**: TCP
- **Encryption**: Modified RC4
- **Authentication**: Username/Password

### Handshake Process

#### 1. TCP Connect
```
Client → Server: TCP SYN
Server → Client: TCP SYN/ACK
Client → Server: TCP ACK
```

#### 2. Seed Exchange
```
Server → Client: 16-byte random seed
```

#### 3. Key Derivation
```
client_hash = SHA1(seed + username)
server_hash = SHA1(seed + password)
client_key = RC4_key(client_hash)
server_key = RC4_key(server_hash)
```

#### 4. Authentication
```
Client → Server: SHA1(username + password) (encrypted)
Server → Client: 20-byte ACK (encrypted)
```

#### 5. Card Data Exchange
```
Server → Client: NEW_CARD messages (encrypted)
```

### Message Format

#### Header Structure
```
[4 bytes] Header
  - cmd: 1 byte (message type)
  - datalen: 2 bytes (big-endian)
  - reserved: 1 byte

[n bytes] Data (encrypted)
```

### Message Types

#### Client Messages

##### MSG_ECM_REQUEST (0x01)
Request ECM decryption:
```
Header + Data:
- caid: 2 bytes
- provid: 4 bytes
- cardid: 4 bytes
- sid: 2 bytes
- ecm_data: variable
```

##### MSG_KEEPALIVE (0x03)
Keep connection alive:
```
Header (no data)
```

##### MSG_CLI_DATA (0x05)
Client identification:
```
Header + Data:
- username: null-terminated string
- CCcam\0: protocol identifier
```

#### Server Messages

##### MSG_ECM_REQUEST (0x01)
ECM response with CW:
```
Header + Data:
- cw: 16 bytes (Control Word)
```

##### MSG_ECM_NOK1 (0x02)
ECM cannot be decrypted:
```
Header (no data)
```

##### MSG_ECM_NOK2 (0x08)
ECM decryption failed:
```
Header (no data)
```

##### MSG_KEEPALIVE (0x03)
Keepalive response:
```
Header (no data)
```

##### MSG_NEW_CARD (0x06)
New card available:
```
Header + Data:
- card_data: variable
```

##### MSG_CARD_REMOVED (0x07)
Card no longer available:
```
Header + Data:
- card_id: 4 bytes
```

##### MSG_SRV_DATA (0x0E)
Server information:
```
Header + Data:
- server_info: variable
```

### Encryption Details

#### RC4 Variant
- Modified RC4 algorithm
- Key schedule alteration
- State initialization changes

#### Key Schedule
```cpp
void cc_crypt_init(uint8_t* key, int len) {
    // Modified key schedule
    for (int i = 0; i < 256; i++) {
        s[i] = i;
        k[i] = key[i % len];
    }
    
    // Key mixing
    int j = 0;
    for (int i = 0; i < 256; i++) {
        j = (j + s[i] + k[i]) & 0xFF;
        swap(s[i], s[j]);
    }
}
```

#### Encryption/Decryption
```cpp
void cc_crypt_crypt(uint8_t* data, int len, uint8_t* state) {
    int i = 0, j = 0;
    for (int n = 0; n < len; n++) {
        i = (i + 1) & 0xFF;
        j = (j + state[i]) & 0xFF;
        swap(state[i], state[j]);
        data[n] ^= state[(state[i] + state[j]) & 0xFF];
    }
}
```

## 🔄 SOCKS5 Proxy Protocol

### Connection Sequence
1. TCP connect to proxy
2. SOCKS5 authentication
3. Connect to target server
4. Transparent data forwarding

### SOCKS5 Messages

#### Initial Greeting
```
Client → Proxy: [5, 1, 2]  (VER, NMETHODS, METHOD)
Proxy → Client: [5, 0]       (VER, METHOD)
```

#### Connect Request
```
Client → Proxy: [5, 1, 0, 3, len, host, port_hi, port_lo]
Proxy → Client: [5, 0, 0, 1, ip_hi, ip_lo, ip_hi, ip_lo, port_hi, port_lo]
```

#### Username/Password Auth
```
Client → Proxy: [1, len_user, user, len_pass, pass]
Proxy → Client: [1, 0]  (success)
```

## 📊 ECM/CW Data Flow

### ECM Request Process
1. STB sends ECM to application
2. Application checks AI cache
3. If cache miss: forward to upstream
4. Upstream returns CW or NOK
5. Application stores CW in cache
6. CW sent to STB

### Data Structures

#### ECM Message
```cpp
struct EcmMessage {
    uint16_t caid;        // Conditional Access ID
    uint32_t provid;      // Provider ID
    uint32_t cardid;      // Card ID
    uint16_t sid;         // Service ID
    uint8_t ecm_data[256]; // ECM body
    int ecm_len;          // ECM length
};
```

#### CW Response
```cpp
struct CwResponse {
    uint8_t even_cw[8];   // Even Control Word
    uint8_t odd_cw[8];    // Odd Control Word
};
```

## 🔍 Debugging Tools

### Packet Capture
```bash
# MediaStar protocol
tcpdump -i any port 9982

# CCcam protocol
tcpdump -i any port 12000

# SOCKS5 proxy
tcpdump -i any port 1080
```

### Protocol Analysis
- **Wireshark**: CCcam dissector available
- **tcpdump**: Command-line packet capture
- **netcat**: Manual protocol testing

### Test Scripts
```bash
# Test MediaStar connectivity
nc -zv 192.168.1.100 9982

# Test CCcam server
echo -e "\x01\x00\x00\x08test" | nc 192.168.1.100 12000
```

## 📚 References

### MediaStar Protocol
- Manufacturer documentation (limited)
- Reverse-engineered specifications
- Community forums and wikis

### CCcam Protocol
- [CCcam Protocol Wiki](http://www.streamboard.tv/wiki/CCcam)
- [OSCam Implementation](https://github.com/oscam-dev/oscam)
- Academic papers on conditional access

### Cryptography
- RC4 algorithm specifications
- Cryptanalysis of CCcam encryption
- Secure key derivation practices

---

For implementation details, see the source code in `stb/` and `cccam/` directories.
