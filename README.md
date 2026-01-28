# PixaCompose

**A comprehensive Compose Multiplatform UI library** with 30+ production-ready components for Android and iOS applications.

[![Maven Central](https://img.shields.io/maven-central/v/com.pixamob/pixacompose)](https://central.sonatype.com/artifact/com.pixamob/pixacompose)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-purple.svg)](https://kotlinlang.org)

## ✨ Features

- 🎨 **30+ Components** - Cards, TextField, Slider, Switch, Button, DatePicker, Icons, Images, and more
- 🌗 **Theme Support** - Built-in light and dark themes with customizable colors
- 📱 **Multiplatform** - Android and iOS support via Compose Multiplatform
- ♿ **Accessible** - Full accessibility support with semantic roles and descriptions
- 🎭 **Variants** - Multiple style variants for each component
- 📏 **Flexible** - Customizable sizes, colors, and styling
- 🎬 **Animated** - Smooth animations and transitions
- 🎯 **Type-Safe** - 100% Kotlin with full type safety
- 📖 **Documented** - Comprehensive KDoc documentation
- 🔄 **Loading States** - Built-in skeleton loading for all components

## 📦 Installation

### Kotlin Multiplatform

```kotlin
commonMain.dependencies {
    implementation("com.pixamob:pixacompose:1.1.0")
}
```

### Android Only

```kotlin
dependencies {
    implementation("com.pixamob:pixacompose:1.1.0")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
        ) {
            // Card Components
            InfoCard(
                title = "Welcome to PixaCompose",
                description = "Build beautiful UIs with ready-to-use components",
                icon = Icons.Default.Info
            )
            
            // Text Input
            var text by remember { mutableStateOf("") }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = "Email",
                placeholder = "Enter your email"
            )
            
            // Action Button
            PrimaryButton(
                text = "Get Started",
                onClick = { /* Handle click */ }
            )
        }
    }
}
```

## 📚 Components

PixaCompose includes 30+ components organized in categories:

### Display Components

#### Cards (9 Types)
- **PixaCard** - Base card component with variants (Elevated, Outlined, Filled, Ghost)
- **InfoCard** - Display static information with icon and description
- **ActionCard** - Clickable cards for navigation and actions
- **SelectCard** ⭐ - Flexible selection cards (single/multi-select, supports remote icons)
- **MediaCard** - Cards with prominent media content
- **StatCard** - Display metrics and statistics with trends
- **ListItemCard** - List entries for settings and navigation
- **FeatureCard** - Showcase features with centered icon
- **CompactCard** - Small cards for tags and chips
- **SummaryCard** - Display grouped summary data

#### Other Display Components
- **PixaIcon** - Unified icon component (Vector, Painter, URL sources)
- **PixaImage** - Advanced image loading with Coil3 (crossfade, placeholder, error handling)
- **PixaBadge** - Notification badges and labels
- **Skeleton** - Loading placeholder with shimmer effect

### Input Components
- **TextField** - Single-line text input (Filled, Outlined, Minimal variants)
- **TextArea** - Multi-line text input
- **SearchBar** - Search-specific input with icon
- **Slider** - Value selection (continuous/discrete, with label and value display)
- **Switch** - Binary toggle (Filled, Outlined, Minimal variants)
- **Checkbox** - Multi-select options (with indeterminate state)
- **RadioButton** - Single-select from group
- **DatePicker** - Date selection with calendar popup

### Feedback Components
- **Button** - Primary, Secondary, Tertiary, Minimal variants
- **PixaBadge** - Notification count and status indicators

## 🎨 Card Components - Quick Reference

### InfoCard - Static Information
```kotlin
InfoCard(
    title = "Welcome",
    description = "Get started with PixaCompose",
    icon = Icons.Default.Info,
    variant = BaseCardVariant.Elevated
)
```

### ActionCard - Clickable Actions
```kotlin
ActionCard(
    title = "Settings",
    description = "Manage your preferences",
    icon = Icons.Default.Settings,
    trailingIcon = Icons.Default.ChevronRight,
    onClick = { /* navigate */ }
)
```

### SelectCard ⭐ - Flexible Selection
Perfect for settings, choices, and options with full flexibility.

```kotlin
// With remote icon (perfect for profile settings)
SelectCard(
    title = "7-8 hours",
    description = "Recommended sleep",
    iconUrl = "https://example.com/sleep-icon.png",
    isSelected = selected == 0,
    onClick = { selected = 0 }
)

// With vector icon
SelectCard(
    title = "Dark Mode",
    icon = Icons.Default.DarkMode,
    isSelected = selected == 1,
    onClick = { selected = 1 }
)

// Icon only mode
SelectCard(
    icon = Icons.Default.Favorite,
    isSelected = isLiked,
    onClick = { isLiked = !isLiked }
)
```

### MediaCard - Image Content
```kotlin
MediaCard(
    imageUrl = "https://example.com/image.jpg",
    title = "Article Title",
    subtitle = "Category",
    description = "Article preview text",
    onClick = { /* open article */ }
)
```

### StatCard - Metrics Display
```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
    StatCard(
        modifier = Modifier.weight(1f),
        value = "42",
        label = "Active",
        trend = "+12%",
        trendPositive = true
    )
    
    StatCard(
        modifier = Modifier.weight(1f),
        value = "85%",
        label = "Success Rate"
    )
}
```

### ListItemCard - Settings & Navigation
```kotlin
ListItemCard(
    title = "Notifications",
    subtitle = "Push, Email, SMS",
    leadingIcon = Icons.Default.Notifications,
    trailingIcon = Icons.Default.ChevronRight,
    onClick = { /* navigate */ }
)
```

### FeatureCard - Feature Showcase
```kotlin
FeatureCard(
    title = "Fast Setup",
    description = "Get started in minutes",
    icon = Icons.Default.Speed,
    iconBackgroundColor = AppTheme.colors.brandSurfaceSubtle
)
```

### CompactCard - Tags & Chips
```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Small)) {
    CompactCard(title = "Health", icon = Icons.Default.FavoriteBorder)
    CompactCard(title = "Fitness", icon = Icons.Default.FitnessCenter)
}
```

### SummaryCard - Grouped Data
```kotlin
SummaryCard(
    title = "Weekly Summary",
    icon = Icons.Default.CalendarMonth,
    items = listOf(
        "Total" to "12",
        "Completed" to "10",
        "Success Rate" to "83%"
    )
)
```

## 🎨 Card Styling

All cards support customization:

### Variants
```kotlin
variant = BaseCardVariant.Elevated    // Shadow (default for info)
variant = BaseCardVariant.Outlined    // Border only
variant = BaseCardVariant.Filled      // Solid background
variant = BaseCardVariant.Ghost       // Subtle (default for list items)
```

### Padding
```kotlin
padding = BaseCardPadding.Compact     // 8dp
padding = BaseCardPadding.Small       // 12dp  
padding = BaseCardPadding.Medium      // 16dp (default)
padding = BaseCardPadding.Large       // 24dp
```

### Custom Colors
```kotlin
InfoCard(
    title = "Custom",
    backgroundColor = Color.Blue,      // Optional override
    variant = BaseCardVariant.Filled
)
```

## 🎨 Theme Customization

### Using AppTheme

```kotlin
@Composable
fun App() {
    AppTheme(
        isDarkTheme = isSystemInDarkTheme()
    ) {
        // Your app content
        MyScreen()
    }
}
```

### Custom Typography

```kotlin
@Composable
fun App() {
    val customFont = FontFamily(/* your font */)
    
    AppTheme(
        customFontFamily = customFont
    ) {
        // Your app content with custom font
    }
}
```

### Accessing Theme Values

```kotlin
@Composable
fun MyComponent() {
    // Colors
    val brandColor = AppTheme.colors.brandContentDefault
    val backgroundColor = AppTheme.colors.baseSurfaceDefault
    
    // Typography
    val titleStyle = AppTheme.typography.titleBold
    val bodyStyle = AppTheme.typography.bodyRegular
    
    // Spacing
    val padding = Spacing.Medium
    val gap = Spacing.Small
}
```

## 🔄 Loading States

All components support skeleton loading:

```kotlin
InfoCard(
    title = "Loading...",
    isLoading = true  // Shows skeleton loader
)

PixaImage(
    source = PixaImageSource.Url(imageUrl),
    contentDescription = "Image",
    isLoading = isImageLoading
)
```

## 🌐 Icons & Images

### PixaIcon - Unified Icon Component

```kotlin
// Vector icon
PixaIcon(
    source = IconSource.Vector(Icons.Default.Home),
    contentDescription = "Home",
    size = IconSize.Large,
    tint = AppTheme.colors.brandContentDefault
)

// Remote URL icon
PixaIcon(
    source = IconSource.Url("https://example.com/icon.png"),
    contentDescription = "Logo",
    size = 48.dp
)

// Painter icon
PixaIcon(
    source = IconSource.Resource(painterResource(R.drawable.logo)),
    contentDescription = "App Logo"
)
```

### PixaImage - Advanced Image Loading

```kotlin
PixaImage(
    source = PixaImageSource.Url("https://example.com/image.jpg"),
    contentDescription = "Product Image",
    modifier = Modifier.size(200.dp),
    contentScale = ContentScale.Crop,
    shape = RoundedCornerShape(12.dp),
    enableCrossfade = true,
    placeholder = painterResource(R.drawable.placeholder),
    error = painterResource(R.drawable.error)
)
```

## 📱 Examples

### Dashboard Screen
```kotlin
@Composable
fun DashboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.Medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
    ) {
        // Stats Row
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = "42",
                label = "Total",
                icon = Icons.Default.CheckCircle
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = "85%",
                label = "Success",
                trend = "+5%",
                trendPositive = true
            )
        }
        
        // Info Card
        InfoCard(
            title = "New Features",
            description = "Check out the latest updates",
            icon = Icons.Default.Info
        )
        
        // Action Card
        ActionCard(
            title = "Settings",
            description = "Manage preferences",
            icon = Icons.Default.Settings,
            trailingIcon = Icons.Default.ChevronRight,
            onClick = { /* navigate */ }
        )
    }
}
```

### Settings Screen
```kotlin
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Spacing.Tiny)
    ) {
        ListItemCard(
            title = "Profile",
            subtitle = "Manage your account",
            leadingIcon = Icons.Default.Person,
            trailingIcon = Icons.Default.ChevronRight,
            onClick = { }
        )
        
        ListItemCard(
            title = "Notifications",
            subtitle = "Push, Email, SMS",
            leadingIcon = Icons.Default.Notifications,
            trailingIcon = Icons.Default.ChevronRight,
            onClick = { }
        )
        
        ListItemCard(
            title = "Privacy",
            subtitle = "Data and security",
            leadingIcon = Icons.Default.Lock,
            trailingIcon = Icons.Default.ChevronRight,
            onClick = { }
        )
    }
}
```

## 📖 Documentation

For detailed documentation on each component, see [COMPONENTS.md](COMPONENTS.md)

## 🤝 Contributing

Contributions are welcome! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 📄 License

```
Copyright 2025 PixaMob

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

- [Maven Central Repository](https://central.sonatype.com/artifact/com.pixamob/pixacompose)
- [Issue Tracker](https://github.com/pixamob/pixacompose/issues)
- [Changelog](CHANGELOG.md)

---

**Made with ❤️ by PixaMob**

