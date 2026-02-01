package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.ComponentSize
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.IconSize
import com.pixamob.pixacompose.theme.ShadowSize
import com.pixamob.pixacompose.theme.Spacing
import com.pixamob.pixacompose.utils.AnimationUtils
import kotlin.math.roundToInt

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class SliderVariant {
    Filled,
    Outlined,
    Minimal
}

enum class SliderSize {
    Small,
    Medium,
    Large
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class SliderColors(
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

@Immutable
@Stable
data class SliderSizeConfig(
    val trackHeight: Dp,
    val thumbSize: Dp,
    val thumbElevation: Dp,
    val labelStyle: TextStyle,
    val valueStyle: TextStyle
)

@Stable
data class SliderConfig(
    val showStepIndicators: Boolean = true,
    val thumbScaleOnDrag: Float = 1.2f,
    val thumbBorderWidth: Dp = 2.5.dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getSliderTheme(variant: SliderVariant, colors: ColorPalette): SliderColors {
    return when (variant) {
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

@Composable
private fun getSliderSizeConfig(size: SliderSize): SliderSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SliderSize.Small -> SliderSizeConfig(
            trackHeight = ComponentSize.SliderTrackMedium,
            thumbSize = IconSize.VerySmall,
            thumbElevation = ShadowSize.Medium,
            labelStyle = typography.labelSmall,
            valueStyle = typography.bodyBold
        )
        SliderSize.Medium -> SliderSizeConfig(
            trackHeight = ComponentSize.SliderTrackLarge,
            thumbSize = IconSize.Small,
            thumbElevation = ShadowSize.Large,
            labelStyle = typography.labelMedium,
            valueStyle = typography.bodyRegular
        )
        SliderSize.Large -> SliderSizeConfig(
            trackHeight = ComponentSize.SliderTrackLarge + Spacing.Micro,
            thumbSize = IconSize.Medium,
            thumbElevation = HierarchicalSize.Shadow.Huge,
            labelStyle = typography.labelLarge,
            valueStyle = typography.bodyLight
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaSlider - Continuous or discrete value selection component
 *
 * A flexible slider with customizable range, steps, and visual styles.
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic slider
 * var value by remember { mutableStateOf(0.5f) }
 * PixaSlider(
 *     value = value,
 *     onValueChange = { value = it }
 * )
 *
 * // Slider with range and steps
 * var volume by remember { mutableStateOf(50f) }
 * PixaSlider(
 *     value = volume,
 *     onValueChange = { volume = it },
 *     valueRange = 0f..100f,
 *     steps = 9,
 *     showValue = true,
 *     valueFormatter = { "${it.toInt()}%" }
 * )
 *
 * // Outlined variant
 * PixaSlider(
 *     value = brightness,
 *     onValueChange = { brightness = it },
 *     variant = SliderVariant.Outlined,
 *     size = SliderSize.Large
 * )
 *
 * // Gradient slider for color selection
 * PixaSlider(
 *     value = hue,
 *     onValueChange = { hue = it },
 *     valueRange = 0f..360f,
 *     gradientBrush = Brush.horizontalGradient(
 *         colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red)
 *     )
 * )
 *
 * // Custom colors
 * PixaSlider(
 *     value = progress,
 *     onValueChange = { progress = it },
 *     colors = SliderColors(
 *         activeTrack = Color.Green,
 *         inactiveTrack = Color.Gray,
 *         thumb = Color.Green,
 *         // ... other colors
 *     )
 * )
 * ```
 *
 * @param value Current slider value
 * @param onValueChange Callback when value changes
 * @param modifier Modifier for the slider
 * @param variant Visual style variant (Filled, Outlined, Minimal)
 * @param size Size preset (Small, Medium, Large)
 * @param enabled Whether the slider is enabled
 * @param valueRange Range of values (min to max)
 * @param steps Number of discrete steps (0 for continuous)
 * @param colors Custom colors (null = use theme)
 * @param config Slider behavior configuration
 * @param showValue Whether to show current value
 * @param valueFormatter Custom value formatter
 * @param onValueChangeFinished Callback when user finishes changing value
 * @param gradientBrush Optional gradient for track background
 * @param thumbColorOverride Optional custom thumb color
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
    colors: SliderColors? = null,
    config: SliderConfig = SliderConfig(),
    showValue: Boolean = false,
    valueFormatter: (Float) -> String = { it.roundToInt().toString() },
    onValueChangeFinished: (() -> Unit)? = null,
    gradientBrush: Brush? = null,
    thumbColorOverride: Color? = null
) {
    val themeColors = getSliderTheme(variant, AppTheme.colors)
    val finalColors = colors ?: themeColors
    val sizeConfig = getSliderSizeConfig(size)

    val normalizedValue = remember(value, valueRange) {
        ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    }

    var isDragging by remember { mutableStateOf(false) }

    val thumbSize by animateDpAsState(
        targetValue = if (isDragging) sizeConfig.thumbSize * config.thumbScaleOnDrag else sizeConfig.thumbSize,
        animationSpec = AnimationUtils.fastSpring()
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
            setProgress { targetValue ->
                val newValue = valueRange.start + (targetValue * (valueRange.endInclusive - valueRange.start))
                onValueChange(newValue)
                true
            }
        }
    ) {
        if (showValue) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valueFormatter(value),
                    style = sizeConfig.valueStyle,
                    color = finalColors.valueText
                )
            }
            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(sizeConfig.thumbSize)) {
            val trackWidth = constraints.maxWidth.toFloat()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sizeConfig.thumbSize)
                    .pointerInput(enabled) {
                        if (!enabled) return@pointerInput
                        detectTapGestures { offset ->
                            val newNormalizedValue = calculateNormalizedValue(offset.x, trackWidth, steps)
                            val newValue = valueRange.start + (newNormalizedValue * (valueRange.endInclusive - valueRange.start))
                            onValueChange(newValue.coerceIn(valueRange))
                            onValueChangeFinished?.invoke()
                        }
                    }
                    .pointerInput(enabled) {
                        if (!enabled) return@pointerInput
                        detectDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = { isDragging = false; onValueChangeFinished?.invoke() },
                            onDragCancel = { isDragging = false }
                        ) { change, _ ->
                            change.consume()
                            val newNormalizedValue = calculateNormalizedValue(change.position.x, trackWidth, steps)
                            val newValue = valueRange.start + (newNormalizedValue * (valueRange.endInclusive - valueRange.start))
                            onValueChange(newValue.coerceIn(valueRange))
                        }
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                TrackContent(
                    sizeConfig = sizeConfig,
                    normalizedValue = normalizedValue,
                    activeTrackColor = activeTrackColor,
                    inactiveTrackColor = inactiveTrackColor,
                    gradientBrush = gradientBrush,
                    steps = steps,
                    enabled = enabled,
                    showStepIndicators = config.showStepIndicators
                )

                ThumbContent(
                    thumbSize = thumbSize,
                    thumbColor = thumbColorOverride ?: thumbColor,
                    sizeConfig = sizeConfig,
                    enabled = enabled,
                    normalizedValue = normalizedValue,
                    variant = variant,
                    config = config,
                    maxWidth = this@BoxWithConstraints.maxWidth
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

private fun calculateNormalizedValue(x: Float, trackWidth: Float, steps: Int): Float {
    val coercedX = x.coerceIn(0f, trackWidth)
    var newNormalizedValue = coercedX / trackWidth
    if (steps > 0) {
        val stepSize = 1f / (steps + 1)
        newNormalizedValue = (newNormalizedValue / stepSize).roundToInt() * stepSize
    }
    return newNormalizedValue
}

@Composable
private fun TrackContent(
    sizeConfig: SliderSizeConfig,
    normalizedValue: Float,
    activeTrackColor: Color,
    inactiveTrackColor: Color,
    gradientBrush: Brush?,
    steps: Int,
    enabled: Boolean,
    showStepIndicators: Boolean
) {
    Box(modifier = Modifier.fillMaxWidth().height(sizeConfig.trackHeight)) {
        if (gradientBrush != null) {
            Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(gradientBrush))
        } else {
            Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(inactiveTrackColor))
            Box(modifier = Modifier.fillMaxWidth(normalizedValue).fillMaxHeight().clip(CircleShape).background(activeTrackColor))
        }
    }

    if (steps > 0 && enabled && showStepIndicators) {
        Canvas(modifier = Modifier.fillMaxWidth().height(sizeConfig.trackHeight)) {
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val stepSize = canvasWidth / (steps + 1)
            val stepRadius = sizeConfig.trackHeight.toPx() / 2

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
}

@Composable
private fun ThumbContent(
    thumbSize: Dp,
    thumbColor: Color,
    sizeConfig: SliderSizeConfig,
    enabled: Boolean,
    normalizedValue: Float,
    variant: SliderVariant,
    config: SliderConfig,
    maxWidth: Dp
) {
    val borderColor = if (thumbColor.luminance() > 0.5f) Color.Black.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.9f)

    Box(
        modifier = Modifier
            .offset(x = (maxWidth - thumbSize) * normalizedValue)
            .size(thumbSize)
            .shadow(elevation = if (enabled) sizeConfig.thumbElevation else 0.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(thumbColor)
            .border(config.thumbBorderWidth, borderColor, CircleShape)
            .then(
                if (variant == SliderVariant.Outlined) {
                    Modifier.border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                } else Modifier
            )
    )
}
