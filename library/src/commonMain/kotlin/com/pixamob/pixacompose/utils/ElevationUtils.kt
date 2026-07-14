package com.pixamob.pixacompose.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.HierarchicalSize

/**
 * Canonical semantic elevation ladder — the single source of truth for how
 * "deep" a surface sits above its neighbors. Every component that raises a
 * surface with a shadow (Card, Button, Snackbar, Toast, Dialog, Menu,
 * Popover, Tooltip, ...) should express its intent as one of these five
 * tiers rather than reaching for a raw `Dp` or a private per-component enum.
 *
 * The underlying `Dp` values are *not* redefined here — they're read from
 * [HierarchicalSize.Shadow] (see [toDp]), so there is exactly one numeric
 * elevation scale in the theme, not two parallel ones.
 *
 * Semantic guide (see DOCUMENTATION.md "Elevation" for the full component
 * mapping):
 * - [None]: flat content, disabled surfaces, outlined/ghost variants that
 *   rely on a border instead of a shadow for separation (see [Modifier.elevationShadow]'s
 *   `enabled` param for the disabled case).
 * - [Low]: resting-state surfaces that are only subtly raised — a default
 *   filled/tonal button, a card at rest.
 * - [Medium]: standard raised surfaces — the default elevated card.
 * - [High]: surfaces that float *above* general content and need clear
 *   separation without being the topmost layer — dialogs, menus, popovers,
 *   dropdowns (Uber Base's "shallow below" tier).
 * - [Highest]: surfaces that need the strongest visual hierarchy because
 *   they interrupt or float over everything else — snackbars, toasts,
 *   tooltips (Uber Base's "deep" tier).
 */
enum class ComponentElevation {
    None,
    Low,
    Medium,
    High,
    Highest
}

/**
 * Resolves the [Dp] shadow depth for a [ComponentElevation] tier, sourced
 * from [HierarchicalSize.Shadow] — the one place elevation `Dp` values live.
 */
fun ComponentElevation.toDp(): Dp = when (this) {
    ComponentElevation.None -> HierarchicalSize.Shadow.None       // 0dp
    ComponentElevation.Low -> HierarchicalSize.Shadow.Nano        // 1dp
    ComponentElevation.Medium -> HierarchicalSize.Shadow.Compact  // 2dp
    ComponentElevation.High -> HierarchicalSize.Shadow.Medium     // 4dp
    ComponentElevation.Highest -> HierarchicalSize.Shadow.Large   // 8dp
}

/**
 * Elevation is only one of four ways a component expresses depth/emphasis —
 * don't reach for a shadow when one of the others is the actual right tool:
 *
 * - **Surface layering**: which physical surface a component's background
 *   paints onto (`colors.baseSurfaceDefault` vs `.baseSurfaceElevated`, see
 *   `theme/Color.kt`). This is what actually distinguishes "this card sits
 *   on top of the screen background" independent of any shadow.
 * - **Shadow rendering**: the literal drop-shadow this file applies — use it
 *   to show a surface is *lifted above* its neighbors (floating, draggable,
 *   overlaying), never merely to separate adjacent same-level content.
 * - **Tonal emphasis**: a `Tonal`/filled color variant (e.g. `ButtonVariant.Tonal`)
 *   communicating prominence through color instead of depth — a tonal
 *   surface can legitimately sit at [ComponentElevation.None] or [ComponentElevation.Low].
 * - **Border emphasis**: an `Outlined`/`Ghost` variant using
 *   `HierarchicalSize.Border` + a border color for separation instead of a
 *   shadow — per Uber Base's guidance, prefer a border over a shadow when a
 *   component merely needs a visual boundary, not physical lift.
 *
 * Apply elevation shadow to a composable
 *
 * @param elevation The elevation level
 * @param shape The shape for the shadow
 * @param clip Whether to clip the content to the shape
 * @param enabled Whether elevation is enabled (e.g., disabled state might remove elevation)
 *
 * @return Modifier with shadow applied
 */
fun Modifier.elevationShadow(
    elevation: ComponentElevation,
    shape: Shape,
    clip: Boolean = false,
    enabled: Boolean = true
): Modifier {
    val elevationDp = if (enabled) elevation.toDp() else 0.dp
    return if (elevationDp > 0.dp) {
        this.shadow(elevation = elevationDp, shape = shape, clip = clip)
    } else {
        this
    }
}

/**
 * Apply elevation shadow to a composable using Dp value
 *
 * @param elevation The elevation in Dp
 * @param shape The shape for the shadow
 * @param clip Whether to clip the content to the shape
 * @param enabled Whether elevation is enabled
 *
 * @return Modifier with shadow applied
 */
fun Modifier.elevationShadow(
    elevation: Dp,
    shape: Shape,
    clip: Boolean = false,
    enabled: Boolean = true
): Modifier {
    val elevationDp = if (enabled) elevation else 0.dp
    return if (elevationDp > 0.dp) {
        this.shadow(elevation = elevationDp, shape = shape, clip = clip)
    } else {
        this
    }
}

