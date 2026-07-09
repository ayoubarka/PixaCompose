package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.candlestickSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberCandlestickCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoZoomState
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.*

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Chart type variants
 */
enum class ChartType {
    /** Line chart - best for trends over time */
    Line,

    /** Column/bar chart - best for categorical comparisons */
    Column,

    /** Candlestick chart - best for financial/OHLC data */
    Candlestick
}

/**
 * Axis visibility configuration
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
    val showTopAxis: Boolean = false
)

/**
 * Chart height presets
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

// ============================================================================
// INTERNAL HELPERS
// ============================================================================

private fun getChartHeightDp(height: ChartHeight): Dp {
    return when (height) {
        ChartHeight.Compact -> 150.dp
        ChartHeight.Small -> 200.dp
        ChartHeight.Medium -> 250.dp
        ChartHeight.Large -> 300.dp
        ChartHeight.ExtraLarge -> 400.dp
    }
}

// ============================================================================
// CORE CHART COMPOSABLE
// ============================================================================

/**
 * Internal core chart composable wrapping Vico's CartesianChartHost
 */
@Composable
private fun InternalCartesianChart(
    modelProducer: CartesianChartModelProducer,
    chartType: ChartType,
    modifier: Modifier = Modifier,
    scrollBehavior: ChartScrollBehavior = ChartScrollBehavior.None,
    zoomBehavior: ChartZoomBehavior = ChartZoomBehavior.None
) {
    val chart = when (chartType) {
        ChartType.Line -> rememberCartesianChart(rememberLineCartesianLayer())
        ChartType.Column -> rememberCartesianChart(rememberColumnCartesianLayer())
        ChartType.Candlestick -> rememberCartesianChart(rememberCandlestickCartesianLayer())
    }

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

// ============================================================================
// PUBLIC API - BASE CHART
// ============================================================================

/**
 * PixaChart - Base chart component wrapping Vico's CartesianChartHost
 *
 * Provides a flexible, theme-integrated charting component using the Vico library.
 * Supports line, column, and candlestick chart types with configurable axes,
 * scrolling, and zooming behaviors.
 *
 * @param modelProducer CartesianChartModelProducer managing the chart data
 * @param chartType Type of chart to render (Line, Column, Candlestick)
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset for the chart area
 * @param axisConfig Axis visibility configuration
 * @param scrollBehavior Scroll behavior for the chart
 * @param zoomBehavior Zoom behavior for the chart
 * @param isLoading Loading state (shows skeleton)
 * @param variant Card variant for the wrapping card container
 * @param padding Card padding
 * @param cornerRadius Corner radius
 *
 * @sample
 * ```
 * val modelProducer = remember { CartesianChartModelProducer() }
 *
 * LaunchedEffect(Unit) {
 *     modelProducer.runTransaction {
 *         lineSeries { series(4, 12, 8, 16, 10, 14, 6) }
 *     }
 * }
 *
 * PixaChart(
 *     modelProducer = modelProducer,
 *     chartType = ChartType.Line,
 *     title = "Sales Trend",
 *     subtitle = "Last 7 days"
 * )
 * ```
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
            height = getChartHeightDp(chartHeight) + 60.dp,
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
            // Header section
            if (title != null || subtitle != null) {
                if (title != null) {
                    Text(
                        text = title,
                        style = AppTheme.typography.titleBold,
                        color = AppTheme.colors.baseContentTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Nano))
                    Text(
                        text = subtitle,
                        style = AppTheme.typography.captionRegular,
                        color = AppTheme.colors.baseContentCaption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
            }

            // Chart area
            InternalCartesianChart(
                modelProducer = modelProducer,
                chartType = chartType,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(getChartHeightDp(chartHeight)),
                scrollBehavior = scrollBehavior,
                zoomBehavior = zoomBehavior
            )
        }
    }
}

// ============================================================================
// CONVENIENCE VARIANTS - LINE CHART
// ============================================================================

/**
 * PixaLineChart - Convenience line chart component
 *
 * Simplified API for the most common line chart use case.
 * Accepts raw data values and handles the CartesianChartModelProducer internally.
 *
 * **Use Cases:** Trends, time series, performance metrics, analytics dashboards
 *
 * @param data List of data series (each inner list is one line)
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset for the chart area
 * @param axisConfig Axis visibility configuration
 * @param scrollBehavior Scroll behavior
 * @param zoomBehavior Zoom behavior
 * @param isLoading Loading state
 * @param variant Card variant
 * @param padding Card padding
 * @param cornerRadius Corner radius
 *
 * @sample
 * ```
 * PixaLineChart(
 *     data = listOf(listOf(4, 12, 8, 16, 10, 14, 6)),
 *     title = "Revenue Trend",
 *     subtitle = "Last 7 days"
 * )
 *
 * // Multiple lines
 * PixaLineChart(
 *     data = listOf(
 *         listOf(4, 12, 8, 16, 10, 14, 6),
 *         listOf(2, 8, 6, 12, 8, 10, 4)
 *     ),
 *     title = "Revenue vs Expenses"
 * )
 * ```
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

// ============================================================================
// CONVENIENCE VARIANTS - COLUMN CHART
// ============================================================================

/**
 * PixaColumnChart - Convenience column/bar chart component
 *
 * Simplified API for column charts. Accepts raw data values and manages
 * the CartesianChartModelProducer internally.
 *
 * **Use Cases:** Category comparisons, rankings, distributions, survey results
 *
 * @param data List of data series (each inner list is one set of columns)
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset for the chart area
 * @param axisConfig Axis visibility configuration
 * @param scrollBehavior Scroll behavior
 * @param zoomBehavior Zoom behavior
 * @param isLoading Loading state
 * @param variant Card variant
 * @param padding Card padding
 * @param cornerRadius Corner radius
 *
 * @sample
 * ```
 * PixaColumnChart(
 *     data = listOf(listOf(5, 12, 8, 15, 7)),
 *     title = "Monthly Sales",
 *     subtitle = "Units sold per month"
 * )
 *
 * // Grouped columns
 * PixaColumnChart(
 *     data = listOf(
 *         listOf(5, 12, 8, 15, 7),
 *         listOf(3, 10, 6, 12, 5)
 *     ),
 *     title = "Sales by Region"
 * )
 * ```
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

// ============================================================================
// CONVENIENCE VARIANTS - CANDLESTICK CHART
// ============================================================================

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
 * PixaCandlestickChart - Convenience candlestick chart component
 *
 * Simplified API for financial OHLC candlestick charts. Accepts structured
 * OHLC data and manages the CartesianChartModelProducer internally.
 *
 * **Use Cases:** Stock prices, financial data, market analysis, crypto trading
 *
 * @param data List of OHLC data points
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset for the chart area
 * @param axisConfig Axis visibility configuration
 * @param scrollBehavior Scroll behavior
 * @param zoomBehavior Zoom behavior
 * @param isLoading Loading state
 * @param variant Card variant
 * @param padding Card padding
 * @param cornerRadius Corner radius
 *
 * @sample
 * ```
 * PixaCandlestickChart(
 *     data = listOf(
 *         OhlcData(open = 100, high = 110, low = 95, close = 108),
 *         OhlcData(open = 108, high = 115, low = 102, close = 104),
 *         OhlcData(open = 104, high = 112, low = 100, close = 110),
 *         OhlcData(open = 110, high = 118, low = 106, close = 115)
 *     ),
 *     title = "AAPL Stock",
 *     subtitle = "Last 4 days"
 * )
 * ```
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

// ============================================================================
// SPECIALIZED CHART VARIANTS
// ============================================================================

/**
 * TrendChart - Minimal line chart optimized for dashboard stat cards
 *
 * A compact, borderless chart designed to sit inside stat/summary cards.
 * Shows trend data without axes for a clean, minimal look.
 *
 * **Use Cases:** Dashboard KPI cards, metric sparklines, trend indicators
 *
 * @param data Single series of trend data values
 * @param modifier Modifier for the chart
 * @param chartHeight Height preset (default: Compact)
 * @param isLoading Loading state
 *
 * @sample
 * ```
 * TrendChart(
 *     data = listOf(4, 8, 6, 12, 10, 15, 13, 18)
 * )
 * ```
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
            .height(getChartHeightDp(chartHeight))
    )
}

/**
 * ComparisonChart - Side-by-side column chart for comparing datasets
 *
 * A grouped column chart wrapped in a card with title and legend support.
 *
 * **Use Cases:** Before/after comparisons, A/B test results, multi-period analysis
 *
 * @param dataSets List of named data series for comparison
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset
 * @param isLoading Loading state
 * @param variant Card variant
 *
 * @sample
 * ```
 * ComparisonChart(
 *     dataSets = listOf(
 *         "2024" to listOf(10, 15, 8, 20, 12),
 *         "2025" to listOf(14, 18, 12, 25, 16)
 *     ),
 *     title = "Yearly Comparison",
 *     subtitle = "Q1–Q5 performance"
 * )
 * ```
 */
@Composable
fun ComparisonChart(
    dataSets: List<Pair<String, List<Number>>>,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    chartHeight: ChartHeight = ChartHeight.Medium,
    isLoading: Boolean = false,
    variant: BaseCardVariant = BaseCardVariant.Elevated
) {
    if (isLoading) {
        Skeleton(
            modifier = modifier.fillMaxWidth(),
            height = getChartHeightDp(chartHeight) + 80.dp,
            shape = RoundedCornerShape(HierarchicalSize.Radius.Medium),
            shimmerEnabled = true
        )
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

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
            // Header
            if (title != null) {
                Text(
                    text = title,
                    style = AppTheme.typography.titleBold,
                    color = AppTheme.colors.baseContentTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Nano))
                Text(
                    text = subtitle,
                    style = AppTheme.typography.captionRegular,
                    color = AppTheme.colors.baseContentCaption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (title != null || subtitle != null) {
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                dataSets.forEach { (label, _) ->
                    Text(
                        text = "● $label",
                        style = AppTheme.typography.captionRegular,
                        color = AppTheme.colors.baseContentBody
                    )
                    Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Medium))
                }
            }

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

            // Chart
            InternalCartesianChart(
                modelProducer = modelProducer,
                chartType = ChartType.Column,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(getChartHeightDp(chartHeight))
            )
        }
    }
}

