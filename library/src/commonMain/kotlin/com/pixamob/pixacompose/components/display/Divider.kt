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
private fun PixaDividerImpl(
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
 * PixaDivider - Visual separator between content
 *
 * A simple line component for separating content sections horizontally or vertically.
 * Commonly used in lists, cards, toolbars, and layouts to create visual hierarchy.
 *
 * @param modifier Modifier for the divider
 * @param orientation Direction of the divider (Horizontal or Vertical)
 * @param thickness Line weight (Thin, Standard, Thick, Heavy)
 * @param color Optional custom color (overrides default color)
 *
 * @sample
 * ```
 * // Horizontal divider (most common)
 * PixaDivider()
 *
 * // Vertical divider in toolbar
 * PixaDivider(orientation = DividerOrientation.Vertical)
 *
 * // Custom thickness
 * PixaDivider(thickness = DividerThickness.Heavy)
 *
 * // Custom color divider
 * PixaDivider(color = Color.Red)
 * ```
 */
@Composable
fun PixaDivider(
    modifier: Modifier = Modifier,
    orientation: DividerOrientation = DividerOrientation.Horizontal,
    thickness: DividerThickness = DividerThickness.Standard,
    color: Color? = null
) {
    val defaultColor = AppTheme.colors.baseBorderDefault
    val finalColor = color ?: defaultColor
    val finalColors = DividerColors(finalColor)
    val thicknessDp = getDividerThicknessDp(thickness)

    PixaDividerImpl(
        modifier = modifier,
        orientation = orientation,
        thickness = thicknessDp,
        colors = finalColors
    )
}

// ============================================================================
// CONVENIENCE FUNCTIONS
// ============================================================================

/**
 * Horizontal Divider - Explicit horizontal separator
 */
@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: DividerThickness = DividerThickness.Standard,
    color: Color? = null
) {
    PixaDivider(
        modifier = modifier,
        orientation = DividerOrientation.Horizontal,
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
    thickness: DividerThickness = DividerThickness.Standard,
    color: Color? = null
) {
    PixaDivider(
        modifier = modifier,
        orientation = DividerOrientation.Vertical,
        thickness = thickness,
        color = color
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
 *     PixaDivider()
 *     Text("Section 2")
 * }
 * ```
 *
 * 2. Horizontal divider in list:
 * ```
 * LazyColumn {
 *     items(items) { item ->
 *         ListItem(item)
 *         HorizontalDivider()
 *     }
 * }
 * ```
 *
 * 3. Thick section divider:
 * ```
 * Column {
 *     HeaderSection()
 *     PixaDivider(thickness = DividerThickness.Heavy)
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
 * PixaDivider(
 *     color = Color.Red,
 *     thickness = DividerThickness.Thick
 * )
 * ```
 *
 * 6. Sidebar vertical divider:
 * ```
 * Row {
 *     Sidebar()
 *     VerticalDivider(modifier = Modifier.fillMaxHeight())
 *     MainContent()
 * }
 * ```
 */
