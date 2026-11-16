#!/usr/bin/env bash
set -e

echo "=== Honar Zistan Automated Release Builder ==="
echo "1. Cleaning previous build artifacts..."
./gradlew clean

echo "2. Ensuring upload keystore exists..."
if [ ! -f "my-upload-key.jks" ]; then
  echo "Generating upload keystore..."
  keytool -genkeypair -v -keystore my-upload-key.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000 -storepass android123 -keypass android123 -dname "CN=HonarZistan, OU=AIStudio, O=HonarZistan, L=Tehran, ST=Tehran, C=IR"
fi

echo "3. Building Signed Release APK & App Bundle (AAB for Google Play)..."
STORE_PASSWORD="${STORE_PASSWORD:-android123}" KEY_PASSWORD="${KEY_PASSWORD:-android123}" ./gradlew assembleRelease bundleRelease -x test -x lintVitalRelease -x lintVitalAnalyzeRelease --stacktrace

echo "=== Build Completed Successfully! ==="
echo "Generated APK: app/build/outputs/apk/release/app-release.apk"
echo "Generated AAB: app/build/outputs/bundle/release/app-release.aab"
