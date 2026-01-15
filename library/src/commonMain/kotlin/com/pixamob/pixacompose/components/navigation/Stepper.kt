package com.pixamob.pixacompose.components.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import com.pixamob.pixacompose.components.display.PixaCard
import com.pixamob.pixacompose.components.display.BaseCardElevation
import com.pixamob.pixacompose.components.display.BaseCardPadding
import com.pixamob.pixacompose.components.display.BaseCardVariant
import com.pixamob.pixacompose.components.display.Icon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils

/**
 * Stepper Component
 *
 * Multi-step progress indicator for workflows, forms, and guided processes.
 * Displays steps with completed/current/pending states and smooth animations.
 *
 * Features:
 * - Vertical and Horizontal orientations
 * - Multiple indicator types: Dot, Number, Icon, Checkmark
 * - Connector styles: Line, Dashed, Arrow
 * - Card-based step content
 * - Interactive step navigation
 * - Animated state transitions
 * - Full accessibility support
 *
 * @sample
 * ```
 * // Basic vertical stepper
 * val steps = listOf(
 *     StepData(title = "Account Info", isCompleted = true),
 *     StepData(title = "Verification", isCompleted = false),
 *     StepData(title = "Confirm", isCompleted = false)
 * )
 * Stepper(
 *     steps = steps,
 *     currentStep = 1,
 *     orientation = StepperOrientation.Vertical
 * )
 *
 * // Horizontal with numbers
 * Stepper(
 *     steps = steps,
 *     currentStep = 2,
 *     orientation = StepperOrientation.Horizontal,
 *     indicatorType = StepIndicatorType.Number
 * )
 *
 * // Interactive with click handler
 * Stepper(
 *     steps = steps,
 *     currentStep = currentStep,
 *     onStepClick = { step -> if (step < currentStep) currentStep = step }
 * )
 * ```
 */

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Stepper orientation
 */
enum class StepperOrientation {
    /** Vertical layout - default for mobile */
    Vertical,
    /** Horizontal layout - for wide screens or few steps */
    Horizontal
}

/**
 * Step indicator type
 */
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
    IconNumber
}

/**
 * Connector style between steps
 */
