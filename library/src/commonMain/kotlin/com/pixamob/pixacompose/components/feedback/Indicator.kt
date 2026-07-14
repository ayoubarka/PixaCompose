package com.pixamob.pixacompose.components.feedback

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.MotionDuration
import com.pixamob.pixacompose.utils.QuinticEaseInOutEasing
import com.pixamob.pixacompose.utils.QuinticEaseOutEasing
import kotlin.math.min
import kotlin.math.sin

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class ProgressVariant {
    Primary,
    Success,
    Warning,
    Error,
    Info,
    Neutral
}

/**
 * Progress bar orientation
 */
enum class ProgressOrientation {
    /** Horizontal progress bar */
    Horizontal,
    /** Vertical progress bar */
    Vertical
}

/**
 * Fill direction for determinate progress, mapped from Uber Base's Progress
 * Circle spec: "Clockwise: Forward progress communication" / "Counterclockwise:
 * Countdown timer context." [PixaProgressPill] has no literal rotation, so it
 * maps [CounterClockwise] onto the standard countdown-bar convention instead —
 * the fill depletes from the end (right) edge rather than growing from the
 * start (left) edge.
 */
enum class ProgressDirection {
    Clockwise,
    CounterClockwise
}

/**
 * Pager indicator style
 */
enum class PagerIndicatorStyle {
    /** Circular dots */
    Circle,
    /** Dash/line indicators */
    Dash
}

/**
 * Pager indicator width mode
 */
enum class PagerIndicatorWidthMode {
    /** All indicators have same width */
    Uniform,
    /** Selected indicator is wider than others */
    ExpandSelected
}

/**
 * Progress segment for multi-part progress bars
 */
@Immutable
@Stable
data class ProgressSegment(
    val progress: Float,
    val variant: ProgressVariant = ProgressVariant.Primary,
    val color: Color? = null,
    // Spec: "the active step loops a fill from left to right and fades
    // back" — marks the one currently-in-progress segment (as opposed to
    // already-complete or not-yet-started ones) so it gets that loop
    // animation instead of a static fill.
    val isActive: Boolean = false
)

/**
 * Progress indicator colors
 */
@Immutable
@Stable
data class ProgressColors(
    val progress: Color,
    val track: Color,
    val label: Color
)

/**
 * Progress configuration
 */
@Immutable
@Stable
data class ProgressConfig(
    val size: Dp,
    val strokeWidth: Dp,
    val trackOpacity: Float = 0.2f,
    val labelStyle: @Composable () -> TextStyle,
    val percentageFormat: String = "%d%%"
)

/**
 * Linear progress bar configuration — Uber Base's Progress Bar spec names 3
 * sizes (Small/Medium/Large), each pairing a track thickness with a label
 * text style (`LabelSmall`/`LabelMedium`/`LabelLarge`).
 */
@Immutable
@Stable
data class LinearProgressConfig(
    val trackThickness: Dp,
    val labelStyle: @Composable () -> TextStyle
)

/**
 * Pager indicator configuration
 */
@Immutable
@Stable
data class PagerIndicatorConfig(
    val indicatorSize: Dp = HierarchicalSize.Border.Nano,
    val selectedIndicatorSize: Dp = HierarchicalSize.Border.Nano,
    val spacing: Dp = HierarchicalSize.Border.Nano,
    val selectedColor: Color,
    val unselectedColor: Color,
    val cornerRadius: Dp = HierarchicalSize.Radius.Compact
)

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get progress colors based on variant
 */
@Composable
private fun getProgressColors(
    variant: ProgressVariant,
    colors: ColorPalette
): ProgressColors {
    return when (variant) {
        ProgressVariant.Primary -> ProgressColors(
            progress = colors.brandSurfaceDefault,
            track = colors.brandSurfaceDefault.copy(alpha = 0.2f),
            label = colors.baseContentBody
        )
        ProgressVariant.Success -> ProgressColors(
            progress = colors.successSurfaceDefault,
            track = colors.successSurfaceDefault.copy(alpha = 0.2f),
            label = colors.successContentDefault
        )
        ProgressVariant.Warning -> ProgressColors(
            progress = colors.warningSurfaceDefault,
            track = colors.warningSurfaceDefault.copy(alpha = 0.2f),
            label = colors.warningContentDefault
        )
        ProgressVariant.Error -> ProgressColors(
            progress = colors.errorSurfaceDefault,
            track = colors.errorSurfaceDefault.copy(alpha = 0.2f),
            label = colors.errorContentDefault
        )
        ProgressVariant.Info -> ProgressColors(
            progress = colors.infoSurfaceDefault,
            track = colors.infoSurfaceDefault.copy(alpha = 0.2f),
            label = colors.infoContentDefault
        )
        ProgressVariant.Neutral -> ProgressColors(
            progress = colors.baseSurfaceSubtle,
            track = colors.baseBorderSubtle,
            label = colors.baseContentBody
        )
    }
}

