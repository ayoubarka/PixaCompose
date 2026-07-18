package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import kotlin.math.roundToInt

/**
 * PixaSlider — Single-value range selection control.
 *
 * ### Anatomy
 * Track (inactive + active fill) + one thumb, plus optional min/max icons,
 * min/max labels, a value readout bubble, and discrete step ticks.
 *
 * ### Variants
 * [SliderVariant]: Filled/Outlined/Ghost — visual treatment of track and thumb.
 * The continuous vs discrete axis is expressed via [steps].
 *
 * ### States
 * Enabled, disabled, dragging (thumb scales up), focused/dragging (value bubble).
 *
 * ### Sizing
 * [size] via [HierarchicalSize] (track height, thumb size, elevation).
 *
 * ### Interaction
 * Drag thumb, tap track to jump, keyboard: arrows step by increment,
 * Page Up/Down jump 10%, Home/End snap to bounds.
 * RTL pointer math is mirrored automatically.
 *
 * @param value Current value
 * @param onValueChange Callback when value changes
 * @param modifier Modifier
 * @param variant Visual style
 * @param size Size preset
 * @param enabled Whether enabled
 * @param steps Number of discrete steps (0 = continuous)
 * @param valueRange Value range
 * @param colors Custom colors
 * @param gradientBrush Gradient for active track
 * @param labelFormat Format function for value readout
 * @param minIcon Optional min-end icon
 * @param maxIcon Optional max-end icon
 * @param minLabel Optional min-end label
 * @param maxLabel Optional max-end label
 */

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class SliderVariant {
    Filled,
    Outlined,
    Ghost
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
    val valueBubbleSurface: Color,
    val valueBubbleContent: Color,
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
    // Value bubble mirrors PixaTooltip's high-contrast surface (see
    // overlay/Tooltip.kt) so every floating micro-label in the library reads
    // the same regardless of the host component's own variant palette.
    val valueBubbleSurface = colors.baseContentTitle
    val valueBubbleContent = colors.baseSurfaceDefault

    return when (variant) {
        SliderVariant.Filled -> SliderColors(
            activeTrack = colors.brandContentDefault,
            inactiveTrack = colors.baseSurfaceElevated,
            thumb = colors.brandContentDefault,
            thumbBorder = colors.baseSurfaceDefault,
            label = colors.baseContentBody,
            valueText = colors.baseContentTitle,
            valueBubbleSurface = valueBubbleSurface,
            valueBubbleContent = valueBubbleContent,
            disabledActiveTrack = colors.baseContentDisabled,
            disabledInactiveTrack = colors.baseSurfaceDisabled,
            disabledThumb = colors.baseContentDisabled
        )
        SliderVariant.Outlined -> SliderColors(
            activeTrack = colors.brandBorderDefault,
            inactiveTrack = colors.baseBorderSubtle,
            thumb = colors.baseSurfaceDefault,
            thumbBorder = colors.brandBorderDefault,
            label = colors.baseContentBody,
            valueText = colors.baseContentTitle,
            valueBubbleSurface = valueBubbleSurface,
            valueBubbleContent = valueBubbleContent,
            disabledActiveTrack = colors.baseBorderDisabled,
            disabledInactiveTrack = colors.baseBorderDisabled,
            disabledThumb = colors.baseSurfaceDisabled
        )
        SliderVariant.Ghost -> SliderColors(
            activeTrack = colors.baseContentCaption,
            inactiveTrack = colors.baseSurfaceElevated,
            thumb = colors.baseContentTitle,
            thumbBorder = colors.baseBorderDefault,
            label = colors.baseContentBody,
            valueText = colors.baseContentTitle,
            valueBubbleSurface = valueBubbleSurface,
            valueBubbleContent = valueBubbleContent,
            disabledActiveTrack = colors.baseContentDisabled,
            disabledInactiveTrack = colors.baseSurfaceDisabled,
            disabledThumb = colors.baseContentDisabled
        )
    }
}

