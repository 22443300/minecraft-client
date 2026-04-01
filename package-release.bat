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

REM ── 2. Compile installer ──────────────────────────────────────────────────
echo [2/3] Compiling installer...
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

REM ── 3. Bundle mod JAR into installer ─────────────────────────────────────
echo [3/3] Bundling mod into installer...
copy /y "%MOD_JAR%" out\phantom-1.0.0.jar >nul
if %errorlevel% neq 0 ( echo ERROR: Failed to copy mod JAR. & cd .. & pause & exit /b 1 )
jar -cfe PhantomClientInstaller.jar dev.phantom.installer.PhantomInstaller -C out .
echo       Packaged: installer\PhantomClientInstaller.jar
cd ..

REM ── 4. Assemble release folder ────────────────────────────────────────────
echo Assembling release folder...
if exist release rmdir /s /q release
mkdir release
copy installer\PhantomClientInstaller.jar release\PhantomClientInstaller.jar >nul
copy installer\PhantomInstaller.bat        release\PhantomClientInstaller.bat >nul
copy installer\PhantomInstaller.sh         release\PhantomClientInstaller.sh  >nul

echo Phantom Client v1.0 - Installation Instructions > release\HOW_TO_INSTALL.txt
echo ================================================ >> release\HOW_TO_INSTALL.txt
echo. >> release\HOW_TO_INSTALL.txt
echo PREREQUISITES (do these first if not already done): >> release\HOW_TO_INSTALL.txt
echo   1. Java 21         https://adoptium.net/ >> release\HOW_TO_INSTALL.txt
echo   2. Fabric Loader   https://fabricmc.net/use/installer/ >> release\HOW_TO_INSTALL.txt
echo      - Select Minecraft 1.21.1 + Loader 0.15.11, click Install >> release\HOW_TO_INSTALL.txt
echo   3. Fabric API      https://modrinth.com/mod/fabric-api >> release\HOW_TO_INSTALL.txt
echo      - Download fabric-api-0.102.0+1.21.1.jar into your mods folder >> release\HOW_TO_INSTALL.txt
echo. >> release\HOW_TO_INSTALL.txt
echo INSTALL PHANTOM CLIENT: >> release\HOW_TO_INSTALL.txt
echo   Double-click PhantomClientInstaller.bat >> release\HOW_TO_INSTALL.txt
echo   Press "INSTALL PHANTOM CLIENT" >> release\HOW_TO_INSTALL.txt
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
