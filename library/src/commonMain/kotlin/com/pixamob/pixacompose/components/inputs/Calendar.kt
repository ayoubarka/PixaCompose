package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils.standardSpring
import com.pixamob.pixacompose.utils.DateTimeUtils
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapWeekHeaderPosition
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minus
import kotlinx.datetime.plus

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/** Selection behavior shared by [PixaCalendar] and [PixaDatePicker]'s Calendar variant. */
enum class DateSelectionMode { Single, Multiple, Range }

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class CalendarColors(
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

@Stable
data class CalendarStrings(
    val weekdayNames: List<String> = listOf(
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    ),
    val weekdayShortNames: List<String> = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
    val monthNames: List<String> = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ),
    val previousLabel: String? = "Previous",
    val nextLabel: String? = "Next",
    val startLabel: String? = "Start",
    val endLabel: String? = "End",
    val selectLabel: String? = "Select"
)

@Stable
data class DayItemStyle(
    val shape: Shape = CircleShape,
    val selectedShape: Shape = CircleShape,
    val todayBorderWidth: Dp = HierarchicalSize.Border.Medium,
    val animateSelection: Boolean = true
)

@Stable
data class CalendarConfig(
    val showWeekdayHeaders: Boolean = true,
    val highlightToday: Boolean = true,
    val showAdjacentMonthDays: Boolean = false,
    val firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
    val dayItemStyle: DayItemStyle = DayItemStyle(),
    val activityDots: Map<LocalDate, List<Color>> = emptyMap(),
    val heatmapIntensity: Map<LocalDate, Float> = emptyMap()
)

@Stable
data class PixaCalendarDayData(
    val date: LocalDate,
    val dayNumber: String,
    val isCurrentMonth: Boolean,
    val isSelected: Boolean,
    val isToday: Boolean,
    val isEnabled: Boolean = true,
    val isInRange: Boolean = false,
    val isRangeEdge: Boolean = false
)

