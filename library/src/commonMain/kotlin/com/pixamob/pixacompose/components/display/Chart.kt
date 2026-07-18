package com.pixamob.pixacompose.components.display

/**
 * PixaChart — data visualization component wrapping Vico's cartesian chart engine.
 *
 * ### Supported chart types
 * [ChartType.Line], [ChartType.Column], [ChartType.Candlestick].
 *
 * ### Sizing
 * [ChartHeight] presets for the plot area; the wrapping card uses [SizeVariant] padding.
 *
 * ### Color
 * Categorical data uses a balanced palette from [rememberChartCategoricalPalette].
 * See that function's KDoc for a known approximation caveat.
 *
 * ### Usage rules
 * - Line charts: keep series at or below [CHART_MAX_RECOMMENDED_SERIES] (5).
 * - Do not smooth line chart connectors — Vico defaults to sharp connectors.
 * - Legend uses FlowRow so entries wrap rather than clip.
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.candlestickSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberCandlestickCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.multiplatform.common.Fill
import com.patrykandpatrick.vico.multiplatform.common.component.rememberLineComponent
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.components.surfaces.BaseCardVariant
import com.pixamob.pixacompose.components.surfaces.PixaCard
import com.pixamob.pixacompose.theme.*

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Chart type variants supported by the Vico cartesian engine.
 */
enum class ChartType {
    /** Line chart — trends over time. */
    Line,

    /** Column/bar chart — categorical comparisons. */
    Column,

    /** Candlestick chart — financial/OHLC data. */
    Candlestick
}

/**
 * Legend indicator shape.
 */
enum class ChartLegendIndicator {
    /** Line swatch — conventional for [ChartType.Line]. */
    Line,

    /** Square swatch — conventional for [ChartType.Column]. */
    Square,

    /** Circle swatch — conventional for point/dot series. */
    Circle
}

/**
 * Axis and gridline configuration for Vico's four axis slots.
 *
 * Enabling a start/end axis plus a bottom/top axis with [showGridlines] produces
 * dual gridlines projected from both axes.
 */
@Stable
data class ChartAxisConfig(
    /** Show the start (left) axis */
    val showStartAxis: Boolean = true,
    /** Show the bottom axis */
    val showBottomAxis: Boolean = true,
    /** Show the end (right) axis */
    val showEndAxis: Boolean = false,
    /** Show the top axis */
    val showTopAxis: Boolean = false,
    /** Draw dotted gridlines from each enabled axis. */
    val showGridlines: Boolean = true
) {
    companion object {
        /** No axes, no gridlines — for sparklines/[TrendChart]. */
        val None: ChartAxisConfig = ChartAxisConfig(
            showStartAxis = false,
            showBottomAxis = false,
            showEndAxis = false,
            showTopAxis = false,
            showGridlines = false
        )
    }
}

/**
 * Chart height presets for the plot area.
 *
 * Chart-local ladder — no [HierarchicalSize] category covers plot-area height.
 */
enum class ChartHeight {
    /** Compact chart - 150dp */
    Compact,

    /** Small chart - 200dp */
    Small,

    /** Medium chart - 250dp (DEFAULT) */
    Medium,

    /** Large chart - 300dp */
    Large,

    /** Extra large chart - 400dp */
    ExtraLarge
}

/**
 * Scroll behavior for charts
 */
enum class ChartScrollBehavior {
    /** No scrolling - data fits in view */
    None,

    /** Horizontal scrolling enabled */
    Horizontal
}

/**
 * Zoom behavior for charts
 */
enum class ChartZoomBehavior {
    /** No zooming */
    None,

    /** Pinch-to-zoom enabled */
    Enabled
}

/**
 * Maximum recommended series per chart. Exceeding this causes the categorical palette to cycle.
 */
const val CHART_MAX_RECOMMENDED_SERIES: Int = 5

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER / RESOLVERS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Categorical (qualitative) data palette derived from semantic `*ContentDefault` tokens.
 *
 * **Known approximation.** PixaCompose's `ColorPalette` has no dedicated data-viz token group,
 * so this derives the widest hue spread available from existing semantic tokens. Non-semantic
 * hues (brand/accent/info) are ordered first so charts with ≤3 series never imply
 * "good/bad" through success-green or error-red. A dedicated colorblind-tested `dataViz`
 * token group in `theme/Color.kt` is the correct long-term fix.
 *
 * Length is [CHART_MAX_RECOMMENDED_SERIES]; callers with more series cycle through it.
 */
