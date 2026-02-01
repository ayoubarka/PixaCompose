package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.pixamob.pixacompose.theme.BorderSize
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.ComponentSize
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.RadiusSize
import com.pixamob.pixacompose.theme.Spacing
import com.pixamob.pixacompose.utils.AnimationUtils.standardSpring
import com.pixamob.pixacompose.utils.DateTimeUtils
import com.pixamob.pixacompose.utils.DateTimeUtils.toEpochMillis
import dev.darkokoa.datetimewheelpicker.WheelDatePicker
import dev.darkokoa.datetimewheelpicker.core.WheelPickerDefaults
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.minus
import kotlinx.datetime.plus

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class DatePickerVariant {
    Wheel,
    Calendar,
    MonthDayPicker,
    WeekdayPicker,
    MonthPicker,
    DayCountPicker
}

enum class DatePickerSize { Small, Medium, Large }

enum class DateSelectionMode { Single, Multiple, Range }

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
    val cornerRadius: Dp,
    val titleTextStyle: TextStyle,
    val itemTextStyle: TextStyle,
    val dayTextStyle: TextStyle,
    val selectorShape: Shape = RoundedCornerShape(16.dp),
    val selectorBorder: BorderStroke? = null
)

@Stable
data class DatePickerStrings(
    val weekdayNames: List<String> = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"),
    val weekdayShortNames: List<String> = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
    val monthNames: List<String> = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"),
    val monthShortNames: List<String> = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
    val startLabel: String = "Start",
    val endLabel: String = "End",
    val selectLabel: String = "Select",
    val todayLabel: String = "Today",
    val repeatLabelSingular: String = "Every 1 day",
    val repeatLabelPlural: (Int) -> String = { "Every $it days" },
    val previousLabel: String = "Previous",
    val nextLabel: String = "Next",
    val increaseLabel: String = "Increase",
    val decreaseLabel: String = "Decrease",
    val dayOfMonthLabel: (Int) -> String = { "Day $it" }
)

@Stable
data class DayItemStyle(
    val shape: Shape = CircleShape,
    val selectedShape: Shape = CircleShape,
    val todayBorderWidth: Dp = 2.dp,
    val animateSelection: Boolean = true
)

