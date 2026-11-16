#!/usr/bin/env bash
set -e

echo "=== Honar Zistan Automated Release Builder ==="
echo "1. Cleaning previous build artifacts..."
gradle clean

echo "2. Running unit and Robolectric tests..."
gradle :app:testDebugUnitTest

echo "3. Building Signed Release APK..."
gradle assembleRelease

echo "4. Building Signed Release App Bundle (AAB for Google Play)..."
gradle bundleRelease

echo "=== Build Completed Successfully! ==="
echo "Generated APK: app/build/outputs/apk/release/app-release.apk"
echo "Generated AAB: app/build/outputs/bundle/release/app-release.aab"
