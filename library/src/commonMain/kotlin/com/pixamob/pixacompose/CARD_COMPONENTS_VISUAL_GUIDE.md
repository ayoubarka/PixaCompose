# Card Components Visual Guide

## Quick Reference Card Matrix

| Card Type | Purpose | Default Style | Common Use Cases |
|-----------|---------|---------------|------------------|
| **InfoCard** | Static information | Elevated | Tips, notices, help content |
| **ActionCard** | Clickable actions | Filled | Navigation, triggers, menu items |
| **SelectCard** ⭐ | Option selection | Outlined (auto-styled) | Settings, choices, multi-select |
| **MediaCard** | Image + content | Elevated | Gallery, articles, previews |
| **StatCard** | Metrics display | Filled | Dashboard, analytics, KPIs |
| **ListItemCard** | List entries | Ghost | Settings lists, menus |
| **FeatureCard** | Feature showcase | Outlined | Onboarding, tours, benefits |
| **CompactCard** | Tags/chips | Filled | Tags, filters, quick actions |
| **SummaryCard** | Grouped data | Elevated | Reports, summaries, overviews |

---

## SelectCard - The Flexible Solution ⭐

### Three Modes

```
┌────────────────────────────────────┐
│  Mode 1: Full Content              │
├────────────────────────────────────┤
│  [Icon]  Title                     │
│          Description               │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│  Mode 2: Icon + Title Only         │
├────────────────────────────────────┤
│  [Icon]  Title                     │
└────────────────────────────────────┘

┌──────────┐
│  Mode 3: │
│  Icon    │
│  Only    │
│  [Icon]  │
└──────────┘
```

### SelectCard Features
- ✅ All parameters optional (title, description, icon can be null)
- ✅ Supports ImageVector (Material Icons)
- ✅ Supports URL strings (Remote images)
- ✅ Auto-styles based on `isSelected` state
- ✅ Custom icon size and tint
- ✅ Perfect for ProfileSettingScreen use case

---

## Size Variants

```
┌─────────────────────┐
│  COMPACT            │  - Minimal padding
│  (Small elements)   │  - Tight spacing
└─────────────────────┘

┌──────────────────────────┐
│  SMALL                   │  - Compact but readable
│  (Comfortable)           │  - Good for lists
└──────────────────────────┘

┌───────────────────────────────────┐
│  MEDIUM (Default)                 │  - Balanced size
│  (Most common)                    │  - Recommended
└───────────────────────────────────┘

┌────────────────────────────────────────────┐
│  LARGE                                     │  - Spacious layout
│  (More emphasis)                           │  - Touch-friendly
└────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│  HUGE                                                  │  - Maximum padding
│  (Maximum emphasis)                                    │  - Hero sections
└────────────────────────────────────────────────────────┘
```

---

## Shape Variants

```
╭──────────────────────╮
│  DEFAULT             │  - Standard rounded corners
│  (Based on size)     │  - Most common
╰──────────────────────╯

╭────────────────────────╮
│  ROUNDED               │  - Extra rounded (1.5x)
│  (More friendly)       │  - Softer appearance
╰────────────────────────╯

╭──────────────────────────╮
│  EXTRA ROUNDED           │  - Maximum rounded (2x)
│  (Very soft)             │  - Pill-like shape
╰──────────────────────────╯
```

---

## Built-in Styles

### 1. Elevated (with shadow)
```
┌────────────────────────┐
│  Elevated Card         │  - White/surface background
│  (Floating effect)     │  - Shadow elevation
└────────────────────────┘  - Primary content
     ▼ Shadow
```

### 2. Filled (solid background)
```
█████████████████████████
█  Filled Card          █  - Solid background
█  (Solid appearance)   █  - No shadow
█████████████████████████  - Secondary content
```

### 3. Outlined (border only)
```
┏━━━━━━━━━━━━━━━━━━━━━━┓
┃  Outlined Card       ┃  - Border line
┃  (Defined boundary)  ┃  - Transparent background
┗━━━━━━━━━━━━━━━━━━━━━━┛  - Clear separation
```

### 4. Ghost (subtle background)
```
░░░░░░░░░░░░░░░░░░░░░░░░
░  Ghost Card          ░  - Subtle background
░  (Low emphasis)      ░  - Minimal visual weight
░░░░░░░░░░░░░░░░░░░░░░░░  - Background grouping
```

---

## ProfileSettingScreen Use Case

### Original Need
```kotlin
// From ProfileSettingScreen.kt - Sleep hours selection
TextIconCard(
    iconProps = IconProps(
        source = IconSource.ResourceIcon(iconUrl),
        color = colorPalette.brandBorderFocus
    ),
    title = title,
    description = description,
    isEnabled = true,
)
```

### New Solution with SelectCard
```kotlin
SelectCard(
    modifier = Modifier.fillMaxWidth(),
    title = title,
    description = description,
    iconUrl = iconUrl,
    isSelected = selectedOption == index,
    onClick = { selectedOption = index }
)
```

### Visual Representation
```
┌─────────────────────────────────────────────┐
│  [😴]  7-8 hours                            │  UNSELECTED
│        Recommended sleep                    │  (Outlined style)
└─────────────────────────────────────────────┘

╔═════════════════════════════════════════════╗
║  [😴]  7-8 hours                            ║  SELECTED
║        Recommended sleep                    ║  (Brand style + border)
╚═════════════════════════════════════════════╝
```

