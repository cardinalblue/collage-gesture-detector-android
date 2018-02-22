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
import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_DRAG
import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_IDLE
import com.cardinalblue.gesture.PointerUtils

class DragStateForDragOnly(owner: IGestureStateOwner,
                           private val mMinFlingVelocity: Int,
                           private val mMaxFlingVelocity: Int)
    : BaseGestureState(owner) {

    private var mVelocityTracker: VelocityTracker? = null
    private var mStartFocusId: Int = -1
    private var mStartFocusX: Float = 0f
    private var mStartFocusY: Float = 0f

    override fun onEnter(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        val action = event.actionMasked

        // Find the focus pointer ID.
        when (action) {
            MotionEvent.ACTION_POINTER_UP -> {
                // Need to find ID of the pointer other than the up one.
                val upIndex = event.actionIndex
                for (i in 0 until event.pointerCount) {
                    if (i == upIndex) continue

                    mStartFocusId = event.getPointerId(i)
                    break
                }
            }
            else -> {
                // Initialize the ID from idle state to move state.
                mStartFocusId = event.getPointerId(event.actionIndex)
            }
        }

        // Hold the start focus x and y.
        val focusIndex = PointerUtils.getFocusIndexFromId(event, mStartFocusId)
        mStartFocusX = event.getX(focusIndex)
        mStartFocusY = event.getY(focusIndex)
//        Log.d("xyz", "enter: focus index=%d, pointer count=%d".format(focusIndex, event.pointerCount))

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
        val focusIndex = PointerUtils.getFocusIndexFromId(event, mStartFocusId)
        val focusX = event.getX(focusIndex)
        val focusY = event.getY(focusIndex)

        when (action) {

            MotionEvent.ACTION_POINTER_UP -> {
//                Log.d("xyz", "ACTION_POINTER_UP: x=%.3f, y=%.3f".format(focusX, focusY))
                // Kill and respawn this state: exit -> enter
                val upIndex = event.actionIndex
                if (upIndex == focusIndex) {
                    owner.issueStateTransition(STATE_DRAG,
                                               event,
                                               target,
                                               context)
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
//                Log.d("xyz", "ACTION_POINTER_DOWN: x=%.3f, y=%.3f".format(focusX, focusY))
            }

            MotionEvent.ACTION_MOVE -> {
//                Log.d("xyz", "ACTION_MOVE, count=%d, x=%.3f, y=%.3f".format(event.pointerCount, focusX, focusY))
                owner.listener?.onDrag(
                    obtainMyMotionEvent(event), target, context,
                    PointF(mStartFocusX, mStartFocusY),
                    PointF(focusX, focusY))
            }

            MotionEvent.ACTION_UP -> {
//                Log.d("xyz", "ACTION_UP")
                // Transit to IDLE state.
                owner.issueStateTransition(STATE_IDLE,
                                           event,
                                           target,
                                           context)
            }

            MotionEvent.ACTION_CANCEL -> {
//                Log.d("xyz", "ACTION_CANCEL")
                // Else transition to IDLE state.
                owner.issueStateTransition(STATE_IDLE, event, target, context)
            }
        }
    }

    override fun onExit(event: MotionEvent,
                        target: Any?,
                        context: Any?) {
        val clone = obtainMyMotionEvent(event)
        val focusIndex = PointerUtils.getFocusIndexFromId(event, mStartFocusId)
        val focusX = event.getX(focusIndex)
        val focusY = event.getY(focusIndex)

//        Log.d("xyz", "exit: focus index=%d, pointer count=%d".format(focusIndex, event.pointerCount))

        if (isConsideredFling(event)) {
            // TODO: Complete fling arguments.
            owner.listener?.onDragFling(clone, target, context,
                                        PointF(mStartFocusX, mStartFocusY),
                                        PointF(focusX, focusY),
                                        0f, 0f)
        }

        // Callback.
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
