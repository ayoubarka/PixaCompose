package com.pixamob.pixacompose.primitives

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.pixamob.pixacompose.theme.PixaTheme

/** Share of the outline the moving light covers, tail included. */
private const val BeamLength = 0.35f

/** The tail is drawn in this many steps, each fainter than the one ahead of it. */
private const val TailSteps = 24

/**
 * A light that runs around the edge of [shape], for a control that is busy. It is drawn just
 * inside the bounds, over the border, in [color]; one lap takes two of the style's pulses.
 */
@Composable
internal fun Modifier.loadingBorder(shape: Shape, color: Color): Modifier {
    val lap = PixaTheme.motion.pulseMillis * 2
    val phase by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(lap, easing = LinearEasing)),
    )
    val width = max(PixaTheme.decoration.borderWidth, 2.dp)
    val measure = remember { PathMeasure() }
    val piece = remember { Path() }
    return progressSemantics().drawWithCache {
        val stroke = width.toPx()
        val outline = shape.createOutline(Size(size.width - stroke, size.height - stroke), layoutDirection, this)
        val edge = Path().apply { addOutline(outline) }
        measure.setPath(edge, forceClosed = true)
        val length = measure.length
        onDrawWithContent {
            drawContent()
            if (length <= 0f) return@onDrawWithContent
            val head = phase * length
            val step = length * BeamLength / TailSteps
            translate(stroke / 2, stroke / 2) {
                for (i in 0 until TailSteps) {
                    // i = 0 is the bright head; later steps trail behind it and fade out.
                    val end = head - i * step
                    val alpha = 1f - i.toFloat() / TailSteps
                    drawPiece(measure, piece, end - step, end, length) {
                        drawPath(piece, color.copy(alpha = color.alpha * alpha), style = Stroke(stroke, cap = StrokeCap.Butt))
                    }
                }
            }
        }
    }
}

/** Fills [piece] with the outline from [start] to [end], wrapping past the starting point. */
private inline fun drawPiece(measure: PathMeasure, piece: Path, start: Float, end: Float, length: Float, draw: () -> Unit) {
    val from = ((start % length) + length) % length
    val to = from + (end - start)
    piece.reset()
    if (to <= length) {
        measure.getSegment(from, to, piece, startWithMoveTo = true)
    } else {
        measure.getSegment(from, length, piece, startWithMoveTo = true)
        measure.getSegment(0f, to - length, piece, startWithMoveTo = true)
    }
    draw()
}
