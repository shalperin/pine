package com.samhalperin.android.pine

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.RuntimeException
import java.util.*


@Entity
data class Day(
    @PrimaryKey @ColumnInfo(name = "date") var date: Date,
    @ColumnInfo(name = "behavior") val behavior: Behavior,
) {

    fun isFirstOfMonth():Boolean {
        return date.dayOfMonth() == 1
    }

    fun dayOfMonth():Int {
        return date.dayOfMonth()
    }

    fun monthName(context: Context) : String {
        return date.monthName(context)
    }

    class Builder(var date: Date, val behavior: Behavior) {

        fun build(): Day {
            return Day(date.normalize(), behavior)
        }

        fun add(days: Int):Builder {
            date = date.addDaysToJavaUtilDate(days)
            return this
        }

    }
    companion object {
        fun today():Day {
            return Builder(Date(), Behavior.NO_DATA).build()
        }
        fun onDate(d: Date) :Day {
            return Builder(d, Behavior.NO_DATA).build()
        }

        fun onDate(c: Calendar): Day {
            return Builder(c.time, Behavior.NO_DATA).build()
        }
    }

}


