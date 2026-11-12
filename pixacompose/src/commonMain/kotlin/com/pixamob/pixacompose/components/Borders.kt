package com.pixamob.pixacompose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaBorder
import com.pixamob.pixacompose.theme.PixaTheme

/** Border of a surface whose edge is its outline: card, outlined button, unchecked checkbox. */
@Composable
internal fun outlinedBorder(color: Color = PixaTheme.colors.border): PixaBorder? =
    PixaTheme.decoration.borderWidth.let { if (it > 0.dp) PixaBorder(it, color) else null }

/** Border of a surface that is already a solid fill. Most styles have none; Cartoon does. */
@Composable
internal fun filledBorder(color: Color = PixaTheme.colors.border): PixaBorder? =
    PixaTheme.decoration.filledBorderWidth.let { if (it > 0.dp) PixaBorder(it, color) else null }
