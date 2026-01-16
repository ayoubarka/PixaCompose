# PixaImage Component Documentation

## Overview

**PixaImage** is a powerful, production-ready image component for Compose Multiplatform that handles all image display scenarios with robust loading/error states using Coil 3.

---

## ✨ Key Features

### Core Capabilities
- ✅ **Single Entry Point**: One composable for URLs, Painters, and ImageVectors
- ✅ **Coil 3 Integration**: Async URL loading with full multiplatform support
- ✅ **Automatic State Handling**: Loading, success, and error states managed automatically
- ✅ **Shimmer Loading Effect**: Beautiful animated loading using valentinilk.shimmer
- ✅ **Theme-Aware**: Uses Material3 color scheme for fallbacks
- ✅ **Mobile-First**: Touch-friendly with ripple effects on click
- ✅ **Accessibility**: Full semantics support with role and contentDescription
- ✅ **Shape Clipping**: Support for circles, rounded corners, custom shapes
- ✅ **Performance**: Efficient rendering with proper state management

### Loading & Error States
- **Loading**: Shimmer effect (default) or custom placeholder painter
- **Error**: Broken image icon (default) or custom error fallback
- **Success**: Smooth display with optional crossfade animation

---

## 📦 Component Structure

### Sealed Class: PixaImageSource

```kotlin
sealed class PixaImageSource {
    data class Url(val url: String) : PixaImageSource()
    data class Resource(val painter: Painter) : PixaImageSource()
    data class Vector(val imageVector: ImageVector) : PixaImageSource()
}
```

### Main Composable Signature

```kotlin
@Composable
fun PixaImage(
    source: PixaImageSource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RectangleShape,
    size: Dp? = null,
    tint: Color? = null,
    loadingPlaceholder: Painter? = null,
    errorFallback: Painter? = null,
    onClick: (() -> Unit)? = null,
    crossfade: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    alignment: Alignment = Alignment.Center
)
```

---

## 🎯 Usage Examples

### 1. URL Image (Basic)

**Most common use case** - Display remote image with automatic loading/error states:

```kotlin
PixaImage(
    source = PixaImageSource.Url("https://example.com/photo.jpg"),
    contentDescription = "Profile photo",
    shape = CircleShape,
    size = 64.dp
)
```

**What happens:**
- Shows shimmer loading effect while image loads
- Displays image when loaded successfully
- Shows broken image icon if loading fails

---

### 2. Avatar Image (Circular with Click)

**User profile pictures** with click handling:

```kotlin
PixaImage(
    url = user.avatarUrl, // Convenience function
    contentDescription = "User avatar: ${user.name}",
    shape = CircleShape,
    size = 80.dp,
    contentScale = ContentScale.Crop,
    onClick = { navigateToProfile(user.id) }
    // Ripple effect automatically added
)
```

---

### 3. Photo Gallery Item

**Grid or list images** with custom aspect ratio:

```kotlin
PixaImage(
    url = photo.url,
    contentDescription = "Photo: ${photo.title}",
    shape = RoundedCornerShape(12.dp),
    contentScale = ContentScale.Crop,
    onClick = { openFullScreen(photo) },
    modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f) // Square
)
```

---

### 4. Banner Image with Custom Placeholders

**Hero images, banners** with branded loading/error states:

```kotlin
PixaImage(
    url = banner.imageUrl,
    contentDescription = "Promotional banner",
    loadingPlaceholder = painterResource(R.drawable.banner_placeholder),
    errorFallback = painterResource(R.drawable.banner_error),
    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    contentScale = ContentScale.FillWidth,
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
)
```

---

### 5. Vector Icon as Image

**Icon-as-image scenarios** with theme-aware tinting:

```kotlin
PixaImage(
    source = PixaImageSource.Vector(Icons.Default.Person),
    contentDescription = "User profile",
    tint = MaterialTheme.colorScheme.primary,
    size = 48.dp
)
```

---

### 6. Product Thumbnail

**E-commerce product images** with background for transparent PNGs:

```kotlin
PixaImage(
    url = product.thumbnailUrl,
    contentDescription = "Product: ${product.name}",
    backgroundColor = Color.White,
    shape = RoundedCornerShape(8.dp),
    contentScale = ContentScale.Fit, // Preserve aspect ratio
    size = 120.dp
)
```

