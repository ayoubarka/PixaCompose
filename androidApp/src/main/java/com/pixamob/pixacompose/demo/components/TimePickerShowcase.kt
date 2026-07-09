package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.inputs.PixaTimePicker
import com.pixamob.pixacompose.components.inputs.TimeFormat
import com.pixamob.pixacompose.components.inputs.TimePickerVariant
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun TimePickerShowcase() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Wheel Mode") {
            PixaTimePicker(
                variant = TimePickerVariant.Wheel,
                size = SizeVariant.Medium,
                format = TimeFormat.Hour12
            )
        }

        ShowcaseSection("24-Hour Format") {
            PixaTimePicker(
                variant = TimePickerVariant.Wheel,
                size = SizeVariant.Medium,
                format = TimeFormat.Hour24
            )
        }
    }
}
