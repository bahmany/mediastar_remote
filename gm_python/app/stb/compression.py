from __future__ import annotations

import zlib


def gs_compress(data: bytes) -> bytes:
    compressor = zlib.compressobj(zlib.Z_DEFAULT_COMPRESSION, zlib.DEFLATED, -zlib.MAX_WBITS)
    compressed = compressor.compress(data)
    compressed += compressor.flush()
    return compressed


def gs_decompress(data: bytes) -> bytes:
    decompressor = zlib.decompressobj(-zlib.MAX_WBITS)
    decompressed = decompressor.decompress(data)
    decompressed += decompressor.flush()
    return decompressed
