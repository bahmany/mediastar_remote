# MediaStar 4030 4K Remote Control

A comprehensive C++ application for controlling MediaStar 4030 4K set-top boxes via network connection. Features include channel management, remote control, and AI-powered CCcam server with intelligent ECM routing and caching.

## 🌟 New in v2.0: AI-Powered CCcam Engine

The CCcam server now includes advanced AI capabilities for optimal performance:

### 🤖 AI Decryption Engine
- **CW Cache**: 10-second TTL cache for instant responses to repeated ECMs (zero upstream latency)
- **Smart ECM Router**: AI ranks servers per CAID and tries the best server first
- **Async Ollama Analyzer**: Background analysis every 60s with structured server scoring
- **Pattern Learning**: Learns ECM→CW patterns for fallback prediction when upstreams fail

### 📊 AI Dashboard
- **Stats Tab**: Real-time cache hit rates, prediction accuracy, and learning metrics
- **CAIDs Tab**: Discovered encryption systems with success rates
- **AI Report Tab**: Color-coded Ollama analysis with server rankings and issues
- **Servers Tab**: AI-scored server performance with latency and success rates

## Features

- **Channel Management**: Full channel list with satellite names, frequencies, and symbol rates
- **Remote Control**: Complete MediaStar 4030 remote control with all buttons
- **Text Input**: Send text to STB via virtual keyboard
- **CCcam Server**: Built-in CCcam 2.x server with AI-powered ECM forwarding
- **AI Optimization**: Intelligent caching, routing, and server analysis
- **Stable Connection**: Automatic reconnection and keep-alive
- **Modern UI**: ImGui-based interface with dark theme

## Screenshots

![Main Interface](docs/screenshots/main.png)
![Channel List](docs/screenshots/channels.png)
![CCcam Server](docs/screenshots/cccam.png)
![AI Dashboard](docs/screenshots/ai.png)

## Download

