package com.pixamob.pixacompose.components.feedback

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.QuinticEaseInOutEasing
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.ShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import com.valentinilk.shimmer.shimmerSpec

/**
 * PixaSkeleton family — a shimmering placeholder that takes the place of content before it appears.
 *
 * ### Purpose
 * Reduces perceived wait time on first loads, especially when parts of the
 * screen are already cached. Per the spec, prefer a progress indicator
 * instead when the wait is triggered *by a user action* rather than an
 * initial/first-time load.
 *
 * ### Anatomy
 * Every placeholder block is a container + a shimmering gradient fill, both
 * required — this file's base [Skeleton] composable is the single place that
 * anatomy is assembled (border, background, clip, shimmer); every other
 * composable in this file builds on it rather than re-drawing that anatomy
 * itself.
 *
 * ### Variants
 * Layout presets: [SkeletonText], [SkeletonCircle], [SkeletonImage],
 * [SkeletonButton], [SkeletonCard], [SkeletonListItem]/[SkeletonList],
 * [SkeletonAvatarWithText], [SkeletonGrid], and [SkeletonCustom] for
 * arbitrary compositions.
 *
 * ### States
 * The spec names exactly 3: Loading (the shimmer itself), content-loaded
 * (see [SkeletonCrossfade] for the spec's 500ms linear fade transition), and
 * failure — "the component used for the error message can vary by context,"
 * which this library leaves to the caller (e.g. pairing with
 * `PixaEmptyState`) rather than hard-wiring a dependency from this file.
 *
 * ### Sizing
 * [getSkeletonConfig] resolves line-height/corner-radius by [SizeVariant].
 *
 * ### Motion
 * The shimmer sweep matches the spec's exact motion: 1000ms duration, 0ms
 * delay, 45° (upper-left to lower-right), [QuinticEaseInOutEasing], and a
 * ~40%-wide bright band within the gradient (approximated via
 * [shaderColorStops][ShimmerTheme.shaderColorStops] — see [rememberPixaShimmer]).
 * The library's own overshoot/travel-distance internals aren't independently
 * configurable to the spec's literal "-0.2 to 1.2" bounds without patching
 * the shimmer dependency itself, so that part is an accepted approximation.
 *
 * ### Accessibility
 * Every top-level composable defaults to `hideFromAccessibility()`
 * (decorative), matching most real-world use where a single "Loading"
 * announcement at the screen level is enough. Pass `contentDescription` to
 * have an individual placeholder announce a label instead (spec: "Loading
 * ETD" for individual elements, static-text "Loading" for a full page).
 *
 * ### Customization
 * [SkeletonConfig.shimmerDurationMillis]/[SkeletonConfig.shimmerDirection]
 * are exposed per-composable where relevant; border color/width are not
 * exposed per-instance (kept consistent library-wide via the same tokens
 * every other bordered surface uses).
 */

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Shimmer sweep angle. [Horizontal] is the spec's own default — "45° from
 * upper left to lower right." [Vertical] (90°) is a Pixa extension for
 * tall/portrait placeholder blocks where a diagonal sweep reads oddly.
 */
enum class ShimmerDirection {
    Horizontal,
    Vertical
}

