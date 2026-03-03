$f = 'D:\projects\gmscreen\update_patch\MS-4030_BlueMenu_Ver1.11.20688_2026-1-22.bin'
$b = [System.IO.File]::ReadAllBytes($f)
"Size: $($b.Length) bytes"
"HEAD_HEX: " + (($b[0..31] | ForEach-Object { '{0:X2}' -f $_ }) -join ' ')

# Scan for known container/compression magic bytes across the whole file
$sigs = [ordered]@{
    'ZIP_PK'    = [byte[]](0x50,0x4B,0x03,0x04)
    'gzip'      = [byte[]](0x1F,0x8B)
    'zlib_78DA' = [byte[]](0x78,0xDA)
    'zlib_789C' = [byte[]](0x78,0x9C)
    'zlib_785E' = [byte[]](0x78,0x5E)
    'ELF'       = [byte[]](0x7F,0x45,0x4C,0x46)
    'LZMA_xz'   = [byte[]](0xFD,0x37,0x7A,0x58,0x5A,0x00)
    'squashfs'  = [byte[]](0x73,0x71,0x73,0x68)
    'uImage'    = [byte[]](0x27,0x05,0x19,0x56)
    'BZip2'     = [byte[]](0x42,0x5A,0x68)
    'LZMA_props'= [byte[]](0x5D,0x00,0x00)
    'jffs2'     = [byte[]](0x19,0x85)
    'romfs'     = [byte[]](0x2D,0x72,0x6F,0x6D,0x31,0x66,0x73,0x2D)
    'cramfs'    = [byte[]](0x45,0x3D,0xCD,0x28)
    'POSIX_tar' = [byte[]](0x75,0x73,0x74,0x61,0x72)
    'RAR4'      = [byte[]](0x52,0x61,0x72,0x21,0x1A,0x07,0x00)
    'RAR5'      = [byte[]](0x52,0x61,0x72,0x21,0x1A,0x07,0x01)
    'LZIP'      = [byte[]](0x4C,0x5A,0x49,0x50)
    'LZ4'       = [byte[]](0x04,0x22,0x4D,0x18)
}

$limit = $b.Length - 8
$found = [System.Collections.Generic.List[string]]::new()
foreach ($name in $sigs.Keys) {
    $pat = $sigs[$name]
    $pl = $pat.Length
    $count = 0
    for ($i = 0; $i -lt $limit; $i++) {
        if ($b[$i] -ne $pat[0]) { continue }
        $ok = $true
        for ($j = 1; $j -lt $pl; $j++) { if ($b[$i+$j] -ne $pat[$j]) { $ok = $false; break } }
        if ($ok) {
            $count++
            if ($count -le 5) { $found.Add("  SIG:$name  offset=0x{0:X8}  ({1})" -f $i, $i) }
        }
    }
    if ($count -gt 0) { "FOUND $name  x$count occurrences" }
}
$found | ForEach-Object { $_ }

# Extract ASCII strings >= 10 chars, filter for useful protocol/command tokens
""; "=== PROTOCOL / COMMAND STRINGS ==="
$cur = [System.Text.StringBuilder]::new()
$proto = [System.Collections.Generic.List[string]]::new()
$keywords = 'Key','Program','TvState','Channel','Remote','Fav','EPG','Login','Mobile','Screen','Version','Update','Model','Platform','Serial','Restart','Power','Volume','Switch','Command','Request','Socket','Start','End','Magic','Code','Port','User','Pass','Auth'
for ($i = 0; $i -lt $b.Length; $i++) {
    $c = [char]$b[$i]
    if ($c -ge 0x20 -and $c -le 0x7E) { [void]$cur.Append($c) }
    else {
        $s = $cur.ToString(); $cur.Clear()
        if ($s.Length -ge 8) {
            foreach ($kw in $keywords) {
                if ($s -match $kw) { $proto.Add($s); break }
            }
        }
    }
}
$proto | Select-Object -Unique | ForEach-Object { "  STR: $_" }

""; "=== LONG READABLE STRINGS (>=20 chars) ==="
$cur2 = [System.Text.StringBuilder]::new()
$long_strs = [System.Collections.Generic.List[string]]::new()
for ($i = 0; $i -lt $b.Length; $i++) {
    $c = [char]$b[$i]
    if ($c -ge 0x20 -and $c -le 0x7E) { [void]$cur2.Append($c) }
    else {
        $s = $cur2.ToString(); $cur2.Clear()
        if ($s.Length -ge 20) { $long_strs.Add($s) }
    }
}
$long_strs | Select-Object -Unique | Select-Object -First 200 | ForEach-Object { "  $_ " }
