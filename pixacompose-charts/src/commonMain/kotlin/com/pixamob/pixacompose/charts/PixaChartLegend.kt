package com.pixamob.pixacompose.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme
import com.pixamob.pixacompose.theme.category

/**
 * Names the series or slices of a chart: a dot of each color and its name, wrapping onto more
 * rows when narrow. Pass the chart's own [PixaChartSeries] so the colors match.
 */
@Composable
public fun PixaChartLegend(series: List<PixaChartSeries>, modifier: Modifier = Modifier) {
    PixaChartLegend(series.map { it.name }, modifier, series.map { it.color })
}

/**
 * Names the slices of a [PixaPieChart] or any chart's series by position: [names] in order,
 * each in the matching entry of [colors], else the category hue at that position, as the
 * charts color them.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun PixaChartLegend(names: List<String>, modifier: Modifier = Modifier, colors: List<Color> = emptyList()) {
    val theme = PixaTheme.colors
    val spacing = PixaTheme.spacing
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        names.forEachIndexed { index, name ->
            val color = colors.getOrNull(index)?.takeIf { it != Color.Unspecified }
                ?: if (names.size == 1) theme.primary else theme.category(index)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                PixaSurface(Modifier.size(10.dp), color = color, shape = PixaTheme.shapes.full) {}
                PixaText(name, style = PixaTheme.typography.caption, color = theme.mutedForeground)
            }
        }
    }
}
