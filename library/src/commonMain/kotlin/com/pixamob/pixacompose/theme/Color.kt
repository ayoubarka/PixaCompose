package com.pixamob.pixacompose.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Enhanced Brand Color Palette - Vibrant Green
val brandColor = mapOf(
    50  to Color(0xFFf6f2e4),  // lighter
    100 to Color(0xFFede4ca),
    200 to Color(0xFFe4d7af),
    300 to Color(0xFFdbc994),
    400 to Color(0xFFd2bc79),
    500 to Color(0xFFbf9f40),  // base
    600 to Color(0xFF866f2d),
    700 to Color(0xFF6b5924),
    800 to Color(0xFF50431b),
    900 to Color(0xFF352d12),
    950 to Color(0xFF1b1609) // darker
)

// Enhanced Accent Color Palette - Magenta/Pink
val accentColor = mapOf(
    50  to Color(0xFFe7f3f3),  // lighter
    100 to Color(0xFFcfe8e8),
    200 to Color(0xFFb7dcdc),
    300 to Color(0xFF9fd1d1),
    400 to Color(0xFF87c5c5),
    500 to Color(0xFF53acac),  // base - Dark Teal
    600 to Color(0xFF3a7878),
    700 to Color(0xFF2e6060),
    800 to Color(0xFF234848),
    900 to Color(0xFF173030),
    950 to Color(0xFF0c1818)   // darker
)

// Enhanced Base/Neutral Color Palette
val baseColor = mapOf(
    50  to Color(0xFFeeeeec),  // lighter
    100 to Color(0xFFdeddd9),
    200 to Color(0xFFcdcbc6),
    300 to Color(0xFFbdbab3),
    400 to Color(0xFFaca9a0),
    500 to Color(0xFF888477),  // base
    600 to Color(0xFF5f5c53),
    700 to Color(0xFF4c4a42),
    800 to Color(0xFF393732),
    900 to Color(0xFF262521),
    950 to Color(0xFF131211) // darker
)

// Info Color Palette - Blue
val infoColor = mapOf(
    50 to Color(0xFFe4ecf6), // lighter
    100 to Color(0xFFc9d8ed),
    200 to Color(0xFFafc5e4),
    300 to Color(0xFF94b2db),
    400 to Color(0xFF799ed2),
    500 to Color(0xFF4075bf), // base
    600 to Color(0xFF2d5286),
    700 to Color(0xFF24416b),
    800 to Color(0xFF1b3150),
    900 to Color(0xFF122136),
    950 to Color(0xFF09101b), // darker
)

// Success Color Palette - Green
val successColor = mapOf(
    50 to Color(0xFFe4f6e5), // lighter
    100 to Color(0xFFc9edca),
    200 to Color(0xFFafe4b0),
    300 to Color(0xFF94db95),
    400 to Color(0xFF79d27b),
    500 to Color(0xFF40bf42), // base
    600 to Color(0xFF2d862e),
    700 to Color(0xFF246b25),
    800 to Color(0xFF1b501c),
    900 to Color(0xFF123612),
    950 to Color(0xFF091b09), // darker
)

// Warning Color Palette - Amber
val warningColor = mapOf(
    50 to Color(0xFFf6f3e4), // lighter
    100 to Color(0xFFede7c9),
    200 to Color(0xFFe4dbaf),
    300 to Color(0xFFdbcf94),
    400 to Color(0xFFd2c379),
    500 to Color(0xFFbfaa40), // base
    600 to Color(0xFF86772d),
    700 to Color(0xFF6b5f24),
    800 to Color(0xFF50471b),
    900 to Color(0xFF363012),
    950 to Color(0xFF1b1809), // darker
)

// Error Color Palette - Red

val errorColor = mapOf(
    50 to Color(0xFFf6e6e4), // lighter
    100 to Color(0xFFedccc9),
    200 to Color(0xFFe4b3af),
    300 to Color(0xFFdb9a94),
    400 to Color(0xFFd28179),
    500 to Color(0xFFbf4a40), // base
    600 to Color(0xFF86342d),
    700 to Color(0xFF6b2a24),
    800 to Color(0xFF501f1b),
    900 to Color(0xFF361512),
    950 to Color(0xFF1b0a09), // darker
)
/**
 * OPTIMIZED COLOR SYSTEM
 * Core Rule: ALL *_content_default = weight 500 for both light and dark modes
 * This creates a consistent anchor point for content visibility
 */

/** Light Theme Colors - COMPLETE */

