package com.pixamob.pixacompose.demo.actions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.pixamob.pixacompose.components.actions.PixaSlidingButton
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun SlidingButtonShowcase() {
    var completed by remember { mutableStateOf(false) }

    ShowcaseScreen {
        ShowcaseSection("Sliding Button") {
            PixaSlidingButton(
                label = "Slide to confirm",
                arrowIcon = rememberVectorPainter(Icons.Default.ArrowForward),
                onSlideComplete = { completed = true },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
