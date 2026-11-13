package com.pixamob.pixacompose.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.Zoom
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.multiplatform.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.multiplatform.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.CartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.CartesianMarkerController
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.multiplatform.common.Fill
import com.patrykandpatrick.vico.multiplatform.common.Insets
import com.patrykandpatrick.vico.multiplatform.common.component.ShapeComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberLineComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberTextComponent
import com.pixamob.pixacompose.theme.PixaTheme

private val DefaultChartHeight = 180.dp

/**
 * A line chart of one series, drawn by Vico in the theme's colors. [labels] name the points
 * along the bottom; leave it empty to show their positions instead.
 */
@Composable
public fun PixaLineChart(
    values: List<Number>,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList(),
    /** Curved line between points; false draws straight segments. */
    smooth: Boolean = true,
) {
    if (values.isEmpty()) return
    val primary = PixaTheme.colors.primary
    val line = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(Fill(primary)),
        areaFill = LineCartesianLayer.AreaFill.single(
            Fill(Brush.verticalGradient(listOf(primary.copy(alpha = 0.25f), Color.Transparent))),
        ),
        pointConnector = if (smooth) LineCartesianLayer.PointConnector.cubic() else LineCartesianLayer.PointConnector.Sharp,
    )
    val layer = rememberLineCartesianLayer(LineCartesianLayer.LineProvider.series(line))
    val model = remember(values) { CartesianChartModel(LineCartesianLayerModel.build { series(values) }) }
    Chart(layer, model, labels, modifier)
}

/** A bar chart of one series, drawn by Vico in the theme's colors. See [PixaLineChart]. */
@Composable
public fun PixaBarChart(
    values: List<Number>,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList(),
    barWidth: Dp = 16.dp,
) {
    if (values.isEmpty()) return
    val bar = rememberLineComponent(
        fill = Fill(PixaTheme.colors.primary),
        thickness = barWidth,
        shape = PixaTheme.shapes.sm,
    )
    val layer = rememberColumnCartesianLayer(ColumnCartesianLayer.ColumnProvider.series(bar))
    val model = remember(values) { CartesianChartModel(ColumnCartesianLayerModel.build { series(values) }) }
    Chart(layer, model, labels, modifier)
}

/** Axes, guidelines and labels shared by every chart. This is the only place they are styled. */
@Composable
private fun Chart(
    layer: CartesianLayer<*>,
    model: CartesianChartModel,
    labels: List<String>,
    modifier: Modifier,
) {
    val label = rememberAxisLabelComponent(
        PixaTheme.typography.caption.copy(color = PixaTheme.colors.mutedForeground),
    )
    val guideline = rememberAxisGuidelineComponent(fill = Fill(PixaTheme.colors.border))
    val bottomFormatter = remember(labels) {
        CartesianValueFormatter { _, value, _ -> labels.getOrNull(value.toInt()) ?: value.toInt().toString() }
    }
    val colors = PixaTheme.colors
    // Tap a point to show its value; tap again to hide it.
    val marker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(
            style = PixaTheme.typography.caption.copy(color = colors.background),
            // Lifts the label clear of the point's dot.
            margins = Insets(bottom = PixaTheme.spacing.sm),
            padding = Insets(PixaTheme.spacing.sm, PixaTheme.spacing.xs),
            background = rememberShapeComponent(fill = Fill(colors.foreground), shape = PixaTheme.shapes.sm),
        ),
        // Plain text: by default Vico tints the value with the series color.
        valueFormatter = remember { DefaultCartesianMarker.ValueFormatter.default(colorCode = false) },
        labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint,
        indicator = { _ ->
            ShapeComponent(
                fill = Fill(colors.background),
                shape = CircleShape,
                strokeFill = Fill(colors.primary),
                strokeThickness = 2.dp,
            )
        },
        indicatorSize = 10.dp,
        guideline = rememberAxisGuidelineComponent(fill = Fill(colors.mutedForeground)),
    )
    val chart = rememberCartesianChart(
        layer,
        marker = marker,
        markerController = CartesianMarkerController.rememberToggleOnTap(),
        startAxis = VerticalAxis.rememberStart(line = null, label = label, tick = null, guideline = guideline),
        bottomAxis = HorizontalAxis.rememberBottom(
            line = null, label = label, tick = null, guideline = null, valueFormatter = bottomFormatter,
        ),
    )
    CartesianChartHost(
        chart = chart,
        model = model,
        // The caller's size wins; these only apply when none is given.
        modifier = modifier.fillMaxWidth().height(DefaultChartHeight),
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content),
    )
}
