# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **SchedulePicker Variant for DatePicker**:
  - New `DatePickerVariant.SchedulePicker` for recurring schedule selection
  - `ScheduleFrequency` enum: `Daily`, `Weekly`, `Monthly` modes
  - `ScheduleSelection` data class for tracking frequency and selected days
  - `ScheduleConfig` with full customization options:
    - `showFrequencyTabs` - toggle Daily/Weekly/Monthly tab visibility
    - `allowMultipleWeekdays` - enable multi-select for weekdays
    - `allowMultipleMonthDays` - enable multi-select for month days
    - `weekdayChipStyle` - Horizontal, Vertical, or Grid layouts
    - `weekdayItemShape` - custom shape for weekday items
    - `monthDayItemShape` - custom shape for month day items
    - `tabShape` - custom shape for frequency tabs
    - `tabContainerShape` - custom shape for tab container
  - Smooth animations on selection with configurable shapes
  - Full accessibility support with semantic content descriptions

- **Optional/Nullable Labels**:
  - `DatePickerStrings` labels are now nullable for cleaner customization
  - `TimePickerStrings` labels are now nullable
  - Added `headerLabel` property for custom header text

### Changed
- **Demo App Restructured**:
  - `InputsDemoScreen` reorganized with keyed lazy list items for better performance
  - Added SchedulePicker demo with default and custom shape examples
  - Each component demo extracted into separate composable functions

### Planning
- Dialog component
- BottomSheet component
- Navigation components
- Menu components

## [1.0.9] - 2026-01-24

### Added
- **Unified Hierarchical Sizing System**:
  - `HierarchicalSize` object with consistent size naming across all components
  - Standard size variants: `ExtraSmall`, `Small`, `Medium`, `Large`, `ExtraLarge`
  - Additional granular sizes: `None`, `Tiny`, `Hairline`, `Huge`, `VeryLarge`, `Full`, `Micro`, `Massive`
  - Component-specific size objects: `Container`, `Button`, `Icon`, `Input`, `Chip`, `ListItem`, `Card`, `Avatar`, `Badge`, `Toggle`, `Checkbox`, `AppBar`, `Tab`, `BottomNav`
  - Spacing and layout sizes: `Spacing`, `Padding`, `Radius`, `Border`, `Shadow`, `TouchTarget`, `Divider`, `Stroke`, `SliderTrack`
  - `forVariant()` helper functions for programmatic size selection
  - `getSizesFor(variant)` helper function to get all component sizes for a variant
  - `ComponentSizes` data class with unified size access

- **Comprehensive Shape Customization System**:
  - `ConcaveShape` - inward-dipping curves (Top, Bottom, Left, Right positions)
  - `ConvexShape` - outward-bulging curves with configurable depth
  - `WaveShape` - smooth wave patterns with adjustable wavelength and amplitude
  - `ArchShape` - dome/arch curves with curvature control
  - `TabShape` - browser tab-like shapes with customizable width
  - `NotchShape` - notch cutouts with rounded or sharp styles
  - `BubbleShape` - chat bubble shapes with tail (RTL-aware)
  - `ShapePosition` enum for directional configuration
  - `NotchStyle` enum for notch appearance
  - `CustomShapes` data class for size variant collections
  - Updated `ShapeStyles` with all new shape families
  - New shape documentation in `SHAPE_CUSTOMIZATION_GUIDE.md`

### Changed
- **Button Component**:
  - Fixed `customColors` to respect `isDestructive` flag
  - `isDestructive` now takes precedence even when custom colors are provided
  - Destructive styling always applied for destructive actions

- **Size System Refactoring**:
  - All legacy size objects (`BorderSize`, `ComponentSize`, `RadiusSize`, etc.) maintained for compatibility
  - Legacy aliases map to new `HierarchicalSize` system
  - Consistent naming: all size categories now use `ExtraSmall`, `Small`, `Medium`, `Large`, `ExtraLarge`
  - Removed inconsistent size names in favor of unified system

