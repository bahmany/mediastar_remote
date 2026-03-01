#pragma once

#include <cstdint>
#include <vector>

namespace stb {
namespace crypto {

/**
 * @brief Scramble/descramble STB broadcast info data
 * 
 * Algorithm: XOR + byte swap (reversible)
 * - XOR value: 0x5B (91)
 * - Swap bytes from outside in, then XOR both
 * - If odd length, XOR middle byte
 * 
 * @param buffer Data buffer to scramble in-place
 * @param length Length of data to process
 * @param xor_value XOR value (default 0x5B)
 */
void scrambleStbInfo(std::vector<uint8_t>& buffer, uint8_t xor_value = 0x5B);
void scrambleStbInfo(uint8_t* buffer, size_t length, uint8_t xor_value = 0x5B);

/**
 * @brief Descramble (same operation as scramble - it's reversible)
 */
inline void descrambleStbInfo(std::vector<uint8_t>& buffer, uint8_t xor_value = 0x5B) {
    scrambleStbInfo(buffer, xor_value);
}

inline void descrambleStbInfo(uint8_t* buffer, size_t length, uint8_t xor_value = 0x5B) {
    scrambleStbInfo(buffer, length, xor_value);
}

} // namespace crypto
} // namespace stb
