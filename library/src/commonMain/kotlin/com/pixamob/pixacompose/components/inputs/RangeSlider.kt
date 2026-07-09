package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import kotlin.math.roundToInt

@Immutable
@Stable
data class RangeSliderColors(
    val activeTrack: Color,
    val inactiveTrack: Color,
    val thumb: Color,
    val thumbBorder: Color,
    val valueText: Color,
    val disabledActiveTrack: Color,
    val disabledInactiveTrack: Color,
    val disabledThumb: Color
)

@Immutable
@Stable
data class RangeSliderSizeConfig(
    val trackHeight: Dp,
    val thumbSize: Dp,
    val thumbElevation: Dp,
    val labelStyle: TextStyle,
    val valueStyle: TextStyle
)

@Stable
data class RangeSliderConfig(
    val thumbScaleOnDrag: Float = 1.2f,
    val thumbBorderWidth: Dp = 2.5.dp
)

@Composable
private fun getRangeSliderTheme(colors: ColorPalette): RangeSliderColors {
    return RangeSliderColors(
        activeTrack = colors.brandContentDefault,
        inactiveTrack = colors.baseSurfaceElevated,
        thumb = colors.brandContentDefault,
        thumbBorder = Color.White,
        valueText = colors.baseContentTitle,
        disabledActiveTrack = colors.baseContentDisabled,
        disabledInactiveTrack = colors.baseSurfaceDisabled,
        disabledThumb = colors.baseContentDisabled
    )
}

@Composable
private fun getRangeSliderSizeConfig(size: SizeVariant): RangeSliderSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.Small -> RangeSliderSizeConfig(
            trackHeight = HierarchicalSize.SliderTrack.Medium,
            thumbSize = HierarchicalSize.Icon.Compact,
            thumbElevation = HierarchicalSize.Shadow.Medium,
            labelStyle = typography.labelSmall,
            valueStyle = typography.bodyBold
        )
        SizeVariant.Medium -> RangeSliderSizeConfig(
            trackHeight = HierarchicalSize.SliderTrack.Large,
            thumbSize = HierarchicalSize.Icon.Small,
            thumbElevation = HierarchicalSize.Shadow.Large,
            labelStyle = typography.labelMedium,
            valueStyle = typography.bodyRegular
        )
        SizeVariant.Large -> RangeSliderSizeConfig(
            trackHeight = HierarchicalSize.SliderTrack.Large + HierarchicalSize.Spacing.Nano,
            thumbSize = HierarchicalSize.Icon.Medium,
            thumbElevation = HierarchicalSize.Shadow.Huge,
            labelStyle = typography.labelLarge,
            valueStyle = typography.bodyLight
        )
        else -> RangeSliderSizeConfig(
            trackHeight = HierarchicalSize.SliderTrack.Large,
            thumbSize = HierarchicalSize.Icon.Small,
            thumbElevation = HierarchicalSize.Shadow.Large,
            labelStyle = typography.labelMedium,
            valueStyle = typography.bodyRegular
        )
    }
}

/**
 * PixaRangeSlider - Two-thumb range slider for min-max value selection.
 *
 * Allows selecting a range between a lower and upper bound with two draggable thumbs.
 * Supports discrete steps and customizable colors.
 *
 * @param lowerValue Current lower thumb value
 * @param upperValue Current upper thumb value
 * @param onValueChange Callback with (lower, upper) when values change
 * @param modifier Modifier for the slider
 * @param size Size preset (Small, Medium, Large)
 * @param enabled Whether the slider is enabled
 * @param valueRange Range of values (min to max)
 * @param steps Number of discrete steps (0 for continuous)
 * @param colors Custom colors (null = use theme)
 * @param config Slider behavior configuration
 * @param showValue Whether to display lower/upper value text
 * @param valueFormatter Custom value formatter
 * @param onValueChangeFinished Callback when user finishes dragging
 */
