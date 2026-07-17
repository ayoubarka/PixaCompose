package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.pixamob.pixacompose.components.display.IconSource
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.theme.forVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.withAlpha
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Width behavior, mapped from Uber Base's segmented-control width types:
 * - [Fixed] = "Track occupies full container; segments divide equally" — labels
 *   truncate rather than widening the segment.
 * - [Intrinsic] = "Track length grows with the content in the segments" — each
 *   segment hugs its own content, so the track hugs the row.
 *
 * Both are single-row: Uber Base allows no text wrapping and no horizontal
 * scrolling within a segmented control.
 */
enum class SegmentedButtonWidth {
    Fixed,
    Intrinsic
}

/**
 * Track/tile corner treatment. Mirrors [ButtonShape]'s vocabulary minus
 * `Circle`, which has no meaning for a multi-segment track.
 */
enum class SegmentedButtonShape {
    Default,
    Pill
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/**
 * A single segment. Mapped from Uber Base's content model: optional leading
 * [icon], required [label], optional [paragraph] descriptor below the label.
 *
 * @param id Stable identity used for selection
 * @param label Primary text — keep to a few words (Uber Base: labels must fit one line)
 * @param paragraph Optional secondary descriptor rendered below [label]
 * @param icon Optional leading icon — Uber Base recommends pairing icons with labels
 *   rather than shipping icon-only segments
 * @param enabled Whether this segment can be selected
 * @param contentDescription Overrides the default "[Label], [Paragraph]" screen-reader text
 */
@Immutable
@Stable
data class SegmentedButtonItem(
    val id: String,
    val label: String,
    val paragraph: String? = null,
    val icon: IconSource? = null,
    val enabled: Boolean = true,
    val contentDescription: String? = null
)

/**
 * Colors for a segmented control, mapped from the Uber Base spec's tokens:
 * - [trackSurface] ← `backgroundTertiary` (the inactive/track background)
 * - [activeTileSurface] ← `backgroundPrimary` (the sliding active tile)
 * - [disabledContent] ← `contentStateDisabled`
 * - [focusBorder] ← `borderAccent` (keyboard focus ring)
 */
@Immutable
@Stable
data class SegmentedButtonColors(
    val trackSurface: Color,
    val trackBorder: Color,
    val activeTileSurface: Color,
    val activeContent: Color,
    val inactiveContent: Color,
    val paragraphContent: Color,
    val disabledContent: Color,
    val focusBorder: Color,
    /** Overlay tinted over a segment on hover/press — see [HOVER_OVERLAY_ALPHA]. */
    val stateOverlay: Color
)

/**
 * Resolved metrics for one size tier. Segment metrics are delegated to
 * [getButtonSizeConfig] rather than re-laddered here — a segment is a
 * button-shaped control, so the two must stay dimensionally in step.
 */
@Immutable
@Stable
private data class SegmentedButtonSizeConfig(
    val segmentMinHeight: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    val iconSpacing: Dp,
    val trackPadding: Dp,
    val labelStyle: TextStyle,
    val paragraphStyle: TextStyle
)

// Uber Base state overlays: "a 4% black overlay" on hover, "8% black overlay"
// added on mouse down. These are the spec's own ratios, not a size/color token
// the theme has an equivalent for, so they stay as named local constants.
private const val HOVER_OVERLAY_ALPHA = 0.04f
private const val PRESSED_OVERLAY_ALPHA = 0.08f

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Uber Base names `backgroundPrimary` for the active tile and `backgroundTertiary`
 * for the track behind it. In PixaCompose the base surface ramp inverts between
 * schemes — `baseSurfaceSubtle` is the *raised* surface in both light (base 50)
 * and dark (base 700), and `baseSurfaceDefault` is the recessed one (base 100 /
 * base 800) — so subtle-on-default reproduces Uber's tile-on-track contrast in
 * either scheme rather than only in light.
 */
@Composable
private fun defaultSegmentedButtonColors(): SegmentedButtonColors = SegmentedButtonColors(
    trackSurface = AppTheme.colors.baseSurfaceDefault,
    trackBorder = AppTheme.colors.baseBorderSubtle,
    activeTileSurface = AppTheme.colors.baseSurfaceSubtle,
    activeContent = AppTheme.colors.baseContentTitle,
    inactiveContent = AppTheme.colors.baseContentBody,
    paragraphContent = AppTheme.colors.baseContentCaption,
    disabledContent = AppTheme.colors.baseContentDisabled,
    focusBorder = AppTheme.colors.accentBorderDefault,
    // The spec's "black overlay" — baseSurfaceShadow is the theme's near-black
    // token (base 950 in both schemes), so this stays theme-driven.
    stateOverlay = AppTheme.colors.baseSurfaceShadow
)

@Composable
private fun getSegmentedButtonSizeConfig(size: SizeVariant): SegmentedButtonSizeConfig {
    val buttonConfig = getButtonSizeConfig(size)
    val typography = AppTheme.typography
    return SegmentedButtonSizeConfig(
        segmentMinHeight = buttonConfig.height,
        horizontalPadding = buttonConfig.horizontalPadding,
        iconSize = buttonConfig.iconSize,
        iconSpacing = buttonConfig.iconSpacing,
        // The gutter between track edge and tile is an optically constant
        // hairline, not a size-scaled padding — it stays at the tightest tier
        // so the tile keeps reading as inset at every size.
        trackPadding = HierarchicalSize.Padding.Nano,
        labelStyle = buttonConfig.textStyle(),
        // Paragraph sits one tier under the label across the whole ladder.
        paragraphStyle = when (size) {
            SizeVariant.None, SizeVariant.Nano, SizeVariant.Compact -> typography.labelSmall
            SizeVariant.Small, SizeVariant.Medium -> typography.labelMedium
            SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> typography.labelLarge
        }
    )
}

@Composable
private fun segmentedButtonShapeFor(
    shape: SegmentedButtonShape,
    size: SizeVariant
): Shape = when (shape) {
    SegmentedButtonShape.Default -> AppTheme.shapes.rounded.forVariant(size)
    SegmentedButtonShape.Pill -> AppTheme.shapes.pill
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL SEGMENTED BUTTON
// ════════════════════════════════════════════════════════════════════════════

/**
 * One segment: leading icon + label + optional paragraph, stacked per Uber Base's
 * anatomy. Renders no background of its own — the sliding tile behind it supplies
 * the active surface — only the hover/press overlay on top of it.
 */
@Composable
private fun RowScope.SegmentedButtonSegment(
    item: SegmentedButtonItem,
    selected: Boolean,
    enabled: Boolean,
    width: SegmentedButtonWidth,
    config: SegmentedButtonSizeConfig,
    colors: SegmentedButtonColors,
    tileShape: Shape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val overlayColor by animateColorAsState(
        targetValue = when {
            !enabled -> Color.Transparent
            isPressed -> colors.stateOverlay.withAlpha(PRESSED_OVERLAY_ALPHA)
            isHovered -> colors.stateOverlay.withAlpha(HOVER_OVERLAY_ALPHA)
            else -> Color.Transparent
        },
        animationSpec = AnimationUtils.colorSpring,
        label = "segmentOverlay"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledContent
            selected -> colors.activeContent
            else -> colors.inactiveContent
        },
        animationSpec = AnimationUtils.colorSpring,
        label = "segmentContent"
    )

    val paragraphColor = if (enabled) colors.paragraphContent else colors.disabledContent

    val segmentWidth = when (width) {
        SegmentedButtonWidth.Fixed -> Modifier.weight(1f)
        SegmentedButtonWidth.Intrinsic -> Modifier.wrapContentWidth()
    }

    Box(
        modifier = modifier
            .then(segmentWidth)
            .fillMaxHeight()
            .clip(tileShape)
            .background(overlayColor)
            .then(
                // Uber Base focus state: "Outlined in 3 border borderAccent".
                if (isFocused && enabled) {
                    Modifier.border(HierarchicalSize.Border.Large, colors.focusBorder, tileShape)
                } else {
                    Modifier
                }
            )
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                // No indication: Uber Base specifies the press/hover feedback as
                // explicit background overlays (drawn above), and a Material
                // ripple is not available to this library.
                indication = null,
                onClick = onClick
            )
            .then(
                if (item.contentDescription != null) {
                    Modifier.semantics { contentDescription = item.contentDescription }
                } else {
                    // Default text merging already voices "[Label], [Paragraph]".
                    Modifier
                }
            )
            .defaultMinSize(minHeight = config.segmentMinHeight)
            .padding(horizontal = config.horizontalPadding, vertical = config.trackPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                config.iconSpacing,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item.icon?.let { icon ->
                PixaIcon(
                    source = icon,
                    contentDescription = null,
                    tint = contentColor,
                    customSize = config.iconSize
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BasicText(
                    text = item.label,
                    style = config.labelStyle.copy(
                        color = contentColor,
                        textAlign = TextAlign.Center
                    ),
                    // Uber Base: single row, no wrapping — fixed-width segments
                    // truncate, intrinsic-width ones grow instead.
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                item.paragraph?.let { paragraph ->
                    BasicText(
                        text = paragraph,
                        style = config.paragraphStyle.copy(
                            color = paragraphColor,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaSegmentedButton — lets users pick one option from a linear set of 2–5
 * closely related choices, to input, filter, change presentation, or browse
 * content within the current view.
 *
 * ### Anatomy
 * A **track** (container with a 1px inside border) holds an **active tile** that
 * slides behind the selected segment, plus 2–5 **segments** each carrying an
 * optional leading icon, a label, and an optional paragraph.
 *
 * ### Variants
 * [SegmentedButtonWidth.Fixed] makes the track fill its container and divides it
 * equally between segments (labels truncate);
 * [SegmentedButtonWidth.Intrinsic] grows the track with segment content.
 *
 * ### States
 * Preloading ([showSkeleton]), enabled, hover (4% overlay), focus (3dp accent
 * outline), pressed (8% overlay), and disabled (content drops to the disabled
 * token). Per-item [SegmentedButtonItem.enabled] disables a single segment;
 * [enabled] = false disables the whole control.
 *
 * ### Sizing
 * Segment height/padding/icon/label all derive from the shared button ladder for
 * [size], so a segment stays dimensionally in step with [PixaButton].
 *
 * ### Adaptive behavior
 * Uber Base's responsive rule is about *width*, not control scale: the control
 * matches the width of the main content on narrow viewports and may be resized
 * smaller on wide ones — with no horizontal scrolling or wrapping.
 * [SegmentedButtonWidth.Fixed] already expresses this; constrain the caller's
 * `modifier` to narrow it on wide viewports. [size] stays caller-authoritative
 * and is not silently overridden by [AppTheme.adaptiveSizeVariant].
 *
 * ### Usage notes
 * - Provide 2–5 segments. Uber Base treats fewer as a job for a switch and more
 *   as a job for a scrolling button group; like [PixaButtonGroup]'s own item
 *   rule, this is a content rule, not runtime-enforced.
 * - Don't use this for binary yes/no choices — use a switch instead.
 * - Don't use it for multiple selections — this control is single-select only.
 *   Use [PixaButtonGroup] with [ButtonGroupSelectionMode.Multi] for that.
 * - Don't use it to navigate to a new view — that's [PixaTab]. A segmented
 *   control filters or re-presents the content already in the current view.
 * - Prefer icon+label over icon-only segments.
 * - If this control swaps the content below it, fade that content in and out
 *   (see `AnimationUtils.fadeInTransition`/`fadeOutTransition`).
 *
 * @param items The segments to render, in order (2–5)
 * @param selectedId Id of the currently selected segment
 * @param onSelectionChange Called with the newly selected id — only fires on an
 *   actual change, since a single-select control always keeps one selection
 * @param modifier Modifier for the track
 * @param width Track width behavior (Default: [SegmentedButtonWidth.Fixed])
 * @param size Size variant applied to every segment (Default: [SizeVariant.Medium])
 * @param shape Track/tile corner treatment (Default: [SegmentedButtonShape.Default])
 * @param enabled Whether the whole control is enabled (Default: true)
 * @param showSkeleton Whether to render the preloading placeholder (Default: false)
 * @param customColors Overrides the theme-derived colors
 *
 * @sample
 * ```
 * var selected by remember { mutableStateOf("list") }
 * PixaSegmentedButton(
 *     items = listOf(
 *         SegmentedButtonItem("list", label = "List"),
 *         SegmentedButtonItem("map", label = "Map")
 *     ),
 *     selectedId = selected,
 *     onSelectionChange = { selected = it }
 * )
 * ```
 */
@Composable
fun PixaSegmentedButton(
    items: List<SegmentedButtonItem>,
    selectedId: String,
    onSelectionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    width: SegmentedButtonWidth = SegmentedButtonWidth.Fixed,
    size: SizeVariant = SizeVariant.Medium,
    shape: SegmentedButtonShape = SegmentedButtonShape.Default,
    enabled: Boolean = true,
    showSkeleton: Boolean = false,
    customColors: SegmentedButtonColors? = null
) {
    val config = getSegmentedButtonSizeConfig(size)
    val colors = customColors ?: defaultSegmentedButtonColors()
    val trackShape = segmentedButtonShapeFor(shape, size)
    val density = LocalDensity.current

    val trackWidth = when (width) {
        SegmentedButtonWidth.Fixed -> Modifier.fillMaxWidth()
        SegmentedButtonWidth.Intrinsic -> Modifier.wrapContentWidth()
    }

    if (showSkeleton) {
        Skeleton(
            modifier = modifier.then(trackWidth),
            height = config.segmentMinHeight + config.trackPadding * 2,
            shape = trackShape,
            shimmerEnabled = true
        )
        return
    }

    if (items.isEmpty()) return

    // index -> (x offset within the row, width), both in px. Rebuilt when the
    // segment count changes so stale bounds can't outlive their segment.
    val segmentBounds = remember(items.size) { mutableStateMapOf<Int, Pair<Float, Float>>() }
    var rowHeightPx by remember(items.size) { mutableStateOf(0) }

    val selectedIndex = items.indexOfFirst { it.id == selectedId }
    val bounds = segmentBounds[selectedIndex]
    val targetX = bounds?.first ?: 0f
    val targetTileWidth = bounds?.second ?: 0f

    val tileX = remember { Animatable(0f) }
    val tileWidth = remember { Animatable(0f) }
    var tilePlaced by remember { mutableStateOf(false) }

    // Uber Base: "The tile behind the active segment slides along the track to sit
    // behind the newest selection." The first placement snaps — there is nowhere
    // for it to have slid from.
    LaunchedEffect(targetX, targetTileWidth) {
        if (targetTileWidth <= 0f) return@LaunchedEffect
        if (!tilePlaced) {
            tileX.snapTo(targetX)
            tileWidth.snapTo(targetTileWidth)
            tilePlaced = true
        } else {
            launch { tileX.animateTo(targetX, AnimationUtils.selectionSpring) }
            launch { tileWidth.animateTo(targetTileWidth, AnimationUtils.selectionSpring) }
        }
    }

    Box(
        modifier = modifier
            .then(trackWidth)
            .clip(trackShape)
            .background(colors.trackSurface)
            .border(HierarchicalSize.Border.Compact, colors.trackBorder, trackShape)
            .padding(config.trackPadding)
    ) {
        // The sliding active tile, drawn behind the segments.
        if (tileWidth.value > 0f && rowHeightPx > 0) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(tileX.value.roundToInt(), 0) }
                    .width(with(density) { tileWidth.value.toDp() })
                    .height(with(density) { rowHeightPx.toDp() })
                    .background(colors.activeTileSurface, trackShape)
            )
        }

        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .onSizeChanged { rowHeightPx = it.height }
        ) {
            items.forEachIndexed { index, item ->
                SegmentedButtonSegment(
                    item = item,
                    selected = index == selectedIndex,
                    enabled = enabled && item.enabled,
                    width = width,
                    config = config,
                    colors = colors,
                    tileShape = trackShape,
                    onClick = {
                        // Single-select: re-tapping the active segment is a no-op.
                        if (item.id != selectedId) onSelectionChange(item.id)
                    },
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        segmentBounds[index] =
                            coordinates.positionInParent().x to coordinates.size.width.toFloat()
                    }
                )
            }
        }
    }
}
