package com.pixamob.pixacompose.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.theme.AppTheme
import com.pixamob.pixacompose.theme.HierarchicalSize

/**
 * Common header for demo screens
 */
@Composable
fun DemoScreenHeader(
    title: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.brandContentDefault)
            .padding(HierarchicalSize.Padding.Medium)
            .padding(top = HierarchicalSize.Padding.Large)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Small))
            Text(
                text = title,
                style = AppTheme.typography.headlineBold,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Section container for grouping related demos
 */
@Composable
fun DemoSection(
    title: String,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = AppTheme.typography.titleBold,
            color = AppTheme.colors.baseContentTitle,
            fontWeight = FontWeight.SemiBold
        )

        if (description != null) {
            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Nano))
            Text(
                text = description,
                style = AppTheme.typography.bodyRegular,
                color = AppTheme.colors.baseContentSubtitle
            )
        }

        Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Medium))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(HierarchicalSize.Radius.Medium))
                .background(AppTheme.colors.baseSurfaceDefault)
                .padding(HierarchicalSize.Padding.Medium)
        ) {
            Column {
                content()
            }
        }
    }
}

/**
 * Code snippet display (optional, for showing code examples)
 */
@Composable
fun CodeSnippet(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HierarchicalSize.Radius.Small))
            .background(Color(0xFF1E1E1E))
            .padding(HierarchicalSize.Padding.Medium)
    ) {
        Text(
            text = code,
            style = AppTheme.typography.captionLight.copy(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            ),
            color = Color(0xFFD4D4D4)
        )
    }
}

/**
 * Label for describing examples
 */
@Composable
fun DemoLabel(text: String) {
    Text(
        text = text,
        style = AppTheme.typography.labelMedium,
        color = AppTheme.colors.baseContentHint,
        modifier = Modifier.padding(bottom = HierarchicalSize.Spacing.Nano)
    )
}
