package com.pixamob.pixacompose.components.surfaces

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.contrastColor
import com.pixamob.pixacompose.utils.toDp

/*
 * Card primitives. Two unrelated containers live here on purpose:
 *  - PixaSurfaceCard: the Uber Base-aligned primitive (context/state model, spec-accurate
 *    border/radius/elevation tokens). Build the rich anatomy/variant family on this — see
 *    `PixaContentCard` in `components/display/ContentCard.kt`.
 *  - PixaCard: legacy ConstraintLayout container, kept only because `BottomNavBar`, `Stepper`,
 *    `Toast`, `Alert`, and `Chart` still build on it internally. Do not add new call sites.
 */

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/** Structural context driving [PixaSurfaceCard]'s corner radius and resting border weight. */
enum class SurfaceCardContext {
    /** Standalone card: large radius, heavier border, elevated by default. */
    Isolated,

    /** Full-width feed card: small radius, thin divider border, flat by default. */
    Feed
}

/** Visual style for the legacy [PixaCard]. */
enum class BaseCardVariant {
    Elevated,
    Outlined,
    Filled,
    Tonal,
    Ghost
}

/** Border stroke pattern for the legacy [PixaCard]. */
enum class CardBorderStyle {
    Solid,
    Dashed,
    Dotted,
    None
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
private data class SurfaceCardColors(
    val background: Color,
    val border: Color,
    val borderWidth: Dp,
    val hoverOverlay: Color,
    val pressedOverlay: Color,
    val dismissBackground: Color,
    val dismissContent: Color
)

@Stable
data class BaseCardColors(
    val background: Color,
    val border: Color = Color.Transparent,
    val content: Color
)

/** Border override for the legacy [PixaCard]; `color = null` falls back to the variant's theme border. */
@Stable
data class CardBorderConfig(
    val color: Color? = null,
    val width: Dp = HierarchicalSize.Border.Medium,
    val style: CardBorderStyle = CardBorderStyle.Solid
)

/** Shadow override for the legacy [PixaCard]. */
@Stable
data class CardShadowConfig(
    val elevation: Dp = 0.dp,
    val color: Color = Color.Black.copy(alpha = 0.1f),
    val spreadRadius: Dp = 0.dp,
    val blurRadius: Dp = 0.dp,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp
)

@Stable
data class BaseCardStateColors(
    val default: BaseCardColors,
    val hover: BaseCardColors = default,
    val pressed: BaseCardColors = default,
    val disabled: BaseCardColors
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER (state/context resolvers)
// ════════════════════════════════════════════════════════════════════════════

/**
 * Resolves card surface colors from enabled/disabled/focus/selected states.
 * Hover/pressed overlays auto-contrast against [backgroundColor] for light/dark mode.
 */
@Composable
private fun resolveSurfaceCardColors(
    context: SurfaceCardContext,
    backgroundColor: Color,
    enabled: Boolean,
    selected: Boolean,
    isFocused: Boolean
): SurfaceCardColors {
    val isLightBackground = backgroundColor.contrastColor() == Color.Black

    val restingBorderColor = if (context == SurfaceCardContext.Isolated) {
        AppTheme.colors.baseBorderDefault
    } else {
        AppTheme.colors.baseBorderSubtle
    }
    val restingBorderWidth = if (context == SurfaceCardContext.Isolated) {
        HierarchicalSize.Border.Huge
    } else {
        HierarchicalSize.Border.Compact
    }

    val (border, borderWidth) = when {
        !enabled -> AppTheme.colors.baseBorderDisabled to restingBorderWidth
        selected || isFocused -> AppTheme.colors.brandBorderFocus to HierarchicalSize.Border.Large
        else -> restingBorderColor to restingBorderWidth
    }

    val background = if (!enabled) AppTheme.colors.baseSurfaceDisabled else backgroundColor

    val hoverOverlay = if (isLightBackground) Color.Black.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.10f)
    val pressedOverlay = if (isLightBackground) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.20f)

    val dismissBackground = if (isLightBackground) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.4f)

    return SurfaceCardColors(
        background = background,
        border = border,
        borderWidth = borderWidth,
        hoverOverlay = hoverOverlay,
        pressedOverlay = pressedOverlay,
        dismissBackground = dismissBackground,
        dismissContent = backgroundColor.contrastColor()
    )
}

