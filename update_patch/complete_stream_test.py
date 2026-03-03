#!/usr/bin/env python3
"""Complete streaming test: change channel via gmscreen, then test RTSP stream"""

import sys
import os
import time
import socket
import subprocess
import threading

# Add gmscreen to path
sys.path.append(os.path.join(os.path.dirname(__file__), '..', 'gm_c', 'src', 'stb'))

STB_IP = "192.168.1.2"
RTSP_URL = f"rtsp://{STB_IP}:554/stream"

def connect_to_stb():
    """Connect to STB using gmscreen protocol"""
    try:
        import TcpClient
        client = TcpClient.TcpClient()
        
        print(f"Connecting to STB at {STB_IP}:20000...")
        if client.connect(STB_IP, 20000, 5000):
            print("[+] Connected to STB")
            return client
        else:
            print("[-] Failed to connect to STB")
            return None
    except Exception as e:
        print(f"[ERROR] {e}")
        return None

def send_command(client, cmd, params=None):
    """Send command to STB"""
    try:
        if params is None:
            params = []
        
        # Simple frame format (from gmscreen)
        payload = f'{{"cmd":{cmd},"params":{params}}}'
        frame = f"Start{len(payload):07d}End{payload}"
        
        result = client.send(frame.encode())
        if result:
            print(f"[+] Sent command {cmd}")
            return True
        else:
            print(f"[-] Failed to send command {cmd}")
            return False
    except Exception as e:
        print(f"[ERROR] sending command: {e}")
        return False

def change_channel(client):
    """Try to change to a known channel"""
    print("\n[1] Changing channel on STB...")
    
    # Try common channel change commands
    commands = [
        (1000, '[{"TvState":1,"ProgramId":"1"}]'),  # Change to channel 1
        (1000, '[{"TvState":1,"ProgramId":"2"}]'),  # Change to channel 2
        (1000, '[{"TvState":1,"ProgramId":"10"}]'), # Change to channel 10
    ]
    
    for cmd, params in commands:
        if send_command(client, cmd, params):
            time.sleep(2)  # Wait for channel to change
            return True
    
    return False

def test_rtsp_stream():
    """Test if RTSP stream has video data"""
    print("\n[2] Testing RTSP stream for video data...")
    
    # Create UDP socket for RTP
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.settimeout(2)
    sock.bind(('0.0.0.0', 5000))
    
    try:
        # Quick RTSP setup to trigger streaming
        rtsp_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        rtsp_sock.settimeout(5)
        rtsp_sock.connect((STB_IP, 554))
        
        # SETUP
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
                    print("[+] RTSP PLAY successful - listening for video packets...")
                    
                    # Listen for RTP packets
                    packet_count = 0
                    start_time = time.time()
                    
                    while time.time() - start_time < 10:  # Listen for 10 seconds
                        try:
                            data, addr = sock.recvfrom(2048)
                            packet_count += 1
                            
                            # Check if it's MPEG-2 TS
                            if len(data) >= 12:
                                payload_type = data[1] & 0x7F
                                if payload_type == 33:  # MPEG-2 TS
                                    payload = data[12:]
                                    if b'\x47' in payload:  # TS sync byte
                                        print(f"[+] Video packet #{packet_count} received ({len(data)} bytes)")
                                        return True
                                
                        except socket.timeout:
                            continue
                    
                    print(f"[-] No video packets received in 10 seconds")
                else:
                    print("[-] RTSP PLAY failed")
            else:
                print("[-] No session ID in SETUP response")
        else:
            print("[-] RTSP SETUP failed")
        
        rtsp_sock.close()
        
    except Exception as e:
        print(f"[ERROR] RTSP test: {e}")
    finally:
        sock.close()
    
    return False

def test_with_ffplay():
    """Test stream with FFplay"""
    print("\n[3] Testing with FFplay (5 seconds)...")
    
    try:
        # Run FFplay in background
        cmd = ['ffplay', '-v', 'quiet', '-t', '5', '-rtsp_transport', 'tcp', RTSP_URL]
        process = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        
        # Wait for completion
        stdout, stderr = process.communicate(timeout=10)
        
        if process.returncode == 0:
            print("[+] FFplay played successfully!")
            return True
        else:
            print(f"[-] FFplay failed with code {process.returncode}")
            if stderr:
                print(f"    Error: {stderr.decode()[:200]}")
            return False
            
    except subprocess.TimeoutExpired:
        process.kill()
        print("[-] FFplay timed out")
        return False
    except Exception as e:
        print(f"[ERROR] FFplay test: {e}")
        return False

def main():
    print("=" * 70)
    print("Complete MediaStar 4030 Streaming Test")
    print("=" * 70)
    print("This test will:")
    print("1. Connect to STB and change channel")
    print("2. Test RTSP stream for video data")
    print("3. Verify with FFplay")
    print()
    
    # Step 1: Connect to STB and change channel
    client = connect_to_stb()
    if not client:
        print("Cannot proceed without STB connection")
        return
    
    try:
        if change_channel(client):
            print("[+] Channel changed successfully")
        else:
            print("[!] Could not change channel, proceeding anyway...")
        
        # Step 2: Test RTSP stream
        if test_rtsp_stream():
            print("\n[+] SUCCESS: Video stream is working!")
            
            # Step 3: Verify with FFplay
            if test_with_ffplay():
                print("\n" + "=" * 70)
                print("FINAL RESULT: STREAMING IS WORKING!")
                print("=" * 70)
                print(f"You can watch the stream with:")
                print(f"  VLC: {RTSP_URL}")
                print(f"  FFplay: ffplay -rtsp_transport tcp {RTSP_URL}")
                print(f"  Browser (RTSP-enabled): {RTSP_URL}")
            else:
                print("\n[!] Stream detected but FFplay couldn't play it")
        else:
            print("\n[-] No video stream detected")
            print("\nTroubleshooting:")
            print("1. Make sure STB is on and connected to satellite")
            print("2. Make sure a channel is actively playing")
            print("3. Check network connectivity")
            print("4. Try changing channels manually on STB")
            
    finally:
        try:
            client.disconnect()
        except:
            pass

if __name__ == "__main__":
    main()
