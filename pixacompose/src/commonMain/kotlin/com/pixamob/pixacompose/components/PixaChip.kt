package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * A pill that is on or off: a filter, a category, a day in a date strip. The caller owns the
 * selection, so the same chip works for choosing one or several.
 */
@Composable
public fun PixaChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = PixaTheme.colors
    val contentColor = if (selected) colors.primaryForeground else colors.secondaryForeground
    PixaSurface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 32.dp),
        enabled = enabled,
        color = if (selected) colors.primary else colors.secondary,
        contentColor = contentColor,
        shape = PixaTheme.shapes.full,
        border = filledBorder(),
        contentAlignment = Alignment.Center,
    ) {
        ProvideContentStyle(contentColor, PixaTheme.typography.label) {
            Row(
                modifier = Modifier.padding(horizontal = PixaTheme.spacing.md, vertical = PixaTheme.spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}
