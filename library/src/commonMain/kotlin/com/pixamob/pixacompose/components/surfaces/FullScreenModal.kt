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
 * Uber Base's two Modal Full Screen presentation types.
 *
 * [StackedSheet] — "recommended on iOS": shows a visible edge of the previous context
 * behind the modal (via [PixaFullScreenModal]'s optional `underlyingContent` slot, which
 * models the spec's "snapshot of the previous context that was suspended"), and supports
 * swipe-down-to-dismiss. [Immersive] hides the previous context entirely — "use for video
 * viewing, photo capture, barcode scanning, or complex multi-step tasks" — and only
 * dismisses via an explicit button tap, never a gesture.
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

/**
 * Spec: "Border radius (48px)" is a fixed specification only on wide (windowed) viewports —
 * narrow viewports render genuinely edge-to-edge with square corners, so this constant is
 * never applied below [WindowSizeClass.Medium]. Not tokenized into [HierarchicalSize.Radius]
 * (whose ladder tops out at `Massive` = 24dp): the value is a one-off fixed constant pinned
 * by an external spec, not a Pixa-native rounding tier that other components should share.
 */
private val WideModalCornerRadius = 48.dp

/** Spec: "Border weight: 4px (outside align)" — matches [HierarchicalSize.Border.Huge]. */
private val WideModalBorderWidth = HierarchicalSize.Border.Huge

/** Spec: wide-viewport default panel size ("W: 816px; H: 445px"), read as a max-width/max-height
 * cap rather than a fixed box so real content can still determine actual height up to that cap. */
private val WideModalMaxWidth = 816.dp

/** Spec: wide viewports margin panels 40px from the screen edge — the same foundation value
 * [com.pixamob.pixacompose.components.overlay.DialogWidth]-based dialogs already use for their own wide-viewport edge inset. */
private val WideEdgeMargin = 40.dp

/** Pixels of downward drag required before a Stacked Sheet's swipe-down gesture commits to
 * dismissal rather than springing back — mirrors [com.pixamob.pixacompose.components.overlay.PixaBottomSheet]'s drag-to-dismiss threshold. */
private val SwipeDismissThreshold = 200.dp

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL FULL SCREEN MODAL
// ════════════════════════════════════════════════════════════════════════════

/** Enter: same "shift up + fade" motion language [com.pixamob.pixacompose.components.overlay.PixaDialog] uses for its own reveal —
 * a full screen modal is still a "surface entering," per [AnimationUtils]' motion taxonomy. */
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
 * PixaFullScreenModal — a blocking experience that takes over the entire screen, delivering
 * a message or helping users complete a task, then returning to the previous context when
 * completed.
 *
 * ### Anatomy
 * Fixed header ([title] + required dismissal button) → scrollable [content] → optional fixed
 * button dock ([confirmText]/[dismissText]), matching spec: "image scrolls away; heading/buttons
 * remain fixed when content exceeds max height." The dismissal button is never optional — per
 * spec's content model ("Dismissal button — always required") and anti-pattern list ("No
 * explicit dismissal button").
 *
 * ### Variants
 * [presentation] — [FullScreenModalPresentation.StackedSheet] (peeks the previous context via
 * [underlyingContent], swipe-down dismissible) or [FullScreenModalPresentation.Immersive]
 * (fully opaque, button-dismissal only).
 *
 * ### States & behavior
 * Stacked Sheet dismissal per spec: swiping down from the top of the screen (always available,
 * via an invisible top drag strip) or swiping down from anywhere once the content is scrolled
 * to its top (tracked via the body's scroll position), plus tapping the dismissal button.
 * Immersive dismissal per spec: tapping [onConfirm] or the dismissal button only — no swipe
 * gesture is attached.
 *
 * ### Sizing / Adaptive behavior
 * Narrow viewports ([WindowSizeClass.Compact]) render genuinely edge-to-edge — no radius,
 * border, margin, or shadow — matching the spec's iPhone (375×812) full-bleed reference.
 * Wide viewports ([WindowSizeClass.Medium]/[WindowSizeClass.Expanded]) render as a centered,
 * chrome'd panel capped at the spec's exact iPad/Tablet box (816×445 max, 48px radius, 4px
 * border, 16px-blur shadow, 40px edge margin) — the same narrow/wide split [com.pixamob.pixacompose.components.overlay.PixaDialog] and
 * [com.pixamob.pixacompose.components.overlay.PixaBottomSheet] already use, reusing [WindowSizeClass] rather than inventing a second
 * responsive system. [AppTheme.colors.baseSurfaceDefault] stands in for the spec's literal
 * `#FFFFFF` fill so the panel still themes correctly in dark mode — an intentional approximation.
 *
 * ### Customization
 * [underlyingContent] is a free-form slot for the "snapshot of the previous context" a Stacked
 * Sheet peeks behind — the caller owns what that snapshot looks like, since this library has
 * no navigation/routing layer to source it from automatically.
 *
 * ### Usage notes
 * Uber Base: "get confirmation before closing a modal when people could lose their work" — this
 * component does not build a second confirmation modal internally (would create a modal-in-modal
 * dependency this library's overlay family avoids); instead, gate the call to [onDismissRequest]
 * at the call site and show [com.pixamob.pixacompose.components.overlay.PixaConfirmDialog]/[com.pixamob.pixacompose.components.overlay.PixaDestructiveDialog] first when unsaved work
 * exists. Primary actions must stay in the docked button ([confirmText]) or inline within
 * [content] — per spec anti-pattern, this component deliberately has no header-trailing action
 * slot ("Top-right navigation bar placement" is explicitly a Don't).
 *
 * @param onDismissRequest Callback when the modal should close (dismiss button, swipe, or back/ESC)
 * @param title Heading — required by spec ("Always include descriptive title; avoid leaving off titles")
 * @param modifier Modifier for the modal panel
 * @param presentation [FullScreenModalPresentation.StackedSheet] (default) or [FullScreenModalPresentation.Immersive]
 * @param colors Custom colors overriding the default theme
 * @param headingMaxLines Heading truncation limit (spec default: 2 lines)
 * @param dismissIcon Optional custom painter for the dismissal icon; falls back to a text glyph when unset
 * @param underlyingContent Optional "previous context" snapshot peeked behind a [FullScreenModalPresentation.StackedSheet]
 * @param confirmText Docked primary action button text (spec: "docked floating placement at bottom")
 * @param dismissText Docked secondary/"Cancel"-style button text
 * @param onConfirm Primary action callback; also dismisses [FullScreenModalPresentation.Immersive] modals
 * @param onDismiss Secondary action callback (defaults to [onDismissRequest] when unset)
 * @param dismissOnBackClick ESC/back dismissal
 * @param content Scrollable body content — the modal's task or message
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

/** Preset for the spec's "video viewing, photo capture, barcode scanning, or complex
 * multi-step tasks" use case — [FullScreenModalPresentation.Immersive], no underlying-context peek. */
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

/** Preset for the spec's "signup or onboarding, forms, terms and conditions" use case —
 * [FullScreenModalPresentation.StackedSheet] with a docked confirm/cancel action pair. */
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
