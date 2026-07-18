package com.pixamob.pixacompose.components.display

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.components.inputs.PixaCheckbox
import com.pixamob.pixacompose.components.inputs.PixaSwitch
import com.pixamob.pixacompose.components.inputs.RadioButton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.theme.forVariant
import com.pixamob.pixacompose.utils.AnimationUtils

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Behavior axis.
 * - [Selection]: styled like checkboxes/radio buttons for making choices.
 * - [Action]: styled like secondary buttons for triggering actions/navigation.
 *   Action tiles ignore `selected` and trailing control styling.
 */
enum class TileBehavior {
    Selection,
    Action
}

/** Horizontal alignment of the bottom label/paragraph content. */
enum class TileContentAlignment {
    Start,
    Center,
    End
}

/**
 * Leading (top-left) artwork slot sizing.
 * [Off] and [LabelOnly] both render no artwork — they exist as distinct anatomy
 * choices for documentation clarity; callers control content via `label`/`paragraphs`.
 */
sealed class TileArtwork {
    data object Off : TileArtwork()
    data object LabelOnly : TileArtwork()
    data object Medium : TileArtwork()
    data object Large : TileArtwork()
    data class Custom(val size: Dp) : TileArtwork()
}

/**
 * Trailing (top-right) content slot.
 * [Check]/[Radio] render read-only visual state driven by `selected` — the tile itself
 * is the tap target. [Switch] stays genuinely interactive (no read-only mode in PixaSwitch).
 */
