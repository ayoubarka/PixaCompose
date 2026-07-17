package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.feedback.PixaCircularIndicator
import com.pixamob.pixacompose.components.feedback.ProgressColors
import com.pixamob.pixacompose.components.feedback.Skeleton
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.WindowSizeClass
import com.pixamob.pixacompose.utils.elevationShadow
import com.pixamob.pixacompose.utils.pixaRipple
import com.pixamob.pixacompose.utils.toDp

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Button hierarchy, mapped from Uber Base's Primary/Secondary/Tertiary/Outline/OnBrand
 * types onto Pixa's existing Filled/Tonal/Outlined/Ghost naming (kept per Pixa
 * convention rather than renamed to match Uber terms):
 * - [Filled] = Primary — single main action per screen.
 * - [Tonal] = Secondary — gray/tinted background, the most commonly used type.
 * - [Ghost] = Tertiary — low-emphasis dismissive actions (cancel, skip, dismiss).
 * - [Outlined] = Outline — for actions available only within button groups.
 * - [OnBrand] = OnBrand — for buttons placed on brand-colored surfaces (membership,
 *   special offers), where a colored fill would lose contrast.
 */
enum class ButtonVariant {
    Filled,
    Tonal,
    Outlined,
    Ghost,
    OnBrand
}

enum class ButtonShape {
    Default,
    Pill,
    Circle
}

/**
 * Width behavior, mapped from Uber Base's Fill/Hug-content model:
 * - [Flexible] = Hug content — width auto-adjusts to label/icon length, single-line,
 *   truncates with ellipsis once it hits the layout edge.
 * - [Fixed] = the explicit-fixed-width case — content wraps to additional lines
 *   instead of truncating (override the line cap with `PixaButton(maxLines = ...)`).
 *   Combine with `Modifier.width(...)` on the call site to actually fix the width.
 * - [FullBleed] = Fill — spans the full container width, label/leading icon centered,
 *   trailing icon pinned to the end.
 */
sealed class ButtonWidthPolicy {
    data object Flexible : ButtonWidthPolicy()
    data object Fixed : ButtonWidthPolicy()
    data object FullBleed : ButtonWidthPolicy()
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class ButtonSizeConfig(
    val height: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    val iconSpacing: Dp,
    val minWidth: Dp,
    val cornerRadius: Dp,
    val textStyle: @Composable () -> TextStyle
)

@Immutable
@Stable
data class ButtonColors(
    val background: Color,
    val content: Color,
    val border: Color = Color.Transparent,
    val ripple: Color = content.copy(alpha = 0.12f)
)

@Immutable
@Stable
data class ButtonStateColors(
    val default: ButtonColors,
    val disabled: ButtonColors,
    val loading: ButtonColors = default
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
internal fun getButtonSizeConfig(size: SizeVariant): ButtonSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.None -> ButtonSizeConfig(
            height = HierarchicalSize.Button.None,
            horizontalPadding = HierarchicalSize.Padding.None,
            iconSize = HierarchicalSize.Icon.None,
            iconSpacing = HierarchicalSize.Spacing.None,
            minWidth = HierarchicalSize.Spacing.None,  // no minimum — hug content
            cornerRadius = HierarchicalSize.Radius.None,
            textStyle = { typography.labelSmall }
        )

        SizeVariant.Nano -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Nano,
            horizontalPadding = HierarchicalSize.Padding.Nano,
            iconSize = HierarchicalSize.Icon.Nano,  // 12dp
            iconSpacing = HierarchicalSize.Spacing.Nano,  // 2dp
            minWidth = HierarchicalSize.Spacing.None,  // no minimum — hug content
            cornerRadius = HierarchicalSize.Radius.Nano,  // 2dp
            textStyle = { typography.actionMini }  // 10sp for 24dp button
        )

