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
import com.pixamob.pixacompose.components.inputs.PixaSwitch
import com.pixamob.pixacompose.components.inputs.SwitchVariant
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun SwitchShowcase() {
    var toggle1 by remember { mutableStateOf(false) }
    var toggle2 by remember { mutableStateOf(true) }

    ShowcaseScreen {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaSwitch(checked = toggle1, onCheckedChange = { toggle1 = it }, variant = SwitchVariant.Filled, label = "Filled")
                PixaSwitch(checked = toggle2, onCheckedChange = { toggle2 = it }, variant = SwitchVariant.Outlined, label = "Outlined")
                PixaSwitch(checked = false, onCheckedChange = {}, variant = SwitchVariant.Ghost, label = "Ghost")
            }
        }

        ShowcaseSection("Sizes") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaSwitch(checked = true, onCheckedChange = {}, size = SizeVariant.Small, label = "Small")
                PixaSwitch(checked = true, onCheckedChange = {}, size = SizeVariant.Medium, label = "Medium")
                PixaSwitch(checked = true, onCheckedChange = {}, size = SizeVariant.Large, label = "Large")
            }
        }

        ShowcaseSection("States") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaSwitch(checked = true, onCheckedChange = {}, label = "On")
                PixaSwitch(checked = false, onCheckedChange = {}, label = "Off")
                PixaSwitch(checked = true, onCheckedChange = {}, enabled = false, label = "Disabled on")
                PixaSwitch(checked = false, onCheckedChange = {}, enabled = false, label = "Disabled off")
                PixaSwitch(checked = false, onCheckedChange = {}, loading = true, label = "Loading")
                PixaSwitch(checked = false, onCheckedChange = {}, isError = true, label = "Error")
            }
        }

        ShowcaseSection("Interactive") {
            PixaSwitch(checked = toggle1, onCheckedChange = { toggle1 = it }, label = "Toggle me")
        }
    }
}
