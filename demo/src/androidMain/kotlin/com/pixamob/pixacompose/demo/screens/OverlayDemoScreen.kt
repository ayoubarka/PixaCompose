package com.pixamob.pixacompose.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.overlay.PixaDialog
import com.pixamob.pixacompose.components.overlay.DialogVariant
import com.pixamob.pixacompose.components.overlay.PixaMenu
import com.pixamob.pixacompose.components.overlay.MenuItem
import com.pixamob.pixacompose.components.overlay.MenuItemType
import com.pixamob.pixacompose.components.overlay.PixaPopover
import com.pixamob.pixacompose.components.overlay.PopoverPosition
import com.pixamob.pixacompose.components.overlay.PixaTooltip
import com.pixamob.pixacompose.components.overlay.TooltipPosition
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant

/**
 * Overlay Category - Dialogs, BottomSheets, Tooltips, Popovers, Menus
 * Showcasing all overlay components with real usage
 */
@Composable
fun OverlayDemoScreen(onBack: () -> Unit) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showPopover by remember { mutableStateOf(false) }
    var showTooltip by remember { mutableStateOf(false) }

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
                    title = "PixaDialog",
                    description = "Modal dialog with variants: Confirmation, Alert, Custom"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        PixaButton(
                            onClick = { showConfirmDialog = true },
                            text = "Show Confirmation Dialog",
                            variant = ButtonVariant.Solid,
                            size = SizeVariant.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )

                        PixaButton(
                            onClick = { showAlertDialog = true },
                            text = "Show Alert Dialog",
                            variant = ButtonVariant.Outlined,
                            size = SizeVariant.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ===== MENU =====
            item {
                DemoSection(
                    title = "PixaMenu",
                    description = "Context menu with actions, icons, and destructive items"
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PixaButton(
                            onClick = { showMenu = true },
                            text = "Show Menu",
                            variant = ButtonVariant.Tonal,
                            size = SizeVariant.Medium
                        )

                        PixaMenu(
                            visible = showMenu,
                            onDismiss = { showMenu = false },
                            items = listOf(
                                MenuItem("edit", "Edit"),
                                MenuItem("share", "Share"),
                                MenuItem("copy", "Copy Link"),
                                MenuItem("delete", "Delete", type = MenuItemType.Destructive)
                            ),
                            onItemClick = { item ->
                                println("Selected: ${item.id}")
                            }
                        )
                    }
                }
            }

            // ===== POPOVER =====
            item {
                DemoSection(
                    title = "PixaPopover",
                    description = "Contextual popup with custom content"
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PixaButton(
                                onClick = { showPopover = !showPopover },
                                text = if (showPopover) "Close Popover" else "Show Popover",
                                variant = ButtonVariant.Outlined,
                                size = SizeVariant.Medium
                            )

                            PixaPopover(
                                visible = showPopover,
                                onDismiss = { showPopover = false },
                                position = PopoverPosition.BottomCenter
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
                                ) {
                                    Text(
                                        "Popover Content",
                                        style = AppTheme.typography.subtitleBold,
                                        color = AppTheme.colors.baseContentTitle
                                    )
                                    Text(
                                        "This is a contextual popup that can contain any content.",
                                        style = AppTheme.typography.bodyRegular,
                                        color = AppTheme.colors.baseContentBody
                                    )
                                    PixaButton(
                                        onClick = { showPopover = false },
                                        text = "Got it!",
                                        variant = ButtonVariant.Tonal,
                                        size = SizeVariant.Small
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ===== TOOLTIP =====
            item {
                DemoSection(
                    title = "PixaTooltip",
                    description = "Informational tooltip on hover/tap"
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PixaTooltip(
                            tooltip = "This is a helpful tooltip message",
                            visible = showTooltip,
                            position = TooltipPosition.Bottom,
                            onDismiss = { showTooltip = false }
                        ) {
                            PixaButton(
                                onClick = { showTooltip = !showTooltip },
                                text = "Toggle Tooltip",
                                variant = ButtonVariant.Ghost,
                                size = SizeVariant.Medium
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))
            }
        }
    }

    // Dialogs rendered outside LazyColumn
    if (showConfirmDialog) {
        PixaDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = "Confirm Action",
            message = "Are you sure you want to proceed with this action?",
            variant = DialogVariant.Default,
            confirmText = "Confirm",
            dismissText = "Cancel",
            onConfirm = { showConfirmDialog = false },
            onDismiss = { showConfirmDialog = false }
        )
    }

    if (showAlertDialog) {
        PixaDialog(
            onDismissRequest = { showAlertDialog = false },
            title = "Warning",
            message = "This action cannot be undone. Please make sure you want to continue.",
            variant = DialogVariant.Warning,
            confirmText = "I Understand",
            onConfirm = { showAlertDialog = false }
        )
    }
}
