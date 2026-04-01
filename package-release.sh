#!/usr/bin/env bash
# package-release.sh — Build Phantom Client and produce a fully self-contained installer
# Usage: ./package-release.sh
# Output: release/PhantomClientInstaller.jar  +  release/PhantomClientInstaller.app.zip
#
# The installer bundles EVERYTHING — Phantom mod, Fabric Loader installer, Fabric API.
# The only thing users need is Java 17+.

set -e
cd "$(dirname "$0")"

FABRIC_INSTALLER_URL="https://maven.fabricmc.net/net/fabricmc/fabric-installer/1.0.1/fabric-installer-1.0.1.jar"
FABRIC_API_URL="https://cdn.modrinth.com/data/P7dR8mSH/versions/lcy3WH6P/fabric-api-0.102.0+1.21.1.jar"

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
echo "[1/5] Building Phantom Client mod..."
./gradlew build --no-daemon -q
MOD_JAR=$(find build/libs -name "phantom-*.jar" ! -name "*-sources*" ! -name "*-dev*" | head -1)
if [ -z "$MOD_JAR" ]; then
    echo "ERROR: Build failed — no phantom-*.jar found in build/libs/"
    exit 1
fi
echo "      Built: $MOD_JAR"

# ── 2. Download Fabric Installer + Fabric API ────────────────────────────────
echo "[2/5] Downloading bundled dependencies..."
mkdir -p installer/deps

if [ ! -f installer/deps/fabric-installer.jar ]; then
    echo "      Downloading Fabric Installer..."
    curl -fsSL "$FABRIC_INSTALLER_URL" -o installer/deps/fabric-installer.jar
fi
echo "      Fabric Installer: OK"

if [ ! -f installer/deps/fabric-api.jar ]; then
    echo "      Downloading Fabric API..."
    curl -fsSL "$FABRIC_API_URL" -o installer/deps/fabric-api.jar
fi
echo "      Fabric API: OK"

# ── 3. Compile installer ─────────────────────────────────────────────────────
echo "[3/5] Compiling installer..."
cd installer
rm -rf out && mkdir out
javac -d out src/main/java/dev/phantom/installer/PhantomInstaller.java
echo "      Compiled."

# ── 4. Bundle everything into installer JAR ───────────────────────────────────
echo "[4/5] Bundling mod + Fabric Installer + Fabric API into installer..."
cp "../$MOD_JAR"        out/phantom-1.0.0.jar
cp deps/fabric-installer.jar out/fabric-installer.jar
cp deps/fabric-api.jar       out/fabric-api.jar
jar -cfe PhantomClientInstaller.jar dev.phantom.installer.PhantomInstaller -C out .
SIZE=$(du -sh PhantomClientInstaller.jar | cut -f1)
echo "      Packaged: installer/PhantomClientInstaller.jar ($SIZE)"
cd ..

# ── 5. Assemble release folder ────────────────────────────────────────────────
echo "[5/5] Assembling release + macOS .app bundle..."
rm -rf release && mkdir release
cp installer/PhantomClientInstaller.jar release/
cp installer/PhantomInstaller.bat       release/PhantomClientInstaller.bat
cp installer/PhantomInstaller.sh        release/PhantomClientInstaller.sh
chmod +x release/PhantomClientInstaller.sh

cat > release/HOW_TO_INSTALL.txt << 'EOF'
Phantom Client v1.0 — Installation Instructions
================================================

ONLY requirement: Java 17+  (https://adoptium.net/ — free download)
Everything else (Fabric Loader, Fabric API, the mod) is bundled inside.
No internet connection needed during install.

MAC (double-click app — easiest):
  Unzip PhantomClientInstaller.zip
  Double-click PhantomClientInstaller.app
  (If blocked: right-click → Open → Open anyway)
  Press "INSTALL EVERYTHING"

WINDOWS:
  Double-click PhantomClientInstaller.bat
  Press "INSTALL EVERYTHING"

LINUX / MANUAL (any platform):
  java -jar PhantomClientInstaller.jar

After install:
  Open Minecraft Launcher → select Fabric 1.21.1 → Play
  Press TAB in-game to open the Phantom Client menu
EOF

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
