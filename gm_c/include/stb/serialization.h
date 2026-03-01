#pragma once

#include <cstdint>
#include <string>
#include <vector>
#include <map>
#include <variant>

namespace stb {
namespace protocol {

/**
 * @brief Command parameter value type (string or nested map)
 */
using ParamValue = std::variant<std::string, std::map<std::string, std::string>>;

/**
 * @brief Build socket frame with Start/Length/End format
 * 
 * Format: Start + 7-digit length + End + payload
 * Example: Start0000095End<xml...>
 * 
 * @param payload Raw payload data
 * @return Framed data ready to send
 */
std::vector<uint8_t> buildSocketFrame(const std::vector<uint8_t>& payload);
std::vector<uint8_t> buildSocketFrame(const std::string& payload);

/**
 * @brief XML Serializer for STB commands
 */
class XmlSerializer {
public:
    /**
     * @brief Serialize command to XML
     * @param request_type Command type number
     * @param params List of parameter maps
     * @return XML string
     */
    static std::string serialize(int request_type, const std::vector<std::map<std::string, ParamValue>>& params);
    
    /**
     * @brief Serialize simple command with single param
     */
    static std::string serializeSimple(int request_type, const std::string& key, const std::string& value);
    
    /**
     * @brief Parse XML response to map
     * @param xml XML data
     * @return Parsed data structure
     */
    static std::map<std::string, std::string> parse(const std::string& xml);
    
    /**
     * @brief Parse XML channel list response
     */
    static std::vector<std::map<std::string, std::string>> parseChannelList(const std::string& xml);
    
private:
    static std::string xmlEscape(const std::string& text);
    static std::string buildParams(int request_type, 
                                     const std::vector<std::map<std::string, ParamValue>>& params);
};

/**
 * @brief JSON Serializer for STB commands
 */
class JsonSerializer {
public:
    /**
     * @brief Serialize command to JSON
     * @param request_type Command type number
     * @param params List of parameter maps
     * @return JSON string
     */
    static std::string serialize(int request_type, const std::vector<std::map<std::string, ParamValue>>& params);
    
    /**
     * @brief Serialize simple command
     */
    static std::string serializeSimple(int request_type, const std::string& key, const std::string& value);
    
    /**
     * @brief Parse JSON response
     * @param json JSON data
     * @return Parsed data structure
     */
    static std::map<std::string, std::string> parse(const std::string& json);
    
    /**
     * @brief Parse JSON array response (for channel lists)
     */
    static std::vector<std::map<std::string, std::string>> parseArray(const std::string& json);
    
private:
    static std::string escapeString(const std::string& text);
    static std::string buildJsonObject(const std::map<std::string, std::string>& obj);
};

/**
 * @brief Received message structure (after parsing GCDH header)
 */
struct ReceivedMessage {
    uint32_t command_type = 0;
    uint32_t response_state = 0;
    std::vector<uint8_t> data;
    
    bool isSuccess() const { return response_state == 0; }
    bool hasData() const { return !data.empty(); }
};

/**
 * @brief GCDH Header parser
 */
class GcdhHeader {
public:
    static constexpr size_t HEADER_SIZE = 16;
    static constexpr const char* MAGIC = "GCDH";
    
    /**
     * @brief Parse header from raw bytes
     * @param data Must be at least 16 bytes
     * @return true if valid GCDH header
     */
    static bool parse(const uint8_t* data, size_t length, 
                      uint32_t& out_data_length, 
                      uint32_t& out_command_type,
                      uint32_t& out_response_state);
    
    /**
     * @brief Check if data starts with GCDH magic
     */
    static bool isValid(const uint8_t* data, size_t length);
};

} // namespace protocol
} // namespace stb
