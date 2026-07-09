---
name: docs-sync
description: Keep DOCUMENTATION.md and the androidApp demo showcase in sync after any change to a component's public API. Trigger phrases -- "update the docs", "sync the showcase", "I changed a component's API", "add a showcase screen", "register a new component in the demo", "update DOCUMENTATION.md".
---

# docs-sync

PixaCompose has two places that must track a component's public API — both are stale as soon as you rename a parameter, add a variant, or ship a new component, and neither is auto-generated.

## The two sync points

**1. `DOCUMENTATION.md` → `## Component Reference`** (starts around line 431). One `#### PixaXxx` heading per public composable, grouped under `### Actions` / `### Inputs` / `### Display` / `### Feedback` / `### Navigation` (matching the `components/<category>/` folders). Each section documents the signature and shows usage examples — this is hand-written prose, not generated from KDoc, so it drifts silently.

**2. `androidApp/.../demo/`** — the live showcase app:
- `ComponentRegistry.kt` — a sealed class `ComponentEntry` where each `data object` declares `name`, `category` (`ComponentCategory` enum: `Actions/Inputs/Display/Feedback/Overlay/Navigation`), `description`, and a `showcase` lambda pointing at a composable in `demo/components/`.
- `demo/components/<Name>Showcase.kt` — one file per component, the actual interactive demo (`ButtonShowcase.kt`, `DatePickerShowcase.kt`, etc. — 40 of these currently exist, one per public component).
- `MainScreen.kt` reads `ComponentEntry.byCategory(category)` to build the component list — it needs no changes when you add/update an entry, it's driven entirely by `ComponentRegistry.kt`.

## Process after any public API change

**Existing component, changed API** (renamed param, new variant, new default):
1. Update the corresponding `#### PixaXxx` section in `DOCUMENTATION.md` — check every code sample under that heading still compiles against the new signature.
2. Update the matching `demo/components/<Name>Showcase.kt` to exercise the changed API (new variant should get its own visible example in the showcase, not just compile).

**Brand new component**:
1. Add its `#### PixaXxx` section to `DOCUMENTATION.md` under the right category, following the existing heading style (see a neighboring entry in the same category for tone/format).
2. Create `demo/components/<Name>Showcase.kt`.
3. Register it: add a new `data object` to `ComponentRegistry.kt`'s `ComponentEntry` sealed class with `name`, correct `ComponentCategory`, a one-line `description`, and `showcase` pointing at the new file. Without this step the component compiles and works but never appears in the demo app.

## What NOT to do

- Don't skip the showcase update because "it still compiles" — the showcase exists to be visually exercised (per this repo's `verify`-skill philosophy of driving real UI, not just type-checking).
- Don't add a `ComponentEntry` without a matching `Showcase.kt` file, or vice versa — `MainScreen.kt` will either crash on a missing reference or silently never show a working demo.
- Don't invent a new documentation location — `DOCUMENTATION.md` is the single source; there is no per-component `.md` file.
