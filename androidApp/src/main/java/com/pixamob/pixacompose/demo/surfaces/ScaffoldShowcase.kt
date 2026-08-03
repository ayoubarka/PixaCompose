package com.pixamob.pixacompose.demo.surfaces

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaButton
import com.pixamob.pixacompose.components.surfaces.PixaAdaptiveScaffold
import com.pixamob.pixacompose.components.surfaces.PixaScaffold
import com.pixamob.pixacompose.components.surfaces.PixaSimpleScaffold
import com.pixamob.pixacompose.components.surfaces.ScaffoldFabPosition
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

/** Simple mock bar used across this showcase to stand in for real navigation components. */
@Composable
private fun MockBar(label: String, height: androidx.compose.ui.unit.Dp = 48.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(AppTheme.colors.baseSurfaceElevated),
        contentAlignment = Alignment.Center
    ) {
        BasicText(text = label, style = AppTheme.typography.labelMedium.copy(color = AppTheme.colors.baseContentBody))
    }
}

@Composable
private fun MockFab(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(AppTheme.colors.brandContentDefault)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        BasicText(text = "+", style = AppTheme.typography.titleBold.copy(color = AppTheme.colors.baseSurfaceDefault))
    }
}

@Composable
private fun MockRail(selected: Int, onSelect: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .background(AppTheme.colors.baseSurfaceElevated)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (index == selected) AppTheme.colors.brandContentDefault else AppTheme.colors.baseSurfaceSubtle)
                    .clickable { onSelect(index) }
            )
        }
    }
}

@Composable
fun ScaffoldShowcase() {
    var demoFabClicks by remember { mutableIntStateOf(0) }
    var railSelected by remember { mutableIntStateOf(0) }
    var showFullScaffold by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        ShowcaseSection("Basic — top bar, bottom bar, floating content") {
            Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                PixaScaffold(
                    topBar = { MockBar("Top Bar") },
                    bottomBar = { MockBar("Bottom Bar") },
                    floatingContent = { MockFab(onClick = { demoFabClicks++ }) },
                    fabPosition = ScaffoldFabPosition.End
                ) { contentPadding ->
                    ScaffoldFeed(contentPadding, "FAB clicks: $demoFabClicks")
                }
            }
        }

        ShowcaseSection("Adaptive — rail on wide viewports, bottom bar on compact") {
            Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                PixaAdaptiveScaffold(
                    navigation = { MockRail(selected = railSelected, onSelect = { railSelected = it }) },
                    topBar = { MockBar("Adaptive Scaffold") }
                ) { contentPadding ->
                    ScaffoldFeed(contentPadding, "Rail selection: $railSelected")
                }
            }
        }

        ShowcaseSection("Simple — top/bottom bar with padding applied for you") {
            Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                PixaSimpleScaffold(
                    topBar = { MockBar("Settings") }
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        BasicText(
                            text = "Content root already padded — no contentPadding lambda to wire.",
                            style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody)
                        )
                    }
                }
            }
        }

        ShowcaseSection("Full-screen preview") {
            PixaButton(text = "Open full-screen scaffold", onClick = { showFullScaffold = true })
        }
    }

    if (showFullScaffold) {
        PixaScaffold(
            topBar = { MockBar("Home", height = 56.dp) },
            bottomBar = { MockBar("Nav", height = 56.dp) },
            floatingContent = { MockFab(onClick = { showFullScaffold = false }) },
            fabPosition = ScaffoldFabPosition.End
        ) { contentPadding ->
            ScaffoldFeed(contentPadding, "Tap the FAB to close")
        }
    }
}

@Composable
private fun ScaffoldFeed(contentPadding: PaddingValues, note: String) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = contentPadding
    ) {
        item {
            BasicText(
                text = note,
                style = AppTheme.typography.captionRegular.copy(color = AppTheme.colors.baseContentHint),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(6) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                BasicText(
                    text = "Item $index",
                    style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody)
                )
            }
        }
    }
}
