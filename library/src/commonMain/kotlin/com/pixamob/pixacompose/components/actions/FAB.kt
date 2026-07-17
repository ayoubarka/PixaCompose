package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// CONFIGURATION
// ════════════════════════════════════════════════════════════════════════════

enum class FABVariant {
    Filled,
    Tonal,
    Outlined
}

@Immutable
data class FABColors(
    val containerColor: Color = Color.Unspecified,
    val contentColor: Color = Color.Unspecified,
    val borderColor: Color = Color.Unspecified,
    val disabledContainerColor: Color = Color.Unspecified,
    val disabledContentColor: Color = Color.Unspecified
)

@Immutable
data class FABSizeConfig(
    val containerSize: Dp,
    val iconSize: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getFABSizeConfig(size: SizeVariant): FABSizeConfig {
    return when (size) {
        SizeVariant.Medium -> FABSizeConfig(
            containerSize = 48.dp,
            iconSize = 24.dp
        )
        SizeVariant.Large -> FABSizeConfig(
            containerSize = 56.dp,
            iconSize = 24.dp
        )
        SizeVariant.Huge -> FABSizeConfig(
            containerSize = 96.dp,
            iconSize = 36.dp
        )
        else -> FABSizeConfig(
            containerSize = 56.dp,
            iconSize = 24.dp
        )
    }
}

@Composable
private fun getFABTheme(variant: FABVariant): FABColors {
    val colors = AppTheme.colors
    return when (variant) {
        FABVariant.Filled -> FABColors(
            containerColor = colors.brandSurfaceDefault,
            contentColor = colors.brandContentDefault,
            borderColor = Color.Transparent,
            disabledContainerColor = colors.baseSurfaceDisabled,
            disabledContentColor = colors.baseContentDisabled
        )
        FABVariant.Tonal -> FABColors(
            containerColor = colors.brandSurfaceSubtle,
            contentColor = colors.brandContentDefault,
            borderColor = Color.Transparent,
            disabledContainerColor = colors.baseSurfaceDisabled,
            disabledContentColor = colors.baseContentDisabled
        )
        FABVariant.Outlined -> FABColors(
            containerColor = Color.Transparent,
            contentColor = colors.brandContentDefault,
            borderColor = colors.brandBorderDefault,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colors.baseContentDisabled
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaFAB - Floating Action Button component
 *
 * A floating action button (FAB) for primary actions, supporting mini,
 * standard, and extended variants.
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Standard FAB
 * PixaFAB(
 *     icon = painterResource(R.drawable.ic_add),
 *     onClick = { }
 * )
 *
 * // Extended FAB with label
 * PixaFAB(
 *     icon = painterResource(R.drawable.ic_create),
 *     label = "Compose",
 *     onClick = { }
 * )
 *
 * // Mini FAB
 * PixaFAB(
 *     icon = painterResource(R.drawable.ic_edit),
 *     onClick = { },
 *     size = SizeVariant.Medium
 * )
 * ```
 *
 * @param icon Required icon painter
 * @param onClick Required click callback
 * @param modifier Modifier for styling
 * @param label Optional label text for extended FAB
 * @param size Size variant (Medium=48dp mini, Large=56dp standard, Huge=96dp large)
 * @param variant Visual style variant (default: Filled)
 * @param enabled Whether the button is enabled
 * @param colors Custom colors
 * @param contentDescription Accessibility description
 */
@Composable
fun PixaFAB(
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    size: SizeVariant = SizeVariant.Large,
    variant: FABVariant = FABVariant.Filled,
    enabled: Boolean = true,
    colors: FABColors = FABColors(),
    contentDescription: String? = null
) {
    val sizeConfig = getFABSizeConfig(size)
    val themeColors = getFABTheme(variant)

    val isExtended = label != null
    val expansionFraction by animateFloatAsState(
        targetValue = if (isExtended) 1f else 0f,
        animationSpec = AnimationUtils.indicatorSpring,
        label = "fabExpansion"
    )

    val containerColor by animateColorAsState(
        targetValue = if (!enabled) themeColors.disabledContainerColor
        else if (colors.containerColor != Color.Unspecified) colors.containerColor
        else themeColors.containerColor,
        label = "fabContainer"
    )

    val contentColor by animateColorAsState(
        targetValue = if (!enabled) themeColors.disabledContentColor
        else if (colors.contentColor != Color.Unspecified) colors.contentColor
        else themeColors.contentColor,
        label = "fabContent"
    )

    val borderColor = if (!enabled) themeColors.disabledContainerColor
    else if (colors.borderColor != Color.Unspecified) colors.borderColor
    else themeColors.borderColor

    val shape: Shape = CircleShape
    val fabElevation = 6.dp

    Row(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription?.let { this.contentDescription = it }
            }
            .height(sizeConfig.containerSize)
            .widthIn(min = sizeConfig.containerSize)
            .shadow(fabElevation, shape)
            .clip(shape)
            .background(containerColor)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(1.dp, borderColor, shape)
                } else Modifier
            )
            .clickable(
                enabled = enabled,
                role = Role.Button,
                indication = pixaRipple(bounded = true, color = contentColor.copy(alpha = 0.12f)),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(start = 16.dp, end = if (isExtended) 20.dp else 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        PixaIcon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(sizeConfig.iconSize),
            tint = contentColor
        )

        if (isExtended || expansionFraction > 0.01f) {
            Spacer(modifier = Modifier.width(8.dp * expansionFraction))
            Text(
                text = label ?: "",
                style = AppTheme.typography.labelLarge,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alpha(expansionFraction)
            )
        }
    }
}

// ============================================================================
// Convenience Variants
// ============================================================================

@Composable
fun MiniFAB(
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PixaFAB(
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        size = SizeVariant.Medium
    )
}

@Composable
fun StandardFAB(
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PixaFAB(
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        size = SizeVariant.Large
    )
}

@Composable
fun ExtendedFAB(
    icon: Painter,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PixaFAB(
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        label = label,
        size = SizeVariant.Large
    )
}
