package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.inputs.PixaToggleButtonGroup
import com.pixamob.pixacompose.components.inputs.ToggleOption
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun ToggleButtonGroupShowcase() {
    var selectedIds by remember { mutableStateOf(setOf("bold")) }

    ShowcaseScreen {
        ShowcaseSection("Toggle Button Group") {
            PixaToggleButtonGroup(
                options = listOf(
                    ToggleOption(id = "bold", title = "Bold"),
                    ToggleOption(id = "italic", title = "Italic"),
                    ToggleOption(id = "underline", title = "Underline")
                ),
                selectedIds = selectedIds,
                onSelectionChange = { selectedIds = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
