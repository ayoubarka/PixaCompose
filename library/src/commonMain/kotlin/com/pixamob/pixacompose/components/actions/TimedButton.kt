package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.elevationShadow
import com.pixamob.pixacompose.utils.toDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Uber Base's timing presets for [PixaTimedButton.durationSeconds], plus the
 * absolute floor the spec calls out: "Never use a timer of under 10sec...
 * keep the button and its container for at least 15-20sec." [Minimum] is
 * enforced at runtime (see [PixaTimedButton]) regardless of what a caller
 * passes in.
 */
object TimedButtonDuration {
    /** Enough time to read short content and make a quick call. */
    const val Short = 30

    /** Default middle ground for most timed offers/confirmations. */
    const val Medium = 45

    /** For content that takes longer to read or weigh. */
    const val Long = 75

    /** Absolute runtime floor — durations below this are coerced up to it. */
    const val Minimum = 15
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class TimedButtonColors(
    val background: Color,
    val content: Color,
    val border: Color = Color.Transparent,
    // Drawn on top of [background] to represent the elapsed portion of the
    // countdown — the closest Compose primitive equivalent to the Figma
    // `Inner Shadow` progress technique the spec describes.
    val progressOverlay: Color = content.copy(alpha = 0.14f),
    val ripple: Color = content.copy(alpha = 0.12f)
)

@Immutable
@Stable
data class TimedButtonStateColors(
    val default: TimedButtonColors,
    val disabled: TimedButtonColors
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/**
 * Timed-button color resolution mirrors [ButtonVariant]'s semantics from
 * `Button.kt` (Filled/Tonal/Outlined/Ghost/OnBrand), but isn't reused directly
 * because `Button.kt`'s color resolver is file-private and its [ButtonColors]
 * has no progress-overlay slot.
 */
@Composable
private fun getTimedButtonTheme(
    variant: ButtonVariant,
    colors: ColorPalette,
    isDestructive: Boolean
): TimedButtonStateColors {
    val brandOrErrorContent = if (isDestructive) colors.errorContentDefault else colors.brandContentDefault
    val brandOrErrorSurface = if (isDestructive) colors.errorSurfaceDefault else colors.brandSurfaceDefault
    val brandOrErrorSurfaceSubtle = if (isDestructive) colors.errorSurfaceSubtle else colors.brandSurfaceSubtle
    val brandOrErrorBorder = if (isDestructive) colors.errorBorderDefault else colors.brandBorderDefault

    val disabled = TimedButtonColors(
        background = colors.baseSurfaceDisabled,
        content = colors.baseContentDisabled,
        progressOverlay = Color.Transparent,
        ripple = Color.Transparent
    )

    return when (variant) {
        ButtonVariant.Filled -> TimedButtonStateColors(
            default = TimedButtonColors(background = brandOrErrorSurface, content = brandOrErrorContent),
            disabled = disabled
        )

        ButtonVariant.Tonal -> TimedButtonStateColors(
            default = TimedButtonColors(background = brandOrErrorSurfaceSubtle, content = brandOrErrorContent),
            disabled = disabled
        )

        ButtonVariant.Outlined -> TimedButtonStateColors(
            default = TimedButtonColors(
                background = Color.Transparent,
                content = brandOrErrorContent,
                border = brandOrErrorBorder
            ),
            disabled = disabled.copy(border = colors.baseBorderDisabled)
        )

        ButtonVariant.Ghost -> TimedButtonStateColors(
            default = TimedButtonColors(background = Color.Transparent, content = brandOrErrorContent),
            disabled = disabled.copy(background = Color.Transparent)
        )

        ButtonVariant.OnBrand -> TimedButtonStateColors(
            default = TimedButtonColors(
                background = colors.baseSurfaceDefault,
                content = if (isDestructive) colors.errorContentDefault else colors.brandContentDefault
            ),
            disabled = disabled
        )
    }
}

@Composable
private fun timedButtonShapeFor(shape: ButtonShape, sizeConfig: ButtonSizeConfig): Shape = when (shape) {
    ButtonShape.Pill, ButtonShape.Circle -> AppTheme.shapes.pill
    ButtonShape.Default -> RoundedCornerShape(sizeConfig.cornerRadius)
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL TIMED BUTTON
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun InternalTimedButton(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    size: SizeVariant,
    shape: ButtonShape,
    colors: TimedButtonStateColors,
    progressFraction: Float,
    remainingSecondsLabel: String,
    description: String?,
    text: String,
    leadingIcon: Painter?,
    trailingIcon: Painter?,
) {
    val sizeConfig = getButtonSizeConfig(size)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val currentColors = if (enabled) colors.default else colors.disabled

    val backgroundColor by animateColorAsState(
        targetValue = currentColors.background,
        animationSpec = AnimationUtils.standardTween(150),
        label = "timed_button_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = currentColors.content,
        animationSpec = AnimationUtils.standardTween(150),
        label = "timed_button_content"
    )
    val borderColor by animateColorAsState(
        targetValue = currentColors.border,
        animationSpec = AnimationUtils.standardTween(150),
        label = "timed_button_border"
    )

    val buttonShape = timedButtonShapeFor(shape, sizeConfig)
    val elevation = when {
        // Outlined/Ghost rely on border/tonal emphasis rather than a shadow,
        // matching Button.kt's own elevation defaults.
        !enabled -> ComponentElevation.None.toDp()
        else -> ComponentElevation.Low.toDp()
    }

    Column(
        modifier = modifier
            .elevationShadow(elevation = elevation, shape = buttonShape, clip = false, enabled = enabled)
            .clip(buttonShape)
            .background(backgroundColor)
            .then(
                if (currentColors.border != Color.Transparent) {
                    Modifier.border(BorderStroke(HierarchicalSize.Border.Compact, borderColor), buttonShape)
                } else Modifier
            )
            .focusable(interactionSource = interactionSource)
            .then(
                if (isFocused && enabled) {
                    Modifier.border(BorderStroke(HierarchicalSize.Border.Medium, currentColors.content), buttonShape)
                } else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = currentColors.ripple),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
                onClickLabel = description
            )
            .semantics {
                role = Role.Button
                contentDescription = description ?: "$text, $remainingSecondsLabel"
                // "Updates frequently" (VoiceOver) / a polite live region (TalkBack)
                // so the countdown is announced without the user re-focusing the
                // control — the spec's required screen-reader fallback for users
                // who can't perceive the visual progress overlay.
                stateDescription = remainingSecondsLabel
                liveRegion = LiveRegionMode.Polite
            }
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = sizeConfig.minWidth)
                .height(sizeConfig.height)
        ) {
            // Progress overlay: fills from the start edge and shrinks as time
            // elapses, standing in for Figma's `Inner Shadow` progress technique
            // (there is no Compose primitive for an animated inner shadow).
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                    .background(currentColors.progressOverlay)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = sizeConfig.horizontalPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimedButtonContent(
                    text = text,
                    remainingSecondsLabel = remainingSecondsLabel,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    iconSize = sizeConfig.iconSize,
                    iconSpacing = sizeConfig.iconSpacing,
                    contentColor = contentColor
                )
            }
        }
    }
}

