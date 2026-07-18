package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.pixaRipple

/**
 * PixaSwitch — Binary on/off control with immediate effect.
 *
 * ### Anatomy
 * Track (sliding channel) + knob (thumb), with optional label and description.
 *
 * ### Variants
 * [SwitchVariant]: Filled/Outlined/Ghost — each maps onto its own token set.
 *
 * ### States
 * Enabled (on/off), disabled, hover (knob shadow steps up), focus
 * (3dp `accentBorderFocus` ring), and [loading] (skeleton placeholder).
 *
 * ### Sizing
 * [SizeVariant]-driven via [HierarchicalSize].
 *
 * ### Behavior
 * Tapping the switch or its label toggles immediately via [Modifier.toggleable].
 * Use [PixaCheckbox] when a confirmation step is needed.
 *
 * @param checked Whether the switch is on or off
 * @param onCheckedChange Callback when switch state changes
 * @param modifier Modifier
 * @param variant Visual style variant (Filled, Outlined, Ghost)
 * @param size Size preset (Small, Medium, Large)
 * @param enabled Whether the switch is enabled
 * @param isError Error state (overrides variant colors)
 * @param loading Renders a [Skeleton] placeholder shaped like the track
 * @param colors Custom colors (null = use theme)
 * @param label Optional label text
 * @param labelPosition Position of label (Start or End)
 * @param description Optional secondary text below label
 * @param interactionSource Interaction source for state
 * @param contentDescription Accessibility description
 */

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class SwitchVariant {
    Filled,
    Outlined,
    Ghost
}

enum class LabelPosition {
    Start,
    End
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class SwitchSizeConfig(
    val width: Dp,
    val height: Dp,
    val thumbSize: Dp,
    val thumbPadding: Dp,
    val thumbElevation: Dp,
    val thumbHoverElevation: Dp,
    val borderWidth: Dp,
    val labelStyle: TextStyle,
    val labelSpacing: Dp
)

@Immutable
@Stable
data class SwitchColors(
    val trackOn: Color,
    val trackOff: Color,
    val thumbOn: Color,
    val thumbOff: Color,
    val borderOn: Color,
    val borderOff: Color,
    val label: Color,
    val focusRing: Color,
    val disabledTrackOn: Color,
    val disabledTrackOff: Color,
    val disabledThumb: Color,
    val disabledBorder: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getSwitchSizeConfig(size: SizeVariant): SwitchSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.Small -> SwitchSizeConfig(
            width = HierarchicalSize.Container.Compact,
            height = 20.dp - HierarchicalSize.Spacing.Small,
            thumbSize = HierarchicalSize.Icon.Nano,
            thumbPadding = HierarchicalSize.Spacing.Nano,
            thumbElevation = HierarchicalSize.Shadow.Medium,
            thumbHoverElevation = HierarchicalSize.Shadow.Large,
            borderWidth = HierarchicalSize.Border.Compact,
            labelStyle = typography.bodyLight,
            labelSpacing = HierarchicalSize.Spacing.Small
        )
        SizeVariant.Medium -> SwitchSizeConfig(
            width = HierarchicalSize.Container.Medium,
            height = HierarchicalSize.Icon.Medium,
            thumbSize = HierarchicalSize.Icon.Small,
            thumbPadding = HierarchicalSize.Spacing.Nano,
            thumbElevation = HierarchicalSize.Shadow.Large,
            thumbHoverElevation = HierarchicalSize.Shadow.Huge,
            borderWidth = 2.5.dp,
            labelStyle = typography.bodyRegular,
            labelSpacing = HierarchicalSize.Spacing.Medium
        )
        SizeVariant.Large -> SwitchSizeConfig(
            width = HierarchicalSize.Container.Huge,
            height = HierarchicalSize.Button.Small - HierarchicalSize.Spacing.Small,
            thumbSize = HierarchicalSize.Icon.Large,
            thumbPadding = HierarchicalSize.Spacing.Nano,
            thumbElevation = HierarchicalSize.Shadow.Huge,
            thumbHoverElevation = HierarchicalSize.Shadow.Massive,
            borderWidth = HierarchicalSize.Border.Medium,
            labelStyle = typography.bodyBold,
            labelSpacing = HierarchicalSize.Spacing.Large
        )
        else -> SwitchSizeConfig(
            width = HierarchicalSize.Container.Medium,
            height = HierarchicalSize.Icon.Medium,
            thumbSize = HierarchicalSize.Icon.Small,
            thumbPadding = HierarchicalSize.Spacing.Nano,
            thumbElevation = HierarchicalSize.Shadow.Large,
            thumbHoverElevation = HierarchicalSize.Shadow.Huge,
            borderWidth = 2.5.dp,
            labelStyle = typography.bodyRegular,
            labelSpacing = HierarchicalSize.Spacing.Medium
        )
    }
}