enum class SkeletonImageShape {
    Rectangle,
    Circle
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════
@Immutable
@Stable
data class SkeletonConfig(
    val height: Dp,
    val width: Dp? = null,
    val cornerRadius: Dp = HierarchicalSize.Radius.Medium,
    val shimmerEnabled: Boolean = true,
    // Spec: "duration: 1000ms... delay: 0ms."
    val shimmerDurationMillis: Int = 1000,
    val shimmerDirection: ShimmerDirection = ShimmerDirection.Horizontal
)

/**
 * Get skeleton configuration based on size
 */
@Composable
private fun getSkeletonConfig(size: SizeVariant): SkeletonConfig {
    return when (size) {
        SizeVariant.Small, SizeVariant.Compact, SizeVariant.Nano -> SkeletonConfig(
            height = HierarchicalSize.Icon.Compact,
            cornerRadius = HierarchicalSize.Radius.Compact
        )
        SizeVariant.Medium -> SkeletonConfig(
            height = HierarchicalSize.Icon.Small,
            cornerRadius = HierarchicalSize.Radius.Medium
        )
        SizeVariant.Large, SizeVariant.Huge -> SkeletonConfig(
            height = HierarchicalSize.Icon.Medium,
            cornerRadius = HierarchicalSize.Radius.Medium
        )
        SizeVariant.Massive -> SkeletonConfig(
            height = HierarchicalSize.Container.Massive,
            cornerRadius = HierarchicalSize.Radius.Large
        )
        else -> SkeletonConfig(
            height = HierarchicalSize.Icon.Small,
            cornerRadius = HierarchicalSize.Radius.Medium
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Builds the spec-accurate shimmer motion: [durationMillis]/0ms delay,
 * [QuinticEaseInOutEasing], and a 45°/90° sweep angle from [direction].
 * [shaderColorStops] narrows the bright band to ~40% of the gradient width
 * (spec: "40% gradient highlight") instead of the shimmer library's default
 * full-width triangular fade; fill stays white per spec ("Fill color:
 * #FFFFFF") since [ShimmerTheme.blendMode] `DstIn` uses these as an alpha
 * mask over whatever `baseColor` the container already has.
 */
@Composable
private fun rememberPixaShimmer(durationMillis: Int, direction: ShimmerDirection): Shimmer {
    val rotation = when (direction) {
        ShimmerDirection.Horizontal -> 45f
        ShimmerDirection.Vertical -> 90f
    }
    val theme = remember(durationMillis, rotation) {
        ShimmerTheme(
            animationSpec = infiniteRepeatable(
                animation = shimmerSpec(
                    durationMillis = durationMillis,
                    delayMillis = 0,
                    easing = QuinticEaseInOutEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            blendMode = BlendMode.DstIn,
            rotation = rotation,
            shaderColors = listOf(
                Color.White.copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.25f),
                Color.White.copy(alpha = 1f),
                Color.White.copy(alpha = 1f),
                Color.White.copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.25f)
            ),
            shaderColorStops = listOf(0f, 0.3f, 0.4f, 0.6f, 0.7f, 1f),
            shimmerWidth = defaultShimmerTheme.shimmerWidth
        )
    }
    return rememberShimmer(ShimmerBounds.View, theme = theme)
}

// ============================================================================
// BASE COMPONENTS
// ============================================================================

/**
 * Base Skeleton composable — the anatomy every other composable in this file
 * builds on: container (background + 1dp inside-aligned border, per spec) +
 * shimmering gradient fill.
 *
 * @param modifier Modifier for the skeleton
 * @param width Width of the skeleton (null for fillMaxWidth)
 * @param height Height of the skeleton
 * @param shape Shape of the skeleton
 * @param shimmerEnabled Whether to show shimmer animation
 * @param shimmerDurationMillis Shimmer sweep duration (Default: spec's 1000ms)
 * @param shimmerDirection Shimmer sweep angle (Default: spec's 45°)
 * @param baseColor Base color of the skeleton
 * @param showBorder Whether to draw the spec-required 1dp container border
 * @param borderColor Border color (Default: [AppTheme.colors.baseBorderSubtle])
 * @param contentDescription Accessibility label; null (default) hides this element from accessibility as decorative
 */
@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp = HierarchicalSize.Icon.Small,
    shape: Shape = RoundedCornerShape(HierarchicalSize.Radius.Medium),
    shimmerEnabled: Boolean = true,
    shimmerDurationMillis: Int = 1000,
    shimmerDirection: ShimmerDirection = ShimmerDirection.Horizontal,
    baseColor: Color = AppTheme.colors.baseSurfaceDefault,
    showBorder: Boolean = true,
    borderColor: Color = AppTheme.colors.baseBorderSubtle,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .semantics {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                } else {
                    hideFromAccessibility()
                }
            }
            .then(
                if (width != null) {
                    Modifier.width(width)
                } else {
                    Modifier.fillMaxWidth()
                }
            )
            .height(height)
            .clip(shape)
            .then(
                if (showBorder) {
                    Modifier.border(HierarchicalSize.Border.Compact, borderColor, shape)
                } else Modifier
            )
            .then(
                if (shimmerEnabled) {
                    Modifier.shimmer(rememberPixaShimmer(shimmerDurationMillis, shimmerDirection))
                } else {
                    Modifier
                }
            )
            .background(baseColor)
    )
}

/**
 * Circular Skeleton
 *
 * @param size Diameter of the circle
 * @param modifier Modifier for the skeleton
 * @param shimmerEnabled Whether to show shimmer animation
 */
@Composable
fun SkeletonCircle(
    size: Dp = HierarchicalSize.Container.Medium,
    modifier: Modifier = Modifier,
    shimmerEnabled: Boolean = true
) {
    Skeleton(
        modifier = modifier,
        width = size,
        height = size,
        shape = CircleShape,
        shimmerEnabled = shimmerEnabled
    )
}

/**
 * Text Line Skeleton
 *
 * @param modifier Modifier for the skeleton
 * @param width Width of the text line (null for fillMaxWidth)
 * @param size Size preset for the text line
 * @param shimmerEnabled Whether to show shimmer animation
 */
@Composable
fun SkeletonText(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    size: SizeVariant = SizeVariant.Medium,
    shimmerEnabled: Boolean = true
) {
    val config = getSkeletonConfig(size)
    Skeleton(
        modifier = modifier,
        width = width,
        height = config.height,
        shape = RoundedCornerShape(config.cornerRadius),
        shimmerEnabled = shimmerEnabled
    )
}

/**
 * Image Skeleton - Simulates an image placeholder
 *
 * @param modifier Modifier for the skeleton
 * @param width Width of the image (null for fillMaxWidth)
 * @param height Height of the image
 * @param shape Shape of the image (Rectangle or Circle)
 * @param shimmerEnabled Whether to show shimmer animation
 */
@Composable
fun SkeletonImage(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp = HierarchicalSize.Image.Large,
    shape: SkeletonImageShape = SkeletonImageShape.Rectangle,
    shimmerEnabled: Boolean = true
) {
    val imageShape = when (shape) {
        SkeletonImageShape.Circle -> CircleShape
        SkeletonImageShape.Rectangle -> RoundedCornerShape(HierarchicalSize.Radius.Medium)
    }

    Skeleton(
        modifier = modifier,
        width = width,
        height = height,
        shape = imageShape,
        shimmerEnabled = shimmerEnabled
    )
}

/**
 * Button Skeleton - Simulates a button placeholder
 *
 * @param modifier Modifier for the skeleton
 * @param width Width of the button (null for fillMaxWidth)
 * @param height Height of the button
 * @param withIcon Whether to show icon placeholder
 * @param shimmerEnabled Whether to show shimmer animation
 */
@Composable
fun SkeletonButton(
    modifier: Modifier = Modifier,
    width: Dp? = HierarchicalSize.Icon.Small,
    height: Dp = HierarchicalSize.Container.Medium,
    withIcon: Boolean = false,
    shimmerEnabled: Boolean = true
) {
    val shape = RoundedCornerShape(HierarchicalSize.Radius.Medium)

    Row(
        modifier = modifier
            .semantics { hideFromAccessibility() }
            .then(
                if (width != null) {
                    Modifier.width(width)
                } else {
                    Modifier.fillMaxWidth()
                }
            )
            .height(height)
            .clip(shape)
            .border(HierarchicalSize.Border.Compact, AppTheme.colors.baseBorderSubtle, shape)
            .then(
                if (shimmerEnabled) {
                    Modifier.shimmer(rememberPixaShimmer(1000, ShimmerDirection.Horizontal))
                } else {
                    Modifier
                }
            )
            .background(AppTheme.colors.baseSurfaceSubtle)
            .padding(horizontal = HierarchicalSize.Spacing.Medium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (withIcon) {
            // Pre-existing bug fixed: this previously sized the icon-dot to
            // `Border.Compact` (1dp) — a border-width token, not an icon
            // size — which rendered an invisible dot instead of a visible
            // icon placeholder.
            Box(
                modifier = Modifier
                    .size(HierarchicalSize.Icon.Compact)
                    .clip(CircleShape)
                    .background(AppTheme.colors.baseSurfaceDefault)
            )
            Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
        }
        Box(
            modifier = Modifier
                .width(HierarchicalSize.Icon.Small)
                .height(HierarchicalSize.Icon.Compact)
                .clip(RoundedCornerShape(HierarchicalSize.Radius.Compact))
                .background(AppTheme.colors.baseSurfaceDefault)
        )
    }
}

/**
 * Card Skeleton - Simulates a card with image and text content
 * Note: The skeleton inherits the parent card's shape (HierarchicalSize.Radius.Large)
 *
 * @param modifier Modifier for the skeleton
 * @param showImage Whether to show image placeholder
 * @param imageShape Shape of the image (Rectangle or Circle)
 * @param imageHeight Height of the image placeholder
 * @param textLines Number of text lines to show
 * @param lastLineFraction Width fraction of the last text line (0.0-1.0)
 * @param shimmerEnabled Whether to show shimmer animation
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    showImage: Boolean = true,
    imageShape: SkeletonImageShape = SkeletonImageShape.Rectangle,
    imageHeight: Dp = HierarchicalSize.Image.Large,
    textLines: Int = 3,
    lastLineFraction: Float = 0.6f,
    shimmerEnabled: Boolean = true
) {
    val cardShape = RoundedCornerShape(HierarchicalSize.Radius.Large)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { hideFromAccessibility() }
            .clip(cardShape)
            .border(HierarchicalSize.Border.Compact, AppTheme.colors.baseBorderSubtle, cardShape)
            .then(
                if (shimmerEnabled) {
                    Modifier.shimmer(rememberPixaShimmer(1000, ShimmerDirection.Horizontal))
                } else {
                    Modifier
                }
            )
            .background(AppTheme.colors.baseSurfaceDefault, cardShape)
    ) {
        if (showImage) {
            val shape = when (imageShape) {
                SkeletonImageShape.Circle -> CircleShape
                SkeletonImageShape.Rectangle -> cardShape
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .clip(shape)
                    .background(AppTheme.colors.baseSurfaceSubtle)
            )
        }

        Column(
            modifier = Modifier.padding(HierarchicalSize.Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
        ) {
            repeat(textLines) { index ->
                val width = when {
                    index == textLines - 1 -> lastLineFraction
                    else -> 1f
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(width)
                        .height(HierarchicalSize.Icon.Small)
                        .background(
                            AppTheme.colors.baseSurfaceSubtle,
                            RoundedCornerShape(HierarchicalSize.Radius.Small)
                        )
                )
            }
        }
    }
}

/**
 * List Item Skeleton - Simulates a list item with avatar and text
 *
 * @param modifier Modifier for the skeleton
 * @param showAvatar Whether to show avatar placeholder
 * @param avatarSize Size of the avatar
 * @param textLines Number of text lines to show
 * @param showSeparator Whether to show separator at bottom
 * @param shimmerEnabled Whether to show shimmer animation
 */
@Composable
fun SkeletonListItem(
    modifier: Modifier = Modifier,
    showAvatar: Boolean = true,
    avatarSize: Dp = HierarchicalSize.Avatar.Medium,
    textLines: Int = 2,
    showSeparator: Boolean = false,
    shimmerEnabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { hideFromAccessibility() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (shimmerEnabled) {
                        Modifier.shimmer(rememberPixaShimmer(1000, ShimmerDirection.Horizontal))
                    } else {
                        Modifier
                    }
                )
                .padding(HierarchicalSize.Spacing.Medium),
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showAvatar) {
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .border(HierarchicalSize.Border.Compact, AppTheme.colors.baseBorderSubtle, CircleShape)
                        .background(AppTheme.colors.baseSurfaceSubtle)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
            ) {
                repeat(textLines) { index ->
                    val width = when {
                        index == 0 -> 0.7f // First line
                        else -> 0.5f // Secondary lines
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(width)
                            .height(if (index == 0) HierarchicalSize.Icon.Small else HierarchicalSize.Icon.Compact)
                            .background(
                                AppTheme.colors.baseSurfaceSubtle,
                                RoundedCornerShape(HierarchicalSize.Radius.Small)
                            )
                    )
                }
            }
        }

        // Separator
        if (showSeparator) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HierarchicalSize.Divider.Compact)
                    .background(AppTheme.colors.baseBorderSubtle)
            )
        }
    }
}

