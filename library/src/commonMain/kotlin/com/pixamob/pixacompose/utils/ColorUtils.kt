package com.pixamob.pixacompose.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Color Utilities
 *
 * Provides conversion functions between various color spaces (RGB, HSV, HSL)
 * and utility functions for color manipulation used by ColorPicker components.
 */

// ============================================================================
// HSV Conversion
// ============================================================================

/**
 * Converts RGB color to HSV (Hue, Saturation, Value) representation
 *
 * @return Triple of (hue: 0-360°, saturation: 0-1, value: 0-1)
 */
fun Color.toHSV(): HSV {
    val r = red
    val g = green
    val b = blue

    val cmax = max(r, max(g, b))
    val cmin = min(r, min(g, b))
    val delta = cmax - cmin

    // Calculate hue
    val hue = when {
        delta == 0f -> 0f
        cmax == r -> ((g - b) / delta % 6) * 60f
        cmax == g -> ((b - r) / delta + 2) * 60f
        else -> ((r - g) / delta + 4) * 60f
    }.let { if (it < 0) it + 360f else it }

    // Calculate saturation
    val saturation = if (cmax == 0f) 0f else delta / cmax

    // Calculate value
    val value = cmax

    return HSV(hue, saturation, value)
}

/**
 * Creates a Color from HSV values
 *
 * @param hue 0-360 degrees
 * @param saturation 0-1
 * @param value 0-1
 * @param alpha 0-1
 */
