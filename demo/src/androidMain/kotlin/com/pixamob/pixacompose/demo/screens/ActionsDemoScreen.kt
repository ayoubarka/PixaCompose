package com.pixamob.pixacompose.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.ButtonShape
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.demo.R

/**
 * Complete Actions Category Demo Screen
 * Displays: Button, Chip, Tab with all sizes, variants, and states
 */
@Composable
fun ActionsDemoScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.baseSurfaceSubtle)
    ) {
        DemoScreenHeader(title = "Actions", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(HierarchicalSize.Padding.Medium),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)
        ) {
            // ===== BUTTON COMPONENT =====
            item {
                DemoSection(
                    title = "Button - All Sizes",
                    description = "Nano (24dp) → Massive (64dp) with proper typography"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)) {
                        PixaButton(
                            onClick = {},
                            text = "Nano (24dp)",
                            size = SizeVariant.Nano,
                            variant = ButtonVariant.Solid,
                         )
                        PixaButton(
                            onClick = {},
                            text = "Compact (32dp)",
                            size = SizeVariant.Compact,
                            variant = ButtonVariant.Solid,
                         )
                        PixaButton(
                            onClick = {},
                            text = "Small (36dp)",
                            size = SizeVariant.Small,
                            variant = ButtonVariant.Solid,
                         )
                        PixaButton(
                            onClick = {},
                            text = "Medium (44dp)",
                            size = SizeVariant.Medium,
                            variant = ButtonVariant.Solid,
                         )
                        PixaButton(
                            onClick = {},
                            text = "Large (48dp)",
                            size = SizeVariant.Large,
                            variant = ButtonVariant.Solid,
                         )
                        PixaButton(
                            onClick = {},
                            text = "Huge (56dp)",
                            size = SizeVariant.Huge,
                            variant = ButtonVariant.Solid,
                         )
                        PixaButton(
                            onClick = {},
                            text = "Massive (64dp)",
                            size = SizeVariant.Massive,
                            variant = ButtonVariant.Solid,
                         )
                    }
                }
            }

            item {
                DemoSection(
                    title = "Button - All Variants",
                    description = "Solid, Tonal, Outlined, Ghost styles"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        // Solid
                        Text(
                            text = "Solid (High Emphasis)",
                            style = AppTheme.typography.subtitleBold,
                            modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
                        )
                        PixaButton(
                            onClick = {},
                            text = "Solid Button",
                            size = SizeVariant.Medium,
                            variant = ButtonVariant.Solid,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        // Tonal
                        Text(
                            text = "Tonal (Medium Emphasis)",
                            style = AppTheme.typography.subtitleBold,
                            modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
                        )
                        PixaButton(
                            onClick = {},
                            text = "Tonal Button",
                            size = SizeVariant.Medium,
                            variant = ButtonVariant.Tonal,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        // Outlined
                        Text(
                            text = "Outlined (Medium Emphasis)",
                            style = AppTheme.typography.subtitleBold,
                            modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
                        )
                        PixaButton(
                            onClick = {},
                            text = "Outlined Button",
                            size = SizeVariant.Medium,
                            variant = ButtonVariant.Outlined,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        // Ghost
                        Text(
                            text = "Ghost (Low Emphasis)",
                            style = AppTheme.typography.subtitleBold,
                            modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
                        )
                        PixaButton(
                            onClick = {},
                            text = "Ghost Button",
                            size = SizeVariant.Medium,
                            variant = ButtonVariant.Ghost,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                DemoSection(
                    title = "Button - All Shapes",
                    description = "Default, Pill, Circle shapes"
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)
                    ) {
                        PixaButton(
                            onClick = {},
                            text = "Default",
                            size = SizeVariant.Medium,
                            variant = ButtonVariant.Solid,
                            shape = ButtonShape.Default,
                            modifier = Modifier.widthIn(min = 100.dp)
                        )
                        PixaButton(
                            onClick = {},
                            text = "Pill",
                            size = SizeVariant.Medium,
                            variant = ButtonVariant.Solid,
                            shape = ButtonShape.Pill,
                            modifier = Modifier.widthIn(min = 100.dp)
                        )
                        PixaButton(
                            onClick = {},
                            text = "◉",
                            size = SizeVariant.Medium,
                            variant = ButtonVariant.Solid,
                            shape = ButtonShape.Circle,
                            modifier = Modifier.width(56.dp)
                        )
                    }
                }
            }

            item {
                DemoSection(
                    title = "Button - States",
                    description = "Enabled and Disabled states"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        Text(
                            text = "Enabled",
                            style = AppTheme.typography.subtitleBold
                        )
                        PixaButton(
                            onClick = {},
                            text = "Click Me",
                            size = SizeVariant.Medium,
                            variant = ButtonVariant.Solid,
                            enabled = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        Text(
                            text = "Disabled",
                            style = AppTheme.typography.subtitleBold
                        )
                        PixaButton(
                            onClick = {},
                            text = "Disabled",
                            size = SizeVariant.Medium,
                            variant = ButtonVariant.Solid,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {

                    PixaButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {},
                        text = "Start Arrangement",
                        leadingIcon = painterResource(R.drawable.ic_home_bold_duotone),
                        size = SizeVariant.Large,
                        variant = ButtonVariant.Solid,
                        arrangement = Arrangement.Start
                    )

                    PixaButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {},
                        text = "Center Arrangement",
                        leadingIcon = painterResource(R.drawable.ic_home_bold_duotone),
                        size = SizeVariant.Large,
                        variant = ButtonVariant.Solid,
                        arrangement = Arrangement.Center
                    )

                    PixaButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {},
                        text = "End Arrangement",
                        leadingIcon = painterResource(R.drawable.ic_home_bold_duotone),
                        size = SizeVariant.Large,
                        variant = ButtonVariant.Solid,
                        arrangement = Arrangement.End
                    )

                    PixaButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {},
                        text = "Between Arrangement",
                        leadingIcon = painterResource(R.drawable.ic_home_bold_duotone),
                        size = SizeVariant.Large,
                        variant = ButtonVariant.Solid,
                        arrangement = Arrangement.SpaceBetween
                    )

                    PixaButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {},
                        text = "Around Arrangement",
                        leadingIcon = painterResource(R.drawable.ic_home_bold_duotone),
                        size = SizeVariant.Large,
                        variant = ButtonVariant.Solid,
                        arrangement = Arrangement.SpaceAround
                    )

                    PixaButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {},
                        text = "Evenly Arrangement",
                        leadingIcon = painterResource(R.drawable.ic_home_bold_duotone),
                        size = SizeVariant.Large,
                        variant = ButtonVariant.Solid,
                        arrangement = Arrangement.SpaceEvenly
                    )
                }
            }

            // Additional spacing
            item { Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large)) }
        }
    }
}
