package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.inputs.PixaStarRating
import com.pixamob.pixacompose.components.inputs.StarRatingSize
import com.pixamob.pixacompose.components.inputs.StarRatingVariant
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun StarRatingShowcase() {
    var rating by remember { mutableStateOf(3) }

    ShowcaseScreen {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaStarRating(variant = StarRatingVariant.Interactive, size = StarRatingSize.Medium, value = rating.toFloat(), onValueChange = { rating = it })
                PixaStarRating(variant = StarRatingVariant.Descriptive, size = StarRatingSize.Medium, value = 4.5f, onValueChange = {})
            }
        }

        ShowcaseSection("Sizes") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaStarRating(variant = StarRatingVariant.Descriptive, size = StarRatingSize.Small, value = 4f, onValueChange = {})
                PixaStarRating(variant = StarRatingVariant.Descriptive, size = StarRatingSize.Medium, value = 4f, onValueChange = {})
                PixaStarRating(variant = StarRatingVariant.Descriptive, size = StarRatingSize.Large, value = 4f, onValueChange = {})
            }
        }

        ShowcaseSection("Interactive") {
            PixaStarRating(variant = StarRatingVariant.Interactive, size = StarRatingSize.Medium, value = rating.toFloat(), onValueChange = { rating = it })
        }
    }
}
