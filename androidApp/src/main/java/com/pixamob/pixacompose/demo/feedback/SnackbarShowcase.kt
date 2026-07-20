package com.pixamob.pixacompose.demo.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.feedback.SnackbarHost
import com.pixamob.pixacompose.components.feedback.rememberSnackbarHostState
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun SnackbarShowcase() {
    val snackbarState = rememberSnackbarHostState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(AppTheme.colors.baseSurfaceDefault)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ShowcaseSection("Interactive") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PixaButton(text = "Show Default Snackbar", onClick = { scope.launch { snackbarState.showSnackbar(message = "Hello from PixaCompose!") } })
                    PixaButton(text = "Show Success Snackbar", onClick = { scope.launch { snackbarState.showSuccessSnackbar(message = "Changes saved successfully") } })
                    PixaButton(text = "Show Error Snackbar", onClick = { scope.launch { snackbarState.showErrorSnackbar(message = "Failed to save") } })
                }
            }

            ShowcaseSection("With Action") {
                PixaButton(text = "Snackbar with Undo", onClick = { scope.launch { snackbarState.showSnackbar(message = "Item deleted", actionLabel = "Undo", onAction = { scope.launch { snackbarState.showSuccessSnackbar("Undone!") } }) } })
            }
        }

        SnackbarHost(hostState = snackbarState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
