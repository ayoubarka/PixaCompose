package com.pixamob.pixacompose.components.display

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.feedback.BadgeStyle
import com.pixamob.pixacompose.components.feedback.BadgeVariant
import com.pixamob.pixacompose.components.feedback.PixaBadge
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.*

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Variants for BaseCard appearance
 */
enum class BaseCardVariant {
    /** Elevated card with shadow - DEFAULT (best for content separation) */
    Elevated,

    /** Card with border, no shadow (subtle, good for light backgrounds) */
    Outlined,

    /** Card with background color fill, no shadow or border (minimal) */
    Filled,

    /** Transparent card with subtle border (for overlays or grouped content) */
    Ghost
}

/**
 * BaseCard elevation levels for shadow depth
 */
enum class BaseCardElevation {
    /** No elevation (flat) */
    None,

    /** Subtle elevation - 1dp */
    Low,

    /** Standard elevation - 2dp (DEFAULT) */
    Medium,

    /** Prominent elevation - 4dp */
    High,

    /** Very prominent elevation - 8dp */
    Highest
}

/**
 * BaseCard padding presets
 */
enum class BaseCardPadding {
    /** No padding */
    None,

    /** Compact padding - 8dp */
    Compact,

    /** Standard padding - 12dp */
    Small,

    /** Default padding - 16dp (DEFAULT) */
    Medium,

    /** Large padding - 20dp */
    Large,

    /** Extra large padding - 24dp */
    ExtraLarge
}

/**
 * Colors for base card states
 */
@Stable
data class BaseCardColors(
    val background: Color,
    val border: Color = Color.Transparent,
    val content: Color
)

/**
 * Border configuration for cards
 */
@Stable
data class CardBorderConfig(
    /** Border color (null = use theme default based on variant) */
    val color: Color? = null,
    /** Border width */
    val width: Dp = BorderSize.Standard,
    /** Border style - solid or dashed pattern */
    val style: CardBorderStyle = CardBorderStyle.Solid
)

/**
 * Border style options
 */
enum class CardBorderStyle {
    /** Solid continuous border */
    Solid,
    /** Dashed border pattern */
    Dashed,
    /** Dotted border pattern */
    Dotted,
    /** No border */
    None
}

/**
 * Shadow configuration for cards
 */
@Stable
data class CardShadowConfig(
    /** Shadow elevation */
    val elevation: Dp = 0.dp,
    /** Shadow color */
    val color: Color = Color.Black.copy(alpha = 0.1f),
    /** Shadow spread radius */
    val spreadRadius: Dp = 0.dp,
    /** Shadow blur radius */
    val blurRadius: Dp = 0.dp,
    /** Shadow X offset */
    val offsetX: Dp = 0.dp,
    /** Shadow Y offset */
    val offsetY: Dp = 0.dp
)

/**
 * State-based colors for base card
 */
@Stable
data class BaseCardStateColors(
    val default: BaseCardColors,
    val hover: BaseCardColors = default,
    val pressed: BaseCardColors = default,
    val disabled: BaseCardColors
)

// ============================================================================
// THEME PROVIDER
// ============================================================================

@Composable
private fun getBaseCardTheme(
    variant: BaseCardVariant,
    colors: ColorPalette
): BaseCardStateColors {
    return when (variant) {
        BaseCardVariant.Elevated -> BaseCardStateColors(
            default = BaseCardColors(
                background = colors.baseSurfaceDefault,
                content = colors.baseContentBody
            ),
            hover = BaseCardColors(
                background = colors.baseSurfaceSubtle,
                content = colors.baseContentBody
            ),
            pressed = BaseCardColors(
                background = colors.baseSurfaceSubtle.copy(alpha = 0.8f),
                content = colors.baseContentBody
            ),
            disabled = BaseCardColors(
                background = colors.baseSurfaceDisabled,
                content = colors.baseContentDisabled
            )
        )

        BaseCardVariant.Outlined -> BaseCardStateColors(
            default = BaseCardColors(
                background = colors.baseSurfaceDefault,
                border = colors.baseBorderDefault,
                content = colors.baseContentBody
            ),
            hover = BaseCardColors(
                background = colors.baseSurfaceSubtle,
                border = colors.baseBorderFocus,
                content = colors.baseContentBody
            ),
            pressed = BaseCardColors(
                background = colors.baseSurfaceSubtle.copy(alpha = 0.8f),
                border = colors.baseBorderFocus,
                content = colors.baseContentBody
            ),
            disabled = BaseCardColors(
                background = colors.baseSurfaceDisabled,
                border = colors.baseBorderDisabled,
                content = colors.baseContentDisabled
            )
        )

        BaseCardVariant.Filled -> BaseCardStateColors(
            default = BaseCardColors(
                background = colors.baseSurfaceSubtle,
                content = colors.baseContentBody
            ),
            hover = BaseCardColors(
                background = colors.baseSurfaceDefault,
                content = colors.baseContentBody
            ),
            pressed = BaseCardColors(
                background = colors.baseSurfaceDefault.copy(alpha = 0.8f),
                content = colors.baseContentBody
            ),
            disabled = BaseCardColors(
                background = colors.baseSurfaceDisabled,
                content = colors.baseContentDisabled
            )
        )

        BaseCardVariant.Ghost -> BaseCardStateColors(
            default = BaseCardColors(
                background = Color.Transparent,
                border = colors.baseBorderSubtle,
                content = colors.baseContentBody
            ),
            hover = BaseCardColors(
                background = colors.baseSurfaceSubtle.copy(alpha = 0.5f),
                border = colors.baseBorderDefault,
                content = colors.baseContentBody
            ),
            pressed = BaseCardColors(
                background = colors.baseSurfaceSubtle.copy(alpha = 0.7f),
                border = colors.baseBorderDefault,
                content = colors.baseContentBody
            ),
            disabled = BaseCardColors(
                background = Color.Transparent,
                border = colors.baseBorderDisabled,
                content = colors.baseContentDisabled
            )
        )
    }
}

private fun getBaseCardElevationDp(elevation: BaseCardElevation): Dp {
    return when (elevation) {
        BaseCardElevation.None -> 0.dp
        BaseCardElevation.Low -> 1.dp
        BaseCardElevation.Medium -> 2.dp
        BaseCardElevation.High -> 4.dp
        BaseCardElevation.Highest -> 8.dp
    }
}

private fun getBaseCardPaddingDp(padding: BaseCardPadding): Dp {
    return when (padding) {
        BaseCardPadding.None -> Spacing.None
        BaseCardPadding.Compact -> Spacing.Tiny
        BaseCardPadding.Small -> HierarchicalSize.Spacing.Small
        BaseCardPadding.Medium -> HierarchicalSize.Spacing.Medium
        BaseCardPadding.Large -> HierarchicalSize.Spacing.Large
        BaseCardPadding.ExtraLarge -> HierarchicalSize.Spacing.Huge
    }
}

// ============================================================================
// INTERNAL CARD (Core logic)
// ============================================================================

