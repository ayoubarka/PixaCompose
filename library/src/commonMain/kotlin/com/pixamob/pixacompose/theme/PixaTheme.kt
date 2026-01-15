package com.pixamob.pixacompose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

/**
 * App Theme Configuration
 * Provides centralized theming with support for light/dark modes and customizable colors/fonts
 *
 * @param useDarkTheme Whether to use dark theme (defaults to system preference)
 * @param lightColorPalette Optional custom light color palette (defaults to built-in light colors)
 * @param darkColorPalette Optional custom dark color palette (defaults to built-in dark colors)
 * @param fontFamily Optional custom font family (defaults to system font)
 * @param content The composable content to wrap with theme
 */
@Composable
fun PixaTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    lightColorPalette: ColorPalette? = null,
    darkColorPalette: ColorPalette? = null,
    fontFamily: FontFamily? = null,
    content: @Composable () -> Unit
) {
    val colorPalette = if (useDarkTheme) {
        darkColorPalette ?: localDarkColorScheme
    } else {
        lightColorPalette ?: localLightColorScheme
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
