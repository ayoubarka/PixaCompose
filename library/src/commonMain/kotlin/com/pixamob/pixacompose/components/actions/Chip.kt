package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.components.display.Icon
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.IconSize
import com.pixamob.pixacompose.theme.RadiusSize
import com.pixamob.pixacompose.theme.Spacing

// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * Chip Variant - Visual style of the chip
 * Chips are compact elements that represent an attribute, text, entity, or action
 */
enum class ChipVariant {
    /** Solid background with high emphasis - Selected filters, primary tags */
    Solid,
    /** Subtle tonal background - Default filter state, categories */
    Tonal,
    /** Border only with transparent background - Unselected filters, options */
    Outlined,
    /** Transparent background with minimal styling - Tags, labels in lists */
    Ghost
}

/**
 * Chip Size - Height and padding variants
 */
enum class ChipSize {
    /** 24dp height - Compact tags, dense lists */
    Small,
    /** 32dp height - DEFAULT, filters, tags */
    Medium,
    /** 40dp height - Prominent filters, interactive chips */
    Large
}

/**
 * Chip Type - Behavior and interaction style
 */
enum class ChipType {
    /** Non-interactive, display only (tags, labels) */
    Static,
    /** Clickable, single selection (filters, options) */
    Selectable,
    /** Clickable with dismiss action (removable tags, filters) */
    Dismissible,
    /** Input chip for user-created content (email recipients, search tags) */
    Input
}

/**
 * Chip Size Configuration
 */
@Immutable
@Stable
data class ChipSizeConfig(
    val height: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    val iconSpacing: Dp,
    val cornerRadius: Dp,
    val textStyle: @Composable () -> TextStyle
)

/**
 * Chip Colors for different states
 */
@Immutable
@Stable
data class ChipColors(
    val background: Color,
    val content: Color,
    val border: Color = Color.Transparent,
    val ripple: Color = content.copy(alpha = 0.12f)
)

/**
 * Chip State Colors
 */
@Immutable
@Stable
data class ChipStateColors(
    val default: ChipColors,
    val selected: ChipColors,
    val disabled: ChipColors
)

// ============================================================================
// SIZE CONFIGURATIONS
// ============================================================================

/**
 * Get size configuration for a chip size variant
 */
@Composable
private fun getChipSizeConfig(size: ChipSize): ChipSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        ChipSize.Small -> ChipSizeConfig(
            height = 24.dp,
            horizontalPadding = Spacing.Small,
            iconSize = IconSize.Tiny,
            iconSpacing = Spacing.Micro,
            cornerRadius = RadiusSize.Small,
            textStyle = { typography.labelSmall }
        )
        ChipSize.Medium -> ChipSizeConfig(
            height = 32.dp,
            horizontalPadding = Spacing.Medium,
            iconSize = IconSize.Small,
            iconSpacing = Spacing.Small,
            cornerRadius = RadiusSize.Medium,
            textStyle = { typography.labelMedium }
        )
        ChipSize.Large -> ChipSizeConfig(
            height = 40.dp,
            horizontalPadding = Spacing.Large,
            iconSize = IconSize.Medium,
            iconSpacing = Spacing.Small,
            cornerRadius = RadiusSize.Large,
            textStyle = { typography.labelLarge }
        )
    }
}

// ============================================================================
// THEME/STYLE SYSTEM
// ============================================================================

/**
 * Get chip theme colors based on variant
 */
@Composable
private fun getChipTheme(
    variant: ChipVariant,
    colors: ColorPalette
): ChipStateColors {
    return when (variant) {
        ChipVariant.Solid -> ChipStateColors(
            default = ChipColors(
                background = colors.baseSurfaceDefault,
                content = colors.baseContentBody,
                border = Color.Transparent
            ),
            selected = ChipColors(
                background = colors.brandSurfaceDefault,
                content = colors.baseContentNegative,
                border = Color.Transparent
            ),
            disabled = ChipColors(
                background = colors.baseSurfaceDisabled,
                content = colors.baseContentDisabled,
                border = Color.Transparent
            )
        )

        ChipVariant.Tonal -> ChipStateColors(
            default = ChipColors(
                background = colors.baseSurfaceSubtle,
                content = colors.baseContentBody,
                border = Color.Transparent
            ),
            selected = ChipColors(
                background = colors.brandSurfaceSubtle,
                content = colors.brandContentDefault,
                border = Color.Transparent
            ),
            disabled = ChipColors(
                background = colors.baseSurfaceDisabled,
                content = colors.baseContentDisabled,
                border = Color.Transparent
            )
        )

        ChipVariant.Outlined -> ChipStateColors(
            default = ChipColors(
                background = Color.Transparent,
                content = colors.baseContentBody,
                border = colors.baseBorderDefault
            ),
            selected = ChipColors(
                background = Color.Transparent,
                content = colors.brandContentDefault,
                border = colors.brandBorderDefault
            ),
            disabled = ChipColors(
                background = Color.Transparent,
                content = colors.baseContentDisabled,
                border = colors.baseBorderDisabled
            )
        )

        ChipVariant.Ghost -> ChipStateColors(
            default = ChipColors(
                background = Color.Transparent,
                content = colors.baseContentBody,
                border = Color.Transparent
            ),
            selected = ChipColors(
                background = Color.Transparent,
                content = colors.brandContentDefault,
                border = Color.Transparent
            ),
            disabled = ChipColors(
                background = Color.Transparent,
                content = colors.baseContentDisabled,
                border = Color.Transparent
            )
        )
    }
}

