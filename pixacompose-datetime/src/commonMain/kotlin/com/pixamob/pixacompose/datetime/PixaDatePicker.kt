package com.pixamob.pixacompose.datetime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.PixaButton
import com.pixamob.pixacompose.components.PixaButtonVariant
import com.pixamob.pixacompose.components.PixaPopover
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * A field that opens a [PixaCalendar] in a popover and closes when a day is picked. The caller
 * owns the date and whether the popover is open.
 *
 * ```
 * PixaDatePicker(date, { date = it }, open, { open = it })
 * ```
 */
@Composable
public fun PixaDatePicker(
    value: LocalDate?,
    onValueChange: (LocalDate) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = "Pick a date",
    today: LocalDate = remember { currentDate() },
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    isEnabled: (LocalDate) -> Boolean = { true },
    labels: PixaDateLabels = PixaDateLabels.English,
    /** How the picked date is written in the field, e.g. "12 Nov 2026". */
    format: (LocalDate) -> String = { "${it.day} ${labels.months[it.month.number - 1].take(3)} ${it.year}" },
) {
    Box(modifier) {
        PixaButton(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier.fillMaxWidth(),
            variant = PixaButtonVariant.Outline,
            enabled = enabled,
        ) {
            PixaText(
                text = value?.let(format) ?: placeholder,
                modifier = Modifier.weight(1f),
                color = if (value == null) PixaTheme.colors.mutedForeground else PixaTheme.colors.foreground,
            )
            PixaIcon(PixaIcons.ChevronDown, contentDescription = null, tint = PixaTheme.colors.mutedForeground, size = 16.dp)
        }
        PixaPopover(visible = expanded, onDismissRequest = { onExpandedChange(false) }) {
            PixaCalendar(
                selected = value,
                onSelect = { onValueChange(it); onExpandedChange(false) },
                today = today,
                firstDayOfWeek = firstDayOfWeek,
                isEnabled = isEnabled,
                labels = labels,
            )
        }
    }
}
