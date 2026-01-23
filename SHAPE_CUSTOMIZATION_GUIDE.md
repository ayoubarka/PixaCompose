# Shape Customization Guide

## Overview

PixaCompose provides a comprehensive shape customization system that extends beyond standard rounded and cut corners. The system includes various custom shape families for creating unique and engaging UI designs.

## Shape Families

### 1. Basic Shapes (Material3)

#### Rounded Corners
Standard rounded corner shapes using Material3's `RoundedCornerShape`.

```kotlin
// Access via AppTheme
Box(
    modifier = Modifier.background(
        color = Color.Blue,
        shape = AppTheme.shapes.rounded.medium
    )
)

// Available sizes: extraSmall, small, medium, large, extraLarge
```

#### Cut Corners
Angular cut corner shapes using Material3's `CutCornerShape`.

```kotlin
Card(
    shape = AppTheme.shapes.cut.large
) {
    // Content
}
```

---

### 2. Concave Shapes

Shapes with inward-dipping curves. Perfect for creating depth and unique header/footer designs.

**Parameters:**
- `position`: Top, Bottom, Left, Right
- `curveDepth`: 0.0 to 1.0 (fraction of dimension)
- `cornerRadius`: Radius for remaining corners

**Example:**

```kotlin
// Top concave header
Surface(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .background(
            color = Color(0xFF6200EE),
            shape = AppTheme.shapes.concaveTop.medium
        )
) {
    // Header content
}

// Bottom concave footer
Box(
    modifier = Modifier.background(
        color = Color(0xFF03DAC5),
        shape = AppTheme.shapes.concaveBottom.large
    )
)
```

**Custom Usage:**

```kotlin
// Create custom concave shape
val customConcave = ConcaveShape(
    position = ShapePosition.Left,
    curveDepth = 0.2f,
    cornerRadius = 16f
)

Box(
    modifier = Modifier.background(
        color = Color.Red,
        shape = customConcave
    )
)
```

---

### 3. Convex Shapes

Shapes with outward-bulging curves. Ideal for prominent buttons, cards, or focal elements.

**Parameters:**
- `position`: Top, Bottom, Left, Right
- `curveDepth`: 0.0 to 1.0 (outward curve depth)
- `cornerRadius`: Radius for remaining corners

**Example:**

```kotlin
// Prominent card with convex top
Card(
    modifier = Modifier.size(300.dp, 200.dp),
    shape = AppTheme.shapes.convexTop.medium,
    elevation = 8.dp
) {
    // Card content
}

// Custom convex shape
val bubbleButton = ConvexShape(
    position = ShapePosition.Bottom,
    curveDepth = 0.3f,
    cornerRadius = 12f
)
```

---

### 4. Wave Shapes

Smooth wave patterns for decorative edges.

**Parameters:**
- `position`: Top, Bottom, Left, Right
- `wavelength`: Number of complete wave cycles (default 2)
- `amplitude`: Wave height/depth (0.0 to 1.0)
- `cornerRadius`: Radius for non-wave edges

**Example:**

```kotlin
// Decorative wave footer
Surface(
    modifier = Modifier
        .fillMaxWidth()
        .height(100.dp),
    shape = AppTheme.shapes.wave.medium,
    color = Color(0xFF018786)
) {
    // Content
}

// Custom wave with more cycles
val detailedWave = WaveShape(
    position = ShapePosition.Top,
    wavelength = 4,
    amplitude = 0.08f,
    cornerRadius = 8f
)
```

---

### 5. Arch Shapes

Dome or arch-like curves.

**Parameters:**
- `position`: Top, Bottom, Left, Right
- `curvature`: Arch intensity (0.0 to 1.0, where 0.5 is semicircle)
- `cornerRadius`: Radius for remaining corners

**Example:**

```kotlin
// Arched header
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .background(
            color = Color(0xFF3700B3),
            shape = AppTheme.shapes.archTop.large
        )
) {
    // Header content
}

// Custom arch
val doorwayArch = ArchShape(
    position = ShapePosition.Top,
    curvature = 0.4f,
    cornerRadius = 0f
)
```

---

### 6. Tab Shapes

Browser tab-like shapes.

**Parameters:**
- `position`: Top or Bottom
- `tabWidthFraction`: Tab width as fraction of total (0.0 to 1.0)
- `tabHeight`: Height of the tab portion
- `cornerRadius`: Radius for rounded corners

**Example:**

```kotlin
// Tab navigation item
Surface(
    modifier = Modifier
        .width(150.dp)
        .height(80.dp),
    shape = AppTheme.shapes.tab.medium,
    color = if (selected) Color.White else Color.Gray
) {
    Text(
        text = "Tab Title",
        modifier = Modifier.padding(16.dp)
    )
}

// Custom tab
val wideTab = TabShape(
    position = ShapePosition.Top,
    tabWidthFraction = 0.95f,
    tabHeight = 50f,
    cornerRadius = 20f
)
```

