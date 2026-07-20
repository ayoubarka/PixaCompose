package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.inputs.PixaRangeSlider
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun RangeSliderShowcase() {
    var lower by remember { mutableStateOf(0.2f) }
    var upper by remember { mutableStateOf(0.8f) }

    ShowcaseScreen {
        ShowcaseSection("Range Slider") {
            PixaRangeSlider(lowerValue = lower, upperValue = upper, onValueChange = { l, u -> lower = l; upper = u }, modifier = Modifier.fillMaxWidth())
        }
    }
}