@Immutable
@Stable
data class CalendarSizeConfig(
    val padding: Dp,
    val monthLabelStyle: TextStyle,
    val dayTextStyle: TextStyle
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
internal fun getCalendarTheme(colors: ColorPalette): CalendarColors {
    return CalendarColors(
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
internal fun getCalendarSizeConfig(size: SizeVariant): CalendarSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.Small -> CalendarSizeConfig(
            padding = HierarchicalSize.Spacing.Medium,
            monthLabelStyle = typography.bodyLight,
            dayTextStyle = typography.labelSmall
        )

        SizeVariant.Large -> CalendarSizeConfig(
            padding = HierarchicalSize.Spacing.Huge,
            monthLabelStyle = typography.titleBold,
            dayTextStyle = typography.labelLarge
        )

        else -> CalendarSizeConfig(
            padding = HierarchicalSize.Spacing.Large,
            monthLabelStyle = typography.bodyBold,
            dayTextStyle = typography.labelMedium
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// HELPERS
// ════════════════════════════════════════════════════════════════════════════

private fun getDaysInMonth(year: Int, month: Month): Int = DateTimeUtils.getDaysInMonth(year, month)

private fun getDayOfWeekIndex(dayOfWeek: DayOfWeek): Int = DateTimeUtils.getDayOfWeekIndex(dayOfWeek)

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL — MONTH GRID
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun MonthNavigationRow(
    sizeConfig: CalendarSizeConfig, colors: CalendarColors, strings: CalendarStrings,
    currentMonth: LocalDate, onPrevious: () -> Unit, onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = HierarchicalSize.Spacing.Small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Chevron glyphs are BasicText characters, not PixaIcon assets, so they can't pull a
        // size straight from HierarchicalSize.Icon — but they still track it numerically
        // (Icon.Medium = 24dp) rather than using a bare literal.
        val navGlyphStyle = TextStyle(
            fontSize = HierarchicalSize.Icon.Medium.value.sp,
            color = colors.unselectedText
        )
        BasicText(
            text = "‹", style = navGlyphStyle,
            modifier = Modifier.clickable(onClick = onPrevious)
                .semantics { contentDescription = strings.previousLabel ?: "Previous" })
        BasicText(
            text = "${strings.monthNames.getOrElse(currentMonth.month.ordinal) { currentMonth.month.name }} ${currentMonth.year}",
            style = sizeConfig.monthLabelStyle.copy(
                color = colors.title,
                fontWeight = FontWeight.SemiBold
            )
        )
        BasicText(
            text = "›", style = navGlyphStyle,
            modifier = Modifier.clickable(onClick = onNext)
                .semantics { contentDescription = strings.nextLabel ?: "Next" })
    }
}

@Composable
private fun WeekdayHeaderRow(sizeConfig: CalendarSizeConfig, colors: CalendarColors, strings: CalendarStrings) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        strings.weekdayShortNames.forEach { day ->
            BasicText(
                text = day,
                style = sizeConfig.dayTextStyle.copy(
                    color = colors.unselectedText.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DaysGrid(
    sizeConfig: CalendarSizeConfig, colors: CalendarColors, strings: CalendarStrings,
    currentMonth: LocalDate, enabled: Boolean, minDate: LocalDate?, maxDate: LocalDate?,
    calendarConfig: CalendarConfig, selectedDates: Set<LocalDate>, rangeStart: LocalDate?,
    rangeEnd: LocalDate?, onDateClick: (LocalDate) -> Unit,
    dayContent: (@Composable BoxScope.(PixaCalendarDayData) -> Unit)?
) {
    val daysInMonth = getDaysInMonth(currentMonth.year, currentMonth.month)
    val firstDayOfMonth = LocalDate(currentMonth.year, currentMonth.month, 1)
    val startDayOfWeek = getDayOfWeekIndex(firstDayOfMonth.dayOfWeek)
    val today = DateTimeUtils.now()

    Column {
        var dayCounter = 1 - startDayOfWeek
        repeat(6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) {
                    val day = dayCounter++
                    if (day in 1..daysInMonth) {
                        val date = LocalDate(currentMonth.year, currentMonth.month, day)
                        val isSelected = date in selectedDates
                        val isToday = date == today && calendarConfig.highlightToday
                        val isEnabled =
                            enabled && (minDate == null || date >= minDate) && (maxDate == null || date <= maxDate)
                        val isInRange =
                            rangeStart != null && rangeEnd != null && date > rangeStart && date < rangeEnd
                        val isRangeEdge = date == rangeStart || date == rangeEnd
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected || isRangeEdge) 1.1f else 1f,
                            animationSpec = standardSpring(), label = "dayScale"
                        )

                        val dayData = PixaCalendarDayData(
                            date = date,
                            dayNumber = day.toString(),
                            isCurrentMonth = true,
                            isSelected = isSelected,
                            isToday = isToday,
                            isEnabled = isEnabled,
                            isInRange = isInRange,
                            isRangeEdge = isRangeEdge
                        )

                        Box(
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                            .padding(HierarchicalSize.Spacing.Nano)
                            .scale(if (calendarConfig.dayItemStyle.animateSelection) scale else 1f)
                            .clip(calendarConfig.dayItemStyle.shape)
                            .background(
                                when {
                                    isSelected || isRangeEdge -> colors.selectedBackground
                                    isInRange -> colors.rangeBackground
                                    isToday -> colors.todayHighlight.copy(alpha = 0.1f)
                                    else -> Color.Transparent
                                }
                            )
                            .clickable(enabled = isEnabled) { onDateClick(date) }
                            .semantics {
                                contentDescription =
                                    "$day ${strings.monthNames.getOrElse(currentMonth.month.ordinal) { currentMonth.month.name }} ${currentMonth.year}"
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayContent != null) {
                                dayContent(dayData)
                            } else {
                                BasicText(
                                    text = day.toString(), style = sizeConfig.dayTextStyle.copy(
                                        color = when {
                                            isSelected || isRangeEdge -> colors.selectedText
                                            !isEnabled -> colors.disabledText
                                            isToday -> colors.todayHighlight
                                            else -> colors.unselectedText
                                        },
                                        fontWeight = if (isSelected || isToday || isRangeEdge) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    modifier: Modifier,
    sizeConfig: CalendarSizeConfig,
    colors: CalendarColors,
    strings: CalendarStrings,
    enabled: Boolean,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    calendarConfig: CalendarConfig,
    currentMonth: LocalDate,
    onMonthChange: (LocalDate) -> Unit,
    selectedDates: Set<LocalDate>,
    rangeStart: LocalDate?,
    rangeEnd: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
    dayContent: (@Composable BoxScope.(PixaCalendarDayData) -> Unit)?
) {
    Column(modifier = modifier) {
        MonthNavigationRow(
            sizeConfig, colors, strings, currentMonth,
            { onMonthChange(currentMonth.minus(1, DateTimeUnit.MONTH)) },
            { onMonthChange(currentMonth.plus(1, DateTimeUnit.MONTH)) })
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
        if (calendarConfig.showWeekdayHeaders) {
            WeekdayHeaderRow(sizeConfig, colors, strings)
            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
        }
        DaysGrid(
            sizeConfig, colors, strings, currentMonth, enabled, minDate, maxDate, calendarConfig,
            selectedDates, rangeStart, rangeEnd, onDateClick, dayContent
        )
    }
}

@Composable
private fun RangeSelectorRow(
    sizeConfig: CalendarSizeConfig, colors: CalendarColors, strings: CalendarStrings,
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
    label: String, value: String, isSelected: Boolean, sizeConfig: CalendarSizeConfig,
    colors: CalendarColors, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        BasicText(
            text = label,
            style = sizeConfig.dayTextStyle.copy(color = colors.unselectedText.copy(alpha = 0.6f))
        )
        BasicText(
            text = value, style = sizeConfig.monthLabelStyle.copy(
                color = if (isSelected) colors.selectedText else colors.unselectedText,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ), modifier = Modifier.clip(AppTheme.shapes.rounded.small)
                .background(if (isSelected) colors.selectedBackground else Color.Transparent)
                .padding(HierarchicalSize.Spacing.Small).clickable(onClick = onClick)
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaCalendar — reusable calendar browsing/selection primitive.
 *
 * Owns everything the eBay Calendar spec describes as calendar anatomy: month
 * navigators, days header, date cells, current-date highlight, and range-edge/
 * range-fill visualization. [PixaDatePicker]'s Calendar variant composes this
 * rather than duplicating grid logic — this is the single owner of month-grid
 * rendering in the library.
 *
 * ### Selection modes
 * Single, Multiple, Range — matching the eBay spec's Default/Range picker
 * behavior: "first tap sets the starting date and a second tap sets the ending
 * date" (a third tap starts a new range via [onRangeSelected] state managed by
 * the caller or [PixaDatePicker]).
 *
 * ### Anatomy
 * Month navigation row → optional weekday header row → 6-row day grid.
 * [doubleMonth] renders two of these side by side (spec's "Double Picker").
 *
 * ### Sizing
 * `Small`/`Medium`/`Large` scale text only — the grid itself is always
 * proportional (`weight(1f)` + `aspectRatio(1f)` cells), so there is no fixed
 * cell-size ladder to pick from.
 *
 * ### Customization
 * [dayContent] overrides the default day-cell rendering entirely, receiving
 * [PixaCalendarDayData] for the cell being drawn.
 *
 * @param minDate/[maxDate] Disable dates outside the allowed range (spec: "Dates can be
 *   disabled if they are outside of the allowed time range").
 * @param maxRangeDays Range mode only. Once a start date is picked, dates beyond
 *   `startDate + maxRangeDays` are disabled. Null = no cap. Not part of the eBay
 *   spec; a Pixa-native constraint for range length.
 * @param doubleMonth Shows the current and next month side by side (Single/Range only).
 */
@Composable
fun PixaCalendar(
    modifier: Modifier = Modifier,
    mode: DateSelectionMode = DateSelectionMode.Single,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    colors: CalendarColors? = null,
    strings: CalendarStrings = CalendarStrings(),
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    maxRangeDays: Int? = null,
    doubleMonth: Boolean = false,
    calendarConfig: CalendarConfig = CalendarConfig(),
    initialDate: LocalDate? = null,
    initialDates: Set<LocalDate> = emptySet(),
    initialStartDate: LocalDate? = null,
    initialEndDate: LocalDate? = null,
    onDateSelected: ((LocalDate) -> Unit)? = null,
    onDatesSelected: ((Set<LocalDate>) -> Unit)? = null,
    onRangeSelected: ((LocalDate?, LocalDate?) -> Unit)? = null,
    dayContent: (@Composable BoxScope.(PixaCalendarDayData) -> Unit)? = null
) {
    val finalColors = colors ?: getCalendarTheme(AppTheme.colors)
    val sizeConfig = getCalendarSizeConfig(size)
    var currentMonth by remember { mutableStateOf(initialDate ?: DateTimeUtils.now()) }

    when (mode) {
        DateSelectionMode.Single -> {
            var selectedDate by remember { mutableStateOf(initialDate) }
            val selected = selectedDate?.let { setOf(it) } ?: emptySet()
            val onPick: (LocalDate) -> Unit = { date ->
                selectedDate = date; onDateSelected?.invoke(date)
            }
            if (doubleMonth) {
                Row(
                    modifier = modifier,
                    horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)
                ) {
                    CalendarGrid(
                        Modifier.weight(1f), sizeConfig, finalColors, strings, enabled,
                        minDate, maxDate, calendarConfig, currentMonth, { currentMonth = it },
                        selected, null, null, onPick, dayContent
                    )
                    CalendarGrid(
                        Modifier.weight(1f), sizeConfig, finalColors, strings, enabled,
                        minDate, maxDate, calendarConfig, currentMonth.plus(1, DateTimeUnit.MONTH),
                        { currentMonth = it.minus(1, DateTimeUnit.MONTH) },
                        selected, null, null, onPick, dayContent
                    )
                }
            } else {
                CalendarGrid(
                    modifier, sizeConfig, finalColors, strings, enabled, minDate, maxDate,
                    calendarConfig, currentMonth, { currentMonth = it }, selected, null, null,
                    onPick, dayContent
                )
            }
        }

        DateSelectionMode.Multiple -> {
            var selectedDates by remember { mutableStateOf(initialDates) }
            CalendarGrid(
                modifier, sizeConfig, finalColors, strings, enabled, minDate, maxDate,
                calendarConfig, currentMonth, { currentMonth = it }, selectedDates, null, null,
                { date ->
                    selectedDates =
                        if (date in selectedDates) selectedDates - date else selectedDates + date
                    onDatesSelected?.invoke(selectedDates)
                }, dayContent
            )
        }

        DateSelectionMode.Range -> {
            var selectingStart by remember { mutableStateOf(true) }
            var startDate by remember { mutableStateOf(initialStartDate) }
            var endDate by remember { mutableStateOf(initialEndDate) }
            // Once a start date is picked, cap the end date by disabling
            // days beyond the max stay/range rather than rejecting the selection afterwards.
            val cappedMaxDate = if (!selectingStart && startDate != null && maxRangeDays != null) {
                val cap = startDate!!.plus(maxRangeDays, DateTimeUnit.DAY)
                if (maxDate != null && maxDate < cap) maxDate else cap
            } else maxDate
            val rangeMinDate = if (!selectingStart) startDate else minDate
            val onPick: (LocalDate) -> Unit = { date ->
                if (selectingStart) {
                    startDate = date; endDate = null; selectingStart = false
                } else {
                    endDate = date
                }
                onRangeSelected?.invoke(startDate, endDate)
            }
            Column(modifier = modifier) {
                RangeSelectorRow(
                    sizeConfig, finalColors, strings, startDate?.toString(), endDate?.toString(),
                    selectingStart, { selectingStart = true }, { selectingStart = false })
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                if (doubleMonth) {
                    Row(horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)) {
                        CalendarGrid(
                            Modifier.weight(1f), sizeConfig, finalColors, strings, enabled,
                            rangeMinDate, cappedMaxDate, calendarConfig, currentMonth,
                            { currentMonth = it }, emptySet(), startDate, endDate, onPick, dayContent
                        )
                        CalendarGrid(
                            Modifier.weight(1f), sizeConfig, finalColors, strings, enabled,
                            rangeMinDate, cappedMaxDate, calendarConfig,
                            currentMonth.plus(1, DateTimeUnit.MONTH),
                            { currentMonth = it.minus(1, DateTimeUnit.MONTH) },
                            emptySet(), startDate, endDate, onPick, dayContent
                        )
                    }
                } else {
                    CalendarGrid(
                        Modifier, sizeConfig, finalColors, strings, enabled, rangeMinDate,
                        cappedMaxDate, calendarConfig, currentMonth, { currentMonth = it },
                        emptySet(), startDate, endDate, onPick, dayContent
                    )
                }
            }
        }
    }
}

/**
 * PixaHeatmapCalendar — Kizitonwose-backed activity heatmap over a date range.
 *
 * Not part of the eBay Calendar spec (no heatmap variant is described there);
 * this is a Pixa-native browsing/visualization surface, kept in the Calendar
 * family because it is fundamentally month-grid presentation, not a date-field
 * input flow. [calendarConfig]'s `activityDots`/`heatmapIntensity` maps drive
 * per-day visualization; tapping a day still reports [onDateSelected].
 *
 * ### Customization
 * Raw Kizitonwose `CalendarDay`/`YearMonth` types stay internal — the public
 * surface only exchanges Pixa's own `LocalDate`.
 */
@Composable
fun PixaHeatmapCalendar(
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    colors: CalendarColors? = null,
    strings: CalendarStrings = CalendarStrings(),
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    calendarConfig: CalendarConfig = CalendarConfig(),
    initialDate: LocalDate? = null,
    onDateSelected: ((LocalDate) -> Unit)? = null
) {
    val finalColors = colors ?: getCalendarTheme(AppTheme.colors)
    val sizeConfig = getCalendarSizeConfig(size)
    val currentDate = initialDate ?: DateTimeUtils.now()
    val rawStartMonth =
        YearMonth(minDate?.year ?: (currentDate.year - 1), minDate?.month ?: Month.JANUARY)
    val rawEndMonth =
        YearMonth(maxDate?.year ?: (currentDate.year + 1), maxDate?.month ?: Month.DECEMBER)

    // Ensure startMonth is not after endMonth
    val (startMonth, endMonth) = if (rawStartMonth > rawEndMonth) {
        rawEndMonth to rawStartMonth
    } else {
        rawStartMonth to rawEndMonth
    }

    val state = rememberHeatMapCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth(currentDate.year, currentDate.month),
        firstDayOfWeek = firstDayOfWeekFromLocale()
    )

    Column(modifier = modifier) {
        val currentMonth = state.firstVisibleMonth.yearMonth
        MonthNavigationRow(
            sizeConfig, finalColors, strings, LocalDate(currentMonth.year, currentMonth.month, 1),
            { }, { }
        )
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

        HeatMapCalendar(
            state = state,
            weekHeaderPosition = HeatMapWeekHeaderPosition.Start,
            weekHeader = {
                BasicText(
                    text = strings.weekdayShortNames.getOrElse(it.ordinal) { "" }.take(1),
                    style = sizeConfig.dayTextStyle.copy(color = finalColors.unselectedText.copy(alpha = 0.6f)),
                    modifier = Modifier.padding(end = HierarchicalSize.Spacing.Small)
                )
            },
            dayContent = { day, _ ->
                val date = LocalDate(day.date.year, day.date.month, day.date.day)
                val count = calendarConfig.heatmapIntensity[date] ?: 0f
                val dotColors = calendarConfig.activityDots[date] ?: emptyList()

                val alpha = (0.2f + (0.8f * count.coerceIn(0f, 1f))).coerceIn(0f, 1f)
                val isToday = date == DateTimeUtils.now() && calendarConfig.highlightToday

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(HierarchicalSize.Spacing.Nano)
                        .aspectRatio(1f)
                        .clip(calendarConfig.dayItemStyle.shape)
                        .background(
                            when {
                                count > 0 -> finalColors.selectedBackground.copy(alpha = alpha)
                                isToday -> finalColors.todayHighlight.copy(alpha = 0.1f)
                                else -> finalColors.surface
                            }
                        )
                        .border(
                            width = if (isToday) calendarConfig.dayItemStyle.todayBorderWidth else HierarchicalSize.Border.Compact,
                            color = if (isToday) finalColors.todayHighlight else finalColors.divider,
                            shape = calendarConfig.dayItemStyle.shape
                        )
                        .clickable(enabled = enabled) { onDateSelected?.invoke(date) },
                    verticalArrangement = Arrangement.Center
                ) {
                    if (dotColors.isEmpty() && count == 0f) {
                        BasicText(
                            text = day.date.day.toString(), style = sizeConfig.dayTextStyle.copy(
                                color = if (isToday) finalColors.todayHighlight else finalColors.unselectedText
                            )
                        )
                    } else if (dotColors.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Nano)) {
                            dotColors.take(3).forEach { color ->
                                // No HierarchicalSize category covers a sub-Icon.Nano(10dp) activity
                                // dot; 4dp is a deliberate one-off for this decorative indicator.
                                Box(
                                    modifier = Modifier.size(4.dp).clip(CircleShape)
                                        .background(color)
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}
