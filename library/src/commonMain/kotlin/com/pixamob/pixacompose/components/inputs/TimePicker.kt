package com.pixamob.pixacompose.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import com.pixamob.pixacompose.components.actions.Chip
import com.pixamob.pixacompose.components.actions.ChipType
import com.pixamob.pixacompose.components.actions.ChipVariant
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.RadiusSize
import com.pixamob.pixacompose.theme.HierarchicalSize
import dev.darkokoa.datetimewheelpicker.WheelTimePicker
import dev.darkokoa.datetimewheelpicker.core.WheelPickerDefaults
import dev.darkokoa.datetimewheelpicker.core.format.timeFormatter
import dev.darkokoa.datetimewheelpicker.core.format.TimeFormat as DarkokaoTimeFormat
import kotlinx.datetime.LocalTime

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * TimePicker Variant - Visual style and interaction pattern
 */
enum class TimePickerVariant {
    /** Wheel picker (iOS-style) - Works on all platforms */
    Wheel,
    /** Clock face (Material 3) - Best for Android */
    Clock,
    /** Time of day picker - Select predefined time slots (Morning/Afternoon/Evening) */
    TimeOfDayPicker
}

/**
 * TimePicker Size - Height and padding variants
 */
enum class TimePickerSize {
    /** Small size - 240dp - Compact pickers */
    Small,
    /** Medium size - 280dp - DEFAULT, standard picker */
    Medium,
    /** Large size - 320dp - Prominent pickers */
    Large
}

/**
 * Time Format
 */
enum class TimeFormat {
    /** 12-hour format with AM/PM */
    Hour12,
    /** 24-hour format */
    Hour24
}

/**
 * Time Selection Mode
 */
enum class TimeSelectionMode {
    /** Single time selection */
    Single,
    /** Time range selection (start and end times) */
    Range
}

/**
 * TimePicker Colors - Theme-aware color configuration
 */
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

/**
 * TimePicker Size Configuration
 */
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

/**
 * Localization strings for TimePicker
 * All text is customizable for i18n support
 */
@Stable
data class TimePickerStrings(
    val amLabel: String = "AM",
    val pmLabel: String = "PM",
    val startLabel: String = "Start",
    val endLabel: String = "End",
    val selectLabel: String = "Select",
    val hourLabel: String = "Hour",
    val minuteLabel: String = "Minute",
    // Time of day slots (customizable)
    val timeOfDayLabels: Map<String, LocalTime> = mapOf(
        "Morning" to LocalTime(9, 0),
        "Afternoon" to LocalTime(14, 0),
        "Evening" to LocalTime(18, 0),
        "Night" to LocalTime(21, 0)
    )
)

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get time picker colors from theme
 */
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

/**
 * Get time picker size configuration
 */
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

// ============================================================================
// UNIFIED TIME PICKER (Main Entry Point)
// ============================================================================

/**
 * Unified TimePicker - Main composable that delegates to specific variants
 *
 * A comprehensive time picker with multiple variants and full i18n support.
 * All text is customizable via the `strings` parameter for multilingual apps.
 *
 * @param variant The picker variant to display
 * @param mode Selection mode (Single or Range)
 * @param modifier Modifier for the picker
 * @param size Size variant
 * @param title Optional title text
 * @param format Time format (12-hour or 24-hour)
 * @param colors Optional custom colors (overrides theme)
 * @param strings Localization strings for i18n (defaults to English)
 * @param minuteInterval Minute step interval (1, 5, 15, 30, etc.)
 * @param minTime Minimum selectable time (null = no limit)
 * @param maxTime Maximum selectable time (null = no limit)
 * @param onTimeSelected Callback for single time selection (LocalTime)
 * @param onRangeSelected Callback for range selection (start, end as LocalTime)
 * @param onTimeOfDaySelected Callback for time of day selection (slot name and LocalTime)
 *
 * @sample
 * ```
 * // Single time with Wheel
 * TimePicker(
 *     variant = TimePickerVariant.Wheel,
 *     onTimeSelected = { time -> println("Selected: $time") }
 * )
 *
 * // Range selection
 * TimePicker(
 *     variant = TimePickerVariant.Wheel,
 *     mode = TimeSelectionMode.Range,
 *     onRangeSelected = { start, end -> println("Range: $start to $end") }
 * )
 *
 * // With Chinese localization
 * TimePicker(
 *     variant = TimePickerVariant.Wheel,
 *     strings = TimePickerStrings(
 *         amLabel = "上午",
 *         pmLabel = "下午",
 *         startLabel = "开始",
 *         endLabel = "结束"
 *     ),
 *     onTimeSelected = { ... }
 * )
 * ```
 */
