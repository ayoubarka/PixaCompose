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
 * Container shape. Uber Base's Avatar spec fixes this to always-circular
 * ("Shape (always circular)" under its Fixed customization boundary, "Don't
 * scale or reshape...use the available Avatar sizes and shapes instead") —
 * [Circle] is therefore the default and the only spec-compliant choice.
 * [Rounded]/[Square] are kept as a pre-existing Pixa-native extension beyond
 * the Uber spec (not deleted, since removing working API surface without a
 * concrete reason would break callers), but reaching for them opts out of
 * spec fidelity — document that at the call site if used.
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
 * Resolves per-[SizeVariant] avatar config. Typography pairing follows Uber
 * Base's literal table (24→Label Xsmall, 36→Label medium, 48→Heading Xsmall,
 * 64→Heading medium, 80→Heading Xlarge, 112→Display medium) mapped onto
 * Pixa's own tier names in the same relative order (label < title < headline
 * < header < display) since Pixa has no separate "Heading" family — 112's
 * `displayMedium` is a literal 1:1 name match, a useful anchor point.
 *
 * [maxInitials]/[supportsIconFallback] encode two literal Uber Base content
 * rules: "size 24 drops to single letter; sizes 36+ support up to 2
 * characters," and "icon fallback sizes cap at 64...not available for sizes
 * 80 and 112."
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
            size = HierarchicalSize.Avatar.Nano,  // 16dp — Pixa extension, below Uber's floor
            textStyle = typography.labelSmall,
            iconSize = HierarchicalSize.Icon.Nano,
            statusSize = HierarchicalSize.Spacing.Compact,
            maxInitials = 1,
            supportsIconFallback = true
        )
        SizeVariant.Compact -> AvatarConfig(
            size = HierarchicalSize.Avatar.Compact,  // 24dp — Uber "Label Xsmall"
            textStyle = typography.labelSmall,
            iconSize = HierarchicalSize.Icon.Nano,  // ~10dp, closest token to Uber's literal 12dp
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
            iconSize = HierarchicalSize.Icon.Medium,  // 24dp, closest token to Uber's literal 26dp
            statusSize = HierarchicalSize.Icon.Compact,
            maxInitials = 2,
            supportsIconFallback = true  // 64dp is Uber's icon-fallback cap — still supported
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
 * Extracts initials, capped at [maxChars] — the literal single-letter-at-24dp
 * / two-characters-at-36dp+ rule from Uber Base's content model.
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
 * First name only, per Uber Base's accessibility guidance: "show the user's
 * first name only, to protect privacy" rather than a full name.
 */
private fun getFirstName(name: String): String =
    name.trim().split(" ").firstOrNull { it.isNotBlank() } ?: name

/**
 * Rotation used by [PixaAvatarGroup]'s `useHashColor`. Uber Base recommends
 * an 8-swatch literal hex rotation (Blue600/Red600/Yellow300/Purple600/
 * Green600/Magenta600/Gray600/Orange600) to "diversify the background color
 * palette when many actors are involved" — but it explicitly notes this
 * default sequence is a *recommendation*, not a fixed customization boundary
 * (color selection is listed under "Flexible"). Hardcoding 8 raw hex swatches
 * would violate this project's "colors must come from AppTheme.colors.*"
 * rule, so the rotation instead cycles through 7 already-tokenized semantic
 * groups (brand/accent/info/success/warning/error/base), each paired with
 * its own `*ContentDefault` token for guaranteed contrast — preserving the
 * spec's actual intent (visually distinguishable, contrast-safe avatars)
 * without introducing untracked raw colors.
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

/** Hover/pressed state-layer scrim — Uber Base's literal "base color + 4%/8% black overlay." */
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
 * PixaAvatar — a circular visual representation of a person or business.
 *
 * ### Anatomy
 * A required circular container plus required content: a user photo, or a
 * fallback of initials / an icon / a [backgroundContent] vector pattern —
 * priority is image > icon (only at sizes where [AvatarConfig.supportsIconFallback]
 * is true) > [backgroundContent] > initials. An optional status badge overlays
 * the container ([statusBadge]/[secondaryStatusBadge], capped at 2 per Uber
 * Base's "avoid overloading an avatar with too many badges").
 *
 * ### Sizing
 * [size] resolves through [HierarchicalSize.Avatar], retuned to Uber Base's
 * exact 6-size ladder (24/36/48/64/80/112dp across Compact..Massive) — see
 * [getAvatarConfig]. Initials are capped at 1 character for [SizeVariant.Compact]
 * (24dp) and 2 for [SizeVariant.Small] (36dp) and above; icon fallback is
 * unavailable at [SizeVariant.Huge]/[SizeVariant.Massive] (80/112dp).
 *
 * ### States
 * Enabled, hover/pressed (4%/8% black scrim, only when [onClick] is set),
 * focus (3dp `accentBorderDefault` outline via keyboard focus), disabled
 * (dimmed surface/content tokens, image content at 40% opacity), preloading
 * ([isLoading] → [SkeletonCircle]). Uber Base has no direct active/selected
 * state on Avatar itself — "pair with list item controls" instead, which
 * this component doesn't attempt to own.
 *
 * ### Customization
 * [shape] defaults to [AvatarShape.Circle] (spec-compliant); [backgroundColor]/
 * [contentColor] are free-form theme-token overrides per Uber Base's
 * "Flexible: Color selection" boundary. [borderColor]/[borderWidth] default
 * to Uber's 1px inside-aligned border when unset.
 *
 * @param modifier Modifier for the avatar
 * @param size Avatar size (Default: [SizeVariant.Medium], 48dp)
 * @param shape Container shape (Default: [AvatarShape.Circle] — see [AvatarShape])
 * @param isLoading Whether to render the preloading [SkeletonCircle] (Default: false)
 * @param enabled Whether the avatar is interactive/full-opacity (Default: true)
 * @param imageUrl URL for a remote photo (Coil)
 * @param imageModel Alternative Coil image model
 * @param text Name used to derive initials (capped per size tier) and the first-name-only accessibility label
 * @param icon Icon fallback painter (ignored at sizes where icon fallback isn't supported — see [getAvatarConfig])
 * @param backgroundContent Vector/illustration fallback rendered behind initials (Uber Base's "Vector" content variant)
 * @param placeholder Placeholder shown while the image loads
 * @param backgroundColor Background color (defaults to theme token)
 * @param contentColor Content color for text/icon (defaults to theme token)
 * @param borderColor Border color (defaults to Uber's 1px inside-aligned border token when unset)
 * @param borderWidth Border width (Default: [HierarchicalSize.Border.Compact], 1dp)
 * @param onClick Optional click handler — enables hover/press/focus states
 * @param statusBadge Optional primary badge overlay
 * @param statusPosition Position of [statusBadge]
 * @param secondaryStatusBadge Optional second badge overlay (max 2 total, per Uber Base)
 * @param secondaryStatusPosition Position of [secondaryStatusBadge]
 * @param contentDescription Accessibility description (defaults to a first-name-only label per Uber Base's privacy guidance)
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
 * Data class for avatar configuration in groups
 *
 * @param imageUrl URL for remote image
 * @param imageModel Alternative image model for Coil
 * @param text Name used to derive initials
 * @param icon Icon painter to display
 * @param backgroundColor Background color (overrides [useHashColor])
 * @param contentColor Content color for text/icon
 * @param useHashColor Diversify the background color by rotating through tokenized semantic color groups, keyed off [text] — see [avatarHashColorFor]
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
