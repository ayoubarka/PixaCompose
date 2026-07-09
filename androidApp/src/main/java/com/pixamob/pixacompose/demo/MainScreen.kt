package com.pixamob.pixacompose.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onComponentClick: (ComponentEntry) -> Unit) {
    val categories = ComponentCategory.entries
    val pagerState = rememberPagerState(pageCount = { categories.size })
    val coroutineScope = rememberCoroutineScope()
    val toggleTheme = LocalThemeToggle.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(AppTheme.colors.baseSurfaceDefault)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AppTheme.colors.baseSurfaceDefault,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PixaCompose",
                    style = AppTheme.typography.titleBold,
                    color = AppTheme.colors.baseContentTitle,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = toggleTheme) {
                    Icon(
                        imageVector = if (AppTheme.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (AppTheme.isDarkTheme) "Switch to light" else "Switch to dark",
                        tint = AppTheme.colors.baseContentTitle
                    )
                }
            }
        }

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            containerColor = AppTheme.colors.baseSurfaceDefault,
            contentColor = AppTheme.colors.baseContentTitle,
            divider = {}
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = category.displayName,
                            style = AppTheme.typography.bodyBold,
                            maxLines = 1
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val category = categories[page]
            val components = ComponentEntry.byCategory(category)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                components.forEach { entry ->
                    ComponentCard(entry = entry, onClick = { onComponentClick(entry) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComponentCard(entry: ComponentEntry, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.baseSurfaceSubtle
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.name,
                style = AppTheme.typography.titleBold,
                color = AppTheme.colors.baseContentTitle
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = entry.description,
                style = AppTheme.typography.captionRegular,
                color = AppTheme.colors.baseContentBody
            )
        }
    }
}