/** Corner radius per context: 24dp isolated, 12dp feed. */
private fun SurfaceCardContext.cornerRadius(): Dp = when (this) {
    SurfaceCardContext.Isolated -> HierarchicalSize.Radius.Massive
    SurfaceCardContext.Feed -> HierarchicalSize.Radius.Large
}

/** Isolated cards get drop shadow by default; feed cards rely on divider border (flat). */
private fun SurfaceCardContext.defaultElevation(): ComponentElevation = when (this) {
    SurfaceCardContext.Isolated -> ComponentElevation.High
    SurfaceCardContext.Feed -> ComponentElevation.None
}

@Composable
private fun getBaseCardTheme(variant: BaseCardVariant, colors: ColorPalette): BaseCardStateColors {
    return when (variant) {
        BaseCardVariant.Elevated -> BaseCardStateColors(
            default = BaseCardColors(background = colors.baseSurfaceDefault, content = colors.baseContentBody),
            hover = BaseCardColors(background = colors.baseSurfaceSubtle, content = colors.baseContentBody),
            pressed = BaseCardColors(background = colors.baseSurfaceSubtle.copy(alpha = 0.8f), content = colors.baseContentBody),
            disabled = BaseCardColors(background = colors.baseSurfaceDisabled, content = colors.baseContentDisabled)
        )

        BaseCardVariant.Outlined -> BaseCardStateColors(
            default = BaseCardColors(background = colors.baseSurfaceDefault, border = colors.baseBorderDefault, content = colors.baseContentBody),
            hover = BaseCardColors(background = colors.baseSurfaceSubtle, border = colors.baseBorderFocus, content = colors.baseContentBody),
            pressed = BaseCardColors(background = colors.baseSurfaceSubtle.copy(alpha = 0.8f), border = colors.baseBorderFocus, content = colors.baseContentBody),
            disabled = BaseCardColors(background = colors.baseSurfaceDisabled, border = colors.baseBorderDisabled, content = colors.baseContentDisabled)
        )

        BaseCardVariant.Filled -> BaseCardStateColors(
            default = BaseCardColors(background = colors.baseSurfaceSubtle, content = colors.baseContentBody),
            hover = BaseCardColors(background = colors.baseSurfaceDefault, content = colors.baseContentBody),
            pressed = BaseCardColors(background = colors.baseSurfaceDefault.copy(alpha = 0.8f), content = colors.baseContentBody),
            disabled = BaseCardColors(background = colors.baseSurfaceDisabled, content = colors.baseContentDisabled)
        )

        BaseCardVariant.Tonal -> BaseCardStateColors(
            default = BaseCardColors(background = colors.brandSurfaceSubtle, content = colors.baseContentTitle),
            hover = BaseCardColors(background = colors.brandSurfaceSubtle.copy(alpha = 0.85f), content = colors.baseContentTitle),
            pressed = BaseCardColors(background = colors.brandSurfaceSubtle.copy(alpha = 0.7f), content = colors.baseContentTitle),
            disabled = BaseCardColors(background = colors.brandSurfaceSubtle.copy(alpha = 0.4f), content = colors.baseContentDisabled)
        )

        BaseCardVariant.Ghost -> BaseCardStateColors(
            default = BaseCardColors(background = Color.Transparent, border = colors.baseBorderSubtle, content = colors.baseContentBody),
            hover = BaseCardColors(background = colors.baseSurfaceSubtle.copy(alpha = 0.5f), border = colors.baseBorderDefault, content = colors.baseContentBody),
            pressed = BaseCardColors(background = colors.baseSurfaceSubtle.copy(alpha = 0.7f), border = colors.baseBorderDefault, content = colors.baseContentBody),
            disabled = BaseCardColors(background = Color.Transparent, border = colors.baseBorderDisabled, content = colors.baseContentDisabled)
        )
    }
}

