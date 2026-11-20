package com.pixamob.pixacompose.charts

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlin.jvm.JvmName
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent

/**
 * A line chart of one series, drawn by Vico in the theme's colors. [labels] name the points
 * along the bottom; leave it empty to show their positions instead.
 *
 * @param fittedYAxis fits the vertical axis to the values instead of starting it at 0, so
 * small changes in large numbers, such as a body weight, show as a slope rather than a flat line.
 * @param drawIn draws the chart in from the left when it appears and when [values] change.
 * @param formatValue writes the numbers on the vertical axis, the guides and the tap marker.
 * Pass one that follows the app's locale, such as "12,5" in French.
 *
 * See the multi-series overload for the other parameters. Charts read left to right in every
 * layout direction, so time runs the same way in a right-to-left app.
 */
@Composable
public fun PixaLineChart(
    values: List<Number>,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList(),
    /** Curved line between points; false draws straight segments. */
    smooth: Boolean = true,
    fittedYAxis: Boolean = false,
    drawIn: Boolean = false,
    area: Boolean = true,
    showPoints: Boolean = false,
    showValues: Boolean = false,
    guides: List<PixaChartGuide> = emptyList(),
    visiblePoints: Int? = null,
    formatValue: (Double) -> String = ::plainNumber,
) {
    PixaLineChart(
        series = remember(values) { listOf(PixaChartSeries(values)) },
        modifier = modifier, labels = labels, smooth = smooth, fittedYAxis = fittedYAxis, drawIn = drawIn,
        area = area, showPoints = showPoints, showValues = showValues, guides = guides,
        visiblePoints = visiblePoints, formatValue = formatValue,
    )
}

/**
 * A line chart of several [series] over the same x positions, such as the weights of a few
 * pets. Each series takes its own color, the next category hue unless it sets one; put a
 * [PixaChartLegend] next to the chart to name them.
 *
 * @param area fills under each line with a fade of its color.
 * @param showPoints draws a dot on every value.
 * @param showValues writes each value above its point.
 * @param guides lines and bands across the chart, such as a goal or a normal range.
 * @param visiblePoints shows this many x positions at a time; the rest scroll sideways and a
 * pinch zooms. Null fits every point.
 */
@Composable
@JvmName("PixaLineChartOfSeries")
public fun PixaLineChart(
    series: List<PixaChartSeries>,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList(),
    smooth: Boolean = true,
    fittedYAxis: Boolean = false,
    drawIn: Boolean = false,
    area: Boolean = false,
    showPoints: Boolean = false,
    showValues: Boolean = false,
    guides: List<PixaChartGuide> = emptyList(),
    visiblePoints: Int? = null,
    formatValue: (Double) -> String = ::plainNumber,
) {
    val shown = series.filter { it.values.isNotEmpty() }
    if (shown.isEmpty()) return
    val fitted = remember(shown, guides, fittedYAxis) {
        if (fittedYAxis) FittedRange.of(shown.flatMap { it.values } + guides.guideValues()) else null
    }
    val lines = shown.mapIndexed { index, item ->
        lineOf(seriesColor(item.color, index, shown.size), item.dashed, smooth, area, showPoints, showValues, formatValue)
    }
    val layer = rememberLineCartesianLayer(
        LineCartesianLayer.LineProvider.series(lines),
        rangeProvider = fitted ?: if (showValues) Headroom else remember { CartesianLayerRangeProvider.auto() },
    )
    val model = remember(shown) {
        CartesianChartModel(LineCartesianLayerModel.build { shown.forEach { series(it.values) } })
    }
    val reveal = if (drawIn) drawInModifier(shown) else Modifier
    ChartFrame(
        listOf(layer), model, labels, modifier.then(reveal), formatValue,
        yStep = fitted?.step, guides = guides, visiblePoints = visiblePoints,
    )
}

/** One series' line, styled from the theme. */
@Composable
private fun lineOf(
    color: Color,
    dashed: Boolean,
    smooth: Boolean,
    area: Boolean,
    showPoints: Boolean,
    showValues: Boolean,
    formatValue: (Double) -> String,
): LineCartesianLayer.Line {
    val dot = rememberShapeComponent(fill = Fill(color), shape = CircleShape)
    return LineCartesianLayer.rememberLine(
        fill = remember(color) { LineCartesianLayer.LineFill.single(Fill(color)) },
        stroke = if (dashed) LineCartesianLayer.LineStroke.Dashed() else LineCartesianLayer.LineStroke.Continuous(),
        areaFill = if (area) {
            remember(color) {
                LineCartesianLayer.AreaFill.single(Fill(Brush.verticalGradient(listOf(color.copy(alpha = 0.25f), Color.Transparent))))
            }
        } else null,
        pointProvider = if (showPoints) remember(dot) { LineCartesianLayer.PointProvider.single(LineCartesianLayer.Point(dot, 6.dp)) } else null,
        interpolator = remember(smooth) { if (smooth) LineCartesianLayer.Interpolator.cubic() else LineCartesianLayer.Interpolator.Sharp },
        dataLabel = if (showValues) valueLabel() else null,
        dataLabelValueFormatter = valueFormatter(formatValue),
    )
}

/** The values guides sit at, so a fitted axis keeps them in view. */
internal fun List<PixaChartGuide>.guideValues(): List<Number> = flatMap { guide ->
    when (guide) {
        is PixaChartGuide.Line -> listOf(guide.y)
        is PixaChartGuide.Band -> listOf(guide.from, guide.to)
    }
}
