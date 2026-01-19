package com.pixamob.pixacompose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * App Theme Configuration - Optimized Version
 *
 * Provides centralized theming with simplified color and typography customization.
 *
 * **Color Customization:**
 * - Provide partial or complete color scales via `colorScales` parameter
 * - Light and dark palettes are automatically derived from the scales
 * - Missing colors fall back to elegant defaults
 *
 * **Typography Customization:**
 * - Provide complete font family via `fontFamily` parameter (all 9 weights required)
 * - If null, system default font is used
 *
 * **Examples:**
 *
 * 1. Customize only brand colors:
 * ```
 * PixaTheme(
 *     colorScales = ColorScales(
 *         brand = mapOf(
 *             50 to Color(0xFFF0F9FF),
 *             500 to Color(0xFF0284C7),
 *             900 to Color(0xFF0C4A6E)
 *         )
 *     )
 * ) { /* App content */ }
 * ```
 *
 * 2. Modify specific colors while keeping defaults:
 * ```
 * PixaTheme(
 *     colorScales = ColorScales(
 *         brand = DefaultColorScales.brand!! + mapOf(500 to MyCustomColor)
 *     )
 * ) { /* App content */ }
 * ```
 *
 * 3. Custom font with all weights:
 * ```
 * PixaTheme(
 *     fontFamily = FontFamilyConfig(
 *         thin = Font(MR.fonts.inter_thin.font),
 *         extraLight = Font(MR.fonts.inter_extra_light.font),
 *         light = Font(MR.fonts.inter_light.font),
 *         regular = Font(MR.fonts.inter_regular.font),
 *         medium = Font(MR.fonts.inter_medium.font),
 *         semiBold = Font(MR.fonts.inter_semi_bold.font),
 *         bold = Font(MR.fonts.inter_bold.font),
 *         extraBold = Font(MR.fonts.inter_extra_bold.font),
 *         black = Font(MR.fonts.inter_black.font)
 *     )
 * ) { /* App content */ }
 * ```
 *
 * @param useDarkTheme Whether to use dark theme (defaults to system preference)
 * @param colorScales Optional color scales for customization. Provide partial or complete scales.
 * @param fontFamily Optional font family configuration with all 9 weights
 * @param content The composable content to wrap with theme
 */
@Composable
fun PixaTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    colorScales: ColorScales? = null,
    fontFamily: FontFamilyConfig? = null,
    content: @Composable () -> Unit
) {
    // Build color palette from scales with smart merging
    val colorPalette = if (colorScales != null) {
        buildColorPaletteFromScales(
            useDarkTheme = useDarkTheme,
            colorScales = colorScales
        )
    } else {
        // Use default palettes
        if (useDarkTheme) localDarkColorScheme else localLightColorScheme
    }

    val typography = provideTextTypography(fontFamily)

    CompositionLocalProvider(
        LocalColorPalette provides colorPalette,
        LocalTextTypography provides typography,
        LocalShapeStyle provides shapeStyles,
        LocalIsDarkTheme provides useDarkTheme
    ) {
        MaterialTheme(
            colorScheme = if (useDarkTheme) darkMaterialColorScheme else lightMaterialColorScheme,
            content = content
        )
    }
}

/**
 * Theme accessor object for easy access to theme properties
 *
 * Usage examples:
 * - AppTheme.colors.brandContentDefault
 * - AppTheme.typography.titleBold
 * - AppTheme.shapes.rounded
 * - AppTheme.isDarkTheme
 */
object AppTheme {
    /**
     * Access current color palette
     */
    val colors: ColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalColorPalette.current

    /**
     * Access typography styles
     */
    val typography: TextTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTextTypography.current

    /**
     * Access shape styles
     */
    val shapes: ShapeStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalShapeStyle.current


    /**
     * Check if dark theme is active
     */
    val isDarkTheme: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsDarkTheme.current
}

// Local provider for dark theme state
private val LocalIsDarkTheme = staticCompositionLocalOf { false }

