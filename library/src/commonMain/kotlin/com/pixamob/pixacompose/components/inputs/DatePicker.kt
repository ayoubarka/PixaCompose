package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import com.pixamob.pixacompose.components.actions.Chip
import com.pixamob.pixacompose.components.actions.ChipType
import com.pixamob.pixacompose.components.actions.ChipVariant
import com.pixamob.pixacompose.components.display.Icon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.DateTimeUtils
import com.pixamob.pixacompose.utils.DateTimeUtils.toEpochMillis
import com.pixamob.pixacompose.utils.AnimationUtils.standardSpring
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.WheelPickerDefaults
import kotlinx.datetime.*

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * DatePicker Variant - Visual style and interaction pattern
 */
enum class DatePickerVariant {
    /** Wheel picker (iOS-style) - Works on all platforms */
    Wheel,
    /** Calendar grid (Material 3) - Best for Android */
    Calendar,
    /** Month day picker - Select specific day of month (1-31) */
    MonthDayPicker,
    /** Weekday picker - Multi-select days of week */
    WeekdayPicker,
    /** Day count picker - Select repeat interval (Every X days) */
    DayCountPicker
}

/**
 * DatePicker Size - Height and padding variants
 */
enum class DatePickerSize {
    /** Small size - 240dp - Compact pickers */
    Small,
    /** Medium size - 280dp - DEFAULT, standard picker */
    Medium,
    /** Large size - 320dp - Prominent pickers */
    Large
}

/**
 * Date Selection Mode
 */
enum class DateSelectionMode {
    /** Single date selection */
    Single,
    /** Date range selection (start and end dates) */
    Range
}

/**
 * DatePicker Colors - Theme-aware color configuration
 */
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
    val disabledText: Color
)

/**
 * DatePicker Size Configuration
 */
@Immutable
@Stable
data class DatePickerSizeConfig(
    val height: Dp,
    val padding: Dp,
    val cornerRadius: Dp,
    val titleTextStyle: TextStyle,
    val itemTextStyle: TextStyle,
    val dayTextStyle: TextStyle
)

/**
 * Localization strings for DatePicker
 * All text is customizable for i18n support
 */
@Stable
data class DatePickerStrings(
    val weekdayNames: List<String> = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"),
    val weekdayShortNames: List<String> = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
    val monthNames: List<String> = listOf("January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"),
    val monthShortNames: List<String> = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
    val startLabel: String = "Start",
    val endLabel: String = "End",
    val selectLabel: String = "Select",
    val todayLabel: String = "Today",
    val repeatLabelSingular: String = "Every 1 day",
    val repeatLabelPlural: (Int) -> String = { count -> "Every $count days" },
    val previousMonthLabel: String = "Previous month",
    val nextMonthLabel: String = "Next month",
    val increaseLabel: String = "Increase",
    val decreaseLabel: String = "Decrease",
    val dayOfMonthLabel: (Int) -> String = { day -> "Day $day" }
)

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get date picker colors from theme
 */
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
        disabledText = colors.baseContentDisabled
    )
}

/**
 * Get date picker size configuration
 */
@Composable
private fun getDatePickerSizeConfig(size: DatePickerSize): DatePickerSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        DatePickerSize.Small -> DatePickerSizeConfig(
            height = 240.dp,
            padding = Spacing.Medium,
            cornerRadius = RadiusSize.Medium,
            titleTextStyle = typography.bodySmall,
            itemTextStyle = typography.bodySmall,
            dayTextStyle = typography.labelSmall
        )
        DatePickerSize.Medium -> DatePickerSizeConfig(
            height = 280.dp,
            padding = Spacing.Large,
            cornerRadius = RadiusSize.Medium,
            titleTextStyle = typography.bodyLarge,
            itemTextStyle = typography.bodyLarge,
            dayTextStyle = typography.labelMedium
        )
        DatePickerSize.Large -> DatePickerSizeConfig(
            height = 320.dp,
            padding = Spacing.ExtraLarge,
            cornerRadius = RadiusSize.Large,
            titleTextStyle = typography.titleBold,
            itemTextStyle = typography.titleRegular,
            dayTextStyle = typography.labelLarge
        )
    }
}

/**
 * Helper to get days in month
 */
private fun getDaysInMonth(year: Int, month: Month): Int {
    return DateTimeUtils.getDaysInMonth(year, month)
}

/**
 * Helper to get day of week as 0-6 (0=Sunday)
 */
