package com.pixamob.pixacompose.components.actions

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Selection behavior:
 * - [Single] = radio — selecting one deselects all others; tapping the
 *   already-selected item is a no-op.
 * - [Multi] = checkbox — any number can be selected; tapping selected deselects.
 * - [None] = plain action group — buttons trigger tasks independently,
 *   no selection state is tracked.
 */
enum class ButtonGroupSelectionMode {
    Single,
    Multi,
    None
}

/**
 * Layout/overflow behavior:
 * - [Clustered] — wraps buttons onto additional rows at the container edge.
 * - [HorizontalScroll] — keeps buttons in a single scrollable row.
 */
enum class ButtonGroupLayout {
    Clustered,
    HorizontalScroll
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class ButtonGroupItem(
    val id: String,
    val text: String? = null,
    val leadingIcon: Painter? = null,
    val trailingIcon: Painter? = null,
    val enabled: Boolean = true,
    val description: String? = null
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun buttonGroupSpacing(size: SizeVariant): Dp =
    HierarchicalSize.Spacing.forVariant(size)

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL BUTTON GROUP
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ClusteredButtonGroup(
    modifier: Modifier = Modifier,
    spacing: Dp,
    content: @Composable () -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        content()
    }
}

@Composable
private fun ScrollingButtonGroup(
    modifier: Modifier = Modifier,
    spacing: Dp,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .wrapContentWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        content()
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaButtonGroup — a collection of 2+ [PixaButton]s for filtering content,
 * making selections, or triggering related actions in a compact space.
 *
 * ### Anatomy
 * A container of individual [PixaButton]s ([items]) — each button keeps its own
 * enabled/selected state while participating in the group's selection logic.
 * The group renders no chrome of its own (no background/border).
 *
 * ### Variants
 * [selectionMode] drives interaction: [ButtonGroupSelectionMode.Single] (radio —
 * exactly one selected, tapping the current selection is a no-op),
 * [ButtonGroupSelectionMode.Multi] (checkbox — any number selected, tap again to
 * deselect), [ButtonGroupSelectionMode.None] (plain action group, no selection
 * tracking).
 *
 * ### Layout
 * [ButtonGroupLayout.Clustered] wraps buttons onto additional rows;
 * [ButtonGroupLayout.HorizontalScroll] keeps a single scrollable row.
 * Callers pick explicitly rather than the group switching itself.
 *
 * ### Sizing
 * A single [size] and [shape] apply to every button, keeping them visually
 * consistent.
 *
 * ### States
 * [enabled] = false disables the whole group. Per-item [ButtonGroupItem.enabled]
 * can additionally disable a single button within an otherwise enabled group.
 *
 * ### Usage notes
 * - Use short, succinct labels; avoid long labels that hide the scroll
 *   affordance in [ButtonGroupLayout.HorizontalScroll] (not runtime-enforced).
 * - Don't group [ButtonVariant.Ghost] buttons together — prefer [ButtonVariant.Tonal]
 *   or [ButtonVariant.Outlined].
 * - Use [PixaTab] or segmented-controls for navigation/single-choice-only cases;
 *   button groups additionally support multi-select and independently-triggered
 *   actions.
 *
 * @param items The buttons to render, in order (provide at least 2)
 * @param modifier Modifier for the group container
 * @param selectedIds Currently selected item ids (ignored when [selectionMode] is [ButtonGroupSelectionMode.None])
 * @param onSelectionChange Called with the new selection set after a selection-changing tap
 * @param onItemClick Called with the tapped item's id on every tap, regardless of [selectionMode]
 * @param selectionMode Selection behavior (Default: [ButtonGroupSelectionMode.Single])
 * @param layout Overflow behavior (Default: [ButtonGroupLayout.Clustered])
 * @param variant Visual hierarchy applied to every button (Default: [ButtonVariant.Tonal])
 * @param size Size variant applied to every button (Default: [SizeVariant.Medium])
 * @param shape Shape applied to every button (Default: [ButtonShape.Default])
 * @param isDestructive Whether the group represents a destructive choice (uses error colors)
 * @param enabled Whether the whole group is enabled (Default: true)
 *
 * @sample
 * ```
 * // Single-select filter group
 * PixaButtonGroup(
 *     items = listOf(
 *         ButtonGroupItem("all", text = "All"),
 *         ButtonGroupItem("active", text = "Active"),
 *         ButtonGroupItem("done", text = "Done")
 *     ),
 *     selectedIds = setOf(selected),
 *     onSelectionChange = { selected = it.first() },
 *     selectionMode = ButtonGroupSelectionMode.Single
 * )
 * ```
 */
@Composable
fun PixaButtonGroup(
    items: List<ButtonGroupItem>,
    modifier: Modifier = Modifier,
    selectedIds: Set<String> = emptySet(),
    onSelectionChange: (Set<String>) -> Unit = {},
    onItemClick: (String) -> Unit = {},
    selectionMode: ButtonGroupSelectionMode = ButtonGroupSelectionMode.Single,
    layout: ButtonGroupLayout = ButtonGroupLayout.Clustered,
    variant: ButtonVariant = ButtonVariant.Tonal,
    size: SizeVariant = SizeVariant.Medium,
    shape: ButtonShape = ButtonShape.Default,
    isDestructive: Boolean = false,
    enabled: Boolean = true,
) {
    val spacing = buttonGroupSpacing(size)

    fun handleTap(id: String) {
        onItemClick(id)
        when (selectionMode) {
            ButtonGroupSelectionMode.Single -> {
                if (id !in selectedIds) {
                    onSelectionChange(setOf(id))
                }
            }

            ButtonGroupSelectionMode.Multi -> {
                onSelectionChange(
                    if (id in selectedIds) selectedIds - id else selectedIds + id
                )
            }

            ButtonGroupSelectionMode.None -> Unit
        }
    }

    val buttons: @Composable () -> Unit = {
        items.forEach { item ->
            PixaButton(
                onClick = { handleTap(item.id) },
                text = item.text,
                variant = variant,
                isDestructive = isDestructive,
                enabled = enabled && item.enabled,
                size = size,
                shape = shape,
                selected = selectionMode != ButtonGroupSelectionMode.None && item.id in selectedIds,
                leadingIcon = item.leadingIcon,
                trailingIcon = item.trailingIcon,
                description = item.description
            )
        }
    }

    when (layout) {
        ButtonGroupLayout.Clustered -> ClusteredButtonGroup(
            modifier = modifier,
            spacing = spacing,
            content = buttons
        )

        ButtonGroupLayout.HorizontalScroll -> ScrollingButtonGroup(
            modifier = modifier,
            spacing = spacing,
            content = buttons
        )
    }
}
