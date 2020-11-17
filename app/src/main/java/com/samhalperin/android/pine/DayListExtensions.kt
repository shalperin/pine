package com.samhalperin.android.pine

import com.samhalperin.android.pine.Day.Companion.onDate
import java.lang.RuntimeException
import java.util.*


/**
 * Return the count of behaviors in the last n days.
 *
 * @param days Days
 * @param behavior a behavior
 */
fun List<Day>.behaviorCount(days: Int, behavior: Behavior) :Int {
    return takeLast(days).filter { day ->
        day.behavior == behavior
    }.size
}

/**
 * Return the percentage of behavior days over the last n days.
 *
 * Note there is a convention at play here:  take(7) might return less than
 * 7 objects.  The percentage is not calculated by score / 7, but by score / take(7).size
 * @param days The number to take
 * @behavior The behevior to count
 */
fun List<Day>.behaviorPercent(days: Int, behavior: Behavior): Int {
    return percent(days, behaviorCount(days, behavior))
}

fun percent(total: Int, score: Int):Int {
    if (total == 0) {return 0}
    return Math.ceil(score.toDouble() / total.toDouble() * 100.0).toInt()
}


/**
 * Pads the end of the list so that the last day is always today.
 * Pad cells will have Behavior.NO_DATA
 */
fun List<Day>.endOnToday(): List<Day>  {
    if (isEmpty()) {
        return listOf(Day.today())
    } else if (last().date != Day.today().date) {
        return this + Day.today()
    } else {
        return this
    }
}

fun List<Day>.separateMonthsWithSpacers(): List<Day> {
    val retval = mutableListOf<Day>()
    forEachIndexed{i, day ->
        if (i==0) {
            retval.add(day)
        } else if (day.isFirstOfMonth()) {
            for (i in 0..6) {
                retval.add(Day(Date(), Behavior.SPACER))

            }
            retval.add(day)
        } else {
            retval.add(day)
        }
    }
    return retval
}


/**
 * Pads the front of the list so that it always starts on the day specified.
 * Padded cells will have Behavior.NO_DATA
 * @param day e.g: Calendar.MONDAY, Calendar.TUESDAY...etc
 */
fun List<Day>.startOnDotw(day:Int): List<Day> {
    var retval = this

    if (isEmpty()) {
        throw RuntimeException("Should have called endOnToday on this list first.")
    }

    val c = Calendar.getInstance()
    c.time = first().date

    while (c.get(Calendar.DAY_OF_WEEK) != day) {
        c.add(Calendar.DATE, -1)
        retval = listOf(Day.Builder(c.time, Behavior.NO_DATA).build()) + retval
    }

    return retval

}


fun List<Day>.startOnFirstOfMonth(): List<Day> {
    if (isEmpty()) {
        throw RuntimeException("Should have called endOnToday on this list first.")
    }
    val c = Calendar.getInstance()
    c.time = first().date
    c.set(Calendar.DAY_OF_MONTH, 1)
    val d0 = Day.onDate(c.time)

    return listOf(d0) + this

}

/**
 * Takes a sparse list of Days, and makes it a dense list
 * by filling in with Behavior.NO_DATA days.
 */
fun List<Day>.makeDense(): List<Day> {
    if (size == 0) {return this}
    val retval = mutableListOf<Day>()
    val c = Calendar.getInstance()
    val startDate = first().date
    val endDate = last().date
    c.time = startDate
    val m = map {it.date to it}.toMap()

    while (c.time <= endDate) {
        val newDate = m.get(c.time) ?: Day.Builder(c.time, Behavior.NO_DATA).build()
        retval.add(newDate)
        c.add(Calendar.DATE, 1)
    }
    return retval
}

fun List<Day>.addLastDayOfLastMonth(): List<Day> {
    if (size == 0) {return this}
    val c = Calendar.getInstance()
    c.time = last().date
    c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
    return this + Day.onDate(c)
}
