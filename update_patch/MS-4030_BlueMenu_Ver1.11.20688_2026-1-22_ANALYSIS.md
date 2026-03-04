# Deep Firmware Analysis: MS-4030 BlueMenu v1.11.20688 (2026-01-22)

> **Static analysis only — no patching, no bypass, no execution.**
> Goal: full technical understanding for code improvement in the `gmscreen` project.

## 1) File Identity
| Field | Value |
|---|---|
| Path | `MS-4030_BlueMenu_Ver1.11.20688_2026-1-22.bin` |
| Size | 8,558,561 bytes (~8.16 MB) |
| SHA-256 | `b3f13153c956ab1b467a38b43068e956764bc59b82bad7132f8a279302e00a59` |
| Global entropy | 5.91 bits/byte (mixed: structured + compressed regions) |
| 0xFF bytes | 3,253,227 (38.0%) — typical NOR flash unwritten regions |
| 0x00 bytes | 42,072 only — confirms active data dominates |

---

## 2) Firmware Memory Map (Discovered via Static Analysis)

| Section | Offset Start | Offset End | Size | Identification | Notes |
|---|---|---|---|---|---|
| HDR | `0x00000000` | `0x000003B6` | 950 B | `54 33` = "T3" vendor magic | Proprietary update header |
| BOOT_TABLE | `0x000003FE` | `0x00000762` | 868 B | Structured 4-byte records | Hardware init table |
| ARM_CODE | `0x0000092E` | `0x0000587A` | 20 KB | `06 00 00 EA` = ARM branch opcode | ARM bootloader code |
| SECTION_A | `0x00005C0E` | `0x0002992E` | 143 KB | Compressed binary | Secondary boot payload |
| **MAIN_FS** | `0x0004012E` | `0x004AC37F` | **4.4 MB** | `87 65 43 21 AA 41 AD A3` = vendor-signed container | Root filesystem (compressed + integrity-signed) |
| *(0xFF gap)* | `0x004AC37F` | `0x0055B002` | 699 KB | Erased flash | Unwritten NOR flash |
| BMPS | `0x0055B002` | `0x005BB84C` | 387 KB | `42 4D 50 53` = "BMPS" | Boot splash/bitmap resources |
| *(0xFF gap)* | `0x005BB84C` | `0x0075B002` | 1.6 MB | Erased flash | Reserved NOR flash |
| SEC_3KB | `0x0075B002` | `0x0075BC44` | 3 KB | Structured binary | Small config block |
| SEC_5KB | `0x0077B002` | `0x0077C5F5` | 5.5 KB | `44 3A` entries | Address/config table |
| **STRG** | `0x0079B002` | `0x007AC7D6` | 70 KB | `53 54 52 47` = "STRG" + "ttx" | **String resource table, incl. Teletext** |
| DATA_14KB | `0x0081B002` | `0x0081E8E2` | 14 KB | Transponder DB strings | **Satellite transponder/channel DB** |
| DATA_26KB | `0x0081F1A3` | `0x008257A8` | 25 KB | Binary data | Config/key material |
| **CA_TABLE** | `0x00826A0E` | `0x008272B1` | 2.2 KB | `Tandberg` + `GetTV` entries | **CA provider key table** |
| TAIL | `0x00827FEE` | `0x008297E1` | 6 KB | Mixed binary | Trailing data/signatures |

### 2.1 Header Magic "T3"
The file begins with `54 33 C2 81` = "T3" + two non-ASCII bytes. This is a **MediaStar vendor-specific update container** identifier. The symmetric signature `AA 41 AD A3  A3 AD 41 AA` in the main filesystem section confirms **integrity checking** (not encryption).

### 2.2 Entropy Map
- Blocks 0x0000–0x0028xxx: entropy **5.0–6.0** (ARM code + structured tables)
- Blocks 0x002A–0x003F xxx: entropy **0.000** (pure 0xFF = erased NOR flash)
- Blocks 0x0040xxx onward: entropy **7.7–8.0** (compressed/scrambled filesystem)

No plaintext protocol strings exist in the uncompressed regions — confirming the command protocol lives entirely inside the compressed 4.4MB MAIN_FS.

---

## 3) Key Section Deep-Dives

### 3.1 CA_TABLE — Complete Conditional Access Provider Map (`0x00826A0E`)

Record format (28 bytes each, 78 entries total = 2,184 bytes):
```
[4-byte CA_ID LE] [8-byte key/hash material] [1-byte type flag] [14-byte provider name, null-padded] [0xFF terminator]
```

#### Full CA_ID Table (78 entries)

