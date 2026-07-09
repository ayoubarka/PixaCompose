package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.feedback.ToastHost
import com.pixamob.pixacompose.components.feedback.ToastPosition
import com.pixamob.pixacompose.components.feedback.ToastVariant
import com.pixamob.pixacompose.components.feedback.rememberToastHostState
import com.pixamob.pixacompose.demo.ShowcaseSection
import kotlinx.coroutines.launch

@Composable
fun ToastShowcase() {
    val toastState = rememberToastHostState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ShowcaseSection("Interactive") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PixaButton(text = "Show Info Toast", onClick = { scope.launch { toastState.showToast(message = "This is an info toast", variant = ToastVariant.Info) } })
                    PixaButton(text = "Show Success Toast", onClick = { scope.launch { toastState.showSuccessToast(message = "Operation successful!") } })
                    PixaButton(text = "Show Warning Toast", onClick = { scope.launch { toastState.showWarningToast(message = "Please be careful") } })
                    PixaButton(text = "Show Error Toast", onClick = { scope.launch { toastState.showErrorToast(message = "Something went wrong") } })
                }
            }

            ShowcaseSection("With Action") {
                PixaButton(text = "Toast with Undo", onClick = { scope.launch { toastState.showToast(message = "Item deleted", variant = ToastVariant.Success, actionText = "Undo", onAction = { scope.launch { toastState.showSuccessToast("Undone!") } }) } })
            }
        }

        ToastHost(hostState = toastState, position = ToastPosition.Bottom, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
