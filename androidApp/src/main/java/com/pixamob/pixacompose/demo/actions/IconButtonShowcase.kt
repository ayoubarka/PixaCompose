package com.pixamob.pixacompose.demo.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.IconButtonVariant
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun IconButtonShowcase() {
    var isSelected by remember { mutableStateOf(false) }
    val heartIcon = rememberVectorPainter(Icons.Default.Favorite)

    ShowcaseScreen {
        ShowcaseSection("Variants") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaIconButton(icon = heartIcon, onClick = {}, variant = IconButtonVariant.Ghost)
                PixaIconButton(icon = heartIcon, onClick = {}, variant = IconButtonVariant.Filled)
                PixaIconButton(icon = heartIcon, onClick = {}, variant = IconButtonVariant.Outlined)
                PixaIconButton(icon = heartIcon, onClick = {}, variant = IconButtonVariant.Tonal)
            }
        }

        ShowcaseSection("Sizes") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaIconButton(icon = heartIcon, onClick = {}, size = SizeVariant.Small)
                PixaIconButton(icon = heartIcon, onClick = {}, size = SizeVariant.Medium)
                PixaIconButton(icon = heartIcon, onClick = {}, size = SizeVariant.Large)
            }
        }

        ShowcaseSection("Multiple Icons") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaIconButton(icon = rememberVectorPainter(Icons.Default.Home), onClick = {}, contentDescription = "Home")
                PixaIconButton(icon = rememberVectorPainter(Icons.Default.Favorite), onClick = {}, contentDescription = "Favorites")
                PixaIconButton(icon = rememberVectorPainter(Icons.Default.Settings), onClick = {}, contentDescription = "Settings")
            }
        }

        ShowcaseSection("States") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaIconButton(icon = heartIcon, onClick = {}, selected = false)
                PixaIconButton(icon = heartIcon, onClick = {}, selected = true)
                PixaIconButton(icon = heartIcon, onClick = {}, enabled = false)
            }
        }
    }
}
