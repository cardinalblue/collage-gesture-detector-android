// Copyright May 2018-present CardinalBlue
//
// Author: boy@cardinalblue.com
//         prada@cardinalblue.com
//         raymond.wu@cardinalblue.com
//         shih.yao@cardinalblue.com
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

package com.cardinalblue.gesture.rx

import android.graphics.PointF
import com.cardinalblue.gesture.*
import java.util.*

sealed class GestureEvent

// Lifecycle //////////////////////////////////////////////////////////////////

/**
 * A event formation for the callback, [IGestureLifecycleListener.onActionBegin],
 * which represents the beginning of a touch.
 */
data class TouchBeginEvent(val rawEvent: MyMotionEvent,
                           val target: Any?,
                           val context: Any?) : GestureEvent()

/**
 * A event formation for the callback, [IGestureLifecycleListener.onActionEnd],
 * which represents the end of a touch.
 */
data class TouchEndEvent(val rawEvent: MyMotionEvent,
                         val target: Any?,
                         val context: Any?) : GestureEvent()

// Tap ////////////////////////////////////////////////////////////////////////

/**
 * A event formation for these callbacks, [ITapGestureListener.onSingleTap],
 * [ITapGestureListener.onDoubleTap], [ITapGestureListener.onMoreTap],
 * which represent the family of tap.
 */
data class TapEvent(val rawEvent: MyMotionEvent,
                    val target: Any?,
                    val context: Any?,
                    val downX: Float,
                    val downY: Float,
                    val taps: Int) : GestureEvent()

/**
 * A event formation for these callbacks, [ITapGestureListener.onLongTap], which
 * represents a long-tap.
 */
data class LongTapEvent(val rawEvent: MyMotionEvent,
                        val target: Any?,
                        val context: Any?,
                        val downX: Float,
                        val downY: Float) : GestureEvent()

/**
 * A event formation for the callback, [ITapGestureListener.onLongPress], which
 * represents a long-press.
 */
data class LongPressEvent(val rawEvent: MyMotionEvent,
                          val target: Any?,
                          val context: Any?,
                          val downX: Float,
                          val downY: Float) : GestureEvent()

// Drag ///////////////////////////////////////////////////////////////////////

/**
 * A event formation for the callback, [IDragGestureListener.onDragBegin],
 * which represents the beginning of a drag.
 */
data class DragBeginEvent(val rawEvent: MyMotionEvent,
                          val target: Any?,
                          val context: Any?,
                          val startPointer: PointF) : GestureEvent()

/**
 * A event formation for the callback, [IDragGestureListener.onDrag], which
 * represents a on-going drag.
 */
data class OnDragEvent(val rawEvent: MyMotionEvent,
                       val target: Any?,
                       val context: Any?,
                       val startPointer: PointF,
                       val stopPointer: PointF) : GestureEvent()

/**
 * A event formation for the callback, [IDragGestureListener.onDragFling], which
 * represents a drag-fling.
 */
data class DragFlingEvent(val rawEvent: MyMotionEvent,
                          val target: Any?,
                          val context: Any?,
                          val startPointer: PointF,
                          val stopPointer: PointF,
                          val velocityX: Float,
                          val velocityY: Float) : GestureEvent()

/**
 * A event formation for the callback, [IDragGestureListener.onDragEnd], which
 * represents the end of a drag.
 */
data class DragEndEvent(val rawEvent: MyMotionEvent,
                        val target: Any?,
                        val context: Any?,
                        val startPointer: PointF,
                        val stopPointer: PointF) : GestureEvent()

// Pinch //////////////////////////////////////////////////////////////////////

/**
 * A event formation for the callback, [IPinchGestureListener.onPinchBegin],
 * which represents the beginning of a pinch (pan).
 */
data class PinchBeginEvent(val rawEvent: MyMotionEvent,
                           val target: Any?,
                           val context: Any?,
                           val startPointers: Array<PointF>) : GestureEvent() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PinchBeginEvent

        if (rawEvent != other.rawEvent) return false
        if (target != other.target) return false
        if (context != other.context) return false
        if (!Arrays.equals(startPointers, other.startPointers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rawEvent.hashCode()
        result = 31 * result + (target?.hashCode() ?: 0)
        result = 31 * result + (context?.hashCode() ?: 0)
        result = 31 * result + Arrays.hashCode(startPointers)
        return result
    }
}

/**
 * A event formation for the callback, [IPinchGestureListener.onPinch], which
 * represents a on-going pinch (pan).
 */
data class OnPinchEvent(val rawEvent: MyMotionEvent,
                        val target: Any?,
                        val context: Any?,
                        val startPointers: Array<PointF>,
                        val stopPointers: Array<PointF>) : GestureEvent() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OnPinchEvent

        if (rawEvent != other.rawEvent) return false
        if (target != other.target) return false
        if (context != other.context) return false
        if (!Arrays.equals(startPointers, other.startPointers)) return false
        if (!Arrays.equals(stopPointers, other.stopPointers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rawEvent.hashCode()
        result = 31 * result + (target?.hashCode() ?: 0)
        result = 31 * result + (context?.hashCode() ?: 0)
        result = 31 * result + Arrays.hashCode(startPointers)
        result = 31 * result + Arrays.hashCode(stopPointers)
        return result
    }
}

/**
 * A event formation for the callback, [IPinchGestureListener.onPinchFling], which
 * represents a pinch (pan) fling (or flick).
 */
data class PinchFlingEvent(val rawEvent: MyMotionEvent,
                           val target: Any?,
                           val context: Any?) : GestureEvent()

/**
 * A event formation for the callback, [IPinchGestureListener.onPinchEnd], which
 * represents the end of a pinch (pan).
 */
data class PinchEndEvent(val rawEvent: MyMotionEvent,
                         val target: Any?,
                         val context: Any?,
                         val startPointers: Array<PointF>,
                         val stopPointers: Array<PointF>) : GestureEvent() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PinchEndEvent

        if (rawEvent != other.rawEvent) return false
        if (target != other.target) return false
        if (context != other.context) return false
        if (!Arrays.equals(startPointers, other.startPointers)) return false
        if (!Arrays.equals(stopPointers, other.stopPointers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rawEvent.hashCode()
        result = 31 * result + (target?.hashCode() ?: 0)
        result = 31 * result + (context?.hashCode() ?: 0)
        result = 31 * result + Arrays.hashCode(startPointers)
        result = 31 * result + Arrays.hashCode(stopPointers)
        return result
    }
}
