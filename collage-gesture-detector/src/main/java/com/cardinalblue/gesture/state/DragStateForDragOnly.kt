// Copyright Feb 2017-present CardinalBlue
//
// Author: boy@cardinalblue.com
//         yolung.lu@cardinalblue.com
//         jack.huang@cardinalblue.com
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

import android.graphics.PointF
import android.os.Message
import android.view.MotionEvent
import android.view.VelocityTracker
import com.cardinalblue.gesture.IGestureStateOwner
import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_IDLE

class DragStateForDragOnly(owner: IGestureStateOwner,
                           private val mMinFlingVelocity: Int,
                           private val mMaxFlingVelocity: Int)
    : BaseGestureState(owner) {

    private var mVelocityTracker: VelocityTracker? = null
    private var mStartFocusX: Float = 0.toFloat()
    private var mStartFocusY: Float = 0.toFloat()

    override fun onEnter(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        mStartFocusX = event.x
        mStartFocusY = event.y

        owner.listener?.onDragBegin(
            obtainMyMotionEvent(event),
            target, context)
    }

    override fun onDoing(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)

        val action = event.actionMasked
        val focusX = event.x
        val focusY = event.y

        when (action) {

            MotionEvent.ACTION_POINTER_UP -> {
                // TODO: Respwan state: end -> start
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                // TODO: Respwan state: end -> start
            }

            MotionEvent.ACTION_MOVE -> {
                owner.listener?.onDrag(
                    obtainMyMotionEvent(event), target, context,
                    PointF(mStartFocusX, mStartFocusY),
                    PointF(focusX, focusY))
            }

            MotionEvent.ACTION_UP -> {
                if (event.pointerCount - 1 > 0) {
                    // Still finger touching.
                    return
                }
                // Transit to IDLE state.
                owner.issueStateTransition(STATE_IDLE,
                                           event,
                                           target,
                                           context)
            }

            MotionEvent.ACTION_CANCEL -> {
                // Else transition to IDLE state.
                owner.issueStateTransition(STATE_IDLE, event, target, context)
            }
        }
    }

    override fun onExit(event: MotionEvent,
                        target: Any?,
                        context: Any?) {
        val clone = obtainMyMotionEvent(event)
        val focusX = event.x
        val focusY = event.y

        if (isConsideredFling(event)) {
            // TODO: Complete fling arguments.
            owner.listener?.onDragFling(clone, target, context,
                                        PointF(), PointF(), 0f, 0f)
        }

        // TODO: Complete the translation argument.
        owner.listener?.onDragEnd(
            clone, target, context,
            PointF(mStartFocusX, mStartFocusY),
            PointF(focusX, focusY))

        if (mVelocityTracker != null) {
            // This may have been cleared when we called out to the
            // application above.
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    override fun onHandleMessage(msg: Message): Boolean {
        return false
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected / Private Methods ////////////////////////////////////////////

    private fun isConsideredFling(event: MotionEvent): Boolean {
        val action = event.actionMasked

        return if (action == MotionEvent.ACTION_UP) {
            // A fling must travel the minimum tap distance
            val pointerId = event.getPointerId(0)
            mVelocityTracker!!.computeCurrentVelocity(1000, mMaxFlingVelocity.toFloat())

            val velocityY = mVelocityTracker!!.getYVelocity(pointerId)
            val velocityX = mVelocityTracker!!.getXVelocity(pointerId)

            Math.abs(velocityY) > mMinFlingVelocity || Math.abs(velocityX) > mMinFlingVelocity
        } else {
            false
        }
    }
}
