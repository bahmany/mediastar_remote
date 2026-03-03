cmake_minimum_required(VERSION 3.20)

project(gm_c VERSION 2.0.0 LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(Threads REQUIRED)

# Find static zlib instead of DLL
set(CMAKE_FIND_LIBRARY_SUFFIXES ".a")
find_package(ZLIB REQUIRED)

# Include directories
include_directories(${CMAKE_SOURCE_DIR}/include)

# Dear ImGui (vendored)
set(IMGUI_DIR ${CMAKE_SOURCE_DIR}/third_party/imgui)
if(EXISTS ${IMGUI_DIR}/imgui.h)
    add_library(imgui STATIC
        ${IMGUI_DIR}/imgui.cpp
        ${IMGUI_DIR}/imgui_draw.cpp
        ${IMGUI_DIR}/imgui_tables.cpp
        ${IMGUI_DIR}/imgui_widgets.cpp
        ${IMGUI_DIR}/imgui_demo.cpp
        ${IMGUI_DIR}/backends/imgui_impl_win32.cpp
        ${IMGUI_DIR}/backends/imgui_impl_dx11.cpp
    )
    target_include_directories(imgui PUBLIC
        ${IMGUI_DIR}
        ${IMGUI_DIR}/backends
    )
endif()

# STB Core Library Sources
set(STB_CORE_SOURCES
    src/stb/crypto.cpp
    src/stb/compression.cpp
    src/stb/models.cpp
    src/stb/serialization.cpp
    src/stb/network.cpp
    src/stb/message_processor.cpp
    src/stb/favorites_manager.cpp
    src/stb/stb_client.cpp
)

# Create STB core library
add_library(stb_core STATIC ${STB_CORE_SOURCES})

target_link_libraries(stb_core
    PUBLIC
        Threads::Threads
        ${ZLIB_LIBRARIES}
)

if(WIN32)
    target_link_libraries(stb_core PUBLIC ws2_32)
endif()

# Console CLI executable
add_executable(gmscreen_cli
    src/console/main.cpp
)

target_link_libraries(gmscreen_cli PRIVATE stb_core)

# Windows GUI executable
if(WIN32)
    # Enable Unicode for Windows API
    add_definitions(-DUNICODE -D_UNICODE)
    
    # Static link C++ runtime and dependencies for GUI
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -static-libgcc -static-libstdc++ -static")
    
    add_executable(gmscreen_win32 WIN32
        src/gui/main.cpp
        src/gui/main_window.cpp
        src/gui/channel_list_view.cpp
        src/gui/remote_control.cpp
        src/gui/device_discovery_dialog.cpp
    )
    
    # GUI uses wWinMain (Unicode)
    target_link_options(gmscreen_win32 PRIVATE -static -municode)
    
    target_link_libraries(gmscreen_win32 PRIVATE
        stb_core
        comctl32
        gdi32
        user32
        dwmapi
    )

    if(TARGET imgui)
        add_executable(gmscreen_imgui_win32 WIN32
            src/gui/main_imgui.cpp
            src/gui/imgui_app.cpp
        )

        target_link_options(gmscreen_imgui_win32 PRIVATE -static -municode)

        target_link_libraries(gmscreen_imgui_win32 PRIVATE
            stb_core
            imgui
            d3d11
            dxgi
            d3dcompiler_47
            imm32
            user32
            gdi32
            dwmapi
        )
    endif()
endif()

# Install targets
install(TARGETS stb_core gmscreen_cli
    ARCHIVE DESTINATION lib
    RUNTIME DESTINATION bin
)

install(DIRECTORY include/ DESTINATION include)
