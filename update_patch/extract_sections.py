import struct, re, os, sys

BIN = r'D:\projects\gmscreen\update_patch\MS-4030_BlueMenu_Ver1.11.20688_2026-1-22.bin'
OUT = r'D:\projects\gmscreen\update_patch\extracted'
OUTF = r'D:\projects\gmscreen\update_patch\sections_out.txt'
os.makedirs(OUT, exist_ok=True)

data = open(BIN, 'rb').read()
_f = open(OUTF, 'w', encoding='utf-8')
sys.stdout = _f

def prnhex(buf, maxn=64):
    h = buf[:maxn].hex(' ')
    p = ''.join(chr(c) if 0x20<=c<=0x7E else '.' for c in buf[:maxn])
    print(f"  hex: {h}")
    print(f"  asc: {p}")

def get_strings(buf, minlen=6):
    return [m.group().decode('latin1') for m in re.finditer(rb'[ -~]{%d,}' % minlen, buf)]

# ── Section boundaries from previous analysis ─────────────────────────────────
sections = [
    (0x00000000, 0x000003B6,  'HDR_950b'),
    (0x000003FE, 0x00000762,  'BOOT_TABLE'),
    (0x0000092E, 0x0000587A,  'ARM_CODE'),
    (0x00005C0E, 0x0002992E,  'SECTION_A'),
    (0x0004012E, 0x004AC37F,  'MAIN_PAYLOAD_4MB'),
    (0x0055B002, 0x005BB84C,  'BMPS_SECTION'),
    (0x0075B002, 0x0075BC44,  'SEC_3KB'),
    (0x0077B002, 0x0077C5F5,  'SEC_5KB'),
    (0x0079B002, 0x007AC7D6,  'STRG_SECTION'),
    (0x0081B002, 0x0081E8E2,  'DATA_14KB'),
    (0x0081F1A3, 0x008257A8,  'DATA_26KB'),
    (0x00826222, 0x0082628E,  'DATA_108b'),
    (0x00826A0E, 0x008272B1,  'DATA_2KB_TAN'),
    (0x00827FEE, 0x008297E1,  'DATA_6KB_END'),
]

for start, end, name in sections:
    buf = data[start:end]
    strs = get_strings(buf, 6)
    print(f"\n{'='*70}")
    print(f"SECTION: {name}  @ 0x{start:08X}-0x{end:08X}  len={len(buf):,}")
    prnhex(buf, 48)
    print(f"  String count: {len(strs)}")
    if strs:
        # Show all strings
        for s in strs[:300]:
            print(f"    [{len(s):3d}] {s}")
    # Save section to file
    fname = os.path.join(OUT, f"{name}_{start:08X}.bin")
    open(fname,'wb').write(buf)

# ── Deep scan of STRG section ──────────────────────────────────────────────────
print("\n\n" + "="*70)
print("STRG SECTION DEEP SCAN")
strg_start = 0x0079B002
strg_end   = 0x007AC7D6
strg = data[strg_start:strg_end]
print(f"  Size: {len(strg):,} bytes")
print(f"  Header: {strg[:32].hex(' ')}")
# Parse STRG magic
if strg[:4] == b'STRG':
    print("  >> STRG magic confirmed!")
    # Try to parse as a string table
    # Common format: 4-byte magic, 4-byte count, then offset table, then string data
    count = struct.unpack_from('<I', strg, 4)[0]
    ver   = struct.unpack_from('<I', strg, 8)[0]
    print(f"  count field: {count} (0x{count:08X})")
    print(f"  version/flags: {ver} (0x{ver:08X})")
    # Show more of the header
    print(f"  Bytes 0-63: {strg[:64].hex(' ')}")
    # Scan for all readable strings
    strs = get_strings(strg, 4)
    print(f"  All strings (min 4): {len(strs)}")
    for s in strs:
        print(f"    [{len(s):3d}] {s}")

# ── Scan section with "Tan" ───────────────────────────────────────────────────
print("\n\n" + "="*70)
print("DATA_2KB_TAN DEEP SCAN")
tan_start = 0x00826A0E
tan_end   = 0x008272B1
tan = data[tan_start:tan_end]
print(f"  Size: {len(tan):,} bytes")
print(f"  Full hex dump (first 256 bytes):")
for i in range(0, min(256, len(tan)), 16):
    h = tan[i:i+16].hex(' ')
    p = ''.join(chr(c) if 0x20<=c<=0x7E else '.' for c in tan[i:i+16])
    print(f"    {i:04X}: {h:<48}  {p}")

# ── Scan DATA_14KB for structures ─────────────────────────────────────────────
print("\n\n" + "="*70)
print("DATA_14KB DEEP SCAN (0x0081B002)")
d14_start = 0x0081B002
d14_end   = 0x0081E8E2
d14 = data[d14_start:d14_end]
print(f"  Size: {len(d14):,} bytes")
strs14 = get_strings(d14, 4)
print(f"  Strings (min4): {len(strs14)}")
for s in strs14:
    print(f"    [{len(s):3d}] {s}")
# First 128 bytes hex
print(f"  First 128 bytes:")
for i in range(0, 128, 16):
    h = d14[i:i+16].hex(' ')
    p = ''.join(chr(c) if 0x20<=c<=0x7E else '.' for c in d14[i:i+16])
    print(f"    {i:04X}: {h:<48}  {p}")

# ── ARM code section analysis ─────────────────────────────────────────────────
print("\n\n" + "="*70)
print("ARM_CODE SECTION (0x0000092E)")
arm = data[0x0000092E:0x0000587A]
print(f"  Size: {len(arm):,} bytes")
strs_arm = get_strings(arm, 5)
print(f"  Strings: {len(strs_arm)}")
for s in strs_arm[:100]:
    print(f"    [{len(s):3d}] {s}")

# ── BOOT_TABLE ────────────────────────────────────────────────────────────────
print("\n\n" + "="*70)
print("BOOT_TABLE (0x000003FE)")
boot = data[0x000003FE:0x00000762]
print(f"  Size: {len(boot):,} bytes")
print(f"  Full hex dump:")
for i in range(0, len(boot), 16):
    h = boot[i:i+16].hex(' ')
    p = ''.join(chr(c) if 0x20<=c<=0x7E else '.' for c in boot[i:i+16])
    print(f"    {i:04X}: {h:<48}  {p}")

_f.flush(); _f.close(); sys.stdout = sys.__stdout__
print("Done -> sections_out.txt")
