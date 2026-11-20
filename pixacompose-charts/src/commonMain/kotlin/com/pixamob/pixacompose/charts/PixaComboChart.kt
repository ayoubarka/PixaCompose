package com.pixamob.pixacompose.charts

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.axis.Axis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent

/**
 * Bars and lines in one chart over the same x positions, such as daily food as bars with the
 * weight as a line. Bars take the first colors and lines the next; put a [PixaChartLegend]
 * next to the chart to name them.
 *
 * @param separateAxes measures the lines on their own axis at the end, for values of another
 * kind or scale than the bars; [formatLineValue] writes that axis.
 */
@Composable
public fun PixaComboChart(
    bars: List<PixaChartSeries>,
    lines: List<PixaChartSeries>,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList(),
    barWidth: Dp = 16.dp,
    stackedBars: Boolean = false,
    separateAxes: Boolean = false,
    guides: List<PixaChartGuide> = emptyList(),
    visiblePoints: Int? = null,
    formatValue: (Double) -> String = ::plainNumber,
    formatLineValue: (Double) -> String = formatValue,
) {
    val shownBars = bars.filter { it.values.isNotEmpty() }
    val shownLines = lines.filter { it.values.isNotEmpty() }
    if (shownBars.isEmpty() || shownLines.isEmpty()) return
    val total = shownBars.size + shownLines.size
    val barLayer = rememberBarLayer(
        shownBars.mapIndexed { index, item -> item.copy(color = seriesColor(item.color, index, total)) },
        barWidth, stackedBars, showValues = false, formatValue,
    )
    val lineStyles = shownLines.mapIndexed { index, item ->
        val color = seriesColor(item.color, shownBars.size + index, total)
        val dot = rememberShapeComponent(fill = Fill(color), shape = CircleShape)
        LineCartesianLayer.rememberLine(
            fill = remember(color) { LineCartesianLayer.LineFill.single(Fill(color)) },
            stroke = if (item.dashed) LineCartesianLayer.LineStroke.Dashed() else LineCartesianLayer.LineStroke.Continuous(),
            pointProvider = remember(dot) { LineCartesianLayer.PointProvider.single(LineCartesianLayer.Point(dot, 6.dp)) },
            interpolator = remember { LineCartesianLayer.Interpolator.cubic() },
        )
    }
    // On its own axis the line is fitted to its values, so its changes show.
    val lineRange = remember(shownLines, separateAxes) {
        if (separateAxes) FittedRange.of(shownLines.flatMap { it.values }) else null
    }
    val lineLayer = rememberLineCartesianLayer(
        LineCartesianLayer.LineProvider.series(lineStyles),
        rangeProvider = lineRange ?: remember { CartesianLayerRangeProvider.auto() },
        verticalAxisPosition = if (separateAxes) Axis.Position.Vertical.End else null,
    )
    val model = remember(shownBars, shownLines) {
        CartesianChartModel(
            ColumnCartesianLayerModel.build { shownBars.forEach { series(it.values) } },
            LineCartesianLayerModel.build { shownLines.forEach { series(it.values) } },
        )
    }
    ChartFrame(
        listOf(barLayer, lineLayer), model, labels, modifier, formatValue,
        guides = guides,
        endAxisFormat = if (separateAxes) formatLineValue else null,
        endYStep = lineRange?.step,
        visiblePoints = visiblePoints,
    )
}
