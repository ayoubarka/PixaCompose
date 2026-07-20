package com.pixamob.pixacompose.demo.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
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
import com.pixamob.pixacompose.components.navigation.NavItem
import com.pixamob.pixacompose.components.navigation.PixaBottomNavBar
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun BottomNavBarShowcase() {
    var selectedIndex by remember { mutableStateOf(0) }
    val homeIcon = rememberVectorPainter(Icons.Default.Home)
    val searchIcon = rememberVectorPainter(Icons.Default.Search)
    val notifIcon = rememberVectorPainter(Icons.Default.Notifications)
    val personIcon = rememberVectorPainter(Icons.Default.Person)

    ShowcaseScreen {
        ShowcaseSection("Bottom Navigation") {
            PixaBottomNavBar(
                items = listOf(
                    NavItem(title = "Home", iconSelected = homeIcon, iconUnselected = homeIcon),
                    NavItem(title = "Search", iconSelected = searchIcon, iconUnselected = searchIcon),
                    NavItem(title = "Notifications", iconSelected = notifIcon, iconUnselected = notifIcon),
                    NavItem(title = "Profile", iconSelected = personIcon, iconUnselected = personIcon)
                ),
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
