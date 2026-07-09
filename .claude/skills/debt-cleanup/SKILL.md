---
name: debt-cleanup
description: Actionable task list for PixaCompose's known technical debt -- missing SizeVariant params, Card.kt's duplicate composables, raw dp literal cleanup ranked by severity, HierarchicalSize.forVariant() adoption, and elevation unification. Trigger phrases -- "clean up technical debt", "fix Card.kt duplicates", "add size param to X", "remove raw dp literals", "unify elevation", "adopt forVariant", "what's the biggest debt item".
---

# debt-cleanup

Every item below was verified against current source (not carried over from a stale audit — see `CLAUDE.md`'s "Known technical debt" for the fixed-vs-outstanding split). Pick one item at a time; each is independently shippable. **Branch before starting any of these** — several touch widely-used files (`Card.kt`, `Tab.kt`) per `CLAUDE.md`'s git rules.

## 1. Add `SizeVariant` to the 9 components that have none

None of these take a `size: SizeVariant` parameter at all today:

| File | Category |
|---|---|
| `feedback/Alert.kt` | `PixaAlert` + 4 severity wrappers |
| `actions/According.kt` | accordion (note: file is actually named `According.kt`, not `Accordion.kt`) |
| `overlay/Menu.kt` | menu |
| `overlay/Popover.kt` | popover |
| `overlay/Tooltip.kt` | tooltip |
| `navigation/Drawer.kt` | drawer |
| `feedback/Snackbar.kt` | internal `Snackbar`/`SnackbarHost`/`GlobalSnackbarHost` |
| `feedback/Toast.kt` | internal `Toast`/`ToastHost`/`GlobalToastHost` |
| `display/Divider.kt` | has `thickness: Dp` but no `SizeVariant` |

For each: add `size: SizeVariant = SizeVariant.Medium`, resolve dimensions via `HierarchicalSize.<Category>.forVariant(size)` (see item 3 below — don't hand-roll a local `when(size)`), and follow the `new-component` skill's section-order rules while you're touching the file.

## 2. Fix `Card.kt`'s duplicate composable definitions

Three names are each defined twice in `library/src/commonMain/kotlin/com/pixamob/pixacompose/components/display/Card.kt`, with **incompatible signatures** (this compiles today only because Kotlin treats them as overloads — it's still a confusing, duplicated API surface that needs one canonical version per name):

- **`CompactCard`**: line ~683 (thin `Tier 1` wrapper: `modifier, variant, onClick, content` → delegates to `PixaCard(padding = Compact)`) vs. line ~2824 (`Tier 2` preset: `title, icon, onClick, enabled, variant, cornerRadius, isLoading`).
- **`ActionCard`**: line ~1379 (`icon, title, description, ctaText, onCtaClick, ...`) vs. line ~2019 (`title, onClick, subtitle, description, icon, iconUrl, trailingIcon, enabled, padding, ...`).
- **`MediaCard`**: line ~1473 (`imageUrl, title, subtitle, duration, showPlayButton, ...`) vs. line ~2357 (`imageUrl, title, subtitle, description, imageHeight, cornerRadius, ...`).

Process: for each pair, grep the rest of the repo (including `androidApp/`) for actual call sites of that name to see which signature is really in use, keep that one, delete the other, and rename if you want to preserve both behaviors (e.g. the deleted "action + CTA button" shape could become `ActionCtaCard`). Don't just delete blindly — line numbers above will shift once you remove the first duplicate, re-grep (`grep -n "^fun ActionCard\|^fun MediaCard\|^fun CompactCard"`) before touching the second one.

## 3. Adopt `HierarchicalSize.forVariant()` — currently used by exactly 1 of 40 components

Only `display/Icon.kt` calls `.forVariant(size)`. Every other component hand-rolls a local `when(size) { ... }` block that duplicates the same `Dp` values already sitting in `HierarchicalSize`, several with hardcoded fallback literals for enum cases they don't bother mapping (`Nano`/`Huge`/`Massive` often fall through to a `Medium`-shaped default). This isn't a single task — do it opportunistically whenever you're already in a file for another reason (e.g. while doing item 1 or 4), rather than a standalone sweep across 39 files.

## 4. Raw `.dp` literal cleanup — 386 occurrences across 35 of 40 files, ranked worst-first

Work top-down; the first four files alone account for ~40% of all occurrences:

| Rank | File | Count |
|---|---|---|
| 1 | `actions/Tab.kt` | 42 |
| 2 | `feedback/Skeleton.kt` | 39 |
| 3 | `display/Card.kt` | 38 |
| 4 | `navigation/Stepper.kt` | 27 |
| 5 | `actions/Button.kt` | 18 |
| 6 | `feedback/Badge.kt` | 17 |
| 7 | `inputs/DatePicker.kt` | 14 |
| 7 | `actions/PixaFAB.kt` | 14 |
| 9 | `overlay/BottomSheet.kt` | 13 |
| 9 | `inputs/TimePicker.kt` | 13 |
| 11 | `navigation/TopNavBar.kt` | 12 |
| 11 | `navigation/BottomNavBar.kt` | 12 |
| 11 | `inputs/Dropdown.kt` | 12 |
| 14 | `display/Avatar.kt` | 10 |
| 14 | `actions/PixaIconButton.kt` | 10 |

(Remaining ~20 files each have ≤9 occurrences — `grep -roE '[0-9]+(\.[0-9]+)?\.dp' components/<file> | wc -l` to recheck any file's current count before starting, this list will drift as you fix things.)

For each literal: check whether it matches (or is close to) an existing `HierarchicalSize.<Category>` value for the same `SizeVariant` tier — if so, replace with `HierarchicalSize.<Category>.<Tier>` or `.forVariant(size)`; if it's a one-off value with no category equivalent (e.g. a decorative offset), leave it — not every `.dp` is debt, only the ones duplicating a value the theme already defines.

## 5. Unify elevation (currently fragmented three ways)

- `utils/ElevationUtils.kt`'s `ComponentElevation` enum (`None/Low/Medium/High/Highest` → 0/1/2/4/8dp) + `Modifier.elevationShadow(...)` is used by exactly 2 files: `feedback/Toast.kt` (`ComponentElevation.Medium` default) and `feedback/Snackbar.kt` (`ComponentElevation.Highest` default).
- `display/Card.kt` has its own **duplicate** `BaseCardElevation` enum (same 0/1/2/4/8dp ladder) with a private `getBaseCardElevationDp()` — and confusingly, `Toast.kt`/`Snackbar.kt` compose `PixaCard` internally too, so they end up importing *both* `ComponentElevation` (for their own `elevationShadow` modifier) and `BaseCardElevation` (to pass `elevation = BaseCardElevation.None` into the nested `PixaCard`, since Card's own shadow handles it instead).
- `actions/Button.kt` uses a raw nullable `Dp? = null` with no enum at all, auto-resolving to `HierarchicalSize.Shadow.Nano` (1dp) for `Filled`/`Tonal` variants.

Target end state: `Card.kt` should consume `ElevationUtils.ComponentElevation` directly instead of maintaining `BaseCardElevation`/`getBaseCardElevationDp()` — the values are already identical, so this is a rename-and-delete, not a redesign. `Button.kt`'s raw `Dp?` can stay as-is if you'd rather not touch a widely-used public API's parameter type, but note it explicitly if you leave it, so the next pass doesn't assume it's an oversight.

## 6. `DateTimeUtils` underuse

Only `inputs/DatePicker.kt` imports `utils/DateTimeUtils.kt`. `inputs/TimePicker.kt` still does not, despite `LocalTime.toFormattedString()` and `LocalTime.to12HourFormat(am, pm)` existing specifically for time formatting — check what formatting logic `TimePicker.kt` currently hand-rolls and replace it with these two functions.