| CA_ID | Key Material | Provider | Group |
|-------|-------------|----------|-------|
| 0x0001 | `5FDB06AA1364E500` | Tandberg | SECA base (1-5) |
| 0x0002 | `CA138BF89FBE0E00` | Tandberg | |
| 0x0003 | `52614F2CB05D3801` | Tandberg | |
| 0x0004 | `345E9B5B97D5BC01` | Tandberg | |
| 0x0005 | `9EFB52CDD0FBA101` | Tandberg | |
| 0x000B | `8913EDA56DE5BC00` | Tandberg | Aux (0B-0D) |
| 0x000C | `E326D5546DD03E00` | Tandberg | |
| 0x000D | `B7A28B41A38C9500` | Tandberg | |
| 0x0015 | `0517DC6537166F00` | Tandberg | Extended (15-16) |
| 0x0016 | `4A2FD304753D8C00` | Tandberg | (2 entries) |
| 0x0016 | `235751D97F32C201` | Tandberg | |
| **0x0017** | `5B70F00700000000` | **GetTV** | **Middle-East CA** |
| 0x0083 | `4DB639DF3C4A2D00` | Tandberg | Bulsat (83-84) |
| 0x0084 | `EDB30CBA1EFB0500` | Tandberg | |
| 0x00C9 | `D097A72F1E367A00` | Tandberg | Regional (C9-CE) |
| 0x00CA | `3775D9D2D8E04400` | Tandberg | |
| **0x00CE** | `8C235D3E1A97EC01` | **103W-4120-H** | **Satellite-specific** |
| 0x03E8-0x03ED | `8BAD71EFB7A83C00` | Tandberg | **Shared key group** (6 entries, same key!) |
| 0x04FE | `8E75452BFEBF3F00` | Tandberg | Standalone |
| 0x0691-0x06CE | *(varies)* | Tandberg | 06xx range (15 entries) |
| 0x0A26 | `B7D9C24C62827700` | Tandberg | Standalone |
| 0x1600-0x16E5 | *(varies)* | Tandberg | 16xx range (13 entries) |
| 0x1773-0x1778 | *(varies)* | Tandberg | 17xx range (4 entries) |
| 0x1839-0x183D | *(varies)* | Tandberg | 18xx range (5 entries) |
| 0x1959 | `405E18941A49DA00` | Tandberg | Standalone |
| 0x1ED5 | `67A8578D03D5C400` | Tandberg | Standalone |
| 0x2140 | `0660C5AFE5251800` | Tandberg | Standalone |
| 0x2249-0x224A | *(varies)* | Tandberg | 22xx range |
| 0x2705-0x2710 | *(varies)* | Tandberg | 27xx range (4 entries) |
| 0x645A | `8CEB764A5D586B00` | Tandberg | Standalone (highest ID) |

#### Key Findings

1. **Shared key group (0x03E8-0x03ED):** 6 consecutive CA_IDs all share identical key material `8BAD71EFB7A83C00`. This suggests a multi-service package using a single decryption key.

2. **GetTV (0x0017):** Only non-Tandberg provider. Key material has 4 trailing zero bytes, suggesting a shorter effective key. Middle-East satellite TV conditional access.

3. **103W-4120-H (0x00CE):** The only entry with a satellite position in the name field instead of "Tandberg" — likely refers to a specific transponder at 103°W, frequency 4120 MHz, horizontal polarization.

4. **Duplicate CA_ID 0x0016:** Two entries with different key material — possibly representing key rotation or alternative decryption paths.

5. **Type flag:** Always `0x01` except entry at 0x270D which has flag `0x10` — possibly a key version or priority indicator.

**Code integration (completed):** The enriched CAID database has been integrated into `cw_predictor.h::getCaidName()` with all 78 entries mapped to descriptive names.

### 3.1b DATA_108b — CA Package Quick-Lookup (`0x00826222`)

108 bytes containing exactly 2 CA package entries:

| Offset | Key Material | Provider Name |
|--------|-------------|---------------|
| 0x00 | `35F6B9217C2BA83F...` | **Ant-1 Euro** |
| 0x44 | `AE652B210BF89FC6...` | **Bulsat ECM-IV** |

These appear to be pre-configured CA packages for Greek (Ant-1 = ANT1 Europe) and Bulgarian (Bulsat) satellite services.

### 3.1c DATA_26KB — CA Service Package Database (`0x0081F1A3`)

25 KB binary structure. First entry: `WBBC Package` (likely BBC World). Contains ~500 binary records that appear to map service IDs to CA package configurations. The `bin9gosI` pattern repeating at regular intervals suggests a fixed-size record structure with embedded checksums.

### 3.2 DATA_14KB — Embedded Satellite Transponder Database (`0x0081B002`)

This section contains a **binary transponder lookup table** with entries truncated to 14 chars. Sample entries extracted:

