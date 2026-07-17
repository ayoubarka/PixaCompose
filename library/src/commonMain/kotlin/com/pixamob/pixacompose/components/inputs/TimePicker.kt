package com.pixamob.pixacompose.components.inputs

/**
 * PixaTimePicker — PixaCompose's migration of Uber Base's "Time Picker" component.
 *
 * Source: https://base.uber.com/6d2425e9f/p/96a4c2-time-picker.md
 *
 * Purpose:
 *   Lets users select either a specific time (a point on the clock) or a
 *   duration (a length of time), via two distinct interaction models the
 *   spec documents separately.
 *
 * Anatomy / Variants (mobile, per spec — the spec's "Web Variants," Select
 *   Time Input and Timezone Input, are intentionally out of scope; see
 *   "Adaptive behavior" below):
 *   - [TimePickerVariant.Wheel] — spec's "Pinwheel": a rotating column
 *     interface (hour / minute / AM-PM) users swipe through. Backed by the
 *     third-party `dev.darkokoa.datetimewheelpicker` dependency (already
 *     approved in CLAUDE.md's dependency table) — not Material 3.
 *   - [TimePickerVariant.Stepper] — spec's "Stepper": a duration-focused
 *     +/- control. Built directly on [QuantityStepper] (same `inputs`
 *     package), which already implements this exact anatomy/behavior/focus
 *     model per its own migration from Uber Base's separate Stepper spec —
 *     a clean same-family reuse rather than a parallel implementation.
 *   - [TimePickerVariant.TimeOfDayPicker] — a Pixa-native extension (chip-based
 *     named time-slot picker) predating this migration and not defined by
 *     the spec, kept because it doesn't conflict with anything the spec
 *     requires and has no Material 3 dependency.
 *
 *   Removed: the previous `Clock` variant (a Material 3 `TimePickerDefaults`
 *   circular clock face) is gone. It matched no variant this spec defines,
 *   and — being built entirely on `androidx.compose.material3.TimePicker` —
 *   directly violated CLAUDE.md's "no Material 3 UI components" constraint.
 *   Its `minTime`/`maxTime` parameters were already dead code (never wired
 *   to any variant, verified via `grep`), so they were dropped rather than
 *   preserved; `stepperConfig` now owns duration bounds instead.
 *
 * States: Enabled (`contentPrimary` on `backgroundPrimary` — mapped to
 *   `baseContentBody`/`baseSurfaceDefault`), Focus (3px `borderAccent` outline
 *   around the *whole* control, not sub-elements — [QuantityStepper] already
 *   implements this for Stepper; Wheel gets the same treatment via an added
 *   `focusable` + border wrapper), Disabled (`contentStateDisabled`).
 *
 * Sizing: [SizeVariant] (Small/Medium/Large) — a Pixa extension; the spec's
 *   own Small/Medium pixel tables describe fixed reference mockups, not a
 *   general size ladder.
 *
 * Adaptive behavior: the spec's "Narrow fills viewport width / Wide sits in
 *   a popover pointing to the entry point" responsive rule is intentionally
 *   out of scope for [PixaTimePicker] itself — it renders inline content only.
 *   Callers building a wide/desktop entry point should wrap [PixaTimePicker]
 *   in the existing `overlay/Popover.kt` themselves; baking popover-anchoring
 *   logic into this file would duplicate that component rather than reuse it.
 *   The spec's Web-only variants (Select Time Input, Timezone Input) are
 *   likewise out of scope — this library targets Android + iOS, not web.
 *
 * Customization: variant, mode (Single/Multiple/Range), format (12h/24h),
 *   size, colors, strings, `minuteInterval` (Wheel), `stepperConfig`
 *   (Stepper's customizable unit/bounds — spec: "customizable unit increases
 *   or decreases"), `wheelConfig`.
 */

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.DateTimeUtils.to12HourFormat
import com.pixamob.pixacompose.utils.DateTimeUtils.toFormattedString
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
    Stepper,
    TimeOfDayPicker
}

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
    val disabledText: Color,
    /** Spec: "Focus: 3px `borderAccent` outline added" to Pinwheel/Stepper. */
    val focusBorder: Color
)

