package com.pixamob.pixacompose.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.components.actions.IconButtonColors
import com.pixamob.pixacompose.components.actions.IconButtonVariant
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.ScreenUtil
import com.pixamob.pixacompose.utils.elevationShadow
import com.pixamob.pixacompose.utils.pixaRipple
import kotlinx.coroutines.delay

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/** Which edge of the anchor the tooltip container appears on. */
enum class TooltipPosition {
    Top,
    Bottom,
    Start,
    End
}

/**
 * Where the pointer arrow sits along the anchor-facing edge, relative to [TooltipPosition].
 */
enum class TooltipPointerAlignment {
    Leading,
    Center,
    Trailing
}

/**
 * Display type. Governs default auto-dismiss behavior and available dismissal gestures.
 */
enum class TooltipVariant {
    Unprompted,
    Prompted
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class TooltipColors(
    val background: Color,
    val content: Color,
    val border: Color = Color.Unspecified
)

@Immutable
@Stable
data class TooltipSizeConfig(
    val padding: Dp,
    val cornerRadius: Dp,
    val borderWidth: Dp,
    val textStyle: TextStyle,
    val offset: Dp,
    val pointerWidth: Dp,
    val pointerHeight: Dp,
    val contentSpacing: Dp
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getTooltipTheme(): TooltipColors {
    val colors = AppTheme.colors
    // Inverse surface pairing: dark container / light content in light theme, and vice versa.
    return TooltipColors(
        background = colors.baseContentTitle,
        content = colors.baseSurfaceDefault,
        // Content-derived overlay keeps contrast correct in both themes (inverted surface polarity).
        border = colors.baseSurfaceDefault.copy(alpha = 0.16f)
    )
}

@Composable
private fun getTooltipSizeConfig(size: SizeVariant): TooltipSizeConfig {
    val typography = AppTheme.typography
    val textStyle = when (size) {
        SizeVariant.None, SizeVariant.Nano, SizeVariant.Compact -> typography.footnoteRegular
        SizeVariant.Small, SizeVariant.Medium -> typography.captionRegular
        SizeVariant.Large, SizeVariant.Huge, SizeVariant.Massive -> typography.bodyLight
    }
    val pointerWidth = HierarchicalSize.Spacing.forVariant(size)
    return TooltipSizeConfig(
        padding = HierarchicalSize.Padding.forVariant(size),
        cornerRadius = HierarchicalSize.Radius.forVariant(size),
        borderWidth = HierarchicalSize.Border.Compact,
        textStyle = textStyle,
        offset = HierarchicalSize.Spacing.Small,
        pointerWidth = pointerWidth,
        // Equilateral-ish arrow proportion: height is half the base width.
        pointerHeight = pointerWidth / 2,
        contentSpacing = HierarchicalSize.Spacing.Small
    )
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL TOOLTIP
// ════════════════════════════════════════════════════════════════════════════

private fun TooltipPointerAlignment.toHorizontalAlignment(): Alignment.Horizontal = when (this) {
    TooltipPointerAlignment.Leading -> Alignment.Start
    TooltipPointerAlignment.Center -> Alignment.CenterHorizontally
    TooltipPointerAlignment.Trailing -> Alignment.End
}

private fun TooltipPointerAlignment.toVerticalAlignment(): Alignment.Vertical = when (this) {
    TooltipPointerAlignment.Leading -> Alignment.Top
    TooltipPointerAlignment.Center -> Alignment.CenterVertically
    TooltipPointerAlignment.Trailing -> Alignment.Bottom
}

/** Direction the pointer arrow's tip faces, derived from [TooltipPosition]. */
private enum class PointerDirection { Up, Down, Left, Right }

/**
 * Pointer arrow — small filled triangle connecting the tooltip to its anchor.
 * Drawn locally with `Canvas` (no reusable directional-triangle shape in the library).
 */
@Composable
private fun PointerArrow(
    direction: PointerDirection,
    color: Color,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val size = when (direction) {
        PointerDirection.Up, PointerDirection.Down -> Modifier.size(width = width, height = height)
        PointerDirection.Left, PointerDirection.Right -> Modifier.size(width = height, height = width)
    }
    Canvas(modifier = modifier.then(size)) {
        val path = Path().apply {
            when (direction) {
                PointerDirection.Down -> {
                    moveTo(0f, 0f)
                    lineTo(this@Canvas.size.width, 0f)
                    lineTo(this@Canvas.size.width / 2f, this@Canvas.size.height)
                    close()
                }

                PointerDirection.Up -> {
                    moveTo(0f, this@Canvas.size.height)
                    lineTo(this@Canvas.size.width, this@Canvas.size.height)
                    lineTo(this@Canvas.size.width / 2f, 0f)
                    close()
                }

                PointerDirection.Right -> {
                    moveTo(0f, 0f)
                    lineTo(0f, this@Canvas.size.height)
                    lineTo(this@Canvas.size.width, this@Canvas.size.height / 2f)
                    close()
                }

                PointerDirection.Left -> {
                    moveTo(this@Canvas.size.width, 0f)
                    lineTo(this@Canvas.size.width, this@Canvas.size.height)
                    lineTo(0f, this@Canvas.size.height / 2f)
                    close()
                }
            }
        }
        drawPath(path = path, color = color)
    }
}

/**
 * Container + pointer arrow, laid out together so the arrow sits flush
 * against the edge the container is anchored from.
 */
@Composable
private fun TooltipBubble(
    message: String,
    position: TooltipPosition,
    pointerAlignment: TooltipPointerAlignment,
    variant: TooltipVariant,
    themeColors: TooltipColors,
    sizeConfig: TooltipSizeConfig,
    maxWidthFraction: Float,
    trailingIcon: Painter?,
    trailingIconContentDescription: String,
    onTrailingIconClick: (() -> Unit)?,
    onDismiss: (() -> Unit)?
) {
    val shape = RoundedCornerShape(sizeConfig.cornerRadius)
    val maxWidth = ScreenUtil.percentOfWidth(maxWidthFraction)

    val bubble = @Composable {
        Row(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .elevationShadow(ComponentElevation.Medium, shape = shape)
                .clip(shape)
                .background(themeColors.background)
                .border(sizeConfig.borderWidth, themeColors.border, shape)
                .then(
                    if (variant == TooltipVariant.Unprompted && onDismiss != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = pixaRipple(color = themeColors.content.copy(alpha = 0.12f)),
                            onClick = onDismiss
                        )
                    } else {
                        Modifier
                    }
                )
                .semantics { contentDescription = message }
                .padding(horizontal = sizeConfig.padding * 1.5f, vertical = sizeConfig.padding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(sizeConfig.contentSpacing)
        ) {
            BasicText(
                text = message,
                style = sizeConfig.textStyle.copy(color = themeColors.content),
                modifier = Modifier.widthIn(max = maxWidth)
            )

            if (trailingIcon != null) {
                PixaIconButton(
                    icon = trailingIcon,
                    onClick = { onTrailingIconClick?.invoke() },
                    variant = IconButtonVariant.Ghost,
                    size = SizeVariant.Small,
                    colors = IconButtonColors(contentColor = themeColors.content),
                    contentDescription = trailingIconContentDescription
                )
            }
        }
    }

    val arrow = @Composable { direction: PointerDirection ->
        PointerArrow(
            direction = direction,
            color = themeColors.background,
            width = sizeConfig.pointerWidth,
            height = sizeConfig.pointerHeight
        )
    }

    when (position) {
        TooltipPosition.Top -> Column(horizontalAlignment = pointerAlignment.toHorizontalAlignment()) {
            bubble()
            arrow(PointerDirection.Down)
        }

        TooltipPosition.Bottom -> Column(horizontalAlignment = pointerAlignment.toHorizontalAlignment()) {
            arrow(PointerDirection.Up)
            bubble()
        }

        TooltipPosition.Start -> Row(verticalAlignment = pointerAlignment.toVerticalAlignment()) {
            bubble()
            arrow(PointerDirection.Right)
        }

        TooltipPosition.End -> Row(verticalAlignment = pointerAlignment.toVerticalAlignment()) {
            arrow(PointerDirection.Left)
            bubble()
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * Brief contextual message anchored to [content].
 *
 * ### Purpose
 * Step-by-step guidance, upsells, supplementary info. Not for critical messages,
 * first-run onboarding (use popover), or feedback (use Toast/Snackbar).
 *
 * ### Anatomy
 * Container + pointer arrow + message + optional trailing icon button.
 *
 * ### Variants
 * [TooltipVariant.Unprompted] (auto-dismiss) or [TooltipVariant.Prompted] (caller-driven).
 * [TooltipPosition] controls anchor edge; [TooltipPointerAlignment] controls arrow position.
 *
 * ### States
 * Hidden → Fade-in → Visible → Fade-out, driven by [visible].
 *
 * ### Sizing
 * [SizeVariant]-driven. Width capped at 50% screen width.
 *
 * @param tooltip Message text (wraps, never truncates)
 * @param visible Tooltip visibility
 * @param variant Unprompted (auto-dismiss) or Prompted
 * @param position Anchor edge
 * @param pointerAlignment Arrow position along edge
 * @param size Padding/radius/typography preset
 * @param colors Custom color overrides (default: inverse-surface)
 * @param maxWidthFraction Max screen-width fraction (default: 0.5)
 * @param autoDismissMs Auto-dismiss duration (Unprompted default: 6000ms)
 * @param trailingIcon Optional icon-only trailing button
 * @param trailingIconContentDescription Accessibility label
 * @param onTrailingIconClick Trailing icon tap callback
 * @param onDismiss Dismiss callback (timeout, tap, Esc)
 * @param content Anchor content
 */
@Composable
fun PixaTooltip(
    tooltip: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    variant: TooltipVariant = TooltipVariant.Prompted,
    position: TooltipPosition = TooltipPosition.Top,
    pointerAlignment: TooltipPointerAlignment = TooltipPointerAlignment.Center,
    size: SizeVariant = SizeVariant.Medium,
    colors: TooltipColors? = null,
    maxWidthFraction: Float = 0.5f,
    autoDismissMs: Long? = null,
    trailingIcon: Painter? = null,
    trailingIconContentDescription: String = "Dismiss",
    onTrailingIconClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val themeColors = colors ?: getTooltipTheme()
    val sizeConfig = getTooltipSizeConfig(size)

    val effectiveAutoDismissMs = autoDismissMs
        ?: if (variant == TooltipVariant.Unprompted) UnpromptedDefaultDismissMs else null

    var isVisible by remember { mutableStateOf(visible) }

    LaunchedEffect(visible, effectiveAutoDismissMs) {
        isVisible = visible
        if (visible && effectiveAutoDismissMs != null) {
            delay(effectiveAutoDismissMs)
            isVisible = false
            onDismiss?.invoke()
        }
    }

    val dismiss: (() -> Unit)? = onDismiss?.let {
        {
            isVisible = false
            it()
        }
    }

    Box(
        modifier = modifier
            // Esc dismisses without stealing focus from the anchor subtree.
            .onKeyEvent { event ->
                if (isVisible && event.type == KeyEventType.KeyUp && event.key == Key.Escape) {
                    dismiss?.invoke()
                    true
                } else {
                    false
                }
            }
    ) {
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
                    enter = fadeIn(AnimationUtils.standardTween()),
                    exit = fadeOut(AnimationUtils.fastTween())
                ) {
                    TooltipBubble(
                        message = tooltip,
                        position = position,
                        pointerAlignment = pointerAlignment,
                        variant = variant,
                        themeColors = themeColors,
                        sizeConfig = sizeConfig,
                        maxWidthFraction = maxWidthFraction,
                        trailingIcon = trailingIcon,
                        trailingIconContentDescription = trailingIconContentDescription,
                        onTrailingIconClick = onTrailingIconClick,
                        onDismiss = dismiss
                    )
                }
            }
        }
    }
}

/** Spec: unprompted tooltips auto-dismiss after a 5-7 second window. */
private const val UnpromptedDefaultDismissMs = 6000L

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/**
 * Self-contained prompted tooltip: shows on demand and dismisses itself after
 * a short delay. Convenience wrapper over [PixaTooltip] for the common
 * "tap anchor to peek a hint" case.
 */
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
        variant = TooltipVariant.Prompted,
        position = position,
        autoDismissMs = 2000L,
        onDismiss = { showTooltip = false }
    ) {
        Box(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { showTooltip = !showTooltip }
            )
        ) {
            content()
        }
    }
}


