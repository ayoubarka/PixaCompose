package com.pixamob.pixacompose.demo.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.ChipType
import com.pixamob.pixacompose.components.actions.ChipVariant
import com.pixamob.pixacompose.components.actions.PixaChip
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun ChipShowcase() {
    var selectedChip by remember { mutableStateOf(false) }

    ShowcaseScreen {
        ShowcaseSection("Variants") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaChip(text = "Tonal", variant = ChipVariant.Tonal)
                PixaChip(text = "Filled", variant = ChipVariant.Filled)
                PixaChip(text = "Outlined", variant = ChipVariant.Outlined)
                PixaChip(text = "Ghost", variant = ChipVariant.Ghost)
            }
        }

        ShowcaseSection("Sizes") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaChip(text = "Sm", size = SizeVariant.Small)
                PixaChip(text = "Md", size = SizeVariant.Medium)
                PixaChip(text = "Lg", size = SizeVariant.Large)
            }
        }

        ShowcaseSection("With Icon") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val icon = rememberVectorPainter(Icons.Default.Favorite)
                PixaChip(text = "Favorite", leadingIcon = icon)
                PixaChip(text = "Remove", trailingIcon = rememberVectorPainter(Icons.Default.Close))
            }
        }

        ShowcaseSection("Selectable") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaChip(
                    text = "Toggle",
                    type = ChipType.Selectable,
                    selected = selectedChip,
                    onClick = { selectedChip = !selectedChip }
                )
            }
        }

        ShowcaseSection("States") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaChip(text = "Enabled")
                PixaChip(text = "Disabled", enabled = false)
                PixaChip(text = "Dismissible", onDismiss = {})
            }
        }
    }
}
