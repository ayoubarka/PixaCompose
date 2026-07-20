package com.pixamob.pixacompose.demo.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize

@Composable
fun ThemeShowcase() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Brand Colors") {
            ColorSwatches(
                colors = listOf(
                    "Brand Surface Default" to AppTheme.colors.brandSurfaceDefault,
                    "Brand Surface Subtle" to AppTheme.colors.brandSurfaceSubtle,
                    "Brand Border Default" to AppTheme.colors.brandBorderDefault,
                    "Brand Border Subtle" to AppTheme.colors.brandBorderSubtle,
                    "Brand Content Default" to AppTheme.colors.brandContentDefault,
                    "Brand Content Subtle" to AppTheme.colors.brandContentSubtle,
                    "Brand Surface Focus" to AppTheme.colors.brandSurfaceFocus,
                    "Brand Content Focus" to AppTheme.colors.brandContentFocus
                )
            )
        }

        ShowcaseSection("Base Surface Colors") {
            ColorSwatches(
                colors = listOf(
                    "Base Surface Default" to AppTheme.colors.baseSurfaceDefault,
                    "Base Surface Subtle" to AppTheme.colors.baseSurfaceSubtle,
                    "Base Surface Elevated" to AppTheme.colors.baseSurfaceElevated,
                    "Base Surface Focus" to AppTheme.colors.baseSurfaceFocus,
                    "Base Surface Disabled" to AppTheme.colors.baseSurfaceDisabled
                )
            )
        }

        ShowcaseSection("Base Content Colors") {
            ColorSwatches(
                colors = listOf(
                    "Base Content Title" to AppTheme.colors.baseContentTitle,
                    "Base Content Subtitle" to AppTheme.colors.baseContentSubtitle,
                    "Base Content Body" to AppTheme.colors.baseContentBody,
                    "Base Content Caption" to AppTheme.colors.baseContentCaption,
                    "Base Content Hint" to AppTheme.colors.baseContentHint,
                    "Base Content Negative" to AppTheme.colors.baseContentNegative,
                    "Base Content Disabled" to AppTheme.colors.baseContentDisabled
                )
            )
        }

        ShowcaseSection("Semantic Colors") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SemanticGroup(
                    label = "Success",
                    surface = AppTheme.colors.successSurfaceDefault,
                    border = AppTheme.colors.successBorderDefault,
                    content = AppTheme.colors.successContentDefault
                )
                SemanticGroup(
                    label = "Warning",
                    surface = AppTheme.colors.warningSurfaceDefault,
                    border = AppTheme.colors.warningBorderDefault,
                    content = AppTheme.colors.warningContentDefault
                )
                SemanticGroup(
                    label = "Error",
                    surface = AppTheme.colors.errorSurfaceDefault,
                    border = AppTheme.colors.errorBorderDefault,
                    content = AppTheme.colors.errorContentDefault
                )
                SemanticGroup(
                    label = "Info",
                    surface = AppTheme.colors.infoSurfaceDefault,
                    border = AppTheme.colors.infoBorderDefault,
                    content = AppTheme.colors.infoContentDefault
                )
            }
        }

        ShowcaseSection("Typography") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TypographySample("displayLarge", AppTheme.typography.displayLarge, "The quick brown fox")
                TypographySample("displayMedium", AppTheme.typography.displayMedium, "The quick brown fox")
                TypographySample("displaySmall", AppTheme.typography.displaySmall, "The quick brown fox")
                TypographySample("headerBold", AppTheme.typography.headerBold, "The quick brown fox")
                TypographySample("headlineBold", AppTheme.typography.headlineBold, "The quick brown fox")
                TypographySample("titleBold", AppTheme.typography.titleBold, "The quick brown fox")
                TypographySample("titleRegular", AppTheme.typography.titleRegular, "The quick brown fox")
                TypographySample("subtitleRegular", AppTheme.typography.subtitleRegular, "The quick brown fox")
                TypographySample("bodyRegular", AppTheme.typography.bodyRegular, "Body text at 16sp — the anchor size")
                TypographySample("captionRegular", AppTheme.typography.captionRegular, "Caption text")
                TypographySample("labelMedium", AppTheme.typography.labelMedium, "Label medium")
                TypographySample("footnoteRegular", AppTheme.typography.footnoteRegular, "Footnote text")
            }
        }

        ShowcaseSection("Spacing Tiers") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SpacingSample("Nano", HierarchicalSize.Spacing.Nano)
                SpacingSample("Compact", HierarchicalSize.Spacing.Compact)
                SpacingSample("Small", HierarchicalSize.Spacing.Small)
                SpacingSample("Medium", HierarchicalSize.Spacing.Medium)
                SpacingSample("Large", HierarchicalSize.Spacing.Large)
                SpacingSample("Huge", HierarchicalSize.Spacing.Huge)
                SpacingSample("Massive", HierarchicalSize.Spacing.Massive)
            }
        }
    }
}

@Composable
private fun ColorSwatches(colors: List<Pair<String, Color>>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        colors.forEach { (name, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(color)
                        .border(0.5.dp, AppTheme.colors.baseBorderSubtle, RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                BasicText(
                    text = name,
                    style = AppTheme.typography.captionRegular.copy(
                        color = AppTheme.colors.baseContentBody
                    )
                )
            }
        }
    }
}

@Composable
private fun SemanticGroup(label: String, surface: Color, border: Color, content: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(surface)
                .border(1.dp, border, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = "Aa",
                style = AppTheme.typography.captionBold.copy(
                    color = content,
                    textAlign = TextAlign.Center
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        BasicText(
            text = label,
            style = AppTheme.typography.captionRegular.copy(
                color = AppTheme.colors.baseContentCaption
            )
        )
    }
}

@Composable
private fun TypographySample(label: String, style: androidx.compose.ui.text.TextStyle, sample: String) {
    Column {
        BasicText(
            text = label,
            style = AppTheme.typography.captionRegular.copy(
                color = AppTheme.colors.baseContentCaption
            )
        )
        BasicText(
            text = sample,
            style = style.copy(color = AppTheme.colors.baseContentTitle)
        )
    }
}

@Composable
private fun SpacingSample(label: String, dp: Dp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BasicText(
            text = "$label ($dp)",
            style = AppTheme.typography.captionRegular.copy(
                color = AppTheme.colors.baseContentCaption
            ),
            modifier = Modifier.width(120.dp)
        )
        Box(
            modifier = Modifier
                .height(12.dp)
                .width(dp)
                .clip(RoundedCornerShape(2.dp))
                .background(AppTheme.colors.brandContentDefault)
        )
    }
}
