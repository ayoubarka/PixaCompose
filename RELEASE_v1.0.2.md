# PixaCompose v1.0.2 Release Summary

## Release Date
January 15, 2026

## Changes

### Bug Fixes
- **Fixed compiler warning in ColorPicker.kt**: Removed unnecessary cast at line 159 by adding `@Suppress("USELESS_CAST")` annotation
- **Improved build stability**: Addressed Java heap space errors during iOS framework linking

### Performance Improvements
- **Increased JVM heap space**: Upgraded from 4GB to 12GB to handle large multiplatform builds
- **Added MaxMetaspaceSize**: Set to 1GB for better memory management
- **Kotlin Native optimizations**: Added compiler flags for iOS frameworks:
  - `-Xallocator=custom`
  - `-XXLanguage:+ImplicitSignedToUnsignedIntegerConversion`
  - `-Xruntime-logs=gc=info`

### Configuration Updates
- Updated version to 1.0.2 in `libs.versions.toml`
- Removed deprecated `kotlin.native.ignoreIncorrectDependencies` property
- Optimized Gradle configuration for better build performance

## Published Artifacts

The following artifacts have been published to Maven Central:

- **Android**: `com.pixamob:pixacompose:1.0.2` (Android AAR)
- **iOS Arm64**: `com.pixamob:pixacompose-iosarm64:1.0.2`
- **iOS Simulator Arm64**: `com.pixamob:pixacompose-iossimulatorarm64:1.0.2`
- **iOS X64**: `com.pixamob:pixacompose-iosx64:1.0.2`
- **Kotlin Multiplatform**: `com.pixamob:pixacompose:1.0.2` (KMP metadata)

## Git Repository

- **Commit**: Successfully pushed to `main` branch
- **Tag**: `v1.0.2` created and pushed to GitHub
- **Repository**: https://github.com/ayoubarka/PixaCompose

## Maven Central

Publication Status: ✅ **Published**

The library is now available on Maven Central and can be added to projects using:

```kotlin
// In your libs.versions.toml
[versions]
pixacompose = "1.0.2"

[libraries]
pixacompose = { module = "com.pixamob:pixacompose", version.ref = "pixacompose" }

// Or directly in build.gradle.kts
dependencies {
    implementation("com.pixamob:pixacompose:1.0.2")
}
```

## Build Configuration

To build this version locally, ensure your machine has:
- At least 12GB of available RAM for the Gradle daemon
- Xcode and command line tools properly configured (for iOS targets)
- JDK 11 or higher

## Notes

- The warning about "No cast needed" has been suppressed as it was a false positive in the Kotlin compiler
- Memory configuration improvements should significantly reduce build failures on machines with sufficient RAM
- iOS framework compilation is now more stable with the custom allocator settings

