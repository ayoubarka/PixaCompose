package com.pixamob.pixacompose.charts

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * Dots of values over time, several allowed at the same [PixaChartPoint.x], such as symptom
 * scores logged a few times a day. [labels] name the x positions 0, 1, 2, …; see [PixaLineChart]
 * for the other parameters.
 */
@Composable
public fun PixaScatterChart(
    points: List<PixaChartPoint>,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList(),
    fittedYAxis: Boolean = false,
    dotSize: Dp = 8.dp,
    guides: List<PixaChartGuide> = emptyList(),
    visiblePoints: Int? = null,
    formatValue: (Double) -> String = ::plainNumber,
) {
    if (points.isEmpty()) return
    val fitted = remember(points, guides, fittedYAxis) {
        if (fittedYAxis) FittedRange.of(points.map { it.y } + guides.guideValues()) else null
    }
    val dot = LineCartesianLayer.Point(
        rememberShapeComponent(fill = Fill(PixaTheme.colors.primary), shape = CircleShape),
        dotSize,
    )
    val line = LineCartesianLayer.rememberLine(
        fill = remember { LineCartesianLayer.LineFill.single(Fill(Color.Transparent)) },
        areaFill = null,
        pointProvider = remember(dot) { LineCartesianLayer.PointProvider.single(dot) },
    )
    val layer = rememberLineCartesianLayer(
        LineCartesianLayer.LineProvider.series(line),
        rangeProvider = fitted ?: remember { CartesianLayerRangeProvider.auto() },
    )
    // A series holds one value per x, so the n-th dot of every x goes in the n-th series.
    val model = remember(points) {
        val rounds = points.groupBy { it.x.toDouble() }.values.let { groups ->
            (0 until groups.maxOf { it.size }).map { n -> groups.mapNotNull { it.getOrNull(n) } }
        }
        CartesianChartModel(
            LineCartesianLayerModel.build {
                rounds.forEach { round -> series(x = round.map { it.x }, y = round.map { it.y }) }
            },
        )
    }
    ChartFrame(
        listOf(layer), model, labels, modifier, formatValue,
        yStep = fitted?.step, guides = guides, visiblePoints = visiblePoints,
    )
}