@Immutable
@Stable
data class TimePickerSizeConfig(
    val height: Dp,
    val padding: Dp,
    /** Container/focus-outline shape, pre-resolved from AppTheme.shapes.rounded by [getTimePickerSizeConfig]. */
    val containerShape: Shape,
    val titleTextStyle: TextStyle,
    val itemTextStyle: TextStyle,
    /**
     * Raw RoundedCornerShape (not AppTheme.shapes) is unavoidable here: default
     * parameter values are evaluated at the call site, and AppTheme.shapes is a
     * @ReadOnlyComposable accessor — this default must also compile for
     * non-composable construction of this data class. HierarchicalSize.Radius.Huge
     * still keeps it token-driven rather than a bare numeric literal.
     */
    val selectorShape: Shape = RoundedCornerShape(HierarchicalSize.Radius.Huge),
    val selectorBorder: BorderStroke? = null
)

@Stable
data class TimePickerStrings(
    val amLabel: String? = "AM",
    val pmLabel: String? = "PM",
    val startLabel: String? = "Start",
    val endLabel: String? = "End",
    val selectLabel: String? = "Select",
    val hourLabel: String? = "Hour",
    val minuteLabel: String? = "Minute",
    val headerLabel: String? = null,
    val timeOfDayLabels: Map<String, LocalTime> = mapOf(
        "Morning" to LocalTime(9, 0),
        "Afternoon" to LocalTime(14, 0),
        "Evening" to LocalTime(18, 0),
        "Night" to LocalTime(21, 0)
    ),
    /** Formats a Stepper duration (minutes) for display; null uses [defaultDurationLabel]. */
    val durationLabel: ((Int) -> String)? = null
)

@Stable
data class WheelTimeConfig(
    val showSeconds: Boolean = false,
    val showDividers: Boolean = true
)

/**
 * Stepper variant configuration. Spec: "customizable unit increases or
 * decreases" (→ [unitMinutes]) plus required min/max bounds — the spec calls
 * unlimited ranges an anti-pattern, so both bounds are non-null with sane
 * defaults rather than optional/unlimited.
 */
@Stable
data class StepperTimeConfig(
    val unitMinutes: Int = 15,
    val minDurationMinutes: Int = 0,
    /** Default 8 hours, matching the spec's own accessibility example ("1 hour. 1 of 8"). */
    val maxDurationMinutes: Int = 480
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
        disabledText = colors.baseContentDisabled,
        focusBorder = colors.accentBorderFocus
    )
}

@Composable
private fun getTimePickerSizeConfig(size: SizeVariant): TimePickerSizeConfig {
    val typography = AppTheme.typography
    val colors = AppTheme.colors
    val shapes = AppTheme.shapes
    return when (size) {
        SizeVariant.Small -> TimePickerSizeConfig(
            // Spec-driven Wheel viewport height; no HierarchicalSize category
            // (Container/Card/Image checked) has a Small/Medium/Large ladder
            // matching 280/320/360, so this stays a raw literal.
            height = 280.dp,
            padding = HierarchicalSize.Spacing.Medium,
            containerShape = shapes.rounded.medium,
            titleTextStyle = typography.bodyLight,
            itemTextStyle = typography.bodyLight,
            selectorShape = shapes.rounded.large,
            selectorBorder = BorderStroke(HierarchicalSize.Border.Small, colors.baseBorderDefault)
        )

        SizeVariant.Medium -> TimePickerSizeConfig(
            height = 320.dp, // spec-driven Wheel viewport height, see Small's note above
            padding = HierarchicalSize.Spacing.Large,
            containerShape = shapes.rounded.medium,
            titleTextStyle = typography.bodyBold,
            itemTextStyle = typography.bodyBold,
            selectorShape = shapes.rounded.extraLarge,
            selectorBorder = BorderStroke(HierarchicalSize.Border.Medium, colors.baseBorderDefault)
        )

        SizeVariant.Large -> TimePickerSizeConfig(
            height = 360.dp, // spec-driven Wheel viewport height, see Small's note above
            padding = HierarchicalSize.Spacing.Huge,
            containerShape = shapes.rounded.large,
            titleTextStyle = typography.titleBold,
            itemTextStyle = typography.titleRegular,
            selectorShape = RoundedCornerShape(20.dp), // intentional one-off between Huge(16)/Massive(24) radius tokens, no exact match
            selectorBorder = BorderStroke(2.5.dp, colors.baseBorderDefault) // one-off between Border.Medium(2)/Border.Large(3), no exact match
        )

        else -> TimePickerSizeConfig(
            height = 320.dp,
            padding = HierarchicalSize.Spacing.Large,
            containerShape = shapes.rounded.medium,
            titleTextStyle = typography.bodyBold,
            itemTextStyle = typography.bodyBold,
            selectorShape = shapes.rounded.extraLarge,
            selectorBorder = BorderStroke(HierarchicalSize.Border.Medium, colors.baseBorderDefault)
        )
    }
}

