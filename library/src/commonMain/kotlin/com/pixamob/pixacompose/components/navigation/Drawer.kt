package com.pixamob.pixacompose.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.IconSize
import com.pixamob.pixacompose.theme.RadiusSize

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class DrawerPosition {
    Start,
    End
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Stable
data class DrawerItem(
    val id: String,
    val title: String,
    val icon: Painter? = null,
    val badge: String? = null,
    val enabled: Boolean = true
)

@Stable
data class DrawerSection(
    val title: String? = null,
    val items: List<DrawerItem>
)

@Immutable
@Stable
data class DrawerColors(
    val background: Color,
    val scrim: Color,
    val itemBackground: Color,
    val selectedItemBackground: Color,
    val itemText: Color,
    val selectedItemText: Color,
    val icon: Color,
    val selectedIcon: Color,
    val sectionTitle: Color,
    val divider: Color
)

@Immutable
@Stable
data class DrawerSizeConfig(
    val width: Dp,
    val itemHeight: Dp,
    val itemPadding: Dp,
    val iconSize: Dp,
    val titleStyle: TextStyle,
    val sectionTitleStyle: TextStyle,
    val cornerRadius: Dp,
    val spacing: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getDrawerTheme(): DrawerColors {
    val colors = AppTheme.colors
    return DrawerColors(
        background = colors.baseSurfaceDefault,
        scrim = Color.Black.copy(alpha = 0.5f),
        itemBackground = Color.Transparent,
        selectedItemBackground = colors.brandSurfaceSubtle,
        itemText = colors.baseContentBody,
        selectedItemText = colors.brandContentDefault,
        icon = colors.baseContentBody,
        selectedIcon = colors.brandContentDefault,
        sectionTitle = colors.baseContentCaption,
        divider = colors.baseBorderSubtle
    )
}

@Composable
private fun getDrawerSizeConfig(): DrawerSizeConfig {
    val typography = AppTheme.typography
    return DrawerSizeConfig(
        width = 280.dp,
        itemHeight = 48.dp,
        itemPadding = HierarchicalSize.Spacing.Medium,
        iconSize = IconSize.Medium,
        titleStyle = typography.bodyRegular,
        sectionTitleStyle = typography.captionBold,
        cornerRadius = RadiusSize.Medium,
        spacing = HierarchicalSize.Spacing.Small
    )
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaDrawer - Side navigation drawer
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Modal drawer
 * var drawerOpen by remember { mutableStateOf(false) }
 * PixaDrawer(
 *     visible = drawerOpen,
 *     onDismiss = { drawerOpen = false },
 *     selectedItemId = currentRoute,
 *     sections = listOf(
 *         DrawerSection(items = listOf(
 *             DrawerItem("home", "Home", painterResource(Res.drawable.ic_home)),
 *             DrawerItem("profile", "Profile", painterResource(Res.drawable.ic_user))
 *         )),
 *         DrawerSection("Settings", listOf(
 *             DrawerItem("settings", "Settings", painterResource(Res.drawable.ic_settings))
 *         ))
 *     ),
 *     onItemClick = { navigate(it.id) }
 * )
 *
 * // Drawer with header
 * PixaDrawer(
 *     visible = isOpen,
 *     onDismiss = { isOpen = false },
 *     header = {
 *         UserHeader(user = currentUser)
 *     },
 *     sections = navigationSections,
 *     onItemClick = { handleNavigation(it) }
 * )
 * ```
 *
 * @param visible Whether drawer is visible
 * @param onDismiss Callback when drawer should close
 * @param sections List of drawer sections
 * @param onItemClick Callback when item is clicked
 * @param modifier Modifier
 * @param selectedItemId Currently selected item ID
 * @param position Drawer position (Start or End)
 * @param colors Custom colors
 * @param header Optional header content
 * @param footer Optional footer content
 */
@Composable
fun PixaDrawer(
    visible: Boolean,
    onDismiss: () -> Unit,
    sections: List<DrawerSection>,
    onItemClick: (DrawerItem) -> Unit,
    modifier: Modifier = Modifier,
    selectedItemId: String? = null,
    position: DrawerPosition = DrawerPosition.Start,
    colors: DrawerColors? = null,
    header: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null
) {
    val themeColors = colors ?: getDrawerTheme()
    val sizeConfig = getDrawerSizeConfig()

    val slideDirection = if (position == DrawerPosition.Start) -1 else 1

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(200))
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(themeColors.scrim)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )

            // Drawer content
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(tween(300)) { slideDirection * it },
                exit = slideOutHorizontally(tween(300)) { slideDirection * it },
                modifier = Modifier.align(
                    if (position == DrawerPosition.Start) Alignment.CenterStart else Alignment.CenterEnd
                )
            ) {
                Column(
                    modifier = Modifier
                        .width(sizeConfig.width)
                        .fillMaxHeight()
                        .background(themeColors.background)
                ) {
                    header?.invoke()

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = sizeConfig.spacing)
                    ) {
                        sections.forEach { section ->
                            if (section.title != null) {
                                item {
                                    Text(
                                        text = section.title,
                                        style = sizeConfig.sectionTitleStyle,
                                        color = themeColors.sectionTitle,
                                        modifier = Modifier.padding(
                                            horizontal = sizeConfig.itemPadding,
                                            vertical = sizeConfig.spacing
                                        )
                                    )
                                }
                            }

                            items(section.items) { item ->
                                val isSelected = item.id == selectedItemId
                                DrawerItemRow(
                                    item = item,
                                    isSelected = isSelected,
                                    onClick = { onItemClick(item) },
                                    colors = themeColors,
                                    sizeConfig = sizeConfig
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(sizeConfig.spacing))
                            }
                        }
                    }

                    footer?.invoke()
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun DrawerItemRow(
    item: DrawerItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: DrawerColors,
    sizeConfig: DrawerSizeConfig
) {
    val backgroundColor = if (isSelected) colors.selectedItemBackground else colors.itemBackground
    val textColor = if (isSelected) colors.selectedItemText else colors.itemText
    val iconColor = if (isSelected) colors.selectedIcon else colors.icon

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = sizeConfig.spacing)
            .height(sizeConfig.itemHeight)
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(backgroundColor)
            .clickable(
                enabled = item.enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = sizeConfig.itemPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(sizeConfig.spacing)
    ) {
        if (item.icon != null) {
            PixaIcon(
                painter = item.icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(sizeConfig.iconSize)
            )
        }

        Text(
            text = item.title,
            style = sizeConfig.titleStyle,
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        if (item.badge != null) {
            Text(
                text = item.badge,
                style = AppTheme.typography.captionBold,
                color = colors.selectedItemText,
                modifier = Modifier
                    .clip(RoundedCornerShape(RadiusSize.Full))
                    .background(colors.selectedItemBackground)
                    .padding(horizontal = HierarchicalSize.Spacing.Small, vertical = 2.dp)
            )
        }
    }
}
