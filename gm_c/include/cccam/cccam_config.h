#pragma once
// Persistent CCcam config — dynamic list of upstream servers with SOCKS proxy support.
// Saves/loads to JSON file next to exe.
#include <string>
#include <vector>
#include <fstream>
#include <sstream>
#include <ctime>
#include <atomic>
#ifdef _WIN32
#include <windows.h>
#endif

namespace cccam {

// ── One upstream CCcam server ─────────────────────────────────────────────────
struct UpstreamServer {
    std::string name;           // display name
    std::string host;
    int         port = 12000;
    std::string user;
    std::string pass;
    bool        enabled = true;
    // SOCKS5 proxy (empty = direct)
    std::string proxy_host;
    int         proxy_port = 0;
    std::string proxy_user;
    std::string proxy_pass;
    // Runtime status (not persisted)
    int    ping_status = 0;     // 0=unknown, 1=ok(green), -1=fail(red)
    int    ping_ms = -1;
    time_t dead_since = 0;      // when server first became dead (0=not dead)
    std::string test_detail;    // e.g. "OK(42 cards)", "TCP FAIL", "NO SEED", "AUTH FAIL"
    bool hasProxy() const { return !proxy_host.empty() && proxy_port > 0; }
    bool valid()    const { return !host.empty() && port > 0; }
};

// ── Parse C-lines from text ──────────────────────────────────────────────────
// Supports: "C: host port user pass" and "host port user pass" formats.
// Returns parsed servers. proxyHost/proxyPort applied to all if non-empty.
inline std::vector<UpstreamServer> parseCLines(const std::string& text,
    const std::string& pxHost = "", int pxPort = 0)
{
    std::vector<UpstreamServer> out;
    std::istringstream ss(text);
    std::string line;
    while (std::getline(ss, line)) {
        // trim
        while (!line.empty() && (line.front()==' '||line.front()=='\t'||line.front()=='\r'))
            line.erase(line.begin());
        while (!line.empty() && (line.back()==' '||line.back()=='\t'||line.back()=='\r'))
            line.pop_back();
        if (line.empty() || line[0] == '#') continue;
        // Strip leading "C:" or "C :"
        if (line.size() > 2 && (line[0]=='C'||line[0]=='c') && (line[1]==':'||line[1]==' ')) {
            line = line.substr(line[1]==':' ? 2 : 1);
            while (!line.empty() && line.front()==' ') line.erase(line.begin());
        }
        // Split by whitespace
        std::vector<std::string> tok;
        {
            std::istringstream ls(line);
            std::string t;
            while (ls >> t) tok.push_back(t);
        }
        if (tok.size() < 4) continue;
        UpstreamServer s;
        s.host = tok[0];
        try { s.port = std::stoi(tok[1]); } catch (...) { continue; }
        s.user = tok[2];
        s.pass = tok[3];
        s.enabled = true;
        s.name = s.host + ":" + std::to_string(s.port);
        if (!pxHost.empty() && pxPort > 0) {
            s.proxy_host = pxHost;
            s.proxy_port = pxPort;
        }
        if (s.valid()) out.push_back(std::move(s));
    }
    return out;
}

// ── Full config ───────────────────────────────────────────────────────────────
struct CccamConfig {
    int         port = 8000;
    std::string user = "a";
    std::string pass = "a";
    bool        log_ecm = true;         // log ECM/CW to disk
    std::vector<UpstreamServer> servers; // dynamic upstream list

    static std::string exeDir() {
#ifdef _WIN32
        char buf[MAX_PATH] = {};
        GetModuleFileNameA(nullptr, buf, MAX_PATH);
        std::string p(buf);
        auto pos = p.find_last_of("\\/");
        if (pos != std::string::npos) p = p.substr(0, pos + 1);
        return p;
#else
        return "";
#endif
    }
    static std::string filePath()  { return exeDir() + "gmscreen_cccam.json"; }
    static std::string ecmLogPath(){ return exeDir() + "gmscreen_ecm_log.csv"; }

    // ── Save ──────────────────────────────────────────────────────────────────
    bool save(const std::string& path = "") const {
        std::string f = path.empty() ? filePath() : path;
        try {
            std::ofstream o(f, std::ios::trunc);
            if (!o.is_open()) return false;
            o << "{\n";
            o << "\"port\":" << port << ",\n";
            o << "\"user\":\"" << esc(user) << "\",\n";
            o << "\"pass\":\"" << esc(pass) << "\",\n";
            o << "\"log_ecm\":" << (log_ecm ? "true" : "false") << ",\n";
            o << "\"servers\":[\n";
            for (size_t i = 0; i < servers.size(); i++) {
                auto& s = servers[i];
                o << "  {";
                o << "\"name\":\"" << esc(s.name) << "\",";
                o << "\"host\":\"" << esc(s.host) << "\",";
                o << "\"port\":" << s.port << ",";
                o << "\"user\":\"" << esc(s.user) << "\",";
                o << "\"pass\":\"" << esc(s.pass) << "\",";
                o << "\"enabled\":" << (s.enabled ? "true" : "false") << ",";
                o << "\"proxy_host\":\"" << esc(s.proxy_host) << "\",";
                o << "\"proxy_port\":" << s.proxy_port << ",";
                o << "\"proxy_user\":\"" << esc(s.proxy_user) << "\",";
                o << "\"proxy_pass\":\"" << esc(s.proxy_pass) << "\"";
                o << "}" << (i + 1 < servers.size() ? ",\n" : "\n");
            }
            o << "]\n}\n";
            o.close();
            return true;
        } catch (...) { return false; }
    }

