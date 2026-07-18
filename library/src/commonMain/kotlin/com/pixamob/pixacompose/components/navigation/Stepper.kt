package com.pixamob.pixacompose.components.navigation

/**
 * Multi-step progress indicator for processes (wizards, onboarding, itineraries).
 *
 * ### Anatomy
 * Indicator + connector path + label content + optional trailing content.
 * Path at first/last step independently toggleable.
 *
 * ### Variants
 * - Artwork size: [StepArtworkSize.XSmall]–[StepArtworkSize.Large]
 * - Artwork type: Dot, Number, Icon, CheckmarkNumber, IconNumber, Bar, None
 * - Connector: Line, Separator, Dashed, Arrow, None
 * - Content style: Simple, Card, Compact
 *
 * ### States
 * Completed, Current, Skipped, Error, Pending.
 *
 * ### Sizing
 * [SizeVariant] → [StepArtworkSize] via [StepArtworkSize.fromSizeVariant].
 *
 * > Requires at least 3 steps.
 */

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.surfaces.PixaCard
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.components.surfaces.BaseCardVariant
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.feedback.SkeletonCircle
import com.pixamob.pixacompose.components.feedback.SkeletonText
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class StepperOrientation {
    Vertical,
    Horizontal
}

/** Step indicator type. */
enum class StepIndicatorType {
    /** Simple dot indicator */
    Dot,

    /** Numbered indicator (1, 2, 3...) */
    Number,

    /** Icon-based indicator */
    Icon,

    /** Checkmark for completed, number for others */
    CheckmarkNumber,

    /** Horizontal segmented bar (filled/empty rectangles) */
    Bar,

    /** Mixed: Icon + Number overlay */
    IconNumber,

    /** No visible artwork — path runs straight through the step. */
    None
}

/** Artwork/indicator size tier. XSmall (16dp) is reserved for stops/bullets. */
enum class StepArtworkSize {
    XSmall,
    Small,
    Medium,
    Large;

    companion object {
        /** Maps [SizeVariant] onto the 4-tier artwork scale. */
        fun fromSizeVariant(variant: SizeVariant): StepArtworkSize = when (variant) {
            SizeVariant.None, SizeVariant.Nano -> XSmall
            SizeVariant.Compact, SizeVariant.Small -> Small
            SizeVariant.Medium -> Medium
            SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> Large
        }
    }
}

/**
 * Artwork shape at XSmall. Restricted to Circle/Square only (no icon/number at this size).
 */
enum class StepArtworkShape {
    Circle,
    Square
}

/**
 * Connector style between steps.
 */
enum class StepConnectorStyle {
    /** Solid line connector (spec: enabled path) */
    Line,

    /** Dashed line connector */
    Dashed,

    /** Dashed line with short segments */
    DashedShort,

    /** Dashed line with long segments */
    DashedLong,

    /** Arrow connector */
    Arrow,

    /** No connector (spec: disabled path) */
    None,

    /** Thin gray separator line */
    Separator
}

/**
 * Step content style
 */
enum class StepContentStyle {
    /** Simple text labels */
    Simple,

    /** Card-wrapped content */
    Card,

    /** Compact inline */
    Compact
}

/**
 * Card shape for step content
 */
enum class StepCardShape {
    /** Rounded corners (default) */
    Rounded,

    /** Arrow/pointer at bottom (vertical) or right (horizontal) */
    Arrow,

    /** Pointed/triangular bottom/right */
    Pointy
}

/**
 * Step data
 */
@Immutable
@Stable
data class StepData(
    val title: String,
    val subTitle: String? = null,
    val icon: Painter? = null,
    val isCompleted: Boolean = false,
    val isError: Boolean = false,
    val isSkipped: Boolean = false,
    val isClickable: Boolean = true
)

/**
 * Stepper strings for i18n
 */
@Immutable
@Stable
data class StepperStrings(
    val headerFormat: String = "Step %d of %d",
    val completeLabel: String = "Complete",
    val currentLabel: String = "Active",
    val pendingLabel: String = "Incomplete",
    val errorLabel: String = "Error",
    val skippedLabel: String = "Skipped",
    val percentCompleteFormat: String = "%d%% complete"
)

/**
 * Stepper configuration
 */
