package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.IconTone
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun IconShowcase() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Sizes") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaIcon(imageVector = Icons.Default.Home, contentDescription = null, size = SizeVariant.Small)
                PixaIcon(imageVector = Icons.Default.Home, contentDescription = null, size = SizeVariant.Medium)
                PixaIcon(imageVector = Icons.Default.Home, contentDescription = null, size = SizeVariant.Large)
                PixaIcon(imageVector = Icons.Default.Home, contentDescription = null, size = SizeVariant.Huge)
            }
        }

        ShowcaseSection("Tones") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    PixaIcon(imageVector = Icons.Default.Star, contentDescription = null, tone = IconTone.Default)
                    PixaIcon(imageVector = Icons.Default.Star, contentDescription = null, tone = IconTone.Brand)
                    PixaIcon(imageVector = Icons.Default.Star, contentDescription = null, tone = IconTone.Success)
                    PixaIcon(imageVector = Icons.Default.Star, contentDescription = null, tone = IconTone.Warning)
                    PixaIcon(imageVector = Icons.Default.Star, contentDescription = null, tone = IconTone.Error)
                }
            }
        }

        ShowcaseSection("Multiple Icons") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaIcon(imageVector = Icons.Default.Home, contentDescription = "Home")
                PixaIcon(imageVector = Icons.Default.Search, contentDescription = "Search")
                PixaIcon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                PixaIcon(imageVector = Icons.Default.Star, contentDescription = "Star")
            }
        }

        ShowcaseSection("With Animation") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaIcon(imageVector = Icons.Default.Star, contentDescription = null, animation = true)
                PixaIcon(imageVector = Icons.Default.Home, contentDescription = null, animation = true)
                PixaIcon(imageVector = Icons.Default.Search, contentDescription = null, animation = true)
            }
        }
    }
}
