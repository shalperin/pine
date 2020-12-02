package com.samhalperin.android.pine.database

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.samhalperin.android.pine.entities.Behavior
import com.samhalperin.android.pine.entities.Day
import com.samhalperin.android.pine.entities.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class DayRepositoryRoomImpl(application: Application): IDayDataRepository {

    val db = Room
        .databaseBuilder(application, AppDatabase::class.java, "app-database")
        .build()
    val dayDao = db.dayDao()
    val timerDao = db.timerDao()
    private val all: LiveData<List<Day>> = dayDao.getAll()


    private suspend fun insertOrUpdate(date: LocalDate, behavior: Behavior)  {
        withContext(Dispatchers.IO) {
            dayDao.insert(Day(date, behavior))
        }
    }

    override suspend fun markToday(behavior: Behavior) {
        insertOrUpdate(LocalDate.now(), behavior)
    }

    override suspend fun markYesterday(behavior: Behavior) {
        insertOrUpdate(LocalDate.now().minusDays(1), behavior)
    }

    override fun getAll(): LiveData<List<Day>> {
        return all
    }

    override  suspend fun populateDatabase() {
        withContext(Dispatchers.IO) {
            dayDao.deleteDatabase()
            val days = generateDummyData(1000)
            dayDao.insert(days)
        }
    }

    override  suspend fun deleteDatabase() {
        withContext(Dispatchers.IO) {
            dayDao.deleteDatabase()
        }
    }

    override fun today() = dayDao.today()


}

fun generateDummyData(n: Long): List<Day> {
    val behaviors = listOf(Behavior.FAILURE, Behavior.SUCCESS, Behavior.FASTED)
    val skipDays = listOf( 1L, 1L, 1L, 2L, 2L, 2L, 3L, 5L)
    val days = mutableListOf<Day>()
    var i = 0L
    while (i < n) {
        i += skipDays.shuffled().first()
        val d = LocalDate.now().minusDays(i)
        val behavior = behaviors.shuffled().first()
        days.add(Day(d, behavior))
    }
    return days
}
