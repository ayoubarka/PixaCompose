package com.pixamob.pixacompose.demo.overlay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
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
import com.pixamob.pixacompose.components.overlay.MenuItem
import com.pixamob.pixacompose.components.overlay.PixaMenu
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun MenuShowcase() {
    var showMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        ShowcaseSection("Menu") {
            PixaButton(text = "Open Menu", onClick = { showMenu = true })
        }
    }

    PixaMenu(
        visible = showMenu,
        onDismiss = { showMenu = false },
        items = listOf(
            MenuItem(id = "edit", title = "Edit", icon = rememberVectorPainter(Icons.Default.Edit)),
            MenuItem(id = "share", title = "Share", icon = rememberVectorPainter(Icons.Default.Share)),
            MenuItem(id = "delete", title = "Delete")
        ),
        onItemClick = { showMenu = false },
        alignment = Alignment.TopStart
    )
}
