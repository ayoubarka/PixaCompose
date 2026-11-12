package com.pixamob.pixacompose.styles

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.PixaColors
import com.pixamob.pixacompose.theme.PixaDecoration
import com.pixamob.pixacompose.theme.PixaShadow
import com.pixamob.pixacompose.theme.PixaShapes
import com.pixamob.pixacompose.theme.PixaStyle
import com.pixamob.pixacompose.theme.PressEffect

private val Cyan = Color(0xFF00F0FF)
private val Void = Color(0xFF0A0A14)

// Neon only reads as neon on a dark canvas, so light and dark share one palette.
private val neonColors = PixaColors(
    background = Void,
    foreground = Color(0xFFE6E6FF),
    card = Color(0xFF12122A),
    cardForeground = Color(0xFFE6E6FF),
    primary = Cyan,
    primaryForeground = Void,
    secondary = Color(0xFFFF2BD6),
    secondaryForeground = Void,
    muted = Color(0xFF1A1A33),
    mutedForeground = Color(0xFF8C8CB8),
    accent = Color(0xFF9D4EDD),
    accentForeground = Color(0xFFFFFFFF),
    destructive = Color(0xFFFF3864),
    destructiveForeground = Void,
    border = Cyan,
    input = Color(0xFF2E2E5C),
    ring = Cyan,
)

internal fun neonStyle() = PixaStyle(
    name = "Neon",
    lightColors = neonColors,
    darkColors = neonColors,
    shapes = PixaShapes(
        sm = CutCornerShape(3.dp),
        md = CutCornerShape(6.dp),
        lg = CutCornerShape(10.dp),
        full = CircleShape,
    ),
    typography = typography(fontFamily = FontFamily.Monospace),
    decoration = PixaDecoration(
        borderWidth = 1.dp,
        filledBorderWidth = 0.dp,
        shadow = PixaShadow.Glow(radius = 14.dp),
        press = PressEffect.Scale(0.96f),
    ),
)
