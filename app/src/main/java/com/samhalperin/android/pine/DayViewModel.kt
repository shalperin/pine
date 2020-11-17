package com.samhalperin.android.pine

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import java.util.*

class DayViewModel(application: Application) : AndroidViewModel(application) {

    val repo = DayRepository(application)

    fun logToday(behavior: Behavior) {
        viewModelScope.launch {
            repo.markToday(behavior)
        }
    }

    fun logYesterday(behavior:Behavior) {
        viewModelScope.launch {
            repo.markYesterday(behavior)
        }
    }

    fun populate() {
        viewModelScope.launch {
            repo.populateDatabase()
        }
    }

    val all = Transformations.map(repo.getAll(), { sparseList ->
        sparseList
            .endOnToday()
            .startOnFirstOfMonth()
            .addLastDayOfLastMonth()
            .makeDense()
    })


    fun deleteDatabase() {
        viewModelScope.launch {
            repo.deleteDatabase()
        }
    }

    fun statsPercentage(days: Int): LiveData<Map<Behavior, Int>> {
        return Transformations.map(all, {
            mapOf(
                Behavior.SUCCESS to it.behaviorPercent(days, Behavior.SUCCESS),
                Behavior.FAILURE to it.behaviorPercent(days, Behavior.FAILURE),
                Behavior.FASTED to it.behaviorPercent(days, Behavior.FASTED),
            )
        })
    }

    fun statsCount(days:Int): LiveData<Map<Behavior, Int>> {
        return Transformations.map(all, {
            mapOf(
                Behavior.SUCCESS to it.behaviorCount(days, Behavior.SUCCESS),
                Behavior.FAILURE to it.behaviorCount(days, Behavior.FAILURE),
                Behavior.FASTED to it.behaviorCount(days, Behavior.FASTED),
            )
        })
    }

    fun today(): LiveData<Day> {
        return repo.today()
    }


}





//TODO: I would really like Behavior not to be a string.
