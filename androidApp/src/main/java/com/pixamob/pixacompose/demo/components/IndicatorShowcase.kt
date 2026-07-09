package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.feedback.LoadingIndicator
import com.pixamob.pixacompose.components.feedback.PixaCircularIndicator
import com.pixamob.pixacompose.components.feedback.PixaLinearIndicator
import com.pixamob.pixacompose.components.feedback.PixaPagerIndicator
import com.pixamob.pixacompose.components.feedback.ProgressVariant
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun IndicatorShowcase() {
    var value by remember { mutableFloatStateOf(0.3f) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Circular Indeterminate") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                LoadingIndicator(sizePreset = SizeVariant.Small)
                LoadingIndicator(sizePreset = SizeVariant.Medium)
                LoadingIndicator(sizePreset = SizeVariant.Large)
            }
        }

        ShowcaseSection("Circular Determinate") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaCircularIndicator(progress = 0.25f, sizePreset = SizeVariant.Medium)
                PixaCircularIndicator(progress = 0.5f, sizePreset = SizeVariant.Medium)
                PixaCircularIndicator(progress = 0.75f, sizePreset = SizeVariant.Medium, showPercentage = true)
            }
        }

        ShowcaseSection("Linear Indeterminate") {
            PixaLinearIndicator(progress = null, modifier = Modifier.fillMaxWidth())
        }

        ShowcaseSection("Linear Determinate") {
            PixaLinearIndicator(progress = 0.5f, modifier = Modifier.fillMaxWidth(), showLabel = true)
        }

        ShowcaseSection("Variants") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaCircularIndicator(progress = 0.6f, variant = ProgressVariant.Success, sizePreset = SizeVariant.Small)
                PixaCircularIndicator(progress = 0.6f, variant = ProgressVariant.Warning, sizePreset = SizeVariant.Small)
                PixaCircularIndicator(progress = 0.6f, variant = ProgressVariant.Error, sizePreset = SizeVariant.Small)
                PixaCircularIndicator(progress = 0.6f, variant = ProgressVariant.Info, sizePreset = SizeVariant.Small)
            }
        }

        ShowcaseSection("Interactive") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaCircularIndicator(progress = value, sizePreset = SizeVariant.Medium, showPercentage = true)
                PixaLinearIndicator(progress = value, modifier = Modifier.fillMaxWidth(), showLabel = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PixaButton(text = "-0.1", onClick = { value = (value - 0.1f).coerceAtLeast(0f) })
                    PixaButton(text = "+0.1", onClick = { value = (value + 0.1f).coerceAtMost(1f) })
                }
            }
        }

        ShowcaseSection("Pager Indicator") {
            PixaPagerIndicator(pageCount = 5, currentPage = 2)
        }
    }
}
