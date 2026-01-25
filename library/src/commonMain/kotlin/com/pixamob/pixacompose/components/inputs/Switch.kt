package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils

// ============================================================================
// Configuration
// ============================================================================

/**
 * Switch variant enum
 */
enum class SwitchVariant {
    Filled,    // Solid filled background
    Outlined,  // Outlined style with border
    Minimal    // Minimal design
}

/**
 * Switch size enum
 */
enum class SwitchSize {
    Small,     // Compact switch
    Medium,    // Standard switch
    Large      // Prominent switch
}

/**
 * Configuration for Switch appearance
 */
@Stable
private data class SwitchConfig(
    val width: Dp,
    val height: Dp,
    val thumbSize: Dp,
    val thumbPadding: Dp,
    val thumbElevation: Dp,
    val borderWidth: Dp,
    val labelStyle: TextStyle,
    val labelSpacing: Dp
)

/**
 * Get configuration for given size
 */
@Composable
private fun SwitchSize.config(): SwitchConfig {
    val typography = AppTheme.typography
    return when (this) {
        SwitchSize.Small -> SwitchConfig(
            width = ComponentSize.VerySmall,
            height = ComponentSize.Minimal - HierarchicalSize.Spacing.Small,
            thumbSize = IconSize.Tiny,
            thumbPadding = Spacing.Micro,
            thumbElevation = ShadowSize.Medium,
            borderWidth = BorderSize.Tiny,
            labelStyle = typography.bodyLight,
            labelSpacing = HierarchicalSize.Spacing.Small
        )
        SwitchSize.Medium -> SwitchConfig(
            width = ComponentSize.Medium,
            height = IconSize.Medium,
            thumbSize = IconSize.Small,
            thumbPadding = Spacing.Micro,
            thumbElevation = ShadowSize.Large,
            borderWidth = BorderSize.SlightlyThicker,
            labelStyle = typography.bodyRegular,
            labelSpacing = HierarchicalSize.Spacing.Medium
        )
        SwitchSize.Large -> SwitchConfig(
            width = ComponentSize.ExtraLarge,
            height = HierarchicalSize.Button.Small - HierarchicalSize.Spacing.Small,
            thumbSize = IconSize.Large,
            thumbPadding = Spacing.Micro,
            thumbElevation = HierarchicalSize.Shadow.Huge,
            borderWidth = BorderSize.Standard,
            labelStyle = typography.bodyBold,
            labelSpacing = HierarchicalSize.Spacing.Large
        )
    }
}

// ============================================================================
// Theme
// ============================================================================

/**
 * Colors for Switch states
 */
@Stable
private data class SwitchColors(
    val trackOn: Color,
    val trackOff: Color,
    val thumbOn: Color,
    val thumbOff: Color,
    val borderOn: Color,
    val borderOff: Color,
    val label: Color,
    val disabledTrackOn: Color,
    val disabledTrackOff: Color,
    val disabledThumb: Color,
    val disabledBorder: Color
)

/**
 * Get colors for Switch variant
 */
@Composable
private fun SwitchVariant.colors(): SwitchColors {
    val colors = AppTheme.colors

    return when (this) {
        SwitchVariant.Filled -> SwitchColors(
            trackOn = colors.brandSurfaceDefault,
            trackOff = colors.baseSurfaceElevated,
            thumbOn = colors.brandContentDefault,
            thumbOff = colors.baseContentCaption,
            borderOn = Color.Transparent,
            borderOff = Color.Transparent,
            label = colors.baseContentTitle,
            disabledTrackOn = colors.baseSurfaceDisabled,
            disabledTrackOff = colors.baseSurfaceDisabled,
            disabledThumb = colors.baseContentDisabled,
            disabledBorder = Color.Transparent
        )
        SwitchVariant.Outlined -> SwitchColors(
            trackOn = Color.Transparent,
            trackOff = Color.Transparent,
            thumbOn = colors.brandContentDefault,
            thumbOff = colors.baseContentCaption,
            borderOn = colors.brandBorderDefault,
            borderOff = colors.baseBorderDefault,
            label = colors.baseContentTitle,
            disabledTrackOn = Color.Transparent,
            disabledTrackOff = Color.Transparent,
            disabledThumb = colors.baseContentDisabled,
            disabledBorder = colors.baseBorderDisabled
        )
        SwitchVariant.Minimal -> SwitchColors(
            trackOn = colors.baseSurfaceElevated,
            trackOff = colors.baseSurfaceSubtle,
            thumbOn = colors.baseContentTitle,
            thumbOff = colors.baseContentBody,
            borderOn = colors.baseBorderSubtle,
            borderOff = colors.baseBorderSubtle,
            label = colors.baseContentTitle,
            disabledTrackOn = colors.baseSurfaceDisabled,
            disabledTrackOff = colors.baseSurfaceDisabled,
            disabledThumb = colors.baseContentDisabled,
            disabledBorder = colors.baseBorderDisabled
        )
    }
}

// ============================================================================
// Pixa Component
// ============================================================================

