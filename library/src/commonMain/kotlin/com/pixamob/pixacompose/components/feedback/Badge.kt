package com.pixamob.pixacompose.components.feedback

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*

/**
 * Badge Component - Small status indicator for notification counts, status dots, or labels
 */

/**
 * Badge variant types
 */
enum class BadgeVariant {
    /** Primary brand color - important notifications */
    Primary,
    /** Success/positive indicator */
    Success,
    /** Warning/caution indicator */
    Warning,
    /** Error/critical indicator */
    Error,
    /** Neutral/default indicator */
    Neutral,
    /** Informational indicator */
    Info
}

/**
 * Badge size variants
 */
enum class BadgeSize {
    /** 6dp - Small dot indicator */
    Dot,
    /** 16dp - Minimal badge */
    Small,
    /** 20dp - Default badge size */
    Medium,
    /** 24dp - Large badge */
    Large
}

/**
 * Badge style - affects appearance
 */
enum class BadgeStyle {
    /** Solid background */
    Solid,
    /** Outlined with border */
    Outlined,
    /** Subtle background */
    Subtle
}

/**
 * Badge colors for different states
 */
@Immutable
@Stable
data class BadgeColors(
    val background: Color,
    val content: Color,
    val border: Color = Color.Transparent
)

/**
 * Badge configuration
 */
@Immutable
@Stable
data class BadgeConfig(
    val size: Dp,
    val padding: Dp,
    val textStyle: TextStyle,
    val cornerRadius: Dp,
    val iconSize: Dp
)

/**
 * Get badge configuration based on size
 */
@Composable
private fun getBadgeConfig(size: BadgeSize): BadgeConfig {
    val typography = AppTheme.typography
    return when (size) {
        BadgeSize.Dot -> BadgeConfig(
            size = 8.dp,
            padding = 0.dp,
            textStyle = typography.captionBold,
            cornerRadius = RadiusSize.Full,
            iconSize = 6.dp
        )
        BadgeSize.Small -> BadgeConfig(
            size = 16.dp,
            padding = 3.dp,
            textStyle = typography.labelSmall,  // 10sp - proper Nano text size
            cornerRadius = RadiusSize.Small,
            iconSize = 10.dp
        )
        BadgeSize.Medium -> BadgeConfig(
            size = 20.dp,
            padding = 4.dp,
            textStyle = typography.labelSmall,  // 10sp - proper Nano text size
            cornerRadius = RadiusSize.Medium,
            iconSize = 12.dp
        )
        BadgeSize.Large -> BadgeConfig(
            size = 24.dp,
            padding = 5.dp,
            textStyle = typography.labelMedium,  // 12sp - proper Compact text size
            cornerRadius = RadiusSize.Medium,
            iconSize = 14.dp
        )
    }
}

/**
 * Get badge colors based on variant and style
 */
@Composable
private fun getBadgeColors(
    variant: BadgeVariant,
    style: BadgeStyle,
    colors: ColorPalette
): BadgeColors {
    return when (style) {
        BadgeStyle.Solid -> when (variant) {
            BadgeVariant.Primary -> BadgeColors(
                background = colors.brandContentDefault,
                content = colors.baseContentNegative
            )
            BadgeVariant.Success -> BadgeColors(
                background = colors.successContentDefault,
                content = colors.baseContentNegative
            )
            BadgeVariant.Warning -> BadgeColors(
                background = colors.warningContentDefault,
                content = colors.baseContentNegative
            )
            BadgeVariant.Error -> BadgeColors(
                background = colors.errorContentDefault,
                content = colors.baseContentNegative
            )
            BadgeVariant.Neutral -> BadgeColors(
                background = colors.baseSurfaceDefault,
                content = colors.baseContentBody
            )
            BadgeVariant.Info -> BadgeColors(
                background = colors.infoContentDefault,
                content = colors.baseContentNegative
            )
        }
        BadgeStyle.Outlined -> when (variant) {
            BadgeVariant.Primary -> BadgeColors(
                background = Color.Transparent,
                content = colors.brandContentDefault,
                border = colors.brandBorderDefault
            )
            BadgeVariant.Success -> BadgeColors(
                background = Color.Transparent,
                content = colors.successContentDefault,
                border = colors.successBorderDefault
            )
            BadgeVariant.Warning -> BadgeColors(
                background = Color.Transparent,
                content = colors.warningContentDefault,
                border = colors.warningBorderDefault
            )
            BadgeVariant.Error -> BadgeColors(
                background = Color.Transparent,
                content = colors.errorContentDefault,
                border = colors.errorBorderDefault
            )
            BadgeVariant.Neutral -> BadgeColors(
                background = Color.Transparent,
                content = colors.baseContentBody,
                border = colors.baseBorderDefault
            )
            BadgeVariant.Info -> BadgeColors(
                background = Color.Transparent,
                content = colors.infoContentDefault,
                border = colors.infoBorderDefault
            )
        }
        BadgeStyle.Subtle -> when (variant) {
            BadgeVariant.Primary -> BadgeColors(
                background = colors.brandSurfaceSubtle,
                content = colors.brandContentDefault
            )
            BadgeVariant.Success -> BadgeColors(
                background = colors.successSurfaceSubtle,
                content = colors.successContentDefault
            )
            BadgeVariant.Warning -> BadgeColors(
                background = colors.warningSurfaceSubtle,
                content = colors.warningContentDefault
            )
            BadgeVariant.Error -> BadgeColors(
                background = colors.errorSurfaceSubtle,
                content = colors.errorContentDefault
            )
            BadgeVariant.Neutral -> BadgeColors(
                background = colors.baseSurfaceSubtle,
                content = colors.baseContentBody
            )
            BadgeVariant.Info -> BadgeColors(
                background = colors.infoSurfaceSubtle,
                content = colors.infoContentDefault
            )
        }
    }
}

