package com.pixamob.pixacompose.demo.feedback

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.feedback.PixaSystemBanner
import com.pixamob.pixacompose.components.feedback.SystemBannerVariant
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun SystemBannerShowcase() {
    var showWarning by remember { mutableStateOf(true) }
    var showInfo by remember { mutableStateOf(true) }

    ShowcaseScreen {
        ShowcaseSection("System Banner") {
            PixaSystemBanner(
                visible = showWarning,
                message = "You are offline",
                variant = SystemBannerVariant.Warning,
                dismissible = true,
                onDismiss = { showWarning = false },
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("Info Banner") {
            PixaSystemBanner(
                visible = showInfo,
                message = "New update available",
                variant = SystemBannerVariant.Accent,
                dismissible = true,
                onDismiss = { showInfo = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
