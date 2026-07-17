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
 * Selection behavior, from the eBay Toggle Button Group spec's "single and multi selection pattern".
 *
 * Note that **both** modes toggle off — spec: "The selection state toggles on and off for both multi
 * and single select option list styles." [Single] is therefore *not* radio behavior: re-tapping the
 * selected option clears the group to an empty selection. This is the key semantic difference from
 * `PixaButtonGroup`'s `Single` mode, where re-tapping the current selection is a deliberate no-op.
 *
 * There is no `None` mode here: the spec is a selection pattern and explicitly rules out plain
 * actions — "Do not add CTAs or additional buttons to the option list." Use `PixaButtonGroup` for
 * groups of independently-triggered actions.
 */
enum class ToggleGroupSelectionMode {
    Single,
    Multi
}

/**
 * The spec's three layout variants.
 *
 * - [Minimal] — compact options that wrap; the lightest treatment.
 * - [ListView] — full-width options stacked vertically, lead element inline.
 * - [Gallery] — fixed-width option cards that wrap, lead image stacked above the text.
 *
 * This is an anatomy axis, unlike `ButtonGroupLayout`'s Clustered/HorizontalScroll, which is an
 * overflow axis. The two enums are not interchangeable.
 */
enum class ToggleGroupLayout {
    Minimal,
    ListView,
    Gallery
}

/**
 * The spec's optional "Lead element (optional — Icon or Image)". Modelled as a sealed type because
 * the two cases genuinely render differently (an icon is tinted and icon-sized; an image is
 * untinted, cropped, and in [ToggleGroupLayout.Gallery] becomes the tile's header), so a single
 * nullable slot could not express either faithfully.
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
 * [title] and [subtitle] are the spec's named content parts. Content rules the spec states but which
 * are the author's to honour (enforcing them in code would silently corrupt copy): titles are
 * sentence case, no ending punctuation, and at most ~20 characters; subtitles are sentence case.
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

/**
 * Spec: "Minimum height: 40px" for the minimal layout. Maps exactly onto the shared container
 * ladder's 40dp "secondary containers" tier — no local literal needed.
 */
private val OptionMinHeight = HierarchicalSize.Container.Small

/**
 * The spec's per-layout width constraints (Minimal: min 72 / max 600; List view: min 140 / max 600;
 * Gallery: fixed 140). These stay local raw dp deliberately: they are one-off constraint values for
 * this pattern, and `HierarchicalSize` has no option-tile width category to hang them on. Inventing
 * one for a single component would add noise rather than reuse — the same reasoning behind the
 * existing one-off `HierarchicalSize.Container.DialogMaxWidth`.
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

/** Spec: "Title can wrap to 2 lines for title-only option." */
private const val TitleMaxLines = 2

/**
 * Spec: "Subtitle max: 2 lines" (its anti-pattern list separately says never beyond 3). Capping is
 * structural here, not editorial: the spec also requires that "all buttons in group must maintain
 * consistent height", which an unbounded subtitle would break.
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

        // The spec names only Small / Medium / Large; Medium is the library's anchor and the
        // fallback for tiers this pattern does not scale to.
        else -> ToggleOptionTypography(
            title = typography.subtitleBold,
            subtitle = typography.captionRegular
        )
    }
}

/**
 * Resolves the spec's four states — enabled, hover, disabled, focused — plus selection.
 *
 * Selection maps the spec's three named changes onto tokens: border colour to `border.strong`
 * ([AppTheme.colors] `baseBorderFocus`, the strongest neutral border emphasis available), background
 * to `background.secondary` (`baseSurfaceSubtle`), and an increased border width. Hover and focus
 * have no values in the spec (it only points at its colour-token page), so they map to the nearest
 * existing interaction roles: `baseSurfaceFocus` for hover, and the strong border for focus so
 * keyboard users get a visible ring.
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
            // the neutral default: the spec fixes gallery width but states no image aspect ratio.
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
 * PixaToggleButtonGroup — a single and multi selection pattern that provides increased visual
 * emphasis for the available choices.
 *
 * ### Purpose
 * Spec: use "when selection options exist in isolation, meaning they are not part of a multiple
 * selection groups", and avoid it "when multiple selection sections appear on the same page". Reach
 * for something else when the spec says so: `PixaRadioButton` for short binary choices,
 * `PixaCheckbox` for multiple choice inside a grouped form, a filter chip for filtering, and
 * `PixaTab` for navigating between views. This is a richer *option* pattern than `PixaButtonGroup`,
 * which groups plain actions — the spec forbids putting CTAs in this list.
 *
 * ### Anatomy
 * A container of option tiles. Each tile is border + container + optional lead element (icon or
 * image) + a content area holding [ToggleOption.title] and optional [ToggleOption.subtitle]. The
 * tile's own border and background *are* the selection indicator; the spec forbids adding a
 * checkbox, radio, or checkmark on top.
 *
 * ### Variants
 * [ToggleGroupLayout] covers the spec's Minimal / List view / Gallery layouts, and
 * [ToggleGroupSelectionMode] its single/multi selection. Both selection modes toggle off, so an
 * empty selection is reachable in either.
 *
 * ### States
 * Enabled, hover, focused, and disabled — the four the spec lists. It defines no error state, so
 * none is offered. [enabled] disables the whole group; [ToggleOption.enabled] disables one tile.
 *
 * ### Sizing
 * The spec's per-layout width rules are applied structurally: Minimal is min 72dp wide, List view
 * min 140dp and full width, Gallery a fixed 140dp; all cap at 600dp, with a 40dp minimum height.
 * [size] drives the shared type scale, padding, radius, and lead-element size.
 *
 * ### Adaptive behavior
 * The spec names Small / Medium / Large sizes but gives no breakpoints, so [size] stays explicit and
 * caller-controlled rather than reading `AppTheme.adaptiveSizeVariant` — consistent with
 * `PixaButtonGroup`, which likewise leaves the breakpoint choice to the caller.
 *
 * ### Usage notes
 * Keep options "lightweight and short enough to scan instantly" and self-explanatory. The spec's
 * "all buttons in group must maintain consistent height/width" is enforced where it is structural
 * (one [size] and [layout] for the group, a capped subtitle, fixed gallery width); beyond that it is
 * a content rule, since only the author can keep option copy balanced.
 *
 * @param options The options to render, in order
 * @param selectedIds Currently selected option ids
 * @param onSelectionChange Called with the new selection after a tap; may be empty (both modes toggle off)
 * @param modifier Modifier for the group container
 * @param selectionMode Single or multi selection (Default: [ToggleGroupSelectionMode.Single])
 * @param layout Option anatomy/layout (Default: [ToggleGroupLayout.Minimal])
 * @param size Size applied to every option (Default: [SizeVariant.Medium])
 * @param enabled Whether the whole group is enabled (Default: true)
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
 * [selectedId] is nullable because the spec's single-select still toggles off to no selection.
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
