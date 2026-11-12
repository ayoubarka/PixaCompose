package com.pixamob.pixacompose.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * A pulsing placeholder for content that is still loading. Size it with [modifier] to match
 * what it stands in for.
 */
@Composable
public fun PixaSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = PixaTheme.shapes.md,
) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(PixaTheme.motion.pulseMillis), RepeatMode.Reverse),
    )
    Box(
        modifier
            .graphicsLayer { this.alpha = alpha }
            .background(PixaTheme.colors.muted, shape),
    )
}
