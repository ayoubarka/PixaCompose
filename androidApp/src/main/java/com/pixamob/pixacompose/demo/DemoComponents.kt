package com.pixamob.pixacompose.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme

private val noop: () -> Unit = {}
val LocalThemeToggle = staticCompositionLocalOf<() -> Unit> { noop }

@Composable
fun ShowcaseScreen(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        content()
    }
}

@Composable
fun SectionTitle(title: String) {
    BasicText(
        text = title,
        style = AppTheme.typography.titleBold.copy(
            color = AppTheme.colors.brandContentDefault
        ),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun ComponentLabel(text: String) {
    BasicText(
        text = text,
        style = AppTheme.typography.captionRegular.copy(
            color = AppTheme.colors.baseContentCaption
        ),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun ShowcaseSection(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ComponentLabel(label)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = AppTheme.colors.baseSurfaceSubtle,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            content()
        }
    }
}
