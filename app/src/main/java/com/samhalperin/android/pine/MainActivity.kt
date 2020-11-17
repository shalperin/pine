package com.samhalperin.android.pine

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.samhalperin.android.pine.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    val model : DayViewModel by viewModels()
    val layoutManager = GridLayoutManager(this, 7)
    val adapter = MainActivityVizAdapter()
    lateinit var fabs : List<View>
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        intent.extras
            ?.getString("shortcut")
            ?.let { shortcut ->
                when(shortcut) {
                    Behavior.FAILURE.toString() -> failToday()
                    Behavior.SUCCESS.toString() -> successToday()
                    Behavior.FASTED.toString() -> fastToday()
                    else -> throw RuntimeException("uh oh")
                }
            }


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


        binding.contentMain.vizRv.adapter = adapter
        binding.contentMain.vizRv.layoutManager = layoutManager

        model.all.observe(this, Observer {
            adapter.update(it)
            layoutManager.scrollToPosition(adapter.itemCount - 1)
        })

        model.statsPercentage(7).observe(this, Observer {percentageStats ->
            pieChart(
                binding.contentMain.last7dayStats.pieChart,
                binding.contentMain.last7dayStats.pieChartPlaceholder,
                percentageStats
            )
            binding.contentMain.last7dayStats.last7FailurePercent.text =
                "${percentageStats[Behavior.FAILURE]}%"
            binding.contentMain.last7dayStats.last7SuccessPercent.text =
                "${percentageStats[Behavior.SUCCESS]}%"
            binding.contentMain.last7dayStats.last7FastedPercent.text =
                "${percentageStats[Behavior.FASTED]}%"
        })

        model.statsPercentage(30).observe(this, Observer {percentageStats->
            pieChart(
                binding.contentMain.last30dayStats.pieChart,
                binding.contentMain.last30dayStats.pieChartPlaceholder,
                percentageStats
            )
            binding.contentMain.last30dayStats.last30FailurePercent.text =
                "${percentageStats[Behavior.FAILURE]}%"
            binding.contentMain.last30dayStats.last30SuccessPercent.text =
                "${percentageStats[Behavior.SUCCESS]}%"
            binding.contentMain.last30dayStats.last30FastedPercent.text =
                "${percentageStats[Behavior.FASTED]}%"
        })

        model.statsCount(7).observe(this, Observer {countStats->
            binding.contentMain.last7dayStats.last7FailureCount.text =
                "${countStats[Behavior.FAILURE]}"
            binding.contentMain.last7dayStats.last7SuccessCount.text =
                "${countStats[Behavior.SUCCESS]}"
            binding.contentMain.last7dayStats.last7FastedCount.text =
                "${countStats[Behavior.FASTED]}"
        })

        model.statsCount(30).observe(this, Observer {countStats->
            binding.contentMain.last30dayStats.last30FailureCount.text =
                "${countStats[Behavior.FAILURE]}"
            binding.contentMain.last30dayStats.last30SuccessCount.text =
                "${countStats[Behavior.SUCCESS]}"
            binding.contentMain.last30dayStats.last30FastedCount.text =
                "${countStats[Behavior.FASTED]}"
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.delete_database -> deleteDatabase()
            R.id.change_yesterday -> changeYesterday()
            R.id.populate_database -> populateDatabase()
        }
        return true
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
        Toast.makeText(this, R.string.success_feedback, Toast.LENGTH_SHORT).show()
    }

    private fun failToday() {
        model.logToday(Behavior.FAILURE)
        Toast.makeText(this, R.string.failure_feedback, Toast.LENGTH_SHORT).show()
    }

    private fun fastToday() {
        model.logToday(Behavior.FASTED)
        Toast.makeText(this, R.string.fasted_feedback, Toast.LENGTH_SHORT).show()
    }

    private fun deleteDatabase() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to delete the whole database?")
            .setPositiveButton(
                "I'm sure",
                { _, _ -> model.deleteDatabase() })
            .setNegativeButton(
                "No, don't do that!",
                { _, _ -> /*no-op*/})
            .show()
    }

    private fun populateDatabase() {
        AlertDialog.Builder(this)
            .setMessage("If you have real data, this will spam your database with mock values, do it?")
            .setPositiveButton(
                "Yes, do it!",
                { _, _ -> model.populate() })
            .setNegativeButton(
                "No, don't do that!",
                { _, _ -> /*no op*/})
            .show()
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
                    ContextCompat.getColor(this@MainActivity, R.color.success),
                    ContextCompat.getColor(this@MainActivity, R.color.fasted),
                    ContextCompat.getColor(this@MainActivity, R.color.failure)
                )
            }

            data.setDrawValues(false)
            chart.setDrawHoleEnabled(true);
            chart.setHoleColor(ContextCompat.getColor(this, R.color.statsBg))

            chart.setTransparentCircleColor(ContextCompat.getColor(this, R.color.statsBg))
            chart.setTransparentCircleAlpha(255)
            chart.setUsePercentValues(true);
            chart.getLegend().setEnabled(false)
            chart.getDescription().setEnabled(false)
            chart.setBackgroundColor(ContextCompat.getColor(this, R.color.statsBg))
            chart.data = PieData(data)
            chart.invalidate()
        }
    }


    private fun changeYesterday() {

    }
}