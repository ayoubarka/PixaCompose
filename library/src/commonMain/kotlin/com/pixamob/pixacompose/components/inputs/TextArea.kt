package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils

// ============================================================================
// Configuration
// ============================================================================

/**
 * TextArea variant enum
 */
enum class TextAreaVariant {
    Filled,    // Filled background
    Outlined,  // Border only
    Ghost      // Transparent, minimal
}

/**
 * TextArea size enum
 */
enum class TextAreaSize {
    Small,     // Compact (96dp default height)
    Medium,    // Standard (128dp default height)
    Large      // Comfortable (160dp default height)
}

/**
 * Configuration for TextArea appearance
 */
@Stable
private data class TextAreaConfig(
    val minHeight: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val textStyle: TextStyle,
    val labelTextStyle: TextStyle,
    val helperTextStyle: TextStyle,
    val iconSize: Dp,
    val borderWidth: Dp,
    val cornerRadius: Dp
)

/**
 * Get configuration for given size
 */
@Composable
private fun TextAreaSize.config(): TextAreaConfig {
    val typography = AppTheme.typography
    return when (this) {
        TextAreaSize.Small -> TextAreaConfig(
            minHeight = 96.dp,
            horizontalPadding = Spacing.Medium,
            verticalPadding = Spacing.Small,
            textStyle = typography.bodyLight,
            labelTextStyle = typography.labelSmall,
            helperTextStyle = typography.captionRegular,
            iconSize = IconSize.Small,
            borderWidth = BorderWidth.Thin,
            cornerRadius = CornerRadius.Small
        )
        TextAreaSize.Medium -> TextAreaConfig(
            minHeight = 128.dp,
            horizontalPadding = Spacing.Large,
            verticalPadding = Spacing.Medium,
            textStyle = typography.bodyRegular,
            labelTextStyle = typography.labelMedium,
            helperTextStyle = typography.captionRegular,
            iconSize = IconSize.Medium,
            borderWidth = BorderWidth.Medium,
            cornerRadius = CornerRadius.Medium
        )
        TextAreaSize.Large -> TextAreaConfig(
            minHeight = 160.dp,
            horizontalPadding = Spacing.ExtraLarge,
            verticalPadding = Spacing.Large,
            textStyle = typography.bodyBold,
            labelTextStyle = typography.labelLarge,
            helperTextStyle = typography.captionRegular,
            iconSize = IconSize.Large,
            borderWidth = BorderWidth.Thick,
            cornerRadius = CornerRadius.Large
        )
    }
}

// ============================================================================
// Theme
// ============================================================================

/**
 * Colors for TextArea states
 */
@Stable
private data class TextAreaColors(
    val background: Color,
    val border: Color,
    val focusedBorder: Color,
    val errorBorder: Color,
    val text: Color,
    val placeholder: Color,
    val label: Color,
    val helperText: Color,
    val errorText: Color,
    val disabledBackground: Color,
    val disabledBorder: Color,
    val disabledText: Color
)

/**
 * Get colors for TextArea variant
 */
@Composable
private fun TextAreaVariant.colors(
    isError: Boolean,
    isFocused: Boolean,
    enabled: Boolean
): TextAreaColors {
    val colors = AppTheme.colors

    return when (this) {
        TextAreaVariant.Filled -> TextAreaColors(
            background = if (enabled) colors.baseSurfaceDefault else colors.baseSurfaceDisabled,
            border = Color.Transparent,
            focusedBorder = colors.brandBorderDefault.copy(alpha = 0.2f),
            errorBorder = colors.errorBorderDefault,
            text = if (enabled) colors.baseContentTitle else colors.baseContentDisabled,
            placeholder = colors.baseContentBody.copy(alpha = 0.5f),
            label = if (isFocused) colors.brandContentDefault else colors.baseContentBody,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            disabledBackground = colors.baseSurfaceDisabled,
            disabledBorder = Color.Transparent,
            disabledText = colors.baseContentDisabled
        )
        TextAreaVariant.Outlined -> TextAreaColors(
            background = Color.Transparent,
            border = colors.baseBorderDefault,
            focusedBorder = colors.brandBorderDefault,
            errorBorder = colors.errorBorderDefault,
            text = if (enabled) colors.baseContentTitle else colors.baseContentDisabled,
            placeholder = colors.baseContentBody.copy(alpha = 0.5f),
            label = if (isFocused) colors.brandContentDefault else colors.baseContentBody,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            disabledBackground = Color.Transparent,
            disabledBorder = colors.baseBorderDisabled,
            disabledText = colors.baseContentDisabled
        )
        TextAreaVariant.Ghost -> TextAreaColors(
            background = Color.Transparent,
            border = Color.Transparent,
            focusedBorder = colors.baseBorderDefault.copy(alpha = 0.3f),
            errorBorder = colors.errorBorderDefault.copy(alpha = 0.3f),
            text = if (enabled) colors.baseContentTitle else colors.baseContentDisabled,
            placeholder = colors.baseContentBody.copy(alpha = 0.4f),
            label = if (isFocused) colors.brandContentDefault else colors.baseContentBody,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            disabledBackground = Color.Transparent,
            disabledBorder = Color.Transparent,
            disabledText = colors.baseContentDisabled
        )
    }
}

