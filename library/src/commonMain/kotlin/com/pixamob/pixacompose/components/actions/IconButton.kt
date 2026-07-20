package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// CONFIGURATION
// ════════════════════════════════════════════════════════════════════════════

enum class IconButtonVariant {
    Filled,
    Outlined,
    Ghost,
    Tonal
}

@Immutable
data class IconButtonColors(
    val containerColor: Color = Color.Unspecified,
    val contentColor: Color = Color.Unspecified,
    val borderColor: Color = Color.Unspecified,
    val disabledContainerColor: Color = Color.Unspecified,
    val disabledContentColor: Color = Color.Unspecified,
)

@Immutable
data class IconButtonSizeConfig(
    val containerSize: Dp,
    val iconSize: Dp,
    val labelStyle: @Composable () -> TextStyle
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getIconButtonSizeConfig(size: SizeVariant): IconButtonSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.Small -> IconButtonSizeConfig(
            containerSize = 36.dp,
            iconSize = 20.dp,
            labelStyle = { typography.labelSmall }
        )
        SizeVariant.Medium -> IconButtonSizeConfig(
            containerSize = 44.dp,
            iconSize = 24.dp,
            labelStyle = { typography.labelMedium }
        )
        SizeVariant.Large -> IconButtonSizeConfig(
            containerSize = 52.dp,
            iconSize = 28.dp,
            labelStyle = { typography.labelLarge }
        )
        else -> IconButtonSizeConfig(
            containerSize = 44.dp,
            iconSize = 24.dp,
            labelStyle = { typography.labelMedium }
        )
    }
}

@Composable
private fun getIconButtonTheme(
    variant: IconButtonVariant,
    colors: ColorPalette,
    selected: Boolean = false,
    enabled: Boolean = true
): IconButtonColors {
    val baseColors = when (variant) {
        IconButtonVariant.Filled -> IconButtonColors(
            containerColor = colors.brandSurfaceDefault,
            contentColor = colors.brandContentDefault,
            borderColor = Color.Transparent,
            disabledContainerColor = colors.baseSurfaceDisabled,
            disabledContentColor = colors.baseContentDisabled
        )
        IconButtonVariant.Outlined -> IconButtonColors(
            containerColor = Color.Transparent,
            contentColor = colors.brandContentDefault,
            borderColor = colors.brandBorderDefault,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colors.baseContentDisabled
        )
        IconButtonVariant.Ghost -> IconButtonColors(
            containerColor = Color.Transparent,
            contentColor = colors.brandContentDefault,
            borderColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colors.baseContentDisabled
        )
        IconButtonVariant.Tonal -> IconButtonColors(
            containerColor = colors.brandSurfaceSubtle,
            contentColor = colors.brandContentDefault,
            borderColor = Color.Transparent,
            disabledContainerColor = colors.baseSurfaceDisabled,
            disabledContentColor = colors.baseContentDisabled
        )
    }

    if (selected && enabled) {
        return baseColors.copy(
            containerColor = colors.brandSurfaceFocus,
            contentColor = colors.brandContentDefault
        )
    }

    return baseColors
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaIconButton - Circular icon button component
 *
 * A button with an icon only (optional label below), supporting multiple
 * visual variants and sizes.
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic ghost icon button
 * PixaIconButton(
 *     icon = painterResource(R.drawable.ic_edit),
 *     onClick = { }
 * )
 *
 * // Filled icon button with label
 * PixaIconButton(
 *     icon = painterResource(R.drawable.ic_settings),
 *     onClick = { },
 *     variant = IconButtonVariant.Filled,
 *     label = "Settings",
 *     contentDescription = "Open settings"
 * )
 *
 * // Selected icon button
 * PixaIconButton(
 *     icon = painterResource(R.drawable.ic_heart),
 *     onClick = { },
 *     selected = true
 * )
 * ```
 *
 * @param icon Required icon painter
 * @param onClick Required click callback
 * @param modifier Modifier for styling
 * @param variant Visual style variant (default: Ghost)
 * @param size Size variant (default: Medium)
 * @param label Optional label text rendered below icon
 * @param selected Whether the button is in selected state
 * @param enabled Whether the button is enabled
 * @param colors Custom colors (defaults: empty IconButtonColors)
 * @param contentDescription Accessibility description
 */
@Composable
fun PixaIconButton(
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: IconButtonVariant = IconButtonVariant.Ghost,
    size: SizeVariant = SizeVariant.Medium,
    label: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonColors(),
    contentDescription: String? = null
) {
    val sizeConfig = getIconButtonSizeConfig(size)
    val themeColors = getIconButtonTheme(variant, AppTheme.colors, selected, enabled)

    val containerColor by animateColorAsState(
        targetValue = if (!enabled) themeColors.disabledContainerColor
        else if (colors.containerColor != Color.Unspecified) colors.containerColor
        else themeColors.containerColor,
        label = "iconButtonContainer"
    )

    val contentColor by animateColorAsState(
        targetValue = if (!enabled) themeColors.disabledContentColor
        else if (colors.contentColor != Color.Unspecified) colors.contentColor
        else themeColors.contentColor,
        label = "iconButtonContent"
    )

    val borderColor = if (!enabled) themeColors.disabledContainerColor
    else if (colors.borderColor != Color.Unspecified) colors.borderColor
    else themeColors.borderColor

    Column(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription?.let { this.contentDescription = it }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(sizeConfig.containerSize)
                .clip(CircleShape)
                .background(containerColor)
                .then(
                    if (borderColor != Color.Transparent) {
                        Modifier.border(1.dp, borderColor, CircleShape)
                    } else Modifier
                )
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    indication = pixaRipple(bounded = true, color = contentColor.copy(alpha = 0.12f)),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            PixaIcon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(sizeConfig.iconSize),
                tint = contentColor
            )
        }

        if (label != null) {
            BasicText(
                text = label,
                style = sizeConfig.labelStyle().copy(
                    color = AppTheme.colors.baseContentCaption,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================================
// Convenience Variants
// ============================================================================

@Composable
fun FilledIconButton(
    icon: Painter,
    onClick: () -> Unit,
    size: SizeVariant = SizeVariant.Medium,
    modifier: Modifier = Modifier
) {
    PixaIconButton(
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        variant = IconButtonVariant.Filled,
        size = size
    )
}

@Composable
fun OutlinedIconButton(
    icon: Painter,
    onClick: () -> Unit,
    size: SizeVariant = SizeVariant.Medium,
    modifier: Modifier = Modifier
) {
    PixaIconButton(
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        variant = IconButtonVariant.Outlined,
        size = size
    )
}

@Composable
fun GhostIconButton(
    icon: Painter,
    onClick: () -> Unit,
    size: SizeVariant = SizeVariant.Medium,
    modifier: Modifier = Modifier
) {
    PixaIconButton(
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        variant = IconButtonVariant.Ghost,
        size = size
    )
}
