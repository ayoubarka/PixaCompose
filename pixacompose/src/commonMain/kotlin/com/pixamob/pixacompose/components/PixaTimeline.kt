package com.pixamob.pixacompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

private val DotSize = 32.dp

/**
 * A vertical stepper: one row per step, a dot on the start side joined to the next by a line,
 * and the step's own content beside it. Steps before [current] are done (filled, line in the
 * primary colour), the [current] one is outlined, later ones are muted. Use it for a process
 * read top to bottom: an order's progress, what happens during a free trial.
 *
 * @param current zero-based index of the step in progress. Pass [steps] when all are done.
 * @param indicator what sits inside step [index]'s dot; it inherits the dot's content colour.
 *   Null draws a check on a done step and the step's number otherwise, like [PixaStepper].
 * @param content the step's text, usually a title and a line under it.
 */
@Composable
public fun PixaTimeline(
    steps: Int,
    current: Int,
    modifier: Modifier = Modifier,
    indicator: (@Composable (index: Int) -> Unit)? = null,
    content: @Composable (index: Int) -> Unit,
) {
    val colors = PixaTheme.colors
    val spacing = PixaTheme.spacing
    val lineWidth = PixaTheme.decoration.borderWidth.coerceAtLeast(2.dp)
    Column(modifier.fillMaxWidth()) {
        repeat(steps) { index ->
            val done = index < current
            val active = index == current
            val last = index == steps - 1
            // Intrinsic height so the connector stretches to however tall the content is.
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
                Column(Modifier.width(DotSize).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                    PixaSurface(
                        modifier = Modifier.size(DotSize),
                        color = when {
                            done -> colors.primary
                            active -> colors.background
                            else -> colors.muted
                        },
                        contentColor = when {
                            done -> colors.primaryForeground
                            active -> colors.primary
                            else -> colors.mutedForeground
                        },
                        shape = PixaTheme.shapes.full,
                        border = if (active) filledBorder() ?: outlinedBorder(colors.primary) else filledBorder(),
                        contentAlignment = Alignment.Center,
                    ) {
                        when {
                            indicator != null -> indicator(index)
                            done -> PixaIcon(PixaIcons.Check, contentDescription = null, size = 16.dp)
                            else -> PixaText(
                                "${index + 1}",
                                style = PixaTheme.typography.caption.merge(fontWeight = PixaTheme.typography.label.fontWeight),
                            )
                        }
                    }
                    if (!last) {
                        Box(
                            Modifier
                                .padding(vertical = spacing.xs)
                                .width(lineWidth)
                                .weight(1f)
                                .background(if (done) colors.primary else colors.input, PixaTheme.shapes.full),
                        )
                    }
                }
                Box(Modifier.weight(1f).padding(top = spacing.xs, bottom = if (last) 0.dp else spacing.lg)) {
                    content(index)
                }
            }
        }
    }
}
