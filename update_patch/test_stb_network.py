#!/usr/bin/env python3
"""Comprehensive network test for MediaStar 4030 STB streaming capabilities"""

import socket
import struct
import time
from concurrent.futures import ThreadPoolExecutor, as_completed

STB_IP = "192.168.1.2"

print("=" * 80)
print("MediaStar 4030 STB - Comprehensive Network Streaming Test")
print("=" * 80)
print(f"Target: {STB_IP}")
print()

# Test 1: Quick port scan on common streaming ports
print("[1/5] Testing Common Streaming Ports...")
print("-" * 80)

streaming_ports = {
    20000: "MediaStar Control Protocol",
    554: "RTSP (Real Time Streaming Protocol)",
    8080: "HTTP Streaming / Web Server",
    8000: "HTTP Streaming Alt / CCcam",
    8081: "HTTP Streaming Alt",
    1935: "RTMP (Flash Streaming)",
    5004: "RTP (Real-time Transport)",
    8554: "RTSP Alt",
    9090: "HTTP Streaming Alt",
    80: "HTTP Web Server",
    443: "HTTPS",
    7070: "RealPlayer / RTSP Alt",
    1234: "UDP Multicast (common)",
    5000: "UPnP",
    1900: "UPnP SSDP Discovery",
}

open_ports = []

def test_port(port, desc, timeout=2):
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(timeout)
        result = sock.connect_ex((STB_IP, port))
        sock.close()
        if result == 0:
            return (port, desc, True)
        return (port, desc, False)
    except:
        return (port, desc, False)

with ThreadPoolExecutor(max_workers=20) as executor:
    futures = [executor.submit(test_port, port, desc) for port, desc in streaming_ports.items()]
    for future in as_completed(futures):
        port, desc, is_open = future.result()
        if is_open:
            print(f"  [OPEN]   Port {port:5d} - {desc}")
            open_ports.append((port, desc))
        else:
            print(f"  [CLOSED] Port {port:5d} - {desc}")

print()

# Test 2: Extended port scan (1-1024 common ports)
print("[2/5] Extended Port Scan (1-1024)...")
print("-" * 80)

def quick_scan(port):
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(0.5)
        result = sock.connect_ex((STB_IP, port))
        sock.close()
        return (port, result == 0)
    except:
        return (port, False)

additional_open = []
with ThreadPoolExecutor(max_workers=50) as executor:
    futures = [executor.submit(quick_scan, port) for port in range(1, 1025)]
    for future in as_completed(futures):
        port, is_open = future.result()
        if is_open and port not in [p for p, _ in open_ports]:
            additional_open.append(port)

if additional_open:
    print(f"  Found {len(additional_open)} additional open ports:")
    for port in sorted(additional_open):
        print(f"    Port {port}")
        open_ports.append((port, "Unknown service"))
else:
    print("  No additional open ports found in range 1-1024")

print()

# Test 3: Try RTSP connection
print("[3/5] Testing RTSP Protocol...")
print("-" * 80)

rtsp_tested = False
for port, desc in open_ports:
    if port in [554, 8554, 7070]:
        rtsp_tested = True
        print(f"  Testing RTSP on port {port}...")
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.settimeout(3)
            sock.connect((STB_IP, port))
            
            # Send RTSP OPTIONS request
            request = f"OPTIONS rtsp://{STB_IP}:{port}/ RTSP/1.0\r\nCSeq: 1\r\n\r\n"
            sock.send(request.encode())
            
            response = sock.recv(4096).decode('latin1', errors='ignore')
            sock.close()
            
            if "RTSP" in response:
                print(f"    [SUCCESS] RTSP server responded!")
                print(f"    Response: {response[:200]}")
            else:
                print(f"    [FAIL] No RTSP response")
        except Exception as e:
            print(f"    [FAIL] Error: {e}")

if not rtsp_tested:
    print("  No RTSP ports open to test")

print()

# Test 4: Try HTTP requests on open ports
print("[4/5] Testing HTTP Services...")
print("-" * 80)

