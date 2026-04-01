#!/bin/bash
# Build the Phantom Client Installer
mkdir -p out
javac -d out src/main/java/dev/phantom/installer/PhantomInstaller.java
jar -cfe PhantomInstaller.jar dev.phantom.installer.PhantomInstaller -C out .
echo "Built: PhantomInstaller.jar"
echo "Run with: java -jar PhantomInstaller.jar"
