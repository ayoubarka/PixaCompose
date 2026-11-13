package com.pixamob.pixacompose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.pixamob.pixacompose.primitives.PixaBorder
import com.pixamob.pixacompose.theme.PixaTheme

/** How strongly an accent color shows through as a fill behind content drawn in that color. */
private const val TintFillAlpha = 0.16f

/**
 * Soft fill for icon tiles and selected tiles of an accent [color]. It is opaque, blended over
 * the card color, so a style's hard shadow does not show through it.
 */
@Composable
internal fun tintFill(color: Color): Color =
    color.copy(alpha = color.alpha * TintFillAlpha).compositeOver(PixaTheme.colors.card)

/** Outline of a selected tile: at least 2dp so selection reads even in thin-lined styles. */
@Composable
internal fun selectedBorder(color: Color): PixaBorder =
    PixaBorder(max(PixaTheme.decoration.borderWidth, 2.dp), color)
