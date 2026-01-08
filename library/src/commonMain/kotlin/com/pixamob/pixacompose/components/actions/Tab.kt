package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.feedback.Badge
import com.pixamob.pixacompose.components.feedback.BadgeSize
import com.pixamob.pixacompose.components.feedback.BadgeStyle
import com.pixamob.pixacompose.components.feedback.BadgeVariant
import com.pixamob.pixacompose.components.display.Icon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.ComponentSize
import com.pixamob.pixacompose.theme.IconSize
import com.pixamob.pixacompose.theme.RadiusSize
import com.pixamob.pixacompose.theme.Spacing

// ============================================================================
// CONFIGURATION & MODELS
// ============================================================================

/**
 * Tab variant types for different use cases
 */
enum class TabVariant {
    /** Primary tabs - Default style with underline indicator (Main navigation, content switching) */
    Primary,
    /** Secondary tabs - Subtle background with border (Sub-navigation, secondary options) */
    Secondary,
    /** Segmented tabs - Connected pill-shaped tabs (Toggle between 2-3 options) */
    Segmented,
    /** Vertical tabs - Stacked side navigation (Settings sections, multi-step forms) */
    Vertical
}

/**
 * Tab size variants (capped at Large for mobile-first design)
 */
enum class TabSize {
    Mini,       // 28dp height - Very compact
    Compact,    // 32dp height - Compact
    Small,      // 40dp height - Small
    Medium,     // 48dp height - Default
    Large       // 56dp height - Large
}

/**
 * Tab shape variants (simplified for mobile-first)
 */
enum class TabShape {
    Default,    // Rounded corners
    Pill        // Fully rounded
}

/**
 * Tab orientation for icon and text arrangement
 */
enum class TabOrientation {
    Horizontal, // Icon and text side by side
    Vertical    // Icon above text
}

/**
 * Tab indicator style - defines how the selected state is shown
 */
enum class TabIndicatorStyle {
    /** Bottom underline (default for Primary tabs) */
    Underline,
    /** Pill background (for Segmented tabs) */
    Pill,
    /** Vertical left border (for Vertical tabs) */
    LeftBorder,
    /** No indicator */
    None
}

/**
 * Tab content - can be text, icon, or both
 */
sealed class TabContent {
    data class Text(val text: String) : TabContent()
    data class Icon(val icon: ImageVector) : TabContent()
    data class IconPainter(val painter: Painter) : TabContent()
    data class TextWithIcon(val text: String, val icon: ImageVector) : TabContent()
    data class TextWithPainter(val text: String, val painter: Painter) : TabContent()
}

/**
 * Tab configuration for dimensions and styling
 */
@Immutable
@Stable
data class TabConfig(
    val height: Dp,
    val minWidth: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val iconSize: Dp,
    val cornerRadius: Dp,
    val textStyle: TextStyle,
    val indicatorHeight: Dp = 2.dp,
    val borderWidth: Dp = 1.dp
)

/**
 * Tab colors for different states
 */
@Immutable
@Stable
data class TabColors(
    val background: Color,
    val content: Color,
    val border: Color = Color.Transparent,
    val indicator: Color = Color.Transparent,
    val ripple: Color = content.copy(alpha = 0.12f)
)

/**
 * Tab style configuration
 */
@Immutable
@Stable
data class TabStyle(
    val default: TabColors,
    val selected: TabColors,
    val hovered: TabColors,
    val disabled: TabColors,
    val enableRipple: Boolean = true,
    val rippleColor: Color = Color.Unspecified
)

/**
 * Tab item data class
 * Represents a single tab with its content and state
 */
@Immutable
@Stable
data class TabItem(
    val content: TabContent,
    val badge: String? = null,
    val enabled: Boolean = true,
    val contentDescription: String? = null
)

/**
 * Custom style configuration for tab content
 */
@Immutable
@Stable
data class TabContentStyle(
    val iconSize: Dp? = null,
    val iconTint: Color? = null,
    val textStyle: TextStyle? = null,
    val textColor: Color? = null
)

// ============================================================================
// SIZE CONFIGURATIONS
// ============================================================================

/**
 * Get tab configuration based on size
 */
