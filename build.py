#!/usr/bin/env python3
"""
MediaStar 4030 Remote Control Build Script
Automates the build process for Windows releases
"""

import os
import sys
import subprocess
import shutil
import json
from datetime import datetime
from pathlib import Path

def run_command(cmd, cwd=None, check=True):
    """Run a command and return the result"""
    print(f"Running: {cmd}")
    result = subprocess.run(cmd, shell=True, cwd=cwd, capture_output=True, text=True)
    if check and result.returncode != 0:
        print(f"Error: {result.stderr}")
        sys.exit(1)
    return result

def get_version():
    """Get version from CMakeLists.txt"""
    cmake_file = Path("gm_c/CMakeLists.txt")
    if not cmake_file.exists():
        return "2.0.0"
    
    with open(cmake_file) as f:
        for line in f:
            if "project(gm_c VERSION" in line:
                version = line.split()[2].strip(")")
                return version
    return "2.0.0"

def main():
    print("MediaStar 4030 Remote Control Build Script")
    print("=" * 50)
    
    # Get version
    version = get_version()
    print(f"Version: {version}")
    
    # Check MSYS2
    msys2_path = r"C:\msys64\usr\bin\bash.exe"
    if not os.path.exists(msys2_path):
        print("Error: MSYS2 not found at", msys2_path)
        sys.exit(1)
    
    # Clean previous build
    build_dir = Path("gm_c/build_ucrt64_ninja2")
    if build_dir.exists():
        print("Cleaning previous build...")
        # Try to rename locked executable first
        old_exe = build_dir / "gmscreen_imgui_win32.exe"
        if old_exe.exists():
            try:
                old_exe.rename(build_dir / "gmscreen_imgui_win32_old.exe")
            except:
                pass
        try:
            shutil.rmtree(build_dir)
        except:
            print("Warning: Could not clean build directory, continuing...")
    
    # Create build directory
    build_dir.mkdir(parents=True, exist_ok=True)
    
    # Configure with CMake
    print("\nConfiguring with CMake...")
    cmake_cmd = f'"{msys2_path}" -c "cd /d/projects/gmscreen/gm_c/build_ucrt64_ninja2 && PATH=/ucrt64/bin:$PATH /ucrt64/bin/cmake.exe -G Ninja -DCMAKE_CXX_COMPILER=/ucrt64/bin/g++.exe -DCMAKE_C_COMPILER=/ucrt64/bin/gcc.exe .."'
    run_command(cmake_cmd)
    
    # Build
    print("\nBuilding...")
    build_cmd = f'"{msys2_path}" -c "cd /d/projects/gmscreen/gm_c/build_ucrt64_ninja2 && PATH=/ucrt64/bin:$PATH /ucrt64/bin/ninja.exe -j4"'
    run_command(build_cmd)
    
    # Check executable
    exe_path = build_dir / "gmscreen_imgui_win32.exe"
    if not exe_path.exists():
        print("Error: Build failed - executable not found")
        sys.exit(1)
    
    # Create release directory
    release_dir = Path(f"release/v{version}")
    release_dir.mkdir(parents=True, exist_ok=True)
    
    # Copy executable
    release_exe = release_dir / "gmscreen.exe"
    print(f"\nCopying executable to {release_exe}")
    shutil.copy2(exe_path, release_exe)
    
    # Copy documentation
    docs_dir = release_dir / "docs"
    docs_dir.mkdir(exist_ok=True)
    
    print("Copying documentation...")
    shutil.copy2("README.md", release_dir)
    shutil.copy2("CHANGELOG.md", release_dir)
    shutil.copy2("RELEASE_NOTES.md", release_dir)
    shutil.copy2("CONTRIBUTING.md", release_dir)
    shutil.copy2("SECURITY.md", release_dir)
    
    # Copy docs folder
    if Path("docs").exists():
        for doc_file in Path("docs").glob("*.md"):
            shutil.copy2(doc_file, docs_dir / doc_file.name)
    
    # Create version info
    version_info = {
        "version": version,
        "build_date": datetime.now().isoformat(),
        "git_commit": "unknown",
        "build_type": "Release",
        "platform": "Windows x64",
        "dependencies": [
            "MSYS2 UCRT64",
            "ImGui",
            "Winsock2",
            "Static linking (no DLLs)"
        ]
    }
    
    # Try to get git commit
    try:
        git_result = run_command("git rev-parse --short HEAD", check=False)
        if git_result.returncode == 0:
            version_info["git_commit"] = git_result.stdout.strip()
    except:
        pass
    
    with open(release_dir / "version.json", "w") as f:
        json.dump(version_info, f, indent=2)
    
    # Calculate file size
    file_size = release_exe.stat().st_size / (1024 * 1024)  # MB
    print(f"\nBuild completed successfully!")
    print(f"Executable: {release_exe}")
    print(f"Size: {file_size:.1f} MB")
    print(f"Release directory: {release_dir}")
    
    # Create checksum
    print("\nGenerating checksum...")
    import hashlib
    
    def sha256_file(filename):
        sha256_hash = hashlib.sha256()
        with open(filename, "rb") as f:
            for chunk in iter(lambda: f.read(4096), b""):
                sha256_hash.update(chunk)
        return sha256_hash.hexdigest()
    
    checksum = sha256_file(release_exe)
    with open(release_dir / "checksum.txt", "w") as f:
        f.write(f"SHA256(gmscreen.exe) = {checksum}\n")
    
    print(f"SHA256: {checksum}")
    
    # Test executable
    print("\nTesting executable...")
    try:
        test_result = run_command(f'"{release_exe}" --version', check=False)
        if test_result.returncode == 0:
            print("✓ Executable runs successfully")
        else:
            print("⚠ Executable test failed")
    except Exception as e:
        print(f"⚠ Could not test executable: {e}")
    
    print("\n" + "=" * 50)
    print("Build Summary:")
    print(f"  Version: {version}")
    print(f"  Size: {file_size:.1f} MB")
    print(f"  Location: {release_dir}")
    print(f"  Checksum: {checksum[:16]}...")
    print("=" * 50)

if __name__ == "__main__":
    main()