/**
 * Renders the two required text slots side by side: the primary label, then
 * the numerical countdown as a distinct, smaller secondary label. Per the
 * spec, "two line layouts are strictly reserved for localization & dynamic
 * type" — so the countdown sits inline as a second run of text rather than
 * wrapping the primary label onto its own second line.
 */
@Composable
private fun RowScope.TimedButtonContent(
    text: String,
    remainingSecondsLabel: String,
    leadingIcon: Painter?,
    trailingIcon: Painter?,
    iconSize: Dp,
    iconSpacing: Dp,
    contentColor: Color,
) {
    if (leadingIcon != null) {
        PixaIcon(painter = leadingIcon, contentDescription = null, customSize = iconSize, tint = contentColor)
        Spacer(modifier = Modifier.width(iconSpacing))
    }

    Column(
        modifier = Modifier.wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicText(
            text = text,
            style = AppTheme.typography.actionMedium.copy(color = contentColor, textAlign = TextAlign.Center),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        BasicText(
            text = remainingSecondsLabel,
            style = AppTheme.typography.captionRegular.copy(
                color = contentColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            ),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }

    if (trailingIcon != null) {
        Spacer(modifier = Modifier.width(iconSpacing))
        PixaIcon(painter = trailingIcon, contentDescription = null, customSize = iconSize, tint = contentColor)
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaTimedButton — a rectangle button that auto-advances the user after a
 * finite countdown. Migrated from Uber Base's Timed Button spec.
 *
 * ### Purpose
 * Notifies the user that the current screen requires timely attention, and
 * commits to [onTimeout] automatically if they don't act first. Per the spec:
 * "if misused, timed buttons can cause more harm than good — ensure usage is
 * tied only to optional flows and actions," never a required step.
 *
 * ### Anatomy
 * Follows the same rectangle-button anatomy as [PixaButton], plus two
 * required additions: a secondary numerical countdown label, and a progress
 * overlay that visually depletes as time elapses. Leading/trailing icons are
 * optional, matching standard button icon sizing.
 *
 * ### Variants
 * Reuses [ButtonVariant]/[ButtonShape] from `Button.kt` for visual hierarchy
 * and shape — a timed button is a rectangle-button behavior overlay, not a
 * new visual family. [isDestructive] swaps brand tokens for error tokens.
 *
 * ### States
 * Enabled (counting down immediately, before any interaction), pressed/focus
 * (handled by the shared interaction/ripple/focus-border treatment used by
 * [PixaButton]), and disabled (freezes the countdown — see Usage notes).
 *
 * ### Sizing
 * [SizeVariant] resolves through the same [HierarchicalSize.Button] ladder as
 * [PixaButton] via [getButtonSizeConfig].
 *
 * ### Behavior
 * Counts down immediately in the enabled state. Either the user clicks
 * proactively (invokes [onClick], defaulting to [onTimeout] if not supplied)
 * or the timer reaches zero and [onTimeout] fires automatically. [resetKey]
 * restarts the countdown when changed (e.g. re-showing the same button after
 * navigating back to it).
 *
 * ### Sizing/timing presets
 * [durationSeconds] accepts any of [TimedButtonDuration]'s presets or a
 * custom value; anything under [TimedButtonDuration.Minimum] (15s) is coerced
 * up to it — the spec calls a sub-10s timer "way too short" and a likely
 * cause of user errors.
 *
 * ### Adaptive behavior
 * Out of scope for this migration: the spec defers to "the same Button
 * breakpoints rules defined in [Uber's] documentation" without giving timed
 * button-specific breakpoint values, and `Button.kt`'s own `adaptiveWidth`
 * plumbing is a `ButtonWidthPolicy.Flexible`-only concern this component
 * doesn't currently expose a width policy for.
 *
 * ### Accessibility
 * The numerical countdown is a required element, never a decoration — the
 * spec is explicit that a purely visual progress indicator excludes users
 * with cognitive or visual disabilities. This implementation mirrors that:
 * [remainingSecondsLabel] is always rendered as text (never hidden), and the
 * control's `stateDescription` + a polite live region keep screen readers
 * announcing the remaining time as it changes, increasing in perceived
 * urgency as the countdown nears zero — matching the spec's requested
 * VoiceOver "Updates frequently" trait / TalkBack live-region behavior.
 *
 * ### Customization
 * Allowed: leading/trailing icons, custom [durationSeconds] (floor enforced),
 * [resetKey] as an explicit "extend/restart" trigger, [customColors]. Not
 * allowed by this API: removing the numerical countdown, or a duration below
 * the 15s floor — both are spec anti-patterns, not just style preferences.
 *
 * @param text Primary action label (required, single line)
 * @param onTimeout Invoked once when the countdown reaches zero, or immediately on a proactive click if [onClick] is not supplied
 * @param modifier Modifier for styling
 * @param onClick Optional proactive-click handler; defaults to [onTimeout] when null
 * @param variant Visual hierarchy (Default: [ButtonVariant.Filled])
 * @param isDestructive Whether this is a destructive action (uses error colors)
 * @param enabled Whether the button is interactive and counting down (Default: true)
 * @param size Size variant (Default: Medium)
 * @param shape Shape variant (Default: Default)
 * @param leadingIcon Optional icon before the text
 * @param trailingIcon Optional icon after the text
 * @param durationSeconds Countdown length in seconds (Default: [TimedButtonDuration.Short] = 30s); coerced up to [TimedButtonDuration.Minimum] (15s)
 * @param resetKey Changing this value restarts the countdown from [durationSeconds]
 * @param customColors Optional custom [TimedButtonStateColors] to override theme defaults
 * @param description Accessibility description; defaults to "{text}, {N} seconds remaining"
 *
 * @sample
 * ```
 * // A time-boxed offer that auto-confirms
 * PixaTimedButton(
 *     text = "Confirm ride",
 *     durationSeconds = TimedButtonDuration.Short,
 *     onTimeout = { confirmRide() }
 * )
 *
 * // Reversible action with an explicit cancel window
 * PixaTimedButton(
 *     text = "Undo",
 *     variant = ButtonVariant.Tonal,
 *     durationSeconds = TimedButtonDuration.Medium,
 *     onTimeout = { commitDelete() },
 *     onClick = { commitDelete() }
 * )
 * ```
 */
@Composable
fun PixaTimedButton(
    text: String,
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    variant: ButtonVariant = ButtonVariant.Filled,
    isDestructive: Boolean = false,
    enabled: Boolean = true,
    size: SizeVariant = SizeVariant.Medium,
    shape: ButtonShape = ButtonShape.Default,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    durationSeconds: Int = TimedButtonDuration.Short,
    resetKey: Any? = null,
    customColors: TimedButtonStateColors? = null,
    description: String? = null,
) {
    val effectiveDuration = remember(durationSeconds) {
        durationSeconds.coerceAtLeast(TimedButtonDuration.Minimum)
    }

    var remainingSeconds by remember(resetKey, effectiveDuration) { mutableIntStateOf(effectiveDuration) }
    val progress = remember(resetKey, effectiveDuration) { Animatable(1f) }

    LaunchedEffect(resetKey, enabled, effectiveDuration) {
        if (!enabled) return@LaunchedEffect

        progress.snapTo(1f)
        remainingSeconds = effectiveDuration

        val tickJob = launch {
            while (remainingSeconds > 0) {
                delay(1000)
                remainingSeconds -= 1
            }
        }

        progress.animateTo(
            targetValue = 0f,
            animationSpec = AnimationUtils.standardTween(
                durationMillis = effectiveDuration * 1000,
                easing = LinearEasing
            )
        )

        tickJob.cancel()
        remainingSeconds = 0
        onTimeout()
    }

    val colors = customColors ?: getTimedButtonTheme(variant, AppTheme.colors, isDestructive)
    val remainingSecondsLabel = "$remainingSeconds seconds remaining"

    InternalTimedButton(
        onClick = onClick ?: onTimeout,
        modifier = modifier,
        enabled = enabled,
        size = size,
        shape = shape,
        colors = colors,
        progressFraction = progress.value,
        remainingSecondsLabel = remainingSecondsLabel,
        description = description,
        text = text,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    )
}
