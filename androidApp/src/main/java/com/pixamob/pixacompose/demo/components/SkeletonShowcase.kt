package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.feedback.SkeletonButton
import com.pixamob.pixacompose.components.feedback.SkeletonCard
import com.pixamob.pixacompose.components.feedback.SkeletonCircle
import com.pixamob.pixacompose.components.feedback.SkeletonListItem
import com.pixamob.pixacompose.components.feedback.SkeletonText
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun SkeletonShowcase() {
    var shimmerEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Primitives") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                SkeletonCircle(size = 48.dp, shimmerEnabled = shimmerEnabled)
                SkeletonText(width = 120.dp, shimmerEnabled = shimmerEnabled)
                SkeletonButton(width = 80.dp, shimmerEnabled = shimmerEnabled)
            }
        }

        ShowcaseSection("Card") {
            SkeletonCard(shimmerEnabled = shimmerEnabled, modifier = Modifier.fillMaxWidth())
        }

        ShowcaseSection("List Item") {
            SkeletonListItem(showAvatar = true, shimmerEnabled = shimmerEnabled)
        }

        ShowcaseSection("Toggle Shimmer") {
            PixaButton(
                text = if (shimmerEnabled) "Disable Shimmer" else "Enable Shimmer",
                onClick = { shimmerEnabled = !shimmerEnabled }
            )
        }
    }
}
