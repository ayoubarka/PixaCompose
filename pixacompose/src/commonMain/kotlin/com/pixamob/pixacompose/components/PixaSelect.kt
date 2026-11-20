package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * A field that opens a [PixaMenu] of choices. It holds no selection itself: show the current
 * choice in [value] (or a muted placeholder) and update your state from each [PixaMenuItem].
 *
 * ```
 * PixaSelect(open, { open = it }, value = { PixaText(fruit) }) {
 *     fruits.forEach { PixaMenuItem(onClick = { fruit = it; open = false }) { PixaText(it) } }
 * }
 * ```
 */
@Composable
public fun PixaSelect(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    value: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = PixaTheme.colors
    val spacing = PixaTheme.spacing
    Box(modifier) {
        PixaSurface(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 40.dp).fieldLabel(),
            enabled = enabled,
            role = Role.DropdownList,
            color = colors.background,
            contentColor = colors.foreground,
            shape = PixaTheme.shapes.md,
            border = outlinedBorder(colors.input),
            shadow = PixaTheme.decoration.controlShadow,
            contentAlignment = Alignment.CenterStart,
        ) {
            ProvideContentStyle(colors.foreground, PixaTheme.typography.bodySmall) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md, vertical = spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        content = value,
                    )
                    PixaIcon(PixaIcons.ChevronDown, contentDescription = null, tint = colors.mutedForeground, size = 16.dp)
                }
            }
        }
        PixaMenu(visible = expanded, onDismissRequest = { onExpandedChange(false) }, content = content)
    }
}
