#!/usr/bin/env python3
"""Test RTSP video stream from MediaStar 4030"""

import cv2
import time
import threading
import sys

STB_IP = "192.168.1.2"
RTSP_URLS = [
    f"rtsp://{STB_IP}:554/stream",
    f"rtsp://{STB_IP}:554/live", 
    f"rtsp://{STB_IP}:554/video",
    f"rtsp://{STB_IP}:554/h264",
    f"rtsp://{STB_IP}:554/main",
]

def test_stream(url, timeout=10):
    """Test RTSP stream and return success info"""
    print(f"\nTesting: {url}")
    print("-" * 60)
    
    try:
        # Try to open the stream
        cap = cv2.VideoCapture(url)
        
        # Set timeout for connection
        cap.set(cv2.CAP_PROP_OPEN_TIMEOUT_MSEC, timeout * 1000)
        cap.set(cv2.CAP_PROP_READ_TIMEOUT_MSEC, timeout * 1000)
        
        if not cap.isOpened():
            print(f"[FAIL] Could not open stream")
            return False, "Could not open stream"
        
        # Try to read a frame
        ret, frame = cap.read()
        
        if ret and frame is not None:
            height, width = frame.shape[:2]
            print(f"[SUCCESS] Stream working!")
            print(f"  Resolution: {width}x{height}")
            print(f"  Frame type: {type(frame)}")
            
            # Try to get stream properties
            fps = cap.get(cv2.CAP_PROP_FPS)
            fourcc = int(cap.get(cv2.CAP_PROP_FOURCC))
            print(f"  FPS: {fps}")
            print(f"  Codec: {fourcc:08X}")
            
            cap.release()
            return True, f"Resolution: {width}x{height}, FPS: {fps}"
        else:
            print(f"[FAIL] Could not read frame")
            cap.release()
            return False, "Could not read frame"
            
    except Exception as e:
        print(f"[ERROR] {str(e)}")
        return False, str(e)

def main():
    print("=" * 80)
    print("MediaStar 4030 RTSP Video Stream Test")
    print("=" * 80)
    print(f"Target: {STB_IP}")
    print()
    
    # Test all URLs
    working_urls = []
    
    for url in RTSP_URLS:
        success, info = test_stream(url, timeout=8)
        if success:
            working_urls.append((url, info))
    
    print("\n" + "=" * 80)
    print("RESULTS")
    print("=" * 80)
    
    if working_urls:
        print(f"\n[+] Found {len(working_urls)} working stream(s):\n")
        for url, info in working_urls:
            print(f"  {url}")
            print(f"    {info}")
            print()
        
        # Test the first working URL with live preview
        best_url = working_urls[0][0]
        print(f"\n[DEMO] Starting 15-second preview of: {best_url}")
        print("Press 'q' to quit early...")
        
        try:
            cap = cv2.VideoCapture(best_url)
            cap.set(cv2.CAP_PROP_BUFFERSIZE, 1)  # Reduce latency
            
            start_time = time.time()
            frame_count = 0
            
            while time.time() - start_time < 15:
                ret, frame = cap.read()
                if ret:
                    frame_count += 1
                    # Add timestamp overlay
                    cv2.putText(frame, f"Frame: {frame_count}", (10, 30), 
                               cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2)
                    cv2.putText(frame, f"Time: {int(time.time() - start_time)}s", (10, 70), 
                               cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2)
                    
                    cv2.imshow("MediaStar 4030 Live Stream", frame)
                    
                    key = cv2.waitKey(1) & 0xFF
                    if key == ord('q'):
                        break
                else:
                    print("  [WARN] Failed to read frame")
                    time.sleep(0.1)
            
            cap.release()
            cv2.destroyAllWindows()
            print(f"\n[DEMO] Played {frame_count} frames in {int(time.time() - start_time)} seconds")
            
        except Exception as e:
            print(f"[DEMO ERROR] {str(e)}")
    else:
        print("\n[-] No working streams found")
        print("\nPossible issues:")
        print("  - STB might not be streaming any content")
        print("  - Network connectivity issues")
        print("  - STB might need to be on a specific channel")
        print("  - RTSP server might need activation")

if __name__ == "__main__":
    main()
