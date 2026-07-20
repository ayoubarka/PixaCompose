package com.pixamob.pixacompose.demo.overlay

import androidx.compose.foundation.layout.Arrangement
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
import com.pixamob.pixacompose.components.overlay.DialogVariant
import com.pixamob.pixacompose.components.overlay.PixaAlertDialog
import com.pixamob.pixacompose.components.overlay.PixaConfirmDialog
import com.pixamob.pixacompose.components.overlay.PixaDestructiveDialog
import com.pixamob.pixacompose.components.overlay.PixaDialog
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun DialogShowcase() {
    var showAlert by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    var showDestructive by remember { mutableStateOf(false) }
    var showCustom by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Alert Dialog") {
            PixaButton(text = "Open Alert Dialog", onClick = { showAlert = true })
        }
        ShowcaseSection("Confirm Dialog") {
            PixaButton(text = "Open Confirm Dialog", onClick = { showConfirm = true })
        }
        ShowcaseSection("Destructive Dialog") {
            PixaButton(text = "Open Destructive Dialog", onClick = { showDestructive = true })
        }
        ShowcaseSection("Custom Dialog") {
            PixaButton(text = "Open Custom Dialog", onClick = { showCustom = true })
        }
    }

    if (showAlert) {
        PixaAlertDialog(
            onDismissRequest = { showAlert = false },
            title = "Information",
            message = "This is an alert dialog",
            confirmText = "OK",
            variant = DialogVariant.Info
        )
    }
    if (showConfirm) {
        PixaConfirmDialog(
            onDismissRequest = { showConfirm = false },
            title = "Confirm Action",
            message = "Are you sure you want to proceed?",
            confirmText = "Confirm",
            dismissText = "Cancel",
            onConfirm = { showConfirm = false }
        )
    }
    if (showDestructive) {
        PixaDestructiveDialog(
            onDismissRequest = { showDestructive = false },
            title = "Delete Item",
            message = "This action cannot be undone",
            confirmText = "Delete",
            dismissText = "Cancel",
            onConfirm = { showDestructive = false }
        )
    }
    if (showCustom) {
        PixaDialog(
            onDismissRequest = { showCustom = false },
            title = "Custom Dialog",
            message = "This dialog uses custom variant",
            variant = DialogVariant.Success,
            confirmText = "Great",
            dismissText = "Later",
            onConfirm = { showCustom = false }
        )
    }
}
