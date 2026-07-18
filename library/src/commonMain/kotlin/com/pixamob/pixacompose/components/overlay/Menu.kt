package com.pixamob.pixacompose.components.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.inputs.PixaCheckbox
import com.pixamob.pixacompose.components.inputs.PixaSwitch
import com.pixamob.pixacompose.components.inputs.PixaTextField
import com.pixamob.pixacompose.components.inputs.TextFieldVariant
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.elevationShadow
import com.pixamob.pixacompose.utils.toDp

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

/**
 * @param checked When non-null, renders a trailing checkbox — spec: Standard/Chevron "support...
 *   checkbox support." Null hides the accessory entirely.
 * @param selected Spec's "Active" state — "indicates selected option from option groups."
 */
@Stable
data class MenuItem(
    val id: String,
    val title: String,
    val icon: Painter? = null,
    val type: MenuItemType = MenuItemType.Default,
    val shortcut: String? = null,
    val checked: Boolean? = null,
    val onCheckedChange: ((Boolean) -> Unit)? = null,
    val selected: Boolean = false
)

/**
 * 7 menu content kinds with distinct anatomy, modeled as sealed subtypes.
 */
@Stable
sealed class MenuContent {
    /** Standard interactive option. Optional icon, shortcut, checkbox. */
    data class Item(val menuItem: MenuItem) : MenuContent()

    /** Option with trailing drill-down chevron for sub-menus. */
    data class Chevron(val menuItem: MenuItem, val chevronIcon: Painter? = null) : MenuContent()

    /** Reorderable option with drag handle + optional switch. */
    data class Grabber(
        val menuItem: MenuItem,
        val switchOn: Boolean? = null,
        val onSwitchChange: ((Boolean) -> Unit)? = null,
        val dragHandleIcon: Painter? = null
    ) : MenuContent()

    /** Search query field. */
    data class Search(
        val query: String,
        val onQueryChange: (String) -> Unit,
        val placeholder: String = "Search"
    ) : MenuContent()

    /** Non-interactive section header. */
    data class Header(val title: String) : MenuContent()

    /** Non-interactive description text. */
    data class Paragraph(val text: String) : MenuContent()

    /** Decorative separator. */
    object Divider : MenuContent()
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
    val hoverBackground: Color,
    val border: Color,
    val focusBorder: Color,
    val activeBackground: Color,
    val paragraphText: Color
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
internal fun getMenuTheme(): MenuColors {
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
        hoverBackground = colors.baseSurfaceSubtle,
        border = colors.baseBorderSubtle,
        focusBorder = colors.brandBorderFocus,
        activeBackground = colors.brandSurfaceSubtle,
        paragraphText = colors.baseContentCaption
    )
}

@Composable
internal fun getMenuSizeConfig(): MenuSizeConfig {
    val typography = AppTheme.typography
    return MenuSizeConfig(
        minWidth = 160.dp,
        maxWidth = 280.dp,
        maxHeight = 336.dp,
        itemHeight = HierarchicalSize.ListItem.Medium,
        itemPadding = HierarchicalSize.Spacing.Medium,
        iconSize = HierarchicalSize.Icon.Small,
        textStyle = typography.bodyRegular,
        headerStyle = typography.captionBold,
        cornerRadius = HierarchicalSize.Radius.Large,
        elevation = ComponentElevation.High.toDp(),
        spacing = HierarchicalSize.Spacing.Small
    )
}

/** Per-kind item heights for kinds that don't match [HierarchicalSize.ListItem] tiers. */
private val SearchItemHeight = 88.dp
private val HeaderItemHeight = 52.dp
private val DividerItemHeight = 32.dp
private val ParagraphItemHeight = HierarchicalSize.ListItem.Large

/** Focus highlight border width. */
private val MenuFocusBorderWidth = HierarchicalSize.Border.Large

/** Default border width for all states. */
private val MenuBorderWidth = HierarchicalSize.Border.Compact

/** Hover overlay color. */
private val MenuHoverOverlay = Color.Black.copy(alpha = 0.04f)

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaMenu - Convenience wrapper around [PixaMenuContent] for a flat list of
 * [MenuItem]s (the most common case). Use [PixaMenuContent] directly when you
 * need headers, dividers, or mixed item types.
 *
 * ### Purpose
 * Context menu with actions triggered by a user interaction.
 *
 * ### Anatomy
 * A rounded, elevated, 1px-bordered container (12px radius, shallow-below
 * shadow) around a scrollable list of items — see [PixaMenuContent] for
 * full anatomy details.
 *
 * ### States
 * Interactive items support Enabled, Hover, Focus, Active ([MenuItem.selected]),
 * and Disabled ([MenuItemType.Disabled]).
 *
 * @param visible Whether menu is visible
 * @param onDismiss Callback when menu should close
 * @param items Flat list of menu items
 * @param onItemClick Callback when an item is clicked
 * @param modifier Modifier
 * @param colors Custom colors
 * @param alignment Menu alignment
 *
 * @sample
 * ```kotlin
 * var showMenu by remember { mutableStateOf(false) }
 * Box {
 *     IconButton(onClick = { showMenu = true }) {
 *         Icon(Icons.Default.MoreVert, "Options")
 *     }
 *     PixaMenu(
 *         visible = showMenu,
 *         onDismiss = { showMenu = false },
 *         items = listOf(
 *             MenuItem("edit", "Edit", icon),
 *             MenuItem("delete", "Delete", type = MenuItemType.Destructive)
 *         ),
 *         onItemClick = { item -> handleAction(item.id) }
 *     )
 * }
 * ```
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

