package com.pixamob.pixacompose.components.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.WindowSizeClass
import com.pixamob.pixacompose.utils.elevationShadow

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class TabBarVariant {
    Underline,
    Filled,
    Pill
}

/**
 * Uber Base's "Width Types" variant axis. [FixedWidth]: "all tabs equal width (container width ÷ tab
 * count)... maximum 4 tabs on mobile... all content must fit without scrolling." [IntrinsicWidth]:
 * "width determined by label length plus 16px padding on sides... supports horizontal scrolling for
 * overflow."
 */
enum class TabBarWidthMode {
    FixedWidth,
    IntrinsicWidth
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Stable
data class TabBarItem(
    val title: String,
    val icon: Painter? = null,
    val badge: Int? = null,
    val enabled: Boolean = true
)

@Immutable
@Stable
data class TabBarColors(
    val background: Color,
    val selectedBackground: Color,
    val selectedContent: Color,
    val unselectedContent: Color,
    val indicator: Color,
    val disabledContent: Color,
    val border: Color
)

@Immutable
@Stable
data class TabBarSizeConfig(
    val height: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val iconSize: Dp,
    val textStyle: TextStyle,
    val indicatorHeight: Dp,
    val cornerRadius: Dp,
    val spacing: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getTabBarSizeConfig(size: SizeVariant): TabBarSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.Small -> TabBarSizeConfig(
            height = 40.dp,
            horizontalPadding = HierarchicalSize.Spacing.Medium,
            verticalPadding = HierarchicalSize.Spacing.Small,
            iconSize = HierarchicalSize.Icon.Small,
            textStyle = typography.labelSmall,
            indicatorHeight = 2.dp,
            cornerRadius = HierarchicalSize.Radius.Small,
            spacing = HierarchicalSize.Spacing.Compact
        )
        SizeVariant.Medium -> TabBarSizeConfig(
            height = 48.dp,
            horizontalPadding = HierarchicalSize.Spacing.Large,
            verticalPadding = HierarchicalSize.Spacing.Small,
            iconSize = HierarchicalSize.Icon.Medium,
            textStyle = typography.labelMedium,
            indicatorHeight = 3.dp,
            cornerRadius = HierarchicalSize.Radius.Medium,
            spacing = HierarchicalSize.Spacing.Small
        )
        SizeVariant.Large -> TabBarSizeConfig(
            height = 56.dp,
            horizontalPadding = HierarchicalSize.Spacing.Huge,
            verticalPadding = HierarchicalSize.Spacing.Medium,
            iconSize = HierarchicalSize.Icon.Large,
            textStyle = typography.labelLarge,
            indicatorHeight = 4.dp,
            cornerRadius = HierarchicalSize.Radius.Large,
            spacing = HierarchicalSize.Spacing.Medium
        )
        else -> TabBarSizeConfig(
            height = 48.dp,
            horizontalPadding = HierarchicalSize.Spacing.Large,
            verticalPadding = HierarchicalSize.Spacing.Small,
            iconSize = HierarchicalSize.Icon.Medium,
            textStyle = typography.labelMedium,
            indicatorHeight = 3.dp,
            cornerRadius = HierarchicalSize.Radius.Medium,
            spacing = HierarchicalSize.Spacing.Small
        )
    }
}

