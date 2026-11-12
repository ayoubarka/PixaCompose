# PixaCompose

Style-agnostic UI components for Compose Multiplatform (Android, iOS, desktop), built on
`compose.foundation` only. One set of components, any visual language: the look comes from a
`PixaStyle` object, not from the component code.

```kotlin
PixaTheme(style = PixaStyles.Cartoon) {   // Minimal, Cartoon, Neon, or your own PixaStyle
    Button(onClick = { }) { Text("Save") }
}
```

Version 2 is a ground-up rewrite. The 1.x code lives on the `legacy` branch.

## Modules

| Module | Purpose |
|---|---|
| `:pixacompose` | The published library (`com.pixamob:pixacompose`) |
| `:showcase` | Shared showcase UI, desktop entry point, snapshot test |
| `:androidApp` | Thin Android wrapper around `:showcase` |

## Run

```bash
./gradlew :showcase:run                 # desktop showcase, fastest loop
./gradlew :androidApp:installDebug      # Android
./gradlew :showcase:jvmTest             # renders every style to showcase/build/snapshots/
```

See [ROADMAP.md](ROADMAP.md) for what is planned and [CLAUDE.md](CLAUDE.md) for the rules.
