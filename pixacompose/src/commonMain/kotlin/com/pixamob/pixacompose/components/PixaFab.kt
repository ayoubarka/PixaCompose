package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.ProvideIconSize
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

public enum class PixaFabSize(internal val edge: Dp, internal val iconSize: Dp) {
    Md(56.dp, 24.dp),
    Lg(64.dp, 28.dp),
}

/**
 * A round primary button that floats over the screen, for the screen's main action. Place it
 * yourself, for example `Modifier.align(Alignment.BottomEnd).padding(...)` inside a `Box`.
 * The content is a slot, usually one [PixaIcon][com.pixamob.pixacompose.primitives.PixaIcon]
 * with a content description.
 */
@Composable
public fun PixaFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: PixaFabSize = PixaFabSize.Md,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = PixaTheme.colors
    PixaSurface(
        onClick = onClick,
        modifier = modifier.size(size.edge),
        enabled = enabled,
        role = Role.Button,
        color = colors.primary,
        contentColor = colors.primaryForeground,
        shape = PixaTheme.shapes.full,
        border = filledBorder(),
        // It floats above content, so it takes the stronger container shadow.
        shadow = PixaTheme.decoration.containerShadow,
        contentAlignment = Alignment.Center,
    ) {
        ProvideContentStyle(colors.primaryForeground, PixaTheme.typography.label) {
            ProvideIconSize(size.iconSize) { content() }
        }
    }
}
