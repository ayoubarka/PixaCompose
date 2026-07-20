package com.pixamob.pixacompose.demo.display

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.pixamob.pixacompose.components.display.AccordionExpansionMode
import com.pixamob.pixacompose.components.display.AccordionItem
import com.pixamob.pixacompose.components.display.AccordionVariant
import com.pixamob.pixacompose.components.display.PixaAccordion
import com.pixamob.pixacompose.components.display.PixaAccordionGroup
import com.pixamob.pixacompose.demo.ShowcaseScreen
import com.pixamob.pixacompose.demo.ShowcaseSection
import com.pixamob.pixacompose.theme.AppTheme

@Composable
fun AccordionShowcase() {
    var expanded by remember { mutableStateOf(false) }
    val expandIcon = rememberVectorPainter(Icons.Default.KeyboardArrowDown)

    ShowcaseScreen {
        ShowcaseSection("Variants") {
            PixaAccordion(
                title = "Default", expanded = true, onExpandedChange = {},
                expandIcon = expandIcon, variant = AccordionVariant.Default,
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicText("Content", style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
            }
            PixaAccordion(
                title = "Outlined", expanded = true, onExpandedChange = {},
                expandIcon = expandIcon, variant = AccordionVariant.Outlined,
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicText("Content", style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
            }
            PixaAccordion(
                title = "Filled", expanded = true, onExpandedChange = {},
                expandIcon = expandIcon, variant = AccordionVariant.Filled,
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicText("Content", style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
            }
        }

        ShowcaseSection("Interactive") {
            PixaAccordion(
                title = "Toggle Me", expanded = expanded,
                onExpandedChange = { expanded = it },
                expandIcon = expandIcon, modifier = Modifier.fillMaxWidth()
            ) {
                BasicText("This content can be shown or hidden.",
                    style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
            }
        }

        ShowcaseSection("Group") {
            PixaAccordionGroup(
                items = listOf(
                    AccordionItem(title = "Section 1", content = {
                        BasicText("Content 1", style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
                    }),
                    AccordionItem(title = "Section 2", content = {
                        BasicText("Content 2", style = AppTheme.typography.bodyRegular.copy(color = AppTheme.colors.baseContentBody))
                    })
                ),
                expandIcon = expandIcon,
                modifier = Modifier.fillMaxWidth(),
                expansionMode = AccordionExpansionMode.Multiple
            )
        }
    }
}
