package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * TextArea visual style variant, sharing the container vocabulary with [PixaTextField].
 */
enum class TextAreaVariant {
    Filled,    // Filled background
    Outlined,  // Border only
    Ghost      // Transparent, minimal
}

/** Default is 4 rows, expressed through Compose's native `minLines`. */
private const val DefaultRows = 4

/** Horizontal padding locked at the container level, matching [PixaTextField]. */
private val ContentHorizontalPadding = HierarchicalSize.Spacing.Large

/** Vertical padding, container-level constant. */
private val ContentVerticalPadding = HierarchicalSize.Spacing.Small

/** Spec: "Space between label and text area: 4px." */
private val LabelGap = HierarchicalSize.Spacing.Compact

/** Spec: "Space between text area and helper text: 8px." */
private val HelperGap = HierarchicalSize.Spacing.Small

/**
 * Inherited from sibling [PixaTextField]'s container model (1px default / 3px active) so that
 * a text field and a text area stacked in the same form read as one family.
 */
private val DefaultBorderWidth = HierarchicalSize.Border.Compact
private val ActiveBorderWidth = HierarchicalSize.Border.Large

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Per-size configuration for TextArea.
 *
 * Only the type scale varies by [SizeVariant]. Padding, gaps, radius, and border are
 * container-level constants matched to [PixaTextField], so no spatial values are size-dependent.
 */
@Stable
private data class TextAreaConfig(
    val textStyle: TextStyle,
    val labelTextStyle: TextStyle,
    val helperTextStyle: TextStyle
)

/**
 * Colors for TextArea states.
 *
 * Four states: default, focus, disabled, error — plus [readOnlyBorder] for the read-only affordance.
 * No hover, pressed, or success entries: these are not applicable to this input type.
 */