@Composable
fun rememberChartCategoricalPalette(): List<Color> {
    val colors = AppTheme.colors
    return remember(colors) {
        listOf(
            colors.brandContentDefault,
            colors.accentContentDefault,
            colors.infoContentDefault,
            colors.successContentDefault,
            colors.warningContentDefault
        )
    }
}

/** Resolves a series index onto the categorical palette, cycling past its end. */
private fun List<Color>.seriesColor(index: Int): Color = this[index % size]

private fun getChartHeightDp(height: ChartHeight): Dp {
    // Raw literals: see [ChartHeight] — no HierarchicalSize category covers plot-area height.
    return when (height) {
        ChartHeight.Compact -> 150.dp
        ChartHeight.Small -> 200.dp
        ChartHeight.Medium -> 250.dp
        ChartHeight.Large -> 300.dp
        ChartHeight.ExtraLarge -> 400.dp
    }
}

/**
 * Approximate height of the title/subtitle block, for sizing the loading [Skeleton] so it does
 * not jump when real content arrives.
 */
private val ChartHeaderSkeletonHeight: Dp = HierarchicalSize.Container.Huge

/** As [ChartHeaderSkeletonHeight], plus a legend row. */
private val ChartHeaderWithLegendSkeletonHeight: Dp = HierarchicalSize.Container.Massive

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL IMPLEMENTATION
// ════════════════════════════════════════════════════════════════════════════

/**
 * Axis line component styled from Pixa tokens.
 */
@Composable
private fun rememberChartLine() = rememberAxisLineComponent(
    fill = Fill(AppTheme.colors.baseBorderDefault),
    thickness = HierarchicalSize.Border.Compact
)

@Composable
private fun rememberChartTick() = rememberAxisTickComponent(
    fill = Fill(AppTheme.colors.baseBorderDefault),
    thickness = HierarchicalSize.Border.Compact
)

@Composable
private fun rememberChartLabel() = rememberAxisLabelComponent(
    style = AppTheme.typography.captionRegular.copy(color = AppTheme.colors.baseContentCaption)
)

/**
 * Gridline component. Defaults to Vico's `DashedShape()`.
 */
@Composable
private fun rememberChartGuideline(enabled: Boolean) = if (enabled) {
    rememberAxisGuidelineComponent(
        fill = Fill(AppTheme.colors.baseBorderSubtle),
        thickness = HierarchicalSize.Border.Nano
    )
} else {
    null
}

/**
 * Internal chart composable wrapping Vico's CartesianChartHost.
 *
 * Series colors match [ChartLegend] swatches by index via [rememberChartCategoricalPalette].
 */
@Composable
private fun InternalCartesianChart(
    modelProducer: CartesianChartModelProducer,
    chartType: ChartType,
    modifier: Modifier = Modifier,
    axisConfig: ChartAxisConfig = ChartAxisConfig(),
    scrollBehavior: ChartScrollBehavior = ChartScrollBehavior.None,
    zoomBehavior: ChartZoomBehavior = ChartZoomBehavior.None
) {
    val palette = rememberChartCategoricalPalette()

    val layer = when (chartType) {
        ChartType.Line -> rememberLineCartesianLayer(
            lineProvider = LineCartesianLayer.LineProvider.series(
                palette.map { color ->
                    // pointConnector defaults to PointConnector.Sharp — do not replace with cubic().
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(Fill(color))
                    )
                }
            )
        )

        ChartType.Column -> rememberColumnCartesianLayer(
            columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                palette.map { color ->
                    rememberLineComponent(
                        fill = Fill(color),
                        // Column thickness: no HierarchicalSize category expresses bar width;
                        // Spacing.Small (8dp) matches Vico's own default column width.
                        thickness = HierarchicalSize.Spacing.Small
                    )
                }
            )
        )

        // Candlestick keeps Vico's default bullish/bearish colors: for OHLC, up/down genuinely
        // *is* semantic, so the categorical palette would be the wrong encoding here.
        ChartType.Candlestick -> rememberCandlestickCartesianLayer()
    }

    val chart = rememberCartesianChart(
        layer,
        startAxis = if (axisConfig.showStartAxis) {
            VerticalAxis.rememberStart(
                line = rememberChartLine(),
                label = rememberChartLabel(),
                tick = rememberChartTick(),
                guideline = rememberChartGuideline(axisConfig.showGridlines)
            )
        } else null,
        endAxis = if (axisConfig.showEndAxis) {
            VerticalAxis.rememberEnd(
                line = rememberChartLine(),
                label = rememberChartLabel(),
                tick = rememberChartTick(),
                guideline = rememberChartGuideline(axisConfig.showGridlines)
            )
        } else null,
        bottomAxis = if (axisConfig.showBottomAxis) {
            HorizontalAxis.rememberBottom(
                line = rememberChartLine(),
                label = rememberChartLabel(),
                tick = rememberChartTick(),
                guideline = rememberChartGuideline(axisConfig.showGridlines)
            )
        } else null,
        topAxis = if (axisConfig.showTopAxis) {
            HorizontalAxis.rememberTop(
                line = rememberChartLine(),
                label = rememberChartLabel(),
                tick = rememberChartTick(),
                guideline = rememberChartGuideline(axisConfig.showGridlines)
            )
        } else null
    )

    val scrollState = rememberVicoScrollState(
        scrollEnabled = scrollBehavior == ChartScrollBehavior.Horizontal
    )

    val zoomState = rememberVicoZoomState(
        zoomEnabled = zoomBehavior == ChartZoomBehavior.Enabled
    )

    CartesianChartHost(
        chart = chart,
        modelProducer = modelProducer,
        modifier = modifier,
        scrollState = scrollState,
        zoomState = zoomState
    )
}

