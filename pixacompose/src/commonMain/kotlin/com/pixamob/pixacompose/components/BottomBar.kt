package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * The navigation bar at the bottom of a screen, holding three to five [BottomBarItem]s. Its
 * background extends under the system navigation area while its items stay above it.
 */
@Composable
public fun BottomBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PixaTheme.colors.background,
        contentColor = PixaTheme.colors.foreground,
        shape = RectangleShape,
    ) {
        Column(
            Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal),
            ),
        ) {
            Separator()
            Row(
                modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = PixaTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}

/** One destination in a [BottomBar]. Items share the bar's width equally. */
@Composable
public fun RowScope.BottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: (@Composable () -> Unit)? = null,
    icon: @Composable () -> Unit,
) {
    val colors = PixaTheme.colors
    val spacing = PixaTheme.spacing
    val labelColor = if (selected) colors.foreground else colors.mutedForeground
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.weight(1f).fillMaxHeight(),
        enabled = enabled,
        role = Role.Tab,
        color = Color.Transparent,
        contentColor = labelColor,
        shape = PixaTheme.shapes.md,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            // The selected destination is marked by a pill behind its icon.
            Surface(
                color = if (selected) colors.secondary else Color.Transparent,
                contentColor = if (selected) colors.secondaryForeground else colors.mutedForeground,
                shape = PixaTheme.shapes.full,
                border = if (selected) filledBorder() else null,
            ) {
                Box(Modifier.padding(horizontal = spacing.lg, vertical = spacing.xs)) { icon() }
            }
            if (label != null) {
                ProvideContentStyle(labelColor, PixaTheme.typography.caption) { label() }
            }
        }
    }
}
