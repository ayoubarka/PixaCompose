# PixaCompose v1.0.6 Release Notes

**Release Date:** January 19, 2026

## 🎨 Major Theme Refactoring

This release focuses on a comprehensive refactoring of all component dimensions to use centralized theme properties instead of hardcoded values. This improves maintainability, consistency, and makes the library more flexible.

## ✨ What's New

### Theme System Integration
All components now exclusively use theme-based dimension properties from:
- `Spacing` - Consistent spacing values (2dp - 80dp)
- `ComponentSize` - Standard component heights and dimensions
- `IconSize` - Standardized icon dimensions
- `BorderSize` - Border width standards
- `RadiusSize` - Corner radius values
- `ShadowSize` - Shadow/elevation values
- `DividerSize` - Divider thickness
- `TouchTarget` - Accessibility-compliant touch targets
- `AvatarSize` - Avatar-specific sizing

### Components Updated

#### Input Components
- **ColorPicker** - All dimensions now use theme properties (spacing, sizes, borders, radii, icons, shadows)
- **DatePicker** - Heights, padding, borders, button sizes
- **Switch** - Track and thumb sizes, elevations, borders, padding
- **RadioButton** - Circle sizes, inner/outer dimensions
- **Checkbox** - Box sizes, checkmark stroke widths
- **Slider** - Track heights, thumb sizes, elevations
- **SearchBar** - Divider heights

#### Action Components
- **Chip** - Heights, borders, touch targets, text width constraints
- **Tab** - Minimum widths, indicator heights, border widths

#### Display Components
- **Avatar** - Complete size system using AvatarSize enum with proper icon and status indicator sizing

## 🔧 Technical Improvements

### Before (Hardcoded)
```kotlin
height = 44.dp
border = 2.dp
padding = 8.dp
```

### After (Theme-based)
```kotlin
height = ComponentSize.Medium
border = BorderSize.Standard
padding = Spacing.ExtraSmall
```

## 📊 Benefits

1. **Consistency** - All components use the same spacing and sizing system
2. **Maintainability** - Design changes only need to be made in one place (theme files)
3. **Flexibility** - Easy to create design system variants by adjusting theme values
4. **Accessibility** - Proper touch targets (44dp minimum) enforced through `TouchTarget.Minimum`
5. **Scalability** - New components can easily adopt the same system
6. **Type Safety** - Theme properties are strongly typed and compile-time checked

## 🐛 Bug Fixes
- Fixed inconsistent spacing across components
- Resolved touch target accessibility issues in Chip component
- Corrected border width inconsistencies

## 📦 Installation

### Gradle (Kotlin DSL)
```kotlin
commonMain.dependencies {
    implementation("com.pixamob:pixacompose:1.0.6")
}
```

### Gradle (Groovy)
```groovy
commonMain {
    dependencies {
        implementation 'com.pixamob:pixacompose:1.0.6'
    }
}
```

## 🔄 Migration Guide

No breaking changes! All APIs remain the same. Components will automatically use the new theme-based dimensions.

However, if you were relying on specific pixel-perfect dimensions, note that some values may have slight adjustments to align with the theme system (all within 2dp tolerance).

## 📚 Documentation

- Full component documentation: [COMPONENTS.md](./COMPONENTS.md)
- Refactoring details: [REFACTORING_SUMMARY.md](./REFACTORING_SUMMARY.md)
- Theme system: `library/src/commonMain/kotlin/com/pixamob/pixacompose/theme/`

## 🎯 What's Next (v1.0.7)

- Additional card component variants
- Navigation components
- Advanced layout utilities
- Performance optimizations

## 👥 Contributors

- [@ayouboubarka](https://github.com/ayouboubarka)

## 📝 Full Changelog

### Refactoring
- Replaced all hardcoded `dp` values with theme properties across entire codebase
- Added missing theme imports to all affected components
- Updated version to 1.0.6

### Components Modified
- ColorPicker.kt - 20+ hardcoded values replaced
- DatePicker.kt - 7 hardcoded values replaced
- Switch.kt - 18 hardcoded values replaced
- RadioButton.kt - 6 hardcoded values replaced
- Checkbox.kt - 9 hardcoded values replaced
- Slider.kt - 9 hardcoded values replaced
- SearchBar.kt - 1 hardcoded value replaced
- Chip.kt - 6 hardcoded values replaced
- Tab.kt - 6 hardcoded values replaced
- Avatar.kt - 21 hardcoded values replaced

### Theme Properties Used
- 103+ instances of hardcoded dimensions replaced with theme properties
- Added `BorderSize`, `ComponentSize`, `TouchTarget`, `DividerSize` imports where needed

## 🔗 Links

- **GitHub**: https://github.com/ayouboubarka/PixaCompose
- **Maven Central**: https://central.sonatype.com/artifact/com.pixamob/pixacompose
- **Documentation**: [README.md](./README.md)
- **Issues**: https://github.com/ayouboubarka/PixaCompose/issues

---

**Full diff**: [v1.0.5...v1.0.6](https://github.com/ayouboubarka/PixaCompose/compare/v1.0.5...v1.0.6)

