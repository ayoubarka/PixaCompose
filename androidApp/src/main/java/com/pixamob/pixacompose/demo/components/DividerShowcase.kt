package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.HorizontalDivider
import com.pixamob.pixacompose.components.display.VerticalDivider
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun DividerShowcase() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Horizontal Dividers") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Thin (1dp)", style = AppTheme.typography.captionRegular, color = AppTheme.colors.baseContentCaption)
                HorizontalDivider(thickness = 1.dp)
                Text("Medium (2dp)", style = AppTheme.typography.captionRegular, color = AppTheme.colors.baseContentCaption)
                HorizontalDivider(thickness = 2.dp)
                Text("Thick (4dp)", style = AppTheme.typography.captionRegular, color = AppTheme.colors.baseContentCaption)
                HorizontalDivider(thickness = 4.dp)
            }
        }

        ShowcaseSection("Vertical Dividers") {
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Left", style = AppTheme.typography.bodyRegular, color = AppTheme.colors.baseContentBody)
                VerticalDivider(modifier = Modifier.height(24.dp))
                Text("Center", style = AppTheme.typography.bodyRegular, color = AppTheme.colors.baseContentBody)
                VerticalDivider(modifier = Modifier.height(24.dp))
                Text("Right", style = AppTheme.typography.bodyRegular, color = AppTheme.colors.baseContentBody)
            }
        }

        ShowcaseSection("Custom Colors") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HorizontalDivider(thickness = 2.dp, color = Color.Red)
                HorizontalDivider(thickness = 2.dp, color = Color.Blue)
                HorizontalDivider(thickness = 2.dp, color = AppTheme.colors.brandBorderDefault)
            }
        }
    }
}
