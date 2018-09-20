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

import com.cardinalblue.gesture.*
import java.util.*

sealed class GestureEvent(open val rawEvent: ShadowMotionEvent)

abstract class GestureLifecycleEvent(rawEvent: ShadowMotionEvent)
    : GestureEvent(rawEvent = rawEvent)
/**
 * The parent class for all the tap and drag events.
 */
abstract class SingleFingerEvent(rawEvent: ShadowMotionEvent)
    : GestureEvent(rawEvent = rawEvent)
/**
 * The parent class for all the pinch events.
 */
abstract class TwoFingersEvent(rawEvent: ShadowMotionEvent)
    : GestureEvent(rawEvent = rawEvent)
/**
 * The parent class for all the unknown events.
 */
abstract class MultipleFingersEvent(rawEvent: ShadowMotionEvent)
    : GestureEvent(rawEvent = rawEvent)

// Lifecycle //////////////////////////////////////////////////////////////////

/**
 * A event formation for the callback, [IGestureLifecycleListener.onTouchBegin],
 * which represents the beginning of a touch.
 */
data class TouchBeginEvent(override val rawEvent: ShadowMotionEvent,
                           val target: Any?,
                           val context: Any?)
    : GestureLifecycleEvent(rawEvent = rawEvent)

/**
 * A event formation for the callback, [IGestureLifecycleListener.onTouchEnd],
 * which represents the end of a touch.
 */
data class TouchEndEvent(override val rawEvent: ShadowMotionEvent,
                         val target: Any?,
                         val context: Any?)
    : GestureLifecycleEvent(rawEvent = rawEvent)

// Tap ////////////////////////////////////////////////////////////////////////

/**
 * A event formation for these callbacks, [ITapGestureListener.onSingleTap],
 * [ITapGestureListener.onDoubleTap], [ITapGestureListener.onMoreTap],
 * which represent the family of tap.
 */
data class TapEvent(override val rawEvent: ShadowMotionEvent,
                    val target: Any?,
                    val context: Any?,
                    val downX: Float,
                    val downY: Float,
                    val taps: Int)
    : SingleFingerEvent(rawEvent = rawEvent)

/**
 * A event formation for these callbacks, [ITapGestureListener.onLongTap], which
 * represents a long-tap.
 */
data class LongTapEvent(override val rawEvent: ShadowMotionEvent,
                        val target: Any?,
                        val context: Any?,
                        val downX: Float,
                        val downY: Float)
    : SingleFingerEvent(rawEvent = rawEvent)

/**
 * A event formation for the callback, [ITapGestureListener.onLongPress], which
 * represents a long-press.
 */
data class LongPressEvent(override val rawEvent: ShadowMotionEvent,
                          val target: Any?,
                          val context: Any?,
                          val downX: Float,
                          val downY: Float)
    : SingleFingerEvent(rawEvent = rawEvent)

// Drag ///////////////////////////////////////////////////////////////////////

/**
 * A event formation for the callback, [IDragGestureListener.onDragBegin],
 * which represents the beginning of a drag.
 */
data class DragBeginEvent(override val rawEvent: ShadowMotionEvent,
                          val target: Any?,
                          val context: Any?,
                          val startPointer: Pair<Float, Float>)
    : SingleFingerEvent(rawEvent = rawEvent)

/**
 * A event formation for the callback, [IDragGestureListener.onDrag], which
 * represents a on-going drag.
 */
data class DragDoingEvent(override val rawEvent: ShadowMotionEvent,
                          val target: Any?,
                          val context: Any?,
                          val startPointer: Pair<Float, Float>,
                          val stopPointer: Pair<Float, Float>)
    : SingleFingerEvent(rawEvent = rawEvent)

/**
 * A event formation for the callback, [IDragGestureListener.onDragFling], which
 * represents a drag-fling.
 */
data class DragFlingEvent(override val rawEvent: ShadowMotionEvent,
                          val target: Any?,
                          val context: Any?,
                          val startPointer: Pair<Float, Float>,
                          val stopPointer: Pair<Float, Float>,
                          val velocityX: Float,
                          val velocityY: Float)
    : SingleFingerEvent(rawEvent = rawEvent)

/**
 * A event formation for the callback, [IDragGestureListener.onDragEnd], which
 * represents the end of a drag.
 */
data class DragEndEvent(override val rawEvent: ShadowMotionEvent,
                        val target: Any?,
                        val context: Any?,
                        val startPointer: Pair<Float, Float>,
                        val stopPointer: Pair<Float, Float>)
    : SingleFingerEvent(rawEvent = rawEvent)

// Pinch //////////////////////////////////////////////////////////////////////

/**
 * A event formation for the callback, [IPinchGestureListener.onPinchBegin],
 * which represents the beginning of a pinch (pan).
 */
data class PinchBeginEvent(override val rawEvent: ShadowMotionEvent,
                           val target: Any?,
                           val context: Any?,
                           val startPointers: Array<Pair<Float, Float>>)
    : TwoFingersEvent(rawEvent = rawEvent) {

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
data class PinchDoingEvent(override val rawEvent: ShadowMotionEvent,
                           val target: Any?,
                           val context: Any?,
                           val startPointers: Array<Pair<Float, Float>>,
                           val stopPointers: Array<Pair<Float, Float>>)
    : TwoFingersEvent(rawEvent = rawEvent) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PinchDoingEvent

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
data class PinchFlingEvent(override val rawEvent: ShadowMotionEvent,
                           val target: Any?,
                           val context: Any?)
    : GestureEvent(rawEvent = rawEvent)

/**
 * A event formation for the callback, [IPinchGestureListener.onPinchEnd], which
 * represents the end of a pinch (pan).
 */
data class PinchEndEvent(override val rawEvent: ShadowMotionEvent,
                         val target: Any?,
                         val context: Any?,
                         val startPointers: Array<Pair<Float, Float>>,
                         val stopPointers: Array<Pair<Float, Float>>)
    : TwoFingersEvent(rawEvent = rawEvent) {

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
