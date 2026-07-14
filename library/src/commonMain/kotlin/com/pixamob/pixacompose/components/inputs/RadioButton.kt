package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class RadioButtonVariant {
    Filled,
    Outlined,
    Ghost
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

/**
 * RadioButton State Colors
 */
@Immutable
@Stable
data class RadioButtonStateColors(
    val unselected: RadioButtonColors,
    val selected: RadioButtonColors,
    val disabled: RadioButtonColors
)

// ============================================================================
// SIZE CONFIGURATIONS
// ============================================================================

/**
 * Get size configuration for radio button
 */
@Composable
private fun getRadioButtonSizeConfig(size: SizeVariant): RadioButtonSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.Small -> RadioButtonSizeConfig(
            outerCircleSize = HierarchicalSize.Icon.Compact,
            innerCircleSize = HierarchicalSize.Spacing.Compact,
            borderWidth = HierarchicalSize.Border.Compact,
            labelSpacing = HierarchicalSize.Spacing.Nano,
            labelStyle = { typography.bodyBold }
        )
        SizeVariant.Medium -> RadioButtonSizeConfig(
            outerCircleSize = HierarchicalSize.Icon.Small,
            innerCircleSize = HierarchicalSize.Icon.Nano / 1.2f,
            borderWidth = HierarchicalSize.Border.Medium,
            labelSpacing = HierarchicalSize.Spacing.Small,
            labelStyle = { typography.bodyRegular }
        )
        SizeVariant.Large -> RadioButtonSizeConfig(
            outerCircleSize = HierarchicalSize.Icon.Medium,
            innerCircleSize = HierarchicalSize.Spacing.Small,
            borderWidth = HierarchicalSize.Border.Large,
            labelSpacing = HierarchicalSize.Spacing.Small,
            labelStyle = { typography.bodyLight }
        )
        else -> RadioButtonSizeConfig(
            outerCircleSize = HierarchicalSize.Icon.Small,
            innerCircleSize = HierarchicalSize.Icon.Nano / 1.2f,
            borderWidth = HierarchicalSize.Border.Medium,
            labelSpacing = HierarchicalSize.Spacing.Small,
            labelStyle = { typography.bodyRegular }
        )
    }
}

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get radio button colors based on variant
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

// ============================================================================
// BASE COMPONENT (Internal)
// ============================================================================

/**
 * Base RadioButton Circle implementation
 */
