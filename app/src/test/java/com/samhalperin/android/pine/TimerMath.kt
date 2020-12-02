package com.samhalperin.android.pine

import com.samhalperin.android.pine.entities.Timer
import com.samhalperin.android.pine.entities.timeLeft
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class TimerMath {
    val timer = Timer(LocalDateTime.of(2020, 1, 2, 0, 0, 0))

    @Test
    fun testTimeLeftToString1() {
        var now = LocalDateTime.of(2020, 1, 1, 0, 0, 0)
        assertEquals("24h 00m", timer.timeLeft(now))
    }

    @Test
    fun testTimeLeftToString2() {
        var now = LocalDateTime.of(2020, 1, 1, 12, 0, 0)
        assertEquals("12h 00m", timer.timeLeft(now))
    }

    @Test
    fun howDowsLocalDateTimeSerialize() {
        var t = LocalDateTime.of(2020, 1, 1, 12, 0, 0)
        val srep = t.toString()
        assertEquals("2020-01-01T12:00", srep)
    }

}