# MediaStar 4030 4K Remote Control

A comprehensive C++ application for controlling MediaStar 4030 4K set-top boxes via network connection. Features include channel management, remote control, and CCcam server with ECM forwarding.

## Features

- **Channel Management**: Full channel list with satellite names, frequencies, and symbol rates
- **Remote Control**: Complete MediaStar 4030 remote control with all buttons
- **Text Input**: Send text to STB via virtual keyboard
- **CCcam Server**: Built-in CCcam 2.x server with upstream ECM forwarding
- **Stable Connection**: Automatic reconnection and keep-alive
- **Modern UI**: ImGui-based interface with dark theme

## Screenshots

![Main Interface](docs/screenshots/main.png)
![Channel List](docs/screenshots/channels.png)
![CCcam Server](docs/screenshots/cccam.png)

## Download

### Latest Release (v1.0)
- **Windows**: [gmscreen.exe](https://github.com/bahmany/mediastar_remote/releases/download/v1.0/gmscreen.exe) - Standalone executable, no DLLs required

## Quick Start

1. Download `gmscreen.exe`
2. Run the application
3. Enter your MediaStar STB IP address
4. Click "Connect"
5. Use the remote control or channel list to control your STB

## Build from Source

### Prerequisites
- Windows 10/11
- MSYS2 with MINGW64 toolchain
- CMake 3.15+

### Build Steps

```bash
# Install dependencies (MSYS2 MINGW64)
pacman -S mingw-w64-x86_64-gcc mingw-w64-x86_64-cmake mingw-w64-x86_64-make

# Clone and build
git clone https://github.com/bahmany/mediastar_remote.git
cd mediastar_remote/gm_c
mkdir build_mingw64_static
cd build_mingw64_static
cmake -G Ninja -DCMAKE_CXX_COMPILER=/mingw64/bin/g++.exe -DCMAKE_C_COMPILER=/mingw64/bin/gcc.exe -DZLIB_LIBRARY=/mingw64/lib/libz.a ..
ninja

# Executable will be at: gmscreen.exe
```

## CCcam Server Configuration

The built-in CCcam server can work in two modes:

### 1. Standalone Mode (Default)
- Acts as a local CCcam server
- Responds to ECM requests with ECM_NOK2 (no decryption)

### 2. Upstream Forwarding Mode
- Forwards ECM requests to a real CCcam server
- Relays CW (Control Word) responses back to STB
- Enables actual channel decryption

**To enable upstream forwarding:**
1. In CCcam Server window, configure:
   - Host: upstream CCcam server IP/hostname
   - Port: upstream server port (usually 12000)
   - User/Pass: credentials for upstream server
2. Start the server
3. STB will receive decrypted channels if upstream server provides access

## Channel List Features

- **Satellite Names**: Shows which satellite each channel is on
- **Full Frequency**: Complete frequency in MHz
- **Symbol Rate**: Channel symbol rate
- **Search**: Search by channel name, number, frequency, or satellite
- **Sorting**: Sort by any column
- **Favorites**: Mark and filter favorite channels
- **HD/FTA/ENC**: Visual indicators for channel properties

## Remote Control

All MediaStar 4030 remote buttons are implemented:
- Navigation arrows, OK, Menu, Back
- Number pad (0-9)
- Color buttons (Red, Green, Yellow, Blue)
- Channel/Volume controls
- Playback controls (Play, Pause, etc.)
- Function keys (EPG, Info, Subtitle, etc.)

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

## Troubleshooting

### Connection Issues
- Verify STB IP address is correct
- Check network connectivity (ping the STB)
- Ensure no firewall is blocking port 9982

### CCcam Not Working
- Verify upstream server details are correct
- Check if upstream server is accessible
- Look at CCcam log messages for errors

### Channels Not Showing Video
- Enable upstream CCcam forwarding with a real server
- Verify your subscription includes the channel CAID
- Check ECM/OK statistics in CCcam window

## Technical Details

### Protocol Support
- **MediaStar GCDH Protocol**: For remote control and channel data
- **CCcam 2.x Protocol**: For card sharing and ECM forwarding
- **JSON/XML Serialization**: Compatible with MediaStar firmware

### Architecture
- **C++17** with modern practices
- **ImGui** for user interface
- **Win32** for Windows-specific features
- **Static linking** - no external DLL dependencies

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

## Changelog

### v1.0 (2026-03-01)
- Initial release
- Complete MediaStar 4030 remote control
- Channel list with satellite/frequency display
- CCcam server with upstream forwarding
- Stable connection handling
- Standalone Windows executable

---

**by bahmanymb@gmail.com**