/**
 * Build a color palette from color scales with intelligent merging.
 *
 * This function:
 * - Merges user-provided color scales with defaults
 * - Automatically derives light/dark palettes based on color weights
 * - Supports partial customization (customize only what you need)
 *
 * Color weight mapping:
 * - Light theme: Uses weights 50-700 (lighter colors)
 * - Dark theme: Uses weights 300-950 (darker colors)
 *
 * @param useDarkTheme Whether to generate dark theme palette
 * @param colorScales User-provided color scales (partial or complete)
 * @return Complete ColorPalette with all semantic colors
 */
private fun buildColorPaletteFromScales(
    useDarkTheme: Boolean,
    colorScales: ColorScales
): ColorPalette {
    // Merge user scales with defaults (user scales take precedence)
    val brandColors = mergeColorScale(colorScales.brand, brandColor)
    val accentColors = mergeColorScale(colorScales.accent, accentColor)
    val baseColors = mergeColorScale(colorScales.base, baseColor)
    val infoColors = mergeColorScale(colorScales.info, infoColor)
    val successColors = mergeColorScale(colorScales.success, successColor)
    val warningColors = mergeColorScale(colorScales.warning, warningColor)
    val errorColors = mergeColorScale(colorScales.error, errorColor)

    return if (useDarkTheme) {
        // Dark theme palette
        ColorPalette(
            // Brand colors
            brandSurfaceSubtle = brandColors[700] ?: dark_brand_surface_subtle,
            brandSurfaceDefault = brandColors[800] ?: dark_brand_surface_default,
            brandSurfaceFocus = brandColors[900] ?: dark_brand_surface_focus,
            brandBorderSubtle = brandColors[900] ?: dark_brand_border_subtle,
            brandBorderDefault = brandColors[700] ?: dark_brand_border_default,
            brandBorderFocus = brandColors[500] ?: dark_brand_border_focus,
            brandContentSubtle = brandColors[400] ?: dark_brand_content_subtle,
            brandContentDefault = brandColors[500] ?: dark_brand_content_default,
            brandContentFocus = brandColors[300] ?: dark_brand_content_focus,

            // Accent colors
            accentSurfaceSubtle = accentColors[700] ?: dark_accent_surface_subtle,
            accentSurfaceDefault = accentColors[800] ?: dark_accent_surface_default,
            accentSurfaceFocus = accentColors[900] ?: dark_accent_surface_focus,
            accentBorderSubtle = accentColors[900] ?: dark_accent_border_subtle,
            accentBorderDefault = accentColors[700] ?: dark_accent_border_default,
            accentBorderFocus = accentColors[500] ?: dark_accent_border_focus,
            accentContentSubtle = accentColors[400] ?: dark_accent_content_subtle,
            accentContentDefault = accentColors[500] ?: dark_accent_content_default,
            accentContentFocus = accentColors[300] ?: dark_accent_content_focus,

            // Base colors
            baseSurfaceSubtle = baseColors[900] ?: dark_base_surface_subtle,
            baseSurfaceDefault = baseColors[800] ?: dark_base_surface_default,
            baseSurfaceElevated = baseColors[700] ?: dark_base_surface_focus,
            baseSurfaceFocus = baseColors[700] ?: dark_base_surface_focus,
            baseSurfaceShadow = baseColors[950] ?: dark_base_surface_shadow,
            baseSurfaceDisabled = baseColors[950]?.copy(0.5f) ?: dark_base_surface_disabled,
            baseBorderSubtle = baseColors[600] ?: dark_base_border_subtle,
            baseBorderDefault = baseColors[700] ?: dark_base_border_default,
            baseBorderFocus = baseColors[800] ?: dark_base_border_focus,
            baseBorderDisabled = baseColors[900] ?: dark_base_border_disabled,
            baseContentTitle = baseColors[50] ?: dark_base_content_title,
            baseContentSubtitle = baseColors[200] ?: dark_base_content_subtitle,
            baseContentBody = baseColors[400] ?: dark_base_content_body,
            baseContentCaption = baseColors[500] ?: dark_base_content_caption,
            baseContentHint = baseColors[600] ?: dark_base_content_hint,
            baseContentNegative = baseColors[900] ?: dark_base_content_negative,
            baseContentDisabled = baseColors[400]?.copy(0.75f) ?: dark_base_content_disabled,

            // Info colors
            infoSurfaceSubtle = infoColors[700] ?: dark_info_surface_subtle,
            infoSurfaceDefault = infoColors[800] ?: dark_info_surface_default,
            infoSurfaceFocus = infoColors[900] ?: dark_info_surface_focus,
            infoBorderSubtle = infoColors[900] ?: dark_info_border_subtle,
            infoBorderDefault = infoColors[700] ?: dark_info_border_default,
            infoBorderFocus = infoColors[500] ?: dark_info_border_focus,
            infoContentSubtle = infoColors[400] ?: dark_info_content_subtle,
            infoContentDefault = infoColors[500] ?: dark_info_content_default,
            infoContentFocus = infoColors[300] ?: dark_info_content_focus,

            // Success colors
            successSurfaceSubtle = successColors[700] ?: dark_success_surface_subtle,
            successSurfaceDefault = successColors[800] ?: dark_success_surface_default,
            successSurfaceFocus = successColors[900] ?: dark_success_surface_focus,
            successBorderSubtle = successColors[900] ?: dark_success_border_subtle,
            successBorderDefault = successColors[700] ?: dark_success_border_default,
            successBorderFocus = successColors[500] ?: dark_success_border_focus,
            successContentSubtle = successColors[400] ?: dark_success_content_subtle,
            successContentDefault = successColors[500] ?: dark_success_content_default,
            successContentFocus = successColors[300] ?: dark_success_content_focus,

            // Warning colors
            warningSurfaceSubtle = warningColors[700] ?: dark_warning_surface_subtle,
            warningSurfaceDefault = warningColors[800] ?: dark_warning_surface_default,
            warningSurfaceFocus = warningColors[900] ?: dark_warning_surface_focus,
            warningBorderSubtle = warningColors[900] ?: dark_warning_border_subtle,
            warningBorderDefault = warningColors[700] ?: dark_warning_border_default,
            warningBorderFocus = warningColors[500] ?: dark_warning_border_focus,
            warningContentSubtle = warningColors[400] ?: dark_warning_content_subtle,
            warningContentDefault = warningColors[500] ?: dark_warning_content_default,
            warningContentFocus = warningColors[300] ?: dark_warning_content_focus,

            // Error colors
            errorSurfaceSubtle = errorColors[700] ?: dark_error_surface_subtle,
            errorSurfaceDefault = errorColors[800] ?: dark_error_surface_default,
            errorSurfaceFocus = errorColors[600] ?: dark_error_surface_focus,
            errorBorderSubtle = errorColors[900] ?: dark_error_border_subtle,
            errorBorderDefault = errorColors[700] ?: dark_error_border_default,
            errorBorderFocus = errorColors[500] ?: dark_error_border_focus,
            errorContentSubtle = errorColors[400] ?: dark_error_content_subtle,
            errorContentDefault = errorColors[500] ?: dark_error_content_default,
            errorContentFocus = errorColors[300] ?: dark_error_content_focus,
        )
    } else {
        // Light theme palette
        ColorPalette(
            // Brand colors
            brandSurfaceSubtle = brandColors[50] ?: light_brand_surface_subtle,
            brandSurfaceDefault = brandColors[100] ?: light_brand_surface_default,
            brandSurfaceFocus = brandColors[200] ?: light_brand_surface_focus,
            brandBorderSubtle = brandColors[300] ?: light_brand_border_subtle,
            brandBorderDefault = brandColors[400] ?: light_brand_border_default,
            brandBorderFocus = brandColors[500] ?: light_brand_border_focus,
            brandContentSubtle = brandColors[600] ?: light_brand_content_subtle,
            brandContentDefault = brandColors[500] ?: light_brand_content_default,
            brandContentFocus = brandColors[700] ?: light_brand_content_focus,

            // Accent colors
            accentSurfaceSubtle = accentColors[50] ?: light_accent_surface_subtle,
            accentSurfaceDefault = accentColors[100] ?: light_accent_surface_default,
            accentSurfaceFocus = accentColors[200] ?: light_accent_surface_focus,
            accentBorderSubtle = accentColors[300] ?: light_accent_border_subtle,
            accentBorderDefault = accentColors[400] ?: light_accent_border_default,
            accentBorderFocus = accentColors[500] ?: light_accent_border_focus,
            accentContentSubtle = accentColors[600] ?: light_accent_content_subtle,
            accentContentDefault = accentColors[500] ?: light_accent_content_default,
            accentContentFocus = accentColors[700] ?: light_accent_content_focus,

            // Base colors
            baseSurfaceSubtle = baseColors[50] ?: light_base_surface_subtle,
            baseSurfaceDefault = baseColors[100] ?: light_base_surface_default,
            baseSurfaceElevated = baseColors[200] ?: light_base_surface_focus,
            baseSurfaceFocus = baseColors[200] ?: light_base_surface_focus,
            baseSurfaceShadow = baseColors[950] ?: light_base_surface_shadow,
            baseSurfaceDisabled = baseColors[300]?.copy(0.5f) ?: light_base_surface_disabled,
            baseBorderSubtle = baseColors[300] ?: light_base_border_subtle,
            baseBorderDefault = baseColors[400] ?: light_base_border_default,
            baseBorderFocus = baseColors[500] ?: light_base_border_focus,
            baseBorderDisabled = baseColors[300]?.copy(0.75f) ?: light_base_border_disabled,
            baseContentTitle = baseColors[950] ?: light_base_content_title,
            baseContentSubtitle = baseColors[800] ?: light_base_content_subtitle,
            baseContentBody = baseColors[600] ?: light_base_content_body,
            baseContentCaption = baseColors[500] ?: light_base_content_caption,
            baseContentHint = baseColors[400] ?: light_base_content_hint,
            baseContentNegative = baseColors[100] ?: light_base_content_negative,
            baseContentDisabled = baseColors[500]?.copy(0.5f) ?: light_base_content_disabled,

            // Info colors
            infoSurfaceSubtle = infoColors[50] ?: light_info_surface_subtle,
            infoSurfaceDefault = infoColors[100] ?: light_info_surface_default,
            infoSurfaceFocus = infoColors[200] ?: light_info_surface_focus,
            infoBorderSubtle = infoColors[300] ?: light_info_border_subtle,
            infoBorderDefault = infoColors[400] ?: light_info_border_default,
            infoBorderFocus = infoColors[500] ?: light_info_border_focus,
            infoContentSubtle = infoColors[600] ?: light_info_content_subtle,
            infoContentDefault = infoColors[500] ?: light_info_content_default,
            infoContentFocus = infoColors[700] ?: light_info_content_focus,

            // Success colors
            successSurfaceSubtle = successColors[50] ?: light_success_surface_subtle,
            successSurfaceDefault = successColors[100] ?: light_success_surface_default,
            successSurfaceFocus = successColors[200] ?: light_success_surface_focus,
            successBorderSubtle = successColors[300] ?: light_success_border_subtle,
            successBorderDefault = successColors[400] ?: light_success_border_default,
            successBorderFocus = successColors[500] ?: light_success_border_focus,
            successContentSubtle = successColors[600] ?: light_success_content_subtle,
            successContentDefault = successColors[500] ?: light_success_content_default,
            successContentFocus = successColors[700] ?: light_success_content_focus,

            // Warning colors
            warningSurfaceSubtle = warningColors[50] ?: light_warning_surface_subtle,
            warningSurfaceDefault = warningColors[100] ?: light_warning_surface_default,
            warningSurfaceFocus = warningColors[200] ?: light_warning_surface_focus,
            warningBorderSubtle = warningColors[300] ?: light_warning_border_subtle,
            warningBorderDefault = warningColors[400] ?: light_warning_border_default,
            warningBorderFocus = warningColors[500] ?: light_warning_border_focus,
            warningContentSubtle = warningColors[600] ?: light_warning_content_subtle,
            warningContentDefault = warningColors[500] ?: light_warning_content_default,
            warningContentFocus = warningColors[700] ?: light_warning_content_focus,

            // Error colors
            errorSurfaceSubtle = errorColors[50] ?: light_error_surface_subtle,
            errorSurfaceDefault = errorColors[100] ?: light_error_surface_default,
            errorSurfaceFocus = errorColors[200] ?: light_error_surface_focus,
            errorBorderSubtle = errorColors[300] ?: light_error_border_subtle,
            errorBorderDefault = errorColors[400] ?: light_error_border_default,
            errorBorderFocus = errorColors[500] ?: light_error_border_focus,
            errorContentSubtle = errorColors[600] ?: light_error_content_subtle,
            errorContentDefault = errorColors[500] ?: light_error_content_default,
            errorContentFocus = errorColors[700] ?: light_error_content_focus,
        )
    }
}

