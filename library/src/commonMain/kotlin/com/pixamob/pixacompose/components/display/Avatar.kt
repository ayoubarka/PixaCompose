package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Container shape. [Circle] is the default (recommended).
 * [Rounded] and [Square] are available as extensions but deviate from
 * standard avatar presentation — document when used.
 */
enum class AvatarShape {
    Circle,
    Rounded,
    Square
}

enum class AvatarStatusPosition {
    TopLeft,
    TopRight,
    BottomLeft,
    BottomRight
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class AvatarConfig(
    val size: Dp,
    val textStyle: TextStyle,
    val iconSize: Dp,
    val statusSize: Dp,
    val maxInitials: Int,
    val supportsIconFallback: Boolean
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Resolves per-[SizeVariant] avatar config including typography, dimensions,
 * initial cap, and icon-fallback support.
 *
 * - Compact (24dp): single initial. Small+ (36dp+): up to 2 initials.
 * - Icon fallback supported through Large (64dp); unavailable at Huge/Massive (80/112dp).
 */
@Composable
private fun getAvatarConfig(size: SizeVariant): AvatarConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.None -> AvatarConfig(
            size = 0.dp,
            textStyle = typography.captionRegular,
            iconSize = 0.dp,
            statusSize = 0.dp,
            maxInitials = 0,
            supportsIconFallback = false
        )
        SizeVariant.Nano -> AvatarConfig(
            size = HierarchicalSize.Avatar.Nano,  // 16dp
            textStyle = typography.labelSmall,
            iconSize = HierarchicalSize.Icon.Nano,
            statusSize = HierarchicalSize.Spacing.Compact,
            maxInitials = 1,
            supportsIconFallback = true
        )
        SizeVariant.Compact -> AvatarConfig(
            size = HierarchicalSize.Avatar.Compact,  // 24dp — Uber "Label Xsmall"
            textStyle = typography.labelSmall,
            iconSize = HierarchicalSize.Icon.Nano,  // ~10dp
            statusSize = HierarchicalSize.Spacing.Small,
            maxInitials = 1,  // "Size 24 drops to single letter"
            supportsIconFallback = true
        )
        SizeVariant.Small -> AvatarConfig(
            size = HierarchicalSize.Avatar.Small,  // 36dp — Uber "Label medium"
            textStyle = typography.labelMedium,
            iconSize = HierarchicalSize.Icon.Compact,  // 14dp
            statusSize = HierarchicalSize.Spacing.Small,
            maxInitials = 2,
            supportsIconFallback = true
        )
        SizeVariant.Medium -> AvatarConfig(
            size = HierarchicalSize.Avatar.Medium,  // 48dp — Uber "Heading Xsmall"
            textStyle = typography.titleBold,
            iconSize = HierarchicalSize.Icon.Small,  // 18dp
            statusSize = HierarchicalSize.Icon.Nano,
            maxInitials = 2,
            supportsIconFallback = true
        )
        SizeVariant.Large -> AvatarConfig(
            size = HierarchicalSize.Avatar.Large,  // 64dp — Uber "Heading medium"
            textStyle = typography.headlineBold,
            iconSize = HierarchicalSize.Icon.Medium,  // 24dp
            statusSize = HierarchicalSize.Icon.Compact,
            maxInitials = 2,
            supportsIconFallback = true  // 64dp is icon-fallback cap
        )
        SizeVariant.Huge -> AvatarConfig(
            size = HierarchicalSize.Avatar.Huge,  // 80dp — Uber "Heading Xlarge"
            textStyle = typography.headerBold,
            iconSize = HierarchicalSize.Icon.Huge,
            statusSize = HierarchicalSize.Icon.Compact,
            maxInitials = 2,
            supportsIconFallback = false  // "not available for sizes 80 and 112"
        )
        SizeVariant.Massive -> AvatarConfig(
            size = HierarchicalSize.Avatar.Massive,  // 112dp — Uber "Display medium"
            textStyle = typography.displayMedium,
            iconSize = HierarchicalSize.Icon.Massive,
            statusSize = HierarchicalSize.Icon.Medium,
            maxInitials = 2,
            supportsIconFallback = false
        )
    }
}

private fun getAvatarShape(shape: AvatarShape, size: Dp): Shape {
    return when (shape) {
        AvatarShape.Circle -> CircleShape
        AvatarShape.Rounded -> RoundedCornerShape(size * 0.25f)
        AvatarShape.Square -> RoundedCornerShape(0.dp)
    }
}

/**
 * Extracts initials, capped at [maxChars].
 */
private fun getInitials(name: String, maxChars: Int): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    val initials = when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts[0].take(2)
        else -> "${parts[0].first()}${parts.last().first()}"
    }
    return initials.take(maxChars).uppercase()
}

/**
 * First name only — shows the user's first name to protect privacy.
 */
private fun getFirstName(name: String): String =
    name.trim().split(" ").firstOrNull { it.isNotBlank() } ?: name

/**
 * Cycles through 7 tokenized semantic color groups (brand/accent/info/success/
 * warning/error/base) for visually distinguishable avatars in a group.
 * Each pair provides a surface + content color with guaranteed contrast.
 */
