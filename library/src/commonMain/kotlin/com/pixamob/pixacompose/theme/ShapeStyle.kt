package com.pixamob.pixacompose.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape

/**
 * Container for custom shape size variants.
 * Similar to Material3's Shapes but for custom shapes.
 */
@Immutable
data class CustomShapes(
    val extraSmall: Shape,
    val small: Shape,
    val medium: Shape,
    val large: Shape,
    val extraLarge: Shape
)

/**
 * Resolves a [Shape] from a 5-tier [CustomShapes] family using an 8-tier
 * [SizeVariant], so components can pull a themed shape straight from
 * `AppTheme.shapes.<family>.forVariant(size)` instead of separately
 * resolving `HierarchicalSize.Radius.forVariant(size)` and wrapping it in a
 * raw `RoundedCornerShape`/custom shape constructor themselves.
 *
 * Bucketing: [SizeVariant.None]/[SizeVariant.Nano] → extraSmall,
 * [SizeVariant.Compact] → small, [SizeVariant.Small]/[SizeVariant.Medium] → medium,
 * [SizeVariant.Large] → large, [SizeVariant.Huge]/[SizeVariant.Massive] → extraLarge.
 */
fun CustomShapes.forVariant(variant: SizeVariant): Shape = when (variant) {
    SizeVariant.None, SizeVariant.Nano -> extraSmall
    SizeVariant.Compact -> small
    SizeVariant.Small, SizeVariant.Medium -> medium
    SizeVariant.Large -> large
    SizeVariant.Huge, SizeVariant.Massive -> extraLarge
}

/**
 * Same bridge as [CustomShapes.forVariant] but for the Material3 [Shapes]
 * container used by [ShapeStyles.rounded]/[ShapeStyles.cut].
 */
fun Shapes.forVariant(variant: SizeVariant): Shape = when (variant) {
    SizeVariant.None, SizeVariant.Nano -> extraSmall
    SizeVariant.Compact -> small
    SizeVariant.Small, SizeVariant.Medium -> medium
    SizeVariant.Large -> large
    SizeVariant.Huge, SizeVariant.Massive -> extraLarge
}

// Standard rounded corner shapes
val roundedCornerShapes = Shapes(
    extraSmall = RoundedCornerShape(HierarchicalSize.Radius.Compact),
    small = RoundedCornerShape(HierarchicalSize.Radius.Small),
    medium = RoundedCornerShape(HierarchicalSize.Radius.Medium),
    large = RoundedCornerShape(HierarchicalSize.Radius.Large),
    extraLarge = RoundedCornerShape(HierarchicalSize.Radius.Huge)
)

// Cut corner shapes
val cutCornerShapes = Shapes(
    extraSmall = CutCornerShape(HierarchicalSize.Radius.Compact),
    small = CutCornerShape(HierarchicalSize.Radius.Small),
    medium = CutCornerShape(HierarchicalSize.Radius.Medium),
    large = CutCornerShape(HierarchicalSize.Radius.Large),
    extraLarge = CutCornerShape(HierarchicalSize.Radius.Huge)
)

// Concave shapes - curves dip inward
val concaveTopShapes = CustomShapes(
    extraSmall = ConcaveShape(position = ShapePosition.Top, curveDepth = 0.08f, cornerRadius = HierarchicalSize.Radius.Compact.value),
    small = ConcaveShape(position = ShapePosition.Top, curveDepth = 0.10f, cornerRadius = HierarchicalSize.Radius.Small.value),
    medium = ConcaveShape(position = ShapePosition.Top, curveDepth = 0.15f, cornerRadius = HierarchicalSize.Radius.Medium.value),
    large = ConcaveShape(position = ShapePosition.Top, curveDepth = 0.20f, cornerRadius = HierarchicalSize.Radius.Large.value),
    extraLarge = ConcaveShape(position = ShapePosition.Top, curveDepth = 0.25f, cornerRadius = HierarchicalSize.Radius.Huge.value)
)

val concaveBottomShapes = CustomShapes(
    extraSmall = ConcaveShape(position = ShapePosition.Bottom, curveDepth = 0.08f, cornerRadius = HierarchicalSize.Radius.Compact.value),
    small = ConcaveShape(position = ShapePosition.Bottom, curveDepth = 0.10f, cornerRadius = HierarchicalSize.Radius.Small.value),
    medium = ConcaveShape(position = ShapePosition.Bottom, curveDepth = 0.15f, cornerRadius = HierarchicalSize.Radius.Medium.value),
    large = ConcaveShape(position = ShapePosition.Bottom, curveDepth = 0.20f, cornerRadius = HierarchicalSize.Radius.Large.value),
    extraLarge = ConcaveShape(position = ShapePosition.Bottom, curveDepth = 0.25f, cornerRadius = HierarchicalSize.Radius.Huge.value)
)