@Composable
private fun getTabConfig(size: TabSize): TabConfig {
    val typography = AppTheme.typography

    return when (size) {
        TabSize.Mini -> TabConfig(
            height = ComponentSize.Minimal,
            minWidth = 48.dp,
            horizontalPadding = Spacing.Small,
            verticalPadding = Spacing.Micro,
            iconSize = IconSize.Tiny,
            cornerRadius = RadiusSize.Tiny,
            textStyle = typography.actionMini,
            indicatorHeight = 2.dp,
            borderWidth = 1.dp
        )
        TabSize.Compact -> TabConfig(
            height = ComponentSize.VerySmall,
            minWidth = 56.dp,
            horizontalPadding = Spacing.Medium,
            verticalPadding = Spacing.Tiny,
            iconSize = IconSize.VerySmall,
            cornerRadius = RadiusSize.Tiny,
            textStyle = typography.actionExtraSmall,
            indicatorHeight = 2.dp,
            borderWidth = 1.dp
        )
        TabSize.Small -> TabConfig(
            height = ComponentSize.ExtraSmall,
            minWidth = 64.dp,
            horizontalPadding = Spacing.Medium,
            verticalPadding = Spacing.Small,
            iconSize = IconSize.ExtraSmall,
            cornerRadius = RadiusSize.Small,
            textStyle = typography.actionSmall,
            indicatorHeight = 2.dp,
            borderWidth = 1.dp
        )
        TabSize.Medium -> TabConfig(
            height = ComponentSize.Medium,
            minWidth = 80.dp,
            horizontalPadding = Spacing.Medium,
            verticalPadding = Spacing.Medium,
            iconSize = IconSize.Medium,
            cornerRadius = RadiusSize.Medium,
            textStyle = typography.actionMedium,
            indicatorHeight = 3.dp,
            borderWidth = 1.dp
        )
        TabSize.Large -> TabConfig(
            height = ComponentSize.Large,
            minWidth = 96.dp,
            horizontalPadding = Spacing.Large,
            verticalPadding = Spacing.Medium,
            iconSize = IconSize.Large,
            cornerRadius = RadiusSize.Large,
            textStyle = typography.actionLarge,
            indicatorHeight = 3.dp,
            borderWidth = 1.dp
        )
    }
}

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get tab style based on variant
 */
@Composable
private fun getTabStyle(
    variant: TabVariant,
    customColors: TabColors?,
    colors: ColorPalette
): TabStyle {
    // If custom colors provided, use them
    if (customColors != null) {
        return TabStyle(
            default = TabColors(
                background = customColors.background,
                content = customColors.content,
                border = customColors.border
            ),
            selected = TabColors(
                background = customColors.background,
                content = customColors.content,
                border = customColors.border,
                indicator = customColors.indicator
            ),
            hovered = TabColors(
                background = customColors.background,
                content = customColors.content,
                border = customColors.border
            ),
            disabled = TabColors(
                background = Color.Transparent,
                content = colors.baseContentDisabled
            ),
            enableRipple = true
        )
    }

    return when (variant) {
        TabVariant.Primary -> TabStyle(
            default = TabColors(
                background = Color.Transparent,
                content = colors.baseContentCaption
            ),
            selected = TabColors(
                background = Color.Transparent,
                content = colors.brandContentDefault,
                indicator = colors.brandContentDefault
            ),
            hovered = TabColors(
                background = colors.baseSurfaceFocus,
                content = colors.baseContentSubtitle
            ),
            disabled = TabColors(
                background = Color.Transparent,
                content = colors.baseContentDisabled
            ),
            enableRipple = true
        )
        TabVariant.Secondary -> TabStyle(
            default = TabColors(
                background = Color.Transparent,
                content = colors.baseContentCaption,
                border = colors.baseBorderDefault
            ),
            selected = TabColors(
                background = colors.brandSurfaceSubtle,
                content = colors.brandContentDefault,
                border = colors.brandBorderDefault
            ),
            hovered = TabColors(
                background = colors.baseSurfaceFocus,
                content = colors.baseContentSubtitle,
                border = colors.baseBorderDefault
            ),
            disabled = TabColors(
                background = Color.Transparent,
                content = colors.baseContentDisabled,
                border = colors.baseBorderDisabled
            ),
            enableRipple = true
        )
        TabVariant.Segmented -> TabStyle(
            default = TabColors(
                background = Color.Transparent,
                content = colors.baseContentCaption
            ),
            selected = TabColors(
                background = colors.baseSurfaceDefault,
                content = colors.baseContentSubtitle
            ),
            hovered = TabColors(
                background = colors.baseSurfaceFocus,
                content = colors.baseContentSubtitle
            ),
            disabled = TabColors(
                background = Color.Transparent,
                content = colors.baseContentDisabled
            ),
            enableRipple = true
        )
        TabVariant.Vertical -> TabStyle(
            default = TabColors(
                background = Color.Transparent,
                content = colors.baseContentCaption
            ),
            selected = TabColors(
                background = colors.brandSurfaceSubtle,
                content = colors.brandContentDefault,
                indicator = colors.brandContentDefault
            ),
            hovered = TabColors(
                background = colors.baseSurfaceFocus,
                content = colors.baseContentSubtitle
            ),
            disabled = TabColors(
                background = Color.Transparent,
                content = colors.baseContentDisabled
            ),
            enableRipple = true
        )
    }
}

