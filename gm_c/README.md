# GMScreen - MediaStar STB Control (C++ Edition)

A professional C++ Windows application implementing the MediaStar 4030 4K STB remote control protocol. This is a complete rewrite of the Python and Java (APK) implementations with proper software engineering practices.

## Features

### Core Protocol
- **TCP Communication**: Start+Length+End framing (Start0000095End)
- **XOR Scrambling**: 0x5B XOR + byte-swap for 108-byte login response
- **GCDH Protocol**: 16-byte header with little-endian fields (dataLen, cmdType, responseState)
- **Compression**: zlib/deflate decompression for STB responses
- **Keep-Alive**: Automatic heartbeat with 25s interval, 30s timeout
- **Auto-Reconnect**: 10 attempts with exponential backoff

### Device Management
- UDP broadcast discovery on port 25860
- Automatic device enumeration with 5s timeout
- 108-byte login info parsing (GsMobileLoginInfo)

### Remote Control
- Complete RCU key support (numeric, navigation, color buttons)
- Text input with Unicode support
- Keyboard simulation (enter, backspace, tab, space)
- Input method dismissal

### Channel Management
- Batch channel list loading (100 channels per request)
- 1-hour cache support with automatic validation
- Channel switching by index or program ID
- EPG request support

### Favorites System
- 8 default favorite groups
- Add/remove channels to favorites
- Group CRUD operations (create, rename, delete)
- JSON import/export for backup/restore
- STB synchronization via set_channel_fav_mark

### Applications
- **Console CLI**: Full command-line interface
- **Windows GUI**: Win32 native interface with channel list and remote control

## Project Structure

```
gm_c/
├── include/
│   ├── stb/                    # Core STB protocol library
│   │   ├── constants.h         # Protocol constants (GMS_MSG_*, ports, etc.)
│   │   ├── models.h            # Data structures (GsMobileLoginInfo, Channel, etc.)
│   │   ├── crypto.h            # XOR scramble/descramble
│   │   ├── compression.h       # zlib wrapper
│   │   ├── serialization.h     # XML/JSON command serializers
│   │   ├── network.h           # TcpClient, UdpDiscovery, ReceiveThread
│   │   ├── message_processor.h # STB response handling
│   │   ├── favorites_manager.h # Favorite channels/groups management
│   │   ├── stb_client.h        # Main high-level API
│   │   └── rcu_keys.h          # Remote control key codes
│   └── gui/                    # Windows GUI components
│       ├── main_window.h
│       ├── channel_list_view.h
│       ├── remote_control.h
│       └── device_discovery_dialog.h
├── src/
│   ├── stb/                    # Core library implementations
│   │   ├── crypto.cpp          # XOR scramble algorithm
│   │   ├── compression.cpp     # zlib compress/decompress
│   │   ├── models.cpp          # Data model implementations
│   │   ├── serialization.cpp   # XML/JSON serialization
│   │   ├── network.cpp         # Network layer (TCP, UDP, threads)
│   │   ├── message_processor.cpp # Message parsing & state updates
│   │   ├── favorites_manager.cpp # Favorites CRUD operations
│   │   └── stb_client.cpp      # Main client with reconnection
│   ├── gui/                    # GUI implementations
│   │   ├── main.cpp            # WinMain entry point
│   │   ├── main_window.cpp     # Main application window
│   │   ├── channel_list_view.cpp
│   │   ├── remote_control.cpp
│   │   └── device_discovery_dialog.cpp
│   └── console/                # Console application
│       └── main.cpp
└── CMakeLists.txt              # Build configuration
```

## Building

### Prerequisites
- Windows 10/11
- Visual Studio 2019+ OR MSYS2 UCRT64
- CMake 3.20+
- zlib library

### Build with MSYS2 UCRT64 (Recommended)

