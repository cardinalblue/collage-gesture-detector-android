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
import com.cardinalblue.gesture.PointerUtils

class SingleFingerPressingStateForDragOnly(owner: IGestureStateOwner,
                                           private val mTouchSlopSquare: Long)
    : BaseGestureState(owner) {

    private var mStartFocusId: Int = -1
    private var mStartFocusX: Float = 0f
    private var mStartFocusY: Float = 0f

    override fun onEnter(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        println("${StateConst.TAG} enter ${javaClass.simpleName}")

        // Initialize the ID from idle state to move state.
        mStartFocusId = event.getPointerId(event.actionIndex)

        // Hold the start focus x and y.
        val focusIndex = PointerUtils.getFocusIndexFromId(event, mStartFocusId)
        mStartFocusX = event.getX(focusIndex)
        mStartFocusY = event.getY(focusIndex)
    }

    override fun onDoing(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        val action = event.actionMasked

        // Determine focal point.
        val focusIndex = PointerUtils.getFocusIndexFromId(event, mStartFocusId)
        var focusX = event.getX(focusIndex)
        var focusY = event.getY(focusIndex)

        when (action) {

            MotionEvent.ACTION_POINTER_DOWN -> {
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val upIndex = event.actionIndex
                // Update focus point.
                if (focusIndex == upIndex) {
                    var newFocusIndex = -1
                    for (i in 0 until event.pointerCount) {
                        if (i == upIndex) continue
                        mStartFocusId = event.getPointerId(i)
                        newFocusIndex = i
                        break
                    }
                    if (newFocusIndex == -1) {
                        throw IllegalStateException("Cannot find other focus pointer")
                    }

                    focusX = event.getX(newFocusIndex)
                    focusY = event.getY(newFocusIndex)
                    mStartFocusX = focusX
                    mStartFocusY = focusY
                }
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

            MotionEvent.ACTION_UP,
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
        println("${StateConst.TAG} exit ${javaClass.simpleName}")
    }

    override fun onHandleMessage(msg: Message): Boolean {
        return false
    }
}
