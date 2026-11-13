package com.pixamob.pixacompose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.DisabledAlpha
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaShadow
import com.pixamob.pixacompose.theme.PixaTheme
import kotlin.math.roundToInt

private val ThumbSize = 20.dp
private val TrackHeight = 10.dp

/**
 * Picks a value in [valueRange] by tapping or dragging along a track.
 *
 * @param valueLabel when set, a bubble with this content floats above the thumb while the
 * slider is being touched, e.g. `{ PixaText("${(it * 100).toInt()}%") }`.
 */
@Composable
public fun PixaSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true,
    valueLabel: (@Composable (Float) -> Unit)? = null,
) {
    val colors = PixaTheme.colors
    val span = valueRange.endInclusive - valueRange.start
    val fraction = if (span > 0f) ((value - valueRange.start) / span).coerceIn(0f, 1f) else 0f
    val onChange by rememberUpdatedState(onValueChange)
    val thumbPx = with(LocalDensity.current) { ThumbSize.toPx() }
    var pressed by remember { mutableStateOf(false) }
    var dragging by remember { mutableStateOf(false) }

    // The thumb's center travels from half a thumb inside one end to half a thumb inside the other.
    fun valueAt(x: Float, width: Int): Float {
        val travel = (width - thumbPx).coerceAtLeast(1f)
        return valueRange.start + ((x - thumbPx / 2) / travel).coerceIn(0f, 1f) * span
    }

    val gestures = if (enabled) {
        Modifier
            .pointerInput(valueRange) {
                detectTapGestures(
                    onPress = { pressed = true; tryAwaitRelease(); pressed = false },
                ) { onChange(valueAt(it.x, size.width)) }
            }
            .pointerInput(valueRange) {
                detectHorizontalDragGestures(
                    onDragStart = { dragging = true; onChange(valueAt(it.x, size.width)) },
                    onDragEnd = { dragging = false },
                    onDragCancel = { dragging = false },
                ) { change, _ ->
                    change.consume()
                    onChange(valueAt(change.position.x, size.width))
                }
            }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ThumbSize + 8.dp)
            .graphicsLayer { alpha = if (enabled) 1f else DisabledAlpha }
            .progressSemantics(value, valueRange)
            .then(gestures),
        contentAlignment = Alignment.CenterStart,
    ) {
        PixaSurface(
            modifier = Modifier.fillMaxWidth().height(TrackHeight),
            color = colors.muted,
            shape = PixaTheme.shapes.full,
            border = filledBorder(),
        ) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(fraction).background(colors.primary, PixaTheme.shapes.full))
        }
        PixaSurface(
            modifier = Modifier
                .layout { measurable, constraints ->
                    val thumb = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
                    layout(constraints.maxWidth, thumb.height) {
                        thumb.placeRelative(((constraints.maxWidth - thumb.width) * fraction).roundToInt(), 0)
                    }
                }
                .size(ThumbSize),
            color = colors.background,
            shape = PixaTheme.shapes.full,
            border = filledBorder() ?: outlinedBorder(colors.primary),
            shadow = if (enabled) PixaTheme.decoration.controlShadow else PixaShadow.None,
        ) {}
        if (valueLabel != null) {
            val gap = with(LocalDensity.current) { PixaTheme.spacing.sm.roundToPx() }
            val motion = PixaTheme.motion.overlay
            AnimatedVisibility(
                visible = enabled && (pressed || dragging),
                // Takes no room: it floats above the thumb's center, clear of the finger.
                modifier = Modifier.layout { measurable, constraints ->
                    val bubble = measurable.measure(Constraints())
                    layout(constraints.maxWidth, 0) {
                        val center = thumbPx / 2 + (constraints.maxWidth - thumbPx) * fraction
                        val x = (center - bubble.width / 2).roundToInt()
                            .coerceIn(0, (constraints.maxWidth - bubble.width).coerceAtLeast(0))
                        bubble.placeRelative(x, -bubble.height - (thumbPx / 2).roundToInt() - gap)
                    }
                },
                enter = fadeIn(motion.spec()) + scaleIn(motion.spec(), initialScale = 0.8f),
                exit = fadeOut(motion.spec()) + scaleOut(motion.spec(), targetScale = 0.8f),
            ) { TooltipBubble { valueLabel(value) } }
        }
    }
}
