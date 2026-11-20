package com.pixamob.pixacompose.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.layer.CartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.CandlestickCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarkerController
import com.patrykandpatrick.vico.compose.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.pixamob.pixacompose.theme.PixaTheme
import com.pixamob.pixacompose.theme.category

internal val DefaultChartHeight = 180.dp

/** The color of series [index] of [count]: the primary color alone, else the category hues. */
@Composable
internal fun seriesColor(color: Color, index: Int, count: Int): Color = when {
    color != Color.Unspecified -> color
    count == 1 -> PixaTheme.colors.primary
    else -> PixaTheme.colors.category(index)
}

/** Small value labels drawn on bars and points. */
@Composable
internal fun valueLabel(): TextComponent =
    rememberTextComponent(
        style = PixaTheme.typography.caption.copy(color = PixaTheme.colors.mutedForeground),
        margins = Insets(bottom = PixaTheme.spacing.xs),
    )

/** [formatValue] as a Vico formatter. */
@Composable
internal fun valueFormatter(formatValue: (Double) -> String): CartesianValueFormatter =
    remember(formatValue) { CartesianValueFormatter { _, value, _ -> formatValue(value) } }

/**
 * Axes, guidelines, guides and the tap marker shared by every Cartesian chart. This is the only
 * place they are styled.
 *
 * @param endAxisFormat draws a second vertical axis at the end for layers placed there,
 * with labels [endYStep] apart.
 * @param visiblePoints shows this many x positions and scrolls and zooms through the rest;
 * null fits every point.
 */
@Composable
internal fun ChartFrame(
    layers: List<CartesianLayer<*>>,
    model: CartesianChartModel,
    labels: List<String>,
    modifier: Modifier,
    formatValue: (Double) -> String,
    yStep: Double? = null,
    guides: List<PixaChartGuide> = emptyList(),
    endAxisFormat: ((Double) -> String)? = null,
    endYStep: Double? = null,
    visiblePoints: Int? = null,
) {
    val colors = PixaTheme.colors
    val label = rememberAxisLabelComponent(PixaTheme.typography.caption.copy(color = colors.mutedForeground))
    val guideline = rememberAxisGuidelineComponent(fill = Fill(colors.border))
    val bottomFormatter = remember(labels) {
        CartesianValueFormatter { _, value, _ -> labels.getOrNull(value.toInt()) ?: value.toInt().toString() }
    }
    val chart = rememberCartesianChart(
        *layers.toTypedArray(),
        marker = rememberMarker(formatValue),
        markerController = if (visiblePoints == null) remember { ScrubMarkerController() } else CartesianMarkerController.rememberShowOnPress(),
        decorations = rememberDecorations(guides, formatValue),
        startAxis = VerticalAxis.rememberStart(
            line = null,
            label = label,
            tick = null,
            guideline = guideline,
            itemPlacer = remember(yStep) { VerticalAxis.ItemPlacer.step({ yStep }) },
            valueFormatter = valueFormatter(formatValue),
        ),
        endAxis = endAxisFormat?.let {
            VerticalAxis.rememberEnd(
                line = null, label = label, tick = null, guideline = null,
                itemPlacer = remember(endYStep) { VerticalAxis.ItemPlacer.step({ endYStep }) },
                valueFormatter = valueFormatter(it),
            )
        },
        bottomAxis = HorizontalAxis.rememberBottom(
            line = null, label = label, tick = null, guideline = null, valueFormatter = bottomFormatter,
        ),
    )
    // Time runs left to right in every language, so the chart is not mirrored.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        val sized = modifier.fillMaxWidth().height(DefaultChartHeight)
        CartesianChartHost(
            chart = chart,
            model = model,
            // The caller's size wins; these only apply when none is given.
            modifier = if (visiblePoints == null) sized.holdHorizontalDrag() else sized,
            scrollState = rememberVicoScrollState(scrollEnabled = visiblePoints != null),
            zoomState = rememberVicoZoomState(
                zoomEnabled = visiblePoints != null,
                initialZoom = if (visiblePoints == null) Zoom.Content else Zoom.max(Zoom.x(visiblePoints.toDouble()), Zoom.Content),
            ),
        )
    }
}

/** The label shown above the touched point: plain values, in the foreground color. */
@Composable
private fun rememberMarker(formatValue: (Double) -> String): DefaultCartesianMarker {
    val colors = PixaTheme.colors
    return rememberDefaultCartesianMarker(
        label = rememberTextComponent(
            style = PixaTheme.typography.caption.copy(color = colors.background),
            // Lifts the label clear of the point's dot.
            margins = Insets(bottom = PixaTheme.spacing.sm),
            padding = Insets(PixaTheme.spacing.sm, PixaTheme.spacing.xs),
            background = rememberShapeComponent(fill = Fill(colors.foreground), shape = PixaTheme.shapes.sm),
        ),
        // Plain text: by default Vico tints the value with the series color.
        valueFormatter = remember(formatValue) {
            DefaultCartesianMarker.ValueFormatter { _, targets ->
                targets.flatMap { target ->
                    when (target) {
                        is LineCartesianLayerMarkerTarget -> target.points.map { formatValue(it.entry.y) }
                        is ColumnCartesianLayerMarkerTarget -> target.columns.map { formatValue(it.entry.y) }
                        is CandlestickCartesianLayerMarkerTarget -> with(target.entry) {
                            listOf("${formatValue(opening)} → ${formatValue(closing)}")
                        }
                        else -> emptyList()
                    }
                }.joinToString(", ")
            }
        },
        labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint,
        // Ringed in the touched series' own color.
        indicator = { seriesColor ->
            ShapeComponent(
                fill = Fill(colors.background),
                shape = CircleShape,
                strokeFill = Fill(seriesColor),
                strokeThickness = 2.dp,
            )
        },
        indicatorSize = 10.dp,
        guideline = rememberAxisGuidelineComponent(fill = Fill(colors.mutedForeground)),
    )
}