@Composable
private fun SizeVariant.cardPadding(): Dp = when (this) {
    SizeVariant.None -> HierarchicalSize.Spacing.None
    SizeVariant.Compact -> HierarchicalSize.Spacing.Compact
    SizeVariant.Small -> HierarchicalSize.Spacing.Small
    SizeVariant.Medium -> HierarchicalSize.Spacing.Medium
    SizeVariant.Large -> HierarchicalSize.Spacing.Large
    else -> HierarchicalSize.Spacing.Medium
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun BoxScope.SurfaceCardDismissButton(
    onDismiss: () -> Unit,
    colors: SurfaceCardColors,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(HierarchicalSize.TouchTarget.Small)
            .clip(CircleShape)
            .background(colors.dismissBackground)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onDismiss)
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = "×",
            style = AppTheme.typography.titleBold.copy(color = colors.dismissContent, textAlign = TextAlign.Center)
        )
    }
}

@Composable
private fun InternalCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    elevation: ComponentElevation = ComponentElevation.Medium,
    padding: SizeVariant = SizeVariant.Medium,
    cornerRadius: Dp = HierarchicalSize.Radius.Medium,
    colors: BaseCardStateColors,
    borderConfig: CardBorderConfig? = null,
    shadowConfig: CardShadowConfig? = null,
    content: @Composable ConstraintLayoutScope.() -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (!enabled) colors.disabled.background else colors.default.background,
        animationSpec = AnimationUtils.colorSpring,
        label = "card_background"
    )

    val themeBorderColor = if (!enabled) colors.disabled.border else colors.default.border
    val finalBorderColor = borderConfig?.color ?: themeBorderColor

    val borderColor by animateColorAsState(
        targetValue = finalBorderColor,
        animationSpec = AnimationUtils.colorSpring,
        label = "card_border"
    )

    val elevationDp = when {
        shadowConfig != null -> shadowConfig.elevation
        variant == BaseCardVariant.Elevated && enabled -> elevation.toDp()
        else -> 0.dp
    }

    val paddingDp = padding.cardPadding()
    val shape = RoundedCornerShape(cornerRadius)

    val borderModifier = when {
        borderConfig?.style == CardBorderStyle.None -> Modifier
        borderConfig?.style == CardBorderStyle.Dashed || borderConfig?.style == CardBorderStyle.Dotted -> {
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
            Modifier.border(width = borderConfig?.width ?: HierarchicalSize.Border.Medium, color = borderColor, shape = shape)
        }
        else -> Modifier
    }

    val clickableModifier = if (onClick != null && enabled) {
        Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
    } else {
        Modifier
    }

    ConstraintLayout(
        modifier = modifier
            .semantics { if (onClick != null) role = Role.Button }
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

        if (!enabled) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)))
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * The Uber Base-aligned card container primitive: background + border + shape + elevation +
 * optional whole-surface tap target + optional dismiss button + loading shimmer + a content slot.
 * Not a finished card pattern — compose your own layout in [content], or reach for
 * `PixaContentCard` (`components/display/ContentCard.kt`) for anatomy slots on top of this.
 *
 * [SurfaceCardContext.Isolated]: large radius, heavier border, elevated by default.
 * [SurfaceCardContext.Feed]: small radius, thin divider border, flat by default.
 *
 * @param onClick Optional whole-card tap target
 * @param selected Accent border for selected state
 * @param onDismiss Optional close button
 * @param backgroundColor Accepts any [Color]; overlays auto-contrast
 * @param cornerRadius / @param elevation Default from [context]; both overridable
 * @param borderColor / @param borderWidth Stroke override
 * @param semanticsRole Overrides the auto [Role.Button] applied when [onClick] is set — e.g.
 * [Role.Checkbox]/[Role.RadioButton] for a selectable card
 */
