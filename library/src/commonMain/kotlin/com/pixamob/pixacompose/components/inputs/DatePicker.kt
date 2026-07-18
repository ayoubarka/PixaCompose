package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixamob.pixacompose.components.actions.PixaChip
import com.pixamob.pixacompose.components.actions.ChipType
import com.pixamob.pixacompose.components.actions.ChipVariant
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils.AnimatedVisibilityStandard
import com.pixamob.pixacompose.utils.AnimationUtils.standardSpring
import com.pixamob.pixacompose.utils.DateTimeUtils
import com.pixamob.pixacompose.utils.DateTimeUtils.toEpochMillis
import dev.darkokoa.datetimewheelpicker.WheelDatePicker
import dev.darkokoa.datetimewheelpicker.core.WheelPickerDefaults
import kotlinx.datetime.LocalDate

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class DatePickerVariant {
    Wheel,
    Calendar,
    HeatMap,
    MonthDayPicker,
    WeekdayPicker,
    MonthPicker,
    DayCountPicker,
    SchedulePicker
}

enum class ScheduleFrequency {
    Daily,
    Weekly,
    Monthly
}

// DateSelectionMode now lives in Calendar.kt (same package) — it is a calendar-grid
// selection concept shared by PixaCalendar and PixaDatePicker's Calendar variant.

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class DatePickerColors(
    val background: Color,
    val surface: Color,
    val selectedBackground: Color,
    val selectedText: Color,
    val unselectedText: Color,
    val divider: Color,
    val title: Color,
    val todayHighlight: Color,
    val disabledText: Color,
    val rangeBackground: Color = selectedBackground.copy(alpha = 0.2f)
)

@Immutable
@Stable
data class DatePickerSizeConfig(
    val height: Dp,
    val padding: Dp,
    /** Container/focus-outline shape, pre-resolved from AppTheme.shapes.rounded by [getDatePickerSizeConfig]. */
    val containerShape: Shape,
    val titleTextStyle: TextStyle,
    val itemTextStyle: TextStyle,
    val dayTextStyle: TextStyle,
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
data class DatePickerStrings(
    val weekdayNames: List<String> = listOf(
        "Sunday",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday"
    ),
    val weekdayShortNames: List<String> = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
    val monthNames: List<String> = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    ),
    val monthShortNames: List<String> = listOf(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
    ),
    val startLabel: String? = "Start",
    val endLabel: String? = "End",
    val selectLabel: String? = "Select",
    val todayLabel: String? = "Today",
    val repeatLabelSingular: String = "Every 1 day",
    val repeatLabelPlural: (Int) -> String = { "Every $it days" },
    val previousLabel: String? = "Previous",
    val nextLabel: String? = "Next",
    val increaseLabel: String? = "Increase",
    val decreaseLabel: String? = "Decrease",
    val dayOfMonthLabel: (Int) -> String = { "Day $it" },
    val dailyLabel: String? = "Daily",
    val weeklyLabel: String? = "Weekly",
    val monthlyLabel: String? = "Monthly",
    val everyDayLabel: String? = "Every day",
    val selectWeekdaysHint: String? = "Select weekdays",
    val selectMonthDaysHint: String? = "Select days of month",
    val headerLabel: String? = null
)

// DayItemStyle and CalendarConfig now live in Calendar.kt (same package) — they are
// calendar-grid presentation concerns owned by PixaCalendar/PixaHeatmapCalendar.

@Stable
data class WheelConfig(
    val yearsRange: IntRange = 1900..2100,
    val showDividers: Boolean = true
)

@Stable
data class StepperConfig(
    val minValue: Int = 1,
    val maxValue: Int = 365,
    val step: Int = 1
)

@Stable
data class ScheduleConfig(
    val showFrequencyTabs: Boolean = true,
    val allowMultipleWeekdays: Boolean = true,
    val allowMultipleMonthDays: Boolean = true,
    val weekdayChipStyle: WeekdayChipStyle = WeekdayChipStyle.Horizontal,
    val weekdayItemShape: Shape = CircleShape,
    val monthDayItemShape: Shape = CircleShape,
    // Raw RoundedCornerShape unavoidable: same non-composable-default constraint as
    // DatePickerSizeConfig.selectorShape above (data class defaults can't call AppTheme.shapes).
    val tabShape: Shape = RoundedCornerShape(HierarchicalSize.Radius.Medium),
    val tabContainerShape: Shape = RoundedCornerShape(HierarchicalSize.Radius.Large)
)

enum class WeekdayChipStyle {
    Horizontal,
    Vertical,
    Grid
}

@Immutable
@Stable
data class ScheduleSelection(
    val frequency: ScheduleFrequency = ScheduleFrequency.Daily,
    val selectedWeekdays: Set<Int> = emptySet(),
    val selectedMonthDays: Set<Int> = emptySet()
)

// PixaCalendarDayData now lives in Calendar.kt (same package) — it is the day-cell
// data model PixaCalendar/PixaHeatmapCalendar hand to a custom dayContent slot.

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getDatePickerTheme(colors: ColorPalette): DatePickerColors {
    return DatePickerColors(
        background = colors.baseSurfaceDefault,
        surface = colors.baseSurfaceElevated,
        selectedBackground = colors.brandSurfaceDefault,
        selectedText = colors.baseContentNegative,
        unselectedText = colors.baseContentBody,
        divider = colors.baseBorderDefault,
        title = colors.baseContentTitle,
        todayHighlight = colors.brandContentDefault,
        disabledText = colors.baseContentDisabled,
        rangeBackground = colors.brandSurfaceDefault.copy(alpha = 0.2f)
    )
}

