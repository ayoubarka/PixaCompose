package com.pixamob.pixacompose.components.actions
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Shape
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
import com.pixamob.pixacompose.components.feedback.PixaBadge
import com.pixamob.pixacompose.components.feedback.BadgeSize
import com.pixamob.pixacompose.components.feedback.BadgeStyle
import com.pixamob.pixacompose.components.feedback.BadgeVariant
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette

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
 * Tab indicator shape options
 */
enum class TabIndicatorShape {
    /** Rectangle (default) - sharp corners */
    Rectangle,
    /** Rounded rectangle - slight corner rounding */
    RoundedRectangle,
    /** Oval/Pill - fully rounded ends */
    Oval,
    /** Top rounded - only top corners rounded (for bottom indicators) */
    TopRounded,
    /** Bottom rounded - only bottom corners rounded (for top indicators) */
    BottomRounded
}

/**
 * Tab indicator configuration for customizing indicator appearance
 *
 * @param shape The shape of the indicator
 * @param height Height of the indicator (for underline/top-bottom styles)
 * @param width Width of the indicator (null = match tab width)
 * @param cornerRadius Corner radius for RoundedRectangle shape
 * @param color Optional custom indicator color (null = use theme default)
 * @param animationDurationMs Duration of indicator animation in milliseconds
 * @param horizontalPadding Horizontal padding to inset the indicator from tab edges
 */