// Convex shapes - curves bulge outward
val convexTopShapes = CustomShapes(
    extraSmall = ConvexShape(position = ShapePosition.Top, curveDepth = 0.10f, cornerRadius = HierarchicalSize.Radius.Compact.value),
    small = ConvexShape(position = ShapePosition.Top, curveDepth = 0.15f, cornerRadius = HierarchicalSize.Radius.Small.value),
    medium = ConvexShape(position = ShapePosition.Top, curveDepth = 0.20f, cornerRadius = HierarchicalSize.Radius.Medium.value),
    large = ConvexShape(position = ShapePosition.Top, curveDepth = 0.25f, cornerRadius = HierarchicalSize.Radius.Large.value),
    extraLarge = ConvexShape(position = ShapePosition.Top, curveDepth = 0.30f, cornerRadius = HierarchicalSize.Radius.Huge.value)
)

val convexBottomShapes = CustomShapes(
    extraSmall = ConvexShape(position = ShapePosition.Bottom, curveDepth = 0.10f, cornerRadius = HierarchicalSize.Radius.Compact.value),
    small = ConvexShape(position = ShapePosition.Bottom, curveDepth = 0.15f, cornerRadius = HierarchicalSize.Radius.Small.value),
    medium = ConvexShape(position = ShapePosition.Bottom, curveDepth = 0.20f, cornerRadius = HierarchicalSize.Radius.Medium.value),
    large = ConvexShape(position = ShapePosition.Bottom, curveDepth = 0.25f, cornerRadius = HierarchicalSize.Radius.Large.value),
    extraLarge = ConvexShape(position = ShapePosition.Bottom, curveDepth = 0.30f, cornerRadius = HierarchicalSize.Radius.Huge.value)
)

// Wave shapes - smooth wave patterns
val waveShapes = CustomShapes(
    extraSmall = WaveShape(position = ShapePosition.Bottom, wavelength = 2, amplitude = 0.05f, cornerRadius = HierarchicalSize.Radius.Compact.value),
    small = WaveShape(position = ShapePosition.Bottom, wavelength = 2, amplitude = 0.08f, cornerRadius = HierarchicalSize.Radius.Small.value),
    medium = WaveShape(position = ShapePosition.Bottom, wavelength = 2, amplitude = 0.10f, cornerRadius = HierarchicalSize.Radius.Medium.value),
    large = WaveShape(position = ShapePosition.Bottom, wavelength = 3, amplitude = 0.12f, cornerRadius = HierarchicalSize.Radius.Large.value),
    extraLarge = WaveShape(position = ShapePosition.Bottom, wavelength = 3, amplitude = 0.15f, cornerRadius = HierarchicalSize.Radius.Huge.value)
)

// Arch shapes - dome/arch curves
val archTopShapes = CustomShapes(
    extraSmall = ArchShape(position = ShapePosition.Top, curvature = 0.3f, cornerRadius = HierarchicalSize.Radius.Compact.value),
    small = ArchShape(position = ShapePosition.Top, curvature = 0.35f, cornerRadius = HierarchicalSize.Radius.Small.value),
    medium = ArchShape(position = ShapePosition.Top, curvature = 0.4f, cornerRadius = HierarchicalSize.Radius.Medium.value),
    large = ArchShape(position = ShapePosition.Top, curvature = 0.45f, cornerRadius = HierarchicalSize.Radius.Large.value),
    extraLarge = ArchShape(position = ShapePosition.Top, curvature = 0.5f, cornerRadius = HierarchicalSize.Radius.Huge.value)
)

val archBottomShapes = CustomShapes(
    extraSmall = ArchShape(position = ShapePosition.Bottom, curvature = 0.3f, cornerRadius = HierarchicalSize.Radius.Compact.value),
    small = ArchShape(position = ShapePosition.Bottom, curvature = 0.35f, cornerRadius = HierarchicalSize.Radius.Small.value),
    medium = ArchShape(position = ShapePosition.Bottom, curvature = 0.4f, cornerRadius = HierarchicalSize.Radius.Medium.value),
    large = ArchShape(position = ShapePosition.Bottom, curvature = 0.45f, cornerRadius = HierarchicalSize.Radius.Large.value),
    extraLarge = ArchShape(position = ShapePosition.Bottom, curvature = 0.5f, cornerRadius = HierarchicalSize.Radius.Huge.value)
)

