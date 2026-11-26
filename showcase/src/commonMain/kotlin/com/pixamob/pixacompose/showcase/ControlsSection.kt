package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.PixaAvatar
import com.pixamob.pixacompose.components.PixaAvatarSize
import com.pixamob.pixacompose.components.PixaBadge
import com.pixamob.pixacompose.components.PixaBadgeVariant
import com.pixamob.pixacompose.components.PixaCheckbox
import com.pixamob.pixacompose.components.PixaChip
import com.pixamob.pixacompose.components.PixaCircularProgress
import com.pixamob.pixacompose.components.PixaInput
import com.pixamob.pixacompose.components.PixaProgress
import com.pixamob.pixacompose.components.PixaRadioButton
import com.pixamob.pixacompose.components.PixaSeparator
import com.pixamob.pixacompose.components.PixaSkeleton
import com.pixamob.pixacompose.components.PixaSwitch
import com.pixamob.pixacompose.components.PixaTextarea
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme
import com.pixamob.pixacompose.theme.category

@Composable
fun ControlsSection() {
    val gap = Arrangement.spacedBy(12.dp)

    PixaText("Input", style = PixaTheme.typography.h3)
    var name by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("Compose") }
    var notes by remember { mutableStateOf("") }
    PixaInput(name, { name = it }, Modifier.fillMaxWidth(), placeholder = "Your name")
    PixaInput(
        search, { search = it }, Modifier.fillMaxWidth(),
        leading = { PixaIcon(PixaIcons.Plus, contentDescription = null, size = 16.dp) },
        trailing = { PixaIcon(PixaIcons.Close, contentDescription = "Clear", size = 16.dp) },
    )
    PixaInput("Disabled", {}, Modifier.fillMaxWidth(), enabled = false)
    PixaTextarea(notes, { notes = it }, Modifier.fillMaxWidth(), placeholder = "Notes")

    PixaText("Checkbox, radio, switch", style = PixaTheme.typography.h3)
    var agree by remember { mutableStateOf(true) }
    var plan by remember { mutableIntStateOf(0) }
    var wifi by remember { mutableStateOf(true) }
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        PixaCheckbox(agree, { agree = it })
        PixaCheckbox(!agree, { agree = !it })
        PixaCheckbox(true, {}, enabled = false)
        PixaText("Accept terms", style = PixaTheme.typography.bodySmall)
    }
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        listOf("Free", "Pro").forEachIndexed { index, label ->
            PixaRadioButton(selected = plan == index, onClick = { plan = index })
            PixaText(label, style = PixaTheme.typography.bodySmall)
        }
        PixaRadioButton(selected = true, onClick = {}, enabled = false)
    }
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        PixaSwitch(wifi, { wifi = it })
        PixaSwitch(!wifi, { wifi = !it })
        PixaSwitch(true, {}, enabled = false)
        PixaText("Wi-Fi", style = PixaTheme.typography.bodySmall)
    }

    PixaText("Badge", style = PixaTheme.typography.h3)
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        PixaBadgeVariant.entries.forEach { variant -> PixaBadge(variant = variant) { PixaText(variant.name) } }
    }
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        PixaBadge { PixaIcon(PixaIcons.Check, contentDescription = null, size = 12.dp); PixaText("Done") }
        PixaBadge(variant = PixaBadgeVariant.Destructive) { PixaText("3") }
    }

    PixaText("Avatar", style = PixaTheme.typography.h3)
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        PixaAvatar(size = PixaAvatarSize.Sm) { PixaText("A") }
        PixaAvatar { PixaText("AO") }
        PixaAvatar(size = PixaAvatarSize.Lg) { PixaText("PX") }
        PixaAvatar { PixaIcon(PixaIcons.Plus, contentDescription = "Add person") }
    }

    PixaText("Progress", style = PixaTheme.typography.h3)
    PixaProgress(0.15f)
    PixaProgress(0.65f)
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        PixaCircularProgress(0.75f) { PixaText("75%") }
        PixaCircularProgress(0.3f, size = 40.dp, strokeWidth = 4.dp)
        PixaCircularProgress(1f) { PixaIcon(PixaIcons.Check, contentDescription = null, size = 16.dp) }
        PixaCircularProgress(0.5f, color = PixaTheme.colors.category(2)) { PixaText("50%") }
    }

    PixaText("Chip", style = PixaTheme.typography.h3)
    var filter by remember { mutableIntStateOf(0) }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        listOf("All", "Strength", "Cardio").forEachIndexed { index, label ->
            PixaChip(selected = filter == index, onClick = { filter = index }) { PixaText(label) }
        }
        PixaChip(selected = false, onClick = {}, enabled = false) { PixaText("Yoga") }
    }

    PixaText("Skeleton", style = PixaTheme.typography.h3)
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        PixaSkeleton(Modifier.size(40.dp), shape = PixaTheme.shapes.full)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PixaSkeleton(Modifier.width(200.dp).height(14.dp))
            PixaSkeleton(Modifier.width(140.dp).height(14.dp))
        }
    }

    PixaText("Separator", style = PixaTheme.typography.h3)
    PixaText("Above", style = PixaTheme.typography.bodySmall)
    PixaSeparator()
    PixaText("Below", style = PixaTheme.typography.bodySmall)
}