/**
 * Avatar with Text Skeleton - Simulates avatar with text beside it
 *
 * @param modifier Modifier for the skeleton
 * @param avatarSize Size of the avatar
 * @param textLines Number of text lines to show
 * @param shimmerEnabled Whether to show shimmer animation
 */
@Composable
fun SkeletonAvatarWithText(
    modifier: Modifier = Modifier,
    avatarSize: Dp = HierarchicalSize.Avatar.Small,
    textLines: Int = 2,
    shimmerEnabled: Boolean = true
) {
    Row(
        modifier = modifier
            .semantics { hideFromAccessibility() }
            .then(
                if (shimmerEnabled) {
                    Modifier.shimmer(rememberPixaShimmer(1000, ShimmerDirection.Horizontal))
                } else {
                    Modifier
                }
            ),
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .border(HierarchicalSize.Border.Compact, AppTheme.colors.baseBorderSubtle, CircleShape)
                .background(AppTheme.colors.baseSurfaceSubtle)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
        ) {
            repeat(textLines) { index ->
                Box(
                    modifier = Modifier
                        // Free-floating text-line widths have no matching
                        // HierarchicalSize category (they approximate content
                        // length, not a UI-chrome dimension) — kept as a
                        // one-off ladder, unchanged from the original.
                        .width(if (index == 0) 100.dp else 80.dp)
                        .height(if (index == 0) HierarchicalSize.Icon.Small else HierarchicalSize.Icon.Compact)
                        .background(
                            AppTheme.colors.baseSurfaceSubtle,
                            RoundedCornerShape(HierarchicalSize.Radius.Small)
                        )
                )
            }
        }
    }
}

