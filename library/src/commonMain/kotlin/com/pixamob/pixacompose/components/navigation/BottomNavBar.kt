package com.pixamob.pixacompose.components.navigation

import com.pixamob.pixacompose.theme.SizeVariant

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.pixamob.pixacompose.components.actions.ButtonShape

import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.display.PixaCard
import com.pixamob.pixacompose.components.display.BaseCardVariant
import com.pixamob.pixacompose.components.display.BaseCardElevation
import com.pixamob.pixacompose.components.display.BaseCardPadding
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*

// ═══════════════════════════════════════════════════════════════════════════════
// CONFIGURATION - Data models and enums
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Navigation item configuration for bottom navigation bar
 *
 * @param title Display text for the navigation item
 * @param iconSelected Painter for selected/active state icon
 * @param iconUnselected Painter for unselected/inactive state icon
 * @param contentDescription Accessibility description (null uses title)
 * @param badgeCount Optional badge count for notifications/updates
 * @param enabled Whether the item is enabled for interaction
 */
data class NavItem(
    val title: String,
    val iconSelected: Painter,
    val iconUnselected: Painter,
    val contentDescription: String? = null,
    val badgeCount: Int? = null,
    val enabled: Boolean = true
)

/**
 * Display style for navigation tab items
 */
enum class TabDisplayStyle {
    /** Show icon only (no text) */
    IconOnly,
    /** Show text only (no icon) */
    TextOnly,
    /** Show both icon and text (default) */
    IconWithText
}

/**
 * Icon style variant for navigation items
 */
enum class NavIconStyle {
    /** Bold icons for selected, line icons for unselected (default behavior) */
    BoldLine,
    /** Custom painter-based selection (use iconSelected/iconUnselected) */
    Custom
}

/**
 * Size variant for bottom navigation bar
 */
enum class BottomNavBarSize {
    /** Small size: 48dp height, suitable for compact layouts */
    Small,
    /** Medium size: 56dp height, standard navigation bar */
    Medium,
    /** Large size: 64dp height, prominent navigation */
    Large
}

/**
 * Orientation for navigation items
 */
enum class NavOrientation {
    /** Horizontal layout (standard bottom navigation) */
    Horizontal,
    /** Vertical layout (rare, for side navigation adapted as bottom bar) */
    Vertical
}

// ═══════════════════════════════════════════════════════════════════════════════
// THEME - Size mappings and styling configurations
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Internal size configuration for bottom navigation bar
 */
private data class NavBarSizeConfig(
    val height: Dp,
    val iconSize: Dp,
    val buttonSize: SizeVariant,
    val horizontalPadding: Dp,
    val verticalPadding: Dp
)

/**
 * Maps size variant to concrete dimensions
 */
