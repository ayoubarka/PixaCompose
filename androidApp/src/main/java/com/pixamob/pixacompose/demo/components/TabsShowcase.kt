package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.ChipType
import com.pixamob.pixacompose.components.actions.ChipVariant
import com.pixamob.pixacompose.components.actions.PixaChip
import com.pixamob.pixacompose.components.actions.ScrollableTabs
import com.pixamob.pixacompose.components.actions.SegmentedTabs
import com.pixamob.pixacompose.components.actions.TabContent
import com.pixamob.pixacompose.components.actions.TabContentStyle
import com.pixamob.pixacompose.components.actions.TabIconMode
import com.pixamob.pixacompose.components.actions.TabItem
import com.pixamob.pixacompose.components.actions.Tabs
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun TabsShowcase() {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedSegmented by remember { mutableStateOf(0) }
    var selectedScrollable by remember { mutableStateOf(0) }
    var iconMode by remember { mutableStateOf(TabIconMode.Both) }
    var alignmentName by remember { mutableStateOf("Center") }
    val alignment = when (alignmentName) {
        "Start" -> Alignment.CenterStart
        "End" -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Fixed Tabs") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TabIconMode.entries.forEach { mode ->
                        PixaChip(
                            text = mode.name,
                            type = ChipType.Selectable,
                            variant = ChipVariant.Outlined,
                            size = SizeVariant.Small,
                            selected = iconMode == mode,
                            onClick = { iconMode = mode }
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Start", "Center", "End").forEach { label ->
                        PixaChip(
                            text = label,
                            type = ChipType.Selectable,
                            variant = ChipVariant.Tonal,
                            size = SizeVariant.Small,
                            selected = alignmentName == label,
                            onClick = { alignmentName = label }
                        )
                    }
                }
                Tabs(
                    selectedTabIndex = selectedTab,
                    tabs = listOf(
                        TabItem(TabContent.TextWithIcons("Home", leadingIcon = Icons.Default.Home)),
                        TabItem(TabContent.TextWithIcons("Favs", leadingIcon = Icons.Default.Favorite)),
                        TabItem(TabContent.TextWithIcons("Search", leadingIcon = Icons.Default.Search))
                    ),
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.fillMaxWidth(),
                    customContentStyle = TabContentStyle(
                        iconMode = iconMode,
                        contentAlignment = alignment
                    )
                )
                Text(
                    text = "Selected: $selectedTab, Icon: ${iconMode.name}, Align: $alignmentName",
                    style = AppTheme.typography.bodyRegular,
                    color = AppTheme.colors.baseContentBody
                )
            }
        }

        ShowcaseSection("Segmented Tabs") {
            SegmentedTabs(
                selectedTabIndex = selectedSegmented,
                tabs = listOf(
                    TabItem(TabContent.Text("Day")),
                    TabItem(TabContent.Text("Week")),
                    TabItem(TabContent.Text("Month"))
                ),
                onTabSelected = { selectedSegmented = it }
            )
            Text(
                text = "Selected: $selectedSegmented",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )
        }

        ShowcaseSection("Scrollable Tabs") {
            ScrollableTabs(
                selectedTabIndex = selectedScrollable,
                tabs = listOf(
                    TabItem(TabContent.Text("All")),
                    TabItem(TabContent.Text("Technology")),
                    TabItem(TabContent.Text("Business")),
                    TabItem(TabContent.Text("Entertainment")),
                    TabItem(TabContent.Text("Sports"))
                ),
                onTabSelected = { selectedScrollable = it }
            )
            Text(
                text = "Selected: $selectedScrollable",
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentBody
            )
        }
    }
}
