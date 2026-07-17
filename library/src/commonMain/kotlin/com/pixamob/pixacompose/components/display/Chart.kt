package com.pixamob.pixacompose.components.display

/**
 * PixaChart — PixaCompose's migration of Uber Base's "Charts" library.
 *
 * Source: https://base.uber.com/6d2425e9f/p/61b6c1-charts.md
 *
 * Purpose (spec): "Charts is a global library for data visualization that provides reusable,
 *   flexible chart modules that can be used across all platforms and lines of business."
 *
 * Anatomy (spec): "Each chart is made up of a sum of parts, each part being its own individual
 *   module that is swappable for maximum customizability." The spec names: Title (optional),
 *   Legend (optional), Axes (with alignment + tick marks), Gridlines (dual gridlines, dotted
 *   lines, label position), and the data-visualization area. All five are modelled here:
 *   title/subtitle → [PixaChart]'s header; legend → [ChartLegendIndicator] + `legend`;
 *   axes/tick marks/gridlines → [ChartAxisConfig]; data area → the Vico layer.
 *
 * Variants: the spec catalogues a large chart taxonomy (bar/stacked/grouped/histogram/waterfall,
 *   line/bump/bell-curve, area/stacked-area, bubble/scatter/pie/donut, Sankey/treemap, data
 *   table). PixaCompose implements the subset the approved `vico-multiplatform` cartesian engine
 *   covers — [ChartType.Line], [ChartType.Column], [ChartType.Candlestick] — see "Out of scope".
 *
 * Sizing: [ChartHeight] presets for the plot area; the wrapping card uses [SizeVariant] padding.
 *
 * Color (spec): the spec mandates a *dedicated* data-viz palette rather than raw brand colors —
 *   "Brand colors usually possess a specific meaning or serve as a product's function... If you
 *   use these colors to represent data not associated with these ideas, you could be creating
 *   confusion for the user." For categorical data it requires "a balanced palette with a variety
 *   of lightness, hue, and saturation... while also not creating a group of colors with a clear
 *   order or hierarchy." See [rememberChartCategoricalPalette] for how that maps onto Pixa's
 *   token system, and the caveat recorded there.
 *
 * Usage rules carried over from the spec (enforced in docs, not by throwing — these are the
 *   spec's own "rule of thumb" wording, and a hard runtime cap would be a worse failure mode):
 *   - Line charts: "Don't plot too many lines. A good rule of thumb is to limit yourself to five
 *     or fewer lines, lest the plot ends up looking like an unreadable tangle."
 *     [CHART_MAX_RECOMMENDED_SERIES]
 *   - Line charts: "Don't smooth the line. Attempting this kind of fitting will be assured of
 *     distorting the perception of trends in the data." Vico 2.4.3's `Line.pointConnector`
 *     already defaults to `PointConnector.Sharp` (verified in the dependency's sources), so this
 *     rule holds by default — do not switch these layers to `PointConnector.cubic(...)`.
 *   - Labels: "Provide enough room, so the labels don't overlap." The legend uses `FlowRow` so
 *     entries wrap rather than clip; axis labels are left to Vico's own placer.
 *
 * Out of scope (documented rather than silently missing): pie/donut, scatter/bubble, area,
 *   histogram, waterfall, Sankey, treemap and data-table are all named by the spec but are not
 *   provided by the cartesian Vico host this file wraps, and inventing them from `Canvas`
 *   primitives would be a new component family rather than a migration of this one. The spec's
 *   sequential/divergent numerical scales are likewise unimplemented — they describe
 *   heatmap/choropleth encodings none of the three supported cartesian types use.
 *
 * Responsive behavior: the spec states no breakpoint rules (its only stated dimensions are a
 *   single desktop artboard, "W: 891 px; H: 428 px"), so no adaptive behavior is inferred here.
 *   Charts fill their parent's width; height is caller-controlled via [ChartHeight].
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
import com.pixamob.pixacompose.theme.*

// ============================================================================
// ENUMS & TYPES
// ============================================================================

/**
 * Chart type variants.
 *
 * Covers the subset of the Uber Base chart taxonomy that the approved `vico-multiplatform`
 * cartesian engine supports. See the file header for the spec types that are out of scope.
 */
enum class ChartType {
    /** Line chart - best for trends over time. Spec: "Change over time". */
    Line,

    /**
     * Column/bar chart - best for categorical comparisons. Spec: "If the variable we want to
     * show on the horizontal axis is not numeric or ordered but instead categorical, we need to
     * use a bar chart instead of a line chart."
     */
    Column,

    /** Candlestick chart - best for financial/OHLC data. A Pixa/Vico extension, not a spec type. */
    Candlestick
}

