package com.pixamob.pixacompose.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize

/**
 * Overlay Category - Dialogs, BottomSheets, Tooltips, Popovers, Menus
 * Simplified demo version showing overlay components structure
 */
@Composable
fun OverlayDemoScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.baseSurfaceSubtle)
    ) {
        DemoScreenHeader(title = "Overlay", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(HierarchicalSize.Padding.Medium),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)
        ) {
            // ===== DIALOGS =====
            item {
                DemoSection(
                    title = "Dialogs",
                    description = "Modal dialog boxes"
                ) {
                    var showDialog by remember { mutableStateOf(false) }

                    Column {
                        androidx.compose.material3.Button(
                            onClick = { showDialog = !showDialog }
                        ) {
                            Text(if (showDialog) "Close Dialog" else "Show Dialog")
                        }

                        if (showDialog) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(AppTheme.colors.baseSurfaceDefault)
                                    .padding(HierarchicalSize.Padding.Large),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Dialog Content",
                                        style = AppTheme.typography.subtitleBold
                                    )
                                    Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)
                                    ) {
                                        androidx.compose.material3.Button(onClick = { showDialog = false }) {
                                            Text("Cancel")
                                        }
                                        androidx.compose.material3.Button(onClick = { showDialog = false }) {
                                            Text("OK")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ===== BOTTOM SHEETS =====
            item {
                DemoSection(
                    title = "Bottom Sheets",
                    description = "Slide-up sheet from bottom"
                ) {
                    var showBottomSheet by remember { mutableStateOf(false) }

                    Column {
                        androidx.compose.material3.Button(
                            onClick = { showBottomSheet = !showBottomSheet }
                        ) {
                            Text(if (showBottomSheet) "Hide Sheet" else "Show Sheet")
                        }

                        if (showBottomSheet) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(AppTheme.colors.baseSurfaceDefault)
                                    .padding(HierarchicalSize.Padding.Large),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Bottom Sheet Content",
                                    style = AppTheme.typography.bodyRegular
                                )
                            }
                        }
                    }
                }
            }

            // ===== MENUS =====
            item {
                DemoSection(
                    title = "Dropdown Menus",
                    description = "Context menu and dropdown options"
                ) {
                    var showMenu by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            androidx.compose.material3.Button(
                                onClick = { showMenu = !showMenu }
                            ) {
                                Text("Menu Options")
                            }

                            if (showMenu) {
                                Column(
                                    modifier = Modifier
                                        .padding(top = HierarchicalSize.Spacing.Medium)
                                        .background(AppTheme.colors.baseSurfaceDefault)
                                        .padding(HierarchicalSize.Padding.Small)
                                ) {
                                    listOf("Edit", "Share", "Delete").forEach { item ->
                                        androidx.compose.material3.TextButton(
                                            onClick = { showMenu = false },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(item)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))
            }
        }
    }
}
