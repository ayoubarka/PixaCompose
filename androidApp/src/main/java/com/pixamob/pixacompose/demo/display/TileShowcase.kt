package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.display.PixaTile
import com.pixamob.pixacompose.components.display.TileBehavior
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun TileShowcase() {
    var tileSelected by remember { mutableStateOf(false) }

    ShowcaseScreen {
        ShowcaseSection("Behaviors") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaTile(label = "Action", onClick = {}, behavior = TileBehavior.Action)
                PixaTile(label = "Selection", onClick = {}, behavior = TileBehavior.Selection, selected = true)
            }
        }

        ShowcaseSection("Sizes") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaTile(label = "Sm", onClick = {}, size = SizeVariant.Small)
                PixaTile(label = "Md", onClick = {}, size = SizeVariant.Medium)
                PixaTile(label = "Lg", onClick = {}, size = SizeVariant.Large)
            }
        }

        ShowcaseSection("With Icon") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaTile(label = "Home", onClick = {}, leadingContent = { PixaIcon(imageVector = Icons.Default.Home, contentDescription = null) })
            }
        }

        ShowcaseSection("Selectable") {
            Column {
                PixaTile(
                    label = if (tileSelected) "Selected" else "Tap me",
                    selected = tileSelected,
                    onClick = { tileSelected = !tileSelected },
                    behavior = if (tileSelected) TileBehavior.Selection else TileBehavior.Action
                )
            }
        }
    }
}
