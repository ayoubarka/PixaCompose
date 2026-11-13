package com.pixamob.pixacompose.styles

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.PixaColors
import com.pixamob.pixacompose.theme.PixaDecoration
import com.pixamob.pixacompose.theme.PixaShadow
import com.pixamob.pixacompose.theme.PixaShapes
import com.pixamob.pixacompose.theme.PixaStyle
import com.pixamob.pixacompose.theme.PressEffect

internal fun minimalStyle() = PixaStyle(
    name = "Minimal",
    lightColors = PixaColors(
        background = Color(0xFFFFFFFF),
        foreground = Color(0xFF09090B),
        card = Color(0xFFFFFFFF),
        cardForeground = Color(0xFF09090B),
        primary = Color(0xFF18181B),
        primaryForeground = Color(0xFFFAFAFA),
        secondary = Color(0xFFF4F4F5),
        secondaryForeground = Color(0xFF18181B),
        muted = Color(0xFFF4F4F5),
        mutedForeground = Color(0xFF71717A),
        accent = Color(0xFFF4F4F5),
        accentForeground = Color(0xFF18181B),
        destructive = Color(0xFFDC2626),
        destructiveForeground = Color(0xFFFAFAFA),
        success = Color(0xFF16A34A),
        successForeground = Color(0xFFFAFAFA),
        border = Color(0xFFE4E4E7),
        input = Color(0xFFE4E4E7),
        ring = Color(0xFFA1A1AA),
    ),
    darkColors = PixaColors(
        background = Color(0xFF09090B),
        foreground = Color(0xFFFAFAFA),
        card = Color(0xFF18181B),
        cardForeground = Color(0xFFFAFAFA),
        primary = Color(0xFFFAFAFA),
        primaryForeground = Color(0xFF18181B),
        secondary = Color(0xFF27272A),
        secondaryForeground = Color(0xFFFAFAFA),
        muted = Color(0xFF27272A),
        mutedForeground = Color(0xFFA1A1AA),
        accent = Color(0xFF27272A),
        accentForeground = Color(0xFFFAFAFA),
        destructive = Color(0xFFEF4444),
        destructiveForeground = Color(0xFFFAFAFA),
        success = Color(0xFF22C55E),
        successForeground = Color(0xFF052E16),
        border = Color(0xFF27272A),
        input = Color(0xFF3F3F46),
        ring = Color(0xFFD4D4D8),
    ),
    shapes = PixaShapes(
        sm = RoundedCornerShape(6.dp),
        md = RoundedCornerShape(8.dp),
        lg = RoundedCornerShape(12.dp),
        full = CircleShape,
    ),
    typography = typography(),
    decoration = PixaDecoration(
        borderWidth = 1.dp,
        filledBorderWidth = 0.dp,
        controlShadow = PixaShadow.None,
        containerShadow = PixaShadow.Soft(2.dp),
        press = PressEffect.Dim(0.85f),
    ),
)
