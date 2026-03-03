#!/usr/bin/env python3
"""RTP packet listener to test if STB is actually sending video data"""

import socket
import time
import struct

STB_IP = "192.168.1.2"
RTP_PORT = 5006  # From SETUP response

def listen_for_rtp_packets(timeout=10):
    """Listen for RTP packets from STB"""
    
    # Create UDP socket for RTP
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.settimeout(1)
    
    try:
        # Bind to client port (from SETUP: client_port=5000-5001)
        sock.bind(('0.0.0.0', 5000))
        print(f"Listening for RTP packets on port 5000...")
        print(f"Expected from {STB_IP}:{RTP_PORT}")
        print("-" * 50)
        
        packet_count = 0
        start_time = time.time()
        
        while time.time() - start_time < timeout:
            try:
                data, addr = sock.recvfrom(2048)
                packet_count += 1
                
                # Parse RTP header (first 12 bytes)
                if len(data) >= 12:
                    # RTP header structure
                    v_p_x_cc = data[0]
                    version = (v_p_x_cc >> 6) & 0x3
                    padding = (v_p_x_cc >> 5) & 0x1
                    extension = (v_p_x_cc >> 4) & 0x1
                    csrc_count = v_p_x_cc & 0x0F
                    
                    m_pt = data[1]
                    marker = (m_pt >> 7) & 0x1
                    payload_type = m_pt & 0x7F
                    
                    sequence = struct.unpack('!H', data[2:4])[0]
                    timestamp = struct.unpack('!I', data[4:8])[0]
                    ssrc = struct.unpack('!I', data[8:12])[0]
                    
                    print(f"Packet #{packet_count}: {len(data)} bytes from {addr}")
                    print(f"  RTP: v={version}, pt={payload_type}, seq={sequence}, ts={timestamp}")
                    print(f"  SSRC: {ssrc:08X}, Marker={marker}")
                    
                    # Check if it's MPEG-2 TS (payload type 33)
                    if payload_type == 33:
                        print("  [MPEG-2 TS payload detected]")
                        
                        # Look for TS sync bytes (0x47) in payload
                        payload = data[12:]
                        sync_count = payload.count(b'\x47')
                        if sync_count > 0:
                            print(f"  Found {sync_count} MPEG-TS sync bytes in payload")
                    
                    print()
                else:
                    print(f"Packet #{packet_count}: {len(data)} bytes (too short for RTP)")
                    
            except socket.timeout:
                continue
            except Exception as e:
                print(f"Error receiving packet: {e}")
                break
        
        if packet_count == 0:
            print("No RTP packets received.")
            print("\nPossible reasons:")
            print("  - STB is not actively streaming")
            print("  - No channel is selected/playing")
            print("  - Network firewall blocking UDP")
            print("  - RTSP session not properly established")
        else:
            print(f"\nReceived {packet_count} RTP packets in {timeout} seconds")
            print("Stream appears to be working!")
            
    except Exception as e:
        print(f"Error: {e}")
    finally:
        sock.close()

if __name__ == "__main__":
    print("=" * 60)
    print("RTP Packet Listener Test")
    print("=" * 60)
    print("This test checks if the STB is actually sending video data")
    print("Note: You may need to start playing a channel on the STB first")
    print()
    
    listen_for_rtp_packets(timeout=15)
