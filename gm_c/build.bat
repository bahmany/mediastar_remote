@echo off
setlocal enabledelayedexpansion

echo === Building gm_c with MSYS2 UCRT64 ===

REM Add UCRT64 toolchain to PATH
set "PATH=C:\msys64\ucrt64\bin;%PATH%"

REM Verify tools exist
where cmake >nul 2>&1
if errorlevel 1 (
    echo ERROR: cmake not found on PATH. Install mingw-w64-ucrt-x86_64-cmake
    exit /b 1
)

where ninja >nul 2>&1
if errorlevel 1 (
    echo ERROR: ninja not found on PATH. Install mingw-w64-ucrt-x86_64-ninja
    exit /b 1
)

where gcc >nul 2>&1
if errorlevel 1 (
    echo ERROR: gcc not found on PATH. Install mingw-w64-ucrt-x86_64-gcc
    exit /b 1
)

echo Tools found.

REM Clean previous build if requested
if /i "%1"=="clean" (
    echo Cleaning build_ucrt64 directory...
    if exist build_ucrt64 rmdir /s /q build_ucrt64
)

REM Configure
echo Configuring with CMake...
cmake -S . -B build_ucrt64 -G Ninja ^
  -DCMAKE_MAKE_PROGRAM="C:/msys64/ucrt64/bin/ninja.exe" ^
  -DCMAKE_C_COMPILER="C:/msys64/ucrt64/bin/gcc.exe" ^
  -DCMAKE_CXX_COMPILER="C:/msys64/ucrt64/bin/g++.exe"

if errorlevel 1 (
    echo ERROR: CMake configuration failed.
    exit /b 1
)

REM Build
echo Building...
cmake --build build_ucrt64 -j

if errorlevel 1 (
    echo ERROR: Build failed.
    exit /b 1
)

echo.
echo === Build succeeded ===
echo Executable: build_ucrt64\gmscreen_cli.exe
echo.
echo To run:
echo   build_ucrt64\gmscreen_cli.exe --ip 192.168.1.100 --port 20000
echo.
pause
