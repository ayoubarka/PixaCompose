package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

/** Horizontal is the default; Vertical is the same line rotated 90°. */
enum class DividerOrientation {
    Horizontal,
    Vertical
}

/**
 * Uber Base defines exactly two divider archetypes, distinguished by weight *and*
 * alignment (not just thickness) — see [DividerVariant.defaultThickness]/[defaultInset].
 */
enum class DividerVariant {
    /** 1dp line inset to a content's leading edge, ending flush with the far edge. Separates cells within a list/row group. */
    Cell,

    /** 4dp line stretched full-bleed edge-to-edge. Separates larger content clusters/sections (e.g. card feed groups). */
    Module
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
// THEME PROVIDER (size/inset resolvers)
// ════════════════════════════════════════════════════════════════════════════

/** Border weight per Uber spec: Cell = 1dp, Module = 4dp. */
private fun DividerVariant.defaultThickness(): Dp = when (this) {
    DividerVariant.Cell -> HierarchicalSize.Divider.Compact
    DividerVariant.Module -> HierarchicalSize.Divider.Huge
}

/**
 * Leading inset per Uber spec: Cell dividers "begin with text labels" (inside alignment),
 * Module dividers are full-bleed ("outside alignment", edge-to-edge). [HierarchicalSize.Spacing.Small]
 * mirrors the only real list-row content inset in this library ([ListItemCard]'s padding);
 * callers with a different leading edge should override via [PixaDivider]'s `inset` param.
 */
private fun DividerVariant.defaultInset(): Dp = when (this) {
    DividerVariant.Cell -> HierarchicalSize.Spacing.Small
    DividerVariant.Module -> HierarchicalSize.Spacing.None
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENT
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun PixaDividerImpl(
    modifier: Modifier = Modifier,
    orientation: DividerOrientation,
    thickness: Dp,
    inset: Dp,
    colors: DividerColors
) {
    val dividerModifier = when (orientation) {
        DividerOrientation.Horizontal -> modifier
            .fillMaxWidth()
            .padding(start = inset)
            .height(thickness)
        DividerOrientation.Vertical -> modifier
            .fillMaxHeight()
            .padding(top = inset)
            .width(thickness)
    }

    Box(
        modifier = dividerModifier.background(colors.line)
    )
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaDivider - Visual separator between content.
 *
 * Purpose: a non-interactive line that separates sections of content; it carries no
 * text/icon/accessory content of its own.
 *
 * Anatomy: a single filled line, [DividerOrientation.Horizontal] (default) or rotated
 * [DividerOrientation.Vertical], with an optional leading inset before the line begins.
 *
 * Variants: [DividerVariant.Cell] (1dp, inset to a leading edge, e.g. between list rows)
 * and [DividerVariant.Module] (4dp, full-bleed edge-to-edge, e.g. between card-feed sections).
 *
 * States: none — dividers are decorative and non-interactive per spec.
 *
 * Sizing: driven by [variant] via [HierarchicalSize.Divider]; [thickness] overrides it directly
 * when a design needs a weight outside the two spec'd values.
 *
 * Adaptive behavior: out of scope. The Uber spec states divider spacing/weight stays constant
 * across breakpoints, so this does not read `AppTheme.windowSizeClass`.
 *
 * Usage notes: per spec, only place a divider *between* content — never after the last cell or
 * section on a page/list, since it visually implies more content follows.
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Cell divider between list rows (most common)
 * PixaDivider()
 *
 * // Module divider between card-feed sections
 * PixaDivider(variant = DividerVariant.Module)
 *
 * // Vertical divider in toolbar, no leading inset
 * PixaDivider(orientation = DividerOrientation.Vertical, inset = HierarchicalSize.Spacing.None)
 *
 * // Custom color divider
 * PixaDivider(color = Color.Red)
 * ```
 *
 * @param modifier Modifier for the divider
 * @param orientation Direction (Horizontal or Vertical)
 * @param variant Cell (inset, thin) or Module (full-bleed, thick); drives [thickness]/[inset] defaults
 * @param thickness Explicit line weight override; wins over [variant] when provided
 * @param inset Explicit leading-edge inset override (start for Horizontal, top for Vertical); wins over [variant] when provided
 * @param color Optional custom color; defaults to `AppTheme.colors.baseBorderDefault`
 */
@Composable
fun PixaDivider(
    modifier: Modifier = Modifier,
    orientation: DividerOrientation = DividerOrientation.Horizontal,
    variant: DividerVariant = DividerVariant.Cell,
    thickness: Dp? = null,
    inset: Dp? = null,
    color: Color? = null
) {
    val defaultColor = AppTheme.colors.baseBorderDefault
    val finalColor = color ?: defaultColor
    val finalColors = DividerColors(finalColor)
    val finalThickness = thickness ?: variant.defaultThickness()
    val finalInset = inset ?: variant.defaultInset()

    PixaDividerImpl(
        modifier = modifier,
        orientation = orientation,
        thickness = finalThickness,
        inset = finalInset,
        colors = finalColors
    )
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Horizontal Divider - Explicit horizontal separator
 */
@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    variant: DividerVariant = DividerVariant.Cell,
    thickness: Dp? = null,
    inset: Dp? = null,
    color: Color? = null
) {
    PixaDivider(
        modifier = modifier,
        orientation = DividerOrientation.Horizontal,
        variant = variant,
        thickness = thickness,
        inset = inset,
        color = color
    )
}

/**
 * Vertical Divider - Explicit vertical separator
 */
@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    variant: DividerVariant = DividerVariant.Cell,
    thickness: Dp? = null,
    inset: Dp? = null,
    color: Color? = null
) {
    PixaDivider(
        modifier = modifier,
        orientation = DividerOrientation.Vertical,
        variant = variant,
        thickness = thickness,
        inset = inset,
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
