package com.pixamob.pixacompose.components

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.Modal
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * A centered modal panel. Keep it in the composition and drive it with [visible] so it can
 * animate out. Content is a slot: a title, some text and a row of buttons are ordinary
 * `PixaText` and `PixaButton` calls.
 *
 * @param onDismissRequest called on back, escape or a tap outside the panel.
 */
@Composable
public fun PixaDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spec = PixaTheme.motion.overlay
    Modal(
        visible = visible,
        onDismissRequest = onDismissRequest,
        contentAlignment = Alignment.Center,
        enter = fadeIn(spec.spec()) + scaleIn(spec.spec(), initialScale = 0.92f),
        exit = fadeOut(spec.exitSpec()) + scaleOut(spec.exitSpec(), targetScale = 0.92f),
    ) {
        PixaSurface(
            modifier = modifier.padding(PixaTheme.spacing.xl).widthIn(max = 420.dp).fillMaxWidth(),
            border = outlinedBorder(),
            shadow = PixaTheme.decoration.containerShadow,
        ) {
            Column(
                modifier = Modifier.padding(PixaTheme.spacing.xl),
                verticalArrangement = Arrangement.spacedBy(PixaTheme.spacing.md),
                content = content,
            )
        }
    }
}
