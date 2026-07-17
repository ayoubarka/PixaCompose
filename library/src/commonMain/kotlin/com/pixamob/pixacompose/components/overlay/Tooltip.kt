package com.pixamob.pixacompose.components.overlay

/**
 * PixaTooltip — PixaCompose's equivalent of Uber Base's "Tooltip" component.
 *
 * Source: https://base.uber.com/6d2425e9f/p/63fdd3-tooltip.md
 *
 * Purpose:
 *   Brief, informative messages that introduce new content/features or give
 *   short step-by-step guidance — upsells, education, and disclosure of
 *   supplementary info. Not for critical information, first-run onboarding
 *   (use a popover), or feedback messaging (use a snackbar/toast).
 *
 * Anatomy:
 *   Container (background + border + shadow) + pointer arrow (connects to the
 *   anchor) + message text + optional trailing icon button (navigation or
 *   dismissal — icon-only, never a text button).
 *
 * Variants:
 *   - [TooltipVariant.Unprompted]: auto-appears, auto-dismisses after
 *     ~5-7s (default 6s), dismissible early via its own tap, a trailing X
 *     button, or Esc.
 *   - [TooltipVariant.Prompted]: caller-driven via [PixaTooltip]'s `visible`
 *     (hover/click), dismissed by the caller (mouse leave/click) or Esc.
 *     Per spec, avoid pairing a trailing button with a hover-only trigger —
 *     that's a call-site wiring decision this component can't detect.
 *   - Pointer side ([TooltipPosition]): Top/Bottom/Start/End, i.e. which edge
 *     of the anchor the container appears on.
 *   - Pointer alignment ([TooltipPointerAlignment]): Leading/Center/Trailing
 *     — where the arrow sits along that edge (spec's vertical/horizontal
 *     pointer-position axes, unified into one enum relative to the placement
 *     axis).
 *
 * States: Hidden → Appearing (fade in) → Visible → Dismissing (fade out),
 *   driven by `visible` + [AnimatedVisibility] fade transitions.
 *
 * Sizing: [SizeVariant]-driven via [HierarchicalSize] resolvers (padding,
 *   radius, spacing) plus a size-scaled typography tier. Border weight (1dp)
 *   and elevation are fixed per spec regardless of size tier.
 *
 * Adaptive behavior: width is capped at 50% of the measured screen width via
 *   [ScreenUtil.percentOfWidth] on every platform — the spec applies the same
 *   50% ceiling on both mobile and desktop, so no separate breakpoint
 *   handling is needed here.
 *
 * Customization: background/content/border colors, pointer side + alignment,
 *   size, trailing icon + its click handler, auto-dismiss duration, max-width
 *   fraction. Not exposed: text truncation (spec explicitly discourages it —
 *   content authors are responsible for keeping messages to 1-3 lines; this
 *   component wraps but never clips), multiple trailing actions (spec allows
 *   exactly one icon button), an outlined 4px border style (spec calls it an
 *   example variant, not a documented customization boundary).
 *
 * Out of scope (documented per the spec, not implemented — global/app-level
 *   concerns a single composable can't own):
 *   - "One at a time" — showing a second tooltip only after the first
 *     dismisses, when multiple exist in one experience.
 *   - Suppressing display during active-driving scenarios.
 */

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
 * Where the pointer arrow sits along the anchor-facing edge (spec's
 * "Pointer Position" vertical/horizontal axes, unified relative to
 * [TooltipPosition]).
 */
enum class TooltipPointerAlignment {
    Leading,
    Center,
    Trailing
}

