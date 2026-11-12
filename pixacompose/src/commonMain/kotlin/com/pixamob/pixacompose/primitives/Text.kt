package com.pixamob.pixacompose.primitives

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

/** Color for text and icons, set by the nearest surface. */
public val LocalContentColor: androidx.compose.runtime.ProvidableCompositionLocal<Color> =
    compositionLocalOf { Color.Black }

/** Default text style, set by the theme and by components for their content slot. */
public val LocalTextStyle: androidx.compose.runtime.ProvidableCompositionLocal<TextStyle> =
    compositionLocalOf { TextStyle.Default }

@Composable
public fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val resolvedColor = color.takeOrElse { style.color.takeOrElse { LocalContentColor.current } }
    BasicText(
        text = text,
        modifier = modifier,
        style = style.merge(color = resolvedColor, textAlign = textAlign ?: TextAlign.Unspecified),
        maxLines = maxLines,
        overflow = overflow,
    )
}

/** Sets the content color and text style for a component's content slot. */
@Composable
internal fun ProvideContentStyle(
    color: Color,
    textStyle: TextStyle = LocalTextStyle.current,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides color,
        LocalTextStyle provides textStyle,
        content = content,
    )
}
