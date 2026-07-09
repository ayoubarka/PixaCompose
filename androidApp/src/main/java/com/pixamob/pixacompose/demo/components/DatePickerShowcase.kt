package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.inputs.DatePickerVariant
import com.pixamob.pixacompose.components.inputs.PixaDatePicker
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.SizeVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DatePickerShowcase() {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var wheelDate by remember { mutableStateOf<Long?>(null) }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Calendar Mode") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaDatePicker(
                    variant = DatePickerVariant.Calendar,
                    size = SizeVariant.Medium,
                    onDateSelected = { timestamp -> selectedDate = timestamp }
                )
                if (selectedDate != null) {
                    Text(
                        text = "Selected: ${dateFormat.format(Date(selectedDate!!))}",
                        style = AppTheme.typography.bodyRegular,
                        color = AppTheme.colors.baseContentTitle
                    )
                }
            }
        }

        ShowcaseSection("Wheel Mode") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaDatePicker(
                    variant = DatePickerVariant.Wheel,
                    size = SizeVariant.Medium,
                    onDateSelected = { timestamp -> wheelDate = timestamp }
                )
                if (wheelDate != null) {
                    Text(
                        text = "Selected: ${dateFormat.format(Date(wheelDate!!))}",
                        style = AppTheme.typography.bodyRegular,
                        color = AppTheme.colors.baseContentTitle
                    )
                }
            }
        }
    }
}
