package com.pixamob.pixacompose.demo.overlay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.components.overlay.PixaTooltip
import com.pixamob.pixacompose.components.overlay.TooltipPosition
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun TooltipShowcase() {
    var showTooltip by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        ShowcaseSection("Tooltip") {
            PixaTooltip(
                tooltip = "This is a tooltip",
                visible = showTooltip,
                position = TooltipPosition.Top,
                onDismiss = { showTooltip = false }
            ) {
                PixaIconButton(
                    icon = rememberVectorPainter(Icons.Default.Info),
                    onClick = { showTooltip = !showTooltip },
                    contentDescription = "Info"
                )
            }
        }
    }
}
