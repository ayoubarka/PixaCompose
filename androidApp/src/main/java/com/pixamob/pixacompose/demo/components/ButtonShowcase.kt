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
import com.pixamob.pixacompose.components.actions.ButtonShape
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun ButtonShowcase() {
    var count by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaButton(text = "Filled", variant = ButtonVariant.Filled, size = SizeVariant.Compact, onClick = {})
                PixaButton(text = "Outlined", variant = ButtonVariant.Outlined, size = SizeVariant.Compact, onClick = {})
                PixaButton(text = "Ghost", variant = ButtonVariant.Ghost, size = SizeVariant.Compact, onClick = {})
                PixaButton(text = "Tonal", variant = ButtonVariant.Tonal, size = SizeVariant.Compact, onClick = {})
            }
        }

        ShowcaseSection("Sizes") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaButton(text = "Compact", size = SizeVariant.Compact, onClick = {})
                PixaButton(text = "Small", size = SizeVariant.Small, onClick = {})
                PixaButton(text = "Medium", size = SizeVariant.Medium, onClick = {})
                PixaButton(text = "Large", size = SizeVariant.Large, onClick = {})
            }
        }

        ShowcaseSection("Shapes") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaButton(text = "Default", shape = ButtonShape.Default, onClick = {})
                PixaButton(text = "Pill", shape = ButtonShape.Pill, onClick = {})
                PixaButton(text = "Circle", shape = ButtonShape.Circle, onClick = {})
            }
        }

        ShowcaseSection("States") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaButton(text = "Enabled", onClick = {})
                PixaButton(text = "Disabled", enabled = false, onClick = {})
                PixaButton(text = "Loading", loading = true, onClick = {})
                PixaButton(text = "Skeleton", showSkeleton = true, onClick = {})
            }
        }

        ShowcaseSection("Interactive") {
            PixaButton(text = "Clicked $count times", onClick = { count++ })
        }
    }
}
