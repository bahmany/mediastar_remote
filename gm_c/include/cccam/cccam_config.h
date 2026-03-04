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
    const std::string& pxHost = "", int pxPort = 0,
    const std::string& pxUser = "", const std::string& pxPass = "")
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
            s.proxy_user = pxUser;
            s.proxy_pass = pxPass;
        }
        if (s.valid()) out.push_back(std::move(s));
    }
    return out;
}

// ── Smart text parser — extracts CCcam servers from messy mixed-format text ───
// Handles: C-lines, Oscam [reader] blocks, HOST/PORT/USER/PASS blocks,
// bare "host port user pass", and mixed/incomplete formats.
// Deduplicates by host:port:user.
inline std::vector<UpstreamServer> parseSmartText(const std::string& text,
    const std::string& pxHost = "", int pxPort = 0,
    const std::string& pxUser = "", const std::string& pxPass = "")
{
    std::vector<UpstreamServer> out;
    auto trim = [](std::string s) -> std::string {
        while (!s.empty() && (s.front()==' '||s.front()=='\t'||s.front()=='\r'||s.front()=='\n'))
            s.erase(s.begin());
        while (!s.empty() && (s.back()==' '||s.back()=='\t'||s.back()=='\r'||s.back()=='\n'))
            s.pop_back();
        return s;
    };
    auto toLower = [](std::string s) -> std::string {
        for (auto& c : s) c = (char)tolower((unsigned char)c);
        return s;
    };
    auto isPort = [](const std::string& s) -> int {
        if (s.empty()) return 0;
        try {
            int p = std::stoi(s);
            return (p > 0 && p < 65536) ? p : 0;
        } catch (...) { return 0; }
    };
    auto isHostLike = [](const std::string& s) -> bool {
        if (s.empty() || s.size() > 253) return false;
        // Must contain a dot or be an IP
        if (s.find('.') == std::string::npos) return false;
        // No spaces, no special chars except .-_
        for (char c : s) {
            if (!isalnum((unsigned char)c) && c != '.' && c != '-' && c != '_') return false;
        }
        return true;
    };
    auto addSrv = [&](const std::string& host, int port, const std::string& user,
                       const std::string& pass) {
        if (host.empty() || port <= 0 || user.empty()) return;
        UpstreamServer s;
        s.host = host; s.port = port; s.user = user; s.pass = pass;
        s.enabled = true;
        s.name = host + ":" + std::to_string(port);
        if (!pxHost.empty() && pxPort > 0) {
            s.proxy_host = pxHost; s.proxy_port = pxPort;
            s.proxy_user = pxUser; s.proxy_pass = pxPass;
        }
        // Deduplicate
        for (auto& e : out)
            if (e.host == s.host && e.port == s.port && e.user == s.user) return;
        out.push_back(std::move(s));
    };

    // Split into lines
    std::vector<std::string> lines;
    {
        std::istringstream ss(text);
        std::string l;
        while (std::getline(ss, l)) lines.push_back(trim(l));
    }

    // State for multi-line HOST/PORT/USER/PASS block parsing
    std::string blkHost, blkUser, blkPass;
    int blkPort = 0;

    auto flushBlock = [&]() {
        if (!blkHost.empty() && blkPort > 0 && !blkUser.empty())
            addSrv(blkHost, blkPort, blkUser, blkPass);
        blkHost.clear(); blkUser.clear(); blkPass.clear(); blkPort = 0;
    };

    for (size_t li = 0; li < lines.size(); li++) {
        std::string line = lines[li];
        if (line.empty()) continue;
        // Skip lines that are clearly not server data
        std::string low = toLower(line);
        if (low.find("newcamd") != std::string::npos) continue;
        if (low.find("deskey") != std::string::npos) continue;
        if (low.find("oscam") != std::string::npos && low.find("device") == std::string::npos) continue;
        // Skip separator lines
        { bool allSep = true;
          for (char c : line) if (c != '-' && c != '_' && c != '=' && c != ' ' && c != '\t') { allSep = false; break; }
          if (allSep) continue;
        }

        // ── Format 1: C-line — "C: host port user pass" ──
        if (line.size() > 2 && (line[0]=='C'||line[0]=='c') && (line[1]==':'||line[1]==' ')) {
            std::string rest = line.substr(line[1]==':' ? 2 : 1);
            rest = trim(rest);
            // Strip another ":" if present (e.g. "C : host ...")
            if (!rest.empty() && rest[0] == ':') rest = trim(rest.substr(1));
            std::vector<std::string> tok;
            { std::istringstream ls(rest); std::string t; while (ls >> t) tok.push_back(t); }
            if (tok.size() >= 4) {
                int p = isPort(tok[1]);
                if (p > 0 && isHostLike(tok[0])) {
                    addSrv(tok[0], p, tok[2], tok[3]);
                    continue;
                }
            }
        }

        // ── Format 2: Oscam [reader] block — "device = host,port" ──
        if (low.find("device") != std::string::npos && line.find('=') != std::string::npos) {
            // Extract value after '='
            std::string val = trim(line.substr(line.find('=') + 1));
            // Parse "host,port" or "host, port"
            auto comma = val.find(',');
            if (comma != std::string::npos) {
                std::string h = trim(val.substr(0, comma));
                std::string pStr = trim(val.substr(comma + 1));
                int p = isPort(pStr);
                if (p > 0 && isHostLike(h)) {
                    // Look ahead for user= and password= in next ~10 lines
                    std::string oUser, oPass;
                    for (size_t j = li + 1; j < lines.size() && j <= li + 15; j++) {
                        std::string jl = toLower(lines[j]);
                        auto eqPos = lines[j].find('=');
                        if (eqPos == std::string::npos) continue;
                        std::string key = trim(toLower(lines[j].substr(0, eqPos)));
                        std::string val2 = trim(lines[j].substr(eqPos + 1));
                        if (key == "user" || key.find("user") != std::string::npos)
                            oUser = val2;
                        else if (key == "password" || key.find("pass") != std::string::npos)
                            oPass = val2;
                        if (jl.find("[reader]") != std::string::npos) break;
                    }
                    if (!oUser.empty())
                        addSrv(h, p, oUser, oPass);
                    continue;
                }
            }
        }

        // ── Format 3: Oscam user= / password= (catch standalone lines) ──
        if (line.find('=') != std::string::npos) {
            auto eqPos = line.find('=');
            std::string key = trim(toLower(line.substr(0, eqPos)));
            std::string val = trim(line.substr(eqPos + 1));
            if (key == "user" || key == "username") { blkUser = val; continue; }
            if (key == "password" || key == "pass") { blkPass = val; continue; }
            if (key == "protocol" || key == "label" || key == "enable" || key == "group" ||
                key == "cccversion" || key == "ccckeepalive") continue;
        }

        // ── Format 4: HOST/PORT/USER/PASS block (label : value or label value) ──
        if (low.substr(0, 4) == "host" || low.substr(0, 5) == "host:") {
            // Flush any previous block
            flushBlock();
            // Extract host value: "HOST: xxx" or "HOST : xxx"
            std::string rest = line.substr(4);
            if (!rest.empty() && rest[0] == ':') rest = rest.substr(1);
            rest = trim(rest);
            if (isHostLike(rest)) blkHost = rest;
            continue;
        }
        if (low.substr(0, 4) == "port" || low.substr(0, 5) == "port:") {
            std::string rest = line.substr(4);
            if (!rest.empty() && rest[0] == ':') rest = rest.substr(1);
            rest = trim(rest);
            int p = isPort(rest);
            if (p > 0) blkPort = p;
            continue;
        }
        if ((low.substr(0, 4) == "user" && (low.size() <= 4 || low[4] == ':' || low[4] == ' ' || low[4] == '\t'))
            || low.substr(0, 5) == "user:") {
            // Could be "USER: xxx pass" or "USER: xxx"
            std::string rest;
            if (low[4] == ':' || low[4] == ' ') rest = line.substr(5);
            else rest = line.substr(4);
            if (!rest.empty() && rest[0] == ':') rest = rest.substr(1);
            rest = trim(rest);
            // "USER: xxx yyy" — user and pass on same line
            std::vector<std::string> tok;
            { std::istringstream ls(rest); std::string t; while (ls >> t) tok.push_back(t); }
            if (tok.size() >= 2) {
                blkUser = tok[0]; blkPass = tok[1];
                // If we have a host+port already, emit now
                if (!blkHost.empty() && blkPort > 0) {
                    addSrv(blkHost, blkPort, blkUser, blkPass);
                    // Don't clear host/port in case multiple users share same host
                    blkUser.clear(); blkPass.clear();
                }
            } else if (tok.size() == 1) {
                blkUser = tok[0];
            }
            continue;
        }
        if (low.substr(0, 4) == "pass" && (low.size() <= 4 || low[4] == ':' || low[4] == ' ' || low[4] == '\t' || low[4] == 'w')) {
            // "PASS: xxx" or "PASSWORD: xxx"
            size_t start = 4;
            if (low.substr(0, 8) == "password") start = 8;
            std::string rest = line.substr(start);
            if (!rest.empty() && (rest[0] == ':' || rest[0] == ' ')) rest = rest.substr(1);
            rest = trim(rest);
            if (!rest.empty()) blkPass = rest;
            // If we have complete block, emit
            if (!blkHost.empty() && blkPort > 0 && !blkUser.empty()) {
                addSrv(blkHost, blkPort, blkUser, blkPass);
                blkUser.clear(); blkPass.clear();
            }
            continue;
        }

        // ── Format 5: bare "host port user pass" (space-separated, 4 tokens) ──
        {
            std::vector<std::string> tok;
            { std::istringstream ls(line); std::string t; while (ls >> t) tok.push_back(t); }
            if (tok.size() == 4 && isHostLike(tok[0]) && isPort(tok[1]) > 0) {
                addSrv(tok[0], isPort(tok[1]), tok[2], tok[3]);
                continue;
            }
            // "host,port user pass" (comma-separated host,port)
            if (tok.size() == 3) {
                auto comma = tok[0].find(',');
                if (comma != std::string::npos) {
                    std::string h = tok[0].substr(0, comma);
                    int p = isPort(tok[0].substr(comma + 1));
                    if (p > 0 && isHostLike(h)) {
                        addSrv(h, p, tok[1], tok[2]);
                        continue;
                    }
                }
            }
            // Might be just "host port" on one line — store as potential block start
            if (tok.size() == 2 && isHostLike(tok[0]) && isPort(tok[1]) > 0) {
                flushBlock();
                blkHost = tok[0]; blkPort = isPort(tok[1]);
                continue;
            }
        }

        // ── Format 6: [reader] marker — flush previous block ──
        if (low.find("[reader]") != std::string::npos) {
            flushBlock();
            continue;
        }
    }
    // Flush any remaining block
    flushBlock();
    return out;
}

