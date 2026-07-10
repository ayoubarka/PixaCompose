# PixaCompose

**A comprehensive Compose Multiplatform UI library** with 46+ production-ready components for Android and iOS applications.

[![Maven Central](https://img.shields.io/maven-central/v/com.pixamob/pixacompose)](https://central.sonatype.com/artifact/com.pixamob/pixacompose)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-purple.svg)](https://kotlinlang.org)

---

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Architecture](#architecture)
- [Theme System](#theme-system)
  - [Colors](#colors)
  - [Typography](#typography)
  - [Shapes](#shapes)
  - [Sizing System](#sizing-system)
  - [Theme Customization](#theme-customization)
- [Component Reference](#component-reference)
  - [Actions](#actions)
  - [Inputs](#inputs)
  - [Display](#display)
  - [Feedback](#feedback)
  - [Navigation](#navigation)
  - [Overlay](#overlay)
- [Global Feedback System](#global-feedback-system)
  - [Toast](#toast)
  - [Snackbar](#snackbar)
- [Utilities](#utilities)
- [Common UI Patterns](#common-ui-patterns)
- [Best Practices](#best-practices)
- [Contributing](#contributing)
- [Changelog](#changelog)
- [License](#license)

---

## Features

- **48+ Components** — Buttons, Cards, TextFields, DatePicker, Slider, Charts, and more
- **Multiplatform** — Android (24+) and iOS (13+) via Compose Multiplatform
- **Theme System** — Centralized colors (71 semantic color properties), typography (27 text styles), shapes, and dimensions
- **Light/Dark Mode** — Automatic theme switching via `PixaTheme(useDarkTheme = isSystemInDarkTheme())`
- **Type-Safe Variants** — Enums for variants, sizes, shapes (never string-based)
- **Accessibility** — Full semantic roles and content descriptions on all components
- **Loading States** — Skeleton loading built into components
- **Animations** — Spring-based physics animations via `PixaAnimationSpecs` (indicatorSpring, selectionSpring, thumbSpring, colorSpring, fastSpring, slowSpring) + legacy tween utilities in AnimationUtils
- **Global Managers** — Toast and Snackbar accessible from ViewModel/UseCase/Composable via singletons
- **No Material 3 Wrappers** — Built from Compose primitives (Box, Row, Column, Canvas)

---

## Installation

### Kotlin Multiplatform

```kotlin
commonMain.dependencies {
    implementation("com.pixamob:pixacompose:1.1.1")
}
```

### Android Only

```kotlin
dependencies {
    implementation("com.pixamob:pixacompose:1.1.1")
}
```

### Requirements

| Platform | Minimum |
|----------|---------|
| Android | API 24+ |
| iOS | 13+ |
| Kotlin | 2.3.x |
| Compose Multiplatform | 1.10.x |

### Exposed Dependencies

These libraries are exposed as `api` and available to consumers:

- **Coil 3** (3.4.0) — `coil-compose`, `coil-network-ktor3`, `coil-svg`
- **Vico** (2.4.3) — Multiplatform charts

---

## Quick Start

Wrap your app in `PixaTheme` and use any component:

```kotlin
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import com.pixamob.pixacompose.theme.PixaTheme
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.actions.ButtonVariant
import com.pixamob.pixacompose.components.inputs.PixaTextField
import com.pixamob.pixacompose.components.inputs.TextFieldVariant
import com.pixamob.pixacompose.components.display.InfoCard

@Composable
fun MyApp() {
    PixaTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCard(
                title = "Welcome to PixaCompose",
                description = "Build beautiful UIs with ready-to-use components"
            )

            var text by remember { mutableStateOf("") }
            PixaTextField(
                value = text,
                onValueChange = { text = it },
                label = "Email",
                variant = TextFieldVariant.Outlined
            )

            PixaButton(
                text = "Get Started",
                onClick = { /* handle click */ }
            )
        }
    }
}
```

---

## Architecture

### Project Structure

```
library/src/commonMain/kotlin/com/pixamob/pixacompose/
├── theme/           # Centralized styling
│   ├── Color.kt           # Color palettes (71 semantic colors)
│   ├── Typography.kt      # 27 text styles
│   ├── PixaTheme.kt       # Theme provider + AppTheme accessor
│   ├── Dimen.kt           # Hierarchical sizing system
│   ├── CustomShapes.kt    # Decorative shapes (Concave, Wave, Bubble, etc.)
│   └── ShapeStyle.kt      # Shape style configuration
├── components/      # 46+ components by category
│   ├── actions/     # Button, Chip, Accordion, Tab, PixaIconButton, PixaFAB
│   ├── inputs/      # TextField, TextArea, SearchBar, Slider, Switch, Checkbox, RadioButton, Dropdown, DatePicker, TimePicker, ColorPicker
│   ├── display/     # Card, Avatar, Icon, Image, Chart, Divider
│   ├── feedback/    # Alert, Toast, Snackbar, Badge, Skeleton, Indicator, EmptyState
│   ├── navigation/  # TopNavBar, BottomNavBar, TabBar, Drawer, Stepper
│   └── overlay/     # Dialog, BottomSheet, Menu, Popover, Tooltip
└── utils/           # Helper utilities
    ├── AnimationSpecs.kt     # Spring physics specs (PixaAnimationSpecs)
    ├── AnimationUtils.kt     # Legacy tween/spring utilities
    ├── ColorUtils.kt
    ├── DateTimeUtils.kt
    ├── ElevationUtils.kt
    └── ScreenUtil.kt
```

### Component File Pattern

Every component follows this structure:

1. **Configuration** — Enums, data classes for variants/sizes/colors
2. **Theme Provider** — Maps variant to theme colors
3. **Base Component** — Internal implementation using Box/Row/Column
4. **Public API** — Exported composable with full parameters
5. **Convenience Variants** — Shortcuts (e.g., `FilledButton`, `OutlinedTextField`)

---

## Theme System

### PixaTheme

The root composable that provides colors, typography, and shapes to the entire component tree:

```kotlin
@Composable
fun PixaTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    colorScales: ColorScales? = null,
    colorOverrides: ColorOverrides? = null,
    fontFamily: FontFamilyConfig? = null,
    content: @Composable () -> Unit
)
```

### AppTheme

Access theme values anywhere inside `PixaTheme`:

```kotlin
@Composable
fun MyComponent() {
    val brandColor = AppTheme.colors.brandContentDefault
    val titleStyle = AppTheme.typography.titleBold
    val shape = AppTheme.shapes.rounded
    val isDark = AppTheme.isDarkTheme
}
```

### Color Overrides

Override individual semantic colors without redefining the entire palette. `ColorOverrides` accepts any subset of the 71 `ColorPalette` properties — only the fields you set are applied, the rest fall back to the base palette:

```kotlin
PixaTheme(
    colorOverrides = ColorOverrides(
        brandContentDefault = Color(0xFF0066FF),
        errorContentDefault = Color(0xFFFF3B30),
        baseContentTitle = Color(0xFF111111)
    )
) {
    // All components reflect the overrides; everything else stays default
}
```

This is the recommended way to brand the library. Use `ColorScales` only when you need to redefine an entire color scale (50-950 ramp). Use `ColorOverrides` when you only need to change specific semantic tokens.

### Colors

The `ColorPalette` provides 71 semantic color properties organized in 7 groups:

| Group | Properties | Purpose |
|-------|-----------|---------|
| **Brand** | 9 (`surfaceSubtle`, `surfaceDefault`, `surfaceFocus`, `borderSubtle`, `borderDefault`, `borderFocus`, `contentSubtle`, `contentDefault`, `contentFocus`) | Primary brand identity |
| **Accent** | 9 (same structure) | Secondary/accent identity |
| **Base** | 17 (subtle/default/elevated/focus/shadow/disabled surfaces, subtle/default/focus/disabled borders, title/subtitle/body/caption/hint/negative/disabled content) | Neutral UI foundation |
| **Info** | 9 (same as Brand) | Informational states |
| **Success** | 9 (same as Brand) | Success states |
| **Warning** | 9 (same as Brand) | Warning states |
| **Error** | 9 (same as Brand) | Error states |

Key access patterns:

```kotlin
// Surfaces (backgrounds)
AppTheme.colors.baseSurfaceDefault          // Main background
AppTheme.colors.baseSurfaceElevated         // Elevated surfaces (cards, dialogs)
AppTheme.colors.brandSurfaceDefault         // Brand backgrounds

// Content (text, icons)
AppTheme.colors.baseContentTitle            // Primary text
AppTheme.colors.baseContentBody             // Body text
AppTheme.colors.baseContentCaption          // Secondary/helper text
AppTheme.colors.baseContentHint             // Placeholder text
AppTheme.colors.baseContentDisabled         // Disabled content
AppTheme.colors.brandContentDefault         // Primary brand text

// Borders
AppTheme.colors.baseBorderDefault           // Default borders
AppTheme.colors.baseBorderFocus             // Focused borders
AppTheme.colors.baseBorderDisabled          // Disabled borders

// Semantic
AppTheme.colors.infoContentDefault          // Info text
AppTheme.colors.successContentDefault       // Success text
AppTheme.colors.warningContentDefault       // Warning text
AppTheme.colors.errorContentDefault         // Error text
```

### Typography

The `TextTypography` provides 27 text styles across 9 categories:

```kotlin
// Display — Large headers (hero sections)
AppTheme.typography.displayLarge      // 64sp
AppTheme.typography.displayMedium     // 52sp
AppTheme.typography.displaySmall      // 40sp

// Header
AppTheme.typography.headerBold        // 32sp Bold
AppTheme.typography.headerRegular     // 32sp

// Headline
AppTheme.typography.headlineBold      // 24sp Bold
AppTheme.typography.headlineRegular   // 24sp

// Title
AppTheme.typography.titleBold         // 20sp Bold
AppTheme.typography.titleRegular      // 20sp
AppTheme.typography.titleLight        // 20sp Light

// Subtitle
AppTheme.typography.subtitleBold      // 18sp Bold
AppTheme.typography.subtitleRegular   // 18sp
AppTheme.typography.subtitleLight     // 18sp Light

// Body — Primary content text
AppTheme.typography.bodyBold          // 16sp Bold
AppTheme.typography.bodyRegular       // 16sp (base)
AppTheme.typography.bodyLight         // 16sp Light

// Caption — Secondary/smaller text
AppTheme.typography.captionBold       // 14sp Bold
AppTheme.typography.captionRegular    // 14sp
AppTheme.typography.captionLight      // 14sp Light

// Footnote — Small print
AppTheme.typography.footnoteBold      // 12sp Bold
AppTheme.typography.footnoteRegular   // 12sp

// Overline — Section labels
AppTheme.typography.overline          // 12sp, uppercase

// Label — Form labels, chip text
AppTheme.typography.labelLarge        // 14sp
AppTheme.typography.labelMedium       // 12sp
AppTheme.typography.labelSmall        // 10sp

// Action — Button text (per size variant)
AppTheme.typography.actionMini        // 10sp
AppTheme.typography.actionSmall       // 12sp
AppTheme.typography.actionMedium      // 14sp
AppTheme.typography.actionLarge       // 16sp
AppTheme.typography.actionHuge        // 24sp
```

### Font Customization

```kotlin
@Composable
fun App() {
    val customFont = FontFamilyConfig(
        thin = Font(R.font.inter_thin),
        extraLight = Font(R.font.inter_extra_light),
        light = Font(R.font.inter_light),
        regular = Font(R.font.inter_regular),
        medium = Font(R.font.inter_medium),
        semiBold = Font(R.font.inter_semi_bold),
        bold = Font(R.font.inter_bold),
        extraBold = Font(R.font.inter_extra_bold),
        black = Font(R.font.inter_black)
    )

    PixaTheme(fontFamily = customFont) {
        MyApp()
    }
}
```

### Shapes

Access via `AppTheme.shapes`:

```kotlin
// Rounded shapes
AppTheme.shapes.rounded.extraSmall     // 4dp
AppTheme.shapes.rounded.small          // 8dp
AppTheme.shapes.rounded.medium         // 12dp
AppTheme.shapes.rounded.large          // 16dp
AppTheme.shapes.rounded.extraLarge     // 24dp

// Cut/corner shapes (same sizes)
AppTheme.shapes.cut.small

// Decorative shapes
AppTheme.shapes.concaveTop             // Inward curve
AppTheme.shapes.convexTop              // Outward bulge
AppTheme.shapes.wave                   // Sine wave edge
AppTheme.shapes.archTop                // Dome arch
AppTheme.shapes.tab                    // Browser-style tab
AppTheme.shapes.notchRounded           // Rounded notch
AppTheme.shapes.notchSharp             // Sharp notch
AppTheme.shapes.bubbleLeft             // Chat bubble (left tail)
AppTheme.shapes.bubbleRight            // Chat bubble (right tail)
```

### Sizing System

PixaCompose uses a hierarchical sizing system via `SizeVariant`:

```kotlin
enum class SizeVariant { None, Nano, Compact, Small, Medium, Large, Huge, Massive }
```

Access sizes via `HierarchicalSize`:

```kotlin
// Container sizes
HierarchicalSize.Container.forVariant(SizeVariant.Medium)   // 48dp

// Icon sizes
HierarchicalSize.Icon.forVariant(SizeVariant.Medium)        // 24dp

// Spacing
HierarchicalSize.Spacing.Small    // 8dp
HierarchicalSize.Spacing.Medium   // 16dp
HierarchicalSize.Spacing.Large    // 24dp

// Touch targets (minimum 44dp for accessibility)
HierarchicalSize.TouchTarget.Medium  // 48dp
```

### Theme Customization

Override specific color scales:

```kotlin
PixaTheme(
    useDarkTheme = false,
    colorScales = ColorScales(
        brand = mapOf(
            500 to Color(0xFF0284C7),    // Primary brand
            600 to Color(0xFF0369A1),
            700 to Color(0xFF075985)
        ),
        success = mapOf(
            500 to Color(0xFF059669)
        )
    )
) {
    MyApp()
}
```

---

## Component Reference

### Actions

#### PixaButton

**Import**: `com.pixamob.pixacompose.components.actions.PixaButton`
**File**: `components/actions/Button.kt`

**Variants**:
| Variant | Description |
|---------|-------------|
| `ButtonVariant.Filled` | Filled background (primary actions) |
| `ButtonVariant.Tonal` | Subtle tonal background |
| `ButtonVariant.Outlined` | Border only (secondary actions) |
| `ButtonVariant.Ghost` | Transparent (tertiary actions) |

**Sizes**: `SizeVariant.Nano`, `Compact`, `Small`, `Medium` (default), `Large`, `Huge`, `Massive`

**Width Policies**:
| Policy | Description |
|--------|-------------|
| `ButtonWidthPolicy.Flexible` | Auto-width with fixed horizontal padding (default) |
| `ButtonWidthPolicy.Fixed` | No fixed padding, width set by caller Modifier |
| `ButtonWidthPolicy.FullBleed` | `fillMaxWidth`, forces 0dp corner radius |

**Shapes**:
| Shape | Description |
|-------|-------------|
| `ButtonShape.Default` | Rounded corners |
| `ButtonShape.Pill` | Fully rounded (pill shape) |
| `ButtonShape.Circle` | Perfect circle (icon-only buttons) |

**Key Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `text` | `String?` | `null` | Button label text |
| `onClick` | `() -> Unit` | required | Click handler |
| `variant` | `ButtonVariant` | `Filled` | Visual style |
| `size` | `SizeVariant` | `Medium` | Size preset |
| `shape` | `ButtonShape` | `Default` | Corner style |
| `widthPolicy` | `ButtonWidthPolicy` | `Flexible` | Layout width behavior |
| `selected` | `Boolean` | `false` | Selected state styling |
| `enabled` | `Boolean` | `true` | Interactive state |
| `loading` | `Boolean` | `false` | Show loading spinner |
| `showSkeleton` | `Boolean` | `false` | Show skeleton placeholder |
| `isDestructive` | `Boolean` | `false` | Destructive action styling |
| `leadingIcon` | `Painter?` | `null` | Icon before text |
| `trailingIcon` | `Painter?` | `null` | Icon after text |

**Examples**:
```kotlin
// Primary solid button
PixaButton(
    text = "Submit",
    onClick = { submitForm() },
            variant = ButtonVariant.Filled,
    size = SizeVariant.Large,
    enabled = isValid,
    loading = isSubmitting
)

// Ghost icon button (circle)
PixaButton(
    onClick = { openMenu() },
    variant = ButtonVariant.Ghost,
    shape = ButtonShape.Circle,
    icon = painterResource(Res.drawable.ic_menu)
)

// Destructive outlined
PixaButton(
    text = "Delete",
    onClick = { deleteItem() },
    variant = ButtonVariant.Outlined,
    isDestructive = true
)
```

#### PixaChip

**Import**: `com.pixamob.pixacompose.components.actions.PixaChip`
**File**: `components/actions/Chip.kt`

**Variants**: `ChipVariant.Filled`, `Tonal`, `Outlined`, `Ghost`

**Types**: `ChipType.Static`, `Selectable`, `Dismissible`, `Input`

**Sizes**: `SizeVariant.Compact`, `Small`, `Medium` (default), `Large`

**Key Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `label` | `String` | required | Chip text |
| `selected` | `Boolean` | required | Selected state |
| `onClick` | `() -> Unit` | required | Click handler |
| `type` | `ChipType` | required | Behavior type |
| `variant` | `ChipVariant` | `Filled` | Visual style |
| `size` | `SizeVariant` | `Medium` | Size preset |
| `leadingIcon` | `Painter?` | `null` | Icon before text |
| `trailingIcon` | `Painter?` | `null` | Decorative icon after text (not shown with Dismissible) |
| `onDismiss` | `(() -> Unit)?` | `null` | Dismiss callback (for Dismissible) |

**Example**:
```kotlin
var selectedTags by remember { mutableStateOf(setOf<String>()) }
val tags = listOf("Kotlin", "Compose", "Android")

FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    tags.forEach { tag ->
        PixaChip(
            label = tag,
            selected = tag in selectedTags,
            onClick = {
                selectedTags = if (tag in selectedTags) selectedTags - tag
                else selectedTags + tag
            },
            type = ChipType.Selectable,
            variant = ChipVariant.Tonal
        )
    }
}
```

#### PixaIconButton

**Import**: `com.pixamob.pixacompose.components.actions.PixaIconButton`
**File**: `components/actions/PixaIconButton.kt`

A circular icon button with optional label, supporting multiple visual variants.

**Variants**: `IconButtonVariant.Filled`, `Outlined`, `Ghost`, `Tonal`

**Sizes**: `SizeVariant.Small` (36dp), `Medium` (44dp, default), `Large` (52dp)

**Key Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `icon` | `Painter` | required | Icon painter |
| `onClick` | `() -> Unit` | required | Click handler |
| `variant` | `IconButtonVariant` | `Ghost` | Visual style |
| `size` | `SizeVariant` | `Medium` | Size preset |
| `label` | `String?` | `null` | Label text below icon |
| `selected` | `Boolean` | `false` | Selected state styling |
| `enabled` | `Boolean` | `true` | Interactive state |
| `colors` | `IconButtonColors` | `IconButtonColors()` | Custom color overrides |
| `contentDescription` | `String?` | `null` | Accessibility description |

**Variants color mapping**:
| Variant | Container | Content | Border |
|---------|-----------|---------|--------|
| Filled | `brandSurfaceDefault` | `brandContentDefault` | Transparent |
| Outlined | Transparent | `brandContentDefault` | `brandBorderDefault` |
| Ghost | Transparent | `brandContentDefault` | None |
| Tonal | `brandSurfaceSubtle` | `brandContentDefault` | Transparent |

**Selected state** (all variants): Container → `brandSurfaceFocus`, Icon → `brandContentDefault`

**Convenience Variants**: `FilledIconButton(icon, onClick, size, modifier)`, `OutlinedIconButton(...)`, `GhostIconButton(...)`

```kotlin
PixaIconButton(
    icon = painterResource(Res.drawable.ic_edit),
    onClick = { editItem() },
    variant = IconButtonVariant.Filled,
    contentDescription = "Edit"
)
```

#### PixaFAB

**Import**: `com.pixamob.pixacompose.components.actions.PixaFAB`
**File**: `components/actions/PixaFAB.kt`

Floating Action Button for primary actions, supporting mini, standard, extended, and large variants.

**Variants**: `FABVariant.Filled`, `Tonal`, `Outlined`

**Sizes**: `SizeVariant.Medium` (48dp mini), `Large` (56dp standard, default), `Huge` (96dp large)

**Key Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `icon` | `Painter` | required | Icon painter |
| `onClick` | `() -> Unit` | required | Click handler |
| `label` | `String?` | `null` | Label text (extended FAB when set) |
| `size` | `SizeVariant` | `Large` | Size preset |
| `variant` | `FABVariant` | `Filled` | Visual style |
| `enabled` | `Boolean` | `true` | Interactive state |
| `colors` | `FABColors` | `FABColors()` | Custom color overrides |
| `contentDescription` | `String?` | `null` | Accessibility description |

**Extended FAB**: When `label` is not null, the FAB expands horizontally with an animated label to the right of the icon. Padding: 16dp left, 20dp right.

**Convenience Variants**: `MiniFAB(icon, onClick, modifier)`, `StandardFAB(icon, onClick, modifier)`, `ExtendedFAB(icon, label, onClick, modifier)`

```kotlin
// Standard FAB
PixaFAB(
    icon = painterResource(Res.drawable.ic_add),
    onClick = { addItem() }
)

// Extended FAB with label
PixaFAB(
    icon = painterResource(Res.drawable.ic_create),
    label = "Compose",
    onClick = { composeMessage() }
)
```

#### PixaAccordion

**Import**: `com.pixamob.pixacompose.components.actions.PixaAccordion`
**File**: `components/actions/Accordion.kt`

Collapsible content sections with group support.

```kotlin
PixaAccordion(
    title = "Advanced Settings",
    expanded = isExpanded,
    onToggle = { isExpanded = !isExpanded }
) {
    // Expanded content
    AdvancedSettingsContent()
}

// Grouped accordions
PixaAccordionGroup(
    items = listOf(
        AccordionItem("Section 1", { Section1Content() }),
        AccordionItem("Section 2", { Section2Content() })
    )
)
```

#### PixaTab

**Import**: `com.pixamob.pixacompose.components.actions.PixaTab`
**File**: `components/actions/Tab.kt`

Multiple tab variants including segmented tabs, scrollable tabs, and vertical tabs.

```kotlin
// Default tabs
Tabs(
    tabs = listOf("Overview", "Details", "Reviews"),
    selectedIndex = selectedTab,
    onTabSelected = { selectedTab = it }
)

// Segmented tabs
SegmentedTabs(
    tabs = listOf("Day", "Week", "Month"),
    selectedIndex = selectedSegment,
    onTabSelected = { selectedSegment = it }
)

// Vertical tabs
VerticalTabs(
    tabs = listOf("Profile", "Settings", "Help"),
    selectedIndex = selectedVerticalTab,
    onTabSelected = { selectedVerticalTab = it }
)
```

**Animations**: Background, content, and border color transitions use `colorSpring`; indicator color transitions use `colorSpring` for a smooth fade between transparent and the selected indicator color.

---

### Inputs

#### PixaTextField

**Import**: `com.pixamob.pixacompose.components.inputs.PixaTextField`
**File**: `components/inputs/TextField.kt`

**Variants**: `TextFieldVariant.Filled`, `Outlined` (default), `Ghost`

**Sizes**: `SizeVariant.Small` (36dp), `Medium` (44dp, default), `Large` (52dp)

**Key Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | required | Current text |
| `onValueChange` | `(String) -> Unit` | required | Text change callback |
| `variant` | `TextFieldVariant` | `Outlined` | Visual style |
| `size` | `SizeVariant` | `Medium` | Height preset |
| `enabled` | `Boolean` | `true` | Enabled state |
| `readOnly` | `Boolean` | `false` | Read-only mode |
| `isError` | `Boolean` | `false` | Error state |
| `label` | `String?` | `null` | Label above field |
| `placeholder` | `String?` | `null` | Hint when empty |
| `helperText` | `String?` | `null` | Helper message |
| `errorText` | `String?` | `null` | Error message |
| `leadingIcon` | `Painter?` | `null` | Icon at start |
| `trailingIcon` | `Painter?` | `null` | Icon at end |
| `onClear` | `(() -> Unit)?` | `null` | Clear callback — shows ✕ icon when text is non-empty |
| `visualTransformation` | `VisualTransformation` | `None` | Password, etc. |
| `keyboardOptions` | `KeyboardOptions` | `Default` | Keyboard config |
| `singleLine` | `Boolean` | `true` | Single line |
| `maxLength` | `Int?` | `null` | Max characters |

**Example**:
```kotlin
var email by remember { mutableStateOf("") }

PixaTextField(
    value = email,
    onValueChange = { email = it },
    label = "Email",
    placeholder = "you@example.com",
    variant = TextFieldVariant.Outlined,
    size = SizeVariant.Medium,
    isError = !email.contains("@"),
    errorText = "Invalid email",
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next
    )
)
```

**Convenience Variants**:
```kotlin
EmailTextField(value = email, onValueChange = { email = it })
PasswordTextField(value = password, onValueChange = { password = it })
SearchTextField(value = query, onValueChange = { query = it }, onSearch = { search() })
```

#### PixaTextArea

**Import**: `com.pixamob.pixacompose.components.inputs.PixaTextArea`
**File**: `components/inputs/TextArea.kt`

Same parameters as TextField plus:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `minLines` | `Int` | `3` | Minimum visible lines |
| `maxLines` | `Int` | `Int.MAX_VALUE` | Max lines before scroll |

**Auto-height**: Height grows from `minLines` up to `maxLines`, then scrolls. Default minLines=3, maxLines unconstrained.

```kotlin
var notes by remember { mutableStateOf("") }

PixaTextArea(
    value = notes,
    onValueChange = { notes = it },
    label = "Notes",
    placeholder = "Enter your notes...",
    minLines = 4,
    maxLines = 10,
    maxLength = 500
)
```

**Convenience Variants**: `CommentTextArea`, `BioTextArea`, `NoteTextArea`

#### PixaSearchBar

**Import**: `com.pixamob.pixacompose.components.inputs.PixaSearchBar`
**File**: `components/inputs/SearchBar.kt`

Search-specific input with built-in search icon.

```kotlin
var query by remember { mutableStateOf("") }

PixaSearchBar(
    value = query,
    onValueChange = { query = it },
    placeholder = "Search products...",
    onSearch = { performSearch(query) }
)
```

**Convenience Variants**: `ProductSearchBar`, `LocationSearchBar`, `ContactSearchBar`

#### PixaSlider

**Import**: `com.pixamob.pixacompose.components.inputs.PixaSlider`
**File**: `components/inputs/Slider.kt`

**Variants**: `SliderVariant.Filled`, `Outlined`, `Ghost`

**Key Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Float` | required | Current value |
| `onValueChange` | `(Float) -> Unit` | required | Value change callback |
| `valueRange` | `ClosedFloatingPointRange<Float>` | `0f..1f` | Min to max range |
| `steps` | `Int` | `0` | Discrete steps (0 = continuous) |
| `label` | `String?` | `null` | Label text |
| `showValue` | `Boolean` | `false` | Display current value |
| `valueFormatter` | `(Float) -> String` | `{ it.toInt().toString() }` | Custom format |
| `minIcon` | `Painter?` | `null` | Icon at minimum end of track |
| `maxIcon` | `Painter?` | `null` | Icon at maximum end of track |
| `minValueText` | `String?` | `null` | Text at minimum end of track |
| `maxValueText` | `String?` | `null` | Text at maximum end of track |

```kotlin
var volume by remember { mutableStateOf(50f) }

PixaSlider(
    value = volume,
    onValueChange = { volume = it },
    valueRange = 0f..100f,
    label = "Volume",
    showValue = true,
    valueFormatter = { "${it.toInt()}%" }
)

// Discrete rating slider
var rating by remember { mutableStateOf(3f) }
PixaSlider(
    value = rating,
    onValueChange = { rating = it },
    valueRange = 1f..5f,
    steps = 3, // 5 discrete positions
    showValue = true,
    valueFormatter = { "${it.toInt()}/5" }
)

// Slider with icons
PixaSlider(
    value = volume,
    onValueChange = { volume = it },
    valueRange = 0f..100f,
    minIcon = Icons.Default.VolumeDown,
    maxIcon = Icons.Default.VolumeUp
)
```

**Animations**: Track fill width and thumb position animate via `fastSpring` (NoBouncy, High stiffness) for responsive, immediate feedback. The fraction is computed with `derivedStateOf` to avoid unnecessary recompositions.

#### PixaRangeSlider

**Import**: `com.pixamob.pixacompose.components.inputs.PixaRangeSlider`
**File**: `components/inputs/RangeSlider.kt`

Two-thumb range slider for selecting a min/max range.

**Key Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `lowerValue` | `Float` | required | Lower thumb value |
| `upperValue` | `Float` | required | Upper thumb value |
| `onValueChange` | `(Float, Float) -> Unit` | required | Range change callback (lower, upper) |
| `valueRange` | `ClosedFloatingPointRange<Float>` | `0f..1f` | Min to max range |
| `steps` | `Int` | `0` | Discrete steps (0 = continuous) |
| `showValue` | `Boolean` | `false` | Display current values |
| `valueFormatter` | `(Float, Float) -> String` | `{ "${l.toInt()} - ${u.toInt()}" }` | Custom format |

```kotlin
var lower by remember { mutableStateOf(20f) }
var upper by remember { mutableStateOf(80f) }

PixaRangeSlider(
    lowerValue = lower,
    upperValue = upper,
    onValueChange = { l, u -> lower = l; upper = u },
    valueRange = 0f..100f,
    showValue = true
)
```

#### PixaSwitch

**Import**: `com.pixamob.pixacompose.components.inputs.PixaSwitch`
**File**: `components/inputs/Switch.kt`

**Variants**: `SwitchVariant.Filled`, `Outlined`, `Ghost`

**Key Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `checked` | `Boolean` | required | Current state |
| `onCheckedChange` | `(Boolean) -> Unit` | required | State change callback |
| `label` | `String?` | `null` | Label text |
| `description` | `String?` | `null` | Secondary text beneath label |
| `isError` | `Boolean` | `false` | Error state styling (overrides variant, not disabled) |
| `enabled` | `Boolean` | `true` | Enabled state |

```kotlin
var notifications by remember { mutableStateOf(false) }

PixaSwitch(
    checked = notifications,
    onCheckedChange = { notifications = it },
    label = "Enable Notifications",
    variant = SwitchVariant.Filled
)
```

**Animations**: Thumb position uses `thumbSpring` (MediumBouncy, High stiffness) for a subtle overshoot feel; track/thumb/border colors use `colorSpring`; disabled scale uses `selectionSpring`.

**Convenience Variants**: `FilledSwitch`, `OutlinedSwitch`, `MinimalSwitch` (all support `isError` + `description`), `SettingSwitch` (label on left), `ToggleSwitch` (no label)

#### PixaCheckbox

**Import**: `com.pixamob.pixacompose.components.inputs.PixaCheckbox`
**File**: `components/inputs/Checkbox.kt`

**Variants**: `CheckboxVariant.Filled`, `Outlined`, `Ghost`

**States**: `CheckboxState.Unchecked`, `Checked`, `Indeterminate`

**Key Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `state` | `CheckboxState` | required | Current state |
| `onStateChange` | `(CheckboxState) -> Unit` | required | State change callback |
| `label` | `String?` | `null` | Label text |
| `description` | `String?` | `null` | Secondary text beneath label |
| `isError` | `Boolean` | `false` | Error state styling |
| `enabled` | `Boolean` | `true` | Enabled state |

```kotlin
var agreed by remember { mutableStateOf(CheckboxState.Unchecked) }

PixaCheckbox(
    state = agreed,
    onStateChange = { agreed = it },
    label = "I agree to terms and conditions",
    variant = CheckboxVariant.Filled
)

// With error and description
PixaCheckbox(
    state = agreed,
    onStateChange = { agreed = it },
    label = "Accept terms",
    description = "You must accept to continue",
    isError = true
)
```

**CheckboxGroup** — flat list with optional "Select All":
```kotlin
val options = listOf("Apple", "Banana", "Cherry")
var selected by remember { mutableStateOf(setOf<String>()) }

CheckboxGroup(
    options = options,
    selectedValues = selected,
    onSelectionChange = { selected = it },
    showSelectAll = true
)
```

**PixaCheckboxTree** — hierarchical with automatic parent indeterminate state:
```kotlin
data class Category(val name: String, val items: List<String>)

val treeData = remember {
    listOf(
        CheckboxTreeItem(
            id = "fruits", label = "Fruits", isLeaf = false,
            children = listOf(
                CheckboxTreeItem(id = "apple", label = "Apple"),
                CheckboxTreeItem(id = "banana", label = "Banana")
            )
        )
    )
}
var selections by remember { mutableStateOf(setOf<String>()) }

PixaCheckboxTree(
    items = treeData,
    selectedIds = selections,
    onSelectionChange = { selections = it }
)
```

**Animations**: Box, border, and checkmark colors use `colorSpring`; checkmark path draw progress uses `selectionSpring` for a smooth draw-in/draw-out effect.

#### RadioButton & RadioGroup

**Import**: `com.pixamob.pixacompose.components.inputs.RadioButton`
**File**: `components/inputs/RadioButton.kt`

**Variants**: `RadioButtonVariant.Filled`, `Outlined`, `Ghost`

**Key Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `selected` | `Boolean` | required | Selection state |
| `onClick` | `(() -> Unit)?` | required | Click callback (null = read-only) |
| `label` | `String?` | `null` | Label text |
| `description` | `String?` | `null` | Secondary text beneath label |
| `isError` | `Boolean` | `false` | Error state styling |
| `enabled` | `Boolean` | `true` | Enabled state |

```kotlin
var selected by remember { mutableStateOf("option1") }

// Using RadioGroup (generic typed, supports isError)
RadioGroup(
    options = listOf("Option 1", "Option 2", "Option 3"),
    selectedValue = selected,
    onValueSelected = { selected = it },
    isError = true
)

// Or manually
Column {
    RadioButton(
        selected = selected == "option1",
        onClick = { selected = "option1" },
        label = "Option 1",
        description = "First choice description"
    )
    RadioButton(
        selected = selected == "option2",
        onClick = { selected = "option2" },
        label = "Option 2"
    )
}

// Horizontal layout
HorizontalRadioGroup(
    options = listOf("Small", "Medium", "Large"),
    selectedValue = size,
    onValueSelected = { size = it }
)
```

#### PixaDropdown

**Import**: `com.pixamob.pixacompose.components.inputs.PixaDropdown`
**File**: `components/inputs/Dropdown.kt`

Generic typed dropdown with search/filter support.

**Key Parameters** (additional):
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `isError` | `Boolean` | `false` | Error state styling |
| `errorText` | `String?` | `null` | Error message (shown when isError) |
| `helperText` | `String?` | `null` | Helper message below dropdown |
| `required` | `Boolean` | `false` | Shows required asterisk on label |

```kotlin
var selectedCountry by remember { mutableStateOf("") }
val countries = listOf("USA", "Canada", "UK", "Australia")

PixaDropdown(
    selectedValue = selectedCountry,
    onValueSelected = { selectedCountry = it },
    options = countries,
    label = "Country",
    placeholder = "Select country",
    isError = selectedCountry.isEmpty(),
    errorText = "Please select a country",
    required = true
)
```

#### PixaDatePicker

**Import**: `com.pixamob.pixacompose.components.inputs.PixaDatePicker`
**File**: `components/inputs/DatePicker.kt`

**Variants**: `DatePickerVariant.Calendar`, `Wheel`, `MonthDayPicker`, `WeekdayPicker`, `MonthPicker`, `DayCountPicker`, `SchedulePicker`

**Selection Modes**: `DateSelectionMode.Single`, `Multiple`, `Range`

```kotlin
var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

PixaDatePicker(
    variant = DatePickerVariant.Calendar,
    mode = DateSelectionMode.Single,
    onDateSelected = { epochMillis ->
        selectedDate = epochMillis.toLocalDate()
    }
)

// Schedule Picker (repeat intervals)
PixaDatePicker(
    variant = DatePickerVariant.SchedulePicker,
    scheduleConfig = ScheduleConfig(
        showFrequencyTabs = true,
        allowMultipleWeekdays = true
    ),
    initialScheduleSelection = scheduleSelection,
    onScheduleSelected = { selection -> scheduleSelection = selection }
)
```

#### PixaTimePicker

**Import**: `com.pixamob.pixacompose.components.inputs.PixaTimePicker`
**File**: `components/inputs/TimePicker.kt`

**Variants**: `TimePickerVariant.Wheel`, `Clock`, `Digital`
**Formats**: 12h/24h

```kotlin
var appointmentTime by remember { mutableStateOf<LocalTime?>(null) }

PixaTimePicker(
    selectedTime = appointmentTime,
    onTimeSelected = { appointmentTime = it },
    label = "Appointment Time"
)
```

#### PixaColorPicker

**Import**: `com.pixamob.pixacompose.components.inputs.PixaColorPicker`
**File**: `components/inputs/ColorPicker.kt`

**Modes**: `ColorPickerMode.Grid`, `Spectrum`, `Palette`, `Custom`

```kotlin
val colorState = rememberColorPickerState(initialColor = Color.Red)

PixaColorPicker(
    state = colorState,
    label = "Choose Color"
)
```

---

### Display

#### PixaCard

**Import**: `com.pixamob.pixacompose.components.display.PixaCard`
**File**: `components/display/Card.kt`

**Variants**: `BaseCardVariant.Elevated`, `Outlined`, `Filled`, `Tonal`, `Ghost`

**Elevations**: `BaseCardElevation.None`, `Low`, `Medium`, `High`, `Highest`

**Padding**: `SizeVariant.None`, `Compact`, `Small`, `Medium` (default), `Large`, `Huge`, `Massive`

```kotlin
PixaCard(
    variant = BaseCardVariant.Elevated,
    elevation = BaseCardElevation.Medium,
    padding = SizeVariant.Medium,
    onClick = { navigateToDetail() }
) {
    Column {
        Text("Card Title", style = AppTheme.typography.headlineBold)
        Text("Card content", style = AppTheme.typography.bodyRegular)
    }
}
```

**Specialized Cards**:
```kotlin
InfoCard(title = "Info", description = "Static information", icon = Icons.Default.Info)
ActionCard(title = "Settings", onClick = { navigate() }, icon = Icons.Default.Settings)
SelectCard(title = "Option", isSelected = selected, onClick = { toggle() })
MediaCard(imageUrl = url, title = "Article", subtitle = "Category")
StatCard(value = "42", label = "Active", trend = "+12%", trendPositive = true)
ListItemCard(title = "Notifications", leadingIcon = Icons.Default.Notifications)
FeatureCard(title = "Fast Setup", description = "Get started in minutes", icon = Icons.Default.Speed)
CompactInfoCard(title = "Health", icon = Icons.Default.FavoriteBorder)
SummaryCard(title = "Summary", items = listOf("Total" to "12", "Done" to "10"))
PricingCard(price = "$9.99", period = "/month", features = listOf("Feature 1", "Feature 2"))
ProfileCard(name = "John Doe", title = "Developer", avatarUrl = url)
NotificationCard(title = "New message", message = "You have a new message", time = "2m ago")
TestimonialCard(quote = "Great product!", author = "Jane", role = "CEO")
```

#### PixaAvatar

**Import**: `com.pixamob.pixacompose.components.display.PixaAvatar`
**File**: `components/display/Avatar.kt`

**Sizes**: `SizeVariant.Nano` (16dp), `Compact` (24dp), `Small` (32dp), `Medium` (40dp), `Large` (48dp), `Huge` (64dp), `Massive` (96dp)

**Shapes**: `AvatarShape.Circle`, `Rounded`

```kotlin
PixaAvatar(
    imageUrl = user.photoUrl,
    name = user.name, // Shows initials if no image
    size = SizeVariant.Large,
    shape = AvatarShape.Circle,
    onClick = { navigateToProfile() }
)

// Avatar group (stacked avatars)
PixaAvatarGroup(
    users = listOf(user1, user2, user3),
    maxVisible = 3
)
```

#### PixaIcon

**Import**: `com.pixamob.pixacompose.components.display.PixaIcon`
**File**: `components/display/Icon.kt`

```kotlin
// Vector icon
PixaIcon(
    painter = painterResource(Res.drawable.ic_search),
    contentDescription = "Search",
    tint = AppTheme.colors.brandContentDefault
)

// Remote URL icon
PixaIcon(
    source = IconSource.Url("https://example.com/icon.png"),
    contentDescription = "Logo",
    size = 48.dp
)
```

#### PixaImage

**Import**: `com.pixamob.pixacompose.components.display.PixaImage`
**File**: `components/display/Image.kt`

Multiple source types using Coil 3:

```kotlin
// URL image
PixaImage(
    source = PixaImageSource.Url(product.imageUrl),
    contentDescription = product.name,
    modifier = Modifier.size(200.dp),
    contentScale = ContentScale.Crop,
    enableCrossfade = true
)

// SVG file from composeResources/files/
PixaImage(
    source = PixaImageSource.SvgFile("icons/faces/icon.svg"),
    contentDescription = "Icon",
    modifier = Modifier.size(32.dp),
    tint = Color.Blue
)

// Vector image
PixaImage(
    source = PixaImageSource.Vector(Icons.Default.Star),
    contentDescription = "Star",
    modifier = Modifier.size(24.dp)
)

// Drawable resource
PixaImage(
    source = PixaImageSource.DrawableResource(Res.drawable.logo),
    contentDescription = "Logo",
    modifier = Modifier.size(64.dp)
)
```

**SVG Setup**: Requires Coil SVG decoder:

```kotlin
val imageLoader = ImageLoader.Builder(context)
    .components { add(SvgDecoder.Factory()) }
    .build()

CompositionLocalProvider(LocalImageLoader provides imageLoader) {
    MyApp()
}
```

**Features**: Crossfade animations, loading shimmer, error fallback, tinting, Coil caching.

#### PixaChart

**Import**: `com.pixamob.pixacompose.components.display.PixaChart`
**File**: `components/display/Chart.kt`

Vico-based charts:

```kotlin
PixaLineChart(
    data = lineData,
    modifier = Modifier.height(200.dp)
)

PixaColumnChart(
    data = columnData,
    modifier = Modifier.height(200.dp)
)

// Specialized chart composables
TrendChart(data = trendData, modifier = Modifier.height(200.dp))
ComparisonChart(data1 = series1, data2 = series2)
MultiLineChart(series = listOf(series1, series2))
```

#### PixaDivider

**Import**: `com.pixamob.pixacompose.components.display.PixaDivider`
**File**: `components/display/Divider.kt`

```kotlin
PixaDivider()                                // Horizontal, 1dp
HorizontalDivider(thickness = 2.dp)          // Thicker
VerticalDivider(modifier = Modifier.height(24.dp))  // Vertical
```

---

### Feedback

#### PixaAlert

**Import**: `com.pixamob.pixacompose.components.feedback.PixaAlert`
**File**: `components/feedback/Alert.kt`

**Variants**: `AlertVariant.Info`, `Success`, `Warning`, `Error`

**Styles**: `AlertStyle.Filled`, `Outlined`, `Subtle` (default)

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `title` | `String` | required | Alert title |
| `message` | `String?` | `null` | Alert message |
| `variant` | `AlertVariant` | `Info` | Semantic type |
| `style` | `AlertStyle` | `Subtle` | Visual style |
| `dismissible` | `Boolean` | `false` | Show dismiss button |
| `onDismiss` | `(() -> Unit)?` | `null` | Dismiss callback |
| `actionText` | `String?` | `null` | Action button text |
| `onAction` | `(() -> Unit)?` | `null` | Action callback |
| `autoDismissMillis` | `Long?` | `null` | Auto-dismiss delay |
| `showIcon` | `Boolean` | `true` | Show variant icon |

```kotlin
PixaAlert(
    title = "Connection Failed",
    message = "Unable to connect. Please try again.",
    variant = AlertVariant.Error,
    style = AlertStyle.Filled,
    dismissible = true,
    onDismiss = { /* dismissed */ },
    actionText = "Retry",
    onAction = { retryConnection() }
)
```

**Convenience**: `InfoAlert()`, `SuccessAlert()`, `WarningAlert()`, `ErrorAlert()`

#### PixaBadge

**Import**: `com.pixamob.pixacompose.components.feedback.PixaBadge`
**File**: `components/feedback/Badge.kt`

**Variants**: `BadgeVariant.Default`, `Success`, `Error`, `Warning`, `Info`

**Styles**: `BadgeStyle.Filled`, `Outlined`, `Subtle`

```kotlin
Box {
    PixaIcon(painter = painterResource(Res.drawable.ic_notifications))
    PixaBadge(
        content = unreadCount.toString(),
        variant = BadgeVariant.Error,
        style = BadgeStyle.Filled,
        modifier = Modifier.align(Alignment.TopEnd)
    )
}

// Dot indicator (notification dot)
PixaBadge(dot = true, variant = BadgeVariant.Error)

// Wrapper composable
BadgedBox(
    badge = { PixaBadge(content = "3") }
) {
    Icon(...)
}
```

#### Skeleton

**Import**: `com.pixamob.pixacompose.components.feedback.Skeleton`
**File**: `components/feedback/Skeleton.kt`

Loading placeholders with shimmer animation.

**Variants**: `Skeleton()`, `SkeletonCircle()`, `SkeletonText()`, `SkeletonImage()`, `SkeletonButton()`, `SkeletonCard()`, `SkeletonListItem()`, `SkeletonAvatarWithText()`, `SkeletonGrid()`, `SkeletonList()`, `SkeletonCustom()`

```kotlin
if (isLoading) {
    SkeletonCard()
    SkeletonText(lines = 3)
} else {
    ActualContent()
}
```

#### ProgressIndicator

**Import**: `com.pixamob.pixacompose.components.feedback.PixaCircularIndicator`
**File**: `components/feedback/Indicator.kt`

```kotlin
// Indeterminate circular
PixaCircularIndicator(progress = null)

// Determinate circular
PixaCircularIndicator(progress = 0.7f)

// Linear progress
PixaLinearIndicator(
    progress = downloadProgress,
    modifier = Modifier.fillMaxWidth()
)

// Segmented progress
SegmentedProgressIndicator(
    segments = listOf(0.3f, 0.5f, 0.2f),
    colors = listOf(red, green, blue)
)

// Page indicator (dots)
PixaPagerIndicator(pageCount = 5, currentPage = 2)

// Loading spinner
LoadingIndicator(modifier = Modifier.size(24.dp))
```

#### PixaEmptyState

**Import**: `com.pixamob.pixacompose.components.feedback.PixaEmptyState`
**File**: `components/feedback/EmptyState.kt`

```kotlin
PixaEmptyState(
    title = "No Messages",
    message = "You don't have any messages yet",
    icon = painterResource(Res.drawable.ic_inbox_empty),
    actionText = "Compose Message",
    onAction = { navigateToCompose() }
)

// Specialized variants
EmptySearchResults(query = "keyword")
NetworkError(onRetry = { retry() })
PermissionDenied(onRequestPermission = { request() })
```

---

### Navigation

#### PixaTopNavBar

**Import**: `com.pixamob.pixacompose.components.navigation.PixaTopNavBar`
**File**: `components/navigation/TopNavBar.kt`

**Sizes**: `SizeVariant.Small` through `Huge`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `title` | `String` | required | Nav bar title |
| `navigationIcon` | `Painter?` | `null` | Back/menu icon |
| `onNavigationClick` | `(() -> Unit)?` | `null` | Navigation callback |
| `actions` | `List<TopNavAction>` | `emptyList()` | Action items (icon, badge, click) |
| `size` | `SizeVariant` | `Medium` | Size preset |

```kotlin
PixaTopNavBar(
    title = "My App",
    navigationIcon = painterResource(Res.drawable.ic_menu),
    onNavigationClick = { openDrawer() },
    actions = listOf(
        TopNavAction(
            icon = painterResource(Res.drawable.ic_search),
            contentDescription = "Search",
            onClick = { openSearch() }
        ),
        TopNavAction(
            icon = painterResource(Res.drawable.ic_more),
            contentDescription = "More",
            onClick = { showMenu() },
            badge = 3
        )
    )
)
```

#### PixaBottomNavBar

**Import**: `com.pixamob.pixacompose.components.navigation.PixaBottomNavBar`
**File**: `components/navigation/BottomNavBar.kt`

```kotlin
var selectedTab by remember { mutableStateOf(0) }

PixaBottomNavBar(
    items = listOf(
        BottomNavItem(icon = Icons.Default.Home, label = "Home"),
        BottomNavItem(icon = Icons.Default.Search, label = "Search"),
        BottomNavItem(icon = Icons.Default.Person, label = "Profile", badge = 5)
    ),
    selectedIndex = selectedTab,
    onItemSelected = { selectedTab = it }
)
```

**Animations**: Selection transitions use `selectionSpring` — content color, scale (selected 1.0, unselected 0.95), icon scale (selected 1.1, unselected 1.0), and label alpha (selected 1.0, unselected 0.0 in IconWithText mode).

#### PixaTabBar

**Import**: `com.pixamob.pixacompose.components.navigation.PixaTabBar`
**File**: `components/navigation/TabBar.kt`

**Variants**: `TabVariant.Primary`, `Secondary`, `Segmented`, `Vertical`

```kotlin
PixaTabBar(
    tabs = listOf(
        TabItem(TabContent.Text("Overview")),
        TabItem(TabContent.Text("Details")),
        TabItem(TabContent.Text("Reviews"), badge = "3")
    ),
    selectedIndex = selectedTab,
    onTabSelected = { selectedTab = it },
    variant = TabVariant.Primary
)

when (selectedTab) {
    0 -> OverviewContent()
    1 -> DetailsContent()
    2 -> ReviewsContent()
}
```

#### PixaDrawer

**Import**: `com.pixamob.pixacompose.components.navigation.PixaDrawer`
**File**: `components/navigation/Drawer.kt`

Side navigation drawer with configurable items and sections.

```kotlin
PixaDrawer(
    isOpen = isOpen,
    onClose = { isOpen = false },
    items = listOf(
        DrawerItem("Home", icon = Icons.Default.Home),
        DrawerItem("Settings", icon = Icons.Default.Settings),
        DrawerItem("Help", icon = Icons.Default.Help)
    ),
    selectedIndex = selectedItem,
    onItemSelected = {
        selectedItem = it
        isOpen = false
    }
) {
    // Main content
}
```

#### PixaStepper

**Import**: `com.pixamob.pixacompose.components.navigation.PixaStepper`
**File**: `components/navigation/Stepper.kt`

Multi-step processes (checkout, onboarding).

```kotlin
var currentStep by remember { mutableStateOf(0) }

// Horizontal stepper
HorizontalStepper(
    steps = listOf("Account", "Profile", "Preferences"),
    currentStep = currentStep,
    onStepClick = { currentStep = it }
)

// Vertical stepper
VerticalStepper(
    steps = listOf("Cart", "Shipping", "Payment"),
    currentStep = checkoutStep,
    onStepClick = { checkoutStep = it }
)
```

**Animations**: Step indicator background and content colors use `colorSpring`; connector line progress uses `slowSpring` (NoBouncy, Low stiffness) for a gradual filling effect between steps.

---

### Overlay

#### PixaDialog

**Import**: `com.pixamob.pixacompose.components.overlay.PixaDialog`
**File**: `components/overlay/Dialog.kt`

**Variants**: Default, Alert, Confirm, Destructive

```kotlin
if (showDialog) {
    PixaDialog(
        title = "Confirm Delete",
        message = "Are you sure?",
        onDismiss = { showDialog = false },
        confirmButton = {
            PixaButton(
                text = "Delete",
                onClick = { deleteItem(); showDialog = false },
                variant = ButtonVariant.Filled
            )
        },
        dismissButton = {
            PixaButton(
                text = "Cancel",
                onClick = { showDialog = false },
                variant = ButtonVariant.Ghost
            )
        }
    )
}

// Convenience variants
PixaAlertDialog(title = "Attention", message = "Something happened")
PixaConfirmDialog(title = "Confirm", message = "Are you sure?", onConfirm = { confirm() })
PixaDestructiveDialog(title = "Delete", message = "This cannot be undone", onConfirm = { delete() })
```

#### PixaBottomSheet

**Import**: `com.pixamob.pixacompose.components.overlay.PixaBottomSheet`
**File**: `components/overlay/BottomSheet.kt`

```kotlin
if (showSheet) {
    PixaBottomSheet(
        onDismiss = { showSheet = false },
        title = "Filter Options"
    ) {
        FilterOptions()
    }
}

// Specialized variants
SelectOptionBottomSheet(options = listOf("A", "B", "C"), onSelected = { select(it) })
ListBottomSheet(items = listOf(item1, item2), onItemSelected = { select(it) })
ConfirmationBottomSheet(title = "Confirm?", onConfirm = { confirm() })
```

#### PixaMenu

**Import**: `com.pixamob.pixacompose.components.overlay.PixaMenu`
**File**: `components/overlay/Menu.kt`

```kotlin
var showMenu by remember { mutableStateOf(false) }

Box {
    IconButton(onClick = { showMenu = true }) {
        PixaIcon(painter = painterResource(Res.drawable.ic_more_vert))
    }

    PixaMenu(
        expanded = showMenu,
        onDismiss = { showMenu = false },
        items = listOf(
            MenuItem("Edit", onClick = { edit() }),
            MenuItem("Delete", onClick = { delete() }),
            MenuItem("Share", onClick = { share() })
        )
    )
}
```

#### PixaPopover

**Import**: `com.pixamob.pixacompose.components.overlay.PixaPopover`
**File**: `components/overlay/Popover.kt`

**Placements**: `PopoverPlacement.Top`, `Bottom`, `Start`, `End`

```kotlin
PixaPopover(
    content = { Text("Additional info here") },
    placement = PopoverPlacement.Top
) {
    PixaIcon(painter = painterResource(Res.drawable.ic_info))
}
```

#### PixaTooltip

**Import**: `com.pixamob.pixacompose.components.overlay.PixaTooltip`
**File**: `components/overlay/Tooltip.kt`

**Placements**: `TooltipPlacement.Top`, `Bottom`, `Start`, `End`

```kotlin
PixaTooltip(
    text = "Click to copy",
    placement = TooltipPlacement.Bottom
) {
    IconButton(onClick = { copyToClipboard() }) {
        PixaIcon(painter = painterResource(Res.drawable.ic_copy))
    }
}
```

---

## Global Feedback System

### Toast

Global toast notifications accessible from anywhere (ViewModel, UseCase, Repository, Composable).

#### Setup (once at app root)

```kotlin
@Composable
fun App() {
    PixaTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            GlobalToastHost(position = ToastPosition.Bottom)
            GlobalSnackbarHost()

            // Your app content
            MyNavigation()
        }
    }
}
```

#### Usage from ViewModel

```kotlin
class MyViewModel : ViewModel() {
    fun saveData() {
        viewModelScope.launch {
            try {
                repository.save()
                PixaToastManager.showSuccess("Data saved!")
            } catch (e: Exception) {
                PixaToastManager.showError("Failed to save")
            }
        }
    }
}
```

#### Usage from Composable

```kotlin
@Composable
fun MyScreen() {
    val toastScope = rememberToastScope()

    Button(onClick = {
        toastScope.showSuccess("Item saved")
    }) {
        Text("Save")
    }
}
```

#### API Reference

```kotlin
// Suspend methods (for ViewModel/UseCase)
suspend fun PixaToastManager.showSuccess(message: String)
suspend fun PixaToastManager.showError(message: String)
suspend fun PixaToastManager.showWarning(message: String)
suspend fun PixaToastManager.showInfo(message: String)
suspend fun PixaToastManager.showErrorFromException(exception: Throwable)
suspend fun PixaToastManager.dismissToast(id: Long)
suspend fun PixaToastManager.dismissAll()

// Non-suspend context
fun PixaToastManager.launch { /* suspend block */ }

// Full customization
suspend fun showToast(
    message: String,
    variant: ToastVariant = ToastVariant.Info,
    duration: ToastDuration = ToastDuration.Short,
    style: ToastStyle = ToastStyle.Filled,
    icon: Painter? = null,
    showIcon: Boolean = true,
    dismissible: Boolean = true,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    customColors: ToastColors? = null
)
```

**Durations**: `ToastDuration.Short` (2s), `Long` (4s), `Unlimited` (manual dismiss)

**Variants**: `ToastVariant.Info`, `Success`, `Warning`, `Error`

**Styles**: `ToastStyle.Filled`, `Outlined`, `Subtle`

### Snackbar

Global snackbar notifications with optional action buttons.

#### Setup

Same as Toast — add `GlobalSnackbarHost()` at app root (see above).

#### Usage

```kotlin
// From ViewModel
viewModelScope.launch {
    PixaSnackbarManager.showSuccess(
        message = "Item deleted",
        actionLabel = "Undo",
        onAction = { repository.restore(id) }
    )
}

// From Composable
val snackbarScope = rememberSnackbarScope()

Button(onClick = {
    snackbarScope.showSuccess(
        message = "Settings saved",
        actionLabel = "View"
    )
}) {
    Text("Save Settings")
}
```

#### API Reference

```kotlin
suspend fun PixaSnackbarManager.showSuccess(
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
)
suspend fun PixaSnackbarManager.showError(...)
suspend fun PixaSnackbarManager.showWarning(...)
suspend fun PixaSnackbarManager.showInfo(...)
suspend fun PixaSnackbarManager.showErrorFromException(exception: Throwable)
suspend fun PixaSnackbarManager.dismissCurrent()
```

**Durations**: `SnackbarDuration.Short` (4s), `Long` (10s), `Indefinite`

**Variants**: `SnackbarVariant.Default`, `Info`, `Success`, `Warning`, `Error`

---

## Utilities

### PixaAnimationSpecs

**File**: `utils/AnimationSpecs.kt`

Shared spring animation specifications used by all interactive components for smooth, physics-based transitions:

```kotlin
object PixaAnimationSpecs {
    val indicatorSpring     // spring<Float>(NoBouncy, MediumLow)   — Tab indicator sliding
    val selectionSpring     // spring<Float>(NoBouncy, Medium)      — Selection feedback
    val thumbSpring         // spring<Float>(MediumBouncy, High)    — Switch thumb, interactive toggles
    val colorSpring         // spring<Color>(NoBouncy, Medium)       — Color transitions everywhere
    val fastSpring          // spring<Float>(NoBouncy, High)         — Slider thumb, quick responses
    val slowSpring          // spring<Float>(NoBouncy, Low)          — Stepper connector progress
}
```

**Usage pattern** — every `animateXAsState` call uses a `PixaAnimationSpecs` spec with a `label`:

```kotlin
val thumbOffset by animateFloatAsState(
    targetValue = if (checked) 1f else 0f,
    animationSpec = PixaAnimationSpecs.thumbSpring,
    label = "switch_thumb"
)

val bgColor by animateColorAsState(
    targetValue = targetColor,
    animationSpec = PixaAnimationSpecs.colorSpring,
    label = "component_bg"
)
```

### AnimationUtils

**File**: `utils/AnimationUtils.kt`

Legacy tween/spring utilities (use `PixaAnimationSpecs` for new code):

```kotlin
// Spring animations
AnimationUtils.standardSpring()       // DampingRatio 0.5, Stiffness 300
AnimationUtils.fastSpring()           // DampingRatio 0.4, Stiffness 500
AnimationUtils.smoothSpring()         // DampingRatio 0.8, Stiffness 200

// Tween animations
AnimationUtils.standardTween()        // 300ms
AnimationUtils.fastTween()            // 150ms
AnimationUtils.slowTween()            // 500ms

// Pre-built transitions
AnimatedVisibilityStandard(
    visible = isVisible,
    enter = fadeInTransition + scaleInTransition,
    exit = fadeOutTransition + scaleOutTransition
) {
    Content()
}
```

### Animation Convention

All interactive components use spring physics (`PixaAnimationSpecs`) for state-to-state transitions — never `tween`. This ensures natural-feeling, interruptible animations throughout the library. The `label` parameter is required on every `animateXAsState` call for debugging and testing.

### ColorUtils

**File**: `utils/ColorUtils.kt`

```kotlin
// Color space conversion
val hsv: HSV = Color.Red.toHSV()
val color: Color = Color.Companion.hsv(hue = 0f, saturation = 1f, value = 1f)

val hsl: HSL = Color.Red.toHSL()
val color2: Color = Color.Companion.hsl(hue = 0f, saturation = 1f, lightness = 0.5f)

// Hex strings
val hex: String = Color.Red.toHexString()           // #FFFF0000
val color3: Color = Color.Companion.fromHex("#FF0000")

// Color manipulation
Color.Red.withAlpha(0.5f)           // Semi-transparent
Color.Red.blend(Color.Blue, 0.5f)   // 50/50 blend
Color.Red.lighten(0.2f)             // 20% lighter
Color.Red.darken(0.2f)             // 20% darker
Color.Red.contrastColor()           // Black or white for readability

// Preset color palettes
MaterialColors.tailwindPalette       // Tailwind-inspired colors
MaterialColors.material3Colors       // Material 3 palette
```

### DateTimeUtils

**File**: `utils/DateTimeUtils.kt`

```kotlin
DateTimeUtils.now()                  // Current LocalDate
DateTimeUtils.nowDateTime()          // Current LocalDateTime
DateTimeUtils.nowMillis()            // Current epoch millis

// Conversion
val epoch = LocalDate.now().toEpochMillis()
val date = epoch.toLocalDate()
val dateTime = epoch.toLocalDateTime()

// Date arithmetic
val tomorrow = LocalDate.now().plusDays(1)
val yesterday = LocalDate.now().minusDays(1)
val nextMonth = LocalDate.now().plusMonths(1)
val daysBetween = LocalDate.now().daysUntil(otherDate)

// Utilities
LocalDate.now().isToday()
LocalDate.now().isInRange(startDate, endDate)
LocalDate.now().toIsoString()        // "2026-03-15"
Month.JANUARY.getDisplayName()       // "January"
LocalTime.now().to12HourFormat()     // "3:30 PM"
getDaysInMonth(2026, 3)              // 31
isLeapYear(2024)                     // true
```

### ElevationUtils

**File**: `utils/ElevationUtils.kt`

```kotlin
enum class ComponentElevation { None, Low(1.dp), Medium(2.dp), High(4.dp), Highest(8.dp) }

// Apply elevation shadow
Modifier.elevationShadow(elevation = ComponentElevation.Medium)
Modifier.elevationShadow(elevation = 4.dp)
```

### ScreenUtil

**File**: `utils/ScreenUtil.kt`

```kotlin
val screenWidth = ScreenUtil.getScreenWidth()      // Dp
val screenHeight = ScreenUtil.getScreenHeight()     // Dp
val isLandscape = ScreenUtil.isLandscape()

// Percentage-based sizing
Modifier.width(ScreenUtil.percentOfWidth(0.5f))    // 50% of screen width
Modifier.height(ScreenUtil.percentOfHeight(0.3f))  // 30% of screen height

// Or via CompositionLocal
val screenSize = LocalScreenSize.current
val density = ScreenUtil.getAspectRatio()
```

---

## Common UI Patterns

### Login Form

```kotlin
@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PixaTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            variant = TextFieldVariant.Outlined,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        PixaTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            variant = TextFieldVariant.Outlined,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        PixaButton(
            text = "Login",
            onClick = { login(email, password) },
    variant = ButtonVariant.Filled,
            modifier = Modifier.fillMaxWidth(),
            loading = isLoading,
            enabled = email.isNotBlank() && password.isNotBlank()
        )
    }
}
```

### List with Cards

```kotlin
@Composable
fun ProductList(products: List<Product>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(products) { product ->
            PixaCard(
                variant = BaseCardVariant.Elevated,
                onClick = { navigateTo(product) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PixaImage(
                        source = PixaImageSource.Url(product.imageUrl),
                        modifier = Modifier.size(80.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.name, style = AppTheme.typography.headlineBold)
                        Text(product.description, style = AppTheme.typography.bodyRegular)
                        Text("$${product.price}", style = AppTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
```

### Filter UI with Chips

```kotlin
@Composable
fun FilterSection(categories: List<String>) {
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    Column {
        Text("Categories", style = AppTheme.typography.headlineBold)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            categories.forEach { category ->
                PixaChip(
                    label = category,
                    selected = category in selectedCategories,
                    onClick = {
                        selectedCategories = if (category in selectedCategories)
                            selectedCategories - category
                        else selectedCategories + category
                    },
                    type = ChipType.Selectable,
                    variant = ChipVariant.Tonal
                )
            }
        }
    }
}
```

### Dashboard Screen

```kotlin
@Composable
fun DashboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = "42",
                label = "Total",
                icon = painterResource(Res.drawable.ic_check)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = "85%",
                label = "Success Rate",
                trend = "+5%",
                trendPositive = true
            )
        }

        InfoCard(
            title = "New Features",
            description = "Check out the latest updates"
        )

        ActionCard(
            title = "Settings",
            description = "Manage your preferences",
            onClick = { navigateToSettings() }
        )
    }
}
```

### Settings Screen

```kotlin
@Composable
fun SettingsScreen() {
    var notifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        ListItemCard(
            title = "Profile",
            subtitle = "Manage your account",
            leadingIcon = Icons.Default.Person,
            trailingIcon = Icons.Default.ChevronRight,
            onClick = { navigateToProfile() }
        )

        PixaSwitch(
            checked = notifications,
            onCheckedChange = { notifications = it },
            label = "Enable Notifications"
        )

        PixaDivider()

        PixaSwitch(
            checked = darkMode,
            onCheckedChange = { darkMode = it },
            label = "Dark Mode"
        )
    }
}
```

### Screen with Top Bar & Bottom Nav

```kotlin
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            PixaTopNavBar(
                title = "My App",
                navigationIcon = painterResource(Res.drawable.ic_menu),
                onNavigationClick = { /* open drawer */ }
            )
        },
        bottomBar = {
            PixaBottomNavBar(
                items = listOf(
                    BottomNavItem(icon = Icons.Default.Home, label = "Home"),
                    BottomNavItem(icon = Icons.Default.Search, label = "Search"),
                    BottomNavItem(icon = Icons.Default.Person, label = "Profile")
                ),
                selectedIndex = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        when (selectedTab) {
            0 -> HomeContent(Modifier.padding(padding))
            1 -> SearchContent(Modifier.padding(padding))
            2 -> ProfileContent(Modifier.padding(padding))
        }
    }
}
```

### Delete with Undo (Snackbar)

```kotlin
class ItemsViewModel : ViewModel() {
    fun deleteItem(id: String) {
        viewModelScope.launch {
            val item = repository.getItem(id)
            repository.delete(id)

            PixaSnackbarManager.showSuccess(
                message = "Item deleted",
                actionLabel = "Undo",
                onAction = { repository.restore(item) }
            )
        }
    }
}
```

---

## Best Practices

1. **Always wrap in PixaTheme**
   ```kotlin
   PixaTheme {
       // Your app
   }
   ```

2. **Use AppTheme for colors and typography**
   ```kotlin
   Text(
       text = "Title",
       style = AppTheme.typography.titleBold,
       color = AppTheme.colors.baseContentTitle
   )
   ```

3. **Prefer semantic colors over hardcoded values**
   - `✅ AppTheme.colors.brandContentDefault`
   - `❌ Color(0xFF0284C7)`

4. **Use appropriate component sizes** — minimum 44dp touch targets for mobile

5. **Provide accessibility** — always set `contentDescription` for icons/images, use `label` for form inputs

6. **Handle states properly**
   - Use `remember { mutableStateOf() }` for local state
   - Use ViewModel/`rememberToastScope()` for global feedback
   - Show loading states with `loading` parameter or `Skeleton`/`PixaCircularIndicator`

7. **Use appropriate feedback**
   - Errors → `PixaAlert` or error state on inputs
   - Success → `PixaToastManager.showSuccess()`
   - Loading → `PixaCircularIndicator` or `Skeleton`
   - No data → `PixaEmptyState`

8. **Button variant conventions**
   - `ButtonVariant.Filled` — Primary action (CTA)
   - `ButtonVariant.Outlined` — Secondary action
   - `ButtonVariant.Ghost` — Tertiary/minimal action
   - `ButtonVariant.Tonal` — Subtle emphasis

9. **Decision tree for components**

   Need user input?
   - Text → `PixaTextField`, `PixaTextArea`, `PixaSearchBar`
   - Selection → `PixaCheckbox`, `RadioButton`, `PixaSwitch`, `PixaDropdown`
   - Value → `PixaSlider`
   - Date/Time → `PixaDatePicker`, `PixaTimePicker`
   - Color → `PixaColorPicker`

   Need to show information?
   - User identity → `PixaAvatar`
   - Status/count → `PixaBadge`
   - Content container → `PixaCard`
   - Image/icon → `PixaImage`, `PixaIcon`

   Need action?
   - Primary → `PixaButton` (Filled)
   - Secondary → `PixaButton` (Outlined)
   - Tertiary → `PixaButton` (Ghost)

   Need navigation?
   - Top bar → `PixaTopNavBar`
   - Bottom tabs → `PixaBottomNavBar`
   - Content tabs → `PixaTabBar`
   - Side menu → `PixaDrawer`
   - Multi-step → `PixaStepper`

   Need overlay?
   - Modal → `PixaDialog`
   - Bottom options → `PixaBottomSheet`
   - Context menu → `PixaMenu`
   - Info → `PixaPopover`, `PixaTooltip`

---

## Contributing

### Development Setup

- JDK 11+
- Android Studio Hedgehog+ (for Android)
- Xcode 14+ (for iOS on macOS)

```bash
./gradlew build                                # Full build
./gradlew :library:testDebugUnitTest          # Android tests
./gradlew test                                 # All tests
```

### Component Standards

Every component must follow this structure:

```kotlin
// ============================================================================
// CONFIGURATION
// ============================================================================
enum class ComponentVariant { ... }
data class ComponentColors(...)

// ============================================================================
// THEME PROVIDER
// ============================================================================
@Composable
private fun getComponentTheme(...): ComponentStateColors { ... }

// ============================================================================
// BASE COMPONENT (Internal)
// ============================================================================
@Composable
private fun BaseComponent(...) { }

// ============================================================================
// PUBLIC API
// ============================================================================
@Composable
fun Component(...) { }

// ============================================================================
// CONVENIENCE VARIANTS (Optional)
// ============================================================================
@Composable
fun ComponentVariantOne(...) = Component(...)
```

### Core Principles

1. **Build from Primitives** — Use `Box`, `Row`, `Column`, `Canvas` — never Material 3 wrappers
2. **Single File** — All logic in one file with clear sections
3. **Theme-Aware** — Use `AppTheme.colors`, `AppTheme.typography`, etc.
4. **Mobile-First** — Minimum 44dp touch targets
5. **Accessibility** — Proper semantic roles and content descriptions

### Pull Request Process

1. Create feature branch (`feature/component-name` or `fix/issue-description`)
2. Run `./gradlew test` and `./gradlew build`
3. Commit with conventional messages: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`
4. Open PR with clear title, description, and screenshots for UI changes

### Commit Message Format

- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation only
- `refactor:` Code refactoring
- `test:` Adding tests
- `chore:` Maintenance

---

## Changelog

### 1.1.1 (Current)
- SchedulePicker variant for DatePicker
- Nullable labels on text fields
- Bug fixes and performance improvements

### 1.0.9
- Hierarchical sizing system (`SizeVariant`, `HierarchicalSize`)
- Comprehensive shape system (Concave, Convex, Wave, Arch, Notch, Bubble)
- CustomShapes with 10+ decorative shapes

### 1.0.7
- Global Toast and Snackbar managers (`PixaToastManager`, `PixaSnackbarManager`)
- AnimationUtils integration (spring/tween presets, transitions)
- Simplified Divider component

### 1.0.5
- 9 purpose-built card components (InfoCard, ActionCard, SelectCard, MediaCard, etc.)
- Enhanced theme customization with `ColorScales`

### 1.0.2
- Performance optimizations
- Bug fixes

### 1.0.0
- Initial release with 25+ components
- Core theme system
- Android + iOS support

---

## License

```
Copyright 2025 PixaMob

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Links

- [Maven Central Repository](https://central.sonatype.com/artifact/com.pixamob/pixacompose)
- [Issue Tracker](https://github.com/pixamob/pixacompose/issues)
- [Changelog](CHANGELOG.md)

---

**Made with ❤️ by PixaMob**
