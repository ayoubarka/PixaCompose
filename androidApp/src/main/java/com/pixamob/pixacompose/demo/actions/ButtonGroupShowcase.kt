package com.pixamob.pixacompose.demo.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.ButtonGroupItem
import com.pixamob.pixacompose.components.actions.PixaButtonGroup
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun ButtonGroupShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Button Group") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaButtonGroup(
                    items = listOf(
                        ButtonGroupItem(id = "create", text = "Create", leadingIcon = rememberVectorPainter(Icons.Default.Add)),
                        ButtonGroupItem(id = "edit", text = "Edit", leadingIcon = rememberVectorPainter(Icons.Default.Edit)),
                        ButtonGroupItem(id = "delete", text = "Delete", leadingIcon = rememberVectorPainter(Icons.Default.Delete))
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                PixaButtonGroup(
                    items = listOf(
                        ButtonGroupItem(id = "yes", text = "Yes"),
                        ButtonGroupItem(id = "no", text = "No"),
                        ButtonGroupItem(id = "maybe", text = "Maybe")
                    ),
                    size = SizeVariant.Compact,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
