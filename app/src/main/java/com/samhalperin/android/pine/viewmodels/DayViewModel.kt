package com.samhalperin.android.pine.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.samhalperin.android.pine.database.DayRepositoryRoomImpl
import com.samhalperin.android.pine.entities.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek

class DayViewModel(application: Application) : AndroidViewModel(application) {

    private val loadTrigger = MutableLiveData(Unit)

    fun refresh() {
        loadTrigger.value = Unit
    }

    private lateinit var repo: DayRepositoryRoomImpl

    init {
        repo = DayRepositoryRoomImpl(application)
    }


    fun logToday(behavior: Behavior) {
        viewModelScope.launch {
            repo.markToday(behavior)
        }
    }

    fun logYesterday(behavior: Behavior) {
        viewModelScope.launch {
            repo.markYesterday(behavior)
        }
    }

    fun populate() {
        viewModelScope.launch {
            repo.populateDatabase()
        }
    }

    val all = Transformations.switchMap(loadTrigger, {
        Transformations.map(repo.getAll(), { sparseList ->
            sparseList
                .endOnToday()
                .startOnDotw(DayOfWeek.SUNDAY)
                .makeDense()
        })
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





