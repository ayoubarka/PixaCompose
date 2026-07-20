package com.pixamob.pixacompose.demo.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaTab
import com.pixamob.pixacompose.components.actions.SegmentedTabs
import com.pixamob.pixacompose.components.actions.TabContent
import com.pixamob.pixacompose.components.actions.TabIndicatorStyle
import com.pixamob.pixacompose.components.actions.TabItem
import com.pixamob.pixacompose.components.actions.TabShape
import com.pixamob.pixacompose.components.actions.Tabs
import com.pixamob.pixacompose.components.actions.VerticalTabs
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.SizeVariant
import com.pixamob.pixacompose.utils.pixaRipple

@Composable
fun TabShowcase() {
    var primaryTab by remember { mutableStateOf(0) }
    var segmentedTab by remember { mutableStateOf(0) }
    var verticalTab by remember { mutableStateOf(0) }
    var iconTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Primary Tabs") {
            Tabs(
                selectedTabIndex = primaryTab,
                tabs = listOf(
                    TabItem(content = TabContent.Text("Home")),
                    TabItem(content = TabContent.Text("Profile")),
                    TabItem(content = TabContent.Text("Settings")),
                    TabItem(content = TabContent.Text("Favorites"))
                ),
                onTabSelected = { primaryTab = it },
                modifier = Modifier.fillMaxWidth()
            )
            BasicText(
                text = "Selected tab: ${listOf("Home", "Profile", "Settings", "Favorites")[primaryTab]}",
                style = AppTheme.typography.captionRegular.copy(
                    color = AppTheme.colors.baseContentCaption
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        ShowcaseSection("Segmented Tabs") {
            SegmentedTabs(
                selectedTabIndex = segmentedTab,
                tabs = listOf(
                    TabItem(content = TabContent.Text("Day")),
                    TabItem(content = TabContent.Text("Week")),
                    TabItem(content = TabContent.Text("Month"))
                ),
                onTabSelected = { segmentedTab = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("Vertical Tabs") {
            VerticalTabs(
                selectedTabIndex = verticalTab,
                tabs = listOf(
                    TabItem(content = TabContent.Text("General")),
                    TabItem(content = TabContent.Text("Security")),
                    TabItem(content = TabContent.Text("Privacy"))
                ),
                onTabSelected = { verticalTab = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("Icon Tabs") {
            Tabs(
                selectedTabIndex = iconTab,
                tabs = listOf(
                    TabItem(content = TabContent.Icon(Icons.Default.Home)),
                    TabItem(content = TabContent.Icon(Icons.Default.Favorite)),
                    TabItem(content = TabContent.Icon(Icons.Default.Settings))
                ),
                onTabSelected = { iconTab = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        ShowcaseSection("With Badges") {
            Tabs(
                selectedTabIndex = 0,
                tabs = listOf(
                    TabItem(content = TabContent.Text("Inbox"), badge = "3"),
                    TabItem(content = TabContent.Text("Sent")),
                    TabItem(content = TabContent.Text("Spam"), badge = "12")
                ),
                onTabSelected = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
