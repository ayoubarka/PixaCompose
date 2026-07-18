package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class RadioButtonVariant {
    Filled,
    Outlined,
    Ghost
}

/**
 * RadioButton Label Position
 */
enum class RadioButtonLabelPosition {
    /** Label appears before the radio button */
    Start,
    /** Label appears after the radio button */
    End
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class RadioButtonSizeConfig(
    val outerCircleSize: Dp,
    val innerCircleSize: Dp,
    val borderWidth: Dp,
    val labelSpacing: Dp,
    val labelStyle: @Composable () -> TextStyle
)

@Immutable
@Stable
data class RadioButtonColors(
    val outerCircle: Color,
    val outerBorder: Color,
    val innerCircle: Color,
    val label: Color
)

@Immutable
@Stable
data class RadioButtonStateColors(
    val unselected: RadioButtonColors,
    val selected: RadioButtonColors,
    val disabled: RadioButtonColors
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Border width is fixed at [HierarchicalSize.Border.Compact] (1px) across
 * all size tiers. [innerCircleSize] is derived as half the outer diameter.
 * [SizeVariant] scales outer circle size, corner radius, and typography.
 */
@Composable
private fun getRadioButtonSizeConfig(size: SizeVariant): RadioButtonSizeConfig {
    val typography = AppTheme.typography
    val outerCircleSize = when (size) {
        SizeVariant.Small -> HierarchicalSize.Icon.Compact
        SizeVariant.Large -> HierarchicalSize.Icon.Medium
        else -> HierarchicalSize.Icon.Small
    }
    val labelStyle: @Composable () -> TextStyle = when (size) {
        SizeVariant.Small -> ({ typography.bodyBold })
        SizeVariant.Large -> ({ typography.bodyLight })
        else -> ({ typography.bodyRegular })
    }
    val labelSpacing = when (size) {
        SizeVariant.Small -> HierarchicalSize.Spacing.Nano
        else -> HierarchicalSize.Spacing.Small
    }
    return RadioButtonSizeConfig(
        outerCircleSize = outerCircleSize,
        innerCircleSize = outerCircleSize / 2f,
        borderWidth = HierarchicalSize.Border.Compact,
        labelSpacing = labelSpacing,
        labelStyle = labelStyle
    )
}

/**
 * Get radio button colors based on variant.
 *
 * [RadioButtonVariant.Outlined] is the default; [Filled]/[Ghost] are
 * alternative style extensions.
 */
@Composable
private fun getRadioButtonTheme(
    variant: RadioButtonVariant,
    colors: ColorPalette
): RadioButtonStateColors {
    return when (variant) {
        RadioButtonVariant.Filled -> RadioButtonStateColors(
            unselected = RadioButtonColors(
                outerCircle = Color.Transparent,
                outerBorder = colors.baseBorderDefault,
                innerCircle = Color.Transparent,
                label = colors.baseContentBody
            ),
            selected = RadioButtonColors(
                outerCircle = colors.brandSurfaceDefault,
                outerBorder = colors.brandBorderDefault,
                innerCircle = colors.brandContentDefault,
                label = colors.baseContentBody
            ),
            disabled = RadioButtonColors(
                outerCircle = colors.baseSurfaceDisabled,
                outerBorder = colors.baseBorderDisabled,
                innerCircle = colors.baseContentDisabled,
                label = colors.baseContentDisabled
            )
        )
        RadioButtonVariant.Outlined -> RadioButtonStateColors(
            unselected = RadioButtonColors(
                outerCircle = Color.Transparent,
                outerBorder = colors.baseBorderDefault,
                innerCircle = Color.Transparent,
                label = colors.baseContentBody
            ),
            selected = RadioButtonColors(
                outerCircle = Color.Transparent,
                outerBorder = colors.brandBorderFocus,
                innerCircle = colors.brandContentDefault,
                label = colors.baseContentBody
            ),
            disabled = RadioButtonColors(
                outerCircle = Color.Transparent,
                outerBorder = colors.baseBorderDisabled,
                innerCircle = colors.baseContentDisabled,
                label = colors.baseContentDisabled
            )
        )
        RadioButtonVariant.Ghost -> RadioButtonStateColors(
            unselected = RadioButtonColors(
                outerCircle = Color.Transparent,
                outerBorder = Color.Transparent,
                innerCircle = Color.Transparent,
                label = colors.baseContentBody
            ),
            selected = RadioButtonColors(
                outerCircle = Color.Transparent,
                outerBorder = Color.Transparent,
                innerCircle = colors.brandContentDefault,
                label = colors.baseContentBody
            ),
            disabled = RadioButtonColors(
                outerCircle = Color.Transparent,
                outerBorder = Color.Transparent,
                innerCircle = colors.baseContentDisabled,
                label = colors.baseContentDisabled
            )
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL RADIOBUTTON
// ════════════════════════════════════════════════════════════════════════════

/**
 * Base RadioButton circle implementation.
 *
 * Hover/pressed use fixed black/white alpha scrims (4%/8% black unselected,
 * 10%/20% white selected). Focus renders a 3px ([HierarchicalSize.Border.Large])
 * outline in `brandBorderFocus` in place of the normal state border.
 */
@Composable
private fun PixaRadioButtonCircle(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean,
    isError: Boolean,
    isHovered: Boolean,
    isPressed: Boolean,
    isFocused: Boolean,
    sizeConfig: RadioButtonSizeConfig,
    colors: RadioButtonStateColors
) {
    val errorColors = AppTheme.colors
    val currentColors = when {
        !enabled -> colors.disabled
        isError -> RadioButtonColors(
            outerCircle = if (selected) errorColors.errorSurfaceDefault else Color.Transparent,
            outerBorder = errorColors.errorBorderDefault,
            innerCircle = if (selected) errorColors.errorContentDefault else Color.Transparent,
            label = colors.selected.label
        )
        selected -> colors.selected
        else -> colors.unselected
    }

    val animatedOuterColor by animateColorAsState(
        targetValue = currentColors.outerCircle,
        animationSpec = AnimationUtils.colorSpring
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = currentColors.outerBorder,
        animationSpec = AnimationUtils.colorSpring
    )

    val animatedInnerColor by animateColorAsState(
        targetValue = currentColors.innerCircle,
        animationSpec = AnimationUtils.colorSpring
    )

    val innerCircleScale by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = AnimationUtils.selectionSpring
    )

    val overlayColor = when {
        !enabled -> Color.Transparent
        isPressed -> if (selected) Color.White.copy(alpha = 0.20f) else Color.Black.copy(alpha = 0.08f)
        isHovered -> if (selected) Color.White.copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.04f)
        else -> Color.Transparent
    }
    val effectiveBorderWidth = if (isFocused && enabled) HierarchicalSize.Border.Large else sizeConfig.borderWidth
    val effectiveBorderColor = if (isFocused && enabled) errorColors.brandBorderFocus else animatedBorderColor

    Box(
        modifier = modifier
            .size(sizeConfig.outerCircleSize)
            .clip(CircleShape)
            .background(animatedOuterColor)
            .background(overlayColor)
            .border(
                width = effectiveBorderWidth,
                color = effectiveBorderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size
            val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
            val innerRadius = (sizeConfig.innerCircleSize.toPx() / 2f) * innerCircleScale

            if (innerCircleScale > 0f) {
                drawCircle(
                    color = animatedInnerColor,
                    radius = innerRadius,
                    center = center
                )
            }
        }
    }
}

/**
 * Base RadioButton implementation.
 *
 * Uses [Modifier.selectable] (role = [Role.RadioButton]) so the selected
 * value is announced programmatically to assistive tech. The whole row
 * (circle + label) is the click target with a 48px minimum height
 * ([HierarchicalSize.TouchTarget.Small]).
 */
@Composable
private fun PixaRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: (() -> Unit)?,
    enabled: Boolean,
    isError: Boolean,
    label: String?,
    description: String?,
    labelPosition: RadioButtonLabelPosition,
    sizeConfig: RadioButtonSizeConfig,
    colors: RadioButtonStateColors
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val clickModifier = if (enabled && onClick != null) {
        Modifier.selectable(
            selected = selected,
            interactionSource = interactionSource,
            indication = pixaRipple(
                bounded = false,
                radius = sizeConfig.outerCircleSize
            ),
            role = Role.RadioButton,
            onClick = onClick
        )
    } else Modifier

    val labelColor = if (enabled) colors.selected.label else colors.disabled.label

    val labelText = @Composable { text: String ->
        val animatedLabelColor by animateColorAsState(labelColor, AnimationUtils.colorSpring, label = "radio_label")
        if (description != null) {
            Column {
                BasicText(
                    text = text,
                    style = sizeConfig.labelStyle().copy(color = animatedLabelColor)
                )
                BasicText(
                    text = description,
                    style = AppTheme.typography.captionRegular.copy(color = AppTheme.colors.baseContentCaption),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            BasicText(
                text = text,
                style = sizeConfig.labelStyle().copy(color = animatedLabelColor)
            )
        }
    }

    val content = @Composable {
        PixaRadioButtonCircle(
            selected = selected,
            enabled = enabled,
            isError = isError,
            isHovered = isHovered,
            isPressed = isPressed,
            isFocused = isFocused,
            sizeConfig = sizeConfig,
            colors = colors
        )

        label?.let {
            Spacer(modifier = Modifier.width(sizeConfig.labelSpacing))
            labelText(it)
        }
    }

    Row(
        modifier = modifier
            .sizeIn(minHeight = HierarchicalSize.TouchTarget.Small)
            .then(clickModifier)
            .focusable(enabled = enabled, interactionSource = interactionSource),
        // Text wraps beneath the radio with control and first line top-aligned.
        horizontalArrangement = if (labelPosition == RadioButtonLabelPosition.End) {
            Arrangement.Start
        } else {
            Arrangement.End
        },
        verticalAlignment = Alignment.Top
    ) {
        if (labelPosition == RadioButtonLabelPosition.Start && label != null) {
            labelText(label)
            Spacer(modifier = Modifier.width(sizeConfig.labelSpacing))
        }

        content()
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * RadioButton — Single-selection input control for choosing one option from a set.
 *
 * ### Anatomy
 * Circular container (1px inside-aligned border) + filled inner circle on
 * selection. Label/description sits beside the circle as part of the click target.
 *
 * ### Variants
 * [RadioButtonVariant.Outlined] (default), [Filled], [Ghost].
 *
 * ### States
 * Default, disabled, hover/pressed scrims, 3px focus outline, error.
 *
 * ### Sizing
 * [size] scales outer circle, label style, and spacing. Border width is
 * fixed at 1px.
 *
 * ### Usage
 * - Always pre-select one option in a group
 * - Prefer vertical stacking ([RadioGroup]) over horizontal
 * - Keep groups to ≤5 options; use a dropdown for larger sets
 *
 * @param selected Whether this radio button is selected
 * @param onClick Callback when clicked (null for read-only)
 * @param modifier Modifier
 * @param enabled Whether enabled
 * @param isError Whether to show error state
 * @param label Optional text label
 * @param description Optional descriptive text below the label
 * @param labelPosition Position of the label (Start or End)
 * @param variant Visual style (Outlined, Filled, Ghost)
 * @param size Size variant (Small, Medium, Large)
 *
 * @sample
 * ```
 * var selectedOption by remember { mutableStateOf("option1") }
 * RadioButton(
 *     selected = selectedOption == "option1",
 *     onClick = { selectedOption = "option1" },
 *     label = "Option 1"
 * )
 * ```
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String? = null,
    description: String? = null,
    labelPosition: RadioButtonLabelPosition = RadioButtonLabelPosition.End,
    variant: RadioButtonVariant = RadioButtonVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium
) {
    val themeColors = getRadioButtonTheme(variant, AppTheme.colors)
    val sizeConfig = getRadioButtonSizeConfig(size)

    PixaRadioButton(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        isError = isError,
        label = label,
        description = description,
        labelPosition = labelPosition,
        sizeConfig = sizeConfig,
        colors = themeColors
    )
}

/**
 * RadioGroup — Vertical container for managing radio button selections.
 *
 * Stacks radio buttons vertically. Recommended over horizontal layout for
 * clarity. Keep groups to ≤5 options; use a dropdown for larger sets.
 *
 * @param options List of option values
 * @param selectedOption Currently selected option
 * @param onOptionSelected Callback when an option is selected
 * @param modifier Modifier
 * @param enabled Whether enabled
 * @param optionLabel Function to get label for each option
 * @param variant Visual style for all buttons
 * @param size Size for all buttons
 * @param verticalArrangement Spacing between radio buttons
 */
@Composable
fun <T> RadioGroup(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    optionLabel: (T) -> String = { it.toString() },
    variant: RadioButtonVariant = RadioButtonVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        options.forEach { option ->
            RadioButton(
                selected = option == selectedOption,
                onClick = { onOptionSelected(option) },
                enabled = enabled,
                isError = isError,
                label = optionLabel(option),
                variant = variant,
                size = size
            )
        }
    }
}

/**
 * Horizontal RadioGroup — Row layout for radio buttons.
 *
 * Horizontal radio layout can make it harder to associate labels with controls.
 * Prefer [RadioGroup] (vertical) by default. Kept for cases that accept this
 * tradeoff (e.g. compact rating scales).
 */
@Composable
fun <T> HorizontalRadioGroup(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    optionLabel: (T) -> String = { it.toString() },
    variant: RadioButtonVariant = RadioButtonVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            RadioButton(
                selected = option == selectedOption,
                onClick = { onOptionSelected(option) },
                enabled = enabled,
                isError = isError,
                label = optionLabel(option),
                variant = variant,
                size = size
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Filled RadioButton — solid-container radio style (Pixa extension).
 */
@Composable
fun FilledRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String? = null,
    description: String? = null,
    size: SizeVariant = SizeVariant.Medium
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isError = isError,
        label = label,
        description = description,
        variant = RadioButtonVariant.Filled,
        size = size
    )
}

/**
 * Labeled RadioButton — Radio button with required label
 */
@Composable
fun LabeledRadioButton(
    label: String,
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    description: String? = null,
    labelPosition: RadioButtonLabelPosition = RadioButtonLabelPosition.End,
    variant: RadioButtonVariant = RadioButtonVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isError = isError,
        label = label,
        description = description,
        labelPosition = labelPosition,
        variant = variant,
        size = size
    )
}

// ════════════════════════════════════════════════════════════════════════════
// USAGE EXAMPLES
// ════════════════════════════════════════════════════════════════════════════

/**
 * USAGE EXAMPLES:
 *
 * 1. Simple radio group:
 * ```
 * var selectedSize by remember { mutableStateOf("Medium") }
 * Column {
 *     RadioButton(selected = selectedSize == "Small", onClick = { selectedSize = "Small" }, label = "Small")
 *     RadioButton(selected = selectedSize == "Medium", onClick = { selectedSize = "Medium" }, label = "Medium")
 *     RadioButton(selected = selectedSize == "Large", onClick = { selectedSize = "Large" }, label = "Large")
 * }
 * ```
 *
 * 2. Using RadioGroup helper:
 * ```
 * var selectedOption by remember { mutableStateOf("option1") }
 * RadioGroup(
 *     options = listOf("option1", "option2", "option3"),
 *     selectedOption = selectedOption,
 *     onOptionSelected = { selectedOption = it },
 *     optionLabel = { it.capitalize() }
 * )
 * ```
 *
 * 3. Form with radio buttons:
 * ```
 * Column {
 *     RadioGroup(
 *         options = listOf("Credit Card", "PayPal", "Bank Transfer"),
 *         selectedOption = paymentMethod,
 *         onOptionSelected = { paymentMethod = it }
 *     )
 * }
 * ```
 *
 * 4. Disabled radio button:
 * ```
 * RadioButton(
 *     selected = true,
 *     onClick = null,
 *     enabled = false,
 *     label = "Unavailable option"
 * )
 * ```
 *
 * 5. Filled variant:
 * ```
 * FilledRadioButton(
 *     selected = selected,
 *     onClick = { /* handle */ },
 *     label = "Emphasized option",
 *     size = SizeVariant.Small
 * )
 * ```
 *
 * 6. Custom enum radio group:
 * ```
 * enum class Theme { Light, Dark, Auto }
 * var theme by remember { mutableStateOf(Theme.Auto) }
 * RadioGroup(
 *     options = Theme.values().toList(),
 *     selectedOption = theme,
 *     onOptionSelected = { theme = it },
 *     optionLabel = { it.name }
 * )
 * ```
 */
