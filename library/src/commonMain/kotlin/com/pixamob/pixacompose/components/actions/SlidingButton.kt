package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.feedback.PixaCircularIndicator
import com.pixamob.pixacompose.components.feedback.ProgressColors
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.elevationShadow
import com.pixamob.pixacompose.utils.toDp
import kotlinx.coroutines.launch

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Completion threshold, mapped from Uber Base's Low (Easy)/High (Hard) drag
 * thresholds. [Low] is the recommended default — Uber Base itself flags
 * [High] as difficult for users with physical disabilities and seniors, so
 * this is left an explicit opt-in rather than the default.
 */
enum class SlidingButtonThreshold {
    Low,
    High
}

/** Fraction of the track's drag distance required to auto-trigger [PixaSlidingButton.onSlideComplete]. */
internal val SlidingButtonThreshold.fraction: Float
    get() = when (this) {
        SlidingButtonThreshold.Low -> 0.2f  // Uber Base: "Complete >20% of drag distance"
        SlidingButtonThreshold.High -> 0.8f // Uber Base: "Complete >80% of drag distance"
    }

/**
 * Color variant, mapped from Uber Base's accessibility guidance restricting
 * Sliding Button to "primary, high-contrast colors like: InverseBackgroundPrimary,
 * backgroundAccent, backgroundNegative, backgroundPositive." Pixa's `brand`/`error`/
 * `success` color groups are the closest-named equivalents to Uber's
 * accent/negative/positive, each already paired as `<group>SurfaceDefault` +
 * `<group>ContentDefault` for guaranteed contrast — same convention
 * [ButtonVariant.Filled] uses.
 */
