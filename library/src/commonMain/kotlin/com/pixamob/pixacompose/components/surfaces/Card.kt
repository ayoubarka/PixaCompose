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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.contrastColor
import com.pixamob.pixacompose.utils.toDp

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * The two card contexts Uber Base's Card spec distinguishes structurally, not just visually.
 * Drives corner radius and the resting border weight — see [PixaSurfaceCard] docs.
 */
enum class SurfaceCardContext {
    /** "Inset with a corner radius, so they stand out from the rest of the content." Large radius, heavier separating border, elevated by default. */
    Isolated,

    /** "Full-width... with a module divider to separate individual cards." Small radius, thin internal divider border, flat by default. */
    Feed
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

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER (state/context resolvers)
// ════════════════════════════════════════════════════════════════════════════

/**
 * Resolves colors from the Uber Base states table:
 * Enabled -> `backgroundPrimary` + 2dp opaque border; Focus/Selected -> 3dp accent border
 * (both approximated with `brandBorderFocus`, the closest existing token — the theme has no
 * separate "borderSelected" token, same substitution [com.pixamob.pixacompose.components.inputs.PixaCheckboxBox]
 * already makes); Disabled -> disabled surface/border tokens. Hover/pressed use the spec's literal
 * 4%/8% black overlay percentages rather than theme tokens, flipped to white on dark backgrounds so
 * a caller-supplied [backgroundColor] (see [PixaSurfaceCard.backgroundColor]) stays legible in
 * either mode — same technique [com.pixamob.pixacompose.components.display.PixaMessageCard] uses.
 *
 * Context only decides the resting (enabled, unfocused, unselected) border weight: 4dp for
 * [SurfaceCardContext.Isolated] ("4px outside weight") vs 1dp for [SurfaceCardContext.Feed]
 * ("1px inside weight" / module divider) — both exact spec matches via [HierarchicalSize.Border].
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

/** Corner radius per context: 24dp isolated (closest `HierarchicalSize.Radius` tier to the spec's oversized 48px flagship radius), 12dp feed (exact spec match). */
private fun SurfaceCardContext.cornerRadius(): Dp = when (this) {
    SurfaceCardContext.Isolated -> HierarchicalSize.Radius.Massive
    SurfaceCardContext.Feed -> HierarchicalSize.Radius.Large
}

/** Isolated cards float above content by default (drop shadow); feed cards rely on their divider border, matching the spec's flatter full-width treatment. */
private fun SurfaceCardContext.defaultElevation(): ComponentElevation = when (this) {
    SurfaceCardContext.Isolated -> ComponentElevation.High
    SurfaceCardContext.Feed -> ComponentElevation.None
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL <NAME>
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
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onDismiss
            )
            .semantics {
                // Voice label intentionally omitted per spec ("Close icon: voice label
                // excluded; implicit in actions menu"); Role.Button alone still exposes it
                // as an actionable element to screen readers.
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        // No dedicated close-icon asset exists in the library yet (same gap noted in
        // PixaMessageCard's dismiss button) — a glyph avoids a new icon dependency for one glyph.
        BasicText(
            text = "×",
            style = AppTheme.typography.titleBold.copy(color = colors.dismissContent, textAlign = TextAlign.Center)
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaSurfaceCard - the foundational card surface.
 *
 * This is a **primitive**, not a finished card pattern: it renders only the container
 * (surface, shape, border, elevation, states, tap target, optional dismiss button, loading
 * shimmer). It does not implement the spec's tiered anatomy (eyebrow/logo/headline/paragraph/
 * media/custom/button layer-cake) — per the spec's own "build-your-own" model ("individual card
 * elements stacked per layer-cake model"), those tiers are meant to be composed by callers using
 * existing Pixa primitives ([content]), or by dedicated future components (e.g. a media-forward
 * card, a list-item card) built on top of this primitive. [com.pixamob.pixacompose.components.display.PixaCard]
 * is the library's earlier, unrelated generic card system — kept for backward compatibility (see
 * its file header) but not the foundation new card-like components should build on; this is.
 *
 * Purpose: "a contained unit of information related to a topic... communicate a state within a
 * feed of cards."
 *
 * Anatomy: this primitive owns the container only — background, border, shape, elevation, the
 * whole-surface tap target, an optional [onDismiss] close affordance, and a [content] slot for
 * whatever the caller stacks inside.
 *
 * Variants: [context] — [SurfaceCardContext.Isolated] ("stand out from the rest of the content",
 * large radius, heavier separating border, elevated) vs [SurfaceCardContext.Feed] ("full-width...
 * with a module divider", small radius, thin internal divider border, flat).
 *
 * States: enabled, disabled, hover (4% black / 10% white overlay), pressed (8% black / 20% white
 * overlay), focus (3dp accent border), [selected] (3dp accent border — approximated with the same
 * `brandBorderFocus` token as focus; the theme has no separate "borderSelected" token), and
 * [isLoading] (renders a [Skeleton] shimmer in place of [content], per "all elements in the card
 * become a Placeholder shimmer").
 *
 * Sizing: [cornerRadius] defaults from [context] (24dp isolated / 12dp feed, the latter an exact
 * spec match). [elevation] defaults from [context] too; the spec's literal "16px blur, 4px Y,
 * 12%-black" shadow isn't expressible through Compose's elevation-driven (not blur-driven) shadow
 * model, so the `ambientColor`/`spotColor` are pinned to the exact 12%-black while the elevation
 * `Dp` is the closest [ComponentElevation] stand-in for the Y-offset, mirroring how
 * [com.pixamob.pixacompose.components.display.PixaMessageCard] handles the same gap.
 *
 * Adaptive behavior: out of scope — the spec's breakpoint guidance (4-column narrow, gridded
 * medium, 3-column large) is about the surrounding grid/layout, not this card's own internal
 * sizing, so it doesn't map onto `WindowSizeClass`. Callers control column span in their own
 * layout; the card fills whatever width it's given.
 *
 * Customization: [backgroundColor] intentionally accepts any [Color] (not just semantic
 * `AppTheme.colors.*` tokens) — like [com.pixamob.pixacompose.components.display.PixaMessageCard],
 * this primitive must stay legible under arbitrary caller-chosen backgrounds, so state overlays
 * and the dismiss chip auto-contrast off the resolved background color.
 *
 * Usage notes: whole-card tap is the only supported interaction surface (single destination per
 * spec — "keep the card simple and focused on a single topic with a single destination when
 * possible"); [onDismiss], when set, remains independently tappable above the card's own tap
 * target. Per spec, prefer this primitive over a plain list row only when the content genuinely
 * needs a heading+paragraph card treatment — "lists... take up less space... ideal for search
 * results, settings" is still the better fit for those.
 *
 * @param modifier Modifier for the card
 * @param context [SurfaceCardContext.Isolated] or [SurfaceCardContext.Feed]; drives the default corner radius, resting border weight, and elevation
 * @param onClick Optional click handler for the whole card surface
 * @param enabled Disabled state (renders the disabled surface/border tokens, disables tap targets)
 * @param selected Renders the 3dp accent border used for both focus and selection per spec
 * @param isLoading Shows a [Skeleton] shimmer in place of [content]
 * @param onDismiss Optional dismiss handler; the close affordance is omitted entirely when null
 * @param backgroundColor Card background; accepts any [Color], see Customization above
 * @param cornerRadius Corner radius override; defaults to [context]'s spec radius
 * @param elevation Shadow elevation override; defaults to [context]'s spec treatment
 * @param borderColor Border color override; defaults to the resolved enabled/focus/selected/disabled
 * state color. Callers whose border decision depends on their own background (e.g. a card that only
 * shows a stroke on light backgrounds, per the spec's "card with white background artwork: add
 * stroke; with high-contrast background: optional stroke removal") should resolve that externally
 * and pass it here rather than duplicating this primitive's state-border logic.
 * @param borderWidth Border width override; only consulted when [borderColor] is non-null, defaults to the resolved state width
 * @param contentPadding Padding around [content]
 * @param content Card body content; compose your own eyebrow/heading/media/button layer-cake here
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
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
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
            .semantics { if (onClick != null) role = Role.Button }
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
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(HierarchicalSize.Spacing.Small)
            )
        }
    }
}
