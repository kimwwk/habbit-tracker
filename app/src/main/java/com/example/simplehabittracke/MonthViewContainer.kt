package com.example.simplehabittracke


import android.view.View
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.synthetic.main.calendar_month_layout.view.*

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = view.calendarMonthText

    // Without the kotlin android extensions plugin
    // val textView = view.findViewById<TextView>(R.id.calendarDayText)
}