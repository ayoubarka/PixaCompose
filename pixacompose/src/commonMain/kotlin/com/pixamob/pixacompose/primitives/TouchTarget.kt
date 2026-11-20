package com.pixamob.pixacompose.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Smallest touch area of a tappable control: 48dp by default, from the Android and iOS
 * accessibility guidelines. A dense grid whose cells are tapped in context, such as a calendar,
 * or a desktop-only screen, can provide a smaller value; 0.dp turns it off.
 */
public val LocalPixaMinimumTouchTarget: ProvidableCompositionLocal<Dp> = staticCompositionLocalOf { 48.dp }

/**
 * Reserves at least [LocalPixaMinimumTouchTarget] in each direction around a smaller control and
 * centers the control in it. The control keeps its drawn size; touches in the reserved space
 * reach it because Compose widens the hit area of small pointer targets up to the same size.
 */
@Composable
internal fun Modifier.minimumTouchTarget(): Modifier {
    val target = LocalPixaMinimumTouchTarget.current
    if (target <= 0.dp) return this
    return layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val min = target.roundToPx()
        val width = maxOf(placeable.width, min).coerceAtMost(constraints.maxWidth)
        val height = maxOf(placeable.height, min).coerceAtMost(constraints.maxHeight)
        layout(width, height) {
            placeable.place((width - placeable.width) / 2, (height - placeable.height) / 2)
        }
    }
}
