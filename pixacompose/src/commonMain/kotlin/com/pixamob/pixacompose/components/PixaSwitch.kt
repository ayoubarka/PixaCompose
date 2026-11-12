package com.pixamob.pixacompose.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme
import kotlin.math.roundToInt

private val TrackWidth = 46.dp
private val TrackHeight = 26.dp
private val ThumbSize = 18.dp

@Composable
public fun PixaSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = PixaTheme.colors
    val motion = PixaTheme.motion
    val position by animateFloatAsState(if (checked) 1f else 0f, motion.state.spec())
    val track by animateColorAsState(if (checked) colors.primary else colors.muted, motion.state.spec())
    val border = filledBorder()
    PixaSurface(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.size(TrackWidth, TrackHeight),
        enabled = enabled,
        role = Role.Switch,
        color = track,
        shape = PixaTheme.shapes.full,
        // An off track is only a faint fill, so it needs an outline in styles without filled borders.
        border = border ?: if (checked) null else outlinedBorder(colors.input),
        contentAlignment = Alignment.CenterStart,
    ) {
        PixaSurface(
            modifier = Modifier
                .fillMaxSize()
                .padding((TrackHeight - ThumbSize) / 2)
                // Slide the thumb across whatever room the track leaves, so no width is hardcoded.
                .layout { measurable, constraints ->
                    val thumb = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
                    val travel = constraints.maxWidth - thumb.width
                    layout(constraints.maxWidth, constraints.maxHeight) {
                        thumb.placeRelative(
                            x = (travel * position).roundToInt(),
                            y = (constraints.maxHeight - thumb.height) / 2,
                        )
                    }
                }
                .size(ThumbSize),
            color = colors.background,
            shape = PixaTheme.shapes.full,
            border = border,
        ) {}
    }
}
