package com.pixamob.pixacompose.components.inputs

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class SearchBarVariant {
    Filled,
    Outlined,
    Elevated
}

enum class SearchBarSize {
    Small,
    Medium,
    Large
}

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class SearchBarSizeConfig(
    val height: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val textStyle: TextStyle,
    val suggestionTextStyle: TextStyle,
    val iconSize: Dp,
    val borderWidth: Dp,
    val cornerRadius: Dp,
    val elevation: Dp
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

@Composable
private fun getSearchBarSizeConfig(size: SearchBarSize): SearchBarSizeConfig {
    val typography = AppTheme.typography
    return when (size) {
        SearchBarSize.Small -> SearchBarSizeConfig(
            height = ComponentSize.InputSmall,
            horizontalPadding = HierarchicalSize.Spacing.Medium,
            verticalPadding = HierarchicalSize.Spacing.Compact,
            textStyle = typography.bodyLight,
            suggestionTextStyle = typography.bodyLight,
            iconSize = IconSize.Small,
            borderWidth = BorderWidth.Thin,
            cornerRadius = CornerRadius.Small,
            elevation = Elevation.Small
        )
        SearchBarSize.Medium -> SearchBarSizeConfig(
            height = ComponentSize.InputMedium,
            horizontalPadding = HierarchicalSize.Spacing.Large,
            verticalPadding = HierarchicalSize.Spacing.Small,
            textStyle = typography.bodyRegular,
            suggestionTextStyle = typography.bodyRegular,
            iconSize = IconSize.Medium,
            borderWidth = BorderWidth.Medium,
            cornerRadius = CornerRadius.Medium,
            elevation = Elevation.Medium
        )
        SearchBarSize.Large -> SearchBarSizeConfig(
            height = ComponentSize.InputLarge,
            horizontalPadding = HierarchicalSize.Spacing.Huge,
            verticalPadding = HierarchicalSize.Spacing.Medium,
            textStyle = typography.bodyBold,
            suggestionTextStyle = typography.bodyBold,
            iconSize = IconSize.Large,
            borderWidth = BorderWidth.Thick,
            cornerRadius = CornerRadius.Large,
            elevation = Elevation.Large
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
// MAIN COMPONENT
// ════════════════════════════════════════════════════════════════════════════

/**
 * PixaSearchBar - Search input component with suggestions
 *
 * A flexible search input with suggestions, filtering, and customization.
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic search bar
 * var query by remember { mutableStateOf("") }
 * PixaSearchBar(
 *     value = query,
 *     onValueChange = { query = it },
 *     placeholder = "Search..."
 * )
 *
 * // With suggestions
 * PixaSearchBar(
 *     value = query,
 *     onValueChange = { query = it },
 *     suggestions = listOf(
 *         SearchSuggestion("Recent search 1", isRecent = true),
 *         SearchSuggestion("Suggestion 2")
 *     ),
 *     showSuggestions = query.isNotEmpty(),
 *     onSuggestionClick = { suggestion -> query = suggestion.text }
 * )
 *
 * // Elevated variant with voice search
 * PixaSearchBar(
 *     value = query,
 *     onValueChange = { query = it },
 *     variant = SearchBarVariant.Elevated,
 *     size = SearchBarSize.Large,
 *     onSearch = { performSearch(query) },
 *     onVoiceSearch = { startVoiceInput() }
 * )
 *
 * // Custom colors
 * PixaSearchBar(
 *     value = query,
 *     onValueChange = { query = it },
 *     colors = SearchBarColors(
 *         background = Color.White,
 *         text = Color.Black,
 *         // ... other colors
 *     )
 * )
 * ```
 *
 * @param value Current search query
 * @param onValueChange Callback when query changes
 * @param modifier Modifier for the search bar
 * @param variant Visual style variant (Filled, Outlined, Elevated)
 * @param size Size preset (Small, Medium, Large)
 * @param enabled Whether the search bar is enabled
 * @param colors Custom colors (null = use theme)
 * @param placeholder Optional placeholder text
 * @param searchIcon Optional search icon
 * @param clearIcon Optional clear/close icon
 * @param voiceSearchIcon Optional voice search icon
 * @param showClearButton Whether to show clear button when text exists
 * @param onClear Callback when clear button is clicked
 * @param onSearch Callback when search is triggered
 * @param onVoiceSearch Optional callback for voice search
 * @param suggestions List of search suggestions
 * @param showSuggestions Whether to show suggestions dropdown
 * @param onSuggestionClick Callback when suggestion is clicked
 * @param filterSuggestions Whether to filter suggestions based on query
 * @param maxSuggestions Maximum number of suggestions to show
 * @param keyboardOptions Keyboard configuration
 * @param keyboardActions Keyboard actions
 * @param interactionSource Interaction source for state
 * @param contentDescription Accessibility description
 */
@Composable
fun PixaSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: SearchBarVariant = SearchBarVariant.Filled,
    size: SearchBarSize = SearchBarSize.Medium,
    enabled: Boolean = true,
    colors: SearchBarColors? = null,
    placeholder: String = "Search...",
    searchIcon: Painter? = null,
    clearIcon: Painter? = null,
    voiceSearchIcon: Painter? = null,
    showClearButton: Boolean = true,
    onClear: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    onVoiceSearch: (() -> Unit)? = null,
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

    val filteredSuggestions = remember(value, suggestions, filterSuggestions) {
        if (filterSuggestions && value.isNotEmpty()) {
            suggestions.filter { 
                it.text.contains(value, ignoreCase = true) 
            }.take(maxSuggestions)
        } else {
            suggestions.take(maxSuggestions)
        }
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused) themeColors.focusedBorder else themeColors.border,
        animationSpec = tween(durationMillis = 200)
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isFocused && variant == SearchBarVariant.Outlined) sizeConfig.borderWidth * 1.2f else sizeConfig.borderWidth,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier.semantics {
            contentDescription?.let { this.contentDescription = it }
            role = Role.Button
        }
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = sizeConfig.height),
            enabled = enabled,
            textStyle = sizeConfig.textStyle.copy(color = themeColors.text),
            keyboardOptions = keyboardOptions.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch?.invoke() }
            ),
            singleLine = true,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(themeColors.iconTint),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(sizeConfig.cornerRadius))
                        .then(
                            if (variant == SearchBarVariant.Elevated) {
                                Modifier.shadow(
                                    elevation = sizeConfig.elevation,
                                    shape = RoundedCornerShape(sizeConfig.cornerRadius)
                                )
                            } else Modifier
                        )
                        .background(themeColors.background)
                        .then(
                            if (animatedBorderColor != Color.Transparent) {
                                Modifier.border(
                                    width = animatedBorderWidth,
                                    color = animatedBorderColor,
                                    shape = RoundedCornerShape(sizeConfig.cornerRadius)
                                )
                            } else Modifier
                        )
                        .padding(
                            horizontal = sizeConfig.horizontalPadding,
                            vertical = sizeConfig.verticalPadding
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(HierarchicalSize.Spacing.Small)
                    ) {
                        if (searchIcon != null) {
                            PixaIcon(
                                painter = searchIcon,
                                contentDescription = "Search",
                                tint = themeColors.iconTint,
                                modifier = Modifier.size(sizeConfig.iconSize)
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = sizeConfig.textStyle,
                                    color = themeColors.placeholder
                                )
                            }
                            innerTextField()
                        }

                        if (showClearButton && value.isNotEmpty() && clearIcon != null) {
                            PixaIcon(
                                painter = clearIcon,
                                contentDescription = "Clear",
                                tint = themeColors.iconTint,
                                modifier = Modifier
                                    .size(sizeConfig.iconSize)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        onValueChange("")
                                        onClear?.invoke()
                                    }
                            )
                        }
                        if (onVoiceSearch != null && voiceSearchIcon != null && value.isEmpty()) {
                            PixaIcon(
                                painter = voiceSearchIcon,
                                contentDescription = "Voice search",
                                tint = themeColors.iconTint,
                                modifier = Modifier
                                    .size(sizeConfig.iconSize)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        onVoiceSearch.invoke()
                                    }
                            )
                        }
                    }
                }
            }
        )

        // Suggestions dropdown
        if (showSuggestions && filteredSuggestions.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopStart,
                offset = androidx.compose.ui.unit.IntOffset(0, sizeConfig.height.value.toInt()),
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

/**
 * Suggestion dropdown component
 */
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
            .shadow(
                elevation = sizeConfig.elevation,
                shape = RoundedCornerShape(sizeConfig.cornerRadius)
            )
            .clip(RoundedCornerShape(sizeConfig.cornerRadius))
            .background(colors.suggestionBackground)
            .border(
                width = sizeConfig.borderWidth,
                color = colors.divider,
                shape = RoundedCornerShape(sizeConfig.cornerRadius)
            )
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
                        .height(DividerSize.Thin)
                        .background(colors.divider)
                )
            }
        }
    }
}

