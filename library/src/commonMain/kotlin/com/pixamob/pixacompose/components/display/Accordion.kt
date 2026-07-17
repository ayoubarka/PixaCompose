package com.pixamob.pixacompose.components.display

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AccelerateDecelerateEasing
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.MotionDuration
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/** Pixa's color-hierarchy vocabulary for the panel surface — orthogonal to the
 * Uber spec's own Standard/Compact (density) and Full-width/Inset (layout)
 * variant axes below, which are the ones the spec actually defines. */
enum class AccordionVariant {
    Default,
    Outlined,
    Filled
}

/**
 * Maps 1:1 onto the Uber Base spec's expand-behavior variant:
 * "Single active: limits an accordion to expand only one item at a time...
 * best for step-by-step processes, progressive disclosure, and form wizards."
 * "Multi-active: enables an accordion to expand multiple panels at a time...
 * for FAQs and comparative content."
 */
enum class AccordionExpansionMode {
    Single,
    Multiple
}

/**
 * Uber Base's "Sizing" variant: [Standard] has a minimum cell height and
 * supports small/medium/large/custom artwork; [Compact] has no minimum
 * height, only supports small/medium artwork, and uses 12dp top/bottom
 * padding instead of a height floor.
 */
enum class AccordionDensity {
    Standard,
    Compact
}

/**
 * Uber Base's "Layout" variant: [FullWidth] spans the entire container;
 * [Inset] is a fixed spec combination — 16dp left/right outer margin, 12dp
 * corner radius, 2dp outline stroke — independent of [AccordionVariant] or
 * [AccordionDensity].
 */
