#pragma once
// AI-Powered CW Prediction Engine
// Uses Ollama LLM to analyze ECM patterns and predict Control Words
// Integrates with CCcam server for automated decryption assistance

#include <string>
#include <vector>
#include <unordered_map>
#include <mutex>
#include <thread>
#include <atomic>
#include <array>
#include <cstdint>
#include <ctime>
#include <fstream>
#include <sstream>
#include <algorithm>
#include <functional>
#include <chrono>
#include <numeric>
#include <cmath>

#ifdef _WIN32
#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif
#include <winsock2.h>
#include <ws2tcpip.h>
#endif

namespace ai {

// ECM pattern for learning
struct EcmPattern {
    uint16_t caid = 0;
    uint16_t sid = 0;
    uint32_t provid = 0;
    std::array<uint8_t, 16> ecm_prefix{};  // First 16 bytes of ECM
    std::array<uint8_t, 16> cw{};          // Resulting CW
    int success_count = 0;
    time_t last_seen = 0;
};

// Server quality metrics for AI-based ranking
struct ServerQuality {
    std::string host;
    int port = 0;
    int total_requests = 0;
    int successful = 0;
    int failed = 0;
    float avg_latency_ms = 0;
    float success_rate = 0;
    std::vector<uint16_t> known_caids;  // CAIDs this server can decode
    time_t last_success = 0;
    time_t last_fail = 0;
    int consecutive_fails = 0;
    int ai_score = 0;  // AI-computed quality score (0-100)
};

// Known CAID information
struct CaidInfo {
    uint16_t caid = 0;
    std::string name;
    std::string provider;
    int ecm_count = 0;
    int success_count = 0;
    time_t last_seen = 0;
};

// Per-channel ECM analytics (keyed by CAID+SID)
struct ChannelStats {
    static constexpr float kSuccessPrior = 1.0f;
    static constexpr float kLatencyEmaAlpha = 0.15f;
    static constexpr int kBestServerMinSamples = 5;
    static constexpr float kWilsonZ = 1.0f;
    static constexpr float kRotationMinSec = 2.0f;
    static constexpr float kRotationMaxSec = 180.0f;
    static constexpr float kRotationEmaAlpha = 0.20f;
    static constexpr float kRotationJitterEmaAlpha = 0.15f;
    static constexpr int kXorStableRepeats = 3;

    uint16_t caid = 0;
    uint16_t sid = 0;
    uint32_t provid = 0;
    int ecm_total = 0;
    int ecm_ok = 0;
    int ecm_fail = 0;
    float success_rate = 0;
    float success_rate_raw = 0;
    float avg_latency_ms = 0;
    time_t first_seen = 0;
    time_t last_seen = 0;
    std::string best_server;         // server with best success rate for this channel

    // CW rotation tracking
    std::array<uint8_t, 16> last_cw{};
    std::array<uint8_t, 16> prev_cw{};
    time_t last_cw_change = 0;       // when CW last changed
    time_t prev_cw_change = 0;       // when CW changed before that
    int cw_changes = 0;              // total CW rotations observed
    float avg_rotation_sec = 0;      // average CW rotation period (seconds)
    float min_rotation_sec = 999999; // shortest observed rotation
    float max_rotation_sec = 0;      // longest observed rotation
    int rotation_samples = 0;
    float rotation_jitter_sec = 0;

    // XOR-delta analysis: track XOR between consecutive CWs
    std::array<uint8_t, 16> last_xor_delta{};  // last_cw XOR prev_cw
    int xor_pattern_repeats = 0;    // how many times same XOR-delta repeated
    bool xor_stable = false;         // true if XOR-delta is consistent (possible linear key schedule)
    float xor_stability = 0;

    // ECM table_id parity tracking
    int even_ecm_count = 0;   // table_id 0x80
    int odd_ecm_count = 0;    // table_id 0x81
    uint8_t last_table_id = 0;

    // Per-server success map (server_label -> {ok, fail})
    struct SrvHit { int ok = 0; int fail = 0; };
    std::unordered_map<std::string, SrvHit> server_hits;

