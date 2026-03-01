#include "stb/crypto.h"
#include <algorithm>

namespace stb {
namespace crypto {

void scrambleStbInfo(std::vector<uint8_t>& buffer, uint8_t xor_value) {
    scrambleStbInfo(buffer.data(), buffer.size(), xor_value);
}

void scrambleStbInfo(uint8_t* buffer, size_t length, uint8_t xor_value) {
    if (!buffer || length == 0) return;
    
    size_t half = length / 2;
    
    // Swap bytes from outside in and XOR both
    for (size_t i = 0; i < half; ++i) {
        size_t j = length - 1 - i;
        
        // Swap bytes
        uint8_t temp = buffer[j];
        buffer[j] = buffer[i];
        buffer[i] = temp;
        
        // XOR both swapped bytes
        buffer[i] = (buffer[i] ^ xor_value) & 0xFF;
        buffer[j] = (buffer[j] ^ xor_value) & 0xFF;
    }
    
    // Handle middle byte for odd-length buffers
    if (length % 2 == 1) {
        size_t mid = length / 2;
        buffer[mid] = (buffer[mid] ^ xor_value) & 0xFF;
    }
}

} // namespace crypto
} // namespace stb
