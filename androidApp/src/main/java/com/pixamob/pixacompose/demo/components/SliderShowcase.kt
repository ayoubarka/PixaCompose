package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.inputs.PixaSlider
import com.pixamob.pixacompose.components.inputs.SliderVariant
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun SliderShowcase() {
    var value by remember { mutableFloatStateOf(0.5f) }
    var volume by remember { mutableFloatStateOf(50f) }
    var brightness by remember { mutableFloatStateOf(0.8f) }
    var disabledValue by remember { mutableFloatStateOf(0.4f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaSlider(
                    value = value, onValueChange = { value = it },
                    variant = SliderVariant.Filled, showValue = true,
                    valueFormatter = { "${(it * 100).toInt()}%" }
                )
                PixaSlider(
                    value = value, onValueChange = { value = it },
                    variant = SliderVariant.Outlined, showValue = true,
                    valueFormatter = { "${(it * 100).toInt()}%" }
                )
                PixaSlider(
                    value = value, onValueChange = { value = it },
                    variant = SliderVariant.Ghost, showValue = true,
                    valueFormatter = { "${(it * 100).toInt()}%" }
                )
            }
        }

        ShowcaseSection("Sizes") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaSlider(value = value, onValueChange = { value = it }, size = SizeVariant.Small)
                PixaSlider(value = value, onValueChange = { value = it }, size = SizeVariant.Medium)
                PixaSlider(value = value, onValueChange = { value = it }, size = SizeVariant.Large)
            }
        }

        ShowcaseSection("Interactive") {
            Text(
                text = "Value: ${(value * 100).toInt()}%",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentTitle
            )
            PixaSlider(
                value = value,
                onValueChange = { value = it },
                showValue = true,
                valueFormatter = { "${(it * 100).toInt()}%" }
            )
        }

        ShowcaseSection("With Steps") {
            Text(
                text = "Volume: ${volume.toInt()}",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentTitle
            )
            PixaSlider(
                value = volume,
                onValueChange = { volume = it },
                valueRange = 0f..100f,
                steps = 4,
                showValue = true,
                valueFormatter = { "${it.toInt()}" }
            )
        }

        ShowcaseSection("Disabled") {
            PixaSlider(
                value = disabledValue,
                onValueChange = { disabledValue = it },
                enabled = false
            )
        }
    }
}
