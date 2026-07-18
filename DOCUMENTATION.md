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
  - [Surfaces](#surfaces)
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
- **Animations** — Semantic motion system via `AnimationUtils` (selection/feedback/drag-follow/emphasis/reveal/dismissal/loading presets — see "AnimationUtils — Motion" below), never raw `spring()`/`tween()`
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
│   ├── Typography.kt      # 32 text styles
│   ├── PixaTheme.kt       # Theme provider + AppTheme accessor
│   ├── Dimen.kt           # Hierarchical sizing system
│   ├── CustomShapes.kt    # Decorative shapes (Concave, Wave, Bubble, etc.)
│   └── ShapeStyle.kt      # Shape style configuration
├── components/      # 62 component files by category
│   ├── actions/     # Button, Chip, Accordion, Tab, IconButton, FAB, ButtonGroup,
│   │                #   SegmentedButton, ButtonDock, Link, SlidingButton, TimedButton
│   ├── inputs/      # TextField, TextArea, SearchBar, Slider, RangeSlider, Switch, Checkbox,
│   │                #   RadioButton, Dropdown, Calendar, DatePicker, TimePicker, ColorPicker,
│   │                #   ToggleButtonGroup, PinCode, QuantityStepper, StarRating
│   ├── display/     # Card, Banner, Avatar, Icon, Image, MediaContainer, Chart, Divider, ListItem, Tile,
│   │                #   MessageCard, SectionHeading, Tag, Accordion
│   ├── feedback/    # Alert, Toast, Snackbar, Badge, Skeleton, Indicator, EmptyState, SystemBanner
│   ├── navigation/  # TopNavBar, BottomNavBar, TabBar, Drawer, Stepper
│   ├── overlay/     # Dialog, Menu, Popover, Tooltip
│   └── surfaces/    # Sheet, FullScreenModal, Card
└── utils/           # Helper utilities
    ├── AnimationUtils.kt     # Canonical semantic motion system (single source for all animation specs)
    ├── ColorUtils.kt
    ├── DateTimeUtils.kt
    ├── ElevationUtils.kt
    └── ScreenUtil.kt
```

> Each component file carries its own source-level documentation next to its public API. This page is
> the consolidated overview; the file is the detailed reference and the source of truth.

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

`TextTypography` provides 32 text styles across 9 semantic groups, all accessed through `AppTheme.typography.*`. Raw `TextStyle(...)` must never appear inside a component (existing exceptions in `Chip.kt`/`DatePicker.kt`/`Calendar.kt` are tracked as debt, see below).

Scale rules (mirrors the `HierarchicalSize` mathematical model):
- Base size is `bodyRegular` at 16sp; sizes step by ~1.25x per tier going up, and down through Caption/Footnote/Label/Action.
- `lineHeight` ≈ `fontSize × 1.4`, then rounded to the nearest 4sp so every style lands on the same 4dp baseline grid the rest of the theme (`HierarchicalSize.Spacing`) uses.
- `letterSpacing` tightens (goes negative) at the largest display sizes and widens slightly at the smallest caption/label/action sizes — standard practice for legibility at scale extremes.
- Each size tier below Header carries up to three weights (`Bold` > `Regular` > `Light`, in that font-weight order) so components can express hierarchy without changing size.

| Token | Size / Line height | Weight | Letter spacing | Usage |
| --- | --- | --- | --- | --- |
| `displayLarge` | 64sp / 72sp | W900 | -0.5sp | Hero/marketing headlines |
| `displayMedium` | 52sp / 60sp | W800 | -0.25sp | Large hero numerals or short hero text |
| `displaySmall` | 40sp / 48sp | W700 | 0sp | Smaller hero/splash headings |
| `headerBold` | 32sp / 40sp | W900 | 0sp | Page-level titles |
| `headerRegular` | 32sp / 40sp | W700 | 0sp | Page-level titles, lighter emphasis |
| `headlineBold` | 24sp / 32sp | W800 | 0sp | Section headings |
| `headlineRegular` | 24sp / 32sp | W600 | 0sp | Section headings, lighter emphasis |
| `titleBold` | 20sp / 28sp | W700 | 0.15sp | Card/dialog headers |
| `titleRegular` | 20sp / 28sp | W600 | 0.15sp | Card/dialog headers, lighter emphasis |
| `titleLight` | 20sp / 28sp | W500 | 0.15sp | Card/dialog headers, lightest emphasis |
| `subtitleBold` | 18sp / 28sp | W600 | 0.15sp | Secondary headings |
| `subtitleRegular` | 18sp / 28sp | W500 | 0.15sp | Secondary headings, lighter emphasis |
| `subtitleLight` | 18sp / 28sp | W400 | 0.15sp | Secondary headings, lightest emphasis |
| `bodyBold` | 16sp / 24sp | W500 | 0.5sp | Emphasized paragraph/content text |
| `bodyRegular` | 16sp / 24sp | W400 | 0.5sp | Primary paragraph/content text (scale anchor) |
| `bodyLight` | 16sp / 24sp | W300 | 0.25sp | De-emphasized paragraph/content text |
| `captionBold` | 14sp / 20sp | W500 | 0.4sp | Emphasized secondary/supporting text |
| `captionRegular` | 14sp / 20sp | W400 | 0.4sp | Secondary/supporting text |
| `captionLight` | 14sp / 20sp | W300 | 0.4sp | De-emphasized supporting text |
| `overline` | 12sp / 16sp | W600 | 1.5sp | Uppercase section/category labels |
| `footnoteBold` | 12sp / 16sp | W500 | 0.5sp | Emphasized fine print |
| `footnoteRegular` | 12sp / 16sp | W400 | 0.5sp | Fine print, disclaimers |
| `labelLarge` | 14sp / 20sp | W600 | 0.1sp | Larger form labels, chip text |
| `labelMedium` | 12sp / 20sp | W600 | 0.5sp | Standard form labels, chip text |
| `labelSmall` | 10sp / 16sp | W600 | 0.5sp | Compact/dense form labels |
| `actionMini` | 10sp / 16sp | W700 | 0.5sp | Micro buttons (24dp) |
| `actionExtraSmall` | 12sp / 16sp | W700 | 0.5sp | Compact buttons (32dp) |
| `actionSmall` | 14sp / 20sp | W700 | 0.46sp | Secondary buttons (36dp) |
| `actionMedium` | 16sp / 20sp | W700 | 0.4sp | Standard buttons (44dp) |
| `actionLarge` | 18sp / 24sp | W700 | 0.3sp | Primary/prominent buttons (48dp) |
| `actionExtraLarge` | 20sp / 28sp | W800 | 0.2sp | Hero CTAs (56dp) |
| `actionHuge` | 24sp / 32sp | W900 | 0.15sp | Marketing/full-width buttons (64dp) |

#### Migration notes (typography audit)

- **Line heights realigned to the 4sp baseline grid.** `subtitle*` (26→28sp), `labelMedium` (18→20sp), `labelSmall`/`actionMini` (14→16sp), `actionSmall` (18→20sp), `actionExtraLarge` (26→28sp), and `actionHuge` (30→32sp) were nudged to the nearest 4sp multiple so every text style aligns with the same spacing grid the rest of the theme uses. Font sizes, weights, and letter spacing are unchanged.
- **No tokens were removed.** An audit of `library/src` and `androidApp/src` found several tokens with zero current call sites (`displayMedium`, `headerRegular`, `headlineRegular`, `titleLight`, `subtitleLight`, `overline`, `footnoteRegular`). Each is semantically distinct from its siblings (a genuine weight step within an intentional Bold→Regular→Light ladder, or, for `overline`, the only wide-tracking uppercase-label style) rather than a visual duplicate, so per the "preserve names where already good" rule they were kept as unused-but-valid API surface, not deleted.
- **Known debt, not fixed here:** `Chip.kt`, `DatePicker.kt`, and `Calendar.kt` construct raw `TextStyle(...)` instead of using `AppTheme.typography.*` tokens (the month-nav chevron and stepper +/- glyphs are decorative characters, not `PixaIcon` assets, so they track `HierarchicalSize.Icon.Medium` numerically rather than through a text-style token). Left out of scope since fixing them means picking replacement tokens per call site (a component-level change), not a typography-model change.

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
// Rounded shapes (tied 1:1 to HierarchicalSize.Radius)
AppTheme.shapes.rounded.extraSmall     // 4dp  (Radius.Compact)
AppTheme.shapes.rounded.small          // 6dp  (Radius.Small)
AppTheme.shapes.rounded.medium         // 8dp  (Radius.Medium)
AppTheme.shapes.rounded.large          // 12dp (Radius.Large)
AppTheme.shapes.rounded.extraLarge     // 16dp (Radius.Huge)

// Cut/corner shapes (same sizes)
AppTheme.shapes.cut.small

// Fully-rounded pill/stadium shape — size-invariant, always "as round as
// possible" (built from Radius.Full). Use for chips, pill buttons/badges.
AppTheme.shapes.pill

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

`AppTheme.shapes.rounded`/`.cut`/every decorative family are 5-tier (`extraSmall`…`extraLarge`), while components size themselves against the 8-tier `SizeVariant`. Bridge the two with the `forVariant(SizeVariant)` extension instead of hand-picking a tier or re-deriving a `Dp` and wrapping it in a raw shape constructor:

```kotlin
// Instead of: RoundedCornerShape(HierarchicalSize.Radius.forVariant(size))
val shape = AppTheme.shapes.rounded.forVariant(size)

// Also works on any decorative family and on `cut`:
val bubble = AppTheme.shapes.bubbleLeft.forVariant(size)
```

`forVariant` buckets the 8 `SizeVariant` tiers down to the 5 shape tiers: `None`/`Nano`→`extraSmall`, `Compact`→`small`, `Small`/`Medium`→`medium`, `Large`→`large`, `Huge`/`Massive`→`extraLarge`.

#### Radius audit (2026-07)

`HierarchicalSize.Radius` already lines up closely with Uber Base's 4/8/12/16 corner-radius scale (`Compact`=4, `Medium`=8, `Large`=12, `Huge`=16), plus two Pixa-specific extra rungs Uber doesn't have: `Small`=6 (a half-step between 4 and 8, used by several components for compact chips/tags) and `Massive`=24 + `Full`=9999 (pill/circle). All of these are real, in-use tiers — no radius tier was removed.

The actual gap found wasn't in the token scale, it was in **adoption**: every component computes its own `Dp` per `SizeVariant` and wraps it in a component-local `RoundedCornerShape(...)`, and zero components read `AppTheme.shapes` at all (confirmed via `grep -rl "AppTheme.shapes" components/` returning nothing before this pass) — so the entire shape-token layer, decorative families included, was unreferenced. That's not a defect in the shapes themselves (the decorative richness stays, per constraint), it's a missing bridge, which `forVariant()` now provides. A handful of raw, untokenized radius literals were also found and fixed:

- `TimePicker.kt`/`DatePicker.kt`: `selectorShape`/`tabShape`/`tabContainerShape` used literal `RoundedCornerShape(8.dp/12.dp/16.dp)` where `8`/`12`/`16` are exact matches for `Radius.Medium`/`Large`/`Huge` — swapped to the tokens. Their `20.dp` selector (the `Large` size tier) has no matching rung between `Huge`(16) and `Massive`(24); left as an explicit, commented one-off rather than forcing a new token for a single call site.
- `Chip.kt`'s dismiss-icon clip and `Drawer.kt`'s badge chip both built `RoundedCornerShape(HierarchicalSize.Radius.Full)` locally to get a pill — both now use `AppTheme.shapes.pill`, the new field added for exactly this recurring pattern.

Remaining `RoundedCornerShape(HierarchicalSize.Radius.X)`-style calls elsewhere are token-driven, not ad hoc (the `Dp` already traces to a `Radius` token) — they're a valid, if longer-hand, way of expressing the same shape `forVariant()` now offers directly, and are fine to migrate to the bridge opportunistically without being urgent debt.

#### Component-to-radius mapping

| Component | Radius rule | Notes |
| --- | --- | --- |
| Buttons | `HierarchicalSize.Radius.forVariant(size)` (rounded) or `height / 2` (pill/circle) | `ButtonShape.Pill`/`Circle` compute half of the button's own resolved height — that's a measured/derived value, not a hardcoded literal, so it's not a token violation even though it isn't a fixed `Radius` rung. |
| Chips | `HierarchicalSize.Radius.forVariant(size)`, rounded-rect (not pill) | Pixa chips are intentionally rounded-rect at all sizes, not stadium-shaped, so they stay legible as multi-word tags; use `AppTheme.shapes.pill` only for the circular dismiss affordance inside a chip. |
| Inputs (TextField/TextArea/SearchBar/Dropdown) | `HierarchicalSize.Radius.forVariant(size)`, typically clamped to `Medium`/`Large` | Inputs shouldn't get rounder than `Large` (12dp) regardless of `SizeVariant` — an overly round text field reads as a search/pill field, which is a distinct component. |
| Cards | `HierarchicalSize.Radius.Large`/`Huge` (12–16dp), independent of nested content | Matches Uber's "medium container" tier (their 12px default). Card content nested inside (thumbnails, chips) should use one tier down from the card's own radius (Uber's "nested components" rule) — e.g. a `Huge` (16dp) card nests `Large` (12dp) media. |
| Dialogs | `HierarchicalSize.Radius.Huge` (16dp) | Matches Uber's "large container" tier (their 16px, sheets/dialogs). |
| Bottom sheets | `HierarchicalSize.Radius.Huge` (16dp) top corners only, or `height / 2` for a drag-handle affordance | Sheets are the same "large container" tier as dialogs; the handle itself is a small pill, not part of the sheet's own corner radius. |
| Badges | `HierarchicalSize.Radius.Small`/`Medium` (6–8dp) for count/label badges; `CircleShape` for dot badges | Matches Uber's "small component" tier (their 4px tags, scaled slightly for Pixa's denser badge content). Dot badges have no meaningful corner radius concept — always a circle. |
| FABs | `CircleShape` for `PixaFAB`/`PixaExpandableFab`'s circular buttons; `AppTheme.shapes.pill` (via `PixaButton`) for `PixaFabPill` | Circle and pill are two distinct FAB anatomies per the LINE spec, not a size variant of one shape — there is no rectangular/rounded-rect FAB shape. |

#### When to use which shape

- **`rounded`** (`AppTheme.shapes.rounded.forVariant(size)`): the default for anything rectangular — buttons, inputs, cards, dialogs, menus, tooltips, popovers. If you're not sure which shape a new component needs, start here.
- **`pill`** (`AppTheme.shapes.pill`): stadium/fully-round rectangles where the shape should look "as round as possible" regardless of size — dismiss affordances, pill-style badges/tags, filter pills. Don't use for anything that needs to stay legible as a rectangle with visible corners (see Chips above).
- **`CircleShape`** (from `androidx.compose.foundation.shape`, not a Pixa token — a true circle has no radius/size axis to key off): avatars, dots, icon buttons, FAB default, slider thumbs, radio buttons, selection circles. Use directly; there's nothing for a shape token to parameterize here.
- **Decorative/custom families** (`concaveTop/Bottom`, `convexTop/Bottom`, `wave`, `archTop/Bottom`, `tab`, `notchRounded/Sharp`, `bubbleLeft/Right`): reserved for expressive, branded moments a component author explicitly opts into (a chat bubble, a ticket-stub card, a wave-bottom hero banner) — never a default. They exist and are preserved in full per this task's constraints, but no core component should silently pick one; it should be a deliberate, named variant of that component (e.g. a `CardShape.Ticket` option), not baked into the default render path.

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
HierarchicalSize.Spacing.Medium   // 12dp
HierarchicalSize.Spacing.Large    // 16dp

// Touch targets (minimum 48dp for accessibility)
HierarchicalSize.TouchTarget.Small   // 48dp — WCAG minimum
HierarchicalSize.TouchTarget.Medium  // 52dp — standard default
```

#### Category audit (2026-07)

All 24 `HierarchicalSize` categories were audited against real usage and kept — each maps to a distinct component family (`Container`, `Button`, `Icon`, `Input`, `Chip`, `ListItem`, `Card`, `Avatar`, `Badge`, `Toggle`, `Checkbox`, `AppBar`, `Tab`, `BottomNav`, `Spacing`, `Padding`, `Radius`, `Border`, `Shadow`, `TouchTarget`, `Divider`, `Stroke`, `SliderTrack`, `Image`). None were redundant enough to remove, but three had two variants collapsed to the same literal value, which defeats the point of having 8 distinct steps:

| Category | Before | After | Why |
| --- | --- | --- | --- |
| `TouchTarget` | `Small` = `Medium` = 48dp | `Medium` = 52dp | `Small` stays the WCAG-minimum floor (48dp); `Medium` (the default) is now visibly larger, matching every other category's pattern of `Medium` > `Small`. |
| `BottomNav` | `Nano` = `Compact` = 48dp | `Nano` = 44dp | Restores a real step below `Compact`; `Nano` is documented "not recommended" precisely because it dips under WCAG. |
| `Shadow` | `Compact` = `Small` = 2dp | `Small` = 3dp | Gives `Small` its own rung between resting-state cards (`Compact`) and raised buttons (`Medium`). |

Also removed a dead "Legacy Aliases" section header (and a matching dead code sample in the kdoc) that referenced aliases which don't exist in the file — it was misleading, not functional.

#### Dimension conventions

- **Component heights** (`Container`/`Button`/`Input`/`Chip`/`ListItem`/`Tab`/`AppBar`/`BottomNav`): always resolve via that category's `.forVariant(size)` — never hardcode a height that happens to match one of these.
- **Internal padding**: use `HierarchicalSize.Padding.forVariant(size)` for the common case (symmetric or near-symmetric internal padding). Component-specific fine-tuning (e.g. Badge's 3/4/5dp padding ladder) is fine to leave as local literals — those values aren't reusable elsewhere, and per the "no one-off tokens" rule they don't belong in the shared system either.
- **Icon sizes**: use `HierarchicalSize.Icon.forVariant(size)` when the icon is meant to track the component's own size variant one-to-one. When a component's icon ladder is deliberately offset from its own size ladder (e.g. Badge's icon is always one step down from the badge's own step), reference the fixed value from `HierarchicalSize.Icon` directly (e.g. `HierarchicalSize.Icon.Nano`) rather than introducing a new literal, so the value stays discoverable and consistent with every other component that also uses that icon rung.
- **Touch targets**: interactive elements must use `HierarchicalSize.TouchTarget.forVariant(size)` for the tappable area, which can be larger than the element's visual size (expand via padding/`Modifier.size`, not by inflating the visual component itself).
- **Spacing gaps** (`Arrangement.spacedBy`, `Spacer` widths/heights between components): use `HierarchicalSize.Spacing.forVariant(size)`.
- **Min widths**: only introduce a `DialogMaxWidth`-style named constant (see `Container.DialogMaxWidth`) when a real layout constraint forces a fixed min/max width independent of `SizeVariant` — don't add one per component speculatively.
- **Adaptive sizing hook**: `AppTheme.adaptiveSizeVariant` (from `WindowSizeClass`) is the recommended *default* for a component's `size` parameter when the call site doesn't care, but an explicit `size` argument passed by the caller must always win. Follow the pattern used by `PixaDivider`/`HorizontalDivider`/`VerticalDivider` (see below): accept `size: SizeVariant = SizeVariant.Compact` alongside a nullable explicit override (e.g. `thickness: Dp? = null`), and only fall back to `HierarchicalSize.<Category>.forVariant(size)` when the override is null.

#### First-pass component refactors

- **`Divider.kt`** (`PixaDivider`/`HorizontalDivider`/`VerticalDivider`) gained a `size: SizeVariant = SizeVariant.Compact` parameter. `thickness` is now a nullable override (`Dp? = null`) that wins when provided; otherwise thickness resolves from `HierarchicalSize.Divider.forVariant(size)`. This was one of the 9 components CLAUDE.md flagged as missing `SizeVariant` entirely.
- **`Badge.kt`**: `getBadgeConfig`'s local `when(size)` ladder had literal `16.dp`/`20.dp`/`24.dp`/`8.dp` that were exact matches for `HierarchicalSize.Badge.Small`/`Medium`/`Large`/`Nano` — swapped in the token references. Icon sizes `10.dp`/`14.dp` matched `HierarchicalSize.Icon.Nano`/`Compact` and were swapped too. Left `12.dp` (Medium's icon) and the `3.dp`/`4.dp`/`5.dp` padding ladder as local literals — none of those have a matching reusable category value, so tokenizing them would mean inventing one-off tokens, which the task explicitly rules out.

**Remaining debt** (not addressed in this pass, still tracked per CLAUDE.md): `Alert.kt`, `According.kt`, `Menu.kt`, `Popover.kt`, `Tooltip.kt`, `Drawer.kt`, `Snackbar.kt`, `Toast.kt` still have no `SizeVariant` param; `Tab.kt`, `Skeleton.kt`, `Card.kt`, `Stepper.kt` remain the highest raw-`.dp`-literal files and still need their local `when(size)` ladders audited value-by-value the way `Badge.kt` was in this pass. `Menu.kt` also renders its divider via `androidx.compose.material3.HorizontalDivider` instead of Pixa's own `HorizontalDivider` — a Material-3-usage violation worth fixing alongside its `SizeVariant` gap.

### Layout & Spacing Policy

PixaCompose has no column/gutter grid system, and deliberately does not adopt one. Uber Base's layout-grid model (columns, gutters that scale by breakpoint, span/skip/hide props) is a *web-first responsive-grid abstraction* — it exists to reflow variable-width content across arbitrary browser widths. Compose Multiplatform screens are built from `Box`/`Row`/`Column` with known, finite breakpoints (`WindowSizeClass.Compact/Medium/Expanded`), so a column-count/gutter-count system would be a DSL with no Compose equivalent to plug into — exactly the "CSS-like abstraction layer" and "web-only grid abstraction" the task rules out. What *does* transfer from Uber's model is the underlying idea worth keeping: **outer margin and inter-section gutter should scale with available width, on a 4dp-rooted scale, using the same handful of breakpoints already in the theme.** That's implemented directly on top of `HierarchicalSize.Spacing` below, with no new abstraction layer.

#### The five spacing roles

| Role | Use for | Token | Adaptive? |
| --- | --- | --- | --- |
| **Page padding** (margin) | Outer padding of a screen-level `Column`/`Box`/`LazyColumn` content padding | `AppTheme.pageMargin` | Yes — scales with `WindowSizeClass` |
| **Section spacing** (gutter) | Gap between major top-level sections of a screen (header → content → footer, or between unrelated content blocks) | `AppTheme.sectionSpacing` | Yes — scales with `WindowSizeClass` |
| **Inline gaps** | Gap between small, related elements sitting in the same row/column (icon + label, two buttons side by side) | `HierarchicalSize.Spacing.Small` (8dp) or `.Compact` (4dp) | No — stays fixed regardless of screen size |
| **List/card spacing** | `Arrangement.spacedBy(...)` between items in a `LazyColumn`/`LazyRow`/`LazyVerticalGrid`, or the gap between sibling cards | `HierarchicalSize.Spacing.Medium` (12dp) default; `.Small` (8dp) for dense lists | No — density is a deliberate component choice, not a screen-size one |
| **Vertical rhythm** | Baseline gap between a heading and its body text, or between stacked text blocks within one component | `HierarchicalSize.Spacing.Small`–`.Medium` (8–12dp), matched to the `Typography` line-height grid so text blocks land on the same 4dp rhythm as the type scale | No |

Everything here is `HierarchicalSize.Spacing.forVariant(...)` or a fixed rung of it — no new spacing scale was introduced. `AppTheme.pageMargin`/`AppTheme.sectionSpacing` are two small `@Composable` properties on `AppTheme` (theme/PixaTheme.kt) that just switch on the current `WindowSizeClass` and return an existing `Spacing` rung:

```kotlin
val AppTheme.pageMargin: Dp        // Compact→16dp, Medium→24dp, Expanded→48dp
val AppTheme.sectionSpacing: Dp    // Compact→24dp, Medium→48dp, Expanded→48dp
```

Usage:

```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = AppTheme.pageMargin),
    verticalArrangement = Arrangement.spacedBy(AppTheme.sectionSpacing)
) {
    HeaderSection()
    ContentSection()
    FooterSection()
}
```

Both cap out at `HierarchicalSize.Spacing.Massive` (48dp) at the `Expanded` tier rather than introducing a larger token — PixaCompose targets phone/tablet/foldable layouts, not desktop-scale margins, so there was no reusable case for going further.

#### Compact vs Medium vs Expanded, concretely

| Tier | Width | `pageMargin` | `sectionSpacing` | Typical device |
| --- | --- | --- | --- | --- |
| Compact | < 600dp | 16dp | 24dp | Phone portrait |
| Medium | 600–840dp | 24dp | 48dp | Phone landscape, foldable, small tablet portrait |
| Expanded | ≥ 840dp | 48dp | 48dp | Large tablet, desktop |

This reuses the exact breakpoints `WindowSizeClass`/`AppTheme.adaptiveSizeVariant` already define (`utils/ScreenUtil.kt`) — no new breakpoint concept was added. A screen that also wants its components to grow (buttons, icons, containers) should pass `size = AppTheme.adaptiveSizeVariant` the same way it already can; `pageMargin`/`sectionSpacing` are the layout-level counterpart to that same hook.

#### Helper vs. plain modifiers

- **Worth a helper**: page margin and section spacing, because the "right" value depends on `WindowSizeClass`, which the call site shouldn't have to branch on by hand every time. That's the only genuinely repeated, adaptive decision in layout code — hence the two `AppTheme` properties above.
- **Not worth a helper**: inline gaps, list/card spacing, and vertical rhythm. These are fixed values already expressed with a single `HierarchicalSize.Spacing.<rung>` call; wrapping that in another function would just rename `Arrangement.spacedBy(HierarchicalSize.Spacing.Small)` to `Arrangement.spacedBy(SomeHelper.inlineGap())` for no behavioral gain. Plain `Modifier.padding(...)` / `Arrangement.spacedBy(...)` calls referencing `HierarchicalSize.Spacing` directly are the correct, idiomatic form — introducing a grid DSL (columns/spans/gutters as props) here would add a layer Compose's own layout primitives already cover.

#### What component authors should do

1. Screen/page-level composables (in `androidApp`, or any top-level layout a consumer builds): pad the outer container with `AppTheme.pageMargin` and separate major sections with `AppTheme.sectionSpacing`.
2. Inside a single component (`components/**`): never reach for `AppTheme.pageMargin`/`sectionSpacing` — those are screen-level concepts. Use `HierarchicalSize.Spacing`/`Padding` directly, exactly as already documented under Sizing System above.
3. Never hardcode a raw `.dp` spacing literal that happens to equal an existing `Spacing`/`Padding` rung — that's the same "audit for exact token matches" rule applied in the Dimensions pass, and it's why `pageMargin`/`sectionSpacing` are built from `HierarchicalSize.Spacing` instead of their own literals.

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

> **This page is the high-level consolidated reference.** Every component file also carries its own
> source-level documentation next to the public API — purpose, anatomy, variants, states, sizing,
> adaptive behavior, and usage notes, including the design-spec rationale behind each decision. When
> you need the full detail for one component, open its file (listed as **File** in each entry below);
> when you need the overview across the library, stay here. The source is always the final authority.

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
    leadingIcon = painterResource(Res.drawable.ic_menu)
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
| `text` | `String?` | `null` | Chip text |
| `selected` | `Boolean` | `false` | Selected state |
| `onClick` | `(() -> Unit)?` | `null` | Click handler |
| `type` | `ChipType` | `Static` | Behavior type |
| `variant` | `ChipVariant` | `Tonal` | Visual style |
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
            text = tag,
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

#### PixaFAB family (PixaFAB / PixaFabPill / PixaExpandableFab)

**File**: `components/actions/FAB.kt`

A floating-action-button family (LINE Design System FAB spec), not one overloaded
component — three roles, each its own composable:

| Composable | Role | Anatomy |
|---|---|---|
| `PixaFAB` | Single-action circle FAB — runs its action immediately | Icon only, never a label (source: "do not add text inside the floating button") |
| `PixaFabPill` | Pill FAB — also runs its action immediately, just pill-shaped | Optional icon + required label, thin wrapper over `PixaButton(shape = ButtonShape.Pill)` |
| `PixaExpandableFab` | Expandable circle FAB — opens a child-action menu | Main button (reuses `PixaFAB`) + up to 5 child actions (each reusing `PixaButton(shape = ButtonShape.Circle)`) + dimmer/scrim, owned by this component |

**`PixaFAB`** — **Variants**: `FABVariant.Filled`, `Tonal`, `Outlined`. **Sizes**: `SizeVariant.Medium` (48dp), `Large` (56dp, default), `Huge` (96dp — a deliberate one-off tier, no exact `HierarchicalSize.Container` rung sits at 96dp).

```kotlin
PixaFAB(
    icon = painterResource(Res.drawable.ic_add),
    onClick = { addItem() }
)
```

**`PixaFabPill`** — always fires `onClick` immediately; not the same thing as the expandable menu pattern. Defaults to a neutral white/bordered theme (per the source's design-spec table), not a brand-filled one — pass `customColors` to override.

```kotlin
PixaFabPill(
    label = "Refresh",
    icon = painterResource(Res.drawable.ic_refresh),
    onClick = { refresh() }
)
```

**`PixaExpandableFab`** — child actions are modeled as `FabAction(icon, onClick, label = null, contentDescription = null)`; `actions` is capped at 5 (source: "do not use more than five buttons except for main button" — extras are silently dropped). Two states, Default and Expanded: selecting the main button flips `expanded`, shows the dimmer + child rows, and crossfades the main icon to `closeIcon`. The dimmer/scrim and child menu render in a `Popup` anchored to `dockAlignment` (default `BottomEnd`) — **this component owns overlay/dimmer positioning for its menu**, but the main button itself stays caller-positioned exactly like plain `PixaFAB` (place it via your own `Modifier.align(...)`, matching `dockAlignment`, so the menu appears directly above it).

```kotlin
PixaExpandableFab(
    icon = painterResource(Res.drawable.ic_add),
    closeIcon = painterResource(Res.drawable.ic_close),
    actions = listOf(
        FabAction(icon = painterResource(Res.drawable.ic_photo), label = "Photo", onClick = { addPhoto() }),
        FabAction(icon = painterResource(Res.drawable.ic_camera), label = "Camera", onClick = { openCamera() })
    ),
    modifier = Modifier.align(Alignment.BottomEnd)
)
```

**Convenience Variants**: `MiniFAB(icon, onClick, modifier)`, `StandardFAB(icon, onClick, modifier)` (both `PixaFAB` presets).

> The old `label`-driven "extended FAB" mode and `ExtendedFAB` convenience were removed from `PixaFAB` — they put text inside the circular FAB, which the LINE spec explicitly flags as a don't. Use `PixaFabPill` for an icon+label, immediate-trigger FAB instead.

#### PixaAccordion

**Import**: `com.pixamob.pixacompose.components.actions.PixaAccordion`
**File**: `components/actions/Accordion.kt`

Collapsible content sections with group support.

```kotlin
PixaAccordion(
    title = "Advanced Settings",
    expanded = isExpanded,
    onExpandedChange = { isExpanded = it }
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
    selectedTabIndex = selectedTab,
    onTabSelected = { selectedTab = it }
)

// Segmented tabs
SegmentedTabs(
    tabs = listOf("Day", "Week", "Month"),
    selectedTabIndex = selectedSegment,
    onTabSelected = { selectedSegment = it }
)

// Vertical tabs
VerticalTabs(
    tabs = listOf("Profile", "Settings", "Help"),
    selectedTabIndex = selectedVerticalTab,
    onTabSelected = { selectedVerticalTab = it }
)
```

**Animations**: Background, content, and border color transitions use `colorSpring`; indicator color transitions use `colorSpring` for a smooth fade between transparent and the selected indicator color.

#### PixaButtonGroup

**Import**: `com.pixamob.pixacompose.components.actions.PixaButtonGroup`
**File**: `components/actions/ButtonGroup.kt`

Groups related **actions** or simple selections as a row of buttons. For richer option tiles with a
title, subtitle, and image, use [PixaToggleButtonGroup](#pixatogglebuttongroup) instead.

```kotlin
PixaButtonGroup(
    items = listOf(
        ButtonGroupItem(id = "all", text = "All"),
        ButtonGroupItem(id = "active", text = "Active"),
        ButtonGroupItem(id = "done", text = "Done")
    ),
    selectedIds = setOf(selected),
    onSelectionChange = { selected = it.first() },
    selectionMode = ButtonGroupSelectionMode.Single,  // Single / Multi / None
    layout = ButtonGroupLayout.Clustered              // Clustered (wraps) / HorizontalScroll
)
```

#### PixaSegmentedButton

**Import**: `com.pixamob.pixacompose.components.actions.PixaSegmentedButton`
**File**: `components/actions/SegmentedButton.kt`

Single-select segmented control. Exactly one segment is always selected.

```kotlin
PixaSegmentedButton(
    items = listOf(
        SegmentedButtonItem(id = "day", label = "Day"),
        SegmentedButtonItem(id = "week", label = "Week"),
        SegmentedButtonItem(id = "month", label = "Month")
    ),
    selectedId = selectedRange,
    onSelectionChange = { selectedRange = it },
    width = SegmentedButtonWidth.Fixed,   // or Fill
    shape = SegmentedButtonShape.Default
)
```

#### PixaButtonDock

**Import**: `com.pixamob.pixacompose.components.actions.PixaButtonDock`
**File**: `components/actions/ButtonDock.kt`

A pinned dock of primary actions at the bottom of a screen. `hasOverflowContent` signals that content
scrolls beneath the dock.

```kotlin
PixaButtonDock(
    items = listOf(
        ButtonDockItem(id = "cancel", text = "Cancel", onClick = { cancel() }, variant = ButtonVariant.Ghost),
        ButtonDockItem(id = "save", text = "Save", onClick = { save() }, variant = ButtonVariant.Filled)
    ),
    layout = DockLayout.Auto,
    hasOverflowContent = true
)
```

#### PixaLink

**Import**: `com.pixamob.pixacompose.components.actions.PixaLink`
**File**: `components/actions/Link.kt`

Inline navigational text action. `LinkSize` is its own scale (not `SizeVariant`) so links can match
surrounding body text.

```kotlin
PixaLink(
    text = "Terms and conditions",
    onClick = { openTerms() },
    size = LinkSize.Medium,
    visited = hasVisited
)
```

#### PixaSlidingButton

**Import**: `com.pixamob.pixacompose.components.actions.PixaSlidingButton`
**File**: `components/actions/SlidingButton.kt`

Slide-to-confirm control for consequential actions.

```kotlin
PixaSlidingButton(
    label = "Slide to confirm",
    arrowIcon = painterResource(Res.drawable.ic_arrow_right),
    onSlideComplete = { confirm() },
    variant = SlidingButtonVariant.Brand,
    threshold = SlidingButtonThreshold.Low,
    loading = isSubmitting,
    completed = isDone
)
```

#### PixaTimedButton

**Import**: `com.pixamob.pixacompose.components.actions.PixaTimedButton`
**File**: `components/actions/TimedButton.kt`

A button that fires `onTimeout` when its countdown elapses, with a visible progress treatment. Change
`resetKey` to restart the countdown.

```kotlin
PixaTimedButton(
    text = "Undo",
    onTimeout = { commitDelete() },
    onClick = { undoDelete() },
    durationSeconds = TimedButtonDuration.Short,
    resetKey = pendingItemId
)
```

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
| `minLines` | `Int` | `4` | Field height in rows |
| `maxLines` | `Int` | `minLines` | Max lines before the content scrolls |

**Height is expressed in rows, not dp.** `minLines` drives the container height via the type scale.
`maxLines` defaults to `minLines`, so the field is a fixed N rows tall and scrolls beyond that. Raise
`maxLines` above `minLines` to opt into auto-grow.

```kotlin
var notes by remember { mutableStateOf("") }

PixaTextArea(
    value = notes,
    onValueChange = { notes = it },
    label = "Notes",
    placeholder = "Enter your notes...",
    minLines = 4,
    maxLines = 10,   // auto-grows from 4 rows up to 10, then scrolls
    maxLength = 500  // setting maxLength shows the "xx / n" counter
)
```

> The character counter appears whenever `maxLength` is set — there is no `showCharacterCount` flag.
> There is no `leadingIcon`: the external spec defines no accessory for a text area.

**Convenience Variants**: `CommentTextArea`, `BioTextArea`, `NoteTextArea`

#### PixaSearchBar

**Import**: `com.pixamob.pixacompose.components.inputs.PixaSearchBar`
**File**: `components/inputs/SearchBar.kt`

Migrated to the [eBay Playbook Search Field](https://playbook.ebay.com/design-system/components/search-field) spec (2026-07-18). Filters a list using characters typed into the field; the leading search icon is a static, always-visible affordance (never hidden once text is entered).

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `onValueChange` | `String` / `(String) -> Unit` | required | The only filtering contract — wire `onValueChange` to filter your own list live |
| `variant` | `SearchBarVariant` | `Filled` | `Filled`/`Outlined`/`Elevated` — a Pixa styling axis, not from the source spec |
| `size` | `SizeVariant` | **`Small`** | Small=32dp/Medium=40dp/Large=48dp visual height; `Small` default per source ("small as default") — differs from every sibling input's `Medium` default |
| `trailingAccessoryIcon` / `onTrailingAccessoryClick` | `Painter?` / `(() -> Unit)?` | `null` | Default trailing accessory (source example: a camera button); generalized rather than hardcoded to one icon |
| `showClearButton` | `Boolean` | `true` | Enables the focused+non-empty clear affordance |
| `onSearch` | `(() -> Unit)?` | `null` | IME "search" key trigger |
| `suggestions` / `showSuggestions` / `onSuggestionClick` | — | — | Typeahead dropdown — a **Pixa addition**, not part of the source anatomy |

**Clear-button replacement** (source-confirmed): the trailing accessory is replaced by a clear glyph only while the field **is focused and holds text**. Unfocusing — even with text still present — reverts the trailing slot back to `trailingAccessoryIcon`. This is different from "clear button shows whenever there's text."

**Width**: always constrained to 200–480dp (source-confirmed, uniform across sizes), which alone reproduces "full width on small screens" on any phone-width layout. Touch target never drops below 48dp regardless of visual height (source: "tap target is 48px across all sizes").

**Not part of this component** (per source, these are external composition, not anatomy): a cancel/back button placed next to the field in a dedicated search view, and a "search" button placed after the field for server-triggered refresh. Compose these yourself alongside `PixaSearchBar`.

```kotlin
var query by remember { mutableStateOf("") }

PixaSearchBar(
    value = query,
    onValueChange = { query = it },
    placeholder = "Search products...",
    onSearch = { performSearch(query) }
)
```

> Breaking change from the pre-migration API: `clearIcon` was removed (the clear affordance now renders a built-in glyph, matching `PixaTextField`'s `onClear` pattern); `voiceSearchIcon`/`onVoiceSearch` were renamed to the generic `trailingAccessoryIcon`/`onTrailingAccessoryClick`.

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

// There is no `label` param — pair the slider with your own label, or use
// `minValueText`/`maxValueText` for end captions.
PixaSlider(
    value = volume,
    onValueChange = { volume = it },
    valueRange = 0f..100f,
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
| `checked` | `Boolean` | required | Checked state |
| `onCheckedChange` | `((Boolean) -> Unit)?` | required | Change callback |
| `label` | `String?` | `null` | Label text |
| `description` | `String?` | `null` | Secondary text beneath label |
| `labelPosition` | `CheckboxLabelPosition` | `End` | Label side |
| `isError` | `Boolean` | `false` | Error state styling |
| `enabled` | `Boolean` | `true` | Enabled state |
| `state` | `CheckboxState?` | `null` | Optional override; wins over `checked` (use for `Indeterminate`) |

```kotlin
var agreed by remember { mutableStateOf(false) }

PixaCheckbox(
    checked = agreed,
    onCheckedChange = { agreed = it },
    label = "I agree to terms and conditions",
    variant = CheckboxVariant.Filled
)

// With error and description
PixaCheckbox(
    checked = agreed,
    onCheckedChange = { agreed = it },
    label = "Accept terms",
    description = "You must accept to continue",
    isError = true
)
```

**Tri-state** — use `TriStateCheckbox` for `Indeterminate`; it takes `state`/`onStateChange`:

```kotlin
var state by remember { mutableStateOf(CheckboxState.Indeterminate) }

TriStateCheckbox(
    state = state,
    onStateChange = { state = it },
    label = "Select all"
)
```

**CheckboxGroup** — flat list with optional "Select All":
```kotlin
val options = listOf("Apple", "Banana", "Cherry")
var selected by remember { mutableStateOf(setOf<String>()) }

CheckboxGroup(
    options = options,
    selected = selected,
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
    selectedOption = selected,
    onOptionSelected = { selected = it },
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
    selectedOption = size,
    onOptionSelected = { size = it }
)
```

#### PixaDropdown

**Import**: `com.pixamob.pixacompose.components.inputs.PixaDropdown`
**File**: `components/inputs/Dropdown.kt`

Generic typed single-select dropdown. The **field** owns the label, value, placeholder, helper/error
text and required state; the **option list** is presented as a separate surface, adaptively.

**Adaptive presentation**: `DropdownPresentation.Adaptive` (the default) reads
`AppTheme.windowSizeClass` — compact screens present options in a [PixaSheet](#pixasheet), larger
screens in an anchored popover menu (the same menu surface as [PixaMenu](#pixamenu)). Pass
`DropdownPresentation.Sheet` or `.Popover` to pin one explicitly.

**Key Parameters** (additional):
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `presentation` | `DropdownPresentation` | `Adaptive` | `Adaptive` / `Sheet` / `Popover` |
| `isError` | `Boolean` | `false` | Error state styling |
| `errorText` | `String?` | `null` | Error message; replaces `helperText` when `isError` |
| `helperText` | `String?` | `null` | Helper message below the field |
| `required` | `Boolean` | `false` | Shows required asterisk on label |
| `sheetTitle` | `String?` | `null` | Title for the compact-screen sheet; falls back to `label` |

```kotlin
var selectedCountry by remember { mutableStateOf<String?>(null) }

PixaDropdown(
    items = listOf(
        DropdownItem(value = "us", label = "USA"),
        DropdownItem(value = "ca", label = "Canada"),
        DropdownItem(value = "uk", label = "UK")
    ),
    selectedItem = selectedCountry,
    onItemSelected = { selectedCountry = it },
    label = "Country",
    placeholder = "Select country",
    required = true,
    isError = selectedCountry == null,
    errorText = "Select a country"
)
```

> `DropdownColors` describes the field only. The option surface is themed by the menu layer
> (`MenuColors`), so it no longer carries `menuBackground`/`menuItemHover`/`selectedBackground`.

#### PixaCalendar

**Import**: `com.pixamob.pixacompose.components.inputs.PixaCalendar`
**File**: `components/inputs/Calendar.kt`

The reusable calendar browsing/selection primitive — month navigators, weekday header,
day grid, current-day highlight, and range-edge/range-fill visualization (anatomy per the
eBay Playbook Calendar spec). Owns all month-grid rendering in the library; `PixaDatePicker`'s
`Calendar` variant composes this rather than duplicating grid logic.

**Selection Modes**: `DateSelectionMode.Single`, `Multiple`, `Range` (also shared by `PixaDatePicker`)

```kotlin
PixaCalendar(
    mode = DateSelectionMode.Range,
    minDate = LocalDate(2026, 1, 1),
    maxDate = LocalDate(2026, 12, 31),
    maxRangeDays = 14,
    onRangeSelected = { start, end -> /* ... */ }
)

// Two months side by side ("Double Picker" in the eBay spec)
PixaCalendar(
    mode = DateSelectionMode.Single,
    doubleMonth = true,
    onDateSelected = { date -> /* ... */ }
)
```

#### PixaHeatmapCalendar

**Import**: `com.pixamob.pixacompose.components.inputs.PixaHeatmapCalendar`
**File**: `components/inputs/Calendar.kt`

Kizitonwose-backed activity heatmap over a date range — a Pixa-native browsing/visualization
surface (not part of the eBay Calendar spec), kept in the Calendar family because it is
month-grid presentation, not a date-field input flow. Drives per-day visualization from
`CalendarConfig`'s `activityDots`/`heatmapIntensity` maps.

```kotlin
PixaHeatmapCalendar(
    calendarConfig = CalendarConfig(
        heatmapIntensity = mapOf(LocalDate(2026, 7, 10) to 0.8f)
    ),
    onDateSelected = { date -> /* ... */ }
)
```

#### PixaDatePicker

**Import**: `com.pixamob.pixacompose.components.inputs.PixaDatePicker`
**File**: `components/inputs/DatePicker.kt`

**Variants**: `DatePickerVariant.Calendar`, `Wheel`, `HeatMap`, `MonthDayPicker`, `WeekdayPicker`, `MonthPicker`, `DayCountPicker`, `SchedulePicker`

**Selection Modes**: `DateSelectionMode.Single`, `Multiple`, `Range`

`Calendar` and `HeatMap` variants internally compose `PixaCalendar`/`PixaHeatmapCalendar` —
`PixaDatePicker` owns only the field/trigger surface, callback wiring, and the other
non-calendar variants (Wheel, and the recurrence-style pickers below); it holds no
grid/month-nav code of its own.

```kotlin
var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

PixaDatePicker(
    variant = DatePickerVariant.Calendar,
    mode = DateSelectionMode.Single,
    onDateSelected = { epochMillis ->
        selectedDate = epochMillis.toLocalDate()
    }
)

// Field-first: shows a compact date field that expands PixaCalendar inline when tapped,
// instead of always rendering the calendar surface (opt-in; default is the always-visible
// behavior above).
PixaDatePicker(
    variant = DatePickerVariant.Calendar,
    asField = true,
    onDateSelected = { epochMillis -> selectedDate = epochMillis.toLocalDate() }
)

// Schedule Picker (repeat intervals) — a recurrence-selector taxonomy, not a date-value
// picker: WeekdayPicker/MonthPicker/DayCountPicker/MonthDayPicker/SchedulePicker all pick
// an abstract weekday/month/day-count index rather than a LocalDate.
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
    variant = TimePickerVariant.Wheel,   // required
    mode = TimeSelectionMode.Single,
    format = TimeFormat.Hour12,
    initialTime = appointmentTime,
    onTimeSelected = { appointmentTime = it }
)
```

#### PixaColorPicker

**Import**: `com.pixamob.pixacompose.components.inputs.PixaColorPicker`
**File**: `components/inputs/ColorPicker.kt`

Presentation-agnostic color-selection primitive — 2D saturation/value field, hue slider,
optional alpha slider, live preview swatch, hex + R/G/B entry, and an optional recent-colors
row. Renders inline content only; use [ColorPickerDialog]/[ColorPickerSheet] (thin wrappers
around `PixaDialog`/`PixaSheet`) to launch it as a dialog or bottom sheet, or embed
`PixaColorPicker` directly in any other container.

Recent colors live on `ColorPickerState` (seed via `rememberColorPickerState`'s
`initialRecentColors`, persisted across recomposition/config changes) but nothing is added
implicitly while dragging — call `state.commitToRecents()` at the point selection is
confirmed (both wrappers already do this on confirm/dismiss).

```kotlin
val colorState = rememberColorPickerState(initialColor = Color.Red)

// Inline
PixaColorPicker(
    state = colorState,
    showAlpha = true,
    showRgbFields = true
)

// Dialog wrapper
var showPicker by remember { mutableStateOf(false) }
if (showPicker) {
    ColorPickerDialog(
        onDismissRequest = { showPicker = false },
        onColorConfirmed = { selected -> /* use selected */ }
    )
}

// Bottom sheet wrapper
ColorPickerSheet(
    onDismissRequest = { showSheet = false },
    onColorConfirmed = { selected -> /* use selected */ }
)
```

**Note**: there's no platform screen-color-sampling API in PixaCompose. Pass
`eyedropperIcon`/`onEyedropperClick` to surface a dropper action — the caller owns the actual
sampling implementation.

#### PixaToggleButtonGroup

**Import**: `com.pixamob.pixacompose.components.inputs.PixaToggleButtonGroup`
**File**: `components/inputs/ToggleButtonGroup.kt`

Single- or multi-select **option tiles** — each option is a bordered container with a title, optional
subtitle, and optional lead icon/image. Selection is shown by the tile's own border and background
(never a checkbox, radio, or checkmark). Distinct from [PixaButtonGroup](#pixabuttongroup), which
groups plain actions.

**Note**: both modes toggle *off* — re-tapping a selected option in `Single` mode clears the
selection, so `selectedIds` may legitimately be empty.

```kotlin
PixaToggleButtonGroup(
    options = listOf(
        ToggleOption(id = "std", title = "Standard", subtitle = "3–5 business days"),
        ToggleOption(id = "exp", title = "Express", subtitle = "Next business day")
    ),
    selectedIds = selected,
    onSelectionChange = { selected = it },
    selectionMode = ToggleGroupSelectionMode.Single,  // or Multi
    layout = ToggleGroupLayout.ListView              // Minimal / ListView / Gallery
)

// Convenience wrappers
SingleToggleButtonGroup(options = options, selectedId = id, onSelectionChange = { id = it })
MultiToggleButtonGroup(options = options, selectedIds = ids, onSelectionChange = { ids = it })
```

#### PixaPinCode

**Import**: `com.pixamob.pixacompose.components.inputs.PixaPinCode`
**File**: `components/inputs/PinCode.kt`

Fixed-length code entry rendered as one box per digit.

```kotlin
PixaPinCode(
    value = code,
    onValueChange = { code = it },
    length = 6,
    variant = PinCodeVariant.Masked,  // or Unmasked
    isError = isInvalid,
    isLoading = isVerifying
)

// Convenience wrappers
MaskedPinCode(value = code, onValueChange = { code = it })
UnmaskedPinCode(value = code, onValueChange = { code = it })
```

#### QuantityStepper

**Import**: `com.pixamob.pixacompose.components.inputs.QuantityStepper`
**File**: `components/inputs/QuantityStepper.kt`

Increment/decrement control bounded by `min`/`max`.

```kotlin
QuantityStepper(
    value = quantity,
    onValueChange = { quantity = it },
    min = 1,
    max = 10,
    variant = QuantityStepperVariant.Narrow,
    valueLabel = { "$it items" }
)

// Convenience wrappers
WideQuantityStepper(value = qty, onValueChange = { qty = it }, min = 0, max = 99)
TimeQuantityStepper(minutes = mins, onMinutesChange = { mins = it }, minMinutes = 0, maxMinutes = 60)
```

#### PixaStarRating

**Import**: `com.pixamob.pixacompose.components.inputs.PixaStarRating`
**File**: `components/inputs/StarRating.kt`

Star rating display and input. Pass `onValueChange` to make it interactive; omit it for a read-only
rating. `StarRatingSize` is its own scale, not `SizeVariant`.

```kotlin
PixaStarRating(
    variant = StarRatingVariant.Default,
    size = StarRatingSize.Medium,
    value = 4.5f,
    trailingText = "(128)"
)

// Interactive
PixaStarRating(
    variant = StarRatingVariant.Default,
    size = StarRatingSize.Large,
    value = rating.toFloat(),
    onValueChange = { rating = it }
)
```

---

### Display

#### PixaContentCard

**Import**: `com.pixamob.pixacompose.components.display.PixaContentCard`
**File**: `components/display/ContentCard.kt`

The reusable card family. Built on `PixaSurfaceCard` (`components/surfaces/Card.kt` — the bare
container primitive; see [below](#pixasurfacecard)). `PixaContentCard` is the base anatomy:
composable slots for media, leading icon/avatar, eyebrow, title/subtitle, body, metadata, status/
progress/rating, primary/secondary actions, and footer — every slot optional, collapsing with no
leftover spacing when omitted.

```kotlin
PixaContentCard(
    leading = { PixaIcon(source = IconSource.Vector(Icons.Default.Person), contentDescription = null) },
    title = "Card title",
    subtitle = "Supporting subtitle",
    body = "Body copy wraps up to bodyMaxLines.",
    metadata = listOf("Author name", "2h ago"), // auto-joined with "•"
    onClick = { open() }
)
```

**Archetype presets** — each a thin wrapper over `PixaContentCard`, extracted from real-app card
examples cited in the [Justinmind card UI article](https://www.justinmind.com/ui-design/cards) and
grouped by anatomy rather than one-per-example:

| Preset | Real-app inspiration | Anatomy |
|---|---|---|
| `PixaProductCard` | Shopping/headphones/shoe/hair-care apps, AliExpress, NFT marketplaces | media, eyebrow (discount/rarity), title, metadata (price/rating), cart action |
| `PixaArticleCard` | Space-news portal, magazine app, Sky News, streaming service, e-learning courses | media, eyebrow (category/status), title, body (excerpt), metadata (author/date) |
| `PixaBookingCard` | Hotel/travel booking sites, Skyscanner, Wander, meeting-room booking | media, title, metadata (location/price), status (rating), CTA |
| `PixaTaskCard` | Trello (kanban task item) | compact, leading, eyebrow (label), title, metadata (assignee/due date) |
| `PixaPinCard` | Pinterest (masonry pin grid) | variable-height media, optional title, trailing overflow menu |
| `PixaAppCard` | Google Play (horizontal app rows) | compact icon + name + rating tile |
| `PixaContactCard` | Supperto/Teamup (contacts, conversations, hiring rows) | leading avatar, title/subtitle, trailing accessory, metadata |
| `PixaStatCard` | Finalytic/Skillex/Savings-app dashboards | leading icon, value, label, trend and/or progress bar |
| `PixaActionCard` | Settings rows, feature dashboards, mood/session pickers | `Row` (leading label) or `Centered` (focal icon) layout, optional CTA |
| `PixaSelectCard` | Travel-activity toggles, e-learning status filters | owns the toggle interaction model (`selected` + `Role.Checkbox`/`RadioButton`) |
| `PixaMessageCard` | Promo/campaign notification banners | heading/paragraph/CTA + top-or-trailing artwork with a background-adaptive CTA chip |

```kotlin
PixaProductCard(
    media = PixaCardMedia(source = PixaImageSource.Url(url), height = 160.dp),
    title = "Wireless headphones",
    eyebrow = { PixaTag(text = "Sale", color = TagColor.Error) },
    metadata = listOf("$79.99", "4.6 ★"),
    primaryAction = PixaCardAction(label = "Add to cart", onClick = { addToCart() })
)

PixaMessageCard(
    heading = "Free delivery",
    paragraph = "On orders over $30.",
    ctaText = "Learn more",
    onCtaClick = { open() },
    artwork = MessageCardArtwork(source = PixaImageSource.Url(bannerUrl), position = MessageCardArtworkPosition.Top)
)
```

Testimonial, pricing, and multi-stat summary cards from the article don't get dedicated presets —
their anatomy is fully expressible via `PixaContentCard`'s existing slots (e.g. testimonial:
`body` = quote, `status` = a `PixaStarRating`, `footer` = an avatar + author row).

#### PixaBanner

**Import**: `com.pixamob.pixacompose.components.display.PixaBanner`
**File**: `components/display/Banner.kt` (new)

New component, migrated from the [eBay Playbook Banner](https://playbook.ebay.com/design-system/components/banner) spec (2026-07-18). A full-bleed, expressive promo/marketing banner ("curations, promotions, events, and programs with a CTA") — distinct from `PixaSystemBanner` (`components/feedback/SystemBanner.kt`), which is for system status, not promotions.

**Anatomy**: overline → headline → body (hidden below 600dp width) → single action button → disclaimer, laid over `background`.

**`BannerBackground` variants** (sealed class — each carries its own required content):
| Variant | Content |
|---|---|
| `Image` | Full-bleed photo; text gets an automatic scrim + radial gradient + drop shadow for legibility |
| `Color` | Solid color background, optional foreground PNG image, caller-supplied colors |
| `InsetImage` | Fixed light-neutral background + black text, rounded-corner inset image (not caller-themed) |
| `MultiDestination` | Solid color background + a scrollable rail of independently-tappable `BannerDestination`s |
| `Loyalty` | Solid color background, no image — for dense, height-constrained pages |

**Sizing**: responsive by construction (`Modifier.aspectRatio`, no fixed height) — height scales with measured width per the source. `height: BannerHeight` (`Tall`/`Short`) adjusts the ratio; the source confirms both tiers exist and their use-cases but not the exact size delta between them (approximated).

```kotlin
PixaBanner(
    headline = "Summer Sale",
    actionLabel = "Shop now",
    onActionClick = { navigateToSale() },
    background = BannerBackground.Image(painter = saleImage),
    overline = "Limited time",
    onClick = { navigateToSale() }
)
```

Multiple banners: `PixaBannerCarousel(banners = listOf({ PixaBanner(...) }, { PixaBanner(...) }))` — stacks vertically with no controls below 600dp width ("we do not use carousels" on small screens per the source); auto-scrolls with dot indicators + back/forward/pause-play controls at 600dp+.

> `Color`/`MultiDestination`/`Loyalty` take literal `Color` values, not `AppTheme.colors` tokens — the source requires banner colors to stay fixed across light/dark theme switches ("in dark mode, banners do not change color"), which theme tokens don't guarantee.
> Content-length guidance from the source (overline/headline ≤33 chars, body/disclaimer ≤65 chars, button label 1–4 words) is documented in KDoc only, not runtime-enforced.

#### PixaAvatar

**Import**: `com.pixamob.pixacompose.components.display.PixaAvatar`
**File**: `components/display/Avatar.kt`

**Sizes**: `SizeVariant.Nano` (16dp), `Compact` (24dp), `Small` (32dp), `Medium` (40dp), `Large` (48dp), `Huge` (64dp), `Massive` (96dp)

**Shapes**: `AvatarShape.Circle`, `Rounded`

```kotlin
PixaAvatar(
    imageUrl = user.photoUrl,
    text = user.initials, // Shown when no image is available
    size = SizeVariant.Large,
    shape = AvatarShape.Circle,
    onClick = { navigateToProfile() }
)

// Avatar group (stacked avatars)
PixaAvatarGroup(
    avatars = listOf(avatar1, avatar2, avatar3),
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

Multiple source types using Coil 3.

**Aspect ratio**: `PixaImageRatio` frames the container — `Square` (1:1), `Portrait3x4`,
`Landscape4x3`, `Wide16x9`, `Tall9x16`, or `Original`. The default `Original` imposes **no** frame, so
the image keeps its natural proportions — use it for hero, gallery, and masonry surfaces, and pick a
framed ratio for uniform surfaces such as result tiles and mixed carousels.

**Image integrity**: `ContentScale.Crop` (fill and clip) and `ContentScale.Fit` (letterbox) both
preserve proportions. `ContentScale.FillBounds` stretches the image and logs a runtime warning.
`alignment` doubles as the focal point, deciding which part survives when a framed ratio crops.

```kotlin
// URL image, framed to 1:1
PixaImage(
    source = PixaImageSource.Url(product.imageUrl),
    contentDescription = product.name,
    modifier = Modifier.fillMaxWidth(),
    ratio = PixaImageRatio.Square,
    contentScale = ContentScale.Crop,
    alignment = Alignment.TopCenter,  // focal point for the crop
    crossfade = true
)

// Hero image — keeps the original ratio
PixaImage(
    source = PixaImageSource.Url(hero.imageUrl),
    contentDescription = hero.title,
    modifier = Modifier.fillMaxWidth(),
    ratio = PixaImageRatio.Original,
    contentScale = ContentScale.Fit
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

#### PixaMediaContainer

**Import**: `com.pixamob.pixacompose.components.display.PixaMediaContainer`
**File**: `components/display/MediaContainer.kt`

Migrated from eBay Playbook's Media Container spec. A framed surface for still/animated media with
three anatomy layers: **Matte** (background, adapts light/dark) → **Media** (the content) →
**Scrim** (a toggleable, light radial overlay that stays constant across light/dark mode).

**Ratio**: `MediaContainerRatio` — `Square` (1:1), `Portrait2x3`, `Portrait4x5`, or `Wide16x9`. Only
these four are exposed; the spec calls arbitrary ratios out as introducing inconsistency.

**Fill type**: `MediaContainerFillType.Fill` (default, crops to fill), `.Fit` (letterboxes over the
matte), or `.Auto` (fills when the source's own ratio is within `autoFillTolerance`, default 75%, of
the container ratio — otherwise falls back to `.Fit`). `.Auto` only has a source ratio to compare for
`PixaImageSource.Url`/`Resource`/`DrawableResource`; vector/SVG sources and custom media behave like
`.Fit` under `.Auto`.

**Media source**: `MediaContainerSource.Image` reuses `PixaImage`'s existing `PixaImageSource` types.
`MediaContainerSource.Custom` is the escape hatch for media types the spec covers that PixaCompose has
no renderer for yet — video, GIF, 3D assets, Rive animations — callers supply their own composable and
the container still provides ratio framing, matte, scrim, and the disabled treatment around it.

**Corner radius is not customizable**: the container measures itself and applies 16dp
(`AppTheme.shapes.rounded.extraLarge`) at 80dp x 80dp and above, 8dp (`.medium`) below — matching the
spec's threshold exactly. The spec explicitly discourages border-radius and scrim-opacity overrides,
so neither is exposed as a parameter.

**Disabled state**: `enabled = false` desaturates and dims the media layer to 40% opacity (the same
value `PixaAvatar` already uses for disabled images).

```kotlin
// Product image, square, fill (the common case)
PixaMediaContainer(
    url = product.imageUrl,
    contentDescription = product.name,
    modifier = Modifier.fillMaxWidth(),
    ratio = MediaContainerRatio.Square
)

// Letterboxed hero, matte visible around the content
PixaMediaContainer(
    source = MediaContainerSource.Image(PixaImageSource.Url(hero.imageUrl)),
    contentDescription = hero.title,
    modifier = Modifier.fillMaxWidth(),
    ratio = MediaContainerRatio.Wide16x9,
    fillType = MediaContainerFillType.Fit
)

// Custom media — e.g. a caller-supplied video player, since PixaCompose has no built-in one
PixaMediaContainer(
    source = MediaContainerSource.Custom { MyVideoPlayer(videoUrl) },
    modifier = Modifier.fillMaxWidth(),
    ratio = MediaContainerRatio.Wide16x9
)
```

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
PixaCandlestickChart(          // OHLC / financial series
    data = ohlcData,           // List<OhlcData>
    chartHeight = ChartHeight.Medium
)

TrendChart(data = trendData, modifier = Modifier.height(200.dp))
ComparisonChart(dataSets = listOf("A" to seriesA, "B" to seriesB))
MultiLineChart(dataSets = listOf("Revenue" to revenue, "Cost" to cost))
```

#### PixaDivider

**Import**: `com.pixamob.pixacompose.components.display.PixaDivider`
**File**: `components/display/Divider.kt`

```kotlin
PixaDivider()                                // Horizontal, 1dp
HorizontalDivider(thickness = 2.dp)          // Thicker
VerticalDivider(modifier = Modifier.height(24.dp))  // Vertical
```

#### PixaListItem

**Import**: `com.pixamob.pixacompose.components.display.PixaListItem`
**File**: `components/display/ListItem.kt`

Standard list row. `leading`/`trailing` are sealed slots (`ListItemLeading` / `ListItemTrailing`)
rather than loose painters, so each accessory kind renders with its own correct anatomy.

```kotlin
PixaListItem(
    title = "Notifications",
    subtitle = "Push, email and SMS",
    onClick = { open() },
    variant = ListItemVariant.FullWidth,
    density = ListItemDensity.Standard,
    leading = ListItemLeading.Icon(painterResource(Res.drawable.ic_bell)),
    trailing = ListItemTrailing.Off
)

// Selection row
SelectionListItem(title = "Dark theme", selected = isDark, onClick = { toggle() })
```

#### PixaTile

**Import**: `com.pixamob.pixacompose.components.display.PixaTile`
**File**: `components/display/Tile.kt`

Compact tappable tile with optional artwork and trailing control.

```kotlin
PixaTile(
    label = "Payments",
    onClick = { open() },
    behavior = TileBehavior.Action,
    artwork = TileArtwork.Off,
    paragraphs = listOf("Manage cards and billing"),
    contentAlignment = TileContentAlignment.Start
)
```

#### PixaSectionHeading

**Import**: `com.pixamob.pixacompose.components.display.PixaSectionHeading`
**File**: `components/display/SectionHeading.kt`

Section title with an optional subheading and a trailing accessory.

```kotlin
PixaSectionHeading(
    heading = "Recent orders",
    subheading = "Last 30 days",
    trailing = SectionHeadingTrailing.TextButton(text = "See all", onClick = { seeAll() })
    // also: SectionHeadingTrailing.None / IconButton(...) / Labels(...)
)
```

#### PixaTag

**Import**: `com.pixamob.pixacompose.components.display.PixaTag`
**File**: `components/display/Tag.kt`

Compact status/metadata label. `TagType.Display` is non-interactive; other types support
`onClick`/`onDismiss`.

```kotlin
PixaTag(
    text = "In stock",
    hierarchy = TagHierarchy.Secondary,
    color = TagColor.Neutral,
    type = TagType.Display
)
```

#### PixaCarousel

**Import**: `com.pixamob.pixacompose.components.display.PixaCarousel`
**File**: `components/display/Carousel.kt` (new)

New component, migrated from the [Ant Design](https://ant.design/components/carousel) and
[eBay Playbook](https://playbook.ebay.com/design-system/components/carousel) Carousel specs
(2026-07-18). Generic, slot-based carousel — every slide is an arbitrary composable, not an
image. Not to be confused with `PixaBannerCarousel` (`Banner.kt`), the unrelated full-bleed
promo-banner pager.

**Anatomy** (eBay Playbook): optional container-level `title` + "see all" link (rendered via
`PixaSectionHeading`) → horizontally scrollable slides (`HorizontalPager`) → pagination dots
(`PixaPagerIndicator`) → optional arrow buttons. The group title/see-all pair is intentionally
container-level, not per-slide — it names the whole group, per eBay's "include a title on each
carousel" guidance. Per-slide structured metadata (title/description/content slot) is a separate,
optional concern: use `PixaCarouselItem` with the `items: List<PixaCarouselItem>` overload, or
skip it entirely with the primary `itemContent: @Composable (Int) -> Unit` API.

**Interaction**: swipe/drag/fling (native `HorizontalPager`), tap indicator dots or arrow buttons,
keyboard Left/Right arrows once the carousel is focused (RTL-aware). Arrows are caller-supplied —
there's no bundled icon set in this repo — so they only render when `arrowIcon` (a single
chevron-left `Painter`, rotated 180° for "next") is provided.

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `itemCount` / `items` | `Int` / `List<PixaCarouselItem>` | required | Slide count, or structured slides |
| `itemContent` | `@Composable (Int) -> Unit` | required (index overload only) | Per-slide content |
| `title` / `onSeeAllClick` | `String?` / `(() -> Unit)?` | `null` | Container-level heading + "see all" |
| `arrowVisibility` | `CarouselArrowVisibility` | `Adaptive` | `Hidden`/`HoverVisible`/`AlwaysVisible`/`Adaptive` (maps `AppTheme.windowSizeClass`) |
| `transitionEffect` | `CarouselTransitionEffect` | `Scroll` | `Scroll` or `Fade` |
| `autoPlay` / `autoPlayIntervalMillis` | `Boolean` / `Long` | `false` / `3000` | Auto-advance |
| `infiniteScroll` | `Boolean` | `false` | Wraparound looping |
| `adaptiveHeight` | `Boolean` | `false` | Resize to current slide's height on settle |

```kotlin
PixaCarousel(
    items = listOf(
        PixaCarouselItem(title = "Wireless Headphones", description = "$79.99") { PixaImage(...) },
        PixaCarouselItem(title = "Smart Watch", description = "$199.99") { PixaImage(...) }
    ),
    title = "Recommended for you",
    onSeeAllClick = { seeAll() },
    arrowIcon = painterResource(Res.drawable.ic_chevron_left)
)
```

> eBay's fractional tile-count/partial-peek breakpoint table (1.5–2.5 tiles on small screens up
> to 5 on x-large) doesn't map 1:1 onto Pixa's 3-tier `WindowSizeClass` — use `contentPadding` and
> `slideSpacing` directly to achieve a peek effect for your own layout instead.
> `infiniteScroll`, `adaptiveHeight`, and `transitionEffect = Fade` are confirmed Ant Design
> features implemented as approximations — see the KDoc on `PixaCarousel` for the exact caveats.

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

#### Badges

**Import**: `com.pixamob.pixacompose.components.feedback.PixaNotificationBadge`
**File**: `components/feedback/Badge.kt`

There is no single `PixaBadge`. The family is two components with distinct jobs, plus a positioning
wrapper:

- **`PixaNotificationBadge`** — carries content: a `count`, `text`, or `icon`.
- **`PixaHintBadge`** — a bare dot with no content, for "something changed here".
- **`BadgedBox`** — positions any badge over its content.

**Variants**: `BadgeVariant.Accent` (default), `Success`, `Warning`, `Error`, `OnBrand`.
There is no `BadgeStyle`.

```kotlin
// Count badge over an icon
BadgedBox(
    badge = { PixaNotificationBadge(count = unreadCount, variant = BadgeVariant.Error, maxCount = 99) }
) {
    PixaIcon(painter = painterResource(Res.drawable.ic_notifications))
}

// Bare dot
BadgedBox(badge = { PixaHintBadge(variant = BadgeVariant.Accent) }) {
    PixaIcon(painter = painterResource(Res.drawable.ic_settings))
}
```

> `OnBrand` exists for badges sitting on brand-colored surfaces, where a colored badge would lose
> contrast.

#### Skeleton

**Import**: `com.pixamob.pixacompose.components.feedback.Skeleton`
**File**: `components/feedback/Skeleton.kt`

Loading placeholders with shimmer animation.

**Variants**: `Skeleton()`, `SkeletonCircle()`, `SkeletonText()`, `SkeletonImage()`, `SkeletonButton()`, `SkeletonCard()`, `SkeletonListItem()`, `SkeletonAvatarWithText()`, `SkeletonGrid()`, `SkeletonList()`, `SkeletonCustom()`

```kotlin
if (isLoading) {
    SkeletonCard()
    SkeletonText(width = 120.dp)
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
    segments = listOf(
        ProgressSegment(progress = 1f),
        ProgressSegment(progress = 0.5f),
        ProgressSegment(progress = 0f)
    )
)

// Page indicator (dots)
PixaPagerIndicator(pageCount = 5, currentPage = 2)

// Loading spinner
LoadingIndicator(modifier = Modifier.size(24.dp))

// Compact pill showing progress inline
PixaProgressPill(progress = 0.4f)
```

**Also in this family**: `ProgressBar`, `SegmentedProgressIndicator`, `PagerIndicator`.

#### PixaEmptyState

**Import**: `com.pixamob.pixacompose.components.feedback.PixaEmptyState`
**File**: `components/feedback/EmptyState.kt`

Supports up to two actions (`primaryActionText`/`onPrimaryAction` and the secondary pair). The body
text is `description`, not `message`.

```kotlin
PixaEmptyState(
    type = EmptyStateType.Empty.NoContent,
    title = "No messages",
    description = "You don't have any messages yet",
    icon = painterResource(Res.drawable.ic_inbox_empty),
    primaryActionText = "Compose message",
    onPrimaryAction = { navigateToCompose() }
)

// Specialized variants
EmptyContent()
EmptySearchResults(query = "keyword")
NetworkError(onRetry = { retry() })
ServerError(onRetry = { retry() })
PermissionDenied(onRequestAccess = { request() }, onSignIn = { signIn() })
```

#### PixaSystemBanner

**Import**: `com.pixamob.pixacompose.components.feedback.PixaSystemBanner`
**File**: `components/feedback/SystemBanner.kt`

Persistent, app-level status message. Unlike [Toast](#toast)/[Snackbar](#snackbar) it does not
auto-dismiss — use it for conditions that stay true (offline, degraded service, maintenance).

```kotlin
PixaSystemBanner(
    visible = isOffline,
    message = "You're offline. Some features are unavailable.",
    variant = SystemBannerVariant.Accent,
    action = SystemBannerAction.Single(onClick = { retry() }),
    // also: SystemBannerAction.None / Dual(...)
    dismissible = true,
    onDismiss = { dismissed = true }
)
```

---

### Navigation

#### PixaTopNavBar

**Import**: `com.pixamob.pixacompose.components.navigation.PixaTopNavBar`
**File**: `components/navigation/TopNavBar.kt`

**Sizes**: `SizeVariant.Small` through `Huge`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `title` | `String?` | `null` | Nav bar title |
| `subtitle` | `String?` | `null` | Secondary line under the title |
| `startActions` | `List<TopNavAction>` | `emptyList()` | Leading actions (back/menu live here) |
| `endActions` | `List<TopNavAction>` | `emptyList()` | Trailing actions |
| `collapsed` | `Boolean` | `false` | Collapsed treatment |
| `size` | `SizeVariant` | `Medium` | Size preset |

There is no `navigationIcon`/`onNavigationClick` — the leading icon is just the first `startActions`
entry. `TopNavAction` fields are `icon`, `description`, `onClick`, `enabled`, `badge`, `tint`.

```kotlin
PixaTopNavBar(
    title = "My App",
    startActions = listOf(
        TopNavAction(
            icon = painterResource(Res.drawable.ic_menu),
            description = "Open navigation",
            onClick = { openDrawer() }
        )
    ),
    endActions = listOf(
        TopNavAction(
            icon = painterResource(Res.drawable.ic_search),
            description = "Search",
            onClick = { openSearch() }
        ),
        TopNavAction(
            icon = painterResource(Res.drawable.ic_more),
            description = "More",
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

Side navigation drawer. Items are grouped into `DrawerSection`s and identified by a stable `id`
(not an index). The drawer renders itself as an overlay — it does not wrap your screen content.

```kotlin
PixaDrawer(
    visible = isOpen,
    onDismiss = { isOpen = false },
    sections = listOf(
        DrawerSection(
            title = "Main",
            items = listOf(
                DrawerItem(id = "home", title = "Home", icon = painterResource(Res.drawable.ic_home)),
                DrawerItem(id = "settings", title = "Settings", icon = painterResource(Res.drawable.ic_settings), badge = "3")
            )
        ),
        DrawerSection(items = listOf(DrawerItem(id = "help", title = "Help")))
    ),
    selectedItemId = selectedId,
    onItemClick = { item ->
        selectedId = item.id
        isOpen = false
    },
    position = DrawerPosition.Start,
    header = { DrawerHeader() },
    footer = { DrawerFooter() }
)
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

Actions are text + callbacks (`confirmText`/`onConfirm`, `dismissText`/`onDismiss`) — not composable
button slots. `onDismissRequest` is the required dismissal callback.

```kotlin
if (showDialog) {
    PixaDialog(
        onDismissRequest = { showDialog = false },
        variant = DialogVariant.Destructive,
        title = "Confirm delete",
        message = "Are you sure? This cannot be undone.",
        confirmText = "Delete",
        onConfirm = { deleteItem(); showDialog = false },
        dismissText = "Cancel",
        onDismiss = { showDialog = false }
    )
}

// Convenience variants
PixaAlertDialog(title = "Attention", message = "Something happened")
PixaConfirmDialog(title = "Confirm", message = "Are you sure?", onConfirm = { confirm() })
PixaDestructiveDialog(title = "Delete", message = "This cannot be undone", onConfirm = { delete() })
```

> **`PixaBottomSheet` has been replaced.** `components/overlay/BottomSheet.kt` no longer exists — the
> sheet is now `PixaSheet` in the [Surfaces](#surfaces) category. See [PixaSheet](#pixasheet).

#### PixaMenu

**Import**: `com.pixamob.pixacompose.components.overlay.PixaMenu`
**File**: `components/overlay/Menu.kt`

```kotlin
var showMenu by remember { mutableStateOf(false) }

Box {
    PixaIconButton(onClick = { showMenu = true }, icon = painterResource(Res.drawable.ic_more_vert))

    // Simple flat-item API. Selection is reported by onItemClick, not per-item callbacks.
    PixaMenu(
        visible = showMenu,
        onDismiss = { showMenu = false },
        items = listOf(
            MenuItem(id = "edit", title = "Edit"),
            MenuItem(id = "share", title = "Share"),
            MenuItem(id = "delete", title = "Delete", type = MenuItemType.Destructive)
        ),
        onItemClick = { item -> handleAction(item.id) }
    )
}
```

`PixaMenuContent` is the richer API, taking the full `MenuContent` list — `Item`, `Chevron`,
`Grabber`, `Search`, `Header`, `Paragraph`, and `Divider`:

```kotlin
PixaMenuContent(
    visible = showMenu,
    onDismiss = { showMenu = false },
    content = listOf(
        MenuContent.Header("Actions"),
        MenuContent.Item(MenuItem(id = "copy", title = "Copy")),
        MenuContent.Divider,
        MenuContent.Item(MenuItem(id = "delete", title = "Delete", type = MenuItemType.Destructive))
    ),
    onItemClick = { handleAction(it.id) }
)
```

#### PixaPopover

**Import**: `com.pixamob.pixacompose.components.overlay.PixaPopover`
**File**: `components/overlay/Popover.kt`

**Positions**: `PopoverPosition.BottomCenter` (default) and the other `PopoverPosition` entries.
There is no `PopoverPlacement`.

`PixaPopover` is visibility-controlled and takes a required `heading`:

```kotlin
var showInfo by remember { mutableStateOf(false) }

PixaPopover(
    visible = showInfo,
    onDismiss = { showInfo = false },
    heading = "About this field",
    body = "We use this to personalise your results.",
    position = PopoverPosition.BottomCenter
)
```

#### PixaTooltip

**Import**: `com.pixamob.pixacompose.components.overlay.PixaTooltip`
**File**: `components/overlay/Tooltip.kt`

**Positions**: `TooltipPosition.Top`, `Bottom`, `Start`, `End`
**Variants**: `TooltipVariant.Prompted` (default), `Unprompted`
**Pointer**: `TooltipPointerAlignment.Leading`, `Center`, `Trailing`

`PixaTooltip` is visibility-controlled — you own the `visible` state:

```kotlin
var showTip by remember { mutableStateOf(false) }

PixaTooltip(
    tooltip = "Click to copy",
    visible = showTip,
    position = TooltipPosition.Bottom,
    variant = TooltipVariant.Prompted,
    autoDismissMs = 3000,
    onDismiss = { showTip = false }
) {
    PixaIconButton(onClick = { showTip = true }, icon = painterResource(Res.drawable.ic_copy))
}
```

`PixaTooltipBox` is the simpler wrapper that manages its own visibility:

```kotlin
PixaTooltipBox(tooltip = "Click to copy", position = TooltipPosition.Top) {
    PixaIconButton(onClick = { copy() }, icon = painterResource(Res.drawable.ic_copy))
}
```

### Surfaces

Container surfaces that host other content. This category replaces the former
`components/overlay/BottomSheet.kt`.

#### PixaSheet

**Import**: `com.pixamob.pixacompose.components.surfaces.PixaSheet`
**File**: `components/surfaces/Sheet.kt`

Replaces the removed `PixaBottomSheet`. `Expandable` sheets show a grabber and move through the
`SheetSnapPoint` ladder (`Collapsed`/`Middle`/`Expanded`); `Fixed` sheets hug their content.

```kotlin
if (showSheet) {
    PixaSheet(
        onDismissRequest = { showSheet = false },
        title = "Filter options",
        presentation = SheetPresentation.Modal,       // or NonModal
        expandability = SheetExpandability.Expandable, // or Fixed
        initialSnapPoint = SheetSnapPoint.Middle
    ) {
        FilterOptions()
    }
}

// Convenience wrappers
FixedDetailSheet(onDismissRequest = { close() }, title = "Details") { DetailBody() }
FilterSheet(onDismissRequest = { close() }, title = "Filters") { Filters() }
```

#### PixaFullScreenModal

**Import**: `com.pixamob.pixacompose.components.surfaces.PixaFullScreenModal`
**File**: `components/surfaces/FullScreenModal.kt`

For self-contained flows that take over the screen. Not an option-list presenter — prefer
[PixaSheet](#pixasheet) or `PixaDropdown`'s own adaptive presentation for option selection.

```kotlin
if (showModal) {
    PixaFullScreenModal(
        onDismissRequest = { showModal = false },
        title = "Edit profile",
        presentation = FullScreenModalPresentation.StackedSheet,
        confirmText = "Save",
        dismissText = "Cancel",
        onConfirm = { save() }
    ) {
        ProfileForm()
    }
}

// Convenience wrappers
ImmersiveFullScreenModal(onDismissRequest = { close() }, title = "Gallery") { Gallery() }
TaskFullScreenModal(onDismissRequest = { close() }, title = "Checkout", onConfirm = { pay() }) { Checkout() }
```

#### PixaSurfaceCard

**Import**: `com.pixamob.pixacompose.components.surfaces.PixaSurfaceCard`
**File**: `components/surfaces/Card.kt`

The Uber Base-aligned card container primitive — background/border/shape/elevation/dismiss/
loading, no anatomy of its own. `Isolated` insets with a corner radius so it stands out; `Feed` is
full-width with a module divider between cards. Build a finished card on top of it with
[PixaContentCard](#pixacontentcard) (`components/display/ContentCard.kt`), or compose your own
layout directly in `content` for anything simpler.

```kotlin
PixaSurfaceCard(
    context = SurfaceCardContext.Isolated,  // or Feed
    onClick = { open() },
    selected = isSelected,
    onDismiss = { dismiss() }
) {
    CardBody()
}
```

#### PixaCard

**Import**: `com.pixamob.pixacompose.components.surfaces.PixaCard`
**File**: `components/surfaces/Card.kt`

Legacy `ConstraintLayout`-based card container, kept only because `BottomNavBar`, `Stepper`,
`Toast`, `Alert`, and `Chart` still build on it internally. Not the base for new card-like
components — use [PixaSurfaceCard](#pixasurfacecard)/[PixaContentCard](#pixacontentcard) instead.

**Variants**: `BaseCardVariant.Elevated`, `Outlined`, `Filled`, `Tonal`, `Ghost`

```kotlin
PixaCard(
    variant = BaseCardVariant.Elevated,
    padding = SizeVariant.Medium,
    onClick = { navigateToDetail() }
) {
    val (title, description) = createRefs()
    // ConstraintLayoutScope — use constrainAs() for positioning
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

### AnimationUtils — Motion

**File**: `utils/AnimationUtils.kt`

`AnimationUtils` is the single source of truth for motion: every component animates through a named preset here, never a raw `spring()`/`tween()` call (verified: zero raw calls remain in `components/`). The `label` parameter is required on every `animateXAsState` call for debugging and testing.

#### Motion taxonomy

Presets are grouped by what the motion *communicates*, not by how they're tuned. Pick the category first, then the preset:

| Category | Communicates | Presets |
| --- | --- | --- |
| **Selection** | A user picked one option among several | `selectionSpring` |
| **Feedback** | A color/state changed in response to input | `colorSpring` |
| **Drag/gesture follow** | A value tracks a continuous user gesture in real time | `thumbSpring`, `fastSpring` |
| **Emphasis** | Draws attention to a value/status, not tied to a discrete selection | `indicatorSpring`, `slowSpring`, `repeatableAnimation` |
| **Reveal** | A component entering (appearing/expanding/sliding in) | `fadeInTransition`, `scaleInTransition`, `slideInFromBottomTransition`, `enterSnapTween()`, `AnimatedVisibilityStandard` |
| **Dismissal** | A component exiting | `fadeOutTransition`, `scaleOutTransition`, `slideOutToBottomTransition`, `passiveExitTween()`, `userDismissExitTween()` |
| **Loading** | Repeats for as long as a background operation is in flight | `infiniteRepeatable` |
| **Navigation** | Relationship between an outgoing and incoming surface | `contextChangeTransitionSpec<S>()` |
| **Surface transition** | An overlay's entry/exit implying its relationship to what's behind it (partial obstruction vs. full replacement) | `slideInFromBottomTransition`/`slideOutToBottomTransition` (partial), `contextChangeTransitionSpec<S>()` (full) |

Generic tuning factories (`standardSpring`, `standardTween`, `fastTween`, `slowTween`, `smoothSpring`, `fastSpringSpec`, `instantTween`, `emphasizedTween`) exist for cases none of the named semantic presets cover — reach for a semantic preset first.

```kotlin
// Selection: tab/segment/checkbox selection state
val fraction by animateFloatAsState(target, animationSpec = AnimationUtils.selectionSpring, label = "tab_selection")

// Feedback: color transition on state change
val bg by animateColorAsState(target, animationSpec = AnimationUtils.colorSpring, label = "card_background")

// Drag/gesture follow: slider thumb tracking a drag
val thumbX by animateFloatAsState(target, animationSpec = AnimationUtils.thumbSpring, label = "thumb_position")

// Reveal/dismissal: a component appearing/disappearing
AnimationUtils.AnimatedVisibilityStandard(
    visible = isVisible,
    enter = AnimationUtils.fadeInTransition + AnimationUtils.scaleInTransition,
    exit = AnimationUtils.fadeOutTransition + AnimationUtils.scaleOutTransition
) {
    Content()
}

// Navigation: swapping unrelated content (e.g. bottom-nav tab content)
AnimatedContent(
    targetState = selectedTab,
    transitionSpec = AnimationUtils.contextChangeTransitionSpec()
) { tab -> TabContent(tab) }
```

#### Timing tiers and easing

Every duration in `AnimationUtils` traces back to one of five named tiers in `MotionDuration` — Pixa's translation of Uber Base's timing bands:

| Tier | Duration | Use for |
| --- | --- | --- |
| `MotionDuration.Instant` | 100ms | Near-immediate feedback; a building block for other factories (e.g. the fade-out half of a context change) |
| `MotionDuration.Fast` | 150ms | Quick feedback transitions (press states, `fastTween()`) |
| `MotionDuration.Standard` | 300ms | The default for most enter/exit and color/opacity motion (`standardTween()`) |
| `MotionDuration.Slow` | 500ms | Deliberate, larger-movement transitions (`slowTween()`, `enterSnapTween()`) |
| `MotionDuration.Emphasized` | 650ms | Hero/attention moments only — use sparingly (`emphasizedTween()`) |

Four named easing curves (top-level `val`s in `AnimationUtils.kt`, Compose-friendly `CubicBezierEasing` translations of Uber Base's five easing curves — the fifth, linear, is Compose's own built-in `LinearEasing`) express *why* something is moving, not just how fast:

| Easing | Uber Base intent | Use for |
| --- | --- | --- |
| `AccelerateDecelerateEasing` | Moving/pushing an element already in view | General position/size changes |
| `DecelerateEasing` | Entering view, or snapping after a drag release | `enterSnapTween()` — sheets/drawers appearing, drag-release snaps |
| `AccelerateEasing` | Passively exiting *without* direct user interaction | `passiveExitTween()` — a snackbar/toast timing out on its own |
| `ResponsiveAccelerateEasing` | Exiting because the user *directly* dismissed it | `userDismissExitTween()` — a snackbar/toast the user tapped to close |
| `LinearEasing` (Compose built-in) | Opacity/color changes | `fadeInTransition`/`fadeOutTransition`, `colorSpring` |

The passive-vs-direct-dismiss distinction (`passiveExitTween()` vs. `userDismissExitTween()`) is documented but not yet wired into `Toast.kt`/`Snackbar.kt` — both currently animate out identically regardless of dismiss reason. Threading a dismiss-reason through their dismiss call chain is a real API change, not a drive-by animation-spec swap, so it's tracked as debt rather than forced into this pass.

#### Interaction → motion preset mapping

| Component | Category | Preset(s) used |
| --- | --- | --- |
| Tab / TabBar | Selection + Emphasis | `selectionSpring` (selected state) + `indicatorSpring` (indicator position/width) |
| BottomNavBar | Selection + Feedback | `selectionSpring` (selected item) + `colorSpring` (icon/label color); tab-content swap should use `contextChangeTransitionSpec<S>()` (Navigation) if/when it adopts `AnimatedContent` |
| Switch | Drag/gesture follow + Selection | `thumbSpring` (thumb slide) + `selectionSpring`/`colorSpring` (track color) |
| Checkbox | Selection + Feedback | `selectionSpring` (check-mark reveal) + `colorSpring` (fill color) |
| RadioButton | Selection + Feedback | `selectionSpring` (inner-dot scale) + `colorSpring` (fill/border/label color) |
| Slider / RangeSlider | Drag/gesture follow | `fastSpring` (thumb position while dragging), `fastSpringSpec()`/`smoothSpring()` (track fill) |
| Dialog | Reveal/Dismissal | Platform `Dialog()` enter/exit; content shadow via `HierarchicalSize.Shadow` (see Elevation docs) |
| BottomSheet | Reveal/Dismissal (Surface transition — partial obstruction) | `standardTween()`-based slide (`SheetEnterTransition`/`SheetExitTransition`) + fade (scrim) |
| Menu | Reveal/Dismissal | Currently a raw `Popup` show/hide with **no transition** — tracked as debt, see audit notes |
| Popover | Reveal/Dismissal | `standardTween()` (enter) + `fastTween()` (exit), fade + scale |
| Tooltip | Reveal/Dismissal | `standardTween()` (enter) + `fastTween()` (exit) fade |
| Snackbar | Reveal/Dismissal + Emphasis | `standardSpring()`/`fastSpringSpec()` slide/fade in-out; `Highest` elevation for emphasis (see Elevation docs) |
| Toast | Reveal/Dismissal + Emphasis | Same as Snackbar |
| Drawer | Reveal/Dismissal (Surface transition — partial obstruction) | `standardTween()`-based horizontal slide + fade (scrim) |
| Stepper | Emphasis | `slowSpring` (deliberate step-advance emphasis) + `colorSpring` (connector/step color) + `fastTween()` |
| Accordion | Reveal/Dismissal | `standardTween()` for expand/collapse |
| Badge (pulse/dot) | Emphasis/Loading | `repeatableAnimation`/`infiniteRepeatable` for pulsing dot badges |
| Progress indicators | Loading | `infiniteRepeatable` (indeterminate), `indicatorSpring` (determinate sweep) |

#### Audit notes (2026-07)

- Confirmed zero raw `spring()`/`tween()` calls remain in `components/` — the constraint from the prior animation pass still holds, including after this pass's additions.
- Added `MotionDuration` (5 named tiers: Instant/Fast/Standard/Slow/Emphasized) and 4 named easing curves (`AccelerateDecelerateEasing`/`DecelerateEasing`/`AccelerateEasing`/`ResponsiveAccelerateEasing`) as top-level declarations in `AnimationUtils.kt`, translated directly from Uber Base's timing spec. `standardTween`/`fastTween`/`slowTween` now reference the named tiers instead of repeating magic numbers; `instantTween()`/`emphasizedTween()` complete the 5-tier band.
- Added `enterSnapTween()`/`passiveExitTween()`/`userDismissExitTween()` — Uber Base's distinct entering-vs-passive-exit-vs-direct-dismiss timing intents, previously not expressible (the system only had generic fast/standard/slow). Available for adoption; not force-wired into Toast/Snackbar in this pass (see above).
- Added `contextChangeTransitionSpec<S>()` for `AnimatedContent`-based content swaps — Uber Base's "context change" pattern (full fade-out, then fade-in), the concrete, implementable piece of Uber Base's transitions taxonomy. Uber Base's other navigation patterns (drill forward/back, slide forward/back) are explicitly documented on Uber's own side as "not yet available in code" — they're full-screen navigation-stack transitions that belong in an app's navigation layer, not a component library, so PixaCompose doesn't attempt to implement them.
- **Refactored `RadioButton.kt`**: its four `animateColorAsState`/`animateFloatAsState` calls (outer/border/inner circle color, label color, inner-dot scale) used `standardTween(150)`/`standardTween(200)` — a generic fallback for exactly the color/selection-state motion that its sibling `Checkbox.kt` already expresses with `colorSpring`/`selectionSpring`. Same interaction type, inconsistent preset choice — now both use the same semantic presets.
- `Popover.kt`/`Tooltip.kt`'s missing-`animationSpec` fix from the prior pass still holds (both now pass `standardTween()`/`fastTween()` explicitly).
- `Menu.kt`'s untransitioned `Popup` jump-cut remains open debt — still not fixed here for the same reason as before (a structural change, not a spec swap).
- No preset was renamed. Reorganizing `AnimationUtils.kt` into named category sections (Selection/Feedback/Drag-gesture-follow/Emphasis/Loading/Reveal-Dismissal/Navigation-Surface-transition/Generic) makes intent explicit without touching any existing call site.

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

`ComponentElevation` is the single canonical elevation ladder for the whole library — the one source of truth every shadow-bearing component should express its depth through:

```kotlin
enum class ComponentElevation { None, Low, Medium, High, Highest }

// Dp values are not redefined here — they read from HierarchicalSize.Shadow,
// so there is exactly one numeric elevation scale, not two:
// None → Shadow.None (0dp), Low → Shadow.Nano (1dp), Medium → Shadow.Compact (2dp),
// High → Shadow.Medium (4dp), Highest → Shadow.Large (8dp)
ComponentElevation.Medium.toDp()   // 2dp

// Apply elevation shadow (both overloads live in the one function, so every
// component applies shadows the same way regardless of whether its depth is
// a fixed semantic tier or a size-driven Dp)
Modifier.elevationShadow(elevation = ComponentElevation.Medium, shape = shape, enabled = enabled)
Modifier.elevationShadow(elevation = sizeConfig.elevation /* Dp */, shape = shape, enabled = enabled)
```

#### Audit (2026-07): what was fragmented, and what changed

Elevation was previously spread across three uncoordinated systems:

1. **`ElevationUtils.kt`'s `ComponentElevation`** — used only by `Toast.kt`/`Snackbar.kt`, with its five `Dp` values (0/1/2/4/8) hardcoded directly in `toDp()`.
2. **`Card.kt`'s private `BaseCardElevation`** — a byte-for-byte duplicate of the same five-tier enum (`None`/`Low`/`Medium`/`High`/`Highest`, same 0/1/2/4/8 values), independently re-implemented, and leaked into four other files that needed a card-shaped elevation (`BottomNavBar.kt`, `Stepper.kt`, `Toast.kt`, `Alert.kt` all imported `components.display.BaseCardElevation` just to pass a value into `PixaCard`).
3. **`Button.kt`'s raw nullable `Dp?`** with an ad hoc `HierarchicalSize.Shadow.Nano` literal for its "auto-elevate Filled/Tonal variants" logic, applied via a hand-rolled `Modifier.shadow(...)` call instead of the shared `elevationShadow` helper.
4. **`Dialog.kt`/`Menu.kt`/`Tooltip.kt`/`Popover.kt`** each read a per-size `Dp` from `HierarchicalSize.Shadow` (legitimate — see below) but applied it via their own raw `Modifier.shadow(sizeConfig.elevation, shape)` call, duplicating the "zero it out when disabled, skip the shadow modifier entirely at 0dp" logic that `elevationShadow` already centralizes.

**Fixed:**
- `BaseCardElevation` is deleted. `Card.kt` and its four downstream consumers (`BottomNavBar.kt`, `Stepper.kt`, `Toast.kt`, `Alert.kt`) now all take/pass `ComponentElevation` directly — same values, one type.
- `ComponentElevation.toDp()` no longer hardcodes its own `Dp` scale; it reads from `HierarchicalSize.Shadow`, so `HierarchicalSize.Shadow` is now unambiguously *the* numeric elevation scale, and `ComponentElevation` is a semantic naming layer on top of it, not a second parallel scale.
- `Button.kt`'s auto-elevation now reads `ComponentElevation.Low.toDp()`/`.None.toDp()` instead of a raw `HierarchicalSize.Shadow.Nano` literal, and its shadow is applied via `Modifier.elevationShadow(...)` instead of a raw `Modifier.shadow(...)`.
- `Dialog.kt`/`Menu.kt`/`Tooltip.kt`/`Popover.kt` now apply their shadow via `Modifier.elevationShadow(sizeConfig.elevation, shape)` instead of a raw `Modifier.shadow(...)` — same `Dp` values (their elevation legitimately scales with `SizeVariant`, see below), now going through the one shared "how to render a shadow" function instead of reimplementing it four times.

**Left alone, deliberately:** the four overlays' `elevation: Dp` fields still hold size-scaled values from `HierarchicalSize.Shadow` (e.g. `Dialog`'s `Small`/`Medium`/`Large` tiers use `Shadow.Large`/`Huge`/`Massive` — 8/12/16dp) rather than a fixed `ComponentElevation` tier. This is intentional, not leftover fragmentation: a `SizeVariant.Large` dialog is a physically bigger surface and should cast a proportionally bigger shadow, which a fixed 5-tier semantic enum can't express (its top tier, `Highest`, caps at 8dp). `Modifier.elevationShadow`'s `Dp` overload exists specifically for this — components whose depth is *size-driven* pass a `Dp` sourced from `HierarchicalSize.Shadow`; components whose depth is *state-driven* (variant/interaction, not footprint) pass a `ComponentElevation` tier. Both go through the same function, so "how a shadow gets applied" has one implementation either way — only "how deep" has two legitimate inputs.

#### The four axes of depth/emphasis

Elevation (a shadow) is only one of four tools for expressing that a component is prominent or separated from its surroundings — reaching for a shadow when one of the others fits is exactly the "ad hoc" pattern this pass removed:

| Axis | What it communicates | Where it lives |
| --- | --- | --- |
| **Surface layering** | Which physical layer a component's background belongs to (screen background vs. an elevated panel) | `AppTheme.colors.baseSurfaceDefault` vs `.baseSurfaceElevated` (`theme/Color.kt`) |
| **Shadow rendering** | A surface is physically *lifted above* its neighbors (floating, draggable, overlaying) | `ComponentElevation` + `Modifier.elevationShadow` (this file) |
| **Tonal emphasis** | Prominence via color/fill instead of depth | A `Tonal`/filled variant (e.g. `ButtonVariant.Tonal`, `BaseCardVariant.Tonal`) — can sit at `ComponentElevation.None`/`Low` |
| **Border emphasis** | A visual boundary without physical lift | `Outlined`/`Ghost` variants + `HierarchicalSize.Border` — per Uber Base's explicit guidance, prefer a border over a shadow when a component only needs separation, not lift |

`Alert.kt` is the clean example already in the codebase: it renders via `PixaCard(elevation = ComponentElevation.None, ...)` and relies on background/border color for separation — exactly Uber Base's "don't shadow a banner, use color/border" rule, and needed no change in this pass.

#### Component-to-tier mapping

| Component | Tier | Why |
| --- | --- | --- |
| `Card` (default `Elevated` variant) | `Medium` | Standard raised surface — Uber Base's default container tier. |
| `Card` (`Outlined`/`Filled`/`Tonal`/`Ghost` variants) | `None` | Border or tonal emphasis instead of shadow, per Uber Base's separation guidance. |
| `Button` (`Filled`/`Tonal`) | `Low` | Subtle resting lift — Uber Base "shallow above" territory for small interactive elements. |
| `Button` (`Outlined`/`Ghost`/`Text`) | `None` | Border/tonal emphasis, no shadow. |
| `Snackbar`, `Toast` | `Highest` | Uber Base's "deep" tier — interrupts/floats over all other content, needs the strongest hierarchy. |
| `Dialog`, `Menu`, `Popover` | Size-scaled `Dp` from `HierarchicalSize.Shadow` (≈`High`–`Highest` range, growing with `SizeVariant`) | Uber Base's "shallow below" tier for dialog/menu/popover/calendar, adjusted per footprint. |
| `Tooltip` | Size-scaled `Dp` from `HierarchicalSize.Shadow` | Small floating surface; Uber Base groups tooltips with the "deep" tier alongside snackbars, so it stays at the higher end of the scale despite its small size. |
| `Alert` | `None` | Banner-style, separated by background/border color, never a shadow. |

Dark theme note: `Modifier.shadow`'s ambient/spot shadow rendering is theme-agnostic (it darkens against whatever is beneath it), so no separate dark-theme elevation values are needed — the same `ComponentElevation` tier reads correctly in both themes. What *should* differ between themes is surface layering (axis 1 above), which `theme/Color.kt`'s light/dark scheme builders already handle independently of elevation.

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
                    text = category,
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
        PixaActionCard(
            title = "Profile",
            subtitle = "Manage your account",
            leading = { PixaIcon(source = IconSource.Vector(Icons.Default.Person), contentDescription = null) },
            trailing = { PixaIcon(source = IconSource.Vector(Icons.Default.ChevronRight), contentDescription = null) },
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
                startActions = listOf(
                    TopNavAction(
                        icon = painterResource(Res.drawable.ic_menu),
                        description = "Open navigation",
                        onClick = { /* open drawer */ }
                    )
                )
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
   - Bottom options → `PixaSheet`
   - Full-screen flow → `PixaFullScreenModal`
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
