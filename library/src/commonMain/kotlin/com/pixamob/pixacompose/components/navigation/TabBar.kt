package com.pixamob.pixacompose.components.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.IconSize
import com.pixamob.pixacompose.theme.RadiusSize

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class TabBarVariant {
    Underline,
    Filled,
    Pill
}

enum class TabBarSize {
    Small,
    Medium,
    Large
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
    val disabledContent: Color
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
private fun getTabBarSizeConfig(size: TabBarSize): TabBarSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        TabBarSize.Small -> TabBarSizeConfig(
            height = 40.dp,
            horizontalPadding = HierarchicalSize.Spacing.Medium,
            verticalPadding = HierarchicalSize.Spacing.Small,
            iconSize = IconSize.Small,
            textStyle = typography.labelSmall,
            indicatorHeight = 2.dp,
            cornerRadius = RadiusSize.Small,
            spacing = HierarchicalSize.Spacing.Compact
        )
        TabBarSize.Medium -> TabBarSizeConfig(
            height = 48.dp,
            horizontalPadding = HierarchicalSize.Spacing.Large,
            verticalPadding = HierarchicalSize.Spacing.Small,
            iconSize = IconSize.Medium,
            textStyle = typography.labelMedium,
            indicatorHeight = 3.dp,
            cornerRadius = RadiusSize.Medium,
            spacing = HierarchicalSize.Spacing.Small
        )
        TabBarSize.Large -> TabBarSizeConfig(
            height = 56.dp,
            horizontalPadding = HierarchicalSize.Spacing.Huge,
            verticalPadding = HierarchicalSize.Spacing.Medium,
            iconSize = IconSize.Large,
            textStyle = typography.labelLarge,
            indicatorHeight = 4.dp,
            cornerRadius = RadiusSize.Large,
            spacing = HierarchicalSize.Spacing.Medium
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
            disabledContent = colors.baseContentDisabled
        )
        TabBarVariant.Filled -> TabBarColors(
            background = colors.baseSurfaceSubtle,
            selectedBackground = colors.brandSurfaceDefault,
            selectedContent = colors.brandContentDefault,
            unselectedContent = colors.baseContentBody,
            indicator = colors.brandContentDefault,
            disabledContent = colors.baseContentDisabled
        )
        TabBarVariant.Pill -> TabBarColors(
            background = colors.baseSurfaceSubtle,
            selectedBackground = colors.brandContentDefault,
            selectedContent = Color.White,
            unselectedContent = colors.baseContentBody,
            indicator = colors.brandContentDefault,
            disabledContent = colors.baseContentDisabled
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaTabBar - Horizontal tab navigation
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic tab bar
 * val tabs = listOf(
 *     TabBarItem(title = "Home"),
 *     TabBarItem(title = "Search"),
 *     TabBarItem(title = "Profile")
 * )
 * var selectedIndex by remember { mutableStateOf(0) }
 *
 * PixaTabBar(
 *     tabs = tabs,
 *     selectedIndex = selectedIndex,
 *     onTabSelected = { selectedIndex = it }
 * )
 *
 * // Tab bar with icons
 * PixaTabBar(
 *     tabs = listOf(
 *         TabBarItem("Home", painterResource(Res.drawable.ic_home)),
 *         TabBarItem("Search", painterResource(Res.drawable.ic_search)),
 *         TabBarItem("Profile", painterResource(Res.drawable.ic_profile))
 *     ),
 *     selectedIndex = currentTab,
 *     onTabSelected = { currentTab = it },
 *     variant = TabBarVariant.Pill
 * )
 *
 * // Filled variant with badge
 * PixaTabBar(
 *     tabs = listOf(
 *         TabBarItem("Messages", badge = 5),
 *         TabBarItem("Notifications", badge = 12)
 *     ),
 *     selectedIndex = tab,
 *     onTabSelected = { tab = it },
 *     variant = TabBarVariant.Filled
 * )
 * ```
 *
 * @param tabs List of tab items
 * @param selectedIndex Currently selected tab index
 * @param onTabSelected Callback when tab is selected
 * @param modifier Modifier
 * @param variant Visual style variant
 * @param size Size preset
 * @param colors Custom colors
 * @param scrollable Whether tabs are scrollable
 */
@Composable
fun PixaTabBar(
    tabs: List<TabBarItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    variant: TabBarVariant = TabBarVariant.Underline,
    size: TabBarSize = TabBarSize.Medium,
    colors: TabBarColors? = null,
    scrollable: Boolean = false
) {
    val sizeConfig = getTabBarSizeConfig(size)
    val themeColors = colors ?: getTabBarTheme(variant)

    val rowModifier = if (scrollable) {
        modifier
            .horizontalScroll(rememberScrollState())
            .background(themeColors.background)
    } else {
        modifier
            .fillMaxWidth()
            .background(themeColors.background)
    }

    Row(
        modifier = rowModifier.height(sizeConfig.height),
        horizontalArrangement = if (scrollable) Arrangement.Start else Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, tab ->
            TabBarItemContent(
                item = tab,
                selected = index == selectedIndex,
                onClick = { if (tab.enabled) onTabSelected(index) },
                variant = variant,
                sizeConfig = sizeConfig,
                colors = themeColors
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun TabBarItemContent(
    item: TabBarItem,
    selected: Boolean,
    onClick: () -> Unit,
    variant: TabBarVariant,
    sizeConfig: TabBarSizeConfig,
    colors: TabBarColors
) {
    val contentColor by animateColorAsState(
        targetValue = when {
            !item.enabled -> colors.disabledContent
            selected -> colors.selectedContent
            else -> colors.unselectedContent
        },
        animationSpec = tween(200)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected && variant != TabBarVariant.Underline) colors.selectedBackground else Color.Transparent,
        animationSpec = tween(200)
    )

    val shape = when (variant) {
        TabBarVariant.Pill -> RoundedCornerShape(sizeConfig.cornerRadius)
        else -> RoundedCornerShape(sizeConfig.cornerRadius / 2)
    }

    Column(
        modifier = Modifier
            .clip(shape)
            .background(backgroundColor)
            .clickable(
                enabled = item.enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                role = Role.Tab,
                onClick = onClick
            )
            .padding(horizontal = sizeConfig.horizontalPadding, vertical = sizeConfig.verticalPadding)
            .semantics { this.selected = selected },
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
            Text(
                text = item.title,
                style = sizeConfig.textStyle,
                color = contentColor
            )
        }

        if (variant == TabBarVariant.Underline && selected) {
            Spacer(modifier = Modifier.height(sizeConfig.spacing))
            Box(
                modifier = Modifier
                    .width(sizeConfig.iconSize * 2)
                    .height(sizeConfig.indicatorHeight)
                    .clip(RoundedCornerShape(sizeConfig.indicatorHeight / 2))
                    .background(colors.indicator)
            )
        }
    }
}
