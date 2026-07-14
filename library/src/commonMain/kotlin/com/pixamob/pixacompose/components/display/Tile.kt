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
 * Behavior axis, mapped from Uber Base's Selection Tiles / Action Tiles:
 * - [Selection] = styled like checkboxes/radio buttons for making choices.
 * - [Action] = styled like secondary buttons for triggering actions or
 *   navigation — "Active/Selected (not available for button-like tiles)" per
 *   the spec's states table, so [Action] tiles ignore `selected`/[TileTrailingControl.Check]/
 *   [TileTrailingControl.Radio]/[TileTrailingControl.Switch] styling.
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
 * Leading (top-left) artwork slot sizing, mapped from Uber Base's Leading
 * Content options. [Off] and [LabelOnly] both render no artwork — kept as
 * two names because Uber Base lists them as distinct anatomy choices even
 * though they're visually identical (the difference is purely which content
 * *besides* artwork is present, which callers already control via [PixaTile]'s
 * `label`/`paragraphs` params).
 */
sealed class TileArtwork {
    data object Off : TileArtwork()
    data object LabelOnly : TileArtwork()
    data object Medium : TileArtwork()
    data object Large : TileArtwork()
    data class Custom(val size: Dp) : TileArtwork()
}

/**
 * Trailing (top-right) content slot, mapped from Uber Base's Trailing
 * Content options. [Check]/[Radio]/[Switch] render read-only *visual state*
 * driven by [PixaTile]'s own `selected` — Uber Base's interaction model is
 * "interacting with any part of the tile activates the control inside," i.e.
 * the tile itself is the tap target, not an independently-clickable nested
 * control. [Switch] is the one exception: [com.pixamob.pixacompose.components.inputs.PixaSwitch]
 * has no read-only mode (`onCheckedChange` isn't nullable, unlike
 * Checkbox/RadioButton), so it stays genuinely interactive — which also
 * matches Uber Base's own guidance to "use a switch if it takes immediate
 * effect" rather than waiting for the tile-level selection to commit.
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
 * Resolves Tile's fixed color/border ladder. Border widths land exactly on
 * existing [HierarchicalSize.Border] tiers — enabled's 2px ("borderOpaque")
 * is [HierarchicalSize.Border.Medium], selected's 3px ("borderSelected") is
 * [HierarchicalSize.Border.Large] — so no new border-width token is needed.
 * Hover/pressed are Uber Base's literal 4%/8% black state-layer overlays
 * (a scrim technique, not a themed color token — see [TileOverlayScrim]),
 * kept as raw alpha values since Uber Base specifies them as fixed
 * percentages rather than theme-driven tokens.
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
        // Action tiles have no selected/control state per Uber Base's states table.
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
            onCheckedChange = onSwitchToggle, // genuinely interactive — "immediate effect" per spec
            enabled = enabled,
            size = size
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaTile — a button-like, bordered container for grid-style selection or
 * action patterns. Migrated from Uber Base's Tile spec.
 *
 * ### Anatomy
 * Top row: leading [artwork]/[leadingContent] slot, trailing [trailing]
 * control slot. Bottom: a required [label] plus up to 2 [paragraphs]
 * (defensively capped — this is an anatomy limit, not a soft content rule).
 * Per Uber Base, a single tile is not a supported use case — "if you desire
 * a single tile, consider using a [PixaCard] instead" — so this component is
 * always meant to be composed 2+ at a call site (in a `Row`/`FlowRow`/`LazyGrid`
 * the caller owns; no dedicated group/grid wrapper ships here, matching how
 * [PixaCheckbox]/`RadioButton` don't ship one either).
 *
 * ### Variants
 * [TileBehavior.Selection] (checkbox/radio-styled) vs [TileBehavior.Action]
 * (button-styled, no selected/control visuals — Uber Base explicitly
 * excludes Active/Selected states from action-style tiles).
 *
 * ### States
 * default (2px [com.pixamob.pixacompose.theme.HierarchicalSize.Border.Medium]
 * border), hover/pressed (4%/8% black overlay — see [TileOverlayScrim]),
 * selected (3px [com.pixamob.pixacompose.theme.HierarchicalSize.Border.Large]
 * border, brand-tinted surface — same token pairing [com.pixamob.pixacompose.components.display.SelectCard]
 * uses), disabled (dimmed border/content), preloading ([loading] → [Skeleton]).
 *
 * ### Sizing
 * [size] drives padding/spacing/artwork sizing/border-radius through
 * [HierarchicalSize]/[AppTheme.shapes]; Uber Base doesn't define discrete
 * Tile height tiers itself (its "sizing" section only covers grid spacing
 * and artwork dimensions), so this follows Pixa's own size-system convention
 * rather than an Uber-specified ladder.
 *
 * ### Customization
 * Background/border/content colors ([customColors]), alignment
 * ([contentAlignment]), artwork size/content ([artwork]/[leadingContent]),
 * trailing control ([trailing]) — matching Uber Base's stated customization
 * boundaries. Minimum anatomy (container + label) and the 2-paragraph cap
 * are not overridable, per the spec's "constrained" list.
 *
 * ### Usage notes
 * - Grid 2-3 tiles across on mobile ([HierarchicalSize.Spacing.Small], 8dp,
 *   between tiles) and up to 3+ on wide/web layouts
 *   ([HierarchicalSize.Spacing.Large], 16dp between tiles) — layout/grid
 *   composition is caller-owned, these are the token values to reach for.
 * - In horizontally-scrolling tile rows, peek the next tile by at least 24dp
 *   from the container edge (not runtime-enforced here — a caller-side
 *   scroll-container layout concern).
 * - Communicate multi-select limits explicitly in the surrounding UI when a
 *   cap exists and no visible counter/control communicates it.
 *
 * @param label Required bottom-content label
 * @param onClick Called when any part of the tile is tapped (also drives [trailing]'s Check/Radio visual)
 * @param modifier Modifier for the tile container
 * @param behavior [TileBehavior.Selection] or [TileBehavior.Action] (Default: Action)
 * @param selected Whether the tile is in the selected state (Default: false, caller-tracked like [com.pixamob.pixacompose.components.actions.PixaButton]'s `selected`)
 * @param enabled Whether the tile is interactive (Default: true)
 * @param loading Whether to render the preloading [Skeleton] placeholder (Default: false)
 * @param artwork Leading artwork size preset (Default: [TileArtwork.Off])
 * @param leadingContent Composable rendered at [artwork]'s resolved size (an icon/image/avatar)
 * @param trailing Trailing content slot (Default: [TileTrailingControl.Off])
 * @param paragraphs Up to 2 supporting paragraphs beneath [label]
 * @param contentAlignment Horizontal alignment of the bottom content (Default: Start)
 * @param size Size variant driving padding/spacing/artwork/radius (Default: [SizeVariant.Medium])
 * @param description Accessibility description
 * @param customColors Optional [TileStateColors] override
 *
 * @sample
 * ```
 * PixaTile(
 *     label = "Airport",
 *     behavior = TileBehavior.Selection,
 *     trailing = TileTrailingControl.Check,
 *     selected = isSelected,
 *     onClick = { isSelected = !isSelected }
 * )
 * ```
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
