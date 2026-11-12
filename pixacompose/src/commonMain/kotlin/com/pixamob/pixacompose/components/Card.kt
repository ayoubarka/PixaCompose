package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.primitives.PixaBorder
import com.pixamob.pixacompose.primitives.Surface
import com.pixamob.pixacompose.theme.PixaTheme

@Composable
public fun Card(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(PixaTheme.spacing.lg),
    content: @Composable ColumnScope.() -> Unit,
) {
    val decoration = PixaTheme.decoration
    Surface(
        modifier = modifier,
        border = PixaBorder(decoration.borderWidth, PixaTheme.colors.border),
        shadow = decoration.containerShadow,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(PixaTheme.spacing.sm),
            content = content,
        )
    }
}
