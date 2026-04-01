@echo off
REM package-release.bat — Build Phantom Client and produce a fully self-contained installer
REM Usage: double-click this file (or run from command prompt)
REM Output: release\PhantomClientInstaller.jar  (bundles mod + Fabric Loader + Fabric API)
REM The only thing users need is Java 17+.

cd /d "%~dp0"
echo.
echo  Phantom Client - Release Packager
echo  ==================================
echo.

REM ── Java version check ────────────────────────────────────────────────────
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java not found.
    echo   Install Java 21 from https://adoptium.net/
    echo   Then re-run this script.
    pause & exit /b 1
)

REM Extract major version number
for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VER_RAW=%%v
)
set JAVA_VER_RAW=%JAVA_VER_RAW:"=%
for /f "delims=." %%m in ("%JAVA_VER_RAW%") do set JAVA_MAJOR=%%m

if %JAVA_MAJOR% LSS 17 (
    echo ERROR: Java %JAVA_MAJOR% detected - Java 17 or higher is required.
    echo   Install Java 21 from https://adoptium.net/
    echo   Then re-run this script.
    pause & exit /b 1
)
echo   Java %JAVA_MAJOR% detected. OK.
echo.

REM ── 1. Build the mod ──────────────────────────────────────────────────────
echo [1/4] Building Phantom Client mod...
call gradlew.bat build --no-daemon -q
if %errorlevel% neq 0 (
    echo ERROR: Gradle build failed.
    pause & exit /b 1
)

REM Find the mod JAR
set MOD_JAR=
for /f "delims=" %%f in ('dir /b /s build\libs\phantom-*.jar 2^>nul ^| findstr /v "sources dev"') do set MOD_JAR=%%f
if "%MOD_JAR%"=="" (
    echo ERROR: No phantom-*.jar found in build\libs\
    pause & exit /b 1
)
echo       Built: %MOD_JAR%

REM ── 2. Download Fabric Installer + Fabric API ─────────────────────────────
echo [2/5] Downloading bundled dependencies...
if not exist installer\deps mkdir installer\deps

if not exist installer\deps\fabric-installer.jar (
    echo       Downloading Fabric Installer...
    curl -fsSL "https://maven.fabricmc.net/net/fabricmc/fabric-installer/1.0.1/fabric-installer-1.0.1.jar" -o installer\deps\fabric-installer.jar
    if %errorlevel% neq 0 (
        echo ERROR: Could not download Fabric Installer. Check your internet connection.
        pause & exit /b 1
    )
)
echo       Fabric Installer: OK

if not exist installer\deps\fabric-api.jar (
    echo       Downloading Fabric API...
    curl -fsSL "https://cdn.modrinth.com/data/P7dR8mSH/versions/lcy3WH6P/fabric-api-0.102.0+1.21.1.jar" -o installer\deps\fabric-api.jar
    if %errorlevel% neq 0 (
        echo ERROR: Could not download Fabric API. Check your internet connection.
        pause & exit /b 1
    )
)
echo       Fabric API: OK

REM ── 3. Compile installer ───────────────────────────────────────────────────
echo [3/5] Compiling installer...
cd installer
if exist out rmdir /s /q out
mkdir out
javac -d out src\main\java\dev\phantom\installer\PhantomInstaller.java
if %errorlevel% neq 0 (
    echo ERROR: javac failed.
    cd ..
    pause & exit /b 1
)
echo       Compiled.

REM ── 4. Bundle everything into installer JAR ────────────────────────────────
echo [4/5] Bundling mod + Fabric Installer + Fabric API into installer...
copy /y "..\%MOD_JAR%"          out\phantom-1.0.0.jar >nul
copy /y deps\fabric-installer.jar out\fabric-installer.jar >nul
copy /y deps\fabric-api.jar       out\fabric-api.jar >nul
jar -cfe PhantomClientInstaller.jar dev.phantom.installer.PhantomInstaller -C out .
echo       Packaged: installer\PhantomClientInstaller.jar
cd ..

REM ── 5. Assemble release folder ────────────────────────────────────────────
echo [5/5] Assembling release folder...
if exist release rmdir /s /q release
mkdir release
copy installer\PhantomClientInstaller.jar release\PhantomClientInstaller.jar >nul
copy installer\PhantomInstaller.bat        release\PhantomClientInstaller.bat >nul
copy installer\PhantomInstaller.sh         release\PhantomClientInstaller.sh  >nul

echo Phantom Client v1.0 - Installation Instructions > release\HOW_TO_INSTALL.txt
echo ================================================ >> release\HOW_TO_INSTALL.txt
echo. >> release\HOW_TO_INSTALL.txt
echo Requirement: Java 21  (https://adoptium.net/ if not installed) >> release\HOW_TO_INSTALL.txt
echo. >> release\HOW_TO_INSTALL.txt
echo WINDOWS: >> release\HOW_TO_INSTALL.txt
echo   Double-click PhantomClientInstaller.bat >> release\HOW_TO_INSTALL.txt
echo   Press "INSTALL EVERYTHING" >> release\HOW_TO_INSTALL.txt
echo. >> release\HOW_TO_INSTALL.txt
echo After install: >> release\HOW_TO_INSTALL.txt
echo   Open Minecraft Launcher - select Fabric 1.21.1 - Play >> release\HOW_TO_INSTALL.txt
echo   Press TAB in-game to open the Phantom Client menu >> release\HOW_TO_INSTALL.txt

echo.
echo  Done! Share the release\ folder with your team.
echo  They just double-click PhantomClientInstaller.bat and press Install.
echo.
dir release\
pause
