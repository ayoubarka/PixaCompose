package com.pixamob.pixacompose.components.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.LinkColors
import com.pixamob.pixacompose.components.actions.LinkSize
import com.pixamob.pixacompose.components.actions.PixaLink
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.MotionDuration
import com.pixamob.pixacompose.utils.QuinticEaseOutEasing
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Semantic variant, each with fixed highest-contrast treatment. No contrast
 * customization exposed — distinct from [AlertVariant].
 */
enum class SystemBannerVariant {
    Accent,
    Warning,
    Negative,
    Positive
}

/**
 * Trailing action model — action count changes the tap-target shape:
 * - [None]: static text, nothing tappable.
 * - [Single]: entire banner is one tap target.
 * - [Dual]: content block + icon = one target ([onPrimaryClick]); underlined
 *   CTA ([secondaryText]/[onSecondaryClick]) = second independent target.
 *
 * Structurally different from [AlertAction]'s single-slot model: System
 * Banner supports two simultaneous tap targets.
 */
sealed class SystemBannerAction {
    data object None : SystemBannerAction()
    data class Single(val onClick: () -> Unit) : SystemBannerAction()
    data class Dual(
        val onPrimaryClick: () -> Unit,
        val secondaryText: String,
        val onSecondaryClick: () -> Unit
    ) : SystemBannerAction()
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class SystemBannerColors(
    val background: Color,
    val content: Color
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Each variant uses saturated `*ContentDefault` as background with
 * [ColorPalette.baseContentNegative] for contrast. No contrast knob.
 */
@Composable
private fun getSystemBannerColors(variant: SystemBannerVariant, colors: ColorPalette): SystemBannerColors =
    when (variant) {
        SystemBannerVariant.Accent -> SystemBannerColors(colors.accentContentDefault, colors.baseContentNegative)
        SystemBannerVariant.Warning -> SystemBannerColors(colors.warningContentDefault, colors.baseContentNegative)
        SystemBannerVariant.Negative -> SystemBannerColors(colors.errorContentDefault, colors.baseContentNegative)
        SystemBannerVariant.Positive -> SystemBannerColors(colors.successContentDefault, colors.baseContentNegative)
    }

/**
 * Entrance/exit transitions: 300ms quintic ease-out, slides vertically from above.
 */
private val systemBannerEnter = slideInVertically(
    initialOffsetY = { -it },
    animationSpec = AnimationUtils.standardTween(MotionDuration.Standard, QuinticEaseOutEasing)
) + fadeIn(animationSpec = AnimationUtils.standardTween(MotionDuration.Standard, QuinticEaseOutEasing))

private val systemBannerExit = slideOutVertically(
    targetOffsetY = { -it },
    animationSpec = AnimationUtils.standardTween(MotionDuration.Standard, QuinticEaseOutEasing)
) + fadeOut(animationSpec = AnimationUtils.standardTween(MotionDuration.Standard, QuinticEaseOutEasing))

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL SYSTEM BANNER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun DismissControl(color: Color, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .size(HierarchicalSize.TouchTarget.Small) // 48px WCAG 2.5.5 minimum
            .clip(CircleShape)
            .clickable(
                onClick = onDismiss,
                indication = pixaRipple(bounded = true, color = color.copy(alpha = 0.16f)),
                interactionSource = remember { MutableInteractionSource() }
            )
            .semantics {
                contentDescription = "Dismiss"
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        BasicText(text = "×", style = AppTheme.typography.titleBold.copy(color = color))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaSystemBanner — communicates a global, high-priority state affecting the
 * entire app, appears without user action, and persists across screens until
 * dismissed or resolved. [PixaAlert] is the inline counterpart for
 * feature-specific banners.
 *
 * ### Anatomy
 * Required: container + [message]. Optional: leading [icon] (24dp, fixed),
 * trailing dismiss, and [action] (0/1/2 tap targets — see [SystemBannerAction]).
 *
 * ### Variants
 * [SystemBannerVariant.Accent]/`.Warning`/`.Negative`/`.Positive`.
 * All rendered at fixed maximum contrast.
 *
 * ### States
 * [visible] is caller-owned (not internal state) — hoist above navigation.
 * Entrance/exit: 300ms quintic ease-out.
 *
 * ### Sizing
 * Icon fixed at 24dp. [sidePadding] defaults to 64dp. [size] drives spacing.
 *
 * ### Adaptive behavior
 * Container spans full width. [sidePadding] exposed for responsive grid.
 * Status-bar integration and z-ordering are caller-owned.
 *
 * ### Customization
 * [customColors] overrides background/content pairing (caller manages contrast).
 * Action count capped at 2 by [SystemBannerAction].
 *
 * ### Usage notes
 * - Use only for critical functionality (connectivity loss) or system status.
 *   Not for promotions or ephemeral confirmations (use snackbar).
 * - [SystemBannerAction.Dual] underlined CTA uses [PixaLink].
 *
 * @param visible Caller-owned visibility (required)
 * @param message Required message text
 * @param variant Semantic variant (Default: [SystemBannerVariant.Accent])
 * @param modifier Modifier for the banner
 * @param icon Optional leading icon painter, fixed at 24dp
 * @param action Trailing action model — 0/1/2 tap targets (Default: [SystemBannerAction.None])
 * @param dismissible Whether to show trailing dismiss control (Default: false)
 * @param onDismiss Callback when dismiss is activated
 * @param sidePadding Left/right inset (Default: 64dp)
 * @param size Size variant driving internal spacing (Default: [SizeVariant.Medium])
 * @param messageMaxLines Line cap for [message] (Default: 2)
 * @param customColors Optional [SystemBannerColors] override
 * @param contentDescription Accessibility description for the message
 *
 * @sample
 * ```
 * PixaSystemBanner(
 *     visible = isOffline,
 *     message = "You're offline. Some features may be unavailable.",
 *     variant = SystemBannerVariant.Negative,
 *     dismissible = true,
 *     onDismiss = { isOffline = false }
 * )
 * ```
 */
@Composable
fun PixaSystemBanner(
    visible: Boolean,
    message: String,
    variant: SystemBannerVariant = SystemBannerVariant.Accent,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    action: SystemBannerAction = SystemBannerAction.None,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    sidePadding: Dp = 64.dp,
    size: SizeVariant = SizeVariant.Medium,
    messageMaxLines: Int = 2,
    customColors: SystemBannerColors? = null,
    contentDescription: String? = null,
) {
    val theme = customColors ?: getSystemBannerColors(variant, AppTheme.colors)
    val verticalPadding = HierarchicalSize.Padding.forVariant(size)
    val spacing = HierarchicalSize.Spacing.forVariant(size)

    AnimatedVisibility(
        visible = visible,
        enter = systemBannerEnter,
        exit = systemBannerExit
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(theme.background)
                .padding(horizontal = sidePadding, vertical = verticalPadding)
                .semantics {
                    this.contentDescription = contentDescription ?: message
                    liveRegion = LiveRegionMode.Polite
                },
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val contentBlockModifier = when (action) {
                is SystemBannerAction.Single -> Modifier
                    .weight(1f, fill = true)
                    .clickable(
                        onClick = action.onClick,
                        indication = pixaRipple(),
                        interactionSource = remember { MutableInteractionSource() },
                        role = Role.Button
                    )

                is SystemBannerAction.Dual -> Modifier
                    .weight(1f, fill = true)
                    .clickable(
                        onClick = action.onPrimaryClick,
                        indication = pixaRipple(),
                        interactionSource = remember { MutableInteractionSource() },
                        role = Role.Button
                    )

                SystemBannerAction.None -> Modifier.weight(1f, fill = true)
            }

            Row(
                modifier = contentBlockModifier,
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    PixaIcon(
                        painter = it,
                        contentDescription = null,
                        tint = theme.content,
                        modifier = Modifier.size(HierarchicalSize.Icon.Medium)
                    )
                }
                BasicText(
                    text = message,
                    style = AppTheme.typography.bodyRegular.copy(color = theme.content),
                    maxLines = messageMaxLines,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (action is SystemBannerAction.Dual) {
                PixaLink(
                    text = action.secondaryText,
                    onClick = action.onSecondaryClick,
                    size = LinkSize.Medium,
                    customColors = LinkColors(
                        default = theme.content,
                        hover = theme.content,
                        visited = theme.content,
                        disabled = theme.content.copy(alpha = 0.4f),
                        focusBorder = theme.content
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .sizeIn(minHeight = HierarchicalSize.TouchTarget.Small)
                )
            }

            if (dismissible && onDismiss != null) {
                DismissControl(theme.content, onDismiss)
            }
        }
    }
}