// Brand Colors - LIGHT MODE
val light_brand_surface_subtle: Color = brandColor[50]!!
val light_brand_surface_default: Color = brandColor[100]!!
val light_brand_surface_focus: Color = brandColor[200]!!

val light_brand_border_subtle: Color = brandColor[300]!!
val light_brand_border_default: Color = brandColor[400]!!
val light_brand_border_focus: Color = brandColor[500]!!       // Same as content anchor

val light_brand_content_subtle: Color = brandColor[600]!!     // Darker than anchor
val light_brand_content_default: Color = brandColor[500]!!    // ANCHOR - weight 500
val light_brand_content_focus: Color = brandColor[700]!!      // Even darker for emphasis

// Accent Colors - LIGHT MODE
val light_accent_surface_subtle: Color = accentColor[50]!!
val light_accent_surface_default: Color = accentColor[100]!!
val light_accent_surface_focus: Color = accentColor[200]!!

val light_accent_border_subtle: Color = accentColor[300]!!
val light_accent_border_default: Color = accentColor[400]!!
val light_accent_border_focus: Color = accentColor[500]!!

val light_accent_content_subtle: Color = accentColor[600]!!
val light_accent_content_default: Color = accentColor[500]!!  // ANCHOR - weight 500
val light_accent_content_focus: Color = accentColor[700]!!

// Base Colors - LIGHT MODE
val light_base_surface_subtle: Color = baseColor[50]!!
val light_base_surface_default: Color = baseColor[100]!!
val light_base_surface_focus: Color = baseColor[200]!!
val light_base_surface_disabled: Color = baseColor[300]?.copy(0.5f)!!
val light_base_surface_shadow: Color = baseColor[950]!!

val light_base_border_subtle: Color = baseColor[300]!!
val light_base_border_default: Color = baseColor[400]!!
val light_base_border_focus: Color = baseColor[500]!!
val light_base_border_disabled: Color = baseColor[300]?.copy(0.75f)!!

val light_base_content_title: Color = baseColor[950]!!        // Darkest - highest emphasis
val light_base_content_subtitle: Color = baseColor[800]!!     // Very dark
val light_base_content_body: Color = baseColor[600]!!         // Dark
val light_base_content_caption: Color = baseColor[500]!!      // ANCHOR - weight 500
val light_base_content_hint: Color = baseColor[400]!!         // Lighter hint
val light_base_content_negative: Color = baseColor[100]!!     // Light text on dark bg
val light_base_content_disabled: Color = baseColor[500]?.copy(0.5F)!!

// Info Colors - LIGHT MODE
val light_info_surface_subtle: Color = infoColor[50]!!
val light_info_surface_default: Color = infoColor[100]!!
val light_info_surface_focus: Color = infoColor[200]!!

val light_info_border_subtle: Color = infoColor[300]!!
val light_info_border_default: Color = infoColor[400]!!
val light_info_border_focus: Color = infoColor[500]!!

val light_info_content_subtle: Color = infoColor[600]!!
val light_info_content_default: Color = infoColor[500]!!      // ANCHOR - weight 500
val light_info_content_focus: Color = infoColor[700]!!

// Success Colors - LIGHT MODE
val light_success_surface_subtle: Color = successColor[50]!!
val light_success_surface_default: Color = successColor[100]!!
val light_success_surface_focus: Color = successColor[200]!!

val light_success_border_subtle: Color = successColor[300]!!
val light_success_border_default: Color = successColor[400]!!
val light_success_border_focus: Color = successColor[500]!!

val light_success_content_subtle: Color = successColor[600]!!
val light_success_content_default: Color = successColor[500]!! // ANCHOR - weight 500
val light_success_content_focus: Color = successColor[700]!!

// Warning Colors - LIGHT MODE
val light_warning_surface_subtle: Color = warningColor[50]!!
val light_warning_surface_default: Color = warningColor[100]!!
val light_warning_surface_focus: Color = warningColor[200]!!

val light_warning_border_subtle: Color = warningColor[300]!!
val light_warning_border_default: Color = warningColor[400]!!
val light_warning_border_focus: Color = warningColor[500]!!

val light_warning_content_subtle: Color = warningColor[600]!!
val light_warning_content_default: Color = warningColor[500]!! // ANCHOR - weight 500
val light_warning_content_focus: Color = warningColor[700]!!

// Error Colors - LIGHT MODE
val light_error_surface_subtle: Color = errorColor[50]!!
val light_error_surface_default: Color = errorColor[100]!!
val light_error_surface_focus: Color = errorColor[200]!!

