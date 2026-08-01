# ============================================================
#  AltaPinta - Arranque del backend
#
#  Uso:   .\run.ps1
#
#  Por que existe este script:
#  El proyecto compila y corre con Java 17 (ver <java.version> en pom.xml),
#  pero en esta maquina el "java" del PATH es un JDK mas nuevo. Spring Boot
#  3.3.5 es anterior a esos JDK y puede fallar de formas raras al arrancar.
#
#  El script fija JAVA_HOME SOLO para esta ventana de PowerShell. No cambia
#  la configuracion del sistema ni afecta a otros proyectos.
# ============================================================

$ErrorActionPreference = "Stop"

# --- 1. Ubicarse en la carpeta del script ---
Set-Location -Path $PSScriptRoot

# --- 2. Elegir JDK 17 ---
$candidatos = @(
    "C:\Program Files\Java\jdk-17",
    "C:\Program Files\Eclipse Adoptium\jdk-17",
    "$env:LOCALAPPDATA\Programs\Eclipse Adoptium\jdk-17"
)

$jdk = $null
foreach ($ruta in $candidatos) {
    if (Test-Path (Join-Path $ruta "bin\java.exe")) { $jdk = $ruta; break }
}

if ($null -eq $jdk) {
    Write-Host "ERROR: no se encontro un JDK 17." -ForegroundColor Red
    Write-Host "Instalalo desde https://adoptium.net/temurin/releases/?version=17"
    Write-Host "o edita la lista `$candidatos al inicio de este script."
    exit 1
}

$env:JAVA_HOME = $jdk
$env:PATH = "$jdk\bin;$env:PATH"

Write-Host "JDK  : $jdk" -ForegroundColor Cyan

# --- 3. Avisar si faltan los secretos ---
if (-not (Test-Path ".\secrets.properties")) {
    Write-Host ""
    Write-Host "AVISO: falta secrets.properties." -ForegroundColor Yellow
    Write-Host "Crealo con:  Copy-Item secrets.properties.example secrets.properties"
    Write-Host "y rellena DB_PASSWORD, MAIL_PASSWORD y JWT_SECRET."
    Write-Host ""
}

# --- 4. Arrancar ---
Write-Host "Arrancando backend en http://localhost:8080 ..." -ForegroundColor Cyan
Write-Host ""

& ".\mvnw.cmd" spring-boot:run
exit $LASTEXITCODE
