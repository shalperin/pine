package com.samhalperin.android.pine.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.samhalperin.android.pine.entities.Behavior
import com.samhalperin.android.pine.R
import com.samhalperin.android.pine.entities.Day
import com.samhalperin.android.pine.entities.separateMonthsWithSpacers
import kotlinx.android.synthetic.main.rvitem_viz_fasted.view.*
import java.lang.RuntimeException

class MainActivityVizAdapter : RecyclerView.Adapter<MainActivityVizAdapter.VH>() {
    private var data = listOf<Day>()

    inner class VH(val view: View): RecyclerView.ViewHolder(view) {

        fun bind(day: Day, today:Boolean = false) {

            if (day.dayOfMonth() == 1) {
                view.month_label.text = day.monthName(view.context)
            } else {
                view.month_label.text = ""
            }

            view.daynum_label.text = day.dayOfMonth().toString()
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (position == data.size - 1) {
            holder.bind(data.get(position), true)
        } else {
            holder.bind(data.get(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layoutToInflate =
        when (viewType) {
            0 -> R.layout.rvitem_viz_success
            1 -> R.layout.rvitem_viz_failure
            2 -> R.layout.rvitem_viz_fasted
            3 -> R.layout.rvitem_viz_nodata
            4 -> R.layout.rvitem_viz_success_today
            5 -> R.layout.rvitem_viz_failure_today
            6 -> R.layout.rvitem_viz_fasted_today
            7 -> R.layout.rvitem_viz_nodata_today
            8 -> R.layout.rvitem_viz_spacer

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
        val IS_TODAY = true
        val NOT_TODAY = false
        return when (Pair(data.get(position).behavior, position  == itemCount -1)) {
            Pair(Behavior.SUCCESS, NOT_TODAY) -> 0
            Pair(Behavior.FAILURE, NOT_TODAY) -> 1
            Pair(Behavior.FASTED, NOT_TODAY) -> 2
            Pair(Behavior.NO_DATA, NOT_TODAY) -> 3
            Pair(Behavior.SPACER, NOT_TODAY) -> 8
            Pair(Behavior.SUCCESS, IS_TODAY) -> 4
            Pair(Behavior.FAILURE, IS_TODAY) -> 5
            Pair(Behavior.FASTED, IS_TODAY) -> 6
            Pair(Behavior.NO_DATA, IS_TODAY) -> 7
            Pair(Behavior.SPACER, IS_TODAY) -> 8

            else -> throw RuntimeException("uh oh")
        }
    }

    fun update(data : List<Day>) {
        this.data = data.separateMonthsWithSpacers()
        notifyDataSetChanged()
    }
}