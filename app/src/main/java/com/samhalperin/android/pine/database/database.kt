package com.samhalperin.android.pine.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.samhalperin.android.pine.entities.Behavior
import com.samhalperin.android.pine.entities.Day
import com.samhalperin.android.pine.entities.Timer
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface DayDao {
    @Query("SELECT * FROM day ORDER BY date ASC")
    fun getAll(): LiveData<List<Day>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(day: Day)

    @Query ("SELECT COUNT(*) FROM day")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(days : List<Day>)

    @Query("DELETE FROM day")
    suspend fun deleteDatabase()

    @Query("SELECT * from day ORDER BY date DESC LIMIT 1")
    fun today() : LiveData<Day>

}

@Dao
interface TimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: Timer)

    @Query("DELETE from timer")
    suspend fun clearTimers()

    @Query("SELECT * from timer LIMIT 1")
    fun currentTimer(): LiveData<Timer>
}

class Converters {
    @TypeConverter
    fun behaviorToString(behavior: Behavior?): String {
        return when(behavior) {
            Behavior.SUCCESS -> Behavior.SUCCESS.toString()
            Behavior.FAILURE -> Behavior.FAILURE.toString()
            Behavior.FASTED -> Behavior.FASTED.toString()
            else ->
                throw RuntimeException("Do not store any other behavior types in the database")

        }
    }

    @TypeConverter
    fun behaviorFromString(srep: String) : Behavior {
        return when (srep) {
             Behavior.SUCCESS.toString() -> Behavior.SUCCESS
            Behavior.FAILURE.toString() -> Behavior.FAILURE
            Behavior.FASTED.toString() -> Behavior.FASTED
            else -> throw RuntimeException("There shouldn't be any other behavior types in the database.")
        }
    }

    @TypeConverter
    fun localDateToString(l: LocalDate) : String {
        return l.toString()
    }

    @TypeConverter
    fun  stringToLocalDate(isoString: String) : LocalDate {
        return LocalDate.parse(isoString)
    }

    @TypeConverter
    fun localDateTimeToString(l: LocalDateTime) : String {
        return l.toString()
    }

    @TypeConverter
    fun stringToLocalDateTime(srep: String): LocalDateTime {
        return LocalDateTime.parse(srep)
    }
}

@Database(entities = [Day::class, Timer::class], version = 1, exportSchema = false)  // TODO get schema export to work
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dayDao(): DayDao
    abstract fun timerDao(): TimerDao
}


