package com.pixamob.pixacompose.charts

import androidx.compose.ui.graphics.Color

/**
 * One series of a multi-series chart: one value per x position.
 *
 * @param name names the series in a [PixaChartLegend].
 * @param color [Color.Unspecified] takes the next of the theme's category hues.
 * @param dashed draws a line series dashed, such as a goal or a forecast.
 */
public data class PixaChartSeries(
    val values: List<Number>,
    val name: String = "",
    val color: Color = Color.Unspecified,
    val dashed: Boolean = false,
)

/** One dot of a [PixaScatterChart]: [x] is the position along the bottom, such as a day index. */
public data class PixaChartPoint(val x: Number, val y: Number)

/** One period of a [PixaCandlestickChart]: the first, last, lowest and highest value in it. */
public data class PixaCandle(val open: Number, val close: Number, val low: Number, val high: Number)

/** A reference drawn across a Cartesian chart, behind no data: a goal, a limit, a normal range. */
public sealed interface PixaChartGuide {
    /** A horizontal line at [y], such as a target weight. */
    public data class Line(val y: Number, val label: String? = null, val color: Color = Color.Unspecified) : PixaChartGuide

    /** A shaded band from [from] to [to], such as a healthy range. */
    public data class Band(
        val from: Number,
        val to: Number,
        val label: String? = null,
        val color: Color = Color.Unspecified,
    ) : PixaChartGuide
}