@Immutable
@Stable
data class StepperConfig(
    val indicatorSize: Dp,
    val iconSize: Dp,
    val connectorWidth: Dp,
    val connectorThickness: Dp,
    val titleStyle: @Composable () -> TextStyle,
    val subTitleStyle: @Composable () -> TextStyle,
    val numberStyle: @Composable () -> TextStyle,
    val spacing: Dp,
    val minTouchTarget: Dp = HierarchicalSize.TouchTarget.Small,
    val animateConnector: Boolean = true,
    /** XSmall is restricted to bullet/stop artwork (circle/square, no icons/numbers). */
    val isXSmall: Boolean = false
)

/**
 * Step colors
 */
@Immutable
@Stable
data class StepColors(
    val completed: Color,
    val current: Color,
    val pending: Color,
    val error: Color,
    val skipped: Color,
    val completedContent: Color,
    val currentContent: Color,
    val pendingContent: Color,
    val errorContent: Color,
    val skippedContent: Color,
    val connector: Color,
    val label: Color,
    val subLabel: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Get stepper configuration based on artwork size.
 */
@Composable
private fun getStepperConfig(size: StepArtworkSize): StepperConfig {
    val typography = AppTheme.typography
    return when (size) {
        // XSmall artwork reserved for stops/bullets.
        StepArtworkSize.XSmall -> StepperConfig(
            indicatorSize = HierarchicalSize.Badge.Small,
            iconSize = HierarchicalSize.Icon.None,
            connectorWidth = HierarchicalSize.Spacing.Massive,
            connectorThickness = HierarchicalSize.Border.Medium,
            titleStyle = { typography.captionRegular },
            subTitleStyle = { typography.footnoteRegular },
            numberStyle = { typography.captionRegular.copy(fontWeight = FontWeight.SemiBold) },
            spacing = HierarchicalSize.Spacing.Small,
            isXSmall = true
        )

        StepArtworkSize.Small -> StepperConfig(
            indicatorSize = HierarchicalSize.Container.Compact,
            iconSize = HierarchicalSize.Icon.Compact,
            connectorWidth = HierarchicalSize.Spacing.Huge + HierarchicalSize.Spacing.Huge,
            connectorThickness = HierarchicalSize.Border.Medium,
            titleStyle = { typography.bodyLight },
            subTitleStyle = { typography.captionRegular },
            numberStyle = { typography.bodyLight.copy(fontWeight = FontWeight.SemiBold) },
            spacing = HierarchicalSize.Spacing.Small
        )

        StepArtworkSize.Medium -> StepperConfig(
            indicatorSize = HierarchicalSize.Container.Small,
            iconSize = HierarchicalSize.Icon.Small,
            connectorWidth = HierarchicalSize.Container.Medium,
            connectorThickness = HierarchicalSize.Border.Medium,
            titleStyle = { typography.bodyRegular },
            subTitleStyle = { typography.bodyLight },
            numberStyle = { typography.bodyRegular.copy(fontWeight = FontWeight.Bold) },
            spacing = HierarchicalSize.Spacing.Medium
        )

        StepArtworkSize.Large -> StepperConfig(
            indicatorSize = HierarchicalSize.Container.Medium,
            iconSize = HierarchicalSize.Icon.Medium,
            connectorWidth = HierarchicalSize.Container.Large,
            connectorThickness = HierarchicalSize.Border.Large,
            titleStyle = { typography.bodyBold },
            subTitleStyle = { typography.bodyRegular },
            numberStyle = { typography.bodyBold.copy(fontWeight = FontWeight.Bold) },
            spacing = HierarchicalSize.Spacing.Medium
        )
    }
}

/**
 * Get step colors
 */
@Composable
private fun getStepColors(colors: ColorPalette): StepColors {
    return StepColors(
        completed = colors.successSurfaceDefault,
        current = colors.brandSurfaceDefault,
        pending = colors.baseSurfaceSubtle,
        error = colors.errorSurfaceDefault,
        skipped = colors.baseSurfaceDisabled,
        completedContent = colors.successContentDefault,
        currentContent = colors.brandContentDefault,
        pendingContent = colors.baseContentHint,
        errorContent = colors.errorContentDefault,
        skippedContent = colors.baseContentDisabled,
        connector = colors.baseBorderDefault,
        label = colors.baseContentBody,
        subLabel = colors.baseContentCaption
    )
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL STEPPER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Step state enum
 */
private enum class StepState {
    Completed,
    Current,
    Error,
    Skipped,
    Pending
}

private fun resolveStepState(stepData: StepData, isCurrentStep: Boolean): StepState = when {
    stepData.isError -> StepState.Error
    stepData.isCompleted -> StepState.Completed
    stepData.isSkipped -> StepState.Skipped
    isCurrentStep -> StepState.Current
    else -> StepState.Pending
}

/**
 * Get accessibility description for step.
 */
private fun getStepAccessibilityDescription(
    stepNumber: Int,
    totalSteps: Int,
    stepData: StepData,
    isCurrentStep: Boolean,
    strings: StepperStrings
): String {
    val state = when (resolveStepState(stepData, isCurrentStep)) {
        StepState.Error -> strings.errorLabel
        StepState.Completed -> strings.completeLabel
        StepState.Skipped -> strings.skippedLabel
        StepState.Current -> strings.currentLabel
        StepState.Pending -> strings.pendingLabel
    }
    return "Step $stepNumber of $totalSteps, ${stepData.title}, $state"
}

/**
 * Stepper Header - Shows "Step X of Y" and progress percentage
 */
@Composable
private fun StepperHeader(
    currentStep: Int,
    totalSteps: Int,
    strings: StepperStrings,
    colors: StepColors,
    config: StepperConfig
) {
    val completedCount = currentStep
    val percentComplete = if (totalSteps > 0) {
        (completedCount.toFloat() / totalSteps * 100).toInt()
    } else {
        0
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
    ) {
        BasicText(
            text = strings.headerFormat.replace("%d", (currentStep + 1).toString())
                .replaceFirst("%d", totalSteps.toString()),
            style = config.titleStyle().copy(color = colors.label, fontWeight = FontWeight.SemiBold)
        )

        BasicText(
            text = strings.percentCompleteFormat.replace("%d", percentComplete.toString()),
            style = config.subTitleStyle().copy(color = colors.subLabel)
        )
    }
}

/**
 * Invisible frame preserving step alignment when a leading/trailing path is toggled off.
 */
@Composable
private fun PathSpacer(config: StepperConfig, orientation: StepperOrientation, modifier: Modifier = Modifier) {
    when (orientation) {
        StepperOrientation.Vertical -> Spacer(
            modifier = modifier
                .width(config.connectorThickness)
                .height(config.connectorWidth)
        )

        StepperOrientation.Horizontal -> Spacer(
            modifier = modifier
                .height(config.connectorThickness)
                .width(config.connectorWidth)
        )
    }
}

/**
 * Step Indicator - The circle/dot/icon for each step
 */
@Composable
private fun StepIndicator(
    stepNumber: Int,
    stepData: StepData,
    isCurrentStep: Boolean,
    config: StepperConfig,
    colors: StepColors,
    indicatorType: StepIndicatorType,
    artworkShape: StepArtworkShape,
    totalSteps: Int,
    strings: StepperStrings,
    onClick: (() -> Unit)?
) {
    // XSmall artwork reserved for stops/bullets — no icons/numbers.
    val effectiveType = if (config.isXSmall && indicatorType != StepIndicatorType.None) {
        StepIndicatorType.Dot
    } else {
        indicatorType
    }

    if (effectiveType == StepIndicatorType.None) {
        // Path continues with no visible indicator; frame still occupies space.
        Spacer(modifier = Modifier.size(config.indicatorSize))
        return
    }

    val stepState = resolveStepState(stepData, isCurrentStep)

    val backgroundColor by animateColorAsState(
        targetValue = when (stepState) {
            StepState.Completed -> colors.completed
            StepState.Current -> colors.current
            StepState.Error -> colors.error
            StepState.Skipped -> colors.skipped
            StepState.Pending -> colors.pending
        },
        animationSpec = AnimationUtils.colorSpring,
        label = "step_background"
    )

    val contentColor by animateColorAsState(
        targetValue = when (stepState) {
            StepState.Completed -> colors.completedContent
            StepState.Current -> colors.currentContent
            StepState.Error -> colors.errorContent
            StepState.Skipped -> colors.skippedContent
            StepState.Pending -> colors.pendingContent
        },
        animationSpec = AnimationUtils.colorSpring,
        label = "step_content"
    )

    val scale by animateFloatAsState(
        targetValue = if (isCurrentStep) 1.1f else 1f,
        animationSpec = AnimationUtils.standardSpring(),
        label = "step_scale"
    )

    val borderColor = if (stepState == StepState.Pending) {
        colors.connector
    } else {
        Color.Transparent
    }

    val interactionSource = remember { MutableInteractionSource() }
    val shape = if (artworkShape == StepArtworkShape.Circle) CircleShape else AppTheme.shapes.rounded.forVariant(SizeVariant.Nano)

    Box(
        modifier = Modifier
            .size(config.indicatorSize)
            .scale(scale)
            .semantics {
                this.contentDescription = getStepAccessibilityDescription(
                    stepNumber,
                    totalSteps,
                    stepData,
                    isCurrentStep,
                    strings
                )
                if (onClick != null) {
                    this.role = Role.Button
                }
            }
            .then(
                if (onClick != null && stepData.isClickable) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = pixaRipple(bounded = false, radius = config.indicatorSize / 2),
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(backgroundColor, shape)
            .border(
                width = if (stepState == StepState.Pending) HierarchicalSize.Border.Medium else HierarchicalSize.Border.None,
                color = borderColor,
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        when (effectiveType) {
            StepIndicatorType.Dot -> {
                // Empty - background color shows
            }

            StepIndicatorType.Number -> {
                BasicText(
                    text = stepNumber.toString(),
                    style = config.numberStyle().copy(color = contentColor, textAlign = TextAlign.Center)
                )
            }

            StepIndicatorType.Icon -> {
                stepData.icon?.let { icon ->
                    PixaIcon(
                        painter = icon,
                        contentDescription = null,
                        tint = contentColor,
                        customSize = config.iconSize
                    )
                }
            }

            StepIndicatorType.CheckmarkNumber -> {
                if (stepData.isCompleted) {
                    AnimatedCheckmark(
                        color = contentColor,
                        size = config.iconSize
                    )
                } else {
                    BasicText(
                        text = stepNumber.toString(),
                        style = config.numberStyle().copy(color = contentColor, textAlign = TextAlign.Center)
                    )
                }
            }

            StepIndicatorType.IconNumber -> {
                Box(contentAlignment = Alignment.Center) {
                    stepData.icon?.let { icon: Painter ->
                        PixaIcon(
                            painter = icon,
                            contentDescription = null,
                            tint = contentColor.copy(alpha = 0.5f),
                            customSize = config.iconSize
                        )
                    }
                    BasicText(
                        text = stepNumber.toString(),
                        style = config.numberStyle().copy(
                            fontSize = config.numberStyle().fontSize * IconNumberOverlayScale,
                            color = contentColor,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            StepIndicatorType.Bar -> {
                // Bar is handled differently in horizontal layout - show dot here
            }

            StepIndicatorType.None -> {} // Already handled above
        }
    }
}

/** Overlay number is scaled down relative to the base numeral style; spec-derived, local to IconNumber only. */
private const val IconNumberOverlayScale = 0.7f

/**
 * Animated Checkmark
 */
@Composable
private fun AnimatedCheckmark(
    color: Color,
    size: Dp
) {
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = AnimationUtils.standardSpring(),
        label = "checkmark_draw"
    )

    Canvas(modifier = Modifier.size(size)) {
        val canvasSize = this.size
        val strokeWidth = HierarchicalSize.Stroke.Small.toPx()

        val checkPath = Path().apply {
            val startX = canvasSize.width * 0.2f
            val startY = canvasSize.height * 0.5f
            val midX = canvasSize.width * 0.4f
            val midY = canvasSize.height * 0.7f
            val endX = canvasSize.width * 0.8f
            val endY = canvasSize.height * 0.3f

            moveTo(startX, startY)
            lineTo(midX, midY)
            lineTo(endX, endY)
        }

        val pathMeasure = PathMeasure()
        pathMeasure.setPath(checkPath, false)
        val pathLength = pathMeasure.length

        val animatedPath = Path()
        pathMeasure.getSegment(0f, pathLength * progress, animatedPath, true)

        drawPath(
            path = animatedPath,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )
    }
}

/**
 * Step Connector - Line/dash between steps
 */
@Composable
private fun StepConnector(
    isCompleted: Boolean,
    config: StepperConfig,
    colors: StepColors,
    style: StepConnectorStyle,
    orientation: StepperOrientation,
    modifier: Modifier = Modifier
) {
    // Don't render anything for None connector
    if (style == StepConnectorStyle.None) return

    val progress by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0f,
        animationSpec = if (config.animateConnector) {
            AnimationUtils.slowSpring
        } else {
            AnimationUtils.fastTween()
        },
        label = "connector_progress"
    )

    val connectorColor = if (isCompleted) colors.completed else colors.connector

    // Separator style - thin gray line
    val isSeparator = style == StepConnectorStyle.Separator
    val separatorColor = colors.connector.copy(alpha = 0.3f)
    val thickness = if (isSeparator) HierarchicalSize.Divider.Compact else config.connectorThickness

    when (orientation) {
        StepperOrientation.Vertical -> {
            when (style) {
                StepConnectorStyle.Line, StepConnectorStyle.Separator -> {
                    Box(
                        modifier = modifier
                            .width(thickness)
                            .height(config.connectorWidth)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (isSeparator) separatorColor else colors.connector)
                        )

                        if (!isSeparator) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(progress)
                                    .background(connectorColor)
                            )
                        }
                    }
                }

                StepConnectorStyle.Dashed, StepConnectorStyle.DashedShort, StepConnectorStyle.DashedLong -> {
                    val dashInterval = dashIntervalFor(style)

                    Canvas(
                        modifier = modifier
                            .width(thickness)
                            .height(config.connectorWidth)
                    ) {
                        val pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            intervals = dashInterval,
                            phase = 0f
                        )

                        val startY = 0f
                        val endY = size.height * progress

                        drawLine(
                            color = connectorColor,
                            start = Offset(size.width / 2, startY),
                            end = Offset(size.width / 2, endY),
                            strokeWidth = thickness.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = pathEffect
                        )
                    }
                }

                StepConnectorStyle.Arrow -> {
                    Canvas(
                        modifier = modifier
                            .width(config.indicatorSize / 2)
                            .height(config.connectorWidth)
                    ) {
                        val arrowPath = Path().apply {
                            val width = size.width
                            val height = size.height * progress
                            moveTo(width / 2, 0f)
                            lineTo(width / 2, height - width / 2)
                            lineTo(0f, height - width)
                            moveTo(width / 2, height - width / 2)
                            lineTo(width, height - width)
                        }

                        drawPath(
                            path = arrowPath,
                            color = connectorColor,
                            style = Stroke(
                                width = thickness.toPx(),
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }

                StepConnectorStyle.None -> {} // Already handled above
            }
        }

        StepperOrientation.Horizontal -> {
            when (style) {
                StepConnectorStyle.Line, StepConnectorStyle.Separator -> {
                    Box(
                        modifier = modifier
                            .height(thickness)
                            .width(config.connectorWidth)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (isSeparator) separatorColor else colors.connector)
                        )

                        if (!isSeparator) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progress)
                                    .background(connectorColor)
                            )
                        }
                    }
                }

                StepConnectorStyle.Dashed, StepConnectorStyle.DashedShort, StepConnectorStyle.DashedLong -> {
                    val dashInterval = dashIntervalFor(style)

                    Canvas(
                        modifier = modifier
                            .height(thickness)
                            .width(config.connectorWidth)
                    ) {
                        val pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            intervals = dashInterval,
                            phase = 0f
                        )

                        val startX = 0f
                        val endX = size.width * progress

                        drawLine(
                            color = connectorColor,
                            start = Offset(startX, size.height / 2),
                            end = Offset(endX, size.height / 2),
                            strokeWidth = thickness.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = pathEffect
                        )
                    }
                }

                StepConnectorStyle.Arrow -> {
                    Canvas(
                        modifier = modifier
                            .height(config.indicatorSize / 2)
                            .width(config.connectorWidth)
                    ) {
                        val arrowPath = Path().apply {
                            val width = size.width * progress
                            val height = size.height
                            moveTo(0f, height / 2)
                            lineTo(width - height / 2, height / 2)
                            lineTo(width - height, 0f)
                            moveTo(width - height / 2, height / 2)
                            lineTo(width - height, height)
                        }

                        drawPath(
                            path = arrowPath,
                            color = connectorColor,
                            style = Stroke(
                                width = thickness.toPx(),
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }

                StepConnectorStyle.None -> {} // Already handled above
            }
        }
    }
}

private fun dashIntervalFor(style: StepConnectorStyle): FloatArray = when (style) {
    StepConnectorStyle.DashedShort -> floatArrayOf(8f, 4f)
    StepConnectorStyle.DashedLong -> floatArrayOf(16f, 8f)
    else -> floatArrayOf(12f, 6f)
}

/**
 * Step Content - Title and subtitle
 */
@Composable
private fun StepContent(
    stepData: StepData,
    isCurrentStep: Boolean,
    config: StepperConfig,
    colors: StepColors,
    showLabels: Boolean,
    showSubLabels: Boolean,
    contentStyle: StepContentStyle,
    modifier: Modifier = Modifier
) {
    if (!showLabels) return

    val content = @Composable {
        Column(
            modifier = Modifier.padding(start = config.spacing),
            verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
        ) {
            BasicText(
                text = stepData.title,
                style = config.titleStyle().copy(
                    color = if (isCurrentStep) colors.label else colors.subLabel,
                    fontWeight = if (isCurrentStep) FontWeight.SemiBold else FontWeight.Normal
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (showSubLabels && stepData.subTitle != null) {
                BasicText(
                    text = stepData.subTitle,
                    style = config.subTitleStyle().copy(color = colors.subLabel),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    when (contentStyle) {
        StepContentStyle.Card -> {
            PixaCard(
                modifier = modifier,
                variant = if (isCurrentStep) BaseCardVariant.Elevated else BaseCardVariant.Ghost,
                elevation = if (isCurrentStep) ComponentElevation.Low else ComponentElevation.None,
                padding = SizeVariant.Small
            ) {
                content()
            }
        }

        else -> {
            Box(modifier = modifier) {
                content()
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * Multi-step progress indicator for complex processes.
 *
 * ### Anatomy
 * Indicator + connector + label + trailing content. First/last path independently toggleable.
 *
 * ### Variants
 * [StepperOrientation.Vertical] (default) or [StepperOrientation.Horizontal].
 * 8 [StepIndicatorType]s, 6 [StepConnectorStyle]s, 3 [StepContentStyle]s.
 *
 * ### States
 * Completed, Current, Error, Skipped, Pending — driven by [StepData] flags.
 *
 * ### Sizing
 * [SizeVariant] → [StepArtworkSize] via [StepArtworkSize.fromSizeVariant].
 *
 * @param steps Step data (at least 3 required)
 * @param currentStep 0-based current step index
 * @param orientation Vertical or Horizontal layout
 * @param indicatorType Dot, Number, Icon, CheckmarkNumber, etc.
 * @param artworkShape Circle or Square at XSmall size
 * @param connectorStyle Line, Dashed, Arrow, Separator, None
 * @param contentStyle Simple, Card, Compact
 * @param cardShape Rounded, Arrow, Pointy (for Card content)
 * @param size Size preset
 * @param showLabels Show/hide step labels
 * @param showSubLabels Show/hide step subtitles
 * @param showHeader Show "Step X of Y" header
 * @param showLeadingPath Path before first step
 * @param showTrailingPath Path after last step
 * @param onStepClick Optional click handler
 * @param isStepClickable Per-step clickability check
 * @param strings Localized labels
 * @param isLoading Shows skeleton placeholder
 */
@Composable
fun PixaStepper(
    steps: List<StepData>,
    currentStep: Int,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    orientation: StepperOrientation = StepperOrientation.Vertical,
    indicatorType: StepIndicatorType = StepIndicatorType.CheckmarkNumber,
    artworkShape: StepArtworkShape = StepArtworkShape.Circle,
    connectorStyle: StepConnectorStyle = StepConnectorStyle.Line,
    contentStyle: StepContentStyle = StepContentStyle.Simple,
    cardShape: StepCardShape = StepCardShape.Rounded,
    size: SizeVariant = SizeVariant.Medium,
    showLabels: Boolean = true,
    showSubLabels: Boolean = true,
    showHeader: Boolean = false,
    showLeadingPath: Boolean = false,
    showTrailingPath: Boolean = false,
    isStepClickable: ((Int) -> Boolean)? = null,
    strings: StepperStrings = StepperStrings(),
    onStepClick: ((Int) -> Unit)? = null,
) {
    require(steps.size >= 3) {
        "PixaStepper requires at least 3 steps; got ${steps.size}."
    }

    val config = getStepperConfig(StepArtworkSize.fromSizeVariant(size))
    val colors = getStepColors(AppTheme.colors)

    if (isLoading) {
        Column(modifier = modifier) {
            repeat(steps.size.coerceAtLeast(3)) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SkeletonCircle(
                        size = config.indicatorSize,
                        shimmerEnabled = true
                    )
                    Spacer(modifier = Modifier.width(config.spacing))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        SkeletonText(
                            width = HierarchicalSize.Image.Compact,
                            size = SizeVariant.Medium
                        )
                        if (showSubLabels) {
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                            SkeletonText(
                                width = HierarchicalSize.Container.Massive,
                                size = SizeVariant.Small
                            )
                        }
                    }
                }
                if (index < steps.size - 1) {
                    Spacer(modifier = Modifier.height(config.spacing))
                }
            }
        }
        return
    }

    Column(modifier = modifier) {
        if (showHeader) {
            StepperHeader(
                currentStep = currentStep,
                totalSteps = steps.size,
                strings = strings,
                colors = colors,
                config = config
            )
            Spacer(modifier = Modifier.height(config.spacing))
        }

        when (orientation) {
            StepperOrientation.Vertical -> {
                VerticalStepper(
                    steps = steps,
                    currentStep = currentStep,
                    config = config,
                    colors = colors,
                    indicatorType = indicatorType,
                    artworkShape = artworkShape,
                    connectorStyle = connectorStyle,
                    contentStyle = contentStyle,
                    cardShape = cardShape,
                    showLabels = showLabels,
                    showSubLabels = showSubLabels,
                    showLeadingPath = showLeadingPath,
                    showTrailingPath = showTrailingPath,
                    onStepClick = onStepClick,
                    isStepClickable = isStepClickable,
                    strings = strings
                )
            }

            StepperOrientation.Horizontal -> {
                HorizontalStepper(
                    steps = steps,
                    currentStep = currentStep,
                    config = config,
                    colors = colors,
                    indicatorType = indicatorType,
                    artworkShape = artworkShape,
                    connectorStyle = connectorStyle,
                    contentStyle = contentStyle,
                    cardShape = cardShape,
                    showLabels = showLabels,
                    showSubLabels = showSubLabels,
                    onStepClick = onStepClick,
                    isStepClickable = isStepClickable,
                    strings = strings
                )
            }
        }
    }
}

/**
 * Vertical Stepper Layout
 */
@Composable
private fun VerticalStepper(
    steps: List<StepData>,
    currentStep: Int,
    config: StepperConfig,
    colors: StepColors,
    indicatorType: StepIndicatorType,
    artworkShape: StepArtworkShape,
    connectorStyle: StepConnectorStyle,
    contentStyle: StepContentStyle,
    cardShape: StepCardShape,
    showLabels: Boolean,
    showSubLabels: Boolean,
    showLeadingPath: Boolean,
    showTrailingPath: Boolean,
    onStepClick: ((Int) -> Unit)?,
    isStepClickable: ((Int) -> Boolean)?,
    strings: StepperStrings,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Leading path above the first step; invisible frame preserves alignment.
                    if (index == 0) {
                        if (showLeadingPath && connectorStyle != StepConnectorStyle.None) {
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                            StepConnector(
                                isCompleted = false,
                                config = config,
                                colors = colors,
                                style = connectorStyle,
                                orientation = StepperOrientation.Vertical
                            )
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                        } else {
                            PathSpacer(config, StepperOrientation.Vertical)
                        }
                    }

                    StepIndicator(
                        stepNumber = index + 1,
                        stepData = step,
                        isCurrentStep = index == currentStep,
                        config = config,
                        colors = colors,
                        indicatorType = indicatorType,
                        artworkShape = artworkShape,
                        totalSteps = steps.size,
                        strings = strings,
                        onClick = if (onStepClick != null && (isStepClickable?.invoke(index) != false) && step.isClickable) {
                            { onStepClick(index) }
                        } else null
                    )

                    if (index < steps.size - 1 && connectorStyle != StepConnectorStyle.None) {
                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                        StepConnector(
                            isCompleted = step.isCompleted,
                            config = config,
                            colors = colors,
                            style = connectorStyle,
                            orientation = StepperOrientation.Vertical
                        )
                        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                    } else if (index == steps.size - 1) {
                        if (showTrailingPath && connectorStyle != StepConnectorStyle.None) {
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                            StepConnector(
                                isCompleted = step.isCompleted,
                                config = config,
                                colors = colors,
                                style = connectorStyle,
                                orientation = StepperOrientation.Vertical
                            )
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                        } else {
                            PathSpacer(config, StepperOrientation.Vertical)
                        }
                    }
                }

                StepContent(
                    stepData = step,
                    isCurrentStep = index == currentStep,
                    config = config,
                    colors = colors,
                    showLabels = showLabels,
                    showSubLabels = showSubLabels,
                    contentStyle = contentStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Horizontal Stepper Layout
 */
@Composable
private fun HorizontalStepper(
    steps: List<StepData>,
    currentStep: Int,
    config: StepperConfig,
    colors: StepColors,
    indicatorType: StepIndicatorType,
    artworkShape: StepArtworkShape,
    connectorStyle: StepConnectorStyle,
    contentStyle: StepContentStyle,
    cardShape: StepCardShape,
    showLabels: Boolean,
    showSubLabels: Boolean,
    onStepClick: ((Int) -> Unit)?,
    isStepClickable: ((Int) -> Boolean)?,
    strings: StepperStrings,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        StepIndicator(
                            stepNumber = index + 1,
                            stepData = step,
                            isCurrentStep = index == currentStep,
                            config = config,
                            colors = colors,
                            indicatorType = indicatorType,
                            artworkShape = artworkShape,
                            totalSteps = steps.size,
                            strings = strings,
                            onClick = if (onStepClick != null && (isStepClickable?.invoke(index) != false) && step.isClickable) {
                                { onStepClick(index) }
                            } else null
                        )

                        if (showLabels) {
                            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                            BasicText(
                                text = step.title,
                                style = config.titleStyle().copy(
                                    color = if (index == currentStep) colors.label else colors.subLabel,
                                    fontWeight = if (index == currentStep) FontWeight.SemiBold else FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (index < steps.size - 1 && connectorStyle != StepConnectorStyle.None) {
                        StepConnector(
                            isCompleted = step.isCompleted,
                            config = config,
                            colors = colors,
                            style = connectorStyle,
                            orientation = StepperOrientation.Horizontal,
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Vertical Stepper - Quick vertical layout
 */
@Composable
fun VerticalStepper(
    steps: List<StepData>,
    currentStep: Int,
    modifier: Modifier = Modifier,
    indicatorType: StepIndicatorType = StepIndicatorType.CheckmarkNumber,
    onStepClick: ((Int) -> Unit)? = null
) {
    PixaStepper(
        steps = steps,
        currentStep = currentStep,
        modifier = modifier,
        orientation = StepperOrientation.Vertical,
        indicatorType = indicatorType,
        onStepClick = onStepClick
    )
}

/**
 * Horizontal Stepper - Quick horizontal layout
 */
@Composable
fun HorizontalStepper(
    steps: List<StepData>,
    currentStep: Int,
    modifier: Modifier = Modifier,
    indicatorType: StepIndicatorType = StepIndicatorType.Number,
    onStepClick: ((Int) -> Unit)? = null
) {
    PixaStepper(
        steps = steps,
        currentStep = currentStep,
        modifier = modifier,
        orientation = StepperOrientation.Horizontal,
        indicatorType = indicatorType,
        onStepClick = onStepClick
    )
}

/**
 * Progress Header - Shows "Step X of Y" text
 */
@Composable
fun StepProgressHeader(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    BasicText(
        text = "Step ${currentStep + 1} of $totalSteps",
        style = AppTheme.typography.subtitleRegular.copy(color = AppTheme.colors.baseContentBody),
        modifier = modifier
    )
}

// ════════════════════════════════════════════════════════════════════════════
// USAGE EXAMPLES
// ════════════════════════════════════════════════════════════════════════════

// Usage examples omitted — see DOCUMENTATION.md for runnable snippets.
