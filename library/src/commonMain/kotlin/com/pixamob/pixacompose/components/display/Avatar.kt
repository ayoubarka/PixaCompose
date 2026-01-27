package com.pixamob.pixacompose.components.display

import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.pixamob.pixacompose.components.feedback.SkeletonCircle
import com.pixamob.pixacompose.theme.AppTheme

/**
 * Avatar Component
 *
 * Display user profile pictures, initials, or placeholder icons.
 * Supports images (with Coil), text initials, icons, and status badges.
 */


/**
 * Avatar shape variants
 */
enum class AvatarShape {
    /** Circular avatar */
    Circle,
    /** Rounded square avatar */
    Rounded,
    /** Square avatar */
    Square
}

/**
 * Avatar status indicator position
 */
enum class AvatarStatusPosition {
    TopLeft,
    TopRight,
    BottomLeft,
    BottomRight
}

/**
 * Avatar configuration
 */
@Immutable
@Stable
data class AvatarConfig(
    val size: Dp,
    val textStyle: TextStyle,
    val iconSize: Dp,
    val statusSize: Dp
)

/**
 * Get avatar configuration based on size
 */
@Composable
private fun getAvatarConfig(size: SizeVariant): AvatarConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.None -> AvatarConfig(
            size = 0.dp,
            textStyle = typography.captionRegular,
            iconSize = 0.dp,
            statusSize = 0.dp
        )
        SizeVariant.Nano -> AvatarConfig(
            size = HierarchicalSize.Avatar.Nano,  // 16dp
            textStyle = typography.labelSmall,  // 10sp - micro labels
            iconSize = HierarchicalSize.Icon.Nano,  // 12dp
            statusSize = HierarchicalSize.Spacing.Compact  // 4dp
        )
        SizeVariant.Compact -> AvatarConfig(
            size = HierarchicalSize.Avatar.Compact,  // 24dp
            textStyle = typography.labelMedium,  // 12sp - compact size
            iconSize = HierarchicalSize.Icon.Compact,  // 16dp
            statusSize = HierarchicalSize.Spacing.Small  // 8dp
        )
        SizeVariant.Small -> AvatarConfig(
            size = HierarchicalSize.Avatar.Small,  // 32dp
            textStyle = typography.bodyRegular,  // 16sp - body text
            iconSize = HierarchicalSize.Icon.Small,  // 20dp
            statusSize = HierarchicalSize.Spacing.Small  // 8dp
        )
        SizeVariant.Medium -> AvatarConfig(
            size = HierarchicalSize.Avatar.Medium,  // 40dp
            textStyle = typography.bodyRegular,  // 16sp - standard (not bold)
            iconSize = HierarchicalSize.Icon.Medium,  // 24dp
            statusSize = HierarchicalSize.Icon.Nano  // 12dp
        )
        SizeVariant.Large -> AvatarConfig(
            size = HierarchicalSize.Avatar.Large,  // 48dp
            textStyle = typography.subtitleBold,  // 18sp - subtitle
            iconSize = HierarchicalSize.Icon.Large,  // 28dp
            statusSize = HierarchicalSize.Icon.Compact  // 16dp
        )
        SizeVariant.Huge -> AvatarConfig(
            size = HierarchicalSize.Avatar.Huge,  // 64dp
            textStyle = typography.titleBold,  // 20sp - title
            iconSize = HierarchicalSize.Icon.Huge,  // 32dp
            statusSize = HierarchicalSize.Icon.Compact  // 16dp
        )
        SizeVariant.Massive -> AvatarConfig(
            size = HierarchicalSize.Avatar.Massive,  // 96dp
            textStyle = typography.headlineBold,  // 24sp - headline (not display)
            iconSize = HierarchicalSize.Icon.Massive,  // 48dp
            statusSize = HierarchicalSize.Icon.Medium  // 24dp
        )
    }
}

/**
 * Get avatar shape
 */
private fun getAvatarShape(shape: AvatarShape, size: Dp): Shape {
    return when (shape) {
        AvatarShape.Circle -> CircleShape
        AvatarShape.Rounded -> RoundedCornerShape(size * 0.25f)
        AvatarShape.Square -> RoundedCornerShape(0.dp)
    }
}

/**
 * Extract initials from a name
 */
private fun getInitials(name: String): String {
    val parts = name.trim().split(" ")
    return when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "${parts[0].first()}${parts.last().first()}".uppercase()
    }
}

