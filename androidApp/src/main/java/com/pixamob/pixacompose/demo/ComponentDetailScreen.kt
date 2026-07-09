package com.pixamob.pixacompose.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun ComponentDetailScreen(
    componentName: String,
    onBack: () -> Unit
) {
    val entry = ComponentEntry.find(componentName)
    val toggleTheme = LocalThemeToggle.current

    if (entry == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(AppTheme.colors.baseSurfaceDefault),
            contentAlignment = Alignment.Center
        ) {
            Text("Component '$componentName' not found", color = AppTheme.colors.errorContentDefault)
        }
        return
    }

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
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AppTheme.colors.baseContentTitle
                    )
                }
                Text(
                    text = entry.name,
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
        entry.showcase()
    }
}
