package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.pixamob.pixacompose.components.feedback.AlertVariant
import com.pixamob.pixacompose.components.feedback.PixaAlert
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun AlertShowcase() {
    var showDismissible by remember { mutableStateOf(true) }
    var showAction by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaAlert(title = "Info Alert", message = "This is an informational message", variant = AlertVariant.Info, modifier = Modifier.fillMaxWidth())
                PixaAlert(title = "Success Alert", message = "Operation completed successfully", variant = AlertVariant.Success, modifier = Modifier.fillMaxWidth())
                PixaAlert(title = "Warning Alert", message = "Please check your settings", variant = AlertVariant.Warning, modifier = Modifier.fillMaxWidth())
                PixaAlert(title = "Error Alert", message = "Something went wrong", variant = AlertVariant.Error, modifier = Modifier.fillMaxWidth())
            }
        }

        ShowcaseSection("Dismissible") {
            if (showDismissible) {
                PixaAlert(
                    title = "Dismiss Me",
                    message = "Tap the X to dismiss this alert",
                    variant = AlertVariant.Info,
                    dismissible = true,
                    onDismiss = { showDismissible = false },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ShowcaseSection("With Action") {
            if (showAction) {
                PixaAlert(
                    title = "Action Alert",
                    message = "This alert has an action button",
                    variant = AlertVariant.Success,
                    actionText = "Undo",
                    onAction = { showAction = false },
                    dismissible = true,
                    onDismiss = { showAction = false },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
