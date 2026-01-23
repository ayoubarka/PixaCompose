package com.pixamob.pixacompose.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Component Size Variants - Hierarchical system
enum class SizeVariant {
    ExtraSmall,
    Small,
    Medium,
    Large,
    ExtraLarge
}

/**
 * Unified hierarchical component sizing system
 * All size categories follow consistent naming: ExtraSmall, Small, Medium, Large, ExtraLarge
 * Additional granular sizes available where needed (None, Tiny, Hairline, Huge, VeryLarge, Full)
 */
object HierarchicalSize {

    // Base Container Sizes - Foundation for all components
    object Container {
        val ExtraSmall = 32.dp
        val Small = 40.dp
        val Medium = 48.dp
        val Large = 56.dp
        val ExtraLarge = 64.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Button Sizes - Aligned with container hierarchy
    object Button {
        val ExtraSmall = 32.dp
        val Small = 36.dp
        val Medium = 44.dp
        val Large = 48.dp
        val ExtraLarge = 56.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Icon Sizes - Proportional to container
    object Icon {
        val Tiny = 12.dp
        val ExtraSmall = 16.dp
        val Small = 20.dp
        val Medium = 24.dp
        val Large = 28.dp
        val ExtraLarge = 32.dp
        val Huge = 36.dp
        val VeryLarge = 48.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Text Field / Input Sizes
    object Input {
        val ExtraSmall = 32.dp
        val Small = 40.dp
        val Medium = 48.dp
        val Large = 56.dp
        val ExtraLarge = 64.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Chip Sizes
    object Chip {
        val ExtraSmall = 24.dp
        val Small = 28.dp
        val Medium = 32.dp
        val Large = 36.dp
        val ExtraLarge = 40.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // List Item Sizes
    object ListItem {
        val ExtraSmall = 40.dp
        val Small = 48.dp
        val Medium = 56.dp
        val Large = 64.dp
        val ExtraLarge = 72.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Card Minimum Heights
    object Card {
        val ExtraSmall = 80.dp
        val Small = 120.dp
        val Medium = 160.dp
        val Large = 200.dp
        val ExtraLarge = 240.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Avatar Sizes
    object Avatar {
        val ExtraSmall = 24.dp
        val Small = 32.dp
        val Medium = 40.dp
        val Large = 48.dp
        val ExtraLarge = 64.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Badge Sizes
    object Badge {
        val ExtraSmall = 14.dp
        val Small = 16.dp
        val Medium = 20.dp
        val Large = 24.dp
        val ExtraLarge = 28.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Toggle/Switch Sizes
    object Toggle {
        val ExtraSmall = 16.dp
        val Small = 20.dp
        val Medium = 24.dp
        val Large = 28.dp
        val ExtraLarge = 32.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Checkbox/Radio Sizes
    object Checkbox {
        val ExtraSmall = 16.dp
        val Small = 18.dp
        val Medium = 20.dp
        val Large = 24.dp
        val ExtraLarge = 28.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // App Bar Sizes
    object AppBar {
        val ExtraSmall = 48.dp
        val Small = 52.dp
        val Medium = 56.dp
        val Large = 64.dp
        val ExtraLarge = 72.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Tab Sizes
    object Tab {
        val ExtraSmall = 36.dp
        val Small = 40.dp
        val Medium = 48.dp
        val Large = 56.dp
        val ExtraLarge = 64.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Bottom Navigation Sizes
    object BottomNav {
        val ExtraSmall = 48.dp
        val Small = 56.dp
        val Medium = 64.dp
        val Large = 72.dp
        val ExtraLarge = 80.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Spacing - For margins, padding, gaps
    object Spacing {
        val None = 0.dp
        val Micro = 1.dp
        val Tiny = 2.dp
        val ExtraSmall = 4.dp
        val Small = 8.dp
        val Medium = 12.dp
        val Large = 16.dp
        val ExtraLarge = 24.dp
        val Huge = 32.dp
        val VeryLarge = 40.dp
        val Massive = 48.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Padding - Internal component padding
    object Padding {
        val ExtraSmall = 4.dp
        val Small = 8.dp
        val Medium = 12.dp
        val Large = 16.dp
        val ExtraLarge = 20.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Corner Radius
    object Radius {
        val None = 0.dp
        val Tiny = 2.dp
        val ExtraSmall = 4.dp
        val Small = 6.dp
        val Medium = 8.dp
        val Large = 12.dp
        val ExtraLarge = 16.dp
        val Huge = 20.dp
        val VeryLarge = 24.dp
        val Full = 9999.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Border/Stroke Widths
    object Border {
        val None = 0.dp
        val Hairline = 0.5.dp
        val ExtraSmall = 1.dp
        val Small = 1.5.dp
        val Medium = 2.dp
        val Large = 3.dp
        val ExtraLarge = 4.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Shadow/Elevation
    object Shadow {
        val None = 0.dp
        val Tiny = 1.dp
        val ExtraSmall = 2.dp
        val Small = 2.dp
        val Medium = 4.dp
        val Large = 8.dp
        val ExtraLarge = 12.dp
        val Huge = 16.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Touch Target Sizes (Accessibility - WCAG compliant)
    object TouchTarget {
        val ExtraSmall = 40.dp
        val Small = 44.dp
        val Medium = 48.dp  // WCAG minimum
        val Large = 56.dp
        val ExtraLarge = 64.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Divider/Separator Thickness
    object Divider {
        val Hairline = 0.5.dp
        val ExtraSmall = 1.dp
        val Small = 1.dp
        val Medium = 2.dp
        val Large = 3.dp
        val ExtraLarge = 4.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Stroke Width (for shapes and outlines)
    object Stroke {
        val ExtraSmall = 1.dp
        val Small = 2.dp
        val Medium = 3.dp
        val Large = 4.dp
        val ExtraLarge = 6.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }

