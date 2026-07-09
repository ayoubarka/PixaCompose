package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.inputs.PixaSearchBar
import com.pixamob.pixacompose.components.inputs.SearchBarVariant
import com.pixamob.pixacompose.components.inputs.SearchSuggestion
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun SearchBarShowcase() {
    var query by remember { mutableStateOf("") }
    var suggestionsQuery by remember { mutableStateOf("") }

    val suggestions = listOf(
        SearchSuggestion(text = "apple"),
        SearchSuggestion(text = "banana"),
        SearchSuggestion(text = "cherry"),
        SearchSuggestion(text = "date"),
        SearchSuggestion(text = "elderberry")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaSearchBar(
                    value = query,
                    onValueChange = { query = it },
                    variant = SearchBarVariant.Filled,
                    placeholder = "Filled search"
                )
                PixaSearchBar(
                    value = query,
                    onValueChange = { query = it },
                    variant = SearchBarVariant.Outlined,
                    placeholder = "Outlined search"
                )
                PixaSearchBar(
                    value = query,
                    onValueChange = { query = it },
                    variant = SearchBarVariant.Elevated,
                    placeholder = "Elevated search"
                )
            }
        }

        ShowcaseSection("Sizes") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixaSearchBar(
                    value = query,
                    onValueChange = { query = it },
                    size = SizeVariant.Small,
                    placeholder = "Small"
                )
                PixaSearchBar(
                    value = query,
                    onValueChange = { query = it },
                    size = SizeVariant.Medium,
                    placeholder = "Medium"
                )
                PixaSearchBar(
                    value = query,
                    onValueChange = { query = it },
                    size = SizeVariant.Large,
                    placeholder = "Large"
                )
            }
        }

        ShowcaseSection("Interactive") {
            PixaSearchBar(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search...",
                onSearch = { /* handle search */ },
                onClear = { query = "" }
            )
        }

        ShowcaseSection("With Suggestions") {
            PixaSearchBar(
                value = suggestionsQuery,
                onValueChange = { suggestionsQuery = it },
                placeholder = "Type to see suggestions...",
                suggestions = suggestions,
                showSuggestions = suggestionsQuery.isNotEmpty(),
                onSuggestionClick = { suggestion -> suggestionsQuery = suggestion.text }
            )
        }
    }
}
