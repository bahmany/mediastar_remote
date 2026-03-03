import struct, zlib, gzip, io, os, re, hashlib, sys

BIN = r'D:\projects\gmscreen\update_patch\MS-4030_BlueMenu_Ver1.11.20688_2026-1-22.bin'
OUT = r'D:\projects\gmscreen\update_patch\extracted'
OUTF = r'D:\projects\gmscreen\update_patch\deep2_out.txt'
os.makedirs(OUT, exist_ok=True)
# Redirect stdout to UTF-8 file to avoid Windows cp1252 encoding errors
_out = open(OUTF, 'w', encoding='utf-8')
sys.stdout = _out

data = open(BIN,'rb').read()
print(f"[*] Size: {len(data):,}  SHA256: {hashlib.sha256(data).hexdigest()[:16]}...")

# ── 1. Header structure analysis ─────────────────────────────────────────────
print("\n=== HEADER ANALYSIS ===")
print(f"  Bytes 0-3:   {data[:4].hex(' ')}  = '{data[:4].decode('latin1','replace')}'")
print(f"  Bytes 4-7:   {data[4:8].hex(' ')}  = LE u32: {struct.unpack_from('<I',data,4)[0]}")
print(f"  Bytes 8-11:  {data[8:12].hex(' ')}  = LE u32: {struct.unpack_from('<I',data,8)[0]}")
print(f"  Bytes 12-15: {data[12:16].hex(' ')}  = LE u32: {struct.unpack_from('<I',data,12)[0]}")
print(f"  Bytes 16-19: {data[16:20].hex(' ')}  = LE u32: {struct.unpack_from('<I',data,16)[0]}")
print(f"  Bytes 20-23: {data[20:24].hex(' ')}  = LE u32: {struct.unpack_from('<I',data,20)[0]}")
# Also try BE
for off in range(0, 64, 4):
    le = struct.unpack_from('<I',data,off)[0]
    be = struct.unpack_from('>I',data,off)[0]
    print(f"  +{off:02d}: LE={le:10d} (0x{le:08X})  BE={be:10d} (0x{be:08X})")

# ── 2. Find 0xFF runs (flash section boundaries) ─────────────────────────────
print("\n=== 0xFF RUNS (section boundaries) ===")
in_run = False
run_start = 0
boundaries = []
min_run = 64  # min 64 0xFF bytes to count
i = 0
while i < len(data):
    if data[i] == 0xFF:
        if not in_run:
            in_run = True
            run_start = i
    else:
        if in_run:
            run_len = i - run_start
            if run_len >= min_run:
                boundaries.append((run_start, i, run_len))
            in_run = False
    i += 1
print(f"  Found {len(boundaries)} 0xFF gaps >= {min_run} bytes")
for s,e,l in boundaries[:30]:
    print(f"  GAP: 0x{s:08X} - 0x{e:08X}  len={l:,}  ({l//1024}KB)")

# ── 3. Look at section data between gaps ──────────────────────────────────────
print("\n=== SECTIONS BETWEEN GAPS ===")
prev_end = 0
sections = []
for s,e,l in boundaries[:20]:
    if s - prev_end > 32:
        sections.append((prev_end, s))
    prev_end = e
sections.append((prev_end, len(data)))
for sec_s, sec_e in sections[:15]:
    sec = data[sec_s:sec_e]
    if len(sec) < 4: continue
    head_hex = sec[:16].hex(' ')
    printable = ''.join(chr(c) if 0x20<=c<=0x7E else '.' for c in sec[:16])
    print(f"  SECTION 0x{sec_s:08X}-0x{sec_e:08X}  len={sec_e-sec_s:,}  head={head_hex} '{printable}'")

