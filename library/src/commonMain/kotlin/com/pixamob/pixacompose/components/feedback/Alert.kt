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
import com.pixamob.pixacompose.components.display.BaseCardVariant
import com.pixamob.pixacompose.components.display.PixaCard
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

/**
 * Semantic variant, mapped 1:1 from Uber Base Banner's Info/Success/Warning/Failure
 * types (kept as [AlertVariant.Error] per Pixa's existing feedback-family naming
 * convention rather than renamed to "Failure").
 */
enum class AlertVariant {
    Info,
    Success,
    Warning,
    Error
}

/**
 * Visual contrast level. Uber Base ties contrast directly to variant severity
 * ("low-contrast for lower severity; high-contrast for higher severity") rather
 * than leaving it a free choice — see [defaultAlertStyle], which [PixaAlert]
 * uses whenever [PixaAlert]'s `style` argument is left `null`. An explicit
 * `style` is still accepted for the rare case a caller needs to deviate.
 */
enum class AlertStyle {
    Filled,
    Outlined,
    Subtle
}

/**
 * Leading artwork slot, mapped from Uber Base Banner's two supported asset
 * kinds: a 24dp icon or a 40dp badge (both fixed sizes from Uber's icon/asset
 * libraries — not [SizeVariant]-driven, since the spec pins these as literal
 * asset dimensions, not a density scale). Illustrations/photos are an explicit
 * spec anti-pattern and have no representation here.
 */
sealed class AlertArtwork {
    /** No leading artwork. */
    data object None : AlertArtwork()

    /**
     * 24dp icon. Uber Base names specific per-variant icon assets
     * (`circle_i`/`circle_check`/`alert`/`circle_exclamation_point`) that Pixa
     * does not ship — pass [painter] to supply one, or omit it to fall back to
     * a plain variant-colored dot (this is a known gap, not a design choice;
     * see [PixaAlert]'s Customization docs).
     */
    data class Icon(val painter: Painter? = null) : AlertArtwork()

    /**
     * 40dp badge. Uber Base names specific per-variant badge assets
     * (`info`/`success`/`warning`/`circle_exclamation_mark`) that Pixa does
     * not ship — same fallback behavior as [Icon].
     */
    data class Badge(val painter: Painter? = null) : AlertArtwork()
}

/**
 * Trailing action slot. Uber Base caps Banner to "up to a single action in
 * the form of a secondary style pill button or tertiary icon button" — this
 * sealed type enforces exactly one control renders, matching that anatomy
 * constraint (the pre-spec API allowed an arbitrary multi-action row, which
 * this replaces).
 */
sealed class AlertAction {
    /** No trailing action. */
    data object None : AlertAction()

    /**
     * Secondary-style pill button ([ButtonVariant.Tonal] + [ButtonShape.Pill]
     * per Uber Base's "secondary style pill button"). Renders below the
     * message rather than inline with the title, since a caller-supplied
     * label's intrinsic width can't be measured in advance — this is Uber
     * Base's own documented alternative for "buttons with longer labels...
     * the button drops to the next line below the message," applied
     * unconditionally rather than as an opt-in layout mode.
     */
    data class Button(val text: String, val onClick: () -> Unit) : AlertAction()

    /**
     * Tertiary icon button ([IconButtonVariant.Ghost] per Uber Base's
     * "tertiary icon button"). Renders inline in the top-right corner next
     * to the title — icon buttons have a fixed, small footprint per spec, so
     * they never risk the label-overflow problem [Button] works around.
     */
    data class IconButton(
        val icon: Painter,
        val onClick: () -> Unit,
        val contentDescription: String? = null
    ) : AlertAction()

