package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.actions.ChipType
import com.pixamob.pixacompose.components.actions.PixaChip
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.feedback.PixaCircularIndicator
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import kotlin.math.roundToInt

// ════════════════════════════════════════════════════════════════════════════
// Configuration
// ════════════════════════════════════════════════════════════════════════════

/**
 * TextField variant enum
 */
enum class TextFieldVariant {
    Filled,    // Filled background
    Outlined,  // Border only
    Ghost      // Transparent, minimal
}

/**
 * Trailing status indicator — Complete/Incomplete (fill-status, independent of
 * [PixaTextField]'s isError/isSuccess validation axis) and Loading. Occupies the trailing
 * slot with priority over `trailingIcon`/`onClear` when not [None].
 */
enum class TextFieldStatus {
    None,
    /** Green circle_check — contentPositive trailing icon. */
    Complete,
    /** Red circle_x — contentNegative trailing icon. */
    Incomplete,
    /** Progress spinner at trailing position — reuses [PixaCircularIndicator]. */
    Loading
}

/**
 * Content type — Plaintext or Tags (committed values rendered as removable [PixaChip]s
 * ahead of the text input). Secure/Masked is handled via [visualTransformation].
 */
enum class TextFieldContentType {
    Plaintext,
    Tags
}

/**
 * Configuration for TextField appearance.
 *
 * Container-level constants (not per-size): corner radius (8px min), horizontal padding (16px),
 * and border width (1px default / 3px active-error-success-readonly). Only vertical
 * padding and height/type-scale vary per size.
 */
@Stable
private data class TextFieldConfig(
    val height: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val textStyle: TextStyle,
    val labelTextStyle: TextStyle,
    val helperTextStyle: TextStyle,
    val iconSize: Dp,
    val cornerRadius: Dp
)

/** 1px border weight, inside alignment. Constant across size/variant. */
private val DefaultBorderWidth = HierarchicalSize.Border.Compact

/** 3px for active/error/success/read-only. Constant across size/variant. */
private val ActiveBorderWidth = HierarchicalSize.Border.Large

/**
 * Get configuration for given size. Heights/vertical padding follow
 * [HierarchicalSize.Input]/[HierarchicalSize.Spacing].
 */
