#!/usr/bin/env python3
"""Simple test for DVB to IPTV functionality"""

import subprocess
import time
import sys

STB_IP = "192.168.1.2"
RTSP_URL = f"rtsp://{STB_IP}:554/stream"

def test_stream():
    """Test RTSP stream with FFplay"""
    print("Testing RTSP stream...")
    print(f"URL: {RTSP_URL}")
    print()
    
    try:
        cmd = [
            'ffplay',
            '-v', 'info',
            '-t', '15',
            '-rtsp_transport', 'tcp',
            '-analyzeduration', '5000000',
            RTSP_URL
        ]
        
        print("Running FFplay for 15 seconds...")
        print("Watch for video/audio detection messages...")
        print("-" * 60)
        
        process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            universal_newlines=True
        )
        
        video_found = False
        audio_found = False
        start_time = time.time()
        
        while time.time() - start_time < 20:
            if process.poll() is not None:
                break
                
            try:
                line = process.stdout.readline()
                if line:
                    line_clean = line.strip()
                    print(line_clean)
                    
                    if "Video:" in line_clean or "Stream #0:0: Video:" in line_clean:
                        video_found = True
                        print("*** VIDEO DETECTED! ***")
                    elif "Audio:" in line_clean or "Stream #0:1: Audio:" in line_clean:
                        audio_found = True
                        print("*** AUDIO DETECTED! ***")
                    elif "size=" in line_clean and "fps=" in line_clean:
                        print("*** VIDEO PARAMETERS FOUND! ***")
                        
            except:
                break
        
        # Clean up
        try:
            process.terminate()
            process.wait(timeout=3)
        except:
            process.kill()
        
        print("-" * 60)
        if video_found or audio_found:
            print("SUCCESS: Stream is working!")
            return True
        else:
            print("FAILED: No video/audio detected")
            return False
            
    except Exception as e:
        print(f"Error: {e}")
        return False

def main():
    print("=" * 70)
    print("DVB to IPTV Stream Test")
    print("=" * 70)
    print()
    print("BEFORE RUNNING THIS TEST:")
    print("1. Go to your MediaStar 4030 menu")
    print("2. Find 'DVB to IPTV' option")
    print("3. Enable it")
    print("4. Select a channel to stream")
    print("5. Save settings")
    print("6. Make sure a channel is playing")
    print()
    print("Press Enter to start the test...")
    input()
    
    print("=" * 70)
    
    if test_stream():
        print()
        print("=" * 70)
        print("RESULT: DVB to IPTV is WORKING!")
        print("=" * 70)
        print()
        print("You can now watch the stream using:")
        print(f"  VLC:        {RTSP_URL}")
        print(f"  FFplay:     ffplay -rtsp_transport tcp {RTSP_URL}")
        print(f"  MPV:        mpv {RTSP_URL}")
        print()
        print("Tips:")
        print("- Use TCP transport for better reliability")
        print("- 1-3 seconds delay is normal")
        print("- Stream quality depends on source signal")
    else:
        print()
        print("=" * 70)
        print("RESULT: DVB to IPTV not working yet")
        print("=" * 70)
        print()
        print("Troubleshooting:")
        print("1. Make sure DVB to IPTV is enabled in STB menu")
        print("2. Try different channels")
        print("3. Check satellite signal strength")
        print("4. Restart the STB after enabling DVB to IPTV")
        print("5. Check if there are additional settings in DVB to IPTV menu")

if __name__ == "__main__":
    main()
