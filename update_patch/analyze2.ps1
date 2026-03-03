$f = 'D:\projects\gmscreen\update_patch\MS-4030_BlueMenu_Ver1.11.20688_2026-1-22.bin'
$b = [System.IO.File]::ReadAllBytes($f)
$text = [System.Text.Encoding]::Latin1.GetString($b)
"SIZE: $($b.Length)"
"HEAD_HEX: " + (($b[0..31] | ForEach-Object { '{0:X2}' -f $_ }) -join ' ')

# Fast signature search using .NET IndexOf
function FindSig($arr, $sig) {
    $idx = 0
    $hits = @()
    while ($true) {
        $pos = [Array]::IndexOf($arr, $sig[0], $idx)
        if ($pos -lt 0 -or $pos -ge $arr.Length - $sig.Length) { break }
        $ok = $true
        for ($j=1; $j -lt $sig.Length; $j++) {
            if ($arr[$pos+$j] -ne $sig[$j]) { $ok=$false; break }
        }
        if ($ok) { $hits += $pos }
        $idx = $pos + 1
        if ($hits.Count -ge 10) { break }
    }
    return $hits
}

$sigs = [ordered]@{
    'ZIP_PK'     = [byte[]](0x50,0x4B,0x03,0x04)
    'gzip'       = [byte[]](0x1F,0x8B,0x08)
    'zlib_78DA'  = [byte[]](0x78,0xDA)
    'zlib_789C'  = [byte[]](0x78,0x9C)
    'ELF'        = [byte[]](0x7F,0x45,0x4C,0x46)
    'XZ/LZMA'    = [byte[]](0xFD,0x37,0x7A,0x58,0x5A,0x00)
    'squashfs_le'= [byte[]](0x73,0x71,0x73,0x68)
    'squashfs_be'= [byte[]](0x68,0x73,0x71,0x73)
    'uImage'     = [byte[]](0x27,0x05,0x19,0x56)
    'BZip2'      = [byte[]](0x42,0x5A,0x68)
    'LZ4'        = [byte[]](0x04,0x22,0x4D,0x18)
    'LZMA_alone' = [byte[]](0x5D,0x00,0x00,0x80)
    'POSIX_tar'  = [byte[]](0x75,0x73,0x74,0x61,0x72)
    'cramfs'     = [byte[]](0x45,0x3D,0xCD,0x28)
    'romfs'      = [byte[]](0x2D,0x72,0x6F,0x6D,0x31,0x66,0x73,0x2D)
    'JPEG'       = [byte[]](0xFF,0xD8,0xFF)
    'PNG'        = [byte[]](0x89,0x50,0x4E,0x47)
}
""; "=== SIGNATURES ==="
foreach ($nm in $sigs.Keys) {
    $hits = FindSig $b $sigs[$nm]
    if ($hits.Count -gt 0) {
        $offsets = ($hits | ForEach-Object { "0x{0:X}" -f $_ }) -join ', '
        "  $nm  found at: $offsets"
    }
}

# String search using fast .NET Regex on Latin1 text
""; "=== PROTOCOL / COMMAND STRINGS (context: KeyValue/ProgramId/etc) ==="
$proto_rx = [regex]'(?i)(KeyValue|ProgramId|TvState|KeyCode|FavMark|FavorGroup|FromIndex|ToIndex|MobileLogin|MagicCode|platform_id|SwVersion|ModelName|GsRemote|GsMobile|do_remote|channel_switch|keep_alive|screenshot|GMS_MSG|39Wwij|Start.{0,3}End|GCDH|BegC|39W)[^\x00-\x1F\x7F-\xFF]{0,60}'
$proto_rx.Matches($text) | Select-Object -ExpandProperty Value | Select-Object -Unique | ForEach-Object { "  $_" }

""; "=== URLS / IPs / HOSTS ==="
$url_rx = [regex]'(?i)(https?://[^\x00-\x1F\x7F-\xFF]{5,80}|[a-zA-Z0-9\-]+\.(com|net|org|tv|info|cn|ir)[^\x00-\x1F\x7F-\xFF]{0,40}|\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})'
$url_rx.Matches($text) | Select-Object -ExpandProperty Value | Select-Object -Unique | ForEach-Object { "  $_" }

""; "=== VERSION / MODEL STRINGS ==="
$ver_rx = [regex]'(?i)(Ver|Version|v\d+\.\d+|MS-\d+|MediaStar|BlueMenu|4030|20688|firmware|update|model|serial)[^\x00-\x1F\x7F-\xFF]{0,60}'
$ver_rx.Matches($text) | Select-Object -ExpandProperty Value | Select-Object -Unique | Select-Object -First 80 | ForEach-Object { "  $_" }

""; "=== LONG STRINGS (>=30 chars, readable) ==="
$long_rx = [regex]'[ -~]{30,}'
$long_rx.Matches($text) | Select-Object -ExpandProperty Value | Select-Object -Unique | Select-Object -First 300 | ForEach-Object { "  $_" }

""; "=== BYTE DISTRIBUTION (first 256 offsets, count non-zero byte values) ==="
$freq = New-Object 'int[]' 256
foreach ($byt in $b) { $freq[$byt]++ }
$nonzero = ($freq | Where-Object { $_ -gt 0 }).Count
"  Non-zero byte values: $nonzero / 256"
"  Zero bytes: $($freq[0])"
"  0xFF bytes: $($freq[255])"
