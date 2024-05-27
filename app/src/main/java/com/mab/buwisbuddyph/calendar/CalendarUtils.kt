package com.mab.buwisbuddyph.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object CalendarUtils {
    var selectedDate: LocalDate? = null

    fun formattedDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        return date.format(formatter)
    }

    fun formattedTime(time: LocalTime): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm:ss a")
        return time.format(formatter)
    }

    fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    fun daysInMonthArray(date: LocalDate): ArrayList<LocalDate?> {
        val daysInMonthArray = ArrayList<LocalDate?>()
        val yearMonth = YearMonth.from(date)

        val daysInMonth = yearMonth.lengthOfMonth()

        val firstOfMonth = selectedDate?.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth?.dayOfWeek?.value ?: 0

        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek)
                daysInMonthArray.add(null)
            else
                daysInMonthArray.add(LocalDate.of(selectedDate!!.year, selectedDate!!.month, i - dayOfWeek))
        }
        return daysInMonthArray
    }

    fun daysInWeekArray(selectedDate: LocalDate): ArrayList<LocalDate> {
        val days = ArrayList<LocalDate>()
        var current = sundayForDate(selectedDate)
        val endDate = current.plusWeeks(1)

        while (current.isBefore(endDate)) {
            days.add(current)
            current = current.plusDays(1)
        }
        return days
    }

    private fun sundayForDate(current: LocalDate): LocalDate {
        var tempCurrent = current
        val oneWeekAgo = tempCurrent.minusWeeks(1)

        while (tempCurrent.isAfter(oneWeekAgo)) {
            if (tempCurrent.dayOfWeek == DayOfWeek.SUNDAY) return tempCurrent
            tempCurrent = tempCurrent.minusDays(1)
        }
        return tempCurrent
    }
}
