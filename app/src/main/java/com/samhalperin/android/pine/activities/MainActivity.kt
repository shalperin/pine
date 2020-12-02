package com.samhalperin.android.pine.activities

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.samhalperin.android.pine.entities.Behavior
import com.samhalperin.android.pine.R
import com.samhalperin.android.pine.databinding.ActivityMainBinding
import com.samhalperin.android.pine.viewmodels.DayViewModel
import kotlinx.android.synthetic.main.dialog_yesterday.*


class MainActivity : AppCompatActivity() {
    private val model : DayViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

     setSupportActionBar(findViewById(R.id.toolbar2))

        intent.extras
            ?.getString("shortcut")
            ?.let { shortcut ->
                when(shortcut) {
                    Behavior.FAILURE.toString() -> model.logToday(Behavior.FAILURE)
                    Behavior.SUCCESS.toString() -> model.logToday(Behavior.SUCCESS)
                    Behavior.FASTED.toString() -> model.logToday(Behavior.FASTED)
                    else -> throw RuntimeException("uh oh")
                }
            }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
        model.refresh()

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
            R.id.change_previous -> changeYesterday()
            R.id.populate_database -> populateDatabase()
            R.id.timer -> navigateToTimer()
        }
        return true
    }


    private fun navigateToTimer() {
        navController.navigate(R.id.action_calendarFragment_to_timerFragment)
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


    private fun changeYesterday() {
        val dialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(R.layout.dialog_yesterday)
        }
        dialog.success_fab.setOnClickListener{
            model.logYesterday(Behavior.SUCCESS)
            dialog.hide()
        }
        dialog.fail_fab.setOnClickListener{
            model.logYesterday(Behavior.FAILURE)
            dialog.hide()
        }
        dialog.fast_fab.setOnClickListener{
            model.logYesterday(Behavior.FASTED)
            dialog.hide()
        }
        dialog.show()
    }

}