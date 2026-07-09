package com.pixamob.pixacompose.theme

import androidx.compose.foundation.isSystemInDarkTheme
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
 * - **Primary**: Use `colorOverrides` to set specific semantic tokens directly
 * - **Advanced**: Use `colorScales` to override entire color families at the scale level
 * - Light and dark palettes are automatically derived from the scales
 * - Missing colors fall back to elegant defaults
 *
 * **Typography Customization:**
 * - Provide complete font family via `fontFamily` parameter (all 9 weights required)
 * - If null, system default font is used
 *
 * **Examples:**
 *
 * 1. Override specific semantic colors (primary API):
 * ```
 * PixaTheme(
 *     colorOverrides = ColorOverrides(
 *         brandContentDefault = Color(0xFF0284C7),
 *         brandBorderDefault = Color(0xFF0369A1)
 *     )
 * ) { /* App content */ }
 * ```
 *
 * 2. Customize an entire color scale (advanced):
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
 * 3. Combine both for layered customization:
 * ```
 * PixaTheme(
 *     colorScales = ColorScales(brand = myBrandScale),
 *     colorOverrides = ColorOverrides(
 *         brandBorderDefault = Color(0xFF123456)  // overrides the scale-derived value
 *     )
 * ) { /* App content */ }
 * ```
 *
 * 4. Custom font with all weights:
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
 * @param colorOverrides Optional semantic color overrides (primary customization API)
 * @param colorScales Optional color scales for advanced full-family customization
 * @param fontFamily Optional font family configuration with all 9 weights
 * @param content The composable content to wrap with theme
 */
