package com.pixamob.pixacompose.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import androidx.compose.material3.Text

private val noop: () -> Unit = {}
val LocalThemeToggle = staticCompositionLocalOf<() -> Unit> { noop }

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = AppTheme.typography.titleBold,
        color = AppTheme.colors.brandContentDefault,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun ComponentLabel(text: String) {
    Text(
        text = text,
        style = AppTheme.typography.captionRegular,
        color = AppTheme.colors.baseContentCaption,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun ShowcaseSection(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ComponentLabel(label)
        content()
    }
}
