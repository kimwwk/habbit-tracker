package com.example.simplehabittracke

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.threeten.bp.LocalDate


class MyAdapter(private val dataList: MutableList<HabitData>,
                private val checklistForDate: MutableMap<Int, HabitCheck>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(private val textView: TextView, private val outerItem : GradientDrawable)
        : RecyclerView.ViewHolder(textView){
        private var color = Color.TRANSPARENT
        private val defaultColor = Color.TRANSPARENT
        private var checked: Boolean = false

        fun bindData(data: HabitData, boolean: Boolean){
            textView.text = data.name
            outerItem.setStroke(4, data.color)
            checked = boolean
            color = data.color

            changeColor()
        }

        private fun changeColor(){
            Log.d("d_Tag","MyAdapter is checked/unchecked")
            color?.run {
                if (checked )
                    outerItem.setColor(this)
                else
                    outerItem.setColor(defaultColor)
            }

        }

        fun bindListener(item: Int, listener: OnItemClickListener, pos:Int) {
            itemView.setOnClickListener {
                checked = !checked
                changeColor()
                listener.onItemClick(item, checked, pos)
            }
        }
    }




    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view, parent, false) as TextView
        // set the view's size, margins, paddings and layout parameters
        val drawable = textView.background as LayerDrawable
        val innerItem = drawable.findDrawableByLayerId(R.id.layer_list_solid_item) as GradientDrawable
        val outerItem = drawable.findDrawableByLayerId(R.id.layer_list_stroke_item) as GradientDrawable

        return MyViewHolder(textView, outerItem)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.textView.text = myDataset[position]
        Log.d("d_Tag", "myAdapter.onBindViewHolder run...")
        Log.d("d_Tag", "checklistForDate: $checklistForDate")
        val data = dataList[position]

        var bool = false
        var id = 0
        val isItemExisted = checklistForDate.containsKey(position+1)
        if (isItemExisted){
            val habitCheckItem = checklistForDate.getValue(position+1)
            habitCheckItem?.run {
                bool = this.checked
                id = this.id
            }
        }

        holder.bindData(data, bool)
        holder.bindListener(id, listener, position+1)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataList.size

    override fun getItemId(position: Int): Long = position.toLong()

    fun dataSetChanged(data : MutableMap<Int, HabitCheck>){
        checklistForDate.clear()
        checklistForDate.putAll(data)
    }

    interface OnItemClickListener{
        fun onItemClick(item: Int, checked:Boolean, pos :Int)
    }
}