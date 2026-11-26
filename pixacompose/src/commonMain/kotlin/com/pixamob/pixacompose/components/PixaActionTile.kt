package com.pixamob.pixacompose.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

/** Growth of a selected tile, so the choice stands out in a row of equal tiles. */
internal const val SelectedTileScale: Float = 1.04f

/**
 * A tappable [PixaIconTile] with a short label under it, for a grid of quick actions or a
 * picker of types. With [selected] set it works as one option of many: it takes a fill and an
 * outline of [color]. Keep [onClick]'s action and the label short; the icon carries the meaning.
 *
 * ```
 * PixaActionTile(onClick = { }, color = foodColor, label = { PixaText("Food") }) {
 *     PixaIcon(AppIcons.Bowl, contentDescription = null)
 * }
 * ```
 */
@Composable
public fun PixaActionTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = PixaTheme.colors.primary,
    selected: Boolean = false,
    enabled: Boolean = true,
    label: @Composable () -> Unit,
    icon: @Composable BoxScope.() -> Unit,
) {
    val colors = PixaTheme.colors
    val spacing = PixaTheme.spacing
    val scale by animateFloatAsState(if (selected) SelectedTileScale else 1f, PixaTheme.motion.state.spec())
    PixaSurface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        enabled = enabled,
        role = Role.Button,
        color = if (selected) tintFill(color) else colors.card,
        contentColor = colors.cardForeground,
        shape = PixaTheme.shapes.lg,
        border = if (selected) selectedBorder(color) else containerBorder(),
        shadow = PixaTheme.decoration.controlShadow,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = spacing.xs, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PixaIconTile(color = color, content = icon)
            val style = PixaTheme.typography.caption.copy(textAlign = TextAlign.Center)
            ProvideContentStyle(colors.cardForeground, style) { label() }
        }
    }
}