/**
 * PixaSwitch - Core switch component
 *
 * Binary toggle control with smooth animations and full customization.
 *
 * @param checked Whether the switch is on (true) or off (false)
 * @param onCheckedChange Callback when switch state changes
 * @param modifier Modifier for the switch
 * @param variant Visual style variant
 * @param size Size preset
 * @param enabled Whether the switch is enabled
 * @param label Optional label text
 * @param labelPosition Position of label (Start or End)
 * @param interactionSource Interaction source for state
 * @param contentDescription Accessibility description
 */
@Composable
fun PixaSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    variant: SwitchVariant = SwitchVariant.Filled,
    size: SwitchSize = SwitchSize.Medium,
    enabled: Boolean = true,
    label: String? = null,
    labelPosition: LabelPosition = LabelPosition.End,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null
) {
    val config = size.config()
    val colors = variant.colors()

    // Animated values
    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled && checked -> colors.disabledTrackOn
            !enabled -> colors.disabledTrackOff
            checked -> colors.trackOn
            else -> colors.trackOff
        },
        animationSpec = AnimationUtils.smoothSpring()
    )

    val thumbColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledThumb
            checked -> colors.thumbOn
            else -> colors.thumbOff
        },
        animationSpec = AnimationUtils.smoothSpring()
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledBorder
            checked -> colors.borderOn
            else -> colors.borderOff
        },
        animationSpec = AnimationUtils.smoothSpring()
    )

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) {
            config.width - config.thumbSize - config.thumbPadding
        } else {
            config.thumbPadding
        },
        animationSpec = AnimationUtils.smoothSpring()
    )

    val thumbScale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.9f,
        animationSpec = AnimationUtils.smoothSpring()
    )

    Row(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                this.contentDescription = contentDescription ?: label ?: "Switch"
            }
            .clickable(
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = false,
                    radius = config.width / 2
                )
            ) {
                onCheckedChange(!checked)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(config.labelSpacing)
    ) {
        // Label before switch
        if (label != null && labelPosition == LabelPosition.Start) {
            Text(
                text = label,
                style = config.labelStyle,
                color = if (enabled) colors.label else colors.disabledThumb
            )
        }

        // Switch track and thumb
        Box(
            modifier = Modifier
                .size(width = config.width, height = config.height)
                .clip(RoundedCornerShape(config.height / 2))
                .background(trackColor)
                .then(
                    if (borderColor != Color.Transparent) {
                        Modifier.border(
                            width = config.borderWidth,
                            color = borderColor,
                            shape = RoundedCornerShape(config.height / 2)
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            // Thumb
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(config.thumbSize)
                    .shadow(
                        elevation = if (enabled) config.thumbElevation else 0.dp,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(thumbColor)
            )
        }

        // Label after switch
        if (label != null && labelPosition == LabelPosition.End) {
            Text(
                text = label,
                style = config.labelStyle,
                color = if (enabled) colors.label else colors.disabledThumb
            )
        }
    }
}

/**
 * Label position for switch
 */
enum class LabelPosition {
    Start, // Label before switch
    End    // Label after switch
}

// ============================================================================
// Convenience Variants
// ============================================================================

/**
 * FilledSwitch - Filled style switch
 */
@Composable
fun FilledSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: SwitchSize = SwitchSize.Medium,
    enabled: Boolean = true,
    label: String? = null,
    labelPosition: LabelPosition = LabelPosition.End,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null
) {
    PixaSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        variant = SwitchVariant.Filled,
        size = size,
        enabled = enabled,
        label = label,
        labelPosition = labelPosition,
        interactionSource = interactionSource,
        contentDescription = contentDescription
    )
}

/**
 * OutlinedSwitch - Outlined style switch
 */
@Composable
fun OutlinedSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: SwitchSize = SwitchSize.Medium,
    enabled: Boolean = true,
    label: String? = null,
    labelPosition: LabelPosition = LabelPosition.End,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null
) {
    PixaSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        variant = SwitchVariant.Outlined,
        size = size,
        enabled = enabled,
        label = label,
        labelPosition = labelPosition,
        interactionSource = interactionSource,
        contentDescription = contentDescription
    )
}

/**
 * MinimalSwitch - Minimal design switch
 */
@Composable
fun MinimalSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: SwitchSize = SwitchSize.Medium,
    enabled: Boolean = true,
    label: String? = null,
    labelPosition: LabelPosition = LabelPosition.End,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null
) {
    PixaSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        variant = SwitchVariant.Minimal,
        size = size,
        enabled = enabled,
        label = label,
        labelPosition = labelPosition,
        interactionSource = interactionSource,
        contentDescription = contentDescription
    )
}

// ============================================================================
// Specialized Variants
// ============================================================================

/**
 * ToggleSwitch - Simple toggle without label
 */
@Composable
fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: SwitchSize = SwitchSize.Medium,
    enabled: Boolean = true,
    contentDescription: String? = null
) {
    FilledSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        size = size,
        enabled = enabled,
        label = null,
        contentDescription = contentDescription
    )
}

/**
 * SettingSwitch - Switch with label for settings
 */
@Composable
fun SettingSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    size: SwitchSize = SwitchSize.Medium,
    enabled: Boolean = true
) {
    FilledSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.fillMaxWidth(),
        size = size,
        enabled = enabled,
        label = label,
        labelPosition = LabelPosition.Start
    )
}
