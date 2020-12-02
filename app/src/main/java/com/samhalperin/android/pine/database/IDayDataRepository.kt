package com.samhalperin.android.pine.database

import androidx.lifecycle.LiveData
import com.samhalperin.android.pine.entities.Behavior
import com.samhalperin.android.pine.entities.Day
import com.samhalperin.android.pine.entities.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

interface IDayDataRepository {
    suspend fun markToday(behavior: Behavior)

    suspend fun markYesterday(behavior: Behavior)

    fun getAll(): LiveData<List<Day>>

    suspend fun populateDatabase()

    suspend fun deleteDatabase()

    fun today(): LiveData<Day>


}