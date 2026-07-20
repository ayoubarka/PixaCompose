package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pixamob.pixacompose.components.display.BannerBackground
import com.pixamob.pixacompose.components.display.PixaBanner
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun BannerShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Banner") {
            PixaBanner(
                headline = "Special Offer",
                actionLabel = "Shop Now",
                onActionClick = {},
                background = BannerBackground.Color(background = Color(0xFF1E293B), content = Color.White),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
