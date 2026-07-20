package com.pixamob.pixacompose.demo.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.navigation.DrawerItem
import com.pixamob.pixacompose.components.navigation.DrawerSection
import com.pixamob.pixacompose.components.navigation.PixaDrawer
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun DrawerShowcase() {
    var showDrawer by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(AppTheme.colors.baseSurfaceDefault)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ShowcaseSection("Interactive") {
                PixaButton(
                    text = if (showDrawer) "Close Drawer" else "Open Drawer",
                    onClick = { showDrawer = !showDrawer }
                )
            }
        }

        PixaDrawer(
            visible = showDrawer,
            onDismiss = { showDrawer = false },
            sections = listOf(
                DrawerSection(
                    title = "Main",
                    items = listOf(
                        DrawerItem(id = "home", title = "Home", icon = rememberVectorPainter(Icons.Default.Home)),
                        DrawerItem(id = "profile", title = "Profile", icon = rememberVectorPainter(Icons.Default.Person)),
                        DrawerItem(id = "settings", title = "Settings", icon = rememberVectorPainter(Icons.Default.Settings))
                    )
                )
            ),
            onItemClick = { showDrawer = false },
            selectedItemId = "home",
            header = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BasicText("John Doe", style = AppTheme.typography.titleBold.copy(color = AppTheme.colors.baseContentTitle))
                    BasicText("john@example.com", style = AppTheme.typography.captionRegular.copy(color = AppTheme.colors.baseContentCaption))
                }
            }
        )
    }
}