/**
 * Individual suggestion item
 */
@Composable
private fun SuggestionItem(
    suggestion: SearchSuggestion,
    sizeConfig: SearchBarSizeConfig,
    colors: SearchBarColors,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.Transparent)
            .padding(
                horizontal = sizeConfig.horizontalPadding,
                vertical = sizeConfig.verticalPadding
            ),
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
            Text(
                text = suggestion.text,
                style = sizeConfig.suggestionTextStyle,
                color = colors.suggestionText
            )
            
            if (suggestion.metadata != null) {
                Text(
                    text = suggestion.metadata,
                    style = sizeConfig.suggestionTextStyle.copy(
                        fontSize = sizeConfig.suggestionTextStyle.fontSize * 0.85f
                    ),
                    color = colors.suggestionText.copy(alpha = 0.6f)
                )
            }
        }

        if (suggestion.isRecent) {
            Text(
                text = "Recent",
                style = sizeConfig.suggestionTextStyle.copy(
                    fontSize = sizeConfig.suggestionTextStyle.fontSize * 0.75f
                ),
                color = colors.suggestionText.copy(alpha = 0.5f)
            )
        }
    }
}

// ============================================================================
// Convenience Variants
// ============================================================================

/**
 * FilledSearchBar - Filled background variant
 */
