package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.navigation.DrawerItem
import com.pixamob.pixacompose.components.navigation.DrawerSection
import com.pixamob.pixacompose.components.navigation.PixaDrawer
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun DrawerShowcase() {
    var showDrawer by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ShowcaseSection("Interactive") {
                PixaButton(
                    text = if (showDrawer) "Close Drawer" else "Open Drawer",
                    onClick = { showDrawer = !showDrawer },
                    variant = if (showDrawer) ButtonVariant.Outlined else ButtonVariant.Filled
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
                        DrawerItem(id = "home", title = "Home", icon = rememberVectorPainter(image = Icons.Default.Home)),
                        DrawerItem(id = "profile", title = "Profile", icon = rememberVectorPainter(image = Icons.Default.Person)),
                        DrawerItem(id = "settings", title = "Settings", icon = rememberVectorPainter(image = Icons.Default.Settings))
                    )
                ),
                DrawerSection(
                    title = "Account",
                    items = listOf(
                        DrawerItem(id = "logout", title = "Logout", icon = rememberVectorPainter(image = Icons.Default.Logout))
                    )
                )
            ),
            onItemClick = { showDrawer = false },
            selectedItemId = "home",
            header = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "John Doe",
                        style = AppTheme.typography.titleBold,
                        color = AppTheme.colors.baseContentTitle
                    )
                    Text(
                        text = "john.doe@example.com",
                        style = AppTheme.typography.captionRegular,
                        color = AppTheme.colors.baseContentCaption
                    )
                }
            }
        )
    }
}