    void update(bool success, float latMs, const std::string& server, const uint8_t* cw,
                uint8_t tableId) {
        ecm_total++;
        if (success) ecm_ok++; else ecm_fail++;
        success_rate_raw = ecm_total > 0 ? (float)ecm_ok / ecm_total : 0;
        success_rate = (ecm_total > 0)
            ? (float)(ecm_ok + kSuccessPrior) / (float)(ecm_total + 2.0f * kSuccessPrior)
            : 0.0f;

        if (latMs >= 0) {
            if (avg_latency_ms <= 0)
                avg_latency_ms = latMs;
            else
                avg_latency_ms = avg_latency_ms + kLatencyEmaAlpha * (latMs - avg_latency_ms);
        }

        time_t now = time(nullptr);
        if (first_seen == 0) first_seen = now;
        last_seen = now;
        last_table_id = tableId;
        if (tableId == 0x80) even_ecm_count++;
        else if (tableId == 0x81) odd_ecm_count++;

        // Server tracking
        auto& sh = server_hits[server];
        if (success) sh.ok++; else sh.fail++;

        // Find best server
        float bestScore = -1e9f;
        for (auto& [lbl, h] : server_hits) {
            int tot = h.ok + h.fail;
            if (tot < kBestServerMinSamples) continue;
            float n = (float)tot;
            float phat = (float)(h.ok + kSuccessPrior) / (n + 2.0f * kSuccessPrior);
            float denom = 1.0f + (kWilsonZ * kWilsonZ) / n;
            float center = phat + (kWilsonZ * kWilsonZ) / (2.0f * n);
            float rad = kWilsonZ * std::sqrt((phat * (1.0f - phat) + (kWilsonZ * kWilsonZ) / (4.0f * n)) / n);
            float lower = (center - rad) / denom;
            if (lower > bestScore) { bestScore = lower; best_server = lbl; }
        }

        // CW rotation detection
        if (success && cw) {
            bool cwChanged = (memcmp(cw, last_cw.data(), 16) != 0);
            bool cwNonZero = false;
            for (int i = 0; i < 16; i++) if (cw[i]) { cwNonZero = true; break; }

            if (cwNonZero && cwChanged) {
                // Compute XOR-delta
                std::array<uint8_t, 16> newXor{};
                bool lastNonZero = false;
                for (int i = 0; i < 16; i++) {
                    if (last_cw[i]) lastNonZero = true;
                    newXor[i] = cw[i] ^ last_cw[i];
                }

                if (lastNonZero) {
                    // Check if XOR-delta is same as last time
                    if (last_xor_delta == newXor)
                        xor_pattern_repeats++;
                    else
                        xor_pattern_repeats = 0;
                    xor_stable = (xor_pattern_repeats >= kXorStableRepeats);
                    xor_stability = std::min(1.0f, (float)xor_pattern_repeats / (float)kXorStableRepeats);
                    last_xor_delta = newXor;

                    // Rotation timing
                    prev_cw_change = last_cw_change;
                    last_cw_change = now;
                    cw_changes++;

                    if (prev_cw_change > 0 && last_cw_change > prev_cw_change) {
                        float period = (float)(last_cw_change - prev_cw_change);
                        if (period >= kRotationMinSec && period <= kRotationMaxSec) {
                            rotation_samples++;
                            if (rotation_samples == 1 || avg_rotation_sec <= 0) {
                                avg_rotation_sec = period;
                                rotation_jitter_sec = 0;
                            } else {
                                avg_rotation_sec = avg_rotation_sec + kRotationEmaAlpha * (period - avg_rotation_sec);
                                float dev = std::fabs(period - avg_rotation_sec);
                                rotation_jitter_sec = rotation_jitter_sec + kRotationJitterEmaAlpha * (dev - rotation_jitter_sec);
                            }
                            if (period < min_rotation_sec) min_rotation_sec = period;
                            if (period > max_rotation_sec) max_rotation_sec = period;
                        }
                    }
                }

                prev_cw = last_cw;
                memcpy(last_cw.data(), cw, 16);
            } else if (cwNonZero && !cwChanged) {
                // Same CW repeated — not a rotation
            } else if (cwNonZero) {
                // First CW for this channel
                memcpy(last_cw.data(), cw, 16);
                last_cw_change = now;
            }
        }
    }
};

// CW Cache entry - keyed by FNV hash of CAID+SID+ECM body
struct CwCacheEntry {
    std::array<uint8_t, 16> cw{};
    std::chrono::steady_clock::time_point expires;
    std::string server;
    uint8_t key_parity = 0;  // ECM table_id: 0x80=even, 0x81=odd
};

// AI CW Predictor - integrates with CCcam for intelligent decryption
class CwPredictor {
public:
    std::string ollama_host = "127.0.0.1";
    int ollama_port = 11434;
    std::string model = "qwen3:14b";
    
    std::atomic<bool> enabled{true};
    std::atomic<bool> learning{true};
    std::atomic<int> predictions_made{0};
    std::atomic<int> predictions_correct{0};
    std::atomic<int> ai_predictions{0};
    std::atomic<bool> ollama_available{false};
    std::atomic<int> cache_hits{0};
    std::atomic<int> cache_misses{0};
    
    std::function<void(const std::string&)> onLog;

    // ── CW Cache ─────────────────────────────────────────────────────────────
    // Lookup CW from cache. Returns true and fills cwOut[16] if found and not expired.
    bool cacheLookup(uint16_t caid, uint16_t sid, const uint8_t* ecm, int ecmLen, uint8_t* cwOut) {
        uint64_t key = hashEcm(caid, sid, ecm, ecmLen);
        std::lock_guard<std::mutex> g(cacheMu_);
        auto it = cwCache_.find(key);
        if (it == cwCache_.end()) { cache_misses++; return false; }
        if (std::chrono::steady_clock::now() > it->second.expires) {
            cwCache_.erase(it);
            cache_misses++;
            return false;
        }
        memcpy(cwOut, it->second.cw.data(), 16);
        cache_hits++;
        return true;
    }

    // Store a CW in cache with 10-second TTL (standard CCcam crypto period)
    void cacheStore(uint16_t caid, uint16_t sid, const uint8_t* ecm, int ecmLen,
                    const uint8_t* cw, const std::string& server) {
        uint64_t key = hashEcm(caid, sid, ecm, ecmLen);
        CwCacheEntry entry;
        memcpy(entry.cw.data(), cw, 16);
        entry.expires = std::chrono::steady_clock::now() + std::chrono::seconds(10);
        entry.server = server;
        entry.key_parity = (ecmLen > 0) ? ecm[0] : 0;
        std::lock_guard<std::mutex> g(cacheMu_);
        cwCache_[key] = entry;
        // Evict expired entries periodically
        if (cwCache_.size() > 500) {
            auto now = std::chrono::steady_clock::now();
            for (auto it = cwCache_.begin(); it != cwCache_.end(); ) {
                if (now > it->second.expires) it = cwCache_.erase(it);
                else ++it;
            }
        }
    }

