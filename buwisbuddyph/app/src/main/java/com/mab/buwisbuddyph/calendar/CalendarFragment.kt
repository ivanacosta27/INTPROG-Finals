package com.mab.buwisbuddyph.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mab.buwisbuddyph.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment(), CalendarAdapter.OnItemListener {

    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var selectedDate: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        initWidgets(view)
        selectedDate = Calendar.getInstance()
        setMonthView()

        val weeklyButton: Button = view.findViewById(R.id.weeklyButton)
        weeklyButton.setOnClickListener {
            openWeeklyViewFragment(selectedDate)
        }


        val previousMonthButton: Button = view.findViewById(R.id.previousMonthButton)
        previousMonthButton.setOnClickListener {
            previousMonthAction()
        }

        val nextMonthButton: Button = view.findViewById(R.id.nextMonthButton)
        nextMonthButton.setOnClickListener {
            nextMonthAction()
        }
        return view
    }


    private fun openWeeklyViewFragment(selectedDate: Calendar) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val weekViewFragment = WeekViewFragment.newInstance(selectedDate)
        fragmentTransaction.replace(R.id.frameLayout, weekViewFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }



    private fun initWidgets(view: View) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        monthYearText = view.findViewById(R.id.monthYearTV)
    }

    private fun setMonthView() {
        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)

        val calendarAdapter = CalendarAdapter(daysInMonth, this)
        val layoutManager = GridLayoutManager(requireContext(), 7)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun daysInMonthArray(date: Calendar): ArrayList<LocalDate> {
        val daysInMonthArray = ArrayList<LocalDate>()
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH)
        val daysInMonth = getDaysInMonth(year, month)
        val firstOfMonth = Calendar.getInstance()
        firstOfMonth.set(year, month, 1)
        val dayOfWeek = firstOfMonth.get(Calendar.DAY_OF_WEEK)

        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(LocalDate.MIN)
            } else {
                daysInMonthArray.add(LocalDate.of(year, month + 1, i - dayOfWeek))
            }
        }
        return daysInMonthArray
    }

    private fun monthYearFromDate(date: Calendar): String {
        val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return formatter.format(date.time)
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun previousMonthAction() {
        selectedDate.add(Calendar.MONTH, -1)
        setMonthView()
    }

    private fun nextMonthAction() {
        selectedDate.add(Calendar.MONTH, 1)
        setMonthView()
    }

    override fun onItemClick(position: Int, date: LocalDate) {
        if (date != LocalDate.MIN) {
            val message = "Selected Date: $date"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

}