---

## Common Layouts

### Dashboard Layout
```
┌──────────────┬──────────────┬──────────────┐
│ StatCard     │ StatCard     │ StatCard     │
│ Value: 42    │ Value: 85%   │ Value: 12    │
│ Label: ...   │ Label: ...   │ Label: ...   │
└──────────────┴──────────────┴──────────────┘

┌─────────────────────────────────────────────┐
│ SummaryCard - Weekly Summary                │
│ • Total Habits: 12                          │
│ • Completed: 10                             │
│ • In Progress: 2                            │
└─────────────────────────────────────────────┘
```

### Settings Layout
```
┌─────────────────────────────────────────────┐
│ ListItemCard                                │
│ [Icon] Notifications → Push, Email, SMS     │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ ListItemCard                                │
│ [Icon] Privacy → Data and security          │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ ListItemCard                                │
│ [Icon] About → Version 1.0.0                │
└─────────────────────────────────────────────┘
```

### Feature Tour Layout
```
┌──────────────┬──────────────┬──────────────┐
│ FeatureCard  │ FeatureCard  │ FeatureCard  │
│   [Icon]     │   [Icon]     │   [Icon]     │
│   Title      │   Title      │   Title      │
│ Description  │ Description  │ Description  │
└──────────────┴──────────────┴──────────────┘
```

### Selection Grid
```
┌─────────────────────────────────────────────┐
│ SelectCard - Option 1      [Icon] ✓         │
└─────────────────────────────────────────────┘
┌─────────────────────────────────────────────┐
│ SelectCard - Option 2      [Icon]           │
└─────────────────────────────────────────────┘
┌─────────────────────────────────────────────┐
│ SelectCard - Option 3      [Icon]           │
└─────────────────────────────────────────────┘
```

---

## Color Coding by State

### Default State
```
Background: Surface/Fill color
Border: Default border
Text: Body text color
```

### Selected State (SelectCard)
```
Background: Brand subtle surface
Border: Brand focus border (highlighted)
Text: Brand focus text
```

### Disabled State
```
Background: Disabled surface (38% opacity)
Border: Disabled border (38% opacity)
Text: Disabled text (38% opacity)
```

### Hover/Press State (Automatic)
```
Ripple effect with 12% opacity
Smooth transitions via animations
```

---

## Quick Decision Tree

```
Need to show information?
├─ Static info → InfoCard
└─ Clickable info → ActionCard

Need selection capability?
├─ Single/multi-select → SelectCard ⭐
└─ List of settings → ListItemCard

Need to show data?
├─ Single metric → StatCard
└─ Multiple metrics → SummaryCard

Need media content?
└─ Prominent image → MediaCard

Need to highlight features?
├─ Feature showcase → FeatureCard
└─ Tags/filters → CompactCard
```

---

## Implementation Checklist

### For SelectCard in ProfileSettingScreen
- [ ] Import `SelectCard` from `ui/components/CardComponents`
- [ ] Create state variable: `var selected by remember { mutableStateOf<Int?>(null) }`
- [ ] Map your options to a list
- [ ] Use `forEachIndexed` or `items` to create SelectCards
- [ ] Pass `iconUrl` for remote images OR `icon` for vectors
- [ ] Set `isSelected = selected == index`
- [ ] Handle `onClick = { selected = index }`

### Example Code
```kotlin
var selectedSleepHours by remember { mutableStateOf<Int?>(null) }

sleepOptions.forEachIndexed { index, (iconUrl, title, desc) ->
    SelectCard(
        modifier = Modifier.fillMaxWidth(),
        title = title,
        description = desc,
        iconUrl = iconUrl,
        isSelected = selectedSleepHours == index,
        onClick = { selectedSleepHours = index }
    )
}
```

---

## Performance Tips

1. **Use `remember` for state**
   ```kotlin
   var selected by remember { mutableStateOf(0) }
   ```

2. **Use `LazyColumn` for long lists**
   ```kotlin
   LazyColumn {
       items(options.size) { index ->
           SelectCard(...)
       }
   }
   ```

3. **Avoid unnecessary recompositions**
   ```kotlin
   val options = remember { listOf(...) }
   ```

4. **Use proper keys**
   ```kotlin
   items(options.size, key = { options[it].id }) { ... }
   ```

---

## Accessibility

All cards include:
- ✅ Proper `contentDescription` for icons
- ✅ Semantic `Role.Button` for clickable cards
- ✅ Touch target sizes (minimum 48dp for clickable cards)
- ✅ Sufficient color contrast
- ✅ Screen reader support

---

## Files to Reference

1. **Main Components**: `CardComponents.kt`
2. **Usage Examples**: `CardComponentsExamples.kt`
3. **Full Documentation**: `CARD_COMPONENTS_README.md`
4. **This Visual Guide**: `CARD_COMPONENTS_VISUAL_GUIDE.md`
5. **Summary**: `IMPLEMENTATION_SUMMARY.md`

---

**Created**: December 25, 2025  
**Project**: Streakio Habit Tracker KMP  
**Status**: Ready to Use ✅