private fun BottomNavBarSize.toSizeConfig(): NavBarSizeConfig = when (this) {
    BottomNavBarSize.Small -> NavBarSizeConfig(
        height = 48.dp,
        iconSize = 20.dp,
        buttonSize = SizeVariant.Medium,
        horizontalPadding = HierarchicalSize.Spacing.Small,
        verticalPadding = HierarchicalSize.Spacing.Compact
    )
    BottomNavBarSize.Medium -> NavBarSizeConfig(
        height = 56.dp,
        iconSize = 24.dp,
        buttonSize = SizeVariant.Large,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Small
    )
    BottomNavBarSize.Large -> NavBarSizeConfig(
        height = 64.dp,
        iconSize = 28.dp,
        buttonSize = SizeVariant.Large,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Medium
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// BASE - Internal composables
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Internal navigation item component with animations
 */
@Composable
private fun AnimatedNavItem(
    item: NavItem,
    isSelected: Boolean,
    iconSize: Dp,
    tabDisplayStyle: TabDisplayStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconPainter = if (isSelected) item.iconSelected else item.iconUnselected

    // Animate color transition
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            AppTheme.colors.brandContentDefault
        } else {
            AppTheme.colors.baseContentBody.copy(alpha = 0.5f)
        },
        animationSpec = tween(durationMillis = 200),
        label = "navItemColor"
    )

    // Animate scale for subtle selection feedback
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.95f,
        animationSpec = tween(durationMillis = 200),
        label = "navItemScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                enabled = item.enabled,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics {
                role = Role.Tab
                this.contentDescription = item.contentDescription ?: item.title
            },
        contentAlignment = Alignment.Center
    ) {
        when (tabDisplayStyle) {
            TabDisplayStyle.IconOnly -> {
                PixaIcon(
                    painter = iconPainter,
                    contentDescription = item.contentDescription ?: item.title,
                    modifier = Modifier.size(iconSize),
                    tint = contentColor
                )
            }
            TabDisplayStyle.TextOnly -> {
                Text(
                    text = item.title,
                    style = AppTheme.typography.captionBold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TabDisplayStyle.IconWithText -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = Spacing.Tiny)
                ) {
                    PixaIcon(
                        painter = iconPainter,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = contentColor
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.title,
                            style = AppTheme.typography.captionBold,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Internal center action button (FAB)
 */
@Composable
private fun CenterActionButton(
    centerIcon: Painter?,
    centerContentDescription: String?,
    buttonSize: SizeVariant,
    enabled: Boolean,
    onCenterAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (centerIcon != null) {
        PixaButton(
            variant = ButtonVariant.Solid,
            onClick = onCenterAction,
            modifier = modifier
                .zIndex(1f)
                .semantics {
                    role = Role.Button
                    centerContentDescription?.let {
                        contentDescription = it
                    }
                },
            shape = ButtonShape.Circle,
            enabled = enabled,
            size = buttonSize,
            leadingIcon = centerIcon,
            description = centerContentDescription
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PUBLIC - Main component
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BottomNavBar - Fully Dynamic Bottom Navigation Bar Component
 *
 * A flexible bottom navigation bar with support for 2-5 items, optional center actions (FAB),
 * animations, and full theme integration. Designed for multiplatform Compose with touch-friendly
 * dimensions and accessibility support.
 *
 * ## Key Features
 * - Fully dynamic: handles 2-5 items automatically with intelligent layout
 * - Optional center action button (FAB) with smart positioning
 * - Size variants (Small/Medium/Large) with consistent spacing
 * - Animated icon/color transitions on selection
 * - Theme-aware styling with AppTheme integration
 * - Accessibility semantics for screen readers
 * - Scrollable support for >5 items with auto-scroll to selected
 * - Tab display styles (IconOnly/TextOnly/IconWithText)
 * - Safe area padding support for iOS/Android notches
 *
 * ## Dynamic Behavior
 * - **2-4 items + center action**: Places FAB in middle, tabs distributed evenly on both sides
 * - **5 items or no center action**: Items fill entire width evenly, no FAB shown
 * - **>5 items**: Enables horizontal scrolling with auto-scroll to selected
 * - Validates item count (min 2, max recommended 5 for optimal UX)
 *
 * ## Usage Examples
 *
 * ### Three tabs with center FAB:
 * ```kotlin
 * BottomNavBar(
 *     items = listOf(
 *         NavItem("Home", homeIconBold, homeIconLine),
 *         NavItem("Search", searchIconBold, searchIconLine),
 *         NavItem("Profile", profileIconBold, profileIconLine)
 *     ),
 *     selectedIndex = 0,
 *     onItemSelected = { index -> viewModel.navigateTo(index) },
 *     withCenterAction = true,
 *     centerIcon = addIcon,
 *     onCenterAction = { viewModel.showAddDialog() }
 * )
 * ```
 *
 * ### Five tabs without center action:
 * ```kotlin
 * BottomNavBar(
 *     items = listOf(
 *         NavItem("Home", homeIconBold, homeIconLine),
 *         NavItem("Search", searchIconBold, searchIconLine),
 *         NavItem("Favorites", favIconBold, favIconLine),
 *         NavItem("Profile", profileIconBold, profileIconLine),
 *         NavItem("Settings", settingsIconBold, settingsIconLine)
 *     ),
 *     selectedIndex = currentTab,
 *     onItemSelected = { index -> handleNavigation(index) },
 *     withCenterAction = false,
 *     size = BottomNavBarSize.Large
 * )
 * ```
 *
 * ### Two tabs with center FAB (minimal layout):
 * ```kotlin
 * BottomNavBar(
 *     items = listOf(
 *         NavItem("Feed", feedIconBold, feedIconLine),
 *         NavItem("Profile", profileIconBold, profileIconLine)
 *     ),
 *     selectedIndex = selectedTab,
 *     onItemSelected = onTabChange,
 *     withCenterAction = true,
 *     centerIcon = cameraIcon,
 *     onCenterAction = { openCamera() },
 *     size = BottomNavBarSize.Medium
 * )
 * ```
 *
 * ### With badges and custom accessibility:
 * ```kotlin
 * BottomNavBar(
 *     items = listOf(
 *         NavItem(
 *             title = "Messages",
 *             iconSelected = msgIconBold,
 *             iconUnselected = msgIconLine,
 *             contentDescription = "Messages, 3 unread",
 *             badgeCount = 3
 *         ),
 *         NavItem("Calls", callIconBold, callIconLine),
 *         NavItem("Contacts", contactIconBold, contactIconLine)
 *     ),
 *     selectedIndex = selectedTab,
 *     onItemSelected = onTabChange,
 *     withCenterAction = true,
 *     centerIcon = addIcon,
 *     onCenterAction = { showAddMenu() }
 * )
 * ```
 *
 * @param items List of navigation items (2-5 recommended, >5 enables scrolling)
 * @param selectedIndex Currently selected item index (0-based)
 * @param onItemSelected Callback when an item is selected, receives item index
 * @param modifier Modifier for the entire navigation bar container
 * @param withCenterAction If true, shows a center FAB and arranges tabs around it
 * @param centerIcon Icon painter for center action button (required if withCenterAction=true)
 * @param centerContentDescription Accessibility description for center button
 * @param onCenterAction Callback for center action button press
 * @param centerEnabled Whether center action button is enabled
 * @param size Size variant affecting height, icon size, and spacing
 * @param iconStyle Icon style variant (BoldLine uses iconSelected/Unselected, Custom allows custom painters)
 * @param orientation Layout orientation (Horizontal is standard for bottom nav)
 * @param showBackground Whether to show card background (true for card wrapper)
 * @param cardVariant Card variant for background (Elevated, Outlined, Filled, Ghost)
 * @param tabDisplayStyle Display style for tabs (IconOnly/TextOnly/IconWithText)
 * @param enableScrolling If true and items > 5, enables horizontal scrolling
 * @param enableAutoScroll If true, auto-scrolls to selected item when scrolling is enabled
 */
@Composable
fun PixaBottomNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    withCenterAction: Boolean = false,
    centerIcon: Painter? = null,
    centerContentDescription: String? = null,
    centerEnabled: Boolean = true,
    size: BottomNavBarSize = BottomNavBarSize.Medium,
    iconStyle: NavIconStyle = NavIconStyle.BoldLine,
    orientation: NavOrientation = NavOrientation.Horizontal,
    showBackground: Boolean = true,
    cardVariant: BaseCardVariant = BaseCardVariant.Elevated,
    tabDisplayStyle: TabDisplayStyle = TabDisplayStyle.IconWithText,
    enableScrolling: Boolean = true,
    enableAutoScroll: Boolean = true,
    onItemSelected: (Int) -> Unit,
    onCenterAction: () -> Unit = {},
) {
    // Validation with helpful error messages
    require(items.size >= 2) {
        "BottomNavBar requires at least 2 items for proper navigation. Provided: ${items.size}"
    }

    require(selectedIndex in items.indices) {
        "selectedIndex $selectedIndex is out of bounds for ${items.size} items (valid range: 0-${items.lastIndex})"
    }

    if (withCenterAction) {
        require(centerIcon != null) {
            "centerIcon is required when withCenterAction=true. Provide a valid Painter or set withCenterAction=false"
        }
    }

    val sizeConfig = size.toSizeConfig()
    val shouldScroll = enableScrolling && items.size > 5
    val scrollState = rememberScrollState()

    // Auto-scroll to selected item with smooth animation
    LaunchedEffect(selectedIndex) {
        if (shouldScroll && enableAutoScroll && items.isNotEmpty()) {
            // Calculate approximate scroll position based on item width
            val itemWidth = 80.dp.value
            val targetScroll = (selectedIndex * itemWidth).coerceAtLeast(0f).toInt()
            scrollState.animateScrollTo(targetScroll)
        }
    }

    // Dynamic layout: determine if center action should be shown
    // Center action is shown only when withCenterAction=true AND items.size <= 4
    val showCenterActionButton = withCenterAction && centerIcon != null && items.size <= 4

    // Calculate items list with center spacer if needed
    val displayItems = if (showCenterActionButton) {
        // Insert null spacer at middle position for FAB
        val middleIndex = (items.size + 1) / 2
        items.take(middleIndex) + listOf(null) + items.drop(middleIndex)
    } else {
        // No spacer, display all items evenly
        items.map { it as NavItem? }
    }

    // Container with optional background
    val content: @Composable () -> Unit = {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(sizeConfig.height)
        ) {
            val (navBar, centerButton) = createRefs()

            // Navigation items row
            val navBarModifier = Modifier
                .fillMaxSize()
                .constrainAs(navBar) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }

            if (shouldScroll) {
                Row(
                    modifier = navBarModifier.horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavBarItems(
                        items = displayItems,
                        selectedIndex = selectedIndex,
                        onItemSelected = onItemSelected,
                        sizeConfig = sizeConfig,
                        tabDisplayStyle = tabDisplayStyle,
                        shouldScroll = shouldScroll
                    )
                }
            } else {
                Row(
                    modifier = navBarModifier,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavBarItems(
                        items = displayItems,
                        selectedIndex = selectedIndex,
                        onItemSelected = onItemSelected,
                        sizeConfig = sizeConfig,
                        tabDisplayStyle = tabDisplayStyle,
                        shouldScroll = shouldScroll
                    )
                }
            }

            // Center action button (FAB) - only shown if conditions are met
            if (showCenterActionButton) {
                CenterActionButton(
                    centerIcon = centerIcon,
                    centerContentDescription = centerContentDescription,
                    buttonSize = sizeConfig.buttonSize,
                    enabled = centerEnabled,
                    onCenterAction = onCenterAction,
                    modifier = Modifier.constrainAs(centerButton) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
            }
        }
    }

    if (showBackground) {
        PixaCard(
            modifier = modifier
                .padding(horizontal = sizeConfig.horizontalPadding)
                .padding(bottom = sizeConfig.verticalPadding)
                .fillMaxWidth()
                .height(sizeConfig.height),
            variant = cardVariant,
            elevation = when (cardVariant) {
                BaseCardVariant.Elevated -> BaseCardElevation.Medium
                else -> BaseCardElevation.None
            },
            padding = BaseCardPadding.None
        ) {
            content()
        }
    } else {
        Box(
            modifier = modifier
                .padding(horizontal = sizeConfig.horizontalPadding)
                .padding(bottom = sizeConfig.verticalPadding)
                .fillMaxWidth()
                .height(sizeConfig.height)
        ) {
            content()
        }
    }
}

/**
 * Internal composable for rendering navigation items
 */
@Composable
private fun RowScope.NavBarItems(
    items: List<NavItem?>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    sizeConfig: NavBarSizeConfig,
    tabDisplayStyle: TabDisplayStyle,
    shouldScroll: Boolean
) {
    var actualIndex = 0
    items.forEach { item ->
        if (item == null) {
            // Spacer for center FAB
            if (!shouldScroll) {
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.width(60.dp))
            }
        } else {
            val isSelected = selectedIndex == actualIndex
            val currentIndex = actualIndex

            AnimatedNavItem(
                item = item,
                isSelected = isSelected,
                iconSize = sizeConfig.iconSize,
                tabDisplayStyle = tabDisplayStyle,
                onClick = { onItemSelected(currentIndex) },
                modifier = if (shouldScroll) {
                    Modifier
                        .width(80.dp)
                        .fillMaxHeight()
                } else {
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                }
            )

            actualIndex++
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// CONVENIENCE - Helper extensions and utilities
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Helper extension to validate if item count is optimal for bottom navigation
 * Logs a warning if >5 items (scrolling required) or <2 items (insufficient for navigation)
 */
fun List<NavItem>.validateForBottomNav(): Boolean {
    return when {
        size < 2 -> {
            println("Warning: BottomNavBar works best with 2-5 items. Found $size items.")
            false
        }
        size > 5 -> {
            println("Info: BottomNavBar has >5 items ($size). Horizontal scrolling will be enabled.")
            true
        }
        else -> true
    }
}

/**
 * Creates a minimal NavItem with just title and icons
 */
fun createNavItem(
    title: String,
    iconSelected: Painter,
    iconUnselected: Painter
): NavItem = NavItem(
    title = title,
    iconSelected = iconSelected,
    iconUnselected = iconUnselected,
    contentDescription = null,
    badgeCount = null,
    enabled = true
)
