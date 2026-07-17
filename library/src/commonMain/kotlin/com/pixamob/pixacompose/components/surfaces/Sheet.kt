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

/** Uber Base's two presentation modes — same axis [com.pixamob.pixacompose.components.overlay.PixaDialog]
 * already uses. [Modal] dims the background via a scrim and blocks its interaction ("suitable when
 * background context unnecessary"); [NonModal] renders no scrim, leaving the background genuinely
 * interactive ("useful when background reference is needed") — the sheet's core purpose per spec:
 * "enable simultaneous interaction with two surfaces." */
enum class SheetPresentation {
    Modal,
    NonModal
}

/** [Expandable] sheets show a [SheetGrabber], support the [SheetSnapPoint] ladder via swipe/tap-cycle,
 * and scroll their body. [Fixed] sheets have no grabber, cannot expand/collapse, and "hug" their content
 * at a single height instead of resolving to a snap point. */
enum class SheetExpandability {
    Expandable,
    Fixed
}

/**
 * Uber Base's three mobile snap-point states. [Expanded] resolves to the spec's default "Top" position
 * (60dp inset from the screen top) — the "Near Full"/"Full" expanded variants are explicitly flagged by
 * the spec as requiring "custom navigation header integration" and are out of scope here.
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

/** Spec: Collapsed sheets occupy "10-30% screen height" — the midpoint of that documented range. */
private const val CollapsedHeightFraction = 0.2f

/** Spec: Middle (default) sheets occupy "40-80% screen height" — the midpoint of that documented range. */
private const val MiddleHeightFraction = 0.6f

/** Spec: "Expanded (Top, default): 60px from status bar, background and nav visible." The "Near Full"
 * and "Full" expanded variants are explicitly flagged as requiring custom navigation integration and are
 * not modeled by [SheetSnapPoint]. */
private val ExpandedTopInset = 60.dp

/** Spec: "Side padding: 16px from screen edges" — matches [HierarchicalSize.Spacing.Large] exactly. */
private val SheetSidePadding = HierarchicalSize.Spacing.Large

/** Spec: "Header padding: Includes 8px buffer around action buttons" — matches [HierarchicalSize.Spacing.Small]. */
private val HeaderActionBuffer = HierarchicalSize.Spacing.Small

/** Spec gives no exact grabber width, only "typically 4-5px [thick]" — width is read from the existing
 * [com.pixamob.pixacompose.components.overlay.PixaBottomSheet] drag-handle precedent (32-48dp range);
 * 40dp is that range's midpoint, a one-off value with no dedicated token category to promote into. */
private val GrabberWidth = 40.dp
private val GrabberHeight = 4.dp

/** Spec: drop shadow "16px blur... rgba(0,0,0,0.12)" — matches [HierarchicalSize.Shadow.Massive] exactly,
 * the same tier [com.pixamob.pixacompose.components.surfaces.PixaFullScreenModal]'s equivalent spec shadow uses. */
private val SheetElevation = HierarchicalSize.Shadow.Massive

/** Spec: entry/exit duration is "400ms" for both directions. */
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

/** VoiceOver/TalkBack state label — spec: "Minimized/Half-screen/Full-screen" (iOS), "Half screen/
 * Expanded/Collapsed" (Android); this reads as the shared, platform-neutral vocabulary between the two. */
