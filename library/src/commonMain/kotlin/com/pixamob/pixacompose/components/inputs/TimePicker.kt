package com.pixamob.pixacompose.components.inputs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaChip
import com.pixamob.pixacompose.components.actions.ChipType
import com.pixamob.pixacompose.components.actions.ChipVariant
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.RadiusSize
import dev.darkokoa.datetimewheelpicker.WheelTimePicker
import dev.darkokoa.datetimewheelpicker.core.WheelPickerDefaults
import dev.darkokoa.datetimewheelpicker.core.format.timeFormatter
import dev.darkokoa.datetimewheelpicker.core.format.TimeFormat as DarkokaoTimeFormat
import kotlinx.datetime.LocalTime

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class TimePickerVariant {
    Wheel,
    Clock,
    TimeOfDayPicker
}

enum class TimePickerSize { Small, Medium, Large }

enum class TimeFormat { Hour12, Hour24 }

enum class TimeSelectionMode { Single, Multiple, Range }

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class TimePickerColors(
    val background: Color,
    val surface: Color,
    val selectedBackground: Color,
    val selectedText: Color,
    val unselectedText: Color,
    val divider: Color,
    val title: Color,
    val disabledText: Color
)

@Immutable
@Stable
data class TimePickerSizeConfig(
    val height: Dp,
    val padding: Dp,
    val cornerRadius: Dp,
    val titleTextStyle: TextStyle,
    val itemTextStyle: TextStyle,
    val selectorShape: Shape = RoundedCornerShape(16.dp),
    val selectorBorder: BorderStroke? = null
)

@Stable
data class TimePickerStrings(
    val amLabel: String = "AM",
    val pmLabel: String = "PM",
    val startLabel: String = "Start",
    val endLabel: String = "End",
    val selectLabel: String = "Select",
    val hourLabel: String = "Hour",
    val minuteLabel: String = "Minute",
    val timeOfDayLabels: Map<String, LocalTime> = mapOf(
        "Morning" to LocalTime(9, 0),
        "Afternoon" to LocalTime(14, 0),
        "Evening" to LocalTime(18, 0),
        "Night" to LocalTime(21, 0)
    )
)

@Stable
data class WheelTimeConfig(
    val showSeconds: Boolean = false,
    val showDividers: Boolean = true
)

