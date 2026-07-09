# AGENTS.md - AI Agent Guide for PixaCompose

> **Purpose**: Equip AI coding agents with essential knowledge to be immediately productive in the PixaCompose Kotlin Multiplatform UI library.

## 🏗️ Architecture Overview

**PixaCompose** is a production-ready Compose Multiplatform UI component library (Android + iOS) featuring 30+ components organized by category. The architecture prioritizes:

- **Theme-Driven Design**: All styling flows from centralized `AppTheme` (colors, typography, dimensions)
- **Composable Primitives**: Components built from `Box`, `Row`, `Column`, not Material 3 wrappers
- **Type-Safe Variants**: Enums define `ButtonVariant`, `ButtonSize`, `ButtonShape` - not string-based
- **Single-File Components**: Each component lives in one file with clear sections (Configuration → Theme Provider → Base Component → Public API)

**Key Structure**:
```
library/src/commonMain/kotlin/com/pixamob/pixacompose/
├── theme/           # Centralized styling (Color.kt, Typography.kt, PixaTheme.kt, Dimen.kt)
├── components/      # 30+ components organized by category
│   ├── actions/     # Button, Chip, Accordion
│   ├── inputs/      # TextField, SearchBar, Slider
│   ├── display/     # Card, Avatar, Icon, Image, Chart
│   ├── feedback/    # Alert, Toast, Badge, Skeleton, Indicator
│   ├── overlay/     # Dialog, Menu, Popover
│   └── navigation/  # TopNavBar, BottomNavBar, TabBar, Drawer
└── utils/           # Helper utilities (ColorUtils, DateTimeUtils, AnimationUtils)
```

## 🎨 Theme System (Critical Foundation)

**Access theme values via `AppTheme`**:
```kotlin
AppTheme.colors.brandContentDefault    // Primary brand color
AppTheme.colors.baseContentTitle       // Primary text
AppTheme.colors.errorContentDefault    // Error states
AppTheme.typography.bodyRegular        // Body text style
AppTheme.spacing.medium                // Spacing value (Dp)
```

**Color Palette Structure**:
- **Brand Colors**: Primary identity (500 base, 50-950 scale)
- **Base Colors**: Neutral UI elements
- **Semantic Colors**: Success, Error, Warning, Info (each 50-950 scale)

**Customization Pattern**:
```kotlin
PixaTheme(
    useDarkTheme = isSystemInDarkTheme(),
    colorScales = ColorScales(brand = mapOf(500 to Color(0xFF0284C7))),
    fontFamily = FontFamilyConfig(thin = ..., regular = ..., bold = ...)
) { YourApp() }
```

## 🧩 Component Development Pattern

**Every component follows this mandatory structure** (see `CONTRIBUTING.md` and existing components):

```kotlin
// 1. CONFIGURATION - Enums and data classes for variants/sizes
enum class ComponentVariant { Solid, Outlined, Ghost }
enum class ComponentSize { Small, Medium, Large }
@Immutable data class ComponentColors(...)
@Immutable data class ComponentStyle(default: ComponentColors, disabled: ComponentColors)

// 2. THEME PROVIDER - Map variant to theme colors
@Composable
private fun getComponentTheme(variant: ComponentVariant, colors: ColorPalette): ComponentStyle

// 3. BASE COMPONENT - Internal implementation using primitives (Box, Row, Column)
@Composable
private fun BaseComponent(...)

// 4. PUBLIC API - Exported function with all parameters
@Composable
fun Component(text: String, onClick: () -> Unit, ...)

// 5. CONVENIENCE VARIANTS - Shortcuts for common combinations
@Composable
fun FilledButton(...) = Button(variant = ButtonVariant.Filled, ...)
```

