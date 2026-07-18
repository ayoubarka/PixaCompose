package com.pixamob.pixacompose.components.surfaces

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.components.actions.IconButtonColors
import com.pixamob.pixacompose.components.actions.IconButtonVariant
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.components.feedback.PixaLinearIndicator
import com.pixamob.pixacompose.components.feedback.ProgressVariant
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.QuinticEaseInOutEasing
import com.pixamob.pixacompose.utils.QuinticEaseOutEasing
import com.pixamob.pixacompose.utils.ScreenUtil
import com.pixamob.pixacompose.utils.elevationShadow
import kotlinx.coroutines.launch

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Presentation mode. [Modal]: dims background with scrim, blocks interaction.
 * [NonModal]: no scrim, background stays interactive — for simultaneous two-surface interaction.
 */
enum class SheetPresentation {
    Modal,
    NonModal
}

/**
 * [Expandable]: shows grabber, supports snap-point ladder via swipe/tap, scrollable body.
 * [Fixed]: no grabber, single hugging height, no expansion.
 */
enum class SheetExpandability {
    Expandable,
    Fixed
}

/**
 * Three mobile snap-point states. [Expanded] = 60dp from screen top.
 * Near-Full/Full variants requiring custom nav integration are out of scope.
 */
enum class SheetSnapPoint {
    Collapsed,
    Middle,
    Expanded
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class SheetColors(
    val background: Color,
    val scrim: Color,
    val title: Color,
    val description: Color,
    val grabber: Color,
    val border: Color,
    val actionTint: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getSheetTheme(): SheetColors {
    val colors = AppTheme.colors
    return SheetColors(
        background = colors.baseSurfaceDefault,
        scrim = Color.Black.copy(alpha = 0.5f),
        title = colors.baseContentTitle,
        description = colors.baseContentBody,
        grabber = colors.baseContentCaption.copy(alpha = 0.4f),
        border = colors.baseBorderSubtle,
        actionTint = colors.baseContentTitle
    )
}

/** Collapsed height: midpoint of 10-30% screen height range. */
private const val CollapsedHeightFraction = 0.2f

/** Middle height: midpoint of 40-80% screen height range. */
private const val MiddleHeightFraction = 0.6f

/** Expanded inset from screen top. Near-Full/Full variants not modeled. */
private val ExpandedTopInset = 60.dp

/** Side padding from screen edges. */
private val SheetSidePadding = HierarchicalSize.Spacing.Large

/** Header action button buffer. */
private val HeaderActionBuffer = HierarchicalSize.Spacing.Small

/** Grabber dimensions. */
private val GrabberWidth = 40.dp
private val GrabberHeight = 4.dp

/** Sheet elevation shadow. */
private val SheetElevation = HierarchicalSize.Shadow.Massive

/** Entry/exit animation duration. */
private const val SheetAnimDurationMillis = 400

@Composable
private fun heightFor(snapPoint: SheetSnapPoint, screenHeight: Dp): Dp = when (snapPoint) {
    SheetSnapPoint.Collapsed -> screenHeight * CollapsedHeightFraction
    SheetSnapPoint.Middle -> screenHeight * MiddleHeightFraction
    SheetSnapPoint.Expanded -> screenHeight - ExpandedTopInset
}

private fun nearestSnapPoint(heightPx: Float, collapsedPx: Float, middlePx: Float, expandedPx: Float): SheetSnapPoint {
    val distances = listOf(
        SheetSnapPoint.Collapsed to kotlin.math.abs(heightPx - collapsedPx),
        SheetSnapPoint.Middle to kotlin.math.abs(heightPx - middlePx),
        SheetSnapPoint.Expanded to kotlin.math.abs(heightPx - expandedPx)
    )
    return distances.minByOrNull { it.second }!!.first
}

private fun SheetSnapPoint.next(): SheetSnapPoint = when (this) {
    SheetSnapPoint.Collapsed -> SheetSnapPoint.Middle
    SheetSnapPoint.Middle -> SheetSnapPoint.Expanded
    SheetSnapPoint.Expanded -> SheetSnapPoint.Collapsed
}

/** Accessibility state label. */
private fun SheetSnapPoint.stateLabel(): String = when (this) {
    SheetSnapPoint.Collapsed -> "Collapsed"
    SheetSnapPoint.Middle -> "Half-screen"
    SheetSnapPoint.Expanded -> "Expanded"
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL SHEET
// ════════════════════════════════════════════════════════════════════════════

/** Enter: slide in from viewport edge + fade, Quintic ease-out, 400ms. */
@Composable
private fun sheetEnterTransition() = slideInVertically(
    initialOffsetY = { it },
    animationSpec = AnimationUtils.standardTween(durationMillis = SheetAnimDurationMillis, easing = QuinticEaseOutEasing)
) + fadeIn(animationSpec = AnimationUtils.standardTween(durationMillis = SheetAnimDurationMillis, easing = QuinticEaseOutEasing))

/** Exit: slide out + fade, Quintic ease-in-out, 400ms. */
private val SheetExitTransitionSpec = slideOutVertically(
    targetOffsetY = { it },
    animationSpec = AnimationUtils.standardTween(durationMillis = SheetAnimDurationMillis, easing = QuinticEaseInOutEasing)
) + fadeOut(animationSpec = AnimationUtils.standardTween(durationMillis = SheetAnimDurationMillis, easing = QuinticEaseInOutEasing))

@Composable
private fun SheetGrabber(
    color: Color,
    stateLabel: String,
    onTap: () -> Unit,
    dragModifier: Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = HierarchicalSize.TouchTarget.Small)
            .clickable(onClick = onTap)
            .then(dragModifier)
            .semantics {
                role = Role.Button
                contentDescription = "Sheet grabber"
                stateDescription = stateLabel
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(GrabberWidth)
                .height(GrabberHeight)
                .background(color, AppTheme.shapes.pill)
        )
    }
}

@Composable
private fun SheetHeader(
    title: String,
    description: String?,
    headingMaxLines: Int,
    descriptionMaxLines: Int,
    titleAlignment: TextAlign,
    themeColors: SheetColors,
    leadingIcon: Painter?,
    onLeadingClick: (() -> Unit)?,
    trailingIcon: Painter?,
    onDismiss: () -> Unit,
    showProgress: Boolean,
    progress: Float?
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = SheetSidePadding, vertical = HeaderActionBuffer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null && onLeadingClick != null) {
            PixaIconButton(
                icon = leadingIcon,
                onClick = onLeadingClick,
                variant = IconButtonVariant.Ghost,
                size = SizeVariant.Small,
                colors = IconButtonColors(contentColor = themeColors.actionTint),
                contentDescription = "Back"
            )
            Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = if (titleAlignment == TextAlign.Center) Alignment.CenterHorizontally else Alignment.Start
        ) {
            BasicText(
                text = title,
                style = AppTheme.typography.titleBold.copy(color = themeColors.title, textAlign = titleAlignment),
                overflow = TextOverflow.Ellipsis,
                maxLines = headingMaxLines,
                modifier = Modifier.fillMaxWidth().semantics { heading() }
            )
            if (description != null) {
                BasicText(
                    text = description,
                    style = AppTheme.typography.bodyRegular.copy(color = themeColors.description, textAlign = titleAlignment),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = descriptionMaxLines,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
        if (trailingIcon != null) {
            PixaIconButton(
                icon = trailingIcon,
                onClick = onDismiss,
                variant = IconButtonVariant.Ghost,
                size = SizeVariant.Small,
                colors = IconButtonColors(contentColor = themeColors.actionTint),
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
                BasicText(text = "×", style = AppTheme.typography.titleBold.copy(color = themeColors.actionTint))
            }
        }
    }

    // Progress bar OR border — spec: "Border — displayed when no progress indicator active."
    if (showProgress) {
        PixaLinearIndicator(
            progress = progress,
            variant = ProgressVariant.Primary,
            modifier = Modifier.padding(horizontal = SheetSidePadding)
        )
        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HierarchicalSize.Divider.Compact)
                .background(themeColors.border)
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * Bottom-anchored surface for simultaneous two-surface interaction.
 *
 * ### Anatomy
 * [SheetGrabber] (Expandable only) → [SheetHeader] (leading action → title/description →
 * trailing dismiss) → progress bar or border divider → scrollable [content].
 *
 * ### Variants
 * [SheetPresentation]: Modal (scrim) or NonModal (interactive background).
 * [SheetExpandability]: Expandable (grabber + snap points) or Fixed (single height).
 *
 * ### States
 * Expandable: [SheetSnapPoint.Collapsed] / [SheetSnapPoint.Middle] / [SheetSnapPoint.Expanded],
 * reachable by drag (settles to nearest) or tap (cycles Collapsed → Middle → Expanded → Collapsed).
 * Fixed: no snap system, hugs content up to Middle height cap.
 *
 * ### Sizing / Adaptive behavior
 * Narrow (<600dp) layout only: full-width, bottom-anchored. Wide side-anchored layout is out of scope.
 *
 * ### Usage notes
 * [title] is mandatory. [trailingIcon] is the primary dismissal; outside-click and back are
 * supplemental. Gate [onDismissRequest] at call site for unsaved-work confirmation.
 *
 * @param onDismissRequest Close callback (X, outside tap, ESC)
 * @param title Mandatory heading
 * @param modifier Modifier for the sheet surface
 * @param presentation Modal or NonModal
 * @param expandability Expandable (grabber + snap) or Fixed
 * @param initialSnapPoint Starting snap point (default: Middle)
 * @param colors Custom color overrides
 * @param description Optional text below title
 * @param headingMaxLines Title truncation (default: 2)
 * @param descriptionMaxLines Description truncation (default: 2)
 * @param titleAlignment Center (default) or Start
 * @param leadingIcon Optional leading action icon (requires [onLeadingClick])
 * @param onLeadingClick Leading action callback
 * @param trailingIcon Custom dismiss icon (falls back to text glyph)
 * @param showProgress Show progress bar instead of border divider
 * @param progress Determinate (0f-1f) or null for indeterminate
 * @param dismissOnOutsideClick Overlay-tap dismissal (no effect in NonModal)
 * @param dismissOnBackClick ESC/back dismissal
 * @param onSnapPointChange Snap point change callback
 * @param content Scrollable body content
 */
@Composable
fun PixaSheet(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    presentation: SheetPresentation = SheetPresentation.Modal,
    expandability: SheetExpandability = SheetExpandability.Expandable,
    initialSnapPoint: SheetSnapPoint = SheetSnapPoint.Middle,
    colors: SheetColors? = null,
    description: String? = null,
    headingMaxLines: Int = 2,
    descriptionMaxLines: Int = 2,
    titleAlignment: TextAlign = TextAlign.Center,
    leadingIcon: Painter? = null,
    onLeadingClick: (() -> Unit)? = null,
    trailingIcon: Painter? = null,
    showProgress: Boolean = false,
    progress: Float? = null,
    dismissOnOutsideClick: Boolean = true,
    dismissOnBackClick: Boolean = true,
    onSnapPointChange: ((SheetSnapPoint) -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val themeColors = colors ?: getSheetTheme()
    val isModal = presentation == SheetPresentation.Modal
    val isExpandable = expandability == SheetExpandability.Expandable
    val screenHeight = ScreenUtil.getScreenHeight()
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    val collapsedHeight = heightFor(SheetSnapPoint.Collapsed, screenHeight)
    val middleHeight = heightFor(SheetSnapPoint.Middle, screenHeight)
    val expandedHeight = heightFor(SheetSnapPoint.Expanded, screenHeight)
    val collapsedPx = with(density) { collapsedHeight.toPx() }
    val middlePx = with(density) { middleHeight.toPx() }
    val expandedPx = with(density) { expandedHeight.toPx() }

    var currentSnapPoint by remember { mutableStateOf(initialSnapPoint) }
    val sheetHeightPx = remember {
        Animatable(
            when (initialSnapPoint) {
                SheetSnapPoint.Collapsed -> collapsedPx
                SheetSnapPoint.Middle -> middlePx
                SheetSnapPoint.Expanded -> expandedPx
            }
        )
    }

    fun animateToSnapPoint(point: SheetSnapPoint) {
        currentSnapPoint = point
        onSnapPointChange?.invoke(point)
        val target = when (point) {
            SheetSnapPoint.Collapsed -> collapsedPx
            SheetSnapPoint.Middle -> middlePx
            SheetSnapPoint.Expanded -> expandedPx
        }
        coroutineScope.launch { sheetHeightPx.animateTo(target, AnimationUtils.selectionSpring) }
    }

    val animVisibleState = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) { animVisibleState.targetState = true }
    val requestDismiss: () -> Unit = remember(onDismissRequest) { { animVisibleState.targetState = false } }
    LaunchedEffect(animVisibleState) {
        snapshotFlow { animVisibleState.isIdle && !animVisibleState.currentState }
            .collect { finished -> if (finished) onDismissRequest() }
    }

    val grabberDragModifier = if (isExpandable) {
        Modifier.pointerInput(Unit) {
            detectVerticalDragGestures(
                onDragEnd = {
                    val nearest = nearestSnapPoint(sheetHeightPx.value, collapsedPx, middlePx, expandedPx)
                    animateToSnapPoint(nearest)
                },
                onDragCancel = { animateToSnapPoint(currentSnapPoint) },
                onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        sheetHeightPx.snapTo((sheetHeightPx.value - dragAmount).coerceIn(collapsedPx, expandedPx))
                    }
                }
            )
        }
    } else {
        Modifier
    }

    Popup(
        alignment = Alignment.BottomCenter,
        onDismissRequest = { if (dismissOnBackClick) requestDismiss() },
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = dismissOnBackClick,
            dismissOnClickOutside = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isModal) {
                AnimatedVisibility(
                    visibleState = animVisibleState,
                    enter = fadeIn(animationSpec = AnimationUtils.standardTween(SheetAnimDurationMillis, easing = LinearEasing)),
                    exit = fadeOut(animationSpec = AnimationUtils.standardTween(SheetAnimDurationMillis, easing = LinearEasing))
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
                enter = sheetEnterTransition(),
                exit = SheetExitTransitionSpec,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                val panelHeightModifier = if (isExpandable) {
                    Modifier.height(with(density) { sheetHeightPx.value.toDp() })
                } else {
                    Modifier.heightIn(max = middleHeight)
                }
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .then(panelHeightModifier)
                        .elevationShadow(SheetElevation, RoundedCornerShape(topStart = HierarchicalSize.Radius.Huge, topEnd = HierarchicalSize.Radius.Huge))
                        .clip(RoundedCornerShape(topStart = HierarchicalSize.Radius.Huge, topEnd = HierarchicalSize.Radius.Huge))
                        .background(themeColors.background)
                        .semantics { contentDescription = "$title, ${currentSnapPoint.stateLabel()}" }
                ) {
                    if (isExpandable) {
                        SheetGrabber(
                            color = themeColors.grabber,
                            stateLabel = currentSnapPoint.stateLabel(),
                            onTap = { animateToSnapPoint(currentSnapPoint.next()) },
                            dragModifier = grabberDragModifier
                        )
                    } else {
                        Spacer(modifier = Modifier.height(HeaderActionBuffer))
                    }

                    SheetHeader(
                        title = title,
                        description = description,
                        headingMaxLines = headingMaxLines,
                        descriptionMaxLines = descriptionMaxLines,
                        titleAlignment = titleAlignment,
                        themeColors = themeColors,
                        leadingIcon = leadingIcon,
                        onLeadingClick = onLeadingClick,
                        trailingIcon = trailingIcon,
                        onDismiss = requestDismiss,
                        showProgress = showProgress,
                        progress = progress
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .then(if (isExpandable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                            .padding(horizontal = SheetSidePadding)
                    ) {
                        content()
                    }
                    Spacer(modifier = Modifier.height(SheetSidePadding))
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/** Preset for detail views — Fixed, no grabber, single hugging height. */
@Composable
fun FixedDetailSheet(
    onDismissRequest: () -> Unit,
    title: String,
    description: String? = null,
    presentation: SheetPresentation = SheetPresentation.Modal,
    content: @Composable ColumnScope.() -> Unit
) {
    PixaSheet(
        onDismissRequest = onDismissRequest,
        title = title,
        description = description,
        presentation = presentation,
        expandability = SheetExpandability.Fixed,
        content = content
    )
}

/** Preset for filters/settings — starts at Middle, NonModal for background interactivity. */
@Composable
fun FilterSheet(
    onDismissRequest: () -> Unit,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    PixaSheet(
        onDismissRequest = onDismissRequest,
        title = title,
        presentation = SheetPresentation.NonModal,
        expandability = SheetExpandability.Expandable,
        initialSnapPoint = SheetSnapPoint.Middle,
        content = content
    )
}
