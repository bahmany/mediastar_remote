#pragma once

#include <cstdint>
#include <vector>
#include <string>

namespace stb {
namespace compression {

/**
 * @brief Compress data using zlib deflate
 * @param data Input data
 * @return Compressed data
 */
std::vector<uint8_t> compress(const std::vector<uint8_t>& data);
std::vector<uint8_t> compress(const std::string& data);

/**
 * @brief Decompress data using zlib inflate
 * @param data Compressed input data
 * @return Decompressed data
 * @throws std::runtime_error on decompression failure
 */
std::vector<uint8_t> decompress(const std::vector<uint8_t>& data);

/**
 * @brief Check if data appears to be compressed
 * @param data Data to check
 * @return true if starts with "BegC" compression marker
 */
bool isCompressed(const std::vector<uint8_t>& data);

/**
 * @brief Auto-decompress if needed
 * @param data Input data
 * @return Decompressed data if compressed, otherwise original
 */
std::vector<uint8_t> autoDecompress(const std::vector<uint8_t>& data);

} // namespace compression
} // namespace stb