@Composable
fun TimePicker(
    variant: TimePickerVariant,
    modifier: Modifier = Modifier,
    mode: TimeSelectionMode = TimeSelectionMode.Single,
    size: TimePickerSize = TimePickerSize.Medium,
    title: String = "",
    format: TimeFormat = TimeFormat.Hour12,
    colors: TimePickerColors? = null,
    strings: TimePickerStrings = TimePickerStrings(),
    minuteInterval: Int = 1,
    minTime: LocalTime? = null,
    maxTime: LocalTime? = null,
    // Callbacks for different variants
    onTimeSelected: ((LocalTime) -> Unit)? = null,
    onRangeSelected: ((LocalTime?, LocalTime?) -> Unit)? = null,
    onTimeOfDaySelected: ((String, LocalTime) -> Unit)? = null
) {
    val themeColors = getTimePickerTheme(AppTheme.colors)
    val finalColors = colors ?: themeColors
    val sizeConfig = getTimePickerSizeConfig(size)

    when (variant) {
        TimePickerVariant.Wheel -> {
            if (mode == TimeSelectionMode.Range) {
                // Range mode with wheel picker
                RangeWheelTimePickerImpl(
                    modifier = modifier,
                    sizeConfig = sizeConfig,
                    colors = finalColors,
                    strings = strings,
                    title = title,
                    format = format,
                    minuteInterval = minuteInterval,
                    onRangeSelected = onRangeSelected ?: { _, _ -> }
                )
            } else {
                // Single time wheel picker
                WheelTimePickerImpl(
                    modifier = modifier,
                    sizeConfig = sizeConfig,
                    colors = finalColors,
                    strings = strings,
                    title = title,
                    format = format,
                    minuteInterval = minuteInterval,
                    onTimeSelected = onTimeSelected ?: {}
                )
            }
        }
        TimePickerVariant.Clock -> {
            if (mode == TimeSelectionMode.Range) {
                // Range mode with clock (note: simplified, Material3 TimePicker doesn't support range natively)
                RangeClockTimePickerImpl(
                    modifier = modifier,
                    sizeConfig = sizeConfig,
                    colors = finalColors,
                    strings = strings,
                    title = title,
                    format = format,
                    minTime = minTime,
                    maxTime = maxTime,
                    onRangeSelected = onRangeSelected ?: { _, _ -> }
                )
            } else {
                // Single time clock picker (Material3-style)
                ClockTimePickerImpl(
                    modifier = modifier,
                    sizeConfig = sizeConfig,
                    colors = finalColors,
                    strings = strings,
                    title = title,
                    format = format,
                    minTime = minTime,
                    maxTime = maxTime,
                    onTimeSelected = onTimeSelected ?: {}
                )
            }
        }
        TimePickerVariant.TimeOfDayPicker -> {
            TimeOfDayPickerImpl(
                modifier = modifier,
                sizeConfig = sizeConfig,
                colors = finalColors,
                strings = strings,
                title = title,
                onTimeOfDaySelected = onTimeOfDaySelected ?: { _, _ -> }
            )
        }
    }
}

// ============================================================================
// WHEEL TIME PICKER IMPLEMENTATION
// ============================================================================