    // ── Load ──────────────────────────────────────────────────────────────────
    bool load(const std::string& path = "") {
        std::string f = path.empty() ? filePath() : path;
        try {
            std::ifstream in(f);
            if (!in.is_open()) return false;
            std::string c((std::istreambuf_iterator<char>(in)),
                           std::istreambuf_iterator<char>());
            in.close();
            port    = getInt(c, "port", port);
            user    = getStr(c, "user", user);
            pass    = getStr(c, "pass", pass);
            log_ecm = getBool(c, "log_ecm", log_ecm);
            // Parse servers array
            servers.clear();
            auto arrStart = c.find("\"servers\"");
            if (arrStart == std::string::npos) return true;
            arrStart = c.find('[', arrStart);
            if (arrStart == std::string::npos) return true;
            // Find each {...} object within the array, respecting strings
            int brace = 0; size_t objStart = 0; bool inStr = false;
            for (size_t i = arrStart + 1; i < c.size(); i++) {
                char ch = c[i];
                if (inStr) {
                    if (ch == '\\' && i+1 < c.size()) { i++; continue; }
                    if (ch == '"') inStr = false;
                    continue;
                }
                if (ch == '"') { inStr = true; continue; }
                if (ch == '{') { if (brace == 0) objStart = i; brace++; }
                else if (ch == '}') {
                    brace--;
                    if (brace == 0) {
                        std::string obj = c.substr(objStart, i - objStart + 1);
                        UpstreamServer s;
                        s.name       = getStr(obj, "name", "");
                        s.host       = getStr(obj, "host", "");
                        s.port       = getInt(obj, "port", 12000);
                        s.user       = getStr(obj, "user", "");
                        s.pass       = getStr(obj, "pass", "");
                        s.enabled    = getBool(obj, "enabled", true);
                        s.proxy_host = getStr(obj, "proxy_host", "");
                        s.proxy_port = getInt(obj, "proxy_port", 0);
                        s.proxy_user = getStr(obj, "proxy_user", "");
                        s.proxy_pass = getStr(obj, "proxy_pass", "");
                        if (s.valid()) servers.push_back(std::move(s));
                    }
                }
                else if (ch == ']' && brace == 0) break;
            }
            return true;
        } catch (...) { return false; }
    }

    // ── JSON helpers (public for reuse) ───────────────────────────────────────
    static std::string esc(const std::string& s) {
        std::string r;
        for (char ch : s) {
            if (ch == '"') r += "\\\"";
            else if (ch == '\\') r += "\\\\";
            else r += ch;
        }
        return r;
    }
    static std::string getStr(const std::string& json, const std::string& key, const std::string& def) {
        std::string needle = "\"" + key + "\"";
        auto p = json.find(needle);
        if (p == std::string::npos) return def;
        p = json.find(':', p + needle.size());
        if (p == std::string::npos) return def;
        p = json.find('"', p + 1);
        if (p == std::string::npos) return def;
        p++;
        std::string result;
        for (size_t i = p; i < json.size(); i++) {
            if (json[i] == '\\' && i + 1 < json.size()) { result += json[++i]; continue; }
            if (json[i] == '"') break;
            result += json[i];
        }
        return result;
    }
    static int getInt(const std::string& json, const std::string& key, int def) {
        std::string needle = "\"" + key + "\"";
        auto p = json.find(needle);
        if (p == std::string::npos) return def;
        p = json.find(':', p + needle.size());
        if (p == std::string::npos) return def;
        p++;
        while (p < json.size() && (json[p] == ' ' || json[p] == '\t')) p++;
        try { return std::stoi(json.substr(p)); } catch (...) { return def; }
    }
    static bool getBool(const std::string& json, const std::string& key, bool def) {
        std::string needle = "\"" + key + "\"";
        auto p = json.find(needle);
        if (p == std::string::npos) return def;
        p = json.find(':', p + needle.size());
        if (p == std::string::npos) return def;
        p++;
        while (p < json.size() && (json[p] == ' ' || json[p] == '\t')) p++;
        if (p < json.size() && json[p] == 't') return true;
        if (p < json.size() && json[p] == 'f') return false;
        return def;
    }
};

} // namespace cccam
