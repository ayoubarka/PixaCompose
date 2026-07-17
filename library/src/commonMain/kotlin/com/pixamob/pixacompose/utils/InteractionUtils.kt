package com.pixamob.pixacompose.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.theme.LocalColorPalette
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.max

/**
 * Canonical press/hover/focus feedback for every interactive Pixa component —
 * the library's own [Indication], and the reason no component needs to import
 * `androidx.compose.material3.ripple`.
 *
 * This exists for the same reason the rest of the library avoids Material 3:
 * `ripple()` is a Material 3 *interaction* dependency that drags Material's
 * defaults (and its versioning churn) into every clickable surface in the
 * design system. [pixaRipple] is a drop-in replacement — same parameter names,
 * same alpha model (see below) — so components express interaction feedback in
 * Pixa's own vocabulary.
 *
 * ## Alpha model
 *
 * [color] contributes **hue only** — its alpha is replaced by the relevant
 * [PixaRippleAlpha] state alpha, exactly as Material 3's ripple treats its own
 * `color` parameter. This is deliberate rather than incidental: call sites
 * across the library pass pre-attenuated colors (`content.copy(alpha = 0.12f)`)
 * whose alpha Material was already discarding, so multiplying instead of
 * replacing would make every ripple in the library ~8x fainter than it ships
 * today. Preserve the replace.
 *
 * When [color] is unspecified it falls back to `baseContentBody` from the
 * ambient [LocalColorPalette], so a ripple always tracks the active theme
 * rather than a hardcoded neutral.
 *
 * ## Known differences from Material's ripple
 *
 * The alpha model and radius geometry match, but the animation is deliberately
 * simpler in two ways. Both are acceptable for this library's flat, low-alpha
 * ripples; neither is worth Material's machinery:
 * - **One ripple at a time.** A second press restarts the circle from the new
 *   press point rather than compositing two expanding circles.
 * - **A fast tap can fade before it finishes expanding** — release starts the
 *   fade immediately instead of waiting for the expansion to complete, so a
 *   very quick tap reads as a smaller flash.
 *
 * @param bounded whether the ripple is clipped to the component's bounds.
 *   Bounded ripples expand from the press point to the farthest corner;
 *   unbounded ones are circular and may overflow (icon buttons, checkboxes).
 * @param radius explicit target radius. When unspecified it's derived from the
 *   component's size and the press position (see [bounded]).
 * @param color base color, before [PixaRippleAlpha] is applied.
 */
fun pixaRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = Color.Unspecified
): Indication = PixaRipple(bounded, radius, color)

/**
 * State alphas applied on top of a ripple's base color, mirroring the
 * proportions Material 3 uses so that migrating a component off `ripple()`
 * is not also a visual change.
 */
object PixaRippleAlpha {
    const val Pressed = 0.12f
    const val Focused = 0.12f
    const val Dragged = 0.16f
    const val Hovered = 0.08f
}

@Immutable
private data class PixaRipple(
    private val bounded: Boolean,
    private val radius: Dp,
    private val color: Color
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        PixaRippleNode(interactionSource, bounded, radius, color)
}

/**
 * Draws the press ripple plus the hover/focus/drag state layer.
 *
 * The two are independent: the state layer is a flat wash that persists for as
 * long as the pointer/focus rests on the component, while the press ripple is a
 * one-shot expanding circle. Both read their animation values as snapshot state
 * inside [draw], which is what schedules the redraws — there is no explicit
 * invalidation loop.
 */
private class PixaRippleNode(
    private val interactionSource: InteractionSource,
    private val bounded: Boolean,
    private val radius: Dp,
    private val color: Color
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private val expansion = Animatable(0f)
    private val pressAlpha = Animatable(0f)
    private val stateAlpha = Animatable(0f)

    private var pressPosition: Offset? = null
    private var hovered = false
    private var focused = false
    private var dragged = false

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        pressPosition = interaction.pressPosition
                        // Each press restarts the ripple from the new press point.
                        // Launched separately so a release arriving mid-expansion
                        // isn't blocked behind this animation completing.
                        coroutineScope.launch {
                            expansion.snapTo(0f)
                            pressAlpha.snapTo(1f)
                            expansion.animateTo(1f, AnimationUtils.standardTween())
                        }
                    }

                    is PressInteraction.Release, is PressInteraction.Cancel -> {
                        coroutineScope.launch { pressAlpha.animateTo(0f, AnimationUtils.fastTween()) }
                    }

                    is HoverInteraction.Enter -> { hovered = true; syncStateLayer() }
                    is HoverInteraction.Exit -> { hovered = false; syncStateLayer() }
                    is FocusInteraction.Focus -> { focused = true; syncStateLayer() }
                    is FocusInteraction.Unfocus -> { focused = false; syncStateLayer() }
                    is DragInteraction.Start -> { dragged = true; syncStateLayer() }
                    is DragInteraction.Stop, is DragInteraction.Cancel -> { dragged = false; syncStateLayer() }
                }
            }
        }
    }

    /**
     * Collapses the overlapping hover/focus/drag flags into the single strongest
     * wash, so releasing one state falls back to the next rather than to nothing.
     */
    private fun syncStateLayer() {
        val target = when {
            dragged -> PixaRippleAlpha.Dragged
            focused -> PixaRippleAlpha.Focused
            hovered -> PixaRippleAlpha.Hovered
            else -> 0f
        }
        coroutineScope.launch { stateAlpha.animateTo(target, AnimationUtils.fastTween()) }
    }

    /**
     * Bounded ripples must reach the farthest corner from the press point, so
     * the fill covers the whole surface no matter where the press landed.
     */
    private fun DrawScope.resolveTargetRadius(position: Offset): Float {
        if (radius != Dp.Unspecified) return radius.toPx()
        return if (bounded) {
            hypot(
                max(position.x, size.width - position.x),
                max(position.y, size.height - position.y)
            )
        } else {
            size.maxDimension / 2f
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        val base = if (color.isSpecified) color else currentValueOf(LocalColorPalette).baseContentBody

        val wash = stateAlpha.value
        if (wash > 0f) {
            drawRect(color = base.copy(alpha = wash))
        }

        val alpha = pressAlpha.value
        val position = pressPosition
        if (alpha > 0f && position != null) {
            val rippleColor = base.copy(alpha = PixaRippleAlpha.Pressed * alpha)
            val currentRadius = resolveTargetRadius(position) * expansion.value
            if (bounded) {
                clipRect { drawCircle(color = rippleColor, radius = currentRadius, center = position) }
            } else {
                drawCircle(color = rippleColor, radius = currentRadius, center = position)
            }
        }
    }
}
