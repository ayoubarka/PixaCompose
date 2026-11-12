package com.pixamob.pixacompose.showcase

import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.use
import com.pixamob.pixacompose.styles.PixaStyles
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Renders the showcase once per built-in style and theme into `showcase/build/snapshots/`.
 * Not a pixel comparison yet: it proves every style renders headlessly and gives images to review.
 */
class StyleSnapshotTest {
    @Test
    fun rendersEveryStyle() {
        val outDir = File("build/snapshots").apply { mkdirs() }
        for (style in PixaStyles.all) {
            for (dark in listOf(false, true)) {
                val file = File(outDir, "${style.name.lowercase()}-${if (dark) "dark" else "light"}.png")
                ImageComposeScene(width = 840, height = 1500, density = Density(2f)) {
                    ShowcaseApp(initialStyle = style, initialDark = dark)
                }.use { scene ->
                    val data = scene.render().encodeToData(EncodedImageFormat.PNG)
                    file.writeBytes(checkNotNull(data).bytes)
                }
                assertTrue(file.length() > 0, "empty snapshot for ${file.name}")
            }
        }
    }
}
