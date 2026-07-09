package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.overlay.BottomSheetSizeVariant
import com.pixamob.pixacompose.components.overlay.PixaBottomSheet
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun BottomSheetShowcase() {
    var showSheet by remember { mutableStateOf(false) }
    var showSheet2 by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Basic Bottom Sheet") {
            PixaButton(text = "Open Bottom Sheet", onClick = { showSheet = true })
        }

        ShowcaseSection("Compact Bottom Sheet") {
            PixaButton(text = "Open Compact Sheet", onClick = { showSheet2 = true })
        }
    }

    if (showSheet) {
        PixaBottomSheet(
            onDismissRequest = { showSheet = false },
            size = BottomSheetSizeVariant.Standard
        ) {
            Text(
                text = "Sheet Title",
                style = AppTheme.typography.titleBold,
                color = AppTheme.colors.baseContentTitle,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "This is the bottom sheet content area. Drag the handle to dismiss.",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )
            PixaButton(
                text = "Close",
                onClick = { showSheet = false },
                modifier = Modifier.align(Alignment.End).padding(top = 16.dp)
            )
        }
    }

    if (showSheet2) {
        PixaBottomSheet(
            onDismissRequest = { showSheet2 = false },
            size = BottomSheetSizeVariant.Compact
        ) {
            Text(
                text = "Quick Action",
                style = AppTheme.typography.subtitleBold,
                color = AppTheme.colors.baseContentTitle
            )
            Text(
                text = "A compact sheet for quick interactions.",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