enum class StepConnectorStyle {
    /** Solid line connector */
    Line,
    /** Dashed line connector */
    Dashed,
    /** Dashed line with short segments */
    DashedShort,
    /** Dashed line with long segments */
    DashedLong,
    /** Arrow connector */
    Arrow,
    /** No connector */
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
 * Stepper size presets
 */
enum class StepperSize {
    /** Small - 24dp indicator */
    Small,
    /** Medium - 32dp indicator (default) */
    Medium,
    /** Large - 40dp indicator */
    Large
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
    val completeLabel: String = "Completed",
    val currentLabel: String = "Current step",
    val pendingLabel: String = "Pending",
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
    val minTouchTarget: Dp = 44.dp,
    val animateConnector: Boolean = true
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
    val completedContent: Color,
    val currentContent: Color,
    val pendingContent: Color,
    val errorContent: Color,
    val connector: Color,
    val label: Color,
    val subLabel: Color
)

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get stepper configuration based on size
 */
@Composable
private fun getStepperConfig(size: StepperSize): StepperConfig {
    val typography = AppTheme.typography
    return when (size) {
        StepperSize.Small -> StepperConfig(
            indicatorSize = 24.dp,
            iconSize = 14.dp,
            connectorWidth = 40.dp,
            connectorThickness = 2.dp,
            titleStyle = { typography.bodyLight },
            subTitleStyle = { typography.captionRegular },
            numberStyle = { typography.bodyLight.copy(fontWeight = FontWeight.SemiBold) },
            spacing = Spacing.Small
        )
        StepperSize.Medium -> StepperConfig(
            indicatorSize = 32.dp,
            iconSize = 18.dp,
            connectorWidth = 48.dp,
            connectorThickness = 2.dp,
            titleStyle = { typography.bodyRegular },
            subTitleStyle = { typography.bodyLight },
            numberStyle = { typography.bodyRegular.copy(fontWeight = FontWeight.Bold) },
            spacing = Spacing.Medium
        )
        StepperSize.Large -> StepperConfig(
            indicatorSize = 40.dp,
            iconSize = 22.dp,
            connectorWidth = 56.dp,
            connectorThickness = 3.dp,
            titleStyle = { typography.bodyBold },
            subTitleStyle = { typography.bodyRegular },
            numberStyle = { typography.bodyBold.copy(fontWeight = FontWeight.Bold) },
            spacing = Spacing.Medium
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
        completedContent = colors.successContentDefault,
        currentContent = colors.brandContentDefault,
        pendingContent = colors.baseContentHint,
        errorContent = colors.errorContentDefault,
        connector = colors.baseBorderDefault,
        label = colors.baseContentBody,
        subLabel = colors.baseContentCaption
    )
}

// ============================================================================
// BASE COMPONENTS
// ============================================================================

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
        verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
    ) {
        Text(
            text = strings.headerFormat.replace("%d", (currentStep + 1).toString())
                .replaceFirst("%d", totalSteps.toString()),
            style = config.titleStyle(),
            color = colors.label,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = strings.percentCompleteFormat.replace("%d", percentComplete.toString()),
            style = config.subTitleStyle(),
            color = colors.subLabel
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
    onClick: (() -> Unit)?
) {
    val stepState = when {
        stepData.isError -> StepState.Error
        stepData.isCompleted -> StepState.Completed
        isCurrentStep -> StepState.Current
        else -> StepState.Pending
    }

    val backgroundColor by animateColorAsState(
        targetValue = when (stepState) {
            StepState.Completed -> colors.completed
            StepState.Current -> colors.current
            StepState.Error -> colors.error
            StepState.Pending -> colors.pending
        },
        animationSpec = AnimationUtils.standardTween(),
        label = "step_background"
    )

    val contentColor by animateColorAsState(
        targetValue = when (stepState) {
            StepState.Completed -> colors.completedContent
            StepState.Current -> colors.currentContent
            StepState.Error -> colors.errorContent
            StepState.Pending -> colors.pendingContent
        },
        animationSpec = AnimationUtils.standardTween(),
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

    Box(
        modifier = Modifier
            .size(config.indicatorSize)
            .scale(scale)
            .semantics {
                this.contentDescription = getStepAccessibilityDescription(
                    stepNumber,
                    stepData,
                    isCurrentStep
                )
                if (onClick != null) {
                    this.role = Role.Button
                }
            }
            .then(
                if (onClick != null && stepData.isClickable) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = ripple(bounded = false, radius = config.indicatorSize / 2),
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (stepState == StepState.Pending) 2.dp else 0.dp,
                color = borderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        when (indicatorType) {
            StepIndicatorType.Dot -> {
                // Empty - background color shows
            }
            StepIndicatorType.Number -> {
                Text(
                    text = stepNumber.toString(),
                    style = config.numberStyle(),
                    color = contentColor,
                    textAlign = TextAlign.Center
                )
            }
            StepIndicatorType.Icon -> {
                stepData.icon?.let { icon ->
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(config.iconSize)
                    )
                }
            }
            StepIndicatorType.CheckmarkNumber -> {
                if (stepData.isCompleted) {
                    // Animated checkmark
                    AnimatedCheckmark(
                        color = contentColor,
                        size = config.iconSize
                    )
                } else {
                    Text(
                        text = stepNumber.toString(),
                        style = config.numberStyle(),
                        color = contentColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
            StepIndicatorType.IconNumber -> {
                // Mixed: Show icon with number overlay
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    stepData.icon?.let { icon ->
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = contentColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(config.iconSize)
                        )
                    }
                    Text(
                        text = stepNumber.toString(),
                        style = config.numberStyle().copy(fontSize = config.numberStyle().fontSize * 0.7f),
                        color = contentColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            StepIndicatorType.Bar -> {
                // Bar is handled differently in horizontal layout - show dot here
            }
        }
    }
}

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
        val strokeWidth = 2.dp.toPx()

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
        targetValue = if (isCompleted && config.animateConnector) 1f else if (isCompleted) 1f else 0f,
        animationSpec = if (config.animateConnector) {
            AnimationUtils.standardSpring()
        } else {
            AnimationUtils.fastTween()
        },
        label = "connector_progress"
    )

    val connectorColor = if (isCompleted) colors.completed else colors.connector

    // Separator style - thin gray line
    val isSeparator = style == StepConnectorStyle.Separator
    val separatorColor = colors.connector.copy(alpha = 0.3f)
    val thickness = if (isSeparator) 1.dp else config.connectorThickness

    when (orientation) {
        StepperOrientation.Vertical -> {
            when (style) {
                StepConnectorStyle.Line, StepConnectorStyle.Separator -> {
                    Box(
                        modifier = modifier
                            .width(thickness)
                            .height(config.connectorWidth)
                    ) {
                        // Background connector
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (isSeparator) separatorColor else colors.connector)
                        )

                        // Animated progress (not for separator)
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
                    val dashInterval = when (style) {
                        StepConnectorStyle.DashedShort -> floatArrayOf(8f, 4f)
                        StepConnectorStyle.DashedLong -> floatArrayOf(16f, 8f)
                        else -> floatArrayOf(12f, 6f)
                    }

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
                    // Arrow connector for vertical
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
                        // Background connector
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (isSeparator) separatorColor else colors.connector)
                        )

                        // Animated progress (not for separator)
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
                    val dashInterval = when (style) {
                        StepConnectorStyle.DashedShort -> floatArrayOf(8f, 4f)
                        StepConnectorStyle.DashedLong -> floatArrayOf(16f, 8f)
                        else -> floatArrayOf(12f, 6f)
                    }

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
                    // Arrow connector for horizontal
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
            verticalArrangement = Arrangement.spacedBy(Spacing.Tiny)
        ) {
            Text(
                text = stepData.title,
                style = config.titleStyle(),
                color = if (isCurrentStep) colors.label else colors.subLabel,
                fontWeight = if (isCurrentStep) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (showSubLabels && stepData.subTitle != null) {
                Text(
                    text = stepData.subTitle,
                    style = config.subTitleStyle(),
                    color = colors.subLabel,
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
                elevation = if (isCurrentStep) BaseCardElevation.Low else BaseCardElevation.None,
                padding = BaseCardPadding.Small
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

// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * Stepper - Multi-step progress indicator
 *
 * @param steps List of step data
 * @param currentStep Current step index (0-based)
 * @param modifier Modifier for the stepper
 * @param orientation Vertical or Horizontal layout
 * @param indicatorType Type of step indicator
 * @param connectorStyle Style of connector between steps
 * @param contentStyle Style for step content
 * @param cardShape Shape for Card content style (Rounded/Arrow/Pointy)
 * @param size Size preset
 * @param showLabels Whether to show step labels
 * @param showSubLabels Whether to show step subtitles
 * @param showHeader Whether to show integrated header ("Step X of Y")
 * @param onStepClick Optional click handler for interactive navigation
 * @param isStepClickable Optional callback to determine if step is clickable
 * @param strings Localization strings
 */
@Composable
fun Stepper(
    steps: List<StepData>,
    currentStep: Int,
    modifier: Modifier = Modifier,
    orientation: StepperOrientation = StepperOrientation.Vertical,
    indicatorType: StepIndicatorType = StepIndicatorType.CheckmarkNumber,
    connectorStyle: StepConnectorStyle = StepConnectorStyle.Line,
    contentStyle: StepContentStyle = StepContentStyle.Simple,
    cardShape: StepCardShape = StepCardShape.Rounded,
    size: StepperSize = StepperSize.Medium,
    showLabels: Boolean = true,
    showSubLabels: Boolean = true,
    showHeader: Boolean = false,
    onStepClick: ((Int) -> Unit)? = null,
    isStepClickable: ((Int) -> Boolean)? = null,
    strings: StepperStrings = StepperStrings()
) {
    val config = getStepperConfig(size)
    val colors = getStepColors(AppTheme.colors)

    Column(modifier = modifier) {
        // Optional integrated header
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
            StepperOrientation.Horizontal -> {
                HorizontalStepper(
                    steps = steps,
                    currentStep = currentStep,
                    config = config,
                    colors = colors,
                    indicatorType = indicatorType,
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
        steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Indicator column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StepIndicator(
                        stepNumber = index + 1,
                        stepData = step,
                        isCurrentStep = index == currentStep,
                        config = config,
                        colors = colors,
                        indicatorType = indicatorType,
                        onClick = if (onStepClick != null && (isStepClickable?.invoke(index) != false) && step.isClickable) {
                            { onStepClick(index) }
                        } else null
                    )

                    // Connector to next step
                    if (index < steps.size - 1 && connectorStyle != StepConnectorStyle.None) {
                        Spacer(modifier = Modifier.height(Spacing.Tiny))
                        StepConnector(
                            isCompleted = step.isCompleted,
                            config = config,
                            colors = colors,
                            style = connectorStyle,
                            orientation = StepperOrientation.Vertical
                        )
                        Spacer(modifier = Modifier.height(Spacing.Tiny))
                    }
                }

                // Content
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
                // Each step takes equal weight
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
                            onClick = if (onStepClick != null && (isStepClickable?.invoke(index) != false) && step.isClickable) {
                                { onStepClick(index) }
                            } else null
                        )

                        if (showLabels) {
                            Spacer(modifier = Modifier.height(Spacing.Tiny))
                            Text(
                                text = step.title,
                                style = config.titleStyle(),
                                color = if (index == currentStep) colors.label else colors.subLabel,
                                fontWeight = if (index == currentStep) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Connector
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

// ============================================================================
// HELPER FUNCTIONS
// ============================================================================

/**
 * Step state enum
 */
private enum class StepState {
    Completed,
    Current,
    Error,
    Pending
}

/**
 * Get accessibility description for step
 */
private fun getStepAccessibilityDescription(
    stepNumber: Int,
    stepData: StepData,
    isCurrentStep: Boolean
): String {
    val state = when {
        stepData.isError -> "error"
        stepData.isCompleted -> "completed"
        isCurrentStep -> "current"
        else -> "pending"
    }
    return "Step $stepNumber: ${stepData.title}, $state"
}

// ============================================================================
// CONVENIENCE VARIANTS
// ============================================================================

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
    Stepper(
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
    Stepper(
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
    Text(
        text = "Step ${currentStep + 1} of $totalSteps",
        style = AppTheme.typography.subtitleRegular,
        color = AppTheme.colors.baseContentBody,
        modifier = modifier
    )
}

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * 1. Basic vertical stepper:
 * ```
 * val steps = listOf(
 *     StepData(title = "Account Information", isCompleted = true),
 *     StepData(title = "Verification", isCompleted = false),
 *     StepData(title = "Complete", isCompleted = false)
 * )
 * var currentStep by remember { mutableStateOf(1) }
 *
 * Column {
 *     StepProgressHeader(currentStep, steps.size)
 *     VerticalStepper(
 *         steps = steps,
 *         currentStep = currentStep
 *     )
 * }
 * ```
 *
 * 2. Horizontal with numbers:
 * ```
 * HorizontalStepper(
 *     steps = steps,
 *     currentStep = currentStep,
 *     indicatorType = StepIndicatorType.Number
 * )
 * ```
 *
 * 3. Interactive stepper with navigation:
 * ```
 * Stepper(
 *     steps = steps,
 *     currentStep = currentStep,
 *     onStepClick = { step ->
 *         // Allow going back to completed steps
 *         if (step < currentStep) {
 *             currentStep = step
 *         }
 *     }
 * )
 * ```
 *
 * 4. With icons and subtitles:
 * ```
 * val steps = listOf(
 *     StepData(
 *         title = "Choose Plan",
 *         subTitle = "Select your subscription",
 *         icon = rememberVectorPainter(Icons.Default.ShoppingCart),
 *         isCompleted = true
 *     ),
 *     StepData(
 *         title = "Payment",
 *         subTitle = "Enter card details",
 *         icon = rememberVectorPainter(Icons.Default.Payment),
 *         isCompleted = false
 *     )
 * )
 *
 * Stepper(
 *     steps = steps,
 *     currentStep = 1,
 *     indicatorType = StepIndicatorType.Icon,
 *     showSubLabels = true
 * )
 * ```
 *
 * 5. Card-style stepper:
 * ```
 * Stepper(
 *     steps = steps,
 *     currentStep = currentStep,
 *     contentStyle = StepContentStyle.Card,
 *     orientation = StepperOrientation.Vertical
 * )
 * ```
 *
 * 6. Dot indicators (minimal):
 * ```
 * Stepper(
 *     steps = steps,
 *     currentStep = currentStep,
 *     indicatorType = StepIndicatorType.Dot,
 *     showLabels = false,
 *     size = StepperSize.Small
 * )
 * ```
 *
 * 7. Form wizard with next button:
 * ```
 * var currentStep by remember { mutableStateOf(0) }
 * val steps = remember { /* your steps */ }
 *
 * Column(
 *     modifier = Modifier
 *         .fillMaxSize()
 *         .padding(16.dp),
 *     verticalArrangement = Arrangement.SpaceBetween
 * ) {
 *     Column {
 *         StepProgressHeader(currentStep, steps.size)
 *         Spacer(Modifier.height(16.dp))
 *         VerticalStepper(steps, currentStep)
 *         Spacer(Modifier.height(24.dp))
 *         // Your form content here
 *     }
 *
 *     Button(
 *         text = if (currentStep < steps.size - 1) "Next" else "Complete",
 *         onClick = {
 *             if (currentStep < steps.size - 1) {
 *                 steps[currentStep].isCompleted = true
 *                 currentStep++
 *             }
 *         }
 *     )
 * }
 * ```
 */