private fun SheetSnapPoint.stateLabel(): String = when (this) {
    SheetSnapPoint.Collapsed -> "Collapsed"
    SheetSnapPoint.Middle -> "Half-screen"
    SheetSnapPoint.Expanded -> "Expanded"
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL SHEET
// ════════════════════════════════════════════════════════════════════════════

/** Enter: "Slides in from viewport edge... Quintic ease-out... 400ms." Reuses the already-promoted
 * [QuinticEaseOutEasing] rather than adding a second near-identical bezier curve to [AnimationUtils] —
 * the spec's literal `cubic-bezier(0.23, 1, 0.32, 1)` and this library's existing true-power quintic-out
 * curve are visually indistinguishable "fast start, decelerating end" shapes. */
@Composable
private fun sheetEnterTransition() = slideInVertically(
    initialOffsetY = { it },
    animationSpec = AnimationUtils.standardTween(durationMillis = SheetAnimDurationMillis, easing = QuinticEaseOutEasing)
) + fadeIn(animationSpec = AnimationUtils.standardTween(durationMillis = SheetAnimDurationMillis, easing = QuinticEaseOutEasing))

/** Exit: "Quintic ease-in-and-out... 400ms" — reuses [QuinticEaseInOutEasing] for the same reason
 * [sheetEnterTransition] reuses [QuinticEaseOutEasing]. */
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
 * PixaSheet — a bottom-anchored surface that enables simultaneous interaction with two surfaces,
 * overlaying (or, in [SheetPresentation.NonModal], sitting alongside) a primary background surface.
 * Deliberately a new primitive under `components/surfaces/`
 * rather than a refactor of [com.pixamob.pixacompose.components.overlay.PixaBottomSheet] (kept
 * unchanged, per this migration's scope) — the two model different anatomies: [PixaBottomSheet] is a
 * simple slide-up container, while this component adds the spec's full header (grabber, title,
 * description, leading/trailing actions, progress-or-border) and multi-snap-point expand/collapse system.
 *
 * ### Anatomy
 * [SheetGrabber] (Expandable only) → [SheetHeader] (leading action → title/description → trailing
 * dismiss) → progress bar or border divider → scrollable [content].
 *
 * ### Variants
 * [presentation] (Modal/NonModal) and [expandability] (Expandable/Fixed) are independent axes, per spec.
 *
 * ### States
 * [SheetSnapPoint.Collapsed]/[SheetSnapPoint.Middle]/[SheetSnapPoint.Expanded] for [SheetExpandability.Expandable]
 * sheets — reachable by dragging the grabber (continuous drag, settles to the nearest snap point on
 * release) or tapping it (cycles Collapsed → Middle → Expanded → Collapsed, per spec). [SheetExpandability.Fixed]
 * sheets have no snap system; they hug [content] up to the Middle-tier height cap.
 *
 * ### Sizing / Adaptive behavior
 * Only the spec's narrow (<600dp) layout is implemented — full width, bottom-anchored, vertical
 * (Y-axis) motion. The ≥600dp side-anchored wide layout is explicitly marked "Not yet fully supported"
 * by Uber Base itself, so it is intentionally out of scope here rather than approximated.
 *
 * ### Customization
 * [progress]/[showProgress] map to the spec's Determinate ([progress] non-null)/Indeterminate
 * ([progress] null while [showProgress] is true) states, reusing [PixaLinearIndicator] rather than a
 * bespoke bar. When [showProgress] is false, a plain border divider renders instead, per spec: "Border —
 * displayed when no progress indicator active."
 *
 * ### Usage notes
 * [title] is non-nullable — spec: "Title mandatory (not optional)," required for W3C 2.4.2 compliance.
 * [trailingIcon]'s dismiss action is the spec's required primary dismissal ("X icon button... trailing
 * action, top right"); [dismissOnOutsideClick]/[dismissOnBackClick] are supplemental only, per spec's
 * anti-pattern guidance ("using passive/obscure dismiss methods as primary").
 *
 * @param onDismissRequest Callback when the sheet should close (X button, outside tap, or back/ESC)
 * @param title Heading — mandatory per spec
 * @param modifier Modifier for the sheet panel
 * @param presentation Modal (scrim, blocks background) or NonModal (no scrim, background interactive)
 * @param expandability Expandable (grabber + snap points) or Fixed (single height, no grabber)
 * @param initialSnapPoint Starting [SheetSnapPoint] for [SheetExpandability.Expandable] sheets (spec default: Middle)
 * @param colors Custom colors overriding the default theme
 * @param description Optional supplementary text below [title]
 * @param headingMaxLines Title truncation limit (spec default: 2 lines, configurable to 3)
 * @param descriptionMaxLines Description truncation limit (spec default: 2 lines, configurable to 3)
 * @param titleAlignment Header text alignment (spec default: Center; Start for left alignment)
 * @param leadingIcon Optional leading header action (e.g. back arrow); requires [onLeadingClick]
 * @param onLeadingClick Leading action callback
 * @param trailingIcon Optional custom painter for the required dismiss "X"; falls back to a text glyph when unset
 * @param showProgress Whether to render a progress bar in place of the header's border divider
 * @param progress Determinate progress (0f-1f) when non-null; indeterminate when null and [showProgress] is true
 * @param dismissOnOutsideClick Overlay-tap dismissal (supplemental only, per spec) — no effect when [presentation] is [SheetPresentation.NonModal]
 * @param dismissOnBackClick ESC/back dismissal (supplemental only, per spec)
 * @param onSnapPointChange Callback fired whenever the resolved [SheetSnapPoint] changes
 * @param content Scrollable body content, below the header
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

/** Preset for the spec's "quick-edit detail views," "location details" use cases — [SheetExpandability.Fixed],
 * no grabber, no snap points, a single hugging height. */
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

/** Preset for the spec's "filters/settings affecting main content" use case — starts at
 * [SheetSnapPoint.Middle], [SheetPresentation.NonModal] so the primary surface stays referenceable/interactive. */
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
