# Phantom Client Installer

A standalone Java Swing GUI installer for Phantom Client.

## Building
```bash
cd installer
./build.sh
```
This produces `PhantomInstaller.jar`.

## Distributing
Place `PhantomInstaller.jar` and `phantom-1.0.0.jar` (built from the main project) in the same folder, then share both files together.

## Running
```bash
java -jar PhantomInstaller.jar
```
Or double-click on Windows/macOS if Java is associated with `.jar` files.

## What it does
1. Auto-detects your `.minecraft` directory
2. Checks whether Fabric Loader is installed (guides you if not)
3. Downloads Fabric API automatically from Modrinth
4. Copies `phantom-*.jar` into your mods folder
5. Dark-themed UI matching Phantom Client's aesthetic
