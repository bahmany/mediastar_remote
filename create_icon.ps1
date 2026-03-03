# Create a simple icon for MediaStar Remote Control
# This script creates a basic icon using Windows built-in capabilities

Add-Type -AssemblyName System.Drawing

# Create a bitmap
$bitmap = New-Object System.Drawing.Bitmap 256, 256
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)

# Set background
$graphics.Clear([System.Drawing.Color]::FromArgb(26, 26, 46))

# Draw TV shape
$tvBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(15, 52, 96))
$graphics.FillRectangle($tvBrush, 48, 64, 160, 96)

# Draw inner screen
$screenBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(22, 33, 62))
$graphics.FillRectangle($screenBrush, 56, 72, 144, 80)

# Draw signal waves
$wavePen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(0, 173, 181), 3)
$graphics.DrawArc($wavePen, 64, 32, 48, 32, 180, 180)
$graphics.DrawArc($wavePen, 80, 24, 48, 32, 180, 180)

# Draw text
$font = New-Object System.Drawing.Font("Arial", 24, [System.Drawing.FontStyle]::Bold)
$textBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White)
$graphics.DrawString("MS", $font, $textBrush, 110, 180)

# Draw AI symbol
$aiBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(0, 173, 181))
$graphics.FillEllipse($aiBrush, 188, 44, 24, 24)
$aiFont = New-Object System.Drawing.Font("Arial", 12)
$graphics.DrawString("AI", $aiFont, $textBrush, 194, 50)

# Save as PNG first (ICO conversion requires special library)
$bitmap.Save("icon_temp.png", [System.Drawing.Imaging.ImageFormat]::Png)

Write-Host "Basic icon created as icon_temp.png"
Write-Host "To convert to ICO, use an online tool or specialized software"
Write-Host "Recommended: https://favicon.io/image-converter/"

# Cleanup
$graphics.Dispose()
$bitmap.Dispose()
