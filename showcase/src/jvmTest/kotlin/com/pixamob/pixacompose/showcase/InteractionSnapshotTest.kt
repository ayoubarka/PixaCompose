package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.use
import com.pixamob.pixacompose.charts.PixaBarChart
import com.pixamob.pixacompose.charts.PixaLineChart
import com.pixamob.pixacompose.components.PixaSlider
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.styles.PixaStyles
import com.pixamob.pixacompose.theme.PixaStyle
import com.pixamob.pixacompose.theme.PixaTheme
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

private const val Width = 840
private val Steps = listOf(6200, 7400, 8752, 5100, 9300, 4000, 7800)

/**
 * Renders what only shows while touching: the slider's value bubble and the charts' value
 * marker. Output goes to `showcase/build/snapshots/interaction/`.
 */
class InteractionSnapshotTest {
    private val outDir = File("build/snapshots/interaction").apply { mkdirs() }

    @Test
    fun sliderShowsValueBubbleWhilePressed() {
        for (style in PixaStyles.all) {
            scene(style, height = 300) {
                var value by remember { mutableFloatStateOf(0.4f) }
                PixaSlider(value, { value = it }, valueLabel = { PixaText("${(it * 100).toInt()}%") })
            }.use { scene ->
                scene.render()
                scene.sendPointerEvent(PointerEventType.Press, Offset(600f, 150f))
                save(scene, "${style.name.lowercase()}-slider")
            }
        }
    }

    @Test
    fun chartsShowValueMarkerAfterTap() {
        for (style in PixaStyles.all) {
            val charts: List<Pair<String, @Composable () -> Unit>> = listOf(
                "line" to { PixaLineChart(Steps) },
                "bar" to { PixaBarChart(Steps) },
            )
            for ((name, chart) in charts) {
                scene(style, height = 480, content = chart).use { scene ->
                    scene.render()
                    scene.sendPointerEvent(PointerEventType.Press, Offset(500f, 240f))
                    scene.render(nanoTime = 50_000_000)
                    scene.sendPointerEvent(PointerEventType.Release, Offset(500f, 240f))
                    save(scene, "${style.name.lowercase()}-chart-$name")
                }
            }
        }
    }

    private fun scene(style: PixaStyle, height: Int, content: @Composable () -> Unit) =
        ImageComposeScene(Width, height, Density(2f)) {
            PixaTheme(style, darkTheme = false) {
                Box(
                    Modifier.fillMaxSize().background(PixaTheme.colors.background).padding(20.dp),
                    contentAlignment = Alignment.Center,
                ) { content() }
            }
        }

    private fun save(scene: ImageComposeScene, name: String) {
        var image = scene.render(nanoTime = 100_000_000)
        for (millis in 200..1500 step 100) {
            Thread.sleep(20)
            image = scene.render(nanoTime = millis * 1_000_000L)
        }
        val file = File(outDir, "$name.png")
        file.writeBytes(checkNotNull(image.encodeToData(EncodedImageFormat.PNG)).bytes)
        assertTrue(file.length() > 0, "empty snapshot for ${file.name}")
    }
}
