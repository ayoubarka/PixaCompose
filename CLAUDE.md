# PixaCompose

Compose Multiplatform (Android + iOS) UI component library. Core constraint: **no Material 3 UI components** — everything is built from `Box`/`Row`/`Column`/`Canvas` primitives, styled through a custom theme system (`AppTheme`), never `MaterialTheme`/`Button`/`TextField`/etc. This exists so the library ships a fully custom design system that doesn't inherit Material's look, versioning churn, or defaults. `material3` is still a transitive Compose dependency but is not used for UI surface — verify with `grep` before assuming otherwise if this ever seems violated.

Modules: `:library` (the KMP library, all real code) and `:androidApp` (demo/showcase app only — not published).

## Module structure

```
library/src/commonMain/kotlin/com/pixamob/pixacompose/
├── theme/
│   ├── Color.kt         # ColorPalette (79 tokens), light/dark scheme builders, ColorOverrides/ColorScales
│   ├── CustomShapes.kt  # raw Shape geometry (Concave/Convex/Wave/Arch/Tab/Bubble)
│   ├── Dimen.kt         # SizeVariant enum + HierarchicalSize (24 size categories)
│   ├── Package.kt       # doc-only package marker, zero code, inert
│   ├── PixaTheme.kt     # PixaTheme() composable + AppTheme accessor object
│   ├── ShapeStyle.kt    # ShapeStyles token layer (14 shape families × 5 sizes)
│   └── Typography.kt    # TextTypography (~32 named text styles)
├── utils/
│   ├── AnimationUtils.kt  # single source for all animation specs (see below)
│   ├── ColorUtils.kt      # brand-agnostic color math (HSV/HSL/hex/blend/contrast)
│   ├── DateTimeUtils.kt   # kotlinx-datetime helpers; only DatePicker.kt consumes it
│   ├── ElevationUtils.kt  # ComponentElevation enum + elevationShadow() modifier
│   └── ScreenUtil.kt      # screen dims + WindowSizeClass breakpoint system
└── components/{actions,display,feedback,inputs,navigation,overlay}/  # 40 files, one component family per file
```

`AnimationSpecs.kt` no longer exists — it was merged into `AnimationUtils.kt`. Adaptive sizing lives in `ScreenUtil.kt` (`WindowSizeClass`), not a separate `AdaptiveSize.kt`. There is no `AppTheme.spacing` — spacing comes from `HierarchicalSize.Spacing.forVariant(size)`.

## Hard rules

- **Colors**: always `AppTheme.colors.<token>` (e.g. `AppTheme.colors.brandContentDefault`). Never hardcode `Color(0x...)` except `Color.Transparent` as a sentinel. Token shape is `{group}{Role}{Emphasis}`:
  - Groups: `brand`, `accent`, `info`, `success`, `warning`, `error` (9 tokens each) and `base` (17 tokens, richer ladder for neutral UI).
  - Roles: `Surface` (fill), `Border` (outline), `Content` (foreground). `base` additionally has `Title/Subtitle/Body/Caption/Hint/Negative/Disabled` content roles.
  - Emphasis: `Subtle` / `Default` / `Focus`.
- **Typography**: always `AppTheme.typography.<token>`, never raw `TextStyle(fontSize = ...)`. Tiers, largest to smallest: `display{Large,Medium,Small}` → `header{Bold,Regular}` → `headline{Bold,Regular}` → `title{Bold,Regular,Light}` → `subtitle{Bold,Regular,Light}` → `body{Bold,Regular,Light}` → `caption{Bold,Regular,Light}` → `overline`/`footnote{Bold,Regular}` → `label{Large,Medium,Small}` → `action{Mini,ExtraSmall,Small,Medium,Large,ExtraLarge,Huge}`. `bodyRegular` (16sp) is the scale anchor.
- **Sizing**: prefer `HierarchicalSize.<Category>.forVariant(size)` over hand-rolled `when(size)` blocks or raw `.dp` literals. In practice almost no component does this yet (see Debt below) — don't copy that pattern into new code.
- **Shapes**: use `AppTheme.shapes.<family>` (`theme/ShapeStyle.kt`), not raw `RoundedCornerShape(Ndp)`. There are 14 shape families × 5 size tiers (`extraSmall/small/medium/large/extraLarge`) — standard rounded/cut corners plus decorative ones (`concaveTop/Bottom`, `convexTop/Bottom`, `wave`, `archTop/Bottom`, `tab`, `notchRounded/Sharp`, `bubbleLeft/Right`). Raw shape geometry (the actual `Shape` implementations) lives in `theme/CustomShapes.kt`; `ShapeStyle.kt` is the token layer built on top of it — don't confuse the two files, only `ShapeStyle.kt` is what components should reference.
- **Animation**: use `AnimationUtils` presets/factories, never raw `spring()`/`tween()` inline. Existing components are already 100% compliant here (verified: zero raw `spring()`/`tween()` calls remain in `components/`) — keep it that way.
- **Variant naming**: structural components use `{Filled, Outlined, Ghost, Tonal}`-style enums; feedback components (`Alert`, `Badge`, `Toast`, `Snackbar`, `Dialog`, `Indicator`) additionally carry a semantic axis (`Info/Success/Warning/Error/Neutral`). Both conventions are intentional — don't try to unify them.
- **Single-file components**: one file per component family under `components/<category>/`, internal section order is `ENUMS & TYPES → DATA CLASSES → THEME PROVIDER (size/color resolvers) → INTERNAL <NAME> → PUBLIC API → CONVENIENCE VARIANTS`. Match this order in new files.
- **States**: components model `default/disabled` at minimum; some (`Card`) also model `hover/pressed` in their color data class even when nothing wires them up yet — don't assume a `hover`/`pressed` field is actually live without checking the render-time `when`.

