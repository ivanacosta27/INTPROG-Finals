package com.mab.buwisbuddyph.calendar

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalTime

data class Event(
    var eventID: String = "",
    var name: String,
    var date: LocalDate,
    var time: LocalTime,
    var timestamp: Timestamp? = null,
    var user: String = ""
) {
    companion object {
        val eventsList = ArrayList<Event>()

        fun eventsForDate(date: LocalDate): List<Event> {
            return eventsList.filter { it.date == date }
        }
    }
}


