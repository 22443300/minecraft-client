#!/usr/bin/env bash
# create-macapp.sh — Wraps PhantomClientInstaller.jar into a macOS .app bundle
# Run AFTER package-release.sh (or manually point JAR_SRC at any installer JAR).
#
# Output: release/PhantomClientInstaller.app
#         release/PhantomClientInstaller.zip  (ready to share)

set -e
cd "$(dirname "$0")"

APP_NAME="PhantomClientInstaller"
APP_BUNDLE="release/${APP_NAME}.app"

# ── Locate the installer JAR ─────────────────────────────────────────────────
JAR_SRC=""
for candidate in \
    "release/PhantomClientInstaller.jar" \
    "installer/PhantomClientInstaller.jar" \
    "installer/PhantomInstaller.jar"; do
    if [ -f "$candidate" ]; then JAR_SRC="$candidate"; break; fi
done

if [ -z "$JAR_SRC" ]; then
    echo "ERROR: Installer JAR not found."
    echo "  Run ./package-release.sh first, then re-run this script."
    exit 1
fi
echo "Using JAR: $JAR_SRC"

# ── Build .app structure ─────────────────────────────────────────────────────
rm -rf "$APP_BUNDLE"
mkdir -p "${APP_BUNDLE}/Contents/MacOS"
mkdir -p "${APP_BUNDLE}/Contents/Resources"

cp "$JAR_SRC" "${APP_BUNDLE}/Contents/Resources/PhantomInstaller.jar"

# ── Info.plist ───────────────────────────────────────────────────────────────
cat > "${APP_BUNDLE}/Contents/Info.plist" << 'PLIST'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN"
    "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>
    <string>PhantomClientInstaller</string>
    <key>CFBundleIdentifier</key>
    <string>dev.phantom.installer</string>
    <key>CFBundleName</key>
    <string>Phantom Client Installer</string>
    <key>CFBundleDisplayName</key>
    <string>Phantom Client Installer</string>
    <key>CFBundleVersion</key>
    <string>1.0.0</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0.0</string>
    <key>CFBundlePackageType</key>
    <string>APPL</string>
    <key>NSHighResolutionCapable</key>
    <true/>
    <key>LSMinimumSystemVersion</key>
    <string>10.13</string>
    <key>NSHumanReadableCopyright</key>
    <string>Copyright 2024 Phantom Client</string>
    <key>NSRequiresAquaSystemAppearance</key>
    <false/>
</dict>
</plist>
PLIST

# ── Launcher script ──────────────────────────────────────────────────────────
cat > "${APP_BUNDLE}/Contents/MacOS/PhantomClientInstaller" << 'LAUNCHER'
#!/bin/bash
# Phantom Client Installer — macOS launcher
# 1. Finds Java 17+ from common locations
# 2. Shows a native dialog and opens download page if Java is missing/old
# 3. Launches the installer GUI

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$SCRIPT_DIR/../Resources/PhantomInstaller.jar"
MIN_VER=17
JAVA_DL="https://adoptium.net/temurin/releases/?version=21&os=mac&arch=any&package=jre"

# ── Locate java executable ───────────────────────────────────────────────────
find_java() {
    # 1. JAVA_HOME env var
    if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
        echo "$JAVA_HOME/bin/java"; return 0
    fi
    # 2. Homebrew OpenJDK (Apple Silicon and Intel paths)
    for p in \
        /opt/homebrew/opt/openjdk@21/bin/java \
        /opt/homebrew/opt/openjdk/bin/java \
        /usr/local/opt/openjdk@21/bin/java \
        /usr/local/opt/openjdk/bin/java; do
        [ -x "$p" ] && echo "$p" && return 0
    done
    # 3. macOS java_home utility (lists all JVMs)
    if [ -x /usr/libexec/java_home ]; then
        local jh
        jh=$(/usr/libexec/java_home 2>/dev/null) && \
            [ -n "$jh" ] && [ -x "$jh/bin/java" ] && \
            echo "$jh/bin/java" && return 0
    fi
    # 4. Common system paths
    for p in /usr/bin/java /usr/local/bin/java; do
        [ -x "$p" ] && echo "$p" && return 0
    done
    # 5. PATH lookup
    command -v java 2>/dev/null
}

JAVA_EXEC="$(find_java)"

# ── Check Java is present ────────────────────────────────────────────────────
if [ -z "$JAVA_EXEC" ]; then
    osascript << OSAS
display dialog "Java $MIN_VER+ is required to run Phantom Client Installer, but Java was not found on this Mac.

Click \"Download Java\" to open the download page. After installing Java 21, double-click Phantom Client Installer again." ¬
    buttons {"Cancel", "Download Java"} default button "Download Java" ¬
    with title "Java Required" with icon caution
if button returned of result is "Download Java" then
    open location "$JAVA_DL"
end if
OSAS
    exit 1
fi

# ── Check Java version ───────────────────────────────────────────────────────
RAW=$("$JAVA_EXEC" -version 2>&1 | head -1)
# Handles: java version "21.0.1", java version "1.8.0_301", openjdk version "17.0.5"
VER=$(echo "$RAW" | sed 's/[^"]*"\([^"]*\)".*/\1/')
MAJOR=$(echo "$VER" | cut -d. -f1)
# Legacy 1.x format — real version is second component
if [ "$MAJOR" = "1" ]; then
    MAJOR=$(echo "$VER" | cut -d. -f2)
fi

if [ "${MAJOR:-0}" -lt "$MIN_VER" ] 2>/dev/null; then
    osascript << OSAS
display dialog "Java $MAJOR detected, but Java $MIN_VER or higher is required.

Click \"Download Java\" to get Java 21, then double-click Phantom Client Installer again." ¬
    buttons {"Cancel", "Download Java"} default button "Download Java" ¬
    with title "Java Update Required" with icon caution
if button returned of result is "Download Java" then
    open location "$JAVA_DL"
end if
OSAS
    exit 1
fi

# ── Launch the installer GUI ─────────────────────────────────────────────────
exec "$JAVA_EXEC" \
    -Dapple.awt.application.name="Phantom Client Installer" \
    -Dapple.laf.useScreenMenuBar=true \
    -Dawt.useSystemAAFontSettings=on \
    -Dswing.aatext=true \
    -jar "$JAR" "$@"
LAUNCHER

chmod +x "${APP_BUNDLE}/Contents/MacOS/PhantomClientInstaller"

# ── Zip it for easy sharing ──────────────────────────────────────────────────
ZIP_OUT="release/${APP_NAME}.zip"
rm -f "$ZIP_OUT"
(cd release && zip -qr "${APP_NAME}.zip" "${APP_NAME}.app")
echo ""
echo "  Created: $APP_BUNDLE"
echo "  Created: $ZIP_OUT"
echo ""
echo "  Share the .zip with your team."
echo "  They unzip it, double-click ${APP_NAME}.app, and press INSTALL."
