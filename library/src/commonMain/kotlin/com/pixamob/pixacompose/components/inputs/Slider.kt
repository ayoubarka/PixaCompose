package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import kotlin.math.roundToInt

// ============================================================================
// Configuration
// ============================================================================

/**
 * Slider variant enum
 */
enum class SliderVariant {
    Filled,    // Solid filled track
    Outlined,  // Outlined style
    Minimal    // Minimal design
}

/**
 * Slider size enum
 */
enum class SliderSize {
    Small,     // Compact slider
    Medium,    // Standard slider
    Large      // Prominent slider
}

/**
 * Configuration for Slider appearance
 */
@Stable
private data class SliderConfig(
    val trackHeight: Dp,
    val thumbSize: Dp,
    val thumbElevation: Dp,
    val labelStyle: TextStyle,
    val valueStyle: TextStyle
)

/**
 * Get configuration for given size
 */
@Composable
private fun SliderSize.config(): SliderConfig {
    val typography = AppTheme.typography
    return when (this) {
        SliderSize.Small -> SliderConfig(
            trackHeight = ComponentSize.SliderTrackMedium,
            thumbSize = IconSize.VerySmall,
            thumbElevation = ShadowSize.Medium,
            labelStyle = typography.labelSmall,
            valueStyle = typography.bodyBold
        )
        SliderSize.Medium -> SliderConfig(
            trackHeight = ComponentSize.SliderTrackLarge,
            thumbSize = IconSize.Small,
            thumbElevation = ShadowSize.Large,
            labelStyle = typography.labelMedium,
            valueStyle = typography.bodyRegular
        )
        SliderSize.Large -> SliderConfig(
            trackHeight = ComponentSize.SliderTrackLarge + Spacing.Micro,
            thumbSize = IconSize.Medium,
            thumbElevation = ShadowSize.Huge,
            labelStyle = typography.labelLarge,
            valueStyle = typography.bodyLight
        )
    }
}

// ============================================================================
// Theme
// ============================================================================

/**
 * Colors for Slider states
 */
@Stable
private data class SliderColors(
    val activeTrack: Color,
    val inactiveTrack: Color,
    val thumb: Color,
    val thumbBorder: Color,
    val label: Color,
    val valueText: Color,
    val disabledActiveTrack: Color,
    val disabledInactiveTrack: Color,
    val disabledThumb: Color
)

/**
 * Get colors for Slider variant
 */
@Composable
private fun SliderVariant.colors(enabled: Boolean): SliderColors {
    val colors = AppTheme.colors

    return when (this) {
        SliderVariant.Filled -> SliderColors(
            activeTrack = colors.brandContentDefault,
            inactiveTrack = colors.baseSurfaceElevated,
            thumb = colors.brandContentDefault,
            thumbBorder = Color.White,
            label = colors.baseContentBody,
            valueText = colors.baseContentTitle,
            disabledActiveTrack = colors.baseContentDisabled,
            disabledInactiveTrack = colors.baseSurfaceDisabled,
            disabledThumb = colors.baseContentDisabled
        )
        SliderVariant.Outlined -> SliderColors(
            activeTrack = colors.brandBorderDefault,
            inactiveTrack = colors.baseBorderSubtle,
            thumb = Color.White,
            thumbBorder = colors.brandBorderDefault,
            label = colors.baseContentBody,
            valueText = colors.baseContentTitle,
            disabledActiveTrack = colors.baseBorderDisabled,
            disabledInactiveTrack = colors.baseBorderDisabled,
            disabledThumb = colors.baseSurfaceDisabled
        )
        SliderVariant.Minimal -> SliderColors(
            activeTrack = colors.baseContentCaption,
            inactiveTrack = colors.baseSurfaceElevated,
            thumb = colors.baseContentTitle,
            thumbBorder = colors.baseBorderDefault,
            label = colors.baseContentBody,
            valueText = colors.baseContentTitle,
            disabledActiveTrack = colors.baseContentDisabled,
            disabledInactiveTrack = colors.baseSurfaceDisabled,
            disabledThumb = colors.baseContentDisabled
        )
    }
}

// ============================================================================
// Base Component
// ============================================================================

