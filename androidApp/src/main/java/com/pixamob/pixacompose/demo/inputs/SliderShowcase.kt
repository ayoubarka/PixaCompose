package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.inputs.PixaSlider
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun SliderShowcase() {
    var value1 by remember { mutableStateOf(0.5f) }
    var value2 by remember { mutableStateOf(0.3f) }

    ShowcaseScreen {
        ShowcaseSection("Basic") {
            PixaSlider(value = value1, onValueChange = { value1 = it }, modifier = Modifier.fillMaxWidth())
        }

        ShowcaseSection("Sizes") {
            PixaSlider(value = value2, onValueChange = { value2 = it }, size = SizeVariant.Small, modifier = Modifier.fillMaxWidth())
            PixaSlider(value = value2, onValueChange = { value2 = it }, size = SizeVariant.Medium, modifier = Modifier.fillMaxWidth())
            PixaSlider(value = value2, onValueChange = { value2 = it }, size = SizeVariant.Large, modifier = Modifier.fillMaxWidth())
        }

        ShowcaseSection("States") {
            PixaSlider(value = 0.7f, onValueChange = {}, enabled = true, modifier = Modifier.fillMaxWidth())
            PixaSlider(value = 0.7f, onValueChange = {}, enabled = false, modifier = Modifier.fillMaxWidth())
        }

        ShowcaseSection("Interactive") {
            PixaSlider(value = value1, onValueChange = { value1 = it }, modifier = Modifier.fillMaxWidth())
        }
    }
}
