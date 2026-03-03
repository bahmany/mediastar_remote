#!/usr/bin/env python3
"""Final working test with different RTSP parameters"""

import subprocess
import time

STB_IP = "192.168.1.2"
RTSP_URLS = [
    f"rtsp://{STB_IP}:554/stream",
    f"rtsp://{STB_IP}:554/live",
    f"rtsp://{STB_IP}:554/video",
    f"rtsp://{STB_IP}:554/h264",
]

def test_url(url, test_name):
    """Test specific RTSP URL"""
    print(f"\n[{test_name}] Testing: {url}")
    print("-" * 60)
    
    try:
        cmd = [
            'ffplay',
            '-v', 'info',
            '-t', '8',
            '-rtsp_transport', 'tcp',
            '-rtsp_flags', 'prefer_tcp',
            '-analyzeduration', '2000000',
            '-probesize', '2000000',
            url
        ]
        
        print("Starting FFplay...")
        process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True
        )
        
        video_detected = False
        audio_detected = False
        important_lines = []
        
        start_time = time.time()
        while time.time() - start_time < 12:
            if process.poll() is not None:
                break
                
            try:
                line = process.stdout.readline()
                if line:
                    line_clean = line.strip()
                    
                    # Look for important information
                    if any(keyword in line_clean for keyword in ["Video:", "Audio:", "Stream #0:", "size=", "fps=", "bitrate=", "detected", "Input #0:", "Output #0:"]):
                        important_lines.append(line_clean)
                        print(f"  {line_clean}")
                        
                        if "Video:" in line_clean or "Stream #0:0: Video:" in line_clean:
                            video_detected = True
                            print("*** VIDEO DETECTED! ***")
                        elif "Audio:" in line_clean or "Stream #0:1: Audio:" in line_clean:
                            audio_detected = True
                            print("*** AUDIO DETECTED! ***")
                    elif "Operation not permitted" in line_clean:
                        print("*** ACCESS DENIED - May need authentication ***")
                    elif "Connection refused" in line_clean:
                        print("*** CONNECTION REFUSED ***")
                        break
                        
            except:
                break
        
        # Clean up
        try:
            process.terminate()
            process.wait(timeout=3)
        except:
            process.kill()
        
        print("-" * 60)
        
        if video_detected or audio_detected:
            print("SUCCESS: This URL works!")
            return True, important_lines
        elif len(important_lines) > 0:
            print("PARTIAL: Server responds but no video/audio")
            return False, important_lines
        else:
            print("FAILED: No response")
            return False, []
            
    except Exception as e:
        print(f"ERROR: {e}")
        return False, []

def main():
    print("=" * 80)
    print("FINAL WORKING TEST - Multiple RTSP URLs")
    print("=" * 80)
    print("Testing different RTSP URLs to find the working one")
    print("Make sure DVB to IPTV is enabled and a channel is playing!")
    print("=" * 80)
    
    working_urls = []
    all_results = []
    
    for i, url in enumerate(RTSP_URLS, 1):
        success, details = test_url(url, f"TEST {i}")
        all_results.append((url, success, details))
        
        if success:
            working_urls.append(url)
        
        # Wait between tests
        if i < len(RTSP_URLS):
            print("\nWaiting 15 seconds before next test...")
            for j in range(15, 0, -1):
                print(f"\r  Waiting... {j:2d}s  ", end='', flush=True)
                time.sleep(1)
            print("\n")
    
    # Final summary
    print("\n" + "=" * 80)
    print("FINAL SUMMARY")
    print("=" * 80)
    
    for url, success, details in all_results:
        status = "[+] WORKING" if success else "[-] FAILED"
        print(f"{url:40s}: {status}")
    
    print("\n" + "=" * 80)
    
    if working_urls:
        print("SUCCESS! DVB to IPTV is working!")
        print("=" * 80)
        print(f"\nWorking URL(s):")
        for url in working_urls:
            print(f"  {url}")
        
        print(f"\nYou can watch the stream:")
        print(f"  FFplay: ffplay -rtsp_transport tcp {working_urls[0]}")
        print(f"  VLC:    {working_urls[0]}")
        
        print(f"\nStream details:")
        for url, success, details in all_results:
            if success and details:
                print(f"\n  {url}:")
                for line in details[:5]:  # Show first 5 important lines
                    print(f"    {line}")
    else:
        print("DVB to IPTV needs configuration")
        print("=" * 80)
        print("\nWhat to check:")
        print("1. DVB to IPTV enabled in STB menu?")
        print("2. Channel selected for streaming?")
        print("3. Channel actually playing?")
        print("4. Try different channels")
        print("5. Check DVB to IPTV settings (codec, bitrate, etc.)")
        print("6. Restart STB after enabling DVB to IPTV")
        
        print("\nAlternative solutions:")
        print("- HDMI capture card ($20-50)")
        print("- HDMI to IP encoder ($50-200)")

if __name__ == "__main__":
    main()
