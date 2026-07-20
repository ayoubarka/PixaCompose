package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.inputs.PixaCalendar
import com.pixamob.pixacompose.components.inputs.PixaHeatmapCalendar
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun CalendarShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Calendar Grid") {
            PixaCalendar(modifier = Modifier.fillMaxWidth())
        }

        ShowcaseSection("Heatmap") {
            PixaHeatmapCalendar(modifier = Modifier.fillMaxWidth())
        }
    }
}