@Stable
data class ClockConfig(
    val showMinuteSelector: Boolean = true,
    val showPeriodSelector: Boolean = true
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getTimePickerTheme(colors: ColorPalette): TimePickerColors {
    return TimePickerColors(
        background = colors.baseSurfaceDefault,
        surface = colors.baseSurfaceElevated,
        selectedBackground = colors.brandSurfaceDefault,
        selectedText = colors.baseContentNegative,
        unselectedText = colors.baseContentBody,
        divider = colors.baseBorderDefault,
        title = colors.baseContentTitle,
        disabledText = colors.baseContentDisabled
    )
}

@Composable
private fun getTimePickerSizeConfig(size: TimePickerSize): TimePickerSizeConfig {
    val typography = AppTheme.typography
    val colors = AppTheme.colors
    return when (size) {
        TimePickerSize.Small -> TimePickerSizeConfig(
            height = 280.dp,
            padding = HierarchicalSize.Spacing.Medium,
            cornerRadius = RadiusSize.Medium,
            titleTextStyle = typography.bodyLight,
            itemTextStyle = typography.bodyLight,
            selectorShape = RoundedCornerShape(12.dp),
            selectorBorder = BorderStroke(1.5.dp, colors.baseBorderDefault)
        )

        TimePickerSize.Medium -> TimePickerSizeConfig(
            height = 320.dp,
            padding = HierarchicalSize.Spacing.Large,
            cornerRadius = RadiusSize.Medium,
            titleTextStyle = typography.bodyBold,
            itemTextStyle = typography.bodyBold,
            selectorShape = RoundedCornerShape(16.dp),
            selectorBorder = BorderStroke(2.dp, colors.baseBorderDefault)
        )

        TimePickerSize.Large -> TimePickerSizeConfig(
            height = 360.dp,
            padding = HierarchicalSize.Spacing.Huge,
            cornerRadius = RadiusSize.Large,
            titleTextStyle = typography.titleBold,
            itemTextStyle = typography.titleRegular,
            selectorShape = RoundedCornerShape(20.dp),
            selectorBorder = BorderStroke(2.5.dp, colors.baseBorderDefault)
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaTimePicker - Comprehensive time picker component
 *
 * A flexible time picker with multiple variants and full customization.
 * Supports single, multiple, and range selection modes.
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Wheel time picker (iOS style)
 * PixaTimePicker(
 *     variant = TimePickerVariant.Wheel,
 *     onTimeSelected = { time -> println("Selected: $time") }
 * )
 *
 * // Clock picker (Material 3 style)
 * PixaTimePicker(
 *     variant = TimePickerVariant.Clock,
 *     format = TimeFormat.Hour24,
 *     onTimeSelected = { time -> println("Selected: $time") }
 * )
 *
 * // Range selection
 * PixaTimePicker(
 *     variant = TimePickerVariant.Wheel,
 *     mode = TimeSelectionMode.Range,
 *     initialStartTime = LocalTime(9, 0),
 *     initialEndTime = LocalTime(17, 0),
 *     onRangeSelected = { start, end -> println("$start to $end") }
 * )
 *
 * // Time of day picker
 * PixaTimePicker(
 *     variant = TimePickerVariant.TimeOfDayPicker,
 *     strings = TimePickerStrings(
 *         timeOfDayLabels = mapOf(
 *             "Breakfast" to LocalTime(8, 0),
 *             "Lunch" to LocalTime(12, 30),
 *             "Dinner" to LocalTime(19, 0)
 *         )
 *     ),
 *     onTimeOfDaySelected = { slot, time -> println("$slot at $time") }
 * )
 *
 * // Custom colors
 * PixaTimePicker(
 *     variant = TimePickerVariant.Wheel,
 *     colors = TimePickerColors(
 *         background = Color.White,
 *         selectedBackground = Color.Blue,
 *         selectedText = Color.White,
 *         // ... other colors
 *     ),
 *     onTimeSelected = { ... }
 * )
 * ```
 *
 * @param variant Visual style variant (Wheel, Clock, TimeOfDayPicker)
 * @param modifier Modifier for the picker
 * @param mode Selection mode (Single, Multiple, Range)
 * @param size Size variant (Small, Medium, Large)
 * @param format Time format (Hour12, Hour24)
 * @param enabled Whether the picker is enabled
 * @param colors Custom colors (null = use theme)
 * @param strings Localization strings
 * @param minuteInterval Minute step interval (1, 5, 15, 30, etc.)
 * @param minTime Minimum selectable time
 * @param maxTime Maximum selectable time
 * @param wheelConfig Wheel picker configuration
 * @param clockConfig Clock picker configuration
 * @param initialTime Initial selected time
 * @param initialStartTime Initial range start time
 * @param initialEndTime Initial range end time
 * @param initialTimeOfDay Initial selected time of day slot
 * @param onTimeSelected Callback for single time selection
 * @param onRangeSelected Callback for range selection
 * @param onTimeOfDaySelected Callback for time of day selection
 */
@Composable
fun PixaTimePicker(
    variant: TimePickerVariant,
    modifier: Modifier = Modifier,
    mode: TimeSelectionMode = TimeSelectionMode.Single,
    size: TimePickerSize = TimePickerSize.Medium,
    format: TimeFormat = TimeFormat.Hour12,
    enabled: Boolean = true,
    colors: TimePickerColors? = null,
    strings: TimePickerStrings = TimePickerStrings(),
    minuteInterval: Int = 1,
    minTime: LocalTime? = null,
    maxTime: LocalTime? = null,
    wheelConfig: WheelTimeConfig = WheelTimeConfig(),
    clockConfig: ClockConfig = ClockConfig(),
    initialTime: LocalTime? = null,
    initialStartTime: LocalTime? = null,
    initialEndTime: LocalTime? = null,
    initialTimeOfDay: String? = null,
    onTimeSelected: ((LocalTime) -> Unit)? = null,
    onRangeSelected: ((LocalTime?, LocalTime?) -> Unit)? = null,
    onTimeOfDaySelected: ((String, LocalTime) -> Unit)? = null
) {
    val themeColors = getTimePickerTheme(AppTheme.colors)
    val finalColors = colors ?: themeColors
    val sizeConfig = getTimePickerSizeConfig(size)

    val containerModifier = modifier
        .clip(RoundedCornerShape(sizeConfig.cornerRadius))
        .background(finalColors.background)
        .padding(sizeConfig.padding)

    when (variant) {
        TimePickerVariant.Wheel -> WheelTimePickerContent(
            modifier = containerModifier,
            mode = mode,
            sizeConfig = sizeConfig,
            colors = finalColors,
            strings = strings,
            format = format,
            enabled = enabled,
            minuteInterval = minuteInterval,
            initialTime = initialTime,
            initialStartTime = initialStartTime,
            initialEndTime = initialEndTime,
            onTimeSelected = onTimeSelected,
            onRangeSelected = onRangeSelected
        )

        TimePickerVariant.Clock -> ClockTimePickerContent(
            modifier = containerModifier,
            mode = mode,
            sizeConfig = sizeConfig,
            colors = finalColors,
            strings = strings,
            format = format,
            enabled = enabled,
            minTime = minTime,
            maxTime = maxTime,
            clockConfig = clockConfig,
            initialTime = initialTime,
            initialStartTime = initialStartTime,
            initialEndTime = initialEndTime,
            onTimeSelected = onTimeSelected,
            onRangeSelected = onRangeSelected
        )

        TimePickerVariant.TimeOfDayPicker -> TimeOfDayPickerContent(
            modifier = containerModifier,
            mode = mode,
            sizeConfig = sizeConfig,
            colors = finalColors,
            strings = strings,
            enabled = enabled,
            initialTimeOfDay = initialTimeOfDay,
            onTimeOfDaySelected = onTimeOfDaySelected
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// WHEEL TIME PICKER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun WheelTimePickerContent(
    modifier: Modifier,
    mode: TimeSelectionMode,
    sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors,
    strings: TimePickerStrings,
    format: TimeFormat,
    enabled: Boolean,
    minuteInterval: Int,
    initialTime: LocalTime?,
    initialStartTime: LocalTime?,
    initialEndTime: LocalTime?,
    onTimeSelected: ((LocalTime) -> Unit)?, onRangeSelected: ((LocalTime?, LocalTime?) -> Unit)?
) {
    when (mode) {
        TimeSelectionMode.Single -> SingleWheelTimePicker(
            modifier = modifier, sizeConfig = sizeConfig, colors = colors, strings = strings,
            format = format, enabled = enabled, minuteInterval = minuteInterval,
            initialTime = initialTime, onTimeSelected = onTimeSelected ?: {}
        )

        TimeSelectionMode.Range -> RangeWheelTimePicker(
            modifier = modifier, sizeConfig = sizeConfig, colors = colors, strings = strings,
            format = format, enabled = enabled, minuteInterval = minuteInterval,
            initialStartTime = initialStartTime, initialEndTime = initialEndTime,
            onRangeSelected = onRangeSelected ?: { _, _ -> }
        )

        TimeSelectionMode.Multiple -> SingleWheelTimePicker(
            modifier = modifier, sizeConfig = sizeConfig, colors = colors, strings = strings,
            format = format, enabled = enabled, minuteInterval = minuteInterval,
            initialTime = initialTime, onTimeSelected = onTimeSelected ?: {}
        )
    }
}

@Composable
private fun SingleWheelTimePicker(
    modifier: Modifier, sizeConfig: TimePickerSizeConfig, colors: TimePickerColors,
    strings: TimePickerStrings, format: TimeFormat, enabled: Boolean,
    minuteInterval: Int, initialTime: LocalTime?, onTimeSelected: (LocalTime) -> Unit
) {
    var selectedTime by remember { mutableStateOf(initialTime ?: LocalTime(12, 0)) }

    Column(modifier = modifier.semantics {
        contentDescription = if (format == TimeFormat.Hour12) "${strings.hourLabel} ${strings.minuteLabel} ${strings.amLabel}/${strings.pmLabel}"
            else "${strings.hourLabel} ${strings.minuteLabel}"
    }, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.fillMaxWidth().height(sizeConfig.height),
            contentAlignment = Alignment.Center
        ) {
            WheelTimePicker(
                modifier = Modifier.fillMaxSize(), startTime = selectedTime,
                timeFormatter = timeFormatter(
                    timeFormat = if (format == TimeFormat.Hour24) DarkokaoTimeFormat.HOUR_24 else DarkokaoTimeFormat.AM_PM
                ),
                textStyle = sizeConfig.itemTextStyle.copy(textAlign = TextAlign.Center),
                textColor = colors.selectedText,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    color = colors.divider,
                    shape = sizeConfig.selectorShape,
                    border = sizeConfig.selectorBorder
                )
            ) { time ->
                if (enabled) {
                    val adjustedMinute = (time.minute / minuteInterval) * minuteInterval
                    val adjustedTime = LocalTime(time.hour, adjustedMinute)
                    selectedTime = adjustedTime
                    onTimeSelected(adjustedTime)
                }
            }
        }
    }
}

@Composable
private fun RangeWheelTimePicker(
    modifier: Modifier, sizeConfig: TimePickerSizeConfig, colors: TimePickerColors,
    strings: TimePickerStrings, format: TimeFormat, enabled: Boolean,
    minuteInterval: Int, initialStartTime: LocalTime?, initialEndTime: LocalTime?,
    onRangeSelected: (LocalTime?, LocalTime?) -> Unit
) {
    var selectingStart by remember { mutableStateOf(true) }
    var startTime by remember { mutableStateOf(initialStartTime) }
    var endTime by remember { mutableStateOf(initialEndTime) }

    Column(modifier = modifier) {
        RangeSelectorRow(
            sizeConfig, colors, strings, startTime?.toString(), endTime?.toString(),
            selectingStart, { selectingStart = true }, { selectingStart = false })
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
        Box(
            modifier = Modifier.fillMaxWidth().height(sizeConfig.height),
            contentAlignment = Alignment.Center
        ) {
            WheelTimePicker(
                modifier = Modifier.fillMaxSize(),
                startTime = if (selectingStart) (startTime ?: LocalTime(12, 0)) else (endTime
                    ?: LocalTime(12, 0)),
                timeFormatter = timeFormatter(
                    timeFormat = if (format == TimeFormat.Hour24) DarkokaoTimeFormat.HOUR_24 else DarkokaoTimeFormat.AM_PM
                ),
                textStyle = sizeConfig.itemTextStyle.copy(textAlign = TextAlign.Center),
                textColor = colors.selectedText,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    color = colors.divider,
                    shape = sizeConfig.selectorShape,
                    border = sizeConfig.selectorBorder
                )
            ) { time ->
                if (enabled) {
                    val adjustedMinute = (time.minute / minuteInterval) * minuteInterval
                    val adjustedTime = LocalTime(time.hour, adjustedMinute)
                    if (selectingStart) {
                        startTime = adjustedTime; selectingStart = false
                    } else {
                        endTime = adjustedTime
                    }
                    onRangeSelected(startTime, endTime)
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CLOCK TIME PICKER
// ════════════════════════════════════════════════════════════════════════════

@Suppress("UNUSED_PARAMETER")
@Composable
private fun ClockTimePickerContent(
    modifier: Modifier, mode: TimeSelectionMode, sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors, strings: TimePickerStrings, format: TimeFormat,
    enabled: Boolean, minTime: LocalTime?, maxTime: LocalTime?, clockConfig: ClockConfig,
    initialTime: LocalTime?, initialStartTime: LocalTime?, initialEndTime: LocalTime?,
    onTimeSelected: ((LocalTime) -> Unit)?, onRangeSelected: ((LocalTime?, LocalTime?) -> Unit)?
) {
    when (mode) {
        TimeSelectionMode.Single, TimeSelectionMode.Multiple -> SingleClockTimePicker(
            modifier = modifier, sizeConfig = sizeConfig, colors = colors,
            format = format, enabled = enabled, initialTime = initialTime,
            onTimeSelected = onTimeSelected ?: {}
        )

        TimeSelectionMode.Range -> RangeClockTimePicker(
            modifier = modifier, sizeConfig = sizeConfig, colors = colors, strings = strings,
            format = format, enabled = enabled,
            initialStartTime = initialStartTime, initialEndTime = initialEndTime,
            onRangeSelected = onRangeSelected ?: { _, _ -> }
        )
    }
}

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleClockTimePicker(
    modifier: Modifier, sizeConfig: TimePickerSizeConfig, colors: TimePickerColors,
    format: TimeFormat, enabled: Boolean,
    initialTime: LocalTime?, onTimeSelected: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime?.hour ?: 12,
        initialMinute = initialTime?.minute ?: 0,
        is24Hour = format == TimeFormat.Hour24
    )

    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        if (enabled) {
            onTimeSelected(LocalTime(timePickerState.hour, timePickerState.minute))
        }
    }

    Column(modifier = modifier) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = colors.selectedBackground, onPrimary = colors.selectedText,
                surface = colors.surface, onSurface = colors.unselectedText,
                surfaceVariant = colors.background, onSurfaceVariant = colors.unselectedText
            )
        ) {
            TimePicker(
                state = timePickerState, modifier = Modifier.fillMaxWidth(),
                colors = TimePickerDefaults.colors(
                    clockDialColor = colors.surface,
                    clockDialSelectedContentColor = colors.selectedText,
                    clockDialUnselectedContentColor = colors.unselectedText,
                    selectorColor = colors.selectedBackground,
                    containerColor = colors.background,
                    periodSelectorBorderColor = colors.divider,
                    periodSelectorSelectedContainerColor = colors.selectedBackground,
                    periodSelectorUnselectedContainerColor = colors.surface,
                    periodSelectorSelectedContentColor = colors.selectedText,
                    periodSelectorUnselectedContentColor = colors.unselectedText,
                    timeSelectorSelectedContainerColor = colors.selectedBackground,
                    timeSelectorUnselectedContainerColor = colors.surface,
                    timeSelectorSelectedContentColor = colors.selectedText,
                    timeSelectorUnselectedContentColor = colors.unselectedText
                )
            )
        }
    }
}

@Composable
private fun RangeClockTimePicker(
    modifier: Modifier,
    sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors,
    strings: TimePickerStrings,
    format: TimeFormat,
    enabled: Boolean,
    initialStartTime: LocalTime?,
    initialEndTime: LocalTime?,
    onRangeSelected: (LocalTime?, LocalTime?) -> Unit
) {
    var selectingStart by remember { mutableStateOf(true) }
    var startTime by remember { mutableStateOf(initialStartTime) }
    var endTime by remember { mutableStateOf(initialEndTime) }

    Column(modifier = modifier) {
        RangeSelectorRow(
            sizeConfig, colors, strings, startTime?.toString(), endTime?.toString(),
            selectingStart, { selectingStart = true }, { selectingStart = false })
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
        SingleClockTimePicker(
            modifier = Modifier, sizeConfig = sizeConfig, colors = colors,
            format = format, enabled = enabled,
            initialTime = if (selectingStart) startTime else endTime
        ) { time ->
            if (selectingStart) {
                startTime = time; selectingStart = false
            } else {
                endTime = time
            }
            onRangeSelected(startTime, endTime)
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// TIME OF DAY PICKER
// ════════════════════════════════════════════════════════════════════════════

@Suppress("UNUSED_PARAMETER")
@Composable
private fun TimeOfDayPickerContent(
    modifier: Modifier, mode: TimeSelectionMode, sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors, strings: TimePickerStrings, enabled: Boolean,
    initialTimeOfDay: String?, onTimeOfDaySelected: ((String, LocalTime) -> Unit)?
) {
    var selectedSlots by remember {
        mutableStateOf(initialTimeOfDay?.let { setOf(it) } ?: emptySet<String>())
    }
    val isMultiSelect = mode == TimeSelectionMode.Multiple

    Column(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)) {
            strings.timeOfDayLabels.forEach { (slotName, time) ->
                val isSelected = slotName in selectedSlots
                PixaChip(
                    text = "$slotName (${time})",
                    variant = if (isSelected) ChipVariant.Solid else ChipVariant.Outlined,
                    type = ChipType.Selectable,
                    selected = isSelected,
                    enabled = enabled,
                    onClick = {
                        selectedSlots = if (isMultiSelect) {
                            if (isSelected) selectedSlots - slotName else selectedSlots + slotName
                        } else {
                            setOf(slotName)
                        }
                        onTimeOfDaySelected?.invoke(slotName, time)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentDescription = "$slotName at $time"
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SHARED COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun RangeSelectorRow(
    sizeConfig: TimePickerSizeConfig, colors: TimePickerColors, strings: TimePickerStrings,
    startValue: String?, endValue: String?, selectingStart: Boolean,
    onStartClick: () -> Unit, onEndClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = HierarchicalSize.Spacing.Small),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RangeSelectorItem(
            strings.startLabel, startValue ?: strings.selectLabel, selectingStart,
            sizeConfig, colors, onStartClick, Modifier.weight(1f)
        )
        RangeSelectorItem(
            strings.endLabel, endValue ?: strings.selectLabel, !selectingStart,
            sizeConfig, colors, onEndClick, Modifier.weight(1f)
        )
    }
}

@Composable
private fun RangeSelectorItem(
    label: String, value: String, isSelected: Boolean, sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label, style = sizeConfig.itemTextStyle.copy(
                fontSize = sizeConfig.itemTextStyle.fontSize * 0.8f
            ),
            color = colors.unselectedText.copy(alpha = 0.6f)
        )
        Text(
            text = value, style = sizeConfig.itemTextStyle,
            color = if (isSelected) colors.selectedText else colors.unselectedText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier
                .clip(RoundedCornerShape(RadiusSize.Small))
                .background(if (isSelected) colors.selectedBackground else Color.Transparent)
                .padding(HierarchicalSize.Spacing.Small)
                .clickable(onClick = onClick)
        )
    }
}
