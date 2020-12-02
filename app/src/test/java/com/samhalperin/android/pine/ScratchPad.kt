package com.samhalperin.android.pine

import com.samhalperin.android.pine.entities.Behavior
import org.junit.Test
import org.junit.Assert.*

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