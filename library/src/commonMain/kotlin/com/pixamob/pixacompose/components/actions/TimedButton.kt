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
import com.pixamob.pixacompose.utils.pixaRipple
import com.pixamob.pixacompose.utils.toDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Timing presets for [PixaTimedButton.durationSeconds], plus the absolute
 * minimum floor (15s). Anything below [Minimum] is coerced up at runtime.
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
    // Drawn on top of [background] to represent the elapsed portion of the countdown.
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
                indication = pixaRipple(bounded = true, color = currentColors.ripple),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
                onClickLabel = description
            )
            .semantics {
                role = Role.Button
                contentDescription = description ?: "$text, $remainingSecondsLabel"
                // Polite live region (TalkBack) / "Updates frequently" (VoiceOver)
                // so the countdown is announced without re-focusing the control —
                // required for users who can't perceive the visual progress overlay.
                stateDescription = remainingSecondsLabel
                liveRegion = LiveRegionMode.Polite
            }
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = sizeConfig.minWidth)
                .height(sizeConfig.height)
        ) {
            // Progress overlay: fills from the start edge and shrinks as time elapses.
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
 * Renders the primary label and numerical countdown side by side. The countdown
 * is inline as a distinct secondary label, not wrapped onto a second line.
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
 * PixaTimedButton — a button that auto-advances after a finite countdown.
 * Use for optional flows requiring timely attention; commits to [onTimeout]
 * automatically if the user does not act first.
 *
 * ### Anatomy
 * Follows the same rectangle-button anatomy as [PixaButton], plus a secondary
 * numerical countdown label and a progress overlay that depletes as time
 * elapses. Leading/trailing icons are optional.
 *
 * ### Variants
 * Reuses [ButtonVariant]/[ButtonShape] from `Button.kt` for visual hierarchy
 * and shape. [isDestructive] swaps brand tokens for error tokens.
 *
 * ### States
 * Enabled (counting down immediately), pressed/focus (shared interaction/ripple/
 * focus-border treatment from [PixaButton]), disabled (freezes the countdown).
 *
 * ### Sizing
 * [SizeVariant] resolves through [HierarchicalSize.Button] via [getButtonSizeConfig].
 *
 * ### Behavior
 * Counts down immediately when enabled. Either the user clicks (invokes [onClick],
 * defaulting to [onTimeout] if not supplied) or the timer reaches zero and
 * [onTimeout] fires. [resetKey] restarts the countdown when changed.
 *
 * ### Timing presets
 * [durationSeconds] accepts [TimedButtonDuration] presets or a custom value;
 * anything under [TimedButtonDuration.Minimum] (15s) is coerced up.
 *
 * ### Accessibility
 * The numerical countdown is rendered as text (never hidden), with a polite
 * live region so screen readers announce the remaining time as it changes.
 *
 * ### Customization
 * Allowed: leading/trailing icons, custom [durationSeconds] (floor enforced),
 * [resetKey], [customColors]. Not allowed: removing the numerical countdown,
 * or a duration below the 15s floor.
 *
 * @param text Primary action label (required, single line)
 * @param onTimeout Invoked when the countdown reaches zero, or on proactive click if [onClick] is not supplied
 * @param modifier Modifier for styling
 * @param onClick Optional proactive-click handler; defaults to [onTimeout] when null
 * @param variant Visual hierarchy (Default: [ButtonVariant.Filled])
 * @param isDestructive Whether this is a destructive action (uses error colors)
 * @param enabled Whether the button is interactive and counting down (Default: true)
 * @param size Size variant (Default: Medium)
 * @param shape Shape variant (Default: Default)
 * @param leadingIcon Optional icon before the text
 * @param trailingIcon Optional icon after the text
 * @param durationSeconds Countdown length in seconds (Default: 30s); coerced up to 15s minimum
 * @param resetKey Changing this value restarts the countdown from [durationSeconds]
 * @param customColors Optional custom [TimedButtonStateColors] to override theme defaults
 * @param description Accessibility description; defaults to "{text}, {N} seconds remaining"
 *
 * @sample
 * ```
 * // A time-boxed offer that auto-confirms
 * PixaTimedButton(text = "Confirm ride", durationSeconds = TimedButtonDuration.Short, onTimeout = { confirmRide() })
 *
 * // Reversible action with an explicit cancel window
 * PixaTimedButton(text = "Undo", variant = ButtonVariant.Tonal, durationSeconds = TimedButtonDuration.Medium, onTimeout = { commitDelete() })
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
