package com.pixamob.pixacompose.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.DisabledAlpha
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaShadow
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * A row of boxes for a short numeric code, such as one sent by SMS. It is one text field, so
 * typing, deleting and pasting behave as usual; only digits are accepted, up to [length].
 */
@Composable
public fun PixaCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    enabled: Boolean = true,
    isError: Boolean = LocalFieldInvalid.current,
) {
    val colors = PixaTheme.colors
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    BasicTextField(
        // The caret is pinned to the end: there is no visible text to place it in, and a tap
        // would otherwise put it at the start so new digits landed in front.
        value = TextFieldValue(value, TextRange(value.length)),
        onValueChange = { new ->
            if (new.text.length <= length && new.text.all(Char::isDigit)) onValueChange(new.text)
        },
        modifier = modifier,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        interactionSource = source,
        // The boxes replace the usual text, so the inner field is deliberately not drawn.
        decorationBox = {
            Row(
                Modifier.graphicsLayer { alpha = if (enabled) 1f else DisabledAlpha },
                horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.sm),
            ) {
                repeat(length) { index ->
                    PixaSurface(
                        modifier = Modifier.size(width = 44.dp, height = 48.dp),
                        color = colors.background,
                        contentColor = colors.foreground,
                        shape = PixaTheme.shapes.md,
                        border = outlinedBorder(if (isError) colors.destructive else colors.input),
                        shadow = if (enabled) PixaTheme.decoration.controlShadow else PixaShadow.None,
                        // The ring marks the box the next digit goes into.
                        focused = focused && index == value.length.coerceAtMost(length - 1),
                        contentAlignment = Alignment.Center,
                    ) {
                        PixaText(value.getOrNull(index)?.toString().orEmpty(), style = PixaTheme.typography.h3)
                    }
                }
            }
        },
    )
}
