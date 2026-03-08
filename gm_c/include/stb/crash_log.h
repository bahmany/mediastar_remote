#pragma once
// Global crash/debug log — thread-safe, callable from any module.
#include <cstdio>
#include <ctime>
#include <mutex>

namespace stb {

inline void CrashLog(const char* msg) {
    static std::mutex mu;
    try {
        std::lock_guard<std::mutex> g(mu);
        FILE* f = nullptr;
        fopen_s(&f, "my4030_crash.log", "a");
        if (f) {
            time_t t = time(nullptr);
            struct tm tm_{}; localtime_s(&tm_, &t);
            char ts[32]; strftime(ts, sizeof(ts), "%Y-%m-%d %H:%M:%S", &tm_);
            fprintf(f, "[%s] %s\n", ts, msg);
            fclose(f);
        }
    } catch (...) {}
}

} // namespace stb