/**
 * PixaMenuContent — a container to nest various hierarchical information, features, or settings,
 * displaying navigational lists and lists of actions.
 *
 * ### Anatomy
 * A rounded, elevated, 1px-bordered container (spec: 12px radius, "shallow-below" shadow) around a
 * scrollable list of 7 item kinds — see [MenuContent]'s per-subtype docs. A scroll indicator
 * (default Compose scrollbar-affordance) shows when content exceeds [MenuSizeConfig.maxHeight].
 *
 * ### States
 * Interactive kinds (Standard/Chevron/Grabber) resolve all 5 states the spec names: Enabled (default),
 * Hover (4% black overlay, desktop/pointer input), Focus (3px accent border, keyboard/voice nav), Active
 * ([MenuItem.selected] — tinted background for "selected option from option groups"), and Disabled
 * ([MenuItemType.Disabled]).
 *
 * @param visible Whether menu is visible
 * @param onDismiss Callback when menu should close
 * @param content Full per-kind content list (use [PixaMenu] instead for the simple flat-items API)
 * @param onItemClick Callback when an interactive item (Standard/Chevron/Grabber) is clicked
 * @param modifier Modifier
 * @param colors Custom colors
 * @param alignment Menu alignment
 */
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
    val shape = RoundedCornerShape(sizeConfig.cornerRadius)

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
                    .elevationShadow(sizeConfig.elevation, shape)
                    .clip(shape)
                    .background(themeColors.background)
                    .border(MenuBorderWidth, themeColors.border, shape)
                    .padding(vertical = sizeConfig.spacing)
            ) {
                menuContentItems(
                    content = content,
                    onItemClick = onItemClick,
                    onDismiss = onDismiss,
                    themeColors = themeColors,
                    sizeConfig = sizeConfig
                )
            }
        }
    }
}

/**
 * Menu option rows without the [Popup] or elevated container chrome.
 *
 * Extracted so a host with its own surface can reuse the same item anatomy and state behavior
 * (e.g. `PixaDropdown` on compact screens). Internal — not a second public menu API.
 */
internal fun LazyListScope.menuContentItems(
    content: List<MenuContent>,
    onItemClick: (MenuItem) -> Unit,
    onDismiss: () -> Unit,
    themeColors: MenuColors,
    sizeConfig: MenuSizeConfig
) {
    items(content) { menuContent ->
        when (menuContent) {
                        is MenuContent.Item -> {
                            MenuItemRow(
                                item = menuContent.menuItem,
                                trailingGlyph = null,
                                onClick = {
                                    onItemClick(menuContent.menuItem)
                                    onDismiss()
                                },
                                colors = themeColors,
                                sizeConfig = sizeConfig
                            )
                        }
                        is MenuContent.Chevron -> {
                            MenuItemRow(
                                item = menuContent.menuItem,
                                trailingIcon = menuContent.chevronIcon,
                                trailingGlyph = if (menuContent.chevronIcon == null) "›" else null,
                                onClick = {
                                    onItemClick(menuContent.menuItem)
                                    onDismiss()
                                },
                                colors = themeColors,
                                sizeConfig = sizeConfig
                            )
                        }
                        is MenuContent.Grabber -> {
                            MenuGrabberRow(
                                content = menuContent,
                                colors = themeColors,
                                sizeConfig = sizeConfig
                            )
                        }
                        is MenuContent.Search -> {
                            Box(modifier = Modifier.height(SearchItemHeight).padding(horizontal = sizeConfig.itemPadding)) {
                                PixaTextField(
                                    value = menuContent.query,
                                    onValueChange = menuContent.onQueryChange,
                                    variant = TextFieldVariant.Outlined,
                                    size = SizeVariant.Small,
                                    placeholder = menuContent.placeholder,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        is MenuContent.Divider -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(DividerItemHeight),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(MenuBorderWidth)
                                        .background(themeColors.divider)
                                )
                            }
                        }
                        is MenuContent.Header -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = HeaderItemHeight)
                                    .padding(horizontal = sizeConfig.itemPadding, vertical = sizeConfig.spacing),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                BasicText(
                                    text = menuContent.title,
                                    style = sizeConfig.headerStyle.copy(color = themeColors.headerText),
                                    modifier = Modifier.semantics { heading() }
                                )
                            }
                        }
            is MenuContent.Paragraph -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = ParagraphItemHeight)
                        .padding(horizontal = sizeConfig.itemPadding, vertical = sizeConfig.spacing),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicText(
                        text = menuContent.text,
                        style = AppTheme.typography.captionRegular.copy(color = themeColors.paragraphText)
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

/** Resolves the 5-state visual treatment (Enabled/Hover/Focus/Active/Disabled) shared by Standard,
 * Chevron, and Grabber rows. */
@Composable
private fun rememberMenuRowInteraction(): MutableInteractionSource = remember { MutableInteractionSource() }

@Composable
private fun MenuItemRow(
    item: MenuItem,
    trailingGlyph: String?,
    onClick: () -> Unit,
    colors: MenuColors,
    sizeConfig: MenuSizeConfig,
    trailingIcon: Painter? = null
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
    val interactionSource = rememberMenuRowInteraction()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val rowBackground = when {
        item.selected -> colors.activeBackground
        isHovered && isEnabled -> MenuHoverOverlay
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = sizeConfig.itemHeight)
            .focusable(interactionSource = interactionSource, enabled = isEnabled)
            .hoverable(interactionSource = interactionSource, enabled = isEnabled)
            .background(rowBackground)
            .then(
                if (isFocused && isEnabled) {
                    Modifier.border(MenuFocusBorderWidth, colors.focusBorder)
                } else {
                    Modifier
                }
            )
            .clickable(
                enabled = isEnabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = sizeConfig.itemPadding, vertical = sizeConfig.spacing)
            .semantics { contentDescription = item.title },
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

        BasicText(
            text = item.title,
            style = sizeConfig.textStyle.copy(color = textColor),
            modifier = Modifier.weight(1f)
        )

        if (item.shortcut != null) {
            BasicText(
                text = item.shortcut,
                style = AppTheme.typography.captionRegular.copy(color = colors.disabledText)
            )
        }

        if (item.checked != null) {
            PixaCheckbox(
                checked = item.checked,
                onCheckedChange = item.onCheckedChange,
                enabled = isEnabled,
                size = SizeVariant.Small
            )
        }

        if (trailingIcon != null) {
            PixaIcon(
                painter = trailingIcon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(sizeConfig.iconSize)
            )
        } else if (trailingGlyph != null) {
            BasicText(text = trailingGlyph, style = sizeConfig.textStyle.copy(color = iconColor))
        }
    }
}

