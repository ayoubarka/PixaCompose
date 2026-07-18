package com.pixamob.pixacompose.components.surfaces

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.IconButtonColors
import com.pixamob.pixacompose.components.actions.IconButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.MotionDuration
import com.pixamob.pixacompose.utils.QuinticEaseOutEasing
import com.pixamob.pixacompose.utils.ScreenUtil
import com.pixamob.pixacompose.utils.WindowSizeClass
import com.pixamob.pixacompose.utils.elevationShadow
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Full screen modal presentation types.
 *
 * [StackedSheet]: shows a visible edge of the previous context via [underlyingContent],
 * supports swipe-down-to-dismiss. [Immersive]: hides previous context entirely, dismissal
 * via button tap only (video/photo/camera/barcode use cases).
 */
enum class FullScreenModalPresentation {
    StackedSheet,
    Immersive
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class FullScreenModalColors(
    val background: Color,
    val scrim: Color,
    val title: Color,
    val border: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getFullScreenModalTheme(): FullScreenModalColors {
    val colors = AppTheme.colors
    return FullScreenModalColors(
        background = colors.baseSurfaceDefault,
        scrim = Color.Black.copy(alpha = 0.5f),
        title = colors.baseContentTitle,
        border = colors.baseBorderSubtle
    )
}

/** Wide-viewport corner radius (48dp). Narrow viewports render edge-to-edge with square corners. */
private val WideModalCornerRadius = 48.dp

/** Wide-viewport border width. */
private val WideModalBorderWidth = HierarchicalSize.Border.Huge

/** Wide-viewport max panel size — caps width so content determines actual height. */
private val WideModalMaxWidth = 816.dp

/** Wide-viewport edge margin (40dp). */
private val WideEdgeMargin = 40.dp

/** Drag threshold for Stacked Sheet swipe-down dismissal. */
private val SwipeDismissThreshold = 200.dp

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL FULL SCREEN MODAL
// ════════════════════════════════════════════════════════════════════════════

/** Enter: shift up + fade (same motion language as [com.pixamob.pixacompose.components.overlay.PixaDialog]). */
@Composable
private fun fullScreenModalEnterTransition() = with(LocalDensity.current) {
    slideInVertically(
        initialOffsetY = { 16.dp.roundToPx() },
        animationSpec = AnimationUtils.standardTween(durationMillis = MotionDuration.Slow, easing = QuinticEaseOutEasing)
    ) + fadeIn(animationSpec = AnimationUtils.standardTween(durationMillis = MotionDuration.Instant, easing = LinearEasing))
}

private val FullScreenModalExitTransition = fadeOut(
    animationSpec = AnimationUtils.standardTween(durationMillis = MotionDuration.Instant, easing = LinearEasing)
)

@Composable
private fun FullScreenModalHeader(
    title: String,
    headingMaxLines: Int,
    titleColor: Color,
    dismissIcon: Painter?,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (dismissIcon != null) {
            PixaIconButton(
                icon = dismissIcon,
                onClick = onDismiss,
                variant = IconButtonVariant.Ghost,
                size = SizeVariant.Small,
                colors = IconButtonColors(contentColor = titleColor),
                contentDescription = "Close $title"
            )
        } else {
            Box(
                modifier = Modifier
                    .size(HierarchicalSize.TouchTarget.Small)
                    .clip(AppTheme.shapes.pill)
                    .clickable(onClick = onDismiss)
                    .semantics { contentDescription = "Close $title" },
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = "×",
                    style = AppTheme.typography.titleBold.copy(color = titleColor)
                )
            }
        }
        Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
        BasicText(
            text = title,
            style = AppTheme.typography.titleBold.copy(color = titleColor),
            overflow = TextOverflow.Ellipsis,
            maxLines = headingMaxLines,
            modifier = Modifier.weight(1f).semantics { heading() }
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * Full-screen modal blocking experience for tasks or messages.
 *
 * ### Anatomy
 * Fixed header ([title] + required dismissal) → scrollable [content] → optional button dock
 * ([confirmText]/[dismissText]). Header/buttons stay fixed while body scrolls.
 *
 * ### Variants
 * [FullScreenModalPresentation.StackedSheet]: peeks previous context via [underlyingContent],
 * swipe-down-dismissible. [FullScreenModalPresentation.Immersive]: fully opaque, button-only dismiss.
 *
 * ### States & behavior
 * Stacked Sheet: swipe down from top strip or from scrolled-to-top body, plus dismissal button.
 * Immersive: [onConfirm] or dismissal button only — no swipe gesture.
 *
 * ### Sizing / Adaptive behavior
 * Narrow viewports (Compact): edge-to-edge, no radius/border/shadow.
 * Wide viewports (Medium/Expanded): centered panel with 48dp radius, 4dp border, 40dp margin.
 *
 * ### Usage notes
 * Gate [onDismissRequest] at call site for unsaved-work confirmation (component does not build
 * a nested dialog). Primary actions go in docked [confirmText] or inline [content] — no header
 * action slot.
 *
 * @param onDismissRequest Close callback (dismiss button, swipe, ESC)
 * @param title Required heading
 * @param presentation StackedSheet or Immersive
 * @param colors Custom color overrides
 * @param headingMaxLines Heading truncation (default: 2)
 * @param dismissIcon Custom dismiss icon painter (falls back to text glyph)
 * @param underlyingContent Previous-context snapshot for StackedSheet
 * @param confirmText Docked primary action text
 * @param dismissText Docked secondary/cancel text
 * @param onConfirm Primary action callback
 * @param onDismiss Secondary action callback (defaults to onDismissRequest)
 * @param dismissOnBackClick ESC/back dismissal
 * @param content Scrollable body content
 */
@Composable
fun PixaFullScreenModal(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    presentation: FullScreenModalPresentation = FullScreenModalPresentation.StackedSheet,
    colors: FullScreenModalColors? = null,
    headingMaxLines: Int = 2,
    dismissIcon: Painter? = null,
    underlyingContent: (@Composable () -> Unit)? = null,
    confirmText: String? = null,
    dismissText: String? = null,
    onConfirm: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    dismissOnBackClick: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val themeColors = colors ?: getFullScreenModalTheme()
    val windowSizeClass = AppTheme.windowSizeClass
    val screenWidth = ScreenUtil.getScreenWidth()
    val screenHeight = ScreenUtil.getScreenHeight()
    val isCompact = windowSizeClass == WindowSizeClass.Compact
    val isStackedSheet = presentation == FullScreenModalPresentation.StackedSheet
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val panelShape: Shape = if (isCompact) RoundedCornerShape(0.dp) else RoundedCornerShape(WideModalCornerRadius)
    val panelWidthModifier = if (isCompact) {
        Modifier.fillMaxSize()
    } else {
        Modifier.widthIn(max = WideModalMaxWidth.coerceAtMost(screenWidth - WideEdgeMargin * 2))
            .heightIn(max = screenHeight - WideEdgeMargin * 2)
    }

    val animVisibleState = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) { animVisibleState.targetState = true }
    val requestDismiss: () -> Unit = remember(onDismissRequest) { { animVisibleState.targetState = false } }
    LaunchedEffect(animVisibleState) {
        snapshotFlow { animVisibleState.isIdle && !animVisibleState.currentState }
            .collect { finished -> if (finished) onDismissRequest() }
    }

    // Swipe-down-to-dismiss (Stacked Sheet only): armed from the top strip always,
    // and from the scrollable body once it's scrolled to its top — mirrors
    // PixaBottomSheet's "drag gesture never fights inner scroll content" pattern.
    val scrollState = rememberScrollState()
    val dragOffsetPx = remember { Animatable(0f) }
    val dismissThresholdPx = with(density) { SwipeDismissThreshold.toPx() }

    fun onSwipeDragEnd() {
        coroutineScope.launch {
            if (dragOffsetPx.value > dismissThresholdPx) {
                requestDismiss()
            } else {
                dragOffsetPx.animateTo(0f, AnimationUtils.standardSpring())
            }
        }
    }

    val swipeDragModifier = if (isStackedSheet) {
        Modifier.pointerInput(Unit) {
            detectVerticalDragGestures(
                onDragEnd = { onSwipeDragEnd() },
                onDragCancel = { coroutineScope.launch { dragOffsetPx.animateTo(0f) } },
                onVerticalDrag = { change, dragAmount ->
                    if (scrollState.value == 0 && (dragOffsetPx.value > 0f || dragAmount > 0f)) {
                        change.consume()
                        coroutineScope.launch {
                            dragOffsetPx.snapTo((dragOffsetPx.value + dragAmount).coerceAtLeast(0f))
                        }
                    }
                }
            )
        }
    } else {
        Modifier
    }
    val topStripDragModifier = if (isStackedSheet) {
        Modifier.pointerInput(Unit) {
            detectVerticalDragGestures(
                onDragEnd = { onSwipeDragEnd() },
                onDragCancel = { coroutineScope.launch { dragOffsetPx.animateTo(0f) } },
                onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        dragOffsetPx.snapTo((dragOffsetPx.value + dragAmount).coerceAtLeast(0f))
                    }
                }
            )
        }
    } else {
        Modifier
    }