@Composable
private fun PixaRadioButtonCircle(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean,
    isError: Boolean,
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

    Box(
        modifier = modifier
            .size(sizeConfig.outerCircleSize)
            .clip(CircleShape)
            .background(animatedOuterColor)
            .border(
                width = sizeConfig.borderWidth,
                color = animatedBorderColor,
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
 * Base RadioButton implementation
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

    val clickModifier = if (enabled && onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(
                bounded = false,
                radius = sizeConfig.outerCircleSize
            ),
            role = Role.RadioButton,
            onClick = onClick
        )
    } else Modifier

    val labelContent = @Composable { label?.let { lbl ->
        val labelColor = if (enabled) colors.selected.label else colors.disabled.label
        val animatedLabelColor by animateColorAsState(labelColor, AnimationUtils.colorSpring, label = "radio_label")

        Spacer(modifier = Modifier.width(sizeConfig.labelSpacing))

        if (description != null) {
            Column {
                Text(
                    text = lbl,
                    style = sizeConfig.labelStyle(),
                    color = animatedLabelColor
                )
                Text(
                    text = description,
                    style = AppTheme.typography.captionRegular,
                    color = AppTheme.colors.baseContentCaption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            Text(
                text = lbl,
                style = sizeConfig.labelStyle(),
                color = animatedLabelColor
            )
        }
    } }

    val content = @Composable {
        PixaRadioButtonCircle(
            selected = selected,
            enabled = enabled,
            isError = isError,
            sizeConfig = sizeConfig,
            colors = colors
        )

        labelContent()
    }

    Row(
        modifier = modifier.then(clickModifier),
        horizontalArrangement = if (labelPosition == RadioButtonLabelPosition.End) {
            Arrangement.Start
        } else {
            Arrangement.End
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (labelPosition == RadioButtonLabelPosition.Start && label != null) {
            if (description != null) {
                Column {
                    Text(
                        text = label,
                        style = sizeConfig.labelStyle(),
                        color = if (enabled) colors.selected.label else colors.disabled.label
                    )
                    Text(
                        text = description,
                        style = AppTheme.typography.captionRegular,
                        color = AppTheme.colors.baseContentCaption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Text(
                    text = label,
                    style = sizeConfig.labelStyle(),
                    color = if (enabled) colors.selected.label else colors.disabled.label
                )
            }
            Spacer(modifier = Modifier.width(sizeConfig.labelSpacing))
        }

        content()
    }
}

// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * RadioButton Label Position
 */
enum class RadioButtonLabelPosition {
    /** Label appears before the radio button */
    Start,
    /** Label appears after the radio button */
    End
}

/**
 * RadioButton - Single selection input control
 *
 * A radio button component for selecting one option from a group. Should be used within
 * a RadioGroup for managing the selection state across multiple options.
 *
 * @param selected Whether this radio button is selected
 * @param onClick Callback when radio button is clicked (null for read-only)
 * @param modifier Modifier for the radio button
 * @param enabled Whether the radio button is enabled
 * @param isError Whether to show the radio button in error state
 * @param label Optional text label
 * @param description Optional descriptive text below the label
 * @param labelPosition Position of the label (Start or End)
 * @param variant Visual style (Filled or Outlined)
 * @param size Size variant (Small, Medium, Large)
 *
 * @sample
 * ```
 * // Basic radio button
 * var selectedOption by remember { mutableStateOf("option1") }
 * RadioButton(
 *     selected = selectedOption == "option1",
 *     onClick = { selectedOption = "option1" }
 * )
 *
 * // Radio button with label
 * RadioButton(
 *     selected = selectedOption == "option2",
 *     onClick = { selectedOption = "option2" },
 *     label = "Option 2"
 * )
 *
 * // Radio group
 * Column {
 *     RadioButton(selected = selected == 0, onClick = { selected = 0 }, label = "Small")
 *     RadioButton(selected = selected == 1, onClick = { selected = 1 }, label = "Medium")
 *     RadioButton(selected = selected == 2, onClick = { selected = 2 }, label = "Large")
 * }
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
    variant: RadioButtonVariant = RadioButtonVariant.Filled,
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
 * RadioGroup - Container for managing radio button selections
 *
 * Manages a group of radio buttons with a single selected value.
 *
 * @param options List of option values
 * @param selectedOption Currently selected option
 * @param onOptionSelected Callback when an option is selected
 * @param modifier Modifier for the group
 * @param enabled Whether the radio group is enabled
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
    variant: RadioButtonVariant = RadioButtonVariant.Filled,
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
 * Horizontal RadioGroup - Row layout for radio buttons
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
    variant: RadioButtonVariant = RadioButtonVariant.Filled,
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

// ============================================================================
// CONVENIENCE VARIANTS
// ============================================================================

/**
 * Outlined RadioButton - Subtle radio button style
 */
@Composable
fun OutlinedRadioButton(
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
        variant = RadioButtonVariant.Outlined,
        size = size
    )
}

/**
 * Labeled RadioButton - Radio button with required label
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
    variant: RadioButtonVariant = RadioButtonVariant.Filled,
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

// ============================================================================
// USAGE EXAMPLES
// ============================================================================

/**
 * USAGE EXAMPLES:
 *
 * 1. Simple radio group:
 * ```
 * var selectedSize by remember { mutableStateOf("Medium") }
 * Column {
 *     RadioButton(
 *         selected = selectedSize == "Small",
 *         onClick = { selectedSize = "Small" },
 *         label = "Small"
 *     )
 *     RadioButton(
 *         selected = selectedSize == "Medium",
 *         onClick = { selectedSize = "Medium" },
 *         label = "Medium"
 *     )
 *     RadioButton(
 *         selected = selectedSize == "Large",
 *         onClick = { selectedSize = "Large" },
 *         label = "Large"
 *     )
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
 * 3. Horizontal radio group:
 * ```
 * var rating by remember { mutableStateOf(3) }
 * HorizontalRadioGroup(
 *     options = listOf(1, 2, 3, 4, 5),
 *     selectedOption = rating,
 *     onOptionSelected = { rating = it },
 *     optionLabel = { "$it Star${if (it > 1) "s" else ""}" }
 * )
 * ```
 *
 * 4. Form with radio buttons:
 * ```
 * Column {
 *     Text("Select payment method:", style = MaterialTheme.typography.titleMedium)
 *     Spacer(modifier = Modifier.height(8.dp))
 *     RadioGroup(
 *         options = listOf("Credit Card", "PayPal", "Bank Transfer"),
 *         selectedOption = paymentMethod,
 *         onOptionSelected = { paymentMethod = it }
 *     )
 * }
 * ```
 *
 * 5. Disabled radio button:
 * ```
 * RadioButton(
 *     selected = true,
 *     onClick = null,
 *     enabled = false,
 *     label = "Unavailable option"
 * )
 * ```
 *
 * 6. Outlined variant:
 * ```
 * OutlinedRadioButton(
 *     selected = selected,
 *     onClick = { /* handle */ },
 *     label = "Subtle option",
 *     size = SizeVariant.Small
 * )
 * ```
 *
 * 7. Custom enum radio group:
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
