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
 * Link size, mapped from Uber Base's three fixed Link size variants. Each
 * tier reuses [com.pixamob.pixacompose.theme.TextTypography]'s `action*`
 * family for fontSize/lineHeight/letterSpacing (the only Pixa tokens whose
 * numbers already line up with Uber's 14/20, 16/20, 18/24 pairings) — see
 * [getLinkSizeConfig] for why the weight is overridden on top of that base.
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
 * Uber Base pins Link to fontSize/lineHeight pairs of 14/20, 16/20, 18/24 —
 * numbers that only exist in Pixa's `action*` typography family
 * ([com.pixamob.pixacompose.theme.TextTypography.actionSmall]/`actionMedium`/`actionLarge`),
 * not in `label*` (a different 10/12/14 scale). Those `action*` tokens are
 * [FontWeight.W700] (bold, tuned for filled button labels); Uber Base
 * mandates Link stay Semibold ([FontWeight.W600]) specifically, so the
 * weight is overridden on top of the reused base rather than introducing a
 * parallel raw `TextStyle` — fontSize/lineHeight/letterSpacing remain fully
 * token-sourced, only the one attribute the spec calls out differently changes.
 */
@Composable
private fun getLinkSizeConfig(size: LinkSize): LinkSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        LinkSize.Small -> LinkSizeConfig(
            textStyle = { typography.actionSmall.copy(fontWeight = FontWeight.W600) },
            underlineThickness = HierarchicalSize.Border.Small // 1.5dp — matches Uber's 1.5px minimum
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
 * Resolves Link's fixed color ladder. Uber Base scopes Link to exactly the
 * neutral `contentPrimary`/`contentSecondary`/`contentStateDisabled` +
 * `borderAccent` tokens (no brand/accent fill is specified for the enabled
 * state) — mapped onto Pixa's closest-named `base` content roles and the
 * `accent` group's border role for the focus ring.
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
 * PixaLink — inline, text-based navigation control. Migrated from Uber
 * Base's Link spec.
 *
 * ### Anatomy
 * A single text label with a mandatory underline drawn beneath it (1.5dp,
 * inside-aligned — [HierarchicalSize.Border.Small], matching Uber Base's
 * 1.5px minimum stroke). Unlike [PixaButton], Link has no icon/leading-
 * accessory slots and no background/border chrome — Uber Base explicitly
 * scopes Link to text + underline only.
 *
 * ### Variants
 * [LinkSize.Small]/[LinkSize.Medium]/[LinkSize.Large] — see [getLinkSizeConfig].
 * There is no visual-hierarchy axis (no Filled/Tonal/Ghost): Uber Base
 * defines exactly one Link style, differentiated only by size.
 *
 * ### States
 * Enabled (`contentPrimary`), hover (`contentSecondary` — only fires on
 * platforms that report pointer hover, e.g. desktop/web targets), focus
 * (`contentPrimary` + a 2dp `borderAccent` outline via [HierarchicalSize.Border.Medium]
 * on an [AppTheme.shapes.pill] ring — Link's own 20px-corner-radius container
 * has no exact [HierarchicalSize.Radius] tier, and `pill` renders the same
 * fully-rounded stadium shape at Link's small footprint, so it's reused
 * instead of introducing a new one-off radius token), [visited] (caller-
 * supplied — Compose has no navigation-history concept, so "has this been
 * visited" is state the host app must track and pass in, same precedent as
 * [PixaButton]'s caller-driven `selected`), disabled (`contentStateDisabled`,
 * not clickable).
 *
 * ### Customization
 * Deliberately narrow, per Uber Base's "customization boundaries": no
 * variant/shape API, [customColors] only overrides the fixed token ladder
 * (not the color *categories* — Link is still always contentPrimary-class,
 * never brand-colored), font weight and underline are never optional.
 *
 * ### Usage notes
 * - Use Link for navigation only (external sites, page sections, anchors,
 *   `mailto:`/`tel:`); use [PixaButton] for actions (submit, save, open a
 *   dialog) — not runtime-enforced, a content/IA rule per Uber Base.
 * - Link text should be descriptive in isolation for screen readers; avoid
 *   embedding links mid-sentence for non-English audiences.
 * - No minimum touch target is specified on desktop; on mobile this
 *   composable pads its hit area up to [HierarchicalSize.TouchTarget.Small]
 *   (WCAG minimum) without inflating the visible text/underline, matching
 *   Uber Base's "mobile requires alignment with minimum touch targets" note.
 *
 * @param text The link's visible label
 * @param onClick Callback when the link is activated
 * @param modifier Modifier for the link
 * @param size Size variant (Default: [LinkSize.Medium])
 * @param enabled Whether the link is interactive (Default: true)
 * @param visited Whether to render the visited-state color (Default: false, caller-tracked)
 * @param description Accessibility description/hint appended to the "Link" trait announcement
 * @param customColors Optional custom [LinkColors] to override theme defaults
 * @param enforceMinTouchTarget Whether to pad the hit area to the WCAG touch minimum on mobile without resizing the visible text (Default: true)
 *
 * @sample
 * ```
 * PixaLink(
 *     text = "View our privacy policy",
 *     onClick = { }
 * )
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
