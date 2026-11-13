package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * What a screen or list shows when it has nothing yet: a picture, a title, a short message and
 * one action, centered. It has no background; put it in a [PixaCard] to frame it.
 *
 * ```
 * PixaEmptyState(
 *     picture = { PixaIconTile(size = PixaIconTileSize.Xl) { PixaIcon(AppIcons.Paw, null) } },
 *     message = { PixaText("Add a pet to start its health log.") },
 *     action = { PixaButton(onClick = onAddPet) { PixaText("Add pet") } },
 * ) { PixaText("No pet yet") }
 * ```
 */
@Composable
public fun PixaEmptyState(
    modifier: Modifier = Modifier,
    picture: (@Composable () -> Unit)? = null,
    message: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    val colors = PixaTheme.colors
    val typography = PixaTheme.typography
    val spacing = PixaTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth().padding(spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        picture?.invoke()
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProvideContentStyle(colors.foreground, typography.h3.copy(textAlign = TextAlign.Center)) { title() }
            if (message != null) {
                ProvideContentStyle(colors.mutedForeground, typography.bodySmall.copy(textAlign = TextAlign.Center)) {
                    message()
                }
            }
        }
        if (action != null) Box(Modifier.padding(top = spacing.xs)) { action() }
    }
}
