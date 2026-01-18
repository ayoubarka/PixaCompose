# Card Components Documentation

## Overview

The `CardComponents.kt` file provides a comprehensive set of reusable card components for the Streakio app, following UI design patterns and best practices from [justinmind.com/ui-design/cards](https://www.justinmind.com/ui-design/cards).

All cards are built on top of the existing card elements in `ui/elements/cards/` and follow the project's theme-driven architecture.

## Key Features

- ✅ **9 Purpose-Built Card Types** - Each card optimized for specific use cases
- ✅ **Dynamic Styling** - All cards accept custom `CardStyle` parameter
- ✅ **Theme-Aware** - Automatically adapts to light/dark mode
- ✅ **Flexible Content** - Support for icons (vector & URL), text, images, and custom content
- ✅ **Consistent API** - Similar parameters across all card types
- ✅ **Accessibility** - Proper content descriptions and semantic roles
- ✅ **Size Variants** - Compact, Small, Medium, Large, Huge
- ✅ **Shape Variants** - Default, Rounded, ExtraRounded

---

## Card Types

### 1. InfoCard
**Purpose**: Display static informational content

**Use Cases**:
- Notices and tips
- Help content
- Information panels
- Announcements

**Parameters**:
```kotlin
InfoCard(
    title: String,                          // Required
    modifier: Modifier = Modifier,
    subtitle: String? = null,               // Optional
    description: String? = null,            // Optional
    icon: ImageVector? = null,              // Optional leading icon
    trailingIcon: ImageVector? = null,      // Optional trailing icon
    size: CardSizeVariant = Medium,
    shape: CardShapeVariant = Default,
    cardStyle: CardStyle? = null            // Defaults to elevated
)
```

**Example**:
```kotlin
InfoCard(
    title = "Welcome to Streakio",
    description = "Track your daily habits and build consistent streaks.",
    icon = Icons.Default.Info
)
```

---

### 2. ActionCard
**Purpose**: Clickable cards that trigger actions

**Use Cases**:
- Navigation cards
- Action triggers
- Interactive menu items
- Quick actions

**Parameters**:
```kotlin
ActionCard(
    title: String,                          // Required
    onClick: () -> Unit,                    // Required
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    description: String? = null,
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    size: CardSizeVariant = Medium,
    shape: CardShapeVariant = Default,
    cardStyle: CardStyle? = null            // Defaults to filled
)
```

**Example**:
```kotlin
ActionCard(
    title = "Create New Habit",
    description = "Start tracking a new habit today",
    icon = Icons.Default.Add,
    trailingIcon = Icons.Default.ChevronRight,
    onClick = { navController.navigate("create_habit") }
)
```

---

### 3. SelectCard ⭐
**Purpose**: Cards for selectable options (single or multi-select)

**Use Cases**:
- Choice selection
- Settings options
- Preference selection
- Multi-select lists
- **Profile settings (sleep hours, activity level, etc.)**

**Special Features**:
- All parameters are optional (title, description, icon can all be null)
- Supports both ImageVector icons and remote image URLs
- Automatically highlights selected state
- Perfect for the ProfileSettingScreen sleep hours use case

**Parameters**:
```kotlin
SelectCard(
    modifier: Modifier = Modifier,
    title: String? = null,                  // Optional - can be null
    description: String? = null,            // Optional - can be null
    icon: ImageVector? = null,              // Optional - vector icon
    iconUrl: String? = null,                // Optional - remote image URL
    iconSize: Dp = IconSize.ExtraLarge,
    iconTint: Color? = null,
    isSelected: Boolean = false,            // Selection state
    onClick: () -> Unit,                    // Required
    enabled: Boolean = true,
    size: CardSizeVariant = Medium,
    shape: CardShapeVariant = Default,
    cardStyle: CardStyle? = null            // Auto-styles based on selection
)
```

**Example 1 - Sleep Hours (with remote icon URLs)**:
```kotlin
var selectedOption by remember { mutableStateOf<Int?>(null) }

SelectCard(
    modifier = Modifier.fillMaxWidth(),
    title = "7-8 hours",
    description = "Recommended sleep",
    iconUrl = "https://streakio-supabase.pixamob.com/storage/v1/object/public/stickers/face-emojis/260205.png",
    isSelected = selectedOption == 0,
    onClick = { selectedOption = 0 }
)
```

**Example 2 - Theme Selection (with vector icons)**:
```kotlin
SelectCard(
    title = "Dark Mode",
    description = "Easy on the eyes",
    icon = Icons.Default.DarkMode,
    iconTint = AppTheme.colors.brandContentDefault,
    isSelected = selectedTheme == 1,
    onClick = { selectedTheme = 1 }
)
```

**Example 3 - Icon Only**:
```kotlin
SelectCard(
    icon = Icons.Default.Favorite,
    isSelected = selected,
    onClick = { selected = !selected }
)
```

---

### 4. MediaCard
**Purpose**: Cards with prominent media content

**Use Cases**:
- Gallery items
- Content previews
- Media libraries
- Article cards

**Parameters**:
```kotlin
MediaCard(
    imageUrl: String,                       // Required
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    description: String? = null,
    imageHeight: Dp = ComponentSize.Massive,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    size: CardSizeVariant = Medium,
    shape: CardShapeVariant = Default,
    cardStyle: CardStyle? = null            // Defaults to elevated
)
```

**Example**:
```kotlin
MediaCard(
    imageUrl = "https://example.com/image.jpg",
    title = "Habit Tracking Tips",
    subtitle = "Featured Article",
    description = "Learn the best practices for building habits.",
    onClick = { openArticle() }
)
```

---

### 5. StatCard
**Purpose**: Display statistics and metrics

**Use Cases**:
- Dashboard stats
- Analytics display
- Progress metrics
- KPI cards

**Parameters**:
```kotlin
StatCard(
    value: String,                          // Required - e.g., "42", "85%"
    label: String,                          // Required
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trend: String? = null,                  // e.g., "+12%"
    trendPositive: Boolean = true,          // Affects trend color
    onClick: (() -> Unit)? = null,
    size: CardSizeVariant = Medium,
    shape: CardShapeVariant = Default,
    cardStyle: CardStyle? = null            // Defaults to filled
)
```

**Example**:
```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
    StatCard(
        modifier = Modifier.weight(1f),
        value = "42",
        label = "Active Streaks",
        icon = Icons.Default.TrendingUp,
        trend = "+12%",
        trendPositive = true
    )
    
    StatCard(
        modifier = Modifier.weight(1f),
        value = "85%",
        label = "Completion Rate",
        trend = "-3%",
        trendPositive = false
    )
}
```

---

### 6. ListItemCard
**Purpose**: Cards for list items with consistent layout

**Use Cases**:
- Settings items
- Menu items
- Selectable lists
- Navigation lists

**Parameters**:
```kotlin
ListItemCard(
    title: String,                          // Required
    onClick: () -> Unit,                    // Required
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    leadingContent: (@Composable () -> Unit)? = null,  // Overrides icon
    trailingIcon: ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null, // Overrides icon
    enabled: Boolean = true,
    size: CardSizeVariant = Small,
    shape: CardShapeVariant = Default,
    cardStyle: CardStyle? = null            // Defaults to ghost
)
```

**Example**:
```kotlin
ListItemCard(
    title = "Notifications",
    subtitle = "Push, Email, SMS",
    leadingIcon = Icons.Default.Notifications,
    trailingIcon = Icons.Default.ChevronRight,
    onClick = { openNotificationSettings() }
)
```

---

### 7. FeatureCard
**Purpose**: Highlight features or benefits

**Use Cases**:
- Onboarding screens
- Feature tours
- Marketing content
- Benefits showcase

**Parameters**:
```kotlin
FeatureCard(
    title: String,                          // Required
    description: String,                    // Required
    icon: ImageVector,                      // Required
    modifier: Modifier = Modifier,
    iconBackgroundColor: Color? = null,
    iconTint: Color? = null,
    onClick: (() -> Unit)? = null,
    size: CardSizeVariant = Medium,
    shape: CardShapeVariant = Default,
    cardStyle: CardStyle? = null            // Defaults to outlined
)
```

**Example**:
```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
    FeatureCard(
        modifier = Modifier.weight(1f),
        title = "Track Daily",
        description = "Log your habits every day",
        icon = Icons.Default.CalendarToday
    )
    
    FeatureCard(
        modifier = Modifier.weight(1f),
        title = "View Progress",
        description = "Visualize your progress",
        icon = Icons.Default.BarChart
    )
}
```

---

### 8. CompactCard
**Purpose**: Small cards for compact layouts

**Use Cases**:
- Tags and chips
- Quick actions
- Compact lists
- Filter options

**Parameters**:
```kotlin
CompactCard(
    title: String,                          // Required
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: CardShapeVariant = Rounded,
    cardStyle: CardStyle? = null            // Defaults to filled
)
```

**Example**:
```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Small)) {
    CompactCard(
        title = "Health",
        icon = Icons.Default.FavoriteBorder
    )
    CompactCard(
        title = "Fitness",
        icon = Icons.Default.FitnessCenter
    )
}
```

---

### 9. SummaryCard
**Purpose**: Display grouped summary information

**Use Cases**:
- Overview panels
- Summary sections
- Data aggregation
- Reports

**Parameters**:
```kotlin
SummaryCard(
    title: String,                          // Required
    items: List<Pair<String, String>>,      // Required - label to value pairs
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    size: CardSizeVariant = Medium,
    shape: CardShapeVariant = Default,
    cardStyle: CardStyle? = null            // Defaults to elevated
)
```

**Example**:
```kotlin
SummaryCard(
    title = "Weekly Summary",
    icon = Icons.Default.CalendarMonth,
    items = listOf(
        "Total Habits" to "12",
        "Completed" to "10",
        "In Progress" to "2",
        "Completion Rate" to "83%"
    )
)
```

---

## Card Styles

All cards accept an optional `cardStyle` parameter for custom styling. You can use built-in styles or create custom ones.

### Built-in Styles

Access via `AppTheme.cards`:

```kotlin
// 1. Elevated - Card with shadow elevation
cardStyle = AppTheme.cards.elevated

// 2. Filled - Solid background card
cardStyle = AppTheme.cards.filled

// 3. Outlined - Card with border
cardStyle = AppTheme.cards.outlined

// 4. Ghost - Minimal card with subtle background
cardStyle = AppTheme.cards.ghost
```

### Custom Styles

Create custom card styles using `CardStyle`:

```kotlin
val customStyle = CardStyle(
    default = CardColors(
        background = Color.Blue,
        content = Color.White,
        border = Color.Transparent,
        shadow = Color.Black.copy(alpha = 0.12f),
        ripple = Color.White.copy(alpha = 0.12f)
    ),
    disabled = CardColors(
        background = Color.Blue.copy(alpha = 0.38f),
        content = Color.White.copy(alpha = 0.38f),
        border = Color.Transparent,
        shadow = Color.Transparent,
        ripple = Color.Transparent
    ),
    enableRipple = true
)

ActionCard(
    title = "Custom Card",
    onClick = { },
    cardStyle = customStyle
)
```

Or use the `CustomCard` component directly:

```kotlin
CustomCard(
    contentColor = Color.White,
    backgroundColor = Color.Blue,
    borderColor = Color.Transparent,
    onClick = { }
) {
    // Your content
}
```

---

## Size Variants

All cards support 5 size variants via `CardSizeVariant`:

- `Compact` - Minimal padding and small elements
- `Small` - Compact but readable
- `Medium` - Default, balanced size (recommended)
- `Large` - Spacious layout
- `Huge` - Maximum padding and large elements

**Example**:
```kotlin
ActionCard(
    title = "Compact",
    size = CardSizeVariant.Compact,
    onClick = { }
)

ActionCard(
    title = "Large",
    size = CardSizeVariant.Large,
    onClick = { }
)
```

---

## Shape Variants

All cards support 3 shape variants via `CardShapeVariant`:

- `Default` - Standard rounded corners based on size
- `Rounded` - Extra rounded corners (1.5x)
- `ExtraRounded` - Maximum rounded corners (2x)

**Example**:
```kotlin
InfoCard(
    title = "Default Shape",
    shape = CardShapeVariant.Default
)

InfoCard(
    title = "Extra Rounded",
    shape = CardShapeVariant.ExtraRounded
)
```

---

## Real-World Example: Profile Settings Page

Here's how to implement a complete profile settings page using `SelectCard`:

```kotlin
@Composable
fun ProfileSettingsScreen() {
    var selectedSleepHours by remember { mutableStateOf<Int?>(null) }
    var selectedActivity by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.Large),
        verticalArrangement = Arrangement.spacedBy(Spacing.ExtraLarge)
    ) {
        // Section 1: Sleep Hours
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
                Text(
                    text = "How many hours do you sleep?",
                    style = AppTheme.typography.headlineBold
                )
                
                Text(
                    text = "This helps us suggest optimal habit times",
                    style = AppTheme.typography.bodyRegular
                )

                // Sleep options with emoji icons
                listOf(
                    Triple(
                        "https://streakio-supabase.pixamob.com/storage/v1/object/public/stickers/face-emojis/260241.png",
                        "Less than 4 hours",
                        "Barely sleeping"
                    ),
                    Triple(
                        "https://streakio-supabase.pixamob.com/storage/v1/object/public/stickers/face-emojis/260230.png",
                        "4-6 hours",
                        "Not enough rest"
                    ),
                    Triple(
                        "https://streakio-supabase.pixamob.com/storage/v1/object/public/stickers/face-emojis/260205.png",
                        "7-8 hours",
                        "Recommended sleep"
                    ),
                    Triple(
                        "https://streakio-supabase.pixamob.com/storage/v1/object/public/stickers/face-emojis/260208.png",
                        "8-9 hours",
                        "Good sleep"
                    ),
                    Triple(
                        "https://streakio-supabase.pixamob.com/storage/v1/object/public/stickers/face-emojis/260237.png",
                        "More than 9 hours",
                        "Plenty of sleep"
                    )
                ).forEachIndexed { index, (iconUrl, title, description) ->
                    SelectCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = title,
                        description = description,
                        iconUrl = iconUrl,
                        isSelected = selectedSleepHours == index,
                        onClick = { selectedSleepHours = index }
                    )
                }
            }
        }

        // Section 2: Activity Level
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
                Text(
                    text = "What's your activity level?",
                    style = AppTheme.typography.headlineBold
                )

                listOf(
                    Triple(Icons.Default.DirectionsWalk, "Light", "Occasional activity"),
                    Triple(Icons.Default.DirectionsRun, "Moderate", "Regular exercise"),
                    Triple(Icons.Default.FitnessCenter, "Active", "Daily workouts")
                ).forEachIndexed { index, (icon, title, desc) ->
                    SelectCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = icon,
                        title = title,
                        description = desc,
                        isSelected = selectedActivity == index,
                        onClick = { selectedActivity = index }
                    )
                }
            }
        }
    }
}
```

---

## Best Practices

### 1. Choose the Right Card Type
- Use `InfoCard` for static information
- Use `ActionCard` for clickable actions
- Use `SelectCard` for selectable options
- Use `MediaCard` when image is the primary content
- Use `StatCard` for metrics and numbers
- Use `ListItemCard` for list-based layouts
- Use `FeatureCard` for showcasing features
- Use `CompactCard` for tags and chips
- Use `SummaryCard` for grouped data

### 2. Consistent Sizing
- Use `Medium` size for most cards (default)
- Use `Small` or `Compact` in tight spaces
- Use `Large` or `Huge` for emphasis or primary actions

### 3. Icon Usage
- Always provide icons for better visual hierarchy
- Use `iconUrl` for remote images (like emojis)
- Use `icon` for Material Icons
- Provide meaningful `contentDescription` for accessibility

### 4. Selection State
- For `SelectCard`, always manage selection state properly
- Use `isSelected` to highlight selected items
- Provide visual feedback on selection

### 5. Custom Styling
- Use built-in styles when possible
- Create custom styles for brand-specific cards
- Maintain consistency across similar cards

### 6. Performance
- Use `remember` for selection state
- Avoid unnecessary recompositions
- Use `LazyColumn` for long lists of cards

---

## Migration from Existing Code

If you have existing card implementations, here's how to migrate:

### Before (Custom Implementation):
```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(color)
        .clickable { onClick() }
        .padding(16.dp)
) {
    Row {
        Icon(icon, null)
        Text(title)
    }
}
```

### After (Using CardComponents):
```kotlin
ActionCard(
    title = title,
    icon = icon,
    onClick = onClick
)
```

---

## Related Files

- `CardComponents.kt` - Main card components (this documentation)
- `CardComponentsExamples.kt` - Usage examples and demos
- `ui/elements/cards/BaseCard.kt` - Base card implementation
- `ui/elements/cards/Cards.kt` - Card variants (Elevated, Filled, etc.)
- `ui/elements/cards/CardTheme.kt` - Card theme configuration

---

## Support

For questions or issues:
1. Check the examples in `CardComponentsExamples.kt`
2. Review the existing card elements in `ui/elements/cards/`
3. Refer to the project's copilot instructions
4. Follow the theme-driven architecture guidelines

---

**Last Updated**: December 2025  
**Project**: Streakio Habit Tracker KMP  
**Maintainer**: Development Team

