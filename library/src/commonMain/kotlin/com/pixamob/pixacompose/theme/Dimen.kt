package com.pixamob.pixacompose.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Component Size Variants - Unified hierarchical system
 *
 * Consistent naming across all size objects:
 * - None: Zero size (0dp) - for spacing/padding only
 * - Nano: Micro UI elements (12-16dp) - badges, indicators
 * - Compact: Compact components (20-32dp) - chips, small buttons
 * - Small: Secondary actions (32-40dp) - supporting content
 * - Medium: Primary standard size (40-48dp) - default ⭐
 * - Large: Prominent actions (48-56dp) - accessibility-friendly
 * - Huge: Hero elements (56-64dp) - tablet optimized
 * - Massive: Desktop/marketing (64-80dp+) - large displays
 *
 * Note: Not all components support all variants - use what makes sense for each component family.
 */
enum class SizeVariant {
    None,          // 0dp - only for spacing/padding
    Nano,          // ~12-16dp range - micro elements
    Compact,       // ~20-32dp range - compact UI
    Small,         // ~32-40dp range - secondary
    Medium,        // ~40-48dp range - standard (default) ⭐
    Large,         // ~48-56dp range - prominent
    Huge,          // ~56-64dp range - hero
    Massive        // ~64-80dp+ range - desktop/marketing
}

/**
 * Unified Hierarchical Component Sizing System
 *
 * Design Principles:
 * 1. BASE UNIT: 4dp - all sizes are multiples of 4dp for pixel-perfect rendering
 * 2. VISUAL HIERARCHY: Icon + 2*Padding < Button < Container (proper nesting)
 * 3. TOUCH TARGETS: Always ≥ 48dp (WCAG accessibility standard)
 * 4. PROGRESSION: Each level feels noticeably (~15-25%) larger than previous
 * 5. CONSISTENCY: Related components use aligned sizes across variants
 *
 * Usage:
 * - Primary components: Use Container/Button/Input sizes as foundation
 * - Icons: Use Icon.forVariant() - automatically sized for parent
 * - Spacing: Use Padding/Spacing based on component density
 * - Effects: Use Radius/Border/Shadow for visual treatment
 *
 * All size categories support .forVariant(SizeVariant) helper for quick access.
 */
object HierarchicalSize {

    // ==========================================
    // CORE COMPONENT SIZES (Foundation)
    // ==========================================