val light_error_border_subtle: Color = errorColor[300]!!
val light_error_border_default: Color = errorColor[400]!!
val light_error_border_focus: Color = errorColor[500]!!

val light_error_content_subtle: Color = errorColor[600]!!
val light_error_content_default: Color = errorColor[500]!!     // ANCHOR - weight 500
val light_error_content_focus: Color = errorColor[700]!!

/** Dark Theme Colors - COMPLETE */

// Brand Colors - DARK MODE
val dark_brand_surface_subtle: Color = brandColor[700]!!
val dark_brand_surface_default: Color = brandColor[800]!!
val dark_brand_surface_focus: Color = brandColor[900]!!

val dark_brand_border_subtle: Color = brandColor[900]!!
val dark_brand_border_default: Color = brandColor[700]!!
val dark_brand_border_focus: Color = brandColor[500]!!        // Same as content anchor

val dark_brand_content_subtle: Color = brandColor[400]!!      // Lighter than anchor
val dark_brand_content_default: Color = brandColor[500]!!     // ANCHOR - weight 500
val dark_brand_content_focus: Color = brandColor[300]!!       // Even lighter for emphasis

// Accent Colors - DARK MODE
val dark_accent_surface_subtle: Color = accentColor[700]!!
val dark_accent_surface_default: Color = accentColor[800]!!
val dark_accent_surface_focus: Color = accentColor[900]!!

val dark_accent_border_subtle: Color = accentColor[900]!!
val dark_accent_border_default: Color = accentColor[700]!!
val dark_accent_border_focus: Color = accentColor[500]!!

val dark_accent_content_subtle: Color = accentColor[400]!!
val dark_accent_content_default: Color = accentColor[500]!!   // ANCHOR - weight 500
val dark_accent_content_focus: Color = accentColor[300]!!

// Base Colors - DARK MODE
val dark_base_surface_subtle: Color = baseColor[900]!!
val dark_base_surface_default: Color = baseColor[800]!!
val dark_base_surface_focus: Color = baseColor[700]!!
val dark_base_surface_disabled: Color = baseColor[950]?.copy(0.5F)!!
val dark_base_surface_shadow: Color = brandColor[950]!!

val dark_base_border_subtle: Color = baseColor[600]!!
val dark_base_border_default: Color = baseColor[700]!!
val dark_base_border_focus: Color = baseColor[800]!!
val dark_base_border_disabled: Color = brandColor[900]!!

val dark_base_content_title: Color = baseColor[50]!!         // Lightest - highest emphasis
val dark_base_content_subtitle: Color = baseColor[200]!!      // Very light
val dark_base_content_body: Color = baseColor[400]!!          // Light
val dark_base_content_caption: Color = baseColor[500]!!       // ANCHOR - weight 500
val dark_base_content_hint: Color = baseColor[600]!!          // Darker hint
val dark_base_content_negative: Color = baseColor[900]!!      // Dark text on light bg
val dark_base_content_disabled: Color = baseColor[400]?.copy(0.75F)!!

// Info Colors - DARK MODE
val dark_info_surface_subtle: Color = infoColor[700]!!
val dark_info_surface_default: Color = infoColor[800]!!
val dark_info_surface_focus: Color = infoColor[900]!!

val dark_info_border_subtle: Color = infoColor[900]!!
val dark_info_border_default: Color = infoColor[700]!!
val dark_info_border_focus: Color = infoColor[500]!!

val dark_info_content_subtle: Color = infoColor[400]!!
val dark_info_content_default: Color = infoColor[500]!!       // ANCHOR - weight 500
val dark_info_content_focus: Color = infoColor[300]!!

// Success Colors - DARK MODE
val dark_success_surface_subtle: Color = successColor[700]!!
val dark_success_surface_default: Color = successColor[800]!!
val dark_success_surface_focus: Color = successColor[900]!!

val dark_success_border_subtle: Color = successColor[900]!!
val dark_success_border_default: Color = successColor[700]!!
val dark_success_border_focus: Color = successColor[500]!!

val dark_success_content_subtle: Color = successColor[400]!!
val dark_success_content_default: Color = successColor[500]!! // ANCHOR - weight 500
val dark_success_content_focus: Color = successColor[300]!!

// Warning Colors - DARK MODE
val dark_warning_surface_subtle: Color = warningColor[700]!!
val dark_warning_surface_default: Color = warningColor[800]!!
val dark_warning_surface_focus: Color = warningColor[900]!!

