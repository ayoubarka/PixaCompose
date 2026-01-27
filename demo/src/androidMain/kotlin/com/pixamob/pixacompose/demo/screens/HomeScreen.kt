package com.pixamob.pixacompose.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
 * Component category data model
 */
data class ComponentCategory(
    val title: String,
    val description: String,
    val icon: String,
    val route: String,
    val color: Color
)

/**
 * Home screen showing all component categories
 */
@Composable
fun HomeScreen(
    onNavigateToComponent: (String) -> Unit
) {
    val categories = listOf(
        ComponentCategory(
            title = "Actions",
            description = "Buttons, Chips, Tabs - Interactive elements that trigger actions",
            icon = "🎯",
            route = "actions",
            color = Color(0xFF6366F1)
        ),
        ComponentCategory(
            title = "Inputs",
            description = "TextFields, Checkboxes, Switches, Sliders, Radio Buttons, Dropdowns",
            icon = "✏️",
            route = "inputs",
            color = Color(0xFF06B6D4)
        ),
        ComponentCategory(
            title = "Display",
            description = "Cards, Avatars, Icons, Images, Dividers - Visual content",
            icon = "🎨",
            route = "display",
            color = Color(0xFF8B5CF6)
        ),
        ComponentCategory(
            title = "Feedback",
            description = "Progress Indicators, Badges, Alerts, Toasts, Snackbars, Skeletons",
            icon = "💬",
            route = "feedback",
            color = Color(0xFF10B981)
        ),
        ComponentCategory(
            title = "Navigation",
            description = "TopNavBar, BottomNavBar, TabBar, Stepper, Drawer (Coming Soon)",
            icon = "🧭",
            route = "navigation",
            color = Color(0xFFF59E0B)
        ),
        ComponentCategory(
            title = "Overlay",
            description = "Dialogs, BottomSheets, Tooltips, Popovers, Menus (Coming Soon)",
            icon = "📋",
            route = "overlay",
            color = Color(0xFFEC4899)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.baseSurfaceSubtle)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.colors.brandContentDefault)
                .padding(HierarchicalSize.Padding.Large)
                .padding(top = HierarchicalSize.Padding.Massive)
        ) {
            Column {
                Text(
                    text = "PixaCompose",
                    style = AppTheme.typography.displayLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Compact))
                Text(
                    text = "Component Library Demo",
                    style = AppTheme.typography.bodyRegular,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Component List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(HierarchicalSize.Padding.Medium)
        ) {
            items(categories) { category ->
                ComponentCategoryCard(
                    category = category,
                    onClick = { onNavigateToComponent(category.route) }
                )
                Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Small))
            }
        }
    }
}

@Composable
private fun ComponentCategoryCard(
    category: ComponentCategory,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HierarchicalSize.Radius.Medium))
            .background(AppTheme.colors.baseSurfaceDefault)
            .clickable(onClick = onClick)
            .padding(HierarchicalSize.Padding.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(HierarchicalSize.Radius.Small))
                .background(category.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.icon,
                style = AppTheme.typography.headlineBold
            )
        }

        Spacer(modifier = Modifier.width(HierarchicalSize.Spacing.Medium))

        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = category.title,
                style = AppTheme.typography.titleBold,
                color = AppTheme.colors.baseContentTitle,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(HierarchicalSize.Spacing.Nano))
            Text(
                text = category.description,
                style = AppTheme.typography.captionRegular,
                color = AppTheme.colors.baseContentSubtitle
            )
        }

        // Arrow
        Text(
            text = "›",
            style = AppTheme.typography.headlineBold,
            color = AppTheme.colors.baseContentHint
        )
    }
}
