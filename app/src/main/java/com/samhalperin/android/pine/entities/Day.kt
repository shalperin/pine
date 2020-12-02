package com.samhalperin.android.pine.entities

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*


@Entity
data class Day(
    @PrimaryKey @ColumnInfo(name = "date") var date: LocalDate,
    @ColumnInfo(name = "behavior") val behavior: Behavior,
) {

    fun isFirstOfMonth():Boolean {
        return date.dayOfMonth == 1
    }

    fun dayOfMonth():Int {
        return date.dayOfMonth
    }

    fun monthName(context: Context) : String {
        val locale = Locale.getDefault()
        return date.getMonth().getDisplayName(TextStyle.SHORT, locale)

    }

    fun add(days:Long) {
        date = date.plusDays(days)
    }

    companion object {
        fun today(): Day {
            return Day(LocalDate.now(), Behavior.NO_DATA)
        }
    }
}