// ============================================================================
// BASE TAB (Internal - handles core logic)
// ============================================================================

/**
 * BaseTab - Core tab component
 *
 * Built from primitives: Box, Row, Column, Text, Icon
 * Handles: Click interaction, State animation, Content rendering
 */
@Composable
private fun BaseTab(
    selected: Boolean,
    onClick: () -> Unit,
    content: TabContent,
    style: TabStyle,
    config: TabConfig,
    size: TabSize = TabSize.Medium,
    shape: TabShape = TabShape.Default,
    orientation: TabOrientation = TabOrientation.Horizontal,
    contentAlignment: Alignment = Alignment.Center,
    customContentStyle: TabContentStyle? = null,
    badge: String? = null,
    badgeVariant: BadgeVariant = BadgeVariant.Error,
    enabled: Boolean = true,
    indicatorStyle: TabIndicatorStyle = TabIndicatorStyle.Underline,
    modifier: Modifier = Modifier
) {
    // Animated colors with spring for snappier feel
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> style.disabled.background
            selected -> style.selected.background
            else -> style.default.background
        },
        animationSpec = spring(),
        label = "tab_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            !enabled -> style.disabled.content
            selected -> style.selected.content
            else -> style.default.content
        },
        animationSpec = spring(),
        label = "tab_content"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> style.disabled.border
            selected -> style.selected.border
            else -> style.default.border
        },
        animationSpec = spring(),
        label = "tab_border"
    )

    val indicatorColor by animateColorAsState(
        targetValue = if (selected) style.selected.indicator else Color.Transparent,
        animationSpec = spring(),
        label = "tab_indicator"
    )

    // Shape configuration
    val cornerRadius = when (shape) {
        TabShape.Default -> config.cornerRadius
        TabShape.Pill -> config.height / 2
    }

    // Determine badge size based on tab size
    val badgeSize = when (size) {
        TabSize.Mini, TabSize.Compact -> BadgeSize.Small
        TabSize.Small, TabSize.Medium -> BadgeSize.Medium
        TabSize.Large -> BadgeSize.Large
    }

    // Determine if indicator should show
    val showIndicator = indicatorStyle != TabIndicatorStyle.None && selected

    // Layout based on indicator style
    when (indicatorStyle) {
        TabIndicatorStyle.Underline, TabIndicatorStyle.None -> {
            Column(
                modifier = modifier
                    .semantics {
                        role = Role.Tab
                        this.selected = selected
                        when (content) {
                            is TabContent.Text -> this.contentDescription = content.text
                            is TabContent.Icon -> this.contentDescription = "Tab"
                            is TabContent.IconPainter -> this.contentDescription = "Tab"
                            is TabContent.TextWithIcon -> this.contentDescription = content.text
                            is TabContent.TextWithPainter -> this.contentDescription = content.text
                        }
                    }
            ) {
                // Tab content box
                Box(
                    modifier = Modifier
                        .height(config.height)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(backgroundColor)
                        .then(
                            if (borderColor != Color.Transparent) {
                                Modifier.border(
                                    width = config.borderWidth,
                                    color = borderColor,
                                    shape = RoundedCornerShape(cornerRadius)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable(
                            enabled = enabled,
                            onClick = onClick,
                            indication = if (style.enableRipple) {
                                val ripple = if (style.rippleColor != Color.Unspecified) {
                                    style.rippleColor
                                } else {
                                    contentColor.copy(alpha = 0.12f)
                                }
                                ripple(bounded = true, color = ripple)
                            } else null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .padding(
                            horizontal = config.horizontalPadding,
                            vertical = config.verticalPadding
                        ),
                    contentAlignment = contentAlignment
                ) {
                    // Render content based on orientation
                    if (orientation == TabOrientation.Vertical) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            RenderTabContentColumn(
                                content = content,
                                contentColor = contentColor,
                                selected = selected,
                                badge = badge,
                                badgeSize = badgeSize,
                                badgeVariant = badgeVariant,
                                config = config,
                                customContentStyle = customContentStyle
                            )
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RenderTabContentRow(
                                content = content,
                                contentColor = contentColor,
                                selected = selected,
                                badge = badge,
                                badgeSize = badgeSize,
                                badgeVariant = badgeVariant,
                                config = config,
                                customContentStyle = customContentStyle
                            )
                        }
                    }
                }

                // Bottom indicator (Underline)
                if (showIndicator && indicatorStyle == TabIndicatorStyle.Underline) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(config.indicatorHeight)
                            .background(indicatorColor)
                    )
                }
            }
        }
        TabIndicatorStyle.Pill -> {
            // Pill style - indicator is the background itself
            Box(
                modifier = modifier
                    .semantics {
                        role = Role.Tab
                        this.selected = selected
                        when (content) {
                            is TabContent.Text -> this.contentDescription = content.text
                            is TabContent.Icon -> this.contentDescription = "Tab"
                            is TabContent.IconPainter -> this.contentDescription = "Tab"
                            is TabContent.TextWithIcon -> this.contentDescription = content.text
                            is TabContent.TextWithPainter -> this.contentDescription = content.text
                        }
                    }
                    .height(config.height)
                    .clip(RoundedCornerShape(config.height / 2))
                    .background(backgroundColor)
                    .clickable(
                        enabled = enabled,
                        onClick = onClick,
                        indication = if (style.enableRipple) {
                            val ripple = if (style.rippleColor != Color.Unspecified) {
                                style.rippleColor
                            } else {
                                contentColor.copy(alpha = 0.12f)
                            }
                            ripple(bounded = true, color = ripple)
                        } else null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .padding(
                        horizontal = config.horizontalPadding,
                        vertical = config.verticalPadding
                    ),
                contentAlignment = contentAlignment
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RenderTabContentRow(
                        content = content,
                        contentColor = contentColor,
                        selected = selected,
                        badge = badge,
                        badgeSize = badgeSize,
                        badgeVariant = badgeVariant,
                        config = config,
                        customContentStyle = customContentStyle
                    )
                }
            }
        }
        TabIndicatorStyle.LeftBorder -> {
            // Left border indicator
            Row(
                modifier = modifier
                    .semantics {
                        role = Role.Tab
                        this.selected = selected
                        when (content) {
                            is TabContent.Text -> this.contentDescription = content.text
                            is TabContent.Icon -> this.contentDescription = "Tab"
                            is TabContent.IconPainter -> this.contentDescription = "Tab"
                            is TabContent.TextWithIcon -> this.contentDescription = content.text
                            is TabContent.TextWithPainter -> this.contentDescription = content.text
                        }
                    }
            ) {
                // Left indicator
                if (showIndicator) {
                    Box(
                        modifier = Modifier
                            .width(config.indicatorHeight)
                            .height(config.height)
                            .background(indicatorColor)
                    )
                }

                // Tab content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(config.height)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(backgroundColor)
                        .then(
                            if (borderColor != Color.Transparent) {
                                Modifier.border(
                                    width = config.borderWidth,
                                    color = borderColor,
                                    shape = RoundedCornerShape(cornerRadius)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable(
                            enabled = enabled,
                            onClick = onClick,
                            indication = if (style.enableRipple) {
                                val ripple = if (style.rippleColor != Color.Unspecified) {
                                    style.rippleColor
                                } else {
                                    contentColor.copy(alpha = 0.12f)
                                }
                                ripple(bounded = true, color = ripple)
                            } else null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .padding(
                            horizontal = config.horizontalPadding,
                            vertical = config.verticalPadding
                        ),
                    contentAlignment = contentAlignment
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RenderTabContentRow(
                            content = content,
                            contentColor = contentColor,
                            selected = selected,
                            badge = badge,
                            badgeSize = badgeSize,
                            badgeVariant = badgeVariant,
                            config = config,
                            customContentStyle = customContentStyle
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper function to render tab content in Row scope
 */
@Composable
private fun RowScope.RenderTabContentRow(
    content: TabContent,
    contentColor: Color,
    selected: Boolean,
    badge: String?,
    badgeSize: BadgeSize,
    badgeVariant: BadgeVariant,
    config: TabConfig,
    customContentStyle: TabContentStyle?
) {
    val iconSize = customContentStyle?.iconSize ?: config.iconSize
    val iconTint = customContentStyle?.iconTint ?: contentColor
    val textStyle = customContentStyle?.textStyle ?: config.textStyle
    val textColor = customContentStyle?.textColor ?: contentColor

    when (content) {
        is TabContent.Text -> {
            Text(
                text = content.text,
                style = textStyle,
                color = textColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        is TabContent.Icon -> {
            Icon(
                imageVector = content.icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
        is TabContent.IconPainter -> {
            Icon(
                painter = content.painter,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
        is TabContent.TextWithIcon -> {
            Icon(
                imageVector = content.icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
            Text(
                text = content.text,
                style = textStyle,
                color = textColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        is TabContent.TextWithPainter -> {
            Icon(
                painter = content.painter,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
            Text(
                text = content.text,
                style = textStyle,
                color = textColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    // Badge using Badge component
    badge?.let {
        Badge(
            content = it,
            variant = badgeVariant,
            size = badgeSize,
            modifier = Modifier.offset(x = (-4).dp, y = (-4).dp)
        )
    }
}

/**
 * Helper function to render tab content in Column scope
 */
@Composable
private fun ColumnScope.RenderTabContentColumn(
    content: TabContent,
    contentColor: Color,
    selected: Boolean,
    badge: String?,
    badgeSize: BadgeSize,
    badgeVariant: BadgeVariant,
    config: TabConfig,
    customContentStyle: TabContentStyle?
) {
    val iconSize = customContentStyle?.iconSize ?: config.iconSize
    val iconTint = customContentStyle?.iconTint ?: contentColor
    val textStyle = customContentStyle?.textStyle ?: config.textStyle
    val textColor = customContentStyle?.textColor ?: contentColor

    when (content) {
        is TabContent.Text -> {
            Text(
                text = content.text,
                style = textStyle,
                color = textColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        is TabContent.Icon -> {
            Box {
                Icon(
                    imageVector = content.icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
                // Badge positioned at top-right
                badge?.let {
                    Badge(
                        content = it,
                        variant = badgeVariant,
                        size = badgeSize,
                        style = BadgeStyle.Solid,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                    )
                }
            }
        }
        is TabContent.IconPainter -> {
            Box {
                Icon(
                    painter = content.painter,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
                // Badge positioned at top-right
                badge?.let {
                    Badge(
                        content = it,
                        variant = badgeVariant,
                        size = badgeSize,
                        style = BadgeStyle.Solid,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                    )
                }
            }
        }
        is TabContent.TextWithIcon -> {
            Box {
                Icon(
                    imageVector = content.icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
                // Badge positioned at top-right
                badge?.let {
                    Badge(
                        content = it,
                        variant = badgeVariant,
                        size = badgeSize,
                        style = BadgeStyle.Solid,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                    )
                }
            }
            Text(
                text = content.text,
                style = textStyle,
                color = textColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        is TabContent.TextWithPainter -> {
            Box {
                Icon(
                    painter = content.painter,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
                // Badge positioned at top-right
                badge?.let {
                    Badge(
                        content = it,
                        variant = badgeVariant,
                        size = badgeSize,
                        style = BadgeStyle.Solid,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                    )
                }
            }
            Text(
                text = content.text,
                style = textStyle,
                color = textColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================================
// PUBLIC API - TAB VARIANTS
// ============================================================================

/**
 * Tabs - Primary tabs with underline indicator
 *
 * A flexible tab component for main navigation and content switching:
 * - Primary style with underline indicator
 * - Equal width tabs that fill container
 * - Support for icons, text, or both
 * - Badge support
 * - Multiple size variants
 * - Horizontal or vertical orientation
 * - Smooth animations
 * - Full theme integration
 *
 * Purpose: Main navigation, content switching
 *
 * @param selectedTabIndex Currently selected tab index
 * @param tabs List of tab items
 * @param onTabSelected Callback when tab is selected
 * @param modifier Modifier for styling
 * @param size Size variant of tabs (Default: Medium)
 * @param orientation Orientation of icon and text (Default: Horizontal)
 * @param indicatorStyle Style of the selection indicator (Default: Underline)
 * @param badgeVariant Badge color variant (Default: Error)
 * @param customContentStyle Custom styling for text and icons
 * @param customColors Custom color override
 *
 * @sample
 * ```
 * var selectedTab by remember { mutableStateOf(0) }
 * Tabs(
 *     selectedTabIndex = selectedTab,
 *     tabs = listOf(
 *         TabItem(TabContent.Text("Home")),
 *         TabItem(TabContent.Text("Profile"), badge = "5"),
 *         TabItem(TabContent.Text("Settings"))
 *     ),
 *     onTabSelected = { selectedTab = it }
 * )
 * ```
 */
@Composable
fun Tabs(
    selectedTabIndex: Int,
    tabs: List<TabItem>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    size: TabSize = TabSize.Medium,
    orientation: TabOrientation = TabOrientation.Horizontal,
    indicatorStyle: TabIndicatorStyle = TabIndicatorStyle.Underline,
    badgeVariant: BadgeVariant = BadgeVariant.Error,
    customContentStyle: TabContentStyle? = null,
    customColors: TabColors? = null
) {
    val config = getTabConfig(size)
    val style = getTabStyle(TabVariant.Primary, customColors, AppTheme.colors)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.colors.baseSurfaceDefault)
    ) {
        tabs.forEachIndexed { index, tab ->
            BaseTab(
                selected = index == selectedTabIndex,
                onClick = { if (tab.enabled) onTabSelected(index) },
                content = tab.content,
                style = style,
                config = config,
                size = size,
                shape = TabShape.Default,
                orientation = orientation,
                customContentStyle = customContentStyle,
                badge = tab.badge,
                badgeVariant = badgeVariant,
                enabled = tab.enabled,
                indicatorStyle = indicatorStyle,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * SegmentedTabs - Pill-shaped connected tabs
 *
 * Connected tabs with segmented appearance:
 * - Pill-shaped background container
 * - Selected tab has filled background
 * - Equal width tabs
 * - Compact layout
 *
 * Purpose: Toggle between 2-3 options
 *
 * @param selectedTabIndex Currently selected tab index
 * @param tabs List of tab items (2-4 recommended)
 * @param onTabSelected Callback when tab is selected
 * @param modifier Modifier for styling
 * @param size Size variant of tabs (Default: Medium)
 * @param orientation Orientation of icon and text (Default: Horizontal)
 * @param customContentStyle Custom styling for text and icons
 * @param customColors Custom color override
 *
 * @sample
 * ```
 * var selected by remember { mutableStateOf(0) }
 * SegmentedTabs(
 *     selectedTabIndex = selected,
 *     tabs = listOf(
 *         TabItem(TabContent.Text("Day")),
 *         TabItem(TabContent.Text("Week")),
 *         TabItem(TabContent.Text("Month"))
 *     ),
 *     onTabSelected = { selected = it }
 * )
 * ```
 */
@Composable
fun SegmentedTabs(
    selectedTabIndex: Int,
    tabs: List<TabItem>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    size: TabSize = TabSize.Medium,
    orientation: TabOrientation = TabOrientation.Horizontal,
    customContentStyle: TabContentStyle? = null,
    customColors: TabColors? = null
) {
    val config = getTabConfig(size)
    val style = getTabStyle(TabVariant.Segmented, customColors, AppTheme.colors)

    Row(
        modifier = modifier
            .background(AppTheme.colors.baseSurfaceSubtle, RoundedCornerShape(config.cornerRadius))
            .padding(Spacing.Micro)
    ) {
        tabs.forEachIndexed { index, tab ->
            BaseTab(
                selected = index == selectedTabIndex,
                onClick = { if (tab.enabled) onTabSelected(index) },
                content = tab.content,
                style = style,
                config = config,
                size = size,
                shape = TabShape.Pill,
                orientation = orientation,
                customContentStyle = customContentStyle,
                badge = tab.badge,
                enabled = tab.enabled,
                indicatorStyle = TabIndicatorStyle.Pill,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * ScrollableTabs - Horizontally scrollable tabs
 *
 * Scrollable tabs for many options:
 * - Horizontal scrolling
 * - Variable width tabs
 * - Pill shape with border on selected
 * - Compact spacing
 *
 * Purpose: Many tab options (Categories, filters)
 *
 * @param selectedTabIndex Currently selected tab index
 * @param tabs List of tab items
 * @param onTabSelected Callback when tab is selected
 * @param modifier Modifier for styling
 * @param size Size variant of tabs (Default: Medium)
 * @param orientation Orientation of icon and text (Default: Horizontal)
 * @param customContentStyle Custom styling for text and icons
 * @param customColors Custom color override
 *
 * @sample
 * ```
 * var selected by remember { mutableStateOf(0) }
 * ScrollableTabs(
 *     selectedTabIndex = selected,
 *     tabs = listOf(
 *         TabItem(TabContent.Text("All")),
 *         TabItem(TabContent.Text("Technology")),
 *         TabItem(TabContent.Text("Business")),
 *         TabItem(TabContent.Text("Entertainment")),
 *         TabItem(TabContent.Text("Sports"))
 *     ),
 *     onTabSelected = { selected = it }
 * )
 * ```
 */
@Composable
fun ScrollableTabs(
    selectedTabIndex: Int,
    tabs: List<TabItem>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    size: TabSize = TabSize.Medium,
    orientation: TabOrientation = TabOrientation.Horizontal,
    customContentStyle: TabContentStyle? = null,
    customColors: TabColors? = null
) {
    val config = getTabConfig(size)
    val style = getTabStyle(TabVariant.Secondary, customColors, AppTheme.colors)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.colors.baseSurfaceDefault)
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Spacing.Medium, vertical = Spacing.Small),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            tabs.forEachIndexed { index, tab ->
                BaseTab(
                    selected = index == selectedTabIndex,
                    onClick = { if (tab.enabled) onTabSelected(index) },
                    content = tab.content,
                    style = style,
                    config = config,
                    size = size,
                    shape = TabShape.Pill,
                    orientation = orientation,
                    customContentStyle = customContentStyle,
                    badge = tab.badge,
                    enabled = tab.enabled,
                    indicatorStyle = TabIndicatorStyle.Pill
                )
            }
        }
    }
}

/**
 * VerticalTabs - Side navigation tabs
 *
 * Vertical stacked tabs for side navigation:
 * - Full width within container
 * - Vertical stacking
 * - Selected state with background fill
 * - Suitable for settings, forms, wizards
 *
 * Purpose: Settings sections, multi-step forms, side navigation
 *
 * @param selectedTabIndex Currently selected tab index
 * @param tabs List of tab items
 * @param onTabSelected Callback when tab is selected
 * @param modifier Modifier for styling
 * @param size Size variant of tabs (Default: Medium)
 * @param orientation Orientation of icon and text (Default: Horizontal)
 * @param customContentStyle Custom styling for text and icons
 * @param customColors Custom color override
 *
 * @sample
 * ```
 * var selectedSection by remember { mutableStateOf(0) }
 * VerticalTabs(
 *     selectedTabIndex = selectedSection,
 *     tabs = listOf(
 *         TabItem(TabContent.TextWithIcon("Profile", Icons.Default.Person)),
 *         TabItem(TabContent.TextWithIcon("Security", Icons.Default.Lock)),
 *         TabItem(TabContent.TextWithIcon("Notifications", Icons.Default.Notifications))
 *     ),
 *     onTabSelected = { selectedSection = it }
 * )
 * ```
 */
@Composable
fun VerticalTabs(
    selectedTabIndex: Int,
    tabs: List<TabItem>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    size: TabSize = TabSize.Medium,
    orientation: TabOrientation = TabOrientation.Horizontal,
    customContentStyle: TabContentStyle? = null,
    customColors: TabColors? = null
) {
    val config = getTabConfig(size)
    val style = getTabStyle(TabVariant.Vertical, customColors, AppTheme.colors)

    Column(
        modifier = modifier
            .background(AppTheme.colors.baseSurfaceDefault)
    ) {
        tabs.forEachIndexed { index, tab ->
            BaseTab(
                selected = index == selectedTabIndex,
                onClick = { if (tab.enabled) onTabSelected(index) },
                content = tab.content,
                style = style,
                config = config,
                size = size,
                shape = TabShape.Default,
                orientation = orientation,
                customContentStyle = customContentStyle,
                badge = tab.badge,
                enabled = tab.enabled,
                indicatorStyle = TabIndicatorStyle.LeftBorder,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
