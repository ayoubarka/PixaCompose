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
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant

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
private fun getChipSizeConfig(size: SizeVariant): ChipSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.None -> ChipSizeConfig(
            height = 0.dp,
            horizontalPadding = 0.dp,
            iconSize = 0.dp,
            iconSpacing = 0.dp,
            cornerRadius = 0.dp,
            textStyle = { typography.labelSmall }
        )
        SizeVariant.Nano -> ChipSizeConfig(
            height = HierarchicalSize.Chip.Nano,  // 20dp
            horizontalPadding = HierarchicalSize.Padding.Nano,  // 2dp
            iconSize = HierarchicalSize.Icon.Nano,  // 12dp
            iconSpacing = HierarchicalSize.Spacing.Nano,  // 2dp
            cornerRadius = HierarchicalSize.Radius.Nano,  // 2dp
            textStyle = { typography.labelSmall }
        )
        SizeVariant.Compact -> ChipSizeConfig(
            height = HierarchicalSize.Chip.Compact,  // 24dp
            horizontalPadding = HierarchicalSize.Padding.Small,  // 8dp
            iconSize = HierarchicalSize.Icon.Nano,  // 12dp
            iconSpacing = HierarchicalSize.Spacing.Nano,  // 2dp
            cornerRadius = HierarchicalSize.Radius.Small,  // 6dp
            textStyle = { typography.labelSmall }
        )
        SizeVariant.Small -> ChipSizeConfig(
            height = HierarchicalSize.Chip.Small,  // 28dp
            horizontalPadding = HierarchicalSize.Padding.Small,  // 8dp
            iconSize = HierarchicalSize.Icon.Compact,  // 16dp
            iconSpacing = HierarchicalSize.Spacing.Nano,  // 2dp
            cornerRadius = HierarchicalSize.Radius.Small,  // 6dp
            textStyle = { typography.labelSmall }
        )
        SizeVariant.Medium -> ChipSizeConfig(
            height = HierarchicalSize.Chip.Medium,  // 32dp
            horizontalPadding = HierarchicalSize.Padding.Medium,  // 12dp
            iconSize = HierarchicalSize.Icon.Small,  // 20dp
            iconSpacing = HierarchicalSize.Spacing.Small,  // 8dp
            cornerRadius = HierarchicalSize.Radius.Medium,  // 8dp
            textStyle = { typography.labelMedium }
        )
        SizeVariant.Large -> ChipSizeConfig(
            height = HierarchicalSize.Chip.Large,  // 36dp
            horizontalPadding = HierarchicalSize.Padding.Large,  // 16dp
            iconSize = HierarchicalSize.Icon.Medium,  // 24dp
            iconSpacing = HierarchicalSize.Spacing.Small,  // 8dp
            cornerRadius = HierarchicalSize.Radius.Large,  // 12dp
            textStyle = { typography.labelLarge }
        )
        SizeVariant.Huge -> ChipSizeConfig(
            height = HierarchicalSize.Chip.Huge,  // 40dp
            horizontalPadding = HierarchicalSize.Padding.Large,  // 16dp
            iconSize = HierarchicalSize.Icon.Large,  // 28dp
            iconSpacing = HierarchicalSize.Spacing.Small,  // 8dp
            cornerRadius = HierarchicalSize.Radius.Large,  // 12dp
            textStyle = { typography.labelLarge }
        )
        SizeVariant.Massive -> ChipSizeConfig(
            height = HierarchicalSize.Chip.Massive,  // 48dp
            horizontalPadding = HierarchicalSize.Padding.Huge,  // 20dp
            iconSize = HierarchicalSize.Icon.Huge,  // 32dp
            iconSpacing = HierarchicalSize.Spacing.Medium,  // 12dp
            cornerRadius = HierarchicalSize.Radius.Huge,  // 16dp
            textStyle = { typography.actionMedium }
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
 * PixaChip - Simplified chip component with sensible defaults
 *
 * A compact element that represents an attribute, text, entity, or action.
 * All parameters are optional with sensible defaults for ease of use.
 *
 * @param text The text content of the chip (optional - can be null for icon-only chips)
 * @param modifier Modifier for customization
 * @param variant Visual style variant (default: Tonal)
 * @param size Size variant (default: Medium)
 * @param type Behavior type (default: Static)
 * @param selected Whether the chip is selected (for Selectable type)
 * @param enabled Whether the chip is enabled
 * @param onClick Click handler (optional)
 * @param onDismiss Dismiss handler (for Dismissible type)
 * @param leadingIcon Optional icon before text
 * @param trailingIcon Optional icon after text (not used with Dismissible type)
 * @param backgroundColor Optional independent background color that overrides theme
 * @param contentColor Optional independent content/text color that overrides theme
 * @param borderColor Optional independent border color that overrides theme
 * @param customColors Optional custom state colors for different states (overrides individual color params)
 * @param contentDescription Optional accessibility description (defaults to text if provided)
 *
 * @sample
 * ```
 * // Basic chip with defaults
 * PixaChip(text = "Featured")
 *
 * // Chip with custom background color
 * PixaChip(
 *     text = "Custom",
 *     backgroundColor = Color.Red.copy(alpha = 0.2f)
 * )
 *
 * // Selectable filter chip
 * var selected by remember { mutableStateOf(false) }
 * PixaChip(
 *     text = "Filter",
 *     type = ChipType.Selectable,
 *     selected = selected,
 *     onClick = { selected = !selected }
 * )
 * ```
 */
@Composable
fun PixaChip(
    text: String? = null,
    modifier: Modifier = Modifier,
    variant: ChipVariant = ChipVariant.Tonal,
    size: SizeVariant = SizeVariant.Medium,
    type: ChipType = ChipType.Static,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    borderColor: Color? = null,
    customColors: ChipStateColors? = null,
    contentDescription: String? = null
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

    // Build final colors - priority: customColors > individual colors > theme
    val finalColors = when {
        customColors != null -> when {
            !enabled -> customColors.disabled
            selected -> customColors.selected
            else -> customColors.default
        }
        backgroundColor != null || contentColor != null || borderColor != null -> {
            ChipColors(
                background = backgroundColor ?: stateColors.background,
                content = contentColor ?: stateColors.content,
                border = borderColor ?: stateColors.border,
                ripple = (contentColor ?: stateColors.content).copy(alpha = 0.12f)
            )
        }
        else -> stateColors
    }

    // Animated colors with spring for snappier feel
    val animatedBackgroundColor by animateColorAsState(
        targetValue = finalColors.background,
        animationSpec = spring(),
        label = "chipBackgroundColor"
    )

    val animatedContentColor by animateColorAsState(
        targetValue = finalColors.content,
        animationSpec = spring(),
        label = "chipContentColor"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = finalColors.border,
        animationSpec = spring(),
        label = "chipBorderColor"
    )

    val isClickable = enabled && onClick != null && type != ChipType.Static

    Box(
        modifier = modifier
            .sizeIn(minHeight = if (isClickable) HierarchicalSize.TouchTarget.Small else config.height)
            .height(config.height)
            .clip(RoundedCornerShape(config.cornerRadius))
            .then(
                if (animatedBorderColor != Color.Transparent) {
                    Modifier.border(
                        width = HierarchicalSize.Border.Compact,
                        color = animatedBorderColor,
                        shape = RoundedCornerShape(config.cornerRadius)
                    )
                } else Modifier
            )
            .background(animatedBackgroundColor)
            .semantics {
                if (type == ChipType.Selectable) {
                    role = Role.Checkbox
                    this.selected = selected
                }
                contentDescription?.let {
                    this.contentDescription = it
                } ?: text?.let {
                    this.contentDescription = it
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
        CompositionLocalProvider(LocalContentColor provides animatedContentColor) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading icon
                if (leadingIcon != null) {
                    PixaIcon(
                        painter = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(config.iconSize),
                        tint = animatedContentColor
                    )
                    Spacer(modifier = Modifier.width(config.iconSpacing))
                }

                // Text (only if provided)
                if (!text.isNullOrBlank()) {
                    Text(
                        text = text,
                        style = config.textStyle(),
                        color = animatedContentColor,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.widthIn(max = HierarchicalSize.Image.Medium + HierarchicalSize.Spacing.Massive)
                    )
                }

                // Trailing icon
                if (trailingIcon != null && type != ChipType.Dismissible) {
                    Spacer(modifier = Modifier.width(config.iconSpacing))
                    PixaIcon(
                        painter = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(config.iconSize),
                        tint = animatedContentColor
                    )
                }

                // Dismiss icon for dismissible chips
                if (type == ChipType.Dismissible && onDismiss != null && enabled) {
                    Spacer(modifier = Modifier.width(config.iconSpacing))
                    Box(
                        modifier = Modifier
                            .size(config.iconSize)
                            .clip(RoundedCornerShape(HierarchicalSize.Radius.Full))
                            .clickable(
                                onClick = onDismiss,
                                indication = ripple(bounded = true, color = animatedContentColor.copy(alpha = 0.2f)),
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "×",
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = config.iconSize.value.sp,
                                color = animatedContentColor
                            )
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// CONVENIENCE WRAPPER (Backward Compatibility)
// ============================================================================

/**
 * Chip - Backward compatible wrapper for PixaChip
 * @see PixaChip for full documentation
 */
@Composable
fun Chip(
    text: String? = null,
    modifier: Modifier = Modifier,
    variant: ChipVariant = ChipVariant.Tonal,
    size: SizeVariant = SizeVariant.Medium,
    type: ChipType = ChipType.Static,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    borderColor: Color? = null,
    customColors: ChipStateColors? = null,
    contentDescription: String? = null
) {
    PixaChip(
        text = text,
        modifier = modifier,
        variant = variant,
        size = size,
        type = type,
        selected = selected,
        enabled = enabled,
        onClick = onClick,
        onDismiss = onDismiss,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        borderColor = borderColor,
        customColors = customColors,
        contentDescription = contentDescription
    )
}
