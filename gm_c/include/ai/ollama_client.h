#pragma once
// Ollama AI client for local LLM integration
// Uses HTTP REST API to communicate with Ollama server

#include <string>
#include <vector>
#include <functional>
#include <thread>
#include <atomic>
#include <mutex>

#ifdef _WIN32
#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif
#include <winsock2.h>
#include <ws2tcpip.h>
#pragma comment(lib, "ws2_32.lib")
#endif

namespace ai {

class OllamaClient {
public:
    std::string host = "127.0.0.1";
    int port = 11434;
    std::string model = "qwen3:14b";
    
    std::atomic<bool> busy{false};
    std::string lastResponse;
    std::string lastError;
    
    // Callback for streaming responses
    std::function<void(const std::string&)> onChunk;
    std::function<void(const std::string&)> onComplete;
    std::function<void(const std::string&)> onError;

    bool isAvailable() {
        // Quick check if Ollama is running
        SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock == INVALID_SOCKET) return false;
        
        sockaddr_in addr{};
        addr.sin_family = AF_INET;
        addr.sin_port = htons((uint16_t)port);
        inet_pton(AF_INET, host.c_str(), &addr.sin_addr);
        
        // Set short timeout
        DWORD timeout = 1000;
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&timeout, sizeof(timeout));
        setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, (char*)&timeout, sizeof(timeout));
        
        bool ok = (::connect(sock, (sockaddr*)&addr, sizeof(addr)) == 0);
        closesocket(sock);
        return ok;
    }

    // Synchronous generate (blocks until complete)
    std::string generate(const std::string& prompt, int timeoutSec = 60) {
        if (busy.load()) return "[AI busy]";
        busy = true;
        lastResponse.clear();
        lastError.clear();
        
        SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock == INVALID_SOCKET) {
            busy = false;
            lastError = "Socket creation failed";
            return "";
        }
        
        sockaddr_in addr{};
        addr.sin_family = AF_INET;
        addr.sin_port = htons((uint16_t)port);
        inet_pton(AF_INET, host.c_str(), &addr.sin_addr);
        
        DWORD timeout = timeoutSec * 1000;
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&timeout, sizeof(timeout));
        setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, (char*)&timeout, sizeof(timeout));
        
        if (::connect(sock, (sockaddr*)&addr, sizeof(addr)) != 0) {
            closesocket(sock);
            busy = false;
            lastError = "Connection failed - is Ollama running?";
            return "";
        }
        
        // Build JSON body (escape special chars in prompt)
        std::string escaped = escapeJson(prompt);
        std::string body = "{\"model\":\"" + model + "\",\"prompt\":\"" + escaped + "\",\"stream\":false}";
        
        // HTTP POST request
        std::string req = "POST /api/generate HTTP/1.1\r\n"
                          "Host: " + host + ":" + std::to_string(port) + "\r\n"
                          "Content-Type: application/json\r\n"
                          "Content-Length: " + std::to_string(body.size()) + "\r\n"
                          "Connection: close\r\n\r\n" + body;
        
        if (send(sock, req.c_str(), (int)req.size(), 0) <= 0) {
            closesocket(sock);
            busy = false;
            lastError = "Send failed";
            return "";
        }
        
        // Read response
        std::string response;
        char buf[4096];
        int n;
        while ((n = recv(sock, buf, sizeof(buf) - 1, 0)) > 0) {
            buf[n] = 0;
            response += buf;
        }
        closesocket(sock);
        busy = false;
        
        // Parse JSON response to extract "response" field
        size_t pos = response.find("\"response\":\"");
        if (pos != std::string::npos) {
            pos += 12;
            std::string result;
            for (size_t i = pos; i < response.size(); i++) {
                if (response[i] == '\\' && i + 1 < response.size()) {
                    char next = response[i + 1];
                    if (next == 'n') { result += '\n'; i++; }
                    else if (next == 't') { result += '\t'; i++; }
                    else if (next == '"') { result += '"'; i++; }
                    else if (next == '\\') { result += '\\'; i++; }
                    else result += response[i];
                } else if (response[i] == '"') {
                    break;
                } else {
                    result += response[i];
                }
            }
            lastResponse = result;
            return result;
        }
        
        lastError = "Failed to parse response";
        return "";
    }

    // Async generate (non-blocking)
    void generateAsync(const std::string& prompt, int timeoutSec = 120) {
        std::thread([this, prompt, timeoutSec]() {
            std::string result = generate(prompt, timeoutSec);
            if (!result.empty() && onComplete) {
                onComplete(result);
            } else if (!lastError.empty() && onError) {
                onError(lastError);
            }
        }).detach();
    }

    // Chat-style prompt for CCcam analysis
    std::string analyzeCCcam(const std::string& logs, const std::string& question) {
        std::string prompt = 
            "You are an expert in satellite TV card sharing protocols, specifically CCcam.\n"
            "Analyze the following CCcam server logs and answer the question.\n"
            "Be concise and technical. Focus on actionable solutions.\n\n"
            "=== LOGS ===\n" + logs + "\n\n"
            "=== QUESTION ===\n" + question + "\n\n"
            "=== ANSWER ===\n";
        return generate(prompt, 90);
    }

    // Find working CCcam servers (AI-assisted search suggestions)
    std::string suggestServers() {
        std::string prompt = 
            "/no_think\n"
            "List 5 currently working free CCcam test servers with host, port, username, password.\n"
            "Format each as: HOST PORT USER PASS\n"
            "Only list servers you know are commonly available for testing.\n"
            "Do not include explanations, just the server list.";
        return generate(prompt, 60);
    }

private:
    static std::string escapeJson(const std::string& s) {
        std::string r;
        r.reserve(s.size() * 2);
        for (char c : s) {
            switch (c) {
                case '"': r += "\\\""; break;
                case '\\': r += "\\\\"; break;
                case '\n': r += "\\n"; break;
                case '\r': r += "\\r"; break;
                case '\t': r += "\\t"; break;
                default: r += c;
            }
        }
        return r;
    }
};

} // namespace ai
