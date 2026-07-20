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
import com.pixamob.pixacompose.components.inputs.PixaTextArea
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun TextAreaShowcase() {
    var text by remember { mutableStateOf("") }

    ShowcaseScreen {
        ShowcaseSection("TextArea") {
            PixaTextArea(value = text, onValueChange = { text = it }, label = "Description", placeholder = "Enter your description here...")
        }

        ShowcaseSection("With Character Count") {
            PixaTextArea(value = text, onValueChange = { text = it }, label = "Bio", placeholder = "Write about yourself", maxLength = 150)
        }

        ShowcaseSection("States") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaTextArea(value = "Read only text", onValueChange = {}, label = "Read Only", readOnly = true)
                PixaTextArea(value = "Disabled text", onValueChange = {}, label = "Disabled", enabled = false)
                PixaTextArea(value = "Error text", onValueChange = {}, label = "Error", isError = true, errorText = "This field has an error")
            }
        }

        ShowcaseSection("Interactive") {
            PixaTextArea(value = text, onValueChange = { text = it }, label = "Write here", placeholder = "Start typing...")
        }
    }
}
