# Contributing to MediaStar 4030 Remote Control

Thank you for your interest in contributing! This guide will help you get started.

## 🚀 Quick Start

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 🛠️ Development Environment

### Prerequisites
- Windows 10/11
- MSYS2 with UCRT64 toolchain
- CMake 3.15+
- Git

### Setup
```bash
# Install dependencies
pacman -S mingw-w64-ucrt-x86_64-gcc mingw-w64-ucrt-x86_64-cmake mingw-w64-ucrt-x86_64-ninja mingw-w64-ucrt-x86_64-git

# Clone your fork
git clone https://github.com/YOUR_USERNAME/mediastar_remote.git
cd mediastar_remote/gm_c

# Create build directory
mkdir build_ucrt64_ninja2
cd build_ucrt64_ninja2

# Configure and build
cmake -G Ninja -DCMAKE_CXX_COMPILER=/ucrt64/bin/g++.exe -DCMAKE_C_COMPILER=/ucrt64/bin/gcc.exe ..
ninja

# Run
./gmscreen_imgui_win32.exe
```

## 📁 Project Structure

```
gm_c/
├── include/           # Header files
│   ├── ai/           # AI components
│   ├── cccam/        # CCcam protocol
│   ├── stb/          # STB client
│   └── gui/          # UI components
├── src/              # Source files
│   ├── gui/          # ImGui implementation
│   └── main.cpp      # Application entry
├── third_party/      # Vendored libraries
└── docs/            # Documentation
```

## 🧪 Testing

### Unit Tests
Currently no formal unit tests, but please:
- Test all UI interactions
- Verify CCcam connections
- Check AI features with/without Ollama
- Test edge cases (empty lists, network failures)

### Manual Testing Checklist
- [ ] Application starts without errors
- [ ] STB connection works
- [ ] Remote control buttons respond
- [ ] Channel list loads and searches
- [ ] CCcam server starts/stops correctly
- [ ] Upstream servers connect
- [ ] AI features enable/disable
- [ ] Cache hits/misses track correctly
- [ ] Ollama analysis works (if available)
- [ ] Custom lists save/load
- [ ] Function keys work
- [ ] SOCKS5 proxy connections work

## 📝 Code Style

### C++ Guidelines
- Use **C++17** features
- Follow **camelCase** for variables
- Use **PascalCase** for classes
- Use **UPPER_CASE** for constants
- Include guards for all headers
- Use `nullptr` instead of `NULL`

### Example
```cpp
// Header guard
#pragma once

// Includes
#include <string>
#include <vector>

// Class definition
class CwPredictor {
public:
    void learn(uint16_t caid, const uint8_t* ecm, int ecmLen);
    bool predict(uint16_t caid, const uint8_t* ecm, int ecmLen, uint8_t* cwOut);
    
private:
    std::unordered_map<std::string, EcmPattern> patterns_;
    static constexpr int CACHE_TTL_SECONDS = 10;
};
```

## 🤖 AI Features

### Adding New AI Capabilities
1. Update `ai/cw_predictor.h` with new methods
2. Add thread-safe data structures
3. Update UI in `src/gui/imgui_app.cpp`
4. Add statistics to `Stats` struct
5. Update persistence in `save()/load()` methods

### Ollama Integration
- Use structured prompts with clear output formats
- Handle connection failures gracefully
- Run analysis in background thread
- Parse responses with error checking

## 📋 Pull Request Process

### Before Submitting
1. **Test thoroughly** - ensure no regressions
2. **Update documentation** - README, comments, etc.
3. **Check formatting** - consistent code style
4. **Clean commits** - meaningful messages, squash if needed

### PR Template
```markdown
## Description
Brief description of changes

## Type
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation

## Testing
- [ ] Manual testing completed
- [ ] Edge cases tested
- [ ] Performance verified

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No breaking changes (or documented)
```

## 🐛 Bug Reports

When reporting bugs, please include:
- **OS version**: Windows 10/11 build
- **Application version**: From Help → About
- **STB model**: MediaStar 4030 firmware version
- **Network setup**: Direct/VPN/proxy
- **Steps to reproduce**: Detailed sequence
- **Expected vs actual**: What should happen vs what does
- **Logs**: Application log entries
- **Screenshots**: If UI-related

## 💡 Feature Requests

For new features, please:
1. **Check existing issues** - avoid duplicates
2. **Describe use case** - why this is needed
3. **Consider complexity** - implementation effort
4. **Think about UI** - how users will interact
5. **Discuss alternatives** - other approaches

## 🔧 Debugging

### Enabling Debug Output
Add to `main.cpp`:
```cpp
#ifdef _DEBUG
    AllocConsole();
    freopen_s((FILE**)stdout, "CONOUT$", "w", stdout);
#endif
```

### Common Issues
- **Connection failures**: Check firewall, network path
- **CCcam issues**: Verify upstream server details
- **AI not working**: Check Ollama service status
- **Memory leaks**: Use Visual Studio debugger

## 📚 Resources

### CCcam Protocol
- [CCcam Protocol Documentation](http://www.streamboard.tv/wiki/CCcam)
- [OSCam Implementation](https://github.com/oscam-dev/oscam)

### ImGui
- [Dear ImGui GitHub](https://github.com/ocornut/imgui)
- [ImGui Manual](https://pthom.github.io/imgui_manual_online/manual.html)

### Ollama
- [Ollama Documentation](https://ollama.ai/documentation)
- [Ollama API Reference](https://github.com/ollama/ollama/blob/main/docs/api.md)

## 🤝 Community

- **Discussions**: Use GitHub Discussions for questions
- **Issues**: Bug reports and feature requests
- **Code Review**: Help review other PRs
- **Documentation**: Improve docs and comments

## 📄 License

By contributing, you agree that your contributions will be licensed under the same license as the project (educational and personal use only).

---

Thank you for contributing to MediaStar 4030 Remote Control! 🎉
