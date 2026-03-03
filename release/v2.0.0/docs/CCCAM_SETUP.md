# CCcam Server Setup Guide

## Overview

The MediaStar 4030 Remote Control includes a full CCcam 2.x server with AI-powered optimization. This guide covers configuration, optimization, and troubleshooting.

## 🚀 Quick Setup

### Basic Configuration
1. Open CCcam Server window
2. Add upstream servers:
   - **Host**: Server IP or hostname
   - **Port**: Usually 12000
   - **Username**: Server username
   - **Password**: Server password
3. Enable "AI Engine" for intelligent routing
4. Click "Start Server"

### Minimum Working Setup
```
Host: 192.168.1.100
Port: 12000
Username: myuser
Password: mypass
Enabled: ✓
```

## 🔧 Advanced Configuration

### Multiple Upstream Servers
Configure multiple servers for redundancy and load balancing:

```
Server 1 (Primary):
Host: cccam.provider1.com
Port: 12000
Username: user1
Password: pass1

Server 2 (Backup):
Host: cccam.provider2.com
Port: 14000
Username: user2
Password: pass2

Server 3 (Specialized):
Host: 192.168.1.50
Port: 16000
Username: local
Password: localpass
```

### SOCKS5 Proxy Setup
For servers requiring proxy access:

```
Server Configuration:
Host: cccam.example.com
Port: 12000
Username: myuser
Password: mypass

Proxy Settings:
Proxy Host: proxy.example.com
Proxy Port: 1080
Proxy User: proxyuser
Proxy Pass: proxypass
```

### Port Configuration
- **Default**: 8000 (application listens)
- **Custom**: Any available port 1024-65535
- **Firewall**: Ensure port is open for STB connections

## 🤖 AI Features

### Enable AI Engine
1. Toggle "AI Engine" switch
2. Enable "Learning" for pattern recognition
3. (Optional) Start Ollama for enhanced analysis

### AI Dashboard Tabs

#### Stats Tab
Monitor real-time performance:
- **Cache Hit Rate**: Target >80%
- **Success Rate**: Overall decryption success
- **Predictions**: AI fallback predictions
- **Cache Size**: Live entries in cache

#### CAIDs Tab
View discovered encryption systems:
- **CAID**: Encryption system identifier
- **Name**: System type (NDS, Viaccess, etc.)
- **Success Rate**: Per-system effectiveness
- **Last Seen**: Recent activity

#### AI Report Tab
Live server analysis:
- **RANK lines**: Server quality scores
- **ISSUE lines**: Identified problems
- **BEST lines**: Optimal server per CAID
- **SUMMARY**: Overall assessment

#### Servers Tab
Detailed performance metrics:
- **Requests**: Total ECM attempts
- **Success**: Successful decryptions
- **Rate**: Success percentage
- **Latency**: Average response time

## 📊 Performance Optimization

### Cache Optimization
- **Hit Rate**: Monitor in Stats tab
- **TTL**: Fixed at 10 seconds (CCcam standard)
- **Size**: Auto-managed (500 entry limit)
- **Eviction**: Automatic expired entry removal

### Server Ranking
AI scores servers based on:
- **Success Rate** (55% weight)
- **CAID Coverage** (25% weight)
- **Latency** (15% weight)
- **Failures** (-7 points each)
- **Ollama Score** (0.05% weight)

### Routing Strategy
1. **Cache Check**: Instant response if available
2. **Pattern Prediction**: AI fallback if cache miss
3. **Smart Routing**: Best server first, sequential try
4. **Failover**: Automatic fallback to next best

## 🔍 Testing & Validation

### Connection Testing
Use built-in test tools:

#### TCP Ping
- Tests basic connectivity
- Measures round-trip time
- Quick validation (1-2 seconds)

#### Full Test
- Complete CCcam handshake
- Validates credentials
- Counts available cards
- Detailed error reporting (5-10 seconds)

### Test Results Interpretation
```
OK (42 cards)     ✓ Success with card count
TCP FAIL          ✗ Network connectivity issue
NO SEED           ✗ Not a CCcam server
AUTH FAIL         ✗ Invalid credentials
```

### Performance Testing
1. Start CCcam server
2. Tune to encrypted channel
3. Monitor Stats tab
4. Check cache hit rate after 30 seconds
5. Verify AI Report updates

## 🐛 Troubleshooting

### Common Issues

#### Server Not Connecting
**Symptoms**: "TCP FAIL" in test results
**Solutions**:
- Check server IP/hostname
- Verify port number
- Test network connectivity
- Check firewall settings

#### Authentication Failed
**Symptoms**: "AUTH FAIL" in test results
**Solutions**:
- Verify username/password
- Check for special characters
- Confirm account is active
- Try with different client

#### No Decryption
**Symptoms**: ECM requests but no CW responses
**Solutions**:
- Verify subscription includes channel
- Check CAID coverage
- Monitor server logs
- Try different upstream server

#### AI Not Working
**Symptoms**: No cache hits, no analysis
**Solutions**:
- Enable AI Engine toggle
- Start Ollama service
- Check Ollama connectivity
- Verify learning is enabled

### Debug Information

#### Enable Debug Logging
Add to application shortcut:
```
gmscreen.exe --debug --log-level=trace
```

#### Monitor Network Traffic
Use Wireshark to filter:
```
tcp.port == 8000  # CCcam server
tcp.port == 12000 # Upstream server
tcp.port == 11434 # Ollama (if used)
```

#### Check Configuration Files
- `gmscreen_cccam.json`: Server settings
- `gmscreen_ai_cw.dat`: AI learned data
- `gmscreen_ecm_log.csv`: ECM/CW log (if enabled)

## 📋 Best Practices

### Security
- Use strong, unique passwords
- Rotate credentials regularly
- Limit configuration file access
- Use VPN for remote access

### Performance
- Enable AI Engine for optimal routing
- Monitor cache hit rates
- Use geographically close servers
- Balance load across multiple servers

### Reliability
- Configure backup servers
- Enable automatic reconnection
- Monitor server health
- Keep application updated

### Maintenance
- Regular server testing
- Clear old cache data
- Update Ollama models
- Review AI reports weekly

## 🔧 Configuration Files

### gmscreen_cccam.json
```json
{
  "port": 8000,
  "username": "cccam",
  "password": "cccam",
  "log_ecm": false,
  "servers": [
    {
      "name": "Main Server",
      "host": "cccam.example.com",
      "port": 12000,
      "username": "user",
      "password": "pass",
      "enabled": true,
      "proxy_host": "",
      "proxy_port": 0,
      "proxy_user": "",
      "proxy_pass": ""
    }
  ]
}
```

### gmscreen_ai_cw.dat
Binary format containing:
- Learned ECM patterns
- Server quality metrics
- Cache state
- AI model data

## 📞 Support

### Getting Help
1. Check this guide first
2. Review AI Dashboard for insights
3. Enable debug logging
4. Collect configuration files
5. Contact support with details

### What to Include in Support Requests
- Application version
- Server configuration (sanitized)
- Test results
- AI Dashboard screenshots
- Log files
- Network setup details

---

For technical implementation details, see the source code in `cccam/` and `ai/` directories.
