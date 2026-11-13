package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.theme.PixaTheme

public enum class PixaAlertVariant { Default, Destructive }

/** A message box inside a page: a notice, a warning, an error. It is not an overlay. */
@Composable
public fun PixaAlert(
    modifier: Modifier = Modifier,
    variant: PixaAlertVariant = PixaAlertVariant.Default,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = PixaTheme.colors
    val spacing = PixaTheme.spacing
    val destructive = variant == PixaAlertVariant.Destructive
    val strong = if (destructive) colors.destructive else colors.cardForeground
    PixaSurface(
        modifier = modifier.fillMaxWidth(),
        contentColor = strong,
        shape = PixaTheme.shapes.md,
        border = outlinedBorder(if (destructive) colors.destructive else colors.border),
    ) {
        Row(Modifier.padding(spacing.lg), horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
            icon?.invoke()
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                if (title != null) ProvideContentStyle(strong, PixaTheme.typography.label, title)
                ProvideContentStyle(
                    if (destructive) colors.destructive else colors.mutedForeground,
                    PixaTheme.typography.bodySmall,
                ) { content() }
            }
        }
    }
}
