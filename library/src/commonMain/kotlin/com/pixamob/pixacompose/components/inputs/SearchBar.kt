package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils
import com.pixamob.pixacompose.utils.ComponentElevation
import com.pixamob.pixacompose.utils.elevationShadow

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class SearchBarVariant {
    Filled,
    Outlined,
    Elevated
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Per-size configuration.
 *
 * Container-level constants live outside this class ([HorizontalPadding],
 * [DefaultBorderWidth], [FocusedBorderWidth], width/touch-target floors) —
 * per the eBay Playbook source, only visible height, icon size and type
 * scale vary per size tier.
 */
@Immutable
@Stable
private data class SearchBarSizeConfig(
    val height: Dp,
    val textStyle: TextStyle,
    val suggestionTextStyle: TextStyle,
    val iconSize: Dp,
    val shape: androidx.compose.ui.graphics.Shape
)

@Immutable
@Stable
data class SearchBarColors(
    val background: Color,
    val border: Color,
    val focusedBorder: Color,
    val text: Color,
    val placeholder: Color,
    val iconTint: Color,
    val suggestionBackground: Color,
    val suggestionHover: Color,
    val suggestionText: Color,
    val divider: Color
)

data class SearchSuggestion(
    val text: String,
    val icon: Painter? = null,
    val metadata: String? = null,
    val isRecent: Boolean = false
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

/** 1px default border, container-level constant per the eBay Playbook source (not per-size). */
private val DefaultBorderWidth = HierarchicalSize.Border.Compact

/** 2px focused border. */
private val FocusedBorderWidth = HierarchicalSize.Border.Medium

/** Standard, uniform horizontal padding — a container-level constant, not per-size. */
private val HorizontalPadding = HierarchicalSize.Spacing.Medium

/**
 * eBay Playbook: "Search fields have a maximum width of 480px and a minimum
 * width of 200px." Uniform across all sizes. No existing [HierarchicalSize]
 * category models a component-level width envelope, and this rule is
 * specific to this one component, so it's kept as a local, spec-cited
 * constant rather than a new token family.
 */
private val SearchFieldMinWidth = 200.dp
private val SearchFieldMaxWidth = 480.dp

/**
 * eBay Playbook: "the tap target is 48px across all sizes" — the interactive
 * region never shrinks below this, even at the [SizeVariant.Small] visual
 * height (32dp). Maps exactly onto [HierarchicalSize.TouchTarget.Small],
 * the library's own WCAG-minimum rung.
 */
private val TouchTargetFloor = HierarchicalSize.TouchTarget.Small

/**
 * Visual height per size. The eBay Playbook source confirms three named
 * tiers (small/medium/large, small default) and three heights (48/40/32px)
 * but never states the tier→height pairing in text — only an unlabeled
 * screenshot implies it. Mapped here in ascending order (Small < Medium <
 * Large) to match every other Pixa size ladder; [SizeVariant.Large] lands
 * exactly on the 48px tap target with no invisible padding, [Small]/[Medium]
 * get their invisible padding from [TouchTargetFloor]. Flagged as an
 * assumption, not a confirmed rule.
 */
@Composable
private fun getSearchBarSizeConfig(size: SizeVariant): SearchBarSizeConfig {
    val typography = AppTheme.typography
    val shape = AppTheme.shapes.rounded.forVariant(size)
    return when (size) {
        SizeVariant.Small -> SearchBarSizeConfig(
            height = HierarchicalSize.Input.Compact,
            textStyle = typography.bodyLight,
            suggestionTextStyle = typography.bodyLight,
            iconSize = HierarchicalSize.Icon.Small,
            shape = shape
        )
        SizeVariant.Medium -> SearchBarSizeConfig(
            height = HierarchicalSize.Input.Small,
            textStyle = typography.bodyRegular,
            suggestionTextStyle = typography.bodyRegular,
            iconSize = HierarchicalSize.Icon.Medium,
            shape = shape
        )
        SizeVariant.Large -> SearchBarSizeConfig(
            height = HierarchicalSize.Input.Medium,
            textStyle = typography.bodyBold,
            suggestionTextStyle = typography.bodyBold,
            iconSize = HierarchicalSize.Icon.Large,
            shape = shape
        )
        else -> SearchBarSizeConfig(
            height = HierarchicalSize.Input.Small,
            textStyle = typography.bodyRegular,
            suggestionTextStyle = typography.bodyRegular,
            iconSize = HierarchicalSize.Icon.Medium,
            shape = shape
        )
    }
}

@Composable
private fun getSearchBarTheme(
    variant: SearchBarVariant,
    isFocused: Boolean,
    enabled: Boolean
): SearchBarColors {
    val colors = AppTheme.colors
    return when (variant) {
        SearchBarVariant.Filled -> SearchBarColors(
            background = if (enabled) colors.baseSurfaceDefault else colors.baseSurfaceDisabled,
            border = Color.Transparent,
            focusedBorder = colors.brandBorderDefault.copy(alpha = 0.2f),
            text = if (enabled) colors.baseContentTitle else colors.baseContentDisabled,
            placeholder = colors.baseContentBody.copy(alpha = 0.5f),
            iconTint = if (isFocused) colors.brandContentDefault else colors.baseContentBody,
            suggestionBackground = colors.baseSurfaceDefault,
            suggestionHover = colors.baseSurfaceElevated,
            suggestionText = colors.baseContentTitle,
            divider = colors.baseBorderDefault
        )
        SearchBarVariant.Outlined -> SearchBarColors(
            background = Color.Transparent,
            border = colors.baseBorderDefault,
            focusedBorder = colors.brandBorderDefault,
            text = if (enabled) colors.baseContentTitle else colors.baseContentDisabled,
            placeholder = colors.baseContentBody.copy(alpha = 0.5f),
            iconTint = if (isFocused) colors.brandContentDefault else colors.baseContentBody,
            suggestionBackground = colors.baseSurfaceDefault,
            suggestionHover = colors.baseSurfaceElevated,
            suggestionText = colors.baseContentTitle,
            divider = colors.baseBorderDefault
        )
        SearchBarVariant.Elevated -> SearchBarColors(
            background = colors.baseSurfaceDefault,
            border = Color.Transparent,
            focusedBorder = Color.Transparent,
            text = if (enabled) colors.baseContentTitle else colors.baseContentDisabled,
            placeholder = colors.baseContentBody.copy(alpha = 0.5f),
            iconTint = if (isFocused) colors.brandContentDefault else colors.baseContentBody,
            suggestionBackground = colors.baseSurfaceDefault,
            suggestionHover = colors.baseSurfaceElevated,
            suggestionText = colors.baseContentTitle,
            divider = colors.baseBorderDefault
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL SUGGESTION DROPDOWN
// ════════════════════════════════════════════════════════════════════════════

/** Pixa-native addition, not part of the eBay Playbook anatomy — see [PixaSearchBar] KDoc. */
@Composable
private fun SuggestionDropdown(
    suggestions: List<SearchSuggestion>,
    sizeConfig: SearchBarSizeConfig,
    colors: SearchBarColors,
    onSuggestionClick: (SearchSuggestion) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .elevationShadow(ComponentElevation.High, sizeConfig.shape)
            .clip(sizeConfig.shape)
            .background(colors.suggestionBackground)
            .border(width = DefaultBorderWidth, color = colors.divider, shape = sizeConfig.shape)
    ) {
        suggestions.forEachIndexed { index, suggestion ->
            SuggestionItem(
                suggestion = suggestion,
                sizeConfig = sizeConfig,
                colors = colors,
                onClick = { onSuggestionClick(suggestion) }
            )

            if (index < suggestions.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HierarchicalSize.Divider.Compact)
                        .background(colors.divider)
                )
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: SearchSuggestion,
    sizeConfig: SearchBarSizeConfig,
    colors: SearchBarColors,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .background(if (isHovered) colors.suggestionHover else Color.Transparent)
            .padding(horizontal = HorizontalPadding, vertical = HierarchicalSize.Spacing.Small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
    ) {
        if (suggestion.icon != null) {
            PixaIcon(
                painter = suggestion.icon,
                contentDescription = null,
                tint = colors.iconTint,
                modifier = Modifier.size(sizeConfig.iconSize)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            BasicText(text = suggestion.text, style = sizeConfig.suggestionTextStyle.copy(color = colors.suggestionText))

            if (suggestion.metadata != null) {
                BasicText(
                    text = suggestion.metadata,
                    style = AppTheme.typography.captionRegular.copy(color = colors.suggestionText.copy(alpha = 0.6f))
                )
            }
        }

        if (suggestion.isRecent) {
            BasicText(
                text = "Recent",
                style = AppTheme.typography.overline.copy(color = colors.suggestionText.copy(alpha = 0.5f))
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaSearchBar — filters a list using characters typed into the field, per
 * the [eBay Playbook Search Field](https://playbook.ebay.com/design-system/components/search-field).
 *
 * ### Anatomy
 * leading [searchIcon] → [placeholder]/input text → trailing accessory
 * ([trailingAccessoryIcon], swapped for a clear glyph once focused with
 * text). The leading icon is static — never hide it, even once text is
 * entered.
 *
 * ### Clear-button replacement
 * The trailing accessory is replaced by a clear affordance only while the
 * field **is focused and holds text** — unfocus (even with text still in
 * the field) reverts the trailing slot back to [trailingAccessoryIcon].
 * Set [showClearButton] to `false` to opt out entirely.
 *
 * ### Real-time filtering
 * [value]/[onValueChange] are the only contract for filtering — wire
 * `onValueChange` to filter your own list on every keystroke. The built-in
 * [suggestions] dropdown (typeahead, not part of the source spec) is a
 * separate, opt-in Pixa addition layered on top; it doesn't replace the
 * list-filtering pattern above.
 *
 * ### Sizing
 * [size] resolves visual height via [HierarchicalSize.Input] — Small=32dp,
 * Medium=40dp, Large=48dp (exact px-to-tier mapping not textually confirmed
 * by the source; see file comment on [getSearchBarSizeConfig]). The
 * interactive tap target never drops below 48dp regardless of visual
 * height, matching the source's "48px tap target across all sizes."
 * `small` is the source-confirmed default, which intentionally differs from
 * every sibling input's `Medium` default.
 *
 * ### Width
 * Always constrained to 200–480dp per the source ("minimum width of 200px"
 * / "maximum width of 480px"), uniform across sizes. On phone-width
 * screens this alone reproduces the source's "full width on small screens"
 * guidance; keeping the field near the top of the page and scrolling it
 * into view on focus is a screen-composition concern outside this
 * component's scope.
 *
 * ### Usage notes
 * - Place a cancel/back button next to the field yourself (e.g. a
 *   [com.pixamob.pixacompose.components.actions.PixaIconButton] in the
 *   same `Row`) when the field lives in a dedicated search view — not part
 *   of this component's anatomy.
 * - Likewise, a "search" button that triggers a server request belongs
 *   *after* the field as a sibling, not inside it; [onSearch] already
 *   covers the IME "search" key for that trigger.
 *
 * @param value Current search query
 * @param onValueChange Callback when query changes
 * @param modifier Modifier for the search bar
 * @param variant Visual style variant (Filled, Outlined, Elevated) — a Pixa styling axis, not defined by the source
 * @param size Size preset (Small default, Medium, Large)
 * @param enabled Whether the search bar is enabled
 * @param colors Custom colors (null = use theme)
 * @param placeholder Placeholder text describing what's being filtered
 * @param searchIcon Leading search icon — keep this supplied and visible; never conditionally hide it
 * @param trailingAccessoryIcon Default trailing accessory (e.g. camera/voice/filter), shown when not overridden by the clear affordance
 * @param onTrailingAccessoryClick Callback for [trailingAccessoryIcon]
 * @param trailingAccessoryContentDescription Accessibility label for [trailingAccessoryIcon]
 * @param showClearButton Whether the focused+non-empty clear affordance is enabled at all
 * @param onClear Callback when the clear affordance is used (in addition to clearing [value])
 * @param onSearch Callback when the IME "search" action is triggered
 * @param suggestions Typeahead suggestions (Pixa addition, not sourced from the spec)
 * @param showSuggestions Whether to show the suggestions dropdown
 * @param onSuggestionClick Callback when a suggestion is tapped
 * @param filterSuggestions Whether [suggestions] are filtered against [value] client-side
 * @param maxSuggestions Cap on suggestions shown
 * @param keyboardOptions Keyboard configuration
 * @param keyboardActions Keyboard actions
 * @param interactionSource Interaction source for state
 * @param contentDescription Accessibility description for the field
 */
@Composable
fun PixaSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: SearchBarVariant = SearchBarVariant.Filled,
    size: SizeVariant = SizeVariant.Small,
    enabled: Boolean = true,
    colors: SearchBarColors? = null,
    placeholder: String = "Search...",
    searchIcon: Painter? = null,
    trailingAccessoryIcon: Painter? = null,
    onTrailingAccessoryClick: (() -> Unit)? = null,
    trailingAccessoryContentDescription: String = "Search accessory",
    showClearButton: Boolean = true,
    onClear: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    suggestions: List<SearchSuggestion> = emptyList(),
    showSuggestions: Boolean = false,
    onSuggestionClick: ((SearchSuggestion) -> Unit)? = null,
    filterSuggestions: Boolean = true,
    maxSuggestions: Int = 5,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contentDescription: String? = null
) {
    val sizeConfig = getSearchBarSizeConfig(size)
    val isFocused by interactionSource.collectIsFocusedAsState()
    val themeColors = colors ?: getSearchBarTheme(variant, isFocused, enabled)

    // Source: "replaced with a clear button by default when the field is focused
    // and receives one or more characters." Unfocusing (even with text present)
    // reverts to the default trailing accessory.
    val showClear = showClearButton && isFocused && value.isNotEmpty()
    val effectiveHeight = maxOf(sizeConfig.height, TouchTargetFloor)

    val filteredSuggestions = remember(value, suggestions, filterSuggestions) {
        if (filterSuggestions && value.isNotEmpty()) {
            suggestions.filter { it.text.contains(value, ignoreCase = true) }.take(maxSuggestions)
        } else {
            suggestions.take(maxSuggestions)
        }
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused) themeColors.focusedBorder else themeColors.border,
        animationSpec = AnimationUtils.standardTween(200)
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isFocused) FocusedBorderWidth else DefaultBorderWidth,
        animationSpec = AnimationUtils.standardTween(200)
    )

    Box(
        modifier = modifier
            .widthIn(min = SearchFieldMinWidth, max = SearchFieldMaxWidth)
            .semantics {
                contentDescription?.let { this.contentDescription = it }
            }
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = TouchTargetFloor),
            enabled = enabled,
            textStyle = sizeConfig.textStyle.copy(color = themeColors.text),
            keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke() }),
            singleLine = true,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(themeColors.iconTint),
            decorationBox = { innerTextField ->
                // Visual chrome paints at the size-driven height; the surrounding
                // Box (already ≥48dp via heightIn above) centers it so the touch
                // target can exceed the painted field at Small/Medium sizes.
                Box(
                    modifier = Modifier.fillMaxWidth().heightIn(min = TouchTargetFloor),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(sizeConfig.height)
                            .clip(sizeConfig.shape)
                            .then(
                                if (variant == SearchBarVariant.Elevated) {
                                    Modifier.elevationShadow(ComponentElevation.Low, sizeConfig.shape)
                                } else Modifier
                            )
                            .background(themeColors.background)
                            .then(
                                if (animatedBorderColor != Color.Transparent) {
                                    Modifier.border(width = animatedBorderWidth, color = animatedBorderColor, shape = sizeConfig.shape)
                                } else Modifier
                            )
                            .padding(horizontal = HorizontalPadding),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
                        ) {
                            // Leading search icon — static affordance, always visible per the source
                            // ("Don't hide the search icon even when text is entered").
                            if (searchIcon != null) {
                                PixaIcon(
                                    painter = searchIcon,
                                    contentDescription = "Search",
                                    tint = themeColors.iconTint,
                                    modifier = Modifier.size(sizeConfig.iconSize)
                                )
                            }

                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                if (value.isEmpty()) {
                                    BasicText(text = placeholder, style = sizeConfig.textStyle.copy(color = themeColors.placeholder))
                                }
                                innerTextField()
                            }

                            // Trailing slot: clear affordance takes priority while focused+non-empty,
                            // otherwise the default trailing accessory (if any) is shown.
                            if (showClear) {
                                Box(
                                    modifier = Modifier
                                        .size(sizeConfig.iconSize)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            onValueChange("")
                                            onClear?.invoke()
                                        }
                                        .semantics { this.contentDescription = "Clear search" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    BasicText(text = "✕", style = AppTheme.typography.bodyRegular.copy(color = themeColors.iconTint))
                                }
                            } else if (trailingAccessoryIcon != null) {
                                PixaIcon(
                                    painter = trailingAccessoryIcon,
                                    contentDescription = trailingAccessoryContentDescription,
                                    tint = themeColors.iconTint,
                                    modifier = Modifier
                                        .size(sizeConfig.iconSize)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            enabled = onTrailingAccessoryClick != null
                                        ) { onTrailingAccessoryClick?.invoke() }
                                )
                            }
                        }
                    }
                }
            }
        )

        if (showSuggestions && filteredSuggestions.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, effectiveHeight.value.toInt()),
                properties = PopupProperties(focusable = false)
            ) {
                SuggestionDropdown(
                    suggestions = filteredSuggestions,
                    sizeConfig = sizeConfig,
                    colors = themeColors,
                    onSuggestionClick = { suggestion ->
                        onSuggestionClick?.invoke(suggestion)
                        onValueChange(suggestion.text)
                    }
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONVENIENCE VARIANTS
// ════════════════════════════════════════════════════════════════════════════

/** FilledSearchBar — filled background variant. */
@Composable
fun FilledSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Small,
    enabled: Boolean = true,
    placeholder: String = "Search...",
    searchIcon: Painter? = null,
    trailingAccessoryIcon: Painter? = null,
    onTrailingAccessoryClick: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    suggestions: List<SearchSuggestion> = emptyList(),
    showSuggestions: Boolean = false,
    onSuggestionClick: ((SearchSuggestion) -> Unit)? = null
) {
    PixaSearchBar(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = SearchBarVariant.Filled,
        size = size,
        enabled = enabled,
        placeholder = placeholder,
        searchIcon = searchIcon,
        trailingAccessoryIcon = trailingAccessoryIcon,
        onTrailingAccessoryClick = onTrailingAccessoryClick,
        onSearch = onSearch,
        suggestions = suggestions,
        showSuggestions = showSuggestions,
        onSuggestionClick = onSuggestionClick
    )
}

/** OutlinedSearchBar — outlined border variant. */
@Composable
fun OutlinedSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Small,
    enabled: Boolean = true,
    placeholder: String = "Search...",
    searchIcon: Painter? = null,
    trailingAccessoryIcon: Painter? = null,
    onTrailingAccessoryClick: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    suggestions: List<SearchSuggestion> = emptyList(),
    showSuggestions: Boolean = false,
    onSuggestionClick: ((SearchSuggestion) -> Unit)? = null
) {
    PixaSearchBar(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = SearchBarVariant.Outlined,
        size = size,
        enabled = enabled,
        placeholder = placeholder,
        searchIcon = searchIcon,
        trailingAccessoryIcon = trailingAccessoryIcon,
        onTrailingAccessoryClick = onTrailingAccessoryClick,
        onSearch = onSearch,
        suggestions = suggestions,
        showSuggestions = showSuggestions,
        onSuggestionClick = onSuggestionClick
    )
}

/** ElevatedSearchBar — elevated with shadow variant. */
@Composable
fun ElevatedSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: SizeVariant = SizeVariant.Small,
    enabled: Boolean = true,
    placeholder: String = "Search...",
    searchIcon: Painter? = null,
    trailingAccessoryIcon: Painter? = null,
    onTrailingAccessoryClick: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    suggestions: List<SearchSuggestion> = emptyList(),
    showSuggestions: Boolean = false,
    onSuggestionClick: ((SearchSuggestion) -> Unit)? = null
) {
    PixaSearchBar(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = SearchBarVariant.Elevated,
        size = size,
        enabled = enabled,
        placeholder = placeholder,
        searchIcon = searchIcon,
        trailingAccessoryIcon = trailingAccessoryIcon,
        onTrailingAccessoryClick = onTrailingAccessoryClick,
        onSearch = onSearch,
        suggestions = suggestions,
        showSuggestions = showSuggestions,
        onSuggestionClick = onSuggestionClick
    )
}

