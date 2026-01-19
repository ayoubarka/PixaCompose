package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.*

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Checkbox State - Selection state
 */
enum class CheckboxState {
    /** Not selected */
    Unchecked,
    /** Selected */
    Checked,
    /** Partially selected (for parent checkbox with mixed children) */
    Indeterminate
}

/**
 * Checkbox Variant - Visual style
 */
enum class CheckboxVariant {
    /** Filled background when checked (Primary style) */
    Filled,
    /** Outlined border with checkmark (Subtle style) */
    Outlined
}

/**
 * Checkbox Size - Box size variants
 */
enum class CheckboxSize {
    /** 16dp - Compact UIs, dense forms */
    Small,
    /** 20dp - DEFAULT, standard forms */
    Medium,
    /** 24dp - Touch-friendly, prominent selections */
    Large
}

/**
 * Checkbox Size Configuration
 */
@Immutable
@Stable
data class CheckboxSizeConfig(
    val boxSize: Dp,
    val cornerRadius: Dp,
    val borderWidth: Dp,
    val checkmarkStroke: Dp,
    val labelSpacing: Dp,
    val labelStyle: @Composable () -> TextStyle
)

/**
 * Checkbox Colors
 */
@Immutable
@Stable
data class CheckboxColors(
    val box: Color,
    val border: Color,
    val checkmark: Color,
    val label: Color
)

/**
 * Checkbox State Colors
 */
@Immutable
@Stable
data class CheckboxStateColors(
    val unchecked: CheckboxColors,
    val checked: CheckboxColors,
    val indeterminate: CheckboxColors,
    val disabled: CheckboxColors
)

// ============================================================================
// SIZE CONFIGURATIONS
// ============================================================================

/**
 * Get size configuration for checkbox
 */
@Composable
private fun getCheckboxSizeConfig(size: CheckboxSize): CheckboxSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        CheckboxSize.Small -> CheckboxSizeConfig(
            boxSize = IconSize.VerySmall,
            cornerRadius = RadiusSize.Tiny,
            borderWidth = BorderSize.Tiny,
            checkmarkStroke = BorderSize.SlightlyThicker,
            labelSpacing = Spacing.Micro,
            labelStyle = { typography.bodyBold }
        )
        CheckboxSize.Medium -> CheckboxSizeConfig(
            boxSize = IconSize.Small,
            cornerRadius = RadiusSize.Small,
            borderWidth = BorderSize.Standard,
            checkmarkStroke = BorderSize.Standard,
            labelSpacing = Spacing.Small,
            labelStyle = { typography.bodyRegular }
        )
        CheckboxSize.Large -> CheckboxSizeConfig(
            boxSize = IconSize.Medium,
            cornerRadius = RadiusSize.Small,
            borderWidth = BorderSize.Thick,
            checkmarkStroke = BorderSize.Medium,
            labelSpacing = Spacing.Small,
            labelStyle = { typography.bodyLight }
        )
    }
}

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get checkbox colors based on variant
 */
@Composable
private fun getCheckboxTheme(
    variant: CheckboxVariant,
    colors: ColorPalette
): CheckboxStateColors {
    return when (variant) {
        CheckboxVariant.Filled -> CheckboxStateColors(
            unchecked = CheckboxColors(
                box = Color.Transparent,
                border = colors.baseBorderDefault,
                checkmark = Color.Transparent,
                label = colors.baseContentBody
            ),
            checked = CheckboxColors(
                box = colors.brandSurfaceDefault,
                border = colors.brandBorderDefault,
                checkmark = colors.brandContentDefault,
                label = colors.baseContentBody
            ),
            indeterminate = CheckboxColors(
                box = colors.brandSurfaceDefault,
                border = colors.brandBorderDefault,
                checkmark = colors.brandContentDefault,
                label = colors.baseContentBody
            ),
            disabled = CheckboxColors(
                box = colors.baseSurfaceDisabled,
                border = colors.baseBorderDisabled,
                checkmark = colors.baseContentDisabled,
                label = colors.baseContentDisabled
            )
        )
        CheckboxVariant.Outlined -> CheckboxStateColors(
            unchecked = CheckboxColors(
                box = Color.Transparent,
                border = colors.baseBorderDefault,
                checkmark = Color.Transparent,
                label = colors.baseContentBody
            ),
            checked = CheckboxColors(
                box = Color.Transparent,
                border = colors.brandBorderFocus,
                checkmark = colors.brandContentDefault,
                label = colors.baseContentBody
            ),
            indeterminate = CheckboxColors(
                box = Color.Transparent,
                border = colors.brandBorderFocus,
                checkmark = colors.brandContentDefault,
                label = colors.baseContentBody
            ),
            disabled = CheckboxColors(
                box = Color.Transparent,
                border = colors.baseBorderDisabled,
                checkmark = colors.baseContentDisabled,
                label = colors.baseContentDisabled
            )
        )
    }
}

