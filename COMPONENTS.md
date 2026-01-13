# Components Documentation

Comprehensive guide to all PixaCompose components with parameters, variants, and usage examples.

## Table of Contents

- [Input Components](#input-components)
  - [TextField](#textfield)
  - [TextArea](#textarea)
  - [SearchBar](#searchbar)
  - [Slider](#slider)
  - [Switch](#switch)
  - [Checkbox](#checkbox)
  - [RadioButton](#radiobutton)
  - [DatePicker](#datepicker)
- [Display Components](#display-components)
  - [Button](#button)
  - [Icon](#icon)
  - [Avatar](#avatar)
  - [Badge](#badge)
  - [Chip](#chip)
  - [Divider](#divider)
  - [Loader](#loader)
- [Navigation Components](#navigation-components)
  - [BottomNavBar](#bottomnavbar)
  - [TopAppBar](#topappbar)
  - [NavigationDrawer](#navigationdrawer)
  - [Tabs](#tabs)

---

## Input Components

### TextField

Single-line text input with label, placeholder, icons, and error handling.

**Variants**: `FilledTextField`, `OutlinedTextField`, `GhostTextField`

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | Required | Current text value |
| `onValueChange` | `(String) -> Unit` | Required | Callback when text changes |
| `modifier` | `Modifier` | `Modifier` | Modifier for the field |
| `size` | `TextFieldSize` | `Medium` | Size preset (Small, Medium, Large) |
| `enabled` | `Boolean` | `true` | Whether the field is enabled |
| `readOnly` | `Boolean` | `false` | Whether the field is read-only |
| `isError` | `Boolean` | `false` | Show error state |
| `label` | `String?` | `null` | Optional label text |
| `placeholder` | `String?` | `null` | Optional placeholder text |
| `helperText` | `String?` | `null` | Optional helper text below field |
| `errorText` | `String?` | `null` | Error text (shown when isError=true) |
| `leadingIcon` | `Painter?` | `null` | Optional leading icon |
| `trailingIcon` | `Painter?` | `null` | Optional trailing icon |
| `visualTransformation` | `VisualTransformation` | `None` | Visual transformation (e.g., password) |
| `keyboardOptions` | `KeyboardOptions` | `Default` | Keyboard configuration |
| `singleLine` | `Boolean` | `true` | Limit to single line |
| `maxLength` | `Int?` | `null` | Maximum character length |

**Example**:
```kotlin
var email by remember { mutableStateOf("") }
var password by remember { mutableStateOf("") }

OutlinedTextField(
    value = email,
    onValueChange = { email = it },
    label = "Email",
    placeholder = "your@email.com",
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
)

OutlinedTextField(
    value = password,
    onValueChange = { password = it },
    label = "Password",
    visualTransformation = PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
)
```

---

### TextArea

Multi-line text input for longer content.

**Parameters**: Same as TextField, plus:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `minLines` | `Int` | `3` | Minimum visible lines |
| `maxLines` | `Int` | `Int.MAX_VALUE` | Maximum lines before scrolling |

**Example**:
```kotlin
var notes by remember { mutableStateOf("") }

FilledTextArea(
    value = notes,
    onValueChange = { notes = it },
    label = "Notes",
    placeholder = "Enter your notes here...",
    minLines = 5,
    maxLines = 10
)
```

---

### SearchBar

Search-specific input with built-in search icon.

**Parameters**: Same as TextField

**Example**:
```kotlin
var searchQuery by remember { mutableStateOf("") }

SearchBar(
    value = searchQuery,
    onValueChange = { searchQuery = it },
    placeholder = "Search...",
    size = SearchBarSize.Medium
)
```

---

### Slider

Continuous or discrete value selection.

**Variants**: `FilledSlider`, `OutlinedSlider`, `MinimalSlider`

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Float` | Required | Current slider value |
| `onValueChange` | `(Float) -> Unit` | Required | Callback when value changes |
| `modifier` | `Modifier` | `Modifier` | Modifier for the slider |
| `size` | `SliderSize` | `Medium` | Size preset (Small, Medium, Large) |
| `enabled` | `Boolean` | `true` | Whether the slider is enabled |
| `valueRange` | `ClosedFloatingPointRange<Float>` | `0f..1f` | Range of values (min to max) |
| `steps` | `Int` | `0` | Number of discrete steps (0 = continuous) |
| `label` | `String?` | `null` | Optional label text |
| `showValue` | `Boolean` | `false` | Show current value |
| `valueFormatter` | `(Float) -> String` | `{ it.toInt().toString() }` | Custom value formatter |
| `onValueChangeFinished` | `(() -> Unit)?` | `null` | Callback when user finishes changing |

**Example**:
```kotlin
var volume by remember { mutableStateOf(50f) }
var rating by remember { mutableStateOf(3f) }

// Continuous slider
FilledSlider(
    value = volume,
    onValueChange = { volume = it },
    label = "Volume",
    valueRange = 0f..100f,
    showValue = true,
    valueFormatter = { "${it.toInt()}%" }
)

// Discrete slider (5 steps for 1-5 rating)
FilledSlider(
    value = rating,
    onValueChange = { rating = it },
    label = "Rating",
    valueRange = 1f..5f,
    steps = 3, // Creates 5 discrete positions
    showValue = true,
    valueFormatter = { "${it.toInt()}/5 ⭐" }
)
```

**Specialized Variants**:
```kotlin
// Volume slider (0-100)
VolumeSlider(
    value = volume,
    onValueChange = { volume = it }
)

// Rating slider (1-5)
RatingSlider(
    value = rating,
    onValueChange = { rating = it }
)
```

---

### Switch

Binary toggle control (on/off).

**Variants**: `FilledSwitch`, `OutlinedSwitch`, `MinimalSwitch`

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `checked` | `Boolean` | Required | Whether switch is on |
| `onCheckedChange` | `(Boolean) -> Unit` | Required | Callback when state changes |
| `modifier` | `Modifier` | `Modifier` | Modifier for the switch |
| `size` | `SwitchSize` | `Medium` | Size preset (Small, Medium, Large) |
| `enabled` | `Boolean` | `true` | Whether the switch is enabled |
| `label` | `String?` | `null` | Optional label text |
| `labelPosition` | `LabelPosition` | `End` | Label position (Start or End) |

**Example**:
```kotlin
var notificationsEnabled by remember { mutableStateOf(false) }
var darkModeEnabled by remember { mutableStateOf(false) }

// Switch with label on right
FilledSwitch(
    checked = notificationsEnabled,
    onCheckedChange = { notificationsEnabled = it },
    label = "Enable Notifications"
)

// Switch with label on left (settings style)
SettingSwitch(
    checked = darkModeEnabled,
    onCheckedChange = { darkModeEnabled = it },
    label = "Dark Mode"
)

// Simple toggle without label
ToggleSwitch(
    checked = isEnabled,
    onCheckedChange = { isEnabled = it }
)
```

---

### Checkbox

Multi-select option with three states (unchecked, checked, indeterminate).

**Variants**: `FilledCheckbox`, `OutlinedCheckbox`

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `state` | `CheckboxState` | Required | Current state (Unchecked, Checked, Indeterminate) |
| `onStateChange` | `(CheckboxState) -> Unit` | Required | Callback when state changes |
| `modifier` | `Modifier` | `Modifier` | Modifier for the checkbox |
| `size` | `CheckboxSize` | `Medium` | Size preset (Small, Medium, Large) |
| `enabled` | `Boolean` | `true` | Whether the checkbox is enabled |
| `label` | `String?` | `null` | Optional label text |

**Example**:
```kotlin
var agreeToTerms by remember { mutableStateOf(CheckboxState.Unchecked) }

FilledCheckbox(
    state = agreeToTerms,
    onStateChange = { agreeToTerms = it },
    label = "I agree to the terms and conditions"
)
```

---

### RadioButton

Single-select from a group of options.

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `selected` | `Boolean` | Required | Whether this option is selected |
| `onClick` | `() -> Unit` | Required | Callback when clicked |
| `modifier` | `Modifier` | `Modifier` | Modifier for the radio button |
| `size` | `RadioButtonSize` | `Medium` | Size preset (Small, Medium, Large) |
| `enabled` | `Boolean` | `true` | Whether the button is enabled |
| `label` | `String?` | `null` | Optional label text |

**Example**:
```kotlin
var selectedOption by remember { mutableStateOf("option1") }

Column {
    FilledRadioButton(
        selected = selectedOption == "option1",
        onClick = { selectedOption = "option1" },
        label = "Option 1"
    )
    FilledRadioButton(
        selected = selectedOption == "option2",
        onClick = { selectedOption = "option2" },
        label = "Option 2"
    )
}
```

---

### DatePicker

Date selection with calendar interface.

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `selectedDate` | `LocalDate?` | Required | Currently selected date |
| `onDateSelected` | `(LocalDate) -> Unit` | Required | Callback when date is selected |
| `modifier` | `Modifier` | `Modifier` | Modifier for the picker |
| `minDate` | `LocalDate?` | `null` | Minimum selectable date |
| `maxDate` | `LocalDate?` | `null` | Maximum selectable date |
| `label` | `String?` | `null` | Optional label |

**Example**:
```kotlin
var birthDate by remember { mutableStateOf<LocalDate?>(null) }

DatePicker(
    selectedDate = birthDate,
    onDateSelected = { birthDate = it },
    label = "Birth Date",
    maxDate = LocalDate.now() // Can't select future dates
)
```

---

## Display Components

### Button

Action buttons with multiple variants and states.

**Variants**: `PrimaryButton`, `SecondaryButton`, `GhostButton`

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `text` | `String` | Required | Button text |
| `onClick` | `() -> Unit` | Required | Callback when clicked |
| `modifier` | `Modifier` | `Modifier` | Modifier for the button |
| `size` | `ButtonSize` | `Medium` | Size preset (Small, Medium, Large) |
| `enabled` | `Boolean` | `true` | Whether the button is enabled |
| `loading` | `Boolean` | `false` | Show loading state |
| `leadingIcon` | `Painter?` | `null` | Optional leading icon |
| `trailingIcon` | `Painter?` | `null` | Optional trailing icon |

**Example**:
```kotlin
PrimaryButton(
    text = "Submit",
    onClick = { /* Handle submit */ },
    size = ButtonSize.Large
)

SecondaryButton(
    text = "Cancel",
    onClick = { /* Handle cancel */ }
)

GhostButton(
    text = "Learn More",
    onClick = { /* Navigate */ },
    trailingIcon = Icons.Default.ArrowForward
)
```

---

### Icon

Display vector graphics with consistent sizing.

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `painter` | `Painter` | Required | Icon painter |
| `contentDescription` | `String?` | Required | Accessibility description |
| `modifier` | `Modifier` | `Modifier` | Modifier for the icon |
| `size` | `IconSize` | `Medium` | Size preset (Small, Medium, Large) |
| `tint` | `Color?` | `null` | Optional color tint |

**Example**:
```kotlin
Icon(
    painter = painterResource(Res.drawable.ic_search),
    contentDescription = "Search",
    size = IconSize.Large,
    tint = AppTheme.colors.brandContentDefault
)
```

---

### Avatar

Display user images with circular or rounded shapes.

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `imageUrl` | `String?` | Required | Image URL or null |
| `name` | `String?` | Required | Name for initials fallback |
| `modifier` | `Modifier` | `Modifier` | Modifier for the avatar |
| `size` | `AvatarSize` | `Medium` | Size preset (Small, Medium, Large, ExtraLarge) |
| `shape` | `AvatarShape` | `Circle` | Shape (Circle or Rounded) |

**Example**:
```kotlin
Avatar(
    imageUrl = user.photoUrl,
    name = user.name,
    size = AvatarSize.Large,
    shape = AvatarShape.Circle
)
```

---

### Badge

Notification indicators for counts and status.

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `count` | `Int?` | Required | Count to display (null for dot) |
| `modifier` | `Modifier` | `Modifier` | Modifier for the badge |
| `variant` | `BadgeVariant` | `Default` | Variant (Default, Success, Error, Warning) |

**Example**:
```kotlin
Box {
    Icon(...)
    Badge(
        count = unreadCount,
        variant = BadgeVariant.Error,
        modifier = Modifier.align(Alignment.TopEnd)
    )
}
```

---

### Chip

Compact selectable elements for filters and tags.

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `label` | `String` | Required | Chip label |
| `selected` | `Boolean` | Required | Whether chip is selected |
| `onClick` | `() -> Unit` | Required | Callback when clicked |
| `modifier` | `Modifier` | `Modifier` | Modifier for the chip |
| `size` | `ChipSize` | `Medium` | Size preset (Small, Medium, Large) |
| `leadingIcon` | `Painter?` | `null` | Optional leading icon |

**Example**:
```kotlin
var selectedTags by remember { mutableStateOf(setOf<String>()) }

FlowRow {
    listOf("Kotlin", "Compose", "Android", "iOS").forEach { tag ->
        FilterChip(
            label = tag,
            selected = tag in selectedTags,
            onClick = {
                selectedTags = if (tag in selectedTags) {
                    selectedTags - tag
                } else {
                    selectedTags + tag
                }
            }
        )
    }
}
```

---

### Divider

Visual separation between content sections.

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `modifier` | `Modifier` | `Modifier` | Modifier for the divider |
| `thickness` | `Dp` | `1.dp` | Divider thickness |
| `color` | `Color?` | `null` | Custom color (uses theme default if null) |

**Example**:
```kotlin
Column {
    Text("Section 1")
    Divider()
    Text("Section 2")
}
```

---

### Loader

Loading indicators for asynchronous operations.

**Variants**: `CircularLoader`, `LinearLoader`

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `modifier` | `Modifier` | `Modifier` | Modifier for the loader |
| `size` | `LoaderSize` | `Medium` | Size preset (Small, Medium, Large) |
| `progress` | `Float?` | `null` | Progress (0-1) for determinate loader |

**Example**:
```kotlin
if (isLoading) {
    CircularLoader(size = LoaderSize.Large)
}

// Determinate progress
LinearLoader(
    progress = downloadProgress,
    modifier = Modifier.fillMaxWidth()
)
```

---

## Navigation Components

### BottomNavBar

Bottom navigation bar with icons and labels.

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `items` | `List<NavBarItem>` | Required | Navigation items |
| `selectedIndex` | `Int` | Required | Currently selected item index |
| `onItemSelected` | `(Int) -> Unit` | Required | Callback when item is selected |
| `modifier` | `Modifier` | `Modifier` | Modifier for the nav bar |

**Example**:
```kotlin
var selectedIndex by remember { mutableStateOf(0) }

BottomNavBar(
    items = listOf(
        NavBarItem("Home", Icons.Default.Home),
        NavBarItem("Search", Icons.Default.Search),
        NavBarItem("Profile", Icons.Default.Person)
    ),
    selectedIndex = selectedIndex,
    onItemSelected = { selectedIndex = it }
)
```

---

### TopAppBar

Top app bar with title and actions.

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `title` | `String` | Required | App bar title |
| `modifier` | `Modifier` | `Modifier` | Modifier for the app bar |
| `navigationIcon` | `Painter?` | `null` | Optional navigation icon (back/menu) |
| `onNavigationClick` | `(() -> Unit)?` | `null` | Navigation icon click handler |
| `actions` | `@Composable RowScope.() -> Unit` | `{}` | Action buttons on the right |

**Example**:
```kotlin
TopAppBar(
    title = "My App",
    navigationIcon = Icons.Default.Menu,
    onNavigationClick = { openDrawer() },
    actions = {
        IconButton(onClick = { /* search */ }) {
            Icon(Icons.Default.Search, "Search")
        }
        IconButton(onClick = { /* more */ }) {
            Icon(Icons.Default.MoreVert, "More")
        }
    }
)
```

---

### NavigationDrawer

Side drawer navigation menu.

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `items` | `List<DrawerItem>` | Required | Drawer menu items |
| `selectedIndex` | `Int` | Required | Currently selected item |
| `onItemSelected` | `(Int) -> Unit` | Required | Callback when item is selected |
| `isOpen` | `Boolean` | Required | Whether drawer is open |
| `onClose` | `() -> Unit` | Required | Callback to close drawer |

**Example**:
```kotlin
var isDrawerOpen by remember { mutableStateOf(false) }
var selectedIndex by remember { mutableStateOf(0) }

NavigationDrawer(
    items = listOf(
        DrawerItem("Home", Icons.Default.Home),
        DrawerItem("Settings", Icons.Default.Settings),
        DrawerItem("Help", Icons.Default.Help)
    ),
    selectedIndex = selectedIndex,
    onItemSelected = {
        selectedIndex = it
        isDrawerOpen = false
    },
    isOpen = isDrawerOpen,
    onClose = { isDrawerOpen = false }
)
```

---

### Tabs

Tabbed navigation interface.

**Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `tabs` | `List<String>` | Required | Tab labels |
| `selectedIndex` | `Int` | Required | Currently selected tab |
| `onTabSelected` | `(Int) -> Unit` | Required | Callback when tab is selected |
| `modifier` | `Modifier` | `Modifier` | Modifier for the tabs |

**Example**:
```kotlin
var selectedTab by remember { mutableStateOf(0) }

Column {
    Tabs(
        tabs = listOf("Overview", "Details", "Reviews"),
        selectedIndex = selectedTab,
        onTabSelected = { selectedTab = it }
    )
    
    when (selectedTab) {
        0 -> OverviewContent()
        1 -> DetailsContent()
        2 -> ReviewsContent()
    }
}
```

---

## Common Patterns

### Form Example

```kotlin
@Composable
fun RegistrationForm() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name",
            size = TextFieldSize.Large
        )
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledCheckbox(
                checked = agreeToTerms,
                onCheckedChange = { agreeToTerms = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("I agree to the terms and conditions")
        }
        
        PrimaryButton(
            text = "Register",
            onClick = { /* Handle registration */ },
            enabled = name.isNotBlank() && email.isNotBlank() && 
                     password.isNotBlank() && agreeToTerms,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

### Settings Screen Example

```kotlin
@Composable
fun SettingsScreen() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var volume by remember { mutableStateOf(70f) }
    
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingSwitch(
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it },
            label = "Enable Notifications"
        )
        
        Divider()
        
        SettingSwitch(
            checked = darkModeEnabled,
            onCheckedChange = { darkModeEnabled = it },
            label = "Dark Mode"
        )
        
        Divider()
        
        VolumeSlider(
            value = volume,
            onValueChange = { volume = it }
        )
    }
}
```

---

## Theme Colors

All components use the following theme colors:

### Brand Colors
- `brandContentDefault` - Primary brand color
- `brandSurfaceDefault` - Brand background
- `brandBorderDefault` - Brand borders

### Base Colors
- `baseContentTitle` - Primary text
- `baseContentBody` - Body text
- `baseContentCaption` - Secondary text
- `baseSurfaceDefault` - Default background
- `baseBorderDefault` - Default borders

### Utility Colors
- `errorContentDefault` - Error text/icons
- `errorBorderDefault` - Error borders
- `successContentDefault` - Success indicators
- `warningContentDefault` - Warning indicators

---

## Size Presets

### Component Heights
- **Small**: 36dp
- **Medium**: 44dp
- **Large**: 52dp

### Icon Sizes
- **Small**: 16dp
- **Medium**: 24dp
- **Large**: 32dp

### Typography Sizes
- **Small**: 12-14sp
- **Medium**: 14-16sp
- **Large**: 16-18sp

---

For more examples and detailed API documentation, visit the [GitHub repository](https://github.com/ayoubarka/PixaCompose).

