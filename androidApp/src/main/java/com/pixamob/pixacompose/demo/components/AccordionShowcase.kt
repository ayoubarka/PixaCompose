package com.pixamob.pixacompose.demo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.display.AccordionExpansionMode
import com.pixamob.pixacompose.components.display.AccordionItem
import com.pixamob.pixacompose.components.display.AccordionVariant
import com.pixamob.pixacompose.components.actions.ChipType
import com.pixamob.pixacompose.components.display.PixaAccordion
import com.pixamob.pixacompose.components.display.PixaAccordionGroup
import com.pixamob.pixacompose.components.actions.PixaChip
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun AccordionShowcase() {
    var expanded by remember { mutableStateOf(false) }
    var expansionMode by remember { mutableStateOf(AccordionExpansionMode.Single) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ShowcaseSection("Variants") {
            PixaAccordion(
                title = "Default Accordion",
                expanded = true,
                onExpandedChange = {},
                variant = AccordionVariant.Default,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Content for default variant", color = AppTheme.colors.baseContentBody)
            }
        }

        PixaAccordion(
            title = "Outlined Accordion",
            expanded = true,
            onExpandedChange = {},
            variant = AccordionVariant.Outlined,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Content for outlined variant", color = AppTheme.colors.baseContentBody)
        }

        PixaAccordion(
            title = "Filled Accordion",
            expanded = true,
            onExpandedChange = {},
            variant = AccordionVariant.Filled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Content for filled variant", color = AppTheme.colors.baseContentBody)
        }

        ShowcaseSection("Interactive") {
            PixaAccordion(
                title = "Toggle Me",
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "This content can be shown or hidden by tapping the header.",
                    color = AppTheme.colors.baseContentBody
                )
            }
        }

        ShowcaseSection("Group") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mode:", color = AppTheme.colors.baseContentBody)
                PixaChip(
                    text = "Single",
                    type = ChipType.Selectable,
                    selected = expansionMode == AccordionExpansionMode.Single,
                    onClick = { expansionMode = AccordionExpansionMode.Single }
                )
                PixaChip(
                    text = "Multiple",
                    type = ChipType.Selectable,
                    selected = expansionMode == AccordionExpansionMode.Multiple,
                    onClick = { expansionMode = AccordionExpansionMode.Multiple }
                )
            }
            PixaAccordionGroup(
                items = listOf(
                    AccordionItem(title = "Section 1", content = {
                        Text("Content for section 1", color = AppTheme.colors.baseContentBody)
                    }),
                    AccordionItem(title = "Section 2", content = {
                        Text("Content for section 2", color = AppTheme.colors.baseContentBody)
                    }),
                    AccordionItem(title = "Section 3", content = {
                        Text("Content for section 3", color = AppTheme.colors.baseContentBody)
                    })
                ),
                modifier = Modifier.fillMaxWidth(),
                expansionMode = expansionMode
            )
        }
    }
}