// ============================================================================
// Pixa Component
// ============================================================================

/**
 * PixaTextArea - Core multi-line text input component
 *
 * Multi-line text input with variants, sizes, and full customization.
 * Follows Material 3 design with theme integration.
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param modifier Modifier for the text area
 * @param variant Visual style variant
 * @param size Size preset
 * @param enabled Whether the field is enabled
 * @param readOnly Whether the field is read-only
 * @param isError Whether to show error state
 * @param label Optional label text
 * @param placeholder Optional placeholder text
 * @param helperText Optional helper text below field
 * @param errorText Optional error text (shown when isError=true)
 * @param leadingIcon Optional leading icon (top-aligned)
 * @param keyboardOptions Keyboard configuration
 * @param keyboardActions Keyboard actions
 * @param minLines Minimum number of lines
 * @param maxLines Maximum number of lines
 * @param maxLength Optional maximum character length
 * @param showCharacterCount Whether to show character counter
 * @param interactionSource Interaction source for state
 * @param contentDescription Accessibility description
 *
 * @sample
 * ```
 * var text by remember { mutableStateOf("") }
 * PixaTextArea(
 *     value = text,
 *     onValueChange = { text = it },
 *     label = "Description",
 *     placeholder = "Enter your description",
 *     variant = TextAreaVariant.Outlined,
 *     maxLength = 500
 * )
 * ```
 */
@Composable
fun PixaTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextAreaVariant = TextAreaVariant.Outlined,
    size: TextAreaSize = TextAreaSize.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    leadingIcon: Painter? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    minLines: Int = 3,
    maxLines: Int = Int.MAX_VALUE,
    maxLength: Int? = null,
    showCharacterCount: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null
) {
    val config = size.config()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val colors = variant.colors(isError, isFocused, enabled)

    // Animated colors
    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledBorder
            isError -> colors.errorBorder
            isFocused -> colors.focusedBorder
            else -> colors.border
        },
        animationSpec = tween(durationMillis = 200)
    )

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (!enabled) colors.disabledBackground else colors.background,
        animationSpec = tween(durationMillis = 200)
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isFocused && variant == TextAreaVariant.Outlined) config.borderWidth * 1.2f else config.borderWidth,
        animationSpec = tween(durationMillis = 200)
    )

    Column(
        modifier = modifier.semantics {
            contentDescription?.let { this.contentDescription = it }
        }
    ) {
        // Label
        if (label != null) {
            Text(
                text = label,
                style = config.labelTextStyle,
                color = if (isError) colors.errorText else colors.label,
                modifier = Modifier.padding(bottom = Spacing.ExtraSmall)
            )
        }

        // Text area container
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                val finalValue = if (maxLength != null) {
                    newValue.take(maxLength)
                } else {
                    newValue
                }
                onValueChange(finalValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = config.minHeight),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = config.textStyle.copy(color = if (enabled) colors.text else colors.disabledText),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = false,
            minLines = minLines,
            maxLines = maxLines,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(colors.label),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        .padding(
                            horizontal = config.horizontalPadding,
                            vertical = config.verticalPadding
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Leading icon (top-aligned)
                        if (leadingIcon != null) {
                            PixaIcon(
                                painter = leadingIcon,
                                contentDescription = null,
                                tint = colors.label,
                                modifier = Modifier
                                    .size(config.iconSize)
                                    .padding(top = Spacing.ExtraSmall)
                            )
                        }

                        // Text input area
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.TopStart
                        ) {
                            // Placeholder
                            if (value.isEmpty() && placeholder != null) {
                                Text(
                                    text = placeholder,
                                    style = config.textStyle,
                                    color = colors.placeholder
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            }
        )

        // Bottom row with helper/error text and character count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = config.horizontalPadding,
                    end = config.horizontalPadding,
                    top = Spacing.ExtraSmall
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Helper/Error text
            val bottomText = if (isError && errorText != null) errorText else helperText
            if (bottomText != null) {
                Text(
                    text = bottomText,
                    style = config.helperTextStyle,
                    color = if (isError) colors.errorText else colors.helperText,
                    modifier = Modifier.weight(1f, fill = false)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Character counter
            if (showCharacterCount || (maxLength != null && value.length > maxLength * 0.8)) {
                val counterText = if (maxLength != null) {
                    "${value.length}/$maxLength"
                } else {
                    "${value.length}"
                }
                Text(
                    text = counterText,
                    style = config.helperTextStyle,
                    color = if (maxLength != null && value.length >= maxLength) colors.errorText else colors.helperText
                )
            }
        }
    }
}

// ============================================================================
// Convenience Variants
// ============================================================================

/**
 * FilledTextArea - Filled background variant
 */
@Composable
fun FilledTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: TextAreaSize = TextAreaSize.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    leadingIcon: Painter? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    minLines: Int = 3,
    maxLines: Int = Int.MAX_VALUE,
    maxLength: Int? = null,
    showCharacterCount: Boolean = false
) {
    PixaTextArea(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = TextAreaVariant.Filled,
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        label = label,
        placeholder = placeholder,
        helperText = helperText,
        errorText = errorText,
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        minLines = minLines,
        maxLines = maxLines,
        maxLength = maxLength,
        showCharacterCount = showCharacterCount
    )
}