---

### 7. Local Resource Image

**Bundled assets, onboarding screens**:

```kotlin
PixaImage(
    painter = painterResource(R.drawable.onboarding_1),
    contentDescription = "Onboarding screen 1: Welcome",
    contentScale = ContentScale.FillBounds,
    modifier = Modifier.fillMaxSize()
)
```

---

### 8. Card Cover Image

**Responsive card images** with 16:9 aspect ratio:

```kotlin
Card {
    PixaImage(
        url = article.coverImageUrl,
        contentDescription = "Article cover: ${article.title}",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    )
}
```

---

## 🎨 Styling Guide

### Shape Options

```kotlin
// Rectangle (default)
shape = RectangleShape

// Circle (avatars)
shape = CircleShape

// Rounded corners (all corners)
shape = RoundedCornerShape(16.dp)

// Rounded corners (specific corners)
shape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

// Custom shape
shape = CutCornerShape(12.dp)
```

### ContentScale Options

```kotlin
// Crop to fill (photos, avatars)
contentScale = ContentScale.Crop

// Fit inside bounds (logos, icons)
contentScale = ContentScale.Fit

// Fill entire bounds (backgrounds)
contentScale = ContentScale.FillBounds

// Fill width, scale height
contentScale = ContentScale.FillWidth

// Fill height, scale width
contentScale = ContentScale.FillHeight
```

### Size Management

```kotlin
// Fixed size (icons, avatars)
size = 64.dp

// Responsive size via modifier (preferred for cards, banners)
modifier = Modifier
    .fillMaxWidth()
    .height(200.dp)

// Aspect ratio (gallery items)
modifier = Modifier
    .fillMaxWidth()
    .aspectRatio(1f)
```

---

## 🔧 Advanced Features

### Custom Loading State

Replace default shimmer with custom placeholder:

```kotlin
PixaImage(
    url = imageUrl,
    contentDescription = "Photo",
    loadingPlaceholder = painterResource(R.drawable.custom_loader),
    // Shimmer effect not shown when custom placeholder provided
)
```

### Custom Error State

Replace default broken image icon:

```kotlin
PixaImage(
    url = imageUrl,
    contentDescription = "Photo",
    errorFallback = painterResource(R.drawable.custom_error),
    // Custom error shown instead of default icon
)
```

### Tinting Images

Apply color tint (works for vectors and painters):

```kotlin
PixaImage(
    source = PixaImageSource.Vector(Icons.Default.Star),
    contentDescription = "Favorite",
    tint = Color.Yellow, // Apply tint
    size = 32.dp
)

// Remove tint (preserve original colors)
PixaImage(
    painter = painterResource(R.drawable.colorful_logo),
    contentDescription = "Logo",
    tint = null // No tint applied
)
```

### Background Colors

Useful for transparent PNGs or loading states:

```kotlin
PixaImage(
    url = productImage,
    contentDescription = "Product",
    backgroundColor = MaterialTheme.colorScheme.surface,
    // Ensures transparent areas have a background
)
```

---

## ♿ Accessibility

### Always Provide contentDescription

```kotlin
// ✅ Good
PixaImage(
    url = imageUrl,
    contentDescription = "User profile picture: John Doe"
)

// ⚠️ Warns in console
PixaImage(
    url = imageUrl,
    contentDescription = null
)
```

### Descriptive Content

```kotlin
// ❌ Not helpful
contentDescription = "Image"

// ✅ Descriptive
contentDescription = "Profile picture of ${user.name}"
contentDescription = "Product photo: ${product.name}"
contentDescription = "Article cover: ${article.title}"
```

### Decorative Images

For purely decorative images (rare):

```kotlin
PixaImage(
    url = decorativePattern,
    contentDescription = null, // Intentionally null for decorative
    // Will warn but acceptable for decorative content
)
```

---

## 🚀 Performance Tips

### 1. Use Appropriate ContentScale

```kotlin
// For photos, avatars (better performance)
contentScale = ContentScale.Crop

// For logos, icons
contentScale = ContentScale.Fit
```

### 2. Specify Size When Possible