    // ── Smart Server Router ───────────────────────────────────────────────────
    // Sort server indices by AI-computed score for a given CAID.
    // indices is initially [0,1,2,...,N-1]; labels[i] = upstream label string.
    // After call, indices[0] = best server for this CAID.
    void sortByScore(uint16_t caid, std::vector<int>& indices, const std::vector<std::string>& labels) {
        std::lock_guard<std::mutex> g(mu_);
        std::stable_sort(indices.begin(), indices.end(), [&](int a, int b) {
            float sa = computeScore_(caid, a < (int)labels.size() ? labels[a] : "");
            float sb = computeScore_(caid, b < (int)labels.size() ? labels[b] : "");
            return sa > sb;
        });
    }

    // ── Async Ollama Analyzer ─────────────────────────────────────────────────
    // Start background Ollama analysis thread (call once on server start)
    void startOllamaAnalyzer() {
        if (ollamaRunning_.exchange(true)) return;  // already running
        std::thread([this]() { ollamaLoop(); }).detach();
    }

    // Stop background analyzer (call on server stop)
    void stopOllamaAnalyzer() { ollamaRunning_ = false; }

    // Trigger an immediate analysis cycle (async, returns instantly)
    void triggerAnalysis() { analyzeNow_ = true; }

    // Get last Ollama analysis report text
    std::string getOllamaReport() {
        std::lock_guard<std::mutex> g(reportMu_);
        return ollamaReport_;
    }
    time_t getOllamaReportTime() { return ollamaReportTime_; }