@Composable
private fun InternalCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    elevation: BaseCardElevation = BaseCardElevation.Medium,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    colors: BaseCardStateColors,
    borderConfig: CardBorderConfig? = null,
    shadowConfig: CardShadowConfig? = null,
    content: @Composable ConstraintLayoutScope.() -> Unit
) {
    // Animated colors based on state with spring for snappier feel
    val backgroundColor by animateColorAsState(
        targetValue = if (!enabled) colors.disabled.background else colors.default.background,
        animationSpec = spring(),
        label = "card_background"
    )

    // Determine border color - priority: borderConfig > theme colors
    val themeBorderColor = if (!enabled) colors.disabled.border else colors.default.border
    val finalBorderColor = borderConfig?.color ?: themeBorderColor

    val borderColor by animateColorAsState(
        targetValue = finalBorderColor,
        animationSpec = spring(),
        label = "card_border"
    )

    // Determine elevation - priority: shadowConfig > elevation enum
    val elevationDp = when {
        shadowConfig != null -> shadowConfig.elevation
        variant == BaseCardVariant.Elevated && enabled -> getBaseCardElevationDp(elevation)
        else -> 0.dp
    }

    val paddingDp = getBaseCardPaddingDp(padding)
    val shape = RoundedCornerShape(cornerRadius)

    // Build border modifier based on border config
    val borderModifier = when {
        borderConfig?.style == CardBorderStyle.None -> Modifier
        borderConfig?.style == CardBorderStyle.Dashed || borderConfig?.style == CardBorderStyle.Dotted -> {
            // For dashed/dotted, we use a custom draw modifier
            Modifier.drawBehind {
                val strokeWidth = (borderConfig.width).toPx()
                val pathEffect = when (borderConfig.style) {
                    CardBorderStyle.Dashed -> PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    CardBorderStyle.Dotted -> PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                    else -> null
                }
                drawRoundRect(
                    color = borderColor,
                    style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
            }
        }
        borderColor != Color.Transparent -> {
            Modifier.border(
                width = borderConfig?.width ?: BorderSize.Standard,
                color = borderColor,
                shape = shape
            )
        }
        else -> Modifier
    }

    val clickableModifier = if (onClick != null && enabled) {
        Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    } else {
        Modifier
    }

    ConstraintLayout(
        modifier = modifier
            .semantics {
                if (onClick != null) {
                    role = Role.Button
                }
            }
            .shadow(
                elevation = elevationDp,
                shape = shape,
                clip = false,
                ambientColor = shadowConfig?.color ?: Color.Black.copy(alpha = 0.1f),
                spotColor = shadowConfig?.color ?: Color.Black.copy(alpha = 0.1f)
            )
            .clip(shape)
            .then(borderModifier)
            .background(backgroundColor)
            .then(clickableModifier)
            .padding(paddingDp)
    ) {
        content()

        // Disabled overlay (semi-transparent scrim)
        if (!enabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f))
            )
        }
    }
}

// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * BaseCard component for grouping related content
 *
 * Uses ConstraintLayout internally for flexible child positioning. You can use
 * createRefs() to define constraints for complex layouts like headers, footers,
 * or overlaid elements.
 *
 * @param modifier Modifier for the card
 * @param variant Visual style (Elevated, Outlined, Filled, Ghost)
 * @param onClick Optional click handler (makes card interactive)
 * @param enabled Whether card is enabled (affects colors and clickability)
 * @param elevation Shadow depth (only applies to Elevated variant)
 * @param padding Internal padding
 * @param cornerRadius Corner radius override (default: RadiusSize.Medium)
 * @param backgroundColor Optional background color override
 * @param borderConfig Optional border configuration (color, width, style)
 * @param shadowConfig Optional shadow configuration (elevation, color, offsets)
 * @param content Card content (ConstraintLayout scope for flexible positioning)
 *
 * @sample
 * ```
 * // Simple card with constrained content
 * PixaCard(
 *     variant = BaseCardVariant.Elevated,
 *     onClick = { /* handle click */ }
 * ) {
 *     val (title, description) = createRefs()
 *
 *     Text(
 *         text = "Card Title",
 *         style = AppTheme.typography.titleMedium,
 *         modifier = Modifier.constrainAs(title) {
 *             top.linkTo(parent.top)
 *             start.linkTo(parent.start)
 *         }
 *     )
 *
 *     Text(
 *         text = "Card content goes here",
 *         style = AppTheme.typography.bodyRegular,
 *         modifier = Modifier.constrainAs(description) {
 *             top.linkTo(title.bottom, margin = 8.dp)
 *             start.linkTo(parent.start)
 *         }
 *     )
 * }
 *
 * // Card with pinned footer
 * BaseCard {
 *     val (header, body, footer) = createRefs()
 *
 *     Text("Header", Modifier.constrainAs(header) {
 *         top.linkTo(parent.top)
 *     })
 *
 *     Text("Body", Modifier.constrainAs(body) {
 *         top.linkTo(header.bottom, margin = 8.dp)
 *     })
 *
 *     BaseButton(
 *         text = "Action",
 *         onClick = {},
 *         modifier = Modifier.constrainAs(footer) {
 *             bottom.linkTo(parent.bottom)
 *             end.linkTo(parent.end)
 *         }
 *     )
 * }
 * ```
 */
@Composable
fun PixaCard(
    modifier: Modifier = Modifier,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    elevation: BaseCardElevation = BaseCardElevation.Medium,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    backgroundColor: Color? = null,
    borderConfig: CardBorderConfig? = null,
    shadowConfig: CardShadowConfig? = null,
    skeletonShape: androidx.compose.ui.graphics.Shape? = null,
    content: @Composable ConstraintLayoutScope.() -> Unit
) {
    // Show skeleton when isLoading = true
    if (isLoading) {
        Skeleton(
            modifier = modifier.fillMaxWidth(),
            height = 120.dp, // Default card height
            shape = skeletonShape ?: RoundedCornerShape(cornerRadius),
            shimmerEnabled = true
        )
        return
    }

    val themeColors = getBaseCardTheme(variant, AppTheme.colors)

    // Override background color if provided
    val finalColors = if (backgroundColor != null) {
        BaseCardStateColors(
            default = themeColors.default.copy(background = backgroundColor),
            hover = themeColors.hover.copy(background = backgroundColor),
            pressed = themeColors.pressed.copy(background = backgroundColor),
            disabled = themeColors.disabled.copy(background = backgroundColor)
        )
    } else {
        themeColors
    }

    InternalCard(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        variant = variant,
        elevation = elevation,
        padding = padding,
        cornerRadius = cornerRadius,
        colors = finalColors,
        borderConfig = borderConfig,
        shadowConfig = shadowConfig,
        content = content
    )
}

// ============================================================================
// CONVENIENCE VARIANTS
// ============================================================================

/**
 * Elevated base card with shadow (DEFAULT style)
 * Best for: Content cards, product cards, list items
 */
@Composable
fun ElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    elevation: BaseCardElevation = BaseCardElevation.Medium,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    content: @Composable ConstraintLayoutScope.() -> Unit
) = PixaCard(
    modifier = modifier,
    variant = BaseCardVariant.Elevated,
    onClick = onClick,
    enabled = enabled,
    isLoading = isLoading,
    elevation = elevation,
    padding = padding,
    cornerRadius = cornerRadius,
    content = content
)

/**
 * Outlined base card with border
 * Best for: Forms, settings sections, subtle containers
 */
@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    content: @Composable ConstraintLayoutScope.() -> Unit
) = PixaCard(
    modifier = modifier,
    variant = BaseCardVariant.Outlined,
    onClick = onClick,
    enabled = enabled,
    isLoading = isLoading,
    padding = padding,
    cornerRadius = cornerRadius,
    content = content
)

/**
 * Filled base card with background color
 * Best for: Promotional banners, highlighted sections, info boxes
 */
@Composable
fun FilledCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    backgroundColor: Color? = null,
    content: @Composable ConstraintLayoutScope.() -> Unit
) = PixaCard(
    modifier = modifier,
    variant = BaseCardVariant.Filled,
    onClick = onClick,
    enabled = enabled,
    isLoading = isLoading,
    padding = padding,
    cornerRadius = cornerRadius,
    backgroundColor = backgroundColor,
    content = content
)

/**
 * Ghost base card with transparent background
 * Best for: Overlays, grouped content, minimal containers
 */
@Composable
fun GhostCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    content: @Composable ConstraintLayoutScope.() -> Unit
) = PixaCard(
    modifier = modifier,
    variant = BaseCardVariant.Ghost,
    onClick = onClick,
    enabled = enabled,
    padding = padding,
    cornerRadius = cornerRadius,
    content = content
)

/**
 * Interactive base card optimized for lists (full width, standard padding)
 * Best for: List items, feed items, selectable cards
 */
@Composable
fun InteractiveCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    enabled: Boolean = true,
    content: @Composable ConstraintLayoutScope.() -> Unit
) = PixaCard(
    modifier = modifier.fillMaxWidth(),
    variant = variant,
    onClick = onClick,
    enabled = enabled,
    padding = BaseCardPadding.Medium,
    content = content
)

/**
 * Compact base card with minimal padding
 * Best for: Dense layouts, small info boxes, chips-like cards
 */
