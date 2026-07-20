package com.pixamob.pixacompose.demo.feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.pixamob.pixacompose.components.feedback.EmptyStateType
import com.pixamob.pixacompose.components.feedback.PixaEmptyState
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun EmptyStateShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Types") {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                PixaEmptyState(
                    type = EmptyStateType.Empty.NoContent,
                    title = "No items",
                    description = "Your list is empty.",
                    modifier = Modifier.fillMaxWidth()
                )
                PixaEmptyState(
                    type = EmptyStateType.Empty.NoResults,
                    title = "No results",
                    description = "Try a different search term.",
                    icon = rememberVectorPainter(Icons.Default.Search),
                    modifier = Modifier.fillMaxWidth()
                )
                PixaEmptyState(
                    type = EmptyStateType.Server.InternalError,
                    title = "Something went wrong",
                    description = "Please try again later.",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ShowcaseSection("With Action") {
            PixaEmptyState(
                type = EmptyStateType.Empty.NoContent,
                title = "No messages",
                description = "Start a conversation",
                primaryActionText = "New Message",
                onPrimaryAction = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
