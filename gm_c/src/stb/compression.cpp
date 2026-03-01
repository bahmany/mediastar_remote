#include "stb/compression.h"
#include <zlib.h>
#include <stdexcept>
#include <cstring>

namespace stb {
namespace compression {

std::vector<uint8_t> compress(const std::vector<uint8_t>& data) {
    if (data.empty()) return {};
    
    z_stream stream;
    stream.zalloc = Z_NULL;
    stream.zfree = Z_NULL;
    stream.opaque = Z_NULL;
    
    // Initialize with raw deflate (no zlib header)
    int ret = deflateInit2(&stream, Z_DEFAULT_COMPRESSION, Z_DEFLATED, -MAX_WBITS, 8, Z_DEFAULT_STRATEGY);
    if (ret != Z_OK) {
        throw std::runtime_error("Failed to initialize zlib compressor");
    }
    
    stream.next_in = const_cast<Bytef*>(data.data());
    stream.avail_in = static_cast<uInt>(data.size());
    
    std::vector<uint8_t> result;
    result.reserve(data.size());  // Compressed may be larger, but this is a good starting point
    
    uint8_t buffer[8192];
    do {
        stream.next_out = buffer;
        stream.avail_out = sizeof(buffer);
        
        ret = deflate(&stream, Z_FINISH);
        if (ret == Z_STREAM_ERROR) {
            deflateEnd(&stream);
            throw std::runtime_error("Zlib compression error");
        }
        
        size_t have = sizeof(buffer) - stream.avail_out;
        result.insert(result.end(), buffer, buffer + have);
    } while (stream.avail_out == 0);
    
    deflateEnd(&stream);
    return result;
}

std::vector<uint8_t> compress(const std::string& data) {
    return compress(std::vector<uint8_t>(data.begin(), data.end()));
}

// Internal inflate helper. wbits controls format:
//   -MAX_WBITS  = raw deflate
//   MAX_WBITS   = zlib-wrapped
//   MAX_WBITS+16 = gzip
//   MAX_WBITS+32 = auto-detect zlib/gzip
static std::vector<uint8_t> inflateData(const std::vector<uint8_t>& data, int wbits) {
    if (data.empty()) return {};
    
    z_stream stream{};
    stream.zalloc = Z_NULL;
    stream.zfree = Z_NULL;
    stream.opaque = Z_NULL;
    stream.avail_in = static_cast<uInt>(data.size());
    stream.next_in = const_cast<Bytef*>(data.data());
    
    int ret = inflateInit2(&stream, wbits);
    if (ret != Z_OK) {
        throw std::runtime_error("Failed to initialize zlib decompressor");
    }
    
    std::vector<uint8_t> result;
    result.reserve(data.size() * 4);
    
    uint8_t buffer[8192];
    do {
        stream.next_out = buffer;
        stream.avail_out = sizeof(buffer);
        
        ret = inflate(&stream, Z_NO_FLUSH);
        if (ret == Z_NEED_DICT || ret == Z_DATA_ERROR || ret == Z_MEM_ERROR) {
            inflateEnd(&stream);
            throw std::runtime_error("Zlib decompression error: " + std::to_string(ret));
        }
        
        size_t have = sizeof(buffer) - stream.avail_out;
        result.insert(result.end(), buffer, buffer + have);
    } while (ret != Z_STREAM_END);
    
    inflateEnd(&stream);
    return result;
}

std::vector<uint8_t> decompress(const std::vector<uint8_t>& data) {
    // Try auto-detect (zlib or gzip), fall back to raw deflate
    try {
        return inflateData(data, MAX_WBITS + 32); // auto zlib/gzip
    } catch (...) {}
    return inflateData(data, -MAX_WBITS); // raw deflate
}

bool isCompressed(const std::vector<uint8_t>& data) {
    if (data.size() < 2) return false;
    // BegC marker
    if (data.size() >= 4 && data[0]=='B' && data[1]=='e' && data[2]=='g' && data[3]=='C')
        return true;
    // zlib header: first byte 0x78 (CMF with CM=8)
    if (data[0] == 0x78)
        return true;
    // gzip header
    if (data[0] == 0x1F && data[1] == 0x8B)
        return true;
    return false;
}

std::vector<uint8_t> autoDecompress(const std::vector<uint8_t>& data) {
    if (data.size() < 2) return data;
    
    // BegC marker: strip 4-byte prefix then decompress
    if (data.size() >= 4 && data[0]=='B' && data[1]=='e' && data[2]=='g' && data[3]=='C') {
        std::vector<uint8_t> stripped(data.begin() + 4, data.end());
        try { return decompress(stripped); } catch (...) { return data; }
    }
    
    // zlib-wrapped (0x78) or gzip (0x1F 0x8B)
    if (data[0] == 0x78 || (data[0] == 0x1F && data[1] == 0x8B)) {
        try { return decompress(data); } catch (...) { return data; }
    }
    
    return data;
}

} // namespace compression
} // namespace stb