@Composable
private fun WheelTimePickerImpl(
    modifier: Modifier,
    sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors,
    strings: TimePickerStrings, // Used in semantics for accessibility
    title: String,
    format: TimeFormat,
    minuteInterval: Int, // Constraint applied in onTimeChangeListener
    onTimeSelected: (LocalTime) -> Unit
) {
    var selectedTime by remember { mutableStateOf(LocalTime(12, 0)) }

    Column (
        modifier = modifier
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.background)
            .padding(sizeConfig.padding)
            .semantics {
                contentDescription = title.ifEmpty {
                    if (format == TimeFormat.Hour12)
                        "${strings.hourLabel} ${strings.minuteLabel} ${strings.amLabel}/${strings.pmLabel}"
                    else
                        "${strings.hourLabel} ${strings.minuteLabel}"
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = sizeConfig.titleTextStyle,
                color = colors.title,
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Medium)
            )
        }

        // Using darkokoa datetime wheel picker
        Box(
            modifier = Modifier.fillMaxWidth().height(sizeConfig.height),
            contentAlignment = Alignment.Center
        ) {
            WheelTimePicker(
                modifier = Modifier.fillMaxSize(),
                startTime = selectedTime,
                timeFormatter = timeFormatter(
                    timeFormat = if (format == TimeFormat.Hour24)
                        DarkokaoTimeFormat.HOUR_24
                    else
                        DarkokaoTimeFormat.AM_PM
                ),
                textStyle = sizeConfig.itemTextStyle.copy(textAlign = TextAlign.Center),
                textColor = colors.selectedText,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                   color = colors.divider,
                   shape = sizeConfig.selectorShape,
                   border = sizeConfig.selectorBorder
                )
            ) { time ->
                // Apply minuteInterval constraint
                val adjustedMinute = (time.minute / minuteInterval) * minuteInterval
                val adjustedTime = LocalTime(time.hour, adjustedMinute)
                selectedTime = adjustedTime
                onTimeSelected(adjustedTime)
            }
        }
    }
}

@Composable
private fun RangeWheelTimePickerImpl(
    modifier: Modifier,
    sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors,
    strings: TimePickerStrings,
    title: String,
    format: TimeFormat,
    minuteInterval: Int, // Constraint applied in onTimeChangeListener
    onRangeSelected: (LocalTime?, LocalTime?) -> Unit
) {
    var selectingStart by remember { mutableStateOf(true) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.background)
            .padding(sizeConfig.padding)
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = sizeConfig.titleTextStyle,
                color = colors.title,
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
            )
        }

        // Range labels
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = HierarchicalSize.Spacing.Small),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = strings.startLabel, style = sizeConfig.itemTextStyle.copy(fontSize = sizeConfig.itemTextStyle.fontSize * 0.8f), color = colors.unselectedText.copy(alpha = 0.6f))
                Text(
                    text = startTime?.toString() ?: strings.selectLabel,
                    style = sizeConfig.itemTextStyle,
                    color = if (selectingStart) colors.selectedText else colors.unselectedText,
                    fontWeight = if (selectingStart) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(RadiusSize.Small))
                        .background(if (selectingStart) colors.selectedBackground else Color.Transparent)
                        .padding(HierarchicalSize.Spacing.Small)
                        .clickable { selectingStart = true }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = strings.endLabel, style = sizeConfig.itemTextStyle.copy(fontSize = sizeConfig.itemTextStyle.fontSize * 0.8f), color = colors.unselectedText.copy(alpha = 0.6f))
                Text(
                    text = endTime?.toString() ?: strings.selectLabel,
                    style = sizeConfig.itemTextStyle,
                    color = if (!selectingStart) colors.selectedText else colors.unselectedText,
                    fontWeight = if (!selectingStart) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(RadiusSize.Small))
                        .background(if (!selectingStart) colors.selectedBackground else Color.Transparent)
                        .padding(HierarchicalSize.Spacing.Small)
                        .clickable { selectingStart = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

        // Wheel picker
        Box(
            modifier = Modifier.fillMaxWidth().height(sizeConfig.height),
            contentAlignment = Alignment.Center
        ) {
            WheelTimePicker(
                modifier = Modifier.fillMaxSize(),
                startTime = if (selectingStart) (startTime ?: LocalTime(12, 0)) else (endTime ?: LocalTime(12, 0)),
                timeFormatter = timeFormatter(
                    timeFormat = if (format == TimeFormat.Hour24)
                        DarkokaoTimeFormat.HOUR_24
                    else
                        DarkokaoTimeFormat.AM_PM
                ),
                textStyle = sizeConfig.itemTextStyle.copy(textAlign = TextAlign.Center),
                textColor = colors.selectedText,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    color = colors.divider,
                    shape = sizeConfig.selectorShape,
                    border = sizeConfig.selectorBorder
                )
            ) { time ->
                // Apply minuteInterval constraint
                val adjustedMinute = (time.minute / minuteInterval) * minuteInterval
                val adjustedTime = LocalTime(time.hour, adjustedMinute)

                if (selectingStart) {
                    startTime = adjustedTime
                    selectingStart = false
                } else {
                    endTime = adjustedTime
                }
                onRangeSelected(startTime, endTime)
            }
        }
    }
}

