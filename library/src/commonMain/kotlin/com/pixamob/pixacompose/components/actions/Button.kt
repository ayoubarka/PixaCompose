package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.Icon
import com.pixamob.pixacompose.components.feedback.CircularProgressIndicator
import com.pixamob.pixacompose.components.feedback.ProgressSize
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.BorderSize
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.ComponentSize
import com.pixamob.pixacompose.theme.IconSize
import com.pixamob.pixacompose.theme.RadiusSize
import com.pixamob.pixacompose.theme.ShadowSize
import com.pixamob.pixacompose.theme.Spacing

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Button Variant - Visual style of the button
 */
enum class ButtonVariant {
    /** Solid background with high emphasis (Primary actions: Submit, Save, Continue) */
    Solid,
    /** Subtle tonal background with medium emphasis (Featured secondary actions) */
    Tonal,
    /** Border only with medium emphasis (Secondary actions: Cancel, Back, Edit) */
    Outlined,
    /** Transparent background with low emphasis (Tertiary actions: Learn More, Details) */
    Ghost
}

/**
 * Button Size - Height and padding variants
 */
enum class ButtonSize {
    /** 24dp height - Compact spaces, inline actions */
    Mini,
    /** 32dp height - Dense UIs, toolbars */
    Compact,
    /** 36dp height - Forms, cards */
    Small,
    /** 44dp height - DEFAULT, primary touch target */
    Medium,
    /** 48dp height - Prominent actions */
    Large,
    /** 64dp height - Hero sections, landing pages */
    Huge
}

/**
 * Button Shape - Corner radius variants
 */
enum class ButtonShape {
    /** Rounded corners based on size */
    Default,
    /** Fully rounded (height / 2 radius) */
    Pill,
    /** Perfect circle (width = height, for icon-only buttons) */
    Circle
}

/**
 * Button Size Configuration
 */
@Immutable
@Stable
data class ButtonSizeConfig(
    val height: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    val iconSpacing: Dp,
    val minWidth: Dp,
    val cornerRadius: Dp,
    val textStyle: @Composable () -> TextStyle
)

/**
 * Button Colors for different states
 */
@Immutable
@Stable
data class ButtonColors(
    val background: Color,
    val content: Color,
    val border: Color = Color.Transparent,
    val ripple: Color = content.copy(alpha = 0.12f)
)

/**
 * Button State Colors
 */
@Immutable
@Stable
data class ButtonStateColors(
    val default: ButtonColors,
    val disabled: ButtonColors,
    val loading: ButtonColors = default
)

// ============================================================================
// SIZE CONFIGURATIONS
// ============================================================================

/**
 * Get size configuration for a button size variant
 */
@Composable
private fun getButtonSizeConfig(size: ButtonSize): ButtonSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        ButtonSize.Mini -> ButtonSizeConfig(
            height = ComponentSize.Minimal,
            horizontalPadding = Spacing.Small,
            iconSize = IconSize.Tiny,
            iconSpacing = Spacing.Micro,
            minWidth = 0.dp,
            cornerRadius = RadiusSize.Tiny,
            textStyle = { typography.labelSmall }
        )
        ButtonSize.Compact -> ButtonSizeConfig(
            height = ComponentSize.VerySmall,
            horizontalPadding = Spacing.Small,
            iconSize = IconSize.VerySmall,
            iconSpacing = Spacing.Tiny,
            minWidth = 0.dp,
            cornerRadius = RadiusSize.ExtraSmall,
            textStyle = { typography.labelMedium }
        )
        ButtonSize.Small -> ButtonSizeConfig(
            height = ComponentSize.ExtraSmall,
            horizontalPadding = Spacing.Medium,
            iconSize = IconSize.ExtraSmall,
            iconSpacing = Spacing.Tiny,
            minWidth = 0.dp,
            cornerRadius = RadiusSize.Small,
            textStyle = { typography.actionSmall }
        )
        ButtonSize.Medium -> ButtonSizeConfig(
            height = ComponentSize.Medium,
            horizontalPadding = Spacing.Large,
            iconSize = IconSize.Medium,
            iconSpacing = Spacing.ExtraSmall,
            minWidth = ComponentSize.Massive,
            cornerRadius = RadiusSize.Medium,
            textStyle = { typography.actionMedium }
        )
        ButtonSize.Large -> ButtonSizeConfig(
            height = ComponentSize.ExtraLarge,
            horizontalPadding = Spacing.Large,
            iconSize = IconSize.Large,
            iconSpacing = Spacing.ExtraSmall,
            minWidth = ComponentSize.VeryLarge * 1.5f,
            cornerRadius = RadiusSize.Large,
            textStyle = { typography.actionLarge }
        )
        ButtonSize.Huge -> ButtonSizeConfig(
            height = ComponentSize.Huge,
            horizontalPadding = Spacing.ExtraLarge,
            iconSize = IconSize.Huge,
            iconSpacing = Spacing.Small,
            minWidth = ComponentSize.Massive * 2,
            cornerRadius = RadiusSize.ExtraLarge,
            textStyle = { typography.actionHuge }
        )
    }
}

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get button theme colors for a variant
 */
