# Roadmap

Decisions taken on 2026-11-12:

- **Distribution:** Maven library (`com.pixamob:pixacompose`). A copy-in registry may come later.
- **Headless base:** our own primitives on `compose.foundation`. No Material, no third-party UI libraries in the core module.
- **Targets:** Android and iOS for apps, plus desktop for the showcase and snapshot tests.
- **Repo:** same GitHub repo; 1.x moves to a `legacy` branch, `main` restarts as 2.0.
- **Two built-in styles:** Minimal (tools, health, finance) and Cartoon (games, playful apps). Any other look is an app's own `PixaStyle`, usually a `copy()` of Minimal with its colors, radii and fonts. Neon was removed after it proved a third look needs no component edits.
- **Bento is a layout, not a style:** cards of mixed sizes in rows and columns. There is no Bento style and no grid component.
- **Nothing is added until a real app needs it.** This applies to components, style knobs and optional modules alike.

## Architecture

1. **Tokens** (`theme/`): 17 semantic colors, 4 shapes, 7 text styles, 6 spacing steps, 3 motion roles.
2. **Decoration** (`theme/PixaStyle.kt`): border widths, control and container shadows (soft, hard), press effect, focus ring. Drawn in one place, `primitives/Surface.kt`.
3. **Components** (`components/`): small, slot-based, at most a `variant` and a `size`.
4. **Styles** (`styles/`): the only place raw color values are allowed.

The test for every change: a new style must never require editing a component.

## Phases

- [x] **0. Scaffold** - modules, version catalog, showcase on Android and desktop, snapshot test.
- [ ] **0b. Loose ends** - `iosApp` Xcode project (needs Xcode installed), CI workflow, `legacy` branch on GitHub.
- [x] **1. Foundation** - `PixaSurface`, `PixaText`, `PixaIcon` plus the built-in `PixaIcons`, `PixaButton`, `PixaCard`, the built-in styles, keyboard focus ring, motion and spacing tokens, separate control and container shadows, `withFontFamily`.
- [ ] **1b. Open questions from phase 1** - icon stroke weight as a style knob (Cartoon wants thicker icons), bundling a default font per style, how the focus ring should sit over a hard shadow.
- [x] **2. Core controls** - Input, Textarea, Checkbox, Switch, RadioButton, Badge, Separator, Avatar, Skeleton, Progress. `PixaSurface` gained toggleable and selectable variants.
- [ ] **2b. Open questions from phase 2** - minimum touch target for the 20dp checkbox and radio, a labelled row (`Field`/`Label`) so the text is tappable too, error state for inputs, whether small toggles should take the control shadow.
- [x] **3. Overlays** - Dialog, Sheet, Popover, Menu with MenuItem, Tooltip, Toast (state plus host). Built on two internal primitives: `Modal` (platform dialog window) and `AnchoredPopup` (platform popup with flip and edge clamping).
- [ ] **3b. Open questions from phase 3** - check every overlay on a real Android device and on iOS (scrim, insets, keyboard, shadow margin of popups), drag-to-dismiss for Sheet, style-controlled scrim color, swipe-to-dismiss and an action slot for Toast, keyboard arrow navigation in Menu.
- [x] **4. Navigation and composites** - Tabs with Tab, TopBar, BottomBar with BottomBarItem, AccordionItem, Slider, Select (a field that opens a Menu), Stepper.
- [ ] **4b. Open questions from phase 4** - sliding selection indicator for Tabs, keyboard and right-to-left support for Slider, step labels for Stepper, Select menu as wide as its field.
- [x] **4c. App basics** - `PixaCard` takes a `color` and `contentColor` for colored tiles, `PixaChip`, `PixaCircularProgress`. Neon and the glow shadow removed.
- [ ] **4d. Open questions from 4c** - `PixaCircularProgress` has no outline in Cartoon (it is drawn as arcs, not a `PixaSurface`).
- [ ] **5. Optional modules** - each one its own module, built one at a time and only when an app needs it. Third-party libraries are allowed here if they have no Material dependency and no look of their own.
  - [x] `pixacompose-image`: `PixaImage` on Coil 3.4.0, with placeholder and error slots. Coil is pinned there because newer releases need Compose 1.11+ and Kotlin 2.4. Not checked: Android and iOS network loading on a device, SVG and GIF (need extra Coil artifacts).
  - [x] `pixacompose-charts`: `PixaLineChart` and `PixaBarChart`, one series each, on Vico 2.4.3 (`com.patrykandpatrick.vico:multiplatform`, no Material). The wrapper is one file that only passes theme colors, fonts and shapes to Vico; the 1.x wrapper was 983 lines, do not repeat that. Since 2026-11-20, on Vico 3.3.1 (`compose`): several series, stacked and grouped bars, bars with lines on two axes, candlesticks, pie and donut, guide lines and bands, value labels, scroll and zoom, and `PixaChartLegend`, in one small file per chart. In Cartoon the guidelines take the ink border color and look heavy.
  - [x] `pixacompose-datetime`: `PixaCalendar` (one month, single date, disabled days, custom labels and first weekday) and `PixaDatePicker` (an outline `PixaButton` that opens the calendar in a `PixaPopover`, the shadcn anatomy). Uses `kotlinx-datetime` 0.7.1 for the maths only. Not done: range and multiple selection, month and year menus, time, typed input, presets, keyboard arrows, right-to-left, a sheet instead of a popover on phones.
  - [x] 2026-11-23: datetime merged into `:pixacompose` (package `components`), and `pixacompose-image` renamed `pixacompose-media` (package `media`) to hold other media types, such as GIF or video, when an app needs them.
  - `pixacompose-shapes`: the 1.x custom shape geometry.
