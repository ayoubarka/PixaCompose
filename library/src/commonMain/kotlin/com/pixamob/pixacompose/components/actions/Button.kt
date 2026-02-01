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
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.feedback.PixaCircularIndicator
import com.pixamob.pixacompose.components.feedback.ProgressColors
import com.pixamob.pixacompose.components.feedback.ProgressSize
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class ButtonVariant {
    Solid,
    Tonal,
    Outlined,
    Ghost
}

enum class ButtonShape {
    Default,
    Pill,
    Circle
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

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

@Immutable
@Stable
data class ButtonColors(
    val background: Color,
    val content: Color,
    val border: Color = Color.Transparent,
    val ripple: Color = content.copy(alpha = 0.12f)
)

@Immutable
@Stable
data class ButtonStateColors(
    val default: ButtonColors,
    val disabled: ButtonColors,
    val loading: ButtonColors = default
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getButtonSizeConfig(size: SizeVariant): ButtonSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.None -> ButtonSizeConfig(
            height = 0.dp,
            horizontalPadding = 0.dp,
            iconSize = 0.dp,
            iconSpacing = 0.dp,
            minWidth = 0.dp,
            cornerRadius = 0.dp,
            textStyle = { typography.labelSmall }
        )

        SizeVariant.Nano -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Nano,
            horizontalPadding = HierarchicalSize.Padding.Nano,
            iconSize = HierarchicalSize.Icon.Nano,  // 12dp
            iconSpacing = HierarchicalSize.Spacing.Nano,  // 2dp
            minWidth = 0.dp,
            cornerRadius = HierarchicalSize.Radius.Nano,  // 2dp
            textStyle = { typography.actionMini }  // 10sp for 24dp button
        )

