package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.AnchoredPopup
import com.pixamob.pixacompose.primitives.PopupAlign
import com.pixamob.pixacompose.primitives.PopupSide
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaTheme

public enum class PopoverSide { Top, Bottom }

internal fun PopoverSide.toPopupSide(): PopupSide = if (this == PopoverSide.Top) PopupSide.Top else PopupSide.Bottom

/**
 * A floating panel attached to the layout it is declared in. Put it in the same `Box` as its
 * trigger:
 *
 * ```
 * Box {
 *     Button(onClick = { open = true }) { Text("Filters") }
 *     Popover(visible = open, onDismissRequest = { open = false }) { ... }
 * }
 * ```
 *
 * @param side preferred side; it flips when there is no room.
 */
@Composable
public fun Popover(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    side: PopoverSide = PopoverSide.Bottom,
    content: @Composable ColumnScope.() -> Unit,
) {
    AnchoredPopup(visible, onDismissRequest, side.toPopupSide(), PopupAlign.Start) {
        Surface(
            modifier = modifier.widthIn(max = 320.dp),
            shape = PixaTheme.shapes.md,
            border = outlinedBorder(),
            shadow = PixaTheme.decoration.containerShadow,
        ) {
            Column(
                modifier = Modifier.padding(PixaTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(PixaTheme.spacing.sm),
                content = content,
            )
        }
    }
}
