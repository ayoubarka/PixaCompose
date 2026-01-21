# PixaCompose v1.0.7 Release Notes

**Release Date**: January 21, 2026  
**Version**: 1.0.7 (Code: 8)  
**Status**: ✅ Published to Maven Central

---

## 🎉 Major Features

### 1. Global Toast & Snackbar Manager System
- **PixaToastManager** - Singleton for global toast access
- **PixaSnackbarManager** - Singleton for global snackbar access
- **GlobalToastHost** & **GlobalSnackbarHost** - Root-level composables
- **LocalToastManager** & **LocalSnackbarManager** - CompositionLocals for overrides
- Full thread-safe implementation with Mutex synchronization

### 2. AnimationUtils Integration
- **StandardSpring()** - Smooth bouncy animations for enter transitions
- **FastSpring()** - Quick animations for exit transitions
- **StandardTween()** - Standard fade in (300ms)
- **FastTween()** - Quick fade out (200ms)
- Applied to both Toast and Snackbar components

### 3. Divider Component Simplification
- ❌ Removed `DividerVariant` enum (Subtle, Default, Strong)
- ✅ Single unified divider with configurable thickness
- ✅ Maintains `HorizontalDivider()` and `VerticalDivider()` convenience functions
- ✅ Uses default theme color with optional custom color support

### 4. Enhanced Documentation
- Updated **AI_COMPONENTS_GUIDE.md** with global manager patterns
- Added **QUICK_START_GUIDE.md** with 15+ real-world examples
- Added **IMPLEMENTATION_SUMMARY.md** with technical details
- Comprehensive usage examples for ViewModel, UseCase, Composable contexts

---

## 📋 What's Changed

### Toast.kt
```
✅ Added AnimationUtils import
✅ Replaced inline animation specs with AnimationUtils calls
✅ Added PixaToastManager singleton object (thread-safe)
✅ Added GlobalToastHost composable
✅ Added LocalToastManager composition local
✅ Added rememberToastScope() and ToastScope class
✅ Added launchToast() extension function
✅ Full documentation with 10+ usage examples
```

### Snackbar.kt
```
✅ Added AnimationUtils import
✅ Replaced inline animation specs with AnimationUtils calls
✅ Added PixaSnackbarManager singleton object (thread-safe)
✅ Added GlobalSnackbarHost composable
✅ Added LocalSnackbarManager composition local
✅ Added rememberSnackbarScope() and SnackbarScope class
✅ Added launchSnackbar() extension function
✅ Full documentation with 10+ usage examples
✅ Removed unused spring/tween imports
```

### Divider.kt
```
✅ Removed DividerVariant enum
✅ Removed getDividerTheme() function
✅ Updated PixaDivider() - removed variant parameter
✅ Updated HorizontalDivider() - removed variant parameter
✅ Updated VerticalDivider() - removed variant parameter
❌ Removed SubtleDivider() function
❌ Removed StrongDivider() function
✅ Removed unused dp import
✅ Updated usage examples
```

### AI_COMPONENTS_GUIDE.md
```
✅ Updated Toast section with global manager setup
✅ Updated Snackbar section with global manager setup
✅ Updated Divider section (renamed to PixaDivider)
✅ Updated imports reference with new global manager classes
✅ Added setup examples for App root
✅ Added ViewModel usage examples
✅ Added Composable usage examples
```

### Version Bump
```
gradle/libs.versions.toml:
  appVersionCode: 7 → 8
  appVersionName: 1.0.6 → 1.0.7
```

---

## 🚀 Usage Examples

### Quick Start - Setup at App Root
```kotlin
@Composable
fun App() {
    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Initialize once at app root
            GlobalToastHost(position = ToastPosition.Bottom)
            GlobalSnackbarHost()
            
            Scaffold {
                Navigation()
            }
        }
    }
}
```

### From ViewModel
```kotlin
class HabitViewModel : ViewModel() {
    fun createHabit(habit: Habit) {
        viewModelScope.launch {
            try {
                repository.create(habit)
                PixaToastManager.showSuccess("Habit created!")
                PixaSnackbarManager.showSuccess(
                    message = "Habit added successfully",
                    actionLabel = "View",
                    onAction = { navigateToHabitDetail(habit.id) }
                )
            } catch (e: Exception) {
                PixaToastManager.showError("Failed: ${e.message}")
                PixaSnackbarManager.showErrorFromException(e)
            }
        }
    }
}
```