### Improved
- **Type Safety**: `SizeVariant` enum ensures compile-time safety for size selection
- **Consistency**: All components now use unified sizing terminology
- **Accessibility**: `TouchTarget` sizes comply with WCAG guidelines (48dp minimum)
- **Documentation**: Comprehensive guides for sizing and shape customization
- **RTL Support**: `BubbleShape` automatically adapts to RTL layouts
- **Performance**: Shape path creation optimized for common use cases

### Migration Guide
```kotlin
// Old approach (deprecated)
val size = ComponentSize.Medium
val radius = RadiusSize.Large
val border = BorderSize.Default

// New unified approach
val size = HierarchicalSize.Container.Medium
val radius = HierarchicalSize.Radius.Large
val border = HierarchicalSize.Border.Small

// Using variant helpers
val sizes = getSizesFor(SizeVariant.Large)
val buttonSize = HierarchicalSize.Button.forVariant(SizeVariant.Medium)
```

### Documentation
- Added `SHAPE_CUSTOMIZATION_GUIDE.md` with comprehensive shape usage examples
- Updated `AI_COMPONENTS_GUIDE.md` with unified sizing examples
- Added KDoc comments to all size objects with usage examples
- Migration guides for legacy size objects

## [1.0.7] - 2026-01-21

### Added
- **Global Toast & Snackbar Manager System**:
  - `PixaToastManager` singleton for global toast access from anywhere
  - `PixaSnackbarManager` singleton for global snackbar access from anywhere
  - `GlobalToastHost()` and `GlobalSnackbarHost()` root-level composables
  - `LocalToastManager` and `LocalSnackbarManager` composition locals for testing
  - Thread-safe implementation with Mutex synchronization
  - Full support for ViewModel, UseCase, Repository, and Composable contexts

- **AnimationUtils Integration in Toast & Snackbar**:
  - Replaced inline animation specs with centralized `AnimationUtils` calls
  - `standardSpring()` for smooth bouncy enter animations
  - `fastSpring()` for quick exit animations
  - `standardTween()` for fade in (300ms)
  - `fastTween()` for fade out (200ms)
  - Consistent animations across all feedback components

- **Documentation & Guides**:
  - New `QUICK_START_GUIDE.md` with 15+ real-world examples
  - New `IMPLEMENTATION_SUMMARY.md` with technical details
  - Enhanced `AI_COMPONENTS_GUIDE.md` with global manager patterns
  - Comprehensive KDoc in Toast.kt and Snackbar.kt

### Changed
- **Toast System**:
  - Refactored to use global manager pattern
  - Maintained backward compatibility with local `PixaToastHostState`
  - Added extension functions: `rememberToastScope()`, `launchToast()`
  - Added convenience methods: `showSuccess()`, `showError()`, `showWarning()`, `showInfo()`

- **Snackbar System**:
  - Refactored to use global manager pattern
  - Maintained backward compatibility with local `PixaSnackbarHostState`
  - Added extension functions: `rememberSnackbarScope()`, `launchSnackbar()`
  - Added convenience methods: `showSuccess()`, `showError()`, `showWarning()`, `showInfo()`

- **Divider Component**:
  - Simplified by removing `DividerVariant` enum (Subtle, Default, Strong)
  - Now uses single unified divider with configurable thickness
  - Maintains `HorizontalDivider()` and `VerticalDivider()` convenience functions
  - Uses default theme color with optional custom color support

### Removed
- ❌ `DividerVariant` enum and related theme function
- ❌ `SubtleDivider()` and `StrongDivider()` convenience functions
- ❌ Variant parameter from `PixaDivider()`, `HorizontalDivider()`, `VerticalDivider()`
- ❌ Unused `spring()` and `tween()` imports from Snackbar.kt

