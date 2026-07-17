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
 * Selection behavior, mapped from Uber Base's Single Select (radio) / Multi-select
 * (checkbox) button group variants:
 * - [Single] = radio behavior — selecting one item deselects all others; tapping
 *   the already-selected item is a no-op (a radio group always has one selection).
 * - [Multi] = checkbox behavior — any number of items can be selected; tapping a
 *   selected item deselects it.
 * - [None] = plain action group — buttons independently trigger tasks, no
 *   selection state is tracked (Uber Base's "grouping related actions" use case).
 */
enum class ButtonGroupSelectionMode {
    Single,
    Multi,
    None
}

/**
 * Layout/overflow behavior, mapped from Uber Base's Clustered / Horizontal scroll
 * layout modes. Uber Base's third mode (vertical scroll, where surrounding page
 * content scrolls beneath a pinned button group) is a page-level layout concern,
 * not something the group container itself renders — out of scope here, same as
 * any other component that can simply be placed inside a scrollable column.
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
 * PixaButtonGroup — a collection of 2+ [PixaButton]s combined to let users filter
 * content, make selections, or trigger related actions in a compact space.
 *
 * ### Anatomy
 * A container of individual [PixaButton]s ([items]). Each button keeps its own
 * enabled/selected state while participating in the group's shared selection
 * logic — the group itself renders no chrome of its own (no background/border),
 * matching Uber Base's "2 or more buttons inside a container" anatomy without
 * inventing a new visual surface.
 *
 * ### Variants
 * [selectionMode] drives interaction: [ButtonGroupSelectionMode.Single] (radio —
 * exactly one item selected, tapping the current selection is a no-op),
 * [ButtonGroupSelectionMode.Multi] (checkbox — any number selected, tap again to
 * deselect), [ButtonGroupSelectionMode.None] (plain action group, [onItemClick]
 * fires but no selection is tracked).
 *
 * ### Layout
 * [ButtonGroupLayout.Clustered] wraps buttons onto additional rows at the
 * container edge (Uber Base's Medium/Large breakpoint recommendation);
 * [ButtonGroupLayout.HorizontalScroll] keeps a single scrollable row (Uber
 * Base's Small/mobile breakpoint recommendation). Callers pick explicitly per
 * [AppTheme.windowSizeClass] rather than the group silently switching itself —
 * consistent with [PixaButton]'s own opt-in-only adaptive behavior.
 *
 * ### Sizing
 * A single [size] and [shape] apply to every button in the group, satisfying
 * Uber Base's "buttons of the same size and the same corner radius work better
 * together" rule structurally rather than leaving it to caller discipline.
 *
 * ### States
 * [enabled] = false disables the whole group — every button renders disabled,
 * per Uber Base ("when the button group is disabled, all of the buttons in the
 * group are defaulted to disabled"). Per-item [ButtonGroupItem.enabled] can
 * additionally disable a single button within an otherwise enabled group.
 *
 * ### Usage notes
 * - Use short, succinct labels; avoid long labels that hide the scroll
 *   affordance in [ButtonGroupLayout.HorizontalScroll] (Uber Base content rule,
 *   not runtime-enforced — same precedent as [PixaButton]'s label rules).
 * - Don't group [ButtonVariant.Ghost] (tertiary/transparent) buttons together —
 *   prefer [ButtonVariant.Tonal] (secondary) or [ButtonVariant.Outlined] (outline).
 * - Compare against [PixaTab]/segmented-control patterns for pure navigation or
 *   single-choice-only cases; button groups additionally support multi-select
 *   and independently-triggered actions.
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
