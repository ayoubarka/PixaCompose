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

// ============================================================================
// ENUMS & TYPES
// ============================================================================

/**
 * TextArea visual style variant.
 *
 * The eBay Playbook Text Area spec defines no variant axis, so this stays the pre-existing
 * PixaCompose container vocabulary shared with [PixaTextField].
 */
enum class TextAreaVariant {
    Filled,    // Filled background
    Outlined,  // Border only
    Ghost      // Transparent, minimal
}

/**
 * Spec: "The default number of rows can be customized to increase or decrease the height of the
 * field." Default is 4 rows — expressed through Compose's native `minLines` rather than an eBay-style
 * `rows` param.
 */
private const val DefaultRows = 4

/**
 * Spec: "Padding left/right of content: 16px." Locked at the container level (like [PixaTextField]'s
 * horizontal padding), not scaled per [SizeVariant] — the spec states one value for all text areas.
 */
private val ContentHorizontalPadding = HierarchicalSize.Spacing.Large

/** Spec: "Padding top/bottom of content: 8px." Container-level constant, same reasoning as above. */
private val ContentVerticalPadding = HierarchicalSize.Spacing.Small

/** Spec: "Space between label and text area: 4px." */
private val LabelGap = HierarchicalSize.Spacing.Compact

/** Spec: "Space between text area and helper text: 8px." */
private val HelperGap = HierarchicalSize.Spacing.Small

/**
 * Border weight is NOT COVERED by the eBay Text Area spec. Rather than inventing a per-size ladder,
 * the text area inherits its sibling [PixaTextField]'s container model (1px default / 3px active) so
 * that a text field and a text area stacked in the same form read as one family.
 */
private val DefaultBorderWidth = HierarchicalSize.Border.Compact
private val ActiveBorderWidth = HierarchicalSize.Border.Large

// ============================================================================
// DATA CLASSES
// ============================================================================

/**
 * Per-size configuration for TextArea.
 *
 * Only the type scale varies by [SizeVariant]. Every spatial value the eBay spec pins down
 * (padding, gaps) is a container-level constant above, and the container radius/border are locked to
 * match [PixaTextField] — so nothing dimensional belongs in here.
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
 * Mirrors the four states the eBay spec actually defines — default, focus, disabled, error — plus
 * [readOnlyBorder] for PixaCompose's retained read-only affordance. There is deliberately no hover,
 * pressed, or success entry here: the Text Area spec defines none.
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

// ============================================================================
// THEME PROVIDER
// ============================================================================

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

// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * PixaTextArea — multi-line text input.
 *
 * ### Purpose
 * Spec: use "when longer values and sentences are encouraged." Do **not** use it when the expected
 * value is short — reach for [PixaTextField] instead.
 *
 * ### Anatomy
 * [label] → container (input area, holding the value or [placeholder]) → [helperText]/[errorText] and
 * the character counter. The spec defines no leading/trailing icons or accessories for a text area,
 * so this component intentionally exposes none.
 *
 * ### Variants
 * [TextAreaVariant] is a PixaCompose axis; the spec defines no variants.
 *
 * ### States
 * Default, focus, [enabled]`= false`, and [isError] — the four the spec defines. [readOnly] is
 * retained from the previous PixaCompose API (a focusable, submittable, non-editable field, which
 * the spec's disabled state explicitly is not). Focus/error/read-only widen the border to 3px.
 *
 * ### Sizing
 * Height is expressed in rows, per spec: [minLines] defaults to 4 and drives the container height via
 * the type scale, so the field is exactly N rows tall rather than a hardcoded dp. [maxLines] defaults
 * to [minLines], matching the spec's fixed-height/overflow model; raise it to opt into auto-grow.
 * [size] only shifts the type scale — the spec defines no size axis for text areas.
 *
 * ### Adaptive behavior
 * Spec: text areas "expand the full width of the screen up to the page margins" on small screens and
 * "expand up to their full width" on larger ones. The component always fills the width it is given;
 * the page margin is the caller's screen-level concern (`AppTheme.pageMargin`), not a second
 * responsive system inside the component.
 *
 * ### Customization
 * [maxLength] enables the counter. Content rules from the spec, enforced by writers rather than code:
 * labels are sentence case, no ending punctuation, a 1–3 word noun phrase; placeholders are realistic
 * examples ending in an ellipsis, never instructions or required information; helper text does not
 * repeat the label and stays to one line; error messages are neutral sentence case with no
 * "please"/"sorry"/"oops".
 *
 * ### Usage notes
 * Overflow follows the spec's "content shifts to keep the cursor in view" while focused, which is
 * Compose's native behavior for a row-bounded field. The spec's blur-time "scroll to the beginning"
 * and the browser's native resize indicator are **not** reproduced — both are web-platform
 * affordances, and this is an Android/iOS library with no browser chrome to inherit them from.
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param modifier Modifier for the text area
 * @param variant Visual style variant
 * @param size Size preset — type scale only
 * @param enabled Whether the field is enabled (not focusable; no value submitted — use [readOnly] if the value must still submit)
 * @param readOnly Whether the field is read-only (focusable and submittable, but not editable)
 * @param isError Whether to show the error state — [errorText] then replaces [helperText], per spec
 * @param label Optional label text
 * @param placeholder Optional placeholder text, replaced by [value] after the first character
 * @param helperText Optional helper text below the field
 * @param errorText Optional error text (shown when [isError] is true)
 * @param keyboardOptions Keyboard configuration
 * @param keyboardActions Keyboard actions
 * @param minLines The spec's "rows" — the field's height in lines
 * @param maxLines Maximum lines before the content scrolls; defaults to [minLines] (fixed height)
 * @param maxLength Optional maximum character length — showing the counter, per spec
 * @param interactionSource Interaction source for state
 * @param contentDescription Accessibility description
 * @param customTextColor Optional custom text color (defaults to theme color)
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

        // Container — height comes from minLines/maxLines (the spec's "rows"), not a fixed dp.
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

// ============================================================================
// CONVENIENCE VARIANTS
// ============================================================================

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

// ============================================================================
// SPECIALIZED CONVENIENCE FUNCTIONS
// ============================================================================

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
