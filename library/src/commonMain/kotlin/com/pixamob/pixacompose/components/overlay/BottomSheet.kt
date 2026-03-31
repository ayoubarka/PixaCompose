package com.pixamob.pixacompose.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.RadiusSize
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// ═══════════════════════════════════════════════════════════════════════════════
// CONFIGURATION - Data models and enums
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Bottom Sheet Size Variant
 * Defines height constraints and padding for different sheet sizes
 */
enum class BottomSheetSizeVariant {
    /** Compact: 40% screen height, minimal padding */
    Compact,
    /** Standard: 60% screen height, standard padding (default) */
    Standard,
    /** Expanded: 75% screen height, generous padding */
    Expanded,
    /** Full: 95% screen height, maximum content */
    Full
}

/**
 * Bottom Sheet Style Variant
 * Defines the visual style/color scheme of the sheet
 */
enum class BottomSheetStyle {
    /** Primary: Default surface with standard content colors */
    Primary,
    /** Secondary: Subtle surface with secondary content colors */
    Secondary,
    /** Surface: White/light surface with high contrast */
    Surface
}

// ═══════════════════════════════════════════════════════════════════════════════
// THEME - Size mappings and styling configurations
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Bottom Sheet Size Configuration
 * Internal configuration for size variant
 */
@Immutable
private data class BottomSheetSizeConfig(
    val maxHeightFraction: Float,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cornerRadius: Dp,
    val dragHandleHeight: Dp,
    val dragHandleWidth: Dp
)

/**
 * Maps size variant to concrete dimensions
 */
private fun BottomSheetSizeVariant.toSizeConfig(): BottomSheetSizeConfig = when (this) {
    BottomSheetSizeVariant.Compact -> BottomSheetSizeConfig(
        maxHeightFraction = 0.4f,
        horizontalPadding = HierarchicalSize.Spacing.Medium,
        verticalPadding = HierarchicalSize.Spacing.Medium,
        cornerRadius = RadiusSize.Large,
        dragHandleHeight = 4.dp,
        dragHandleWidth = 32.dp
    )
    BottomSheetSizeVariant.Standard -> BottomSheetSizeConfig(
        maxHeightFraction = 0.6f,
        horizontalPadding = HierarchicalSize.Spacing.Large,
        verticalPadding = HierarchicalSize.Spacing.Large,
        cornerRadius = RadiusSize.ExtraLarge,
        dragHandleHeight = 4.dp,
        dragHandleWidth = 40.dp
    )
    BottomSheetSizeVariant.Expanded -> BottomSheetSizeConfig(
        maxHeightFraction = 0.75f,
        horizontalPadding = HierarchicalSize.Spacing.Huge,
        verticalPadding = HierarchicalSize.Spacing.Huge,
        cornerRadius = RadiusSize.ExtraLarge,
        dragHandleHeight = 4.dp,
        dragHandleWidth = 48.dp
    )
    BottomSheetSizeVariant.Full -> BottomSheetSizeConfig(
        maxHeightFraction = 0.95f,
        horizontalPadding = HierarchicalSize.Spacing.Huge,
        verticalPadding = HierarchicalSize.Spacing.Huge,
        cornerRadius = RadiusSize.ExtraLarge,
        dragHandleHeight = 4.dp,
        dragHandleWidth = 48.dp
    )
}

/**
 * Bottom Sheet Color Configuration
 */
@Stable
private data class BottomSheetColors(
    val container: Color,
    val content: Color,
    val scrim: Color,
    val dragHandle: Color,
    val divider: Color
)

/**
 * Get colors for sheet style (elevated or default)
 */
