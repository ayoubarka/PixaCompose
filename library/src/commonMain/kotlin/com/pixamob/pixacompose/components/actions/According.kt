package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.IconSize
import com.pixamob.pixacompose.theme.RadiusSize

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class AccordionVariant {
    Default,
    Outlined,
    Filled
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Stable
data class AccordionItem(
    val title: String,
    val content: @Composable () -> Unit,
    val icon: Painter? = null,
    val enabled: Boolean = true
)

@Immutable
@Stable
data class AccordionColors(
    val background: Color,
    val headerBackground: Color,
    val contentBackground: Color,
    val title: Color,
    val icon: Color,
    val border: Color,
    val divider: Color
)

@Immutable
@Stable
data class AccordionSizeConfig(
    val headerPadding: Dp,
    val contentPadding: Dp,
    val iconSize: Dp,
    val titleStyle: TextStyle,
    val cornerRadius: Dp,
    val borderWidth: Dp,
    val spacing: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getAccordionTheme(variant: AccordionVariant): AccordionColors {
    val colors = AppTheme.colors
    return when (variant) {
        AccordionVariant.Default -> AccordionColors(
            background = Color.Transparent,
            headerBackground = Color.Transparent,
            contentBackground = Color.Transparent,
            title = colors.baseContentTitle,
            icon = colors.baseContentBody,
            border = Color.Transparent,
            divider = colors.baseBorderSubtle
        )
        AccordionVariant.Outlined -> AccordionColors(
            background = Color.Transparent,
            headerBackground = Color.Transparent,
            contentBackground = Color.Transparent,
            title = colors.baseContentTitle,
            icon = colors.baseContentBody,
            border = colors.baseBorderDefault,
            divider = colors.baseBorderSubtle
        )
        AccordionVariant.Filled -> AccordionColors(
            background = colors.baseSurfaceSubtle,
            headerBackground = colors.baseSurfaceDefault,
            contentBackground = colors.baseSurfaceSubtle,
            title = colors.baseContentTitle,
            icon = colors.baseContentBody,
            border = Color.Transparent,
            divider = colors.baseBorderSubtle
        )
    }
}

@Composable
private fun getAccordionSizeConfig(): AccordionSizeConfig {
    val typography = AppTheme.typography
    return AccordionSizeConfig(
        headerPadding = HierarchicalSize.Spacing.Medium,
        contentPadding = HierarchicalSize.Spacing.Medium,
        iconSize = IconSize.Small,
        titleStyle = typography.bodyBold,
        cornerRadius = RadiusSize.Medium,
        borderWidth = 1.dp,
        spacing = HierarchicalSize.Spacing.Small
    )
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaAccordion - Expandable/collapsible content sections
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Single accordion
 * var expanded by remember { mutableStateOf(false) }
 * PixaAccordion(
 *     title = "FAQ Question",
 *     expanded = expanded,
 *     onExpandedChange = { expanded = it }
 * ) {
 *     Text("Answer content goes here")
 * }
 *
 * // Accordion group
 * PixaAccordionGroup(
 *     items = listOf(
 *         AccordionItem("Section 1") { Text("Content 1") },
 *         AccordionItem("Section 2") { Text("Content 2") },
 *         AccordionItem("Section 3") { Text("Content 3") }
 *     ),
 *     allowMultiple = false
 * )
 * ```
 *
 * @param title Header title
 * @param expanded Whether content is visible
 * @param onExpandedChange Callback when expanded state changes
 * @param modifier Modifier
 * @param variant Visual style
 * @param colors Custom colors
 * @param icon Optional leading icon
 * @param expandIcon Custom expand/collapse icon
 * @param enabled Whether accordion is interactive
 * @param content Content to show when expanded
 */
@Composable
fun PixaAccordion(
    title: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    variant: AccordionVariant = AccordionVariant.Default,
    colors: AccordionColors? = null,
    icon: Painter? = null,
    expandIcon: Painter? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val themeColors = colors ?: getAccordionTheme(variant)
    val sizeConfig = getAccordionSizeConfig()

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(200)
    )

    val shape = RoundedCornerShape(sizeConfig.cornerRadius)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(themeColors.background)
            .then(
                if (variant == AccordionVariant.Outlined) {
                    Modifier.border(sizeConfig.borderWidth, themeColors.border, shape)
                } else Modifier
            )
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(themeColors.headerBackground)
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    role = Role.Button,
                    onClick = { onExpandedChange(!expanded) }
                )
                .padding(sizeConfig.headerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (icon != null) {
                    PixaIcon(
                        painter = icon,
                        contentDescription = null,
                        tint = themeColors.icon,
                        modifier = Modifier.size(sizeConfig.iconSize)
                    )
                    Spacer(modifier = Modifier.width(sizeConfig.spacing))
                }
                Text(
                    text = title,
                    style = sizeConfig.titleStyle,
                    color = themeColors.title
                )
            }

            if (expandIcon != null) {
                PixaIcon(
                    painter = expandIcon,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = themeColors.icon,
                    modifier = Modifier
                        .size(sizeConfig.iconSize)
                        .rotate(rotation)
                )
            }
        }

        // Content
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(200)),
            exit = shrinkVertically(animationSpec = tween(200))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeColors.contentBackground)
                    .padding(sizeConfig.contentPadding)
            ) {
                content()
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun PixaAccordionGroup(
    items: List<AccordionItem>,
    modifier: Modifier = Modifier,
    variant: AccordionVariant = AccordionVariant.Default,
    allowMultiple: Boolean = false,
    colors: AccordionColors? = null,
    expandIcon: Painter? = null
) {
    var expandedIndices by remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        items.forEachIndexed { index, item ->
            val isExpanded = index in expandedIndices

            PixaAccordion(
                title = item.title,
                expanded = isExpanded,
                onExpandedChange = { expanded ->
                    expandedIndices = if (expanded) {
                        if (allowMultiple) expandedIndices + index
                        else setOf(index)
                    } else {
                        expandedIndices - index
                    }
                },
                variant = variant,
                colors = colors,
                icon = item.icon,
                expandIcon = expandIcon,
                enabled = item.enabled,
                content = item.content
            )
        }
    }
}