```kotlin
// Better performance - fixed size
size = 64.dp

// vs

// May cause re-composition
modifier = Modifier.size(avatarSize)
```

### 3. Reuse Placeholders

```kotlin
// Define once
val defaultPlaceholder = painterResource(R.drawable.placeholder)

// Reuse everywhere
PixaImage(
    url = url1,
    contentDescription = "Photo 1",
    loadingPlaceholder = defaultPlaceholder
)

PixaImage(
    url = url2,
    contentDescription = "Photo 2",
    loadingPlaceholder = defaultPlaceholder
)
```

---

## 🐛 Troubleshooting

### Image Not Loading

**Check:**
1. URL is valid and accessible
2. Internet permission granted (Android)
3. HTTPS/ATS configured (iOS)
4. Coil 3 properly initialized

**Debug:**
```kotlin
PixaImage(
    url = imageUrl,
    contentDescription = "Debug image",
    errorFallback = painterResource(R.drawable.error),
    // Error fallback will show if loading fails
)
```

### Shimmer Not Appearing

**Ensure:**
1. `com.valentinilk.shimmer` dependency added
2. No custom `loadingPlaceholder` provided (overrides shimmer)

### Click Not Working

**Check:**
```kotlin
// ✅ Correct
onClick = { doSomething() }

// ❌ Won't work
onClick = null
```

### Accessibility Warning

**Fix:**
```kotlin
// Provide meaningful contentDescription
contentDescription = "Profile picture of ${user.name}"
```

---

## 📚 API Reference

### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `source` | `PixaImageSource` | Required | Image source (Url, Resource, Vector) |
| `contentDescription` | `String?` | Required | Accessibility description |
| `modifier` | `Modifier` | `Modifier` | Container modifier |
| `contentScale` | `ContentScale` | `Crop` | How to scale image |
| `shape` | `Shape` | `RectangleShape` | Clipping shape |
| `size` | `Dp?` | `null` | Fixed size |
| `tint` | `Color?` | `null` | Tint color (vectors/painters) |
| `loadingPlaceholder` | `Painter?` | `null` | Custom loading image |
| `errorFallback` | `Painter?` | `null` | Custom error image |
| `onClick` | `(() -> Unit)?` | `null` | Click handler |
| `crossfade` | `Boolean` | `true` | Crossfade animation |
| `backgroundColor` | `Color` | `Transparent` | Background color |
| `alignment` | `Alignment` | `Center` | Image alignment |

### Convenience Functions

```kotlin
// URL shorthand
fun PixaImage(
    url: String,
    contentDescription: String?,
    ...
)

// Painter shorthand
fun PixaImage(
    painter: Painter,
    contentDescription: String?,
    ...
)

// ImageVector shorthand
fun PixaImage(
    imageVector: ImageVector,
    contentDescription: String?,
    ...
)
```

---

## 🎯 Best Practices Summary

1. ✅ **Always provide contentDescription** for accessibility
2. ✅ **Use CircleShape** for avatars and profile pictures
3. ✅ **Use ContentScale.Crop** for photos
4. ✅ **Use ContentScale.Fit** for logos and icons
5. ✅ **Specify fixed size** when possible for performance
6. ✅ **Add onClick** for interactive images
7. ✅ **Use backgroundColor** for transparent PNGs
8. ✅ **Provide custom error fallbacks** for branded experiences
9. ✅ **Test with slow networks** to verify loading states
10. ✅ **Use convenience functions** for cleaner code

---

## 🔗 Related Components

- **PixaIcon**: For small icons (24dp-48dp) with animation support
- **PixaAvatar**: Specialized avatar component (if implemented)
- **PixaImageCarousel**: Image gallery/carousel (if implemented)

---

## 📦 Dependencies

- `coil3.compose` - Async image loading (Coil 3)
- `com.valentinilk.shimmer` - Shimmer loading effect
- `androidx.compose.material3` - Theme-aware colors and ripple

---

## 🎉 Summary

**PixaImage** provides a complete, production-ready solution for all image display needs in Compose Multiplatform applications. With automatic loading/error handling, theme-aware fallbacks, and full accessibility support, it eliminates boilerplate while maintaining flexibility for custom scenarios.

Use it for avatars, photos, banners, product images, and any other image display requirement!

