package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaTag
import com.pixamob.pixacompose.components.display.TagColor
import com.pixamob.pixacompose.components.display.TagHierarchy
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun TagShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Hierarchies") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaTag(text = "Primary", hierarchy = TagHierarchy.Primary)
                PixaTag(text = "Secondary", hierarchy = TagHierarchy.Secondary)
                PixaTag(text = "Tertiary", hierarchy = TagHierarchy.Tertiary)
            }
        }

        ShowcaseSection("Colors") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaTag(text = "Neutral", color = TagColor.Neutral)
                PixaTag(text = "Brand", color = TagColor.Brand)
                PixaTag(text = "Accent", color = TagColor.Accent)
                PixaTag(text = "Info", color = TagColor.Info)
                PixaTag(text = "Success", color = TagColor.Success)
            }
        }

        ShowcaseSection("Sizes") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaTag(text = "Sm", size = SizeVariant.Small)
                PixaTag(text = "Md", size = SizeVariant.Medium)
                PixaTag(text = "Lg", size = SizeVariant.Large)
            }
        }

        ShowcaseSection("States") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    PixaTag(text = "Default")
                    PixaTag(text = "Disabled", enabled = false)
                    PixaTag(text = "Dismissible", onDismiss = {})
                }
            }
        }
    }
}