        SizeVariant.Compact -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Compact,  // 32dp
            horizontalPadding = HierarchicalSize.Padding.Small,  // 8dp
            iconSize = HierarchicalSize.Icon.Compact,  // 16dp
            iconSpacing = HierarchicalSize.Spacing.Compact,  // 4dp
            minWidth = HierarchicalSize.Spacing.None,  // no minimum — hug content
            cornerRadius = HierarchicalSize.Radius.Compact,  // 4dp
            textStyle = { typography.actionExtraSmall }  // 12sp for 32dp button
        )

        SizeVariant.Small -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Small,  // 36dp
            horizontalPadding = HierarchicalSize.Padding.Medium,  // 12dp
            iconSize = HierarchicalSize.Icon.Small,  // 20dp
            iconSpacing = HierarchicalSize.Spacing.Compact,  // 4dp
            minWidth = HierarchicalSize.Spacing.None,  // no minimum — hug content
            cornerRadius = HierarchicalSize.Radius.Small,  // 6dp
            textStyle = { typography.actionSmall }  // 14sp for 36dp button
        )

        SizeVariant.Medium -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Medium,  // 44dp
            horizontalPadding = HierarchicalSize.Padding.Large,  // 16dp
            iconSize = HierarchicalSize.Icon.Medium,  // 24dp
            iconSpacing = HierarchicalSize.Spacing.Compact,  // 4dp
            minWidth = HierarchicalSize.Spacing.Massive,  // 48dp
            cornerRadius = HierarchicalSize.Radius.Medium,  // 8dp
            textStyle = { typography.actionMedium }  // 16sp for 44dp button ⭐
        )

        SizeVariant.Large -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Large,  // 48dp
            horizontalPadding = HierarchicalSize.Padding.Large,  // 16dp
            iconSize = HierarchicalSize.Icon.Large,  // 28dp
            iconSpacing = HierarchicalSize.Spacing.Compact,  // 4dp
            minWidth = 120.dp,  // no HierarchicalSize category for button minWidth; per-tier floor keeps large CTAs from reading as too narrow
            cornerRadius = HierarchicalSize.Radius.Large,  // 12dp
            textStyle = { typography.actionLarge }  // 18sp for 48dp button
        )

        SizeVariant.Huge -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Huge,  // 56dp
            horizontalPadding = HierarchicalSize.Padding.Huge,  // 20dp
            iconSize = HierarchicalSize.Icon.Huge,  // 32dp
            iconSpacing = HierarchicalSize.Spacing.Small,  // 8dp
            minWidth = 160.dp,  // no HierarchicalSize category for button minWidth; see Large tier above
            cornerRadius = HierarchicalSize.Radius.Huge,  // 16dp
            textStyle = { typography.actionExtraLarge }  // 20sp for 56dp button
        )

        SizeVariant.Massive -> ButtonSizeConfig(
            height = HierarchicalSize.Button.Massive,  // 64dp
            horizontalPadding = HierarchicalSize.Padding.Massive,  // 24dp
            iconSize = HierarchicalSize.Icon.Massive,  // 48dp
            iconSpacing = HierarchicalSize.Spacing.Medium,  // 12dp
            minWidth = 200.dp,  // no HierarchicalSize category for button minWidth; see Large tier above
            cornerRadius = HierarchicalSize.Radius.Massive,  // 24dp
            textStyle = { typography.actionHuge }
        )
    }
}

/**
 * Resolves the button's clip/border shape.
 *
 * [ButtonShape.Pill]/[ButtonShape.Circle] reuse [AppTheme.shapes.pill] instead
 * of a raw `RoundedCornerShape(sizeConfig.height / 2)`: `Radius.Full` (9999dp)
 * is clamped by Compose to half the shortest side at draw time, so it renders
 * an identical stadium/circle for any given box size — same pixels, token-backed.
 * [ButtonShape.Default] still builds a raw `RoundedCornerShape` from
 * [sizeConfig]'s per-tier [HierarchicalSize.Radius] value (already a token,
 * just not wrapped in an `AppTheme.shapes.*` preset) because the 5-tier
 * `AppTheme.shapes.rounded` family doesn't line up with Button's own
 * per-`SizeVariant` radius ladder — swapping it in would change the actual
 * rendered corner radius on several size tiers. [cornerRadiusOverride] (only
 * used for the FullBleed rectangular case) also has no `AppTheme.shapes`
 * equivalent since it's a caller-supplied one-off value, not a themed tier.
 */
