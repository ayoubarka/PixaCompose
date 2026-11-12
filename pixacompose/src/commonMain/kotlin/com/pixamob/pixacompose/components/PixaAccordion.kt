package com.pixamob.pixacompose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * A heading that shows or hides the content below it. Stack several in a `Column` to make an
 * accordion; the caller decides whether one or many can be open by how it holds the state.
 */
@Composable
public fun PixaAccordionItem(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = PixaTheme.colors
    val spacing = PixaTheme.spacing
    val spec = PixaTheme.motion.state
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, spec.spec())

    Column(modifier.fillMaxWidth()) {
        PixaSurface(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            contentColor = colors.foreground,
            shape = PixaTheme.shapes.sm,
        ) {
            ProvideContentStyle(colors.foreground, PixaTheme.typography.label) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        content = title,
                    )
                    PixaIcon(
                        PixaIcons.ChevronDown,
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer { rotationZ = rotation },
                        size = 16.dp,
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(spec.spec()) + fadeIn(spec.spec()),
            exit = shrinkVertically(spec.spec()) + fadeOut(spec.spec()),
        ) {
            ProvideContentStyle(colors.mutedForeground, PixaTheme.typography.bodySmall) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = spacing.md),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    content = content,
                )
            }
        }
        PixaSeparator()
    }
}
