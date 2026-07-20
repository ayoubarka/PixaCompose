package com.pixamob.pixacompose.demo.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.navigation.PixaTopNavBar
import com.pixamob.pixacompose.components.navigation.TopNavAction
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun TopNavBarShowcase() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Simple Title") {
            PixaTopNavBar(
                title = "Dashboard",
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("Title + Subtitle") {
            PixaTopNavBar(
                title = "Settings",
                subtitle = "Manage your preferences",
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("With Actions") {
            PixaTopNavBar(
                title = "Messages",
                subtitle = "3 unread",
                startActions = listOf(
                    TopNavAction(
                        icon = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ArrowBack),
                        description = "Back",
                        onClick = {}
                    )
                ),
                endActions = listOf(
                    TopNavAction(
                        icon = rememberVectorPainter(image = Icons.Default.Search),
                        description = "Search",
                        onClick = {}
                    ),
                    TopNavAction(
                        icon = rememberVectorPainter(image = Icons.Default.Notifications),
                        description = "Notifications",
                        onClick = {},
                        badge = 3
                    ),
                    TopNavAction(
                        icon = rememberVectorPainter(image = Icons.Default.Settings),
                        description = "Settings",
                        onClick = {}
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
