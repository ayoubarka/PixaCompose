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

// Component Sizes - Comprehensive sizing system for all UI components
object ComponentSize {
    // Base Component Sizes
    val Minimal = 24.dp       // Minimal height for compact components
    val Tiny = 28.dp          // Tiny components
    val VerySmall = 32.dp     // Very small components
    val ExtraSmall = 36.dp    // Extra small components
    val Small = 40.dp         // Small (compact mode)
    val Medium = 44.dp        // Medium (touch-friendly default)
    val Large = 48.dp         // Large (comfortable)
    val ExtraLarge = 56.dp    // Extra large
    val Huge = 64.dp          // Huge
    val VeryLarge = 72.dp     // Very large
    val Massive = 80.dp       // Massive

    // Button Sizes
    val ButtonSmall = 36.dp   // Small button height
    val ButtonMedium = 44.dp  // Medium button height (default)
    val ButtonLarge = 52.dp   // Large button height

    // Input Field Sizes (TextField, TextArea, etc.)
    val InputSmall = 36.dp    // Small text input height
    val InputMedium = 44.dp   // Medium text input height (default)
    val InputLarge = 52.dp    // Large text input height

    // Chip Sizes
    val ChipSmall = 24.dp     // Small chip height
    val ChipMedium = 32.dp    // Medium chip height (default)
    val ChipLarge = 40.dp     // Large chip height

    // Badge Sizes
    val BadgeSmall = 16.dp    // Small badge size
    val BadgeMedium = 20.dp   // Medium badge size
    val BadgeLarge = 24.dp    // Large badge size

    // Toggle/Switch Sizes
    val ToggleSmall = 20.dp   // Small toggle height
    val ToggleMedium = 24.dp  // Medium toggle height
    val ToggleLarge = 28.dp   // Large toggle height

    // Checkbox/Radio Sizes
    val CheckboxSmall = 18.dp   // Small checkbox size
    val CheckboxMedium = 20.dp  // Medium checkbox size
    val CheckboxLarge = 24.dp   // Large checkbox size

    // Slider Sizes
    val SliderTrackSmall = 2.dp    // Small slider track height
    val SliderTrackMedium = 4.dp   // Medium slider track height
    val SliderTrackLarge = 6.dp    // Large slider track height
    val SliderThumbSmall = 16.dp   // Small slider thumb size
    val SliderThumbMedium = 20.dp  // Medium slider thumb size
    val SliderThumbLarge = 24.dp   // Large slider thumb size

    // Progress Indicator Sizes
    val ProgressSmall = 2.dp     // Small progress bar height
    val ProgressMedium = 4.dp    // Medium progress bar height
    val ProgressLarge = 8.dp     // Large progress bar height
    val ProgressCircularSmall = 16.dp   // Small circular progress diameter
    val ProgressCircularMedium = 24.dp  // Medium circular progress diameter
    val ProgressCircularLarge = 32.dp   // Large circular progress diameter

    // Card Sizes (min heights)
    val CardSmall = 120.dp    // Small card minimum height
    val CardMedium = 160.dp   // Medium card minimum height
    val CardLarge = 200.dp    // Large card minimum height

    // List Item Sizes
    val ListItemSmall = 40.dp     // Small list item height
    val ListItemMedium = 56.dp    // Medium list item height (default)
    val ListItemLarge = 72.dp     // Large list item height
    val ListItemExtraLarge = 88.dp // Extra large list item height

    // Tab Sizes
    val TabSmall = 36.dp      // Small tab height
    val TabMedium = 48.dp     // Medium tab height (default)
    val TabLarge = 56.dp      // Large tab height

    // Bottom Navigation Sizes
    val BottomNavSmall = 56.dp    // Small bottom nav height
    val BottomNavMedium = 64.dp   // Medium bottom nav height
    val BottomNavLarge = 72.dp    // Large bottom nav height

    // Top App Bar Sizes
    val AppBarSmall = 48.dp       // Small app bar height
    val AppBarMedium = 56.dp      // Medium app bar height (default)
    val AppBarLarge = 64.dp       // Large app bar height
    val AppBarExtraLarge = 72.dp  // Extra large app bar height

    // Dialog Sizes
    val DialogMinWidth = 280.dp   // Minimum dialog width
    val DialogMaxWidth = 560.dp   // Maximum dialog width
    val DialogMinHeight = 120.dp  // Minimum dialog height

    // Bottom Sheet Sizes
    val BottomSheetPeek = 56.dp   // Bottom sheet peek height
    val BottomSheetSmall = 200.dp // Small bottom sheet height
    val BottomSheetMedium = 400.dp // Medium bottom sheet height
    val BottomSheetLarge = 600.dp  // Large bottom sheet height

    // Snackbar Sizes
    val SnackbarSingleLine = 48.dp  // Single-line snackbar height
    val SnackbarMultiLine = 68.dp   // Multi-line snackbar height

    // Tooltip Sizes
    val TooltipMinHeight = 24.dp  // Minimum tooltip height
    val TooltipMaxWidth = 200.dp  // Maximum tooltip width

    // Menu Item Sizes
    val MenuItemSmall = 32.dp     // Small menu item height
    val MenuItemMedium = 48.dp    // Medium menu item height (default)
    val MenuItemLarge = 56.dp     // Large menu item height

    // Divider Sizes (heights for horizontal, widths for vertical)
    val DividerThin = 1.dp        // Thin divider
    val DividerMedium = 2.dp      // Medium divider
    val DividerThick = 4.dp       // Thick divider

    // Image Placeholder Sizes
    val ImageSmall = 80.dp        // Small image placeholder
    val ImageMedium = 120.dp      // Medium image placeholder
    val ImageLarge = 200.dp       // Large image placeholder
    val ImageExtraLarge = 300.dp  // Extra large image placeholder
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

// Border Width - For borders and outlines
object BorderWidth {
    val None = 0.dp
    val Hairline = 0.5.dp
    val Thin = 1.dp
    val Medium = 1.5.dp
    val Thick = 2.dp
    val ExtraThick = 3.dp
}

// Corner Radius - For rounded corners
object CornerRadius {
    val None = 0.dp
    val Small = 6.dp
    val Medium = 8.dp
    val Large = 12.dp
    val ExtraLarge = 16.dp
    val Full = 9999.dp
}

// Elevation - For shadows and elevation
object Elevation {
    val None = 0.dp
    val Small = 2.dp
    val Medium = 4.dp
    val Large = 8.dp
    val ExtraLarge = 16.dp
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