# Using PixaCompose in an app

PixaCompose is a Compose Multiplatform UI library (Android, iOS, desktop) built on
`compose.foundation` only. There is no Material in it. Every component is prefixed `Pixa`, and
its look comes from one `PixaStyle` object given to `PixaTheme`.

## Add it

Versions the library is built with: Kotlin 2.4.21, Compose Multiplatform 1.12.2, AGP 9.4.1,
Gradle 9.6.0, JDK 17, minSdk 24. An app may use newer Kotlin and Compose versions than these,
not older ones.

```kotlin
// shared/build.gradle.kts
commonMain.dependencies {
    implementation("com.pixamob:pixacompose:2.0.0-alpha03")           // core, always
    implementation("com.pixamob:pixacompose-media:2.0.0-alpha03")     // PixaImage (Coil)
    implementation("com.pixamob:pixacompose-charts:2.0.0-alpha03")    // charts (Vico 3.3.1)
}
```

To work against the library source instead of a released version, add this to the app's
`settings.gradle.kts`; Gradle then swaps the coordinates above for the local modules. This
only works when the app uses the same Kotlin, Compose and Android Gradle plugin versions as the
library, so prefer the released version:

```kotlin
includeBuild("../PixaCompose")
```

Do not add Material (`compose.material`, `material3`) to the app's UI. An Android app that
loads images from the network needs the `INTERNET` permission in its manifest.

## Theme

```kotlin
val base = PixaStyles.Minimal            // or PixaStyles.Cartoon
val appStyle = base.copy(
    lightColors = base.lightColors.copy(primary = Color(0xFF1F9D7A), primaryForeground = Color.White),
    darkColors = base.darkColors.copy(primary = Color(0xFF4FD1AE), primaryForeground = Color(0xFF05241C)),
    shapes = base.shapes.copy(md = RoundedCornerShape(16.dp), lg = RoundedCornerShape(24.dp)),
).withFontFamily(body = bodyFont, heading = headingFont)

PixaTheme(style = appStyle) { App() }   // darkTheme defaults to the system setting
```

Read tokens through `PixaTheme`: `colors`, `typography`, `shapes`, `spacing`, `decoration`,
`motion`.

