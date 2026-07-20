package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.display.ChartHeight
import com.pixamob.pixacompose.components.display.PixaColumnChart
import com.pixamob.pixacompose.components.display.PixaLineChart
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun ChartShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Line Chart") {
            PixaLineChart(
                data = listOf(listOf(4, 12, 8, 16, 10, 14, 6)),
                title = "Revenue Trend",
                subtitle = "Last 7 days",
                chartHeight = ChartHeight.Small,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("Column Chart") {
            PixaColumnChart(
                data = listOf(listOf(5, 12, 8, 15, 7)),
                title = "Monthly Sales",
                chartHeight = ChartHeight.Small,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("Loading State") {
            PixaLineChart(
                data = listOf(listOf(1, 2, 3)),
                title = "Loading...",
                chartHeight = ChartHeight.Small,
                isLoading = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
