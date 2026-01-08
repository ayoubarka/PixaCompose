# PixaCompose 🎨

[![Maven Central](https://img.shields.io/maven-central/v/com.pixamob.pixacompose/pixacompose.svg)](https://central.sonatype.com/artifact/com.pixamob.pixacompose/pixacompose)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-2.2.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.7.6-blue)](https://www.jetbrains.com/lp/compose-multiplatform/)

A comprehensive, production-ready UI component library for Compose Multiplatform applications (Android & iOS). Built with Material 3 design principles, featuring 40+ customizable components, full theming support, and accessibility-first approach.

## ✨ Features

- 🎯 **Mobile-First Design**: Touch-friendly sizing (44dp minimum), safe area support, optimized for iOS and Android
- 🎨 **Complete Theme System**: Light/dark modes, custom colors, typography, and spacing with `AppTheme`
- 🧩 **40+ Components**: Buttons, inputs, navigation, overlays, feedback, and display components
- 🔧 **Highly Customizable**: Size variants, styles, animations, and full parameter control
- 📦 **Single-File Components**: Easy to read, maintain, and extend
- 🚀 **Performance Optimized**: Built from Compose primitives, no unnecessary wrappers
- ♿ **Accessibility Ready**: WCAG compliant with semantic roles and screen reader support
- 🎭 **Smooth Animations**: Native-feeling transitions using `AnimationUtils`

## 📦 Installation

Add the dependency to your `commonMain` source set:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.pixamob.pixacompose:pixacompose:1.0.0")
        }
    }
}
```

## 🚀 Quick Start

Wrap your app with `AppTheme` and start using components:

```kotlin
@Composable
fun App() {
    AppTheme(
        useDarkTheme = isSystemInDarkTheme()
    ) {
        Scaffold(
            topBar = {
                BaseTopNavBar(
                    title = "My App",
                    startActions = listOf(
                        TopNavAction(menuIcon, "Menu", { openDrawer() })
                    )
                )
            },
            bottomBar = {
                BottomNavBar(
                    items = navItems,
                    selectedIndex = currentTab,
                    onItemSelected = { index -> onTabChange(index) }
                )
            }
        ) { paddingValues ->
            Content(modifier = Modifier.padding(paddingValues))
        }
    }
}
```

## 🏗️ Component Categories

### 🎬 Actions
User interaction components:
- **BaseButton** (Solid, Outlined, Ghost, Text variants) - Primary interactions
- **IconButton** - Compact icon-only buttons  
- **FloatingActionButton** - Prominent floating actions
- **Chip** - Tags, filters, selections with styles and sizes
- **Tab** - Navigation tabs with icons and labels

### 📝 Inputs
Form and data entry:
- **TextField** - Single-line text input with validation
- **TextArea** - Multi-line text input
- **SearchBar** - Search with suggestions and icons
- **Checkbox** - Boolean selection with labels
- **RadioButton** - Single choice from options
- **Switch** - Toggle on/off states
- **Slider** - Range selection with labels

### 🧭 Navigation  
Screen navigation components:
- **BaseTopNavBar** - Top app bar with actions, title, subtitle, badges, avatar
- **BottomNavBar** - Bottom navigation with 2-5 items, optional center FAB
- **Drawer** - Side navigation drawer
- **TabBar** - Horizontal tabs for content sections
- **Stepper** - Multi-step progress indicator

### 🔔 Feedback
User feedback and status:
- **Badge** - Notification counts, status dots (Dot, Small, Medium, Large sizes)
- **Toast** - Temporary notifications
- **ProgressIndicator** - Linear and circular progress
- **Skeleton** - Loading placeholders
- **EmptyState** - No content states

### 🎭 Overlays
Modal and overlay components:
- **BaseBottomSheet** - Bottom sheets with 4 size variants and 3 styles
- **Dialog** - Modal dialogs
- **AlertDialog** - Confirmation dialogs
- **SelectOptionBottomSheet** - Trigger + content pattern
- **ExpandableBottomSheet** - Collapsed/expanded states
- **ListBottomSheet** - Scrollable item lists
- **ConfirmationBottomSheet** - Confirm/cancel actions

### 📦 Display
Content presentation:
- **BaseCard** - Cards with 4 variants (Elevated, Outlined, Filled, Ghost)
- **Avatar** - Profile images (6 sizes: Tiny to Massive)
- **Icon** - SVG/vector icons with tints
- **Divider** - Horizontal/vertical separators
- **Image** - Images with loading states

## 🎨 Theming

### Custom Colors

```kotlin
AppTheme(
    lightColorPalette = ColorPalette(
        brandContentDefault = Color(0xFF007AFF),
        baseSurfaceDefault = Color.White,
        // ... more colors
    ),
    darkColorPalette = ColorPalette(
        brandContentDefault = Color(0xFF0A84FF),
        baseSurfaceDefault = Color(0xFF1C1C1E),
        // ... more colors
    )
) {
    // Your app
}
```

### Custom Typography

```kotlin
AppTheme(
    fontFamily = myCustomFontFamily
) {
    // Components automatically use custom fonts
}
```

### Access Theme in Components

```kotlin
@Composable
fun MyComponent() {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val shapes = AppTheme.shapes
    
    Text(
        text = "Hello",
        style = typography.titleBold,
        color = colors.brandContentDefault
    )
}
```

## 📖 Documentation

For detailed documentation, see [docs.md](docs.md) which includes:
- Complete setup guide
- Theme customization
- All components with parameters and examples
- Best practices and patterns

## 🔧 Requirements

- **Kotlin**: 2.0.20 or higher
- **Compose Multiplatform**: 1.7.0 or higher  
- **Android**: minSdk 24 (Android 7.0)
- **iOS**: iOS 14.0+

## 📄 License

```
Copyright 2024 PixaMob

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

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## 🔗 Links

