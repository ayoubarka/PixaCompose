# PixaCompose Documentation

## Overview

PixaCompose is a production-ready Compose Multiplatform UI library providing 40+ customizable components for mobile apps (Android & iOS). Built on Material 3 principles with a comprehensive theme system, it offers buttons, inputs, navigation, overlays, feedback, and display components. Each component is mobile-optimized (44dp touch targets), accessibility-ready (WCAG compliant), and fully themeable through `AppTheme`. The library uses single-file component architecture for easy maintenance and includes smooth animations via `AnimationUtils`.

## Setup

### Installation
```kotlin
// build.gradle.kts (commonMain)
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.pixamob.pixacompose:pixacompose:1.0.0")
        }
    }
}
```

### Basic Usage
```kotlin
@Composable
fun App() {
    AppTheme {
        BaseButton(text = "Click Me", onClick = { })
    }
}
```

## Theme Customization

### Colors
```kotlin
AppTheme(
    lightColorPalette = ColorPalette(
        brandContentDefault = Color(0xFF007AFF),
        baseSurfaceDefault = Color.White
    ),
    darkColorPalette = ColorPalette(/* dark colors */)
)
```

### Typography
```kotlin
AppTheme(fontFamily = myCustomFont)
```

### Accessing Theme
```kotlin
val colors = AppTheme.colors
val typography = AppTheme.typography
```

## Components

### Actions

**BaseButton** - Primary interaction component  
*Params*: `text`, `onClick`, `variant` (Solid/Outlined/Ghost/Text), `size`, `isDestructive`  
`BaseButton(text = "Submit", onClick = { }, variant = ButtonVariant.Solid)`

**IconButton** - Icon-only button  
*Params*: `icon`, `onClick`, `size`, `enabled`  
`IconButton(icon = checkIcon, onClick = { })`

**Chip** - Compact tag/filter  
*Params*: `label`, `onClick`, `style` (Solid/Outlined/Subtle), `selected`  
`Chip(label = "Filter", onClick = { }, selected = true)`

**Tab** - Navigation tab  
*Params*: `title`, `icon`, `selected`, `onClick`  
`Tab(title = "Home", icon = homeIcon, selected = true, onClick = { })`

### Inputs

**TextField** - Single-line input  
*Params*: `value`, `onValueChange`, `label`, `error`, `supportingText`  
`TextField(value = text, onValueChange = { text = it }, label = "Name")`

**TextArea** - Multi-line input  
*Params*: `value`, `onValueChange`, `label`, `maxLines`  
`TextArea(value = bio, onValueChange = { bio = it }, maxLines = 5)`

**Checkbox** - Boolean selection  
*Params*: `checked`, `onCheckedChange`, `label`  
`Checkbox(checked = agree, onCheckedChange = { agree = it }, label = "I agree")`

**RadioButton** - Single choice  
*Params*: `selected`, `onClick`, `label`, `group`  
`RadioButton(selected = option == 1, onClick = { option = 1 }, label = "Option 1")`

**Switch** - Toggle  
*Params*: `checked`, `onCheckedChange`, `label`  
`Switch(checked = enabled, onCheckedChange = { enabled = it })`

**Slider** - Range selection  
*Params*: `value`, `onValueChange`, `valueRange`, `label`  
`Slider(value = volume, onValueChange = { volume = it }, valueRange = 0f..100f)`

### Navigation

**BaseTopNavBar** - Top app bar  
*Params*: `title`, `subtitle`, `startActions`, `endActions`, `profileImageUrl`, `elevation`  
`BaseTopNavBar(title = "Home", startActions = listOf(TopNavAction(menuIcon, "Menu", { })))`

**BottomNavBar** - Bottom navigation  
*Params*: `items`, `selectedIndex`, `onItemSelected`, `withCenterAction`  
`BottomNavBar(items = navItems, selectedIndex = 0, onItemSelected = { })`

**Drawer** - Side drawer  
*Params*: `drawerContent`, `content`, `drawerState`  
`Drawer(drawerContent = { NavMenu() }, content = { Screen() })`

**TabBar** - Horizontal tabs  
*Params*: `tabs`, `selectedIndex`, `onTabSelected`  
`TabBar(tabs = listOf("Tab 1", "Tab 2"), selectedIndex = 0, onTabSelected = { })`

**Stepper** - Progress indicator  
*Params*: `steps`, `currentStep`, `onStepClick`  
`Stepper(steps = 4, currentStep = 2, onStepClick = { })`

### Feedback

**Badge** - Notification indicator  
*Params*: `content`, `variant` (Primary/Success/Warning/Error), `size` (Dot/Small/Medium/Large)  
`Badge(content = "5", variant = BadgeVariant.Error, size = BadgeSize.Small)`

