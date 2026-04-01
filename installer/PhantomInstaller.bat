@echo off
title Phantom Client Installer
cd /d "%~dp0"

where java >nul 2>&1
if %errorlevel% neq 0 (
    echo Java 21 is required but was not found.
    echo Download it from: https://adoptium.net/
    pause
    exit /b 1
)

java -jar "%~dp0PhantomInstaller.jar"
