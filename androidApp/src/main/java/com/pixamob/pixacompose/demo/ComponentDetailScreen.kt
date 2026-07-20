package com.pixamob.pixacompose.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.IconButtonVariant
import com.pixamob.pixacompose.components.actions.PixaIconButton
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun ComponentDetailScreen(
    componentName: String,
    onBack: () -> Unit
) {
    val entry = ComponentEntry.find(componentName)
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
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val backIconPainter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack)
            PixaIconButton(
                icon = backIconPainter,
                onClick = onBack,
                variant = IconButtonVariant.Ghost,
                contentDescription = "Back"
            )

            if (entry != null) {
                BasicText(
                    text = entry.name,
                    style = AppTheme.typography.titleBold.copy(
                        color = AppTheme.colors.baseContentTitle
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

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

        if (entry == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = "Component '$componentName' not found",
                    style = AppTheme.typography.bodyRegular.copy(
                        color = AppTheme.colors.errorContentDefault
                    )
                )
            }
        } else {
            entry.showcase()
        }
    }
}
