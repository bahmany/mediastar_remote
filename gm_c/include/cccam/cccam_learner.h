#pragma once
// CCcam ECM/CW Learning Engine
// Stores ECM->CW mappings and learns patterns for CW prediction.
// Uses a simple hash-based lookup + statistical frequency analysis.
#include <string>
#include <vector>
#include <unordered_map>
#include <set>
#include <mutex>
#include <fstream>
#include <sstream>
#include <cstdint>
#include <ctime>
#include <algorithm>
#include <array>
#ifdef _WIN32
#include <windows.h>
#endif

namespace cccam {

// ECM/CW training sample
struct EcmCwSample {
    uint16_t caid = 0;
    uint32_t provid = 0;
    uint16_t sid = 0;
    std::string ecm_hex;       // full ECM body hex
    std::string cw_hex;        // 16-byte CW hex (empty if failed)
    std::string server;
    time_t      timestamp = 0;
    bool        success = false;
};

// Learning engine — stores known ECM->CW mappings and statistical patterns
class CwLearner {
public:
    struct Prediction {
        bool ok = false;
        bool exact = false;
        int  votes = 0; // pattern vote count
        std::array<uint8_t, 16> cw{};
        std::string cw_hex;
    };

    struct ServerStats {
        int total = 0;
        int ok = 0;
        int fail = 0;
        time_t last_ok = 0;
        time_t last_fail = 0;
    };

    // Stats
    struct Stats {
        int total_samples = 0;
        int unique_ecms = 0;
        int unique_cws = 0;
        int predictions = 0;
        int pred_used = 0;
        int pred_exact = 0;
        int pred_pattern = 0;
        int pred_correct = 0;
        int pred_wrong = 0;
        int pred_unverified = 0;
        float pred_accuracy = 0.0f;
        int servers = 0;
    };

    // Add a training sample (ECM + CW pair)
    void addSample(uint16_t caid, uint32_t provid, uint16_t sid,
                   const uint8_t* ecmBody, int ecmLen,
                   bool gotCw, const uint8_t cw[16],
                   const std::string& server)
    {
        EcmCwSample s;
        s.caid = caid;
        s.provid = provid;
        s.sid = sid;
        s.ecm_hex = toHex(ecmBody, ecmLen);
        if (gotCw) s.cw_hex = toHex(cw, 16);
        s.server = server;
        s.timestamp = time(nullptr);
        s.success = gotCw;

        std::lock_guard<std::mutex> g(mu_);
        totalSamples_++;

        if (gotCw) {
            // Store ECM->CW mapping
            std::string key = makeKey(caid, provid, sid, ecmBody, ecmLen);
            cwMap_[key] = s.cw_hex;
            uniqueEcms_.insert(key);
            uniqueCws_.insert(s.cw_hex);

            // Store per-CAID/SID pattern: first 8 bytes of ECM -> CW
            std::string patKey = makePatternKey(caid, sid, ecmBody, std::min(ecmLen, 8));
            auto& hist = patternHist_[patKey];
            hist[s.cw_hex]++;
        }

        // Update server stats
        auto& ss = serverStats_[server];
        ss.total++;
        if (gotCw) { ss.ok++; ss.last_ok = s.timestamp; }
        else { ss.fail++; ss.last_fail = s.timestamp; }

        // Append to training log + recent cache
        appendLog(s);
        appendTrainCsv(s);
        maybeAutoSave();
    }

