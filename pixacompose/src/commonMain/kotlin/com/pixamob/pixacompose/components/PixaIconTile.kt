package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.ProvideIconSize
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

public enum class PixaIconTileSize(internal val edge: Dp, internal val iconSize: Dp) {
    Sm(32.dp, 16.dp),
    Md(44.dp, 22.dp),
    Lg(56.dp, 28.dp),
    Xl(72.dp, 36.dp),
}

/**
 * An icon on a soft square of its own color, to mark a kind of thing: a category, a log type,
 * a list row. The icon is drawn in [color] on a faint fill of it.
 *
 * ```
 * PixaIconTile(color = categoryColor) { PixaIcon(AppIcons.Pill, contentDescription = null) }
 * ```
 */
@Composable
public fun PixaIconTile(
    modifier: Modifier = Modifier,
    color: Color = PixaTheme.colors.primary,
    size: PixaIconTileSize = PixaIconTileSize.Md,
    content: @Composable BoxScope.() -> Unit,
) {
    PixaSurface(
        modifier = modifier.size(size.edge),
        color = tintFill(color),
        contentColor = color,
        shape = PixaTheme.shapes.md,
        border = filledBorder(),
        contentAlignment = Alignment.Center,
    ) {
        ProvideContentStyle(color, PixaTheme.typography.label) {
            ProvideIconSize(size.iconSize) { content() }
        }
    }
}