**Toast** - Temporary notification  
*Params*: `message`, `duration`, `type`  
`Toast.show(message = "Saved!", type = ToastType.Success)`

**ProgressIndicator** - Loading state  
*Params*: `progress`, `type` (Linear/Circular)  
`ProgressIndicator(progress = 0.5f, type = ProgressType.Linear)`

**Skeleton** - Loading placeholder  
*Params*: `width`, `height`, `shape`  
`Skeleton(width = 200.dp, height = 20.dp, shape = RoundedCornerShape(4.dp))`

**EmptyState** - No content  
*Params*: `title`, `description`, `icon`, `action`  
`EmptyState(title = "No Items", description = "Add your first item", icon = emptyIcon)`

### Overlays

**BaseBottomSheet** - Bottom sheet  
*Params*: `onDismissRequest`, `size` (Compact/Standard/Expanded/Full), `style`, `elevated`  
`BaseBottomSheet(onDismissRequest = { }, size = BottomSheetSizeVariant.Standard) { Content() }`

**SelectOptionBottomSheet** - Trigger + content  
*Params*: `trigger`, `content`  
`SelectOptionBottomSheet(trigger = { show -> Button("Select", { show() }) }, content = { dismiss -> Options() })`

**ListBottomSheet** - Scrollable list  
*Params*: `title`, `items`, `onItemSelected`, `itemContent`  
`ListBottomSheet(title = "Select", items = countries, onItemSelected = { }) { item, select -> Text(item) }`

**ConfirmationBottomSheet** - Confirm action  
*Params*: `title`, `message`, `confirmText`, `onConfirm`, `isDestructive`  
`ConfirmationBottomSheet(title = "Delete?", message = "Cannot undo", confirmText = "Delete", onConfirm = { })`

**Dialog** - Modal dialog  
*Params*: `onDismissRequest`, `title`, `content`  
`Dialog(onDismissRequest = { }, title = "Alert") { Text("Message") }`

**AlertDialog** - Confirmation dialog  
*Params*: `title`, `message`, `confirmText`, `dismissText`, `onConfirm`  
`AlertDialog(title = "Confirm?", message = "Are you sure?", confirmText = "Yes", onConfirm = { })`

### Display

**BaseCard** - Content container  
*Params*: `variant` (Elevated/Outlined/Filled/Ghost), `elevation`, `padding`  
`BaseCard(variant = BaseCardVariant.Elevated, elevation = BaseCardElevation.Medium) { Content() }`

**Avatar** - Profile image  
*Params*: `imageUrl`, `size` (Tiny/Small/Medium/Large/Huge/Massive), `onClick`  
`Avatar(imageUrl = "https://...", size = AvatarSize.Medium, onClick = { })`

**Icon** - Vector icon  
*Params*: `painter`, `contentDescription`, `tint`, `size`  
`Icon(painter = checkIcon, contentDescription = "Check", tint = Color.Green)`

**Divider** - Visual separator  
*Params*: `thickness`, `color`, `orientation`  
`HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.baseBorderSubtle)`

**Image** - Image display  
*Params*: `imageUrl`, `contentDescription`, `contentScale`, `placeholder`  
`Image(imageUrl = "https://...", contentDescription = "Photo", contentScale = ContentScale.Crop)`

## Best Practices

1. **Always wrap your app with `AppTheme`** for consistent theming
2. **Use size variants** (Small/Medium/Large) instead of custom dimensions
3. **Provide content descriptions** for accessibility
4. **Leverage convenience functions** (e.g., `BackTopNavBar`, `ProfileTopNavBar`)
5. **Use AppTheme colors/typography** instead of hardcoded values
6. **Enable safe area padding** for iOS/Android notches (where applicable)
7. **Prefer BaseButton over Material Button** to avoid naming conflicts
8. **Use badges for notifications** on TopNavBar actions and BottomNavBar items
9. **Apply touch-friendly sizes** - minimum 44dp for interactive elements
10. **Test in light and dark modes** - all components are theme-aware

## Migration from Material 3

- `Button` → `BaseButton`
- `Card` → `BaseCard`
- `TopAppBar` → `BaseTopNavBar`
- `BottomSheet` → `BaseBottomSheet`
- Access colors via `AppTheme.colors` not `MaterialTheme.colorScheme`
- Access typography via `AppTheme.typography` not `MaterialTheme.typography`

## Support

- **GitHub**: [pixamob/pixacompose](https://github.com/pixamob/pixacompose)
- **Issues**: Report bugs and request features
- **Discussions**: Community help and questions

