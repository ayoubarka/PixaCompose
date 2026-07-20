package com.pixamob.pixacompose.demo.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.feedback.AlertVariant
import com.pixamob.pixacompose.components.feedback.PixaAlert
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun AlertShowcase() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaAlert(
                    title = "Information",
                    message = "This is an informational alert.",
                    variant = AlertVariant.Info,
                    modifier = Modifier.fillMaxWidth()
                )
                PixaAlert(
                    title = "Success",
                    message = "Your changes have been saved.",
                    variant = AlertVariant.Success,
                    modifier = Modifier.fillMaxWidth()
                )
                PixaAlert(
                    title = "Warning",
                    message = "Please review your input before continuing.",
                    variant = AlertVariant.Warning,
                    modifier = Modifier.fillMaxWidth()
                )
                PixaAlert(
                    title = "Error",
                    message = "An unexpected error occurred.",
                    variant = AlertVariant.Error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ShowcaseSection("With Dismiss") {
            PixaAlert(
                title = "Dismissible Alert",
                message = "Tap the close button to dismiss.",
                variant = AlertVariant.Info,
                onDismiss = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
