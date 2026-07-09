package com.pixamob.pixacompose.demo.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.demo.ShowcaseSection

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
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaIcon(imageVector = Icons.Default.Home, contentDescription = "Home", customSize = 24.dp)
                PixaIcon(imageVector = Icons.Default.Search, contentDescription = "Search", customSize = 32.dp)
                PixaIcon(imageVector = Icons.Default.Settings, contentDescription = "Settings", customSize = 48.dp)
            }
        }

        ShowcaseSection("Tints") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaIcon(imageVector = Icons.Default.Home, contentDescription = "Red", tint = Color.Red, customSize = 32.dp)
                PixaIcon(imageVector = Icons.Default.Search, contentDescription = "Blue", tint = Color.Blue, customSize = 32.dp)
                PixaIcon(imageVector = Icons.Default.Settings, contentDescription = "Green", tint = Color.Green, customSize = 32.dp)
            }
        }

        ShowcaseSection("Animated") {
            PixaIcon(imageVector = Icons.Default.Home, contentDescription = "Animated", customSize = 48.dp, animation = true)
        }

        ShowcaseSection("Icons Row") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaIcon(imageVector = Icons.Default.Home, contentDescription = "Home", customSize = 32.dp)
                PixaIcon(imageVector = Icons.Default.Search, contentDescription = "Search", customSize = 32.dp)
                PixaIcon(imageVector = Icons.Default.Settings, contentDescription = "Settings", customSize = 32.dp)
            }
        }
    }
}