enum class SlidingButtonVariant {
    Brand,
    Success,
    Error
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class SlidingButtonColors(
    val trackBackground: Color,
    val trackBorder: Color,
    val thumbBackground: Color,
    val thumbContent: Color,
    val labelDefault: Color,
    val labelDragging: Color,
    val labelDisabled: Color,
    val focusBorder: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getSlidingButtonTheme(
    variant: SlidingButtonVariant,
    colors: ColorPalette
): SlidingButtonColors {
    val (surface, content) = when (variant) {
        SlidingButtonVariant.Brand -> colors.brandSurfaceDefault to colors.brandContentDefault
        SlidingButtonVariant.Success -> colors.successSurfaceDefault to colors.successContentDefault
        SlidingButtonVariant.Error -> colors.errorSurfaceDefault to colors.errorContentDefault
    }

    return SlidingButtonColors(
        trackBackground = surface,
        trackBorder = surface,
        thumbBackground = colors.baseSurfaceDefault,
        thumbContent = content,
        labelDefault = content,
        labelDragging = colors.baseContentDisabled, // "text dims to disabled color" while dragging
        labelDisabled = colors.baseContentDisabled,
        focusBorder = colors.brandBorderFocus
    )
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL SLIDING BUTTON
// ════════════════════════════════════════════════════════════════════════════

/**
 * Corner radius is Uber Base's single fixed 8dp value ([HierarchicalSize.Radius.Medium])
 * rather than [PixaButton]'s own per-[SizeVariant] radius ladder — Sliding
 * Button's spec pins one literal radius regardless of size tier ("Corner
 * radius: 8px"), it doesn't scale like Button's does.
 */
private val SlidingButtonShape: Shape
    @Composable get() = RoundedCornerShape(HierarchicalSize.Radius.Medium)

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaSlidingButton — a drag-to-confirm control that requires a deliberate
 * gesture before triggering a critical, hard-to-reverse action. Migrated
 * from Uber Base's Sliding Button spec.
 *
 * ### Anatomy
 * A full-width track (base label) plus a single draggable circular thumb
 * carrying one arrow icon — Uber Base is explicit that "the component does
 * not support any other icons" and no additional accessories, so unlike
 * [PixaButton] there are no leading/trailing icon slots on the label itself.
 *
 * ### Sizing
 * [size] resolves through [com.pixamob.pixacompose.components.actions.getButtonSizeConfig]
 * (made `internal`, not duplicated) — the same height/text-style ladder
 * [PixaButton] uses, satisfying Uber Base's "height consistency between
 * sliding button and base button is mandatory" and "avoid resizing elements
 * inside the button...try going up or down in size for the entire button
 * instead" rules structurally: there's no per-part size override in this API.
 *
 * ### States
 * Enabled, focus (3dp [HierarchicalSize.Border.Large] `brandBorderFocus`
 * outline), on-drag (label dims to `baseContentDisabled`), [loading] (spinner
 * replaces track content), disabled (reduced-opacity, non-interactive),
 * preloading ([showSkeleton] → [Skeleton]). [completed] is caller-driven —
 * same stateless-terminal-state precedent as [PixaButton]'s `selected`/
 * `PixaLink`'s `visited` — Compose has no notion of "this drag already
 * happened," so the host tracks it: `true` snaps/holds the thumb at the
 * track end, `false` resets it to the start.
 *
 * ### Behavior
 * Drag the thumb horizontally; crossing [threshold]'s fraction of the track
 * width invokes [onSlideComplete] exactly once and snaps the thumb to the
 * end. Releasing before the threshold springs the thumb back to the start
 * via [AnimationUtils.thumbSpring]. No manual confirmation step exists after
 * the threshold is crossed, matching Uber Base's "action triggers
 * automatically."
 *
 * ### Adaptive behavior
 * None — Uber Base explicitly discourages Sliding Button on desktop
 * ("trying to complete the task with a mouse or trackpad feels unnatural").
 * This is a content/platform-usage rule the host app should apply at the
 * call site (e.g. only rendering this on touch targets); it doesn't map to
 * [AppTheme.windowSizeClass]/`WindowSizeClass` since the issue is input
 * modality, not screen width, so no adaptive branching is added here.
 *
 * ### Customization
 * [variant] is restricted to [SlidingButtonVariant.Brand]/`Success`/`Error` —
 * Uber Base's own customization boundary limits Sliding Button to
 * high-contrast primary colors, so there is intentionally no Tonal/Outlined/
 * Ghost axis like [PixaButton] has ("don't use secondary or inverted button
 * styling"). [arrowIcon] is required, not optional — the arrow is
 * "non-negotiable" per the spec.
 *
 * ### Usage notes
 * - Reserve for the last step of critical/irreversible flows (trip
 *   completion, emergency calls, payments) — using it for non-critical
 *   actions adds friction without benefit (Uber Base's own caution).
 * - [threshold] defaults to [SlidingButtonThreshold.Low]; only reach for
 *   [SlidingButtonThreshold.High] when the extra precision is an intentional
 *   safeguard, given its accessibility cost.
 *
 * @param label Base track label text
 * @param arrowIcon Required single arrow icon rendered inside the thumb
 * @param onSlideComplete Invoked once when the drag crosses [threshold]
 * @param modifier Modifier for the track container
 * @param variant Color variant (Default: [SlidingButtonVariant.Brand])
 * @param threshold Drag-completion threshold (Default: [SlidingButtonThreshold.Low])
 * @param size Size variant, shared with [PixaButton]'s own ladder (Default: [SizeVariant.Medium])
 * @param enabled Whether the control is interactive (Default: true)
 * @param loading Whether to show the loading spinner in place of track content (Default: false)
 * @param completed Caller-driven terminal state — holds the thumb at the track end when true (Default: false)
 * @param showSkeleton Whether to render the preloading placeholder (Default: false)
 * @param description Accessibility description — VoiceOver/TalkBack should announce "{action}, Button" (Uber Base explicitly omits "Slide" terminology)
 * @param customColors Optional [SlidingButtonColors] override
 *
 * @sample
 * ```
 * PixaSlidingButton(
 *     label = "Slide to end trip",
 *     arrowIcon = painterResource(Res.drawable.ic_arrow_forward),
 *     onSlideComplete = { viewModel.endTrip() },
 *     description = "End trip"
 * )
 * ```
 */
@Composable
fun PixaSlidingButton(
    label: String,
    arrowIcon: Painter,
    onSlideComplete: () -> Unit,
    modifier: Modifier = Modifier,
    variant: SlidingButtonVariant = SlidingButtonVariant.Brand,
    threshold: SlidingButtonThreshold = SlidingButtonThreshold.Low,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    loading: Boolean = false,
    completed: Boolean = false,
    showSkeleton: Boolean = false,
    description: String? = null,
    customColors: SlidingButtonColors? = null,
) {
    val sizeConfig = getButtonSizeConfig(size)
    val shape = SlidingButtonShape

    if (showSkeleton) {
        Skeleton(
            modifier = modifier.fillMaxWidth().height(sizeConfig.height),
            height = sizeConfig.height,
            shape = shape,
            shimmerEnabled = true
        )
        return
    }

    val colors = customColors ?: getSlidingButtonTheme(variant, AppTheme.colors)
    val interactive = enabled && !loading && !completed
    val coroutineScope = rememberCoroutineScope()
    val dragFraction = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var hasTriggered by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(completed) {
        if (completed) {
            dragFraction.snapTo(1f)
        } else {
            hasTriggered = false
            dragFraction.animateTo(0f, AnimationUtils.thumbSpring)
        }
    }

    val trackBackground by animateColorAsState(
        targetValue = if (enabled) colors.trackBackground else colors.trackBackground.copy(alpha = 0.4f),
        animationSpec = AnimationUtils.standardTween(150),
        label = "sliding_button_track"
    )

    val labelColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.labelDisabled
            isDragging -> colors.labelDragging
            else -> colors.labelDefault
        },
        animationSpec = AnimationUtils.standardTween(150),
        label = "sliding_button_label"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(sizeConfig.height)
            .elevationShadow(
                elevation = ComponentElevation.Highest.toDp(), // closest ladder tier to Uber's 16px-blur spec (no exact token)
                shape = shape,
                clip = false,
                enabled = enabled
            )
            .clip(shape)
            .background(trackBackground)
            .border(HierarchicalSize.Border.Compact, colors.trackBorder, shape)
            .focusable(interactionSource = interactionSource, enabled = interactive)
            .then(
                if (isFocused && interactive) {
                    Modifier.border(HierarchicalSize.Border.Large, colors.focusBorder, shape)
                } else {
                    Modifier
                }
            )
            // Accessibility fallback: Uber Base recommends "standard button interaction"
            // for screen readers since the drag gesture itself demands precision that's
            // hard for VoiceOver/TalkBack users — a synthesized accessibility click lands
            // here (not on the nested thumb) and completes the action immediately.
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = interactive,
                role = Role.Button,
                onClickLabel = description
            ) {
                if (!hasTriggered) {
                    hasTriggered = true
                    coroutineScope.launch { dragFraction.animateTo(1f, AnimationUtils.thumbSpring) }
                    onSlideComplete()
                }
            }
            .padding(HierarchicalSize.Spacing.Nano),
        contentAlignment = Alignment.CenterStart
    ) {
        if (loading) {
            PixaCircularIndicator(
                progress = null,
                modifier = Modifier.size(sizeConfig.iconSize),
                sizePreset = SizeVariant.Small,
                customColors = ProgressColors(
                    progress = colors.thumbContent,
                    track = colors.thumbContent.copy(alpha = 0.2f),
                    label = colors.thumbContent
                )
            )
        } else {
            BasicText(
                text = label,
                style = sizeConfig.textStyle().copy(color = labelColor, textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth()
            )

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val thumbSize = sizeConfig.height - HierarchicalSize.Spacing.Nano * 2
                val trackWidthPx = constraints.maxWidth.toFloat()
                val thumbWidthPx = with(LocalDensity.current) { thumbSize.toPx() }
                val maxDragPx = (trackWidthPx - thumbWidthPx).coerceAtLeast(0f)

                Box(
                    modifier = Modifier
                        .offset { IntOffset((dragFraction.value * maxDragPx).toInt(), 0) }
                        .size(thumbSize)
                        .clip(AppTheme.shapes.pill)
                        .background(colors.thumbBackground)
                        .then(
                            if (interactive) {
                                Modifier.pointerInput(threshold, maxDragPx) {
                                    detectDragGestures(
                                        onDragStart = { isDragging = true },
                                        onDragEnd = {
                                            isDragging = false
                                            coroutineScope.launch {
                                                if (dragFraction.value >= threshold.fraction && !hasTriggered) {
                                                    hasTriggered = true
                                                    dragFraction.animateTo(1f, AnimationUtils.thumbSpring)
                                                    onSlideComplete()
                                                } else if (!hasTriggered) {
                                                    dragFraction.animateTo(0f, AnimationUtils.thumbSpring)
                                                }
                                            }
                                        },
                                        onDragCancel = {
                                            isDragging = false
                                            coroutineScope.launch {
                                                if (!hasTriggered) dragFraction.animateTo(0f, AnimationUtils.thumbSpring)
                                            }
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        if (maxDragPx > 0f) {
                                            coroutineScope.launch {
                                                val newFraction = (dragFraction.value + dragAmount.x / maxDragPx).coerceIn(0f, 1f)
                                                dragFraction.snapTo(newFraction)
                                            }
                                        }
                                    }
                                }
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PixaIcon(
                        painter = arrowIcon,
                        contentDescription = description,
                        customSize = sizeConfig.iconSize,
                        tint = colors.thumbContent
                    )
                }
            }
        }
    }
}
