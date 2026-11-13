package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.charts.PixaBarChart
import com.pixamob.pixacompose.charts.PixaLineChart
import com.pixamob.pixacompose.components.PixaCard
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme

private val Days = listOf("M", "T", "W", "T", "F", "S", "S")

@Composable
fun ChartsSection() {
    PixaText("Charts", style = PixaTheme.typography.h3)
    PixaCard(Modifier.fillMaxWidth()) {
        PixaText("Calories burned", style = PixaTheme.typography.caption, color = PixaTheme.colors.mutedForeground)
        PixaText("2,450 kcal", style = PixaTheme.typography.h2)
        PixaLineChart(values = listOf(310, 280, 360, 330, 420, 520, 480), labels = Days)
    }
    PixaCard(Modifier.fillMaxWidth()) {
        PixaText("Weight, fitted axis", style = PixaTheme.typography.caption, color = PixaTheme.colors.mutedForeground)
        PixaText("4.6 kg", style = PixaTheme.typography.h2)
        PixaLineChart(
            values = listOf(4.2, 4.25, 4.3, 4.28, 4.4, 4.5, 4.6),
            labels = Days,
            fittedYAxis = true,
            drawIn = true,
        )
    }
    PixaCard(Modifier.fillMaxWidth()) {
        PixaText("Steps", style = PixaTheme.typography.caption, color = PixaTheme.colors.mutedForeground)
        PixaBarChart(values = listOf(6200, 7400, 8752, 5100, 9300, 4000, 7800), labels = Days)
    }
}
