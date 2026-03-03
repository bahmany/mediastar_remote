import struct, zlib, gzip, io, os, re

BIN = r'D:\projects\gmscreen\update_patch\MS-4030_BlueMenu_Ver1.11.20688_2026-1-22.bin'
OUT = r'D:\projects\gmscreen\update_patch'

data = open(BIN,'rb').read()
print(f"[*] File size: {len(data):,} bytes")
print(f"[*] Header (32 bytes hex): {data[:32].hex(' ')}")
print(f"[*] 0xFF count: {data.count(bytes([0xFF])):,} ({data.count(bytes([0xFF]))*100/len(data):.1f}%)")
print(f"[*] 0x00 count: {data.count(bytes([0x00])):,}")

# ── 1. Try XOR decoding with the known STB XOR key 0x5B ─────────────────────
print("\n[*] Trying XOR decode with 0x5B (known STB key)...")
xor_5b = bytes(b ^ 0x5B for b in data[:128])
print(f"    Decoded head (128 bytes): {xor_5b.hex(' ')}")
try:
    print(f"    ASCII head: {xor_5b.decode('latin1','replace')[:80]}")
except: pass

# Try a few common XOR keys on the header
for key in [0x5B, 0xFF, 0xAA, 0x55, 0x42, 0x12, 0x20, 0x66]:
    decoded_head = bytes(b ^ key for b in data[:32])
    printable = ''.join(chr(c) if 0x20 <= c <= 0x7E else '.' for c in decoded_head)
    print(f"    XOR 0x{key:02X}: {printable}  | hex: {decoded_head[:8].hex(' ')}")

# ── 2. Find ALL zlib / gzip blocks ──────────────────────────────────────────
print("\n[*] Scanning for gzip/zlib blocks...")
gzip_magic = b'\x1f\x8b\x08'
zlib_magics = [b'\x78\xda', b'\x78\x9c', b'\x78\x5e', b'\x78\x01']

def try_decompress_zlib(buf, offset):
    try:
        dec = zlib.decompress(buf[offset:offset+min(4*1024*1024, len(buf)-offset)])
        return dec
    except:
        # Try with wbits=-15 (raw deflate)
        try:
            dec = zlib.decompress(buf[offset:offset+min(4*1024*1024, len(buf)-offset)], -15)
            return dec
        except:
            return None

def try_decompress_gzip(buf, offset):
    try:
        return gzip.decompress(buf[offset:])
    except Exception as e:
        try:
            return gzip.decompress(buf[offset:offset+min(4*1024*1024, len(buf)-offset)])
        except:
            return None

def extract_strings(buf, min_len=10):
    pattern = re.compile(rb'[ -~]{' + str(min_len).encode() + rb',}')
    return [m.group().decode('latin1') for m in pattern.finditer(buf)]

blocks = []
# Gzip
pos = 0
while True:
    idx = data.find(gzip_magic, pos)
    if idx < 0: break
    blocks.append(('gzip', idx))
    pos = idx + 1

# Zlib
for magic in zlib_magics:
    pos = 0
    while True:
        idx = data.find(magic, pos)
        if idx < 0: break
        blocks.append(('zlib', idx))
        pos = idx + 1

blocks.sort(key=lambda x: x[1])
print(f"    Total candidate blocks: {len(blocks)}")

# ── 3. Attempt decompression and string extraction ───────────────────────────
print("\n[*] Attempting decompression of first 20 blocks...")
proto_keywords = ['KeyValue','ProgramId','TvState','KeyCode','FavMark','FavorGroup',
                  'FromIndex','ToIndex','MobileLogin','MagicCode','platform_id','GsRemote',
                  'GsMobile','RemoteControl','ChannelSwitch','keep_alive','screenshot',
                  'GMS_MSG','39Wwij','GCDH','BegC','KeyUp','KeyDown','MenuKey','OKKey',
                  'ServiceID','ServiceName','program_id','channel','epg','version','model',
                  'MS-4030','MediaStar','BlueMenu','Update','serial','password']

all_strings = []
for i, (kind, offset) in enumerate(blocks[:40]):
    if kind == 'gzip':
        dec = try_decompress_gzip(data, offset)
    else:
        dec = try_decompress_zlib(data, offset)
    
    if dec and len(dec) > 100:
        strs = extract_strings(dec, 8)
        print(f"    [{i:02d}] {kind} @ 0x{offset:08X} -> {len(dec):,} bytes, {len(strs)} strings")
        # Save decompressed block
        fname = os.path.join(OUT, f"block_{i:02d}_{kind}_{offset:08X}.bin")
        open(fname, 'wb').write(dec)
        # Check for protocol keywords
        proto = [s for s in strs if any(kw.lower() in s.lower() for kw in proto_keywords)]
        if proto:
            print(f"       PROTOCOL STRINGS: {proto[:10]}")
        all_strings.extend(strs)
    elif dec:
        print(f"    [{i:02d}] {kind} @ 0x{offset:08X} -> {len(dec)} bytes (tiny, skip)")
    else:
        pass  # failed decompression, normal for false positives

# ── 4. Search whole XOR-decoded data ────────────────────────────────────────
print("\n[*] Checking if XOR-decoded file has readable content...")
for key in [0x5B, 0xFF, 0x42, 0x66, 0x12]:
    xdata = bytes(b ^ key for b in data)
    strs = extract_strings(xdata[:200000], 12)
    proto = [s for s in strs if any(kw.lower() in s.lower() for kw in proto_keywords)]
    if proto:
        print(f"    XOR 0x{key:02X} FOUND PROTOCOL STRINGS: {proto[:5]}")
    if strs:
        print(f"    XOR 0x{key:02X} -> {len(strs)} strings in first 200KB, sample: {strs[:3]}")

# ── 5. Unique strings from all decompressed blocks ──────────────────────────
print(f"\n[*] Total strings from all decompressed blocks: {len(all_strings)}")
unique = sorted(set(all_strings), key=len, reverse=True)
print(f"    Unique: {len(unique)}")

# Protocol strings
proto_all = [s for s in unique if any(kw.lower() in s.lower() for kw in proto_keywords)]
print(f"\n[*] Protocol-related strings ({len(proto_all)}):")
for s in proto_all[:100]:
    print(f"    {s}")

# Long strings
long_all = [s for s in unique if len(s) >= 25]
print(f"\n[*] Long strings >= 25 chars ({len(long_all)}):")
for s in long_all[:200]:
    print(f"    {s}")

# Model/version strings
model_all = [s for s in unique if re.search(r'(?i)(MS-|4030|BlueMenu|MediaStar|Ver|Version|firmware|platform|serial|model)', s)]
print(f"\n[*] Model/version strings ({len(model_all)}):")
for s in model_all[:50]:
    print(f"    {s}")

print("\n[DONE]")
