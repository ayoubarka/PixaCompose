package com.pixamob.pixacompose.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.pie.PieChart
import com.patrykandpatrick.vico.compose.pie.PieChartHost
import com.patrykandpatrick.vico.compose.pie.PieSize
import com.patrykandpatrick.vico.compose.pie.data.PieChartModel
import com.patrykandpatrick.vico.compose.pie.data.PieValueFormatter
import com.patrykandpatrick.vico.compose.pie.rememberPieChart
import com.pixamob.pixacompose.theme.PixaTheme
import com.pixamob.pixacompose.theme.category

/** Share of the diameter left empty in the middle of a donut. */
private const val DonutHole = 0.62f

/**
 * A pie of [values], each slice in the next category hue unless [colors] gives its color, such
 * as how logged entries split between types. Put a [PixaChartLegend] with the same order next
 * to it to name the slices.
 *
 * @param donut leaves the middle empty; [center] then draws inside it, such as the total.
 * @param showValues writes each value on its slice, written by [formatValue].
 *
 * Vico marks its pie charts experimental, so their drawing may change between Vico releases.
 */
@Composable
public fun PixaPieChart(
    values: List<Number>,
    modifier: Modifier = Modifier,
    colors: List<Color> = emptyList(),
    donut: Boolean = false,
    showValues: Boolean = false,
    formatValue: (Double) -> String = ::plainNumber,
    center: (@Composable BoxScope.() -> Unit)? = null,
) {
    if (values.isEmpty() || values.all { it.toDouble() <= 0.0 }) return
    val theme = PixaTheme.colors
    val sliceColors = values.indices.map { index -> colors.getOrNull(index) ?: theme.category(index) }
    val label = rememberTextComponent(style = PixaTheme.typography.caption.copy(color = theme.background))
    val model = remember(values) { PieChartModel.build(*values.toTypedArray()) }
    // The caller's size wins; this only applies when none is given.
    BoxWithConstraints(modifier.fillMaxWidth().height(DefaultChartHeight), contentAlignment = Alignment.Center) {
        val diameter = min(maxWidth, maxHeight)
        val chart = rememberPieChart(
            sliceProvider = remember(sliceColors, showValues, label) {
                PieChart.SliceProvider.series(
                    sliceColors.map { color ->
                        PieChart.Slice(fill = Fill(color), label = if (showValues) PieChart.SliceLabel.Inside(label) else null)
                    },
                )
            },
            // The card shows through thin gaps between slices.
            spacing = 2.dp,
            innerSize = if (donut) PieSize.Inner.fixed(diameter * DonutHole) else PieSize.Inner.Zero,
            valueFormatter = remember(formatValue) { PieValueFormatter { _, value, _ -> formatValue(value.toDouble()) } },
        )
        PieChartHost(chart, model, Modifier.fillMaxSize(), chartAreaHeight = maxHeight)
        if (donut && center != null) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center, content = center)
    }
}
