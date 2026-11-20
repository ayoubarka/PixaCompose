package com.pixamob.pixacompose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.theme.PixaTheme

/** Edge of the avatar in each menu row; the avatar slot is fitted to it. */
private val MenuAvatarEdge = 28.dp

/**
 * A pill showing the current item's avatar and a chevron, for switching between a few profiles,
 * pets or workspaces. Tapping it opens a [PixaMenu] of [items], with a check on the current one
 * and, when [onAdd] is set, a last row to add a new one.
 *
 * ```
 * PixaAvatarSwitcher(
 *     items = pets, selected = pet, onSelect = { pet = it },
 *     expanded = open, onExpandedChange = { open = it },
 *     onAdd = onAddPet, addLabel = { PixaText("Add pet") },
 *     label = { PixaText(it.name) },
 * ) { item, current -> PixaAvatar(ringColor = item.color, selected = current) { PixaText(item.initial) } }
 * ```
 *
 * The pill is as wide as its content unless [modifier] gives it a width, such as
 * `Modifier.fillMaxWidth()` or `Modifier.width(240.dp)`; the label then takes the free space
 * and the chevron stays at the end.
 *
 * @param selected the current item; null shows the first one.
 * @param showLabel shows the current item's [label] in the pill; false keeps only the avatar
 * and the chevron, for a tight spot such as a top bar.
 * @param contentDescription what the pill does, for screen readers, such as "Switch pet".
 * @param avatarSize edge of the avatar in the pill. The menu rows keep a small one.
 * @param menuLabel the label in each menu row, when it should differ from the pill's [label],
 * for example a large name in the pill and a plain one in the menu.
 * @param avatar draws one item's avatar; `current` is true for the selected item. It is fitted
 * into a small square, so any [PixaAvatarSize] works.
 */
@Composable
public fun <T> PixaAvatarSwitcher(
    items: List<T>,
    selected: T?,
    onSelect: (T) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    showLabel: Boolean = true,
    onAdd: (() -> Unit)? = null,
    addLabel: @Composable RowScope.() -> Unit = {},
    avatarSize: PixaAvatarSize = PixaAvatarSize.Sm,
    label: @Composable RowScope.(item: T) -> Unit,
    menuLabel: @Composable RowScope.(item: T) -> Unit = label,
    avatar: @Composable (item: T, current: Boolean) -> Unit,
) {
    if (items.isEmpty()) return
    val spacing = PixaTheme.spacing
    val current = selected ?: items.first()
    // Intrinsic width: wraps the content by default, yet any width from the caller wins.
    Box(modifier.width(IntrinsicSize.Max)) {
        val described = if (contentDescription != null) Modifier.semantics { this.contentDescription = contentDescription } else Modifier
        PixaSurface(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier.fillMaxWidth().then(described),
            enabled = enabled,
            role = Role.DropdownList,
            color = Color.Transparent,
            contentColor = PixaTheme.colors.foreground,
            shape = PixaTheme.shapes.full,
            border = outlinedBorder(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = spacing.xs, end = spacing.sm, top = spacing.xs, bottom = spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(avatarSize.edge), contentAlignment = Alignment.Center) { avatar(current, true) }
                if (showLabel) {
                    ProvideContentStyle(PixaTheme.colors.foreground, PixaTheme.typography.label) {
                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) { label(current) }
                    }
                    PixaIcon(PixaIcons.ChevronDown, contentDescription = null, size = 18.dp)
                } else {
                    // Pushes the chevron to the end when the pill is given a width.
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        PixaIcon(PixaIcons.ChevronDown, contentDescription = null, size = 18.dp)
                    }
                }
            }
        }
        PixaMenu(visible = expanded, onDismissRequest = { onExpandedChange(false) }) {
            items.forEach { item ->
                val isCurrent = item == current
                PixaMenuItem(onClick = { onExpandedChange(false); onSelect(item) }) {
                    Box(Modifier.size(MenuAvatarEdge), contentAlignment = Alignment.Center) { avatar(item, isCurrent) }
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) { menuLabel(item) }
                    if (isCurrent) PixaIcon(PixaIcons.Check, contentDescription = "Selected", size = 18.dp)
                }
            }
            if (onAdd != null) {
                PixaSeparator()
                PixaMenuItem(onClick = { onExpandedChange(false); onAdd() }) {
                    Box(Modifier.size(MenuAvatarEdge), contentAlignment = Alignment.Center) {
                        PixaIcon(PixaIcons.Plus, contentDescription = null, size = 18.dp)
                    }
                    addLabel()
                }
            }
        }
    }
}