/**
 * Grid Skeleton - Displays a grid of skeleton items
 *
 * @param modifier Modifier for the skeleton
 * @param columns Number of columns in the grid
 * @param rows Number of rows to show
 * @param itemHeight Height of each grid item
 * @param itemShape Shape of each grid item
 * @param horizontalSpacing Horizontal spacing between items
 * @param verticalSpacing Vertical spacing between rows
 * @param shimmerEnabled Whether to show shimmer animation
 */
@Composable
fun SkeletonGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    rows: Int = 3,
    itemHeight: Dp = HierarchicalSize.Image.Medium,
    itemShape: Shape = RoundedCornerShape(HierarchicalSize.Radius.Medium),
    horizontalSpacing: Dp = HierarchicalSize.Spacing.Medium,
    verticalSpacing: Dp = HierarchicalSize.Spacing.Medium,
    shimmerEnabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { hideFromAccessibility() },
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        repeat(rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                repeat(columns) {
                    Skeleton(
                        modifier = Modifier.weight(1f),
                        height = itemHeight,
                        shape = itemShape,
                        shimmerEnabled = shimmerEnabled
                    )
                }
            }
        }
    }
}

/**
 * List Skeleton - Displays a list of skeleton items
 *
 * @param modifier Modifier for the skeleton
 * @param itemCount Number of items to show
 * @param showAvatar Whether to show avatar in each item
 * @param showSeparators Whether to show separators between items
 * @param itemSpacing Spacing between list items (if no separators)
 * @param shimmerEnabled Whether to show shimmer animation
 */
