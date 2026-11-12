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
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

public enum class PixaPopoverSide { Top, Bottom }

internal fun PixaPopoverSide.toPopupSide(): PopupSide = if (this == PixaPopoverSide.Top) PopupSide.Top else PopupSide.Bottom

/**
 * A floating panel attached to the layout it is declared in. Put it in the same `Box` as its
 * trigger:
 *
 * ```
 * Box {
 *     PixaButton(onClick = { open = true }) { PixaText("Filters") }
 *     PixaPopover(visible = open, onDismissRequest = { open = false }) { ... }
 * }
 * ```
 *
 * @param side preferred side; it flips when there is no room.
 */
@Composable
public fun PixaPopover(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    side: PixaPopoverSide = PixaPopoverSide.Bottom,
    content: @Composable ColumnScope.() -> Unit,
) {
    AnchoredPopup(visible, onDismissRequest, side.toPopupSide(), PopupAlign.Start) {
        PixaSurface(
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
