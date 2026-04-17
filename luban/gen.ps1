param(
    [string]$LubanDll,
    [string]$JsonOutputDir = (Join-Path $PSScriptRoot "..\\src\\main\\resources\\config")
)

$ErrorActionPreference = "Stop"

function Resolve-LubanDll {
    param([string]$ExplicitPath)

    $candidates = @()
    if ($ExplicitPath) {
        $candidates += $ExplicitPath
    }
    if ($env:LUBAN_DLL) {
        $candidates += $env:LUBAN_DLL
    }

    $candidates += @(
        (Join-Path $PSScriptRoot "..\\tools\\Luban\\Luban.dll"),
        (Join-Path $PSScriptRoot "..\\Tools\\Luban\\Luban.dll"),
        (Join-Path $PSScriptRoot "Tools\\Luban\\Luban.dll")
    )

    foreach ($candidate in $candidates) {
        if (-not $candidate) {
            continue
        }

        $resolved = Resolve-Path -LiteralPath $candidate -ErrorAction SilentlyContinue
        if ($resolved) {
            return $resolved.Path
        }
    }

    throw "Luban.dll was not found. Pass -LubanDll or set the LUBAN_DLL environment variable."
}

$resolvedLubanDll = Resolve-LubanDll -ExplicitPath $LubanDll
$resolvedJsonOutputDir = [System.IO.Path]::GetFullPath($JsonOutputDir)

New-Item -ItemType Directory -Force -Path $resolvedJsonOutputDir | Out-Null
Get-ChildItem -LiteralPath $resolvedJsonOutputDir -Force -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force

$confPath = Join-Path $PSScriptRoot "luban.conf"

& dotnet $resolvedLubanDll `
    -t server `
    -d json `
    --conf $confPath `
    -x "json.outputDataDir=$resolvedJsonOutputDir"

if ($LASTEXITCODE -ne 0) {
    throw "Luban export failed with exit code: $LASTEXITCODE"
}

Write-Host "Luban JSON output: $resolvedJsonOutputDir"
