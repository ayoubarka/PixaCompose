package com.pixamob.pixacompose.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

/** A horizontal bar showing how far along something is. [value] is clamped to 0..1. */
@Composable
public fun PixaProgress(
    value: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
) {
    val target = value.coerceIn(0f, 1f)
    val fraction by animateFloatAsState(target, PixaTheme.motion.state.spec())
    PixaSurface(
        modifier = modifier.fillMaxWidth().height(height).progressSemantics(target),
        color = PixaTheme.colors.muted,
        shape = PixaTheme.shapes.full,
        border = filledBorder(),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                // A bouncy style may overshoot; the bar itself must stay inside the track.
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .background(PixaTheme.colors.primary, PixaTheme.shapes.full),
        )
    }
}

/**
 * A ring showing how far along something is. [value] is clamped to 0..1. [content] sits in the
 * middle, for a percentage or an icon.
 */
@Composable
public fun PixaCircularProgress(
    value: Float,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    strokeWidth: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val target = value.coerceIn(0f, 1f)
    val fraction by animateFloatAsState(target, PixaTheme.motion.state.spec())
    val track = PixaTheme.colors.muted
    val indicator = PixaTheme.colors.primary
    Box(
        modifier = modifier
            .size(size)
            .progressSemantics(target)
            .drawBehind {
                val stroke = strokeWidth.toPx()
                val inset = Offset(stroke / 2, stroke / 2)
                val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
                drawArc(track, 0f, 360f, useCenter = false, topLeft = inset, size = arcSize, style = Stroke(stroke))
                drawArc(
                    indicator, -90f, 360f * fraction.coerceIn(0f, 1f), useCenter = false,
                    topLeft = inset, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Round),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        ProvideContentStyle(PixaTheme.colors.foreground, PixaTheme.typography.caption) { content() }
    }
}
