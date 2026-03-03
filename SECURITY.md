# Security Policy

## 🛡️ Security Considerations

This application is designed for educational and personal use. While we take security seriously, users should be aware of the following considerations:

## 🔒 Network Security

### MediaStar Protocol (Port 9982)
- The application connects to MediaStar STBs on port 9982
- No encryption is used in the MediaStar protocol
- Ensure your local network is secure
- Consider using VPN for remote access

### CCcam Protocol (Port 8000/Custom)
- CCcam protocol uses RC4-variant encryption
- Credentials are stored in plain text in configuration files
- Use strong, unique passwords for upstream servers
- Consider using SOCKS5 proxy for additional privacy

## 📁 Data Protection

### Configuration Files
- `gmscreen_cccam.json` - Contains server credentials in plain text
- `gmscreen_ai_cw.dat` - Contains learned patterns (non-sensitive)
- `gmscreen_custom_lists.json` - User preferences (non-sensitive)

### Recommendations
- Restrict access to configuration files
- Use environment variables for sensitive credentials in production
- Regularly rotate upstream server passwords

## 🌐 Internet Connectivity

### Ollama Integration (Optional)
- Local LLM service on port 11434
- No data sent to external servers
- ECM/CW patterns are analyzed locally
- Can be disabled if not needed

### Network Recommendations
- Block outbound connections if not using Ollama
- Use firewall rules to restrict port access
- Monitor network traffic for unusual activity

## 🔍 Privacy

### Data Collection
- No telemetry or analytics collection
- No data sent to external servers
- Local logging only
- User data stays on user's machine

### ECM/CW Logging
- Optional CSV logging for training data
- Contains sensitive decryption information
- Disabled by default
- Use only for legitimate testing/educational purposes

## 🚨 Responsible Use

### Legal Compliance
- Only use with services you have rights to access
- Respect terms of service of content providers
- Follow local laws and regulations
- Educational and personal use only

### Ethical Guidelines
- Do not share credentials or configuration files
- Do not use for unauthorized content access
- Do not reverse engineer protected content
- Report security vulnerabilities responsibly

## 🔧 Security Best Practices

### For Users
1. **Strong Passwords**: Use unique, strong passwords for upstream servers
2. **Network Isolation**: Run on dedicated network if possible
3. **Regular Updates**: Keep application and dependencies updated
4. **Access Control**: Limit who can access configuration files
5. **Monitoring**: Monitor logs for unusual activity

### For Developers
1. **Input Validation**: Validate all network inputs
2. **Error Handling**: Don't expose sensitive information in errors
3. **Memory Safety**: Use secure coding practices
4. **Dependencies**: Keep third-party libraries updated
5. **Testing**: Regular security testing and code review

## 🐛 Vulnerability Reporting

### Disclosure Policy
- Private disclosure preferred
- Allow reasonable time for patching
- Coordinate disclosure if needed
- Credit for responsible disclosure

### How to Report
1. **Email**: bahmanymb@gmail.com
2. **Private Issue**: Create a private GitHub issue
3. **Include**: Steps to reproduce, impact assessment, suggested fix

### Response Timeline
- **Acknowledgment**: Within 48 hours
- **Assessment**: Within 1 week
- **Fix**: As soon as practical, based on severity
- **Disclosure**: After fix is available

## 🔐 Encryption Details

### CCcam Protocol
- Uses modified RC4 cipher
- 16-byte keys derived from username/password
- Session keys negotiated during handshake
- Considered weak by modern standards

### Recommendations
- Use VPN tunnels for remote access
- Consider additional encryption layers
- Rotate keys regularly
- Monitor for protocol anomalies

## 📋 Security Checklist

### Before Deployment
- [ ] Review all network configurations
- [ ] Verify firewall rules
- [ ] Check file permissions
- [ ] Test with minimal privileges
- [ ] Review logging settings

### Regular Maintenance
- [ ] Update application regularly
- [ ] Rotate server passwords
- [ ] Review access logs
- [ ] Check for unusual network activity
- [ ] Backup configuration securely

## ⚠️ Disclaimer

This software is provided "as-is" for educational and personal use. The authors are not responsible for:
- Security breaches due to misuse
- Unauthorized content access
- Network security issues
- Legal compliance violations

Users are responsible for:
- Securing their own networks
- Complying with applicable laws
- Protecting sensitive credentials
- Using the software ethically

---

For security concerns or vulnerabilities, please contact: bahmanymb@gmail.com