@Composable
fun SkeletonList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5,
    showAvatar: Boolean = true,
    showSeparators: Boolean = false,
    itemSpacing: Dp = if (showSeparators) HierarchicalSize.Spacing.None else HierarchicalSize.Spacing.Compact,
    shimmerEnabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { hideFromAccessibility() },
        verticalArrangement = if (showSeparators) {
            Arrangement.Top
        } else {
            Arrangement.spacedBy(itemSpacing)
        }
    ) {
        repeat(itemCount) { index ->
            SkeletonListItem(
                showAvatar = showAvatar,
                showSeparator = showSeparators && index < itemCount - 1,
                shimmerEnabled = shimmerEnabled
            )
        }
    }
}

/**
 * Custom Skeleton - Build complex skeleton layouts with a composable builder
 *
 * @param modifier Modifier for the skeleton
 * @param shimmerEnabled Whether to show shimmer animation for the entire custom skeleton
 * @param content Composable builder that receives a Modifier for shimmer application
 *
 * @sample
 * ```
 * SkeletonCustom { modifier ->
 *     Row(modifier = modifier.padding(16.dp)) {
 *         SkeletonCircle(size = 48.dp)
 *         Spacer(Modifier.width(12.dp))
 *         Column {
 *             SkeletonText(width = 150.dp)
 *             SkeletonText(width = 100.dp, size = SizeVariant.Small)
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun SkeletonCustom(
    modifier: Modifier = Modifier,
    shimmerEnabled: Boolean = true,
    content: @Composable (Modifier) -> Unit
) {
    Box(
        modifier = modifier
            .semantics { hideFromAccessibility() }
            .then(
                if (shimmerEnabled) {
                    Modifier.shimmer(rememberPixaShimmer(1000, ShimmerDirection.Horizontal))
                } else {
                    Modifier
                }
            )
    ) {
        content(Modifier)
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Crossfades between a loading skeleton and its final content — the spec's
 * "content loaded: placeholder fades out as content appears," at the exact
 * motion it specifies: 500ms, linear easing, no delay.
 *
 * @param loading Whether the skeleton (true) or [content] (false) is shown
 * @param modifier Modifier for the crossfade container
 * @param skeleton The loading placeholder, e.g. [SkeletonCard]/[SkeletonList]
 * @param content The real content shown once loading completes
 */