/** Grabber row — spec: "highly interactive; enables reordering; supports optional switch." The drag
 * handle is a leading accessory (glyph fallback, matching this codebase's established close-icon/
 * dismiss-icon text-glyph fallback convention) rather than an icon-only requirement, since the spec
 * doesn't mandate a specific glyph. Actual drag-and-drop reordering is left to the caller's own list
 * state (e.g. `LazyColumn` `Modifier.dragAndDropSource`/reorder library) — this row surfaces the
 * affordance and the switch accessory, not a full reorder gesture implementation, which would require
 * this component to own list mutation it has no visibility into.
 */
@Composable
private fun MenuGrabberRow(
    content: MenuContent.Grabber,
    colors: MenuColors,
    sizeConfig: MenuSizeConfig
) {
    val item = content.menuItem
    val textColor = when (item.type) {
        MenuItemType.Default -> colors.itemText
        MenuItemType.Destructive -> colors.destructiveText
        MenuItemType.Disabled -> colors.disabledText
    }
    val isEnabled = item.type != MenuItemType.Disabled
    val interactionSource = rememberMenuRowInteraction()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val rowBackground = when {
        item.selected -> colors.activeBackground
        isHovered && isEnabled -> MenuHoverOverlay
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = sizeConfig.itemHeight)
            .focusable(interactionSource = interactionSource, enabled = isEnabled)
            .hoverable(interactionSource = interactionSource, enabled = isEnabled)
            .background(rowBackground)
            .then(
                if (isFocused && isEnabled) {
                    Modifier.border(MenuFocusBorderWidth, colors.focusBorder)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = sizeConfig.itemPadding, vertical = sizeConfig.spacing)
            .semantics { contentDescription = item.title },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(sizeConfig.spacing)
    ) {
        if (content.dragHandleIcon != null) {
            PixaIcon(
                painter = content.dragHandleIcon,
                contentDescription = "Reorder ${item.title}",
                tint = colors.icon,
                modifier = Modifier.size(sizeConfig.iconSize)
            )
        } else {
            BasicText(
                text = "≡",
                style = sizeConfig.textStyle.copy(color = colors.icon),
                modifier = Modifier.semantics { contentDescription = "Reorder ${item.title}" }
            )
        }

        if (item.icon != null) {
            PixaIcon(
                painter = item.icon,
                contentDescription = null,
                tint = colors.icon,
                modifier = Modifier.size(sizeConfig.iconSize)
            )
        }

        BasicText(
            text = item.title,
            style = sizeConfig.textStyle.copy(color = textColor),
            modifier = Modifier.weight(1f)
        )

        if (content.switchOn != null && content.onSwitchChange != null) {
            PixaSwitch(
                checked = content.switchOn,
                onCheckedChange = content.onSwitchChange,
                enabled = isEnabled,
                size = SizeVariant.Small
            )
        }
    }
}