// ============================================================================
// BASE COMPONENT (Internal)
// ============================================================================

/**
 * Base Checkbox Box implementation
 */
@Composable
private fun PixaCheckboxBox(
    modifier: Modifier = Modifier,
    state: CheckboxState,
    enabled: Boolean,
    sizeConfig: CheckboxSizeConfig,
    colors: CheckboxStateColors
) {
    val currentColors = when {
        !enabled -> colors.disabled
        state == CheckboxState.Checked -> colors.checked
        state == CheckboxState.Indeterminate -> colors.indeterminate
        else -> colors.unchecked
    }

    val animatedBoxColor by animateColorAsState(
        targetValue = currentColors.box,
        animationSpec = tween(150)
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = currentColors.border,
        animationSpec = tween(150)
    )

    val animatedCheckmarkColor by animateColorAsState(
        targetValue = currentColors.checkmark,
        animationSpec = tween(150)
    )

    val checkmarkProgress by animateFloatAsState(
        targetValue = when (state) {
            CheckboxState.Checked, CheckboxState.Indeterminate -> 1f
            CheckboxState.Unchecked -> 0f
        },
        animationSpec = tween(200)
    )

    Box(
        modifier = modifier
            .size(sizeConfig.boxSize)
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(animatedBoxColor)
            .border(
                width = sizeConfig.borderWidth,
                color = animatedBorderColor,
                shape = RoundedCornerShape(sizeConfig.cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size
            val strokeWidth = sizeConfig.checkmarkStroke.toPx()

            if (state == CheckboxState.Indeterminate) {
                // Draw indeterminate line (horizontal bar)
                val lineY = canvasSize.height / 2f
                val lineStartX = canvasSize.width * 0.25f
                val lineEndX = canvasSize.width * 0.75f

                drawLine(
                    color = animatedCheckmarkColor,
                    start = Offset(lineStartX, lineY),
                    end = Offset(lineStartX + (lineEndX - lineStartX) * checkmarkProgress, lineY),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            } else if (state == CheckboxState.Checked) {
                // Draw checkmark
                val checkPath = Path().apply {
                    val startX = canvasSize.width * 0.25f
                    val startY = canvasSize.height * 0.5f
                    val midX = canvasSize.width * 0.42f
                    val midY = canvasSize.height * 0.68f
                    val endX = canvasSize.width * 0.75f
                    val endY = canvasSize.height * 0.32f

                    moveTo(startX, startY)
                    lineTo(midX, midY)
                    lineTo(endX, endY)
                }

                val pathMeasure = PathMeasure()
                pathMeasure.setPath(checkPath, false)
                val pathLength = pathMeasure.length

                val animatedPath = Path()
                pathMeasure.getSegment(0f, pathLength * checkmarkProgress, animatedPath, true)

                drawPath(
                    path = animatedPath,
                    color = animatedCheckmarkColor,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

/**
 * Base Checkbox implementation
 */
@Composable
private fun PixaCheckbox(
    modifier: Modifier = Modifier,
    state: CheckboxState,
    onCheckedChange: ((CheckboxState) -> Unit)?,
    enabled: Boolean,
    label: String?,
    labelPosition: CheckboxLabelPosition,
    sizeConfig: CheckboxSizeConfig,
    colors: CheckboxStateColors
) {
    val interactionSource = remember { MutableInteractionSource() }

    val clickModifier = if (enabled && onCheckedChange != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(
                bounded = false,
                radius = sizeConfig.boxSize
            ),
            role = Role.Checkbox,
            onClick = {
                val newState = when (state) {
                    CheckboxState.Unchecked, CheckboxState.Indeterminate -> CheckboxState.Checked
                    CheckboxState.Checked -> CheckboxState.Unchecked
                }
                onCheckedChange(newState)
            }
        )
    } else Modifier

    val content = @Composable {
        PixaCheckboxBox(
            state = state,
            enabled = enabled,
            sizeConfig = sizeConfig,
            colors = colors
        )

        if (label != null) {
            val labelColor = if (enabled) colors.checked.label else colors.disabled.label
            val animatedLabelColor by animateColorAsState(labelColor, tween(150))

            Spacer(modifier = Modifier.width(sizeConfig.labelSpacing))

            Text(
                text = label,
                style = sizeConfig.labelStyle(),
                color = animatedLabelColor
            )
        }
    }

    Row(
        modifier = modifier.then(clickModifier),
        horizontalArrangement = if (labelPosition == CheckboxLabelPosition.End) {
            Arrangement.Start
        } else {
            Arrangement.End
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (labelPosition == CheckboxLabelPosition.Start && label != null) {
            Text(
                text = label,
                style = sizeConfig.labelStyle(),
                color = if (enabled) colors.checked.label else colors.disabled.label
            )
            Spacer(modifier = Modifier.width(sizeConfig.labelSpacing))
        }

        content()
    }
}

// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * Checkbox Label Position
 */
enum class CheckboxLabelPosition {
    /** Label appears before the checkbox */
    Start,
    /** Label appears after the checkbox */
    End
}

/**
 * Checkbox - Multi-selection input control
 *
 * A checkbox component for binary selection (checked/unchecked) or tri-state selection
 * (checked/unchecked/indeterminate). Commonly used in forms, settings, and multi-select lists.
 *
 * @param checked Whether the checkbox is checked
 * @param onCheckedChange Callback when checkbox state changes (null for read-only)
 * @param modifier Modifier for the checkbox
 * @param enabled Whether the checkbox is enabled
 * @param label Optional text label
 * @param labelPosition Position of the label (Start or End)
 * @param variant Visual style (Filled or Outlined)
 * @param size Size variant (Small, Medium, Large)
 * @param state Explicit tri-state value (overrides checked parameter)
 *
 * @sample
 * ```
 * // Basic checkbox
 * var checked by remember { mutableStateOf(false) }
 * Checkbox(
 *     checked = checked,
 *     onCheckedChange = { checked = it }
 * )
 *
 * // Checkbox with label
 * Checkbox(
 *     checked = checked,
 *     onCheckedChange = { checked = it },
 *     label = "Accept terms and conditions"
 * )
 *
 * // Indeterminate checkbox (for "Select All" with partial selection)
 * Checkbox(
 *     state = CheckboxState.Indeterminate,
 *     onCheckedChange = { /* handle */ },
 *     label = "Select All"
 * )
 *
 * // Outlined variant
 * Checkbox(
 *     checked = checked,
 *     onCheckedChange = { checked = it },
 *     variant = CheckboxVariant.Outlined
 * )
 * ```
 */
@Composable
fun PixaCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
    labelPosition: CheckboxLabelPosition = CheckboxLabelPosition.End,
    variant: CheckboxVariant = CheckboxVariant.Filled,
    size: CheckboxSize = CheckboxSize.Medium,
    state: CheckboxState? = null
) {
    val themeColors = getCheckboxTheme(variant, AppTheme.colors)
    val sizeConfig = getCheckboxSizeConfig(size)

    val checkboxState = state ?: if (checked) CheckboxState.Checked else CheckboxState.Unchecked

    val stateChangeHandler: ((CheckboxState) -> Unit)? = onCheckedChange?.let { handler ->
        { newState ->
            handler(newState == CheckboxState.Checked)
        }
    }

    PixaCheckbox(
        modifier = modifier,
        state = checkboxState,
        onCheckedChange = stateChangeHandler,
        enabled = enabled,
        label = label,
        labelPosition = labelPosition,
        sizeConfig = sizeConfig,
        colors = themeColors
    )
}

/**
 * Tri-State Checkbox - Checkbox with indeterminate state support
 */
@Composable
fun TriStateCheckbox(
    state: CheckboxState,
    onStateChange: ((CheckboxState) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
    labelPosition: CheckboxLabelPosition = CheckboxLabelPosition.End,
    variant: CheckboxVariant = CheckboxVariant.Filled,
    size: CheckboxSize = CheckboxSize.Medium
) {
    val themeColors = getCheckboxTheme(variant, AppTheme.colors)
    val sizeConfig = getCheckboxSizeConfig(size)

    PixaCheckbox(
        modifier = modifier,
        state = state,
        onCheckedChange = onStateChange,
        enabled = enabled,
        label = label,
        labelPosition = labelPosition,
        sizeConfig = sizeConfig,
        colors = themeColors
    )
}

// ============================================================================
// CONVENIENCE VARIANTS
// ============================================================================

/**
 * Outlined Checkbox - Subtle checkbox style
 */
@Composable
fun OutlinedCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
    size: CheckboxSize = CheckboxSize.Medium
) {
    PixaCheckbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        label = label,
        variant = CheckboxVariant.Outlined,
        size = size
    )
}

/**
 * Labeled Checkbox - Checkbox with required label
 */
@Composable
fun LabeledCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelPosition: CheckboxLabelPosition = CheckboxLabelPosition.End,
    variant: CheckboxVariant = CheckboxVariant.Filled,
    size: CheckboxSize = CheckboxSize.Medium
) {
    PixaCheckbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        label = label,
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
 * 1. Simple checkbox with label:
 * ```
 * var agreed by remember { mutableStateOf(false) }
 * Checkbox(
 *     checked = agreed,
 *     onCheckedChange = { agreed = it },
 *     label = "I agree to the terms and conditions"
 * )
 * ```
 *
 * 2. Form with multiple checkboxes:
 * ```
 * Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
 *     Checkbox(
 *         checked = newsletter,
 *         onCheckedChange = { newsletter = it },
 *         label = "Subscribe to newsletter"
 *     )
 *     Checkbox(
 *         checked = updates,
 *         onCheckedChange = { updates = it },
 *         label = "Receive product updates"
 *     )
 *     OutlinedCheckbox(
 *         checked = offers,
 *         onCheckedChange = { offers = it },
 *         label = "Special offers (optional)",
 *         size = CheckboxSize.Small
 *     )
 * }
 * ```
 *
 * 3. CheckboxGroup for multi-select with "Select All":
 * ```
 * var selectedOptions by remember { mutableStateOf(setOf<String>()) }
 * CheckboxGroup(
 *     options = listOf("Option A", "Option B", "Option C", "Option D"),
 *     selected = selectedOptions,
 *     onSelectionChange = { selectedOptions = it },
 *     showSelectAll = true,
 *     selectAllLabel = "Select All Options"
 * )
 * ```
 *
 * 4. Manual tri-state checkbox (for hierarchical selection):
 * ```
 * val childStates = remember { mutableStateListOf(true, false, true) }
 * val allChecked = childStates.all { it }
 * val noneChecked = childStates.none { it }
 * val parentState = when {
 *     allChecked -> CheckboxState.Checked
 *     noneChecked -> CheckboxState.Unchecked
 *     else -> CheckboxState.Indeterminate
 * }
 *
 * TriStateCheckbox(
 *     state = parentState,
 *     onStateChange = { newState ->
 *         val shouldCheck = newState == CheckboxState.Checked
 *         childStates.replaceAll { shouldCheck }
 *     },
 *     label = "Parent: Select All Children",
 *     contentDescription = "Select all child items"
 * )
 *
 * Column(modifier = Modifier.padding(start = 24.dp)) {
 *     childStates.forEachIndexed { index, checked ->
 *         Checkbox(
 *             checked = checked,
 *             onCheckedChange = { childStates[index] = it },
 *             label = "Child $index",
 *             size = CheckboxSize.Small
 *         )
 *     }
 * }
 * ```
 *
 * 5. Disabled checkbox (read-only state):
 * ```
 * Checkbox(
 *     checked = true,
 *     onCheckedChange = null,
 *     enabled = false,
 *     label = "This option is permanently enabled"
 * )
 * ```
 *
 * 6. Custom colors for branded checkboxes:
 * ```
 * Checkbox(
 *     checked = isPremium,
 *     onCheckedChange = { isPremium = it },
 *     label = "Premium Feature",
 *     customColors = CheckboxStateColors(
 *         checked = CheckboxColors(
 *             box = Color(0xFFFFD700), // Gold
 *             border = Color(0xFFB8860B),
 *             checkmark = Color.White,
 *             label = AppTheme.colors.baseContentBody
 *         ),
 *         // ... other states
 *     )
 * )
 * ```
 *
 * 7. Compact checkbox group with custom data:
 * ```
 * data class Feature(val id: String, val name: String, val isPro: Boolean)
 * val features = listOf(
 *     Feature("f1", "Dark Mode", false),
 *     Feature("f2", "Cloud Sync", true),
 *     Feature("f3", "Offline Mode", false)
 * )
 * var enabledFeatures by remember { mutableStateOf(setOf<Feature>()) }
 *
 * CheckboxGroup(
 *     options = features,
 *     selected = enabledFeatures,
 *     onSelectionChange = { enabledFeatures = it },
 *     optionLabel = { "${it.name}${if (it.isPro) " (Pro)" else ""}" },
 *     size = CheckboxSize.Small,
 *     variant = CheckboxVariant.Outlined
 * )
 * ```
 */
