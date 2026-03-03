#!/usr/bin/env python3
"""Test DVB to IPTV functionality on MediaStar 4030"""

import socket
import time
import subprocess
import threading

STB_IP = "192.168.1.2"
RTSP_URL = f"rtsp://{STB_IP}:554/stream"

def connect_to_stb():
    """Connect to STB control port"""
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(5)
        sock.connect((STB_IP, 20000))
        return sock
    except Exception as e:
        print(f"[ERROR] Cannot connect to STB: {e}")
        return None

def send_stb_command(sock, command):
    """Send command to STB and get response"""
    try:
        # Simple frame format
        payload = command
        frame = f"Start{len(payload):07d}End{payload}"
        
        sock.send(frame.encode())
        time.sleep(0.5)
        
        # Try to read response
        sock.settimeout(2)
        response = sock.recv(4096).decode('latin1', errors='ignore')
        return response
    except Exception as e:
        print(f"[ERROR] sending command: {e}")
        return None

def test_stream_after_activation():
    """Test RTSP stream after DVB to IPTV activation"""
    print("\n[2] Testing stream after DVB to IPTV activation...")
    
    # Test with FFplay for 10 seconds
    try:
        cmd = [
            'ffplay',
            '-v', 'verbose',
            '-t', '10',
            '-rtsp_transport', 'tcp',
            '-analyzeduration', '3000000',
            RTSP_URL
        ]
        
        print(f"  Running: {' '.join(cmd)}")
        process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            universal_newlines=True
        )
        
        output_lines = []
        start_time = time.time()
        video_detected = False
        
        while time.time() - start_time < 15:
            if process.poll() is not None:
                break
                
            try:
                line = process.stdout.readline()
                if line:
                    output_lines.append(line.strip())
                    print(f"    {line.strip()}")
                    
                    # Check for video stream
                    if "Video:" in line or "Stream #0:0: Video:" in line:
                        video_detected = True
                        print("  [+] VIDEO STREAM DETECTED!")
                    elif "Audio:" in line or "Stream #0:1: Audio:" in line:
                        print("  [+] AUDIO STREAM DETECTED!")
                    elif "size=" in line and "fps=" in line:
                        print("  [+] VIDEO PARAMETERS FOUND!")
                        
            except:
                break
        
        # Wait for completion
        try:
            process.communicate(timeout=5)
        except subprocess.TimeoutExpired:
            process.kill()
            print("  [!] Killing FFplay process")
        
        return video_detected or any("Video:" in line for line in output_lines)
        
    except Exception as e:
        print(f"  [ERROR] {e}")
        return False

def main():
    print("=" * 80)
    print("DVB to IPTV Activation Test")
    print("=" * 80)
    print("This test will:")
    print("1. Try to activate DVB to IPTV functionality")
    print("2. Test RTSP stream after activation")
    print()
    print("Make sure you have found DVB to IPTV in STB menu!")
    print("=" * 80)
    
    # Connect to STB
    sock = connect_to_stb()
    if not sock:
        print("Cannot proceed without STB connection")
        return
    
    try:
        print("[1] Testing DVB to IPTV activation...")
        print("\nTrying common DVB to IPTV commands...")
        
        # Try different commands that might activate DVB to IPTV
        commands_to_try = [
            # Common DVB to IPTV activation commands
            '{"cmd":2000,"params":[{"enable":1}]}',  # Enable streaming
            '{"cmd":2001,"params":[{"dvb_to_iptv":1}]}',  # DVB to IPTV enable
            '{"cmd":2002,"params":[{"streaming":"start"}]}',  # Start streaming
            '{"cmd":2003,"params":[{"rtsp":"enable"}]}',  # Enable RTSP
            '{"cmd":2004,"params":[{"transcode":1}]}',  # Enable transcoding
            '{"cmd":2005,"params":[{"output":"network"}]}',  # Network output
            '{"cmd":2006,"params":[{"dvb_stream":1}]}',  # DVB stream enable
            '{"cmd":2007,"params":[{"iptv_server":1}]}',  # IPTV server
            '{"cmd":2008,"params":[{"stream_mode":"dvb"}]}',  # Stream mode DVB
            '{"cmd":2009,"params":[{"network_stream":1}]}',  # Network stream
        ]
        
        for i, cmd in enumerate(commands_to_try, 1):
            print(f"\n  Trying command {i}: {cmd[:50]}...")
            response = send_stb_command(sock, cmd)
            
            if response:
                print(f"    Response: {response[:100]}")
                if "OK" in response or "success" in response.lower():
                    print("    [+] Command successful!")
                    
                    # Test stream immediately after successful command
                    if test_stream_after_activation():
                        print("\n" + "=" * 80)
                        print("SUCCESS! DVB to IPTV is now working!")
                        print("=" * 80)
                        print(f"You can now watch the stream:")
                        print(f"  VLC: {RTSP_URL}")
                        print(f"  FFplay: ffplay -rtsp_transport tcp {RTSP_URL}")
                        return
                    else:
                        print("    Stream not yet working, trying next command...")
                else:
                    print("    Command failed or no effect")
            else:
                print("    No response")
            
            time.sleep(1)  # Wait between commands
        
        print("\n[!] No automatic activation worked")
        print("\nManual activation required:")
        print("1. Go to STB menu and find 'DVB to IPTV' option")
        print("2. Enable it manually")
        print("3. Select a channel to stream")
        print("4. Save settings")
        print("5. Run this test again")
        
        # Test stream anyway in case it's already enabled
        print("\n[2] Testing current stream status...")
        if test_stream_after_activation():
            print("\n[+] Stream is already working!")
        else:
            print("\n[-] Stream not working yet")
        
    finally:
        sock.close()

if __name__ == "__main__":
    main()