@Composable
private fun getTabBarTheme(variant: TabBarVariant): TabBarColors {
    val colors = AppTheme.colors
    return when (variant) {
        TabBarVariant.Underline -> TabBarColors(
            background = Color.Transparent,
            selectedBackground = Color.Transparent,
            selectedContent = colors.brandContentDefault,
            unselectedContent = colors.baseContentBody,
            indicator = colors.brandContentDefault,
            disabledContent = colors.baseContentDisabled,
            border = colors.baseBorderSubtle
        )
        TabBarVariant.Filled -> TabBarColors(
            background = colors.baseSurfaceSubtle,
            selectedBackground = colors.brandSurfaceDefault,
            selectedContent = colors.brandContentDefault,
            unselectedContent = colors.baseContentBody,
            indicator = colors.brandContentDefault,
            disabledContent = colors.baseContentDisabled,
            border = colors.baseBorderSubtle
        )
        TabBarVariant.Pill -> TabBarColors(
            background = colors.baseSurfaceSubtle,
            selectedBackground = colors.brandContentDefault,
            // Spec-adjacent fix: was a hardcoded `Color.White` literal, which violates this codebase's
            // "always AppTheme.colors.*" hard rule — the theme's own lightest surface tone reads the
            // same in light mode and stays theme-correct in dark mode.
            selectedContent = colors.baseSurfaceDefault,
            unselectedContent = colors.baseContentBody,
            indicator = colors.brandContentDefault,
            disabledContent = colors.baseContentDisabled,
            border = colors.baseBorderSubtle
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaTabBar — a navigational component that allows users to move easily between groups of related
 * content.
 *
 * ### Anatomy
 * Container with a 1px bottom border → tabs (label + optional leading icon) → an active tab highlight
 * that slides between tabs on selection change, per spec: "Transition: animated highlight movement
 * between tabs." The sliding indicator is drawn once, as a single overlay tracking the selected tab's
 * measured bounds, rather than a static mark redrawn under whichever tab happens to be selected.
 *
 * ### Variants
 * [widthMode] is the spec's real "Width Types" axis — [TabBarWidthMode.FixedWidth] (default; all tabs
 * equal width, `SpaceEvenly`, no scrolling — spec: "maximum 4 tabs on mobile") vs
 * [TabBarWidthMode.IntrinsicWidth] (content-width tabs, horizontally scrollable overflow, last tab
 * naturally aligns to the container's trailing edge at max scroll). The legacy [scrollable] flag still
 * works (`true` behaves as [TabBarWidthMode.IntrinsicWidth]) for callers on the pre-migration API.
 * [variant] (Underline/Filled/Pill) is a separate, Pixa-native styling axis — spec's own anatomy only
 * describes an underline-style highlight, so Filled/Pill are kept as pre-existing extensions rather than
 * removed, per this migration's backward-compatibility rule.
 *
 * ### Sizing / Adaptive behavior
 * [SizeVariant.Medium]'s horizontal padding (16dp) matches the spec's "16px padding" exactly, for both
 * width modes. On wide viewports ([WindowSizeClass.Medium]/[Expanded], ≥600dp), [TabBarWidthMode.IntrinsicWidth]
 * gets a leading margin — spec: "First tab aligns with content margins... leading margin compensates for
 * larger content margins" — and the container renders the spec's wide-breakpoint drop shadow (16px
 * blur/4dp Y/12% alpha, mapped to [HierarchicalSize.Shadow.Massive]).
 *
 * ### Usage notes
 * Spec recommends at least 2 tabs and at most 4 for [TabBarWidthMode.FixedWidth] on mobile — neither is
 * runtime-enforced (a component library shouldn't throw on a caller's content choices), just documented.
 *
 * @param tabs List of tab items
 * @param selectedIndex Currently selected tab index
 * @param onTabSelected Callback when tab is selected
 * @param modifier Modifier
 * @param variant Visual style variant (Pixa-native axis, separate from [widthMode])
 * @param widthMode Fixed (equal-width, spec default for mobile) or Intrinsic (content-width, scrollable)
 * @param size Size preset
 * @param colors Custom colors
 * @param scrollable Legacy flag; `true` is equivalent to `widthMode = TabBarWidthMode.IntrinsicWidth`
 */
@Composable
fun PixaTabBar(
    tabs: List<TabBarItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    variant: TabBarVariant = TabBarVariant.Underline,
    widthMode: TabBarWidthMode = TabBarWidthMode.FixedWidth,
    size: SizeVariant = SizeVariant.Medium,
    colors: TabBarColors? = null,
    scrollable: Boolean = false
) {
    val sizeConfig = getTabBarSizeConfig(size)
    val themeColors = colors ?: getTabBarTheme(variant)
    val scrollState = rememberScrollState()
    val effectiveWidthMode = if (scrollable) TabBarWidthMode.IntrinsicWidth else widthMode
    val isIntrinsic = effectiveWidthMode == TabBarWidthMode.IntrinsicWidth
    val isWide = AppTheme.windowSizeClass != WindowSizeClass.Compact
    val density = LocalDensity.current

    // Auto-center selected tab when horizontally scrollable
    if (isIntrinsic) {
        val estimatedTabWidthPx = with(density) {
            (sizeConfig.horizontalPadding * 2 + sizeConfig.iconSize + sizeConfig.spacing + 40.dp).roundToPx()
        }
        LaunchedEffect(selectedIndex) {
            val targetScroll = selectedIndex * estimatedTabWidthPx - (estimatedTabWidthPx / 2)
            scrollState.animateScrollTo(targetScroll.coerceAtLeast(0))
        }
    }

    // Selected tab's measured bounds, populated by each tab's onGloballyPositioned — drives the single
    // sliding indicator overlay rather than a per-tab static mark.
    val tabBounds = remember { mutableStateMapOf<Int, Pair<Dp, Dp>>() }
    val selectedBounds = tabBounds[selectedIndex]
    val animatedIndicatorX by animateDpAsState(
        targetValue = selectedBounds?.first ?: 0.dp,
        animationSpec = AnimationUtils.standardSpring(),
        label = "tab_indicator_x"
    )
    val animatedIndicatorWidth by animateDpAsState(
        targetValue = selectedBounds?.second ?: 0.dp,
        animationSpec = AnimationUtils.standardSpring(),
        label = "tab_indicator_width"
    )

    val rowModifier = if (isIntrinsic) {
        modifier.horizontalScroll(scrollState)
    } else {
        modifier.fillMaxWidth()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isWide) Modifier.elevationShadow(HierarchicalSize.Shadow.Massive, RoundedCornerShape(0.dp)) else Modifier)
            .background(themeColors.background)
    ) {
        Box {
            Row(
                modifier = rowModifier.height(sizeConfig.height),
                horizontalArrangement = if (isIntrinsic) Arrangement.Start else Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spec: wide viewports give Intrinsic tabs a leading margin so the first tab aligns
                // with the page's content margins instead of the raw container edge.
                if (isIntrinsic && isWide) {
                    Spacer(modifier = Modifier.width(sizeConfig.horizontalPadding))
                }
                tabs.forEachIndexed { index, tab ->
                    TabBarItemContent(
                        item = tab,
                        index = index,
                        tabCount = tabs.size,
                        selected = index == selectedIndex,
                        onClick = { if (tab.enabled) onTabSelected(index) },
                        variant = variant,
                        sizeConfig = sizeConfig,
                        colors = themeColors,
                        onBoundsMeasured = { offsetX, width -> tabBounds[index] = offsetX to width }
                    )
                }
            }

            // Single sliding indicator overlay — Underline variant only, per spec's own anatomy.
            if (variant == TabBarVariant.Underline && selectedBounds != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = animatedIndicatorX)
                        .width(animatedIndicatorWidth)
                        .height(sizeConfig.indicatorHeight)
                        .clip(RoundedCornerShape(sizeConfig.indicatorHeight / 2))
                        .background(themeColors.indicator)
                )
            }
        }

        // Spec: "Container with bottom border" (1px inside alignment).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HierarchicalSize.Border.Compact)
                .background(themeColors.border)
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun TabBarItemContent(
    item: TabBarItem,
    index: Int,
    tabCount: Int,
    selected: Boolean,
    onClick: () -> Unit,
    variant: TabBarVariant,
    sizeConfig: TabBarSizeConfig,
    colors: TabBarColors,
    onBoundsMeasured: (offsetX: Dp, width: Dp) -> Unit
) {
    val density = LocalDensity.current
    val contentColor by animateColorAsState(
        targetValue = when {
            !item.enabled -> colors.disabledContent
            selected -> colors.selectedContent
            else -> colors.unselectedContent
        },
        animationSpec = AnimationUtils.standardTween(200)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected && variant != TabBarVariant.Underline) colors.selectedBackground else Color.Transparent,
        animationSpec = AnimationUtils.standardTween(200)
    )

    val shape = when (variant) {
        TabBarVariant.Pill -> RoundedCornerShape(sizeConfig.cornerRadius)
        else -> RoundedCornerShape(sizeConfig.cornerRadius / 2)
    }

    Column(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                with(density) {
                    onBoundsMeasured(
                        coordinates.positionInParent().x.toDp(),
                        coordinates.size.width.toDp()
                    )
                }
            }
            .clip(shape)
            .background(backgroundColor)
            .clickable(
                enabled = item.enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .padding(horizontal = sizeConfig.horizontalPadding, vertical = sizeConfig.verticalPadding)
            .semantics {
                this.selected = selected
                contentDescription = item.title
                stateDescription = "${index + 1} of $tabCount"
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(sizeConfig.spacing)
        ) {
            if (item.icon != null) {
                PixaIcon(
                    painter = item.icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(sizeConfig.iconSize)
                )
            }
            // Spec: "Text doesn't wrap or truncate" — single line, unbounded (never Ellipsis).
            BasicText(
                text = item.title,
                style = sizeConfig.textStyle.copy(color = contentColor),
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        }
    }
}
