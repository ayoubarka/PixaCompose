# Quick Reference Guide - TextArea & SearchBar

## TextArea Quick Reference

### Basic Usage
```kotlin
// Simple outlined text area
var text by remember { mutableStateOf("") }
OutlinedTextArea(
    value = text,
    onValueChange = { text = it },
    label = "Description",
    placeholder = "Enter description..."
)
```

### With Character Limit
```kotlin
OutlinedTextArea(
    value = text,
    onValueChange = { text = it },
    label = "Bio",
    placeholder = "Tell us about yourself...",
    maxLength = 300,
    showCharacterCount = true,
    minLines = 4,
    maxLines = 8
)
```

### With Error State
```kotlin
val hasError = text.length < 10
OutlinedTextArea(
    value = text,
    onValueChange = { text = it },
    label = "Comment",
    placeholder = "Write your comment...",
    isError = hasError,
    errorText = "Comment must be at least 10 characters",
    helperText = if (!hasError) "Share your thoughts" else null
)
```

### Pre-configured Variants
```kotlin
// Comment (500 chars)
CommentTextArea(
    value = comment,
    onValueChange = { comment = it }
)

// Bio (300 chars)
BioTextArea(
    value = bio,
    onValueChange = { bio = it }
)

// Note (unlimited)
NoteTextArea(
    value = note,
    onValueChange = { note = it }
)
```

## SearchBar Quick Reference

### Basic Usage
```kotlin
var query by remember { mutableStateOf("") }
FilledSearchBar(
    value = query,
    onValueChange = { query = it },
    placeholder = "Search...",
    onSearch = { performSearch(query) }
)
```

### With Suggestions
```kotlin
val suggestions = listOf(
    SearchSuggestion("React Native", isRecent = true),
    SearchSuggestion("React Hooks", metadata = "JavaScript"),
    SearchSuggestion("React Router", metadata = "Library")
)

OutlinedSearchBar(
    value = query,
    onValueChange = { query = it },
    placeholder = "Search documentation...",
    suggestions = suggestions,
    showSuggestions = query.length >= 2,
    onSearch = { performSearch(query) },
    onSuggestionClick = { suggestion ->
        query = suggestion.text
        performSearch(query)
    }
)
```

### With Voice Search
```kotlin
ElevatedSearchBar(
    value = query,
    onValueChange = { query = it },
    placeholder = "Search products...",
    onSearch = { performSearch(query) },
    onVoiceSearch = { startVoiceRecognition() }
)
```

### Pre-configured Variants
```kotlin
// Product search
ProductSearchBar(
    value = query,
    onValueChange = { query = it },
    suggestions = productSuggestions,
    onSearch = { searchProducts(query) }
)

// Location search (triggers at 3+ chars)
LocationSearchBar(
    value = query,
    onValueChange = { query = it },
    suggestions = locationSuggestions,
    onSearch = { searchLocations(query) }
)

// Contact search
ContactSearchBar(
    value = query,
    onValueChange = { query = it },
    suggestions = contactSuggestions,
    onSuggestionClick = { contact ->
        selectContact(contact.text)
    }
)
```

## Component Sizes Reference

### TextArea Sizes
- **Small**: 96dp min height, compact padding
- **Medium**: 128dp min height, standard padding (default)
- **Large**: 160dp min height, comfortable padding

### SearchBar Sizes
- **Small**: 36dp height, compact
- **Medium**: 44dp height, touch-friendly (default)
- **Large**: 52dp height, comfortable

## Visual Variants

### TextArea Variants
- **Filled**: Solid background, subtle border on focus
- **Outlined**: Transparent background, visible border (default)
- **Ghost**: Minimal, transparent, subtle focus indicator

### SearchBar Variants
- **Filled**: Solid background, integrated look
- **Outlined**: Transparent with border, classic look
- **Elevated**: Raised with shadow, prominent

## ComponentSize Usage Throughout Project

### Buttons
```kotlin
Button(
    modifier = Modifier.height(ComponentSize.ButtonMedium)
)
```

### Chips
```kotlin
Chip(
    modifier = Modifier.height(ComponentSize.ChipSmall)
)
```

### List Items
```kotlin
ListItem(
    modifier = Modifier.height(ComponentSize.ListItemMedium)
)
```

