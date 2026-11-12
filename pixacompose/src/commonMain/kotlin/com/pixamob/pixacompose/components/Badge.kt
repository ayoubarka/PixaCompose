package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaTheme

public enum class BadgeVariant { Primary, Secondary, Outline, Destructive }

/** A small, non-interactive label: a status, a count, a tag. */
@Composable
public fun Badge(
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Primary,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = PixaTheme.colors
    val container: Color
    val contentColor: Color
    when (variant) {
        BadgeVariant.Primary -> { container = colors.primary; contentColor = colors.primaryForeground }
        BadgeVariant.Secondary -> { container = colors.secondary; contentColor = colors.secondaryForeground }
        BadgeVariant.Destructive -> { container = colors.destructive; contentColor = colors.destructiveForeground }
        BadgeVariant.Outline -> { container = Color.Transparent; contentColor = colors.foreground }
    }
    Surface(
        modifier = modifier,
        color = container,
        contentColor = contentColor,
        shape = PixaTheme.shapes.full,
        border = if (variant == BadgeVariant.Outline) outlinedBorder() else filledBorder(),
    ) {
        ProvideContentStyle(contentColor, PixaTheme.typography.caption.merge(fontWeight = PixaTheme.typography.label.fontWeight)) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}
