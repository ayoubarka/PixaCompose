package com.pixamob.pixacompose.styles

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.PixaColors
import com.pixamob.pixacompose.theme.PixaDecoration
import com.pixamob.pixacompose.theme.PixaFocusRing
import com.pixamob.pixacompose.theme.PixaMotion
import com.pixamob.pixacompose.theme.PixaShadow
import com.pixamob.pixacompose.theme.PixaShapes
import com.pixamob.pixacompose.theme.PixaSpring
import com.pixamob.pixacompose.theme.PixaStyle
import com.pixamob.pixacompose.theme.PressEffect

private val Ink = Color(0xFF1A1A1A)
private val Cream = Color(0xFFFFF8E7)

internal fun cartoonStyle() = PixaStyle(
    name = "Cartoon",
    lightColors = PixaColors(
        background = Cream,
        foreground = Ink,
        card = Color(0xFFFFFFFF),
        cardForeground = Ink,
        primary = Color(0xFFFFD23F),
        primaryForeground = Ink,
        secondary = Color(0xFF7BDFF2),
        secondaryForeground = Ink,
        muted = Color(0xFFF3EAD3),
        mutedForeground = Color(0xFF5C5648),
        accent = Color(0xFFFF8FAB),
        accentForeground = Ink,
        destructive = Color(0xFFFF5A5F),
        destructiveForeground = Ink,
        border = Ink,
        input = Ink,
        ring = Color(0xFF3A86FF),
    ),
    darkColors = PixaColors(
        background = Color(0xFF1F1B2E),
        foreground = Cream,
        card = Color(0xFF2B2540),
        cardForeground = Cream,
        primary = Color(0xFFFFD23F),
        primaryForeground = Ink,
        secondary = Color(0xFF7BDFF2),
        secondaryForeground = Ink,
        muted = Color(0xFF3A3355),
        mutedForeground = Color(0xFFC9C2DD),
        accent = Color(0xFFFF8FAB),
        accentForeground = Ink,
        destructive = Color(0xFFFF5A5F),
        destructiveForeground = Ink,
        border = Cream,
        input = Cream,
        ring = Color(0xFF7BDFF2),
    ),
    shapes = PixaShapes(
        sm = RoundedCornerShape(8.dp),
        md = RoundedCornerShape(14.dp),
        lg = RoundedCornerShape(20.dp),
        full = CircleShape,
    ),
    typography = typography(headingWeight = FontWeight.Black, labelWeight = FontWeight.Bold),
    decoration = PixaDecoration(
        borderWidth = 2.dp,
        filledBorderWidth = 2.dp,
        controlShadow = PixaShadow.Hard(offset = DpOffset(3.dp, 4.dp)),
        containerShadow = PixaShadow.Hard(offset = DpOffset(5.dp, 6.dp)),
        press = PressEffect.Sink,
        focusRing = PixaFocusRing(width = 3.dp, offset = 3.dp),
    ),
    // Bouncy everywhere: the overshoot is part of the cartoon feel.
    motion = PixaMotion(
        press = PixaSpring(dampingRatio = 0.45f, stiffness = 900f),
        state = PixaSpring(dampingRatio = 0.5f, stiffness = 500f),
        overlay = PixaSpring(dampingRatio = 0.6f, stiffness = 350f),
    ),
)
