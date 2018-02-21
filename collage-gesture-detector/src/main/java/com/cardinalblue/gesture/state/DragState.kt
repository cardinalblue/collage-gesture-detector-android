//  Copyright Oct 2017-present CardinalBlue
//
//  Author: boy@cardinalblue.com
//          jack.huang@cardinalblue.com
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package com.cardinalblue.gesture.state

import android.graphics.PointF
import android.os.Message
import android.view.MotionEvent
import android.view.VelocityTracker

import com.cardinalblue.gesture.IGestureStateOwner

import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_IDLE
import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_MULTIPLE_FINGERS_PRESSING

class DragState(owner: IGestureStateOwner,
                private val mMinFlingVelocity: Int,
                private val mMaxFlingVelocity: Int)
    : BaseGestureState(owner) {

    private var mIsMultitouchEnabled = true

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
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (!mIsMultitouchEnabled) {
                    // Don't issue transition.
                    return
                }

                // Transit to multiple-fingers-pressing state.
                owner.issueStateTransition(
                    STATE_MULTIPLE_FINGERS_PRESSING,
                    event, target, context)
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

        // Else transition to IDLE state.
            MotionEvent.ACTION_CANCEL -> {
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

    fun setIsTransitionToMultiTouchEnabled(enabled: Boolean) {
        mIsMultitouchEnabled = enabled
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