---

### 7. Notch Shapes

Shapes with cutout notches.

**Parameters:**
- `position`: Top, Bottom, Left, Right
- `notchSize`: Notch size as fraction (0.0 to 1.0)
- `style`: `NotchStyle.Rounded` or `NotchStyle.Sharp`
- `cornerRadius`: Radius for remaining corners

**Example:**

```kotlin
// Rounded notch for camera cutout
Card(
    shape = AppTheme.shapes.notchRounded.medium
) {
    // Content with notch at top
}

// Sharp notch
Card(
    shape = AppTheme.shapes.notchSharp.small
) {
    // Content
}

// Custom notch at bottom
val customNotch = NotchShape(
    position = ShapePosition.Bottom,
    notchSize = 0.3f,
    style = NotchStyle.Rounded,
    cornerRadius = 16f
)
```

---

### 8. Bubble Shapes

Chat bubble shapes with tails. **RTL-aware** for proper internationalization.

**Parameters:**
- `tailPosition`: Left, Right, or Bottom
- `tailSize`: Size of the tail in pixels
- `cornerRadius`: Radius for rounded corners
- `tailOffset`: Tail position as fraction (0.0 to 1.0)

**Example:**

```kotlin
// Chat message (left bubble)
Row(modifier = Modifier.fillMaxWidth()) {
    Surface(
        modifier = Modifier.padding(8.dp),
        shape = AppTheme.shapes.bubbleLeft.medium,
        color = Color.LightGray
    ) {
        Text(
            text = "Hello! How are you?",
            modifier = Modifier.padding(12.dp)
        )
    }
}

// Reply message (right bubble)
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.End
) {
    Surface(
        modifier = Modifier.padding(8.dp),
        shape = AppTheme.shapes.bubbleRight.medium,
        color = Color(0xFF6200EE)
    ) {
        Text(
            text = "I'm good, thanks!",
            modifier = Modifier.padding(12.dp),
            color = Color.White
        )
    }
}

// Custom bubble with tail at specific position
val customBubble = BubbleShape(
    tailPosition = ShapePosition.Bottom,
    tailSize = 16f,
    cornerRadius = 20f,
    tailOffset = 0.3f  // Tail at 30% from edge
)
```

---

## Size Variants

All shape families provide five size variants:

- `extraSmall` - Subtle effects
- `small` - Small elements
- `medium` - Default/common use
- `large` - Prominent elements
- `extraLarge` - Hero sections

**Example:**

```kotlin
Column {
    Box(modifier = Modifier.background(Color.Blue, AppTheme.shapes.concaveTop.extraSmall))
    Box(modifier = Modifier.background(Color.Blue, AppTheme.shapes.concaveTop.small))
    Box(modifier = Modifier.background(Color.Blue, AppTheme.shapes.concaveTop.medium))
    Box(modifier = Modifier.background(Color.Blue, AppTheme.shapes.concaveTop.large))
    Box(modifier = Modifier.background(Color.Blue, AppTheme.shapes.concaveTop.extraLarge))
}
```

---

## Custom Shape Creation

All shape classes implement `Shape` interface and support custom parameters:

```kotlin
// Custom concave with specific parameters
val myShape = ConcaveShape(
    position = ShapePosition.Bottom,
    curveDepth = 0.25f,
    cornerRadius = 24f
)

// Custom wave with many cycles
val oceanWave = WaveShape(
    position = ShapePosition.Top,
    wavelength = 6,
    amplitude = 0.12f,
    cornerRadius = 0f
)

// Custom bubble for specific chat style
val messageBubble = BubbleShape(
    tailPosition = ShapePosition.Left,
    tailSize = 10f,
    cornerRadius = 18f,
    tailOffset = 0.85f
)
```

---

## RTL Support

Bubble shapes automatically adapt to RTL (Right-to-Left) layouts:

```kotlin
// Automatically flips in RTL languages
CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    // Left bubble will appear on the right in RTL
    Surface(
        shape = AppTheme.shapes.bubbleLeft.medium
    ) {
        Text("مرحبا")
    }
}
```

---

## Combining with Modifiers

Shapes work seamlessly with standard Compose modifiers:

```kotlin
// With clip
Image(
    painter = painterResource(id = R.drawable.image),
    contentDescription = null,
    modifier = Modifier
        .size(200.dp)
        .clip(AppTheme.shapes.archTop.large)
)

// With border
Box(
    modifier = Modifier
        .size(150.dp)
        .border(
            width = 2.dp,
            color = Color.Blue,
            shape = AppTheme.shapes.convexTop.medium
        )
)

// With shadow/elevation
Surface(
    modifier = Modifier.size(200.dp),
    shape = AppTheme.shapes.tab.large,
    shadowElevation = 8.dp
) {
    // Content
}
```

