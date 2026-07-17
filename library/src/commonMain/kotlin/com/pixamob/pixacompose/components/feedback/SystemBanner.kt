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
 * Semantic variant (Accent/Warning/Negative/Positive), each with fixed highest-contrast treatment.
 * Separate from [AlertVariant] — this component does not expose contrast customization.
 */
enum class SystemBannerVariant {
    Accent,
    Warning,
    Negative,
    Positive
}

/**
 * Trailing action model — action *count* directly changes the tap-target shape:
 * - [None]: message is static text, nothing is tappable.
 * - [Single]: the entire banner becomes one tap target.
 * - [Dual]: the message+icon content block is one tap target ([onPrimaryClick]),
 *   and a separate underlined CTA ([secondaryText]/[onSecondaryClick]) is a
 *   second, independent tap target — matching spec's "CTA text underlined;
 *   content block + icon is tap target."
 *
 * This is structurally different from [AlertAction] (Alert/Banner's single-slot
 * model) because System Banner explicitly supports *two* simultaneous tap
 * targets with two different shapes, which [AlertAction] has no case for.
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
 * Each variant uses its saturated `*ContentDefault` token as background with
 * [ColorPalette.baseContentNegative] for highest contrast.
 * No caller-facing contrast/style knob — only the four semantic variants are customizable.
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
 * Entrance/exit transitions using 300ms quintic ease-out for both directions.
 * Slides vertically from above (docked position) rather than bottom-anchored like other surfaces.
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
 * entire app (not a single feature), appears without user action, and
 * persists across screens until dismissed or the triggering condition
 * resolves. [PixaAlert] is the inline/localized/rounded-and-inset
 * counterpart for feature-specific banners.
 *
 * ### Anatomy
 * Required: container + [message]. Optional: leading [icon] (24dp, fixed —
 * Uber Base explicitly excludes badge-sized assets here, "unsupported due to
 * limited space"), trailing dismiss ([dismissible]/[onDismiss]), and
 * [action] (0/1/2 actions, each count changing the tap-target shape — see
 * [SystemBannerAction]).
 *
 * ### Variants
 * [SystemBannerVariant.Accent] (default) / `.Warning` / `.Negative` / `.Positive`.
 * All rendered at fixed maximum contrast (see [getSystemBannerColors]).
 *
 * ### States
 * [visible] is caller-owned (not internal `remember` state like [PixaAlert])
 * so System Banner persists across screen/navigation changes.
 * Hoist [visible] above your navigation host.
 * Entrance/exit use [systemBannerEnter]/[systemBannerExit] (300ms quintic ease-out).
 *
 * ### Sizing
 * Icon is fixed at 24dp ([HierarchicalSize.Icon.Medium]).
 * [sidePadding] defaults to 64dp (not available in hierarchical size tiers).
 * [size] drives internal spacing/vertical padding.
 *
 * ### Adaptive behavior
 * The container always spans full width.
 * [sidePadding] is exposed directly for responsive grid adjustments.
 * Status-bar color integration and z-ordering are caller-owned concerns.
 *
 * ### Customization
 * [customColors] overrides the fixed background/content pairing (caller is responsible for maintaining contrast).
 * Action count is capped at 2 by [SystemBannerAction]; anatomy is not otherwise overridable.
 *
 * ### Usage notes
 * - Use only for critical functionality (connectivity loss) or system status (order status, chat).
 *   Not for promotions or ephemeral confirmations — use a snackbar for those.
 * - [SystemBannerAction.Dual]'s underlined CTA reuses [PixaLink], not a button.
 * - [SystemBannerAction] caps the underlined CTA at one.
 *
 * @param visible Caller-owned visibility (Default: must be supplied; no internal dismiss state)
 * @param message Required message text (Default line cap: [messageMaxLines])
 * @param variant Semantic variant (Default: [SystemBannerVariant.Accent])
 * @param modifier Modifier for the banner
 * @param icon Optional leading icon painter, fixed at 24dp
 * @param action Trailing action model — 0/1/2 tap targets (Default: [SystemBannerAction.None])
 * @param dismissible Whether to show a trailing dismiss control (Default: false)
 * @param onDismiss Callback when the dismiss control is activated
 * @param sidePadding Left/right inset (Default: 64dp)
 * @param size Size variant driving internal spacing/vertical padding (Default: [SizeVariant.Medium])
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
