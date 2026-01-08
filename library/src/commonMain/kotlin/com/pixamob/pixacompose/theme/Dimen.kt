package com.pixamob.pixacompose.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Spacing Values - Based on 4dp grid system
object Spacing {
    val None = 0.dp
    val Micro = 2.dp
    val Tiny = 4.dp
    val ExtraSmall = 8.dp
    val Small = 12.dp
    val Medium = 16.dp
    val Large = 24.dp
    val ExtraLarge = 32.dp
    val Huge = 48.dp
    val Massive = 64.dp
    val SuperMassive = 80.dp
}

// Inset/Padding Values - For consistent content padding
object Inset {
    val Tiny = 4.dp
    val ExtraSmall = 8.dp
    val Small = 12.dp
    val Medium = 16.dp
    val Large = 20.dp
    val ExtraLarge = 24.dp
    val Huge = 32.dp
}

// Corner Radius Values
object RadiusSize {
    val None = 0.dp
    val Tiny = 2.dp     // Subtle rounding
    val ExtraSmall = 4.dp      // Small rounding
    val Small = 6.dp       // Default small
    val Medium = 8.dp       // Medium rounding (most common)
    val Large = 12.dp      // Large rounding
    val ExtraLarge = 16.dp     // Extra large rounding
    val Huge = 20.dp    // Huge rounding
    val VeryLarge = 24.dp   // Very rounded
    val Full = 9999.dp // Perfect circle/pill
}

// Icon Sizes - Standardized icon dimensions
object IconSize {
    val Tiny = 12.dp   // Tiny icons
    val VerySmall = 16.dp    // Very small icons
    val ExtraSmall = 18.dp     // Extra small icons
    val Small = 20.dp      // Small icons
    val Medium = 24.dp      // Medium icons (most common)
    val Large = 28.dp      // Large icons
    val ExtraLarge = 32.dp     // Extra large icons
    val Huge = 36.dp    // Huge icons
    val VeryLarge = 40.dp   // Very large icons
    val Massive = 48.dp  // Massive icons
}

// Component Sizes - Heights for buttons, inputs, etc.
object ComponentSize {
    val Minimal = 24.dp  // Minimal
    val Tiny = 28.dp   // Tiny
    val VerySmall = 32.dp    // Very small
    val ExtraSmall = 36.dp     // Extra small
    val Small = 40.dp      // Small (compact mode)
    val Medium = 44.dp      // Medium (touch-friendly)
    val Large = 48.dp      // Large (comfortable)
    val ExtraLarge = 56.dp     // Extra large
    val Huge = 64.dp    // Huge
    val VeryLarge = 72.dp   // Very large
    val Massive = 80.dp  // Massive
}

// Avatar Sizes - For profile pictures and avatars
object AvatarSize {
    val Tiny = 20.dp
    val ExtraSmall = 24.dp
    val Small = 32.dp
    val Medium = 40.dp
    val Large = 48.dp
    val ExtraLarge = 64.dp
    val Huge = 80.dp
    val VeryLarge = 96.dp
    val Massive = 120.dp
}

// Width Percentages - For responsive layouts
object WidthPercent {
    val Tiny = 0.20f   // 20% width
    val VerySmall = 0.25f    // 25% width
    val ExtraSmall = 0.33f     // 33% width
    val Small = 0.40f      // 40% width
    val Medium = 0.50f      // 50% width
    val Large = 0.60f      // 60% width
    val ExtraLarge = 0.66f     // 66% width
    val Huge = 0.75f    // 75% width
    val VeryLarge = 0.85f   // 85% width
    val Massive = 0.90f  // 90% width
    val Full = 1.0f    // 100% width
}

// Blur Radius Values - For backdrop effects
object BlurRadius {
    val None = 0.dp
    val Tiny = 4.dp
    val ExtraSmall = 8.dp
    val Small = 12.dp
    val Medium = 16.dp
    val Large = 24.dp
    val ExtraLarge = 32.dp
    val Huge = 48.dp
    val VeryLarge = 64.dp
}

// Shadow/Elevation Sizes
object ShadowSize {
    val None = 0.dp
    val Tiny = 0.5.dp
    val ExtraSmall = 1.dp
    val Small = 1.5.dp
    val Medium = 2.dp
    val Large = 3.dp
    val ExtraLarge = 4.dp
    val Huge = 6.dp
    val VeryLarge = 8.dp
}

