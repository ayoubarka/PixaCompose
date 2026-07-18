package com.pixamob.pixacompose.components.inputs

/**
 * QuantityStepper — Numeric increment/decrement input control.
 *
 * A numeric input letting users increment/decrement a value via plus/minus
 * buttons (passenger count, order item count), with a formatting hook for
 * temporal values (duration/time pickers).
 *
 * ### Anatomy
 * Left button (decrement) + center value display + right button (increment),
 * spaced 16px apart. Buttons are plain circles with plus/minus glyphs.
 *
 * ### Variants
 * - [QuantityStepperVariant.Narrow]: buttons hug the value display (compact inline group)
 * - [QuantityStepperVariant.Wide]: buttons pin to the row's edges, value centered
 * - [TimeQuantityStepper]: formatting wrapper over [QuantityStepper] via `valueLabel`
 *
 * ### States
 * Enabled, Focus (3px accent border around the whole component), Disabled,
 * Preloading ([Skeleton] placeholder), [isError].
 *
 * ### Sizing
 * [SizeVariant] resolves to [HierarchicalSize.Button] tiers (Medium = 36dp).
 * Value typography fixed to `labelLarge` regardless of size.
 *
 * ### Customization
 * [variant], [size], [min]/[max]/[step] bounds (required), [valueLabel],
 * [isError]. No decimal steps (Int value type).
 *
 * ### Usage rules
 * - `min`/`max` are required parameters
 * - Whole numbers only
 * - Screen-reader focus wraps the whole component, not individual buttons
 */

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class QuantityStepperVariant {
    /** Buttons hug the value display — spec: "for constrained layouts." */
    Narrow,

    /** Buttons pin to the row's edges, value centered — spec: "for expansive layouts." */
    Wide
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class QuantityStepperSizeConfig(
    val buttonSize: Dp,
    val glyphSize: Dp,
    val glyphStroke: Dp,
    val elementSpacing: Dp,
    val valueMinWidth: Dp
)

@Immutable
@Stable
data class QuantityStepperColors(
    val buttonBackground: Color,
    val buttonContent: Color,
    val valueContent: Color,
    val containerBackground: Color,
    val focusBorder: Color
)

@Immutable
@Stable
data class QuantityStepperStateColors(
    val enabled: QuantityStepperColors,
    val disabled: QuantityStepperColors,
    val error: QuantityStepperColors
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Button size maps 36dp to [HierarchicalSize.Button.Small]. [glyphSize]/[glyphStroke]
 * are derived as a fraction of [buttonSize]. [elementSpacing] fixed at 16dp
 * ([HierarchicalSize.Spacing.Large]) across all sizes.
 */
@Composable
private fun getQuantityStepperSizeConfig(size: SizeVariant): QuantityStepperSizeConfig {
    val buttonSize = when (size) {
        SizeVariant.Small -> HierarchicalSize.Button.Compact
        SizeVariant.Large -> HierarchicalSize.Button.Medium
        else -> HierarchicalSize.Button.Small
    }
    return QuantityStepperSizeConfig(
        buttonSize = buttonSize,
        glyphSize = buttonSize * GlyphSizeRatio,
        glyphStroke = HierarchicalSize.Stroke.Small,
        elementSpacing = HierarchicalSize.Spacing.Large,
        valueMinWidth = HierarchicalSize.Container.Compact
    )
}

/** Plus/minus glyph bounding box as a fraction of the button diameter. */
private const val GlyphSizeRatio = 0.4f

/**
 * Maps variant state colors to theme tokens. `error` is a Pixa extension
 * using existing error tokens.
 */
@Composable
private fun getQuantityStepperTheme(colors: ColorPalette): QuantityStepperStateColors {
    return QuantityStepperStateColors(
        enabled = QuantityStepperColors(
            buttonBackground = colors.baseSurfaceSubtle,
            buttonContent = colors.baseContentBody,
            valueContent = colors.baseContentBody,
            containerBackground = Color.Transparent,
            focusBorder = colors.accentBorderFocus
        ),
        disabled = QuantityStepperColors(
            buttonBackground = colors.baseSurfaceDisabled,
            buttonContent = colors.baseContentDisabled,
            valueContent = colors.baseContentDisabled,
            containerBackground = colors.baseSurfaceDisabled,
            focusBorder = Color.Transparent
        ),
        error = QuantityStepperColors(
            buttonBackground = colors.errorSurfaceDefault,
            buttonContent = colors.errorContentDefault,
            valueContent = colors.errorContentDefault,
            containerBackground = Color.Transparent,
            focusBorder = colors.errorBorderDefault
        )
    )
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL IMPLEMENTATION
// ════════════════════════════════════════════════════════════════════════════

/**
 * Plus/minus glyph button. Hidden from accessibility tree
 * ([hideFromAccessibility]) — [PixaQuantityStepper] carries merged semantics
 * so screen-reader focus wraps the whole component, not individual buttons.
 */
@Composable
private fun StepperGlyphButton(
    isIncrement: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    sizeConfig: QuantityStepperSizeConfig,
    background: Color,
    content: Color,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val animatedBackground by animateColorAsState(background, AnimationUtils.colorSpring, label = "stepper_button_bg")
    val animatedContent by animateColorAsState(content, AnimationUtils.colorSpring, label = "stepper_button_glyph")

    Box(
        modifier = modifier
            .size(sizeConfig.buttonSize)
            .clip(CircleShape)
            .background(animatedBackground)
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = pixaRipple(bounded = true, radius = sizeConfig.buttonSize / 2),
                        role = Role.Button,
                        onClick = onClick
                    )
                } else Modifier
            )
            .semantics { hideFromAccessibility() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeConfig.glyphSize)) {
            val strokeWidthPx = sizeConfig.glyphStroke.toPx()
            val midY = size.height / 2f
            drawLine(
                color = animatedContent,
                start = Offset(0f, midY),
                end = Offset(size.width, midY),
                strokeWidth = strokeWidthPx,
                cap = StrokeCap.Round
            )
            if (isIncrement) {
                val midX = size.width / 2f
                drawLine(
                    color = animatedContent,
                    start = Offset(midX, 0f),
                    end = Offset(midX, size.height),
                    strokeWidth = strokeWidthPx,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Base QuantityStepper implementation.
 *
 * Uses `Modifier.semantics(mergeDescendants = true)` with [stateDescription]
 * and two [CustomAccessibilityAction]s so assistive tech can adjust the value
 * without focusing individual buttons. Focus renders a 3px accent border
 * outline around the whole container.
 */
@Composable
private fun PixaQuantityStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int,
    max: Int,
    step: Int,
    enabled: Boolean,
    isError: Boolean,
    variant: QuantityStepperVariant,
    valueLabel: (Int) -> String,
    contentDescription: String,
    sizeConfig: QuantityStepperSizeConfig,
    colors: QuantityStepperStateColors,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val currentColors = when {
        !enabled -> colors.disabled
        isError -> colors.error
        else -> colors.enabled
    }

    val canDecrement = enabled && value > min
    val canIncrement = enabled && value < max

    val decrement: () -> Unit = { if (canDecrement) onValueChange((value - step).coerceAtLeast(min)) }
    val increment: () -> Unit = { if (canIncrement) onValueChange((value + step).coerceAtMost(max)) }

    val shape = RoundedCornerShape(HierarchicalSize.Radius.Medium)
    val effectiveBorderWidth = if (isFocused && enabled) HierarchicalSize.Border.Large else HierarchicalSize.Border.None
    val effectiveBorderColor = if (isFocused && enabled) currentColors.focusBorder else Color.Transparent

    val rowModifier = modifier
        .then(if (variant == QuantityStepperVariant.Wide) Modifier.fillMaxWidth() else Modifier.wrapContentWidth())
        .clip(shape)
        .background(currentColors.containerBackground)
        .border(effectiveBorderWidth, effectiveBorderColor, shape)
        .padding(HierarchicalSize.Spacing.Compact)
        .focusable(enabled = enabled, interactionSource = interactionSource)
        .semantics(mergeDescendants = true) {
            this.contentDescription = "$contentDescription. ${valueLabel(value)}"
            this.stateDescription = valueLabel(value)
            this.role = Role.Button
            this.customActions = listOf(
                CustomAccessibilityAction("Increase") { if (canIncrement) { increment(); true } else false },
                CustomAccessibilityAction("Decrease") { if (canDecrement) { decrement(); true } else false }
            )
        }

    Row(
        modifier = rowModifier,
        horizontalArrangement = if (variant == QuantityStepperVariant.Wide) {
            Arrangement.SpaceBetween
        } else {
            Arrangement.spacedBy(sizeConfig.elementSpacing)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperGlyphButton(
            isIncrement = false,
            enabled = canDecrement,
            onClick = decrement,
            sizeConfig = sizeConfig,
            background = currentColors.buttonBackground,
            content = currentColors.buttonContent
        )

        BasicText(
            text = valueLabel(value),
            style = AppTheme.typography.labelLarge.copy(color = currentColors.valueContent, textAlign = TextAlign.Center),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .widthIn(min = sizeConfig.valueMinWidth)
                .then(if (variant == QuantityStepperVariant.Narrow) Modifier.padding(horizontal = sizeConfig.elementSpacing) else Modifier)
        )

        StepperGlyphButton(
            isIncrement = true,
            enabled = canIncrement,
            onClick = increment,
            sizeConfig = sizeConfig,
            background = currentColors.buttonBackground,
            content = currentColors.buttonContent
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * QuantityStepper — Numeric increment/decrement input control.
 *
 * @param value Current numeric value
 * @param onValueChange Callback when the value changes via either button
 * @param min Minimum bound (inclusive, required)
 * @param max Maximum bound (inclusive, required)
 * @param modifier Modifier for the stepper
 * @param step Whole-number increment/decrement amount (default 1)
 * @param enabled Whether the stepper is interactive
 * @param isError Whether to show error state
 * @param isLoading Preloading state — renders a [Skeleton] placeholder
 * @param variant [QuantityStepperVariant.Narrow] (default) or `.Wide`
 * @param size Size variant (Small, Medium, Large)
 * @param valueLabel Formats the displayed value; defaults to plain digits
 * @param contentDescription Accessibility label for the value being adjusted
 *
 * @sample
 * ```
 * var passengers by remember { mutableStateOf(1) }
 * QuantityStepper(
 *     value = passengers,
 *     onValueChange = { passengers = it },
 *     min = 1,
 *     max = 6,
 *     contentDescription = "Passenger count"
 * )
 * ```
 */
@Composable
fun QuantityStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int,
    max: Int,
    modifier: Modifier = Modifier,
    step: Int = 1,
    enabled: Boolean = true,
    isError: Boolean = false,
    isLoading: Boolean = false,
    variant: QuantityStepperVariant = QuantityStepperVariant.Narrow,
    size: SizeVariant = SizeVariant.Medium,
    valueLabel: (Int) -> String = { it.toString() },
    contentDescription: String = "Quantity"
) {
    require(min <= max) { "QuantityStepper requires min ($min) <= max ($max)." }
    require(step > 0) { "QuantityStepper requires a positive step; got $step." }

    val sizeConfig = getQuantityStepperSizeConfig(size)

    if (isLoading) {
        Skeleton(
            modifier = modifier,
            width = if (variant == QuantityStepperVariant.Wide) null else sizeConfig.buttonSize * 3 + sizeConfig.elementSpacing * 2,
            height = sizeConfig.buttonSize,
            shape = RoundedCornerShape(HierarchicalSize.Radius.Medium),
            contentDescription = "$contentDescription loading"
        )
        return
    }

    val themeColors = getQuantityStepperTheme(AppTheme.colors)

    PixaQuantityStepper(
        value = value.coerceIn(min, max),
        onValueChange = onValueChange,
        min = min,
        max = max,
        step = step,
        enabled = enabled,
        isError = isError,
        variant = variant,
        valueLabel = valueLabel,
        contentDescription = contentDescription,
        sizeConfig = sizeConfig,
        colors = themeColors,
        modifier = modifier
    )
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Wide QuantityStepper — buttons pin to the row's edges.
 */
@Composable
fun WideQuantityStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int,
    max: Int,
    modifier: Modifier = Modifier,
    step: Int = 1,
    enabled: Boolean = true,
    size: SizeVariant = SizeVariant.Medium,
    contentDescription: String = "Quantity"
) {
    QuantityStepper(
        value = value,
        onValueChange = onValueChange,
        min = min,
        max = max,
        modifier = modifier,
        step = step,
        enabled = enabled,
        variant = QuantityStepperVariant.Wide,
        size = size,
        contentDescription = contentDescription
    )
}

/**
 * Time QuantityStepper — formatting wrapper over [QuantityStepper] for
 * duration values, displayed as minutes with a "min" suffix.
 */
@Composable
fun TimeQuantityStepper(
    minutes: Int,
    onMinutesChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minMinutes: Int = 0,
    maxMinutes: Int = 120,
    stepMinutes: Int = 5,
    enabled: Boolean = true,
    size: SizeVariant = SizeVariant.Medium
) {
    QuantityStepper(
        value = minutes,
        onValueChange = onMinutesChange,
        min = minMinutes,
        max = maxMinutes,
        modifier = modifier,
        step = stepMinutes,
        enabled = enabled,
        size = size,
        valueLabel = { "$it min" },
        contentDescription = "Duration in minutes"
    )
}

// ════════════════════════════════════════════════════════════════════════════
// USAGE EXAMPLES
// ════════════════════════════════════════════════════════════════════════════

/**
 * USAGE EXAMPLES:
 *
 * 1. Passenger count (narrow, default):
 * ```
 * var passengers by remember { mutableStateOf(1) }
 * QuantityStepper(
 *     value = passengers,
 *     onValueChange = { passengers = it },
 *     min = 1,
 *     max = 6,
 *     contentDescription = "Passenger count"
 * )
 * ```
 *
 * 2. Order item count (wide, full-row layout):
 * ```
 * var quantity by remember { mutableStateOf(1) }
 * WideQuantityStepper(
 *     value = quantity,
 *     onValueChange = { quantity = it },
 *     min = 0,
 *     max = 99,
 *     contentDescription = "Item quantity"
 * )
 * ```
 *
 * 3. Time/duration stepper:
 * ```
 * var durationMinutes by remember { mutableStateOf(15) }
 * TimeQuantityStepper(
 *     minutes = durationMinutes,
 *     onMinutesChange = { durationMinutes = it },
 *     minMinutes = 5,
 *     maxMinutes = 60,
 *     stepMinutes = 5
 * )
 * ```
 *
 * 4. Disabled / read-only:
 * ```
 * QuantityStepper(
 *     value = 3,
 *     onValueChange = {},
 *     min = 1,
 *     max = 5,
 *     enabled = false,
 *     contentDescription = "Guests"
 * )
 * ```
 *
 * 5. Loading placeholder:
 * ```
 * QuantityStepper(
 *     value = 0,
 *     onValueChange = {},
 *     min = 0,
 *     max = 10,
 *     isLoading = true,
 *     contentDescription = "Quantity"
 * )
 * ```
 */