### From Composable
```kotlin
@Composable
fun MyScreen() {
    val toastScope = rememberToastScope()
    val snackbarScope = rememberSnackbarScope()
    
    Button(onClick = {
        toastScope.showSuccess("Copied to clipboard!")
        snackbarScope.showSuccess(
            message = "Item saved",
            actionLabel = "Undo",
            onAction = { /* undo */ }
        )
    }) {
        Text("Save & Copy")
    }
}
```

### From UseCase
```kotlin
class SyncDataUseCase(private val repository: Repository) {
    suspend operator fun invoke() {
        try {
            repository.sync()
            PixaToastManager.showSuccess("Data synced!")
        } catch (e: NetworkException) {
            PixaSnackbarManager.showError(
                message = "Sync failed",
                actionLabel = "Retry",
                onAction = { invoke() }
            )
        }
    }
}
```

---

## 🔄 Animation Improvements

### Before (Manual specs)
```kotlin
slideInVertically(
    initialOffsetY = { -it },
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
) + fadeIn(animationSpec = tween(300))
```

### After (AnimationUtils)
```kotlin
slideInVertically(
    initialOffsetY = { -it },
    animationSpec = AnimationUtils.standardSpring()
) + fadeIn(animationSpec = AnimationUtils.standardTween())
```

**Benefits**:
- ✅ Consistent animations across components
- ✅ Easy to maintain and update
- ✅ Centralized configuration
- ✅ Reduced code duplication

---

## 📦 Maven Central Publication

**Artifact**: `com.pixamob:pixacompose:1.0.7`

### Published Modules
- ✅ `android` - Android AAR
- ✅ `kotlinMultiplatform` - KMP metadata
- ✅ `iosArm64` - iOS ARM64
- ✅ `iosX64` - iOS Simulator
- ✅ `iosSimulatorArm64` - iOS Simulator ARM64

**Status**: 🟢 Successfully published  
**Build Time**: 2m 4s  
**Deployment ID**: d8ff0c79-53a4-4ba4-b2e4-579d1083a014

---

## ✨ Breaking Changes

⚠️ **Divider API Changes**:
- `DividerVariant` enum removed
- `Divider(variant = DividerVariant.Subtle)` → Use `Divider(color = customColor)` or custom thickness
- `SubtleDivider()` → Use `Divider(thickness = DividerThickness.Thin)`
- `StrongDivider()` → Use `Divider(thickness = DividerThickness.Heavy)`

**Migration**: Update Divider calls to use thickness instead of variants. See QUICK_START_GUIDE.md for examples.

---

## ✅ Quality Checklist

- [x] All animations use AnimationUtils
- [x] Toast and Snackbar have global managers
- [x] Thread-safe implementation with Mutex
- [x] CompositionLocal support for testing
- [x] Comprehensive documentation added
- [x] Backward compatible (local states still work)
- [x] Published to Maven Central
- [x] Git commit and tag created
- [x] Release notes documented

---

## 📚 Documentation Files

1. **QUICK_START_GUIDE.md** - Fast reference guide with 15+ examples
2. **IMPLEMENTATION_SUMMARY.md** - Technical implementation details
3. **PIXA_TOAST_ENHANCEMENTS.md** - Original enhancement proposal
4. **AI_COMPONENTS_GUIDE.md** - Updated component decision tree

---

## 🔗 Get Started

### Installation

Add to your `build.gradle.kts`:
```kotlin
dependencies {
    implementation("com.pixamob:pixacompose:1.0.7")
}
```

### First Steps

1. Read: **QUICK_START_GUIDE.md** (5 min)
2. Setup: Add `GlobalToastHost()` and `GlobalSnackbarHost()` to App root
3. Use: `PixaToastManager.showSuccess()` from anywhere
4. Reference: Check examples in QUICK_START_GUIDE.md for your use case

---

## 🐛 Known Issues

None at this time. All features tested and working.

---

## 🎯 Future Improvements

- [ ] Toast/Snackbar persistence (save/restore on config changes)
- [ ] Advanced animations (swipe-left, swipe-right patterns)
- [ ] Queue management UI (show pending notifications)
- [ ] Analytics integration helpers
- [ ] Haptic feedback support

---

## 👤 Release Information

**Released by**: Ayoub Oubarka  
**Repository**: PixaCompose  
**Tag**: v1.0.7  
**Commit**: 88e1531  
**Date**: January 21, 2026

---

## 📞 Support

For questions or issues:
1. Check QUICK_START_GUIDE.md for common patterns
2. Review IMPLEMENTATION_SUMMARY.md for technical details
3. See Toast.kt and Snackbar.kt for full API documentation

---

**Thank you for using PixaCompose! 🙌**