    /**
     * Container Sizes - Foundation for cards, dialogs, bottom sheets
     * Touch-safe sizes that work well for interactive containers
     *
     * Visual relationship: Container = Icon + (2 × Padding.Large) + visual breathing room
     * Example: Medium = 24dp (icon) + 16dp (padding each side) = ~48-56dp range
     */
    object Container {
        val None = 0.dp            // No size (for conditional rendering)
        val Nano = 24.dp           // Micro containers (badges, indicators)
        val Compact = 32.dp        // Compact cards, small tiles
        val Small = 40.dp          // Secondary containers
        val Medium = 48.dp         // Standard container (WCAG touch minimum) ⭐
        val Large = 56.dp          // Prominent containers, list items
        val Huge = 64.dp           // Hero cards, featured content
        val Massive = 80.dp        // Desktop/tablet primary containers
        val DialogMaxWidth = 560.dp // Maximum width for dialog containers

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Button Sizes - Optimized for tap targets and text legibility
     * Slightly smaller than containers to feel "actionable"
     *
     * Note: Touch target is automatically expanded via TouchTarget object
     * Visual size can be smaller than 48dp if touch area is padded
     */
    object Button {
        val None = 0.dp            // No size
        val Nano = 24.dp           // Icon-only micro buttons
        val Compact = 32.dp        // Compact action buttons
        val Small = 36.dp          // Secondary buttons
        val Medium = 44.dp         // Standard button (visual) ⭐
        val Large = 48.dp          // Primary actions
        val Huge = 56.dp           // Hero CTAs
        val Massive = 64.dp        // Marketing/landing page buttons

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Icon Sizes - Comprehensive range from tiny indicators to hero graphics
     *
     * Hierarchy principle: Icon.Large + 2*Padding.Large ≤ Button.Large
     * Example: 24dp icon + 2*12dp padding = 48dp button ✓
     *
     * Choose icon size based on parent component:
     * - Button.Medium (44dp) → Icon.Medium (24dp)
     * - Button.Large (48dp) → Icon.Large (28dp)
     * - Container.Large (56dp) → Icon.Huge (40dp)
     */
    object Icon {
        val None = 0.dp            // No icon
        val Nano = 10.dp           // Micro indicators, inline icons
        val Compact = 14.dp        // Small badges, compact UI
        val Small = 18.dp          // Secondary icons, list item accessories
        val Medium = 24.dp         // Standard icons (Material Design base) ⭐
        val Large = 28.dp          // Prominent icons in buttons/cards
        val Huge = 36.dp           // Hero icons, primary actions
        val Massive = 48.dp        // Marketing, empty states, splash screens

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Input/TextField Sizes - Aligned with touch targets
     * Consistent with Button sizes for unified action rows
     */
    object Input {
        val None = 0.dp            // No size
        val Nano = 28.dp           // Inline edit fields (rare)
        val Compact = 32.dp        // Compact forms
        val Small = 40.dp          // Dense layouts
        val Medium = 48.dp         // Standard input (WCAG minimum) ⭐
        val Large = 56.dp          // Comfortable typing
        val Huge = 64.dp           // Prominent forms, search bars
        val Massive = 72.dp        // Hero search, landing pages

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Chip Sizes - Compact, pill-shaped UI elements
     * Smaller than buttons to feel less prominent
     */
    object Chip {
        val None = 0.dp            // No size
        val Nano = 20.dp           // Micro tags
        val Compact = 24.dp        // Compact chips
        val Small = 28.dp          // Standard tags
        val Medium = 32.dp         // Default chips (Material Design) ⭐
        val Large = 36.dp          // Prominent filters
        val Huge = 40.dp           // Large selection chips
        val Massive = 48.dp        // Hero category chips

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * List Item Sizes - Minimum heights for list rows
     * Must accommodate icon + text + padding comfortably
     */
    object ListItem {
        val None = 0.dp            // No size
        val Nano = 32.dp           // Ultra-compact lists (not recommended)
        val Compact = 40.dp        // Compact lists (density++)
        val Small = 48.dp          // Dense list items
        val Medium = 56.dp         // Standard list item (Material Design) ⭐
        val Large = 64.dp          // Comfortable lists
        val Huge = 72.dp           // Spacious lists, with avatars
        val Massive = 88.dp        // Hero list items, featured content

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Card Minimum Heights - For card-based layouts
     * Provides enough space for meaningful content
     */
    object Card {
        val None = 0.dp            // No minimum height
        val Nano = 64.dp           // Tiny info cards
        val Compact = 80.dp        // Compact cards
        val Small = 120.dp         // Small content cards
        val Medium = 160.dp        // Standard cards ⭐
        val Large = 200.dp         // Feature cards
        val Huge = 240.dp          // Hero cards
        val Massive = 320.dp       // Full-screen cards, marketing

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Avatar Sizes - Profile pictures, user representations
     * Circular or rounded square elements
     */
    object Avatar {
        val None = 0.dp            // No avatar
        val Nano = 16.dp           // Micro user indicators
        val Compact = 24.dp        // Tiny avatars (comments, badges)
        val Small = 32.dp          // Compact avatars (lists)
        val Medium = 40.dp         // Standard avatars ⭐
        val Large = 48.dp          // Prominent avatars
        val Huge = 64.dp           // Profile headers
        val Massive = 96.dp        // Hero profile images

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Badge Sizes - Notification dots, count indicators
     * Small by design to not overwhelm parent component
     */
    object Badge {
        val None = 0.dp            // No badge
        val Nano = 8.dp            // Notification dot (no text)
        val Compact = 14.dp        // Small count badges
        val Small = 16.dp          // Standard badges
        val Medium = 20.dp         // Badge with 2-digit numbers ⭐
        val Large = 24.dp          // Large badges
        val Huge = 28.dp           // Prominent badges
        val Massive = 32.dp        // Hero badges (rare)

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Toggle/Switch Sizes - Track height for switches
     */
    object Toggle {
        val None = 0.dp            // No size
        val Nano = 12.dp           // Micro switches
        val Compact = 16.dp        // Compact switches
        val Small = 20.dp          // Small switches
        val Medium = 24.dp         // Standard switch (Material Design) ⭐
        val Large = 28.dp          // Large switches
        val Huge = 32.dp           // Prominent switches
        val Massive = 40.dp        // Hero switches (accessibility)

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Checkbox/Radio Sizes - Box/circle size for selection controls
     */
    object Checkbox {
        val None = 0.dp            // No size
        val Nano = 12.dp           // Micro checkboxes
        val Compact = 16.dp        // Compact forms
        val Small = 18.dp          // Dense lists
        val Medium = 20.dp         // Standard checkbox (Material Design) ⭐
        val Large = 24.dp          // Comfortable selection
        val Huge = 28.dp           // Large, accessible checkboxes
        val Massive = 32.dp        // Hero checkboxes (rare)

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    // ==========================================
    // NAVIGATION COMPONENTS
    // ==========================================

