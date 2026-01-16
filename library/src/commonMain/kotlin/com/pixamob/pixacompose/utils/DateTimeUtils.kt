package com.pixamob.pixacompose.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Utility functions for date and time operations
 * Provides multiplatform-compatible date/time helpers
 */
object DateTimeUtils {

    /**
     * Get current LocalDate
     */
    fun now(): LocalDate {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    /**
     * Get current LocalDateTime
     */
    fun nowDateTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }

    /**
     * Get current timestamp in milliseconds
     */
    fun nowMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }

    /**
     * Convert LocalDate to timestamp (milliseconds since epoch)
     */
    fun LocalDate.toEpochMillis(): Long {
        return this.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }

    /**
     * Convert timestamp to LocalDate
     */
    fun Long.toLocalDate(): LocalDate {
        return Instant.fromEpochMilliseconds(this)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    /**
     * Convert timestamp to LocalDateTime
     */
    fun Long.toLocalDateTime(): LocalDateTime {
        return Instant.fromEpochMilliseconds(this)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }

    /**
     * Get days in month (handles leap years)
     */
    fun getDaysInMonth(year: Int, month: Month): Int {
        return when (month) {
            Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
            Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            Month.FEBRUARY -> if (isLeapYear(year)) 29 else 28
        }
    }

    /**
     * Check if year is leap year
     */
    fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    /**
     * Get day of week as 0-6 (0=Sunday)
     */
    fun getDayOfWeekIndex(dayOfWeek: DayOfWeek): Int {
        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> 0
            DayOfWeek.MONDAY -> 1
            DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3
            DayOfWeek.THURSDAY -> 4
            DayOfWeek.FRIDAY -> 5
            DayOfWeek.SATURDAY -> 6
        }
    }

    /**
     * Add days to LocalDate
     */
    fun LocalDate.plusDays(days: Int): LocalDate {
        return this.plus(days, DateTimeUnit.DAY)
    }

    /**
     * Subtract days from LocalDate
     */
    fun LocalDate.minusDays(days: Int): LocalDate {
        return this.minus(days, DateTimeUnit.DAY)
    }

    /**
     * Add months to LocalDate
     */
    fun LocalDate.plusMonths(months: Int): LocalDate {
        return this.plus(months, DateTimeUnit.MONTH)
    }

    /**
     * Subtract months from LocalDate
     */
    fun LocalDate.minusMonths(months: Int): LocalDate {
        return this.minus(months, DateTimeUnit.MONTH)
    }

    /**
     * Get number of days between two dates
     */
    fun LocalDate.daysUntil(other: LocalDate): Int {
        return this.daysUntil(other)
    }

    /**
     * Check if date is today
     */
    fun LocalDate.isToday(): Boolean {
        return this == now()
    }

    /**
     * Check if date is in range
     */
    fun LocalDate.isInRange(minDate: LocalDate?, maxDate: LocalDate?): Boolean {
        return (minDate == null || this >= minDate) && (maxDate == null || this <= maxDate)
    }

    /**
     * Format LocalDate to string (ISO format)
     */
    fun LocalDate.toIsoString(): String {
        return this.toString()
    }

    /**
     * Get month name from Month enum
     */
    fun Month.getDisplayName(): String {
        return this.name.lowercase().replaceFirstChar { it.uppercase() }
    }

    /**
     * Format integer as zero-padded string (e.g., 5 -> "05")
     */
    fun formatTwoDigits(value: Int): String {
        return if (value < 10) "0$value" else value.toString()
    }

    /**
     * Format LocalTime to HH:MM string
     */
    fun LocalTime.toFormattedString(): String {
        return "${formatTwoDigits(hour)}:${formatTwoDigits(minute)}"
    }

    /**
     * Format LocalTime to 12-hour format with AM/PM
     */
    fun LocalTime.to12HourFormat(amLabel: String = "AM", pmLabel: String = "PM"): String {
        val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val period = if (hour < 12) amLabel else pmLabel
        return "${formatTwoDigits(hour12)}:${formatTwoDigits(minute)} $period"
    }
}

