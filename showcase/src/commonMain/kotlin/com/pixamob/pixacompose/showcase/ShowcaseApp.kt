package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.Button
import com.pixamob.pixacompose.components.ButtonSize
import com.pixamob.pixacompose.components.ButtonVariant
import com.pixamob.pixacompose.components.Card
import com.pixamob.pixacompose.primitives.Text
import com.pixamob.pixacompose.styles.PixaStyles
import com.pixamob.pixacompose.theme.PixaStyle
import com.pixamob.pixacompose.theme.PixaTheme

@Composable
fun ShowcaseApp(initialStyle: PixaStyle = PixaStyles.Minimal, initialDark: Boolean = false) {
    var style by remember { mutableStateOf(initialStyle) }
    var dark by remember { mutableStateOf(initialDark) }

    PixaTheme(style = style, darkTheme = dark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PixaTheme.colors.background)
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text("PixaCompose", style = PixaTheme.typography.h1)
            Text(
                "Same components, different style object.",
                style = PixaTheme.typography.bodySmall,
                color = PixaTheme.colors.mutedForeground,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PixaStyles.all.forEach { candidate ->
                    Button(
                        onClick = { style = candidate },
                        variant = if (candidate == style) ButtonVariant.Primary else ButtonVariant.Outline,
                        size = ButtonSize.Sm,
                    ) { Text(candidate.name) }
                }
            }
            Button(onClick = { dark = !dark }, variant = ButtonVariant.Ghost, size = ButtonSize.Sm) {
                Text(if (dark) "Switch to light" else "Switch to dark")
            }

            Text("Button", style = PixaTheme.typography.h3)
            ButtonVariant.entries.forEach { variant ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {}, variant = variant) { Text(variant.name) }
                    Button(onClick = {}, variant = variant, enabled = false) { Text("Disabled") }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ButtonSize.entries.forEach { size ->
                    Button(onClick = {}, size = size) { Text(size.name) }
                }
            }

            Text("Card", style = PixaTheme.typography.h3)
            var count by remember { mutableIntStateOf(0) }
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("Counter", style = PixaTheme.typography.h3)
                Text(
                    "Pressed $count times.",
                    style = PixaTheme.typography.bodySmall,
                    color = PixaTheme.colors.mutedForeground,
                )
                Button(onClick = { count++ }) { Text("Press me") }
            }
        }
    }
}
