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
echo "[4/4] Assembling release folder..."
rm -rf release && mkdir release
cp installer/PhantomClientInstaller.jar release/
cp installer/PhantomInstaller.bat       release/PhantomClientInstaller.bat
cp installer/PhantomInstaller.sh        release/PhantomClientInstaller.sh
chmod +x release/PhantomClientInstaller.sh

cat > release/HOW_TO_INSTALL.txt << 'EOF'
Phantom Client v1.0 — Installation Instructions
================================================

Requirement: Java 21  (https://adoptium.net/ if not installed)

WINDOWS:
  Double-click PhantomClientInstaller.bat
  Press "INSTALL EVERYTHING"

MAC / LINUX:
  Double-click PhantomClientInstaller.sh
  (or: chmod +x PhantomClientInstaller.sh && ./PhantomClientInstaller.sh)
  Press "INSTALL EVERYTHING"

MANUAL (any platform):
  java -jar PhantomClientInstaller.jar

After install:
  Open Minecraft Launcher → select Fabric 1.21.1 → Play
  Press TAB in-game to open the Phantom Client menu
EOF

echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║  Done!  Share the entire  release/  folder           ║"
echo "║  OR just share  PhantomClientInstaller.jar  alone    ║"
echo "║  Team members just double-click and press Install.   ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""
ls -lh release/
