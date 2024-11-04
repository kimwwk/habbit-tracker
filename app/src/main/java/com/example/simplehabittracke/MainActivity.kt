package com.example.simplehabittracke

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
// import android.R
// import android.widget.CalendarView
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.legend_layout.*
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import java.util.*


class MainActivity : AppCompatActivity() {
    private var selectedDate: LocalDate? = null
    private var today: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidThreeTen.init(this)

        createBinder()
        setup()
    }

    fun createBinder() {
        createDayBinder()
        createMonthBinder()
    }

    private fun createDayBinder() {
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                val textView = container.textView
                textView.text = day.date.dayOfMonth.toString()

                // make outdate gray
                if (day.owner == DayOwner.THIS_MONTH) {
                    container.textView.setTextColor(Color.BLACK)
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }

                //
                if (day.owner == DayOwner.THIS_MONTH) {
//                    textView.makeVisible()
                    when (day.date) {
                        selectedDate -> {
                            textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.selectedDateColor))
//                            textView.setBackgroundResource(R.drawable.example_2_selected_bg)
                        }
                        today -> {
//                            textView.setTextColorRes(R.color.example_2_red)
                            textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.todayDateColor))
                            textView.background = null
                        }
                        else -> {
//                            textView.setTextColorRes(R.color.example_2_black)
                            textView.background = null
                        }
                    }
                } else {
//                    textView.makeInVisible()
                }

            }
        }
    }

    private fun createMonthBinder() {
        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = MonthViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.textView.text = month.yearMonth.month.toString();
            }

        }


    }

    private fun setup() {
        today = LocalDate.now()

//        val daysOfWeek = daysOfWeekFromLocale()
//        legendLayout.children.forEachIndexed { index, view ->
//            (view as TextView).apply {
//                text = daysOfWeek[index].name.first().toString()
////                setTextColorRes(R.color.example_2_white)
//            }
//        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)
    }

//    fun createCalendarView() {
//        val simpleCalendarView = findViewById(R.id.calendarView) as CalendarView // get the reference of CalendarView
//        val selectedDate = simpleCalendarView.date // get selected date in milliseconds
//    }


}

