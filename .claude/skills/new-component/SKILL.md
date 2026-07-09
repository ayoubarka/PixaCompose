---
name: new-component
description: Scaffold a new PixaCompose component (or a new variant of an existing one) following the library's mandatory single-file structure, theme-token rules, and sizing/animation conventions. Trigger phrases -- "add a new component", "create a PixaX component", "scaffold a component", "add a Pixa<Name>", "new component for <category>", "how do I structure a component in this library".
---

# new-component

Scaffolds a component that matches every hard rule in `CLAUDE.md`. Read `CLAUDE.md` first for the token/sizing/animation rules this skill assumes — this file only covers structure and the concrete template.

## Where it goes

One file per component family under `library/src/commonMain/kotlin/com/pixamob/pixacompose/components/<category>/<Name>.kt`, category is one of `actions, display, feedback, inputs, navigation, overlay`. Never split a component family across files, and never add it to `components/display/Card.kt` — that file is explicitly non-canonical (see `debt-cleanup` skill) and must not be used as a reference pattern.

## Reference files

Use `components/actions/Button.kt` (public API, states, animated color transitions) and `components/inputs/DatePicker.kt` (multi-variant dispatch, complex size/color resolution) as the canonical patterns. Do **not** copy patterns from `Card.kt` — it has duplicate composable definitions and hand-rolled elevation that contradict the rules below.

## Mandatory section order

Every file is organized top to bottom, marked with `// ====...` banner comments:

```
1. imports
2. // ENUMS & TYPES        -- Variant, Shape/Mode, WidthPolicy-style sealed classes
3. // DATA CLASSES          -- SizeConfig, Colors, StateColors (@Immutable @Stable)
4. // THEME PROVIDER        -- size resolver: getXSizeConfig(size: SizeVariant)
                            -- color resolver: getXTheme(variant, colors: ColorPalette)
5. // INTERNAL <NAME>        -- private InternalX(...) with the real render logic
6. // PUBLIC API             -- fun PixaX(...) — the only thing library users call
7. // CONVENIENCE VARIANTS   -- thin wrappers, only if there's real precedent (see Button: none; Card: many)
```

## Template (adapted from `Button.kt`)

```kotlin
package com.pixamob.pixacompose.components.<category>

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.ColorPalette
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.AnimationUtils

// ════════════════════════════════════════════════════════════════════════════
// ENUMS & TYPES
// ════════════════════════════════════════════════════════════════════════════

enum class XVariant { Filled, Tonal, Outlined, Ghost }

// ════════════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ════════════════════════════════════════════════════════════════════════════

@Immutable
@Stable
data class XSizeConfig(
    val height: Dp,
    val iconSize: Dp,
    val cornerRadius: Dp
)

@Immutable
@Stable
data class XColors(
    val background: Color,
    val content: Color,
    val border: Color = Color.Transparent
)

@Immutable
@Stable
data class XStateColors(
    val default: XColors,
    val disabled: XColors
)

// ════════════════════════════════════════════════════════════════════════════
// THEME PROVIDER
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun getXSizeConfig(size: SizeVariant): XSizeConfig = XSizeConfig(
    height = HierarchicalSize.Container.forVariant(size),
    iconSize = HierarchicalSize.Icon.forVariant(size),
    cornerRadius = HierarchicalSize.Radius.forVariant(size)
)
// ^ prefer forVariant() over a hand-rolled `when(size)` block -- almost every
//   existing component still hand-rolls this; don't add another one.

@Composable
private fun getXTheme(variant: XVariant, colors: ColorPalette): XStateColors = when (variant) {
    XVariant.Filled -> XStateColors(
        default = XColors(background = colors.brandContentDefault, content = Color.White),
        disabled = XColors(background = colors.baseSurfaceDisabled, content = colors.baseContentDisabled)
    )
    // ... Tonal / Outlined / Ghost, see color-tokens skill for the full mapping table
    else -> TODO()
}

// ════════════════════════════════════════════════════════════════════════════
// INTERNAL X
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun InternalX(
    enabled: Boolean,
    colors: XStateColors,
    sizeConfig: XSizeConfig
) {
    val current = if (enabled) colors.default else colors.disabled
    val backgroundColor by animateColorAsState(
        targetValue = current.background,
        animationSpec = AnimationUtils.standardTween(150),
        label = "x_bg"
    )
    // render with Box/Row/Column only -- never Material 3 components
}

// ════════════════════════════════════════════════════════════════════════════
// PUBLIC API
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun PixaX(
    modifier: Modifier = Modifier,
    variant: XVariant = XVariant.Filled,
    enabled: Boolean = true,
    size: SizeVariant = SizeVariant.Medium,
) {
    val sizeConfig = getXSizeConfig(size)
    val colors = getXTheme(variant, AppTheme.colors)
    InternalX(enabled = enabled, colors = colors, sizeConfig = sizeConfig)
}
```

## Checklist before calling it done

- [ ] File lives under `components/<category>/<Name>.kt`, one component family per file
- [ ] Section order matches the banner-comment structure above
- [ ] `size: SizeVariant = SizeVariant.Medium` is a real parameter — don't ship a component in the "9 components missing size param" state (see `debt-cleanup`)
- [ ] Sizing pulls from `HierarchicalSize.<Category>.forVariant(size)`, not a local `when(size)` with hardcoded `.dp` literals
- [ ] All colors come from `AppTheme.colors.<token>` (see `color-tokens` skill for the full catalog) — no hardcoded `Color(0x...)` except `Color.Transparent` as a sentinel
- [ ] All text styles come from `AppTheme.typography.<token>`
- [ ] Shapes come from `AppTheme.shapes.<family>`, not raw `RoundedCornerShape(Ndp)`
- [ ] Any animated value goes through `AnimationUtils` (see `animation` skill) — zero raw `spring()`/`tween()` calls
- [ ] Variant enum follows `{Filled, Outlined, Ghost, Tonal}` unless this is a feedback-category component, where a semantic axis (`Info/Success/Warning/Error`) is also expected
- [ ] Colors/state data classes are `@Immutable @Stable`, model at minimum `default`/`disabled`
- [ ] No Material 3 components (`Button`, `TextField`, `Card`, etc.) anywhere in the implementation — `Box`/`Row`/`Column`/`Canvas` only
- [ ] After the component compiles, run the `docs-sync` skill to add its `DOCUMENTATION.md` section and demo showcase file
