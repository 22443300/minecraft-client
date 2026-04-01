#!/usr/bin/env bash
# package-release.sh — Build Phantom Client and produce a single shareable installer JAR
# Usage: ./package-release.sh
# Output: release/PhantomClientInstaller.jar  (share this one file with your team)

set -e
cd "$(dirname "$0")"

echo ""
echo "╔══════════════════════════════════════╗"
echo "║   Phantom Client — Release Packager  ║"
echo "╚══════════════════════════════════════╝"
echo ""

# ── Java version check ───────────────────────────────────────────────────────
JAVA_VER=$(java -version 2>&1 | head -1 | sed 's/.*version "\([0-9]*\).*/\1/')
if [ -z "$JAVA_VER" ]; then
    echo "ERROR: Java not found."
    echo "  Install Java 21 from https://adoptium.net/ then re-run this script."
    exit 1
fi
if [ "$JAVA_VER" -lt 17 ] 2>/dev/null; then
    echo "ERROR: Java $JAVA_VER detected — Java 17 or higher is required."
    echo "  Install Java 21 from https://adoptium.net/ then re-run this script."
    exit 1
fi
echo "  Java $JAVA_VER detected. OK."
echo ""

# ── 1. Build the mod ────────────────────────────────────────────────────────
echo "[1/4] Building Phantom Client mod..."
./gradlew build --no-daemon -q
MOD_JAR=$(find build/libs -name "phantom-*.jar" ! -name "*-sources*" ! -name "*-dev*" | head -1)
if [ -z "$MOD_JAR" ]; then
    echo "ERROR: Build failed — no phantom-*.jar found in build/libs/"
    exit 1
fi
echo "      Built: $MOD_JAR"

# ── 2. Compile installer ─────────────────────────────────────────────────────
echo "[2/4] Compiling installer..."
cd installer
rm -rf out && mkdir out
javac -d out src/main/java/dev/phantom/installer/PhantomInstaller.java
echo "      Compiled."

# ── 3. Bundle mod JAR into installer ─────────────────────────────────────────
echo "[3/4] Bundling mod into installer..."
cp "../$MOD_JAR" out/phantom-1.0.0.jar
jar -cfe PhantomClientInstaller.jar dev.phantom.installer.PhantomInstaller -C out .
SIZE=$(du -sh PhantomClientInstaller.jar | cut -f1)
echo "      Packaged: installer/PhantomClientInstaller.jar ($SIZE)"
cd ..

# ── 4. Assemble release folder ────────────────────────────────────────────────
echo "[4/5] Assembling release folder..."
rm -rf release && mkdir release
cp installer/PhantomClientInstaller.jar release/
cp installer/PhantomInstaller.bat       release/PhantomClientInstaller.bat
cp installer/PhantomInstaller.sh        release/PhantomClientInstaller.sh
chmod +x release/PhantomClientInstaller.sh

cat > release/HOW_TO_INSTALL.txt << 'EOF'
Phantom Client v1.0 — Installation Instructions
================================================

Requirement: Java 21  (https://adoptium.net/ if not installed)

MAC (easiest — double-click app):
  Unzip PhantomClientInstaller.zip
  Double-click PhantomClientInstaller.app
  Press "INSTALL EVERYTHING"
  (If macOS blocks it: right-click → Open → Open anyway)

WINDOWS:
  Double-click PhantomClientInstaller.bat
  Press "INSTALL EVERYTHING"

LINUX / MANUAL:
  java -jar PhantomClientInstaller.jar

After install:
  Open Minecraft Launcher → select Fabric 1.21.1 → Play
  Press TAB in-game to open the Phantom Client menu
EOF

# ── 5. Build macOS .app bundle ────────────────────────────────────────────────
echo "[5/5] Building macOS .app bundle..."
bash create-macapp.sh

echo ""
echo "╔══════════════════════════════════════════════════════════╗"
echo "║  Done!  Files in  release/                               ║"
echo "║                                                          ║"
echo "║  Mac team members:   share PhantomClientInstaller.zip   ║"
echo "║                      (unzip → double-click .app)        ║"
echo "║  Windows:            share PhantomClientInstaller.bat   ║"
echo "║  All platforms:      java -jar PhantomClientInstaller.jar║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""
ls -lh release/
