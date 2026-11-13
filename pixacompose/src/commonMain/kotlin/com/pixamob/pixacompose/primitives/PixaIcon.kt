package com.pixamob.pixacompose.primitives

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Default icon edge length. */
public val DefaultIconSize: Dp = 20.dp

/** Icon size for a component's content slot, so a large tile gets a large icon by default. */
internal val LocalIconSize = compositionLocalOf { DefaultIconSize }

@Composable
internal fun ProvideIconSize(size: Dp, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalIconSize provides size, content = content)
}

/**
 * A single-color icon tinted with the content color of the surface it sits on.
 *
 * @param contentDescription what the icon means, for screen readers. Pass null only when the
 * icon is decorative, for example next to a text label that already says the same thing.
 * @param tint pass [Color.Unspecified] to keep the painter's own colors.
 */
@Composable
public fun PixaIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalPixaContentColor.current,
    size: Dp = LocalIconSize.current,
) {
    val semantics = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            role = Role.Image
        }
    } else {
        Modifier
    }
    Box(
        modifier
            .size(size)
            .paint(
                painter = painter,
                colorFilter = if (tint == Color.Unspecified) null else ColorFilter.tint(tint),
                contentScale = ContentScale.Fit,
            )
            .then(semantics),
    )
}

@Composable
public fun PixaIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalPixaContentColor.current,
    size: Dp = LocalIconSize.current,
) {
    PixaIcon(rememberVectorPainter(imageVector), contentDescription, modifier, tint, size)
}
