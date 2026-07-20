package com.pixamob.pixacompose.demo.display

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
import com.pixamob.pixacompose.components.display.AvatarStatusPosition
import com.pixamob.pixacompose.components.display.PixaAvatar
import com.pixamob.pixacompose.components.display.PixaAvatarGroup
import com.pixamob.pixacompose.components.feedback.BadgeVariant
import com.pixamob.pixacompose.components.feedback.PixaHintBadge
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
        ShowcaseSection("Text Avatars") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaAvatar(text = "JD", size = SizeVariant.Small)
                PixaAvatar(text = "AK", size = SizeVariant.Medium)
                PixaAvatar(text = "ML", size = SizeVariant.Large)
                PixaAvatar(text = "RS", size = SizeVariant.Huge)
            }
        }

        ShowcaseSection("Sizes") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaAvatar(text = "XS", size = SizeVariant.Nano)
                PixaAvatar(text = "CP", size = SizeVariant.Compact)
                PixaAvatar(text = "SM", size = SizeVariant.Small)
                PixaAvatar(text = "MD", size = SizeVariant.Medium)
                PixaAvatar(text = "LG", size = SizeVariant.Large)
                PixaAvatar(text = "HG", size = SizeVariant.Huge)
            }
        }

        ShowcaseSection("With Status Badge") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaAvatar(
                    text = "JD", size = SizeVariant.Medium,
                    statusBadge = { PixaHintBadge(variant = BadgeVariant.Success) },
                    statusPosition = AvatarStatusPosition.BottomRight
                )
                PixaAvatar(
                    text = "AK", size = SizeVariant.Medium,
                    statusBadge = { PixaHintBadge(variant = BadgeVariant.Error) },
                    statusPosition = AvatarStatusPosition.TopRight
                )
            }
        }

        ShowcaseSection("Group") {
            PixaAvatarGroup(
                avatars = listOf(
                    AvatarData(text = "JD"),
                    AvatarData(text = "AK"),
                    AvatarData(text = "ML"),
                    AvatarData(text = "RS")
                ),
                maxVisible = 3
            )
        }

        ShowcaseSection("Loading State") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaAvatar(text = "JD", isLoading = true, size = SizeVariant.Medium)
                PixaAvatar(text = "AK", isLoading = true, size = SizeVariant.Medium)
            }
        }
    }
}
