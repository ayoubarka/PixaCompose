package com.pixamob.pixacompose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Font family configuration requiring all 9 font weights.
 *
 * This ensures complete typography support across all text styles.
 * Use with Moko Resources or platform-specific font loading.
 *
 * Example with Moko Resources:
 * ```
 * val customFont = FontFamilyConfig(
 *     thin = Font(MR.fonts.inter_thin.font),
 *     extraLight = Font(MR.fonts.inter_extra_light.font),
 *     light = Font(MR.fonts.inter_light.font),
 *     regular = Font(MR.fonts.inter_regular.font),
 *     medium = Font(MR.fonts.inter_medium.font),
 *     semiBold = Font(MR.fonts.inter_semi_bold.font),
 *     bold = Font(MR.fonts.inter_bold.font),
 *     extraBold = Font(MR.fonts.inter_extra_bold.font),
 *     black = Font(MR.fonts.inter_black.font)
 * )
 * ```
 *
 * @param thin Font with weight 100 (W100/Thin)
 * @param extraLight Font with weight 200 (W200/ExtraLight)
 * @param light Font with weight 300 (W300/Light)
 * @param regular Font with weight 400 (W400/Regular)
 * @param medium Font with weight 500 (W500/Medium)
 * @param semiBold Font with weight 600 (W600/SemiBold)
 * @param bold Font with weight 700 (W700/Bold)
 * @param extraBold Font with weight 800 (W800/ExtraBold)
 * @param black Font with weight 900 (W900/Black)
 */
@Immutable
data class FontFamilyConfig(
    val thin: Font,        // W100
    val extraLight: Font,  // W200
    val light: Font,       // W300
    val regular: Font,     // W400
    val medium: Font,      // W500
    val semiBold: Font,    // W600
    val bold: Font,        // W700
    val extraBold: Font,   // W800
    val black: Font        // W900
) {
    /**
     * Converts the font configuration to a FontFamily with proper weight assignments.
     */
    fun toFontFamily(): FontFamily = FontFamily(
        thin,
        extraLight,
        light,
        regular,
        medium,
        semiBold,
        bold,
        extraBold,
        black
    )
}

/**
 * Provides text typography with optional custom font family configuration.
 *
 * @param fontConfig Optional font family configuration with all 9 weights.
 * If null, system default font will be used.
 */
@Composable
fun provideTextTypography(fontConfig: FontFamilyConfig? = null): TextTypography {
    val fontFamily = fontConfig?.toFontFamily() ?: FontFamily.Default

    return TextTypography(
        // Display styles - for hero sections and large headings
        displayLarge = TextStyle(
            fontSize = 57.sp,
            lineHeight = 64.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W900,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontSize = 45.sp,
            lineHeight = 52.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W800,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W700,
            letterSpacing = 0.sp
        ),

        // Header styles - for page titles
        headerBold = TextStyle(
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W900,
            letterSpacing = 0.sp
        ),
        headerRegular = TextStyle(
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W700,
            letterSpacing = 0.sp
        ),

        // Headline styles - for section headings
        headlineBold = TextStyle(
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W800,
            letterSpacing = 0.sp
        ),
        headlineRegular = TextStyle(
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W600,
            letterSpacing = 0.sp
        ),

        // Title styles - for card headers and dialogs
        titleBold = TextStyle(
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W700,
            letterSpacing = 0.sp
        ),
        titleRegular = TextStyle(
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W600,
            letterSpacing = 0.15.sp
        ),
        titleLight = TextStyle(
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W500,
            letterSpacing = 0.15.sp
        ),

        // Subtitle styles - for secondary headings
        subtitleBold = TextStyle(
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W600,
            letterSpacing = 0.15.sp
        ),
        subtitleRegular = TextStyle(
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W500,
            letterSpacing = 0.15.sp
        ),
        subtitleLight = TextStyle(
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.15.sp
        ),

        // Body styles - for main content
        bodyBold = TextStyle(
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W500,
            letterSpacing = 0.25.sp
        ),
        bodyRegular = TextStyle(
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.25.sp
        ),
        bodyLight = TextStyle(
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W300,
            letterSpacing = 0.25.sp
        ),

        // Caption styles - for supporting text
        captionBold = TextStyle(
            fontSize = 13.sp,
            lineHeight = 16.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W500,
            letterSpacing = 0.4.sp
        ),
        captionRegular = TextStyle(
            fontSize = 13.sp,
            lineHeight = 16.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.4.sp
        ),
        captionLight = TextStyle(
            fontSize = 13.sp,
            lineHeight = 16.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W300,
            letterSpacing = 0.4.sp
        ),

        // Overline - for labels and categories
        overline = TextStyle(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W500,
            letterSpacing = 1.5.sp
        ),

        // Footnote styles - for fine print
        footnoteBold = TextStyle(
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W500,
            letterSpacing = 0.5.sp
        ),
        footnoteRegular = TextStyle(
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.5.sp
        ),

        // Label styles - for form labels and tags
        labelLarge = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W500,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W500,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W500,
            letterSpacing = 0.5.sp
        ),

        // Action/Button styles - optimized for CTAs
        actionMini = TextStyle(
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W600,
            letterSpacing = 0.5.sp
        ),
        actionExtraSmall = TextStyle(
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W600,
            letterSpacing = 0.5.sp
        ),
        actionSmall = TextStyle(
            fontSize = 13.sp,
            lineHeight = 16.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W600,
            letterSpacing = 0.46.sp
        ),
        actionMedium = TextStyle(
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W700,
            letterSpacing = 0.4.sp
        ),
        actionLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W700,
            letterSpacing = 0.3.sp
        ),
        actionExtraLarge = TextStyle(
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W800,
            letterSpacing = 0.2.sp
        ),
        actionHuge = TextStyle(
            fontSize = 20.sp,
            lineHeight = 26.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W900,
            letterSpacing = 0.15.sp
        )
    )
}

