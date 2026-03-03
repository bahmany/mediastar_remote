#!/usr/bin/env python3
"""Final comprehensive test of MediaStar 4030 streaming capabilities"""

import socket
import subprocess
import time
import threading
import sys

STB_IP = "192.168.1.2"
RTSP_URL = f"rtsp://{STB_IP}:554/stream"

def test_ffplay():
    """Test stream with FFplay"""
    print("[1] Testing with FFplay...")
    
    try:
        # Run FFplay with verbose output
        cmd = [
            'ffplay', 
            '-v', 'verbose', 
            '-t', '8',  # 8 seconds
            '-rtsp_transport', 'tcp',
            '-rtsp_flags', 'prefer_tcp',
            '-analyzeduration', '5000000',  # 5 seconds
            '-probesize', '5000000',
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
        
        # Capture output in real-time
        output_lines = []
        start_time = time.time()
        
        while time.time() - start_time < 12:  # Wait up to 12 seconds
            if process.poll() is not None:
                break
                
            try:
                line = process.stdout.readline()
                if line:
                    output_lines.append(line.strip())
                    print(f"    {line.strip()}")
                    
                    # Check for successful stream detection
                    if "Stream #0:0: Video:" in line:
                        print("  [+] Video stream detected!")
                    elif "Stream #0:1: Audio:" in line:
                        print("  [+] Audio stream detected!")
                    elif "size=" in line and "fps=" in line:
                        print("  [+] Video parameters found!")
                        
            except:
                break
        
        # Wait for process to complete
        try:
            stdout, stderr = process.communicate(timeout=3)
            if stdout:
                for line in stdout.split('\n'):
                    if line.strip():
                        output_lines.append(line.strip())
                        print(f"    {line.strip()}")
        except subprocess.TimeoutExpired:
            process.kill()
            print("  [!] FFplay timed out, killing process")
        
        # Analyze results
        success_indicators = [
            "Video:", "Audio:", "fps=", "size=", "bitrate=",
            "Stream #0:", "Input #0:", "detected"
        ]
        
        success = any(indicator in ' '.join(output_lines) for indicator in success_indicators)
        
        if success:
            print("  [+] FFplay detected video/audio stream!")
            return True, output_lines
        else:
            print("  [-] FFplay did not detect valid stream")
            return False, output_lines
            
    except Exception as e:
        print(f"  [ERROR] {e}")
        return False, []

def test_ffmpeg_probe():
    """Test stream with FFprobe for detailed analysis"""
    print("\n[2] Testing with FFprobe...")
    
    try:
        cmd = [
            'ffprobe',
            '-v', 'error',
            '-show_format',
            '-show_streams',
            '-print_format', 'json',
            '-rtsp_transport', 'tcp',
            '-analyzeduration', '3000000',  # 3 seconds
            RTSP_URL
        ]
        
        print(f"  Running: {' '.join(cmd)}")
        process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            universal_newlines=True
        )
        
        try:
            stdout, stderr = process.communicate(timeout=10)
            
            if process.returncode == 0 and stdout:
                print("  [+] FFprobe successful!")
                print("  Stream details:")
                
                # Parse JSON output
                try:
                    import json
                    data = json.loads(stdout)
                    
                    if 'streams' in data:
                        for i, stream in enumerate(data['streams']):
                            codec_type = stream.get('codec_type', 'unknown')
                            codec_name = stream.get('codec_name', 'unknown')
                            print(f"    Stream {i}: {codec_type} ({codec_name})")
                            
                            if codec_type == 'video':
                                width = stream.get('width', 'unknown')
                                height = stream.get('height', 'unknown')
                                fps = stream.get('r_frame_rate', 'unknown')
                                print(f"      Resolution: {width}x{height}")
                                print(f"      FPS: {fps}")
                            elif codec_type == 'audio':
                                sample_rate = stream.get('sample_rate', 'unknown')
                                channels = stream.get('channels', 'unknown')
                                print(f"      Sample rate: {sample_rate} Hz")
                                print(f"      Channels: {channels}")
                    
                    if 'format' in data:
                        format_name = data['format'].get('format_name', 'unknown')
                        duration = data['format'].get('duration', 'unknown')
                        print(f"    Container: {format_name}")
                        print(f"    Duration: {duration} seconds")
                    
                    return True, data
                    
                except json.JSONDecodeError:
                    print("  [!] Could not parse FFprobe JSON output")
                    print(f"  Raw output: {stdout[:500]}")
                    return False, stdout
            else:
                print(f"  [-] FFprobe failed (code {process.returncode})")
                if stderr:
                    print(f"    Error: {stderr[:300]}")
                return False, stderr
                
        except subprocess.TimeoutExpired:
            process.kill()
            print("  [!] FFprobe timed out")
            return False, []
            
    except Exception as e:
        print(f"  [ERROR] {e}")
        return False, []