@Composable
private fun getDatePickerSizeConfig(size: SizeVariant): DatePickerSizeConfig {
    val typography = AppTheme.typography
    val colors = AppTheme.colors
    val shapes = AppTheme.shapes
    return when (size) {
        SizeVariant.Small -> DatePickerSizeConfig(
            height = HierarchicalSize.Container.Massive * 3f,
            padding = HierarchicalSize.Spacing.Medium,
            containerShape = shapes.rounded.medium,
            titleTextStyle = typography.bodyLight,
            itemTextStyle = typography.bodyLight,
            dayTextStyle = typography.labelSmall,
            selectorShape = shapes.rounded.large,
            selectorBorder = BorderStroke(HierarchicalSize.Border.Small, colors.baseBorderDefault)
        )

        SizeVariant.Medium -> DatePickerSizeConfig(
            height = HierarchicalSize.Container.Massive * 3.5f,
            padding = HierarchicalSize.Spacing.Large,
            containerShape = shapes.rounded.medium,
            titleTextStyle = typography.bodyBold,
            itemTextStyle = typography.bodyBold,
            dayTextStyle = typography.labelMedium,
            selectorShape = shapes.rounded.extraLarge,
            selectorBorder = BorderStroke(HierarchicalSize.Border.Medium, colors.baseBorderDefault)
        )

        SizeVariant.Large -> DatePickerSizeConfig(
            height = HierarchicalSize.Container.Massive * 4f,
            padding = HierarchicalSize.Spacing.Huge,
            containerShape = shapes.rounded.large,
            titleTextStyle = typography.titleBold,
            itemTextStyle = typography.titleRegular,
            dayTextStyle = typography.labelLarge,
            selectorShape = RoundedCornerShape(20.dp), // intentional one-off between Huge(16)/Massive(24) radius tokens, no exact match
            selectorBorder = BorderStroke(
                2.5.dp,
                colors.baseBorderDefault
            ) // one-off between Border.Medium(2)/Border.Large(3), no exact match
        )

        else -> DatePickerSizeConfig(
            height = HierarchicalSize.Container.Massive * 3.5f,
            padding = HierarchicalSize.Spacing.Large,
            containerShape = shapes.rounded.medium,
            titleTextStyle = typography.bodyBold,
            itemTextStyle = typography.bodyBold,
            dayTextStyle = typography.labelMedium,
            selectorShape = shapes.rounded.extraLarge,
            selectorBorder = BorderStroke(HierarchicalSize.Border.Medium, colors.baseBorderDefault)
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// HELPERS — bridge DatePicker's own theme/strings into PixaCalendar's
// ════════════════════════════════════════════════════════════════════════════

private fun DatePickerColors.toCalendarColors(): CalendarColors = CalendarColors(
    background = background,
    surface = surface,
    selectedBackground = selectedBackground,
    selectedText = selectedText,
    unselectedText = unselectedText,
    divider = divider,
    title = title,
    todayHighlight = todayHighlight,
    disabledText = disabledText,
    rangeBackground = rangeBackground
)

private fun DatePickerStrings.toCalendarStrings(): CalendarStrings = CalendarStrings(
    weekdayNames = weekdayNames,
    weekdayShortNames = weekdayShortNames,
    monthNames = monthNames,
    previousLabel = previousLabel,
    nextLabel = nextLabel,
    startLabel = startLabel,
    endLabel = endLabel,
    selectLabel = selectLabel
)

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaDatePicker — date and date-range selection component.
 *
 * ### Selection modes
 * Single, Multiple, Range.
 *
 * ### Variants
 * Calendar, Wheel, WeekdayPicker, MonthPicker, DayCountPicker, SchedulePicker.
 *
 * ### States
 * Enabled, disabled, error.
 *
 * @sample
 *
 * ```kotlin
 * // Calendar picker with single selection
 * PixaDatePicker(
 *     variant = DatePickerVariant.Calendar,
 *     onDateSelected = { timestamp -> println("Selected: $timestamp") }
 * )
 *
 * // Multiple date selection
 * PixaDatePicker(
 *     variant = DatePickerVariant.Calendar,
 *     mode = DateSelectionMode.Multiple,
 *     initialDates = setOf(LocalDate(2024, 1, 15), LocalDate(2024, 1, 20)),
 *     onDatesSelected = { dates -> println("Selected: $dates") }
 * )
 *
 * // Range selection with initial values
 * PixaDatePicker(
 *     variant = DatePickerVariant.Calendar,
 *     mode = DateSelectionMode.Range,
 *     initialStartDate = LocalDate(2024, 1, 1),
 *     initialEndDate = LocalDate(2024, 1, 31),
 *     onRangeSelected = { start, end -> println("$start to $end") }
 * )
 *
 * // Weekday picker for recurring events
 * PixaDatePicker(
 *     variant = DatePickerVariant.WeekdayPicker,
 *     mode = DateSelectionMode.Multiple,
 *     initialWeekdays = setOf(0, 2, 4), // Sun, Tue, Thu
 *     onWeekdaysSelected = { days -> println("Days: $days") }
 * )
 *
 * // Month picker
 * PixaDatePicker(
 *     variant = DatePickerVariant.MonthPicker,
 *     mode = DateSelectionMode.Multiple,
 *     onMonthsSelected = { months -> println("Months: $months") }
 * )
 *
 * // Custom colors
 * PixaDatePicker(
 *     variant = DatePickerVariant.Calendar,
 *     colors = DatePickerColors(
 *         background = Color.White,
 *         selectedBackground = Color.Blue,
 *         selectedText = Color.White,
 *         // ... other colors
 *     ),
 *     onDateSelected = { ... }
 * )
 * ```
 *
 * @param variant Visual style variant (Calendar, Wheel, WeekdayPicker, etc.)
 * @param modifier Modifier for the picker
 * @param mode Selection mode (Single, Multiple, Range)
 * @param size Size variant (Small, Medium, Large)
 * @param enabled Whether the picker is enabled
 * @param colors Custom colors (null = use theme)
 * @param strings Localization strings
 * @param minDate Minimum selectable date
 * @param maxDate Maximum selectable date
 * @param calendarConfig Calendar-specific configuration
 * @param wheelConfig Wheel picker configuration
 * @param stepperConfig Day count stepper configuration
 * @param initialDate Initial selected date (Single mode)
 * @param initialDates Initial selected dates (Multiple mode)
 * @param initialStartDate Initial range start date
 * @param initialEndDate Initial range end date
 * @param initialWeekdays Initial selected weekdays (0-6)
 * @param initialMonths Initial selected months (0-11)
 * @param initialDayOfMonth Initial day of month (1-31)
 * @param initialDayCount Initial day count
 * @param onDateSelected Callback for single date selection
 * @param onDatesSelected Callback for multiple dates selection
 * @param onRangeSelected Callback for range selection
 * @param onWeekdaysSelected Callback for weekdays selection
 * @param onMonthsSelected Callback for months selection
 * @param onDayOfMonthSelected Callback for day of month selection
 * @param onDayCountSelected Callback for day count selection
 * @param label Field label rendered above the picker
 * @param helperText Assistive text rendered below the picker
 * @param doubleMonth (Calendar variant, Single/Range only) shows the current and next month
 *   side by side instead of one
 * @param maxRangeDays Calendar Range mode only. Once a start date is picked, dates beyond
 *   `startDate + maxRangeDays` are disabled. Null = no cap.
 * @param asField Calendar/HeatMap variants only. When true, renders a compact date-field
 *   trigger showing the current selection; tapping it expands [PixaCalendar]/[PixaHeatmapCalendar]
 *   inline below the field instead of always showing the calendar surface. Default `false`
 *   preserves the pre-existing always-visible calendar behavior.
 */
@Composable
fun PixaDatePicker(
    variant: DatePickerVariant,
    modifier: Modifier = Modifier,
    mode: DateSelectionMode = DateSelectionMode.Single,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    colors: DatePickerColors? = null,
    strings: DatePickerStrings = DatePickerStrings(),
    label: String? = null,
    helperText: String? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    maxRangeDays: Int? = null,
    doubleMonth: Boolean = false,
    asField: Boolean = false,
    calendarConfig: CalendarConfig = CalendarConfig(),
    wheelConfig: WheelConfig = WheelConfig(),
    stepperConfig: StepperConfig = StepperConfig(),
    initialDate: LocalDate? = null,
    initialDates: Set<LocalDate> = emptySet(),
    initialStartDate: LocalDate? = null,
    initialEndDate: LocalDate? = null,
    initialWeekdays: Set<Int> = emptySet(),
    initialMonths: Set<Int> = emptySet(),
    initialDayOfMonth: Int? = null,
    initialDayCount: Int = 1,
    scheduleConfig: ScheduleConfig = ScheduleConfig(),
    initialScheduleSelection: ScheduleSelection = ScheduleSelection(),
    onDateSelected: ((Long) -> Unit)? = null,
    onDatesSelected: ((Set<LocalDate>) -> Unit)? = null,
    onRangeSelected: ((LocalDate?, LocalDate?) -> Unit)? = null,
    onWeekdaysSelected: ((Set<Int>) -> Unit)? = null,
    onMonthsSelected: ((Set<Int>) -> Unit)? = null,
    onDayOfMonthSelected: ((Int) -> Unit)? = null,
    onDayCountSelected: ((Int) -> Unit)? = null,
    onScheduleSelected: ((ScheduleSelection) -> Unit)? = null,
    dayContent: (@Composable BoxScope.(PixaCalendarDayData) -> Unit)? = null
) {
    val themeColors = getDatePickerTheme(AppTheme.colors)
    val finalColors = colors ?: themeColors
    val sizeConfig = getDatePickerSizeConfig(size)

    val containerModifier = Modifier
        .clip(sizeConfig.containerShape)
        .background(finalColors.background)
        .padding(sizeConfig.padding)

    Column(modifier = modifier) {
        label?.let {
            BasicText(
                text = it,
                style = sizeConfig.titleTextStyle.copy(color = finalColors.title),
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
            )
        }

        when (variant) {
            DatePickerVariant.Wheel -> WheelDatePickerContent(
                modifier = containerModifier,
                mode = mode,
                sizeConfig = sizeConfig,
                colors = finalColors,
                strings = strings,
                enabled = enabled,
                wheelConfig = wheelConfig,
                initialDate = initialDate,
                initialStartDate = initialStartDate,
                initialEndDate = initialEndDate,
                onDateSelected = onDateSelected,
                onRangeSelected = onRangeSelected
            )

            DatePickerVariant.Calendar -> CalendarFieldOrInline(
                modifier = containerModifier,
                mode = mode,
                sizeConfig = sizeConfig,
                colors = finalColors,
                strings = strings,
                enabled = enabled,
                asField = asField,
                minDate = minDate,
                maxDate = maxDate,
                maxRangeDays = maxRangeDays,
                doubleMonth = doubleMonth,
                calendarConfig = calendarConfig,
                initialDate = initialDate,
                initialDates = initialDates,
                initialStartDate = initialStartDate,
                initialEndDate = initialEndDate,
                onDateSelected = onDateSelected,
                onDatesSelected = onDatesSelected,
                onRangeSelected = onRangeSelected,
                dayContent = dayContent
            )

            DatePickerVariant.HeatMap -> HeatMapFieldOrInline(
                modifier = containerModifier, sizeConfig = sizeConfig, colors = finalColors,
                strings = strings, enabled = enabled, asField = asField,
                minDate = minDate, maxDate = maxDate,
                calendarConfig = calendarConfig, initialDate = initialDate,
                onDateSelected = onDateSelected
            )

            DatePickerVariant.MonthDayPicker -> MonthDayPickerContent(
                modifier = containerModifier,
                mode = mode,
                sizeConfig = sizeConfig,
                colors = finalColors,
                strings = strings,
                enabled = enabled,
                initialDayOfMonth = initialDayOfMonth,
                onDayOfMonthSelected = onDayOfMonthSelected
            )

            DatePickerVariant.WeekdayPicker -> WeekdayPickerContent(
                modifier = containerModifier,
                mode = mode,
                sizeConfig = sizeConfig,
                colors = finalColors,
                strings = strings,
                enabled = enabled,
                initialWeekdays = initialWeekdays,
                onWeekdaysSelected = onWeekdaysSelected
            )

            DatePickerVariant.MonthPicker -> MonthPickerContent(
                modifier = containerModifier,
                mode = mode,
                sizeConfig = sizeConfig,
                colors = finalColors,
                strings = strings,
                enabled = enabled,
                initialMonths = initialMonths,
                onMonthsSelected = onMonthsSelected
            )

            DatePickerVariant.DayCountPicker -> DayCountPickerContent(
                modifier = containerModifier, sizeConfig = sizeConfig, colors = finalColors,
                strings = strings, enabled = enabled, stepperConfig = stepperConfig,
                initialDayCount = initialDayCount, onDayCountSelected = onDayCountSelected
            )

            DatePickerVariant.SchedulePicker -> SchedulePickerContent(
                modifier = containerModifier, sizeConfig = sizeConfig, colors = finalColors,
                strings = strings, enabled = enabled, scheduleConfig = scheduleConfig,
                initialSelection = initialScheduleSelection, onScheduleSelected = onScheduleSelected
            )
        }

        helperText?.let {
            BasicText(
                text = it,
                style = AppTheme.typography.captionRegular.copy(
                    color = finalColors.unselectedText.copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier.padding(top = HierarchicalSize.Spacing.Small)
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// WHEEL DATE PICKER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun WheelDatePickerContent(
    modifier: Modifier,
    mode: DateSelectionMode,
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    strings: DatePickerStrings,
    enabled: Boolean,
    wheelConfig: WheelConfig,
    initialDate: LocalDate?,
    initialStartDate: LocalDate?,
    initialEndDate: LocalDate?,
    onDateSelected: ((Long) -> Unit)?,
    onRangeSelected: ((LocalDate?, LocalDate?) -> Unit)?
) {
    if (mode == DateSelectionMode.Range) {
        RangeWheelPicker(
            modifier, sizeConfig, colors, strings, enabled, wheelConfig,
            initialStartDate, initialEndDate, onRangeSelected ?: { _, _ -> })
    } else {
        SingleWheelPicker(
            modifier, sizeConfig, colors, enabled, wheelConfig,
            initialDate, onDateSelected ?: {})
    }
}

@Composable
private fun SingleWheelPicker(
    modifier: Modifier,
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    enabled: Boolean,
    wheelConfig: WheelConfig,
    initialDate: LocalDate?,
    onDateSelected: (Long) -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate ?: DateTimeUtils.now()) }

    Column(
        modifier = modifier.semantics { contentDescription = "Date picker" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(sizeConfig.height),
            contentAlignment = Alignment.Center
        ) {
            WheelDatePicker(
                modifier = Modifier.fillMaxSize(),
                startDate = selectedDate,
                yearsRange = wheelConfig.yearsRange,
                textStyle = sizeConfig.itemTextStyle.copy(textAlign = TextAlign.Center),
                textColor = colors.selectedText,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    color = colors.divider,
                    shape = sizeConfig.selectorShape, border = sizeConfig.selectorBorder
                )
            ) { date ->
                if (enabled) {
                    selectedDate = date; onDateSelected(date.toEpochMillis())
                }
            }
        }
    }
}

@Composable
private fun RangeWheelPicker(
    modifier: Modifier,
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    strings: DatePickerStrings,
    enabled: Boolean,
    wheelConfig: WheelConfig,
    initialStartDate: LocalDate?,
    initialEndDate: LocalDate?,
    onRangeSelected: (LocalDate?, LocalDate?) -> Unit
) {
    var selectingStart by remember { mutableStateOf(true) }
    var startDate by remember { mutableStateOf(initialStartDate) }
    var endDate by remember { mutableStateOf(initialEndDate) }

    Column(modifier = modifier) {
        RangeSelectorRow(
            sizeConfig, colors, strings, startDate?.toString(), endDate?.toString(),
            selectingStart, { selectingStart = true }, { selectingStart = false })
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
        Box(
            modifier = Modifier.fillMaxWidth().height(sizeConfig.height),
            contentAlignment = Alignment.Center
        ) {
            WheelDatePicker(
                modifier = Modifier.fillMaxSize(),
                startDate = if (selectingStart) (startDate ?: DateTimeUtils.now()) else (endDate
                    ?: DateTimeUtils.now()),
                yearsRange = wheelConfig.yearsRange,
                textStyle = sizeConfig.itemTextStyle.copy(textAlign = TextAlign.Center),
                textColor = colors.selectedText,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    color = colors.divider,
                    shape = sizeConfig.selectorShape, border = sizeConfig.selectorBorder
                )
            ) { date ->
                if (enabled) {
                    if (selectingStart) {
                        startDate = date; selectingStart = false
                    } else {
                        endDate = date
                    }
                    onRangeSelected(startDate, endDate)
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CALENDAR / HEATMAP DELEGATION — composes PixaCalendar / PixaHeatmapCalendar
// (Calendar.kt) rather than owning any grid/month-nav logic itself.
// ════════════════════════════════════════════════════════════════════════════

private fun formatFieldDate(date: LocalDate?, strings: DatePickerStrings): String? =
    date?.let { d ->
        val monthLabel = strings.monthShortNames.getOrElse(d.month.ordinal) { d.month.name }
        "$monthLabel ${d.day}, ${d.year}"
    }

@Composable
private fun DateFieldSurface(
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    displayText: String,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(sizeConfig.containerShape)
            .background(colors.surface)
            .clickable(enabled = enabled, onClick = onToggle)
            .padding(sizeConfig.padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(text = displayText, style = sizeConfig.itemTextStyle.copy(color = colors.title))
        // Calendar glyph — same decorative-emoji precedent as SchedulePicker's Daily slot (📅).
        BasicText(
            text = "📅",
            style = TextStyle(fontSize = HierarchicalSize.Icon.Medium.value.sp, color = colors.unselectedText)
        )
    }
}

@Composable
private fun CalendarFieldOrInline(
    modifier: Modifier, mode: DateSelectionMode, sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors, strings: DatePickerStrings, enabled: Boolean, asField: Boolean,
    minDate: LocalDate?, maxDate: LocalDate?, maxRangeDays: Int?, doubleMonth: Boolean,
    calendarConfig: CalendarConfig, initialDate: LocalDate?, initialDates: Set<LocalDate>,
    initialStartDate: LocalDate?, initialEndDate: LocalDate?,
    onDateSelected: ((Long) -> Unit)?, onDatesSelected: ((Set<LocalDate>) -> Unit)?,
    onRangeSelected: ((LocalDate?, LocalDate?) -> Unit)?,
    dayContent: (@Composable BoxScope.(PixaCalendarDayData) -> Unit)?
) {
    val calendarColors = colors.toCalendarColors()
    val calendarStrings = strings.toCalendarStrings()

    if (!asField) {
        PixaCalendar(
            modifier = modifier, mode = mode, size = SizeVariant.Medium, enabled = enabled,
            colors = calendarColors, strings = calendarStrings, minDate = minDate, maxDate = maxDate,
            maxRangeDays = maxRangeDays, doubleMonth = doubleMonth, calendarConfig = calendarConfig,
            initialDate = initialDate, initialDates = initialDates,
            initialStartDate = initialStartDate, initialEndDate = initialEndDate,
            onDateSelected = { date -> onDateSelected?.invoke(date.toEpochDays() * 86400000L) },
            onDatesSelected = onDatesSelected, onRangeSelected = onRangeSelected, dayContent = dayContent
        )
        return
    }

    var expanded by remember { mutableStateOf(false) }
    var fieldDate by remember { mutableStateOf(initialDate) }
    var fieldDates by remember { mutableStateOf(initialDates) }
    var fieldStart by remember { mutableStateOf(initialStartDate) }
    var fieldEnd by remember { mutableStateOf(initialEndDate) }

    val displayText = when (mode) {
        DateSelectionMode.Range -> {
            val s = formatFieldDate(fieldStart, strings) ?: strings.startLabel ?: strings.selectLabel ?: "Select"
            val e = formatFieldDate(fieldEnd, strings) ?: strings.endLabel ?: strings.selectLabel ?: "Select"
            "$s – $e"
        }

        DateSelectionMode.Multiple -> if (fieldDates.isEmpty()) {
            strings.selectLabel ?: "Select dates"
        } else {
            "${fieldDates.size} selected"
        }

        DateSelectionMode.Single -> formatFieldDate(fieldDate, strings) ?: strings.selectLabel ?: "Select date"
    }

    Column(modifier = modifier) {
        DateFieldSurface(sizeConfig, colors, displayText, enabled) { expanded = !expanded }
        AnimatedVisibilityStandard(visible = expanded) {
            PixaCalendar(
                modifier = Modifier.padding(top = HierarchicalSize.Spacing.Small),
                mode = mode, size = SizeVariant.Medium, enabled = enabled,
                colors = calendarColors, strings = calendarStrings, minDate = minDate, maxDate = maxDate,
                maxRangeDays = maxRangeDays, doubleMonth = doubleMonth, calendarConfig = calendarConfig,
                initialDate = initialDate, initialDates = initialDates,
                initialStartDate = initialStartDate, initialEndDate = initialEndDate,
                onDateSelected = { date ->
                    fieldDate = date
                    onDateSelected?.invoke(date.toEpochDays() * 86400000L)
                },
                onDatesSelected = { dates -> fieldDates = dates; onDatesSelected?.invoke(dates) },
                onRangeSelected = { start, end ->
                    fieldStart = start; fieldEnd = end
                    onRangeSelected?.invoke(start, end)
                },
                dayContent = dayContent
            )
        }
    }
}

@Composable
private fun HeatMapFieldOrInline(
    modifier: Modifier, sizeConfig: DatePickerSizeConfig, colors: DatePickerColors,
    strings: DatePickerStrings, enabled: Boolean, asField: Boolean,
    minDate: LocalDate?, maxDate: LocalDate?, calendarConfig: CalendarConfig,
    initialDate: LocalDate?, onDateSelected: ((Long) -> Unit)?
) {
    val calendarColors = colors.toCalendarColors()
    val calendarStrings = strings.toCalendarStrings()

    if (!asField) {
        PixaHeatmapCalendar(
            modifier = modifier, size = SizeVariant.Medium, enabled = enabled,
            colors = calendarColors, strings = calendarStrings, minDate = minDate, maxDate = maxDate,
            calendarConfig = calendarConfig, initialDate = initialDate,
            onDateSelected = { date -> onDateSelected?.invoke(date.toEpochDays() * 86400000L) }
        )
        return
    }

    var expanded by remember { mutableStateOf(false) }
    var fieldDate by remember { mutableStateOf(initialDate) }
    val displayText = formatFieldDate(fieldDate, strings) ?: strings.selectLabel ?: "Select date"

    Column(modifier = modifier) {
        DateFieldSurface(sizeConfig, colors, displayText, enabled) { expanded = !expanded }
        AnimatedVisibilityStandard(visible = expanded) {
            PixaHeatmapCalendar(
                modifier = Modifier.padding(top = HierarchicalSize.Spacing.Small),
                size = SizeVariant.Medium, enabled = enabled,
                colors = calendarColors, strings = calendarStrings, minDate = minDate, maxDate = maxDate,
                calendarConfig = calendarConfig, initialDate = initialDate,
                onDateSelected = { date ->
                    fieldDate = date
                    onDateSelected?.invoke(date.toEpochDays() * 86400000L)
                }
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// MONTH DAY PICKER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun MonthDayPickerContent(
    modifier: Modifier, mode: DateSelectionMode, sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors, strings: DatePickerStrings, enabled: Boolean,
    initialDayOfMonth: Int?, onDayOfMonthSelected: ((Int) -> Unit)?
) {
    var selectedDays by remember {
        mutableStateOf(initialDayOfMonth?.let { setOf(it) } ?: emptySet<Int>())
    }
    val isMultiSelect = mode == DateSelectionMode.Multiple

    Column(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
            modifier = Modifier.heightIn(max = sizeConfig.height)
        ) {
            items((1..31).toList()) { day ->
                val isSelected = day in selectedDays
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1f,
                    animationSpec = standardSpring(), label = "dayScale"
                )
                Box(
                    modifier = Modifier.aspectRatio(1f).scale(scale).clip(CircleShape)
                    .background(if (isSelected) colors.selectedBackground else colors.surface)
                    .border(
                        width = if (isSelected) HierarchicalSize.Border.Medium else HierarchicalSize.Border.Compact,
                        color = if (isSelected) colors.selectedBackground else colors.divider,
                        shape = CircleShape
                    )
                    .clickable(enabled = enabled) {
                        selectedDays = if (isMultiSelect) {
                            if (isSelected) selectedDays - day else selectedDays + day
                        } else {
                            setOf(day)
                        }
                        onDayOfMonthSelected?.invoke(day)
                    }
                    .semantics { contentDescription = strings.dayOfMonthLabel(day) },
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = day.toString(), style = sizeConfig.dayTextStyle.copy(
                            color = if (isSelected) colors.selectedText else colors.unselectedText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// WEEKDAY PICKER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun WeekdayPickerContent(
    modifier: Modifier, mode: DateSelectionMode, sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors, strings: DatePickerStrings, enabled: Boolean,
    initialWeekdays: Set<Int>, onWeekdaysSelected: ((Set<Int>) -> Unit)?
) {
    var selectedDays by remember { mutableStateOf(initialWeekdays) }
    val isMultiSelect = mode != DateSelectionMode.Single

    Column(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)) {
            strings.weekdayNames.forEachIndexed { index, dayName ->
                val isSelected = index in selectedDays
                PixaChip(
                    text = dayName,
                    variant = if (isSelected) ChipVariant.Filled else ChipVariant.Outlined,
                    type = ChipType.Selectable,
                    selected = isSelected,
                    enabled = enabled,
                    backgroundColor = if (isSelected) colors.selectedBackground else null,
                    contentColor = if (isSelected) colors.selectedText else colors.unselectedText,
                    onClick = {
                        selectedDays = if (isMultiSelect) {
                            if (isSelected) selectedDays - index else selectedDays + index
                        } else {
                            setOf(index)
                        }
                        onWeekdaysSelected?.invoke(selectedDays)
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = sizeConfig.height / 4),
                    contentDescription = "$dayName ${if (isSelected) "selected" else "not selected"}"
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// MONTH PICKER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun MonthPickerContent(
    modifier: Modifier, mode: DateSelectionMode, sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors, strings: DatePickerStrings, enabled: Boolean,
    initialMonths: Set<Int>, onMonthsSelected: ((Set<Int>) -> Unit)?
) {
    var selectedMonths by remember { mutableStateOf(initialMonths) }
    val isMultiSelect = mode != DateSelectionMode.Single
    val monthShape = AppTheme.shapes.rounded.medium

    Column(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
            modifier = Modifier.heightIn(max = sizeConfig.height)
        ) {
            items(strings.monthNames.indices.toList()) { index ->
                val isSelected = index in selectedMonths
                Box(
                    modifier = Modifier.clip(monthShape)
                    .background(if (isSelected) colors.selectedBackground else colors.surface)
                    .border(
                        width = if (isSelected) HierarchicalSize.Border.Medium else HierarchicalSize.Border.Compact,
                        color = if (isSelected) colors.selectedBackground else colors.divider,
                        shape = monthShape
                    )
                    .clickable(enabled = enabled) {
                        selectedMonths = if (isMultiSelect) {
                            if (isSelected) selectedMonths - index else selectedMonths + index
                        } else {
                            setOf(index)
                        }
                        onMonthsSelected?.invoke(selectedMonths)
                    }
                    .padding(HierarchicalSize.Spacing.Medium)
                    .semantics { contentDescription = strings.monthNames[index] },
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = strings.monthShortNames[index], style = sizeConfig.dayTextStyle.copy(
                            color = if (isSelected) colors.selectedText else colors.unselectedText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// DAY COUNT PICKER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun DayCountPickerContent(
    modifier: Modifier, sizeConfig: DatePickerSizeConfig, colors: DatePickerColors,
    strings: DatePickerStrings, enabled: Boolean, stepperConfig: StepperConfig,
    initialDayCount: Int, onDayCountSelected: ((Int) -> Unit)?
) {
    var dayCount by remember {
        mutableStateOf(
            initialDayCount.coerceIn(
                stepperConfig.minValue,
                stepperConfig.maxValue
            )
        )
    }

    Column(
        modifier = modifier.semantics { contentDescription = "Day count picker: $dayCount days" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicText(
            text = if (dayCount == 1) strings.repeatLabelSingular else strings.repeatLabelPlural(
                dayCount
            ),
            style = sizeConfig.itemTextStyle.copy(
                fontWeight = FontWeight.Bold,
                color = colors.selectedText,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth().clip(AppTheme.shapes.rounded.medium)
                .background(colors.selectedBackground).padding(HierarchicalSize.Spacing.Large)
        )
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepperButton(
                "−",
                enabled && dayCount > stepperConfig.minValue,
                colors,
                strings.decreaseLabel ?: "Decrease"
            ) {
                dayCount = (dayCount - stepperConfig.step).coerceAtLeast(stepperConfig.minValue)
                onDayCountSelected?.invoke(dayCount)
            }
            BasicText(
                text = dayCount.toString(), style = sizeConfig.titleTextStyle.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = sizeConfig.titleTextStyle.fontSize * 1.5,
                    color = colors.title
                ),
                modifier = Modifier.padding(horizontal = HierarchicalSize.Spacing.Large)
            )
            StepperButton(
                "+",
                enabled && dayCount < stepperConfig.maxValue,
                colors,
                strings.increaseLabel ?: "Increase"
            ) {
                dayCount = (dayCount + stepperConfig.step).coerceAtMost(stepperConfig.maxValue)
                onDayCountSelected?.invoke(dayCount)
            }
        }
    }
}

@Composable
private fun StepperButton(
    text: String, enabled: Boolean, colors: DatePickerColors,
    contentDescription: String, onClick: () -> Unit
) {
    Box(
        modifier = Modifier.size(HierarchicalSize.Container.Huge).clip(CircleShape)
            .background(if (enabled) colors.surface else colors.surface.copy(alpha = 0.3f))
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        // +/- glyph, same Icon.Medium(24dp)-tracking rationale as Calendar.kt's month-nav chevrons.
        BasicText(
            text = text, style = TextStyle(
                fontSize = HierarchicalSize.Icon.Medium.value.sp,
                color = if (enabled) colors.unselectedText else colors.disabledText
            )
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SHARED COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun RangeSelectorRow(
    sizeConfig: DatePickerSizeConfig, colors: DatePickerColors, strings: DatePickerStrings,
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
    label: String, value: String, isSelected: Boolean, sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        BasicText(
            text = label,
            style = sizeConfig.dayTextStyle.copy(color = colors.unselectedText.copy(alpha = 0.6f))
        )
        BasicText(
            text = value, style = sizeConfig.itemTextStyle.copy(
                color = if (isSelected) colors.selectedText else colors.unselectedText,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ), modifier = Modifier.clip(AppTheme.shapes.rounded.small)
                .background(if (isSelected) colors.selectedBackground else Color.Transparent)
                .padding(HierarchicalSize.Spacing.Small).clickable(onClick = onClick)
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SCHEDULE PICKER (Daily / Weekly / Monthly with Multi-Select)
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun SchedulePickerContent(
    modifier: Modifier,
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    strings: DatePickerStrings,
    enabled: Boolean,
    scheduleConfig: ScheduleConfig,
    initialSelection: ScheduleSelection,
    onScheduleSelected: ((ScheduleSelection) -> Unit)?
) {
    var selectedFrequency by remember { mutableStateOf(initialSelection.frequency) }
    var selectedWeekdays by remember { mutableStateOf(initialSelection.selectedWeekdays) }
    var selectedMonthDays by remember { mutableStateOf(initialSelection.selectedMonthDays) }

    val currentSelection = ScheduleSelection(
        frequency = selectedFrequency,
        selectedWeekdays = selectedWeekdays,
        selectedMonthDays = selectedMonthDays
    )

    Column(modifier = modifier) {
        // Frequency Tab Selector
        if (scheduleConfig.showFrequencyTabs) {
            ScheduleFrequencyTabs(
                selectedFrequency = selectedFrequency,
                onFrequencySelected = { frequency ->
                    selectedFrequency = frequency
                    onScheduleSelected?.invoke(currentSelection.copy(frequency = frequency))
                },
                strings = strings,
                colors = colors,
                sizeConfig = sizeConfig,
                enabled = enabled,
                tabShape = scheduleConfig.tabShape,
                tabContainerShape = scheduleConfig.tabContainerShape
            )
            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))
        }

        // Content based on frequency
        when (selectedFrequency) {
            ScheduleFrequency.Daily -> {
                DailyScheduleContent(
                    colors = colors,
                    strings = strings,
                    sizeConfig = sizeConfig
                )
            }

            ScheduleFrequency.Weekly -> {
                WeeklyScheduleContent(
                    selectedWeekdays = selectedWeekdays,
                    onWeekdaysChanged = { weekdays ->
                        selectedWeekdays = weekdays
                        onScheduleSelected?.invoke(currentSelection.copy(selectedWeekdays = weekdays))
                    },
                    strings = strings,
                    colors = colors,
                    sizeConfig = sizeConfig,
                    enabled = enabled,
                    allowMultiple = scheduleConfig.allowMultipleWeekdays,
                    chipStyle = scheduleConfig.weekdayChipStyle,
                    itemShape = scheduleConfig.weekdayItemShape
                )
            }

            ScheduleFrequency.Monthly -> {
                MonthlyScheduleContent(
                    selectedDays = selectedMonthDays,
                    onDaysChanged = { days ->
                        selectedMonthDays = days
                        onScheduleSelected?.invoke(currentSelection.copy(selectedMonthDays = days))
                    },
                    strings = strings,
                    colors = colors,
                    sizeConfig = sizeConfig,
                    enabled = enabled,
                    allowMultiple = scheduleConfig.allowMultipleMonthDays,
                    itemShape = scheduleConfig.monthDayItemShape
                )
            }
        }
    }
}

@Composable
private fun ScheduleFrequencyTabs(
    selectedFrequency: ScheduleFrequency,
    onFrequencySelected: (ScheduleFrequency) -> Unit,
    strings: DatePickerStrings,
    colors: DatePickerColors,
    sizeConfig: DatePickerSizeConfig,
    enabled: Boolean,
    // Composable-function defaults (unlike data-class defaults above) run inside
    // the function body, so AppTheme.shapes — a @ReadOnlyComposable accessor — is safe here.
    tabShape: Shape = AppTheme.shapes.rounded.small,
    tabContainerShape: Shape = AppTheme.shapes.rounded.medium
) {
    val frequencies = listOf(
        ScheduleFrequency.Daily to strings.dailyLabel,
        ScheduleFrequency.Weekly to strings.weeklyLabel,
        ScheduleFrequency.Monthly to strings.monthlyLabel
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(tabContainerShape)
            .background(colors.surface)
            .padding(HierarchicalSize.Spacing.Compact),
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
    ) {
        frequencies.forEach { (frequency, label) ->
            val isSelected = selectedFrequency == frequency
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.95f,
                animationSpec = standardSpring(),
                label = "tabScale"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .scale(scale)
                    .clip(tabShape)
                    .background(if (isSelected) colors.selectedBackground else Color.Transparent)
                    .clickable(enabled = enabled) { onFrequencySelected(frequency) }
                    .padding(vertical = HierarchicalSize.Spacing.Medium),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = label ?: "",
                    style = sizeConfig.itemTextStyle.copy(
                        color = if (isSelected) colors.selectedText else colors.unselectedText,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Composable
private fun DailyScheduleContent(
    colors: DatePickerColors,
    strings: DatePickerStrings,
    sizeConfig: DatePickerSizeConfig
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppTheme.shapes.rounded.medium)
            .background(colors.surface)
            .padding(HierarchicalSize.Spacing.Large),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Decorative emoji glyph for the Pixa-native SchedulePicker's Daily slot (not
            // spec-defined); 32sp sits between Icon.Huge(36)/Icon.Medium(24), no exact match.
            BasicText(
                text = "📅",
                style = TextStyle(fontSize = 32.sp)
            )
            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
            strings.everyDayLabel?.let { label ->
                BasicText(
                    text = label,
                    style = sizeConfig.itemTextStyle.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.title
                    )
                )
            }
        }
    }
}

@Composable
private fun WeeklyScheduleContent(
    selectedWeekdays: Set<Int>,
    onWeekdaysChanged: (Set<Int>) -> Unit,
    strings: DatePickerStrings,
    colors: DatePickerColors,
    sizeConfig: DatePickerSizeConfig,
    enabled: Boolean,
    allowMultiple: Boolean,
    chipStyle: WeekdayChipStyle,
    itemShape: Shape = CircleShape
) {
    Column {
        strings.selectWeekdaysHint?.let { hint ->
            BasicText(
                text = hint,
                style = sizeConfig.dayTextStyle.copy(color = colors.unselectedText.copy(alpha = 0.7f)),
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
            )
        }

        when (chipStyle) {
            WeekdayChipStyle.Horizontal -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
                ) {
                    strings.weekdayShortNames.forEachIndexed { index, dayName ->
                        val isSelected = index in selectedWeekdays
                        WeekdayChipItem(
                            text = dayName.first().toString(),
                            isSelected = isSelected,
                            enabled = enabled,
                            colors = colors,
                            shape = itemShape,
                            onClick = {
                                val newSelection = if (allowMultiple) {
                                    if (isSelected) selectedWeekdays - index else selectedWeekdays + index
                                } else {
                                    setOf(index)
                                }
                                onWeekdaysChanged(newSelection)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            WeekdayChipStyle.Grid -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                    verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                    modifier = Modifier.heightIn(max = sizeConfig.height / 2)
                ) {
                    items(strings.weekdayShortNames.indices.toList()) { index ->
                        val isSelected = index in selectedWeekdays
                        WeekdayChipItem(
                            text = strings.weekdayShortNames[index],
                            isSelected = isSelected,
                            enabled = enabled,
                            colors = colors,
                            shape = itemShape,
                            onClick = {
                                val newSelection = if (allowMultiple) {
                                    if (isSelected) selectedWeekdays - index else selectedWeekdays + index
                                } else {
                                    setOf(index)
                                }
                                onWeekdaysChanged(newSelection)
                            }
                        )
                    }
                }
            }

            WeekdayChipStyle.Vertical -> {
                Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)) {
                    strings.weekdayNames.forEachIndexed { index, dayName ->
                        val isSelected = index in selectedWeekdays
                        PixaChip(
                            text = dayName,
                            variant = if (isSelected) ChipVariant.Filled else ChipVariant.Outlined,
                            type = ChipType.Selectable,
                            selected = isSelected,
                            enabled = enabled,
                            backgroundColor = if (isSelected) colors.selectedBackground else null,
                            contentColor = if (isSelected) colors.selectedText else colors.unselectedText,
                            onClick = {
                                val newSelection = if (allowMultiple) {
                                    if (isSelected) selectedWeekdays - index else selectedWeekdays + index
                                } else {
                                    setOf(index)
                                }
                                onWeekdaysChanged(newSelection)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            contentDescription = "$dayName ${if (isSelected) "selected" else "not selected"}"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekdayChipItem(
    text: String,
    isSelected: Boolean,
    enabled: Boolean,
    colors: DatePickerColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = standardSpring(),
        label = "weekdayScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .aspectRatio(1f)
            .clip(shape)
            .background(if (isSelected) colors.selectedBackground else colors.surface)
            .border(
                width = if (isSelected) HierarchicalSize.Border.Medium else HierarchicalSize.Border.Compact,
                color = if (isSelected) colors.selectedBackground else colors.divider,
                shape = shape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = text,
            style = AppTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) colors.selectedText else colors.unselectedText
            )
        )
    }
}

@Composable
private fun MonthlyScheduleContent(
    selectedDays: Set<Int>,
    onDaysChanged: (Set<Int>) -> Unit,
    strings: DatePickerStrings,
    colors: DatePickerColors,
    sizeConfig: DatePickerSizeConfig,
    enabled: Boolean,
    allowMultiple: Boolean,
    itemShape: Shape = CircleShape
) {
    Column {
        strings.selectMonthDaysHint?.let { hint ->
            BasicText(
                text = hint,
                style = sizeConfig.dayTextStyle.copy(color = colors.unselectedText.copy(alpha = 0.7f)),
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact),
            modifier = Modifier.heightIn(max = sizeConfig.height)
        ) {
            items((1..31).toList()) { day ->
                val isSelected = day in selectedDays
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1f,
                    animationSpec = standardSpring(),
                    label = "dayScale"
                )

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .scale(scale)
                        .clip(itemShape)
                        .background(if (isSelected) colors.selectedBackground else colors.surface)
                        .border(
                            width = if (isSelected) HierarchicalSize.Border.Medium else HierarchicalSize.Border.Compact,
                            color = if (isSelected) colors.selectedBackground else colors.divider,
                            shape = itemShape
                        )
                        .clickable(enabled = enabled) {
                            val newSelection = if (allowMultiple) {
                                if (isSelected) selectedDays - day else selectedDays + day
                            } else {
                                setOf(day)
                            }
                            onDaysChanged(newSelection)
                        }
                        .semantics { contentDescription = strings.dayOfMonthLabel(day) },
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = day.toString(),
                        style = sizeConfig.dayTextStyle.copy(
                            color = if (isSelected) colors.selectedText else colors.unselectedText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}




