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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.overlay.PixaPopover
import com.pixamob.pixacompose.components.overlay.PopoverPosition
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun PopoverShowcase() {
    var showPopover by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Basic Popover") {
            Box {
                PixaButton(text = "Toggle Popover", onClick = { showPopover = !showPopover })
                PixaPopover(
                    visible = showPopover,
                    onDismiss = { showPopover = false },
                    position = PopoverPosition.BottomCenter
                ) {
                    Column {
                        Text("Popover Content", style = AppTheme.typography.bodyBold, color = AppTheme.colors.baseContentTitle)
                        Text("This is a contextual popup", style = AppTheme.typography.captionRegular, color = AppTheme.colors.baseContentBody)
                    }
                }
            }
        }
    }
}
