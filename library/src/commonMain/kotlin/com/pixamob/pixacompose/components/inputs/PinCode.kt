package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.feedback.PixaCircularIndicator
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Uber Base's two display modes. [Masked] replaces committed digits with a dot — "for sensitive
 * scenarios (bank accounts, card PINs)." [Unmasked] shows the actual characters — "for clarity" in
 * non-sensitive contexts like verbal dictation.
 */
enum class PinCodeVariant {
    Masked,
    Unmasked
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
private data class PinCodeSizeConfig(
    val slotSize: Dp,
    val cornerRadius: Dp,
    val slotSpacing: Dp,
    val textStyle: TextStyle,
    val labelStyle: TextStyle,
    val helperStyle: TextStyle
)

@Immutable
@Stable
private data class PinCodeColors(
    val background: Color,
    val border: Color,
    val focusedBorder: Color,
    val errorBorder: Color,
    val successBorder: Color,
    val readOnlyBorder: Color,
    val disabledBackground: Color,
    val disabledBorder: Color,
    val text: Color,
    val placeholder: Color,
    val label: Color,
    val helperText: Color,
    val errorText: Color,
    val successText: Color,
    val disabledText: Color,
    val hoverOverlay: Color,
    val pressedOverlay: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/** Mirrors [TextFieldVariant]'s box family (`boxVariant`) so a PIN code slot and a text field read as
 * the same input family — reuses the enum itself rather than duplicating a parallel Filled/Outlined/
 * Ghost axis, since [PinCodeVariant] already owns the PIN-specific Masked/Unmasked axis. */
@Composable
private fun TextFieldVariant.pinCodeColors(): PinCodeColors {
    val colors = AppTheme.colors
    val hoverOverlay = Color.Black.copy(alpha = 0.04f)
    val pressedOverlay = Color.Black.copy(alpha = 0.08f)

    return when (this) {
        TextFieldVariant.Filled -> PinCodeColors(
            background = colors.baseSurfaceSubtle,
            border = Color.Transparent,
            focusedBorder = colors.brandBorderDefault,
            errorBorder = colors.errorBorderDefault,
            successBorder = colors.successBorderDefault,
            readOnlyBorder = colors.baseBorderDefault,
            disabledBackground = colors.baseSurfaceDisabled,
            disabledBorder = Color.Transparent,
            text = colors.baseContentTitle,
            placeholder = colors.baseContentBody.copy(alpha = 0.5f),
            label = colors.baseContentBody,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            successText = colors.successContentDefault,
            disabledText = colors.baseContentDisabled,
            hoverOverlay = hoverOverlay,
            pressedOverlay = pressedOverlay
        )

        TextFieldVariant.Outlined -> PinCodeColors(
            background = Color.Transparent,
            border = colors.baseBorderDefault,
            focusedBorder = colors.brandBorderDefault,
            errorBorder = colors.errorBorderDefault,
            successBorder = colors.successBorderDefault,
            readOnlyBorder = colors.baseBorderDefault,
            disabledBackground = Color.Transparent,
            disabledBorder = colors.baseBorderDisabled,
            text = colors.baseContentTitle,
            placeholder = colors.baseContentBody.copy(alpha = 0.5f),
            label = colors.baseContentBody,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            successText = colors.successContentDefault,
            disabledText = colors.baseContentDisabled,
            hoverOverlay = hoverOverlay,
            pressedOverlay = pressedOverlay
        )

        TextFieldVariant.Ghost -> PinCodeColors(
            background = Color.Transparent,
            border = Color.Transparent,
            focusedBorder = colors.baseBorderDefault.copy(alpha = 0.3f),
            errorBorder = colors.errorBorderDefault.copy(alpha = 0.3f),
            successBorder = colors.successBorderDefault.copy(alpha = 0.3f),
            readOnlyBorder = colors.baseBorderDefault.copy(alpha = 0.3f),
            disabledBackground = Color.Transparent,
            disabledBorder = Color.Transparent,
            text = colors.baseContentTitle,
            placeholder = colors.baseContentBody.copy(alpha = 0.4f),
            label = colors.baseContentBody,
            helperText = colors.baseContentBody,
            errorText = colors.errorContentDefault,
            successText = colors.successContentDefault,
            disabledText = colors.baseContentDisabled,
            hoverOverlay = hoverOverlay,
            pressedOverlay = pressedOverlay
        )
    }
}

/** Slot side length reuses [HierarchicalSize.Input] (same height ladder [PixaTextField] uses) since a
 * PIN slot is conceptually a square, single-character text field. */
@Composable
private fun SizeVariant.pinCodeConfig(): PinCodeSizeConfig {
    val typography = AppTheme.typography
    return when (this) {
        SizeVariant.Small -> PinCodeSizeConfig(
            slotSize = HierarchicalSize.Input.Small,
            cornerRadius = HierarchicalSize.Radius.Medium,
            slotSpacing = HierarchicalSize.Spacing.Small,
            textStyle = typography.titleBold,
            labelStyle = typography.labelSmall,
            helperStyle = typography.captionLight
        )
        SizeVariant.Large -> PinCodeSizeConfig(
            slotSize = HierarchicalSize.Input.Large,
            cornerRadius = HierarchicalSize.Radius.Medium,
            slotSpacing = HierarchicalSize.Spacing.Medium,
            textStyle = typography.headlineBold,
            labelStyle = typography.labelLarge,
            helperStyle = typography.captionBold
        )
        else -> PinCodeSizeConfig(
            slotSize = HierarchicalSize.Input.Medium,
            cornerRadius = HierarchicalSize.Radius.Medium,
            slotSpacing = HierarchicalSize.Spacing.Small,
            textStyle = typography.titleBold,
            labelStyle = typography.labelMedium,
            helperStyle = typography.captionRegular
        )
    }
}

/** Uber Base: border "1px weight" default, "3px" active/error/success/read-only — mirrors the same
 * locked ratio [PixaTextField] applies (see `TextField.kt`'s `DefaultBorderWidth`/`ActiveBorderWidth`);
 * declared again here (not imported) since those are file-private and a PIN slot's border isn't the
 * same visual element as a text field's container border, just the same design-system ratio. */
private val DefaultSlotBorderWidth = HierarchicalSize.Border.Compact
private val ActiveSlotBorderWidth = HierarchicalSize.Border.Large

/**
 * Resolves the placeholder character shown in an empty slot. Uber Base names three modes: "default
 * (displays as '9999' for 4-slot field)," a custom placeholder matching the code length 1:1, or a
 * shorter custom placeholder that loops across slots.
 */
private fun resolvePlaceholderChar(placeholder: String?, index: Int, length: Int): Char? = when {
    placeholder == null -> '9'
    placeholder.isEmpty() -> null
    placeholder.length >= length -> placeholder[index]
    else -> placeholder[index % placeholder.length]
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL PIN CODE
// ════════════════════════════════════════════════════════════════════════════

/** Hover/pressed state-layer scrim — Uber Base's literal "4% / 8% black overlay," matching the
 * convention already established across this library's other Uber Base migrations. */
@Composable
private fun BoxScope.PinSlotOverlayScrim(color: Color, shape: Shape) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .clip(shape)
            .background(color)
    )
}

@Composable
private fun PinSlot(
    index: Int,
    value: String,
    length: Int,
    isFocused: Boolean,
    isHovered: Boolean,
    isPressed: Boolean,
    enabled: Boolean,
    readOnly: Boolean,
    showValidation: Boolean,
    isError: Boolean,
    isSuccess: Boolean,
    variant: PinCodeVariant,
    placeholder: String?,
    colors: PinCodeColors,
    config: PinCodeSizeConfig,
    modifier: Modifier = Modifier
) {
    val char = value.getOrNull(index)
    val isActive = enabled && !readOnly && isFocused && index == value.length.coerceAtMost(length - 1)
    val shape = RoundedCornerShape(config.cornerRadius)

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledBorder
            showValidation && isError -> colors.errorBorder
            showValidation && isSuccess -> colors.successBorder
            readOnly -> colors.readOnlyBorder
            isActive -> colors.focusedBorder
            else -> colors.border
        },
        animationSpec = AnimationUtils.smoothSpring()
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isActive || readOnly || (showValidation && (isError || isSuccess))) ActiveSlotBorderWidth else DefaultSlotBorderWidth,
        animationSpec = AnimationUtils.standardTween(200)
    )
    val backgroundColor = if (!enabled) colors.disabledBackground else colors.background
    val textColor = if (!enabled) colors.disabledText else colors.text

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(backgroundColor)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(borderWidth, borderColor, shape)
                } else {
                    Modifier
                }
            )
            .semantics {
                // Masked fields omit the value from the accessibility tree per spec ("omit value
                // announcement for security"); unmasked fields announce the actual character.
                if (variant == PinCodeVariant.Masked) {
                    hideFromAccessibility()
                } else {
                    contentDescription = char?.toString() ?: "Slot ${index + 1} of $length, empty"
                }
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            char != null && variant == PinCodeVariant.Masked -> Box(
                modifier = Modifier
                    .size(config.slotSize * 0.25f)
                    .clip(AppTheme.shapes.pill)
                    .background(textColor)
            )
            char != null -> BasicText(text = char.toString(), style = config.textStyle.copy(color = textColor, textAlign = TextAlign.Center))
            else -> resolvePlaceholderChar(placeholder, index, length)?.let {
                BasicText(text = it.toString(), style = config.textStyle.copy(color = colors.placeholder, textAlign = TextAlign.Center))
            }
        }

        when {
            isPressed && enabled -> PinSlotOverlayScrim(colors.pressedOverlay, shape)
            isHovered && enabled -> PinSlotOverlayScrim(colors.hoverOverlay, shape)
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaPinCode — a Personal Identification Number field for secure authentication and access
 * control.
 *
 * ### Anatomy
 * Unlike a single-container text field, this renders "as many containers as the length of the
 * required code" (one square slot per character) while treating input as a single string — a single
 * hidden [BasicTextField] drives all slots so paste, backspace, and auto-advance all fall out of
 * normal string editing rather than manually juggling per-slot focus.
 *
 * ### Content model
 * [length] (spec: 4–8 characters, coerced into that range) and [placeholder] resolve per-slot via
 * [resolvePlaceholderChar] — default "9" digit, a custom string matching [length] 1:1, or a shorter
 * custom string that loops across slots.
 *
 * ### Variants
 * [variant] (Masked/Unmasked, the spec's real axis — see [PinCodeVariant]) × [boxVariant] (reuses
 * [TextFieldVariant]'s Filled/Outlined/Ghost box family for visual consistency with [PixaTextField]).
 *
 * ### States
 * Enabled, focused ("Active" — 3px `brandBorderDefault` outline on the next-empty slot), disabled,
 * read-only (3px neutral outline, focusable/tab-navigable/value-submitted, mirrors [PixaTextField]),
 * hover/pressed (4%/8% black overlay), loading ([isLoading] — a [PixaCircularIndicator] overlay).
 * [isError]/[isSuccess] are real per spec but gated by [length]: "You cannot surface either on an
 * individual slot level, only once the input has been filled out" — both are ignored, on every slot,
 * until `value.length == length`.
 *
 * ### Sizing
 * [size] resolves slot side length through [HierarchicalSize.Input] (same ladder [PixaTextField]
 * uses). Spec: "intrinsic width... takes the size of its content," with slots shrinking to fit via
 * auto-layout Fill when space-constrained — implemented here as `Modifier.weight(1f)` + `aspectRatio(1f)`
 * per slot inside the row, the direct Compose translation of that behavior.
 *
 * ### Usage notes
 * Primary contexts per spec: two-factor authentication, account linking, trip/delivery ID, earner PIN
 * codes. [onComplete] fires once when the code reaches [length] characters — not named explicitly in
 * the spec text, but the standard OTP auto-submit pattern implied by "Complete" as a named state and
 * the 2FA use case; kept opt-in (nullable) so it's a convenience, not an imposed behavior.
 *
 * @param value Current code value (single string, one character per slot)
 * @param onValueChange Callback when the code changes; already length/keyboard-filtered before this fires
 * @param length Number of slots (spec: 4–8, coerced into that range)
 * @param modifier Modifier for the whole component (label + slots + helper text)
 * @param variant Masked (dot) or Unmasked (plaintext) display — see [PinCodeVariant]
 * @param boxVariant Slot box family — reuses [TextFieldVariant] (Filled, Outlined, Ghost)
 * @param size Size preset (Small, Medium, Large)
 * @param enabled Whether the field is enabled (not focusable — use [readOnly] if the value must still submit)
 * @param readOnly Whether the field is read-only (focusable, tab-navigable, value submitted — 3px outline)
 * @param isError Whether to show the error validation state — only visible once `value.length == length`
 * @param isSuccess Whether to show the success validation state — only visible once `value.length == length`
 * @param isLoading Whether to show a loading overlay (e.g. verifying the code server-side)
 * @param label Optional label text above the slots
 * @param placeholder Optional placeholder string — see [resolvePlaceholderChar] for the three modes
 * @param helperText Optional helper text below the slots
 * @param errorText Optional error text (shown when [isError] and complete)
 * @param keyboardOptions Keyboard configuration (default: numeric PIN keypad)
 * @param interactionSource Interaction source for state
 * @param onComplete Fires once when the code reaches [length] characters
 * @param contentDescription Accessibility description for the whole field
 */
@Composable
fun PixaPinCode(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int = 4,
    modifier: Modifier = Modifier,
    variant: PinCodeVariant = PinCodeVariant.Masked,
    boxVariant: TextFieldVariant = TextFieldVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    isSuccess: Boolean = false,
    isLoading: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onComplete: ((String) -> Unit)? = null,
    contentDescription: String? = null
) {
    val slotCount = length.coerceIn(4, 8)
    val config = size.pinCodeConfig()
    val colors = boxVariant.pinCodeColors()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val showValidation = value.length == slotCount

    LaunchedEffect(value, slotCount) {
        if (value.length == slotCount) onComplete?.invoke(value)
    }

    Column(
        modifier = modifier.semantics {
            contentDescription?.let { this.contentDescription = it }
        }
    ) {
        if (label != null) {
            BasicText(
                text = label,
                style = config.labelStyle.copy(color = if (showValidation && isError) colors.errorText else colors.label),
                modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Compact)
            )
        }

        Box {
            BasicTextField(
                value = value,
                onValueChange = { newValue -> onValueChange(newValue.take(slotCount)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .hoverable(interactionSource = interactionSource, enabled = enabled),
                enabled = enabled && !isLoading,
                readOnly = readOnly,
                textStyle = config.textStyle.copy(color = Color.Transparent),
                keyboardOptions = keyboardOptions,
                singleLine = true,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(Color.Transparent),
                decorationBox = { innerTextField ->
                    Box {
                        // The real text field is invisible (alpha, not zero-sized — a zero-sized field
                        // breaks touch-to-focus and cursor placement on some platforms) — it only owns
                        // focus/IME/paste/backspace. Slot rendering below is purely presentational.
                        Box(modifier = Modifier.matchParentSize().alpha(0f)) { innerTextField() }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(config.slotSpacing),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(slotCount) { index ->
                                PinSlot(
                                    index = index,
                                    value = value,
                                    length = slotCount,
                                    isFocused = isFocused,
                                    isHovered = isHovered,
                                    isPressed = isPressed,
                                    enabled = enabled,
                                    readOnly = readOnly,
                                    showValidation = showValidation,
                                    isError = isError,
                                    isSuccess = isSuccess,
                                    variant = variant,
                                    placeholder = placeholder,
                                    colors = colors,
                                    config = config,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(config.cornerRadius)),
                    contentAlignment = Alignment.Center
                ) {
                    PixaCircularIndicator(
                        modifier = Modifier.size(config.slotSize * 0.6f),
                        sizePreset = SizeVariant.Small,
                        contentDescription = "Verifying code"
                    )
                }
            }
        }

        val bottomText = if (showValidation && isError && errorText != null) errorText else helperText
        if (bottomText != null) {
            BasicText(
                text = bottomText,
                style = config.helperStyle.copy(
                    color = when {
                        showValidation && isError -> colors.errorText
                        showValidation && isSuccess -> colors.successText
                        else -> colors.helperText
                    }
                ),
                maxLines = 3,
                modifier = Modifier.padding(top = HierarchicalSize.Spacing.Compact)
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/** MaskedPinCode — dot display, for sensitive PINs (bank accounts, card PINs). */
@Composable
fun MaskedPinCode(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int = 4,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    onComplete: ((String) -> Unit)? = null
) {
    PixaPinCode(
        value = value,
        onValueChange = onValueChange,
        length = length,
        modifier = modifier,
        variant = PinCodeVariant.Masked,
        size = size,
        enabled = enabled,
        isError = isError,
        label = label,
        helperText = helperText,
        errorText = errorText,
        onComplete = onComplete
    )
}

/** UnmaskedPinCode — plaintext display, for non-sensitive contexts like verbal dictation. */
@Composable
fun UnmaskedPinCode(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int = 4,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    onComplete: ((String) -> Unit)? = null
) {
    PixaPinCode(
        value = value,
        onValueChange = onValueChange,
        length = length,
        modifier = modifier,
        variant = PinCodeVariant.Unmasked,
        size = size,
        enabled = enabled,
        isError = isError,
        label = label,
        helperText = helperText,
        errorText = errorText,
        onComplete = onComplete
    )
}
