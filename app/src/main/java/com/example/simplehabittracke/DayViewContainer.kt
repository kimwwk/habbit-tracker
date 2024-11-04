package com.example.simplehabittracke

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.OnItemActivatedListener
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.synthetic.main.calendar_day_layout.view.*

class DayViewContainer(view: View) : ViewContainer(view) {
    lateinit var day : CalendarDay
    private val textView = view.calendarDayText
    private val barView = view.view
    private val barView2 = view.view2

    internal fun setTextView(string: String){
        textView.text = string
    }

    internal fun setTextViewColor(color:Int){
        textView.setTextColor(color)
    }

    internal fun bindData(day: CalendarDay, listener: View.OnClickListener){
        this.day = day

        val dayStr = day.date.dayOfMonth.toString()
        setTextView(dayStr)

        // make outdate gray
        if (day.owner == DayOwner.THIS_MONTH) {
            setTextViewColor(Color.BLACK)
        } else {
            setTextViewColor(Color.GRAY)
        }

        // onclick listener
        view.setOnClickListener(listener)

    }

    // Without the kotlin android extensions plugin
    // val textView = view.findViewById<TextView>(R.id.calendarDayText)
}