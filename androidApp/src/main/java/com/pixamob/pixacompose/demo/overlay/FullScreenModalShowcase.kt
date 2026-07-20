package com.pixamob.pixacompose.demo.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.surfaces.PixaFullScreenModal
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun FullScreenModalShowcase() {
    var showModal by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        ShowcaseSection("Full Screen Modal") {
            PixaButton(text = "Open Modal", onClick = { showModal = true })
        }
    }

    if (showModal) {
        PixaFullScreenModal(
            onDismissRequest = { showModal = false },
            title = "Modal Title"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.foundation.text.BasicText(
                    "This is a full-screen modal.",
                    style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody)
                )
                PixaButton(text = "Close", onClick = { showModal = false })
            }
        }
    }
}