@Composable
private fun BottomSheetStyle.getColors(elevated: Boolean): BottomSheetColors {
    val colors = AppTheme.colors

    return when (this) {
        BottomSheetStyle.Primary -> BottomSheetColors(
            container = if (elevated) colors.baseSurfaceElevated else colors.baseSurfaceDefault,
            content = colors.baseContentBody,
            scrim = colors.baseSurfaceShadow.copy(alpha = 0.66f),
            dragHandle = colors.baseContentCaption.copy(alpha = 0.4f),
            divider = colors.baseBorderSubtle.copy(alpha = 0.33f)
        )
        BottomSheetStyle.Secondary -> BottomSheetColors(
            container = if (elevated) colors.baseSurfaceElevated else colors.baseSurfaceSubtle,
            content = colors.baseContentSubtitle,
            scrim = colors.baseSurfaceShadow.copy(alpha = 0.66f),
            dragHandle = colors.baseContentCaption.copy(alpha = 0.4f),
            divider = colors.baseBorderSubtle.copy(alpha = 0.33f)
        )
        BottomSheetStyle.Surface -> BottomSheetColors(
            container = if (elevated) colors.baseSurfaceElevated else colors.baseSurfaceDefault,
            content = colors.baseContentBody,
            scrim = colors.baseSurfaceShadow.copy(alpha = 0.66f),
            dragHandle = colors.baseContentCaption.copy(alpha = 0.4f),
            divider = colors.baseBorderSubtle.copy(alpha = 0.33f)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ANIMATION - Shared bottom sheet animation specs
// ═══════════════════════════════════════════════════════════════════════════════

/** Duration for scrim fade and sheet slide animations (ms). */
private const val SHEET_ANIM_DURATION = 300

/** Sheet slide-in from bottom enter transition. */
private val SheetEnterTransition = slideInVertically(
    initialOffsetY = { it },
    animationSpec = tween(SHEET_ANIM_DURATION)
)

/** Sheet slide-out to bottom exit transition. */
private val SheetExitTransition = slideOutVertically(
    targetOffsetY = { it },
    animationSpec = tween(SHEET_ANIM_DURATION)
)

/** Scrim fade-in enter transition. */
private val ScrimEnterTransition = fadeIn(animationSpec = tween(SHEET_ANIM_DURATION))

/** Scrim fade-out exit transition. */
private val ScrimExitTransition = fadeOut(animationSpec = tween(SHEET_ANIM_DURATION))

// ═══════════════════════════════════════════════════════════════════════════════
// BASE - Internal composables
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Internal drag handle composable.
 * When the sheet is not fixed, drag gestures are attached ONLY here
 * so inner scrollable content never fights with sheet drag.
 */
@Composable
private fun DragHandle(
    width: Dp,
    height: Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .padding(top = 8.dp)
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .background(
                    color = color,
                    shape = RoundedCornerShape(height / 2)
                )
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PUBLIC - Main component
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * PixaBottomSheet — Fully Custom Bottom Sheet Component
 *
 * A 100% custom bottom sheet built from scratch using Box + Popup + drag gestures.
 * No Material 3 ModalBottomSheet dependency. This gives full control over
 * scroll/drag behavior with no interference from M3 internals.
 *
 * ## Architecture
 * ```
 * Popup (full-screen overlay, z-ordered above everything)
 *   └── Box (fillMaxSize)
 *         ├── Scrim  (AnimatedVisibility — fadeIn / fadeOut)
 *         └── Sheet  (AnimatedVisibility — slideInFromBottom / slideOutToBottom)
 *               ├── DragHandle (optional, drag gesture attached here ONLY)
 *               └── Content   (scrollable internally, NO drag gesture)
 * ```
 *
 * ## Key Design Decisions
 * - **Drag gesture is ONLY on the drag handle**, never on the content area.
 *   This means nested scrollable content (LazyColumn, LazyVerticalGrid) scrolls
 *   freely to the end without ever triggering sheet dismissal.
 * - `isFixed = true` disables drag handle & all drag gestures entirely.
 * - Back press is handled by Popup's `onDismissRequest`.
 * - Entry/exit animations use `MutableTransitionState` so the exit animation
 *   plays fully before the Popup is removed from the tree.
 *
 * @param onDismissRequest Callback when sheet should be dismissed
 * @param modifier Modifier for the sheet panel
 * @param size Size variant affecting max height and padding
 * @param style Style variant affecting color scheme
 * @param elevated If true, uses elevated surface colors
 * @param shape Custom shape (defaults to rounded top corners)
 * @param showDragHandle If true, shows drag handle at top
 * @param dismissOnOutsideClick If true, clicking the scrim dismisses the sheet
 * @param dismissOnBackClick If true, back press / gesture dismisses the sheet
 * @param isFixed If true, disables drag handle & all drag gestures
 * @param content The content of the bottom sheet
 */
@Composable
fun PixaBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    size: BottomSheetSizeVariant = BottomSheetSizeVariant.Standard,
    style: BottomSheetStyle = BottomSheetStyle.Primary,
    elevated: Boolean = false,
    shape: Shape? = null,
    showDragHandle: Boolean = true,
    dismissOnOutsideClick: Boolean = true,
    dismissOnBackClick: Boolean = true,
    isFixed: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val sizeConfig = size.toSizeConfig()
    val colors = style.getColors(elevated)
    val sheetShape = shape ?: RoundedCornerShape(
        topStart = sizeConfig.cornerRadius,
        topEnd = sizeConfig.cornerRadius
    )
    val coroutineScope = rememberCoroutineScope()

    // Drag offset — only used when isFixed == false and drag handle is shown
    val dragOffset = remember { Animatable(0f) }

    // Dismiss threshold in pixels
    val dismissThresholdPx = with(LocalDensity.current) { 200.dp.toPx() }

    // ── Animation state ──
    // Starts as false, immediately set to true → triggers enter animation.
    // On dismiss request → set to false → triggers exit animation.
    // When exit animation finishes (isIdle && !currentState && !targetState)
    // → actually remove the Popup by calling onDismissRequest.
    val animVisibleState = remember { MutableTransitionState(false) }

    // Trigger enter animation on first composition
    LaunchedEffect(Unit) {
        animVisibleState.targetState = true
    }

    // Internal dismiss helper: starts the exit animation
    val requestDismiss: () -> Unit = remember(onDismissRequest) {
        { animVisibleState.targetState = false }
    }

    // Detect when exit animation is fully complete → actually dismiss
    LaunchedEffect(animVisibleState) {
        snapshotFlow { animVisibleState.isIdle && !animVisibleState.currentState }
            .collect { finished ->
                if (finished) onDismissRequest()
            }
    }

    // Use Popup for true overlay z-ordering (above nav bars, other content)
    Popup(
        alignment = Alignment.BottomCenter,
        onDismissRequest = {
            if (dismissOnBackClick) requestDismiss()
        },
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = dismissOnBackClick
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // ── Scrim (animated fade) ──
            AnimatedVisibility(
                visibleState = animVisibleState,
                enter = ScrimEnterTransition,
                exit = ScrimExitTransition
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.scrim)
                        .then(
                            if (dismissOnOutsideClick) {
                                Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { requestDismiss() }
                            } else Modifier
                        )
                )
            }

            // ── Sheet panel (animated slide from bottom) ──
            AnimatedVisibility(
                visibleState = animVisibleState,
                enter = SheetEnterTransition,
                exit = SheetExitTransition,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .heightIn(max = 1000.dp.times(sizeConfig.maxHeightFraction))
                        .offset { IntOffset(0, dragOffset.value.roundToInt().coerceAtLeast(0)) }
                        .clip(sheetShape)
                        .background(colors.container)
                        .padding(
                            horizontal = sizeConfig.horizontalPadding,
                            vertical = sizeConfig.verticalPadding
                        )
                ) {
                    // ── Drag Handle (drag gesture attached ONLY here) ──
                    if (showDragHandle && !isFixed) {
                        DragHandle(
                            width = sizeConfig.dragHandleWidth,
                            height = sizeConfig.dragHandleHeight,
                            color = colors.dragHandle,
                            modifier = Modifier.pointerInput(Unit) {
                                detectVerticalDragGestures(
                                    onDragEnd = {
                                        coroutineScope.launch {
                                            if (dragOffset.value > dismissThresholdPx) {
                                                requestDismiss()
                                            } else {
                                                dragOffset.animateTo(
                                                    targetValue = 0f,
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessMedium
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    onDragCancel = {
                                        coroutineScope.launch { dragOffset.animateTo(0f) }
                                    },
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        coroutineScope.launch {
                                            dragOffset.snapTo(
                                                (dragOffset.value + dragAmount).coerceAtLeast(0f)
                                            )
                                        }
                                    }
                                )
                            }
                        )
                    }

                    // ── Content (NO drag gesture — scrollable content is free) ──
                    content()
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// CONVENIENCE - Preset configurations and common patterns
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SelectOptionBottomSheet - Sheet with Trigger and Content Pattern
 *
 * A bottom sheet that combines a trigger composable with sheet content.
 * Automatically manages visibility state.
 *
 * ## Usage Example
 * ```kotlin
 * SelectOptionBottomSheet(
 *     trigger = { show ->
 *         BaseButton(
 *             text = "Select Option",
 *             onClick = { show() }
 *         )
 *     },
 *     content = { dismiss ->
 *         Column {
 *             Text("Option 1", modifier = Modifier.clickable { dismiss() })
 *             Text("Option 2", modifier = Modifier.clickable { dismiss() })
 *         }
 *     }
 * )
 * ```
 *
 * @param trigger Composable that triggers the sheet (receives show function)
 * @param content Content of the sheet (receives dismiss function)
 * @param modifier Modifier for the trigger container
 * @param size Size variant
 * @param style Style variant
 * @param elevated Elevated surface
 * @param showDragHandle Show drag handle
 * @param isFixed If true, prevents dragging for sheets with scrollable content
 * @param errorMessage Optional error message below trigger
 * @param onDismiss Additional callback when dismissed
 */
@Composable
fun SelectOptionBottomSheet(
    trigger: @Composable (show: () -> Unit) -> Unit,
    content: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    size: BottomSheetSizeVariant = BottomSheetSizeVariant.Standard,
    style: BottomSheetStyle = BottomSheetStyle.Primary,
    elevated: Boolean = false,
    showDragHandle: Boolean = true,
    isFixed: Boolean = false,
    errorMessage: String? = null,
    onDismiss: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        trigger { isVisible = true }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = AppTheme.typography.captionBold,
                color = AppTheme.colors.errorContentDefault,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (isVisible) {
        PixaBottomSheet(
            onDismissRequest = {
                isVisible = false
                onDismiss()
            },
            size = size,
            style = style,
            elevated = elevated,
            showDragHandle = showDragHandle,
            isFixed = isFixed
        ) {
            content {
                isVisible = false
                onDismiss()
            }
        }
    }
}

/**
 * ExpandableBottomSheet - Sheet with Collapsed and Expanded States
 *
 * A bottom sheet that shows collapsed content initially and can expand to show more.
 *
 * @param collapsedContent Initial collapsed content (receives expand function)
 * @param expandedContent Full expanded content (receives dismiss function)
 * @param modifier Modifier for container
 * @param initiallyExpanded Start in expanded state
 * @param size Size variant for expanded state
 * @param style Style variant
 * @param elevated Elevated surface
 */
@Composable
fun ExpandableBottomSheet(
    collapsedContent: @Composable ColumnScope.(expand: () -> Unit) -> Unit,
    expandedContent: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
    size: BottomSheetSizeVariant = BottomSheetSizeVariant.Expanded,
    style: BottomSheetStyle = BottomSheetStyle.Primary,
    elevated: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    var isVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (!isVisible) {
            collapsedContent {
                isVisible = true
                isExpanded = true
            }
        }
    }

    if (isVisible) {
        PixaBottomSheet(
            onDismissRequest = {
                isVisible = false
                isExpanded = false
            },
            size = if (isExpanded) size else BottomSheetSizeVariant.Compact,
            style = style,
            elevated = elevated
        ) {
            expandedContent {
                isVisible = false
                isExpanded = false
            }
        }
    }
}

/**
 * ListBottomSheet - Sheet with List of Selectable Items
 *
 * A bottom sheet displaying a list of items for selection.
 *
 * @param title Title text for the sheet
 * @param items List of items to display
 * @param onItemSelected Callback when an item is selected
 * @param itemContent Composable for each item (receives item and select callback)
 * @param modifier Modifier for container
 * @param size Size variant
 * @param style Style variant
 * @param elevated Elevated surface
 * @param onDismiss Callback when sheet is dismissed
 */
@Composable
fun <T> ListBottomSheet(
    title: String,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    itemContent: @Composable (item: T, onSelect: (T) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    size: BottomSheetSizeVariant = BottomSheetSizeVariant.Standard,
    style: BottomSheetStyle = BottomSheetStyle.Primary,
    elevated: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(true) }

    if (isVisible) {
        PixaBottomSheet(
            onDismissRequest = {
                isVisible = false
                onDismiss()
            },
            size = size,
            style = style,
            elevated = elevated
        ) {
            Text(
                text = title,
                style = AppTheme.typography.titleBold,
                color = AppTheme.colors.baseContentTitle,
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Small)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AppTheme.colors.baseBorderSubtle.copy(alpha = 0.33f))
            )

            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
            ) {
                items(items) { item ->
                    itemContent(item) { selectedItem ->
                        onItemSelected(selectedItem)
                        isVisible = false
                    }
                }
            }
        }
    }
}

/**
 * ConfirmationBottomSheet - Sheet for Confirmation Dialogs
 *
 * A bottom sheet for confirming actions with title, message, and action buttons.
 *
 * @param title Title text
 * @param message Message/description text
 * @param confirmText Text for confirm button
 * @param cancelText Text for cancel button
 * @param onConfirm Callback when confirmed
 * @param onDismiss Callback when dismissed/canceled
 * @param modifier Modifier for container
 * @param isDestructive If true, uses destructive/error colors for confirm button
 * @param size Size variant
 * @param style Style variant
 */
@Composable
fun ConfirmationBottomSheet(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
    size: BottomSheetSizeVariant = BottomSheetSizeVariant.Compact,
    style: BottomSheetStyle = BottomSheetStyle.Primary
) {
    PixaBottomSheet(
        onDismissRequest = onDismiss,
        size = size,
        style = style,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = AppTheme.typography.titleBold,
            color = AppTheme.colors.baseContentTitle
        )

        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))

        Text(
            text = message,
            style = AppTheme.typography.bodyRegular,
            color = AppTheme.colors.baseContentBody
        )

        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)
        ) {
            PixaButton(
                text = cancelText,
                onClick = onDismiss,
                variant = ButtonVariant.Outlined,
                modifier = Modifier.weight(1f)
            )

            PixaButton(
                text = confirmText,
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                variant = ButtonVariant.Solid,
                isDestructive = isDestructive,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