// Border Sizes
object BorderSize {
    val None = 0.dp
    val Hairline = 0.5.dp  // Thinnest visible line
    val Tiny = 1.dp         // Default thin border
    val SlightlyThicker = 1.5.dp        // Slightly thicker
    val Standard = 2.dp           // Standard border
    val Medium = 2.5.dp         // Medium border
    val Thick = 3.dp           // Thick border
    val ExtraThick = 4.dp          // Extra thick
    val VeryThick = 5.dp         // Very thick
    val Massive = 6.dp        // Massive border
}

// Divider Sizes
object DividerSize {
    val Hairline = 0.5.dp
    val Thin = 1.dp
    val Default = 1.5.dp
    val Thick = 2.dp
    val ExtraThick = 4.dp
}

// Elevation/Shadow System - Material Design 3 inspired
enum class ElevationLevel(
    val default: Dp,
    val pressed: Dp,
    val hovered: Dp,
    val focused: Dp,
    val dragged: Dp,
    val disabled: Dp
) {
    Level0(
        default = 0.dp,
        pressed = 0.dp,
        hovered = 0.dp,
        focused = 0.dp,
        dragged = 0.dp,
        disabled = 0.dp
    ),
    Level1(
        default = 1.dp,
        pressed = 2.dp,
        hovered = 2.dp,
        focused = 2.dp,
        dragged = 4.dp,
        disabled = 0.dp
    ),
    Level2(
        default = 3.dp,
        pressed = 4.dp,
        hovered = 4.dp,
        focused = 4.dp,
        dragged = 6.dp,
        disabled = 0.dp
    ),
    Level3(
        default = 6.dp,
        pressed = 8.dp,
        hovered = 8.dp,
        focused = 8.dp,
        dragged = 12.dp,
        disabled = 0.dp
    ),
    Level4(
        default = 8.dp,
        pressed = 10.dp,
        hovered = 10.dp,
        focused = 10.dp,
        dragged = 16.dp,
        disabled = 0.dp
    ),
    Level5(
        default = 12.dp,
        pressed = 16.dp,
        hovered = 16.dp,
        focused = 16.dp,
        dragged = 24.dp,
        disabled = 0.dp
    )
}

// Animation Durations (in milliseconds)
object AnimationDuration {
    const val Instant = 0
    const val Fast = 100
    const val Quick = 150
    const val Normal = 200
    const val Medium = 300
    const val Slow = 400
    const val VerySlow = 500
    const val ExtraSlow = 700
}

// Z-Index/Layer Levels
object ZIndex {
    const val Background = 0
    const val Default = 1
    const val Raised = 2
    const val Dropdown = 10
    const val StickyHeader = 20
    const val Overlay = 30
    const val Modal = 40
    const val Popover = 50
    const val Tooltip = 60
    const val Toast = 70
    const val Maximum = 100
}

// Opacity Levels
object OpacityLevel {
    const val Invisible = 0.0f
    const val Subtle = 0.05f
    const val Light = 0.10f
    const val Medium = 0.20f
    const val SemiTransparent = 0.40f
    const val Translucent = 0.60f
    const val SemiOpaque = 0.80f
    const val AlmostOpaque = 0.90f
    const val Opaque = 1.0f

    // Disabled states
    const val Disabled = 0.38f
    const val DisabledContainer = 0.12f
}

// Max Width Constraints - For responsive design
object MaxWidth {
    val PhonePortrait = 360.dp    // Phone portrait
    val PhoneLandscape = 480.dp     // Phone landscape
    val SmallTablet = 600.dp     // Small tablet
    val LargeTablet = 840.dp     // Large tablet
    val DesktopSmall = 1024.dp   // Desktop small
    val DesktopMedium = 1280.dp  // Desktop medium
    val DesktopLarge = 1440.dp // Desktop large
    val DesktopExtraLarge = 1920.dp // Desktop extra large
}

// Touch Target Sizes - WCAG Accessibility
object TouchTarget {
    val Minimum = 44.dp     // Minimum accessible touch target
    val Comfortable = 48.dp // Comfortable touch target
    val Large = 56.dp       // Large touch target
}

// Line Heights - For dividers and separators
object LineHeight {
    val Thin = 1.dp
    val Default = 2.dp
    val Thick = 4.dp
}

enum class ComponentSizeVariant {
    ExtraSmall,
    Small,
    Medium,
    Large,
    ExtraLarge
}