    /**
     * App Bar Sizes - Top app bar, toolbar heights
     * Based on Material Design guidelines
     */
    object AppBar {
        val None = 0.dp            // No app bar
        val Nano = 40.dp           // Ultra-compact (not recommended)
        val Compact = 48.dp        // Compact app bar
        val Small = 52.dp          // Dense app bar
        val Medium = 56.dp         // Standard app bar (Material Design) ⭐
        val Large = 64.dp          // Comfortable app bar (default recommended)
        val Huge = 72.dp           // Prominent app bar
        val Massive = 96.dp        // Hero/marketing app bar, tablet

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Tab Sizes - Tab bar item heights
     */
    object Tab {
        val None = 0.dp            // No tab
        val Nano = 32.dp           // Compact tabs
        val Compact = 36.dp        // Dense tabs
        val Small = 40.dp          // Small tabs
        val Medium = 48.dp         // Standard tabs (Material Design) ⭐
        val Large = 56.dp          // Comfortable tabs
        val Huge = 64.dp           // Large tabs
        val Massive = 72.dp        // Hero tabs (tablet)

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Bottom Navigation Sizes - Bottom nav bar heights
     * Must be touch-friendly (≥48dp)
     */
    object BottomNav {
        val None = 0.dp            // No bottom nav
        val Nano = 48.dp           // Minimal (not recommended)
        val Compact = 48.dp        // Compact bottom nav
        val Small = 56.dp          // Dense bottom nav
        val Medium = 64.dp         // Standard bottom nav (recommended) ⭐
        val Large = 72.dp          // Comfortable bottom nav
        val Huge = 80.dp           // Prominent bottom nav
        val Massive = 96.dp        // Hero bottom nav (tablet)

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    // ==========================================
    // SPACING & LAYOUT
    // ==========================================

    /**
     * Spacing - External spacing (margins, gaps between components)
     *
     * Base progression: 0 → 2 → 4 → 8 → 12 → 16 → 24 → 32 → 48 → 64
     * Use for: Column/Row spacing, section gaps, screen margins
     */
    object Spacing {
        val None = 0.dp
        val Nano = 2.dp            // Tightest spacing
        val Compact = 4.dp         // Very tight spacing
        val Small = 8.dp           // Dense spacing
        val Medium = 12.dp         // Standard spacing ⭐
        val Large = 16.dp          // Comfortable spacing (default recommended)
        val Huge = 24.dp           // Spacious spacing
        val Massive = 48.dp        // Major sections, page margins

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Padding - Internal component padding
     *
     * Hierarchy: Padding.Large should leave room for icon + breathing space inside buttons
     * Example: Button.Medium (44dp) - 2*Padding.Medium (2*12dp=24dp) = 20dp for icon ✓
     */
    object Padding {
        val None = 0.dp
        val Nano = 2.dp            // Micro padding (chips, badges)
        val Compact = 4.dp         // Compact padding
        val Small = 8.dp           // Dense padding
        val Medium = 12.dp         // Standard padding (recommended default) ⭐
        val Large = 16.dp          // Comfortable padding
        val Huge = 20.dp           // Spacious padding
        val Massive = 24.dp        // Very spacious padding

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    // ==========================================
    // VISUAL EFFECTS (Fine-grained)
    // ==========================================

    /**
     * Corner Radius - Border radius for rounded corners
     *
     * More granular progression for precise visual control
     * Component recommendations:
     * - Buttons: Medium (8dp) or Large (12dp)
     * - Cards: Large (12dp) or Huge (16dp)
     * - Chips: Full (pill shape)
     * - Inputs: Small (6dp) or Medium (8dp)
     */
    object Radius {
        val None = 0.dp            // Square corners
        val Nano = 2.dp            // Subtle rounding
        val Compact = 4.dp         // Compact rounding
        val Small = 6.dp           // Gentle rounding
        val Medium = 8.dp          // Standard rounding (recommended default) ⭐
        val Large = 12.dp          // Comfortable rounding
        val Huge = 16.dp           // Prominent rounding
        val Massive = 24.dp        // Heavily rounded
        val Full = 9999.dp         // Perfect circle/pill

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Border/Stroke Widths - For outlines, frames
     *
     * Fine-grained for visual precision
     * Recommendation: Use Compact (1dp) as standard, Medium (2dp) for emphasis
     */
    object Border {
        val None = 0.dp
        val Nano = 0.5.dp          // Ultra-thin borders (dividers)
        val Compact = 1.dp         // Standard thin border (recommended default) ⭐
        val Small = 1.5.dp         // Slightly thicker
        val Medium = 2.dp          // Standard border
        val Large = 3.dp           // Prominent border
        val Huge = 4.dp            // Thick border
        val Massive = 6.dp         // Very thick border (decorative)

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Shadow/Elevation - Material Design elevation
     *
     * Use sparingly for depth perception
     */
    object Shadow {
        val None = 0.dp
        val Nano = 1.dp            // Subtle shadow
        val Compact = 2.dp         // Light elevation (cards at rest)
        val Small = 2.dp           // Standard elevation
        val Medium = 4.dp          // Moderate elevation (raised buttons) ⭐
        val Large = 8.dp           // High elevation (dialogs, menus)
        val Huge = 12.dp           // Very high elevation (modals)
        val Massive = 16.dp        // Maximum elevation

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    // ==========================================
    // ACCESSIBILITY & INTERACTION
    // ==========================================