/**
 * Get progress configuration based on size. Uber Base's Progress Circle spec
 * names 4 sizes (Small/Medium/Large/X Large) — the branches below already
 * bucket the 8-tier [SizeVariant] down to that same 4-tier ladder
 * (Nano/Compact/Small→Small, Medium→Medium, Large/Huge→Large, Massive→X Large),
 * so no separate spec-specific enum is needed here (unlike, say,
 * [com.pixamob.pixacompose.components.display.ListItemDensity], which needed
 * one because its own generic-vs-spec tiers didn't already line up this
 * cleanly). [ProgressConfig.strokeWidth] is [HierarchicalSize.Border.Compact]
 * (1dp) in every branch, an exact match to the spec's literal "Border Style
 * (all sizes): Weight 1px" — replacing the previous [HierarchicalSize.Border.Nano]
 * (0.5dp) default. The spec's own pixel-dimension table (e.g. "286×118") reads
 * as full Figma-frame sizes including the below-circle label region, not the
 * ring's own diameter, so it isn't a usable literal source for [ProgressConfig.size]
 * — this keeps the existing icon-scale diameters (14/18/24/28dp) rather than
 * inventing an unjustified circle-diameter figure from an ambiguous table.
 */
@Composable
private fun getProgressConfig(size: SizeVariant): ProgressConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.Small, SizeVariant.Compact, SizeVariant.Nano -> ProgressConfig(
            size = HierarchicalSize.Icon.Compact,
            strokeWidth = HierarchicalSize.Border.Compact,
            labelStyle = { typography.footnoteBold }
        )
        SizeVariant.Medium -> ProgressConfig(
            size = HierarchicalSize.Icon.Small,
            strokeWidth = HierarchicalSize.Border.Compact,
            labelStyle = { typography.captionBold }
        )
        SizeVariant.Large, SizeVariant.Huge -> ProgressConfig(
            size = HierarchicalSize.Icon.Medium,
            strokeWidth = HierarchicalSize.Border.Compact,
            labelStyle = { typography.bodyBold }
        )
        SizeVariant.Massive -> ProgressConfig(
            size = HierarchicalSize.Icon.Large,
            strokeWidth = HierarchicalSize.Border.Compact,
            labelStyle = { typography.subtitleBold }
        )
        else -> ProgressConfig(
            size = HierarchicalSize.Icon.Small,
            strokeWidth = HierarchicalSize.Border.Compact,
            labelStyle = { typography.captionBold }
        )
    }
}

/**
 * Resolves [PixaProgressPill]'s track height + shape — Uber Base's
 * Determinate Pill anatomy ("pill-shaped track... label placed inside").
 * Height comes from [HierarchicalSize.Chip], the existing token category
 * already built for "a rounded container sized to hold inline text," a much
 * closer fit than the hairline-thin [HierarchicalSize.SliderTrack]/[HierarchicalSize.Border]
 * categories other progress components use for tracks with no inside label.
 */
@Composable
private fun getProgressPillHeight(size: SizeVariant): Dp = HierarchicalSize.Chip.forVariant(size)

/**
 * Resolves [LinearProgressConfig] for the bar's 3 spec sizes. Track thickness
 * comes from [HierarchicalSize.SliderTrack] — the existing token category
 * built for exactly this ("linear track thickness"), rather than the
 * previous default's border-width token ([HierarchicalSize.Border.Nano],
 * 0.5dp — an almost invisible hairline for a progress bar's own track).
 */
@Composable
private fun getLinearProgressConfig(size: SizeVariant): LinearProgressConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.None, SizeVariant.Nano, SizeVariant.Compact, SizeVariant.Small -> LinearProgressConfig(
            trackThickness = HierarchicalSize.SliderTrack.Small,
            labelStyle = { typography.labelSmall }
        )
        SizeVariant.Medium -> LinearProgressConfig(
            trackThickness = HierarchicalSize.SliderTrack.Medium,
            labelStyle = { typography.labelMedium }
        )
        SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> LinearProgressConfig(
            trackThickness = HierarchicalSize.SliderTrack.Large,
            labelStyle = { typography.labelLarge }
        )
    }
}

// ============================================================================
// CIRCULAR PROGRESS INDICATOR
// ============================================================================

