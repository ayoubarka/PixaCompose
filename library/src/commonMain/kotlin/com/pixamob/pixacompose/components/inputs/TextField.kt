package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixamob.pixacompose.components.display.Icon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils

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
            height = ComponentSize.Small,
            horizontalPadding = Spacing.Medium,
            verticalPadding = Spacing.ExtraSmall,
            textStyle = typography.bodySmall,
            labelTextStyle = typography.labelSmall,
            helperTextStyle = typography.labelSmall,
            iconSize = IconSize.Small,
            borderWidth = BorderSize.Tiny,
            cornerRadius = RadiusSize.Small
        )
        TextFieldSize.Medium -> TextFieldConfig(
            height = ComponentSize.Medium,
            horizontalPadding = Spacing.Large,
            verticalPadding = Spacing.Small,
            textStyle = typography.bodyRegular,
            labelTextStyle = typography.labelMedium,
            helperTextStyle = typography.labelMedium,
            iconSize = IconSize.Medium,
            borderWidth = BorderSize.Standard,
            cornerRadius = RadiusSize.Medium
        )
        TextFieldSize.Large -> TextFieldConfig(
            height = ComponentSize.Large,
            horizontalPadding = Spacing.ExtraLarge,
            verticalPadding = Spacing.Medium,
            textStyle = typography.bodyLarge,
            labelTextStyle = typography.labelLarge,
            helperTextStyle = typography.labelLarge,
            iconSize = IconSize.Large,
            borderWidth = BorderSize.Thick,
            cornerRadius = RadiusSize.Large
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
data class TextFieldColors(
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
 */
@Composable
private fun TextFieldVariant.colors(
    isFocused: Boolean,
    enabled: Boolean
): TextFieldColors {
    val colors = AppTheme.colors

    return when (this) {
        TextFieldVariant.Filled -> TextFieldColors(
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
        TextFieldVariant.Outlined -> TextFieldColors(
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
        TextFieldVariant.Ghost -> TextFieldColors(
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
// Base Component
// ============================================================================

/**
 * BaseTextField - Core text input component
 *
 * Single/multi-line text input with variants, sizes, floating labels, and full customization.
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
 * @param label Optional label text (floats when focused/filled)
 * @param placeholder Optional placeholder text
 * @param helperText Optional helper text below field
 * @param errorText Optional error text (shown when isError=true)
 * @param leadingIcon Optional leading icon
 * @param trailingIcon Optional trailing icon
 * @param onTrailingIconClick Optional click handler for trailing icon
 * @param showClearButton Show clear button when text is not empty
 * @param visualTransformation Visual transformation (e.g., password)
 * @param keyboardOptions Keyboard configuration
 * @param keyboardActions Keyboard actions
 * @param singleLine Whether to limit to single line
 * @param maxLines Maximum lines (for multi-line)
 * @param maxLength Optional maximum character length
 * @param showCharacterCount Show character counter
 * @param characterCountThreshold Show counter when length > (maxLength * threshold)
 * @param customColors Optional color overrides
 * @param floatingLabel Enable floating label animation
 * @param interactionSource Interaction source for state
 * @param contentDescription Accessibility description
 *
 * @sample
 * ```
 * var text by remember { mutableStateOf("") }
 * BaseTextField(
 *     value = text,
 *     onValueChange = { text = it },
 *     label = "Email",
 *     placeholder = "Enter your email",
 *     variant = TextFieldVariant.Outlined,
 *     floatingLabel = true,
 *     showClearButton = true
 * )
 * ```
 */
@Composable
fun BaseTextField(
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
    onTrailingIconClick: (() -> Unit)? = null,
    showClearButton: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    maxLength: Int? = null,
    showCharacterCount: Boolean = maxLength != null,
    characterCountThreshold: Float = 0.7f,
    customColors: TextFieldColors? = null,
    floatingLabel: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null
) {
    val config = size.config()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val defaultColors = variant.colors(isFocused, enabled)
    val colors = customColors ?: defaultColors

    val hasValue = value.isNotEmpty()
    val labelShouldFloat = floatingLabel && (isFocused || hasValue)

    // Animations
    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledBorder
            isError -> colors.errorBorder
            isFocused -> colors.focusedBorder
            else -> colors.border
        },
        animationSpec = AnimationUtils.standardSpring()
    )

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (!enabled) colors.disabledBackground else colors.background,
        animationSpec = AnimationUtils.standardSpring()
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isFocused && variant == TextFieldVariant.Outlined) config.borderWidth * 1.2f else config.borderWidth,
        animationSpec = tween(durationMillis = 200)
    )

    // Floating label animation
    val labelScale by animateFloatAsState(
        targetValue = if (labelShouldFloat) 0.85f else 1f,
        animationSpec = tween(durationMillis = 200)
    )

    val labelOffsetY by animateDpAsState(
        targetValue = if (labelShouldFloat) -(config.labelTextStyle.fontSize.value * 1.2).dp else 0.dp,
        animationSpec = tween(durationMillis = 200)
    )

    Column(
        modifier = modifier.semantics {
            contentDescription?.let { this.contentDescription = it }
            if (isError) {
                error("Error: ${errorText ?: "Invalid input"}")
            }
        }
    ) {
        // Floating label (above field when floating)
        if (label != null && floatingLabel && labelShouldFloat) {
            Text(
                text = label,
                style = config.labelTextStyle.copy(fontSize = (config.labelTextStyle.fontSize.value * 0.85f).sp),
                color = if (isError) colors.errorText else colors.label,
                modifier = Modifier.padding(
                    start = config.horizontalPadding,
                    bottom = Spacing.ExtraSmall
                )
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
            textStyle = config.textStyle.copy(color = if (enabled) colors.text else colors.disabledText),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = visualTransformation,
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
                        verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                    ) {
                        // Leading icon
                        if (leadingIcon != null) {
                            Icon(
                                painter = leadingIcon,
                                contentDescription = null,
                                tint = colors.label,
                                modifier = Modifier
                                    .size(config.iconSize)
                                    .then(if (!singleLine) Modifier.padding(top = Spacing.ExtraSmall) else Modifier)
                            )
                        }

                        // Text input area with inline label or placeholder
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
                        ) {
                            // Inline label (when not floating)
                            if (label != null && !floatingLabel && value.isEmpty() && !isFocused) {
                                Text(
                                    text = label,
                                    style = config.labelTextStyle,
                                    color = colors.placeholder
                                )
                            }
                            // Placeholder (shown when no label or label is floating)
                            else if (value.isEmpty() && placeholder != null && (label == null || labelShouldFloat)) {
                                Text(
                                    text = placeholder,
                                    style = config.textStyle,
                                    color = colors.placeholder
                                )
                            }
                            innerTextField()
                        }

                        // Clear button or trailing icon
                        val showClear = showClearButton && hasValue && enabled
                        val effectiveTrailingIcon = if (showClear) null else trailingIcon

                        if (showClear) {
                            // Clear button (×)
                            Box(
                                modifier = Modifier
                                    .size(config.iconSize)
                                    .clickable { onValueChange("") }
                                    .then(if (!singleLine) Modifier.padding(top = Spacing.ExtraSmall) else Modifier),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "×",
                                    style = TextStyle(
                                        fontSize = (config.iconSize.value * 1.2f).sp,
                                        color = colors.label
                                    )
                                )
                            }
                        } else if (effectiveTrailingIcon != null) {
                            val iconModifier = Modifier
                                .size(config.iconSize)
                                .then(if (!singleLine) Modifier.padding(top = Spacing.ExtraSmall) else Modifier)
                                .then(
                                    if (onTrailingIconClick != null) {
                                        Modifier.clickable { onTrailingIconClick() }
                                    } else Modifier
                                )

                            Icon(
                                painter = effectiveTrailingIcon,
                                contentDescription = null,
                                tint = colors.label,
                                modifier = iconModifier
                            )
                        }
                    }
                }
            }
        )

        // Helper/Error text and character counter row
        val bottomText = if (isError && errorText != null) errorText else helperText
        val shouldShowCounter = showCharacterCount && maxLength != null && value.length > (maxLength * characterCountThreshold).toInt()

        if (bottomText != null || shouldShowCounter) {
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
                if (bottomText != null) {
                    Text(
                        text = bottomText,
                        style = config.helperTextStyle,
                        color = if (isError) colors.errorText else colors.helperText,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }

                if (shouldShowCounter && maxLength != null) {
                    Text(
                        text = "${value.length}/$maxLength",
                        style = config.helperTextStyle,
                        color = if (value.length >= maxLength) colors.errorText else colors.helperText
                    )
                }
            }
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
    onTrailingIconClick: (() -> Unit)? = null,
    showClearButton: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    maxLength: Int? = null,
    floatingLabel: Boolean = true
) {
    BaseTextField(
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
        onTrailingIconClick = onTrailingIconClick,
        showClearButton = showClearButton,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        maxLength = maxLength,
        floatingLabel = floatingLabel
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
    onTrailingIconClick: (() -> Unit)? = null,
    showClearButton: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    maxLength: Int? = null,
    floatingLabel: Boolean = true
) {
    BaseTextField(
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
        onTrailingIconClick = onTrailingIconClick,
        showClearButton = showClearButton,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        maxLength = maxLength,
        floatingLabel = floatingLabel
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
    onTrailingIconClick: (() -> Unit)? = null,
    showClearButton: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    maxLength: Int? = null,
    floatingLabel: Boolean = false
) {
    BaseTextField(
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
        onTrailingIconClick = onTrailingIconClick,
        showClearButton = showClearButton,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        maxLength = maxLength,
        floatingLabel = floatingLabel
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
    errorText: String? = "Invalid email address",
    showClearButton: Boolean = true
) {
    BaseTextField(
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
        showClearButton = showClearButton,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true
    )
}

/**
 * PasswordTextField - Pre-configured for password input with visibility toggle
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
    showVisibilityToggle: Boolean = true,
    trailingIcon: Painter? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    BaseTextField(
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
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = if (showVisibilityToggle) null else trailingIcon,
        onTrailingIconClick = if (showVisibilityToggle) {
            { passwordVisible = !passwordVisible }
        } else null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}

/**
 * SearchTextField - Pre-configured for search input
 * Note: leadingIcon param should be provided with a search icon painter
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
    leadingIcon: Painter? = null,
    showClearButton: Boolean = true,
    onSearch: (() -> Unit)? = null
) {
    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        size = size,
        enabled = enabled,
        placeholder = placeholder,
        leadingIcon = leadingIcon, // Caller should provide search icon
        showClearButton = showClearButton,
        floatingLabel = false,
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
