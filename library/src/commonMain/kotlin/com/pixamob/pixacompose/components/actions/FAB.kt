package com.pixamob.pixacompose.components.actions

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.AnimationUtils.AnimatedVisibilityStandard
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.elevationShadow
import com.pixamob.pixacompose.utils.pixaRipple
import com.pixamob.pixacompose.utils.toDp

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class FABVariant {
    Filled,
    Tonal,
    Outlined
}

/**
 * Screen corner an expandable FAB's menu (and the caller's own placement of the
 * main button) anchor to. Not part of the LINE spec — the source only says FABs
 * sit "at the top layer," without mandating a default corner; this is a Pixa
 * adaptation so [PixaExpandableFab] can position its scrim/menu Popup.
 */
enum class FabDockAlignment { BottomEnd, BottomStart, BottomCenter }

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
data class FABColors(
    val containerColor: Color = Color.Unspecified,
    val contentColor: Color = Color.Unspecified,
    val borderColor: Color = Color.Unspecified,
    val disabledContainerColor: Color = Color.Unspecified,
    val disabledContentColor: Color = Color.Unspecified
)

@Immutable
data class FABSizeConfig(
    val containerSize: Dp,
    val iconSize: Dp
)

/**
 * One child action in an expandable FAB's menu — LINE anatomy's "Child Button
 * Container" + "Child Button Icon" + optional "Button Label".
 */
@Immutable
data class FabAction(
    val icon: Painter,
    val onClick: () -> Unit,
    val label: String? = null,
    val contentDescription: String? = null
)

