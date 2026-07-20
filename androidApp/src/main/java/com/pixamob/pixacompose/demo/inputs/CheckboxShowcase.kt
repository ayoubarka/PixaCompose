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
import com.pixamob.pixacompose.components.inputs.CheckboxVariant
import com.pixamob.pixacompose.components.inputs.PixaCheckbox
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun CheckboxShowcase() {
    var checked1 by remember { mutableStateOf(false) }
    var checked2 by remember { mutableStateOf(false) }

    ShowcaseScreen {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaCheckbox(checked = checked1, onCheckedChange = { checked1 = it }, variant = CheckboxVariant.Outlined, label = "Outlined")
                PixaCheckbox(checked = checked2, onCheckedChange = { checked2 = it }, variant = CheckboxVariant.Filled, label = "Filled")
                PixaCheckbox(checked = false, onCheckedChange = {}, variant = CheckboxVariant.Ghost, label = "Ghost")
            }
        }

        ShowcaseSection("States") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaCheckbox(checked = false, onCheckedChange = {}, label = "Unchecked")
                PixaCheckbox(checked = true, onCheckedChange = {}, label = "Checked")
                PixaCheckbox(checked = false, onCheckedChange = {}, enabled = false, label = "Disabled")
                PixaCheckbox(checked = true, onCheckedChange = {}, enabled = false, label = "Disabled checked")
                PixaCheckbox(checked = false, onCheckedChange = {}, isError = true, label = "Error")
            }
        }

        ShowcaseSection("Interactive") {
            PixaCheckbox(checked = checked1, onCheckedChange = { checked1 = it }, label = "Toggle me")
        }
    }
}