    // Try to predict CW from ECM (returns Prediction with confidence metadata)
    Prediction predict(uint16_t caid, uint32_t provid, uint16_t sid,
                       const uint8_t* ecmBody, int ecmLen)
    {
        Prediction p;
        std::lock_guard<std::mutex> g(mu_);
        pred_requests_++;

        // 1. Exact match lookup
        std::string key = makeKey(caid, provid, sid, ecmBody, ecmLen);
        auto it = cwMap_.find(key);
        if (it != cwMap_.end()) {
            if (fromHex(it->second, p.cw.data(), 16)) {
                p.ok = true;
                p.exact = true;
                p.votes = 0;
                p.cw_hex = it->second;
                return p;
            }
        }

        // 2. Pattern-based prediction (most common CW for this ECM prefix)
        std::string patKey = makePatternKey(caid, sid, ecmBody, std::min(ecmLen, 8));
        auto pit = patternHist_.find(patKey);
        if (pit != patternHist_.end() && !pit->second.empty()) {
            // Find most frequent CW for this pattern
            std::string bestCw;
            int bestCount = 0;
            for (auto& [cw, cnt] : pit->second) {
                if (cnt > bestCount) { bestCount = cnt; bestCw = cw; }
            }
            if (bestCount > 0 && fromHex(bestCw, p.cw.data(), 16)) {
                p.ok = true;
                p.exact = false;
                p.votes = bestCount;
                p.cw_hex = bestCw;
                return p;
            }
        }

        return p;
    }

    void markPredictionUsed(const Prediction& p) {
        std::lock_guard<std::mutex> g(mu_);
        pred_used_++;
        if (p.exact) pred_exact_++;
        else pred_pattern_++;
    }

    void reportPredictionResult(bool matched) {
        std::lock_guard<std::mutex> g(mu_);
        if (matched) pred_correct_++;
        else pred_wrong_++;
    }

    void reportPredictionUnverified() {
        std::lock_guard<std::mutex> g(mu_);
        pred_unverified_++;
    }

    Stats getStats() const {
        std::lock_guard<std::mutex> g(mu_);
        Stats st;
        st.total_samples = totalSamples_;
        st.unique_ecms = (int)uniqueEcms_.size();
        st.unique_cws = (int)uniqueCws_.size();
        st.predictions = pred_requests_;
        st.pred_used = pred_used_;
        st.pred_exact = pred_exact_;
        st.pred_pattern = pred_pattern_;
        st.pred_correct = pred_correct_;
        st.pred_wrong = pred_wrong_;
        st.pred_unverified = pred_unverified_;
        st.pred_accuracy = pred_used_ > 0 ? (float)pred_correct_ / pred_used_ * 100.0f : 0.0f;
        st.servers = (int)serverStats_.size();
        return st;
    }

    int minPatternVotes() const {
        std::lock_guard<std::mutex> g(mu_);
        return min_pattern_votes_;
    }

    void setMinPatternVotes(int v) {
        std::lock_guard<std::mutex> g(mu_);
        min_pattern_votes_ = std::max(1, v);
    }

    // Save learned data to binary file
    bool save(const std::string& path = "") const {
        std::string fp = path.empty() ? defaultPath() : path;
        std::lock_guard<std::mutex> g(mu_);
        try {
            std::ofstream f(fp, std::ios::trunc);
            if (!f.is_open()) return false;
            f << "CWLEARN2\n";
            f << totalSamples_ << " " << pred_requests_ << " " << pred_used_ << " "
              << pred_correct_ << " " << pred_wrong_ << " " << pred_unverified_ << " "
              << min_pattern_votes_ << "\n";
            f << cwMap_.size() << "\n";
            for (auto& [k, v] : cwMap_)
                f << k << " " << v << "\n";
            f << patternHist_.size() << "\n";
            for (auto& [pk, hist] : patternHist_) {
                f << pk << " " << hist.size() << "\n";
                for (auto& [cw, cnt] : hist)
                    f << cw << " " << cnt << "\n";
            }
            f.close();
            return true;
        } catch (...) { return false; }
    }