    /**
     * Touch Target Sizes - WCAG 2.1 compliant touch targets
     *
     * CRITICAL: All interactive elements must have ≥48dp touch target
     * Visual element can be smaller if touch area is expanded via padding
     *
     * Recommendation: Use Small (48dp) as absolute minimum
     */
    object TouchTarget {
        val None = 0.dp            // No touch target
        val Nano = 40.dp           // Below WCAG (use only for dense professional tools)
        val Compact = 44.dp        // Slightly below WCAG (use with caution)
        val Small = 48.dp          // WCAG minimum (recommended minimum) ⭐
        val Medium = 48.dp         // Standard touch target
        val Large = 56.dp          // Comfortable touch target (recommended default)
        val Huge = 64.dp           // Spacious touch target
        val Massive = 72.dp        // Very comfortable (tablet, accessibility++)

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    // ==========================================
    // DECORATIVE ELEMENTS
    // ==========================================

    /**
     * Divider/Separator Thickness
     */
    object Divider {
        val None = 0.dp            // No divider
        val Nano = 0.5.dp          // Subtle divider
        val Compact = 1.dp         // Standard thin divider (recommended) ⭐
        val Small = 1.dp           // Standard divider
        val Medium = 2.dp          // Prominent divider
        val Large = 3.dp           // Thick divider
        val Huge = 4.dp            // Very thick divider
        val Massive = 6.dp         // Decorative divider

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Stroke Width - For drawn shapes, outlines, vector graphics
     */
    object Stroke {
        val None = 0.dp            // No stroke
        val Nano = 0.5.dp          // Thin strokes
        val Compact = 1.dp         // Standard thin stroke
        val Small = 2.dp           // Standard stroke (recommended) ⭐
        val Medium = 3.dp          // Medium stroke
        val Large = 4.dp           // Thick stroke
        val Huge = 6.dp            // Very thick stroke
        val Massive = 8.dp         // Decorative stroke

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    /**
     * Slider Track Sizes - Progress bar and slider track heights
     */
    object SliderTrack {
        val None = 0.dp            // No track
        val Nano = 2.dp            // Hairline track
        val Compact = 2.dp         // Thin track
        val Small = 3.dp           // Standard thin track
        val Medium = 4.dp          // Standard track (recommended) ⭐
        val Large = 6.dp           // Prominent track
        val Huge = 8.dp            // Thick track
        val Massive = 12.dp        // Very thick track (accessibility)

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }

    // ==========================================
    // IMAGE & MEDIA SIZES
    // ==========================================

    /**
     * Image Sizes - Predefined sizes for common image use cases
     * Use for thumbnails, previews, hero images
     */
    object Image {
        val None = 0.dp            // No image
        val Nano = 48.dp           // Tiny thumbnails
        val Compact = 64.dp        // Small thumbnails
        val Small = 80.dp          // List item thumbnails
        val Medium = 120.dp        // Medium images, gallery thumbnails ⭐
        val Large = 200.dp         // Large images, featured content
        val Huge = 320.dp          // Hero images, banners
        val Massive = 480.dp       // Full-width images

