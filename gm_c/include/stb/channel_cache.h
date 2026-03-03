#pragma once
#include "stb/models.h"
#include <string>
#include <vector>
#include <fstream>
#include <sstream>
#include <ctime>
#ifdef _WIN32
#include <windows.h>
#endif

namespace stb {

// Persistent channel cache — saves/loads channel list to a JSON file on disk.
// Channels are cached permanently until the user explicitly refreshes.
class ChannelCache {
public:
    static constexpr const char* CACHE_FILENAME = "gmscreen_channels.json";

    // Get cache file path next to the executable
    static std::string cacheFilePath() {
#ifdef _WIN32
        char buf[MAX_PATH] = {};
        GetModuleFileNameA(nullptr, buf, MAX_PATH);
        std::string p(buf);
        auto pos = p.find_last_of("\\/");
        if (pos != std::string::npos) p = p.substr(0, pos + 1);
        return p + CACHE_FILENAME;
#else
        return CACHE_FILENAME;
#endif
    }

    // Save channels to file. Returns true on success.
    static bool save(const std::vector<Channel>& channels, 
                     const std::string& file = "") {
        std::string path = file.empty() ? cacheFilePath() : file;
        try {
            std::ofstream f(path, std::ios::trunc);
            if (!f.is_open()) return false;
            
            f << "{\n\"saved_at\":" << time(nullptr) 
              << ",\n\"count\":" << channels.size()
              << ",\n\"channels\":[\n";
            
            for (size_t i = 0; i < channels.size(); ++i) {
                auto& ch = channels[i];
                if (i > 0) f << ",\n";
                f << "{";
                f << "\"si\":" << ch.service_index;
                f << ",\"sid\":\"" << esc(ch.service_id) << "\"";
                f << ",\"nm\":\"" << esc(ch.service_name) << "\"";
                f << ",\"rd\":" << (ch.is_radio?1:0);
                f << ",\"hd\":" << (ch.is_hd?1:0);
                f << ",\"sc\":" << (ch.is_scrambled?1:0);
                f << ",\"lk\":" << (ch.is_locked?1:0);
                f << ",\"ep\":" << (ch.has_epg?1:0);
                f << ",\"t2\":" << (ch.is_tuner2?1:0);
                f << ",\"fb\":" << ch.fav_bit;
                f << ",\"vp\":" << ch.video_pid;
                f << ",\"pp\":" << ch.pmt_pid;
                f << ",\"pc\":" << ch.pcr_pid;
                f << ",\"tp\":" << ch.ttx_pid;
                f << ",\"ms\":" << ch.modulation_system;
                f << ",\"mt\":" << ch.modulation_type;
                f << ",\"ro\":" << ch.roll_off;
                f << ",\"pt\":" << ch.pilot_tones;
                f << ",\"aa\":\"" << esc(ch.audio_pids_raw) << "\"";
                f << ",\"sa\":\"" << esc(ch.subtitle_pids_raw) << "\"";
                f << ",\"sx\":" << ch.satellite_index;
                f << ",\"sn\":\"" << esc(ch.satellite_name) << "\"";
                f << "}";
            }
            
            f << "\n]\n}\n";
            f.close();
            return true;
        } catch (...) { return false; }
    }
    
    // Load channels from file. Returns empty vector on failure.
    static std::vector<Channel> load(const std::string& file = "") {
        std::string path = file.empty() ? cacheFilePath() : file;
        std::vector<Channel> result;
        try {
            std::ifstream f(path);
            if (!f.is_open()) return result;
            
            std::string content((std::istreambuf_iterator<char>(f)),
                                 std::istreambuf_iterator<char>());
            f.close();
            
            // Find "channels":[ and parse objects
            size_t pos = content.find("\"channels\"");
            if (pos == std::string::npos) return result;
            pos = content.find('[', pos);
            if (pos == std::string::npos) return result;
            
            // Parse each {...} object — skip braces inside quoted strings
            int brace = 0;
            size_t obj_start = 0;
            bool inStr = false;
            for (size_t i = pos; i < content.size(); ++i) {
                char c = content[i];
                if (inStr) {
                    if (c == '\\' && i+1 < content.size()) { i++; continue; }
                    if (c == '"') inStr = false;
                    continue;
                }
                if (c == '"') { inStr = true; continue; }
                if (c == '{') {
                    if (brace == 0) obj_start = i;
                    brace++;
                } else if (c == '}') {
                    brace--;
                    if (brace == 0) {
                        std::string obj = content.substr(obj_start, i - obj_start + 1);
                        Channel ch = parseObj(obj);
                        if (ch.service_index >= 0 || !ch.service_id.empty())
                            result.push_back(std::move(ch));
                    }
                } else if (c == ']' && brace == 0) {
                    break;
                }
            }
        } catch (const std::exception& ex) {
            // parsing failed — return what we have
            (void)ex;
        } catch (...) {}
        return result;
    }
    
