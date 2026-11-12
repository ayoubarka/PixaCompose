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
import com.pixamob.pixacompose.components.AccordionItem
import com.pixamob.pixacompose.components.BottomBar
import com.pixamob.pixacompose.components.BottomBarItem
import com.pixamob.pixacompose.components.Button
import com.pixamob.pixacompose.components.ButtonSize
import com.pixamob.pixacompose.components.ButtonVariant
import com.pixamob.pixacompose.components.MenuItem
import com.pixamob.pixacompose.components.Select
import com.pixamob.pixacompose.components.Slider
import com.pixamob.pixacompose.components.Stepper
import com.pixamob.pixacompose.components.Tab
import com.pixamob.pixacompose.components.Tabs
import com.pixamob.pixacompose.components.TopBar
import com.pixamob.pixacompose.primitives.Icon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.Text
import com.pixamob.pixacompose.theme.PixaTheme

/** @param selectInitiallyOpen lets a snapshot test start with the Select's menu showing. */
@Composable
fun NavigationSection(selectInitiallyOpen: Boolean = false) {
    Text("Tabs", style = PixaTheme.typography.h3)
    var tab by remember { mutableIntStateOf(0) }
    Tabs(Modifier.fillMaxWidth()) {
        listOf("Account", "Password", "Team").forEachIndexed { index, label ->
            Tab(selected = tab == index, onClick = { tab = index }, modifier = Modifier.weight(1f)) { Text(label) }
        }
    }

    Text("Select", style = PixaTheme.typography.h3)
    val fruits = listOf("Apple", "Banana", "Cherry")
    var fruit by remember { mutableStateOf<String?>(null) }
    var selectOpen by remember { mutableStateOf(selectInitiallyOpen) }
    Select(
        expanded = selectOpen,
        onExpandedChange = { selectOpen = it },
        modifier = Modifier.fillMaxWidth(),
        value = { Text(fruit ?: "Pick a fruit", color = if (fruit == null) PixaTheme.colors.mutedForeground else PixaTheme.colors.foreground) },
    ) {
        fruits.forEach { option ->
            MenuItem(onClick = { fruit = option; selectOpen = false }) { Text(option) }
        }
    }

    Text("Slider", style = PixaTheme.typography.h3)
    var volume by remember { mutableFloatStateOf(0.4f) }
    Slider(volume, { volume = it })
    Slider(0.7f, {}, enabled = false)

    Text("Stepper", style = PixaTheme.typography.h3)
    var step by remember { mutableIntStateOf(1) }
    Stepper(steps = 4, current = step)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = { if (step > 0) step-- }, variant = ButtonVariant.Outline, size = ButtonSize.Sm) { Text("Back") }
        Button(onClick = { if (step < 4) step++ }, size = ButtonSize.Sm) { Text("Next") }
    }

    Text("Accordion", style = PixaTheme.typography.h3)
    var openItem by remember { mutableIntStateOf(0) }
    Column {
        listOf(
            "Is it styled?" to "Only by the PixaStyle you pass to the theme.",
            "Is it animated?" to "Yes, with the springs the style defines.",
            "Is it accessible?" to "Roles and states are set; touch targets are still open.",
        ).forEachIndexed { index, (question, answer) ->
            AccordionItem(
                expanded = openItem == index,
                onExpandedChange = { openItem = if (it) index else -1 },
                title = { Text(question) },
            ) { Text(answer) }
        }
    }

    Text("Top bar", style = PixaTheme.typography.h3)
    TopBar(
        navigation = {
            Button(onClick = {}, variant = ButtonVariant.Ghost, size = ButtonSize.Sm) {
                Icon(PixaIcons.ChevronLeft, contentDescription = "Back")
            }
        },
        actions = {
            Button(onClick = {}, variant = ButtonVariant.Ghost, size = ButtonSize.Sm) {
                Icon(PixaIcons.Plus, contentDescription = "Add")
            }
        },
    ) { Text("Projects") }

    Text("Bottom bar", style = PixaTheme.typography.h3)
    var destination by remember { mutableIntStateOf(0) }
    BottomBar {
        listOf("Home" to PixaIcons.Check, "Add" to PixaIcons.Plus, "More" to PixaIcons.ChevronDown)
            .forEachIndexed { index, (label, icon) ->
                BottomBarItem(
                    selected = destination == index,
                    onClick = { destination = index },
                    label = { Text(label) },
                ) { Icon(icon, contentDescription = null) }
            }
    }
}