@Immutable
@Stable
data class TabIndicatorConfig(
    val shape: TabIndicatorShape = TabIndicatorShape.Rectangle,
    val height: Dp? = null,
    val width: Dp? = null,
    val cornerRadius: Dp = 0.dp,
    val color: Color? = null,
    val animationDurationMs: Int = 300,
    val horizontalPadding: Dp = 0.dp
)

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
private fun getTabConfig(size: SizeVariant): TabConfig {
    val typography = AppTheme.typography

    return when (size) {
        SizeVariant.None -> TabConfig(
            height = 0.dp,
            minWidth = 0.dp,
            horizontalPadding = 0.dp,
            verticalPadding = 0.dp,
            iconSize = 0.dp,
            cornerRadius = 0.dp,
            textStyle = typography.actionMini,
            indicatorHeight = 0.dp,
            borderWidth = 0.dp
        )
        SizeVariant.Nano -> TabConfig(
            height = HierarchicalSize.Tab.Nano,  // 32dp
            minWidth = HierarchicalSize.Container.Medium,  // 48dp
            horizontalPadding = HierarchicalSize.Spacing.Small,  // 8dp
            verticalPadding = HierarchicalSize.Spacing.Nano,  // 2dp
            iconSize = HierarchicalSize.Icon.Nano,  // 12dp
            cornerRadius = HierarchicalSize.Radius.Nano,  // 2dp
            textStyle = typography.actionMini,
            indicatorHeight = HierarchicalSize.Border.Medium,  // 2dp
            borderWidth = HierarchicalSize.Border.Compact  // 1dp
        )
        SizeVariant.Compact -> TabConfig(
            height = HierarchicalSize.Tab.Compact,  // 36dp
            minWidth = HierarchicalSize.Container.Huge,  // 64dp
            horizontalPadding = HierarchicalSize.Spacing.Medium,  // 12dp
            verticalPadding = HierarchicalSize.Spacing.Compact,  // 4dp
            iconSize = HierarchicalSize.Icon.Compact,  // 16dp
            cornerRadius = HierarchicalSize.Radius.Nano,  // 2dp
            textStyle = typography.actionSmall,
            indicatorHeight = HierarchicalSize.Border.Medium,  // 2dp
            borderWidth = HierarchicalSize.Border.Compact  // 1dp
        )
        SizeVariant.Small -> TabConfig(
            height = HierarchicalSize.Tab.Small,  // 40dp
            minWidth = HierarchicalSize.Container.Massive,  // 80dp
            horizontalPadding = HierarchicalSize.Spacing.Medium,  // 12dp
            verticalPadding = HierarchicalSize.Spacing.Small,  // 8dp
            iconSize = HierarchicalSize.Icon.Small,  // 20dp
            cornerRadius = HierarchicalSize.Radius.Small,  // 6dp
            textStyle = typography.actionSmall,
            indicatorHeight = 2.dp,
            borderWidth = 1.dp
        )
        SizeVariant.Medium -> TabConfig(
            height = HierarchicalSize.Tab.Medium,  // 48dp
            minWidth = 120.dp,
            horizontalPadding = HierarchicalSize.Spacing.Large,  // 16dp
            verticalPadding = HierarchicalSize.Spacing.Medium,  // 12dp
            iconSize = HierarchicalSize.Icon.Medium,  // 24dp
            cornerRadius = HierarchicalSize.Radius.Medium,  // 8dp
            textStyle = typography.actionMedium,
            indicatorHeight = 3.dp,
            borderWidth = 1.5.dp
        )
        SizeVariant.Large -> TabConfig(
            height = HierarchicalSize.Tab.Large,  // 56dp
            minWidth = 140.dp,
            horizontalPadding = HierarchicalSize.Spacing.Large,  // 16dp
            verticalPadding = HierarchicalSize.Spacing.Large,  // 16dp
            iconSize = HierarchicalSize.Icon.Large,  // 28dp
            cornerRadius = HierarchicalSize.Radius.Large,  // 12dp
            textStyle = typography.actionLarge,
            indicatorHeight = 4.dp,
            borderWidth = 2.dp
        )
        SizeVariant.Huge -> TabConfig(
            height = HierarchicalSize.Tab.Huge,  // 64dp
            minWidth = 160.dp,
            horizontalPadding = HierarchicalSize.Spacing.Huge,  // 24dp
            verticalPadding = HierarchicalSize.Spacing.Large,  // 16dp
            iconSize = HierarchicalSize.Icon.Huge,  // 32dp
            cornerRadius = HierarchicalSize.Radius.Huge,  // 16dp
            textStyle = typography.actionLarge,
            indicatorHeight = 4.dp,
            borderWidth = 2.dp
        )
        SizeVariant.Massive -> TabConfig(
            height = HierarchicalSize.Tab.Massive,  // 72dp
            minWidth = 200.dp,
            horizontalPadding = HierarchicalSize.Spacing.Massive,  // 48dp
            verticalPadding = HierarchicalSize.Spacing.Huge,  // 24dp
            iconSize = HierarchicalSize.Icon.Massive,  // 48dp
            cornerRadius = HierarchicalSize.Radius.Massive,  // 24dp
            textStyle = typography.actionHuge,
            indicatorHeight = 6.dp,
            borderWidth = 3.dp
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
// HELPER FUNCTIONS
// ============================================================================

/**
 * Get the shape for the tab indicator based on configuration
 */
private fun getIndicatorShape(
    indicatorShape: TabIndicatorShape,
    height: Dp,
    cornerRadius: Dp
): Shape {
    return when (indicatorShape) {
        TabIndicatorShape.Rectangle -> RoundedCornerShape(0.dp)
        TabIndicatorShape.RoundedRectangle -> RoundedCornerShape(cornerRadius)
        TabIndicatorShape.Oval -> RoundedCornerShape(height / 2)
        TabIndicatorShape.TopRounded -> RoundedCornerShape(
            topStart = cornerRadius,
            topEnd = cornerRadius,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
        TabIndicatorShape.BottomRounded -> RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = cornerRadius,
            bottomEnd = cornerRadius
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
fun PixaTab(
    selected: Boolean,
    onClick: () -> Unit,
    content: TabContent,
    style: TabStyle,
    config: TabConfig,
    size: SizeVariant = SizeVariant.Medium,
    shape: TabShape = TabShape.Default,
    orientation: TabOrientation = TabOrientation.Horizontal,
    contentAlignment: Alignment = Alignment.Center,
    customContentStyle: TabContentStyle? = null,
    badge: String? = null,
    badgeVariant: BadgeVariant = BadgeVariant.Error,
    enabled: Boolean = true,
    indicatorStyle: TabIndicatorStyle = TabIndicatorStyle.Underline,
    indicatorConfig: TabIndicatorConfig = TabIndicatorConfig(),
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

    // Use custom indicator color if provided, otherwise use style default
    val indicatorColor by animateColorAsState(
        targetValue = if (selected) {
            indicatorConfig.color ?: style.selected.indicator
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = indicatorConfig.animationDurationMs),
        label = "tab_indicator"
    )

    // Shape configuration
    val cornerRadius = when (shape) {
        TabShape.Default -> config.cornerRadius
        TabShape.Pill -> config.height / 2
    }

    // Determine badge size based on tab size
    val badgeSize = when (size) {
        SizeVariant.None -> BadgeSize.Small
        SizeVariant.Nano, SizeVariant.Compact -> BadgeSize.Small
        SizeVariant.Small, SizeVariant.Medium -> BadgeSize.Medium
        SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> BadgeSize.Large
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
                    val indicatorHeight = indicatorConfig.height ?: config.indicatorHeight
                    val indicatorShape = getIndicatorShape(indicatorConfig.shape, indicatorHeight, indicatorConfig.cornerRadius)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = indicatorConfig.horizontalPadding)
                            .height(indicatorHeight)
                            .then(
                                if (indicatorConfig.width != null) {
                                    Modifier.width(indicatorConfig.width)
                                } else {
                                    Modifier.fillMaxWidth()
                                }
                            )
                            .clip(indicatorShape)
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
                // Left indicator with configurable shape
                if (showIndicator) {
                    val indicatorWidth = indicatorConfig.width ?: config.indicatorHeight
                    val indicatorShape = getIndicatorShape(indicatorConfig.shape, indicatorWidth, indicatorConfig.cornerRadius)

                    Box(
                        modifier = Modifier
                            .width(indicatorWidth)
                            .height(indicatorConfig.height ?: config.height)
                            .clip(indicatorShape)
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
            PixaIcon(
                imageVector = content.icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
        is TabContent.IconPainter -> {
            PixaIcon(
                painter = content.painter,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
        is TabContent.TextWithIcon -> {
            PixaIcon(
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
            PixaIcon(
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
        PixaBadge(
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
                PixaIcon(
                    imageVector = content.icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
                // Badge positioned at top-right
                badge?.let {
                    PixaBadge(
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
                PixaIcon(
                    painter = content.painter,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
                // Badge positioned at top-right
                badge?.let {
                    PixaBadge(
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
                PixaIcon(
                    imageVector = content.icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
                // Badge positioned at top-right
                badge?.let {
                    PixaBadge(
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
                PixaIcon(
                    painter = content.painter,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
                // Badge positioned at top-right
                badge?.let {
                    PixaBadge(
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
    size: SizeVariant = SizeVariant.Medium,
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
            PixaTab(
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
    size: SizeVariant = SizeVariant.Medium,
    orientation: TabOrientation = TabOrientation.Horizontal,
    customContentStyle: TabContentStyle? = null,
    customColors: TabColors? = null
) {
    val config = getTabConfig(size)
    val style = getTabStyle(TabVariant.Segmented, customColors, AppTheme.colors)

    Row(
        modifier = modifier
            .background(AppTheme.colors.baseSurfaceSubtle, RoundedCornerShape(config.cornerRadius))
            .padding(HierarchicalSize.Spacing.Nano)
    ) {
        tabs.forEachIndexed { index, tab ->
            PixaTab(
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
    size: SizeVariant = SizeVariant.Medium,
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
                .padding(horizontal = HierarchicalSize.Spacing.Medium, vertical = HierarchicalSize.Spacing.Small),
            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
        ) {
            tabs.forEachIndexed { index, tab ->
                PixaTab(
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
    size: SizeVariant = SizeVariant.Medium,
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
            PixaTab(
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
