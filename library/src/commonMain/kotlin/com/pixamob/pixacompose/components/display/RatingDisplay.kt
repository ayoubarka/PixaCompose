package com.pixamob.pixacompose.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.inputs.PixaStarRating
import com.pixamob.pixacompose.components.inputs.StarRatingSize
import com.pixamob.pixacompose.components.inputs.StarRatingVariant
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import kotlin.math.roundToInt

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

// No custom types — sizing uses the shared [SizeVariant] ladder.

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaRatingSummary — a read-only rating block: average score, star visual, review count,
 * and an optional per-star distribution histogram.
 *
 * ### Anatomy
 * Left column: numeric average, 5-star visual, total review count.  
 * Right column: 5 horizontal bars from 5★ down to 1★ showing the share of reviews per star.
 *
 * ### Sizing
 * [SizeVariant] drives the histogram bar height and text scale.
 *
 * @param average Average rating, 0f-5f
 * @param totalCount Total number of reviews
 * @param distribution Map of star value (1-5) to fraction of total reviews (0f-1f)
 * @param modifier Modifier for the summary row
 * @param size Size preset
 * @param showDistribution Whether to render the histogram bars
 */
@Composable
fun PixaRatingSummary(
    average: Float,
    totalCount: Int,
    distribution: Map<Int, Float>,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    showDistribution: Boolean = true
) {
    val barHeight = when (size) {
        SizeVariant.Small, SizeVariant.Compact -> HierarchicalSize.Spacing.Small
        SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> HierarchicalSize.Spacing.Medium
        else -> HierarchicalSize.Spacing.Small
    }
    val captionStyle = when (size) {
        SizeVariant.Small, SizeVariant.Compact -> AppTheme.typography.captionLight
        SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> AppTheme.typography.bodyRegular
        else -> AppTheme.typography.captionRegular
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Average + stars + count
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
        ) {
            val roundedAverage = (average.coerceIn(0f, 5f) * 10).roundToInt() / 10f
            BasicText(
                text = roundedAverage.toString(),
                style = AppTheme.typography.displaySmall.copy(color = AppTheme.colors.baseContentTitle)
            )
            PixaStarRating(
                variant = StarRatingVariant.Descriptive,
                size = StarRatingSize.Medium,
                value = average.coerceIn(0f, 5f)
            )
            BasicText(
                text = "$totalCount reviews",
                style = captionStyle.copy(color = AppTheme.colors.baseContentCaption)
            )
        }

        if (showDistribution) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
            ) {
                (5 downTo 1).forEach { star ->
                    RatingDistributionBar(
                        star = star,
                        fraction = distribution[star] ?: 0f,
                        barHeight = barHeight,
                        captionStyle = captionStyle
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingDistributionBar(
    star: Int,
    fraction: Float,
    barHeight: Dp,
    captionStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        BasicText(
            text = star.toString(),
            style = captionStyle.copy(
                color = AppTheme.colors.baseContentCaption,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.width(HierarchicalSize.Container.Compact)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
                .clip(RoundedCornerShape(HierarchicalSize.Radius.Full))
                .background(AppTheme.colors.baseSurfaceElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .height(barHeight)
                    .clip(RoundedCornerShape(HierarchicalSize.Radius.Full))
                    .background(AppTheme.colors.brandContentDefault)
            )
        }
    }
}
