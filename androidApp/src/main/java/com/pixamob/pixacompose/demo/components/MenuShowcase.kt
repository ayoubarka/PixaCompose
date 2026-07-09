package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.overlay.MenuItem
import com.pixamob.pixacompose.components.overlay.MenuItemType
import com.pixamob.pixacompose.components.overlay.PixaMenu
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun MenuShowcase() {
    var showMenu1 by remember { mutableStateOf(false) }
    var showMenu2 by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Basic Menu") {
            Box {
                PixaButton(text = "Show Menu", onClick = { showMenu1 = true })
                PixaMenu(
                    visible = showMenu1,
                    onDismiss = { showMenu1 = false },
                    items = listOf(
                        MenuItem(id = "settings", title = "Settings"),
                        MenuItem(id = "profile", title = "Profile"),
                        MenuItem(id = "help", title = "Help")
                    ),
                    onItemClick = {
                        selectedAction = "Clicked: ${it.title}"
                        showMenu1 = false
                    }
                )
            }
        }

        ShowcaseSection("With Icons & Types") {
            Box {
                PixaButton(text = "Show Advanced Menu", onClick = { showMenu2 = true })
                PixaMenu(
                    visible = showMenu2,
                    onDismiss = { showMenu2 = false },
                    items = listOf(
                        MenuItem(id = "edit", title = "Edit"),
                        MenuItem(id = "share", title = "Share"),
                        MenuItem(id = "delete", title = "Delete", type = MenuItemType.Destructive)
                    ),
                    onItemClick = {
                        selectedAction = "Clicked: ${it.title}"
                        showMenu2 = false
                    }
                )
            }
        }
    }
}
