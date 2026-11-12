package com.pixamob.pixacompose.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaTheme

/** A horizontal bar showing how far along something is. [value] is clamped to 0..1. */
@Composable
public fun Progress(
    value: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
) {
    val target = value.coerceIn(0f, 1f)
    val fraction by animateFloatAsState(target, PixaTheme.motion.state.spec())
    Surface(
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