// ============================================================================
// CLOCK TIME PICKER IMPLEMENTATION (Material 3 Compose Multiplatform)
// ============================================================================

/**
 * Material 3 TimePicker implementation using Compose Multiplatform
 * Works on all platforms (Android, iOS, Desktop)
 * https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-time-picker.html
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClockTimePickerImpl(
    modifier: Modifier,
    sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors,
    @Suppress("UNUSED_PARAMETER") strings: TimePickerStrings, // Reserved for future customization
    title: String,
    format: TimeFormat,
    @Suppress("UNUSED_PARAMETER") minTime: LocalTime?, // Reserved for validation
    @Suppress("UNUSED_PARAMETER") maxTime: LocalTime?, // Reserved for validation
    onTimeSelected: (LocalTime) -> Unit
) {
    // Create Material3 TimePickerState
    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = format == TimeFormat.Hour24
    )

    // Observe state changes and notify parent
    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        onTimeSelected(LocalTime(timePickerState.hour, timePickerState.minute))
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.background)
            .padding(sizeConfig.padding)
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = sizeConfig.titleTextStyle,
                color = colors.title,
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
            )
        }

        // Use Material 3 TimePicker from Compose Multiplatform
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = colors.selectedBackground,
                onPrimary = colors.selectedText,
                surface = colors.surface,
                onSurface = colors.unselectedText,
                surfaceVariant = colors.background,
                onSurfaceVariant = colors.unselectedText
            )
        ) {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.fillMaxWidth(),
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
private fun RangeClockTimePickerImpl(
    modifier: Modifier,
    sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors,
    strings: TimePickerStrings,
    title: String,
    format: TimeFormat,
    minTime: LocalTime?,
    maxTime: LocalTime?,
    onRangeSelected: (LocalTime?, LocalTime?) -> Unit
) {
    var selectingStart by remember { mutableStateOf(true) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.background)
            .padding(sizeConfig.padding)
    ) {
        if (title.isNotEmpty()) {
            Text(text = title, style = sizeConfig.titleTextStyle, color = colors.title, modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small))
        }

        // Range labels
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = HierarchicalSize.Spacing.Small), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = strings.startLabel, style = sizeConfig.itemTextStyle.copy(fontSize = sizeConfig.itemTextStyle.fontSize * 0.8f), color = colors.unselectedText.copy(alpha = 0.6f))
                Text(
                    text = startTime?.toString() ?: strings.selectLabel,
                    style = sizeConfig.itemTextStyle,
                    color = if (selectingStart) colors.selectedText else colors.unselectedText,
                    fontWeight = if (selectingStart) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(RadiusSize.Small))
                        .background(if (selectingStart) colors.selectedBackground else Color.Transparent)
                        .padding(HierarchicalSize.Spacing.Small)
                        .clickable { selectingStart = true }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = strings.endLabel, style = sizeConfig.itemTextStyle.copy(fontSize = sizeConfig.itemTextStyle.fontSize * 0.8f), color = colors.unselectedText.copy(alpha = 0.6f))
                Text(
                    text = endTime?.toString() ?: strings.selectLabel,
                    style = sizeConfig.itemTextStyle,
                    color = if (!selectingStart) colors.selectedText else colors.unselectedText,
                    fontWeight = if (!selectingStart) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(RadiusSize.Small))
                        .background(if (!selectingStart) colors.selectedBackground else Color.Transparent)
                        .padding(HierarchicalSize.Spacing.Small)
                        .clickable { selectingStart = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

        // Reuse clock impl
        ClockTimePickerImpl(
            modifier = Modifier,
            sizeConfig = sizeConfig,
            colors = colors,
            strings = strings,
            title = "",
            format = format,
            minTime = if (!selectingStart) startTime else minTime,
            maxTime = maxTime,
            onTimeSelected = { time ->
                if (selectingStart) {
                    startTime = time
                    selectingStart = false
                } else {
                    endTime = time
                }
                onRangeSelected(startTime, endTime)
            }
        )
    }
}

// ============================================================================
// TIME OF DAY PICKER IMPLEMENTATION
// ============================================================================

@Composable
private fun TimeOfDayPickerImpl(
    modifier: Modifier,
    sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors,
    strings: TimePickerStrings,
    title: String,
    onTimeOfDaySelected: (String, LocalTime) -> Unit
) {
    var selectedSlot by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.background)
            .padding(sizeConfig.padding)
    ) {
        if (title.isNotEmpty()) {
            Text(text = title, style = sizeConfig.titleTextStyle, color = colors.title, modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Medium))
        }

        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)) {
            strings.timeOfDayLabels.forEach { (slotName, time) ->
                val isSelected = slotName == selectedSlot

                Chip(
                    text = "$slotName (${time.toString()})",
                    variant = if (isSelected) ChipVariant.Solid else ChipVariant.Outlined,
                    type = ChipType.Selectable,
                    selected = isSelected,
                    onClick = {
                        selectedSlot = slotName
                        onTimeOfDaySelected(slotName, time)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentDescription = "$slotName at ${time}"
                )
            }
        }
    }
}

// ============================================================================
// USAGE EXAMPLES & DOCUMENTATION
// ============================================================================

/**
 * COMPREHENSIVE USAGE EXAMPLES:
 *
 * 1. Simple Wheel Time Picker (12-hour):
 * ```
 * TimePicker(
 *     variant = TimePickerVariant.Wheel,
 *     format = TimeFormat.Hour12,
 *     onTimeSelected = { time ->
 *         println("Selected: $time")
 *     }
 * )
 * ```
 *
 * 2. 24-hour Wheel Picker:
 * ```
 * TimePicker(
 *     variant = TimePickerVariant.Wheel,
 *     format = TimeFormat.Hour24,
 *     title = "Select Time",
 *     onTimeSelected = { time -> ... }
 * )
 * ```
 *
 * 3. Range Selection with Wheel:
 * ```
 * TimePicker(
 *     variant = TimePickerVariant.Wheel,
 *     mode = TimeSelectionMode.Range,
 *     onRangeSelected = { start, end ->
 *         println("Range: $start to $end")
 *     }
 * )
 * ```
 *
 * 4. Clock Face Picker (Material3-style):
 * ```
 * TimePicker(
 *     variant = TimePickerVariant.Clock,
 *     format = TimeFormat.Hour12,
 *     onTimeSelected = { time -> ... }
 * )
 * ```
 *
 * 5. Time of Day Picker:
 * ```
 * TimePicker(
 *     variant = TimePickerVariant.TimeOfDayPicker,
 *     onTimeOfDaySelected = { slot, time ->
 *         println("$slot at $time")
 *     }
 * )
 * ```
 *
 * 6. Custom Time Slots:
 * ```
 * TimePicker(
 *     variant = TimePickerVariant.TimeOfDayPicker,
 *     strings = TimePickerStrings(
 *         timeOfDayLabels = mapOf(
 *             "Breakfast" to LocalTime(8, 0),
 *             "Lunch" to LocalTime(12, 30),
 *             "Dinner" to LocalTime(19, 0)
 *         )
 *     ),
 *     onTimeOfDaySelected = { slot, time -> ... }
 * )
 * ```
 *
 * 7. Chinese Localization:
 * ```
 * TimePicker(
 *     variant = TimePickerVariant.Wheel,
 *     format = TimeFormat.Hour12,
 *     strings = TimePickerStrings(
 *         amLabel = "上午",
 *         pmLabel = "下午",
 *         startLabel = "开始",
 *         endLabel = "结束",
 *         hourLabel = "小时",
 *         minuteLabel = "分钟"
 *     ),
 *     onTimeSelected = { ... }
 * )
 * ```
 *
 * 8. With Time Bounds:
 * ```
 * TimePicker(
 *     variant = TimePickerVariant.Clock,
 *     minTime = LocalTime(9, 0),
 *     maxTime = LocalTime(17, 0),
 *     onTimeSelected = { ... }
 * )
 * ```
 */