- Colors: `background`, `foreground`, `card`, `cardForeground`, `primary`, `primaryForeground`,
  `secondary`, `secondaryForeground`, `muted`, `mutedForeground`, `accent`, `accentForeground`,
  `destructive`, `destructiveForeground`, `success`, `successForeground`, `border`, `input`, `ring`,
  and `categories`: distinct hues for telling things apart (a pet's ring, a log type). Read one
  with `colors.category(index)`, which wraps around and falls back to `primary` when the list
  is empty.
  An app that builds its own `PixaColors` (not with `copy`) must set `success` and
  `successForeground` too.
- Typography: `h1`, `h2`, `h3`, `body`, `bodySmall`, `label`, `caption`.
- Shapes: `sm`, `md`, `lg`, `full`.
- Spacing: `xs` 4, `sm` 8, `md` 12, `lg` 16, `xl` 24, `xxl` 32.
- Decoration: `borderWidth`, `filledBorderWidth`, `containerBorderWidth`, `controlShadow`,
  `containerShadow`, `press`, `focusRing`. `containerBorderWidth` is the outline of resting
  containers (`PixaCard`, `PixaActionTile`) and defaults to `borderWidth`, so a style that does
  not set it looks as before. Inputs, outlined buttons and overlays keep `borderWidth`.

Flat cards, set apart by their fill alone:

```kotlin
val flat = base.copy(
    decoration = base.decoration.copy(containerShadow = PixaShadow.None, containerBorderWidth = 0.dp),
)
```

Touch targets: tappable controls smaller than 48dp reserve 48dp around themselves. Provide
`LocalPixaMinimumTouchTarget` with another value (or `0.dp`) to change that for a subtree.

Fonts loaded from Compose resources are composable, so build the `FontFamily` inside the
composition and `remember` the style.

## Components

Signatures are shortened: every component also takes `modifier`, and interactive ones `enabled`.
Trailing lambdas are content slots.

### Basics (`primitives`)

| Component | Use |
|---|---|
| `PixaText(text, style, color, textAlign, maxLines, overflow)` | All text. Takes the surrounding content color by default. |
| `PixaIcon(imageVector or painter, contentDescription, tint, size)` | Icons. `PixaIcons` has only Check, Close, Plus, Minus and three chevrons; bring the app's own icon set. |
| `PixaSurface(...)` | Fill, border, shadow, press feedback. Four forms: static, `onClick`, `checked`/`onCheckedChange`, `selected`/`onClick`. Build custom tappable things on this. The `onClick` form takes `loadingColor` to run a busy light around its edge. |

### Actions and display (`components`)

| Component | Use |
|---|---|
| `PixaButton(onClick, variant, size, loading, color, contentColor) { }` | Variants: Primary, Secondary, Outline, Ghost, Destructive. Sizes: Sm, Md, Lg. Put an icon in the content for an icon button. `loading = true` runs a light around the edge and ignores taps; the label stays. `color`/`contentColor` override the fill for brand buttons. |
| `PixaCard(color, contentColor, contentPadding) { }` | Container. Pass a color for colored tiles; a bento layout is cards in rows and columns. Its outline follows `decoration.containerBorderWidth` (see Theme for flat cards). |
| `PixaListItem(onClick, leading, trailing, supporting) { headline }` | One row of a list or settings screen. |
| `PixaBadge(variant) { }` | Small status label. |
| `PixaChip(selected, onClick) { }` | Filter or date-strip pill. |
| `PixaAvatar(size, color, contentColor, ringColor, ringWidth, selected, onClick, contentDescription) { }` | Round frame for initials, an icon or `PixaImage`. Sizes: Sm, Md, Lg, Xl; `Modifier.size` overrides the edge. `ringColor` draws a ring with a gap; `selected = false` thins it to a hairline. `onClick` makes it tappable. |
| `PixaAvatarRow(items, selected, onSelect, onAdd, addLabel, avatarSize, label = { item -> }) { item, current -> avatar }` | Scrolling row of avatars with a label under each, the selected one ringed, and an optional "add" entry at the end. |
| `PixaIconTile(color, size) { icon }` | Icon on a soft square of its own color, for categories and list rows. Sizes: Sm, Md, Lg, Xl. |
| `PixaActionTile(onClick, color, selected, label = { }) { icon }` | Tappable icon tile with a label under it: quick actions, or a type picker with `selected`. |
| `PixaFab(onClick, size) { icon }` | Round floating button for the screen's main action; place it with `Modifier.align`. Sizes: Md, Lg. |
| `PixaEmptyState(picture, message, action) { title }` | Picture, title, message and one action, centered. No background; wrap it in a `PixaCard` to frame it. |
| `PixaAlert(variant, icon, title) { }` | Inline message. Variants: Default, Destructive. |
| `PixaSeparator(orientation)` | Divider line. |
| `PixaSkeleton(shape)` | Loading placeholder; size it with the modifier. |
| `PixaSpinner(size, strokeWidth, color)` | Loading of unknown length; works inside a button. |
| `PixaProgress(value)` / `PixaCircularProgress(value, size, strokeWidth, color) { }` | Known progress, 0..1. The ring has a center slot; `color` tints it (primary by default), e.g. `colors.category(i)`. |
| `PixaPageDots(count, current, contentDescription)` | Pager indicator. |
| `PixaStepper(steps, current)` | Numbered steps of a flow. |
| `PixaTimeline(steps, current, indicator) { index -> }` | Vertical stepper: a dot and line per step with content beside it. Steps before `current` are done, `current` is outlined, later ones muted; pass `current = steps` when all are done. `indicator` replaces the check/number in a dot. |
| `PixaAccordionItem(expanded, onExpandedChange, title) { }` | Collapsible section. |

### Forms

| Component | Use |
|---|---|
| `PixaInput(value, onValueChange, placeholder, leading, trailing, isError, keyboardOptions, ...)` | Single-line field. |
| `PixaTextarea(value, onValueChange, placeholder, minLines, maxLines, isError)` | Multi-line field. |
| `PixaField(label, helper, error) { }` | Label, helper and error around a control; inputs inside turn red when `error` is set and are named after `label` for screen readers. An app's own control can read `LocalPixaFieldLabel`. |
| `PixaCodeInput(value, onValueChange, length)` | Verification code boxes, digits only. |
| `PixaCheckbox(checked, onCheckedChange, contentDescription)` / `PixaSwitch(checked, onCheckedChange)` / `PixaRadioButton(selected, onClick)` | Toggles. They have no label; put a `PixaText` next to them. |
| `PixaSlider(value, onValueChange, valueRange, valueLabel)` | `valueLabel` shows a bubble above the thumb while touched. |
| `PixaRatingPicker(value, onValueChange, max, color) { value -> }` | Row of square buttons for 1..`max`. Numbers by default; pass `{ PixaRatingFace(it) }` for faces. |
| `PixaSelect(expanded, onExpandedChange, value = { }) { PixaMenuItem... }` | Field that opens a menu. |

### Navigation

| Component | Use |
|---|---|
| `PixaTopBar(navigation, actions, size, showDivider) { title }` | Screen header. Sizes: Md (56dp), Lg (72dp). `showDivider = false` drops the line under it. |
| `PixaAvatarSwitcher(items, selected, onSelect, expanded, onExpandedChange, showLabel, onAdd, addLabel, label = { }) { item, current -> avatar }` | Pill with the current avatar, its label and a chevron; opens a menu with a check on the current item and an optional "add" row. Wraps its content unless the modifier gives a width (`fillMaxWidth()`, `width(240.dp)`); `showLabel = false` keeps only avatar and chevron. |
| `PixaBottomBar { PixaBottomBarItem(selected, onClick, label) { icon } }` | Bottom tabs. |
| `PixaTabs { PixaTab(selected, onClick) { } }` | Segmented switch inside a screen. |

There is no navigation library inside PixaCompose; the app chooses its own.

### Overlays

All take `visible` and `onDismissRequest` and stay composed so they can animate out.

| Component | Use |
|---|---|
| `PixaDialog(visible, onDismissRequest) { }` | Centered modal. |
| `PixaSheet(visible, onDismissRequest) { }` | Bottom sheet; drag down to dismiss. Rides up with the keyboard and keeps its content above the gesture bar, so forms work inside it; do not add `imePadding()`. A scrolling list inside conflicts with the drag. |
| `PixaPopover(visible, onDismissRequest, side) { }` | Panel anchored to the layout it is declared in. |
| `PixaMenu(visible, onDismissRequest) { PixaMenuItem(onClick) { } }` | Anchored action list. |
| `PixaTooltip(tooltip = { }) { content }` | Hover on desktop, long press on touch. |
| `rememberPixaToastState()`, `state.show(title, description, variant)`, `PixaToastHost(state, position, offset)` | Toasts. Variants: Default, Success, Destructive. Put one host at the root of the app, above the content, aligned to match `position` (Top or Bottom); `offset` keeps them clear of a top bar. |

### Optional modules

| Component | Module | Use |
|---|---|---|
| `PixaImage(model, contentDescription, shape, contentScale, placeholder, error)` | `pixacompose-media` | URL, file or bytes. It has no size of its own; give it one. |
| `PixaLineChart(values, labels, smooth, fittedYAxis, drawIn)` / `PixaBarChart(values, labels, barWidth)` | `pixacompose-charts` | One series. Touch or slide a finger across to show the value nearest it; the value stays after release, and a tap on the same point hides it. A sideways slide, or a slide after a long press, keeps a scrolling screen still; a drag that starts vertically scrolls it. `fittedYAxis` fits the axis to the values instead of starting at 0; `drawIn` draws the line in from the left. |
| `PixaLineChart(series, …)` / `PixaBarChart(series, stacked, …)` | `pixacompose-charts` | Several `PixaChartSeries` (name, color, `dashed`), colored by the style's category hues unless set. Bars sit side by side or `stacked`. Both, and the one-series forms, take `showValues`, `guides` (`PixaChartGuide.Line` for a goal, `.Band` for a range) and `visiblePoints` (scroll and pinch through longer data); the line also takes `area` and `showPoints`. |
| `PixaScatterChart(points, labels, fittedYAxis, dotSize, guides, visiblePoints)` | `pixacompose-charts` | `PixaChartPoint(x, y)` dots; several may share an x (readings logged a few times a day). |
| `PixaComboChart(bars, lines, separateAxes)` | `pixacompose-charts` | Bars and lines over the same x positions; `separateAxes` measures the lines on their own, fitted axis at the end. |
| `PixaCandlestickChart(candles)` | `pixacompose-charts` | `PixaCandle(open, close, low, high)` per period; rises in the success color, falls in the destructive one. |
| `PixaPieChart(values, colors, donut, showValues) { center }` | `pixacompose-charts` | Slices in category hues; a donut draws `center` in its hole. Vico marks pies experimental. |
| (all charts) | `pixacompose-charts` | `formatValue` turns a number into the text shown on the axis, value labels and marker (plain number with up to two decimals by default). Line and bar charts take `interactive = false` to drop the touch marker, for a small chart inside a tappable card. Charts are always drawn left to right, also in RTL layouts. |
| `PixaChartLegend(series)` / `PixaChartLegend(names, colors)` | `pixacompose-charts` | A dot and a name per series or slice, wrapping; place it next to the chart. |
| `PixaCalendar(selected, onSelect, today, firstDayOfWeek, isEnabled, labels)` | `pixacompose` | One month, single date. Uses `kotlinx.datetime.LocalDate`. |
| `PixaDatePicker(value, onValueChange, expanded, onExpandedChange, placeholder, format, ...)` | `pixacompose` | Field that opens the calendar. |
| `PixaTimeInput(value, onValueChange, use24Hour, isError, labels)` | `pixacompose` | Hour and minute segments, plus AM/PM when `use24Hour = false`. Uses `kotlinx.datetime.LocalTime`; gives null while a segment is empty. Typing moves to the next segment; arrow keys step a segment. |

## Rules for app code

- Use `Pixa*` components and `PixaTheme` tokens for everything visible. No Material components,
  no hardcoded colors or text sizes in screens; brand colors live in the app's one `PixaStyle`.
- Layout is plain Compose: `Row`, `Column`, `Box`, `LazyColumn`, `Scaffold`-like structure by hand.
- Compose a screen from existing components before writing a new one. If something app-specific
  is needed, build it in the app on top of `PixaSurface`, `PixaText` and the tokens.

## When a component is missing

Do not work around it silently and do not pull in Material for it. Say so:

1. Name what is missing and the screen that needs it.
2. Build a small app-local version on `PixaSurface` and the tokens so work continues.
3. Add a line to `MISSING_COMPONENTS.md` at the root of the app: the component, the screen, and
   the app-local file standing in for it.

That list is what decides which components are added to the library next.

## Known limits

- Verified on desktop and one Android phone. iOS is compiled in CI but has not been run on a device.
- Not in the library: range dates, a time picker with a dial or wheel, pull to refresh, pager,
  drawer, search bar, snackbar actions. Right-to-left is handled for chevrons and the time input,
  and charts stay left to right; other components have not been checked in RTL.