/**
 * PixaCircularIndicator — a circular loading/progress ring. Migrated from
 * Uber Base's Progress Circle spec ("Circle Type").
 *
 * ### Purpose
 * Indicates status or completion of a process — open-ended (indeterminate,
 * short waits) or precise (determinate, long waits with a known duration).
 *
 * ### Anatomy
 * A circular track + a rotating (indeterminate) or filling (determinate)
 * ring, plus an optional label **below** the circle — Uber Base's literal
 * Circle Type anatomy, not a label overlaid inside the ring (the previous
 * implementation's placement, corrected here).
 *
 * ### Variants
 * Indeterminate (continuous spin, [progress] = null) vs Determinate
 * ([direction] = [ProgressDirection.CounterClockwise] for countdown timers).
 *
 * ### States
 * Active (animating) and Complete — pass [completedContent] to crossfade
 * into it once [completed] is true, using the spec's literal 500ms/0ms-delay/
 * Linear transition ([MotionDuration.Slow] + [LinearEasing]).
 *
 * ### Sizing
 * [sizePreset] buckets the 8-tier [SizeVariant] down to Uber Base's own
 * Small/Medium/Large/X Large ladder — see [getProgressConfig]. Stroke width
 * is a fixed 1dp ([HierarchicalSize.Border.Compact]) at every size, matching
 * the spec's literal "Border Style (all sizes): 1px."
 *
 * ### Customization
 * [variant] (color), [sizePreset], [direction], [showPercentage] (label
 * below the circle — no longer restricted to non-Small sizes now that it
 * lives below the ring instead of cramped inside it), [customColors],
 * [completedContent].
 *
 * ### Usage notes
 * - Switch from indeterminate to determinate once a wait-time estimate
 *   becomes available (Uber Base's "progressive enhancement").
 * - Placement communicates scope: centered on a screen = whole page loading;
 *   inside a sheet/button = that surface's own content loading.
 *
 * @param progress Progress value from 0.0 to 1.0 (null for indeterminate)
 * @param modifier Modifier for the indicator (sizes/positions the ring + label stack)
 * @param variant Color variant (Primary, Success, Warning, Error, Info, Neutral)
 * @param sizePreset Size preset (Default: Medium)
 * @param direction Fill direction for determinate progress (Default: Clockwise)
 * @param showPercentage Show a percentage label below the circle (determinate only)
 * @param completed Whether the Complete state is active — crossfades to [completedContent]
 * @param completedContent Content shown once [completed] is true (success message, result, etc.)
 * @param customColors Optional custom colors
 * @param contentDescription Accessibility description (defaults to Uber Base's literal VoiceOver wording, "Loading" / "Loading, N% complete")
 */