@Stable
data class CalendarConfig(
    val showWeekdayHeaders: Boolean = true,
    val highlightToday: Boolean = true,
    val showAdjacentMonthDays: Boolean = false,
    val firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
    val dayItemStyle: DayItemStyle = DayItemStyle()
)

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
private fun getDatePickerSizeConfig(size: DatePickerSize): DatePickerSizeConfig {
    val typography = AppTheme.typography
    val colors = AppTheme.colors
    return when (size) {
        DatePickerSize.Small -> DatePickerSizeConfig(
            height = HierarchicalSize.Container.Massive * 3f,
            padding = HierarchicalSize.Spacing.Medium,
            cornerRadius = RadiusSize.Medium,
            titleTextStyle = typography.bodyLight,
            itemTextStyle = typography.bodyLight,
            dayTextStyle = typography.labelSmall,
            selectorShape = RoundedCornerShape(12.dp),
            selectorBorder = BorderStroke(1.5.dp, colors.baseBorderDefault)
        )
        DatePickerSize.Medium -> DatePickerSizeConfig(
            height = HierarchicalSize.Container.Massive * 3.5f,
            padding = HierarchicalSize.Spacing.Large,
            cornerRadius = RadiusSize.Medium,
            titleTextStyle = typography.bodyBold,
            itemTextStyle = typography.bodyBold,
            dayTextStyle = typography.labelMedium,
            selectorShape = RoundedCornerShape(16.dp),
            selectorBorder = BorderStroke(2.dp, colors.baseBorderDefault)
        )
        DatePickerSize.Large -> DatePickerSizeConfig(
            height = HierarchicalSize.Container.Massive * 4f,
            padding = HierarchicalSize.Spacing.Huge,
            cornerRadius = RadiusSize.Large,
            titleTextStyle = typography.titleBold,
            itemTextStyle = typography.titleRegular,
            dayTextStyle = typography.labelLarge,
            selectorShape = RoundedCornerShape(20.dp),
            selectorBorder = BorderStroke(2.5.dp, colors.baseBorderDefault)
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// HELPERS
// ════════════════════════════════════════════════════════════════════════════

private fun getDaysInMonth(year: Int, month: Month): Int = DateTimeUtils.getDaysInMonth(year, month)

private fun getDayOfWeekIndex(dayOfWeek: DayOfWeek): Int = DateTimeUtils.getDayOfWeekIndex(dayOfWeek)

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaDatePicker - Comprehensive date picker component
 *
 * A flexible date picker with multiple variants and full customization.
 * Supports single, multiple, and range selection modes.
 *
 * ## Usage Examples
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
 */
@Composable
fun PixaDatePicker(
    variant: DatePickerVariant,
    modifier: Modifier = Modifier,
    mode: DateSelectionMode = DateSelectionMode.Single,
    size: DatePickerSize = DatePickerSize.Medium,
    enabled: Boolean = true,
    colors: DatePickerColors? = null,
    strings: DatePickerStrings = DatePickerStrings(),
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
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
    onDateSelected: ((Long) -> Unit)? = null,
    onDatesSelected: ((Set<LocalDate>) -> Unit)? = null,
    onRangeSelected: ((LocalDate?, LocalDate?) -> Unit)? = null,
    onWeekdaysSelected: ((Set<Int>) -> Unit)? = null,
    onMonthsSelected: ((Set<Int>) -> Unit)? = null,
    onDayOfMonthSelected: ((Int) -> Unit)? = null,
    onDayCountSelected: ((Int) -> Unit)? = null
) {
    val themeColors = getDatePickerTheme(AppTheme.colors)
    val finalColors = colors ?: themeColors
    val sizeConfig = getDatePickerSizeConfig(size)

    val containerModifier = modifier
        .clip(RoundedCornerShape(sizeConfig.cornerRadius))
        .background(finalColors.background)
        .padding(sizeConfig.padding)

    when (variant) {
        DatePickerVariant.Wheel -> WheelDatePickerContent(
            modifier = containerModifier, mode = mode, sizeConfig = sizeConfig, colors = finalColors,
            strings = strings, enabled = enabled, wheelConfig = wheelConfig,
            initialDate = initialDate, initialStartDate = initialStartDate, initialEndDate = initialEndDate,
            onDateSelected = onDateSelected, onRangeSelected = onRangeSelected
        )
        DatePickerVariant.Calendar -> CalendarDatePickerContent(
            modifier = containerModifier, mode = mode, sizeConfig = sizeConfig, colors = finalColors,
            strings = strings, enabled = enabled, minDate = minDate, maxDate = maxDate,
            calendarConfig = calendarConfig, initialDate = initialDate, initialDates = initialDates,
            initialStartDate = initialStartDate, initialEndDate = initialEndDate,
            onDateSelected = onDateSelected, onDatesSelected = onDatesSelected, onRangeSelected = onRangeSelected
        )
        DatePickerVariant.MonthDayPicker -> MonthDayPickerContent(
            modifier = containerModifier, mode = mode, sizeConfig = sizeConfig, colors = finalColors,
            strings = strings, enabled = enabled, initialDayOfMonth = initialDayOfMonth,
            onDayOfMonthSelected = onDayOfMonthSelected
        )
        DatePickerVariant.WeekdayPicker -> WeekdayPickerContent(
            modifier = containerModifier, mode = mode, sizeConfig = sizeConfig, colors = finalColors,
            strings = strings, enabled = enabled, initialWeekdays = initialWeekdays,
            onWeekdaysSelected = onWeekdaysSelected
        )
        DatePickerVariant.MonthPicker -> MonthPickerContent(
            modifier = containerModifier, mode = mode, sizeConfig = sizeConfig, colors = finalColors,
            strings = strings, enabled = enabled, initialMonths = initialMonths,
            onMonthsSelected = onMonthsSelected
        )
        DatePickerVariant.DayCountPicker -> DayCountPickerContent(
            modifier = containerModifier, sizeConfig = sizeConfig, colors = finalColors,
            strings = strings, enabled = enabled, stepperConfig = stepperConfig,
            initialDayCount = initialDayCount, onDayCountSelected = onDayCountSelected
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// WHEEL DATE PICKER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun WheelDatePickerContent(
    modifier: Modifier, mode: DateSelectionMode, sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors, strings: DatePickerStrings, enabled: Boolean,
    wheelConfig: WheelConfig, initialDate: LocalDate?, initialStartDate: LocalDate?,
    initialEndDate: LocalDate?, onDateSelected: ((Long) -> Unit)?, onRangeSelected: ((LocalDate?, LocalDate?) -> Unit)?
) {
    if (mode == DateSelectionMode.Range) {
        RangeWheelPicker(modifier, sizeConfig, colors, strings, enabled, wheelConfig,
            initialStartDate, initialEndDate, onRangeSelected ?: { _, _ -> })
    } else {
        SingleWheelPicker(modifier, sizeConfig, colors, enabled, wheelConfig,
            initialDate, onDateSelected ?: {})
    }
}

@Composable
private fun SingleWheelPicker(
    modifier: Modifier, sizeConfig: DatePickerSizeConfig, colors: DatePickerColors,
    enabled: Boolean, wheelConfig: WheelConfig, initialDate: LocalDate?, onDateSelected: (Long) -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate ?: DateTimeUtils.now()) }

    Column(modifier = modifier.semantics { contentDescription = "Date picker" },
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().height(sizeConfig.height), contentAlignment = Alignment.Center) {
            WheelDatePicker(
                modifier = Modifier.fillMaxSize(), startDate = selectedDate, yearsRange = wheelConfig.yearsRange,
                textStyle = sizeConfig.itemTextStyle.copy(textAlign = TextAlign.Center), textColor = colors.selectedText,
                selectorProperties = WheelPickerDefaults.selectorProperties(color = colors.divider,
                    shape = sizeConfig.selectorShape, border = sizeConfig.selectorBorder)
            ) { date -> if (enabled) { selectedDate = date; onDateSelected(date.toEpochMillis()) } }
        }
    }
}

@Composable
private fun RangeWheelPicker(
    modifier: Modifier, sizeConfig: DatePickerSizeConfig, colors: DatePickerColors,
    strings: DatePickerStrings, enabled: Boolean, wheelConfig: WheelConfig,
    initialStartDate: LocalDate?, initialEndDate: LocalDate?, onRangeSelected: (LocalDate?, LocalDate?) -> Unit
) {
    var selectingStart by remember { mutableStateOf(true) }
    var startDate by remember { mutableStateOf(initialStartDate) }
    var endDate by remember { mutableStateOf(initialEndDate) }

    Column(modifier = modifier) {
        RangeSelectorRow(sizeConfig, colors, strings, startDate?.toString(), endDate?.toString(),
            selectingStart, { selectingStart = true }, { selectingStart = false })
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
        Box(modifier = Modifier.fillMaxWidth().height(sizeConfig.height), contentAlignment = Alignment.Center) {
            WheelDatePicker(
                modifier = Modifier.fillMaxSize(),
                startDate = if (selectingStart) (startDate ?: DateTimeUtils.now()) else (endDate ?: DateTimeUtils.now()),
                yearsRange = wheelConfig.yearsRange, textStyle = sizeConfig.itemTextStyle.copy(textAlign = TextAlign.Center),
                textColor = colors.selectedText,
                selectorProperties = WheelPickerDefaults.selectorProperties(color = colors.divider,
                    shape = sizeConfig.selectorShape, border = sizeConfig.selectorBorder)
            ) { date ->
                if (enabled) {
                    if (selectingStart) { startDate = date; selectingStart = false } else { endDate = date }
                    onRangeSelected(startDate, endDate)
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CALENDAR DATE PICKER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun CalendarDatePickerContent(
    modifier: Modifier, mode: DateSelectionMode, sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors, strings: DatePickerStrings, enabled: Boolean,
    minDate: LocalDate?, maxDate: LocalDate?, calendarConfig: CalendarConfig, initialDate: LocalDate?,
    initialDates: Set<LocalDate>, initialStartDate: LocalDate?, initialEndDate: LocalDate?,
    onDateSelected: ((Long) -> Unit)?, onDatesSelected: ((Set<LocalDate>) -> Unit)?,
    onRangeSelected: ((LocalDate?, LocalDate?) -> Unit)?
) {
    var currentMonth by remember { mutableStateOf(initialDate ?: DateTimeUtils.now()) }

    when (mode) {
        DateSelectionMode.Single -> {
            var selectedDate by remember { mutableStateOf(initialDate) }
            CalendarGrid(modifier, sizeConfig, colors, strings, enabled, minDate, maxDate,
                calendarConfig, currentMonth, { currentMonth = it }, selectedDate?.let { setOf(it) } ?: emptySet(),
                null, null) { date -> selectedDate = date; onDateSelected?.invoke(date.toEpochDays() * 86400000L) }
        }
        DateSelectionMode.Multiple -> {
            var selectedDates by remember { mutableStateOf(initialDates) }
            CalendarGrid(modifier, sizeConfig, colors, strings, enabled, minDate, maxDate,
                calendarConfig, currentMonth, { currentMonth = it }, selectedDates, null, null) { date ->
                selectedDates = if (date in selectedDates) selectedDates - date else selectedDates + date
                onDatesSelected?.invoke(selectedDates)
            }
        }
        DateSelectionMode.Range -> {
            var selectingStart by remember { mutableStateOf(true) }
            var startDate by remember { mutableStateOf(initialStartDate) }
            var endDate by remember { mutableStateOf(initialEndDate) }
            Column(modifier = modifier) {
                RangeSelectorRow(sizeConfig, colors, strings, startDate?.toString(), endDate?.toString(),
                    selectingStart, { selectingStart = true }, { selectingStart = false })
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                CalendarGrid(Modifier, sizeConfig, colors, strings, enabled,
                    if (!selectingStart) startDate else minDate, maxDate, calendarConfig, currentMonth,
                    { currentMonth = it }, emptySet(), startDate, endDate) { date ->
                    if (selectingStart) { startDate = date; endDate = null; selectingStart = false }
                    else { endDate = date }
                    onRangeSelected?.invoke(startDate, endDate)
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    modifier: Modifier, sizeConfig: DatePickerSizeConfig, colors: DatePickerColors,
    strings: DatePickerStrings, enabled: Boolean, minDate: LocalDate?, maxDate: LocalDate?,
    calendarConfig: CalendarConfig, currentMonth: LocalDate, onMonthChange: (LocalDate) -> Unit,
    selectedDates: Set<LocalDate>, rangeStart: LocalDate?, rangeEnd: LocalDate?, onDateClick: (LocalDate) -> Unit
) {
    Column(modifier = modifier) {
        MonthNavigationRow(sizeConfig, colors, strings, currentMonth,
            { onMonthChange(currentMonth.minus(1, DateTimeUnit.MONTH)) },
            { onMonthChange(currentMonth.plus(1, DateTimeUnit.MONTH)) })
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
        if (calendarConfig.showWeekdayHeaders) {
            WeekdayHeaderRow(sizeConfig, colors, strings)
            Spacer(modifier = Modifier.height(Spacing.Tiny))
        }
        DaysGrid(sizeConfig, colors, strings, currentMonth, enabled, minDate, maxDate, calendarConfig,
            selectedDates, rangeStart, rangeEnd, onDateClick)
    }
}

@Composable
private fun MonthNavigationRow(
    sizeConfig: DatePickerSizeConfig, colors: DatePickerColors, strings: DatePickerStrings,
    currentMonth: LocalDate, onPrevious: () -> Unit, onNext: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = HierarchicalSize.Spacing.Small),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "‹", style = TextStyle(fontSize = 24.sp, color = colors.unselectedText),
            modifier = Modifier.clickable(onClick = onPrevious).semantics { contentDescription = strings.previousLabel })
        Text(text = "${strings.monthNames.getOrElse(currentMonth.month.ordinal) { currentMonth.month.name }} ${currentMonth.year}",
            style = sizeConfig.itemTextStyle, color = colors.title, fontWeight = FontWeight.SemiBold)
        Text(text = "›", style = TextStyle(fontSize = 24.sp, color = colors.unselectedText),
            modifier = Modifier.clickable(onClick = onNext).semantics { contentDescription = strings.nextLabel })
    }
}

@Composable
private fun WeekdayHeaderRow(sizeConfig: DatePickerSizeConfig, colors: DatePickerColors, strings: DatePickerStrings) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        strings.weekdayShortNames.forEach { day ->
            Text(text = day, style = sizeConfig.dayTextStyle, color = colors.unselectedText.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun DaysGrid(
    sizeConfig: DatePickerSizeConfig, colors: DatePickerColors, strings: DatePickerStrings,
    currentMonth: LocalDate, enabled: Boolean, minDate: LocalDate?, maxDate: LocalDate?,
    calendarConfig: CalendarConfig, selectedDates: Set<LocalDate>, rangeStart: LocalDate?,
    rangeEnd: LocalDate?, onDateClick: (LocalDate) -> Unit
) {
    val daysInMonth = getDaysInMonth(currentMonth.year, currentMonth.month)
    val firstDayOfMonth = LocalDate(currentMonth.year, currentMonth.month, 1)
    val startDayOfWeek = getDayOfWeekIndex(firstDayOfMonth.dayOfWeek)
    val today = DateTimeUtils.now()

    Column {
        var dayCounter = 1 - startDayOfWeek
        repeat(6) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                repeat(7) {
                    val day = dayCounter++
                    if (day in 1..daysInMonth) {
                        val date = LocalDate(currentMonth.year, currentMonth.month, day)
                        val isSelected = date in selectedDates
                        val isToday = date == today && calendarConfig.highlightToday
                        val isEnabled = enabled && (minDate == null || date >= minDate) && (maxDate == null || date <= maxDate)
                        val isInRange = rangeStart != null && rangeEnd != null && date > rangeStart && date < rangeEnd
                        val isRangeEdge = date == rangeStart || date == rangeEnd
                        val scale by animateFloatAsState(targetValue = if (isSelected || isRangeEdge) 1.1f else 1f,
                            animationSpec = standardSpring(), label = "dayScale")

                        Box(modifier = Modifier.weight(1f).aspectRatio(1f).padding(Spacing.Micro)
                            .scale(if (calendarConfig.dayItemStyle.animateSelection) scale else 1f)
                            .clip(calendarConfig.dayItemStyle.shape)
                            .background(when {
                                isSelected || isRangeEdge -> colors.selectedBackground
                                isInRange -> colors.rangeBackground
                                isToday -> colors.todayHighlight.copy(alpha = 0.1f)
                                else -> Color.Transparent
                            })
                            .clickable(enabled = isEnabled) { onDateClick(date) }
                            .semantics { contentDescription = "$day ${strings.monthNames.getOrElse(currentMonth.month.ordinal) { currentMonth.month.name }} ${currentMonth.year}" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = day.toString(), style = sizeConfig.dayTextStyle, color = when {
                                isSelected || isRangeEdge -> colors.selectedText
                                !isEnabled -> colors.disabledText
                                isToday -> colors.todayHighlight
                                else -> colors.unselectedText
                            }, fontWeight = if (isSelected || isToday || isRangeEdge) FontWeight.Bold else FontWeight.Normal)
                        }
                    } else { Box(modifier = Modifier.weight(1f).aspectRatio(1f)) }
                }
            }
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
    var selectedDays by remember { mutableStateOf(initialDayOfMonth?.let { setOf(it) } ?: emptySet<Int>()) }
    val isMultiSelect = mode == DateSelectionMode.Multiple

    Column(modifier = modifier) {
        LazyVerticalGrid(columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
            modifier = Modifier.heightIn(max = sizeConfig.height)) {
            items((1..31).toList()) { day ->
                val isSelected = day in selectedDays
                val scale by animateFloatAsState(targetValue = if (isSelected) 1.1f else 1f,
                    animationSpec = standardSpring(), label = "dayScale")
                Box(modifier = Modifier.aspectRatio(1f).scale(scale).clip(CircleShape)
                    .background(if (isSelected) colors.selectedBackground else colors.surface)
                    .border(width = if (isSelected) BorderSize.Standard else BorderSize.Tiny,
                        color = if (isSelected) colors.selectedBackground else colors.divider, shape = CircleShape)
                    .clickable(enabled = enabled) {
                        selectedDays = if (isMultiSelect) { if (isSelected) selectedDays - day else selectedDays + day } else { setOf(day) }
                        onDayOfMonthSelected?.invoke(day)
                    }
                    .semantics { contentDescription = strings.dayOfMonthLabel(day) },
                    contentAlignment = Alignment.Center) {
                    Text(text = day.toString(), style = sizeConfig.dayTextStyle,
                        color = if (isSelected) colors.selectedText else colors.unselectedText,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
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
                    variant = if (isSelected) ChipVariant.Solid else ChipVariant.Outlined,
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

    Column(modifier = modifier) {
        LazyVerticalGrid(columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
            modifier = Modifier.heightIn(max = sizeConfig.height)) {
            items(strings.monthNames.indices.toList()) { index ->
                val isSelected = index in selectedMonths
                Box(modifier = Modifier.clip(RoundedCornerShape(RadiusSize.Medium))
                    .background(if (isSelected) colors.selectedBackground else colors.surface)
                    .border(width = if (isSelected) BorderSize.Standard else BorderSize.Tiny,
                        color = if (isSelected) colors.selectedBackground else colors.divider,
                        shape = RoundedCornerShape(RadiusSize.Medium))
                    .clickable(enabled = enabled) {
                        selectedMonths = if (isMultiSelect) { if (isSelected) selectedMonths - index else selectedMonths + index } else { setOf(index) }
                        onMonthsSelected?.invoke(selectedMonths)
                    }
                    .padding(HierarchicalSize.Spacing.Medium)
                    .semantics { contentDescription = strings.monthNames[index] },
                    contentAlignment = Alignment.Center) {
                    Text(text = strings.monthShortNames[index], style = sizeConfig.dayTextStyle,
                        color = if (isSelected) colors.selectedText else colors.unselectedText,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center)
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
    var dayCount by remember { mutableStateOf(initialDayCount.coerceIn(stepperConfig.minValue, stepperConfig.maxValue)) }

    Column(modifier = modifier.semantics { contentDescription = "Day count picker: $dayCount days" },
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = if (dayCount == 1) strings.repeatLabelSingular else strings.repeatLabelPlural(dayCount),
            style = sizeConfig.itemTextStyle.copy(fontWeight = FontWeight.Bold), color = colors.selectedText,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(RadiusSize.Medium))
                .background(colors.selectedBackground).padding(HierarchicalSize.Spacing.Large),
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {
            StepperButton("−", enabled && dayCount > stepperConfig.minValue, colors, strings.decreaseLabel) {
                dayCount = (dayCount - stepperConfig.step).coerceAtLeast(stepperConfig.minValue)
                onDayCountSelected?.invoke(dayCount)
            }
            Text(text = dayCount.toString(), style = sizeConfig.titleTextStyle.copy(
                fontWeight = FontWeight.Bold, fontSize = sizeConfig.titleTextStyle.fontSize * 1.5),
                color = colors.title, modifier = Modifier.padding(horizontal = HierarchicalSize.Spacing.Large))
            StepperButton("+", enabled && dayCount < stepperConfig.maxValue, colors, strings.increaseLabel) {
                dayCount = (dayCount + stepperConfig.step).coerceAtMost(stepperConfig.maxValue)
                onDayCountSelected?.invoke(dayCount)
            }
        }
    }
}

@Composable
private fun StepperButton(text: String, enabled: Boolean, colors: DatePickerColors,
    contentDescription: String, onClick: () -> Unit) {
    Box(modifier = Modifier.size(ComponentSize.ExtraLarge).clip(CircleShape)
        .background(if (enabled) colors.surface else colors.surface.copy(alpha = 0.3f))
        .clickable(enabled = enabled, onClick = onClick)
        .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center) {
        Text(text = text, style = TextStyle(fontSize = 24.sp,
            color = if (enabled) colors.unselectedText else colors.disabledText))
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
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = HierarchicalSize.Spacing.Small),
        horizontalArrangement = Arrangement.SpaceBetween) {
        RangeSelectorItem(strings.startLabel, startValue ?: strings.selectLabel, selectingStart,
            sizeConfig, colors, onStartClick, Modifier.weight(1f))
        RangeSelectorItem(strings.endLabel, endValue ?: strings.selectLabel, !selectingStart,
            sizeConfig, colors, onEndClick, Modifier.weight(1f))
    }
}

@Composable
private fun RangeSelectorItem(
    label: String, value: String, isSelected: Boolean, sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, style = sizeConfig.dayTextStyle, color = colors.unselectedText.copy(alpha = 0.6f))
        Text(text = value, style = sizeConfig.itemTextStyle,
            color = if (isSelected) colors.selectedText else colors.unselectedText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.clip(RoundedCornerShape(RadiusSize.Small))
                .background(if (isSelected) colors.selectedBackground else Color.Transparent)
                .padding(HierarchicalSize.Spacing.Small).clickable(onClick = onClick))
    }
}