/**
 * BaseSlider - Core slider component
 *
 * Continuous or discrete value selection with customizable range.
 *
 * @param value Current slider value
 * @param onValueChange Callback when value changes
 * @param modifier Modifier for the slider
 * @param variant Visual style variant
 * @param size Size preset
 * @param enabled Whether the slider is enabled
 * @param valueRange Range of values (min to max)
 * @param steps Number of discrete steps (0 for continuous)
 * @param label Optional label text
 * @param showValue Whether to show current value
 * @param valueFormatter Custom value formatter
 * @param onValueChangeFinished Callback when user finishes changing value
 */
@Composable
fun PixaSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    variant: SliderVariant = SliderVariant.Filled,
    size: SliderSize = SliderSize.Medium,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    label: String? = null,
    showValue: Boolean = false,
    valueFormatter: (Float) -> String = { it.roundToInt().toString() },
    onValueChangeFinished: (() -> Unit)? = null
) {
    val config = size.config()
    val colors = variant.colors(enabled)

    // Normalize value to 0..1 range
    val normalizedValue = remember(value, valueRange) {
        ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start))
            .coerceIn(0f, 1f)
    }

    // Animation
    var isDragging by remember { mutableStateOf(false) }
    val thumbSize by animateDpAsState(
        targetValue = if (isDragging) config.thumbSize * 1.2f else config.thumbSize,
        animationSpec = AnimationUtils.fastSpring()
    )

    val activeTrackColor by animateColorAsState(
        targetValue = if (enabled) colors.activeTrack else colors.disabledActiveTrack,
        animationSpec = AnimationUtils.smoothSpring()
    )

    val inactiveTrackColor by animateColorAsState(
        targetValue = if (enabled) colors.inactiveTrack else colors.disabledInactiveTrack,
        animationSpec = AnimationUtils.smoothSpring()
    )

    val thumbColor by animateColorAsState(
        targetValue = if (enabled) colors.thumb else colors.disabledThumb,
        animationSpec = AnimationUtils.smoothSpring()
    )

    Column(
        modifier = modifier.semantics {
            if (!enabled) disabled()
            setProgress { targetValue ->
                val newValue = valueRange.start + (targetValue * (valueRange.endInclusive - valueRange.start))
                onValueChange(newValue)
                true
            }
        }
    ) {
        // Label and value
        if (label != null || showValue) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style = config.labelStyle,
                        color = colors.label
                    )
                }
                if (showValue) {
                    Text(
                        text = valueFormatter(value),
                        style = config.valueStyle,
                        color = colors.valueText
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.Small))
        }

        // Slider track and thumb
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(config.thumbSize),
            contentAlignment = Alignment.CenterStart
        ) {
            val trackWidth = constraints.maxWidth.toFloat()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(config.thumbSize)
                    .pointerInput(enabled) {
                        if (!enabled) return@pointerInput

                        fun updateValue(x: Float) {
                            val coercedX = x.coerceIn(0f, trackWidth)
                            var newNormalizedValue = coercedX / trackWidth

                            // Handle steps
                            if (steps > 0) {
                                val stepSize = 1f / (steps + 1)
                                newNormalizedValue = (newNormalizedValue / stepSize).roundToInt() * stepSize
                            }

                            val newValue = valueRange.start + (newNormalizedValue * (valueRange.endInclusive - valueRange.start))
                            onValueChange(newValue.coerceIn(valueRange))
                        }

                        detectTapGestures { offset ->
                            updateValue(offset.x)
                            onValueChangeFinished?.invoke()
                        }
                    }
                    .pointerInput(enabled) {
                        if (!enabled) return@pointerInput

                        detectDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                onValueChangeFinished?.invoke()
                            },
                            onDragCancel = { isDragging = false }
                        ) { change, _ ->
                            change.consume()
                            val coercedX = change.position.x.coerceIn(0f, trackWidth)
                            var newNormalizedValue = coercedX / trackWidth

                            // Handle steps
                            if (steps > 0) {
                                val stepSize = 1f / (steps + 1)
                                newNormalizedValue = (newNormalizedValue / stepSize).roundToInt() * stepSize
                            }

                            val newValue = valueRange.start + (newNormalizedValue * (valueRange.endInclusive - valueRange.start))
                            onValueChange(newValue.coerceIn(valueRange))
                        }
                    }
            ) {
                // Track background (inactive)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(config.trackHeight)
                        .clip(CircleShape)
                        .background(inactiveTrackColor)
                )

                // Track progress (active)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(normalizedValue)
                        .height(config.trackHeight)
                        .clip(CircleShape)
                        .background(activeTrackColor)
                )

                // Step indicators
                if (steps > 0 && enabled) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val canvasWidth = this.size.width
                        val canvasHeight = this.size.height
                        val stepSize = canvasWidth / (steps + 1)
                        val stepRadius = config.trackHeight.toPx() / 2

                        for (i in 1..steps) {
                            val x = stepSize * i
                            val isActive = (i.toFloat() / (steps + 1)) <= normalizedValue

                            drawCircle(
                                color = if (isActive) activeTrackColor else inactiveTrackColor,
                                radius = stepRadius,
                                center = Offset(x, canvasHeight / 2)
                            )
                        }
                    }
                }

                // Thumb
                Box(
                    modifier = Modifier
                        .offset(x = this@BoxWithConstraints.maxWidth * normalizedValue - thumbSize * normalizedValue)
                        .size(thumbSize)
                        .shadow(
                            elevation = if (enabled) config.thumbElevation else 0.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(thumbColor)
                        .then(
                            if (variant == SliderVariant.Outlined) {
                                Modifier.padding(Spacing.Micro).background(Color.White, CircleShape)
                            } else Modifier
                        )
                )
            }
        }
    }
}

