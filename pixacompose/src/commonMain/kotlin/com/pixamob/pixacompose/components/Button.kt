package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaBorder
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaShadow
import com.pixamob.pixacompose.theme.PixaTheme

public enum class ButtonVariant { Primary, Secondary, Outline, Ghost, Destructive }

public enum class ButtonSize(internal val minHeight: Dp, internal val padding: PaddingValues) {
    Sm(32.dp, PaddingValues(horizontal = 12.dp, vertical = 6.dp)),
    Md(40.dp, PaddingValues(horizontal = 16.dp, vertical = 8.dp)),
    Lg(48.dp, PaddingValues(horizontal = 24.dp, vertical = 10.dp)),
}

/**
 * Content is a slot: put any mix of text and icons inside, spaced automatically.
 *
 * ```
 * Button(onClick = { }) {
 *     Icon(PixaIcons.Plus, contentDescription = null)
 *     Text("Add")
 * }
 * ```
 */
@Composable
public fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Md,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = PixaTheme.colors
    val decoration = PixaTheme.decoration

    val container: Color
    val contentColor: Color
    when (variant) {
        ButtonVariant.Primary -> { container = colors.primary; contentColor = colors.primaryForeground }
        ButtonVariant.Secondary -> { container = colors.secondary; contentColor = colors.secondaryForeground }
        ButtonVariant.Destructive -> { container = colors.destructive; contentColor = colors.destructiveForeground }
        ButtonVariant.Outline -> { container = colors.background; contentColor = colors.foreground }
        ButtonVariant.Ghost -> { container = Color.Transparent; contentColor = colors.foreground }
    }
    val borderWidth = when (variant) {
        ButtonVariant.Ghost -> 0.dp
        ButtonVariant.Outline -> decoration.borderWidth
        else -> decoration.filledBorderWidth
    }

    Surface(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = size.minHeight),
        enabled = enabled,
        role = Role.Button,
        color = container,
        contentColor = contentColor,
        shape = PixaTheme.shapes.md,
        border = if (borderWidth > 0.dp) PixaBorder(borderWidth, colors.border) else null,
        shadow = if (variant == ButtonVariant.Ghost) PixaShadow.None else decoration.controlShadow,
        contentAlignment = Alignment.Center,
    ) {
        ProvideContentStyle(contentColor, PixaTheme.typography.label) {
            Row(
                modifier = Modifier.padding(size.padding),
                horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.sm, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}
