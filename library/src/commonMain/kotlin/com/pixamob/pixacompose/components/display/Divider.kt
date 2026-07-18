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
 * Two divider archetypes distinguished by thickness and alignment.
 */
enum class DividerVariant {
    /** 1dp line inset to leading edge. Separates items within a list/row group. */
    Cell,

    /** 4dp line full-bleed edge-to-edge. Separates larger content clusters/sections. */
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

/** Border weight: Cell = 1dp, Module = 4dp. */
private fun DividerVariant.defaultThickness(): Dp = when (this) {
    DividerVariant.Cell -> HierarchicalSize.Divider.Compact
    DividerVariant.Module -> HierarchicalSize.Divider.Huge
}

/**
 * Leading inset: Cell dividers align inside with text; Module dividers are full-bleed.
 * Callers with a non-standard leading edge should override via `inset`.
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
 * PixaDivider — visual separator between sections of content.
 *
 * ### Anatomy
 * A single filled line, [DividerOrientation.Horizontal] (default) or
 * [DividerOrientation.Vertical], with an optional leading inset.
 *
 * ### Variants
 * [DividerVariant.Cell] (1dp, inset to leading edge) and
 * [DividerVariant.Module] (4dp, full-bleed edge-to-edge).
 *
 * ### States
 * None — dividers are decorative and non-interactive.
 *
 * ### Sizing
 * Driven by [variant] via [HierarchicalSize.Divider]; [thickness] overrides.
 *
 * ### Adaptive behavior
 * Out of scope — divider weight/spacing stays constant across breakpoints.
 *
 * ### Usage notes
 * Place a divider *between* content, never after the last cell or section.
 *
 * @param modifier Modifier for the divider
 * @param orientation Horizontal or Vertical
 * @param variant Cell (thin/inset) or Module (thick/full-bleed)
 * @param thickness Overrides [variant]'s default thickness
 * @param inset Overrides [variant]'s default leading-edge inset
 * @param color Custom line color (default: `baseBorderDefault`)
 *
 * @sample
 * ```kotlin
 * // Cell divider (most common)
 * PixaDivider()
 *
 * // Module divider between sections
 * PixaDivider(variant = DividerVariant.Module)
 *
 * // Vertical toolbar divider
 * PixaDivider(orientation = DividerOrientation.Vertical, inset = HierarchicalSize.Spacing.None)
 *
 * // Custom color
 * PixaDivider(color = Color.Red)
 * ```
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



