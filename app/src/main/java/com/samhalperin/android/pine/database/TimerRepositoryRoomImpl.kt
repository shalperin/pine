package com.samhalperin.android.pine.database

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.samhalperin.android.pine.entities.Timer

class TimerRepositoryRoomImpl(application: Application): ITimerRepository {
    val dao = Room
        .databaseBuilder(application, AppDatabase::class.java, "app-database")
        .build().timerDao()

    override  suspend fun newTimer(timer: Timer) {
        dao.clearTimers()
        dao.insert(timer)
    }

    override  fun currentTimer(): LiveData<Timer> = dao.currentTimer()

    override suspend fun clearTimer() {
        dao.clearTimers()
    }
}