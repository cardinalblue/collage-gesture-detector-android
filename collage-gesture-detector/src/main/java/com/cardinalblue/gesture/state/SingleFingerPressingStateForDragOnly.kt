// Copyright Feb 2017-present CardinalBlue
//
// Author: boy@cardinalblue.com
//         jack.huang@cardinalblue.com
//         yolung.lu@cardinalblue.com
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

package com.cardinalblue.gesture.state

import android.os.Message
import android.view.MotionEvent

import com.cardinalblue.gesture.IGestureStateOwner

import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_DRAG
import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_IDLE

class SingleFingerPressingStateForDragOnly(owner: IGestureStateOwner,
                                           private val mTouchSlopSquare: Long)
    : BaseGestureState(owner) {

    private var mHadLongPress: Boolean = false

    private var mTapCount: Int = 0

    private var mStartFocusX: Float = 0.toFloat()
    private var mStartFocusY: Float = 0.toFloat()

    private var mTouchingObject: Any? = null
    private var mTouchingContext: Any? = null

    override fun onEnter(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        mTapCount = 0
        mHadLongPress = false

        // Hold touching things.
        mTouchingObject = target
        mTouchingContext = context

        onDoing(event, target, context)
    }

    override fun onDoing(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        val action = event.actionMasked
        val pointerUp = action == MotionEvent.ACTION_POINTER_UP
        val upIndex = if (pointerUp) event.actionIndex else -1

        // Determine focal point.
        var sumX = 0f
        var sumY = 0f
        val count = event.pointerCount
        for (i in 0 until count) {
            if (upIndex == i) continue
            sumX += event.getX(i)
            sumY += event.getY(i)
        }
        val downPointerCount = if (pointerUp) count - 1 else count
        val focusX = sumX / downPointerCount
        val focusY = sumY / downPointerCount

        when (action) {

            MotionEvent.ACTION_POINTER_UP -> {
                // TODO: end -> start drag
                mStartFocusX = focusX
                mStartFocusY = focusY
            }

            MotionEvent.ACTION_DOWN -> {
                // Hold start x and y.
                mStartFocusX = focusX
                mStartFocusY = focusY
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaX = (focusX - mStartFocusX).toInt()
                val deltaY = (focusY - mStartFocusY).toInt()
                val distance = deltaX * deltaX + deltaY * deltaY

                if (distance > mTouchSlopSquare) {
                    // Transit to new state.
                    owner.issueStateTransition(
                        STATE_DRAG,
                        event, target, context)
                }
            }

            MotionEvent.ACTION_UP -> {
                // Transit to IDLE state.
                owner.issueStateTransition(
                    STATE_IDLE, event, target, context)
            }

            MotionEvent.ACTION_CANCEL -> {
                // Transit to IDLE state.
                owner.issueStateTransition(STATE_IDLE,
                                           event,
                                           target,
                                           context)
            }
        }
    }

    override fun onExit(event: MotionEvent,
                        target: Any?,
                        context: Any?) {
    }

    override fun onHandleMessage(msg: Message): Boolean {
        return false
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected / Private Methods ////////////////////////////////////////////
}
