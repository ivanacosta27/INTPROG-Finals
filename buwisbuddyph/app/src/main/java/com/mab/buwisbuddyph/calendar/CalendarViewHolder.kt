package com.mab.buwisbuddyph.calendar

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mab.buwisbuddyph.R
import java.time.LocalDate

class CalendarViewHolder(
    itemView: View,
    private val onItemListener: CalendarAdapter.OnItemListener
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var date: LocalDate? = null
    val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(date: LocalDate) {
        this.date = date
        dayOfMonth.text = date.dayOfMonth.toString()
    }

    override fun onClick(view: View) {
        date?.let { onItemListener.onItemClick(adapterPosition, it) }
    }

}
