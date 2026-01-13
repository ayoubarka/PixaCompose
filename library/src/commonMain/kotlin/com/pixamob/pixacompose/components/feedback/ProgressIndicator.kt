package com.pixamob.pixacompose.components.feedback

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.BorderSize
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.Spacing
import com.pixamob.pixacompose.utils.AnimationUtils
import kotlin.math.min

/**
 * ProgressIndicator Component
 *
 * Show loading state or task progress with circular and linear variants.
 * Supports determinate (with progress value) and indeterminate (infinite animation) modes.
 * Uses semantic colors for different status indicators.
 *
 * Features:
 * - Circular and Linear variants
 * - Determinate (0.0 to 1.0 progress) and Indeterminate (infinite) modes
 * - Multiple size presets
 * - Semantic color variants (Primary, Success, Warning, Error, Info, Neutral)
 * - Optional percentage label for circular progress
 * - Smooth animations
 * - Full accessibility support
 *
 * @sample
 * ```
 * // Indeterminate loading
 * CircularProgressIndicator()
 *
 * // Determinate progress
 * CircularProgressIndicator(
 *     progress = 0.65f,
 *     showPercentage = true
 * )
 *
 * // Linear progress bar
 * LinearProgressIndicator(
 *     progress = uploadProgress,
 *     variant = ProgressVariant.Success,
 *     showLabel = true,
 *     label = "Uploading..."
 * )
 * ```
 */

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Progress indicator variant - semantic colors
 */
enum class ProgressVariant {
    /** Primary brand color - default */
    Primary,
    /** Success/completion indicator */
    Success,
    /** Warning/caution indicator */
    Warning,
    /** Error/failed indicator */
    Error,
    /** Informational indicator */
    Info,
    /** Neutral/default indicator */
    Neutral
}

/**
 * Progress indicator size presets
 */
enum class ProgressSize {
    /** 16dp - Small inline indicator */
    Small,
    /** 24dp - Medium size (default) */
    Medium,
    /** 40dp - Large prominent indicator */
    Large,
    /** 48dp - Extra large for loading screens (mobile-optimized) */
    ExtraLarge
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
 * Progress segment for multi-part progress bars
 */
@Immutable
@Stable
data class ProgressSegment(
    val progress: Float,
    val variant: ProgressVariant = ProgressVariant.Primary,
    val color: Color? = null
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
 * Get progress configuration based on size
 */
@Composable
private fun getProgressConfig(size: ProgressSize): ProgressConfig {
    val typography = AppTheme.typography
    return when (size) {
        ProgressSize.Small -> ProgressConfig(
            size = 16.dp,
            strokeWidth = BorderSize.Tiny,
            labelStyle = { typography.captionRegular }
        )
        ProgressSize.Medium -> ProgressConfig(
            size = 24.dp,
            strokeWidth = BorderSize.Standard,
            labelStyle = { typography.bodySmall }
        )
        ProgressSize.Large -> ProgressConfig(
            size = 40.dp,
            strokeWidth = BorderSize.Thick,
            labelStyle = { typography.bodyRegular }
        )
        ProgressSize.ExtraLarge -> ProgressConfig(
            size = 48.dp,
            strokeWidth = BorderSize.ExtraThick,
            labelStyle = { typography.bodyLarge }
        )
    }
}

// ============================================================================
// CIRCULAR PROGRESS INDICATOR
// ============================================================================

/**
 * Circular Progress Indicator - Shows progress in circular form
 *
 * @param progress Progress value from 0.0 to 1.0 (null for indeterminate)
 * @param modifier Modifier for the indicator
 * @param variant Color variant (Primary, Success, Warning, Error, Info, Neutral)
 * @param sizePreset Size preset
 * @param showPercentage Show percentage text in center (only for determinate)
 * @param customColors Optional custom colors
 * @param contentDescription Accessibility description
 */
@Composable
fun CircularProgressIndicator(
    progress: Float? = null,
    modifier: Modifier = Modifier,
    variant: ProgressVariant = ProgressVariant.Primary,
    sizePreset: ProgressSize = ProgressSize.Medium,
    showPercentage: Boolean = false,
    customColors: ProgressColors? = null,
    contentDescription: String? = null
) {
    val colors = customColors ?: getProgressColors(variant, AppTheme.colors)
    val config = getProgressConfig(sizePreset)

    val isIndeterminate = progress == null
    val progressValue = progress?.coerceIn(0f, 1f) ?: 0f

    // Animate sweep angle for determinate progress
    val animatedSweepAngle by animateFloatAsState(
        targetValue = if (!isIndeterminate) 360f * progressValue else 0f,
        animationSpec = AnimationUtils.standardSpring(),
        label = "sweep_angle"
    )

    val description = contentDescription ?: if (isIndeterminate) {
        "Loading"
    } else {
        "Progress ${(progressValue * 100).toInt()} percent"
    }

    Box(
        modifier = modifier
            .size(config.size)
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
        contentAlignment = Alignment.Center
    ) {
        if (isIndeterminate) {
            // Indeterminate circular animation
            val infiniteTransition = rememberInfiniteTransition(label = "circular_progress")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = AnimationUtils.infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing)
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
                    sweepAngle = 90f,
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
                        sweepAngle = animatedSweepAngle,
                        useCenter = false,
                        topLeft = Offset(topLeftX, topLeftY),
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )
                }
            }