    // Load learned data from file
    bool load(const std::string& path = "") {
        std::string fp = path.empty() ? defaultPath() : path;
        std::lock_guard<std::mutex> g(mu_);
        try {
            std::ifstream f(fp);
            if (!f.is_open()) return false;
            uniqueEcms_.clear();
            uniqueCws_.clear();
            serverStats_.clear();
            recentSamples_.clear();
            pred_exact_ = 0;
            pred_pattern_ = 0;
            std::string hdr;
            std::getline(f, hdr);
            if (hdr == "CWLEARN1") {
                f >> totalSamples_ >> pred_requests_ >> pred_correct_;
                pred_used_ = pred_correct_;
                pred_wrong_ = 0;
                pred_unverified_ = 0;
                min_pattern_votes_ = 5;
            } else if (hdr == "CWLEARN2") {
                f >> totalSamples_ >> pred_requests_ >> pred_used_ >> pred_correct_ >> pred_wrong_ >> pred_unverified_ >> min_pattern_votes_;
            } else {
                return false;
            }
            size_t mapSz;
            f >> mapSz;
            cwMap_.clear();
            for (size_t i = 0; i < mapSz; i++) {
                std::string k, v;
                f >> k >> v;
                cwMap_[k] = v;
                uniqueEcms_.insert(k);
                uniqueCws_.insert(v);
            }
            size_t patSz;
            f >> patSz;
            patternHist_.clear();
            for (size_t i = 0; i < patSz; i++) {
                std::string pk;
                size_t histSz;
                f >> pk >> histSz;
                auto& hist = patternHist_[pk];
                for (size_t j = 0; j < histSz; j++) {
                    std::string cw;
                    int cnt;
                    f >> cw >> cnt;
                    hist[cw] = cnt;
                }
            }
            f.close();
            return true;
        } catch (...) { return false; }
    }

    // Get recent samples for display
    std::vector<EcmCwSample> getRecentSamples(int maxN = 50) const {
        std::lock_guard<std::mutex> g(mu_);
        if (maxN <= 0 || (int)recentSamples_.size() <= maxN) return recentSamples_;
        return std::vector<EcmCwSample>(recentSamples_.end() - maxN, recentSamples_.end());
    }

    std::vector<std::pair<std::string, ServerStats>> getServerStats() const {
        std::lock_guard<std::mutex> g(mu_);
        std::vector<std::pair<std::string, ServerStats>> out;
        out.reserve(serverStats_.size());
        for (const auto& kv : serverStats_) out.push_back(kv);
        std::sort(out.begin(), out.end(), [](const auto& a, const auto& b){
            return a.second.total > b.second.total;
        });
        return out;
    }

private:
    mutable std::mutex mu_;

    // ECM (full hash) -> CW hex
    std::unordered_map<std::string, std::string> cwMap_;
    // ECM pattern (caid+sid+prefix) -> {CW -> count}
    std::unordered_map<std::string, std::unordered_map<std::string, int>> patternHist_;
    // Unique tracking
    std::set<std::string> uniqueEcms_;
    std::set<std::string> uniqueCws_;

    int totalSamples_ = 0;
    int pred_requests_ = 0;
    int pred_used_ = 0;
    int pred_exact_ = 0;
    int pred_pattern_ = 0;
    int pred_correct_ = 0;
    int pred_wrong_ = 0;
    int pred_unverified_ = 0;
    int min_pattern_votes_ = 5;
    time_t last_save_ = 0;
    int samples_since_save_ = 0;

    // Recent samples for UI display
    std::vector<EcmCwSample> recentSamples_;
    std::unordered_map<std::string, ServerStats> serverStats_;

    static std::string defaultPath() {
#ifdef _WIN32
        char buf[MAX_PATH] = {};
        GetModuleFileNameA(nullptr, buf, MAX_PATH);
        std::string p(buf);
        auto pos = p.find_last_of("\\/");
        if (pos != std::string::npos) p = p.substr(0, pos + 1);
        return p + "gmscreen_cwlearn.dat";
#else
        return "gmscreen_cwlearn.dat";
#endif
    }

    static std::string toHex(const uint8_t* d, int n) {
        std::string r;
        r.reserve(n * 2);
        for (int i = 0; i < n; i++) {
            char h[3];
            snprintf(h, sizeof(h), "%02X", d[i]);
            r += h;
        }
        return r;
    }

    static bool fromHex(const std::string& hex, uint8_t* out, int maxBytes) {
        if ((int)hex.size() < maxBytes * 2) return false;
        for (int i = 0; i < maxBytes; i++) {
            unsigned int b;
            if (sscanf(hex.c_str() + i * 2, "%02x", &b) != 1) return false;
            out[i] = (uint8_t)b;
        }
        return true;
    }

