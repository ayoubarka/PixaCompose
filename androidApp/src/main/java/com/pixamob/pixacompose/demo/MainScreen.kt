package com.pixamob.pixacompose.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.IconButtonVariant
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.components.actions.TabContent
import com.pixamob.pixacompose.components.actions.TabItem
import com.pixamob.pixacompose.components.actions.Tabs
import com.pixamob.pixacompose.components.display.IconTone
import com.pixamob.pixacompose.components.display.PixaActionCard
import com.pixamob.pixacompose.components.display.PixaIcon
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun MainScreen(onComponentClick: (ComponentEntry) -> Unit) {
    val categories = ComponentCategory.entries
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val toggleTheme = LocalThemeToggle.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(AppTheme.colors.baseSurfaceDefault)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.colors.baseSurfaceDefault)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(
                text = "PixaCompose",
                style = AppTheme.typography.titleBold.copy(
                    color = AppTheme.colors.baseContentTitle
                ),
                modifier = Modifier.weight(1f)
            )

            val themeIconPainter = if (AppTheme.isDarkTheme) {
                rememberVectorPainter(Icons.Default.LightMode)
            } else {
                rememberVectorPainter(Icons.Default.DarkMode)
            }
            PixaIconButton(
                icon = themeIconPainter,
                onClick = toggleTheme,
                variant = IconButtonVariant.Ghost,
                contentDescription = if (AppTheme.isDarkTheme) "Switch to light" else "Switch to dark"
            )
        }

        Tabs(
            selectedTabIndex = selectedCategoryIndex,
            tabs = categories.map { category ->
                TabItem(content = TabContent.Text(category.displayName))
            },
            onTabSelected = { selectedCategoryIndex = it },
            size = SizeVariant.Compact,
            modifier = Modifier.fillMaxWidth()
        )

        val currentCategory = categories[selectedCategoryIndex]
        val components = ComponentEntry.byCategory(currentCategory)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(components, key = { it.name }) { entry ->
                PixaActionCard(
                    title = entry.name,
                    subtitle = entry.description,
                    trailing = {
                        PixaIcon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tone = IconTone.Subtle
                        )
                    },
                    onClick = { onComponentClick(entry) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
