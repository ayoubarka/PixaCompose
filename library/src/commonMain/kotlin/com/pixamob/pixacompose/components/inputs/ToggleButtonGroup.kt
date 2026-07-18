package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.IconSource
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.display.PixaImage
import com.pixamob.pixacompose.components.display.PixaImageSource
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.theme.forVariant
import com.pixamob.pixacompose.utils.AnimationUtils

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Selection behavior — single and multi selection pattern. Both modes toggle off:
 * re-tapping the selected option clears the group to an empty selection.
 *
 * [Single] is *not* radio behavior (re-tapping the current selection clears it).
 * This differs from `PixaButtonGroup`'s `Single` mode, where re-tapping is a no-op.
 */
enum class ToggleGroupSelectionMode {
    Single,
    Multi
}

/**
 * Three layout variants:
 * - [Minimal] — compact options that wrap
 * - [ListView] — full-width options stacked vertically
 * - [Gallery] — fixed-width option cards that wrap
 */
enum class ToggleGroupLayout {
    Minimal,
    ListView,
    Gallery
}

/**
 * Optional lead element — Icon or Image. Sealed type because the two render differently:
 * an icon is tinted and icon-sized; an image is untinted and cropped.
 */
@Immutable
sealed interface ToggleLeadElement {
    @Immutable
    data class Icon(val source: IconSource) : ToggleLeadElement

