package com.pixamob.pixacompose.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.Modal
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * The panel extends this far below the screen edge. It hides the bottom corners, border and
 * shadow whatever the style's shape is, and covers the gap when a bouncy style overshoots.
 */
private val Overhang = 48.dp

/** Downward speed, in pixels per second, that dismisses the sheet however far it has moved. */
private const val FlingDismissVelocity = 1800f

/** A modal panel that slides up from the bottom edge. Drive it with [visible], like [PixaDialog]. */
@Composable
public fun PixaSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spec = PixaTheme.motion.overlay
    val spacing = PixaTheme.spacing
    // Dragging down moves the panel with the finger; past a third of its height, or with a
    // fling, it dismisses. Otherwise it settles back.
    val drag = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var height by remember { mutableIntStateOf(0) }
    LaunchedEffect(visible) { if (visible) drag.snapTo(0f) }
    Modal(
        visible = visible,
        onDismissRequest = onDismissRequest,
        contentAlignment = Alignment.BottomCenter,
        enter = slideInVertically(spec.spec()) { it },
        exit = slideOutVertically(spec.spec()) { it },
    ) {
        PixaSurface(
            modifier = modifier
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .offset(y = Overhang)
                .offset { IntOffset(0, drag.value.roundToInt()) }
                .onSizeChanged { height = it.height }
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        scope.launch { drag.snapTo((drag.value + delta).coerceAtLeast(0f)) }
                    },
                    onDragStopped = { velocity ->
                        if (drag.value > height / 3f || velocity > FlingDismissVelocity) {
                            onDismissRequest()
                        } else {
                            drag.animateTo(0f, spec.spec())
                        }
                    },
                ),
            border = outlinedBorder(),
            shadow = PixaTheme.decoration.containerShadow,
        ) {
            Column(
                modifier = Modifier.padding(
                    start = spacing.xl,
                    end = spacing.xl,
                    top = spacing.md,
                    bottom = spacing.xl + Overhang,
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 40.dp, height = 4.dp)
                        .background(PixaTheme.colors.border, PixaTheme.shapes.full),
                )
                content()
            }
        }
    }
}
