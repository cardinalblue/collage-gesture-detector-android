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

import android.os.Looper
import android.view.MotionEvent
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class GestureDetectorTest : BaseMockAndroidTest() {

    private lateinit var mockLifecycleListener: IGestureLifecycleListener
    private lateinit var mockTapListener: ITapGestureListener
    private lateinit var mockDragListener: IDragGestureListener
    private lateinit var mockPinchListener: IPinchGestureListener

    private lateinit var gestureDetector: GestureDetector

    @Before
    fun setup() {
        mockLifecycleListener = Mockito.mock(IGestureLifecycleListener::class.java)
        mockTapListener = Mockito.mock(ITapGestureListener::class.java)
        mockDragListener = Mockito.mock(IDragGestureListener::class.java)
        mockPinchListener = Mockito.mock(IPinchGestureListener::class.java)

        gestureDetector = GestureDetector(uiLooper = Looper.getMainLooper(),
                                          viewConfig = mockViewConfiguration,
                                          touchSlop = SCALED_TOUCH_SLOP.toFloat(),
                                          tapSlop = SCALED_TAP_SLOP.toFloat(),
                                          minFlingVec = SCALED_MIN_FLING_VELOCITY.toFloat(),
                                          maxFlingVec = SCALED_MAX_FLING_VELOCITY.toFloat())
    }

    @Test
    fun `lifecycle test`() {
        gestureDetector.addLifecycleListener(mockLifecycleListener)

        // obtain arguments: downTime: Long,
        //                   eventTime: Long,
        //                   action: Int,
        //                   x: Float,
        //                   y: Float,
        //                   metaState: Int
        gestureDetector.onTouchEvent(MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_DOWN, 0f, 0f, 0), null, null)
        gestureDetector.onTouchEvent(MotionEvent.obtain(0L, 1L, MotionEvent.ACTION_UP, 0f, 0f, 0), null, null)

        Mockito.verify(mockLifecycleListener, Mockito.times(1))
            .onTouchBegin(ShadowMotionEvent(maskedAction = MotionEvent.ACTION_DOWN,
                                            downFocusX = 0f,
                                            downFocusY = 0f,
                                            downXs = FloatArray(1) { 0f },
                                            downYs = FloatArray(1) { 0f }),
                          null, null)
        // FIXME: Handler doesn't really work
//        Mockito.verify(mockLifecycleListener, Mockito.times(1))
//            .onTouchEnd(ShadowMotionEvent(maskedAction = MotionEvent.ACTION_UP,
//                                          downFocusX = 0f,
//                                          downFocusY = 0f,
//                                          downXs = FloatArray(1) { 0f },
//                                          downYs = FloatArray(1) { 0f }),
//                        null, null)
    }
}
