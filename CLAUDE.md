# PixaCompose

Compose Multiplatform (Android + iOS) UI component library. Core constraint: **no Material 3 UI components** — everything is built from `Box`/`Row`/`Column`/`Canvas` primitives, styled through a custom theme system (`AppTheme`), never `MaterialTheme`/`Button`/`TextField`/etc. This exists so the library ships a fully custom design system that doesn't inherit Material's look, versioning churn, or defaults. The constraint is **not fully held yet**: `material3.Text` is still imported and used by 7 component files and `material3.LocalContentColor` by 2 (see Known technical debt) — that's the tracked gap, not a licence to add more. Material's *interaction* layer is fully out (`pixaRipple` replaced `material3.ripple`). Always `grep` for the current state rather than trusting this paragraph.

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
│   ├── AnimationUtils.kt   # single source for all animation specs (see below)
│   ├── ColorUtils.kt       # brand-agnostic color math (HSV/HSL/hex/blend/contrast)
│   ├── DateTimeUtils.kt    # kotlinx-datetime helpers; DatePicker.kt + TimePicker.kt consume it
│   ├── ElevationUtils.kt   # ComponentElevation enum + elevationShadow() modifier
│   ├── InteractionUtils.kt # pixaRipple() Indication — the library's own press/hover/focus feedback
│   └── ScreenUtil.kt       # screen dims + WindowSizeClass breakpoint system
└── components/{actions,display,feedback,inputs,navigation,overlay,surfaces}/  # 59 files, one component family per file
```

Component files per category: `actions` 11, `display` 12, `feedback` 8, `inputs` 16, `navigation` 5, `overlay` 4, `surfaces` 3. Note there are **two** `Card.kt` files — `display/Card.kt` (the large ConstraintLayout-slot card family) and `surfaces/Card.kt` — they are unrelated; check the import before assuming which one a call site means.

`AnimationSpecs.kt` no longer exists — it was merged into `AnimationUtils.kt`. Adaptive sizing lives in `ScreenUtil.kt` (`WindowSizeClass`), not a separate `AdaptiveSize.kt`. There is no `AppTheme.spacing` — spacing comes from `HierarchicalSize.Spacing.forVariant(size)`.

## Hard rules

- **Colors**: always `AppTheme.colors.<token>` (e.g. `AppTheme.colors.brandContentDefault`). Never hardcode `Color(0x...)` except `Color.Transparent` as a sentinel. Token shape is `{group}{Role}{Emphasis}`:
  - Groups: `brand`, `accent`, `info`, `success`, `warning`, `error` (9 tokens each) and `base` (17 tokens, richer ladder for neutral UI).
  - Roles: `Surface` (fill), `Border` (outline), `Content` (foreground). `base` additionally has `Title/Subtitle/Body/Caption/Hint/Negative/Disabled` content roles.
  - Emphasis: `Subtle` / `Default` / `Focus`.
- **Typography**: always `AppTheme.typography.<token>`, never raw `TextStyle(fontSize = ...)`. Tiers, largest to smallest: `display{Large,Medium,Small}` → `header{Bold,Regular}` → `headline{Bold,Regular}` → `title{Bold,Regular,Light}` → `subtitle{Bold,Regular,Light}` → `body{Bold,Regular,Light}` → `caption{Bold,Regular,Light}` → `overline`/`footnote{Bold,Regular}` → `label{Large,Medium,Small}` → `action{Mini,ExtraSmall,Small,Medium,Large,ExtraLarge,Huge}`. `bodyRegular` (16sp) is the scale anchor. All `lineHeight` values are rounded to the nearest 4sp so every style lands on the same 4dp baseline grid `HierarchicalSize.Spacing` uses (e.g. `subtitle*` is 18sp/28sp not 18sp/26sp, `labelMedium` is 12sp/20sp not 12sp/18sp) — don't hand-compute a new lineHeight without rounding to that grid.
- **Sizing**: prefer `HierarchicalSize.<Category>.forVariant(size)` over hand-rolled `when(size)` blocks or raw `.dp` literals. Adoption is partial (17 of 59 files, see Debt below) — the hand-rolled `when(size)` block is still the majority pattern, but it's the one being migrated away from, so don't copy it into new code.
- **Shapes**: use `AppTheme.shapes.<family>` (`theme/ShapeStyle.kt`), not raw `RoundedCornerShape(Ndp)`. There are 14 shape families × 5 size tiers (`extraSmall/small/medium/large/extraLarge`) — standard rounded/cut corners plus decorative ones (`concaveTop/Bottom`, `convexTop/Bottom`, `wave`, `archTop/Bottom`, `tab`, `notchRounded/Sharp`, `bubbleLeft/Right`), plus a size-invariant `pill` (`RoundedCornerShape(HierarchicalSize.Radius.Full)`) for chips/pill buttons/pill badges. Raw shape geometry (the actual `Shape` implementations) lives in `theme/CustomShapes.kt`; `ShapeStyle.kt` is the token layer built on top of it — don't confuse the two files, only `ShapeStyle.kt` is what components should reference. Both `CustomShapes` and Material3 `Shapes` now have a `forVariant(SizeVariant)` extension bridging the 8-tier `SizeVariant` down to the 5-tier shape family (`None/Nano`→extraSmall, `Compact`→small, `Small/Medium`→medium, `Large`→large, `Huge/Massive`→extraLarge) — prefer `AppTheme.shapes.<family>.forVariant(size)` over hand-picking a tier.
- **Animation**: use `AnimationUtils` presets/factories, never raw `spring()`/`tween()` inline. Existing components are already 100% compliant here (verified: zero raw `spring()`/`tween()` calls remain in `components/`; 46 files import `AnimationUtils`) — keep it that way.
- **Interaction feedback**: use `pixaRipple(bounded, radius, color)` from `utils/InteractionUtils.kt` as the `indication` on `clickable`/`selectable`/`toggleable`, never `androidx.compose.material3.ripple`. It's a signature-compatible drop-in that keeps Material's interaction layer out of the design system. Its `color` argument contributes **hue only** — the alpha is replaced by the relevant `PixaRippleAlpha` state alpha (pressed 0.12 / focused 0.12 / dragged 0.16 / hovered 0.08), matching what Material's ripple did. Call sites that pass a pre-attenuated color (`content.copy(alpha = 0.12f)`) are a harmless legacy of that — the alpha there is and always was discarded.
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

**Adaptive sizing** (new, `utils/ScreenUtil.kt`): `WindowSizeClass { Compact, Medium, Expanded }` is derived from measured screen width via breakpoints at 600dp/840dp (`windowSizeClassOf(width)`), no `material3.adaptive` dependency involved — it was removed. `PixaTheme` provides it via `LocalWindowSizeClass`, exposed as `AppTheme.windowSizeClass`. `AppTheme.adaptiveSizeVariant` maps it to a default `SizeVariant` (`Compact→Medium, Medium→Large, Expanded→Huge`) via `WindowSizeClass.toAdaptiveSizeVariant()`. **Adoption**: exactly one component reads it — `EmptyState.kt`, behind an opt-in `adaptiveSize: Boolean` param (`val effectiveSize = if (adaptiveSize) AppTheme.adaptiveSizeVariant else size`). Several other components (`Stepper`, `SegmentedButton`, `MessageCard`, `ToggleButtonGroup`) mention it only in doc comments, deliberately documenting that they stay caller-controlled — those are not adoption, and `grep` hits there are misleading. The established convention is opt-in per component, not implicit. `AppTheme` also exposes `pageMargin`/`sectionSpacing` composable `Dp` properties (screen-edge margin and inter-section gap per `WindowSizeClass`, sourced from `HierarchicalSize.Spacing`) — use these on screen-level root padding/gaps, not inside individual components.

## Animation system

`utils/AnimationUtils.kt` is the single source (post-merge, `AnimationSpecs.kt` is gone). It contains three layers:

- **Fixed presets** (pick by semantic name): `indicatorSpring`, `selectionSpring`, `thumbSpring`, `colorSpring`, `fastSpring`, `slowSpring`.
- **Parameterized factories** (when a preset doesn't fit): `standardSpring()`, `fastSpringSpec()`, `smoothSpring()`, `standardTween()`, `fastTween()`, `slowTween()`, `repeatableAnimation()`, `infiniteRepeatable()`.
- **Ready-made transitions**: `fadeInTransition`/`fadeOutTransition`, `scaleInTransition`/`scaleOutTransition`, `slideInFromBottomTransition`/`slideOutToBottomTransition`, plus the composable `AnimatedVisibilityStandard(...)`.

Top-level `springAnimation()`/`tweenAnimation()` are thin delegates into the object — prefer calling `AnimationUtils` directly in new code.

## Known technical debt

Counts below were re-derived from source on 2026-07-15. They drift fast — re-`grep` before trusting any number here.

**Fixed already:**
- Animation duplication (`AnimationSpecs.kt` vs `AnimationUtils.kt`) — resolved by merge; 0 raw `spring()`/`tween()` calls remain across `components/`, 46 files import `AnimationUtils`.
- No adaptive-sizing mechanism existed — `WindowSizeClass` + `AppTheme.adaptiveSizeVariant` now exist (see above). Broad adoption is intentionally *not* the goal; opt-in per component is the convention.
- `Card.kt` had duplicate composable definitions — `ActionCard`, `MediaCard`, `CompactCard` were each defined twice with incompatible signatures. Resolved: the generic definition kept the original name, the specialized preset was renamed (`ActionCard`→`ActionCtaCard`, `MediaCard`→`VideoCard`, `CompactCard`→`CompactInfoCard`). Zero call sites existed for either half of any pair, so this was a pure rename, no behavior change.
- **Elevation is now unified** — `ComponentElevation.toDp()` reads its 0/1/2/4/8dp values from `HierarchicalSize.Shadow`, and the private `BaseCardElevation` ladder that `display/Card.kt` used to duplicate is **gone** (0 occurrences repo-wide). `Card.kt` takes `elevation: ComponentElevation` directly. There is one elevation scale.
- **`DateTimeUtils` is no longer underused** — both `DatePicker.kt` and `TimePicker.kt` import it; `TimePicker.kt` uses `to12HourFormat`/`toFormattedString`.
- **`material3.ripple` is gone from `components/`** — replaced by `pixaRipple()` in `utils/InteractionUtils.kt` across all 20 files / 28 call sites that used it (see Hard rules). This closed the last Material 3 *interaction* dependency.

**Still outstanding (verified against current source):**
- **Material 3 UI components still imported in 7 files** — this is the most direct violation of the top-of-file "no Material 3 UI components" constraint, and is now the largest remaining Material surface:
  - `material3.Text` (7 files, actually used, not stale imports): `actions/{FAB, Tab, IconButton, Chip}.kt`, `display/Card.kt`, `inputs/SearchBar.kt`, `navigation/Drawer.kt`. The replacement is `androidx.compose.foundation.text.BasicText` — `Button.kt` already uses it, so there's an in-repo precedent. Note `display/Card.kt`'s ~70 `Text(` hits are mostly KDoc samples, so the real call-site count is far lower than a naive grep suggests.
  - `material3.LocalContentColor` (2 files): `actions/Chip.kt`, `display/Tag.kt`, both only to `CompositionLocalProvider(LocalContentColor provides ...)`. Needs a Pixa-owned local if that plumbing is still wanted after `Text` is gone.
- **7 components have no `SizeVariant` param at all**: `actions/Link.kt`, `display/Divider.kt`, `display/Image.kt`, `inputs/StarRating.kt`, `navigation/Drawer.kt`, `overlay/Popover.kt`, `surfaces/Card.kt`. (The old list here was wrong: `Alert`, `Menu`, `Tooltip`, `Snackbar`, `Toast` all have one now, and `According.kt` never existed — the file is `display/Accordion.kt`.)
- **Raw `.dp` literals**: 248 occurrences across 34 of 59 component files — *down* from the 386 previously recorded, not up. Worst offenders now: `actions/Tab.kt` (42), `display/Card.kt` (33), `feedback/Skeleton.kt` (23), `actions/FAB.kt` (14), `navigation/TabBar.kt` (12). `navigation/Stepper.kt` is no longer an offender at all (0) — it was listed as one of the worst.
- **`HierarchicalSize.forVariant()` adoption is partial, not absent** — 17 files call it (`Icon`, `Tag`, `Tile`, `Accordion`, `SectionHeading`, `Alert`, `Indicator`, `Snackbar`, `SystemBanner`, `Dialog`, `Tooltip`, `Stepper`, `BottomNavBar`, `ButtonDock`, `ButtonGroup`, `SegmentedButton`, `ToggleButtonGroup`). The remaining ~42 hand-roll a local `when(size)` block, several with hardcoded fallback literals for uncovered enum cases. `actions/Tab.kt` is the highest-value target (worst `.dp` count *and* no `forVariant`).

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

## Skills (live in `.claude/skills/`)

- `new-component` — scaffold a component following the single-file section order and theme-token rules above.
- `color-tokens` — add/audit `AppTheme.colors` tokens across light/dark schemes.
- `animation` — apply `AnimationUtils` presets correctly; flag raw `spring()`/`tween()`.
- `third-party` — vet a new dependency addition (api vs implementation, which component owns it).
- `docs-sync` — keep component docs/showcase screens in sync with the public API.
- `debt-cleanup` — work through the outstanding debt list above (dp literals, `forVariant()` adoption, remaining `material3.Text` usage).
- `uber-component-migrator` — migrate/create a component from an Uber Base spec URL or Markdown file.

`adaptive-sizing` was listed here as planned but was never created — adaptive sizing is opt-in per component (see above), so there's little for it to sweep.

## Git rules

- Start from a clean working tree; check `git status` before beginning multi-file work.
- Branch before any change that touches more than one component file or any `theme/`/`utils/` file — this is a shared library, breakage is felt everywhere at once.
- Never commit a build that doesn't compile (`./gradlew :library:build`) — this is a published library, not an app.
- Commit messages: imperative mood, state the *why* over the *what* (see recent log for tone: `Add lightweight adaptive breakpoint system, remove unused material3-adaptive dep`).

## Source of truth

Code always wins over this file or any other `.md` in the repo (`AGENTS.md`, `DOCUMENTATION.md`, etc. may lag behind). For the full historical exploration this file was distilled from — file-by-file byte sizes, line-level findings, and the reasoning behind each debt item — see `.claude/plans/unified-meandering-iverson.md` (user-level, not in this repo). If a rule here looks wrong, `grep` the actual source before trusting it.
