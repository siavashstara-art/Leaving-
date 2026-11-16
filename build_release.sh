#!/usr/bin/env bash
set -e

echo "=== Honar Zistan Automated Release Builder ==="
echo "1. Cleaning previous build artifacts..."
./gradlew clean

echo "2. Building Signed Release APK & App Bundle (AAB for Google Play)..."
./gradlew assembleRelease bundleRelease -x test

echo "=== Build Completed Successfully! ==="
echo "Generated APK: app/build/outputs/apk/release/app-release.apk"
echo "Generated AAB: app/build/outputs/bundle/release/app-release.aab"
