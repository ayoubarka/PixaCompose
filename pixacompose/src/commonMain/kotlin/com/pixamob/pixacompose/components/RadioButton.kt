package com.pixamob.pixacompose.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaTheme

private val OuterSize = 20.dp
private val DotSize = 10.dp

/** One option in a single-choice group. The group owns the state and passes [selected]. */
@Composable
public fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = PixaTheme.colors
    val dot by animateFloatAsState(if (selected) 1f else 0f, PixaTheme.motion.state.spec())
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.size(OuterSize),
        enabled = enabled,
        role = Role.RadioButton,
        color = colors.background,
        shape = PixaTheme.shapes.full,
        border = outlinedBorder(if (selected) colors.primary else colors.input),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(DotSize)
                .graphicsLayer {
                    scaleX = dot
                    scaleY = dot
                }
                .background(colors.primary, PixaTheme.shapes.full),
        )
    }
}
