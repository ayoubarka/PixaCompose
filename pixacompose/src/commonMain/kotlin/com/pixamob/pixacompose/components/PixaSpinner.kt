package com.pixamob.pixacompose.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.LocalPixaContentColor
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * A turning arc for work of unknown length. It takes the surrounding content color, so it can
 * sit inside a button next to its label. For a known amount use [PixaCircularProgress].
 */
@Composable
public fun PixaSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    strokeWidth: Dp = 2.dp,
    color: Color = LocalPixaContentColor.current,
) {
    val angle by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(PixaTheme.motion.pulseMillis, easing = LinearEasing)),
    )
    Box(
        modifier.size(size).progressSemantics().drawBehind {
            val stroke = strokeWidth.toPx()
            rotate(angle) {
                drawArc(
                    color, startAngle = 0f, sweepAngle = 270f, useCenter = false,
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = Size(this.size.width - stroke, this.size.height - stroke),
                    style = Stroke(stroke, cap = StrokeCap.Round),
                )
            }
        },
    )
}
