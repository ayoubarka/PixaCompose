# PixaCompose

Style-agnostic UI components for Compose Multiplatform (Android, iOS, desktop), built on
`compose.foundation` only. One set of components, any visual language: the look comes from a
`PixaStyle` object, not from the component code.

```kotlin
PixaTheme(style = PixaStyles.Cartoon) {   // Minimal, Cartoon, or your own PixaStyle
    PixaButton(onClick = { }) { PixaText("Save") }
}
```

Version 2 is a ground-up rewrite. The 1.x code lives on the `legacy` branch.

## Your own colors and fonts

A style is a plain data class. Start from a built-in one and change what your brand needs:

```kotlin
val base = PixaStyles.Minimal
val appStyle = base.copy(
    lightColors = base.lightColors.copy(primary = Color(0xFF1F9D7A), primaryForeground = Color.White),
    darkColors = base.darkColors.copy(primary = Color(0xFF4FD1AE), primaryForeground = Color(0xFF05241C)),
    shapes = base.shapes.copy(md = RoundedCornerShape(16.dp)),
).withFontFamily(body = Nunito, heading = Baloo)

PixaTheme(style = appStyle) { App() }
```

Fonts loaded from Compose resources (`Font(Res.font.nunito_regular)`) are composable, so build
the `FontFamily` inside the composition and `remember` the style. Sizes and weights can be changed
through `typography.copy(...)`.

## Modules

| Module | Purpose |
|---|---|
| `:pixacompose` | The published library (`com.pixamob:pixacompose`) |
| `:pixacompose-image` | Optional. `PixaImage`, loaded with Coil (`com.pixamob:pixacompose-image`) |
| `:pixacompose-charts` | Optional. `PixaLineChart` and `PixaBarChart`, drawn with Vico (`com.pixamob:pixacompose-charts`) |
| `:pixacompose-datetime` | Optional. `PixaCalendar` and `PixaDatePicker` (`com.pixamob:pixacompose-datetime`) |
| `:showcase` | Shared showcase UI, desktop entry point, snapshot test |
| `:androidApp` | Thin Android wrapper around `:showcase` |

## Run

```bash
./gradlew :showcase:run                 # desktop showcase, fastest loop
./gradlew :androidApp:installDebug      # Android
./gradlew :showcase:jvmTest             # renders every style to showcase/build/snapshots/
```

App developers: start with [docs/GUIDE.md](docs/GUIDE.md), the component list and rules for using the library.

See [ROADMAP.md](ROADMAP.md) for what is planned and [CLAUDE.md](CLAUDE.md) for the rules.
