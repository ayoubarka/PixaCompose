package com.pixamob.pixacompose.demo.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.pixamob.pixacompose.components.navigation.NavIndicatorStyle
import com.pixamob.pixacompose.components.navigation.NavItem
import com.pixamob.pixacompose.components.navigation.PixaBottomNavBar
import com.pixamob.pixacompose.components.navigation.PixaCenterNavBar
import com.pixamob.pixacompose.components.navigation.PixaColorNavBar
import com.pixamob.pixacompose.components.navigation.PixaIndicatorNavBar
import com.pixamob.pixacompose.components.navigation.PixaPillNavBar
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun BottomNavBarShowcase() {
    val homeIcon = rememberVectorPainter(Icons.Default.Home)
    val searchIcon = rememberVectorPainter(Icons.Default.Search)
    val notifIcon = rememberVectorPainter(Icons.Default.Notifications)
    val historyIcon = rememberVectorPainter(Icons.Default.DateRange)
    val personIcon = rememberVectorPainter(Icons.Default.Person)
    val addIcon = rememberVectorPainter(Icons.Default.Add)

    // Five tabs for the indicator variants.
    val fiveTabs = listOf(
        NavItem("Home", homeIcon, homeIcon),
        NavItem("Search", searchIcon, searchIcon),
        NavItem("Alerts", notifIcon, notifIcon),
        NavItem("History", historyIcon, historyIcon),
        NavItem("Profile", personIcon, personIcon),
    )
    // Four tabs leave the middle gap free for the center action.
    val fourTabs = listOf(
        NavItem("Home", homeIcon, homeIcon),
        NavItem("Search", searchIcon, searchIcon),
        NavItem("History", historyIcon, historyIcon),
        NavItem("Profile", personIcon, personIcon),
    )

    ShowcaseScreen {
        ShowcaseSection("Base — explicit indicator override") {
            var index by remember { mutableStateOf(2) }
            PixaBottomNavBar(
                items = fiveTabs,
                selectedIndex = index,
                onItemSelected = { index = it },
                indicator = NavIndicatorStyle.Underline,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        ShowcaseSection("Color-only (all labels)") {
            var index by remember { mutableStateOf(2) }
            PixaColorNavBar(
                items = fiveTabs,
                selectedIndex = index,
                onItemSelected = { index = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        ShowcaseSection("Pill background") {
            var index by remember { mutableStateOf(0) }
            PixaPillNavBar(
                items = fiveTabs,
                selectedIndex = index,
                onItemSelected = { index = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        ShowcaseSection("Top-line indicator") {
            var index by remember { mutableStateOf(2) }
            PixaIndicatorNavBar(
                items = fiveTabs,
                selectedIndex = index,
                onItemSelected = { index = it },
                accent = NavIndicatorStyle.TopLine,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        ShowcaseSection("Dot indicator") {
            var index by remember { mutableStateOf(1) }
            PixaIndicatorNavBar(
                items = fiveTabs,
                selectedIndex = index,
                onItemSelected = { index = it },
                accent = NavIndicatorStyle.Dot,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        ShowcaseSection("Water-drop indicator") {
            var index by remember { mutableStateOf(3) }
            PixaIndicatorNavBar(
                items = fiveTabs,
                selectedIndex = index,
                onItemSelected = { index = it },
                accent = NavIndicatorStyle.WaterDrop,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        ShowcaseSection("Center action — centered") {
            var index by remember { mutableStateOf(0) }
            PixaCenterNavBar(
                items = fourTabs,
                selectedIndex = index,
                onItemSelected = { index = it },
                centerIcon = addIcon,
                onCenterAction = {},
                centerContentDescription = "Create",
                centerRaiseFraction = 0f,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        ShowcaseSection("Center action — raised above the bar") {
            var index by remember { mutableStateOf(2) }
            PixaCenterNavBar(
                items = fourTabs,
                selectedIndex = index,
                onItemSelected = { index = it },
                centerIcon = addIcon,
                onCenterAction = {},
                centerContentDescription = "Create",
                centerRaiseFraction = 0.6f,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
