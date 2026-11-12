package com.pixamob.pixacompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.PixaTheme

public enum class SeparatorOrientation { Horizontal, Vertical }

/** A line between groups of content, as thick as the style's borders. */
@Composable
public fun Separator(
    modifier: Modifier = Modifier,
    orientation: SeparatorOrientation = SeparatorOrientation.Horizontal,
    thickness: Dp = PixaTheme.decoration.borderWidth.coerceAtLeast(1.dp),
) {
    val size = when (orientation) {
        SeparatorOrientation.Horizontal -> Modifier.fillMaxWidth().height(thickness)
        SeparatorOrientation.Vertical -> Modifier.fillMaxHeight().width(thickness)
    }
    Box(modifier.then(size).background(PixaTheme.colors.border))
}