@Composable
private fun buttonShapeFor(
    shape: ButtonShape,
    sizeConfig: ButtonSizeConfig,
    cornerRadiusOverride: Dp?
): Shape = when {
    cornerRadiusOverride != null -> RoundedCornerShape(cornerRadiusOverride)
    shape == ButtonShape.Pill || shape == ButtonShape.Circle -> AppTheme.shapes.pill
    else -> RoundedCornerShape(sizeConfig.cornerRadius)
}

// ============================================================================
// THEME PROVIDER
// ============================================================================

/**
 * Get button theme colors for a variant
 */
@Composable
private fun getButtonTheme(
    variant: ButtonVariant,
    colors: ColorPalette,
    isDestructive: Boolean = false
): ButtonStateColors {
    // If destructive, use error colors instead of brand colors
    val brandOrErrorContent =
        if (isDestructive) colors.errorContentDefault else colors.brandContentDefault
    val brandOrErrorSurface =
        if (isDestructive) colors.errorSurfaceDefault else colors.brandSurfaceDefault
    val brandOrErrorSurfaceSubtle =
        if (isDestructive) colors.errorSurfaceSubtle else colors.brandSurfaceSubtle
    val brandOrErrorBorder =
        if (isDestructive) colors.errorBorderDefault else colors.brandBorderDefault

    return when (variant) {
        // Surface role = fill/background, Content role = foreground — matches
        // the Filled/Tonal convention used by PixaIconButton, PixaFAB, Chip, etc.
        ButtonVariant.Filled -> ButtonStateColors(
            default = ButtonColors(
                background = brandOrErrorSurface,
                content = brandOrErrorContent,
                ripple = brandOrErrorContent.copy(alpha = 0.12f)
            ),
            disabled = ButtonColors(
                background = colors.baseSurfaceDisabled,
                content = colors.baseContentDisabled,
                ripple = Color.Transparent
            )
        )

        ButtonVariant.Tonal -> ButtonStateColors(
            default = ButtonColors(
                background = brandOrErrorSurfaceSubtle,
                content = brandOrErrorContent,
                ripple = brandOrErrorContent.copy(alpha = 0.12f)
            ),
            disabled = ButtonColors(
                background = colors.baseSurfaceDisabled,
                content = colors.baseContentDisabled,
                ripple = Color.Transparent
            )
        )

        ButtonVariant.Outlined -> ButtonStateColors(
            default = ButtonColors(
                background = Color.Transparent,
                content = brandOrErrorContent,
                border = brandOrErrorBorder,
                ripple = brandOrErrorContent.copy(alpha = 0.12f)
            ),
            disabled = ButtonColors(
                background = Color.Transparent,
                content = colors.baseContentDisabled,
                border = colors.baseBorderDisabled,
                ripple = Color.Transparent
            )
        )

        ButtonVariant.Ghost -> ButtonStateColors(
            default = ButtonColors(
                background = Color.Transparent,
                content = brandOrErrorContent,
                ripple = brandOrErrorContent.copy(alpha = 0.12f)
            ),
            disabled = ButtonColors(
                background = Color.Transparent,
                content = colors.baseContentDisabled,
                ripple = Color.Transparent
            )
        )

        // Neutral base surface + brand content reads clearly against a brand-colored
        // parent — same reasoning as BadgeVariant.OnBrand in feedback/Badge.kt.
        ButtonVariant.OnBrand -> ButtonStateColors(
            default = ButtonColors(
                background = colors.baseSurfaceDefault,
                content = if (isDestructive) colors.errorContentDefault else colors.brandContentDefault,
                ripple = colors.brandContentDefault.copy(alpha = 0.12f)
            ),
            disabled = ButtonColors(
                background = colors.baseSurfaceDisabled,
                content = colors.baseContentDisabled,
                ripple = Color.Transparent
            )
        )
    }
}

// ============================================================================
// INTERNAL BUTTON (Core logic)
// ============================================================================

/**
 * Internal button implementation with core functionality
 */