fun Color.Companion.hsv(hue: Float, saturation: Float, value: Float, alpha: Float = 1f): Color {
    val h = (hue % 360f).let { if (it < 0) it + 360f else it }
    val s = saturation.coerceIn(0f, 1f)
    val v = value.coerceIn(0f, 1f)

    val c = v * s
    val x = c * (1 - kotlin.math.abs((h / 60f) % 2 - 1))
    val m = v - c

    val (r, g, b) = when {
        h < 60 -> Triple(c, x, 0f)
        h < 120 -> Triple(x, c, 0f)
        h < 180 -> Triple(0f, c, x)
        h < 240 -> Triple(0f, x, c)
        h < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(r + m, g + m, b + m, alpha)
}

// ============================================================================
// HSL Conversion
// ============================================================================

/**
 * Converts RGB color to HSL (Hue, Saturation, Lightness) representation
 *
 * @return Triple of (hue: 0-360°, saturation: 0-1, lightness: 0-1)
 */
fun Color.toHSL(): HSL {
    val r = red
    val g = green
    val b = blue

    val cmax = max(r, max(g, b))
    val cmin = min(r, min(g, b))
    val delta = cmax - cmin

    // Calculate hue
    val hue = when {
        delta == 0f -> 0f
        cmax == r -> ((g - b) / delta % 6) * 60f
        cmax == g -> ((b - r) / delta + 2) * 60f
        else -> ((r - g) / delta + 4) * 60f
    }.let { if (it < 0) it + 360f else it }

    // Calculate lightness
    val lightness = (cmax + cmin) / 2f

    // Calculate saturation
    val saturation = if (delta == 0f) 0f else delta / (1 - kotlin.math.abs(2 * lightness - 1))

    return HSL(hue, saturation, lightness)
}

/**
 * Creates a Color from HSL values
 *
 * @param hue 0-360 degrees
 * @param saturation 0-1
 * @param lightness 0-1
 * @param alpha 0-1
 */
fun Color.Companion.hsl(hue: Float, saturation: Float, lightness: Float, alpha: Float = 1f): Color {
    val h = (hue % 360f).let { if (it < 0) it + 360f else it }
    val s = saturation.coerceIn(0f, 1f)
    val l = lightness.coerceIn(0f, 1f)

    val c = (1 - kotlin.math.abs(2 * l - 1)) * s
    val x = c * (1 - kotlin.math.abs((h / 60f) % 2 - 1))
    val m = l - c / 2

    val (r, g, b) = when {
        h < 60 -> Triple(c, x, 0f)
        h < 120 -> Triple(x, c, 0f)
        h < 180 -> Triple(0f, c, x)
        h < 240 -> Triple(0f, x, c)
        h < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(r + m, g + m, b + m, alpha)
}

// ============================================================================
// Hex Conversion
// ============================================================================

/**
 * Converts Color to hex string (RRGGBB or AARRGGBB format)
 *
 * @param includeAlpha Whether to include alpha channel in hex string
 */
fun Color.toHexString(includeAlpha: Boolean = true): String {
    val a = (alpha * 255).roundToInt()
    val r = (red * 255).roundToInt()
    val g = (green * 255).roundToInt()
    val b = (blue * 255).roundToInt()

    return if (includeAlpha) {
        "#${a.toHex()}${r.toHex()}${g.toHex()}${b.toHex()}"
    } else {
        "#${r.toHex()}${g.toHex()}${b.toHex()}"
    }
}

/**
 * Parses hex string to Color
 * Supports formats: #RGB, #RRGGBB, #AARRGGBB, #RRGGBBAA
 */
fun Color.Companion.fromHex(hex: String): Color? {
    val cleanHex = hex.removePrefix("#")

    return try {
        when (cleanHex.length) {
            3 -> {
                // #RGB -> #RRGGBB
                val r = cleanHex[0].toString().repeat(2).toInt(16)
                val g = cleanHex[1].toString().repeat(2).toInt(16)
                val b = cleanHex[2].toString().repeat(2).toInt(16)
                Color(r / 255f, g / 255f, b / 255f, 1f)
            }
            6 -> {
                // #RRGGBB
                val r = cleanHex.substring(0, 2).toInt(16)
                val g = cleanHex.substring(2, 4).toInt(16)
                val b = cleanHex.substring(4, 6).toInt(16)
                Color(r / 255f, g / 255f, b / 255f, 1f)
            }
            8 -> {
                // #AARRGGBB or #RRGGBBAA - check which format
                val first = cleanHex.substring(0, 2).toInt(16)
                val second = cleanHex.substring(2, 4).toInt(16)
                val third = cleanHex.substring(4, 6).toInt(16)
                val fourth = cleanHex.substring(6, 8).toInt(16)

                // Assume #AARRGGBB format
                Color(second / 255f, third / 255f, fourth / 255f, first / 255f)
            }
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}

private fun Int.toHex(): String = this.toString(16).padStart(2, '0').uppercase()

// ============================================================================
// Geometry Utilities
// ============================================================================

/**
 * Creates an Offset from angle and distance
 */
fun Offset.Companion.fromPolar(angle: Float, distance: Float): Offset {
    return Offset(
        x = distance * cos(angle),
        y = distance * sin(angle)
    )
}

/**
 * Calculates the angle of this offset from origin
 */
fun Offset.angle(): Float = atan2(y, x)

/**
 * Calculates the distance from origin
 */
fun Offset.length(): Float = sqrt(x * x + y * y)

/**
 * Converts degrees to radians
 */
fun Float.toRadians(): Float = this * kotlin.math.PI.toFloat() / 180f

/**
 * Converts radians to degrees
 */
fun Float.toDegrees(): Float = this * 180f / kotlin.math.PI.toFloat()

// ============================================================================
// Data Classes
// ============================================================================

/**
 * HSV color representation
 */
data class HSV(
    val hue: Float,        // 0-360
    val saturation: Float, // 0-1
    val value: Float       // 0-1
)

/**
 * HSL color representation
 */
data class HSL(
    val hue: Float,        // 0-360
    val saturation: Float, // 0-1
    val lightness: Float   // 0-1
)

// ============================================================================
// Color Manipulation Extensions
// ============================================================================

/**
 * Get color with alpha transparency
 */
fun Color.withAlpha(alpha: Float): Color = this.copy(alpha = alpha)

/**
 * Blend two colors
 */
fun Color.blend(other: Color, ratio: Float = 0.5f): Color {
    val inverseRatio = 1 - ratio
    return Color(
        red = this.red * inverseRatio + other.red * ratio,
        green = this.green * inverseRatio + other.green * ratio,
        blue = this.blue * inverseRatio + other.blue * ratio,
        alpha = this.alpha * inverseRatio + other.alpha * ratio
    )
}

/**
 * Lighten a color by a percentage
 */
fun Color.lighten(percentage: Float = 0.1f): Color {
    return this.blend(Color.White, percentage)
}

/**
 * Darken a color by a percentage
 */
fun Color.darken(percentage: Float = 0.1f): Color {
    return this.blend(Color.Black, percentage)
}

/**
 * Get contrasting color (black or white) based on luminance
 */
fun Color.contrastColor(): Color {
    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue)
    return if (luminance > 0.5) Color.Black else Color.White
}

// ============================================================================
// Material Color Palettes
// ============================================================================

/**
 * Material Design 3 color palette
 */
object MaterialColors {
    @Suppress("unused")
    val material3Colors = listOf(
        Color(0xFFEF5350), // Red
        Color(0xFFEC407A), // Pink
        Color(0xFFAB47BC), // Purple
        Color(0xFF7E57C2), // Deep Purple
        Color(0xFF5C6BC0), // Indigo
        Color(0xFF42A5F5), // Blue
        Color(0xFF29B6F6), // Light Blue
        Color(0xFF26C6DA), // Cyan
        Color(0xFF26A69A), // Teal
        Color(0xFF66BB6A), // Green
        Color(0xFF9CCC65), // Light Green
        Color(0xFFD4E157), // Lime
        Color(0xFFFFEE58), // Yellow
        Color(0xFFFFCA28), // Amber
        Color(0xFFFF9800), // Orange
        Color(0xFFFF7043), // Deep Orange
        Color(0xFF8D6E63), // Brown
        Color(0xFFBDBDBD), // Grey
        Color(0xFF78909C), // Blue Grey
        Color(0xFF000000), // Black
        Color(0xFFFFFFFF), // White
    )

    @Suppress("unused")
    val material2Colors = listOf(
        Color(0xFFF44336), // Red 500
        Color(0xFFE91E63), // Pink 500
        Color(0xFF9C27B0), // Purple 500
        Color(0xFF673AB7), // Deep Purple 500
        Color(0xFF3F51B5), // Indigo 500
        Color(0xFF2196F3), // Blue 500
        Color(0xFF03A9F4), // Light Blue 500
        Color(0xFF00BCD4), // Cyan 500
        Color(0xFF009688), // Teal 500
        Color(0xFF4CAF50), // Green 500
        Color(0xFF8BC34A), // Light Green 500
        Color(0xFFCDDC39), // Lime 500
        Color(0xFFFFEB3B), // Yellow 500
        Color(0xFFFFC107), // Amber 500
        Color(0xFFFF9800), // Orange 500
        Color(0xFFFF5722), // Deep Orange 500
    )

    @Suppress("unused")
    val basicColors = listOf(
        Color.Red, Color.Green, Color.Blue,
        Color.Yellow, Color.Cyan, Color.Magenta,
        Color.Black, Color.White, Color.Gray,
        Color(0xFFFF6B6B), // Light Red
        Color(0xFF4ECDC4), // Light Cyan
        Color(0xFF95E1D3), // Mint
    )
}

