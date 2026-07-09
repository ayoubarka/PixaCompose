package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.feedback.BadgedBox
import com.pixamob.pixacompose.components.feedback.BadgeStyle
import com.pixamob.pixacompose.components.feedback.BadgeVariant
import com.pixamob.pixacompose.components.feedback.PixaBadge
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun BadgeShowcase() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaBadge(content = "3", variant = BadgeVariant.Primary)
                PixaBadge(content = "5", variant = BadgeVariant.Success)
                PixaBadge(content = "2", variant = BadgeVariant.Warning)
                PixaBadge(content = "1", variant = BadgeVariant.Error)
                PixaBadge(content = "!", variant = BadgeVariant.Neutral)
                PixaBadge(content = "7", variant = BadgeVariant.Info)
            }
        }

        ShowcaseSection("Styles") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaBadge(content = "Solid", variant = BadgeVariant.Primary, style = BadgeStyle.Filled)
                PixaBadge(content = "Outlined", variant = BadgeVariant.Primary, style = BadgeStyle.Outlined)
                PixaBadge(content = "Subtle", variant = BadgeVariant.Primary, style = BadgeStyle.Subtle)
            }
        }

        ShowcaseSection("Sizes") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaBadge(content = "1", size = SizeVariant.Small, variant = BadgeVariant.Error)
                PixaBadge(content = "5", size = SizeVariant.Medium, variant = BadgeVariant.Error)
                PixaBadge(content = "99", size = SizeVariant.Large, variant = BadgeVariant.Error)
            }
        }

        ShowcaseSection("With Dot") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaBadge(variant = BadgeVariant.Error, dot = true)
                PixaBadge(variant = BadgeVariant.Success, dot = true)
                PixaBadge(variant = BadgeVariant.Warning, dot = true, pulse = true)
            }
        }

        ShowcaseSection("Badged Box") {
            BadgedBox(badge = { PixaBadge(content = "5", variant = BadgeVariant.Error) }) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.Gray))
            }
        }
    }
}