# ── 4. Look at the gzip area in detail ────────────────────────────────────────
print("\n=== GZIP AREA @ 0x580669 ===")
gz_off = 0x580669
print(f"  32 bytes before: {data[gz_off-16:gz_off].hex(' ')}")
print(f"  gzip header: {data[gz_off:gz_off+16].hex(' ')}")
# Parse gzip header
if data[gz_off:gz_off+3] == b'\x1f\x8b\x08':
    flags = data[gz_off+3]
    mtime = struct.unpack_from('<I', data, gz_off+4)[0]
    xfl = data[gz_off+8]
    os_byte = data[gz_off+9]
    print(f"  flags=0x{flags:02X} mtime={mtime} xfl=0x{xfl:02X} os=0x{os_byte:02X}")
    # Try to decompress
    try:
        dec = gzip.decompress(data[gz_off:])
        print(f"  DECOMPRESSED: {len(dec):,} bytes")
        open(os.path.join(OUT,'gzip_580669.bin'),'wb').write(dec)
        strs = [m.group().decode('latin1') for m in re.finditer(rb'[ -~]{10,}', dec)]
        print(f"  Strings ({len(strs)}): {strs[:20]}")
    except Exception as ex:
        print(f"  decompress failed: {ex}")
        # Try reading just up to end of file
        try:
            buf = io.BytesIO(data[gz_off:])
            with gzip.GzipFile(fileobj=buf) as g:
                dec = g.read(8*1024*1024)
            print(f"  DECOMPRESSED (stream): {len(dec):,} bytes")
        except Exception as ex2:
            print(f"  stream decompress failed: {ex2}")

# ── 5. Try XOR on just the section around gzip ───────────────────────────────
print("\n=== XOR DECODE AROUND GZIP ===")
for key in [0x5B, 0x33, 0x54, 0xB9, 0x77, 0x20, 0xC2, 0x81]:
    xd = bytes(b ^ key for b in data[gz_off:gz_off+256])
    if xd[:3] == b'\x1f\x8b\x08':
        print(f"  XOR 0x{key:02X} -> gzip header still intact")
    # Check if XOR makes it gzip
    for off2 in range(0, 32):
        if xd[off2:off2+3] == b'\x1f\x8b\x08':
            print(f"  XOR 0x{key:02X} shifts gzip to +{off2}")
            break

# ── 6. Scan entire file for embedded strings (no XOR) at wider range ──────────
print("\n=== EMBEDDED ASCII STRINGS (no XOR, len>=15) ===")
strs_all = [m.group().decode('latin1') for m in re.finditer(rb'[ -~]{15,}', data)]
print(f"  Found {len(strs_all)} strings")
# Show most interesting ones
keywords = ['Key','Program','Tv','Channel','Remote','Fav','EPG','Login','Mobile',
            'Screen','Version','Update','Model','Platform','Serial','Restart','Power',
            'Volume','Switch','Command','Request','Socket','Magic','Code','Port',
            'User','Pass','Auth','MS-','Star','Menu','4030','20688','gzip','zlib',
            'http','www','com','net','TV','Radio','String','Error','OK','Server']
proto = [s for s in strs_all if any(kw.lower() in s.lower() for kw in keywords)]
print(f"  Protocol/keyword matches: {len(proto)}")
for s in proto[:100]:
    print(f"    {s}")
print(f"\n  All long strings (top 200 by length):")
for s in sorted(strs_all, key=len, reverse=True)[:200]:
    print(f"    [{len(s):3d}] {s}")

# ── 7. Entropy per 4KB block ──────────────────────────────────────────────────
print("\n=== ENTROPY PER 4KB SECTION (first 80 sections) ===")
import math
for i in range(min(80, len(data)//4096)):
    blk = data[i*4096:(i+1)*4096]
    freq = {}
    for b in blk: freq[b] = freq.get(b,0)+1
    ent = sum(-c/4096*math.log2(c/4096) for c in freq.values() if c>0)
    bar = '#' * int(ent)
    print(f"  blk {i:03d} @ 0x{i*4096:08X}: ent={ent:.3f} {bar}")

_out.flush(); _out.close(); sys.stdout = sys.__stdout__
