package com.pixamob.pixacompose.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarkerController
import com.patrykandpatrick.vico.compose.cartesian.marker.Interaction
import com.pixamob.pixacompose.theme.PixaTheme
import kotlin.math.abs

/** Clips the chart to a growing width from the left, keyed on [key]. */
@Composable
internal fun drawInModifier(key: Any): Modifier {
    val fraction = remember(key) { Animatable(0f) }
    val spec = PixaTheme.motion.overlay
    LaunchedEffect(key) { fraction.animateTo(1f, spec.spec()) }
    return Modifier.drawWithContent {
        clipRect(right = size.width * fraction.value.coerceIn(0f, 1f)) { this@drawWithContent.drawContent() }
    }
}

/**
 * Shows the marker at the point nearest the finger from the moment it touches the chart and
 * follows it while it slides. The marker stays after release so the value can be read; tapping
 * the same point again hides it.
 */
internal class ScrubMarkerController : CartesianMarkerController {
    private var pressed = false
    private var moved = false
    private var tappedShownPoint = false
    private var shown: List<CartesianMarker.Target>? = null

    override val acceptsLongPress: Boolean = false
    override val lock: CartesianMarkerController.Lock = CartesianMarkerController.Lock.Position

    override fun shouldAcceptInteraction(interaction: Interaction, targets: List<CartesianMarker.Target>): Boolean =
        when (interaction) {
            is Interaction.Press, is Interaction.Release -> true
            is Interaction.Move -> pressed
            else -> false
        }

    override fun shouldShowMarker(interaction: Interaction, targets: List<CartesianMarker.Target>): Boolean {
        val show = when (interaction) {
            is Interaction.Press -> {
                pressed = true
                moved = false
                tappedShownPoint = shown != null && targets.sameXAs(shown)
                targets.isNotEmpty()
            }
            is Interaction.Move -> {
                moved = true
                targets.isNotEmpty()
            }
            is Interaction.Release -> {
                pressed = false
                targets.isNotEmpty() && (moved || !tappedShownPoint)
            }
            else -> shown != null
        }
        shown = if (show) targets else null
        return show
    }

    private fun List<CartesianMarker.Target>.sameXAs(other: List<CartesianMarker.Target>?): Boolean =
        other != null && map { it.x } == other.map { it.x }
}

/**
 * Keeps a scrolling screen still while the finger reads the chart. Once a touch on the chart
 * slides sideways, or slides after a long press, its moves are consumed so a parent
 * `verticalScroll` or `LazyColumn` does not take them. A touch that starts by moving up or down
 * is left alone and scrolls the screen as usual.
 */
internal fun Modifier.holdHorizontalDrag(): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val slop = viewConfiguration.touchSlop
        val longPress = viewConfiguration.longPressTimeoutMillis
        var travel = Offset.Zero
        var holding = false
        while (true) {
            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
            if (!change.pressed) break
            if (!holding) {
                travel += change.positionChange()
                val held = change.uptimeMillis - down.uptimeMillis >= longPress
                when {
                    abs(travel.x) > slop && abs(travel.x) > abs(travel.y) -> holding = true
                    held && travel.getDistance() <= slop * 2 -> holding = true
                    abs(travel.y) > slop -> break
                }
            }
            if (holding) change.consume()
        }
    }
}
