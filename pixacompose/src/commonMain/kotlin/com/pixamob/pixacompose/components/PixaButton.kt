package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaShadow
import com.pixamob.pixacompose.theme.PixaTheme
import kotlin.math.abs

public enum class PixaButtonVariant { Primary, Secondary, Outline, Ghost, Destructive }

public enum class PixaButtonSize(internal val minHeight: Dp, internal val padding: PaddingValues) {
    Sm(32.dp, PaddingValues(horizontal = 12.dp, vertical = 6.dp)),
    Md(40.dp, PaddingValues(horizontal = 16.dp, vertical = 8.dp)),
    Lg(48.dp, PaddingValues(horizontal = 24.dp, vertical = 10.dp)),
}

/**
 * Content is a slot: put any mix of text and icons inside, spaced automatically.
 *
 * ```
 * PixaButton(onClick = { }) {
 *     PixaIcon(PixaIcons.Plus, contentDescription = null)
 *     PixaText("Add")
 * }
 * ```
 *
 * @param color fill in place of the [variant]'s, for a button in a brand's own colors such as
 * "Continue with Google". The variant still sets the outline and shadow, so pair a light
 * [color] with [PixaButtonVariant.Outline].
 * @param contentColor text and icon color to go with [color].
 * @param loading runs a light around the button's edge and ignores taps, for an action that
 * is in progress. The content stays, so the button keeps its size.
 */
@Composable
public fun PixaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PixaButtonVariant = PixaButtonVariant.Primary,
    size: PixaButtonSize = PixaButtonSize.Md,
    enabled: Boolean = true,
    loading: Boolean = false,
    color: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = PixaTheme.colors
    val decoration = PixaTheme.decoration

    val (variantContainer, variantContent) = when (variant) {
        PixaButtonVariant.Primary -> colors.primary to colors.primaryForeground
        PixaButtonVariant.Secondary -> colors.secondary to colors.secondaryForeground
        PixaButtonVariant.Destructive -> colors.destructive to colors.destructiveForeground
        PixaButtonVariant.Outline -> colors.background to colors.foreground
        PixaButtonVariant.Ghost -> Color.Transparent to colors.foreground
    }
    val container = color.takeOrElse { variantContainer }
    val onContainer = contentColor.takeOrElse { variantContent }
    val border = when (variant) {
        PixaButtonVariant.Ghost -> null
        PixaButtonVariant.Outline -> outlinedBorder()
        else -> filledBorder()
    }

    PixaSurface(
        onClick = { if (!loading) onClick() },
        modifier = modifier.defaultMinSize(minHeight = size.minHeight),
        enabled = enabled,
        role = Role.Button,
        color = container,
        contentColor = onContainer,
        shape = PixaTheme.shapes.md,
        border = border,
        shadow = if (variant == PixaButtonVariant.Ghost) PixaShadow.None else decoration.controlShadow,
        contentAlignment = Alignment.Center,
        loadingColor = if (loading) loadingLight(border?.color ?: container, onContainer) else Color.Unspecified,
    ) {
        ProvideContentStyle(onContainer, PixaTheme.typography.label) {
            Row(
                modifier = Modifier.padding(size.padding),
                horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.sm, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}

/**
 * Color of the loading light: it must stand out from the edge it runs along, the border if
 * there is one, else the fill. Picks whichever theme color differs most from it in lightness.
 */
@Composable
private fun loadingLight(edgeColor: Color, contentColor: Color): Color {
    val colors = PixaTheme.colors
    val edge = edgeColor.takeIf { it.alpha > 0f } ?: colors.background
    return listOf(contentColor, colors.primary, colors.ring).maxBy { abs(it.luminance() - edge.luminance()) }
}
