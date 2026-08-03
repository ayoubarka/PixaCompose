package com.pixamob.pixacompose.components.surfaces

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.WindowSizeClass

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Horizontal placement of the floating content slot within the content area,
 * mirroring the FAB-position concept from the Material scaffold reference
 * without depending on it.
 */
enum class ScaffoldFabPosition {
    Start,
    Center,
    End
}

/**
 * Where a single adaptive navigation slot is rendered.
 *
 * [Auto] follows the current [WindowSizeClass]: a bottom bar on
 * [WindowSizeClass.Compact], a leading rail on wider viewports — the
 * PixaCompose-native equivalent of Material's adaptive navigation switch.
 * [BottomBar]/[Rail] pin the placement regardless of size class.
 */
enum class ScaffoldNavigationMode {
    Auto,
    BottomBar,
    Rail
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Surface colors for the scaffold canvas. Bars, rails and panes supply their
 * own backgrounds — this only paints the screen behind the primary content.
 */
@Immutable
@Stable
data class ScaffoldColors(
    val background: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getScaffoldColors(colors: ColorPalette): ScaffoldColors = ScaffoldColors(
    // The subtle base surface reads as the app canvas; elevated bars/cards sit on top of it.
    background = colors.baseSurfaceSubtle
)

/**
 * Default content padding: adaptive screen-edge margin on the horizontal axis
 * ([AppTheme.pageMargin]), flush on the vertical axis so bars sit edge-to-edge
 * against the content. Callers apply this to their content root.
 */
@Composable
private fun defaultContentPadding(): PaddingValues =
    PaddingValues(horizontal = AppTheme.pageMargin)

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL SCAFFOLD
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun InternalScaffold(
    modifier: Modifier,
    colors: ScaffoldColors,
    contentPadding: PaddingValues,
    fabPosition: ScaffoldFabPosition,
    floatingGap: Dp,
    topBar: (@Composable () -> Unit)?,
    bottomBar: (@Composable () -> Unit)?,
    navigationRail: (@Composable () -> Unit)?,
    sidePane: (@Composable () -> Unit)?,
    floatingContent: (@Composable () -> Unit)?,
    content: @Composable (PaddingValues) -> Unit
) {
    val background by animateColorAsState(
        targetValue = colors.background,
        animationSpec = AnimationUtils.standardTween(150),
        label = "scaffold_bg"
    )

    val fabAlignment = when (fabPosition) {
        ScaffoldFabPosition.Start -> Alignment.BottomStart
        ScaffoldFabPosition.Center -> Alignment.BottomCenter
        ScaffoldFabPosition.End -> Alignment.BottomEnd
    }

    // Column: [topBar] / [rail | content(+side pane, +floating) ] / [bottomBar].
    // Bars occupy their own rows so the content area is naturally sized to the
    // remaining space — no overlap, no measured-inset padding pass.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
    ) {
        topBar?.invoke()

        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
            navigationRail?.let { rail ->
                Box(modifier = Modifier.fillMaxHeight()) { rail() }
            }

            Box(modifier = Modifier.fillMaxHeight().weight(1f)) {
                // Primary content receives the padding contract; it decides whether
                // to consume it (padded root) or offset it (edge-to-edge lists).
                content(contentPadding)

                floatingContent?.let { fab ->
                    Box(
                        modifier = Modifier
                            .align(fabAlignment)
                            .padding(floatingGap),
                        contentAlignment = Alignment.Center
                    ) { fab() }
                }
            }

            sidePane?.let { pane ->
                Box(modifier = Modifier.fillMaxHeight()) { pane() }
            }
        }

        bottomBar?.invoke()
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * Screen-level layout container that frames a primary [content] area with
 * optional bars, rails, a side pane and floating content.
 *
 * ### Anatomy
 * ```
 * ┌───────────────── topBar ─────────────────┐
 * │ rail │        content (+ floating)  │ side │
 * │      │                              │ pane │
 * └───────────────── bottomBar ───────────────┘
 * ```
 * Slots are stacked structurally (Column of bars around a Row of rail/content/
 * pane), so each slot owns its space and nothing overlaps. This is the
 * PixaCompose adaptation of the Material scaffold role — a structural container,
 * not a Material `Scaffold` wrapper.
 *
 * ### Slots
 * - [topBar] / [bottomBar]: frame navigation or actions (e.g. `PixaTopNavBar`,
 *   `PixaBottomNavBar`).
 * - [navigationRail]: leading perimeter structure for wide viewports.
 * - [sidePane]: trailing supporting pane (detail/supporting content).
 * - [floatingContent]: overlaid above the content area (e.g. `PixaFab`),
 *   positioned by [fabPosition].
 * - [content]: primary area, receives the [contentPadding] contract to apply
 *   (padded root) or offset (edge-to-edge scrolling lists).
 *
 * ### Layout policy
 * Content padding defaults to the adaptive [AppTheme.pageMargin] on the
 * horizontal axis and is flush vertically so bars meet content edge-to-edge.
 * The floating-content inset scales with [size] via
 * [HierarchicalSize.Spacing]. Bars are drawn opaque in normal flow rather than
 * behind translucent content — the deliberate simplification over Material's
 * measured-inset scaffold; components that need system-bar insets (e.g.
 * `PixaTopNavBar`) already apply them internally.
 *
 * ### Sizing / Adaptive behavior
 * [size] tunes the floating-content gap. For adaptive navigation that switches
 * a single nav slot between a bottom bar and a rail by [WindowSizeClass], use
 * [PixaAdaptiveScaffold].
 *
 * @param modifier Modifier for the root container
 * @param colors Canvas colors (defaults to the subtle base surface)
 * @param size Size variant governing the floating-content inset
 * @param contentPadding Padding contract handed to [content] (defaults to
 *   adaptive horizontal [AppTheme.pageMargin])
 * @param fabPosition Horizontal placement of [floatingContent]
 * @param topBar Optional top bar slot
 * @param bottomBar Optional bottom bar slot
 * @param navigationRail Optional leading rail slot
 * @param sidePane Optional trailing supporting pane slot
 * @param floatingContent Optional floating overlay slot
 * @param content Primary content, receiving the padding contract to apply
 */
@Composable
fun PixaScaffold(
    modifier: Modifier = Modifier,
    colors: ScaffoldColors? = null,
    size: SizeVariant = SizeVariant.Medium,
    contentPadding: PaddingValues? = null,
    fabPosition: ScaffoldFabPosition = ScaffoldFabPosition.End,
    topBar: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    navigationRail: (@Composable () -> Unit)? = null,
    sidePane: (@Composable () -> Unit)? = null,
    floatingContent: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val resolvedColors = colors ?: getScaffoldColors(AppTheme.colors)
    val resolvedPadding = contentPadding ?: defaultContentPadding()
    InternalScaffold(
        modifier = modifier,
        colors = resolvedColors,
        contentPadding = resolvedPadding,
        fabPosition = fabPosition,
        floatingGap = HierarchicalSize.Spacing.forVariant(size),
        topBar = topBar,
        bottomBar = bottomBar,
        navigationRail = navigationRail,
        sidePane = sidePane,
        floatingContent = floatingContent,
        content = content
    )
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Scaffold that adapts a single [navigation] slot to the current
 * [WindowSizeClass]: a bottom bar on [WindowSizeClass.Compact], a leading rail
 * on wider viewports. Opt-in adaptive placement (the library convention) —
 * pass [ScaffoldNavigationMode.BottomBar]/[ScaffoldNavigationMode.Rail] to pin
 * one placement.
 *
 * @param navigation The navigation surface, placed as a bar or rail by [mode]
 * @param mode Adaptive placement policy (defaults to [ScaffoldNavigationMode.Auto])
 * @param topBar Optional top bar slot
 * @param sidePane Optional trailing supporting pane slot
 * @param floatingContent Optional floating overlay slot
 * @param fabPosition Horizontal placement of [floatingContent]
 * @param content Primary content, receiving the padding contract to apply
 */
@Composable
fun PixaAdaptiveScaffold(
    navigation: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    mode: ScaffoldNavigationMode = ScaffoldNavigationMode.Auto,
    colors: ScaffoldColors? = null,
    size: SizeVariant = SizeVariant.Medium,
    contentPadding: PaddingValues? = null,
    topBar: (@Composable () -> Unit)? = null,
    sidePane: (@Composable () -> Unit)? = null,
    floatingContent: (@Composable () -> Unit)? = null,
    fabPosition: ScaffoldFabPosition = ScaffoldFabPosition.End,
    content: @Composable (PaddingValues) -> Unit
) {
    val useRail = when (mode) {
        ScaffoldNavigationMode.Rail -> true
        ScaffoldNavigationMode.BottomBar -> false
        ScaffoldNavigationMode.Auto -> AppTheme.windowSizeClass != WindowSizeClass.Compact
    }
    PixaScaffold(
        modifier = modifier,
        colors = colors,
        size = size,
        contentPadding = contentPadding,
        fabPosition = fabPosition,
        topBar = topBar,
        bottomBar = if (useRail) null else navigation,
        navigationRail = if (useRail) navigation else null,
        sidePane = sidePane,
        floatingContent = floatingContent,
        content = content
    )
}

/**
 * Scaffold aligned to the primary content start/end margins of [contentPadding]
 * applied for you — a plain container preset for simple screens that only need a
 * top bar, bottom bar and padded content, no rail/pane/floating slots.
 *
 * The [content] here receives no padding lambda; the horizontal
 * [AppTheme.pageMargin] is applied directly so the caller can drop in a
 * `Column` without wiring the padding contract.
 */
@Composable
fun PixaSimpleScaffold(
    modifier: Modifier = Modifier,
    colors: ScaffoldColors? = null,
    topBar: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current
    PixaScaffold(
        modifier = modifier,
        colors = colors,
        topBar = topBar,
        bottomBar = bottomBar
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = padding.calculateStartPadding(layoutDirection),
                    end = padding.calculateEndPadding(layoutDirection),
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                ),
            content = content
        )
    }
}