@Composable
private fun getButtonTheme(
    variant: ButtonVariant,
    colors: ColorPalette,
    isDestructive: Boolean = false
): ButtonStateColors {
    // If destructive, use error colors instead of brand colors
    val brandOrErrorContent = if (isDestructive) colors.errorContentDefault else colors.brandContentDefault
    val brandOrErrorSurface = if (isDestructive) colors.errorSurfaceDefault else colors.brandSurfaceDefault
    val brandOrErrorBorder = if (isDestructive) colors.errorBorderDefault else colors.brandBorderDefault

    return when (variant) {
        ButtonVariant.Solid -> ButtonStateColors(
            default = ButtonColors(
                background = brandOrErrorContent,
                content = Color.White,
                ripple = Color.White.copy(alpha = 0.12f)
            ),
            disabled = ButtonColors(
                background = colors.baseSurfaceDisabled,
                content = colors.baseContentDisabled,
                ripple = Color.Transparent
            )
        )
        ButtonVariant.Tonal -> ButtonStateColors(
            default = ButtonColors(
                background = brandOrErrorSurface,
                content = brandOrErrorContent,
                ripple = brandOrErrorContent.copy(alpha = 0.12f)
            ),
            disabled = ButtonColors(
                background = colors.baseSurfaceDisabled,
                content = colors.baseContentDisabled,
                ripple = Color.Transparent
            )
        )
        ButtonVariant.Outlined -> ButtonStateColors(
            default = ButtonColors(
                background = Color.Transparent,
                content = brandOrErrorContent,
                border = brandOrErrorBorder,
                ripple = brandOrErrorContent.copy(alpha = 0.12f)
            ),
            disabled = ButtonColors(
                background = Color.Transparent,
                content = colors.baseContentDisabled,
                border = colors.baseBorderDisabled,
                ripple = Color.Transparent
            )
        )
        ButtonVariant.Ghost -> ButtonStateColors(
            default = ButtonColors(
                background = Color.Transparent,
                content = brandOrErrorContent,
                ripple = brandOrErrorContent.copy(alpha = 0.12f)
            ),
            disabled = ButtonColors(
                background = Color.Transparent,
                content = colors.baseContentDisabled,
                ripple = Color.Transparent
            )
        )
    }
}

// ============================================================================
// INTERNAL BUTTON (Core logic)
// ============================================================================

/**
 * Internal button implementation with core functionality
 */
@Composable
private fun InternalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ButtonSize = ButtonSize.Medium,
    shape: ButtonShape = ButtonShape.Default,
    colors: ButtonStateColors,
    elevation: Dp = 0.dp,
    contentAlignment: Alignment = Alignment.Center,
    contentDescription: String? = null,
    content: @Composable RowScope.() -> Unit
) {
    val sizeConfig = getButtonSizeConfig(size)

    // Determine current colors based on state
    val currentColors = when {
        loading -> colors.loading
        !enabled -> colors.disabled
        else -> colors.default
    }

    // Animate color transitions
    val backgroundColor by animateColorAsState(
        targetValue = currentColors.background,
        animationSpec = tween(150),
        label = "button_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = currentColors.content,
        animationSpec = tween(150),
        label = "button_content"
    )

    val borderColor by animateColorAsState(
        targetValue = currentColors.border,
        animationSpec = tween(150),
        label = "button_border"
    )

    // Determine corner radius based on shape
    val cornerRadius = when (shape) {
        ButtonShape.Default -> sizeConfig.cornerRadius
        ButtonShape.Pill -> sizeConfig.height / 2
        ButtonShape.Circle -> sizeConfig.height / 2
    }

    val buttonShape = RoundedCornerShape(cornerRadius)

    // For circle shape, width equals height
    val buttonModifier = if (shape == ButtonShape.Circle) {
        modifier.size(sizeConfig.height)
    } else {
        modifier
            .height(sizeConfig.height)
            .widthIn(min = sizeConfig.minWidth)
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = buttonModifier
                .shadow(
                    elevation = if (enabled) elevation else 0.dp,
                    shape = buttonShape,
                    clip = false
                )
                .clip(buttonShape)
                .background(backgroundColor)
                .then(
                    if (currentColors.border != Color.Transparent) {
                        Modifier.border(
                            BorderStroke(BorderSize.Tiny, borderColor),
                            buttonShape
                        )
                    } else Modifier
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = currentColors.ripple),
                    enabled = enabled && !loading,
                    role = Role.Button,
                    onClick = onClick,
                    onClickLabel = contentDescription
                ),
            contentAlignment = contentAlignment
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = if (shape == ButtonShape.Circle) {
                        0.dp
                    } else {
                        sizeConfig.horizontalPadding
                    }
                ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        progress = null, // Indeterminate
                        modifier = Modifier.size(sizeConfig.iconSize),
                        sizePreset = ProgressSize.Small,
                        customColors = com.pixamob.pixacompose.components.feedback.ProgressColors(
                            progress = contentColor,
                            track = contentColor.copy(alpha = 0.2f),
                            label = contentColor
                        )
                    )
                } else {
                    content()
                }
            }
        }
    }
}

// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * BaseButton - Main button component with full customization
 *
 * A flexible button component built from Compose primitives supporting:
 * - Multiple visual variants (Solid, Tonal, Outlined, Ghost)
 * - Destructive mode for critical actions
 * - Six size options from Mini to Huge
 * - Three shape variants (Default, Pill, Circle)
 * - Optional text with leading and trailing icons
 * - Icon-only buttons (when text is null or empty)
 * - Loading state with progress indicator
 * - Disabled state
 * - Smooth animations
 * - Full theme integration
 * - Elevation support
 * - Accessibility support with contentDescription
 *
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for styling
 * @param text Optional button text content (null or empty for icon-only buttons)
 * @param variant Visual style variant (Default: Solid)
 * @param isDestructive Whether this is a destructive action (uses error colors)
 * @param enabled Whether the button is enabled (Default: true)
 * @param loading Whether the button shows loading state (Default: false)
 * @param size Size variant (Default: Medium)
 * @param shape Shape variant (Default: Default)
 * @param leadingIcon Optional icon before text
 * @param trailingIcon Optional icon after text
 * @param elevation Shadow elevation (Default: 0dp, 1dp for Solid/Tonal)
 * @param contentDescription Accessibility description (recommended for icon-only buttons)
 * @param customColors Optional custom colors to override theme
 * @param customIconSize Optional custom icon size to override size config
 * @param customTextStyle Optional custom text style to override size config
 *
 * @sample
 * ```
 * // Basic button
 * PixaButton(
 *     text = "Submit",
 *     onClick = { }
 * )
 *
 * // Icon-only button
 * PixaButton(
 *     onClick = { },
 *     shape = ButtonShape.Circle,
 *     leadingIcon = painterResource(R.drawable.ic_add),
 *     contentDescription = "Add item"
 * )
 *
 * // Destructive button
 * PixaButton(
 *     text = "Delete",
 *     isDestructive = true,
 *     onClick = { }
 * )
 *
 * // Button with icon
 * PixaButton(
 *     text = "Save",
 *     leadingIcon = painterResource(R.drawable.ic_save),
 *     onClick = { }
 * )
 *
 * // Loading button
 * PixaButton(
 *     text = "Processing...",
 *     loading = true,
 *     onClick = { }
 * )
 *
 * // Outlined destructive button
 * PixaButton(
 *     text = "Remove",
 *     variant = ButtonVariant.Outlined,
 *     isDestructive = true,
 *     onClick = { }
 * )
 * ```
 */
@Composable
fun PixaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    variant: ButtonVariant = ButtonVariant.Solid,
    isDestructive: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ButtonSize = ButtonSize.Medium,
    shape: ButtonShape = ButtonShape.Default,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    elevation: Dp? = null,
    contentDescription: String? = null,
    customColors: ButtonStateColors? = null,
    customIconSize: Dp? = null,
    customTextStyle: TextStyle? = null
) {
    val colors = customColors ?: getButtonTheme(variant, AppTheme.colors, isDestructive)
    val sizeConfig = getButtonSizeConfig(size)

    // Auto-elevation for Solid and Tonal variants (Material 3 style)
    val buttonElevation = elevation ?: when (variant) {
        ButtonVariant.Solid, ButtonVariant.Tonal -> ShadowSize.Tiny
        else -> 0.dp
    }

    // Determine if this is icon-only button
    val hasText = !text.isNullOrBlank()
    val hasIcons = leadingIcon != null || trailingIcon != null

    // For icon-only buttons without explicit circle shape, suggest circle shape
    val effectiveShape = if (!hasText && hasIcons && shape == ButtonShape.Default) {
        ButtonShape.Circle
    } else {
        shape
    }

    InternalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        size = size,
        shape = effectiveShape,
        colors = colors,
        elevation = buttonElevation,
        contentDescription = contentDescription
    ) {
        ButtonContent(
            text = text,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            iconSize = customIconSize ?: sizeConfig.iconSize,
            iconSpacing = sizeConfig.iconSpacing,
            textStyle = customTextStyle ?: sizeConfig.textStyle(),
            shape = effectiveShape
        )
    }
}