/**
 * OutlinedTextArea - Outlined border variant (default)
 */
@Composable
fun OutlinedTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: TextAreaSize = TextAreaSize.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    leadingIcon: Painter? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    minLines: Int = 3,
    maxLines: Int = Int.MAX_VALUE,
    maxLength: Int? = null,
    showCharacterCount: Boolean = false
) {
    PixaTextArea(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = TextAreaVariant.Outlined,
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        label = label,
        placeholder = placeholder,
        helperText = helperText,
        errorText = errorText,
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        minLines = minLines,
        maxLines = maxLines,
        maxLength = maxLength,
        showCharacterCount = showCharacterCount
    )
}

/**
 * GhostTextArea - Minimal transparent variant
 */
@Composable
fun GhostTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: TextAreaSize = TextAreaSize.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    leadingIcon: Painter? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    minLines: Int = 3,
    maxLines: Int = Int.MAX_VALUE,
    maxLength: Int? = null,
    showCharacterCount: Boolean = false
) {
    PixaTextArea(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = TextAreaVariant.Ghost,
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        label = label,
        placeholder = placeholder,
        helperText = helperText,
        errorText = errorText,
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        minLines = minLines,
        maxLines = maxLines,
        maxLength = maxLength,
        showCharacterCount = showCharacterCount
    )
}

// ============================================================================
// Specialized Convenience Functions
// ============================================================================

/**
 * CommentTextArea - Pre-configured for comments
 */
@Composable
fun CommentTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextAreaVariant = TextAreaVariant.Outlined,
    size: TextAreaSize = TextAreaSize.Medium,
    enabled: Boolean = true,
    label: String = "Comment",
    placeholder: String = "Write your comment...",
    maxLength: Int = 500,
    showCharacterCount: Boolean = true
) {
    PixaTextArea(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Default
        ),
        minLines = 3,
        maxLines = 6,
        maxLength = maxLength,
        showCharacterCount = showCharacterCount
    )
}

/**
 * BioTextArea - Pre-configured for biography/description
 */
@Composable
fun BioTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextAreaVariant = TextAreaVariant.Outlined,
    size: TextAreaSize = TextAreaSize.Medium,
    enabled: Boolean = true,
    label: String = "Bio",
    placeholder: String = "Tell us about yourself...",
    maxLength: Int = 300,
    showCharacterCount: Boolean = true
) {
    PixaTextArea(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Default
        ),
        minLines = 4,
        maxLines = 8,
        maxLength = maxLength,
        showCharacterCount = showCharacterCount
    )
}

/**
 * NoteTextArea - Pre-configured for notes
 */
@Composable
fun NoteTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextAreaVariant = TextAreaVariant.Filled,
    size: TextAreaSize = TextAreaSize.Medium,
    enabled: Boolean = true,
    placeholder: String = "Take a note...",
    maxLength: Int? = null
) {
    PixaTextArea(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Default
        ),
        minLines = 5,
        maxLines = Int.MAX_VALUE,
        maxLength = maxLength,
        showCharacterCount = maxLength != null
    )
}
