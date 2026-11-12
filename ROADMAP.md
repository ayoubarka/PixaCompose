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
  - [x] `pixacompose-charts`: `PixaLineChart` and `PixaBarChart`, one series each, on Vico 2.4.3 (`com.patrykandpatrick.vico:multiplatform`, no Material). The wrapper is one file that only passes theme colors, fonts and shapes to Vico; the 1.x wrapper was 983 lines, do not repeat that. Not done: several series, markers on touch, scroll and zoom, pie or donut. In Cartoon the guidelines take the ink border color and look heavy.
  - `pixacompose-datetime`: `kotlinx-datetime` for the date maths; calendar and pickers on our own components.
  - `pixacompose-shapes`: the 1.x custom shape geometry.
- [x] **5b. Naming** - every public component, type and composition local is prefixed `Pixa`, files included.
- [ ] **6. Polish** - pixel-diff snapshot tests, docs site, 2.0.0 release.

Waiting for an app to ask: gradient and translucent fills on `PixaSurface`, a floating bottom bar,
a tile color palette in `PixaColors`.

## Carried over from 1.x

Port when the phase needs them: `ColorUtils`, the animation presets idea, `WindowSizeClass`,
`CustomShapes` geometry, the showcase component registry.

Deliberately not ported: `HierarchicalSize` and the 8-tier `SizeVariant`, the 79-token palette,
per-component color and size data classes, `custom*` parameters, card presets.
