package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaTheme

@Composable
public fun Card(
    modifier: Modifier = Modifier,
    /** Fill of the card. An app can pass its own color to make colored tiles. */
    color: Color = PixaTheme.colors.card,
    contentColor: Color = PixaTheme.colors.cardForeground,
    contentPadding: PaddingValues = PaddingValues(PixaTheme.spacing.lg),
    content: @Composable ColumnScope.() -> Unit,
) {
    val decoration = PixaTheme.decoration
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        border = outlinedBorder(),
        shadow = decoration.containerShadow,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(PixaTheme.spacing.sm),
            content = content,
        )
    }
}