**Critical Principles**:
- Use only `Box`, `Row`, `Column`, `Canvas` - never Material 3 components (`Button`, `TextField`, etc.)
- Import colors from `AppTheme.colors`, typography from `AppTheme.typography`
- Define sizes as `@Immutable` data classes (see `Button.kt:ButtonSizeConfig`)
- No hardcoded colors/dimensions - use theme values everywhere
- Mobile-first: 44dp minimum touch targets, no hover/pressed states

**Example Component Reference**: `/library/src/commonMain/kotlin/com/pixamob/pixacompose/components/actions/Button.kt` (635 lines) shows the full pattern with variants, sizes, states, animations, loading indicators.

## 🔧 Developer Workflows

### Build & Test
```bash
./gradlew build                                # Full build
./gradlew :library:testDebugUnitTest          # Android tests
./gradlew :library:iosSimulatorArm64Test      # iOS tests
./gradlew test                                 # All tests
```

### Multiplatform Requirements
- **JDK 11+** for builds
- **Android Studio Hedgehog+** for Android development
- **Xcode 14+** for iOS builds (macOS only)
- Target: **Android 24+** (minSdk), **iOS 13+** (via framework)

### Publishing
- Library publishes to **Maven Central** via `vanniktechMavenPublish` plugin
- Version controlled in `gradle/libs.versions.toml` (`appVersionName`)
- Built artifacts include Android + iOS frameworks in single JAR

## 📦 Critical Dependencies & Integrations

**Core Compose Dependencies** (`gradle/libs.versions.toml`):
- Compose Multiplatform 1.10.3
- Material3 1.10.0-alpha05
- Coil 3.4.0 (image loading, exposed as API)
- Vico 2.4.3 (charts, exposed as API)
- Kizitonwose Calendar 2.10.0 (Material 3 calendar), DateTime Wheel Picker 1.1.0 (time picker), Shimmer, ConstraintLayout (CMP libraries)

**Key Pattern**: Libraries exposed with `api(libs.*)` are accessible to library users; internal only use `implementation()`.

## 📋 Project-Specific Conventions

1. **Component Naming**: Files use PascalCase matching component name (`Button.kt` contains `Button()`)
2. **Enum Variants**: All style/size/shape options are enums, never string parameters
3. **State Management**: Only three states in components - Default, Disabled, Loading
4. **Accessibility**: All components include `semantic(role = Role.Button)` or equivalent; descriptions via `contentDescription`
5. **Documentation**: Every component requires KDoc comments + usage examples at file end (see `DOCUMENTATION.md` format)
6. **Global Feedback**: Toast and Snackbar use global managers (`PixaToastManager`, `PixaSnackbarManager`) - not local state

## 🎯 Common Tasks & Quick Answers

**Add a new component?**
1. Create file in appropriate `components/[category]/` folder
2. Follow single-file pattern with 5 sections above
3. Use theme values from `AppTheme`
4. Add examples to `DOCUMENTATION.md`

**Add a new color or typography style?**
- Modify `theme/Color.kt` (color palettes) or `theme/Typography.kt`
- Update `ColorPalette` data class in `theme/PixaTheme.kt`
- Update `AppTheme` composition local provider

**Make light/dark theme work?**
- Automatic via `PixaTheme(useDarkTheme = isSystemInDarkTheme())`
- All color values already have light/dark variants in `Color.kt`
- No component-level theme switching needed

**Use images or icons?**
- Images: `PixaImage` component or Coil `AsyncImage` directly
- Icons: `PixaIcon` component (wraps Material Icons Extended)
- Charts: `VicChart` or raw Vico library (already exposed as API)

**Handle async/network operations?**
- Library is presentation-only; users handle networking
- Expose `loading` boolean parameter on components
- Use `Skeleton` component for placeholder states while loading

## 📖 References & Agent Guides

- **DOCUMENTATION.md**: Complete library reference — install, theme, all components, patterns, contributing
- **CHANGELOG.md**: Version history and release notes
- **ui-expert.agent.md**: Existing agent guide (theme architecture, MVI patterns)

---

**Last Updated**: March 2026 | **Version**: PixaCompose 1.1.1