---

## Use Cases by Shape Type

### Concave Shapes
- Page headers with unique curves
- Card footers with depth
- Sidebar navigation edges
- Bottom sheets with curved tops

### Convex Shapes
- Prominent CTA buttons
- Floating action buttons with custom shapes
- Spotlight/highlight sections
- Decorative banners

### Wave Shapes
- Decorative page dividers
- Ocean/water-themed designs
- Footer decorations
- Background patterns

### Arch Shapes
- Doorway/portal effects
- Top navigation bars
- Modal dialog tops
- Architectural design elements

### Tab Shapes
- Browser-style tabs
- Segmented controls
- Navigation items
- Folder tabs

### Notch Shapes
- Camera cutout accommodations
- Sensor area designs
- Ticket-style cards
- Tag shapes

### Bubble Shapes
- Chat messages
- Tooltips
- Speech bubbles
- Annotation callouts

---

## Performance Considerations

1. **Path Creation**: Custom shapes create paths on each recomposition. Consider:
   - Using `remember` for complex custom shapes used frequently
   - Keeping `wavelength` reasonable for wave shapes (2-4 cycles)

2. **Canvas Performance**: 
   - Simpler shapes (Concave, Convex, Arch) are more performant
   - Complex waves with many segments may impact scroll performance

3. **Best Practices**:
   ```kotlin
   // Good: Reuse shape instance
   val myShape = remember { 
       ConcaveShape(ShapePosition.Top, 0.15f, 16f) 
   }
   
   // Prefer predefined variants when possible
   shape = AppTheme.shapes.concaveTop.medium
   ```

---

## Complete Example

```kotlin
@Composable
fun CustomShapedScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        // Concave header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = Color(0xFF6200EE),
                    shape = AppTheme.shapes.concaveTop.large
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Welcome",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge
            )
        }
        
        // Content with tab shapes
        Row(modifier = Modifier.padding(16.dp)) {
            repeat(3) { index ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .padding(horizontal = 4.dp),
                    shape = AppTheme.shapes.tab.small,
                    color = if (index == 0) Color(0xFF03DAC5) else Color.LightGray
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Tab ${index + 1}")
                    }
                }
            }
        }
        
        // Chat bubbles
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Left bubble
            Row {
                Surface(
                    shape = AppTheme.shapes.bubbleLeft.medium,
                    color = Color.LightGray
                ) {
                    Text(
                        text = "Hello!",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Right bubble
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = AppTheme.shapes.bubbleRight.medium,
                    color = Color(0xFF6200EE)
                ) {
                    Text(
                        text = "Hi there!",
                        modifier = Modifier.padding(12.dp),
                        color = Color.White
                    )
                }
            }
        }
        
        // Wave footer
        Spacer(modifier = Modifier.weight(1f))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    color = Color(0xFF018786),
                    shape = AppTheme.shapes.wave.medium
                )
        )
    }
}
```

---

## Migration Guide

If you were using `ConcaveTopShape` previously:

```kotlin
// Old
val shape = ConcaveTopShape(curveDepth = 0.15f, bottomCornerRadius = 16f)

// New - using predefined variant
val shape = AppTheme.shapes.concaveTop.medium

// New - custom with same parameters
val shape = ConcaveShape(
    position = ShapePosition.Top,
    curveDepth = 0.15f,
    cornerRadius = 16f
)
```

---

## API Reference

### Shape Position Enum
```kotlin
enum class ShapePosition {
    Top, Bottom, Left, Right
}
```

### Notch Style Enum
```kotlin
enum class NotchStyle {
    Rounded, Sharp
}
```

### Custom Shapes Data Class
```kotlin
@Immutable
data class CustomShapes(
    val extraSmall: Shape,
    val small: Shape,
    val medium: Shape,
    val large: Shape,
    val extraLarge: Shape
)
```

---

## Troubleshooting

**Issue**: Shapes not appearing correctly
- Ensure the container has explicit size
- Check that curveDepth/amplitude values are reasonable (0.0-1.0)

**Issue**: Performance issues with wave shapes
- Reduce `wavelength` parameter
- Use `remember` for frequently recomposed shapes

**Issue**: RTL not working for bubbles
- Ensure `LocalLayoutDirection` is properly set
- Check that Material theme is configured

---

## Additional Resources

- [Material Design Shape Guidelines](https://m3.material.io/styles/shape)
- [Compose Graphics Documentation](https://developer.android.com/jetpack/compose/graphics)
- PixaCompose Components Guide
- PixaCompose Quick Start Guide