// Tab shapes - like browser tabs
val tabShapes = CustomShapes(
    extraSmall = TabShape(position = ShapePosition.Top, tabWidthFraction = 0.9f, tabHeight = 30f, cornerRadius = HierarchicalSize.Radius.Compact.value),
    small = TabShape(position = ShapePosition.Top, tabWidthFraction = 0.85f, tabHeight = 35f, cornerRadius = HierarchicalSize.Radius.Small.value),
    medium = TabShape(position = ShapePosition.Top, tabWidthFraction = 0.8f, tabHeight = 40f, cornerRadius = HierarchicalSize.Radius.Medium.value),
    large = TabShape(position = ShapePosition.Top, tabWidthFraction = 0.75f, tabHeight = 45f, cornerRadius = HierarchicalSize.Radius.Large.value),
    extraLarge = TabShape(position = ShapePosition.Top, tabWidthFraction = 0.7f, tabHeight = 50f, cornerRadius = HierarchicalSize.Radius.Huge.value)
)

// Notch shapes - with cutouts
val notchRoundedShapes = CustomShapes(
    extraSmall = NotchShape(position = ShapePosition.Top, notchSize = 0.15f, style = NotchStyle.Rounded, cornerRadius = HierarchicalSize.Radius.Compact.value),
    small = NotchShape(position = ShapePosition.Top, notchSize = 0.18f, style = NotchStyle.Rounded, cornerRadius = HierarchicalSize.Radius.Small.value),
    medium = NotchShape(position = ShapePosition.Top, notchSize = 0.20f, style = NotchStyle.Rounded, cornerRadius = HierarchicalSize.Radius.Medium.value),
    large = NotchShape(position = ShapePosition.Top, notchSize = 0.22f, style = NotchStyle.Rounded, cornerRadius = HierarchicalSize.Radius.Large.value),
    extraLarge = NotchShape(position = ShapePosition.Top, notchSize = 0.25f, style = NotchStyle.Rounded, cornerRadius = HierarchicalSize.Radius.Huge.value)
)

val notchSharpShapes = CustomShapes(
    extraSmall = NotchShape(position = ShapePosition.Top, notchSize = 0.15f, style = NotchStyle.Sharp, cornerRadius = HierarchicalSize.Radius.Compact.value),
    small = NotchShape(position = ShapePosition.Top, notchSize = 0.18f, style = NotchStyle.Sharp, cornerRadius = HierarchicalSize.Radius.Small.value),
    medium = NotchShape(position = ShapePosition.Top, notchSize = 0.20f, style = NotchStyle.Sharp, cornerRadius = HierarchicalSize.Radius.Medium.value),
    large = NotchShape(position = ShapePosition.Top, notchSize = 0.22f, style = NotchStyle.Sharp, cornerRadius = HierarchicalSize.Radius.Large.value),
    extraLarge = NotchShape(position = ShapePosition.Top, notchSize = 0.25f, style = NotchStyle.Sharp, cornerRadius = HierarchicalSize.Radius.Huge.value)
)

// Bubble shapes - chat bubble with tail
val bubbleLeftShapes = CustomShapes(
    extraSmall = BubbleShape(tailPosition = ShapePosition.Left, tailSize = 8f, cornerRadius = HierarchicalSize.Radius.Compact.value, tailOffset = 0.8f),
    small = BubbleShape(tailPosition = ShapePosition.Left, tailSize = 10f, cornerRadius = HierarchicalSize.Radius.Small.value, tailOffset = 0.8f),
    medium = BubbleShape(tailPosition = ShapePosition.Left, tailSize = 12f, cornerRadius = HierarchicalSize.Radius.Medium.value, tailOffset = 0.8f),
    large = BubbleShape(tailPosition = ShapePosition.Left, tailSize = 14f, cornerRadius = HierarchicalSize.Radius.Large.value, tailOffset = 0.8f),
    extraLarge = BubbleShape(tailPosition = ShapePosition.Left, tailSize = 16f, cornerRadius = HierarchicalSize.Radius.Huge.value, tailOffset = 0.8f)
)