            // Percentage label
            if (showPercentage && sizePreset != ProgressSize.Small) {
                val percentage = (progressValue * 100).toInt()
                val percentageText = config.percentageFormat.replace("%d", percentage.toString())
                Text(
                    text = percentageText,
                    style = config.labelStyle(),
                    color = colors.label,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ============================================================================
// LINEAR PROGRESS INDICATOR
// ============================================================================

/**
 * Linear Progress Indicator - Shows progress as a horizontal or vertical bar
 *
 * @param progress Progress value from 0.0 to 1.0 (null for indeterminate)
 * @param modifier Modifier for the indicator
 * @param variant Color variant
 * @param orientation Horizontal or Vertical
 * @param height Height of the progress bar (width for vertical)
 * @param showLabel Show text label
 * @param label Custom label text (shows percentage if not provided for determinate)
 * @param customColors Optional custom colors
 * @param contentDescription Accessibility description
 */
@Composable
fun LinearProgressIndicator(
    progress: Float? = null,
    modifier: Modifier = Modifier,
    variant: ProgressVariant = ProgressVariant.Primary,
    orientation: ProgressOrientation = ProgressOrientation.Horizontal,
    height: Dp = 4.dp,
    showLabel: Boolean = false,
    label: String? = null,
    customColors: ProgressColors? = null,
    contentDescription: String? = null
) {
    val colors = customColors ?: getProgressColors(variant, AppTheme.colors)
    val typography = AppTheme.typography

    val isIndeterminate = progress == null
    val progressValue = progress?.coerceIn(0f, 1f) ?: 0f

    // Animate determinate progress
    val animatedProgress by animateFloatAsState(
        targetValue = if (!isIndeterminate) progressValue else 0f,
        animationSpec = AnimationUtils.standardSpring(),
        label = "linear_progress"
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

                    Text(
                        text = displayLabel,
                        style = typography.bodySmall,
                        color = colors.label,
                        modifier = Modifier.padding(bottom = Spacing.ExtraSmall)
                    )
                }

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                        .clip(RoundedCornerShape(height / 2))
                        .background(colors.track)
                ) {
                    if (isIndeterminate) {
                        // Indeterminate linear animation
                        val infiniteTransition = rememberInfiniteTransition(label = "linear_progress")
                        val animatedProgress by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = AnimationUtils.infiniteRepeatable(
                                animation = tween(1500, easing = FastOutSlowInEasing)
                            ),
                            label = "progress"
                        )

                        // Draw animated progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .background(colors.progress, RoundedCornerShape(height / 2))
                        )
                    } else {
                        // Determinate linear progress with animation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .background(colors.progress, RoundedCornerShape(height / 2))
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
                horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
            ) {
                // Progress bar (vertical)
                Box(
                    modifier = Modifier
                        .width(height)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(height / 2))
                        .background(colors.track)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                    ) {
                        if (isIndeterminate) {
                            // Indeterminate vertical animation
                            val infiniteTransition = rememberInfiniteTransition(label = "vertical_progress")
                            val animatedProgress by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = AnimationUtils.infiniteRepeatable(
                                    animation = tween(1500, easing = FastOutSlowInEasing)
                                ),
                                label = "progress"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(animatedProgress)
                                    .background(colors.progress, RoundedCornerShape(height / 2))
                            )
                        } else {
                            // Determinate vertical progress with animation
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(animatedProgress)
                                    .background(colors.progress, RoundedCornerShape(height / 2))
                            )
                        }
                    }
                }

                // Label (vertical)
                if (showLabel) {
                    val displayLabel = label ?: if (!isIndeterminate) {
                        "${(progressValue * 100).toInt()}%"
                    } else {
                        "..."
                    }

                    Text(
                        text = displayLabel,
                        style = typography.bodySmall,
                        color = colors.label
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
    sizePreset: ProgressSize = ProgressSize.Medium,
    variant: ProgressVariant = ProgressVariant.Primary
) {
    CircularProgressIndicator(
        progress = null,
        modifier = modifier,
        variant = variant,
        sizePreset = sizePreset
    )
}

/**
 * Progress Bar - Linear determinate progress with label
 */
@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    variant: ProgressVariant = ProgressVariant.Primary,
    label: String? = null
) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier,
        variant = variant,
        showLabel = true,
        label = label
    )
}

/**
 * Segmented Progress Indicator - Multi-part progress bar with different colors per segment
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
    height: Dp = 4.dp,
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
            Text(
                text = "${(totalProgress * 100).toInt()}%",
                style = typography.bodySmall,
                color = colors.baseContentBody,
                modifier = Modifier.padding(bottom = Spacing.ExtraSmall)
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
                        Box(
                            modifier = Modifier
                                .weight(segmentProgress)
                                .fillMaxHeight()
                                .background(
                                    segment.color ?: segmentColors.progress,
                                    RoundedCornerShape(height / 2)
                                )
                        )
                    }
                }
            }
        }
    }
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
 *     sizePreset = ProgressSize.Large
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
 *         sizePreset = ProgressSize.Large,
 *         variant = ProgressVariant.Primary
 *     )
 * }
 * ```
 */
