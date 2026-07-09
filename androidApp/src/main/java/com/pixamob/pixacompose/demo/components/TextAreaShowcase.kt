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
import com.pixamob.pixacompose.components.inputs.PixaTextArea
import com.pixamob.pixacompose.components.inputs.TextAreaVariant
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun TextAreaShowcase() {
    var text by remember { mutableStateOf("") }
    var disabledText by remember { mutableStateOf("This is disabled") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaTextArea(
                    value = text,
                    onValueChange = { text = it },
                    variant = TextAreaVariant.Filled,
                    label = "Filled",
                    placeholder = "Enter text..."
                )
                PixaTextArea(
                    value = text,
                    onValueChange = { text = it },
                    variant = TextAreaVariant.Outlined,
                    label = "Outlined",
                    placeholder = "Enter text..."
                )
                PixaTextArea(
                    value = text,
                    onValueChange = { text = it },
                    variant = TextAreaVariant.Ghost,
                    label = "Ghost",
                    placeholder = "Enter text..."
                )
            }
        }

        ShowcaseSection("Sizes") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaTextArea(
                    value = text,
                    onValueChange = { text = it },
                    size = SizeVariant.Small,
                    label = "Small",
                    placeholder = "Small text area"
                )
                PixaTextArea(
                    value = text,
                    onValueChange = { text = it },
                    size = SizeVariant.Medium,
                    label = "Medium",
                    placeholder = "Medium text area"
                )
                PixaTextArea(
                    value = text,
                    onValueChange = { text = it },
                    size = SizeVariant.Large,
                    label = "Large",
                    placeholder = "Large text area"
                )
            }
        }

        ShowcaseSection("States") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaTextArea(
                    value = "Enabled",
                    onValueChange = {},
                    label = "Enabled"
                )
                PixaTextArea(
                    value = disabledText,
                    onValueChange = { disabledText = it },
                    enabled = false,
                    label = "Disabled"
                )
                PixaTextArea(
                    value = text,
                    onValueChange = { text = it },
                    isError = true,
                    label = "Error",
                    errorText = "Please fix this field"
                )
            }
        }

        ShowcaseSection("Interactive") {
            PixaTextArea(
                value = text,
                onValueChange = { text = it },
                label = "Description",
                placeholder = "Enter your description...",
                showCharacterCount = true,
                maxLength = 200
            )
        }
    }
}
