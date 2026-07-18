package com.pixamob.pixacompose.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.IconButtonColors
import com.pixamob.pixacompose.components.actions.IconButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.components.display.PixaIcon
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

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/** Semantic tint axis for dialog icon and border accenting. Purely visual; orthogonal to [DialogPresentation]. */
enum class DialogVariant {
    Default,
    Info,
    Success,
    Warning,
    Error
}

/**
 * Presentation mode. [Modal]: blocks background with scrim, input-blocking [Popup].
 * [NonModal]: no scrim, background stays interactive.
 */
enum class DialogPresentation {
    Modal,
    NonModal
}

/**
 * Wide-viewport (≥600dp) width tier. Ignored on narrow viewports (always bottom-docked, viewport − 32dp).
 */
enum class DialogWidth {
    XSmall, Small, Medium, Large
}

/** Maps [SizeVariant] → [DialogWidth] for the width ladder. */
fun SizeVariant.toDialogWidth(): DialogWidth = when (this) {
    SizeVariant.None, SizeVariant.Nano, SizeVariant.Compact -> DialogWidth.XSmall
    SizeVariant.Small -> DialogWidth.Small
    SizeVariant.Medium -> DialogWidth.Medium
    SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> DialogWidth.Large
}

/**
 * Wide-viewport docking position. Ignored on narrow viewports (always bottom-docked).
 * Positions: [Center], [TopLeft], [TopRight], [BottomLeft], [BottomRight] (40dp from edge).
 */
