package com.pixamob.pixacompose.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.AnchoredPopup
import com.pixamob.pixacompose.primitives.PopupAlign
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * A floating list of actions, attached to the layout it is declared in like [PixaPopover].
 * Fill it with [PixaMenuItem]s and, between groups, [PixaSeparator]s.
 */
@Composable
public fun PixaMenu(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    side: PixaPopoverSide = PixaPopoverSide.Bottom,
    content: @Composable ColumnScope.() -> Unit,
) {
    AnchoredPopup(visible, onDismissRequest, side.toPopupSide(), PopupAlign.Start) {
        PixaSurface(
            modifier = modifier.width(IntrinsicSize.Max).widthIn(min = 180.dp, max = 320.dp),
            shape = PixaTheme.shapes.md,
            border = outlinedBorder(),
            shadow = PixaTheme.decoration.containerShadow,
        ) {
            Column(
                modifier = Modifier.padding(PixaTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(PixaTheme.spacing.xs),
                content = content,
            )
        }
    }
}

/** One action in a [PixaMenu]. It does not close the menu itself; do that in [onClick]. */
@Composable
public fun PixaMenuItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = PixaTheme.colors
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val focused by source.collectIsFocusedAsState()
    val highlighted = enabled && (hovered || focused)
    val contentColor = if (highlighted) colors.accentForeground else colors.cardForeground
    PixaSurface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        color = if (highlighted) colors.accent else Color.Transparent,
        contentColor = contentColor,
        shape = PixaTheme.shapes.sm,
        interactionSource = source,
    ) {
        ProvideContentStyle(contentColor, PixaTheme.typography.bodySmall) {
            Row(
                modifier = Modifier.padding(horizontal = PixaTheme.spacing.md, vertical = PixaTheme.spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}