@Composable
fun CompactCard(
    modifier: Modifier = Modifier,
    variant: BaseCardVariant = BaseCardVariant.Outlined,
    onClick: (() -> Unit)? = null,
    content: @Composable ConstraintLayoutScope.() -> Unit
) = PixaCard(
    modifier = modifier,
    variant = variant,
    onClick = onClick,
    padding = BaseCardPadding.Compact,
    content = content
)

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * 1. Basic elevated card:
 * ```
 * ElevatedBaseCard {
 *     Text("Simple Card")
 * }
 * ```
 *
 * 2. Interactive card with click:
 * ```
 * InteractiveCard(onClick = { /* navigate */ }) {
 *     Text("Tap me", style = AppTheme.typography.titleMedium)
 *     Spacer(modifier = Modifier.height(4.dp))
 *     Text("Card description", style = AppTheme.typography.bodySmall)
 * }
 * ```
 *
 * 3. Outlined form section:
 * ```
 * OutlinedCard(padding = BaseCardPadding.Large) {
 *     Text("Form Section", style = AppTheme.typography.titleLarge)
 *     Spacer(modifier = Modifier.height(16.dp))
 *     // Form fields here
 * }
 * ```
 *
 * 4. Filled promotional card:
 * ```
 * FilledCard(
 *     backgroundColor = AppTheme.colors.brandSurfaceSubtle,
 *     onClick = { /* action */ }
 * ) {
 *     Text("Special Offer!", style = AppTheme.typography.titleBold)
 *     Text("Get 20% off", style = AppTheme.typography.bodyRegular)
 * }
 * ```
 *
 * 5. Ghost card for overlay:
 * ```
 * GhostBaseCard {
 *     Icon(painter = painterResource(R.drawable.ic_info), contentDescription = null)
 *     Text("Info bubble")
 * }
 * ```
 *
 * 6. Compact card for dense layout:
 * ```
 * CompactCard(variant = BaseCardVariant.Filled) {
 *     Text("Tag: Kotlin", style = AppTheme.typography.captionRegular)
 * }
 * ```
 *
 * 7. High elevation card:
 * ```
 * PixaCard(
 *     variant = BaseCardVariant.Elevated,
 *     elevation = BaseCardElevation.Highest
 * ) {
 *     Text("Prominent card")
 * }
 * ```
 *
 * ```
 *
 * 8. Disabled card:
 * ```
 * Card(enabled = false) {
 *     Text("Unavailable content")
 * }
 * ```
 */

// ============================================================================
// SPECIALIZED CARD VARIANTS
// ============================================================================

/**
 * ProductCard - Display product with image, title, price, rating
 *
 * @param imageUrl Product image URL
 * @param title Product name
 * @param price Product price string
 * @param rating Optional rating (0.0 to 5.0)
 * @param badgeText Optional badge text (e.g., "Sale", "New")
 * @param onClick Click handler
 * @param modifier Modifier
 * @param variant Card variant style
 * @param isLoading Loading state
 */
@Composable
fun ProductCard(
    imageUrl: String,
    title: String,
    price: String,
    modifier: Modifier = Modifier,
    rating: Float? = null,
    badgeText: String? = null,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        onClick = onClick,
        isLoading = isLoading,
        padding = BaseCardPadding.None
    ) {
        val (image, badge, content) = createRefs()

        // Product Image
        PixaImage(
            url = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Crop,
            shape = RoundedCornerShape(
                topStart = RadiusSize.Medium,
                topEnd = RadiusSize.Medium
            )
        )

        // Badge overlay
        if (badgeText != null) {
            PixaBadge(
                content = badgeText,
                variant = BadgeVariant.Error,
                style = BadgeStyle.Solid,
                modifier = Modifier.constrainAs(badge) {
                    top.linkTo(image.top, margin = HierarchicalSize.Spacing.Small)
                    end.linkTo(image.end, margin = HierarchicalSize.Spacing.Small)
                }
            )
        }

        // Content section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HierarchicalSize.Spacing.Medium)
                .constrainAs(content) {
                    top.linkTo(image.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            // Title
            Text(
                text = title,
                style = AppTheme.typography.titleRegular,
                color = AppTheme.colors.baseContentTitle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.Tiny))

            // Price and Rating Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = price,
                    style = AppTheme.typography.titleBold,
                    color = AppTheme.colors.brandContentDefault
                )

                if (rating != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "★",
                            style = AppTheme.typography.bodyRegular,
                            color = AppTheme.colors.warningContentDefault
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = rating.toString(),
                            style = AppTheme.typography.captionRegular,
                            color = AppTheme.colors.baseContentBody
                        )
                    }
                }
            }
        }
    }
}

/**
 * ArticleCard - Display article with image, category, title, description
 *
 * @param imageUrl Article featured image URL
 * @param category Article category
 * @param title Article title
 * @param description Article excerpt
 * @param author Optional author name
 * @param date Optional publish date
 * @param onClick Click handler
 * @param modifier Modifier
 * @param variant Card variant style
 * @param isLoading Loading state
 */
@Composable
fun ArticleCard(
    imageUrl: String,
    category: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    author: String? = null,
    date: String? = null,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        onClick = onClick,
        isLoading = isLoading,
        padding = BaseCardPadding.None
    ) {
        val (image, badge, content) = createRefs()

        // Article Image
        PixaImage(
            url = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Crop,
            shape = RoundedCornerShape(
                topStart = RadiusSize.Medium,
                topEnd = RadiusSize.Medium
            )
        )

        // Category Badge
        PixaBadge(
            content = category,
            variant = BadgeVariant.Primary,
            style = BadgeStyle.Solid,
            modifier = Modifier.constrainAs(badge) {
                top.linkTo(image.top, margin = HierarchicalSize.Spacing.Small)
                start.linkTo(image.start, margin = HierarchicalSize.Spacing.Small)
            }
        )

        // Content section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HierarchicalSize.Spacing.Medium)
                .constrainAs(content) {
                    top.linkTo(image.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            // Title
            Text(
                text = title,
                style = AppTheme.typography.titleBold,
                color = AppTheme.colors.baseContentTitle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.Tiny))

            // Description
            Text(
                text = description,
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (author != null || date != null) {
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

                // Metadata Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Tiny)
                ) {
                    author?.let {
                        Text(
                            text = it,
                            style = AppTheme.typography.captionBold,
                            color = AppTheme.colors.baseContentCaption
                        )
                    }
                    if (author != null && date != null) {
                        Text(
                            text = "•",
                            style = AppTheme.typography.captionRegular,
                            color = AppTheme.colors.baseContentCaption
                        )
                    }
                    date?.let {
                        Text(
                            text = it,
                            style = AppTheme.typography.captionRegular,
                            color = AppTheme.colors.baseContentCaption
                        )
                    }
                }
            }
        }
    }
}

/**
 * ProfileCard - Display user profile with avatar, name, role, stats
 *
 * @param avatarUrl User avatar image URL
 * @param name User name
 * @param role User role or title
 * @param stats Map of stat labels to values (e.g., "Followers" to "1.2K")
 * @param onFollowClick Optional follow button click handler
 * @param onMessageClick Optional message button click handler
 * @param onClick Card click handler
 * @param modifier Modifier
 * @param variant Card variant style
 * @param isLoading Loading state
 * @param isFollowing Whether user is already following
 */
@Composable
fun ProfileCard(
    avatarUrl: String,
    name: String,
    role: String,
    modifier: Modifier = Modifier,
    stats: Map<String, String>? = null,
    onFollowClick: (() -> Unit)? = null,
    onMessageClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    isLoading: Boolean = false,
    isFollowing: Boolean = false
) {
    PixaCard(
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        onClick = onClick,
        isLoading = isLoading,
        padding = BaseCardPadding.Medium
    ) {
        val (avatar, nameText, roleText, statsRow, actionsRow) = createRefs()

        // Avatar
        PixaAvatar(
            imageUrl = avatarUrl,
            contentDescription = name,
            size = SizeVariant.Huge,
            modifier = Modifier.constrainAs(avatar) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Name
        Text(
            text = name,
            style = AppTheme.typography.titleBold,
            color = AppTheme.colors.baseContentTitle,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(nameText) {
                top.linkTo(avatar.bottom, margin = HierarchicalSize.Spacing.Small)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Role
        Text(
            text = role,
            style = AppTheme.typography.bodyRegular,
            color = AppTheme.colors.baseContentCaption,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(roleText) {
                top.linkTo(nameText.bottom, margin = 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Stats Row
        if (stats != null && stats.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(statsRow) {
                        top.linkTo(roleText.bottom, margin = HierarchicalSize.Spacing.Medium)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                stats.forEach { (label, value) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = value,
                            style = AppTheme.typography.titleBold,
                            color = AppTheme.colors.baseContentTitle
                        )
                        Text(
                            text = label,
                            style = AppTheme.typography.captionRegular,
                            color = AppTheme.colors.baseContentCaption
                        )
                    }
                }
            }
        }

        // Action Buttons
        if (onFollowClick != null || onMessageClick != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(actionsRow) {
                        top.linkTo(
                            if (stats != null) statsRow.bottom else roleText.bottom,
                            margin = HierarchicalSize.Spacing.Medium
                        )
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
            ) {
                onFollowClick?.let {
                    PixaButton(
                        text = if (isFollowing) "Following" else "Follow",
                        onClick = it,
                        variant = if (isFollowing) ButtonVariant.Outlined else ButtonVariant.Solid,
                        size = SizeVariant.Small,
                        modifier = Modifier.weight(1f)
                    )
                }
                onMessageClick?.let {
                    PixaButton(
                        text = "Message",
                        onClick = it,
                        variant = ButtonVariant.Outlined,
                        size = SizeVariant.Small,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * NotificationCard - Display notification with icon, title, message, time
 *
 * @param icon Icon source for notification type
 * @param title Notification title
 * @param message Notification message
 * @param time Time string (e.g., "2 hours ago")
 * @param isUnread Whether notification is unread
 * @param onClick Click handler
 * @param modifier Modifier
 * @param variant Card variant style
 * @param isLoading Loading state
 */
@Composable
fun NotificationCard(
    icon: IconSource,
    title: String,
    message: String,
    time: String,
    modifier: Modifier = Modifier,
    isUnread: Boolean = false,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Outlined,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        onClick = onClick,
        isLoading = isLoading,
        padding = BaseCardPadding.Medium,
        backgroundColor = if (isUnread) AppTheme.colors.baseSurfaceSubtle else null
    ) {
        val (iconRef, unreadDot, contentCol, timeText) = createRefs()

        // Icon
        PixaIcon(
            source = icon,
            contentDescription = null,
            size = IconSize.Medium,
            tint = AppTheme.colors.brandContentDefault,
            modifier = Modifier.constrainAs(iconRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        // Unread dot indicator
        if (isUnread) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        AppTheme.colors.brandContentDefault,
                        shape = CircleShape
                    )
                    .constrainAs(unreadDot) {
                        top.linkTo(iconRef.top)
                        end.linkTo(iconRef.end)
                    }
            )
        }

        // Content
        Column(
            modifier = Modifier
                .constrainAs(contentCol) {
                    top.linkTo(parent.top)
                    start.linkTo(iconRef.end, margin = HierarchicalSize.Spacing.Small)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                text = title,
                style = AppTheme.typography.bodyBold,
                color = AppTheme.colors.baseContentTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = message,
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Time
        Text(
            text = time,
            style = AppTheme.typography.captionRegular,
            color = AppTheme.colors.baseContentCaption,
            modifier = Modifier.constrainAs(timeText) {
                top.linkTo(contentCol.bottom, margin = Spacing.Tiny)
                start.linkTo(contentCol.start)
            }
        )
    }
}

/**
 * StatsCard - Display statistic with icon, label, value, trend
 *
 * @param icon Icon source for stat type
 * @param label Stat label (e.g., "Total Sales")
 * @param value Stat value (e.g., "12,345")
 * @param trend Optional trend indicator (e.g., "+12.5%")
 * @param trendPositive Whether trend is positive (green) or negative (red)
 * @param onClick Click handler
 * @param modifier Modifier
 * @param variant Card variant style
 * @param isLoading Loading state
 */
@Composable
fun StatsCard(
    icon: IconSource,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trend: String? = null,
    trendPositive: Boolean = true,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Filled,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier,
        variant = variant,
        onClick = onClick,
        isLoading = isLoading,
        padding = BaseCardPadding.Medium
    ) {
        val (iconRef, labelText, valueText, trendText) = createRefs()

        // Icon
        PixaIcon(
            source = icon,
            contentDescription = null,
            size = IconSize.Medium,
            tint = AppTheme.colors.brandContentDefault,
            modifier = Modifier.constrainAs(iconRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        // Label
        Text(
            text = label,
            style = AppTheme.typography.bodyRegular,
            color = AppTheme.colors.baseContentCaption,
            modifier = Modifier.constrainAs(labelText) {
                top.linkTo(iconRef.bottom, margin = HierarchicalSize.Spacing.Small)
                start.linkTo(parent.start)
            }
        )

        // Value
        Text(
            text = value,
            style = AppTheme.typography.titleBold,
            color = AppTheme.colors.baseContentTitle,
            modifier = Modifier.constrainAs(valueText) {
                top.linkTo(labelText.bottom, margin = 4.dp)
                start.linkTo(parent.start)
            }
        )

        // Trend
        if (trend != null) {
            Text(
                text = trend,
                style = AppTheme.typography.captionBold,
                color = if (trendPositive) AppTheme.colors.successContentDefault else AppTheme.colors.errorContentDefault,
                modifier = Modifier.constrainAs(trendText) {
                    top.linkTo(valueText.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                }
            )
        }
    }
}

/**
 * ActionCard - Display action prompt with icon, title, description, CTA
 *
 * @param icon Icon source for action type
 * @param title Action title
 * @param description Action description
 * @param ctaText CTA button text
 * @param onCtaClick CTA button click handler
 * @param onClick Card click handler
 * @param modifier Modifier
 * @param variant Card variant style
 * @param isLoading Loading state
 */
@Composable
fun ActionCard(
    icon: IconSource,
    title: String,
    description: String,
    ctaText: String,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Outlined,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        onClick = onClick,
        isLoading = isLoading,
        padding = BaseCardPadding.Medium
    ) {
        val (iconRef, content, button) = createRefs()

        // Icon
        PixaIcon(
            source = icon,
            contentDescription = null,
            size = IconSize.Large,
            tint = AppTheme.colors.brandContentDefault,
            modifier = Modifier.constrainAs(iconRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(content) {
                    top.linkTo(iconRef.bottom, margin = HierarchicalSize.Spacing.Small)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = AppTheme.typography.titleBold,
                color = AppTheme.colors.baseContentTitle,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.Tiny))

            Text(
                text = description,
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        // CTA Button
        PixaButton(
            text = ctaText,
            onClick = onCtaClick,
            variant = ButtonVariant.Solid,
            size = SizeVariant.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(button) {
                    top.linkTo(content.bottom, margin = HierarchicalSize.Spacing.Medium)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
    }
}

/**
 * MediaCard - Display media with overlay content
 *
 * @param imageUrl Media image/thumbnail URL
 * @param title Media title
 * @param subtitle Optional media subtitle
 * @param duration Optional duration string (e.g., "12:34")
 * @param showPlayButton Whether to show play button overlay
 * @param onClick Click handler
 * @param modifier Modifier
 * @param variant Card variant style
 * @param isLoading Loading state
 */
@Composable
fun MediaCard(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    duration: String? = null,
    showPlayButton: Boolean = true,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        onClick = onClick,
        isLoading = isLoading,
        padding = BaseCardPadding.None
    ) {
        val (image, overlay, playButton, durationBadge, textContent) = createRefs()

        // Media Image
        PixaImage(
            url = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Crop
        )

        // Dark overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
                .constrainAs(overlay) {
                    top.linkTo(image.top)
                    start.linkTo(image.start)
                    end.linkTo(image.end)
                }
        )

        // Play Button
        if (showPlayButton) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Color.White.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
                    .constrainAs(playButton) {
                        top.linkTo(image.top)
                        bottom.linkTo(image.bottom)
                        start.linkTo(image.start)
                        end.linkTo(image.end)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    style = AppTheme.typography.titleBold,
                    color = AppTheme.colors.brandContentDefault
                )
            }
        }

        // Duration Badge
        if (duration != null) {
            Box(
                modifier = Modifier
                    .padding(HierarchicalSize.Spacing.Small)
                    .background(
                        Color.Black.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(RadiusSize.Small)
                    )
                    .padding(horizontal = Spacing.Tiny, vertical = 4.dp)
                    .constrainAs(durationBadge) {
                        bottom.linkTo(image.bottom, margin = HierarchicalSize.Spacing.Small)
                        end.linkTo(image.end, margin = HierarchicalSize.Spacing.Small)
                    }
            ) {
                Text(
                    text = duration,
                    style = AppTheme.typography.captionBold,
                    color = Color.White
                )
            }
        }

        // Text Content Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HierarchicalSize.Spacing.Medium)
                .constrainAs(textContent) {
                    bottom.linkTo(image.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text(
                text = title,
                style = AppTheme.typography.titleBold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            subtitle?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = AppTheme.typography.bodyRegular,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * TestimonialCard - Display testimonial with avatar, quote, author
 *
 * @param avatarUrl Author avatar image URL
 * @param quote Testimonial quote text
 * @param authorName Author name
 * @param authorRole Author role or company
 * @param rating Optional rating (0.0 to 5.0)
 * @param onClick Click handler
 * @param modifier Modifier
 * @param variant Card variant style
 * @param isLoading Loading state
 */
@Composable
fun TestimonialCard(
    avatarUrl: String,
    quote: String,
    authorName: String,
    authorRole: String,
    modifier: Modifier = Modifier,
    rating: Float? = null,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Outlined,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        onClick = onClick,
        isLoading = isLoading,
        padding = BaseCardPadding.Large
    ) {
        val (quoteIcon, quoteText, ratingRow, avatarRef, authorInfo) = createRefs()

        // Quote Icon
        Text(
            text = "",
            style = AppTheme.typography.displaySmall,
            color = AppTheme.colors.brandContentDefault.copy(alpha = 0.3f),
            modifier = Modifier.constrainAs(quoteIcon) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        // Quote Text
        Text(
            text = quote,
            style = AppTheme.typography.bodyRegular,
            color = AppTheme.colors.baseContentBody,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(quoteText) {
                    top.linkTo(quoteIcon.bottom, margin = HierarchicalSize.Spacing.Small)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Rating
        if (rating != null) {
            Row(
                modifier = Modifier
                    .constrainAs(ratingRow) {
                        top.linkTo(quoteText.bottom, margin = HierarchicalSize.Spacing.Small)
                        start.linkTo(parent.start)
                    },
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(5) { index ->
                    Text(
                        text = if (index < rating.toInt()) "★" else "☆",
                        style = AppTheme.typography.bodyRegular,
                        color = AppTheme.colors.warningContentDefault
                    )
                }
            }
        }

        // Author Avatar
        PixaAvatar(
            imageUrl = avatarUrl,
            contentDescription = authorName,
            size = SizeVariant.Medium,
            modifier = Modifier.constrainAs(avatarRef) {
                top.linkTo(
                    if (rating != null) ratingRow.bottom else quoteText.bottom,
                    margin = HierarchicalSize.Spacing.Medium
                )
                start.linkTo(parent.start)
            }
        )

        // Author Info
        Column(
            modifier = Modifier.constrainAs(authorInfo) {
                top.linkTo(avatarRef.top)
                bottom.linkTo(avatarRef.bottom)
                start.linkTo(avatarRef.end, margin = HierarchicalSize.Spacing.Small)
            }
        ) {
            Text(
                text = authorName,
                style = AppTheme.typography.bodyBold,
                color = AppTheme.colors.baseContentTitle
            )
            Text(
                text = authorRole,
                style = AppTheme.typography.captionRegular,
                color = AppTheme.colors.baseContentSubtitle
            )
        }
    }
}

/**
 * PricingCard - Display pricing plan with features and CTA
 *
 * @param planName Plan name (e.g., "Pro", "Enterprise")
 * @param price Price string (e.g., "$29/month")
 * @param features List of feature strings
 * @param ctaText CTA button text
 * @param onCtaClick CTA button click handler
 * @param isPopular Whether to show "Popular" badge
 * @param onClick Card click handler
 * @param modifier Modifier
 * @param variant Card variant style
 * @param isLoading Loading state
 */
@Composable
fun PricingCard(
    planName: String,
    price: String,
    features: List<String>,
    ctaText: String,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPopular: Boolean = false,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Outlined,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier,
        variant = variant,
        onClick = onClick,
        isLoading = isLoading,
        padding = BaseCardPadding.Large,
        backgroundColor = if (isPopular) AppTheme.colors.brandSurfaceSubtle else null
    ) {
        val (popularBadge, planText, priceText, featuresList, ctaButton) = createRefs()

        // Popular Badge
        if (isPopular) {
            PixaBadge(
                content = "Popular",
                variant = BadgeVariant.Primary,
                style = BadgeStyle.Solid,
                modifier = Modifier.constrainAs(popularBadge) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }

        // Plan Name
        Text(
            text = planName,
            style = AppTheme.typography.titleBold,
            color = AppTheme.colors.baseContentTitle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(planText) {
                    top.linkTo(
                        if (isPopular) popularBadge.bottom else parent.top,
                        margin = if (isPopular) HierarchicalSize.Spacing.Small else 0.dp
                    )
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Price
        Text(
            text = price,
            style = AppTheme.typography.headerBold,
            color = AppTheme.colors.brandContentDefault,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(priceText) {
                    top.linkTo(planText.bottom, margin = Spacing.Tiny)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Features List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(featuresList) {
                    top.linkTo(priceText.bottom, margin = HierarchicalSize.Spacing.Medium)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
        ) {
            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Tiny)
                ) {
                    Text(
                        text = "✓",
                        style = AppTheme.typography.bodyRegular,
                        color = AppTheme.colors.successContentDefault
                    )
                    Text(
                        text = feature,
                        style = AppTheme.typography.bodyRegular,
                        color = AppTheme.colors.baseContentBody
                    )
                }
            }
        }

        // CTA Button
        PixaButton(
            text = ctaText,
            onClick = onCtaClick,
            variant = if (isPopular) ButtonVariant.Solid else ButtonVariant.Outlined,
            size = SizeVariant.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(ctaButton) {
                    top.linkTo(featuresList.bottom, margin = HierarchicalSize.Spacing.Medium)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
    }
}

/**
 * EventCard - Display event with date badge, title, location, time
 *
 * **Use Cases:** Notices, tips, help content, information panels, announcements
 *
 * @param title Card title (required)
 * @param modifier Modifier for the card
 * @param subtitle Optional subtitle
 * @param description Optional description
 * @param icon Optional leading icon (ImageVector)
 * @param iconUrl Optional leading icon from URL
 * @param trailingIcon Optional trailing icon
 * @param variant Card visual style (default: Elevated)
 * @param padding Card padding (default: Medium)
 * @param cornerRadius Corner radius (default: RadiusSize.Medium)
 * @param isLoading Loading state
 *
 * @sample
 * ```
 * InfoCard(
 *     title = "Welcome to PixaCompose",
 *     description = "Build beautiful UIs with ready-to-use components.",
 *     icon = Icons.Default.Info
 * )
 * ```
 */
@Composable
fun InfoCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    description: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconUrl: String? = null,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier,
        variant = variant,
        padding = padding,
        cornerRadius = cornerRadius,
        isLoading = isLoading
    ) {
        val (leadingIcon, content, trailing) = createRefs()

        // Leading Icon
        if (icon != null || iconUrl != null) {
            Box(
                modifier = Modifier.constrainAs(leadingIcon) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
            ) {
                when {
                    icon != null -> PixaIcon(
                        source = IconSource.Vector(icon),
                        contentDescription = title,
                        size = IconSize.Large,
                        tint = AppTheme.colors.brandContentDefault
                    )

                    iconUrl != null -> PixaIcon(
                        source = IconSource.Url(iconUrl),
                        contentDescription = title,
                        size = IconSize.Large
                    )
                }
            }
        }

        // Content Column
        Column(
            modifier = Modifier.constrainAs(content) {
                top.linkTo(parent.top)
                start.linkTo(
                    if (icon != null || iconUrl != null) leadingIcon.end else parent.start,
                    margin = if (icon != null || iconUrl != null) HierarchicalSize.Spacing.Small else 0.dp
                )
                end.linkTo(
                    if (trailingIcon != null) trailing.start else parent.end,
                    margin = if (trailingIcon != null) HierarchicalSize.Spacing.Small else 0.dp
                )
                width = Dimension.fillToConstraints
            }
        ) {
            Text(
                text = title,
                style = AppTheme.typography.titleBold,
                color = AppTheme.colors.baseContentTitle
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(Spacing.Micro))
                Text(
                    text = subtitle,
                    style = AppTheme.typography.bodyBold,
                    color = AppTheme.colors.baseContentCaption
                )
            }

            if (description != null) {
                Spacer(modifier = Modifier.height(Spacing.Tiny))
                Text(
                    text = description,
                    style = AppTheme.typography.bodyRegular,
                    color = AppTheme.colors.baseContentBody
                )
            }
        }

        // Trailing Icon
        if (trailingIcon != null) {
            Box(
                modifier = Modifier.constrainAs(trailing) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            ) {
                PixaIcon(
                    source = IconSource.Vector(trailingIcon),
                    contentDescription = "More",
                    size = IconSize.Medium,
                    tint = AppTheme.colors.baseContentCaption
                )
            }
        }
    }
}

/**
 * ActionCard - Clickable cards that trigger actions
 *
 * **Use Cases:** Navigation cards, action triggers, interactive menu items, quick actions
 *
 * @param title Card title (required)
 * @param onClick Click handler (required)
 * @param modifier Modifier for the card
 * @param subtitle Optional subtitle
 * @param description Optional description
 * @param icon Optional leading icon
 * @param iconUrl Optional leading icon from URL
 * @param trailingIcon Optional trailing icon
 * @param enabled Whether the card is enabled
 * @param variant Card visual style (default: Filled)
 * @param padding Card padding (default: Medium)
 * @param cornerRadius Corner radius (default: RadiusSize.Medium)
 * @param isLoading Loading state
 *
 * @sample
 * ```
 * ActionCard(
 *     title = "Create New Project",
 *     description = "Start a new project from scratch",
 *     icon = Icons.Default.Add,
 *     trailingIcon = Icons.Default.ChevronRight,
 *     onClick = { /* navigate */ }
 * )
 * ```
 */
@Composable
fun ActionCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    description: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconUrl: String? = null,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    enabled: Boolean = true,
    variant: BaseCardVariant = BaseCardVariant.Filled,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier.semantics { role = Role.Button },
        variant = variant,
        padding = padding,
        cornerRadius = cornerRadius,
        onClick = if (enabled) onClick else null,
        enabled = enabled,
        isLoading = isLoading
    ) {
        val (leadingIcon, content, trailing) = createRefs()

        // Leading Icon
        if (icon != null || iconUrl != null) {
            Box(
                modifier = Modifier.constrainAs(leadingIcon) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
            ) {
                when {
                    icon != null -> PixaIcon(
                        source = IconSource.Vector(icon),
                        contentDescription = title,
                        size = IconSize.Large,
                        tint = if (enabled) AppTheme.colors.brandContentDefault else AppTheme.colors.baseContentDisabled
                    )

                    iconUrl != null -> PixaIcon(
                        source = IconSource.Url(iconUrl),
                        contentDescription = title,
                        size = IconSize.Large
                    )
                }
            }
        }

        // Content Column
        Column(
            modifier = Modifier.constrainAs(content) {
                top.linkTo(parent.top)
                start.linkTo(
                    if (icon != null || iconUrl != null) leadingIcon.end else parent.start,
                    margin = if (icon != null || iconUrl != null) HierarchicalSize.Spacing.Small else 0.dp
                )
                end.linkTo(
                    if (trailingIcon != null) trailing.start else parent.end,
                    margin = if (trailingIcon != null) HierarchicalSize.Spacing.Small else 0.dp
                )
                width = Dimension.fillToConstraints
            }
        ) {
            Text(
                text = title,
                style = AppTheme.typography.titleBold,
                color = if (enabled) AppTheme.colors.baseContentTitle else AppTheme.colors.baseContentDisabled
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(Spacing.Micro))
                Text(
                    text = subtitle,
                    style = AppTheme.typography.bodyBold,
                    color = if (enabled) AppTheme.colors.baseContentCaption else AppTheme.colors.baseContentDisabled
                )
            }

            if (description != null) {
                Spacer(modifier = Modifier.height(Spacing.Tiny))
                Text(
                    text = description,
                    style = AppTheme.typography.bodyRegular,
                    color = if (enabled) AppTheme.colors.baseContentBody else AppTheme.colors.baseContentDisabled
                )
            }
        }

        // Trailing Icon
        if (trailingIcon != null) {
            Box(
                modifier = Modifier.constrainAs(trailing) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            ) {
                PixaIcon(
                    source = IconSource.Vector(trailingIcon),
                    contentDescription = "Action",
                    size = IconSize.Medium,
                    tint = if (enabled) AppTheme.colors.baseContentCaption else AppTheme.colors.baseContentDisabled
                )
            }
        }
    }
}

/**
 * SelectCard - Cards for selectable options (single or multi-select)
 *
 * **Use Cases:** Choice selection, settings options, preference selection, multi-select lists,
 * profile settings (sleep hours, activity level, etc.)
 *
 * **Special Features:**
 * - All parameters optional (title, description, icon can be null)
 * - Supports both ImageVector icons and remote image URLs
 * - Automatically highlights selected state with brand colors
 * - Perfect for ProfileSettingScreen sleep hours use case
 *
 * @param modifier Modifier for the card
 * @param title Optional title
 * @param description Optional description
 * @param icon Optional vector icon
 * @param iconUrl Optional remote image URL
 * @param iconSize Icon size (default: ExtraLarge)
 * @param iconTint Icon tint color (null for no tint)
 * @param isSelected Selection state
 * @param onClick Click handler (required)
 * @param enabled Whether the card is enabled
 * @param variant Card visual style (auto-styled based on selection if null)
 * @param padding Card padding (default: Medium)
 * @param cornerRadius Corner radius (default: RadiusSize.Medium)
 * @param isLoading Loading state
 *
 * @sample
 * ```
 * // Example 1: Sleep Hours (with remote icon URLs)
 * SelectCard(
 *     modifier = Modifier.fillMaxWidth(),
 *     title = "7-8 hours",
 *     description = "Recommended sleep",
 *     iconUrl = "https://example.com/sleep-icon.png",
 *     isSelected = selectedOption == 0,
 *     onClick = { selectedOption = 0 }
 * )
 *
 * // Example 2: Theme Selection (with vector icons)
 * SelectCard(
 *     title = "Dark Mode",
 *     description = "Easy on the eyes",
 *     icon = Icons.Default.DarkMode,
 *     iconTint = AppTheme.colors.brandContentDefault,
 *     isSelected = selectedTheme == 1,
 *     onClick = { selectedTheme = 1 }
 * )
 *
 * // Example 3: Icon Only
 * SelectCard(
 *     icon = Icons.Default.Favorite,
 *     isSelected = selected,
 *     onClick = { selected = !selected }
 * )
 * ```
 */
@Composable
fun SelectCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconUrl: String? = null,
    iconSize: Dp = IconSize.ExtraLarge,
    iconTint: Color? = null,
    isSelected: Boolean = false,
    enabled: Boolean = true,
    variant: BaseCardVariant? = null,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    isLoading: Boolean = false
) {
    // Auto-style based on selection state if variant not provided
    val cardVariant =
        variant ?: if (isSelected) BaseCardVariant.Filled else BaseCardVariant.Outlined

    // Custom border color for selected state
    val borderModifier = if (isSelected && variant == null) {
        Modifier.border(
            width = BorderSize.Thick,
            color = AppTheme.colors.brandBorderFocus,
            shape = RoundedCornerShape(cornerRadius)
        )
    } else {
        Modifier
    }

    PixaCard(
        modifier = modifier.then(borderModifier).semantics { role = Role.Checkbox },
        variant = cardVariant,
        padding = padding,
        cornerRadius = cornerRadius,
        onClick = if (enabled) onClick else null,
        enabled = enabled,
        backgroundColor = if (isSelected && variant == null)
            AppTheme.colors.brandSurfaceSubtle
        else null,
        isLoading = isLoading
    ) {
        val hasContent = title != null || description != null

        if (hasContent) {
            val (leadingIcon, content) = createRefs()

            // Leading Icon
            if (icon != null || iconUrl != null) {
                Box(
                    modifier = Modifier.constrainAs(leadingIcon) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                ) {
                    when {
                        icon != null -> PixaIcon(
                            source = IconSource.Vector(icon),
                            contentDescription = title,
                            size = iconSize,
                            tint = iconTint ?: if (isSelected)
                                AppTheme.colors.brandContentDefault
                            else
                                AppTheme.colors.baseContentBody
                        )

                        iconUrl != null -> PixaIcon(
                            source = IconSource.Url(iconUrl),
                            contentDescription = title,
                            size = iconSize,
                            tint = iconTint
                        )
                    }
                }
            }

            // Content Column
            Column(
                modifier = Modifier.constrainAs(content) {
                    top.linkTo(parent.top)
                    start.linkTo(
                        if (icon != null || iconUrl != null) leadingIcon.end else parent.start,
                        margin = if (icon != null || iconUrl != null) HierarchicalSize.Spacing.Small else 0.dp
                    )
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = AppTheme.typography.titleBold,
                        color = if (isSelected)
                            AppTheme.colors.brandContentDefault
                        else
                            AppTheme.colors.baseContentTitle
                    )
                }

                if (description != null) {
                    Spacer(modifier = Modifier.height(Spacing.Micro))
                    Text(
                        text = description,
                        style = AppTheme.typography.bodyRegular,
                        color = if (isSelected)
                            AppTheme.colors.brandContentSubtle
                        else
                            AppTheme.colors.baseContentBody
                    )
                }
            }
        } else {
            // Icon-only mode (centered)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    icon != null -> PixaIcon(
                        source = IconSource.Vector(icon),
                        contentDescription = "Option",
                        size = iconSize,
                        tint = iconTint ?: if (isSelected)
                            AppTheme.colors.brandContentDefault
                        else
                            AppTheme.colors.baseContentBody
                    )

                    iconUrl != null -> PixaIcon(
                        source = IconSource.Url(iconUrl),
                        contentDescription = "Option",
                        size = iconSize,
                        tint = iconTint
                    )
                }
            }
        }
    }
}

/**
 * MediaCard - Cards with prominent media content
 *
 * **Use Cases:** Gallery items, content previews, media libraries, article cards
 *
 * @param imageUrl Image URL (required)
 * @param modifier Modifier for the card
 * @param title Optional title
 * @param subtitle Optional subtitle
 * @param description Optional description
 * @param imageHeight Height of the image section
 * @param onClick Optional click handler
 * @param enabled Whether the card is enabled
 * @param variant Card visual style (default: Elevated)
 * @param cornerRadius Corner radius (default: RadiusSize.Medium)
 * @param isLoading Loading state
 *
 * @sample
 * ```
 * MediaCard(
 *     imageUrl = "https://example.com/image.jpg",
 *     title = "Component Library",
 *     subtitle = "Featured Article",
 *     description = "Learn best practices for building UIs.",
 *     onClick = { openArticle() }
 * )
 * ```
 */
@Composable
fun MediaCard(
    imageUrl: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    description: String? = null,
    imageHeight: Dp = HierarchicalSize.Container.Massive,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    cornerRadius: Dp = RadiusSize.Medium,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier,
        variant = variant,
        padding = BaseCardPadding.None,
        cornerRadius = cornerRadius,
        onClick = if (enabled) onClick else null,
        enabled = enabled,
        isLoading = isLoading
    ) {
        val (image, content) = createRefs()

        // Media Image
        PixaImage(
            source = PixaImageSource.Url(imageUrl),
            contentDescription = title ?: "Media",
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Crop,
            shape = RoundedCornerShape(
                topStart = cornerRadius,
                topEnd = cornerRadius
            )
        )

        // Content Section (if any text provided)
        if (title != null || subtitle != null || description != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HierarchicalSize.Spacing.Medium)
                    .constrainAs(content) {
                        top.linkTo(image.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = AppTheme.typography.captionBold,
                        color = AppTheme.colors.brandContentDefault
                    )
                    Spacer(modifier = Modifier.height(Spacing.Micro))
                }

                if (title != null) {
                    Text(
                        text = title,
                        style = AppTheme.typography.titleBold,
                        color = AppTheme.colors.baseContentTitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (description != null) {
                    Spacer(modifier = Modifier.height(Spacing.Tiny))
                    Text(
                        text = description,
                        style = AppTheme.typography.bodyRegular,
                        color = AppTheme.colors.baseContentBody,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * StatCard - Display statistics and metrics
 *
 * **Use Cases:** Dashboard stats, analytics display, progress metrics, KPI cards
 *
 * @param value Stat value (e.g., "42", "85%") - required
 * @param label Stat label - required
 * @param modifier Modifier for the card
 * @param icon Optional icon
 * @param trend Optional trend indicator (e.g., "+12%")
 * @param trendPositive Whether trend is positive (affects color)
 * @param onClick Optional click handler
 * @param variant Card visual style (default: Filled)
 * @param padding Card padding (default: Medium)
 * @param cornerRadius Corner radius (default: RadiusSize.Medium)
 * @param isLoading Loading state
 *
 * @sample
 * ```
 * Row(horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
 *     StatCard(
 *         modifier = Modifier.weight(1f),
 *         value = "42",
 *         label = "Active Projects",
 *         icon = Icons.Default.TrendingUp,
 *         trend = "+12%",
 *         trendPositive = true
 *     )
 *
 *     StatCard(
 *         modifier = Modifier.weight(1f),
 *         value = "85%",
 *         label = "Completion Rate",
 *         trend = "-3%",
 *         trendPositive = false
 *     )
 * }
 * ```
 */
@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trend: String? = null,
    trendPositive: Boolean = true,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Filled,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier,
        variant = variant,
        padding = padding,
        cornerRadius = cornerRadius,
        onClick = onClick,
        isLoading = isLoading
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon + Trend Row
            if (icon != null || trend != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        PixaIcon(
                            source = IconSource.Vector(icon),
                            contentDescription = label,
                            size = IconSize.Medium,
                            tint = AppTheme.colors.brandContentDefault
                        )
                    }

                    if (trend != null) {
                        Text(
                            text = trend,
                            style = AppTheme.typography.captionBold,
                            color = if (trendPositive)
                                AppTheme.colors.successContentDefault
                            else
                                AppTheme.colors.errorContentDefault
                        )
                    }
                }
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
            }

            // Value
            Text(
                text = value,
                style = AppTheme.typography.displayLarge,
                color = AppTheme.colors.baseContentTitle
            )

            Spacer(modifier = Modifier.height(Spacing.Micro))

            // Label
            Text(
                text = label,
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentCaption
            )
        }
    }
}

/**
 * ListItemCard - Cards for list items with consistent layout
 *
 * **Use Cases:** Settings items, menu items, selectable lists, navigation lists
 *
 * @param title Item title (required)
 * @param onClick Click handler (required)
 * @param modifier Modifier for the card
 * @param subtitle Optional subtitle
 * @param leadingIcon Optional leading icon
 * @param leadingContent Optional leading content (overrides icon)
 * @param trailingIcon Optional trailing icon
 * @param trailingContent Optional trailing content (overrides icon)
 * @param enabled Whether the item is enabled
 * @param variant Card visual style (default: Ghost)
 * @param padding Card padding (default: Small)
 * @param cornerRadius Corner radius (default: RadiusSize.Small)
 * @param isLoading Loading state
 *
 * @sample
 * ```
 * ListItemCard(
 *     title = "Notifications",
 *     subtitle = "Push, Email, SMS",
 *     leadingIcon = Icons.Default.Notifications,
 *     trailingIcon = Icons.Default.ChevronRight,
 *     onClick = { openNotificationSettings() }
 * )
 * ```
 */
@Composable
fun ListItemCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    variant: BaseCardVariant = BaseCardVariant.Ghost,
    padding: BaseCardPadding = BaseCardPadding.Small,
    cornerRadius: Dp = RadiusSize.Small,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier.semantics { role = Role.Button },
        variant = variant,
        padding = padding,
        cornerRadius = cornerRadius,
        onClick = if (enabled) onClick else null,
        enabled = enabled,
        isLoading = isLoading
    ) {
        val (leading, content, trailing) = createRefs()

        // Leading Content/Icon
        if (leadingContent != null || leadingIcon != null) {
            Box(
                modifier = Modifier.constrainAs(leading) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
            ) {
                when {
                    leadingContent != null -> leadingContent()
                    leadingIcon != null -> PixaIcon(
                        source = IconSource.Vector(leadingIcon),
                        contentDescription = title,
                        size = IconSize.Medium,
                        tint = if (enabled) AppTheme.colors.baseContentBody else AppTheme.colors.baseContentDisabled
                    )
                }
            }
        }

        // Content Column
        Column(
            modifier = Modifier.constrainAs(content) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(
                    if (leadingContent != null || leadingIcon != null) leading.end else parent.start,
                    margin = if (leadingContent != null || leadingIcon != null) HierarchicalSize.Spacing.Small else 0.dp
                )
                end.linkTo(
                    if (trailingContent != null || trailingIcon != null) trailing.start else parent.end,
                    margin = if (trailingContent != null || trailingIcon != null) HierarchicalSize.Spacing.Small else 0.dp
                )
                width = Dimension.fillToConstraints
            }
        ) {
            Text(
                text = title,
                style = AppTheme.typography.bodyBold,
                color = if (enabled) AppTheme.colors.baseContentTitle else AppTheme.colors.baseContentDisabled
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(Spacing.Micro))
                Text(
                    text = subtitle,
                    style = AppTheme.typography.captionRegular,
                    color = if (enabled) AppTheme.colors.baseContentCaption else AppTheme.colors.baseContentDisabled
                )
            }
        }

        // Trailing Content/Icon
        if (trailingContent != null || trailingIcon != null) {
            Box(
                modifier = Modifier.constrainAs(trailing) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
            ) {
                when {
                    trailingContent != null -> trailingContent()
                    trailingIcon != null -> PixaIcon(
                        source = IconSource.Vector(trailingIcon),
                        contentDescription = "More",
                        size = IconSize.Medium,
                        tint = if (enabled) AppTheme.colors.baseContentCaption else AppTheme.colors.baseContentDisabled
                    )
                }
            }
        }
    }
}

/**
 * FeatureCard - Highlight features or benefits
 *
 * **Use Cases:** Onboarding screens, feature tours, marketing content, benefits showcase
 *
 * @param title Feature title (required)
 * @param description Feature description (required)
 * @param icon Feature icon (required)
 * @param modifier Modifier for the card
 * @param iconBackgroundColor Optional background color for icon
 * @param iconTint Optional icon tint color
 * @param onClick Optional click handler
 * @param variant Card visual style (default: Outlined)
 * @param padding Card padding (default: Medium)
 * @param cornerRadius Corner radius (default: RadiusSize.Medium)
 * @param isLoading Loading state
 *
 * @sample
 * ```
 * Row(horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
 *     FeatureCard(
 *         modifier = Modifier.weight(1f),
 *         title = "Fast Setup",
 *         description = "Get started in minutes",
 *         icon = Icons.Default.Speed
 *     )
 *
 *     FeatureCard(
 *         modifier = Modifier.weight(1f),
 *         title = "Cross-Platform",
 *         description = "Works on Android & iOS",
 *         icon = Icons.Default.Devices
 *     )
 * }
 * ```
 */
@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    iconBackgroundColor: Color? = null,
    iconTint: Color? = null,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Outlined,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier,
        variant = variant,
        padding = padding,
        cornerRadius = cornerRadius,
        onClick = onClick,
        isLoading = isLoading
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon with optional background
            Box(
                modifier = Modifier
                    .size(ComponentSize.Huge)
                    .background(
                        color = iconBackgroundColor ?: AppTheme.colors.brandSurfaceSubtle,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                PixaIcon(
                    source = IconSource.Vector(icon),
                    contentDescription = title,
                    size = IconSize.ExtraLarge,
                    tint = iconTint ?: AppTheme.colors.brandContentDefault
                )
            }

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

            // Title
            Text(
                text = title,
                style = AppTheme.typography.titleBold,
                color = AppTheme.colors.baseContentTitle,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.Tiny))

            // Description
            Text(
                text = description,
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * CompactCard - Small cards for compact layouts
 *
 * **Use Cases:** Tags and chips, quick actions, compact lists, filter options
 *
 * @param title Card title (required)
 * @param modifier Modifier for the card
 * @param icon Optional icon
 * @param onClick Optional click handler
 * @param enabled Whether the card is enabled
 * @param variant Card visual style (default: Filled)
 * @param cornerRadius Corner radius (default: RadiusSize.Small)
 * @param isLoading Loading state
 *
 * @sample
 * ```
 * Row(horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)) {
 *     CompactCard(
 *         title = "Health",
 *         icon = Icons.Default.FavoriteBorder
 *     )
 *     CompactCard(
 *         title = "Fitness",
 *         icon = Icons.Default.FitnessCenter
 *     )
 * }
 * ```
 */
@Composable
fun CompactCard(
    title: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    variant: BaseCardVariant = BaseCardVariant.Filled,
    cornerRadius: Dp = RadiusSize.Small,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier,
        variant = variant,
        padding = BaseCardPadding.Compact,
        cornerRadius = cornerRadius,
        onClick = if (enabled) onClick else null,
        enabled = enabled,
        isLoading = isLoading
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Tiny)
        ) {
            if (icon != null) {
                PixaIcon(
                    source = IconSource.Vector(icon),
                    contentDescription = title,
                    size = IconSize.Small,
                    tint = if (enabled) AppTheme.colors.baseContentBody else AppTheme.colors.baseContentDisabled
                )
            }

            Text(
                text = title,
                style = AppTheme.typography.captionBold,
                color = if (enabled) AppTheme.colors.baseContentTitle else AppTheme.colors.baseContentDisabled
            )
        }
    }
}

/**
 * SummaryCard - Display grouped summary information
 *
 * **Use Cases:** Overview panels, summary sections, data aggregation, reports
 *
 * @param title Summary title (required)
 * @param items List of label-value pairs (required)
 * @param modifier Modifier for the card
 * @param icon Optional header icon
 * @param onClick Optional click handler
 * @param variant Card visual style (default: Elevated)
 * @param padding Card padding (default: Medium)
 * @param cornerRadius Corner radius (default: RadiusSize.Medium)
 * @param isLoading Loading state
 *
 * @sample
 * ```
 * SummaryCard(
 *     title = "Weekly Summary",
 *     icon = Icons.Default.CalendarMonth,
 *     items = listOf(
 *         "Total Projects" to "12",
 *         "Completed" to "10",
 *         "In Progress" to "2",
 *         "Completion Rate" to "83%"
 *     )
 * )
 * ```
 */
@Composable
fun SummaryCard(
    title: String,
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: (() -> Unit)? = null,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    isLoading: Boolean = false
) {
    PixaCard(
        modifier = modifier,
        variant = variant,
        padding = padding,
        cornerRadius = cornerRadius,
        onClick = onClick,
        isLoading = isLoading
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
            ) {
                if (icon != null) {
                    PixaIcon(
                        source = IconSource.Vector(icon),
                        contentDescription = title,
                        size = IconSize.Medium,
                        tint = AppTheme.colors.brandContentDefault
                    )
                }

                Text(
                    text = title,
                    style = AppTheme.typography.titleBold,
                    color = AppTheme.colors.baseContentTitle
                )
            }

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

            // Items List
            Column(
                verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
            ) {
                items.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = AppTheme.typography.bodyRegular,
                            color = AppTheme.colors.baseContentBody
                        )

                        Text(
                            text = value,
                            style = AppTheme.typography.bodyBold,
                            color = AppTheme.colors.baseContentTitle
                        )
                    }
                }
            }
        }
    }
}




