#!/usr/bin/env python3
"""Test various RTSP URLs to find working streaming endpoint"""

import socket
import time

STB_IP = "192.168.1.2"
RTSP_PORT = 554

print("=" * 80)
print("Testing RTSP Streaming URLs on MediaStar 4030")
print("=" * 80)
print()

# Common RTSP paths for STB devices
rtsp_paths = [
    "/",
    "/stream",
    "/live",
    "/channel1",
    "/ch1",
    "/video",
    "/mpeg4",
    "/h264",
    "/main",
    "/stream0",
    "/stream1",
    "/av0",
    "/av1",
    "/media",
    "/rtsp",
    "/live.sdp",
    "/stream.sdp",
]

def test_rtsp_url(path):
    """Test RTSP URL with DESCRIBE method"""
    url = f"rtsp://{STB_IP}:{RTSP_PORT}{path}"
    
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(3)
        sock.connect((STB_IP, RTSP_PORT))
        
        # Send RTSP DESCRIBE request
        request = (
            f"DESCRIBE {url} RTSP/1.0\r\n"
            f"CSeq: 2\r\n"
            f"Accept: application/sdp\r\n"
            f"\r\n"
        )
        sock.send(request.encode())
        
        response = sock.recv(8192).decode('latin1', errors='ignore')
        sock.close()
        
        # Check response
        if "200 OK" in response:
            # Check if SDP content is present
            if "m=video" in response or "m=audio" in response or "a=control" in response:
                return ("WORKING", response)
            else:
                return ("OK_NO_SDP", response)
        elif "404" in response:
            return ("NOT_FOUND", response)
        elif "401" in response or "403" in response:
            return ("AUTH_REQUIRED", response)
        else:
            return ("UNKNOWN", response)
            
    except socket.timeout:
        return ("TIMEOUT", None)
    except Exception as e:
        return ("ERROR", str(e))

print("Testing RTSP paths (this may take a minute)...\n")

working_urls = []
possible_urls = []

for path in rtsp_paths:
    url = f"rtsp://{STB_IP}:{RTSP_PORT}{path}"
    print(f"Testing: {url:50s} ... ", end='', flush=True)
    
    status, response = test_rtsp_url(path)
    
    if status == "WORKING":
        print("[+] WORKING (SDP found)")
        working_urls.append((url, response))
    elif status == "OK_NO_SDP":
        print("[?] OK (no SDP)")
        possible_urls.append((url, response))
    elif status == "NOT_FOUND":
        print("[-] 404 Not Found")
    elif status == "AUTH_REQUIRED":
        print("[!] Auth Required")
    elif status == "TIMEOUT":
        print("[-] Timeout")
    elif status == "UNKNOWN":
        print(f"? Unknown: {response[:50] if response else 'N/A'}")
    else:
        print(f"[-] Error: {response}")
    
    time.sleep(0.1)  # Small delay between requests

print("\n" + "=" * 80)
print("RESULTS")
print("=" * 80)

if working_urls:
    print(f"\n[+] Found {len(working_urls)} WORKING streaming URL(s):\n")
    for url, response in working_urls:
        print(f"  {url}")
        print("\n  SDP Content:")
        # Extract and display SDP content
        if "\r\n\r\n" in response:
            sdp = response.split("\r\n\r\n", 1)[1]
            for line in sdp.split('\n')[:20]:
                if line.strip():
                    print(f"    {line.strip()}")
        print()

if possible_urls:
    print(f"\n[?] Found {len(possible_urls)} possible URL(s) (no SDP, may still work):\n")
    for url, response in possible_urls:
        print(f"  {url}")
        print(f"    Response: {response[:200]}")
        print()

if not working_urls and not possible_urls:
    print("\n[-] No working RTSP streaming URLs found")
    print("\nThe RTSP server exists but may require:")
    print("  - Specific authentication")
    print("  - Special URL format not tested")
    print("  - Activation through STB menu")
    print("\nTry opening VLC and manually testing:")
    print(f"  rtsp://{STB_IP}:554/")

print("\n" + "=" * 80)
print("HOW TO VIEW THE STREAM")
print("=" * 80)

if working_urls or possible_urls:
    test_url = working_urls[0][0] if working_urls else possible_urls[0][0]
    
    print(f"\n1. VLC Media Player:")
    print(f"   - Open VLC")
    print(f"   - Media → Open Network Stream")
    print(f"   - Enter: {test_url}")
    print(f"   - Click Play")
    
    print(f"\n2. FFplay (command line):")
    print(f"   ffplay -rtsp_transport tcp {test_url}")
    
    print(f"\n3. Browser (if browser supports RTSP):")
    print(f"   {test_url}")
    
    print(f"\n4. Python/OpenCV:")
    print(f"   import cv2")
    print(f"   cap = cv2.VideoCapture('{test_url}')")
    print(f"   ret, frame = cap.read()")

print("\n" + "=" * 80)