/**
 * Text typography data class containing all text styles
 *
 * Usage example:
 * ```kotlin
 * Text(
 *     text = "Hello World",
 *     style = AppTheme.typography.titleBold
 * )
 * ```
 */
@Immutable
data class TextTypography(
    // Display styles
    val displayLarge: TextStyle = TextStyle(),
    val displayMedium: TextStyle = TextStyle(),
    val displaySmall: TextStyle = TextStyle(),

    // Header styles
    val headerBold: TextStyle = TextStyle(),
    val headerRegular: TextStyle = TextStyle(),

    // Headline styles
    val headlineBold: TextStyle = TextStyle(),
    val headlineRegular: TextStyle = TextStyle(),

    // Title styles
    val titleBold: TextStyle = TextStyle(),
    val titleRegular: TextStyle = TextStyle(),
    val titleLight: TextStyle = TextStyle(),

    // Subtitle styles
    val subtitleBold: TextStyle = TextStyle(),
    val subtitleRegular: TextStyle = TextStyle(),
    val subtitleLight: TextStyle = TextStyle(),

    // Body styles
    val bodyBold: TextStyle = TextStyle(),
    val bodyRegular: TextStyle = TextStyle(),
    val bodyLight: TextStyle = TextStyle(),

    // Caption styles
    val captionBold: TextStyle = TextStyle(),
    val captionRegular: TextStyle = TextStyle(),
    val captionLight: TextStyle = TextStyle(),

    // Overline
    val overline: TextStyle = TextStyle(),

    // Footnote styles
    val footnoteBold: TextStyle = TextStyle(),
    val footnoteRegular: TextStyle = TextStyle(),

    // Label styles
    val labelLarge: TextStyle = TextStyle(),
    val labelMedium: TextStyle = TextStyle(),
    val labelSmall: TextStyle = TextStyle(),

    // Action/Button styles
    val actionMini: TextStyle = TextStyle(),
    val actionExtraSmall: TextStyle = TextStyle(),
    val actionSmall: TextStyle = TextStyle(),
    val actionMedium: TextStyle = TextStyle(),
    val actionLarge: TextStyle = TextStyle(),
    val actionExtraLarge: TextStyle = TextStyle(),
    val actionHuge: TextStyle = TextStyle(),
)

val LocalTextTypography = staticCompositionLocalOf { TextTypography() }