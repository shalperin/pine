package com.samhalperin.android.pine

import com.samhalperin.android.pine.entities.Behavior
import com.samhalperin.android.pine.entities.Day
import com.samhalperin.android.pine.entities.addLastDayOfLastMonth
import com.samhalperin.android.pine.entities.makeDense
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

class DateMath {

    /*
    Note: I use a convention of forwarding private myfunc() to Test_myfunc() so that it can
    be tested.  This isn't great, but it works as long as it doesn't confuse maintainers.
     */


    // one element list
    @Test
    fun testMakeDense1() {
        val startDate = LocalDate.now()
        val sparse = listOf(
            Day(startDate, Behavior.FAILURE)
        )
        val dense = sparse.makeDense()
        assertEquals(sparse, dense)
    }

    // two adjacent dates
    @Test
    fun testMakeDense2() {
        val startDate = LocalDate.now()
        val sparse = listOf(
            Day(startDate, Behavior.FAILURE),
            Day(startDate.plusDays(1), Behavior.FAILURE)
        )
        val dense = sparse.makeDense()
        assertEquals(sparse, dense)
    }

    //2 dates, skipping a day
    @Test
    fun testMakeDense3() {
        val startDate = LocalDate.now()
        val sparse = listOf(
            Day(startDate, Behavior.FAILURE),
            Day(startDate.plusDays(2), Behavior.SUCCESS)
        )
        val dense = sparse.makeDense()
        val answer = listOf(
            Day(startDate, Behavior.FAILURE),
            Day(startDate.plusDays(1), Behavior.NO_DATA),
            Day(startDate.plusDays(2), Behavior.SUCCESS)
        )
        assertEquals(dense, answer)
    }

    //more dates
    @Test
    fun testMakeDense4() {
        val startDate = LocalDate.now()
        val sparse = listOf(
            Day(startDate, Behavior.FAILURE),
            Day(startDate.plusDays(2), Behavior.SUCCESS),
            Day(startDate.plusDays(4), Behavior.SUCCESS)
        )
        val dense = sparse.makeDense()
        val expected = listOf(
            Day(startDate, Behavior.FAILURE),
            Day(startDate.plusDays(1), Behavior.NO_DATA),
            Day(startDate.plusDays(2), Behavior.SUCCESS),
            Day(startDate.plusDays(3), Behavior.NO_DATA),
            Day(startDate.plusDays(4), Behavior.SUCCESS)
        )
        assertEquals(expected, dense)
    }

    @Test
    fun checkLastDay() {
        val c = LocalDate.parse("2020-03-01")
        var l = listOf(Day(c, Behavior.NO_DATA ))
        l = l.addLastDayOfLastMonth().makeDense()
        assertEquals(31, l.size)
    }


    @Test
    fun checkLastDayFeb() {

        val c = LocalDate.parse("1999-02-01")
        var l = listOf(Day(c, Behavior.NO_DATA))
        l = l.addLastDayOfLastMonth().makeDense()
        assertEquals(28, l.size)
    }

    @Test
    fun checkLastDayLeap1() {
        val c = LocalDate.parse("2000-02-01")
        var l = listOf(Day(c, Behavior.NO_DATA))
        l = l.addLastDayOfLastMonth().makeDense()
        assertEquals(29, l.size)
    }

    @Test
    fun checkLastDayLeap2() {
        val c = LocalDate.parse("2020-02-01")
        var l = listOf(Day(c, Behavior.NO_DATA))
        l = l.addLastDayOfLastMonth().makeDense()
        assertEquals(29, l.size)
    }

}