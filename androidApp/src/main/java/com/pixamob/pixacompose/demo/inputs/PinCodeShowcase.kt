package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.inputs.PixaPinCode
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun PinCodeShowcase() {
    var pin by remember { mutableStateOf("") }

    ShowcaseScreen {
        ShowcaseSection("PIN Code") {
            PixaPinCode(value = pin, onValueChange = { pin = it }, length = 4)
        }

        ShowcaseSection("States") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaPinCode(value = "12", onValueChange = {}, length = 4, enabled = false)
                PixaPinCode(value = "", onValueChange = {}, length = 4, isError = true)
            }
        }
    }
}
