package com.pixamob.pixacompose.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaBorder
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

public enum class PixaAvatarSize(internal val edge: Dp) { Sm(32.dp), Md(40.dp), Lg(56.dp), Xl(80.dp) }

/** Space between a ring and the avatar inside it. */
private val RingGap = 2.dp

/** Width of the ring around an avatar that is not [selected]. */
private val IdleRingWidth = 1.dp

/**
 * A round frame for a person or entity. The content is a slot: initials as [PixaText][com.pixamob.pixacompose.primitives.PixaText],
 * an icon, or an image composable from the app's own image loader filling the frame.
 *
 * @param color fill behind the content; pass an entity's own color for colored initials.
 * @param contentColor color of initials and icons.
 * @param ringColor draws a ring around the avatar, with a small gap, inside the same overall
 * size. [Color.Unspecified] draws none.
 * @param ringWidth width of the ring while [selected].
 * @param selected with a ring, false thins it to a hairline in the border color, so a row of
 * avatars can show which one is current.
 */
@Composable
public fun PixaAvatar(
    modifier: Modifier = Modifier,
    size: PixaAvatarSize = PixaAvatarSize.Md,
    color: Color = PixaTheme.colors.muted,
    contentColor: Color = PixaTheme.colors.foreground,
    ringColor: Color = Color.Unspecified,
    ringWidth: Dp = 3.dp,
    selected: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val textStyle = when (size) {
        PixaAvatarSize.Xl -> PixaTheme.typography.h1
        PixaAvatarSize.Lg -> PixaTheme.typography.h3
        else -> PixaTheme.typography.label
    }
    val face: @Composable (Modifier) -> Unit = { faceModifier ->
        PixaSurface(
            modifier = faceModifier,
            color = color,
            contentColor = contentColor,
            shape = PixaTheme.shapes.full,
            border = filledBorder(),
            contentAlignment = Alignment.Center,
        ) {
            ProvideContentStyle(contentColor, textStyle) { content() }
        }
    }
    if (!ringColor.isSpecified) {
        face(modifier.size(size.edge))
        return
    }
    val ring by animateDpAsState(if (selected) ringWidth else IdleRingWidth, PixaTheme.motion.state.spec())
    PixaSurface(
        modifier = modifier.size(size.edge),
        color = Color.Transparent,
        shape = PixaTheme.shapes.full,
        border = PixaBorder(ring, if (selected) ringColor else PixaTheme.colors.border),
        contentAlignment = Alignment.Center,
    ) {
        // The face keeps its size while the ring animates, so nothing inside jumps.
        face(Modifier.fillMaxSize().padding(ringWidth + RingGap))
    }
}
