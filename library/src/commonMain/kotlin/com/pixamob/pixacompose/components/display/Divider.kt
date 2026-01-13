package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.*

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Divider Orientation - Direction of the divider
 */
enum class DividerOrientation {
    /** Horizontal line (default for content separation) */
    Horizontal,
    /** Vertical line (for toolbar/sidebar separation) */
    Vertical
}

/**
 * Divider Variant - Visual emphasis level
 */
enum class DividerVariant {
    /** Subtle border - Minimal separation (List items, cards) */
    Subtle,
    /** Default border - Standard separation (Sections, groups) */
    Default,
    /** Strong border - Clear separation (Major sections) */
    Strong
}

/**
 * Divider Thickness - Line weight
 */
enum class DividerThickness {
    /** 0.5dp - Hairline (Very subtle) */
    Thin,
    /** 1dp - Standard line */
    Standard,
    /** 2dp - Bold line */
    Thick,
    /** 4dp - Heavy separator */
    Heavy
}

/**
 * Divider Colors
 */
@Immutable
@Stable
data class DividerColors(
    val line: Color
)

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get divider colors based on variant
 */
@Composable
private fun getDividerTheme(
    variant: DividerVariant,
    colors: ColorPalette
): DividerColors {
    return when (variant) {
        DividerVariant.Subtle -> DividerColors(
            line = colors.baseBorderSubtle
        )
        DividerVariant.Default -> DividerColors(
            line = colors.baseBorderDefault
        )
        DividerVariant.Strong -> DividerColors(
            line = colors.baseBorderFocus
        )
    }
}

/**
 * Get thickness in Dp
 */
private fun getDividerThicknessDp(thickness: DividerThickness): Dp {
    return when (thickness) {
        DividerThickness.Thin -> DividerSize.Hairline
        DividerThickness.Standard -> DividerSize.Thin
        DividerThickness.Thick -> DividerSize.Thick
        DividerThickness.Heavy -> DividerSize.ExtraThick
    }
}

// ============================================================================
// BASE COMPONENT (Internal)
// ============================================================================

/**
 * Base Divider implementation
 */
@Composable
private fun PixaDivider(
    modifier: Modifier = Modifier,
    orientation: DividerOrientation,
    thickness: Dp,
    colors: DividerColors
) {
    val dividerModifier = when (orientation) {
        DividerOrientation.Horizontal -> modifier
            .fillMaxWidth()
            .height(thickness)
        DividerOrientation.Vertical -> modifier
            .fillMaxHeight()
            .width(thickness)
    }

    Box(
        modifier = dividerModifier.background(colors.line)
    )
}

// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * Divider - Visual separator between content
 *
 * A simple line component for separating content sections horizontally or vertically.
 * Commonly used in lists, cards, toolbars, and layouts to create visual hierarchy.
 *
 * @param modifier Modifier for the divider
 * @param orientation Direction of the divider (Horizontal or Vertical)
 * @param variant Visual emphasis level (Subtle, Default, Strong)
 * @param thickness Line weight (Thin, Standard, Thick, Heavy)
 * @param color Optional custom color (overrides variant color)
 *
 * @sample
 * ```
 * // Horizontal divider (most common)
 * Divider()
 *
 * // Subtle divider for list items
 * Divider(variant = DividerVariant.Subtle)
 *
 * // Strong divider for major sections
 * Divider(variant = DividerVariant.Strong, thickness = DividerThickness.Thick)
 *
 * // Vertical divider in toolbar
 * Divider(orientation = DividerOrientation.Vertical)
 *
 * // Custom color divider
 * Divider(color = Color.Red)
 * ```
 */
@Composable
fun Divider(
    modifier: Modifier = Modifier,
    orientation: DividerOrientation = DividerOrientation.Horizontal,
    variant: DividerVariant = DividerVariant.Default,
    thickness: DividerThickness = DividerThickness.Standard,
    color: Color? = null
) {
    val themeColors = getDividerTheme(variant, AppTheme.colors)
    val finalColors = color?.let { DividerColors(it) } ?: themeColors
    val thicknessDp = getDividerThicknessDp(thickness)

    PixaDivider(
        modifier = modifier,
        orientation = orientation,
        thickness = thicknessDp,
        colors = finalColors
    )
}

// ============================================================================
// CONVENIENCE VARIANTS
// ============================================================================

/**
 * Horizontal Divider - Explicit horizontal separator
 */
@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    variant: DividerVariant = DividerVariant.Default,
    thickness: DividerThickness = DividerThickness.Standard,
    color: Color? = null
) {
    Divider(
        modifier = modifier,
        orientation = DividerOrientation.Horizontal,
        variant = variant,
        thickness = thickness,
        color = color
    )
}

/**
 * Vertical Divider - Explicit vertical separator
 */
@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    variant: DividerVariant = DividerVariant.Default,
    thickness: DividerThickness = DividerThickness.Standard,
    color: Color? = null
) {
    Divider(
        modifier = modifier,
        orientation = DividerOrientation.Vertical,
        variant = variant,
        thickness = thickness,
        color = color
    )
}

/**
 * Subtle Divider - Minimal visual separation
 */
@Composable
fun SubtleDivider(
    modifier: Modifier = Modifier,
    orientation: DividerOrientation = DividerOrientation.Horizontal,
    thickness: DividerThickness = DividerThickness.Thin
) {
    Divider(
        modifier = modifier,
        orientation = orientation,
        variant = DividerVariant.Subtle,
        thickness = thickness
    )
}

/**
 * Strong Divider - Clear visual separation
 */
@Composable
fun StrongDivider(
    modifier: Modifier = Modifier,
    orientation: DividerOrientation = DividerOrientation.Horizontal,
    thickness: DividerThickness = DividerThickness.Thick
) {
    Divider(
        modifier = modifier,
        orientation = orientation,
        variant = DividerVariant.Strong,
        thickness = thickness
    )
}

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * 1. Basic horizontal divider:
 * ```
 * Column {
 *     Text("Section 1")
 *     Divider()
 *     Text("Section 2")
 * }
 * ```
 *
 * 2. Subtle divider in list:
 * ```
 * LazyColumn {
 *     items(items) { item ->
 *         ListItem(item)
 *         SubtleDivider()
 *     }
 * }
 * ```
 *
 * 3. Strong section divider:
 * ```
 * Column {
 *     HeaderSection()
 *     StrongDivider(thickness = DividerThickness.Heavy)
 *     ContentSection()
 * }
 * ```
 *
 * 4. Vertical toolbar divider:
 * ```
 * Row {
 *     IconButton(onClick = {})
 *     VerticalDivider(modifier = Modifier.height(24.dp))
 *     IconButton(onClick = {})
 * }
 * ```
 *
 * 5. Custom colored divider:
 * ```
 * Divider(
 *     color = MaterialTheme.colorScheme.primary,
 *     thickness = DividerThickness.Thick
 * )
 * ```
 *
 * 6. Sidebar vertical divider:
 * ```
 * Row {
 *     Sidebar()
 *     VerticalDivider(
 *         modifier = Modifier.fillMaxHeight(),
 *         variant = DividerVariant.Default
 *     )
 *     MainContent()
 * }
 * ```
 */
