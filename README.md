# PixaCompose

**A comprehensive Compose Multiplatform UI library** with 20+ production-ready components for Android and iOS applications.

[![Maven Central](https://img.shields.io/maven-central/v/com.pixamob/pixacompose)](https://central.sonatype.com/artifact/com.pixamob/pixacompose)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-purple.svg)](https://kotlinlang.org)

## ✨ Features

- 🎨 **20+ Components** - TextField, Slider, Switch, Button, DatePicker, and more
- 🌗 **Theme Support** - Built-in light and dark themes with customizable colors
- 📱 **Multiplatform** - Android and iOS support via Compose Multiplatform
- ♿ **Accessible** - Full accessibility support with semantic roles
- 🎭 **Variants** - Multiple style variants (Filled, Outlined, Minimal) for each component
- 📏 **Sizes** - Three size presets (Small, Medium, Large) for all components
- 🎬 **Animated** - Smooth animations and transitions
- 🎯 **Type-Safe** - 100% Kotlin with full type safety
- 📖 **Documented** - Comprehensive KDoc documentation

## 📦 Installation

### Kotlin Multiplatform

```kotlin
commonMain.dependencies {
    implementation("com.pixamob:pixacompose:1.0.0")
}
```

### Android Only

```kotlin
dependencies {
    implementation("com.pixamob:pixacompose:1.0.0")
}
```

## 🚀 Quick Start

```kotlin
import androidx.compose.runtime.*
import com.pixamob.pixacompose.components.inputs.*
import com.pixamob.pixacompose.components.display.*
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun MyApp() {
    AppTheme {
        Column {
            // Text Input
            var text by remember { mutableStateOf("") }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = "Email",
                placeholder = "Enter your email"
            )
            
            // Slider
            var volume by remember { mutableStateOf(50f) }
            FilledSlider(
                value = volume,
                onValueChange = { volume = it },
                label = "Volume",
                valueRange = 0f..100f,
                showValue = true
            )
            
            // Switch
            var isEnabled by remember { mutableStateOf(false) }
            FilledSwitch(
                checked = isEnabled,
                onCheckedChange = { isEnabled = it },
                label = "Enable Notifications"
            )
            
            // Button
            PrimaryButton(
                text = "Submit",
                onClick = { /* Handle click */ }
            )
        }
    }
}
```

## 📚 Components

PixaCompose includes 20+ components organized in three categories:

### Input Components
- **TextField** - Single-line text input
- **TextArea** - Multi-line text input
- **SearchBar** - Search-specific input with icon
- **Slider** - Value selection (continuous/discrete)
- **Switch** - Binary toggle (on/off)
- **Checkbox** - Multi-select options
- **RadioButton** - Single-select from group
- **DatePicker** - Date selection with calendar

### Display Components
- **Button** - Primary/secondary/ghost buttons
- **Icon** - Vector graphics with sizes
- **Avatar** - User images (circular/rounded)
- **Badge** - Notification indicators
- **Chip** - Compact selectable elements
- **Divider** - Content separation
- **Loader** - Loading states (spinner/progress)

### Navigation Components
- **BottomNavBar** - Bottom navigation
- **TopAppBar** - Top app bar with actions
- **NavigationDrawer** - Side drawer menu
- **Tabs** - Tabbed navigation

👉 **See [COMPONENTS.md](./COMPONENTS.md) for detailed documentation and parameters**

## 🎨 Theming

All components integrate with the `AppTheme` for consistent styling:

```kotlin
@Composable
fun App() {
    AppTheme(
        useDarkTheme = false,
        colors = myCustomColors, // Optional
        typography = myCustomTypography // Optional
    ) {
        // Your app content
    }
}
```

### Custom Colors

```kotlin
val myCustomColors = ColorPalette(
    brandContentDefault = Color(0xFF007AFF),
    brandSurfaceDefault = Color(0xFFE3F2FD),
    // ... customize any color
)
```

### Custom Typography

```kotlin
val myCustomTypography = provideTextTypography(
    customFontFamily = myFontFamily
)
```

## 🎯 Component Variants

Most components support three visual variants:

- **Filled** - Solid background (primary style)
- **Outlined** - Border with transparent background
- **Minimal** - Subtle, minimal design

```kotlin
// Filled variant (default)
FilledTextField(value, onValueChange, label = "Name")

// Outlined variant
OutlinedTextField(value, onValueChange, label = "Name")

// Ghost/Minimal variant
GhostTextField(value, onValueChange, label = "Name")
```

## 📏 Component Sizes

Three size presets are available for all components:

- **Small** - Compact UI (36dp height for inputs)
- **Medium** - Standard size (44dp) - Default
- **Large** - Touch-friendly (52dp)

```kotlin
FilledButton(
    text = "Click Me",
    onClick = { },
    size = ButtonSize.Large
)
```

## ♿ Accessibility

All components include full accessibility support:

```kotlin
FilledSwitch(
    checked = isEnabled,
    onCheckedChange = { isEnabled = it },
    label = "Enable Feature",
    contentDescription = "Toggle to enable or disable the feature"
)
```

## 🎬 Animations

Components include smooth, built-in animations:

- Color transitions
- Size changes
- Opacity fading
- Slide/scale effects

All animations use Material Motion principles for natural feel.

## 📱 Platform Support

| Platform | Supported |
|----------|-----------|
| Android  | ✅ API 24+ |
| iOS      | ✅ iOS 14+ |
| Desktop  | ⏳ Coming soon |
| Web      | ⏳ Coming soon |

## 🛠️ Requirements

- **Kotlin**: 2.3.0+
- **Compose Multiplatform**: 1.8.0+
- **Android**: API 24+ (Android 7.0)
- **iOS**: 14.0+

## 📖 Documentation

- **[COMPONENTS.md](./COMPONENTS.md)** - Detailed component documentation with all parameters
- **[PUBLISHING_GUIDE.md](./PUBLISHING_GUIDE.md)** - Guide for contributors to publish updates
- **[CHANGELOG.md](./CHANGELOG.md)** - Version history and changes

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guidelines](./CONTRIBUTING.md) first.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

```
Copyright 2026 Pixamob

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🔗 Links

- **Maven Central**: https://central.sonatype.com/artifact/com.pixamob/pixacompose
- **GitHub**: https://github.com/ayoubarka/PixaCompose
- **Issues**: https://github.com/ayoubarka/PixaCompose/issues
- **Releases**: https://github.com/ayoubarka/PixaCompose/releases

## 💡 Use Cases

PixaCompose is perfect for:

- ✅ **Multiplatform Apps** - Share UI code between Android and iOS
- ✅ **Design Systems** - Build consistent UIs with reusable components
- ✅ **Rapid Prototyping** - Quickly build beautiful interfaces
- ✅ **Enterprise Apps** - Production-ready components with accessibility
- ✅ **Startups** - Focus on features, not UI boilerplate

## 🎯 Examples

Check out the sample app in the repository:

```bash
git clone https://github.com/ayoubarka/PixaCompose.git
cd PixaCompose
./gradlew :app:installDebug  # Android
./gradlew :app:iosApp         # iOS
```

## 🙏 Acknowledgments

Built with:
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) - JetBrains
- [Material 3](https://m3.material.io/) - Google
- [Kotlin](https://kotlinlang.org/) - JetBrains

---

Made with ❤️ by [Pixamob](https://github.com/ayoubarka)