/**
 * Merges user-provided color scale with default scale.
 * User colors take precedence, missing weights fall back to defaults.
 */
private fun mergeColorScale(
    userScale: Map<Int, Color>?,
    defaultScale: Map<Int, Color>
): Map<Int, Color> {
    return if (userScale != null) {
        defaultScale + userScale  // User scale overwrites defaults
    } else {
        defaultScale
    }
}

/**
 * Material 3 color scheme integration
 */
private val lightMaterialColorScheme = androidx.compose.material3.lightColorScheme(
    primary = brandColor[500]!!,
    onPrimary = Color.White,
    primaryContainer = brandColor[100]!!,
    onPrimaryContainer = brandColor[900]!!,

    secondary = accentColor[500]!!,
    onSecondary = Color.White,
    secondaryContainer = accentColor[100]!!,
    onSecondaryContainer = accentColor[900]!!,

    tertiary = infoColor[500]!!,
    onTertiary = Color.White,
    tertiaryContainer = infoColor[100]!!,
    onTertiaryContainer = infoColor[900]!!,

    error = errorColor[500]!!,
    onError = Color.White,
    errorContainer = errorColor[100]!!,
    onErrorContainer = errorColor[900]!!,

    background = baseColor[50]!!,
    onBackground = baseColor[900]!!,

    surface = Color.White,
    onSurface = baseColor[900]!!,
    surfaceVariant = baseColor[100]!!,
    onSurfaceVariant = baseColor[700]!!,

    outline = baseColor[300]!!,
    outlineVariant = baseColor[200]!!,

    scrim = baseColor[900]!!.copy(alpha = 0.32f),

    inverseSurface = baseColor[900]!!,
    inverseOnSurface = baseColor[50]!!,
    inversePrimary = brandColor[200]!!,

    surfaceTint = brandColor[500]!!,
)

