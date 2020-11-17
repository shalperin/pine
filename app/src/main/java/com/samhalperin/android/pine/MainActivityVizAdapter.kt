package com.samhalperin.android.pine

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rvitem_viz_fasted.view.*
import java.lang.RuntimeException

class MainActivityVizAdapter : RecyclerView.Adapter<MainActivityVizAdapter.VH>() {
    private var data = listOf<Day>()

    inner class VH(val view: View): RecyclerView.ViewHolder(view) {

        fun bind(day: Day, today:Boolean = false) {

            if (today) {
                view.findViewById<TextView>(R.id.daynum_label).setTextColor(
                    view.context.getColor(R.color.colorPrimary)
                )
            } else {
                view.findViewById<TextView>(R.id.daynum_label).setTextColor(Color.BLACK)
            }

            val dayOfMonth = day.dayOfMonth()
            if (dayOfMonth == 1) {
                view.month_label.text = day.monthName(view.context)
            } else {
                view.month_label.text = ""
            }

            if (day.behavior != Behavior.SPACER) {
                view.daynum_label.text = dayOfMonth.toString()
            } else {
                view.daynum_label.text = ""
            }


        }
    }

    override fun onBindViewHolder(holder: MainActivityVizAdapter.VH, position: Int) {
        if (position == data.size - 1) {
            holder.bind(data.get(position), true)
        } else {
            holder.bind(data.get(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivityVizAdapter.VH {
        val layoutToInflate =
        when (fromViewType(viewType)) {
            Behavior.SUCCESS -> R.layout.rvitem_viz_success
            Behavior.FAILURE -> R.layout.rvitem_viz_failure
            Behavior.FASTED -> R.layout.rvitem_viz_fasted
            Behavior.NO_DATA -> R.layout.rvitem_viz_nodata
            else -> throw RuntimeException("uh oh")
        }
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutToInflate, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (data.get(position).behavior) {
            Behavior.SUCCESS -> 0
            Behavior.FAILURE -> 1
            Behavior.FASTED -> 2
            Behavior.NO_DATA -> 3
            Behavior.SPACER -> 3
            else -> throw RuntimeException("uh oh")
        }
    }

    private fun fromViewType(i : Int) :Behavior{
        return when (i) {
            0 -> Behavior.SUCCESS
            1 -> Behavior.FAILURE
            2 -> Behavior.FASTED
            3 -> Behavior.NO_DATA
            else -> throw RuntimeException("uh oh")
        }
    }

    fun update(data : List<Day>) {
        this.data = data.separateMonthsWithSpacers()
        notifyDataSetChanged()
    }
}