        SizeVariant.Compact -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Compact,  // 32dp
            horizontalPadding = HierarchicalSize.Padding.Small,  // 8dp
            iconSize = HierarchicalSize.Icon.Compact,  // 16dp
            iconSpacing = HierarchicalSize.Spacing.Compact,  // 4dp
            minWidth = 0.dp,
            cornerRadius = HierarchicalSize.Radius.Compact,  // 4dp
            textStyle = { typography.actionExtraSmall }  // 12sp for 32dp button
        )

        SizeVariant.Small -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Small,  // 36dp
            horizontalPadding = HierarchicalSize.Padding.Medium,  // 12dp
            iconSize = HierarchicalSize.Icon.Small,  // 20dp
            iconSpacing = HierarchicalSize.Spacing.Compact,  // 4dp
            minWidth = 0.dp,
            cornerRadius = HierarchicalSize.Radius.Small,  // 6dp
            textStyle = { typography.actionSmall }  // 14sp for 36dp button
        )

        SizeVariant.Medium -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Medium,  // 44dp
            horizontalPadding = HierarchicalSize.Padding.Large,  // 16dp
            iconSize = HierarchicalSize.Icon.Medium,  // 24dp
            iconSpacing = HierarchicalSize.Spacing.Compact,  // 4dp
            minWidth = HierarchicalSize.Spacing.Massive,  // 48dp
            cornerRadius = HierarchicalSize.Radius.Medium,  // 8dp
            textStyle = { typography.actionMedium }  // 16sp for 44dp button ⭐
        )

        SizeVariant.Large -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Large,  // 48dp
            horizontalPadding = HierarchicalSize.Padding.Large,  // 16dp
            iconSize = HierarchicalSize.Icon.Large,  // 28dp
            iconSpacing = HierarchicalSize.Spacing.Compact,  // 4dp
            minWidth = 120.dp,
            cornerRadius = HierarchicalSize.Radius.Large,  // 12dp
            textStyle = { typography.actionLarge }  // 18sp for 48dp button
        )

        SizeVariant.Huge -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Huge,  // 56dp
            horizontalPadding = HierarchicalSize.Padding.Huge,  // 20dp
            iconSize = HierarchicalSize.Icon.Huge,  // 32dp
            iconSpacing = HierarchicalSize.Spacing.Small,  // 8dp
            minWidth = 160.dp,
            cornerRadius = HierarchicalSize.Radius.Huge,  // 16dp
            textStyle = { typography.actionExtraLarge }  // 20sp for 56dp button
        )

        SizeVariant.Massive -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Massive,  // 64dp
            horizontalPadding = HierarchicalSize.Padding.Massive,  // 24dp
            iconSize = HierarchicalSize.Icon.Massive,  // 48dp
            iconSpacing = HierarchicalSize.Spacing.Medium,  // 12dp
            minWidth = 200.dp,
            cornerRadius = HierarchicalSize.Radius.Massive,  // 24dp
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
    val brandOrErrorContent =
        if (isDestructive) colors.errorContentDefault else colors.brandContentDefault
    val brandOrErrorSurface =
        if (isDestructive) colors.errorSurfaceDefault else colors.brandSurfaceDefault
    val brandOrErrorBorder =
        if (isDestructive) colors.errorBorderDefault else colors.brandBorderDefault

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
    loadingIcon: Painter? = null,
    size: SizeVariant = SizeVariant.Medium,
    shape: ButtonShape = ButtonShape.Default,
    colors: ButtonStateColors,
    elevation: Dp = 0.dp,
    contentAlignment: Alignment = Alignment.Center,
    arrangement: Arrangement.Horizontal = Arrangement.Center,
    description: String? = null,
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
        // ✅ Use Row directly instead of Box + Row
        Row(
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
                            BorderStroke(HierarchicalSize.Border.Compact, borderColor),
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
                    onClickLabel = description
                )
                .padding(
                    horizontal = if (shape == ButtonShape.Circle) {
                        0.dp
                    } else {
                        sizeConfig.horizontalPadding
                    }
                ),
            horizontalArrangement = arrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                if (loadingIcon != null) {
                    PixaIcon(
                        painter = loadingIcon,
                        contentDescription = "Loading",
                        size = sizeConfig.iconSize,
                        tint = contentColor
                    )
                } else {
                    PixaCircularIndicator(
                        progress = null,
                        modifier = Modifier.size(sizeConfig.iconSize),
                        sizePreset = ProgressSize.Small,
                        customColors = ProgressColors(
                            progress = contentColor,
                            track = contentColor.copy(alpha = 0.2f),
                            label = contentColor
                        )
                    )
                }
            } else {
                content()
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
 * - Content alignment and spacing control
 * - Custom colors via ButtonColors
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
 * @param description Accessibility description (recommended for icon-only buttons)
 * @param customColors Optional custom ButtonStateColors to override theme defaults
 * @param customIconSize Optional custom icon size to override size config
 * @param customTextStyle Optional custom text style to override size config
 * @param arrangement Content arrangement in the Row (Default: Center)
 *
 * @sample
 * ```
 * // Basic button
 * PixaButton(
 *     text = "Submit",
 *     onClick = { }
 * )
 *
 * // Button with custom colors
 * PixaButton(
 *     text = "Custom",
 *     variant = ButtonVariant.Solid,
 *     customColors = ButtonStateColors(
 *         default = ButtonColors(
 *             background = Color.Magenta,
 *             content = Color.White
 *         ),
 *         disabled = ButtonColors(
 *             background = Color.Gray,
 *             content = Color.LightGray
 *         )
 *     ),
 *     onClick = { }
 * )
 *
 * // Button with space between content
 * PixaButton(
 *     text = "Options",
 *     trailingIcon = painterResource(R.drawable.ic_arrow),
 *     horizontalArrangement = Arrangement.SpaceBetween,
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
    loadingIcon: Painter? = null,
    isLoading: Boolean = false,
    size: SizeVariant = SizeVariant.Medium,
    shape: ButtonShape = ButtonShape.Default,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    elevation: Dp? = null,
    customColors: ButtonStateColors? = null,
    customIconSize: Dp? = null,
    customTextStyle: TextStyle? = null,
    arrangement: Arrangement.Horizontal = Arrangement.Center,
    description: String? = null,
) {
    val sizeConfig = getButtonSizeConfig(size)

    // Show skeleton when isLoading = true
    if (isLoading) {
        val cornerRadius = when (shape) {
            ButtonShape.Default -> sizeConfig.cornerRadius
            ButtonShape.Pill -> sizeConfig.height / 2
            ButtonShape.Circle -> sizeConfig.height / 2
        }

        val buttonModifier = if (shape == ButtonShape.Circle) {
            modifier.size(sizeConfig.height)
        } else {
            modifier
                .height(sizeConfig.height)
                .widthIn(min = sizeConfig.minWidth)
        }

        Skeleton(
            modifier = buttonModifier,
            height = sizeConfig.height,
            shape = RoundedCornerShape(cornerRadius),
            shimmerEnabled = true
        )
        return
    }

    // Use custom colors if provided, otherwise use theme defaults
    // If customColors provided but isDestructive=true, still apply destructive styling
    val colors = if (customColors != null && !isDestructive) {
        customColors
    } else {
        getButtonTheme(variant, AppTheme.colors, isDestructive)
    }

    // Auto-elevation for Solid and Tonal variants (Material 3 style)
    val buttonElevation = elevation ?: when (variant) {
        ButtonVariant.Solid, ButtonVariant.Tonal -> HierarchicalSize.Shadow.Nano  // 1dp
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
        loadingIcon = loadingIcon,
        size = size,
        shape = effectiveShape,
        colors = colors,
        elevation = buttonElevation,
        arrangement = arrangement,
        description = description
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
        PixaIcon(
            painter = leadingIcon,
            contentDescription = null,
            size = iconSize
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
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }

    if (hasTrailingIcon) {
        if (hasText && shape != ButtonShape.Circle) {
            Spacer(modifier = Modifier.width(iconSpacing))
        }

        PixaIcon(
            painter = trailingIcon,
            contentDescription = null,
            size = iconSize
        )
    }
}