/**
 * Avatar - Display user profile pictures, initials, or icons
 *
 * @param modifier Modifier for the avatar
 * @param size Avatar size
 * @param shape Avatar shape (Circle, Rounded, Square)
 * @param imageUrl URL for remote image (uses Coil)
 * @param imageModel Alternative image model for Coil (use instead of imageUrl for more control)
 * @param text Text to display as initials (auto-extracts initials from name)
 * @param icon Icon painter to display
 * @param placeholder Placeholder to show while loading image
 * @param backgroundColor Background color (defaults to theme color)
 * @param contentColor Content color for text/icon
 * @param borderColor Optional border color
 * @param borderWidth Border width (0.dp for no border)
 * @param onClick Optional click handler
 * @param statusBadge Optional composable for status badge overlay
 * @param statusPosition Position of status badge
 * @param contentDescription Accessibility description
 *
 * @sample
 * ```
 * // Image avatar with URL
 * Avatar(
 *     imageUrl = "https://example.com/avatar.jpg",
 *     size = AvatarSize.Large
 * )
 *
 * // Initials avatar
 * Avatar(
 *     text = "John Doe",
 *     size = AvatarSize.Medium
 * )
 *
 * // Icon avatar
 * Avatar(
 *     icon = rememberVectorPainter(Icons.Default.Person),
 *     size = AvatarSize.Small
 * )
 *
 * // Avatar with status badge
 * Avatar(
 *     imageUrl = "https://example.com/avatar.jpg",
 *     statusBadge = {
 *         Badge(
 *             size = BadgeSize.Dot,
 *             variant = BadgeVariant.Success
 *         )
 *     }
 * )
 *
 * // Clickable avatar with border
 * Avatar(
 *     text = "Jane Smith",
 *     borderColor = AppTheme.colors.brandBorderDefault,
 *     borderWidth = 2.dp,
 *     onClick = { /* handle click */ }
 * )
 * ```
 */
@Composable
fun PixaAvatar(
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    shape: AvatarShape = AvatarShape.Circle,
    isLoading: Boolean = false,
    imageUrl: String? = null,
    imageModel: Any? = null,
    text: String? = null,
    icon: Painter? = null,
    placeholder: @Composable (() -> Unit)? = null,
    backgroundColor: Color = AppTheme.colors.baseSurfaceSubtle,
    contentColor: Color = AppTheme.colors.baseContentBody,
    borderColor: Color? = null,
    borderWidth: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    statusBadge: (@Composable () -> Unit)? = null,
    statusPosition: AvatarStatusPosition = AvatarStatusPosition.BottomRight,
    contentDescription: String? = null
) {
    val config = getAvatarConfig(size)
    val avatarShape = getAvatarShape(shape, config.size)

    // Show skeleton when isLoading = true
    if (isLoading) {
        SkeletonCircle(
            size = config.size,
            modifier = modifier,
            shimmerEnabled = true
        )
        return
    }

    // Determine display mode priority: image > icon > text
    val model = imageModel ?: imageUrl
    val initials = text?.let { getInitials(it) }

    val accessibilityDescription = contentDescription ?: when {
        text != null -> "Avatar for $text"
        imageUrl != null -> "Profile picture"
        icon != null -> "Avatar icon"
        else -> "Avatar"
    }

    Box(
        modifier = modifier
            .semantics {
                this.contentDescription = accessibilityDescription
                if (onClick != null) {
                    this.role = Role.Button
                }
            }
    ) {
        Box(
            modifier = Modifier
                .size(config.size)
                .clip(avatarShape)
                .background(backgroundColor)
                .then(
                    if (borderColor != null && borderWidth > 0.dp) {
                        Modifier.border(borderWidth, borderColor, avatarShape)
                    } else {
                        Modifier
                    }
                )
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            onClick = onClick,
                            indication = ripple(bounded = true, color = contentColor.copy(alpha = 0.2f)),
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Priority 1: Image
                model != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = model,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                // Priority 2: Icon
                icon != null -> {
                    PixaIcon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(config.iconSize),
                        tint = contentColor
                    )
                }
                // Priority 3: Text initials
                initials != null -> {
                    Text(
                        text = initials,
                        style = config.textStyle,
                        color = contentColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }

        // Status badge overlay
        statusBadge?.let {
            val alignment = when (statusPosition) {
                AvatarStatusPosition.TopLeft -> Alignment.TopStart
                AvatarStatusPosition.TopRight -> Alignment.TopEnd
                AvatarStatusPosition.BottomLeft -> Alignment.BottomStart
                AvatarStatusPosition.BottomRight -> Alignment.BottomEnd
            }

            val offset = config.statusSize * 0.15f

            Box(
                modifier = Modifier
                    .align(alignment)
                    .offset(
                        x = when (statusPosition) {
                            AvatarStatusPosition.TopRight, AvatarStatusPosition.BottomRight -> offset
                            else -> -offset
                        },
                        y = when (statusPosition) {
                            AvatarStatusPosition.BottomLeft, AvatarStatusPosition.BottomRight -> offset
                            else -> -offset
                        }
                    )
            ) {
                statusBadge()
            }
        }
    }
}

/**
 * AvatarGroup - Display multiple avatars stacked together
 *
 * @param avatars List of avatar configurations
 * @param maxVisible Maximum number of avatars to show before "+N" indicator
 * @param size Size of each avatar
 * @param modifier Modifier for the group
 * @param spacing Overlap spacing (negative value for overlap, use -8.dp for typical stacking)
 * @param borderColor Border color for each avatar (helps with overlap visibility)
 * @param borderWidth Border width
 * @param shape Shape for all avatars
 *
 * @sample
 * ```
 * // Basic avatar group with stacking
 * AvatarGroup(
 *     avatars = listOf(
 *         AvatarData(imageUrl = "url1"),
 *         AvatarData(text = "John Doe"),
 *         AvatarData(text = "Jane Smith"),
 *         AvatarData(imageUrl = "url4")
 *     ),
 *     maxVisible = 3,
 *     size = AvatarSize.Small,
 *     spacing = (-8).dp
 * )
 *
 * // Avatar group with custom colors
 * AvatarGroup(
 *     avatars = listOf(
 *         AvatarData(
 *             text = "AB",
 *             backgroundColor = AppTheme.colors.brandSurfaceDefault
 *         ),
 *         AvatarData(
 *             text = "CD",
 *             backgroundColor = AppTheme.colors.accentSurfaceDefault
 *         )
 *     )
 * )
 * ```
 */
@Composable
fun PixaAvatarGroup(
    avatars: List<AvatarData>,
    maxVisible: Int = 5,
    size: SizeVariant = SizeVariant.Small,
    modifier: Modifier = Modifier,
    spacing: Dp = (-8).dp,
    borderColor: Color = AppTheme.colors.baseSurfaceDefault,
    borderWidth: Dp = 2.dp,
    shape: AvatarShape = AvatarShape.Circle
) {
    val visibleAvatars = avatars.take(maxVisible)
    val remainingCount = (avatars.size - maxVisible).coerceAtLeast(0)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        visibleAvatars.forEachIndexed { index, avatarData ->
            // Apply z-index to ensure proper stacking (first items on top)
            PixaAvatar(
                modifier = Modifier.zIndex((visibleAvatars.size - index).toFloat()),
                size = size,
                shape = shape,
                imageUrl = avatarData.imageUrl,
                imageModel = avatarData.imageModel,
                text = avatarData.text,
                icon = avatarData.icon,
                backgroundColor = avatarData.backgroundColor ?: AppTheme.colors.baseSurfaceSubtle,
                contentColor = avatarData.contentColor ?: AppTheme.colors.baseContentBody,
                borderColor = borderColor,
                borderWidth = borderWidth,
                contentDescription = avatarData.contentDescription
            )
        }

        if (remainingCount > 0) {
            PixaAvatar(
                modifier = Modifier.zIndex(0f),
                size = size,
                shape = shape,
                text = "+$remainingCount",
                backgroundColor = AppTheme.colors.baseSurfaceDefault,
                contentColor = AppTheme.colors.baseContentBody,
                borderColor = borderColor,
                borderWidth = borderWidth,
                contentDescription = "$remainingCount more avatars"
            )
        }
    }
}

/**
 * Data class for avatar configuration in groups
 *
 * @param imageUrl URL for remote image
 * @param imageModel Alternative image model for Coil
 * @param text Text to display as initials
 * @param icon Icon painter to display
 * @param backgroundColor Background color (overrides useHashColor)
 * @param contentColor Content color for text/icon
 * @param useHashColor Generate background color from text hash
 * @param contentDescription Accessibility description
 */
@Immutable
data class AvatarData(
    val imageUrl: String? = null,
    val imageModel: Any? = null,
    val text: String? = null,
    val icon: Painter? = null,
    val backgroundColor: Color? = null,
    val contentColor: Color? = null,
    val useHashColor: Boolean = false,
    val contentDescription: String? = null
)
