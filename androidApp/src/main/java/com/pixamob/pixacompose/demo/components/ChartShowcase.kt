package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.ChartHeight
import com.pixamob.pixacompose.components.display.PixaColumnChart
import com.pixamob.pixacompose.components.display.PixaLineChart
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun ChartShowcase() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
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
                subtitle = "Units sold per month",
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
