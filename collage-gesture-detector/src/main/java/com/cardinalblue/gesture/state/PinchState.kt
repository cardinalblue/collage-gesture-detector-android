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
    private val mStartPointers = SparseArray<Pair<Float, Float>>()
    /**
     * The pointers at the moment this state is doing.
     */
    private val mStopPointers = SparseArray<Pair<Float, Float>>()
    /**
     * This is for holding the order of fingers getting down. Because the
     * [SparseArray] is unordered, we introduce auxiliary space to store
     * the order.
     */
    private val mOrderedPointerIds = ArrayList<Int>()

    override fun onEnter(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        println("${StateConst.TAG} enter ${javaClass.simpleName}")

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

            mStartPointers.put(id, Pair(event.getX(i),
                                        event.getY(i)))
            mStopPointers.put(id, Pair(event.getX(i),
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
                    val idPointer1 = mOrderedPointerIds[0]
                    mStopPointers.setValueAt(0,
                            Pair(event.getXbyId(idPointer1), event.getYbyId(idPointer1)))

                    val idPointer2 = mOrderedPointerIds[1]
                    mStopPointers.setValueAt(1,
                            Pair(event.getXbyId(idPointer2), event.getYbyId(idPointer2)))

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
                mStartPointers.put(downId, Pair(event.getX(downIndex),
                                                  event.getY(downIndex)))
                mStopPointers.put(downId, Pair(event.getX(downIndex),
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
                                               Pair(event.getX(i),
                                                      event.getY(i)))
                            mStopPointers.put(event.getPointerId(i),
                                              Pair(event.getX(i),
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
        println("${StateConst.TAG} exit ${javaClass.simpleName}")

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

fun MotionEvent.getXbyId(pointerId: Int): Float {
    val pointerIndex = this.findPointerIndex(pointerId)

    if (pointerIndex == -1) throw IllegalArgumentException()

    return this.getX(pointerIndex)
}

fun MotionEvent.getYbyId(pointerId: Int): Float {
    val pointerIndex = this.findPointerIndex(pointerId)

    if (pointerIndex == -1) throw IllegalArgumentException()

    return this.getY(pointerIndex)
}