- [x] **5b. Naming** - every public component, type and composition local is prefixed `Pixa`, files included.
- [x] **5c. Android device pass** (2026-11-12, Android 16 phone, 1080x2392) - every section renders in both styles and themes; dialog, sheet, popover, menu, toast, select and date picker open and close with Back; typing, slider drag and accordion work. Fixed in the app wrapper: `adjustResize` for the keyboard, status bar icons following the showcase theme. Found, not fixed: a focused input scrolls to the keyboard edge with its focus ring and shadow cut off. Not exercised: tooltip (needs hover), sheet drag, long text, RTL, TalkBack.
- [x] **5d. Touch feedback** - charts show a value marker on tap (tap again to hide); `PixaSlider` takes a `valueLabel` slot shown in a bubble above the thumb while touched. Snapshots in `showcase/build/snapshots/interaction/`. Checked on the Android phone on 2026-11-13; open point: whether the bubble sits high enough above a real finger.
- [x] **5e. App basics, second round** - `PixaListItem`, `PixaSpinner`, `PixaAlert`, `PixaField` (label, helper, error; inputs inside take the error look through `isError`), `PixaPageDots`, `PixaCodeInput`, and drag-to-dismiss on `PixaSheet`. Checked on the Android phone on 2026-11-13: sheet drag settles back and dismisses, code input types and deletes at the end (a caret bug found there was fixed). Sheet drag has no nested-scroll handling, so a scrolling list inside a sheet will fight it. Touch targets: Compose already extends the hit area of small clickable controls to 48dp, so no code was added; confirmed on the phone by tapping about 6dp outside a radio button.
- [ ] **6. Polish** - pixel-diff snapshot tests, docs site, 2.0.0 release.

Waiting for an app to ask: gradient and translucent fills on `PixaSurface`, a floating bottom bar,
a tile color palette in `PixaColors`.

## Carried over from 1.x

Port when the phase needs them: `ColorUtils`, the animation presets idea, `WindowSizeClass`,
`CustomShapes` geometry, the showcase component registry.

Deliberately not ported: `HierarchicalSize` and the 8-tier `SizeVariant`, the 79-token palette,
per-component color and size data classes, `custom*` parameters, card presets.
