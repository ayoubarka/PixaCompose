---
name: third-party
description: The verified PixaCompose third-party dependency table plus the required process before adding any new dependency to library/build.gradle.kts. Trigger phrases -- "add a dependency", "add a library for X", "can we use <library>", "new gradle dependency", "does this need a third-party library", "api vs implementation".
---

# third-party

`CLAUDE.md` already has this table verified against `library/build.gradle.kts` + `gradle/libs.versions.toml` — reproduced here so this skill is self-contained:

| Dependency | Consumer(s) | Notes |
|---|---|---|
| `kotlinx-datetime` 0.7.1 | `utils/DateTimeUtils.kt`, `inputs/DatePicker.kt`, `inputs/TimePicker.kt` | date/time value types |
| `kizitonwose-calendar` (compose-multiplatform 2.10.0) | `inputs/DatePicker.kt` | calendar grid / heat-map rendering |
| `cmp-datetime-picker` (datetime-wheel-picker 1.1.0) | `inputs/DatePicker.kt`, `inputs/TimePicker.kt` | wheel-scroll picker UI |
| `cmp-constraintlayout` 0.7.0 | `display/Card.kt`, `navigation/BottomNavBar.kt` | Card's content slot is a `ConstraintLayoutScope` |
| `cmp-shimmer` 1.3.3 | `feedback/Skeleton.kt`, `display/Image.kt` | shimmer placeholder animation |
| `coil` bundle (coil-compose/network-ktor3/svg 3.4.0, exposed via `api(...)`) | `display/Icon.kt`, `display/Image.kt`, `display/Avatar.kt` | async image loading |
| `vico-multiplatform` 2.4.3 (exposed via `api(...)`) | `display/Chart.kt` | charting engine |

**Removed, do not re-add**: `material3.adaptive` (`adaptive`/`adaptive-layout`/`adaptive-navigation` bundle) was declared but had zero usages; it's been stripped from both `build.gradle.kts` and `libs.versions.toml`. The lightweight `WindowSizeClass` in `utils/ScreenUtil.kt` replaces what it would have provided — don't reintroduce the Gradle dependency to get window-size-class behavior.

## Process for proposing a new dependency

Follow these in order — **stop and ask the user for approval before editing `build.gradle.kts`**, this is not a step to push through autonomously:

1. **Justify why Compose Multiplatform + existing `utils/` can't do it.** Most raw layout, gesture, and animation needs are already covered by `Box`/`Row`/`Column`/`Canvas`/`Modifier` + `AnimationUtils`. A new dependency should solve something genuinely hard (calendar math, image decoding/caching, chart rendering) — the kind of thing already justifying the 6 dependencies above — not a convenience wrapper around a few lines of Compose.
2. **Check KMP compatibility.** The library targets Android + iOS via `commonMain`. Confirm the library ships a `compose-multiplatform`/`kotlin-multiplatform` artifact (not JVM/Android-only) before adding it — check the library's own published metadata, not just its README.
3. **Decide `api(...)` vs `implementation(...)`.** If library consumers need to reference the dependency's types directly at their call sites (like `Coil`'s `AsyncImage` or `Vico`'s chart model types), it must be `api(...)`. If it's purely an internal implementation detail (like `kizitonwose-calendar` or `cmp-shimmer`), use `implementation(...)` so it doesn't leak into consumers' classpaths.
4. **Identify and record the exact consumer file(s) up front** — don't add a dependency "for later use." Every entry in the table above maps to specific files; a new entry should too.
5. **Wait for explicit approval** before touching `gradle/libs.versions.toml` or `library/build.gradle.kts` — dependency changes affect every consumer of the published library, and are exactly the kind of hard-to-reverse, shared-impact change that needs a human sign-off first.
