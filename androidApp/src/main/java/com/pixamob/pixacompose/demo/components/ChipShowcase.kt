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
import com.pixamob.pixacompose.components.actions.ChipType
import com.pixamob.pixacompose.components.actions.ChipVariant
import com.pixamob.pixacompose.components.actions.PixaChip
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun ChipShowcase() {
    var selected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaChip(text = "Filled", variant = ChipVariant.Filled, size = SizeVariant.Small)
                PixaChip(text = "Tonal", variant = ChipVariant.Tonal, size = SizeVariant.Small)
                PixaChip(text = "Outlined", variant = ChipVariant.Outlined, size = SizeVariant.Small)
                PixaChip(text = "Ghost", variant = ChipVariant.Ghost, size = SizeVariant.Small)
            }
        }

        ShowcaseSection("Types") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaChip(text = "Static", type = ChipType.Static, size = SizeVariant.Small)
                PixaChip(text = "Selectable", type = ChipType.Selectable, onClick = {}, size = SizeVariant.Small)
                PixaChip(text = "Dismissible", type = ChipType.Dismissible, onDismiss = {}, size = SizeVariant.Small)
            }
        }

        ShowcaseSection("States") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaChip(text = "Enabled", type = ChipType.Selectable, onClick = {}, size = SizeVariant.Small)
                PixaChip(text = "Disabled", type = ChipType.Selectable, enabled = false, onClick = {}, size = SizeVariant.Small)
            }
        }

        ShowcaseSection("Interactive") {
            PixaChip(
                text = if (selected) "Selected" else "Unselected",
                type = ChipType.Selectable,
                selected = selected,
                onClick = { selected = !selected }
            )
        }
    }
}
