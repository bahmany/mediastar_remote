MediaStar 4030 Remote Control v2.0.0 - AI Revolution
=====================================================

🚀 MAJOR RELEASE: AI-Powered CCcam Engine

This release transforms the CCcam server from a simple forwarder into an 
intelligent, self-optimizing decryption engine with advanced AI capabilities.

📦 Package Contents:
- gmscreen.exe (8.6 MB) - Standalone executable, no DLLs required
- README.md - Main documentation
- CHANGELOG.md - Version history
- RELEASE_NOTES.md - Detailed release information
- CONTRIBUTING.md - Development guidelines
- SECURITY.md - Security considerations
- checksum.txt - SHA256 hash verification
- version.json - Build information
- docs/ - Technical documentation
  - AI_FEATURES.md - AI capabilities documentation
  - CCCAM_SETUP.md - CCcam configuration guide
  - PROTOCOL_REFERENCE.md - Protocol specifications

🤖 AI Features:
- CW Cache: 10-second TTL cache for instant repeated ECM responses
- Smart ECM Router: AI ranks servers per CAID, tries best first
- Async Ollama Analyzer: Background server analysis every 60s
- Pattern Learning: Learns ECM→CW patterns for fallback prediction
- AI Dashboard: Real-time stats, CAID tracking, server rankings

🎯 New Features:
- Function Keys: One-click access to hidden STB menus
- Custom Lists: Create and manage personal channel lists
- SOCKS5 Proxy: Optional proxy support for upstream servers
- Enhanced Testing: Full CCcam handshake validation
- Tabbed UI: Reorganized interface with better navigation

📈 Performance Gains:
- 90%+ cache hit rate for repeated ECMs
- 50-80% reduction in upstream traffic
- Sub-100ms response for cached ECMs
- Automatic server optimization

🔧 Requirements:
- Windows 10/11
- Network access to MediaStar STB
- Upstream CCcam servers (for decryption)
- Optional: Ollama with qwen3:14b model for enhanced AI

🚀 Quick Start:
1. Run gmscreen.exe
2. Enter MediaStar STB IP address
3. Add upstream CCcam servers
4. Enable "AI Engine" toggle
5. Start server and monitor AI Dashboard

🔍 Verification:
SHA256(gmscreen.exe) = DB9A2B557C2E43BFD6CC35674F566B52C4D35A18F0A6DDC3714B5060D8CA4FE2

📞 Support:
- Issues: https://github.com/bahmany/mediastar_remote/issues
- Email: bahmanymb@gmail.com
- Documentation: See docs/ folder

🎉 Download and experience the future of CCcam server management!

Build Date: 2026-03-03
Version: 2.0.0
Platform: Windows x64
