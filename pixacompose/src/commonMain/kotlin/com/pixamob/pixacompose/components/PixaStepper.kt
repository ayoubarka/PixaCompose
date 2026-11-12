package com.pixamob.pixacompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme

private val DotSize = 28.dp

/**
 * Shows progress through a fixed number of steps: finished steps are checked, the current one
 * is outlined, the rest are muted.
 *
 * @param current zero-based index of the step in progress. Pass [steps] when all are done.
 */
@Composable
public fun PixaStepper(
    steps: Int,
    current: Int,
    modifier: Modifier = Modifier,
) {
    val colors = PixaTheme.colors
    val lineHeight = PixaTheme.decoration.borderWidth.coerceAtLeast(2.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .progressSemantics(current.coerceIn(0, steps).toFloat(), 0f..steps.toFloat(), steps = (steps - 1).coerceAtLeast(0)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(steps) { index ->
            val done = index < current
            val active = index == current
            if (index > 0) {
                Box(
                    Modifier
                        .weight(1f)
                        .padding(horizontal = PixaTheme.spacing.xs)
                        .height(lineHeight)
                        .background(if (index <= current) colors.primary else colors.input, PixaTheme.shapes.full),
                )
            }
            PixaSurface(
                modifier = Modifier.size(DotSize),
                color = when {
                    done -> colors.primary
                    active -> colors.background
                    else -> colors.muted
                },
                contentColor = when {
                    done -> colors.primaryForeground
                    active -> colors.foreground
                    else -> colors.mutedForeground
                },
                shape = PixaTheme.shapes.full,
                border = if (active) filledBorder() ?: outlinedBorder(colors.primary) else filledBorder(),
                contentAlignment = Alignment.Center,
            ) {
                if (done) {
                    PixaIcon(PixaIcons.Check, contentDescription = null, size = 16.dp)
                } else {
                    PixaText(
                        "${index + 1}",
                        style = PixaTheme.typography.caption.merge(fontWeight = PixaTheme.typography.label.fontWeight),
                    )
                }
            }
        }
    }
}