private fun getDayOfWeekIndex(dayOfWeek: DayOfWeek): Int {
    return DateTimeUtils.getDayOfWeekIndex(dayOfWeek)
}

// ============================================================================
// UNIFIED DATE PICKER (Main Entry Point)
// ============================================================================

/**
 * Unified DatePicker - Main composable that delegates to specific variants
 *
 * A comprehensive date picker with multiple variants and full i18n support.
 * All text is customizable via the `strings` parameter for multilingual apps.
 *
 * @param variant The picker variant to display
 * @param mode Selection mode (Single or Range)
 * @param modifier Modifier for the picker
 * @param size Size variant
 * @param title Optional title text
 * @param colors Optional custom colors (overrides theme)
 * @param strings Localization strings for i18n (defaults to English)
 * @param minDate Minimum selectable date (null = no limit)
 * @param maxDate Maximum selectable date (null = no limit)
 * @param onDateSelected Callback for single date selection (Long timestamp)
 * @param onRangeSelected Callback for range selection (start, end as LocalDate)
 * @param onWeekdaysSelected Callback for weekday selection (Set of weekday indices 0-6)
 * @param onDayCountSelected Callback for day count selection (Int)
 * @param onDayOfMonthSelected Callback for day of month selection (Int 1-31)
 *
 * @sample
 * ```
 * // Single date with Calendar
 * DatePicker(
 *     variant = DatePickerVariant.Calendar,
 *     onDateSelected = { timestamp -> ... }
 * )
 *
 * // Range selection
 * DatePicker(
 *     variant = DatePickerVariant.Calendar,
 *     mode = DateSelectionMode.Range,
 *     onRangeSelected = { start, end -> ... }
 * )
 *
 * // With French localization
 * DatePicker(
 *     variant = DatePickerVariant.Calendar,
 *     strings = DatePickerStrings(
 *         weekdayShortNames = listOf("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"),
 *         monthNames = listOf("Janvier", "Février", ...)
 *     ),
 *     onDateSelected = { ... }
 * )
 * ```
 */
@Composable
fun DatePicker(
    variant: DatePickerVariant,
    modifier: Modifier = Modifier,
    mode: DateSelectionMode = DateSelectionMode.Single,
    size: DatePickerSize = DatePickerSize.Medium,
    title: String = "",
    colors: DatePickerColors? = null,
    strings: DatePickerStrings = DatePickerStrings(),
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    // Callbacks for different variants
    onDateSelected: ((Long) -> Unit)? = null,
    onRangeSelected: ((LocalDate?, LocalDate?) -> Unit)? = null,
    onWeekdaysSelected: ((Set<Int>) -> Unit)? = null,
    onDayCountSelected: ((Int) -> Unit)? = null,
    onDayOfMonthSelected: ((Int) -> Unit)? = null
) {
    val themeColors = getDatePickerTheme(AppTheme.colors)
    val finalColors = colors ?: themeColors
    val sizeConfig = getDatePickerSizeConfig(size)

    when (variant) {
        DatePickerVariant.Wheel -> {
            if (mode == DateSelectionMode.Range) {
                // Range mode with wheel picker
                RangeWheelDatePickerImpl(
                    modifier = modifier,
                    sizeConfig = sizeConfig,
                    colors = finalColors,
                    strings = strings,
                    title = title,
                    onRangeSelected = onRangeSelected ?: { _, _ -> }
                )
            } else {
                // Single date wheel picker
                WheelDatePickerImpl(
                    modifier = modifier,
                    sizeConfig = sizeConfig,
                    colors = finalColors,
                    title = title,
                    onDateSelected = onDateSelected ?: {}
                )
            }
        }
        DatePickerVariant.Calendar -> {
            if (mode == DateSelectionMode.Range) {
                // Range mode with calendar
                RangeCalendarDatePickerImpl(
                    modifier = modifier,
                    sizeConfig = sizeConfig,
                    colors = finalColors,
                    strings = strings,
                    title = title,
                    minDate = minDate,
                    maxDate = maxDate,
                    onRangeSelected = onRangeSelected ?: { _, _ -> }
                )
            } else {
                // Single date calendar picker
                CalendarDatePickerImpl(
                    modifier = modifier,
                    sizeConfig = sizeConfig,
                    colors = finalColors,
                    strings = strings,
                    title = title,
                    minDate = minDate,
                    maxDate = maxDate,
                    onDateSelected = { date ->
                        onDateSelected?.invoke(date.toEpochDays().toLong() * 86400000)
                    }
                )
            }
        }
        DatePickerVariant.MonthDayPicker -> {
            MonthDayPickerImpl(
                modifier = modifier,
                sizeConfig = sizeConfig,
                colors = finalColors,
                strings = strings,
                title = title,
                onDaySelected = onDayOfMonthSelected ?: {}
            )
        }
        DatePickerVariant.WeekdayPicker -> {
            WeekdayPickerImpl(
                modifier = modifier,
                sizeConfig = sizeConfig,
                colors = finalColors,
                strings = strings,
                title = title,
                onWeekdaysSelected = onWeekdaysSelected ?: {}
            )
        }
        DatePickerVariant.DayCountPicker -> {
            DayCountPickerImpl(
                modifier = modifier,
                sizeConfig = sizeConfig,
                colors = finalColors,
                strings = strings,
                title = title,
                onDayCountSelected = onDayCountSelected ?: {}
            )
        }
    }
}

