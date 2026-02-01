package com.pixamob.pixacompose.components.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.IconSize
import com.pixamob.pixacompose.theme.RadiusSize

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class MenuItemType {
    Default,
    Destructive,
    Disabled
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Stable
data class MenuItem(
    val id: String,
    val title: String,
    val icon: Painter? = null,
    val type: MenuItemType = MenuItemType.Default,
    val shortcut: String? = null
)

@Stable
sealed class MenuContent {
    data class Item(val menuItem: MenuItem) : MenuContent()
    object Divider : MenuContent()
    data class Header(val title: String) : MenuContent()
}

@Immutable
@Stable
data class MenuColors(
    val background: Color,
    val itemText: Color,
    val destructiveText: Color,
    val disabledText: Color,
    val icon: Color,
    val destructiveIcon: Color,
    val divider: Color,
    val headerText: Color,
    val hoverBackground: Color
)

@Immutable
@Stable
data class MenuSizeConfig(
    val minWidth: Dp,
    val maxWidth: Dp,
    val maxHeight: Dp,
    val itemHeight: Dp,
    val itemPadding: Dp,
    val iconSize: Dp,
    val textStyle: TextStyle,
    val headerStyle: TextStyle,
    val cornerRadius: Dp,
    val elevation: Dp,
    val spacing: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getMenuTheme(): MenuColors {
    val colors = AppTheme.colors
    return MenuColors(
        background = colors.baseSurfaceDefault,
        itemText = colors.baseContentBody,
        destructiveText = colors.errorContentDefault,
        disabledText = colors.baseContentDisabled,
        icon = colors.baseContentBody,
        destructiveIcon = colors.errorContentDefault,
        divider = colors.baseBorderSubtle,
        headerText = colors.baseContentCaption,
        hoverBackground = colors.baseSurfaceSubtle
    )
}

@Composable
private fun getMenuSizeConfig(): MenuSizeConfig {
    val typography = AppTheme.typography
    return MenuSizeConfig(
        minWidth = 160.dp,
        maxWidth = 280.dp,
        maxHeight = 320.dp,
        itemHeight = 44.dp,
        itemPadding = HierarchicalSize.Spacing.Medium,
        iconSize = IconSize.Small,
        textStyle = typography.bodyRegular,
        headerStyle = typography.captionBold,
        cornerRadius = RadiusSize.Medium,
        elevation = HierarchicalSize.Shadow.Medium,
        spacing = HierarchicalSize.Spacing.Small
    )
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaMenu - Context menu with actions
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic menu
 * var showMenu by remember { mutableStateOf(false) }
 * Box {
 *     IconButton(onClick = { showMenu = true }) {
 *         Icon(Icons.Default.MoreVert, "Options")
 *     }
 *     PixaMenu(
 *         visible = showMenu,
 *         onDismiss = { showMenu = false },
 *         items = listOf(
 *             MenuItem("edit", "Edit", painterResource(Res.drawable.ic_edit)),
 *             MenuItem("share", "Share", painterResource(Res.drawable.ic_share)),
 *             MenuItem("delete", "Delete", type = MenuItemType.Destructive)
 *         ),
 *         onItemClick = { item -> handleAction(item.id) }
 *     )
 * }
 *
 * // Menu with sections
 * PixaMenu(
 *     visible = isVisible,
 *     onDismiss = { isVisible = false },
 *     content = listOf(
 *         MenuContent.Header("Actions"),
 *         MenuContent.Item(MenuItem("copy", "Copy")),
 *         MenuContent.Item(MenuItem("paste", "Paste")),
 *         MenuContent.Divider,
 *         MenuContent.Item(MenuItem("delete", "Delete", type = MenuItemType.Destructive))
 *     ),
 *     onItemClick = { handleItem(it) }
 * )
 * ```
 *
 * @param visible Whether menu is visible
 * @param onDismiss Callback when menu should close
 * @param items List of menu items (simple API)
 * @param onItemClick Callback when item is clicked
 * @param modifier Modifier
 * @param colors Custom colors
 * @param alignment Menu alignment
 */
@Composable
fun PixaMenu(
    visible: Boolean,
    onDismiss: () -> Unit,
    items: List<MenuItem>,
    onItemClick: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
    colors: MenuColors? = null,
    alignment: Alignment = Alignment.TopStart
) {
    val content = items.map { MenuContent.Item(it) }
    PixaMenuContent(
        visible = visible,
        onDismiss = onDismiss,
        content = content,
        onItemClick = onItemClick,
        modifier = modifier,
        colors = colors,
        alignment = alignment
    )
}

@Composable
fun PixaMenuContent(
    visible: Boolean,
    onDismiss: () -> Unit,
    content: List<MenuContent>,
    onItemClick: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
    colors: MenuColors? = null,
    alignment: Alignment = Alignment.TopStart
) {
    val themeColors = colors ?: getMenuTheme()
    val sizeConfig = getMenuSizeConfig()

    if (visible) {
        Popup(
            alignment = alignment,
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true)
        ) {
            LazyColumn(
                modifier = modifier
                    .widthIn(min = sizeConfig.minWidth, max = sizeConfig.maxWidth)
                    .heightIn(max = sizeConfig.maxHeight)
                    .shadow(sizeConfig.elevation, RoundedCornerShape(sizeConfig.cornerRadius))
                    .clip(RoundedCornerShape(sizeConfig.cornerRadius))
                    .background(themeColors.background)
                    .padding(vertical = sizeConfig.spacing)
            ) {
                items(content) { menuContent ->
                    when (menuContent) {
                        is MenuContent.Item -> {
                            MenuItemRow(
                                item = menuContent.menuItem,
                                onClick = {
                                    onItemClick(menuContent.menuItem)
                                    onDismiss()
                                },
                                colors = themeColors,
                                sizeConfig = sizeConfig
                            )
                        }
                        is MenuContent.Divider -> {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = sizeConfig.spacing),
                                color = themeColors.divider
                            )
                        }
                        is MenuContent.Header -> {
                            Text(
                                text = menuContent.title,
                                style = sizeConfig.headerStyle,
                                color = themeColors.headerText,
                                modifier = Modifier.padding(
                                    horizontal = sizeConfig.itemPadding,
                                    vertical = sizeConfig.spacing
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun MenuItemRow(
    item: MenuItem,
    onClick: () -> Unit,
    colors: MenuColors,
    sizeConfig: MenuSizeConfig
) {
    val textColor = when (item.type) {
        MenuItemType.Default -> colors.itemText
        MenuItemType.Destructive -> colors.destructiveText
        MenuItemType.Disabled -> colors.disabledText
    }
    val iconColor = when (item.type) {
        MenuItemType.Default -> colors.icon
        MenuItemType.Destructive -> colors.destructiveIcon
        MenuItemType.Disabled -> colors.disabledText
    }
    val isEnabled = item.type != MenuItemType.Disabled

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = isEnabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = sizeConfig.itemPadding, vertical = sizeConfig.spacing),
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
            style = sizeConfig.textStyle,
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        if (item.shortcut != null) {
            Text(
                text = item.shortcut,
                style = AppTheme.typography.captionRegular,
                color = colors.disabledText
            )
        }
    }
}
