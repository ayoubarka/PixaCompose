package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.overlay.PixaTooltip
import com.pixamob.pixacompose.components.overlay.PixaTooltipBox
import com.pixamob.pixacompose.components.overlay.TooltipPosition
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun TooltipShowcase() {
    var showTooltip by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Tooltip Box") {
            PixaTooltipBox(tooltip = "This is a helpful tooltip", position = TooltipPosition.Top) {
                PixaButton(text = "Hover or Tap Me", onClick = {})
            }
        }

        ShowcaseSection("Controlled Tooltip") {
            PixaTooltip(
                tooltip = "Settings menu",
                visible = showTooltip,
                position = TooltipPosition.Bottom,
                autoDismissMs = 3000L,
                onDismiss = { showTooltip = false }
            ) {
                PixaButton(text = "Toggle Tooltip", onClick = { showTooltip = !showTooltip })
            }
        }
    }
}
