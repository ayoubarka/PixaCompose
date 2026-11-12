package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

public enum class PixaAvatarSize(internal val edge: Dp) { Sm(32.dp), Md(40.dp), Lg(56.dp) }

/**
 * A round frame for a person or entity. The content is a slot: initials as [PixaText][com.pixamob.pixacompose.primitives.Text],
 * an icon, or an image composable from the app's own image loader filling the frame.
 */
@Composable
public fun PixaAvatar(
    modifier: Modifier = Modifier,
    size: PixaAvatarSize = PixaAvatarSize.Md,
    content: @Composable BoxScope.() -> Unit,
) {
    PixaSurface(
        modifier = modifier.size(size.edge),
        color = PixaTheme.colors.muted,
        contentColor = PixaTheme.colors.foreground,
        shape = PixaTheme.shapes.full,
        border = filledBorder(),
        contentAlignment = Alignment.Center,
    ) {
        val textStyle = if (size == PixaAvatarSize.Lg) PixaTheme.typography.h3 else PixaTheme.typography.label
        ProvideContentStyle(PixaTheme.colors.foreground, textStyle) { content() }
    }
}
