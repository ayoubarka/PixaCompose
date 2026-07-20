package com.pixamob.pixacompose.demo.actions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.actions.ButtonDockItem
import com.pixamob.pixacompose.components.actions.PixaButtonDock
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun ButtonDockShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Button Dock") {
            PixaButtonDock(
                items = listOf(
                    ButtonDockItem(id = "continue", text = "Continue", onClick = {})
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("With Secondary Action") {
            PixaButtonDock(
                items = listOf(
                    ButtonDockItem(id = "save", text = "Save", onClick = {}),
                    ButtonDockItem(id = "cancel", text = "Cancel", onClick = {})
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