@Composable
private fun avatarHashColorFor(name: String, colors: ColorPalette): Pair<Color, Color> {
    val rotation = listOf(
        colors.brandSurfaceDefault to colors.brandContentDefault,
        colors.accentSurfaceDefault to colors.accentContentDefault,
        colors.infoSurfaceDefault to colors.infoContentDefault,
        colors.successSurfaceDefault to colors.successContentDefault,
        colors.warningSurfaceDefault to colors.warningContentDefault,
        colors.errorSurfaceDefault to colors.errorContentDefault,
        colors.baseSurfaceFocus to colors.baseContentTitle
    )
    val index = (name.trim().hashCode().let { if (it == Int.MIN_VALUE) 0 else kotlin.math.abs(it) }) % rotation.size
    return rotation[index]
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL AVATAR
// ════════════════════════════════════════════════════════════════════════════

/** Hover/pressed state-layer scrim: black with 4%/8% alpha overlay. */
@Composable
private fun BoxScope.AvatarOverlayScrim(alpha: Float, shape: Shape) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .clip(shape)
            .background(Color.Black.copy(alpha = alpha))
    )
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaAvatar — circular visual representation of a person or business.
 *
 * ### Anatomy
 * Container + content: photo, or fallback of initials / icon / [backgroundContent].
 * Priority: image > icon (at supported sizes) > [backgroundContent] > initials.
 * Optional status badge overlays (max 2).
 *
 * ### Sizing
 * [size] resolves through [HierarchicalSize.Avatar] (24/36/48/64/80/112dp).
 * Compact (24dp): single initial. Small+ (36dp+): up to 2 initials.
 * Icon fallback unavailable at Huge/Massive (80/112dp).
 *
 * ### States
 * Enabled, hover/pressed (black scrim, only with [onClick]), focus (accent border),
 * disabled (dimmed content, image at 40% opacity), preloading ([SkeletonCircle]).
 *
 * @param modifier Modifier for the avatar
 * @param size Avatar size (Default: Medium, 48dp)
 * @param shape Container shape (Default: [AvatarShape.Circle])
 * @param isLoading Shows [SkeletonCircle] (Default: false)
 * @param enabled Whether interactive/full-opacity (Default: true)
 * @param imageUrl Remote photo URL (Coil)
 * @param imageModel Alternative Coil image model
 * @param text Name for initials (capped per size) and the first-name accessibility label
 * @param icon Icon fallback painter (unsupported at Huge/Massive)
 * @param backgroundContent Vector/illustration behind initials
 * @param placeholder Placeholder while image loads
 * @param backgroundColor Background color (defaults to theme token)
 * @param contentColor Text/icon color (defaults to theme token)
 * @param borderColor Border color (defaults to 1dp inside-aligned border)
 * @param borderWidth Border width (Default: 1dp)
 * @param onClick Click handler — enables hover/press/focus states
 * @param statusBadge Primary badge overlay
 * @param statusPosition Position of [statusBadge]
 * @param secondaryStatusBadge Second badge overlay (max 2)
 * @param secondaryStatusPosition Position of [secondaryStatusBadge]
 * @param contentDescription Accessibility label (defaults to first-name-only)
 */