@Composable
fun PixaCircularIndicator(
    progress: Float? = null,
    modifier: Modifier = Modifier,
    variant: ProgressVariant = ProgressVariant.Primary,
    sizePreset: SizeVariant = SizeVariant.Medium,
    direction: ProgressDirection = ProgressDirection.Clockwise,
    showPercentage: Boolean = false,
    completed: Boolean = false,
    completedContent: (@Composable () -> Unit)? = null,
    customColors: ProgressColors? = null,
    contentDescription: String? = null
) {
    val colors = customColors ?: getProgressColors(variant, AppTheme.colors)
    val config = getProgressConfig(sizePreset)

    val isIndeterminate = progress == null
    val progressValue = progress?.coerceIn(0f, 1f) ?: 0f
    val directionSign = if (direction == ProgressDirection.Clockwise) 1f else -1f

    // Animate sweep angle for determinate progress
    val animatedSweepAngle by animateFloatAsState(
        targetValue = if (!isIndeterminate) 360f * progressValue else 0f,
        animationSpec = AnimationUtils.standardSpring(),
        label = "sweep_angle"
    )

    val description = contentDescription ?: if (isIndeterminate) {
        "Loading"
    } else {
        "Loading, ${(progressValue * 100).toInt()}% complete"
    }

    val indicator: @Composable () -> Unit = {
        Column(
            modifier = modifier
                .then(
                    if (!isIndeterminate) {
                        Modifier.progressSemantics(progressValue)
                    } else {
                        Modifier
                    }
                )
                .semantics {
                    this.contentDescription = description
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
        ) {
            Box(
                modifier = Modifier.size(config.size),
                contentAlignment = Alignment.Center
            ) {
                if (isIndeterminate) {
                    // Indeterminate circular animation
                    val infiniteTransition = rememberInfiniteTransition(label = "circular_progress")
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f * directionSign,
                        animationSpec = AnimationUtils.infiniteRepeatable(
                            animation = AnimationUtils.standardTween(1200, LinearEasing)
                        ),
                        label = "rotation"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidthPx = config.strokeWidth.toPx()
                        val diameter = min(size.width, size.height)
                        val radius = (diameter - strokeWidthPx) / 2f
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val topLeftX = (size.width - diameter) / 2f + strokeWidthPx / 2f
                        val topLeftY = (size.height - diameter) / 2f + strokeWidthPx / 2f
                        val arcSize = diameter - strokeWidthPx

                        // Track circle
                        drawCircle(
                            color = colors.track,
                            radius = radius,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                        )

                        // Progress arc (90 degrees)
                        drawArc(
                            color = colors.progress,
                            startAngle = rotation - 90f,
                            sweepAngle = 90f * directionSign,
                            useCenter = false,
                            topLeft = Offset(topLeftX, topLeftY),
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                        )
                    }
                } else {
                    // Determinate circular progress
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidthPx = config.strokeWidth.toPx()
                        val diameter = min(size.width, size.height)
                        val radius = (diameter - strokeWidthPx) / 2f
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val topLeftX = (size.width - diameter) / 2f + strokeWidthPx / 2f
                        val topLeftY = (size.height - diameter) / 2f + strokeWidthPx / 2f
                        val arcSize = diameter - strokeWidthPx

                        // Track circle
                        drawCircle(
                            color = colors.track,
                            radius = radius,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                        )

                        // Progress arc with animation
                        if (animatedSweepAngle > 0) {
                            drawArc(
                                color = colors.progress,
                                startAngle = -90f,
                                sweepAngle = animatedSweepAngle * directionSign,
                                useCenter = false,
                                topLeft = Offset(topLeftX, topLeftY),
                                size = Size(arcSize, arcSize),
                                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                            )
                        }
                    }
                }
            }

            // Optional label below the circle — Uber Base's literal Circle Type anatomy
            if (showPercentage && !isIndeterminate) {
                val percentage = (progressValue * 100).toInt()
                val percentageText = config.percentageFormat.replace("%d", percentage.toString())
                BasicText(
                    text = percentageText,
                    style = config.labelStyle().copy(
                        color = colors.label,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }

    if (completedContent != null) {
        Crossfade(
            targetState = completed,
            animationSpec = AnimationUtils.standardTween(MotionDuration.Slow, LinearEasing),
            label = "circular_progress_completion"
        ) { isCompleted ->
            if (isCompleted) completedContent() else indicator()
        }
    } else {
        indicator()
    }
}

// ============================================================================
// PROGRESS PILL
// ============================================================================

/**
 * PixaProgressPill — a pill-shaped determinate progress indicator with its
 * label inside the pill. Migrated from Uber Base's Progress Circle spec
 * ("Pill Type" / "Determinate Pill" variant) — a known-duration alternative
 * to [PixaCircularIndicator] for the same "long wait, measurable progress"
 * use case.
 *
 * ### Anatomy
 * A pill-shaped track ([AppTheme.shapes.pill]) + a fill that grows from the
 * start edge, with an optional label **centered inside the pill** — distinct
 * from [PixaCircularIndicator]'s label-below-the-circle anatomy, per Uber
 * Base's own "Circle Type" vs "Pill Type" anatomy split.
 *
 * ### States
 * Active (filling) and Complete — pass [completedContent] to crossfade into
 * it once [completed] is true, using the same literal 500ms/Linear timing as
 * [PixaCircularIndicator].
 *
 * ### Sizing
 * [sizePreset] resolves the pill's height via [HierarchicalSize.Chip] (a
 * rounded-container-sized-for-text token, not the hairline track thickness
 * [PixaLinearIndicator] uses — see [getProgressPillHeight]). Width fills the
 * available space by default; override via [modifier].
 *
 * ### Customization
 * [variant] (color), [sizePreset], [direction] (fill grows from the start
 * edge for [ProgressDirection.Clockwise], depletes from the end edge for
 * [ProgressDirection.CounterClockwise] — the standard countdown-bar
 * convention, since a pill has no literal rotation to reverse), [label]
 * override, [customColors], [completedContent].
 *
 * This is a determinate-only component — Uber Base defines no "Indeterminate
 * Pill" variant (only Indeterminate Circle, Determinate Circle, Determinate
 * Pill), so [progress] is non-nullable here, unlike [PixaCircularIndicator].
 *
 * @param progress Progress value from 0.0 to 1.0
 * @param modifier Modifier for the pill (defaults to filling available width)
 * @param variant Color variant (Primary, Success, Warning, Error, Info, Neutral)
 * @param sizePreset Size preset (Default: Medium)
 * @param direction Fill direction (Default: Clockwise — grows from the start edge)
 * @param label Custom label shown inside the pill (defaults to a percentage, e.g. "70%")
 * @param showLabel Whether to render a label inside the pill (Default: true)
 * @param completed Whether the Complete state is active — crossfades to [completedContent]
 * @param completedContent Content shown once [completed] is true
 * @param customColors Optional custom colors
 * @param contentDescription Accessibility description (defaults to Uber Base's literal VoiceOver wording, "Loading, N% complete")
 */
@Composable
fun PixaProgressPill(
    progress: Float,
    modifier: Modifier = Modifier,
    variant: ProgressVariant = ProgressVariant.Primary,
    sizePreset: SizeVariant = SizeVariant.Medium,
    direction: ProgressDirection = ProgressDirection.Clockwise,
    label: String? = null,
    showLabel: Boolean = true,
    completed: Boolean = false,
    completedContent: (@Composable () -> Unit)? = null,
    customColors: ProgressColors? = null,
    contentDescription: String? = null
) {
    val colors = customColors ?: getProgressColors(variant, AppTheme.colors)
    val config = getProgressConfig(sizePreset)
    val height = getProgressPillHeight(sizePreset)
    val shape = AppTheme.shapes.pill

    val progressValue = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressValue,
        animationSpec = AnimationUtils.standardSpring(),
        label = "pill_progress"
    )

    val percentage = (progressValue * 100).toInt()
    val displayLabel = label ?: "$percentage%"
    val description = contentDescription ?: "Loading, $percentage% complete"

    val indicator: @Composable () -> Unit = {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(colors.track)
                .border(HierarchicalSize.Border.Compact, AppTheme.colors.baseBorderSubtle, shape)
                .progressSemantics(progressValue)
                .semantics {
                    this.contentDescription = description
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .align(if (direction == ProgressDirection.Clockwise) Alignment.CenterStart else Alignment.CenterEnd)
                    .background(colors.progress, shape)
            )

            if (showLabel) {
                BasicText(
                    text = displayLabel,
                    style = config.labelStyle().copy(
                        color = colors.label,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }

    if (completedContent != null) {
        Crossfade(
            targetState = completed,
            animationSpec = AnimationUtils.standardTween(MotionDuration.Slow, LinearEasing),
            label = "progress_pill_completion"
        ) { isCompleted ->
            if (isCompleted) completedContent() else indicator()
        }
    } else {
        indicator()
    }
}

// ============================================================================
// LINEAR PROGRESS INDICATOR
// ============================================================================

/**
 * Linear Progress Indicator — migrated from Uber Base's Progress Bar spec.
 *
 * ### Anatomy
 * A linear background track + a progress indicator fill, with an optional
 * label below (or beside, for [ProgressOrientation.Vertical]).
 *
 * ### Sizing
 * [size] resolves track thickness + label style via [getLinearProgressConfig]
 * (Small/Medium/Large, per spec). [height] remains available as an explicit
 * override for a one-off exact thickness; when null (default) it derives
 * from [size]. Previously this defaulted to [HierarchicalSize.Border.Nano]
 * (0.5dp) — a border-width token misapplied as a bar thickness, rendering an
 * almost invisible hairline; that default is fixed here.
 *
 * ### Behavior
 * Determinate: fills from the leading edge proportionally to [progress].
 * Indeterminate: per spec, "pulses back and forth... quintic ease-in-and-out,
 * with opaque color moving center to sides on enter" — implemented as a
 * center-anchored fill that grows outward and back, not a one-directional
 * sweep.
 *
 * @param progress Progress value from 0.0 to 1.0 (null for indeterminate)
 * @param modifier Modifier for the indicator
 * @param variant Color variant
 * @param orientation Horizontal or Vertical
 * @param size Size tier (Default: Medium) — resolves track thickness + label style
 * @param height Optional exact track thickness override (width for vertical); null derives from [size]
 * @param showLabel Show text label
 * @param label Custom label text (shows percentage if not provided for determinate)
 * @param customColors Optional custom colors
 * @param contentDescription Accessibility description
 */
@Composable
fun PixaLinearIndicator(
    progress: Float? = null,
    modifier: Modifier = Modifier,
    variant: ProgressVariant = ProgressVariant.Primary,
    orientation: ProgressOrientation = ProgressOrientation.Horizontal,
    size: SizeVariant = SizeVariant.Medium,
    height: Dp? = null,
    showLabel: Boolean = false,
    label: String? = null,
    customColors: ProgressColors? = null,
    contentDescription: String? = null
) {
    val colors = customColors ?: getProgressColors(variant, AppTheme.colors)
    val config = getLinearProgressConfig(size)
    val trackThickness = height ?: config.trackThickness

    val isIndeterminate = progress == null
    val progressValue = progress?.coerceIn(0f, 1f) ?: 0f

    // Animate determinate progress
    val animatedProgress by animateFloatAsState(
        targetValue = if (!isIndeterminate) progressValue else 0f,
        animationSpec = AnimationUtils.standardSpring(),
        label = "linear_progress"
    )

    // Indeterminate: center-anchored pulse, growing outward and back —
    // spec's "opaque color moving center to sides," quintic ease-in-and-out,
    // back-and-forth (RepeatMode.Reverse, not Restart).
    val infiniteTransition = rememberInfiniteTransition(label = "indeterminate_linear_progress")
    val pulseFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = AnimationUtils.infiniteRepeatable(
            animation = AnimationUtils.standardTween(
                durationMillis = MotionDuration.Slow,
                easing = QuinticEaseInOutEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val description = contentDescription ?: if (isIndeterminate) {
        "Loading"
    } else {
        "Progress ${(progressValue * 100).toInt()} percent"
    }

    when (orientation) {
        ProgressOrientation.Horizontal -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .then(
                        if (!isIndeterminate) {
                            Modifier.progressSemantics(progressValue)
                        } else {
                            Modifier
                        }
                    )
                    .semantics {
                        this.contentDescription = description
                    }
            ) {
                // Label
                if (showLabel) {
                    val displayLabel = label ?: if (!isIndeterminate) {
                        "${(progressValue * 100).toInt()}%"
                    } else {
                        "Loading..."
                    }

                    BasicText(
                        text = displayLabel,
                        style = config.labelStyle().copy(color = colors.label),
                        modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Compact)
                    )
                }

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(trackThickness)
                        .clip(RoundedCornerShape(trackThickness / 2))
                        .background(colors.track),
                    contentAlignment = Alignment.Center
                ) {
                    if (isIndeterminate) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(pulseFraction)
                                .fillMaxHeight()
                                .background(colors.progress, RoundedCornerShape(trackThickness / 2))
                        )
                    } else {
                        // Determinate linear progress with animation
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .background(colors.progress, RoundedCornerShape(trackThickness / 2))
                        )
                    }
                }
            }
        }

        ProgressOrientation.Vertical -> {
            Row(
                modifier = modifier
                    .fillMaxHeight()
                    .then(
                        if (!isIndeterminate) {
                            Modifier.progressSemantics(progressValue)
                        } else {
                            Modifier
                        }
                    )
                    .semantics {
                        this.contentDescription = description
                    },
                horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
            ) {
                // Progress bar (vertical)
                Box(
                    modifier = Modifier
                        .width(trackThickness)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(trackThickness / 2))
                        .background(colors.track),
                    contentAlignment = Alignment.Center
                ) {
                    if (isIndeterminate) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(pulseFraction)
                                .background(colors.progress, RoundedCornerShape(trackThickness / 2))
                        )
                    } else {
                        // Determinate vertical progress with animation
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .fillMaxHeight(animatedProgress)
                                .background(colors.progress, RoundedCornerShape(trackThickness / 2))
                        )
                    }
                }

                // Label (vertical)
                if (showLabel) {
                    val displayLabel = label ?: if (!isIndeterminate) {
                        "${(progressValue * 100).toInt()}%"
                    } else {
                        "..."
                    }

                    BasicText(
                        text = displayLabel,
                        style = config.labelStyle().copy(color = colors.label)
                    )
                }
            }
        }
    }
}

