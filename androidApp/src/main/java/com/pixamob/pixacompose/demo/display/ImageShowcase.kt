package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaImage
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun ImageShowcase() {
    ShowcaseScreen {
        ShowcaseSection("With URL") {
            PixaImage(
                url = "https://picsum.photos/400/200",
                contentDescription = "Random image",
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
        }
    }
}
