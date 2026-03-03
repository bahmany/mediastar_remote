#pragma once
#include <string>
#include <vector>
#include <fstream>
#include <sstream>
#include <ctime>
#include <algorithm>
#ifdef _WIN32
#include <windows.h>
#endif

namespace stb {

struct CustomListEntry {
    std::string service_id;
    std::string name;
    int service_index = -1;
    bool is_radio = false;
};

struct CustomList {
    std::string title;
    std::vector<CustomListEntry> entries;
};

// Persistent custom channel lists — auto-save/load JSON next to exe.
class CustomLists {
public:
    static constexpr const char* FILENAME = "gmscreen_custom_lists.json";

    static std::string filePath() {
#ifdef _WIN32
        char buf[MAX_PATH] = {};
        GetModuleFileNameA(nullptr, buf, MAX_PATH);
        std::string p(buf);
        auto pos = p.find_last_of("\\/");
        if (pos != std::string::npos) p = p.substr(0, pos + 1);
        return p + FILENAME;
#else
        return FILENAME;
#endif
    }

    std::vector<CustomList> lists;

    bool save() const {
        try {
            std::ofstream f(filePath(), std::ios::trunc);
            if (!f.is_open()) return false;
            f << "{\"lists\":[\n";
            for (size_t li = 0; li < lists.size(); li++) {
                auto& L = lists[li];
                if (li > 0) f << ",\n";
                f << "{\"title\":\"" << esc(L.title) << "\",\"entries\":[\n";
                for (size_t ei = 0; ei < L.entries.size(); ei++) {
                    auto& e = L.entries[ei];
                    if (ei > 0) f << ",\n";
                    f << "{\"sid\":\"" << esc(e.service_id) << "\""
                      << ",\"nm\":\"" << esc(e.name) << "\""
                      << ",\"si\":" << e.service_index
                      << ",\"rd\":" << (e.is_radio ? 1 : 0) << "}";
                }
                f << "]}";
            }
            f << "]}\n";
            return true;
        } catch (...) { return false; }
    }

    bool load() {
        try {
            std::ifstream f(filePath());
            if (!f.is_open()) return false;
            std::string c((std::istreambuf_iterator<char>(f)),
                           std::istreambuf_iterator<char>());
            f.close();
            lists.clear();

            // Parse "lists":[ ... ]
            size_t pos = c.find("\"lists\"");
            if (pos == std::string::npos) return false;
            pos = c.find('[', pos);
            if (pos == std::string::npos) return false;
            pos++; // skip [

            // Parse each list object
            while (pos < c.size()) {
                skipWs(c, pos);
                if (pos >= c.size() || c[pos] == ']') break;
                if (c[pos] == ',') { pos++; continue; }
                if (c[pos] != '{') break;

                CustomList L;
                // Find title
                size_t tpos = c.find("\"title\"", pos);
                if (tpos == std::string::npos) break;
                L.title = extractStr(c, tpos);

                // Find entries array
                size_t epos = c.find("\"entries\"", pos);
                if (epos == std::string::npos) break;
                epos = c.find('[', epos);
                if (epos == std::string::npos) break;
                epos++;

                // Parse each entry
                while (epos < c.size()) {
                    skipWs(c, epos);
                    if (epos >= c.size() || c[epos] == ']') { epos++; break; }
                    if (c[epos] == ',') { epos++; continue; }
                    if (c[epos] != '{') break;

                    size_t eEnd = findMatchingBrace(c, epos);
                    if (eEnd == std::string::npos) break;
                    std::string obj = c.substr(epos, eEnd - epos + 1);

                    CustomListEntry e;
                    e.service_id = getStrVal(obj, "sid");
                    e.name = getStrVal(obj, "nm");
                    e.service_index = getIntVal(obj, "si", -1);
                    e.is_radio = getIntVal(obj, "rd", 0) != 0;
                    L.entries.push_back(std::move(e));
                    epos = eEnd + 1;
                }

                // Find end of this list object
                size_t lEnd = findMatchingBrace(c, pos);
                if (lEnd == std::string::npos) break;
                lists.push_back(std::move(L));
                pos = lEnd + 1;
            }
            return true;
        } catch (...) { return false; }
    }

private:
    static std::string esc(const std::string& s) {
        std::string r; r.reserve(s.size());
        for (char ch : s) {
            if (ch == '"') r += "\\\"";
            else if (ch == '\\') r += "\\\\";
            else r += ch;
        }
        return r;
    }
    static std::string unesc(const std::string& s) {
        std::string r; r.reserve(s.size());
        for (size_t i = 0; i < s.size(); i++) {
            if (s[i] == '\\' && i + 1 < s.size()) {
                if (s[i+1] == '"') { r += '"'; i++; }
                else if (s[i+1] == '\\') { r += '\\'; i++; }
                else r += s[i];
            } else r += s[i];
        }
        return r;
    }
    static void skipWs(const std::string& s, size_t& p) {
        while (p < s.size() && (s[p]==' '||s[p]=='\n'||s[p]=='\r'||s[p]=='\t')) p++;
    }
    static size_t findMatchingBrace(const std::string& s, size_t start) {
        if (start >= s.size() || s[start] != '{') return std::string::npos;
        int depth = 0;
        bool inStr = false;
        for (size_t i = start; i < s.size(); i++) {
            if (inStr) {
                if (s[i] == '\\' && i+1 < s.size()) { i++; continue; }
                if (s[i] == '"') inStr = false;
                continue;
            }
            if (s[i] == '"') { inStr = true; continue; }
            if (s[i] == '{') depth++;
            else if (s[i] == '}') { depth--; if (depth == 0) return i; }
        }
        return std::string::npos;
    }
    static std::string extractStr(const std::string& c, size_t keyPos) {
        size_t colon = c.find(':', keyPos);
        if (colon == std::string::npos) return "";
        size_t q1 = c.find('"', colon + 1);
        if (q1 == std::string::npos) return "";
        q1++;
        std::string val;
        for (size_t i = q1; i < c.size() && c[i] != '"'; i++) {
            if (c[i] == '\\' && i+1 < c.size()) { val += c[i]; val += c[i+1]; i++; }
            else val += c[i];
        }
        return unesc(val);
    }
    static std::string getStrVal(const std::string& obj, const char* key) {
        std::string needle = std::string("\"") + key + "\":";
        size_t pos = obj.find(needle);
        if (pos == std::string::npos) return "";
        return extractStr(obj, pos);
    }
    static int getIntVal(const std::string& obj, const char* key, int def) {
        std::string needle = std::string("\"") + key + "\":";
        size_t pos = obj.find(needle);
        if (pos == std::string::npos) return def;
        pos += needle.size();
        while (pos < obj.size() && obj[pos] == ' ') pos++;
        size_t end = obj.find_first_of(",}", pos);
        if (end == std::string::npos) return def;
        try { return std::stoi(obj.substr(pos, end - pos)); } catch (...) { return def; }
    }
};

} // namespace stb