### Latest Release (v2.0)
- **Windows**: [gmscreen.exe](https://github.com/bahmany/mediastar_remote/releases/download/v2.0/gmscreen.exe) - Standalone executable, no DLLs required

### Requirements for AI Features
- **Optional**: Ollama with `qwen3:14b` model for advanced analysis
  ```bash
  # Install Ollama
  curl -fsSL https://ollama.ai/install.sh | sh
  
  # Pull the model
  ollama pull qwen3:14b
  
  # Start the service
  ollama serve
  ```
- AI features work without Ollama but with enhanced capabilities when available

## Quick Start

1. Download `gmscreen.exe`
2. Run the application
3. Enter your MediaStar STB IP address
4. Click "Connect"
5. Use the remote control or channel list to control your STB
6. For CCcam: Configure upstream servers and enable AI features

## Build from Source

### Prerequisites
- Windows 10/11
- MSYS2 with UCRT64 toolchain
- CMake 3.15+

### Build Steps

```bash
# Install dependencies (MSYS2 UCRT64)
pacman -S mingw-w64-ucrt-x86_64-gcc mingw-w64-ucrt-x86_64-cmake mingw-w64-ucrt-x86_64-ninja

# Clone and build
git clone https://github.com/bahmany/mediastar_remote.git
cd mediastar_remote/gm_c
mkdir build_ucrt64_ninja2
cd build_ucrt64_ninja2
cmake -G Ninja -DCMAKE_CXX_COMPILER=/ucrt64/bin/g++.exe -DCMAKE_C_COMPILER=/ucrt64/bin/gcc.exe ..
ninja

# Executable will be at: gmscreen_imgui_win32.exe
```

## CCcam Server Configuration

The built-in CCcam server supports multiple upstream servers with AI optimization:

### Basic Configuration
1. In CCcam Server window, add upstream servers:
   - **Host**: upstream CCcam server IP/hostname
   - **Port**: upstream server port (usually 12000)
   - **User/Pass**: credentials for upstream server
   - **SOCKS5 Proxy**: Optional proxy support
2. Enable "AI Engine" toggle for intelligent routing
3. Start the server

### AI Features
- **Enable AI**: Toggle AI-powered caching and routing
- **Enable Learning**: Allow AI to learn from ECM/CW patterns
- **Analyze Now**: Trigger immediate server analysis
- **Check Ollama**: Verify Ollama connectivity for enhanced analysis

### Performance Optimization
- **CW Cache**: Automatically caches CWs for 10 seconds (standard crypto period)
- **Smart Routing**: Tries best server for each CAID first, stops after first success
- **Server Scoring**: AI scores servers based on success rate, latency, and CAID coverage

## Channel List Features

- **Satellite Names**: Shows which satellite each channel is on
- **Full Frequency**: Complete frequency in MHz
- **Symbol Rate**: Channel symbol rate
- **Search**: Search by channel name, number, frequency, or satellite
- **Sorting**: Sort by any column
- **Favorites**: Mark and filter favorite channels
- **HD/FTA/ENC**: Visual indicators for channel properties
- **Custom Lists**: Create and manage personal channel lists

## Remote Control

All MediaStar 4030 remote buttons are implemented:
- Navigation arrows, OK, Menu, Back
- Number pad (0-9)
- Color buttons (Red, Green, Yellow, Blue)
- Channel/Volume controls
- Playback controls (Play, Pause, etc.)
- Function keys (EPG, Info, Subtitle, etc.)
- **Function Codes**: One-click access to hidden menus (SysInfo, ECM List, BISS, etc.)

## Text Input

Send text to the STB:
1. Type in the text input field
2. Click "Send" or press Enter
3. Use "Backspace" to delete characters
4. "Dismiss" closes the virtual keyboard

## Network Requirements

- MediaStar STB must be accessible on the network
- Default port: 9982 (MediaStar protocol)
- CCcam server port: 8000 (configurable)
- Optional: SOCKS5 proxy support for upstream connections

## Troubleshooting

### Connection Issues
- Verify STB IP address is correct
- Check network connectivity (ping the STB)
- Ensure no firewall is blocking port 9982

### CCcam Not Working
- Verify upstream server details are correct
- Check if upstream server is accessible
- Look at CCcam log messages for errors
- Try "TCP Ping" and "Full Test" buttons to validate servers

### AI Features Not Working
- Check if Ollama is running: `ollama serve`
- Verify model is installed: `ollama list`
- Click "Check Ollama" button in AI Dashboard
- AI cache and routing work without Ollama, just with reduced intelligence

### Channels Not Showing Video
- Enable upstream CCcam forwarding with real servers
- Verify your subscription includes the channel CAID
- Check ECM/OK statistics in CCcam window
- Monitor AI cache hit rates for performance

## Technical Details

### Protocol Support
- **MediaStar GCDH Protocol**: For remote control and channel data
- **CCcam 2.x Protocol**: For card sharing and ECM forwarding
- **SOCKS5 Proxy**: Optional proxy support for upstream connections
- **JSON/XML Serialization**: Compatible with MediaStar firmware

### AI Architecture
- **FNV-1a Hashing**: Fast ECM body hashing for cache keys
- **Pattern Learning**: ECM prefix → CW mapping with vote counting
- **Ollama Integration**: REST API to local LLM for server analysis
- **Thread-Safe**: Atomic counters and mutex-protected data structures

### Architecture
- **C++17** with modern practices
- **ImGui** for user interface
- **Win32** for Windows-specific features
- **Static linking** - no external DLL dependencies
- **Multi-threading**: Dedicated threads for CCcam server and AI analysis

## Data Files

The application creates several data files:
- `gmscreen_cccam.json`: CCcam server configuration
- `gmscreen_ecm_log.csv`: ECM/CW training data (if enabled)
- `gmscreen_ai_cw.dat`: AI learned patterns and cache
- `gmscreen_custom_lists.json`: User channel lists

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is for educational and personal use only.

## Support

For issues and questions:
- Create an issue on GitHub
- Check existing issues for solutions
- Provide detailed error logs and system information
- Include AI dashboard screenshots for CCcam-related issues

## Changelog

### v2.0 (2026-03-03) - AI Revolution
- 🤖 **AI Decryption Engine**: CW cache, smart routing, and Ollama analysis
- 📊 **AI Dashboard**: Real-time stats, CAID tracking, and server rankings
- 🗃️ **CW Cache**: 10-second TTL cache for instant repeated ECM responses
- 🧭 **Smart Router**: AI-scored server selection per CAID
- 🔍 **Ollama Analyzer**: Background server quality analysis every 60s
- 📈 **Enhanced Stats**: Cache hit rates, prediction accuracy, learning metrics
- 🎯 **Function Keys**: One-click access to hidden STB menus
- 📋 **Custom Lists**: Create and manage personal channel lists
- 🛡️ **SOCKS5 Proxy**: Optional proxy support for upstream servers
- 🔧 **Improved UI**: Tabbed interface, better organization, dark theme

### v1.0 (2026-03-01)
- Initial release
- Complete MediaStar 4030 remote control
- Channel list with satellite/frequency display
- CCcam server with upstream forwarding
- Stable connection handling
- Standalone Windows executable

---

**by bahmanymb@gmail.com**