@Composable
private fun InternalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingIcon: Painter? = null,
    size: SizeVariant = SizeVariant.Medium,
    shape: ButtonShape = ButtonShape.Default,
    colors: ButtonStateColors,
    elevation: Dp = ComponentElevation.None.toDp(),
    contentAlignment: Alignment = Alignment.Center,
    arrangement: Arrangement.Horizontal = Arrangement.Center,
    description: String? = null,
    cornerRadiusOverride: Dp? = null,
    // Fixed-width buttons grow past sizeConfig.height to fit wrapped label lines;
    // every other width policy stays pinned to the single-line size-tier height.
    allowMultilineHeight: Boolean = false,
    // Passed explicitly (rather than read from LocalContentColor) so text/icon
    // content can be colored without a CompositionLocalProvider wrapper.
    content: @Composable RowScope.(contentColor: Color) -> Unit
) {
    val sizeConfig = getButtonSizeConfig(size)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Determine current colors based on state
    val currentColors = when {
        loading -> colors.loading
        !enabled -> colors.disabled
        else -> colors.default
    }

    // Animate color transitions
    val backgroundColor by animateColorAsState(
        targetValue = currentColors.background,
        animationSpec = AnimationUtils.standardTween(150),
        label = "button_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = currentColors.content,
        animationSpec = AnimationUtils.standardTween(150),
        label = "button_content"
    )

    val borderColor by animateColorAsState(
        targetValue = currentColors.border,
        animationSpec = AnimationUtils.standardTween(150),
        label = "button_border"
    )

    val buttonShape = buttonShapeFor(shape, sizeConfig, cornerRadiusOverride)

    // For circle shape, width equals height
    val buttonModifier = if (shape == ButtonShape.Circle) {
        modifier.size(sizeConfig.height)
    } else if (allowMultilineHeight) {
        modifier
            .heightIn(min = sizeConfig.height)
            .widthIn(min = sizeConfig.minWidth)
            // Breathing room around wrapped lines now that height isn't fixed.
            .padding(vertical = HierarchicalSize.Spacing.Nano)
    } else {
        modifier
            .height(sizeConfig.height)
            .widthIn(min = sizeConfig.minWidth)
    }

    // ✅ Use Row directly instead of Box + Row
    Row(
        modifier = buttonModifier
            .elevationShadow(
                elevation = elevation,
                shape = buttonShape,
                clip = false,
                enabled = enabled
            )
            .clip(buttonShape)
            .background(backgroundColor)
            .then(
                if (currentColors.border != Color.Transparent) {
                    Modifier.border(
                        BorderStroke(HierarchicalSize.Border.Compact, borderColor),
                        buttonShape
                    )
                } else Modifier
            )
            .focusable(interactionSource = interactionSource)
            .then(
                if (isFocused && enabled) {
                    Modifier.border(
                        BorderStroke(HierarchicalSize.Border.Medium, colors.default.content),
                        buttonShape
                    )
                } else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = pixaRipple(bounded = true, color = currentColors.ripple),
                enabled = enabled && !loading,
                role = Role.Button,
                onClick = onClick,
                onClickLabel = description
            )
            .padding(
                horizontal = if (shape == ButtonShape.Circle) {
                    HierarchicalSize.Padding.None
                } else {
                    sizeConfig.horizontalPadding
                }
            ),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            if (loadingIcon != null) {
                PixaIcon(
                    painter = loadingIcon,
                    contentDescription = "Loading",
                    customSize = sizeConfig.iconSize,
                    tint = contentColor
                )
            } else {
                PixaCircularIndicator(
                    progress = null,
                    modifier = Modifier.size(sizeConfig.iconSize),
                    sizePreset = SizeVariant.Small,
                    customColors = ProgressColors(
                        progress = contentColor,
                        track = contentColor.copy(alpha = 0.2f),
                        label = contentColor
                    )
                )
            }
            Spacer(modifier = Modifier.width(sizeConfig.iconSpacing))
            content(contentColor)
        } else {
            content(contentColor)
        }
    }
}
// ============================================================================
// PUBLIC API
// ============================================================================

