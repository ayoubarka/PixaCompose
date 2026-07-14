package com.pixamob.pixacompose.components.display

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.feedback.SkeletonListItem
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.utils.AnimationUtils

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Configuration variant, mapped from Uber Base's "Configuration Variants":
 * - [FullWidth]: a full-bleed row with a single continuous background tint
 *   for its active/selected state — no border, no corner radius.
 * - [Selection]: "16px insets with 12px rounded corners, outlined stroke" —
 *   an inset, bordered row used for selection patterns (radio/checkbox lists).
 */
enum class ListItemVariant {
    FullWidth,
    Selection
}

/**
 * Row density, mapped from Uber Base's "Size Variants": [Standard] (64dp
 * minimum height) and [Compact] (48dp minimum height, always fits its
 * tallest content). This is a dedicated 2-tier enum rather than the generic
 * 8-tier [com.pixamob.pixacompose.theme.SizeVariant] because Uber Base's List
 * Item spec itself only defines these two named tiers (same reasoning
 * [DividerVariant] uses for Cell/Module rather than adopting `SizeVariant`).
 */
enum class ListItemDensity {
    Standard,
    Compact
}

/**
 * Leading/trailing icon-tier sizing, mapped from Uber Base's literal
 * 16/24/36px icon ladder. [Small] has no exact matching [HierarchicalSize.Icon]
 * tier (the ladder jumps 14→18dp around it) so it resolves to the closest
 * existing token (18dp) rather than introducing a new one-off token — the
 * same closest-token approach [com.pixamob.pixacompose.components.display.PixaAvatar]
 * already uses for its own Uber-derived icon sizes.
 */
enum class ListItemIconSize {
    Small,
    Medium,
    Large
}

/**
 * Leading content slot, mapped from Uber Base's "Leading Content Options".
 */
sealed class ListItemLeading {
    /** No artwork — text only, row gets the plain 16dp side inset. */
    data object Off : ListItemLeading()

    /**
     * A 16/24/36px icon, centered in a fixed-width container matching the
     * row's own minimum height (Uber Base's literal "centered in 64px
     * containers" at Standard density).
     */
    data class Icon(
        val size: ListItemIconSize = ListItemIconSize.Medium,
        val content: @Composable () -> Unit
    ) : ListItemLeading()

    /**
     * Artwork larger than 36dp rendered at its natural size — avatars,
     * badges, custom illustrations. Uber Base gives this a plain 16dp side
     * inset rather than a fixed centering container (unlike [Icon]).
     */
    data class Artwork(val content: @Composable () -> Unit) : ListItemLeading()
}

/**
 * Trailing content slot, mapped from Uber Base's "Trailing Content Options".
 * Each case carries its own alignment/inset rule per the spec, since they
 * differ (fixed-width centered container vs. right-aligned-with-padding vs.
 * truly flush).
 */
sealed class ListItemTrailing {
    data object Off : ListItemTrailing()

    /**
     * A small 24dp icon/indicator (e.g. a chevron), centered in a
     * fixed-width container — read-only, the row itself is the tap target.
     * Per Uber Base: restrict to navigation into a subset of options, never
     * as a generic "this row is tappable" affordance.
     */
    data class Icon(val content: @Composable () -> Unit) : ListItemTrailing()

    /**
     * An independently-clickable control (switch, icon button) — its own tap
     * target distinct from the row's, right-aligned with 16dp padding from
     * the row's edge. Per Uber Base, give it its own background (e.g.
     * [com.pixamob.pixacompose.components.actions.IconButtonVariant.Tonal])
     * so the separate tap zone reads clearly ("gray background protection").
     */
    data class Control(val content: @Composable () -> Unit) : ListItemTrailing()

    /** Right-aligned trailing text, 16dp padding from the row's edge. */
    data class Caption(val text: String) : ListItemTrailing()

