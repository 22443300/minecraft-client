#!/usr/bin/env bash
# Phantom Client Installer — macOS / Linux launcher
set -e

DIR="$(cd "$(dirname "$0")" && pwd)"

if ! command -v java &>/dev/null; then
    echo "Java 21 is required but was not found."
    echo "Download it from: https://adoptium.net/"
    exit 1
fi

java -jar "$DIR/PhantomInstaller.jar"