enum class DialogPosition {
    Center,
    TopLeft,
    TopRight,
    BottomLeft,
    BottomRight
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class DialogColors(
    val background: Color,
    val scrim: Color,
    val title: Color,
    val message: Color,
    val icon: Color,
    val border: Color
)

@Immutable
@Stable
private data class DialogSizeConfig(
    val padding: Dp,
    val iconSize: Dp,
    val titleStyle: TextStyle,
    val messageStyle: TextStyle,
    val cornerRadius: Dp,
    val elevation: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getDialogSizeConfig(width: DialogWidth): DialogSizeConfig {
    val typography = AppTheme.typography
    return when (width) {
        DialogWidth.XSmall -> DialogSizeConfig(
            padding = HierarchicalSize.Spacing.Large,
            iconSize = HierarchicalSize.Icon.Large,
            titleStyle = typography.subtitleBold,
            messageStyle = typography.bodyRegular,
            cornerRadius = HierarchicalSize.Radius.Medium,
            elevation = HierarchicalSize.Shadow.Large
        )
        DialogWidth.Small -> DialogSizeConfig(
            padding = HierarchicalSize.Spacing.Large,
            iconSize = HierarchicalSize.Icon.Large,
            titleStyle = typography.titleBold,
            messageStyle = typography.bodyRegular,
            cornerRadius = HierarchicalSize.Radius.Large,
            elevation = HierarchicalSize.Shadow.Huge
        )
        DialogWidth.Medium, DialogWidth.Large -> DialogSizeConfig(
            padding = HierarchicalSize.Spacing.Huge,
            iconSize = HierarchicalSize.Icon.Huge,
            titleStyle = typography.headlineBold,
            messageStyle = typography.bodyRegular,
            cornerRadius = HierarchicalSize.Radius.Huge,
            elevation = HierarchicalSize.Shadow.Massive
        )
    }
}

@Composable
private fun getDialogTheme(variant: DialogVariant): DialogColors {
    val colors = AppTheme.colors
    return when (variant) {
        DialogVariant.Default -> DialogColors(
            background = colors.baseSurfaceDefault,
            scrim = Color.Black.copy(alpha = 0.5f),
            title = colors.baseContentTitle,
            message = colors.baseContentBody,
            icon = colors.brandContentDefault,
            border = colors.baseBorderSubtle
        )
        DialogVariant.Info -> DialogColors(
            background = colors.baseSurfaceDefault,
            scrim = Color.Black.copy(alpha = 0.5f),
            title = colors.baseContentTitle,
            message = colors.baseContentBody,
            icon = colors.infoContentDefault,
            border = colors.infoBorderDefault
        )
        DialogVariant.Success -> DialogColors(
            background = colors.baseSurfaceDefault,
            scrim = Color.Black.copy(alpha = 0.5f),
            title = colors.baseContentTitle,
            message = colors.baseContentBody,
            icon = colors.successContentDefault,
            border = colors.successBorderDefault
        )
        DialogVariant.Warning -> DialogColors(
            background = colors.baseSurfaceDefault,
            scrim = Color.Black.copy(alpha = 0.5f),
            title = colors.baseContentTitle,
            message = colors.baseContentBody,
            icon = colors.warningContentDefault,
            border = colors.warningBorderDefault
        )
        DialogVariant.Error -> DialogColors(
            background = colors.baseSurfaceDefault,
            scrim = Color.Black.copy(alpha = 0.5f),
            title = colors.baseContentTitle,
            message = colors.baseContentBody,
            icon = colors.errorContentDefault,
            border = colors.errorBorderDefault
        )
    }
}

/** Resolves wide-viewport panel width from the 480/640/800 ladder, capped at viewport − 40dp. */
private fun resolveWideDialogWidth(width: DialogWidth, screenWidth: Dp): Dp {
    val target = when (width) {
        DialogWidth.XSmall -> 480.dp
        DialogWidth.Small -> 640.dp
        DialogWidth.Medium -> 800.dp
        DialogWidth.Large -> screenWidth - WideEdgeMargin
    }
    return target.coerceAtMost(screenWidth - WideEdgeMargin).coerceAtLeast(0.dp)
}

/** Wide viewport: 40dp edge margin and max-height cap. */
private val WideEdgeMargin = 40.dp

/** Narrow viewport: 16dp gutter on each edge (32dp total). */
private val NarrowEdgeMargin = 16.dp

/** Narrow viewports are always bottom-docked; wide viewports honor [position], defaulting to centered. */
private fun resolveDialogAlignment(windowSizeClass: WindowSizeClass, position: DialogPosition): Alignment =
    if (windowSizeClass == WindowSizeClass.Compact) {
        Alignment.BottomCenter
    } else {
        when (position) {
            DialogPosition.Center -> Alignment.Center
            DialogPosition.TopLeft -> Alignment.TopStart
            DialogPosition.TopRight -> Alignment.TopEnd
            DialogPosition.BottomLeft -> Alignment.BottomStart
            DialogPosition.BottomRight -> Alignment.BottomEnd
        }
    }

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL DIALOG
// ════════════════════════════════════════════════════════════════════════════

/** Enter: 16dp upward shift + fade (Quintic EaseOut, 400ms). */
@Composable
private fun dialogEnterTransition() = with(LocalDensity.current) {
    slideInVertically(
        initialOffsetY = { -16.dp.roundToPx() },
        animationSpec = AnimationUtils.standardTween(durationMillis = 400, easing = QuinticEaseOutEasing)
    ) + fadeIn(animationSpec = AnimationUtils.standardTween(durationMillis = MotionDuration.Instant, easing = LinearEasing))
}

/** Exit: 100ms linear fade. */
private val DialogExitTransition = fadeOut(
    animationSpec = AnimationUtils.standardTween(durationMillis = MotionDuration.Instant, easing = LinearEasing)
)

/**
 * Shake validation feedback: 9dp x-axis offset (50ms linear impulse + underdamped spring settle).
 */

private val DialogShakeImpulse = AnimationUtils.standardTween<Float>(durationMillis = 50, easing = LinearEasing)
private val DialogShakeSettle = AnimationUtils.standardSpring<Float>(dampingRatio = 0.22f, stiffness = 475f)

@Composable
private fun DialogCloseButton(
    onClick: () -> Unit,
    tint: Color,
    icon: Painter?,
    contentDescription: String
) {
    if (icon != null) {
        PixaIconButton(
            icon = icon,
            onClick = onClick,
            variant = IconButtonVariant.Ghost,
            size = SizeVariant.Small,
            colors = IconButtonColors(contentColor = tint),
            contentDescription = contentDescription
        )
    } else {
        Box(
            modifier = Modifier
                .size(HierarchicalSize.TouchTarget.Small)
                .clip(AppTheme.shapes.pill)
                .clickable(onClick = onClick)
                .semantics { this.contentDescription = contentDescription },
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = "×",
                modifier = Modifier.wrapContentSize(Alignment.Center, unbounded = true),
                style = AppTheme.typography.titleBold.copy(color = tint, textAlign = TextAlign.Center)
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * Dialog floating over another surface for single-step tasks or messages.
 *
 * ### Anatomy
 * Fixed header ([title] + optional X) → scrollable body ([artwork] → [message] → [content]) →
 * fixed button dock ([dismissText]/[confirmText]). Header and buttons stay fixed while body scrolls.
 *
 * ### Variants
 * [DialogVariant]: Default, Info, Success, Warning, Error (semantic tint).
 * [DialogPresentation]: Modal (scrim + blocking) or NonModal (no scrim).
 * [DialogWidth] / [DialogPosition]: wide-viewport size/docking.
 *
 * ### States & motion
 * Enter: 16dp shift (Quintic EaseOut, 400ms) + fade (100ms linear). Exit: 100ms linear fade.
 * [shakeTrigger] plays validation-error shake on increment.
 *
 * ### Sizing
 * [SizeVariant] → [DialogWidth] via [SizeVariant.toDialogWidth]. Narrow viewports: bottom-docked,
 * viewport − 32dp. Wide viewports: centered (or docked per [position]), 40dp from edge.
 *
 * ### Usage notes
 * Single-step tasks only. Prefer a sheet or full screen for long/complex content. Avoid stacking.
 * Validate errors via [shakeTrigger] rather than a second dialog.
 *
 * @param onDismissRequest Close callback (X, Cancel, ESC, outside click)
 * @param modifier Modifier for the dialog surface
 * @param variant Semantic tint (icon/border color accent)
 * @param presentation Modal (scrim) or NonModal (no scrim)
 * @param size Width tier → [DialogWidth]
 * @param position Wide-viewport docking; ignored on narrow
 * @param colors Custom color overrides
 * @param icon Optional icon above title
 * @param artwork Optional scrollable artwork slot
 * @param title Heading text
 * @param headingMaxLines Heading truncation (default: 2)
 * @param message Body text
 * @param confirmText Primary action button text
 * @param dismissText Secondary/cancel button text
 * @param onConfirm Primary action callback
 * @param onDismiss Secondary action callback (defaults to onDismissRequest)
 * @param dismissOnOutsideClick Overlay-tap dismissal (no effect in NonModal)
 * @param dismissOnBackClick ESC/back dismissal
 * @param showDismissIcon Show header X button (default: true)
 * @param dismissIcon Custom X icon painter
 * @param shakeTrigger Increment to trigger validation-error shake
 * @param content Custom scrollable body content after message
 */
@Composable
fun PixaDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    variant: DialogVariant = DialogVariant.Default,
    presentation: DialogPresentation = DialogPresentation.Modal,
    size: SizeVariant = SizeVariant.Medium,
    position: DialogPosition = DialogPosition.Center,
    colors: DialogColors? = null,
    icon: Painter? = null,
    artwork: (@Composable () -> Unit)? = null,
    title: String? = null,
    headingMaxLines: Int = 2,
    message: String? = null,
    confirmText: String? = null,
    dismissText: String? = null,
    onConfirm: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    dismissOnOutsideClick: Boolean = true,
    dismissOnBackClick: Boolean = true,
    showDismissIcon: Boolean = true,
    dismissIcon: Painter? = null,
    shakeTrigger: Int = 0,
    content: @Composable (() -> Unit)? = null
) {
    val dialogWidth = size.toDialogWidth()
    val sizeConfig = getDialogSizeConfig(dialogWidth)
    val themeColors = colors ?: getDialogTheme(variant)
    val windowSizeClass = AppTheme.windowSizeClass
    val screenWidth = ScreenUtil.getScreenWidth()
    val screenHeight = ScreenUtil.getScreenHeight()
    val isModal = presentation == DialogPresentation.Modal
    val alignment = resolveDialogAlignment(windowSizeClass, position)

    val panelWidthModifier = if (windowSizeClass == WindowSizeClass.Compact) {
        Modifier.width(screenWidth - NarrowEdgeMargin * 2)
    } else {
        Modifier.widthIn(max = resolveWideDialogWidth(dialogWidth, screenWidth))
    }
    val panelMaxHeight = if (windowSizeClass == WindowSizeClass.Compact) {
        screenHeight - NarrowEdgeMargin * 2
    } else {
        screenHeight - WideEdgeMargin
    }
    val edgeInset = if (windowSizeClass == WindowSizeClass.Compact || position == DialogPosition.Center) {
        0.dp
    } else {
        WideEdgeMargin
    }

    // Enter/exit lifecycle: starts hidden, flips true on mount (enter), flips false on
    // dismiss request (exit) — actual removal only happens once the exit animation finishes.
    val animVisibleState = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) { animVisibleState.targetState = true }
    val requestDismiss: () -> Unit = remember(onDismissRequest) { { animVisibleState.targetState = false } }
    LaunchedEffect(animVisibleState) {
        snapshotFlow { animVisibleState.isIdle && !animVisibleState.currentState }
            .collect { finished -> if (finished) onDismissRequest() }
    }

    // Shake: 9px impulse then a spring settle back to rest, replayed each time shakeTrigger changes.
    val density = LocalDensity.current
    val shakeOffsetPx = remember { Animatable(0f) }
    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger > 0) {
            val impulsePx = with(density) { 9.dp.toPx() }
            shakeOffsetPx.animateTo(impulsePx, DialogShakeImpulse)
            shakeOffsetPx.animateTo(0f, DialogShakeSettle)
        }
    }

    Popup(
        alignment = Alignment.Center,
        onDismissRequest = { if (dismissOnBackClick) requestDismiss() },
        properties = PopupProperties(
            focusable = isModal,
            dismissOnBackPress = dismissOnBackClick,
            dismissOnClickOutside = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isModal) {
                AnimatedVisibility(
                    visibleState = animVisibleState,
                    enter = fadeIn(animationSpec = AnimationUtils.standardTween(MotionDuration.Instant, easing = LinearEasing)),
                    exit = DialogExitTransition
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(themeColors.scrim)
                            .then(
                                if (dismissOnOutsideClick) {
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
            }

            AnimatedVisibility(
                visibleState = animVisibleState,
                enter = dialogEnterTransition(),
                exit = DialogExitTransition,
                modifier = Modifier.align(alignment).padding(edgeInset)
            ) {
                Column(
                    modifier = modifier
                        .then(panelWidthModifier)
                        .heightIn(max = panelMaxHeight)
                        .offset { IntOffset(shakeOffsetPx.value.roundToInt(), 0) }
                        .elevationShadow(sizeConfig.elevation, RoundedCornerShape(sizeConfig.cornerRadius))
                        .clip(RoundedCornerShape(sizeConfig.cornerRadius))
                        .background(themeColors.background)
                        .imePadding()
                        .semantics { contentDescription = title ?: "Dialog" }
                        .padding(sizeConfig.padding)
                ) {
                    if (title != null || showDismissIcon) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (icon != null) {
                                PixaIcon(painter = icon, contentDescription = null, tint = themeColors.icon, modifier = Modifier.size(sizeConfig.iconSize))
                                Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
                            }
                            if (title != null) {
                                BasicText(
                                    text = title,
                                    style = sizeConfig.titleStyle.copy(color = themeColors.title),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = headingMaxLines,
                                    modifier = Modifier.weight(1f).semantics { heading() }
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            if (showDismissIcon) {
                                Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
                                DialogCloseButton(
                                    onClick = requestDismiss,
                                    tint = themeColors.title,
                                    icon = dismissIcon,
                                    contentDescription = "Close ${title ?: "dialog"}"
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
                    }

                    Column(
                        modifier = Modifier
                            .weight(weight = 1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        artwork?.let {
                            it()
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                        }
                        if (message != null) {
                            BasicText(
                                text = message,
                                style = sizeConfig.messageStyle.copy(color = themeColors.message),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))
                        }
                        content?.invoke()
                    }

                    if (confirmText != null || dismissText != null) {
                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Large))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small, Alignment.End)
                        ) {
                            if (dismissText != null) {
                                PixaButton(text = dismissText, onClick = { onDismiss?.invoke() ?: requestDismiss() }, variant = ButtonVariant.Ghost, size = SizeVariant.Medium)
                            }
                            if (confirmText != null) {
                                PixaButton(text = confirmText, onClick = { onConfirm?.invoke(); requestDismiss() }, variant = ButtonVariant.Filled, isDestructive = variant == DialogVariant.Error, size = SizeVariant.Medium)
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

@Composable
fun PixaAlertDialog(onDismissRequest: () -> Unit, title: String, message: String, confirmText: String = "OK", variant: DialogVariant = DialogVariant.Default, icon: Painter? = null) {
    PixaDialog(onDismissRequest = onDismissRequest, variant = variant, icon = icon, title = title, message = message, confirmText = confirmText, onConfirm = onDismissRequest)
}

@Composable
fun PixaConfirmDialog(onDismissRequest: () -> Unit, title: String, message: String, confirmText: String = "Confirm", dismissText: String = "Cancel", onConfirm: () -> Unit, variant: DialogVariant = DialogVariant.Default, icon: Painter? = null) {
    PixaDialog(onDismissRequest = onDismissRequest, variant = variant, icon = icon, title = title, message = message, confirmText = confirmText, dismissText = dismissText, onConfirm = onConfirm, onDismiss = onDismissRequest)
}

@Composable
fun PixaDestructiveDialog(onDismissRequest: () -> Unit, title: String, message: String, confirmText: String = "Delete", dismissText: String = "Cancel", onConfirm: () -> Unit, icon: Painter? = null) {
    PixaDialog(onDismissRequest = onDismissRequest, variant = DialogVariant.Error, icon = icon, title = title, message = message, confirmText = confirmText, dismissText = dismissText, onConfirm = onConfirm, onDismiss = onDismissRequest)
}