```
Fox-News  (12380V28000)   HBO (4oW 10842)   CNN (22oW 11560)
Discovery Suda...         ESPN (40.5oW)      BBC (127oW 3800)
Turner (43oW)             Viacom (101oW)     A&E Networks
NHK World Network         TV Globo (55oW)    GolTV (131oW)
Fox Sports Chile          Caracol (58oW)     Telefe (55oW)
```

Satellite positions found: `4°W, 7°W, 22°W, 30°W, 40.5°W, 43°W, 47.5°W, 55°W, 58°W, 91°W, 99°W, 101°W, 103°W, 121°W, 123°W, 125°W, 127°W, 131°W, 133°W, 135°W`

Frequency/SR examples: `12303V27500`, `12380V28000`, `11595V275`, `3900V29079`, `3780H29270`, `3960V30000`

**Implication:** The firmware embeds a complete transponder scan database. This is used for initial channel scan. Our project can use this data to validate/enrich channel frequency display.

### 3.3 STRG — String Resource Table (`0x0079B002`)

- Magic: `53 54 52 47` = **"STRG"**
- Contains "ttx" = **Teletext** string resources
- Format: count header + offset table + null-terminated string pool
- Likely contains all UI menu strings in multiple languages

**Implication:** The STB supports Teletext. A future `requestTeletextPage()` command may be feasible.

### 3.4 ARM_CODE Section (`0x0000092E`)

- Header: `06 00 00 EA FE FF FF EA` = ARM32 branch + infinite loop
- This is the **secondary bootloader** (SPL/BL2) in ARM Thumb-2
- Contains CPU init, memory controller setup, UART init
- No protocol strings (too early in boot sequence)

### 3.5 BMPS Section (`0x0055B002`)

- Magic: `42 4D 50 53` = **"BMPS"**  
- Size: ~387 KB — contains boot splash screens and OSD bitmap resources
- Likely uses a proprietary bitmap compression for the LCD/HDMI framebuffer

---

## 4) Remote/Command Protocol Map (from project source cross-reference)

### 4.1 Transport Frame
```
"Start" + 7-digit-decimal-length + "End" + payload
```
(ref: `gm_c/include/stb/serialization.h`, `gm_python/app/stb/serializers.py`)

### 4.2 Command Families
| Range | Family | Example |
|---|---|---|
| 0–999 | REQUEST | `GMS_MSG_REQUEST_CHANNEL_LIST = 0` |
| 1000–1999 | DO | `GMS_MSG_DO_CHANNEL_SWITCH = 1000` |
| 2000–2999 | NOTIFY | `GMS_MSG_NOTIFY_CHANNEL_CHANGED` |
| 3000+ | Internal/CMD | state machine responses |

### 4.3 All Key Commands
| Command | ID | Payload | Status |
|---|---:|---|---|
| Channel List | 0 | `FromIndex`, `ToIndex` | Implemented |
| Current Channel | 3 | — | Implemented |
| EPG | 5 | `ProgramId` | Implemented |
| Keep Alive | 26 | — | Implemented |
| Channel Switch | 1000 | `TvState`, `ProgramId` | Implemented + fixed |
| Fav Mark | 1004 | `TvState`, `FavMark`, `FavorGroupID`, `ProgramIds` | Implemented |
| Remote Key | 1040 | `KeyValue` | **Fully wrapped (new)** |
| Power Toggle | 1041 | — | Implemented |
| TV/Radio Switch | 1042 | — | Implemented |
| STB Restart | 1043 | — | Implemented |
| Dismiss Input | 1058 | — | Implemented |
| Keyboard Code | 1059 | `KeyCode` | Implemented |

### 4.4 Platform-aware Key Map
Two RCU profiles in `rcu_keys.h`:
- **Default** — standard MediaStar 4030
- **Platform 30** — different key codes, selected when `login_info->platformId() == 30`

New typed API (implemented in `stb_client.cpp`): `sendNavUp/Down/Left/Right`, `sendOk`, `sendBack`, `sendMenu`, `sendChannelUp/Down`, `sendVolUp/Down`, `sendMute`, `sendNumericKey(0-9)`, `sendNumericSequence`, `sendColorKey(R/G/Y/B)`, `sendInfo`, `sendEpg`, `sendFav`, `sendPlayPause`, `sendStop`, `sendRecord`, `sendRewind`, `sendFastForward`.

---

## 5) Implemented Code Improvements

### 5.1 Channel Switch Fix (Root cause resolved)
**Root cause:** `changeChannel(service_index)` returned `true` on TCP send success even when the STB silently ignored the command — fallbacks never triggered.

**Fix in `imgui_app.cpp`:**
```cpp
// NEW priority order — most reliable first
if (!ch.service_id.empty())
    ok = client.changeChannelDirect(ch.service_id, tvst);        // 1. Direct by ServiceID
if (!ok && ch.program_id != ch.service_id)
    ok = client.changeChannelDirect(ch.program_id, tvst);        // 2. Direct by ProgramId
if (!ok)
    ok = client.changeChannel(ch.service_index);                 // 3. Index fallback
// + async re-send after 700ms (handles STB busy state)
```

