---
name: color-tokens
description: Full catalog of PixaCompose's 79 AppTheme.colors tokens, the group/role/emphasis naming scheme, and the ComponentColors + Color.Unspecified override-fallback pattern real components use. Trigger phrases -- "what color token should I use", "add a color to the theme", "which AppTheme.colors token", "color override pattern", "customize component colors", "add colorOverrides support".
---

# color-tokens

`CLAUDE.md` states the naming rule (`{group}{Role}{Emphasis}`) and the hard rule to never hardcode colors. This file is the full token catalog plus the real override pattern components use.

## Full token catalog (`theme/Color.kt` → `ColorPalette`, 79 tokens)

Groups `brand`, `accent`, `info`, `success`, `warning`, `error` all share the identical 9-token shape:

| Role | Subtle | Default | Focus |
|---|---|---|---|
| Surface | `{g}SurfaceSubtle` | `{g}SurfaceDefault` | `{g}SurfaceFocus` |
| Border | `{g}BorderSubtle` | `{g}BorderDefault` | `{g}BorderFocus` |
| Content | `{g}ContentSubtle` | `{g}ContentDefault` | `{g}ContentFocus` |

`base` (neutral UI) has a richer, 17-token ladder instead:

- Surface: `baseSurfaceSubtle`, `baseSurfaceDefault`, `baseSurfaceElevated`, `baseSurfaceFocus`, `baseSurfaceShadow`, `baseSurfaceDisabled`
- Border: `baseBorderSubtle`, `baseBorderDefault`, `baseBorderFocus`, `baseBorderDisabled`
- Content: `baseContentTitle`, `baseContentSubtitle`, `baseContentBody`, `baseContentCaption`, `baseContentHint`, `baseContentNegative`, `baseContentDisabled`

**Anchor rule**: every group's `*ContentDefault` is weight 500 of its raw scale in both light and dark mode — this is the one fixed point the whole palette is built around. Everything else shifts around it (lighter in light mode, darker in dark mode).

## When you need a new token

- **A new semantic color for an existing group** (e.g. a new emphasis tier): add the field to `ColorPalette` (`Color.kt:321`), to both `localLightColorScheme`/`localDarkColorScheme` builders, to `ColorOverrides` (nullable mirror, `Color.kt:626`), to `ColorOverrides.isEmpty()`'s field list, and to `applyColorOverrides()` in `PixaTheme.kt`. Miss one of these five and overrides silently stop working for that token.
- **A whole new group** (e.g. a `neutral2` family): also add a raw 50–950 scale map (like `brandColor` at `Color.kt:8`), wire it into `buildColorPaletteFromScales()` in `PixaTheme.kt`, and add it to `ColorScales`/`DefaultColorScales`.
- Don't invent ad-hoc token names outside `{group}{Role}{Emphasis}` — if a component needs a color that doesn't map to an existing role/emphasis, that's a sign to re-examine the design rather than bolt on a one-off field.

## The `ComponentColors` + `Color.Unspecified` override pattern

Real components (`PixaIconButton.kt`, `PixaFAB.kt`, `Tab.kt`) let callers override individual colors per-instance without needing a full custom theme. The pattern, verified from `PixaIconButton.kt:49-56` and `:191-223`:

```kotlin
@Immutable
data class IconButtonColors(
    val containerColor: Color = Color.Unspecified,   // sentinel: "not overridden"
    val contentColor: Color = Color.Unspecified,
    val borderColor: Color = Color.Unspecified,
    val disabledContainerColor: Color = Color.Unspecified,
    val disabledContentColor: Color = Color.Unspecified,
)

@Composable
fun PixaIconButton(
    // ...
    colors: IconButtonColors = IconButtonColors(),   // empty = fully theme-driven
) {
    val themeColors = getIconButtonTheme(variant, AppTheme.colors, selected, enabled)

    val containerColor by animateColorAsState(
        targetValue = if (!enabled) themeColors.disabledContainerColor
        else if (colors.containerColor != Color.Unspecified) colors.containerColor   // caller override wins
        else themeColors.containerColor,                                            // else fall back to theme
        label = "iconButtonContainer"
    )
}
```

Use this pattern (not a nullable `Color?`) whenever a component needs "theme default, but overridable per call site" — `Color.Unspecified` is a real, comparable `Color` value so `!= Color.Unspecified` is a safe, cheap check. Note: `Button.kt`'s equivalent (`customColors: ButtonStateColors? = null`) uses nullable instead — both patterns exist in the codebase; prefer the `Color.Unspecified` version for new components since it composes better with `.copy()` for partial overrides (see `getIconButtonTheme`'s `selected` branch using `baseColors.copy(...)`).

## Variant → token mapping (structural components: Filled/Tonal/Outlined/Ghost)

Canonical source: `PixaIconButton.kt:getIconButtonTheme()` and `PixaFAB.kt:getFABTheme()` — every structural Filled/Tonal/Outlined/Ghost component in the library (`Chip`, `TextField`, `TextArea`, `Dropdown`, `SearchBar`) follows the same rule: **background is always a `Surface`-role token, content is always a `Content`-role token, never the reverse.** `Surface` = fill, `Content` = foreground — using a `Content` token as a background inverts the roles' purpose and was a real bug in `Button.kt` (fixed — see below), not a valid alternate pattern.

| Variant | background/container | content | border |
|---|---|---|---|
| `Filled` | `{g}SurfaceDefault` | `{g}ContentDefault` | `Color.Transparent` |
| `Tonal` | `{g}SurfaceSubtle` | `{g}ContentDefault` | `Color.Transparent` |
| `Outlined` | `Color.Transparent` | `{g}ContentDefault` | `{g}BorderDefault` |
| `Ghost` | `Color.Transparent` | `{g}ContentDefault` | `Color.Transparent` |
| any variant, `disabled` | `baseSurfaceDisabled` | `baseContentDisabled` | `baseBorderDisabled` or `Color.Transparent` |

`{g}` = `brand` normally, swapped to `error` when the component has an `isDestructive`/error-semantic flag (see `Button.kt`'s `brandOrErrorContent`/`brandOrErrorSurface`/`brandOrErrorSurfaceSubtle` pattern — resolve the group once per tier at the top of the theme resolver, then reuse across all four variant branches).

**Fixed 2026-07-10**: `Button.kt`'s `getButtonTheme()` previously set `Filled`'s background to `{g}ContentDefault` with a hardcoded `Color.White` for content — the sole outlier against every other Filled/Tonal/Outlined/Ghost component in the codebase, and a fragile one (a hardcoded `Color.White` has no guaranteed contrast against a customized `brandContentDefault`). It now matches the table above; `Tonal` was moved from `{g}SurfaceDefault` down to `{g}SurfaceSubtle` in the same fix so the two variants stay visually distinct instead of colliding on the same tier. Note this is a distinct taxonomy from `BadgeStyle.Filled` (`Badge.kt`), which does use `{g}ContentDefault` as background — that's the semantic-severity axis (see `CLAUDE.md`'s variant-naming rule), not this structural one, and isn't evidence against the rule above.

For feedback-category components (`Alert`, `Badge`, `Toast`, `Snackbar`, `Dialog`, `Indicator`), the semantic axis (`Info/Success/Warning/Error/Neutral`) selects `{g}` directly instead of a Filled/Tonal/Outlined/Ghost style axis — see `CLAUDE.md`'s variant-naming rule for why both conventions are intentional.