// ── Full config ───────────────────────────────────────────────────────────────
struct CccamConfig {
    int         port = 8000;
    std::string user = "a";
    std::string pass = "a";
    bool        log_ecm = true;         // log ECM/CW to disk
    // Global proxy (applied to servers that have no per-server proxy)
    std::string global_proxy_host;
    int         global_proxy_port = 0;
    std::string global_proxy_user;
    std::string global_proxy_pass;
    std::vector<UpstreamServer> servers; // dynamic upstream list

    // Apply global proxy to all servers that don't have their own proxy set
    void applyGlobalProxy() {
        if (global_proxy_host.empty() || global_proxy_port <= 0) return;
        for (auto& s : servers) {
            if (s.proxy_host.empty() || s.proxy_port <= 0) {
                s.proxy_host = global_proxy_host;
                s.proxy_port = global_proxy_port;
                s.proxy_user = global_proxy_user;
                s.proxy_pass = global_proxy_pass;
            }
        }
    }

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
            o << "\"global_proxy_host\":\"" << esc(global_proxy_host) << "\",\n";
            o << "\"global_proxy_port\":" << global_proxy_port << ",\n";
            o << "\"global_proxy_user\":\"" << esc(global_proxy_user) << "\",\n";
            o << "\"global_proxy_pass\":\"" << esc(global_proxy_pass) << "\",\n";
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
            global_proxy_host = getStr(c, "global_proxy_host", global_proxy_host);
            global_proxy_port = getInt(c, "global_proxy_port", global_proxy_port);
            global_proxy_user = getStr(c, "global_proxy_user", global_proxy_user);
            global_proxy_pass = getStr(c, "global_proxy_pass", global_proxy_pass);
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
