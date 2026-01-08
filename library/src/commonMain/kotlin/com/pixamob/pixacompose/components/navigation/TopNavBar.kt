package com.pixamob.pixacompose.components.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.Avatar
import com.pixamob.pixacompose.components.display.AvatarSize
import com.pixamob.pixacompose.components.display.Icon
import com.pixamob.pixacompose.components.feedback.Badge
import com.pixamob.pixacompose.components.feedback.BadgeSize
import com.pixamob.pixacompose.components.feedback.BadgeVariant
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils

// ═══════════════════════════════════════════════════════════════════════════════
// CONFIGURATION - Data models and enums
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Action configuration for top navigation bar
 *
 * @param icon Painter for the action icon
 * @param contentDescription Accessibility description for the action
 * @param onClick Callback when action is clicked
 * @param enabled Whether the action is enabled for interaction
 * @param badge Optional badge count for notifications/updates
 * @param tint Optional custom tint color (null uses default contentColor)
 */
@Stable
data class TopNavAction(
    val icon: Painter,
    val contentDescription: String?,
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
    val avatarSize: AvatarSize,
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
        height = 48.dp,
        iconSize = 20.dp,
        avatarSize = AvatarSize.Small,
        horizontalPadding = Spacing.Small,
        verticalPadding = Spacing.ExtraSmall,
        actionSpacing = Spacing.ExtraSmall,
        titleFontScale = 0.9f
    )
    TopNavSize.Medium -> TopNavSizeConfig(
        height = 56.dp,
        iconSize = 24.dp,
        avatarSize = AvatarSize.Medium,
        horizontalPadding = Spacing.Medium,
        verticalPadding = Spacing.Small,
        actionSpacing = Spacing.Small,
        titleFontScale = 1.0f
    )
    TopNavSize.Large -> TopNavSizeConfig(
        height = 72.dp,
        iconSize = 28.dp,
        avatarSize = AvatarSize.Large,
        horizontalPadding = Spacing.Medium,
        verticalPadding = Spacing.Medium,
        actionSpacing = Spacing.Medium,
        titleFontScale = 1.15f
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// BASE - Internal composables
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Internal action button with animation and optional badge
 */
@Composable
private fun AnimatedActionButton(
    action: TopNavAction,
    iconSize: Dp,
    defaultTint: Color,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Animate scale for press feedback
    val scale by animateFloatAsState(
        targetValue = if (action.enabled) 1.0f else 0.85f,
        animationSpec = AnimationUtils.fastTween(),
        label = "actionScale"
    )

    // Use custom tint or default
    val tintColor = action.tint ?: defaultTint

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(iconSize + 20.dp) // Touch target 44dp minimum
                .scale(scale)
                .clip(CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, radius = iconSize + 4.dp),
                    enabled = action.enabled,
                    role = Role.Button,
                    onClick = action.onClick
                )
                .semantics {
                    role = Role.Button
                    action.contentDescription?.let {
                        contentDescription = it
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = action.icon,
                contentDescription = action.contentDescription,
                modifier = Modifier.size(iconSize),
                tint = if (action.enabled) tintColor else tintColor.copy(alpha = 0.5f)
            )
        }

        // Badge overlay
        if (action.badge != null && action.badge > 0) {
            Badge(
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
 * BaseTopNavBar(
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
 * BaseTopNavBar(
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
 * BaseTopNavBar(
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
 * BaseTopNavBar(
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
 * BaseTopNavBar(
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
fun BaseTopNavBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    titleComposable: @Composable (() -> Unit)? = null,
    titleAlignment: TopNavTitleAlignment? = null,
    startActions: List<TopNavAction> = emptyList(),
    endActions: List<TopNavAction> = emptyList(),
    profileImageUrl: String? = null,
    onAvatarClick: (() -> Unit)? = null,
    containerColor: Color = AppTheme.colors.baseSurfaceDefault,
    contentColor: Color = AppTheme.colors.baseContentTitle,
    size: TopNavSize = TopNavSize.Medium,
    elevation: Dp = 0.dp,
    bottomDivider: Boolean = false,
    includeSafeAreaPadding: Boolean = true,
    enableScrolling: Boolean = false
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

    // Animated content color
    val animatedContentColor by animateColorAsState(
        targetValue = contentColor,
        animationSpec = AnimationUtils.standardTween(),
        label = "contentColor"
    )

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
                .padding(horizontal = sizeConfig.horizontalPadding)
                .padding(vertical = sizeConfig.verticalPadding),
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
                        AnimatedActionButton(
                            action = action,
                            iconSize = sizeConfig.iconSize,
                            defaultTint = animatedContentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.Small))
            }

            // Title section (flexible weight)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Spacing.ExtraSmall),
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
                Spacer(modifier = Modifier.width(Spacing.Small))

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
                        AnimatedActionButton(
                            action = action,
                            iconSize = sizeConfig.iconSize,
                            defaultTint = animatedContentColor
                        )
                    }

                    // Profile avatar (if provided)
                    if (profileImageUrl != null && onAvatarClick != null) {
                        Spacer(modifier = Modifier.width(Spacing.ExtraSmall))
                        Avatar(
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

// ═══════════════════════════════════════════════════════════════════════════════
// CONVENIENCE - Preset configurations
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Convenience function for a top nav bar with back button and title
 *
 * @param title Title text to display
 * @param onBack Callback when back button is clicked
 * @param backIcon Back icon painter
 * @param subtitle Optional subtitle text
 * @param modifier Optional modifier
 * @param size Size variant
 * @param elevation Shadow elevation
 * @param containerColor Background color
 */
@Composable
fun BackTopNavBar(
    title: String,
    onBack: () -> Unit,
    backIcon: Painter,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    size: TopNavSize = TopNavSize.Medium,
    elevation: Dp = 0.dp,
    containerColor: Color = AppTheme.colors.baseSurfaceDefault
) {
    BaseTopNavBar(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        startActions = listOf(
            TopNavAction(
                icon = backIcon,
                contentDescription = "Navigate back",
                onClick = onBack
            )
        ),
        size = size,
        elevation = elevation,
        containerColor = containerColor
    )
}

/**
 * Convenience function for a top nav bar with only a title (no actions)
 *
 * @param title Title text to display
 * @param subtitle Optional subtitle text
 * @param modifier Optional modifier
 * @param titleAlignment Title alignment (defaults to Center)
 * @param size Size variant
 * @param elevation Shadow elevation
 * @param bottomDivider Show bottom divider
 * @param containerColor Background color
 */
@Composable
fun TitleOnlyTopNavBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleAlignment: TopNavTitleAlignment = TopNavTitleAlignment.Center,
    size: TopNavSize = TopNavSize.Medium,
    elevation: Dp = 0.dp,
    bottomDivider: Boolean = false,
    containerColor: Color = AppTheme.colors.baseSurfaceDefault
) {
    BaseTopNavBar(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        titleAlignment = titleAlignment,
        size = size,
        elevation = elevation,
        bottomDivider = bottomDivider,
        containerColor = containerColor
    )
}

/**
 * Convenience function for a top nav bar with title and profile avatar
 *
 * @param title Title text to display
 * @param profileImageUrl URL of the profile image
 * @param onAvatarClick Callback when avatar is clicked
 * @param subtitle Optional subtitle text
 * @param modifier Optional modifier
 * @param startActions Optional start actions (e.g., menu button)
 * @param endActions Optional additional end actions before avatar
 * @param size Size variant
 * @param elevation Shadow elevation
 * @param containerColor Background color
 */
@Composable
fun ProfileTopNavBar(
    title: String,
    profileImageUrl: String,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    startActions: List<TopNavAction> = emptyList(),
    endActions: List<TopNavAction> = emptyList(),
    size: TopNavSize = TopNavSize.Medium,
    elevation: Dp = 2.dp,
    containerColor: Color = AppTheme.colors.baseSurfaceDefault
) {
    BaseTopNavBar(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        startActions = startActions,
        endActions = endActions,
        profileImageUrl = profileImageUrl,
        onAvatarClick = onAvatarClick,
        size = size,
        elevation = elevation,
        containerColor = containerColor
    )
}

/**
 * Convenience function for a top nav bar with custom title composable
 *
 * @param titleComposable Custom composable for the title area
 * @param modifier Optional modifier
 * @param startActions Optional start actions
 * @param endActions Optional end actions
 * @param size Size variant
 * @param elevation Shadow elevation
 * @param bottomDivider Show bottom divider
 * @param containerColor Background color
 */
@Composable
fun CustomTitleTopNavBar(
    titleComposable: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    startActions: List<TopNavAction> = emptyList(),
    endActions: List<TopNavAction> = emptyList(),
    size: TopNavSize = TopNavSize.Medium,
    elevation: Dp = 0.dp,
    bottomDivider: Boolean = false,
    containerColor: Color = AppTheme.colors.baseSurfaceDefault
) {
    BaseTopNavBar(
        modifier = modifier,
        titleComposable = titleComposable,
        startActions = startActions,
        endActions = endActions,
        size = size,
        elevation = elevation,
        bottomDivider = bottomDivider,
        containerColor = containerColor
    )
}

