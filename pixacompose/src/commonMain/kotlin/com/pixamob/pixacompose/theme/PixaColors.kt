package com.pixamob.pixacompose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Semantic color roles. Every fill has a matching `*Foreground` for content drawn on top of it.
 * Components read these roles only; raw color values live in `styles/`.
 */
@Immutable
public data class PixaColors(
    val background: Color,
    val foreground: Color,
    val card: Color,
    val cardForeground: Color,
    val primary: Color,
    val primaryForeground: Color,
    val secondary: Color,
    val secondaryForeground: Color,
    val muted: Color,
    val mutedForeground: Color,
    val accent: Color,
    val accentForeground: Color,
    val destructive: Color,
    val destructiveForeground: Color,
    /** Confirmation: a saved change, a finished task. */
    val success: Color,
    val successForeground: Color,
    /** Outline of surfaces: cards, outlined buttons, separators. */
    val border: Color,
    /** Outline of form controls. */
    val input: Color,
    /** Focus indicator. */
    val ring: Color,
    /**
     * Distinct hues for telling things apart by category: a pet's ring, a log type, a tag.
     * Each is drawn at full strength as an icon or ring and as a soft fill behind it, so one
     * list can serve both themes. Read them with [category].
     */
    val categories: List<Color> = emptyList(),
)

/** The [index]-th category hue, wrapping around; [PixaColors.primary] when the style has none. */
public fun PixaColors.category(index: Int): Color =
    if (categories.isEmpty()) primary else categories[index.mod(categories.size)]
