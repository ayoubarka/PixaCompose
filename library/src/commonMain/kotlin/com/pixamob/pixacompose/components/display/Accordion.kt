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

/** Color-hierarchy for the panel surface. */
enum class AccordionVariant {
    Default,
    Outlined,
    Filled
}

/**
 * Expand behavior: limit to one panel at a time, or allow multiple.
 * Single — for step-by-step processes, progressive disclosure, form wizards.
 * Multiple — for FAQs and comparative content.
 */
enum class AccordionExpansionMode {
    Single,
    Multiple
}

/**
 * Sizing density: [Standard] has a minimum header height and supports all artwork sizes;
 * [Compact] has no height floor, supports small/medium artwork only, uses 12dp padding.
 */
enum class AccordionDensity {
    Standard,
    Compact
}

/**
 * Layout mode: [FullWidth] spans the entire container;
 * [Inset] applies fixed outer margins (16dp), corner radius (12dp), and border (2dp).
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
 * Resolves [AccordionDensity] × [artworkSize] into concrete layout values.
 * - [AccordionDensity.Standard]: 64dp minimum header height, all artwork sizes.
 * - [AccordionDensity.Compact]: no height floor, 12dp padding, artwork coerced to Medium max.
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
 * Fixed layout values for [AccordionLayout.Inset]: 16dp margin, 12dp radius, 2dp border.
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
 * Groups secondary/supporting content to reduce page scroll.
 * Not for essential information — hiding content reduces visibility.
 *
 * ### Anatomy
 * Heading: title, chevron [expandIcon], optional leading [icon], bottom divider.
 * Content expands below the heading. Content is one level deep
 * (no nested accordion support — use a tree component for that).
 *
 * ### Variants
 * [AccordionDensity] (Standard/Compact), [AccordionLayout] (FullWidth/Inset),
 * [AccordionVariant] (Default/Outlined/Filled).
 *
 * ### States
 * Collapsed, Expanded, Disabled, Focus (keyboard ring), Pressed (ripple).
 *
 * Chevron: required [expandIcon] is a chevron-down painter; rotated 180° when expanded.
 * Only one asset needed — never swap painters or use chevronRight.
 *
 * ### Animation
 * Expand/collapse: 500ms via [AccelerateDecelerateEasing]. Content fades/slides
 * in over 200ms. Panel never wraps content in its own scroll container.
 *
 * ### Accessibility
 * Header carries Role.Button, collapsed/expanded stateDescription, title contentDescription.
 * Keyboard: Enter/Space toggles via Compose's key-to-click.
 *
 * @param title Header label (single line recommended)
 * @param expanded Whether content is visible
 * @param onExpandedChange Expanded state callback
 * @param expandIcon Chevron-down painter (required); rotated 180° when expanded
 * @param modifier Modifier for styling
 * @param variant Color hierarchy (Default: Default)
 * @param density Sizing tier (Default: Standard)
 * @param layout Layout mode (Default: FullWidth)
 * @param artworkSize Leading [icon] size (Default: Small); coerced to Medium max under [AccordionDensity.Compact]
 * @param colors Custom color override
 * @param icon Optional leading artwork
 * @param enabled Whether the panel is interactive
 * @param staggerDelayMillis Staggered content enter delay for [PixaAccordionGroup] (50ms per item)
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
 *
 * Items closed by default — avoid pre-expanding every item to prevent cognitive overload.
 * Applies 50ms-per-item content-enter stagger via [PixaAccordion.staggerDelayMillis]
 * (meaningful when multiple items expand at once).
 *
 * @param items List of accordion panels
 * @param expandIcon Chevron-down painter (shared across all panels)
 * @param modifier Modifier for styling
 * @param variant Color hierarchy (passed to each panel)
 * @param density Sizing tier (passed to each panel)
 * @param layout Layout mode (passed to each panel)
 * @param expansionMode Single or Multiple panels open at once
 * @param colors Custom color override (passed to each panel)
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
