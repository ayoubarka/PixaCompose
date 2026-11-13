package com.pixamob.pixacompose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme
import kotlinx.coroutines.delay

public enum class PixaToastVariant { Default, Success, Destructive }

/** Screen edge the toasts come from. The newest toast is the one nearest that edge. */
public enum class PixaToastPosition { Top, Bottom }

@Immutable
public class PixaToastData internal constructor(
    internal val id: Long,
    public val title: String,
    public val description: String?,
    public val variant: PixaToastVariant,
    public val durationMillis: Long,
)

private const val MaxVisibleToasts = 3

/**
 * Queue of short messages. Create one with [rememberPixaToastState], place a single [PixaToastHost]
 * at the root of the screen, and call [show] from anywhere that holds the state.
 */
@Stable
public class PixaToastState {
    internal val toasts = mutableStateListOf<PixaToastData>()
    private var nextId = 0L

    public fun show(
        title: String,
        description: String? = null,
        variant: PixaToastVariant = PixaToastVariant.Default,
        durationMillis: Long = 4000,
    ) {
        toasts += PixaToastData(nextId++, title, description, variant, durationMillis)
        while (toasts.size > MaxVisibleToasts) toasts.removeAt(0)
    }

    internal fun remove(toast: PixaToastData) {
        toasts.remove(toast)
    }
}

@Composable
public fun rememberPixaToastState(): PixaToastState = remember { PixaToastState() }

/**
 * Draws the toasts of [state]. Align it yourself in the root layout to match [position], for
 * example `Modifier.align(Alignment.TopCenter)` inside a `Box` for [PixaToastPosition.Top].
 *
 * @param offset extra distance from that edge, for example the height of a top bar the toasts
 * should not cover.
 */
@Composable
public fun PixaToastHost(
    state: PixaToastState,
    modifier: Modifier = Modifier,
    position: PixaToastPosition = PixaToastPosition.Bottom,
    offset: Dp = 0.dp,
) {
    val top = position == PixaToastPosition.Top
    Column(
        modifier = modifier
            .padding(top = if (top) offset else 0.dp, bottom = if (top) 0.dp else offset)
            .padding(PixaTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(PixaTheme.spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val toasts = if (top) state.toasts.asReversed() else state.toasts
        toasts.forEach { toast ->
            key(toast.id) { ToastItem(toast, fromTop = top, onGone = { state.remove(toast) }) }
        }
    }
}

@Composable
private fun ToastItem(toast: PixaToastData, fromTop: Boolean, onGone: () -> Unit) {
    val visibility = remember { MutableTransitionState(false).apply { targetState = true } }
    LaunchedEffect(Unit) {
        delay(toast.durationMillis)
        visibility.targetState = false
    }
    if (visibility.isIdle && !visibility.currentState) {
        LaunchedEffect(Unit) { onGone() }
    }

    val colors = PixaTheme.colors
    val spec = PixaTheme.motion.overlay
    val destructive = toast.variant == PixaToastVariant.Destructive
    val slide = if (fromTop) -1 else 1
    AnimatedVisibility(
        visibleState = visibility,
        enter = fadeIn(spec.spec()) + slideInVertically(spec.spec()) { slide * it / 2 },
        exit = fadeOut(spec.spec()) + slideOutVertically(spec.spec()) { slide * it / 2 },
    ) {
        PixaSurface(
            modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth(),
            color = if (destructive) colors.destructive else colors.card,
            contentColor = if (destructive) colors.destructiveForeground else colors.cardForeground,
            shape = PixaTheme.shapes.md,
            border = if (destructive) filledBorder() else outlinedBorder(),
            shadow = PixaTheme.decoration.containerShadow,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = PixaTheme.spacing.lg, vertical = PixaTheme.spacing.md),
                horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (toast.variant == PixaToastVariant.Success) {
                    PixaSurface(
                        modifier = Modifier.size(24.dp),
                        color = colors.success,
                        contentColor = colors.successForeground,
                        shape = PixaTheme.shapes.full,
                        border = filledBorder(),
                        contentAlignment = Alignment.Center,
                    ) { PixaIcon(PixaIcons.Check, contentDescription = null, size = 14.dp) }
                }
                Column(verticalArrangement = Arrangement.spacedBy(PixaTheme.spacing.xs)) {
                    PixaText(toast.title, style = PixaTheme.typography.label)
                    if (toast.description != null) {
                        PixaText(
                            toast.description,
                            style = PixaTheme.typography.bodySmall,
                            color = if (destructive) colors.destructiveForeground else colors.mutedForeground,
                        )
                    }
                }
            }
        }
    }
}
