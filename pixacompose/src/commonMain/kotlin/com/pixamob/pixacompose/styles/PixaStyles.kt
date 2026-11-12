package com.pixamob.pixacompose.styles

import com.pixamob.pixacompose.theme.PixaStyle

/** Built-in styles. An app can also build its own [PixaStyle] or `copy()` one of these. */
public object PixaStyles {
    public val Minimal: PixaStyle = minimalStyle()
    public val Cartoon: PixaStyle = cartoonStyle()

    public val all: List<PixaStyle> = listOf(Minimal, Cartoon)
}
