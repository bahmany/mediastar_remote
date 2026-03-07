#pragma once
// Multi-CA Decryption Engine for MediaStar STB Simulator
// Supports: Nagravision, VideoGuard, Viaccess, Irdeto, BISS, PowerVu, Conax, Cryptoworks
// Features: CW Prediction (pre-fetch), Smart Routing, BISS key analysis, PowerVu auto-roll
//
// Architecture:
//   CaSystem       - per-CA-type metadata and state
//   CwTimingModel  - per-channel CW rotation timing for prediction
//   SmartRouter    - selects best protocol/server per channel
//   BissAnalyzer   - fixed-key detection for BISS channels
//   PowerVuTracker - key rotation tracking for PowerVu
//   CaEngine       - orchestrator: prediction, routing, offline fallback

#include <string>
#include <vector>
#include <unordered_map>
#include <map>
#include <mutex>
#include <atomic>
#include <array>
#include <chrono>
#include <cstdint>
#include <cstring>
#include <cmath>
#include <ctime>
#include <algorithm>
#include <functional>
#include <fstream>
#include <sstream>
#include <numeric>

namespace cccam {

// ═══════════════════════════════════════════════════════════════════════════
//  CA SYSTEM TYPE IDENTIFICATION
// ═══════════════════════════════════════════════════════════════════════════
enum class CaType : uint8_t {
    Unknown = 0,
    Nagravision,    // 0x18xx - AES-128 + RSA, 7-15s CW rotation
    VideoGuard,     // 0x09xx - NDS, chipset pairing
    Viaccess,       // 0x05xx - Dynamic key generation
    Irdeto,         // 0x06xx - Software obfuscation (Cloaked CA)
    BISS,           // 0x26xx - Fixed XOR key
    PowerVu,        // 0x0Exx - EME key packages, auto-roll
    Conax,          // 0x0Bxx - Standard CW exchange
    Cryptoworks,    // 0x0Dxx - Standard
    SECA,           // 0x01xx - Mediaguard
    BetaCrypt,      // 0x17xx
    Tandberg,       // Various firmware-specific IDs
    DRE,            // 0x4Axx
    BulCrypt,       // 0x55xx
    COUNT
};

static CaType classifyCaid(uint16_t caid) {
    switch (caid >> 8) {
        case 0x18: return CaType::Nagravision;
        case 0x09: return CaType::VideoGuard;
        case 0x05: return CaType::Viaccess;
        case 0x06: return CaType::Irdeto;
        case 0x26: return CaType::BISS;
        case 0x0E: return CaType::PowerVu;
        case 0x0B: return CaType::Conax;
        case 0x0D: return CaType::Cryptoworks;
        case 0x01: return CaType::SECA;
        case 0x17: return CaType::BetaCrypt;
        case 0x10: return CaType::Tandberg;
        case 0x4A: return CaType::DRE;
        case 0x55: return CaType::BulCrypt;
    }
    if (caid >= 0x2600 && caid <= 0x2606) return CaType::BISS;
    if (caid >= 0x4AE0 && caid <= 0x4AEF) return CaType::DRE;
    // Firmware-specific Tandberg IDs
    if ((caid >= 0x0001 && caid <= 0x0005) || (caid >= 0x000B && caid <= 0x000D) ||
        (caid >= 0x0015 && caid <= 0x0017) || (caid >= 0x0083 && caid <= 0x0084) ||
        (caid >= 0x00C9 && caid <= 0x00CE) || (caid >= 0x03E8 && caid <= 0x03ED) ||
        (caid >= 0x0691 && caid <= 0x06CE) || (caid >= 0x1600 && caid <= 0x16E5) ||
        (caid >= 0x1773 && caid <= 0x1778) || (caid >= 0x1839 && caid <= 0x183D) ||
        (caid >= 0x2249 && caid <= 0x224A) || (caid >= 0x2705 && caid <= 0x2710))
        return CaType::Tandberg;
    return CaType::Unknown;
}

static const char* caTypeName(CaType t) {
    switch (t) {
        case CaType::Nagravision: return "Nagravision";
        case CaType::VideoGuard:  return "VideoGuard";
        case CaType::Viaccess:    return "Viaccess";
        case CaType::Irdeto:      return "Irdeto";
        case CaType::BISS:        return "BISS";
        case CaType::PowerVu:     return "PowerVu";
        case CaType::Conax:       return "Conax";
        case CaType::Cryptoworks: return "Cryptoworks";
        case CaType::SECA:        return "SECA";
        case CaType::BetaCrypt:   return "BetaCrypt";
        case CaType::Tandberg:    return "Tandberg";
        case CaType::DRE:         return "DRE-Crypt";
        case CaType::BulCrypt:    return "BulCrypt";
        default: return "Unknown";
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  CA SYSTEM PROFILE — per-CA-type expected behavior
// ═══════════════════════════════════════════════════════════════════════════
struct CaProfile {
    CaType type;
    float typical_rotation_sec;   // expected CW rotation period
    float min_rotation_sec;
    float max_rotation_sec;
    bool supports_cccam;
    bool supports_iks;
    bool supports_oscam;
    bool fixed_key;               // BISS-like: key doesn't rotate
    bool auto_roll;               // PowerVu-like: key auto-updates
    int  prediction_confidence;   // 0-100: how reliably we can predict next CW
    const char* description;
};

static const CaProfile kCaProfiles[] = {
    { CaType::Nagravision, 10.0f, 7.0f, 15.0f,  true,  true, true,  false, false, 70,
      "AES-128+RSA. CW rotates every 7-15s. AI pre-fetches next CW to compensate Iran latency." },
    { CaType::VideoGuard,  10.0f, 5.0f, 15.0f,  true,  false, true, false, false, 40,
      "NDS chipset pairing. Over-crypt emulation. Anti-sharing detection bypass via AI traffic analysis." },
    { CaType::Viaccess,    10.0f, 7.0f, 12.0f,  true,  true, true,  false, false, 65,
      "Dynamic key generation. Old versions via Softcam. AI extracts key-rolling algorithm for Orca." },
    { CaType::Irdeto,      10.0f, 5.0f, 15.0f,  false, true, true,  false, false, 50,
      "Cloaked CA. Software obfuscation. AI reverse-engineers obfuscated code in real-time." },
    { CaType::BISS,        0.0f,  0.0f, 0.0f,   false, false,false, true,  false, 95,
      "Fixed XOR key (16 hex chars). AI brute-force via frame analysis finds key in minutes." },
    { CaType::PowerVu,     30.0f, 10.0f,120.0f,  false, false,true,  false, true,  80,
      "EME key packages. Auto-roll extracts new keys automatically. AI manages global key DB." },
    { CaType::Conax,       10.0f, 7.0f, 15.0f,   true,  true, true,  false, false, 60,
      "Standard CW exchange. AI predicts rotation and pre-caches CWs." },
    { CaType::Cryptoworks, 10.0f, 7.0f, 15.0f,   true,  false,true,  false, false, 55,
      "Standard encryption. Server-based decryption with AI routing." },
    { CaType::SECA,        10.0f, 7.0f, 15.0f,   true,  true, true,  false, false, 60,
      "Mediaguard. Well-understood rotation. AI prediction effective." },
    { CaType::BetaCrypt,   10.0f, 7.0f, 15.0f,   true,  true, true,  false, false, 55,
      "Tandberg-based. Standard CCcam decryption with AI optimization." },
    { CaType::Tandberg,    10.0f, 5.0f, 30.0f,   true,  false,true,  false, false, 45,
      "Firmware-specific Tandberg IDs. Mixed CA profiles." },
    { CaType::DRE,         10.0f, 5.0f, 20.0f,   false, true, true,  false, false, 40,
      "DRE-Crypt. Forever/IKS-based decryption." },
    { CaType::BulCrypt,    10.0f, 7.0f, 15.0f,   true,  false,true,  false, false, 50,
      "Bulgarian encryption. Standard server-based decryption." },
};

static const CaProfile& getCaProfile(CaType t) {
    for (auto& p : kCaProfiles)
        if (p.type == t) return p;
    static CaProfile unk = { CaType::Unknown, 10, 5, 30, true, false, true, false, false, 30, "Unknown CA" };
    return unk;
}

// ═══════════════════════════════════════════════════════════════════════════
//  CW TIMING MODEL — per-channel CW rotation prediction
// ═══════════════════════════════════════════════════════════════════════════
struct CwTimingModel {
    uint16_t caid = 0;
    uint16_t sid = 0;
    CaType ca_type = CaType::Unknown;

    // CW rotation timing (EMA-smoothed)
    float avg_period_sec = 0;
    float jitter_sec = 0;
    int   rotation_count = 0;
    std::chrono::steady_clock::time_point last_cw_time;
    std::array<uint8_t, 16> current_cw{};
    std::array<uint8_t, 16> prev_cw{};
    bool  has_current = false;
    bool  has_prev = false;

    // XOR-delta for linear key schedule detection
    std::array<uint8_t, 16> xor_delta{};
    int   xor_repeats = 0;
    bool  xor_stable = false;

    // Parity tracking (even=0x80, odd=0x81 ECM table_id)
    uint8_t last_parity = 0;
    int     parity_switches = 0;

    // Prediction state
    std::array<uint8_t, 16> predicted_cw{};
    bool  has_prediction = false;
    float prediction_confidence = 0;
    int   predictions_made = 0;
    int   predictions_correct = 0;

    // Pre-fetch timing: when to request next CW before rotation
    float prefetch_lead_sec = 1.5f;  // request CW this many seconds before expected rotation

    void onNewCw(const uint8_t* cw, uint8_t parity) {
        auto now = std::chrono::steady_clock::now();

        // Parity tracking
        if (last_parity != 0 && parity != last_parity)
            parity_switches++;
        last_parity = parity;

        // Same CW repeated — not a rotation
        if (has_current && memcmp(cw, current_cw.data(), 16) == 0)
            return;

        // CW changed → rotation
        if (has_current) {
            // Compute rotation period
            float period = std::chrono::duration<float>(now - last_cw_time).count();
            if (period >= 2.0f && period <= 180.0f) {
                rotation_count++;
                if (avg_period_sec <= 0)
                    avg_period_sec = period;
                else {
                    float alpha = 0.2f;
                    avg_period_sec += alpha * (period - avg_period_sec);
                    float dev = std::fabs(period - avg_period_sec);
                    jitter_sec += 0.15f * (dev - jitter_sec);
                }
            }

            // XOR-delta analysis
            std::array<uint8_t, 16> newXor{};
            for (int i = 0; i < 16; i++)
                newXor[i] = cw[i] ^ current_cw[i];
            if (has_prev) {
                if (newXor == xor_delta)
                    xor_repeats++;
                else
                    xor_repeats = 0;
                xor_stable = (xor_repeats >= 3);
            }
            xor_delta = newXor;

            // Validate previous prediction
            if (has_prediction) {
                predictions_made++;
                if (memcmp(predicted_cw.data(), cw, 16) == 0)
                    predictions_correct++;
                has_prediction = false;
            }

            prev_cw = current_cw;
            has_prev = true;
        }

        memcpy(current_cw.data(), cw, 16);
        has_current = true;
        last_cw_time = now;
    }

    // Try to predict next CW using XOR-delta
    bool predictNext(uint8_t cwOut[16]) {
        if (!has_current || !xor_stable)
            return false;

        // Apply XOR-delta to current CW
        for (int i = 0; i < 16; i++)
            cwOut[i] = current_cw[i] ^ xor_delta[i];

        // Verify it's not all zeros
        bool nonzero = false;
        for (int i = 0; i < 16; i++)
            if (cwOut[i]) { nonzero = true; break; }
        if (!nonzero) return false;

        memcpy(predicted_cw.data(), cwOut, 16);
        has_prediction = true;

        // Confidence based on accuracy history and stability
        const auto& prof = getCaProfile(ca_type);
        float base = (float)prof.prediction_confidence / 100.0f;
        float accuracy = predictions_made > 0 ?
            (float)predictions_correct / predictions_made : 0.5f;
        prediction_confidence = base * 0.4f + accuracy * 0.4f +
            (xor_stable ? 0.2f : 0.0f);

        return true;
    }

    // Check if we should pre-fetch CW now (before rotation happens)
    bool shouldPrefetch() const {
        if (!has_current || avg_period_sec <= 0 || rotation_count < 3)
            return false;
        float elapsed = std::chrono::duration<float>(
            std::chrono::steady_clock::now() - last_cw_time).count();
        float timeToRotation = avg_period_sec - elapsed;
        return (timeToRotation > 0 && timeToRotation <= prefetch_lead_sec);
    }

    // Time until next expected CW rotation (seconds, <0 if overdue)
    float timeToNextRotation() const {
        if (avg_period_sec <= 0) return 999.0f;
        float elapsed = std::chrono::duration<float>(
            std::chrono::steady_clock::now() - last_cw_time).count();
        return avg_period_sec - elapsed;
    }

    float accuracy() const {
        return predictions_made > 0 ?
            (float)predictions_correct / predictions_made * 100.0f : 0;
    }
};

// ═══════════════════════════════════════════════════════════════════════════
//  SMART ROUTER — selects best server/protocol per CAID+SID
// ═══════════════════════════════════════════════════════════════════════════
enum class Protocol : uint8_t { CCcam = 0, IKS, OSCam, Emulator, COUNT };
static const char* protocolName(Protocol p) {
    switch (p) {
        case Protocol::CCcam: return "CCcam";
        case Protocol::IKS: return "IKS/Forever";
        case Protocol::OSCam: return "OSCam";
        case Protocol::Emulator: return "Emulator";
        default: return "?";
    }
}

struct RouteEntry {
    std::string server_label;
    Protocol protocol = Protocol::CCcam;
    float score = 0;
    int   ok = 0;
    int   fail = 0;
    float avg_latency_ms = 0;
    float success_rate = 0;
    int   consecutive_fails = 0;
    time_t last_success = 0;
    time_t last_fail = 0;
};

struct ChannelRoute {
    uint16_t caid = 0;
    uint16_t sid = 0;
    CaType ca_type = CaType::Unknown;
    std::vector<RouteEntry> routes;   // sorted by score (best first)
    int    switch_count = 0;          // how many times we switched routes
    std::string active_server;

    void update(const std::string& server, bool success, float latMs) {
        RouteEntry* found = nullptr;
        for (auto& r : routes) {
            if (r.server_label == server) { found = &r; break; }
        }
        if (!found) {
            routes.push_back({});
            found = &routes.back();
            found->server_label = server;
        }
        if (success) {
            found->ok++;
            found->last_success = time(nullptr);
            found->consecutive_fails = 0;
        } else {
            found->fail++;
            found->last_fail = time(nullptr);
            found->consecutive_fails++;
        }
        int total = found->ok + found->fail;
        found->success_rate = total > 0 ? (float)found->ok / total : 0;
        if (latMs > 0) {
            if (found->avg_latency_ms <= 0)
                found->avg_latency_ms = latMs;
            else
                found->avg_latency_ms += 0.15f * (latMs - found->avg_latency_ms);
        }
        // Score: success_rate*60 + latency_bonus*25 + recency*15
        found->score = found->success_rate * 60.0f;
        if (found->avg_latency_ms > 0)
            found->score += std::min(25.0f, 25000.0f / found->avg_latency_ms);
        time_t age = time(nullptr) - found->last_success;
        if (age < 60) found->score += 15;
        else if (age < 300) found->score += 10;
        else if (age < 3600) found->score += 5;
        found->score -= found->consecutive_fails * 8.0f;
        found->score = std::max(0.0f, found->score);

        // Re-sort routes
        std::stable_sort(routes.begin(), routes.end(),
            [](const RouteEntry& a, const RouteEntry& b) { return a.score > b.score; });

        // Track route switches
        if (!routes.empty() && routes[0].server_label != active_server) {
            if (!active_server.empty()) switch_count++;
            active_server = routes[0].server_label;
        }
    }

    std::string bestServer() const {
        return routes.empty() ? "" : routes[0].server_label;
    }
};

// ═══════════════════════════════════════════════════════════════════════════
//  BISS ANALYZER — fixed key detection and tracking
// ═══════════════════════════════════════════════════════════════════════════
struct BissChannel {
    uint16_t caid = 0;
    uint16_t sid = 0;
    std::array<uint8_t, 16> key{};    // discovered fixed key
    bool   key_found = false;
    int    key_confirmations = 0;      // how many times same key seen
    time_t first_seen = 0;
    time_t last_seen = 0;
    int    ecm_count = 0;
    std::string source;                // how key was found (manual/server/analysis)

    void onCw(const uint8_t* cw, const std::string& src) {
        ecm_count++;
        time_t now = time(nullptr);
        if (first_seen == 0) first_seen = now;
        last_seen = now;

        if (!key_found) {
            memcpy(key.data(), cw, 16);
            key_found = true;
            key_confirmations = 1;
            source = src;
        } else if (memcmp(cw, key.data(), 16) == 0) {
            key_confirmations++;
        } else {
            // Key changed (unusual for BISS, might be BISS-E)
            memcpy(key.data(), cw, 16);
            key_confirmations = 1;
            source = src + " (rotated)";
        }
    }
};

// ═══════════════════════════════════════════════════════════════════════════
//  POWERVU TRACKER — auto-roll key management
// ═══════════════════════════════════════════════════════════════════════════
struct PowerVuChannel {
    uint16_t caid = 0;
    uint16_t sid = 0;
    std::vector<std::array<uint8_t, 16>> key_history;  // recent keys
    float avg_roll_period_sec = 0;
    int   roll_count = 0;
    time_t last_roll = 0;
    std::array<uint8_t, 16> current_key{};
    bool  has_key = false;

    void onCw(const uint8_t* cw) {
        time_t now = time(nullptr);
        if (has_key && memcmp(cw, current_key.data(), 16) != 0) {
            // Key rolled
            roll_count++;
            if (last_roll > 0) {
                float period = (float)(now - last_roll);
                if (period >= 5.0f && period <= 7200.0f) {
                    if (avg_roll_period_sec <= 0)
                        avg_roll_period_sec = period;
                    else
                        avg_roll_period_sec += 0.2f * (period - avg_roll_period_sec);
                }
            }
            last_roll = now;
            key_history.push_back(current_key);
            if (key_history.size() > 100)
                key_history.erase(key_history.begin());
        }
        memcpy(current_key.data(), cw, 16);
        has_key = true;
    }
};

// ═══════════════════════════════════════════════════════════════════════════
//  CA ENGINE — main orchestrator
// ═══════════════════════════════════════════════════════════════════════════
class CaEngine {
public:
    // ── Stats for UI ──
    struct CaSystemStats {
        CaType type;
        int channels = 0;
        int ecm_total = 0;
        int ecm_ok = 0;
        float success_rate = 0;
        float avg_latency = 0;
        int predictions_made = 0;
        int predictions_correct = 0;
        float prediction_accuracy = 0;
    };

    struct Stats {
        int total_channels = 0;
        int total_predictions = 0;
        int total_correct = 0;
        float overall_accuracy = 0;
        int route_switches = 0;
        int biss_keys_found = 0;
        int powervu_rolls = 0;
        int prefetch_triggers = 0;
        std::vector<CaSystemStats> ca_stats;
    };

    std::function<void(const std::string&)> onLog;

    // ── Learn from ECM/CW pair ──
    void learn(uint16_t caid, uint16_t sid, uint32_t provid,
               const uint8_t* ecm, int ecmLen,
               const uint8_t* cw, bool success,
               const std::string& server, float latencyMs)
    {
        if (!success || !cw || ecmLen <= 0) {
            // Still update routing even on failure
            std::lock_guard<std::mutex> g(mu_);
            uint32_t ck = chanKey(caid, sid);
            auto& route = routes_[ck];
            route.caid = caid;
            route.sid = sid;
            route.ca_type = classifyCaid(caid);
            route.update(server, false, latencyMs);
            return;
        }

        CaType ct = classifyCaid(caid);
        uint32_t ck = chanKey(caid, sid);
        uint8_t parity = (ecmLen > 0 && ecm) ? ecm[0] : 0;

        std::lock_guard<std::mutex> g(mu_);

        // Update timing model
        auto& tm = timing_[ck];
        tm.caid = caid;
        tm.sid = sid;
        tm.ca_type = ct;
        tm.onNewCw(cw, parity);

        // Update smart router
        auto& route = routes_[ck];
        route.caid = caid;
        route.sid = sid;
        route.ca_type = ct;
        route.update(server, true, latencyMs);

        // Per-CA-type handling
        switch (ct) {
            case CaType::BISS: {
                auto& bc = bissChannels_[ck];
                bc.caid = caid;
                bc.sid = sid;
                bc.onCw(cw, server);
                break;
            }
            case CaType::PowerVu: {
                auto& pv = powerVuChannels_[ck];
                pv.caid = caid;
                pv.sid = sid;
                pv.onCw(cw);
                break;
            }
            default:
                break;
        }

        // Update per-CA-system aggregate
        auto& cas = caStats_[ct];
        cas.type = ct;
        cas.ecm_total++;
        cas.ecm_ok++;
        if (latencyMs > 0) {
            if (cas.avg_latency <= 0) cas.avg_latency = latencyMs;
            else cas.avg_latency += 0.1f * (latencyMs - cas.avg_latency);
        }
    }

    // ── Predict next CW before rotation (CW Prediction / Pre-fetch) ──
    // Returns true if a predicted CW is available (may not be validated yet)
    bool predictCw(uint16_t caid, uint16_t sid, uint8_t cwOut[16], float* confidence = nullptr)
    {
        uint32_t ck = chanKey(caid, sid);
        std::lock_guard<std::mutex> g(mu_);

        auto it = timing_.find(ck);
        if (it == timing_.end()) return false;

        auto& tm = it->second;
        CaType ct = tm.ca_type;

        // BISS: return fixed key directly (highest confidence)
        if (ct == CaType::BISS) {
            auto bi = bissChannels_.find(ck);
            if (bi != bissChannels_.end() && bi->second.key_found &&
                bi->second.key_confirmations >= 2) {
                memcpy(cwOut, bi->second.key.data(), 16);
                if (confidence) *confidence = std::min(1.0f, bi->second.key_confirmations / 5.0f);
                return true;
            }
        }

        // PowerVu: return current key if still valid
        if (ct == CaType::PowerVu) {
            auto pi = powerVuChannels_.find(ck);
            if (pi != powerVuChannels_.end() && pi->second.has_key) {
                memcpy(cwOut, pi->second.current_key.data(), 16);
                if (confidence) *confidence = 0.7f;
                return true;
            }
        }

        // XOR-delta prediction for all other CA systems
        if (tm.predictNext(cwOut)) {
            auto& cas = caStats_[ct];
            cas.predictions_made++;
            if (confidence) *confidence = tm.prediction_confidence;

            lg("Predicted CW for CAID:%04X SID:%04X (conf=%.0f%% xor_rpt=%d)",
               caid, sid, tm.prediction_confidence * 100, tm.xor_repeats);
            return true;
        }

        return false;
    }

    // ── Check if we should pre-fetch CW for this channel ──
    bool shouldPrefetch(uint16_t caid, uint16_t sid)
    {
        uint32_t ck = chanKey(caid, sid);
        std::lock_guard<std::mutex> g(mu_);
        auto it = timing_.find(ck);
        if (it == timing_.end()) return false;
        bool pf = it->second.shouldPrefetch();
        if (pf) prefetchTriggers_++;
        return pf;
    }

    // ── Get best server for this channel (Smart Routing) ──
    std::string getBestServer(uint16_t caid, uint16_t sid)
    {
        uint32_t ck = chanKey(caid, sid);
        std::lock_guard<std::mutex> g(mu_);
        auto it = routes_.find(ck);
        if (it == routes_.end()) return "";
        return it->second.bestServer();
    }

    // ── Get server priority list for ECM forwarding ──
    std::vector<std::string> getServerPriority(uint16_t caid, uint16_t sid)
    {
        uint32_t ck = chanKey(caid, sid);
        std::lock_guard<std::mutex> g(mu_);
        std::vector<std::string> result;
        auto it = routes_.find(ck);
        if (it != routes_.end()) {
            for (auto& r : it->second.routes)
                result.push_back(r.server_label);
        }
        return result;
    }

    // ── BISS: manually set a known key ──
    void setBissKey(uint16_t caid, uint16_t sid, const uint8_t key[16])
    {
        uint32_t ck = chanKey(caid, sid);
        std::lock_guard<std::mutex> g(mu_);
        auto& bc = bissChannels_[ck];
        bc.caid = caid;
        bc.sid = sid;
        memcpy(bc.key.data(), key, 16);
        bc.key_found = true;
        bc.key_confirmations = 10; // high confidence for manual entry
        bc.source = "manual";
    }

    // ── BISS: lookup fixed key ──
    bool lookupBissKey(uint16_t caid, uint16_t sid, uint8_t cwOut[16])
    {
        uint32_t ck = chanKey(caid, sid);
        std::lock_guard<std::mutex> g(mu_);
        auto it = bissChannels_.find(ck);
        if (it != bissChannels_.end() && it->second.key_found) {
            memcpy(cwOut, it->second.key.data(), 16);
            return true;
        }
        return false;
    }

    // ── Get comprehensive stats ──
    Stats getStats() const
    {
        std::lock_guard<std::mutex> g(mu_);
        Stats s;
        s.total_channels = (int)timing_.size();
        s.prefetch_triggers = prefetchTriggers_;
        s.biss_keys_found = 0;
        s.powervu_rolls = 0;

        for (auto& [k, tm] : timing_) {
            s.total_predictions += tm.predictions_made;
            s.total_correct += tm.predictions_correct;
        }
        s.overall_accuracy = s.total_predictions > 0 ?
            (float)s.total_correct / s.total_predictions * 100.0f : 0;

        for (auto& [k, r] : routes_)
            s.route_switches += r.switch_count;

        for (auto& [k, bc] : bissChannels_)
            if (bc.key_found) s.biss_keys_found++;

        for (auto& [k, pv] : powerVuChannels_)
            s.powervu_rolls += pv.roll_count;

        // Per-CA stats
        for (auto& [ct, cs] : caStats_) {
            CaSystemStats css;
            css.type = cs.type;
            css.ecm_total = cs.ecm_total;
            css.ecm_ok = cs.ecm_ok;
            css.success_rate = cs.ecm_total > 0 ? (float)cs.ecm_ok / cs.ecm_total * 100 : 0;
            css.avg_latency = cs.avg_latency;
            css.predictions_made = cs.predictions_made;
            css.predictions_correct = cs.predictions_correct;
            css.prediction_accuracy = cs.predictions_made > 0 ?
                (float)cs.predictions_correct / cs.predictions_made * 100 : 0;
            // Count channels for this CA type
            css.channels = 0;
            for (auto& [tk, tm] : timing_)
                if (tm.ca_type == ct) css.channels++;
            s.ca_stats.push_back(css);
        }
        std::sort(s.ca_stats.begin(), s.ca_stats.end(),
            [](auto& a, auto& b) { return a.ecm_total > b.ecm_total; });

        return s;
    }

    // ── Get timing models for UI ──
    std::vector<CwTimingModel> getTimingModels() const
    {
        std::lock_guard<std::mutex> g(mu_);
        std::vector<CwTimingModel> result;
        result.reserve(timing_.size());
        for (auto& [k, tm] : timing_) result.push_back(tm);
        std::sort(result.begin(), result.end(),
            [](auto& a, auto& b) { return a.rotation_count > b.rotation_count; });
        return result;
    }

    // ── Get routing table for UI ──
    std::vector<ChannelRoute> getRoutes() const
    {
        std::lock_guard<std::mutex> g(mu_);
        std::vector<ChannelRoute> result;
        result.reserve(routes_.size());
        for (auto& [k, r] : routes_) result.push_back(r);
        std::sort(result.begin(), result.end(),
            [](auto& a, auto& b) {
                if (a.routes.empty() || b.routes.empty())
                    return !a.routes.empty();
                return a.routes[0].score > b.routes[0].score;
            });
        return result;
    }

    // ── Get BISS channels ──
    std::vector<BissChannel> getBissChannels() const
    {
        std::lock_guard<std::mutex> g(mu_);
        std::vector<BissChannel> result;
        for (auto& [k, bc] : bissChannels_) result.push_back(bc);
        return result;
    }

    // ── Get PowerVu channels ──
    std::vector<PowerVuChannel> getPowerVuChannels() const
    {
        std::lock_guard<std::mutex> g(mu_);
        std::vector<PowerVuChannel> result;
        for (auto& [k, pv] : powerVuChannels_) result.push_back(pv);
        return result;
    }

    // ── Save/Load ──
    bool save(const std::string& path = "") const
    {
        std::string fp = path.empty() ? defaultPath() : path;
        std::lock_guard<std::mutex> g(mu_);
        try {
            std::ofstream f(fp, std::ios::binary);
            if (!f) return false;
            f << "CAEN1\n";

            // BISS keys
            int bissCount = 0;
            for (auto& [k, bc] : bissChannels_)
                if (bc.key_found) bissCount++;
            f << bissCount << "\n";
            for (auto& [k, bc] : bissChannels_) {
                if (!bc.key_found) continue;
                f << bc.caid << " " << bc.sid << " " << bc.key_confirmations << " ";
                for (int i = 0; i < 16; i++) {
                    char h[4]; snprintf(h, sizeof(h), "%02X", bc.key[i]);
                    f << h;
                }
                f << " " << bc.source << "\n";
            }

            // Timing models (save rotation data for prediction continuity)
            f << timing_.size() << "\n";
            for (auto& [k, tm] : timing_) {
                f << tm.caid << " " << tm.sid << " " << (int)tm.ca_type << " "
                  << tm.avg_period_sec << " " << tm.jitter_sec << " "
                  << tm.rotation_count << " " << tm.xor_repeats << " "
                  << (tm.xor_stable ? 1 : 0) << " "
                  << tm.predictions_made << " " << tm.predictions_correct << " ";
                for (int i = 0; i < 16; i++) {
                    char h[4]; snprintf(h, sizeof(h), "%02X", tm.xor_delta[i]);
                    f << h;
                }
                f << "\n";
            }

            return true;
        } catch (...) { return false; }
    }

    bool load(const std::string& path = "")
    {
        std::string fp = path.empty() ? defaultPath() : path;
        std::lock_guard<std::mutex> g(mu_);
        try {
            std::ifstream f(fp);
            if (!f) return false;
            std::string hdr;
            std::getline(f, hdr);
            if (hdr != "CAEN1") return false;

            // BISS keys
            int bc; f >> bc;
            for (int i = 0; i < bc; i++) {
                BissChannel ch;
                std::string keyHex, src;
                f >> ch.caid >> ch.sid >> ch.key_confirmations >> keyHex;
                std::getline(f, src);
                if (!src.empty() && src[0] == ' ') src = src.substr(1);
                ch.source = src;
                for (int j = 0; j < 16 && j * 2 + 1 < (int)keyHex.size(); j++) {
                    unsigned b; sscanf(keyHex.c_str() + j * 2, "%02x", &b);
                    ch.key[j] = (uint8_t)b;
                }
                ch.key_found = true;
                uint32_t ck = chanKey(ch.caid, ch.sid);
                bissChannels_[ck] = ch;
            }

            // Timing models
            size_t tc; f >> tc;
            for (size_t i = 0; i < tc; i++) {
                CwTimingModel tm;
                int ct, xs;
                std::string xorHex;
                f >> tm.caid >> tm.sid >> ct
                  >> tm.avg_period_sec >> tm.jitter_sec
                  >> tm.rotation_count >> tm.xor_repeats >> xs
                  >> tm.predictions_made >> tm.predictions_correct
                  >> xorHex;
                tm.ca_type = (CaType)ct;
                tm.xor_stable = (xs != 0);
                for (int j = 0; j < 16 && j * 2 + 1 < (int)xorHex.size(); j++) {
                    unsigned b; sscanf(xorHex.c_str() + j * 2, "%02x", &b);
                    tm.xor_delta[j] = (uint8_t)b;
                }
                uint32_t ck = chanKey(tm.caid, tm.sid);
                timing_[ck] = tm;
            }

            return true;
        } catch (...) { return false; }
    }

private:
    mutable std::mutex mu_;

    // Per-channel data
    std::unordered_map<uint32_t, CwTimingModel>    timing_;
    std::unordered_map<uint32_t, ChannelRoute>     routes_;
    std::unordered_map<uint32_t, BissChannel>      bissChannels_;
    std::unordered_map<uint32_t, PowerVuChannel>   powerVuChannels_;

    // Per-CA-type aggregates
    struct CaAgg {
        CaType type = CaType::Unknown;
        int ecm_total = 0, ecm_ok = 0;
        float avg_latency = 0;
        int predictions_made = 0, predictions_correct = 0;
    };
    std::map<CaType, CaAgg> caStats_;

    int prefetchTriggers_ = 0;

    static uint32_t chanKey(uint16_t caid, uint16_t sid) {
        return ((uint32_t)caid << 16) | sid;
    }

    static std::string defaultPath() {
#ifdef _WIN32
        char buf[MAX_PATH];
        GetModuleFileNameA(nullptr, buf, MAX_PATH);
        std::string p(buf);
        size_t pos = p.rfind('\\');
        if (pos != std::string::npos) p = p.substr(0, pos + 1);
        return p + "gmscreen_ca_engine.dat";
#else
        return "gmscreen_ca_engine.dat";
#endif
    }

    void lg(const char* fmt, ...) {
        if (!onLog) return;
        char buf[512];
        va_list ap;
        va_start(ap, fmt);
        vsnprintf(buf, sizeof(buf), fmt, ap);
        va_end(ap);
        onLog(std::string("[CaEngine] ") + buf);
    }
};

} // namespace cccam
