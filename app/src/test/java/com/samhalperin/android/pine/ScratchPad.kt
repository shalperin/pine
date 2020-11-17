package com.samhalperin.android.pine

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class ScratchPad {

    /*
    Note: I use a convention of forwarding private myfunc() to Test_myfunc() so that it can
    be tested.  This isn't great, but it works as long as it doesn't confuse maintainers.
     */
    @Test
    fun testPercentages() {
        assertEquals("FAILURE", Behavior.FAILURE.toString())
    }
}