http_tested = False
for port, desc in open_ports:
    if port in [80, 8080, 8000, 8081, 9090]:
        http_tested = True
        print(f"  Testing HTTP on port {port}...")
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.settimeout(3)
            sock.connect((STB_IP, port))
            
            # Send HTTP GET request
            request = f"GET / HTTP/1.1\r\nHost: {STB_IP}\r\nConnection: close\r\n\r\n"
            sock.send(request.encode())
            
            response = sock.recv(4096).decode('latin1', errors='ignore')
            sock.close()
            
            if "HTTP" in response or "html" in response.lower():
                print(f"    [SUCCESS] HTTP server responded!")
                lines = response.split('\n')[:10]
                for line in lines:
                    print(f"      {line.strip()}")
            else:
                print(f"    [UNKNOWN] Response: {response[:100]}")
        except Exception as e:
            print(f"    [FAIL] Error: {e}")

if not http_tested:
    print("  No HTTP ports open to test")

print()

# Test 5: UPnP/SSDP Discovery
print("[5/5] Testing UPnP/DLNA Discovery...")
print("-" * 80)

try:
    # Send SSDP M-SEARCH
    ssdp_request = (
        'M-SEARCH * HTTP/1.1\r\n'
        'HOST: 239.255.255.250:1900\r\n'
        'MAN: "ssdp:discover"\r\n'
        'MX: 3\r\n'
        'ST: ssdp:all\r\n'
        '\r\n'
    )
    
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.settimeout(5)
    sock.sendto(ssdp_request.encode(), ('239.255.255.250', 1900))
    
    print("  Listening for UPnP/DLNA responses...")
    responses = []
    try:
        while True:
            data, addr = sock.recvfrom(4096)
            if addr[0] == STB_IP:
                responses.append(data.decode('latin1', errors='ignore'))
    except socket.timeout:
        pass
    
    sock.close()
    
    if responses:
        print(f"  [SUCCESS] Found {len(responses)} UPnP service(s) from STB:")
        for i, resp in enumerate(responses, 1):
            print(f"    Response {i}:")
            lines = resp.split('\n')[:8]
            for line in lines:
                print(f"      {line.strip()}")
    else:
        print("  [FAIL] No UPnP/DLNA services found")
        
except Exception as e:
    print(f"  [FAIL] UPnP discovery error: {e}")

print()
print("=" * 80)
print("FINAL REPORT")
print("=" * 80)

print(f"\nTotal Open Ports: {len(open_ports)}")
for port, desc in sorted(open_ports):
    print(f"  - Port {port}: {desc}")

print("\n--- STREAMING CAPABILITY ASSESSMENT ---")

has_rtsp = any(p in [554, 8554, 7070] for p, _ in open_ports)
has_http = any(p in [80, 8080, 8000, 8081, 9090] for p, _ in open_ports)
has_control = any(p == 20000 for p, _ in open_ports)
has_cccam = any(p == 8000 for p, _ in open_ports)

if has_rtsp:
    print("\n[+] RTSP port open - Device MAY support RTSP streaming")
    print("    Try: rtsp://192.168.1.2:554/")
else:
    print("\n[-] No RTSP ports detected")

if has_http:
    print("\n[+] HTTP port open - Device MAY have web interface or HTTP streaming")
    http_ports = [p for p, _ in open_ports if p in [80, 8080, 8000, 8081, 9090]]
    for p in http_ports:
        print(f"    Try: http://192.168.1.2:{p}/")
else:
    print("\n[-] No HTTP ports detected")

if has_control:
    print("\n[+] MediaStar control port (20000) open - gmscreen working correctly")

if has_cccam:
    print("\n[+] CCcam server port (8000) open - Card sharing active")

print("\n--- CONCLUSION ---")
if has_rtsp or has_http:
    print("Device has network services that COULD support streaming.")
    print("Manual testing with VLC/browser required to confirm actual streaming.")
else:
    print("No standard streaming ports detected.")
    print("Device likely does NOT support video streaming over network.")
    print("Use HDMI capture card for remote video access.")

print("\n" + "=" * 80)
