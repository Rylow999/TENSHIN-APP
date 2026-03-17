$fontDir = "app\src\main\res\font"
New-Item -ItemType Directory -Force -Path $fontDir | Out-Null

$fonts = @{
    "exo2_light"     = "https://fonts.gstatic.com/s/exo2/v21/7cH1PD18ifOjBh1i55kvewxFJ3ZJ-jBA.ttf"
    "exo2_regular"   = "https://fonts.gstatic.com/s/exo2/v21/7cH1PD18ifOjBh1i55kvewxFJ_ZJ.ttf"
    "exo2_medium"    = "https://fonts.gstatic.com/s/exo2/v21/7cH1PD18ifOjBh1i55kvewxFJ0NJ-jBA.ttf"
    "exo2_semibold"  = "https://fonts.gstatic.com/s/exo2/v21/7cH1PD18ifOjBh1i55kvewxFJ1ZJ-jBA.ttf"
    "exo2_bold"      = "https://fonts.gstatic.com/s/exo2/v21/7cH1PD18ifOjBh1i55kvewxFJ2BJ-jBA.ttf"
    "exo2_extrabold" = "https://fonts.gstatic.com/s/exo2/v21/7cH1PD18ifOjBh1i55kvewxFJ2tJ-jBA.ttf"
}

$root = (Get-Location).Path

foreach ($name in $fonts.Keys) {
    $url  = $fonts[$name]
    $dest = Join-Path $root $fontDir "$name.ttf"
    Write-Host "Descargando $name..."
    try {
        $wc = New-Object System.Net.WebClient
        $wc.DownloadFile($url, $dest)
        $size = (Get-Item $dest).Length
        Write-Host "  OK: $name.ttf ($size bytes)" -ForegroundColor Green
    } catch {
        Write-Host "  FALLO: $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Fuentes descargadas en: $fontDir" -ForegroundColor Cyan
