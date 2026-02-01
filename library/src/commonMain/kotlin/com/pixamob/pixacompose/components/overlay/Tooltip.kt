package com.pixamob.pixacompose.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.RadiusSize
import kotlinx.coroutines.delay

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class TooltipPosition {
    Top,
    Bottom,
    Start,
    End
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class TooltipColors(
    val background: Color,
    val content: Color
)

@Immutable
@Stable
data class TooltipSizeConfig(
    val padding: Dp,
    val cornerRadius: Dp,
    val elevation: Dp,
    val textStyle: TextStyle,
    val offset: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getTooltipTheme(): TooltipColors {
    val colors = AppTheme.colors
    return TooltipColors(
        background = colors.baseContentTitle,
        content = colors.baseSurfaceDefault
    )
}

@Composable
private fun getTooltipSizeConfig(): TooltipSizeConfig {
    val typography = AppTheme.typography
    return TooltipSizeConfig(
        padding = HierarchicalSize.Spacing.Small,
        cornerRadius = RadiusSize.Small,
        elevation = HierarchicalSize.Shadow.Small,
        textStyle = typography.captionRegular,
        offset = HierarchicalSize.Spacing.Compact
    )
}

// ════════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaTooltip - Contextual hints for UI elements
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic tooltip
 * PixaTooltip(
 *     tooltip = "This is a helpful tip",
 *     visible = showTooltip
 * ) {
 *     IconButton(onClick = { showTooltip = !showTooltip }) {
 *         Icon(Icons.Default.Info, contentDescription = "Info")
 *     }
 * }
 *
 * // Tooltip with position
 * PixaTooltip(
 *     tooltip = "Settings menu",
 *     visible = isHovered,
 *     position = TooltipPosition.Bottom
 * ) {
 *     SettingsIcon()
 * }
 *
 * // Auto-dismiss tooltip
 * PixaTooltip(
 *     tooltip = "Long press to edit",
 *     visible = showHint,
 *     autoDismissMs = 3000L
 * ) {
 *     EditButton()
 * }
 * ```
 *
 * @param tooltip Tooltip text content
 * @param visible Whether tooltip is visible
 * @param modifier Modifier
 * @param position Position relative to anchor
 * @param colors Custom colors
 * @param autoDismissMs Auto dismiss duration (null = no auto dismiss)
 * @param onDismiss Callback when tooltip dismisses
 * @param content Anchor content
 */
@Composable
fun PixaTooltip(
    tooltip: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    position: TooltipPosition = TooltipPosition.Top,
    colors: TooltipColors? = null,
    autoDismissMs: Long? = null,
    onDismiss: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val themeColors = colors ?: getTooltipTheme()
    val sizeConfig = getTooltipSizeConfig()

    var isVisible by remember { mutableStateOf(visible) }

    LaunchedEffect(visible) {
        isVisible = visible
        if (visible && autoDismissMs != null) {
            delay(autoDismissMs)
            isVisible = false
            onDismiss?.invoke()
        }
    }

    Box(modifier = modifier) {
        content()

        if (isVisible) {
            val alignment = when (position) {
                TooltipPosition.Top -> Alignment.TopCenter
                TooltipPosition.Bottom -> Alignment.BottomCenter
                TooltipPosition.Start -> Alignment.CenterStart
                TooltipPosition.End -> Alignment.CenterEnd
            }

            val offset = when (position) {
                TooltipPosition.Top -> IntOffset(0, -sizeConfig.offset.value.toInt())
                TooltipPosition.Bottom -> IntOffset(0, sizeConfig.offset.value.toInt())
                TooltipPosition.Start -> IntOffset(-sizeConfig.offset.value.toInt(), 0)
                TooltipPosition.End -> IntOffset(sizeConfig.offset.value.toInt(), 0)
            }

            Popup(
                alignment = alignment,
                offset = offset,
                properties = PopupProperties(focusable = false)
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(sizeConfig.elevation, RoundedCornerShape(sizeConfig.cornerRadius))
                            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
                            .background(themeColors.background)
                            .padding(
                                horizontal = sizeConfig.padding * 1.5f,
                                vertical = sizeConfig.padding
                            )
                    ) {
                        Text(
                            text = tooltip,
                            style = sizeConfig.textStyle,
                            color = themeColors.content
                        )
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun PixaTooltipBox(
    tooltip: String,
    modifier: Modifier = Modifier,
    position: TooltipPosition = TooltipPosition.Top,
    content: @Composable () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }

    PixaTooltip(
        tooltip = tooltip,
        visible = showTooltip,
        modifier = modifier,
        position = position,
        autoDismissMs = 2000L,
        onDismiss = { showTooltip = false }
    ) {
        Box(
            modifier = Modifier
        ) {
            content()
        }
    }
}