/**
 * Display type (spec: "By Display Type"). Governs default auto-dismiss
 * behavior and available dismissal gestures — see file-level KDoc.
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
    // Inverse surface pairing (dark container / light content in light theme,
    // and vice versa in dark theme) — matches the spec's default white-fill
    // desktop example while staying theme-aware via semantic tokens.
    return TooltipColors(
        background = colors.baseContentTitle,
        content = colors.baseSurfaceDefault,
        // Border derived from content color at low alpha rather than a
        // baseBorder* token: those tokens assume a light surface, but the
        // tooltip's surface polarity is inverted, so a content-derived
        // overlay (the same pattern Button/Chip/IconButton use for ripple
        // tints) keeps contrast correct in both themes.
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
        // Spec: "Weight: 1px (standard) ... Alignment: Inside" — fixed
        // regardless of size tier, the spec never scales border with size.
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
 * Pointer arrow — a small filled triangle connecting the container to its
 * anchor. Uber Base defines no reusable arrow/notch shape token for this
 * (`CustomShapes.kt`'s `TabShape`/`NotchShape` model container geometry, not
 * a standalone directional triangle), so it's drawn locally with `Canvas`,
 * the same approach `Stepper.kt` uses for its checkmark glyph.
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
 * PixaTooltip — brief contextual message anchored to [content] (Uber Base
 * "Tooltip" equivalent). See file-level KDoc for the full spec mapping.
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Prompted (hover/click-driven), no button
 * PixaTooltip(
 *     tooltip = "Settings menu",
 *     visible = isHovered,
 *     position = TooltipPosition.Bottom
 * ) { SettingsIcon() }
 *
 * // Unprompted upsell with dismiss button, auto-dismisses after ~6s
 * PixaTooltip(
 *     tooltip = "New invoice available",
 *     visible = showUpsell,
 *     variant = TooltipVariant.Unprompted,
 *     trailingIcon = painterResource(Res.drawable.ic_close),
 *     onDismiss = { showUpsell = false }
 * ) { InvoiceIcon() }
 * ```
 *
 * @param tooltip Message text. Keep to 1-2 lines (3 max) per spec — this
 *   component wraps within [maxWidthFraction] but never truncates.
 * @param visible Whether the tooltip is shown.
 * @param modifier Modifier for the anchor + tooltip wrapper.
 * @param variant [TooltipVariant.Unprompted] (auto-dismiss, tap/X/Esc to
 *   close early) or [TooltipVariant.Prompted] (caller-driven visibility).
 * @param position Which edge of the anchor the container appears on.
 * @param pointerAlignment Where the pointer arrow sits along that edge.
 * @param size [SizeVariant] driving padding/radius/typography.
 * @param colors Custom colors; falls back to the inverse-surface theme default.
 * @param maxWidthFraction Fraction of screen width the tooltip may occupy
 *   (spec: "no more than 50%" on both mobile and desktop).
 * @param autoDismissMs Auto-dismiss duration; defaults to 6000ms when
 *   [variant] is [TooltipVariant.Unprompted] (spec: 5-7s), null (no
 *   auto-dismiss) when [TooltipVariant.Prompted].
 * @param trailingIcon Optional single icon-only trailing button (navigation
 *   or dismissal). Per spec, avoid pairing this with a hover-only trigger.
 * @param trailingIconContentDescription Accessibility label for [trailingIcon].
 * @param onTrailingIconClick Callback for [trailingIcon] taps.
 * @param onDismiss Callback when the tooltip dismisses (timeout, tap, X, or Esc).
 * @param content Anchor content.
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
            // Spec (WCAG 1.4.13): Esc must dismiss without moving focus off
            // the trigger. Listening here (an ancestor of the anchor, not the
            // popup) avoids stealing focus into a non-interactive popup while
            // still intercepting Esc whenever the anchor subtree has focus.
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

// ════════════════════════════════════════════════════════════════════════════
// USAGE EXAMPLES
// ════════════════════════════════════════════════════════════════════════════

/**
 * USAGE EXAMPLES:
 *
 * 1. Basic prompted tooltip:
 * ```
 * PixaTooltip(
 *     tooltip = "This is a helpful tip",
 *     visible = showTooltip,
 *     onDismiss = { showTooltip = false }
 * ) {
 *     PixaIconButton(icon = infoIcon, onClick = { showTooltip = !showTooltip })
 * }
 * ```
 *
 * 2. Unprompted upsell with dismiss button and trailing pointer alignment:
 * ```
 * PixaTooltip(
 *     tooltip = "New invoice available",
 *     visible = showUpsell,
 *     variant = TooltipVariant.Unprompted,
 *     position = TooltipPosition.Bottom,
 *     pointerAlignment = TooltipPointerAlignment.Leading,
 *     trailingIcon = closeIcon,
 *     onDismiss = { showUpsell = false }
 * ) {
 *     BillingIcon()
 * }
 * ```
 *
 * 3. Quick tap-to-peek tooltip:
 * ```
 * PixaTooltipBox(tooltip = "Long press to edit") {
 *     EditButton()
 * }
 * ```
 */