@Composable
fun SkeletonCrossfade(
    loading: Boolean,
    modifier: Modifier = Modifier,
    skeleton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Crossfade(
        targetState = loading,
        modifier = modifier,
        animationSpec = AnimationUtils.standardTween(
            durationMillis = 500,
            easing = LinearEasing
        )
    ) { isLoading ->
        if (isLoading) skeleton() else content()
    }
}

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * 1. Simple text skeleton:
 * ```
 * SkeletonText()
 * ```
 *
 * 2. Circle skeleton for avatar:
 * ```
 * SkeletonCircle(size = 48.dp)
 * ```
 *
 * 3. Image skeleton with custom shape:
 * ```
 * SkeletonImage(
 *     height = 200.dp,
 *     shape = SkeletonImageShape.Rectangle
 * )
 * ```
 *
 * 4. Button skeleton with icon:
 * ```
 * SkeletonButton(
 *     width = 150.dp,
 *     withIcon = true
 * )
 * ```
 *
 * 5. Card skeleton with image:
 * ```
 * SkeletonCard(
 *     showImage = true,
 *     imageShape = SkeletonImageShape.Circle,
 *     imageHeight = 180.dp,
 *     textLines = 3,
 *     lastLineFraction = 0.7f
 * )
 * ```
 *
 * 6. List skeleton with separators:
 * ```
 * SkeletonList(
 *     itemCount = 5,
 *     showAvatar = true,
 *     showSeparators = true
 * )
 * ```
 *
 * 7. Grid skeleton for image gallery:
 * ```
 * SkeletonGrid(
 *     columns = 3,
 *     rows = 4,
 *     itemHeight = 120.dp,
 *     horizontalSpacing = HierarchicalSize.Spacing.Small,
 *     verticalSpacing = HierarchicalSize.Spacing.Small
 * )
 * ```
 *
 * 8. Custom skeleton composition:
 * ```
 * SkeletonCustom { modifier ->
 *     Row(
 *         modifier = modifier.fillMaxWidth().padding(16.dp),
 *         horizontalArrangement = Arrangement.spacedBy(12.dp)
 *     ) {
 *         SkeletonCircle(size = 48.dp)
 *         Column(
 *             modifier = Modifier.weight(1f),
 *             verticalArrangement = Arrangement.spacedBy(8.dp)
 *         ) {
 *             SkeletonText(size = SizeVariant.Large)
 *             SkeletonText(width = 200.dp, size = SizeVariant.Small)
 *         }
 *     }
 * }
 * ```
 *
 * 9. Avatar with text skeleton:
 * ```
 * SkeletonAvatarWithText(
 *     avatarSize = 40.dp,
 *     textLines = 2
 * )
 * ```
 *
 * 10. Static skeleton (no shimmer):
 * ```
 * SkeletonCard(
 *     showImage = false,
 *     textLines = 2,
 *     shimmerEnabled = false
 * )
 * ```
 *
 * 11. Profile page skeleton:
 * ```
 * Column {
 *     SkeletonCustom { modifier ->
 *         Column(
 *             modifier = modifier.fillMaxWidth().padding(16.dp),
 *             horizontalAlignment = Alignment.CenterHorizontally
 *         ) {
 *             SkeletonCircle(size = 80.dp)
 *             Spacer(Modifier.height(12.dp))
 *             SkeletonText(width = 150.dp, size = SizeVariant.Large)
 *             SkeletonText(width = 100.dp, size = SizeVariant.Small)
 *         }
 *     }
 *     SkeletonList(itemCount = 3, showAvatar = false)
 * }
 * ```
 *
 * 12. Skeleton-to-content crossfade (spec's 500ms linear fade):
 * ```
 * SkeletonCrossfade(
 *     loading = viewModel.isLoading,
 *     skeleton = { SkeletonCard() },
 *     content = { RealCardContent(viewModel.data) }
 * )
 * ```
 */
