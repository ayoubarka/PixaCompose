package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.AvatarData
import com.pixamob.pixacompose.components.display.AvatarShape
import com.pixamob.pixacompose.components.display.PixaAvatar
import com.pixamob.pixacompose.components.display.PixaAvatarGroup
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun AvatarShowcase() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Sizes") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaAvatar(text = "A", size = SizeVariant.Compact)
                PixaAvatar(text = "B", size = SizeVariant.Small)
                PixaAvatar(text = "C", size = SizeVariant.Medium)
                PixaAvatar(text = "D", size = SizeVariant.Large)
            }
        }

        ShowcaseSection("Shapes") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaAvatar(text = "A", shape = AvatarShape.Circle)
                PixaAvatar(text = "B", shape = AvatarShape.Rounded)
                PixaAvatar(text = "C", shape = AvatarShape.Square)
            }
        }

        ShowcaseSection("States") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaAvatar(text = "JD", isLoading = true)
                PixaAvatar(text = "KL", onClick = {})
            }
        }

        ShowcaseSection("Group") {
            PixaAvatarGroup(
                avatars = listOf(
                    AvatarData(text = "AB"),
                    AvatarData(text = "CD"),
                    AvatarData(text = "EF"),
                    AvatarData(text = "GH"),
                    AvatarData(text = "IJ")
                ),
                maxVisible = 4,
                size = SizeVariant.Small
            )
        }
    }
}