@Stable
private data class TextAreaColors(
    val background: Color,
    val border: Color,
    val focusedBorder: Color,
    val errorBorder: Color,
    val readOnlyBorder: Color,
    val text: Color,
    val placeholder: Color,
    val label: Color,
    val helperText: Color,
    val errorText: Color,
    val disabledBackground: Color,
    val disabledBorder: Color,
    val disabledText: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Get configuration for a given size. Type scale only — see [TextAreaConfig].
 * Matches [PixaTextField]'s ladder so the two inputs share a type rhythm.
 */
@Composable
private fun SizeVariant.config(): TextAreaConfig {
    val typography = AppTheme.typography
    return when (this) {
        SizeVariant.Small -> TextAreaConfig(
            textStyle = typography.bodyLight,
            labelTextStyle = typography.labelSmall,
            helperTextStyle = typography.captionLight
        )

        SizeVariant.Large -> TextAreaConfig(
            textStyle = typography.bodyBold,
            labelTextStyle = typography.labelLarge,
            helperTextStyle = typography.captionBold
        )

        // Medium is the anchor tier and the fallback for the tiers this component doesn't scale to.
        else -> TextAreaConfig(
            textStyle = typography.bodyRegular,
            labelTextStyle = typography.labelMedium,
            helperTextStyle = typography.captionRegular
        )
    }
}

/**
 * Get colors for a TextArea variant.
 *
 * Placeholder maps to `baseContentHint` — the spec calls placeholder "ghost text", which is exactly
 * the hint content role, so no alpha fudging is needed.
 */
@Composable
private fun TextAreaVariant.colors(
    isError: Boolean,
    isFocused: Boolean,
    enabled: Boolean
): TextAreaColors {
    val colors = AppTheme.colors

    val text = when {
        !enabled -> colors.baseContentDisabled
        isError -> colors.errorContentDefault
        else -> colors.baseContentTitle
    }
    val label = if (isFocused) colors.brandContentDefault else colors.baseContentBody

    return when (this) {
        TextAreaVariant.Filled -> TextAreaColors(
            background = if (enabled) colors.baseSurfaceSubtle else colors.baseSurfaceDisabled,
            border = Color.Transparent,
            focusedBorder = colors.brandBorderDefault,
            errorBorder = colors.errorBorderDefault,
            readOnlyBorder = colors.baseBorderDefault,
            text = text,
            placeholder = colors.baseContentHint,
            label = label,
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
            readOnlyBorder = colors.baseBorderDefault,
            text = text,
            placeholder = colors.baseContentHint,
            label = label,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            disabledBackground = Color.Transparent,
            disabledBorder = colors.baseBorderDisabled,
            disabledText = colors.baseContentDisabled
        )

        TextAreaVariant.Ghost -> TextAreaColors(
            background = Color.Transparent,
            border = Color.Transparent,
            focusedBorder = colors.baseBorderSubtle,
            errorBorder = colors.errorBorderDefault,
            readOnlyBorder = colors.baseBorderSubtle,
            text = text,
            placeholder = colors.baseContentHint,
            label = label,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            disabledBackground = Color.Transparent,
            disabledBorder = Color.Transparent,
            disabledText = colors.baseContentDisabled
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaTextArea — Multi-line text input for longer-form content.
 *
 * ### Anatomy
 * [label] → input (text entry with [placeholder]) → [helperText]/[errorText].
 *
 * ### States
 * Default, focus, enabled/disabled, [isError], [readOnly]. Focus/error/read-only widen the border to 3px.
 *
 * ### Sizing
 * Height is expressed in rows via [minLines] (default 4). [maxLines] defaults to [minLines] (fixed);
 * raise it for auto-grow. [size] shifts the type scale only.
 *
 * ### Customization
 * [maxLength] enables the character counter.
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param modifier Modifier
 * @param variant Visual style variant
 * @param size Size preset (type scale only)
 * @param enabled Whether the field is enabled
 * @param readOnly Whether the field is read-only
 * @param isError Whether to show the error state — [errorText] replaces [helperText]
 * @param label Optional label text
 * @param placeholder Optional placeholder text
 * @param helperText Optional helper text below the field
 * @param errorText Optional error text (shown when [isError] is true)
 * @param keyboardOptions Keyboard configuration
 * @param keyboardActions Keyboard actions
 * @param minLines The field's height in lines (default 4)
 * @param maxLines Maximum lines before scrolling; defaults to [minLines] (fixed)
 * @param maxLength Optional maximum character length — shows a counter
 * @param interactionSource Interaction source for state
 * @param contentDescription Accessibility description
 * @param customTextColor Optional custom text color
 * @param customFocusedTextColor Optional custom text color when focused
 */
@Composable
fun PixaTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextAreaVariant = TextAreaVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    minLines: Int = DefaultRows,
    maxLines: Int = minLines,
    maxLength: Int? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null,
    customTextColor: Color? = null,
    customFocusedTextColor: Color? = null
) {
    val config = size.config()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val colors = variant.colors(isError, isFocused, enabled)
    val shape = AppTheme.shapes.rounded.medium

    // Custom colors override the variant colors, which already resolve error/focus/enabled.
    val effectiveTextColor = when {
        customFocusedTextColor != null && isFocused -> customFocusedTextColor
        customTextColor != null -> customTextColor
        else -> colors.text
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledBorder
            isError -> colors.errorBorder
            readOnly -> colors.readOnlyBorder
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
        targetValue = if (isFocused || isError || readOnly) ActiveBorderWidth else DefaultBorderWidth,
        animationSpec = AnimationUtils.fastTween()
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
                style = config.labelTextStyle.copy(
                    color = if (isError) colors.errorText else colors.label
                ),
                modifier = Modifier.padding(bottom = LabelGap)
            )
        }

        // Container height comes from minLines/maxLines, not a fixed dp.
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(if (maxLength != null) newValue.take(maxLength) else newValue)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = config.textStyle.copy(color = effectiveTextColor),
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
                        .clip(shape)
                        .background(animatedBackgroundColor)
                        .then(
                            if (animatedBorderColor != Color.Transparent) {
                                Modifier.border(
                                    width = animatedBorderWidth,
                                    color = animatedBorderColor,
                                    shape = shape
                                )
                            } else Modifier
                        )
                        .padding(
                            horizontal = ContentHorizontalPadding,
                            vertical = ContentVerticalPadding
                        ),
                    contentAlignment = Alignment.TopStart
                ) {
                    // Placeholder — "ghost text", replaced by the value after the first character.
                    if (value.isEmpty() && placeholder != null) {
                        BasicText(
                            text = placeholder,
                            style = config.textStyle.copy(color = colors.placeholder)
                        )
                    }
                    innerTextField()
                }
            }
        )

        // Spec: the error message replaces any existing helper text.
        val bottomText = if (isError && errorText != null) errorText else helperText
        // Spec: the counter "appears when a character limit is set" — no threshold, no separate flag.
        val counterText = maxLength?.let { "${value.length} / $it" }

        if (bottomText != null || counterText != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = ContentHorizontalPadding,
                        end = ContentHorizontalPadding,
                        top = HelperGap
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (bottomText != null) {
                    BasicText(
                        text = bottomText,
                        style = config.helperTextStyle.copy(
                            color = if (isError) colors.errorText else colors.helperText
                        ),
                        modifier = Modifier.weight(1f, fill = false)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (counterText != null) {
                    BasicText(
                        text = counterText,
                        style = config.helperTextStyle.copy(
                            color = if (maxLength != null && value.length >= maxLength) {
                                colors.errorText
                            } else {
                                colors.helperText
                            }
                        )
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * FilledTextArea - Filled background variant
 */
@Composable
fun FilledTextArea(
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    minLines: Int = DefaultRows,
    maxLines: Int = minLines,
    maxLength: Int? = null
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
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        minLines = minLines,
        maxLines = maxLines,
        maxLength = maxLength
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
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    minLines: Int = DefaultRows,
    maxLines: Int = minLines,
    maxLength: Int? = null
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
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        minLines = minLines,
        maxLines = maxLines,
        maxLength = maxLength
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
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    minLines: Int = DefaultRows,
    maxLines: Int = minLines,
    maxLength: Int? = null
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
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        minLines = minLines,
        maxLines = maxLines,
        maxLength = maxLength
    )
}

// ════════════════════════════════════════════════════════════════════════════
// SPECIALIZED CONVENIENCE FUNCTIONS
// ════════════════════════════════════════════════════════════════════════════

/** Sentence-cased free text is what every preset below collects. */
private val ProseKeyboardOptions = KeyboardOptions(
    capitalization = KeyboardCapitalization.Sentences,
    keyboardType = KeyboardType.Text,
    imeAction = ImeAction.Default
)

/**
 * CommentTextArea - Pre-configured for comments. Auto-grows past its 4 rows up to 6.
 */
@Composable
fun CommentTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextAreaVariant = TextAreaVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    label: String = "Comment",
    placeholder: String = "Write your comment...",
    maxLength: Int = 500
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
        keyboardOptions = ProseKeyboardOptions,
        maxLines = 6,
        maxLength = maxLength
    )
}

/**
 * BioTextArea - Pre-configured for biography/description. Auto-grows past its 4 rows up to 8.
 */
@Composable
fun BioTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextAreaVariant = TextAreaVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    label: String = "Bio",
    placeholder: String = "Tell us about yourself...",
    maxLength: Int = 300
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
        keyboardOptions = ProseKeyboardOptions,
        maxLines = 8,
        maxLength = maxLength
    )
}

/**
 * NoteTextArea - Pre-configured for notes. Auto-grows without bound.
 */
@Composable
fun NoteTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: TextAreaVariant = TextAreaVariant.Filled,
    size: SizeVariant = SizeVariant.Medium,
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
        keyboardOptions = ProseKeyboardOptions,
        maxLines = Int.MAX_VALUE,
        maxLength = maxLength
    )
}
