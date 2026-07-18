package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * PixaRangeSlider — Dual-knob range selection control.
 *
 * ### Anatomy
 * Track (inactive + active-between-thumbs fill) + two thumbs,
 * plus optional lower/upper value readout.
 *
 * ### States
 * Enabled, disabled, per-thumb dragging/keyboard-focused (value bubble).
 *
 * ### Sizing
 * [SizeVariant]-driven via [HierarchicalSize], matching [PixaSlider].
 *
 * ### Interaction
 * Drag either thumb, tap the track to move the closest thumb, or use keyboard:
 * Tab/Shift+Tab moves focus between thumbs, arrows step the focused thumb.
 *
 * @param valueRange Range value (start to end)
 * @param onValueChange Callback when range changes
 * @param modifier Modifier
 * @param size Size preset
 * @param enabled Whether enabled
 * @param steps Number of discrete steps (0 = continuous)
 * @param valueFormat Format function for value readout
 * @param colors Custom colors
 */

@Immutable
@Stable
data class RangeSliderColors(
    val activeTrack: Color,
    val inactiveTrack: Color,
    val thumb: Color,
    val thumbBorder: Color,
    val valueText: Color,
    val valueBubbleSurface: Color,
    val valueBubbleContent: Color,
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
        thumbBorder = colors.baseSurfaceDefault,
        valueText = colors.baseContentTitle,
        // Mirrors PixaTooltip's high-contrast surface (overlay/Tooltip.kt) so
        // this floating micro-label reads the same as the rest of the library.
        valueBubbleSurface = colors.baseContentTitle,
        valueBubbleContent = colors.baseSurfaceDefault,
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
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

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

    val lowerInteractionSource = remember { MutableInteractionSource() }
    val upperInteractionSource = remember { MutableInteractionSource() }
    val isLowerFocused by lowerInteractionSource.collectIsFocusedAsState()
    val isUpperFocused by upperInteractionSource.collectIsFocusedAsState()
    val isLowerActive = isDraggingLower || isLowerFocused
    val isUpperActive = isDraggingUpper || isUpperFocused

    val thumbSize by animateDpAsState(
        targetValue = if (isDraggingLower || isDraggingUpper) {
            sizeConfig.thumbSize * config.thumbScaleOnDrag
        } else {
            sizeConfig.thumbSize
        },
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
                BasicText(text = valueFormatter(lowerValue), style = sizeConfig.valueStyle.copy(color = finalColors.valueText))
                BasicText(text = valueFormatter(upperValue), style = sizeConfig.valueStyle.copy(color = finalColors.valueText))
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
                    .pointerInput(enabled, lowerValue, upperValue, isRtl) {
                        if (!enabled) return@pointerInput
                        detectTapGestures { offset ->
                            val physicalX = if (isRtl) trackWidth - offset.x else offset.x
                            val xRatio = (physicalX / trackWidth).coerceIn(0f, 1f)
                            val lowerDist = abs(xRatio - animatedLowerFraction)
                            val upperDist = abs(xRatio - animatedUpperFraction)
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
                    .pointerInput(enabled, lowerValue, upperValue, isRtl) {
                        if (!enabled) return@pointerInput
                        var draggingLower = false
                        detectDragGestures(
                            onDragStart = { offset ->
                                val physicalX = if (isRtl) trackWidth - offset.x else offset.x
                                val xRatio = (physicalX / trackWidth).coerceIn(0f, 1f)
                                val lowerDist = abs(xRatio - animatedLowerFraction)
                                val upperDist = abs(xRatio - animatedUpperFraction)
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
                            val physicalX = if (isRtl) trackWidth - change.position.x else change.position.x
                            val xRatio = (physicalX / trackWidth).coerceIn(0f, 1f)
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

                // Lower thumb — independent focus target so Tab/Shift+Tab can
                // select it distinctly from the upper thumb.
                Box(
                    modifier = Modifier
                        .offset(x = (maxWidthDp - thumbSize) * animatedLowerFraction)
                        .size(thumbSize)
                        .focusable(enabled = enabled, interactionSource = lowerInteractionSource)
                        .onKeyEvent { event ->
                            handleRangeSliderKeyEvent(
                                event = event,
                                enabled = enabled,
                                isRtl = isRtl,
                                isLower = true,
                                lowerValue = lowerValue,
                                upperValue = upperValue,
                                valueRange = valueRange,
                                steps = steps,
                                onValueChange = onValueChange,
                                onValueChangeFinished = onValueChangeFinished
                            )
                        }
                        .semantics {
                            progressBarRangeInfo = ProgressBarRangeInfo(lowerValue, valueRange.start..upperValue, steps)
                        }
                        .shadow(
                            elevation = if (enabled) sizeConfig.thumbElevation else 0.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(thumbColor)
                        .border(config.thumbBorderWidth, finalColors.thumbBorder, CircleShape)
                )

                // Upper thumb
                Box(
                    modifier = Modifier
                        .offset(x = (maxWidthDp - thumbSize) * animatedUpperFraction)
                        .size(thumbSize)
                        .focusable(enabled = enabled, interactionSource = upperInteractionSource)
                        .onKeyEvent { event ->
                            handleRangeSliderKeyEvent(
                                event = event,
                                enabled = enabled,
                                isRtl = isRtl,
                                isLower = false,
                                lowerValue = lowerValue,
                                upperValue = upperValue,
                                valueRange = valueRange,
                                steps = steps,
                                onValueChange = onValueChange,
                                onValueChangeFinished = onValueChangeFinished
                            )
                        }
                        .semantics {
                            progressBarRangeInfo = ProgressBarRangeInfo(upperValue, lowerValue..valueRange.endInclusive, steps)
                        }
                        .shadow(
                            elevation = if (enabled) sizeConfig.thumbElevation else 0.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(thumbColor)
                        .border(config.thumbBorderWidth, finalColors.thumbBorder, CircleShape)
                )

                ValueBubble(
                    visible = isLowerActive && enabled,
                    text = valueFormatter(lowerValue),
                    normalizedValue = animatedLowerFraction,
                    thumbSize = sizeConfig.thumbSize,
                    valueStyle = sizeConfig.valueStyle,
                    surfaceColor = finalColors.valueBubbleSurface,
                    contentColor = finalColors.valueBubbleContent,
                    maxWidth = maxWidthDp
                )

                ValueBubble(
                    visible = isUpperActive && enabled,
                    text = valueFormatter(upperValue),
                    normalizedValue = animatedUpperFraction,
                    thumbSize = sizeConfig.thumbSize,
                    valueStyle = sizeConfig.valueStyle,
                    surfaceColor = finalColors.valueBubbleSurface,
                    contentColor = finalColors.valueBubbleContent,
                    maxWidth = maxWidthDp
                )
            }
        }
    }
}

/**
 * Keyboard stepping for the focused thumb, clamped against the other thumb's
 * current value so the two knobs can never cross. Mirrors [PixaSlider]'s key
 * mapping — see that component's `handleSliderKeyEvent` for the rationale.
 */
private fun handleRangeSliderKeyEvent(
    event: KeyEvent,
    enabled: Boolean,
    isRtl: Boolean,
    isLower: Boolean,
    lowerValue: Float,
    upperValue: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float, Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?
): Boolean {
    if (!enabled || event.type != KeyEventType.KeyDown) return false

    val rangeWidth = valueRange.endInclusive - valueRange.start
    val step = if (steps > 0) rangeWidth / (steps + 1) else rangeWidth / 100f
    val pageStep = rangeWidth / 10f
    val current = if (isLower) lowerValue else upperValue
    val minBound = if (isLower) valueRange.start else lowerValue
    val maxBound = if (isLower) upperValue else valueRange.endInclusive

    fun apply(newValue: Float): Boolean {
        val clamped = newValue.coerceIn(minBound, maxBound)
        if (isLower) onValueChange(clamped, upperValue) else onValueChange(lowerValue, clamped)
        onValueChangeFinished?.invoke()
        return true
    }

    if (event.key == Key.MoveHome) return apply(minBound)
    if (event.key == Key.MoveEnd) return apply(maxBound)

    val delta = when (event.key) {
        Key.DirectionRight -> if (isRtl) -step else step
        Key.DirectionLeft -> if (isRtl) step else -step
        Key.DirectionUp -> step
        Key.DirectionDown -> -step
        Key.PageUp -> pageStep
        Key.PageDown -> -pageStep
        else -> return false
    }

    return apply(current + delta)
}

/**
 * Same floating value readout as [PixaSlider]'s internal `ValueBubble` — kept
 * as a private per-file copy rather than a shared cross-family export, since
 * it's a tiny (~15 line) layout and sharing it would mean either promoting it
 * to a public API surface or reaching into `Slider.kt`'s private internals,
 * both worse than the duplication.
 */
@Composable
private fun ValueBubble(
    visible: Boolean,
    text: String,
    normalizedValue: Float,
    thumbSize: Dp,
    valueStyle: TextStyle,
    surfaceColor: Color,
    contentColor: Color,
    maxWidth: Dp
) {
    Box(
        modifier = Modifier
            .offset(
                x = (maxWidth - thumbSize) * normalizedValue,
                y = -(thumbSize + HierarchicalSize.Spacing.Small)
            )
            .size(thumbSize),
        contentAlignment = Alignment.Center
    ) {
        AnimationUtils.AnimatedVisibilityStandard(
            visible = visible,
            enter = AnimationUtils.scaleInTransition,
            exit = AnimationUtils.scaleOutTransition
        ) {
            Box(
                modifier = Modifier
                    .background(surfaceColor, AppTheme.shapes.pill)
                    .padding(horizontal = HierarchicalSize.Padding.Small, vertical = HierarchicalSize.Padding.Nano)
            ) {
                BasicText(text = text, style = valueStyle.copy(color = contentColor))
            }
        }
    }
}
