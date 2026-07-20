package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.IconSource
import com.pixamob.pixacompose.components.display.PixaActionCard
import com.pixamob.pixacompose.components.display.PixaCardMedia
import com.pixamob.pixacompose.components.display.PixaContentCard
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.components.display.PixaImageSource
import com.pixamob.pixacompose.components.display.PixaProductCard
import com.pixamob.pixacompose.components.display.PixaSelectCard
import com.pixamob.pixacompose.components.display.PixaStatCard
import com.pixamob.pixacompose.components.display.PixaTaskCard
import com.pixamob.pixacompose.components.surfaces.BaseCardVariant
import com.pixamob.pixacompose.components.surfaces.PixaCard
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun CardShowcase() {
    var selectedOption by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("PixaCard — legacy base variants") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                listOf(
                    BaseCardVariant.Elevated to "Elevated",
                    BaseCardVariant.Outlined to "Outlined",
                    BaseCardVariant.Filled to "Filled",
                    BaseCardVariant.Tonal to "Tonal",
                    BaseCardVariant.Ghost to "Ghost"
                ).forEach { (variant, label) ->
                    PixaCard(
                        modifier = Modifier.width(80.dp).height(72.dp),
                        variant = variant,
                        padding = SizeVariant.Compact
                    ) {
                        BasicText(
                            text = label,
                            style = AppTheme.typography.captionRegular.copy(
                                color = AppTheme.colors.baseContentBody
                            )
                        )
                    }
                }
            }
        }

        ShowcaseSection("PixaContentCard — base anatomy") {
            PixaContentCard(
                modifier = Modifier.fillMaxWidth(),
                leading = {
                    PixaIcon(
                        source = IconSource.Vector(Icons.Default.Person),
                        contentDescription = null,
                        customSize = HierarchicalSize.Icon.Medium
                    )
                },
                title = "Card title",
                subtitle = "Supporting subtitle",
                body = "Body copy sits below the header row and wraps up to the configured max lines.",
                metadata = listOf("Author name", "2h ago"),
                onClick = {}
            )
        }

        ShowcaseSection("PixaProductCard") {
            PixaProductCard(
                media = PixaCardMedia(
                    source = PixaImageSource.Vector(Icons.Default.Image),
                    contentDescription = "Product photo"
                ),
                title = "Wireless Headphones",
                metadata = listOf("$79.99", "4.6"),
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("PixaTaskCard") {
            PixaTaskCard(
                title = "Design Review",
                metadata = listOf("Due Fri", "2 comments"),
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            )
        }

        ShowcaseSection("PixaStatCard") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaStatCard(
                    value = "42",
                    label = "Active projects",
                    modifier = Modifier.width(150.dp)
                )
                PixaStatCard(
                    value = "85%",
                    label = "Completion",
                    modifier = Modifier.width(150.dp)
                )
            }
        }

        ShowcaseSection("PixaSelectCard") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Option A", "Option B", "Option C").forEachIndexed { index, label ->
                    PixaSelectCard(
                        title = label,
                        isSelected = selectedOption == index,
                        onClick = { selectedOption = index },
                        modifier = Modifier.width(110.dp)
                    )
                }
            }
        }

        ShowcaseSection("PixaActionCard — navigation row") {
            PixaActionCard(
                title = "Notifications",
                subtitle = "Push, email, SMS",
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            )
        }

        ShowcaseSection("Loading state") {
            PixaContentCard(
                modifier = Modifier.fillMaxWidth(),
                isLoading = true
            )
        }
    }
}
