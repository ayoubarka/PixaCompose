package com.pixamob.pixacompose.showcase

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.components.PixaCard
import com.pixamob.pixacompose.datetime.PixaCalendar
import com.pixamob.pixacompose.datetime.PixaDatePicker
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

// A fixed "today" keeps the snapshots the same from day to day.
private val Today = LocalDate(2026, 11, 12)

/** @param pickerInitiallyOpen lets a snapshot test start with the date picker's calendar showing. */
@Composable
fun DateSection(pickerInitiallyOpen: Boolean = false) {
    PixaText("Date picker", style = PixaTheme.typography.h3)
    var due by remember { mutableStateOf<LocalDate?>(null) }
    var open by remember { mutableStateOf(pickerInitiallyOpen) }
    PixaDatePicker(due, { due = it }, open, { open = it }, Modifier.fillMaxWidth(), today = Today)
    PixaDatePicker(Today, {}, false, {}, Modifier.fillMaxWidth(), enabled = false)

    PixaText("Calendar", style = PixaTheme.typography.h3)
    var day by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 11, 18)) }
    PixaCard {
        PixaCalendar(
            selected = day,
            onSelect = { day = it },
            today = Today,
            // Weekends cannot be picked.
            isEnabled = { it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY },
        )
    }
}
