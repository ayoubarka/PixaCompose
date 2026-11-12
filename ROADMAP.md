# Roadmap

Decisions taken on 2026-11-12:

- **Distribution:** Maven library (`com.pixamob:pixacompose`). A copy-in registry may come later.
- **Headless base:** our own primitives on `compose.foundation`. No Material, no third-party UI libraries in the core module.
- **Targets:** Android and iOS for apps, plus desktop for the showcase and snapshot tests.
- **Repo:** same GitHub repo; 1.x moves to a `legacy` branch, `main` restarts as 2.0.

## Architecture

1. **Tokens** (`theme/`): 17 semantic colors, 4 shapes, 7 text styles, 6 spacing steps, 3 motion roles.
2. **Decoration** (`theme/PixaStyle.kt`): border widths, control and container shadows (soft, hard, glow), press effect, focus ring. Drawn in one place, `primitives/Surface.kt`.
3. **Components** (`components/`): small, slot-based, at most a `variant` and a `size`.
4. **Styles** (`styles/`): the only place raw color values are allowed.

The test for every change: a new style must never require editing a component.

## Phases

- [x] **0. Scaffold** - modules, version catalog, showcase on Android and desktop, snapshot test.
- [ ] **0b. Loose ends** - `iosApp` Xcode project (needs Xcode installed), CI workflow, `legacy` branch on GitHub.
- [x] **1. Foundation** - `Surface`, `Text`, `Icon` plus the built-in `PixaIcons`, `Button`, `Card`, three styles, keyboard focus ring, motion and spacing tokens, separate control and container shadows, `withFontFamily`.
- [ ] **1b. Open questions from phase 1** - icon stroke weight as a style knob (Cartoon wants thicker icons), bundling a default font per style, how the focus ring should sit over a hard shadow.
- [x] **2. Core controls** - Input, Textarea, Checkbox, Switch, RadioButton, Badge, Separator, Avatar, Skeleton, Progress. `Surface` gained toggleable and selectable variants.
- [ ] **2b. Open questions from phase 2** - minimum touch target for the 20dp checkbox and radio, a labelled row (`Field`/`Label`) so the text is tappable too, error state for inputs, whether small toggles should take the control shadow.
- [x] **3. Overlays** - Dialog, Sheet, Popover, Menu with MenuItem, Tooltip, Toast (state plus host). Built on two internal primitives: `Modal` (platform dialog window) and `AnchoredPopup` (platform popup with flip and edge clamping).
- [ ] **3b. Open questions from phase 3** - check every overlay on a real Android device and on iOS (scrim, insets, keyboard, shadow margin of popups), drag-to-dismiss for Sheet, style-controlled scrim color, swipe-to-dismiss and an action slot for Toast, keyboard arrow navigation in Menu.
- [x] **4. Navigation and composites** - Tabs with Tab, TopBar, BottomBar with BottomBarItem, AccordionItem, Slider, Select (a field that opens a Menu), Stepper.
- [ ] **4b. Open questions from phase 4** - sliding selection indicator for Tabs, keyboard and right-to-left support for Slider, step labels for Stepper, Select menu as wide as its field, Neon glow shows a faint cut-off edge at large radii.
- [ ] **5. Optional modules** - `pixacompose-datetime`, `pixacompose-charts`, `pixacompose-image`, `pixacompose-shapes` (the 1.x custom shape geometry).
- [ ] **6. Polish** - Bento style, pixel-diff snapshot tests, docs site, 2.0.0 release.

## Carried over from 1.x

Port when the phase needs them: `ColorUtils`, the animation presets idea, `WindowSizeClass`,
`CustomShapes` geometry, the showcase component registry.

Deliberately not ported: `HierarchicalSize` and the 8-tier `SizeVariant`, the 79-token palette,
per-component color and size data classes, `custom*` parameters, card presets.
