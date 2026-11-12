package com.pixamob.pixacompose.primitives

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Full-screen modal layer shared by Dialog and Sheet. The platform supplies the scrim, focus
 * trapping and back/escape handling; this adds placement, tap-outside dismissal and enter/exit
 * animation. It stays composed after [visible] turns false until the exit animation ends.
 */
@Composable
internal fun Modal(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    contentAlignment: Alignment,
    enter: EnterTransition,
    exit: ExitTransition,
    content: @Composable () -> Unit,
) {
    val state = remember { MutableTransitionState(false) }
    state.targetState = visible
    if (!state.currentState && !state.targetState) return

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(onDismissRequest) { detectTapGestures { onDismissRequest() } },
            contentAlignment = contentAlignment,
        ) {
            AnimatedVisibility(visibleState = state, enter = enter, exit = exit) {
                // Swallow taps on the panel itself so they do not reach the dismiss handler.
                Box(Modifier.pointerInput(Unit) { detectTapGestures { } }) { content() }
            }
        }
    }
}
