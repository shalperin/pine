package com.samhalperin.android.pine.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.samhalperin.android.pine.database.TimerRepositoryRoomImpl
import com.samhalperin.android.pine.entities.Timer
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    val repo = TimerRepositoryRoomImpl(application)

    fun currentTimer():LiveData<Timer> = repo.currentTimer()

    fun clearTimer() {
        viewModelScope.launch {
            repo.clearTimer()
        }
    }

    fun setTimer(timer: Timer) {
        viewModelScope.launch {
            repo.newTimer(timer)
        }
    }
}
