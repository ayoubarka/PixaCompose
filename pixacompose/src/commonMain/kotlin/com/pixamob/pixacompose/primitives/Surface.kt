package com.pixamob.pixacompose.primitives

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.lerp
import com.pixamob.pixacompose.theme.PixaShadow
import com.pixamob.pixacompose.theme.PixaTheme
import com.pixamob.pixacompose.theme.PressEffect

@Immutable
public data class PixaBorder(val width: Dp, val color: Color)

private const val DisabledAlpha = 0.5f

/**
 * The one place where fill, outline, shadow and press feedback are drawn. Every component that
 * has a background is built on this, which is what lets a style change all of them at once.
 */
@Composable
public fun Surface(
    modifier: Modifier = Modifier,
    color: Color = PixaTheme.colors.card,
    contentColor: Color = PixaTheme.colors.cardForeground,
    shape: Shape = PixaTheme.shapes.lg,
    border: PixaBorder? = null,
    shadow: PixaShadow = PixaShadow.None,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.surface(color, shape, border, shadow, PressEffect.None) { 0f },
        contentAlignment = contentAlignment,
    ) {
        ProvideContentStyle(contentColor) { content() }
    }
}

/** Clickable [Surface]. Press feedback comes from the style's [PressEffect], not a ripple. */
@Composable
public fun Surface(
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
    val pressed by source.collectIsPressedAsState()
    val pressFraction by animateFloatAsState(
        targetValue = if (pressed && enabled) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 900f),
    )
    Box(
        modifier = modifier
            // Disabled surfaces are flat: the alpha layer clips to bounds, which would cut a shadow.
            .graphicsLayer { alpha = if (enabled) 1f else DisabledAlpha }
            .surface(color, shape, border, if (enabled) shadow else PixaShadow.None, press) { pressFraction }
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = role,
                onClick = onClick,
            ),
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
    pressFraction: () -> Float,
): Modifier {
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
        is PixaShadow.Glow -> {
            val glowColor = shadow.color.takeOrElse { border?.color ?: color }
            Modifier.dropShadow(shape, Shadow(radius = shadow.radius, color = glowColor, spread = shadow.spread))
        }
    }

    return this
        .then(pressed)
        .then(shadowed)
        .background(color, shape)
        .then(if (border != null && border.width.value > 0f) Modifier.border(border.width, border.color, shape) else Modifier)
        .clip(shape)
}
