package com.pixamob.pixacompose.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.graphics.drawscope.clipRect
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
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.multiplatform.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.multiplatform.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.CartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.CartesianMarkerController
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.Interaction
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
import com.patrykandpatrick.vico.multiplatform.common.data.ExtraStore
import com.pixamob.pixacompose.theme.PixaTheme
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow

private val DefaultChartHeight = 180.dp

/**
 * A line chart of one series, drawn by Vico in the theme's colors. [labels] name the points
 * along the bottom; leave it empty to show their positions instead.
 *
 * @param fittedYAxis fits the vertical axis to the values instead of starting it at 0, so
 * small changes in large numbers, such as a body weight, show as a slope rather than a flat line.
 * @param drawIn draws the chart in from the left when it appears and when [values] change.
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
) {
    if (values.isEmpty()) return
    val fitted = remember(values, fittedYAxis) { if (fittedYAxis) FittedRange.of(values) else null }
    val primary = PixaTheme.colors.primary
    val line = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(Fill(primary)),
        areaFill = LineCartesianLayer.AreaFill.single(
            Fill(Brush.verticalGradient(listOf(primary.copy(alpha = 0.25f), Color.Transparent))),
        ),
        pointConnector = if (smooth) LineCartesianLayer.PointConnector.cubic() else LineCartesianLayer.PointConnector.Sharp,
    )
    val layer = rememberLineCartesianLayer(
        LineCartesianLayer.LineProvider.series(line),
        rangeProvider = fitted ?: remember { CartesianLayerRangeProvider.auto() },
    )
    val model = remember(values) { CartesianChartModel(LineCartesianLayerModel.build { series(values) }) }
    val reveal = if (drawIn) drawInModifier(values) else Modifier
    Chart(layer, model, labels, modifier.then(reveal), yStep = fitted?.step)
}

/** Clips the chart to a growing width from the left, keyed on [key]. */
@Composable
private fun drawInModifier(key: Any): Modifier {
    val fraction = remember(key) { Animatable(0f) }
    val spec = PixaTheme.motion.overlay
    LaunchedEffect(key) { fraction.animateTo(1f, spec.spec()) }
    return Modifier.drawWithContent {
        clipRect(right = size.width * fraction.value.coerceIn(0f, 1f)) { this@drawWithContent.drawContent() }
    }
}

/**
 * A vertical range a little wider than the values, with ends on round steps, so the axis labels
 * are round numbers and the line never touches the top or bottom edge.
 */
private class FittedRange(private val bottom: Double, private val top: Double, val step: Double) : CartesianLayerRangeProvider {
    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double = bottom
    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double = top

    companion object {
        private const val Divisions = 4

        fun of(values: List<Number>): FittedRange {
            val low = values.minOf { it.toDouble() }
            val high = values.maxOf { it.toDouble() }
            val spread = (high - low).takeIf { it > 0.0 } ?: max(abs(high) * 0.1, 1.0)
            val step = niceStep(spread / Divisions)
            return FittedRange(floor(low / step - 0.5) * step, ceil(high / step + 0.5) * step, step)
        }

        /** The nearest of 1, 2, 2.5 and 5 times a power of ten at or above [raw]. */
        private fun niceStep(raw: Double): Double {
            val magnitude = 10.0.pow(floor(log10(raw)))
            val nice = listOf(1.0, 2.0, 2.5, 5.0, 10.0).first { it * magnitude >= raw }
            return nice * magnitude
        }
    }
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
    /** Distance between vertical axis labels; null lets Vico choose. */
    yStep: Double? = null,
) {
    val label = rememberAxisLabelComponent(
        PixaTheme.typography.caption.copy(color = PixaTheme.colors.mutedForeground),
    )
    val guideline = rememberAxisGuidelineComponent(fill = Fill(PixaTheme.colors.border))
    val bottomFormatter = remember(labels) {
        CartesianValueFormatter { _, value, _ -> labels.getOrNull(value.toInt()) ?: value.toInt().toString() }
    }
    val colors = PixaTheme.colors
    // Touch or drag across the chart to show the value nearest the finger; it stays after release.
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
        markerController = remember { ScrubMarkerController() },
        startAxis = VerticalAxis.rememberStart(
            line = null,
            label = label,
            tick = null,
            guideline = guideline,
            itemPlacer = remember(yStep) { VerticalAxis.ItemPlacer.step({ yStep }) },
        ),
        bottomAxis = HorizontalAxis.rememberBottom(
            line = null, label = label, tick = null, guideline = null, valueFormatter = bottomFormatter,
        ),
    )
    CartesianChartHost(
        chart = chart,
        model = model,
        // The caller's size wins; these only apply when none is given.
        modifier = modifier.fillMaxWidth().height(DefaultChartHeight).holdHorizontalDrag(),
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content),
    )
}

/**
 * Shows the marker at the point nearest the finger from the moment it touches the chart and
 * follows it while it slides. The marker stays after release so the value can be read; tapping
 * the same point again hides it.
 */
private class ScrubMarkerController : CartesianMarkerController {
    private var pressed = false
    private var moved = false
    private var tappedShownPoint = false
    private var shown: List<CartesianMarker.Target>? = null

    override val acceptsLongPress: Boolean = false
    override val lock: CartesianMarkerController.Lock = CartesianMarkerController.Lock.Position

    override fun shouldAcceptInteraction(interaction: Interaction, targets: List<CartesianMarker.Target>): Boolean =
        when (interaction) {
            is Interaction.Press, is Interaction.Release -> true
            is Interaction.Move -> pressed
            else -> false
        }

    override fun shouldShowMarker(interaction: Interaction, targets: List<CartesianMarker.Target>): Boolean {
        val show = when (interaction) {
            is Interaction.Press -> {
                pressed = true
                moved = false
                tappedShownPoint = shown != null && targets.sameXAs(shown)
                targets.isNotEmpty()
            }
            is Interaction.Move -> {
                moved = true
                targets.isNotEmpty()
            }
            is Interaction.Release -> {
                pressed = false
                targets.isNotEmpty() && (moved || !tappedShownPoint)
            }
            else -> shown != null
        }
        shown = if (show) targets else null
        return show
    }

    private fun List<CartesianMarker.Target>.sameXAs(other: List<CartesianMarker.Target>?): Boolean =
        other != null && map { it.x } == other.map { it.x }
}

/**
 * Keeps a scrolling screen still while the finger reads the chart. Once a touch on the chart
 * slides sideways, or slides after a long press, its moves are consumed so a parent
 * `verticalScroll` or `LazyColumn` does not take them. A touch that starts by moving up or down
 * is left alone and scrolls the screen as usual.
 */
private fun Modifier.holdHorizontalDrag(): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val slop = viewConfiguration.touchSlop
        val longPress = viewConfiguration.longPressTimeoutMillis
        var travel = Offset.Zero
        var holding = false
        while (true) {
            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
            if (!change.pressed) break
            if (!holding) {
                travel += change.positionChange()
                val held = change.uptimeMillis - down.uptimeMillis >= longPress
                when {
                    abs(travel.x) > slop && abs(travel.x) > abs(travel.y) -> holding = true
                    held && travel.getDistance() <= slop * 2 -> holding = true
                    abs(travel.y) > slop -> break
                }
            }
            if (holding) change.consume()
        }
    }
}