/**
 * MultiLineChart - Line chart with multiple series and legend
 *
 * A multi-series line chart wrapped in a card with title and legend.
 *
 * **Use Cases:** Multi-metric dashboards, comparative trends, analytics overviews
 *
 * @param dataSets List of named data series
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset
 * @param axisConfig Axis configuration
 * @param scrollBehavior Scroll behavior
 * @param zoomBehavior Zoom behavior
 * @param isLoading Loading state
 * @param variant Card variant
 *
 * @sample
 * ```
 * MultiLineChart(
 *     dataSets = listOf(
 *         "Revenue" to listOf(10, 15, 12, 20, 18, 25),
 *         "Expenses" to listOf(8, 10, 9, 14, 12, 16)
 *     ),
 *     title = "Financial Overview",
 *     subtitle = "Revenue vs Expenses (6 months)"
 * )
 * ```
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
            height = getChartHeightDp(chartHeight) + 80.dp,
            shape = RoundedCornerShape(HierarchicalSize.Radius.Medium),
            shimmerEnabled = true
        )
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

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
            // Header
            if (title != null) {
                Text(
                    text = title,
                    style = AppTheme.typography.titleBold,
                    color = AppTheme.colors.baseContentTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Nano))
                Text(
                    text = subtitle,
                    style = AppTheme.typography.captionRegular,
                    color = AppTheme.colors.baseContentCaption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (title != null || subtitle != null) {
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                dataSets.forEach { (label, _) ->
                    Text(
                        text = "— $label",
                        style = AppTheme.typography.captionRegular,
                        color = AppTheme.colors.baseContentBody
                    )
                    Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Medium))
                }
            }

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

            // Chart
            InternalCartesianChart(
                modelProducer = modelProducer,
                chartType = ChartType.Line,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(getChartHeightDp(chartHeight)),
                scrollBehavior = scrollBehavior,
                zoomBehavior = zoomBehavior
            )
        }
    }
}

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * 1. Simple line chart with raw data:
 * ```
 * PixaLineChart(
 *     data = listOf(listOf(4, 12, 8, 16, 10, 14, 6)),
 *     title = "Revenue Trend",
 *     subtitle = "Last 7 days"
 * )
 * ```
 *
 * 2. Column chart with categories:
 * ```
 * PixaColumnChart(
 *     data = listOf(listOf(5, 12, 8, 15, 7)),
 *     title = "Monthly Sales",
 *     chartHeight = ChartHeight.Large
 * )
 * ```
 *
 * 3. Candlestick chart for financial data:
 * ```
 * PixaCandlestickChart(
 *     data = listOf(
 *         OhlcData(100, 110, 95, 108),
 *         OhlcData(108, 115, 102, 104),
 *         OhlcData(104, 112, 100, 110),
 *         OhlcData(110, 118, 106, 115)
 *     ),
 *     title = "AAPL Stock Price"
 * )
 * ```
 *
 * 4. Advanced chart with model producer for full control:
 * ```
 * val modelProducer = remember { CartesianChartModelProducer() }
 *
 * LaunchedEffect(salesData) {
 *     modelProducer.runTransaction {
 *         lineSeries {
 *             series(salesData.map { it.revenue })
 *             series(salesData.map { it.cost })
 *         }
 *     }
 * }
 *
 * PixaChart(
 *     modelProducer = modelProducer,
 *     chartType = ChartType.Line,
 *     title = "Revenue vs Cost",
 *     axisConfig = ChartAxisConfig(
 *         showStartAxis = true,
 *         showBottomAxis = true,
 *         showEndAxis = true
 *     ),
 *     scrollBehavior = ChartScrollBehavior.Horizontal,
 *     zoomBehavior = ChartZoomBehavior.Enabled
 * )
 * ```
 *
 * 5. Sparkline trend in a dashboard card:
 * ```
 * Row {
 *     Column(modifier = Modifier.weight(1f)) {
 *         Text("Revenue", style = AppTheme.typography.captionRegular)
 *         Text("$12,345", style = AppTheme.typography.titleBold)
 *         Text("+12.5%", style = AppTheme.typography.captionBold, color = Color.Green)
 *     }
 *     TrendChart(
 *         data = listOf(4, 8, 6, 12, 10, 15, 13, 18),
 *         modifier = Modifier.weight(1f)
 *     )
 * }
 * ```
 *
 * 6. Multi-line comparison chart:
 * ```
 * MultiLineChart(
 *     dataSets = listOf(
 *         "Revenue" to listOf(10, 15, 12, 20, 18, 25),
 *         "Expenses" to listOf(8, 10, 9, 14, 12, 16)
 *     ),
 *     title = "Financial Overview"
 * )
 * ```
 *
 * 7. Grouped column comparison:
 * ```
 * ComparisonChart(
 *     dataSets = listOf(
 *         "Q1" to listOf(10, 15, 8),
 *         "Q2" to listOf(14, 18, 12)
 *     ),
 *     title = "Quarterly Results"
 * )
 * ```
 *
 * 8. Loading state:
 * ```
 * PixaLineChart(
 *     data = emptyList(),
 *     title = "Loading...",
 *     isLoading = true
 * )
 * ```
 *
 * 9. Outlined card variant:
 * ```
 * PixaColumnChart(
 *     data = listOf(listOf(5, 12, 8, 15, 7)),
 *     title = "Monthly Sales",
 *     variant = BaseCardVariant.Outlined,
 *     chartHeight = ChartHeight.Small
 * )
 * ```
 *
 * 10. Chart with scrolling and zooming:
 * ```
 * PixaLineChart(
 *     data = listOf((1..50).map { (Math.random() * 100).toInt() }),
 *     title = "50 Data Points",
 *     scrollBehavior = ChartScrollBehavior.Horizontal,
 *     zoomBehavior = ChartZoomBehavior.Enabled,
 *     chartHeight = ChartHeight.Large
 * )
 * ```
 */