    // Check if cache file exists and has data
    static bool exists(const std::string& file = "") {
        std::string path = file.empty() ? cacheFilePath() : file;
        std::ifstream f(path);
        return f.good();
    }

private:
    static std::string esc(const std::string& s) {
        std::string r; r.reserve(s.size());
        for (char c : s) {
            if (c == '"') r += "\\\"";
            else if (c == '\\') r += "\\\\";
            else if (c == '\n') r += "\\n";
            else if (c == '\r') r += "\\r";
            else if (c == '\t') r += "\\t";
            else r += c;
        }
        return r;
    }
    
    static std::string unesc(const std::string& s) {
        std::string r; r.reserve(s.size());
        for (size_t i = 0; i < s.size(); ++i) {
            if (s[i] == '\\' && i+1 < s.size()) {
                char n = s[i+1];
                if (n == '"') { r += '"'; i++; }
                else if (n == '\\') { r += '\\'; i++; }
                else if (n == 'n') { r += '\n'; i++; }
                else if (n == 'r') { r += '\r'; i++; }
                else if (n == 't') { r += '\t'; i++; }
                else r += s[i];
            } else {
                r += s[i];
            }
        }
        return r;
    }
    
    static std::string getVal(const std::string& obj, const std::string& key) {
        std::string needle = "\"" + key + "\":";
        size_t pos = obj.find(needle);
        if (pos == std::string::npos) return "";
        pos += needle.size();
        // Skip whitespace
        while (pos < obj.size() && (obj[pos]==' '||obj[pos]=='\t')) pos++;
        if (pos >= obj.size()) return "";
        
        if (obj[pos] == '"') {
            // String value
            pos++;
            std::string val;
            for (; pos < obj.size() && obj[pos] != '"'; ++pos) {
                if (obj[pos] == '\\' && pos+1 < obj.size()) {
                    val += obj[pos]; val += obj[pos+1]; pos++;
                } else {
                    val += obj[pos];
                }
            }
            return unesc(val);
        } else {
            // Numeric value
            size_t end = obj.find_first_of(",}", pos);
            if (end == std::string::npos) end = obj.size();
            return obj.substr(pos, end - pos);
        }
    }
    
    static int getIntVal(const std::string& obj, const std::string& key, int def = 0) {
        std::string v = getVal(obj, key);
        if (v.empty()) return def;
        try { return std::stoi(v); } catch (...) { return def; }
    }
    
    static Channel parseObj(const std::string& obj) {
        Channel ch;
        ch.service_index    = getIntVal(obj, "si", -1);
        ch.service_id       = getVal(obj, "sid");
        ch.service_name     = getVal(obj, "nm");
        ch.is_radio         = getIntVal(obj, "rd") != 0;
        ch.is_hd            = getIntVal(obj, "hd") != 0;
        ch.is_scrambled     = getIntVal(obj, "sc") != 0;
        ch.is_locked        = getIntVal(obj, "lk") != 0;
        ch.has_epg          = getIntVal(obj, "ep") != 0;
        ch.is_tuner2        = getIntVal(obj, "t2") != 0;
        ch.fav_bit          = getIntVal(obj, "fb");
        ch.video_pid        = getIntVal(obj, "vp");
        ch.pmt_pid          = getIntVal(obj, "pp");
        ch.pcr_pid          = getIntVal(obj, "pc");
        ch.ttx_pid          = getIntVal(obj, "tp");
        ch.modulation_system= getIntVal(obj, "ms");
        ch.modulation_type  = getIntVal(obj, "mt");
        ch.roll_off         = getIntVal(obj, "ro");
        ch.pilot_tones      = getIntVal(obj, "pt");
        ch.audio_pids_raw   = getVal(obj, "aa");
        ch.subtitle_pids_raw= getVal(obj, "sa");
        ch.satellite_index  = getIntVal(obj, "sx", -1);
        ch.satellite_name   = getVal(obj, "sn");
        // Legacy compat
        ch.program_id       = ch.service_id;
        ch.channel_type     = ch.is_radio ? 1 : 0;
        ch.is_fta           = !ch.is_scrambled;
        ch.is_favorite      = (ch.fav_bit != 0);
        return ch;
    }
};

} // namespace stb
