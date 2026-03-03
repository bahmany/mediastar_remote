#include <windows.h>
#include "resource.h"

// Forward declaration
extern int RunImGuiApp(HINSTANCE hInstance, int nCmdShow);

int WINAPI wWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPWSTR lpCmdLine, int nCmdShow);

int WINAPI wWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPWSTR lpCmdLine, int nCmdShow) {
    UNREFERENCED_PARAMETER(hPrevInstance);
    UNREFERENCED_PARAMETER(lpCmdLine);

    // Single instance check using mutex
    HANDLE hMutex = CreateMutexW(nullptr, TRUE, L"MediaStarRemote_SingleInstance");
    if (hMutex == nullptr) {
        return 1;
    }
    
    if (GetLastError() == ERROR_ALREADY_EXISTS) {
        // Another instance is already running
        MessageBoxW(nullptr, L"MediaStar Remote Control is already running!", L"MediaStar Remote", MB_OK | MB_ICONINFORMATION);
        CloseHandle(hMutex);
        return 0;
    }

    // Load application icon
    HICON hIcon = LoadIcon(hInstance, MAKEINTRESOURCE(IDI_APP_ICON));
    
    // Register window class with custom icon
    WNDCLASSEXW wc = { sizeof(WNDCLASSEXW), CS_CLASSDC, DefWindowProcW, 0L, 0L, 
                       GetModuleHandleW(nullptr), hIcon, nullptr, nullptr, nullptr, 
                       L"MediaStarRemote", nullptr };
    RegisterClassExW(&wc);

    // Implemented in imgui_app.cpp
    int result = RunImGuiApp(hInstance, nCmdShow);
    
    // Release mutex
    CloseHandle(hMutex);
    return result;
}
