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
import androidx.compose.foundation.text.BasicText
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
import com.pixamob.pixacompose.components.surfaces.PixaCard
import com.pixamob.pixacompose.components.surfaces.BaseCardVariant
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

data class NavItem(
    val title: String,
    val iconSelected: Painter,
    val iconUnselected: Painter,
    val contentDescription: String? = null,
    val badgeCount: Int? = null,
    val enabled: Boolean = true
)

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class TabDisplayStyle {
    IconOnly,
    TextOnly,
    IconWithText
}

enum class NavIconStyle {
    BoldLine,
    Custom
}

/** Orientation for navigation items */
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
private fun SizeVariant.toNavBarSizeConfig(): NavBarSizeConfig = when (this) {
    SizeVariant.Small -> NavBarSizeConfig(
        height = 48.dp,
        iconSize = 20.dp,
        buttonSize = SizeVariant.Medium,
        horizontalPadding = HierarchicalSize.Spacing.Small,
        verticalPadding = HierarchicalSize.Spacing.Compact
    )
    SizeVariant.Medium -> NavBarSizeConfig(
        height = 56.dp,
        iconSize = 24.dp,
        buttonSize = SizeVariant.Large,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Small
    )
    SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> NavBarSizeConfig(
        height = 64.dp,
        iconSize = 28.dp,
        buttonSize = SizeVariant.Large,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Medium
    )
    else -> NavBarSizeConfig(
        height = 56.dp,
        iconSize = 24.dp,
        buttonSize = SizeVariant.Large,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Small
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

    // Animate color transition with spring
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            AppTheme.colors.brandContentDefault
        } else {
            AppTheme.colors.baseContentBody.copy(alpha = 0.5f)
        },
        animationSpec = AnimationUtils.colorSpring,
        label = "navItemColor"
    )

    // Animate scale for subtle selection feedback with spring
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.95f,
        animationSpec = AnimationUtils.selectionSpring,
        label = "navItemScale"
    )

    // Animate icon scale for selected state emphasis
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = AnimationUtils.selectionSpring,
        label = "navIconScale"
    )

    // Animate label alpha for IconWithText display style
    val labelAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = AnimationUtils.selectionSpring,
        label = "navLabelAlpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = pixaRipple(bounded = true),
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
                    modifier = Modifier.size(iconSize).scale(iconScale),
                    tint = contentColor
                )
            }
            TabDisplayStyle.TextOnly -> {
                BasicText(
                    text = item.title,
                    style = AppTheme.typography.labelMedium.copy(
                        color = contentColor.copy(alpha = labelAlpha)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TabDisplayStyle.IconWithText -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                PixaIcon(
                    painter = iconPainter,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize).scale(iconScale),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                BasicText(
                    text = item.title,
                    style = AppTheme.typography.labelMedium.copy(
                        color = contentColor.copy(alpha = labelAlpha)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
            variant = ButtonVariant.Filled,
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
 * Bottom navigation bar with 2-5 items and optional center FAB.
 *
 * ### Anatomy
 * Nav items (icon + optional label) + optional center FAB + card background.
 *
 * ### Variants
 * [TabDisplayStyle]: IconOnly, TextOnly, IconWithText.
 * [NavIconStyle]: BoldLine, Custom.
 * [NavOrientation]: Horizontal (standard), Vertical.
 *
 * ### States
 * Selected (animated color/scale), Disabled (per-item).
 *
 * ### Sizing
 * [SizeVariant] Small (48dp), Medium (56dp), Large (64dp) — drives height, icon size, padding.
 *
 * ### Dynamic behavior
 * - 2-4 items + center FAB: FAB in middle, tabs on both sides
 * - 5 items or no FAB: evenly distributed width
 * - >5 items: horizontal scrolling + auto-scroll to selected
 *
 * @param items Navigation items (2+)
 * @param selectedIndex 0-based selected index
 * @param onItemSelected Callback receiving item index
 * @param withCenterAction Shows center FAB when true
 * @param centerIcon Painter for center action
 * @param centerContentDescription Accessibility label for center button
 * @param onCenterAction Center button press callback
 * @param centerEnabled Center button enabled state
 * @param size Height/icon/padding preset
 * @param iconStyle BoldLine vs Custom icon painters
 * @param orientation Horizontal or vertical layout
 * @param showBackground Wraps in [PixaCard] when true
 * @param cardVariant Card background variant
 * @param tabDisplayStyle Icon/Text/Icons+Text per tab
 * @param enableScrolling Horizontal scroll for >5 items
 * @param enableAutoScroll Auto-scroll to selected item
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
    size: SizeVariant = SizeVariant.Medium,
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

    val sizeConfig = size.toNavBarSizeConfig()
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
                BaseCardVariant.Elevated -> ComponentElevation.Medium
                else -> ComponentElevation.None
            },
            padding = SizeVariant.None
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