### App Bars
```kotlin
TopAppBar(
    modifier = Modifier.height(ComponentSize.AppBarMedium)
)
```

### Bottom Navigation
```kotlin
BottomNavigation(
    modifier = Modifier.height(ComponentSize.BottomNavMedium)
)
```

### Tabs
```kotlin
TabRow(
    modifier = Modifier.height(ComponentSize.TabMedium)
)
```

### Progress Indicators
```kotlin
// Linear
LinearProgressIndicator(
    modifier = Modifier.height(ComponentSize.ProgressMedium)
)

// Circular
CircularProgressIndicator(
    modifier = Modifier.size(ComponentSize.ProgressCircularMedium)
)
```

### Cards
```kotlin
Card(
    modifier = Modifier.heightIn(min = ComponentSize.CardMedium)
)
```

### Dialogs
```kotlin
Dialog {
    Box(
        modifier = Modifier
            .widthIn(
                min = ComponentSize.DialogMinWidth,
                max = ComponentSize.DialogMaxWidth
            )
            .heightIn(min = ComponentSize.DialogMinHeight)
    )
}
```

## Border & Corner Radius

### Border Width
```kotlin
Box(
    modifier = Modifier.border(
        width = BorderWidth.Medium,
        color = Color.Gray
    )
)
```

### Corner Radius
```kotlin
Box(
    modifier = Modifier
        .clip(RoundedCornerShape(CornerRadius.Medium))
        .background(Color.Gray)
)
```

### Elevation
```kotlin
Card(
    modifier = Modifier.shadow(
        elevation = Elevation.Medium,
        shape = RoundedCornerShape(CornerRadius.Medium)
    )
)
```

## Common Patterns

### Validation with Error State
```kotlin
var text by remember { mutableStateOf("") }
var hasError by remember { mutableStateOf(false) }

OutlinedTextArea(
    value = text,
    onValueChange = { 
        text = it
        hasError = it.length < 10
    },
    label = "Description",
    isError = hasError,
    errorText = if (hasError) "Minimum 10 characters" else null,
    maxLength = 500,
    showCharacterCount = true
)
```

### Search with Loading State
```kotlin
var query by remember { mutableStateOf("") }
var isLoading by remember { mutableStateOf(false) }
var suggestions by remember { mutableStateOf(emptyList<SearchSuggestion>()) }

LaunchedEffect(query) {
    if (query.length >= 2) {
        isLoading = true
        delay(300) // Debounce
        suggestions = fetchSuggestions(query)
        isLoading = false
    }
}

FilledSearchBar(
    value = query,
    onValueChange = { query = it },
    suggestions = suggestions,
    showSuggestions = query.length >= 2 && !isLoading
)
```

### Dynamic Icon Based on State
```kotlin
var query by remember { mutableStateOf("") }
var isVoiceActive by remember { mutableStateOf(false) }

ElevatedSearchBar(
    value = query,
    onValueChange = { query = it },
    searchIcon = painterResource(
        if (query.isEmpty()) "ic_search" else "ic_search_filled"
    ),
    clearIcon = painterResource("ic_close"),
    voiceSearchIcon = painterResource(
        if (isVoiceActive) "ic_mic_active" else "ic_mic"
    )
)
```

## Best Practices

### TextArea
1. Always provide a label for accessibility
2. Use maxLength with showCharacterCount for user guidance
3. Set appropriate minLines and maxLines for the use case
4. Provide clear error messages
5. Use helper text to guide users

### SearchBar
1. Debounce search queries to reduce API calls
2. Show suggestions after 2-3 characters
3. Limit suggestions to 5-8 items for better UX
4. Mark recent searches with isRecent flag
5. Clear search on navigation away
6. Provide clear feedback during search

### ComponentSize
1. Use consistent sizes across similar components
2. Prefer Medium variants for default cases
3. Use Small for dense layouts (tablets, desktop)
4. Use Large for accessibility or emphasis
5. Always reference ComponentSize instead of hardcoded values

---

**Quick Start Checklist**
- ✅ Import component: `import com.pixamob.pixacompose.components.inputs.*`
- ✅ Use remember for state: `var text by remember { mutableStateOf("") }`
- ✅ Set value and onValueChange
- ✅ Add label and placeholder for better UX
- ✅ Configure size and variant as needed
- ✅ Add error handling and validation
- ✅ Test on multiple screen sizes

