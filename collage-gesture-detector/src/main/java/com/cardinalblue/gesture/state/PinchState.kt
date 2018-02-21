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
import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_SINGLE_FINGER_PRESSING
import java.util.*

class PinchState(owner: IGestureStateOwner) : BaseGestureState(owner) {

    /**
     * The pointers at the moment this state is entered.
     */
    private val mStartPointers = SparseArray<PointF>()
    /**
     * The pointers at the moment this state is doing.
     */
    private val mStopPointers = SparseArray<PointF>()
    /**
     * This is for holding the order of fingers getting down. Because the
     * [SparseArray] is unordered, we introduce auxiliary space to store
     * the order.
     */
    private val mOrderedPointerIds = ArrayList<Int>()

    override fun onEnter(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        val action = event.actionMasked
        val pointerUp = action == MotionEvent.ACTION_POINTER_UP
        val upIndex = if (pointerUp) event.actionIndex else -1

        // Hold the all down pointers.
        mStartPointers.clear()
        mStopPointers.clear()
        mOrderedPointerIds.clear()
        for (i in 0 until event.pointerCount) {
            if (i == upIndex) continue

            val id = event.getPointerId(i)

            mStartPointers.put(id, PointF(event.getX(i),
                                          event.getY(i)))
            mStopPointers.put(id, PointF(event.getX(i),
                                         event.getY(i)))

            mOrderedPointerIds.add(id)
        }

        // Dispatch pinch-begin.
        owner.listener?.onPinchBegin(
            obtainMyMotionEvent(event), target, context,
            arrayOf(mStartPointers.get(mOrderedPointerIds[0]),
                    mStartPointers.get(mOrderedPointerIds[1])))
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
                    // Update stop pointers.
                    val id1 = mOrderedPointerIds[0]
                    val pointer1 = mStopPointers.get(id1)
                    pointer1.set(event.getX(0), event.getY(0))

                    val id2 = mOrderedPointerIds[1]
                    val pointer2 = mStopPointers.get(id2)
                    pointer2.set(event.getX(1), event.getY(1))

                    // Dispatch callback.
                    owner.listener?.onPinch(
                        obtainMyMotionEvent(event), target, context,
                        arrayOf(mStartPointers.get(mOrderedPointerIds[0]),
                                mStartPointers.get(mOrderedPointerIds[1])),
                        arrayOf(mStopPointers.get(mOrderedPointerIds[0]),
                                mStopPointers.get(mOrderedPointerIds[1])))
                } else {
                    // Transit to STATE_SINGLE_FINGER_PRESSING state.
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
                // Hold new down pointer ID.
                mOrderedPointerIds.add(event.getPointerId(downIndex))
            }

            MotionEvent.ACTION_POINTER_UP -> {
                if (downPointerCount >= 2) {
                    val upId = event.getPointerId(upIndex)

                    if (mOrderedPointerIds.indexOf(upId) != -1 && mOrderedPointerIds.indexOf(upId) < 2) {
                        // The anchor pointers (first two) is changed, the gesture
                        // would end and restart.
                        owner.listener?.onPinchEnd(
                            obtainMyMotionEvent(event), target, context,
                            arrayOf(mStartPointers.get(mOrderedPointerIds[0]),
                                    mStartPointers.get(mOrderedPointerIds[1])),
                            arrayOf(mStopPointers.get(mOrderedPointerIds[0]),
                                    mStopPointers.get(mOrderedPointerIds[1])))

                        // Refresh the start pointers.
                        mStartPointers.clear()
                        mStopPointers.clear()
                        mOrderedPointerIds.clear()
                        for (i in 0 until event.pointerCount) {
                            if (i == upIndex) continue

                            mStartPointers.put(event.getPointerId(i),
                                               PointF(event.getX(i),
                                                      event.getY(i)))
                            mStopPointers.put(event.getPointerId(i),
                                              PointF(event.getX(i),
                                                     event.getY(i)))
                            mOrderedPointerIds.add(event.getPointerId(i))
                        }

                        // Restart the gesture.
                        owner.listener?.onPinchBegin(
                            obtainMyMotionEvent(event), target, context,
                            arrayOf(mStartPointers.get(mOrderedPointerIds[0]),
                                    mStartPointers.get(mOrderedPointerIds[1])))
                    } else {
                        // Remove the hold (up) pointer.
                        mStartPointers.remove(upId)
                        mStopPointers.remove(upId)
                        val index = mOrderedPointerIds.indexOf(upId)
                        if (index != -1) {
                            mOrderedPointerIds.removeAt(index)
                        }
                    }
                } else {
                    // Transit to STATE_SINGLE_FINGER_PRESSING state.
                    owner.issueStateTransition(
                        STATE_SINGLE_FINGER_PRESSING,
                        event, target, context)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
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
        // Dispatch pinch-end.
        owner.listener?.onPinchEnd(
            obtainMyMotionEvent(event), target, context,
            arrayOf(mStartPointers.get(mStartPointers.keyAt(0)),
                    mStartPointers.get(mStartPointers.keyAt(1))),
            arrayOf(mStopPointers.get(mStopPointers.keyAt(0)),
                    mStopPointers.get(mStopPointers.keyAt(1))))

        // Clear pointers.
        mStartPointers.clear()
        mStopPointers.clear()
        mOrderedPointerIds.clear()
    }

    override fun onHandleMessage(msg: Message): Boolean {
        return false
    }
}
