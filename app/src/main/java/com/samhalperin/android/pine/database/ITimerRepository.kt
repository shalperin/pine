package com.samhalperin.android.pine.database

import androidx.lifecycle.LiveData
import com.samhalperin.android.pine.entities.Timer

interface ITimerRepository {
    suspend fun newTimer(timer: Timer)

    fun currentTimer(): LiveData<Timer>

    suspend fun clearTimer()
}