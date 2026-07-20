# Implementation Plan - Fix Build Errors and Address SSL Issue

The project currently has compilation errors that prevent it from building. Additionally, the user is experiencing an `SSL peer shut down incorrectly` error, which likely occurs during network operations.

## User Review Required

> [!IMPORTANT]
> The `SSL peer shut down incorrectly` error is usually caused by an unexpected termination of an SSL/TLS handshake. Since the `BASE_URL` in `build.gradle.kts` is currently set to `http`, this error suggests that either:
> 1. The server is redirecting the `http` request to `https`.
> 2. The app is making an `https` request to a server that doesn't support it or has a misconfigured SSL certificate.
> 3. There is a proxy or firewall interfering with the connection.

## Proposed Changes

### Fix Compilation Errors

#### [MODIFY] [AuthInterceptor.kt](file:///C:/Users/patrich/Documents/2026-I/APLICACIONES%20MOVILES%20I/PetPal/Frontend/app/src/main/java/com/petpal/app/data/remote/AuthInterceptor.kt)
- Change `encodedPath()` to `encodedPath` to match the property access required by the current OkHttp version.

#### [MODIFY] [Theme.kt](file:///C:/Users/patrich/Documents/2026-I/APLICACIONES%20MOVILES%20I/PetPal/Frontend/app/src/main/java/com/petpal/app/ui/theme/Theme.kt)
- Add missing `import androidx.compose.ui.graphics.Color` to resolve the unresolved reference.

### Address SSL Error

#### [DEBUG] Investigate Network Traffic
- I will suggest adding a more robust `OkHttpClient` configuration or logging to verify the exact URL being called when the error occurs.
- If the server is indeed local (using `localIp`), ensure it's not trying to use SSL unless configured.

## Verification Plan

### Automated Tests
- Run `./gradlew app:assembleDebug` to verify that the compilation errors are fixed.

### Manual Verification
- Once the app builds, the user should be able to run it and check the Logcat for the exact network call causing the SSL error.
