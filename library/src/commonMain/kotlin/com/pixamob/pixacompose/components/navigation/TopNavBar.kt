package com.pixamob.pixacompose.components.navigation
import com.pixamob.pixacompose.theme.SizeVariant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.ButtonShape
import com.pixamob.pixacompose.components.actions.ButtonColors
import com.pixamob.pixacompose.components.actions.ButtonStateColors
import com.pixamob.pixacompose.components.display.PixaAvatar
import com.pixamob.pixacompose.components.feedback.PixaBadge
import com.pixamob.pixacompose.components.feedback.BadgeSize
import com.pixamob.pixacompose.components.feedback.BadgeVariant
import com.pixamob.pixacompose.theme.*

// ═══════════════════════════════════════════════════════════════════════════════
// CONFIGURATION - Data models and enums
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Action configuration for top navigation bar
 *
 * @param icon Painter for the action icon
 * @param description Accessibility description for the action
 * @param onClick Callback when action is clicked
 * @param enabled Whether the action is enabled for interaction
 * @param badge Optional badge count for notifications/updates
 * @param tint Optional custom tint color (null uses default contentColor)
 */
@Stable
data class TopNavAction(
    val icon: Painter,
    val description: String? = null,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val badge: Int? = null,
    val tint: Color? = null
)

/**
 * Size variant for top navigation bar
 */
enum class TopNavSize {
    /** Small size: 48dp height, compact layout for minimal screens */
    Small,
    /** Medium size: 56dp height, standard app bar (default) */
    Medium,
    /** Large size: 72dp height, prominent header for landing pages */
    Large
}

/**
 * Title alignment options
 */
enum class TopNavTitleAlignment {
    /** Title aligned to start (left) */
    Start,
    /** Title centered */
    Center
}

// ═══════════════════════════════════════════════════════════════════════════════
// THEME - Size mappings and styling configurations
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Internal size configuration for top navigation bar
 */
@Stable
private data class TopNavSizeConfig(
    val height: Dp,
    val iconSize: Dp,
    val avatarSize: SizeVariant,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val actionSpacing: Dp,
    val titleFontScale: Float
)

/**
 * Maps size variant to concrete dimensions
 */