// ============================================================================
// BASE CHIP COMPONENT
// ============================================================================

/**
 * Base Chip Component - Internal implementation
 */
@Composable
private fun PixaChip(
    text: String,
    variant: ChipVariant,
    size: ChipSize,
    type: ChipType,
    selected: Boolean,
    enabled: Boolean,
    onClick: (() -> Unit)?,
    onDismiss: (() -> Unit)?,
    leadingIcon: Painter?,
    trailingIcon: Painter?,
    modifier: Modifier = Modifier,
    customColors: ChipStateColors? = null,
    contentDescriptionText: String? = null
) {
    val colors = AppTheme.colors
    val config = getChipSizeConfig(size)
    val theme = getChipTheme(variant, colors)

    // Determine current state colors
    val stateColors = when {
        !enabled -> theme.disabled
        selected -> theme.selected
        else -> theme.default
    }

    // Use custom colors if provided, otherwise use theme colors
    val finalColors = if (customColors != null) {
        when {
            !enabled -> customColors.disabled
            selected -> customColors.selected
            else -> customColors.default
        }
    } else {
        stateColors
    }

    // Animated colors with spring for snappier feel
    val backgroundColor by animateColorAsState(
        targetValue = finalColors.background,
        animationSpec = spring(),
        label = "chipBackgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = finalColors.content,
        animationSpec = spring(),
        label = "chipContentColor"
    )

    val borderColor by animateColorAsState(
        targetValue = finalColors.border,
        animationSpec = spring(),
        label = "chipBorderColor"
    )

    val isClickable = enabled && onClick != null && type != ChipType.Static

    Box(
        modifier = modifier
            .sizeIn(minHeight = if (isClickable) 44.dp else config.height)
            .height(config.height)
            .clip(RoundedCornerShape(config.cornerRadius))
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(config.cornerRadius)
                    )
                } else Modifier
            )
            .background(backgroundColor)
            .semantics {
                if (type == ChipType.Selectable) {
                    role = Role.Checkbox
                    this.selected = selected
                }
                contentDescriptionText?.let {
                    contentDescription = it
                } ?: run {
                    contentDescription = text
                }
            }
            .then(
                if (isClickable) {
                    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
                    Modifier.clickable(
                        enabled = enabled,
                        onClick = onClick!!,
                        indication = ripple(color = finalColors.ripple),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                } else Modifier
            )
            .padding(horizontal = config.horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading icon
                if (leadingIcon != null) {
                    Icon(
                        painter = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(config.iconSize),
                        tint = contentColor
                    )
                    Spacer(modifier = Modifier.width(config.iconSpacing))
                }

                // Text
                Text(
                    text = text,
                    style = config.textStyle(),
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.widthIn(max = 200.dp)
                )

                // Trailing icon
                if (trailingIcon != null && type != ChipType.Dismissible) {
                    Spacer(modifier = Modifier.width(config.iconSpacing))
                    Icon(
                        painter = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(config.iconSize),
                        tint = contentColor
                    )
                }

                // Dismiss icon for dismissible chips
                if (type == ChipType.Dismissible && onDismiss != null && enabled) {
                    Spacer(modifier = Modifier.width(config.iconSpacing))
                    Box(
                        modifier = Modifier
                            .size(config.iconSize)
                            .clip(RoundedCornerShape(RadiusSize.Full))
                            .clickable(
                                onClick = onDismiss,
                                indication = ripple(bounded = true, color = contentColor.copy(alpha = 0.2f)),
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // TODO: Replace with custom close icon painter
                        Text(
                            text = "×",
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = config.iconSize.value.sp,
                                color = contentColor
                            )
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// PUBLIC API - CHIP VARIANTS
// ============================================================================

/**
 * Chip - Compact element representing an attribute, text, entity, or action
 *
 * @param text The text content of the chip
 * @param variant Visual style variant (default: Tonal)
 * @param size Size variant (default: Medium)
 * @param type Behavior type (default: Static)
 * @param selected Whether the chip is selected (for Selectable type)
 * @param enabled Whether the chip is enabled
 * @param onClick Click handler (required for Selectable and Input types)
 * @param onDismiss Dismiss handler (required for Dismissible type)
 * @param leadingIcon Optional icon before text
 * @param trailingIcon Optional icon after text (not used with Dismissible type)
 * @param modifier Modifier for customization
 * @param customColors Optional custom state colors for different states
 * @param contentDescription Optional accessibility description (defaults to text)
 *
 * Common use cases:
 * - Filter chips: variant = Outlined, type = Selectable
 * - Tag chips: variant = Tonal, type = Static
 * - Removable tags: variant = Solid, type = Dismissible
 * - Input chips: variant = Solid, type = Input
 *
 * @sample
 * ```
 * // Basic static chip
 * Chip(text = "Featured")
 *
 * // Filter chip with selection
 * var selected by remember { mutableStateOf(false) }
 * Chip(
 *     text = "Electronics",
 *     variant = ChipVariant.Outlined,
 *     type = ChipType.Selectable,
 *     selected = selected,
 *     onClick = { selected = !selected }
 * )
 *
 * // Input chip with dismiss
 * Chip(
 *     text = "example@email.com",
 *     variant = ChipVariant.Solid,
 *     type = ChipType.Dismissible,
 *     onDismiss = { /* remove */ }
 * )
 * ```
 */
@Composable
fun Chip(
    text: String,
    modifier: Modifier = Modifier,
    variant: ChipVariant = ChipVariant.Tonal,
    size: ChipSize = ChipSize.Medium,
    type: ChipType = ChipType.Static,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    customColors: ChipStateColors? = null,
    contentDescription: String? = null
) {
    PixaChip(
        text = text,
        variant = variant,
        size = size,
        type = type,
        selected = selected,
        enabled = enabled,
        onClick = onClick,
        onDismiss = onDismiss,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = modifier,
        customColors = customColors,
        contentDescriptionText = contentDescription
    )
}

// ============================================================================
// CONVENIENCE VARIANTS
// ============================================================================

/**
 * Filter Chip - For filtering content (outlined, selectable)
 *
 * @sample
 * ```
 * var selected by remember { mutableStateOf(false) }
 * FilterChip(
 *     text = "Electronics",
 *     selected = selected,
 *     onClick = { selected = !selected }
 * )
 * ```
 */
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ChipSize = ChipSize.Medium,
    enabled: Boolean = true,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    customColors: ChipStateColors? = null,
    contentDescription: String? = null
) {
    Chip(
        text = text,
        variant = ChipVariant.Outlined,
        size = size,
        type = ChipType.Selectable,
        selected = selected,
        enabled = enabled,
        onClick = onClick,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = modifier,
        customColors = customColors,
        contentDescription = contentDescription
    )
}

/**
 * Input Chip - For user-created content (solid, dismissible)
 *
 * @sample
 * ```
 * InputChip(
 *     text = "john@example.com",
 *     onDismiss = { /* remove email */ }
 * )
 * ```
 */
@Composable
fun InputChip(
    text: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    size: ChipSize = ChipSize.Medium,
    enabled: Boolean = true,
    leadingIcon: Painter? = null,
    customColors: ChipStateColors? = null,
    contentDescription: String? = null
) {
    Chip(
        text = text,
        variant = ChipVariant.Solid,
        size = size,
        type = ChipType.Dismissible,
        selected = false,
        enabled = enabled,
        onDismiss = onDismiss,
        leadingIcon = leadingIcon,
        modifier = modifier,
        customColors = customColors,
        contentDescription = contentDescription
    )
}

/**
 * Suggestion Chip - For suggested actions or content (tonal, clickable)
 *
 * @sample
 * ```
 * SuggestionChip(
 *     text = "Search nearby restaurants",
 *     onClick = { /* perform search */ }
 * )
 * ```
 */
@Composable
fun SuggestionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ChipSize = ChipSize.Medium,
    enabled: Boolean = true,
    leadingIcon: Painter? = null,
    customColors: ChipStateColors? = null,
    contentDescription: String? = null
) {
    Chip(
        text = text,
        variant = ChipVariant.Tonal,
        size = size,
        type = ChipType.Selectable,
        selected = false,
        enabled = enabled,
        onClick = onClick,
        leadingIcon = leadingIcon,
        modifier = modifier,
        customColors = customColors,
        contentDescription = contentDescription
    )
}

/**
 * Tag Chip - For static tags/labels (ghost, non-interactive)
 *
 * @sample
 * ```
 * TagChip(
 *     text = "New",
 *     variant = ChipVariant.Ghost
 * )
 * ```
 */
@Composable
fun TagChip(
    text: String,
    modifier: Modifier = Modifier,
    size: ChipSize = ChipSize.Small,
    variant: ChipVariant = ChipVariant.Ghost,
    leadingIcon: Painter? = null,
    customColors: ChipStateColors? = null,
    contentDescription: String? = null
) {
    Chip(
        text = text,
        variant = variant,
        size = size,
        type = ChipType.Static,
        selected = false,
        enabled = true,
        leadingIcon = leadingIcon,
        modifier = modifier,
        customColors = customColors,
        contentDescription = contentDescription
    )
}