    @Immutable
    data class Image(val source: PixaImageSource) : ToggleLeadElement
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/**
 * One selectable option.
 *
 * [title] and [subtitle] are the named content parts. Content rules (titles: sentence case,
 * no ending punctuation, at most ~20 characters; subtitles: sentence case) are the
 * author's to honour.
 */
@Immutable
@Stable
data class ToggleOption(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val lead: ToggleLeadElement? = null,
    val enabled: Boolean = true,
    val contentDescription: String? = null
)

/** Resolved per-size type scale for an option tile. */
@Stable
private data class ToggleOptionTypography(
    val title: TextStyle,
    val subtitle: TextStyle
)

/** Resolved per-state colors for an option tile. */
@Stable
private data class ToggleOptionColors(
    val background: Color,
    val border: Color,
    val borderWidth: Dp,
    val title: Color,
    val subtitle: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/** Minimum height (40dp) for minimal layout, mapped via [HierarchicalSize.Container.Small]. */
private val OptionMinHeight = HierarchicalSize.Container.Small

/**
 * Per-layout width constraints (Minimal: min 72 / max 600; List view: min 140 / max 600;
 * Gallery: fixed 140). Local dp because [HierarchicalSize] has no option-tile width category.
 */
private val MinimalMinWidth = 72.dp
private val ListMinWidth = 140.dp
private val GalleryWidth = 140.dp
private val OptionMaxWidth = 600.dp

/**
 * Spec: on selection "the border width increases". No values are given, so this reuses the shared
 * border ladder's two lowest meaningful tiers rather than inventing numbers.
 */
private val RestingBorderWidth = HierarchicalSize.Border.Compact
private val SelectedBorderWidth = HierarchicalSize.Border.Medium

/** Title wraps to 2 lines for title-only options. */
private const val TitleMaxLines = 2

/**
 * Subtitle capped at 2 lines to maintain consistent tile height across the group.
 */
private const val SubtitleMaxLines = 2

@Composable
private fun SizeVariant.toggleTypography(): ToggleOptionTypography {
    val typography = AppTheme.typography
    return when (this) {
        SizeVariant.Small -> ToggleOptionTypography(
            title = typography.bodyBold,
            subtitle = typography.captionLight
        )

        SizeVariant.Large -> ToggleOptionTypography(
            title = typography.titleBold,
            subtitle = typography.bodyLight
        )

        // Medium is the library's anchor and the fallback for unscaled tiers.
        else -> ToggleOptionTypography(
            title = typography.subtitleBold,
            subtitle = typography.captionRegular
        )
    }
}

/**
 * Resolves the four states — enabled, hover, disabled, focused — plus selection.
 *
 * Selection maps to tokens: border to `baseBorderFocus`, background to `baseSurfaceSubtle`,
 * and increased border width. Hover maps to `baseSurfaceFocus`; focus uses the strong border.
 */
@Composable
private fun toggleOptionColors(
    selected: Boolean,
    enabled: Boolean,
    hovered: Boolean,
    focused: Boolean
): ToggleOptionColors {
    val colors = AppTheme.colors

    return when {
        !enabled -> ToggleOptionColors(
            background = colors.baseSurfaceDisabled,
            border = colors.baseBorderDisabled,
            borderWidth = RestingBorderWidth,
            title = colors.baseContentDisabled,
            subtitle = colors.baseContentDisabled
        )

        selected -> ToggleOptionColors(
            background = colors.baseSurfaceSubtle,
            border = colors.baseBorderFocus,
            borderWidth = SelectedBorderWidth,
            title = colors.baseContentTitle,
            subtitle = colors.baseContentBody
        )

        else -> ToggleOptionColors(
            background = if (hovered) colors.baseSurfaceFocus else Color.Transparent,
            border = if (focused) colors.baseBorderFocus else colors.baseBorderDefault,
            borderWidth = RestingBorderWidth,
            title = colors.baseContentTitle,
            subtitle = colors.baseContentBody
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL TOGGLE BUTTON GROUP
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ToggleLead(
    lead: ToggleLeadElement,
    layout: ToggleGroupLayout,
    size: SizeVariant,
    tint: Color,
    shape: Shape,
    contentDescription: String?
) {
    when (lead) {
        is ToggleLeadElement.Icon -> PixaIcon(
            source = lead.source,
            contentDescription = contentDescription,
            tint = tint,
            size = size
        )

        is ToggleLeadElement.Image -> if (layout == ToggleGroupLayout.Gallery) {
            // Gallery tiles are fixed-width, so the header image takes the full width. A square is
            // Square is the neutral default — gallery width is fixed but no image aspect ratio is stated.
            PixaImage(
                source = lead.source,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(shape),
                shape = shape
            )
        } else {
            PixaImage(
                source = lead.source,
                contentDescription = contentDescription,
                modifier = Modifier.size(HierarchicalSize.Container.forVariant(size)),
                shape = shape
            )
        }
    }
}

@Composable
private fun ToggleOptionTile(
    option: ToggleOption,
    selected: Boolean,
    enabled: Boolean,
    layout: ToggleGroupLayout,
    size: SizeVariant,
    selectionMode: ToggleGroupSelectionMode,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val colors = toggleOptionColors(selected, enabled, hovered, focused)
    val typography = size.toggleTypography()
    val shape = AppTheme.shapes.rounded.forVariant(size)
    val padding = HierarchicalSize.Spacing.forVariant(size)
    val gap = HierarchicalSize.Spacing.Small

    val animatedBackground by animateColorAsState(colors.background, AnimationUtils.smoothSpring())
    val animatedBorder by animateColorAsState(colors.border, AnimationUtils.smoothSpring())
    val animatedBorderWidth by animateDpAsState(colors.borderWidth, AnimationUtils.fastTween())

    // Spec anti-pattern: "Don't add additional controls (checkbox, radio, checkmark)" — selection is
    // conveyed only by the container. The role below is semantics-only, so assistive tech still
    // announces the selection model without any control being drawn.
    val selectionModifier = when (selectionMode) {
        ToggleGroupSelectionMode.Single -> Modifier.selectable(
            selected = selected,
            enabled = enabled,
            role = Role.RadioButton,
            interactionSource = interactionSource,
            indication = null,
            onClick = onToggle
        )

        ToggleGroupSelectionMode.Multi -> Modifier.toggleable(
            value = selected,
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = interactionSource,
            indication = null,
            onValueChange = { onToggle() }
        )
    }

    val container = modifier
        .clip(shape)
        .background(animatedBackground)
        .border(width = animatedBorderWidth, color = animatedBorder, shape = shape)
        .then(selectionModifier)
        .padding(padding)

    val text: @Composable () -> Unit = {
        BasicText(
            text = option.title,
            style = typography.title.copy(color = colors.title),
            maxLines = TitleMaxLines,
            overflow = TextOverflow.Ellipsis
        )
        if (option.subtitle != null) {
            BasicText(
                text = option.subtitle,
                style = typography.subtitle.copy(color = colors.subtitle),
                maxLines = SubtitleMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    when (layout) {
        // Gallery stacks the lead image above the content area.
        ToggleGroupLayout.Gallery -> Column(
            modifier = container,
            verticalArrangement = Arrangement.spacedBy(gap)
        ) {
            option.lead?.let {
                ToggleLead(it, layout, size, colors.title, shape, option.contentDescription)
            }
            text()
        }

        // Minimal and List view keep the lead element inline, ahead of the content area.
        else -> Row(
            modifier = container,
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            option.lead?.let {
                ToggleLead(it, layout, size, colors.title, shape, option.contentDescription)
            }
            Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Nano)) {
                text()
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaToggleButtonGroup — Single or multi selection pattern with visual emphasis for available choices.
 *
 * ### Purpose
 * Use when selection options exist in isolation (not part of multiple selection groups on the same
 * page). For short binary choices use [PixaRadioButton]; for multi-select in forms use [PixaCheckbox].
 *
 * ### Anatomy
 * Container of option tiles. Each tile: border + container + optional lead element (icon or image) +
 * [title] + optional [subtitle]. The border/background *are* the selection indicator — no checkmarks.
 *
 * ### Variants
 * [ToggleGroupLayout]: Minimal / ListView / Gallery.
 * [ToggleGroupSelectionMode]: Single / Multi. Both toggle off (re-tap clears selection).
 *
 * ### States
 * Enabled, hover, focused, disabled. No error state. [enabled] disables the group;
 * [ToggleOption.enabled] disables one tile.
 *
 * ### Sizing
 * Per-layout width rules: Minimal min 72dp, ListView min 140dp (full width), Gallery fixed 140dp;
 * all cap at 600dp, 40dp minimum height. [size] drives type scale, padding, radius, lead-element size.
 *
 * @param options The options to render, in order
 * @param selectedIds Currently selected option ids
 * @param onSelectionChange Called with the new selection; may be empty (both modes toggle off)
 * @param modifier Modifier
 * @param selectionMode Single or multi selection
 * @param layout Option anatomy/layout
 * @param size Size applied to every option
 * @param enabled Whether the whole group is enabled
 */
@Composable
fun PixaToggleButtonGroup(
    options: List<ToggleOption>,
    selectedIds: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    selectionMode: ToggleGroupSelectionMode = ToggleGroupSelectionMode.Single,
    layout: ToggleGroupLayout = ToggleGroupLayout.Minimal,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true
) {
    val spacing = HierarchicalSize.Spacing.forVariant(size)

    fun handleToggle(id: String) {
        // Spec: the selection toggles on and off in BOTH modes — Single clears to empty when the
        // already-selected option is tapped again, unlike a radio group.
        val next = when (selectionMode) {
            ToggleGroupSelectionMode.Single ->
                if (id in selectedIds) emptySet() else setOf(id)

            ToggleGroupSelectionMode.Multi ->
                if (id in selectedIds) selectedIds - id else selectedIds + id
        }
        onSelectionChange(next)
    }

    @Composable
    fun tile(option: ToggleOption, tileModifier: Modifier) {
        ToggleOptionTile(
            option = option,
            selected = option.id in selectedIds,
            enabled = enabled && option.enabled,
            layout = layout,
            size = size,
            selectionMode = selectionMode,
            onToggle = { handleToggle(option.id) },
            modifier = tileModifier.heightIn(min = OptionMinHeight)
        )
    }

    when (layout) {
        ToggleGroupLayout.Minimal -> FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            options.forEach { option ->
                tile(option, Modifier.widthIn(min = MinimalMinWidth, max = OptionMaxWidth))
            }
        }

        ToggleGroupLayout.ListView -> Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            options.forEach { option ->
                tile(
                    option,
                    Modifier
                        .fillMaxWidth()
                        .widthIn(min = ListMinWidth, max = OptionMaxWidth)
                )
            }
        }

        ToggleGroupLayout.Gallery -> FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            options.forEach { option ->
                tile(option, Modifier.width(GalleryWidth))
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * SingleToggleButtonGroup — single-select convenience wrapper over [PixaToggleButtonGroup].
 *
 * [selectedId] is nullable because single-select toggle can still result in no selection.
 */
@Composable
fun SingleToggleButtonGroup(
    options: List<ToggleOption>,
    selectedId: String?,
    onSelectionChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
    layout: ToggleGroupLayout = ToggleGroupLayout.Minimal,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true
) {
    PixaToggleButtonGroup(
        options = options,
        selectedIds = selectedId?.let { setOf(it) } ?: emptySet(),
        onSelectionChange = { onSelectionChange(it.firstOrNull()) },
        modifier = modifier,
        selectionMode = ToggleGroupSelectionMode.Single,
        layout = layout,
        size = size,
        enabled = enabled
    )
}

/**
 * MultiToggleButtonGroup — multi-select convenience wrapper over [PixaToggleButtonGroup].
 */
@Composable
fun MultiToggleButtonGroup(
    options: List<ToggleOption>,
    selectedIds: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    layout: ToggleGroupLayout = ToggleGroupLayout.Minimal,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true
) {
    PixaToggleButtonGroup(
        options = options,
        selectedIds = selectedIds,
        onSelectionChange = onSelectionChange,
        modifier = modifier,
        selectionMode = ToggleGroupSelectionMode.Multi,
        layout = layout,
        size = size,
        enabled = enabled
    )
}
