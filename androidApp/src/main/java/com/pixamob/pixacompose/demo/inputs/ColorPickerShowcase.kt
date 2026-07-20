package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.inputs.PixaColorPicker
import com.pixamob.pixacompose.components.inputs.rememberColorPickerState
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun ColorPickerShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Color Picker") {
            PixaColorPicker(state = rememberColorPickerState())
        }
    }
}
