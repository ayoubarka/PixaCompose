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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class CheckboxState {
    Unchecked,
    Checked,
    Indeterminate
}

enum class CheckboxVariant {
    Filled,
    Outlined,
    Ghost
}

/**
 * Hierarchical checkbox tree item for nested selection groups.
 * Supports automatic parent indeterminate state computation from children.
 */
@Stable
data class CheckboxTreeItem<T>(
    val id: String,
    val label: String,
    val children: List<CheckboxTreeItem<T>> = emptyList(),
    val data: T? = null,
    val enabled: Boolean = true,
    val description: String? = null
) {
    val isLeaf: Boolean
        get() = children.isEmpty()
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

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

@Immutable
@Stable
data class CheckboxColors(
    val box: Color,
    val border: Color,
    val checkmark: Color,
    val label: Color
)

@Immutable
@Stable
data class CheckboxStateColors(
    val unchecked: CheckboxColors,
    val checked: CheckboxColors,
    val indeterminate: CheckboxColors,
    val disabled: CheckboxColors
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getCheckboxSizeConfig(size: SizeVariant): CheckboxSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.Small -> CheckboxSizeConfig(
            boxSize = HierarchicalSize.Icon.Compact,
            cornerRadius = HierarchicalSize.Radius.Nano,
            borderWidth = HierarchicalSize.Border.Compact,
            checkmarkStroke = 2.5.dp,
            labelSpacing = HierarchicalSize.Spacing.Nano,
            labelStyle = { typography.bodyBold }
        )
        SizeVariant.Medium -> CheckboxSizeConfig(
            boxSize = HierarchicalSize.Icon.Small,
            cornerRadius = HierarchicalSize.Radius.Small,
            borderWidth = HierarchicalSize.Border.Medium,
            checkmarkStroke = HierarchicalSize.Border.Medium,
            labelSpacing = HierarchicalSize.Spacing.Small,
            labelStyle = { typography.bodyRegular }
        )
        SizeVariant.Large -> CheckboxSizeConfig(
            boxSize = HierarchicalSize.Icon.Medium,
            cornerRadius = HierarchicalSize.Radius.Small,
            borderWidth = HierarchicalSize.Border.Large,
            checkmarkStroke = HierarchicalSize.Border.Medium,
            labelSpacing = HierarchicalSize.Spacing.Small,
            labelStyle = { typography.bodyLight }
        )
        else -> CheckboxSizeConfig(
            boxSize = HierarchicalSize.Icon.Small,
            cornerRadius = HierarchicalSize.Radius.Small,
            borderWidth = HierarchicalSize.Border.Medium,
            checkmarkStroke = HierarchicalSize.Border.Medium,
            labelSpacing = HierarchicalSize.Spacing.Small,
            labelStyle = { typography.bodyRegular }
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
        CheckboxVariant.Ghost -> CheckboxStateColors(
            unchecked = CheckboxColors(
                box = Color.Transparent,
                border = colors.baseBorderDefault,
                checkmark = Color.Transparent,
                label = colors.baseContentBody
            ),
            checked = CheckboxColors(
                box = Color.Transparent,
                border = Color.Transparent,
                checkmark = colors.brandContentDefault,
                label = colors.baseContentBody
            ),
            indeterminate = CheckboxColors(
                box = Color.Transparent,
                border = Color.Transparent,
                checkmark = colors.brandContentDefault,
                label = colors.baseContentBody
            ),
            disabled = CheckboxColors(
                box = Color.Transparent,
                border = Color.Transparent,
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
    isError: Boolean,
    sizeConfig: CheckboxSizeConfig,
    colors: CheckboxStateColors
) {
    val errorColors = AppTheme.colors

    val currentColors = when {
        !enabled -> colors.disabled
        isError && state == CheckboxState.Unchecked -> CheckboxColors(
            box = errorColors.errorSurfaceDefault.copy(alpha = 0.1f),
            border = errorColors.errorBorderDefault,
            checkmark = Color.Transparent,
            label = colors.unchecked.label
        )
        isError && (state == CheckboxState.Checked || state == CheckboxState.Indeterminate) -> CheckboxColors(
            box = errorColors.errorSurfaceDefault,
            border = errorColors.errorBorderDefault,
            checkmark = errorColors.errorContentDefault,
            label = colors.checked.label
        )
        state == CheckboxState.Checked -> colors.checked
        state == CheckboxState.Indeterminate -> colors.indeterminate
        else -> colors.unchecked
    }

    val currentBorder = currentColors.border
    val currentCheckmark = currentColors.checkmark

    val animatedBoxColor by animateColorAsState(
        targetValue = currentColors.box,
        animationSpec = AnimationUtils.colorSpring,
        label = "checkbox_box"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = currentBorder,
        animationSpec = AnimationUtils.colorSpring,
        label = "checkbox_border"
    )

    val animatedCheckmarkColor by animateColorAsState(
        targetValue = currentCheckmark,
        animationSpec = AnimationUtils.colorSpring,
        label = "checkbox_checkmark"
    )

    val checkmarkProgress by animateFloatAsState(
        targetValue = when (state) {
            CheckboxState.Checked, CheckboxState.Indeterminate -> 1f
            CheckboxState.Unchecked -> 0f
        },
        animationSpec = AnimationUtils.selectionSpring,
        label = "checkbox_progress"
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
    isError: Boolean,
    label: String?,
    description: String?,
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

    val labelContent = @Composable { label?.let { lbl ->
        val labelColor = if (enabled) colors.checked.label else colors.disabled.label
        val animatedLabelColor by animateColorAsState(labelColor, AnimationUtils.colorSpring, label = "checkbox_label")

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
        PixaCheckboxBox(
            state = state,
            enabled = enabled,
            isError = isError,
            sizeConfig = sizeConfig,
            colors = colors
        )

        labelContent()
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
            if (description != null) {
                Column {
                    Text(
                        text = label,
                        style = sizeConfig.labelStyle(),
                        color = if (enabled) colors.checked.label else colors.disabled.label
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
                    color = if (enabled) colors.checked.label else colors.disabled.label
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
    isError: Boolean = false,
    label: String? = null,
    description: String? = null,
    labelPosition: CheckboxLabelPosition = CheckboxLabelPosition.End,
    variant: CheckboxVariant = CheckboxVariant.Filled,
    size: SizeVariant = SizeVariant.Medium,
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
        isError = isError,
        label = label,
        description = description,
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
    isError: Boolean = false,
    label: String? = null,
    description: String? = null,
    labelPosition: CheckboxLabelPosition = CheckboxLabelPosition.End,
    variant: CheckboxVariant = CheckboxVariant.Filled,
    size: SizeVariant = SizeVariant.Medium
) {
    val themeColors = getCheckboxTheme(variant, AppTheme.colors)
    val sizeConfig = getCheckboxSizeConfig(size)

    PixaCheckbox(
        modifier = modifier,
        state = state,
        onCheckedChange = onStateChange,
        enabled = enabled,
        isError = isError,
        label = label,
        description = description,
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
    size: SizeVariant = SizeVariant.Medium
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
    size: SizeVariant = SizeVariant.Medium
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
 *         size = SizeVariant.Small
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
 *             size = SizeVariant.Small
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
 *     size = SizeVariant.Small,
 *     variant = CheckboxVariant.Outlined
 * )
 * ```
 *
 * 8. Hierarchical checkbox tree with parent/child indeterminate propagation:
 * ```
 * val tree = listOf(
 *     CheckboxTreeItem("all", "All Features", children = listOf(
 *         CheckboxTreeItem("basic", "Basic", children = listOf(
 *             CheckboxTreeItem("view", "View Only"),
 *             CheckboxTreeItem("edit", "Edit Access")
 *         )),
 *         CheckboxTreeItem("premium", "Premium", children = listOf(
 *             CheckboxTreeItem("export", "Export Data"),
 *             CheckboxTreeItem("analytics", "Analytics")
 *         ))
 *     ))
 * )
 * var selectedIds by remember { mutableStateOf(setOf<String>()) }
 * PixaCheckboxTree(
 *     items = tree,
 *     selectedIds = selectedIds,
 *     onSelectionChange = { selectedIds = it }
 * )
 * ```
 */

// ============================================================================
// CHECKBOX GROUP & TREE
// ============================================================================

/**
 * CheckboxGroup - Multi-select checkbox list with optional "Select All" toggle.
 */
@Composable
fun <T> CheckboxGroup(
    options: List<T>,
    selected: Set<T>,
    onSelectionChange: (Set<T>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showSelectAll: Boolean = false,
    selectAllLabel: String = "Select All",
    optionLabel: (T) -> String = { it.toString() },
    variant: CheckboxVariant = CheckboxVariant.Filled,
    size: SizeVariant = SizeVariant.Medium,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        if (showSelectAll) {
            val allSelected = options.all { it in selected }
            val noneSelected = options.none { it in selected }

            PixaCheckbox(
                checked = allSelected,
                onCheckedChange = {
                    onSelectionChange(if (allSelected) emptySet() else options.toSet())
                },
                enabled = enabled,
                label = selectAllLabel,
                state = when {
                    allSelected -> CheckboxState.Checked
                    noneSelected -> CheckboxState.Unchecked
                    else -> CheckboxState.Indeterminate
                },
                variant = variant,
                size = size
            )
        }

        options.forEach { option ->
            PixaCheckbox(
                checked = option in selected,
                onCheckedChange = { checked ->
                    onSelectionChange(
                        if (checked) selected + option else selected - option
                    )
                },
                enabled = enabled,
                label = optionLabel(option),
                variant = variant,
                size = size
            )
        }
    }
}

private fun computeParentState(childIds: Set<String>, children: List<CheckboxTreeItem<*>>): CheckboxState {
    if (children.isEmpty()) return CheckboxState.Unchecked
    val allChildIds = collectAllChildIds(children)
    val selectedChildIds = allChildIds.intersect(childIds)
    return when {
        selectedChildIds.isEmpty() -> CheckboxState.Unchecked
        selectedChildIds.size == allChildIds.size -> CheckboxState.Checked
        else -> CheckboxState.Indeterminate
    }
}

private fun collectAllChildIds(items: List<CheckboxTreeItem<*>>): Set<String> {
    val ids = mutableSetOf<String>()
    for (item in items) {
        ids.add(item.id)
        ids.addAll(collectAllChildIds(item.children))
    }
    return ids
}

private fun toggleChildIds(children: List<CheckboxTreeItem<*>>, select: Boolean): Set<String> {
    val allIds = collectAllChildIds(children)
    return if (select) allIds else emptySet()
}

/**
 * PixaCheckboxTree - Hierarchical checkbox group with automatic parent indeterminate state.
 */
@Composable
fun PixaCheckboxTree(
    items: List<CheckboxTreeItem<*>>,
    selectedIds: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: CheckboxVariant = CheckboxVariant.Filled,
    size: SizeVariant = SizeVariant.Medium,
    indent: Dp = HierarchicalSize.Spacing.Medium
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
    ) {
        items.forEach { item ->
            PixaCheckboxTreeItem(
                item = item,
                selectedIds = selectedIds,
                onSelectionChange = onSelectionChange,
                enabled = enabled,
                variant = variant,
                size = size,
                indent = indent,
                depth = 0
            )
        }
    }
}

@Composable
private fun PixaCheckboxTreeItem(
    item: CheckboxTreeItem<*>,
    selectedIds: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    enabled: Boolean,
    variant: CheckboxVariant,
    size: SizeVariant,
    indent: Dp,
    depth: Int
) {
    val isSelected = item.id in selectedIds

    if (item.isLeaf) {
        PixaCheckbox(
            checked = isSelected,
            onCheckedChange = { checked ->
                onSelectionChange(
                    if (checked) selectedIds + item.id else selectedIds - item.id
                )
            },
            enabled = enabled && item.enabled,
            label = item.label,
            description = item.description,
            variant = variant,
            size = size,
            modifier = Modifier.padding(start = indent * depth)
        )
    } else {
        val childAllIds = collectAllChildIds(item.children)
        val childSelectedIds = childAllIds.intersect(selectedIds)
        val allChecked = childSelectedIds.size == childAllIds.size && childAllIds.isNotEmpty()

        PixaCheckbox(
            checked = allChecked,
            onCheckedChange = { checked ->
                val childIds = toggleChildIds(item.children, checked)
                val selfChange = if (checked) setOf(item.id) else emptySet()
                val allChanges = if (checked) childIds + selfChange else childIds
                onSelectionChange(
                    if (checked) selectedIds + allChanges else selectedIds - allChanges
                )
            },
            enabled = enabled && item.enabled,
            label = item.label,
            description = item.description,
            state = when {
                childSelectedIds.isEmpty() && !isSelected -> CheckboxState.Unchecked
                childSelectedIds.size == childAllIds.size && childAllIds.isNotEmpty() -> CheckboxState.Checked
                else -> CheckboxState.Indeterminate
            },
            variant = variant,
            size = size,
            modifier = Modifier.padding(start = indent * depth)
        )

        item.children.forEach { child ->
            PixaCheckboxTreeItem(
                item = child,
                selectedIds = selectedIds,
                onSelectionChange = onSelectionChange,
                enabled = enabled,
                variant = variant,
                size = size,
                indent = indent,
                depth = depth + 1
            )
        }
    }
}
