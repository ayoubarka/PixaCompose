package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * One row of a list: [leading] (an icon, avatar or image), the [headline] with optional
 * [supporting] text under it, and [trailing] (a chevron, a switch, a value). Pass [onClick] to
 * make the whole row tappable.
 */
@Composable
public fun PixaListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    supporting: (@Composable () -> Unit)? = null,
    headline: @Composable () -> Unit,
) {
    val colors = PixaTheme.colors
    val spacing = PixaTheme.spacing
    val row: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md, vertical = spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leading?.invoke()
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xs / 2)) {
                ProvideContentStyle(colors.foreground, PixaTheme.typography.label, headline)
                if (supporting != null) {
                    ProvideContentStyle(colors.mutedForeground, PixaTheme.typography.bodySmall, supporting)
                }
            }
            if (trailing != null) ProvideContentStyle(colors.mutedForeground, PixaTheme.typography.bodySmall, trailing)
        }
    }
    val base = modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp)
    if (onClick != null) {
        PixaSurface(
            onClick = onClick,
            modifier = base,
            enabled = enabled,
            color = Color.Transparent,
            contentColor = colors.foreground,
            shape = PixaTheme.shapes.md,
            contentAlignment = Alignment.CenterStart,
        ) { row() }
    } else {
        PixaSurface(
            modifier = base,
            color = Color.Transparent,
            contentColor = colors.foreground,
            shape = PixaTheme.shapes.md,
            contentAlignment = Alignment.CenterStart,
        ) { row() }
    }
}
