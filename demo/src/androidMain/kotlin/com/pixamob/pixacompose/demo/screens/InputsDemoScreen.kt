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
import com.pixamob.pixacompose.utils.DateTimeUtils.to12HourFormat
import com.pixamob.pixacompose.utils.DateTimeUtils.toLocalDate
import com.pixamob.pixacompose.utils.DateTimeUtils.toIsoString

/**
 * Complete Inputs Category Demo Screen
 * Displays: TextField, Checkbox, RadioButton, Switch, Slider, DatePicker, etc.
 */
@Composable
fun InputsDemoScreen(onBack: () -> Unit) {
    var textValue by remember { mutableStateOf("Sample text") }
    var sliderValue by remember { mutableFloatStateOf(50f) }
    var switchState by remember { mutableStateOf(false) }
    var checkboxState by remember { mutableStateOf(false) }

    // Color Picker state
    var selectedColor by remember { mutableStateOf(androidx.compose.ui.graphics.Color.Cyan) }

    // Time Picker state
    var selectedTime by remember { mutableStateOf<String>("12:00") }

    // Date Picker state
    var selectedDate by remember { mutableStateOf<String>("Select date") }

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
            // ===== TEXTFIELD COMPONENT =====
            item {
                DemoSection(
                    title = "TextField - All Sizes",
                    description = "Small (36dp), Medium (44dp), Large (52dp)"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        PixaTextField(
                            value = textValue,
                            onValueChange = { textValue = it },
                            label = "Small TextField (36dp)",
                            size = TextFieldSize.Small,
                            variant = TextFieldVariant.Outlined,
                            modifier = Modifier.fillMaxWidth()
                        )

                        PixaTextField(
                            value = textValue,
                            onValueChange = { textValue = it },
                            label = "Medium TextField (44dp)",
                            size = TextFieldSize.Medium,
                            variant = TextFieldVariant.Outlined,
                            modifier = Modifier.fillMaxWidth()
                        )

                        PixaTextField(
                            value = textValue,
                            onValueChange = { textValue = it },
                            label = "Large TextField (52dp)",
                            size = TextFieldSize.Large,
                            variant = TextFieldVariant.Outlined,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                DemoSection(
                    title = "TextField - All Variants",
                    description = "Filled, Outlined, Ghost styles"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        Text(
                            text = "Filled",
                            style = AppTheme.typography.subtitleBold,
                            modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
                        )
                        PixaTextField(
                            value = textValue,
                            onValueChange = { textValue = it },
                            label = "Filled TextField",
                            size = TextFieldSize.Medium,
                            variant = TextFieldVariant.Filled,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        Text(
                            text = "Outlined",
                            style = AppTheme.typography.subtitleBold,
                            modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
                        )
                        PixaTextField(
                            value = textValue,
                            onValueChange = { textValue = it },
                            label = "Outlined TextField",
                            size = TextFieldSize.Medium,
                            variant = TextFieldVariant.Outlined,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        Text(
                            text = "Ghost",
                            style = AppTheme.typography.subtitleBold,
                            modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
                        )
                        PixaTextField(
                            value = textValue,
                            onValueChange = { textValue = it },
                            label = "Ghost TextField",
                            size = TextFieldSize.Medium,
                            variant = TextFieldVariant.Ghost,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ===== CHECKBOX COMPONENT =====
            item {
                DemoSection(
                    title = "Checkbox - All Sizes",
                    description = "Small, Medium, Large with labels"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        PixaCheckbox(
                            checked = checkboxState,
                            onCheckedChange = { checkboxState = it },
                            label = "Small Checkbox",
                            size = CheckboxSize.Small
                        )

                        PixaCheckbox(
                            checked = checkboxState,
                            onCheckedChange = { checkboxState = it },
                            label = "Medium Checkbox",
                            size = CheckboxSize.Medium
                        )

                        PixaCheckbox(
                            checked = checkboxState,
                            onCheckedChange = { checkboxState = it },
                            label = "Large Checkbox",
                            size = CheckboxSize.Large
                        )
                    }
                }
            }

            // ===== RADIO BUTTON COMPONENT =====
            item {
                DemoSection(
                    title = "RadioButton - All Sizes",
                    description = "Small, Medium, Large with labels"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = true,
                                onClick = {}
                            )
                            Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
                            Text("Small RadioButton", style = AppTheme.typography.bodyRegular)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = true,
                                onClick = {}
                            )
                            Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
                            Text("Medium RadioButton", style = AppTheme.typography.bodyRegular)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = true,
                                onClick = {}
                            )
                            Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
                            Text("Large RadioButton", style = AppTheme.typography.bodyRegular)
                        }
                    }
                }
            }

            // ===== SWITCH COMPONENT =====
            item {
                DemoSection(
                    title = "Switch - All Sizes",
                    description = "Small, Medium, Large with labels"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        PixaSwitch(
                            checked = switchState,
                            onCheckedChange = { switchState = it },
                            label = "Small Switch",
                            size = SwitchSize.Small
                        )

                        PixaSwitch(
                            checked = switchState,
                            onCheckedChange = { switchState = it },
                            label = "Medium Switch",
                            size = SwitchSize.Medium
                        )

                        PixaSwitch(
                            checked = switchState,
                            onCheckedChange = { switchState = it },
                            label = "Large Switch",
                            size = SwitchSize.Large
                        )
                    }
                }
            }

            // ===== SLIDER COMPONENT =====
            item {
                DemoSection(
                    title = "Slider - All Sizes",
                    description = "Small, Medium, Large with value display"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)) {
                        Text("Small Slider", style = AppTheme.typography.subtitleBold)
                        FilledSlider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            size = SliderSize.Small,
                            label = "Value",
                            showValue = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Medium Slider", style = AppTheme.typography.subtitleBold)
                        FilledSlider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            size = SliderSize.Medium,
                            label = "Value",
                            showValue = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Large Slider", style = AppTheme.typography.subtitleBold)
                        FilledSlider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            size = SliderSize.Large,
                            label = "Value",
                            showValue = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ===== SLIDER VARIANTS =====
            item {
                DemoSection(
                    title = "Slider - All Variants",
                    description = "Filled, Outlined, Minimal styles"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)) {
                        Text("Filled", style = AppTheme.typography.subtitleBold)
                        FilledSlider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            size = SliderSize.Medium,
                            label = "Filled",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Outlined", style = AppTheme.typography.subtitleBold)
                        OutlinedSlider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            size = SliderSize.Medium,
                            label = "Outlined",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Minimal", style = AppTheme.typography.subtitleBold)
                        MinimalSlider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            size = SliderSize.Medium,
                            label = "Minimal",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ===== COLOR PICKER COMPONENT =====
            item {
                DemoSection(
                    title = "ColorPicker - All Modes",
                    description = "Switch between Grid, Wheel, RGB, HSV, HSL using tabs"
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

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                        Text("Selected Color: $selectedColor", style = AppTheme.typography.bodyRegular)
                    }
                }
            }

            // ===== TIME PICKER COMPONENT =====
            item {
                DemoSection(
                    title = "TimePicker - All Variants",
                    description = "Wheel, Clock, and TimeOfDay pickers with different sizes"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)) {
                        Text("Wheel Picker - 12 Hour Format", style = AppTheme.typography.subtitleBold)
                        TimePicker(
                            variant = TimePickerVariant.Wheel,
                            mode = TimeSelectionMode.Single,
                            size = TimePickerSize.Medium,
                            format = TimeFormat.Hour12,
                            title = "Select Time",
                            onTimeSelected = { time ->
                                selectedTime = time.to12HourFormat()
                            }
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

                        Text("Wheel Picker - 24 Hour Format", style = AppTheme.typography.subtitleBold)
                        TimePicker(
                            variant = TimePickerVariant.Wheel,
                            mode = TimeSelectionMode.Single,
                            size = TimePickerSize.Medium,
                            format = TimeFormat.Hour24,
                            title = "Select Time (24H)",
                            onTimeSelected = { time ->
                                selectedTime = time.toFormattedString()
                            }
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

                        Text("Clock Picker - Single Selection", style = AppTheme.typography.subtitleBold)
                        TimePicker(
                            variant = TimePickerVariant.Clock,
                            mode = TimeSelectionMode.Single,
                            size = TimePickerSize.Medium,
                            format = TimeFormat.Hour12,
                            title = "Select Time (Clock)",
                            onTimeSelected = { time ->
                                selectedTime = time.to12HourFormat()
                            }
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

                        Text("TimeOfDay Picker", style = AppTheme.typography.subtitleBold)
                        TimePicker(
                            variant = TimePickerVariant.TimeOfDayPicker,
                            size = TimePickerSize.Medium,
                            title = "Select Time Period",
                            onTimeOfDaySelected = { slot, time ->
                                selectedTime = "$slot - ${time.toFormattedString()}"
                            }
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

                        Text("Time Range Picker - Wheel", style = AppTheme.typography.subtitleBold)
                        TimePicker(
                            variant = TimePickerVariant.Wheel,
                            mode = TimeSelectionMode.Range,
                            size = TimePickerSize.Large,
                            format = TimeFormat.Hour12,
                            title = "Select Time Range",
                            onRangeSelected = { start, end ->
                                if (start != null && end != null) {
                                    selectedTime = "${start.to12HourFormat()} - ${end.to12HourFormat()}"
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                        Text("Selected Time: $selectedTime", style = AppTheme.typography.bodyRegular)
                    }
                }
            }

            // ===== DATE PICKER COMPONENT =====
            item {
                DemoSection(
                    title = "DatePicker - All Variants",
                    description = "Wheel, Calendar, MonthDay, Weekday, and DayCount pickers"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)) {
                        Text("Wheel Picker - Single Date", style = AppTheme.typography.subtitleBold)
                        PixaDatePicker(
                            variant = DatePickerVariant.Wheel,
                            mode = DateSelectionMode.Single,
                            size = DatePickerSize.Medium,
                            title = "Select Date",
                            onDateSelected = { epochMillis ->
                                val dateValue = epochMillis.toLocalDate()
                                selectedDate = dateValue.toIsoString()
                            }
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

                        Text("Calendar Picker - Single Date", style = AppTheme.typography.subtitleBold)
                        PixaDatePicker(
                            variant = DatePickerVariant.Calendar,
                            mode = DateSelectionMode.Single,
                            size = DatePickerSize.Large,
                            title = "Select Date (Calendar)",
                            onDateSelected = { epochMillis ->
                                val dateValue = epochMillis.toLocalDate()
                                selectedDate = dateValue.toIsoString()
                            }
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

                        Text("Calendar Picker - Date Range", style = AppTheme.typography.subtitleBold)
                        PixaDatePicker(
                            variant = DatePickerVariant.Calendar,
                            mode = DateSelectionMode.Range,
                            size = DatePickerSize.Large,
                            title = "Select Date Range",
                            onRangeSelected = { start, end ->
                                if (start != null && end != null) {
                                    selectedDate = "${start.toIsoString()} to ${end.toIsoString()}"
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

                        Text("Month Day Picker", style = AppTheme.typography.subtitleBold)
                        PixaDatePicker(
                            variant = DatePickerVariant.MonthDayPicker,
                            size = DatePickerSize.Medium,
                            title = "Select Day of Month",
                            onDayOfMonthSelected = { day ->
                                selectedDate = "Day: $day"
                            }
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

                        Text("Weekday Picker", style = AppTheme.typography.subtitleBold)
                        PixaDatePicker(
                            variant = DatePickerVariant.WeekdayPicker,
                            size = DatePickerSize.Medium,
                            title = "Select Days of Week",
                            onWeekdaysSelected = { weekdays ->
                                selectedDate = "Selected ${weekdays.size} days"
                            }
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

                        Text("Day Count Picker", style = AppTheme.typography.subtitleBold)
                        PixaDatePicker(
                            variant = DatePickerVariant.DayCountPicker,
                            size = DatePickerSize.Medium,
                            title = "Select Repeat Interval",
                            onDayCountSelected = { count ->
                                selectedDate = "Every $count days"
                            }
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                        Text("Selected Date: $selectedDate", style = AppTheme.typography.bodyRegular)
                    }
                }
            }

            // Additional spacing
            item { Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large)) }
        }
    }
}
