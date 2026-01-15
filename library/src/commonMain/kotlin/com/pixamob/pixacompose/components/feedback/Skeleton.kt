package com.pixamob.pixacompose.components.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.*
import com.valentinilk.shimmer.shimmer

/**
 * Skeleton Component
 *
 * Placeholder for content while loading with shimmer effect.
 * Provides various layout presets, customizable shimmer animations,
 * and dynamic grid/list layouts for different use cases.
 *
 * Features:
 * - Multiple shape variants (Rectangle, Circle, Text, Card, List, Grid, etc.)
 * - Configurable shimmer animation (speed, direction)
 * - Preset layouts for common UI patterns
 * - Custom skeleton builder for complex layouts
 * - Responsive grid layouts
 * - Full accessibility support (invisible to screen readers)
 * - Theme-aware colors and sizes
 *
 * @sample
 * ```
 * // Simple text skeleton
 * SkeletonText()
 *
 * // Card with image
 * SkeletonCard(
 *     showImage = true,
 *     imageShape = SkeletonImageShape.Rectangle
 * )
 *
 * // Custom skeleton composition
 * SkeletonCustom { modifier ->
 *     Row(modifier = modifier) {
 *         SkeletonCircle(size = 40.dp)
 *         Spacer(Modifier.width(12.dp))
 *         Column {
 *             SkeletonText(width = 120.dp)
 *             SkeletonText(width = 80.dp, size = SkeletonSize.Small)
 *         }
 *     }
 * }
 * ```
 */

// ============================================================================
// CONFIGURATION
// ============================================================================


/**
 * Skeleton size presets
 */
enum class SkeletonSize {
    Small,
    Medium,
    Large,
    ExtraLarge
}

/**
 * Shimmer animation direction
 */
enum class ShimmerDirection {
    /** Left to right shimmer */
    Horizontal,
    /** Top to bottom shimmer */
    Vertical
}

/**
 * Image skeleton shape
 */
enum class SkeletonImageShape {
    /** Rectangular image */
    Rectangle,
    /** Circular image (avatar-style) */
    Circle
}

/**
 * Skeleton configuration
 */
@Immutable
@Stable
data class SkeletonConfig(
    val height: Dp,
    val width: Dp? = null,
    val cornerRadius: Dp = RadiusSize.Medium,
    val shimmerEnabled: Boolean = true,
    val shimmerDurationMillis: Int = 1500,
    val shimmerDirection: ShimmerDirection = ShimmerDirection.Horizontal
)

/**
 * Get skeleton configuration based on size
 */
@Composable
private fun getSkeletonConfig(size: SkeletonSize): SkeletonConfig {
    return when (size) {
        SkeletonSize.Small -> SkeletonConfig(
            height = 16.dp,
            cornerRadius = RadiusSize.Small
        )
        SkeletonSize.Medium -> SkeletonConfig(
            height = 24.dp,
            cornerRadius = RadiusSize.Medium
        )
        SkeletonSize.Large -> SkeletonConfig(
            height = 48.dp,
            cornerRadius = RadiusSize.Medium
        )
        SkeletonSize.ExtraLarge -> SkeletonConfig(
            height = 96.dp, // Mobile-optimized (was 120dp)
            cornerRadius = RadiusSize.Large
        )
    }
}

// ============================================================================
// BASE COMPONENTS
// ============================================================================

/**
 * Base Skeleton composable
 *
 * @param modifier Modifier for the skeleton
 * @param width Width of the skeleton (null for fillMaxWidth)
 * @param height Height of the skeleton
 * @param shape Shape of the skeleton
 * @param shimmerEnabled Whether to show shimmer animation
 * @param baseColor Base color of the skeleton
 */
@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp = 24.dp,
    shape: Shape = RoundedCornerShape(RadiusSize.Medium),
    shimmerEnabled: Boolean = true,
    baseColor: Color = AppTheme.colors.baseSurfaceSubtle
) {
    Box(
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
            .then(
                if (shimmerEnabled) {
                    Modifier.shimmer()
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
    size: Dp = 48.dp,
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
    size: SkeletonSize = SkeletonSize.Medium,
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
    height: Dp = 200.dp,
    shape: SkeletonImageShape = SkeletonImageShape.Rectangle,
    shimmerEnabled: Boolean = true
) {
    val imageShape = when (shape) {
        SkeletonImageShape.Circle -> CircleShape
        SkeletonImageShape.Rectangle -> RoundedCornerShape(RadiusSize.Medium)
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
    width: Dp? = 120.dp,
    height: Dp = 44.dp,
    withIcon: Boolean = false,
    shimmerEnabled: Boolean = true
) {
    Row(
        modifier = modifier
            .then(
                if (width != null) {
                    Modifier.width(width)
                } else {
                    Modifier.fillMaxWidth()
                }
            )
            .height(height)
            .clip(RoundedCornerShape(RadiusSize.Medium))
            .then(
                if (shimmerEnabled) {
                    Modifier.shimmer()
                } else {
                    Modifier
                }
            )
            .background(AppTheme.colors.baseSurfaceSubtle)
            .padding(horizontal = Spacing.Medium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (withIcon) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.baseSurfaceDefault)
            )
            Spacer(modifier = Modifier.width(Spacing.Small))
        }
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(RadiusSize.Small))
                .background(AppTheme.colors.baseSurfaceDefault)
        )
    }
}

