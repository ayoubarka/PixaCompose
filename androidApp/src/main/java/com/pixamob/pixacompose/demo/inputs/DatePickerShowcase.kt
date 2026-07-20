package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.inputs.DatePickerVariant
import com.pixamob.pixacompose.components.inputs.PixaDatePicker
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun DatePickerShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Calendar") {
            PixaDatePicker(variant = DatePickerVariant.Calendar)
        }

        ShowcaseSection("Wheel") {
            PixaDatePicker(variant = DatePickerVariant.Wheel)
        }

        ShowcaseSection("As Field") {
            PixaDatePicker(variant = DatePickerVariant.Calendar, asField = true, label = "Select date")
        }
    }
}
