#!/usr/bin/env python3
"""Simple RTSP connection test without video decoding"""

import socket
import time

STB_IP = "192.168.1.2"
RTSP_PORT = 554

def test_rtsp_connection():
    """Test RTSP connection and try to get stream info"""
    
    # Create socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.settimeout(10)
    
    try:
        print(f"Connecting to RTSP server at {STB_IP}:{RTSP_PORT}...")
        sock.connect((STB_IP, RTSP_PORT))
        print("[+] Connected to RTSP server")
        
        # Send OPTIONS request
        options = (
            f"OPTIONS rtsp://{STB_IP}:{RTSP_PORT}/stream RTSP/1.0\r\n"
            f"CSeq: 1\r\n"
            f"User-Agent: TestClient\r\n"
            f"\r\n"
        )
        
        sock.send(options.encode())
        response = sock.recv(4096).decode('latin1', errors='ignore')
        print("\n[OPTIONS Response]")
        print(response[:500])
        
        # Send DESCRIBE request
        describe = (
            f"DESCRIBE rtsp://{STB_IP}:{RTSP_PORT}/stream RTSP/1.0\r\n"
            f"CSeq: 2\r\n"
            f"Accept: application/sdp\r\n"
            f"User-Agent: TestClient\r\n"
            f"\r\n"
        )
        
        sock.send(describe.encode())
        response = sock.recv(4096).decode('latin1', errors='ignore')
        print("\n[DESCRIBE Response]")
        print(response[:1000])
        
        # Check if we got SDP
        if "m=video" in response:
            print("\n[+] Video stream found in SDP!")
            
            # Extract video codec info
            lines = response.split('\n')
            for line in lines:
                if line.startswith('m=video'):
                    print(f"    Video line: {line.strip()}")
                elif line.startswith('a=rtpmap:'):
                    print(f"    Codec info: {line.strip()}")
                elif line.startswith('a=control:'):
                    print(f"    Control: {line.strip()}")
        else:
            print("\n[-] No video stream found in SDP")
        
        # Try SETUP request
        setup = (
            f"SETUP rtsp://{STB_IP}:{RTSP_PORT}/stream/trackID=0 RTSP/1.0\r\n"
            f"CSeq: 3\r\n"
            f"Transport: RTP/AVP;unicast;client_port=5000-5001\r\n"
            f"User-Agent: TestClient\r\n"
            f"\r\n"
        )
        
        sock.send(setup.encode())
        response = sock.recv(4096).decode('latin1', errors='ignore')
        print("\n[SETUP Response]")
        print(response[:500])
        
        if "200 OK" in response:
            print("\n[+] SETUP successful - Stream is ready!")
            
            # Try PLAY
            play = (
                f"PLAY rtsp://{STB_IP}:{RTSP_PORT}/stream RTSP/1.0\r\n"
                f"CSeq: 4\r\n"
                f"Session: 66666666\r\n"  # Dummy session ID
                f"Range: npt=0.000-\r\n"
                f"User-Agent: TestClient\r\n"
                f"\r\n"
            )
            
            sock.send(play.encode())
            response = sock.recv(4096).decode('latin1', errors='ignore')
            print("\n[PLAY Response]")
            print(response[:500])
            
            if "200 OK" in response:
                print("\n[+] PLAY successful - Stream should be playing!")
                print("\nTry opening in VLC:")
                print(f"   rtsp://{STB_IP}:{RTSP_PORT}/stream")
            else:
                print("\n[-] PLAY failed")
        else:
            print("\n[-] SETUP failed")
            
    except Exception as e:
        print(f"[ERROR] {str(e)}")
    finally:
        sock.close()

if __name__ == "__main__":
    print("=" * 60)
    print("Simple RTSP Connection Test")
    print("=" * 60)
    test_rtsp_connection()