/**
 * Card Skeleton - Simulates a card with image and text content
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
    imageHeight: Dp = 200.dp,
    textLines: Int = 3,
    lastLineFraction: Float = 0.6f,
    shimmerEnabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { hideFromAccessibility() }
            .then(
                if (shimmerEnabled) {
                    Modifier.shimmer()
                } else {
                    Modifier
                }
            )
            .background(
                AppTheme.colors.baseSurfaceDefault,
                RoundedCornerShape(RadiusSize.Large)
            )
            .clip(RoundedCornerShape(RadiusSize.Large))
    ) {
        if (showImage) {
            val shape = when (imageShape) {
                SkeletonImageShape.Circle -> CircleShape
                SkeletonImageShape.Rectangle -> RoundedCornerShape(RadiusSize.Large)
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
            modifier = Modifier.padding(Inset.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            repeat(textLines) { index ->
                val width = when {
                    index == textLines - 1 -> lastLineFraction
                    else -> 1f
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(width)
                        .height(16.dp)
                        .background(
                            AppTheme.colors.baseSurfaceSubtle,
                            RoundedCornerShape(RadiusSize.Small)
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
    avatarSize: Dp = 48.dp,
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
                        Modifier.shimmer()
                    } else {
                        Modifier
                    }
                )
                .padding(Inset.Medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showAvatar) {
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(AppTheme.colors.baseSurfaceSubtle)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.Small)
            ) {
                repeat(textLines) { index ->
                    val width = when {
                        index == 0 -> 0.7f // First line
                        else -> 0.5f // Secondary lines
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(width)
                            .height(if (index == 0) 18.dp else 14.dp)
                            .background(
                                AppTheme.colors.baseSurfaceSubtle,
                                RoundedCornerShape(RadiusSize.Small)
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
                    .height(1.dp)
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
    avatarSize: Dp = 40.dp,
    textLines: Int = 2,
    shimmerEnabled: Boolean = true
) {
    Row(
        modifier = modifier
            .then(
                if (shimmerEnabled) {
                    Modifier.shimmer()
                } else {
                    Modifier
                }
            ),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(AppTheme.colors.baseSurfaceSubtle)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.Tiny)
        ) {
            repeat(textLines) { index ->
                Box(
                    modifier = Modifier
                        .width(if (index == 0) 100.dp else 80.dp)
                        .height(if (index == 0) 16.dp else 12.dp)
                        .background(
                            AppTheme.colors.baseSurfaceSubtle,
                            RoundedCornerShape(RadiusSize.Small)
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
    itemHeight: Dp = 120.dp,
    itemShape: Shape = RoundedCornerShape(RadiusSize.Medium),
    horizontalSpacing: Dp = Spacing.Medium,
    verticalSpacing: Dp = Spacing.Medium,
    shimmerEnabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { hideFromAccessibility() }
            .then(
                if (shimmerEnabled) {
                    Modifier.shimmer()
                } else {
                    Modifier
                }
            ),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        repeat(rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                repeat(columns) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(itemHeight)
                            .clip(itemShape)
                            .background(AppTheme.colors.baseSurfaceSubtle)
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
    itemSpacing: Dp = if (showSeparators) 0.dp else Spacing.Tiny,
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
 *             SkeletonText(width = 100.dp, size = SkeletonSize.Small)
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
                    Modifier.shimmer()
                } else {
                    Modifier
                }
            )
    ) {
        content(Modifier)
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
 *     horizontalSpacing = Spacing.Small,
 *     verticalSpacing = Spacing.Small
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
 *             SkeletonText(size = SkeletonSize.Large)
 *             SkeletonText(width = 200.dp, size = SkeletonSize.Small)
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
 *             SkeletonText(width = 150.dp, size = SkeletonSize.Large)
 *             SkeletonText(width = 100.dp, size = SkeletonSize.Small)
 *         }
 *     }
 *     SkeletonList(itemCount = 3, showAvatar = false)
 * }
 * ```
 */
