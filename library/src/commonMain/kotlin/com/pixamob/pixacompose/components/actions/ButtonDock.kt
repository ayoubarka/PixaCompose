package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.WindowSizeClass
import com.pixamob.pixacompose.utils.elevationShadow
import com.pixamob.pixacompose.utils.toDp

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Button arrangement inside the dock:
 * - [Vertical] (≤600dp) — buttons stack full-width, one per row.
 * - [Horizontal] (>600dp) — buttons sit side by side in a single row.
 * - [Auto] (default) — resolves to [Vertical] below the 600dp breakpoint and
 *   [Horizontal] at/above it, via [AppTheme.windowSizeClass]/[WindowSizeClass.Compact].
 *   An explicit [Vertical]/[Horizontal] always wins.
 */
enum class DockLayout {
    Auto,
    Vertical,
    Horizontal
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/**
 * A single dock button. Rendered as rectangular [PixaButton] with
 * [ButtonShape.Default] — this type deliberately has no `shape` field.
 */
@Immutable
@Stable
data class ButtonDockItem(
    val id: String,
    val text: String?,
    val onClick: () -> Unit,
    val variant: ButtonVariant = ButtonVariant.Tonal,
    val isDestructive: Boolean = false,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val leadingIcon: Painter? = null,
    val trailingIcon: Painter? = null,
    val description: String? = null
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun resolveDockLayout(layout: DockLayout): DockLayout = when (layout) {
    DockLayout.Auto -> if (AppTheme.windowSizeClass == WindowSizeClass.Compact) {
        DockLayout.Vertical
    } else {
        DockLayout.Horizontal
    }

    else -> layout
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL BUTTON DOCK
// ════════════════════════════════════════════════════════════════════════════

/**
 * Overflow hint above the dock — a shadow hinting at more content below the
 * fold, faded in/out with [hasOverflowContent]. Removed when there is no
 * overflow.
 */
@Composable
private fun DockOverflowShadow(visible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        enter = AnimationUtils.fadeInTransition,
        exit = AnimationUtils.fadeOutTransition
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(HierarchicalSize.Shadow.Small)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            AppTheme.colors.baseSurfaceDefault.copy(alpha = 0.12f)
                        )
                    )
                )
        )
    }
}

@Composable
private fun DockButtons(
    items: List<ButtonDockItem>,
    layout: DockLayout,
    size: SizeVariant,
    spacing: Dp
) {
    when (layout) {
        DockLayout.Vertical, DockLayout.Auto -> Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            items.forEach { item ->
                PixaButton(
                    onClick = item.onClick,
                    modifier = Modifier.fillMaxWidth(),
                    text = item.text,
                    variant = item.variant,
                    isDestructive = item.isDestructive,
                    enabled = item.enabled,
                    loading = item.loading,
                    size = size,
                    shape = ButtonShape.Default,
                    widthPolicy = ButtonWidthPolicy.FullBleed,
                    leadingIcon = item.leadingIcon,
                    trailingIcon = item.trailingIcon,
                    description = item.description
                )
            }
        }

        DockLayout.Horizontal -> Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            items.forEach { item ->
                PixaButton(
                    onClick = item.onClick,
                    modifier = Modifier.weight(1f),
                    text = item.text,
                    variant = item.variant,
                    isDestructive = item.isDestructive,
                    enabled = item.enabled,
                    loading = item.loading,
                    size = size,
                    shape = ButtonShape.Default,
                    widthPolicy = ButtonWidthPolicy.FullBleed,
                    leadingIcon = item.leadingIcon,
                    trailingIcon = item.trailingIcon,
                    description = item.description
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaButtonDock — a container pinned to the bottom of the screen for keeping
 * up to 4 primary actions visible while page content scrolls behind it.
 *
 * ### Anatomy
 * An optional top [accessory] slot (any composable — summary price, progress
 * line, etc.), followed by 1-4 [items] rendered as large, rectangular
 * [PixaButton]s (shape = [ButtonShape.Default]). Each item is independently
 * enable/disable-able via [ButtonDockItem.enabled]. This composable does not
 * apply its own sticky positioning — callers place it at the bottom of their
 * own `Box`/`Scaffold`-style root.
 *
 * ### Layout
 * [DockLayout.Vertical] (≤600dp) stacks full-width buttons — pass destructive
 * actions first (farthest from the thumb) and dismissive actions last (closest
 * to the thumb). [DockLayout.Horizontal] (>600dp) lays buttons side by side,
 * equally weighted. [DockLayout.Auto] (default) resolves via [AppTheme.windowSizeClass].
 *
 * ### Sizing
 * A single [size] (default [SizeVariant.Large]) applies to every item.
 *
 * ### States
 * [hasOverflowContent] drives the top overflow shadow hint — pass whether
 * scrollable content has more to reveal below the fold; the hint fades in/out.
 *
 * ### Customization
 * [topSpacing] toggles the gap above [accessory]/[items]; background, shadow,
 * and border use fixed design tokens (surface color, [ComponentElevation.High]
 * shadow, hairline top border).
 *
 * ### Usage notes
 * - Up to 4 buttons; most experiences need only 1-2 (not runtime-enforced).
 * - Prefer short labels — side-by-side [DockLayout.Horizontal] labels don't
 *   localize well; vertical stacking accommodates longer translations better.
 *
 * @param items Dock buttons, in thumb-ergonomic order (max 4 recommended)
 * @param modifier Modifier for the dock container
 * @param accessory Optional content above the buttons (progress, price summary, etc.)
 * @param layout Button arrangement (Default: [DockLayout.Auto])
 * @param size Size variant applied to every button (Default: [SizeVariant.Large])
 * @param hasOverflowContent Whether to show the top overflow shadow hint (Default: false)
 * @param topSpacing Whether to reserve a gap above the content (Default: true)
 *
 * @sample
 * ```
 * PixaButtonDock(
 *     items = listOf(
 *         ButtonDockItem(id = "confirm", text = "Confirm", variant = ButtonVariant.Filled, onClick = { }),
 *         ButtonDockItem(id = "cancel", text = "Cancel", variant = ButtonVariant.Ghost, onClick = { })
 *     ),
 *     hasOverflowContent = listState.canScrollForward
 * )
 * ```
 */
@Composable
fun PixaButtonDock(
    items: List<ButtonDockItem>,
    modifier: Modifier = Modifier,
    accessory: (@Composable () -> Unit)? = null,
    layout: DockLayout = DockLayout.Auto,
    size: SizeVariant = SizeVariant.Large,
    hasOverflowContent: Boolean = false,
    topSpacing: Boolean = true,
) {
    val resolvedLayout = resolveDockLayout(layout)
    val spacing = HierarchicalSize.Spacing.forVariant(size)
    val horizontalPadding = HierarchicalSize.Padding.forVariant(size)

    Column(modifier = modifier.fillMaxWidth().wrapContentHeight()) {
        DockOverflowShadow(visible = hasOverflowContent)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .elevationShadow(
                    elevation = ComponentElevation.High.toDp(),
                    // The dock spans full container width edge-to-edge — a flat RectangleShape
                    // matches that intent, not a cornered AppTheme.shapes family.
                    shape = RectangleShape,
                    clip = false
                )
                .background(AppTheme.colors.baseSurfaceDefault)
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = if (topSpacing) spacing else HierarchicalSize.Padding.None,
                    bottom = spacing
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            accessory?.invoke()

            if (items.isNotEmpty()) {
                DockButtons(
                    items = items,
                    layout = resolvedLayout,
                    size = size,
                    spacing = spacing
                )
            }
        }
    }
}