/**
 * Badge - Small status indicator
 *
 * A versatile component for displaying notification counts, status indicators, or labels.
 *
 * @param content Optional text content for the badge (e.g., "5", "NEW"). If null, shows dot only
 * @param variant Badge color variant (Primary, Success, Warning, Error, Neutral, Info)
 * @param size Badge size (Dot, Small, Medium, Large)
 * @param style Badge style (Solid, Outlined, Subtle)
 * @param modifier Optional modifier for the badge
 * @param onClick Optional click handler for interactive badges
 * @param icon Optional icon painter to display instead of or with text
 * @param maxCount Maximum count to display before showing "99+" (default: 99)
 * @param pulse Enable pulse animation for dot badges (useful for live notifications)
 * @param contentDescription Accessibility description (auto-generated if null)
 *
 * @sample
 * ```
 * // Dot indicator
 * Badge(size = BadgeSize.Dot, variant = BadgeVariant.Error)
 *
 * // Notification count
 * Badge(content = "5", variant = BadgeVariant.Primary)
 *
 * // Count with max (shows "99+" if over 99)
 * Badge(content = notificationCount.toString(), maxCount = 99)
 *
 * // Clickable badge
 * Badge(
 *     content = "5",
 *     onClick = { /* clear notifications */ }
 * )
 *
 * // Outlined badge
 * Badge(
 *     content = "NEW",
 *     style = BadgeStyle.Outlined,
 *     variant = BadgeVariant.Success
 * )
 * ```
 */
@Composable
fun PixaBadge(
    content: String? = null,
    variant: BadgeVariant = BadgeVariant.Error,
    size: BadgeSize = BadgeSize.Medium,
    style: BadgeStyle = BadgeStyle.Solid,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: Painter? = null,
    maxCount: Int = 99,
    pulse: Boolean = false,
    contentDescription: String? = null
) {
    val colors = AppTheme.colors
    val config = getBadgeConfig(size)
    val badgeColors = getBadgeColors(variant, style, colors)

    // Process content with maxCount logic
    val displayContent = content?.let { text ->
        text.toIntOrNull()?.let { count ->
            if (count > maxCount) "${maxCount}+" else text
        } ?: text
    }

    val isDot = size == BadgeSize.Dot || (displayContent == null && icon == null)

    // Pulse animation for dot badges
    val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (pulse && isDot) 1.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Generate content description
    val accessibilityDescription = contentDescription ?: when {
        displayContent != null -> "$displayContent notifications"
        isDot -> "Status indicator"
        else -> "Badge"
    }

    Box(
        modifier = modifier
            .semantics {
                this.contentDescription = accessibilityDescription
            }
            .then(
                if (isDot) {
                    Modifier
                        .size(config.size)
                        .scale(if (pulse) pulseScale else 1f)
                } else {
                    Modifier.sizeIn(minWidth = config.size, minHeight = config.size)
                }
            )
            .clip(if (isDot) CircleShape else RoundedCornerShape(config.cornerRadius))
            .background(badgeColors.background)
            .then(
                if (badgeColors.border != Color.Transparent) {
                    Modifier.border(
                        width = BorderSize.Standard,
                        color = badgeColors.border,
                        shape = if (isDot) CircleShape else RoundedCornerShape(config.cornerRadius)
                    )
                } else {
                    Modifier
                }
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick,
                        indication = ripple(bounded = true, color = badgeColors.content.copy(alpha = 0.2f)),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                } else {
                    Modifier
                }
            )
            .then(
                if (!isDot) {
                    Modifier.padding(horizontal = config.padding, vertical = config.padding / 2)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!isDot) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    PixaIcon(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier.size(config.iconSize),
                        tint = badgeColors.content
                    )
                }
                displayContent?.let {
                    Text(
                        text = it,
                        style = config.textStyle,
                        color = badgeColors.content,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * BadgedBox - Convenience composable to position a badge over content
 *
 * @param badge The badge composable to display
 * @param modifier Modifier for the container
 * @param content The content to badge
 *
 * @sample
 * ```
 * BadgedBox(
 *     badge = { Badge(content = "5", variant = BadgeVariant.Error) }
 * ) {
 *     PixaIcon(Icons.Default.Notifications, contentDescription = "Notifications")
 * }
 * ```
 */
@Composable
fun BadgedBox(
    badge: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        content()
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-4).dp)
        ) {
            badge()
        }
    }
}