/**
 * PixaButton — interactive control for triggering actions, confirming choices,
 * and navigating flows.
 *
 * ### Anatomy
 * WithLabel (text + optional leading/trailing icon) or IconOnly (a single icon,
 * or ≤2 characters for [ButtonShape.Circle]) — never combine an icon with text
 * on a circular button; the icon wins and the text is dropped if both are given.
 *
 * ### Variants
 * [ButtonVariant.Filled] (Primary — one per screen), [ButtonVariant.Tonal]
 * (Secondary — the default workhorse), [ButtonVariant.Ghost] (Tertiary —
 * dismissive escapes: "Not now", "Skip", "Go back"), [ButtonVariant.Outlined]
 * (Outline — button-group-only), [ButtonVariant.OnBrand] (branded surfaces).
 * [isDestructive] swaps brand tokens for error tokens on any variant — for the
 * two-step danger flow Uber Base recommends, start with `Tonal + isDestructive`
 * (caution) and confirm with `Filled + isDestructive` (commit).
 *
 * ### Sizes
 * [SizeVariant] resolves through [HierarchicalSize.Button]; Uber Base's own
 * 4-tier button scale maps onto it as: xSmall(28dp)→[SizeVariant.Compact]
 * (32dp, nearest tier at/above the 28dp web click-target minimum — the ladder
 * has no exact 28dp step), Small(36dp)→[SizeVariant.Small] (exact),
 * Medium(48dp, Uber's default)→[SizeVariant.Large] (exact — note this is
 * *not* Pixa's own default tier name), Large(56dp)→[SizeVariant.Huge] (exact).
 *
 * ### Width & content wrapping
 * [ButtonWidthPolicy.Flexible] (hug content, single line, ellipsis-truncates),
 * [ButtonWidthPolicy.FullBleed] (fills the container, rectangular corners,
 * centers label/leading icon, pins trailing icon to the end), and
 * [ButtonWidthPolicy.Fixed] (wraps to [maxLines] lines instead of truncating —
 * pair with an explicit `Modifier.width(...)`).
 *
 * ### Adaptive behavior
 * [adaptiveWidth] opts a [ButtonWidthPolicy.Flexible] button into Uber Base's
 * narrow-viewport rule (scale to full width below the 600dp breakpoint, keep
 * intrinsic size above it) via [AppTheme.windowSizeClass]. Off by default —
 * an explicit [widthPolicy] always wins, adaptive behavior is opt-in, never a
 * hidden override.
 *
 * ### Usage notes
 * - One [ButtonVariant.Filled] button per screen (Uber Base's primary-button
 *   placement rule); multiple primary buttons are only sanctioned one-per-panel
 *   in multi-panel web tools.
 * - Labels: 1-3 words, sentence case, one action per button, no symbols/digits/
 *   punctuation/pronouns — this isn't enforced at runtime, it's a content rule.
 * - Prefer leaving buttons enabled with inline validation/errors over disabling
 *   them — a disabled button doesn't explain what's missing.
 *
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for styling
 * @param text Optional button text content (null or empty for icon-only buttons)
 * @param variant Visual hierarchy (Default: [ButtonVariant.Filled])
 * @param isDestructive Whether this is a destructive action (uses error colors)
 * @param enabled Whether the button is enabled (Default: true)
 * @param loading Whether the button shows loading state (Default: false)
 * @param size Size variant (Default: Medium — see size mapping table above)
 * @param shape Shape variant (Default: Default)
 * @param widthPolicy Width behavior — hug content, fill, or fixed-with-wrap
 * @param leadingIcon Optional icon before text
 * @param trailingIcon Optional icon after text
 * @param elevation Shadow elevation override (Default: auto — Low for filled-background variants)
 * @param description Accessibility description (recommended for icon-only buttons)
 * @param customColors Optional custom ButtonStateColors to override theme defaults
 * @param customIconSize Optional custom icon size to override size config
 * @param customTextStyle Optional custom text style to override size config
 * @param arrangement Content arrangement in the Row (Default: Center)
 * @param maxLines Line cap override; defaults to 1 (Flexible/FullBleed) or unlimited (Fixed)
 * @param adaptiveWidth Opt in to auto-fill width below the 600dp breakpoint (see Adaptive behavior above)
 *
 * @sample
 * ```
 * // Basic button
 * PixaButton(
 *     text = "Submit",
 *     onClick = { }
 * )
 *
 * // Two-step danger flow
 * PixaButton(text = "Delete", variant = ButtonVariant.Tonal, isDestructive = true, onClick = { })
 * PixaButton(text = "Confirm delete", variant = ButtonVariant.Filled, isDestructive = true, onClick = { })
 *
 * // Fixed-width button that wraps instead of truncating
 * PixaButton(
 *     text = "A longer supporting label",
 *     widthPolicy = ButtonWidthPolicy.Fixed,
 *     maxLines = 2,
 *     modifier = Modifier.width(120.dp),
 *     onClick = { }
 * )
 * ```
 */