private fun TopNavSize.toSizeConfig(): TopNavSizeConfig = when (this) {
    TopNavSize.Small -> TopNavSizeConfig(
        height = HierarchicalSize.Container.Medium  ,
        iconSize =  HierarchicalSize.Icon.Small,
        avatarSize = SizeVariant.Small,
        horizontalPadding = HierarchicalSize.Spacing.Small,
        verticalPadding = HierarchicalSize.Spacing.Compact,
        actionSpacing = HierarchicalSize.Spacing.Compact,
        titleFontScale = 0.9f
    )
    TopNavSize.Medium -> TopNavSizeConfig(
        height = HierarchicalSize.Container.Large  ,
        iconSize =  HierarchicalSize.Icon.Medium,
        avatarSize = SizeVariant.Medium,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Small,
        actionSpacing = HierarchicalSize.Spacing.Small,
        titleFontScale = 1.0f
    )
    TopNavSize.Large -> TopNavSizeConfig(
        height = HierarchicalSize.Container.Huge  ,
        iconSize =  HierarchicalSize.Icon.Large,
        avatarSize = SizeVariant.Large,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Medium,
        actionSpacing = HierarchicalSize.Spacing.Medium,
        titleFontScale = 1.15f
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// BASE - Internal composables
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Internal action button using PixaButton for consistent styling
 */
@Composable
private fun ActionButton(
    action: TopNavAction,
    iconSize: Dp,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Use action.tint or contentColor for the icon
        val iconColor = action.tint ?: contentColor

        PixaButton(
            onClick = action.onClick,
            leadingIcon = action.icon,
            size = when (iconSize) {
                HierarchicalSize.Icon.Small -> SizeVariant.Small
                HierarchicalSize.Icon.Medium -> SizeVariant.Medium
                HierarchicalSize.Icon.Large -> SizeVariant.Large
                else -> SizeVariant.Medium
            },
            variant = ButtonVariant.Ghost,
            shape = ButtonShape.Circle,
            enabled = action.enabled,
            description = action.description,
            modifier = Modifier.size(iconSize + 20.dp), // Touch target 44dp minimum
            customColors = ButtonStateColors(
                default = ButtonColors(
                    background = Color.Transparent,
                    content = iconColor,
                    border = Color.Transparent
                ),
                disabled = ButtonColors(
                    background = Color.Transparent,
                    content = iconColor.copy(alpha = 0.5f),
                    border = Color.Transparent
                )
            )
        )

        // Badge overlay
        if (action.badge != null && action.badge > 0) {
            PixaBadge(
                content = action.badge.toString(),
                variant = BadgeVariant.Error,
                size = BadgeSize.Small,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

/**
 * Internal title composable with overflow handling and optional subtitle
 */
@Composable
private fun TopNavTitleSection(
    title: String?,
    subtitle: String?,
    alignment: TopNavTitleAlignment,
    fontScale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = when (alignment) {
            TopNavTitleAlignment.Start -> Alignment.Start
            TopNavTitleAlignment.Center -> Alignment.CenterHorizontally
        }
    ) {
        if (title != null) {
            Text(
                text = title,
                style = AppTheme.typography.titleBold.copy(
                    fontSize = AppTheme.typography.titleBold.fontSize * fontScale
                ),
                color = AppTheme.colors.baseContentTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = when (alignment) {
                    TopNavTitleAlignment.Start -> TextAlign.Start
                    TopNavTitleAlignment.Center -> TextAlign.Center
                }
            )
        }

        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = AppTheme.typography.captionRegular,
                color = AppTheme.colors.baseContentSubtitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = when (alignment) {
                    TopNavTitleAlignment.Start -> TextAlign.Start
                    TopNavTitleAlignment.Center -> TextAlign.Center
                }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PUBLIC - Main component
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BaseTopNavBar - Enhanced Dynamic Top Navigation Bar Component
 *
 * A fully-featured top app bar with support for dynamic actions, optional title/subtitle,
 * avatar integration, badges, elevation, and complete theme awareness. Designed for
 * multiplatform Compose with comprehensive accessibility support.
 *
 * ## Key Features
 * - Dynamic start and end actions with custom icons and badges
 * - Optional title with string or custom composable support
 * - Optional subtitle below title
 * - Optional profile avatar as end action
 * - Badge support on actions for notifications
 * - Status bar padding support for safe area
 * - Size variants (Small/Medium/Large) affecting height, icons, and fonts
 * - Animated action buttons with ripple feedback
 * - Elevation/shadow support
 * - Optional bottom divider
 * - Horizontal scrolling for overflow actions
 * - Theme-aware styling with AppTheme integration
 * - Accessibility semantics for screen readers
 *
 * ## Size Variants Impact
 * - **Small (48dp)**: Compact, 20dp icons, 0.9x title scale - for space-constrained layouts
 * - **Medium (56dp)**: Standard, 24dp icons, 1.0x title scale - default app bar
 * - **Large (72dp)**: Prominent, 28dp icons, 1.15x title scale - for landing/feature pages
 *
 * ## Usage Examples
 *
 * ### Basic back button with title:
 * ```kotlin
 * PixaTopNavBar(
 *     title = "Settings",
 *     startActions = listOf(
 *         TopNavAction(
 *             icon = backIcon,
 *             contentDescription = "Navigate back",
 *             onClick = { navController.popBackStack() }
 *         )
 *     )
 * )
 * ```
 *
 * ### Title with subtitle and profile avatar:
 * ```kotlin
 * PixaTopNavBar(
 *     title = "Dashboard",
 *     subtitle = "Welcome back, John",
 *     profileImageUrl = currentUser.avatar,
 *     onAvatarClick = { navigateToProfile() },
 *     elevation = 2.dp
 * )
 * ```
 *
 * ### Actions with badges:
 * ```kotlin
 * PixaTopNavBar(
 *     title = "Messages",
 *     startActions = listOf(
 *         TopNavAction(menuIcon, "Menu", onClick = { openDrawer() })
 *     ),
 *     endActions = listOf(
 *         TopNavAction(
 *             icon = notificationIcon,
 *             contentDescription = "Notifications",
 *             onClick = { openNotifications() },
 *             badge = 5
 *         ),
 *         TopNavAction(searchIcon, "Search", onClick = { search() })
 *     )
 * )
 * ```
 *
 * ### Large size with custom title composable:
 * ```kotlin
 * PixaTopNavBar(
 *     titleComposable = {
 *         Row(verticalAlignment = Alignment.CenterVertically) {
 *             Image(brandLogo, "Logo", modifier = Modifier.size(32.dp))
 *             Spacer(modifier = Modifier.width(8.dp))
 *             Text("MyApp", style = AppTheme.typography.headerBold)
 *         }
 *     },
 *     size = TopNavSize.Large,
 *     elevation = 4.dp,
 *     bottomDivider = true
 * )
 * ```
 *
 * ### Many actions with scrolling:
 * ```kotlin
 * PixaTopNavBar(
 *     title = "Tools",
 *     endActions = listOf(
 *         TopNavAction(icon1, "Action 1", onClick = {}),
 *         TopNavAction(icon2, "Action 2", onClick = {}),
 *         TopNavAction(icon3, "Action 3", onClick = {}),
 *         TopNavAction(icon4, "Action 4", onClick = {}),
 *         TopNavAction(icon5, "Action 5", onClick = {})
 *     ),
 *     enableScrolling = true
 * )
 * ```
 *
 * @param modifier Modifier for the top nav bar container
 * @param title Optional title text (null for no title)
 * @param subtitle Optional subtitle text below title
 * @param titleComposable Optional custom title composable (overrides title/subtitle if provided)
 * @param titleAlignment Title alignment (Start by default, Center when no start actions)
 * @param startActions List of actions displayed at the start (left/leading)
 * @param endActions List of actions displayed at the end (right/trailing)
 * @param profileImageUrl Optional profile image URL (creates avatar end action)
 * @param onAvatarClick Callback when avatar is clicked (required if profileImageUrl provided)
 * @param containerColor Background color of the top bar
 * @param contentColor Color for icons and text
 * @param size Size variant affecting height, icon sizes, and font scale
 * @param elevation Elevation for shadow effect (0.dp for no shadow)
 * @param bottomDivider If true, shows a thin divider line at the bottom
 * @param includeSafeAreaPadding If true, adds status bar padding at top
 * @param enableScrolling If true and actions overflow, enables horizontal scrolling
 */
@Composable
fun PixaTopNavBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    titleComposable: @Composable (() -> Unit)? = null,
    titleAlignment: TopNavTitleAlignment? = null,
    startActions: List<TopNavAction> = emptyList(),
    endActions: List<TopNavAction> = emptyList(),
    profileImageUrl: String? = null,
    containerColor: Color = AppTheme.colors.baseSurfaceDefault,
    contentColor: Color = AppTheme.colors.baseContentTitle,
    size: TopNavSize = TopNavSize.Medium,
    elevation: Dp = 0.dp,
    bottomDivider: Boolean = false,
    includeSafeAreaPadding: Boolean = false,
    enableScrolling: Boolean = false,
    onAvatarClick: (() -> Unit)? = null,
    ) {
    // Validation
    if (profileImageUrl != null) {
        require(onAvatarClick != null) {
            "onAvatarClick is required when profileImageUrl is provided"
        }
    }

    val sizeConfig = size.toSizeConfig()

    // Calculate status bar padding
    val statusBarPadding = if (includeSafeAreaPadding) {
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    } else {
        0.dp
    }

    // Determine title alignment
    val resolvedAlignment = titleAlignment ?: if (startActions.isEmpty()) {
        TopNavTitleAlignment.Center
    } else {
        TopNavTitleAlignment.Start
    }

    // Scroll state for actions
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation)
            .background(containerColor)
    ) {
        // Status bar spacer
        if (statusBarPadding > 0.dp) {
            Spacer(modifier = Modifier.height(statusBarPadding))
        }

        // Main content row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(sizeConfig.height)
                .padding(horizontal = sizeConfig.horizontalPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start actions section
            if (startActions.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(sizeConfig.actionSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = if (enableScrolling && startActions.size > 2) {
                        Modifier.horizontalScroll(scrollState)
                    } else {
                        Modifier
                    }
                ) {
                    startActions.forEach { action ->
                        ActionButton(
                            action = action,
                            iconSize = sizeConfig.iconSize,
                            contentColor = contentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
            }

            // Title section (flexible weight)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = HierarchicalSize.Spacing.Compact),
                contentAlignment = when (resolvedAlignment) {
                    TopNavTitleAlignment.Start -> Alignment.CenterStart
                    TopNavTitleAlignment.Center -> Alignment.Center
                }
            ) {
                when {
                    titleComposable != null -> {
                        // Custom title composable
                        titleComposable()
                    }
                    title != null || subtitle != null -> {
                        // Default title/subtitle
                        TopNavTitleSection(
                            title = title,
                            subtitle = subtitle,
                            alignment = resolvedAlignment,
                            fontScale = sizeConfig.titleFontScale
                        )
                    }
                }
            }

            // End actions section
            if (endActions.isNotEmpty() || profileImageUrl != null) {
                Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(sizeConfig.actionSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = if (enableScrolling && endActions.size > 3) {
                        Modifier.horizontalScroll(scrollState)
                    } else {
                        Modifier
                    }
                ) {
                    endActions.forEach { action ->
                        ActionButton(
                            action = action,
                            iconSize = sizeConfig.iconSize,
                            contentColor = contentColor
                        )
                    }

                    // Profile avatar (if provided)
                    if (profileImageUrl != null && onAvatarClick != null) {
                        Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Compact))
                        PixaAvatar(
                            imageUrl = profileImageUrl,
                            size = sizeConfig.avatarSize,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = false, radius = 24.dp),
                                role = Role.Button,
                                onClick = onAvatarClick
                            )
                        )
                    }
                }
            }
        }

        // Bottom divider
        if (bottomDivider) {
            androidx.compose.material3.HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = AppTheme.colors.baseBorderSubtle
            )
        }
    }
}