    /** A flush-right button/pill — zero inset from the row's edge. */
    data class Flush(val content: @Composable () -> Unit) : ListItemTrailing()
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class ListItemColors(
    val background: Color,
    val border: Color,
    val borderWidth: Dp,
    val titleContent: Color,
    val bodyContent: Color,
    val captionContent: Color
)

@Immutable
@Stable
data class ListItemStateColors(
    val default: ListItemColors,
    val active: ListItemColors,
    val disabled: ListItemColors,
    val hoverOverlay: Color,
    val pressedOverlay: Color,
    val focusBorderColor: Color
)

@Immutable
@Stable
private data class ListItemLayout(
    val minHeight: Dp,
    val containerWidth: Dp,
    val verticalPadding: Dp,
    val horizontalPadding: Dp,
    val labelSpacing: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Resolves density-driven layout numbers. Every value here is a direct,
 * exact token match to a literal Uber Base figure except
 * [ListItemLayout.containerWidth] at [ListItemDensity.Compact], which Uber
 * Base doesn't spec separately — it's derived from the row's own minimum
 * height so icon-centering stays consistent with how [ListItemDensity.Standard]
 * derives it (64px container == 64px row height).
 */
private fun listItemLayoutFor(density: ListItemDensity): ListItemLayout = when (density) {
    ListItemDensity.Standard -> ListItemLayout(
        minHeight = HierarchicalSize.ListItem.Large,        // 64dp — literal Standard minimum
        containerWidth = HierarchicalSize.ListItem.Large,   // 64dp — literal icon-centering container
        verticalPadding = HierarchicalSize.Padding.Large,    // 16dp — literal Standard top/bottom padding
        horizontalPadding = HierarchicalSize.Padding.Large,  // 16dp — literal side padding (reused for no-artwork/artwork/trailing insets, all spec'd at the same value)
        labelSpacing = HierarchicalSize.Spacing.Compact      // 4dp — literal multi-line label spacing
    )
    ListItemDensity.Compact -> ListItemLayout(
        minHeight = HierarchicalSize.ListItem.Small,         // 48dp — literal Compact minimum
        containerWidth = HierarchicalSize.ListItem.Small,    // 48dp — derived from row min height, see kdoc above
        verticalPadding = HierarchicalSize.Padding.Medium,   // 12dp — literal Compact top/bottom padding
        horizontalPadding = HierarchicalSize.Padding.Large,
        labelSpacing = HierarchicalSize.Spacing.Compact
    )
}

private fun ListItemIconSize.toDp(): Dp = when (this) {
    ListItemIconSize.Small -> HierarchicalSize.Icon.Small   // 18dp, closest token to Uber's literal 16dp
    ListItemIconSize.Medium -> HierarchicalSize.Icon.Medium // 24dp, exact
    ListItemIconSize.Large -> HierarchicalSize.Icon.Huge    // 36dp, exact
}

/**
 * Resolves the fixed color/border ladder per [ListItemVariant]. Border
 * widths land exactly on existing [HierarchicalSize.Border] tiers — 2px
 * ("borderOpaque") is [HierarchicalSize.Border.Medium], 3px
 * ("borderSelected"/focus) is [HierarchicalSize.Border.Large] — the same
 * pairing [com.pixamob.pixacompose.components.display.PixaTile] uses for its
 * own selected state, and the same focus-ring treatment
 * [com.pixamob.pixacompose.components.display.PixaAvatar] uses. Hover/pressed
 * are Uber Base's literal 4%/8% black state-layer overlays — fixed
 * percentages, not theme tokens, matching Tile/Avatar precedent.
 */
@Composable
private fun getListItemTheme(variant: ListItemVariant, colors: ColorPalette): ListItemStateColors {
    val titleContent = colors.baseContentTitle
    val bodyContent = colors.baseContentBody
    val captionContent = colors.baseContentCaption
    val disabledContent = colors.baseContentDisabled

    return when (variant) {
        ListItemVariant.FullWidth -> ListItemStateColors(
            default = ListItemColors(
                background = Color.Transparent,
                border = Color.Transparent,
                borderWidth = HierarchicalSize.Border.None,
                titleContent = titleContent,
                bodyContent = bodyContent,
                captionContent = captionContent
            ),
            active = ListItemColors(
                background = colors.brandSurfaceSubtle,
                border = Color.Transparent,
                borderWidth = HierarchicalSize.Border.None,
                titleContent = titleContent,
                bodyContent = bodyContent,
                captionContent = captionContent
            ),
            disabled = ListItemColors(
                background = Color.Transparent,
                border = Color.Transparent,
                borderWidth = HierarchicalSize.Border.None,
                titleContent = disabledContent,
                bodyContent = disabledContent,
                captionContent = disabledContent
            ),
            hoverOverlay = Color.Black.copy(alpha = 0.04f),
            pressedOverlay = Color.Black.copy(alpha = 0.08f),
            focusBorderColor = colors.accentBorderDefault
        )

        ListItemVariant.Selection -> ListItemStateColors(
            default = ListItemColors(
                background = colors.baseSurfaceDefault,
                border = colors.baseBorderDefault,
                borderWidth = HierarchicalSize.Border.Medium, // 2px, "borderOpaque"
                titleContent = titleContent,
                bodyContent = bodyContent,
                captionContent = captionContent
            ),
            active = ListItemColors(
                background = colors.brandSurfaceSubtle,
                border = colors.brandBorderFocus, // "borderSelected" — same token PixaTile uses
                borderWidth = HierarchicalSize.Border.Large, // 3px
                titleContent = titleContent,
                bodyContent = bodyContent,
                captionContent = captionContent
            ),
            disabled = ListItemColors(
                background = colors.baseSurfaceDefault,
                border = colors.baseBorderDisabled,
                borderWidth = HierarchicalSize.Border.Medium,
                titleContent = disabledContent,
                bodyContent = disabledContent,
                captionContent = disabledContent
            ),
            hoverOverlay = Color.Black.copy(alpha = 0.04f),
            pressedOverlay = Color.Black.copy(alpha = 0.08f),
            focusBorderColor = colors.accentBorderDefault
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL LIST ITEM
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun BoxScope.ListItemOverlayScrim(color: Color, shape: Shape) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .clip(shape)
            .background(color)
    )
}

@Composable
private fun RowScope.ListItemLeadingSlot(leading: ListItemLeading, layout: ListItemLayout) {
    when (leading) {
        ListItemLeading.Off -> Unit

        is ListItemLeading.Icon -> Box(
            modifier = Modifier.width(layout.containerWidth),
            contentAlignment = Alignment.Center
        ) {
            leading.content()
        }

        is ListItemLeading.Artwork -> {
            Box { leading.content() }
            Spacer(modifier = Modifier.width(layout.horizontalPadding))
        }
    }
}

@Composable
private fun RowScope.ListItemTrailingSlot(
    trailing: ListItemTrailing,
    layout: ListItemLayout,
    colors: ListItemColors
) {
    when (trailing) {
        ListItemTrailing.Off -> Unit

        is ListItemTrailing.Icon -> Box(
            modifier = Modifier.width(layout.containerWidth),
            contentAlignment = Alignment.Center
        ) {
            trailing.content()
        }

        is ListItemTrailing.Control -> trailing.content()

        is ListItemTrailing.Caption -> BasicText(
            text = trailing.text,
            style = AppTheme.typography.captionRegular.copy(color = colors.captionContent),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        is ListItemTrailing.Flush -> trailing.content()
    }
}

@Composable
private fun RowScope.ListItemTextColumn(
    title: String,
    subtitle: String?,
    caption: String?,
    singleLine: Boolean,
    colors: ListItemColors,
    labelSpacing: Dp
) {
    val maxLines = if (singleLine) 1 else Int.MAX_VALUE
    val overflow = if (singleLine) TextOverflow.Ellipsis else TextOverflow.Clip

    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(labelSpacing)
    ) {
        BasicText(
            text = title,
            style = AppTheme.typography.bodyBold.copy(color = colors.titleContent),
            maxLines = maxLines,
            overflow = overflow
        )
        subtitle?.let {
            BasicText(
                text = it,
                style = AppTheme.typography.bodyRegular.copy(color = colors.bodyContent),
                maxLines = maxLines,
                overflow = overflow
            )
        }
        caption?.let {
            BasicText(
                text = it,
                style = AppTheme.typography.captionRegular.copy(color = colors.captionContent),
                maxLines = maxLines,
                overflow = overflow
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaListItem — a horizontal row for scanning stacked, related content.
 * Migrated from Uber Base's List Item spec, one of Uber's most-used
 * components ("over 80% of their UI").
 *
 * ### Purpose
 * Vertically stacked rows of text/images arranged horizontally, for
 * efficient scanning — search results, settings, navigation, selection
 * lists (radio/checkbox patterns).
 *
 * ### Anatomy
 * Optional [leading] slot ([ListItemLeading.Off]/[ListItemLeading.Icon]/[ListItemLeading.Artwork]),
 * up to 3 stacked text labels ([title] required, [subtitle], [caption] —
 * Uber Base's "label, paragraph, support paragraph"), and an optional
 * [trailing] slot ([ListItemTrailing.Icon]/[ListItemTrailing.Control]/[ListItemTrailing.Caption]/[ListItemTrailing.Flush]).
 * Cell/section dividers between rows are intentionally **not** rendered by
 * this component — a single row can't know if it's last in its list, and
 * Uber Base itself says never to place a divider after the last cell/section.
 * Compose them at the call site with [PixaDivider] instead:
 * `PixaDivider(variant = DividerVariant.Cell)` between rows,
 * `PixaDivider(variant = DividerVariant.Module)` between sections.
 *
 * ### Variants
 * [ListItemVariant.FullWidth] (full-bleed, tinted active state, no border)
 * vs [ListItemVariant.Selection] (16dp-inset, 12dp-rounded, bordered — for
 * selection patterns).
 *
 * ### States
 * default, hover/pressed (4%/8% black overlay), focus (3dp `accentBorderDefault`
 * ring), active/[selected] (brand-tinted background, or a `brandBorderFocus`
 * border for [ListItemVariant.Selection]), disabled (dimmed content, [enabled] = false),
 * preloading ([loading] → [SkeletonListItem]).
 *
 * ### Sizing
 * [density] drives row height/padding/label-spacing/leading-container-width
 * through [HierarchicalSize] — [ListItemDensity.Standard] (64dp) and
 * [ListItemDensity.Compact] (48dp), Uber Base's own two named tiers, both
 * already at/above the 48dp WCAG touch-target floor by construction.
 * [leading]/[trailing] icon sizes use [ListItemIconSize] (16/24/36dp ladder).
 *
 * ### Adaptive behavior
 * Out of scope for the row itself — Uber Base's responsive guidance
 * (column span, page margins at 320/600/1136dp breakpoints) is a property of
 * the *list's container*, not a single row. Use `AppTheme.pageMargin` on the
 * surrounding list/screen for that, not a per-row override.
 *
 * ### Customization
 * [leading]/[trailing] content, [variant], [density], [selected]/[enabled]/[loading]
 * state, [singleLine] wrap-vs-truncate, [verticalAlignment] (center by
 * default; pass [Alignment.Top] for the "long content" edge case Uber Base
 * calls out), [customColors] override.
 *
 * ### Usage notes
 * - Stick to one artwork size per list; don't mix icon/avatar sizes across
 *   rows in the same list (Uber Base anti-pattern).
 * - Restrict [ListItemTrailing.Icon] chevrons to navigating into a subset of
 *   options — never as a generic "this row is tappable" affordance, and
 *   never as a stand-in for "opens a menu".
 * - Avoid stacking multiple icon indicators in [trailing]; break into
 *   separate rows instead.
 * - Give a [ListItemTrailing.Control] its own background (e.g. a
 *   `Tonal`/`Filled` icon button) so its independent tap target reads
 *   clearly next to the row's own tap target ("gray background protection").
 * - Don't use inline tags within [title]/[subtitle]/[caption] text.
 *
 * @param title Required primary label
 * @param modifier Modifier for the row
 * @param subtitle Optional secondary label ("paragraph")
 * @param caption Optional tertiary label ("support paragraph")
 * @param onClick Row tap handler; null renders a non-interactive row (e.g. one whose only interactivity lives in [trailing])
 * @param variant [ListItemVariant.FullWidth] (default) or [ListItemVariant.Selection]
 * @param density [ListItemDensity.Standard] (default, 64dp) or [ListItemDensity.Compact] (48dp)
 * @param leading Leading content slot (Default: [ListItemLeading.Off])
 * @param trailing Trailing content slot (Default: [ListItemTrailing.Off])
 * @param selected Whether the row is in the active/selected state (Default: false)
 * @param enabled Whether the row is interactive (Default: true)
 * @param loading Whether to render the preloading [SkeletonListItem] placeholder (Default: false)
 * @param singleLine Truncate all text labels to one line instead of wrapping (Default: false)
 * @param verticalAlignment Vertical alignment of the row's children (Default: [Alignment.CenterVertically])
 * @param customColors Optional [ListItemStateColors] override
 * @param contentDescription Accessibility description (defaults to title/subtitle/caption combined, per Uber Base's VoiceOver preview format)
 *
 * @sample
 * ```
 * PixaListItem(
 *     title = "Notifications",
 *     subtitle = "Push, Email, SMS",
 *     leading = ListItemLeading.Icon { PixaIcon(Icons.Default.Notifications, contentDescription = null) },
 *     trailing = ListItemTrailing.Icon { PixaIcon(Icons.Default.ChevronRight, contentDescription = null) },
 *     onClick = { openNotificationSettings() }
 * )
 * ```
 */
@Composable
fun PixaListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    caption: String? = null,
    onClick: (() -> Unit)? = null,
    variant: ListItemVariant = ListItemVariant.FullWidth,
    density: ListItemDensity = ListItemDensity.Standard,
    leading: ListItemLeading = ListItemLeading.Off,
    trailing: ListItemTrailing = ListItemTrailing.Off,
    selected: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    singleLine: Boolean = false,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    customColors: ListItemStateColors? = null,
    contentDescription: String? = null
) {
    val layout = listItemLayoutFor(density)

    if (loading) {
        val textLines = 1 + listOfNotNull(subtitle, caption).size
        val showAvatar = leading != ListItemLeading.Off
        if (variant == ListItemVariant.Selection) {
            val shape = AppTheme.shapes.rounded.large
            Box(
                modifier = modifier
                    .padding(horizontal = HierarchicalSize.Spacing.Large)
                    .clip(shape)
                    .border(HierarchicalSize.Border.Medium, AppTheme.colors.baseBorderDefault, shape)
            ) {
                SkeletonListItem(showAvatar = showAvatar, textLines = textLines, shimmerEnabled = true)
            }
        } else {
            SkeletonListItem(
                modifier = modifier,
                showAvatar = showAvatar,
                textLines = textLines,
                shimmerEnabled = true
            )
        }
        return
    }

    val theme = customColors ?: getListItemTheme(variant, AppTheme.colors)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val interactive = enabled && onClick != null

    val currentColors = when {
        !enabled -> theme.disabled
        selected -> theme.active
        else -> theme.default
    }

    val backgroundColor by animateColorAsState(
        targetValue = currentColors.background,
        animationSpec = AnimationUtils.fastTween(),
        label = "listItemBackground"
    )
    val borderColor by animateColorAsState(
        targetValue = currentColors.border,
        animationSpec = AnimationUtils.fastTween(),
        label = "listItemBorder"
    )

    val shape: Shape = if (variant == ListItemVariant.Selection) AppTheme.shapes.rounded.large else RectangleShape
    val accessibilityDescription = contentDescription
        ?: listOfNotNull(title, subtitle, caption).joinToString(", ")

    val startInset = if (leading is ListItemLeading.Icon) HierarchicalSize.Padding.None else layout.horizontalPadding
    val endInset = when (trailing) {
        is ListItemTrailing.Icon, is ListItemTrailing.Flush -> HierarchicalSize.Padding.None
        else -> layout.horizontalPadding
    }

    Box(
        modifier = modifier
            .then(
                if (variant == ListItemVariant.Selection) {
                    Modifier.padding(horizontal = HierarchicalSize.Spacing.Large)
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(backgroundColor)
            .then(
                if (variant == ListItemVariant.Selection) {
                    Modifier.border(currentColors.borderWidth, borderColor, shape)
                } else {
                    Modifier
                }
            )
            .heightIn(min = layout.minHeight)
            .focusable(interactionSource = interactionSource, enabled = interactive)
            .then(
                if (isFocused && interactive) {
                    Modifier.border(HierarchicalSize.Border.Large, theme.focusBorderColor, shape)
                } else {
                    Modifier
                }
            )
            .semantics {
                this.contentDescription = accessibilityDescription
                this.selected = selected
                if (interactive) {
                    this.role = Role.Button
                }
            }
            .then(
                if (interactive) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        role = Role.Button,
                        onClick = onClick,
                        onClickLabel = accessibilityDescription
                    )
                } else {
                    Modifier
                }
            )
    ) {
        when {
            isPressed && interactive -> ListItemOverlayScrim(theme.pressedOverlay, shape)
            isHovered && interactive -> ListItemOverlayScrim(theme.hoverOverlay, shape)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = startInset,
                    end = endInset,
                    top = layout.verticalPadding,
                    bottom = layout.verticalPadding
                ),
            verticalAlignment = verticalAlignment
        ) {
            ListItemLeadingSlot(leading, layout)
            ListItemTextColumn(title, subtitle, caption, singleLine, currentColors, layout.labelSpacing)
            ListItemTrailingSlot(trailing, layout, currentColors)
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * SelectionListItem — [PixaListItem] preset for [ListItemVariant.Selection],
 * Uber Base's inset/bordered row for radio- or checkbox-style selection
 * lists. Pair [trailing] with a read-only [com.pixamob.pixacompose.components.inputs.PixaCheckbox]
 * or [com.pixamob.pixacompose.components.inputs.RadioButton] (`onCheckedChange`/`onClick = null`)
 * so the row itself stays the single tap target, matching Uber Base's
 * interaction model — the same pattern
 * [com.pixamob.pixacompose.components.display.PixaTile] already uses.
 *
 * @sample
 * ```
 * SelectionListItem(
 *     title = "Airport",
 *     selected = isSelected,
 *     onClick = { isSelected = !isSelected },
 *     trailing = ListItemTrailing.Icon {
 *         RadioButton(selected = isSelected, onClick = null)
 *     }
 * )
 * ```
 */
@Composable
fun SelectionListItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: ListItemLeading = ListItemLeading.Off,
    trailing: ListItemTrailing = ListItemTrailing.Off,
    enabled: Boolean = true,
    density: ListItemDensity = ListItemDensity.Standard
) {
    PixaListItem(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        onClick = onClick,
        variant = ListItemVariant.Selection,
        density = density,
        leading = leading,
        trailing = trailing,
        selected = selected,
        enabled = enabled
    )
}