val bubbleRightShapes = CustomShapes(
    extraSmall = BubbleShape(tailPosition = ShapePosition.Right, tailSize = 8f, cornerRadius = HierarchicalSize.Radius.Compact.value, tailOffset = 0.8f),
    small = BubbleShape(tailPosition = ShapePosition.Right, tailSize = 10f, cornerRadius = HierarchicalSize.Radius.Small.value, tailOffset = 0.8f),
    medium = BubbleShape(tailPosition = ShapePosition.Right, tailSize = 12f, cornerRadius = HierarchicalSize.Radius.Medium.value, tailOffset = 0.8f),
    large = BubbleShape(tailPosition = ShapePosition.Right, tailSize = 14f, cornerRadius = HierarchicalSize.Radius.Large.value, tailOffset = 0.8f),
    extraLarge = BubbleShape(tailPosition = ShapePosition.Right, tailSize = 16f, cornerRadius = HierarchicalSize.Radius.Huge.value, tailOffset = 0.8f)
)

/**
 * Comprehensive shape styles for the theming system.
 *
 * Provides various shape families with size variants (extraSmall to extraLarge).
 * Each shape family supports specific use cases:
 *
 * - **rounded**: Standard rounded corners (most common) - Material3 Shapes
 * - **cut**: Angular cut corners - Material3 Shapes
 * - **concaveTop/Bottom**: Inward dipping curves - CustomShapes
 * - **convexTop/Bottom**: Outward bulging curves - CustomShapes
 * - **wave**: Smooth wave patterns - CustomShapes
 * - **archTop/Bottom**: Dome/arch curves - CustomShapes
 * - **tab**: Browser tab-like shapes - CustomShapes
 * - **notchRounded/Sharp**: Shapes with notch cutouts - CustomShapes
 * - **bubbleLeft/Right**: Chat bubble shapes with tails (RTL-aware) - CustomShapes
 *
 * Example usage:
 * ```
 * // Using in a component with Material3 Shapes
 * Card(shape = AppTheme.shapes.rounded.medium) { }
 *
 * // Custom concave shape
 * Surface(
 *     modifier = Modifier.background(
 *         color = Color.Blue,
 *         shape = AppTheme.shapes.concaveTop.large
 *     )
 * ) { }
 *
 * // Chat bubble
 * Box(
 *     modifier = Modifier.background(
 *         color = Color.Blue,
 *         shape = AppTheme.shapes.bubbleLeft.medium
 *     )
 * ) {
 *     Text("Hello!")
 * }
 * ```
 */
@Immutable
data class ShapeStyles(
    // Basic shapes (Material3 Shapes)
    val rounded: Shapes,
    val cut: Shapes,

    // Fully rounded pill/stadium shape. Size-invariant by design (a pill's
    // corner radius is always "as round as possible", not tied to a
    // SizeVariant tier) — use for chips, pill buttons, pill badges.
    val pill: Shape,

    // Concave shapes (inward curves) - CustomShapes
    val concaveTop: CustomShapes,
    val concaveBottom: CustomShapes,

    // Convex shapes (outward curves) - CustomShapes
    val convexTop: CustomShapes,
    val convexBottom: CustomShapes,

    // Wave shapes - CustomShapes
    val wave: CustomShapes,

    // Arch shapes (dome curves) - CustomShapes
    val archTop: CustomShapes,
    val archBottom: CustomShapes,

    // Tab shapes (browser-like tabs) - CustomShapes
    val tab: CustomShapes,

    // Notch shapes (with cutouts) - CustomShapes
    val notchRounded: CustomShapes,
    val notchSharp: CustomShapes,

    // Bubble shapes (chat bubbles with tails) - CustomShapes
    val bubbleLeft: CustomShapes,
    val bubbleRight: CustomShapes
)

/**
 * Default shape styles with sensible defaults for all shape families.
 */
val shapeStyles = ShapeStyles(
    rounded = roundedCornerShapes,
    cut = cutCornerShapes,
    pill = RoundedCornerShape(HierarchicalSize.Radius.Full),
    concaveTop = concaveTopShapes,
    concaveBottom = concaveBottomShapes,
    convexTop = convexTopShapes,
    convexBottom = convexBottomShapes,
    wave = waveShapes,
    archTop = archTopShapes,
    archBottom = archBottomShapes,
    tab = tabShapes,
    notchRounded = notchRoundedShapes,
    notchSharp = notchSharpShapes,
    bubbleLeft = bubbleLeftShapes,
    bubbleRight = bubbleRightShapes
)

/**
 * CompositionLocal for accessing shape styles throughout the app.
 *
 * Access via `AppTheme.shapes` in composables.
 */
val LocalShapeStyle = staticCompositionLocalOf { shapeStyles }