sealed class TileTrailingControl {
    data object Off : TileTrailingControl()
    data class Badge(val content: @Composable () -> Unit) : TileTrailingControl()
    data object Check : TileTrailingControl()
    data object Radio : TileTrailingControl()
    data object Switch : TileTrailingControl()
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class TileColors(
    val background: Color,
    val border: Color,
    val borderWidth: Dp,
    val titleContent: Color,
    val bodyContent: Color
)

@Immutable
@Stable
data class TileStateColors(
    val default: TileColors,
    val selected: TileColors,
    val disabled: TileColors,
    val hoverOverlay: Color,
    val pressedOverlay: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Resolves Tile's fixed color/border ladder. Hover/pressed use 4%/8% black
 * state-layer overlays (scrim technique, not themed color tokens).
 */
@Composable
private fun getTileTheme(colors: ColorPalette): TileStateColors {
    val defaultColors = TileColors(
        background = colors.baseSurfaceDefault,
        border = colors.baseBorderDefault,
        borderWidth = HierarchicalSize.Border.Medium, // 2px, "borderOpaque"
        titleContent = colors.baseContentTitle,
        bodyContent = colors.baseContentBody
    )

    val selectedColors = TileColors(
        background = colors.brandSurfaceSubtle,
        border = colors.brandBorderFocus, // "borderSelected" — same token SelectCard uses
        borderWidth = HierarchicalSize.Border.Large, // 3px
        titleContent = colors.baseContentTitle,
        bodyContent = colors.baseContentBody
    )

    val disabledColors = TileColors(
        background = colors.baseSurfaceDisabled,
        border = colors.baseBorderDisabled,
        borderWidth = HierarchicalSize.Border.Medium,
        titleContent = colors.baseContentDisabled,
        bodyContent = colors.baseContentDisabled
    )

    return TileStateColors(
        default = defaultColors,
        selected = selectedColors,
        disabled = disabledColors,
        hoverOverlay = Color.Black.copy(alpha = 0.04f),
        pressedOverlay = Color.Black.copy(alpha = 0.08f)
    )
}

@Composable
private fun tileArtworkSize(artwork: TileArtwork): Dp = when (artwork) {
    is TileArtwork.Off, is TileArtwork.LabelOnly -> HierarchicalSize.Icon.None
    is TileArtwork.Medium -> HierarchicalSize.Icon.Medium // 24dp
    is TileArtwork.Large -> HierarchicalSize.Icon.Huge // 36dp
    is TileArtwork.Custom -> artwork.size
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL TILE
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun BoxScope.TileOverlayScrim(color: Color, shape: Shape) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .clip(shape)
            .background(color)
    )
}

@Composable
private fun TileTrailingContent(
    trailing: TileTrailingControl,
    behavior: TileBehavior,
    selected: Boolean,
    enabled: Boolean,
    onSwitchToggle: (Boolean) -> Unit,
    size: SizeVariant
) {
    if (behavior != TileBehavior.Selection && trailing != TileTrailingControl.Off && trailing !is TileTrailingControl.Badge) {
        // Action tiles have no selected/control state.
        return
    }

    when (trailing) {
        TileTrailingControl.Off -> Unit

        is TileTrailingControl.Badge -> trailing.content()

        TileTrailingControl.Check -> PixaCheckbox(
            checked = selected,
            onCheckedChange = null, // read-only — the tile itself is the tap target
            enabled = enabled,
            size = size
        )

        TileTrailingControl.Radio -> RadioButton(
            selected = selected,
            onClick = null, // read-only — the tile itself is the tap target
            enabled = enabled,
            size = size
        )

        TileTrailingControl.Switch -> PixaSwitch(
            checked = selected,
            onCheckedChange = onSwitchToggle, // genuinely interactive
            enabled = enabled,
            size = size
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaTile — a button-like, bordered container for grid-style selection or action patterns.
 *
 * ### Anatomy
 * Top row: leading [artwork]/[leadingContent] slot, trailing [trailing] control slot.
 * Bottom: required [label] plus up to 2 [paragraphs]. Always compose 2+ tiles
 * (a single tile should use [PixaCard] instead).
 *
 * ### Variants
 * [TileBehavior.Selection] (checkbox/radio-styled) vs [TileBehavior.Action]
 * (button-styled, no selected/control visuals).
 *
 * ### States
 * Default (2px border), hover/pressed (4%/8% overlay), selected (3px border,
 * brand-tinted), disabled (dimmed), preloading ([Skeleton]).
 *
 * ### Sizing
 * [size] drives padding/spacing/artwork/radius through [HierarchicalSize]/[AppTheme.shapes].
 *
 * ### Customization
 * Colors ([customColors]), alignment ([contentAlignment]), artwork
 * ([artwork]/[leadingContent]), trailing control ([trailing]).
 * Minimum anatomy (container + label) and 2-paragraph cap are not overridable.
 *
 * ### Usage notes
 * - Grid 2-3 tiles across on mobile (8dp spacing), 3+ on wide layouts (16dp).
 * - Peek next tile by at least 24dp in horizontally-scrolling rows.
 * - Communicate multi-select limits in the surrounding UI.
 *
 * @param label Required bottom-content label
 * @param onClick Tap handler for the tile (also drives Check/Radio visuals)
 * @param modifier Modifier for the tile container
 * @param behavior Selection or Action (Default: Action)
 * @param selected Selected state (Default: false)
 * @param enabled Interactive state (Default: true)
 * @param loading Shows [Skeleton] (Default: false)
 * @param artwork Leading artwork size (Default: Off)
 * @param leadingContent Composable at [artwork]'s resolved size
 * @param trailing Trailing content slot (Default: Off)
 * @param paragraphs Up to 2 supporting paragraphs
 * @param contentAlignment Bottom content alignment (Default: Start)
 * @param size Size variant (Default: Medium)
 * @param description Accessibility description
 * @param customColors Optional [TileStateColors] override
 *
 * @sample
 * PixaTile(
 *     label = "Airport",
 *     behavior = TileBehavior.Selection,
 *     trailing = TileTrailingControl.Check,
 *     selected = isSelected,
 *     onClick = { isSelected = !isSelected }
 * )
 */
@Composable
fun PixaTile(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    behavior: TileBehavior = TileBehavior.Action,
    selected: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    artwork: TileArtwork = TileArtwork.Off,
    leadingContent: (@Composable () -> Unit)? = null,
    trailing: TileTrailingControl = TileTrailingControl.Off,
    paragraphs: List<String> = emptyList(),
    contentAlignment: TileContentAlignment = TileContentAlignment.Start,
    size: SizeVariant = SizeVariant.Medium,
    description: String? = null,
    customColors: TileStateColors? = null,
) {
    val shape = AppTheme.shapes.rounded.forVariant(size)
    val padding = HierarchicalSize.Padding.forVariant(size)
    val spacing = HierarchicalSize.Spacing.forVariant(size)
    val artworkSize = tileArtworkSize(artwork)

    if (loading) {
        Skeleton(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            shimmerEnabled = true
        )
        return
    }

    val theme = customColors ?: getTileTheme(AppTheme.colors)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val effectiveSelected = behavior == TileBehavior.Selection && selected
    val currentColors = when {
        !enabled -> theme.disabled
        effectiveSelected -> theme.selected
        else -> theme.default
    }

    val backgroundColor by animateColorAsState(
        targetValue = currentColors.background,
        animationSpec = AnimationUtils.standardTween(150),
        label = "tile_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = currentColors.border,
        animationSpec = AnimationUtils.standardTween(150),
        label = "tile_border"
    )

    val horizontalAlignment = when (contentAlignment) {
        TileContentAlignment.Start -> Alignment.Start
        TileContentAlignment.Center -> Alignment.CenterHorizontally
        TileContentAlignment.End -> Alignment.End
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(currentColors.borderWidth, borderColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
                onClickLabel = description
            )
    ) {
        when {
            !enabled -> Unit
            isPressed -> TileOverlayScrim(theme.pressedOverlay, shape)
            isHovered -> TileOverlayScrim(theme.hoverOverlay, shape)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            if (artwork != TileArtwork.Off && artwork != TileArtwork.LabelOnly || trailing != TileTrailingControl.Off) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    if (artworkSize > HierarchicalSize.Icon.None && leadingContent != null) {
                        Box(modifier = Modifier.size(artworkSize)) {
                            leadingContent()
                        }
                    }

                    TileTrailingContent(
                        trailing = trailing,
                        behavior = behavior,
                        selected = effectiveSelected,
                        enabled = enabled,
                        onSwitchToggle = { onClick() },
                        size = size
                    )
                }
            }

            BasicText(
                text = label,
                style = AppTheme.typography.bodyBold.copy(color = currentColors.titleContent)
            )

            paragraphs.take(2).forEach { paragraph ->
                BasicText(
                    text = paragraph,
                    style = AppTheme.typography.captionRegular.copy(color = currentColors.bodyContent)
                )
            }
        }
    }
}
