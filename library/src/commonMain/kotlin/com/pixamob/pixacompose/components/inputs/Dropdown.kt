package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.overlay.MenuContent
import com.pixamob.pixacompose.components.overlay.MenuItem
import com.pixamob.pixacompose.components.overlay.MenuItemType
import com.pixamob.pixacompose.components.overlay.PixaMenuContent
import com.pixamob.pixacompose.components.overlay.getMenuSizeConfig
import com.pixamob.pixacompose.components.overlay.getMenuTheme
import com.pixamob.pixacompose.components.overlay.menuContentItems
import com.pixamob.pixacompose.components.surfaces.PixaSheet
import com.pixamob.pixacompose.components.surfaces.SheetExpandability
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.WindowSizeClass

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Container style of the dropdown *field*.
 *
 * This is a PixaCompose axis shared with [PixaTextField], not eBay's Stacked/Floating axis — that one
 * is about label placement and is a separate concern (see [PixaDropdown]'s docs). [Ghost] is eBay's
 * "borderless" case, which the spec exempts from the minimum-width rule.
 */
enum class DropdownVariant {
    Outlined,
    Filled,
    Ghost
}

/**
 * How the option list is presented.
 *
 * [Adaptive] resolves per [AppTheme.windowSizeClass], following eBay's rule that small screens
 * "launch a sheet or a popover menu" while medium/large screens "disclose a list in a popover menu":
 * [WindowSizeClass.Compact] gets [Sheet], everything else gets [Popover]. [Sheet]/[Popover] force one
 * presentation — an explicit caller choice always wins over the adaptive default.
 *
 * eBay also mentions a fullscreen modal for native apps, but conditions it on "the size and
 * complexity of the list" — a property only the caller knows. It is therefore not offered here; see
 * [PixaDropdown]'s docs.
 */
enum class DropdownPresentation {
    Adaptive,
    Sheet,
    Popover
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Stable
data class DropdownItem<T>(
    val value: T,
    val label: String,
    val icon: Painter? = null,
    val enabled: Boolean = true
)

/**
 * Colors for the dropdown *field* only.
 *
 * The presented option list is a menu surface and owns its own `MenuColors`, so the field no longer
 * carries `menuBackground`/`menuItemHover`/`selectedBackground` — those were describing a layer this
 * component no longer renders itself.
 */
@Immutable
@Stable
data class DropdownColors(
    val background: Color,
    val border: Color,
    val text: Color,
    val placeholder: Color,
    val icon: Color,
    val disabledBackground: Color,
    val disabledText: Color,
    val errorBorder: Color = Color.Transparent,
    val errorText: Color = Color.Transparent
)

/**
 * Per-size configuration for the field.
 *
 * Corner radius, border width, and horizontal padding are container-level constants rather than
 * per-size entries — eBay states one padding value (16px) for all dropdowns, and the field family
 * ([PixaTextField]/[PixaTextArea]) locks one radius so stacked form controls agree.
 */
@Immutable
@Stable
private data class DropdownSizeConfig(
    val height: Dp,
    val iconSize: Dp,
    val textStyle: TextStyle
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/** eBay: "Left/right content padding: 16px." One value for every dropdown, so not a per-size entry. */
private val FieldHorizontalPadding = HierarchicalSize.Spacing.Large

/** eBay: "Label-to-dropdown padding: 4px." */
private val LabelGap = HierarchicalSize.Spacing.Compact

/** eBay: "Dropdown-to-helper-text padding: 8px" and "Placeholder-to-icon padding: 8px." */
private val HelperGap = HierarchicalSize.Spacing.Small
private val ValueToIconGap = HierarchicalSize.Spacing.Small

/** Border weight is NOT COVERED by the eBay spec; matches [PixaTextField]'s resting field border. */
private val FieldBorderWidth = HierarchicalSize.Border.Compact

/**
 * eBay: "Minimum width: 2× height for bordered dropdowns." A spec-derived multiplier rather than a
 * magic number — kept as math against the resolved height so it holds across every size tier.
 */
private const val BorderedMinWidthMultiplier = 2

@Composable
private fun getDropdownTheme(variant: DropdownVariant): DropdownColors {
    val colors = AppTheme.colors
    val shared = DropdownColors(
        background = colors.baseSurfaceDefault,
        border = colors.baseBorderDefault,
        text = colors.baseContentTitle,
        placeholder = colors.baseContentHint,
        icon = colors.baseContentBody,
        disabledBackground = colors.baseSurfaceDisabled,
        disabledText = colors.baseContentDisabled,
        errorBorder = colors.errorBorderDefault,
        errorText = colors.errorContentDefault
    )
    return when (variant) {
        DropdownVariant.Outlined -> shared
        DropdownVariant.Filled -> shared.copy(
            background = colors.baseSurfaceSubtle,
            border = Color.Transparent
        )
        DropdownVariant.Ghost -> shared.copy(
            background = Color.Transparent,
            border = Color.Transparent,
            disabledBackground = Color.Transparent
        )
    }
}

/**
 * eBay: "There are two sizes available: 40px and 48px" — those land exactly on
 * [HierarchicalSize.Input]'s Small/Medium tiers, which [PixaTextField] already uses, so the dropdown
 * shares the field ladder instead of its own raw one. Large (56dp) extends the ladder for parity with
 * the rest of the input family.
 */
@Composable
private fun getDropdownSizeConfig(size: SizeVariant): DropdownSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SizeVariant.Small -> DropdownSizeConfig(
            height = HierarchicalSize.Input.Small,
            iconSize = HierarchicalSize.Icon.Small,
            textStyle = typography.bodyRegular
        )

        SizeVariant.Large -> DropdownSizeConfig(
            height = HierarchicalSize.Input.Large,
            iconSize = HierarchicalSize.Icon.Medium,
            textStyle = typography.bodyBold
        )

        // Medium is the anchor tier and the fallback for tiers this component doesn't scale to.
        else -> DropdownSizeConfig(
            height = HierarchicalSize.Input.Medium,
            iconSize = HierarchicalSize.Icon.Small,
            textStyle = typography.bodyRegular
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL DROPDOWN
// ════════════════════════════════════════════════════════════════════════════

/**
 * Projects the dropdown's options onto the menu surface's content model, so the presented list gets
 * Uber Base's item anatomy and its Active/Disabled states rather than a parallel implementation.
 * Identity travels as the option's index, which keeps `T` free of any `id`/equality requirement.
 */
private fun <T> List<DropdownItem<T>>.toMenuContent(selectedItem: T?): List<MenuContent> =
    mapIndexed { index, item ->
        MenuContent.Item(
            MenuItem(
                id = index.toString(),
                title = item.label,
                icon = item.icon,
                type = if (item.enabled) MenuItemType.Default else MenuItemType.Disabled,
                // Spec's "Active" state — "indicates selected option from option groups".
                selected = item.value == selectedItem
            )
        )
    }

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaDropdown — "allow for selection of a value within a predetermined dataset." The field follows
 * eBay Playbook's Dropdown spec; the presented option list is Uber Base's Menu surface, reused via
 * `PixaMenuContent`/`menuContentItems` rather than reimplemented here.
 *
 * ### Anatomy
 * Two layers, deliberately kept separate. The **field**: [label] (+ [required] asterisk) → container
 * (optional [leadingIcon] → selected value or [placeholder] → trailing chevron) → [helperText] /
 * [errorText]. The **option surface**: a menu presented per [presentation], owning its own chrome,
 * sizing, and colors.
 *
 * ### Adaptive behavior
 * [DropdownPresentation.Adaptive] (the default) reads [AppTheme.windowSizeClass]: compact screens get
 * a `PixaSheet`, larger screens an anchored popover menu — eBay's "sheet or popover on small screens,
 * popover menu on web". A caller may pin either presentation explicitly.
 *
 * eBay also allows a fullscreen modal on native "determined by size and complexity of the list".
 * That is deliberately not offered: the component cannot infer a list's complexity, and a modal is a
 * far heavier interruption than a form field warrants. A caller who genuinely needs one should drive
 * `PixaFullScreenModal` directly rather than have a dropdown silently escalate into it.
 *
 * ### States
 * Enabled/disabled ("users cannot focus on or change disabled fields"), [isError] (where [errorText]
 * replaces [helperText], per spec), and expanded. Selection is single — the spec does not cover
 * multi-select.
 *
 * ### Sizing
 * [size] resolves the field height off the shared input ladder (eBay's 40px/48px are Small/Medium).
 * Bordered variants take the spec's "minimum width: 2× height"; [DropdownVariant.Ghost] is the
 * "borderless" case the spec exempts. An overlong value truncates before the icon, per spec.
 *
 * ### Usage notes
 * Content rules the spec states, left to the author: labels are sentence case, 1–3 words, no ending
 * punctuation; options are "short, distinct, and easy to scan"; error text is neutral and jargon-free.
 * eBay's Stacked/Floating label variants are not modelled — this renders the Stacked case only, and a
 * floating label is a cross-field concern that would belong to the whole input family at once, not to
 * the dropdown alone.
 *
 * @param items Options to choose from
 * @param selectedItem Currently selected value
 * @param onItemSelected Callback when an option is selected
 * @param modifier Modifier for the field
 * @param placeholder Placeholder shown until a value is selected
 * @param variant Field container style
 * @param size Size preset (eBay's 40px/48px map to Small/Medium)
 * @param presentation How the option list is presented (Default: [DropdownPresentation.Adaptive])
 * @param colors Custom *field* colors; the option surface is themed by the menu layer
 * @param leadingIcon Optional leading icon in the field
 * @param trailingIcon Trailing chevron; rotates 180° while expanded
 * @param enabled Whether the dropdown is interactive
 * @param label Field label — the spec treats this as required
 * @param isError Whether to show the error state
 * @param errorText Error message; replaces [helperText] when [isError] is true
 * @param helperText Helper text below the field
 * @param required Whether to show the required asterisk
 * @param sheetTitle Title for the compact-screen sheet (Default: [label], else [placeholder])
 */
@Composable
fun <T> PixaDropdown(
    items: List<DropdownItem<T>>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select...",
    variant: DropdownVariant = DropdownVariant.Outlined,
    size: SizeVariant = SizeVariant.Medium,
    presentation: DropdownPresentation = DropdownPresentation.Adaptive,
    colors: DropdownColors? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    enabled: Boolean = true,
    label: String? = null,
    isError: Boolean = false,
    errorText: String? = null,
    helperText: String? = null,
    required: Boolean = false,
    sheetTitle: String? = null
) {
    val themeColors = colors ?: getDropdownTheme(variant)
    val sizeConfig = getDropdownSizeConfig(size)
    val shape = AppTheme.shapes.rounded.medium

    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = items.find { it.value == selectedItem }?.label

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = AnimationUtils.fastTween()
    )

    val textColor = when {
        !enabled -> themeColors.disabledText
        selectedLabel != null -> themeColors.text
        else -> themeColors.placeholder
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            !enabled -> if (variant == DropdownVariant.Outlined) {
                AppTheme.colors.baseBorderDisabled
            } else {
                Color.Transparent
            }
            isError -> themeColors.errorBorder
            else -> themeColors.border
        },
        animationSpec = AnimationUtils.smoothSpring()
    )

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (enabled) themeColors.background else themeColors.disabledBackground,
        animationSpec = AnimationUtils.smoothSpring()
    )

    // Adaptive resolution — an explicit caller choice always wins over the window size class.
    val resolvedPresentation = when (presentation) {
        DropdownPresentation.Adaptive ->
            if (AppTheme.windowSizeClass == WindowSizeClass.Compact) {
                DropdownPresentation.Sheet
            } else {
                DropdownPresentation.Popover
            }

        else -> presentation
    }

    val menuContent = items.toMenuContent(selectedItem)
    val onMenuItemClick: (MenuItem) -> Unit = { menuItem ->
        items.getOrNull(menuItem.id.toIntOrNull() ?: -1)
            ?.takeIf { it.enabled }
            ?.let { onItemSelected(it.value) }
        expanded = false
    }

    // eBay: bordered dropdowns take a minimum width of 2× their height; borderless ones don't.
    val minWidthModifier = if (variant == DropdownVariant.Ghost) {
        Modifier
    } else {
        Modifier.widthIn(min = sizeConfig.height * BorderedMinWidthMultiplier)
    }

    Column(modifier = modifier) {
        if (label != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = LabelGap)
            ) {
                BasicText(
                    text = label,
                    style = AppTheme.typography.labelMedium.copy(
                        color = if (isError) themeColors.errorText else themeColors.text
                    )
                )
                if (required) {
                    BasicText(
                        text = " *",
                        style = AppTheme.typography.labelMedium.copy(
                            color = AppTheme.colors.errorContentDefault
                        )
                    )
                }
            }
        }

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(minWidthModifier)
                    .height(sizeConfig.height)
                    .clip(shape)
                    .background(animatedBackgroundColor)
                    .then(
                        if (animatedBorderColor != Color.Transparent) {
                            Modifier.border(FieldBorderWidth, animatedBorderColor, shape)
                        } else Modifier
                    )
                    .clickable(
                        enabled = enabled,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        role = Role.DropdownList,
                        onClick = { expanded = !expanded }
                    )
                    .padding(horizontal = FieldHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (leadingIcon != null) {
                        PixaIcon(
                            painter = leadingIcon,
                            contentDescription = null,
                            tint = themeColors.icon,
                            modifier = Modifier
                                .size(sizeConfig.iconSize)
                                .padding(end = ValueToIconGap)
                        )
                    }
                    // Spec: content overflow is truncated before the icon.
                    BasicText(
                        text = selectedLabel ?: placeholder,
                        style = sizeConfig.textStyle.copy(color = textColor),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (trailingIcon != null) {
                    PixaIcon(
                        painter = trailingIcon,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = themeColors.icon,
                        modifier = Modifier
                            .padding(start = ValueToIconGap)
                            .size(sizeConfig.iconSize)
                            .rotate(rotation)
                    )
                }
            }

            // Larger screens: the option list is the menu surface, anchored under the field.
            if (resolvedPresentation == DropdownPresentation.Popover) {
                PixaMenuContent(
                    visible = expanded,
                    onDismiss = { expanded = false },
                    content = menuContent,
                    onItemClick = onMenuItemClick,
                    alignment = Alignment.BottomStart
                )
            }
        }

        // Spec: the error message replaces any existing helper text.
        val bottomText = if (isError && errorText != null) errorText else helperText
        if (bottomText != null) {
            BasicText(
                text = bottomText,
                style = AppTheme.typography.captionRegular.copy(
                    color = if (isError) themeColors.errorText else themeColors.text
                ),
                modifier = Modifier.padding(top = HelperGap, start = FieldHorizontalPadding)
            )
        }
    }

    // Compact screens: the same menu rows, hosted by a sheet instead of a popover. The sheet supplies
    // the surface, so only the row list is reused — not the menu's own popup/elevated container.
    if (expanded && resolvedPresentation == DropdownPresentation.Sheet) {
        val menuColors = getMenuTheme()
        val menuSizeConfig = getMenuSizeConfig()
        PixaSheet(
            onDismissRequest = { expanded = false },
            title = sheetTitle ?: label ?: placeholder,
            expandability = SheetExpandability.Fixed
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                menuContentItems(
                    content = menuContent,
                    onItemClick = onMenuItemClick,
                    onDismiss = { expanded = false },
                    themeColors = menuColors,
                    sizeConfig = menuSizeConfig
                )
            }
        }
    }
}
