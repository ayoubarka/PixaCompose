package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * The bar at the top of a screen. Its background extends under the status bar while its
 * content stays below it.
 *
 * @param navigation leading control, usually a ghost [Button] holding a back icon.
 * @param actions trailing controls.
 */
@Composable
public fun TopBar(
    modifier: Modifier = Modifier,
    navigation: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    val colors = PixaTheme.colors
    val spacing = PixaTheme.spacing
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.background,
        contentColor = colors.foreground,
        shape = RectangleShape,
    ) {
        Column(
            Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                navigation?.invoke()
                Box(Modifier.weight(1f).padding(horizontal = spacing.sm)) {
                    ProvideContentStyle(colors.foreground, PixaTheme.typography.h3) { title() }
                }
                actions?.invoke(this)
            }
            Separator()
        }
    }
}
