package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.inputs.LabelPosition
import com.pixamob.pixacompose.components.inputs.PixaSwitch
import com.pixamob.pixacompose.components.inputs.SwitchVariant
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun SwitchShowcase() {
    var checked by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaSwitch(
                    checked = true,
                    onCheckedChange = {},
                    variant = SwitchVariant.Filled,
                    label = "Filled"
                )
                PixaSwitch(
                    checked = true,
                    onCheckedChange = {},
                    variant = SwitchVariant.Outlined,
                    label = "Outlined"
                )
                PixaSwitch(
                    checked = true,
                    onCheckedChange = {},
                    variant = SwitchVariant.Ghost,
                    label = "Ghost"
                )
            }
        }

        ShowcaseSection("Sizes") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PixaSwitch(checked = true, onCheckedChange = {}, size = SizeVariant.Small)
                PixaSwitch(checked = true, onCheckedChange = {}, size = SizeVariant.Medium)
                PixaSwitch(checked = true, onCheckedChange = {}, size = SizeVariant.Large)
            }
        }

        ShowcaseSection("With Labels") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaSwitch(
                    checked = notifications,
                    onCheckedChange = { notifications = it },
                    label = "Notifications",
                    labelPosition = LabelPosition.End
                )
                PixaSwitch(
                    checked = darkMode,
                    onCheckedChange = { darkMode = it },
                    label = "Dark Mode",
                    labelPosition = LabelPosition.Start
                )
            }
        }

        ShowcaseSection("States") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PixaSwitch(checked = true, onCheckedChange = {}, label = "Enabled")
                PixaSwitch(checked = true, onCheckedChange = {}, enabled = false, label = "Disabled")
                PixaSwitch(checked = false, onCheckedChange = {}, enabled = false, label = "Disabled")
            }
        }

        ShowcaseSection("Interactive") {
            PixaSwitch(
                checked = checked,
                onCheckedChange = { checked = it },
                label = "Toggle me"
            )
        }
    }
}