def test_direct_rtp():
    """Test for direct RTP packets"""
    print("\n[3] Testing for RTP packets...")
    
    # First do a quick RTSP SETUP to trigger streaming
    try:
        rtsp_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        rtsp_sock.settimeout(5)
        rtsp_sock.connect((STB_IP, 554))
        
        setup = (
            f"SETUP rtsp://{STB_IP}:554/stream/trackID=0 RTSP/1.0\r\n"
            f"CSeq: 1\r\n"
            f"Transport: RTP/AVP;unicast;client_port=5000-5001\r\n"
            f"\r\n"
        )
        
        rtsp_sock.send(setup.encode())
        response = rtsp_sock.recv(4096).decode('latin1', errors='ignore')
        
        if "200 OK" in response:
            # Extract session ID
            import re
            match = re.search(r'Session: ([^\r\n]+)', response)
            session_id = match.group(1) if match else None
            
            if session_id:
                # PLAY
                play = (
                    f"PLAY rtsp://{STB_IP}:554/stream RTSP/1.0\r\n"
                    f"CSeq: 2\r\n"
                    f"Session: {session_id}\r\n"
                    f"\r\n"
                )
                
                rtsp_sock.send(play.encode())
                response = rtsp_sock.recv(4096).decode('latin1', errors='ignore')
                
                if "200 OK" in response:
                    print("  [+] RTSP session established, listening for RTP...")
                    
                    # Listen for RTP packets
                    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
                    sock.settimeout(2)
                    sock.bind(('0.0.0.0', 5000))
                    
                    packet_count = 0
                    start_time = time.time()
                    
                    while time.time() - start_time < 8:  # Listen for 8 seconds
                        try:
                            data, addr = sock.recvfrom(2048)
                            packet_count += 1
                            
                            # Parse RTP header
                            if len(data) >= 12:
                                payload_type = data[1] & 0x7F
                                sequence = (data[2] << 8) | data[3]
                                
                                print(f"    Packet #{packet_count}: {len(data)} bytes, PT={payload_type}, Seq={sequence}")
                                
                                if payload_type == 33:  # MPEG-2 TS
                                    payload = data[12:]
                                    sync_bytes = payload.count(b'\x47')
                                    if sync_bytes > 0:
                                        print(f"      [MPEG-2 TS: {sync_bytes} sync bytes found]")
                                        return True
                                
                        except socket.timeout:
                            continue
                    
                    print(f"  [-] Received {packet_count} packets, no MPEG-2 TS detected")
                else:
                    print("  [-] RTSP PLAY failed")
            else:
                print("  [-] No session ID")
        else:
            print("  [-] RTSP SETUP failed")
        
        rtsp_sock.close()
        
    except Exception as e:
        print(f"  [ERROR] {e}")
    
    return False

def main():
    print("=" * 80)
    print("FINAL COMPREHENSIVE STREAMING TEST")
    print("=" * 80)
    print(f"Target: {STB_IP}")
    print(f"RTSP URL: {RTSP_URL}")
    print()
    print("This test will:")
    print("1. Try to play the stream with FFplay")
    print("2. Analyze the stream with FFprobe")
    print("3. Listen for RTP packets directly")
    print()
    print("Note: Make sure the STB is ON and playing a channel!")
    print("=" * 80)
    
    results = []
    
    # Test 1: FFplay
    success1, data1 = test_ffplay()
    results.append(("FFplay", success1, data1))
    
    # Test 2: FFprobe
    success2, data2 = test_ffmpeg_probe()
    results.append(("FFprobe", success2, data2))
    
    # Test 3: Direct RTP
    success3 = test_direct_rtp()
    results.append(("RTP", success3, None))
    
    # Summary
    print("\n" + "=" * 80)
    print("FINAL RESULTS")
    print("=" * 80)
    
    for test_name, success, data in results:
        status = "[+] WORKING" if success else "[-] FAILED"
        print(f"{test_name:10s}: {status}")
    
    overall_success = any(success for _, success, _ in results)
    
    print("\n" + "=" * 80)
    if overall_success:
        print("CONCLUSION: STREAMING IS WORKING! 🎉")
        print("=" * 80)
        print("\nYou can watch the stream using:")
        print(f"  VLC:        {RTSP_URL}")
        print(f"  FFplay:     ffplay -rtsp_transport tcp {RTSP_URL}")
        print(f"  MPV:        mpv {RTSP_URL}")
        print(f"  Web player: Any RTSP-enabled player")
        print("\nTips:")
        print("- Use TCP transport for better reliability")
        print("- Make sure STB is actively playing a channel")
        print("- Some delay (1-3 seconds) is normal")
    else:
        print("CONCLUSION: STREAMING NOT WORKING ❌")
        print("=" * 80)
        print("\nTroubleshooting:")
        print("1. Make sure STB is powered on and connected")
        print("2. Make sure a channel is actively playing")
        print("3. Check network connectivity to STB")
        print("4. Try changing channels on the STB")
        print("5. The STB might need to be in a specific mode for streaming")
        print("\nAlternative solutions:")
        print("- Use HDMI capture card for local streaming")
        print("- Use HDMI to IP encoder device")

if __name__ == "__main__":
    main()
