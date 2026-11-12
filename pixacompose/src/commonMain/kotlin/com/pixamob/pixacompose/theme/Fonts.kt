package com.pixamob.pixacompose.theme

import androidx.compose.ui.text.font.FontFamily

/**
 * Replaces the font of every text style, keeping sizes and weights.
 *
 * ```
 * val style = PixaStyles.Cartoon.withFontFamily(body = Nunito, heading = Baloo)
 * ```
 */
public fun PixaTypography.withFontFamily(
    body: FontFamily,
    heading: FontFamily = body,
): PixaTypography = PixaTypography(
    h1 = h1.copy(fontFamily = heading),
    h2 = h2.copy(fontFamily = heading),
    h3 = h3.copy(fontFamily = heading),
    body = this.body.copy(fontFamily = body),
    bodySmall = bodySmall.copy(fontFamily = body),
    label = label.copy(fontFamily = body),
    caption = caption.copy(fontFamily = body),
)

/** Same as [PixaTypography.withFontFamily], applied to a whole style. */
public fun PixaStyle.withFontFamily(
    body: FontFamily,
    heading: FontFamily = body,
): PixaStyle = copy(typography = typography.withFontFamily(body, heading))
