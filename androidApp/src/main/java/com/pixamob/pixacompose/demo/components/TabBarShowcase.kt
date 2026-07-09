package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.pixamob.pixacompose.components.navigation.PixaTabBar
import com.pixamob.pixacompose.components.navigation.TabBarItem
import com.pixamob.pixacompose.components.navigation.TabBarVariant
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun TabBarShowcase() {
    var selectedFixed by remember { mutableStateOf(0) }
    var selectedScrollable by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Fixed Tabs") {
            PixaTabBar(
                tabs = listOf(
                    TabBarItem("Home", icon = rememberVectorPainter(image = Icons.Default.Home)),
                    TabBarItem("Search", icon = rememberVectorPainter(image = Icons.Default.Search)),
                    TabBarItem("Profile", icon = rememberVectorPainter(image = Icons.Default.Person))
                ),
                selectedIndex = selectedFixed,
                onTabSelected = { selectedFixed = it },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Selected: $selectedFixed",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )
        }

        ShowcaseSection("Scrollable Tabs") {
            PixaTabBar(
                tabs = listOf(
                    TabBarItem("Home"),
                    TabBarItem("Search"),
                    TabBarItem("Favorites"),
                    TabBarItem("Profile"),
                    TabBarItem("Settings")
                ),
                selectedIndex = selectedScrollable,
                onTabSelected = { selectedScrollable = it },
                scrollable = true,
                variant = TabBarVariant.Filled,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Selected: $selectedScrollable",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )
        }
    }
}
