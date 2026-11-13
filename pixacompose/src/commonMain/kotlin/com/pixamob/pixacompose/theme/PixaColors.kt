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
)