### 5.2 Typed Remote API (platform-aware)
All `sendXxx()` methods in `stb_client.cpp` auto-select key code from correct profile based on `platformId()`.

### 5.3 Logger::add Bug Fixed
CCcam log viewer UI was incorrectly placed inside `Logger::add()` (deadlock + wrong-thread ImGui calls). Moved to proper render loop.

### 5.4 Virtual Remote Control Panel
New floating window (`showRemote`) with: navigation cross, CH/VOL/MUTE, color keys, numeric keypad 0-9, INFO/EPG/FAV, playback controls.

---

## 6) Stability Recommendations

### 6.1 Reconnect — Exponential Backoff ✅ DONE
Implemented in `stb_client.cpp::doReconnect()`: `delay = min(BASE * 2^attempt, 30s cap)`.

### 6.2 Keepalive Heartbeat ✅ DONE
Sends `GMS_MSG_REQUEST_SOCKET_KEEP_ALIVE` (ID 26) every 30s when connected and idle > 28s.

### 6.3 Command Audit Log ✅ DONE
Rolling 200-entry `cmd_audit_` deque with `{timestamp_ms, cmd_id, tcp_ok, latency_ms}`. Visible in STB Info > Cmd Audit tab.

### 6.4 Auto-Reconnect Upstream CCcam Servers ✅ DONE
Every 30s during ECM loop, disconnected upstreams are automatically reconnected via `connectUpstream()`.

---

## 7) ECM Analytics Engine (NEW — Session 10)

### 7.1 Per-Channel ECM Tracking (`ChannelStats` in `cw_predictor.h`)
Each unique CAID+SID combination is tracked with:
- **ECM counters:** total, ok, fail, success rate
- **Latency:** running average per channel
- **CW rotation detection:** timestamps of CW changes, average/min/max rotation period
- **XOR-delta analysis:** tracks XOR between consecutive CWs; detects linear key schedules
- **ECM parity tracking:** counts even (0x80) vs odd (0x81) table_id ECMs
- **Per-server success map:** which upstream server works best for each channel

### 7.2 XOR-Delta Analysis (Analytics-only)
The XOR-delta signal is tracked **for analytics only** (stability / repeats / anomaly detection).

> Note: Any CW prediction-style helper (e.g. `predictXorDelta()`) has been removed/disabled in the project to keep the tooling strictly observational and avoid bypass-style behavior.

### 7.3 Enriched CAID Database
`getCaidName()` now contains all 78 CA_IDs from firmware analysis, organized by:
- Exact matches for known Tandberg sub-IDs (SECA, Bulsat, regional, shared-key)
- GetTV/BetaCrypt (0x0017)
- Range-based lookups for Tandberg 06xx, 16xx, 17xx, 18xx, 22xx, 27xx series
- Generic family-based fallback for standard CAS providers

### 7.4 UI: Channel Analytics Dashboard
New "Channels" sub-tab in AI Engine showing 9-column table:
`CAID | SID | ECMs | Rate | Latency | CW Rotation | XOR Stable | Parity | Best Server`

With tooltips showing detailed rotation timing and color-coded success rates.

---

## 7.5 MAIN_PAYLOAD Container Notes (Static)

`MAIN_PAYLOAD_4MB_0004012E.bin` begins with:
- Magic: `0x87654321`
- Markers: `AA 41 AD A3` and `A3 AD 41 AA`

Static properties:
- **Very high entropy (~8.0 bits/byte)** across almost all blocks.
- Apparent `JFFS2` and `zlib` signature hits do **not** validate as real filesystem/deflate streams (CRC/header/deflate sanity checks fail), indicating **false positives inside high-entropy data**.
- Multiple DER-like `0x30 0x82/0x81` sequences are present, but no standard CMS/PKCS#7 OID markers were found; treated as **non-authoritative** without further validation.

Conclusion: MAIN_PAYLOAD is likely a **vendor container with integrity checking and/or encryption/obfuscation**, and cannot be safely unpacked using standard filesystem extractors without vendor-specific tooling/keys.

---

## 8) Source Files Reference
- `gm_c/include/stb/stb_client.h` — public API
- `gm_c/src/stb/stb_client.cpp` — implementation
- `gm_c/include/stb/constants.h` — message IDs
- `gm_c/include/stb/rcu_keys.h` — key codes (default + p30)
- `gm_c/include/stb/serialization.h` — frame builder
- `gm_c/src/gui/imgui_app.cpp` — UI + channel play logic
- `gm_python/app/stb/serializers.py` — Python serializers
- `gm_python/app/stb/constants.py` — Python constants
