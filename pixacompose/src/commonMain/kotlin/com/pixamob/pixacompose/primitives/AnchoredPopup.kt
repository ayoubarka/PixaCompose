package com.pixamob.pixacompose.primitives

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.theme.PixaTheme

internal enum class PopupSide { Top, Bottom }

internal enum class PopupAlign { Start, Center }

/**
 * Transparent margin inside the popup window so shadows are not cut at its edge.
 * The margin is part of the popup, so taps on it are forwarded to the dismiss handler, and
 * passive popups (tooltips) get none: it would sit over the anchor and steal its hover.
 */
private val ShadowRoom = 32.dp

/**
 * A popup attached to the layout it is declared in (its anchor). Shared by PixaPopover, PixaMenu and
 * PixaTooltip. Flips to the other side when there is no room and stays inside the window.
 *
 * @param onDismissRequest null makes the popup passive: it takes no focus and no outside taps.
 */
@Composable
internal fun AnchoredPopup(
    visible: Boolean,
    onDismissRequest: (() -> Unit)?,
    side: PopupSide,
    align: PopupAlign,
    content: @Composable () -> Unit,
) {
    val state = remember { MutableTransitionState(false) }
    state.targetState = visible
    if (!state.currentState && !state.targetState) return

    val density = LocalDensity.current
    val gap = with(density) { PixaTheme.spacing.sm.roundToPx() }
    val margin = if (onDismissRequest != null) ShadowRoom else 0.dp
    val room = with(density) { margin.roundToPx() }
    val spec = PixaTheme.motion.overlay
    Popup(
        popupPositionProvider = remember(side, align, gap, room) { AnchoredPositionProvider(side, align, gap, room) },
        onDismissRequest = onDismissRequest,
        // The shadow margin may hang outside the window; the position provider keeps the panel inside.
        properties = PopupProperties(focusable = onDismissRequest != null, clippingEnabled = false),
    ) {
        AnimatedVisibility(
            visibleState = state,
            enter = fadeIn(spec.spec()) + scaleIn(spec.spec(), initialScale = 0.92f),
            exit = fadeOut(spec.spec()) + scaleOut(spec.spec(), targetScale = 0.92f),
        ) {
            Box(
                Modifier
                    .pointerInput(onDismissRequest) { detectTapGestures { onDismissRequest?.invoke() } }
                    .padding(margin)
                    // Taps on the panel itself must not dismiss it.
                    .pointerInput(Unit) { detectTapGestures { } },
            ) { content() }
        }
    }
}

private class AnchoredPositionProvider(
    private val side: PopupSide,
    private val align: PopupAlign,
    private val gap: Int,
    private val room: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        // The visible panel is the popup content minus the shadow margin on every side.
        val width = popupContentSize.width - room * 2
        val height = popupContentSize.height - room * 2

        val preferredX = when (align) {
            PopupAlign.Center -> anchorBounds.left + (anchorBounds.width - width) / 2
            PopupAlign.Start ->
                if (layoutDirection == LayoutDirection.Ltr) anchorBounds.left else anchorBounds.right - width
        }
        val x = preferredX.coerceIn(0, (windowSize.width - width).coerceAtLeast(0))

        val below = anchorBounds.bottom + gap
        val above = anchorBounds.top - gap - height
        val fitsBelow = below + height <= windowSize.height
        val y = when (side) {
            PopupSide.Bottom -> if (fitsBelow || above < 0) below else above
            PopupSide.Top -> if (above >= 0 || !fitsBelow) above else below
        }
        return IntOffset(x - room, y - room)
    }
}
