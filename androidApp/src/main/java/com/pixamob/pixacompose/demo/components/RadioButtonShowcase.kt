package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.inputs.HorizontalRadioGroup
import com.pixamob.pixacompose.components.inputs.RadioButtonVariant
import com.pixamob.pixacompose.components.inputs.RadioGroup
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun RadioButtonShowcase() {
    var selectedVertical by remember { mutableStateOf("Option 1") }
    var selectedHorizontal by remember { mutableStateOf("Small") }
    val options = listOf("Option 1", "Option 2", "Option 3")
    val sizeOptions = listOf("Small", "Medium", "Large")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RadioGroup(
                    options = options,
                    selectedOption = selectedVertical,
                    onOptionSelected = { selectedVertical = it },
                    optionLabel = { "$it (Filled)" },
                    variant = RadioButtonVariant.Filled
                )
                RadioGroup(
                    options = options,
                    selectedOption = selectedVertical,
                    onOptionSelected = { selectedVertical = it },
                    optionLabel = { "$it (Outlined)" },
                    variant = RadioButtonVariant.Outlined
                )
                RadioGroup(
                    options = options,
                    selectedOption = selectedVertical,
                    onOptionSelected = { selectedVertical = it },
                    optionLabel = { "$it (Ghost)" },
                    variant = RadioButtonVariant.Ghost
                )
            }
        }

        ShowcaseSection("Sizes") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RadioGroup(
                    options = options,
                    selectedOption = selectedVertical,
                    onOptionSelected = { selectedVertical = it },
                    size = SizeVariant.Small,
                    optionLabel = { "$it (Small)" }
                )
                RadioGroup(
                    options = options,
                    selectedOption = selectedVertical,
                    onOptionSelected = { selectedVertical = it },
                    size = SizeVariant.Medium,
                    optionLabel = { "$it (Medium)" }
                )
                RadioGroup(
                    options = options,
                    selectedOption = selectedVertical,
                    onOptionSelected = { selectedVertical = it },
                    size = SizeVariant.Large,
                    optionLabel = { "$it (Large)" }
                )
            }
        }

        ShowcaseSection("States") {
            RadioGroup(
                options = options,
                selectedOption = "Option 2",
                onOptionSelected = {},
                enabled = false,
                optionLabel = { "$it (disabled)" }
            )
        }

        ShowcaseSection("Vertical RadioGroup") {
            RadioGroup(
                options = options,
                selectedOption = selectedVertical,
                onOptionSelected = { selectedVertical = it }
            )
        }

        ShowcaseSection("Horizontal RadioGroup") {
            HorizontalRadioGroup(
                options = sizeOptions,
                selectedOption = selectedHorizontal,
                onOptionSelected = { selectedHorizontal = it }
            )
        }
    }
}
