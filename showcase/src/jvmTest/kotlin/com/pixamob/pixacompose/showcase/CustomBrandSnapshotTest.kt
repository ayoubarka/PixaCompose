package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.use
import com.pixamob.pixacompose.styles.PixaStyles
import com.pixamob.pixacompose.theme.PixaStyle
import com.pixamob.pixacompose.theme.withFontFamily
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * A style written the way an app would write it: start from a built-in style and change the
 * palette, the fonts and the corner radii through the public API only.
 */
private fun brandStyle(): PixaStyle {
    val base = PixaStyles.Minimal
    val green = Color(0xFF1F9D7A)
    return base.copy(
        name = "Brand",
        lightColors = base.lightColors.copy(
            background = Color(0xFFF6F3EC),
            card = Color(0xFFFFFDF8),
            primary = green,
            primaryForeground = Color.White,
            secondary = Color(0xFFFFD9C7),
            secondaryForeground = Color(0xFF5A2A14),
            accent = Color(0xFFDFF3EC),
            accentForeground = Color(0xFF0F5A45),
            ring = green,
        ),
        darkColors = base.darkColors.copy(
            primary = Color(0xFF4FD1AE),
            primaryForeground = Color(0xFF05241C),
            ring = Color(0xFF4FD1AE),
        ),
        shapes = base.shapes.copy(
            sm = RoundedCornerShape(10.dp),
            md = RoundedCornerShape(16.dp),
            lg = RoundedCornerShape(24.dp),
        ),
    ).withFontFamily(body = FontFamily.Serif, heading = FontFamily.Monospace)
}

/** Renders the showcase in an app-defined style into `showcase/build/snapshots/brand/`. */
class CustomBrandSnapshotTest {
    @Test
    fun rendersAnAppDefinedStyle() {
        val outDir = File("build/snapshots/brand").apply { mkdirs() }
        for (dark in listOf(false, true)) {
            ImageComposeScene(width = 840, height = 1600, density = Density(2f)) {
                ShowcaseApp(initialStyle = brandStyle(), initialDark = dark, focusDemo = FocusRequester())
            }.use { scene ->
                scene.render()
                val image = scene.render(nanoTime = 1_000_000_000)
                val file = File(outDir, "brand-${if (dark) "dark" else "light"}.png")
                file.writeBytes(checkNotNull(image.encodeToData(EncodedImageFormat.PNG)).bytes)
                assertTrue(file.length() > 0, "empty snapshot for ${file.name}")
            }
        }
    }
}
