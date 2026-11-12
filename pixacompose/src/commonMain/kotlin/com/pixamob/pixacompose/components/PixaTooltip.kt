package com.pixamob.pixacompose.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import com.pixamob.pixacompose.primitives.AnchoredPopup
import com.pixamob.pixacompose.primitives.PopupAlign
import com.pixamob.pixacompose.primitives.PopupSide
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme
import kotlinx.coroutines.delay

private const val HoverDelayMillis = 400L
private const val TouchVisibleMillis = 1500L

/**
 * Shows [tooltip] above [content] while the pointer rests on it, or after a long press on
 * touch screens. The wrapped content keeps working normally.
 */
@Composable
public fun PixaTooltip(
    tooltip: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    var hoverShown by remember { mutableStateOf(false) }
    var touchShown by remember { mutableStateOf(false) }

    LaunchedEffect(hovered) {
        if (hovered) delay(HoverDelayMillis)
        hoverShown = hovered
    }
    LaunchedEffect(touchShown) {
        if (touchShown) {
            delay(TouchVisibleMillis)
            touchShown = false
        }
    }

    Box(
        modifier
            .hoverable(source)
            .pointerInput(Unit) {
                // Watch on the initial pass without consuming, so the content still gets its taps.
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    if (down.type == PointerType.Mouse) return@awaitEachGesture
                    try {
                        withTimeout(viewConfiguration.longPressTimeoutMillis) {
                            waitForUpOrCancellation(PointerEventPass.Initial)
                        }
                    } catch (_: PointerEventTimeoutCancellationException) {
                        touchShown = true
                    }
                }
            },
    ) {
        content()
        AnchoredPopup(hoverShown || touchShown, onDismissRequest = null, PopupSide.Top, PopupAlign.Center) {
            val colors = PixaTheme.colors
            PixaSurface(
                color = colors.foreground,
                contentColor = colors.background,
                shape = PixaTheme.shapes.sm,
            ) {
                ProvideContentStyle(colors.background, PixaTheme.typography.caption) {
                    Box(Modifier.padding(horizontal = PixaTheme.spacing.sm, vertical = PixaTheme.spacing.xs)) {
                        tooltip()
                    }
                }
            }
        }
    }
}
