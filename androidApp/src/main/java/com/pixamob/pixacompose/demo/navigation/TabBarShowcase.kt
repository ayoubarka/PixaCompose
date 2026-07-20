package com.pixamob.pixacompose.demo.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.pixamob.pixacompose.components.navigation.PixaTabBar
import com.pixamob.pixacompose.components.navigation.TabBarItem
import com.pixamob.pixacompose.components.navigation.TabBarVariant
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun TabBarShowcase() {
    var selectedIndex by remember { mutableStateOf(0) }

    ShowcaseScreen {
        ShowcaseSection("Underline Variant") {
            PixaTabBar(
                tabs = listOf(
                    TabBarItem(title = "Home", icon = rememberVectorPainter(Icons.Default.Home)),
                    TabBarItem(title = "Profile", icon = rememberVectorPainter(Icons.Default.Person)),
                    TabBarItem(title = "Settings", icon = rememberVectorPainter(Icons.Default.Settings))
                ),
                selectedIndex = selectedIndex,
                onTabSelected = { selectedIndex = it },
                variant = TabBarVariant.Underline,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("Filled Variant") {
            PixaTabBar(
                tabs = listOf(
                    TabBarItem(title = "Home"),
                    TabBarItem(title = "Profile"),
                    TabBarItem(title = "Settings")
                ),
                selectedIndex = selectedIndex,
                onTabSelected = { selectedIndex = it },
                variant = TabBarVariant.Filled,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("Pill Variant") {
            PixaTabBar(
                tabs = listOf(
                    TabBarItem(title = "Day"),
                    TabBarItem(title = "Week"),
                    TabBarItem(title = "Month")
                ),
                selectedIndex = selectedIndex,
                onTabSelected = { selectedIndex = it },
                variant = TabBarVariant.Pill,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
