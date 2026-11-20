package com.pixamob.pixacompose.charts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.data.CandlestickCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.layer.CandlestickCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.absolute
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberCandlestickCartesianLayer
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * A candlestick chart: for each period a bar from its first to its last value and a thin wick
 * from its lowest to its highest, such as a price or a daily range of readings. Periods that
 * rose use the theme's success color, those that fell its destructive color.
 *
 * The vertical axis fits the values unless [fittedYAxis] is false; see [PixaLineChart] for the
 * other parameters.
 */
@Composable
public fun PixaCandlestickChart(
    candles: List<PixaCandle>,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList(),
    candleWidth: Dp = 8.dp,
    fittedYAxis: Boolean = true,
    guides: List<PixaChartGuide> = emptyList(),
    visiblePoints: Int? = null,
    formatValue: (Double) -> String = ::plainNumber,
) {
    if (candles.isEmpty()) return
    val colors = PixaTheme.colors
    val fitted = remember(candles, guides, fittedYAxis) {
        if (fittedYAxis) FittedRange.of(candles.flatMap { listOf(it.low, it.high) } + guides.guideValues()) else null
    }
    val layer = rememberCandlestickCartesianLayer(
        candleProvider = CandlestickCartesianLayer.CandleProvider.absolute(
            bullish = candleOf(colors.success, candleWidth),
            neutral = candleOf(colors.mutedForeground, candleWidth),
            bearish = candleOf(colors.destructive, candleWidth),
        ),
        rangeProvider = fitted ?: remember { CartesianLayerRangeProvider.auto() },
    )
    val model = remember(candles) {
        CartesianChartModel(
            CandlestickCartesianLayerModel.build(
                opening = candles.map { it.open },
                closing = candles.map { it.close },
                low = candles.map { it.low },
                high = candles.map { it.high },
            ),
        )
    }
    ChartFrame(
        listOf(layer), model, labels, modifier, formatValue,
        yStep = fitted?.step, guides = guides, visiblePoints = visiblePoints,
    )
}

/** A filled candle body with a thin wick of the same color. */
@Composable
private fun candleOf(color: Color, width: Dp): CandlestickCartesianLayer.Candle {
    val body = rememberLineComponent(fill = Fill(color), thickness = width, shape = PixaTheme.shapes.sm)
    val wick = rememberLineComponent(fill = Fill(color), thickness = 1.5.dp)
    return remember(body, wick) { CandlestickCartesianLayer.Candle(body, wick) }
}
