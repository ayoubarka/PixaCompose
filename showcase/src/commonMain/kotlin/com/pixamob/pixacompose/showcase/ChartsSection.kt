package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.charts.PixaBarChart
import com.pixamob.pixacompose.charts.PixaCandle
import com.pixamob.pixacompose.charts.PixaCandlestickChart
import com.pixamob.pixacompose.charts.PixaChartGuide
import com.pixamob.pixacompose.charts.PixaChartLegend
import com.pixamob.pixacompose.charts.PixaChartPoint
import com.pixamob.pixacompose.charts.PixaChartSeries
import com.pixamob.pixacompose.charts.PixaComboChart
import com.pixamob.pixacompose.charts.PixaLineChart
import com.pixamob.pixacompose.charts.PixaPieChart
import com.pixamob.pixacompose.charts.PixaScatterChart
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
        PixaBarChart(values = listOf(6200, 7400, 8752, 5100, 9300, 4000, 7800), labels = Days, showValues = true)
    }
    PixaCard(Modifier.fillMaxWidth()) {
        PixaText("Itching, several logs a day", style = PixaTheme.typography.caption, color = PixaTheme.colors.mutedForeground)
        PixaScatterChart(
            points = listOf(0 to 3, 0 to 5, 1 to 4, 2 to 2, 2 to 6, 2 to 4, 3 to 3, 4 to 1, 5 to 2, 5 to 3, 6 to 1)
                .map { (day, score) -> PixaChartPoint(day, score) },
            labels = Days,
            // A comma decimal, as in French.
            formatValue = { (kotlin.math.round(it * 10) / 10).toString().removeSuffix(".0").replace('.', ',') },
        )
    }
    MoreCharts()
}

/** Several series, guides, stacking, combined layers, candlesticks and pies. */
@Composable
private fun MoreCharts() {
    val caption: @Composable (String) -> Unit = {
        PixaText(it, style = PixaTheme.typography.caption, color = PixaTheme.colors.mutedForeground)
    }
    val weights = listOf(
        PixaChartSeries(listOf(4.2, 4.25, 4.3, 4.28, 4.4, 4.5, 4.6), "Milo"),
        PixaChartSeries(listOf(3.6, 3.7, 3.65, 3.8, 3.75, 3.9, 3.85), "Luna"),
        PixaChartSeries(listOf(4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0), "Goal", dashed = true),
    )
    PixaCard(Modifier.fillMaxWidth()) {
        caption("Weights, several lines and a healthy band")
        PixaLineChart(
            series = weights,
            labels = Days,
            fittedYAxis = true,
            showPoints = true,
            guides = listOf(PixaChartGuide.Band(3.5, 4.5, "Healthy")),
        )
        PixaChartLegend(weights)
    }
    val food = listOf(
        PixaChartSeries(listOf(120, 140, 130, 150, 110, 160, 140), "Dry"),
        PixaChartSeries(listOf(80, 60, 90, 70, 100, 50, 80), "Wet"),
        PixaChartSeries(listOf(20, 30, 10, 25, 15, 40, 20), "Treats"),
    )
    PixaCard(Modifier.fillMaxWidth()) {
        caption("Food in grams, stacked, with a daily limit")
        PixaBarChart(series = food, labels = Days, stacked = true, guides = listOf(PixaChartGuide.Line(240, "Limit")))
        PixaChartLegend(food)
    }
    PixaCard(Modifier.fillMaxWidth()) {
        caption("Grouped bars")
        PixaBarChart(series = food.take(2), labels = Days, barWidth = 10.dp)
        PixaChartLegend(food.take(2))
    }
    val walks = listOf(PixaChartSeries(listOf(30, 45, 20, 60, 40, 90, 75), "Walk, min"))
    val weight = listOf(PixaChartSeries(listOf(4.2, 4.25, 4.3, 4.28, 4.4, 4.5, 4.6), "Weight, kg"))
    PixaCard(Modifier.fillMaxWidth()) {
        caption("Bars and a line on separate axes")
        PixaComboChart(bars = walks, lines = weight, labels = Days, separateAxes = true)
        PixaChartLegend(walks + weight)
    }
    PixaCard(Modifier.fillMaxWidth()) {
        caption("Daily temperature range, candlesticks")
        PixaCandlestickChart(
            candles = listOf(
                PixaCandle(38.2, 38.6, 38.0, 38.9), PixaCandle(38.6, 38.4, 38.2, 38.8),
                PixaCandle(38.4, 38.9, 38.3, 39.1), PixaCandle(38.9, 38.5, 38.4, 39.0),
                PixaCandle(38.5, 38.5, 38.3, 38.7), PixaCandle(38.5, 38.3, 38.1, 38.6),
                PixaCandle(38.3, 38.6, 38.2, 38.8),
            ),
            labels = Days,
        )
    }
    val types = listOf("Weight", "Food", "Symptom", "Mood")
    PixaCard(Modifier.fillMaxWidth()) {
        caption("Logs by type: pie and donut")
        Row(horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.md)) {
            PixaPieChart(values = listOf(12, 30, 5, 9), modifier = Modifier.weight(1f).height(140.dp), showValues = true)
            PixaPieChart(values = listOf(12, 30, 5, 9), modifier = Modifier.weight(1f).height(140.dp), donut = true) {
                PixaText("56", style = PixaTheme.typography.h3)
            }
        }
        PixaChartLegend(types)
    }
    PixaCard(Modifier.fillMaxWidth()) {
        caption("A month of steps, scroll and pinch")
        PixaBarChart(
            values = List(30) { 4000 + (it * 2654) % 6000 },
            labels = List(30) { (it + 1).toString() },
            visiblePoints = 10,
        )
    }
}
