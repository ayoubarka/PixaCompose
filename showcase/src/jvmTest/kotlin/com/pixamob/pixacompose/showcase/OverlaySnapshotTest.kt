package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.use
import com.pixamob.pixacompose.components.Button
import com.pixamob.pixacompose.components.ToastHost
import com.pixamob.pixacompose.components.Tooltip
import com.pixamob.pixacompose.components.rememberToastState
import com.pixamob.pixacompose.primitives.Text
import com.pixamob.pixacompose.styles.PixaStyles
import com.pixamob.pixacompose.theme.PixaTheme
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

private const val Width = 840
private const val Height = 1000

/** Renders each overlay open, once per built-in style, into `showcase/build/snapshots/overlays/`. */
class OverlaySnapshotTest {
    private val outDir = File("build/snapshots/overlays").apply { mkdirs() }

    @Test
    fun rendersEveryOverlay() {
        for (style in PixaStyles.all) {
            for (demo in OverlayDemo.entries) {
                ImageComposeScene(Width, Height, Density(2f)) {
                    PixaTheme(style, darkTheme = false) {
                        val toasts = rememberToastState()
                        Box(Modifier.fillMaxSize().background(PixaTheme.colors.background)) {
                            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                OverlaysSection(toasts, initiallyOpen = demo)
                            }
                            ToastHost(toasts, Modifier.align(Alignment.BottomCenter))
                        }
                    }
                }.use { save(it, "${style.name.lowercase()}-${demo.name.lowercase()}") }
            }
        }
    }

    @Test
    fun rendersTooltipOnHover() {
        for (style in PixaStyles.all) {
            ImageComposeScene(Width, 400, Density(2f)) {
                PixaTheme(style, darkTheme = false) {
                    Box(Modifier.fillMaxSize().background(PixaTheme.colors.background), Alignment.Center) {
                        Tooltip(tooltip = { Text("Add a new item") }) {
                            Button(onClick = {}) { Text("Hover me") }
                        }
                    }
                }
            }.use { scene ->
                scene.render()
                scene.sendPointerEvent(PointerEventType.Move, Offset(Width / 2f, 200f))
                scene.render()
                // The hover delay runs on real time, not the scene's frame clock.
                Thread.sleep(600)
                save(scene, "${style.name.lowercase()}-tooltip")
            }
        }
    }

    /** Advances the clock far enough for enter animations and the tooltip delay to finish. */
    private fun save(scene: ImageComposeScene, name: String) {
        var image = scene.render()
        for (millis in 100..3000 step 100) image = scene.render(nanoTime = millis * 1_000_000L)
        val file = File(outDir, "$name.png")
        file.writeBytes(checkNotNull(image.encodeToData(EncodedImageFormat.PNG)).bytes)
        assertTrue(file.length() > 0, "empty snapshot for ${file.name}")
    }
}