@Composable
fun PixaTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    colorOverrides: ColorOverrides? = null,
    colorScales: ColorScales? = null,
    fontFamily: FontFamilyConfig? = null,
    content: @Composable () -> Unit
) {
    // Build color palette from scales with smart merging
    val basePalette = if (colorScales != null) {
        buildColorPaletteFromScales(
            useDarkTheme = useDarkTheme,
            colorScales = colorScales
        )
    } else {
        // Use default palettes
        if (useDarkTheme) localDarkColorScheme else localLightColorScheme
    }

    // Apply semantic color overrides on top of the scale-derived palette
    val colorPalette = if (colorOverrides != null && !colorOverrides.isEmpty()) {
        applyColorOverrides(basePalette, colorOverrides)
    } else {
        basePalette
    }

    val typography = provideTextTypography(fontFamily)

    CompositionLocalProvider(
        LocalColorPalette provides colorPalette,
        LocalTextTypography provides typography,
        LocalShapeStyle provides shapeStyles,
        LocalIsDarkTheme provides useDarkTheme,
        content = content
    )
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
 * Applies semantic color overrides to a base palette.
 *
 * Any non-null field in [overrides] replaces the corresponding field in
 * the base palette. Null fields are kept from the base palette.
 *
 * @param base The base palette (typically derived from colorScales)
 * @param overrides The semantic color overrides to apply
 * @return A new ColorPalette with overrides applied
 */
private fun applyColorOverrides(
    base: ColorPalette,
    overrides: ColorOverrides
): ColorPalette = base.copy(
    brandSurfaceSubtle = overrides.brandSurfaceSubtle ?: base.brandSurfaceSubtle,
    brandSurfaceDefault = overrides.brandSurfaceDefault ?: base.brandSurfaceDefault,
    brandSurfaceFocus = overrides.brandSurfaceFocus ?: base.brandSurfaceFocus,
    brandBorderSubtle = overrides.brandBorderSubtle ?: base.brandBorderSubtle,
    brandBorderDefault = overrides.brandBorderDefault ?: base.brandBorderDefault,
    brandBorderFocus = overrides.brandBorderFocus ?: base.brandBorderFocus,
    brandContentSubtle = overrides.brandContentSubtle ?: base.brandContentSubtle,
    brandContentDefault = overrides.brandContentDefault ?: base.brandContentDefault,
    brandContentFocus = overrides.brandContentFocus ?: base.brandContentFocus,

    accentSurfaceSubtle = overrides.accentSurfaceSubtle ?: base.accentSurfaceSubtle,
    accentSurfaceDefault = overrides.accentSurfaceDefault ?: base.accentSurfaceDefault,
    accentSurfaceFocus = overrides.accentSurfaceFocus ?: base.accentSurfaceFocus,
    accentBorderSubtle = overrides.accentBorderSubtle ?: base.accentBorderSubtle,
    accentBorderDefault = overrides.accentBorderDefault ?: base.accentBorderDefault,
    accentBorderFocus = overrides.accentBorderFocus ?: base.accentBorderFocus,
    accentContentSubtle = overrides.accentContentSubtle ?: base.accentContentSubtle,
    accentContentDefault = overrides.accentContentDefault ?: base.accentContentDefault,
    accentContentFocus = overrides.accentContentFocus ?: base.accentContentFocus,

    baseSurfaceSubtle = overrides.baseSurfaceSubtle ?: base.baseSurfaceSubtle,
    baseSurfaceDefault = overrides.baseSurfaceDefault ?: base.baseSurfaceDefault,
    baseSurfaceElevated = overrides.baseSurfaceElevated ?: base.baseSurfaceElevated,
    baseSurfaceFocus = overrides.baseSurfaceFocus ?: base.baseSurfaceFocus,
    baseSurfaceShadow = overrides.baseSurfaceShadow ?: base.baseSurfaceShadow,
    baseSurfaceDisabled = overrides.baseSurfaceDisabled ?: base.baseSurfaceDisabled,
    baseBorderSubtle = overrides.baseBorderSubtle ?: base.baseBorderSubtle,
    baseBorderDefault = overrides.baseBorderDefault ?: base.baseBorderDefault,
    baseBorderFocus = overrides.baseBorderFocus ?: base.baseBorderFocus,
    baseBorderDisabled = overrides.baseBorderDisabled ?: base.baseBorderDisabled,
    baseContentTitle = overrides.baseContentTitle ?: base.baseContentTitle,
    baseContentSubtitle = overrides.baseContentSubtitle ?: base.baseContentSubtitle,
    baseContentBody = overrides.baseContentBody ?: base.baseContentBody,
    baseContentCaption = overrides.baseContentCaption ?: base.baseContentCaption,
    baseContentHint = overrides.baseContentHint ?: base.baseContentHint,
    baseContentNegative = overrides.baseContentNegative ?: base.baseContentNegative,
    baseContentDisabled = overrides.baseContentDisabled ?: base.baseContentDisabled,

    infoSurfaceSubtle = overrides.infoSurfaceSubtle ?: base.infoSurfaceSubtle,
    infoSurfaceDefault = overrides.infoSurfaceDefault ?: base.infoSurfaceDefault,
    infoSurfaceFocus = overrides.infoSurfaceFocus ?: base.infoSurfaceFocus,
    infoBorderSubtle = overrides.infoBorderSubtle ?: base.infoBorderSubtle,
    infoBorderDefault = overrides.infoBorderDefault ?: base.infoBorderDefault,
    infoBorderFocus = overrides.infoBorderFocus ?: base.infoBorderFocus,
    infoContentSubtle = overrides.infoContentSubtle ?: base.infoContentSubtle,
    infoContentDefault = overrides.infoContentDefault ?: base.infoContentDefault,
    infoContentFocus = overrides.infoContentFocus ?: base.infoContentFocus,

    successSurfaceSubtle = overrides.successSurfaceSubtle ?: base.successSurfaceSubtle,
    successSurfaceDefault = overrides.successSurfaceDefault ?: base.successSurfaceDefault,
    successSurfaceFocus = overrides.successSurfaceFocus ?: base.successSurfaceFocus,
    successBorderSubtle = overrides.successBorderSubtle ?: base.successBorderSubtle,
    successBorderDefault = overrides.successBorderDefault ?: base.successBorderDefault,
    successBorderFocus = overrides.successBorderFocus ?: base.successBorderFocus,
    successContentSubtle = overrides.successContentSubtle ?: base.successContentSubtle,
    successContentDefault = overrides.successContentDefault ?: base.successContentDefault,
    successContentFocus = overrides.successContentFocus ?: base.successContentFocus,

    warningSurfaceSubtle = overrides.warningSurfaceSubtle ?: base.warningSurfaceSubtle,
    warningSurfaceDefault = overrides.warningSurfaceDefault ?: base.warningSurfaceDefault,
    warningSurfaceFocus = overrides.warningSurfaceFocus ?: base.warningSurfaceFocus,
    warningBorderSubtle = overrides.warningBorderSubtle ?: base.warningBorderSubtle,
    warningBorderDefault = overrides.warningBorderDefault ?: base.warningBorderDefault,
    warningBorderFocus = overrides.warningBorderFocus ?: base.warningBorderFocus,
    warningContentSubtle = overrides.warningContentSubtle ?: base.warningContentSubtle,
    warningContentDefault = overrides.warningContentDefault ?: base.warningContentDefault,
    warningContentFocus = overrides.warningContentFocus ?: base.warningContentFocus,

    errorSurfaceSubtle = overrides.errorSurfaceSubtle ?: base.errorSurfaceSubtle,
    errorSurfaceDefault = overrides.errorSurfaceDefault ?: base.errorSurfaceDefault,
    errorSurfaceFocus = overrides.errorSurfaceFocus ?: base.errorSurfaceFocus,
    errorBorderSubtle = overrides.errorBorderSubtle ?: base.errorBorderSubtle,
    errorBorderDefault = overrides.errorBorderDefault ?: base.errorBorderDefault,
    errorBorderFocus = overrides.errorBorderFocus ?: base.errorBorderFocus,
    errorContentSubtle = overrides.errorContentSubtle ?: base.errorContentSubtle,
    errorContentDefault = overrides.errorContentDefault ?: base.errorContentDefault,
    errorContentFocus = overrides.errorContentFocus ?: base.errorContentFocus
)


