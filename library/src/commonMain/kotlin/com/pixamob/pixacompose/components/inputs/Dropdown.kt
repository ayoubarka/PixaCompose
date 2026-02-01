package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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

enum class DropdownVariant {
    Outlined,
    Filled,
    Ghost
}

enum class DropdownSize {
    Small,
    Medium,
    Large
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Stable
data class DropdownItem<T>(
    val value: T,
    val label: String,
    val icon: Painter? = null,
    val enabled: Boolean = true
)

@Immutable
@Stable
data class DropdownColors(
    val background: Color,
    val border: Color,
    val text: Color,
    val placeholder: Color,
    val icon: Color,
    val menuBackground: Color,
    val menuItemHover: Color,
    val selectedBackground: Color,
    val disabledBackground: Color,
    val disabledText: Color
)

@Immutable
@Stable
data class DropdownSizeConfig(
    val height: Dp,
    val padding: Dp,
    val iconSize: Dp,
    val textStyle: TextStyle,
    val cornerRadius: Dp,
    val borderWidth: Dp,
    val menuMaxHeight: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getDropdownTheme(variant: DropdownVariant): DropdownColors {
    val colors = AppTheme.colors
    return when (variant) {
        DropdownVariant.Outlined -> DropdownColors(
            background = colors.baseSurfaceDefault,
            border = colors.baseBorderDefault,
            text = colors.baseContentTitle,
            placeholder = colors.baseContentHint,
            icon = colors.baseContentBody,
            menuBackground = colors.baseSurfaceDefault,
            menuItemHover = colors.baseSurfaceSubtle,
            selectedBackground = colors.brandSurfaceSubtle,
            disabledBackground = colors.baseSurfaceDisabled,
            disabledText = colors.baseContentDisabled
        )
        DropdownVariant.Filled -> DropdownColors(
            background = colors.baseSurfaceSubtle,
            border = Color.Transparent,
            text = colors.baseContentTitle,
            placeholder = colors.baseContentHint,
            icon = colors.baseContentBody,
            menuBackground = colors.baseSurfaceDefault,
            menuItemHover = colors.baseSurfaceSubtle,
            selectedBackground = colors.brandSurfaceSubtle,
            disabledBackground = colors.baseSurfaceDisabled,
            disabledText = colors.baseContentDisabled
        )
        DropdownVariant.Ghost -> DropdownColors(
            background = Color.Transparent,
            border = Color.Transparent,
            text = colors.baseContentTitle,
            placeholder = colors.baseContentHint,
            icon = colors.baseContentBody,
            menuBackground = colors.baseSurfaceDefault,
            menuItemHover = colors.baseSurfaceSubtle,
            selectedBackground = colors.brandSurfaceSubtle,
            disabledBackground = Color.Transparent,
            disabledText = colors.baseContentDisabled
        )
    }
}

@Composable
private fun getDropdownSizeConfig(size: DropdownSize): DropdownSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        DropdownSize.Small -> DropdownSizeConfig(
            height = 36.dp,
            padding = HierarchicalSize.Spacing.Small,
            iconSize = IconSize.Small,
            textStyle = typography.bodyRegular,
            cornerRadius = RadiusSize.Small,
            borderWidth = 1.dp,
            menuMaxHeight = 200.dp
        )
        DropdownSize.Medium -> DropdownSizeConfig(
            height = 44.dp,
            padding = HierarchicalSize.Spacing.Medium,
            iconSize = IconSize.Small,
            textStyle = typography.bodyRegular,
            cornerRadius = RadiusSize.Medium,
            borderWidth = 1.dp,
            menuMaxHeight = 250.dp
        )
        DropdownSize.Large -> DropdownSizeConfig(
            height = 52.dp,
            padding = HierarchicalSize.Spacing.Medium,
            iconSize = IconSize.Medium,
            textStyle = typography.bodyBold,
            cornerRadius = RadiusSize.Medium,
            borderWidth = 1.dp,
            menuMaxHeight = 300.dp
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaDropdown - Selection from a list of options
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic dropdown
 * var selected by remember { mutableStateOf<Country?>(null) }
 * PixaDropdown(
 *     items = countries.map { DropdownItem(it, it.name) },
 *     selectedItem = selected,
 *     onItemSelected = { selected = it },
 *     placeholder = "Select country"
 * )
 *
 * // Dropdown with icons
 * PixaDropdown(
 *     items = listOf(
 *         DropdownItem("en", "English", painterResource(Res.drawable.flag_en)),
 *         DropdownItem("fr", "French", painterResource(Res.drawable.flag_fr))
 *     ),
 *     selectedItem = language,
 *     onItemSelected = { language = it },
 *     variant = DropdownVariant.Filled
 * )
 * ```
 *
 * @param items List of dropdown items
 * @param selectedItem Currently selected item value
 * @param onItemSelected Callback when item is selected
 * @param modifier Modifier
 * @param placeholder Placeholder text
 * @param variant Visual style
 * @param size Size preset
 * @param colors Custom colors
 * @param leadingIcon Optional leading icon
 * @param trailingIcon Custom trailing icon (chevron)
 * @param enabled Whether dropdown is interactive
 * @param label Optional label above dropdown
 */
@Composable
fun <T> PixaDropdown(
    items: List<DropdownItem<T>>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select...",
    variant: DropdownVariant = DropdownVariant.Outlined,
    size: DropdownSize = DropdownSize.Medium,
    colors: DropdownColors? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    enabled: Boolean = true,
    label: String? = null
) {
    val themeColors = colors ?: getDropdownTheme(variant)
    val sizeConfig = getDropdownSizeConfig(size)

    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = items.find { it.value == selectedItem }?.label

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(200)
    )

    val shape = RoundedCornerShape(sizeConfig.cornerRadius)
    val backgroundColor = if (enabled) themeColors.background else themeColors.disabledBackground
    val textColor = if (enabled) {
        if (selectedLabel != null) themeColors.text else themeColors.placeholder
    } else themeColors.disabledText

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = AppTheme.typography.labelMedium,
                color = themeColors.text,
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Compact)
            )
        }

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(backgroundColor)
                    .then(
                        if (variant == DropdownVariant.Outlined) {
                            Modifier.border(sizeConfig.borderWidth, themeColors.border, shape)
                        } else Modifier
                    )
                    .clickable(
                        enabled = enabled,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true),
                        role = Role.DropdownList,
                        onClick = { expanded = !expanded }
                    )
                    .padding(horizontal = sizeConfig.padding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (leadingIcon != null) {
                        PixaIcon(
                            painter = leadingIcon,
                            contentDescription = null,
                            tint = themeColors.icon,
                            modifier = Modifier
                                .size(sizeConfig.iconSize)
                                .padding(end = HierarchicalSize.Spacing.Small)
                        )
                    }
                    Text(
                        text = selectedLabel ?: placeholder,
                        style = sizeConfig.textStyle,
                        color = textColor,
                        modifier = Modifier.padding(vertical = sizeConfig.padding)
                    )
                }

                if (trailingIcon != null) {
                    PixaIcon(
                        painter = trailingIcon,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = themeColors.icon,
                        modifier = Modifier
                            .size(sizeConfig.iconSize)
                            .rotate(rotation)
                    )
                }
            }

            if (expanded) {
                Popup(
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = sizeConfig.menuMaxHeight)
                            .shadow(HierarchicalSize.Shadow.Medium, shape)
                            .clip(shape)
                            .background(themeColors.menuBackground)
                    ) {
                        itemsIndexed(items) { _, item ->
                            val isSelected = item.value == selectedItem
                            val itemBackground = when {
                                isSelected -> themeColors.selectedBackground
                                else -> Color.Transparent
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(itemBackground)
                                    .clickable(
                                        enabled = item.enabled,
                                        onClick = {
                                            onItemSelected(item.value)
                                            expanded = false
                                        }
                                    )
                                    .padding(sizeConfig.padding),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (item.icon != null) {
                                    PixaIcon(
                                        painter = item.icon,
                                        contentDescription = null,
                                        tint = themeColors.icon,
                                        modifier = Modifier
                                            .size(sizeConfig.iconSize)
                                            .padding(end = HierarchicalSize.Spacing.Small)
                                    )
                                }
                                Text(
                                    text = item.label,
                                    style = sizeConfig.textStyle,
                                    color = if (item.enabled) themeColors.text else themeColors.disabledText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
