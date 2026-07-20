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
import com.pixamob.pixacompose.components.inputs.FilledRadioButton
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun RadioButtonShowcase() {
    var selected by remember { mutableStateOf("option1") }

    ShowcaseScreen {
        ShowcaseSection("Radio Group") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledRadioButton(label = "Option A", selected = selected == "option1", onClick = { selected = "option1" })
                FilledRadioButton(label = "Option B", selected = selected == "option2", onClick = { selected = "option2" })
                FilledRadioButton(label = "Option C", selected = selected == "option3", onClick = { selected = "option3" })
            }
        }

        ShowcaseSection("States") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledRadioButton(label = "Selected", selected = true, onClick = {})
                FilledRadioButton(label = "Unselected", selected = false, onClick = {})
                FilledRadioButton(label = "Disabled selected", selected = true, onClick = {}, enabled = false)
                FilledRadioButton(label = "Disabled unselected", selected = false, onClick = {}, enabled = false)
            }
        }
    }
}
