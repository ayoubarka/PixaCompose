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
import com.pixamob.pixacompose.components.inputs.CheckboxLabelPosition
import com.pixamob.pixacompose.components.inputs.CheckboxState
import com.pixamob.pixacompose.components.inputs.CheckboxVariant
import com.pixamob.pixacompose.components.inputs.PixaCheckbox
import com.pixamob.pixacompose.components.inputs.TriStateCheckbox
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun CheckboxShowcase() {
    var checked by remember { mutableStateOf(false) }
    var checkedA by remember { mutableStateOf(true) }
    var checkedB by remember { mutableStateOf(true) }
    var checkedC by remember { mutableStateOf(false) }
    var triState by remember { mutableStateOf(CheckboxState.Indeterminate) }

    val allChecked = checkedA && checkedB && checkedC
    val noneChecked = !checkedA && !checkedB && !checkedC
    val parentState = when {
        allChecked -> CheckboxState.Checked
        noneChecked -> CheckboxState.Unchecked
        else -> CheckboxState.Indeterminate
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PixaCheckbox(checked = true, onCheckedChange = null, variant = CheckboxVariant.Filled)
                PixaCheckbox(checked = true, onCheckedChange = null, variant = CheckboxVariant.Outlined)
                PixaCheckbox(checked = true, onCheckedChange = null, variant = CheckboxVariant.Ghost)
            }
        }

        ShowcaseSection("Sizes") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PixaCheckbox(checked = true, onCheckedChange = null, size = SizeVariant.Small)
                PixaCheckbox(checked = true, onCheckedChange = null, size = SizeVariant.Medium)
                PixaCheckbox(checked = true, onCheckedChange = null, size = SizeVariant.Large)
            }
        }

        ShowcaseSection("With Labels") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaCheckbox(
                    checked = checkedA,
                    onCheckedChange = { checkedA = it },
                    label = "Label at End",
                    labelPosition = CheckboxLabelPosition.End
                )
                PixaCheckbox(
                    checked = checkedB,
                    onCheckedChange = { checkedB = it },
                    label = "Label at Start",
                    labelPosition = CheckboxLabelPosition.Start
                )
            }
        }

        ShowcaseSection("States") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PixaCheckbox(checked = true, onCheckedChange = {}, label = "Enabled")
                PixaCheckbox(checked = true, onCheckedChange = null, enabled = false, label = "Disabled")
                PixaCheckbox(checked = false, onCheckedChange = null, enabled = false, label = "Disabled")
            }
        }

        ShowcaseSection("Interactive") {
            PixaCheckbox(
                checked = checked,
                onCheckedChange = { checked = it },
                label = "Toggle me"
            )
        }

        ShowcaseSection("Tri-State") {
            TriStateCheckbox(
                state = parentState,
                onStateChange = { newState ->
                    val shouldCheck = newState == CheckboxState.Checked
                    checkedA = shouldCheck
                    checkedB = shouldCheck
                    checkedC = shouldCheck
                },
                label = "Select All"
            )
            Column(modifier = Modifier.padding(start = 24.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                PixaCheckbox(checked = checkedA, onCheckedChange = { checkedA = it }, label = "Item A")
                PixaCheckbox(checked = checkedB, onCheckedChange = { checkedB = it }, label = "Item B")
                PixaCheckbox(checked = checkedC, onCheckedChange = { checkedC = it }, label = "Item C")
            }
        }
    }
}