    std::string makeKey(uint16_t caid, uint32_t provid, uint16_t sid,
                        const uint8_t* ecm, int len) const {
        char hdr[20];
        snprintf(hdr, sizeof(hdr), "%04X%08X%04X_", caid, provid, sid);
        return std::string(hdr) + toHex(ecm, len);
    }

    std::string makePatternKey(uint16_t caid, uint16_t sid,
                               const uint8_t* ecm, int prefixLen) const {
        char hdr[16];
        snprintf(hdr, sizeof(hdr), "P%04X%04X_", caid, sid);
        return std::string(hdr) + toHex(ecm, prefixLen);
    }

    void appendLog(const EcmCwSample& s) {
        // Keep last 200 for UI display
        recentSamples_.push_back(s);
        if (recentSamples_.size() > 200)
            recentSamples_.erase(recentSamples_.begin());
    }

    void appendTrainCsv(const EcmCwSample& s) {
        try {
            static std::string logPath = trainLogPath();
            bool isNew = false;
            {   std::ifstream test(logPath);
                isNew = !test.good() || test.peek() == std::ifstream::traits_type::eof();
            }
            std::ofstream f(logPath, std::ios::app);
            if (!f.is_open()) return;
            if (isNew)
                f << "time,server,caid,provid,sid,ecm_len,ecm_hex,cw_ok,cw_hex\n";
            char ts[32];
            std::tm* tm = std::localtime(&s.timestamp);
            if (tm) std::strftime(ts, sizeof(ts), "%Y-%m-%d %H:%M:%S", tm);
            else snprintf(ts, sizeof(ts), "0");
            f << ts << ",";
            f << '"' << s.server << '"' << ",";
            char hx[12];
            snprintf(hx, sizeof(hx), "%04X", s.caid); f << hx << ",";
            snprintf(hx, sizeof(hx), "%08X", s.provid); f << hx << ",";
            snprintf(hx, sizeof(hx), "%04X", s.sid); f << hx << ",";
            f << (int)(s.ecm_hex.size() / 2) << ",";
            f << s.ecm_hex << ",";
            f << (s.success ? "1" : "0") << ",";
            f << s.cw_hex << "\n";
        } catch (...) {}
    }

    void maybeAutoSave() {
        samples_since_save_++;
        time_t now = time(nullptr);
        if (samples_since_save_ >= 200 || (now - last_save_) > 120) {
            saveUnlocked();
            last_save_ = now;
            samples_since_save_ = 0;
        }
    }

    bool saveUnlocked() const {
        std::string fp = defaultPath();
        try {
            std::ofstream f(fp, std::ios::trunc);
            if (!f.is_open()) return false;
            f << "CWLEARN2\n";
            f << totalSamples_ << " " << pred_requests_ << " " << pred_used_ << " "
              << pred_correct_ << " " << pred_wrong_ << " " << pred_unverified_ << " "
              << min_pattern_votes_ << "\n";
            f << cwMap_.size() << "\n";
            for (auto& [k, v] : cwMap_)
                f << k << " " << v << "\n";
            f << patternHist_.size() << "\n";
            for (auto& [pk, hist] : patternHist_) {
                f << pk << " " << hist.size() << "\n";
                for (auto& [cw, cnt] : hist)
                    f << cw << " " << cnt << "\n";
            }
            f.close();
            return true;
        } catch (...) { return false; }
    }

    // Training log to disk
    static std::string trainLogPath() {
#ifdef _WIN32
        char buf[MAX_PATH] = {};
        GetModuleFileNameA(nullptr, buf, MAX_PATH);
        std::string p(buf);
        auto pos = p.find_last_of("\\/");
        if (pos != std::string::npos) p = p.substr(0, pos + 1);
        return p + "gmscreen_ecm_train.csv";
#else
        return "gmscreen_ecm_train.csv";
#endif
    }
};

} // namespace cccam
