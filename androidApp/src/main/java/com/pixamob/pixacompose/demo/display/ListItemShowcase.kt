package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.pixamob.pixacompose.components.display.ListItemLeading
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.display.PixaListItem
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.HierarchicalSize

@Composable
fun ListItemShowcase() {
    ShowcaseScreen {
        ShowcaseSection("List Item") {
            PixaListItem(
                title = "John Doe",
                subtitle = "john@example.com",
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("With Leading Icon") {
            PixaListItem(
                leading = ListItemLeading.Icon {
                    PixaIcon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        customSize = HierarchicalSize.Icon.Medium
                    )
                },
                title = "Profile",
                subtitle = "View and edit your profile",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
