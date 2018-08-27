// Copyright Aug 2018-present CardinalBlue
//
// Author: boyw165@gmail.com
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the "Software"),
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
// THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.

package com.cardinalblue.gesture

import android.os.Handler
import android.view.ViewConfiguration
import org.junit.Before
import org.mockito.Mockito

internal const val SCALED_TOUCH_SLOP = 15
internal const val SCALED_TAP_SLOP = 15
internal const val SCALED_DOUBLE_TAP_SLOP = 63
internal const val SCALED_MIN_FLING_VELOCITY = 1050
internal const val SCALED_MAX_FLING_VELOCITY = 21000

abstract class BaseMockAndroidTest {

    lateinit var mockViewConfiguration: ViewConfiguration

    @Before
    fun `setup android mock`() {
        mockViewConfiguration = Mockito.mock(ViewConfiguration::class.java)
        Mockito.`when`(mockViewConfiguration.scaledTouchSlop).thenReturn(SCALED_TOUCH_SLOP)
        Mockito.`when`(mockViewConfiguration.scaledDoubleTapSlop).thenReturn(SCALED_DOUBLE_TAP_SLOP)
        Mockito.`when`(mockViewConfiguration.scaledMinimumFlingVelocity).thenReturn(SCALED_MIN_FLING_VELOCITY)
        Mockito.`when`(mockViewConfiguration.scaledMaximumFlingVelocity).thenReturn(SCALED_MAX_FLING_VELOCITY)
    }
}
