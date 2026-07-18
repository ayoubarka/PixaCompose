package com.pixamob.pixacompose.components.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.ButtonShape
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.actions.ButtonWidthPolicy
import com.pixamob.pixacompose.components.actions.IconButtonVariant
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.components.surfaces.BaseCardVariant
import com.pixamob.pixacompose.components.surfaces.PixaCard
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.pixaRipple

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class AlertVariant {
    Info,
    Success,
    Warning,
    Error
}

/**
 * Visual contrast level. Default is severity-derived via [defaultAlertStyle];
 * an explicit [style] overrides it.
 */
enum class AlertStyle {
    Filled,
    Outlined,
    Subtle
}

sealed class AlertArtwork {
    data object None : AlertArtwork()

    data class Icon(val painter: Painter? = null) : AlertArtwork()

    data class Badge(val painter: Painter? = null) : AlertArtwork()
}

sealed class AlertAction {
    data object None : AlertAction()

    data class Button(val text: String, val onClick: () -> Unit) : AlertAction()

    data class IconButton(
        val icon: Painter,
        val onClick: () -> Unit,
        val contentDescription: String? = null
    ) : AlertAction()

    data class Dismiss(val onDismiss: () -> Unit) : AlertAction()
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class AlertColors(
    val background: Color,
    val border: Color,
    val icon: Color,
    val title: Color,
    val message: Color,
    val action: Color,
    val close: Color
)

@Immutable
@Stable
private data class AlertConfig(
    val spacing: Dp,
    val verticalInset: Dp,
    val maxTitleLines: Int,
    val maxMessageLines: Int
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Severity-derived default: Info/Success → Subtle, Warning → Outlined, Error → Filled.
 */
private fun defaultAlertStyle(variant: AlertVariant): AlertStyle = when (variant) {
    AlertVariant.Info, AlertVariant.Success -> AlertStyle.Subtle
    AlertVariant.Warning -> AlertStyle.Outlined
    AlertVariant.Error -> AlertStyle.Filled
}

@Composable
private fun getAlertColors(
    variant: AlertVariant,
    style: AlertStyle,
    colors: ColorPalette
): AlertColors {
    return when (variant) {
        AlertVariant.Info -> when (style) {
            AlertStyle.Filled -> AlertColors(
                background = colors.infoSurfaceDefault,
                border = colors.infoBorderDefault,
                icon = colors.infoContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.infoContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Outlined -> AlertColors(
                background = colors.baseSurfaceDefault,
                border = colors.infoBorderDefault,
                icon = colors.infoContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.infoContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Subtle -> AlertColors(
                background = colors.infoSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.infoContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.infoContentDefault,
                close = colors.baseContentBody
            )
        }
        AlertVariant.Success -> when (style) {
            AlertStyle.Filled -> AlertColors(
                background = colors.successSurfaceDefault,
                border = colors.successBorderDefault,
                icon = colors.successContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.successContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Outlined -> AlertColors(
                background = colors.baseSurfaceDefault,
                border = colors.successBorderDefault,
                icon = colors.successContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.successContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Subtle -> AlertColors(
                background = colors.successSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.successContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.successContentDefault,
                close = colors.baseContentBody
            )
        }
        AlertVariant.Warning -> when (style) {
            AlertStyle.Filled -> AlertColors(
                background = colors.warningSurfaceDefault,
                border = colors.warningBorderDefault,
                icon = colors.warningContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.warningContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Outlined -> AlertColors(
                background = colors.baseSurfaceDefault,
                border = colors.warningBorderDefault,
                icon = colors.warningContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.warningContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Subtle -> AlertColors(
                background = colors.warningSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.warningContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.warningContentDefault,
                close = colors.baseContentBody
            )
        }
        AlertVariant.Error -> when (style) {
            AlertStyle.Filled -> AlertColors(
                background = colors.errorSurfaceDefault,
                border = colors.errorBorderDefault,
                icon = colors.errorContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.errorContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Outlined -> AlertColors(
                background = colors.baseSurfaceDefault,
                border = colors.errorBorderDefault,
                icon = colors.errorContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.errorContentDefault,
                close = colors.baseContentBody
            )
            AlertStyle.Subtle -> AlertColors(
                background = colors.errorSurfaceSubtle,
                border = Color.Transparent,
                icon = colors.errorContentDefault,
                title = colors.baseContentTitle,
                message = colors.baseContentBody,
                action = colors.errorContentDefault,
                close = colors.baseContentBody
            )
        }
    }
}

/**
 * Resolves spacing/inset/line-cap config. [size] drives density via [HierarchicalSize].
 */
@Composable
private fun getAlertConfig(size: SizeVariant, titleMaxLines: Int?, messageMaxLines: Int?): AlertConfig {
    return AlertConfig(
        spacing = HierarchicalSize.Spacing.forVariant(size),
        verticalInset = HierarchicalSize.Padding.forVariant(size),
        // Wrapping is the default; truncation is opt-in.
        maxTitleLines = titleMaxLines ?: Int.MAX_VALUE,
        maxMessageLines = messageMaxLines ?: Int.MAX_VALUE
    )
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL ALERT
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun AlertArtworkContent(artwork: AlertArtwork, tint: Color) {
    val size = when (artwork) {
        is AlertArtwork.Icon -> HierarchicalSize.Icon.Medium
        is AlertArtwork.Badge -> 40.dp
        AlertArtwork.None -> return
    }
    val painter = when (artwork) {
        is AlertArtwork.Icon -> artwork.painter
        is AlertArtwork.Badge -> artwork.painter
        AlertArtwork.None -> null
    }

    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        if (painter != null) {
            PixaIcon(
                painter = painter,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .size(HierarchicalSize.Border.Nano)
                    .clip(CircleShape)
                    .background(tint)
            )
        }
    }
}

@Composable
private fun CornerAction(action: AlertAction, colors: AlertColors, size: SizeVariant) {
    when (action) {
        is AlertAction.IconButton -> PixaIconButton(
            icon = action.icon,
            onClick = action.onClick,
            variant = IconButtonVariant.Ghost,
            size = size,
            contentDescription = action.contentDescription
        )

        is AlertAction.Dismiss -> Box(
            modifier = Modifier
                .size(HierarchicalSize.TouchTarget.Compact)
                .clip(CircleShape)
                .clickable(
                    onClick = action.onDismiss,
                    indication = pixaRipple(bounded = true),
                    interactionSource = remember { MutableInteractionSource() }
                )
                .semantics {
                    contentDescription = "Dismiss"
                    role = Role.Button
                },
            contentAlignment = Alignment.Center
        ) {
            BasicText(text = "×", style = AppTheme.typography.titleBold.copy(color = colors.close))
        }

        else -> Unit
    }
}

@Composable
private fun BelowMessageAction(action: AlertAction.Button) {
    PixaButton(
        text = action.text,
        onClick = action.onClick,
        modifier = Modifier.padding(top = HierarchicalSize.Spacing.Compact),
        variant = ButtonVariant.Tonal,
        shape = ButtonShape.Pill,
        widthPolicy = ButtonWidthPolicy.Flexible,
        size = SizeVariant.Small
    )
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaAlert — communicates a contextual, page-specific state until dismissed
 * or resolved. Use for inline callouts, form validation, and warning/success/failure
 * states.
 *
 * ### Anatomy
 * Required: container + [title] (headline). Optional: [message] (paragraph),
 * leading [artwork] (icon or badge), trailing [action] (single control only —
 * pill button, icon button, or dismiss).
 *
 * ### Variants
 * [AlertVariant.Info]/`.Success`/`.Warning`/`.Error`
 *
 * ### States
 * Contrast is severity-driven via [defaultAlertStyle] unless [style] is
 * explicitly set. Persistence and session rate-limiting are caller-owned.
 *
 * ### Sizing
 * [size] drives spacing/vertical inset density. [horizontalInset] defaults to
 * 16dp. [insetTop]/[insetBottom] toggle vertical insets.
 *
 * ### Adaptive behavior
 * Container always fills its incoming width. Use [AppTheme.pageMargin] or an
 * explicit container for screen-level layout.
 *
 * ### Customization
 * [customColors] overrides background/border/content colors (caller must
 * ensure contrast standards and dark mode mapping).
 * [nested] switches corner radius to 8dp for use inside another rounded container.
 *
 * @param title Required headline text (wraps by default)
 * @param message Optional paragraph text
 * @param variant Semantic variant (Default: [AlertVariant.Info])
 * @param modifier Modifier for the alert
 * @param style Contrast level override (Default: `null` → severity-derived)
 * @param size Size variant driving spacing/vertical inset density (Default: [SizeVariant.Medium])
 * @param artwork Leading artwork slot (Default: [AlertArtwork.Icon] with no painter → dot fallback)
 * @param action Trailing action slot — at most one control renders (Default: [AlertAction.None])
 * @param nested Whether this banner sits inside another rounded container (12dp → 8dp corner radius, Default: false)
 * @param horizontalInset Left/right inset (Default: 16dp)
 * @param insetTop Whether to apply top inset (Default: true)
 * @param insetBottom Whether to apply bottom inset (Default: true)
 * @param titleMaxLines Optional line cap for [title] (Default: `null` → wraps unbounded)
 * @param messageMaxLines Optional line cap for [message] (Default: `null` → wraps unbounded)
 * @param customColors Optional [AlertColors] overriding the theme-derived ladder
 * @param contentDescription Accessibility description for the banner's message
 * @param dismissible Deprecated: maps to `action = AlertAction.Dismiss(onDismiss)`
 * @param onDismiss Deprecated, paired with [dismissible]
 * @param actionText Deprecated: maps to `action = AlertAction.Button(actionText, onAction)`
 * @param onAction Deprecated, paired with [actionText]
 *
 * @sample
 * ```
 * PixaAlert(
 *     title = "Upload failed",
 *     message = "Check your connection and try again.",
 *     variant = AlertVariant.Error,
 *     action = AlertAction.Button("Retry") { retry() }
 * )
 * ```
 */
@Composable
fun PixaAlert(
    title: String,
    message: String? = null,
    variant: AlertVariant = AlertVariant.Info,
    modifier: Modifier = Modifier,
    style: AlertStyle? = null,
    size: SizeVariant = SizeVariant.Medium,
    artwork: AlertArtwork = AlertArtwork.Icon(),
    action: AlertAction = AlertAction.None,
    nested: Boolean = false,
    horizontalInset: Dp = HierarchicalSize.Padding.Large,
    insetTop: Boolean = true,
    insetBottom: Boolean = true,
    titleMaxLines: Int? = null,
    messageMaxLines: Int? = null,
    customColors: AlertColors? = null,
    contentDescription: String? = null,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val resolvedStyle = style ?: defaultAlertStyle(variant)
    val colors = customColors ?: getAlertColors(variant, resolvedStyle, AppTheme.colors)
    val config = getAlertConfig(size, titleMaxLines, messageMaxLines)

    var visible by remember { mutableStateOf(true) }

    // Legacy single-action back-compat: actionText wins when both a
    // legacy action and dismissible are supplied together.
    val resolvedAction = when {
        action != AlertAction.None -> action
        actionText != null -> AlertAction.Button(actionText) { onAction?.invoke() }
        dismissible -> AlertAction.Dismiss {
            visible = false
            onDismiss?.invoke()
        }
        else -> AlertAction.None
    }

    val description = contentDescription ?: "${variant.name.lowercase()} banner: $title"
    val cornerRadius = if (nested) HierarchicalSize.Radius.Medium else HierarchicalSize.Radius.Large

    AnimatedVisibility(
        visible = visible,
        enter = AnimationUtils.scaleInTransition + AnimationUtils.fadeInTransition,
        exit = AnimationUtils.scaleOutTransition + AnimationUtils.fadeOutTransition
    ) {
        PixaCard(
            modifier = modifier
                .fillMaxWidth()
                .semantics { this.contentDescription = description },
            variant = when (resolvedStyle) {
                AlertStyle.Outlined -> BaseCardVariant.Outlined
                else -> BaseCardVariant.Filled
            },
            elevation = ComponentElevation.None,
            padding = SizeVariant.None,
            cornerRadius = cornerRadius,
            backgroundColor = colors.background
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        PaddingValues(
                            start = horizontalInset,
                            end = horizontalInset,
                            top = if (insetTop) config.verticalInset else HierarchicalSize.Padding.None,
                            bottom = if (insetBottom) config.verticalInset else HierarchicalSize.Padding.None
                        )
                    ),
                horizontalArrangement = Arrangement.spacedBy(config.spacing),
                verticalAlignment = Alignment.Top
            ) {
                AlertArtworkContent(artwork, colors.icon)

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Compact)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(config.spacing),
                        verticalAlignment = Alignment.Top
                    ) {
                        BasicText(
                            text = title,
                            style = AppTheme.typography.subtitleBold.copy(color = colors.title),
                            maxLines = config.maxTitleLines,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = true)
                        )
                        CornerAction(resolvedAction, colors, size)
                    }

                    message?.let {
                        BasicText(
                            text = it,
                            style = AppTheme.typography.bodyRegular.copy(color = colors.message),
                            maxLines = config.maxMessageLines,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (resolvedAction is AlertAction.Button) {
                        BelowMessageAction(resolvedAction)
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
fun InfoAlert(
    title: String,
    message: String? = null,
    modifier: Modifier = Modifier,
    action: AlertAction = AlertAction.None,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    PixaAlert(
        title = title,
        message = message,
        variant = AlertVariant.Info,
        modifier = modifier,
        action = action,
        dismissible = dismissible,
        onDismiss = onDismiss,
        actionText = actionText,
        onAction = onAction
    )
}

@Composable
fun SuccessAlert(
    title: String,
    message: String? = null,
    modifier: Modifier = Modifier,
    action: AlertAction = AlertAction.None,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    PixaAlert(
        title = title,
        message = message,
        variant = AlertVariant.Success,
        modifier = modifier,
        action = action,
        dismissible = dismissible,
        onDismiss = onDismiss,
        actionText = actionText,
        onAction = onAction
    )
}

@Composable
fun WarningAlert(
    title: String,
    message: String? = null,
    modifier: Modifier = Modifier,
    action: AlertAction = AlertAction.None,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    PixaAlert(
        title = title,
        message = message,
        variant = AlertVariant.Warning,
        modifier = modifier,
        action = action,
        dismissible = dismissible,
        onDismiss = onDismiss,
        actionText = actionText,
        onAction = onAction
    )
}

@Composable
fun ErrorAlert(
    title: String,
    message: String? = null,
    modifier: Modifier = Modifier,
    action: AlertAction = AlertAction.None,
    dismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    PixaAlert(
        title = title,
        message = message,
        variant = AlertVariant.Error,
        modifier = modifier,
        action = action,
        dismissible = dismissible,
        onDismiss = onDismiss,
        actionText = actionText,
        onAction = onAction
    )
}
