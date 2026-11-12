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
import com.pixamob.pixacompose.components.Avatar
import com.pixamob.pixacompose.components.AvatarSize
import com.pixamob.pixacompose.components.Badge
import com.pixamob.pixacompose.components.BadgeVariant
import com.pixamob.pixacompose.components.Checkbox
import com.pixamob.pixacompose.components.Input
import com.pixamob.pixacompose.components.Progress
import com.pixamob.pixacompose.components.RadioButton
import com.pixamob.pixacompose.components.Separator
import com.pixamob.pixacompose.components.Skeleton
import com.pixamob.pixacompose.components.Switch
import com.pixamob.pixacompose.components.Textarea
import com.pixamob.pixacompose.primitives.Icon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.Text
import com.pixamob.pixacompose.theme.PixaTheme

@Composable
fun ControlsSection() {
    val gap = Arrangement.spacedBy(12.dp)

    Text("Input", style = PixaTheme.typography.h3)
    var name by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("Compose") }
    var notes by remember { mutableStateOf("") }
    Input(name, { name = it }, Modifier.fillMaxWidth(), placeholder = "Your name")
    Input(
        search, { search = it }, Modifier.fillMaxWidth(),
        leading = { Icon(PixaIcons.Plus, contentDescription = null, size = 16.dp) },
        trailing = { Icon(PixaIcons.Close, contentDescription = "Clear", size = 16.dp) },
    )
    Input("Disabled", {}, Modifier.fillMaxWidth(), enabled = false)
    Textarea(notes, { notes = it }, Modifier.fillMaxWidth(), placeholder = "Notes")

    Text("Checkbox, radio, switch", style = PixaTheme.typography.h3)
    var agree by remember { mutableStateOf(true) }
    var plan by remember { mutableIntStateOf(0) }
    var wifi by remember { mutableStateOf(true) }
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(agree, { agree = it })
        Checkbox(!agree, { agree = !it })
        Checkbox(true, {}, enabled = false)
        Text("Accept terms", style = PixaTheme.typography.bodySmall)
    }
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        listOf("Free", "Pro").forEachIndexed { index, label ->
            RadioButton(selected = plan == index, onClick = { plan = index })
            Text(label, style = PixaTheme.typography.bodySmall)
        }
        RadioButton(selected = true, onClick = {}, enabled = false)
    }
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        Switch(wifi, { wifi = it })
        Switch(!wifi, { wifi = !it })
        Switch(true, {}, enabled = false)
        Text("Wi-Fi", style = PixaTheme.typography.bodySmall)
    }

    Text("Badge", style = PixaTheme.typography.h3)
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        BadgeVariant.entries.forEach { variant -> Badge(variant = variant) { Text(variant.name) } }
    }
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        Badge { Icon(PixaIcons.Check, contentDescription = null, size = 12.dp); Text("Done") }
        Badge(variant = BadgeVariant.Destructive) { Text("3") }
    }

    Text("Avatar", style = PixaTheme.typography.h3)
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        Avatar(size = AvatarSize.Sm) { Text("A") }
        Avatar { Text("AO") }
        Avatar(size = AvatarSize.Lg) { Text("PX") }
        Avatar { Icon(PixaIcons.Plus, contentDescription = "Add person") }
    }

    Text("Progress", style = PixaTheme.typography.h3)
    Progress(0.15f)
    Progress(0.65f)

    Text("Skeleton", style = PixaTheme.typography.h3)
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        Skeleton(Modifier.size(40.dp), shape = PixaTheme.shapes.full)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Skeleton(Modifier.width(200.dp).height(14.dp))
            Skeleton(Modifier.width(140.dp).height(14.dp))
        }
    }

    Text("Separator", style = PixaTheme.typography.h3)
    Text("Above", style = PixaTheme.typography.bodySmall)
    Separator()
    Text("Below", style = PixaTheme.typography.bodySmall)
}
