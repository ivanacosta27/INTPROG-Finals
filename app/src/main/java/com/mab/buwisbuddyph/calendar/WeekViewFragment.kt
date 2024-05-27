package com.mab.buwisbuddyph.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.calendar.CalendarUtils.daysInWeekArray
import com.mab.buwisbuddyph.calendar.CalendarUtils.monthYearFromDate
import java.time.LocalDate
import java.util.Calendar

class WeekViewFragment : Fragment(), CalendarAdapter.OnItemListener {
    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var eventListView: ListView

    companion object {
        private const val SELECTED_DATE = "selected_date"

        fun newInstance(selectedDate: Calendar): WeekViewFragment {
            val fragment = WeekViewFragment()
            val args = Bundle()
            args.putSerializable(SELECTED_DATE, selectedDate)
            fragment.arguments = args
            return fragment
        }
    }

    private var selectedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = it.getSerializable(SELECTED_DATE) as? LocalDate
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_week_view, container, false)
        initWidgets(view)
        setWeekView()
        return view
    }

    private fun initWidgets(view: View) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        monthYearText = view.findViewById(R.id.monthYearTV)
        eventListView = view.findViewById(R.id.eventListView)

        val previousWeekAction: Button = view.findViewById(R.id.previousWeekAction)
        previousWeekAction.setOnClickListener{
            previousWeekAction()
        }

        val nextWeekAction: Button = view.findViewById(R.id.nextWeekAction)
        nextWeekAction.setOnClickListener{
            nextWeekAction()
        }

        val newEventAction: Button = view.findViewById(R.id.newEventAction)
        newEventAction.setOnClickListener{
            newEventAction()
        }
    }

    private fun setWeekView() {
        if (selectedDate != null) {
            monthYearText.text = monthYearFromDate(selectedDate!!)
            val days = daysInWeekArray(selectedDate!!)
            val calendarAdapter = CalendarAdapter(ArrayList(days), this)
            calendarRecyclerView.adapter = calendarAdapter
            val layoutManager = GridLayoutManager(requireContext(), 7)
            calendarRecyclerView.layoutManager = layoutManager
            setEventAdapter()
        } else {
            // If selected date is null, initialize it with the current date
            selectedDate = LocalDate.now()
            setWeekView() // Call setWeekView() again to populate the view with the current date
        }
    }

    private fun previousWeekAction() {
        selectedDate = selectedDate?.minusWeeks(1)
        setWeekView()
    }

    private fun nextWeekAction() {
        selectedDate = selectedDate?.plusWeeks(1)
        setWeekView()
    }

    private fun setEventAdapter() {
        val dailyEvents = Event.eventsForDate(selectedDate!!)
        val eventAdapter = EventAdapter(requireContext(), dailyEvents)
        eventListView.adapter = eventAdapter
    }

    override fun onItemClick(position: Int, date: LocalDate) {
        selectedDate = date
        setWeekView()
    }

    private fun newEventAction() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, EventFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
