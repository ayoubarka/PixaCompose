package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.PixaAlert
import com.pixamob.pixacompose.components.PixaAlertVariant
import com.pixamob.pixacompose.components.PixaAvatar
import com.pixamob.pixacompose.components.PixaButton
import com.pixamob.pixacompose.components.PixaButtonSize
import com.pixamob.pixacompose.components.PixaButtonVariant
import com.pixamob.pixacompose.components.PixaCard
import com.pixamob.pixacompose.components.PixaCodeInput
import com.pixamob.pixacompose.components.PixaField
import com.pixamob.pixacompose.components.PixaInput
import com.pixamob.pixacompose.components.PixaListItem
import com.pixamob.pixacompose.components.PixaPageDots
import com.pixamob.pixacompose.components.PixaSeparator
import com.pixamob.pixacompose.components.PixaSpinner
import com.pixamob.pixacompose.components.PixaSwitch
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme

@Composable
fun FormsSection() {
    val gap = Arrangement.spacedBy(12.dp)

    PixaText("Field", style = PixaTheme.typography.h3)
    var email by remember { mutableStateOf("ayoub@") }
    var name by remember { mutableStateOf("") }
    PixaField("Name", helper = "Shown on your profile.") {
        PixaInput(name, { name = it }, Modifier.fillMaxWidth(), placeholder = "Your name")
    }
    PixaField("Email", error = if ("." in email) null else "Enter a valid email address.") {
        PixaInput(email, { email = it }, Modifier.fillMaxWidth())
    }

    PixaText("Code input", style = PixaTheme.typography.h3)
    var code by remember { mutableStateOf("482") }
    PixaCodeInput(code, { code = it })

    PixaText("Alert", style = PixaTheme.typography.h3)
    PixaAlert(
        icon = { PixaIcon(PixaIcons.Check, contentDescription = null) },
        title = { PixaText("Backup complete") },
    ) { PixaText("Your data was saved a minute ago.") }
    PixaAlert(
        variant = PixaAlertVariant.Destructive,
        icon = { PixaIcon(PixaIcons.Close, contentDescription = null) },
        title = { PixaText("Payment failed") },
    ) { PixaText("Check your card details and try again.") }

    PixaText("List item", style = PixaTheme.typography.h3)
    var notify by remember { mutableStateOf(true) }
    PixaCard(Modifier.fillMaxWidth(), contentPadding = PaddingValues(PixaTheme.spacing.sm)) {
        PixaListItem(
            onClick = {},
            leading = { PixaAvatar { PixaText("GS") } },
            trailing = { PixaIcon(PixaIcons.ChevronRight, contentDescription = null, size = 16.dp) },
            supporting = { PixaText("4 sets x 12 reps") },
        ) { PixaText("Goblet squat") }
        PixaSeparator()
        PixaListItem(
            leading = { PixaIcon(PixaIcons.Check, contentDescription = null) },
            trailing = { PixaSwitch(notify, { notify = it }) },
        ) { PixaText("Notifications") }
        PixaSeparator()
        PixaListItem(onClick = {}, enabled = false, trailing = { PixaText("Soon") }) { PixaText("Export data") }
    }

    PixaText("Spinner", style = PixaTheme.typography.h3)
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        PixaSpinner()
        PixaSpinner(size = 32.dp, strokeWidth = 3.dp, color = PixaTheme.colors.primary)
        PixaButton(onClick = {}, enabled = false) {
            PixaSpinner(size = 16.dp)
            PixaText("Saving")
        }
    }

    PixaText("Page dots", style = PixaTheme.typography.h3)
    var page by remember { mutableIntStateOf(1) }
    Row(horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
        PixaPageDots(count = 4, current = page)
        PixaButton(onClick = { page = (page + 1) % 4 }, variant = PixaButtonVariant.Outline, size = PixaButtonSize.Sm) {
            PixaText("Next page")
        }
    }
}