// ============================================================================
// WHEEL DATE PICKER IMPLEMENTATION
// ============================================================================

@Composable
private fun WheelDatePickerImpl(
    modifier: Modifier,
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    title: String,
    onDateSelected: (Long) -> Unit
) {
    var selectedDate by remember { mutableStateOf(DateTimeUtils.now()) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.background)
            .padding(sizeConfig.padding)
            .semantics {
                contentDescription = if (title.isNotEmpty()) title else "Date picker"
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = sizeConfig.titleTextStyle,
                color = colors.title,
                modifier = Modifier.padding(bottom = Spacing.Medium)
            )
        }

        WheelDatePickerView(
            modifier = Modifier.fillMaxWidth(),
            showDatePicker = false,
            hideHeader = true,
            startDate = selectedDate,
            height = sizeConfig.height,
            yearsRange = (1900..2100),
            showMonthAsNumber = false,
            selectedDateTextStyle = sizeConfig.itemTextStyle,
            defaultDateTextStyle = sizeConfig.itemTextStyle.copy(color = colors.unselectedText),
            dateTimePickerView = DateTimePickerView.BOTTOM_SHEET_VIEW,
            selectorProperties = WheelPickerDefaults.selectorProperties(
                borderColor = colors.divider
            ),
            onDateChangeListener = { date ->
                selectedDate = date
                onDateSelected(date.toEpochMillis())
            }
        )
    }
}

@Composable
private fun RangeWheelDatePickerImpl(
    modifier: Modifier,
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    strings: DatePickerStrings,
    title: String,
    onRangeSelected: (LocalDate?, LocalDate?) -> Unit
) {
    var selectingStart by remember { mutableStateOf(true) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

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
                modifier = Modifier.padding(bottom = Spacing.Small)
            )
        }

        // Range labels
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.Small),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = strings.startLabel, style = sizeConfig.dayTextStyle, color = colors.unselectedText.copy(alpha = 0.6f))
                Text(
                    text = startDate?.toString() ?: strings.selectLabel,
                    style = sizeConfig.itemTextStyle,
                    color = if (selectingStart) colors.selectedText else colors.unselectedText,
                    fontWeight = if (selectingStart) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(RadiusSize.Small))
                        .background(if (selectingStart) colors.selectedBackground else Color.Transparent)
                        .padding(Spacing.Small)
                        .clickable { selectingStart = true }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = strings.endLabel, style = sizeConfig.dayTextStyle, color = colors.unselectedText.copy(alpha = 0.6f))
                Text(
                    text = endDate?.toString() ?: strings.selectLabel,
                    style = sizeConfig.itemTextStyle,
                    color = if (!selectingStart) colors.selectedText else colors.unselectedText,
                    fontWeight = if (!selectingStart) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(RadiusSize.Small))
                        .background(if (!selectingStart) colors.selectedBackground else Color.Transparent)
                        .padding(Spacing.Small)
                        .clickable { selectingStart = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        // Wheel picker
        WheelDatePickerView(
            modifier = Modifier.fillMaxWidth(),
            showDatePicker = false,
            hideHeader = true,
            startDate = if (selectingStart) (startDate ?: DateTimeUtils.now()) else (endDate ?: DateTimeUtils.now()),
            height = sizeConfig.height,
            yearsRange = (1900..2100),
            showMonthAsNumber = false,
            selectedDateTextStyle = sizeConfig.itemTextStyle,
            defaultDateTextStyle = sizeConfig.itemTextStyle.copy(color = colors.unselectedText),
            dateTimePickerView = DateTimePickerView.BOTTOM_SHEET_VIEW,
            selectorProperties = WheelPickerDefaults.selectorProperties(borderColor = colors.divider),
            onDateChangeListener = { date ->
                if (selectingStart) {
                    startDate = date
                    selectingStart = false
                } else {
                    endDate = date
                }
                onRangeSelected(startDate, endDate)
            }
        )
    }
}