val dark_warning_border_subtle: Color = warningColor[900]!!
val dark_warning_border_default: Color = warningColor[700]!!
val dark_warning_border_focus: Color = warningColor[500]!!

val dark_warning_content_subtle: Color = warningColor[400]!!
val dark_warning_content_default: Color = warningColor[500]!! // ANCHOR - weight 500
val dark_warning_content_focus: Color = warningColor[300]!!

// Error Colors - DARK MODE
val dark_error_surface_subtle: Color = errorColor[700]!!
val dark_error_surface_default: Color = errorColor[800]!!
val dark_error_surface_focus: Color = errorColor[600]!!

val dark_error_border_subtle: Color = errorColor[900]!!
val dark_error_border_default: Color = errorColor[700]!!
val dark_error_border_focus: Color = errorColor[500]!!

val dark_error_content_subtle: Color = errorColor[400]!!
val dark_error_content_default: Color = errorColor[500]!!     // ANCHOR - weight 500
val dark_error_content_focus: Color = errorColor[300]!!


/** Color Schemes */

data class ColorPalette(
    val brandSurfaceSubtle: Color = Color.Unspecified,
    val brandSurfaceDefault: Color = Color.Unspecified,
    val brandSurfaceFocus: Color = Color.Unspecified,
    val brandBorderSubtle: Color = Color.Unspecified,
    val brandBorderDefault: Color = Color.Unspecified,
    val brandBorderFocus: Color = Color.Unspecified,
    val brandContentSubtle: Color = Color.Unspecified,
    val brandContentDefault: Color = Color.Unspecified,
    val brandContentFocus: Color = Color.Unspecified,

    val accentSurfaceSubtle: Color = Color.Unspecified,
    val accentSurfaceDefault: Color = Color.Unspecified,
    val accentSurfaceFocus: Color = Color.Unspecified,
    val accentBorderSubtle: Color = Color.Unspecified,
    val accentBorderDefault: Color = Color.Unspecified,
    val accentBorderFocus: Color = Color.Unspecified,
    val accentContentSubtle: Color = Color.Unspecified,
    val accentContentDefault: Color = Color.Unspecified,
    val accentContentFocus: Color = Color.Unspecified,

    val baseSurfaceSubtle: Color = Color.Unspecified,
    val baseSurfaceDefault: Color = Color.Unspecified,
    val baseSurfaceElevated: Color = Color.Unspecified,
    val baseSurfaceFocus: Color = Color.Unspecified,
    val baseSurfaceShadow: Color = Color.Unspecified,
    val baseSurfaceDisabled: Color = Color.Unspecified,
    val baseBorderSubtle: Color = Color.Unspecified,
    val baseBorderDefault: Color = Color.Unspecified,
    val baseBorderFocus: Color = Color.Unspecified,
    val baseBorderDisabled: Color = Color.Unspecified,
    val baseContentTitle: Color = Color.Unspecified,
    val baseContentSubtitle: Color = Color.Unspecified,
    val baseContentBody: Color = Color.Unspecified,
    val baseContentCaption: Color = Color.Unspecified,
    val baseContentHint: Color = Color.Unspecified,
    val baseContentNegative: Color = Color.Unspecified,
    val baseContentDisabled: Color = Color.Unspecified,

    val infoSurfaceSubtle: Color = Color.Unspecified,
    val infoSurfaceDefault: Color = Color.Unspecified,
    val infoSurfaceFocus: Color = Color.Unspecified,
    val infoBorderSubtle: Color = Color.Unspecified,
    val infoBorderDefault: Color = Color.Unspecified,
    val infoBorderFocus: Color = Color.Unspecified,
    val infoContentSubtle: Color = Color.Unspecified,
    val infoContentDefault: Color = Color.Unspecified,
    val infoContentFocus: Color = Color.Unspecified,

    val successSurfaceSubtle: Color = Color.Unspecified,
    val successSurfaceDefault: Color = Color.Unspecified,
    val successSurfaceFocus: Color = Color.Unspecified,
    val successBorderSubtle: Color = Color.Unspecified,
    val successBorderDefault: Color = Color.Unspecified,
    val successBorderFocus: Color = Color.Unspecified,
    val successContentSubtle: Color = Color.Unspecified,
    val successContentDefault: Color = Color.Unspecified,
    val successContentFocus: Color = Color.Unspecified,

    val warningSurfaceSubtle: Color = Color.Unspecified,
    val warningSurfaceDefault: Color = Color.Unspecified,
    val warningSurfaceFocus: Color = Color.Unspecified,
    val warningBorderSubtle: Color = Color.Unspecified,
    val warningBorderDefault: Color = Color.Unspecified,
    val warningBorderFocus: Color = Color.Unspecified,
    val warningContentSubtle: Color = Color.Unspecified,
    val warningContentDefault: Color = Color.Unspecified,
    val warningContentFocus: Color = Color.Unspecified,

    val errorSurfaceSubtle: Color = Color.Unspecified,
    val errorSurfaceDefault: Color = Color.Unspecified,
    val errorSurfaceFocus: Color = Color.Unspecified,
    val errorBorderSubtle: Color = Color.Unspecified,
    val errorBorderDefault: Color = Color.Unspecified,
    val errorBorderFocus: Color = Color.Unspecified,
    val errorContentSubtle: Color = Color.Unspecified,
    val errorContentDefault: Color = Color.Unspecified,
    val errorContentFocus: Color = Color.Unspecified,
)

