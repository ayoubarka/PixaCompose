package com.pixamob.pixacompose.demo.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.inputs.PixaSearchBar
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection

@Composable
fun SearchBarShowcase() {
    var query by remember { mutableStateOf("") }

    ShowcaseScreen {
        ShowcaseSection("Search Bar") {
            PixaSearchBar(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth())
        }

        ShowcaseSection("With Suggestions") {
            PixaSearchBar(
                value = query, onValueChange = { query = it },
                placeholder = "Try typing...",
                suggestions = listOf(
                    com.pixamob.pixacompose.components.inputs.SearchSuggestion("Apple"),
                    com.pixamob.pixacompose.components.inputs.SearchSuggestion("Banana"),
                    com.pixamob.pixacompose.components.inputs.SearchSuggestion("Orange")
                ),
                showSuggestions = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
