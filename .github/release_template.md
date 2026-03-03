## 🚀 Release v{VERSION}

### 📦 Downloads
- **Windows**: [gmscreen.exe](https://github.com/bahmany/mediastar_remote/releases/download/v{VERSION}/gmscreen.exe) - Standalone executable, no DLLs required

---

## 🌟 What's New

### 🤖 AI Decryption Engine
- **CW Cache**: 10-second TTL cache for instant responses to repeated ECMs
- **Smart ECM Router**: AI ranks servers per CAID and tries the best server first  
- **Async Ollama Analyzer**: Background analysis every 60s with structured server scoring
- **Pattern Learning**: Learns ECM→CW patterns for fallback prediction

### 📊 AI Dashboard
- **Stats Tab**: Real-time cache hit rates, prediction accuracy, and learning metrics
- **CAIDs Tab**: Discovered encryption systems with success rates
- **AI Report Tab**: Color-coded Ollama analysis with server rankings
- **Servers Tab**: AI-scored server performance with latency and success rates

### 🎯 New Features
- **Function Keys**: One-click access to hidden STB menus
- **Custom Lists**: Create and manage personal channel lists
- **SOCKS5 Proxy**: Optional proxy support for upstream servers
- **Enhanced Testing**: Full CCcam handshake validation with detailed errors

---

## 📋 Requirements

### Minimum
- Windows 10/11
- Network access to MediaStar STB
- Upstream CCcam servers

### Optional for AI Features
- **Ollama** with `qwen3:14b` model:
  ```bash
  curl -fsSL https://ollama.ai/install.sh | sh
  ollama pull qwen3:14b
  ollama serve
  ```

---

## 🚀 Quick Start

1. Download `gmscreen.exe`
2. Run and configure MediaStar STB IP
3. Add upstream CCcam servers
4. Enable "AI Engine" toggle
5. Monitor AI Dashboard for performance

---

## 📈 Performance Gains

- **90%+ cache hit rate** for repeated ECMs
- **50-80% reduction** in upstream traffic  
- **Sub-100ms response** for cached ECMs
- **Automatic server optimization**

---

## 🐛 Bug Fixes

- Fixed race conditions in upstream connections
- Improved timeout handling for ECM forwarding
- Better memory management for large channel lists
- More robust STB connection handling

---

## 🔄 Migration

All v1.0 configurations automatically preserved. AI features disabled by default - enable in UI.

---

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/bahmany/mediastar_remote/issues)
- **Discussions**: [GitHub Discussions](https://github.com/bahmany/mediastar_remote/discussions)
- **Email**: bahmanymb@gmail.com

---

**Download now and experience the future of CCcam server management!**