val localLightColorScheme = ColorPalette(
    brandSurfaceSubtle = light_brand_surface_subtle,
    brandBorderSubtle = light_brand_border_subtle,
    brandContentSubtle = light_brand_content_subtle,
    brandSurfaceDefault = light_brand_surface_default,
    brandBorderDefault = light_brand_border_default,
    brandContentDefault = light_brand_content_default,
    brandSurfaceFocus = light_brand_surface_focus,
    brandBorderFocus = light_brand_border_focus,
    brandContentFocus = light_brand_content_focus,

    accentSurfaceSubtle = light_accent_surface_subtle,
    accentBorderSubtle = light_accent_border_subtle,
    accentContentSubtle = light_accent_content_subtle,
    accentSurfaceDefault = light_accent_surface_default,
    accentBorderDefault = light_accent_border_default,
    accentContentDefault = light_accent_content_default,
    accentSurfaceFocus = light_accent_surface_focus,
    accentBorderFocus = light_accent_border_focus,
    accentContentFocus = light_accent_content_focus,

    baseSurfaceSubtle = light_base_surface_subtle,
    baseSurfaceDefault = light_base_surface_default,
    baseSurfaceElevated = light_base_surface_focus,
    baseSurfaceFocus = light_base_surface_focus,
    baseSurfaceShadow = light_base_surface_shadow,
    baseSurfaceDisabled = light_base_surface_disabled,
    baseBorderSubtle = light_base_border_subtle,
    baseBorderDefault = light_base_border_default,
    baseBorderFocus = light_base_border_focus,
    baseBorderDisabled = light_base_border_disabled,
    baseContentTitle = light_base_content_title,
    baseContentSubtitle = light_base_content_subtitle,
    baseContentBody = light_base_content_body,
    baseContentCaption = light_base_content_caption,
    baseContentHint = light_base_content_hint,
    baseContentNegative = light_base_content_negative,
    baseContentDisabled = light_base_content_disabled,

    infoSurfaceSubtle = light_info_surface_subtle,
    infoBorderSubtle = light_info_border_subtle,
    infoContentSubtle = light_info_content_subtle,
    infoSurfaceDefault = light_info_surface_default,
    infoBorderDefault = light_info_border_default,
    infoContentDefault = light_info_content_default,
    infoSurfaceFocus = light_info_surface_focus,
    infoBorderFocus = light_info_border_focus,
    infoContentFocus = light_info_content_focus,

    successSurfaceSubtle = light_success_surface_subtle,
    successBorderSubtle = light_success_border_subtle,
    successContentSubtle = light_success_content_subtle,
    successSurfaceDefault = light_success_surface_default,
    successBorderDefault = light_success_border_default,
    successContentDefault = light_success_content_default,
    successSurfaceFocus = light_success_surface_focus,
    successBorderFocus = light_success_border_focus,
    successContentFocus = light_success_content_focus,

    warningSurfaceSubtle = light_warning_surface_subtle,
    warningBorderSubtle = light_warning_border_subtle,
    warningContentSubtle = light_warning_content_subtle,
    warningSurfaceDefault = light_warning_surface_default,
    warningBorderDefault = light_warning_border_default,
    warningContentDefault = light_warning_content_default,
    warningSurfaceFocus = light_warning_surface_focus,
    warningBorderFocus = light_warning_border_focus,
    warningContentFocus = light_warning_content_focus,

    errorSurfaceSubtle = light_error_surface_subtle,
    errorBorderSubtle = light_error_border_subtle,
    errorContentSubtle = light_error_content_subtle,
    errorSurfaceDefault = light_error_surface_default,
    errorBorderDefault = light_error_border_default,
    errorContentDefault = light_error_content_default,
    errorSurfaceFocus = light_error_surface_focus,
    errorBorderFocus = light_error_border_focus,
    errorContentFocus = light_error_content_focus,
)

