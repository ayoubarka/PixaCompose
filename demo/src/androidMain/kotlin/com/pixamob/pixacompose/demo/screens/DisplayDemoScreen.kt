package com.pixamob.pixacompose.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider as Material3HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.*
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant

/**
 * Complete Display Category Demo Screen
 * Displays: Card, Avatar, Icon, Image, Divider
 */
@Composable
fun DisplayDemoScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.baseSurfaceSubtle)
    ) {
        DemoScreenHeader(title = "Display", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(HierarchicalSize.Padding.Medium),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Large)
        ) {
            // ===== AVATAR COMPONENT =====
            item {
                DemoSection(
                    title = "Avatar - All Sizes",
                    description = "Nano (16dp) → Massive (96dp)"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Nano", style = AppTheme.typography.labelSmall)
                            PixaAvatar(
                                text = "AB",
                                size = SizeVariant.Nano,
                                shape = AvatarShape.Circle
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Compact", style = AppTheme.typography.labelSmall)
                            PixaAvatar(
                                text = "CD",
                                size = SizeVariant.Compact,
                                shape = AvatarShape.Circle
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Small", style = AppTheme.typography.labelSmall)
                            PixaAvatar(
                                text = "EF",
                                size = SizeVariant.Small,
                                shape = AvatarShape.Circle
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Medium", style = AppTheme.typography.labelSmall)
                            PixaAvatar(
                                text = "GH",
                                size = SizeVariant.Medium,
                                shape = AvatarShape.Circle
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Large", style = AppTheme.typography.labelSmall)
                            PixaAvatar(
                                text = "IJ",
                                size = SizeVariant.Large,
                                shape = AvatarShape.Circle
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Huge", style = AppTheme.typography.labelSmall)
                            PixaAvatar(
                                text = "KL",
                                size = SizeVariant.Huge,
                                shape = AvatarShape.Circle
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Massive", style = AppTheme.typography.labelSmall)
                            PixaAvatar(
                                text = "MN",
                                size = SizeVariant.Massive,
                                shape = AvatarShape.Circle
                            )
                        }
                    }
                }
            }

            // ===== AVATAR SHAPES =====
            item {
                DemoSection(
                    title = "Avatar - All Shapes",
                    description = "Circle, Rounded, Square shapes"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Circle", style = AppTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                            PixaAvatar(
                                text = "AB",
                                size = SizeVariant.Large,
                                shape = AvatarShape.Circle
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Rounded", style = AppTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                            PixaAvatar(
                                text = "CD",
                                size = SizeVariant.Large,
                                shape = AvatarShape.Rounded
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Square", style = AppTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                            PixaAvatar(
                                text = "EF",
                                size = SizeVariant.Large,
                                shape = AvatarShape.Square
                            )
                        }
                    }
                }
            }

            // ===== CARD COMPONENT =====
            item {
                DemoSection(
                    title = "Card - All Variants",
                    description = "Elevated, Outlined, Filled, Ghost"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        Text("Elevated", style = AppTheme.typography.subtitleBold)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(HierarchicalSize.Radius.Medium))
                                .background(AppTheme.colors.baseSurfaceDefault)
                                .padding(HierarchicalSize.Padding.Medium)
                        ) {
                            Column {
                                Text(
                                    text = "Elevated Card",
                                    style = AppTheme.typography.titleBold
                                )
                                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                                Text(
                                    text = "This card has a shadow for emphasis",
                                    style = AppTheme.typography.bodyRegular
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        Text("Outlined", style = AppTheme.typography.subtitleBold)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(HierarchicalSize.Radius.Medium))
                                .background(AppTheme.colors.baseSurfaceDefault)
                                .padding(HierarchicalSize.Padding.Medium)
                        ) {
                            Column {
                                Text(
                                    text = "Outlined Card",
                                    style = AppTheme.typography.titleBold
                                )
                                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                                Text(
                                    text = "This card has a border",
                                    style = AppTheme.typography.bodyRegular
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        Text("Filled", style = AppTheme.typography.subtitleBold)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(HierarchicalSize.Radius.Medium))
                                .background(AppTheme.colors.baseSurfaceDefault)
                                .padding(HierarchicalSize.Padding.Medium)
                        ) {
                            Column {
                                Text(
                                    text = "Filled Card",
                                    style = AppTheme.typography.titleBold
                                )
                                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                                Text(
                                    text = "This card has a filled background",
                                    style = AppTheme.typography.bodyRegular
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        Text("Ghost", style = AppTheme.typography.subtitleBold)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(HierarchicalSize.Radius.Medium))
                                .background(AppTheme.colors.baseSurfaceDefault)
                                .padding(HierarchicalSize.Padding.Medium)
                        ) {
                            Column {
                                Text(
                                    text = "Ghost Card",
                                    style = AppTheme.typography.titleBold
                                )
                                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                                Text(
                                    text = "This card is minimal and subtle",
                                    style = AppTheme.typography.bodyRegular
                                )
                            }
                        }
                    }
                }
            }

            // ===== ICON COMPONENT =====
            item {
                DemoSection(
                    title = "Icon - Common Icons",
                    description = "Various sizes and colors"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = AppTheme.colors.brandContentDefault,
                                modifier = Modifier.size(24.dp)
                            )
                            Text("Home (24dp)", style = AppTheme.typography.bodyRegular)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = AppTheme.colors.brandContentDefault,
                                modifier = Modifier.size(32.dp)
                            )
                            Text("Settings (32dp)", style = AppTheme.typography.bodyRegular)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorite",
                                tint = Color.Red,
                                modifier = Modifier.size(40.dp)
                            )
                            Text("Favorite (40dp)", style = AppTheme.typography.bodyRegular)
                        }
                    }
                }
            }

            // ===== DIVIDER COMPONENT =====
            item {
                DemoSection(
                    title = "Divider - Styles",
                    description = "Horizontal and vertical dividers"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)) {
                        Text("Horizontal", style = AppTheme.typography.subtitleBold)
                        Text("Text above divider", style = AppTheme.typography.bodyRegular)
                        Material3HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        Text("Text below divider", style = AppTheme.typography.bodyRegular)

                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

                        Text("Vertical", style = AppTheme.typography.subtitleBold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Left", style = AppTheme.typography.bodyRegular)
                            Material3HorizontalDivider(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(1.dp)
                            )
                            Text("Right", style = AppTheme.typography.bodyRegular)
                        }
                    }
                }
            }

            // Additional spacing
            item { Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large)) }
        }
    }
}
