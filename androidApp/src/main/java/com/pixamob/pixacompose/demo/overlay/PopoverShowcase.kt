package com.pixamob.pixacompose.demo.overlay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.overlay.PixaPopover
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun PopoverShowcase() {
    var showPopover by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        ShowcaseSection("Popover") {
            PixaButton(text = "Toggle Popover", onClick = { showPopover = !showPopover })
        }
    }

    if (showPopover) {
        PixaPopover(
            visible = showPopover,
            onDismiss = { showPopover = false },
            heading = "Popover Title",
            content = {
                androidx.compose.foundation.text.BasicText(
                    "Popover content",
                    style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody)
                )
            }
        )
    }
}
