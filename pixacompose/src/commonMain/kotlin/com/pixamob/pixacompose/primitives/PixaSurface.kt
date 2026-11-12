package com.pixamob.pixacompose.primitives

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.pixamob.pixacompose.theme.PixaShadow
import com.pixamob.pixacompose.theme.PixaTheme
import com.pixamob.pixacompose.theme.PressEffect

@Immutable
public data class PixaBorder(val width: Dp, val color: Color)

internal const val DisabledAlpha: Float = 0.5f

/**
 * The one place where fill, outline, shadow and press feedback are drawn. Every component that
 * has a background is built on this, which is what lets a style change all of them at once.
 */
@Composable
public fun PixaSurface(
    modifier: Modifier = Modifier,
    color: Color = PixaTheme.colors.card,
    contentColor: Color = PixaTheme.colors.cardForeground,
    shape: Shape = PixaTheme.shapes.lg,
    border: PixaBorder? = null,
    shadow: PixaShadow = PixaShadow.None,
    /** Draws the focus ring; for components that track focus themselves, such as text fields. */
    focused: Boolean = false,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.surface(color, shape, border, shadow, PressEffect.None, focused) { 0f },
        contentAlignment = contentAlignment,
    ) {
        ProvideContentStyle(contentColor) { content() }
    }
}

/** Clickable [PixaSurface]. Press feedback comes from the style's [PressEffect], not a ripple. */
@Composable
public fun PixaSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: Role? = null,
    color: Color = PixaTheme.colors.card,
    contentColor: Color = PixaTheme.colors.cardForeground,
    shape: Shape = PixaTheme.shapes.lg,
    border: PixaBorder? = null,
    shadow: PixaShadow = PixaShadow.None,
    press: PressEffect = PixaTheme.decoration.press,
    interactionSource: MutableInteractionSource? = null,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    InteractiveSurface(
        modifier, enabled, source,
        Modifier.clickable(source, indication = null, enabled = enabled, role = role, onClick = onClick),
        color, contentColor, shape, border, shadow, press, contentAlignment, content,
    )
}

/** Two-state [PixaSurface] for checkboxes and switches. */
@Composable
public fun PixaSurface(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: Role? = null,
    color: Color = PixaTheme.colors.card,
    contentColor: Color = PixaTheme.colors.cardForeground,
    shape: Shape = PixaTheme.shapes.lg,
    border: PixaBorder? = null,
    shadow: PixaShadow = PixaShadow.None,
    press: PressEffect = PixaTheme.decoration.press,
    interactionSource: MutableInteractionSource? = null,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    InteractiveSurface(
        modifier, enabled, source,
        Modifier.toggleable(checked, source, indication = null, enabled = enabled, role = role, onValueChange = onCheckedChange),
        color, contentColor, shape, border, shadow, press, contentAlignment, content,
    )
}

/** One-of-many [PixaSurface] for radio buttons, tabs and other single-selection items. */
@Composable
public fun PixaSurface(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: Role? = null,
    color: Color = PixaTheme.colors.card,
    contentColor: Color = PixaTheme.colors.cardForeground,
    shape: Shape = PixaTheme.shapes.lg,
    border: PixaBorder? = null,
    shadow: PixaShadow = PixaShadow.None,
    press: PressEffect = PixaTheme.decoration.press,
    interactionSource: MutableInteractionSource? = null,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    InteractiveSurface(
        modifier, enabled, source,
        Modifier.selectable(selected, source, indication = null, enabled = enabled, role = role, onClick = onClick),
        color, contentColor, shape, border, shadow, press, contentAlignment, content,
    )
}

@Composable
private fun InteractiveSurface(
    modifier: Modifier,
    enabled: Boolean,
    source: MutableInteractionSource,
    interaction: Modifier,
    color: Color,
    contentColor: Color,
    shape: Shape,
    border: PixaBorder?,
    shadow: PixaShadow,
    press: PressEffect,
    contentAlignment: Alignment,
    content: @Composable BoxScope.() -> Unit,
) {
    val pressed by source.collectIsPressedAsState()
    val focused by source.collectIsFocusedAsState()
    val pressFraction by animateFloatAsState(
        targetValue = if (pressed && enabled) 1f else 0f,
        animationSpec = PixaTheme.motion.press.spec(),
    )
    Box(
        modifier = modifier
            // Disabled surfaces are flat: the alpha layer clips to bounds, which would cut a shadow.
            .graphicsLayer { alpha = if (enabled) 1f else DisabledAlpha }
            .surface(color, shape, border, if (enabled) shadow else PixaShadow.None, press, focused) { pressFraction }
            .then(interaction),
        contentAlignment = contentAlignment,
    ) {
        ProvideContentStyle(contentColor) { content() }
    }
}

@Composable
private fun Modifier.surface(
    color: Color,
    shape: Shape,
    border: PixaBorder?,
    shadow: PixaShadow,
    press: PressEffect,
    focused: Boolean,
    pressFraction: () -> Float,
): Modifier {
    val focusRing = PixaTheme.decoration.focusRing
    val ringColor = PixaTheme.colors.ring
    val ring = if (focused && focusRing.width > 0.dp) {
        Modifier.drawBehind {
            // The ring is the surface's own shape, grown outward by the offset.
            val inset = focusRing.offset.toPx() + focusRing.width.toPx() / 2
            val outline = shape.createOutline(
                Size(size.width + inset * 2, size.height + inset * 2),
                layoutDirection,
                this,
            )
            translate(-inset, -inset) {
                drawOutline(outline, ringColor, style = Stroke(focusRing.width.toPx()))
            }
        }
    } else {
        Modifier
    }

    val fallbackShadowColor = border?.color ?: PixaTheme.colors.border
    val sinks = press is PressEffect.Sink && shadow is PixaShadow.Hard

    val pressed = when (press) {
        PressEffect.None -> Modifier
        is PressEffect.Scale -> Modifier.graphicsLayer {
            val s = lerp(1f, press.scale, pressFraction())
            scaleX = s
            scaleY = s
        }
        is PressEffect.Dim -> Modifier.graphicsLayer { alpha = lerp(1f, press.alpha, pressFraction()) }
        PressEffect.Sink -> if (shadow is PixaShadow.Hard) {
            Modifier.graphicsLayer {
                translationX = shadow.offset.x.toPx() * pressFraction()
                translationY = shadow.offset.y.toPx() * pressFraction()
            }
        } else {
            Modifier
        }
    }

    val shadowed = when (shadow) {
        PixaShadow.None -> Modifier
        is PixaShadow.Soft -> Modifier.shadow(shadow.elevation, shape, clip = false)
        is PixaShadow.Hard -> {
            val shadowColor = shadow.color.takeOrElse { fallbackShadowColor }
            Modifier.drawBehind {
                // While sinking, the surface travels toward the shadow, so the shadow shrinks.
                val remaining = if (sinks) 1f - pressFraction() else 1f
                val outline = shape.createOutline(size, layoutDirection, this)
                translate(shadow.offset.x.toPx() * remaining, shadow.offset.y.toPx() * remaining) {
                    drawOutline(outline, shadowColor)
                }
            }
        }
    }

    return this
        .then(pressed)
        .then(shadowed)
        .then(ring)
        .background(color, shape)
        .then(if (border != null && border.width.value > 0f) Modifier.border(border.width, border.color, shape) else Modifier)
        .clip(shape)
}
