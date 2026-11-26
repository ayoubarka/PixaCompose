package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.PixaAccordionItem
import com.pixamob.pixacompose.components.PixaBottomBar
import com.pixamob.pixacompose.components.PixaBottomBarItem
import com.pixamob.pixacompose.components.PixaButton
import com.pixamob.pixacompose.components.PixaButtonSize
import com.pixamob.pixacompose.components.PixaButtonVariant
import com.pixamob.pixacompose.components.PixaMenuItem
import com.pixamob.pixacompose.components.PixaSelect
import com.pixamob.pixacompose.components.PixaSlider
import com.pixamob.pixacompose.components.PixaStepper
import com.pixamob.pixacompose.components.PixaTimeline
import com.pixamob.pixacompose.components.PixaTab
import com.pixamob.pixacompose.components.PixaTabs
import com.pixamob.pixacompose.components.PixaTopBar
import com.pixamob.pixacompose.components.PixaTopBarSize
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme

/** @param selectInitiallyOpen lets a snapshot test start with the PixaSelect's menu showing. */
@Composable
fun NavigationSection(selectInitiallyOpen: Boolean = false) {
    PixaText("Tabs", style = PixaTheme.typography.h3)
    var tab by remember { mutableIntStateOf(0) }
    PixaTabs(Modifier.fillMaxWidth()) {
        listOf("Account", "Password", "Team").forEachIndexed { index, label ->
            PixaTab(selected = tab == index, onClick = { tab = index }, modifier = Modifier.weight(1f)) { PixaText(label) }
        }
    }

    PixaText("Select", style = PixaTheme.typography.h3)
    val fruits = listOf("Apple", "Banana", "Cherry")
    var fruit by remember { mutableStateOf<String?>(null) }
    var selectOpen by remember { mutableStateOf(selectInitiallyOpen) }
    PixaSelect(
        expanded = selectOpen,
        onExpandedChange = { selectOpen = it },
        modifier = Modifier.fillMaxWidth(),
        value = { PixaText(fruit ?: "Pick a fruit", color = if (fruit == null) PixaTheme.colors.mutedForeground else PixaTheme.colors.foreground) },
    ) {
        fruits.forEach { option ->
            PixaMenuItem(onClick = { fruit = option; selectOpen = false }) { PixaText(option) }
        }
    }

    PixaText("Slider", style = PixaTheme.typography.h3)
    var volume by remember { mutableFloatStateOf(0.4f) }
    PixaSlider(volume, { volume = it }, valueLabel = { PixaText("${(it * 100).toInt()}%") })
    PixaSlider(0.7f, {}, enabled = false)

    PixaText("Stepper", style = PixaTheme.typography.h3)
    var step by remember { mutableIntStateOf(1) }
    PixaStepper(steps = 4, current = step)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PixaButton(onClick = { if (step > 0) step-- }, variant = PixaButtonVariant.Outline, size = PixaButtonSize.Sm) { PixaText("Back") }
        PixaButton(onClick = { if (step < 4) step++ }, size = PixaButtonSize.Sm) { PixaText("Next") }
    }

    PixaText("Timeline", style = PixaTheme.typography.h3)
    val order = listOf("Ordered" to "Monday, 9:12", "Packed" to "Tuesday, 14:30", "On its way" to "Expected Thursday", "Delivered" to "")
    PixaTimeline(steps = order.size, current = step.coerceAtMost(order.size)) { index ->
        Column {
            PixaText(order[index].first, style = PixaTheme.typography.label)
            if (order[index].second.isNotEmpty()) PixaText(order[index].second, style = PixaTheme.typography.caption, color = PixaTheme.colors.mutedForeground)
        }
    }

    PixaText("Accordion", style = PixaTheme.typography.h3)
    var openItem by remember { mutableIntStateOf(0) }
    Column {
        listOf(
            "Is it styled?" to "Only by the PixaStyle you pass to the theme.",
            "Is it animated?" to "Yes, with the springs the style defines.",
            "Is it accessible?" to "Roles and states are set; touch targets are still open.",
        ).forEachIndexed { index, (question, answer) ->
            PixaAccordionItem(
                expanded = openItem == index,
                onExpandedChange = { openItem = if (it) index else -1 },
                title = { PixaText(question) },
            ) { PixaText(answer) }
        }
    }

    PixaText("Top bar", style = PixaTheme.typography.h3)
    PixaTopBar(
        navigation = {
            PixaButton(onClick = {}, variant = PixaButtonVariant.Ghost, size = PixaButtonSize.Sm) {
                PixaIcon(PixaIcons.ChevronLeft, contentDescription = "Back")
            }
        },
        actions = {
            PixaButton(onClick = {}, variant = PixaButtonVariant.Ghost, size = PixaButtonSize.Sm) {
                PixaIcon(PixaIcons.Plus, contentDescription = "Add")
            }
        },
    ) { PixaText("Projects") }
    PixaTopBar(
        size = PixaTopBarSize.Lg,
        showDivider = false,
        actions = {
            PixaButton(onClick = {}, variant = PixaButtonVariant.Ghost, size = PixaButtonSize.Sm) {
                PixaIcon(PixaIcons.Plus, contentDescription = "Add")
            }
        },
    ) { PixaText("Health", style = PixaTheme.typography.h2) }

    PixaText("Bottom bar", style = PixaTheme.typography.h3)
    var destination by remember { mutableIntStateOf(0) }
    PixaBottomBar {
        listOf("Home" to PixaIcons.Check, "Add" to PixaIcons.Plus, "More" to PixaIcons.ChevronDown)
            .forEachIndexed { index, (label, icon) ->
                PixaBottomBarItem(
                    selected = destination == index,
                    onClick = { destination = index },
                    label = { PixaText(label) },
                ) { PixaIcon(icon, contentDescription = null) }
            }
    }
}
