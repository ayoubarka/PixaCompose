package com.pixamob.pixacompose.components.inputs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

// No component-specific enums — sizing is the shared [SizeVariant].

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class FormFieldColors(
    val label: Color,
    val requiredIndicator: Color,
    val hint: Color,
    val error: Color
)

@Immutable
@Stable
private data class FormFieldSizeConfig(
    val labelSpacing: Dp,
    val helperSpacing: Dp,
    val labelStyle: @Composable () -> TextStyle,
    val hintStyle: @Composable () -> TextStyle
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getFormFieldColors(colors: ColorPalette): FormFieldColors = FormFieldColors(
    label = colors.baseContentCaption,
    requiredIndicator = colors.errorContentDefault,
    hint = colors.baseContentCaption,
    error = colors.errorContentDefault
)

@Composable
private fun getFormFieldSizeConfig(size: SizeVariant): FormFieldSizeConfig {
    val typography = AppTheme.typography
    return FormFieldSizeConfig(
        labelSpacing = HierarchicalSize.Spacing.Small,
        helperSpacing = HierarchicalSize.Spacing.Compact,
        labelStyle = {
            when (size) {
                SizeVariant.Small, SizeVariant.Compact -> typography.labelSmall
                SizeVariant.Medium -> typography.overline
                SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> typography.labelLarge
                else -> typography.overline
            }
        },
        hintStyle = {
            when (size) {
                SizeVariant.Small, SizeVariant.Compact -> typography.captionLight
                SizeVariant.Medium -> typography.captionRegular
                SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> typography.bodyRegular
                else -> typography.captionRegular
            }
        }
    )
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL COMPONENTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun FormFieldLabel(
    label: String,
    required: Boolean,
    colors: FormFieldColors,
    labelStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        BasicText(
            text = label,
            style = labelStyle.copy(color = colors.label)
        )
        if (required) {
            BasicText(
                text = " *",
                style = labelStyle.copy(color = colors.requiredIndicator)
            )
        }
    }
}

@Composable
private fun FormFieldHelper(
    text: String,
    isError: Boolean,
    colors: FormFieldColors,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    BasicText(
        text = text,
        style = style.copy(color = if (isError) colors.error else colors.hint),
        modifier = modifier
    )
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaFormField — a stacked label + field content + helper text primitive.
 *
 * Use this to wrap non-text-field form controls (pickers, chip selectors, sliders,
 * color swatches) with the same label/hint/error scaffolding as [PixaTextField].
 *
 * ### Anatomy
 * [label] → spacing → [content] → spacing → optional hint/error text.
 *
 * ### Sizing
 * [SizeVariant] drives the label and hint typography. Spacing is fixed to the
 * existing [HierarchicalSize.Spacing] ladder.
 *
 * ### Customization
 * [customColors] overrides label/hint/error colors. [customLabelStyle] and
 * [customHintStyle] override the resolved typography.
 *
 * @param label Field label text
 * @param modifier Modifier for the outer column
 * @param required Whether to render a required indicator next to the label
 * @param hint Optional helper text shown below the content
 * @param error Optional error text; takes precedence over [hint] when both are supplied
 * @param size Size variant for label/hint typography (Default: [SizeVariant.Medium])
 * @param customColors Optional color override
 * @param customLabelStyle Optional label text style override
 * @param customHintStyle Optional hint text style override
 * @param content The field control itself
 */
@Composable
fun PixaFormField(
    label: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    hint: String? = null,
    error: String? = null,
    size: SizeVariant = SizeVariant.Medium,
    customColors: FormFieldColors? = null,
    customLabelStyle: TextStyle? = null,
    customHintStyle: TextStyle? = null,
    content: @Composable () -> Unit
) {
    val colors = customColors ?: getFormFieldColors(AppTheme.colors)
    val config = getFormFieldSizeConfig(size)
    val labelStyle = customLabelStyle ?: config.labelStyle()
    val hintStyle = customHintStyle ?: config.hintStyle()

    Column(modifier = modifier) {
        FormFieldLabel(
            label = label,
            required = required,
            colors = colors,
            labelStyle = labelStyle
        )

        Spacer(modifier = Modifier.height(config.labelSpacing))

        content()

        val helperText = error ?: hint
        if (helperText != null) {
            Spacer(modifier = Modifier.height(config.helperSpacing))
            FormFieldHelper(
                text = helperText,
                isError = error != null,
                colors = colors,
                style = hintStyle
            )
        }
    }
}
