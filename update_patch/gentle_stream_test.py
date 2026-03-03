#!/usr/bin/env python3
"""Gentle streaming test with long delays to prevent STB crashes"""

import subprocess
import time
import sys

STB_IP = "192.168.1.2"
RTSP_URL = f"rtsp://{STB_IP}:554/stream"

def wait_with_countdown(seconds, message="Waiting"):
    """Wait with countdown display"""
    for i in range(seconds, 0, -1):
        sys.stdout.write(f"\r{message}... {i:2d}s  ")
        sys.stdout.flush()
        time.sleep(1)
    print(f"\r{message}... DONE!          ")

def test_gentle_ffplay():
    """Test with very gentle FFplay settings"""
    print("\n[TEST 1] Gentle FFplay Test (5 seconds)")
    print("-" * 50)
    
    try:
        cmd = [
            'ffplay',
            '-v', 'error',  # Minimal output
            '-t', '5',      # Only 5 seconds
            '-rtsp_transport', 'tcp',
            '-rtsp_flags', 'prefer_tcp',
            '-timeout', '3000000',  # 3 second timeout
            '-analyzeduration', '1000000',  # 1 second analysis
            RTSP_URL
        ]
        
        print("Starting FFplay (very gentle)...")
        process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        
        # Wait with timeout
        try:
            stdout, stderr = process.communicate(timeout=10)
            
            if process.returncode == 0:
                print("[+] FFplay completed successfully!")
                if stderr and len(stderr) < 200:
                    print(f"    Output: {stderr.strip()}")
                return True
            else:
                print(f"[-] FFplay failed (code {process.returncode})")
                if stderr and len(stderr) < 200:
                    print(f"    Error: {stderr.strip()}")
                return False
                
        except subprocess.TimeoutExpired:
            process.kill()
            print("[!] FFplay timed out (killed)")
            return False
            
    except Exception as e:
        print(f"[ERROR] {e}")
        return False

def test_ffprobe_gentle():
    """Gentle FFprobe test"""
    print("\n[TEST 2] Gentle FFprobe Test")
    print("-" * 50)
    
    try:
        cmd = [
            'ffprobe',
            '-v', 'error',
            '-show_format',
            '-t', '3',  # Only probe 3 seconds
            '-rtsp_transport', 'tcp',
            '-analyzeduration', '1000000',  # 1 second
            RTSP_URL
        ]
        
        print("Starting FFprobe (gentle)...")
        process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        
        try:
            stdout, stderr = process.communicate(timeout=8)
            
            if process.returncode == 0 and stdout:
                print("[+] FFprobe successful!")
                if "format=" in stdout:
                    print("    Stream format detected")
                return True
            else:
                print("[-] FFprobe failed")
                return False
                
        except subprocess.TimeoutExpired:
            process.kill()
            print("[!] FFprobe timed out")
            return False
            
    except Exception as e:
        print(f"[ERROR] {e}")
        return False

def test_vlc_simple():
    """Simple VLC test (if available)"""
    print("\n[TEST 3] VLC Test (if installed)")
    print("-" * 50)
    
    try:
        cmd = [
            'vlc',
            '--intf', 'dummy',
            '--play-and-exit',
            '--rtsp-timeout', '3000',
            '--run-time', '5',
            RTSP_URL
        ]
        
        print("Testing with VLC...")
        process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        
        try:
            stdout, stderr = process.communicate(timeout=10)
            
            if process.returncode == 0:
                print("[+] VLC played successfully!")
                return True
            else:
                print("[-] VLC failed")
                return False
                
        except subprocess.TimeoutExpired:
            process.kill()
            print("[!] VLC timed out")
            return False
            
    except FileNotFoundError:
        print("[-] VLC not found")
        return False
    except Exception as e:
        print(f"[ERROR] {e}")
        return False

def test_socket_connection():
    """Simple socket connection test"""
    print("\n[TEST 4] Socket Connection Test")
    print("-" * 50)
    
    try:
        import socket
        
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(5)
        
        print(f"Connecting to {STB_IP}:554...")
        result = sock.connect_ex((STB_IP, 554))
        
        if result == 0:
            print("[+] RTSP port is open")
            
            # Try simple RTSP OPTIONS
            request = f"OPTIONS rtsp://{STB_IP}:554/stream RTSP/1.0\r\nCSeq: 1\r\n\r\n"
            sock.send(request.encode())
            
            try:
                response = sock.recv(1024).decode('latin1', errors='ignore')
                if "200 OK" in response:
                    print("[+] RTSP server responding")
                    sock.close()
                    return True
                else:
                    print("[-] RTSP server not responding properly")
                    return False
            except:
                print("[-] No RTSP response")
                return False
        else:
            print("[-] RTSP port closed")
            return False
            
    except Exception as e:
        print(f"[ERROR] {e}")
        return False
    finally:
        try:
            sock.close()
        except:
            pass

def main():
    print("=" * 70)
    print("GENTLE STREAMING TEST")
    print("=" * 70)
    print("This test runs slowly to prevent STB crashes")
    print("Each test has 30 second delays between them")
    print()
    print("Make sure DVB to IPTV is enabled in STB menu!")
    print("=" * 70)
    
    results = []
    
    # Test 1: Socket connection
    if test_socket_connection():
        results.append(("Socket", True))
    else:
        results.append(("Socket", False))
    
    wait_with_countdown(30, "Waiting before next test")
    
    # Test 2: Gentle FFplay
    if test_gentle_ffplay():
        results.append(("FFplay", True))
    else:
        results.append(("FFplay", False))
    
    wait_with_countdown(30, "Waiting before next test")
    
    # Test 3: FFprobe
    if test_ffprobe_gentle():
        results.append(("FFprobe", True))
    else:
        results.append(("FFprobe", False))
    
    wait_with_countdown(30, "Waiting before next test")
    
    # Test 4: VLC (if available)
    if test_vlc_simple():
        results.append(("VLC", True))
    else:
        results.append(("VLC", False))
    
    # Final results
    print("\n" + "=" * 70)
    print("FINAL RESULTS")
    print("=" * 70)
    
    for test_name, success in results:
        status = "[+] WORKING" if success else "[-] FAILED"
        print(f"{test_name:10s}: {status}")
    
    # Overall assessment
    video_tests = [name for name, success in results if success and name in ["FFplay", "FFprobe", "VLC"]]
    
    print("\n" + "=" * 70)
    if video_tests:
        print("SUCCESS! DVB to IPTV is working!")
        print("=" * 70)
        print(f"Working methods: {', '.join(video_tests)}")
        print()
        print("You can watch the stream:")
        print(f"  VLC:    {RTSP_URL}")
        print(f"  FFplay: ffplay -rtsp_transport tcp {RTSP_URL}")
        print()
        print("The stream should show your STB's current channel!")
    else:
        print("DVB to IPTV still not working")
        print("=" * 70)
        print()
        print("What to check:")
        print("1. DVB to IPTV enabled in STB menu?")
        print("2. Channel selected for streaming?")
        print("3. Channel actually playing?")
        print("4. Try restarting STB after enabling DVB to IPTV")
        print("5. Check signal strength")
        print()
        print("Alternative: HDMI capture card ($20-50)")

if __name__ == "__main__":
    main()
