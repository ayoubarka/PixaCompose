package com.pixamob.pixacompose.charts

import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow

/**
 * A vertical range a little wider than the values, with ends on round steps, so the axis labels
 * are round numbers and the line never touches the top or bottom edge.
 */
internal class FittedRange(private val bottom: Double, private val top: Double, val step: Double) : CartesianLayerRangeProvider {
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

/** "12.5", "3", without trailing zeros. */
internal fun plainNumber(value: Double): String {
    val rounded = kotlin.math.round(value * 100) / 100
    return if (rounded == kotlin.math.floor(rounded)) rounded.toLong().toString() else rounded.toString()
}


/** Starts at 0 like Vico's own range and adds room above the top value for its value label. */
internal object Headroom : CartesianLayerRangeProvider {
    private const val Share = 0.25

    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
        val top = if (minY == 0.0 && maxY == 0.0) 1.0 else maxY.coerceAtLeast(0.0)
        return top + (top - minY.coerceAtMost(0.0)) * Share
    }
}
