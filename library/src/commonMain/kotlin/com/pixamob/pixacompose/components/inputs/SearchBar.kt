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
import androidx.compose.runtime.*
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
import com.pixamob.pixacompose.components.display.Icon
import com.pixamob.pixacompose.theme.*
import com.pixamob.pixacompose.utils.AnimationUtils

// ============================================================================
// Configuration
// ============================================================================

/**
 * SearchBar variant enum
 */
enum class SearchBarVariant {
    Filled,    // Filled background
    Outlined,  // Border only
    Elevated   // Elevated with shadow
}

/**
 * SearchBar size enum
 */
enum class SearchBarSize {
    Small,     // Compact (36dp height)
    Medium,    // Standard (44dp height)
    Large      // Comfortable (52dp height)
}

/**
 * Configuration for SearchBar appearance
 */
@Stable
private data class SearchBarConfig(
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

/**
 * Get configuration for given size
 */
@Composable
private fun SearchBarSize.config(): SearchBarConfig {
    val typography = AppTheme.typography
    return when (this) {
        SearchBarSize.Small -> SearchBarConfig(
            height = ComponentSize.InputSmall,
            horizontalPadding = Spacing.Medium,
            verticalPadding = Spacing.ExtraSmall,
            textStyle = typography.bodySmall,
            suggestionTextStyle = typography.bodySmall,
            iconSize = IconSize.Small,
            borderWidth = BorderWidth.Thin,
            cornerRadius = CornerRadius.Small,
            elevation = Elevation.Small
        )
        SearchBarSize.Medium -> SearchBarConfig(
            height = ComponentSize.InputMedium,
            horizontalPadding = Spacing.Large,
            verticalPadding = Spacing.Small,
            textStyle = typography.bodyRegular,
            suggestionTextStyle = typography.bodyRegular,
            iconSize = IconSize.Medium,
            borderWidth = BorderWidth.Medium,
            cornerRadius = CornerRadius.Medium,
            elevation = Elevation.Medium
        )
        SearchBarSize.Large -> SearchBarConfig(
            height = ComponentSize.InputLarge,
            horizontalPadding = Spacing.ExtraLarge,
            verticalPadding = Spacing.Medium,
            textStyle = typography.bodyLarge,
            suggestionTextStyle = typography.bodyLarge,
            iconSize = IconSize.Large,
            borderWidth = BorderWidth.Thick,
            cornerRadius = CornerRadius.Large,
            elevation = Elevation.Large
        )
    }
}

// ============================================================================
// Theme
// ============================================================================

/**
 * Colors for SearchBar states
 */
@Stable
private data class SearchBarColors(
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

/**
 * Get colors for SearchBar variant
 */
@Composable
private fun SearchBarVariant.colors(
    isFocused: Boolean,
    enabled: Boolean
): SearchBarColors {
    val colors = AppTheme.colors

    return when (this) {
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

// ============================================================================
// Data Classes
// ============================================================================

/**
 * Represents a search suggestion item
 */
data class SearchSuggestion(
    val text: String,
    val icon: Painter? = null,
    val metadata: String? = null,
    val isRecent: Boolean = false
)

// ============================================================================
// Base Component
// ============================================================================

/**
 * BaseSearchBar - Core search input component
 *
 * Dynamic and reusable search input with suggestions, filtering, and customization.
 * Follows Material 3 design with theme integration.
 *
 * @param value Current search query
 * @param onValueChange Callback when query changes
 * @param modifier Modifier for the search bar
 * @param variant Visual style variant
 * @param size Size preset
 * @param enabled Whether the search bar is enabled
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
 *
 * @sample
 * ```
 * var query by remember { mutableStateOf("") }
 * PixaSearchBar(
 *     value = query,
 *     onValueChange = { query = it },
 *     placeholder = "Search...",
 *     suggestions = listOf(
 *         SearchSuggestion("Recent search 1", isRecent = true),
 *         SearchSuggestion("Suggestion 2")
 *     ),
 *     showSuggestions = query.isNotEmpty(),
 *     onSearch = { performSearch(query) }
 * )
 * ```
 */
@Composable
fun PixaSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: SearchBarVariant = SearchBarVariant.Filled,
    size: SearchBarSize = SearchBarSize.Medium,
    enabled: Boolean = true,
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
    val config = size.config()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val colors = variant.colors(isFocused, enabled)

    // Filter suggestions based on query
    val filteredSuggestions = remember(value, suggestions, filterSuggestions) {
        if (filterSuggestions && value.isNotEmpty()) {
            suggestions.filter { 
                it.text.contains(value, ignoreCase = true) 
            }.take(maxSuggestions)
        } else {
            suggestions.take(maxSuggestions)
        }
    }

    // Animated colors
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused) colors.focusedBorder else colors.border,
        animationSpec = tween(durationMillis = 200)
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isFocused && variant == SearchBarVariant.Outlined) config.borderWidth * 1.2f else config.borderWidth,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier.semantics {
            contentDescription?.let { this.contentDescription = it }
            role = Role.Button
        }
    ) {
        // Search input
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = config.height),
            enabled = enabled,
            textStyle = config.textStyle.copy(color = colors.text),
            keyboardOptions = keyboardOptions.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch?.invoke() }
            ),
            singleLine = true,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(colors.iconTint),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(config.cornerRadius))
                        .then(
                            if (variant == SearchBarVariant.Elevated) {
                                Modifier.shadow(
                                    elevation = config.elevation,
                                    shape = RoundedCornerShape(config.cornerRadius)
                                )
                            } else Modifier
                        )
                        .background(colors.background)
                        .then(
                            if (animatedBorderColor != Color.Transparent) {
                                Modifier.border(
                                    width = animatedBorderWidth,
                                    color = animatedBorderColor,
                                    shape = RoundedCornerShape(config.cornerRadius)
                                )
                            } else Modifier
                        )
                        .padding(
                            horizontal = config.horizontalPadding,
                            vertical = config.verticalPadding
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                    ) {
                        // Search icon
                        if (searchIcon != null) {
                            Icon(
                                painter = searchIcon,
                                contentDescription = "Search",
                                tint = colors.iconTint,
                                modifier = Modifier.size(config.iconSize)
                            )
                        }

                        // Text input area
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            // Placeholder
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = config.textStyle,
                                    color = colors.placeholder
                                )
                            }
                            innerTextField()
                        }

                        // Clear button
                        if (showClearButton && value.isNotEmpty() && clearIcon != null) {
                            Icon(
                                painter = clearIcon,
                                contentDescription = "Clear",
                                tint = colors.iconTint,
                                modifier = Modifier
                                    .size(config.iconSize)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        onValueChange("")
                                        onClear?.invoke()
                                    }
                            )
                        }

                        // Voice search button
                        if (onVoiceSearch != null && voiceSearchIcon != null && value.isEmpty()) {
                            Icon(
                                painter = voiceSearchIcon,
                                contentDescription = "Voice search",
                                tint = colors.iconTint,
                                modifier = Modifier
                                    .size(config.iconSize)
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
                offset = androidx.compose.ui.unit.IntOffset(0, config.height.value.toInt()),
                properties = PopupProperties(focusable = false)
            ) {
                SuggestionDropdown(
                    suggestions = filteredSuggestions,
                    config = config,
                    colors = colors,
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
    config: SearchBarConfig,
    colors: SearchBarColors,
    onSuggestionClick: (SearchSuggestion) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = config.elevation,
                shape = RoundedCornerShape(config.cornerRadius)
            )
            .clip(RoundedCornerShape(config.cornerRadius))
            .background(colors.suggestionBackground)
            .border(
                width = config.borderWidth,
                color = colors.divider,
                shape = RoundedCornerShape(config.cornerRadius)
            )
    ) {
        suggestions.forEachIndexed { index, suggestion ->
            SuggestionItem(
                suggestion = suggestion,
                config = config,
                colors = colors,
                onClick = { onSuggestionClick(suggestion) }
            )
            
            if (index < suggestions.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
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
    config: SearchBarConfig,
    colors: SearchBarColors,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.Transparent)
            .padding(
                horizontal = config.horizontalPadding,
                vertical = config.verticalPadding
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
    ) {
        // Icon
        if (suggestion.icon != null) {
            Icon(
                painter = suggestion.icon,
                contentDescription = null,
                tint = colors.iconTint,
                modifier = Modifier.size(config.iconSize)
            )
        }

        // Text content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = suggestion.text,
                style = config.suggestionTextStyle,
                color = colors.suggestionText
            )
            
            if (suggestion.metadata != null) {
                Text(
                    text = suggestion.metadata,
                    style = config.suggestionTextStyle.copy(
                        fontSize = config.suggestionTextStyle.fontSize * 0.85f
                    ),
                    color = colors.suggestionText.copy(alpha = 0.6f)
                )
            }
        }

        // Recent indicator
        if (suggestion.isRecent) {
            Text(
                text = "Recent",
                style = config.suggestionTextStyle.copy(
                    fontSize = config.suggestionTextStyle.fontSize * 0.75f
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
