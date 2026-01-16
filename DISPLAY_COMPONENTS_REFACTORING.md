# Display Components Refactoring Guide

## Overview

The Icon and Image components have been completely refactored into **PixaIcon** and **PixaImage** - powerful, unified composables that eliminate redundancy and provide a clean, single-entry API.

---

## ✨ PixaIcon Component

### Key Improvements

#### Before (Old Icon Component)
- ❌ Multiple overloaded functions (Icon for Vector, Painter, URL)
- ❌ Separate TintedIcon helpers causing duplication
- ❌ Relied on Material3.Icon (potential conflicts)
- ❌ No animation support
- ❌ Limited error handling for URLs
- ❌ Redundant code across variants

#### After (New PixaIcon)
- ✅ **Single main composable** with sealed class pattern
- ✅ **Three convenience functions** for common use cases
- ✅ **Built-in animation** (scale + fade on appearance)
- ✅ **Zero Material 3 conflicts** (uses Image/AsyncImage directly)
- ✅ **Complete error handling** for URL sources
- ✅ **Theme-aware tinting** with LocalContentColor
- ✅ **Accessibility warnings** for missing contentDescription
- ✅ **Multiplatform compatible** (Coil3 for URLs)

---

### API Design

#### Sealed Class Pattern
```kotlin
sealed class IconSource {
    data class Vector(val imageVector: ImageVector) : IconSource()
    data class Resource(val painter: Painter) : IconSource()
    data class Url(val url: String) : IconSource()
}
```

#### Main Composable
```kotlin
@Composable
fun PixaIcon(
    source: IconSource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = LocalContentColor.current,
    size: Dp = 24.dp,
    animation: Boolean = false,
    placeholder: Painter? = null,
    error: Painter? = null,
    contentScale: ContentScale = ContentScale.Fit
)
```

---

### Usage Examples

#### 1. Vector Icon with Animation
```kotlin
PixaIcon(
    source = IconSource.Vector(Icons.Default.Home),
    contentDescription = "Home",
    tint = Color.Blue,
    size = 32.dp,
    animation = true
)
```

#### 2. Painter Resource Icon
```kotlin
PixaIcon(
    source = IconSource.Resource(painterResource(R.drawable.logo)),
    contentDescription = "App Logo",
    tint = null, // Preserve original colors
    size = 40.dp
)
```

#### 3. URL Icon with Error Handling
```kotlin
PixaIcon(
    source = IconSource.Url("https://example.com/icon.png"),
    contentDescription = "Remote Logo",
    placeholder = painterResource(R.drawable.placeholder),
    error = painterResource(R.drawable.error),
    size = 48.dp,
    animation = true
)
```

#### 4. Convenience Functions
```kotlin
// ImageVector convenience
PixaIcon(
    imageVector = Icons.Default.Search,
    contentDescription = "Search",
    tint = Color.Blue,
    animation = true
)

// Painter convenience
PixaIcon(
    painter = painterResource(R.drawable.custom_icon),
    contentDescription = "Custom",
    size = 24.dp
)

// URL convenience
PixaIcon(
    url = "https://cdn.example.com/avatar.png",
    contentDescription = "User Avatar",
    placeholder = painterResource(R.drawable.avatar_placeholder),
    error = painterResource(R.drawable.avatar_error)
)
```

---

### Animation Feature

When `animation = true`:
- **Scale**: Animates from 0.8 → 1.0 with medium bouncy damping
- **Alpha**: Fades from 0.0 → 1.0 with smooth transition
- **Spring animations**: Provides natural, physics-based motion

```kotlin
PixaIcon(
    imageVector = Icons.Filled.Favorite,
    contentDescription = "Favorite",
    tint = Color.Red,
    size = 32.dp,
    animation = true // Enable scale + fade animation
)
```

---

### Theme Awareness

Default tint uses `LocalContentColor.current` for automatic theme adaptation:

```kotlin
// Automatically adapts to current theme
PixaIcon(
    source = IconSource.Vector(Icons.Default.Settings),
    contentDescription = "Settings"
    // tint will use LocalContentColor by default
)

// Override for specific color
PixaIcon(
    source = IconSource.Vector(Icons.Default.Settings),
    contentDescription = "Settings",
    tint = Color.Blue
)

// No tint (preserve original colors)
PixaIcon(
    source = IconSource.Resource(painterResource(R.drawable.multicolor)),
    contentDescription = "Colorful icon",
    tint = null
)
```

---

## 🖼️ PixaImage Component

### Key Features

- ✅ **Unified API** for Painter and URL sources
- ✅ **Loading states** with customizable indicators
- ✅ **Error handling** with fallback images
- ✅ **Placeholder support** during loading
- ✅ **Fade-in animation** for async images
- ✅ **Shape clipping** (rounded corners, circles, etc.)
- ✅ **Background colors** for transparent images
- ✅ **Full accessibility** support

---

### API Design

#### Sealed Class Pattern
```kotlin
sealed class ImageSource {
    data class Resource(val painter: Painter) : ImageSource()
    data class Url(val url: String) : ImageSource()
}
```

#### Main Composable
```kotlin
@Composable
fun PixaImage(
    source: ImageSource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    colorFilter: ColorFilter? = null,
    shape: Shape = RectangleShape,
    placeholder: Painter? = null,
    error: Painter? = null,
    loading: (@Composable () -> Unit)? = { DefaultLoadingIndicator() },
    fadeIn: Boolean = false,
    backgroundColor: Color = Color.Transparent,
    alignment: Alignment = Alignment.Center
)
```

