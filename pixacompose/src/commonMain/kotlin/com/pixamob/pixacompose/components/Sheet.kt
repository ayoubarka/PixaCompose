package com.pixamob.pixacompose.components

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.Modal
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * The panel extends this far below the screen edge. It hides the bottom corners, border and
 * shadow whatever the style's shape is, and covers the gap when a bouncy style overshoots.
 */
private val Overhang = 48.dp

/** A modal panel that slides up from the bottom edge. Drive it with [visible], like [Dialog]. */
@Composable
public fun Sheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spec = PixaTheme.motion.overlay
    val spacing = PixaTheme.spacing
    Modal(
        visible = visible,
        onDismissRequest = onDismissRequest,
        contentAlignment = Alignment.BottomCenter,
        enter = slideInVertically(spec.spec()) { it },
        exit = slideOutVertically(spec.spec()) { it },
    ) {
        Surface(
            modifier = modifier.widthIn(max = 640.dp).fillMaxWidth().offset(y = Overhang),
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
