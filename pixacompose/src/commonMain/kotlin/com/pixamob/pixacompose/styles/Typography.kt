package com.pixamob.pixacompose.styles

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pixamob.pixacompose.theme.PixaTypography

/** One type scale shared by the built-in styles; they differ by family and weights only. */
internal fun typography(
    fontFamily: FontFamily = FontFamily.Default,
    headingWeight: FontWeight = FontWeight.SemiBold,
    labelWeight: FontWeight = FontWeight.Medium,
): PixaTypography {
    fun style(size: Int, lineHeight: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
        fontFamily = fontFamily,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        fontWeight = weight,
    )
    return PixaTypography(
        h1 = style(30, 36, headingWeight),
        h2 = style(24, 32, headingWeight),
        h3 = style(18, 26, headingWeight),
        body = style(16, 24),
        bodySmall = style(14, 20),
        label = style(14, 20, labelWeight),
        caption = style(12, 16),
    )
}
