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
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
 * Tag hierarchy — Uber Base's emphasis ladder: "Primary" (promotions, status,
 * new features — used sparingly per "avoid littering a page with too many
 * primary tags"), "Secondary" (properties/categories of an item — the
 * spec's "more commonly used" tier, so it's the default here), and
 * "Tertiary", which the spec references without detailing further; this
 * implementation treats it as the outlined/lowest-emphasis tier, consistent
 * with the emphasis gradation used elsewhere in the library (e.g. [ChipVariant]'s
 * Filled/Tonal/Outlined ladder).
 */
enum class TagHierarchy {
    Primary,
    Secondary,
    Tertiary
}

/**
 * Tag color. Uber Base assigns Tag colors from 8 hue-primitive tokens
 * (gray/red/orange/yellow/green/blue/purple/magenta) that aren't tied to
 * semantics, "because Tag colors are not assigned to specific semantics."
 * PixaCompose's [ColorPalette] doesn't carry a raw-hue ladder — colors are
 * grouped semantically (`brand`/`accent`/`info`/`success`/`warning`/`error`/`base`,
 * per `CLAUDE.md`) — so this enum reuses those 7 groups as the closest
 * available approximation: [Neutral] for gray, [Error] for red, [Warning] for
 * orange/yellow, [Success] for green, [Info] for blue, [Accent] for
 * magenta/purple (per `theme/Color.kt`'s accent palette being magenta-based),
 * and [Brand] as an additional PixaCompose-specific option Uber's set doesn't have.
 * Introducing a true hue-primitive token system was out of scope for this migration.
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
 * Tag content model. Uber Base: "Text tags are static keywords... to provide
 * information" ([Display]) vs. tags that "allow users to input information
 * and filter content" through selection and a dismiss icon ([Selection]).
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

/** Uber Base's Tag sizing is a 3-tier Small/Medium/Large ladder; other [SizeVariant]
 * entries fall back to the nearest bucket via [HierarchicalSize.Chip.forVariant]. */
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
 * Resolves [TagHierarchy] × [TagColor] into concrete state colors.
 * Primary = solid group surface + `baseContentNegative` (contrast text on a
 * strong fill, same pattern [ChipVariant.Filled] uses on `brandSurfaceDefault`).
 * Secondary = subtle tonal surface + group content. Tertiary = transparent +
 * group border (outlined). Selected always resolves to the Primary look,
 * regardless of hierarchy, to read as "active" within a tag group. Disabled
 * is identical across hierarchies per Uber Base: "visually the same for both
 * primary and secondary Tags" — except Tertiary keeps its outline (disabled border).
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

/** Hover/pressed state-layer scrim — Uber Base's literal "4% / 8% black overlay." */
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
 * PixaTag — a static and actionable component that displays an element's
 * status, key properties, or categories, and can allow users to filter
 * content by selecting/dismissing it.
 *
 * ### Anatomy
 * A required container + [text] label, with optional [leadingIcon]/[trailingIcon].
 * [onDismiss] renders a trailing dismiss control instead of [trailingIcon] on
 * [TagType.Selection] tags, mirroring [ChipVariant]'s dismiss slot.
 *
 * ### Variants
 * [hierarchy] (Primary/Secondary/Tertiary emphasis) × [color] (7 semantic
 * groups approximating Uber's 8 hue-primitive palette — see [TagColor]).
 *
 * ### States
 * Enabled, hover/pressed (4%/8% black scrim — only for [TagType.Selection]
 * with [onClick] or [onDismiss]), focus (3dp `accentBorderDefault` outline,
 * matching Uber's "3px border borderAccent"), [selected] (resolves to the
 * Primary look regardless of [hierarchy]), disabled (identical across
 * hierarchies per spec, Tertiary keeps its outline).
 *
 * ### Sizing
 * [size] resolves through [HierarchicalSize.Chip] (Uber's Small/Medium/Large
 * ladder, with the remaining [SizeVariant] tiers bucketed to the nearest fit).
 *
 * ### Adaptive behavior
 * Out of scope — Uber Base's only responsive note is that *clustered* tags
 * wrap to the next row, which is a caller-side `FlowRow` layout concern, not
 * a per-tag sizing concern this component owns.
 *
 * ### Customization
 * [backgroundColor]/[contentColor]/[borderColor] override the resolved
 * theme colors per state, matching [ChipVariant]'s override pattern.
 *
 * ### Usage notes
 * Uber Base: keep labels to 1–2 words, avoid verbs, never wrap to a second
 * line (enforced here via `maxLines = 1` + ellipsis), and don't stretch the
 * container to fill a layout — this composable intentionally never applies
 * `fillMaxWidth`, so it always hugs its content.
 *
 * @param text Tag label (optional — supports icon-only tags)
 * @param modifier Modifier for customization; avoid `fillMaxWidth` — tags hug content per spec
 * @param hierarchy Emphasis tier (default: [TagHierarchy.Secondary], the spec's "more commonly used" tier)
 * @param color Semantic color group (default: [TagColor.Neutral])
 * @param size Size variant (default: [SizeVariant.Medium])
 * @param type Display vs. selection behavior (default: [TagType.Display])
 * @param selected Whether the tag is the active selection within a tag group
 * @param enabled Whether the tag is enabled
 * @param onClick Click handler — only wired for [TagType.Selection]
 * @param onDismiss Dismiss handler — renders a trailing dismiss control for [TagType.Selection]
 * @param leadingIcon Optional icon before the label
 * @param trailingIcon Optional icon after the label (ignored when [onDismiss] is set)
 * @param backgroundColor Optional background color override
 * @param contentColor Optional content/text color override
 * @param borderColor Optional border color override
 * @param contentDescription Accessibility description; defaults to [text]. Uber Base: actionable tags read as buttons
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
        CompositionLocalProvider(LocalContentColor provides finalColors.content) {
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
        }

        when {
            isPressed && isInteractive -> TagOverlayScrim(0.08f, tagShape)
            isHovered && isInteractive -> TagOverlayScrim(0.04f, tagShape)
        }
    }
}
