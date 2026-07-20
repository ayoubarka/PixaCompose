package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.inputs.DropdownItem
import com.pixamob.pixacompose.components.inputs.PixaDropdown
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun DropdownShowcase() {
    var selected by remember { mutableStateOf("opt_a") }
    val items = remember {
        listOf(
            DropdownItem(value = "opt_a", label = "Option A"),
            DropdownItem(value = "opt_b", label = "Option B"),
            DropdownItem(value = "opt_c", label = "Option C")
        )
    }

    ShowcaseScreen {
        ShowcaseSection("Dropdown") {
            PixaDropdown(
                items = items,
                selectedItem = selected,
                onItemSelected = { selected = it },
                label = "Choose an option",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