/** Theming for an expandable FAB's child rows and dimmer/scrim. */
@Immutable
data class FabMenuColors(
    val childContainer: Color = Color.Unspecified,
    val childIcon: Color = Color.Unspecified,
    val labelBackground: Color = Color.Unspecified,
    val labelText: Color = Color.Unspecified,
    val scrim: Color = Color.Unspecified
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getFABSizeConfig(size: SizeVariant): FABSizeConfig {
    return when (size) {
        SizeVariant.Medium -> FABSizeConfig(
            containerSize = HierarchicalSize.Container.Medium, // 48dp
            iconSize = HierarchicalSize.Icon.Medium // 24dp
        )

        SizeVariant.Large -> FABSizeConfig(
            containerSize = HierarchicalSize.Container.Large, // 56dp
            iconSize = HierarchicalSize.Icon.Medium // 24dp
        )

        SizeVariant.Huge -> FABSizeConfig(
            // No HierarchicalSize.Container rung sits at 96dp (Massive is 80dp) —
            // a deliberate one-off for this "hero" FAB tier, kept token-anchored
            // via Container.Massive rather than a bare literal ladder.
            containerSize = HierarchicalSize.Container.Massive + HierarchicalSize.Container.Compact,
            iconSize = HierarchicalSize.Icon.Huge // 36dp
        )

        else -> FABSizeConfig(
            containerSize = HierarchicalSize.Container.Large,
            iconSize = HierarchicalSize.Icon.Medium
        )
    }
}

@Composable
private fun getFABTheme(variant: FABVariant): FABColors {
    val colors = AppTheme.colors
    return when (variant) {
        FABVariant.Filled -> FABColors(
            containerColor = colors.brandSurfaceDefault,
            contentColor = colors.brandContentDefault,
            borderColor = Color.Transparent,
            disabledContainerColor = colors.baseSurfaceDisabled,
            disabledContentColor = colors.baseContentDisabled
        )

        FABVariant.Tonal -> FABColors(
            containerColor = colors.brandSurfaceSubtle,
            contentColor = colors.brandContentDefault,
            borderColor = Color.Transparent,
            disabledContainerColor = colors.baseSurfaceDisabled,
            disabledContentColor = colors.baseContentDisabled
        )

        FABVariant.Outlined -> FABColors(
            containerColor = Color.Transparent,
            contentColor = colors.brandContentDefault,
            borderColor = colors.brandBorderDefault,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colors.baseContentDisabled
        )
    }
}

/**
 * Pill FAB theme, mapped onto [ButtonStateColors] so [PixaFabPill] can hand it
 * straight to [PixaButton]'s `customColors`. Per the LINE design-spec table
 * ("Container: Fill white / Border gray-300", "Icon/Label: black") the pill
 * FAB is a neutral white-surface control, not a brand-filled one — none of
 * [ButtonVariant]'s five brand/error-driven options express that, so this
 * builds a one-off [ButtonStateColors] from `base*` tokens instead of adding a
 * sixth enum case that would only ever be used here.
 */
@Composable
private fun getPillFabTheme(): ButtonStateColors {
    val colors = AppTheme.colors
    return ButtonStateColors(
        default = ButtonColors(
            background = colors.baseSurfaceDefault,
            content = colors.baseContentTitle,
            border = colors.baseBorderDefault,
            ripple = colors.baseContentTitle.copy(alpha = 0.12f)
        ),
        disabled = ButtonColors(
            background = colors.baseSurfaceDisabled,
            content = colors.baseContentDisabled,
            border = colors.baseBorderDisabled,
            ripple = Color.Transparent
        )
    )
}

@Composable
private fun getFabMenuTheme(): FabMenuColors {
    val colors = AppTheme.colors
    return FabMenuColors(
        childContainer = colors.baseSurfaceDefault,
        childIcon = colors.baseContentTitle,
        labelBackground = colors.baseSurfaceDefault,
        labelText = colors.baseContentTitle,
        // Literal, not theme-adaptive — matches the scrim precedent already used by
        // Dialog.kt/Popover.kt/FullScreenModal.kt (Color.Black.copy(alpha = 0.5f)).
        scrim = Color.Black.copy(alpha = 0.5f)
    )
}

private fun FabDockAlignment.toBoxAlignment(): Alignment = when (this) {
    FabDockAlignment.BottomEnd -> Alignment.BottomEnd
    FabDockAlignment.BottomStart -> Alignment.BottomStart
    FabDockAlignment.BottomCenter -> Alignment.BottomCenter
}

private fun FabDockAlignment.toHorizontalAlignment(): Alignment.Horizontal = when (this) {
    FabDockAlignment.BottomEnd -> Alignment.End
    FabDockAlignment.BottomStart -> Alignment.Start
    FabDockAlignment.BottomCenter -> Alignment.CenterHorizontally
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL — EXPANDABLE MENU CHILD ROW
// ════════════════════════════════════════════════════════════════════════════

/**
 * One expandable-menu row: LINE anatomy's optional "Button Label" chip beside
 * the circular "Child Button Container" + "Child Button Icon". Modeled as its
 * own row (not squeezed into a plain icon button) because the label is a
 * separate elevated surface next to the icon, not text inside it.
 */
@Composable
private fun FabChildRow(
    action: FabAction,
    menuColors: FabMenuColors,
    enabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        action.label?.let { label ->
            BasicText(
                text = label,
                style = AppTheme.typography.labelMedium.copy(color = menuColors.labelText),
                modifier = Modifier
                    .elevationShadow(ComponentElevation.Low, AppTheme.shapes.pill)
                    .clip(AppTheme.shapes.pill)
                    .background(menuColors.labelBackground)
                    .padding(
                        horizontal = HierarchicalSize.Padding.Medium,
                        vertical = HierarchicalSize.Padding.Small
                    )
            )
        }

        val childColors = ButtonStateColors(
            default = ButtonColors(
                background = menuColors.childContainer,
                content = menuColors.childIcon,
                ripple = menuColors.childIcon.copy(alpha = 0.12f)
            ),
            disabled = ButtonColors(
                background = AppTheme.colors.baseSurfaceDisabled,
                content = AppTheme.colors.baseContentDisabled
            )
        )

        PixaButton(
            onClick = action.onClick,
            enabled = enabled,
            shape = ButtonShape.Circle,
            // Deliberately smaller than the main FAB's own size ladder — the
            // spec marks child-button size "Unchangeable" without giving an
            // exact value; Button's own Small tier (36dp) is a reasonable
            // child-of-FAB default and doesn't collide with FABSizeConfig.
            size = SizeVariant.Small,
            leadingIcon = action.icon,
            customColors = childColors,
            elevation = ComponentElevation.Low.toDp(),
            description = action.contentDescription ?: action.label
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API — SINGLE-ACTION CIRCLE FAB
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaFAB — single-action circle floating action button; runs its action
 * immediately on tap (LINE: "Single Action Floating Button").
 *
 * ### Anatomy
 * Main Button Container + Icon only — per the source's do/don't guidance,
 * never combine this with inline label text (see [PixaFabPill] for the
 * icon+label, immediate-trigger pattern instead).
 *
 * ### Sizing
 * `Medium` (48dp), `Large` (56dp, default), `Huge` (96dp) — its own ladder,
 * distinct from [PixaButton]'s [SizeVariant] scale.
 *
 * @param icon Required icon painter.
 * @param onClick Required click callback — fires immediately, no menu.
 * @param variant Visual style (default: Filled — brand-filled, per spec's
 *   "Main Button Container: Default ldsg-color-brand-primary").
 */
@Composable
fun PixaFAB(
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Large,
    variant: FABVariant = FABVariant.Filled,
    enabled: Boolean = true,
    colors: FABColors = FABColors(),
    contentDescription: String? = null
) {
    val sizeConfig = getFABSizeConfig(size)
    val themeColors = getFABTheme(variant)

    val containerColor by animateColorAsState(
        targetValue = if (!enabled) themeColors.disabledContainerColor
        else if (colors.containerColor != Color.Unspecified) colors.containerColor
        else themeColors.containerColor,
        label = "fabContainer"
    )

    val contentColor by animateColorAsState(
        targetValue = if (!enabled) themeColors.disabledContentColor
        else if (colors.contentColor != Color.Unspecified) colors.contentColor
        else themeColors.contentColor,
        label = "fabContent"
    )

    val borderColor = if (!enabled) themeColors.disabledContainerColor
    else if (colors.borderColor != Color.Unspecified) colors.borderColor
    else themeColors.borderColor

    val shape: Shape = CircleShape

    Box(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription?.let { this.contentDescription = it }
            }
            .size(sizeConfig.containerSize)
            .elevationShadow(ComponentElevation.High, shape)
            .clip(shape)
            .background(containerColor)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(HierarchicalSize.Border.Compact, borderColor, shape)
                } else Modifier
            )
            .clickable(
                enabled = enabled,
                role = Role.Button,
                indication = pixaRipple(bounded = true, color = contentColor.copy(alpha = 0.12f)),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        PixaIcon(
            painter = icon,
            contentDescription = null,
            customSize = sizeConfig.iconSize,
            tint = contentColor
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API — PILL FAB
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaFabPill — pill-shaped floating action button that triggers its action
 * immediately (LINE: "Pill Floating Button" — "Selecting the button will run
 * the action associated with the button immediately", same trigger semantics
 * as the single-action circle FAB, just pill-shaped with a label).
 *
 * A thin wrapper over [PixaButton] (`shape = ButtonShape.Pill`,
 * `widthPolicy = ButtonWidthPolicy.Flexible` — spec: "Width: Auto Width,
 * Height: Unchangeable Fixed Height") — full reuse of button content, icon,
 * elevation, and interaction internals; only the neutral white/bordered
 * default theme (via [getPillFabTheme]) is FAB-specific.
 *
 * Not the same thing as [PixaExpandableFab]'s menu pattern — this always
 * fires [onClick] directly, never expands.
 *
 * @param label Required visible label (spec: "Button Label").
 * @param icon Optional leading icon (spec marks the icon "Optional").
 */
@Composable
fun PixaFabPill(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    size: SizeVariant = SizeVariant.Medium,
    enabled: Boolean = true,
    customColors: ButtonStateColors? = null,
    contentDescription: String? = null
) {
    PixaButton(
        onClick = onClick,
        modifier = modifier,
        text = label,
        shape = ButtonShape.Pill,
        widthPolicy = ButtonWidthPolicy.Flexible,
        size = size,
        enabled = enabled,
        leadingIcon = icon,
        customColors = customColors ?: getPillFabTheme(),
        elevation = ComponentElevation.High.toDp(),
        description = contentDescription ?: label
    )
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API — EXPANDABLE CIRCLE FAB
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaExpandableFab — circle floating action button for multiple actions
 * (LINE: "Expandable Floating Button" — "Selecting this button will show the
 * sub actions with child buttons").
 *
 * ### States
 * Default and Expanded (both confirmed by source). Selecting the main button
 * toggles between them; in Expanded, child buttons + a dimmer appear and the
 * main icon crossfades to [closeIcon].
 *
 * ### Anatomy
 * Reuses [PixaFAB] for the main button (same circular anatomy/size ladder) and
 * [PixaButton] (`shape = ButtonShape.Circle`) for each child action's icon
 * circle; child labels render as a separate chip beside the icon, never text
 * inside a circle (see [FabChildRow]).
 *
 * ### Overlay ownership
 * This component owns the dimmer/scrim and child menu — they render in a
 * [Popup] anchored to [dockAlignment] so they sit at the "top layer" the spec
 * describes, full-bleed behind the child rows. The main button itself stays
 * caller-positioned exactly like [PixaFAB] (e.g. `Modifier.align(...)` in the
 * caller's own `Box`) — callers should align their own placement to match
 * [dockAlignment] so the menu appears directly above the visible button.
 *
 * ### Usage notes (source do/don't)
 * - Caps at 5 child [actions] — "do not use more than five buttons except for
 *   main button." Extras beyond 5 are silently dropped.
 * - Don't give the main/child icons non-intuitive meanings.
 * - Don't combine this with unrelated extra elements.
 *
 * @param icon Main button icon shown in the Default state.
 * @param closeIcon Main button icon shown in the Expanded state (spec: "the
 *   main button's icon is changed to the close icon").
 * @param actions Child actions shown when expanded; capped at 5.
 * @param dockAlignment Screen corner the scrim/menu Popup anchors to — match
 *   the caller's own placement of this composable (see Overlay ownership).
 */
@Composable
fun PixaExpandableFab(
    icon: Painter,
    closeIcon: Painter,
    actions: List<FabAction>,
    modifier: Modifier = Modifier,
    dockAlignment: FabDockAlignment = FabDockAlignment.BottomEnd,
    size: SizeVariant = SizeVariant.Large,
    variant: FABVariant = FABVariant.Filled,
    enabled: Boolean = true,
    colors: FABColors = FABColors(),
    menuColors: FabMenuColors? = null,
    contentDescription: String? = null,
    expandedContentDescription: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val sizeConfig = getFABSizeConfig(size)
    val effectiveMenuColors = menuColors ?: getFabMenuTheme()
    // "Do not use more than five buttons except for main button."
    val cappedActions = actions.take(5)

    val iconCrossfade by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = AnimationUtils.standardSpring(),
        label = "fabIconCrossfade"
    )

    Box(modifier = modifier) {
        PixaFAB(
            icon = icon,
            onClick = { expanded = !expanded },
            size = size,
            variant = variant,
            enabled = enabled,
            colors = colors,
            contentDescription = if (expanded) expandedContentDescription else contentDescription
        )
        // Crossfade overlay for the close icon — same alpha-driven swap
        // technique the pre-expandable PixaFAB used for its label reveal.
        if (iconCrossfade > 0.01f) {
            Box(
                modifier = Modifier
                    .size(sizeConfig.containerSize)
                    .alpha(iconCrossfade),
                contentAlignment = Alignment.Center
            ) {
                PixaIcon(
                    painter = closeIcon,
                    contentDescription = null,
                    customSize = sizeConfig.iconSize,
                    tint = getFABTheme(variant).contentColor
                )
            }
        }
    }

    if (expanded) {
        Popup(
            alignment = dockAlignment.toBoxAlignment(),
            properties = PopupProperties(focusable = true, dismissOnBackPress = true),
            onDismissRequest = { expanded = false }
        ) {
            AnimatedVisibilityStandard(visible = expanded) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(effectiveMenuColors.scrim)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { expanded = false }
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(dockAlignment.toBoxAlignment())
                            .padding(
                                bottom = sizeConfig.containerSize + HierarchicalSize.Spacing.Large,
                                start = HierarchicalSize.Spacing.Large,
                                end = HierarchicalSize.Spacing.Large
                            ),
                        horizontalAlignment = dockAlignment.toHorizontalAlignment(),
                        verticalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Medium)
                    ) {
                        // Nearest-to-main-button first, reading bottom-to-top.
                        cappedActions.asReversed().forEach { action ->
                            FabChildRow(action = action, menuColors = effectiveMenuColors, enabled = enabled)
                        }
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
fun MiniFAB(
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PixaFAB(
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        size = SizeVariant.Medium
    )
}

@Composable
fun StandardFAB(
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PixaFAB(
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        size = SizeVariant.Large
    )
}
