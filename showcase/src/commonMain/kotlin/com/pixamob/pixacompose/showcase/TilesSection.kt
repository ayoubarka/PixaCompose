package com.pixamob.pixacompose.showcase

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.PixaActionTile
import com.pixamob.pixacompose.components.PixaAvatar
import com.pixamob.pixacompose.components.PixaAvatarSize
import com.pixamob.pixacompose.components.PixaAvatarRow
import com.pixamob.pixacompose.components.PixaAvatarSwitcher
import com.pixamob.pixacompose.components.PixaButton
import com.pixamob.pixacompose.components.PixaCard
import com.pixamob.pixacompose.components.PixaEmptyState
import com.pixamob.pixacompose.components.PixaFab
import com.pixamob.pixacompose.components.PixaFabSize
import com.pixamob.pixacompose.components.PixaIconTile
import com.pixamob.pixacompose.components.PixaIconTileSize
import com.pixamob.pixacompose.components.PixaRatingFace
import com.pixamob.pixacompose.components.PixaRatingPicker
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme
import com.pixamob.pixacompose.theme.category

private class Profile(val name: String, val color: Color)

/** Tiles, ratings, ringed avatars and the other pieces a dashboard-style app is built from. */
@Composable
fun TilesSection(switcherInitiallyOpen: Boolean = false) {
    val colors = PixaTheme.colors
    val gap = Arrangement.spacedBy(12.dp)
    // Category hues come from the style.
    val accents = List(4) { colors.category(it) }

    PixaText("Icon tile", style = PixaTheme.typography.h3)
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        PixaIconTileSize.entries.forEachIndexed { index, size ->
            PixaIconTile(color = accents[index], size = size) { PixaIcon(PixaIcons.Check, contentDescription = null) }
        }
    }

    PixaText("Action tile", style = PixaTheme.typography.h3)
    var picked by remember { mutableIntStateOf(1) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = gap) {
        listOf("Weight", "Food", "Symptom", "Mood").forEachIndexed { index, label ->
            PixaActionTile(
                onClick = { picked = index },
                modifier = Modifier.weight(1f),
                color = accents[index],
                selected = picked == index,
                label = { PixaText(label) },
            ) { PixaIcon(if (index % 2 == 0) PixaIcons.Plus else PixaIcons.Check, contentDescription = null) }
        }
    }

    PixaText("Rating picker", style = PixaTheme.typography.h3)
    var severity by remember { mutableIntStateOf(2) }
    PixaRatingPicker(severity, { severity = it }, Modifier.fillMaxWidth(), color = colors.destructive)
    var mood by remember { mutableIntStateOf(4) }
    PixaRatingPicker(mood, { mood = it }, Modifier.fillMaxWidth(), color = colors.success) { PixaRatingFace(it) }

    PixaText("Avatar ring", style = PixaTheme.typography.h3)
    val profiles = remember(accents) {
        listOf(Profile("Milo", accents[0]), Profile("Luna", accents[1]), Profile("Rex", accents[2]))
    }
    var current by remember { mutableStateOf(profiles.first()) }
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        profiles.forEach { profile ->
            PixaAvatar(
                size = PixaAvatarSize.Lg,
                color = profile.color.copy(alpha = 0.18f),
                contentColor = profile.color,
                ringColor = profile.color,
                selected = profile == current,
            ) { PixaText(profile.name.take(1)) }
        }
        PixaAvatar(size = PixaAvatarSize.Xl, color = colors.primary, contentColor = colors.primaryForeground) { PixaText("AO") }
    }

    PixaText("Avatar row", style = PixaTheme.typography.h3)
    PixaAvatarRow(
        items = profiles,
        selected = current,
        onSelect = { current = it },
        onAdd = {},
        addLabel = { PixaText("Add") },
        label = { PixaText(it.name) },
    ) { profile, isCurrent ->
        PixaAvatar(
            size = PixaAvatarSize.Lg,
            color = profile.color.copy(alpha = 0.18f),
            contentColor = profile.color,
            ringColor = profile.color,
            selected = isCurrent,
        ) { PixaText(profile.name.take(1)) }
    }

    PixaText("Avatar switcher", style = PixaTheme.typography.h3)
    var open by remember { mutableStateOf(switcherInitiallyOpen) }
    PixaAvatarSwitcher(
        items = profiles,
        selected = current,
        onSelect = { current = it },
        expanded = open,
        onExpandedChange = { open = it },
        contentDescription = "Switch pet",
        showLabel = false,
        onAdd = {},
        addLabel = { PixaText("Add pet") },
        avatarSize = PixaAvatarSize.Lg,
        label = { PixaText(it.name) },
        // A large avatar in the pill, a small one in the menu rows.
        menuAvatar = { profile, isCurrent ->
            PixaAvatar(
                color = profile.color.copy(alpha = 0.18f),
                contentColor = profile.color,
                ringColor = profile.color,
                ringWidth = 2.dp,
                selected = isCurrent,
            ) { PixaText(profile.name.take(1), style = PixaTheme.typography.caption) }
        },
    ) { profile, isCurrent ->
        PixaAvatar(
            size = PixaAvatarSize.Lg,
            color = profile.color.copy(alpha = 0.18f),
            contentColor = profile.color,
            ringColor = profile.color,
            ringWidth = 2.dp,
            selected = isCurrent,
        ) { PixaText(profile.name.take(1), style = PixaTheme.typography.caption) }
    }

    // Given a width, the pill stretches: the label takes the room and the chevron stays at the end.
    var wideOpen by remember { mutableStateOf(false) }
    PixaAvatarSwitcher(
        items = profiles,
        selected = current,
        onSelect = { current = it },
        expanded = wideOpen,
        onExpandedChange = { wideOpen = it },
        modifier = Modifier.fillMaxWidth(),
        onAdd = {},
        addLabel = { PixaText("Add pet") },
        label = { PixaText(it.name) },
    ) { profile, isCurrent ->
        PixaAvatar(
            color = profile.color.copy(alpha = 0.18f),
            contentColor = profile.color,
            ringColor = profile.color,
            ringWidth = 2.dp,
            selected = isCurrent,
        ) { PixaText(profile.name.take(1), style = PixaTheme.typography.caption) }
    }

    PixaText("Floating action button", style = PixaTheme.typography.h3)
    var fabOpen by remember { mutableStateOf(false) }
    val turn by animateFloatAsState(if (fabOpen) 45f else 0f, PixaTheme.motion.state.spec())
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
        PixaFab(onClick = { fabOpen = !fabOpen }) {
            PixaIcon(PixaIcons.Plus, contentDescription = "Add", modifier = Modifier.graphicsLayer { rotationZ = turn })
        }
        PixaFab(onClick = {}, size = PixaFabSize.Lg) { PixaIcon(PixaIcons.Check, contentDescription = "Done") }
    }

    PixaText("Empty state", style = PixaTheme.typography.h3)
    PixaCard(Modifier.fillMaxWidth()) {
        PixaEmptyState(
            picture = { PixaIconTile(size = PixaIconTileSize.Xl) { PixaIcon(PixaIcons.Plus, contentDescription = null) } },
            message = { PixaText("Add a pet to start its health log.") },
            action = { PixaButton(onClick = {}) { PixaText("Add pet") } },
        ) { PixaText("No pet yet") }
    }
}
