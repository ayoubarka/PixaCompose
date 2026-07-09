package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.BaseCardVariant
import com.pixamob.pixacompose.components.display.PixaCard
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun CardShowcase() {
    var clickCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                listOf(
                    BaseCardVariant.Elevated to "Elevated",
                    BaseCardVariant.Outlined to "Outlined",
                    BaseCardVariant.Filled to "Filled",
                    BaseCardVariant.Tonal to "Tonal",
                    BaseCardVariant.Ghost to "Ghost"
                ).forEach { (variant, label) ->
                    PixaCard(
                        modifier = Modifier.width(120.dp).height(80.dp),
                        variant = variant,
                        padding = SizeVariant.Compact
                    ) {
                        Text(
                            text = label,
                            style = AppTheme.typography.captionRegular,
                            color = AppTheme.colors.baseContentBody
                        )
                    }
                }
            }
        }

        ShowcaseSection("Loading") {
            PixaCard(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                variant = BaseCardVariant.Elevated,
                isLoading = true
            ) {
            }
        }

        ShowcaseSection("Interactive") {
            PixaCard(
                modifier = Modifier.fillMaxWidth(),
                variant = BaseCardVariant.Elevated,
                onClick = { clickCount++ }
            ) {
                Text(
                    text = "Tapped $clickCount times",
                    style = AppTheme.typography.bodyRegular,
                    color = AppTheme.colors.baseContentBody
                )
            }
        }
    }
}