/**
 * Button content layout helper
 */
@Composable
private fun RowScope.ButtonContent(
    text: String?,
    leadingIcon: Painter?,
    trailingIcon: Painter?,
    iconSize: Dp,
    iconSpacing: Dp,
    textStyle: TextStyle,
    shape: ButtonShape
) {
    val hasText = !text.isNullOrBlank()
    val hasLeadingIcon = leadingIcon != null
    val hasTrailingIcon = trailingIcon != null
    val hasAnyContent = hasText || hasLeadingIcon || hasTrailingIcon

    // Fallback: ensure minimum width for empty buttons
    if (!hasAnyContent) {
        Spacer(modifier = Modifier.width(iconSize))
        return
    }

    if (hasLeadingIcon) {
        Icon(
            painter = leadingIcon,
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.CenterVertically)
        )

        if (hasText && shape != ButtonShape.Circle) {
            Spacer(modifier = Modifier.width(iconSpacing))
        }
    }

    if (hasText && shape != ButtonShape.Circle) {
        Text(
            text = text,
            style = textStyle.copy(lineHeight = textStyle.fontSize),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f, fill = false)
        )
    }

    if (hasTrailingIcon) {
        if (hasText && shape != ButtonShape.Circle) {
            Spacer(modifier = Modifier.width(iconSpacing))
        }

        Icon(
            painter = trailingIcon,
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.CenterVertically)
        )
    }
}

// ============================================================================
// CONVENIENCE VARIANTS
// ============================================================================

/**
 * Solid base button - High emphasis with filled background
 */
@Composable
fun SolidButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ButtonSize = ButtonSize.Medium,
    shape: ButtonShape = ButtonShape.Default,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    isDestructive: Boolean = false,
    contentDescription: String? = null
) = PixaButton(
    onClick = onClick,
    modifier = modifier,
    text = text,
    variant = ButtonVariant.Solid,
    isDestructive = isDestructive,
    enabled = enabled,
    loading = loading,
    size = size,
    shape = shape,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    contentDescription = contentDescription
)

/**
 * Tonal base button - Medium emphasis with subtle background
 */
@Composable
fun TonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ButtonSize = ButtonSize.Medium,
    shape: ButtonShape = ButtonShape.Default,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    isDestructive: Boolean = false,
    contentDescription: String? = null
) = PixaButton(
    onClick = onClick,
    modifier = modifier,
    text = text,
    variant = ButtonVariant.Tonal,
    isDestructive = isDestructive,
    enabled = enabled,
    loading = loading,
    size = size,
    shape = shape,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    contentDescription = contentDescription
)

/**
 * Outlined base button - Medium emphasis with border
 */
@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ButtonSize = ButtonSize.Medium,
    shape: ButtonShape = ButtonShape.Default,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    isDestructive: Boolean = false,
    contentDescription: String? = null
) = PixaButton(
    onClick = onClick,
    modifier = modifier,
    text = text,
    variant = ButtonVariant.Outlined,
    isDestructive = isDestructive,
    enabled = enabled,
    loading = loading,
    size = size,
    shape = shape,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    contentDescription = contentDescription
)

/**
 * Ghost base button - Low emphasis, transparent background
 */
@Composable
fun GhostButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.Medium,
    shape: ButtonShape = ButtonShape.Default,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    isDestructive: Boolean = false,
    contentDescription: String? = null
) = PixaButton(
    onClick = onClick,
    modifier = modifier,
    text = text,
    variant = ButtonVariant.Ghost,
    isDestructive = isDestructive,
    enabled = enabled,
    loading = false,
    size = size,
    shape = shape,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    contentDescription = contentDescription
)

/**
 * Destructive base button - For critical/dangerous actions (Solid variant)
 */
@Composable
fun DestructiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ButtonSize = ButtonSize.Medium,
    shape: ButtonShape = ButtonShape.Default,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    contentDescription: String? = null
) = PixaButton(
    onClick = onClick,
    modifier = modifier,
    text = text,
    variant = ButtonVariant.Solid,
    isDestructive = true,
    enabled = enabled,
    loading = loading,
    size = size,
    shape = shape,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    contentDescription = contentDescription
)