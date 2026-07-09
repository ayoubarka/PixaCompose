package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.PixaImage
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun ImageShowcase() {
    var clickCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Image from Vector") {
            PixaImage(
                imageVector = Icons.Default.Person,
                contentDescription = "Person",
                size = 64.dp,
                tint = AppTheme.colors.brandContentDefault
            )
        }

        ShowcaseSection("Shapes") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaImage(
                    painter = rememberVectorPainter(image = Icons.Default.Person),
                    contentDescription = "Circle",
                    size = 64.dp,
                    tint = AppTheme.colors.brandContentDefault,
                    shape = CircleShape
                )
                PixaImage(
                    painter = rememberVectorPainter(image = Icons.Default.Favorite),
                    contentDescription = "Rounded",
                    size = 64.dp,
                    tint = Color.Red,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        ShowcaseSection("With Tint") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                PixaImage(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Filled",
                    size = 48.dp,
                    tint = Color.Red
                )
                PixaImage(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Outline",
                    size = 48.dp,
                    tint = Color.Gray
                )
            }
        }

        ShowcaseSection("With onClick") {
            PixaImage(
                painter = rememberVectorPainter(image = Icons.Default.Person),
                contentDescription = "Clickable",
                size = 64.dp,
                tint = AppTheme.colors.brandContentDefault,
                onClick = { clickCount++ }
            )
            Text(
                text = "Clicked $clickCount times",
                style = AppTheme.typography.captionRegular,
                color = AppTheme.colors.baseContentCaption
            )
        }
    }
}
