package com.pixamob.pixacompose.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.RadiusSize

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class PopoverPosition {
    TopStart,
    TopCenter,
    TopEnd,
    BottomStart,
    BottomCenter,
    BottomEnd,
    StartTop,
    StartCenter,
    StartBottom,
    EndTop,
    EndCenter,
    EndBottom
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class PopoverColors(
    val background: Color,
    val border: Color,
    val content: Color
)

@Immutable
@Stable
data class PopoverSizeConfig(
    val padding: Dp,
    val cornerRadius: Dp,
    val elevation: Dp,
    val offset: Dp,
    val textStyle: TextStyle
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getPopoverTheme(): PopoverColors {
    val colors = AppTheme.colors
    return PopoverColors(
        background = colors.baseSurfaceDefault,
        border = colors.baseBorderSubtle,
        content = colors.baseContentBody
    )
}

@Composable
private fun getPopoverSizeConfig(): PopoverSizeConfig {
    val typography = AppTheme.typography
    return PopoverSizeConfig(
        padding = HierarchicalSize.Spacing.Medium,
        cornerRadius = RadiusSize.Medium,
        elevation = HierarchicalSize.Shadow.Medium,
        offset = HierarchicalSize.Spacing.Small,
        textStyle = typography.bodyRegular
    )
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaPopover - Contextual popup content
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic popover
 * var showPopover by remember { mutableStateOf(false) }
 * Box {
 *     Button(onClick = { showPopover = true }) {
 *         Text("Show Popover")
 *     }
 *     PixaPopover(
 *         visible = showPopover,
 *         onDismiss = { showPopover = false },
 *         position = PopoverPosition.BottomCenter
 *     ) {
 *         Text("Popover content here")
 *     }
 * }
 *
 * // Popover with custom content
 * PixaPopover(
 *     visible = isVisible,
 *     onDismiss = { isVisible = false },
 *     position = PopoverPosition.TopEnd
 * ) {
 *     Column {
 *         Text("Title", style = MaterialTheme.typography.titleSmall)
 *         Text("Description text")
 *         Button(onClick = { /* action */ }) { Text("Action") }
 *     }
 * }
 * ```
 *
 * @param visible Whether popover is visible
 * @param onDismiss Callback when popover should close
 * @param modifier Modifier
 * @param position Position relative to anchor
 * @param colors Custom colors
 * @param content Popover content
 */
@Composable
fun PixaPopover(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    position: PopoverPosition = PopoverPosition.BottomCenter,
    colors: PopoverColors? = null,
    content: @Composable () -> Unit
) {
    val themeColors = colors ?: getPopoverTheme()
    val sizeConfig = getPopoverSizeConfig()

    val alignment = when (position) {
        PopoverPosition.TopStart, PopoverPosition.TopCenter, PopoverPosition.TopEnd -> Alignment.TopCenter
        PopoverPosition.BottomStart, PopoverPosition.BottomCenter, PopoverPosition.BottomEnd -> Alignment.BottomCenter
        PopoverPosition.StartTop, PopoverPosition.StartCenter, PopoverPosition.StartBottom -> Alignment.CenterStart
        PopoverPosition.EndTop, PopoverPosition.EndCenter, PopoverPosition.EndBottom -> Alignment.CenterEnd
    }

    val offset = when (position) {
        PopoverPosition.TopStart, PopoverPosition.TopCenter, PopoverPosition.TopEnd ->
            IntOffset(0, -sizeConfig.offset.value.toInt())
        PopoverPosition.BottomStart, PopoverPosition.BottomCenter, PopoverPosition.BottomEnd ->
            IntOffset(0, sizeConfig.offset.value.toInt())
        PopoverPosition.StartTop, PopoverPosition.StartCenter, PopoverPosition.StartBottom ->
            IntOffset(-sizeConfig.offset.value.toInt(), 0)
        PopoverPosition.EndTop, PopoverPosition.EndCenter, PopoverPosition.EndBottom ->
            IntOffset(sizeConfig.offset.value.toInt(), 0)
    }

    val transformOrigin = when (position) {
        PopoverPosition.TopStart, PopoverPosition.TopCenter, PopoverPosition.TopEnd ->
            TransformOrigin(0.5f, 1f)
        PopoverPosition.BottomStart, PopoverPosition.BottomCenter, PopoverPosition.BottomEnd ->
            TransformOrigin(0.5f, 0f)
        PopoverPosition.StartTop, PopoverPosition.StartCenter, PopoverPosition.StartBottom ->
            TransformOrigin(1f, 0.5f)
        PopoverPosition.EndTop, PopoverPosition.EndCenter, PopoverPosition.EndBottom ->
            TransformOrigin(0f, 0.5f)
    }

    if (visible) {
        Popup(
            alignment = alignment,
            offset = offset,
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + scaleIn(transformOrigin = transformOrigin),
                exit = fadeOut() + scaleOut(transformOrigin = transformOrigin)
            ) {
                Box(
                    modifier = modifier
                        .shadow(sizeConfig.elevation, RoundedCornerShape(sizeConfig.cornerRadius))
                        .clip(RoundedCornerShape(sizeConfig.cornerRadius))
                        .background(themeColors.background)
                        .padding(sizeConfig.padding)
                ) {
                    content()
                }
            }
        }
    }
}
