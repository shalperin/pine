package com.samhalperin.android.pine

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class DayRepository(application: Application) {

    val dao = Room
        .databaseBuilder(application, AppDatabase::class.java, "app-database")
        .addMigrations(MIGRATION_1_2)
        .build()
        .dayDao()

    private suspend fun insertOrUpdate(date: Date, behavior: Behavior)  {
        withContext(Dispatchers.IO) {
            dao.insert(Day.Builder(date, behavior).build())
            val count = dao.count()
            Log.d("Repo", "count: $count")
        }
    }

    suspend fun markToday(behavior: Behavior) {
        val now = Date()
        insertOrUpdate(now, behavior)
    }

    suspend fun markYesterday(behavior: Behavior) {
        val c = Calendar.getInstance()
            .apply {
                add(Calendar.DATE, -1)
            }
        insertOrUpdate(c.time, behavior)
    }

    fun getAll(): LiveData<List<Day>> {
        return dao.getAll()
    }

    suspend fun populateDatabase() {
        withContext(Dispatchers.IO) {
            dao.deleteDatabase()
            val days = generateDummyData(100)
            dao.insert(days)
        }
    }

    suspend fun deleteDatabase() {
        withContext(Dispatchers.IO) {
            dao.deleteDatabase()
        }
    }

    fun today() = dao.today()
}

fun generateDummyData(n: Int): List<Day> {
    val behaviors = listOf(Behavior.FAILURE, Behavior.SUCCESS, Behavior.FASTED)
    val skipDays = listOf( 1, 1, 1, 2, 2, 2, 3, 5)
    val days = mutableListOf<Day>()
    var i = 0
    while (i < n) {
        i += skipDays.shuffled().first()
        val c = Calendar.getInstance()
            .apply {
                add(Calendar.DATE, -i)
            }
        val behavior = behaviors.shuffled().first()
        days.add(Day.Builder(c.time, behavior).build())
    }
    return days
}