    // Check if Ollama is available
    bool checkOllama() {
        SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock == INVALID_SOCKET) return false;
        sockaddr_in addr{};
        addr.sin_family = AF_INET;
        addr.sin_port = htons((uint16_t)ollama_port);
        inet_pton(AF_INET, ollama_host.c_str(), &addr.sin_addr);
        DWORD timeout = 1000;
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&timeout, sizeof(timeout));
        setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, (char*)&timeout, sizeof(timeout));
        bool ok = (::connect(sock, (sockaddr*)&addr, sizeof(addr)) == 0);
        closesocket(sock);
        ollama_available = ok;
        return ok;
    }

    // Learn from ECM/CW pair
    void learn(uint16_t caid, uint16_t sid, uint32_t provid,
               const uint8_t* ecm, int ecmLen,
               const uint8_t* cw, bool success,
               const std::string& server, float latencyMs)
    {
        if (!learning.load()) return;
        std::lock_guard<std::mutex> g(mu_);
        
        // Store pattern
        EcmPattern pat;
        pat.caid = caid;
        pat.sid = sid;
        pat.provid = provid;
        memcpy(pat.ecm_prefix.data(), ecm, std::min(ecmLen, 16));
        if (success && cw) memcpy(pat.cw.data(), cw, 16);
        pat.success_count = success ? 1 : 0;
        pat.last_seen = time(nullptr);
        
        std::string key = makeKey(caid, sid, ecm, std::min(ecmLen, 8));
        auto& existing = patterns_[key];
        if (existing.success_count == 0) {
            existing = pat;
        } else if (success) {
            existing.cw = pat.cw;
            existing.success_count++;
            existing.last_seen = pat.last_seen;
        }
        
        // Update server quality
        auto& sq = serverQuality_[server];
        sq.host = server.substr(0, server.find(':'));
        if (server.find(':') != std::string::npos)
            sq.port = std::stoi(server.substr(server.find(':') + 1));
        sq.total_requests++;
        if (success) {
            sq.successful++;
            sq.last_success = time(nullptr);
            // Track CAID
            if (std::find(sq.known_caids.begin(), sq.known_caids.end(), caid) == sq.known_caids.end())
                sq.known_caids.push_back(caid);
        } else {
            sq.failed++;
        }
        sq.success_rate = sq.total_requests > 0 ? (float)sq.successful / sq.total_requests : 0;
        sq.avg_latency_ms = (sq.avg_latency_ms * (sq.total_requests - 1) + latencyMs) / sq.total_requests;
        if (success) {
            sq.consecutive_fails = 0;
        } else {
            sq.consecutive_fails++;
            sq.last_fail = time(nullptr);
        }
        
        // Update CAID info
        auto& ci = caidInfo_[caid];
        ci.caid = caid;
        ci.ecm_count++;
        if (success) ci.success_count++;
        ci.last_seen = time(nullptr);
        if (ci.name.empty()) ci.name = getCaidName(caid);
        
        totalSamples_++;
        
        // Auto-save every 50 learns or every 30 seconds
        learnsSinceSave_++;
        auto now = std::chrono::steady_clock::now();
        bool timeTrigger = std::chrono::duration_cast<std::chrono::seconds>(now - lastSave_).count() >= 30;
        if (learnsSinceSave_ >= 50 || timeTrigger) {
            learnsSinceSave_ = 0;
            lastSave_ = now;
            // Save in background to avoid blocking ECM processing
            std::thread([this]() {
                try { this->save(); } catch (...) {}
            }).detach();
        }
    }

    // Try to predict CW from learned patterns (fast, no AI call)
    bool predictFast(uint16_t caid, uint16_t sid, const uint8_t* ecm, int ecmLen, uint8_t* cwOut)
    {
        std::lock_guard<std::mutex> g(mu_);
        std::string key = makeKey(caid, sid, ecm, std::min(ecmLen, 8));
        auto it = patterns_.find(key);
        if (it != patterns_.end() && it->second.success_count > 0) {
            memcpy(cwOut, it->second.cw.data(), 16);
            predictions_made++;
            return true;
        }
        return false;
    }

    // AI-powered CW prediction (slower, uses Ollama for pattern analysis)
    bool predictAI(uint16_t caid, uint16_t sid, uint32_t provid,
                   const uint8_t* ecm, int ecmLen, uint8_t* cwOut)
    {
        if (!enabled.load()) return false;
        
        // Build context from similar patterns
        std::vector<EcmPattern> similar;
        {
            std::lock_guard<std::mutex> g(mu_);
            for (auto& [k, p] : patterns_) {
                if (p.caid == caid && p.success_count > 0) {
                    similar.push_back(p);
                    if (similar.size() >= 10) break;
                }
            }
        }
        
        if (similar.empty()) return false;
        
        // Build prompt for AI analysis
        std::stringstream ss;
        ss << "/no_think\n";
        ss << "ECM pattern analysis for CAID " << std::hex << caid << " SID " << sid << "\n";
        ss << "Current ECM prefix: " << toHex(ecm, std::min(ecmLen, 16)) << "\n";
        ss << "Known working patterns:\n";
        for (auto& p : similar) {
            ss << "ECM:" << toHex(p.ecm_prefix.data(), 8) << " -> CW:" << toHex(p.cw.data(), 16) << "\n";
        }
        ss << "Predict the CW for the current ECM. Output ONLY 32 hex chars (16 bytes CW), nothing else.";
        
        std::string response = queryOllama(ss.str());
        if (response.size() >= 32) {
            // Extract hex from response
            std::string hex;
            for (char c : response) {
                if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))
                    hex += c;
                if (hex.size() >= 32) break;
            }
            if (hex.size() >= 32 && fromHex(hex, cwOut, 16)) {
                predictions_made++;
                if (onLog) onLog("[AI] Predicted CW for CAID " + std::to_string(caid));
                return true;
            }
        }
        return false;
    }

    // Get best server for a specific CAID (AI-ranked)
    std::string getBestServer(uint16_t caid)
    {
        std::lock_guard<std::mutex> g(mu_);
        std::string best;
        float bestScore = -1;
        
        for (auto& [name, sq] : serverQuality_) {
            // Check if server handles this CAID
            bool handles = std::find(sq.known_caids.begin(), sq.known_caids.end(), caid) != sq.known_caids.end();
            if (!handles && !sq.known_caids.empty()) continue;
            
            // Compute score: success_rate * 50 + (1000 / latency) * 30 + recency * 20
            float score = sq.success_rate * 50;
            if (sq.avg_latency_ms > 0) score += (1000.0f / sq.avg_latency_ms) * 30;
            time_t age = time(nullptr) - sq.last_success;
            if (age < 60) score += 20;
            else if (age < 300) score += 15;
            else if (age < 3600) score += 10;
            
            if (score > bestScore) {
                bestScore = score;
                best = name;
            }
        }
        return best;
    }

    // Get server priority list for ECM forwarding (AI-optimized order)
    std::vector<std::string> getServerPriority(uint16_t caid)
    {
        std::lock_guard<std::mutex> g(mu_);
        std::vector<std::pair<std::string, float>> ranked;
        
        for (auto& [name, sq] : serverQuality_) {
            float score = sq.success_rate * 100;
            if (sq.avg_latency_ms > 0) score += 1000.0f / sq.avg_latency_ms;
            bool handles = std::find(sq.known_caids.begin(), sq.known_caids.end(), caid) != sq.known_caids.end();
            if (handles) score += 50;
            ranked.push_back({name, score});
        }
        
        std::sort(ranked.begin(), ranked.end(), [](auto& a, auto& b) { return a.second > b.second; });
        
        std::vector<std::string> result;
        for (auto& [name, score] : ranked) result.push_back(name);
        return result;
    }

    // Auto-discover servers by testing known CCcam ports
    void discoverServers(const std::vector<std::string>& hosts,
                         std::function<void(const std::string&, int, bool)> onResult)
    {
        std::thread([this, hosts, onResult]() {
            std::vector<int> ports = {12000, 14000, 15000, 16000, 18000, 20000, 22000};
            for (const auto& host : hosts) {
                for (int port : ports) {
                    bool ok = testPort(host, port);
                    if (onResult) onResult(host, port, ok);
                    if (ok) break;  // Found working port
                }
            }
        }).detach();
    }

    // Get statistics
    struct Stats {
        int total_samples = 0;
        int unique_patterns = 0;
        int servers_tracked = 0;
        int caids_seen = 0;
        int predictions = 0;
        int correct = 0;
        int ai_predictions = 0;
        int cache_hits = 0;
        int cache_misses = 0;
        int cache_size = 0;
        float accuracy = 0;
        bool ollama_ok = false;
        time_t last_analysis = 0;
    };
    
    Stats getStats() const {
        std::lock_guard<std::mutex> g(mu_);
        Stats s;
        s.total_samples = totalSamples_;
        s.unique_patterns = (int)patterns_.size();
        s.servers_tracked = (int)serverQuality_.size();
        s.caids_seen = (int)caidInfo_.size();
        s.predictions = predictions_made.load();
        s.correct = predictions_correct.load();
        s.ai_predictions = ai_predictions.load();
        s.cache_hits = cache_hits.load();
        s.cache_misses = cache_misses.load();
        {
            std::lock_guard<std::mutex> gc(cacheMu_);
            s.cache_size = (int)cwCache_.size();
        }
        s.accuracy = s.predictions > 0 ? (float)s.correct / s.predictions * 100 : 0;
        s.ollama_ok = ollama_available.load();
        s.last_analysis = ollamaReportTime_;
        return s;
    }
    
    // Get CAID info list
    std::vector<CaidInfo> getCaidList() const {
        std::lock_guard<std::mutex> g(mu_);
        std::vector<CaidInfo> result;
        for (auto& [id, ci] : caidInfo_) result.push_back(ci);
        std::sort(result.begin(), result.end(), [](auto& a, auto& b) { return a.ecm_count > b.ecm_count; });
        return result;
    }
    
    // Get server quality list
    std::vector<std::pair<std::string, ServerQuality>> getServerList() const {
        std::lock_guard<std::mutex> g(mu_);
        std::vector<std::pair<std::string, ServerQuality>> result;
        for (auto& [name, sq] : serverQuality_) result.push_back({name, sq});
        std::sort(result.begin(), result.end(), [](auto& a, auto& b) { return a.second.success_rate > b.second.success_rate; });
        return result;
    }

    // ── Per-channel ECM analytics ─────────────────────────────────────────────
    void learnChannel(uint16_t caid, uint16_t sid, uint32_t provid,
                      bool success, float latencyMs, const std::string& server,
                      const uint8_t* cw, const uint8_t* ecm, int ecmLen)
    {
        uint32_t key = ((uint32_t)caid << 16) | sid;
        std::lock_guard<std::mutex> g(chMu_);
        auto& ch = channelStats_[key];
        ch.caid = caid;
        ch.sid = sid;
        ch.provid = provid;
        uint8_t tableId = (ecm && ecmLen > 0) ? ecm[0] : 0;
        ch.update(success, latencyMs, server, cw, tableId);
    }

    // Get channel stats list sorted by ECM count (descending)
    std::vector<ChannelStats> getChannelStatsList() const {
        std::lock_guard<std::mutex> g(chMu_);
        std::vector<ChannelStats> result;
        result.reserve(channelStats_.size());
        for (auto& [k, cs] : channelStats_) result.push_back(cs);
        std::sort(result.begin(), result.end(), [](auto& a, auto& b) {
            return a.ecm_total > b.ecm_total;
        });
        return result;
    }

    // Get channel stats for a specific CAID+SID
    ChannelStats getChannelStats(uint16_t caid, uint16_t sid) const {
        uint32_t key = ((uint32_t)caid << 16) | sid;
        std::lock_guard<std::mutex> g(chMu_);
        auto it = channelStats_.find(key);
        if (it != channelStats_.end()) return it->second;
        return {};
    }

    int getChannelCount() const {
        std::lock_guard<std::mutex> g(chMu_);
        return (int)channelStats_.size();
    }

    // Get best server for CAID with detailed info
    ServerQuality* getServerForCaid(uint16_t caid) {
        std::lock_guard<std::mutex> g(mu_);
        ServerQuality* best = nullptr;
        float bestScore = -1;
        for (auto& [name, sq] : serverQuality_) {
            bool handles = std::find(sq.known_caids.begin(), sq.known_caids.end(), caid) != sq.known_caids.end();
            if (!handles) continue;
            float score = sq.success_rate * 100 - sq.consecutive_fails * 10;
            if (sq.avg_latency_ms > 0) score += 1000.0f / sq.avg_latency_ms;
            if (score > bestScore) { bestScore = score; best = &sq; }
        }
        return best;
    }

    // Save/Load learned data
    bool save(const std::string& path = "") const {
        std::string fp = path.empty() ? defaultPath() : path;
        std::lock_guard<std::mutex> g(mu_);
        try {
            std::ofstream f(fp, std::ios::binary);
            if (!f) return false;
            
            // Header
            f << "AICW1\n";
            f << totalSamples_ << " " << predictions_made.load() << " " << predictions_correct.load() << "\n";
            
            // Patterns
            f << patterns_.size() << "\n";
            for (auto& [k, p] : patterns_) {
                f << k << " " << p.caid << " " << p.sid << " " << p.provid << " "
                  << p.success_count << " " << p.last_seen << " "
                  << toHex(p.ecm_prefix.data(), 16) << " " << toHex(p.cw.data(), 16) << "\n";
            }
            
            // Server quality
            f << serverQuality_.size() << "\n";
            for (auto& [name, sq] : serverQuality_) {
                f << name << " " << sq.total_requests << " " << sq.successful << " "
                  << sq.failed << " " << sq.avg_latency_ms << " " << sq.last_success << " "
                  << sq.known_caids.size();
                for (auto c : sq.known_caids) f << " " << c;
                f << "\n";
            }
            
            return true;
        } catch (...) { return false; }
    }

    bool load(const std::string& path = "") {
        std::string fp = path.empty() ? defaultPath() : path;
        std::lock_guard<std::mutex> g(mu_);
        try {
            std::ifstream f(fp);
            if (!f) return false;
            
            std::string hdr;
            std::getline(f, hdr);
            if (hdr != "AICW1") return false;
            
            int pm, pc;
            f >> totalSamples_ >> pm >> pc;
            predictions_made = pm;
            predictions_correct = pc;
            
            int np;
            f >> np;
            patterns_.clear();
            for (int i = 0; i < np; i++) {
                std::string k, ecmHex, cwHex;
                EcmPattern p;
                f >> k >> p.caid >> p.sid >> p.provid >> p.success_count >> p.last_seen >> ecmHex >> cwHex;
                fromHex(ecmHex, p.ecm_prefix.data(), 16);
                fromHex(cwHex, p.cw.data(), 16);
                patterns_[k] = p;
            }
            
            int ns;
            f >> ns;
            serverQuality_.clear();
            for (int i = 0; i < ns; i++) {
                std::string name;
                ServerQuality sq;
                int nc;
                f >> name >> sq.total_requests >> sq.successful >> sq.failed
                  >> sq.avg_latency_ms >> sq.last_success >> nc;
                for (int j = 0; j < nc; j++) {
                    uint16_t c; f >> c;
                    sq.known_caids.push_back(c);
                }
                sq.success_rate = sq.total_requests > 0 ? (float)sq.successful / sq.total_requests : 0;
                serverQuality_[name] = sq;
            }
            
            return true;
        } catch (...) { return false; }
    }

