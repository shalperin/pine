package com.samhalperin.android.pine.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.samhalperin.android.pine.entities.Behavior
import com.samhalperin.android.pine.activities.MainActivityVizAdapter
import com.samhalperin.android.pine.R
import com.samhalperin.android.pine.databinding.FragmentCalendarBinding
import com.samhalperin.android.pine.viewmodels.DayViewModel

class CalendarFragment : Fragment() {
    private val model : DayViewModel by activityViewModels()
    private lateinit var layoutManager: GridLayoutManager
    private val adapter = MainActivityVizAdapter()
    private lateinit var fabs : List<View>
    private lateinit var binding : FragmentCalendarBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabs = listOf(binding.fastFab, binding.successFab, binding.failFab)

        binding.fastFab.setOnClickListener{
            hideFabs()
            fastToday()
        }
        binding.successFab.setOnClickListener{
            hideFabs()
            successToday()
        }
        binding.failFab.setOnClickListener{
            hideFabs()
            failToday()
        }
        binding.showhideFab.setOnClickListener{
            var currentlyVisible = binding.fastFab.visibility == View.VISIBLE
            if (currentlyVisible) { hideFabs() }
            else { showFabs() }
        }


        layoutManager = GridLayoutManager(requireContext(), 7)
        binding.vizRv.adapter = adapter
        binding.vizRv.layoutManager = layoutManager

        model.all.observe(viewLifecycleOwner, Observer {
            adapter.update(it)
            layoutManager.scrollToPosition(adapter.itemCount - 1)
        })

        model.statsPercentage(7).observe(viewLifecycleOwner, Observer {percentageStats ->
            pieChart(
                binding.last7dayStats.pieChart,
                binding.last7dayStats.pieChartPlaceholder,
                percentageStats
            )
            binding.last7dayStats.last7FailurePercent.text =
                "${percentageStats[Behavior.FAILURE]}%"
            binding.last7dayStats.last7SuccessPercent.text =
                "${percentageStats[Behavior.SUCCESS]}%"
            binding.last7dayStats.last7FastedPercent.text =
                "${percentageStats[Behavior.FASTED]}%"
        })

        model.statsPercentage(30).observe(viewLifecycleOwner, Observer {percentageStats->
            pieChart(
                binding.last30dayStats.pieChart,
                binding.last30dayStats.pieChartPlaceholder,
                percentageStats
            )
            binding.last30dayStats.last30FailurePercent.text =
                "${percentageStats[Behavior.FAILURE]}%"
            binding.last30dayStats.last30SuccessPercent.text =
                "${percentageStats[Behavior.SUCCESS]}%"
            binding.last30dayStats.last30FastedPercent.text =
                "${percentageStats[Behavior.FASTED]}%"
        })

        model.statsCount(7).observe(viewLifecycleOwner, Observer {countStats->
            binding.last7dayStats.last7FailureCount.text =
                "${countStats[Behavior.FAILURE]}"
            binding.last7dayStats.last7SuccessCount.text =
                "${countStats[Behavior.SUCCESS]}"
            binding.last7dayStats.last7FastedCount.text =
                "${countStats[Behavior.FASTED]}"
        })

        model.statsCount(30).observe(viewLifecycleOwner, Observer {countStats->
            binding.last30dayStats.last30FailureCount.text =
                "${countStats[Behavior.FAILURE]}"
            binding.last30dayStats.last30SuccessCount.text =
                "${countStats[Behavior.SUCCESS]}"
            binding.last30dayStats.last30FastedCount.text =
                "${countStats[Behavior.FASTED]}"
        })

    }

    private fun hideFabs() {
        fabs.forEach {
            it.visibility = View.INVISIBLE
        }
    }

    private fun showFabs() {
        fabs.forEach {
            it.visibility = View.VISIBLE
        }
    }

    private fun successToday() {
        model.logToday(Behavior.SUCCESS)
        Toast.makeText(requireContext(), R.string.success_feedback, Toast.LENGTH_SHORT).show()
    }

    private fun failToday() {
        model.logToday(Behavior.FAILURE)
        Toast.makeText(requireContext(), R.string.failure_feedback, Toast.LENGTH_SHORT).show()
    }

    private fun fastToday() {
        model.logToday(Behavior.FASTED)
        Toast.makeText(requireContext(), R.string.fasted_feedback, Toast.LENGTH_SHORT).show()
    }




    private fun pieChart(chart: PieChart, placeholder: View, statsPercent: Map<Behavior, Int>) {

        if (statsPercent.values.sum() == 0) {
            placeholder.visibility = View.VISIBLE
            chart.visibility = View.GONE
        } else {
            placeholder.visibility = View.GONE
            chart.visibility = View.VISIBLE

            val yEntries = listOf(
                PieEntry(statsPercent[Behavior.SUCCESS]!!.toFloat(), ""),
                PieEntry(statsPercent[Behavior.FASTED]!!.toFloat(), ""),
                PieEntry(statsPercent[Behavior.FAILURE]!!.toFloat(), ""),
            )

            val data = PieDataSet(yEntries, "").apply {
                colors = listOf(
                    ContextCompat.getColor(requireContext(), R.color.success),
                    ContextCompat.getColor(requireContext(), R.color.fasted),
                    ContextCompat.getColor(requireContext(), R.color.failure)
                )
            }

            data.setDrawValues(false)
            chart.setDrawHoleEnabled(true);
            chart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.statsBg))

            chart.setTransparentCircleColor(ContextCompat.getColor(requireContext(),
                R.color.statsBg
            ))
            chart.setTransparentCircleAlpha(255)
            chart.setUsePercentValues(true);
            chart.getLegend().setEnabled(false)
            chart.getDescription().setEnabled(false)
            chart.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.statsBg))
            chart.data = PieData(data)
            chart.invalidate()
        }
    }






    companion object {

    }
}