    /**
     * Dismiss control — a tertiary icon button (an "×" glyph, since Pixa has
     * no close-icon asset shipped) that hides the banner and invokes
     * [onDismiss]. Occupies the same single trailing slot as [Button]/[IconButton]
     * per spec; it is not an additional control layered on top of one of them.
     */
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
 * Uber Base ties visual contrast directly to variant severity rather than
 * leaving it caller-chosen: Info/Success are low severity → low contrast
 * ([AlertStyle.Subtle]), Warning is medium severity → medium contrast
 * ([AlertStyle.Outlined]), Error (Uber's "Failure", which "always persists
 * until resolved") is high severity → high contrast ([AlertStyle.Filled]).
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
 * Resolves spacing/inset/line-cap config. [size] drives density (spacing,
 * vertical inset) — Uber Base doesn't tie these to a size ladder itself, so
 * this follows Pixa's own [SizeVariant] convention (matching [CLAUDE.md]'s
 * guidance to prefer `HierarchicalSize.forVariant()` in new/touched code).
 * Horizontal inset is a separate fixed spec default (16dp mobile), exposed
 * directly on [PixaAlert] rather than folded into [size], since Uber Base
 * calls it out as independently responsive-grid-adjustable.
 */
@Composable
private fun getAlertConfig(size: SizeVariant, titleMaxLines: Int?, messageMaxLines: Int?): AlertConfig {
    return AlertConfig(
        spacing = HierarchicalSize.Spacing.forVariant(size),
        verticalInset = HierarchicalSize.Padding.forVariant(size),
        // Uber Base: headline/paragraph wrap by default; truncation is opt-in
        // only (text truncation is a named accessibility anti-pattern otherwise).
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
        is AlertArtwork.Icon -> HierarchicalSize.Icon.Medium // 24dp, exact spec match
        // No HierarchicalSize tier lands on Uber Base's 40dp asset-library badge
        // size (Badge tops out at 32dp, Avatar jumps 36dp→48dp) — kept as a
        // literal, spec-mandated fixed asset dimension rather than a new token.
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
            // Fallback when no Uber-named asset (circle_i, alert, etc.) is supplied.
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
 * states that persist until the user dismisses them or the underlying condition resolves.
 *
 * ### Anatomy
 * Required: container + [title] (headline). Optional: [message] (paragraph),
 * leading [artwork] (icon or badge), trailing [action] (single control only —
 * pill button, icon button, or dismiss).
 *
 * ### Variants
 * [AlertVariant.Info]/`.Success`/`.Warning`/`.Error` (Uber's Info/Success/Warning/Failure).
 *
 * ### States
 * Contrast is severity-driven via [defaultAlertStyle] unless [style] is
 * explicitly set. Persistence (stays until dismissed / disappears when the
 * underlying condition resolves) and session rate-limiting are caller-owned —
 * Uber Base itself leaves the exact rate-limiting logic implementation-defined,
 * and ties auto-dismiss to state resolution, not a fixed timer, so no
 * `autoDismissMillis`-style timer API is offered here (that belongs to
 * [Toast]/[Snackbar]'s ephemeral-feedback role, which Uber Base explicitly
 * excludes from Banner's scope).
 *
 * ### Sizing
 * [size] drives spacing/vertical inset density (Uber Base doesn't define a
 * size ladder itself). [horizontalInset] defaults to the spec's 16dp mobile
 * value and is independently adjustable, per "customized to accommodate
 * responsive design grids." [insetTop]/[insetBottom] toggle vertical insets
 * off entirely, per "top and bottom insets can be turned off."
 *
 * ### Adaptive behavior
 * The container always fills its incoming width (matching "Banner width
 * matches main content width on narrow and wide viewports") — screen-level
 * layout controls the actual contained width via [AppTheme.pageMargin] or an
 * explicit container, per `CLAUDE.md`'s "explicit caller-provided sizes
 * remain authoritative" rule. The corner radius + insets are what keep the
 * banner visually inset even at full container width; per spec, do not strip
 * them to force an edge-to-edge look.
 *
 * ### Customization
 * [customColors] overrides the background/border/content ladder (background
 * customization is explicitly spec-approved, "provided text/button colors
 * pass contrast standards and are tokenized for dark mode" — enforcing
 * contrast/dark-mode-tokenization automatically isn't possible at this layer,
 * so it's the caller's responsibility when supplying [customColors]).
 * [nested] switches the corner radius from the spec's default 12dp to 8dp
 * for use inside another rounded container. Core anatomy (container +
 * headline/paragraph structure) is not overridable, per spec.
 *
 * ### Usage notes
 * - Use for inline callouts, form validation, and warning/success/failure
 *   states; not for global system messaging (use a system-level banner) or
 *   ephemeral post-action feedback (use [Toast]/[Snackbar]).
 * - Avoid stacking multiple banners in proximity — combine multiple issues
 *   into one banner instead (not runtime-enforced, a caller-side layout rule).
 * - Keep messages short — under ~3 lines on mobile when possible (not
 *   runtime-enforced; [titleMaxLines]/[messageMaxLines] are available if a
 *   hard cap is actually desired, though wrapping is the spec default).
 *
 * @param title Required headline text (wraps by default; see [titleMaxLines])
 * @param message Optional paragraph text (wraps by default; see [messageMaxLines])
 * @param variant Semantic variant (Default: [AlertVariant.Info])
 * @param modifier Modifier for the alert
 * @param style Contrast level override (Default: `null` → severity-derived via [defaultAlertStyle])
 * @param size Size variant driving spacing/vertical inset density (Default: [SizeVariant.Medium])
 * @param artwork Leading artwork slot (Default: [AlertArtwork.Icon] with no painter → dot fallback)
 * @param action Trailing action slot — at most one control renders (Default: [AlertAction.None])
 * @param nested Whether this banner sits inside another rounded container (12dp → 8dp corner radius, Default: false)
 * @param horizontalInset Left/right inset (Default: spec's 16dp mobile value)
 * @param insetTop Whether to apply top inset (Default: true)
 * @param insetBottom Whether to apply bottom inset (Default: true)
 * @param titleMaxLines Optional line cap for [title] (Default: `null` → wraps unbounded)
 * @param messageMaxLines Optional line cap for [message] (Default: `null` → wraps unbounded)
 * @param customColors Optional [AlertColors] overriding the theme-derived ladder
 * @param contentDescription Accessibility description for the banner's message
 * @param dismissible Deprecated back-compat: equivalent to `action = AlertAction.Dismiss(onDismiss)`, ignored when [action] or [actionText] is set
 * @param onDismiss Deprecated back-compat, paired with [dismissible]
 * @param actionText Deprecated back-compat: equivalent to `action = AlertAction.Button(actionText, onAction)`, takes priority over [dismissible] when both are set (only one trailing control renders, per spec)
 * @param onAction Deprecated back-compat, paired with [actionText]
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

    // Legacy single-action back-compat: only one trailing control ever renders,
    // per spec's "up to a single action" anatomy. actionText wins when both a
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
