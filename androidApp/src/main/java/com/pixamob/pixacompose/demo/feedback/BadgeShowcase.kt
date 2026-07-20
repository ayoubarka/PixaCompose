package com.pixamob.pixacompose.demo.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.feedback.BadgeVariant
import com.pixamob.pixacompose.components.feedback.BadgedBox
import com.pixamob.pixacompose.components.feedback.PixaHintBadge
import com.pixamob.pixacompose.components.feedback.PixaNotificationBadge
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun BadgeShowcase() {
    ShowcaseScreen {
        ShowcaseSection("Notification Badge") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaNotificationBadge(count = 3, variant = BadgeVariant.Error)
                PixaNotificationBadge(count = 99, variant = BadgeVariant.Error)
                PixaNotificationBadge(count = 150, variant = BadgeVariant.Error)
            }
        }

        ShowcaseSection("Variants") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaNotificationBadge(count = 1, variant = BadgeVariant.Error)
                PixaNotificationBadge(count = 2, variant = BadgeVariant.Success)
                PixaNotificationBadge(count = 3, variant = BadgeVariant.Warning)
                PixaNotificationBadge(count = 4, variant = BadgeVariant.Accent)
                PixaNotificationBadge(count = 5, variant = BadgeVariant.OnBrand)
            }
        }

        ShowcaseSection("Hint Badge") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaHintBadge(variant = BadgeVariant.Error)
                PixaHintBadge(variant = BadgeVariant.Success)
                PixaHintBadge(variant = BadgeVariant.Warning)
                PixaHintBadge(variant = BadgeVariant.Accent)
            }
        }

        ShowcaseSection("BadgedBox") {
            BadgedBox(badge = { PixaNotificationBadge(count = 3) }) {
                androidx.compose.foundation.text.BasicText("Notifications")
            }
        }
    }
}
