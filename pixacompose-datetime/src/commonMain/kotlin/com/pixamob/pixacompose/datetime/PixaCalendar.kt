package com.pixamob.pixacompose.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.pixamob.pixacompose.primitives.LocalPixaMinimumTouchTarget
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pixamob.pixacompose.components.PixaButton
import com.pixamob.pixacompose.components.PixaButtonSize
import com.pixamob.pixacompose.components.PixaButtonVariant
import com.pixamob.pixacompose.primitives.PixaIcon
import com.pixamob.pixacompose.primitives.PixaIcons
import com.pixamob.pixacompose.primitives.PixaSurface
import com.pixamob.pixacompose.primitives.PixaText
import com.pixamob.pixacompose.theme.PixaTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** The words a calendar shows. Pass your own for another language. */
@Immutable
public class PixaDateLabels(
    /** January first. */
    public val months: List<String>,
    /** Monday first. */
    public val weekdays: List<String>,
    public val previousMonth: String,
    public val nextMonth: String,
) {
    public companion object {
        public val English: PixaDateLabels = PixaDateLabels(
            months = listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December",
            ),
            weekdays = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"),
            previousMonth = "Previous month",
            nextMonth = "Next month",
        )
    }
}

private val CellSize = 40.dp
private const val DaysInWeek = 7
private const val WeekRows = 6

@OptIn(ExperimentalTime::class)
internal fun currentDate(): LocalDate =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

/**
 * One month of days with previous and next buttons. It holds which month is shown; the caller
 * owns [selected]. Days for which [isEnabled] is false are dimmed and cannot be picked.
 */
@Composable
public fun PixaCalendar(
    selected: LocalDate?,
    onSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    today: LocalDate = remember { currentDate() },
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    isEnabled: (LocalDate) -> Boolean = { true },
    labels: PixaDateLabels = PixaDateLabels.English,
) {
    val colors = PixaTheme.colors
    val typography = PixaTheme.typography
    var month by remember { mutableStateOf((selected ?: today).let { LocalDate(it.year, it.month, 1) }) }
    val leadingBlanks = (month.dayOfWeek.isoDayNumber - firstDayOfWeek.isoDayNumber + DaysInWeek) % DaysInWeek
    val daysInMonth = month.daysUntil(month.plus(1, DateTimeUnit.MONTH))

    Column(modifier.width(CellSize * DaysInWeek), verticalArrangement = Arrangement.spacedBy(PixaTheme.spacing.xs)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PixaButton(
                onClick = { month = month.minus(1, DateTimeUnit.MONTH) },
                variant = PixaButtonVariant.Ghost,
                size = PixaButtonSize.Sm,
            ) { PixaIcon(PixaIcons.ChevronLeft, contentDescription = labels.previousMonth, size = 16.dp) }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                PixaText("${labels.months[month.month.number - 1]} ${month.year}", style = typography.label)
            }
            PixaButton(
                onClick = { month = month.plus(1, DateTimeUnit.MONTH) },
                variant = PixaButtonVariant.Ghost,
                size = PixaButtonSize.Sm,
            ) { PixaIcon(PixaIcons.ChevronRight, contentDescription = labels.nextMonth, size = 16.dp) }
        }
        Row {
            repeat(DaysInWeek) { column ->
                val weekday = (firstDayOfWeek.isoDayNumber - 1 + column) % DaysInWeek
                Box(Modifier.size(CellSize), contentAlignment = Alignment.Center) {
                    PixaText(labels.weekdays[weekday], style = typography.caption, color = colors.mutedForeground)
                }
            }
        }
        // Always six rows, so the calendar keeps its height from month to month. The cells
        // touch, so each cell is its own touch area and needs no extra room around it.
        CompositionLocalProvider(LocalPixaMinimumTouchTarget provides 0.dp) {
            repeat(WeekRows) { row ->
                Row {
                    repeat(DaysInWeek) { column ->
                        val day = row * DaysInWeek + column - leadingBlanks + 1
                        if (day in 1..daysInMonth) {
                            val date = LocalDate(month.year, month.month, day)
                            val isSelected = date == selected
                            val isToday = date == today
                            val contentColor = when {
                                isSelected -> colors.primaryForeground
                                isToday -> colors.accentForeground
                                else -> colors.foreground
                            }
                            PixaSurface(
                                selected = isSelected,
                                onClick = { onSelect(date) },
                                modifier = Modifier
                                    .size(CellSize)
                                    .semantics { contentDescription = "$day ${labels.months[month.month.number - 1]} ${month.year}" },
                                enabled = isEnabled(date),
                                color = when {
                                    isSelected -> colors.primary
                                    isToday -> colors.accent
                                    else -> Color.Transparent
                                },
                                contentColor = contentColor,
                                shape = PixaTheme.shapes.sm,
                                contentAlignment = Alignment.Center,
                            ) { PixaText(day.toString(), style = typography.bodySmall) }
                        } else {
                            Spacer(Modifier.size(CellSize))
                        }
                    }
                }
            }
        }
    }
}
