package com.pixamob.pixacompose.demo.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.actions.PixaSegmentedButton
import com.pixamob.pixacompose.components.actions.SegmentedButtonItem
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.SizeVariant

@Composable
fun SegmentedButtonShowcase() {
    var selectedId by remember { mutableStateOf("day") }

    ShowcaseScreen {
        ShowcaseSection("Text Segments") {
            PixaSegmentedButton(
                items = listOf(
                    SegmentedButtonItem(id = "day", label = "Day"),
                    SegmentedButtonItem(id = "week", label = "Week"),
                    SegmentedButtonItem(id = "month", label = "Month")
                ),
                selectedId = selectedId,
                onSelectionChange = { selectedId = it }
            )
        }

        ShowcaseSection("Sizes") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixaSegmentedButton(
                    items = listOf(
                        SegmentedButtonItem(id = "a", label = "A"),
                        SegmentedButtonItem(id = "b", label = "B"),
                        SegmentedButtonItem(id = "c", label = "C")
                    ),
                    selectedId = "a",
                    onSelectionChange = {},
                    size = SizeVariant.Small
                )
                PixaSegmentedButton(
                    items = listOf(
                        SegmentedButtonItem(id = "a", label = "A"),
                        SegmentedButtonItem(id = "b", label = "B"),
                        SegmentedButtonItem(id = "c", label = "C")
                    ),
                    selectedId = "b",
                    onSelectionChange = {},
                    size = SizeVariant.Medium
                )
            }
        }
    }
}