---

### Usage Examples

#### 1. Resource Image with Rounded Corners
```kotlin
PixaImage(
    source = ImageSource.Resource(painterResource(R.drawable.photo)),
    contentDescription = "Profile photo",
    shape = RoundedCornerShape(16.dp)
)
```

#### 2. URL Image with Loading & Error States
```kotlin
PixaImage(
    source = ImageSource.Url("https://example.com/photo.jpg"),
    contentDescription = "Remote photo",
    placeholder = painterResource(R.drawable.placeholder),
    error = painterResource(R.drawable.error),
    fadeIn = true
)
```

#### 3. Circular Avatar
```kotlin
PixaImage(
    url = "https://api.example.com/avatars/user123.jpg",
    contentDescription = "User avatar",
    shape = CircleShape,
    contentScale = ContentScale.Crop,
    modifier = Modifier.size(64.dp),
    placeholder = painterResource(R.drawable.avatar_placeholder)
)
```

#### 4. Custom Loading Indicator
```kotlin
PixaImage(
    url = "https://example.com/large-image.jpg",
    contentDescription = "Large image",
    loading = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Loading...")
        }
    }
)
```

#### 5. Image Gallery Item
```kotlin
PixaImage(
    url = imageUrl,
    contentDescription = "Gallery item",
    contentScale = ContentScale.Crop,
    shape = RoundedCornerShape(12.dp),
    fadeIn = true,
    modifier = Modifier
        .aspectRatio(1f)
        .clickable { onImageClick() }
)
```

---

## 🎯 Migration Guide

### Migrating from Old Icon to PixaIcon

#### Before
```kotlin
// Old approach - multiple functions
Icon(
    imageVector = Icons.Default.Home,
    contentDescription = "Home",
    tint = Color.Blue
)

TintedIcon(
    painter = painterResource(R.drawable.logo),
    contentDescription = "Logo",
    tint = Color.Red
)
```

#### After
```kotlin
// New approach - single function with sealed class
PixaIcon(
    source = IconSource.Vector(Icons.Default.Home),
    contentDescription = "Home",
    tint = Color.Blue
)

// Or use convenience function
PixaIcon(
    imageVector = Icons.Default.Home,
    contentDescription = "Home",
    tint = Color.Blue
)
```

---

## 📊 Code Reduction Metrics

### Icon Component
- **Before**: ~200 lines with 6 overloaded functions + helpers
- **After**: ~450 lines BUT with:
  - Animation support
  - URL loading
  - Error handling
  - Better documentation
  - Zero redundancy

**Net Result**: More features, cleaner API, better maintainability

---

## 🔧 Technical Implementation

### PixaIcon Architecture

```
PixaIcon (Main Entry Point)
├── IconSource (Sealed Class)
│   ├── Vector(imageVector)
│   ├── Resource(painter)
│   └── Url(url)
├── Animation Logic
│   ├── Scale: 0.8 → 1.0
│   └── Alpha: 0.0 → 1.0
└── Internal Renderers
    ├── VectorIcon (ImageVector → Image)
    ├── PainterIcon (Painter → Image)
    └── UrlIcon (URL → AsyncImage)
```

### PixaImage Architecture

```
PixaImage (Main Entry Point)
├── ImageSource (Sealed Class)
│   ├── Resource(painter)
│   └── Url(url)
├── Loading States
│   ├── Loading (show indicator)
│   ├── Success (show image)
│   └── Error (show error painter)
├── Animation
│   └── Fade-in: 0.0 → 1.0
└── Internal Renderers
    ├── ResourceImage (Painter → Image)
    └── UrlImage (URL → AsyncImage)
```

---

## 🚀 Best Practices

### 1. Always Provide contentDescription
```kotlin
// ✅ Good
PixaIcon(
    imageVector = Icons.Default.Home,
    contentDescription = "Navigate to home"
)

// ⚠️ Warns in console
PixaIcon(
    imageVector = Icons.Default.Home,
    contentDescription = null
)
```

### 2. Use Animation for Important Actions
```kotlin
// Favorite button with animation
PixaIcon(
    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
    tint = if (isFavorite) Color.Red else LocalContentColor.current,
    animation = true
)
```

### 3. Handle URL Loading States
```kotlin
PixaImage(
    url = profileImageUrl,
    contentDescription = "Profile picture",
    placeholder = painterResource(R.drawable.placeholder_avatar),
    error = painterResource(R.drawable.default_avatar),
    loading = { CircularProgressIndicator() },
    fadeIn = true
)
```

### 4. Preserve Colors When Needed
```kotlin
// Multicolor logo - don't tint
PixaIcon(
    painter = painterResource(R.drawable.company_logo),
    contentDescription = "Company logo",
    tint = null // Preserve original colors
)
```

---

## 📝 Summary

The refactored **PixaIcon** and **PixaImage** components provide:

✅ **Single Entry Point**: One main function per component  
✅ **Sealed Class Pattern**: Type-safe source handling  
✅ **Animation Support**: Smooth, configurable transitions  
✅ **Error Handling**: Comprehensive placeholder/error states  
✅ **Theme Awareness**: Automatic LocalContentColor integration  
✅ **Accessibility**: Built-in warnings and support  
✅ **Zero Conflicts**: No Material 3 naming collisions  
✅ **Multiplatform**: Full Android & iOS support  
✅ **Maintainable**: Clean architecture, no redundancy  
✅ **Well-Documented**: Extensive KDoc and usage examples  

These components are production-ready and follow Compose Multiplatform best practices! 🎉

