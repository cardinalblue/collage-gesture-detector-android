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
import android.util.SparseArray
import android.view.MotionEvent

import com.cardinalblue.gesture.IGestureStateOwner

import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_IDLE
import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_PINCH
import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_SINGLE_FINGER_PRESSING

class MultipleFingersPressingState(owner: IGestureStateOwner,
                                   private val mTouchSlopSquare: Int)
    : BaseGestureState(owner) {

    // TODO: Time for insertion is O(log(n)), searching is O(1)
    // Pointers.
    private val mStartPointers = SparseArray<PointF>()
    private val mStopPointers = SparseArray<PointF>()

    override fun onEnter(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        val action = event.actionMasked
        val pointerUp = action == MotionEvent.ACTION_POINTER_UP
        val upIndex = if (pointerUp) event.actionIndex else -1

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            // Transit to IDLE state.
            owner.issueStateTransition(
                STATE_IDLE,
                event, target, context)
        } else {
            val isUp = action == MotionEvent.ACTION_POINTER_UP
            val pressCount = event.pointerCount - if (isUp) 1 else 0
            val isMultipleFingers = pressCount > 1

            if (isMultipleFingers) {
                // Hold the all down pointers.
                mStartPointers.clear()
                mStopPointers.clear()
                for (i in 0 until event.pointerCount) {
                    if (i == upIndex) continue

                    val id = event.getPointerId(i)

                    mStartPointers.put(id, PointF(event.getX(i),
                                                  event.getY(i)))
                    mStopPointers.put(id, PointF(event.getX(i),
                                                 event.getY(i)))
                }
            } else {
                // Transit to single-finger-pressing state.
                owner.issueStateTransition(
                    STATE_SINGLE_FINGER_PRESSING,
                    event, target, context)
            }
        }
    }

    override fun onDoing(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        val action = event.actionMasked
        val pointerUp = action == MotionEvent.ACTION_POINTER_UP
        val upIndex = if (pointerUp) event.actionIndex else -1
        val downPointerCount = event.pointerCount - if (pointerUp) 1 else 0

        when (action) {
            MotionEvent.ACTION_MOVE -> {
                if (downPointerCount >= 2) {
                    // Update the stop pointers.
                    for (i in 0 until event.pointerCount) {
                        if (i == upIndex) continue

                        val id = event.getPointerId(i)
                        val pointer = mStopPointers.get(id)

                        pointer?.set(event.getX(i), event.getY(i))
                    }

                    // Transit to PINCH state.
                    if (isConsideredPinch(mStartPointers, mStopPointers)) {
                        owner.issueStateTransition(
                            STATE_PINCH, event, target, context)
                    }
                } else {
                    // Transit to single-finger-pressing state.
                    owner.issueStateTransition(
                        STATE_SINGLE_FINGER_PRESSING,
                        event, target, context)
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                // Hold undocumented pointers.
                val downIndex = event.actionIndex
                val downId = event.getPointerId(downIndex)
                mStartPointers.put(downId, PointF(event.getX(downIndex),
                                                  event.getY(downIndex)))
                mStopPointers.put(downId, PointF(event.getX(downIndex),
                                                 event.getY(downIndex)))
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val upId = event.getPointerId(upIndex)
                mStartPointers.remove(upId)
                mStopPointers.remove(upId)

                val isMultipleFingers = downPointerCount > 1
                if (!isMultipleFingers) {
                    // Transit to single-finger-pressing state.
                    owner.issueStateTransition(
                        STATE_SINGLE_FINGER_PRESSING,
                        event, target, context)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Transit to IDLE state.
                owner.issueStateTransition(
                    STATE_IDLE,
                    event, target, context)
            }
        }
    }

    private fun isConsideredPinch(startPointers: SparseArray<PointF>,
                                  stopPointers: SparseArray<PointF>): Boolean {
        val size = Math.min(startPointers.size(), stopPointers.size())
        for (i in 0 until size) {
            val start = startPointers.get(startPointers.keyAt(i))
            val stop = stopPointers.get(stopPointers.keyAt(i))
            val dx = stop.x - start.x
            val dy = stop.y - start.y

            if (dx * dx + dy * dy > mTouchSlopSquare) {
                return true
            }
        }

        return false
    }

    override fun onExit(event: MotionEvent,
                        target: Any?,
                        context: Any?) {
        mStartPointers.clear()
        mStopPointers.clear()
    }

    override fun onHandleMessage(msg: Message): Boolean {
        return false
    }
}