/** Default Stepper duration formatter — e.g. 90 -> "1h 30m", 45 -> "45m". */
private fun defaultDurationLabel(minutes: Int): String {
    val hours = minutes / 60
    val remainder = minutes % 60
    return when {
        hours > 0 && remainder > 0 -> "${hours}h ${remainder}m"
        hours > 0 -> "${hours}h"
        else -> "${remainder}m"
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
 * // Wheel time picker (spec: "Pinwheel")
 * PixaTimePicker(
 *     variant = TimePickerVariant.Wheel,
 *     onTimeSelected = { time -> println("Selected: $time") }
 * )
 *
 * // Stepper (duration) time picker
 * PixaTimePicker(
 *     variant = TimePickerVariant.Stepper,
 *     stepperConfig = StepperTimeConfig(unitMinutes = 15, maxDurationMinutes = 240),
 *     onDurationSelected = { minutes -> println("Duration: $minutes min") }
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
 * ```
 *
 * @param variant Visual style variant (Wheel, Stepper, TimeOfDayPicker)
 * @param modifier Modifier for the picker
 * @param mode Selection mode (Single, Multiple, Range)
 * @param size Size variant (Small, Medium, Large)
 * @param format Time format (Hour12, Hour24) — applies to Wheel
 * @param enabled Whether the picker is enabled
 * @param colors Custom colors (null = use theme)
 * @param strings Localization strings
 * @param minuteInterval Minute step interval for Wheel (1, 5, 15, 30, etc.)
 * @param wheelConfig Wheel picker configuration
 * @param stepperConfig Stepper picker configuration — unit size and duration bounds
 * @param initialTime Initial selected time (Wheel)
 * @param initialStartTime Initial range start time (Wheel)
 * @param initialEndTime Initial range end time (Wheel)
 * @param initialDurationMinutes Initial duration in minutes (Stepper)
 * @param initialStartDurationMinutes Initial range start duration in minutes (Stepper)
 * @param initialEndDurationMinutes Initial range end duration in minutes (Stepper)
 * @param initialTimeOfDay Initial selected time of day slot
 * @param onTimeSelected Callback for single time selection (Wheel)
 * @param onRangeSelected Callback for range selection (Wheel)
 * @param onDurationSelected Callback for single duration selection, in minutes (Stepper)
 * @param onDurationRangeSelected Callback for duration range selection, in minutes (Stepper)
 * @param onTimeOfDaySelected Callback for time of day selection
 */
@Composable
fun PixaTimePicker(
    variant: TimePickerVariant,
    modifier: Modifier = Modifier,
    mode: TimeSelectionMode = TimeSelectionMode.Single,
    size: SizeVariant = SizeVariant.Medium,
    format: TimeFormat = TimeFormat.Hour12,
    enabled: Boolean = true,
    colors: TimePickerColors? = null,
    strings: TimePickerStrings = TimePickerStrings(),
    minuteInterval: Int = 1,
    wheelConfig: WheelTimeConfig = WheelTimeConfig(),
    stepperConfig: StepperTimeConfig = StepperTimeConfig(),
    initialTime: LocalTime? = null,
    initialStartTime: LocalTime? = null,
    initialEndTime: LocalTime? = null,
    initialDurationMinutes: Int? = null,
    initialStartDurationMinutes: Int? = null,
    initialEndDurationMinutes: Int? = null,
    initialTimeOfDay: String? = null,
    onTimeSelected: ((LocalTime) -> Unit)? = null,
    onRangeSelected: ((LocalTime?, LocalTime?) -> Unit)? = null,
    onDurationSelected: ((Int) -> Unit)? = null,
    onDurationRangeSelected: ((Int?, Int?) -> Unit)? = null,
    onTimeOfDaySelected: ((String, LocalTime) -> Unit)? = null
) {
    val themeColors = getTimePickerTheme(AppTheme.colors)
    val finalColors = colors ?: themeColors
    val sizeConfig = getTimePickerSizeConfig(size)

    val containerModifier = modifier
        .clip(sizeConfig.containerShape)
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

        TimePickerVariant.Stepper -> StepperTimePickerContent(
            modifier = containerModifier,
            mode = mode,
            sizeConfig = sizeConfig,
            colors = finalColors,
            strings = strings,
            enabled = enabled,
            stepperConfig = stepperConfig,
            initialDurationMinutes = initialDurationMinutes,
            initialStartDurationMinutes = initialStartDurationMinutes,
            initialEndDurationMinutes = initialEndDurationMinutes,
            onDurationSelected = onDurationSelected,
            onDurationRangeSelected = onDurationRangeSelected
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

/**
 * Spec: "Focus: 3px `borderAccent` outline added" — applied here via a plain
 * `focusable` + border wrapper since the third-party `WheelTimePicker` does
 * not expose its own interaction source.
 */
@Composable
private fun SingleWheelTimePicker(
    modifier: Modifier, sizeConfig: TimePickerSizeConfig, colors: TimePickerColors,
    strings: TimePickerStrings, format: TimeFormat, enabled: Boolean,
    minuteInterval: Int, initialTime: LocalTime?, onTimeSelected: (LocalTime) -> Unit
) {
    var selectedTime by remember { mutableStateOf(initialTime ?: LocalTime(12, 0)) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusShape = sizeConfig.containerShape

    Column(
        modifier = modifier
            .focusable(enabled = enabled, interactionSource = interactionSource)
            .border(
                width = if (isFocused && enabled) HierarchicalSize.Border.Large else HierarchicalSize.Border.None,
                color = if (isFocused && enabled) colors.focusBorder else Color.Transparent,
                shape = focusShape
            )
            .semantics {
                contentDescription = if (format == TimeFormat.Hour12) "${strings.hourLabel} ${strings.minuteLabel} ${strings.amLabel}/${strings.pmLabel}"
                else "${strings.hourLabel} ${strings.minuteLabel}"
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusShape = sizeConfig.containerShape

    Column(modifier = modifier) {
        RangeSelectorRow(
            sizeConfig, colors, strings,
            startTime?.let { format.formatOrNull(it, strings) },
            endTime?.let { format.formatOrNull(it, strings) },
            selectingStart, { selectingStart = true }, { selectingStart = false })
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(sizeConfig.height)
                .focusable(enabled = enabled, interactionSource = interactionSource)
                .border(
                    width = if (isFocused && enabled) HierarchicalSize.Border.Large else HierarchicalSize.Border.None,
                    color = if (isFocused && enabled) colors.focusBorder else Color.Transparent,
                    shape = focusShape
                ),
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

private fun TimeFormat.formatOrNull(time: LocalTime, strings: TimePickerStrings): String {
    return if (this == TimeFormat.Hour24) time.toFormattedString() else time.to12HourFormat(
        amLabel = strings.amLabel ?: "AM",
        pmLabel = strings.pmLabel ?: "PM"
    )
}

// ════════════════════════════════════════════════════════════════════════════
// STEPPER TIME PICKER (spec: "Stepper" — duration-focused +/- control)
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun StepperTimePickerContent(
    modifier: Modifier,
    mode: TimeSelectionMode,
    sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors,
    strings: TimePickerStrings,
    enabled: Boolean,
    stepperConfig: StepperTimeConfig,
    initialDurationMinutes: Int?,
    initialStartDurationMinutes: Int?,
    initialEndDurationMinutes: Int?,
    onDurationSelected: ((Int) -> Unit)?,
    onDurationRangeSelected: ((Int?, Int?) -> Unit)?
) {
    when (mode) {
        TimeSelectionMode.Single, TimeSelectionMode.Multiple -> SingleStepperTimePicker(
            modifier = modifier, strings = strings, enabled = enabled,
            stepperConfig = stepperConfig, initialDurationMinutes = initialDurationMinutes,
            onDurationSelected = onDurationSelected ?: {}
        )

        TimeSelectionMode.Range -> RangeStepperTimePicker(
            modifier = modifier, sizeConfig = sizeConfig, colors = colors, strings = strings,
            enabled = enabled, stepperConfig = stepperConfig,
            initialStartDurationMinutes = initialStartDurationMinutes,
            initialEndDurationMinutes = initialEndDurationMinutes,
            onDurationRangeSelected = onDurationRangeSelected ?: { _, _ -> }
        )
    }
}

/**
 * Spec: "Tapping the +/- buttons will increase or decrease the values
 * accordingly." Delegates directly to [QuantityStepper], which already
 * implements this exact anatomy, the required min/max bounds, and the
 * spec's "focus around the whole component" outline — no need to
 * re-implement any of it here.
 */
@Composable
private fun SingleStepperTimePicker(
    modifier: Modifier,
    strings: TimePickerStrings,
    enabled: Boolean,
    stepperConfig: StepperTimeConfig,
    initialDurationMinutes: Int?,
    onDurationSelected: (Int) -> Unit
) {
    var duration by remember {
        mutableStateOf(
            (initialDurationMinutes ?: stepperConfig.minDurationMinutes)
                .coerceIn(stepperConfig.minDurationMinutes, stepperConfig.maxDurationMinutes)
        )
    }
    val labelFormatter = strings.durationLabel ?: ::defaultDurationLabel

    QuantityStepper(
        value = duration,
        onValueChange = { duration = it; onDurationSelected(it) },
        min = stepperConfig.minDurationMinutes,
        max = stepperConfig.maxDurationMinutes,
        step = stepperConfig.unitMinutes,
        modifier = modifier,
        enabled = enabled,
        variant = QuantityStepperVariant.Wide,
        valueLabel = labelFormatter,
        contentDescription = strings.headerLabel ?: "Duration"
    )
}

@Composable
private fun RangeStepperTimePicker(
    modifier: Modifier,
    sizeConfig: TimePickerSizeConfig,
    colors: TimePickerColors,
    strings: TimePickerStrings,
    enabled: Boolean,
    stepperConfig: StepperTimeConfig,
    initialStartDurationMinutes: Int?,
    initialEndDurationMinutes: Int?,
    onDurationRangeSelected: (Int?, Int?) -> Unit
) {
    var selectingStart by remember { mutableStateOf(true) }
    var startDuration by remember { mutableStateOf(initialStartDurationMinutes) }
    var endDuration by remember { mutableStateOf(initialEndDurationMinutes) }
    val labelFormatter = strings.durationLabel ?: ::defaultDurationLabel

    Column(modifier = modifier) {
        RangeSelectorRow(
            sizeConfig, colors, strings,
            startDuration?.let(labelFormatter), endDuration?.let(labelFormatter),
            selectingStart, { selectingStart = true }, { selectingStart = false }
        )
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

        val current = (if (selectingStart) startDuration else endDuration)
            ?: stepperConfig.minDurationMinutes

        QuantityStepper(
            value = current.coerceIn(stepperConfig.minDurationMinutes, stepperConfig.maxDurationMinutes),
            onValueChange = { newValue ->
                if (selectingStart) {
                    startDuration = newValue; selectingStart = false
                } else {
                    endDuration = newValue
                }
                onDurationRangeSelected(startDuration, endDuration)
            },
            min = stepperConfig.minDurationMinutes,
            max = stepperConfig.maxDurationMinutes,
            step = stepperConfig.unitMinutes,
            enabled = enabled,
            variant = QuantityStepperVariant.Wide,
            valueLabel = labelFormatter,
            contentDescription = if (selectingStart) (strings.startLabel ?: "Start") else (strings.endLabel ?: "End")
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// TIME OF DAY PICKER (Pixa extension — not defined by the spec)
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
                    text = "$slotName (${time.toFormattedString()})",
                    variant = if (isSelected) ChipVariant.Filled else ChipVariant.Outlined,
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
                    contentDescription = "$slotName at ${time.toFormattedString()}"
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
            strings.startLabel ?: "", startValue ?: (strings.selectLabel ?: ""), selectingStart,
            sizeConfig, colors, onStartClick, Modifier.weight(1f)
        )
        RangeSelectorItem(
            strings.endLabel ?: "", endValue ?: (strings.selectLabel ?: ""), !selectingStart,
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
        BasicText(
            text = label,
            style = AppTheme.typography.captionRegular.copy(color = colors.unselectedText.copy(alpha = 0.6f))
        )
        BasicText(
            text = value,
            style = sizeConfig.itemTextStyle.copy(
                color = if (isSelected) colors.selectedText else colors.unselectedText,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            modifier = Modifier
                .clip(AppTheme.shapes.rounded.small)
                .background(if (isSelected) colors.selectedBackground else Color.Transparent)
                .padding(HierarchicalSize.Spacing.Small)
                .clickable(onClick = onClick)
        )
    }
}
