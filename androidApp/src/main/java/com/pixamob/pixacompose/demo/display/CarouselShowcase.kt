package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaCarousel
import com.pixamob.pixacompose.components.display.PixaCarouselItem
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun CarouselShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Carousel") {
            PixaCarousel(
                items = listOf(
                    PixaCarouselItem(content = { BasicText("Slide 1", style = AppTheme.typography.bodyRegular) }, title = "Slide 1"),
                    PixaCarouselItem(content = { BasicText("Slide 2", style = AppTheme.typography.bodyRegular) }, title = "Slide 2"),
                    PixaCarouselItem(content = { BasicText("Slide 3", style = AppTheme.typography.bodyRegular) }, title = "Slide 3")
                ),
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
        }
    }
}