- **Documentation**: [docs.md](docs.md)
- **Issue Tracker**: [GitHub Issues](https://github.com/pixamob/pixacompose/issues)
- **Changelog**: [CHANGELOG.md](CHANGELOG.md)

---

Made with ❤️ by PixaMob
- **Checkbox** - Multi-selection control
- **RadioButton** - Single-selection control
- **Switch** - Binary toggle control
- **Slider** - Value/range selection
- **DatePicker** - Date selection
- **TimePicker** - Time selection

### 💬 Feedback
User notification and status components:
- **Toast** - Temporary notification messages
- **Snackbar** - Dismissible messages with actions
- **Alert** - Inline important messages
- **Badge** - Status indicators and notification counts
- **ProgressIndicator** - Loading and progress states
- **Skeleton** - Content placeholders with shimmer effect

### 🖼️ Display
Content presentation components:
- **Card** - Content containers
- **Avatar** - Profile pictures and initials
- **Image** - Async image loading
- **Divider** - Content separators
- **Tag** - Labels and categories
- **EmptyState** - Empty content states

### 🧭 Navigation
App navigation components:
- **BottomNavigationBar** - Bottom tab navigation
- **TopAppBar** - Top navigation bar
- **NavigationDrawer** - Side navigation drawer
- **Breadcrumb** - Hierarchical navigation
- **Stepper** - Multi-step process navigation

### 📱 Overlay
Modal and overlay components:
- **Dialog** - Modal dialogs
- **BottomSheet** - Bottom slide-up sheets
- **Dropdown** - Dropdown menus
- **Tooltip** - Contextual hints
- **PopupMenu** - Context menus

### 📐 Layout
Structural layout components:
- **Container** - Content wrapper with constraints
- **Grid** - Grid layout system
- **List** - Vertical/horizontal lists
- **ScrollView** - Scrollable containers
- **Spacer** - Flexible spacing

## 🎨 Theming

PixaCompose includes a comprehensive theme system with:

- **Color System**: 7 semantic palettes (Brand, Accent, Base, Info, Success, Warning, Error) with 11 weight scales each
- **Typography**: 20+ text styles from display to caption
- **Dimensions**: Consistent spacing, sizing, and radius tokens
- **Shapes**: Rounded and cut corner shape system

### Basic Usage

```kotlin
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun App() {
    AppTheme(useDarkTheme = isSystemInDarkTheme()) {
        // Your app content
        MyScreen()
    }
}
```

### Accessing Theme Values

```kotlin
// Colors
val brandColor = AppTheme.colors.brandContentDefault
val errorColor = AppTheme.colors.errorContentDefault

// Typography
val titleStyle = AppTheme.typography.titleMedium
val bodyStyle = AppTheme.typography.bodyRegular

// Spacing
val padding = AppTheme.spacing.medium // 16.dp
val gap = AppTheme.spacing.small // 12.dp

// Component Sizes
val buttonHeight = AppTheme.componentSize.medium // 44.dp
```

## 🚀 Quick Start

### Button Example

```kotlin
import com.pixamob.pixacompose.elements.actions.*

@Composable
fun MyScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        // Primary button
        FlatButton(
            text = "Continue",
            onClick = { /* Handle click */ }
        )
        
        // Secondary button
        OutlinedButton(
            text = "Cancel",
            onClick = { /* Handle click */ }
        )
        
        // Button with icon
        FlatButton(
            text = "Save",
            leadingIcon = painterResource(Res.drawable.ic_save),
            onClick = { /* Handle click */ }
        )
        
        // Loading button
        FlatButton(
            text = "Processing...",
            loading = true,
            onClick = { }
        )
    }
}
```

### TextField Example

```kotlin
import com.pixamob.pixacompose.elements.inputs.*

@Composable
fun LoginForm() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "your@email.com",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )
    }
}
```

### Toast Example

```kotlin
import com.pixamob.pixacompose.elements.feedback.*

@Composable
fun MyScreen() {
    val toastHost = rememberToastHostState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Your content
        Button(
            text = "Show Toast",
            onClick = {
                toastHost.showToast(
                    ToastData(
                        message = "Item saved successfully!",
                        variant = ToastVariant.Success
                    )
                )
            }
        )
        
        // Toast host
        ToastHost(
            hostState = toastHost,
            position = ToastPosition.Bottom
        )
    }
}
```

## 🎯 Design Principles

1. **Build from Primitives**: All components are built from basic Compose primitives (Box, Row, Column, Canvas) - NOT Material 3 wrappers
2. **Single File per Component**: Each component lives in one file with clear sections
3. **Theme-Aware**: All components consume the centralized theme system
4. **Practical Variants**: Cover 80% of real-world use cases, not exhaustive options
5. **Mobile-First**: Minimum 44dp touch targets, optimized for mobile devices
6. **Consistent Structure**: All components follow the same file structure pattern

## 📖 Documentation

For detailed implementation guides and component documentation, see:
- [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md) - Complete phase-by-phase implementation guide
- [Component Storybook](https://pixamob.github.io/pixacompose/) - Interactive component showcase (coming soon)

## 🏗️ Project Structure

```
library/src/commonMain/kotlin/com/pixamob/pixacompose/
├── theme/
│   ├── AppTheme.kt          # Main theme provider
│   ├── Color.kt             # Color system
│   ├── Typography.kt        # Typography scales
│   ├── Dimen.kt            # Spacing & sizing tokens
│   └── ShapeStyle.kt       # Shape system
└── elements/
    ├── actions/            # Button, FAB, Chip, Tab, etc.
    ├── inputs/             # TextField, Checkbox, Switch, etc.
    ├── feedback/           # Toast, Alert, Progress, etc.
    ├── display/            # Card, Avatar, Image, etc.
    ├── navigation/         # BottomNav, TopBar, Drawer, etc.
    ├── overlay/            # Dialog, BottomSheet, Tooltip, etc.
    └── layout/             # Container, Grid, List, etc.
```

## 🧪 Testing

Each component includes comprehensive tests for:
- Component rendering
- User interactions
- State management
- Theme integration
- Accessibility

Run tests with:
```bash
./gradlew :library:testDebugUnitTest  # Android
./gradlew :library:iosSimulatorArm64Test  # iOS
```

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) first.

## 📄 License

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

## 🙏 Acknowledgments

- Built with [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- Inspired by [Material 3 Design](https://m3.material.io/)
- Template from [Kotlin Multiplatform Library Template](https://github.com/Kotlin/multiplatform-library-template)

## 📞 Support

- 🐛 [Report a bug](https://github.com/pixamob/pixacompose/issues)
- 💡 [Request a feature](https://github.com/pixamob/pixacompose/issues)
- 💬 [Join discussions](https://github.com/pixamob/pixacompose/discussions)

---

Made with ❤️ by the Pixamob Team

