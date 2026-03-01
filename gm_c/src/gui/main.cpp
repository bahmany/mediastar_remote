#include <windows.h>
#include "gui/main_window.h"

// Entry point for Windows GUI application
int WINAPI wWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPWSTR lpCmdLine, int nCmdShow) {
    UNREFERENCED_PARAMETER(hPrevInstance);
    UNREFERENCED_PARAMETER(lpCmdLine);
    
    gui::MainWindow window;
    
    if (!window.initialize(hInstance, nCmdShow)) {
        MessageBoxW(nullptr, L"Failed to initialize application", L"Error", MB_OK | MB_ICONERROR);
        return 1;
    }
    
    return window.run();
}
