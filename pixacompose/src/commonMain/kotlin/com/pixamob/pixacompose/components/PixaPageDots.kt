package com.pixamob.pixacompose.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

private val DotSize = 8.dp
private val CurrentDotWidth = 22.dp

/** A row of dots showing which of [count] pages is showing; the [current] one is a wider pill. */
@Composable
public fun PixaPageDots(
    count: Int,
    current: Int,
    modifier: Modifier = Modifier,
) {
    val colors = PixaTheme.colors
    Row(
        modifier.semantics { contentDescription = "Page ${current + 1} of $count" },
        horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.xs + PixaTheme.spacing.xs / 2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val isCurrent = index == current
            val width by animateDpAsState(
                if (isCurrent) CurrentDotWidth else DotSize,
                PixaTheme.motion.state.spec(),
            )
            PixaSurface(
                modifier = Modifier.size(width.coerceAtLeast(DotSize), DotSize),
                color = if (isCurrent) colors.primary else colors.muted,
                shape = PixaTheme.shapes.full,
                border = filledBorder() ?: if (isCurrent) null else outlinedBorder(colors.input),
            ) {}
        }
    }
}