@Composable
private fun getSwitchTheme(variant: SwitchVariant): SwitchColors {
    val colors = AppTheme.colors
    // Spec's focus ring ("3px borderAccent outline") is a single state-driven
    // treatment, not a per-variant style choice, so every variant shares the
    // same `accentBorderFocus` token — PixaCompose's closest match to Uber's
    // "borderAccent" concept.
    val focusRing = colors.accentBorderFocus
    return when (variant) {
        SwitchVariant.Filled -> SwitchColors(
            trackOn = colors.brandSurfaceDefault,
            trackOff = colors.baseSurfaceElevated,
            thumbOn = colors.brandContentDefault,
            thumbOff = colors.baseContentCaption,
            borderOn = Color.Transparent,
            borderOff = Color.Transparent,
            label = colors.baseContentTitle,
            focusRing = focusRing,
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
            focusRing = focusRing,
            disabledTrackOn = Color.Transparent,
            disabledTrackOff = Color.Transparent,
            disabledThumb = colors.baseContentDisabled,
            disabledBorder = colors.baseBorderDisabled
        )
        SwitchVariant.Ghost -> SwitchColors(
            trackOn = colors.baseSurfaceElevated,
            trackOff = colors.baseSurfaceSubtle,
            thumbOn = colors.baseContentTitle,
            thumbOff = colors.baseContentBody,
            borderOn = colors.baseBorderSubtle,
            borderOff = colors.baseBorderSubtle,
            label = colors.baseContentTitle,
            focusRing = focusRing,
            disabledTrackOn = colors.baseSurfaceDisabled,
            disabledTrackOff = colors.baseSurfaceDisabled,
            disabledThumb = colors.baseContentDisabled,
            disabledBorder = colors.baseBorderDisabled
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaSwitch - Binary toggle control component
 *
 * A flexible switch with smooth animations and full customization.
 *
 * @sample
 *
 * ```kotlin
 * // Basic switch
 * var checked by remember { mutableStateOf(false) }
 * PixaSwitch(
 *     checked = checked,
 *     onCheckedChange = { checked = it }
 * )
 *
 * // Switch with label
 * PixaSwitch(
 *     checked = darkMode,
 *     onCheckedChange = { darkMode = it },
 *     label = "Dark Mode",
 *     labelPosition = LabelPosition.Start
 * )
 *
 * // Outlined variant
 * PixaSwitch(
 *     checked = notifications,
 *     onCheckedChange = { notifications = it },
 *     variant = SwitchVariant.Outlined,
 *     size = SizeVariant.Large
 * )
 *
 * // Loading placeholder
 * PixaSwitch(
 *     checked = false,
 *     onCheckedChange = {},
 *     loading = true
 * )
 *
 * // Custom colors
 * PixaSwitch(
 *     checked = enabled,
 *     onCheckedChange = { enabled = it },
 *     colors = SwitchColors(
 *         trackOn = Color.Green,
 *         thumbOn = Color.White,
 *         // ... other colors
 *     )
 * )
 * ```
 *
 * @param checked Whether the switch is on or off
 * @param onCheckedChange Callback when switch state changes
 * @param modifier Modifier for the switch
 * @param variant Visual style variant (Filled, Outlined, Minimal)
 * @param size Size preset (Small, Medium, Large)
 * @param enabled Whether the switch is enabled
 * @param isError Whether the switch is in error state (overrides variant colors, not disabled colors)
 * @param loading Whether to render a [Skeleton] placeholder shaped like the track instead of the interactive switch
 * @param colors Custom colors (null = use theme)
 * @param label Optional label text
 * @param labelPosition Position of label (Start or End)
 * @param description Optional secondary text below label
 * @param interactionSource Interaction source for state
 * @param contentDescription Accessibility description
 */
@Composable
fun PixaSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    variant: SwitchVariant = SwitchVariant.Filled,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    loading: Boolean = false,
    colors: SwitchColors? = null,
    label: String? = null,
    labelPosition: LabelPosition = LabelPosition.End,
    description: String? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null
) {
    val sizeConfig = getSwitchSizeConfig(size)

    if (loading) {
        Skeleton(
            modifier = modifier,
            width = sizeConfig.width,
            height = sizeConfig.height,
            shape = RoundedCornerShape(sizeConfig.height / 2),
            showBorder = false,
            contentDescription = contentDescription ?: label
        )
        return
    }

    val themeColors = colors ?: getSwitchTheme(variant)
    val errorColors = AppTheme.colors

    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled && checked -> themeColors.disabledTrackOn
            !enabled -> themeColors.disabledTrackOff
            isError && checked -> errorColors.errorSurfaceDefault
            checked -> themeColors.trackOn
            else -> themeColors.trackOff
        },
        animationSpec = AnimationUtils.colorSpring,
        label = "switch_track"
    )

    val thumbColor by animateColorAsState(
        targetValue = when {
            !enabled -> themeColors.disabledThumb
            isError -> errorColors.errorContentDefault
            checked -> themeColors.thumbOn
            else -> themeColors.thumbOff
        },
        animationSpec = AnimationUtils.colorSpring,
        label = "switch_thumb_color"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> themeColors.disabledBorder
            isError -> errorColors.errorBorderDefault
            checked -> themeColors.borderOn
            else -> themeColors.borderOff
        },
        animationSpec = AnimationUtils.colorSpring,
        label = "switch_border"
    )

    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) {
            (sizeConfig.width - sizeConfig.thumbSize - sizeConfig.thumbPadding).value
        } else {
            sizeConfig.thumbPadding.value
        },
        animationSpec = AnimationUtils.thumbSpring,
        label = "switch_thumb"
    )

    val thumbScale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.9f,
        animationSpec = AnimationUtils.selectionSpring,
        label = "switch_scale"
    )

    // Spec: "Knob shadow increases ... with 200ms transition" on hover.
    val thumbElevation by animateDpAsState(
        targetValue = when {
            !enabled -> 0.dp
            isHovered -> sizeConfig.thumbHoverElevation
            else -> sizeConfig.thumbElevation
        },
        animationSpec = AnimationUtils.standardTween(durationMillis = 200)
    )

    val trackShape = RoundedCornerShape(sizeConfig.height / 2)

    Row(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                this.contentDescription = contentDescription ?: label ?: "Switch"
            }
            .then(
                if (isFocused && enabled) {
                    Modifier
                        .border(
                            width = HierarchicalSize.Border.Large,
                            color = themeColors.focusRing,
                            shape = RoundedCornerShape(sizeConfig.height / 2 + HierarchicalSize.Spacing.Nano)
                        )
                        .padding(HierarchicalSize.Spacing.Nano)
                } else Modifier
            )
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = pixaRipple(
                    bounded = false,
                    radius = sizeConfig.width / 2
                ),
                onValueChange = onCheckedChange
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(sizeConfig.labelSpacing)
    ) {
        if (label != null && labelPosition == LabelPosition.Start) {
            Column {
                BasicText(
                    text = label,
                    style = sizeConfig.labelStyle.copy(
                        color = if (enabled) themeColors.label else AppTheme.colors.baseContentDisabled
                    )
                )
                if (description != null) {
                    BasicText(
                        text = description,
                        style = AppTheme.typography.captionRegular.copy(color = AppTheme.colors.baseContentCaption),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(width = sizeConfig.width, height = sizeConfig.height)
                .clip(trackShape)
                .background(trackColor)
                .then(
                    if (borderColor != Color.Transparent) {
                        Modifier.border(
                            width = sizeConfig.borderWidth,
                            color = borderColor,
                            shape = trackShape
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset.dp)
                    .size(sizeConfig.thumbSize * thumbScale)
                    .shadow(
                        elevation = thumbElevation,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(thumbColor)
            )
        }

        if (label != null && labelPosition == LabelPosition.End) {
            Column {
                BasicText(
                    text = label,
                    style = sizeConfig.labelStyle.copy(
                        color = if (enabled) themeColors.label else AppTheme.colors.baseContentDisabled
                    )
                )
                if (description != null) {
                    BasicText(
                        text = description,
                        style = AppTheme.typography.captionRegular.copy(color = AppTheme.colors.baseContentCaption),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Convenience Variants
// ════════════════════════════════════════════════════════════════════════════

/**
 * FilledSwitch - Filled style switch
 */
@Composable
fun FilledSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String? = null,
    description: String? = null,
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
        isError = isError,
        label = label,
        description = description,
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
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String? = null,
    description: String? = null,
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
        isError = isError,
        label = label,
        description = description,
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
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String? = null,
    description: String? = null,
    labelPosition: LabelPosition = LabelPosition.End,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null
) {
    PixaSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        variant = SwitchVariant.Ghost,
        size = size,
        enabled = enabled,
        isError = isError,
        label = label,
        description = description,
        labelPosition = labelPosition,
        interactionSource = interactionSource,
        contentDescription = contentDescription
    )
}

// ════════════════════════════════════════════════════════════════════════════
// Specialized Variants
// ════════════════════════════════════════════════════════════════════════════

/**
 * ToggleSwitch - Simple toggle without label
 */
@Composable
fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Medium,
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
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    description: String? = null
) {
    PixaSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.fillMaxWidth(),
        variant = SwitchVariant.Filled,
        size = size,
        enabled = enabled,
        isError = isError,
        label = label,
        labelPosition = LabelPosition.Start,
        description = description
    )
}
