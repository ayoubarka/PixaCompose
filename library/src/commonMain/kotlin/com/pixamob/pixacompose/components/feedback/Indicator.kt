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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.semantics.stateDescription
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
import com.pixamob.pixacompose.utils.elevationShadow
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
 * Fill direction for determinate progress. [Clockwise] grows from the leading
 * edge; [CounterClockwise] depletes (countdown convention).
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
 * Pager indicator color schemes. [Default] and [Inverse] map to theme-relative
 * tokens. [AlwaysDark]/[AlwaysLight] approximate theme-invariant colors using
 * the closest theme-relative base tokens.
 */
enum class PageControlColorScheme {
    /** Selected: theme's primary content tone. Unselected: theme's neutral border/surface tone. */
    Default,
    /** For dark/colored backdrops: selected/unselected swap toward the theme's lighter tones. */
    Inverse,
    /** Approximates a theme-invariant "always dark backdrop" scheme. */
    AlwaysDark,
    /** Approximates a theme-invariant "always light backdrop" scheme. */
    AlwaysLight
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
    // Marks the currently-in-progress segment for loop animation.
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
 * Linear progress bar configuration — 3 sizes, each with track thickness and label style.
 */
@Immutable
@Stable
data class LinearProgressConfig(
    val trackThickness: Dp,
    val labelStyle: @Composable () -> TextStyle
)

/**
 * Pager indicator configuration. [indicatorSize] reads from [HierarchicalSize.Badge].
 * [spacing] defaults to [HierarchicalSize.Spacing.Small].
 */
@Immutable
@Stable
data class PagerIndicatorConfig(
    val indicatorSize: Dp = HierarchicalSize.Badge.Nano,
    val selectedIndicatorSize: Dp = HierarchicalSize.Badge.Nano,
    val spacing: Dp = HierarchicalSize.Spacing.Small,
    val selectedColor: Color,
    val unselectedColor: Color,
    val cornerRadius: Dp = HierarchicalSize.Radius.Compact
)

/** Colors resolved for a [PageControlColorScheme]. */
@Immutable
@Stable
data class PageControlColors(
    val selected: Color,
    val unselected: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

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
 * Resolves [ProgressConfig] from [SizeVariant]. Stroke width is always
 * [HierarchicalSize.Border.Compact] (1dp). Circle diameters use icon-scale
 * tokens (14/18/24/28dp).
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
 * Resolves [PixaProgressPill] height via [HierarchicalSize.Chip].
 */
@Composable
private fun getProgressPillHeight(size: SizeVariant): Dp = HierarchicalSize.Chip.forVariant(size)

/**
 * Resolves [LinearProgressConfig] — track thickness from [HierarchicalSize.SliderTrack].
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

/**
 * Resolves [PageControlColors] for a [PageControlColorScheme]. Disabled state
 * overrides all schemes with neutral disabled tokens.
 */
@Composable
private fun getPageControlColors(scheme: PageControlColorScheme, enabled: Boolean): PageControlColors {
    val colors = AppTheme.colors
    if (!enabled) {
        return PageControlColors(selected = colors.baseContentDisabled, unselected = colors.baseSurfaceDisabled)
    }
    return when (scheme) {
        PageControlColorScheme.Default -> PageControlColors(
            selected = colors.baseContentTitle,
            unselected = colors.baseBorderDefault
        )
        PageControlColorScheme.Inverse -> PageControlColors(
            selected = colors.baseSurfaceDefault,
            unselected = colors.baseSurfaceDefault.copy(alpha = 0.4f)
        )
        PageControlColorScheme.AlwaysDark -> PageControlColors(
            selected = colors.baseSurfaceDefault,
            unselected = colors.baseSurfaceDefault.copy(alpha = 0.24f)
        )
        PageControlColorScheme.AlwaysLight -> PageControlColors(
            selected = colors.baseContentTitle,
            unselected = colors.baseContentTitle.copy(alpha = 0.12f)
        )
    }
}

/**
 * Windows large page counts down to a visible dot list. Below 6 pages, every
 * dot renders full-scale. At/above 6, a [CoreWindowRadius]-dot core slides
 * with [currentPage] and freezes at edges, with [PeekScale]-scaled peek dots
 * on each side when additional pages exist beyond the core.
 *
 * @return list of (page index, render scale) pairs in left-to-right order
 */
private fun windowedPageControlDots(pageCount: Int, currentPage: Int): List<Pair<Int, Float>> {
    if (pageCount <= AdaptiveSizingThreshold) {
        return (0 until pageCount).map { it to 1f }
    }
    val center = currentPage.coerceIn(CoreWindowRadius, pageCount - 1 - CoreWindowRadius)
    val coreStart = center - CoreWindowRadius
    val coreEnd = center + CoreWindowRadius
    val dots = mutableListOf<Pair<Int, Float>>()
    if (coreStart - 1 >= 0) dots.add((coreStart - 1) to PeekScale)
    for (index in coreStart..coreEnd) dots.add(index to 1f)
    if (coreEnd + 1 <= pageCount - 1) dots.add((coreEnd + 1) to PeekScale)
    return dots
}

/** Page count threshold at which adaptive shrinking/clipping engages. */
private const val AdaptiveSizingThreshold = 6

/** Full-scale dots on each side of the current page — 2 either side + current = 5. */
private const val CoreWindowRadius = 2

/** Render scale for shrunk "peek" dots outside the core. */
private const val PeekScale = 0.6f

// ════════════════════════════════════════════════════════════════════════════
// CIRCULAR PROGRESS INDICATOR
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaCircularIndicator — a circular loading/progress ring.
 *
 * ### Purpose
 * Indicates status or completion of a process: indeterminate (open-ended,
 * short waits) or determinate (precise, long waits with known duration).
 *
 * ### Anatomy
 * Circular track + rotating (indeterminate) or filling (determinate) ring,
 * plus an optional label **below** the circle.
 *
 * ### Variants
 * Indeterminate ([progress] = null) vs Determinate ([ProgressDirection.CounterClockwise]
 * for countdown timers).
 *
 * ### States
 * Active (animating) and Complete — pass [completedContent] to crossfade
 * once [completed] is true (500ms linear transition).
 *
 * ### Sizing
 * [sizePreset] → 4 tiers via [getProgressConfig]. Stroke width is fixed
 * at 1dp ([HierarchicalSize.Border.Compact]) at every size.
 *
 * ### Customization
 * [variant], [sizePreset], [direction], [showPercentage], [customColors],
 * [completedContent].
 *
 * ### Usage notes
 * - Switch from indeterminate to determinate once a wait-time estimate is known.
 * - Placement communicates scope: centered = whole-page loading; inside a
 *   sheet/button = that surface's own loading.
 *
 * @param progress Progress value 0.0 to 1.0 (null for indeterminate)
 * @param modifier Modifier for the indicator
 * @param variant Color variant (Default: Primary)
 * @param sizePreset Size preset (Default: Medium)
 * @param direction Fill direction for determinate progress (Default: Clockwise)
 * @param showPercentage Show a percentage label below the circle (determinate only)
 * @param completed Whether the Complete state is active
 * @param completedContent Content shown once [completed] is true
 * @param customColors Optional custom colors
 * @param contentDescription Accessibility description (Default: "Loading" / "Loading, N% complete")
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
                    // Indeterminate spinning animation
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

                        // Progress arc
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

            // Optional label below the circle
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

// ════════════════════════════════════════════════════════════════════════════
// PROGRESS PILL
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaProgressPill — a pill-shaped determinate progress indicator with
 * its label inside the pill. Known-duration alternative to [PixaCircularIndicator].
 *
 * ### Anatomy
 * Pill-shaped track ([AppTheme.shapes.pill]) + fill growing from the start
 * edge, with an optional label **centered inside** the pill.
 *
 * ### States
 * Active (filling) and Complete — pass [completedContent] to crossfade
 * (500ms linear).
 *
 * ### Sizing
 * [sizePreset] resolves height via [HierarchicalSize.Chip]. Width fills
 * available space by default.
 *
 * ### Customization
 * [variant], [sizePreset], [direction] ([CounterClockwise] depletes from the
 * end edge for countdown convention), [label], [customColors], [completedContent].
 *
 * Determinate-only — [progress] is non-nullable.
 *
 * @param progress Progress value 0.0 to 1.0
 * @param modifier Modifier for the pill
 * @param variant Color variant (Default: Primary)
 * @param sizePreset Size preset (Default: Medium)
 * @param direction Fill direction (Default: Clockwise)
 * @param label Custom label inside the pill (defaults to percentage)
 * @param showLabel Whether to render label inside the pill (Default: true)
 * @param completed Whether the Complete state is active
 * @param completedContent Content shown once [completed] is true
 * @param customColors Optional custom colors
 * @param contentDescription Accessibility description (Default: "Loading, N% complete")
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

// ════════════════════════════════════════════════════════════════════════════
// LINEAR PROGRESS INDICATOR
// ════════════════════════════════════════════════════════════════════════════

/**
 * Linear Progress Indicator — a linear progress bar for tracking completion.
 *
 * ### Anatomy
 * Background track + progress fill, with optional label below (horizontal)
 * or beside (vertical).
 *
 * ### Sizing
 * [size] resolves track thickness + label style via [getLinearProgressConfig].
 * [height] overrides track thickness explicitly; null derives from [size].
 *
 * ### Behavior
 * Determinate: fills from the leading edge proportionally to [progress].
 * Indeterminate: center-anchored pulse that grows outward and back
 * (quintic ease-in-out).
 *
 * @param progress Progress value 0.0 to 1.0 (null for indeterminate)
 * @param modifier Modifier for the indicator
 * @param variant Color variant (Default: Primary)
 * @param orientation Horizontal or Vertical (Default: Horizontal)
 * @param size Size tier — resolves track thickness + label style (Default: Medium)
 * @param height Exact track thickness override; null derives from [size]
 * @param showLabel Show text label (Default: false)
 * @param label Custom label text (defaults to percentage for determinate)
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

    // Indeterminate: center-anchored pulse, quintic ease-in-out, back-and-forth.
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

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

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
 * Progress Bar — linear determinate progress with label. Pass [content] to
 * crossfade automatically once [progress] reaches 1f (500ms linear); omit
 * to keep the bar visible indefinitely.
 *
 * @param content Shown in place of the bar once [progress] reaches 1f
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
 * Segmented Progress Indicator — multi-part progress bar with different colors per segment.
 *
 * The segment with [ProgressSegment.isActive] loops a left-to-right fill
 * that fades back (quintic ease-out, 500ms); others are static fills.
 *
 * @param segments List of progress segments
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
 * Active segment loop animation: fill sweeps left to right and fades back
 * (quintic ease-out, 500ms).
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
    // Peaks mid-way, fades out at the end.
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

// ════════════════════════════════════════════════════════════════════════════
// PAGER INDICATOR
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaPagerIndicator — a row of indicators, each representing a page in a list.
 *
 * ### Anatomy
 * A row of dots (or dashes via [PagerIndicatorStyle.Dash]), one per page,
 * equidistantly spaced. Optionally wrapped in a pill container ([showContainer]).
 *
 * ### Adaptive sizing / clipping
 * Below 6 pages, every dot renders full-scale. At/above 6, [windowedPageControlDots]
 * windows the row to a 5-dot core plus one shrunk peek dot per side, with the
 * core frozen at sequence edges.
 *
 * ### Variants
 * [colorScheme] provides Default/Inverse/AlwaysDark/AlwaysLight.
 * [style]/[widthMode] are Pixa extensions beyond dots-only anatomy.
 *
 * ### States
 * [enabled] false renders the disabled state. When [onPageSelected] is set,
 * each dot becomes tappable.
 *
 * ### Usage notes
 * 5 or fewer pages recommended, 10+ discouraged without alternative navigation.
 *
 * @param pageCount Total number of pages
 * @param currentPage Current page index (0-based)
 * @param modifier Modifier for the indicator
 * @param style Visual style (Default: Circle)
 * @param widthMode Width mode (Default: Uniform)
 * @param colorScheme Color scheme variant (Default: Default)
 * @param enabled Whether the control is interactive (Default: true)
 * @param showContainer Wraps dots in a pill container (Default: false)
 * @param onPageSelected Callback with real page index; null = non-interactive
 * @param config Overrides colors/sizes entirely when supplied
 * @param contentDescription Accessibility label (Default: "Page"); per-dot state announced via `stateDescription`
 */
@Composable
fun PixaPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    style: PagerIndicatorStyle = PagerIndicatorStyle.Circle,
    widthMode: PagerIndicatorWidthMode = PagerIndicatorWidthMode.Uniform,
    colorScheme: PageControlColorScheme = PageControlColorScheme.Default,
    enabled: Boolean = true,
    showContainer: Boolean = false,
    onPageSelected: ((Int) -> Unit)? = null,
    config: PagerIndicatorConfig? = null,
    contentDescription: String? = null
) {
    val schemeColors = getPageControlColors(colorScheme, enabled)
    val defaultConfig = PagerIndicatorConfig(
        indicatorSize = if (style == PagerIndicatorStyle.Dash) HierarchicalSize.Radius.Compact else HierarchicalSize.Badge.Nano,
        selectedIndicatorSize = when {
            style == PagerIndicatorStyle.Dash && widthMode == PagerIndicatorWidthMode.ExpandSelected -> HierarchicalSize.Icon.Compact
            style == PagerIndicatorStyle.Dash -> HierarchicalSize.Icon.Nano
            else -> HierarchicalSize.Badge.Nano
        },
        spacing = HierarchicalSize.Spacing.Small,
        selectedColor = schemeColors.selected,
        unselectedColor = schemeColors.unselected,
        cornerRadius = if (style == PagerIndicatorStyle.Dash) HierarchicalSize.Radius.Nano else HierarchicalSize.Radius.Compact
    )

    val indicatorConfig = config ?: defaultConfig
    val safeCurrentPage = currentPage.coerceIn(0, pageCount - 1)
    val visibleDots = windowedPageControlDots(pageCount, safeCurrentPage)

    val rowModifier = Modifier
        .semantics {
            this.contentDescription = contentDescription ?: "Page"
            this.stateDescription = "${safeCurrentPage + 1} of $pageCount"
        }
        .then(
            if (showContainer) {
                Modifier
                    .elevationShadow(HierarchicalSize.Shadow.Massive, AppTheme.shapes.pill)
                    .clip(AppTheme.shapes.pill)
                    .background(AppTheme.colors.baseSurfaceDefault)
                    .border(HierarchicalSize.Border.Compact, AppTheme.colors.baseBorderSubtle, AppTheme.shapes.pill)
                    .padding(horizontal = HierarchicalSize.Spacing.Small, vertical = HierarchicalSize.Spacing.Compact)
            } else {
                Modifier
            }
        )

    Row(
        modifier = modifier.then(rowModifier),
        horizontalArrangement = Arrangement.spacedBy(indicatorConfig.spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleDots.forEach { (index, peekScale) ->
            val isSelected = index == safeCurrentPage

            // Animate color transition
            val indicatorColor by animateColorAsState(
                targetValue = if (isSelected) indicatorConfig.selectedColor else indicatorConfig.unselectedColor,
                animationSpec = AnimationUtils.standardTween(),
                label = "indicator_color_$index"
            )

            // Determine size based on style and mode
            val baseWidth = when {
                style == PagerIndicatorStyle.Circle -> indicatorConfig.indicatorSize
                widthMode == PagerIndicatorWidthMode.ExpandSelected && isSelected -> indicatorConfig.selectedIndicatorSize
                widthMode == PagerIndicatorWidthMode.ExpandSelected -> indicatorConfig.indicatorSize
                else -> indicatorConfig.selectedIndicatorSize
            }

            // Animate size transition (base size, then peek-scaled for edge "more pages" dots)
            val animatedWidth by animateDpAsState(
                targetValue = baseWidth * peekScale,
                animationSpec = AnimationUtils.standardSpring(),
                label = "indicator_width_$index"
            )
            val animatedHeight by animateDpAsState(
                targetValue = indicatorConfig.indicatorSize * peekScale,
                animationSpec = AnimationUtils.standardSpring(),
                label = "indicator_height_$index"
            )

            // Shape based on style
            val shape = when (style) {
                PagerIndicatorStyle.Circle -> CircleShape
                PagerIndicatorStyle.Dash -> RoundedCornerShape(indicatorConfig.cornerRadius)
            }

            if (onPageSelected != null) {
                // Tap-to-navigate hits a WCAG-minimum touch target, not the (much smaller) visual dot itself.
                Box(
                    modifier = Modifier
                        .size(HierarchicalSize.TouchTarget.Small)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = enabled,
                            onClick = { onPageSelected(index) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(animatedWidth)
                            .height(animatedHeight)
                            .background(color = indicatorColor, shape = shape)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(animatedWidth)
                        .height(animatedHeight)
                        .background(color = indicatorColor, shape = shape)
                )
            }
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

// ════════════════════════════════════════════════════════════════════════════
// USAGE EXAMPLES
// ════════════════════════════════════════════════════════════════════════════

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
