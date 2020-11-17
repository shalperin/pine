package com.samhalperin.android.pine

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class DateMath {

    /*
    Note: I use a convention of forwarding private myfunc() to Test_myfunc() so that it can
    be tested.  This isn't great, but it works as long as it doesn't confuse maintainers.
     */

    @Test
    fun testDateNormalization() {
        //I don't even want to think about international travelers.
        val d1 = Date()
        val d1b = d1
        Thread.sleep(1000)
        val d2 = Date()
        assertEquals(d1, d1b)
        assertNotEquals(d1, d2)
        assertEquals(d1.normalize(), d2.normalize())
    }

    // one element list
    @Test
    fun testMakeDense1() {
        val startDate = Date().normalize()
        val sparse = listOf(
            Day(startDate, Behavior.FAILURE)
        )
        val dense = sparse.makeDense()
        assertEquals(sparse, dense)
    }

    // two adjacent dates
    @Test
    fun testMakeDense2() {
        val startDate = Date().normalize()
        val sparse = listOf(
            Day(startDate, Behavior.FAILURE),
            Day.Builder(startDate, Behavior.FAILURE).add(1).build()
        )
        val dense = sparse.makeDense()
        assertEquals(sparse, dense)
    }

    //2 dates, skipping a day
    @Test
    fun testMakeDense3() {
        val startDate = Date().normalize()
        val sparse = listOf(
            Day(startDate, Behavior.FAILURE),
            Day.Builder(startDate, Behavior.SUCCESS).add(2).build()
        )
        val dense = sparse.makeDense()
        val answer = listOf(
            Day(startDate, Behavior.FAILURE),
            Day.Builder(startDate, Behavior.NO_DATA).add(1).build(),
            Day.Builder(startDate, Behavior.SUCCESS).add(2).build()
        )
        assertEquals(dense, answer)
    }

    //more dates
    @Test
    fun testMakeDense4() {
        val startDate = Date().normalize()
        val sparse = listOf(
            Day(startDate, Behavior.FAILURE),
            Day.Builder(startDate, Behavior.SUCCESS).add(2).build(),
            Day.Builder(startDate, Behavior.SUCCESS).add(4).build()
        )
        val dense = sparse.makeDense()
        val expected = listOf(
            Day(startDate, Behavior.FAILURE),
            Day.Builder(startDate, Behavior.NO_DATA).add(1).build(),
            Day.Builder(startDate, Behavior.SUCCESS).add(2).build(),
            Day.Builder(startDate, Behavior.NO_DATA).add(3).build(),
            Day.Builder(startDate, Behavior.SUCCESS).add(4).build()
        )
        assertEquals(expected, dense)
    }

    @Test
    fun checkLastDay() {

        val c = GregorianCalendar(2020, Calendar.MARCH,1)
        var l = listOf(Day.onDate(c))
        l = l.addLastDayOfLastMonth().makeDense()
        assertEquals(31, l.size)
    }


    @Test
    fun checkLastDayFeb() {

        val c = GregorianCalendar(1999, Calendar.FEBRUARY,1)
        var l = listOf(Day.onDate(c))
        l = l.addLastDayOfLastMonth().makeDense()
        assertEquals(28, l.size)
    }

    @Test
    fun checkLastDayLeap1() {

        val c = GregorianCalendar(2000, Calendar.FEBRUARY,1)
        var l = listOf(Day.onDate(c))
        l = l.addLastDayOfLastMonth().makeDense()
        assertEquals(29, l.size)
    }

    @Test
    fun checkLastDayLeap2() {

        val c = GregorianCalendar(2020, Calendar.FEBRUARY,1)
        var l = listOf(Day.onDate(c))
        l = l.addLastDayOfLastMonth().makeDense()
        assertEquals(29, l.size)
    }

}