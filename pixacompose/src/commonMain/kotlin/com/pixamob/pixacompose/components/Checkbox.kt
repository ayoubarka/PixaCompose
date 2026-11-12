package com.pixamob.pixacompose.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.Icon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaTheme

private val BoxSize = 20.dp

@Composable
public fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = PixaTheme.colors
    val mark by animateFloatAsState(if (checked) 1f else 0f, PixaTheme.motion.state.spec())
    Surface(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.size(BoxSize),
        enabled = enabled,
        role = Role.Checkbox,
        color = if (checked) colors.primary else colors.background,
        contentColor = colors.primaryForeground,
        shape = PixaTheme.shapes.sm,
        border = if (checked) filledBorder() ?: outlinedBorder(colors.primary) else outlinedBorder(colors.input),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            PixaIcons.Check,
            contentDescription = null,
            size = 14.dp,
            modifier = Modifier.graphicsLayer {
                alpha = mark.coerceIn(0f, 1f)
                scaleX = mark
                scaleY = mark
            },
        )
    }
}
