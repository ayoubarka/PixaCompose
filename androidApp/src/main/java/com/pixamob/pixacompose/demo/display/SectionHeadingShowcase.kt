package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.pixamob.pixacompose.components.display.PixaSectionHeading
import com.pixamob.pixacompose.components.display.SectionHeadingTrailing
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun SectionHeadingShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Section Heading") {
            PixaSectionHeading(
                heading = "Account Settings",
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("With Action") {
            PixaSectionHeading(
                heading = "Notifications",
                trailing = SectionHeadingTrailing.TextButton("See all", onClick = {}),
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("With Icon") {
            PixaSectionHeading(
                heading = "Security",
                trailing = SectionHeadingTrailing.IconButton(
                    icon = rememberVectorPainter(Icons.Default.Settings),
                    onClick = {},
                    contentDescription = "Settings"
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
