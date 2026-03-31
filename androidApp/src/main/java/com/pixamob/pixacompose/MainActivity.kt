package com.pixamob.pixacompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.theme.PixaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Optional: Setup custom ImageLoader with explicit SVG decoder
        // By default, Coil 3.3.0+ automatically detects SVGs
        // setupCoilImageLoader()

        setContent {
            PixaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                   // PixaComposeDemo()

                   // Example: SVG Image from composeResources/files/
                   // Coil automatically detects and decodes SVGs
                   // PixaImage(
                   //     source = PixaImageSource.SvgFile("icons/faces/icon.svg"),
                   //     contentDescription = "Icon",
                   //     modifier = Modifier.size(48.dp),
                   //     tint = Color.Blue
                   // )
                }
            }
        }
    }

    // Optional: Explicitly add SvgDecoder to ImageLoader
    // Not required as Coil 3.3.0+ auto-detects SVGs by default
    // private fun setupCoilImageLoader() {
    //     val imageLoader = ImageLoader.Builder(this)
    //         .components { add(SvgDecoder.Factory()) }
    //         .build()
    //
    //     Coil.setImageLoader(imageLoader)
    // }
}
