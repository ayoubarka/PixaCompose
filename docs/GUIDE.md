# Using PixaCompose in an app

PixaCompose is a Compose Multiplatform UI library (Android, iOS, desktop) built on
`compose.foundation` only. There is no Material in it. Every component is prefixed `Pixa`, and
its look comes from one `PixaStyle` object given to `PixaTheme`.

## Add it

Versions the library is built with: Kotlin 2.3.20, Compose Multiplatform 1.10.3, AGP 9.1.0,
Gradle 9.4.1, JDK 17, minSdk 24. An app may use newer Kotlin and Compose versions than these,
not older ones.

```kotlin
// shared/build.gradle.kts
commonMain.dependencies {
    implementation("com.pixamob:pixacompose:2.0.0-alpha01")           // core, always
    implementation("com.pixamob:pixacompose-image:2.0.0-alpha01")     // PixaImage (Coil 3.4.0)
    implementation("com.pixamob:pixacompose-charts:2.0.0-alpha01")    // charts (Vico 2.4.3)
    implementation("com.pixamob:pixacompose-datetime:2.0.0-alpha01")  // calendar, date picker
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
  `destructive`, `destructiveForeground`, `border`, `input`, `ring`.
- Typography: `h1`, `h2`, `h3`, `body`, `bodySmall`, `label`, `caption`.
- Shapes: `sm`, `md`, `lg`, `full`.
- Spacing: `xs` 4, `sm` 8, `md` 12, `lg` 16, `xl` 24, `xxl` 32.

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
| `PixaSurface(...)` | Fill, border, shadow, press feedback. Four forms: static, `onClick`, `checked`/`onCheckedChange`, `selected`/`onClick`. Build custom tappable things on this. |

### Actions and display (`components`)

| Component | Use |
|---|---|
| `PixaButton(onClick, variant, size) { }` | Variants: Primary, Secondary, Outline, Ghost, Destructive. Sizes: Sm, Md, Lg. Put an icon in the content for an icon button. |
| `PixaCard(color, contentColor, contentPadding) { }` | Container. Pass a color for colored tiles; a bento layout is cards in rows and columns. |
| `PixaListItem(onClick, leading, trailing, supporting) { headline }` | One row of a list or settings screen. |
| `PixaBadge(variant) { }` | Small status label. |
| `PixaChip(selected, onClick) { }` | Filter or date-strip pill. |
| `PixaAvatar(size) { }` | Round frame for initials, an icon or `PixaImage`. Sizes: Sm, Md, Lg. |
| `PixaAlert(variant, icon, title) { }` | Inline message. Variants: Default, Destructive. |
| `PixaSeparator(orientation)` | Divider line. |
| `PixaSkeleton(shape)` | Loading placeholder; size it with the modifier. |
| `PixaSpinner(size, strokeWidth, color)` | Loading of unknown length; works inside a button. |
| `PixaProgress(value)` / `PixaCircularProgress(value, size, strokeWidth) { }` | Known progress, 0..1. The ring has a center slot. |
| `PixaPageDots(count, current)` | Pager indicator. |
| `PixaStepper(steps, current)` | Numbered steps of a flow. |
| `PixaAccordionItem(expanded, onExpandedChange, title) { }` | Collapsible section. |

### Forms

| Component | Use |
|---|---|
| `PixaInput(value, onValueChange, placeholder, leading, trailing, isError, keyboardOptions, ...)` | Single-line field. |
| `PixaTextarea(value, onValueChange, placeholder, minLines, maxLines, isError)` | Multi-line field. |
| `PixaField(label, helper, error) { }` | Label, helper and error around a control; inputs inside turn red when `error` is set. |
| `PixaCodeInput(value, onValueChange, length)` | Verification code boxes, digits only. |
| `PixaCheckbox(checked, onCheckedChange)` / `PixaSwitch(checked, onCheckedChange)` / `PixaRadioButton(selected, onClick)` | Toggles. They have no label; put a `PixaText` next to them. |
| `PixaSlider(value, onValueChange, valueRange, valueLabel)` | `valueLabel` shows a bubble above the thumb while touched. |
| `PixaSelect(expanded, onExpandedChange, value = { }) { PixaMenuItem... }` | Field that opens a menu. |

### Navigation

| Component | Use |
|---|---|
| `PixaTopBar(navigation, actions) { title }` | Screen header. |
| `PixaBottomBar { PixaBottomBarItem(selected, onClick, label) { icon } }` | Bottom tabs. |
| `PixaTabs { PixaTab(selected, onClick) { } }` | Segmented switch inside a screen. |

There is no navigation library inside PixaCompose; the app chooses its own.

### Overlays

All take `visible` and `onDismissRequest` and stay composed so they can animate out.

| Component | Use |
|---|---|
| `PixaDialog(visible, onDismissRequest) { }` | Centered modal. |
| `PixaSheet(visible, onDismissRequest) { }` | Bottom sheet; drag down to dismiss. A scrolling list inside conflicts with the drag. |
| `PixaPopover(visible, onDismissRequest, side) { }` | Panel anchored to the layout it is declared in. |
| `PixaMenu(visible, onDismissRequest) { PixaMenuItem(onClick) { } }` | Anchored action list. |
| `PixaTooltip(tooltip = { }) { content }` | Hover on desktop, long press on touch. |
| `rememberPixaToastState()`, `state.show(title, description, variant)`, `PixaToastHost(state)` | Toasts. Put one host at the root of the app, above the content. |

### Optional modules

| Component | Module | Use |
|---|---|---|
| `PixaImage(model, contentDescription, shape, contentScale, placeholder, error)` | `pixacompose-image` | URL, file or bytes. It has no size of its own; give it one. |
| `PixaLineChart(values, labels, smooth)` / `PixaBarChart(values, labels, barWidth)` | `pixacompose-charts` | One series. Tap a point to show its value. |
| `PixaCalendar(selected, onSelect, today, firstDayOfWeek, isEnabled, labels)` | `pixacompose-datetime` | One month, single date. Uses `kotlinx.datetime.LocalDate`. |
| `PixaDatePicker(value, onValueChange, expanded, onExpandedChange, placeholder, format, ...)` | `pixacompose-datetime` | Field that opens the calendar. |

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
- Not in the library: range dates, time picker, multi-series or pie charts, pull to refresh,
  pager, drawer, search bar, snackbar actions, right-to-left checks.