    // Slider Track Sizes
    object SliderTrack {
        val ExtraSmall = 2.dp
        val Small = 3.dp
        val Medium = 4.dp
        val Large = 6.dp
        val ExtraLarge = 8.dp

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.ExtraSmall -> ExtraSmall
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.ExtraLarge -> ExtraLarge
        }
    }
}

// ============================================
// Legacy Aliases (Deprecated - Use HierarchicalSize instead)
// ============================================

@Deprecated("Use HierarchicalSize.Border instead", ReplaceWith("HierarchicalSize.Border"))
object BorderSize {
    val None = HierarchicalSize.Border.None
    val Hairline = HierarchicalSize.Border.Hairline
    val Thin = HierarchicalSize.Border.ExtraSmall
    val Default = HierarchicalSize.Border.Small
    val Medium = HierarchicalSize.Border.Medium
    val Thick = HierarchicalSize.Border.Large
    val ExtraThick = HierarchicalSize.Border.ExtraLarge
}

@Deprecated("Use HierarchicalSize.Container/Button/Chip instead", ReplaceWith("HierarchicalSize.Container"))
object ComponentSize {
    val Tiny = HierarchicalSize.Chip.ExtraSmall
    val VerySmall = HierarchicalSize.Container.ExtraSmall
    val ExtraSmall = HierarchicalSize.Button.Small
    val Small = HierarchicalSize.Container.Small
    val Medium = HierarchicalSize.Container.Medium
    val Large = HierarchicalSize.Container.Large
    val ExtraLarge = HierarchicalSize.Container.ExtraLarge
    val Huge = HierarchicalSize.ListItem.ExtraLarge
    val VeryLarge = HierarchicalSize.BottomNav.ExtraLarge
}

@Deprecated("Use HierarchicalSize.Radius instead", ReplaceWith("HierarchicalSize.Radius"))
object RadiusSize {
    val None = HierarchicalSize.Radius.None
    val Tiny = HierarchicalSize.Radius.Tiny
    val ExtraSmall = HierarchicalSize.Radius.ExtraSmall
    val Small = HierarchicalSize.Radius.Small
    val Medium = HierarchicalSize.Radius.Medium
    val Large = HierarchicalSize.Radius.Large
    val ExtraLarge = HierarchicalSize.Radius.ExtraLarge
    val Huge = HierarchicalSize.Radius.Huge
    val VeryLarge = HierarchicalSize.Radius.VeryLarge
    val Full = HierarchicalSize.Radius.Full
}

@Deprecated("Use HierarchicalSize.Icon instead", ReplaceWith("HierarchicalSize.Icon"))
object IconSize {
    val Tiny = HierarchicalSize.Icon.Tiny
    val VerySmall = HierarchicalSize.Icon.ExtraSmall
    val ExtraSmall = 18.dp
    val Small = HierarchicalSize.Icon.Small
    val Medium = HierarchicalSize.Icon.Medium
    val Large = HierarchicalSize.Icon.Large
    val ExtraLarge = HierarchicalSize.Icon.ExtraLarge
    val Huge = HierarchicalSize.Icon.Huge
    val VeryLarge = HierarchicalSize.Icon.VeryLarge
}

@Deprecated("Use HierarchicalSize.Shadow instead", ReplaceWith("HierarchicalSize.Shadow"))
object ShadowSize {
    val None = HierarchicalSize.Shadow.None
    val Tiny = HierarchicalSize.Shadow.Tiny
    val Small = HierarchicalSize.Shadow.Small
    val Medium = HierarchicalSize.Shadow.Medium
    val Large = HierarchicalSize.Shadow.Large
    val ExtraLarge = HierarchicalSize.Shadow.ExtraLarge
    val Huge = HierarchicalSize.Shadow.Huge
}

@Deprecated("Use HierarchicalSize.Spacing instead", ReplaceWith("HierarchicalSize.Spacing"))
object Spacing {
    val None = HierarchicalSize.Spacing.None
    val Micro = HierarchicalSize.Spacing.Micro
    val Tiny = HierarchicalSize.Spacing.Tiny
    val ExtraSmall = HierarchicalSize.Spacing.ExtraSmall
    val Small = HierarchicalSize.Spacing.Small
    val Medium = HierarchicalSize.Spacing.Medium
    val Default = HierarchicalSize.Spacing.Large
    val Large = 20.dp
    val ExtraLarge = HierarchicalSize.Spacing.ExtraLarge
    val Huge = HierarchicalSize.Spacing.Huge
    val VeryLarge = HierarchicalSize.Spacing.VeryLarge
    val Massive = HierarchicalSize.Spacing.Massive
}

@Deprecated("Use HierarchicalSize.TouchTarget instead", ReplaceWith("HierarchicalSize.TouchTarget"))
object TouchTarget {
    val Minimum = HierarchicalSize.TouchTarget.Medium
    val Comfortable = HierarchicalSize.TouchTarget.Large
    val Spacious = HierarchicalSize.TouchTarget.ExtraLarge
}

@Deprecated("Use HierarchicalSize.Divider instead", ReplaceWith("HierarchicalSize.Divider"))
object DividerSize {
    val Hairline = HierarchicalSize.Divider.Hairline
    val Thin = HierarchicalSize.Divider.ExtraSmall
    val Default = HierarchicalSize.Divider.Small
    val Medium = HierarchicalSize.Divider.Medium
    val Thick = HierarchicalSize.Divider.Large
}

@Deprecated("Use HierarchicalSize.Stroke instead", ReplaceWith("HierarchicalSize.Stroke"))
object StrokeSize {
    val Thin = HierarchicalSize.Stroke.ExtraSmall
    val Default = HierarchicalSize.Stroke.Small
    val Medium = HierarchicalSize.Stroke.Medium
    val Thick = HierarchicalSize.Stroke.Large
    val ExtraThick = HierarchicalSize.Stroke.ExtraLarge
}

/**
 * Helper function to get all sizes for a variant across components
 * Example: val sizes = getSizesFor(SizeVariant.Large)
 */
data class ComponentSizes(
    val container: Dp,
    val button: Dp,
    val icon: Dp,
    val input: Dp,
    val chip: Dp,
    val listItem: Dp,
    val card: Dp,
    val avatar: Dp,
    val badge: Dp,
    val spacing: Dp,
    val padding: Dp,
    val radius: Dp,
    val border: Dp,
    val shadow: Dp,
    val touchTarget: Dp,
    val divider: Dp,
    val stroke: Dp
)

fun getSizesFor(variant: SizeVariant) = ComponentSizes(
    container = HierarchicalSize.Container.forVariant(variant),
    button = HierarchicalSize.Button.forVariant(variant),
    icon = HierarchicalSize.Icon.forVariant(variant),
    input = HierarchicalSize.Input.forVariant(variant),
    chip = HierarchicalSize.Chip.forVariant(variant),
    listItem = HierarchicalSize.ListItem.forVariant(variant),
    card = HierarchicalSize.Card.forVariant(variant),
    avatar = HierarchicalSize.Avatar.forVariant(variant),
    badge = HierarchicalSize.Badge.forVariant(variant),
    spacing = HierarchicalSize.Spacing.forVariant(variant),
    padding = HierarchicalSize.Padding.forVariant(variant),
    radius = HierarchicalSize.Radius.forVariant(variant),
    border = HierarchicalSize.Border.forVariant(variant),
    shadow = HierarchicalSize.Shadow.forVariant(variant),
    touchTarget = HierarchicalSize.TouchTarget.forVariant(variant),
    divider = HierarchicalSize.Divider.forVariant(variant),
    stroke = HierarchicalSize.Stroke.forVariant(variant)
)
