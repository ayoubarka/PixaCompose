package com.pixamob.pixacompose.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.ButtonShape
 import com.pixamob.pixacompose.components.navigation.PixaBottomNavBar
import com.pixamob.pixacompose.components.navigation.NavItem
import com.pixamob.pixacompose.components.navigation.PixaTopNavBar
import com.pixamob.pixacompose.components.navigation.TabDisplayStyle
import com.pixamob.pixacompose.components.navigation.TopNavAction
import com.pixamob.pixacompose.components.navigation.TopNavSize
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.demo.R

/**
 * Complete Navigation Category Demo Screen
 * Displays: TopNavBar, BottomNavBar, Stepper demonstrations
 */
@Composable
fun NavigationDemoScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.baseSurfaceSubtle)
    ) {
        DemoScreenHeader(title = "Navigation", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(HierarchicalSize.Padding.Medium),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)
        ) {
            // ===== TOP NAV BAR =====
            item {
                DemoSection(
                    title = "TopNavBar - Header Navigation",
                    description = "Header with title and actions"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        PixaTopNavBar(
                            title = "App Title",
                            subtitle = "With subtitle",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        PixaTopNavBar  (
                            containerColor = AppTheme.colors.baseSurfaceSubtle,
                            size = TopNavSize.Medium,
                            title = "Main Screen",
                            startActions = listOf(
                                TopNavAction(
                                    icon = painterResource(R.drawable.ic_grid_bold_duotone),
                                    onClick = {}
                                )
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ===== BOTTOM NAV BAR =====
            item {
                DemoSection(
                    title = "BottomNavBar - Navigation Tabs",
                    description = "Bottom navigation with icons and text"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)) {
                        Text(
                            text = "Icon + Text Display Style",
                            style = AppTheme.typography.subtitleBold
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AppTheme.colors.baseSurfaceDefault)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            PixaBottomNavBar(
                                items = listOf(

                                    NavItem(
                                        title = "Home",
                                        iconSelected = painterResource(R.drawable.ic_home_bold_duotone),
                                        iconUnselected =  painterResource(R.drawable.ic_home_line_duotone)
                                    ),
                                    NavItem(
                                        title = "Mood",
                                        iconSelected = painterResource(R.drawable.ic_face_bold_duotone),
                                        iconUnselected =  painterResource(R.drawable.ic_face_line_duotone),
                                    ),
                                    NavItem(
                                        title = "Reports",
                                        iconSelected =  painterResource(R.drawable.ic_chart_bold_duotone),
                                        iconUnselected =  painterResource(R.drawable.ic_chart_bold_duotone),
                                    ),
                                    NavItem(
                                        title = "Habits",
                                        iconSelected =  painterResource(R.drawable.ic_grid_bold_duotone),
                                        iconUnselected =  painterResource(R.drawable.ic_grid_bold_duotone),
                                    ),
                                ),
                                selectedIndex = 0,
                                onItemSelected = {},
                                tabDisplayStyle = TabDisplayStyle.IconWithText
                            )
                        }

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        Text(
                            text = "Icon Only Display Style",
                            style = AppTheme.typography.subtitleBold
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AppTheme.colors.baseSurfaceDefault)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            PixaBottomNavBar(
                                items = listOf(
                                    NavItem(
                                        title = "Home",
                                        iconSelected = painterResource(R.drawable.ic_home_bold_duotone),
                                        iconUnselected =  painterResource(R.drawable.ic_home_line_duotone)
                                    ),
                                    NavItem(
                                        title = "Mood",
                                        iconSelected = painterResource(R.drawable.ic_face_bold_duotone),
                                        iconUnselected =  painterResource(R.drawable.ic_face_line_duotone),
                                    ),
                                    NavItem(
                                        title = "Reports",
                                        iconSelected =  painterResource(R.drawable.ic_chart_bold_duotone),
                                        iconUnselected =  painterResource(R.drawable.ic_chart_line_duotone),
                                    ),
                                ),
                                selectedIndex = 0,
                                onItemSelected = {},
                                tabDisplayStyle = TabDisplayStyle.IconOnly
                            )
                        }
                    }
                }
            }

            // ===== STEPPER INFO =====
            item {
                DemoSection(
                    title = "Stepper - Step Progression",
                    description = "Visual step indicator"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        Text(
                            text = "Stepper Component Features:",
                            style = AppTheme.typography.subtitleBold
                        )

                        Text(
                            text = "✓ Multiple sizes (Small, Medium, Large)\n" +
                                    "✓ Horizontal and Vertical orientations\n" +
                                    "✓ Different indicator types (Dot, Number, Icon, Checkmark)\n" +
                                    "✓ Show current step and progress\n" +
                                    "✓ Proper typography hierarchy",
                            style = AppTheme.typography.bodyRegular
                        )
                    }
                }
            }

            // ===== CIRCLE BUTTON EXAMPLE =====
            item {
                DemoSection(
                    title = "Circle Button Examples",
                    description = "Perfect circles for icon-only actions"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PixaButton(
                            onClick = {},
                            text = "+",
                            size = SizeVariant.Medium,
                            variant = ButtonVariant.Solid,
                            shape = ButtonShape.Circle,
                            description = "Add action"
                        )

                        PixaButton(
                            onClick = {},
                            text = "☆",
                            size = SizeVariant.Large,
                            variant = ButtonVariant.Solid,
                            shape = ButtonShape.Circle,
                            description = "Favorite"
                        )

                        PixaButton(
                            onClick = {},
                            text = "→",
                            size = SizeVariant.Huge,
                            variant = ButtonVariant.Tonal,
                            shape = ButtonShape.Circle,
                            description = "Next"
                        )
                    }
                }
            }

            // Additional spacing
            item { Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large)) }
        }
    }
}
