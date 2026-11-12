# PixaCompose 2

Compose Multiplatform UI library (Android, iOS, desktop). The look of every component comes from
a `PixaStyle` passed to `PixaTheme`; component code is style-neutral. Plan and decisions are in
`ROADMAP.md`. The 1.x code is on the `legacy` branch and is reference only.

## Layout

```
pixacompose/src/commonMain/kotlin/com/pixamob/pixacompose/
├── theme/        PixaColors, PixaStyle (shapes, typography, decoration, spacing, motion), PixaTheme, Fonts
├── primitives/   Surface (fill, border, shadow, press, focus ring), Text, Icon, PixaIcons,
│                 Modal and AnchoredPopup (internal bases for overlays)
├── components/   one small file per component
└── styles/       built-in styles; the only place raw colors are allowed
showcase/         shared showcase UI, desktop main, snapshot test
androidApp/       thin Android wrapper
```

## Hard rules

- **A new style must never require editing a component.** If it does, the missing knob belongs in `PixaStyle` or `Surface`, not in the component.
- **Foundation only** in `:pixacompose`: no Material, no third-party UI libraries. Heavy features go in separate optional modules.
- **No raw colors outside `styles/`.** Components read `PixaTheme.colors.*`; `Color.Transparent` is the only literal allowed.
- **Anything with a background is built on `Surface`.** It has four forms: static, clickable (`onClick`), two-state (`checked`/`onCheckedChange`) and one-of-many (`selected`/`onClick`). Borders come from `outlinedBorder()` and `filledBorder()` in `components/Borders.kt`. Do not draw borders, shadows or press feedback anywhere else.
- **Slots over parameters.** A component takes `variant`, `size`, `enabled`, `modifier` and content lambdas. No `leadingIcon`, `customColors`, `customTextStyle` style parameters, and no preset variants of a component inside the library.
- **Gaps and paddings** come from `PixaTheme.spacing`; **animations** from `PixaTheme.motion.<role>.spec()`. No inline `spring()`/`tween()` in components.
- **Shadows**: controls use `decoration.controlShadow`, containers use `decoration.containerShadow`.
- **Icons**: `PixaIcons` holds only what the library's own components need. It is not an icon pack; do not grow it for app use.
- **Overlays** take `visible` and `onDismissRequest` and stay in the composition so they can animate out. Modal ones build on `Modal`, anchored ones on `AnchoredPopup`; do not call the platform `Dialog`/`Popup` from a component.
- **Sizes live with the component** as a small enum (see `ButtonSize`). There is no global size system.
- **Keep files small.** A component file over about 200 lines is a sign it is doing a style's job or needs splitting.
- `explicitApi()` is on: every public declaration is deliberate.

## Verify

```bash
./gradlew :showcase:jvmTest            # renders to showcase/build/snapshots/pages/ and /overlays/
./gradlew :androidApp:assembleDebug
```

Look at the snapshots after any visual change, in at least Minimal and Cartoon. Every new
component gets a section in the showcase, and every overlay an entry in `OverlayDemo` so it is
rendered open. Delays (`delay()`) run on real time in the headless scene, not the frame clock.

iOS targets are declared but cannot be compiled on a machine without full Xcode.

## Git

Commit only when asked. Never commit a state that does not compile.
