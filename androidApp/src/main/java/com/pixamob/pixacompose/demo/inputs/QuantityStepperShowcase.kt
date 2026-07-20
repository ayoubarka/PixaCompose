package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.inputs.QuantityStepper
import com.pixamob.pixacompose.components.inputs.WideQuantityStepper
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun QuantityStepperShowcase() {
    var count by remember { mutableStateOf(1) }

    ShowcaseScreen {
        ShowcaseSection("Narrow") {
            QuantityStepper(value = count, onValueChange = { count = it }, min = 0, max = 10)
        }

        ShowcaseSection("Wide") {
            WideQuantityStepper(value = count, onValueChange = { count = it }, min = 0, max = 10, modifier = Modifier.fillMaxWidth())
        }
    }
}