## Size system

`SizeVariant` (`theme/Dimen.kt`): `None, Nano, Compact, Small, Medium (default), Large, Huge, Massive`.

`HierarchicalSize` is an object with ~24 nested size-category objects, each exposing one `Dp` per `SizeVariant` entry plus `fun forVariant(variant): Dp`. `getSizesFor(variant): ComponentSizes` bundles one value per category. `Medium` is the anchor tier everywhere and is the WCAG touch minimum for interactive categories. Representative `Medium` values, for a feel of scale:

| Category | Medium | Category | Medium |
|---|---|---|---|
| `Container` | 48dp | `Card` | 160dp |
| `Button` | 44dp | `Avatar` | 40dp |
| `Icon` | 24dp | `Badge` | 20dp |
| `Input` | 48dp | `AppBar` | 56dp |
| `Chip` | 32dp | `Spacing` | 12dp |
| `ListItem` | 56dp | `Radius` | 8dp |

Remaining categories (no reference table needed, just aware they exist): `Toggle, Checkbox, Tab, BottomNav, Padding, Border, Shadow, TouchTarget, Divider, Stroke, SliderTrack, Image`.

**Adaptive sizing** (new, `utils/ScreenUtil.kt`): `WindowSizeClass { Compact, Medium, Expanded }` is derived from measured screen width via breakpoints at 600dp/840dp (`windowSizeClassOf(width)`), no `material3.adaptive` dependency involved — it was removed. `PixaTheme` provides it via `LocalWindowSizeClass`, exposed as `AppTheme.windowSizeClass`. `AppTheme.adaptiveSizeVariant` maps it to a default `SizeVariant` (`Compact→Medium, Medium→Large, Expanded→Huge`) via `WindowSizeClass.toAdaptiveSizeVariant()`. **Caveat**: verified that no component currently reads `AppTheme.adaptiveSizeVariant` — it's live plumbing with zero adoption so far, not yet a behavior change.

## Animation system

`utils/AnimationUtils.kt` is the single source (post-merge, `AnimationSpecs.kt` is gone). It contains three layers:

