package com.example.simplehabittracke

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
// import android.R
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_habit.*
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import java.util.*


class MainActivity : AppCompatActivity(), AddHabitFragment.OnFragmentInteractionListener {
    private lateinit var selectedDate: LocalDate
    private lateinit var today: LocalDate

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var habitDataList: MutableList<HabitData> = mutableListOf()
    private var habitCheckList: MutableList<HabitCheck> = mutableListOf()

    private val myDb: DualDatabaseHandler = DualDatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidThreeTen.init(this)

        presetData()
        setup()
        createRecyclerView()
        createBinder()

    }

    private fun presetData() {
//        myDb.dropAllTable(this)
//        myDb.generateDefaultValue()
        val testingArray = myDb.getAllHabitData()
        Log.d("d_tag", "testingArray: $testingArray")

        habitDataList.addAll(testingArray)

        val testingArray2 = myDb.getAllHabitCheck()
        Log.d("d_tag", "testingArray2: $testingArray2")

        habitCheckList.addAll(testingArray2)

    }

    private fun getHabitCheckForDate(date: LocalDate): MutableMap<Int, HabitCheck> {
        // do some filtering with
        val list = myDb.getHabitCheck(date)

        return list.associateBy { it.ref_key }.toMutableMap()
    }

    private fun createRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(habitDataList, getHabitCheckForDate(today), object : MyAdapter.OnItemClickListener {
            override fun onItemClick(habitCheckId: Int, c: Boolean, habitDataId: Int) {
                //                Toast.makeText(getContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                Log.d("d_Tag", "OnItemClickListener habit check id $habitCheckId and set checked $c")

                if (habitCheckId == 0) {
                    myDb.addHabitCheck(HabitCheck(0, selectedDate, c, habitDataId))
                } else
                    myDb.updateHabitCheck(habitCheckId, c)
//                val testing3 = myDb.getHabitCheck(id)
                Log.d("d_tag", "${myDb.getAllHabitCheck()}")
            }
        })

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        val button: Button = findViewById(R.id.addNewHabitButton)
        button.setOnClickListener {
            val fragment: AddHabitFragment = AddHabitFragment.newInstance("testing", "rubbish")
            val fragmentTran: FragmentTransaction = this.supportFragmentManager.beginTransaction()
            fragmentTran.add(R.id.fragment_container, fragment)
            fragmentTran.addToBackStack(null)
            fragmentTran.commit()
        }

    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createBinder() {
        createDayBinder()
        createMonthBinder()
    }

    private fun createDayBinder() {
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {

            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.bindData(day, View.OnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldSelectedDate = selectedDate
                            selectedDate = day.date
                            calendarView.notifyDateChanged(day.date)
                            oldSelectedDate?.let { calendarView.notifyDateChanged(it) }

                            // update adapter
                            updateAdapterForDate(day.date)
                        }
                    }
                })

                if (day.owner == DayOwner.THIS_MONTH) {
                    when (day.date) {
                        selectedDate -> {
                            container.setTextViewColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.selectedDateColor
                                )
                            )
                        }
                        today -> {
                            container.setTextViewColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.todayDateColor
                                )
                            )
                        }
                    }
                }
                // habit check fill in color

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
        selectedDate = today

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)
    }

    fun updateAdapterForDate(date: LocalDate) {
        val adapter = viewAdapter as? MyAdapter
        val newlist = getHabitCheckForDate(date)

        adapter?.dataSetChanged(newlist)
        adapter?.notifyDataSetChanged()
    }

//    fun createCalendarView() {
//        val simpleCalendarView = findViewById(R.id.calendarView) as CalendarView // get the reference of CalendarView
//        val selectedDate = simpleCalendarView.date // get selected date in milliseconds
//    }


}