val localDarkColorScheme = ColorPalette(
    brandSurfaceSubtle = dark_brand_surface_subtle,
    brandBorderSubtle = dark_brand_border_subtle,
    brandContentSubtle = dark_brand_content_subtle,
    brandSurfaceDefault = dark_brand_surface_default,
    brandBorderDefault = dark_brand_border_default,
    brandContentDefault = dark_brand_content_default,
    brandSurfaceFocus = dark_brand_surface_focus,
    brandBorderFocus = dark_brand_border_focus,
    brandContentFocus = dark_brand_content_focus,

    accentSurfaceSubtle = dark_accent_surface_subtle,
    accentBorderSubtle = dark_accent_border_subtle,
    accentContentSubtle = dark_accent_content_subtle,
    accentSurfaceDefault = dark_accent_surface_default,
    accentBorderDefault = dark_accent_border_default,
    accentContentDefault = dark_accent_content_default,
    accentSurfaceFocus = dark_accent_surface_focus,
    accentBorderFocus = dark_accent_border_focus,
    accentContentFocus = dark_accent_content_focus,

    baseSurfaceSubtle = dark_base_surface_subtle,
    baseSurfaceDefault = dark_base_surface_default,
    baseSurfaceElevated = dark_base_surface_focus,
    baseSurfaceFocus = dark_base_surface_focus,
    baseSurfaceShadow = dark_base_surface_shadow,
    baseSurfaceDisabled = dark_base_surface_disabled,
    baseBorderSubtle = dark_base_border_subtle,
    baseBorderDefault = dark_base_border_default,
    baseBorderFocus = dark_base_border_focus,
    baseBorderDisabled = dark_base_border_disabled,
    baseContentTitle = dark_base_content_title,
    baseContentSubtitle = dark_base_content_subtitle,
    baseContentBody = dark_base_content_body,
    baseContentCaption = dark_base_content_caption,
    baseContentHint = dark_base_content_hint,
    baseContentNegative = dark_base_content_negative,
    baseContentDisabled = dark_base_content_disabled,

    infoSurfaceSubtle = dark_info_surface_subtle,
    infoBorderSubtle = dark_info_border_subtle,
    infoContentSubtle = dark_info_content_subtle,
    infoSurfaceDefault = dark_info_surface_default,
    infoBorderDefault = dark_info_border_default,
    infoContentDefault = dark_info_content_default,
    infoSurfaceFocus = dark_info_surface_focus,
    infoBorderFocus = dark_info_border_focus,
    infoContentFocus = dark_info_content_focus,

    successSurfaceSubtle = dark_success_surface_subtle,
    successBorderSubtle = dark_success_border_subtle,
    successContentSubtle = dark_success_content_subtle,
    successSurfaceDefault = dark_success_surface_default,
    successBorderDefault = dark_success_border_default,
    successContentDefault = dark_success_content_default,
    successSurfaceFocus = dark_success_surface_focus,
    successBorderFocus = dark_success_border_focus,
    successContentFocus = dark_success_content_focus,

    warningSurfaceSubtle = dark_warning_surface_subtle,
    warningBorderSubtle = dark_warning_border_subtle,
    warningContentSubtle = dark_warning_content_subtle,
    warningSurfaceDefault = dark_warning_surface_default,
    warningBorderDefault = dark_warning_border_default,
    warningContentDefault = dark_warning_content_default,
    warningSurfaceFocus = dark_warning_surface_focus,
    warningBorderFocus = dark_warning_border_focus,
    warningContentFocus = dark_warning_content_focus,

    errorSurfaceSubtle = dark_error_surface_subtle,
    errorBorderSubtle = dark_error_border_subtle,
    errorContentSubtle = dark_error_content_subtle,
    errorSurfaceDefault = dark_error_surface_default,
    errorBorderDefault = dark_error_border_default,
    errorContentDefault = dark_error_content_default,
    errorSurfaceFocus = dark_error_surface_focus,
    errorBorderFocus = dark_error_border_focus,
    errorContentFocus = dark_error_content_focus,
)

val LocalColorPalette = staticCompositionLocalOf { ColorPalette() }
