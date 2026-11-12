package com.pixamob.pixacompose.showcase

import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.Density
import androidx.compose.ui.use
import com.pixamob.pixacompose.styles.PixaStyles
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Surface
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

private const val Width = 840
private const val PageHeight = 1600
private const val Pages = 5

/**
 * Renders the showcase once per built-in style and theme into `showcase/build/snapshots/pages/`,
 * cut into pages so each image stays readable. Not a pixel comparison yet: it proves every
 * style renders headlessly and gives images to review.
 */
class StyleSnapshotTest {
    @Test
    fun rendersEveryStyle() {
        val outDir = File("build/snapshots/pages").apply { deleteRecursively(); mkdirs() }
        for (style in PixaStyles.all) {
            for (dark in listOf(false, true)) {
                val name = "${style.name.lowercase()}-${if (dark) "dark" else "light"}"
                val focus = FocusRequester()
                ImageComposeScene(width = Width, height = PageHeight * Pages, density = Density(2f)) {
                    ShowcaseApp(initialStyle = style, initialDark = dark, focusDemo = focus)
                }.use { scene ->
                    scene.render()
                    focus.requestFocus()
                    val full = scene.render(nanoTime = 1_000_000_000)
                    repeat(Pages) { page ->
                        val surface = Surface.makeRasterN32Premul(Width, PageHeight)
                        surface.canvas.drawImage(full, 0f, -(page * PageHeight).toFloat())
                        val data = surface.makeImageSnapshot().encodeToData(EncodedImageFormat.PNG)
                        val file = File(outDir, "$name-${page + 1}.png")
                        file.writeBytes(checkNotNull(data).bytes)
                        assertTrue(file.length() > 0, "empty snapshot for ${file.name}")
                    }
                }
            }
        }
    }
}
