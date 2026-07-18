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
 * Width distribution mode. [FixedWidth]: equal-width tabs, no scrolling, up to 4 tabs recommended.
 * [IntrinsicWidth]: content-width tabs with horizontal scroll for overflow.
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
 * Navigational tab bar for switching between content groups.
 *
 * ### Anatomy
 * Bottom-bordered container → tabs (label + optional icon) → animated sliding indicator
 * (single overlay tracking selected tab's measured bounds).
 *
 * ### Variants
 * [TabBarVariant]: Underline, Filled, Pill.
 * [TabBarWidthMode.FixedWidth]: equal-width, no scrolling, up to 4 tabs recommended.
 * [TabBarWidthMode.IntrinsicWidth]: content-width, scrollable overflow.
 *
 * ### Sizing
 * [SizeVariant] Small (40dp), Medium (48dp), Large (56dp) — height, padding, icon size, text style.
 * Wide viewports add leading margin for intrinsic mode + drop shadow.
 *
 * @param tabs Tab items
 * @param selectedIndex 0-based selected index
 * @param modifier Modifier for the tab bar
 * @param onTabSelected Selection callback
 * @param variant Underline, Filled, or Pill
 * @param widthMode FixedWidth (even) or IntrinsicWidth (content-sized)
 * @param size Height/padding/icon preset
 * @param colors Custom color overrides
 * @param scrollable Legacy alias for IntrinsicWidth
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

    // Single sliding indicator driven by the selected tab's measured bounds.
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
                // Wide viewports give Intrinsic tabs leading margin for content alignment.
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

            // Single sliding indicator overlay — Underline variant only.
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

        // Container bottom border.
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
            // Single line, no truncation.
            BasicText(
                text = item.title,
                style = sizeConfig.textStyle.copy(color = contentColor),
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        }
    }
}
