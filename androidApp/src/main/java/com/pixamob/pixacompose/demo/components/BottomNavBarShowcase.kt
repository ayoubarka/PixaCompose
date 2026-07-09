package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.pixamob.pixacompose.components.navigation.NavItem
import com.pixamob.pixacompose.components.navigation.PixaBottomNavBar
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun BottomNavBarShowcase() {
    var selectedIndex by remember { mutableStateOf(0) }
    var selectedCenter by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Standard") {
            PixaBottomNavBar(
                items = listOf(
                    NavItem("Home", rememberVectorPainter(image = Icons.Default.Home), rememberVectorPainter(image = Icons.Default.Home)),
                    NavItem("Search", rememberVectorPainter(image = Icons.Default.Search), rememberVectorPainter(image = Icons.Default.Search)),
                    NavItem("Profile", rememberVectorPainter(image = Icons.Default.Person), rememberVectorPainter(image = Icons.Default.Person)),
                    NavItem("Settings", rememberVectorPainter(image = Icons.Default.Settings), rememberVectorPainter(image = Icons.Default.Settings))
                ),
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("With Center Action") {
            PixaBottomNavBar(
                items = listOf(
                    NavItem("Home", rememberVectorPainter(image = Icons.Default.Home), rememberVectorPainter(image = Icons.Default.Home)),
                    NavItem("Search", rememberVectorPainter(image = Icons.Default.Search), rememberVectorPainter(image = Icons.Default.Search)),
                    NavItem("Profile", rememberVectorPainter(image = Icons.Default.Person), rememberVectorPainter(image = Icons.Default.Person))
                ),
                selectedIndex = selectedCenter,
                onItemSelected = { selectedCenter = it },
                withCenterAction = true,
                centerIcon = rememberVectorPainter(image = Icons.Default.Add),
                onCenterAction = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