### Fixed
- Consistent animation behavior across Toast and Snackbar
- Unused import warnings in Snackbar.kt

### Migration Guide

**Toast Migration (if using old local state)**:
```kotlin
// Old way (still works but not recommended)
val toastState = rememberToastHostState()
ToastHost(hostState = toastState)
toastState.showToast(...)

// New way (recommended)
GlobalToastHost()  // Once at app root
PixaToastManager.showToast(...)  // From anywhere
```

**Snackbar Migration (if using old local state)**:
```kotlin
// Old way (still works but not recommended)
val snackbarState = rememberSnackbarHostState()
SnackbarHost(hostState = snackbarState)
snackbarState.showSnackbar(...)

// New way (recommended)
GlobalSnackbarHost()  // Once at app root
PixaSnackbarManager.showSnackbar(...)  // From anywhere
```

**Divider Migration**:
```kotlin
// Old way (no longer available)
Divider(variant = DividerVariant.Subtle)
SubtleDivider()
StrongDivider(thickness = DividerThickness.Heavy)

// New way
Divider(thickness = DividerThickness.Thin)
Divider(thickness = DividerThickness.Heavy)
Divider(color = customColor)
```

---

## [1.0.7] - 2026-01-19

### Added
- **Optimized PixaTheme Color System**:
  - New `ColorScales` data class for simplified color customization
  - `DefaultColorScales` constant for easy partial customization
  - Automatic light/dark palette derivation from color scales
  - Support for partial color customization (modify only specific color groups)
  - Smart color merging: user colors override defaults, missing values fall back to defaults
  
- **Enhanced Typography System**:
  - New `FontFamilyConfig` data class requiring all 9 font weights (W100-W900)
  - Complete font weight validation and clear documentation
  - Full Moko Resources support for multiplatform font loading
  - Improved `provideTextTypography()` function accepting `FontFamilyConfig`

### Improved
- **PixaTheme API**: Simplified from 7 parameters to 3 (useDarkTheme, colorScales, fontFamily)
- **Color Customization**: Users can now copy `DefaultColorScales` and modify only specific weights or color groups
- **Type Safety**: FontFamilyConfig ensures all required font weights are provided
- **Documentation**: Comprehensive KDoc with multiple usage examples for both color and typography customization

### Changed
- **Breaking**: Removed legacy `lightColorPalette`, `darkColorPalette`, `brandColorOverride`, `baseColorOverride`, `accentColorOverride` parameters
- **Breaking**: `fontFamily` parameter now accepts `FontFamilyConfig?` instead of `FontFamily?`
- Refactored `buildColorPaletteWithOverrides` → `buildColorPaletteFromScales` with support for all 7 color groups

### Migration Guide
**Old API:**
```kotlin
PixaTheme(
    brandColorOverride = mapOf(500 to Color(0xFFFF6B35)),
    fontFamily = myFontFamily
) { }
```

**New API:**
```kotlin
PixaTheme(
    colorScales = ColorScales(
        brand = DefaultColorScales.brand!! + mapOf(500 to Color(0xFFFF6B35))
    ),
    fontFamily = FontFamilyConfig(
        thin = Font(...),
        // ... all 9 weights required
    )
) { }
```

## [1.0.5] - 2026-01-18

### Added
- **9 Purpose-Built Card Components**:
  - `InfoCard` - Static information display with icon and description
  - `ActionCard` - Clickable cards for navigation and actions
  - `SelectCard` ⭐ - Flexible selection cards (single/multi-select, supports remote icon URLs)
  - `MediaCard` - Cards with prominent media content (image + text)
  - `StatCard` - Display metrics and statistics with trend indicators
  - `ListItemCard` - List entries for settings and navigation menus
  - `FeatureCard` - Showcase features with centered icon and description
  - `CompactCard` - Small cards for tags and chips
  - `SummaryCard` - Display grouped summary data with label-value pairs