/** Title/subtitle block. */
@Composable
private fun ChartHeader(title: String?, subtitle: String?) {
    if (title == null && subtitle == null) return

    if (title != null) {
        BasicText(
            text = title,
            style = AppTheme.typography.titleBold.copy(color = AppTheme.colors.baseContentTitle),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    if (subtitle != null) {
        if (title != null) Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Nano))
        BasicText(
            text = subtitle,
            style = AppTheme.typography.captionRegular.copy(color = AppTheme.colors.baseContentCaption),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Legend with [ChartLegendIndicator] swatches. Uses `FlowRow` to wrap entries and avoid overlap.
 *
 * @param entries label → swatch color, resolved against the series palette by index.
 */
@Composable
private fun ChartLegend(
    entries: List<Pair<String, Color>>,
    indicator: ChartLegendIndicator,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) return

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium),
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
    ) {
        entries.forEach { (label, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChartLegendSwatch(color = color, indicator = indicator)
                Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
                BasicText(
                    text = label,
                    style = AppTheme.typography.captionRegular.copy(
                        color = AppTheme.colors.baseContentBody
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Renders one legend swatch in the Line / Square / Circle shape. */
@Composable
private fun ChartLegendSwatch(color: Color, indicator: ChartLegendIndicator) {
    when (indicator) {
        // A line swatch is a short stroke, so it reads from the Border scale (stroke weight)
        // rather than the Icon scale.
        ChartLegendIndicator.Line -> Box(
            modifier = Modifier
                .width(HierarchicalSize.Spacing.Medium)
                .height(HierarchicalSize.Border.Medium)
                .clip(AppTheme.shapes.pill)
                .background(color)
        )

        ChartLegendIndicator.Square -> Box(
            modifier = Modifier
                .size(HierarchicalSize.Icon.Nano)
                .clip(AppTheme.shapes.rounded.extraSmall)
                .background(color)
        )

        ChartLegendIndicator.Circle -> Box(
            modifier = Modifier
                .size(HierarchicalSize.Icon.Nano)
                .clip(CircleShape)
                .background(color)
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API - BASE CHART
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaChart — base chart component wrapping Vico's CartesianChartHost.
 *
 * Supports line, column, and candlestick types with configurable axes, scrolling, and zooming.
 *
 * @param modelProducer CartesianChartModelProducer managing chart data
 * @param chartType Type of chart (Line, Column, Candlestick)
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset for the plot area
 * @param axisConfig Axis/gridline visibility
 * @param scrollBehavior Horizontal scrolling
 * @param zoomBehavior Pinch-to-zoom
 * @param isLoading Shows skeleton placeholder
 * @param variant Card variant
 * @param padding Card padding
 * @param cornerRadius Corner radius
 *
 * @sample
 * PixaChart(
 *     modelProducer = modelProducer,
 *     chartType = ChartType.Line,
 *     title = "Sales Trend"
 * )
 */
@Composable
fun PixaChart(
    modelProducer: CartesianChartModelProducer,
    chartType: ChartType,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    chartHeight: ChartHeight = ChartHeight.Medium,
    axisConfig: ChartAxisConfig = ChartAxisConfig(),
    scrollBehavior: ChartScrollBehavior = ChartScrollBehavior.None,
    zoomBehavior: ChartZoomBehavior = ChartZoomBehavior.None,
    isLoading: Boolean = false,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    padding: SizeVariant = SizeVariant.Medium,
    cornerRadius: Dp = HierarchicalSize.Radius.Medium
) {
    if (isLoading) {
        Skeleton(
            modifier = modifier.fillMaxWidth(),
            height = getChartHeightDp(chartHeight) + ChartHeaderSkeletonHeight,
            shape = RoundedCornerShape(cornerRadius),
            shimmerEnabled = true
        )
        return
    }

    PixaCard(
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        padding = padding,
        cornerRadius = cornerRadius
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (title != null || subtitle != null) {
                ChartHeader(title = title, subtitle = subtitle)
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
            }

            InternalCartesianChart(
                modelProducer = modelProducer,
                chartType = chartType,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(getChartHeightDp(chartHeight)),
                axisConfig = axisConfig,
                scrollBehavior = scrollBehavior,
                zoomBehavior = zoomBehavior
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS - LINE CHART
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaLineChart — convenience line chart. Accepts raw data and manages the model producer.
 *
 * **Use Cases:** Trends, time series, performance metrics, analytics dashboards
 *
 * @param data List of data series (each inner list is one line). Recommended limit:
 *   [CHART_MAX_RECOMMENDED_SERIES]; colors cycle past that count.
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset for the chart area
 * @param axisConfig Axis/gridline visibility
 * @param scrollBehavior Horizontal scrolling
 * @param zoomBehavior Pinch-to-zoom
 * @param isLoading Shows skeleton placeholder
 * @param variant Card variant
 * @param padding Card padding
 * @param cornerRadius Corner radius
 *
 * @sample
 * PixaLineChart(
 *     data = listOf(listOf(4, 12, 8, 16, 10, 14, 6)),
 *     title = "Revenue Trend"
 * )
 */
@Composable
fun PixaLineChart(
    data: List<List<Number>>,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    chartHeight: ChartHeight = ChartHeight.Medium,
    axisConfig: ChartAxisConfig = ChartAxisConfig(),
    scrollBehavior: ChartScrollBehavior = ChartScrollBehavior.None,
    zoomBehavior: ChartZoomBehavior = ChartZoomBehavior.None,
    isLoading: Boolean = false,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    padding: SizeVariant = SizeVariant.Medium,
    cornerRadius: Dp = HierarchicalSize.Radius.Medium
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries {
                data.forEach { seriesData ->
                    series(seriesData)
                }
            }
        }
    }

    PixaChart(
        modelProducer = modelProducer,
        chartType = ChartType.Line,
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        chartHeight = chartHeight,
        axisConfig = axisConfig,
        scrollBehavior = scrollBehavior,
        zoomBehavior = zoomBehavior,
        isLoading = isLoading,
        variant = variant,
        padding = padding,
        cornerRadius = cornerRadius
    )
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS - COLUMN CHART
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaColumnChart — convenience column chart. Accepts raw data and manages the model producer.
 *
 * **Use Cases:** Category comparisons, rankings, distributions, survey results
 *
 * @param data List of data series (each inner list is one set of columns)
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset for the chart area
 * @param axisConfig Axis/gridline visibility
 * @param scrollBehavior Horizontal scrolling
 * @param zoomBehavior Pinch-to-zoom
 * @param isLoading Shows skeleton placeholder
 * @param variant Card variant
 * @param padding Card padding
 * @param cornerRadius Corner radius
 *
 * @sample
 * PixaColumnChart(
 *     data = listOf(listOf(5, 12, 8, 15, 7)),
 *     title = "Monthly Sales"
 * )
 */
@Composable
fun PixaColumnChart(
    data: List<List<Number>>,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    chartHeight: ChartHeight = ChartHeight.Medium,
    axisConfig: ChartAxisConfig = ChartAxisConfig(),
    scrollBehavior: ChartScrollBehavior = ChartScrollBehavior.None,
    zoomBehavior: ChartZoomBehavior = ChartZoomBehavior.None,
    isLoading: Boolean = false,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    padding: SizeVariant = SizeVariant.Medium,
    cornerRadius: Dp = HierarchicalSize.Radius.Medium
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries {
                data.forEach { seriesData ->
                    series(seriesData)
                }
            }
        }
    }

    PixaChart(
        modelProducer = modelProducer,
        chartType = ChartType.Column,
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        chartHeight = chartHeight,
        axisConfig = axisConfig,
        scrollBehavior = scrollBehavior,
        zoomBehavior = zoomBehavior,
        isLoading = isLoading,
        variant = variant,
        padding = padding,
        cornerRadius = cornerRadius
    )
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS - CANDLESTICK CHART
// ════════════════════════════════════════════════════════════════════════════

/**
 * OHLC data point for candlestick charts
 */
@Stable
data class OhlcData(
    val open: Number,
    val high: Number,
    val low: Number,
    val close: Number
)

/**
 * PixaCandlestickChart — convenience candlestick chart. Accepts OHLC data and manages the model producer.
 *
 * **Use Cases:** Stock prices, financial data, market analysis, crypto trading
 *
 * @param data List of OHLC data points
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset for the chart area
 * @param axisConfig Axis/gridline visibility
 * @param scrollBehavior Horizontal scrolling
 * @param zoomBehavior Pinch-to-zoom
 * @param isLoading Shows skeleton placeholder
 * @param variant Card variant
 * @param padding Card padding
 * @param cornerRadius Corner radius
 *
 * @sample
 * PixaCandlestickChart(
 *     data = listOf(
 *         OhlcData(open = 100, high = 110, low = 95, close = 108),
 *         OhlcData(open = 108, high = 115, low = 102, close = 104)
 *     ),
 *     title = "AAPL Stock"
 * )
 */
@Composable
fun PixaCandlestickChart(
    data: List<OhlcData>,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    chartHeight: ChartHeight = ChartHeight.Medium,
    axisConfig: ChartAxisConfig = ChartAxisConfig(),
    scrollBehavior: ChartScrollBehavior = ChartScrollBehavior.None,
    zoomBehavior: ChartZoomBehavior = ChartZoomBehavior.None,
    isLoading: Boolean = false,
    variant: BaseCardVariant = BaseCardVariant.Elevated,
    padding: SizeVariant = SizeVariant.Medium,
    cornerRadius: Dp = HierarchicalSize.Radius.Medium
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            candlestickSeries(
                opening = data.map { it.open },
                closing = data.map { it.close },
                low = data.map { it.low },
                high = data.map { it.high }
            )
        }
    }

    PixaChart(
        modelProducer = modelProducer,
        chartType = ChartType.Candlestick,
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        chartHeight = chartHeight,
        axisConfig = axisConfig,
        scrollBehavior = scrollBehavior,
        zoomBehavior = zoomBehavior,
        isLoading = isLoading,
        variant = variant,
        padding = padding,
        cornerRadius = cornerRadius
    )
}

// ════════════════════════════════════════════════════════════════════════════
// SPECIALIZED CHART VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * TrendChart — borderless sparkline for stat/summary cards.
 *
 * **Use Cases:** Dashboard KPI cards, metric sparklines, trend indicators
 *
 * @param data Single trend data series
 * @param modifier Modifier for the chart
 * @param chartHeight Height preset (default: Compact)
 * @param isLoading Shows skeleton placeholder
 *
 * @sample
 * TrendChart(data = listOf(4, 8, 6, 12, 10, 15, 13, 18))
 */
@Composable
fun TrendChart(
    data: List<Number>,
    modifier: Modifier = Modifier,
    chartHeight: ChartHeight = ChartHeight.Compact,
    isLoading: Boolean = false
) {
    if (isLoading) {
        Skeleton(
            modifier = modifier.fillMaxWidth(),
            height = getChartHeightDp(chartHeight),
            shimmerEnabled = true
        )
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries { series(data) }
        }
    }

    InternalCartesianChart(
        modelProducer = modelProducer,
        chartType = ChartType.Line,
        modifier = modifier
            .fillMaxWidth()
            .height(getChartHeightDp(chartHeight)),
        // Sparkline: no axes/gridlines, per this component's "without axes" contract.
        axisConfig = ChartAxisConfig.None
    )
}

/**
 * ComparisonChart — grouped column chart wrapped in a card with title and legend.
 *
 * **Use Cases:** Before/after comparisons, A/B test results, multi-period analysis
 *
 * @param dataSets Named data series. Recommended limit: [CHART_MAX_RECOMMENDED_SERIES].
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset
 * @param axisConfig Axis/gridline visibility
 * @param isLoading Shows skeleton placeholder
 * @param variant Card variant
 *
 * @sample
 * ComparisonChart(
 *     dataSets = listOf("2024" to listOf(10, 15, 8, 20, 12), "2025" to listOf(14, 18, 12, 25, 16)),
 *     title = "Yearly Comparison"
 * )
 */
@Composable
fun ComparisonChart(
    dataSets: List<Pair<String, List<Number>>>,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    chartHeight: ChartHeight = ChartHeight.Medium,
    axisConfig: ChartAxisConfig = ChartAxisConfig(),
    isLoading: Boolean = false,
    variant: BaseCardVariant = BaseCardVariant.Elevated
) {
    if (isLoading) {
        Skeleton(
            modifier = modifier.fillMaxWidth(),
            height = getChartHeightDp(chartHeight) + ChartHeaderWithLegendSkeletonHeight,
            shape = AppTheme.shapes.rounded.medium,
            shimmerEnabled = true
        )
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }
    val palette = rememberChartCategoricalPalette()

    LaunchedEffect(dataSets) {
        modelProducer.runTransaction {
            columnSeries {
                dataSets.forEach { (_, seriesData) ->
                    series(seriesData)
                }
            }
        }
    }

    PixaCard(
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        padding = SizeVariant.Medium
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (title != null || subtitle != null) {
                ChartHeader(title = title, subtitle = subtitle)
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
            }

            ChartLegend(
                entries = dataSets.mapIndexed { index, (label, _) ->
                    label to palette.seriesColor(index)
                },
                indicator = ChartLegendIndicator.Square
            )

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

            InternalCartesianChart(
                modelProducer = modelProducer,
                chartType = ChartType.Column,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(getChartHeightDp(chartHeight)),
                axisConfig = axisConfig
            )
        }
    }
}

/**
 * MultiLineChart — multi-series line chart wrapped in a card with title and legend.
 *
 * **Use Cases:** Multi-metric dashboards, comparative trends, analytics overviews
 *
 * @param dataSets Named data series. Recommended limit: [CHART_MAX_RECOMMENDED_SERIES]; colors cycle past that count.
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset
 * @param axisConfig Axis/gridline visibility
 * @param scrollBehavior Horizontal scrolling
 * @param zoomBehavior Pinch-to-zoom
 * @param isLoading Shows skeleton placeholder
 * @param variant Card variant
 *
 * @sample
 * MultiLineChart(
 *     dataSets = listOf("Revenue" to listOf(10, 15, 12, 20, 18, 25), "Expenses" to listOf(8, 10, 9, 14, 12, 16)),
 *     title = "Financial Overview"
 * )
 */
@Composable
fun MultiLineChart(
    dataSets: List<Pair<String, List<Number>>>,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    chartHeight: ChartHeight = ChartHeight.Medium,
    axisConfig: ChartAxisConfig = ChartAxisConfig(),
    scrollBehavior: ChartScrollBehavior = ChartScrollBehavior.None,
    zoomBehavior: ChartZoomBehavior = ChartZoomBehavior.None,
    isLoading: Boolean = false,
    variant: BaseCardVariant = BaseCardVariant.Elevated
) {
    if (isLoading) {
        Skeleton(
            modifier = modifier.fillMaxWidth(),
            height = getChartHeightDp(chartHeight) + ChartHeaderWithLegendSkeletonHeight,
            shape = AppTheme.shapes.rounded.medium,
            shimmerEnabled = true
        )
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }
    val palette = rememberChartCategoricalPalette()

    LaunchedEffect(dataSets) {
        modelProducer.runTransaction {
            lineSeries {
                dataSets.forEach { (_, seriesData) ->
                    series(seriesData)
                }
            }
        }
    }

    PixaCard(
        modifier = modifier.fillMaxWidth(),
        variant = variant,
        padding = SizeVariant.Medium
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (title != null || subtitle != null) {
                ChartHeader(title = title, subtitle = subtitle)
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
            }

            ChartLegend(
                entries = dataSets.mapIndexed { index, (label, _) ->
                    label to palette.seriesColor(index)
                },
                indicator = ChartLegendIndicator.Line
            )

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

            InternalCartesianChart(
                modelProducer = modelProducer,
                chartType = ChartType.Line,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(getChartHeightDp(chartHeight)),
                axisConfig = axisConfig,
                scrollBehavior = scrollBehavior,
                zoomBehavior = zoomBehavior
            )
        }
    }
}