private val darkMaterialColorScheme = androidx.compose.material3.darkColorScheme(
    primary = brandColor[400]!!,
    onPrimary = brandColor[900]!!,
    primaryContainer = brandColor[700]!!,
    onPrimaryContainer = brandColor[100]!!,

    secondary = accentColor[400]!!,
    onSecondary = accentColor[900]!!,
    secondaryContainer = accentColor[700]!!,
    onSecondaryContainer = accentColor[100]!!,

    tertiary = infoColor[400]!!,
    onTertiary = infoColor[900]!!,
    tertiaryContainer = infoColor[700]!!,
    onTertiaryContainer = infoColor[100]!!,

    error = errorColor[400]!!,
    onError = errorColor[900]!!,
    errorContainer = errorColor[700]!!,
    onErrorContainer = errorColor[100]!!,

    background = baseColor[950]!!,
    onBackground = baseColor[100]!!,

    surface = baseColor[900]!!,
    onSurface = baseColor[100]!!,
    surfaceVariant = baseColor[800]!!,
    onSurfaceVariant = baseColor[300]!!,

    outline = baseColor[700]!!,
    outlineVariant = baseColor[800]!!,

    scrim = Color.Black.copy(alpha = 0.5f),

    inverseSurface = baseColor[100]!!,
    inverseOnSurface = baseColor[900]!!,
    inversePrimary = brandColor[600]!!,

    surfaceTint = brandColor[400]!!,
)