### Improved
- **PixaCard**: Base card component now supports all parameters properly
- **Theme Integration**: All card components fully integrated with AppTheme
- **Accessibility**: Proper semantic roles (Role.Button, Role.Checkbox) and content descriptions
- **SelectCard Flexibility**:
  - Supports both ImageVector icons and remote URL icons
  - Auto-styles based on selection state
  - Three modes: Full content, Icon+Title, Icon-only
  - Perfect for settings and profile configuration screens

### Fixed
- Color theme references (baseContentSubtle → baseContentCaption)
- Typography references (displayBold → displayLarge)
- Removed redundant qualifiers from PixaBadge calls
- All compilation errors resolved

### Documentation
- Updated README with all 9 card component examples
- Comprehensive card styling guide (variants, padding, colors)
- Quick reference for all card types with usage examples
- Removed outdated implementation documentation files

## [1.0.2] - 2026-01-15

### Fixed
- Fixed unnecessary cast compiler warning in ColorPicker.kt

### Changed
- Increased JVM heap space from 4GB to 12GB for improved build stability
- Added Kotlin Native compiler optimizations for iOS framework compilation
- Removed deprecated `kotlin.native.ignoreIncorrectDependencies` property
- Improved memory management with MaxMetaspaceSize configuration

### Performance
- Enhanced build performance for multiplatform targets
- Optimized iOS framework linking process

## [1.0.0] - 2025-01-09

### Added
- 🎉 Initial release of PixaCompose library
- **Input Components**:
  - TextField with variants (Filled, Outlined, Ghost)
  - TextArea for multi-line input
  - SearchBar with suggestions and filtering
  - Specialized input functions (EmailTextField, PasswordTextField, CommentTextArea, etc.)
- **Button Components**:
  - Primary, Secondary, Tertiary button variants
  - Icon buttons
  - Size variants (Small, Medium, Large)
- **Card Components**:
  - Elevated, Outlined, and Filled variants
  - Configurable padding and elevation
- **Theme System**:
  - Complete color palette (Brand, Base, Accent, Utility colors)
  - Typography system with 40+ text styles
  - Comprehensive dimension system (ComponentSize, Spacing, IconSize, etc.)
  - Border, Corner Radius, and Elevation utilities
- **Utilities**:
  - Animation utilities
  - Shape styles (Rounded and Cut corners)
  - Accessibility support with semantics
- **Documentation**:
  - Comprehensive README with installation and usage
  - Quick Reference Guide
  - Implementation Details Guide
  - Publishing Guide
  - Contributing Guidelines
- **Platform Support**:
  - Android (minSdk 21)
  - iOS (iosX64, iosArm64, iosSimulatorArm64)
- **Publishing**:
  - Maven Central publication via Sonatype Central Portal
  - Automated CI/CD with GitHub Actions
  - GPG signing for all artifacts
  - Sources and Javadoc JARs generation

### Changed
- N/A (Initial release)

### Deprecated
- N/A (Initial release)

### Removed
- N/A (Initial release)

### Fixed
- N/A (Initial release)

### Security
- GPG signing for all published artifacts
- Secure credential management via GitHub Secrets

---

## Release Notes Format

Each release will include:
- **Added**: New features
- **Changed**: Changes in existing functionality
- **Deprecated**: Soon-to-be removed features
- **Removed**: Removed features
- **Fixed**: Bug fixes
- **Security**: Security improvements

---

## Versioning

- **MAJOR.MINOR.PATCH** (e.g., 1.0.0)
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

Pre-release versions:
- **alpha**: Early preview (e.g., 1.0.0-alpha01)
- **beta**: Feature complete, testing (e.g., 1.0.0-beta01)
- **rc**: Release candidate (e.g., 1.0.0-rc01)

---

[Unreleased]: https://github.com/ayoubarka/PixaCompose/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/ayoubarka/PixaCompose/releases/tag/v1.0.0