enum class AccordionLayout {
    FullWidth,
    Inset
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Stable
data class AccordionItem(
    val title: String,
    val content: @Composable () -> Unit,
    val icon: Painter? = null,
    val enabled: Boolean = true
)

@Immutable
@Stable
data class AccordionColors(
    val background: Color,
    val headerBackground: Color,
    val contentBackground: Color,
    val title: Color,
    val icon: Color,
    val border: Color,
    val divider: Color,
    val focusBorder: Color
)

@Immutable
@Stable
data class AccordionSizeConfig(
    val headerMinHeight: Dp,
    val headerVerticalPadding: Dp,
    val headerHorizontalPadding: Dp,
    val contentPadding: Dp,
    val artworkSize: Dp,
    val titleStyle: TextStyle,
    val cornerRadius: Dp,
    val borderWidth: Dp,
    val dividerThickness: Dp,
    val spacing: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getAccordionTheme(variant: AccordionVariant): AccordionColors {
    val colors = AppTheme.colors
    return when (variant) {
        AccordionVariant.Default -> AccordionColors(
            background = colors.baseSurfaceSubtle,
            headerBackground = colors.baseSurfaceSubtle,
            contentBackground = colors.baseSurfaceSubtle,
            title = colors.baseContentTitle,
            icon = colors.baseContentBody,
            border = Color.Transparent,
            divider = colors.baseBorderSubtle,
            focusBorder = colors.baseBorderFocus
        )
        AccordionVariant.Outlined -> AccordionColors(
            background = Color.Transparent,
            headerBackground = Color.Transparent,
            contentBackground = Color.Transparent,
            title = colors.baseContentTitle,
            icon = colors.baseContentBody,
            border = colors.baseBorderDefault,
            divider = colors.baseBorderSubtle,
            focusBorder = colors.baseBorderFocus
        )
        AccordionVariant.Filled -> AccordionColors(
            background = colors.baseSurfaceSubtle,
            headerBackground = colors.baseSurfaceDefault,
            contentBackground = colors.baseSurfaceSubtle,
            title = colors.baseContentTitle,
            icon = colors.baseContentBody,
            border = Color.Transparent,
            divider = colors.baseBorderSubtle,
            focusBorder = colors.baseBorderFocus
        )
    }
}

/**
 * [density]/[artworkSize] resolve the spec's two sizing tiers:
 * - [AccordionDensity.Standard]: a 64dp header floor ([HierarchicalSize.ListItem.Large]
 *   — "comfortable lists," the closest existing token to the spec's 64px
 *   minimum cell height) and small/medium/large/custom artwork.
 * - [AccordionDensity.Compact]: no height floor, 12dp top/bottom padding
 *   ([HierarchicalSize.Spacing.Medium]), and small/medium artwork only —
 *   [SizeVariant.Large]/above is coerced down to [SizeVariant.Medium].
 */
@Composable
private fun getAccordionSizeConfig(
    density: AccordionDensity,
    artworkSize: SizeVariant
): AccordionSizeConfig {
    val typography = AppTheme.typography
    val effectiveArtworkSize = if (density == AccordionDensity.Compact) {
        if (artworkSize.ordinal > SizeVariant.Medium.ordinal) SizeVariant.Medium else artworkSize
    } else {
        artworkSize
    }

    return AccordionSizeConfig(
        headerMinHeight = if (density == AccordionDensity.Standard) HierarchicalSize.ListItem.Large else 0.dp,
        headerVerticalPadding = if (density == AccordionDensity.Compact) {
            HierarchicalSize.Spacing.Medium
        } else {
            HierarchicalSize.Spacing.Small
        },
        headerHorizontalPadding = HierarchicalSize.Spacing.Medium,
        contentPadding = HierarchicalSize.Spacing.Medium,
        artworkSize = HierarchicalSize.Icon.forVariant(effectiveArtworkSize),
        titleStyle = typography.bodyBold,
        cornerRadius = HierarchicalSize.Radius.Medium,
        borderWidth = HierarchicalSize.Border.Compact,
        dividerThickness = HierarchicalSize.Divider.Compact,
        spacing = HierarchicalSize.Spacing.Small
    )
}

/**
 * [AccordionLayout.Inset]'s outer margin/corner/stroke are a fixed spec
 * combination, not size- or variant-driven — 16dp/12dp/2dp all happen to
 * already be exact [HierarchicalSize] tiers ([Padding.Large], [Radius.Large],
 * [Border.Medium]).
 */
private object AccordionInsetSpec {
    val outerMargin get() = HierarchicalSize.Padding.Large
    val cornerRadius get() = HierarchicalSize.Radius.Large
    val borderWidth get() = HierarchicalSize.Border.Medium
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL ACCORDION
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun accordionShapeFor(layout: AccordionLayout, sizeConfig: AccordionSizeConfig): Shape =
    if (layout == AccordionLayout.Inset) {
        RoundedCornerShape(AccordionInsetSpec.cornerRadius)
    } else {
        RoundedCornerShape(sizeConfig.cornerRadius)
    }

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaAccordion — a single collapsible panel.
 *
 * ### Purpose
 * "A vertical stack of collapsible panels that allows users to expand or
 * collapse each panel individually" — shortens pages by grouping related,
 * secondary/supporting content and reducing scroll. Not for essential
 * information: hiding content behind an accordion reduces its visibility.
 *
 * ### Anatomy
 * Heading area (title — required, [expandIcon] chevron button — required,
 * bottom divider — required, optional leading [icon] artwork) + a content
 * area that expands strictly below the title, never above. Content is one
 * level deep — nest a [PixaAccordionGroup]'s own content inside another
 * accordion only if you actually want a tree view instead (not supported
 * here, use a dedicated tree component).
 *
 * ### Variants
 * [AccordionDensity] (Standard/Compact — the spec's own sizing axis),
 * [AccordionLayout] (FullWidth/Inset — the spec's own layout axis), and
 * [AccordionVariant] (Default/Outlined/Filled — Pixa's color-hierarchy
 * vocabulary, layered on top since the spec doesn't prescribe a color scheme).
 *
 * ### States
 * Enabled (collapsed), Active (expanded), Disabled, Focus (keyboard-visible
 * border ring), Pressed (ripple). Preloading/Hover are left to the caller
 * (skeleton loading and desktop-hover styling aren't part of this component's
 * own state machine, matching how `PixaButton` handles the same states).
 *
 * ### Icon rule
 * [expandIcon] is a required chevron-down painter — the spec fixes this
 * icon's identity ("ChevronDown (collapsed) ChevronUp (expanded)... do not
 * use chevronRight") and treats it as mandatory anatomy, so it is no longer
 * an optional parameter. It rotates 180° to represent the up state rather
 * than swapping painters, so only one asset is required per accordion.
 *
 * ### Behavior
 * Expand/collapse runs over [AnimationUtils]'s [MotionDuration.Slow] (500ms)
 * using [AccelerateDecelerateEasing] — Uber Base's "accelerate-decelerate,
 * Quintic easeInOut." It's the nearest bundled easing curve; Compose has no
 * exact Quintic bezier preset already tokenized. Content fades and slides in over
 * 200ms with a 24dp settle-in shift ([HierarchicalSize.Spacing.Huge] — the
 * nearest generic spacing tier to the spec's 32px; a dedicated 32dp token
 * doesn't exist outside component-specific ladders like Card/Chip, which
 * would be the wrong category to borrow from). The container — not this
 * panel — is expected to scroll; this composable never wraps [content] in
 * its own scroll container, per the spec's "content should not scroll inside
 * of an individual panel" rule.
 *
 * ### Accessibility
 * The header carries `Role.Button`, a `stateDescription` of "Collapsed"/
 * "Expanded", and a `contentDescription` combining the title and state —
 * matching the spec's required VoiceOver/TalkBack announcements. Keyboard:
 * the header is focusable and clickable, so Enter/Space toggles it via
 * Compose's built-in key-to-click handling, matching "tab to panel, press
 * Enter/Space to open/close."
 *
 * ### Adaptive behavior
 * Out of scope: the spec's breakpoints describe page-level grid column spans
 * (4/8/12-column), not a per-component size ladder — that's `AppTheme.pageMargin`/
 * screen-layout concern, not something this component should reimplement.
 *
 * @param title Header title (required, single line recommended)
 * @param expanded Whether content is visible
 * @param onExpandedChange Callback when expanded state changes
 * @param expandIcon Required chevron-down painter; rotated 180° when expanded
 * @param modifier Modifier for styling
 * @param variant Color hierarchy (Default: [AccordionVariant.Default])
 * @param density Sizing tier (Default: [AccordionDensity.Standard])
 * @param layout Layout tier (Default: [AccordionLayout.FullWidth])
 * @param artworkSize Leading [icon] size tier (Default: [SizeVariant.Small]); coerced to Medium or below under [AccordionDensity.Compact]
 * @param colors Custom colors overriding theme defaults
 * @param icon Optional leading artwork
 * @param enabled Whether the panel is interactive
 * @param staggerDelayMillis Extra enter delay for [content] (used by [PixaAccordionGroup] for the spec's 50ms-per-item stagger)
 * @param content Content shown when expanded
 */
@OptIn(ExperimentalStdlibApi::class)
@Composable
fun PixaAccordion(
    title: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    expandIcon: Painter,
    modifier: Modifier = Modifier,
    variant: AccordionVariant = AccordionVariant.Default,
    density: AccordionDensity = AccordionDensity.Standard,
    layout: AccordionLayout = AccordionLayout.FullWidth,
    artworkSize: SizeVariant = SizeVariant.Small,
    colors: AccordionColors? = null,
    icon: Painter? = null,
    enabled: Boolean = true,
    staggerDelayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    val themeColors = colors ?: getAccordionTheme(variant)
    val sizeConfig = getAccordionSizeConfig(density, artworkSize)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = AnimationUtils.standardTween(
            durationMillis = MotionDuration.Slow,
            easing = AccelerateDecelerateEasing
        )
    )

    val shape = accordionShapeFor(layout, sizeConfig)
    val effectiveBorderWidth = when {
        layout == AccordionLayout.Inset -> AccordionInsetSpec.borderWidth
        variant == AccordionVariant.Outlined -> sizeConfig.borderWidth
        else -> 0.dp
    }
    val effectiveBorderColor = if (layout == AccordionLayout.Inset && variant != AccordionVariant.Outlined) {
        AppTheme.colors.baseBorderDefault
    } else {
        themeColors.border
    }

    val stateLabel = if (expanded) "Expanded" else "Collapsed"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (layout == AccordionLayout.Inset) {
                    Modifier.padding(horizontal = AccordionInsetSpec.outerMargin)
                } else Modifier
            )
            .clip(shape)
            .background(themeColors.background)
            .then(
                if (effectiveBorderWidth > 0.dp) {
                    Modifier.border(effectiveBorderWidth, effectiveBorderColor, shape)
                } else Modifier
            )
    ) {
        // Heading area: label, optional artwork, required chevron, required divider.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(themeColors.headerBackground)
                .focusable(interactionSource = interactionSource)
                .then(
                    if (isFocused && enabled) {
                        Modifier.border(HierarchicalSize.Border.Medium, themeColors.focusBorder, shape)
                    } else Modifier
                )
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = pixaRipple(bounded = true),
                    role = Role.Button,
                    onClick = { onExpandedChange(!expanded) }
                )
                .semantics {
                    role = Role.Button
                    contentDescription = title
                    stateDescription = stateLabel
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = sizeConfig.headerMinHeight)
                    .padding(
                        horizontal = sizeConfig.headerHorizontalPadding,
                        vertical = sizeConfig.headerVerticalPadding
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (icon != null) {
                        PixaIcon(
                            painter = icon,
                            contentDescription = null,
                            tint = themeColors.icon,
                            modifier = Modifier.size(sizeConfig.artworkSize)
                        )
                        Spacer(modifier = Modifier.width(sizeConfig.spacing))
                    }
                    BasicText(
                        text = title,
                        style = sizeConfig.titleStyle.copy(color = themeColors.title)
                    )
                }

                // Collapse/expand button: fixed ChevronDown identity, rotated to
                // read as ChevronUp — never swapped for a different icon, and
                // never chevronRight (spec anti-pattern).
                PixaIcon(
                    painter = expandIcon,
                    contentDescription = null,
                    tint = themeColors.icon,
                    modifier = Modifier
                        .size(sizeConfig.artworkSize)
                        .rotate(rotation)
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sizeConfig.dividerThickness)
                    .background(themeColors.divider)
            )
        }

        // Content area — expands strictly below the heading, never scrolls
        // internally (the surrounding page/list is expected to scroll).
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = AnimationUtils.standardTween(
                    durationMillis = MotionDuration.Slow,
                    easing = AccelerateDecelerateEasing
                )
            ) + fadeIn(
                animationSpec = AnimationUtils.standardTween(
                    durationMillis = 200,
                    easing = LinearEasing,
                    delayMillis = staggerDelayMillis
                )
            ) + slideInVertically(
                initialOffsetY = { -HierarchicalSize.Spacing.Huge.value.toInt() },
                animationSpec = AnimationUtils.standardTween(
                    durationMillis = 200,
                    easing = LinearEasing,
                    delayMillis = staggerDelayMillis
                )
            ),
            exit = shrinkVertically(
                animationSpec = AnimationUtils.standardTween(
                    durationMillis = MotionDuration.Slow,
                    easing = AccelerateDecelerateEasing
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeColors.contentBackground)
                    .padding(sizeConfig.contentPadding)
            ) {
                content()
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * A vertical stack of [PixaAccordion] panels sharing one [expansionMode].
 * Per the spec, items are closed by default ("recommended for presenting a
 * compact and organized interface") and a caller should avoid pre-expanding
 * every item ("can make a page look busier and create cognitive overload").
 *
 * Applies the spec's 50ms-per-item content-enter stagger via
 * [PixaAccordion.staggerDelayMillis] — only meaningful when several items
 * expand at once (typically [AccordionExpansionMode.Multiple]).
 */
@Composable
fun PixaAccordionGroup(
    items: List<AccordionItem>,
    expandIcon: Painter,
    modifier: Modifier = Modifier,
    variant: AccordionVariant = AccordionVariant.Default,
    density: AccordionDensity = AccordionDensity.Standard,
    layout: AccordionLayout = AccordionLayout.FullWidth,
    expansionMode: AccordionExpansionMode = AccordionExpansionMode.Single,
    colors: AccordionColors? = null
) {
    var expandedIndices by remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        items.forEachIndexed { index, item ->
            val isExpanded = index in expandedIndices

            PixaAccordion(
                title = item.title,
                expanded = isExpanded,
                onExpandedChange = { expandedValue ->
                    expandedIndices = if (expandedValue) {
                        if (expansionMode == AccordionExpansionMode.Multiple) expandedIndices + index
                        else setOf(index)
                    } else {
                        expandedIndices - index
                    }
                },
                expandIcon = expandIcon,
                variant = variant,
                density = density,
                layout = layout,
                colors = colors,
                icon = item.icon,
                enabled = item.enabled,
                staggerDelayMillis = index * 50,
                content = item.content
            )
        }
    }
}
