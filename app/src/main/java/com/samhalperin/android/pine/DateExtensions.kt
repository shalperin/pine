package com.samhalperin.android.pine

import android.content.Context
import java.lang.RuntimeException
import java.util.*


/**
 * Zeroes out the time portion of a Date object.
 */
fun Date.normalize(): Date {
    val c = Calendar.getInstance().apply {
        setTime(this@normalize)
        set(Calendar.HOUR_OF_DAY, 0);
        set(Calendar.MINUTE, 0);
        set(Calendar.SECOND, 0);
        set(Calendar.MILLISECOND, 0)
    }

    return c.time
}

/**
 * Returns the month name for this date.
 */
fun Date.monthName(context: Context):String {
    val c = Calendar.getInstance()
    c.time = this
    return when (c.get(Calendar.MONTH)) {
        Calendar.JANUARY -> context.getString(R.string.january)
        Calendar.FEBRUARY -> context.getString(R.string.february)
        Calendar.MARCH -> context.getString(R.string.march)
        Calendar.APRIL -> context.getString(R.string.april)
        Calendar.MAY -> context.getString(R.string.may)
        Calendar.JUNE -> context.getString(R.string.june)
        Calendar.JULY -> context.getString(R.string.july)
        Calendar.AUGUST -> context.getString(R.string.august)
        Calendar.SEPTEMBER -> context.getString(R.string.september)
        Calendar.OCTOBER -> context.getString(R.string.october)
        Calendar.NOVEMBER -> context.getString(R.string.november)
        Calendar.DECEMBER -> context.getString(R.string.december)
        else -> throw RuntimeException("uh oh")
    }
}

fun Date.dayOfMonth():Int {
    val c = Calendar.getInstance()
    c.time=this
    return c.get(Calendar.DAY_OF_MONTH)
}



fun Date.addDaysToJavaUtilDate(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DATE, days)
    return calendar.time
}