@Composable
fun PixaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    variant: ButtonVariant = ButtonVariant.Filled,
    isDestructive: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingIcon: Painter? = null,
    showSkeleton: Boolean = false,
    size: SizeVariant = SizeVariant.Medium,
    shape: ButtonShape = ButtonShape.Default,
    widthPolicy: ButtonWidthPolicy = ButtonWidthPolicy.Flexible,
    selected: Boolean = false,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    elevation: Dp? = null,
    customColors: ButtonStateColors? = null,
    customIconSize: Dp? = null,
    customTextStyle: TextStyle? = null,
    arrangement: Arrangement.Horizontal = Arrangement.Center,
    description: String? = null,
    maxLines: Int? = null,
    adaptiveWidth: Boolean = false,
) {
    val sizeConfig = getButtonSizeConfig(size)
    val windowSizeClass = AppTheme.windowSizeClass

    if (showSkeleton) {
        val buttonModifier = if (shape == ButtonShape.Circle) {
            modifier.size(sizeConfig.height)
        } else {
            modifier
                .height(sizeConfig.height)
                .widthIn(min = sizeConfig.minWidth)
        }

        Skeleton(
            modifier = buttonModifier,
            height = sizeConfig.height,
            shape = buttonShapeFor(shape, sizeConfig, cornerRadiusOverride = null),
            shimmerEnabled = true
        )
        return
    }

    // Use custom colors if provided, otherwise use theme defaults
    // If customColors provided but isDestructive=true, still apply destructive styling
    val colors = if (customColors != null && !isDestructive) {
        customColors
    } else {
        getButtonTheme(variant, AppTheme.colors, isDestructive)
    }

    // Selected state overrides variant colors (but not disabled)
    val effectiveColors = if (selected && enabled) {
        val brandFocus = AppTheme.colors.brandSurfaceFocus
        val brandContent = AppTheme.colors.brandContentDefault
        ButtonStateColors(
            default = ButtonColors(
                background = brandFocus,
                content = brandContent,
                ripple = brandContent.copy(alpha = 0.12f)
            ),
            disabled = colors.disabled,
            loading = colors.loading
        )
    } else {
        colors
    }

    // Auto-elevation: filled-background variants (Filled/Tonal/OnBrand) rest at a
    // subtle Low lift; Outlined/Ghost rely on border/tonal emphasis instead of a
    // shadow (see ElevationUtils.kt).
    val buttonElevation = elevation ?: when (variant) {
        ButtonVariant.Filled, ButtonVariant.Tonal, ButtonVariant.OnBrand -> ComponentElevation.Low.toDp()
        else -> ComponentElevation.None.toDp()
    }

    // Determine if this is icon-only button
    val hasText = !text.isNullOrBlank()
    val hasIcons = leadingIcon != null || trailingIcon != null

    // Uber Base's narrow-viewport rule: below the 600dp breakpoint a button scales
    // up to fill its container width; above it, the button keeps its intrinsic
    // (hug-content) size. Opt-in only — explicit widthPolicy stays authoritative,
    // this never overrides an explicit FullBleed/Fixed choice, only Flexible.
    val effectiveWidthPolicy = if (adaptiveWidth && widthPolicy == ButtonWidthPolicy.Flexible && windowSizeClass == WindowSizeClass.Compact) {
        ButtonWidthPolicy.FullBleed
    } else {
        widthPolicy
    }

    // For icon-only buttons without explicit circle shape, suggest circle shape
    val effectiveShape = if (!hasText && hasIcons && shape == ButtonShape.Default) {
        ButtonShape.Circle
    } else if (effectiveWidthPolicy == ButtonWidthPolicy.FullBleed) {
        ButtonShape.Default
    } else {
        shape
    }

    // Apply full bleed modifier when widthPolicy is FullBleed
    val effectiveModifier = if (effectiveWidthPolicy == ButtonWidthPolicy.FullBleed) {
        modifier.fillMaxWidth()
    } else {
        modifier
    }

    InternalButton(
        onClick = onClick,
        modifier = effectiveModifier,
        enabled = enabled,
        loading = loading,
        loadingIcon = loadingIcon,
        size = size,
        shape = effectiveShape,
        colors = effectiveColors,
        elevation = buttonElevation,
        arrangement = arrangement,
        description = description,
        cornerRadiusOverride = if (effectiveWidthPolicy == ButtonWidthPolicy.FullBleed) {
            HierarchicalSize.Radius.None
        } else {
            null
        },
        allowMultilineHeight = effectiveWidthPolicy == ButtonWidthPolicy.Fixed
    ) { contentColor ->
        ButtonContent(
            text = text,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            iconSize = customIconSize ?: sizeConfig.iconSize,
            iconSpacing = sizeConfig.iconSpacing,
            textStyle = customTextStyle ?: sizeConfig.textStyle(),
            shape = effectiveShape,
            contentColor = contentColor,
            // Hug-content/fill truncate at one line; Fixed-width wraps by default,
            // per Uber Base's "fixed-width buttons wrap to next line" rule.
            maxLines = maxLines ?: if (effectiveWidthPolicy == ButtonWidthPolicy.Fixed) Int.MAX_VALUE else 1
        )
    }
}

