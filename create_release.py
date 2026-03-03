#!/usr/bin/env python3
"""
GitHub Release Creation Script
Creates a GitHub release for MediaStar 4030 Remote Control
"""

import os
import json
import requests
from pathlib import Path

def create_github_release():
    """Create GitHub release using API"""
    
    # Configuration
    GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")  # Set this environment variable
    REPO = "bahmany/mediastar_remote"
    VERSION = "v2.0.0"
    
    if not GITHUB_TOKEN:
        print("Error: GITHUB_TOKEN environment variable not set")
        print("Get a token from: https://github.com/settings/tokens")
        print("Set it with: set GITHUB_TOKEN=your_token_here")
        return False
    
    # Read release notes
    release_notes_path = Path("RELEASE_NOTES.md")
    if not release_notes_path.exists():
        print("Error: RELEASE_NOTES.md not found")
        return False
    
    with open(release_notes_path, 'r', encoding='utf-8') as f:
        release_notes = f.read()
    
    # Create release
    url = f"https://api.github.com/repos/{REPO}/releases"
    headers = {
        "Authorization": f"token {GITHUB_TOKEN}",
        "Accept": "application/vnd.github.v3+json"
    }
    
    data = {
        "tag_name": VERSION,
        "name": f"MediaStar 4030 Remote Control {VERSION}",
        "body": release_notes,
        "draft": False,
        "prerelease": False
    }
    
    print(f"Creating release {VERSION}...")
    response = requests.post(url, headers=headers, json=data)
    
    if response.status_code == 201:
        release_data = response.json()
        print(f"✅ Release created successfully!")
        print(f"📎 URL: {release_data['html_url']}")
        
        # Upload executable
        exe_path = Path(f"release/{VERSION}/gmscreen.exe")
        if exe_path.exists():
            upload_url = release_data['upload_url'].replace('{?name,label}', '')
            
            with open(exe_path, 'rb') as f:
                exe_data = f.read()
            
            headers_upload = {
                "Authorization": f"token {GITHUB_TOKEN}",
                "Content-Type": "application/octet-stream"
            }
            
            print(f"Uploading gmscreen.exe...")
            upload_response = requests.post(
                upload_url,
                headers=headers_upload,
                data=exe_data,
                params={"name": "gmscreen.exe"}
            )
            
            if upload_response.status_code == 201:
                print(f"✅ Executable uploaded successfully!")
                download_url = upload_response.json()['browser_download_url']
                print(f"📥 Download URL: {download_url}")
            else:
                print(f"❌ Failed to upload executable: {upload_response.status_code}")
                print(upload_response.text)
        
        return True
    else:
        print(f"❌ Failed to create release: {response.status_code}")
        print(response.text)
        return False

def main():
    print("GitHub Release Creation Script")
    print("=" * 40)
    
    if not create_github_release():
        print("\n📝 Manual Release Instructions:")
        print("1. Go to: https://github.com/bahmany/mediastar_remote/releases/new")
        print("2. Tag: v2.0.0")
        print("3. Title: MediaStar 4030 Remote Control v2.0.0")
        print("4. Copy content from RELEASE_NOTES.md")
        print("5. Upload gmscreen.exe from release/v2.0.0/")
        print("6. Publish release")

if __name__ == "__main__":
    main()