private:
    mutable std::mutex mu_;
    std::unordered_map<std::string, EcmPattern> patterns_;
    std::unordered_map<std::string, ServerQuality> serverQuality_;
    std::unordered_map<uint16_t, CaidInfo> caidInfo_;
    int totalSamples_ = 0;
    int learnsSinceSave_ = 0;          // auto-save counter
    std::chrono::steady_clock::time_point lastSave_ = std::chrono::steady_clock::now();

    // Per-channel analytics
    mutable std::mutex chMu_;
    std::unordered_map<uint32_t, ChannelStats> channelStats_;  // key = (caid<<16)|sid

    // CW Cache
    mutable std::mutex cacheMu_;
    std::unordered_map<uint64_t, CwCacheEntry> cwCache_;

    // Async Ollama analyzer state
    std::atomic<bool> ollamaRunning_{false};
    std::atomic<bool> analyzeNow_{false};
    std::string ollamaReport_;
    time_t ollamaReportTime_ = 0;
    mutable std::mutex reportMu_;

    // FNV-1a hash of CAID+SID+ECM body for cache key
    static uint64_t hashEcm(uint16_t caid, uint16_t sid, const uint8_t* ecm, int len) {
        uint64_t h = 14695981039346656037ULL;
        auto feed = [&](uint8_t b) { h = (h ^ b) * 1099511628211ULL; };
        feed(caid >> 8); feed(caid & 0xFF);
        feed(sid >> 8);  feed(sid & 0xFF);
        for (int i = 0; i < len; i++) feed(ecm[i]);
        return h;
    }

    // Compute AI routing score for a server label (mu_ must be held)
    float computeScore_(uint16_t caid, const std::string& label) const {
        auto it = serverQuality_.find(label);
        if (it == serverQuality_.end()) return 40.0f;  // unknown server: below average
        const auto& sq = it->second;
        // Base: success rate (most important)
        float score = sq.success_rate * 55.0f;
        // CAID match bonus: server has proven it can decode this CA system
        for (auto c : sq.known_caids)
            if (c == caid) { score += 25.0f; break; }
        // Latency bonus: prefer faster servers (caps at 15 pts at 1ms)
        if (sq.avg_latency_ms > 0)
            score += std::min(15.0f, 15000.0f / sq.avg_latency_ms);
        // Penalty for recent consecutive failures
        score -= sq.consecutive_fails * 7.0f;
        // Ollama-assigned quality bonus
        score += sq.ai_score * 0.05f;
        return std::max(0.0f, score);
    }

    // Build server stats summary for Ollama prompt
    std::string buildServerStats_() {
        std::lock_guard<std::mutex> g(mu_);
        if (serverQuality_.empty()) return "";
        std::stringstream ss;
        ss << "=== CCcam Server Performance ===\n";
        for (auto& [name, sq] : serverQuality_) {
            if (sq.total_requests == 0) continue;
            ss << "Server: " << name << "\n";
            ss << "  Requests: " << sq.total_requests
               << " OK: " << sq.successful
               << " FAIL: " << sq.failed
               << " Rate: " << (int)(sq.success_rate * 100) << "%\n";
            ss << "  AvgLatency: " << (int)sq.avg_latency_ms << "ms";
            ss << "  ConsecFails: " << sq.consecutive_fails << "\n";
            if (!sq.known_caids.empty()) {
                ss << "  CAIDs: ";
                for (auto c : sq.known_caids)
                    ss << std::hex << std::uppercase << c << " ";
                ss << "\n";
            }
        }
        ss << "=== CA Systems Seen ===\n";
        for (auto& [caid, ci] : caidInfo_) {
            if (ci.ecm_count == 0) continue;
            int rate = ci.ecm_count > 0 ? ci.success_count * 100 / ci.ecm_count : 0;
            ss << "CAID " << std::hex << std::uppercase << caid
               << " (" << ci.name << "): "
               << ci.success_count << "/" << ci.ecm_count
               << " (" << rate << "%)\n";
        }
        return ss.str();
    }

    // Parse Ollama structured output and update ai_score fields
    void applyOllamaReport_(const std::string& report) {
        std::lock_guard<std::mutex> g(mu_);
        std::istringstream ss(report);
        std::string line;
        while (std::getline(ss, line)) {
            // RANK:server_label:score
            if (line.size() > 5 && line.substr(0,5) == "RANK:") {
                auto p1 = line.find(':', 5);
                if (p1 == std::string::npos) continue;
                std::string label = line.substr(5, p1 - 5);
                int score = 0;
                try { score = std::stoi(line.substr(p1 + 1)); } catch (...) {}
                auto it = serverQuality_.find(label);
                if (it != serverQuality_.end())
                    it->second.ai_score = std::max(0, std::min(100, score));
            }
        }
    }

    // Async Ollama analysis loop (runs in detached thread)
    void ollamaLoop() {
        int ticksLeft = 200;  // ~20s initial delay before first analysis
        while (ollamaRunning_.load()) {
            std::this_thread::sleep_for(std::chrono::milliseconds(100));
            if (!analyzeNow_.load()) {
                if (--ticksLeft > 0) continue;
            }
            analyzeNow_ = false;
            ticksLeft = 600;  // ~60s between auto-analyses

            // Enough data?
            std::string stats = buildServerStats_();
            if (stats.empty()) continue;

            // Check Ollama reachability (fast, 1s timeout)
            if (!checkOllama()) continue;

            // Build structured analysis prompt
            std::string prompt =
                "/no_think\n"
                "You are a CCcam server quality analyzer. Analyze the following server performance data:\n\n"
                + stats +
                "\nOutput ONLY lines in these exact formats, nothing else:\n"
                "RANK:[server_label]:[0-100 score]\n"
                "ISSUE:[server_label]:[brief issue, max 60 chars]\n"
                "BEST:[caid_hex]:[server_label]\n"
                "SUMMARY:[one line assessment, max 120 chars]\n"
                "Score 100=perfect, 0=dead. Penalize high fail rate and latency.";

            std::string response = queryOllama(prompt);
            if (response.empty()) continue;

            // Store report
            {
                std::lock_guard<std::mutex> g(reportMu_);
                ollamaReport_ = response;
                ollamaReportTime_ = time(nullptr);
            }
            // Apply scores back into server quality
            applyOllamaReport_(response);

            if (onLog) onLog("[AI] Analysis complete: " + std::to_string(serverQuality_.size()) + " servers scored");
        }
    }
    
    // Known CAID names database (enriched from MS-4030 firmware CA table)
    static std::string getCaidName(uint16_t caid) {
        // Exact CAID matches (from firmware CA_TABLE analysis)
        switch (caid) {
            case 0x0001: case 0x0002: case 0x0003: case 0x0004: case 0x0005:
                return "Tandberg (SECA)";
            case 0x000B: case 0x000C: case 0x000D:
                return "Tandberg (aux)";
            case 0x0015: case 0x0016:
                return "Tandberg (0x" + toHex((const uint8_t*)&caid, 2) + ")";
            case 0x0017:
                return "GetTV/BetaCrypt";
            case 0x0083: case 0x0084:
                return "Tandberg (Bulsat)";
            case 0x00C9: case 0x00CA: case 0x00CE:
                return "Tandberg (regional)";
            case 0x03E8: case 0x03E9: case 0x03EA: case 0x03EB: case 0x03EC: case 0x03ED:
                return "Tandberg (shared key)";
            case 0x04FE:
                return "Tandberg (0x04FE)";
        }
        // Range-based Tandberg IDs from firmware
        if (caid >= 0x0691 && caid <= 0x06CE) return "Tandberg (06xx)";
        if (caid >= 0x0A26 && caid <= 0x0A26) return "Tandberg (0A26)";
        if (caid >= 0x1600 && caid <= 0x16E5) return "Tandberg (16xx)";
        if (caid >= 0x1773 && caid <= 0x1778) return "Tandberg (17xx)";
        if (caid >= 0x1839 && caid <= 0x183D) return "Tandberg (18xx)";
        if (caid >= 0x1959 && caid <= 0x1959) return "Tandberg (1959)";
        if (caid >= 0x1ED5 && caid <= 0x1ED5) return "Tandberg (1ED5)";
        if (caid >= 0x2140 && caid <= 0x2140) return "Tandberg (2140)";
        if (caid >= 0x2249 && caid <= 0x224A) return "Tandberg (22xx)";
        if (caid >= 0x2705 && caid <= 0x2710) return "Tandberg (27xx)";
        if (caid >= 0x645A && caid <= 0x645A) return "Tandberg (645A)";
        // Generic family-based
        switch (caid >> 8) {
            case 0x01: return "SECA/Mediaguard";
            case 0x05: return "Viaccess";
            case 0x06: return "Irdeto";
            case 0x09: return "NDS/Videoguard";
            case 0x0B: return "Conax";
            case 0x0D: return "Cryptoworks";
            case 0x0E: return "PowerVU";
            case 0x10: return "Tandberg";
            case 0x17: return "BetaCrypt";
            case 0x18: return "Nagravision";
            case 0x22: return "Codicrypt";
            case 0x26: return "BISS";
            case 0x27: return "DreCrypt";
            case 0x4A: return "DRE-Crypt";
            case 0x55: return "BulCrypt";
            case 0x56: return "Verimatrix";
            default:
                if (caid >= 0x4AE0 && caid <= 0x4AEF) return "DRE-Crypt";
                if (caid >= 0x2600 && caid <= 0x2606) return "BISS";
                char buf[32]; snprintf(buf, sizeof(buf), "0x%04X", caid);
                return std::string("Unknown ") + buf;
        }
    }

    static std::string makeKey(uint16_t caid, uint16_t sid, const uint8_t* ecm, int len) {
        std::stringstream ss;
        ss << std::hex << caid << "_" << sid << "_";
        for (int i = 0; i < len; i++) ss << std::hex << (int)ecm[i];
        return ss.str();
    }

    static std::string toHex(const uint8_t* d, int len) {
        static const char* hex = "0123456789ABCDEF";
        std::string r;
        r.reserve(len * 2);
        for (int i = 0; i < len; i++) {
            r += hex[d[i] >> 4];
            r += hex[d[i] & 0xF];
        }
        return r;
    }

    static bool fromHex(const std::string& s, uint8_t* out, int maxLen) {
        int len = std::min((int)s.size() / 2, maxLen);
        for (int i = 0; i < len; i++) {
            int hi = hexVal(s[i*2]);
            int lo = hexVal(s[i*2+1]);
            if (hi < 0 || lo < 0) return false;
            out[i] = (uint8_t)((hi << 4) | lo);
        }
        return len > 0;
    }

    static int hexVal(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        return -1;
    }

    static std::string defaultPath() {
#ifdef _WIN32
        char buf[MAX_PATH];
        GetModuleFileNameA(nullptr, buf, MAX_PATH);
        std::string p(buf);
        size_t pos = p.rfind('\\');
        if (pos != std::string::npos) p = p.substr(0, pos + 1);
        return p + "gmscreen_ai_cw.dat";
#else
        return "gmscreen_ai_cw.dat";
#endif
    }

    bool testPort(const std::string& host, int port) {
        SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock == INVALID_SOCKET) return false;
        
        sockaddr_in addr{};
        addr.sin_family = AF_INET;
        addr.sin_port = htons((uint16_t)port);
        
        if (inet_pton(AF_INET, host.c_str(), &addr.sin_addr) != 1) {
            addrinfo hints{}, *res = nullptr;
            hints.ai_family = AF_INET;
            if (getaddrinfo(host.c_str(), nullptr, &hints, &res) != 0 || !res) {
                closesocket(sock);
                return false;
            }
            addr.sin_addr = ((sockaddr_in*)res->ai_addr)->sin_addr;
            freeaddrinfo(res);
        }
        
        DWORD timeout = 3000;
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&timeout, sizeof(timeout));
        setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, (char*)&timeout, sizeof(timeout));
        
        bool ok = (::connect(sock, (sockaddr*)&addr, sizeof(addr)) == 0);
        closesocket(sock);
        return ok;
    }

    std::string queryOllama(const std::string& prompt) {
        SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock == INVALID_SOCKET) return "";
        
        sockaddr_in addr{};
        addr.sin_family = AF_INET;
        addr.sin_port = htons((uint16_t)ollama_port);
        inet_pton(AF_INET, ollama_host.c_str(), &addr.sin_addr);
        
        DWORD timeout = 30000;
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&timeout, sizeof(timeout));
        setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, (char*)&timeout, sizeof(timeout));
        
        if (::connect(sock, (sockaddr*)&addr, sizeof(addr)) != 0) {
            closesocket(sock);
            return "";
        }
        
        // Escape JSON
        std::string escaped;
        for (char c : prompt) {
            if (c == '"') escaped += "\\\"";
            else if (c == '\\') escaped += "\\\\";
            else if (c == '\n') escaped += "\\n";
            else escaped += c;
        }
        
        std::string body = "{\"model\":\"" + model + "\",\"prompt\":\"" + escaped + "\",\"stream\":false}";
        std::string req = "POST /api/generate HTTP/1.1\r\n"
                          "Host: " + ollama_host + "\r\n"
                          "Content-Type: application/json\r\n"
                          "Content-Length: " + std::to_string(body.size()) + "\r\n"
                          "Connection: close\r\n\r\n" + body;
        
        send(sock, req.c_str(), (int)req.size(), 0);
        
        std::string response;
        char buf[4096];
        int n;
        while ((n = recv(sock, buf, sizeof(buf) - 1, 0)) > 0) {
            buf[n] = 0;
            response += buf;
        }
        closesocket(sock);
        
        // Parse response
        size_t pos = response.find("\"response\":\"");
        if (pos != std::string::npos) {
            pos += 12;
            std::string result;
            for (size_t i = pos; i < response.size(); i++) {
                if (response[i] == '\\' && i + 1 < response.size()) {
                    char next = response[i + 1];
                    if (next == 'n') { result += '\n'; i++; }
                    else if (next == '"') { result += '"'; i++; }
                    else if (next == '\\') { result += '\\'; i++; }
                    else result += response[i];
                } else if (response[i] == '"') {
                    break;
                } else {
                    result += response[i];
                }
            }
            return result;
        }
        return "";
    }
};

} // namespace ai