// ============================================================================
// Convenience Variants
// ============================================================================

/**
 * FilledSlider - Filled style slider
 */
@Composable
fun FilledSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    size: SliderSize = SliderSize.Medium,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    label: String? = null,
    showValue: Boolean = true,
    valueFormatter: (Float) -> String = { it.roundToInt().toString() },
    onValueChangeFinished: (() -> Unit)? = null
) {
    PixaSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = SliderVariant.Filled,
        size = size,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        label = label,
        showValue = showValue,
        valueFormatter = valueFormatter,
        onValueChangeFinished = onValueChangeFinished
    )
}

/**
 * OutlinedSlider - Outlined style slider
 */
@Composable
fun OutlinedSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    size: SliderSize = SliderSize.Medium,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    label: String? = null,
    showValue: Boolean = true,
    valueFormatter: (Float) -> String = { it.roundToInt().toString() },
    onValueChangeFinished: (() -> Unit)? = null
) {
    PixaSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = SliderVariant.Outlined,
        size = size,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        label = label,
        showValue = showValue,
        valueFormatter = valueFormatter,
        onValueChangeFinished = onValueChangeFinished
    )
}

/**
 * MinimalSlider - Minimal design slider
 */
@Composable
fun MinimalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    size: SliderSize = SliderSize.Medium,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    label: String? = null,
    showValue: Boolean = false,
    valueFormatter: (Float) -> String = { it.roundToInt().toString() },
    onValueChangeFinished: (() -> Unit)? = null
) {
    PixaSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = SliderVariant.Minimal,
        size = size,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        label = label,
        showValue = showValue,
        valueFormatter = valueFormatter,
        onValueChangeFinished = onValueChangeFinished
    )
}

// ============================================================================
// Specialized Variants
// ============================================================================

/**
 * VolumeSlider - Slider for volume control (0-100)
 */
@Composable
fun VolumeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    size: SliderSize = SliderSize.Medium,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null
) {
    FilledSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        size = size,
        enabled = enabled,
        valueRange = 0f..100f,
        steps = 0,
        label = "Volume",
        showValue = true,
        valueFormatter = { "${it.roundToInt()}%" },
        onValueChangeFinished = onValueChangeFinished
    )
}

/**
 * RatingSlider - Discrete slider for ratings (1-5)
 */
@Composable
fun RatingSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    size: SliderSize = SliderSize.Medium,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null
) {
    FilledSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        size = size,
        enabled = enabled,
        valueRange = 1f..5f,
        steps = 3, // Creates 5 discrete positions
        label = "Rating",
        showValue = true,
        valueFormatter = { "${it.roundToInt()}/5" },
        onValueChangeFinished = onValueChangeFinished
    )
}
