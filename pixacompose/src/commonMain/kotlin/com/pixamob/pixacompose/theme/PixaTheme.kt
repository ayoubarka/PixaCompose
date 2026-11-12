package com.pixamob.pixacompose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.pixamob.pixacompose.primitives.LocalContentColor
import com.pixamob.pixacompose.primitives.LocalTextStyle
import com.pixamob.pixacompose.styles.PixaStyles

private val LocalPixaStyle = staticCompositionLocalOf { PixaStyles.Minimal }
private val LocalIsDark = staticCompositionLocalOf { false }

@Composable
public fun PixaTheme(
    style: PixaStyle = PixaStyles.Minimal,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) style.darkColors else style.lightColors
    CompositionLocalProvider(
        LocalPixaStyle provides style,
        LocalIsDark provides darkTheme,
        LocalContentColor provides colors.foreground,
        LocalTextStyle provides style.typography.body,
        content = content,
    )
}

/** Read access to the current style, e.g. `PixaTheme.colors.primary`. */
public object PixaTheme {
    public val style: PixaStyle
        @Composable @ReadOnlyComposable get() = LocalPixaStyle.current

    public val isDark: Boolean
        @Composable @ReadOnlyComposable get() = LocalIsDark.current

    public val colors: PixaColors
        @Composable @ReadOnlyComposable
        get() = if (LocalIsDark.current) LocalPixaStyle.current.darkColors else LocalPixaStyle.current.lightColors

    public val shapes: PixaShapes
        @Composable @ReadOnlyComposable get() = LocalPixaStyle.current.shapes

    public val typography: PixaTypography
        @Composable @ReadOnlyComposable get() = LocalPixaStyle.current.typography

    public val decoration: PixaDecoration
        @Composable @ReadOnlyComposable get() = LocalPixaStyle.current.decoration

    public val spacing: PixaSpacing
        @Composable @ReadOnlyComposable get() = LocalPixaStyle.current.spacing

    public val motion: PixaMotion
        @Composable @ReadOnlyComposable get() = LocalPixaStyle.current.motion
}
