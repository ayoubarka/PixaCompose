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
 * Configuration variant.
 * - [FullWidth]: full-bleed row with tinted active state — no border, no corner radius.
 * - [Selection]: inset, bordered row with rounded corners for selection patterns.
 */
enum class ListItemVariant {
    FullWidth,
    Selection
}

/**
 * Row density: [Standard] (64dp minimum height) or [Compact] (48dp, fits tallest content).
 */
enum class ListItemDensity {
    Standard,
    Compact
}

/**
 * Leading/trailing icon sizing. Resolves to [HierarchicalSize.Icon] tiers.
 */
enum class ListItemIconSize {
    Small,
    Medium,
    Large
}

/**
 * Leading content slot.
 */
sealed class ListItemLeading {
    /** No artwork — plain 16dp side inset. */
    data object Off : ListItemLeading()

    /**
     * An icon centered in a fixed-width container matching the row's minimum height.
     */
    data class Icon(
        val size: ListItemIconSize = ListItemIconSize.Medium,
        val content: @Composable () -> Unit
    ) : ListItemLeading()

    /**
     * Artwork larger than 36dp at its natural size — avatars, badges, illustrations.
     */
    data class Artwork(val content: @Composable () -> Unit) : ListItemLeading()
}

/**
 * Trailing content slot. Each case has its own alignment/inset rule.
 */
sealed class ListItemTrailing {
    data object Off : ListItemTrailing()

    /**
     * Small icon/indicator (e.g. chevron), centered in a fixed-width container.
     * Row itself is the tap target. Restrict to navigation into subsets of options.
     */
    data class Icon(val content: @Composable () -> Unit) : ListItemTrailing()

    /**
     * Independently-clickable control (switch, icon button) with its own tap target.
     * Give it its own background so the separate tap zone reads clearly.
     */
    data class Control(val content: @Composable () -> Unit) : ListItemTrailing()

    /** Right-aligned trailing text. */
    data class Caption(val text: String) : ListItemTrailing()

    /** Flush-right button/pill — zero inset. */
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
 * Resolves density-driven layout values. Standard container width matches row height
 * (64dp); Compact container width is derived the same way (48dp).
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
    ListItemIconSize.Small -> HierarchicalSize.Icon.Small
    ListItemIconSize.Medium -> HierarchicalSize.Icon.Medium
    ListItemIconSize.Large -> HierarchicalSize.Icon.Huge
}

/**
 * Resolves the fixed color/border ladder per [ListItemVariant]. Hover/pressed
 * use black state-layer overlays (4%/8%).
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
 *
 * ### Anatomy
 * Optional [leading] slot, up to 3 stacked text labels ([title], [subtitle], [caption]),
 * optional [trailing] slot. Dividers between rows are not rendered here —
 * compose [PixaDivider] at the call site instead.
 *
 * ### Variants
 * [ListItemVariant.FullWidth] (full-bleed, no border) vs [ListItemVariant.Selection]
 * (inset, bordered, for selection patterns).
 *
 * ### States
 * Default, hover/pressed (4%/8% overlay), focus (accent border ring),
 * active/[selected] (brand-tinted), disabled, preloading ([SkeletonListItem]).
 *
 * ### Sizing
 * [density] drives row height: Standard (64dp) or Compact (48dp).
 * Icon sizes use [ListItemIconSize].
 *
 * ### Usage notes
 * - Stick to one artwork size per list.
 * - Restrict [ListItemTrailing.Icon] chevrons to navigation into subsets of options.
 * - Give [ListItemTrailing.Control] its own background for a clear tap zone.
 *
 * @param title Required primary label
 * @param modifier Modifier for the row
 * @param subtitle Optional secondary label
 * @param caption Optional tertiary label
 * @param onClick Row tap handler; null = non-interactive
 * @param variant FullWidth (default) or Selection
 * @param density Standard (64dp, default) or Compact (48dp)
 * @param leading Leading content slot (Default: Off)
 * @param trailing Trailing content slot (Default: Off)
 * @param selected Active/selected state (Default: false)
 * @param enabled Interactive state (Default: true)
 * @param loading Shows [SkeletonListItem] (Default: false)
 * @param singleLine Truncate text to one line (Default: false)
 * @param verticalAlignment Row children alignment (Default: CenterVertically)
 * @param customColors Optional [ListItemStateColors] override
 * @param contentDescription Accessibility description (defaults to title/subtitle/caption combined)
 *
 * @sample
 * PixaListItem(
 *     title = "Notifications",
 *     subtitle = "Push, Email, SMS",
 *     leading = ListItemLeading.Icon { PixaIcon(Icons.Default.Notifications, contentDescription = null) },
 *     trailing = ListItemTrailing.Icon { PixaIcon(Icons.Default.ChevronRight, contentDescription = null) },
 *     onClick = { openNotificationSettings() }
 * )
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
 * SelectionListItem — [PixaListItem] preset for [ListItemVariant.Selection].
 * Pair [trailing] with a read-only [PixaCheckbox] or [RadioButton] (`onClick = null`)
 * so the row itself stays the single tap target.
 *
 * @sample
 * SelectionListItem(
 *     title = "Airport",
 *     selected = isSelected,
 *     onClick = { isSelected = !isSelected },
 *     trailing = ListItemTrailing.Icon { RadioButton(selected = isSelected, onClick = null) }
 * )
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
