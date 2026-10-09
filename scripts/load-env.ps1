# Dot-source this from the repo root to load .env into the current session:
#   . .\scripts\load-env.ps1

$envFile = Join-Path (Split-Path $PSScriptRoot -Parent) ".env"

if (-not (Test-Path $envFile)) {
    Write-Warning "No .env found at $envFile"
    return
}

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -eq "" -or $line.StartsWith("#")) { return }

    $parts = $line -split "=", 2
    if ($parts.Count -ne 2) { return }

    $name = $parts[0].Trim()
    $value = $parts[1].Trim().Trim('"')

    Set-Item -Path "Env:$name" -Value $value
}

Write-Host "Loaded environment variables from .env" -ForegroundColor Green
