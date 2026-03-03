#!/usr/bin/env python3
"""Automatic DVB to IPTV stream test"""

import subprocess
import time

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
            '-t', '12',
            '-rtsp_transport', 'tcp',
            '-analyzeduration', '3000000',
            RTSP_URL
        ]
        
        print("Running FFplay for 12 seconds...")
        print("Looking for video/audio streams...")
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
        important_lines = []
        
        start_time = time.time()
        while time.time() - start_time < 15:
            if process.poll() is not None:
                break
                
            try:
                line = process.stdout.readline()
                if line:
                    line_clean = line.strip()
                    
                    # Store important lines
                    if any(keyword in line_clean for keyword in ["Video:", "Audio:", "Stream #0:", "size=", "fps=", "bitrate=", "detected"]):
                        important_lines.append(line_clean)
                        print(f"  {line_clean}")
                        
                        if "Video:" in line_clean or "Stream #0:0: Video:" in line_clean:
                            video_found = True
                        elif "Audio:" in line_clean or "Stream #0:1: Audio:" in line_clean:
                            audio_found = True
                        elif "size=" in line_clean and "fps=" in line_clean:
                            print("*** VIDEO PARAMETERS DETECTED! ***")
                        
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
            print("SUCCESS: DVB to IPTV is WORKING!")
            print("\nDetected streams:")
            for line in important_lines:
                print(f"  {line}")
            return True
        else:
            print("FAILED: No video/audio detected")
            return False
            
    except Exception as e:
        print(f"Error: {e}")
        return False

def main():
    print("=" * 70)
    print("DVB to IPTV Automatic Test")
    print("=" * 70)
    print()
    print("This test checks if DVB to IPTV is working on your MediaStar 4030")
    print()
    print("PREREQUISITES:")
    print("1. DVB to IPTV should be enabled in STB menu")
    print("2. A channel should be selected for streaming")
    print("3. A channel should be actively playing")
    print()
    print("Starting test in 3 seconds...")
    time.sleep(3)
    
    print("=" * 70)
    
    success = test_stream()
    
    print("\n" + "=" * 70)
    print("FINAL RESULT")
    print("=" * 70)
    
    if success:
        print("DVB to IPTV is WORKING!")
        print()
        print("You can now watch the stream:")
        print(f"  VLC:    {RTSP_URL}")
        print(f"  FFplay: ffplay -rtsp_transport tcp {RTSP_URL}")
        print(f"  MPV:    mpv {RTSP_URL}")
        print()
        print("The stream should show whatever channel is playing on the STB!")
    else:
        print("DVB to IPTV is NOT working yet")
        print()
        print("What to check:")
        print("1. Is DVB to IPTV enabled in STB menu?")
        print("2. Is a channel selected for streaming?")
        print("3. Is the channel actually playing?")
        print("4. Try different channels")
        print("5. Check satellite signal strength")
        print("6. Try restarting STB after enabling DVB to IPTV")
        print()
        print("Alternative solutions:")
        print("- HDMI capture card ($20-50)")
        print("- HDMI to IP encoder ($50-200)")

if __name__ == "__main__":
    main()