- **Fixed presets** (pick by semantic name): `indicatorSpring`, `selectionSpring`, `thumbSpring`, `colorSpring`, `fastSpring`, `slowSpring`.
- **Parameterized factories** (when a preset doesn't fit): `standardSpring()`, `fastSpringSpec()`, `smoothSpring()`, `standardTween()`, `fastTween()`, `slowTween()`, `repeatableAnimation()`, `infiniteRepeatable()`.
- **Ready-made transitions**: `fadeInTransition`/`fadeOutTransition`, `scaleInTransition`/`scaleOutTransition`, `slideInFromBottomTransition`/`slideOutToBottomTransition`, plus the composable `AnimatedVisibilityStandard(...)`.

Top-level `springAnimation()`/`tweenAnimation()` are thin delegates into the object — prefer calling `AnimationUtils` directly in new code.

## Known technical debt

**Fixed already:**
- Animation duplication (`AnimationSpecs.kt` vs `AnimationUtils.kt`) — resolved by merge; 0 raw `spring()`/`tween()` calls remain across `components/`, 28 files import `AnimationUtils`.
- No adaptive-sizing mechanism existed — `WindowSizeClass` + `AppTheme.adaptiveSizeVariant` now exist (see above). Adoption is the open item, not the mechanism.

**Still outstanding (verified against current source):**
- **9 components have no `SizeVariant` param at all**: `Alert.kt`, `According.kt`, `Menu.kt`, `Popover.kt`, `Tooltip.kt`, `Drawer.kt`, `Snackbar.kt`, `Toast.kt`, `Divider.kt`.
- **`Card.kt` has duplicate composable definitions** — `ActionCard`, `MediaCard`, `CompactCard` are each defined twice (currently at lines ~683/2824, ~1379/2019, ~1473/2357) — real copy-paste bug, not a style split.
- **Raw `.dp` literals**: 386 occurrences across 35 of 40 component files (grew, not shrank, since the last audit — new files like `PixaFAB.kt`/`PixaIconButton.kt` added more). `Tab.kt`, `Skeleton.kt`, `Card.kt`, `Stepper.kt` are the worst offenders.
- **`HierarchicalSize.forVariant()` is barely adopted** — only `Icon.kt` calls it; every other component hand-rolls its own local `when(size)` block, several with hardcoded fallback literals for uncovered enum cases.
- **Elevation is fragmented three ways**: `ElevationUtils.ComponentElevation` is used only by `Toast.kt`/`Snackbar.kt`; `Card.kt` still duplicates the identical 0/1/2/4/8dp ladder in its own private `BaseCardElevation`; `Button.kt` uses a raw nullable `Dp` with no enum.
- **`DateTimeUtils` underused** — only `DatePicker.kt` imports it; `TimePicker.kt` still does not, despite `LocalTime.toFormattedString()`/`to12HourFormat()` existing specifically for it.

## Third-party dependencies (verified against `library/build.gradle.kts` + `gradle/libs.versions.toml`)

| Dependency | Consumer(s) | Notes |
|---|---|---|
| `kotlinx-datetime` 0.7.1 | `utils/DateTimeUtils.kt`, `inputs/DatePicker.kt`, `inputs/TimePicker.kt` | date/time value types |
| `kizitonwose-calendar` (compose-multiplatform 2.10.0) | `inputs/DatePicker.kt` | calendar grid / heat-map rendering |
| `cmp-datetime-picker` (datetime-wheel-picker 1.1.0) | `inputs/DatePicker.kt`, `inputs/TimePicker.kt` | wheel-scroll picker UI |
| `cmp-constraintlayout` 0.7.0 | `display/Card.kt`, `navigation/BottomNavBar.kt` | Card's content slot is a `ConstraintLayoutScope` |
| `cmp-shimmer` 1.3.3 | `feedback/Skeleton.kt`, `display/Image.kt` | shimmer placeholder animation |
| `coil` bundle (coil-compose/network-ktor3/svg 3.4.0, exposed via `api(...)`) | `display/Icon.kt`, `display/Image.kt`, `display/Avatar.kt` | async image loading |
| `vico-multiplatform` 2.4.3 (exposed via `api(...)`) | `display/Chart.kt` | charting engine |

`material3.adaptive` (formerly declared, unused) has been **removed** from both `build.gradle.kts` and `libs.versions.toml` — do not re-add it; the lightweight `WindowSizeClass` in `ScreenUtil.kt` replaces what it would have provided.

## Skills (planned, not yet created)

- `new-component` — scaffold a component following the single-file section order and theme-token rules above.
- `color-tokens` — add/audit `AppTheme.colors` tokens across light/dark schemes.
- `animation` — apply `AnimationUtils` presets correctly; flag raw `spring()`/`tween()`.
- `third-party` — vet a new dependency addition (api vs implementation, which component owns it).
- `docs-sync` — keep component docs/showcase screens in sync with the public API.
- `adaptive-sizing` — wire `AppTheme.adaptiveSizeVariant`/`WindowSizeClass` into components that don't yet consume it.
- `debt-cleanup` — work through the outstanding debt list above (dp literals, `forVariant()` adoption, elevation fragmentation, Card.kt duplicates).

## Git rules

- Start from a clean working tree; check `git status` before beginning multi-file work.
- Branch before any change that touches more than one component file or any `theme/`/`utils/` file — this is a shared library, breakage is felt everywhere at once.
- Never commit a build that doesn't compile (`./gradlew :library:build`) — this is a published library, not an app.
- Commit messages: imperative mood, state the *why* over the *what* (see recent log for tone: `Add lightweight adaptive breakpoint system, remove unused material3-adaptive dep`).

## Source of truth

Code always wins over this file or any other `.md` in the repo (`AGENTS.md`, `DOCUMENTATION.md`, etc. may lag behind). For the full historical exploration this file was distilled from — file-by-file byte sizes, line-level findings, and the reasoning behind each debt item — see `.claude/plans/unified-meandering-iverson.md` (user-level, not in this repo). If a rule here looks wrong, `grep` the actual source before trusting it.
