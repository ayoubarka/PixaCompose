package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.inputs.PixaTimePicker
import com.pixamob.pixacompose.components.inputs.TimePickerVariant
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun TimePickerShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Wheel") {
            PixaTimePicker(variant = TimePickerVariant.Wheel)
        }

        ShowcaseSection("Stepper") {
            PixaTimePicker(variant = TimePickerVariant.Stepper)
        }

        ShowcaseSection("TimeOfDayPicker") {
            PixaTimePicker(variant = TimePickerVariant.TimeOfDayPicker)
        }
    }
}