@Composable
private fun getSliderSizeConfig(size: SizeVariant): SliderSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.Small -> SliderSizeConfig(
            trackHeight = HierarchicalSize.SliderTrack.Medium,
            thumbSize = HierarchicalSize.Icon.Compact,
            thumbElevation = HierarchicalSize.Shadow.Medium,
            labelStyle = typography.labelSmall,
            valueStyle = typography.bodyBold
        )
        SizeVariant.Medium -> SliderSizeConfig(
            trackHeight = HierarchicalSize.SliderTrack.Large,
            thumbSize = HierarchicalSize.Icon.Small,
            thumbElevation = HierarchicalSize.Shadow.Large,
            labelStyle = typography.labelMedium,
            valueStyle = typography.bodyRegular
        )
        SizeVariant.Large -> SliderSizeConfig(
            trackHeight = HierarchicalSize.SliderTrack.Large + HierarchicalSize.Spacing.Nano,
            thumbSize = HierarchicalSize.Icon.Medium,
            thumbElevation = HierarchicalSize.Shadow.Huge,
            labelStyle = typography.labelLarge,
            valueStyle = typography.bodyLight
        )
        else -> SliderSizeConfig(
            trackHeight = HierarchicalSize.SliderTrack.Large,
            thumbSize = HierarchicalSize.Icon.Small,
            thumbElevation = HierarchicalSize.Shadow.Large,
            labelStyle = typography.labelMedium,
            valueStyle = typography.bodyRegular
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaSlider - Continuous or discrete value selection component
 *
 * A flexible slider with customizable range, steps, and visual styles.
 *
 * @sample
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
 *     size = SizeVariant.Large
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
 * @param variant Visual style variant (Filled, Outlined, Ghost)
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
 * @param minIcon Optional icon displayed at the minimum end of the slider track
 * @param maxIcon Optional icon displayed at the maximum end of the slider track
 * @param minValueText Optional text displayed at the minimum end of the slider track
 * @param maxValueText Optional text displayed at the maximum end of the slider track
 */
@Composable
fun PixaSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    variant: SliderVariant = SliderVariant.Filled,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    colors: SliderColors? = null,
    config: SliderConfig = SliderConfig(),
    showValue: Boolean = false,
    valueFormatter: (Float) -> String = { it.roundToInt().toString() },
    onValueChangeFinished: (() -> Unit)? = null,
    gradientBrush: Brush? = null,
    thumbColorOverride: Color? = null,
    minIcon: Painter? = null,
    maxIcon: Painter? = null,
    minValueText: String? = null,
    maxValueText: String? = null
) {
    val themeColors = getSliderTheme(variant, AppTheme.colors)
    val finalColors = colors ?: themeColors
    val sizeConfig = getSliderSizeConfig(size)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val normalizedValue = remember(value, valueRange) {
        ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    }

    val animatedFraction by animateFloatAsState(
        targetValue = normalizedValue,
        animationSpec = AnimationUtils.fastSpring,
        label = "slider_fraction"
    )

    var isDragging by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isActive = isDragging || isFocused

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
            progressBarRangeInfo = ProgressBarRangeInfo(value, valueRange, steps)
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
                BasicText(
                    text = valueFormatter(value),
                    style = sizeConfig.valueStyle.copy(color = finalColors.valueText)
                )
            }
            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
        ) {
            if (minIcon != null) {
                PixaIcon(
                    painter = minIcon,
                    contentDescription = null,
                    tint = finalColors.label,
                    modifier = Modifier.size(sizeConfig.thumbSize)
                )
            }
            if (minValueText != null) {
                BasicText(text = minValueText, style = sizeConfig.labelStyle.copy(color = finalColors.label))
            }

            BoxWithConstraints(
                modifier = Modifier.weight(1f).height(sizeConfig.thumbSize)
            ) {
                val trackWidth = constraints.maxWidth.toFloat()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .focusable(enabled = enabled, interactionSource = interactionSource)
                        .onKeyEvent { event ->
                            handleSliderKeyEvent(
                                event = event,
                                enabled = enabled,
                                isRtl = isRtl,
                                value = value,
                                valueRange = valueRange,
                                steps = steps,
                                onValueChange = onValueChange,
                                onValueChangeFinished = onValueChangeFinished
                            )
                        }
                        .pointerInput(enabled, isRtl) {
                            if (!enabled) return@pointerInput
                            detectTapGestures { offset ->
                                val x = if (isRtl) trackWidth - offset.x else offset.x
                                val newNormalizedValue = calculateNormalizedValue(x, trackWidth, steps)
                                val newValue = valueRange.start + (newNormalizedValue * (valueRange.endInclusive - valueRange.start))
                                onValueChange(newValue.coerceIn(valueRange))
                                onValueChangeFinished?.invoke()
                            }
                        }
                        .pointerInput(enabled, isRtl) {
                            if (!enabled) return@pointerInput
                            detectDragGestures(
                                onDragStart = { isDragging = true },
                                onDragEnd = { isDragging = false; onValueChangeFinished?.invoke() },
                                onDragCancel = { isDragging = false }
                            ) { change, _ ->
                                change.consume()
                                val x = if (isRtl) trackWidth - change.position.x else change.position.x
                                val newNormalizedValue = calculateNormalizedValue(x, trackWidth, steps)
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
                        showStepIndicators = config.showStepIndicators,
                        animatedFraction = animatedFraction
                    )

                    ThumbContent(
                        thumbSize = thumbSize,
                        thumbColor = thumbColorOverride ?: thumbColor,
                        thumbBorderColor = finalColors.thumbBorder,
                        sizeConfig = sizeConfig,
                        enabled = enabled,
                        normalizedValue = animatedFraction,
                        config = config,
                        maxWidth = this@BoxWithConstraints.maxWidth
                    )

                    ValueBubble(
                        visible = isActive && enabled,
                        text = valueFormatter(value),
                        normalizedValue = animatedFraction,
                        thumbSize = sizeConfig.thumbSize,
                        valueStyle = sizeConfig.valueStyle,
                        surfaceColor = finalColors.valueBubbleSurface,
                        contentColor = finalColors.valueBubbleContent,
                        maxWidth = this@BoxWithConstraints.maxWidth
                    )
                }
            }

            if (maxValueText != null) {
                BasicText(text = maxValueText, style = sizeConfig.labelStyle.copy(color = finalColors.label))
            }
            if (maxIcon != null) {
                PixaIcon(
                    painter = maxIcon,
                    contentDescription = null,
                    tint = finalColors.label,
                    modifier = Modifier.size(sizeConfig.thumbSize)
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL <SLIDER>
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

/**
 * Keyboard stepping: arrows/Page Up/Page Down step the value, Home/End
 * snap to the range bounds. Left/Right are mirrored under RTL so they always
 * mean "toward the start/end" of the visual track; Up/Down are direction-agnostic.
 */
private fun handleSliderKeyEvent(
    event: KeyEvent,
    enabled: Boolean,
    isRtl: Boolean,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?
): Boolean {
    if (!enabled || event.type != KeyEventType.KeyDown) return false

    val rangeWidth = valueRange.endInclusive - valueRange.start
    val step = if (steps > 0) rangeWidth / (steps + 1) else rangeWidth / 100f
    val pageStep = rangeWidth / 10f

    if (event.key == Key.MoveHome) {
        onValueChange(valueRange.start)
        onValueChangeFinished?.invoke()
        return true
    }
    if (event.key == Key.MoveEnd) {
        onValueChange(valueRange.endInclusive)
        onValueChangeFinished?.invoke()
        return true
    }

    val delta = when (event.key) {
        Key.DirectionRight -> if (isRtl) -step else step
        Key.DirectionLeft -> if (isRtl) step else -step
        Key.DirectionUp -> step
        Key.DirectionDown -> -step
        Key.PageUp -> pageStep
        Key.PageDown -> -pageStep
        else -> return false
    }

    onValueChange((value + delta).coerceIn(valueRange))
    onValueChangeFinished?.invoke()
    return true
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
    showStepIndicators: Boolean,
    animatedFraction: Float
) {
    Box(modifier = Modifier.fillMaxWidth().height(sizeConfig.trackHeight)) {
        if (gradientBrush != null) {
            Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(gradientBrush))
        } else {
            Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(inactiveTrackColor))
            Box(modifier = Modifier.fillMaxWidth(animatedFraction).fillMaxHeight().clip(CircleShape).background(activeTrackColor))
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
    thumbBorderColor: Color,
    sizeConfig: SliderSizeConfig,
    enabled: Boolean,
    normalizedValue: Float,
    config: SliderConfig,
    maxWidth: Dp
) {
    Box(
        modifier = Modifier
            .offset(x = (maxWidth - thumbSize) * normalizedValue)
            .size(thumbSize)
            .shadow(elevation = if (enabled) sizeConfig.thumbElevation else 0.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(thumbColor)
            .border(config.thumbBorderWidth, thumbBorderColor, CircleShape)
    )
}

/**
 * Floating value readout shown above the thumb while it's being dragged or is
 * keyboard-focused. Positioned as a same-size box aligned to the thumb's
 * x-offset so it stays centered over the thumb.
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
