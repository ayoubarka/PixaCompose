package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.feedback.EmptyContent
import com.pixamob.pixacompose.components.feedback.EmptySearchResults
import com.pixamob.pixacompose.components.feedback.EmptyStateType
import com.pixamob.pixacompose.components.feedback.NetworkError
import com.pixamob.pixacompose.components.feedback.PixaEmptyState
import com.pixamob.pixacompose.components.feedback.ServerError
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun EmptyStateShowcase() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Empty Types") {
            PixaEmptyState(type = EmptyStateType.Empty.NoContent, size = SizeVariant.Compact)
        }

        ShowcaseSection("No Results") {
            EmptySearchResults(query = "pixacompose")
        }

        ShowcaseSection("Network Error") {
            NetworkError(onRetry = {})
        }

        ShowcaseSection("Server Error") {
            ServerError(onRetry = {}, onContactSupport = {})
        }

        ShowcaseSection("Custom With Action") {
            EmptyContent(title = "No items yet", description = "Add your first item to get started", actionText = "Add Item", onAction = {})
        }
    }
}