    Popup(
        alignment = Alignment.Center,
        onDismissRequest = { if (dismissOnBackClick) requestDismiss() },
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = dismissOnBackClick,
            dismissOnClickOutside = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isStackedSheet && underlyingContent != null) {
                underlyingContent()
            }

            AnimatedVisibility(
                visibleState = animVisibleState,
                enter = fadeIn(animationSpec = AnimationUtils.standardTween(MotionDuration.Instant, easing = LinearEasing)),
                exit = FullScreenModalExitTransition
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(themeColors.scrim)
                        .then(
                            if (!isCompact) {
                                Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { requestDismiss() }
                            } else {
                                Modifier
                            }
                        )
                )
            }

            AnimatedVisibility(
                visibleState = animVisibleState,
                enter = fullScreenModalEnterTransition(),
                exit = FullScreenModalExitTransition,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column(
                    modifier = modifier
                        .then(panelWidthModifier)
                        .offset { IntOffset(0, dragOffsetPx.value.roundToInt()) }
                        .then(if (isCompact) Modifier else Modifier.elevationShadow(HierarchicalSize.Shadow.Massive, panelShape))
                        .clip(panelShape)
                        .background(themeColors.background)
                        .then(
                            if (isCompact) Modifier else Modifier.border(WideModalBorderWidth, themeColors.border, panelShape)
                        )
                        .imePadding()
                        .semantics { contentDescription = title }
                        .padding(HierarchicalSize.Spacing.Large)
                        .then(swipeDragModifier)
                ) {
                    if (isStackedSheet) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(HierarchicalSize.Spacing.Large)
                                .then(topStripDragModifier)
                        )
                    }

                    FullScreenModalHeader(
                        title = title,
                        headingMaxLines = headingMaxLines,
                        titleColor = themeColors.title,
                        dismissIcon = dismissIcon,
                        onDismiss = { onDismiss?.invoke() ?: requestDismiss() }
                    )
                    Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))

                    Column(
                        modifier = Modifier
                            .weight(weight = 1f, fill = false)
                            .verticalScroll(scrollState)
                    ) {
                        content()
                    }

                    if (confirmText != null || dismissText != null) {
                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small, Alignment.End)
                        ) {
                            if (dismissText != null) {
                                PixaButton(
                                    text = dismissText,
                                    onClick = { onDismiss?.invoke() ?: requestDismiss() },
                                    variant = ButtonVariant.Ghost,
                                    size = SizeVariant.Medium
                                )
                            }
                            if (confirmText != null) {
                                PixaButton(
                                    text = confirmText,
                                    onClick = {
                                        onConfirm?.invoke()
                                        if (presentation == FullScreenModalPresentation.Immersive) requestDismiss()
                                    },
                                    variant = ButtonVariant.Filled,
                                    size = SizeVariant.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/** Preset for immersive tasks (video, photo, barcode, multi-step) — no underlying-context peek. */
@Composable
fun ImmersiveFullScreenModal(
    onDismissRequest: () -> Unit,
    title: String,
    confirmText: String? = null,
    onConfirm: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    PixaFullScreenModal(
        onDismissRequest = onDismissRequest,
        title = title,
        presentation = FullScreenModalPresentation.Immersive,
        confirmText = confirmText,
        onConfirm = onConfirm,
        content = content
    )
}

/** Preset for signup, forms, terms — [StackedSheet] with confirm/cancel action pair. */
@Composable
fun TaskFullScreenModal(
    onDismissRequest: () -> Unit,
    title: String,
    confirmText: String,
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    PixaFullScreenModal(
        onDismissRequest = onDismissRequest,
        title = title,
        presentation = FullScreenModalPresentation.StackedSheet,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm,
        onDismiss = onDismissRequest,
        content = content
    )
}
