package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.utils.AnimationUtils

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Link size — Small, Medium, Large. Each tier reuses [AppTheme.typography]'s
 * `action*` family for fontSize/lineHeight/letterSpacing (see [getLinkSizeConfig]
 * for the weight override).
 */
enum class LinkSize {
    Small,
    Medium,
    Large
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class LinkSizeConfig(
    val textStyle: @Composable () -> TextStyle,
    val underlineThickness: androidx.compose.ui.unit.Dp
)

@Immutable
@Stable
data class LinkColors(
    val default: Color,
    val hover: Color,
    val visited: Color,
    val disabled: Color,
    val focusBorder: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Resolves size-tier typography for [LinkSize].
 *
 * Link uses `action*` typography tokens (actionSmall/actionMedium/actionLarge)
 * for fontSize/lineHeight/letterSpacing, with [FontWeight.W600] (Semibold)
 * overriding the default W700 bold weight of those tokens.
 */
@Composable
private fun getLinkSizeConfig(size: LinkSize): LinkSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        LinkSize.Small -> LinkSizeConfig(
            textStyle = { typography.actionSmall.copy(fontWeight = FontWeight.W600) },
            underlineThickness = HierarchicalSize.Border.Small // 1.5dp minimum stroke
        )

        LinkSize.Medium -> LinkSizeConfig(
            textStyle = { typography.actionMedium.copy(fontWeight = FontWeight.W600) },
            underlineThickness = HierarchicalSize.Border.Small
        )

        LinkSize.Large -> LinkSizeConfig(
            textStyle = { typography.actionLarge.copy(fontWeight = FontWeight.W600) },
            underlineThickness = HierarchicalSize.Border.Small
        )
    }
}

/**
 * Resolves Link's fixed color ladder: neutral `base` content roles for text
 * states and `accentBorderDefault` for the focus ring.
 */
@Composable
private fun getLinkTheme(colors: ColorPalette): LinkColors = LinkColors(
    default = colors.baseContentTitle,       // contentPrimary
    hover = colors.baseContentSubtitle,      // contentSecondary
    visited = colors.baseContentSubtitle,    // contentSecondary
    disabled = colors.baseContentDisabled,   // contentStateDisabled
    focusBorder = colors.accentBorderDefault // borderAccent
)

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaLink — inline, text-based navigation control.
 *
 * ### Anatomy
 * A single text label with a mandatory 1.5dp underline drawn beneath it
 * ([HierarchicalSize.Border.Small]). No icon/accessory slots, no background
 * or border chrome — text + underline only.
 *
 * ### Variants
 * [LinkSize.Small]/[LinkSize.Medium]/[LinkSize.Large] — no visual-hierarchy
 * axis; differentiated only by size.
 *
 * ### States
 * Enabled, hover (desktop/web only), focus (2dp accent outline via [AppTheme.shapes.pill]),
 * visited (caller-tracked), disabled (not clickable).
 *
 * ### Customization
 * Deliberately narrow: no variant/shape API, [customColors] only overrides
 * the color ladder (not the color categories), font weight and underline
 * are fixed.
 *
 * ### Usage notes
 * - Use Link for navigation only (external sites, page sections, anchors);
 *   use [PixaButton] for actions (submit, save, open a dialog).
 * - Link text should be descriptive in isolation for screen readers; avoid
 *   embedding links mid-sentence for non-English audiences.
 * - On mobile, padded to [HierarchicalSize.TouchTarget.Small] (WCAG minimum)
 *   without inflating the visible text/underline.
 *
 * @param text The link's visible label
 * @param onClick Callback when the link is activated
 * @param modifier Modifier for the link
 * @param size Size variant (Default: [LinkSize.Medium])
 * @param enabled Whether the link is interactive (Default: true)
 * @param visited Whether to render the visited-state color (Default: false, caller-tracked)
 * @param description Accessibility description/hint appended to the "Link" trait announcement
 * @param customColors Optional custom [LinkColors] to override theme defaults
 * @param enforceMinTouchTarget Whether to pad the hit area to WCAG touch minimum on mobile (Default: true)
 *
 * @sample
 * ```
 * PixaLink(text = "View our privacy policy", onClick = { })
 * ```
 */
@Composable
fun PixaLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: LinkSize = LinkSize.Medium,
    enabled: Boolean = true,
    visited: Boolean = false,
    description: String? = null,
    customColors: LinkColors? = null,
    enforceMinTouchTarget: Boolean = true,
) {
    val sizeConfig = getLinkSizeConfig(size)
    val colors = customColors ?: getLinkTheme(AppTheme.colors)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val targetColor = when {
        !enabled -> colors.disabled
        isHovered -> colors.hover
        visited -> colors.visited
        else -> colors.default
    }

    val contentColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = AnimationUtils.standardTween(150),
        label = "link_content"
    )

    val touchTargetModifier = if (enforceMinTouchTarget) {
        Modifier.sizeIn(minHeight = HierarchicalSize.TouchTarget.Small)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .then(touchTargetModifier)
            .focusable(interactionSource = interactionSource, enabled = enabled)
            .hoverable(interactionSource = interactionSource, enabled = enabled)
            .then(
                if (isFocused && enabled) {
                    Modifier.border(HierarchicalSize.Border.Medium, colors.focusBorder, AppTheme.shapes.pill)
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
                onClickLabel = description
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = text,
            modifier = Modifier
                .wrapContentSize()
                .drawBehind {
                    val strokeWidth = sizeConfig.underlineThickness.toPx()
                    val drawSize = this.size
                    drawLine(
                        color = contentColor,
                        start = Offset(0f, drawSize.height - strokeWidth / 2f),
                        end = Offset(drawSize.width, drawSize.height - strokeWidth / 2f),
                        strokeWidth = strokeWidth
                    )
                },
            style = sizeConfig.textStyle().copy(color = contentColor)
        )
    }
}
