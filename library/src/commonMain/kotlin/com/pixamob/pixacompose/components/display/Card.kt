package com.pixamob.pixacompose.components.display

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
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
        BaseCardPadding.Small -> Spacing.Small
        BaseCardPadding.Medium -> Spacing.Medium
        BaseCardPadding.Large -> Spacing.Large
        BaseCardPadding.ExtraLarge -> Spacing.ExtraLarge
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
    content: @Composable ConstraintLayoutScope.() -> Unit
) {
    // Animated colors based on state with spring for snappier feel
    val backgroundColor by animateColorAsState(
        targetValue = if (!enabled) colors.disabled.background else colors.default.background,
        animationSpec = spring(),
        label = "card_background"
    )

    val borderColor by animateColorAsState(
        targetValue = if (!enabled) colors.disabled.border else colors.default.border,
        animationSpec = spring(),
        label = "card_border"
    )

    val elevationDp = if (variant == BaseCardVariant.Elevated && enabled) {
        getBaseCardElevationDp(elevation)
    } else {
        0.dp
    }

    val paddingDp = getBaseCardPaddingDp(padding)
    val shape = RoundedCornerShape(cornerRadius)

    val borderModifier = if (borderColor != Color.Transparent) {
        Modifier.border(
            width = BorderSize.Standard,
            color = borderColor,
            shape = shape
        )
    } else {
        Modifier
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
                clip = false
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
    elevation: BaseCardElevation = BaseCardElevation.Medium,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    backgroundColor: Color? = null,
    content: @Composable ConstraintLayoutScope.() -> Unit
) {
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
    elevation: BaseCardElevation = BaseCardElevation.Medium,
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    content: @Composable ConstraintLayoutScope.() -> Unit
) = PixaCard(
    modifier = modifier,
    variant = BaseCardVariant.Elevated,
    onClick = onClick,
    enabled = enabled,
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
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    content: @Composable ConstraintLayoutScope.() -> Unit
) = PixaCard(
    modifier = modifier,
    variant = BaseCardVariant.Outlined,
    onClick = onClick,
    enabled = enabled,
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
    padding: BaseCardPadding = BaseCardPadding.Medium,
    cornerRadius: Dp = RadiusSize.Medium,
    backgroundColor: Color? = null,
    content: @Composable ConstraintLayoutScope.() -> Unit
) = PixaCard(
    modifier = modifier,
    variant = BaseCardVariant.Filled,
    onClick = onClick,
    enabled = enabled,
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