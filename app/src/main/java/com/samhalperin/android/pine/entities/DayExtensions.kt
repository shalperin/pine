package com.samhalperin.android.pine.entities

import java.lang.RuntimeException
import java.time.DayOfWeek
import java.time.LocalDate


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
                //Not sure about now() here...  Spacers dates get ignored
                // if you call these fns in the right order. ie last.
                retval.add(Day(LocalDate.now(), Behavior.SPACER))
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
fun List<Day>.startOnDotw(day:DayOfWeek): List<Day> {
    var retval = this

    if (isEmpty()) {
        throw RuntimeException("Should have called endOnToday on this list first.")
    }

    var d = first().date ?: throw RuntimeException("Spacer")

    while (d.dayOfWeek != day) {
        d = d.minusDays(1L)
        retval = listOf(Day(d, Behavior.NO_DATA)) + retval
    }

    return retval
}


fun List<Day>.startOnFirstOfMonth(): List<Day> {
    if (isEmpty()) {
        throw RuntimeException("Should have called endOnToday on this list first.")
    }
    var d = first().date?.withDayOfMonth(1) ?: throw RuntimeException("Spacer")
    return listOf(Day(d, Behavior.NO_DATA)) + this

}

/**
 * Takes a sparse list of Days, and makes it a dense list
 * by filling in with Behavior.NO_DATA days.
 */
fun List<Day>.makeDense(): List<Day> {
    if (size == 0) {return this}
    val retval = mutableListOf<Day>()
    var d = first().date ?: throw RuntimeException("Spacer")
    val endDate = last().date
    val m = map {it.date to it}.toMap()

    while (d <= endDate) {
        val newDate = m.get(d) ?: Day(d, Behavior.NO_DATA)
        retval.add(newDate)
        d = d.plusDays(1)
    }
    return retval
}

fun List<Day>.addLastDayOfLastMonth(): List<Day> {
    if (size == 0) {return this}
    val last = last().date ?: throw RuntimeException("Spacer")
    val d = last.withDayOfMonth(last.lengthOfMonth())
    return this + Day(d, Behavior.NO_DATA)
}