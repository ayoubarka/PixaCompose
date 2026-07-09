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
import com.pixamob.pixacompose.components.inputs.DropdownItem
import com.pixamob.pixacompose.components.inputs.DropdownVariant
import com.pixamob.pixacompose.components.inputs.PixaDropdown
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun DropdownShowcase() {
    var selected by remember { mutableStateOf("") }
    var selectedWithLabel by remember { mutableStateOf("") }

    val items = listOf(
        DropdownItem(value = "Option 1", label = "Option 1"),
        DropdownItem(value = "Option 2", label = "Option 2"),
        DropdownItem(value = "Option 3", label = "Option 3"),
        DropdownItem(value = "Option 4", label = "Option 4")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaDropdown(
                    items = items,
                    selectedItem = selected,
                    onItemSelected = { selected = it },
                    variant = DropdownVariant.Outlined,
                    placeholder = "Outlined"
                )
                PixaDropdown(
                    items = items,
                    selectedItem = selected,
                    onItemSelected = { selected = it },
                    variant = DropdownVariant.Filled,
                    placeholder = "Filled"
                )
                PixaDropdown(
                    items = items,
                    selectedItem = selected,
                    onItemSelected = { selected = it },
                    variant = DropdownVariant.Ghost,
                    placeholder = "Ghost"
                )
            }
        }

        ShowcaseSection("Sizes") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaDropdown(
                    items = items,
                    selectedItem = selected,
                    onItemSelected = { selected = it },
                    size = SizeVariant.Small,
                    placeholder = "Small"
                )
                PixaDropdown(
                    items = items,
                    selectedItem = selected,
                    onItemSelected = { selected = it },
                    size = SizeVariant.Medium,
                    placeholder = "Medium"
                )
                PixaDropdown(
                    items = items,
                    selectedItem = selected,
                    onItemSelected = { selected = it },
                    size = SizeVariant.Large,
                    placeholder = "Large"
                )
            }
        }

        ShowcaseSection("With Label") {
            PixaDropdown(
                items = items,
                selectedItem = selectedWithLabel,
                onItemSelected = { selectedWithLabel = it },
                placeholder = "Select an option",
                label = "Choose Option"
            )
        }

        ShowcaseSection("Disabled") {
            PixaDropdown(
                items = items,
                selectedItem = "Option 2",
                onItemSelected = {},
                placeholder = "Disabled",
                enabled = false
            )
        }

        ShowcaseSection("Interactive") {
            PixaDropdown(
                items = items,
                selectedItem = selected,
                onItemSelected = { selected = it },
                placeholder = "Select an option"
            )
        }
    }
}