// ============================================================================
// CONVENIENCE VARIANTS
// ============================================================================

/**
 * Loading Indicator - Indeterminate circular progress for loading states
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    sizePreset: SizeVariant = SizeVariant.Medium,
    variant: ProgressVariant = ProgressVariant.Primary
) {
    PixaCircularIndicator(
        progress = null,
        modifier = modifier,
        variant = variant,
        sizePreset = sizePreset
    )
}

/**
 * Progress Bar - Linear determinate progress with label.
 *
 * Per the spec's 3rd named state — "progress bar disappears when complete,
 * replaced by content... 500ms, linear" — pass [content] to crossfade into
 * it automatically once [progress] reaches 1f; omit it to keep the bar
 * visible indefinitely (previous behavior, unchanged).
 *
 * @param content Shown in place of the bar once [progress] reaches 1f, via a 500ms linear crossfade (spec-exact)
 */
@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    variant: ProgressVariant = ProgressVariant.Primary,
    label: String? = null,
    content: (@Composable () -> Unit)? = null
) {
    if (content == null) {
        PixaLinearIndicator(
            progress = progress,
            modifier = modifier,
            variant = variant,
            showLabel = true,
            label = label
        )
        return
    }

    Crossfade(
        targetState = progress >= 1f,
        modifier = modifier,
        animationSpec = AnimationUtils.standardTween(durationMillis = 500, easing = LinearEasing)
    ) { isComplete ->
        if (isComplete) {
            content()
        } else {
            PixaLinearIndicator(
                progress = progress,
                variant = variant,
                showLabel = true,
                label = label
            )
        }
    }
}