@Composable
fun FilledSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: SearchBarSize = SearchBarSize.Medium,
    enabled: Boolean = true,
    placeholder: String = "Search...",
    searchIcon: Painter? = null,
    clearIcon: Painter? = null,
    voiceSearchIcon: Painter? = null,
    onSearch: (() -> Unit)? = null,
    onVoiceSearch: (() -> Unit)? = null,
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
        clearIcon = clearIcon,
        voiceSearchIcon = voiceSearchIcon,
        onSearch = onSearch,
        onVoiceSearch = onVoiceSearch,
        suggestions = suggestions,
        showSuggestions = showSuggestions,
        onSuggestionClick = onSuggestionClick
    )
}

/**
 * OutlinedSearchBar - Outlined border variant
 */
@Composable
fun OutlinedSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: SearchBarSize = SearchBarSize.Medium,
    enabled: Boolean = true,
    placeholder: String = "Search...",
    searchIcon: Painter? = null,
    clearIcon: Painter? = null,
    voiceSearchIcon: Painter? = null,
    onSearch: (() -> Unit)? = null,
    onVoiceSearch: (() -> Unit)? = null,
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
        clearIcon = clearIcon,
        voiceSearchIcon = voiceSearchIcon,
        onSearch = onSearch,
        onVoiceSearch = onVoiceSearch,
        suggestions = suggestions,
        showSuggestions = showSuggestions,
        onSuggestionClick = onSuggestionClick
    )
}

/**
 * ElevatedSearchBar - Elevated with shadow variant
 */
@Composable
fun ElevatedSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: SearchBarSize = SearchBarSize.Medium,
    enabled: Boolean = true,
    placeholder: String = "Search...",
    searchIcon: Painter? = null,
    clearIcon: Painter? = null,
    voiceSearchIcon: Painter? = null,
    onSearch: (() -> Unit)? = null,
    onVoiceSearch: (() -> Unit)? = null,
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
        clearIcon = clearIcon,
        voiceSearchIcon = voiceSearchIcon,
        onSearch = onSearch,
        onVoiceSearch = onVoiceSearch,
        suggestions = suggestions,
        showSuggestions = showSuggestions,
        onSuggestionClick = onSuggestionClick
    )
}

// ============================================================================
// Specialized Convenience Functions
// ============================================================================

/**
 * ProductSearchBar - Pre-configured for product search
 */
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

/**
 * LocationSearchBar - Pre-configured for location search
 */
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

/**
 * ContactSearchBar - Pre-configured for contact search
 */
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