@Composable
fun PixaAvatar(
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    shape: AvatarShape = AvatarShape.Circle,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    imageUrl: String? = null,
    imageModel: Any? = null,
    text: String? = null,
    icon: Painter? = null,
    backgroundContent: (@Composable () -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    backgroundColor: Color = AppTheme.colors.baseSurfaceSubtle,
    contentColor: Color = AppTheme.colors.baseContentBody,
    borderColor: Color? = null,
    borderWidth: Dp = HierarchicalSize.Border.Compact,
    onClick: (() -> Unit)? = null,
    statusBadge: (@Composable () -> Unit)? = null,
    statusPosition: AvatarStatusPosition = AvatarStatusPosition.BottomRight,
    secondaryStatusBadge: (@Composable () -> Unit)? = null,
    secondaryStatusPosition: AvatarStatusPosition = AvatarStatusPosition.TopRight,
    contentDescription: String? = null
) {
    val config = getAvatarConfig(size)
    val avatarShape = getAvatarShape(shape, config.size)

    if (isLoading) {
        SkeletonCircle(
            size = config.size,
            modifier = modifier,
            shimmerEnabled = true
        )
        return
    }

    val model = imageModel ?: imageUrl
    val initials = text?.let { getInitials(it, config.maxInitials) }
    val showIcon = icon != null && config.supportsIconFallback

    val effectiveBackground = if (enabled) backgroundColor else AppTheme.colors.baseSurfaceDisabled
    val effectiveContent = if (enabled) contentColor else AppTheme.colors.baseContentDisabled
    val effectiveBorderColor = borderColor ?: AppTheme.colors.baseBorderDefault

    val accessibilityDescription = contentDescription ?: when {
        text != null -> getFirstName(text)
        imageUrl != null -> "Profile picture"
        icon != null -> "Avatar icon"
        else -> "Avatar"
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val interactive = enabled && onClick != null

    Box(
        modifier = modifier
            .semantics {
                this.contentDescription = accessibilityDescription
                if (interactive) {
                    this.role = Role.Button
                }
            }
    ) {
        Box(
            modifier = Modifier
                .size(config.size)
                .clip(avatarShape)
                .background(effectiveBackground)
                .border(borderWidth, effectiveBorderColor, avatarShape)
                .focusable(interactionSource = interactionSource, enabled = interactive)
                .then(
                    if (isFocused && interactive) {
                        Modifier.border(HierarchicalSize.Border.Large, AppTheme.colors.accentBorderDefault, avatarShape)
                    } else {
                        Modifier
                    }
                )
                .then(
                    if (interactive) {
                        Modifier.clickable(
                            onClick = onClick,
                            indication = pixaRipple(bounded = true, color = effectiveContent.copy(alpha = 0.2f)),
                            interactionSource = interactionSource
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                model != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (enabled) 1f else 0.4f), // "images at 40% opacity" when disabled
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

                showIcon -> {
                    PixaIcon(
                        painter = icon!!,
                        contentDescription = null,
                        modifier = Modifier.size(config.iconSize),
                        tint = effectiveContent
                    )
                }

                backgroundContent != null -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        backgroundContent()
                    }
                    initials?.takeIf { it.isNotEmpty() }?.let {
                        BasicText(
                            text = it,
                            style = config.textStyle.copy(
                                color = effectiveContent,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1
                        )
                    }
                }

                initials != null && initials.isNotEmpty() -> {
                    BasicText(
                        text = initials,
                        style = config.textStyle.copy(
                            color = effectiveContent,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1
                    )
                }
            }

            when {
                isPressed && interactive -> AvatarOverlayScrim(0.08f, avatarShape)
                isHovered && interactive -> AvatarOverlayScrim(0.04f, avatarShape)
            }
        }

        statusBadge?.let {
            AvatarBadgeOverlay(position = statusPosition, offset = config.statusSize * 0.15f, content = it)
        }

        secondaryStatusBadge?.let {
            AvatarBadgeOverlay(position = secondaryStatusPosition, offset = config.statusSize * 0.15f, content = it)
        }
    }
}

@Composable
private fun BoxScope.AvatarBadgeOverlay(
    position: AvatarStatusPosition,
    offset: Dp,
    content: @Composable () -> Unit
) {
    val alignment = when (position) {
        AvatarStatusPosition.TopLeft -> Alignment.TopStart
        AvatarStatusPosition.TopRight -> Alignment.TopEnd
        AvatarStatusPosition.BottomLeft -> Alignment.BottomStart
        AvatarStatusPosition.BottomRight -> Alignment.BottomEnd
    }

    Box(
        modifier = Modifier
            .align(alignment)
            .offset(
                x = when (position) {
                    AvatarStatusPosition.TopRight, AvatarStatusPosition.BottomRight -> offset
                    else -> -offset
                },
                y = when (position) {
                    AvatarStatusPosition.BottomLeft, AvatarStatusPosition.BottomRight -> offset
                    else -> -offset
                }
            )
    ) {
        content()
    }
}

/**
 * PixaAvatarGroup — multiple avatars stacked with overlap, e.g. participant
 * lists or "+N more" overflow indicators.
 *
 * @param avatars List of avatar configurations
 * @param maxVisible Maximum number of avatars to show before a "+N" indicator
 * @param size Size of each avatar
 * @param modifier Modifier for the group
 * @param spacing Overlap spacing (negative value for overlap, e.g. -8.dp)
 * @param borderColor Border color for each avatar (helps overlap visibility)
 * @param borderWidth Border width
 * @param shape Shape for all avatars
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
    val colors = AppTheme.colors

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        visibleAvatars.forEachIndexed { index, avatarData ->
            val hashColors = if (avatarData.useHashColor && avatarData.text != null) {
                avatarHashColorFor(avatarData.text, colors)
            } else {
                null
            }

            // Apply z-index to ensure proper stacking (first items on top)
            PixaAvatar(
                modifier = Modifier.zIndex((visibleAvatars.size - index).toFloat()),
                size = size,
                shape = shape,
                imageUrl = avatarData.imageUrl,
                imageModel = avatarData.imageModel,
                text = avatarData.text,
                icon = avatarData.icon,
                backgroundColor = avatarData.backgroundColor ?: hashColors?.first ?: AppTheme.colors.baseSurfaceSubtle,
                contentColor = avatarData.contentColor ?: hashColors?.second ?: AppTheme.colors.baseContentBody,
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
 * Avatar configuration for use in [PixaAvatarGroup].
 *
 * @param imageUrl URL for remote image
 * @param imageModel Alternative Coil image model
 * @param text Name used to derive initials
 * @param icon Icon painter to display
 * @param backgroundColor Background color (overrides [useHashColor])
 * @param contentColor Content color for text/icon
 * @param useHashColor Rotate through tokenized semantic color groups keyed off [text]
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
