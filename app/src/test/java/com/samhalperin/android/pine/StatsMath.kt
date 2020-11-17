package com.samhalperin.android.pine

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class StatsMath {

    /*
    Note: I use a convention of forwarding private myfunc() to Test_myfunc() so that it can
    be tested.  This isn't great, but it works as long as it doesn't confuse maintainers.
     */
    @Test
    fun testPercentages() {
        assertEquals(0, percent(7, 0))
        assertEquals(86, percent(7, 6))
        assertEquals(100, percent(7, 7))
        assertEquals(15, percent(7, 1))
        assertEquals(0, percent(30, 0))
        assertEquals(97, percent(30, 29))
        assertEquals(100, percent(30, 30))
        assertEquals(4, percent(30, 1))
    }

    @Test
    fun testCountAndPercentageFromDayList() {
        val a = listOf(
            Day(Date(), Behavior.SUCCESS),
            Day(Date(), Behavior.FAILURE)
        )
        assertEquals(15, a.behaviorPercent(7, Behavior.SUCCESS))
        assertEquals(1, a.behaviorCount(7, Behavior.SUCCESS))
    }

    @Test
    fun testCountAndPercentageFromDayList2() {
        val a = listOf(
            Day(Date(), Behavior.SUCCESS),  //1
            Day(Date(), Behavior.SUCCESS),  //2
            Day(Date(), Behavior.FAILURE),
            Day(Date(), Behavior.FAILURE),  //...
            Day(Date(), Behavior.NO_DATA),
            Day(Date(), Behavior.FAILURE),
            Day(Date(), Behavior.FAILURE),  //7

        )
        assertEquals(29, a.behaviorPercent(7, Behavior.SUCCESS))
        assertEquals(2, a.behaviorCount(7, Behavior.SUCCESS))
    }

    @Test
    fun testCountAndPercentageFromDayList3() {
        val a = listOf<Day>()
        assertEquals(0, a.behaviorPercent(7, Behavior.SUCCESS))
        assertEquals(0, a.behaviorCount(7, Behavior.SUCCESS))
    }
}

//TODO: jenkins should run these tests on commit to GitHub (or locally?)