/**
 * Legend indicator shape. The Uber Base spec names exactly three: "Line", "Square", "Circle".
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
 * Axis and gridline configuration.
 *
 * Maps the spec's "Axes" (alignment + tick marks) and "Gridlines" (dual gridlines, dotted lines)
 * anatomy onto Vico's four axis slots. "Dual gridlines" — i.e. gridlines projected from both a
 * vertical and a horizontal axis — is what you get by enabling a start/end axis *and* a
 * bottom/top axis with [showGridlines] on.
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
    /**
     * Draw gridlines projected from each enabled axis. Spec anatomy lists gridlines as dotted —
     * Vico's `rememberAxisGuidelineComponent` already defaults to a `DashedShape`.
     */
    val showGridlines: Boolean = true
) {
    companion object {
        /** No axes, no gridlines — for sparklines/[TrendChart] where the plot is the whole point. */
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
 * These are a chart-local ladder rather than [HierarchicalSize] values: no `HierarchicalSize`
 * category expresses "data-visualization area height" (`Card`/`Image`/`Container` were each
 * checked and none has a matching 150/200/250/300/400 rung), and the spec states no height
 * guidance beyond one desktop artboard. Kept as a public enum for source compatibility.
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
 * Spec: "Don't plot too many lines. A good rule of thumb is to limit yourself to five or fewer
 * lines." The same ceiling is stated for part-to-whole slices ("If you have more than five
 * categories, you might consider using a different chart type"), so it also sets the length of
 * [rememberChartCategoricalPalette]. Advisory: callers may exceed it, and the palette then
 * cycles.
 */
const val CHART_MAX_RECOMMENDED_SERIES: Int = 5

// ============================================================================
// THEME PROVIDER / RESOLVERS
// ============================================================================

/**
 * The categorical (qualitative) data palette.
 *
 * Spec: "For qualitative (categorical) data, it's important to have a balanced palette with a
 * variety of lightness, hue, and saturation. So viewers can easily distinguish them from one
 * another while also not creating a group of colors with a clear order or hierarchy."
 *
 * **Known approximation.** The spec's own answer is a *bespoke* data-viz ramp, deliberately
 * separate from the brand palette, and colorblind-tested ("an estimated 8% of males... have
 * color vision problems"). PixaCompose's `ColorPalette` has no data-viz token group, so this
 * derives the widest hue spread available from the existing semantic `*ContentDefault` tokens.
 * The non-semantic hues (brand/accent/info) are ordered first so that charts with <= 3 series —
 * the common case — never imply "good/bad" through success-green or error-red. A dedicated
 * colorblind-tested `dataViz` token group in `theme/Color.kt` is the correct long-term fix;
 * that is a shared-theme change and is out of scope for this component migration.
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

// ============================================================================
// INTERNAL IMPLEMENTATION
// ============================================================================

/**
 * Builds the four axis slots from [ChartAxisConfig], styled from Pixa tokens.
 *
 * Spec anatomy: axis line + tick marks + labels + (dotted) gridlines. The spec's only stated
 * stroke value is "Borders | Weight | 1 px" → [HierarchicalSize.Border.Compact].
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
 * Gridline. Shape is left at Vico's default `DashedShape()`, which already matches the spec's
 * "Dotted lines" gridline anatomy — hence no explicit `shape` argument here.
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
 * Internal core chart composable wrapping Vico's CartesianChartHost.
 *
 * Series colors come from [rememberChartCategoricalPalette] so that the plotted series and the
 * [ChartLegend] swatches are guaranteed to agree by index.
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
                    // pointConnector defaults to PointConnector.Sharp — the spec's "Don't smooth
                    // the line" rule. Do not replace with cubic().
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

/** Title/subtitle block — the spec's optional "Title" anatomy part. */
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
 * Legend — the spec's optional "Legend" anatomy part, with its three named indicator shapes
 * (Line / Square / Circle).
 *
 * `FlowRow` honors the spec's "Provide enough room, so the labels don't overlap" rule by
 * wrapping entries onto a new line instead of clipping them.
 *
 * @param entries label → swatch color, already resolved against the series palette by index.
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

/** Renders one legend swatch in the spec's Line / Square / Circle shapes. */
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
 * @param data List of data series (each inner list is one line). Spec: "Don't plot too many
 *   lines. A good rule of thumb is to limit yourself to five or fewer lines" —
 *   [CHART_MAX_RECOMMENDED_SERIES]. Series colors cycle past that count.
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
            .height(getChartHeightDp(chartHeight)),
        // Sparkline: no axes/gridlines, per this component's "without axes" contract.
        axisConfig = ChartAxisConfig.None
    )
}

/**
 * ComparisonChart - Side-by-side column chart for comparing datasets
 *
 * A grouped column chart wrapped in a card with title and legend support.
 *
 * **Use Cases:** Before/after comparisons, A/B test results, multi-period analysis
 *
 * Legend swatches use [ChartLegendIndicator.Square] and are color-matched to the plotted series
 * by index via [rememberChartCategoricalPalette].
 *
 * @param dataSets List of named data series for comparison. Spec advises at most
 *   [CHART_MAX_RECOMMENDED_SERIES] series.
 * @param modifier Modifier for the chart container
 * @param title Optional chart title
 * @param subtitle Optional chart subtitle
 * @param chartHeight Height preset
 * @param axisConfig Axis/gridline configuration
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
 * MultiLineChart - Line chart with multiple series and legend
 *
 * A multi-series line chart wrapped in a card with title and legend.
 *
 * **Use Cases:** Multi-metric dashboards, comparative trends, analytics overviews
 *
 * Legend swatches use [ChartLegendIndicator.Line] and are color-matched to the plotted series by
 * index via [rememberChartCategoricalPalette].
 *
 * @param dataSets List of named data series. Spec: "limit yourself to five or fewer lines" —
 *   [CHART_MAX_RECOMMENDED_SERIES]. Series colors cycle past that count.
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

