package com.pixamob.pixacompose.demo.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.surfaces.PixaSheet
import com.pixamob.pixacompose.components.surfaces.SheetExpandability
import com.pixamob.pixacompose.components.surfaces.SheetSnapPoint
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun BottomSheetShowcase() {
    var showSheet by remember { mutableStateOf(false) }
    var showSheet2 by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(AppTheme.colors.baseSurfaceDefault)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ShowcaseSection("Expandable Sheet") {
                PixaButton(text = "Open Sheet", onClick = { showSheet = true })
            }
            ShowcaseSection("Fixed Sheet") {
                PixaButton(text = "Open Fixed Sheet", onClick = { showSheet2 = true })
            }
        }

        if (showSheet) {
            PixaSheet(
                onDismissRequest = { showSheet = false },
                title = "Sheet Title",
                expandability = SheetExpandability.Expandable,
                initialSnapPoint = SheetSnapPoint.Middle
            ) {
                BasicText("Sheet content area.", style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
                PixaButton(text = "Close", onClick = { showSheet = false }, modifier = Modifier.align(Alignment.End).padding(top = 16.dp))
            }
        }

        if (showSheet2) {
            PixaSheet(
                onDismissRequest = { showSheet2 = false },
                title = "Quick Action",
                expandability = SheetExpandability.Fixed
            ) {
                BasicText("Fixed-height sheet.", style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
            }
        }
    }
}
