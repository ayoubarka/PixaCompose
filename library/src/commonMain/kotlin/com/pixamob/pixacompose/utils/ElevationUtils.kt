package com.pixamob.pixacompose.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Common elevation levels used across components
 */
enum class ComponentElevation {
    /** No elevation (flat) - 0dp */
    None,
    /** Subtle elevation - 1dp */
    Low,
    /** Standard elevation - 2dp (DEFAULT for most components) */
    Medium,
    /** Prominent elevation - 4dp */
    High,
    /** Very prominent elevation - 8dp */
    Highest
}

/**
 * Convert ComponentElevation enum to Dp value
 */
fun ComponentElevation.toDp(): Dp {
    return when (this) {
        ComponentElevation.None -> 0.dp
        ComponentElevation.Low -> 1.dp
        ComponentElevation.Medium -> 2.dp
        ComponentElevation.High -> 4.dp
        ComponentElevation.Highest -> 8.dp
    }
}

/**
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

