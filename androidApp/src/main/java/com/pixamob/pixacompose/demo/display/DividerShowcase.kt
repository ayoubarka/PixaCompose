package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.DividerOrientation
import com.pixamob.pixacompose.components.display.PixaDivider
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun DividerShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Horizontal Divider") {
            Column {
                BasicText("Above", style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
                PixaDivider(modifier = Modifier.fillMaxWidth())
                BasicText("Below", style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
            }
        }

        ShowcaseSection("Vertical Divider") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicText("Left", style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
                PixaDivider(orientation = DividerOrientation.Vertical, modifier = Modifier.height(24.dp))
                BasicText("Right", style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
            }
        }
    }
}
