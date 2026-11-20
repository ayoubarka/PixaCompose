package com.pixamob.pixacompose.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.ProvideContentStyle
import com.pixamob.pixacompose.theme.PixaTheme

/**
 * A scrolling row of avatars with a label under each, for picking one of a few profiles, pets
 * or workspaces right on the screen. When [onAdd] is set, a last circle with a plus adds one.
 *
 * ```
 * PixaAvatarRow(
 *     items = pets, selected = pet, onSelect = { pet = it },
 *     onAdd = onAddPet, addLabel = { PixaText("Add") },
 *     label = { PixaText(it.name) },
 * ) { item, current -> PixaAvatar(size = PixaAvatarSize.Lg, ringColor = item.color, selected = current) { PixaText(item.initial) } }
 * ```
 *
 * @param selected the current item; null selects none.
 * @param avatarSize edge of the add circle; match it to the avatars drawn by [avatar].
 * @param label the text under an avatar. The current item's is drawn in the foreground color,
 * the others muted.
 * @param avatar draws one item's avatar; `current` is true for the selected item.
 */
@Composable
public fun <T> PixaAvatarRow(
    items: List<T>,
    selected: T?,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onAdd: (() -> Unit)? = null,
    addLabel: @Composable ColumnScope.() -> Unit = {},
    avatarSize: PixaAvatarSize = PixaAvatarSize.Lg,
    label: @Composable ColumnScope.(item: T) -> Unit,
    avatar: @Composable (item: T, current: Boolean) -> Unit,
) {
    val colors = PixaTheme.colors
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(PixaTheme.spacing.xs),
    ) {
        items.forEach { item ->
            val current = item == selected
            AvatarCell(
                surface = { content ->
                    PixaSurface(
                        selected = current,
                        onClick = { onSelect(item) },
                        enabled = enabled,
                        color = Color.Transparent,
                        shape = PixaTheme.shapes.md,
                    ) { content() }
                },
                labelColor = if (current) colors.foreground else colors.mutedForeground,
                avatar = { avatar(item, current) },
                label = { label(item) },
            )
        }
        if (onAdd != null) {
            AvatarCell(
                surface = { content ->
                    PixaSurface(onClick = onAdd, enabled = enabled, color = Color.Transparent, shape = PixaTheme.shapes.md) { content() }
                },
                labelColor = colors.mutedForeground,
                avatar = {
                    PixaAvatar(size = avatarSize, color = colors.secondary, contentColor = colors.secondaryForeground) {
                        PixaIcon(PixaIcons.Plus, contentDescription = null)
                    }
                },
                label = addLabel,
            )
        }
    }
}

/** One avatar over its label, inside the [surface] that makes it tappable. */
@Composable
private fun AvatarCell(
    surface: @Composable (content: @Composable () -> Unit) -> Unit,
    labelColor: Color,
    avatar: @Composable () -> Unit,
    label: @Composable ColumnScope.() -> Unit,
) {
    val spacing = PixaTheme.spacing
    surface {
        Column(
            modifier = Modifier.padding(spacing.xs),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            avatar()
            ProvideContentStyle(labelColor, PixaTheme.typography.caption) { label() }
        }
    }
}
