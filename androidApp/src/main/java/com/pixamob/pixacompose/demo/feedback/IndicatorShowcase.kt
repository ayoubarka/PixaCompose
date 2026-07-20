package com.pixamob.pixacompose.demo.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.feedback.PixaCircularIndicator
import com.pixamob.pixacompose.components.feedback.PixaLinearIndicator
import com.pixamob.pixacompose.components.feedback.ProgressVariant
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun IndicatorShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Linear Indicators") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaLinearIndicator(progress = 0.5f, variant = ProgressVariant.Primary, modifier = Modifier.fillMaxWidth())
                PixaLinearIndicator(progress = 0.7f, variant = ProgressVariant.Success, modifier = Modifier.fillMaxWidth())
                PixaLinearIndicator(progress = 0.3f, variant = ProgressVariant.Warning, modifier = Modifier.fillMaxWidth())
                PixaLinearIndicator(progress = 0.9f, variant = ProgressVariant.Error, modifier = Modifier.fillMaxWidth())
            }
        }

        ShowcaseSection("Circular Indicators") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaCircularIndicator(progress = 0.5f, variant = ProgressVariant.Primary)
                PixaCircularIndicator(progress = 0.7f, variant = ProgressVariant.Success)
                PixaCircularIndicator(progress = 0.3f, variant = ProgressVariant.Warning)
            }
        }

        ShowcaseSection("Indeterminate") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaLinearIndicator(variant = ProgressVariant.Primary, modifier = Modifier.fillMaxWidth())
                PixaCircularIndicator(variant = ProgressVariant.Primary)
            }
        }
    }
}