// ════════════════════════════════════════════════════════════════════════════
// Specialized Convenience Functions
// ════════════════════════════════════════════════════════════════════════════

/** ProductSearchBar — pre-configured for product search. */
@Composable
fun ProductSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: SearchBarVariant = SearchBarVariant.Filled,
    placeholder: String = "Search products...",
    suggestions: List<SearchSuggestion> = emptyList(),
    onSearch: (() -> Unit)? = null,
    onSuggestionClick: ((SearchSuggestion) -> Unit)? = null
) {
    PixaSearchBar(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        placeholder = placeholder,
        suggestions = suggestions,
        showSuggestions = value.length >= 2,
        onSearch = onSearch,
        onSuggestionClick = onSuggestionClick,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = KeyboardType.Text
        )
    )
}

/** LocationSearchBar — pre-configured for location search. */
@Composable
fun LocationSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: SearchBarVariant = SearchBarVariant.Outlined,
    placeholder: String = "Search location...",
    suggestions: List<SearchSuggestion> = emptyList(),
    onSearch: (() -> Unit)? = null,
    onSuggestionClick: ((SearchSuggestion) -> Unit)? = null
) {
    PixaSearchBar(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        placeholder = placeholder,
        suggestions = suggestions,
        showSuggestions = value.length >= 3,
        onSearch = onSearch,
        onSuggestionClick = onSuggestionClick,
        filterSuggestions = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = KeyboardType.Text
        )
    )
}

/** ContactSearchBar — pre-configured for contact search. */
@Composable
fun ContactSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: SearchBarVariant = SearchBarVariant.Filled,
    placeholder: String = "Search contacts...",
    suggestions: List<SearchSuggestion> = emptyList(),
    onSuggestionClick: ((SearchSuggestion) -> Unit)? = null
) {
    PixaSearchBar(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = variant,
        placeholder = placeholder,
        suggestions = suggestions,
        showSuggestions = value.isNotEmpty(),
        onSuggestionClick = onSuggestionClick,
        filterSuggestions = true,
        maxSuggestions = 8,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = KeyboardType.Text
        )
    )
}
