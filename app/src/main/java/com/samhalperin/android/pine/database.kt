package com.samhalperin.android.pine

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

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

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun behaviorToString(behavior:Behavior?): String {
        return when(behavior) {
            Behavior.SUCCESS -> Behavior.SUCCESS.toString()
            Behavior.FAILURE -> Behavior.FAILURE.toString()
            Behavior.FASTED -> Behavior.FASTED.toString()
            else ->
                throw RuntimeException("Do not store any other behavior types in the database")

        }
    }

    @TypeConverter
    fun behaviorFromString(srep: String) : Behavior{
        return when (srep) {
             Behavior.SUCCESS.toString() -> Behavior.SUCCESS
            Behavior.FAILURE.toString() -> Behavior.FAILURE
            Behavior.FASTED.toString() -> Behavior.FASTED
            else -> throw RuntimeException("There shouldn't be any other behavior types in the database.")
        }
    }
}

@Database(entities = [Day::class], version = 2, exportSchema = false)  // TODO get schema export to work
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dayDao(): DayDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "update day set behavior = 'SUCCESS' where behavior = 'success'"
        )
        database.execSQL(
            "update day set behavior = 'FAILURE' where behavior = 'failure'"
        )
        database.execSQL(
            "update day set behavior = 'FASTED' where behavior = 'fasted'"
        )
    }
}