@Composable
fun PixaSurfaceCard(
    modifier: Modifier = Modifier,
    context: SurfaceCardContext = SurfaceCardContext.Isolated,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    isLoading: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    backgroundColor: Color = AppTheme.colors.baseSurfaceDefault,
    cornerRadius: Dp = context.cornerRadius(),
    elevation: ComponentElevation = context.defaultElevation(),
    borderColor: Color? = null,
    borderWidth: Dp? = null,
    contentPadding: Dp = HierarchicalSize.Spacing.Large,
    semanticsRole: Role? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val shape = RoundedCornerShape(cornerRadius)

    if (isLoading) {
        Skeleton(
            modifier = modifier.fillMaxWidth(),
            height = HierarchicalSize.Card.Medium,
            shape = shape,
            shimmerEnabled = true
        )
        return
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val colors = resolveSurfaceCardColors(
        context = context,
        backgroundColor = backgroundColor,
        enabled = enabled,
        selected = selected,
        isFocused = isFocused
    )

    val resolvedBorderColor = borderColor ?: colors.border
    val resolvedBorderWidth = if (borderColor != null) borderWidth ?: colors.borderWidth else colors.borderWidth

    val animatedBackground by animateColorAsState(colors.background, AnimationUtils.colorSpring, label = "surface_card_background")
    val animatedBorder by animateColorAsState(resolvedBorderColor, AnimationUtils.colorSpring, label = "surface_card_border")

    val overlay = when {
        !enabled -> Color.Transparent
        isPressed -> colors.pressedOverlay
        isHovered -> colors.hoverOverlay
        else -> Color.Transparent
    }

    val clickModifier = if (onClick != null && enabled) {
        Modifier
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusable(enabled = enabled, interactionSource = interactionSource)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation.toDp(),
                shape = shape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(animatedBackground)
            .border(width = resolvedBorderWidth, color = animatedBorder, shape = shape)
            .then(clickModifier)
            .semantics { if (onClick != null) role = semanticsRole ?: Role.Button }
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }

        if (overlay != Color.Transparent) {
            Box(modifier = Modifier.fillMaxSize().background(overlay))
        }

        if (onDismiss != null) {
            SurfaceCardDismissButton(
                onDismiss = onDismiss,
                colors = colors,
                modifier = Modifier.align(Alignment.TopEnd).padding(HierarchicalSize.Spacing.Small)
            )
        }
    }
}

/**
 * Legacy `ConstraintLayout`-based card container. Not the base for new card-like components —
 * see [PixaSurfaceCard] (or `PixaContentCard` in `components/display/ContentCard.kt`) instead.
 * Kept only because `BottomNavBar`, `Stepper`, `Toast`, `Alert`, and `Chart` still build on it.
 *
 * @param variant Visual style
 * @param elevation Shadow depth (Elevated variant only)
 * @param borderConfig / @param shadowConfig Optional stroke/shadow overrides
 * @param content `ConstraintLayoutScope` — use `createRefs()`/`constrainAs()` for positioning
 */
@Composable
fun PixaCard(
    modifier: Modifier = Modifier,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    elevation: ComponentElevation = ComponentElevation.Medium,
    padding: SizeVariant = SizeVariant.Medium,
    cornerRadius: Dp = HierarchicalSize.Radius.Medium,
    backgroundColor: Color? = null,
    borderConfig: CardBorderConfig? = null,
    shadowConfig: CardShadowConfig? = null,
    skeletonShape: Shape? = null,
    content: @Composable ConstraintLayoutScope.() -> Unit
) {
    if (isLoading) {
        Skeleton(
            modifier = modifier.fillMaxWidth(),
            height = 120.dp,
            shape = skeletonShape ?: RoundedCornerShape(cornerRadius),
            shimmerEnabled = true
        )
        return
    }

    val themeColors = getBaseCardTheme(variant, AppTheme.colors)

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

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/** [PixaCard] pinned to [BaseCardVariant.Elevated]. */
@Composable
fun ElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    elevation: ComponentElevation = ComponentElevation.Medium,
    padding: SizeVariant = SizeVariant.Medium,
    cornerRadius: Dp = HierarchicalSize.Radius.Medium,
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

/** [PixaCard] pinned to [BaseCardVariant.Outlined]. */
@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    padding: SizeVariant = SizeVariant.Medium,
    cornerRadius: Dp = HierarchicalSize.Radius.Medium,
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

/** [PixaCard] pinned to [BaseCardVariant.Filled]. */
@Composable
fun FilledCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    padding: SizeVariant = SizeVariant.Medium,
    cornerRadius: Dp = HierarchicalSize.Radius.Medium,
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

/** [PixaCard] pinned to [BaseCardVariant.Ghost]. */
@Composable
fun GhostCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    padding: SizeVariant = SizeVariant.Medium,
    cornerRadius: Dp = HierarchicalSize.Radius.Medium,
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

/** Full-width [PixaCard], standard padding — list/feed items. */
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
    padding = SizeVariant.Medium,
    content = content
)

/** [PixaCard] with compact padding — dense layouts, small info boxes. */
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
    padding = SizeVariant.Compact,
    content = content
)