        fun forVariant(variant: SizeVariant): Dp = when (variant) {
            SizeVariant.None -> None
            SizeVariant.Nano -> Nano
            SizeVariant.Compact -> Compact
            SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large -> Large
            SizeVariant.Huge -> Huge
            SizeVariant.Massive -> Massive
        }
    }
}

// ============================================
// LEGACY ALIASES (Maintained for backward compatibility)
// ============================================
//
// These objects preserve the original API surface to avoid breaking existing code.
// New code should prefer HierarchicalSize.* directly for clarity.
//
// Migration Guide:
// - BorderSize.Thin → HierarchicalSize.Border.Compact
// - ComponentSize.Medium → HierarchicalSize.Container.Medium
// - IconSize.Medium → HierarchicalSize.Icon.Medium
// - RadiusSize.Medium → HierarchicalSize.Radius.Medium
// ============================================

object BorderSize {
    val None = HierarchicalSize.Border.None
    val Hairline = HierarchicalSize.Border.Nano
    val Thin = HierarchicalSize.Border.Compact
    val Tiny = HierarchicalSize.Border.Compact
    val Default = HierarchicalSize.Border.Small
    val Medium = HierarchicalSize.Border.Medium
    val Thick = HierarchicalSize.Border.Large
    val ExtraThick = HierarchicalSize.Border.Huge
    val SlightlyThicker = 2.5.dp  // Legacy custom value (not in hierarchy)
    val Standard = HierarchicalSize.Border.Medium
    val BorderWidth = HierarchicalSize.Border.Small
}

object ComponentSize {
    val Tiny = HierarchicalSize.Chip.Compact
    val VerySmall = HierarchicalSize.Container.Compact
    val ExtraSmall = HierarchicalSize.Button.Small
    val Small = HierarchicalSize.Container.Small
    val Medium = HierarchicalSize.Container.Medium
    val Large = HierarchicalSize.Container.Large
    val ExtraLarge = HierarchicalSize.Container.Huge
    val Huge = HierarchicalSize.ListItem.Huge
    val VeryLarge = HierarchicalSize.BottomNav.Huge
    val Minimal = 20.dp  // Legacy custom value
    val ButtonSmall = HierarchicalSize.Button.Small
    val ButtonMedium = HierarchicalSize.Button.Medium
    val InputSmall = HierarchicalSize.Input.Small
    val InputMedium = HierarchicalSize.Input.Medium
    val InputLarge = HierarchicalSize.Input.Large
    val ChipSmall = HierarchicalSize.Chip.Small
    val ChipMedium = HierarchicalSize.Chip.Medium
    val ChipLarge = HierarchicalSize.Chip.Large
    val AvatarSize = HierarchicalSize.Avatar.Medium
    val ImageSmall = HierarchicalSize.Image.Small
    val ImageMedium = HierarchicalSize.Image.Medium
    val DialogMinWidth = 280.dp  // Legacy specific value
    val DialogMaxWidth = 560.dp  // Legacy specific value
    val SnackbarSingleLine = 48.dp
    val SliderTrackMedium = HierarchicalSize.SliderTrack.Medium
    val SliderTrackLarge = HierarchicalSize.SliderTrack.Large
    val Inset = HierarchicalSize.Spacing.Large
    val Elevation = HierarchicalSize.Shadow.Small
}

object RadiusSize {
    val None = HierarchicalSize.Radius.None
    val Tiny = HierarchicalSize.Radius.Nano
    val ExtraSmall = HierarchicalSize.Radius.Compact
    val Small = HierarchicalSize.Radius.Small
    val Medium = HierarchicalSize.Radius.Medium
    val Large = HierarchicalSize.Radius.Large
    val ExtraLarge = HierarchicalSize.Radius.Huge
    val Huge = HierarchicalSize.Radius.Massive
    val VeryLarge = HierarchicalSize.Radius.Massive
    val Full = HierarchicalSize.Radius.Full
    val CornerRadius = HierarchicalSize.Radius.Medium
}

object IconSize {
    val Tiny = HierarchicalSize.Icon.Nano
    val VerySmall = HierarchicalSize.Icon.Compact
    val ExtraSmall = 18.dp  // Legacy custom value (between 16 and 20)
    val Small = HierarchicalSize.Icon.Small
    val Medium = HierarchicalSize.Icon.Medium
    val Large = HierarchicalSize.Icon.Large
    val ExtraLarge = HierarchicalSize.Icon.Huge
    val Huge = HierarchicalSize.Icon.Huge
    val VeryLarge = HierarchicalSize.Icon.Massive
}

object ShadowSize {
    val None = HierarchicalSize.Shadow.None
    val Tiny = HierarchicalSize.Shadow.Nano
    val Small = HierarchicalSize.Shadow.Small
    val Medium = HierarchicalSize.Shadow.Medium
    val Large = HierarchicalSize.Shadow.Large
    val ExtraLarge = HierarchicalSize.Shadow.Huge
    val VeryLarge = HierarchicalSize.Shadow.Massive
}

object Spacing {
    val None = HierarchicalSize.Spacing.None
    val Micro = HierarchicalSize.Spacing.Nano
    val Tiny = HierarchicalSize.Spacing.Compact
    val ExtraSmall = HierarchicalSize.Spacing.Compact
    val Small = HierarchicalSize.Spacing.Small
    val Medium = HierarchicalSize.Spacing.Medium
    val Large = HierarchicalSize.Spacing.Large
    val ExtraLarge = HierarchicalSize.Spacing.Huge
    val VeryLarge = HierarchicalSize.Spacing.Massive
    val Huge = HierarchicalSize.Spacing.Massive
    val Massive = HierarchicalSize.Spacing.Massive
}

object TouchTarget {
    val Minimum = HierarchicalSize.TouchTarget.Small  // 48dp WCAG minimum
    val Comfortable = HierarchicalSize.TouchTarget.Large
    val Spacious = HierarchicalSize.TouchTarget.Huge
}

object DividerSize {
    val Hairline = HierarchicalSize.Divider.Nano
    val Thin = HierarchicalSize.Divider.Compact
    val Default = HierarchicalSize.Divider.Small
    val Medium = HierarchicalSize.Divider.Medium
    val Thick = HierarchicalSize.Divider.Large
    val ExtraThick = HierarchicalSize.Divider.Huge
}

object StrokeSize {
    val Thin = HierarchicalSize.Stroke.Compact
    val Default = HierarchicalSize.Stroke.Small
    val Medium = HierarchicalSize.Stroke.Medium
    val Thick = HierarchicalSize.Stroke.Large
    val Standard = HierarchicalSize.Stroke.Medium
    val ExtraThick = HierarchicalSize.Stroke.Huge
}

// Standalone quick-access objects for commonly used properties
object BorderWidth {
    val Thin = HierarchicalSize.Border.Compact
    val Small = HierarchicalSize.Border.Small
    val Medium = HierarchicalSize.Border.Medium
    val Thick = HierarchicalSize.Border.Large
}

object CornerRadius {
    val Small = HierarchicalSize.Radius.Small
    val Medium = HierarchicalSize.Radius.Medium
    val Large = HierarchicalSize.Radius.Large
}

object Elevation {
    val Small = HierarchicalSize.Shadow.Small
    val Medium = HierarchicalSize.Shadow.Medium
    val Large = HierarchicalSize.Shadow.Large
}

// ============================================
// HELPER FUNCTIONS & DATA CLASSES
// ============================================

/**
 * Component Sizes Bundle - All related sizes for a given variant
 *
 * Use this when building compound components that need consistent sizing across all aspects.
 *
 * Example:
 * ```kotlin
 * val sizes = getSizesFor(SizeVariant.Large)
 * MyCard(
 *     modifier = Modifier.size(sizes.container),
 *     contentPadding = sizes.padding,
 *     cornerRadius = sizes.radius,
 *     elevation = sizes.shadow
 * ) {
 *     Icon(modifier = Modifier.size(sizes.icon))
 * }
 * ```
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
    val stroke: Dp,
    val image: Dp
)

/**
 * Get all coordinated sizes for a specific variant
 *
 * This ensures visual consistency across all aspects of a component hierarchy.
 * All values are mathematically aligned to work together harmoniously.
 *
 * @param variant The size variant to retrieve (None, Nano, Compact, Small, Medium, Large, Huge, Massive)
 * @return ComponentSizes bundle with all related dimensions
 */
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
    stroke = HierarchicalSize.Stroke.forVariant(variant),
    image = HierarchicalSize.Image.forVariant(variant)
)

// ============================================
// USAGE GUIDE & MIGRATION INSTRUCTIONS
// ============================================

/**
 * # HOW TO USE THE NEW HIERARCHICAL SIZE SYSTEM
 *
 * ## QUICK START
 *
 * ### 1. Using Size Variants (Recommended)
 * Components should accept a `SizeVariant` parameter and use `.forVariant()`:
 *
 * ```kotlin
 * @Composable
 * fun PixaButton(
 *     variant: SizeVariant = SizeVariant.Medium,
 *     modifier: Modifier = Modifier
 * ) {
 *     Button(
 *         modifier = modifier
 *             .height(HierarchicalSize.Button.forVariant(variant))
 *             .widthIn(min = HierarchicalSize.TouchTarget.forVariant(variant)),
 *         shape = RoundedCornerShape(HierarchicalSize.Radius.forVariant(variant)),
 *         contentPadding = PaddingValues(
 *             horizontal = HierarchicalSize.Padding.forVariant(variant),
 *             vertical = HierarchicalSize.Padding.forVariant(variant) / 2
 *         )
 *     ) {
 *         Icon(
 *             modifier = Modifier.size(HierarchicalSize.Icon.forVariant(variant))
 *         )
 *         Spacer(Modifier.width(HierarchicalSize.Spacing.forVariant(variant)))
 *         Text("Click Me")
 *     }
 * }
 * ```
 *
 * ### 2. Using Direct Sizes (For Fixed Designs)
 * When you need a specific size regardless of variant:
 *
 * ```kotlin
 * Icon(
 *     modifier = Modifier.size(HierarchicalSize.Icon.Medium),  // Always 24dp
 *     contentDescription = null
 * )
 *
 * Card(
 *     modifier = Modifier.padding(HierarchicalSize.Spacing.Large),  // Always 16dp
 *     shape = RoundedCornerShape(HierarchicalSize.Radius.Large)   // Always 12dp
 * )
 * ```
 *
 * ### 3. Using Legacy Aliases (During Migration)
 * Keep existing code working while you migrate:
 *
 * ```kotlin
 * // Old code (still works)
 * Icon(modifier = Modifier.size(IconSize.Medium))
 *
 * // New code (preferred)
 * Icon(modifier = Modifier.size(HierarchicalSize.Icon.Medium))
 * ```
 *
 * ## COMPONENT ADAPTATION EXAMPLES
 *
 * ### PixaIcon Component
 * ```kotlin
 * @Composable
 * fun PixaIcon(
 *     icon: ImageVector,
 *     variant: SizeVariant = SizeVariant.Medium,
 *     modifier: Modifier = Modifier
 * ) {
 *     Icon(
 *         imageVector = icon,
 *         contentDescription = null,
 *         modifier = modifier.size(HierarchicalSize.Icon.forVariant(variant))
 *     )
 * }
 *
 * // Usage
 * PixaIcon(Icons.Default.Home, variant = SizeVariant.Large)
 * PixaIcon(Icons.Default.Settings, variant = SizeVariant.Small)
 * ```
 *
 * ### PixaCard Component
 * ```kotlin
 * @Composable
 * fun PixaCard(
 *     variant: SizeVariant = SizeVariant.Medium,
 *     modifier: Modifier = Modifier,
 *     content: @Composable () -> Unit
 * ) {
 *     Card(
 *         modifier = modifier.heightIn(min = HierarchicalSize.Card.forVariant(variant)),
 *         shape = RoundedCornerShape(HierarchicalSize.Radius.forVariant(variant)),
 *         elevation = CardDefaults.cardElevation(
 *             defaultElevation = HierarchicalSize.Shadow.forVariant(variant)
 *         ),
 *         border = BorderStroke(
 *             width = HierarchicalSize.Border.forVariant(variant),
 *             color = Color.Gray
 *         )
 *     ) {
 *         Box(modifier = Modifier.padding(HierarchicalSize.Padding.forVariant(variant))) {
 *             content()
 *         }
 *     }
 * }
 * ```
 *
 * ### PixaListItem Component
 * ```kotlin
 * @Composable
 * fun PixaListItem(
 *     title: String,
 *     icon: ImageVector? = null,
 *     variant: SizeVariant = SizeVariant.Medium,
 *     onClick: () -> Unit
 * ) {
 *     val sizes = getSizesFor(variant)
 *
 *     Surface(
 *         onClick = onClick,
 *         modifier = Modifier
 *             .fillMaxWidth()
 *             .height(sizes.listItem)
 *     ) {
 *         Row(
 *             modifier = Modifier.padding(horizontal = sizes.padding),
 *             verticalAlignment = Alignment.CenterVertically,
 *             horizontalArrangement = Arrangement.spacedBy(sizes.spacing)
 *         ) {
 *             icon?.let {
 *                 Icon(
 *                     imageVector = it,
 *                     contentDescription = null,
 *                     modifier = Modifier.size(sizes.icon)
 *                 )
 *             }
 *             Text(title)
 *         }
 *     }
 * }
 * ```
 *
 * ### PixaImage Component
 * ```kotlin
 * @Composable
 * fun PixaImage(
 *     url: String,
 *     variant: SizeVariant = SizeVariant.Medium,
 *     modifier: Modifier = Modifier
 * ) {
 *     val size = HierarchicalSize.Image.forVariant(variant)
 *     val radius = HierarchicalSize.Radius.forVariant(variant)
 *
 *     AsyncImage(
 *         model = url,
 *         contentDescription = null,
 *         modifier = modifier
 *             .size(size)
 *             .clip(RoundedCornerShape(radius)),
 *         contentScale = ContentScale.Crop
 *     )
 * }
 * ```
 *
 * ## MIGRATION CHECKLIST
 *
 * ### Phase 1: Add Variant Support (Non-Breaking)
 * - [ ] Add optional `variant: SizeVariant = SizeVariant.Medium` parameter to components
 * - [ ] Replace hardcoded `48.dp` → `HierarchicalSize.Container.forVariant(variant)`
 * - [ ] Replace hardcoded `24.dp` → `HierarchicalSize.Icon.forVariant(variant)`
 * - [ ] Replace hardcoded `12.dp` → `HierarchicalSize.Padding.forVariant(variant)`
 * - [ ] Replace hardcoded `8.dp` → `HierarchicalSize.Radius.forVariant(variant)`
 * - [ ] Test all size variants: Nano, ExtraSmall, Small, Medium, Large, ExtraLarge, Huge
 *
 * ### Phase 2: Verify Visual Hierarchy
 * - [ ] Check: Icon.Large + 2*Padding.Large ≤ Button.Large ✓
 * - [ ] Check: Button.Large + Padding.Large ≤ Container.Large ✓
 * - [ ] Check: All touch targets ≥ 48dp (accessibility)
 * - [ ] Check: Spacing feels consistent across variants
 * - [ ] Check: Radius scales proportionally with component size
 *
 * ### Phase 3: Update Examples & Documentation
 * - [ ] Update README with new HierarchicalSize examples
 * - [ ] Add variant comparison screenshots (Nano → Huge)
 * - [ ] Document recommended variants per use case
 * - [ ] Create migration guide for v1 → v2
 *
 * ### Phase 4: Component Audit (Find & Replace)
 * Search codebase for hardcoded values and replace:
 *
 * ```kotlin
 * // Find: Modifier.size(48.dp)
 * // Replace with: Modifier.size(HierarchicalSize.Container.Medium)
 *
 * // Find: .padding(16.dp)
 * // Replace with: .padding(HierarchicalSize.Padding.Large)
 *
 * // Find: RoundedCornerShape(8.dp)
 * // Replace with: RoundedCornerShape(HierarchicalSize.Radius.Medium)
 *
 * // Find: Icon(modifier = Modifier.size(24.dp))
 * // Replace with: Icon(modifier = Modifier.size(HierarchicalSize.Icon.Medium))
 * ```
 *
 * ## SIZE VARIANT SELECTION GUIDE
 *
 * ### Nano (Micro UI)
 * - Notification dots (Badge.Nano = 8dp)
 * - Inline status indicators
 * - Dense data tables
 * - NOT recommended for primary interactive elements
 *
 * ### ExtraSmall (Compact)
 * - Compact mobile UI
 * - Secondary actions in toolbars
 * - Dense lists (email clients)
 * - Small chips/tags
 *
 * ### Small (Secondary)
 * - Secondary buttons
 * - Supporting icons
 * - Compact forms
 * - Mobile-first designs
 *
 * ### Medium (Standard) ⭐ DEFAULT
 * - Primary buttons
 * - Standard icons (24dp)
 * - Default forms
 * - Material Design baseline
 * - WCAG touch target minimum (48dp)
 *
 * ### Large (Prominent)
 * - Primary CTAs
 * - Featured content
 * - Comfortable touch targets
 * - Tablet layouts
 *
 * ### ExtraLarge (Hero)
 * - Marketing buttons
 * - Hero sections
 * - Tablet primary actions
 * - Featured cards
 *
 * ### Huge (Desktop/Marketing)
 * - Landing pages
 * - Desktop applications
 * - Kiosk interfaces
 * - Marketing/promotional UI
 *
 * ## VISUAL HIERARCHY VERIFICATION
 *
 * The system is designed so components nest properly:
 *
 * ```
 * Container.Large (56dp)
 *   ├─ Padding.Large (16dp × 2 = 32dp used)
 *   └─ Icon.Large (28dp) ✓ Fits with breathing room
 *
 * Button.Large (48dp)
 *   ├─ Padding.Medium (12dp × 2 = 24dp used)
 *   └─ Icon.Medium (24dp) ✓ Fits perfectly
 *
 * ListItem.Medium (56dp)
 *   ├─ Padding.Medium (12dp × 2 = 24dp vertical used)
 *   ├─ Icon.Medium (24dp) ✓
 *   └─ Spacing.Small (8dp gap) ✓
 * ```
 *
 * ## ACCESSIBILITY NOTES
 *
 * - All touch targets automatically expand to ≥48dp using TouchTarget sizes
 * - Medium variant (default) meets WCAG 2.1 Level AA
 * - Large variant recommended for accessibility-enhanced apps (Level AAA)
 * - Never use Nano or ExtraSmall for primary interactive elements
 *
 * ## TROUBLESHOOTING
 *
 * ### "Icon is too large for button"
 * → Use one variant smaller for icon: Button.Large + Icon.Medium
 *
 * ### "Padding feels cramped"
 * → Use one variant larger for padding: Button.Medium + Padding.Large
 *
 * ### "Touch target too small"
 * → Always use TouchTarget.forVariant(), never Button/Container size for touch area
 *
 * ### "Need size between Medium and Large"
 * → Consider using Padding/Spacing to adjust visual weight instead of adding new variants
 *
 * ## RESOURCES
 *
 * - Material Design 3 Size Guidelines: https://m3.material.io/foundations/layout/applying-layout/window-size-classes
 * - WCAG 2.1 Touch Target: https://www.w3.org/WAI/WCAG21/Understanding/target-size.html
 * - iOS Human Interface Guidelines: https://developer.apple.com/design/human-interface-guidelines/layout
 */

