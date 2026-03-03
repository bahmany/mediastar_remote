#!/usr/bin/env python3
"""Check MS-4030 firmware for video streaming capabilities"""

import re
from pathlib import Path

firmware_path = Path(__file__).parent / "MS-4030_BlueMenu_Ver1.11.20688_2026-1-22.bin"
data = firmware_path.read_bytes()

print("=" * 70)
print("MediaStar 4030 Firmware - Video Streaming Capability Analysis")
print("=" * 70)

# WiFi/Network Hardware
print("\n### WiFi/Network Hardware Indicators ###")
wifi_keywords = [
    b'wifi', b'wlan', b'802.11', b'wireless', 
    b'ethernet', b'eth0', b'eth1', b'dhcp', b'ifconfig'
]
for kw in wifi_keywords:
    matches = len(list(re.finditer(re.escape(kw), data, re.IGNORECASE)))
    if matches > 0:
        print(f"  {kw.decode('latin1'):15s}: {matches:3d} matches")

# Video Streaming Protocols
print("\n### Video Streaming Protocols ###")
stream_keywords = [
    b'rtsp', b'rtp', b'udp', b'multicast',
    b'mpeg-ts', b'mpegts', b'h264', b'h.264', b'hevc', b'h.265',
    b'stream', b'transcod'
]
for kw in stream_keywords:
    matches = len(list(re.finditer(re.escape(kw), data, re.IGNORECASE)))
    if matches > 0:
        print(f"  {kw.decode('latin1'):15s}: {matches:3d} matches")

# Streaming Services/Apps
print("\n### Streaming Services/Frameworks ###")
service_keywords = [
    b'DLNA', b'UPnP', b'Miracast', b'AirPlay', b'Chromecast',
    b'WebRTC', b'HLS', b'DASH', b'ffmpeg', b'vlc', b'gstreamer',
    b'libav', b'IPTV', b'm3u8', b'playlist'
]
for kw in service_keywords:
    matches = len(list(re.finditer(re.escape(kw), data, re.IGNORECASE)))
    if matches > 0:
        print(f"  {kw.decode('latin1'):15s}: {matches:3d} matches")

# Network Ports (streaming-related)
print("\n### Network Streaming Ports ###")
port_keywords = [
    b':554', b':8080', b':8000', b':1935', b':5004'
]
for kw in port_keywords:
    matches = len(list(re.finditer(re.escape(kw), data, re.IGNORECASE)))
    if matches > 0:
        print(f"  {kw.decode('latin1'):15s}: {matches:3d} matches")

# Hardware video encoder/decoder
print("\n### Hardware Video Codec Indicators ###")
codec_keywords = [
    b'encoder', b'decoder', b'codec', b'vpu', b'gpu',
    b'mali', b'vivante', b'broadcom', b'hisilicon', b'amlogic'
]
for kw in codec_keywords:
    matches = len(list(re.finditer(re.escape(kw), data, re.IGNORECASE)))
    if matches > 0:
        print(f"  {kw.decode('latin1'):15s}: {matches:3d} matches")

# Check for HTTP server capabilities
print("\n### HTTP Server Capabilities ###")
http_keywords = [
    b'httpd', b'lighttpd', b'nginx', b'apache',
    b'webserver', b'cgi-bin', b'GET /', b'POST /'
]
for kw in http_keywords:
    matches = len(list(re.finditer(re.escape(kw), data, re.IGNORECASE)))
    if matches > 0:
        print(f"  {kw.decode('latin1'):15s}: {matches:3d} matches")

print("\n" + "=" * 70)
print("CONCLUSION:")
print("=" * 70)

# Analyze results
has_network = any(kw in data.lower() for kw in [b'ethernet', b'eth0', b'dhcp'])
has_wifi = any(kw in data.lower() for kw in [b'wifi', b'wlan', b'wireless'])
has_streaming = any(kw in data.lower() for kw in [b'rtsp', b'rtp', b'stream'])
has_http = any(kw in data.lower() for kw in [b'httpd', b'webserver'])

if has_wifi:
    print("[+] WiFi hardware support detected")
elif has_network:
    print("[+] Ethernet network support detected (no WiFi)")
else:
    print("[-] No clear network hardware indicators found")

if has_streaming:
    print("[+] Video streaming protocol indicators found")
else:
    print("[-] No streaming protocol indicators found")

if has_http:
    print("[+] HTTP server capabilities detected")
else:
    print("[-] No HTTP server found")

print("\nNOTE: Most matches are in compressed MAIN_FS (0x40000-0x4AC37F).")
print("Actual capabilities require runtime analysis or decompression.")
