package com.pixamob.pixacompose.components.inputs

/**
 * QuantityStepper — PixaCompose's equivalent of Uber Base's "Stepper" component.
 *
 * Source: https://base.uber.com/6d2425e9f/p/53dd73-stepper.md
 *
 * Not to be confused with [com.pixamob.pixacompose.components.navigation.PixaStepper]
 * (Pixa's migration of Uber Base's separate "Progress Steps" spec) — the two
 * share only a name; this one is a numeric increment/decrement input, that
 * one is a multi-step wizard/itinerary indicator. No existing PixaCompose
 * component matched this spec, so it is new.
 *
 * Purpose:
 *   A numeric input letting users increment/decrement a value via plus/minus
 *   buttons (passenger count, order item count), with a formatting hook for
 *   temporal values (duration/time pickers).
 *
 * Anatomy:
 *   Left button (decrement) + center value display + right button (increment),
 *   spaced 16px apart (spec-fixed, not size-dependent) — see
 *   [QuantityStepperSizeConfig.elementSpacing]. Buttons are plain circles with
 *   plus/minus glyphs (no icon assets — drawn glyphs only, per spec's "text
 *   only, no icons" content rule).
 *
 * Variants:
 *   - [QuantityStepperVariant.Narrow]: buttons hug the value display (compact
 *     inline group) — spec: "constrained layouts."
 *   - [QuantityStepperVariant.Wide]: buttons pin to the row's edges, value
 *     centered — spec: "expansive layouts."
 *   - Time Stepper: not a separate control per spec's own "Stepper buttons
 *     follow button component behavior" note — implemented as [TimeQuantityStepper],
 *     a thin formatting wrapper over the same primitive via `valueLabel`.
 *
 * States: Enabled, Focus (3px `accentBorderFocus` around the *whole* component,
 *   not per-button — see [PixaQuantityStepper]), Disabled, Preloading (entire
 *   component swapped for a [Skeleton] placeholder). `isError` is a Pixa
 *   extension beyond the spec, kept for consistency with sibling inputs
 *   ([Checkbox], [RadioButton], `TextField`) that all expose it.
 *
 * Sizing: [SizeVariant] resolves to [HierarchicalSize.Button] tiers — Medium
 *   maps to `Button.Small` (36dp), an exact match for the spec's "Default
 *   button size: 36×36px small circles." Value typography stays fixed to
 *   `labelLarge` (spec: "LabelLarge... text styling fixed") regardless of size.
 *
 * Adaptive behavior: out of scope — the spec defines no responsive breakpoints
 *   beyond "extends to container width; values truncate," which [QuantityStepperVariant.Wide]
 *   plus the value display's `TextOverflow.Ellipsis` already covers.
 *
 * Customization: [variant], [size], [min]/[max]/[step] bounds (required, no
 *   unlimited-range default, per spec's anti-pattern), [valueLabel] formatting
 *   hook, [isError]. Not exposed: decimal steps (the `Int` value type
 *   structurally forbids them, per spec's "no decimals permitted"), per-button
 *   independent focus/color overrides (spec: "color palette derives from
 *   system tokens... limiting custom override potential").
 *
 * Usage rules preserved from spec:
 *   - `min`/`max` are required parameters (no unbounded default) — "there
 *     should be a limit to the minimum and maximum values."
 *   - Whole numbers only — enforced structurally via `Int`, not a runtime check.
 *   - Screen-reader focus wraps the whole component, not individual buttons —
 *     see [PixaQuantityStepper]'s merged semantics + [CustomAccessibilityAction]s.
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
 * Spec: "Default button size: 36×36px small circles" — an exact match for
 * [HierarchicalSize.Button.Small]. [SizeVariant] itself is a Pixa extension
 * (the spec shows one fixed size); [glyphSize]/[glyphStroke] are not
 * spec-dictated so are derived as a fixed fraction of [buttonSize] rather
 * than hand-picked per tier. [elementSpacing] stays fixed at
 * [HierarchicalSize.Spacing.Large] (16dp, exact spec match) across all sizes,
 * per the spec's flat "16px" figure — it is not part of the size ladder.
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

/** Plus/minus glyph bounding box as a fraction of the button diameter; spec shows the glyph but not a ratio. */
private const val GlyphSizeRatio = 0.4f

/**
 * Spec's states table names only content-role tokens (`contentPrimary`,
 * `backgroundTertiary`, `borderAccent`, `backgroundStateDisabled`,
 * `contentStateDisabled`) — no `isError` state at all. `error` below is a
 * Pixa extension mapped onto the library's existing error tokens, matching
 * how [Checkbox]/[RadioButton] extend their own specs the same way.
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
 * Plus/minus glyph button. Individually hidden from the accessibility tree
 * ([hideFromAccessibility]) per spec: "the focus state is around the whole
 * component instead of each button" — [PixaQuantityStepper] carries the
 * merged, adjustable semantics instead. Visual click/ripple behavior is
 * unaffected; only screen-reader traversal is collapsed to the parent.
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
 * without ever focusing an individual button — the closest achievable analog
 * in Compose's semantics API to the spec's VoiceOver "Adjustable, swipe up or
 * down" / TalkBack "Slider, use volume keys" behavior, which are platform
 * accessibility-service gestures outside Compose's direct control. Focus
 * renders a fixed 3px ([HierarchicalSize.Border.Large]) `accentBorderFocus`
 * outline around the whole container, per spec.
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
 * @param min Minimum bound (inclusive). Required — the spec calls unbounded
 *   ranges an anti-pattern, so there is no unlimited default.
 * @param max Maximum bound (inclusive). Required, same rationale as [min].
 * @param modifier Modifier for the stepper
 * @param step Whole-number increment/decrement amount (default 1). Must stay
 *   constant across the component's lifetime per spec.
 * @param enabled Whether the stepper is interactive
 * @param isError Whether to show the stepper in error state (Pixa extension,
 *   not in the spec — see [getQuantityStepperTheme])
 * @param isLoading Preloading state — replaces the entire component with a
 *   [Skeleton] placeholder, per spec
 * @param variant [QuantityStepperVariant.Narrow] (default, buttons hug the
 *   value) or `.Wide` (buttons pin to row edges)
 * @param size Size variant (Small, Medium, Large) — Medium (default) maps to
 *   the spec's exact 36×36px button size
 * @param valueLabel Formats the displayed value; defaults to plain digits.
 *   Override for temporal/unit-suffixed display — see [TimeQuantityStepper].
 * @param contentDescription Accessibility label describing what the value
 *   represents (e.g. "Passenger count"), read by screen readers alongside
 *   the current value
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
 * Wide QuantityStepper — buttons pin to the row's edges (spec: "for expansive layouts").
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
 * Time QuantityStepper — spec's "Time Stepper" variant, implemented as a
 * formatting wrapper over [QuantityStepper] rather than a separate control,
 * per the spec's own "stepper buttons follow button component behavior" note.
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
