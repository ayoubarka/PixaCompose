package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.use
import com.pixamob.pixacompose.styles.PixaStyles
import com.pixamob.pixacompose.theme.PixaTheme
import com.sun.net.httpserver.HttpServer
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import java.net.InetSocketAddress
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Renders the image section into `showcase/build/snapshots/image/`, once from bytes and once
 * from a URL served by a local HTTP server, so the network path is covered without leaving
 * the machine.
 */
class ImageSnapshotTest {
    @Test
    fun loadsFromBytesAndFromUrl() {
        var hits = 0
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/sample.png") { exchange ->
            hits++
            exchange.responseHeaders.add("Content-Type", "image/png")
            exchange.sendResponseHeaders(200, SampleImage.size.toLong())
            exchange.responseBody.use { it.write(SampleImage) }
        }
        server.start()
        try {
            val url = "http://127.0.0.1:${server.address.port}/sample.png"
            val outDir = File("build/snapshots/image").apply { mkdirs() }
            for (style in PixaStyles.all) {
                for ((source, model) in listOf("bytes" to SampleImage, "url" to url)) {
                    ImageComposeScene(width = 840, height = 680, density = Density(2f)) {
                        PixaTheme(style = style, darkTheme = false) {
                            Column(
                                Modifier.fillMaxSize().background(PixaTheme.colors.background).padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) { ImageSection(model) }
                        }
                    }.use { scene ->
                        // Loading runs on real threads, so give it real time between frames.
                        var image = scene.render()
                        repeat(20) { frame ->
                            Thread.sleep(100)
                            image = scene.render(nanoTime = (frame + 1) * 100_000_000L)
                        }
                        val file = File(outDir, "${style.name.lowercase()}-$source.png")
                        file.writeBytes(checkNotNull(image.encodeToData(EncodedImageFormat.PNG)).bytes)
                        assertTrue(file.length() > 0, "empty snapshot for ${file.name}")
                    }
                }
            }
            assertTrue(hits > 0, "the URL was never requested")
        } finally {
            server.stop(0)
        }
    }
}
