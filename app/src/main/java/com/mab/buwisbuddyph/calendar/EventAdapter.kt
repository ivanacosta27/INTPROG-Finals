package com.mab.buwisbuddyph.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mab.buwisbuddyph.R

class EventAdapter(context: Context, events: List<Event>) : ArrayAdapter<Event>(context, 0, events) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val event = getItem(position)

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.event_cell, parent, false)
        }

        val eventCellTV = itemView!!.findViewById<TextView>(R.id.eventCellTV)

        val eventTitle = "${event?.name} ${event?.time?.let { CalendarUtils.formattedTime(it) }}"
        eventCellTV.text = eventTitle

        return itemView
    }
}
