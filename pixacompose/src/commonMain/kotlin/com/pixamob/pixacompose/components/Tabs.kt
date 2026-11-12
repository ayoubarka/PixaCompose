package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * A segmented row of [Tab]s. The caller owns which tab is selected and what is shown for it.
 * Give each tab `Modifier.weight(1f)` to make them share the width equally.
 */
@Composable
public fun Tabs(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        color = PixaTheme.colors.muted,
        contentColor = PixaTheme.colors.mutedForeground,
        shape = PixaTheme.shapes.md,
        border = filledBorder(),
    ) {
        Row(
            modifier = Modifier.padding(PixaTheme.spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Composable
public fun Tab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = PixaTheme.colors
    val contentColor = if (selected) colors.foreground else colors.mutedForeground
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 32.dp),
        enabled = enabled,
        role = Role.Tab,
        color = if (selected) colors.background else Color.Transparent,
        contentColor = contentColor,
        shape = PixaTheme.shapes.sm,
        border = if (selected) filledBorder() else null,
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