// ============================================================================
// CALENDAR DATE PICKER IMPLEMENTATION
// ============================================================================

@Composable
private fun CalendarDatePickerImpl(
    modifier: Modifier,
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    strings: DatePickerStrings,
    title: String,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentMonth by remember { mutableStateOf(DateTimeUtils.now()) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.background)
            .padding(sizeConfig.padding)
    ) {
        if (title.isNotEmpty()) {
            Text(text = title, style = sizeConfig.titleTextStyle, color = colors.title, modifier = Modifier.padding(bottom = Spacing.Small))
        }

        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.Small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = strings.previousMonthLabel,
                tint = colors.unselectedText,
                modifier = Modifier.size(IconSize.Medium).clickable {
                    currentMonth = currentMonth.minus(1, DateTimeUnit.MONTH)
                }
            )
            Text(
                text = "${strings.monthNames.getOrElse(currentMonth.month.ordinal) { currentMonth.month.name }} ${currentMonth.year}",
                style = sizeConfig.itemTextStyle,
                color = colors.title,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = strings.nextMonthLabel,
                tint = colors.unselectedText,
                modifier = Modifier.size(IconSize.Medium).clickable {
                    currentMonth = currentMonth.plus(1, DateTimeUnit.MONTH)
                }
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        // Weekday headers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            strings.weekdayShortNames.forEach { day ->
                Text(
                    text = day,
                    style = sizeConfig.dayTextStyle,
                    color = colors.unselectedText.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Tiny))

        // Calendar grid
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
                            val isSelected = date == selectedDate
                            val isToday = date == today
                            val isEnabled = (minDate == null || date >= minDate) && (maxDate == null || date <= maxDate)

                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.1f else 1f,
                                animationSpec = standardSpring(),
                                label = "dayScale"
                            )

                            Box(
                                modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp)
                                    .scale(scale)
                                    .clip(CircleShape)
                                    .background(when {
                                        isSelected -> colors.selectedBackground
                                        isToday -> colors.todayHighlight.copy(alpha = 0.1f)
                                        else -> Color.Transparent
                                    })
                                    .clickable(enabled = isEnabled) {
                                        selectedDate = date
                                        onDateSelected(date)
                                    }
                                    .semantics {
                                        contentDescription = "$day ${strings.monthNames.getOrElse(currentMonth.month.ordinal) { currentMonth.month.name }} ${currentMonth.year}"
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = sizeConfig.dayTextStyle,
                                    color = when {
                                        isSelected -> colors.selectedText
                                        !isEnabled -> colors.disabledText
                                        isToday -> colors.todayHighlight
                                        else -> colors.unselectedText
                                    },
                                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        } else {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RangeCalendarDatePickerImpl(
    modifier: Modifier,
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    strings: DatePickerStrings,
    title: String,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onRangeSelected: (LocalDate?, LocalDate?) -> Unit
) {
    var selectingStart by remember { mutableStateOf(true) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.background)
            .padding(sizeConfig.padding)
    ) {
        if (title.isNotEmpty()) {
            Text(text = title, style = sizeConfig.titleTextStyle, color = colors.title, modifier = Modifier.padding(bottom = Spacing.Small))
        }

        // Range labels
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.Small), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = strings.startLabel, style = sizeConfig.dayTextStyle, color = colors.unselectedText.copy(alpha = 0.6f))
                Text(
                    text = startDate?.toString() ?: strings.selectLabel,
                    style = sizeConfig.itemTextStyle,
                    color = if (selectingStart) colors.selectedText else colors.unselectedText,
                    fontWeight = if (selectingStart) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(RadiusSize.Small))
                        .background(if (selectingStart) colors.selectedBackground else Color.Transparent)
                        .padding(Spacing.Small)
                        .clickable { selectingStart = true }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = strings.endLabel, style = sizeConfig.dayTextStyle, color = colors.unselectedText.copy(alpha = 0.6f))
                Text(
                    text = endDate?.toString() ?: strings.selectLabel,
                    style = sizeConfig.itemTextStyle,
                    color = if (!selectingStart) colors.selectedText else colors.unselectedText,
                    fontWeight = if (!selectingStart) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(RadiusSize.Small))
                        .background(if (!selectingStart) colors.selectedBackground else Color.Transparent)
                        .padding(Spacing.Small)
                        .clickable { selectingStart = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        // Reuse calendar impl with adjusted callback
        CalendarDatePickerImpl(
            modifier = Modifier,
            sizeConfig = sizeConfig,
            colors = colors,
            strings = strings,
            title = "",
            minDate = if (!selectingStart) startDate else minDate,
            maxDate = maxDate,
            onDateSelected = { date ->
                if (selectingStart) {
                    startDate = date
                    selectingStart = false
                } else {
                    endDate = date
                }
                onRangeSelected(startDate, endDate)
            }
        )
    }
}

