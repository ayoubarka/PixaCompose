package com.pixamob.pixacompose.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.inputs.*
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.utils.DateTimeUtils.toFormattedString
import com.pixamob.pixacompose.utils.DateTimeUtils.toLocalDate
import com.pixamob.pixacompose.utils.DateTimeUtils.toIsoString

/**
 * Complete Inputs Category Demo Screen
 * Displays: TextField, Checkbox, Switch, Slider, DatePicker (with SchedulePicker),
 * TimePicker, ColorPicker, Dropdown with optimal performance and clarity
 */
@Composable
fun InputsDemoScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.baseSurfaceSubtle)
    ) {
        DemoScreenHeader(title = "Inputs", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(HierarchicalSize.Padding.Medium),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)
        ) {
            // ===== TEXT FIELD DEMOS =====
            item(key = "textfield_sizes") { TextFieldSizesDemo() }
            item(key = "textfield_variants") { TextFieldVariantsDemo() }

            // ===== SELECTION CONTROLS =====
            item(key = "checkbox") { CheckboxDemo() }
            item(key = "switch") { SwitchDemo() }
            item(key = "slider") { SliderDemo() }

            // ===== SCHEDULE PICKER (NEW - Daily/Weekly/Monthly) =====
            item(key = "schedule_picker") { SchedulePickerDemo() }

            // ===== DATE PICKER VARIANTS =====
            item(key = "datepicker_calendar") { CalendarDatePickerDemo() }
            item(key = "datepicker_weekday") { WeekdayPickerDemo() }
            item(key = "datepicker_monthday") { MonthDayPickerDemo() }

            // ===== TIME PICKER =====
            item(key = "timepicker") { TimePickerDemo() }

            // ===== COLOR PICKER =====
            item(key = "colorpicker") { ColorPickerDemo() }

            // ===== DROPDOWN =====
            item(key = "dropdown") { DropdownDemo() }

            // Footer spacing
            item(key = "footer") { Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large)) }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// TEXT FIELD DEMOS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun TextFieldSizesDemo() {
    var textValue by remember { mutableStateOf("Sample text") }

    DemoSection(
        title = "TextField - Sizes",
        description = "Small (36dp), Medium (44dp), Large (52dp)"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
            TextFieldSize.entries.forEach { size ->
                PixaTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = "${size.name} TextField",
                    size = size,
                    variant = TextFieldVariant.Outlined,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TextFieldVariantsDemo() {
    var textValue by remember { mutableStateOf("Sample text") }

    DemoSection(
        title = "TextField - Variants",
        description = "Filled, Outlined, Ghost styles"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
            TextFieldVariant.entries.forEach { variant ->
                DemoLabel(text = variant.name)
                PixaTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = "${variant.name} TextField",
                    size = TextFieldSize.Medium,
                    variant = variant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SELECTION CONTROLS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun CheckboxDemo() {
    var checkboxState by remember { mutableStateOf(false) }

    DemoSection(
        title = "Checkbox - All Sizes",
        description = "Small, Medium, Large with labels"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
            CheckboxSize.entries.forEach { size ->
                PixaCheckbox(
                    checked = checkboxState,
                    onCheckedChange = { checkboxState = it },
                    label = "${size.name} Checkbox",
                    size = size
                )
            }
        }
    }
}

@Composable
private fun SwitchDemo() {
    var switchState by remember { mutableStateOf(false) }

    DemoSection(
        title = "Switch - All Sizes",
        description = "Small, Medium, Large with labels"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
            SwitchSize.entries.forEach { size ->
                PixaSwitch(
                    checked = switchState,
                    onCheckedChange = { switchState = it },
                    label = "${size.name} Switch",
                    size = size
                )
            }
        }
    }
}

@Composable
private fun SliderDemo() {
    var sliderValue by remember { mutableFloatStateOf(50f) }

    DemoSection(
        title = "Slider - Sizes & Variants",
        description = "Different sizes with Filled, Outlined, Minimal variants"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)) {
            // Sizes
            SliderSize.entries.forEach { size ->
                DemoLabel(text = "${size.name} Slider")
                PixaSlider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    size = size,
                    variant = SliderVariant.Filled,
                    showValue = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

            // Variants
            SliderVariant.entries.forEach { variant ->
                DemoLabel(text = "${variant.name} Variant")
                PixaSlider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    size = SliderSize.Medium,
                    variant = variant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SCHEDULE PICKER (NEW - Daily / Weekly / Monthly with Multi-Select)
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun SchedulePickerDemo() {
    var scheduleSelection by remember {
        mutableStateOf(
            ScheduleSelection(
                frequency = ScheduleFrequency.Daily,
                selectedWeekdays = emptySet(),
                selectedMonthDays = emptySet()
            )
        )
    }

    DemoSection(
        title = "Schedule Picker",
        description = "Daily / Weekly / Monthly with multi-select support"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
            PixaDatePicker(
                variant = DatePickerVariant.SchedulePicker,
                size = DatePickerSize.Medium,
                scheduleConfig = ScheduleConfig(
                    showFrequencyTabs = true,
                    allowMultipleWeekdays = true,
                    allowMultipleMonthDays = true,
                    weekdayChipStyle = WeekdayChipStyle.Horizontal
                ),
                initialScheduleSelection = scheduleSelection,
                onScheduleSelected = { selection ->
                    scheduleSelection = selection
                }
            )

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

            // Display current selection
            val selectionText = when (scheduleSelection.frequency) {
                ScheduleFrequency.Daily -> "Every day"
                ScheduleFrequency.Weekly -> {
                    val days = scheduleSelection.selectedWeekdays
                        .sorted()
                        .mapNotNull { index ->
                            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").getOrNull(index)
                        }
                    if (days.isEmpty()) "Select weekdays" else days.joinToString(", ")
                }
                ScheduleFrequency.Monthly -> {
                    val days = scheduleSelection.selectedMonthDays.sorted()
                    if (days.isEmpty()) "Select days" else "Days: ${days.joinToString(", ")}"
                }
            }
            Text(
                text = "Selection: $selectionText",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

            // Custom Shapes Demo
            DemoLabel(text = "With Custom Shapes (Rounded Rectangle)")
            var customShapeSelection by remember {
                mutableStateOf(ScheduleSelection(frequency = ScheduleFrequency.Weekly))
            }
            PixaDatePicker(
                variant = DatePickerVariant.SchedulePicker,
                size = DatePickerSize.Medium,
                scheduleConfig = ScheduleConfig(
                    showFrequencyTabs = true,
                    allowMultipleWeekdays = true,
                    allowMultipleMonthDays = true,
                    weekdayChipStyle = WeekdayChipStyle.Horizontal,
                    weekdayItemShape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    monthDayItemShape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                    tabShape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    tabContainerShape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ),
                initialScheduleSelection = customShapeSelection,
                onScheduleSelected = { selection ->
                    customShapeSelection = selection
                }
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// DATE PICKER VARIANTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun CalendarDatePickerDemo() {
    var selectedDate by remember { mutableStateOf("Select a date") }

    DemoSection(
        title = "Calendar Date Picker",
        description = "Single and multi-select calendar"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)) {
            DemoLabel(text = "Single Selection")
            PixaDatePicker(
                variant = DatePickerVariant.Calendar,
                mode = DateSelectionMode.Single,
                size = DatePickerSize.Medium,
                onDateSelected = { epochMillis ->
                    selectedDate = epochMillis.toLocalDate().toIsoString()
                }
            )
            Text(
                text = "Selected: $selectedDate",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

            DemoLabel(text = "Date Range Selection")
            var rangeText by remember { mutableStateOf("Select date range") }
            PixaDatePicker(
                variant = DatePickerVariant.Calendar,
                mode = DateSelectionMode.Range,
                size = DatePickerSize.Medium,
                onRangeSelected = { start, end ->
                    rangeText = if (start != null && end != null) {
                        "${start.toIsoString()} to ${end.toIsoString()}"
                    } else {
                        "Select date range"
                    }
                }
            )
            Text(
                text = rangeText,
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )
        }
    }
}

@Composable
private fun WeekdayPickerDemo() {
    var selectedWeekdays by remember { mutableStateOf<Set<Int>>(emptySet()) }

    DemoSection(
        title = "Weekday Picker",
        description = "Multi-select weekdays for recurring events"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
            PixaDatePicker(
                variant = DatePickerVariant.WeekdayPicker,
                mode = DateSelectionMode.Multiple,
                size = DatePickerSize.Medium,
                initialWeekdays = selectedWeekdays,
                onWeekdaysSelected = { days ->
                    selectedWeekdays = days
                }
            )

            val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            val selectedText = selectedWeekdays.sorted()
                .mapNotNull { dayNames.getOrNull(it) }
                .joinToString(", ")
                .ifEmpty { "None selected" }

            Text(
                text = "Selected: $selectedText",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )
        }
    }
}

@Composable
private fun MonthDayPickerDemo() {
    var selectedDay by remember { mutableStateOf<Int?>(null) }

    DemoSection(
        title = "Month Day Picker",
        description = "Select day of month (1-31)"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
            PixaDatePicker(
                variant = DatePickerVariant.MonthDayPicker,
                mode = DateSelectionMode.Multiple,
                size = DatePickerSize.Medium,
                onDayOfMonthSelected = { day ->
                    selectedDay = day
                }
            )

            Text(
                text = "Selected: ${selectedDay?.let { "Day $it" } ?: "None"}",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// TIME PICKER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun TimePickerDemo() {
    var selectedTime by remember { mutableStateOf("12:00") }

    DemoSection(
        title = "Time Picker",
        description = "Wheel, Clock, and TimeOfDay variants"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)) {
            DemoLabel(text = "Wheel Picker - 12 Hour")
            PixaTimePicker(
                variant = TimePickerVariant.Wheel,
                mode = TimeSelectionMode.Single,
                size = TimePickerSize.Medium,
                format = TimeFormat.Hour12,
                onTimeSelected = { time ->
                    selectedTime = time.toFormattedString()
                }
            )

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

            DemoLabel(text = "Clock Picker")
            PixaTimePicker(
                variant = TimePickerVariant.Clock,
                mode = TimeSelectionMode.Single,
                size = TimePickerSize.Medium,
                format = TimeFormat.Hour12,
                onTimeSelected = { time ->
                    selectedTime = time.toFormattedString()
                }
            )

            Text(
                text = "Selected: $selectedTime",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// COLOR PICKER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ColorPickerDemo() {
    var selectedColor by remember { mutableStateOf(Color.Cyan) }

    DemoSection(
        title = "Color Picker",
        description = "Grid mode with hex input and history"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
            PixaColorPicker(
                mode = ColorPickerMode.Grid,
                gridColumnsCount = 8,
                showModeSelector = true,
                showHexInput = true,
                showHistory = true,
                onColorChanged = { selectedColor = it },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(selectedColor, shape = androidx.compose.foundation.shape.CircleShape)
                )
                Text(
                    text = "Selected Color",
                    style = AppTheme.typography.bodyRegular,
                    color = AppTheme.colors.baseContentBody
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// DROPDOWN
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun DropdownDemo() {
    var selectedCountry by remember { mutableStateOf<String?>(null) }
    var selectedSize by remember { mutableStateOf<String?>(null) }

    DemoSection(
        title = "Dropdown",
        description = "Outlined and Filled variants"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
            DemoLabel(text = "Outlined")
            PixaDropdown(
                items = listOf(
                    DropdownItem("us", "United States"),
                    DropdownItem("uk", "United Kingdom"),
                    DropdownItem("ca", "Canada"),
                    DropdownItem("au", "Australia"),
                    DropdownItem("de", "Germany"),
                    DropdownItem("fr", "France")
                ),
                selectedItem = selectedCountry,
                onItemSelected = { selectedCountry = it },
                placeholder = "Select a country",
                label = "Country",
                variant = DropdownVariant.Outlined,
                size = DropdownSize.Medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

            DemoLabel(text = "Filled")
            PixaDropdown(
                items = listOf(
                    DropdownItem("small", "Small"),
                    DropdownItem("medium", "Medium"),
                    DropdownItem("large", "Large"),
                    DropdownItem("xl", "Extra Large")
                ),
                selectedItem = selectedSize,
                onItemSelected = { selectedSize = it },
                placeholder = "Select size",
                label = "Size",
                variant = DropdownVariant.Filled,
                size = DropdownSize.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