@Composable
private fun SizeVariant.config(): TextFieldConfig {
    val typography = AppTheme.typography
    return when (this) {
        SizeVariant.Small -> TextFieldConfig(
            height = HierarchicalSize.Input.Small,
            horizontalPadding = HierarchicalSize.Spacing.Large,
            verticalPadding = HierarchicalSize.Spacing.Small,
            textStyle = typography.bodyLight,
            labelTextStyle = typography.labelSmall,
            helperTextStyle = typography.captionLight,
            iconSize = HierarchicalSize.Icon.Small,
            cornerRadius = HierarchicalSize.Radius.Medium
        )

        SizeVariant.Medium -> TextFieldConfig(
            height = HierarchicalSize.Input.Medium,
            horizontalPadding = HierarchicalSize.Spacing.Large,
            verticalPadding = HierarchicalSize.Spacing.Medium,
            textStyle = typography.bodyRegular,
            labelTextStyle = typography.labelMedium,
            helperTextStyle = typography.captionRegular,
            iconSize = HierarchicalSize.Icon.Medium,
            cornerRadius = HierarchicalSize.Radius.Medium
        )

        SizeVariant.Large -> TextFieldConfig(
            height = HierarchicalSize.Input.Large,
            horizontalPadding = HierarchicalSize.Spacing.Large,
            verticalPadding = HierarchicalSize.Spacing.Large,
            textStyle = typography.bodyBold,
            labelTextStyle = typography.labelLarge,
            helperTextStyle = typography.captionBold,
            iconSize = HierarchicalSize.Icon.Large,
            cornerRadius = HierarchicalSize.Radius.Medium
        )

        else -> TextFieldConfig(
            height = HierarchicalSize.Input.Medium,
            horizontalPadding = HierarchicalSize.Spacing.Large,
            verticalPadding = HierarchicalSize.Spacing.Medium,
            textStyle = typography.bodyRegular,
            labelTextStyle = typography.labelMedium,
            helperTextStyle = typography.captionRegular,
            iconSize = HierarchicalSize.Icon.Medium,
            cornerRadius = HierarchicalSize.Radius.Medium
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Theme
// ════════════════════════════════════════════════════════════════════════════

/**
 * Colors for TextField states.
 *
 * Error and Success are independent validation axes — a field can validate positively as distinctly
 * as it can fail. [focusedBackground] backs the Filled variant's background shift
 * (Outlined/Ghost stay transparent). [readOnlyBorder] maps to `baseBorderDefault`.
 * [hoverOverlay]/[pressedOverlay] are 4% / 8% black overlay.
 */
@Stable
private data class TextFieldColors(
    val background: Color,
    val focusedBackground: Color,
    val border: Color,
    val focusedBorder: Color,
    val errorBorder: Color,
    val successBorder: Color,
    val readOnlyBorder: Color,
    val text: Color,
    val placeholder: Color,
    val label: Color,
    val helperText: Color,
    val errorText: Color,
    val successText: Color,
    val disabledBackground: Color,
    val disabledBorder: Color,
    val disabledText: Color,
    val hoverOverlay: Color,
    val pressedOverlay: Color
)

/**
 * Get colors for TextField variant
 *
 * Returns color configuration based on variant, error state, focus state, and enabled state.
 * Text colors can be customized by using the returned TextFieldColors.text property.
 */
@Composable
private fun TextFieldVariant.colors(
    isError: Boolean,
    isFocused: Boolean,
    enabled: Boolean
): TextFieldColors {
    val colors = AppTheme.colors
    val hoverOverlay = Color.Black.copy(alpha = 0.04f)
    val pressedOverlay = Color.Black.copy(alpha = 0.08f)

    return when (this) {
        TextFieldVariant.Filled -> TextFieldColors(
            background = if (enabled) colors.baseSurfaceSubtle else colors.baseSurfaceDisabled,
            focusedBackground = colors.baseSurfaceFocus,
            border = Color.Transparent,
            focusedBorder = colors.brandBorderDefault,
            errorBorder = colors.errorBorderDefault,
            successBorder = colors.successBorderDefault,
            readOnlyBorder = colors.baseBorderDefault,
            text = when {
                !enabled -> colors.baseContentDisabled
                isError -> colors.errorContentDefault
                else -> colors.baseContentTitle
            },
            placeholder = colors.baseContentBody.copy(alpha = 0.5f),
            label = if (isFocused) colors.brandContentDefault else colors.baseContentBody,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            successText = colors.successContentDefault,
            disabledBackground = colors.baseSurfaceDisabled,
            disabledBorder = Color.Transparent,
            disabledText = colors.baseContentDisabled,
            hoverOverlay = hoverOverlay,
            pressedOverlay = pressedOverlay
        )

        TextFieldVariant.Outlined -> TextFieldColors(
            background = Color.Transparent,
            focusedBackground = Color.Transparent,
            border = colors.baseBorderDefault,
            focusedBorder = colors.brandBorderDefault,
            errorBorder = colors.errorBorderDefault,
            successBorder = colors.successBorderDefault,
            readOnlyBorder = colors.baseBorderDefault,
            text = when {
                !enabled -> colors.baseContentDisabled
                isError -> colors.errorContentDefault
                else -> colors.baseContentTitle
            },
            placeholder = colors.baseContentBody.copy(alpha = 0.5f),
            label = if (isFocused) colors.brandContentDefault else colors.baseContentBody,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            successText = colors.successContentDefault,
            disabledBackground = Color.Transparent,
            disabledBorder = colors.baseBorderDisabled,
            disabledText = colors.baseContentDisabled,
            hoverOverlay = hoverOverlay,
            pressedOverlay = pressedOverlay
        )

        TextFieldVariant.Ghost -> TextFieldColors(
            background = Color.Transparent,
            focusedBackground = Color.Transparent,
            border = Color.Transparent,
            focusedBorder = colors.baseBorderDefault.copy(alpha = 0.3f),
            errorBorder = colors.errorBorderDefault.copy(alpha = 0.3f),
            successBorder = colors.successBorderDefault.copy(alpha = 0.3f),
            readOnlyBorder = colors.baseBorderDefault.copy(alpha = 0.3f),
            text = when {
                !enabled -> colors.baseContentDisabled
                isError -> colors.errorContentDefault
                else -> colors.baseContentTitle
            },
            placeholder = colors.baseContentBody.copy(alpha = 0.4f),
            label = if (isFocused) colors.brandContentDefault else colors.baseContentBody,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            successText = colors.successContentDefault,
            disabledBackground = Color.Transparent,
            disabledBorder = Color.Transparent,
            disabledText = colors.baseContentDisabled,
            hoverOverlay = hoverOverlay,
            pressedOverlay = pressedOverlay
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Base Component
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaTextField — a foundational input component for text-based keyboard input.
 *
 * ### Anatomy
 * [label] → input control (leading enhancer → text/[placeholder] → trailing
 * enhancer/[TextFieldStatus]) → [helperText]/[errorText]. Enhancers accept either artwork
 * ([leadingIcon]/[trailingIcon]) or a text label ([leadingLabel]/[trailingLabel]).
 *
 * ### Content model
 * [contentType] selects Plaintext (default) or Tags (committed [tags] render as removable
 * [PixaChip]s). Secure/Masked input is handled via [visualTransformation].
 *
 * ### States
 * Enabled/disabled, focused (3px `brandBorderDefault` outline), [isError]/[isSuccess]
 * (independent validation axes), [readOnly] (3px outline, focusable/tab-navigable),
 * hover/pressed (4%/8% black overlay), trailing [status] (Complete/Incomplete/Loading),
 * and [isPreloading] (skeleton placeholder).
 *
 * ### Sizing
 * [size] resolves height/vertical padding via [HierarchicalSize.Input].
 * Corner radius (8px min) and horizontal padding (16px) are container-level constants.
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param modifier Modifier for the text field
 * @param variant Visual style variant (Filled, Outlined, Ghost)
 * @param size Size preset (Small, Medium, Large)
 * @param enabled Whether the field is enabled
 * @param readOnly Whether the field is read-only (focusable, tab-navigable, value submitted)
 * @param isError Whether to show the error validation state (independent of [isSuccess])
 * @param isSuccess Whether to show the success validation state (independent of [isError])
 * @param status Trailing fill-status indicator (Complete/Incomplete/Loading)
 * @param isPreloading Whether to render a [Skeleton] placeholder
 * @param label Optional label text
 * @param placeholder Optional placeholder text (disappears on typing)
 * @param helperText Optional helper text below field
 * @param errorText Optional error text (shown when isError=true)
 * @param leadingIcon Optional leading artwork enhancer
 * @param trailingIcon Optional trailing artwork enhancer
 * @param leadingLabel Optional leading text enhancer (mutually exclusive with [leadingIcon])
 * @param trailingLabel Optional trailing text enhancer (mutually exclusive with [trailingIcon]/[status])
 * @param onClear Optional clear callback — shows a clear icon when text is non-empty
 * @param contentType Plaintext or Tags
 * @param tags Committed tag values rendered as removable chips
 * @param onTagsChange Callback when tags change
 * @param visualTransformation Visual transformation (e.g., password)
 * @param keyboardOptions Keyboard configuration
 * @param keyboardActions Keyboard actions
 * @param singleLine Whether to limit to single line
 * @param maxLength Optional maximum character length
 * @param interactionSource Interaction source for state
 * @param contentDescription Accessibility description
 * @param customTextColor Optional custom text color
 * @param customFocusedTextColor Optional custom text color when focused
 */
@Composable
fun PixaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextFieldVariant = TextFieldVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    isSuccess: Boolean = false,
    status: TextFieldStatus = TextFieldStatus.None,
    isPreloading: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    leadingLabel: String? = null,
    trailingLabel: String? = null,
    onClear: (() -> Unit)? = null,
    contentType: TextFieldContentType = TextFieldContentType.Plaintext,
    tags: List<String> = emptyList(),
    onTagsChange: ((List<String>) -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLength: Int? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null,
    customTextColor: Color? = null,
    customFocusedTextColor: Color? = null
) {
    val config = size.config()

    if (isPreloading) {
        Column(modifier = modifier) {
            if (label != null) {
                // Label placeholder hugs ~30% of the row width rather than a fixed dp literal —
                // Skeleton widths are inherently approximate stand-ins, not pixel-meaningful.
                Skeleton(modifier = Modifier.fillMaxWidth(0.3f), height = HierarchicalSize.Spacing.Large, showBorder = false)
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
            }
            Skeleton(height = config.height, shape = RoundedCornerShape(config.cornerRadius))
        }
        return
    }

    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val colors = variant.colors(isError, isFocused, enabled)

    // Determine text color: custom colors override variant colors
    // Variant colors already handle isError, isFocused, and enabled states
    val effectiveTextColor = when {
        customFocusedTextColor != null && isFocused -> customFocusedTextColor
        customTextColor != null -> customTextColor
        else -> colors.text  // Use variant color which already considers all states
    }

    // Border: 1px default, 3px active/error/success/read-only.
    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledBorder
            isError -> colors.errorBorder
            isSuccess -> colors.successBorder
            readOnly -> colors.readOnlyBorder
            isFocused -> colors.focusedBorder
            else -> colors.border
        },
        animationSpec = AnimationUtils.smoothSpring()
    )

    val animatedBackgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledBackground
            isFocused -> colors.focusedBackground
            else -> colors.background
        },
        animationSpec = AnimationUtils.smoothSpring()
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isFocused || isError || isSuccess || readOnly) ActiveBorderWidth else DefaultBorderWidth,
        animationSpec = AnimationUtils.standardTween(200)
    )

    Column(
        modifier = modifier.semantics {
            contentDescription?.let { this.contentDescription = it }
        }
    ) {
        // Label
        if (label != null) {
            BasicText(
                text = label,
                style = config.labelTextStyle.copy(color = if (isError) colors.errorText else colors.label),
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Compact)
            )
        }

        // Tags (TextFieldContentType.Tags) — committed values render as removable chips ahead of the input.
        if (contentType == TextFieldContentType.Tags && tags.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Nano),
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Compact)
            ) {
                tags.forEach { tag ->
                    PixaChip(
                        text = tag,
                        size = SizeVariant.Small,
                        type = ChipType.Dismissible,
                        onDismiss = { onTagsChange?.invoke(tags - tag) }
                    )
                }
            }
        }

        // Text field container
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                if (contentType == TextFieldContentType.Tags && onTagsChange != null &&
                    (newValue.endsWith(",") || newValue.endsWith("\n"))
                ) {
                    val newTag = newValue.trimEnd(',', '\n').trim()
                    if (newTag.isNotEmpty()) onTagsChange(tags + newTag)
                    onValueChange("")
                    return@BasicTextField
                }
                val finalValue = if (maxLength != null) {
                    newValue.take(maxLength)
                } else {
                    newValue
                }
                onValueChange(finalValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = config.height)
                .hoverable(interactionSource = interactionSource, enabled = enabled),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = config.textStyle.copy(color = effectiveTextColor),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(colors.label),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = config.height)
                        .clip(RoundedCornerShape(config.cornerRadius))
                        .background(animatedBackgroundColor)
                        .then(
                            if (animatedBorderColor != Color.Transparent) {
                                Modifier.border(
                                    width = animatedBorderWidth,
                                    color = animatedBorderColor,
                                    shape = RoundedCornerShape(config.cornerRadius)
                                )
                            } else Modifier
                        )
                        .then(
                            when {
                                isPressed && enabled -> Modifier.background(colors.pressedOverlay, RoundedCornerShape(config.cornerRadius))
                                isHovered && enabled -> Modifier.background(colors.hoverOverlay, RoundedCornerShape(config.cornerRadius))
                                else -> Modifier
                            }
                        )
                        .padding(
                            horizontal = config.horizontalPadding,
                            vertical = config.verticalPadding
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
                    ) {
                        // Leading enhancer — artwork or label, not both
                        if (leadingIcon != null) {
                            PixaIcon(
                                painter = leadingIcon,
                                contentDescription = null,
                                tint = colors.label,
                                modifier = Modifier.size(config.iconSize)
                            )
                        } else if (leadingLabel != null) {
                            BasicText(text = leadingLabel, style = config.textStyle.copy(color = colors.label))
                        }

                        // Text input area
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            // Placeholder
                            if (value.isEmpty() && placeholder != null) {
                                BasicText(
                                    text = placeholder,
                                    style = config.textStyle.copy(color = colors.placeholder)
                                )
                            }
                            innerTextField()
                        }

                        // Trailing slot: status indicator takes priority over artwork/label/clear
                        when (status) {
                            TextFieldStatus.Loading -> PixaCircularIndicator(
                                modifier = Modifier.size(config.iconSize),
                                sizePreset = SizeVariant.Small,
                                contentDescription = "Loading"
                            )
                            TextFieldStatus.Complete -> Box(
                                modifier = Modifier.size(config.iconSize).clip(AppTheme.shapes.pill).background(AppTheme.colors.successSurfaceDefault),
                                contentAlignment = Alignment.Center
                            ) {
                                BasicText(text = "✓", style = AppTheme.typography.captionBold.copy(color = AppTheme.colors.successContentDefault))
                            }
                            TextFieldStatus.Incomplete -> Box(
                                modifier = Modifier.size(config.iconSize).clip(AppTheme.shapes.pill).background(AppTheme.colors.errorSurfaceDefault),
                                contentAlignment = Alignment.Center
                            ) {
                                BasicText(text = "✕", style = AppTheme.typography.captionBold.copy(color = AppTheme.colors.errorContentDefault))
                            }
                            TextFieldStatus.None -> {
                                if (trailingIcon != null) {
                                    PixaIcon(
                                        painter = trailingIcon,
                                        contentDescription = null,
                                        tint = colors.label,
                                        modifier = Modifier.size(config.iconSize)
                                    )
                                } else if (trailingLabel != null) {
                                    BasicText(text = trailingLabel, style = config.textStyle.copy(color = colors.label))
                                }
                                if (onClear != null && value.isNotEmpty() && enabled && !readOnly) {
                                    Box(
                                        modifier = Modifier
                                            .size(config.iconSize)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                onValueChange("")
                                                onClear()
                                            }
                                            .semantics {
                                                contentDescription ?: "Clear text"
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        BasicText(
                                            text = "✕",
                                            style = AppTheme.typography.bodyRegular.copy(color = colors.label)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )

        // Helper/Error/Success text
        val bottomText = when {
            isError && errorText != null -> errorText
            else -> helperText
        }
        if (bottomText != null) {
            BasicText(
                text = bottomText,
                style = config.helperTextStyle.copy(
                    color = when {
                        isError -> colors.errorText
                        isSuccess -> colors.successText
                        else -> colors.helperText
                    }
                ),
                maxLines = 3,
                modifier = Modifier.padding(
                    start = config.horizontalPadding,
                    top = HierarchicalSize.Spacing.Compact
                )
            )
        }

        // Character counter
        if (maxLength != null && value.length > maxLength * 0.8) {
            BasicText(
                text = "${value.length}/$maxLength",
                style = config.helperTextStyle.copy(color = if (value.length >= maxLength) colors.errorText else colors.helperText),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(
                        end = config.horizontalPadding,
                        top = HierarchicalSize.Spacing.Compact
                    )
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Convenience Variants
// ════════════════════════════════════════════════════════════════════════════

/**
 * FilledTextField - Filled background variant
 */
@Composable
fun FilledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLength: Int? = null
) {
    PixaTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = TextFieldVariant.Filled,
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        label = label,
        placeholder = placeholder,
        helperText = helperText,
        errorText = errorText,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLength = maxLength
    )
}

/**
 * OutlinedTextField - Outlined border variant (default)
 */
@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLength: Int? = null
) {
    PixaTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = TextFieldVariant.Outlined,
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        label = label,
        placeholder = placeholder,
        helperText = helperText,
        errorText = errorText,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLength = maxLength
    )
}

/**
 * GhostTextField - Minimal transparent variant
 */
@Composable
fun GhostTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLength: Int? = null
) {
    PixaTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = TextFieldVariant.Ghost,
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        label = label,
        placeholder = placeholder,
        helperText = helperText,
        errorText = errorText,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLength = maxLength
    )
}

// ════════════════════════════════════════════════════════════════════════════
// Specialized Convenience Functions
// ════════════════════════════════════════════════════════════════════════════

/**
 * EmailTextField - Pre-configured for email input
 */
@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextFieldVariant = TextFieldVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String = "Email",
    placeholder: String = "Enter your email",
    helperText: String? = null,
    errorText: String? = "Invalid email address"
) {
    PixaTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled,
        isError = isError,
        label = label,
        placeholder = placeholder,
        helperText = helperText,
        errorText = if (isError) errorText else null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true
    )
}

/**
 * PasswordTextField - Pre-configured for password input
 */
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextFieldVariant = TextFieldVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String = "Password",
    placeholder: String = "Enter your password",
    helperText: String? = null,
    errorText: String? = "Password is required",
    visualTransformation: VisualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
) {
    PixaTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled,
        isError = isError,
        label = label,
        placeholder = placeholder,
        helperText = helperText,
        errorText = if (isError) errorText else null,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}

/**
 * SearchTextField - Pre-configured for search input
 */
@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextFieldVariant = TextFieldVariant.Filled,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    placeholder: String = "Search...",
    onSearch: (() -> Unit)? = null
) {
    PixaTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch?.invoke() }
        ),
        singleLine = true
    )
}