Install required packages:
```bash
pacman -S mingw-w64-ucrt-x86_64-gcc \
          mingw-w64-ucrt-x86_64-cmake \
          mingw-w64-ucrt-x86_64-ninja \
          mingw-w64-ucrt-x86_64-zlib
```

Build:
```bash
cd /d/projects/gmscreen/gm_c
cmake -S . -B build_ucrt64 -G Ninja \
  -DCMAKE_MAKE_PROGRAM="C:/msys64/ucrt64/bin/ninja.exe" \
  -DCMAKE_C_COMPILER="C:/msys64/ucrt64/bin/gcc.exe" \
  -DCMAKE_CXX_COMPILER="C:/msys64/ucrt64/bin/g++.exe"

cmake --build build_ucrt64 -j
```

Run:
```bash
./build_ucrt64/gmscreen_cli
```

### Build with Visual Studio

```bash
cd D:\projects\gmscreen\gm_c
mkdir build
cd build
cmake .. -A x64
cmake --build . --config Release
```

## Usage

### Console Application
```
GMScreen Console Client
> help
  connect <ip> [port]  - Connect to STB
  discover             - Discover STB devices
  channels             - Load channel list
  ch <index>           - Change to channel
  key <keycode>        - Send remote key
  text <text>          - Send text input
  fav add <index>      - Add channel to favorites
  fav remove <index>   - Remove from favorites
  fav list             - List favorites
  power                - Power switch
  restart              - Restart STB
  quit                 - Exit

> discover
Found 1 device(s)
  192.168.1.100 - MediaStar 4030 (SN: 230101000001)

> connect 192.168.1.100
Connected successfully!

> channels
Loaded 1250 channels

> ch 10
Changed to channel 10

> key 5
Sent key: 5
```

### GUI Application
1. Launch `gmscreen_win32.exe`
2. Enter STB IP or click "Discover"
3. Click "Connect"
4. Use the virtual remote or channel list

### C++ API Example
```cpp
#include "stb/stb_client.h"

// Create client with auto-reconnect
stb::STBClient client;
client.setAutoReconnect(true);

// Set callbacks
client.setConnectionCallback([](stb::ConnectionState state, const std::string& info) {
    std::cout << "Connection: " << info << "\n";
});

// Connect and login
if (client.connect("192.168.1.100", 20000)) {
    // Load channels
    int count = client.requestChannelList();
    
    // Switch channel
    client.changeChannel(10);
    
    // Send remote key
    client.sendRemoteKey(stb::keys::KEY_CH_UP);
    
    // Add to favorites
    client.addChannelToFavorites(10, {1});  // Group 1
}
```

## Architecture

### Layered Design
| Layer | Components | Responsibility |
|-------|------------|----------------|
| **Protocol** | Constants, Crypto, Compression | Raw protocol handling |
| **Network** | TcpClient, UdpDiscovery, ReceiveThread | Socket I/O, threading |
| **Serialization** | XmlSerializer, JsonSerializer | Command formatting |
| **Processing** | MessageProcessor, STBState | Response parsing, state |
| **Business Logic** | STBClient, FavoritesManager | High-level operations |
| **UI** | MainWindow, Controls | User interface |

### Thread Model
- **Main Thread**: UI, user input
- **Receive Thread**: Async socket reading, message dispatch
- **Keep-Alive Thread**: Heartbeat monitoring
- **Reconnection Thread**: Auto-reconnect handling

### Protocol Flow
```
1. TCP connect to STB:20000
2. Send login XML (request=998)
3. Receive 108 bytes, descramble with XOR 0x5B
4. Verify magic "39WwijOog54a"
5. Start receive thread (GCDH header parsing)
6. Send commands framed: Start<7-digit-len>End<payload>
7. Receive responses: GCDH + dataLen + cmd + state + payload
8. Decompress payload if needed, parse XML/JSON
```

## License

MIT License

## Credits

Protocol reverse-engineered from GMScreen Android APK for MediaStar 4030 4K STB.
