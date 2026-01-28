package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import kotlin.math.roundToInt

// ============================================================================
// Configuration
// ============================================================================

/**
 * TextField variant enum
 */
enum class TextFieldVariant {
    Filled,    // Filled background
    Outlined,  // Border only
    Ghost      // Transparent, minimal
}

/**
 * TextField size enum
 */
enum class TextFieldSize {
    Small,     // Compact (36dp height)
    Medium,    // Standard (44dp height)
    Large      // Comfortable (52dp height)
}

/**
 * Configuration for TextField appearance
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
    val borderWidth: Dp,
    val cornerRadius: Dp
)

/**
 * Get configuration for given size
 */
@Composable
private fun TextFieldSize.config(): TextFieldConfig {
    val typography = AppTheme.typography
    return when (this) {
        TextFieldSize.Small -> TextFieldConfig(
            height = ComponentSize.InputSmall,
            horizontalPadding = HierarchicalSize.Spacing.Medium,
            verticalPadding = HierarchicalSize.Spacing.Compact,
            textStyle = typography.bodyLight,
            labelTextStyle = typography.labelSmall,
            helperTextStyle = typography.captionLight,
            iconSize = IconSize.Small,
            borderWidth = BorderWidth.Thin,
            cornerRadius = CornerRadius.Small
        )

        TextFieldSize.Medium -> TextFieldConfig(
            height = ComponentSize.InputMedium,
            horizontalPadding = HierarchicalSize.Spacing.Large,
            verticalPadding = HierarchicalSize.Spacing.Small,
            textStyle = typography.bodyRegular,
            labelTextStyle = typography.labelMedium,
            helperTextStyle = typography.captionRegular,
            iconSize = IconSize.Medium,
            borderWidth = BorderWidth.Medium,
            cornerRadius = CornerRadius.Medium
        )

        TextFieldSize.Large -> TextFieldConfig(
            height = ComponentSize.InputLarge,
            horizontalPadding = HierarchicalSize.Spacing.Huge,
            verticalPadding = HierarchicalSize.Spacing.Medium,
            textStyle = typography.bodyBold,
            labelTextStyle = typography.labelLarge,
            helperTextStyle = typography.captionBold,
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
 * Colors for TextField states
 */
@Stable
private data class TextFieldColors(
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

    return when (this) {
        TextFieldVariant.Filled -> TextFieldColors(
            background = if (enabled) colors.baseSurfaceSubtle else colors.baseSurfaceDisabled,
            border = Color.Transparent,
            focusedBorder = colors.brandBorderDefault.copy(alpha = 0.2f),
            errorBorder = colors.errorBorderDefault,
            text = when {
                !enabled -> colors.baseContentDisabled
                isError -> colors.errorContentDefault
                else -> colors.baseContentTitle
            },
            placeholder = colors.baseContentBody.copy(alpha = 0.5f),
            label = if (isFocused) colors.brandContentDefault else colors.baseContentBody,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            disabledBackground = colors.baseSurfaceDisabled,
            disabledBorder = Color.Transparent,
            disabledText = colors.baseContentDisabled
        )

        TextFieldVariant.Outlined -> TextFieldColors(
            background = Color.Transparent,
            border = colors.baseBorderDefault,
            focusedBorder = colors.brandBorderDefault,
            errorBorder = colors.errorBorderDefault,
            text = when {
                !enabled -> colors.baseContentDisabled
                isError -> colors.errorContentDefault
                else -> colors.baseContentTitle
            },
            placeholder = colors.baseContentBody.copy(alpha = 0.5f),
            label = if (isFocused) colors.brandContentDefault else colors.baseContentBody,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            disabledBackground = Color.Transparent,
            disabledBorder = colors.baseBorderDisabled,
            disabledText = colors.baseContentDisabled
        )

        TextFieldVariant.Ghost -> TextFieldColors(
            background = Color.Transparent,
            border = Color.Transparent,
            focusedBorder = colors.baseBorderDefault.copy(alpha = 0.3f),
            errorBorder = colors.errorBorderDefault.copy(alpha = 0.3f),
            text = when {
                !enabled -> colors.baseContentDisabled
                isError -> colors.errorContentDefault
                else -> colors.baseContentTitle
            },
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
// Base Component
// ============================================================================

/**
 * PixaTextField - Core text input component
 *
 * Single-line text input with variants, sizes, and full customization.
 * Follows Material 3 design with theme integration.
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param modifier Modifier for the text field
 * @param variant Visual style variant
 * @param size Size preset
 * @param enabled Whether the field is enabled
 * @param readOnly Whether the field is read-only
 * @param isError Whether to show error state
 * @param label Optional label text
 * @param placeholder Optional placeholder text
 * @param helperText Optional helper text below field
 * @param errorText Optional error text (shown when isError=true)
 * @param leadingIcon Optional leading icon
 * @param trailingIcon Optional trailing icon
 * @param visualTransformation Visual transformation (e.g., password)
 * @param keyboardOptions Keyboard configuration
 * @param keyboardActions Keyboard actions
 * @param singleLine Whether to limit to single line
 * @param maxLength Optional maximum character length
 * @param interactionSource Interaction source for state
 * @param contentDescription Accessibility description
 * @param customTextColor Optional custom text color (defaults to theme color)
 * @param customFocusedTextColor Optional custom text color when focused (defaults to customTextColor or theme color)
 *
 * @sample
 * ```
 * var text by remember { mutableStateOf("") }
 * PixaTextField(
 *     value = text,
 *     onValueChange = { text = it },
 *     label = "Email",
 *     placeholder = "Enter your email",
 *     variant = TextFieldVariant.Outlined
 * )
 * ```
 */
@Composable
fun PixaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextFieldVariant = TextFieldVariant.Outlined,
    size: TextFieldSize = TextFieldSize.Medium,
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
    maxLength: Int? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null,
    customTextColor: Color? = null,
    customFocusedTextColor: Color? = null
) {
    val config = size.config()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val colors = variant.colors(isError, isFocused, enabled)

    // Determine text color: custom colors override variant colors
    // Variant colors already handle isError, isFocused, and enabled states
    val effectiveTextColor = when {
        customFocusedTextColor != null && isFocused -> customFocusedTextColor
        customTextColor != null -> customTextColor
        else -> colors.text  // Use variant color which already considers all states
    }

    // Animated colors
    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledBorder
            isError -> colors.errorBorder
            isFocused -> colors.focusedBorder
            else -> colors.border
        },
        animationSpec = AnimationUtils.smoothSpring()
    )

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (!enabled) colors.disabledBackground else colors.background,
        animationSpec = AnimationUtils.smoothSpring()
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isFocused && variant == TextFieldVariant.Outlined) config.borderWidth * 1.2f else config.borderWidth,
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
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Compact)
            )
        }

        // Text field container
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
                .heightIn(min = config.height),
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
                        // Leading icon
                        if (leadingIcon != null) {
                            PixaIcon(
                                painter = leadingIcon,
                                contentDescription = null,
                                tint = colors.label,
                                modifier = Modifier.size(config.iconSize)
                            )
                        }

                        // Text input area
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
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

                        // Trailing icon
                        if (trailingIcon != null) {
                            PixaIcon(
                                painter = trailingIcon,
                                contentDescription = null,
                                tint = colors.label,
                                modifier = Modifier.size(config.iconSize)
                            )
                        }
                    }
                }
            }
        )

        // Helper/Error text
        val bottomText = if (isError && errorText != null) errorText else helperText
        if (bottomText != null) {
            Text(
                text = bottomText,
                style = config.helperTextStyle,
                color = if (isError) colors.errorText else colors.helperText,
                modifier = Modifier.padding(
                    start = config.horizontalPadding,
                    top = HierarchicalSize.Spacing.Compact
                )
            )
        }

        // Character counter
        if (maxLength != null && value.length > maxLength * 0.8) {
            Text(
                text = "${value.length}/$maxLength",
                style = config.helperTextStyle,
                color = if (value.length >= maxLength) colors.errorText else colors.helperText,
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

// ============================================================================
// Convenience Variants
// ============================================================================

/**
 * FilledTextField - Filled background variant
 */
@Composable
fun FilledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: TextFieldSize = TextFieldSize.Medium,
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
    size: TextFieldSize = TextFieldSize.Medium,
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
    size: TextFieldSize = TextFieldSize.Medium,
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

// ============================================================================
// Specialized Convenience Functions
// ============================================================================

/**
 * EmailTextField - Pre-configured for email input
 */
@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextFieldVariant = TextFieldVariant.Outlined,
    size: TextFieldSize = TextFieldSize.Medium,
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
    size: TextFieldSize = TextFieldSize.Medium,
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
    size: TextFieldSize = TextFieldSize.Medium,
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