/**
 * Segmented Progress Indicator - Multi-part progress bar with different colors per segment.
 * Migrated from Uber Base's Progress Bar "Stepped/Segmented" variant.
 *
 * The segment with [ProgressSegment.isActive] set loops a left-to-right fill
 * that fades back (spec: "quintic ease-out, 500ms"); other segments render
 * as a static fill, matching "each progress step is filled when the task is
 * complete."
 *
 * @param segments List of progress segments with their own progress and color
 * @param modifier Modifier for the indicator
 * @param height Height of the progress bar
 * @param showLabel Show combined percentage label
 * @param contentDescription Accessibility description
 */
@Composable
fun SegmentedProgressIndicator(
    segments: List<ProgressSegment>,
    modifier: Modifier = Modifier,
    height: Dp = HierarchicalSize.SliderTrack.Medium,
    showLabel: Boolean = false,
    contentDescription: String? = null
) {
    val typography = AppTheme.typography
    val colors = AppTheme.colors

    val totalProgress = segments.sumOf { it.progress.toDouble() }.toFloat().coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .progressSemantics(totalProgress)
            .semantics {
                this.contentDescription = contentDescription ?: "Progress ${(totalProgress * 100).toInt()} percent"
            }
    ) {
        // Label
        if (showLabel) {
            BasicText(
                text = "${(totalProgress * 100).toInt()}%",
                style = typography.bodyRegular.copy(color = colors.baseContentBody),
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Compact)
            )
        }

        // Segmented progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(colors.baseBorderSubtle)
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                segments.forEach { segment ->
                    val segmentProgress = segment.progress.coerceIn(0f, 1f)
                    if (segmentProgress > 0f) {
                        val segmentColors = getProgressColors(segment.variant, colors)
                        val segmentColor = segment.color ?: segmentColors.progress

                        if (segment.isActive) {
                            ActiveSegmentFill(
                                modifier = Modifier
                                    .weight(segmentProgress)
                                    .fillMaxHeight(),
                                color = segmentColor,
                                shape = RoundedCornerShape(height / 2)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(segmentProgress)
                                    .fillMaxHeight()
                                    .background(segmentColor, RoundedCornerShape(height / 2))
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * The active/in-progress segment's loop animation: a fill sweeps left to
 * right and fades back, repeating — spec: "loops a fill from left to right
 * and fades back... quintic ease-out, 500ms."
 */
@Composable
private fun ActiveSegmentFill(
    modifier: Modifier,
    color: Color,
    shape: RoundedCornerShape
) {
    val infiniteTransition = rememberInfiniteTransition(label = "active_segment")
    val sweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = AnimationUtils.infiniteRepeatable(
            animation = AnimationUtils.standardTween(
                durationMillis = MotionDuration.Slow,
                easing = QuinticEaseOutEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )
    // Fades in at the start of the sweep, peaks mid-way, fades out at the
    // end — "fades back" without a separate reverse-phase animation.
    val fadeAlpha = sin(kotlin.math.PI * sweep).toFloat().coerceIn(0f, 1f)

    Box(modifier = modifier.background(color.copy(alpha = 0.3f), shape)) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(sweep)
                .fillMaxHeight()
                .background(color.copy(alpha = fadeAlpha), shape)
        )
    }
}

// ============================================================================
// PAGER INDICATOR
// ============================================================================

/**
 * Pager Indicator - Shows current page position in a pager/carousel
 *
 * Supports both circle and dash styles with customizable widths.
 * Selected indicator can be the same width as others or expanded.
 *
 * @param pageCount Total number of pages
 * @param currentPage Current page index (0-based)
 * @param modifier Modifier for the indicator
 * @param style Visual style (Circle or Dash)
 * @param widthMode Width mode (Uniform or ExpandSelected)
 * @param config Configuration for sizes and colors
 * @param contentDescription Accessibility description
 */
@Composable
fun PixaPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    style: PagerIndicatorStyle = PagerIndicatorStyle.Circle,
    widthMode: PagerIndicatorWidthMode = PagerIndicatorWidthMode.Uniform,
    config: PagerIndicatorConfig? = null,
    contentDescription: String? = null
) {
    val colors = AppTheme.colors
    val defaultConfig = PagerIndicatorConfig(
        indicatorSize = if (style == PagerIndicatorStyle.Dash) HierarchicalSize.Radius.Compact else HierarchicalSize.Border.Nano,
        selectedIndicatorSize = when {
            style == PagerIndicatorStyle.Dash && widthMode == PagerIndicatorWidthMode.ExpandSelected -> HierarchicalSize.Icon.Compact
            style == PagerIndicatorStyle.Dash -> HierarchicalSize.Icon.Nano
            else -> HierarchicalSize.Border.Nano
        },
        spacing = HierarchicalSize.Border.Nano,
        selectedColor = colors.brandContentDefault,
        unselectedColor = colors.baseBorderDefault,
        cornerRadius = if (style == PagerIndicatorStyle.Dash) HierarchicalSize.Radius.Nano else HierarchicalSize.Radius.Compact
    )

    val indicatorConfig = config ?: defaultConfig
    val safeCurrentPage = currentPage.coerceIn(0, pageCount - 1)

    Row(
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription ?: "Page ${safeCurrentPage + 1} of $pageCount"
            },
        horizontalArrangement = Arrangement.spacedBy(indicatorConfig.spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == safeCurrentPage

            // Animate color transition
            val indicatorColor by animateColorAsState(
                targetValue = if (isSelected) indicatorConfig.selectedColor else indicatorConfig.unselectedColor,
                animationSpec = AnimationUtils.standardTween(),
                label = "indicator_color_$index"
            )

            // Determine size based on style and mode
            val indicatorWidth = when {
                style == PagerIndicatorStyle.Circle -> indicatorConfig.indicatorSize
                widthMode == PagerIndicatorWidthMode.ExpandSelected && isSelected -> indicatorConfig.selectedIndicatorSize
                widthMode == PagerIndicatorWidthMode.ExpandSelected -> indicatorConfig.indicatorSize
                else -> indicatorConfig.selectedIndicatorSize
            }

            // Animate size transition
            val animatedWidth by animateDpAsState(
                targetValue = indicatorWidth,
                animationSpec = AnimationUtils.standardSpring(),
                label = "indicator_width_$index"
            )

            // Shape based on style
            val shape = when (style) {
                PagerIndicatorStyle.Circle -> CircleShape
                PagerIndicatorStyle.Dash -> RoundedCornerShape(indicatorConfig.cornerRadius)
            }

            Box(
                modifier = Modifier
                    .width(animatedWidth)
                    .height(indicatorConfig.indicatorSize)
                    .background(
                        color = indicatorColor,
                        shape = shape
                    )
            )
        }
    }
}

/**
 * Simple Pager Indicator - Convenience function with default settings
 */
@Composable
fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    style: PagerIndicatorStyle = PagerIndicatorStyle.Circle
) {
    PixaPagerIndicator(
        pageCount = pageCount,
        currentPage = currentPage,
        modifier = modifier,
        style = style
    )
}

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * 1. Simple loading indicator:
 * ```
 * LoadingIndicator()
 * ```
 *
 * 2. Circular progress with percentage:
 * ```
 * CircularProgressIndicator(
 *     progress = downloadProgress,
 *     showPercentage = true,
 *     variant = ProgressVariant.Success,
 *     sizePreset = SizeVariant.Large
 * )
 * ```
 *
 * 3. Linear progress bar:
 * ```
 * ProgressBar(
 *     progress = uploadProgress,
 *     label = "Uploading files...",
 *     variant = ProgressVariant.Info
 * )
 * ```
 *
 * 4. Vertical progress indicator:
 * ```
 * LinearProgressIndicator(
 *     progress = volumeLevel,
 *     orientation = ProgressOrientation.Vertical,
 *     variant = ProgressVariant.Primary,
 *     height = 6.dp,
 *     modifier = Modifier.height(200.dp)
 * )
 * ```
 *
 * 5. Error progress indicator:
 * ```
 * LinearProgressIndicator(
 *     progress = 0.75f,
 *     variant = ProgressVariant.Error,
 *     showLabel = true,
 *     label = "Upload failed at 75%"
 * )
 * ```
 *
 * 6. Segmented progress (multiple parts):
 * ```
 * SegmentedProgressIndicator(
 *     segments = listOf(
 *         ProgressSegment(0.3f, ProgressVariant.Success),
 *         ProgressSegment(0.2f, ProgressVariant.Warning),
 *         ProgressSegment(0.15f, ProgressVariant.Error)
 *     ),
 *     showLabel = true,
 *     height = 8.dp
 * )
 * ```
 *
 * 7. Multi-step progress:
 * ```
 * Column {
 *     Text("Step ${currentStep + 1} of ${totalSteps}")
 *     LinearProgressIndicator(
 *         progress = (currentStep + 1).toFloat() / totalSteps,
 *         variant = ProgressVariant.Primary,
 *         height = 8.dp
 * }
 * ```
 *
 * 8. Custom colors:
 * ```
 * CircularProgressIndicator(
 *     progress = 0.65f,
 *     customColors = ProgressColors(
 *         progress = Color.Magenta,
 *         track = Color.Magenta.copy(alpha = 0.2f),
 *         label = Color.Black
 *     ),
 *     showPercentage = true
 * )
 * ```
 *
 * 9. Animated loading with auto-dismiss:
 * ```
 * var isLoading by remember { mutableStateOf(true) }
 *
 * LaunchedEffect(Unit) {
 *     delay(3000)
 *     isLoading = false
 * }
 *
 * if (isLoading) {
 *     LoadingIndicator(
 *         sizePreset = SizeVariant.Large,
 *         variant = ProgressVariant.Primary
 *     )
 * }
 * ```
 *
 * 10. Pager indicator (circle style):
 * ```
 * PagerIndicator(
 *     pageCount = 5,
 *     currentPage = currentPageIndex,
 *     style = PagerIndicatorStyle.Circle
 * )
 * ```
 *
 * 11. Pager indicator (dash style with uniform width):
 * ```
 * PixaPagerIndicator(
 *     pageCount = 4,
 *     currentPage = currentPageIndex,
 *     style = PagerIndicatorStyle.Dash,
 *     widthMode = PagerIndicatorWidthMode.Uniform
 * )
 * ```
 *
 * 12. Pager indicator (dash style with expanded selected):
 * ```
 * PixaPagerIndicator(
 *     pageCount = 3,
 *     currentPage = currentPageIndex,
 *     style = PagerIndicatorStyle.Dash,
 *     widthMode = PagerIndicatorWidthMode.ExpandSelected
 * )
 * ```
 *
 * 13. Custom pager indicator colors:
 * ```
 * PixaPagerIndicator(
 *     pageCount = 5,
 *     currentPage = currentPageIndex,
 *     style = PagerIndicatorStyle.Circle,
 *     config = PagerIndicatorConfig(
 *         indicatorSize = 10.dp,
 *         spacing = 12.dp,
 *         selectedColor = Color.Blue,
 *         unselectedColor = Color.Gray.copy(alpha = 0.4f)
 *     )
 * )
 * ```
 */
