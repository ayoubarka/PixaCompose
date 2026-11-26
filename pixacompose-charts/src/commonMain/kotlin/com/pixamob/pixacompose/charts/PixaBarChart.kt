package com.pixamob.pixacompose.charts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import kotlin.jvm.JvmName
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.pixamob.pixacompose.theme.PixaTheme

/** A bar chart of one series, drawn by Vico in the theme's colors. See [PixaLineChart]. */
@Composable
public fun PixaBarChart(
    values: List<Number>,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList(),
    barWidth: Dp = 16.dp,
    showValues: Boolean = false,
    guides: List<PixaChartGuide> = emptyList(),
    visiblePoints: Int? = null,
    formatValue: (Double) -> String = ::plainNumber,
    interactive: Boolean = true,
) {
    PixaBarChart(
        series = remember(values) { listOf(PixaChartSeries(values)) },
        modifier = modifier, labels = labels, barWidth = barWidth, showValues = showValues,
        guides = guides, visiblePoints = visiblePoints, formatValue = formatValue, interactive = interactive,
    )
}

/**
 * A bar chart of several [series] over the same x positions, side by side or [stacked], such
 * as food by type per day. Colors follow [PixaLineChart]'s multi-series overload; put a
 * [PixaChartLegend] next to the chart to name the series.
 *
 * @param stacked piles each x position's bars into one, so the top shows the total.
 * @param showValues writes each value above its bar. Side-by-side bars of several series only
 * show a value that fits in their width, so widen [barWidth] or stack them for room.
 * @param interactive false draws a picture only, as in [PixaLineChart].
 */
@Composable
@JvmName("PixaBarChartOfSeries")
public fun PixaBarChart(
    series: List<PixaChartSeries>,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList(),
    barWidth: Dp = 16.dp,
    stacked: Boolean = false,
    showValues: Boolean = false,
    guides: List<PixaChartGuide> = emptyList(),
    visiblePoints: Int? = null,
    formatValue: (Double) -> String = ::plainNumber,
    interactive: Boolean = true,
) {
    val shown = series.filter { it.values.isNotEmpty() }
    if (shown.isEmpty()) return
    val layer = rememberBarLayer(shown, barWidth, stacked, showValues, formatValue)
    val model = remember(shown) {
        CartesianChartModel(ColumnCartesianLayerModel.build { shown.forEach { series(it.values) } })
    }
    ChartFrame(listOf(layer), model, labels, modifier, formatValue, guides = guides, visiblePoints = visiblePoints, interactive = interactive)
}

/** The bars of [series], styled from the theme. Shared with [PixaComboChart]. */
@Composable
internal fun rememberBarLayer(
    series: List<PixaChartSeries>,
    barWidth: Dp,
    stacked: Boolean,
    showValues: Boolean,
    formatValue: (Double) -> String,
): ColumnCartesianLayer {
    val bars = series.mapIndexed { index, item ->
        rememberLineComponent(
            fill = Fill(seriesColor(item.color, index, series.size)),
            thickness = barWidth,
            // Stacked parts meet edge to edge; a lone bar takes the style's corners.
            shape = if (stacked) RectangleShape else PixaTheme.shapes.sm,
        )
    }
    return rememberColumnCartesianLayer(
        ColumnCartesianLayer.ColumnProvider.series(bars),
        mergeMode = remember(stacked) {
            val mode = if (stacked) ColumnCartesianLayer.MergeMode.Stacked else ColumnCartesianLayer.MergeMode.Grouped();
            { _ -> mode }
        },
        // Room above the tallest bar for its value.
        rangeProvider = if (showValues) Headroom else remember { CartesianLayerRangeProvider.auto() },
        dataLabel = if (showValues) valueLabel() else null,
        dataLabelValueFormatter = valueFormatter(formatValue),
    )
}