/**
 * Button content layout helper.
 *
 * Circular buttons follow Uber Base's constraint: a single icon, OR up to 2
 * characters of text (initials/a digit) — never both combined. When both an
 * icon and text are supplied to a [ButtonShape.Circle] button, the icon wins
 * and the text is dropped, matching "icon-only" precedence elsewhere in this
 * file (see `effectiveShape` in [PixaButton]).
 */
@Composable
private fun RowScope.ButtonContent(
    text: String?,
    leadingIcon: Painter?,
    trailingIcon: Painter?,
    iconSize: Dp,
    iconSpacing: Dp,
    textStyle: TextStyle,
    shape: ButtonShape,
    contentColor: Color,
    maxLines: Int = 1
) {
    val hasLeadingIcon = leadingIcon != null
    val hasTrailingIcon = trailingIcon != null
    val hasAnyIcon = hasLeadingIcon || hasTrailingIcon
    val isCircle = shape == ButtonShape.Circle

    // Circular content is icon-only or a max-2-character label, never a full label.
    val circleText = text?.takeIf { !hasAnyIcon }?.trim()?.take(2)?.takeIf { it.isNotBlank() }
    val hasText = if (isCircle) circleText != null else !text.isNullOrBlank()
    val hasAnyContent = hasText || hasAnyIcon

    // Fallback: ensure minimum width for empty buttons
    if (!hasAnyContent) {
        Spacer(modifier = Modifier.width(iconSize))
        return
    }

    if (hasLeadingIcon) {
        PixaIcon(
            painter = leadingIcon,
            contentDescription = null,
            customSize = iconSize,
            tint = contentColor
        )

        if (hasText && !isCircle) {
            Spacer(modifier = Modifier.width(iconSpacing))
        }
    }

    if (hasText) {
        BasicText(
            text = if (isCircle) circleText!! else text!!,
            style = textStyle.copy(
                lineHeight = textStyle.fontSize,
                color = contentColor,
                textAlign = TextAlign.Center
            ),
            overflow = TextOverflow.Ellipsis,
            maxLines = if (isCircle) 1 else maxLines,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }

    if (hasTrailingIcon) {
        if (hasText && !isCircle) {
            Spacer(modifier = Modifier.width(iconSpacing))
        }

        PixaIcon(
            painter = trailingIcon,
            contentDescription = null,
            customSize = iconSize,
            tint = contentColor
        )
    }
}
