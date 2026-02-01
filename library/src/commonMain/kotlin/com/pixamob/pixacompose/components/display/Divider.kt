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
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class DividerOrientation {
    Horizontal,
    Vertical
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class DividerColors(
    val line: Color
)

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENT
// ════════════════════════════════════════════════════════════════════════════

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

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaDivider - Visual separator between content
 *
 * A simple line component for separating content sections.
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Horizontal divider (most common)
 * PixaDivider()
 *
 * // Vertical divider in toolbar
 * PixaDivider(orientation = DividerOrientation.Vertical)
 *
 * // Custom thickness
 * PixaDivider(thickness = HierarchicalSize.Divider.Large)
 *
 * // Custom color divider
 * PixaDivider(color = Color.Red)
 * ```
 *
 * @param modifier Modifier for the divider
 * @param orientation Direction (Horizontal or Vertical)
 * @param thickness Line thickness in Dp
 * @param color Optional custom color
 */
@Composable
fun PixaDivider(
    modifier: Modifier = Modifier,
    orientation: DividerOrientation = DividerOrientation.Horizontal,
    thickness: Dp = HierarchicalSize.Divider.Compact,  // 1dp standard
    color: Color? = null
) {
    val defaultColor = AppTheme.colors.baseBorderDefault
    val finalColor = color ?: defaultColor
    val finalColors = DividerColors(finalColor)

    PixaDividerImpl(
        modifier = modifier,
        orientation = orientation,
        thickness = thickness,
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
    thickness: Dp = HierarchicalSize.Divider.Compact,  // 1dp standard
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
    thickness: Dp = HierarchicalSize.Divider.Compact,  // 1dp standard
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
 *     PixaDivider(thickness = HierarchicalSize.Divider.Large)
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
 *     thickness = HierarchicalSize.Divider.Huge
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