@Composable
fun PixaRangeSlider(
    lowerValue: Float,
    upperValue: Float,
    onValueChange: (Float, Float) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    colors: RangeSliderColors? = null,
    config: RangeSliderConfig = RangeSliderConfig(),
    showValue: Boolean = false,
    valueFormatter: (Float) -> String = { it.roundToInt().toString() },
    onValueChangeFinished: (() -> Unit)? = null
) {
    val themeColors = getRangeSliderTheme(AppTheme.colors)
    val finalColors = colors ?: themeColors
    val sizeConfig = getRangeSliderSizeConfig(size)

    val rangeStart = valueRange.start
    val rangeEnd = valueRange.endInclusive
    val rangeWidth = rangeEnd - rangeStart

    val normalizedLower = remember(lowerValue, valueRange) {
        ((lowerValue - rangeStart) / rangeWidth).coerceIn(0f, 1f)
    }
    val normalizedUpper = remember(upperValue, valueRange) {
        ((upperValue - rangeStart) / rangeWidth).coerceIn(0f, 1f)
    }

    val animatedLowerFraction by animateFloatAsState(
        targetValue = normalizedLower,
        animationSpec = AnimationUtils.fastSpring,
        label = "range_lower_fraction"
    )

    val animatedUpperFraction by animateFloatAsState(
        targetValue = normalizedUpper,
        animationSpec = AnimationUtils.fastSpring,
        label = "range_upper_fraction"
    )

    var isDraggingLower by remember { mutableStateOf(false) }
    var isDraggingUpper by remember { mutableStateOf(false) }
    val isDragging = isDraggingLower || isDraggingUpper

    val thumbSize by animateDpAsState(
        targetValue = if (isDragging) sizeConfig.thumbSize * config.thumbScaleOnDrag else sizeConfig.thumbSize,
        animationSpec = AnimationUtils.fastSpringSpec()
    )

    val activeTrackColor by animateColorAsState(
        targetValue = if (enabled) finalColors.activeTrack else finalColors.disabledActiveTrack,
        animationSpec = AnimationUtils.smoothSpring()
    )

    val inactiveTrackColor by animateColorAsState(
        targetValue = if (enabled) finalColors.inactiveTrack else finalColors.disabledInactiveTrack,
        animationSpec = AnimationUtils.smoothSpring()
    )

    val thumbColor by animateColorAsState(
        targetValue = if (enabled) finalColors.thumb else finalColors.disabledThumb,
        animationSpec = AnimationUtils.smoothSpring()
    )

    Column(
        modifier = modifier.semantics {
            if (!enabled) disabled()
        }
    ) {
        if (showValue) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valueFormatter(lowerValue),
                    style = sizeConfig.valueStyle,
                    color = finalColors.valueText
                )
                Text(
                    text = valueFormatter(upperValue),
                    style = sizeConfig.valueStyle,
                    color = finalColors.valueText
                )
            }
            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(sizeConfig.thumbSize)) {
            val trackWidth = constraints.maxWidth.toFloat()
            val maxWidthDp = this@BoxWithConstraints.maxWidth

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sizeConfig.thumbSize)
                    .pointerInput(enabled, lowerValue, upperValue) {
                        if (!enabled) return@pointerInput
                        detectTapGestures { offset ->
                            val xRatio = (offset.x / trackWidth).coerceIn(0f, 1f)
                            val lowerDist = kotlin.math.abs(xRatio - animatedLowerFraction)
                            val upperDist = kotlin.math.abs(xRatio - animatedUpperFraction)
                            val newValue = rangeStart + (xRatio * rangeWidth)

                            if (lowerDist <= upperDist) {
                                val clamped = newValue.coerceIn(valueRange.start, upperValue)
                                onValueChange(clamped, upperValue)
                            } else {
                                val clamped = newValue.coerceIn(lowerValue, valueRange.endInclusive)
                                onValueChange(lowerValue, clamped)
                            }
                            onValueChangeFinished?.invoke()
                        }
                    }
                    .pointerInput(enabled, lowerValue, upperValue) {
                        if (!enabled) return@pointerInput
                        var draggingLower = false
                        detectDragGestures(
                            onDragStart = { offset ->
                                val xRatio = (offset.x / trackWidth).coerceIn(0f, 1f)
                                val lowerDist = kotlin.math.abs(xRatio - animatedLowerFraction)
                                val upperDist = kotlin.math.abs(xRatio - animatedUpperFraction)
                                draggingLower = lowerDist <= upperDist
                                if (draggingLower) isDraggingLower = true else isDraggingUpper = true
                            },
                            onDragEnd = {
                                isDraggingLower = false
                                isDraggingUpper = false
                                onValueChangeFinished?.invoke()
                            },
                            onDragCancel = {
                                isDraggingLower = false
                                isDraggingUpper = false
                            }
                        ) { change, _ ->
                            change.consume()
                            val xRatio = (change.position.x / trackWidth).coerceIn(0f, 1f)
                            var newValue = rangeStart + (xRatio * rangeWidth)
                            if (steps > 0) {
                                val stepSize = rangeWidth / (steps + 1)
                                newValue =
                                    (kotlin.math.round((newValue - rangeStart) / stepSize) * stepSize + rangeStart)
                                        .coerceIn(valueRange)
                            }
                            if (draggingLower) {
                                val clamped = newValue.coerceIn(valueRange.start, upperValue)
                                onValueChange(clamped, upperValue)
                            } else {
                                val clamped = newValue.coerceIn(lowerValue, valueRange.endInclusive)
                                onValueChange(lowerValue, clamped)
                            }
                        }
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                // Inactive track (full width)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sizeConfig.trackHeight)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(inactiveTrackColor)
                )

                // Active track (between thumbs)
                Box(
                    modifier = Modifier
                        .offset(x = maxWidthDp * animatedLowerFraction)
                        .size(
                            width = maxWidthDp * (animatedUpperFraction - animatedLowerFraction),
                            height = sizeConfig.trackHeight
                        )
                        .align(Alignment.CenterStart)
                        .clip(CircleShape)
                        .background(activeTrackColor)
                )

                // Lower thumb
                Box(
                    modifier = Modifier
                        .offset(x = (maxWidthDp - thumbSize) * animatedLowerFraction)
                        .size(thumbSize)
                        .shadow(
                            elevation = if (enabled) sizeConfig.thumbElevation else 0.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(thumbColor)
                        .border(
                            width = config.thumbBorderWidth,
                            color = if (thumbColor.luminance() > 0.5f) Color.Black.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                )

                // Upper thumb
                Box(
                    modifier = Modifier
                        .offset(x = (maxWidthDp - thumbSize) * animatedUpperFraction)
                        .size(thumbSize)
                        .shadow(
                            elevation = if (enabled) sizeConfig.thumbElevation else 0.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(thumbColor)
                        .border(
                            width = config.thumbBorderWidth,
                            color = if (thumbColor.luminance() > 0.5f) Color.Black.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