// ============================================================================
// MONTH DAY PICKER IMPLEMENTATION
// ============================================================================

@Composable
private fun MonthDayPickerImpl(
    modifier: Modifier,
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    strings: DatePickerStrings,
    title: String,
    onDaySelected: (Int) -> Unit
) {
    var selectedDay by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.background)
            .padding(sizeConfig.padding)
    ) {
        if (title.isNotEmpty()) {
            Text(text = title, style = sizeConfig.titleTextStyle, color = colors.title, modifier = Modifier.padding(bottom = Spacing.Medium))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Tiny),
            verticalArrangement = Arrangement.spacedBy(Spacing.Tiny),
            modifier = Modifier.heightIn(max = sizeConfig.height)
        ) {
            items((1..31).toList()) { day ->
                val isSelected = day == selectedDay
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1f,
                    animationSpec = standardSpring(),
                    label = "dayScale"
                )

                Box(
                    modifier = Modifier.aspectRatio(1f)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(if (isSelected) colors.selectedBackground else colors.surface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) colors.selectedBackground else colors.divider,
                            shape = CircleShape
                        )
                        .clickable {
                            selectedDay = day
                            onDaySelected(day)
                        }
                        .semantics {
                            contentDescription = strings.dayOfMonthLabel(day)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.toString(),
                        style = sizeConfig.dayTextStyle,
                        color = if (isSelected) colors.selectedText else colors.unselectedText,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ============================================================================
// WEEKDAY PICKER IMPLEMENTATION
// ============================================================================

@Composable
private fun WeekdayPickerImpl(
    modifier: Modifier,
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    strings: DatePickerStrings,
    title: String,
    onWeekdaysSelected: (Set<Int>) -> Unit
) {
    var selectedDays by remember { mutableStateOf(emptySet<Int>()) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.background)
            .padding(sizeConfig.padding)
    ) {
        if (title.isNotEmpty()) {
            Text(text = title, style = sizeConfig.titleTextStyle, color = colors.title, modifier = Modifier.padding(bottom = Spacing.Medium))
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
            strings.weekdayNames.forEachIndexed { index, dayName ->
                val isSelected = index in selectedDays

                Chip(
                    text = dayName,
                    variant = if (isSelected) ChipVariant.Solid else ChipVariant.Outlined,
                    type = ChipType.Selectable,
                    selected = isSelected,
                    onClick = {
                        selectedDays = if (isSelected) {
                            selectedDays - index
                        } else {
                            selectedDays + index
                        }
                        onWeekdaysSelected(selectedDays)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentDescription = "$dayName ${if (isSelected) "selected" else "not selected"}"
                )
            }
        }
    }
}

// ============================================================================
// DAY COUNT PICKER IMPLEMENTATION
// ============================================================================

@Composable
private fun DayCountPickerImpl(
    modifier: Modifier,
    sizeConfig: DatePickerSizeConfig,
    colors: DatePickerColors,
    strings: DatePickerStrings,
    title: String,
    onDayCountSelected: (Int) -> Unit,
    minDays: Int = 1,
    maxDays: Int = 365
) {
    var dayCount by remember { mutableStateOf(1) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.background)
            .padding(sizeConfig.padding)
            .semantics {
                contentDescription = "$title $dayCount ${if (dayCount == 1) "day" else "days"}"
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title.isNotEmpty()) {
            Text(text = title, style = sizeConfig.titleTextStyle, color = colors.title, modifier = Modifier.padding(bottom = Spacing.Medium))
        }

        // Display
        Text(
            text = if (dayCount == 1) strings.repeatLabelSingular else strings.repeatLabelPlural(dayCount),
            style = sizeConfig.itemTextStyle.copy(fontWeight = FontWeight.Bold),
            color = colors.selectedText,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(RadiusSize.Medium))
                .background(colors.selectedBackground)
                .padding(Spacing.Large),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.Large))

        // Stepper
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape)
                    .background(if (dayCount > minDays) colors.surface else colors.surface.copy(alpha = 0.3f))
                    .clickable(enabled = dayCount > minDays) {
                        dayCount = (dayCount - 1).coerceAtLeast(minDays)
                        onDayCountSelected(dayCount)
                    }
                    .semantics { contentDescription = strings.decreaseLabel },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = null,
                    tint = if (dayCount > minDays) colors.unselectedText else colors.disabledText,
                    modifier = Modifier.size(IconSize.Medium)
                )
            }

            Text(
                text = dayCount.toString(),
                style = sizeConfig.titleTextStyle.copy(fontWeight = FontWeight.Bold, fontSize = sizeConfig.titleTextStyle.fontSize * 1.5),
                color = colors.title,
                modifier = Modifier.padding(horizontal = Spacing.Large)
            )

            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape)
                    .background(if (dayCount < maxDays) colors.surface else colors.surface.copy(alpha = 0.3f))
                    .clickable(enabled = dayCount < maxDays) {
                        dayCount = (dayCount + 1).coerceAtMost(maxDays)
                        onDayCountSelected(dayCount)
                    }
                    .semantics { contentDescription = strings.increaseLabel },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = if (dayCount < maxDays) colors.unselectedText else colors.disabledText,
                    modifier = Modifier.size(IconSize.Medium)
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
 * 1. Simple Calendar Date Picker:
 * ```
 * DatePicker(
 *     variant = DatePickerVariant.Calendar,
 *     onDateSelected = { timestamp ->
 *         println("Selected: ${Instant.fromEpochMilliseconds(timestamp)}")
 *     }
 * )
 * ```
 *
 * 2. Range Selection with Calendar:
 * ```
 * DatePicker(
 *     variant = DatePickerVariant.Calendar,
 *     mode = DateSelectionMode.Range,
 *     onRangeSelected = { start, end ->
 *         println("Range: $start to $end")
 *     }
 * )
 * ```
 *
 * 3. Wheel Picker (iOS-style):
 * ```
 * DatePicker(
 *     variant = DatePickerVariant.Wheel,
 *     title = "Select Date",
 *     onDateSelected = { timestamp -> ... }
 * )
 * ```
 *
 * 4. Month Day Picker:
 * ```
 * DatePicker(
 *     variant = DatePickerVariant.MonthDayPicker,
 *     title = "Select Day of Month",
 *     onDayOfMonthSelected = { day ->
 *         println("Day $day selected")
 *     }
 * )
 * ```
 *
 * 5. Weekday Picker (Multi-select):
 * ```
 * DatePicker(
 *     variant = DatePickerVariant.WeekdayPicker,
 *     title = "Select Days",
 *     onWeekdaysSelected = { days ->
 *         println("Selected: ${days.joinToString()}")
 *     }
 * )
 * ```
 *
 * 6. Day Count Picker (Intervals):
 * ```
 * DatePicker(
 *     variant = DatePickerVariant.DayCountPicker,
 *     title = "Repeat Every",
 *     onDayCountSelected = { count ->
 *         println("Every $count days")
 *     }
 * )
 * ```
 *
 * 7. French Localization:
 * ```
 * DatePicker(
 *     variant = DatePickerVariant.Calendar,
 *     strings = DatePickerStrings(
 *         weekdayShortNames = listOf("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"),
 *         monthNames = listOf("Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
 *             "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"),
 *         startLabel = "Début",
 *         endLabel = "Fin",
 *         selectLabel = "Sélectionner",
 *         todayLabel = "Aujourd'hui",
 *         repeatLabelFormat = "Tous les %d jour",
 *         repeatLabelPluralFormat = "Tous les %d jours"
 *     ),
 *     onDateSelected = { ... }
 * )
 * ```
 *
 * 8. With Date Bounds:
 * ```
 * DatePicker(
 *     variant = DatePickerVariant.Calendar,
 *     minDate = LocalDate(2024, 1, 1),
 *     maxDate = LocalDate(2026, 12, 31),
 *     onDateSelected = { ... }
 * )
 * ```
 */

