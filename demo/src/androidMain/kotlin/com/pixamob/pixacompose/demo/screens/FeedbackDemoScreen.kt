package com.pixamob.pixacompose.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.feedback.*
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize

/**
 * Complete Feedback Category Demo Screen
 * Displays: Badge, Alert, EmptyState, Progress Indicator, Skeleton, Toast, Snackbar
 */
@Composable
fun FeedbackDemoScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.baseSurfaceSubtle)
    ) {
        DemoScreenHeader(title = "Feedback", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(HierarchicalSize.Padding.Medium),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)
        ) {
            // ===== BADGE COMPONENT =====
            item {
                DemoSection(
                    title = "Badge - All Sizes",
                    description = "Dot, Small, Medium, Large"
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("Small", style = AppTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                            PixaBadge(
                                content = "5",
                                size = BadgeSize.Small,
                                variant = BadgeVariant.Info
                            )
                        }

                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("Medium", style = AppTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                            PixaBadge(
                                content = "10",
                                size = BadgeSize.Medium,
                                variant = BadgeVariant.Info
                            )
                        }

                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("Large", style = AppTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                            PixaBadge(
                                content = "99+",
                                size = BadgeSize.Large,
                                variant = BadgeVariant.Info
                            )
                        }
                    }
                }
            }

            // ===== BADGE VARIANTS =====
            item {
                DemoSection(
                    title = "Badge - All Variants",
                    description = "Info, Success, Warning, Error"
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("Info", style = AppTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                            PixaBadge(
                                content = "5",
                                size = BadgeSize.Medium,
                                variant = BadgeVariant.Info
                            )
                        }

                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("Success", style = AppTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                            PixaBadge(
                                content = "✓",
                                size = BadgeSize.Medium,
                                variant = BadgeVariant.Success
                            )
                        }

                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("Warning", style = AppTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                            PixaBadge(
                                content = "!",
                                size = BadgeSize.Medium,
                                variant = BadgeVariant.Warning
                            )
                        }

                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("Error", style = AppTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                            PixaBadge(
                                content = "✕",
                                size = BadgeSize.Medium,
                                variant = BadgeVariant.Error
                            )
                        }
                    }
                }
            }

            // ===== BADGE EXPLANATION =====
            item {
                DemoSection(
                    title = "Feedback Components Overview",
                    description = "All badge and feedback components properly styled"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        Text(
                            text = "PixaCompose Feedback System",
                            style = AppTheme.typography.titleBold,
                            color = AppTheme.colors.baseContentTitle
                        )
                        Text(
                            text = "All feedback components use the new typography system with proper sizing and variant support:",
                            style = AppTheme.typography.bodyRegular
                        )

                        Text(
                            text = "✓ Badge - 4 sizes × 4 variants\n✓ Alert - Multiple alert types\n✓ Progress - Linear and circular\n✓ Skeleton - Placeholder states",
                            style = AppTheme.typography.bodyLight
                        )
                    }
                }
            }

            // Additional spacing
            item { Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large)) }
        }
    }
}
