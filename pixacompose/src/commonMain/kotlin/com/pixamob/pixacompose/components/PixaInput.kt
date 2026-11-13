package com.pixamob.pixacompose.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.DisabledAlpha
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaShadow
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * Single-line text field.
 *
 * @param leading content before the text, usually an icon.
 * @param trailing content after the text: an icon, a clear button, a unit.
 */
@Composable
public fun PixaInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    /** Draws the outline in the destructive color. Defaults to true inside a [PixaField] with an error. */
    isError: Boolean = LocalFieldInvalid.current,
) {
    TextFieldImpl(
        value, onValueChange, modifier, enabled, readOnly, placeholder, keyboardOptions, keyboardActions,
        visualTransformation, singleLine = true, minLines = 1, maxLines = 1, leading, trailing, isError,
    )
}

/** Multi-line text field that grows from [minLines] up to [maxLines]. */
@Composable
public fun PixaTextarea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    placeholder: String? = null,
    minLines: Int = 3,
    maxLines: Int = 8,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = LocalFieldInvalid.current,
) {
    TextFieldImpl(
        value, onValueChange, modifier, enabled, readOnly, placeholder, keyboardOptions, keyboardActions,
        VisualTransformation.None, singleLine = false, minLines = minLines, maxLines = maxLines, null, null, isError,
    )
}

@Composable
private fun TextFieldImpl(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    readOnly: Boolean,
    placeholder: String?,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    visualTransformation: VisualTransformation,
    singleLine: Boolean,
    minLines: Int,
    maxLines: Int,
    leading: (@Composable () -> Unit)?,
    trailing: (@Composable () -> Unit)?,
    isError: Boolean,
) {
    val colors = PixaTheme.colors
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val textStyle = PixaTheme.typography.bodySmall.merge(color = colors.foreground)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        interactionSource = source,
        cursorBrush = SolidColor(colors.foreground),
        decorationBox = { innerTextField ->
            PixaSurface(
                modifier = Modifier
                    .graphicsLayer { alpha = if (enabled) 1f else DisabledAlpha }
                    .defaultMinSize(minHeight = 40.dp),
                color = colors.background,
                contentColor = colors.mutedForeground,
                shape = PixaTheme.shapes.md,
                border = outlinedBorder(if (isError) colors.destructive else colors.input),
                shadow = if (enabled) PixaTheme.decoration.controlShadow else PixaShadow.None,
                focused = focused,
                contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = PixaTheme.spacing.md, vertical = PixaTheme.spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.sm),
                    verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
                ) {
                    leading?.invoke()
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty() && placeholder != null) {
                            PixaText(placeholder, style = textStyle, color = colors.mutedForeground, maxLines = maxLines)
                        }
                        innerTextField()
                    }
                    trailing?.invoke()
                }
            }
        },
    )
}
