package com.pixamob.pixacompose.demo.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.FABVariant
import com.pixamob.pixacompose.components.actions.FabAction
import com.pixamob.pixacompose.components.actions.PixaExpandableFab
import com.pixamob.pixacompose.components.actions.PixaFAB
import com.pixamob.pixacompose.components.actions.PixaFabPill
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun FABShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Variants") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaFAB(icon = rememberVectorPainter(Icons.Default.Add), onClick = {}, variant = FABVariant.Filled)
                PixaFAB(icon = rememberVectorPainter(Icons.Default.Add), onClick = {}, variant = FABVariant.Outlined)
                PixaFAB(icon = rememberVectorPainter(Icons.Default.Add), onClick = {}, variant = FABVariant.Tonal)
            }
        }

        ShowcaseSection("Sizes") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaFAB(icon = rememberVectorPainter(Icons.Default.Add), onClick = {}, size = SizeVariant.Small)
                PixaFAB(icon = rememberVectorPainter(Icons.Default.Add), onClick = {}, size = SizeVariant.Medium)
                PixaFAB(icon = rememberVectorPainter(Icons.Default.Add), onClick = {}, size = SizeVariant.Large)
                PixaFAB(icon = rememberVectorPainter(Icons.Default.Add), onClick = {}, size = SizeVariant.Huge)
            }
        }

        ShowcaseSection("Pill FAB") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaFabPill(label = "Create", onClick = {}, icon = rememberVectorPainter(Icons.Default.Add))
                PixaFabPill(label = "Edit", onClick = {}, icon = rememberVectorPainter(Icons.Default.Edit))
            }
        }

        ShowcaseSection("States") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaFAB(icon = rememberVectorPainter(Icons.Default.Add), onClick = {}, enabled = true)
                PixaFAB(icon = rememberVectorPainter(Icons.Default.Add), onClick = {}, enabled = false)
            }
        }
    }
}
