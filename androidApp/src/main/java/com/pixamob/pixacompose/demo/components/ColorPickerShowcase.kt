package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.inputs.ColorPickerMode
import com.pixamob.pixacompose.components.inputs.PixaColorPicker
import com.pixamob.pixacompose.components.inputs.rememberColorPickerState
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun ColorPickerShowcase() {
    val state = rememberColorPickerState(initialColor = Color(0xFF0284C7))
    var selectedColor by remember { mutableStateOf<Color>(Color(0xFF0284C7)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Grid Mode") {
            PixaColorPicker(
                state = state,
                mode = ColorPickerMode.Grid,
                showAlpha = false,
                showBrightness = false,
                showHistory = true,
                showHexInput = true,
                showModeSelector = false,
                showColorComparison = false,
                onColorChanged = { color -> selectedColor = color }
            )
        }

        ShowcaseSection("Selected Color Preview") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(selectedColor)
            )
        }
    }
}
