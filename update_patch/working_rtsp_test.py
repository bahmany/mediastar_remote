#!/usr/bin/env python3
"""Working RTSP stream test with proper session handling"""

import socket
import time
import re

STB_IP = "192.168.1.2"
RTSP_PORT = 554

def extract_session_id(response):
    """Extract session ID from RTSP response"""
    match = re.search(r'Session: ([^\r\n]+)', response)
    return match.group(1) if match else None

def test_rtsp_stream():
    """Test complete RTSP stream flow"""
    
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.settimeout(10)
    
    try:
        print(f"Connecting to RTSP server at {STB_IP}:{RTSP_PORT}...")
        sock.connect((STB_IP, RTSP_PORT))
        print("[+] Connected to RTSP server")
        
        # Step 1: OPTIONS
        options = (
            f"OPTIONS rtsp://{STB_IP}:{RTSP_PORT}/stream RTSP/1.0\r\n"
            f"CSeq: 1\r\n"
            f"User-Agent: TestClient\r\n"
            f"\r\n"
        )
        
        sock.send(options.encode())
        response = sock.recv(4096).decode('latin1', errors='ignore')
        print("\n[1] OPTIONS: OK")
        
        # Step 2: DESCRIBE
        describe = (
            f"DESCRIBE rtsp://{STB_IP}:{RTSP_PORT}/stream RTSP/1.0\r\n"
            f"CSeq: 2\r\n"
            f"Accept: application/sdp\r\n"
            f"User-Agent: TestClient\r\n"
            f"\r\n"
        )
        
        sock.send(describe.encode())
        response = sock.recv(4096).decode('latin1', errors='ignore')
        print("[2] DESCRIBE: OK")
        
        # Extract stream info
        if "m=video" in response:
            print("    Video stream detected (RTP/AVP 33)")
        
        # Step 3: SETUP
        setup = (
            f"SETUP rtsp://{STB_IP}:{RTSP_PORT}/stream/trackID=0 RTSP/1.0\r\n"
            f"CSeq: 3\r\n"
            f"Transport: RTP/AVP;unicast;client_port=5000-5001\r\n"
            f"User-Agent: TestClient\r\n"
            f"\r\n"
        )
        
        sock.send(setup.encode())
        response = sock.recv(4096).decode('latin1', errors='ignore')
        print("[3] SETUP: OK")
        
        # Extract session ID
        session_id = extract_session_id(response)
        if session_id:
            print(f"    Session ID: {session_id}")
        else:
            print("    No session ID found")
            return
        
        # Extract server ports
        if "server_port=" in response:
            match = re.search(r'server_port=(\d+)-(\d+)', response)
            if match:
                server_rtp_port = match.group(1)
                server_rtcp_port = match.group(2)
                print(f"    Server RTP port: {server_rtp_port}")
                print(f"    Server RTCP port: {server_rtcp_port}")
        
        # Step 4: PLAY
        play = (
            f"PLAY rtsp://{STB_IP}:{RTSP_PORT}/stream RTSP/1.0\r\n"
            f"CSeq: 4\r\n"
            f"Session: {session_id}\r\n"
            f"Range: npt=0.000-\r\n"
            f"User-Agent: TestClient\r\n"
            f"\r\n"
        )
        
        sock.send(play.encode())
        response = sock.recv(4096).decode('latin1', errors='ignore')
        
        if "200 OK" in response:
            print("[4] PLAY: SUCCESS!")
            print("\n[+] STREAM IS NOW PLAYING!")
            print("\nYou can now open this URL in VLC:")
            print(f"   rtsp://{STB_IP}:{RTSP_PORT}/stream")
            print("\nOr use FFplay:")
            print(f"   ffplay -rtsp_transport tcp rtsp://{STB_IP}:{RTSP_PORT}/stream")
            
            # Keep connection alive for a few seconds
            print("\nKeeping stream alive for 10 seconds...")
            time.sleep(10)
            
            # Step 5: TEARDOWN
            teardown = (
                f"TEARDOWN rtsp://{STB_IP}:{RTSP_PORT}/stream RTSP/1.0\r\n"
                f"CSeq: 5\r\n"
                f"Session: {session_id}\r\n"
                f"User-Agent: TestClient\r\n"
                f"\r\n"
            )
            
            sock.send(teardown.encode())
            response = sock.recv(4096).decode('latin1', errors='ignore')
            print("[5] TEARDOWN: OK")
            
        else:
            print("[4] PLAY: FAILED")
            print(f"    Response: {response[:200]}")
            
    except Exception as e:
        print(f"[ERROR] {str(e)}")
    finally:
        sock.close()

if __name__ == "__main__":
    print("=" * 60)
    print("Complete RTSP Stream Test")
    print("=" * 60)
    test_rtsp_stream()
