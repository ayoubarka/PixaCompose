package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.ChipVariant
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.theme.forVariant
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Emphasis tier. [Primary] for promotions/status (use sparingly).
 * [Secondary] for properties/categories (default). [Tertiary] for lowest-emphasis outlined tags.
 */
enum class TagHierarchy {
    Primary,
    Secondary,
    Tertiary
}

/**
 * Tag color. Maps to PixaCompose's semantic color groups as the closest
 * approximation to a hue-primitive palette.
 * [Neutral] (gray), [Error] (red), [Warning] (orange/yellow), [Success] (green),
 * [Info] (blue), [Accent] (magenta/purple), [Brand] (brand color).
 */
enum class TagColor {
    Neutral,
    Brand,
    Accent,
    Info,
    Success,
    Warning,
    Error
}

/**
 * Tag content model. [Display] for static informational tags;
 * [Selection] for interactive selectable/dismissible filter tags.
 */
enum class TagType {
    /** Non-interactive, informational — status/property/category labels. */
    Display,
    /** Interactive — selectable and/or dismissible, used for filtering. */
    Selection
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class TagColors(
    val background: Color,
    val content: Color,
    val border: Color = Color.Transparent
)

@Immutable
@Stable
data class TagStateColors(
    val default: TagColors,
    val selected: TagColors,
    val disabled: TagColors
)

@Immutable
@Stable
private data class TagSizeConfig(
    val height: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    val iconSpacing: Dp,
    val textStyle: @Composable () -> TextStyle
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Icon size is deliberately one [HierarchicalSize.Icon] tier below the tag's
 * own size tier — an icon at the raw ladder value would dominate a compact
 * rectangular tag. Matches the same tier-shift [ChipVariant] siblings use.
 */
private fun tagIconSize(size: SizeVariant): Dp = when (size) {
    SizeVariant.None -> 0.dp
    SizeVariant.Nano -> HierarchicalSize.Icon.Nano
    SizeVariant.Compact, SizeVariant.Small -> HierarchicalSize.Icon.Compact
    SizeVariant.Medium -> HierarchicalSize.Icon.Small
    SizeVariant.Large, SizeVariant.Huge -> HierarchicalSize.Icon.Medium
    SizeVariant.Massive -> HierarchicalSize.Icon.Large
}

/** Tag sizing uses a 3-tier Small/Medium/Large ladder via [HierarchicalSize.Chip.forVariant]. */
@Composable
private fun getTagSizeConfig(size: SizeVariant): TagSizeConfig {
    val typography = AppTheme.typography
    return TagSizeConfig(
        height = HierarchicalSize.Chip.forVariant(size),
        horizontalPadding = HierarchicalSize.Padding.forVariant(size),
        iconSize = tagIconSize(size),
        iconSpacing = HierarchicalSize.Spacing.forVariant(size),
        textStyle = {
            when (size) {
                SizeVariant.None, SizeVariant.Nano, SizeVariant.Compact, SizeVariant.Small -> typography.labelSmall
                SizeVariant.Medium -> typography.labelMedium
                SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> typography.labelLarge
            }
        }
    )
}

private data class TagGroupTokens(
    val surfaceSubtle: Color,
    val surfaceDefault: Color,
    val borderDefault: Color,
    val contentDefault: Color
)

private fun tagGroupTokens(color: TagColor, colors: ColorPalette): TagGroupTokens = when (color) {
    TagColor.Neutral -> TagGroupTokens(colors.baseSurfaceSubtle, colors.baseSurfaceDefault, colors.baseBorderDefault, colors.baseContentBody)
    TagColor.Brand -> TagGroupTokens(colors.brandSurfaceSubtle, colors.brandSurfaceDefault, colors.brandBorderDefault, colors.brandContentDefault)
    TagColor.Accent -> TagGroupTokens(colors.accentSurfaceSubtle, colors.accentSurfaceDefault, colors.accentBorderDefault, colors.accentContentDefault)
    TagColor.Info -> TagGroupTokens(colors.infoSurfaceSubtle, colors.infoSurfaceDefault, colors.infoBorderDefault, colors.infoContentDefault)
    TagColor.Success -> TagGroupTokens(colors.successSurfaceSubtle, colors.successSurfaceDefault, colors.successBorderDefault, colors.successContentDefault)
    TagColor.Warning -> TagGroupTokens(colors.warningSurfaceSubtle, colors.warningSurfaceDefault, colors.warningBorderDefault, colors.warningContentDefault)
    TagColor.Error -> TagGroupTokens(colors.errorSurfaceSubtle, colors.errorSurfaceDefault, colors.errorBorderDefault, colors.errorContentDefault)
}

/**
 * Resolves [TagHierarchy] × [TagColor] into state colors.
 * Primary: solid surface + contrast text. Secondary: subtle tonal surface + group content.
 * Tertiary: transparent + outlined. Selected always resolves to Primary look.
 * Disabled is identical across hierarchies (Tertiary keeps its outline).
 */
@Composable
private fun getTagTheme(hierarchy: TagHierarchy, color: TagColor, colors: ColorPalette): TagStateColors {
    val tokens = tagGroupTokens(color, colors)

    val primary = TagColors(background = tokens.surfaceDefault, content = colors.baseContentNegative)
    val secondary = TagColors(background = tokens.surfaceSubtle, content = tokens.contentDefault)
    val tertiary = TagColors(background = Color.Transparent, content = tokens.contentDefault, border = tokens.borderDefault)

    val default = when (hierarchy) {
        TagHierarchy.Primary -> primary
        TagHierarchy.Secondary -> secondary
        TagHierarchy.Tertiary -> tertiary
    }

    val disabled = TagColors(
        background = if (hierarchy == TagHierarchy.Tertiary) Color.Transparent else colors.baseSurfaceDisabled,
        content = colors.baseContentDisabled,
        border = if (hierarchy == TagHierarchy.Tertiary) colors.baseBorderDisabled else Color.Transparent
    )

    return TagStateColors(default = default, selected = primary, disabled = disabled)
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL TAG
// ════════════════════════════════════════════════════════════════════════════

/** Hover/pressed state-layer scrim at 4% / 8% black opacity. */
@Composable
private fun BoxScope.TagOverlayScrim(alpha: Float, shape: Shape) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .clip(shape)
            .background(Color.Black.copy(alpha = alpha))
    )
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaTag — displays status, properties, or categories, optionally selectable/dismissible.
 *
 * ### Anatomy
 * Container + [text] label, optional [leadingIcon]/[trailingIcon].
 * [onDismiss] renders a dismiss control (overrides [trailingIcon]) on [TagType.Selection].
 *
 * ### Variants
 * [hierarchy] (Primary/Secondary/Tertiary) × [color] (7 semantic groups — see [TagColor]).
 *
 * ### States
 * Enabled, hover/pressed (4%/8% scrim — [TagType.Selection] only with [onClick]/[onDismiss]),
 * focus (accent outline), [selected] (resolves to Primary look), disabled.
 *
 * ### Sizing
 * [size] resolves through [HierarchicalSize.Chip].
 *
 * ### Customization
 * [backgroundColor]/[contentColor]/[borderColor] override resolved theme colors per state.
 *
 * ### Usage notes
 * Keep labels to 1-2 words, avoid verbs, never wrap (`maxLines = 1`).
 * Tag always hugs content — avoid `fillMaxWidth`.
 *
 * @param text Tag label (optional — icon-only tags supported)
 * @param modifier Modifier; avoid `fillMaxWidth` — tags hug content
 * @param hierarchy Emphasis tier (Default: Secondary)
 * @param color Color group (Default: Neutral)
 * @param size Size variant (Default: Medium)
 * @param type Display or Selection behavior (Default: Display)
 * @param selected Active selection state within a tag group
 * @param enabled Whether the tag is enabled
 * @param onClick Click handler — only for [TagType.Selection]
 * @param onDismiss Dismiss handler for [TagType.Selection]
 * @param leadingIcon Optional icon before label
 * @param trailingIcon Optional icon after label (ignored when [onDismiss] set)
 * @param backgroundColor Background color override
 * @param contentColor Content/text color override
 * @param borderColor Border color override
 * @param contentDescription Accessibility description; defaults to [text]
 */
@Composable
fun PixaTag(
    text: String? = null,
    modifier: Modifier = Modifier,
    hierarchy: TagHierarchy = TagHierarchy.Secondary,
    color: TagColor = TagColor.Neutral,
    size: SizeVariant = SizeVariant.Medium,
    type: TagType = TagType.Display,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    borderColor: Color? = null,
    contentDescription: String? = null
) {
    val colors = AppTheme.colors
    val config = getTagSizeConfig(size)
    val theme = getTagTheme(hierarchy, color, colors)
    val tagShape = AppTheme.shapes.rounded.forVariant(size)

    val bodyClick = onClick.takeIf { enabled && type == TagType.Selection }
    val isInteractive = enabled && type == TagType.Selection && (onClick != null || onDismiss != null)

    val stateColors = when {
        !enabled -> theme.disabled
        selected -> theme.selected
        else -> theme.default
    }

    val finalColors = if (backgroundColor != null || contentColor != null || borderColor != null) {
        TagColors(
            background = backgroundColor ?: stateColors.background,
            content = contentColor ?: stateColors.content,
            border = borderColor ?: stateColors.border
        )
    } else {
        stateColors
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = modifier
            .height(config.height)
            .clip(tagShape)
            .then(
                if (finalColors.border != Color.Transparent) {
                    Modifier.border(HierarchicalSize.Border.Compact, finalColors.border, tagShape)
                } else {
                    Modifier
                }
            )
            .background(finalColors.background)
            .then(
                if (isFocused && isInteractive) {
                    Modifier.border(HierarchicalSize.Border.Large, colors.accentBorderDefault, tagShape)
                } else {
                    Modifier
                }
            )
            .focusable(interactionSource = interactionSource, enabled = isInteractive)
            .then(
                if (bodyClick != null) {
                    Modifier.clickable(
                        enabled = enabled,
                        onClick = bodyClick,
                        indication = pixaRipple(color = finalColors.content.copy(alpha = 0.12f)),
                        interactionSource = interactionSource
                    )
                } else {
                    Modifier
                }
            )
            .semantics {
                if (type == TagType.Selection) {
                    role = Role.Button
                    this.selected = selected
                }
                contentDescription?.let { this.contentDescription = it } ?: text?.let { this.contentDescription = it }
            }
            .padding(horizontal = config.horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                PixaIcon(
                    painter = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(config.iconSize),
                    tint = finalColors.content
                )
                Spacer(modifier = Modifier.width(config.iconSpacing))
            }

            if (!text.isNullOrBlank()) {
                BasicText(
                    text = text,
                    style = config.textStyle().copy(color = finalColors.content, textAlign = TextAlign.Center),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            if (onDismiss != null && type == TagType.Selection && enabled) {
                Spacer(modifier = Modifier.width(config.iconSpacing))
                Box(
                    modifier = Modifier
                        .size(config.iconSize)
                        .clip(AppTheme.shapes.pill)
                        .clickable(
                            onClick = onDismiss,
                            indication = pixaRipple(bounded = true, color = finalColors.content.copy(alpha = 0.2f)),
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .semantics { this.contentDescription = text?.let { "Remove $it" } ?: "Remove tag" },
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "×",
                        modifier = Modifier.wrapContentSize(Alignment.Center, unbounded = true),
                        style = config.textStyle().copy(color = finalColors.content, textAlign = TextAlign.Center)
                    )
                }
            } else if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(config.iconSpacing))
                PixaIcon(
                    painter = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(config.iconSize),
                    tint = finalColors.content
                )
            }
        }

        when {
            isPressed && isInteractive -> TagOverlayScrim(0.08f, tagShape)
            isHovered && isInteractive -> TagOverlayScrim(0.04f, tagShape)
        }
    }
}
